package cn.nodemedia.library.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.util.Linkify;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import cn.nodemedia.library.view.adapter.listener.OnItemChildClickListener;
import cn.nodemedia.library.view.adapter.listener.OnItemChildLongClickListener;

/**
 * 为AdapterView和RecyclerView的item设置常见属性（链式编程）
 */
public class ViewHolderHelper implements View.OnClickListener, View.OnLongClickListener {

    protected final SparseArray<View> mViews;
    protected OnItemChildClickListener mOnItemChildClickListener;
    protected OnItemChildLongClickListener mOnItemChildLongClickListener;
    protected View mConvertView;
    protected Context mContext;
    protected int mPosition;
    protected RecyclerView.ViewHolder mRecyclerViewHolder;

    /**
     * 留着以后作为扩充对象
     */
    protected Object mObj;

    public ViewHolderHelper(View convertView) {
        mViews = new SparseArray<>();
        mConvertView = convertView;
        mContext = convertView.getContext();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemChildClickListener != null) {
            mOnItemChildClickListener.onItemChildClick(v, getPosition());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        return mOnItemChildLongClickListener != null && mOnItemChildLongClickListener.onItemChildLongClick(v, getPosition());
    }

    public void setRecyclerViewHolder(RecyclerView.ViewHolder recyclerViewHolder) {
        mRecyclerViewHolder = recyclerViewHolder;
    }

    /**
     * 获取item的根控件
     */
    public View getConvertView() {
        return mConvertView;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    public int getPosition() {
        if (mRecyclerViewHolder != null) {
            return mRecyclerViewHolder.getLayoutPosition();
        }
        return mPosition;
    }

    public void setObj(Object obj) {
        mObj = obj;
    }

    public Object getObj() {
        return mObj;
    }

    /**
     * 设置item子控件点击事件监听器
     */
    public void setOnItemChildClickListener(OnItemChildClickListener onItemChildClickListener) {
        mOnItemChildClickListener = onItemChildClickListener;
    }

    /**
     * 设置item子控件长按事件监听器
     */
    public void setOnItemChildLongClickListener(OnItemChildLongClickListener onItemChildLongClickListener) {
        mOnItemChildLongClickListener = onItemChildLongClickListener;
    }

    /**
     * 通过控件的Id获取对应的控件，如果没有则加入mViews，则从item根控件中查找并保存到mViews中
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 为id为viewId的item子控件设置点击事件监听器
     */
    public void setItemChildClickListener(int viewId) {
        getView(viewId).setOnClickListener(this);
    }

    /**
     * 为id为viewId的item子控件设置长按事件监听器
     */
    public void setItemChildLongClickListener(int viewId) {
        getView(viewId).setOnLongClickListener(this);
    }

    public ViewHolderHelper setTag(int viewId, Object tag) {
        getView(viewId).setTag(tag);
        return this;
    }

    public ViewHolderHelper setTag(int viewId, int key, Object tag) {
        getView(viewId).setTag(key, tag);
        return this;
    }

    public ViewHolderHelper setVisibility(int viewId, int visibility) {
        getView(viewId).setVisibility(visibility);
        return this;
    }

    public ViewHolderHelper setAlpha(int viewId, float value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getView(viewId).setAlpha(value);
        } else {
            // Pre-honeycomb hack to set Alpha value
            AlphaAnimation alpha = new AlphaAnimation(value, value);
            alpha.setDuration(0);
            alpha.setFillAfter(true);
            getView(viewId).startAnimation(alpha);
        }
        return this;
    }

    public ViewHolderHelper setBackgroundRes(int viewId, int backgroundResId) {
        getView(viewId).setBackgroundResource(backgroundResId);
        return this;
    }

    public ViewHolderHelper setBackgroundColor(int viewId, int color) {
        getView(viewId).setBackgroundColor(color);
        return this;
    }

    public ViewHolderHelper setBackgroundColorRes(int viewId, @ColorRes int colorResId) {
        getView(viewId).setBackgroundColor(mContext.getResources().getColor(colorResId));
        return this;
    }

    /**
     * 设置对应id的控件的文本内容
     *
     * @param text 文本内容
     */
    public ViewHolderHelper setText(int viewId, String text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    /**
     * 设置对应id的控件的文本内容
     *
     * @param stringResId 字符串资源id
     */
    public ViewHolderHelper setText(int viewId, @StringRes int stringResId) {
        TextView view = getView(viewId);
        view.setText(stringResId);
        return this;
    }

    /**
     * 设置对应id的控件的html文本内容
     *
     * @param source html文本
     */
    public ViewHolderHelper setHtml(int viewId, String source) {
        TextView view = getView(viewId);
        view.setText(Html.fromHtml(source));
        return this;
    }

    public ViewHolderHelper setTextColorRes(int viewId, @ColorRes int textColorResId) {
        TextView view = getView(viewId);
        view.setTextColor(mContext.getResources().getColor(textColorResId));
        return this;
    }

    public ViewHolderHelper setTextColor(int viewId, int textColor) {
        TextView view = getView(viewId);
        view.setTextColor(textColor);
        return this;
    }

    public ViewHolderHelper linkify(int viewId) {
        TextView view = getView(viewId);
        Linkify.addLinks(view, Linkify.ALL);
        return this;
    }

    public ViewHolderHelper setTypeface(int viewId, Typeface typeface) {
        TextView view = getView(viewId);
        view.setTypeface(typeface);
        view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        return this;
    }

    public ViewHolderHelper setTypeface(Typeface typeface, int... viewIds) {
        for (int viewId : viewIds) {
            TextView view = getView(viewId);
            view.setTypeface(typeface);
            view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        }
        return this;
    }

    public ViewHolderHelper setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bitmap);
        return this;
    }

    public ViewHolderHelper setImageDrawable(int viewId, Drawable drawable) {
        ImageView view = getView(viewId);
        view.setImageDrawable(drawable);
        return this;
    }

    public ViewHolderHelper setImageResource(int viewId, int imageResId) {
        ImageView view = getView(viewId);
        view.setImageResource(imageResId);
        return this;
    }

    /**
     * 设置对应id的控件是否选中
     *
     * @param checked 是否选中
     */
    public ViewHolderHelper setChecked(int viewId, boolean checked) {
        Checkable view = (Checkable) getView(viewId);
        view.setChecked(checked);
        return this;
    }

    public ViewHolderHelper setProgress(int viewId, int progress) {
        ProgressBar view = getView(viewId);
        view.setProgress(progress);
        return this;
    }

    public ViewHolderHelper setProgress(int viewId, int progress, int max) {
        ProgressBar view = getView(viewId);
        view.setMax(max);
        view.setProgress(progress);
        return this;
    }

    public ViewHolderHelper setMax(int viewId, int max) {
        ProgressBar view = getView(viewId);
        view.setMax(max);
        return this;
    }

    public ViewHolderHelper setRating(int viewId, float rating) {
        RatingBar view = getView(viewId);
        view.setRating(rating);
        return this;
    }

    public ViewHolderHelper setRating(int viewId, float rating, int max) {
        RatingBar view = getView(viewId);
        view.setMax(max);
        view.setRating(rating);
        return this;
    }

    public ViewHolderHelper setAdapter(int viewId, Adapter adapter) {
        AdapterView view = getView(viewId);
        view.setAdapter(adapter);
        return this;
    }

}