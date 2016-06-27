package cn.nodemedia.leadlive.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import butterknife.ButterKnife;
import cn.nodemedia.leadlive.R;
import cn.nodemedia.library.view.BaseActivity;

public abstract class AbsFragment extends Fragment {

    protected BaseActivity mActivity;

    private long oldClickTime = 0;
    private boolean isOnClick = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (BaseActivity) getActivity();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mActivity == null) {
            mActivity = (BaseActivity) getActivity();
        }
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 设置是否可使用点击事件
     *
     * @param isOnClick 是否可点击
     */
    public void setOnClick(boolean isOnClick) {
        this.isOnClick = isOnClick;
    }

    /**
     * 判断是否可以继续执行点击事件
     *
     * @param view 点击控件
     */
    public boolean isCanClick(View view) {
        long newClickTime = System.currentTimeMillis();
        if ((newClickTime - oldClickTime) < 300) {
            return false;
        }
        oldClickTime = newClickTime;

        return view.getId() == R.id.actionbar_back || isOnClick;
    }

    /**
     * 返回上一界面
     */
    public void Back() {
        mActivity.finish();
        mActivity.overridePendingTransition(R.anim.view_in_from_left, R.anim.view_out_to_right);
    }
}
