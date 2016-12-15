package com.leinyo.superptrwithrv;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leinyo.superptrwithrv.widget.ptr.BaseRefreshAdapter;
import com.leinyo.superptrwithrv.widget.ptr.PullToRefreshView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hly on 2016/12/15.
 * email hly910206@gmail.com
 */

public class SimpleAdapter extends BaseRefreshAdapter<SimpleAdapter.SimpleViewHolder, Integer> {

    public SimpleAdapter(Context context, List<Integer> list, PullToRefreshView refreshView) {
        super(context, list, refreshView);
    }


    public void setData(List<Integer> list, boolean isRefresh) {
        if (list != null) {
            if (isRefresh) {
                mDataList.clear();
            }
            mDataList.addAll(list);
            notifyDataSetChanged();
        }
    }


    @Override
    protected SimpleViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new SimpleViewHolder(mLayoutInflater.inflate(R.layout.item_simple, parent, false));
    }


    @Override
    protected void onBindHolder(SimpleViewHolder holder, int position) {
        holder.mTvContent.setText(String.valueOf(mDataList.get(position)));
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_content)
        TextView mTvContent;

        SimpleViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
