package cn.nodemedia.library.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 适用于AdapterView的item的ViewHolder
 */
public class AdapterViewHolder {

    protected View mConvertView;
    protected ViewHolderHelper mViewHolderHelper;

    private AdapterViewHolder(Context context, ViewGroup parent, int layoutId) {
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        mConvertView.setTag(this);
        mViewHolderHelper = new ViewHolderHelper(mConvertView);
    }

    /**
     * 拿到一个可重用的ViewHolder对象
     *
     * @param context
     * @param convertView
     * @param parent
     * @param layoutId
     * @return
     */
    public static AdapterViewHolder dequeueReusableAdapterViewHolder(Context context, View convertView, ViewGroup parent, int layoutId) {
        if (convertView == null) {
            return new AdapterViewHolder(context, parent, layoutId);
        }
        return (AdapterViewHolder) convertView.getTag();
    }

    public ViewHolderHelper getViewHolderHelper() {
        return mViewHolderHelper;
    }

    public View getConvertView() {
        return mConvertView;
    }

}