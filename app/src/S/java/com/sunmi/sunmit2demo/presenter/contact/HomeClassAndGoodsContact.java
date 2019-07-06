package com.sunmi.sunmit2demo.presenter.contact;

import com.sunmi.sunmit2demo.modle.ClassAndGoodsModle;
import com.sunmi.sunmit2demo.modle.MenuItemModule;
import com.sunmi.sunmit2demo.modle.OrderInfo;

import java.util.List;

/**
 * author : chrc
 * date   : 2019/5/2  4:04 PM
 * desc   :
 */
public interface HomeClassAndGoodsContact {

    public interface View {
        void loadComplete(List<ClassAndGoodsModle> datas);
        void orderCreateComplete(OrderInfo orderInfo);
        void printerOutOfConnected();
    }

    public interface Presenter {
        void load();
        void pay(List<MenuItemModule> goodList, int totalPrice);
    }
}
