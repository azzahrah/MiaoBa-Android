package cn.nodemedia.library.utils;

import cn.nodemedia.library.App;

/**
 * 屏幕操作
 * Created by Bining.
 */
public class ScreenUtils {

    private static float density = -1F;
    private static float scaledDensity = -1F;
    private static int widthPixels = -1;
    private static int heightPixels = -1;
    private static int statusBarHeight = -1;

    public static float getDensity() {
        if (density <= 0F) {
            density = App.app().getResources().getDisplayMetrics().density;
        }
        return density;
    }

    public static float getScaledDensity() {
        if (scaledDensity <= 0F) {
            scaledDensity = App.app().getResources().getDisplayMetrics().scaledDensity;
        }
        return scaledDensity;
    }

    /**
     * 根据手机的分辨率从dp的单位转成为px(像素)
     *
     * @param dpValue DP值
     */
    public static int dp2px(float dpValue) {
        // return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
        return (int) (dpValue * getDensity() + 0.5F);
    }

    /**
     * 根据手机的分辨率从sp的单位转成为px(像素)
     *
     * @param spValue SP值
     */
    public static int sp2px(float spValue) {
        // return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue,context.getResources().getDisplayMetrics());
        return (int) (spValue * getScaledDensity() + 0.5F);
    }

    /**
     * 根据手机的分辨率从px(像素)的单位转成为dp
     *
     * @param pxValue PX值
     */
    public static float px2dp(float pxValue) {
        return pxValue / getDensity() + 0.5f;
    }

    /**
     * 根据手机的分辨率从px(像素)的单位转成为sp
     *
     * @param pxValue PX值
     */
    public static float px2sp(float pxValue) {
        return pxValue / getScaledDensity() + 0.5f;
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight() {
        if (statusBarHeight <= 0) {
            int resourceId = App.app().getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = App.app().getResources().getDimensionPixelSize(resourceId);
            }
        }
        return statusBarHeight;
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth() {
        if (widthPixels <= 0) {
            widthPixels = App.app().getResources().getDisplayMetrics().widthPixels;
        }
        return widthPixels;
    }

    /**
     * 获取屏幕高度
     */
    public static int getScreenHeight() {
        if (heightPixels <= 0) {
            heightPixels = App.app().getResources().getDisplayMetrics().heightPixels;
        }
        return heightPixels;
    }
}
