package com.lg.dbsync;

import com.lg.util.ClassHelp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

class Model {

    private final Long createAt = System.currentTimeMillis();

    private Object record;
    private Consumer updater;

    private Long ver = 0l;
    private int dirtyCount = 0;
    private long updateAt;

    private DirtySignature signature = new DirtySignature();

    private Map<String, Field> fieldMap = new HashMap<>();

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
    private ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
    private ReentrantReadWriteLock.ReadLock readLock = lock.readLock();

    private static Logger logger = LoggerFactory.getLogger(Model.class);

    public Model(Object record, Consumer updater) {
        this.record = record;
        this.updater = updater;
    }

    private Field findField(String fieldName) {
        return fieldMap.computeIfAbsent(fieldName, name -> ClassHelp.findField(fieldName, record.getClass()));
    }

    public void inc(String prop, Object inc) {
        writeLock.lock();
        ver++;
        dirtyCount++;
        try {
            try {
                Field field = findField(prop);
                Type type = field.getType();
                if (type == Integer.class || type == Integer.TYPE) {
                    field.set(this.record, (int) field.get(this.record) + (int) inc);
                } else if (type == Double.class || type == Double.TYPE) {
                    field.set(this.record, (double) field.get(this.record) + (double) inc);
                } else if (type == Float.class || type == Float.TYPE) {
                    field.set(this.record, (float) field.get(this.record) + (float) inc);
                } else if (type == BigDecimal.class) {
                    field.set(this.record, ((BigDecimal) field.get(this.record)).add((BigDecimal) inc));
                } else {
                    throw new RuntimeException("不支持的inc类型:" + type);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } finally {
            writeLock.unlock();
        }
    }

    public void set(String prop, Object val) {
        writeLock.lock();
        ver++;
        dirtyCount++;
        try {
            try {
                Field field = findField(prop);
                field.set(this.record, val);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } finally {
            writeLock.unlock();
        }
    }

    public void flush() {
        writeLock.lock();
        if (dirtyCount == 0) {
            return;
        }
        try {
            this.updater.accept(record);
            dirtyCount = 0;
            updateAt = System.currentTimeMillis();
        } catch (Exception err) {
            logger.error("同步表失败", err);
        } finally {
            writeLock.unlock();
        }
    }

    public DirtySignature getDirtySignature() {
        readLock.lock();
        try {
            signature.dirtyCount = dirtyCount;
            signature.updateAt = updateAt;
            signature.ver = ver;
            return signature;
        } finally {
            readLock.unlock();
        }

    }

    public Object getUnsafeVal(String fieldName) {
        Field field = findField(fieldName);
        try {
            return field.get(this.record);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getVal(String fieldName) {
        readLock.lock();
        try {
            Field field = findField(fieldName);
            try {
                return field.get(this.record);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } finally {
            readLock.unlock();
        }
    }

    public Object getRecord() {
        return this.record;
    }

    class DirtySignature {

        private Long ver = 0l;
        private int dirtyCount = 0;
        private long updateAt;

        public Long getVer() {
            return ver;
        }

        public void setVer(Long ver) {
            this.ver = ver;
        }

        public int getDirtyCount() {
            return dirtyCount;
        }

        public void setDirtyCount(int dirtyCount) {
            this.dirtyCount = dirtyCount;
        }

        public long getUpdateAt() {
            return updateAt;
        }

        public void setUpdateAt(long updateAt) {
            this.updateAt = updateAt;
        }
    }
}
