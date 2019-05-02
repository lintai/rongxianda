package com.sunmi.sunmit2demo.presenter;

import android.content.Context;

import com.sunmi.sunmit2demo.presenter.contact.HomeClassAndGoodsContact;

/**
 * author : chrc
 * date   : 2019/5/2  4:07 PM
 * desc   :
 */
public class HomePresenter implements HomeClassAndGoodsContact.Presenter {


    private Context context;
    private HomeClassAndGoodsContact.View mView;

    public HomePresenter(Context context, HomeClassAndGoodsContact.View mView) {
        this.context = context;
        this.mView = mView;
    }


    @Override
    public void load() {

    }
}
