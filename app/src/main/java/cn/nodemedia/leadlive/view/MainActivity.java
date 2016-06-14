package cn.nodemedia.leadlive.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.nodemedia.leadlive.R;
import cn.nodemedia.leadlive.view.fragment.HomeFragment;
import cn.nodemedia.leadlive.view.fragment.UserFragment;
import cn.nodemedia.library.bean.EventBusInfo;
import cn.nodemedia.library.utils.ToastUtils;

public class MainActivity extends AbsActionbarActivity implements OnClickListener {

    @InjectView(R.id.main_content)
    FrameLayout mainContent;
    @InjectView(R.id.main_tab_live_icon)
    ImageView mainTabLiveIcon;
    @InjectView(R.id.main_tab_me_icon)
    ImageView mainTabMeIcon;

    private FragmentManager fragmentManager;
    private List<Fragment> fragmentList;
    private int selectIndex = -1;
    private long clickTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        myApplication.exitOtherActivity(this);
        hasActionBar(View.GONE);
        initView();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("selectIndex", selectIndex);
        super.onSaveInstanceState(outState);
    }

    private void initView() {
        fragmentList = new ArrayList<>();
        fragmentList.add(HomeFragment.newInstance());
        fragmentList.add(UserFragment.newInstance());

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction
                .add(R.id.main_content, fragmentList.get(0), "home")
                .add(R.id.main_content, fragmentList.get(1), "user")
                .hide(fragmentList.get(0))
                .hide(fragmentList.get(1))
                .commit();

        changeFooterState(0);
    }

    @OnClick({R.id.main_tab_live, R.id.main_tab_room, R.id.main_tab_me})
    public void onClick(View view) {
        if (!isCanClick(view)) return;
        switch (view.getId()) {
            case R.id.main_tab_live:
                changeFooterState(0);
                break;
            case R.id.main_tab_room:
                StartActivity(LivePublisherActivity.class);
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

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (selectIndex >= 0) {
            fragmentTransaction.hide(fragmentList.get(selectIndex));
        }
        fragmentTransaction.show(fragmentList.get(position)).commitAllowingStateLoss();

        selectIndex = position;

        mainContent.setFocusable(true);
        mainContent.setFocusableInTouchMode(true);
        mainContent.requestFocus();
    }

    @Override
    public void Back() {
        long newClickTime = System.currentTimeMillis();
        if (newClickTime - clickTime < 1000) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            myApplication.exit();
        } else {
            clickTime = newClickTime;
            ToastUtils.show(mContext, getResources().getString(R.string.exit_hint));
        }
    }

    @Override
    public boolean hasSwipeFinish() {
        return false;
    }

    @Override
    public boolean hasEventBus() {
        return true;
    }

    @Override
    public void onSubEvent(EventBusInfo eventBusInfo) {
        super.onSubEvent(eventBusInfo);
    }

}
