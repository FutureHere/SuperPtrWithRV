package com.leinyo.superptrwithrv.widget.ptr.loadmore;

public interface LoadMoreUIHandler {

    public void onLoading(LoadMoreContainer container);

    public void onLoadFinish(LoadMoreContainer container, boolean hasMore);

    public void onWaitToLoadMore(LoadMoreContainer container);

    public void onLoadError(LoadMoreContainer container, int errorCode, String errorMessage);

    public int getStatus();
}