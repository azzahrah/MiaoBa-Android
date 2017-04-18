package tv.miaoba.live.view.contract;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.LiveKit;
import io.rong.imlib.fakeserver.FakeServer;
import io.rong.imlib.fakeserver.HttpUtil;
import tv.miaoba.live.Constants;
import tv.miaoba.live.bean.UserInfo;
import tv.miaoba.live.utils.HttpCallback;
import tv.miaoba.live.utils.HttpUtils;
import xyz.tanwb.airship.db.DbException;
import xyz.tanwb.airship.db.DbManager;
import xyz.tanwb.airship.utils.Log;
import xyz.tanwb.airship.utils.SharedUtils;
import xyz.tanwb.airship.utils.ToastUtils;
import xyz.tanwb.airship.view.BaseView;
import xyz.tanwb.airship.view.contract.PermissionsPresenter;

public interface BaseLoginContract {

    interface View extends BaseView {

        void goNextView();
    }

    abstract class Presenter<T extends View> extends PermissionsPresenter<T> {

        public void login(String uname, String pwds, int type) {
            HttpUtils.login(uname, pwds, type, new HttpCallback<UserInfo>() {
                @Override
                public void onSuccess(UserInfo userInfoAbsT) {
                    if (userInfoAbsT != null) {
                        userResultSucc(userInfoAbsT);
                    }
                }

                @Override
                public void onFailure(String strMsg) {
                    super.onFailure(strMsg);
                    if (absS.errCode.equals("0")) {
                        getBindUserInfo();
                    } else {
                        onFail("登陆遇到问题:" + strMsg);
                    }
                }
            });
        }

        public abstract void getBindUserInfo();

        public void regiter(String uname, String pwds, String nick, String faces, String utype) {
            HttpUtils.register(uname, pwds, nick, faces, utype, new HttpCallback<UserInfo>() {
                @Override
                public void onSuccess(UserInfo userInfoAbsT) {
                    if (userInfoAbsT != null) {
                        userResultSucc(userInfoAbsT);
                    }
                }

                @Override
                public void onFailure(String strMsg) {
                    super.onFailure(strMsg);
                    onFail("注册遇到问题:" + strMsg);
                }
            });
        }

        private void userResultSucc(UserInfo userInfo) {
            SharedUtils.put(Constants.USEROPENID, userInfo.userid);
            try {
                DbManager.getInstance().saveOrUpdate(userInfo);
                SharedUtils.put(Constants.USERISLOGIN, true);
                mRxBusManage.post(Constants.USERISLOGIN, true);

                io.rong.imlib.model.UserInfo rcUserInfo = new io.rong.imlib.model.UserInfo(Integer.toString(userInfo.userid), userInfo.nickname != null ? userInfo.nickname : userInfo.username, Uri.parse(userInfo.faces));
                LiveKit.setCurrentUser(rcUserInfo);
                FakeServer.getToken(rcUserInfo, new HttpUtil.OnResponse() {
                    @Override
                    public void onResponse(int code, String body) {
                        Log.e("code:" + code + " body:" + body);
                        if (code != 200) {
                            ToastUtils.show(mContext, body);
                            return;
                        }

                        String token;
                        try {
                            JSONObject jsonObj = new JSONObject(body);
                            token = jsonObj.getString("token");
                            Log.e("token:" + token);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtils.show(mContext, "Token 解析失败!");
                            return;
                        }

                        LiveKit.connect(token, new RongIMClient.ConnectCallback() {
                            @Override
                            public void onTokenIncorrect() {
                                Log.d("connect onTokenIncorrect");
                                // 检查appKey 与token是否匹配.
                            }

                            @Override
                            public void onSuccess(String userId) {
                                Log.e("connect onSuccess:" + userId);
                                onSucc();
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {
                                Log.d("connect onError = " + errorCode);
                                // 根据errorCode 检查原因.
                            }
                        });
                    }
                });
            } catch (DbException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSucc() {
            super.onSucc();
            if (mView != null) {
                mView.goNextView();
                mView.exit();
            }
        }
    }
}
