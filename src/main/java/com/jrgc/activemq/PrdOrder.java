package com.jrgc.activemq;

import java.io.Serializable;

/**
 * @yuchengying
 * @create 2018-02-08 17:03
 **/

public class PrdOrder implements Serializable {
    private Integer id;
    private String orderNo;
    private String orderName;
    private double price;

    public PrdOrder() {
    }

    public PrdOrder(Integer id, String orderNo, String orderName, double price) {
        this.id = id;
        this.orderNo = orderNo;
        this.orderName = orderName;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "{" +
                "id:" + id +
                ", orderNo:'" + orderNo + '\'' +
                ", orderName:'" + orderName + '\'' +
                ", price:" + price +
                '}';
    }
}
