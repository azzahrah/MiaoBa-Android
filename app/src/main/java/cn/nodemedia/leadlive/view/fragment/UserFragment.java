package cn.nodemedia.leadlive.view.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.nodemedia.leadlive.Constants;
import cn.nodemedia.leadlive.R;
import cn.nodemedia.leadlive.bean.UserInfo;
import cn.nodemedia.leadlive.utils.DBUtils;
import cn.nodemedia.leadlive.view.AbsActivity;
import cn.nodemedia.leadlive.view.UserActivity;
import cn.nodemedia.leadlive.view.UserFaceActivity;
import cn.nodemedia.leadlive.view.UserFansActivity;
import cn.nodemedia.leadlive.view.UserFollowActivity;
import cn.nodemedia.library.bean.EventBusInfo;
import cn.nodemedia.library.db.DbException;
import cn.nodemedia.library.glide.GlideCircleTransform;
import cn.nodemedia.library.utils.SharedUtils;

/**
 * 个人中心
 * Create in Bining.
 */
public class UserFragment extends AbsActionbarFragment {

    @InjectView(R.id.main_live_tab)
    LinearLayout mainLiveTab;
    @InjectView(R.id.main_me_diamonds)
    TextView mainMeDiamonds;
    @InjectView(R.id.user_face)
    ImageView userFace;
    @InjectView(R.id.user_leadid)
    TextView userLeadid;
    @InjectView(R.id.user_name)
    TextView userName;
    @InjectView(R.id.user_sex)
    ImageView userSex;
    @InjectView(R.id.user_rank)
    ImageView userRank;
    @InjectView(R.id.user_follow)
    TextView userFollow;
    @InjectView(R.id.user_fans)
    TextView userFans;
    @InjectView(R.id.user_autograph)
    TextView userAutograph;

    @InjectView(R.id.user_live_text)
    TextView userLiveText;
    @InjectView(R.id.user_level_text)
    TextView userLevelText;
    @InjectView(R.id.user_gain_text)
    TextView userGainText;
    @InjectView(R.id.user_account_text)
    TextView userAccountText;

    @InjectView(R.id.user_ticket_champion)
    ImageView userTicketChampion;
    @InjectView(R.id.user_ticket_runnerup)
    ImageView userTicketRunnerup;
    @InjectView(R.id.user_ticket_third)
    ImageView userTicketThird;

    private boolean isLogin = false;
    private int userid;
    private UserInfo userInfo;

    public static UserFragment newInstance() {
        return new UserFragment();
    }

    @Override
    public View onCreateView(Bundle savedInstanceState, ViewGroup container, LayoutInflater inflater) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        hasActionBar(View.GONE);
        initView();
        initUserData();
    }

    private void initView() {
        mainLiveTab.setVisibility(View.GONE);
        mainMeDiamonds.setVisibility(View.VISIBLE);

        isLogin = SharedUtils.getBoolean(Constants.USERISLOGIN, false);
        userid = SharedUtils.getInt(Constants.USEROPENID, 0);
    }

    private void initUserData() {
        if (!isLogin) return;
        try {
            userInfo = DBUtils.getInstance().findById(UserInfo.class, userid);
            if (userInfo != null) {
                mainMeDiamonds.setText("送出 0");
                Glide.with(mActivity).load(userInfo.faces).asBitmap().error(R.drawable.default_head).transform(new GlideCircleTransform(mActivity)).into(userFace);
                userLeadid.setText("主播号: " + userInfo.userid);
                userName.setText(TextUtils.isEmpty(userInfo.nickname) ? (TextUtils.isEmpty(userInfo.username) ? ("用户" + userInfo.userid) : userInfo.username) : userInfo.nickname);
                userSex.setImageResource(userInfo.sex == 2 ? R.drawable.global_female : R.drawable.global_male);
                userRank.setImageResource(R.drawable.rank_1);
                userFollow.setText("关注 " + userInfo.follow);
                userFans.setText("粉丝 " + userInfo.fans);
                userAutograph.setText(TextUtils.isEmpty(userInfo.autograph) ? "Ta好像忘记写签名了..." : userInfo.autograph);
                userLiveText.setText("0个");
                userLevelText.setText(userInfo.grade + "级");
                userGainText.setText("0");
                userAccountText.setText("0");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.main_chat, R.id.main_search, R.id.user_face, R.id.user_info_edit, R.id.user_follow, R.id.user_fans, R.id.user_live,
            R.id.user_level, R.id.user_gain, R.id.user_account, R.id.user_ticket, R.id.user_auth, R.id.user_setting})
    public void onClick(View v) {
        if (!isCanClick(v)) return;
        switch (v.getId()) {
            case R.id.main_chat:
                //((AbsActivity) mActivity).StartActivity(SettingActivity.class);
                break;
            case R.id.main_search:
                //((AbsActivity) mActivity).StartActivity(MessageListActivity.class);
                break;
            case R.id.user_face:
                ((AbsActivity) mActivity).StartActivity(UserFaceActivity.class, userInfo.faces);
                break;
            case R.id.user_info_edit:
                ((AbsActivity) mActivity).StartActivity(UserActivity.class);
                break;
            case R.id.user_follow:
                ((AbsActivity) mActivity).StartActivity(UserFollowActivity.class);
                break;
            case R.id.user_fans:
                ((AbsActivity) mActivity).StartActivity(UserFansActivity.class);
                break;
            case R.id.user_live:
                break;
            case R.id.user_level:
                break;
            case R.id.user_gain:
                break;
            case R.id.user_account:
                break;
            case R.id.user_ticket:
                break;
            case R.id.user_auth:
                break;
            case R.id.user_setting:
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
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

}