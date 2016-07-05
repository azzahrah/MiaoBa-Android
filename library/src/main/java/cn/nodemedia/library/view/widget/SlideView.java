package cn.nodemedia.library.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.bumptech.glide.Glide;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cn.nodemedia.library.R;
import cn.nodemedia.library.utils.ScreenUtils;

/**
 * 幻灯片
 * Created by Bining.
 */
public class SlideView extends RelativeLayout implements Runnable, OnPageChangeListener, View.OnClickListener {

    /**
     * 引导小点是否显示
     */
    private boolean isShowDots = true;

    /**
     * 引导小点位置
     */
    private int dotsPosition = 0;

    /**
     * 引导小点图片
     */
    private int dotsImageResource = R.drawable.slide_dot;

    /**
     * 引导小点间距
     */
    private float dotsSpacing = ScreenUtils.dp2px(16);

    /**
     * 引导小点的背景颜色或背景图的透明度，取值为0-1,0代表透明
     */
    private float dotsAlpha = 0.8f;

    /**
     * ViewPager是否自动切换
     */
    private boolean isAutoShift = true;

    /**
     * ViewPager自动切换的时间间隔，单位为s，默认为5s
     */
    private int intervalTime = 5;

    /**
     * ViewPager宽高比,0表示均参照父类
     */
    private float aspectRatio = 0.618f;

    /**
     * 自动轮播的切换时间（控制速度）,默认为800ms
     */
    private int transformDuration = 800;

    private ViewPager viewPager;
    private LinearLayout dotsLayout;

    private List<View> contentsList;
    private List<ImageView> dotsList;

    private ViewPagerAdapter pagerAdapter;

    private boolean isStartPlay;
    private boolean isAutoPlay;
    private int countdown;

    private OnItemChildClickListener onItemChildClickListener;

    public SlideView(Context context) {
        this(context, null);
    }

    public SlideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public SlideView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlideView);
            isShowDots = a.getBoolean(R.styleable.SlideView_svIsShowDots, true);
            dotsPosition = a.getInteger(R.styleable.SlideView_svDotsPosition, 0);
            dotsImageResource = a.getResourceId(R.styleable.SlideView_svDotsImageResource, R.drawable.slide_dot);
            dotsSpacing = a.getDimension(R.styleable.SlideView_svDotsSpacing, ScreenUtils.dp2px(10));
            dotsAlpha = a.getFloat(R.styleable.SlideView_svDotsAlpha, 0.8f);
            isAutoShift = a.getBoolean(R.styleable.SlideView_svIsAutoShift, true);
            intervalTime = a.getInteger(R.styleable.SlideView_svIntervalTime, 5);
            aspectRatio = a.getFloat(R.styleable.SlideView_svAspectRatio, 0.618f);
            transformDuration = a.getInt(R.styleable.SlideView_transfromDuration, 800);
            a.recycle();
        }

        viewPager = new ViewPager(getContext());
        viewPager.addOnPageChangeListener(this);
        contentsList = new ArrayList<>();
        pagerAdapter = new ViewPagerAdapter(contentsList);
        viewPager.setAdapter(pagerAdapter);
        initViewPagerScroll();
        addView(viewPager, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        if (isShowDots) {
            dotsLayout = new LinearLayout(getContext());
            LayoutParams dotsLayoutLP = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            switch (dotsPosition) {
                case 0:
                    dotsLayoutLP.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    dotsLayoutLP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                    break;
                case 1:
                    dotsLayoutLP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                    dotsLayoutLP.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                    break;
                case 2:
                    dotsLayoutLP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                    dotsLayoutLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    break;
            }
            int padding = (int) dotsSpacing / 2;
            dotsLayout.setPadding(padding, padding, padding, padding);
            addView(dotsLayout, dotsLayoutLP);

            dotsList = new ArrayList<>();
        }
    }

    /**
     * 设置ViewPager的滑动速度
     */
    private void initViewPagerScroll() {
        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            ViewPagerScroller scroller = new ViewPagerScroller(viewPager.getContext());
            scroller.setScrollDuration(transformDuration);
            mScroller.set(viewPager, scroller);
            // viewPager.setPageTransformer(true, new DepthPageTransformer());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    public void setImageViews(List<Integer> resIds) {
        contentsList.clear();

        ImageView imageView1 = getImageView();
        imageView1.setImageResource(resIds.get(resIds.size() - 1));
        contentsList.add(imageView1);

        for (int i = 0; i < resIds.size(); i++) {
            ImageView imageView = getImageView();
            imageView.setImageResource(resIds.get(i));
            contentsList.add(imageView);
        }

        ImageView imageView2 = getImageView();
        imageView2.setImageResource(resIds.get(0));
        contentsList.add(imageView2);

        initSlideView();
    }

    public void setImageViewsToUrl(List<String> urls) {
        contentsList.clear();

        ImageView imageView1 = getImageView();
        Glide.with(getContext()).load(urls.get(urls.size() - 1)).into(imageView1);
        contentsList.add(imageView1);

        for (int i = 0; i < urls.size(); i++) {
            ImageView imageView = getImageView();
            Glide.with(getContext()).load(urls.get(i)).into(imageView);
            contentsList.add(imageView);
        }

        ImageView imageView2 = getImageView();
        Glide.with(getContext()).load(urls.get(0)).into(imageView2);
        contentsList.add(imageView2);

        initSlideView();
    }

    private ImageView getImageView() {
        ImageView imageView = new ImageView(getContext());
        imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setOnClickListener(SlideView.this);
        return imageView;
    }

    private void initSlideView() {
        if (isShowDots) {
            dotsList.clear();
            dotsLayout.removeAllViews();
            for (int i = 0; i < contentsList.size() - 2; i++) {
                ImageView imageView = new ImageView(getContext());
                int padding = (int) dotsSpacing / 2;
                imageView.setPadding(padding, padding, padding, padding);
                imageView.setAlpha(dotsAlpha);
                imageView.setImageResource(dotsImageResource);
                LinearLayout.LayoutParams imageViewLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                dotsLayout.addView(imageView, imageViewLP);
                dotsList.add(imageView);
            }
        }

        pagerAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(1, false);
        setSelectedDot(0);

        if (isAutoShift && !isStartPlay) {
            isStartPlay = true;
            isAutoPlay = true;
            postDelayed(this, 1000);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) (width * aspectRatio);
        setMeasuredDimension(width, height);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case 1:// 手势滑动，空闲中
                isAutoPlay = false;
                break;
            case 2:// 界面切换中
                isAutoPlay = false;
                break;
            case 0:// 滑动结束，即切换完毕或者加载完毕
                isAutoPlay = true;
                break;
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (position < 1) {
            position = dotsList.size();
            //viewPager.setCurrentItem(position, false);//false:不显示跳转过程的动画
        } else if (position > dotsList.size()) {
            position = 1;
            //viewPager.setCurrentItem(position, false);//false:不显示跳转过程的动画
        }
        setSelectedDot(position - 1);
    }

    private void setSelectedDot(int position) {
        for (int i = 0; i < dotsList.size(); i++) {
            dotsList.get(i).setSelected(i == position);
        }
        countdown = intervalTime;
    }

    @Override
    public void run() {
        if (isAutoPlay) {
            countdown--;
        }
        if (countdown < 1) {
            //TODO 切换pagerView
            viewPager.setCurrentItem((viewPager.getCurrentItem() + 1));
        }
        postDelayed(this, 1000);
    }

    @Override
    public void onClick(View view) {
        if (onItemChildClickListener != null) {
            onItemChildClickListener.onItemChildClick(this, viewPager.getCurrentItem() % dotsList.size());
        }
    }

    public void setOnItemChildClickListener(OnItemChildClickListener onItemChildClickListener) {
        this.onItemChildClickListener = onItemChildClickListener;
    }

    public interface OnItemChildClickListener {
        void onItemChildClick(View v, int position);
    }

    /**
     * Banner适配的adapter
     */
    class ViewPagerAdapter extends PagerAdapter {

        private List<View> views;

        public ViewPagerAdapter(List<View> views) {
            this.views = views;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //position %= (views.size() - 2);
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            int position = viewPager.getCurrentItem();
            if (viewPager.getCurrentItem() < 1) { //首位之前，跳转到末尾（N）
                viewPager.setCurrentItem(dotsList.size(), false);
            } else if (position > dotsList.size()) { //末位之后，跳转到首位（1）
                viewPager.setCurrentItem(1, false);
            }
        }
    }

    /**
     * 重写Scroller控制ViewPager的切换速度
     */
    public class ViewPagerScroller extends Scroller {
        private int mScrollDuration = 800;// 滑动速度,值越大滑动越慢，滑动太快会使效果不明显
        private boolean zero;

        public ViewPagerScroller(Context context) {
            super(context);
        }

        public ViewPagerScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public ViewPagerScroller(Context context, Interpolator interpolator, boolean flywheel) {
            super(context, interpolator, flywheel);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, zero ? 0 : mScrollDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, zero ? 0 : mScrollDuration);
        }

        public int getScrollDuration() {
            return mScrollDuration;
        }

        public void setScrollDuration(int scrollDuration) {
            this.mScrollDuration = scrollDuration;
        }

        public boolean isZero() {
            return zero;
        }

        public void setZero(boolean zero) {
            this.zero = zero;
        }
    }

    /**
     * ViewPager 切换动画
     */
    public class DepthPageTransformer implements ViewPager.PageTransformer {

        @SuppressLint("NewApi")
        @Override
        public void transformPage(View view, float position) {
            position %= dotsList.size();
            try {
                int pageWidth = view.getWidth();
                if (position < -1) {
                    // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    view.setAlpha(0);
                } else if (position <= 0) {
                    // [-1,0]
                    // Use the default slide transition when
                    // moving to the left page
                    view.setAlpha(1);
                    view.setTranslationX(0);
                    view.setScaleX(1);
                    view.setScaleY(1);
                } else if (position <= 1) {
                    // (0,1]
                    // Fade the page out.
                    view.setAlpha(1 - position);
                    // Counteract the default slide transition
                    view.setTranslationX(pageWidth * -position);
                    // Scale the page down (between MIN_SCALE and 1)
                    float MIN_SCALE = 0.75f;
                    float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
                    view.setScaleX(scaleFactor);
                    view.setScaleY(scaleFactor);
                } else {
                    // (1,+Infinity]
                    // This page is way off-screen to the right.
                    view.setAlpha(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
