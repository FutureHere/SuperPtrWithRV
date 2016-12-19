package com.leinyo.superptrwithrv.widget.ptr;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.leinyo.superptrwithrv.R;
import com.leinyo.superptrwithrv.widget.ptr.header.MaterialHeader;
import com.leinyo.superptrwithrv.widget.ptr.loadmore.LoadMoreContainer;
import com.leinyo.superptrwithrv.widget.ptr.loadmore.LoadMoreHandler;
import com.leinyo.superptrwithrv.widget.ptr.loadmore.LoadMoreRecyclerViewContainer;

import static com.leinyo.superptrwithrv.widget.ptr.CommonUtils.dpTopx;


/**
 * Created by hly on 16/9/5.
 * email hly910206@gmail.com
 */
public class PullToRefreshView extends LinearLayout {
    private PtrFrameLayout mPtrFrameLayout;
    private LoadMoreRecyclerViewContainer mLoadMoreRecyclerViewContainer;
    private RecyclerView mRecyclerView;
    private OnRefreshListener mRefreshListener;
    private OnPullRefreshListener mOnPullRefreshListener;
    private OnLoadMoreListener mOnLoadMoreListener;
    private int mRefreshMode, mRefreshHeadMode;
    private int mCurrentRefreshMode;
    public static final int NONE = 0;
    public static final int REFRESH_FROM_START = 1;
    public static final int REFRESH_FROM_END = 2;
    public static final int REFRESH_BOTH = 3;
    private View mHeaderView, mEmptyView;
    private int mPaddingLeft, mPaddingRight;
    private boolean mIsCheckLogin, mIsScrollAble;

    public final class HeaderModeConstants {
        static final int MODE_MATERIAL = 0;
        static final int MODE_NORMAL = 1;
    }

    public PullToRefreshView(Context context) {
        this(context, null);
    }

    public PullToRefreshView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullToRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PullToRefreshView);
        mRefreshMode = ta.getInt(R.styleable.PullToRefreshView_ptr_refresh_mode, NONE);
        mRefreshHeadMode = ta.getInt(R.styleable.PullToRefreshView_ptr_header_mode, 0);
        mIsCheckLogin = ta.getBoolean(R.styleable.PullToRefreshView_ptr_check_login, false);
        mPaddingLeft = ta.getInt(R.styleable.PullToRefreshView_ptr_padding_left, 0);
        mPaddingRight = ta.getInt(R.styleable.PullToRefreshView_ptr_padding_right, 0);
        mIsScrollAble = ta.getBoolean(R.styleable.PullToRefreshView_ptr_scrollable, true);
        ta.recycle();
        setupViews();
    }

    private void buildPullDownView() {
        if (mPtrFrameLayout.getHeaderView() != null) {
            return;
        }
        View view;
        switch (mRefreshHeadMode) {
            case HeaderModeConstants.MODE_NORMAL:
                view = new PtrClassicDefaultHeader(getContext());
                ((PtrClassicDefaultHeader) view).setLastUpdateTimeRelateObject(this);
                break;
            case HeaderModeConstants.MODE_MATERIAL:
                view = new MaterialHeader(getContext());
                int[] colors = getResources().getIntArray(R.array.google_colors);
                ((MaterialHeader) view).setColorSchemeColors(colors);
                view.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
                view.setPadding(0, dpTopx(getContext(), 15), 0, dpTopx(getContext(), 10));
                ((MaterialHeader) view).setPtrFrameLayout(mPtrFrameLayout);
                mPtrFrameLayout.setPinContent(true);
                break;
            default:
                throw new IllegalArgumentException("invalid null parameter: " + mRefreshHeadMode);
        }
        mPtrFrameLayout.setHeaderView(view);
        mPtrFrameLayout.addPtrUIHandler((PtrUIHandler) view);
    }

    private void setupViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_pulltorefresh, this, true);
        mPtrFrameLayout = (PtrFrameLayout) findViewById(R.id.pull_refresh_container);
        mLoadMoreRecyclerViewContainer = (LoadMoreRecyclerViewContainer) findViewById(R.id.load_more_container);
        mRecyclerView = mLoadMoreRecyclerViewContainer.getRecyclerView();
        mRecyclerView.setPadding(dpTopx(getContext(), mPaddingLeft), 0, dpTopx(getContext(), mPaddingRight), 0);
        mRecyclerView.setVerticalScrollBarEnabled(mIsScrollAble);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        setMode(mRefreshMode);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mHeaderView != null) {
            int getY = (int) ev.getY();
            if (getY + mRecyclerView.getScrollY()
                    < mHeaderView.getMeasuredHeight()) {
                mPtrFrameLayout.requestDisallowInterceptTouchEvent(true);
            } else {
                mPtrFrameLayout.requestDisallowInterceptTouchEvent(false);
            }
        } else {
            mPtrFrameLayout.requestDisallowInterceptTouchEvent(false);
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 添加分割线
     */
    public void addItemDecoration(RecyclerView.ItemDecoration decor) {
        mRecyclerView.addItemDecoration(decor);
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    /**
     * 设置模式
     *
     * @param mode
     */
    public void setMode(int mode) {
        mRefreshMode = mode;
        if (mRefreshMode == NONE) {
            return;
        }

        buildPullDownView();

        mPtrFrameLayout.setLoadingMinTime(1000);

        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                if (mIsCheckLogin) {
                    return isLogin() && PtrDefaultHandler.checkContentCanBePulledDown(frame, mRecyclerView, header);
                } else if (mRefreshMode == REFRESH_FROM_START || mRefreshMode == REFRESH_BOTH) {
                    return PtrDefaultHandler.checkContentCanBePulledDown(frame, mRecyclerView, header);
                }
                return false;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                if (mRefreshMode == REFRESH_FROM_START || mRefreshMode == REFRESH_BOTH) {
                    mCurrentRefreshMode = REFRESH_FROM_START;
                    if (mOnPullRefreshListener != null) {
                        mOnPullRefreshListener.onPullRefresh();
                    } else if (mRefreshListener != null) {
                        mRefreshListener.onPullRefresh();
                    }
                }
            }
        });

        // load more container
        if (mRefreshMode == REFRESH_FROM_END || mRefreshMode == REFRESH_BOTH) {
            mLoadMoreRecyclerViewContainer.useDefaultFooter();
            mLoadMoreRecyclerViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
                @Override
                public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                    mCurrentRefreshMode = REFRESH_FROM_END;
                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener.onLoadMoreRefresh();
                    } else if (mRefreshListener != null) {
                        mRefreshListener.onLoadMoreRefresh();
                    }
                }
            });
        }
    }

    /**
     * 是否登录逻辑自己写
     *
     * @return
     */
    public boolean isLogin() {
        return true;
    }

    public void setLayoutManager(final RecyclerView.LayoutManager layoutManager) {
        mRecyclerView.setLayoutManager(layoutManager);
    }

    /**
     * @return 当前正在使用的适配器
     */
    private BaseRefreshAdapter getAdapter() {
        return (BaseRefreshAdapter) mRecyclerView.getAdapter();
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }

    public interface OnRefreshListener {
        void onPullRefresh();

        void onLoadMoreRefresh();
    }

    public interface OnLoadMoreListener {
        void onLoadMoreRefresh();
    }

    public interface OnPullRefreshListener {
        void onPullRefresh();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        this.mRefreshListener = listener;
    }

    public void setPullRefreshListener(OnPullRefreshListener onPullRefreshListener) {
        this.mOnPullRefreshListener = onPullRefreshListener;
    }

    public void removeListener() {
        if (mOnLoadMoreListener != null) {
            mOnLoadMoreListener = null;
            mLoadMoreRecyclerViewContainer.removeLoadMoreHandler();
        }
        if (mOnPullRefreshListener != null) {
            mOnPullRefreshListener = null;
        }
        if (mRefreshListener != null) {
            mRefreshListener = null;
        }
    }


    public boolean getCurrentFreshMode() {
        return mCurrentRefreshMode != REFRESH_FROM_START;
    }

    /**
     * 加载完成
     *
     * @param hasMore
     */
    public void onLoadComplete(boolean hasMore) {
        mLoadMoreRecyclerViewContainer.loadMoreFinish(hasMore);
        if (mCurrentRefreshMode == REFRESH_FROM_START) {
            if (isRefreshing()) {
                mPtrFrameLayout.refreshComplete();
            }
        }
    }

    /**
     * 下拉刷新加载完成
     */
    public void onPullRefreshComplete() {
        mPtrFrameLayout.refreshComplete();
    }

    /**
     * 加载更多完成
     */
    public void onLoadMoreComplete(boolean hasMore) {
        mLoadMoreRecyclerViewContainer.loadMoreFinish(hasMore);
    }

    /**
     * 是否下拉加载中
     */
    public boolean isRefreshing() {
        return mPtrFrameLayout.isRefreshing();
    }

    /**
     * 手动下拉刷新列表
     */
    public void setManualPullRefresh() {
        mPtrFrameLayout.refreshComplete();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
            }
        }, 200);
    }

    /**
     * 设置下拉刷新是否可用
     */
    public void setEnable(boolean enable) {
        mPtrFrameLayout.setEnabled(enable);
    }

    public LoadMoreRecyclerViewContainer getLoadMoreContainer() {
        return mLoadMoreRecyclerViewContainer;
    }

    public void setOnScrollListener(RecyclerView.OnScrollListener l) {
        mLoadMoreRecyclerViewContainer.setOnScrollListener(l);
    }

    public void addHeaderView(View view) {
        if (view != null && view.getLayoutParams()
                == null) {
            LayoutParams pa = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(pa);
        }
        mHeaderView = view;
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public View getEmptyView() {
        return mEmptyView;
    }

    public void addEmptyView(View view) {
        if (view != null && view.getLayoutParams()
                == null) {
            LayoutParams pa = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(pa);
        }
        mEmptyView = view;
    }

    public void removeEmptyView() {
        if (mEmptyView != null) {
            mEmptyView = null;
        }
    }

    public void removeFooterView() {
        if (mLoadMoreRecyclerViewContainer.getFootView() != null) {
            mLoadMoreRecyclerViewContainer.removeFooterView();
        }
    }
}
