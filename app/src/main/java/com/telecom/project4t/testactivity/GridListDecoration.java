package com.telecom.project4t.testactivity;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class GridListDecoration extends RecyclerView.ItemDecoration {

    int largePadding;
    int smallPadding;

    public GridListDecoration(int largePadding, int smallPadding) {
        this.largePadding = largePadding;
        this.smallPadding = smallPadding;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = smallPadding;
        outRect.right = smallPadding;
        outRect.top = largePadding;
        outRect.bottom = largePadding;
    }

}
