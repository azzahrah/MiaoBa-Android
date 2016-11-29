package cn.nodemedia.leadlive.view.contract;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.nodemedia.leadlive.R;
import cn.nodemedia.leadlive.bean.LiveInfo;
import cn.nodemedia.leadlive.utils.HttpCallback;
import cn.nodemedia.leadlive.utils.HttpUtils;
import xyz.tanwb.airship.view.adapter.BaseRecyclerAdapter;
import xyz.tanwb.airship.view.adapter.BaseRecyclerDivider;
import xyz.tanwb.airship.view.adapter.ViewHolderHelper;
import xyz.tanwb.airship.view.adapter.listener.OnLoadMoreListener;
import xyz.tanwb.airship.view.widget.PullToRefreshView;

/**
 * 示例
 */
public interface SimpleContract {

    interface View extends BaseRecyclerContract.View {
    }

    class Presenter extends BaseRecyclerContract.Presenter<View> {

        private SimpleAdapter simpleAdapter;
        private int page = 1;

        @Override
        public void onStart() {
            if (mView != null) {

                pullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
                    @Override
                    public void onRefresh(PullToRefreshView pullToRefreshLayout) {
                        page = 1;
                        getLiveList();
                    }
                });

                simpleAdapter = new SimpleAdapter(mContext);
                simpleAdapter.openLoadAnimation();
                simpleAdapter.openLoadMore(R.layout.layout_list_refresh, 20);
                simpleAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                    @Override
                    public void onLoadMoreClick() {
                        page++;
                        getLiveList();
                    }
                });

                commonRecycler.setLayoutManager(new LinearLayoutManager(mContext));
                commonRecycler.addItemDecoration(new BaseRecyclerDivider());
                commonRecycler.setAdapter(simpleAdapter);

                getLiveList();
            }
        }

        private void getLiveList() {
            HttpUtils.getLiveList(1, page, new HttpCallback<List<LiveInfo>>() {
                @Override
                public void onSuccess(List<LiveInfo> liveInfoAbsL) {
                    if (page == 1) {
                        pullToRefreshView.refreshFinish(true);
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

                @Override
                public void onFailure(String strMsg) {
                    super.onFailure(strMsg);
                    onFail(strMsg);
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
