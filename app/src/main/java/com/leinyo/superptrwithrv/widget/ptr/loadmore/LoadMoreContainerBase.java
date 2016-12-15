package com.leinyo.superptrwithrv.widget.ptr.loadmore;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * @author huqiu.lhq
 */
public abstract class LoadMoreContainerBase extends LinearLayout implements LoadMoreContainer {

    private RecyclerView.OnScrollListener mOnScrollListener;
    private LoadMoreUIHandler mLoadMoreUIHandler;
    private LoadMoreHandler mLoadMoreHandler;

    private boolean mIsLoading;
    private boolean mAutoLoadMore = true;
    private boolean mLoadError = false;

    private boolean mShowLoadingForFirstPage = false;
    public View mFooterView;

    private RecyclerView mRecyclerView;
    private boolean mHasMore = true;

    public LoadMoreContainerBase(Context context) {
        super(context);
    }

    public LoadMoreContainerBase(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mRecyclerView = retrieveAbsListView();
        init();
    }

    public void useDefaultFooter() {
        if (mFooterView == null && mLoadMoreUIHandler == null) {
            LoadMoreDefaultFooterView footerView = new LoadMoreDefaultFooterView(getContext());
            footerView.setVisibility(GONE);
            setLoadMoreView(footerView);
            setLoadMoreUIHandler(footerView);
        }
    }

    private boolean mIsEnd = false;

    private void init() {

        if (mFooterView != null) {
            addFooterView(mFooterView);
        }
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView view, int scrollState) {
                super.onScrollStateChanged(view, scrollState);
                if (null != mOnScrollListener) {
                    mOnScrollListener.onScrollStateChanged(view, scrollState);
                }
                if (scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (mIsEnd) {
                        onReachBottom();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (null != mOnScrollListener) {
                    mOnScrollListener.onScrolled(recyclerView, dx, dy);
                }
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();

                if (visibleItemCount > 0 && linearLayoutManager.findLastVisibleItemPosition() == totalItemCount - 1) {
                    mIsEnd = true;
                } else {
                    mIsEnd = false;
                }
            }
        });
    }

    private void tryToPerformLoadMore() {
        if (mIsLoading) {
            return;
        }

        if (!mHasMore) {
            if (mLoadMoreUIHandler != null) {
                ((LoadMoreDefaultFooterView) mLoadMoreUIHandler).setVisibility(VISIBLE);
                mLoadMoreUIHandler.onLoadFinish(this, false);
            }
            return;
        }

        mIsLoading = true;

        if (mLoadMoreUIHandler != null) {
            mLoadMoreUIHandler.onLoading(this);
        }
        if (null != mLoadMoreHandler) {
            mLoadMoreHandler.onLoadMore(this);
        }
    }

    private void onReachBottom() {
        // if has error, just leave what it should be
        if (null == mLoadMoreHandler) {
            return;
        }
        if (mLoadError) {
            return;
        }
        if (mAutoLoadMore) {
            tryToPerformLoadMore();
        } else {
            if (mHasMore) {
                mLoadMoreUIHandler.onWaitToLoadMore(this);
            }
        }
    }

    @Override
    public void setShowLoadingForFirstPage(boolean showLoading) {
        mShowLoadingForFirstPage = showLoading;
    }

    @Override
    public void setAutoLoadMore(boolean autoLoadMore) {
        mAutoLoadMore = autoLoadMore;
    }

    @Override
    public void setOnScrollListener(RecyclerView.OnScrollListener l) {
        mOnScrollListener = l;
    }

    @Override
    public void setLoadMoreView(View view) {
        // has not been initialized
        if (mRecyclerView == null) {
            mFooterView = view;
            return;
        }
        // remove previous
        if (mFooterView != null && mFooterView != view) {
            removeFooterView(view);
        }

        // add current
        mFooterView = view;
        mFooterView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                tryToPerformLoadMore();
            }
        });

        addFooterView(view);
    }

    @Override
    public void setLoadMoreUIHandler(LoadMoreUIHandler handler) {
        mLoadMoreUIHandler = handler;
    }

    @Override
    public void setLoadMoreHandler(LoadMoreHandler handler) {
        mLoadMoreHandler = handler;
    }

    /**
     * page has loaded
     *
     * @param hasMore
     */
    @Override
    public void loadMoreFinish(boolean hasMore) {
        mLoadError = false;
        mIsLoading = false;
        mHasMore = hasMore;
//        mIsEnd = false;
        if (mLoadMoreUIHandler != null) {
            mLoadMoreUIHandler.onLoadFinish(this, hasMore);
        }
    }

    @Override
    public void loadMoreError(int errorCode, String errorMessage) {
        mIsLoading = false;
        mLoadError = true;
        if (mLoadMoreUIHandler != null) {
            mLoadMoreUIHandler.onLoadError(this, errorCode, errorMessage);
        }
    }

    public void setHasMore(boolean hasMore) {
        this.mHasMore = hasMore;
    }

    protected void addFooterView(View view) {
    }

    protected abstract void removeFooterView(View view);

    protected abstract RecyclerView retrieveAbsListView();

    public View getFootView() {
        return mFooterView;
    }

    public int getStatus() {
        return mLoadMoreUIHandler.getStatus();
    }

    @Override
    public void removeLoadMoreHandler() {
        if (mLoadMoreHandler != null) {
            mLoadMoreHandler = null;
        }
    }

    public void removeFooterView() {
        if (mFooterView != null) {
            mFooterView = null;
        }
        if (mLoadMoreUIHandler != null) {
            mLoadMoreUIHandler = null;
        }
    }

}