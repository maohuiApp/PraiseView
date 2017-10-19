package com.moly.hooyee.praise;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by Hooyee on 2017/10/18.
 */

public class RecordView extends View {
    private static final byte REDUCE = -1;
    private static final byte NONE = 0;
    private static final byte ADD = 1;

    private byte mStatus = NONE;

    private int mTextHeight;
    private int mCurrentNum;

    private String mCurrentString = "0";
    private String mNextString = "1";
    private String mLastString = "0";
    private Paint mPaint;

    private float pointY;
    private int mPaintAlpha;

    public RecordView(Context context) {
        this(context, null);
    }

    public RecordView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initPaint();

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                pointY = 2 * mTextHeight;
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.parseColor("#ff8000"));
        mPaint.setTextSize(Util.dip2px(getContext(), 24));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        switch (widthSpecMode) {
            case MeasureSpec.UNSPECIFIED:
                widthMeasureSpec = getSuggestedMinimumWidth();
                break;
            case MeasureSpec.AT_MOST:
                int width = (int) mPaint.measureText("0", 0, 1) * mCurrentString.length();
                widthMeasureSpec = width;
                break;
            case MeasureSpec.EXACTLY:
                widthMeasureSpec = widthSpecSize;
                break;
        }

        switch (heightSpecMode) {
            case MeasureSpec.UNSPECIFIED:
                heightMeasureSpec = getSuggestedMinimumWidth();
                break;
            case MeasureSpec.AT_MOST:
                Rect rect = new Rect();
                mPaint.getTextBounds("0", 0, 1, rect);
                mTextHeight = (int) (rect.height() + mPaint.getFontSpacing() / 2);
                heightMeasureSpec = (mTextHeight * 3);
                break;
            case MeasureSpec.EXACTLY:
                heightMeasureSpec = heightSpecSize;
                break;
        }
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    public void addOne() {
        mCurrentString = String.valueOf(mCurrentNum);
        mCurrentNum++;
        mNextString = String.valueOf(mCurrentNum);
        mStatus = ADD;

        // 数字位数进1
        if (mCurrentString.length() < mNextString.length()) {
            mCurrentString = " " + mCurrentString;
            requestLayout();
        }

        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "pointY", 2 * mTextHeight, mTextHeight);
        ObjectAnimator alphaAnim = ObjectAnimator.ofInt(this, "paintAlpha", 255 , 0);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(alphaAnim, animator);
        set.start();
    }

    public void reduceOne() {
        mCurrentString = String.valueOf(mCurrentNum);
        mCurrentNum--;
        if (mCurrentString.length() < mLastString.length()) {
            requestLayout();
        }
        mLastString = String.valueOf(mCurrentNum);
        mStatus = REDUCE;

        // 加入占位符
        if (mCurrentString.length() > mLastString.length()) {
            mLastString = " " + mLastString;
            requestLayout();
        }

        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "pointY", mTextHeight, 2 * mTextHeight);
        ObjectAnimator alphaAnim = ObjectAnimator.ofInt(this, "paintAlpha", 255 , 0);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(alphaAnim, animator);
        set.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mStatus == NONE) {
            canvas.drawText(mCurrentString, 0, pointY, mPaint);
        } else if (mStatus == ADD) {
            for (int i = mNextString.length() - 1; i >= 0; i--) {
                String next = String.valueOf(mNextString.charAt(i));
                String current = String.valueOf(mCurrentString.charAt(i));

                // i位置需要改变
                if (!next.equals(current)) {
                    mPaint.setAlpha(mPaintAlpha);
                    canvas.drawText(current, mPaint.measureText("0", 0, 1) * i, pointY, mPaint);

                    // mPaintAlpha : 255  -  0 递减
                    mPaint.setAlpha(255 - mPaintAlpha);
                    canvas.drawText(next, mPaint.measureText("0", 0, 1) * i, mTextHeight + pointY, mPaint);
                    // i位置不需要改变
                } else {
                    mPaint.setAlpha(255);
                    canvas.drawText(current, mPaint.measureText("0", 0, 1) * i, mTextHeight * 2, mPaint);
                }
            }
        } else if (mStatus == REDUCE) {
            // pointY是累加的，因此有个往下滑动效果
            for (int i = mCurrentString.length() - 1; i >= 0; i--) {
                String last = String.valueOf(mLastString.charAt(i));
                String current = String.valueOf(mCurrentString.charAt(i));

                // i位置需要改变
                if (!last.equals(current)) {
                    mPaint.setAlpha(mPaintAlpha);
                    canvas.drawText(current, mPaint.measureText("0", 0, 1) * i, mTextHeight + pointY, mPaint);

                    // mPaintAlpha : 255  -  0 递减
                    mPaint.setAlpha(255 - mPaintAlpha);
                    canvas.drawText(last, mPaint.measureText("0", 0, 1) * i, pointY, mPaint);
                    // i位置不需要改变
                } else {
                    mPaint.setAlpha(255);
                    canvas.drawText(current, mPaint.measureText("0", 0, 1) * i, mTextHeight * 2, mPaint);
                }
            }
        }
//        if (mStatus == NONE) {
//            canvas.drawText(mCurrentString, 0, pointY, mPaint);
//        } else if (mStatus == ADD) {
//            canvas.drawText(mCurrentString, 0, pointY, mPaint);
//            mPaint.setAlpha(255 - mPaintAlpha);
//            canvas.drawText(mNextString, 0, mTextHeight + pointY, mPaint);
//        } else if (mStatus == REDUCE) {
//            canvas.drawText(mCurrentString, 0, mTextHeight + pointY, mPaint);
//            mPaint.setAlpha(255 - mPaintAlpha);
//            canvas.drawText(mLastString, 0, pointY, mPaint);
//        }
    }

    public float getPointY() {
        return pointY;
    }

    public void setPointY(float pointY) {
        this.pointY = pointY;
        invalidate();
    }

    public int getPaintAlpha() {
        return mPaintAlpha;
    }

    public void setPaintAlpha(int mPaintAlpha) {
        this.mPaintAlpha = mPaintAlpha;
        mPaint.setAlpha(mPaintAlpha);
        invalidate();
    }

//    private class Record {
//        String value;
//        boolean needUpdate;
//
//        public Record(String value) {
//            this.value = value;
//        }
//    }
}
