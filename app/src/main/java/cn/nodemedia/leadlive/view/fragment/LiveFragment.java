package cn.nodemedia.leadlive.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import cn.nodemedia.leadlive.R;
import cn.nodemedia.leadlive.view.contract.LiveContract;
import xyz.tanwb.airship.view.BaseFragment;
import xyz.tanwb.airship.view.widget.PullToRefreshView;

/**
 * 直播列表
 * Create in Bining.
 */
public class LiveFragment extends BaseFragment<LiveContract.Presenter> implements LiveContract.View {

    @BindView(R.id.common_recycler)
    RecyclerView commonRecycler;
    @BindView(R.id.common_refresh)
    PullToRefreshView commonRefresh;
    @BindView(R.id.common_nodata_content)
    TextView commonNodataContent;
    @BindView(R.id.common_nodata_icon)
    ImageView commonNodataIcon;
    @BindView(R.id.common_nodata_subtitle)
    TextView commonNodataSubtitle;
    @BindView(R.id.common_nodata)
    RelativeLayout commonNodata;

    public static LiveFragment newInstance(int liveType) {
        LiveFragment liveFragment = new LiveFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("liveType", liveType);
        liveFragment.setArguments(bundle);
        return liveFragment;
    }

    private int liveType;

    @Override
    public int getLayoutId() {
        return R.layout.layout_recycler_refresh;
    }

    @Override
    public void initView(View view, Bundle bundle) {
        if (bundle != null) {
            liveType = bundle.getInt("liveType", 0);
        } else {
            liveType = getArguments().getInt("liveType", 0);
        }
    }

    @Override
    public void initPresenter() {
        mPresenter.initPresenter(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("liveType", liveType);
    }

    @Override
    public PullToRefreshView getPullToRefreshView() {
        return commonRefresh;
    }

    @Override
    public RecyclerView getRecyclerView() {
        return commonRecycler;
    }

    @Override
    public void setNoData(boolean isNoData) {
        commonNodata.setVisibility(isNoData ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getLiveType() {
        return liveType;
    }

}