package cn.nodemedia.library.view.adapter.listener;

import android.view.View;

/**
 * RecyclerView的item长按事件监听器
 */
public interface OnItemLongClickListener {
    boolean onRVItemLongClick(View v, int position);
}