package cn.nodemedia.leadlive.view;

import android.os.Bundle;
import android.os.Handler;

import cn.nodemedia.leadlive.R;
import xyz.tanwb.treasurechest.view.BaseActivity;

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
    public void initView(Bundle bundle) {
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
