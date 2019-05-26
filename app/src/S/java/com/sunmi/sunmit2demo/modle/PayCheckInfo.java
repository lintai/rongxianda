package com.sunmi.sunmit2demo.modle;

/**
 * author : chrc
 * date   : 2019/5/26  1:11 PM
 * desc   :
 */
public class PayCheckInfo extends BaseModle {

    String orderid;
    String payorderid;
    String paystatus;
    int cashfee;
    String paytype;

    public PayCheckInfo(String orderid, String payorderid, String paystatus, int cashfee, String paytype) {
        this.orderid = orderid;
        this.payorderid = payorderid;
        this.paystatus = paystatus;
        this.cashfee = cashfee;
        this.paytype = paytype;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getPayorderid() {
        return payorderid;
    }

    public void setPayorderid(String payorderid) {
        this.payorderid = payorderid;
    }

    public String getPaystatus() {
        return paystatus;
    }

    public void setPaystatus(String paystatus) {
        this.paystatus = paystatus;
    }

    public int getCashfee() {
        return cashfee;
    }

    public void setCashfee(int cashfee) {
        this.cashfee = cashfee;
    }

    public String getPaytype() {
        return paytype;
    }

    public void setPaytype(String paytype) {
        this.paytype = paytype;
    }
}
