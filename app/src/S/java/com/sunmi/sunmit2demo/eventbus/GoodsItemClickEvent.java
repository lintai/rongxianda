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
    public String goodsCode;//商品编码，称重物品则是plu吗
    public int priceType;//价格类型；1-计件 2-称重

    public float weight;//称重商品专用
    public float totalPrice;//称重商品专用

    public GoodsItemClickEvent(String goodsName, int price, String unit, String goodsCode, int priceType) {
        this(goodsName, price, unit, goodsCode, priceType, 0, 0);
    }

    public GoodsItemClickEvent(String goodsName, int price, String unit, String goodsCode, int priceType, float weight, float totalPrice) {
        this.goodsName = goodsName;
        this.price = price;
        this.unit = unit;
        this.goodsCode = goodsCode;
        this.priceType = priceType;
        this.weight = weight;
        this.totalPrice = totalPrice;
    }
}
