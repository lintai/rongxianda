package com.sunmi.sunmit2demo.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sunmi.sunmit2demo.R;
import com.sunmi.sunmit2demo.modle.GoodsSortTagModle;

import java.util.List;

/**
 * author : chrc
 * date   : 2019/5/2  1:01 PM
 * desc   :
 */
public class GoodsSortAdapter extends RecyclerView.Adapter {

    List<GoodsSortTagModle> datas;
    ItemClickListenr itemClickListenr;
    int selectPos;

    public GoodsSortAdapter(List<GoodsSortTagModle> datas) {
        this.datas = datas;
    }

    public void setSelectPos(int selectPos) {
        this.selectPos = selectPos;
    }

    public List<GoodsSortTagModle> getDatas() {
        return datas;
    }

    public void setOnItemClickListener(ItemClickListenr itemClickListen) {
        this.itemClickListenr = itemClickListen;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_goods_sort_tag, parent, false);
        GoodsSortViewHolder viewHolder = new GoodsSortViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final GoodsSortViewHolder viewHolder = (GoodsSortViewHolder) holder;
        final GoodsSortTagModle modle = datas.get(position);
        viewHolder.goodsSortTv.setText(modle.getClassName());
        if (selectPos == position) {
            viewHolder.goodsSortTv.setTextColor(Color.parseColor("#bf333333"));
            viewHolder.goodsSortTv.setBackgroundResource(R.drawable.goods_sort_item_select_rectangle_bg);
        } else {
            viewHolder.goodsSortTv.setTextColor(viewHolder.goodsSortTv.getContext().getResources().getColorStateList(R.color.goods_sort_item_textcolor_selector));
            viewHolder.goodsSortTv.setBackgroundResource(R.drawable.goods_sort_item_selector);
        }
        if (itemClickListenr != null) {
            viewHolder.goodsSortTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectPos != position) {
                        selectPos = position;
                        notifyDataSetChanged();
                        itemClickListenr.onItemClick(modle.getClassId());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    class GoodsSortViewHolder extends RecyclerView.ViewHolder {

        public TextView goodsSortTv;

        public GoodsSortViewHolder(View itemView) {
            super(itemView);
            goodsSortTv = itemView.findViewById(R.id.tv_goods_sort_name);
        }
    }

    public interface ItemClickListenr {
        void onItemClick(long classId);
    }
}
