package com.sunmi.sunmit2demo.modle;

import android.os.Handler;

import java.util.List;

/**
 * author : chrc
 * date   : 2019/5/31  11:59 PM
 * desc   :
 */
public class PrinterModle extends BaseModle {

    private List<MenuItemModule> modules;
    private String orderNo;
    private String time;

    private float totalPrice;
    private float discountPrice;
    private float realPrice;
    private String payType;

    public PrinterModle(List<MenuItemModule> modules, String orderNo, String time, float totalPrice, float discountPrice, float realPrice, String payType) {
        this.modules = modules;
        this.orderNo = orderNo;
        this.time = time;
        this.totalPrice = totalPrice;
        this.discountPrice = discountPrice;
        this.realPrice = realPrice;
        this.payType = payType;
    }

    public List<MenuItemModule> getModules() {
        return modules;
    }

    public void setModules(List<MenuItemModule> modules) {
        this.modules = modules;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public float getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(float discountPrice) {
        this.discountPrice = discountPrice;
    }

    public float getRealPrice() {
        return realPrice;
    }

    public void setRealPrice(float realPrice) {
        this.realPrice = realPrice;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }
}
