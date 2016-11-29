package cn.nodemedia.leadlive.view;

import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.nodemedia.leadlive.R;
import xyz.tanwb.airship.view.BaseActivity;
import xyz.tanwb.airship.view.BasePresenter;

public abstract class ActionbarActivity<T extends BasePresenter> extends BaseActivity<T> {

    // protected RelativeLayout actionbarLayout;
    // protected ImageView actionbarBack;
    // protected TextView actionbarTitle;
    // protected LinearLayout actionbarMenu;
    // protected FrameLayout actionbarContent;

    @BindView(R.id.actionbar_layout)
    RelativeLayout actionbarLayout;
    @BindView(R.id.actionbar_back)
    ImageView actionbarBack;
    @BindView(R.id.actionbar_title)
    TextView actionbarTitle;
    @BindView(R.id.actionbar_menu)
    LinearLayout actionbarMenu;

    @Override
    public void setContentView(int layoutResID) {
        View rootView = getLayoutInflater().inflate(R.layout.actionbar, null);
        FrameLayout content = (FrameLayout) rootView.findViewById(R.id.actionbar_content);
        View contentView = getLayoutInflater().inflate(layoutResID, null);
        content.addView(contentView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        super.setContentView(rootView);
    }

    @Override
    public void initView(Bundle bundle) {
        //activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN); 全屏
    }

    @OnClick({R.id.actionbar_back})
    public void onBarClick(View view) {
        switch (view.getId()) {
            case R.id.actionbar_back:
                onBackClick();
                break;
        }
    }

    @Override
    public boolean hasLightMode() {
        return true;
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
     * @param visible 显示状态
     */
    public void hasProgress(ImageView loading, int visible) {
        if (loading.getVisibility() == visible) {
            return;
        }
        AnimationDrawable animationDrawable = (AnimationDrawable) loading.getDrawable();
        if (visible == View.VISIBLE) {
            animationDrawable.start();
        } else {
            animationDrawable.stop();
        }
        loading.setVisibility(visible);
    }

    /**
     * 设置是否显示加载进度
     *
     * @param visible 显示状态
     */
    public void hasProgress(int visible) {
        //hasProgress(loading, visible);
    }

    /**
     * 设置title背景图片
     *
     * @param resId 图片ID
     */
    public void setBackgroundByRes(int resId) {
        actionbarLayout.setBackgroundResource(resId);
    }

    /**
     * 设置title背景颜色
     *
     * @param color 颜色值
     */
    public void setBackgroundByColor(String color) {
        actionbarLayout.setBackgroundColor(Color.parseColor(color));
    }

    /**
     * 设置是否显示返回键
     *
     * @param visible 显示参数
     */
    public void hasBack(int visible) {
        actionbarBack.setVisibility(visible == View.GONE ? View.INVISIBLE : visible);
    }

    /**
     * 设置返回键图片
     *
     * @param resId 图片ID
     */
    public void setBackRes(int resId) {
        actionbarBack.setImageResource(resId);
    }

    /**
     * 返回键被点击
     */
    public void onBackClick() {
        exit();
    }

    /**
     * 设置是否显示标题
     *
     * @param visible 显示参数
     */
    public void hasTitle(int visible) {
        actionbarTitle.setVisibility(visible);
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
        actionbarTitle.setText(title);
    }

    /**
     * 设置标题颜色
     *
     * @param color 颜色值
     */
    public void setTitleColor(String color) {
        actionbarTitle.setTextColor(Color.parseColor(color));
    }

    /**
     * 设置标题颜色
     *
     * @param colorRes 颜色值
     */
    public void setTitleColor(int colorRes) {
        actionbarTitle.setTextColor(colorRes);
    }

    /**
     * 设置是否显示菜单
     *
     * @param visible 显示参数
     */
    public void hasMenu(int visible) {
        actionbarMenu.setVisibility(visible);
    }

    /**
     * 添加Title功能
     *
     * @param view 菜单项视图
     */
    public void addMenuItme(View view, LinearLayout.LayoutParams layoutParams) {
        hasMenu(View.VISIBLE);
        actionbarMenu.addView(view, layoutParams);
    }

    /**
     * 添加Title功能
     *
     * @param resId 图片ID
     */
    public ImageView addMenuImageItme(int resId, View.OnClickListener mOnClickListener) {
        ImageView mMenuItme = new ImageView(this);
        mMenuItme.setScaleType(ImageView.ScaleType.CENTER);
        mMenuItme.setImageResource(resId);
        mMenuItme.setOnClickListener(mOnClickListener);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.dp_48), LinearLayout.LayoutParams.MATCH_PARENT);
        // params.leftMargin = (int) getResources().getDimension(R.dimen.dp_4);
        addMenuItme(mMenuItme, params);
        return mMenuItme;
    }

    /**
     * 添加Title功能
     *
     * @param textId 文字ID
     */
    public TextView addMenuTextItme(int textId, View.OnClickListener mOnClickListener) {
        TextView mTextView = new TextView(this);
        mTextView.setTextAppearance(mContext, R.style.ToolBarStyle);
        mTextView.setMaxWidth((int) getResources().getDimension(R.dimen.dp_72));
        int padding = (int) getResources().getDimension(R.dimen.dp_6);
        mTextView.setPadding(padding, 0, padding, 0);
        mTextView.setGravity(Gravity.CENTER_VERTICAL);
        mTextView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        mTextView.setTextColor(ContextCompat.getColor(mContext, R.color.textColorPrimary));
        mTextView.setText(textId);
        mTextView.setOnClickListener(mOnClickListener);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        addMenuItme(mTextView, params);
        return mTextView;
    }

    /**
     * 添加Title功能
     *
     * @param textId       文字ID
     * @param textColorRes 文字颜色资源id
     */
    public TextView addMenuTextItme(int textId, int textColorRes, View.OnClickListener mOnClickListener) {
        TextView mTextView = new TextView(this);
        mTextView.setTextAppearance(mContext, R.style.ToolBarStyle);
        mTextView.setMaxWidth((int) getResources().getDimension(R.dimen.dp_72));
        int padding = (int) getResources().getDimension(R.dimen.dp_6);
        mTextView.setPadding(padding, 0, padding, 0);
        mTextView.setGravity(Gravity.CENTER_VERTICAL);
        mTextView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        mTextView.setTextColor(ContextCompat.getColor(mContext, textColorRes));
        mTextView.setText(textId);
        mTextView.setOnClickListener(mOnClickListener);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        addMenuItme(mTextView, params);
        return mTextView;
    }

}
