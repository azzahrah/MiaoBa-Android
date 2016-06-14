package cn.nodemedia.leadlive.view.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.nodemedia.leadlive.R;
import cn.nodemedia.library.utils.ScreenUtils;

public abstract class AbsActionbarFragment extends AbsFragment {

    protected View mContainer;
    protected RelativeLayout mActionbarTitle;
    protected ImageView mActionBarTitleBack;
    protected TextView mActionBarTitleText;
    protected LinearLayout mActionBarTitleMenu;
    protected FrameLayout mActionBarContent;
    protected LinearLayout loadingLayout;
    protected ImageView loading;

    public abstract View onCreateView(Bundle savedInstanceState, ViewGroup container, LayoutInflater inflater);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mContainer = inflater.inflate(R.layout.actionbar, null);
        mActionbarTitle = (RelativeLayout) mContainer.findViewById(R.id.actionbar_layout);
        mActionBarTitleBack = (ImageView) mContainer.findViewById(R.id.actionbar_back);
        mActionBarTitleText = (TextView) mContainer.findViewById(R.id.actionbar_title);
        mActionBarTitleMenu = (LinearLayout) mContainer.findViewById(R.id.actionbar_menu);
        mActionBarContent = (FrameLayout) mContainer.findViewById(R.id.actionbar_content);
        loadingLayout = (LinearLayout) mContainer.findViewById(R.id.loading_layout);
        loading = (ImageView) mContainer.findViewById(R.id.loading);
        mActionBarContent.addView(onCreateView(savedInstanceState, container, inflater), new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        mActionBarTitleBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Back();
            }
        });
        loadingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        return mContainer;
    }

    /**
     * 批量设置控件的显示状态
     *
     * @param visible 显示状态
     * @param views   控件集
     */
    public void setVisibility(int visible, View... views) {
        for (View view : views) view.setVisibility(visible);
    }

    /**
     * 设置是否显示加载进度
     *
     * @param loadingView 加载控件
     * @param visible     显示状态
     */
    public void hasProgress(View loadingView, int visible) {
        if (loadingView == null) {
            return;
            //Glide.with(this).load(R.drawable.loading).into(loading);
//            AnimationDrawable animationDrawable = (AnimationDrawable) loading.getDrawable();
//            if (visible == View.VISIBLE) {
//                animationDrawable.start();
//            } else {
//                animationDrawable.stop();
//            }
//            loadingLayout.setVisibility(visible);
        } else {
            if (loadingView.getVisibility() != visible) {
                if (loadingView instanceof ImageView) {
                    //Glide.with(this).load(R.drawable.loading).into((ImageView) loadingView);
                    //AnimationDrawable animationDrawable = (AnimationDrawable) ((ImageView) loadingView).getDrawable();
                    //if (visible == View.VISIBLE) {
                    //animationDrawable.start();
                    //} else {
                    //animationDrawable.stop();
                    //}
                }
                loadingView.setVisibility(visible);
            }
        }
    }

    /**
     * 设置是否显示加载进度
     *
     * @param visible 显示状态
     */
    public void hasProgress(int visible) {
        hasProgress(null, visible);
    }

    /**
     * 设置是否显示ActionBar
     *
     * @param visible 显示参数
     */
    public void hasActionBar(int visible) {
        mActionbarTitle.setVisibility(visible);
    }

    /**
     * 设置title背景图片
     *
     * @param resId 图片ID
     */
    public void setBackgroundByRes(int resId) {
        mActionbarTitle.setBackgroundResource(resId);
    }

    /**
     * 设置title背景颜色
     *
     * @param color 颜色值
     */
    public void setBackgroundByColor(String color) {
        mActionbarTitle.setBackgroundColor(Color.parseColor(color));
    }

    /**
     * 设置是否显示返回键
     *
     * @param visible 显示参数
     */
    public void hasBack(int visible) {
        mActionBarTitleBack.setVisibility(visible == View.GONE ? View.INVISIBLE : visible);
    }

    /**
     * 设置返回键图片
     *
     * @param resId 图片ID
     */
    public void setBackRes(int resId) {
        mActionBarTitleBack.setImageResource(resId);
    }

    /**
     * 设置是否显示标题
     *
     * @param visible 显示参数
     */
    public void hasTitle(int visible) {
        mActionBarTitleText.setVisibility(visible);
    }

    /**
     * 设置标题
     *
     * @param resId 标题StringID
     */
    public void setTitle(int resId) {
        setTitle(getResources().getString(resId));
    }

    /**
     * 设置标题
     *
     * @param title 标题
     */
    public void setTitle(String title) {
        mActionBarTitleText.setText(title);
    }

    /**
     * 设置标题颜色
     *
     * @param color 颜色值
     */
    public void setTitleColor(String color) {
        mActionBarTitleText.setTextColor(Color.parseColor(color));
    }

    /**
     * 设置是否显示菜单
     *
     * @param visible 显示参数
     */
    public void hasMenu(int visible) {
        mActionBarTitleMenu.setVisibility(visible);
    }

    /**
     * 添加Title功能
     *
     * @param view 菜单项视图
     */
    public void addMenuItme(View view, int width, int height) {
        hasMenu(View.VISIBLE);
        mActionBarTitleMenu.addView(view, width, height);
    }

    /**
     * 添加Title功能
     *
     * @param view 菜单项视图
     */
    public void addMenuItme(View view) {
        addMenuItme(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    /**
     * 添加Title功能
     *
     * @param resId 图片ID
     */
    public ImageView addMenuImageItme(int resId, View.OnClickListener mOnClickListener) {
        ImageView mImageItme = new ImageView(mActivity);
        mImageItme.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mImageItme.setImageResource(resId);
        mImageItme.setOnClickListener(mOnClickListener);
        addMenuItme(mImageItme);
        return mImageItme;
    }

    /**
     * 添加Title功能
     *
     * @param textId 文字ID
     */
    public TextView addMenuTextItme(int textId, View.OnClickListener mOnClickListener) {
        TextView mTextView = new TextView(mActivity);
        mTextView.setTextAppearance(mActivity, R.style.common_textview_black);
        mTextView.setText(textId);
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setOnClickListener(mOnClickListener);
        int padding = ScreenUtils.dp2px(12);
        mTextView.setPadding(padding, padding, padding, padding);
        addMenuItme(mTextView);
        return mTextView;
    }
}
