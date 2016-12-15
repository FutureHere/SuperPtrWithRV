package com.leinyo.superptrwithrv.widget.ptr.loadmore;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

public class LoadMoreRecyclerViewContainer extends LoadMoreContainerBase {

    private RecyclerView mRecyclerView;

    public LoadMoreRecyclerViewContainer(Context context) {
        super(context);
    }

    public LoadMoreRecyclerViewContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void removeFooterView(View view) {
        mRecyclerView.removeView(view);
    }

    @Override
    protected RecyclerView retrieveAbsListView() {
        mRecyclerView = (RecyclerView) findViewById(android.R.id.list);
        return mRecyclerView;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }
}
