package com.leinyo.superptrwithrv.widget.ptr;

import android.content.Context;
import android.util.AttributeSet;

import com.leinyo.superptrwithrv.R;
import com.leinyo.superptrwithrv.widget.ptr.header.MaterialHeader;

public class PtrClassicFrameLayout extends PtrFrameLayout {

    private MaterialHeader mPtrClassicHeader;

    public PtrClassicFrameLayout(Context context) {
        super(context);
        initViews();
    }

    public PtrClassicFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public PtrClassicFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews();
    }

    private void initViews() {

        mPtrClassicHeader = new MaterialHeader(getContext());
        int[] colors = getResources().getIntArray(R.array.google_colors);
        (mPtrClassicHeader).setColorSchemeColors(colors);
        mPtrClassicHeader.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        mPtrClassicHeader.setPadding(0, CommonUtils.dpTopx(getContext(), 15), 0, CommonUtils.dpTopx(getContext(), 10));
        (mPtrClassicHeader).setPtrFrameLayout(this);
        setHeaderView(mPtrClassicHeader);
        addPtrUIHandler(mPtrClassicHeader);
        setPinContent(true);
    }

    public MaterialHeader getHeader() {
        return mPtrClassicHeader;
    }

//    /**
//     * Specify the last update time by this key string
//     *
//     * @param key
//     */
//    public void setLastUpdateTimeKey(String key) {
//        if (mPtrClassicHeader != null) {
//            mPtrClassicHeader.setLastUpdateTimeKey(key);
//        }
//    }
//
//    /**
//     * Using an object to specify the last update time.
//     *
//     * @param object
//     */
//    public void setLastUpdateTimeRelateObject(Object object) {
//        if (mPtrClassicHeader != null) {
//            mPtrClassicHeader.setLastUpdateTimeRelateObject(object);
//        }
//    }
}
