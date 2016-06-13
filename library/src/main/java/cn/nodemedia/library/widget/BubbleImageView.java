package cn.nodemedia.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import cn.nodemedia.library.R;
import cn.nodemedia.library.utils.ScreenUtils;

public class BubbleImageView extends ImageView {

    public static final int RIGHT = 0;
    public static final int LEFT = 1;

    /**
     * 是否裁剪为气泡
     */
    private boolean isBubble = false;

    /**
     * 图片圆角半径.
     */
    private float mRadius = ScreenUtils.dp2px(10);

    /**
     * 三角X轴上的宽度.
     */
    private float mVertexX = ScreenUtils.dp2px(10);

    /**
     * 三角据图片上边缘的距离
     */
    private float mVertexY = ScreenUtils.dp2px(15);

    /**
     * 三角Y轴上的高度.
     */
    private float mHemlineLength = ScreenUtils.dp2px(10);

    /**
     * 三角方向(LEFT or RIGHT).
     */
    private int mOrientationMode = LEFT;

    private Bitmap bubbleBitmap = null;
    private Paint mPaint;
    private Path mPath;

    private int scaledWidth;
    private int scaledHeight;

    public BubbleImageView(Context context) {
        super(context);
        initView();
    }

    public BubbleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initView();
    }

    public BubbleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(context, attrs);
        initView();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BubbleImageView);
        isBubble = a.getBoolean(R.styleable.BubbleImageView_bivIsBubble, isBubble);
        mRadius = a.getDimension(R.styleable.BubbleImageView_bivRadius, mRadius);
        mVertexX = a.getDimension(R.styleable.BubbleImageView_bivVertexXDis, mVertexX);
        mVertexY = a.getDimension(R.styleable.BubbleImageView_bivVertexYDis, mVertexY);
        mHemlineLength = a.getDimension(R.styleable.BubbleImageView_bivHelineLength, mHemlineLength);
        mOrientationMode = a.getInteger(R.styleable.BubbleImageView_bivOrientation, mOrientationMode);
        a.recycle();
    }

    private void initView() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        if (isBubble) {
            BitmapDrawable d = (BitmapDrawable) getDrawable();
            if (null == d) {
                return;
            }

            Bitmap originalBitmap = d.getBitmap();

            if (null == originalBitmap || originalBitmap.isRecycled()) {
                return;
            }

            setImageScaledSize(originalBitmap.getWidth(), originalBitmap.getHeight());

            bubbleBitmap = makeBubbleBitmap(originalBitmap);
            Drawable drawable = new BitmapDrawable(getResources(), bubbleBitmap);
            super.setImageDrawable(drawable);
        }
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        if (isBubble) {
            bubbleBitmap = makeBubbleBitmap(bitmap);
        } else {
            bubbleBitmap = bitmap;
        }
        super.setImageBitmap(bubbleBitmap);
    }

    public void setImageScaledSize(int width, int height) {
        this.scaledWidth = width;
        this.scaledHeight = height;
    }

    private Bitmap makeBubbleBitmap(Bitmap original) {
        if (original.isRecycled()) {
            return null;
        }
        if (mOrientationMode == LEFT) {
            drawLeftPath(scaledWidth, scaledHeight);
        } else if (mOrientationMode == RIGHT) {
            drawRightPath(scaledWidth, scaledHeight);
        } else {
            throw new IllegalArgumentException("OrientationMode is illegal.");
        }
        return makeBubbleBitmap(original, scaledWidth, scaledHeight);
    }

    private Bitmap makeBubbleBitmap(Bitmap original, int width, int height) {
        if (original.isRecycled()) {
            return null;
        }

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(original, width, height, true);

        Bitmap dest = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(dest);
        canvas.clipPath(mPath); // 剪辑图像.
        canvas.drawBitmap(scaledBitmap, 0, 0, mPaint);
        //recycleBitmap(scaledBitmap);
        return dest;
    }

    private void drawRightPath(int targetWidth, int targetHeight) {
        mPath = new Path();

        float halfHemline = mHemlineLength / 2f;

        // Draw the triangle.
        mPath.moveTo(targetWidth - mVertexX, mVertexY + halfHemline);
        mPath.lineTo(targetWidth, mVertexY);
        mPath.lineTo(targetWidth - mVertexX, mVertexY - halfHemline);
        // End

        mPath.lineTo(targetWidth - mVertexX, mRadius);

        // Get the dimater of the arc.
        float diameter = mRadius * 2;

        RectF arc = new RectF();

        // The right top round corner.
        arc.left = targetWidth - mVertexX - diameter;
        arc.top = 0;
        arc.right = targetWidth - mVertexX;
        arc.bottom = diameter;
        mPath.arcTo(arc, 0, -90);

        mPath.lineTo(mRadius, 0);


        // The left top round corner.
        arc.left = 0;
        arc.top = 0;
        arc.right = diameter;
        arc.bottom = diameter;
        mPath.arcTo(arc, 270, -90);


        mPath.lineTo(0, targetHeight - mRadius);

        // The left bottom round corner.
        arc.left = 0;
        arc.top = targetHeight - diameter;
        arc.right = diameter;
        arc.bottom = targetHeight;
        mPath.arcTo(arc, 180, -90);

        mPath.lineTo(targetWidth - mRadius, targetHeight);

        // The right bottom round corner.
        arc.left = targetWidth - mVertexX - diameter;
        arc.top = targetHeight - diameter;
        arc.right = targetWidth - mVertexX;
        arc.bottom = targetHeight;
        mPath.arcTo(arc, 90, -90);

        mPath.close();
    }

    private void drawLeftPath(int targetWidth, int targetHeight) {
        mPath = new Path();

        float halfHemline = mHemlineLength / 2f;

        // Draw the triangle.
        mPath.moveTo(mVertexX, mVertexY + halfHemline);
        mPath.lineTo(0, mVertexY);
        mPath.lineTo(mVertexX, mVertexY - halfHemline);
        // End

        mPath.lineTo(mVertexX, mRadius);

        //
        float diameter = mRadius * 2;

        RectF arc = new RectF();

        // The left top round corner.
        arc.left = mVertexX;
        arc.top = 0;
        arc.right = mVertexX + diameter;
        arc.bottom = diameter;
        mPath.arcTo(arc, 180, 90);


        mPath.lineTo(targetWidth - mRadius, 0);

        // The right top round corner.
        arc.left = targetWidth - diameter;
        arc.top = 0;
        arc.right = targetWidth;
        arc.bottom = diameter;
        mPath.arcTo(arc, 270, 90);


        mPath.lineTo(targetWidth, targetHeight - mRadius);

        // The right bottom round corner.
        arc.left = targetWidth - diameter;
        arc.top = targetHeight - diameter;
        arc.right = targetWidth;
        arc.bottom = targetHeight;
        mPath.arcTo(arc, 0, 90);

        mPath.lineTo(mVertexX + mRadius, targetHeight);

        // The left bottom round corner.
        arc.left = mVertexX;
        arc.top = targetHeight - diameter;
        arc.right = mVertexX + diameter;
        arc.bottom = targetHeight;
        mPath.arcTo(arc, 90, 90);

        mPath.close();
    }

    /**
     * Release the image bitmap.
     */
    public void release() {
        super.setImageBitmap(null);
        recycleBitmap(bubbleBitmap);
        System.gc();
    }

    /**
     * Recycle the bitmap.
     */
    private void recycleBitmap(Bitmap recycle) {
        if (null != recycle && !recycle.isRecycled()) {
            recycle.recycle();
            System.gc();
        }
    }

    /**
     * Set the direction of the triangle.
     *
     * @param orientation Pass {@link #LEFT} or {@link #RIGHT}. Default value is {@link #LEFT}.
     */
    public void setOrientation(int orientation) {
        if (mOrientationMode != orientation) {
            mOrientationMode = orientation;
        }
    }

}
