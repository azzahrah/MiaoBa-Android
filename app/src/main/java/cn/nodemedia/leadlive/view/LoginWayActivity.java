package cn.nodemedia.leadlive.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.nodemedia.leadlive.R;
import cn.nodemedia.library.view.BaseActivity;

/**
 * 登陆方式选择
 * Created by Bining.
 */
public class LoginWayActivity extends BaseActivity {

    @InjectView(R.id.login_way_qq)
    ImageView loginWayQq;
    @InjectView(R.id.login_way_wx)
    ImageView loginWayWx;
    @InjectView(R.id.login_way_xl)
    ImageView loginWayXl;
    @InjectView(R.id.login_way_phone)
    ImageView loginWayPhone;
    @InjectView(R.id.login_way_protocol)
    TextView loginWayProtocol;

    @Override
    public int getLayoutId() {
        return R.layout.activity_login_way;
    }

    @Override
    public void initView() {
        ButterKnife.inject(this);
    }

    @Override
    public void initPresenter() {
    }

    @OnClick({R.id.login_way_qq, R.id.login_way_wx, R.id.login_way_xl, R.id.login_way_phone})
    public void onClick(View v) {
        if (!isCanClick(v)) return;
        switch (v.getId()) {
            case R.id.login_way_qq:
                break;
            case R.id.login_way_wx:
                break;
            case R.id.login_way_xl:
                break;
            case R.id.login_way_phone:
                StartActivity(LoginActivity.class);
                finish();
                break;
        }
    }

}

