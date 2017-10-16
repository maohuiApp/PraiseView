package com.moly.hooyee.praise;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Hooyee on 2017/10/16.
 * mail: hooyee_moly@foxmail.com
 */

public class PraiseView extends View {
    private Drawable mDrawable;
    private Paint mPaint;

    public PraiseView(Context context) {
        this(context, null);
    }

    public PraiseView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PraiseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Resources.Theme theme = getContext().getTheme();
        Drawable drawable = VectorDrawableCompat.create(getResources(), R.drawable.ic_praise, theme);
        setDrawable(drawable);
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
    }

    public void setDrawable(Drawable drawable) {
        mDrawable = drawable;
        Rect drawableRect = new Rect(0, mDrawable.getIntrinsicHeight()/4, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight() + mDrawable.getIntrinsicHeight()/4);
        mDrawable.setBounds(drawableRect);
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        switch (widthSpecMode) {
            case MeasureSpec.UNSPECIFIED:
                widthMeasureSpec = getSuggestedMinimumWidth();
                break;
            case MeasureSpec.AT_MOST:
                widthMeasureSpec = mDrawable.getIntrinsicWidth();
                break;
            case MeasureSpec.EXACTLY:
                widthMeasureSpec = widthSpecSize;
                break;
        }

        switch (heightSpecMode) {
            case MeasureSpec.UNSPECIFIED:
                heightMeasureSpec = getSuggestedMinimumHeight();
                break;
            // wrap_content
            case MeasureSpec.AT_MOST:
                heightMeasureSpec = mDrawable.getIntrinsicHeight() + mDrawable.getIntrinsicHeight()/4;
                break;
            // 具体值或者match_parent
            case MeasureSpec.EXACTLY:
                heightMeasureSpec = heightSpecSize;
                break;
        }

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mDrawable.draw(canvas);
        drawEffect(canvas);
    }

    private void drawEffect(Canvas canvas) {
        int width = mDrawable.getBounds().right - mDrawable.getBounds().left;
        int height = mDrawable.getBounds().bottom - mDrawable.getBounds().top;
        float radius = width > height ? width/2f : height/2f;
        canvas.drawCircle(width/2f, getHeight()/2f, radius, mPaint);

        canvas.drawCircle(width/2f, height/4f, height/4f, mPaint);

//        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
    }
}
