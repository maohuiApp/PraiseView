package com.moly.hooyee.praise;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Hooyee on 2017/10/19.
 * mail: hooyee_moly@foxmail.com
 */

public class PraiseRecordView extends ViewGroup {

    private PraiseView mPraiseView;
    private RecordView mRecordView;
    private OnPriseListener mListener;

    public PraiseRecordView(Context context) {
        this(context, null);
    }

    public PraiseRecordView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PraiseRecordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPraiseView = new PraiseView(context);
        mRecordView = new RecordView(context);

        addView(mPraiseView, 0, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(mRecordView, 1, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mPraiseView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPraiseView.getState() == PraiseView.TO_PRAISE) {
                    mRecordView.addOne();
                } else if (mPraiseView.getState() == PraiseView.CANCEL_PRAISE) {
                    mRecordView.reduceOne();
                }
                if (mListener != null) {
                    mListener.callback(mPraiseView.getState());
                }
            }
        });

        mRecordView.setCurrentNum(109);

        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PraiseRecordView);
        int color = typedArray.getColor(R.styleable.PraiseRecordView_color_praise_view, Color.parseColor("#ff8000"));
        mPraiseView.setPaintColor(color);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthSpecMode == MeasureSpec.EXACTLY) {
            int widthSpec = MeasureSpec.makeMeasureSpec(widthSpecSize / 2, widthSpecMode);
            int heightSpec = MeasureSpec.makeMeasureSpec(heightSpecSize / 2, heightSpecMode);
            measureChildren(widthSpec, heightSpec);
        } else {
            measureChildren(widthMeasureSpec, heightMeasureSpec);
        }

        int height = 0;
        int width = 0;

        for (int i = 0; i < getChildCount(); i++) {
            int childHeight = getChildAt(i).getMeasuredHeight() + getPaddingTop() + getPaddingBottom();
            height = height > childHeight ? height : childHeight;
            width += getChildAt(i).getMeasuredWidth() + getPaddingLeft() + getPaddingRight();
        }

        setMeasuredDimension((widthSpecMode == MeasureSpec.EXACTLY) ? widthSpecSize
                : width, (heightSpecMode == MeasureSpec.EXACTLY) ? heightSpecSize
                : height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int height = getMeasuredHeight();
        int pHeight = mPraiseView.getMeasuredHeight();
        int rHeight = mRecordView.getMeasuredHeight();


        int pLeft = getPaddingLeft();
        int pTop = (height - pHeight) / 2;
        int pRight = pLeft + mPraiseView.getMeasuredWidth();
        int pBottom = pTop + mPraiseView.getMeasuredHeight();

        int rLeft = pRight + getPaddingLeft();
        int rTop = (height - rHeight) / 2;
        int rRight = rLeft + mRecordView.getMeasuredWidth();
        int rBottom = rTop + mRecordView.getMeasuredHeight();

        mPraiseView.layout(pLeft, pTop, pRight, pBottom);
        mRecordView.layout(rLeft, rTop, rRight, rBottom);
    }

    float moveY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                moveY = ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                float currentY = ev.getY();
                if (Math.abs(moveY - currentY) > 50) {
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void setListener(OnPriseListener mListener) {
        this.mListener = mListener;
    }

    public interface OnPriseListener {
        /**
         * @param state 点击后为赞还是取消赞的状态
         */
        void callback(int state);
    }
}
