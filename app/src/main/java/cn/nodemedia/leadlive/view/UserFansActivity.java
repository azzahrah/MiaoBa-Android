package cn.nodemedia.leadlive.view;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.nodemedia.leadlive.Constants;
import cn.nodemedia.leadlive.R;
import cn.nodemedia.leadlive.bean.FollowInfo;
import cn.nodemedia.leadlive.utils.HttpCallback;
import cn.nodemedia.leadlive.utils.HttpUtils;
import xyz.tanwb.airship.glide.GlideManager;
import xyz.tanwb.airship.utils.SharedUtils;
import xyz.tanwb.airship.utils.ToastUtils;
import xyz.tanwb.airship.view.adapter.BaseListAdapter;
import xyz.tanwb.airship.view.adapter.ViewHolderHelper;
import xyz.tanwb.airship.view.adapter.listener.OnItemChildClickListener;
import xyz.tanwb.airship.view.widget.AutoListView;
import xyz.tanwb.airship.view.widget.PullToRefreshView;

/**
 * 用户粉丝
 * Created by Bining.
 */
public class UserFansActivity extends ActionbarActivity {

    @BindView(R.id.common_pulltorefresh)
    PullToRefreshView commonPulltorefresh;
    @BindView(R.id.common_list)
    AutoListView commonList;

    private int userid;
    private int page = 0;
    private int minid = 0;
    private UserAdapter userAdapter;
    private FollowInfo followInfo;

    @Override
    public int getLayoutId() {
        return R.layout.layout_list_refresh;
    }

    @Override
    public void initView(Bundle bundle) {
        super.initView(bundle);
        ButterKnife.bind(this);
        setTitle("我的粉丝");
        userid = SharedUtils.getInt(Constants.USEROPENID, 0);

        commonPulltorefresh.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshView pullToRefreshLayout) {
                page = 1;
                if (userAdapter.getCount() > 0) {
                    minid = userAdapter.getItem(0).id;
                } else {
                    minid = 0;
                }
                getFansList();
            }
        });

        commonPulltorefresh.setOnLoadMoreListener(new PullToRefreshView.OnLoadMoreListener() {
            @Override
            public void onLoadMore(PullToRefreshView pullToRefreshLayout) {
                page++;
                if (userAdapter.getCount() > 0) {
                    minid = userAdapter.getItem(userAdapter.getCount() - 1).id;
                } else {
                    minid = 0;
                }
                getFansList();
            }
        });

        commonList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //((AbsActivity) mActivity).StartActivity(LivePlayerActivity.class, liveNewAdapter.getItem(i));
            }
        });
        userAdapter = new UserAdapter(mActivity);
        commonList.setAdapter(userAdapter);
        getFansList();
    }

    @Override
    public void initPresenter() {
    }

    private void getFansList() {
        HttpUtils.getFollowList(userid, page, minid, "fans", new HttpCallback<List<FollowInfo>>() {
            @Override
            public void onSuccess(List<FollowInfo> followInfoAbsL) {
                if (page == 1) {
                    commonPulltorefresh.refreshFinish(true);
                    userAdapter.clearDatas();
                } else {
                    commonPulltorefresh.loadmoreFinish(true);
                }

                if (followInfoAbsL != null) {
                    userAdapter.addDatas(followInfoAbsL);
                    commonPulltorefresh.setLoadMoreEnable(followInfoAbsL.size() >= 20);
                } else {
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(String strMsg) {
                super.onFailure(strMsg);
                ToastUtils.show(mContext, strMsg);
                if (page == 1) {
                    commonPulltorefresh.refreshFinish(true);
                } else {
                    commonPulltorefresh.loadmoreFinish(true);
                }
            }
        });
    }

    public class UserAdapter extends BaseListAdapter<FollowInfo> implements OnItemChildClickListener {

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

            viewHolderHelper.setOnItemChildClickListener(this);
            viewHolderHelper.setItemChildClickListener(R.id.user_follow);

            GlideManager.load(mContext, model.faces).placeholder(R.drawable.default_head).error(R.drawable.default_head).setTransform(GlideManager.IMAGE_TYPE_CIRCLE).into(userFace);
            userAuth.setImageResource(R.drawable.global_xing_1);
            userName.setText(model.nickname);
            userInfo.setText(TextUtils.isEmpty(model.autograph) ? "Ta很懒,什么介绍都没写..." : model.autograph);
            userSex.setImageResource(model.sex == 2 ? R.drawable.global_female : R.drawable.global_male);
            userRank.setImageResource(R.drawable.rank_1);
            userFollow.setImageResource(model.is_follow ? R.drawable.me_following : R.drawable.me_follow);
        }

        @Override
        public void onItemChildClick(View v, int position) {
            followInfo = getItem(position);
            HttpUtils.postFollow(userid, followInfo.userid, new HttpCallback<Object>() {
                @Override
                public void onSuccess(Object abs) {
                    followInfo.is_follow = !followInfo.is_follow;
                    notifyDataSetChanged();
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
    }
}
