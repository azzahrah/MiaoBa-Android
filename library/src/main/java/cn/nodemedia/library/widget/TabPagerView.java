package cn.nodemedia.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.nodemedia.library.R;
import cn.nodemedia.library.utils.ScreenUtils;
import cn.nodemedia.library.adapter.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 带选项卡的ViewPager
 * Created by Bining.
 */
public class TabPagerView extends LinearLayout implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private Context mContext;
    private LinearLayout tabLayout;
    private View tabCursor;
    private ViewPager viewPager;

    private List<TextView> titleViews;

    private int tabBackground = Color.WHITE;
    private int tabTextNormalColor = Color.BLACK;
    private int tabTextSelectColor = Color.RED;
    private float tabTextSize;
    private int tabTextPadding = ScreenUtils.dp2px(10);
    private int tabChildWidth = 0;
    private float aspectRatio = 0.8f;
    private boolean hasDivider = false;

    /**
     * 是否有提示小圆点
     */
    private boolean hasPoint = false;
    /**
     * 小圆点样式资源
     */
    private int pointResourse = R.drawable.red_point;

    private List<ImageView> points = new ArrayList<>();

    private int cursorIndex = -1;// 选中的项

    private ViewPager.OnPageChangeListener onPageChangeListener;

    public TabPagerView(Context context) {
        super(context);
        initView(context, null);
    }

    public TabPagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public TabPagerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        this.mContext = context;
        setOrientation(LinearLayout.VERTICAL);
        titleViews = new ArrayList<>();

        if (attrs != null) {
            TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.TabPagerView);
            tabBackground = a.getColor(R.styleable.TabPagerView_tpvTabBackground, tabBackground);
            tabTextNormalColor = a.getColor(R.styleable.TabPagerView_tpvTabTextNormalColor, tabTextNormalColor);
            tabTextSelectColor = a.getColor(R.styleable.TabPagerView_tpvTabTextSelectColor, tabTextSelectColor);
            tabTextSize = ScreenUtils.px2sp(a.getDimension(R.styleable.TabPagerView_tpvTabTextSize, ScreenUtils.sp2px(14)));
            tabTextPadding = (int) a.getDimension(R.styleable.TabPagerView_tpvTabTextPadding, tabTextPadding);
            tabChildWidth = (int) a.getDimension(R.styleable.TabPagerView_tpvTabChildWidth, tabBackground);
            aspectRatio = a.getFloat(R.styleable.TabPagerView_tpvAspectRatio, 0.8f);
            hasDivider = a.getBoolean(R.styleable.TabPagerView_hasDivider, false);
            hasPoint = a.getBoolean(R.styleable.TabPagerView_hasPoint, false);
            pointResourse = a.getResourceId(R.styleable.TabPagerView_pointResourse, R.drawable.red_point);
            a.recycle();
        } else {
            tabTextSize = 14f;
        }

        tabLayout = new LinearLayout(mContext);
        tabLayout.setOrientation(LinearLayout.HORIZONTAL);
        tabLayout.setBackgroundColor(tabBackground);
        addView(tabLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        tabCursor = new View(mContext);
        tabCursor.setBackgroundColor(tabTextSelectColor);
        addView(tabCursor, LayoutParams.MATCH_PARENT, ScreenUtils.dp2px(2));

        viewPager = new ViewPager(mContext);
        viewPager.addOnPageChangeListener(this);
        addView(viewPager, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    /**
     * 显示提示小圆点，设置了hasPoint才有效
     */
    public void showPoint(int position) {
        if (hasPoint) {
            points.get(position).setVisibility(VISIBLE);
        }
    }

    /**
     * 隐藏提示小圆点，设置了hasPoint才有效
     */
    public void hidePoint(int position) {
        points.get(position).setVisibility(GONE);
    }

    public void addTabTitles(String... tabTexts) {
        if (tabChildWidth == 0) {
            tabChildWidth = ScreenUtils.getScreenWidth() / tabTexts.length;
        }

        for (int i = 0; i < tabTexts.length; i++) {
            LinearLayout tabItem = new LinearLayout(mContext);
            tabItem.setGravity(Gravity.CENTER);
            tabItem.setOrientation(LinearLayout.HORIZONTAL);
            tabItem.setTag(i);
            tabItem.setOnClickListener(this);

            TextView mTextView = new TextView(mContext);
            mTextView.setText(tabTexts[i]);
            mTextView.setTextColor(tabTextNormalColor);
            mTextView.setTextSize(tabTextSize);
            mTextView.setGravity(Gravity.CENTER);
            mTextView.setPadding(0, tabTextPadding, 0, tabTextPadding);
//            tabLayout.addView(mTextView, tabChildWidth, LayoutParams.WRAP_CONTENT);
            tabItem.addView(mTextView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            titleViews.add(mTextView);

            if (hasPoint) {
                ImageView point = new ImageView(mContext);
                point.setImageResource(pointResourse);
                tabItem.addView(point, ScreenUtils.dp2px(8), ScreenUtils.dp2px(8));
                MarginLayoutParams p2 = (MarginLayoutParams) point.getLayoutParams();
                p2.setMargins(0, 0, 0, ScreenUtils.dp2px(10));
                point.requestLayout();
                points.add(point);
                point.setVisibility(GONE);
            }

            if (hasDivider) {
                int devidedTabChildWidth;
                if (tabChildWidth == ScreenUtils.getScreenWidth() / tabTexts.length) {
                    devidedTabChildWidth = (ScreenUtils.getScreenWidth() - ScreenUtils.dp2px(1) * (tabTexts.length - 1)) / tabTexts.length;
                    tabLayout.addView(tabItem, devidedTabChildWidth, LayoutParams.WRAP_CONTENT);
                } else {
                    tabLayout.addView(tabItem, tabChildWidth, LayoutParams.WRAP_CONTENT);
                }
                if (i == tabTexts.length - 1) continue;
                View devideLine = new View(mContext);
                devideLine.setBackgroundColor(Color.LTGRAY);
                tabLayout.addView(devideLine, ScreenUtils.dp2px(1), LayoutParams.MATCH_PARENT);
                MarginLayoutParams p = (MarginLayoutParams) devideLine.getLayoutParams();
                p.setMargins(0, ScreenUtils.dp2px(12), 0, ScreenUtils.dp2px(12));
                devideLine.requestLayout();
            } else {
                tabLayout.addView(tabItem, tabChildWidth, LayoutParams.WRAP_CONTENT);
            }
        }

        initTabWidth(0);
        changeView(0);
    }

    public void addPagerViews(List<View> viewList) {
        viewPager.setAdapter(new ViewPagerAdapter(viewList));
        viewPager.setCurrentItem(0);
    }

    @Override
    public void onClick(View view) {
        int tag = (int) view.getTag();
        viewPager.setCurrentItem(tag, false);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        changeView(position);
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageScrollStateChanged(state);
        }
    }

    private void changeView(int position) {
        if (cursorIndex != position) {
            titleViews.get(position).setTextColor(tabTextSelectColor);

            int fromXDelta = tabChildWidth * cursorIndex;
            if (cursorIndex != -1) {
                titleViews.get(cursorIndex).setTextColor(tabTextNormalColor);

                int toXDelta = tabChildWidth * position;
                Animation animation = new TranslateAnimation(fromXDelta, toXDelta, 0, 0);// 平移动画
                animation.setFillAfter(true);// 动画终止时停留在最后一帧，不然会回到没有执行前的状态
                animation.setDuration(200);// 动画持续时间0.2秒
                tabCursor.startAnimation(animation);// 是用ImageView来显示动画的

                if (aspectRatio == 0) {
                    initTabWidth(position);
                }
            }
            cursorIndex = position;
        }
    }

    private void initTabWidth(int position) {
        int cursorTextWidth;
        if (aspectRatio == 0) {
            TextView textView = titleViews.get(position);
            String title = textView.getText().toString();
            Rect rect = new Rect();
            textView.getPaint().getTextBounds(title, 0, title.length(), rect);
            cursorTextWidth = rect.width();
        } else {
            cursorTextWidth = (int) (tabChildWidth * aspectRatio);
        }
        MarginLayoutParams p = (MarginLayoutParams) tabCursor.getLayoutParams();
        p.width = cursorTextWidth;
        p.setMargins((tabChildWidth - cursorTextWidth) / 2, 0, 0, 0);
        tabCursor.requestLayout();
    }

    public int getSelectTab() {
        return cursorIndex;
    }

    public TextView getTitleView(int position) {
        return titleViews.get(position);
    }

    public View getTabCursor() {
        return tabCursor;
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
    }

}
