package cn.nodemedia.library.view.adapter;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

public class BaseRecyclerDivider extends RecyclerView.ItemDecoration {

    protected static final int LINEAR_V_MANAGER = 0x00000001;
    protected static final int LINEAR_H_MANAGER = 0x00000002;
    protected static final int GRID_MANAGER = 0x00000003;

    private int magagerType;
    private Paint paint;
    private int dividerWidth = 1;
    private int dividerHeight = 1;

    public BaseRecyclerDivider() {
        this(LINEAR_V_MANAGER);
    }

    public BaseRecyclerDivider(int magagerType) {
        this(magagerType, 0XFFF5F5F5);
    }

    public BaseRecyclerDivider(int magagerType, @ColorInt int color) {
        this.magagerType = magagerType;
        paint = new Paint();
        paint.setColor(color);// 设置颜色
        paint.setAntiAlias(true);// 设置画笔的锯齿效果。 true是去除，大家一看效果就明白了
        paint.setStyle(Paint.Style.FILL);//设置填满
    }

    public void setMagagerType(int magagerType) {
        this.magagerType = magagerType;
    }

    public void setDividerWidth(int dividerWidth) {
        this.dividerWidth = dividerWidth;
    }

    public void setDividerHeight(int dividerHeight) {
        this.dividerHeight = dividerHeight;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        int left, right, top, bottom;
        switch (magagerType) {
            case LINEAR_V_MANAGER:
                left = parent.getPaddingLeft();
                right = parent.getWidth() - parent.getPaddingRight();
                for (int i = 0; i < childCount; i++) {
                    View child = parent.getChildAt(i);
                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                    top = child.getBottom() + params.bottomMargin;
                    bottom = top + dividerHeight;
                    c.drawRect(left, top, right, bottom, paint);
                }
                break;
            case LINEAR_H_MANAGER:
                top = parent.getPaddingTop();
                bottom = parent.getHeight() - parent.getPaddingBottom();

                for (int i = 0; i < childCount; i++) {
                    View child = parent.getChildAt(i);
                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                    left = child.getRight() + params.rightMargin;
                    right = left + dividerWidth;
                    c.drawRect(left, top, right, bottom, paint);
                }
                break;
            case GRID_MANAGER:
                for (int i = 0; i < childCount; i++) {
                    View child = parent.getChildAt(i);
                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                    left = child.getLeft() - params.leftMargin;
                    right = child.getRight() + params.rightMargin + dividerWidth;
                    top = child.getBottom() + params.bottomMargin;
                    bottom = top + dividerHeight;
                    c.drawRect(left, top, right, bottom, paint);
                }

                for (int i = 0; i < childCount; i++) {
                    View child = parent.getChildAt(i);
                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                    top = child.getTop() - params.topMargin;
                    bottom = child.getBottom() + params.bottomMargin;
                    left = child.getRight() + params.rightMargin;
                    right = left + dividerWidth;
                    c.drawRect(left, top, right, bottom, paint);
                }
                break;
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        switch (magagerType) {
            case LINEAR_V_MANAGER:
                outRect.set(0, 0, 0, dividerHeight);
                break;
            case LINEAR_H_MANAGER:
                outRect.set(0, 0, dividerWidth, 0);
                break;
            case GRID_MANAGER:
                int spanCount = getSpanCount(parent);
                int childCount = parent.getAdapter().getItemCount();
                int position = ((GridLayoutManager.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
                if (isLastRaw(parent, position, spanCount, childCount)) {// 如果是最后一行，则不需要绘制底部
                    outRect.set(0, 0, dividerWidth, 0);
                } else if (isLastColum(parent, position, spanCount, childCount)) {// 如果是最后一列，则不需要绘制右边
                    outRect.set(0, 0, 0, dividerHeight);
                } else {
                    outRect.set(0, 0, dividerWidth, dividerHeight);
                }
                break;
        }
    }

    private int getSpanCount(RecyclerView parent) {
        // 列数
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        }
        return spanCount;
    }

    private boolean isLastRaw(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            childCount = childCount - childCount % spanCount;
            if (pos >= childCount)// 如果是最后一行，则不需要绘制底部
                return false;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                childCount = childCount - childCount % spanCount;
                // 如果是最后一行，则不需要绘制底部
                if (pos >= childCount)
                    return true;
            } else { // StaggeredGridLayoutManager 且横向滚动
                // 如果是最后一行，则不需要绘制底部
                if ((pos + 1) % spanCount == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isLastColum(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if ((pos + 1) % spanCount == 0) {// 如果是最后一列，则不需要绘制右边
                return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
                    return true;
            } else {
                childCount = childCount - childCount % spanCount;
                if (pos >= childCount)// 如果是最后一列，则不需要绘制右边
                    return true;
            }
        }
        return false;
    }
}