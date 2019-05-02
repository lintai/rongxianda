package com.sunmi.sunmit2demo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunmi.sunmit2demo.R;

import java.util.List;

/**
 * author : chrc
 * date   : 2019/5/2  1:01 PM
 * desc   :
 */
public class GoodsSortDetailAdapter extends RecyclerView.Adapter {

    List<String> datas;

    public GoodsSortDetailAdapter(List<String> datas) {
        this.datas = datas;
    }

    public List<String> getDatas() {
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
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    class GoodsSortViewHolder extends RecyclerView.ViewHolder {

        private ImageView goodsCoverIv;
        private TextView goodsNameTv, goodsPriceTv;

        public GoodsSortViewHolder(View itemView) {
            super(itemView);

            goodsCoverIv = itemView.findViewById(R.id.iv_goods_cover);
            goodsNameTv = itemView.findViewById(R.id.tv_goods_name);
            goodsPriceTv = itemView.findViewById(R.id.tv_goods_price);
        }
    }
}
