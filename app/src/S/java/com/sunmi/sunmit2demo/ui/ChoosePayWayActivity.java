package com.sunmi.sunmit2demo.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sunmi.sunmit2demo.R;
import com.sunmi.sunmit2demo.Util;
import com.sunmi.sunmit2demo.eventbus.PrintDataEvent;
import com.sunmi.sunmit2demo.modle.OrderInfo;
import com.sunmi.sunmit2demo.modle.PayInfo;
import com.sunmi.sunmit2demo.modle.Result;
import com.sunmi.sunmit2demo.server.ServerManager;
import com.sunmi.sunmit2demo.utils.Utils;

import org.greenrobot.eventbus.EventBus;

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
    private TextView nextTv, preTv;
    private LinearLayout cashPayLayout, vipPayLayout, wxPayLayout, aliPayLayout;
    private ImageView cashPayIv, vipPayIv, wxPayIv, aliPayIv;

    private View focusView;

    private View funFocusView;

    private int payType;
    private OrderInfo orderInfo;

    private String authoData;
    private String goodsCount;
    private double goodsOriginalPrice;

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
        preTv = findViewById(R.id.tv_pre);

        cashPayLayout = findViewById(R.id.layout_cash_pay);
        vipPayLayout = findViewById(R.id.layout_vip_pay);
        wxPayLayout = findViewById(R.id.layout_wx_pay);
        aliPayLayout = findViewById(R.id.layout_ali_pay);

        cashPayIv = findViewById(R.id.iv_cash_pay);
        vipPayIv = findViewById(R.id.iv_vip_pay);
        wxPayIv = findViewById(R.id.iv_wx_pay);
        aliPayIv = findViewById(R.id.iv_ali_pay);

        cashPayLayout.setOnClickListener(this);
        vipPayLayout.setOnClickListener(this);
        wxPayLayout.setOnClickListener(this);
        aliPayLayout.setOnClickListener(this);
        nextTv.setOnClickListener(this);
        preTv.setOnClickListener(this);

        nextTv.setSelected(true);
        cancelLastFunViewFocus(nextTv);

        cashPayLayout.setSelected(true);
        cancelLastViewFocus(cashPayLayout);
        payType = 4;
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        try {
            authoData = bundle.getString(PayingActivity.GOODS_AUTHO_DATA);
            orderInfo = (OrderInfo) bundle.getSerializable(PayingActivity.ORDER_RESULT);
            goodsCount = bundle.getString(PayingActivity.GOODS_COUNT);
            goodsOriginalPrice = bundle.getDouble(PayingActivity.GOODS_ORIGINAL_PRICE);


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
            case R.id.layout_cash_pay:
                cashPayLayout.setSelected(true);
//                cashPayTv.setSelected(true);
                cancelLastViewFocus(cashPayLayout);
                payType = PayingActivity.CASH_PAYT_TYPE;
                break;
            case R.id.layout_vip_pay:
                vipPayLayout.setSelected(true);
                cancelLastViewFocus(vipPayLayout);
                payType = PayingActivity.MEMBER_PAYT_TYPE;
                break;
            case R.id.layout_wx_pay:
                wxPayLayout.setSelected(true);
                cancelLastViewFocus(wxPayLayout);
                payType = PayingActivity.WX_PAY_TYPE;
                break;
            case R.id.layout_ali_pay:
                aliPayLayout.setSelected(true);
                cancelLastViewFocus(aliPayLayout);
                payType = PayingActivity.ALI_PAY_TYPE;
                break;
            case R.id.tv_next:
                nextTv.setSelected(true);
                cancelLastFunViewFocus(nextTv);
                if (payType == PayingActivity.CASH_PAYT_TYPE) {
                    EventBus.getDefault().post(new PrintDataEvent(true));
                }
                if (orderInfo != null) {
                    Intent intent = new Intent(this, PayingActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(PayingActivity.ORDER_RESULT, orderInfo);
                    bundle.putString(PayingActivity.GOODS_COUNT, String.valueOf(goodsCount));
                    bundle.putDouble(PayingActivity.GOODS_ORIGINAL_PRICE, goodsOriginalPrice);
                    bundle.putString(PayingActivity.GOODS_AUTHO_DATA, authoData);
                    bundle.putInt(PayingActivity.GOODS_PAY_TYPE, payType);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, CHOOSE_PAY);
                } else {
                    Toast.makeText(this, "订单生成失败，请稍后再试", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_pre:
                preTv.setSelected(true);
                cancelLastFunViewFocus(preTv);
                finish();
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
    private void cancelLastFunViewFocus(View view) {
        if (funFocusView != null) {
            if (funFocusView != view) {
                funFocusView.setSelected(false);
                funFocusView = view;
            }
        } else {
            funFocusView = view;
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
