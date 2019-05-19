package com.sunmi.sunmit2demo.modle;

/**
 * author : chrc
 * date   : 2019/5/2  3:49 PM
 * desc   :
 */
public class GoodsInfo extends BaseModle{

    private String goodsCode;   //商品编号
    private String goodsName;   //商品名称
    private long classId;       //分类id
    private String classNme;    //分类名称
    private int price;          //价格(分)
    private int weight;         //商品规格
    private int num;            //库存
    private int priceType;      //价格类型：1-计件 2-称重
    private String unit;        //规格单位：g或者ml，与weight组成类似300g或者300ml作为完整规格描述
    private int plu;            //plu吗，用于对接条码秤
    private String faceImg;     //商品封面图片
    private String imgUrls;     //其他图片

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public long getClassId() {
        return classId;
    }

    public void setClassId(long classId) {
        this.classId = classId;
    }

    public String getClassNme() {
        return classNme;
    }

    public void setClassNme(String classNme) {
        this.classNme = classNme;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getPriceType() {
        return priceType;
    }

    public void setPriceType(int priceType) {
        this.priceType = priceType;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getPlu() {
        return plu;
    }

    public void setPlu(int plu) {
        this.plu = plu;
    }

    public String getFaceImg() {
        return faceImg;
    }

    public void setFaceImg(String faceImg) {
        this.faceImg = faceImg;
    }

    public String getImgUrls() {
        return imgUrls;
    }

    public void setImgUrls(String imgUrls) {
        this.imgUrls = imgUrls;
    }
}
