package cn.nodemedia.leadlive.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import cn.nodemedia.leadlive.MyApplication;
import cn.nodemedia.leadlive.R;
import cn.nodemedia.library.bean.EventBusInfo;
import cn.nodemedia.library.utils.Log;

public abstract class AbsActivity extends FragmentActivity {

    protected Activity mActivity;
    protected Context mContext;
    protected MyApplication myApplication;

    // private Observable<EventBusInfo> observable;implements Action1<EventBusInfo>
    private long oldClickTime = 0;
    private boolean isOnClick = true;

    //手指上下滑动时的最小速度
    private static final int YSPEED_MIN = 1000;

    //手指向右滑动时的最小距离
    private static final int XDISTANCE_MIN = 100;

    //手指向上滑或下滑时的最小距离
    private static final int YDISTANCE_MIN = 50;

    //记录手指按下时的横坐标。
    private float xDown;

    //记录手指按下时的纵坐标。
    private float yDown;

    //用于计算手指滑动的速度。
    private VelocityTracker mVelocityTracker;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        mContext = this.getApplicationContext();
        myApplication = (MyApplication) getApplication();
        myApplication.addActivity(this);

//        if (hasRxBus()) {
//            observable = RxBus.get().register(EventBusInfo.class.getName(), EventBusInfo.class);
//            observable.subscribe(this);
//        }

        if (hasEventBus() && !EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        if (hasBindServer()) {
            Log.d("绑定Server");
        }
    }

    public MyApplication getMyApplication() {
        return myApplication;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mActivity == null) {
            mActivity = this;
        }
        if (mContext == null) {
            mContext = this.getApplicationContext();
        }
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //OkHttpManager.getInstance().cancelRequest(mActivity.toString());
        if (hasBindServer()) {
            Log.d("解绑Server");
        }
        if (hasEventBus() && EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        mActivity = null;
        mContext = null;
        myApplication.removeActivity(this);
    }

//    @Override
//    public void call(EventBusInfo rxBusInfo) {
//        Log.d("onRxbusCall >>" + JSON.toJSONString(rxBusInfo));
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Back();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 跳转到下一界面
     *
     * @param cls 调整对象
     * @param obj 传递参数集
     */
    public void StartActivity(Class<?> cls, Object... obj) {
        Intent intent = new Intent(mContext, cls);
        if (obj != null) {
            for (int i = 0; i < obj.length; i++) {
                intent.putExtra("p" + i, (java.io.Serializable) obj[i]);
            }
        }
        startActivity(intent);
        overridePendingTransition(R.anim.view_in_from_right, R.anim.view_out_to_left);
    }

    /**
     * 返回上一界面
     */
    public void Back() {
        this.finish();
        overridePendingTransition(R.anim.view_in_from_left, R.anim.view_out_to_right);
    }

    /**
     * 设置是否可使用点击事件
     *
     * @param isOnClick 是否可点击
     */
    public void setOnClick(boolean isOnClick) {
        this.isOnClick = isOnClick;
    }

    /**
     * 判断是否可以继续执行点击事件
     *
     * @param view 点击控件
     */
    public boolean isCanClick(View view) {
        long newClickTime = System.currentTimeMillis();
        if ((newClickTime - oldClickTime) < 300) {
            return false;
        }
        oldClickTime = newClickTime;

        return view.getId() == R.id.actionbar_back || isOnClick;
    }

    /**
     * 是否注册EventBus
     */
    public boolean hasEventBus() {
        return false;
    }

    /**
     * EventBus回调到主线程
     *
     * @param eventBusInfo
     */
    public void onEventMainThread(EventBusInfo eventBusInfo) {
    }

    /**
     * 是否绑定MQTT服务
     */
    public boolean hasBindServer() {
        return false;
    }

    /**
     * 是否滑动返回
     */
    public boolean hasSwipeFinish() {
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (hasSwipeFinish()) {
            createVelocityTracker(event);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    xDown = event.getRawX();
                    yDown = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    //滑动的距离
                    int distanceX = (int) (event.getRawX() - xDown);
                    int distanceY = (int) (event.getRawY() - yDown);
                    //获取顺时速度
                    int ySpeed = getScrollVelocity();
                    //关闭Activity需满足以下条件：
                    //1.x轴滑动的距离大于XDISTANCE_MIN
                    //2.y轴滑动的距离在YDISTANCE_MIN范围内
                    //3.y轴上（即上下滑动的速度）<XSPEED_MIN，如果大于，则认为用户意图是在上下滑动而非左滑结束Activity
                    if (distanceX > XDISTANCE_MIN && (distanceY < YDISTANCE_MIN && distanceY > -YDISTANCE_MIN) && ySpeed < YSPEED_MIN) {
                        Back();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    recycleVelocityTracker();
                    break;
                default:
                    break;
            }
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 创建VelocityTracker对象，并将触摸界面的滑动事件加入到VelocityTracker当中。
     */
    private void createVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    /**
     * 回收VelocityTracker对象。
     */
    private void recycleVelocityTracker() {
        mVelocityTracker.recycle();
        mVelocityTracker = null;
    }

    /**
     * 得到滑动速度，以每秒钟移动了多少像素值为单位。
     */
    private int getScrollVelocity() {
        mVelocityTracker.computeCurrentVelocity(1000);
        int velocity = (int) mVelocityTracker.getYVelocity();
        return Math.abs(velocity);
    }

    // Event-------------------------start-------------------------------
    // /**
    // * 使用onEvent来接收事件，那么接收事件和分发事件在一个线程中执行
    // *
    // * @param event
    // */
    // public void onEvent(EventBusNotice event) {
    // }
    //
    // /**
    // * 使用onEventMainThread来接收事件，那么不论分发事件在哪个线程运行，接收事件永远在UI线程执行，
    // * 这对于android应用是非常有意义的
    // *
    // * @param event
    // */
    // public void onEventMainThread(EventBusNotice event) {
    // }
    //
    // /**
    // * 使用onEventBackgroundThread来接收事件，如果分发事件在子线程运行，那么接收事件直接在同样线程
    // * 运行，如果分发事件在UI线程，那么会启动一个子线程运行接收事件
    // *
    // * @param event
    // */
    // public void onEventBackgroundThread(EventBusNotice event) {
    // }
    //
    // /**
    // * 使用onEventAsync接收事件，无论分发事件在（UI或者子线程）哪个线程执行，接收都会在另外一个子线程执行
    // *
    // * @param event
    // */
    // public void onEventAsync(EventBusNotice event) {
    // }
    // Event-------------------------end-------------------------------
}
