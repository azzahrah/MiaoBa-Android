package cn.nodemedia.library.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import cn.nodemedia.library.view.adapter.listener.OnItemClickListener;
import cn.nodemedia.library.view.adapter.listener.OnItemLongClickListener;

/**
 * 适用于RecyclerView的item的ViewHolder
 * Created by Bining.
 */
public class BaseRecyclerHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    protected Context mContext;
    protected ViewHolderHelper mViewHolderHelper;
    protected OnItemClickListener mOnItemClickListener;
    protected OnItemLongClickListener mOnItemLongClickListener;

    public BaseRecyclerHolder(View itemView) {
        this(itemView, null, null);
    }

    public BaseRecyclerHolder(View itemView, OnItemClickListener onItemClickListener, OnItemLongClickListener onItemLongClickListener) {
        super(itemView);
        mContext = itemView.getContext();
        mViewHolderHelper = new ViewHolderHelper(this.itemView);
        mViewHolderHelper.setRecyclerViewHolder(this);
        mOnItemClickListener = onItemClickListener;
        mOnItemLongClickListener = onItemLongClickListener;
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    public ViewHolderHelper getViewHolderHelper() {
        return mViewHolderHelper;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == this.itemView.getId() && null != mOnItemClickListener) {
            mOnItemClickListener.onRVItemClick(v, getLayoutPosition());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (v.getId() == this.itemView.getId() && null != mOnItemLongClickListener) {
            return mOnItemLongClickListener.onRVItemLongClick(v, getLayoutPosition());
        }
        return false;
    }
}