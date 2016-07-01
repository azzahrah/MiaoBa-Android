package cn.nodemedia.library.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import butterknife.ButterKnife;
import cn.nodemedia.library.BaseApplication;
import cn.nodemedia.library.R;
import cn.nodemedia.library.utils.Log;
import cn.nodemedia.library.view.widget.SwipeBackLayout;

/**
 * Created by Bining.
 */
public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity {

    protected Activity mActivity;
    protected Context mAppContext;
    protected BaseApplication mApplication;

    public T mPresenter;

    private boolean isOnClick = true;
    private int oldClickView = 0;
    private long oldClickTime = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(this.getLayoutId());
        mActivity = this;
        mAppContext = this.getApplicationContext();
        mApplication = (BaseApplication) getApplication();
        mApplication.addActivity(this);
        mPresenter = getT(this, 0);
        this.initView();
        this.initPresenter();

        // if (hasEventBus() && !EventBus.getDefault().isRegistered(this)) {
        // EventBus.getDefault().register(this);
        // }
        if (hasBindServer()) {
            Log.d("绑定Server");
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        if (hasSwipeFinish()) {
            SwipeBackLayout swipeBackLayout = new SwipeBackLayout(this);
            swipeBackLayout.setDragEdge(SwipeBackLayout.DragEdge.LEFT);
            swipeBackLayout.setOnSwipeBackListener(new SwipeBackLayout.SwipeBackListener() {
                @Override
                public void onViewPositionChanged(float fractionAnchor, float fractionScreen) {
                    Log.e("fractionAnchor:" + fractionAnchor + " fractionScreen:" + fractionScreen);
                }

                @Override
                public void onFinish() {
                    Back();
                }
            });
            swipeBackLayout.addView(LayoutInflater.from(this).inflate(layoutResID, null));
            super.setContentView(swipeBackLayout);
        } else {
            super.setContentView(layoutResID);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mActivity == null) {
            mActivity = this;
        }
        if (mAppContext == null) {
            mAppContext = this.getApplicationContext();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
        if (mPresenter != null)
            mPresenter.onDestroy();
        mPresenter = null;
        if (hasBindServer()) {
            Log.d("解绑Server");
        }
        // if (hasEventBus() && EventBus.getDefault().isRegistered(this)) {
        // EventBus.getDefault().unregister(this);
        // }
        mActivity = null;
        mAppContext = null;
        mApplication.removeActivity(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Back();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 获取布局文件ID
     */
    public abstract int getLayoutId();

    /**
     * 初始化视图
     */
    public abstract void initView();

    /**
     * 简单页面无需mvp就不用管此方法即可,完美兼容各种实际场景的变通
     */
    public abstract void initPresenter();

    /**
     * 是否滑动返回
     */
    public boolean hasSwipeFinish() {
        return true;
    }

    /**
     * 是否绑定MQTT服务
     */
    public boolean hasBindServer() {
        return false;
    }

    /**
     * 是否注册EventBus
     */
    // public boolean hasEventBus() {
    // return false;
    // }

    /**
     * EventBus回调到主线程
     *
     * @param eventBusInfo
     */
    // @Subscribe(threadMode = ThreadMode.MAIN)
    // public void onSubEvent(EventBusInfo eventBusInfo) {
    // Log.e("onSubEvent>>title: " + eventBusInfo.getTitle() + " data: " + eventBusInfo.getData());
    // }

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
        if (isOnClick) {
            int newClickView = view.getId();
            if (newClickView == R.id.back) {
                return true;
            } else {
                boolean isCanClick = true;
                long newClickTime = System.currentTimeMillis();
                if ((newClickTime - oldClickTime) < 300 || newClickView == oldClickView) {
                    isCanClick = false;
                }
                oldClickView = view.getId();
                oldClickTime = newClickTime;
                return isCanClick;
            }
        }
        return false;
    }

    /**
     * 跳转到下一界面
     *
     * @param cls 调整对象
     * @param obj 传递参数集
     */
    public void StartActivity(Class<?> cls, Object... obj) {
        Intent intent = new Intent(mActivity, cls);
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

    public <T> T getT(Object o, int i) {
        try {
            return ((Class<T>) ((ParameterizedType) (o.getClass().getGenericSuperclass())).getActualTypeArguments()[i]).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断字符串是否为空
     */
    public boolean isNotEmpty(String string) {
        return string != null && !string.isEmpty();
    }

    /**
     * 判断集合是否为空
     */
    public <T> boolean isNotNull(List<T> objects) {
        return objects != null && objects.size() > 0;
    }

}
