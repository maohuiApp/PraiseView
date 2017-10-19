package com.moly.hooyee.praise;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Hooyee on 2017/10/19.
 * mail: hooyee_moly@foxmail.com
 */

public class PraiseRecordView extends ViewGroup {

    private PraiseView mPraiseView;
    private RecordView mRecordView;


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

        addView(mPraiseView, 0);
        addView(mRecordView, 1);
        mPraiseView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecordView.addOne();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int height = 0;
        int width = 0;

        for (int i = 0; i < getChildCount(); i++) {
            int childHeight = getChildAt(i).getMeasuredHeight() + getPaddingTop() + getPaddingBottom();
            height = height > childHeight ? height : childHeight;
            width += getChildAt(i).getMeasuredWidth() + getPaddingLeft() + getPaddingRight();
        }

        setMeasuredDimension((widthSpecMode == MeasureSpec.EXACTLY) ? getSuggestedMinimumWidth()
                : width, (heightSpecMode == MeasureSpec.EXACTLY) ? getSuggestedMinimumHeight()
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
        int pBottom =  pTop + mPraiseView.getMeasuredHeight();

        int rLeft = pRight + getPaddingLeft();
        int rTop = (height - rHeight) / 2;
        int rRight = rLeft + mRecordView.getMeasuredWidth();
        int rBottom = rTop + mRecordView.getMeasuredHeight();

        mPraiseView.layout(pLeft, pTop, pRight, pBottom);
        mRecordView.layout(rLeft, rTop, rRight, rBottom);
    }
}
