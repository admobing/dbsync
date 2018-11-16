package com.lg.dbsync;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class TableSyncManage {

    private List<TableSync> tasks = new CopyOnWriteArrayList<>();

    private TableSyncManage() {
        Flowable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.single())
                .observeOn(Schedulers.io())
                .subscribe(time -> {
                    tasks.forEach(task -> {
                        task.flush();
                    });
                });
    }

    private static class LazyHolder {
        public static final TableSyncManage INSTANCE = new TableSyncManage();
    }

    public static TableSyncManage getInstance() {
        return LazyHolder.INSTANCE;
    }

    public static void join(TableSync task) {
        getInstance().tasks.add(task);
    }

    public static void leave(TableSync task) {
        getInstance().tasks.remove(task);
    }


    private static String formHTML(String url,String beanName, String field, String value) {
        StringBuilder sb = new StringBuilder();
        sb.append("<form method=\"post\" action=\"" + url + "\">");
        sb.append("Bean 名称:<input type=\"text\" name=\"beanName\" value=\""+beanName+"\">");
        sb.append("属性名称:<input type=\"text\" name=\"field\" value=\""+field+"\">");
        sb.append("属性值:<input type=\"text\" name=\"value\" value=\""+value+"\">");
        sb.append("<input type=\"submit\" value=\"查询\">");
        sb.append("</form> ");
        return sb.toString();
    }

    public static String status(String url, String beanName, String field, String value) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<body>");

        sb.append(formHTML(url, beanName,field, value));
        getInstance().tasks.stream().filter(task -> {
            if (beanName == null || beanName.isEmpty()) {
                return true;
            }
            return task.getDelegateType()!=null && beanName.equals(task.getDelegateType().getSimpleName());
        }).forEach(task -> {
            sb.append(task.status(field, value));
        });
        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }

    public static String status(String url) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<body>");

        sb.append(formHTML(url,"","",""));
        getInstance().tasks.forEach(task -> {
            sb.append(task.summary());
        });
        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }

}
