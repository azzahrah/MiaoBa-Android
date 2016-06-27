package cn.nodemedia.leadlive.view;

import android.support.annotation.StringRes;
import android.text.TextUtils;

import cn.nodemedia.leadlive.Constants;
import cn.nodemedia.leadlive.R;
import cn.nodemedia.leadlive.bean.UserInfo;
import cn.nodemedia.leadlive.utils.DBUtils;
import cn.nodemedia.leadlive.utils.HttpUtils;
import cn.nodemedia.library.bean.AbsT;
import cn.nodemedia.library.db.DbException;
import cn.nodemedia.library.utils.SharedUtils;
import cn.nodemedia.library.utils.ValidUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class LoginPresenter extends LoginContract.Presenter {

    @Override
    public void onStart() {
        super.onStart();
        if (mView != null) {
            String account = SharedUtils.getString(Constants.USERACCOUNT, "");
            if (!TextUtils.isEmpty(account))
                mView.setUsername(account);
            boolean isSavePsd = SharedUtils.getBoolean(Constants.USERPWDSAVE, false);
            if (isSavePsd) {
                String password = SharedUtils.getString(Constants.USERPWD, "");
                if (!TextUtils.isEmpty(password))
                    mView.setPassword(password);
            }
        }
    }

    public void attemptLogin(String username, String password, boolean isSave) {
        if (TextUtils.isEmpty(username)) {
            onUsernameError(R.string.error_field_required);
            return;
        }
        // if (!ValidUtils.isMobileNO(username)) {
        // onUsernameError(R.string.error_field_format);
        // return;
        // }
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

        HttpUtils.login(username, password, 1).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<AbsT<UserInfo>>() {
            @Override
            public void call(AbsT<UserInfo> userInfoAbsT) {
                if (userInfoAbsT.isSuccess()) {
                    SharedUtils.put(Constants.USEROPENID, userInfoAbsT.result.userid);
                    try {
                        DBUtils.getInstance().saveOrUpdate(userInfoAbsT.result);
                        SharedUtils.put(Constants.USERISLOGIN, true);
                        mRxManage.post(Constants.USERISLOGIN, true);
                        onSucc();
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                } else {
                    onFail(userInfoAbsT.getMsg());
                }
            }
        });
    }

    public void onUsernameError(@StringRes int strRes) {
        if (mView != null)
            mView.setUsernameError(context.getString(strRes));
    }

    public void onPasswordError(@StringRes int strRes) {
        if (mView != null)
            mView.setPasswordError(context.getString(strRes));
    }

    public void onSucc() {
        super.onSucc();
        if (mView != null) {
            mView.goNextView();
            mView.exit();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mView = null;
    }

}
