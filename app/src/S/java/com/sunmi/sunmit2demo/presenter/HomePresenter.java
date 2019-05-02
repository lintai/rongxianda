package com.sunmi.sunmit2demo.presenter;

import android.content.Context;
import android.os.Handler;

import com.sunmi.sunmit2demo.modle.AllClassAndGoodsResult;
import com.sunmi.sunmit2demo.modle.ClassAndGoodsModle;
import com.sunmi.sunmit2demo.modle.GoodsInfo;
import com.sunmi.sunmit2demo.presenter.contact.HomeClassAndGoodsContact;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        //造假数据，先用new Thread方法，后面用Rxjava
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                AllClassAndGoodsResult result = new AllClassAndGoodsResult();
                result.setErrno(0);
                result.setError("");
                final List<ClassAndGoodsModle> modles = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    ClassAndGoodsModle classAndGoodsModle = new ClassAndGoodsModle();
                    classAndGoodsModle.setClassId(i);
                    classAndGoodsModle.setClassName("分类"+i);
                    List<GoodsInfo> infos = new ArrayList<>();
                    for (int j = 0; j < 10; j++) {
                        GoodsInfo info = new GoodsInfo();
                        info.setClassId(i);
                        info.setClassNme("分类"+i);
                        info.setFaceImg("");
                        info.setGoodsCode("123456");
                        info.setGoodsName(classAndGoodsModle.getClassName()+"_"+j);
                        info.setImgUrls("123456");
                        info.setNum(100);
                        info.setPlu(j);
                        info.setPrice(new Random().nextInt(20));
                        info.setPriceType(1);
                        info.setUnit("件");
                        info.setWeight(1);
                        infos.add(info);
                    }
                    classAndGoodsModle.setGoodsList(infos);
                    modles.add(classAndGoodsModle);
                }
                result.setResult(modles);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mView != null) {
                            mView.loadComplete(modles);
                        }
                    }
                });
            }
        }).start();
    }
}
