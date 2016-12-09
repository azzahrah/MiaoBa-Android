package tv.miaoba.live.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import tv.miaoba.live.Application;
import tv.miaoba.live.R;
import tv.miaoba.live.view.contract.LauncherContract;
import xyz.tanwb.airship.utils.StatusBarUtils;
import xyz.tanwb.airship.view.BaseActivity;

/**
 * 启动页
 * Created by Bining.
 */
public class LaunchActivity extends BaseActivity<LauncherContract.Presenter> implements LauncherContract.View, Animation.AnimationListener {

    @BindView(R.id.launcher)
    ImageView launcher;
    @BindView(R.id.login_way)
    LinearLayout loginWay;

    @BindView(R.id.login_way_qq)
    ImageView loginWayQq;
    @BindView(R.id.login_way_wx)
    ImageView loginWayWx;
    @BindView(R.id.login_way_xl)
    ImageView loginWayXl;
    @BindView(R.id.login_way_phone)
    ImageView loginWayPhone;
    @BindView(R.id.login_way_protocol)
    TextView loginWayProtocol;

    private Animation animInAlpha, animInFromBottom;

    @Override
    public int getLayoutId() {
        return R.layout.activity_login_way;
    }

    @Override
    public void initView(Bundle bundle) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        StatusBarUtils.setColorToTransparent(mActivity);

        animInAlpha = AnimationUtils.loadAnimation(mActivity, R.anim.view_in_alpha);
        animInAlpha.setAnimationListener(this);
        animInAlpha.setDuration(1500);
        animInFromBottom = AnimationUtils.loadAnimation(mActivity, R.anim.view_in_from_bottom);
        animInFromBottom.setAnimationListener(this);
        animInFromBottom.setDuration(1500);

        launcher.startAnimation(animInAlpha);
    }

    @Override
    public void initPresenter() {
        mPresenter.initPresenter(this);
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        animation.setFillAfter(true);
        if (animation == animInAlpha) {
            loginWay.setVisibility(View.VISIBLE);
            loginWay.startAnimation(animInFromBottom);
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    @OnClick({R.id.login_way_qq, R.id.login_way_wx, R.id.login_way_xl, R.id.login_way_phone})
    public void onClick(View v) {
        if (!isCanClick(v)) return;
        switch (v.getId()) {
            case R.id.login_way_qq:
                mPresenter.loginToBind(LauncherContract.Presenter.LTYPE_QQ);
                break;
            case R.id.login_way_wx:
                mPresenter.loginToBind(LauncherContract.Presenter.LTYPE_WX);
                break;
            case R.id.login_way_xl:
                mPresenter.loginToBind(LauncherContract.Presenter.LTYPE_SINA);
                break;
            case R.id.login_way_phone:
                advance(LoginActivity.class);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPresenter.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public Application getBaseApplication() {
        return (Application) mApplication;
    }

    @Override
    public void goNextView() {
        advance(MainActivity.class);
    }

    @Override
    public boolean hasSwipeFinish() {
        return false;
    }

}
