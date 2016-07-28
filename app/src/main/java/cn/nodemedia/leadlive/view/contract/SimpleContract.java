package cn.nodemedia.leadlive.view.contract;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.nodemedia.leadlive.R;
import cn.nodemedia.leadlive.bean.LiveInfo;
import cn.nodemedia.leadlive.utils.HttpUtils;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import xyz.tanwb.treasurechest.bean.AbsL;
import xyz.tanwb.treasurechest.rxjava.schedulers.AndroidSchedulers;
import xyz.tanwb.treasurechest.view.BasePresenter;
import xyz.tanwb.treasurechest.view.BaseView;
import xyz.tanwb.treasurechest.view.adapter.BaseRecyclerAdapter;
import xyz.tanwb.treasurechest.view.adapter.BaseRecyclerDivider;
import xyz.tanwb.treasurechest.view.adapter.ViewHolderHelper;
import xyz.tanwb.treasurechest.view.adapter.listener.OnLoadMoreListener;
import xyz.tanwb.treasurechest.view.widget.PullToRefreshView;

/**
 * 示例
 * Created by Bining on 16/7/5.
 */
public interface SimpleContract {

    interface View extends BaseView {

        PullToRefreshView getRefreshView();

        RecyclerView getRecyclerView();
    }

    class Presenter extends BasePresenter<View> {

        private PullToRefreshView commonPulltorefresh;
        private RecyclerView commonRecycler;
        private SimpleAdapter simpleAdapter;
        private int page = 1;

        @Override
        public void onStart() {
            if (mView != null) {
                commonPulltorefresh = mView.getRefreshView();
                commonRecycler = mView.getRecyclerView();

                commonPulltorefresh.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
                    @Override
                    public void onRefresh(PullToRefreshView pullToRefreshLayout) {
                        page = 1;
                        getLiveList();
                    }
                });

                simpleAdapter = new SimpleAdapter(context);
                simpleAdapter.openLoadAnimation();
                simpleAdapter.openLoadMore(R.layout.layout_list_refresh, 20);
                simpleAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                    @Override
                    public void onLoadMoreClick() {
                        page++;
                        getLiveList();
                    }
                });

                commonRecycler.setLayoutManager(new LinearLayoutManager(context));
                commonRecycler.addItemDecoration(new BaseRecyclerDivider());
                commonRecycler.setAdapter(simpleAdapter);

                getLiveList();
            }
        }

        private void getLiveList() {
            HttpUtils.getLiveList(1, page).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<AbsL<LiveInfo>>() {
                @Override
                public void call(AbsL<LiveInfo> liveInfoAbsL) {

                    if (page == 1) {
                        commonPulltorefresh.refreshFinish(true);
                        simpleAdapter.clearDatas();
                    }

                    List<String> data = new ArrayList<>();
                    for (int i = 1; i < 2; i++) {
                        data.add("测试数据:" + i);
                    }

                    simpleAdapter.notifyDataChangedAfterLoadMore(data);

//                    if (liveInfoAbsL.isSuccess()) {
//                        onSucc();
//                        simpleAdapter.addDatas(data);
//                    } else {
//                        onFail(liveInfoAbsL.getMsg());
//                        simpleAdapter.notifyDataSetChanged();
//                    }
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    onFail(throwable.getMessage());
                }
            });
        }
    }

    class SimpleAdapter extends BaseRecyclerAdapter<String> {

        public SimpleAdapter(Context context) {
            super(context, R.layout.item_live);
        }

        @Override
        protected void setItemData(ViewHolderHelper viewHolderHelper, int position, String model) {
            TextView userName = viewHolderHelper.getView(R.id.user_name);
            userName.setText(model);
        }
    }
}
