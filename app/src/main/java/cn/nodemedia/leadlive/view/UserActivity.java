package cn.nodemedia.leadlive.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.lzy.okhttputils.callback.StringCallback;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.nodemedia.leadlive.Constants;
import cn.nodemedia.leadlive.R;
import cn.nodemedia.leadlive.bean.UserInfo;
import cn.nodemedia.leadlive.utils.DBUtils;
import cn.nodemedia.leadlive.utils.HttpUtils;
import cn.nodemedia.library.bean.Abs;
import cn.nodemedia.library.bean.EventBusInfo;
import cn.nodemedia.library.db.DbException;
import cn.nodemedia.library.glide.GlideCircleTransform;
import cn.nodemedia.library.utils.SharedUtils;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 个人信息
 * Created by Bining.
 */
public class UserActivity extends AbsActionbarActivity {

    @InjectView(R.id.user_face)
    ImageView userFace;
    @InjectView(R.id.user_name)
    TextView userName;
    @InjectView(R.id.user_leadid)
    TextView userLeadid;
    @InjectView(R.id.user_sex)
    ImageView userSex;
    @InjectView(R.id.user_auth)
    TextView userAuth;
    @InjectView(R.id.user_age)
    TextView userAge;
    @InjectView(R.id.user_emotion)
    TextView userEmotion;
    @InjectView(R.id.user_city)
    TextView userCity;
    @InjectView(R.id.user_occupation)
    TextView userOccupation;
    @InjectView(R.id.user_weibo)
    TextView userWeibo;

    private int userid;
    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.inject(this);
        setTitle("个人信息");
        userid = SharedUtils.getInt(Constants.USEROPENID, 0);
        initUserData();
        getUsetInfo();
    }

    private void getUsetInfo() {
        HttpUtils.getUserInfo(userid, new StringCallback() {
            @Override
            public void onResponse(boolean isFromCache, String s, Request request, @Nullable Response response) {
                Abs abs = JSON.parseObject(s, Abs.class);
                if (abs.isSuccess()) {
                    UserInfo userInfo = JSON.parseObject(abs.result, UserInfo.class);
                    try {
                        DBUtils.getInstance().saveOrUpdate(userInfo);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void initUserData() {
        try {
            userInfo = DBUtils.getInstance().findById(UserInfo.class, userid);
            if (userInfo != null) {
                Glide.with(mActivity).load(userInfo.faces).asBitmap().error(R.drawable.default_head).transform(new GlideCircleTransform(mActivity)).into(userFace);
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
                ((AbsActivity) mActivity).StartActivity(UserFaceActivity.class, userInfo.faces);
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

    @Override
    public boolean hasEventBus() {
        return true;
    }

    @Override
    public void onSubEvent(EventBusInfo eventBusInfo) {
        super.onSubEvent(eventBusInfo);
        if (eventBusInfo.equals(UserInfo.class.getName())) {
            initUserData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
