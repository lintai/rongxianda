package com.sunmi.sunmit2demo.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sunmi.sunmit2demo.BaseFragment;
import com.sunmi.sunmit2demo.R;
import com.sunmi.sunmit2demo.adapter.GoodsSortDetailAdapter;

import java.util.ArrayList;

/**
 * author : chrc
 * date   : 2019/5/2  2:21 PM
 * desc   :
 */
public class HomeGoodsSortFragment extends BaseFragment {

    public final static String DATA  = "data";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    public static HomeGoodsSortFragment createFragment(Bundle bundle) {
        HomeGoodsSortFragment fragment = new HomeGoodsSortFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int setView() {
        return R.layout.fragment_home_goods_sort;
    }

    @Override
    protected void init(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));

        mAdapter = new GoodsSortDetailAdapter(new ArrayList<String>());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }
}
