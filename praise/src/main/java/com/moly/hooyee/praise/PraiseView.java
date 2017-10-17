package com.moly.hooyee.praise;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewPropertyAnimator;

/**
 * Created by Hooyee on 2017/10/16.
 * mail: hooyee_moly@foxmail.com
 */

public class PraiseView extends View {
    private Drawable mDrawable;
    private Paint mPaint;
    private float mRadius;
    private PointF mCircleCenter = new PointF();

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
        initDrawable(drawable);
        initPaint();
    }

    private void initDrawable(Drawable drawable) {
        mDrawable = drawable;
        Rect drawableRect = new Rect(0, mDrawable.getIntrinsicHeight()/4, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight() + mDrawable.getIntrinsicHeight()/4);
        mDrawable.setBounds(drawableRect);
        requestLayout();

        int width = drawableRect.right - drawableRect.left;
        int height = drawableRect.bottom - drawableRect.top;
        mRadius = width > height ? width/2f : height/2f;
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
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
        mCircleCenter.x = getMeasuredWidth()/2f;
        mCircleCenter.y = getMeasuredHeight()/2f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mDrawable.draw(canvas);
        drawEffect(canvas);
    }

    private void drawEffect(Canvas canvas) {
        // 内圆
        canvas.drawCircle(mCircleCenter.x, mCircleCenter.y, mRadius, mPaint);

        PointF p1 = new PointF(
                mCircleCenter.x + (float)(mRadius * Math.cos(Math.toRadians(45))),
                mCircleCenter.y + (float)(mRadius * Math.sin(Math.toRadians(45)))
                );

        PointF p2 = new PointF(
                mCircleCenter.x + (float)(1.5f * mRadius * Math.cos(Math.toRadians(45))),
                mCircleCenter.y + (float)(1.5f * mRadius * Math.sin(Math.toRadians(45)))
        );

//        canvas.drawLine(p1.x, p1.y, p2.x, p2.y, mPaint);
//        canvas.drawLine(140, 90, 180, 90, mPaint);
        canvas.drawLines(new float[] {
                p1.x, p1.y,
                p2.x, p2.y,
        }, mPaint);

//        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
    }

    public void animation() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "radius", getRadius(), getRadius() * 1.2f);
        animator.setDuration(1000);
        animator.start();
    }

    public float getRadius() {
        return mRadius;
    }

    public void setRadius(float mRadius) {
        this.mRadius = mRadius;
        invalidate();
    }
}
