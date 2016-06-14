package cn.nodemedia.leadlive.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.lzy.okhttputils.callback.StringCallback;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.nodemedia.leadlive.Constants;
import cn.nodemedia.leadlive.R;
import cn.nodemedia.leadlive.bean.FollowInfo;
import cn.nodemedia.leadlive.utils.HttpUtils;
import cn.nodemedia.library.adapter.AdapterViewAdapter;
import cn.nodemedia.library.adapter.OnItemChildClickListener;
import cn.nodemedia.library.adapter.ViewHolderHelper;
import cn.nodemedia.library.bean.Abs;
import cn.nodemedia.library.glide.GlideCircleTransform;
import cn.nodemedia.library.utils.SharedUtils;
import cn.nodemedia.library.utils.ToastUtils;
import cn.nodemedia.library.widget.pulltorefresh.PullToRefreshView;
import cn.nodemedia.library.widget.pulltorefresh.PullableListView;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 用户关注
 * Created by Bining.
 */
public class UserFollowActivity extends AbsActionbarActivity {

    @InjectView(R.id.common_pulltorefresh)
    PullToRefreshView commonPulltorefresh;
    @InjectView(R.id.common_list)
    PullableListView commonList;

    private int userid;
    private int page = 0;
    private int minid = 0;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_refresh);
        ButterKnife.inject(this);
        setTitle("我的关注");
        initView();
    }

    private void initView() {
        userid = SharedUtils.getInt(Constants.USEROPENID, 0);

        commonPulltorefresh.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshView pullToRefreshLayout) {
                page = 1;
                if (userAdapter.getCount() > 0) {
                    minid = userAdapter.getItem(0).userid;
                } else {
                    minid = 0;
                }
                getFollowList();
            }

            @Override
            public void onLoadMore(PullToRefreshView pullToRefreshLayout) {
                page++;
                if (userAdapter.getCount() > 0) {
                    minid = userAdapter.getItem(userAdapter.getCount() - 1).userid;
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

    private void getFollowList() {
        HttpUtils.getFollowList(userid, page, minid, "follow", new StringCallback() {
            @Override
            public void onResponse(boolean isFromCache, String s, Request request, @Nullable Response response) {
                if (page == 1) {
                    commonPulltorefresh.refreshFinish(true);
                    userAdapter.clearDatas();
                } else {
                    commonPulltorefresh.loadmoreFinish(true);
                }

                Abs abs = JSON.parseObject(s, Abs.class);
                if (abs.isSuccess()) {
                    List<FollowInfo> liveInfoList = JSON.parseArray(abs.result, FollowInfo.class);
                    userAdapter.addDatas(liveInfoList);
                } else {
                    userAdapter.notifyDataSetChanged();
                    ToastUtils.show(mActivity, abs.getMsg());
                }
            }
        });
    }

    public class UserAdapter extends AdapterViewAdapter<FollowInfo> implements OnItemChildClickListener {

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
            //ImageView userFollow = viewHolderHelper.getView(R.id.user_follow);

            viewHolderHelper.setOnItemChildClickListener(this);
            viewHolderHelper.setItemChildClickListener(R.id.user_follow);

            Glide.with(mContext).load(model.faces).asBitmap().error(R.drawable.default_head).transform(new GlideCircleTransform(mContext)).into(userFace);

            userAuth.setImageResource(R.drawable.global_xing_1);

            userName.setText(model.nickname);
            userInfo.setText("Ta很懒,什么介绍都没写...");
            userSex.setImageResource(model.sex == 2 ? R.drawable.global_female : R.drawable.global_male);
            userRank.setImageResource(R.drawable.rank_1);

        }

        @Override
        public void onItemChildClick(View v, int position) {
        }
    }
}
