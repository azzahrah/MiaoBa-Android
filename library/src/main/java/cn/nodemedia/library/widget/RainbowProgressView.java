package cn.nodemedia.library.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import cn.nodemedia.library.utils.ScreenUtils;

/***
 * 彩虹进度条
 * Created by Bining.
 */
public class RainbowProgressView extends View {

    /**
     * 分段颜色
     */
//    private static final int[] SECTION_COLORS = {0xFF00E1E3, 0xFFDEF200, 0xFFF49200, 0xFFF40000};
    private static final int[] SECTION_COLORS = {0xFF000000};

    /**
     * 分段位置
     */
//    private static final float[] SECTION_POSITIONS = {0f, 1f / 3, 2f / 3, 1f};
    private static final float[] SECTION_POSITIONS = {0f};

    /**
     * 进度条最大值
     */
    private float maxCount = 100;

    /**
     * 进度条当前值
     */
    private float currentCount = 50;

    private int lineBoxColor = 0xFF7E7E7E;

    private Paint mPaint;
    private int mWidth, mHeight, mRadius;

    public RainbowProgressView(Context context) {
        super(context);
        initView();
    }

    public RainbowProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public RainbowProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//使位图抗锯齿的标志
        mPaint.setStrokeCap(Paint.Cap.ROUND);//当画笔样式为STROKE或FILL_OR_STROKE时，设置笔刷的图形样式，如圆形样式 Cap.ROUND,或方形样式Cap.SQUARE
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        RectF rectF = new RectF(0, 0, mWidth, mHeight);

        LinearGradient shader = new LinearGradient(0, 0, mWidth, mHeight, SECTION_COLORS, SECTION_POSITIONS, Shader.TileMode.MIRROR);
        mPaint.setShader(shader);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(rectF, mRadius, mRadius, mPaint);

        mPaint.setShader(null);
        if (maxCount > 0 && currentCount < maxCount) {
            mPaint.setColor(Color.WHITE);
            rectF.left = (int) (currentCount * mWidth / maxCount);
            canvas.drawRect(rectF, mPaint);
        }

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(lineBoxColor);
        rectF.left = 0;
        canvas.drawRoundRect(rectF, mRadius, mRadius, mPaint);
    }

    /***
     * 设置最大的进度值
     */
    public void setMaxCount(float maxCount) {
        this.maxCount = maxCount;
    }

    public float getMaxCount() {
        return maxCount;
    }

    /***
     * 设置当前的进度值
     */
    public void setCurrentCount(float currentCount) {
        this.currentCount = currentCount > maxCount ? maxCount : currentCount;
        invalidate();
    }

    public float getCurrentCount() {
        return currentCount;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //UNSPECIFIED(未指定),父元素部队自元素施加任何束缚，子元素可以得到任意想要的大小；
        //EXACTLY(完全)，父元素决定自元素的确切大小，子元素将被限定在给定的边界里而忽略它本身大小；
        //AT_MOST(至多)，子元素至多达到指定大小的值。

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthSpecMode == MeasureSpec.AT_MOST || widthSpecMode == MeasureSpec.UNSPECIFIED) {
            mWidth = ScreenUtils.dp2px(68);
        } else {
            mWidth = MeasureSpec.getSize(widthMeasureSpec);
        }

        if (heightSpecMode == MeasureSpec.AT_MOST || heightSpecMode == MeasureSpec.UNSPECIFIED) {
            mHeight = ScreenUtils.dp2px(5);
        } else {
            mHeight = MeasureSpec.getSize(heightMeasureSpec);
        }

        mRadius = mHeight / 2;

        setMeasuredDimension(mWidth, mHeight);
    }

}
