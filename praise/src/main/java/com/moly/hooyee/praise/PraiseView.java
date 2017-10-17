package com.moly.hooyee.praise;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hooyee on 2017/10/16.
 * mail: hooyee_moly@foxmail.com
 */

public class PraiseView extends View {
    private Drawable mDrawable;
    private Paint mPaint;
    private float mRadius;
    private PointF mCircleCenter = new PointF();
    List<PointF> mPointList = new ArrayList<>();

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
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                initPointFs(1.3f);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
    }

    private void initDrawable(Drawable drawable) {
        mDrawable = drawable;
        Rect drawableRect = new Rect(0, mDrawable.getIntrinsicHeight()/4, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight() + mDrawable.getIntrinsicHeight()/4);
        mDrawable.setBounds(drawableRect);
        requestLayout();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#ffFF8000"));
        mPaint.setStrokeCap(Paint.Cap.ROUND);
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
        mCircleCenter.y = getMeasuredHeight()/2f + mDrawable.getBounds().top / 2;
    }

    private void initPointFs(float scale) {
        mPointList.clear();
        float radius = getInitRadius(mDrawable);
        int base = -60;
        int factor = -20;
        for (int i = 0; i < 4; i++) {
            int result = base + factor * i;
            PointF p1 = new PointF(
                    mCircleCenter.x + (float)(radius * Math.cos(Math.toRadians(result))),
                    mCircleCenter.y + (float)(radius * Math.sin(Math.toRadians(result)))
            );

            PointF p2 = new PointF(
                    mCircleCenter.x + (float)(scale * radius * Math.cos(Math.toRadians(result))),
                    mCircleCenter.y + (float)(scale * radius * Math.sin(Math.toRadians(result)))
            );

            mPointList.add(p1);
            mPointList.add(p2);
        }
    }

    private int drawLines;

    public int getDrawLines() {
        return drawLines;
    }

    public void setDrawLines(int drawLines) {
        this.drawLines = drawLines;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mDrawable.draw(canvas);

        drawEffect(canvas);
    }

    private void drawEffect(Canvas canvas) {
        // 画圆
        if (mRadius > 0)
            canvas.drawCircle(mCircleCenter.x, mCircleCenter.y, mRadius, mPaint);

        if (drawLines == 1) {
            // 划线
            float flag = mPaint.getStrokeWidth();
            mPaint.setStrokeWidth(15);
            for (int i = 0; i < mPointList.size(); i += 2) {
                canvas.drawLines(new float[]{
                        mPointList.get(i).x, mPointList.get(i).y,
                        mPointList.get(i + 1).x, mPointList.get(i + 1).y
                }, mPaint);
            }
            mPaint.setStrokeWidth(flag);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                animate().scaleY(0.8f).scaleX(0.8f).start();
                break;
            case MotionEvent.ACTION_UP:
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        animate().cancel();
                        setScaleX(1);
                        setScaleY(1);
                    }
                }, 300);
                animation();
                break;
        }
        return super.onTouchEvent(event);
    }

    public void animation() {
        final float radius = getInitRadius(mDrawable);
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "radius", radius, radius*1.3f, radius * 1.8f);
        animator.setInterpolator(new AnticipateInterpolator());
        animator.setDuration(500);

        ObjectAnimator animator1 = ObjectAnimator.ofInt(this, "drawLines", 0, 1, 0);
        animator1.setInterpolator(new AccelerateDecelerateInterpolator());
        animator1.setDuration(1000);
        animator1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mDrawable.setColorFilter(null);
            }
        });

        mDrawable.setColorFilter(new PorterDuffColorFilter(Color.parseColor("#ffFF8000"), PorterDuff.Mode.SRC_IN));
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animator, animator1);
        set.start();
//        animator.start();
//        animator1.start();
    }

    float getInitRadius(Drawable drawable) {
        int width = drawable.getBounds().right - drawable.getBounds().left;
        int height = drawable.getBounds().bottom - drawable.getBounds().top;
        return width > height ? width / 2 : height / 2;
    }

    public float getRadius() {
        return mRadius;
    }

    public void setRadius(float mRadius) {
        this.mRadius = mRadius;
        invalidate();
    }
}
