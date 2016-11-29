package cn.nodemedia.leadlive.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.nodemedia.leadlive.Constants;
import cn.nodemedia.leadlive.R;
import cn.nodemedia.leadlive.bean.UserInfo;
import cn.nodemedia.leadlive.view.LivePlayerActivity;
import cn.nodemedia.leadlive.view.UserFaceActivity;
import cn.nodemedia.leadlive.view.UserFansActivity;
import cn.nodemedia.leadlive.view.UserFollowActivity;
import cn.nodemedia.leadlive.view.UserInfoActivity;
import cn.nodemedia.leadlive.view.contract.LiveContract;
import rx.functions.Action1;
import xyz.tanwb.airship.db.DbException;
import xyz.tanwb.airship.db.DbManager;
import xyz.tanwb.airship.glide.GlideManager;
import xyz.tanwb.airship.rxjava.RxBusManage;
import xyz.tanwb.airship.utils.SharedUtils;
import xyz.tanwb.airship.view.BaseFragment;
import xyz.tanwb.airship.view.widget.PullToRefreshView;
import xyz.tanwb.airship.view.widget.SlideView;

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