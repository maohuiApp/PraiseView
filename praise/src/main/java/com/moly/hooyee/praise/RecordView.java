package com.moly.hooyee.praise;

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
    private String mCurrentString = "8";
    private String mNextString = "9";
    private String mLastString = "7";
    private Paint mPaint;

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
//                Rect rect = new Rect();
                int width = (int) mPaint.measureText(mCurrentString, 0, mCurrentString.length());
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
                mPaint.getTextBounds(mCurrentString, 0, mCurrentString.length(), rect);
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
        mCurrentNum++;
        mStatus = ADD;

        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "pointY", 2 * mTextHeight, mTextHeight);
        animator.start();
    }

    public void reduceOne() {
        mCurrentNum--;
        mStatus = REDUCE;

        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "pointY", mTextHeight, 2 * mTextHeight);
        animator.start();
    }

    private float pointY;

    public float getPointY() {
        return pointY;
    }

    public void setPointY(float pointY) {
        this.pointY = pointY;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mStatus == NONE) {
            canvas.drawText(mCurrentString, 0, pointY, mPaint);
        } else if (mStatus == ADD) {
            canvas.drawText(mCurrentString, 0, pointY, mPaint);
            canvas.drawText(mNextString, 0, mTextHeight + pointY, mPaint);
        } else if (mStatus == REDUCE) {
            canvas.drawText(mCurrentString, 0, mTextHeight + pointY, mPaint);
            canvas.drawText(mLastString, 0, pointY, mPaint);
        }
    }
}
