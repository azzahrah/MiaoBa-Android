package cn.nodemedia.library.view;

import android.content.Context;
import android.text.TextUtils;

import cn.nodemedia.library.rxjava.RxManage;
import cn.nodemedia.library.utils.ToastUtils;

/**
 * Created by Bining.
 */
public abstract class BasePresenter<T extends BaseView> {

    public Context context;
    public RxManage mRxManage;
    public T mView;

    public void initPresenter(T v) {
        this.mView = v;
        context = mView.getContext();
        mRxManage = new RxManage();
        this.onStart();
    }

    public abstract void onStart();

    public void onSucc() {
        if (mView != null)
            mView.hideProgress();
    }

    public void onFail(String strMsg) {
        if (mView != null) {
            mView.hideProgress();
            if (!TextUtils.isEmpty(strMsg))
                ToastUtils.show(context, strMsg);
        }
    }

    public void onDestroy() {
        mView = null;
        context = null;
        mRxManage.clear();
    }

}
