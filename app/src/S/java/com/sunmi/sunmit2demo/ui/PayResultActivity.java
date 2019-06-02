package com.sunmi.sunmit2demo.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.sunmi.sunmit2demo.R;
import com.sunmi.sunmit2demo.Util;
import com.sunmi.sunmit2demo.eventbus.PrintDataEvent;
import com.sunmi.sunmit2demo.modle.OrderInfo;
import com.sunmi.sunmit2demo.utils.Utils;

import org.greenrobot.eventbus.EventBus;

public class PayResultActivity extends AppCompatActivity {

    public static final String ERROR_MSG = "errorMsg";

    private TextView goodsCountTv, goodsPriceTv, goodsDiscountTv, payTypeTv;
    private TextView payTv, resultTipTv;

    private OrderInfo orderInfo;
    private int payType;

    private float goodsOriginalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_result);
        initView();
        initData();
    }

    private void initView() {
        goodsCountTv = findViewById(R.id.tv_goods_count);
        goodsPriceTv = findViewById(R.id.tv_original_price);
        goodsDiscountTv = findViewById(R.id.tv_discount_price);
        payTv = findViewById(R.id.tv_pay);
        payTypeTv = findViewById(R.id.tv_pay_type);
        resultTipTv = findViewById(R.id.tv_pay_tip);


        payTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new PrintDataEvent(orderInfo.getOrderId(), Util.getCurrData(), Util.getPayType(payType)));
                setResult(-1);
                finish();
            }
        });

    }

    private void initData() {

        Bundle bundle = getIntent().getExtras();
        try {
            orderInfo = (OrderInfo) bundle.getSerializable(PayingActivity.ORDER_RESULT);

            String goodsCount = bundle.getString(PayingActivity.GOODS_COUNT);
            goodsOriginalPrice = bundle.getFloat(PayingActivity.GOODS_ORIGINAL_PRICE);

            goodsCountTv.setText("总共"+goodsCount+"件商品");
            goodsPriceTv.setText("￥"+Utils.numberFormat(goodsOriginalPrice / 100));
            try {
                goodsDiscountTv.setText("￥"+Utils.numberFormat(Float.parseFloat(orderInfo.getCashFee()) / 100));
            } catch (NumberFormatException e) {
                goodsDiscountTv.setText("价格返回错误");
                e.printStackTrace();
            }

            String errorMsg = bundle.getString(ERROR_MSG);
            if (!TextUtils.isEmpty(errorMsg)) {
                resultTipTv.setText("支付失败");
                payTypeTv.setText(errorMsg);
            } else {
                payType = bundle.getInt(PayingActivity.GOODS_PAY_TYPE);
                payTypeTv.setText("支付方式："+Util.getPayType(payType));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
