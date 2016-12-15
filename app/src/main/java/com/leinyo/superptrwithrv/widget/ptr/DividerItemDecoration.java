package com.leinyo.superptrwithrv.widget.ptr;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import com.leinyo.superptrwithrv.R;


/**
  * 创建RecyclerView分割线
  *
  */
 public class DividerItemDecoration extends RecyclerView.ItemDecoration {

     /*
     * RecyclerView的布局方向，默认先赋值
     * 为纵向布局
     * RecyclerView 布局可横向，也可纵向
     * 横向和纵向对应的分割想画法不一样
     * */
     private int mOrientation = LinearLayoutManager.VERTICAL ;

     /**
      * item之间分割线的size，默认为0.5
      */
     private float mItemSize = 0.5f;

     /**
      * 绘制item分割线的画笔，和设置其属性
      * 来绘制个性分割线
      */
     private Paint mPaint ;

     /**
      * 构造方法传入布局方向，不可不传
      * @param context
      * @param orientation LinearLayoutManager.VERTICAL | LinearLayoutManager.HORIZONTAL
      */
     public DividerItemDecoration(Context context, int orientation) {
         this(context,orientation,context.getResources().getColor(R.color.divider),0.5f);
     }

    public DividerItemDecoration(Context context, int orientation, float size) {
        this(context,orientation,context.getResources().getColor(R.color.divider),size);
    }

    public DividerItemDecoration(Context context, int orientation, int color, float size) {
        this.mOrientation = orientation;
        if(orientation != LinearLayoutManager.VERTICAL && orientation != LinearLayoutManager.HORIZONTAL){
            throw new IllegalArgumentException("illegal orientation arguments: " + orientation) ;
        }
        mItemSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,size, context.getResources().getDisplayMetrics());
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG) ;
        mPaint.setColor(color);
         /*设置填充*/
        mPaint.setStyle(Paint.Style.FILL);
    }

     @Override
     public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
         if(mOrientation == LinearLayoutManager.VERTICAL){
             drawVertical(c,parent) ;
         }else {
             drawHorizontal(c,parent) ;
         }
     }

     /**
      * 绘制纵向 item 分割线
      * @param canvas
      * @param parent
      */
     private void drawVertical(Canvas canvas,RecyclerView parent){
         final int left = parent.getPaddingLeft();
         final int right = parent.getMeasuredWidth() - parent.getPaddingRight();
         final int childSize = parent.getChildCount() - 1;  // 最后一根线不用画
         for (int i = 0; i < childSize; i++) {
             final View child = parent.getChildAt(i);
             RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
             final float top = child.getBottom() + layoutParams.bottomMargin;
             final float bottom = top + mItemSize;
             canvas.drawRect(left, top, right, bottom, mPaint);
         }
     }

     /**
      * 绘制横向 item 分割线
      * @param canvas
      * @param parent
      */
     private void drawHorizontal(Canvas canvas,RecyclerView parent){
         final int top = parent.getPaddingTop();
         final int bottom = parent.getMeasuredHeight() - parent.getPaddingBottom();
         final int childSize = parent.getChildCount();
         for (int i = 0; i < childSize; i++) {
             final View child = parent.getChildAt(i);
             RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
             final float left = child.getRight() + layoutParams.rightMargin;
             final float right = left + Math.max(mItemSize,1.0f);
             canvas.drawRect(left, top, right, bottom, mPaint);
         }
     }

     /**
      * 设置item分割线的size
      * @param outRect
      * @param view
      * @param parent
      * @param state
      */
     @Override
     public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
         if (mOrientation == LinearLayoutManager.VERTICAL) {
             outRect.set(0, 0, 0, Math.max((int)mItemSize,1));
         } else {
             outRect.set(0, 0, Math.max((int)mItemSize,1), 0);
         }
     }
}