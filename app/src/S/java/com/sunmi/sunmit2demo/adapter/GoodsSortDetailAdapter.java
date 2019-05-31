package com.sunmi.sunmit2demo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sunmi.sunmit2demo.Constants;
import com.sunmi.sunmit2demo.R;
import com.sunmi.sunmit2demo.eventbus.GoodsItemClickEvent;
import com.sunmi.sunmit2demo.modle.GoodsInfo;
import com.sunmi.sunmit2demo.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * author : chrc
 * date   : 2019/5/2  1:01 PM
 * desc   :
 */
public class GoodsSortDetailAdapter extends RecyclerView.Adapter {

    Context context;
    List<GoodsInfo> datas;

    public GoodsSortDetailAdapter(Context context, List<GoodsInfo> datas) {
        this.datas = datas;
        this.context = context;
    }

    public void setData(List<GoodsInfo> datas) {
        if (datas == null || datas.size()== 0) return;
        if (this.datas == null) {
            this.datas = new ArrayList<>();
        } else {
            this.datas.clear();
        }
        this.datas.addAll(datas);
    }

    public List<GoodsInfo> getDatas() {
        return datas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_goods_sort, parent, false);
        GoodsSortViewHolder viewHolder = new GoodsSortViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        GoodsSortViewHolder viewHolder = (GoodsSortViewHolder) holder;
//        viewHolder.goodsCoverIv
        final GoodsInfo info = datas.get(position);
        if (!TextUtils.isEmpty(info.getGoodsName())) {
            viewHolder.goodsNameTv.setText(info.getGoodsName());
        }
        String weight = (info.getWeight() > 0 && !TextUtils.isEmpty(info.getUnit())) ? "/" + info.getWeight() + info.getUnit() : "";
        String price = "￥"+ (Utils.numberFormat(info.getPrice() * 1.0f / 100)) + weight;
        if (!TextUtils.isEmpty(price)) {
            viewHolder.goodsPriceTv.setText(price);
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (info.getPriceType() == Constants.WEIGHT_PRICE_TYPE) {
                    Toast.makeText(context, "称重商品只能通过扫描商品条形码才能加入已购列表", Toast.LENGTH_SHORT).show();
                } else {
                    EventBus.getDefault().post(
                            new GoodsItemClickEvent(info.getGoodsName(), info.getPrice(), "/"+info.getWeight()+info.getUnit(), info.getGoodsCode(), info.getPriceType()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    class GoodsSortViewHolder extends RecyclerView.ViewHolder {

        public ImageView goodsCoverIv;
        public TextView goodsNameTv, goodsPriceTv;

        public GoodsSortViewHolder(View itemView) {
            super(itemView);

            goodsCoverIv = itemView.findViewById(R.id.iv_goods_cover);
            goodsNameTv = itemView.findViewById(R.id.tv_goods_name);
            goodsPriceTv = itemView.findViewById(R.id.tv_goods_price);
        }
    }
}
