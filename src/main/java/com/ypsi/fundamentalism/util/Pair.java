package com.ypsi.fundamentalism.util;

public class Pair {
    private Double number;
    private Integer color;

    public Pair(Double number, Integer color){
        this.number = number;
        this.color = color;
    }

    public double getNumber(){
        return number;
    }
    public Integer getColor(){
        return color;
    }
}
