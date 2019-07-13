package com.sunmi.sunmit2demo.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunmi.sunmit2demo.Constants;
import com.sunmi.sunmit2demo.R;
import com.sunmi.sunmit2demo.Util;
import com.sunmi.sunmit2demo.modle.MenuItemModule;
import com.sunmi.sunmit2demo.utils.Utils;

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
    private double goodsTotalPrice;

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

    public double getGoodsTotalPrice() {
        return goodsTotalPrice;
    }

    public void addGoodsCount(int goodsCount) {
        this.goodsCount += goodsCount;
    }

    public void clear() {
        this.goodsCount = 0;
        this.goodsTotalPrice = 0;
        this.datas.clear();
        notifyDataSetChanged();
    }

    public void addGoodsTotalPrice(double goodsPrice) {
        this.goodsTotalPrice += goodsPrice;
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder.getItemViewType() == TYPE_NORMAL) {
            MenuViewHolder menuViewHolder = (MenuViewHolder) holder;
            final MenuItemModule module = datas.get(position);

            if (!TextUtils.isEmpty(module.getGoodsName())) {
                menuViewHolder.mGoodsNameTv.setText(module.getGoodsName());
            }
            menuViewHolder.mSingleGoodsPriceTv.setText("￥"+ Utils.numberFormat(module.getPrice() * 1.0f / 100) + module.getUnit());
            if (module.getPriceType() != Constants.WEIGHT_PRICE_TYPE) {
                menuViewHolder.mGoodsCountTv.setText(Utils.numberFormat(module.getGoodsCount()));
                menuViewHolder.mGoodsTotalPrice.setText("￥"+Utils.numberFormat(module.getPrice() * module.getGoodsCount() * 1.0f / 100));
            } else {
                menuViewHolder.mGoodsCountTv.setText(Utils.numberFormat(module.getGoodsCount()) + "g");
                menuViewHolder.mGoodsTotalPrice.setText("￥"+Utils.numberFormat(module.getTotalPrice() * 1.0f / 100));
            }

            //称重的商品无法通过“+”或“-”来增减商品数量
            if (module.getPriceType() != Constants.WEIGHT_PRICE_TYPE) {
                menuViewHolder.addLl.setVisibility(View.VISIBLE);
                menuViewHolder.addLl.setOnClickListener(new View.OnClickListener() {
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
            } else {
                menuViewHolder.addLl.setVisibility(View.GONE);
            }

            if (module.getPriceType() != Constants.WEIGHT_PRICE_TYPE) {
                menuViewHolder.deleaseLl.setVisibility(View.VISIBLE);
                menuViewHolder.deleaseLl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (module.getGoodsCount() <= 0) return;
                        if (goodsCount > 0) {
                            goodsCount--;
                        }
                        goodsTotalPrice -= module.getPrice();
                        goodsTotalPrice = goodsTotalPrice < 0 ? 0 : goodsTotalPrice;

                        module.setGoodsCount(module.getGoodsCount() - 1);
                        if (module.getGoodsCount() == 0) {
                            datas.remove(position);
                            notifyDataSetChanged();
                        } else {
                            notifyItemChanged(position);
                        }
                        if (changeListener != null) {
                            changeListener.change(CHANGE_TYPE_DELEASE, module.getPrice());
                        }
                    }
                });
            } else {
                menuViewHolder.deleaseLl.setVisibility(View.GONE);
            }

            menuViewHolder.mDeleteIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (module.getPriceType()  == Constants.WEIGHT_PRICE_TYPE) {
                        goodsCount = goodsCount  - 1;
                        goodsTotalPrice -= module.getTotalPrice();
                    } else {
                        goodsCount = (int) (goodsCount - module.getGoodsCount());
                        goodsTotalPrice -= module.getGoodsCount() * module.getPrice();
                    }

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
        private LinearLayout deleaseLl, addLl;

        public MenuViewHolder(View itemView) {
            super(itemView);
            mGoodsNameTv = itemView.findViewById(R.id.tv_title);
            mSingleGoodsPriceTv = itemView.findViewById(R.id.tv_single_price);
            mGoodsTotalPrice = itemView.findViewById(R.id.tv_totle_price);
            mGoodsCountTv = itemView.findViewById(R.id.tv_goods_count);
            mdeleaseGoodsIv = itemView.findViewById(R.id.iv_delease);
            deleaseLl = itemView.findViewById(R.id.ll_delease);
            addLl = itemView.findViewById(R.id.ll_add);
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
