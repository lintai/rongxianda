package com.sunmi.sunmit2demo.modle;

/**
 * author : chrc
 * date   : 2019/5/11  12:16 PM
 * desc   :
 */
public class PayInfo extends BaseModle {

    private String orderid;
    private String payorderid;
    private String status;	//状态：6-支付中 2-支付成功 7-支付失败（返回支付中状态需
    private int cashfee;
    private String paytype;

    public PayInfo(String orderid, String payorderid, String status, int cashfee, String paytype) {
        this.orderid = orderid;
        this.payorderid = payorderid;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
