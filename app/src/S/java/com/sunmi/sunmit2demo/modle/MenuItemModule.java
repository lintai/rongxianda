package com.sunmi.sunmit2demo.modle;

/**
 * author : chrc
 * date   : 2019/5/2  6:17 PM
 * desc   :
 */
public class MenuItemModule extends BaseModle {

    String goodsName;
    int price;//单位(分)
    String unit;//计量单位，比如38元/300g 中的 /300g
    String goodsCode;//商品编码
    int priceType;//价格类型；1-计件 2-称重

    float goodsCount;//数量

    public MenuItemModule() {

    }

    public MenuItemModule(String goodsName, int price, String unit, String goodsCode, int priceType) {
        this.goodsName = goodsName;
        this.price = price;
        this.unit = unit;
        this.goodsCode = goodsCode;
        this.priceType = priceType;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public float getGoodsCount() {
        return goodsCount;
    }

    public void setGoodsCount(float goodsCount) {
        this.goodsCount = goodsCount;
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    public int getPriceType() {
        return priceType;
    }

    public void setPriceType(int priceType) {
        this.priceType = priceType;
    }
}
