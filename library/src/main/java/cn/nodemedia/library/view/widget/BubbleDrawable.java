package cn.nodemedia.library.view.widget;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 聊天气泡裁剪图片
 * Created by Bining.
 */
public class BubbleDrawable extends Drawable {

    private RectF mRect;
    private Path mPath = new Path();
    private BitmapShader mBitmapShader;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float mAngleRadius;
    private float mAngleDiameter;
    private int bubbleColor;
    private Bitmap bubbleBitmap;
    private float mArrowHypotenuse;
    private float mArrowPosition;
    private int mArrowLocation;
    private float mArrowRadius;
    private float mArrowHeight;
    private float mArrowOffset;
    private float mArcOffset;
    private float mPointcutsHeight;
    private float mStrokeWidth;
    private int mStrokeColor;

    private BubbleDrawable(Builder builder) {
        this.mRect = builder.mRect;
        this.mAngleRadius = builder.mAngle;
        this.mAngleDiameter = builder.mAngle * 2;
        this.bubbleColor = builder.bubbleColor;
        this.bubbleBitmap = builder.bubbleBitmap;
        this.mArrowHypotenuse = builder.mArrowHypotenuse;
        this.mArrowHeight = builder.mArrowHypotenuse / 2;
        this.mArrowPosition = builder.mArrowPosition;
        this.mArrowLocation = builder.mArrowLocation;

        this.mArrowRadius = this.mArrowHeight / 4;
        this.mArcOffset = (float) Math.tan(22.5) * this.mArrowRadius;
        this.mArrowOffset = (float) Math.sqrt(Math.pow(this.mArrowRadius, 2) * 2);
        this.mPointcutsHeight = (float) Math.sqrt(Math.pow(this.mArrowRadius, 2) / 2);

        this.mStrokeWidth = builder.mStrokeWidth;
        this.mStrokeColor = builder.mStrokeColor;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public void draw(Canvas canvas) {
        setUp(canvas);
    }

    private void setUp(Canvas canvas) {
        if (bubbleBitmap == null) {
            mPaint.setColor(bubbleColor);
        } else {
            if (mBitmapShader == null) {
                mBitmapShader = new BitmapShader(bubbleBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            }
            mPaint.setShader(mBitmapShader);
            setUpShaderMatrix();
        }

        setUpPath(mArrowLocation, mPath);
        canvas.drawPath(mPath, mPaint);

        if (mStrokeWidth > 0) {
            Path mStrokePath = new Path();
            Paint mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mStrokePaint.setStyle(Paint.Style.STROKE);
            mStrokePaint.setStrokeWidth(mStrokeWidth);
            mStrokePaint.setColor(mStrokeColor);
            mStrokePaint.setAntiAlias(true);

            setUpPath(mArrowLocation, mStrokePath);
            canvas.drawPath(mStrokePath, mStrokePaint);
        }
    }

    private void setUpShaderMatrix() {
        float scale;
        Matrix mShaderMatrix = new Matrix();
        mShaderMatrix.set(null);
        int mBitmapWidth = bubbleBitmap.getWidth();
        int mBitmapHeight = bubbleBitmap.getHeight();
        float scaleX = getIntrinsicWidth() / (float) mBitmapWidth;
        float scaleY = getIntrinsicHeight() / (float) mBitmapHeight;
        scale = Math.min(scaleX, scaleY);
        mShaderMatrix.postScale(scale, scale);
        mShaderMatrix.postTranslate(mRect.left, mRect.top);
        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

    private void setUpPath(int mArrowLocation, Path path) {
        switch (mArrowLocation) {
            case Builder.ARROW_LEFT:
                setUpLeftPath(mRect, path);
                break;
            case Builder.ARROW_RIGHT:
                setUpRightPath(mRect, path);
                break;
            case Builder.ARROW_TOP:
                setUpTopPath(mRect, path);
                break;
            case Builder.ARROW_BOTTOM:
                setUpBottomPath(mRect, path);
                break;
        }
    }

    private void setUpLeftPath(RectF rect, Path path) {
        path.moveTo(rect.left + mArrowHeight + mAngleRadius, rect.top);
        path.lineTo(rect.right - mAngleRadius, rect.top);
        path.arcTo(new RectF(rect.right - mAngleDiameter, rect.top, rect.right, rect.top + mAngleDiameter), 270, 90);
        path.lineTo(rect.right, rect.bottom - mAngleRadius);
        path.arcTo(new RectF(rect.right - mAngleDiameter, rect.bottom - mAngleDiameter, rect.right, rect.bottom), 0, 90);
        path.lineTo(rect.left + mArrowHeight + mAngleRadius, rect.bottom);
        path.arcTo(new RectF(rect.left + mArrowHeight, rect.bottom - mAngleDiameter, rect.left + mArrowHeight + mAngleDiameter, rect.bottom), 90, 90);
        path.lineTo(rect.left + mArrowHeight, rect.top + mArrowPosition + mArrowHypotenuse + mArcOffset);
        path.arcTo(new RectF(rect.left + mArrowHeight - mArrowRadius * 2,
                rect.top + mArrowPosition + mArrowHypotenuse + mArcOffset - mArrowRadius,
                rect.left + mArrowHeight,
                rect.top + mArrowPosition + mArrowHypotenuse + mArcOffset + mArrowRadius), 0, -45);
        path.lineTo(rect.left + mArrowOffset / 2, rect.top + mArrowPosition + mArrowHypotenuse / 2 + mArrowOffset / 2);
        path.arcTo(new RectF(rect.left + mArrowOffset - mArrowRadius,
                rect.top + mArrowPosition + mArrowHypotenuse / 2 - mArrowRadius,
                rect.left + mArrowOffset + mArrowRadius,
                rect.top + mArrowPosition + mArrowHypotenuse / 2 + mArrowRadius), 135, 90);
        path.lineTo(rect.left + mArrowHeight - mArrowRadius + mPointcutsHeight, rect.top + mArrowPosition - mArcOffset + mPointcutsHeight);
        path.arcTo(new RectF(rect.left + mArrowHeight - mArrowRadius * 2,
                rect.top + mArrowPosition - mArcOffset - mArrowRadius,
                rect.left + mArrowHeight,
                rect.top + mArrowPosition - mArcOffset + mArrowRadius), 45, -45);
        path.lineTo(rect.left + mArrowHeight, rect.top + mAngleRadius);
        path.arcTo(new RectF(rect.left + mArrowHeight, rect.top, rect.left + mArrowHeight + mAngleDiameter, rect.top + mAngleDiameter), 180, 90);
        path.close();
    }

    private void setUpRightPath(RectF rect, Path path) {
        path.moveTo(rect.left + mAngleRadius, rect.top);
        path.lineTo(rect.right - mAngleRadius - mArrowHeight, rect.top);
        path.arcTo(new RectF(rect.right - mAngleDiameter - mArrowHeight, rect.top, rect.right - mArrowHeight, rect.top + mAngleDiameter), 270, 90);
        path.lineTo(rect.right - mArrowHeight, rect.top + mArrowPosition - mArcOffset);
        path.arcTo(new RectF(rect.right - mArrowHeight,
                rect.top + mArrowPosition - mArcOffset - mArrowRadius,
                rect.right - mArrowHeight + mArrowRadius * 2,
                rect.top + mArrowPosition - mArcOffset + mArrowRadius), 180, -45);
        path.lineTo(rect.right - mArrowOffset / 2, rect.top + mArrowPosition + mArrowHypotenuse / 2 - mArrowOffset / 2);
        path.arcTo(new RectF(rect.right - mArrowOffset - mArrowRadius,
                rect.top + mArrowPosition + mArrowHypotenuse / 2 - mArrowRadius,
                rect.right - mArrowOffset + mArrowRadius,
                rect.top + mArrowPosition + mArrowHypotenuse / 2 + mArrowRadius), 315, 90);
        path.lineTo(rect.right - mArrowHeight + mArrowRadius - mPointcutsHeight, rect.top + mArrowPosition + mArrowHypotenuse + mArcOffset - mPointcutsHeight);
        path.arcTo(new RectF(rect.right - mArrowHeight,
                rect.top + mArrowPosition + mArrowHypotenuse + mArcOffset - mArrowRadius,
                rect.right - mArrowHeight + mArrowRadius * 2,
                rect.top + mArrowPosition + mArrowHypotenuse + mArcOffset + mArrowRadius), 225, -45);
        path.lineTo(rect.right - mArrowHeight, rect.bottom - mAngleRadius);
        path.arcTo(new RectF(rect.right - mAngleDiameter - mArrowHeight, rect.bottom - mAngleDiameter, rect.right - mArrowHeight, rect.bottom), 0, 90);
        path.lineTo(rect.left + mAngleRadius, rect.bottom);
        path.arcTo(new RectF(rect.left, rect.bottom - mAngleDiameter, rect.left + mAngleDiameter, rect.bottom), 90, 90);
        path.lineTo(rect.left, rect.top + mAngleRadius);
        path.arcTo(new RectF(rect.left, rect.top, mAngleDiameter + rect.left, mAngleDiameter + rect.top), 180, 90);
        path.close();
    }

    private void setUpTopPath(RectF rect, Path path) {
        path.moveTo(rect.left + mAngleRadius, rect.top + mArrowHeight);
        path.lineTo(rect.left + mArrowPosition - mArcOffset, rect.top + mArrowHeight);
        path.arcTo(new RectF(rect.left + mArrowPosition - mArcOffset - mArrowRadius,
                rect.top + mArrowHeight - mArrowRadius * 2,
                rect.left + mArrowPosition - mArcOffset + mArrowRadius,
                rect.top + mArrowHeight), 90, -45);
        path.lineTo(rect.left + mArrowPosition + mArrowHypotenuse / 2 - mArrowOffset / 2, rect.top + mArrowOffset / 2);
        path.arcTo(new RectF(rect.left + mArrowPosition + mArrowHypotenuse / 2 - mArrowRadius,
                rect.top + mArrowOffset - mArrowRadius,
                rect.left + mArrowPosition + mArrowHypotenuse / 2 + mArrowRadius,
                rect.top + mArrowOffset + mArrowRadius), 225, 90);
        path.lineTo(rect.left + mArrowPosition + mArrowHypotenuse + mArcOffset - mPointcutsHeight, rect.top + mArrowHeight - mArrowRadius + mPointcutsHeight);
        path.arcTo(new RectF(rect.left + mArrowPosition + mArrowHypotenuse + mArcOffset - mArrowRadius,
                rect.top + mArrowHeight - mArrowRadius * 2,
                rect.left + mArrowPosition + mArrowHypotenuse + mArcOffset + mArrowRadius,
                rect.top + mArrowHeight), 135, -45);
        path.lineTo(rect.right - mAngleRadius, rect.top + mArrowHeight);
        path.arcTo(new RectF(rect.right - mAngleDiameter, rect.top + mArrowHeight, rect.right, mAngleDiameter + rect.top + mArrowHeight), 270, 90);
        path.lineTo(rect.right, rect.bottom - mAngleRadius);
        path.arcTo(new RectF(rect.right - mAngleDiameter, rect.bottom - mAngleDiameter, rect.right, rect.bottom), 0, 90);
        path.lineTo(rect.left + mAngleRadius, rect.bottom);
        path.arcTo(new RectF(rect.left, rect.bottom - mAngleDiameter, mAngleDiameter + rect.left, rect.bottom), 90, 90);
        path.lineTo(rect.left, rect.top + mArrowHeight + mAngleRadius);
        path.arcTo(new RectF(rect.left, rect.top + mArrowHeight, mAngleDiameter + rect.left, mAngleDiameter + rect.top + mArrowHeight), 180, 90);
        path.close();
    }

    private void setUpBottomPath(RectF rect, Path path) {
        path.moveTo(rect.left + mAngleRadius, rect.top);
        path.lineTo(rect.right - mAngleRadius, rect.top);
        path.arcTo(new RectF(rect.right - mAngleDiameter, rect.top, rect.right, mAngleDiameter + rect.top), 270, 90);
        path.lineTo(rect.right, rect.bottom - mArrowHeight - mAngleRadius);
        path.arcTo(new RectF(rect.right - mAngleDiameter, rect.bottom - mAngleDiameter - mArrowHeight, rect.right, rect.bottom - mArrowHeight), 0, 90);
        path.lineTo(rect.left + mArrowHypotenuse + mArrowPosition + mArcOffset, rect.bottom - mArrowHeight);
        path.arcTo(new RectF(rect.left + mArrowPosition + mArrowHypotenuse + mArcOffset - mArrowRadius,
                rect.bottom - mArrowHeight,
                rect.left + mArrowPosition + mArrowHypotenuse + mArcOffset + mArrowRadius,
                rect.bottom - mArrowHeight + mArrowRadius * 2), 270, -45);
        path.lineTo(rect.left + mArrowPosition + mArrowHypotenuse / 2 + mArrowOffset / 2, rect.bottom - mArrowOffset / 2);
        path.arcTo(new RectF(rect.left + mArrowPosition + mArrowHypotenuse / 2 - mArrowRadius,
                rect.bottom - mArrowOffset - mArrowRadius,
                rect.left + mArrowPosition + mArrowHypotenuse / 2 + mArrowRadius,
                rect.bottom - mArrowOffset + mArrowRadius), 45, 90);
        path.lineTo(rect.left + mArrowPosition - mArcOffset + mPointcutsHeight, rect.bottom - mArrowHeight + mArrowRadius - mPointcutsHeight);
        path.arcTo(new RectF(rect.left + mArrowPosition - mArcOffset - mArrowRadius,
                rect.bottom - mArrowHeight + mArrowRadius * 2,
                rect.left + mArrowPosition - mArcOffset + mArrowRadius,
                rect.bottom - mArrowHeight), 315, -45);
        path.lineTo(rect.left + mAngleRadius, rect.bottom - mArrowHeight);
        path.arcTo(new RectF(rect.left, rect.bottom - mAngleDiameter - mArrowHeight, mAngleDiameter + rect.left, rect.bottom - mArrowHeight), 90, 90);
        path.lineTo(rect.left, rect.top + mAngleRadius);
        path.arcTo(new RectF(rect.left, rect.top, mAngleDiameter + rect.left, mAngleDiameter + rect.top), 180, 90);
        path.close();
    }

    @Override
    public int getIntrinsicWidth() {
        return (int) mRect.width();
    }

    @Override
    public int getIntrinsicHeight() {
        return (int) mRect.height();
    }

    public static class Builder {

        public static final float DEFAULT_ARROW_HYPOT = 25;
        public static final float DEFAULT_ANGLE = 20;
        public static final float DEFAULT_ARROW_POSITION = 50;
        public static final int DEFAULT_BUBBLE_COLOR = Color.WHITE;
        public static final float DEFAULT_STROKE_WIDTH = 0;
        public static final int DEFAULT_STROKE_COLOR = Color.GRAY;

        public static final int ARROW_LEFT = 0X00000001;
        public static final int ARROW_RIGHT = 0X00000002;
        public static final int ARROW_TOP = 0X00000003;
        public static final int ARROW_BOTTOM = 0X00000004;

        @IntDef({ARROW_LEFT, ARROW_RIGHT, ARROW_TOP, ARROW_BOTTOM})
        @Retention(RetentionPolicy.SOURCE)
        public @interface ArrowLocation {
        }

        private RectF mRect;
        private float mAngle = DEFAULT_ANGLE;
        private int bubbleColor = DEFAULT_BUBBLE_COLOR;
        private Bitmap bubbleBitmap = null;
        private float mArrowHypotenuse = DEFAULT_ARROW_HYPOT;
        private float mArrowPosition = DEFAULT_ARROW_POSITION;
        private int mArrowLocation = ARROW_LEFT;
        private float mStrokeWidth = DEFAULT_STROKE_WIDTH;
        private int mStrokeColor = DEFAULT_STROKE_COLOR;

        public Builder rect(RectF rect) {
            this.mRect = rect;
            return this;
        }

        public Builder angle(float mAngle) {
            this.mAngle = mAngle * 2;
            return this;
        }

        public Builder bubbleColor(int bubbleColor) {
            this.bubbleColor = bubbleColor;
            return this;
        }

        public Builder bubbleBitmap(Bitmap bubbleBitmap) {
            this.bubbleBitmap = bubbleBitmap;
            return this;
        }

        public Builder arrowHypotenuse(float mArrowHypotenuse) {
            this.mArrowHypotenuse = mArrowHypotenuse;
            return this;
        }

        public Builder arrowPosition(float mArrowPosition) {
            this.mArrowPosition = mArrowPosition;
            return this;
        }

        public Builder arrowLocation(@ArrowLocation int arrowLocation) {
            this.mArrowLocation = arrowLocation;
            return this;
        }

        public Builder strokeWidth(float mStrokeWidth) {
            this.mStrokeWidth = mStrokeWidth;
            return this;
        }

        public Builder strokeColor(int mStrokeColor) {
            this.mStrokeColor = mStrokeColor;
            return this;
        }

        public BubbleDrawable build() {
            if (mRect == null) {
                throw new IllegalArgumentException("BubbleDrawable Rect can not be null");
            }

            if (mArrowPosition < mAngle) {
                throw new IllegalArgumentException("BubbleDrawable ArrowPosition can not be greater than mAngle");
            }

            return new BubbleDrawable(this);
        }
    }

}
