package cn.nodemedia.leadlive.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.nodemedia.leadlive.R;
import cn.nodemedia.library.widget.DrawableEditText;

/**
 * 登陆界面
 * Created by Bining.
 */
public class LoginActivity extends AbsActionbarActivity implements LoginView, View.OnClickListener {

    @InjectView(R.id.phone)
    DrawableEditText phone;
    @InjectView(R.id.password)
    DrawableEditText password;
    @InjectView(R.id.password_eye)
    ImageView passwordEye;
    @InjectView(R.id.login_text)
    TextView loginText;
    @InjectView(R.id.login_button)
    TextView loginButton;

    private LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        setTitle(R.string.login);
        initView();
    }

    private void initView() {
        presenter = new LoginPresenter(this);
        presenter.initData();
    }

    @Override
    @OnClick({R.id.password_eye, R.id.login_text, R.id.login_button})
    public void onClick(View view) {
        if (!isCanClick(view)) return;
        switch (view.getId()) {
            case R.id.password_eye:
                presenter.switchPasswordEye(password, passwordEye);
                break;
            case R.id.login_text:
                //StartActivity(UserPwdForgotActivity.class);
                break;
            case R.id.login_button:
                presenter.attemptLogin(phone.getText().toString(), password.getText().toString(), true);
                break;
        }
    }

    @Override
    public Context getContext() {
        return mActivity;
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
        StartActivity(MainActivity.class);
    }

    @Override
    public void showProgress() {
        hasProgress(null, View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        hasProgress(null, View.GONE);
    }

    @Override
    public void exitActivity() {
        Back();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
        presenter = null;
    }
}