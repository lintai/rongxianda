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
 * date   : 2019/5/2  1:00 PM
 * desc   :
 */
public class HomeMenuAdapter extends RecyclerView.Adapter {

    private final int TYPE_NORMAL = 0;
    private final int TYPE_EMPTY = 1;

    List<String> datas;

    public HomeMenuAdapter(List<String> datas) {
        this.datas = datas;
    }

    public List<String> getDatas() {
        return datas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        if (viewType == TYPE_EMPTY) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_goods_menu_empty, parent, false);
            viewHolder = new MenuEmptyViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_goods_menu, parent, false);
            viewHolder = new MenuViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_NORMAL) {
            MenuViewHolder menuViewHolder = (MenuViewHolder) holder;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (datas == null || datas.size() == 0) {
            return TYPE_EMPTY;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    class MenuViewHolder extends RecyclerView.ViewHolder {
        private TextView mGoodsNameTv, mSingleGoodsPriceTv, mGoodsTotalPrice;
        private TextView mGoodsCountTv;
        private ImageView mAddGoodsIv, mdeleaseGoodsIv, mDeleteIv;

        public MenuViewHolder(View itemView) {
            super(itemView);
            mGoodsNameTv = itemView.findViewById(R.id.tv_name);
            mSingleGoodsPriceTv = itemView.findViewById(R.id.tv_single_price);
            mGoodsTotalPrice = itemView.findViewById(R.id.tv_totle_price);
            mGoodsCountTv = itemView.findViewById(R.id.tv_goods_count);
            mdeleaseGoodsIv = itemView.findViewById(R.id.iv_delease);
            mDeleteIv = itemView.findViewById(R.id.iv_delete);
            mAddGoodsIv = itemView.findViewById(R.id.iv_add);


        }
    }

    class MenuEmptyViewHolder extends RecyclerView.ViewHolder {

        public MenuEmptyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
