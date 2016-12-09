package tv.miaoba.live.view.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import tv.miaoba.live.Constants;
import tv.miaoba.live.R;
import tv.miaoba.live.bean.UserInfo;
import tv.miaoba.live.view.UserFaceActivity;
import tv.miaoba.live.view.UserFollowActivity;
import tv.miaoba.live.view.UserInfoActivity;
import rx.functions.Action1;
import xyz.tanwb.airship.db.DbException;
import xyz.tanwb.airship.db.DbManager;
import xyz.tanwb.airship.glide.GlideManager;
import xyz.tanwb.airship.rxjava.RxBusManage;
import xyz.tanwb.airship.utils.SharedUtils;
import xyz.tanwb.airship.view.BaseFragment;

/**
 * 个人中心
 * Create in Bining.
 */
public class UserFragment extends BaseFragment {

    @BindView(R.id.main_chat)
    ImageView mainChat;
    @BindView(R.id.main_search)
    ImageView mainSearch;
    @BindView(R.id.main_me_diamonds)
    TextView mainMeDiamonds;
    @BindView(R.id.main_actionbar)
    RelativeLayout mainActionbar;
    @BindView(R.id.user_face)
    ImageView userFace;
    @BindView(R.id.user_leadid)
    TextView userLeadid;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.user_sex)
    ImageView userSex;
    @BindView(R.id.user_rank)
    ImageView userRank;
    @BindView(R.id.user_info_edit)
    ImageView userInfoEdit;
    @BindView(R.id.user_follow)
    TextView userFollow;
    @BindView(R.id.user_fans)
    TextView userFans;
    @BindView(R.id.user_autograph)
    TextView userAutograph;
    @BindView(R.id.user_live_icon)
    ImageView userLiveIcon;
    @BindView(R.id.user_live_text)
    TextView userLiveText;
    @BindView(R.id.user_live)
    RelativeLayout userLive;
    @BindView(R.id.user_level_icon)
    ImageView userLevelIcon;
    @BindView(R.id.user_level_text)
    TextView userLevelText;
    @BindView(R.id.user_level)
    RelativeLayout userLevel;
    @BindView(R.id.user_gain_icon)
    ImageView userGainIcon;
    @BindView(R.id.user_gain_text)
    TextView userGainText;
    @BindView(R.id.user_gain)
    RelativeLayout userGain;
    @BindView(R.id.user_account_icon)
    ImageView userAccountIcon;
    @BindView(R.id.user_account_text)
    TextView userAccountText;
    @BindView(R.id.user_account)
    RelativeLayout userAccount;
    @BindView(R.id.user_ticket_champion)
    ImageView userTicketChampion;
    @BindView(R.id.user_ticket_runnerup)
    ImageView userTicketRunnerup;
    @BindView(R.id.user_ticket_third)
    ImageView userTicketThird;
    @BindView(R.id.user_ticket)
    RelativeLayout userTicket;
    @BindView(R.id.user_auth)
    TextView userAuth;
    @BindView(R.id.user_setting)
    TextView userSetting;

    private boolean isLogin = false;
    private int userid;
    private UserInfo userInfo;

    public RxBusManage mRxManage;

    public static UserFragment newInstance() {
        return new UserFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_user;
    }

    @Override
    public void initView(View view, Bundle bundle) {
    }

    @Override
    public void initPresenter() {
        mRxManage = new RxBusManage();
        mRxManage.on(UserInfo.class.getName(), new Action1<Object>() {
            @Override
            public void call(Object o) {
                initUserData();
            }
        });
        mainMeDiamonds.setVisibility(View.VISIBLE);
        isLogin = SharedUtils.getBoolean(Constants.USERISLOGIN, false);
        userid = SharedUtils.getInt(Constants.USEROPENID, 0);
        initUserData();
    }

    private void initUserData() {
        if (!isLogin) return;
        try {
            userInfo = DbManager.getInstance().findById(UserInfo.class, userid);
            if (userInfo != null) {
                mainMeDiamonds.setText("送出 0");
                GlideManager.load(mActivity, userInfo.faces).placeholder(R.drawable.default_head).error(R.drawable.default_head).setTransform(GlideManager.IMAGE_TYPE_CIRCLE).into(userFace);
                userLeadid.setText("主播号: " + userInfo.userid);
                userName.setText(TextUtils.isEmpty(userInfo.nickname) ? (TextUtils.isEmpty(userInfo.username) ? ("用户" + userInfo.userid) : userInfo.username) : userInfo.nickname);
                userSex.setImageResource(userInfo.sex == 2 ? R.drawable.global_female : R.drawable.global_male);
                userRank.setImageResource(R.drawable.rank_1);
                userFollow.setText("关注 " + userInfo.follow);
                userFans.setText("粉丝 " + userInfo.fans);
                userAutograph.setText(TextUtils.isEmpty(userInfo.autograph) ? "Ta好像忘记写签名了..." : userInfo.autograph);
                userLiveText.setText("0个");
                userLevelText.setText(userInfo.grade + "级");
                userGainText.setText("0币");
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
                mActivity.advance(UserFaceActivity.class, userInfo.faces);
                break;
            case R.id.user_info_edit:
                mActivity.advance(UserInfoActivity.class);
                break;
            case R.id.user_follow:
                mActivity.advance(UserFollowActivity.class, 1);
                break;
            case R.id.user_fans:
                mActivity.advance(UserFollowActivity.class, 0);
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
}