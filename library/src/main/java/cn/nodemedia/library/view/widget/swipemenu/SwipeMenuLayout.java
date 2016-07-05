package cn.nodemedia.library.view.widget.swipemenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IntDef;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.OverScroller;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import cn.nodemedia.library.R;

public abstract class SwipeMenuLayout extends FrameLayout {

    public static final int HORIZONTAL = 0x00000001;
    public static final int VERTICAL = 0x00000002;

    public static final int MENU_LOCATION = 2;
    public static final float OPEN_PERCENT = 0.4f;
    public static final int SCROLLER_DURATION = 250;

    protected int mOrientation = HORIZONTAL;
    private int menuLocation = MENU_LOCATION;
    protected float mAutoOpenPercent = OPEN_PERCENT;
    protected int mScrollerDuration = SCROLLER_DURATION;
    protected Interpolator mInterpolator = new LinearInterpolator();

    protected int mScaledTouchSlop;
    protected int mLastX;
    protected int mLastY;
    protected int mDownX;
    protected int mDownY;

    protected View mContentView;
    protected Swiper mBeginSwiper;
    protected Swiper mEndSwiper;
    protected Swiper mCurrentSwiper;

    protected boolean shouldResetSwiper;
    protected boolean mDragging;
    protected boolean swipeEnable = true;

    protected OverScroller mScroller;
    protected VelocityTracker mVelocityTracker;
    protected int mScaledMinimumFlingVelocity;
    protected int mScaledMaximumFlingVelocity;

    protected OnSwipeSwitchListener mOnSwipeSwitchListener;
    protected OnSwipeFractionListener mOnSwipeFractionListener;

    protected NumberFormat mDecimalFormat = new DecimalFormat("#.00");

    protected int mPreScroll;
    protected float mPreBeginMenuFraction = -1;
    protected float mPreEndMenuFraction = -1;

    public SwipeMenuLayout(Context context) {
        this(context, null);
    }

    public SwipeMenuLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeMenuLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!isInEditMode()) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwipeMenu, 0, defStyle);
            mOrientation = a.getInteger(R.styleable.SwipeMenu_orientation, HORIZONTAL);
            menuLocation = a.getInteger(R.styleable.SwipeMenu_menuLocation, MENU_LOCATION);
            mAutoOpenPercent = a.getFloat(R.styleable.SwipeMenu_openPercent, OPEN_PERCENT);
            mScrollerDuration = a.getInteger(R.styleable.SwipeMenu_scrollerDuration, SCROLLER_DURATION);
            int interpolatorId = a.getResourceId(R.styleable.SwipeMenu_scrollerInterpolator, 0);
            if (interpolatorId > 0)
                mInterpolator = AnimationUtils.loadInterpolator(getContext(), interpolatorId);
            a.recycle();
        }
        init();
    }

    public void init() {
        ViewConfiguration mViewConfig = ViewConfiguration.get(getContext());
        mScaledMinimumFlingVelocity = mViewConfig.getScaledMinimumFlingVelocity();
        mScaledMaximumFlingVelocity = mViewConfig.getScaledMaximumFlingVelocity();
        mScaledTouchSlop = mViewConfig.getScaledTouchSlop();
        mScroller = new OverScroller(getContext(), mInterpolator);
    }

    public void smoothOpenBeginMenu() {
        if (mBeginSwiper == null) throw new IllegalArgumentException("Not have begin menu!");
        mCurrentSwiper = mBeginSwiper;
        smoothOpenMenu();
    }

    public void smoothOpenEndMenu() {
        if (mEndSwiper == null) throw new IllegalArgumentException("Not have end menu!");
        mCurrentSwiper = mEndSwiper;
        smoothOpenMenu();
    }

    public void smoothCloseBeginMenu() {
        if (mBeginSwiper == null) throw new IllegalArgumentException("Not have begin menu!");
        mCurrentSwiper = mBeginSwiper;
        smoothCloseMenu();
    }

    public void smoothCloseEndMenu() {
        if (mEndSwiper == null) throw new IllegalArgumentException("Not have end menu!");
        mCurrentSwiper = mEndSwiper;
        smoothCloseMenu();
    }

    public boolean isMenuOpen() {
        int getScroll = mOrientation == HORIZONTAL ? getScrollX() : getScrollY();
        return (mBeginSwiper != null && mBeginSwiper.isMenuOpen(getScroll)) || (mEndSwiper != null && mEndSwiper.isMenuOpen(getScroll));
    }

    public boolean isMenuOpenNotEqual() {
        int getScroll = mOrientation == HORIZONTAL ? getScrollX() : getScrollY();
        return (mBeginSwiper != null && mBeginSwiper.isMenuOpenNotEqual(getScroll)) || (mEndSwiper != null && mEndSwiper.isMenuOpenNotEqual(getScroll));
    }

    public void smoothOpenMenu(int duration) {
        mCurrentSwiper.autoOpenMenu(mScroller, mOrientation == HORIZONTAL ? getScrollX() : getScrollY(), duration);
        invalidate();
    }

    public void smoothCloseMenu(int duration) {
        mCurrentSwiper.autoCloseMenu(mScroller, mOrientation == HORIZONTAL ? getScrollX() : getScrollY(), duration);
        invalidate();
    }

    public void smoothOpenMenu() {
        smoothOpenMenu(mScrollerDuration);
    }

    public void smoothCloseMenu() {
        smoothCloseMenu(mScrollerDuration);
    }

    public void setSwipeEnable(boolean swipeEnable) {
        this.swipeEnable = swipeEnable;
    }

    public boolean isSwipeEnable() {
        return swipeEnable;
    }

    public void setSwipeListener(OnSwipeSwitchListener onSwipeSwitchListener) {
        mOnSwipeSwitchListener = onSwipeSwitchListener;
    }

    public void setSwipeFractionListener(OnSwipeFractionListener onSwipeFractionListener) {
        mOnSwipeFractionListener = onSwipeFractionListener;
    }

    public int getLen() {
        return mOrientation == HORIZONTAL ? mCurrentSwiper.getMenuWidth() : mCurrentSwiper.getMenuHeight();
    }

    public int getMoveLen(MotionEvent ev) {
        if (mOrientation == HORIZONTAL) {
            int sx = getScrollX();
            return (int) (ev.getX() - sx);
        } else {
            int sy = getScrollY();
            return (int) (ev.getY() - sy);
        }
    }

    /**
     * 计算完成时间
     *
     * @param ev       up event
     * @param velocity velocity
     * @return finish duration
     */
    public int getSwipeDuration(MotionEvent ev, int velocity) {
        int moveLen = getMoveLen(ev);
        final int len = getLen();
        final int halfLen = len / 2;
        final float distanceRatio = Math.min(1f, 1.0f * Math.abs(moveLen) / len);
        final float distance = halfLen + halfLen * distanceInfluenceForSnapDuration(distanceRatio);
        int duration;
        if (velocity > 0) {
            duration = 4 * Math.round(1000 * Math.abs(distance / velocity));
        } else {
            final float pageDelta = (float) Math.abs(moveLen) / len;
            duration = (int) ((pageDelta + 1) * 100);
        }
        duration = Math.min(duration, mScrollerDuration);
        return duration;
    }

    private float distanceInfluenceForSnapDuration(float f) {
        f -= 0.5f; // center the values about 0.
        f *= 0.3f * Math.PI / 2.0f;
        return (float) Math.sin(f);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean isIntercepted = super.onInterceptTouchEvent(ev);
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownX = mLastX = (int) ev.getX();
                mDownY = mLastY = (int) ev.getY();
                isIntercepted = false;
                break;
            case MotionEvent.ACTION_MOVE:
                int disX = (int) (ev.getX() - mDownX);
                int disY = (int) (ev.getY() - mDownY);
                if (mOrientation == HORIZONTAL) {
                    isIntercepted = Math.abs(disX) > mScaledTouchSlop && Math.abs(disX) > Math.abs(disY);
                } else {
                    isIntercepted = Math.abs(disY) > mScaledTouchSlop && Math.abs(disY) > Math.abs(disX);
                }
                break;
            case MotionEvent.ACTION_UP:
                isIntercepted = false;
                // menu view opened and click on content view,
                // we just close the menu view and intercept the up event
                if (isMenuOpen() && mCurrentSwiper.isClickOnContentView(this, ev.getY())) {
                    smoothCloseMenu();
                    isIntercepted = true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                isIntercepted = false;
                if (!mScroller.isFinished())
                    mScroller.abortAnimation();
                break;
        }
        return isIntercepted;
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mVelocityTracker == null) mVelocityTracker = VelocityTracker.obtain();
        mVelocityTracker.addMovement(ev);
        int dx;
        int dy;
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastX = (int) ev.getX();
                mLastY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isSwipeEnable()) break;
                int disX = (int) (mLastX - ev.getX());
                int disY = (int) (mLastY - ev.getY());
                if (!mDragging) {
                    if (mOrientation == HORIZONTAL) {
                        mDragging = (Math.abs(disX) > mScaledTouchSlop && Math.abs(disX) > Math.abs(disY));

                    } else {
                        mDragging = (Math.abs(disY) > mScaledTouchSlop && Math.abs(disY) > Math.abs(disX));
                    }
                }

                if (mDragging) {
                    if (mCurrentSwiper == null || shouldResetSwiper) {
                        int dis = mOrientation == HORIZONTAL ? disX : disY;
                        if (dis < 0) {
                            if (mBeginSwiper != null)
                                mCurrentSwiper = mBeginSwiper;
                            else
                                mCurrentSwiper = mEndSwiper;
                        } else {
                            if (mEndSwiper != null)
                                mCurrentSwiper = mEndSwiper;
                            else
                                mCurrentSwiper = mBeginSwiper;
                        }
                    }
                    if (mOrientation == HORIZONTAL) {
                        scrollBy(disX, 0);
                    } else {
                        scrollBy(0, disY);
                    }

                    mLastX = (int) ev.getX();
                    mLastY = (int) ev.getY();
                    shouldResetSwiper = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                dx = (int) (mDownX - ev.getX());
                dy = (int) (mDownY - ev.getY());
                mDragging = false;
                mVelocityTracker.computeCurrentVelocity(1000, mScaledMaximumFlingVelocity);

                int velocityXY = mOrientation == HORIZONTAL ? (int) mVelocityTracker.getXVelocity() : (int) mVelocityTracker.getYVelocity();
                int velocity = Math.abs(velocityXY);
                if (velocity > mScaledMinimumFlingVelocity) {
                    if (mCurrentSwiper != null) {
                        int duration = getSwipeDuration(ev, velocity);
                        if (mCurrentSwiper.getSwiperType() == Swiper.HORIZONTAL_RIGHT || mCurrentSwiper.getSwiperType() == Swiper.VERTICAL_BOTTOM) {
                            if (velocityXY < 0) { // just open
                                smoothOpenMenu(duration);
                            } else { // just close
                                smoothCloseMenu(duration);
                            }
                        } else {
                            if (velocityXY > 0) { // just open
                                smoothOpenMenu(duration);
                            } else { // just close
                                smoothCloseMenu(duration);
                            }
                        }
                        ViewCompat.postInvalidateOnAnimation(this);
                    }
                } else {
                    judgeOpenClose(dx, dy);
                }
                mVelocityTracker.clear();
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                if (mOrientation == HORIZONTAL) {
                    if (Math.abs(mDownX - ev.getX()) > mScaledTouchSlop || Math.abs(mDownY - ev.getY()) > mScaledTouchSlop || isMenuOpen()) { // ignore click listener
                        return true;
                    }
                } else {
                    if (Math.abs(mDownY - ev.getY()) > mScaledTouchSlop || Math.abs(mDownX - ev.getX()) > mScaledTouchSlop || isMenuOpen()) { // ignore click listener
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                mDragging = false;
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                } else {
                    dx = (int) (mDownX - ev.getX());
                    dy = (int) (mDownY - ev.getY());
                    judgeOpenClose(dx, dy);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }


    private void judgeOpenClose(int dx, int dy) {
        if (mCurrentSwiper != null) {
            boolean state = mOrientation == HORIZONTAL ? (Math.abs(getScrollX()) >= (mCurrentSwiper.getMenuView().getWidth() * mAutoOpenPercent)) : (Math.abs(getScrollY()) >= (mCurrentSwiper.getMenuView().getHeight() * mAutoOpenPercent));
            if (state) { // auto open
                if (Math.abs(dx) > mScaledTouchSlop || Math.abs(dy) > mScaledTouchSlop) { // swipe up
                    if (isMenuOpenNotEqual()) smoothCloseMenu();
                    else smoothOpenMenu();
                } else { // normal up
                    if (isMenuOpen()) smoothCloseMenu();
                    else smoothOpenMenu();
                }
            } else { // auto close
                smoothCloseMenu();
            }
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        if (mOrientation == HORIZONTAL) {
            Swiper.Checker checker = mCurrentSwiper.checkXY(x, y);
            shouldResetSwiper = checker.shouldResetSwiper;
            if (checker.x != getScrollX()) {
                super.scrollTo(checker.x, checker.y);
            }
            if (getScrollX() != mPreScroll) {
                int absScrollX = Math.abs(getScrollX());
                if (mCurrentSwiper.getSwiperType() == Swiper.HORIZONTAL_RIGHT) {
                    if (mOnSwipeSwitchListener != null) {
                        if (absScrollX == 0) mOnSwipeSwitchListener.endMenuClosed(this);
                        else if (absScrollX == mEndSwiper.getMenuWidth())
                            mOnSwipeSwitchListener.endMenuOpened(this);
                    }
                    if (mOnSwipeFractionListener != null) {
                        float fraction = (float) absScrollX / mEndSwiper.getMenuWidth();
                        fraction = Float.parseFloat(mDecimalFormat.format(fraction));
                        if (fraction != mPreEndMenuFraction) {
                            mOnSwipeFractionListener.endMenuSwipeFraction(this, fraction);
                        }
                        mPreEndMenuFraction = fraction;
                    }
                } else {
                    if (mOnSwipeSwitchListener != null) {
                        if (absScrollX == 0) mOnSwipeSwitchListener.beginMenuClosed(this);
                        else if (absScrollX == mBeginSwiper.getMenuWidth())
                            mOnSwipeSwitchListener.beginMenuOpened(this);
                    }
                    if (mOnSwipeFractionListener != null) {
                        float fraction = (float) absScrollX / mBeginSwiper.getMenuWidth();
                        fraction = Float.parseFloat(mDecimalFormat.format(fraction));
                        if (fraction != mPreBeginMenuFraction) {
                            mOnSwipeFractionListener.beginMenuSwipeFraction(this, fraction);
                        }
                        mPreBeginMenuFraction = fraction;
                    }
                }
            }
            mPreScroll = getScrollX();
        } else {
            Swiper.Checker checker = mCurrentSwiper.checkXY(x, y);
            shouldResetSwiper = checker.shouldResetSwiper;
            if (checker.y != getScrollY()) {
                super.scrollTo(checker.x, checker.y);
            }
            if (getScrollY() != mPreScroll) {
                int absScrollY = Math.abs(getScrollY());
                if (mCurrentSwiper.getSwiperType() == Swiper.VERTICAL_BOTTOM) {
                    if (mOnSwipeSwitchListener != null) {
                        if (absScrollY == 0) mOnSwipeSwitchListener.endMenuClosed(this);
                        else if (absScrollY == mEndSwiper.getMenuHeight())
                            mOnSwipeSwitchListener.endMenuOpened(this);
                    }
                    if (mOnSwipeFractionListener != null) {
                        float fraction = (float) absScrollY / mEndSwiper.getMenuHeight();
                        fraction = Float.parseFloat(mDecimalFormat.format(fraction));
                        if (fraction != mPreEndMenuFraction) {
                            mOnSwipeFractionListener.endMenuSwipeFraction(this, fraction);
                        }
                        mPreEndMenuFraction = fraction;
                    }
                } else {
                    if (mOnSwipeSwitchListener != null) {
                        if (absScrollY == 0) mOnSwipeSwitchListener.beginMenuClosed(this);
                        else if (absScrollY == mBeginSwiper.getMenuHeight())
                            mOnSwipeSwitchListener.beginMenuOpened(this);
                    }
                    if (mOnSwipeFractionListener != null) {
                        float fraction = (float) absScrollY / mBeginSwiper.getMenuHeight();
                        fraction = Float.parseFloat(mDecimalFormat.format(fraction));
                        if (fraction != mPreBeginMenuFraction) {
                            mOnSwipeFractionListener.beginMenuSwipeFraction(this, fraction);
                        }
                        mPreBeginMenuFraction = fraction;
                    }
                }
            }
            mPreScroll = getScrollY();
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mOrientation == HORIZONTAL) {
                int currX = Math.abs(mScroller.getCurrX());
                if (mCurrentSwiper.getSwiperType() == Swiper.HORIZONTAL_RIGHT) {
                    scrollTo(currX, 0);
                    invalidate();
                } else {
                    scrollTo(-currX, 0);
                    invalidate();
                }
            } else {
                int currY = Math.abs(mScroller.getCurrY());
                if (mCurrentSwiper.getSwiperType() == Swiper.VERTICAL_BOTTOM) {
                    scrollTo(0, currY);
                    invalidate();
                } else {
                    scrollTo(0, -currY);
                    invalidate();
                }
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setClickable(true);
        View menuViewBegin = null, menuViewEnd = null;
        switch (getChildCount()) {
            case 0:
                throw new IllegalArgumentException("Not find contentView by id smContentView");
            case 1:
                throw new IllegalArgumentException("Not find menuView by id (smMenuViewLeft, smMenuViewRight)");
            case 2:
                switch (menuLocation) {
                    case 1:
                        menuViewBegin = getChildAt(1);
                        break;
                    case 2:
                        menuViewEnd = getChildAt(1);
                        break;
                    default:
                        throw new IllegalArgumentException("Not find menuView by id (smMenuViewLeft, smMenuViewRight)");
                }
                break;
            default:
                menuViewBegin = getChildAt(1);
                menuViewEnd = getChildAt(2);
                break;
        }
        mContentView = getChildAt(0);
        if (menuViewBegin != null) mBeginSwiper = new Swiper(Swiper.HORIZONTAL_LEFT, menuViewBegin);
        if (menuViewEnd != null) mEndSwiper = new Swiper(Swiper.HORIZONTAL_RIGHT, menuViewEnd);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int parentViewWidth = ViewCompat.getMeasuredWidthAndState(this);
        int parentViewHeight = ViewCompat.getMeasuredHeightAndState(this);
        int contentViewWidth = ViewCompat.getMeasuredWidthAndState(mContentView);
        int contentViewHeight = ViewCompat.getMeasuredHeightAndState(mContentView);
        LayoutParams lp = (LayoutParams) mContentView.getLayoutParams();
        int lGap = getPaddingLeft() + lp.leftMargin;
        int tGap = getPaddingTop() + lp.topMargin;
        mContentView.layout(lGap, tGap, lGap + contentViewWidth, tGap + contentViewHeight);

        if (mEndSwiper != null) {
            int menuViewWidth = ViewCompat.getMeasuredWidthAndState(mEndSwiper.getMenuView());
            int menuViewHeight = ViewCompat.getMeasuredHeightAndState(mEndSwiper.getMenuView());
            lp = (LayoutParams) mEndSwiper.getMenuView().getLayoutParams();
            if (mOrientation == HORIZONTAL) {
                tGap = getPaddingTop() + lp.topMargin;
                mEndSwiper.getMenuView().layout(parentViewWidth, tGap, parentViewWidth + menuViewWidth, tGap + menuViewHeight);
            } else {
                lGap = getPaddingLeft() + lp.leftMargin;
                mEndSwiper.getMenuView().layout(lGap, parentViewHeight, lGap + menuViewWidth, parentViewHeight + menuViewHeight);
            }
        }
        if (mBeginSwiper != null) {
            int menuViewWidth = ViewCompat.getMeasuredWidthAndState(mBeginSwiper.getMenuView());
            int menuViewHeight = ViewCompat.getMeasuredHeightAndState(mBeginSwiper.getMenuView());
            lp = (LayoutParams) mBeginSwiper.getMenuView().getLayoutParams();
            if (mOrientation == HORIZONTAL) {
                tGap = getPaddingTop() + lp.topMargin;
                mBeginSwiper.getMenuView().layout(-menuViewWidth, tGap, 0, tGap + menuViewHeight);
            } else {
                lGap = getPaddingLeft() + lp.leftMargin;
                mBeginSwiper.getMenuView().layout(lGap, -menuViewHeight, menuViewWidth, 0);
            }
        }
    }

}
