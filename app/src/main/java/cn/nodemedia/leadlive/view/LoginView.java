package cn.nodemedia.leadlive.view;

/**
 * 登陆
 * Created by Bining.
 */
public interface LoginView extends BaseView {

    void setUsername(String username);

    void setPassword(String passwoed);

    void setUsernameError(String message);

    void setPasswordError(String message);

    void goNextView();
}
