package cn.nodemedia.library.adapter;

import android.view.View;

/**
 * RecyclerView的item点击事件监听器
 */
public interface OnItemClickListener {
    void onRVItemClick(View v, int position);
}