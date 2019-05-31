package com.sunmi.sunmit2demo.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunmi.sunmit2demo.PreferenceUtil;
import com.sunmi.sunmit2demo.R;
import com.sunmi.sunmit2demo.Util;
import com.sunmi.sunmit2demo.eventbus.PayCodeEvent;
import com.sunmi.sunmit2demo.eventbus.PrintDataEvent;
import com.sunmi.sunmit2demo.modle.OrderInfo;
import com.sunmi.sunmit2demo.modle.PayCheckInfo;
import com.sunmi.sunmit2demo.modle.PayInfo;
import com.sunmi.sunmit2demo.modle.Result;
import com.sunmi.sunmit2demo.server.ServerManager;
import com.sunmi.sunmit2demo.utils.Utils;
import com.sunmi.sunmit2demo.view.LoadingView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.IllegalFormatCodePointException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class PayingActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String ORDER_RESULT = "order_result";
    public static final String GOODS_COUNT = "goods_count";
    public static final String GOODS_ORIGINAL_PRICE = "goods_original_price";
    public static final String GOODS_PAY_TYPE = "goods_pay_type";

    public static final int ALI_PAY_TYPE = 2;
    public static final int WX_PAY_TYPE = 1;
    public static final int MEMBER_PAYT_TYPE = 3;
    public static final int CASH_PAYT_TYPE = 4;

    public static final String GOODS_AUTHO_DATA = "goods_autho_data";//测试用的代码

    private TextView goodsCountTv, goodsPriceTv, goodsDiscountTv, payTypeTv;
    private TextView preTv, payTv, cashReturnTv, codeNameTv;
    private EditText payCodeEt;
    private LinearLayout cashPayLayout, otherPayLayout;
    private View focusView;

    private LoadingView loadingView;

    private CompositeDisposable compositeDisposable;
    private OrderInfo orderInfo;
    private String authoCode;
    private int payType;
    private boolean isWaiting;

    private float goodsOriginalPrice;

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
                if (cashPayLayout.getVisibility() == View.VISIBLE) {
                    try {
                        String priceData = payCodeEt.getText().toString();
                        if (priceData != null && priceData.length() == 1 && (priceData.equals(".") || priceData.equals("0"))) {
                            payCodeEt.setText("");
                        } else if (!TextUtils.isEmpty(priceData)){
                            float price = Float.parseFloat(priceData);
                            cashReturnTv.setText("￥"+Utils.numberFormat(price - goodsOriginalPrice / 100));
                        } else {
                            cashReturnTv.setText("");
                        }

                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                } else {
                    authoCode = s.toString();
                }
            }
        });

        payTv.setSelected(true);
        payTv.setOnClickListener(this);
        preTv.setOnClickListener(this);
        cancelLastViewFocus(payTv);

        otherPayLayout = findViewById(R.id.layout_other_pay);
        payTypeTv = findViewById(R.id.tv_pay_type);
        cashPayLayout = findViewById(R.id.layout_cash_pay);
        cashReturnTv = findViewById(R.id.tv_cash_return);
        codeNameTv = findViewById(R.id.code_name_tv);
    }

    private void initData() {
        compositeDisposable = new CompositeDisposable();

        Bundle bundle = getIntent().getExtras();
        try {
            authoCode = bundle.getString(GOODS_AUTHO_DATA);
            orderInfo = (OrderInfo) bundle.getSerializable(ORDER_RESULT);

            String goodsCount = bundle.getString(GOODS_COUNT);
            goodsOriginalPrice = bundle.getFloat(GOODS_ORIGINAL_PRICE);

            goodsCountTv.setText("总共"+goodsCount+"商品");
            goodsPriceTv.setText(Utils.numberFormat(goodsOriginalPrice / 100));
            try {
                goodsDiscountTv.setText(Utils.numberFormat(Float.parseFloat(orderInfo.getCashFee()) / 100));
            } catch (NumberFormatException e) {
                goodsDiscountTv.setText("价格返回错误");
                e.printStackTrace();
            }

            payType = bundle.getInt(GOODS_PAY_TYPE);
            if (payType == CASH_PAYT_TYPE) {
                cashPayLayout.setVisibility(View.VISIBLE);
                payCodeEt.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                payCodeEt.setHint("单位：元");
                payTv.setText("结算成功");
                codeNameTv.setText("实收");
            } else {
                otherPayLayout.setVisibility(View.VISIBLE);
                payTypeTv.setText(getPayType(payType));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getPayType(int payType) {
        String type = "";
        if (payType == ALI_PAY_TYPE) {
            type = "支付宝";
        } else if (payType == MEMBER_PAYT_TYPE) {
            type = "会员余额";
        } else {
            type = "微信";
        }
        return type;
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
        EventBus.getDefault().post(new PrintDataEvent(orderInfo.getOrderId(), Util.getCurrData(), getPayType(payType)));
//        loadingView.setVisibility(View.VISIBLE);
//        PreferenceUtil.putString(this, PreferenceUtil.KEY.PAYING_TYPE, "paying");
//        if (TextUtils.isEmpty(authoCode)) {
//            isWaiting = true;
//            return;
//        } else {
//            isWaiting = false;
//        }
//
//        Disposable disposable = Observable.create(new ObservableOnSubscribe<PayInfo>() {
//            @Override
//            public void subscribe(ObservableEmitter<PayInfo> e) throws Exception {
//                Result<PayInfo> result = ServerManager.pay(Util.appId, orderInfo.getOrderId(), payType, authoCode,  2);
//                if (result != null && result.getErrno() == 0 && result.getResult() != null) {
//                    e.onNext(result.getResult());
//                } else {
//                    e.onError(new Throwable());
//                }
//            }
//        })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(new DisposableObserver<PayInfo>() {
//                    @Override
//                    public void onNext(PayInfo payInfo) {
//                        checkPayStatus();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        loadingView.setVisibility(View.GONE);
//                        PreferenceUtil.putString(PayingActivity.this, PreferenceUtil.KEY.PAYING_TYPE, "");
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//        compositeDisposable.add(disposable);
    }

    private void checkPayStatus() {
        final long currTime = System.currentTimeMillis();
        Disposable disposable = Observable.interval(0, 2, TimeUnit.SECONDS)
                .flatMap(new Function<Long, ObservableSource<PayCheckInfo>>() {
                    @Override
                    public ObservableSource<PayCheckInfo> apply(Long aLong) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<PayCheckInfo>() {
                            @Override
                            public void subscribe(ObservableEmitter<PayCheckInfo> e) throws Exception {
                                Log.i("timer_time==="," request_time="+String.valueOf(System.currentTimeMillis() - currTime));
                                Result<PayCheckInfo> result = ServerManager.payStatusCheck(Util.appId, orderInfo.getOrderId());
                                if (result != null && result.getErrno() == 0 && result.getResult() != null) {
                                    e.onNext(result.getResult());
                                } else {
                                    e.onError(new Throwable());
                                }
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<PayCheckInfo>() {
                    @Override
                    public void onNext(PayCheckInfo payCheckInfo) {
                        Log.i("timer_time==="," response_time="+String.valueOf(System.currentTimeMillis() - currTime));
                        if (payCheckInfo != null && "1".equals(payCheckInfo)) {
                            loadingView.setVisibility(View.GONE);
                            PreferenceUtil.putString(PayingActivity.this, PreferenceUtil.KEY.PAYING_TYPE, "");
                            compositeDisposable.remove(this);
                        } else if (System.currentTimeMillis() - currTime > 60 * 1000){
                            loadingView.setVisibility(View.GONE);
                            PreferenceUtil.putString(PayingActivity.this, PreferenceUtil.KEY.PAYING_TYPE, "");
                            compositeDisposable.remove(this);
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        loadingView.setVisibility(View.GONE);
                        PreferenceUtil.putString(PayingActivity.this, PreferenceUtil.KEY.PAYING_TYPE, "");
                        compositeDisposable.remove(this);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        compositeDisposable.add(disposable);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_pay:
                payTv.setSelected(true);
                cancelLastViewFocus(payTv);
                if (payType == CASH_PAYT_TYPE) {
                    setResult(-1);
                    finish();
                } else if (!TextUtils.isEmpty(payCodeEt.getText().toString())) {
                    pay();
                }
                break;
            case R.id.tv_pre:
                preTv.setSelected(true);
                cancelLastViewFocus(preTv);
                finish();
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
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }
}
