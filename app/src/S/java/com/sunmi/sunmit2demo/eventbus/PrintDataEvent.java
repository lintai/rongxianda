package com.sunmi.sunmit2demo.eventbus;

/**
 * author : chrc
 * date   : 2019/5/31  11:53 PM
 * desc   : 通知打印机打印
 */
public class PrintDataEvent {

    public String orderNo;//订单号

    public String time;

    public String payType;

    public boolean openCashBox;

    public int printCount;

    public PrintDataEvent(boolean openCashBox) {
        this.openCashBox = openCashBox;
    }

    public PrintDataEvent(String orderNo, String time, String payType, int printCount) {
        this.orderNo = orderNo;
        this.time = time;
        this.payType = payType;
        this.printCount = printCount;
    }
}
