package cn.nodemedia.library.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;

/**
 * 照片操作类
 * Created by Bining.
 */
public class PhotoUtils {

    public static final int REQUEST_CODE_IMAGE = 9001;//相册图片
    public static final int REQUEST_CODE_CAMERA = 9002;//拍照图片
    public static final int REQUEST_CODE_PHOTOCUT = 9003;//剪切图片

    /**
     * 获取拍照图片存放路径
     */
    public static Uri getSysDCIM() {
        // ContentValues values = new ContentValues();
        // values.put(MediaStore.Images.Media.TITLE, "cm_" + System.currentTimeMillis());
        // photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        String filePath = FileUtils.getAppDefPath(FileUtils.FILE_CACHE);
        if (!TextUtils.isEmpty(filePath)) {
            File mediaFile = new File(filePath + File.separator + "IMG_" + System.currentTimeMillis() + ".jpg");
            return Uri.fromFile(mediaFile);
        }
        return null;
    }

    /**
     * 打开系统图片选择器
     */
    public static void openImageChoice(Context context) {
        Intent intentFromGallery = new Intent();
        intentFromGallery.setType("image/*"); // 设置文件类型
        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        ((Activity) context).startActivityForResult(intentFromGallery, REQUEST_CODE_IMAGE);
    }

    /**
     * 打开系统相机
     *
     * @param uri 照片存储路径
     */
    public static void openSystemCamera(Context context, Uri uri) {
        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (uri != null) {
            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        ((Activity) context).startActivityForResult(intentFromCapture, REQUEST_CODE_CAMERA);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri 图片路径
     */
    public static Uri openPhotoCut(Context context, Uri uri) {
        return openPhotoCut(context, uri, 250, 250);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri     图片路径
     * @param outputX 裁剪宽度
     * @param outputY 裁剪高度
     */
    public static Uri openPhotoCut(Context context, Uri uri, float outputX, float outputY) {
        if (uri != null) {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(uri, "image/*");
            // 设置裁剪
            intent.putExtra("crop", "true");
            if (outputX > 0 && outputY > 0) {
                // outputX outputY 是裁剪图片宽高
                intent.putExtra("outputX", outputX);
                intent.putExtra("outputY", outputY);
                // aspectX aspectY 是宽高的比例
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
            }

            /**
             * 此方法返回的图片只能是小图片（sumsang测试为高宽160px的图片）
             * 故将图片保存在Uri中，调用时将Uri转换为Bitmap，此方法还可解决miui系统不能return data的问题
             */
            //intent.putExtra("return-data", true);

            //uritempFile为Uri类变量，实例化uritempFile

            Uri uritempFile = getSysDCIM();
            //Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "small.jpg");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

            ((Activity) context).startActivityForResult(intent, REQUEST_CODE_PHOTOCUT);

            return uritempFile;
        } else {
            ToastUtils.show(context, "未获取到图片信息.");
        }
        return null;
    }
}
