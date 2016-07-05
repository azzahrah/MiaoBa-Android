package cn.nodemedia.library.view.widget.swipemenu;

import android.support.annotation.IntDef;
import android.view.View;
import android.widget.OverScroller;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Swiper {

    public static final int HORIZONTAL_LEFT = 0x00000001;
    public static final int HORIZONTAL_RIGHT = 0x00000002;
    public static final int VERTICAL_TOP = 0x00000003;
    public static final int VERTICAL_BOTTOM = 0x00000004;

    @IntDef({HORIZONTAL_LEFT, HORIZONTAL_RIGHT, VERTICAL_TOP, VERTICAL_BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SwiperType {
    }

    private int swiperType;
    private View menuView;
    protected Checker mChecker;

    public Swiper(@SwiperType int swiperType, View menuView) {
        this.menuView = menuView;
        this.swiperType = swiperType;
        mChecker = new Checker();
    }

    public boolean isMenuOpen(int scroll) {
        switch (swiperType) {
            case HORIZONTAL_LEFT:
                return scroll <= -getMenuView().getWidth();
            case HORIZONTAL_RIGHT:
                return scroll >= getMenuView().getWidth();
            case VERTICAL_TOP:
                return scroll <= -getMenuView().getHeight();
            case VERTICAL_BOTTOM:
                return scroll >= getMenuView().getHeight();
        }
        return false;
    }

    public boolean isMenuOpenNotEqual(int scroll) {
        switch (swiperType) {
            case HORIZONTAL_LEFT:
                return scroll < -getMenuView().getWidth();
            case HORIZONTAL_RIGHT:
                return scroll > getMenuView().getWidth();
            case VERTICAL_TOP:
                return scroll < -getMenuView().getHeight();
            case VERTICAL_BOTTOM:
                return scroll > getMenuView().getHeight();
        }
        return false;
    }

    public void autoOpenMenu(OverScroller scroller, int scroll, int duration) {
        switch (swiperType) {
            case HORIZONTAL_LEFT:
                scroller.startScroll(Math.abs(scroll), 0, getMenuView().getWidth() - Math.abs(scroll), 0, duration);
            case HORIZONTAL_RIGHT:
                scroller.startScroll(Math.abs(scroll), 0, getMenuView().getWidth() - Math.abs(scroll), 0, duration);
            case VERTICAL_TOP:
                scroller.startScroll(0, Math.abs(scroll), 0, getMenuView().getHeight() - Math.abs(scroll), duration);
            case VERTICAL_BOTTOM:
                scroller.startScroll(0, Math.abs(scroll), 0, getMenuView().getHeight() - Math.abs(scroll), duration);
        }
    }

    public void autoCloseMenu(OverScroller scroller, int scroll, int duration) {
        switch (swiperType) {
            case HORIZONTAL_LEFT:
                scroller.startScroll(-Math.abs(scroll), 0, Math.abs(scroll), 0, duration);
            case HORIZONTAL_RIGHT:
                scroller.startScroll(-Math.abs(scroll), 0, Math.abs(scroll), 0, duration);
            case VERTICAL_TOP:
                scroller.startScroll(0, -Math.abs(scroll), 0, Math.abs(scroll), duration);
            case VERTICAL_BOTTOM:
                scroller.startScroll(0, -Math.abs(scroll), 0, Math.abs(scroll), duration);
        }
    }

    public Checker checkXY(int x, int y) {
        mChecker.x = x;
        mChecker.y = y;
        mChecker.shouldResetSwiper = false;
        switch (swiperType) {
            case HORIZONTAL_LEFT:
                if (mChecker.x == 0) {
                    mChecker.shouldResetSwiper = true;
                } else if (mChecker.x >= 0) {
                    mChecker.x = 0;
                } else if (mChecker.x <= -getMenuView().getWidth()) {
                    mChecker.x = -getMenuView().getWidth();
                }
                break;
            case HORIZONTAL_RIGHT:
                if (mChecker.x == 0) {
                    mChecker.shouldResetSwiper = true;
                } else if (mChecker.x < 0) {
                    mChecker.x = 0;
                } else if (mChecker.x > getMenuView().getWidth()) {
                    mChecker.x = getMenuView().getWidth();
                }
                break;
            case VERTICAL_TOP:
                if (mChecker.y == 0) {
                    mChecker.shouldResetSwiper = true;
                } else if (mChecker.y >= 0) {
                    mChecker.y = 0;
                } else if (mChecker.y <= -getMenuView().getHeight()) {
                    mChecker.y = -getMenuView().getHeight();
                }
                break;
            case VERTICAL_BOTTOM:
                if (mChecker.y == 0) {
                    mChecker.shouldResetSwiper = true;
                } else if (mChecker.y < 0) {
                    mChecker.y = 0;
                } else if (mChecker.y > getMenuView().getHeight()) {
                    mChecker.y = getMenuView().getHeight();
                }
                break;
        }
        return mChecker;
    }

    public boolean isClickOnContentView(View contentView, float distance) {
        switch (swiperType) {
            case HORIZONTAL_LEFT:
                return distance > getMenuView().getWidth();
            case HORIZONTAL_RIGHT:
                return distance < (contentView.getWidth() - getMenuView().getWidth());
            case VERTICAL_TOP:
                return distance > getMenuView().getHeight();
            case VERTICAL_BOTTOM:
                return distance < (contentView.getHeight() - getMenuView().getHeight());
        }
        return false;
    }

    public int getSwiperType() {
        return swiperType;
    }

    public View getMenuView() {
        return menuView;
    }

    public int getMenuWidth() {
        return getMenuView().getWidth();
    }

    public int getMenuHeight() {
        return getMenuView().getHeight();
    }

    public static final class Checker {
        public int x;
        public int y;
        public boolean shouldResetSwiper;
    }
}
