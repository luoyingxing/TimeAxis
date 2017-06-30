package com.luo.timeaxis;

import android.content.Context;
import android.content.res.TypedArray;
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
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TimeAxisView, 0, 0);
        float density = getResources().getDisplayMetrics().density;

        mTextSize = array.getDimensionPixelSize(R.styleable.TimeAxisView_textSize, (int) (14 * density));
        mCircleRadius = array.getDimensionPixelSize(R.styleable.TimeAxisView_radius, (int) (13 * density));
        mCircleStrokeWidth = array.getDimensionPixelSize(R.styleable.TimeAxisView_strokeWidth, (int) (1 * density));
        mCircleTextSpace = array.getDimensionPixelSize(R.styleable.TimeAxisView_circleTextSpace, (int) (1 * density));
        mInterval = array.getDimensionPixelSize(R.styleable.TimeAxisView_interval, (int) (3 * density));
        mLineStrokeWidth = array.getDimensionPixelSize(R.styleable.TimeAxisView_lineStrokeWidth, (int) (1 * density));

        mOrientation = array.getInt(R.styleable.TimeAxisView_orientation, 1);

        mCircleColor = array.getColor(R.styleable.TimeAxisView_circleColor, 0xff666666);
        mTextColor = array.getColor(R.styleable.TimeAxisView_textColor, 0xff666666);

        array.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mTextPaint = new TextPaint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTextAlign(TextPaint.Align.CENTER);

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
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    public static int getDefaultSize(int size, int measureSpec) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED: //match_parent
                result = size;
                break;
            case MeasureSpec.AT_MOST: //wrap_content
            case MeasureSpec.EXACTLY: //xx dp
                result = specSize;
                break;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null == mTextList || 0 == mTextList.size()) {
            return;
        }

        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();

        int width = getMeasuredWidth() - paddingLeft - paddingRight;

        int count = mTextList.size();

        if (count * mCircleRadius * 2 > width) {
            try {
                throw new IllegalStateException("The width of all circles is exceed screen width! please try put it in HorizontalScrollView");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(mTextSize);

        if (HORIZONTAL == mOrientation) {
            int perWidth = width / count;

            float circleX = perWidth / 2;
            float cx;

            for (int i = 0; i < count; i++) {
                //画圆圈
                mPaint.setColor(mCircleColor);
                mPaint.setStrokeWidth(mCircleStrokeWidth);

                if (i == 0 || i == count - 1) {
                    mPaint.setStyle(Paint.Style.FILL);
                } else {
                    mPaint.setStyle(Paint.Style.STROKE);
                }
                cx = circleX + i * perWidth;
                canvas.drawCircle(cx, paddingTop + mCircleRadius, mCircleRadius, mPaint);

                //画文字
                String text = mTextList.get(i);
                Rect bounds = new Rect();
                mTextPaint.getTextBounds(text, 0, text.length(), bounds);
                StaticLayout layout = new StaticLayout(text, mTextPaint, perWidth, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
                canvas.save();
                canvas.translate(cx, paddingTop + mCircleRadius * 2 + mCircleTextSpace + bounds.height() / 2);
                layout.draw(canvas);
                canvas.restore();

                //画水平线
                if (0 != i) {
                    mPaint.setColor(mCircleColor);
                    mPaint.setStrokeWidth(mLineStrokeWidth);
                    canvas.drawLine(cx - perWidth + mCircleRadius + mInterval, paddingTop + mCircleRadius,
                            cx - mCircleRadius - mInterval, paddingTop + mCircleRadius, mPaint);
                }
            }
        } else if (VERTICAL == mOrientation) {
            //TODO 竖直方向
        }
    }

    private void mLog(Object obj) {
        Log.v("TimeAxisView", obj + "");
    }
}