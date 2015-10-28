package com.example.tao.customview_1;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by tao on 2015/10/28.
 */
public class CustomView extends View {

    private String mTitleText;//文本
    private int mTitleTextColor;//文本的颜色
    private int mTitleTextSize;//文本字体的大小

    /**
     * 绘制的时候控制绘制文本的范围
     */
    private Rect mBound;
    private Paint mPaint;

    public CustomView(Context context) {
        this(context, null);
    }

    public CustomView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 获取自己定义的样式属性
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取自己定义的样式属性
        TypedArray a = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.CustomTextView, defStyleAttr, 0);
        mTitleText = a.getString(R.styleable.CustomTextView_titleText);
        //默认的颜色是黑色
        mTitleTextColor = a.getColor(R.styleable.CustomTextView_titleTextcolor, Color.BLACK);
        //默认设置为16sp，TypeValue也可以把sp转化为px
        mTitleTextSize = a.getDimensionPixelSize(R.styleable.CustomTextView_titleTextSize
                , (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP
                , 16, getResources().getDisplayMetrics()));
        a.recycle();

        /**
         * 获得绘制文本的宽和高
         */
        mPaint = new Paint();
        mPaint.setTextSize(mTitleTextSize);
        mBound = new Rect();
        mPaint.getTextBounds(mTitleText, 0, mTitleText.length(), mBound);

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mTitleText = randomText();
                //重绘
                postInvalidate();
            }
        });

    }

    private String randomText() {
        Random random = new Random();
        Set<Integer> set = new HashSet<>();
        while (set.size() < 4) {
            int randomInt = random.nextInt(10);
            set.add(randomInt);
        }
        //StringBuilder比StringBufferY要快
        StringBuilder sb = new StringBuilder();
        for (Integer i : set) {
            sb.append("" + i);
        }
        return sb.toString();
    }

    /**
     * MeasureSpec的specMode一共三种类型：
     * EXACTLY：一般是设置了明确的值或者是MATCH_PARENT
     * AT_MOST：表示子布局限制在一个最大值内，一般为WARP_CONTENT
     * UNSPECIFIED：表示子布局想要多大就多大，很少使用
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            mPaint.setTextSize(mTitleTextSize);
            mPaint.getTextBounds(mTitleText, 0, mTitleText.length(), mBound);
            float textWidth = mBound.width();
            int desired = (int) (getPaddingLeft() + textWidth + getPaddingRight());
            width = desired;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            mPaint.setTextSize(mTitleTextSize);
            mPaint.getTextBounds(mTitleText, 0, mTitleText.length(), mBound);
            float textHeight = mBound.height();
            int desired = (int) (getPaddingTop() + textHeight + getPaddingBottom());
            height = desired;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        /**
         * 首先设置画笔颜色为黄色，绘制背景
         * 再设置画笔颜色为用户定义的颜色，绘制文字
         */
        mPaint.setColor(Color.YELLOW);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);

        mPaint.setColor(mTitleTextColor);
        //drawText的坐标位置信息可参考 http://blog.csdn.net/sirnuo/article/details/21165665
        canvas.drawText(mTitleText, getWidth() / 2 - mBound.width() / 2,
                getHeight() / 2 + mBound.height() / 2, mPaint);
    }
}
