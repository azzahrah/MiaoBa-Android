package cn.nodemedia.leadlive.view.contract;

import cn.nodemedia.leadlive.Constants;
import cn.nodemedia.leadlive.bean.UserInfo;
import cn.nodemedia.leadlive.utils.DBUtils;
import cn.nodemedia.leadlive.utils.HttpUtils;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import xyz.tanwb.treasurechest.bean.AbsT;
import xyz.tanwb.treasurechest.db.DbException;
import xyz.tanwb.treasurechest.rxjava.schedulers.AndroidSchedulers;
import xyz.tanwb.treasurechest.utils.SharedUtils;
import xyz.tanwb.treasurechest.view.BaseView;
import xyz.tanwb.treasurechest.view.contract.PasswordPresenter;

public interface LoginBaseContract {

    interface View extends BaseView {

        void goNextView();
    }

    abstract class Presenter<T extends View> extends PasswordPresenter<T> {

        public void login(String uname, String pwds, int type) {
            HttpUtils.login(uname, pwds, type).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<AbsT<UserInfo>>() {
                @Override
                public void call(AbsT<UserInfo> userInfoAbsT) {
                    if (userInfoAbsT.isSuccess()) {
                        userResultSucc(userInfoAbsT.result);
                    } else if (userInfoAbsT.errCode.equals("0")) {
                        getBindUserInfo();
                    } else {
                        onFail("登陆遇到问题:" + userInfoAbsT.getMsg());
                    }
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    onFail("登陆遇到问题:" + throwable.getMessage());
                }
            });
        }

        public abstract void getBindUserInfo();

        public void regiter(String uname, String pwds, String nick, String faces, String utype) {
            HttpUtils.register(uname, pwds, nick, faces, utype).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<AbsT<cn.nodemedia.leadlive.bean.UserInfo>>() {
                @Override
                public void call(AbsT<UserInfo> userInfoAbsT) {
                    if (userInfoAbsT.isSuccess()) {
                        userResultSucc(userInfoAbsT.result);
                    } else {
                        onFail("注册遇到问题:" + userInfoAbsT.getMsg());
                    }
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    onFail("注册遇到问题:" + throwable.getMessage());
                }
            });
        }

        private void userResultSucc(UserInfo userInfo) {
            SharedUtils.put(Constants.USEROPENID, userInfo.userid);
            try {
                DBUtils.getInstance().saveOrUpdate(userInfo);
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
