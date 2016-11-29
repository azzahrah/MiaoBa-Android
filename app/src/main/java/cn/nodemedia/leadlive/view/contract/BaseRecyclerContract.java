package cn.nodemedia.leadlive.view.contract;

import android.support.v7.widget.RecyclerView;

import xyz.tanwb.airship.view.BasePresenter;
import xyz.tanwb.airship.view.BaseView;
import xyz.tanwb.airship.view.adapter.BaseRecyclerDivider;
import xyz.tanwb.airship.view.widget.PullToRefreshView;

public interface BaseRecyclerContract {

    interface View extends BaseView {

        PullToRefreshView getPullToRefreshView();

        RecyclerView getRecyclerView();

        void setNoData(boolean isNoData);
    }

    class Presenter<T extends View> extends BasePresenter<T> {

        protected PullToRefreshView pullToRefreshView;
        protected RecyclerView commonRecycler;

        protected BaseRecyclerDivider recyclerDivider;

        @Override
        public void onStart() {
            pullToRefreshView = mView.getPullToRefreshView();
            commonRecycler = mView.getRecyclerView();

            recyclerDivider = new BaseRecyclerDivider();
            commonRecycler.addItemDecoration(recyclerDivider);
        }

    }

}
