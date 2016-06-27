package cn.nodemedia.library.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
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
     * ViewPager自动切换的时间间隔，单位为ms，默认为5s
     */
    private int intervalTime = 5;

    /**
     * ViewPager宽高比,0表示均参照父类
     */
    private float aspectRatio = 0;

    /**
     * 自动轮播的切换时间（控制速度）,默认为800ms
     */
    private int transformDuration = 800;

    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private int imageNumber = 1;
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

    /**
     * 获取展示的图片数量
     *
     * @return 图片数量
     */
    public int getImageNumber() {
        return imageNumber;
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
            aspectRatio = a.getFloat(R.styleable.SlideView_svAspectRatio, 0.0f);
            transformDuration = a.getInt(R.styleable.SlideView_transfromDuration, 800);
            a.recycle();
        }

        viewPager = new ViewPager(getContext());
        viewPager.setId(R.id.slide_viewpager);
//        viewPager.setPageTransformer(true, new DepthPageTransformer());
        viewPager.addOnPageChangeListener(this);
        pagerAdapter = new ViewPagerAdapter(context);
        viewPager.setAdapter(pagerAdapter);
        LayoutParams viewPagerLP;

        initViewPagerScroll();
        if (aspectRatio == 0) {
//            viewPagerLP = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            viewPagerLP = new LayoutParams(LayoutParams.MATCH_PARENT, ScreenUtils.getScreenWidth());
        } else {
            viewPagerLP = new LayoutParams(LayoutParams.MATCH_PARENT, (int) (ScreenUtils.getScreenWidth() * aspectRatio));
        }
        addView(viewPager, viewPagerLP);

        if (isShowDots) {
            dotsLayout = new LinearLayout(getContext());
            LayoutParams dotsLayoutLP = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            switch (dotsPosition) {
                case 0:
                    dotsLayoutLP.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.slide_viewpager);
                    dotsLayoutLP.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    break;
                case 1:
                    dotsLayoutLP.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.slide_viewpager);
                    dotsLayoutLP.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                    break;
                case 2:
                    dotsLayoutLP.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.slide_viewpager);
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
            Field mScroller = null;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            ViewPagerScroller scroller = new ViewPagerScroller(
                    viewPager.getContext());
            scroller.setScrollDuration(transformDuration);
            mScroller.set(viewPager, scroller);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    public void setImageViews(List<Integer> resIds) {
        pagerAdapter.setImageViews(resIds);
        imageNumber = resIds.size();
        initSlideView();
    }

    public void setImageViewsToUrl(List<String> urls) {
        pagerAdapter.setImageUrls(urls);
        imageNumber = urls.size();
        initSlideView();
    }

    private void initSlideView() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                pagerAdapter.notifyDataSetChanged();
            }
        }, 1000);
        changeDotsView();
        viewPager.setCurrentItem(0);
        setSelectedDot(0);
        if (isAutoShift && !isStartPlay) {
            isStartPlay = true;
            isAutoPlay = true;
            postDelayed(this, 1000);
        }
    }

    private void changeDotsView() {
        if (isShowDots) {
            dotsList.clear();
            dotsLayout.removeAllViews();
            for (int i = 0; i < imageNumber; i++) {
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
    }

    @Override
    public void onClick(View view) {
        if (onItemChildClickListener != null) {
            onItemChildClickListener.onItemChildClick(this, viewPager.getCurrentItem() % imageNumber);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        setSelectedDot(position);
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
    public void run() {
        if (isAutoPlay) {
            countdown--;
        }
        if (countdown <= 0) {
            //TODO 切换pagerView
            viewPager.setCurrentItem((viewPager.getCurrentItem() + 1));
        }
        postDelayed(this, 1000);
    }

    private void setSelectedDot(int position) {
        if (dotsList.size() > 0) {
            position %= dotsList.size();
        }
        for (int i = 0; i < dotsList.size(); i++) {
            dotsList.get(i).setSelected(i == position);
        }
        countdown = intervalTime;
    }

    public void setOnItemChildClickListener(OnItemChildClickListener onItemChildClickListener) {
        this.onItemChildClickListener = onItemChildClickListener;
    }

    public interface OnItemChildClickListener {
        void onItemChildClick(View v, int position);
    }

    /**
     * ViewPager 切换动画
     */
    public class DepthPageTransformer implements ViewPager.PageTransformer {

        @SuppressLint("NewApi")
        @Override
        public void transformPage(View view, float position) {
            position %= imageNumber;
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

    /**
     * Banner适配的adapter
     */
    class ViewPagerAdapter extends PagerAdapter {

        private List<String> urls;

        /**
         * 预定长度，实现无限轮播，应大于图片数量
         */
        public static final int FAKE_BANNER_SIZE = 20;

        public Context context;

        private int size = 1;

        private List<Integer> imageIds;

        public ViewPagerAdapter(Context context) {
            this.context = context;
        }

        public void setImageUrls(List<String> urls) {
            if (this.urls != null) {
                this.urls.clear();
            }
            this.imageIds = null;
            this.urls = urls;
            this.size = urls.size();
        }

        public void setImageViews(List<Integer> resIds) {
            if (imageIds != null) {
                imageIds.clear();
            }
            this.urls = null;
            this.imageIds = resIds;
            this.size = imageIds.size();
        }

        @Override
        public int getCount() {
            return FAKE_BANNER_SIZE;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getItemPosition(Object object) {
            return super.POSITION_NONE;
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            arg1 %= size;
            ImageView imageView = new ImageView(context);
            //imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setOnClickListener(SlideView.this);
            if (urls != null) {
                Glide.with(context).load(urls.get(arg1)).into(imageView);
            } else if (imageIds != null) {
                imageView.setImageResource(imageIds.get(arg1));
            }
            return imageView;
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            int position = viewPager.getCurrentItem();
            if (position == 0) {
                position = size;
                viewPager.setCurrentItem(position, false);
            } else if (position == FAKE_BANNER_SIZE - 1) {
                position = size - 1;
                viewPager.setCurrentItem(position, false);
            }
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);
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

        public ViewPagerScroller(Context context, Interpolator interpolator,
                                 boolean flywheel) {
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
}
