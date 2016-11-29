package cn.nodemedia.leadlive.view;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.nodemedia.leadlive.R;
import cn.nodemedia.leadlive.view.contract.SimpleContract;
import xyz.tanwb.airship.utils.Log;
import xyz.tanwb.airship.utils.ScreenUtils;
import xyz.tanwb.airship.view.BaseActivity;
import xyz.tanwb.airship.view.widget.PullToRefreshView;

public class SimpleActivity extends BaseActivity<SimpleContract.Presenter> implements SimpleContract.View {

    @BindView(R.id.common_pulltorefresh)
    PullToRefreshView commonPulltorefresh;
    @BindView(R.id.common_recycler)
    RecyclerView commonRecycler;

    @Override
    public int getLayoutId() {
        return R.layout.layout_recycler_refresh;
    }

    @Override
    public void initView(Bundle bundle) {
        ButterKnife.bind(this);

        Configuration config = getResources().getConfiguration();

        Log.e("smallestScreenWidthDp : " + config.smallestScreenWidthDp);
        Log.e("screenWidthDp : " + config.screenWidthDp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Log.e("densityDpi : " + config.densityDpi);
        }
        Log.e("screen width" + ScreenUtils.getScreenWidth());
        Log.e("screen Height" + ScreenUtils.getScreenHeight());

        Log.e("screen TextSize" + getResources().getDimension(R.dimen.sp_10));
    }

    @Override
    public void initPresenter() {
        mPresenter.initPresenter(this);
    }

    @Override
    public PullToRefreshView getPullToRefreshView() {
        return commonPulltorefresh;
    }

    @Override
    public RecyclerView getRecyclerView() {
        return commonRecycler;
    }

    @Override
    public void setNoData(boolean isNoData) {
    }
}
