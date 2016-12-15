package com.leinyo.superptrwithrv.utils;

import android.content.Context;

/**
 * Created by hly on 2016/12/15.
 * email hly910206@gmail.com
 */

public class CommonUtils {

    /**
     * dp转化px
     *
     * @param context
     * @param dpValue
     * @return
     */

    public static int dpTopx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public static int getScreenWidthpx(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }
}
