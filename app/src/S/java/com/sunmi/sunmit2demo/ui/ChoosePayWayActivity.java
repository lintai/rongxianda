package com.sunmi.sunmit2demo.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sunmi.sunmit2demo.R;
import com.sunmi.sunmit2demo.modle.OrderInfo;

public class ChoosePayWayActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String ORDER_RESULT = "order_result";
    public static final String GOODS_COUNT = "goods_count";
    public static final String GOODS_ORIGINAL_PRICE = "goods_original_price";

    private TextView goodsCountTv, goodsPriceTv, goodsDiscountTv;
    private TextView cashPayTv, memberPayTv, wxPayTv, aliPayTv;
    private TextView nextTv;

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
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        try {
            OrderInfo orderInfo = (OrderInfo) bundle.getSerializable(ORDER_RESULT);
            String goodsCount = bundle.getString(GOODS_COUNT);
            String goodsOriginalPrice = bundle.getString(GOODS_ORIGINAL_PRICE);

            goodsCountTv.setText(goodsCount);
            goodsPriceTv.setText(goodsOriginalPrice);
            goodsDiscountTv.setText(orderInfo.getCashFee());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cash_pay:
                break;
            case R.id.tv_member_pay:
                break;
            case R.id.tv_wx_pay:
                break;
            case R.id.tv_ali_pay:
                break;
            case R.id.tv_next:
                break;
        }
    }
}
