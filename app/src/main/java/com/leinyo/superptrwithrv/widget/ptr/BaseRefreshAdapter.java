package com.leinyo.superptrwithrv.widget.ptr;

/**
 * Created by Lsq on 6/17/2016.--8:02 PM
 */

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.leinyo.superptrwithrv.R;
import com.leinyo.superptrwithrv.widget.ptr.loadmore.LoadMoreDefaultFooterView;

import java.util.ArrayList;
import java.util.List;

/**
 * 刷新和加载的适配器
 *
 * @param <VH> ViewHolder的子类
 */
public abstract class BaseRefreshAdapter<VH extends RecyclerView.ViewHolder, T> extends RecyclerView.Adapter {
    protected PullToRefreshView mPullToRefreshView;
    // 内容类型
    private final int TYPE_CONTENT = 1;
    // 底部加载更多
    private final int TYPE_FOOTER = 2;
    // 头部
    private final int TYPE_HEADER = 3;
    // 空布局
    private final int TYPE_EMPTY = 4;

    private List<Integer> mViewTypes; // 子视图类型

    protected List<T> mDataList;

    protected LayoutInflater mLayoutInflater;

    protected Context mContext;


    /**
     * 创建适配器
     */
    public BaseRefreshAdapter(Context context, List<T> list, PullToRefreshView refreshView) {
        this.mPullToRefreshView = refreshView;
        mDataList = list;
        mContext = context;
        mViewTypes = new ArrayList<>();
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_CONTENT || mViewTypes.contains(viewType)) {
            return onCreateHolder(parent, viewType);
        } else if (viewType == TYPE_FOOTER) {
            RecyclerView.LayoutManager layoutManager = mPullToRefreshView.getRecyclerView().getLayoutManager();
            int height = 0;
            int count = 0;
            if (layoutManager instanceof GridLayoutManager) {
                for (int i = 0; i <= layoutManager.getChildCount() - 1; i++) {
                    if (((GridLayoutManager) layoutManager).getSpanSizeLookup().getSpanSize(i) == ((GridLayoutManager) layoutManager).getSpanCount() && mPullToRefreshView.getHeaderView().getVisibility() == View.VISIBLE) {
                        count += layoutManager.getDecoratedMeasuredHeight(layoutManager.getChildAt(i));
                    } else if (((GridLayoutManager) layoutManager).getSpanSizeLookup().getSpanSize(i) == 1) {
                        if (i == 0) {
                            i += 1;
                        }
                        if (i % 2 != 0) {
                            count += layoutManager.getDecoratedMeasuredHeight(layoutManager.getChildAt(i));
                        }
                    }
                }
            } else if (layoutManager instanceof LinearLayoutManager) {
                for (int i = 0; i <= layoutManager.getChildCount() - 1; i++) {
                    count += layoutManager.getDecoratedMeasuredHeight(layoutManager.getChildAt(i));
                }
            }
            height = mPullToRefreshView.getRecyclerView().getMeasuredHeight() - count;

            LoadMoreDefaultFooterView footView = (LoadMoreDefaultFooterView) mPullToRefreshView.getLoadMoreContainer().getFootView();
            LinearLayout linearLayout = (LinearLayout) footView.findViewById(R.id.layout_load_more);
            LinearLayout.LayoutParams params;
            if (height > mPullToRefreshView.getLoadMoreContainer().getFootView().getHeight() && height > ((LoadMoreDefaultFooterView) mPullToRefreshView.getLoadMoreContainer().getFootView()).getViewHeight()) {
                params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            } else {
                params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ((LoadMoreDefaultFooterView) mPullToRefreshView.getLoadMoreContainer().getFootView()).getViewHeight());
            }
            linearLayout.setLayoutParams(params);
            return new RecyclerView.ViewHolder(footView) {
            };
        } else if (viewType == TYPE_HEADER) {
            return new RecyclerView.ViewHolder(mPullToRefreshView.getHeaderView()) {
            };
        } else if (viewType == TYPE_EMPTY) {
            return new RecyclerView.ViewHolder(mPullToRefreshView.getEmptyView()) {
            };
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = holder.getItemViewType();
        if (viewType == TYPE_CONTENT || mViewTypes.contains(viewType)) {
            onBindHolder((VH) holder, mPullToRefreshView.getHeaderView() != null ? position - 1 : position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && mPullToRefreshView.getHeaderView() != null) {
            return TYPE_HEADER;
        }
        if (mDataList.size() == 0 && mPullToRefreshView.getEmptyView() != null) {
            return TYPE_EMPTY;
        }
        //此情况是没有更多时候显示footer，例如显示“没有更多”
        if (mPullToRefreshView.getLoadMoreContainer().getFootView() != null && position == getItemCount() - 1) {
            RecyclerView.ViewHolder viewHolder = mPullToRefreshView.getRecyclerView().getRecycledViewPool().getRecycledView(TYPE_FOOTER);
            if (viewHolder != null) {
                viewHolder.setIsRecyclable(false);
            }
            return TYPE_FOOTER;
        } else {
            int viewType = getItemType(position);
            return viewType == -1 ? TYPE_CONTENT : viewType;
        }
    }

    @Override
    public int getItemCount() {
        int count = mDataList.size();
        if (mPullToRefreshView.getEmptyView() != null && count == 0) {
            count += 1;
        }
        //Footer显示
        if (mPullToRefreshView.getLoadMoreContainer().getFootView() != null && mPullToRefreshView.getEmptyView() == null && count > 0) {
            count += 1;
        }
        if (mPullToRefreshView.getHeaderView() != null) {
            count += 1;
        }
        return count;
    }

    /**
     * 创建ViewHolder, 用来代替onCreateViewHolder()方法, 用法还是一样的
     *
     * @param parent   父控件
     * @param viewType 类型
     */
    protected abstract VH onCreateHolder(ViewGroup parent, int viewType);

    /**
     * 给ViewHolder绑定数据, 用来代替onBindViewHolder(), 用法一样
     *
     * @param holder   ViewHolder的子类实例
     * @param position 位置
     */
    protected abstract void onBindHolder(VH holder, int position);


    /**
     * 自定义获取子视图类型的方法
     *
     * @param position 位置
     * @return 类型
     */
    public int getItemType(int position) {
        return -1;
    }

    /**
     * 设置子视图类型, 如果有新的子视图类型, 直接往参数viewTypes中添加即可, 每个类型的值都要>3, 且不能重复
     *
     * @param viewTypes 子视图类型列表
     */
    public void setItemTypes(List<Integer> viewTypes) {
        if (viewTypes != null) {
            this.mViewTypes.addAll(viewTypes);
        }
    }


    /**
     * @param position 位置
     * @return 是否为脚部布局
     */
    public boolean isFooter(int position) {
        return TYPE_FOOTER == getItemViewType(position);
    }

    /**
     * @param position 位置
     * @return 是否为头部布局
     */
    public boolean isHeader(int position) {
        return TYPE_HEADER == getItemViewType(position);
    }

    /**
     * @param position 位置
     * @return 是否为空布局
     */
    public boolean isEmpty(int position) {
        return TYPE_EMPTY == getItemViewType(position);
    }
}


