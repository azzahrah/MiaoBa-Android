package cn.nodemedia.leadlive.view;

import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.lzy.okhttputils.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;

import cn.nodemedia.leadlive.Constants;
import cn.nodemedia.leadlive.R;
import cn.nodemedia.leadlive.bean.UserInfo;
import cn.nodemedia.leadlive.utils.DBUtils;
import cn.nodemedia.leadlive.utils.HttpUtils;
import cn.nodemedia.library.bean.Abs;
import cn.nodemedia.library.bean.EventBusInfo;
import cn.nodemedia.library.db.DbException;
import cn.nodemedia.library.utils.SharedUtils;
import cn.nodemedia.library.utils.ValidUtils;
import okhttp3.Request;
import okhttp3.Response;

public class LoginPresenter extends PasswordPresenter {

    private LoginView loginView;

    public LoginPresenter(LoginView loginView) {
        super(loginView);
        this.loginView = loginView;
    }

    public void initData() {
        if (loginView != null) {
            String account = SharedUtils.getString(Constants.USERACCOUNT, "");
            if (!TextUtils.isEmpty(account))
                loginView.setUsername(account);
            boolean isSavePsd = SharedUtils.getBoolean(Constants.USERPWDSAVE, false);
            if (isSavePsd) {
                String password = SharedUtils.getString(Constants.USERPWD, "");
                if (!TextUtils.isEmpty(password))
                    loginView.setPassword(password);
            }
        }
    }

    public void attemptLogin(String username, String password, boolean isSave) {
        if (TextUtils.isEmpty(username)) {
            onUsernameError(R.string.error_field_required);
            return;
        }
//        if (!ValidUtils.isMobileNO(username)) {
//            onUsernameError(R.string.error_field_format);
//            return;
//        }
        if (TextUtils.isEmpty(password)) {
            onPasswordError(R.string.error_field_required);
            return;
        }
        if (!ValidUtils.isPasswordLength(password)) {
            onPasswordError(R.string.error_field_format);
            return;
        }

        SharedUtils.put(Constants.USERACCOUNT, username);
        SharedUtils.put(Constants.USERPWD, password);
        SharedUtils.put(Constants.USERPWDSAVE, isSave);

        HttpUtils.login(username, password, 1, new StringCallback() {
            @Override
            public void onResponse(boolean isFromCache, String s, Request request, @Nullable Response response) {
                Abs abs = JSON.parseObject(s, Abs.class);
                if (abs.isSuccess()) {
                    UserInfo userInfo = JSON.parseObject(abs.result, UserInfo.class);
                    SharedUtils.put(Constants.USEROPENID, userInfo.userid);
                    try {
                        DBUtils.getInstance().saveOrUpdate(userInfo);
                        SharedUtils.put(Constants.USERISLOGIN, true);
                        EventBus.getDefault().post(new EventBusInfo(Constants.USERISLOGIN));
                        onSucc();
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                } else {
                    onFail(abs.getMsg());
                }
            }
        });
    }

    public void onUsernameError(@StringRes int strRes) {
        if (loginView != null)
            loginView.setUsernameError(context.getString(strRes));
    }

    public void onPasswordError(@StringRes int strRes) {
        if (loginView != null)
            loginView.setPasswordError(context.getString(strRes));
    }

    public void onSucc() {
        super.onSucc();
        if (loginView != null) {
            loginView.goNextView();
            loginView.exitActivity();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        loginView = null;
    }

}
