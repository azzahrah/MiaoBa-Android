package cn.nodemedia.library.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import cn.nodemedia.library.R;

/**
 * 聊天气泡裁剪Layout
 * Created by Bining.
 */
public class BubbleLinearLayout extends LinearLayout {

    private BubbleDrawable bubbleDrawable;

    private float mAngle;
    private int bubbleColor;
    private float mArrowHypotenuse;
    private float mArrowPosition;
    private int mArrowLocation;
    private float mStrokeWidth;
    private int mStrokeColor;

    public BubbleLinearLayout(Context context) {
        super(context);
        initView(null);
    }

    public BubbleLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            setUp(w, h);
        }
    }

    private void setUp(int width, int height) {
        setUp(getPaddingLeft(), width - getPaddingRight(), getPaddingTop(), height - getPaddingBottom());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(bubbleDrawable);
        } else {
            setBackgroundDrawable(bubbleDrawable);
        }
    }

    private void setUp(int left, int right, int top, int bottom) {
        if (right < left || bottom < top)
            return;

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

}
