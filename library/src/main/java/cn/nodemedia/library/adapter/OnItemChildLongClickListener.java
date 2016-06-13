package cn.nodemedia.library.adapter;

import android.view.View;

/**
 * AdapterView和RecyclerView的item中子控件长按事件监听器
 */
public interface OnItemChildLongClickListener {
    boolean onItemChildLongClick(View v, int position);
}