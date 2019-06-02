package com.sunmi.sunmit2demo;

import android.os.Environment;

/**
 * author : chrc
 * date   : 2019/5/19  6:34 PM
 * desc   :
 */
public interface Constants {

    int PIECE_PRICE_TYPE = 1;//价格类型：计件
    int WEIGHT_PRICE_TYPE = 2;//价格类型：称重

    String SHOPLOGO = Environment.getExternalStorageDirectory().getAbsolutePath() + "/rongxianda/logo.jpg";
}
