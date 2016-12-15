package com.leinyo.superptrwithrv;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.leinyo.superptrwithrv.widget.ptr.DividerItemDecoration;
import com.leinyo.superptrwithrv.widget.ptr.PullToRefreshView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements PullToRefreshView.OnLoadMoreListener, PullToRefreshView.OnPullRefreshListener, PullToRefreshView.OnRefreshListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.pull_view)
    PullToRefreshView mPullView;

    private List<Integer> mIntegerList = new ArrayList<>();
    private SimpleAdapter mSimpleAdapter;
    private int mCursor = 1;
    private MyHandler mMyHandler = new MyHandler(this);
    private final int PULL = 1;
    private final int LOAD_MORE = 2;
    private TextView mHeadView;
    private View mEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        initData();
        mEmpty = LayoutInflater.from(this).inflate(R.layout.view_empty, null);
        mHeadView = new TextView(this);
        mHeadView.setTextSize(30f);
        mHeadView.setGravity(Gravity.CENTER);
        mHeadView.setText(getString(R.string.current_mode, getString(R.string.action_normal)));
        mSimpleAdapter = new SimpleAdapter(this, getData(), mPullView);
        mPullView.addHeaderView(mHeadView);

        mPullView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL, 1f));
        mPullView.setAdapter(mSimpleAdapter);
    }


    private void initData() {
        for (int i = 1; i <= 50; i++) {
            mIntegerList.add(i);
        }
    }

    private List<Integer> getData() {
        List<Integer> list = new ArrayList<>(mIntegerList);
        if (hasMore()) {
            list = list.subList((mCursor - 1) * 10, mCursor * 10);
        }
        mCursor += 1;
        return list;
    }


    private boolean hasMore() {
        return mIntegerList.size() > (mCursor - 1) * 10;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        String headTitle = "";
        mPullView.removeListener();
        if (mPullView.getEmptyView() != null) {
            mPullView.removeEmptyView();
        }
        if (mPullView.getLoadMoreContainer().getFootView() != null) {
            mPullView.removeFooterView();
        }
        switch (id) {
            case R.id.action_normal:
                mPullView.setMode(PullToRefreshView.NONE);
                headTitle = getString(R.string.action_normal);
                break;
            case R.id.action_empty:
                mPullView.addEmptyView(mEmpty);
                headTitle = getString(R.string.action_empty);
                break;
            case R.id.action_pull:
                mPullView.setMode(PullToRefreshView.REFRESH_FROM_START);
                mPullView.setPullRefreshListener(this);
                headTitle = getString(R.string.action_pull);
                break;
            case R.id.action_loadmore:
                mPullView.onLoadMoreComplete(true);
                mPullView.setMode(PullToRefreshView.REFRESH_FROM_END);
                mPullView.setOnLoadMoreListener(this);
                headTitle = getString(R.string.action_loadmore);
                break;
            case R.id.action_both:
                mPullView.onLoadMoreComplete(true);
                mPullView.setMode(PullToRefreshView.REFRESH_BOTH);
                mPullView.setOnRefreshListener(this);
                headTitle = getString(R.string.action_both);
                break;
        }
        if (id == R.id.action_empty) {
            mSimpleAdapter.setData(new ArrayList<Integer>(), true);
        } else if (mCursor > 1) {
            mCursor = 1;
            mSimpleAdapter.setData(getData(), true);
        }
        //mPullView.getRecyclerView().smoothScrollToPosition(0);
        mHeadView.setText(getString(R.string.current_mode, headTitle));

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onLoadMoreRefresh() {
        Toast.makeText(this, "上拉加载", Toast.LENGTH_SHORT).show();
        mMyHandler.sendEmptyMessageDelayed(LOAD_MORE, 1500);
    }

    @Override
    public void onPullRefresh() {
        mCursor = 1;
        Toast.makeText(this, "下拉刷新", Toast.LENGTH_SHORT).show();
        mMyHandler.sendEmptyMessageDelayed(PULL, 1500);
    }

    class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case PULL:
                        mSimpleAdapter.setData(getData(), !mPullView.getCurrentFreshMode());
                        mPullView.onPullRefreshComplete();
                        break;
                    case LOAD_MORE:
                        mSimpleAdapter.setData(getData(), !mPullView.getCurrentFreshMode());
                        mPullView.onLoadMoreComplete(hasMore());
                        break;
                }
            }
        }
    }
}
