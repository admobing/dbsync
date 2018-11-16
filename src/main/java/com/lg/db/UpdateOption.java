package com.lg.db;

public class UpdateOption {
    private String name;
    private UpdateOption.Action action;
    private Object value;

    public UpdateOption() {
        this.action = UpdateOption.Action.SET;
    }

    public static UpdateOption create(String propertyName, UpdateOption.Action action, Object value) {
        UpdateOption updateOption = new UpdateOption();
        updateOption.action = action;
        updateOption.name = propertyName;
        updateOption.value = value;
        return updateOption;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UpdateOption.Action getAction() {
        return this.action;
    }

    public void setAction(UpdateOption.Action action) {
        this.action = action;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public static enum Action {
        SET,
        INC;

        private Action() {
        }
    }
}
