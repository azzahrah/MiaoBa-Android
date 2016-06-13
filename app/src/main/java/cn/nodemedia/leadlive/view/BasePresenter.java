package cn.nodemedia.leadlive.view;

import android.content.Context;

import cn.nodemedia.library.utils.ToastUtils;

public abstract class BasePresenter {

    protected Context context;
    private BaseView view;

    public BasePresenter(BaseView view) {
        this.view = view;
        this.context = view.getContext();
    }

    public void showProgress() {
        if (view != null) {
            view.showProgress();
        }
    }

    public void hindPrgress() {
        if (view != null) {
            view.hideProgress();
        }
    }

    public void onSucc() {
        hindPrgress();
    }

    public void onFail(String strMsg) {
        hindPrgress();
        ToastUtils.show(context, strMsg);
    }

    public void onDestroy() {
        view = null;
    }

}
