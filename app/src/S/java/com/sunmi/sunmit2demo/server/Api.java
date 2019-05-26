package com.sunmi.sunmit2demo.server;

/**
 * author : chrc
 * date   : 2019/5/5  8:33 PM
 * desc   :
 */
public interface Api {

    String HOST = "http://47.92.54.128:9801";

    String HOME_CLASS_GOODS_LIST = HOST + "/GoodsInfo/GoodsInfo/getClassGoodsList";

    String CREATE_ORDER = HOST + "/Order/Order/createOrderFromMc";

    String PAY = HOST + "/Order/Order/Pay";

    String PAY_CHECK = HOST + "/Order/Order/payCheck";
}
