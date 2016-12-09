package tv.miaoba.live.view.contract;

import tv.miaoba.live.Constants;
import tv.miaoba.live.bean.UserInfo;
import tv.miaoba.live.utils.HttpCallback;
import tv.miaoba.live.utils.HttpUtils;
import xyz.tanwb.airship.db.DbException;
import xyz.tanwb.airship.db.DbManager;
import xyz.tanwb.airship.utils.SharedUtils;
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
                onSucc();
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