package com.sunmi.sunmit2demo.eventbus;

/**
 * author : chrc
 * date   : 2019/5/2  6:15 PM
 * desc   :
 */
public class GoodsItemClickEvent {

    public String goodsName;
    public int price;//单位(分)
    public String unit;//计量单位，比如38元/300g 中的/300g

    public GoodsItemClickEvent(String goodsName, int price, String unit) {
        this.goodsName = goodsName;
        this.price = price;
        this.unit = unit;
    }
}
