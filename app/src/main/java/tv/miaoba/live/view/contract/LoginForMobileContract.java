package tv.miaoba.live.view.contract;

import android.provider.Settings;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import tv.miaoba.live.Constants;
import tv.miaoba.live.R;
import xyz.tanwb.airship.utils.SharedUtils;
import xyz.tanwb.airship.utils.ToastUtils;

public interface LoginForMobileContract {

    interface View extends BaseLoginContract.View {
        void setUsername(String username);

        void setPassword(String passwoed);

        void setUsernameError(String message);

        void setPasswordError(String message);
    }

    class Presenter extends BaseLoginContract.Presenter<View> {

        private String deivceId;

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

        @Override
        public void onPermissionsSuccess(String[] strings) {
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
            if (password.length() < 6 || password.length() > 18) {
                onPasswordError(R.string.error_field_format);
                return;
            }

            SharedUtils.put(Constants.USERACCOUNT, username);
            SharedUtils.put(Constants.USERPWD, password);
            SharedUtils.put(Constants.USERPWDSAVE, isSave);

            login(username, password, 0);
        }

        public void onUsernameError(@StringRes int strRes) {
            if (mView != null)
                mView.setUsernameError(mContext.getString(strRes));
        }

        public void onPasswordError(@StringRes int strRes) {
            if (mView != null)
                mView.setPasswordError(mContext.getString(strRes));
        }

        public void attemptYKLogin() {
            deivceId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
            login(deivceId, "", 1);
        }

        @Override
        public void getBindUserInfo() {
            if (!TextUtils.isEmpty(deivceId)) {
                regiter(deivceId, "", "", "", "");
            } else {
                ToastUtils.show(mContext, "未获取到用户信息,请稍候重试.");
            }
        }
    }
}
