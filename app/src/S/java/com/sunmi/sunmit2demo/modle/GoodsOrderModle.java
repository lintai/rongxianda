package com.sunmi.sunmit2demo.modle;

/**
 * author : chrc
 * date   : 2019/5/7  10:08 PM
 * desc   :
 */
public class GoodsOrderModle extends BaseModle {

    String goodsCode;   //商品编码
    String num;         //商品数量/重量，重量用g为单位
    int price;          //物品单价，单位：分；称重物品视为单件物品

    public GoodsOrderModle(String goodsCode, String num, int price) {
        this.goodsCode = goodsCode;
        this.num = num;
        this.price = price;
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
