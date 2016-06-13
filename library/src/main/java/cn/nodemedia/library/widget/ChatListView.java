package cn.nodemedia.library.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ListView;

import cn.nodemedia.library.R;

/**
 * 聊天列表
 * Created by Bining.
 */
public class ChatListView extends ListView {

    private OnLoadingListener mListener;

    private View headerView;

    private int loadingYDist = 0;

    private float touchY = 0;
    private float pullYDist = 0;
    private float radio = 2;
    private boolean isLoading = false;

    private boolean isMoerHistory = true;

    public ChatListView(Context context) {
        super(context);
        initView(context);
    }

    public ChatListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ChatListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        headerView = LayoutInflater.from(context).inflate(R.layout.layout_loading, null);
        headerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                loadingYDist = headerView.getMeasuredHeight() + 10;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    headerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    headerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                setListHanderHeight(false);
            }
        });
        addHeaderView(headerView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (getFirstVisiblePosition() == 0 && isMoerHistory) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchY = ev.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    pullYDist = pullYDist + (ev.getY() - touchY) / radio;
                    touchY = ev.getY();
                    // 根据下拉距离改变比例
                    radio = (float) (2 + 2 * Math.tan(Math.PI / 2 / getMeasuredHeight() * Math.abs(pullYDist)));
                    break;
                case MotionEvent.ACTION_UP:
                    if (!isLoading && pullYDist >= loadingYDist * 1.5f) {
                        setListHanderHeight(true);
                        mListener.onLoadMore(this);
                    }
                    break;
                default:
                    break;
            }
        }
        return super.onTouchEvent(ev);
    }

    private void setListHanderHeight(boolean isLoading) {
        this.isLoading = isLoading;
        ViewGroup.LayoutParams layoutParams = headerView.getLayoutParams();
        layoutParams.height = isLoading ? loadingYDist : 1;
        headerView.setLayoutParams(layoutParams);
        headerView.findViewById(R.id.chat_header_progress).setVisibility(isLoading ? View.VISIBLE : View.GONE);
        requestLayout();
    }

    public void setMoerHistory(boolean isMoerHistory) {
        this.isMoerHistory = isMoerHistory;
    }

    public void endLoading() {
        setListHanderHeight(false);
    }

    public void setOnLoadingListener(OnLoadingListener listener) {
        mListener = listener;
    }

    public interface OnLoadingListener {
        void onLoadMore(ChatListView chatListView);
    }
}
