package com.lg.dbsync;

import com.alibaba.fastjson.JSON;
import com.lg.db.Expression;
import com.lg.db.Filter;
import com.lg.db.Update;
import com.lg.db.UpdateOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TableSync {

    private SyncPolicy policy;
    private Supplier<List<?>> store;
    private Consumer updater;

    private List<Model> models = Collections.EMPTY_LIST;
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
    private ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
    private ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private Class delegateType;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public TableSync(Supplier query, Consumer update) {
        this(query, update, new SyncPolicy.Build().maxDirty(10).maxTimeout(10).build());
    }

    public TableSync(Supplier store, Consumer update, SyncPolicy policy) {
        this.policy = policy;
        this.store = store;
        this.updater = update;
    }

    void flush() {
        readLock.lock();
        models.stream().filter(policy::updateCheck).forEach(Model::flush);
        readLock.unlock();
    }

    /**
     * 开始托管
     */
    public void delegate() {
        writeLock.lock();
        try {
            this.models = this.store.get().stream().map(record -> new Model(record, this.updater)).collect(Collectors.toList());
            if (!this.models.isEmpty()) {
                delegateType = this.models.get(0).getRecord().getClass();
            }else {
                delegateType= null;
            }
            TableSyncManage.join(this);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 托管表中增加一条记录
     *
     * @param record
     */
    public void insert(Object record) {
        writeLock.lock();
        try {
            if(delegateType==null){
                delegateType=record.getClass();
            }
            this.models.add(new Model(record, this.updater));
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 重启
     *
     * @param runnable
     */
    public void reload(Runnable runnable) {
        writeLock.lock();
        try {
            stop();
            if (runnable != null) {
                try {
                    runnable.run();
                } catch (Exception err) {
                    logger.error("重启同步表时指定的外部任务失败", err);
                }
            }
            delegate();
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 结束任务
     */
    public void stop() {
        TableSyncManage.leave(this);
    }


    public String summary() {
        StringBuilder sb = new StringBuilder();
        readLock.lock();
        try {
            sb.append("<div>" + delegateType + "</div>");
            sb.append("<div>").append("总数:").append(models.size()).append(",未同步:").append(models.stream().filter(model -> model.getDirtySignature().getDirtyCount() > 0).count()).append("</div>");
        } finally {
            readLock.unlock();
        }
        return sb.toString();
    }

    /**
     * 查看状态
     *
     * @return
     */
    public String status(String field, String val) {
        StringBuilder sb = new StringBuilder();
        readLock.lock();
        try {
            if (models.isEmpty()) {
                return sb.toString();
            }

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            sb.append("<div>" + delegateType + "</div>");
            sb.append("<table border=\"1\">");
            sb.append("<tr><th>待刷新</th><th>版本</th><th>同步时间</th><th>内容</th></tr>");

            if (field != null && !field.isEmpty() && val != null && !val.isEmpty()) {
                models.stream().filter(model -> val.equals(model.getUnsafeVal(field))).forEach(model -> {
                    Model.DirtySignature signature = model.getDirtySignature();
                    sb.append("<tr>");
                    sb.append("<td>").append(signature.getDirtyCount()).append("</td>");
                    sb.append("<td>").append(signature.getVer()).append("</td>");
                    sb.append("<td>").append(sdf.format(new Date(signature.getUpdateAt()))).append("</td>");
                    sb.append("<td>").append(JSON.toJSONString(model.getRecord())).append("</td>");
                    sb.append("</tr>");
                });
            } else {
                models.forEach(model -> {
                    Model.DirtySignature signature = model.getDirtySignature();
                    sb.append("<tr>");
                    sb.append("<td>").append(signature.getDirtyCount()).append("</td>");
                    sb.append("<td>").append(signature.getVer()).append("</td>");
                    sb.append("<td>").append(sdf.format(new Date(signature.getUpdateAt()))).append("</td>");
                    sb.append("<td>").append(JSON.toJSONString(model.getRecord())).append("</td>");
                    sb.append("</tr>");
                });
            }
            sb.append("</table>");
        } finally {
            readLock.unlock();
        }

        return sb.toString();
    }

    /**
     * 修改(只实现了相等条件判断)
     *
     * @param update
     * @param filter
     * @return 更新行数
     */
    public long update(Update update, Filter filter) {
        readLock.lock();
        try {
            return models.stream().filter(model -> {
                for (Expression expression : filter.getExpressions()) {
                    Expression.Compare compare = expression.getCompare();
                    String name = expression.getName();
                    Object value = expression.getValue();

                    if (compare == Expression.Compare.EQ) {
                        Object fieldValue = model.getUnsafeVal(name);
                        if (fieldValue == null || !fieldValue.equals(value)) {
                            return false;
                        }
                    }
                }

                return true;
            }).map(model -> {
                for (UpdateOption option : update.getOptions()) {
                    UpdateOption.Action action = option.getAction();
                    if (action == UpdateOption.Action.SET) {
                        model.set(option.getName(), option.getValue());
                    } else if (action == UpdateOption.Action.INC) {
                        model.inc(option.getName(), option.getValue());
                    }
                }
                return 0;
            }).count();
        } finally {
            readLock.unlock();
        }
    }

    public List<Object> query(Filter filter) {
        readLock.lock();
        try {
            List list = new ArrayList();
            models.stream().filter(model -> {
                for (Expression expression : filter.getExpressions()) {
                    Expression.Compare compare = expression.getCompare();
                    String name = expression.getName();
                    Object value = expression.getValue();
                    if (compare == Expression.Compare.EQ) {
                        Object fieldValue = model.getUnsafeVal(name);
                        if (fieldValue== null || !fieldValue.equals(value)) {
                            return false;
                        }
                    }
                }
                return true;
            }).forEach(model -> {
                list.add(model.getRecord());
            });
            return list;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * 获取委托类型
     *
     * @return
     */
    public Class getDelegateType() {
        readLock.lock();
        try {
            return delegateType;
        } finally {
            readLock.unlock();
        }
    }

}
