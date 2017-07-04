package com.luo.timeaxis;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TimeAxisView 时光轴
 * <p>
 * Created by luoyingxing on 2017/6/23.
 */

public class TimeAxisView extends View {
    private Context mContext;
    public static final int HORIZONTAL = 1;
    public static final int VERTICAL = 2;
    //画笔
    private Paint mPaint;
    //文字画笔
    private TextPaint mTextPaint;
    //时光轴的方向 水平/竖直
    private int mOrientation;
    //时光轴的文字
    private List<String> mTextList;
    //字体大小
    private float mTextSize = 16f;
    //圆圈的半径
    private int mCircleRadius;
    //文字与圆圈之间的间隔
    private int mCircleTextSpace;
    //圆圈的颜色
    private int mCircleColor;
    //文字颜色
    private int mTextColor;
    //圆圈的边宽
    private int mCircleStrokeWidth;
    //文字的粗细大小
    private int mLineStrokeWidth;
    //圆圈与横线的间隔
    private int mInterval;
    //圆圈的图片
    private int mCircleDrawwableRescourse;

    private static final int FACTOR_VERTICAL = 8;
    private static final int FACTOR_HORIZONTAL = 1;
    private int mDefaultPaddingTop;
    private int mDefaultPaddingBottom;
    private int mDefaultPaddingLeft;
    private int mDefaultPaddingRight;

    public TimeAxisView(Context context) {
        this(context, null);
    }

    public TimeAxisView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeAxisView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TimeAxisView, 0, 0);
        float density = getResources().getDisplayMetrics().density;

        mTextSize = array.getDimensionPixelSize(R.styleable.TimeAxisView_textSize, (int) (14 * density));
        mCircleRadius = array.getDimensionPixelSize(R.styleable.TimeAxisView_radius, (int) (13 * density));
        mCircleStrokeWidth = array.getDimensionPixelSize(R.styleable.TimeAxisView_strokeWidth, (int) (1 * density));
        mCircleTextSpace = array.getDimensionPixelSize(R.styleable.TimeAxisView_circleTextSpace, (int) (1 * density));
        mInterval = array.getDimensionPixelSize(R.styleable.TimeAxisView_interval, (int) (3 * density));
        mLineStrokeWidth = array.getDimensionPixelSize(R.styleable.TimeAxisView_lineStrokeWidth, (int) (1 * density));

        mOrientation = array.getInt(R.styleable.TimeAxisView_orientation, 1);

        mCircleDrawwableRescourse = array.getResourceId(R.styleable.TimeAxisView_drawable, 0);

        mCircleColor = array.getColor(R.styleable.TimeAxisView_circleColor, 0xff666666);
        mTextColor = array.getColor(R.styleable.TimeAxisView_textColor, 0xff666666);

        array.recycle();

        mDefaultPaddingTop = (int) (FACTOR_VERTICAL * density);
        mDefaultPaddingBottom = (int) (FACTOR_VERTICAL * density);
        mDefaultPaddingLeft = (int) (FACTOR_HORIZONTAL * density);
        mDefaultPaddingRight = (int) (FACTOR_HORIZONTAL * density);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(mTextSize);

        mTextPaint = new TextPaint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTextAlign(TextPaint.Align.CENTER);

        //TODO 测试数据
        String text[] = {"李白", "陶渊明", "杜甫", "王维", "李煜"};
        mTextList = new ArrayList<>();
        mTextList.addAll(Arrays.asList(text));
        //TODO 测试数据
    }

    public void setTextList(List<String> list) {
        mTextList = list;
        requestLayout();
    }

    public void setTextList(String... string) {
        mTextList = Arrays.asList(string);
        requestLayout();
    }

    public void addTextList(List<String> list) {
        if (null == mTextList) {
            mTextList = new ArrayList<>();
        }
        mTextList.addAll(list);
        requestLayout();
    }

    public void addTextList(String... string) {
        if (null == mTextList) {
            mTextList = new ArrayList<>();
        }
        mTextList.addAll(Arrays.asList(string));
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = 0;
        int height = 0;

        int specModeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int specSizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int specModeHeight = MeasureSpec.getMode(heightMeasureSpec);
        int specSizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        switch (specModeWidth) {
            case MeasureSpec.UNSPECIFIED:
                width = getSuggestedMinimumWidth();
                break;
            case MeasureSpec.AT_MOST:
                width = getDefaultWidth();
                break;
            case MeasureSpec.EXACTLY:
                width = specSizeWidth;
                break;
        }

        switch (specModeHeight) {
            case MeasureSpec.UNSPECIFIED:
                height = getSuggestedMinimumHeight();
                break;
            case MeasureSpec.AT_MOST:
                height = getDefaultHeight(width);
                break;
            case MeasureSpec.EXACTLY:
                height = specSizeHeight;
                break;
        }

        setMeasuredDimension(width, height);
    }

    /**
     * 计算wrap_content 模式时的view宽度
     *
     * @return wrap_content 宽度
     */
    private int getDefaultWidth() {
        if (null != mTextList) {
            //所有圆圈的宽度 + 所有的间隔 + 默认左右边距
            return mTextList.size() * 2 * mCircleRadius * 2 + getPaddingLeft() + getPaddingRight() + mDefaultPaddingLeft + mDefaultPaddingRight;
        }
        return 0;
    }

    /**
     * 计算wrap_content 模式时的view高度
     *
     * @param width 根据宽度计算每个圆圈的宽度区域
     * @return wrap_content 高度
     */
    private int getDefaultHeight(int width) {
        if (null != mTextList) {
            //圆圈的高度 + 文字的高度 + 默认上下边距
            return mCircleRadius * 2 + mInterval + getTextHeight(width) + getPaddingTop() + getPaddingBottom() + mDefaultPaddingTop + mDefaultPaddingBottom;
        }
        return 0;
    }

    /**
     * 计算文字的最大高度
     *
     * @param width 根据View的宽度来计算每段文字的宽度
     * @return 文字的最大高度
     */
    private int getTextHeight(int width) {
        int count = mTextList.size();
        String text = "";
        for (String str : mTextList) {
            if (text.length() < str.length()) {
                text = str;
            }
        }
        int perWidth = width / count;
        Rect bounds = new Rect();
        TextPaint paint = new TextPaint();
        paint.setTextSize(mTextSize);
        paint.getTextBounds(text, 0, text.length(), bounds);
        StaticLayout layout = new StaticLayout(text, paint, perWidth, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
        return layout.getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null == mTextList || 0 == mTextList.size()) {
            return;
        }

        if (HORIZONTAL == mOrientation) {
            drawHorizontal(canvas);
        } else if (VERTICAL == mOrientation) {
            drawVertical(canvas);
        }
    }

    private void drawHorizontal(Canvas canvas) {
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();

        int width = getMeasuredWidth() - mDefaultPaddingRight - mDefaultPaddingRight - paddingLeft - paddingRight;
        int count = mTextList.size();

        int perWidth = width / count;

        float circleX = perWidth / 2;
        float cx;

        Bitmap bitmap = null;

        if (0 != mCircleDrawwableRescourse) {
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), mCircleDrawwableRescourse);
        }

        for (int i = 0; i < count; i++) {
            //圆圈
            mPaint.setColor(mCircleColor);
            mPaint.setStrokeWidth(mCircleStrokeWidth);

            if (null != bitmap) {
                cx = circleX + i * perWidth;

                Rect rect = new Rect();
                rect.left = (int) (cx + mDefaultPaddingLeft + paddingLeft - mCircleRadius);
                rect.top = mDefaultPaddingTop + paddingTop;
                rect.right = (int) (cx + mDefaultPaddingLeft + paddingLeft + mCircleRadius);
                rect.bottom = mDefaultPaddingTop + paddingTop + mCircleRadius * 2;

                canvas.drawBitmap(bitmap, null, rect, mPaint);

            } else {
                if (0 == i || count - 1 == i) {
                    mPaint.setStyle(Paint.Style.FILL);
                } else {
                    mPaint.setStyle(Paint.Style.STROKE);
                }
                cx = circleX + i * perWidth;
                canvas.drawCircle(cx + mDefaultPaddingLeft + paddingLeft, mDefaultPaddingTop + paddingTop + mCircleRadius, mCircleRadius, mPaint);
            }

            //文字
            String text = mTextList.get(i);
            Rect bounds = new Rect();
            mTextPaint.getTextBounds(text, 0, text.length(), bounds);
            StaticLayout layout = new StaticLayout(text, mTextPaint, perWidth, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
            canvas.save();
            canvas.translate(cx + mDefaultPaddingLeft + paddingLeft, mDefaultPaddingTop + paddingTop + mCircleRadius * 2 + mCircleTextSpace + bounds.height() / 2);
            layout.draw(canvas);
            canvas.restore();

            //水平线
            if (0 != i) {
                mPaint.setColor(mCircleColor);
                mPaint.setStrokeWidth(mLineStrokeWidth);
                canvas.drawLine(cx + mDefaultPaddingLeft + paddingLeft - perWidth + mCircleRadius + mInterval,
                        mDefaultPaddingTop + paddingTop + mCircleRadius,
                        cx + mDefaultPaddingLeft + paddingLeft - mCircleRadius - mInterval,
                        mDefaultPaddingTop + paddingTop + mCircleRadius, mPaint);
            }
        }

        if (null != bitmap) {
            bitmap.recycle();
        }
    }

    private void drawVertical(Canvas canvas) {

    }

    private void mLog(Object obj) {
        Log.i("TimeAxisView", obj + "");
    }
}