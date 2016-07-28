package cn.nodemedia.leadlive;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;

import butterknife.ButterKnife;
import xyz.tanwb.treasurechest.App;
import xyz.tanwb.treasurechest.BaseApplication;

/**
 * Application
 * Created by Bining.
 */
public class Application extends BaseApplication {

    public IWXAPI mWeixinAPI;
    public Tencent mTencent;
    public AuthInfo mAuthInfo;

    @Override
    public void onCreate() {
        super.onCreate();
        ButterKnife.setDebug(App.isDebug());
    }

    public IWXAPI getIWXAPI() {
        if (mWeixinAPI == null) {
            mWeixinAPI = WXAPIFactory.createWXAPI(this, "appid", false);
            mWeixinAPI.registerApp("appid");
        }
        return mWeixinAPI;
    }

    public Tencent getTencent() {
        if (mTencent == null) {
            mTencent = Tencent.createInstance("appid", this);
        }
        return mTencent;
    }

    public AuthInfo getWeibo() {
        if (mAuthInfo == null) {
            mAuthInfo = new AuthInfo(this, "1331670181", "https://api.weibo.com/oauth2/default.html", "email,direct_messages_read,direct_messages_write,friendships_groups_read,friendships_groups_write,statuses_to_me_read,follow_app_official_microblog,invitation_write");
        }
        return mAuthInfo;
    }
}
