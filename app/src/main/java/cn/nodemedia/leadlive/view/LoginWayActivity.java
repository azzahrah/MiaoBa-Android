package cn.nodemedia.leadlive.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.nodemedia.leadlive.Application;
import cn.nodemedia.leadlive.R;
import cn.nodemedia.leadlive.view.contract.LoginBindContract;
import xyz.tanwb.treasurechest.view.BaseActivity;

/**
 * 登陆方式选择
 * Created by Bining.
 */
public class LoginWayActivity extends BaseActivity<LoginBindContract.Presenter> implements LoginBindContract.View {

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

    @Override
    public int getLayoutId() {
        return R.layout.activity_login_way;
    }

    @Override
    public void initView(Bundle bundle) {
        // ButterKnife.bind(this);
    }

    @Override
    public void initPresenter() {
        mPresenter.initPresenter(this);
    }

    @OnClick({R.id.login_way_qq, R.id.login_way_wx, R.id.login_way_xl, R.id.login_way_phone})
    public void onClick(View v) {
        if (!isCanClick(v)) return;
        switch (v.getId()) {
            case R.id.login_way_qq:
                mPresenter.loginToBind(LoginBindContract.Presenter.LTYPE_QQ);
                break;
            case R.id.login_way_wx:
                mPresenter.loginToBind(LoginBindContract.Presenter.LTYPE_WX);
                break;
            case R.id.login_way_xl:
                mPresenter.loginToBind(LoginBindContract.Presenter.LTYPE_SINA);
                break;
            case R.id.login_way_phone:
                StartActivity(LoginActivity.class);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public Context getContext() {
        return mActivity;
    }

    @Override
    public Application getBaseApplication() {
        return (Application) mApplication;
    }

    @Override
    public void goNextView() {
        StartActivity(MainActivity.class);
    }

    @Override
    public void showProgress() {
        // hasProgress(null, View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        // hasProgress(null, View.GONE);
    }

    @Override
    public void exit() {
        Back();
    }

}

