package com.sunmi.sunmit2demo.modle;

/**
 * author : chrc
 * date   : 2019/5/7  10:26 PM
 * desc   :
 */
public class CreateOrderResult extends BaseModle {

    int errno;
    String error;
    OrderInfo result;

    public CreateOrderResult(int errno, String error, OrderInfo result) {
        this.errno = errno;
        this.error = error;
        this.result = result;
    }

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public OrderInfo getResult() {
        return result;
    }

    public void setResult(OrderInfo result) {
        this.result = result;
    }
}
