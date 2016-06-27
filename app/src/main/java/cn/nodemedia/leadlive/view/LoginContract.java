package cn.nodemedia.leadlive.view;

import cn.nodemedia.library.view.BaseView;

public interface LoginContract {

    interface View extends BaseView {
        void setUsername(String username);

        void setPassword(String passwoed);

        void setUsernameError(String message);

        void setPasswordError(String message);

        void goNextView();
    }

    abstract class Presenter extends PasswordPresenter<View> {

        public abstract void attemptLogin(String username, String password, boolean isSave);
    }
}
