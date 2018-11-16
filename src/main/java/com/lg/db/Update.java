package com.lg.db;

import com.alibaba.fastjson.JSON;
import com.lg.db.UpdateOption.Action;

import java.util.LinkedList;
import java.util.List;

public class Update {
    private List<UpdateOption> options = new LinkedList();

    public Update() {
    }

    public static Update create() {
        return new Update();
    }

    public static Update from(String options) {
        if (options == null) {
            return null;
        } else {
            Update update = new Update();

            try {
                update.options.addAll(JSON.parseArray(options, UpdateOption.class));
            } catch (Exception var3) {
                update.options.add(JSON.parseObject(options, UpdateOption.class));
            }

            return update;
        }
    }

    public static Update from(List<UpdateOption> options) {
        Update update = new Update();
        update.options.addAll(options);
        return update;
    }

    public Update set(String propertyName, Object value) {
        this.options.add(UpdateOption.create(propertyName, Action.SET, value));
        return this;
    }

    public Update inc(String propertyName, Object value) {
        this.options.add(UpdateOption.create(propertyName, Action.INC, value));
        return this;
    }

    public List<UpdateOption> getOptions() {
        return this.options;
    }

    public boolean isEmpty() {
        return this.options.isEmpty();
    }
}
