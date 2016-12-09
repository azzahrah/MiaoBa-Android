package tv.miaoba.live.view.contract;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import tv.miaoba.live.R;
import tv.miaoba.live.bean.LiveInfo;
import tv.miaoba.live.utils.HttpCallback;
import tv.miaoba.live.utils.HttpUtils;
import tv.miaoba.live.view.LivePlayerActivity;
import xyz.tanwb.airship.glide.GlideManager;
import xyz.tanwb.airship.utils.ScreenUtils;
import xyz.tanwb.airship.utils.ToastUtils;
import xyz.tanwb.airship.view.adapter.BaseRecyclerAdapter;
import xyz.tanwb.airship.view.adapter.ViewHolderHelper;
import xyz.tanwb.airship.view.adapter.listener.OnItemClickListener;
import xyz.tanwb.airship.view.adapter.listener.OnLoadMoreListener;
import xyz.tanwb.airship.view.widget.PullToRefreshView;
import xyz.tanwb.airship.view.widget.SlideView;

public interface LiveContract {

    interface View extends BaseRecyclerContract.View {
        int getLiveType();
    }

    class Presenter extends BaseRecyclerContract.Presenter<View> {

        private int liveType;

        private LiveAdapter liveAdapter;

        private int page = 1;

        @Override
        public void onStart() {
            super.onStart();

            liveType = mView.getLiveType();

            pullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
                @Override
                public void onRefresh(PullToRefreshView pullToRefreshView) {
                    page = 1;
                    getLiveList();
                }
            });

            liveAdapter = new LiveAdapter(mContext);
            liveAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(android.view.View view, int i) {
                    mView.advance(LivePlayerActivity.class, liveAdapter.getItem(i));
                }
            });
            liveAdapter.openLoadMore(R.layout.layout_loading, 20);
            liveAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMoreClick() {
                    page++;
                    getLiveList();
                }
            });

            if (liveType == 3) {
                SlideView banner = (SlideView) LayoutInflater.from(mContext).inflate(R.layout.layout_banner, null);
                List<Integer> banners = new ArrayList<>();
                banners.add(R.drawable.hall_no_live_bg);
                banners.add(R.drawable.hall_no_live_bg);
                banners.add(R.drawable.hall_no_live_bg);
                banner.setImageViews(banners);
                liveAdapter.setHeaderView(banner);
            }

            commonRecycler.setLayoutManager(new LinearLayoutManager(mContext));
            commonRecycler.setAdapter(liveAdapter);

            getLiveList();
        }

        private void getLiveList() {
            HttpUtils.getLiveList(liveType, page, new HttpCallback<List<LiveInfo>>() {
                @Override
                public void onSuccess(List<LiveInfo> liveInfoAbsL) {
                    if (page == 1) {
                        pullToRefreshView.refreshFinish(true);
                        liveAdapter.clearDatas();
                    }
                    if (liveInfoAbsL != null) {
                        liveAdapter.notifyDataChangedAfterLoadMore(liveInfoAbsL);
                    } else {
                        liveAdapter.notifyDataSetChanged();
                    }
                    liveAdapter.setLoadMoreEnable(liveInfoAbsL != null && liveInfoAbsL.size() == 20);
                    isNoData();
                }

                @Override
                public void onFailure(String strMsg) {
                    super.onFailure(strMsg);
                    ToastUtils.show(mActivity, strMsg);
                    if (page == 1) {
                        pullToRefreshView.refreshFinish(true);
                    } else {
                        liveAdapter.setLoadMoreEnable(false);
                        liveAdapter.notifyDataSetChanged();
                    }
                    isNoData();
                }
            });
        }

        public void isNoData() {
            mView.setNoData(liveAdapter.getDataCount() == 0);
        }

        public class LiveAdapter extends BaseRecyclerAdapter<LiveInfo> {

            public LiveAdapter(Context context) {
                super(context, R.layout.item_live);
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
                GlideManager.load(mContext, model.faces).placeholder(R.drawable.default_head).error(R.drawable.default_head).setTransform(GlideManager.IMAGE_TYPE_CIRCLE).into(userFace);

                userAuth.setImageResource(R.drawable.global_xing_1);

                userName.setText(model.nickname);
                userLocation.setText(model.location);
                userLiveTitle.setText(model.title);

                String watch = model.online + " 在看";
                SpannableStringBuilder builder = new SpannableStringBuilder(watch);
                builder.setSpan(new AbsoluteSizeSpan(ScreenUtils.sp2px(18)), 0, watch.length() - 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.colorTheme)), 0, watch.length() - 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                userWatch.setText(builder);

                int width = ScreenUtils.getScreenWidth();
                userLive.setLayoutParams(new RelativeLayout.LayoutParams(width, width));
                Glide.with(mContext).load(model.images).asBitmap().error(R.drawable.mr_720).into(userLive);

                userLiveState.setVisibility(android.view.View.VISIBLE);
            }
        }

    }
}
