package com.sunmi.sunmit2demo.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sunmi.sunmit2demo.R;
import com.sunmi.sunmit2demo.Util;
import com.sunmi.sunmit2demo.modle.OrderInfo;
import com.sunmi.sunmit2demo.modle.PayInfo;
import com.sunmi.sunmit2demo.modle.Result;
import com.sunmi.sunmit2demo.server.ServerManager;
import com.sunmi.sunmit2demo.utils.Utils;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ChoosePayWayActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String ORDER_RESULT = "order_result";
    public static final String GOODS_COUNT = "goods_count";
    public static final String GOODS_ORIGINAL_PRICE = "goods_original_price";

    public static final String GOODS_AUTHO_DATA = "goods_autho_data";

    private TextView goodsCountTv, goodsPriceTv, goodsDiscountTv;
    private TextView cashPayTv, memberPayTv, wxPayTv, aliPayTv;
    private TextView nextTv;

    private View focusView;

    private int payType;
    private OrderInfo orderInfo;

    private String authoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_pay_way);
        initView();
        initData();
    }

    private void initView() {
        goodsCountTv = findViewById(R.id.tv_goods_count);
        goodsPriceTv = findViewById(R.id.tv_original_price);
        goodsDiscountTv = findViewById(R.id.tv_discount_price);
        cashPayTv = findViewById(R.id.tv_cash_pay);
        memberPayTv = findViewById(R.id.tv_member_pay);
        wxPayTv = findViewById(R.id.tv_wx_pay);
        aliPayTv = findViewById(R.id.tv_ali_pay);
        nextTv = findViewById(R.id.tv_next);

        cashPayTv.setOnClickListener(this);
        memberPayTv.setOnClickListener(this);
        wxPayTv.setOnClickListener(this);
        aliPayTv.setOnClickListener(this);
        nextTv.setOnClickListener(this);

        cashPayTv.setSelected(true);
        cancelLastViewFocus(cashPayTv);
        payType = 4;
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        try {
            authoData = bundle.getString(GOODS_AUTHO_DATA);
            orderInfo = (OrderInfo) bundle.getSerializable(ORDER_RESULT);
            String goodsCount = bundle.getString(GOODS_COUNT);
            float goodsOriginalPrice = bundle.getFloat(GOODS_ORIGINAL_PRICE);


            goodsCountTv.setText("总共"+goodsCount+"商品");
            goodsPriceTv.setText(Utils.numberFormat(goodsOriginalPrice / 100));
            try {
                goodsDiscountTv.setText(Utils.numberFormat(Float.parseFloat(orderInfo.getCashFee()) / 100));
            } catch (NumberFormatException e) {
                goodsDiscountTv.setText("价格返回错误");
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cash_pay:
                cashPayTv.setSelected(true);
                cancelLastViewFocus(cashPayTv);
                payType = 4;
                break;
            case R.id.tv_member_pay:
                memberPayTv.setSelected(true);
                cancelLastViewFocus(memberPayTv);
                break;
            case R.id.tv_wx_pay:
                wxPayTv.setSelected(true);
                cancelLastViewFocus(wxPayTv);
                payType = 1;
                break;
            case R.id.tv_ali_pay:
                aliPayTv.setSelected(true);
                cancelLastViewFocus(aliPayTv);
                payType = 2;
                break;
            case R.id.tv_next:
                pay();
                break;
        }
    }

    private void pay() {
        Observable.create(new ObservableOnSubscribe<PayInfo>() {
            @Override
            public void subscribe(ObservableEmitter<PayInfo> e) throws Exception {
                Result<PayInfo> result = ServerManager.pay(Util.appId, orderInfo.getOrderId(), payType, authoData,  2);
                if (result != null && result.getErrno() == 0 && result.getResult() != null) {
                    e.onNext(result.getResult());
                } else {
                    e.onError(new Throwable());
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<PayInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PayInfo value) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void cancelLastViewFocus(View view) {
        if (focusView != null) {
            focusView.setSelected(false);
            focusView = view;
        } else {
            focusView = view;
        }
    }
}
