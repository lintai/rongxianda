package com.sunmi.sunmit2demo.modle;

/**
 * author : chrc
 * date   : 2019/5/7  10:27 PM
 * desc   :
 */
public class OrderInfo extends BaseModle {

    String cashFee;        //实收总金额，单位：分
    int status;         //订单状态：1-待支付 2-待发货 3-待收货 4-已完成 5-...
    String orderId;     //订单编号
    String termOrderId; //终端订单编号

    public OrderInfo(String cashFee, int status, String orderId, String termOrderId) {
        this.cashFee = cashFee;
        this.status = status;
        this.orderId = orderId;
        this.termOrderId = termOrderId;
    }

    public String getCashFee() {
        return cashFee;
    }

    public void setCashFee(String cashFee) {
        this.cashFee = cashFee;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTermOrderId() {
        return termOrderId;
    }

    public void setTermOrderId(String termOrderId) {
        this.termOrderId = termOrderId;
    }


}
