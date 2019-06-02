package com.sunmi.sunmit2demo.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

    private static final int CHOOSE_PAY = 1;
    private TextView goodsCountTv, goodsPriceTv, goodsDiscountTv;
    private TextView cashPayTv, memberPayTv, wxPayTv, aliPayTv;
    private TextView nextTv;

    private View focusView;

    private int payType;
    private OrderInfo orderInfo;

    private String authoData;
    private String goodsCount;
    private float goodsOriginalPrice;

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
            authoData = bundle.getString(PayingActivity.GOODS_AUTHO_DATA);
            orderInfo = (OrderInfo) bundle.getSerializable(PayingActivity.ORDER_RESULT);
            goodsCount = bundle.getString(PayingActivity.GOODS_COUNT);
            goodsOriginalPrice = bundle.getFloat(PayingActivity.GOODS_ORIGINAL_PRICE);


            goodsCountTv.setText("总共"+goodsCount+"件商品");
            goodsPriceTv.setText("￥"+Utils.numberFormat(goodsOriginalPrice / 100));
            try {
                goodsDiscountTv.setText("￥"+Utils.numberFormat(Float.parseFloat(orderInfo.getCashFee()) / 100));
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
                payType = PayingActivity.CASH_PAYT_TYPE;
                break;
            case R.id.tv_member_pay:
                memberPayTv.setSelected(true);
                cancelLastViewFocus(memberPayTv);
                payType = PayingActivity.MEMBER_PAYT_TYPE;
                break;
            case R.id.tv_wx_pay:
                wxPayTv.setSelected(true);
                cancelLastViewFocus(wxPayTv);
                payType = PayingActivity.WX_PAY_TYPE;
                break;
            case R.id.tv_ali_pay:
                aliPayTv.setSelected(true);
                cancelLastViewFocus(aliPayTv);
                payType = PayingActivity.ALI_PAY_TYPE;
                break;
            case R.id.tv_next:
                if (orderInfo != null) {
                    Intent intent = new Intent(this, PayingActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(PayingActivity.ORDER_RESULT, orderInfo);
                    bundle.putString(PayingActivity.GOODS_COUNT, String.valueOf(goodsCount));
                    bundle.putFloat(PayingActivity.GOODS_ORIGINAL_PRICE, goodsOriginalPrice);
                    bundle.putString(PayingActivity.GOODS_AUTHO_DATA, authoData);
                    bundle.putInt(PayingActivity.GOODS_PAY_TYPE, payType);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, CHOOSE_PAY);
                } else {
                    Toast.makeText(this, "订单生成失败，请稍后再试", Toast.LENGTH_SHORT).show();
                }
                break;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_PAY && resultCode == RESULT_OK) {
            finish();
        }
    }
}
