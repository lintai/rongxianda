package com.sunmi.sunmit2demo.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunmi.sunmit2demo.R;
import com.sunmi.sunmit2demo.modle.MenuItemModule;

import java.util.ArrayList;
import java.util.List;

/**
 * author : chrc
 * date   : 2019/5/2  1:00 PM
 * desc   :
 */
public class HomeMenuAdapter extends RecyclerView.Adapter {

    private final int TYPE_NORMAL = 0;
    private final int TYPE_EMPTY = 1;

    public static final int CHANGE_TYPE_ADD = 2;
    public static final int CHANGE_TYPE_DELEASE = 3;
    public static final int CHANGE_TYPE_DELETE = 4;

    private List<MenuItemModule> datas;
    private GoodsCountChangeListener changeListener;

    private int goodsCount;
    private float goodsTotalPrice;

    public HomeMenuAdapter(List<MenuItemModule> datas) {
        this.datas = datas;
    }

    public List<MenuItemModule> getDatas() {
        if (datas == null) {
            datas = new ArrayList<>();
        }
        return datas;
    }

    public void setChangeListener(GoodsCountChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    public int getGoodsCount() {
        return goodsCount;
    }

    public float getGoodsTotalPrice() {
        return goodsTotalPrice;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        goodsCount = 0;
        goodsTotalPrice = 0;
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder.getItemViewType() == TYPE_NORMAL) {
            MenuViewHolder menuViewHolder = (MenuViewHolder) holder;
            final MenuItemModule module = datas.get(position);
            if (!TextUtils.isEmpty(module.getGoodsName())) {
                menuViewHolder.mGoodsNameTv.setText(module.getGoodsName());
            }
            menuViewHolder.mSingleGoodsPriceTv.setText("￥"+module.getPrice() * 1.0f / 100 + module.getUnit());
            menuViewHolder.mGoodsCountTv.setText(String.valueOf(module.getGoodsCount()));
            menuViewHolder.mGoodsTotalPrice.setText("￥"+(module.getPrice() * module.getGoodsCount() * 1.0 / 100));

            menuViewHolder.mAddGoodsIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goodsCount++;
                    goodsTotalPrice += module.getPrice();
                    module.setGoodsCount(module.getGoodsCount() + 1);
                    notifyItemChanged(position);
                    if (changeListener != null) {
                        changeListener.change(CHANGE_TYPE_ADD, module.getPrice());
                    }
                }
            });
            menuViewHolder.mdeleaseGoodsIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (module.getGoodsCount() <= 0) return;
                    goodsCount--;
                    goodsTotalPrice -= module.getPrice();
                    module.setGoodsCount(module.getGoodsCount() - 1);
                    notifyItemChanged(position);
                    if (changeListener != null) {
                        changeListener.change(CHANGE_TYPE_DELEASE, module.getPrice());
                    }
                }
            });
            menuViewHolder.mDeleteIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goodsCount = goodsCount - module.getGoodsCount();
                    goodsTotalPrice -= module.getGoodsCount() * module.getPrice();
                    datas.remove(position);
                    notifyDataSetChanged();
                    if (changeListener != null) {
                        changeListener.change(CHANGE_TYPE_DELETE, module.getPrice());
                    }
                }
            });
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
            mGoodsNameTv = itemView.findViewById(R.id.tv_title);
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

    public interface GoodsCountChangeListener{
        /**
         *
         * @param type
         * @param money 变动的价格，单位：分(接口返回的单位是分)
         */
        void change(int type, float money);
    }
}
