package tv.miaoba.live;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;

import butterknife.ButterKnife;
import xyz.tanwb.airship.App;
import xyz.tanwb.airship.BaseApplication;
import xyz.tanwb.airship.utils.Log;

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
        if (App.isNamedProcess(getPackageName())) {
            Log.e("启动主进程");
            ButterKnife.setDebug(App.isDebug());
            // SDKInitializer.initialize(getApplicationContext());
            // MyMQTTService.startAndConnect(this);
        } else {
            Log.e("启动其他进程");
        }
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
