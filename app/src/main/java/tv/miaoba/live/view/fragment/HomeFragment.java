package tv.miaoba.live.view.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import tv.miaoba.live.R;
import xyz.tanwb.airship.view.BaseFragment;
import xyz.tanwb.airship.view.adapter.BasePagerFragmentAdapter;

/**
 * 首页
 * Create in Bining.
 */
public class HomeFragment extends BaseFragment {

    @BindView(R.id.main_chat)
    ImageView mainChat;
    @BindView(R.id.main_search)
    ImageView mainSearch;
    @BindView(R.id.main_me_diamonds)
    TextView mainMeDiamonds;
    @BindView(R.id.live_follow_text)
    TextView liveFollowText;
    @BindView(R.id.live_follow)
    RelativeLayout liveFollow;
    @BindView(R.id.live_hot_text)
    TextView liveHotText;
    @BindView(R.id.live_hot_icon)
    ImageView liveHotIcon;
    @BindView(R.id.live_hot)
    RelativeLayout liveHot;
    @BindView(R.id.live_new_text)
    TextView liveNewText;
    @BindView(R.id.live_new)
    RelativeLayout liveNew;
    @BindView(R.id.live_cursor)
    View liveCursor;
    @BindView(R.id.main_live_tab)
    LinearLayout mainLiveTab;
    @BindView(R.id.main_live_viewpager)
    ViewPager mainLiveViewpager;

    private int titleWidth = 0;
    private int cursorIndex = 0;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_live;
    }

    @Override
    public void initView(View view, Bundle bundle) {
        mainMeDiamonds.setVisibility(View.GONE);

        int cursorTextWidth = (int) liveHotText.getPaint().measureText("热门");
        titleWidth = cursorTextWidth + liveHotText.getPaddingLeft() + liveHotText.getPaddingLeft();
        ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) liveCursor.getLayoutParams();
        p.width = cursorTextWidth;
        p.setMargins((titleWidth - cursorTextWidth) / 2, 0, 0, 0);
        liveCursor.requestLayout();
    }

    @Override
    public void initPresenter() {
        List<Fragment> viewList = new ArrayList<>();
        viewList.add(LiveFragment.newInstance(2));
        viewList.add(LiveFragment.newInstance(3));
        viewList.add(LiveFragment.newInstance(1));

        mainLiveViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                changeView(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mainLiveViewpager.setAdapter(new BasePagerFragmentAdapter(getChildFragmentManager(), viewList));
        mainLiveViewpager.setOffscreenPageLimit(3);

        mainLiveViewpager.setCurrentItem(1);
        changeView(1);
    }

    private void changeView(int position) {
        if (cursorIndex == position) return;

        switch (position) {
            case 0:
                liveFollowText.setTextColor(ContextCompat.getColor(mActivity, R.color.colorThemeDark));
                break;
            case 1:
                liveHotText.setTextColor(ContextCompat.getColor(mActivity, R.color.colorThemeDark));
                break;
            case 2:
                liveNewText.setTextColor(ContextCompat.getColor(mActivity, R.color.colorThemeDark));
                break;
        }

        switch (cursorIndex) {
            case 0:
                liveFollowText.setTextColor(Color.WHITE);
                break;
            case 1:
                liveHotText.setTextColor(Color.WHITE);
                break;
            case 2:
                liveNewText.setTextColor(Color.WHITE);
                break;
        }

        Animation animation = new TranslateAnimation(titleWidth * cursorIndex, titleWidth * position, 0, 0);// 平移动画
        animation.setFillAfter(position != 1);// 动画终止时停留在最后一帧，不然会回到没有执行前的状态
        animation.setDuration(200);// 动画持续时间0.2秒
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                liveCursor.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (cursorIndex == 1) {
                    liveHotIcon.setVisibility(View.VISIBLE);
                    liveCursor.setVisibility(View.INVISIBLE);
                } else {
                    liveHotIcon.setVisibility(View.INVISIBLE);
                    liveCursor.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        cursorIndex = position;
        liveCursor.startAnimation(animation);// 是用ImageView来显示动画的
    }

    @OnClick({R.id.main_chat, R.id.main_search, R.id.live_follow, R.id.live_hot, R.id.live_new})
    public void onClick(View v) {
        if (!isCanClick(v)) return;
        switch (v.getId()) {
            case R.id.main_chat:
                break;
            case R.id.main_search:
                break;
            case R.id.live_follow:
                mainLiveViewpager.setCurrentItem(0, false);
                break;
            case R.id.live_hot:
                mainLiveViewpager.setCurrentItem(1, false);
                break;
            case R.id.live_new:
                mainLiveViewpager.setCurrentItem(2, false);
                break;
        }
    }

}