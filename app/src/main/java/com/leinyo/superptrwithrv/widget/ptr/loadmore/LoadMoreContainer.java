package com.leinyo.superptrwithrv.widget.ptr.loadmore;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public interface LoadMoreContainer {

    public void setShowLoadingForFirstPage(boolean showLoading);

    public void setAutoLoadMore(boolean autoLoadMore);

    public void setOnScrollListener(RecyclerView.OnScrollListener l);

    public void setLoadMoreView(View view);

    public void setLoadMoreUIHandler(LoadMoreUIHandler handler);

    public void setLoadMoreHandler(LoadMoreHandler handler);

    public void removeLoadMoreHandler();

    /**
     * When data has loaded
     *
     * @param hasMore
     */
    public void loadMoreFinish(boolean hasMore);

    /**
     * When something unexpected happened while loading the data
     *
     * @param errorCode
     * @param errorMessage
     */
    public void loadMoreError(int errorCode, String errorMessage);
}
