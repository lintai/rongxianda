package com.sunmi.sunmit2demo.presenter.contact;

import com.sunmi.sunmit2demo.modle.ClassAndGoodsModle;

import java.util.List;

/**
 * author : chrc
 * date   : 2019/5/2  4:04 PM
 * desc   :
 */
public interface HomeClassAndGoodsContact {

    public interface View {
        void loadComplete(List<ClassAndGoodsModle> datas);
    }

    public interface Presenter {
        void load();
    }
}
