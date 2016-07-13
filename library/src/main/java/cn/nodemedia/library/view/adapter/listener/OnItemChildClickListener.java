package cn.nodemedia.library.view.adapter.listener;

import android.view.View;

/**
 * AdapterView和RecyclerView的item中子控件点击事件监听器
 */
public interface OnItemChildClickListener {
    void onItemChildClick(View v, int position);
}