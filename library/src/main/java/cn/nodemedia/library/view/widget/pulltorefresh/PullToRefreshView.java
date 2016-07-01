package cn.nodemedia.library.view.widget.pulltorefresh;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import cn.nodemedia.library.R;

/**
 * 自定义的布局，用来管理三个子控件，其中一个是下拉头，一个是包含内容的pullableView（可以是实现Pullable接口的的任何View），
 * 还有一个上拉头
 */
public class PullToRefreshView extends RelativeLayout {

    // 初始状态
    private static final int INIT = 0;
    // 释放刷新
    private static final int RELEASE_TO_REFRESH = 1;
    // 正在刷新
    private static final int REFRESHING = 2;
    // 释放加载
    private static final int RELEASE_TO_LOAD = 3;
    // 正在加载
    private static final int LOADING = 4;
    // 操作完毕
    private static final int DONE = 5;

    private Context mContext;
    // 刷新回调接口
    private OnRefreshListener mListener;
    // 滚动驱动类
    private Scroller mScroller;

    private RotateAnimation mRotateUpAnim;//箭头旋转动画(向上)
    private RotateAnimation mRotateDownAnim;//箭头旋转动画(向下)

    // 下拉视图
    private View refreshView;
    // 下拉的箭头
    private ImageView refreshArrowView;
    // 正在刷新的图标
    private ProgressBar refreshingView;
    // 刷新结果图标
    private ImageView refreshStateView;
    // 刷新状态文字
    private TextView refreshTextView;

    // 实现了Pullable接口的内容View
    private View pullableView;

    // 上拉视图
    private View loadmoreView;
    // 上拉的箭头
    private ImageView loadmoreArrowView;
    // 正在加载的图标
    private ProgressBar loadingView;
    // 加载结果图标
    private ImageView loadStateView;
    // 加载状态文字
    private TextView loadTextView;

    // 是否第一次执行布局
    private boolean isLayout = true;

    // 触发刷新的Y轴移动距离
    private float refreshYDist = 0;

    //下拉或上拉的距离（大于0为下拉,小于0为上拉）
    private float pullYDist = 0;

    //记录上次的触摸点Y坐标
    private float lastY;

    // 过滤多点触碰
    private int mEvents;

    // 这两个变量用来控制pull的方向，如果不加控制，当情况满足可上拉又可下拉时没法下拉
    private boolean canPullUp = true;

    // 当前状态
    private int mState = INIT;
    // 手指滑动距离与下拉头的滑动距离比，中间会随正切函数变化
    private float radio = 2;

    public PullToRefreshView(Context context) {
        super(context);
        initView(context);
    }

    public PullToRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public PullToRefreshView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;

        mScroller = new Scroller(context, new DecelerateInterpolator());

        mRotateUpAnim = new RotateAnimation(0.0f, -180.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateUpAnim.setDuration(180);
        mRotateUpAnim.setFillAfter(true);
        mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateDownAnim.setDuration(180);
        mRotateDownAnim.setFillAfter(true);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (isLayout) {
            isLayout = false;

            pullableView = getChildAt(0);

            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

            refreshView = LayoutInflater.from(mContext).inflate(R.layout.layout_refresh_header, null);
            refreshArrowView = (ImageView) refreshView.findViewById(R.id.refresh_header_arrow);
            refreshingView = (ProgressBar) refreshView.findViewById(R.id.refresh_header_progressbar);
            refreshStateView = (ImageView) refreshView.findViewById(R.id.refresh_hander_state);
            refreshTextView = (TextView) refreshView.findViewById(R.id.refresh_header_text);
            addView(refreshView, 0);
            refreshView.setLayoutParams(lp);

            if (canPullUp) {
                loadmoreView = LayoutInflater.from(mContext).inflate(R.layout.layout_refresh_footer, null);
                loadmoreArrowView = (ImageView) loadmoreView.findViewById(R.id.refresh_footer_arrow);
                loadingView = (ProgressBar) loadmoreView.findViewById(R.id.refresh_footer_progressbar);
                loadTextView = (TextView) loadmoreView.findViewById(R.id.refresh_footer_text);
                loadStateView = (ImageView) loadmoreView.findViewById(R.id.refresh_footer_state);
                addView(loadmoreView);
                loadmoreView.setLayoutParams(lp);
            }
        }

        if (refreshYDist <= 10) {
            refreshYDist = ((ViewGroup) refreshView).getChildAt(0).getMeasuredHeight() + 10;
        }

        if (!canPullUp && pullYDist < 0) {
            pullYDist = 0;
        }

        // 改变子控件的布局，这里直接用(pullDownY + pullUpY)作为偏移量，这样就可以不对当前状态作区分
        if (refreshView != null)
            refreshView.layout(0, (int) pullYDist - refreshView.getMeasuredHeight(), refreshView.getMeasuredWidth(), (int) pullYDist);
        pullableView.layout(0, (int) pullYDist, pullableView.getMeasuredWidth(), (int) pullYDist + pullableView.getMeasuredHeight());
        if (loadmoreView != null)
            loadmoreView.layout(0, (int) pullYDist + pullableView.getMeasuredHeight(), loadmoreView.getMeasuredWidth(), (int) pullYDist + pullableView.getMeasuredHeight() + loadmoreView.getMeasuredHeight());
    }

    /*
     * （非 Javadoc）由父控件决定是否分发事件，防止事件冲突
     *
     * @see android.view.ViewGroup#dispatchTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_POINTER_DOWN:
                mEvents = -1;
                break;
            case MotionEvent.ACTION_DOWN:
                mEvents = 0;
                lastY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mEvents == 0) {
                    // 计算Y轴偏移量,对实际滑动距离做缩小，造成用力拉的感觉
                    pullYDist = pullYDist + (ev.getY() - lastY) / radio;
                    // 记录当前触摸Y坐标
                    lastY = ev.getY();
                    // 根据下拉距离改变比例
                    radio = (float) (2 + 2 * Math.tan(Math.PI / 2 / getMeasuredHeight() * Math.abs(pullYDist)));
                    if (pullYDist > 0 && (((Pullable) pullableView).canPullDown() && mState != LOADING)) {
                        // 可以下拉，正在加载时不能下拉
                        if (pullYDist > getMeasuredHeight())
                            pullYDist = getMeasuredHeight();

                        if (pullYDist <= refreshYDist && (mState == RELEASE_TO_REFRESH || mState == DONE)) {
                            // 如果下拉距离没达到刷新的距离且当前状态是释放刷新，改变状态为下拉刷新
                            changeState(INIT);
                        }

                        if (pullYDist >= refreshYDist && mState == INIT) {
                            // 如果下拉距离达到刷新的距离且当前状态是初始状态刷新，改变状态为释放刷新
                            changeState(RELEASE_TO_REFRESH);
                        }

                        requestLayout();

                        if (Math.abs(pullYDist) > 8) {
                            // 防止下拉过程中误触发长按事件和点击事件
                            ev.setAction(MotionEvent.ACTION_CANCEL);
                        }
                    } else if (pullYDist < 0 && (((Pullable) pullableView).canPullUp() && canPullUp && mState != REFRESHING)) {
                        // 可以上拉，正在刷新时不能上拉
                        if (pullYDist < -getMeasuredHeight())
                            pullYDist = -getMeasuredHeight();

                        if (-pullYDist <= refreshYDist && (mState == RELEASE_TO_LOAD || mState == DONE)) {
                            changeState(INIT);
                        }

                        if (-pullYDist >= refreshYDist && mState == INIT) {
                            changeState(RELEASE_TO_LOAD);
                        }

                        requestLayout();

                        if (Math.abs(pullYDist) > 8) {
                            // 防止下拉过程中误触发长按事件和点击事件
                            ev.setAction(MotionEvent.ACTION_CANCEL);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if ((((Pullable) pullableView).canPullDown() && pullYDist > 0) || (((Pullable) pullableView).canPullUp() && canPullUp && pullYDist < 0)) {
                    changeDist();
                }
                break;
            case MotionEvent.ACTION_UP:
                if ((((Pullable) pullableView).canPullDown() && pullYDist > 0) || (((Pullable) pullableView).canPullUp() && canPullUp && pullYDist < 0)) {
                    changeDist();
                } else {
                    pullYDist = 0;
                }
            default:
                break;
        }
        // 事件分发交给父类
        return super.dispatchTouchEvent(ev);
    }

    private void changeDist() {
        switch (mState) {
            case RELEASE_TO_REFRESH:
                changeState(REFRESHING);
                if (mListener != null)
                    mListener.onRefresh(this);
                mScroller.startScroll(0, (int) pullYDist, 0, (int) (refreshYDist - pullYDist));
                break;
            case REFRESHING:
                mScroller.startScroll(0, (int) pullYDist, 0, (int) (refreshYDist - pullYDist));
                break;
            case RELEASE_TO_LOAD:
                changeState(LOADING);
                if (mListener != null)
                    mListener.onLoadMore(this);
                mScroller.startScroll(0, (int) pullYDist, 0, (int) -(pullYDist + refreshYDist));
                break;
            case LOADING:
                mScroller.startScroll(0, (int) pullYDist, 0, (int) -(pullYDist + refreshYDist));
                break;
            default:
                mScroller.startScroll(0, (int) pullYDist, 0, (int) -pullYDist);
                break;
        }
        invalidate();// 触发 computeScroll
    }

    private void changeState(int toState) {
        switch (toState) {
            case INIT:
                // 下拉布局初始状态
                refreshTextView.setText(R.string.refresh_header_hint_normal);
                refreshArrowView.setVisibility(View.VISIBLE);
                refreshStateView.setVisibility(View.INVISIBLE);
                refreshingView.setVisibility(View.GONE);

                // 上拉布局初始状态
                if (loadmoreView != null) {
                    loadTextView.setText(R.string.refresh_footer_hint_normal);
                    loadmoreArrowView.setVisibility(View.VISIBLE);
                    loadStateView.setVisibility(View.INVISIBLE);
                    loadingView.setVisibility(View.GONE);
                }

                switch (mState) {
                    case RELEASE_TO_REFRESH:
                        refreshArrowView.startAnimation(mRotateDownAnim);
                        break;
                    case RELEASE_TO_LOAD:
                        loadmoreArrowView.startAnimation(mRotateDownAnim);
                        break;
                }
                break;
            case RELEASE_TO_REFRESH:
                // 释放刷新状态
                refreshTextView.setText(R.string.refresh_header_hint_ready);
                refreshArrowView.startAnimation(mRotateUpAnim);
                break;
            case REFRESHING:
                // 正在刷新状态
                refreshingView.setVisibility(View.VISIBLE);
                refreshArrowView.setVisibility(View.INVISIBLE);
                refreshArrowView.clearAnimation();
                refreshTextView.setText(R.string.refreshing);
                break;
            case RELEASE_TO_LOAD:
                // 释放加载状态
                loadTextView.setText(R.string.refresh_footer_hint_ready);
                loadmoreArrowView.startAnimation(mRotateUpAnim);
                break;
            case LOADING:
                // 正在加载状态
                loadingView.setVisibility(View.VISIBLE);
                loadmoreArrowView.setVisibility(View.INVISIBLE);
                loadmoreArrowView.clearAnimation();
                loadTextView.setText(R.string.loading);
                break;
        }
        mState = toState;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            pullYDist = mScroller.getCurrY();
            requestLayout();
            //postInvalidate();
        }
        super.computeScroll();
    }

    /**
     * 设置是否可以加载更多
     */
    public void setPullLoadEnable(boolean canPullUp) {
        this.canPullUp = canPullUp;
    }

    /**
     * 完成刷新操作，显示刷新结果。注意：刷新完成后一定要调用这个方法
     */
    /**
     * @param backState true代表成功，false代表失败
     */
    public void refreshFinish(boolean backState) {
        if (mState == REFRESHING) {
            refreshingView.setVisibility(View.GONE);
            if (backState) {
                refreshStateView.setVisibility(View.VISIBLE);
                refreshStateView.setImageResource(R.drawable.refresh_succeed);
                refreshTextView.setText(R.string.refresh_succeed);
            } else {
                refreshStateView.setVisibility(View.VISIBLE);
                refreshStateView.setImageResource(R.drawable.refresh_failed);
                refreshTextView.setText(R.string.refresh_fail);
            }

            mState = DONE;
//            if (pullYDist >= refreshYDist) {
//                new Handler() {
//                    @Override
//                    public void handleMessage(Message msg) {
//                        changeDist();
//                    }
//                }.sendEmptyMessageDelayed(0, 1000);
//            } else {
//                changeDist();
//            }
            changeDist();
        }
    }

    /**
     * 加载完毕，显示加载结果。注意：加载完成后一定要调用这个方法
     *
     * @param backState true代表成功，false代表失败
     */
    public void loadmoreFinish(boolean backState) {
        if (mState == LOADING) {
            loadingView.setVisibility(View.GONE);
            if (backState) {
                loadStateView.setVisibility(View.VISIBLE);
                loadStateView.setImageResource(R.drawable.refresh_succeed);
                loadTextView.setText(R.string.load_succeed);
            } else {
                loadStateView.setVisibility(View.VISIBLE);
                loadStateView.setImageResource(R.drawable.refresh_failed);
                loadTextView.setText(R.string.load_fail);
            }

            mState = DONE;
//            if (pullYDist <= -refreshYDist) {
//                new Handler() {
//                    @Override
//                    public void handleMessage(Message msg) {
//                        changeDist();
//                    }
//                }.sendEmptyMessageDelayed(0, 1000);
//            } else {
//                changeDist();
//            }
            changeDist();
        }
    }

    /**
     * 自动刷新
     */
    public void autoRefresh() {
        AutoRefreshAndLoadTask task = new AutoRefreshAndLoadTask();
        task.execute(20);
    }

    /**
     * @author chenjing 自动模拟手指滑动的task
     */
    private class AutoRefreshAndLoadTask extends AsyncTask<Integer, Float, String> {

        @Override
        protected String doInBackground(Integer... params) {
            while (pullYDist < 4 / 3 * refreshYDist) {
                pullYDist += 10;
                publishProgress(pullYDist);
                try {
                    Thread.sleep(params[0]);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Float... values) {
            if (pullYDist > refreshYDist)
                changeState(RELEASE_TO_REFRESH);
            requestLayout();
        }

        @Override
        protected void onPostExecute(String result) {
            changeState(REFRESHING);
            // 刷新操作
            if (mListener != null)
                mListener.onRefresh(PullToRefreshView.this);
        }
    }

    /**
     * 自动加载
     */
    public void autoLoad() {
        pullYDist = -refreshYDist;
        requestLayout();
        changeState(LOADING);
        // 加载操作
        if (mListener != null)
            mListener.onLoadMore(this);
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    /**
     * 刷新加载回调接口
     */
    public interface OnRefreshListener {
        /**
         * 刷新操作
         */
        void onRefresh(PullToRefreshView pullToRefreshLayout);

        /**
         * 加载操作
         */
        void onLoadMore(PullToRefreshView pullToRefreshLayout);
    }
}
