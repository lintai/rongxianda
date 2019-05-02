package com.sunmi.sunmit2demo.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sunmi.sunmit2demo.fragment.HomeGoodsSortFragment;

import java.util.List;

/**
 * author : chrc
 * date   : 2019/5/2  2:18 PM
 * desc   :
 */
public class HomeGoodsViewPagerAdapter extends FragmentPagerAdapter {

    List<String> datas;

    public HomeGoodsViewPagerAdapter(FragmentManager fm, List<String> datas) {
        super(fm);
        this.datas = datas;
    }

    @Override
    public Fragment getItem(int position) {
        if (datas == null || datas.size() == 0) return null;
        String data = datas.get(position);
        Bundle bundle = new Bundle();
        bundle.putString(HomeGoodsSortFragment.DATA, data);
        return HomeGoodsSortFragment.createFragment(bundle);
    }

    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size();
    }
}
