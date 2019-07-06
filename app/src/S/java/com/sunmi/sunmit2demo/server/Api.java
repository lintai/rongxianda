package com.sunmi.sunmit2demo.server;

/**
 * author : chrc
 * date   : 2019/5/5  8:33 PM
 * desc   :
 */
public interface Api {

//    String HOST = "http://47.92.54.128:9801";
//    String HOST = "https://rxd.ponlok.com";//正式环境
    String HOST = "https://testapi.ponlok.com";//测试环境

    //获取所有商品信息
    String HOME_CLASS_GOODS_LIST = HOST + "/GoodsInfo/GoodsInfo/getClassGoodsList";

    //创建订单(收银台使用)
    String CREATE_ORDER = HOST + "/Order/Order/createOrderFromMc";

    //订单支付
    String PAY = HOST + "/Order/Order/Pay";

    //订单支付 支付状态查询
    String PAY_CHECK = HOST + "/Order/Order/payCheck";

    //获取店铺信息
    String SHOP_INFO = HOST + "/Settings/SettingInfo/getShopInfo";
}
