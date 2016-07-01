package cn.nodemedia.leadlive.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.nodemedia.leadlive.Constants;
import cn.nodemedia.leadlive.R;
import cn.nodemedia.leadlive.bean.FollowInfo;
import cn.nodemedia.leadlive.utils.HttpUtils;
import cn.nodemedia.library.view.adapter.BaseListAdapter;
import cn.nodemedia.library.view.adapter.listener.OnItemChildClickListener;
import cn.nodemedia.library.view.adapter.ViewHolderHelper;
import cn.nodemedia.library.bean.Abs;
import cn.nodemedia.library.bean.AbsL;
import cn.nodemedia.library.glide.GlideCircleTransform;
import cn.nodemedia.library.utils.SharedUtils;
import cn.nodemedia.library.utils.ToastUtils;
import cn.nodemedia.library.view.widget.pulltorefresh.PullToRefreshView;
import cn.nodemedia.library.view.widget.pulltorefresh.PullableListView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 用户关注
 * Created by Bining.
 */
public class UserFollowActivity extends ActionbarActivity {

    @InjectView(R.id.common_pulltorefresh)
    PullToRefreshView commonPulltorefresh;
    @InjectView(R.id.common_list)
    PullableListView commonList;

    private int userid;
    private int page = 0;
    private int minid = 0;
    private UserAdapter userAdapter;
    private FollowInfo followInfo;

    @Override
    public int getContentView() {
        return R.layout.layout_list_refresh;
    }

    @Override
    public void initView() {
        super.initView();
        ButterKnife.inject(this);
        setTitle("我的关注");
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
                getFollowList();
            }

            @Override
            public void onLoadMore(PullToRefreshView pullToRefreshLayout) {
                page++;
                if (userAdapter.getCount() > 0) {
                    minid = userAdapter.getItem(userAdapter.getCount() - 1).id;
                } else {
                    minid = 0;
                }
                getFollowList();
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
        getFollowList();
    }

    @Override
    public void initPresenter() {
    }

    private void getFollowList() {
        HttpUtils.getFollowList(userid, page, minid, "follow").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<AbsL<FollowInfo>>() {
            @Override
            public void call(AbsL<FollowInfo> followInfoAbsL) {
                if (page == 1) {
                    commonPulltorefresh.refreshFinish(true);
                    userAdapter.clearDatas();
                } else {
                    commonPulltorefresh.loadmoreFinish(true);
                }

                if (followInfoAbsL.isSuccess()) {
                    userAdapter.addDatas(followInfoAbsL.result);
                } else {
                    userAdapter.notifyDataSetChanged();
                    ToastUtils.show(mActivity, followInfoAbsL.getMsg());
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

            Glide.with(mContext).load(model.faces).asBitmap().error(R.drawable.default_head).transform(new GlideCircleTransform(mContext)).into(userFace);
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
            HttpUtils.postFollow(userid, followInfo.userid).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Abs>() {
                @Override
                public void call(Abs abs) {
                    if (abs.isSuccess()) {
                        followInfo.is_follow = !followInfo.is_follow;
                        notifyDataSetChanged();
                    } else {
                        if (followInfo.is_follow) {
                            ToastUtils.show(mActivity, "取消关注失败.");
                        } else {
                            ToastUtils.show(mActivity, "关注失败.");
                        }
                    }
                }
            });
        }
    }
}
