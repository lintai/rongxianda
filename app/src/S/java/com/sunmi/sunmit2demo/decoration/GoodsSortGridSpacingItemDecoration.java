package com.sunmi.sunmit2demo.decoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * author : chrc
 * date   : 2019/5/2  8:07 PM
 * desc   :
 */
public class GoodsSortGridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount; //列数
    private int spacing; //间隔
    private boolean includeEdge; //是否包含边缘

    public GoodsSortGridSpacingItemDecoration(int spanCount, int spacing) {
        this.spanCount = spanCount;
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        //这里是关键，需要根据你有几列来判断
        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % spanCount; // item column


//        if (column != spanCount - 1) {
            //最后一列 才不需要需要左边距
            outRect.right = spacing;
//        }

        if (position >= spanCount) { // top edge
            //第一列不需要top间距
            outRect.top = spacing;
        }

    }
}
