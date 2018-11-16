package com.lg.test;

import java.math.BigDecimal;

public class User {

    private String name;

    private Integer intV = 0;

    private int intv;

    private Float floatV = 0f;

    private Double doubleV = 0.0;

    private float floatv;

    private double doublev;

    private BigDecimal bigDecimal = new BigDecimal(0);

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIntV() {
        return intV;
    }

    public void setIntV(Integer intV) {
        this.intV = intV;
    }

    public int getIntv() {
        return intv;
    }

    public void setIntv(int intv) {
        this.intv = intv;
    }

    public Float getFloatV() {
        return floatV;
    }

    public void setFloatV(Float floatV) {
        this.floatV = floatV;
    }

    public Double getDoubleV() {
        return doubleV;
    }

    public void setDoubleV(Double doubleV) {
        this.doubleV = doubleV;
    }

    public float getFloatv() {
        return floatv;
    }

    public void setFloatv(float floatv) {
        this.floatv = floatv;
    }

    public double getDoublev() {
        return doublev;
    }

    public void setDoublev(double doublev) {
        this.doublev = doublev;
    }

    public BigDecimal getBigDecimal() {
        return bigDecimal;
    }

    public void setBigDecimal(BigDecimal bigDecimal) {
        this.bigDecimal = bigDecimal;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", intV=" + intV +
                ", intv=" + intv +
                ", floatV=" + floatV +
                ", doubleV=" + doubleV +
                ", floatv=" + floatv +
                ", doublev=" + doublev +
                ", bigDecimal=" + bigDecimal +
                '}';
    }
}
