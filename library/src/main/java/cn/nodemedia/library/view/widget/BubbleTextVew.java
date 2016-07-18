package cn.nodemedia.library.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.TextView;

import cn.nodemedia.library.R;

/**
 * 聊天气泡裁剪文字
 * Created by Bining.
 */
public class BubbleTextVew extends TextView {

    private BubbleDrawable bubbleDrawable;
    private float mAngle;
    private int bubbleColor;
    private float mArrowHypotenuse;
    private float mArrowPosition;
    private int mArrowLocation;
    private float mStrokeWidth;
    private int mStrokeColor;

    public BubbleTextVew(Context context) {
        super(context);
        initView(null);
    }

    public BubbleTextVew(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public BubbleTextVew(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.BubbleView);
            mAngle = array.getDimension(R.styleable.BubbleView_angle, BubbleDrawable.Builder.DEFAULT_ANGLE);
            bubbleColor = array.getColor(R.styleable.BubbleView_bubbleColor, BubbleDrawable.Builder.DEFAULT_BUBBLE_COLOR);
            mArrowHypotenuse = array.getDimension(R.styleable.BubbleView_arrowHypotenuse, BubbleDrawable.Builder.DEFAULT_ARROW_HYPOT);
            mArrowPosition = array.getDimension(R.styleable.BubbleView_arrowPosition, BubbleDrawable.Builder.DEFAULT_ARROW_POSITION);
            mArrowLocation = array.getInt(R.styleable.BubbleView_arrowLocation, BubbleDrawable.Builder.ARROW_LEFT);
            mStrokeWidth = array.getDimension(R.styleable.BubbleView_strokeWidth, BubbleDrawable.Builder.DEFAULT_STROKE_WIDTH);
            mStrokeColor = array.getColor(R.styleable.BubbleView_strokeColor, BubbleDrawable.Builder.DEFAULT_STROKE_COLOR);
            array.recycle();
        }
        setUpPadding();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            setUp(w, h);
        }
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        super.layout(l, t, r, b);
        setUp();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bubbleDrawable != null)
            bubbleDrawable.draw(canvas);
        super.onDraw(canvas);
    }

    private void setUp(int width, int height) {
        setUp(0, width, 0, height);
    }

    private void setUp() {
        setUp(getWidth(), getHeight());
    }

    private void setUp(int left, int right, int top, int bottom) {
        bubbleDrawable = new BubbleDrawable.Builder()
                .rect(new RectF(left, top, right, bottom))
                .angle(mAngle)
                .bubbleColor(bubbleColor)
                .arrowHypotenuse(mArrowHypotenuse)
                .arrowPosition(mArrowPosition)
                .arrowLocation(mArrowLocation)
                .strokeWidth(mStrokeWidth)
                .strokeColor(mStrokeColor)
                .build();
    }

    private void setUpPadding() {
        int left = getPaddingLeft();
        int right = getPaddingRight();
        int top = getPaddingTop();
        int bottom = getPaddingBottom();
        switch (mArrowLocation) {
            case BubbleDrawable.Builder.ARROW_LEFT:
                left += mArrowHypotenuse / 2;
                break;
            case BubbleDrawable.Builder.ARROW_RIGHT:
                right += mArrowHypotenuse / 2;
                break;
            case BubbleDrawable.Builder.ARROW_TOP:
                top += mArrowHypotenuse / 2;
                break;
            case BubbleDrawable.Builder.ARROW_BOTTOM:
                bottom += mArrowHypotenuse / 2;
                break;
        }
        setPadding(left, top, right, bottom);
    }

}
