package cn.nodemedia.leadlive.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.view.SurfaceView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.nodemedia.leadlive.R;
import cn.nodemedia.library.view.BaseActivity;
import cn.nodemedia.library.view.widget.PullToRefreshView;

/**
 * 示例
 * Created by Bining on 16/7/5.
 */
public class SimpleActivity extends BaseActivity<SimpleContract.Presenter> implements SimpleContract.View {

    @InjectView(R.id.common_pulltorefresh)
    PullToRefreshView commonPulltorefresh;
    @InjectView(R.id.common_recycler)
    RecyclerView commonRecycler;

    @Override
    public int getLayoutId() {
        return R.layout.layout_recycler;
    }

    @Override
    public void initView() {
        ButterKnife.inject(this);
    }

    @Override
    public void initPresenter() {
        mPresenter.initPresenter(this);
    }

    @Override
    public Context getContext() {
        return mActivity;
    }

    @Override
    public PullToRefreshView getRefreshView() {
        return commonPulltorefresh;
    }

    @Override
    public RecyclerView getRecyclerView() {
        return commonRecycler;
    }

    @Override
    public void showProgress() {
    }

    @Override
    public void hideProgress() {
    }

    @Override
    public void exit() {
        Back();
    }

}
