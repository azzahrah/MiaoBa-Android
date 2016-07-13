package cn.nodemedia.leadlive.view;

import android.support.annotation.StringRes;
import android.text.TextUtils;

import cn.nodemedia.leadlive.Constants;
import cn.nodemedia.leadlive.R;
import cn.nodemedia.library.utils.SharedUtils;
import cn.nodemedia.library.utils.ValidUtils;

public interface LoginTradContract {

    interface View extends LoginBaseContract.View {
        void setUsername(String username);

        void setPassword(String passwoed);

        void setUsernameError(String message);

        void setPasswordError(String message);
    }

    class Presenter extends LoginBaseContract.Presenter<View> {

        @Override
        public void onStart() {
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

            login(username, password, 1);
        }

        public void onUsernameError(@StringRes int strRes) {
            if (mView != null)
                mView.setUsernameError(context.getString(strRes));
        }

        public void onPasswordError(@StringRes int strRes) {
            if (mView != null)
                mView.setPasswordError(context.getString(strRes));
        }

        @Override
        public void getBindUserInfo() {
            onFail("登陆遇到问题:为获取到用户数据.");
        }
    }
}
