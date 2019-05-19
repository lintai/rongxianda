package com.sunmi.sunmit2demo.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sunmi.sunmit2demo.PreferenceUtil;
import com.sunmi.sunmit2demo.R;
import com.sunmi.sunmit2demo.Util;
import com.sunmi.sunmit2demo.eventbus.PayCodeEvent;
import com.sunmi.sunmit2demo.modle.OrderInfo;
import com.sunmi.sunmit2demo.modle.PayInfo;
import com.sunmi.sunmit2demo.modle.Result;
import com.sunmi.sunmit2demo.server.ServerManager;
import com.sunmi.sunmit2demo.utils.Utils;
import com.sunmi.sunmit2demo.view.LoadingView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class PayingActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String ORDER_RESULT = "order_result";
    public static final String GOODS_COUNT = "goods_count";
    public static final String GOODS_ORIGINAL_PRICE = "goods_original_price";
    public static final String GOODS_PAY_TYPE = "goods_pay_type";

    public static final String GOODS_AUTHO_DATA = "goods_autho_data";//测试用的代码

    private TextView goodsCountTv, goodsPriceTv, goodsDiscountTv, payTypeTv;
    private TextView preTv, payTv;
    private EditText payCodeEt;
    private View focusView;

    private LoadingView loadingView;

    private Disposable disposable;
    private OrderInfo orderInfo;
    private String authoCode;
    private int payType;
    private boolean isWaiting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paying);
        EventBus.getDefault().register(this);
        initView();
        initData();
    }

    private void initView() {
        goodsCountTv = findViewById(R.id.tv_goods_count);
        goodsPriceTv = findViewById(R.id.tv_original_price);
        goodsDiscountTv = findViewById(R.id.tv_discount_price);
        preTv = findViewById(R.id.tv_pre);
        payTv = findViewById(R.id.tv_pay);
        loadingView = findViewById(R.id.loading_view);

        payCodeEt = findViewById(R.id.et_pay_code);
        payCodeEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                authoCode = s.toString();
            }
        });

        payTv.setSelected(true);
        payTv.setOnClickListener(this);
        cancelLastViewFocus(payTv);
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        try {
            authoCode = bundle.getString(GOODS_AUTHO_DATA);
            orderInfo = (OrderInfo) bundle.getSerializable(ORDER_RESULT);
            payType = bundle.getInt(GOODS_PAY_TYPE);
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

    private void cancelLastViewFocus(View view) {
        if (focusView != null) {
            focusView.setSelected(false);
            focusView = view;
        } else {
            focusView = view;
        }
    }

    private void pay() {
        loadingView.setVisibility(View.VISIBLE);
        PreferenceUtil.putString(this, PreferenceUtil.KEY.PAYING_TYPE, "paying");
        if (TextUtils.isEmpty(authoCode)) {
            isWaiting = true;
            return;
        } else {
            isWaiting = false;
        }

        disposable = Observable.create(new ObservableOnSubscribe<PayInfo>() {
            @Override
            public void subscribe(ObservableEmitter<PayInfo> e) throws Exception {
                Result<PayInfo> result = ServerManager.pay(Util.appId, orderInfo.getOrderId(), payType, authoCode,  2);
                if (result != null && result.getErrno() == 0 && result.getResult() != null) {
                    e.onNext(result.getResult());
                } else {
                    e.onError(new Throwable());
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<PayInfo>() {
                    @Override
                    public void onNext(PayInfo payInfo) {
                        loadingView.setVisibility(View.GONE);
                        PreferenceUtil.putString(PayingActivity.this, PreferenceUtil.KEY.PAYING_TYPE, "");
                    }

                    @Override
                    public void onError(Throwable e) {
                        loadingView.setVisibility(View.GONE);
                        PreferenceUtil.putString(PayingActivity.this, PreferenceUtil.KEY.PAYING_TYPE, "");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_pay:
                if (!TextUtils.isEmpty(payCodeEt.getText().toString())) {
                    pay();
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void payCodeEvent(PayCodeEvent event) {
        if (!TextUtils.isEmpty(event.payCode)) {
            authoCode = event.payCode;
            payCodeEt.setText(event.payCode);
            if (isWaiting) {
                pay();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (disposable != null) {
            disposable.dispose();
        }
    }
}
