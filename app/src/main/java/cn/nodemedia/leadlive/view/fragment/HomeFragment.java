package cn.nodemedia.leadlive.view.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.nodemedia.leadlive.R;
import cn.nodemedia.leadlive.bean.LiveInfo;
import cn.nodemedia.leadlive.utils.HttpUtils;
import cn.nodemedia.leadlive.view.LivePlayerActivity;
import cn.nodemedia.library.adapter.AdapterViewAdapter;
import cn.nodemedia.library.adapter.OnItemChildClickListener;
import cn.nodemedia.library.adapter.ViewHolderHelper;
import cn.nodemedia.library.adapter.ViewPagerAdapter;
import cn.nodemedia.library.bean.AbsL;
import cn.nodemedia.library.glide.GlideCircleTransform;
import cn.nodemedia.library.utils.ScreenUtils;
import cn.nodemedia.library.utils.ToastUtils;
import cn.nodemedia.library.widget.SlideView;
import cn.nodemedia.library.widget.pulltorefresh.PullToRefreshView;
import cn.nodemedia.library.widget.pulltorefresh.PullableListView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 首页
 * Create in Bining.
 */
public class HomeFragment extends AbsActionbarFragment {

    @InjectView(R.id.live_follow_text)
    TextView liveFollowText;
    @InjectView(R.id.live_hot_text)
    TextView liveHotText;
    @InjectView(R.id.live_hot_icon)
    ImageView liveHotIcon;
    @InjectView(R.id.live_new_text)
    TextView liveNewText;
    @InjectView(R.id.live_cursor)
    View liveCursor;
    @InjectView(R.id.main_live_tab)
    LinearLayout mainLiveTab;
    @InjectView(R.id.main_me_diamonds)
    TextView mainMeDiamonds;
    @InjectView(R.id.main_live_viewpager)
    ViewPager mainLiveViewpager;

    private int titleWidth = 0;
    private int cursorIndex = 0;

    private PullToRefreshView liveFollowPulltorefresh;
    private PullableListView liveFollowList;
    private LiveAdapter liveFollowAdapter;
    private int liveFollowPage = 1;

    private PullToRefreshView liveHotPulltorefresh;
    private SlideView liveHotBanner;
    private ListView liveHotList;
    private LiveAdapter liveHotAdapter;
    private int liveHotPage = 1;

    private PullToRefreshView liveNewPulltorefresh;
    private PullableListView liveNewList;
    private LiveAdapter liveNewAdapter;
    private int liveNewPage = 1;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(Bundle savedInstanceState, ViewGroup container, LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_live, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        hasActionBar(View.GONE);
        initView();
    }

    private void initView() {
        mainLiveTab.setVisibility(View.VISIBLE);
        mainMeDiamonds.setVisibility(View.GONE);

        int cursorTextWidth = (int) liveHotText.getPaint().measureText("热门");
        titleWidth = cursorTextWidth + liveHotText.getPaddingLeft() + liveHotText.getPaddingLeft();
        ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) liveCursor.getLayoutParams();
        p.width = cursorTextWidth;
        p.setMargins((titleWidth - cursorTextWidth) / 2, 0, 0, 0);
        liveCursor.requestLayout();

        List<View> viewList = new ArrayList<>();
        viewList.add(initLiveFollowView());
        viewList.add(initLiveHotView());
        viewList.add(initLiveNewView());

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
        mainLiveViewpager.setAdapter(new ViewPagerAdapter(viewList));

        changeView(1);
        mainLiveViewpager.setCurrentItem(1);
    }

    public View initLiveFollowView() {
        View view = mActivity.getLayoutInflater().inflate(R.layout.layout_main_live_follow, null);

        liveFollowPulltorefresh = (PullToRefreshView) view.findViewById(R.id.live_follow_pulltorefresh);
        liveFollowPulltorefresh.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshView pullToRefreshLayout) {
                liveFollowPage = 1;
                getLiveFollowList();
            }

            @Override
            public void onLoadMore(PullToRefreshView pullToRefreshLayout) {
                liveFollowPage++;
                getLiveFollowList();
            }
        });

        liveFollowList = (PullableListView) view.findViewById(R.id.live_follow_list);
        liveFollowList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mActivity.StartActivity(LivePlayerActivity.class, liveFollowAdapter.getItem(i));
            }
        });
        liveFollowAdapter = new LiveAdapter(mActivity, 0);
        liveFollowList.setAdapter(liveFollowAdapter);

        getLiveFollowList();

        return view;
    }

    private void getLiveFollowList() {
        HttpUtils.getLiveList(2, liveFollowPage).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<AbsL<LiveInfo>>() {
            @Override
            public void call(AbsL<LiveInfo> liveInfoAbsL) {
                if (liveFollowPage == 1) {
                    liveFollowPulltorefresh.refreshFinish(true);
                    liveFollowAdapter.clearDatas();
                } else {
                    liveFollowPulltorefresh.loadmoreFinish(true);
                }

                if (liveInfoAbsL.isSuccess()) {
                    liveFollowAdapter.addDatas(liveInfoAbsL.result);
                    liveFollowPulltorefresh.setPullLoadEnable(liveInfoAbsL.result != null && liveInfoAbsL.result.size() == 20);
                } else {
                    liveFollowAdapter.notifyDataSetChanged();
                    ToastUtils.show(mActivity, liveInfoAbsL.getMsg());
                }
            }
        });
    }

    public View initLiveHotView() {
        View view = mActivity.getLayoutInflater().inflate(R.layout.layout_main_live_hot, null);

        liveHotPulltorefresh = (PullToRefreshView) view.findViewById(R.id.live_hot_pulltorefresh);
        liveHotPulltorefresh.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshView pullToRefreshLayout) {
                liveHotPage = 1;
                getLiveHotList();
            }

            @Override
            public void onLoadMore(PullToRefreshView pullToRefreshLayout) {
                liveHotPage++;
                getLiveHotList();
            }
        });

        liveHotBanner = (SlideView) view.findViewById(R.id.live_hot_banner);
        List<Integer> banners = new ArrayList<>();
        banners.add(R.drawable.hall_no_live_bg);
        banners.add(R.drawable.hall_no_live_bg);
        banners.add(R.drawable.hall_no_live_bg);
        liveHotBanner.setImageViews(banners);

        liveHotList = (ListView) view.findViewById(R.id.live_hot_list);
        liveHotList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mActivity.StartActivity(LivePlayerActivity.class, liveHotAdapter.getItem(i));
            }
        });
        liveHotAdapter = new LiveAdapter(mActivity, 0);
        liveHotList.setAdapter(liveHotAdapter);

        getLiveHotList();

        return view;
    }

    private void getLiveHotList() {
        HttpUtils.getLiveList(3, liveHotPage).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<AbsL<LiveInfo>>() {
            @Override
            public void call(AbsL<LiveInfo> liveInfoAbsL) {
                if (liveHotPage == 1) {
                    liveHotPulltorefresh.refreshFinish(true);
                    liveHotAdapter.clearDatas();
                } else {
                    liveHotPulltorefresh.loadmoreFinish(true);
                }

                if (liveInfoAbsL.isSuccess()) {
                    liveHotAdapter.addDatas(liveInfoAbsL.result);
                    liveHotPulltorefresh.setPullLoadEnable(liveInfoAbsL.result != null && liveInfoAbsL.result.size() == 20);
                } else {
                    liveHotAdapter.notifyDataSetChanged();
                    ToastUtils.show(mActivity, liveInfoAbsL.getMsg());
                }
                liveHotAdapter.changeListHeight(liveHotList);
            }
        });
    }

    public View initLiveNewView() {
        View view = mActivity.getLayoutInflater().inflate(R.layout.layout_main_live_new, null);

        liveNewPulltorefresh = (PullToRefreshView) view.findViewById(R.id.live_new_pulltorefresh);
        liveNewPulltorefresh.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshView pullToRefreshLayout) {
                liveNewPage = 1;
                getLiveNewList();
            }

            @Override
            public void onLoadMore(PullToRefreshView pullToRefreshLayout) {
                liveNewPage++;
                getLiveNewList();
            }
        });

        liveNewList = (PullableListView) view.findViewById(R.id.live_new_list);
        liveNewList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mActivity.StartActivity(LivePlayerActivity.class, liveNewAdapter.getItem(i));
            }
        });
        liveNewAdapter = new LiveAdapter(mActivity, 0);
        liveNewList.setAdapter(liveNewAdapter);

        getLiveNewList();

        return view;
    }

    private void getLiveNewList() {
        HttpUtils.getLiveList(1, liveNewPage).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<AbsL<LiveInfo>>() {
            @Override
            public void call(AbsL<LiveInfo> liveInfoAbsL) {
                if (liveNewPage == 1) {
                    liveNewPulltorefresh.refreshFinish(true);
                    liveNewAdapter.clearDatas();
                } else {
                    liveNewPulltorefresh.loadmoreFinish(true);
                }

                if (liveInfoAbsL.isSuccess()) {
                    liveNewAdapter.addDatas(liveInfoAbsL.result);
                    liveNewPulltorefresh.setPullLoadEnable(liveInfoAbsL.result != null && liveInfoAbsL.result.size() == 20);
                } else {
                    liveNewAdapter.notifyDataSetChanged();
                    ToastUtils.show(mActivity, liveInfoAbsL.getMsg());
                }
            }
        });
    }

    private void changeView(int position) {
        if (cursorIndex == position) return;

        switch (position) {
            case 0:
                liveFollowText.setTextColor(ContextCompat.getColor(mActivity, R.color.common_theme_dark));
                break;
            case 1:
                liveHotText.setTextColor(ContextCompat.getColor(mActivity, R.color.common_theme_dark));
                break;
            case 2:
                liveNewText.setTextColor(ContextCompat.getColor(mActivity, R.color.common_theme_dark));
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    public class LiveAdapter extends AdapterViewAdapter<LiveInfo> implements OnItemChildClickListener {

        private int liveType;

        public LiveAdapter(Context context, int liveType) {
            super(context);
            this.liveType = liveType;
            switch (liveType) {
                case 0:
                    setmItemLayoutId(R.layout.item_live);
                    break;
            }
        }

        @Override
        protected void setItemData(ViewHolderHelper viewHolderHelper, int position, LiveInfo model) {
            ImageView userFace = viewHolderHelper.getView(R.id.user_face);
            ImageView userAuth = viewHolderHelper.getView(R.id.user_auth);
            TextView userName = viewHolderHelper.getView(R.id.user_name);
            TextView userLocation = viewHolderHelper.getView(R.id.user_location);
            TextView userWatch = viewHolderHelper.getView(R.id.user_watch);
            ImageView userLive = viewHolderHelper.getView(R.id.user_cover);
            ImageView userLiveState = viewHolderHelper.getView(R.id.user_live_state);
            TextView userLiveTitle = viewHolderHelper.getView(R.id.user_live_title);

            //viewHolderHelper.setOnItemChildClickListener(this);
            //viewHolderHelper.setItemChildClickListener(R.id.user_live);
            Glide.with(mContext).load(model.faces).asBitmap().error(R.drawable.default_head).transform(new GlideCircleTransform(mContext)).into(userFace);

            userAuth.setImageResource(R.drawable.global_xing_1);

            userName.setText(model.nickname);
            userLocation.setText(model.location);
            userLiveTitle.setText(model.title);

            String watch = model.online + " 在看";
            SpannableStringBuilder builder = new SpannableStringBuilder(watch);
            builder.setSpan(new AbsoluteSizeSpan(ScreenUtils.sp2px(18)), 0, watch.length() - 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.common_theme_light)), 0, watch.length() - 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            userWatch.setText(builder);

            int width = ScreenUtils.getScreenWidth();
            userLive.setLayoutParams(new RelativeLayout.LayoutParams(width, width));
            Glide.with(mContext).load(model.images).asBitmap().error(R.drawable.mr_720).into(userLive);

            userLiveState.setVisibility(View.VISIBLE);
        }

        @Override
        public void onItemChildClick(View v, int position) {
        }
    }
}