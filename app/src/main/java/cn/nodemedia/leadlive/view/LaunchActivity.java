package cn.nodemedia.leadlive.view;

import android.os.Handler;
import android.view.View;

import cn.nodemedia.leadlive.R;
import cn.nodemedia.library.view.BaseActivity;

/**
 * 启动页
 * Created by Bining.
 */
public class LaunchActivity extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_launcher;
    }

    @Override
    public void initView() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                StartActivity(LoginWayActivity.class);
                finish();
            }
        }, 1500);
    }

    @Override
    public void initPresenter() {
    }

}
