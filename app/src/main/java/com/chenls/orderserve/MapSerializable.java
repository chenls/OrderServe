package com.chenls.orderserve;

import com.chenls.orderserve.bean.Dish;

import java.io.Serializable;
import java.util.Map;


public class MapSerializable implements Serializable {
    private Map<Integer, Dish> map;

    public MapSerializable(Map<Integer, Dish> map) {
        this.map = map;
    }

    public Map<Integer, Dish> getMap() {
        return map;
    }
}
