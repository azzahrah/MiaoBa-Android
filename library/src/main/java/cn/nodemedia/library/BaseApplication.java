package cn.nodemedia.library;

import android.app.Activity;
import android.app.Application;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;

import java.util.LinkedList;
import java.util.List;

import cn.nodemedia.library.utils.Log;

/**
 * MyApplication
 * Created by Bining.
 */
public class BaseApplication extends Application {

    private List<Activity> activityList = new LinkedList<>();

    public IWXAPI mWeixinAPI;
    public Tencent mTencent;
    public AuthInfo mAuthInfo;

    @Override
    public void onCreate() {
        super.onCreate();
        //LeakCanary.install(this);
        App.Ext.init(this);
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

    public void addActivity(Activity activity) {
        Log.d("Activity创建 " + activity.getClass().getName());
        activityList.add(activity);
    }

    public void removeActivity(Activity activity) {
        Log.d("Activity移除 " + activity.getClass().getName());
        activityList.remove(activity);
    }

    public void removeActivity(String activityName) {
        Log.d("Activity移除 " + activityName);
        for (Activity activity : activityList) {
            if (activity.getClass().getName().equals(activityName)) {
                activity.finish();
            }
        }
    }

    public void exit() {
        for (Activity activity : activityList) {
            Log.d("Activit销毁 " + activity.getClass().getName());
            activity.finish();
        }
    }

    public void exitOtherActivity(Activity activity) {
        for (Activity a : activityList) {
            if (a != activity) {
                Log.d("Activit销毁 " + a.getClass().getName());
                a.finish();
            }
        }
    }
}
