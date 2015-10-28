package com.example.tao.customimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.lang.reflect.Type;

/**
 * Created by tao on 2015/10/28.
 */
public class CustomImageView extends View {

    private Bitmap mImage;

    private int mImageScale;
    private static final int IMAGE_SCALE_FILLXY = 0;
    private static final int IMAGE_SCALE_CENTER = 1;

    private String mTitleText;

    private int mTitleTextColor;

    private int mTitleTextSize;

    private Paint mPaint;

    //整个view的布局
    private Rect rect;

    //约束文字
    private Rect mTextBound;

    private int mWidth;

    private int mHeight;

    public CustomImageView(Context context) {
        this(context, null);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 获取到自定义的所有属性
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public CustomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.CustomImageView, defStyleAttr, 0);
        mImage = BitmapFactory.decodeResource(getResources()
                , typedArray.getResourceId(R.styleable.CustomImageView_image, 0));
        mImageScale = typedArray.getInt(R.styleable.CustomImageView_imageScaleType, 0);
        mTitleText = typedArray.getString(R.styleable.CustomImageView_titleText);
        mTitleTextColor = typedArray.getInt(R.styleable.CustomImageView_titleTextcolor
                , Color.BLACK);
        mTitleTextSize = typedArray.getDimensionPixelSize(R.styleable.CustomImageView_titleTextSize
                , (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16
                , getResources().getDisplayMetrics()));
        typedArray.recycle();

        mPaint = new Paint();
        rect = new Rect();
        mTextBound = new Rect();
        mPaint.setTextSize(mTitleTextSize);
        //计算描述文字需要的范围
        mPaint.getTextBounds(mTitleText, 0, mTitleText.length(), mTextBound);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        } else {
            int desiredByImg = getPaddingLeft() + getPaddingRight() + mImage.getWidth();
            int desiredByTitle = getPaddingLeft() + getPaddingRight() + mTextBound.width();
            if (widthMode == MeasureSpec.AT_MOST) {
                int desired = Math.max(desiredByImg, desiredByTitle);
                mWidth = Math.min(desired, widthSize);
            }
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        } else {
            int desired = getPaddingTop() + getPaddingBottom() + mImage.getHeight()
                    + mTextBound.height();
            if (heightMode == MeasureSpec.AT_MOST) {
                mHeight = Math.min(desired, heightSize);
            }
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //边框
        mPaint.setStrokeWidth(4);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.CYAN);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);

        rect.left = getPaddingLeft();
        rect.right = mWidth - getPaddingRight();
        rect.top = getPaddingTop();
        rect.bottom = mHeight - getPaddingBottom();

        mPaint.setColor(mTitleTextColor);
        mPaint.setStyle(Paint.Style.FILL);

        if (mTextBound.width() > mWidth) {
            TextPaint paint = new TextPaint(mPaint);
            String msg = TextUtils.ellipsize(mTitleText, paint
                    , (float) mWidth - getPaddingLeft() - getPaddingRight()
                    , TextUtils.TruncateAt.END).toString();
            canvas.drawText(msg, getPaddingLeft(), mHeight - getPaddingBottom(), mPaint);
        } else {
            canvas.drawText(mTitleText, mWidth / 2 - mTextBound.width() * 1.0f / 2
                    , mHeight - getPaddingBottom(), mPaint);
        }

        rect.bottom -= mTextBound.height();

        if (mImageScale == IMAGE_SCALE_FILLXY) {
            canvas.drawBitmap(mImage, null ,rect, mPaint);
        } else {
            rect.left = mWidth / 2 - mImage.getWidth() / 2;
            rect.right = mWidth / 2 + mImage.getWidth() / 2;
            rect.top = (mHeight - mTextBound.height()) / 2 - mImage.getHeight() / 2;
            rect.bottom = (mHeight - mTextBound.height()) / 2 + mImage.getHeight() / 2;

            canvas.drawBitmap(mImage, null, rect, mPaint);
        }
    }
}
