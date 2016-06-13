package cn.nodemedia.library.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * AdapterView适配器
 */
public abstract class AdapterViewAdapter<M> extends BaseAdapter {

    protected Context mContext;
    protected int mItemLayoutId;
    protected List<M> mDatas;
    protected OnItemChildClickListener mOnItemChildClickListener;
    protected OnItemChildLongClickListener mOnItemChildLongClickListener;

    public AdapterViewAdapter(Context context) {
        this(context, 0, null);
    }

    public AdapterViewAdapter(Context context, int itemLayoutId) {
        this(context, itemLayoutId, null);
    }

    public AdapterViewAdapter(Context context, int itemLayoutId, List<M> datas) {
        mContext = context;
        mItemLayoutId = itemLayoutId;
        mDatas = datas;
    }

    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public M getItem(int position) {
        return mDatas == null ? null : mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final AdapterViewHolder viewHolder = AdapterViewHolder.dequeueReusableAdapterViewHolder(mContext, convertView, parent, mItemLayoutId);
        viewHolder.getViewHolderHelper().setPosition(position);
        viewHolder.getViewHolderHelper().setOnItemChildClickListener(mOnItemChildClickListener);
        viewHolder.getViewHolderHelper().setOnItemChildLongClickListener(mOnItemChildLongClickListener);
        setItemChildListener(viewHolder.getViewHolderHelper());
        setItemData(viewHolder.getViewHolderHelper(), position, getItem(position));
        return viewHolder.getConvertView();
    }

    /**
     * 为item的孩子节点设置监听器，并不是每一个数据列表都要为item的子控件添加事件监听器，所以这里采用了空实现，需要设置事件监听器时重写该方法即可
     *
     * @param viewHolderHelper
     */
    protected void setItemChildListener(ViewHolderHelper viewHolderHelper) {
    }

    /**
     * 填充item数据
     *
     * @param viewHolderHelper
     * @param position
     * @param model
     */
    protected abstract void setItemData(ViewHolderHelper viewHolderHelper, int position, M model);

    /**
     * 设置item中的子控件点击事件监听器
     *
     * @param onItemChildClickListener
     */
    public void setOnItemChildClickListener(OnItemChildClickListener onItemChildClickListener) {
        mOnItemChildClickListener = onItemChildClickListener;
    }

    /**
     * 设置item中的子控件长按事件监听器
     *
     * @param onItemChildLongClickListener
     */
    public void setOnItemChildLongClickListener(OnItemChildLongClickListener onItemChildLongClickListener) {
        mOnItemChildLongClickListener = onItemChildLongClickListener;
    }

    public void setmItemLayoutId(@LayoutRes int mItemLayoutId) {
        this.mItemLayoutId = mItemLayoutId;
    }

    public void clearDatas() {
        if (mDatas != null) {
            mDatas.clear();
        }
        mDatas = null;
    }

    public void setDatas(List<M> datas) {
        mDatas = datas;
        notifyDataSetChanged();
    }

    public void addDatas(List<M> datas) {
        if (mDatas == null) {
            mDatas = datas;
        } else {
            mDatas.addAll(datas);
        }
        notifyDataSetChanged();
    }

    public void addData(int position, M model) {
        if (mDatas == null) {
            mDatas = new ArrayList<>();
        }
        mDatas.add(position, model);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        mDatas.remove(position);
        notifyDataSetChanged();
    }

    public void removeItem(M model) {
        mDatas.remove(model);
        notifyDataSetChanged();
    }

    public void setItem(int location, M newModel) {
        mDatas.set(location, newModel);
        notifyDataSetChanged();
    }

    public void setItem(M oldModel, M newModel) {
        setItem(mDatas.indexOf(oldModel), newModel);
    }

    /**
     * 重新设置ListView的高度(ScorllView 中如果再放入scrollView 是无法计算的，我们可以计算后再赋值)
     */
    public void changeListHeight(ListView listView) {
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = getListContentHeight(listView);
        listView.setLayoutParams(params);
    }

    /**
     * 获取ListView内容高度
     */
    public int getListContentHeight(ListView listView) {
        int totalHeight = 0;
        for (int i = 0; i < getCount(); i++) {
            View listItem = getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        return totalHeight + (listView.getDividerHeight() * (getCount() - 1)) + listView.getPaddingBottom() + listView.getPaddingTop();
    }
}