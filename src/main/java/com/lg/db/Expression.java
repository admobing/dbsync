package com.lg.db;

public class Expression {
    private String name;
    private Expression.Compare compare;
    private Object value;

    public Expression() {
        this.compare = Expression.Compare.EQ;
    }

    public static Expression create(String name, Expression.Compare compare, Object value) {
        Expression expression = new Expression();
        expression.compare = compare;
        expression.name = name;
        expression.value = value;
        return expression;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Expression.Compare getCompare() {
        return this.compare;
    }

    public void setCompare(Expression.Compare compare) {
        this.compare = compare;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public static enum Compare {
        EQ,
        NE,
        LT,
        LTE,
        GT,
        GTE,
        LIKE,
        IN,
        NIN,
        NULL,
        NOTNULL,
        ORDER,
        LIMIT,
        WHERE;

        private Compare() {
        }
    }
}
