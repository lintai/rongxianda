package com.sunmi.sunmit2demo.ui;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.concurrent.TimeUnit;

import freemarker.template.utility.StringUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
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

    private float goodsOriginalPrice;
    private String goodsCount;
    private float cashReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paying);
        EventBus.getDefault().register(this);
        PreferenceUtil.putString(this, PreferenceUtil.KEY.PAYING_TYPE, "paying");
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
                            cashReturnTv.setText("￥0");
                        } else if (!TextUtils.isEmpty(priceData)){
                            float price = Float.parseFloat(priceData);
                            cashReturn = price - goodsOriginalPrice / 100;
                            cashReturnTv.setText("￥"+Utils.numberFormat(cashReturn));
                        } else {
                            cashReturnTv.setText("￥0");
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

            goodsCount = bundle.getString(GOODS_COUNT);
            goodsOriginalPrice = bundle.getFloat(GOODS_ORIGINAL_PRICE);

            goodsCountTv.setText("总共"+goodsCount+"件商品");
            goodsPriceTv.setText("￥"+Utils.numberFormat(goodsOriginalPrice / 100));
            try {
                goodsDiscountTv.setText("￥"+Utils.numberFormat(Float.parseFloat(orderInfo.getCashFee()) / 100));
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
                codeNameTv.setText("实收：");
            } else {
                otherPayLayout.setVisibility(View.VISIBLE);
                payTypeTv.setText(Util.getPayType(payType));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cancelLastViewFocus(View view) {
        if (focusView != null) {
            if (focusView != view) {
                focusView.setSelected(false);
                focusView = view;
            }
        } else {
            focusView = view;
        }
    }

    private void pay() {
//        测试代码
//        EventBus.getDefault().post(new PrintDataEvent(orderInfo.getOrderId(), Util.getCurrData(), getPayType(payType)));
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
        loadingView.setVisibility(View.VISIBLE);
        if (payType != CASH_PAYT_TYPE && TextUtils.isEmpty(authoCode)) {
            Toast.makeText(PayingActivity.this, "请输入或扫描付款码", Toast.LENGTH_SHORT).show();
            return;
        }

        authoCode = authoCode == null ? "" : authoCode;
        Disposable disposable = Observable.create(new ObservableOnSubscribe<PayInfo>() {
            @Override
            public void subscribe(ObservableEmitter<PayInfo> e) throws Exception {
                Result<PayInfo> result = ServerManager.pay(Util.appId, orderInfo.getOrderId(), payType, authoCode,  2);
                if (result != null && result.getErrno() == 0 && result.getResult() != null) {
                    e.onNext(result.getResult());
                } else if (result != null && !TextUtils.isEmpty(result.getError())) {
                    gotoNextActivity(result.getError());
                    e.onError(new Throwable());
                } else {
                    gotoNextActivity("支付失败");
                    e.onError(new Throwable());
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<PayInfo>() {
                    @Override
                    public void onNext(PayInfo payInfo) {
                        if ("2".equals(payInfo.getStatus())) {
                            checkComplete("");
                            EventBus.getDefault().post(new PrintDataEvent(orderInfo.getOrderId(), Util.getCurrData(), Util.getPayType(payType)));
                        } else if ("6".equals(payInfo.getStatus())) {
                            checkPayStatus();
                        } else {
                            onError(new Throwable());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        loadingView.setVisibility(View.GONE);;
                        if (e != null && e.getMessage() != null) {
                            Toast.makeText(PayingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        compositeDisposable.add(disposable);
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
                                } else if (result != null && !TextUtils.isEmpty(result.getError())) {
                                    gotoNextActivity(result.getError());
                                    e.onError(new Throwable());
                                } else {
                                    gotoNextActivity("");
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
                        if (payCheckInfo != null && "2".equals(payCheckInfo.getPaystatus())) {
                            compositeDisposable.remove(this);
                            checkComplete("");
                            EventBus.getDefault().post(new PrintDataEvent(orderInfo.getOrderId(), Util.getCurrData(), Util.getPayType(payType)));
                        } else if (System.currentTimeMillis() - currTime > 60 * 1000){
                            compositeDisposable.remove(this);
                            checkComplete("支付失败");
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        compositeDisposable.remove(this);
                        checkComplete("支付失败");
                        if (e != null && e.getMessage() != null) {
                            Toast.makeText(PayingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        compositeDisposable.add(disposable);

    }

    private void checkComplete(String errorMsg) {
        loadingView.setVisibility(View.GONE);
        gotoNextActivity(errorMsg);
    }

    private void gotoNextActivity(String errorMsg) {
        Intent intent = new Intent(this, PayResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(PayingActivity.ORDER_RESULT, orderInfo);
        bundle.putString(PayingActivity.GOODS_COUNT, String.valueOf(goodsCount));
        bundle.putFloat(PayingActivity.GOODS_ORIGINAL_PRICE, goodsOriginalPrice);
        bundle.putInt(PayingActivity.GOODS_PAY_TYPE, payType);
        bundle.putString(PayResultActivity.ERROR_MSG, errorMsg);
        intent.putExtras(bundle);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_pay:
                payTv.setSelected(true);
                cancelLastViewFocus(payTv);
                if (payType == CASH_PAYT_TYPE) {
                    if (cashReturn < 0) {
                        Toast.makeText(this, "实收金额不足", Toast.LENGTH_SHORT).show();
                    } else {
                        pay();
                    }
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

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        final StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        switch (action) {
            case KeyEvent.ACTION_DOWN:
                int unicodeChar = event.getUnicodeChar();
                sb.append((char) unicodeChar);
                if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
                    return super.dispatchKeyEvent(event);
                }
                if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
                    return super.dispatchKeyEvent(event);
                }
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    return super.dispatchKeyEvent(event);
                }
                if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
                    return super.dispatchKeyEvent(event);
                }
                if (event.getKeyCode() == KeyEvent.KEYCODE_HOME) {
                    return super.dispatchKeyEvent(event);
                }
                if (event.getKeyCode() == KeyEvent.KEYCODE_POWER) {
                    return super.dispatchKeyEvent(event);
                }
                final int len = sb.length();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (len != sb.length()) return;
                        if (sb.length() > 0) {
                            scanCodeToPay(sb.toString());
                            sb.setLength(0);
                        }
                    }
                }, 200);
                return true;
            default:
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    private void scanCodeToPay(String code) {
        code = code.trim();
        if (!TextUtils.isEmpty(code)) {
            authoCode = code;
            payCodeEt.setText(code);
            if (payType == ALI_PAY_TYPE) {
                if (!TextUtils.isEmpty(authoCode) && authoCode.startsWith("26")
                        && authoCode.length() == 18) {
                    pay();
                } else {
                    Toast.makeText(PayingActivity.this, "不是支付宝付款码，请重新扫描", Toast.LENGTH_SHORT).show();
                }
            } else if (payType == WX_PAY_TYPE) {
                if (!TextUtils.isEmpty(authoCode) && authoCode.startsWith("13")
                        && authoCode.length() == 18) {
                    pay();
                } else {
                    Toast.makeText(PayingActivity.this, "不是微信付款码，请重新扫描", Toast.LENGTH_SHORT).show();
                }
            } else if (payType == MEMBER_PAYT_TYPE) {
                if (!TextUtils.isEmpty(authoCode) && authoCode.startsWith("11")) {
                    pay();
                } else {
                    Toast.makeText(PayingActivity.this, "不是会员卡付款码，请重新扫描", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void payCodeEvent(PayCodeEvent event) {
        scanCodeToPay(event.payCode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        PreferenceUtil.putString(PayingActivity.this, PreferenceUtil.KEY.PAYING_TYPE, "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            setResult(-1);
            finish();
        }
    }
}
