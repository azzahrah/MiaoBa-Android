package cn.nodemedia.leadlive.view;

import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageView;

import cn.nodemedia.leadlive.R;

public class PasswordPresenter extends BasePresenter {

    public PasswordPresenter(BaseView view) {
        super(view);
    }

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
