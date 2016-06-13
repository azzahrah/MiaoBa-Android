package cn.nodemedia.leadlive.view;

import android.content.Context;

/**
 * 基础视图实现
 * Created by Bining.
 */
public interface BaseView {

    Context getContext();

    void showProgress();

    void hideProgress();

    void exitActivity();

}
