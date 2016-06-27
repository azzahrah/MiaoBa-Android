package cn.nodemedia.library.view;

import android.content.Context;

/**
 * Created by Bining.
 */
public interface BaseView {

    Context getContext();

    void showProgress();

    void hideProgress();

    void exit();
}
