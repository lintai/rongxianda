package com.sunmi.sunmit2demo.modle;

import java.util.List;

/**
 * author : chrc
 * date   : 2019/5/2  3:47 PM
 * desc   :
 */
public class ClassAndGoodsModle extends BaseModle{

    private long classId;//分类id
    private String className;//分类名称
    private List<GoodsInfo> goodsList;

    public long getClassId() {
        return classId;
    }

    public void setClassId(long classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<GoodsInfo> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<GoodsInfo> goodsList) {
        this.goodsList = goodsList;
    }
}
