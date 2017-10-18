package com.moly.hooyee.praise;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
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
import android.support.annotation.Px;
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
    public static final byte CANCEL_PRAISE = 1;
    public static final byte NONE = 0;
    public static final byte TO_PRAISE = -1;

    private Drawable mDrawable;
    private Paint mPaint;
    private float mRadius;
    private PointF mCircleCenter = new PointF();
    List<PointF> mPointList = new ArrayList<>();

    private byte mState;
    /** 点赞后的画笔颜色 */
    private int mColor;

    public PraiseView(Context context) {
        this(context, null);
    }

    public PraiseView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PraiseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Resources.Theme theme = getContext().getTheme();
        mDrawable = VectorDrawableCompat.create(getResources(), R.drawable.ic_praise, theme);

        initAttr(attrs);

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

    private void initAttr(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PraiseView);
        mColor = typedArray.getColor(R.styleable.PraiseView_color_praise, Color.parseColor("#ff8000"));
    }

    // drawable的大小为view的0.6
    private void initDrawable(Drawable drawable, int width, int height) {
        mCircleCenter.x = width / 2f;
        mCircleCenter.y = height / 2;
        mDrawable = drawable;


        Rect drawableRect = new Rect((width * 2 / 10) , (height * 2 / 10), (width * 8 / 10), (height * 8 / 10));
        mDrawable.setBounds(drawableRect);
        requestLayout();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setColor(mColor);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
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
                heightMeasureSpec = mDrawable.getIntrinsicHeight() + mDrawable.getIntrinsicHeight() / 4;
                break;
            // 具体值或者match_parent
            case MeasureSpec.EXACTLY:
                heightMeasureSpec = heightSpecSize;
                break;
        }

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        initDrawable(mDrawable, widthMeasureSpec, heightMeasureSpec);

    }

    /**
     * 用于计算 线条的长度
     * @param scale 外圆半径为内圆半径的scale倍数
     */
    private void initPointFs(float scale) {
        mPointList.clear();
        float radius = getInitRadius(mDrawable);
        int base = -60;
        int factor = -20;
        for (int i = 0; i < 4; i++) {
            int result = base + factor * i;
            PointF p1 = new PointF(
                    mCircleCenter.x + (float) (radius * Math.cos(Math.toRadians(result))),
                    mCircleCenter.y + (float) (radius * Math.sin(Math.toRadians(result)))
            );

            PointF p2 = new PointF(
                    mCircleCenter.x + (float) (scale * radius * Math.cos(Math.toRadians(result))),
                    mCircleCenter.y + (float) (scale * radius * Math.sin(Math.toRadians(result)))
            );

            mPointList.add(p1);
            mPointList.add(p2);
        }
    }

    // 是否画线，1代表画，0代表不画
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
            float strokeWidth = Util.dip2px(getContext(), 10);
            mPaint.setStrokeWidth(strokeWidth);
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
                switch (mState) {
                    // 从mState之前的状态修改为用户操作的目标状态
                    case NONE:
                    case CANCEL_PRAISE:
                        changeState(TO_PRAISE);
                        break;
                    case TO_PRAISE:
                        changeState(CANCEL_PRAISE);
                        break;
                }
                animate().scaleY(0.8f).scaleX(0.8f).start();
                break;
            case MotionEvent.ACTION_UP:
                execCMD(mState);
                break;
        }
        return super.onTouchEvent(event);
    }

    private void execCMD(byte state) {
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animate().cancel();
                setScaleX(1);
                setScaleY(1);
            }
        }, 300);
        switch (state) {
            case TO_PRAISE:
                execPraise();
                break;
            case CANCEL_PRAISE:
                execCancelPraise();
                break;
        }
    }

    private void execPraise() {
        animation();
    }

    private void execCancelPraise() {
        mDrawable.setColorFilter(null);
        setDrawLines(0);
    }

    private void changeState(byte state) {
        mState = state;
    }

    public void animation() {
        final float radius = getInitRadius(mDrawable);
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "radius", radius, radius * 1.5f, radius * 3.0f);
        animator.setInterpolator(new AnticipateInterpolator());
        animator.setDuration(500);

        ObjectAnimator animator1 = ObjectAnimator.ofInt(this, "drawLines", 0, 1);
        animator1.setInterpolator(new AccelerateDecelerateInterpolator());
        animator1.setDuration(500);
        animator1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });

        mDrawable.setColorFilter(new PorterDuffColorFilter(mColor, PorterDuff.Mode.SRC_IN));
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animator, animator1);
        set.start();
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
