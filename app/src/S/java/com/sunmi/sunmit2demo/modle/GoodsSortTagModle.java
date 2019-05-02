package com.sunmi.sunmit2demo.modle;

/**
 * author : chrc
 * date   : 2019/5/2  4:11 PM
 * desc   :
 */
public class GoodsSortTagModle extends BaseModle{

    private long classId;
    private String className;

    public GoodsSortTagModle(long classId, String className) {
        this.classId = classId;
        this.className = className;
    }

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
}
