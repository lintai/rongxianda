package com.sunmi.sunmit2demo.modle;

import java.util.List;

/**
 * author : chrc
 * date   : 2019/5/2  3:44 PM
 * desc   :
 */
public class AllClassAndGoodsResult extends BaseModle {

    private int errno;
    private String error;
    private List<ClassAndGoodsModle> result;

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

    public List<ClassAndGoodsModle> getResult() {
        return result;
    }

    public void setResult(List<ClassAndGoodsModle> result) {
        this.result = result;
    }
}
