package tv.miaoba.live.view.contract;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import tv.miaoba.live.Constants;
import tv.miaoba.live.R;
import tv.miaoba.live.bean.FollowInfo;
import tv.miaoba.live.utils.HttpCallback;
import tv.miaoba.live.utils.HttpUtils;
import xyz.tanwb.airship.glide.GlideManager;
import xyz.tanwb.airship.utils.SharedUtils;
import xyz.tanwb.airship.utils.ToastUtils;
import xyz.tanwb.airship.view.adapter.BaseRecyclerAdapter;
import xyz.tanwb.airship.view.adapter.ViewHolderHelper;
import xyz.tanwb.airship.view.adapter.listener.OnItemChildClickListener;
import xyz.tanwb.airship.view.adapter.listener.OnItemClickListener;
import xyz.tanwb.airship.view.adapter.listener.OnLoadMoreListener;
import xyz.tanwb.airship.view.widget.PullToRefreshView;

public interface UserFollowContract {

    interface View extends BaseRecyclerContract.View {

        int getFollowType();

    }

    class Presenter extends BaseRecyclerContract.Presenter<View> {

        private int followType;
        private int userid;
        private int page = 0;
        private int minid = 0;
        private UserAdapter userAdapter;
        private FollowInfo followInfo;

        @Override
        public void onStart() {

            followType = mView.getFollowType();

            userid = SharedUtils.getInt(Constants.USEROPENID, 0);

            pullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
                @Override
                public void onRefresh(PullToRefreshView pullToRefreshView) {
                    page = 1;
                    if (userAdapter.getDataCount() > 0) {
                        minid = userAdapter.getItem(0).id;
                    } else {
                        minid = 0;
                    }
                    getFollowList();
                }
            });

            userAdapter = new UserAdapter(mContext);
            userAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(android.view.View view, int i) {
                    //((AbsActivity) mActivity).StartActivity(LivePlayerActivity.class, liveNewAdapter.getItem(i));
                }
            });
            userAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
                @Override
                public void onItemChildClick(android.view.View view, int position) {
                    followInfo = userAdapter.getItem(position);
                    HttpUtils.postFollow(userid, followInfo.userid, new HttpCallback<Object>() {
                        @Override
                        public void onSuccess(Object abs) {
                            followInfo.is_follow = !followInfo.is_follow;
                            userAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(String strMsg) {
                            super.onFailure(strMsg);
                            if (followInfo.is_follow) {
                                ToastUtils.show(mActivity, "取消关注失败.");
                            } else {
                                ToastUtils.show(mActivity, "关注失败.");
                            }
                        }
                    });
                }
            });
            userAdapter.openLoadMore(R.layout.layout_loading, 20);
            userAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMoreClick() {
                    page++;
                    if (userAdapter.getDataCount() > 0) {
                        minid = userAdapter.getItem(userAdapter.getDataCount() - 1).id;
                    } else {
                        minid = 0;
                    }
                    getFollowList();
                }
            });

            commonRecycler.setLayoutManager(new LinearLayoutManager(mContext));
            commonRecycler.setAdapter(userAdapter);


            getFollowList();
        }

        private void getFollowList() {
            HttpUtils.getFollowList(userid, page, minid, followType == 1 ? "follow" : "fans", new HttpCallback<List<FollowInfo>>() {

                @Override
                public void onSuccess(List<FollowInfo> followInfoAbsL) {
                    if (page == 1) {
                        pullToRefreshView.refreshFinish(true);
                        userAdapter.clearDatas();
                    } else {
                        pullToRefreshView.loadmoreFinish(true);
                    }

                    if (followInfoAbsL != null) {
                        userAdapter.addDatas(followInfoAbsL);
                        pullToRefreshView.setLoadMoreEnable(followInfoAbsL.size() >= 20);
                    } else {
                        userAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(String strMsg) {
                    super.onFailure(strMsg);
                    ToastUtils.show(mContext, strMsg);
                    if (page == 1) {
                        pullToRefreshView.refreshFinish(true);
                    } else {
                        pullToRefreshView.loadmoreFinish(true);
                    }
                }
            });
        }

        public class UserAdapter extends BaseRecyclerAdapter<FollowInfo> {

            public UserAdapter(Context context) {
                super(context, R.layout.item_user);
            }

            @Override
            protected void setItemData(ViewHolderHelper viewHolderHelper, int position, FollowInfo model) {
                ImageView userFace = viewHolderHelper.getView(R.id.user_face);
                ImageView userAuth = viewHolderHelper.getView(R.id.user_auth);
                TextView userName = viewHolderHelper.getView(R.id.user_name);
                TextView userInfo = viewHolderHelper.getView(R.id.user_info);
                ImageView userSex = viewHolderHelper.getView(R.id.user_sex);
                ImageView userRank = viewHolderHelper.getView(R.id.user_rank);
                ImageView userFollow = viewHolderHelper.getView(R.id.user_follow);

                viewHolderHelper.setItemChildClickListener(R.id.user_follow);

                GlideManager.load(mContext, model.faces).placeholder(R.drawable.default_head).error(R.drawable.default_head).setTransform(GlideManager.IMAGE_TYPE_CIRCLE).into(userFace);
                userAuth.setImageResource(R.drawable.global_xing_1);
                userName.setText(model.nickname);
                userInfo.setText(TextUtils.isEmpty(model.autograph) ? "Ta很懒,什么介绍都没写..." : model.autograph);
                userSex.setImageResource(model.sex == 2 ? R.drawable.global_female : R.drawable.global_male);
                userRank.setImageResource(R.drawable.rank_1);
                userFollow.setImageResource(model.is_follow ? R.drawable.me_following : R.drawable.me_follow);
            }
        }
    }
}
