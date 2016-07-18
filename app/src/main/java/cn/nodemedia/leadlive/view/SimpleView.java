package cn.nodemedia.leadlive.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * 示例
 * Created by Bining on 16/7/5.
 */
public class SimpleView extends View {

    private float mAngleRadius = 50;
    private float mAngleDiameter;
    private float mArrowWidth;
    private float mArrowHeight = 100;
    private float mArrowPosition = 100;
    private float mArrowRadius;
    private float mArcOffset;
    private float mArrowOffset;
    private float mPointcutsHeight;

    public SimpleView(Context context) {
        super(context);
    }

    public SimpleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        this.mAngleDiameter = this.mAngleRadius * 2;
        this.mArrowWidth = this.mArrowHeight / 2;
        this.mArrowRadius = this.mArrowWidth / 4;
        this.mArcOffset = (float) Math.tan(22.5) * this.mArrowRadius;
        this.mArrowOffset = (float) Math.sqrt(Math.pow(this.mArrowRadius, 2) * 2);
        this.mPointcutsHeight = (float) Math.sqrt(Math.pow(this.mArrowRadius, 2) / 2);

        Paint mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setColor(Color.RED);
        mStrokePaint.setStyle(Paint.Style.FILL);
        mStrokePaint.setAntiAlias(true);
        Path mStrokePath = new Path();
        setUpLeftPath(new RectF(5, 5, 400, 400), mStrokePath);
        canvas.drawPath(mStrokePath, mStrokePaint);

        Paint mStrokePaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint1.setColor(Color.BLUE);
        mStrokePaint1.setStrokeWidth(5);
        mStrokePaint1.setStyle(Paint.Style.STROKE);
        mStrokePaint1.setAntiAlias(true);
        Path mStrokePath1 = new Path();
        setUpLeftPath(new RectF(5, 5, 400, 400), mStrokePath1);
        canvas.drawPath(mStrokePath1, mStrokePaint1);

        Paint mStrokePaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint2.setColor(Color.RED);
        mStrokePaint2.setStyle(Paint.Style.FILL);
        mStrokePaint2.setAntiAlias(true);
        Path mStrokePath2 = new Path();
        setUpBottomPath(new RectF(5, 455, 400, 850), mStrokePath2);
        canvas.drawPath(mStrokePath2, mStrokePaint2);

    }

    private void setUpLeftPath(RectF rect, Path path) {
        path.moveTo(rect.left + mArrowWidth + mAngleRadius, rect.top);
        path.lineTo(rect.right - mAngleRadius, rect.top);
        path.arcTo(new RectF(rect.right - mAngleDiameter, rect.top, rect.right, rect.top + mAngleDiameter), 270, 90);
        path.lineTo(rect.right, rect.bottom - mAngleRadius);
        path.arcTo(new RectF(rect.right - mAngleDiameter, rect.bottom - mAngleDiameter, rect.right, rect.bottom), 0, 90);
        path.lineTo(rect.left + mArrowWidth + mAngleRadius, rect.bottom);
        path.arcTo(new RectF(rect.left + mArrowWidth, rect.bottom - mAngleDiameter, rect.left + mArrowWidth + mAngleDiameter, rect.bottom), 90, 90);
        path.lineTo(rect.left + mArrowWidth, rect.top + mArrowPosition + mArrowHeight + mArcOffset);
        path.arcTo(new RectF(rect.left + mArrowWidth - mArrowRadius * 2,
                rect.top + mArrowPosition + mArrowHeight + mArcOffset - mArrowRadius,
                rect.left + mArrowWidth,
                rect.top + mArrowPosition + mArrowHeight + mArcOffset + mArrowRadius), 0, -45);
        path.lineTo(rect.left + mArrowOffset / 2, rect.top + mArrowPosition + mArrowHeight / 2 + mArrowOffset / 2);
        path.arcTo(new RectF(rect.left + mArrowOffset - mArrowRadius,
                rect.top + mArrowPosition + mArrowHeight / 2 - mArrowRadius,
                rect.left + mArrowOffset + mArrowRadius,
                rect.top + mArrowPosition + mArrowHeight / 2 + mArrowRadius), 135, 90);
        path.lineTo(rect.left + mArrowWidth - mArrowRadius + mPointcutsHeight, rect.top + mArrowPosition - mArcOffset + mPointcutsHeight);
        path.arcTo(new RectF(rect.left + mArrowWidth - mArrowRadius * 2,
                rect.top + mArrowPosition - mArcOffset - mArrowRadius,
                rect.left + mArrowWidth,
                rect.top + mArrowPosition - mArcOffset + mArrowRadius), 45, -45);
        path.lineTo(rect.left + mArrowWidth, rect.top + mAngleRadius);
        path.arcTo(new RectF(rect.left + mArrowWidth, rect.top, rect.left + mArrowWidth + mAngleDiameter, rect.top + mAngleDiameter), 180, 90);
        path.close();
    }

    private void setUpRightPath(RectF rect, Path path) {
        path.moveTo(rect.left + mAngleRadius, rect.top);
        path.lineTo(rect.right - mAngleRadius - mArrowWidth, rect.top);
        path.arcTo(new RectF(rect.right - mAngleDiameter - mArrowWidth, rect.top, rect.right - mArrowWidth, rect.top + mAngleDiameter), 270, 90);
        path.lineTo(rect.right - mArrowWidth, rect.top + mArrowPosition - mArcOffset);
        path.arcTo(new RectF(rect.right - mArrowWidth,
                rect.top + mArrowPosition - mArcOffset - mArrowRadius,
                rect.right - mArrowWidth + mArrowRadius * 2,
                rect.top + mArrowPosition - mArcOffset + mArrowRadius), 180, -45);
        path.lineTo(rect.right - mArrowOffset / 2, rect.top + mArrowPosition + mArrowHeight / 2 - mArrowOffset / 2);
        path.arcTo(new RectF(rect.right - mArrowOffset - mArrowRadius,
                rect.top + mArrowPosition + mArrowHeight / 2 - mArrowRadius,
                rect.right - mArrowOffset + mArrowRadius,
                rect.top + mArrowPosition + mArrowHeight / 2 + mArrowRadius), 315, 90);
        path.lineTo(rect.right - mArrowWidth + mArrowRadius - mPointcutsHeight, rect.top + mArrowPosition + mArrowHeight + mArcOffset - mPointcutsHeight);
        path.arcTo(new RectF(rect.right - mArrowWidth,
                rect.top + mArrowPosition + mArrowHeight + mArcOffset - mArrowRadius,
                rect.right - mArrowWidth + mArrowRadius * 2,
                rect.top + mArrowPosition + mArrowHeight + mArcOffset + mArrowRadius), 225, -45);
        path.lineTo(rect.right - mArrowWidth, rect.bottom - mAngleRadius);
        path.arcTo(new RectF(rect.right - mAngleDiameter - mArrowWidth, rect.bottom - mAngleDiameter, rect.right - mArrowWidth, rect.bottom), 0, 90);
        path.lineTo(rect.left + mAngleRadius, rect.bottom);
        path.arcTo(new RectF(rect.left, rect.bottom - mAngleDiameter, rect.left + mAngleDiameter, rect.bottom), 90, 90);
        path.lineTo(rect.left, rect.top + mAngleRadius);
        path.arcTo(new RectF(rect.left, rect.top, mAngleDiameter + rect.left, mAngleDiameter + rect.top), 180, 90);
        path.close();
    }

    private void setUpTopPath(RectF rect, Path path) {
        path.moveTo(rect.left + mAngleRadius, rect.top + mArrowWidth);

        path.lineTo(rect.left + mArrowPosition - mArcOffset, rect.top + mArrowWidth);

        path.arcTo(new RectF(rect.left + mArrowPosition - mArcOffset - mArrowRadius,
                rect.top + mArrowWidth - mArrowRadius * 2,
                rect.left + mArrowPosition - mArcOffset + mArrowRadius,
                rect.top + mArrowWidth), 90, -45);

        path.lineTo(rect.left + mArrowPosition + mArrowHeight / 2 - mArrowOffset / 2, rect.top + mArrowOffset / 2);

        path.arcTo(new RectF(rect.left + mArrowPosition + mArrowHeight / 2 - mArrowRadius,
                rect.top + mArrowOffset - mArrowRadius,
                rect.left + mArrowPosition + mArrowHeight / 2 + mArrowRadius,
                rect.top + mArrowOffset + mArrowRadius), 225, 90);

        path.lineTo(rect.left + mArrowPosition + mArrowHeight + mArcOffset - mPointcutsHeight, rect.top + mArrowWidth - mArrowRadius + mPointcutsHeight);

        path.arcTo(new RectF(rect.left + mArrowPosition + mArrowHeight + mArcOffset - mArrowRadius,
                rect.top + mArrowWidth - mArrowRadius * 2,
                rect.left + mArrowPosition + mArrowHeight + mArcOffset + mArrowRadius,
                rect.top + mArrowWidth), 135, -45);

        path.lineTo(rect.right - mAngleRadius, rect.top + mArrowWidth);
        path.arcTo(new RectF(rect.right - mAngleDiameter, rect.top + mArrowWidth, rect.right, mAngleDiameter + rect.top + mArrowWidth), 270, 90);
        path.lineTo(rect.right, rect.bottom - mAngleRadius);
        path.arcTo(new RectF(rect.right - mAngleDiameter, rect.bottom - mAngleDiameter, rect.right, rect.bottom), 0, 90);
        path.lineTo(rect.left + mAngleRadius, rect.bottom);
        path.arcTo(new RectF(rect.left, rect.bottom - mAngleDiameter, mAngleDiameter + rect.left, rect.bottom), 90, 90);
        path.lineTo(rect.left, rect.top + mArrowWidth + mAngleRadius);
        path.arcTo(new RectF(rect.left, rect.top + mArrowWidth, mAngleDiameter + rect.left, mAngleDiameter + rect.top + mArrowWidth), 180, 90);

        path.close();
    }

    private void setUpBottomPath(RectF rect, Path path) {
        path.moveTo(rect.left + mAngleRadius, rect.top);
        path.lineTo(rect.right - mAngleRadius, rect.top);
        path.arcTo(new RectF(rect.right - mAngleDiameter, rect.top, rect.right, mAngleDiameter + rect.top), 270, 90);
        path.lineTo(rect.right, rect.bottom - mArrowWidth - mAngleRadius);
        path.arcTo(new RectF(rect.right - mAngleDiameter, rect.bottom - mAngleDiameter - mArrowWidth, rect.right, rect.bottom - mArrowWidth), 0, 90);

        path.lineTo(rect.left + mArrowHeight + mArrowPosition + mArcOffset, rect.bottom - mArrowWidth);

        path.arcTo(new RectF(rect.left + mArrowPosition + mArrowHeight + mArcOffset - mArrowRadius,
                rect.bottom - mArrowWidth,
                rect.left + mArrowPosition + mArrowHeight + mArcOffset + mArrowRadius,
                rect.bottom - mArrowWidth + mArrowRadius * 2), 270, -45);

        path.lineTo(rect.left + mArrowPosition + mArrowHeight / 2 + mArrowOffset / 2, rect.bottom - mArrowOffset / 2);

        path.arcTo(new RectF(rect.left + mArrowPosition + mArrowHeight / 2 - mArrowRadius,
                rect.bottom - mArrowOffset - mArrowRadius,
                rect.left + mArrowPosition + mArrowHeight / 2 + mArrowRadius,
                rect.bottom - mArrowOffset + mArrowRadius), 45, 90);

        path.lineTo(rect.left + mArrowPosition - mArcOffset + mPointcutsHeight, rect.bottom - mArrowWidth + mArrowRadius - mPointcutsHeight);

        path.arcTo(new RectF(rect.left + mArrowPosition - mArcOffset - mArrowRadius,
                rect.bottom - mArrowWidth + mArrowRadius * 2,
                rect.left + mArrowPosition - mArcOffset + mArrowRadius,
                rect.bottom - mArrowWidth), 315, -45);

        path.lineTo(rect.left + mAngleRadius, rect.bottom - mArrowWidth);
        path.arcTo(new RectF(rect.left, rect.bottom - mAngleDiameter - mArrowWidth, mAngleDiameter + rect.left, rect.bottom - mArrowWidth), 90, 90);
        path.lineTo(rect.left, rect.top + mAngleRadius);
        path.arcTo(new RectF(rect.left, rect.top, mAngleDiameter + rect.left, mAngleDiameter + rect.top), 180, 90);
        path.close();
    }

}
