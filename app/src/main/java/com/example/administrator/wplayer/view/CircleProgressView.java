package com.example.administrator.wplayer.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.example.administrator.wplayer.R;
import com.example.administrator.wplayer.utils.UnitUtil;


/**
 * Created by wangshaoqiang
 * on 16-12-14.
 */
public class CircleProgressView extends View {

    private int mRadius;
    private int mBackgroundColor;
    private int mProgressColor;
    private int x;
    private int y;
    private int mWidth;
    private int mHeight;
    private int mProgress;
    private int mStripeWidth;
    private int mTxtSize1;
    private int mTxtSize2;
    private int mTxtSize3;
    private int mCurProgress;
    private Paint mBigCirclePaint;
    private Paint mSectorPaint;
    private Paint mSmallCirclePaint;
    private Paint mTextPaint1;
    private Paint mTextPaint2;
    private Paint mTextPaint3;
    private int mTextOneColor;
    private int mTextTwoColor;
    private int mTextThreeColor;
    private boolean mIsClockwise;
    private String mTextOne = "0";
    private String mTextTwo;
    private String mTextThree;
    private boolean mIsTextOneBold;
    private boolean mIsTextTwoBold;
    private boolean mIsTextThreeBold;
    private float textLength3;
    private float textLength1;
    private RectF rect;
    private double txtHeight1;
    private double txtHeight3;
    private int mBackgroundInColor;
    private float y1;
    private float y2;

    public void setRadius(int mRadius) {
        this.mRadius = mRadius;
    }

    public void setBackgroundProgressColor(int mBackgroundColor) {
        this.mBackgroundColor = mBackgroundColor;
    }

    public void setProgressColor(int mProgressColor) {
        this.mProgressColor = mProgressColor;
    }

    public void setStripeWidth(int mStripeWidth) {
        this.mStripeWidth = mStripeWidth;
    }

    public void setTxtSizeOne(int mTxtSize1) {
        this.mTxtSize1 = mTxtSize1;
    }

    public void setTxtSizeTwo(int mTxtSize2) {
        this.mTxtSize2 = mTxtSize2;
    }

    public void setTxtSizeThree(int mTxtSize3) {
        this.mTxtSize3 = mTxtSize3;
    }

    public void setIsClockwise(boolean mIsClockwise) {
        this.mIsClockwise = mIsClockwise;
    }

    public void setTextOneColor(int mTextOneColor) {
        this.mTextOneColor = mTextOneColor;
    }

    public void setTextTwoColor(int mTextTwoColor) {
        this.mTextTwoColor = mTextTwoColor;
    }

    public void setTextThreeColor(int mTextThreeColor) {
        this.mTextThreeColor = mTextThreeColor;
    }

    public void setIsTextOneBold(boolean mIsTextOneBold) {
        this.mIsTextOneBold = mIsTextOneBold;
    }

    public void setIsTextTwoBold(boolean mIsTextTwoBold) {
        this.mIsTextTwoBold = mIsTextTwoBold;
    }

    public void setIsTextThreeBold(boolean mIsTextThreeBold) {
        this.mIsTextThreeBold = mIsTextThreeBold;
    }

    public void setBackgroundInColor(int mBackgroundInColor) {
        this.mBackgroundInColor = mBackgroundInColor;
    }

    public CircleProgressView(Context context) {
        this(context,null);
    }

    public CircleProgressView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CircleProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressView, defStyleAttr, 0);
        setAttributes(context, typedArray);
        initPaint();
    }

    private void setAttributes(Context context, TypedArray typedArray) {
        //r
        mRadius = typedArray.getDimensionPixelSize(R.styleable.CircleProgressView_radius, UnitUtil.dip2px(context,100f));
        //progress width
        mStripeWidth = typedArray.getDimensionPixelSize(R.styleable.CircleProgressView_stripeWidth, UnitUtil.dip2px(context,20f));
        //text size
        mTxtSize1 = typedArray.getDimensionPixelSize(R.styleable.CircleProgressView_textSizeOne, UnitUtil.sp2px(context,20f));
        mTxtSize2 = typedArray.getDimensionPixelSize(R.styleable.CircleProgressView_textSizeTwo, UnitUtil.sp2px(context,20f));
        mTxtSize3 = typedArray.getDimensionPixelSize(R.styleable.CircleProgressView_textSizeThree, UnitUtil.sp2px(context,20f));
        //color
        mBackgroundColor = typedArray.getColor(R.styleable.CircleProgressView_backgroundProgressColor, 0xff6950a1);
        mProgressColor = typedArray.getColor(R.styleable.CircleProgressView_progressColor, 0xff00ff00);
        mTextOneColor = typedArray.getColor(R.styleable.CircleProgressView_textColorOne, 0xffffffff);
        mTextTwoColor = typedArray.getColor(R.styleable.CircleProgressView_textColorTwo, 0xffffffff);
        mTextThreeColor = typedArray.getColor(R.styleable.CircleProgressView_textColorThree, 0xffffffff);
        mBackgroundInColor = typedArray.getColor(R.styleable.CircleProgressView_backgroundInColor, 0xff6950a1);
        //progress
        mProgress = typedArray.getInt(R.styleable.CircleProgressView_progress, 0);
        //orientation
        mIsClockwise = typedArray.getBoolean(R.styleable.CircleProgressView_isClockwise, false);
        //text bold
        mIsTextOneBold = typedArray.getBoolean(R.styleable.CircleProgressView_textBoldOne, false);
        mIsTextTwoBold = typedArray.getBoolean(R.styleable.CircleProgressView_textBoldTwo, false);
        mIsTextThreeBold = typedArray.getBoolean(R.styleable.CircleProgressView_textBoldThree, false);
    }

    private void initPaint() {
        mBigCirclePaint = new Paint();
        mBigCirclePaint.setAntiAlias(true);
        mSectorPaint = new Paint();
        mSectorPaint.setAntiAlias(true);
        mSmallCirclePaint = new Paint();
        mSmallCirclePaint.setAntiAlias(true);
        mTextPaint1 = new Paint();
        mTextPaint1.setAntiAlias(true);
        mTextPaint2 = new Paint();
        mTextPaint2.setAntiAlias(true);
        mTextPaint3 = new Paint();
        mTextPaint3.setAntiAlias(true);
        mTextPaint1.setFakeBoldText(mIsTextOneBold);
        mTextPaint2.setFakeBoldText(mIsTextTwoBold);
        mTextPaint3.setFakeBoldText(mIsTextThreeBold);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {

            mRadius = Math.min(widthSize,heightSize)/2;
            mWidth = mRadius*2;
            mHeight = mRadius*2;
            x = mRadius;
            y = mRadius;
        }

        if(widthMode == MeasureSpec.AT_MOST&&heightMode == MeasureSpec.AT_MOST){
            mWidth = mRadius*2;
            mHeight = mRadius*2;
            x = mRadius;
            y = mRadius;


        }
        setMeasuredDimension(mWidth,mHeight);
        rect = new RectF(0, 0, mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mCurProgress = (int) (mProgress * 3.6);

        mBigCirclePaint.setColor(mBackgroundColor);
        canvas.drawCircle(x, y, mRadius, mBigCirclePaint);

        mSectorPaint.setColor(mProgressColor);
        canvas.drawArc(rect, 270, mCurProgress, true, mSectorPaint);

        mSmallCirclePaint.setColor(mBackgroundInColor);
        canvas.drawCircle(x,y,mRadius - mStripeWidth, mSmallCirclePaint);

        mTextPaint1.setTextSize(mTxtSize1);
        textLength1 = mTextPaint1.measureText(mTextOne);
        txtHeight1 = getTxtHeight(mTextPaint1);
        mTextPaint1.setColor(mTextOneColor);

        mTextPaint2.setTextSize(mTxtSize2);
        mTextPaint2.setColor(mTextTwoColor);

        mTextPaint3.setTextSize(mTxtSize3);
        textLength3 = mTextPaint3.measureText(mTextThree);
        txtHeight3 = getTxtHeight(mTextPaint3);
        mTextPaint3.setColor(mTextThreeColor);

        y1 = (float) (y - ((txtHeight1 + txtHeight3) / 2 - txtHeight1));
        y2 = (float) (y1 + txtHeight3);
        canvas.drawText(mTextOne, x - textLength1 /2, y1, mTextPaint1);
        canvas.drawText(mTextThree, x - textLength3 /2, y2, mTextPaint3);
        canvas.drawText(mTextTwo, (x - textLength1 /2) + textLength1, y1, mTextPaint2);
    }

    public double getTxtHeight(Paint mPaint) {
        Paint.FontMetrics fm = mPaint.getFontMetrics();
        return fm.descent - fm.ascent;
    }

    public synchronized void setProgress(int progress, String textOne, String textTwo, String textThree) {
        if (mProgress > 100){
            throw new IllegalArgumentException("Progress must less then 100");
        }
        if (mIsClockwise){
            this.mProgress = progress;
        }else {
            this.mProgress = -progress;
        }
        this.mTextOne = textOne;
        this.mTextTwo = textTwo;
        this.mTextThree = textThree;
        postInvalidate();
    }

}
