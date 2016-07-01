package cn.nodemedia.library.view;

import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageView;

import cn.nodemedia.library.R;

/**
 * 密码显示隐藏
 * Created by Bining on 16/6/28.
 */
public abstract class PasswordPresenter<T extends BaseView> extends BasePresenter<T> {

    /**
     * 控制密码的显示隐藏
     */
    public void switchPasswordEye(EditText password, ImageView passwordEye) {
        if (password.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)) {
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordEye.setImageResource(R.drawable.icon_eye_hide);
        } else {
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordEye.setImageResource(R.drawable.icon_eye_show);
        }
    }

}
