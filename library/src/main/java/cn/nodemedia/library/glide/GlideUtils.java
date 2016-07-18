package cn.nodemedia.library.glide;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import cn.nodemedia.library.bean.ImagePath;
import cn.nodemedia.library.utils.ImageUtils;
import cn.nodemedia.library.utils.Log;
import cn.nodemedia.library.utils.ScreenUtils;
import cn.nodemedia.library.view.widget.BubbleImageView;

import java.io.File;

/**
 * Glide 图片加载封装
 * Created by Bining.
 * 图片路径说明:http://域名(IP)[:端口]/应用路径/image/图片路径/图片名称.jpg
 */
public class GlideUtils {

    public static final int IMAGE_TYPE_NORMAL = 0;
    public static final int IMAGE_TYPE_ROUND = 1;
    public static final int IMAGE_TYPE_CIRCLE = 2;
    public static final int IMAGE_TYPE_ZOOM = 3;

    private static String baseUrl = null;

    public static void initGlide(Context context, String httpurl) {
        baseUrl = httpurl;
        //Glide.get(context).register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(OkHttpManager.getInstance().getOkHttpClient()));
    }

    /**
     * 获取图片的完整路径
     *
     * @param imagePathJson 图片对象JSON
     * @param resId         模板图片
     */
    public static String getImageCompletePath(String imagePathJson, int resId) {
        int[] loadSize = null;
        if (resId > 0) {
            loadSize = ImageUtils.getBitmapRect(resId);
        }
        return getImageCompletePath(imagePathJson, loadSize);
    }

    /**
     * 获取图片的完整路径
     *
     * @param imagePathJson 图片对象JSON
     * @param loadSize      图片加载大小(null 表示加载原图)
     */
    public static String getImageCompletePath(String imagePathJson, int[] loadSize) {
        if (!TextUtils.isEmpty(imagePathJson)) {
            ImagePath imagePath = JSON.parseObject(imagePathJson, ImagePath.class);
            return getImageCompletePath(imagePath, loadSize);
        }
        return null;
    }

    /**
     * 获取图片的完整路径
     *
     * @param imagePath 图片对象
     * @param loadSize  图片加载大小(null 表示加载原图)
     */
    public static String getImageCompletePath(ImagePath imagePath, int[] loadSize) {
        if (baseUrl != null && imagePath != null && !imagePath.imageEmpty) {
            StringBuilder stringPath = new StringBuilder();
            stringPath.append(imagePath.imagePrefix);
            if (loadSize != null && loadSize[0] > 0 && loadSize[1] > 0) {
                stringPath.append("-res-").append(loadSize[0]).append("-").append(loadSize[1]);
            }
            stringPath.append(imagePath.imageSuffix);

            return baseUrl + stringPath.toString();
        }
        return null;
    }

    /**
     * 从URl地址获取圆形图片
     *
     * @param imagePathJson ImagePath对象JSON
     * @param imageView     ImageView控件
     * @param resId         默认图片路径
     */
    public static void getCircleImageToUrl(Context activity, String imagePathJson, ImageView imageView, @DrawableRes int resId) {
        getIamgeToUrl(activity, imagePathJson, imageView, resId, GlideUtils.IMAGE_TYPE_CIRCLE);
    }

    /**
     * 从URl地址获取圆角图片
     *
     * @param imagePathJson ImagePath对象JSON
     * @param imageView     ImageView控件
     * @param resId         默认图片路径
     */
    public static void getRoundImageToUrl(Context activity, String imagePathJson, ImageView imageView, @DrawableRes int resId) {
        getIamgeToUrl(activity, imagePathJson, imageView, resId, GlideUtils.IMAGE_TYPE_ROUND);
    }

    /**
     * 从URl地址获取缩放图片
     *
     * @param imagePathJson ImagePath对象JSON
     * @param imageView     ImageView控件
     * @param resId         默认图片路径
     */
    public static void getZoomImageToUrl(Context activity, String imagePathJson, ImageView imageView, @DrawableRes int resId) {
        getIamgeToUrl(activity, imagePathJson, imageView, resId, GlideUtils.IMAGE_TYPE_ZOOM);
    }

    /**
     * 从URl地址获取普通图片
     *
     * @param imagePathJson ImagePath对象JSON
     * @param imageView     ImageView控件
     * @param resId         默认图片路径
     */
    public static void getNormalIamgeToUrl(Context activity, String imagePathJson, ImageView imageView, @DrawableRes int resId) {
        getIamgeToUrl(activity, imagePathJson, imageView, resId, GlideUtils.IMAGE_TYPE_NORMAL);
    }

    /**
     * 从URl地址获取图片
     *
     * @param imagePathJson ImagePath对象JSON
     * @param imageView     ImageView控件
     * @param resId         默认图片路径
     * @param imageType     图片裁剪类型
     */
    private static void getIamgeToUrl(Context activity, String imagePathJson, ImageView imageView, @DrawableRes int resId, int imageType) {
        ImagePath imagePath = JSON.parseObject(imagePathJson, ImagePath.class);
        getIamgeToUrl(activity, imagePath, imageView, resId, imageType);
    }

    /**
     * 从URl地址获取图片
     *
     * @param imagePath ImagePath对象
     * @param imageView ImageView控件
     * @param resId     默认图片路径
     * @param imageType 图片裁剪类型
     */
    private static void getIamgeToUrl(Context activity, ImagePath imagePath, ImageView imageView, @DrawableRes int resId, int imageType) {
        if (imagePath != null) {
            BitmapTransformation bitmapTransformation;
            switch (imageType) {
                case IMAGE_TYPE_CIRCLE:
                    bitmapTransformation = new GlideCircleTransform(activity);
                    break;
                case IMAGE_TYPE_ROUND:
                    bitmapTransformation = new GlideRoundTransform(activity);
                    break;
                case IMAGE_TYPE_ZOOM:
                    bitmapTransformation = new GlideZoomTransform(activity, 0);
                    break;
                case IMAGE_TYPE_NORMAL:
                default:
                    bitmapTransformation = new GlideNormalTransform(activity);
                    break;
            }

            if (!TextUtils.isEmpty(imagePath.imageLocalPath)) {
                File file = new File(imagePath.imageLocalPath);
                if (file.exists()) {
                    Glide.with(activity).load(file).asBitmap().error(resId).transform(bitmapTransformation).into(imageView);
                    return;
                }
            } else {
                String path = getImageCompletePath(imagePath, ImageUtils.getBitmapRect(resId));
                if (!TextUtils.isEmpty(path)) {
                    Glide.with(activity).load(path).asBitmap().error(resId).transform(bitmapTransformation).into(imageView);
                    return;
                }
            }
        }
        imageView.setImageResource(resId);
    }

    public static void changeImageSize(Context activity, String imagePathJson, BubbleImageView imageView, int resId) {
        Log.e("Image imagePathJson : " + imagePathJson);
        ImagePath imagePath = JSON.parseObject(imagePathJson, ImagePath.class);

        int sealeWidth = ScreenUtils.dp2px(48) / 2;
        if (imagePath.imageWidth > sealeWidth) {
            sealeWidth = (int) Math.sqrt(sealeWidth * imagePath.imageWidth);

            int sealedMaxwidth = ScreenUtils.getScreenWidth() / 2;
            if (sealeWidth > sealedMaxwidth) {
                sealeWidth = sealedMaxwidth;
            }
        }
        int sealeHeight = imagePath.imageHeight * sealeWidth / imagePath.imageWidth;

        Log.e("Image sealewidth:" + sealeWidth + " sealeheight : " + sealeHeight);
        //imageView.setImageScaledSize(sealeWidth, sealeHeight);
        imageView.setLayoutParams(new RelativeLayout.LayoutParams(sealeWidth, sealeHeight));

        getIamgeToUrl(activity, imagePath, imageView, resId, IMAGE_TYPE_NORMAL);
    }

    public static void changeLoacImageSize(Context activity, String imagePathJson, BubbleImageView imageView, int resId) {
        Log.e("Image imagePathJson : " + imagePathJson);
        ImagePath imagePath = JSON.parseObject(imagePathJson, ImagePath.class);

        int sealeWidth = ScreenUtils.getScreenWidth() / 2;
        int sealeHeight = imagePath.imageHeight * sealeWidth / imagePath.imageWidth;

        Log.e("Image sealewidth:" + sealeWidth + " sealeheight : " + sealeHeight);
        //imageView.setImageScaledSize(sealeWidth, sealeHeight);
        imageView.setLayoutParams(new RelativeLayout.LayoutParams(sealeWidth, sealeHeight));

        getIamgeToUrl(activity, imagePath, imageView, resId, IMAGE_TYPE_NORMAL);
    }
}
