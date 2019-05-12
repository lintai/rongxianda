package com.sunmi.sunmit2demo.modle;

/**
 * author : chrc
 * date   : 2019/5/11  12:15 PM
 * desc   :
 */
public class Result<T> extends BaseModle {
    private int errno;
    private String error;
    private T result;

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

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
