package cn.nodemedia.leadlive.view;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import cn.nodemedia.leadlive.R;
import cn.nodemedia.leadlive.view.contract.LauncherContract;
import xyz.tanwb.airship.utils.StatusBarUtils;
import xyz.tanwb.airship.view.BaseActivity;

/**
 * 启动页
 * Created by Bining.
 */
public class LaunchActivity extends BaseActivity<LauncherContract.Presenter> implements LauncherContract.View {

    @Override
    public int getLayoutId() {
        return R.layout.activity_launcher;
    }

    @Override
    public void initView(Bundle bundle) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        StatusBarUtils.setColorToTransparent(mActivity);
    }

    @Override
    public void initPresenter() {
        mPresenter.initPresenter(this);
    }

    @Override
    public void startAnimation() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                advance(LoginWayActivity.class);
                finish();
            }
        }, 1500);
    }

    @Override
    public boolean hasSwipeFinish() {
        return false;
    }

}
