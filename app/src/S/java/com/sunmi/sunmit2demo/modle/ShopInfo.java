package com.sunmi.sunmit2demo.modle;

/**
 * author : chrc
 * date   : 2019/5/31  10:47 PM
 * desc   :
 */
public class ShopInfo extends BaseModle {

    private String shopname;

    private String phone;

    private String qrcode;

    private String adress;

    public ShopInfo(String shopname, String phone, String qrcode, String adress) {
        this.shopname = shopname;
        this.phone = phone;
        this.qrcode = qrcode;
        this.adress = adress;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }
}
