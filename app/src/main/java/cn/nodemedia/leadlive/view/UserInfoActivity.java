package cn.nodemedia.leadlive.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.nodemedia.leadlive.Constants;
import cn.nodemedia.leadlive.R;
import cn.nodemedia.leadlive.bean.UserInfo;
import cn.nodemedia.leadlive.utils.HttpCallback;
import cn.nodemedia.leadlive.utils.HttpUtils;
import xyz.tanwb.airship.bean.AbsT;
import xyz.tanwb.airship.db.DbException;
import xyz.tanwb.airship.db.DbManager;
import xyz.tanwb.airship.glide.GlideManager;
import xyz.tanwb.airship.utils.SharedUtils;

/**
 * 个人信息
 * Created by Bining.
 */
public class UserInfoActivity extends ActionbarActivity {

    @BindView(R.id.user_face)
    ImageView userFace;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.user_leadid)
    TextView userLeadid;
    @BindView(R.id.user_sex)
    ImageView userSex;
    @BindView(R.id.user_auth)
    TextView userAuth;
    @BindView(R.id.user_age)
    TextView userAge;
    @BindView(R.id.user_emotion)
    TextView userEmotion;
    @BindView(R.id.user_city)
    TextView userCity;
    @BindView(R.id.user_occupation)
    TextView userOccupation;
    @BindView(R.id.user_weibo)
    TextView userWeibo;

    private int userid;
    private UserInfo userInfo;

    @Override
    public int getLayoutId() {
        return R.layout.activity_user;
    }

    @Override
    public void initView(Bundle bundle) {
        super.initView(bundle);
        ButterKnife.bind(this);
        setTitle("个人信息");
        userid = SharedUtils.getInt(Constants.USEROPENID, 0);
        initUserData();
        getUsetInfo();
    }

    @Override
    public void initPresenter() {
    }

    private void getUsetInfo() {
        HttpUtils.getUserInfo(userid, new HttpCallback<UserInfo>() {
            @Override
            public void onSuccess(UserInfo userInfoAbsT) {
                SharedUtils.put(Constants.USEROPENID, userInfoAbsT.userid);
                try {
                    DbManager.getInstance().saveOrUpdate(userInfo);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String strMsg) {
                super.onFailure(strMsg);

            }
        });
    }

    private void initUserData() {
        try {
            userInfo = DbManager.getInstance().findById(UserInfo.class, userid);
            if (userInfo != null) {
                GlideManager.load(mActivity, userInfo.faces).placeholder(R.drawable.default_head).error(R.drawable.default_head).setTransform(GlideManager.IMAGE_TYPE_CIRCLE).into(userFace);
                userName.setText(TextUtils.isEmpty(userInfo.nickname) ? "未设置" : userInfo.nickname);
                userLeadid.setText(userInfo.userid + "");
                userSex.setImageResource(userInfo.sex == 2 ? R.drawable.global_female : R.drawable.global_male);
                userAge.setText(TextUtils.isEmpty(userInfo.age) ? "你猜" : userInfo.age);
                userEmotion.setText(TextUtils.isEmpty(userInfo.emotion) ? "保密" : userInfo.emotion);
                userCity.setText(TextUtils.isEmpty(userInfo.cityid) ? "火星" : userInfo.cityid);
                userOccupation.setText(TextUtils.isEmpty(userInfo.occupation) ? "主播" : userInfo.occupation);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.user_face_layout, R.id.user_name, R.id.user_sex_layout, R.id.user_autograph, R.id.user_identity, R.id.user_auth, R.id.user_age, R.id.user_emotion, R.id.user_city, R.id.user_occupation, R.id.user_weibo})
    public void onClick(View v) {
        if (!isCanClick(v)) return;
        switch (v.getId()) {
            case R.id.user_face_layout:
                advance(UserFaceActivity.class, userInfo.faces);
                break;
            case R.id.user_name:
                break;
            case R.id.user_sex_layout:
                break;
            case R.id.user_autograph:
                break;
            case R.id.user_identity:
                break;
            case R.id.user_auth:
                break;
            case R.id.user_age:
                break;
            case R.id.user_emotion:
                break;
            case R.id.user_city:
                break;
            case R.id.user_occupation:
                break;
            case R.id.user_weibo:
                break;
        }
    }

//    @Override
//    public boolean hasEventBus() {
//        return true;
//    }
//
//    @Override
//    public void onSubEvent(EventBusInfo eventBusInfo) {
//        super.onSubEvent(eventBusInfo);
//        if (eventBusInfo.equals(UserInfo.class.getName())) {
//            initUserData();
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
