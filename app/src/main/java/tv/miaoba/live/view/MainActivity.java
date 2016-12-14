package tv.miaoba.live.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import tv.miaoba.imlib.LiveKit;
import tv.miaoba.live.R;
import tv.miaoba.live.view.contract.MainContract;
import tv.miaoba.live.view.fragment.HomeFragment;
import tv.miaoba.live.view.fragment.UserFragment;
import xyz.tanwb.airship.utils.ToastUtils;
import xyz.tanwb.airship.view.BaseActivity;
import xyz.tanwb.airship.view.adapter.BasePagerFragmentAdapter;
import xyz.tanwb.airship.view.widget.NoScrollViewPager;

public class MainActivity extends BaseActivity<MainContract.Presenter> implements MainContract.View, OnClickListener {

    @BindView(R.id.main_tab_live_icon)
    ImageView mainTabLiveIcon;
    @BindView(R.id.main_tab_me_icon)
    ImageView mainTabMeIcon;
    @BindView(R.id.main_content)
    NoScrollViewPager mainContent;

    private int selectIndex = -1;
    private long clickTime;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView(Bundle bundle) {
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(HomeFragment.newInstance());
        fragmentList.add(UserFragment.newInstance());

        mainContent.setAdapter(new BasePagerFragmentAdapter(getSupportFragmentManager(), fragmentList));

        changeFooterState(0);

        mApplication.exitOtherActivity(this);
    }

    @Override
    public void initPresenter() {
        mPresenter.initPresenter(this);
    }

    @OnClick({R.id.main_tab_live, R.id.main_tab_room, R.id.main_tab_me})
    public void onClick(View view) {
        if (!isCanClick(view)) return;
        switch (view.getId()) {
            case R.id.main_tab_live:
                changeFooterState(0);
                break;
            case R.id.main_tab_room:
                mPresenter.questPermissions();
                break;
            case R.id.main_tab_me:
                changeFooterState(1);
                break;
        }
    }

    private void changeFooterState(int position) {
        if (position == selectIndex) {
            return;
        }

        switch (position) {
            case 0:
                mainTabLiveIcon.setSelected(true);
                break;
            case 1:
                mainTabMeIcon.setSelected(true);
                break;
        }

        switch (selectIndex) {
            case 0:
                mainTabLiveIcon.setSelected(false);
                break;
            case 1:
                mainTabMeIcon.setSelected(false);
                break;
        }

        mainContent.setCurrentItem(position, false);

        selectIndex = position;

        mainContent.setFocusable(true);
        mainContent.setFocusableInTouchMode(true);
        mainContent.requestFocus();
    }

    @Override
    public void startLivePublisher() {
        advance(LivePublisherActivity.class);
    }

    @Override
    public void exit() {
        long newClickTime = System.currentTimeMillis();
        if (newClickTime - clickTime < 1000) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            mApplication.exit();
        } else {
            clickTime = newClickTime;
            ToastUtils.show(mActivity, getResources().getString(R.string.exit_hint));
        }
    }

    @Override
    public boolean hasLightMode() {
        return false;
    }

    @Override
    public boolean hasSwipeFinish() {
        return false;
    }

    @Override
    protected void onDestroy() {
        LiveKit.logout();
        super.onDestroy();
    }
}
