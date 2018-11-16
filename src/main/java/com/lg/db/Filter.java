package com.lg.db;

import com.alibaba.fastjson.JSON;
import com.lg.db.Expression.Compare;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Filter {
    private List<Expression> expressions = new LinkedList();
    private Object lastId;
    private int lastPage;
    private List<Filter> and;
    private List<Filter> or;

    public static Filter and(Filter filter1, Filter filter2) {
        return filter1.and(filter2);
    }

    public static Filter or(Filter filter1, Filter filter2) {
        return filter1.or(filter2);
    }

    public static Filter empty() {
        return new Filter();
    }

    public static Filter create() {
        return new Filter();
    }

    private Filter() {
    }

    public Filter copy() {
        Filter cp = new Filter();
        cp.expressions = new LinkedList(this.expressions);
        return cp;
    }

    public static Filter from(String expressions) {
        Filter filter = new Filter();

        try {
            filter.expressions.addAll(JSON.parseArray(expressions, Expression.class));
        } catch (Exception var3) {
            filter.expressions.add(JSON.parseObject(expressions, Expression.class));
        }

        return filter;
    }

    public static Filter from(List<Expression> expressions) {
        Filter filter = new Filter();
        filter.expressions.addAll(expressions);
        return filter;
    }

    public Filter eq(String propertyName, Object value) {
        this.expressions.add(Expression.create(propertyName, Compare.EQ, value));
        return this;
    }

    public Filter ne(String propertyName, Object value) {
        this.expressions.add(Expression.create(propertyName, Compare.NE, value));
        return this;
    }

    public Filter lt(String propertyName, Object value) {
        this.expressions.add(Expression.create(propertyName, Compare.LT, value));
        return this;
    }

    public Filter lte(String propertyName, Object value) {
        this.expressions.add(Expression.create(propertyName, Compare.LTE, value));
        return this;
    }

    public Filter gt(String propertyName, Object value) {
        this.expressions.add(Expression.create(propertyName, Compare.GT, value));
        return this;
    }

    public Filter gte(String propertyName, Object value) {
        this.expressions.add(Expression.create(propertyName, Compare.GTE, value));
        return this;
    }

    public Filter like(String propertyName, Object value) {
        this.expressions.add(Expression.create(propertyName, Compare.LIKE, value));
        return this;
    }

    public Filter in(String propertyName, Collection value) {
        this.expressions.add(Expression.create(propertyName, Compare.IN, value));
        return this;
    }

    public Filter notin(String propertyName, Collection value) {
        this.expressions.add(Expression.create(propertyName, Compare.NIN, value));
        return this;
    }

    public Filter where(String value) {
        this.expressions.add(Expression.create((String)null, Compare.WHERE, value));
        return this;
    }

    public Filter and(Filter filter) {
        if(this.and == null) {
            this.and = new ArrayList();
        }

        this.and.add(filter);
        return this;
    }

    public Filter or(Filter filter) {
        if(this.or == null) {
            this.or = new ArrayList();
        }

        this.or.add(filter);
        return this;
    }

    public Filter isNull(String propertyName) {
        this.expressions.add(Expression.create(propertyName, Compare.NULL, (Object)null));
        return this;
    }

    public Filter notNull(String propertyName) {
        this.expressions.add(Expression.create(propertyName, Compare.NOTNULL, (Object)null));
        return this;
    }

    public Filter lastPage(Object lastId, int lastPage) {
        this.lastId = lastId;
        this.lastPage = lastPage;
        return this;
    }

    public Filter order(String propertyName, boolean asc) {
        this.expressions.add(Expression.create(propertyName, Compare.ORDER, Boolean.valueOf(asc)));
        return this;
    }

    public Filter limit(int size) {
        this.expressions.add(Expression.create((String)null, Compare.LIMIT, Integer.valueOf(size)));
        return this;
    }

    public List<Expression> getExpressions() {
        return this.expressions;
    }

    public boolean isEmpty() {
        return this.expressions.isEmpty();
    }

    public Object getLastId() {
        return this.lastId;
    }

    public int getLastPage() {
        return this.lastPage;
    }

    public void setLastId(Object lastId) {
        this.lastId = lastId;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

    public List<Filter> getAnd() {
        return this.and == null?Collections.EMPTY_LIST:this.and;
    }

    public List<Filter> getOr() {
        return this.or == null?Collections.EMPTY_LIST:this.or;
    }
}
