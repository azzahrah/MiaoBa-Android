package cn.nodemedia.leadlive.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.text.TextUtils;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.User;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cn.nodemedia.leadlive.utils.HttpUtils;
import cn.nodemedia.library.BaseApplication;
import cn.nodemedia.library.utils.Log;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public interface LoginBindContract {

    interface View extends LoginBaseContract.View {

        BaseApplication getBaseApplication();
    }

    class Presenter extends LoginBaseContract.Presenter<View> {

        public static final int LTYPE_WX = 0X00000001;
        public static final int LTYPE_QQ = 0X00000002;
        public static final int LTYPE_SINA = 0X00000003;

        @IntDef({LTYPE_WX, LTYPE_QQ, LTYPE_SINA})
        @Retention(RetentionPolicy.SOURCE)
        public @interface LoginType {
        }

        private int loginType;
        private boolean isLoginToBind;

        private IWXAPI mIWXAPI;
        private String wxOpenId;
        private String wxToken;

        private Tencent mTencent;
        private SsoHandler mSsoHandler;
        private Oauth2AccessToken mAccessToken;

        @Override
        public void onStart() {
        }

        public boolean loginToBind(@LoginType int loginType) {
            this.loginType = loginType;
            isLoginToBind = false;
            if (mView != null) {
                switch (loginType) {
                    case LTYPE_WX:
                        mRxManage.on(LoginBindContract.class.getName(), new Action1<Object>() {
                            @Override
                            public void call(Object o) {
                                getAccessToken(o.toString());
                            }
                        });

                        SendAuth.Req req = new SendAuth.Req();
                        req.scope = "snsapi_userinfo";
                        req.state = "warehouse9_customer";
                        mIWXAPI = mView.getBaseApplication().getIWXAPI();
                        isLoginToBind = mIWXAPI != null && mIWXAPI.sendReq(req);
                        break;
                    case LTYPE_QQ:
                        mTencent = mView.getBaseApplication().getTencent();
                        if (!mTencent.isSessionValid()) {
                            //SCOPE说明 例如：SCOPE = “get_user_info,add_t”；所有权限用“all”
                            mTencent.login((Activity) context, "all", qqIUiListener);
                            isLoginToBind = true;
                        }
                        break;
                    case LTYPE_SINA:
                        mSsoHandler = new SsoHandler((Activity) context, mView.getBaseApplication().getWeibo());
                        mSsoHandler.authorize(weiboAuthListener);
                        isLoginToBind = true;
                        break;
                    default:
                        break;
                }
            }
            return isLoginToBind;
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (isLoginToBind) {
                switch (loginType) {
                    case LTYPE_QQ:
                        if (requestCode == Constants.REQUEST_LOGIN || requestCode == Constants.REQUEST_APPBAR) {
                            Tencent.onActivityResultData(requestCode, resultCode, data, qqIUiListener);
                        }
                        break;
                    case LTYPE_SINA:
                        if (mSsoHandler != null) {
                            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
                        }
                        break;
                }
            }
        }

        @Override
        public void getBindUserInfo() {
            switch (loginType) {
                case LTYPE_WX:
                    HttpUtils.getWXUserInfo(wxToken, wxOpenId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
                        @Override
                        public void call(String response) {
                            // {
                            // "openid":"OPENID", 普通用户的标识，对当前开发者帐号唯一
                            // "nickname":"NICKNAME", 普通用户昵称
                            // "sex":1, 普通用户性别，1为男性，2为女性
                            // "province":"PROVINCE", 普通用户个人资料填写的省份
                            // "city":"CITY", 普通用户个人资料填写的城市
                            // "country":"COUNTRY", 国家，如中国为CN
                            // "headimgurl": "http://wx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/0", 用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空
                            // "privilege":["PRIVILEGE1","PRIVILEGE2"], 用户特权信息，json数组，如微信沃卡用户为（chinaunicom）
                            // "unionid": " o6_bmasdasdsad6_2sgVt7hMZOPfL" 用户统一标识。针对一个微信开放平台帐号下的应用，同一用户的unionid是唯一的
                            // }

                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                if (jsonResponse.has("nickname") && jsonResponse.has("headimgurl")) {
                                    String nickName = jsonResponse.getString("nickname");
                                    String faceUrl = jsonResponse.getString("headimgurl");
                                    regiter(wxOpenId, null, nickName, faceUrl, "WeiXin");
                                } else {
                                    onFail("微信遇到问题:获取用户信息失败.");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            onFail("微信遇到问题:" + throwable.getMessage());
                        }
                    });
                    break;
                case LTYPE_QQ:
                    if (mTencent != null && mTencent.isSessionValid()) {
                        UserInfo userInfo = new UserInfo(context, mTencent.getQQToken());
                        userInfo.getUserInfo(new BaseUiListener() {

                            @Override
                            protected void doComplete(JSONObject jsonObject) {
                                try {
                                    if (jsonObject.has("nickname") && jsonObject.has("figureurl_qq_2")) {
                                        String nickName = jsonObject.getString("nickname");
                                        String faces = jsonObject.getString("figureurl_qq_2");
                                        regiter(mTencent.getOpenId(), null, nickName, faces, "QQ");
                                    } else {
                                        onFail("QQ遇到问题:获取用户信息失败.");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    break;
                case LTYPE_SINA:
                    HttpUtils.getSinaUserInfo(mAccessToken.getToken(), mAccessToken.getUid()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
                        @Override
                        public void call(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                if (jsonResponse.has("name") && jsonResponse.has("profile_image_url")) {
                                    String nickName = jsonResponse.getString("name");
                                    String faceUrl = jsonResponse.getString("profile_image_url");
                                    regiter(mAccessToken.getUid(), null, nickName, faceUrl, "Sina");
                                } else {
                                    onFail("微博遇到问题:获取用户信息失败.");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            onFail("微博遇到问题:" + throwable.getMessage());
                        }
                    });

//                    UsersAPI usersAPI = new UsersAPI(context, "1331670181", mAccessToken);
//                    usersAPI.show(mAccessToken.getUid(), new RequestListener() {
//                        @Override
//                        public void onComplete(String response) {
//                            Log.e(response);
//                            if (!TextUtils.isEmpty(response)) {
//                                // 调用 User#parse 将JSON串解析成User对象
//                                User user = User.parse(response);
//                                if (user != null) {
//                                    regiter(user.idstr, null, user.screen_name, user.profile_image_url, "Sina");
//                                } else {
//                                    onFail("微博遇到问题:获取用户信息失败.");
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onWeiboException(WeiboException e) {
//                            onFail("微博遇到问题:" + e.getMessage());
//                        }
//                    });
                    break;
                default:
                    break;
            }
        }

        public void getAccessToken(String wxCode) {
            HttpUtils.getWXToken(wxCode).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
                @Override
                public void call(String response) {
                    // {
                    // "access_token":"ACCESS_TOKEN", 接口调用凭证
                    // "expires_in":7200, access_token接口调用凭证超时时间，单位（秒）
                    // "refresh_token":"REFRESH_TOKEN", 用户刷新access_token
                    // "openid":"OPENID", 授权用户唯一标识
                    // "scope":"SCOPE", 用户授权的作用域，使用逗号（,）分隔
                    // "unionid":"o6_bmasdasdsad6_2sgVt7hMZOPfL"  用户统一标识。针对一个微信开放平台帐号下的应用，同一用户的unionid是唯一的
                    // }
                    Log.e(response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        wxOpenId = jsonResponse.getString("openid");
                        wxToken = jsonResponse.getString("access_token");

                        login(wxOpenId, null, 2);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    onFail("微信遇到问题:" + throwable.getMessage());
                }
            });
        }

        WeiboAuthListener weiboAuthListener = new WeiboAuthListener() {
            @Override
            public void onComplete(Bundle values) {
                // 从 Bundle 中解析 Token
                mAccessToken = Oauth2AccessToken.parseAccessToken(values);
                if (mAccessToken.isSessionValid()) {
                    login(mAccessToken.getUid(), null, 2);
                } else {
                    // 当您注册的应用程序签名不正确时，就会收到 Code，请确保签名正确
                    String code = values.getString("code", "");
                    onFail("微博遇到问题:签名错误,code = " + code);
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                onFail("微博遇到问题:" + e.getMessage());
            }

            @Override
            public void onCancel() {
                onFail("您已取消操作.");
            }
        };

        IUiListener qqIUiListener = new BaseUiListener() {

            @Override
            protected void doComplete(JSONObject jsonObject) {
                try {
                    String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
                    String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
                    String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
                    if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires) && !TextUtils.isEmpty(openId)) {
                        mTencent.setAccessToken(token, expires);
                        mTencent.setOpenId(openId);

                        login(openId, null, 2);
                    } else {
                        onFail("QQ遇到问题:获取认证失败.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        private class BaseUiListener implements IUiListener {

            @Override
            public void onComplete(Object response) {
                if (response == null) {
                    onFail("QQ遇到问题:获取数据失败.");
                    return;
                }
                doComplete((JSONObject) response);
            }

            protected void doComplete(JSONObject values) {
                // 登陆返回数据
                // {
                // "ret":0,
                // "pay_token":"xxxxxxxxxxxxxxxx",
                // "pf":"openmobile_android",
                // "expires_in":"7776000",
                // "openid":"xxxxxxxxxxxxxxxxxxx",
                // "pfkey":"xxxxxxxxxxxxxxxxxxx",
                // "msg":"sucess",
                // "access_token":"xxxxxxxxxxxxxxxxxxxxx"
                // }

                // 获取用户数据
                // {
                // "is_yellow_year_vip": "0",
                // "ret": 0,
                // "figureurl_qq_1": "http://q.qlogo.cn/qqapp/222222/8C75BBE3DC6B0E9A64BD31449A3C8CB0/40",
                // "figureurl_qq_2": "http://q.qlogo.cn/qqapp/222222/8C75BBE3DC6B0E9A64BD31449A3C8CB0/100",
                // "nickname": "小罗",
                // "yellow_vip_level": "0",
                // "msg": "",
                // "figureurl_1": "http://qzapp.qlogo.cn/qzapp/222222/8C75BBE3DC6B0E9A64BD31449A3C8CB0/50",
                // "vip": "0",
                // "level": "0",
                // "figureurl_2": "http://qzapp.qlogo.cn/qzapp/222222/8C75BBE3DC6B0E9A64BD31449A3C8CB0/100",
                // "is_yellow_vip": "0",
                // "gender": "男",
                // "figureurl":"http://qzapp.qlogo.cn/qzapp/222222/8C75BBE3DC6B0E9A64BD31449A3C8CB0/30"
                // }
            }

            @Override
            public void onError(UiError uiError) {
                Log.e("code:" + uiError.errorCode + ", msg:" + uiError.errorMessage + ", detail:" + uiError.errorDetail);
                onFail("QQ遇到问题:" + uiError.errorMessage);
            }

            @Override
            public void onCancel() {
                onFail("您已取消操作.");
            }
        }
    }
}
