package tv.miaoba.live;

import android.content.Context;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;

import butterknife.ButterKnife;
import io.rong.imlib.LiveKit;
import io.rong.imlib.fakeserver.FakeServer;
import xyz.tanwb.airship.App;
import xyz.tanwb.airship.BaseApplication;
import xyz.tanwb.airship.utils.Log;

/**
 * Application
 * Created by Bining.
 */
public class Application extends BaseApplication {

    private static Context context;

    public IWXAPI mWeixinAPI;
    public Tencent mTencent;
    public AuthInfo mAuthInfo;

    @Override
    public void onCreate() {
        super.onCreate();
//        /**
//         * OnCreate 会被多个进程重入，这段保护代码，确保只有您需要使用 RongIMClient 的进程和 Push 进程执行了 init。
//         * io.rong.push 为融云 push 进程名称，不可修改。
//         */
//        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext())) ||
//                "io.rong.push".equals(getCurProcessName(getApplicationContext()))) {
//            RongIMClient.init(this);
//        }

        context = this;
        LiveKit.init(context, FakeServer.getAppKey());

        if (App.isNamedProcess(getPackageName())) {
            Log.e("启动主进程");
            ButterKnife.setDebug(App.isDebug());
            // SDKInitializer.initialize(getApplicationContext());
            // MyMQTTService.startAndConnect(this);
        } else {
            Log.e("启动其他进程");
        }
    }

    public static Context getContext() {
        return context;
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
