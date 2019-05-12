package com.sunmi.sunmit2demo.presenter;

import android.content.Context;

import com.sunmi.sunmit2demo.Util;
import com.sunmi.sunmit2demo.modle.AllClassAndGoodsResult;
import com.sunmi.sunmit2demo.modle.ClassAndGoodsModle;
import com.sunmi.sunmit2demo.modle.CreateOrderResult;
import com.sunmi.sunmit2demo.modle.GoodsOrderModle;
import com.sunmi.sunmit2demo.modle.MenuItemModule;
import com.sunmi.sunmit2demo.modle.OrderInfo;
import com.sunmi.sunmit2demo.presenter.contact.HomeClassAndGoodsContact;
import com.sunmi.sunmit2demo.server.ServerManager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
        Observable.create(new ObservableOnSubscribe<List<ClassAndGoodsModle>>() {
            @Override
            public void subscribe(ObservableEmitter<List<ClassAndGoodsModle>> e) throws Exception {
                AllClassAndGoodsResult result = ServerManager.getClassGoodsList(Util.appId);
                if (result != null && result.getErrno() == 0 && result.getResult() != null) {
                    e.onNext(result.getResult());
                } else {
                    e.onError(new Throwable());
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<List<ClassAndGoodsModle>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<ClassAndGoodsModle> value) {
                        mView.loadComplete(value);
                    }

                    @Override
                    public void onError(Throwable e) {
//                        mView.loadComplete(null);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void pay(final List<MenuItemModule> goodList, final int totalPrice) {
        if (goodList == null || goodList.size() == 0) return;
        Observable.create(new ObservableOnSubscribe<OrderInfo>() {
            @Override
            public void subscribe(ObservableEmitter<OrderInfo> e) throws Exception {
                List<GoodsOrderModle> orderModles = new ArrayList<>();
                int size = goodList.size();
                for (int i = 0; i < size; i++) {
                    MenuItemModule module = goodList.get(i);
                    float price;
                    if (module.getPriceType() == 2) {
                        price = module.getPrice() * module.getGoodsCount();
                    } else {
                        price = module.getPrice();
                    }
                    GoodsOrderModle orderModle = new GoodsOrderModle(module.getGoodsCode(), String.valueOf(module.getGoodsCount()), price);
                    orderModles.add(orderModle);
                }
                CreateOrderResult result = ServerManager.createOrder(Util.appId, orderModles, totalPrice);
                if (result != null && result.getErrno() == 0 && result.getResult() != null) {
                    e.onNext(result.getResult());
                } else {
                    e.onError(new Throwable());
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<OrderInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(OrderInfo value) {
                        mView.orderCreateComplete(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.orderCreateComplete(null);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
