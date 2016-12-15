package com.leinyo.superptrwithrv.widget.ptr.loadmore;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leinyo.superptrwithrv.R;

import java.lang.ref.WeakReference;


public class LoadMoreDefaultFooterView extends RelativeLayout implements LoadMoreUIHandler {

    private TextView mTextView;
    private View mLayout;
    private ProgressBar mBar;
    private int mStatus;
    private final int NO_MORE = 0x00;

    public final class StatusConstants {
        final static int LOADING = 1;
        final static int FINISH = 2;
        final static int WAIT = 3;
        final static int ERROR = 4;
    }

    private MyHandler mMyHandler = new MyHandler(this);

    public LoadMoreDefaultFooterView(Context context) {
        this(context, null);
    }

    public LoadMoreDefaultFooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreDefaultFooterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setupViews();
    }

    private void setupViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.footer_ptr_view, this, true);
        mTextView = (TextView) findViewById(R.id.tv_load_more);
        mLayout = findViewById(R.id.layout_load_more);
        mBar = (ProgressBar) findViewById(R.id.pb_loading_more);
    }

    @Override
    public void onLoading(LoadMoreContainer container) {
        mStatus = StatusConstants.LOADING;
        mLayout.setVisibility(VISIBLE);
        mBar.setVisibility(VISIBLE);
        mTextView.setText(R.string.cube_views_load_more_loading);
        setVisibility(VISIBLE);
    }

    @Override
    public void onLoadFinish(LoadMoreContainer container, boolean hasMore) {
        mStatus = StatusConstants.FINISH;
        mLayout.setVisibility(VISIBLE);
        if (!hasMore) {
            mBar.setVisibility(GONE);
            mTextView.setText(R.string.cube_views_load_more_none);
            mMyHandler.removeMessages(NO_MORE);
            mMyHandler.sendEmptyMessageDelayed(NO_MORE, 1500);
        } else {
            setVisibility(GONE);
        }
    }


    @Override
    public void onWaitToLoadMore(LoadMoreContainer container) {
        mStatus = StatusConstants.WAIT;
        mLayout.setVisibility(VISIBLE);
        setVisibility(VISIBLE);
        mTextView.setText(R.string.cube_views_load_more_click_to_load_more);
    }

    @Override
    public void onLoadError(LoadMoreContainer container, int errorCode, String errorMessage) {
        mStatus = StatusConstants.ERROR;
        mLayout.setVisibility(VISIBLE);
        mTextView.setText(R.string.cube_views_load_more_error);
    }

    @Override
    public int getStatus() {
        return mStatus;
    }

    class MyHandler extends Handler {
        private final WeakReference<LoadMoreDefaultFooterView> mActivity;

        public MyHandler(LoadMoreDefaultFooterView activity) {
            mActivity = new WeakReference<LoadMoreDefaultFooterView>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LoadMoreDefaultFooterView activity = mActivity.get();
            if (activity != null) {
                setVisibility(GONE);
            }
        }
    }

    public int getViewHeight() {
        measure(0, 0);
        return getMeasuredHeight();
    }

}
