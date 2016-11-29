package cn.nodemedia.leadlive.view;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.nodemedia.leadlive.R;
import cn.nodemedia.leadlive.view.contract.LoginForMobileContract;
import xyz.tanwb.airship.view.widget.DrawableEditText;

/**
 * 登陆界面
 * Created by Bining.
 */
public class LoginActivity extends ActionbarActivity<LoginForMobileContract.Presenter> implements LoginForMobileContract.View, View.OnClickListener {

    @BindView(R.id.phone)
    DrawableEditText phone;
    @BindView(R.id.password)
    DrawableEditText password;
    @BindView(R.id.password_eye)
    ImageView passwordEye;
    @BindView(R.id.login_text)
    TextView loginText;
    @BindView(R.id.login_button)
    TextView loginButton;

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initView(Bundle bundle) {
        super.initView(bundle);
        setTitle(R.string.login);
    }

    @Override
    public void initPresenter() {
        mPresenter.initPresenter(this);
    }

    @Override
    @OnClick({R.id.password_eye, R.id.login_text, R.id.login_button})
    public void onClick(View view) {
        if (!isCanClick(view)) return;
        switch (view.getId()) {
            case R.id.password_eye:
                mPresenter.switchPasswordEye(password, passwordEye);
                break;
            case R.id.login_text:
                //StartActivity(UserPwdForgotActivity.class);
                break;
            case R.id.login_button:
                mPresenter.attemptLogin(phone.getText().toString(), password.getText().toString(), true);
                break;
        }
    }

    @Override
    public void setUsername(String username) {
        phone.setText(username);
    }

    @Override
    public void setPassword(String passwoed) {
        password.setText(passwoed);
    }

    @Override
    public void setUsernameError(String message) {
        phone.setError(message);
    }

    @Override
    public void setPasswordError(String message) {
        password.setError(message);
    }

    @Override
    public void goNextView() {
        advance(MainActivity.class);
    }

}