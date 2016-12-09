package tv.miaoba.live.view;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import tv.miaoba.live.R;
import tv.miaoba.live.view.contract.UserFollowContract;
import xyz.tanwb.airship.view.widget.PullToRefreshView;

/**
 * 用户关注
 * Created by Bining.
 */
public class UserFollowActivity extends ActionbarActivity<UserFollowContract.Presenter> implements UserFollowContract.View {

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

    private int followType;

    @Override
    public int getLayoutId() {
        return R.layout.layout_recycler_refresh;
    }

    @Override
    public void initView(Bundle bundle) {
        super.initView(bundle);
        if (bundle != null) {
            followType = bundle.getInt("p0", 0);
        } else {
            followType = getIntent().getIntExtra("p0", 0);
        }
        if (followType == 1) {
            setTitle("我的关注");
        } else {
            setTitle("我的粉丝");
        }
    }

    @Override
    public void initPresenter() {
        mPresenter.initPresenter(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("p0", followType);
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
    public int getFollowType() {
        return followType;
    }
}
