package cn.nodemedia.library.utils;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.nodemedia.library.App;

/**
 * 图片操作
 * Created by Bining.
 */
public class ImageUtils {

    /**
     * 保存Image至/data/data/packagename/cache目录
     *
     * @param bitmap Bitmap
     * @return 图片路径
     */
    public static String saveImageToCache(Bitmap bitmap) {
        return saveImageToCache(System.currentTimeMillis() + ".jpg", bitmap);
    }

    /**
     * 保存Image至/data/data/packagename/cache目录
     *
     * @param fileName 指定文件名称
     * @param bitmap   Bitmap
     * @return 图片路径
     */
    public static String saveImageToCache(String fileName, Bitmap bitmap) {
        return saveImageToCache(fileName, bitmap, 75);
    }

    /**
     * 保存Image至/data/data/packagename/cache目录
     *
     * @param fileName 指定文件名称
     * @param bitmap   Bitmap
     * @param quality  图片质量
     * @return 图片路径
     */
    public static String saveImageToCache(String fileName, Bitmap bitmap, int quality) {
        if (!TextUtils.isEmpty(fileName) && bitmap != null) {
            String filePath = App.app().getCacheDir().getPath() + File.separator + fileName;
            return saveImage(filePath, bitmap, quality);
        }
        return null;
    }

    /**
     * 保存Image至SD卡APP默认目录
     *
     * @param bitmap Bitmap
     * @return 图片路径
     */
    public static String saveImage(Bitmap bitmap) {
        String filePath = FileUtils.getAppDefPath(FileUtils.FILE_IMAGE) + File.separator + "IMG_"
                + System.currentTimeMillis() + ".jpg";
        return saveImage(filePath, bitmap);
    }

    /**
     * 保存Image(默认图片质量为75)
     *
     * @param filePath 指定文件保存路径
     * @param bitmap   Bitmap
     * @return 图片路径
     */
    public static String saveImage(String filePath, Bitmap bitmap) {
        return saveImage(filePath, bitmap, 75);
    }

    /**
     * 保存Image
     *
     * @param filePath 指定文件保存路径
     * @param bitmap   Bitmap
     * @param quality  图片质量
     * @return 图片路径
     */
    public static String saveImage(String filePath, Bitmap bitmap, int quality) {
        try {
            if (filePath != null && bitmap != null) {
                FileOutputStream e = new FileOutputStream(filePath);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
                byte[] bytes = stream.toByteArray();
                e.write(bytes);
                e.close();
                return filePath;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据资源ID从资源文件夹下面获取图片
     *
     * @param resId 资源ID
     */
    public static Bitmap getBitmapByRes(int resId) {
        return resId > 0 ? BitmapFactory.decodeResource(App.app().getResources(), resId) : null;
    }

    /**
     * 从图片文件路径下获取图片
     *
     * @param filePath 图片文件路径
     */
    public static Bitmap getBitmapByPath(String filePath) {
        return getBitmapByPath(filePath, null);
    }

    /**
     * 从图片文件路径下获取图片
     *
     * @param filePath 图片文件路径
     * @param filePath 图片文件路径
     */
    public static Bitmap getBitmapByPath(String filePath, BitmapFactory.Options opts) {
        if (opts == null) {
            opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = false;
            opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        }
        return BitmapFactory.decodeFile(filePath, opts);
    }

    /**
     * 从Uri获取图片
     *
     * @param uri 图片Uri路径
     */
    public static Bitmap getBitmapByUri(Uri uri) {
        try {
            return BitmapFactory.decodeStream(App.app().getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取图片缩略图
     *
     * @param context
     * @param imageName
     * @param kind
     * @return
     */
    public static Bitmap getImageThumbnail(Context context, String imageName, int kind) {
        String[] proj = new String[]{"_id", "_display_name"};
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj,
                "_display_name=\'" + imageName + "\'", (String[]) null, (String) null);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            ContentResolver crThumb = context.getContentResolver();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            return MediaStore.Images.Thumbnails.getThumbnail(crThumb, (long) cursor.getInt(0), kind, options);
        } else {
            return null;
        }
    }

    public static Bitmap getImageThumbnail(String imagePath, int width, int height) {
        return ThumbnailUtils.extractThumbnail(getSmallBitmap(imagePath, width, height), width, height, 2);
    }

    public static Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        return ThumbnailUtils.extractThumbnail(bitmap, width, height, 2);
    }

    public static Bitmap getSmallBitmap(String imagePath, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        int scale = Math.max(Math.round((float) options.outHeight / (float) width),
                Math.round((float) options.outWidth / (float) height));
        options.inSampleSize = scale > 0 ? scale : 1;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imagePath, options);
    }

    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Matrix matrix = new Matrix();
            float scaleWidht = (float) w / (float) width;
            float scaleHeight = (float) h / (float) height;
            matrix.postScale(scaleWidht, scaleHeight);
            return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        } else {
            return null;
        }
    }

    public static Bitmap getImageCrop(Bitmap bitmap) {
        int imageRect = Math.min(bitmap.getWidth(), bitmap.getHeight());
        return getImageCrop(bitmap, imageRect, imageRect);
    }

    public static Bitmap getImageCrop(Bitmap bitmap, int cropWidth, int cropHeight) {
        if (bitmap != null && bitmap.getWidth() >= cropWidth && bitmap.getHeight() >= cropHeight) {
            int retX = (bitmap.getWidth() - cropWidth) / 2;
            int retY = (bitmap.getHeight() - cropHeight) / 2;
            return Bitmap.createBitmap(bitmap, retX, retY, cropWidth, cropHeight, (Matrix) null, false);
        } else {
            return bitmap;
        }
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.WHITE);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static Bitmap getRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int imageSize = Math.min(width, height);
        if (width != height) {
            bitmap = getImageCrop(bitmap);
        }

        float roundPx = (float) (imageSize / 2);
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        Rect src = new Rect(0, 0, imageSize, imageSize);
        Rect dst = new Rect(0, 0, imageSize, imageSize);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(roundPx, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
    }

    public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(1.0F, -1.0F);
        Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2, width, height / 2, matrix, false);
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, height + height / 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(bitmap, 0.0F, 0.0F, (Paint) null);
        Paint deafalutPaint = new Paint();
        canvas.drawRect(0.0F, (float) height, (float) width, (float) (height + 4), deafalutPaint);
        canvas.drawBitmap(reflectionImage, 0.0F, (float) (height + 4), (Paint) null);
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0.0F, (float) bitmap.getHeight(), 0.0F,
                (float) (bitmapWithReflection.getHeight() + 4), 1895825407, 16777215, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawRect(0.0F, (float) height, (float) width, (float) (bitmapWithReflection.getHeight() + 4), paint);
        return bitmapWithReflection;
    }

    public static String getAbsolutePathFromNoStandardUri(Uri mUri) {
        String mUriString = Uri.decode(mUri.toString());
        String pre1 = "file://sdcard" + File.separator;
        String pre2 = "file://mnt/sdcard" + File.separator;
        return mUriString.startsWith(pre1) ? FileUtils.getSDCardPath() + mUriString.substring(pre1.length())
                : (mUriString.startsWith(pre2) ? FileUtils.getSDCardPath() + mUriString.substring(pre2.length())
                : null);
    }

    @TargetApi(19)
    public static String getPath(Context context, Uri uri) {
        boolean isKitKat = Build.VERSION.SDK_INT >= 19;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            String docId;
            String[] split;
            String type;
            if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                docId = DocumentsContract.getDocumentId(uri);
                split = docId.split(":");
                type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else {
                if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                    docId = DocumentsContract.getDocumentId(uri);
                    Uri split1 = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                            Long.valueOf(docId).longValue());
                    return getDataColumn(context, split1, (String) null, (String[]) null);
                }

                if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                    docId = DocumentsContract.getDocumentId(uri);
                    split = docId.split(":");
                    type = split[0];
                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    String selection = "_id=?";
                    String[] selectionArgs = new String[]{split[1]};
                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
        } else {
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                if ("com.google.android.apps.photos.content".equals(uri.getAuthority())) {
                    return uri.getLastPathSegment();
                }

                return getDataColumn(context, uri, (String) null, (String[]) null);
            }

            if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String[] projection = new String[]{"_data"};

        String var8;
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, (String) null);
            if (cursor == null || !cursor.moveToFirst()) {
                return null;
            }

            int index = cursor.getColumnIndexOrThrow("_data");
            var8 = cursor.getString(index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }

        }

        return var8;
    }

    public static String getLatestImage(Context context) {
        String[] items = new String[]{"_id", "_data"};
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, items,
                (String) null, (String[]) null, "_id desc");
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                return cursor.getString(1);
            }
        }

        return null;
    }

    public static int[] scaleImageSize(Bitmap bitmap, int[] squareRect, boolean enlarge, boolean narrow) {
        if (bitmap != null) {
            int[] imageRect = new int[]{bitmap.getWidth(), bitmap.getHeight()};
            return scaleImageSize(imageRect, squareRect, enlarge, narrow);
        } else {
            return null;
        }
    }

    public static int[] scaleImageSize(int[] imageRect, int[] squareRect, boolean enlarge, boolean narrow) {
        if (imageRect != null && squareRect != null) {
            if (imageRect[0] == squareRect[0] && imageRect[1] == squareRect[1]) {
                return imageRect;
            } else if (!enlarge && imageRect[0] <= squareRect[0] && imageRect[1] <= squareRect[1]) {
                return imageRect;
            } else if (!narrow && imageRect[0] >= squareRect[0] && imageRect[1] >= squareRect[1]) {
                return imageRect;
            } else {
                double scaleWidth = (double) squareRect[0] / (double) imageRect[0];
                double scaleHeight = (double) squareRect[1] / (double) imageRect[1];
                return scaleWidth > scaleHeight
                        ? new int[]{(int) ((double) imageRect[0] * scaleHeight),
                        (int) ((double) imageRect[1] * scaleHeight)}
                        : new int[]{(int) ((double) imageRect[0] * scaleWidth),
                        (int) ((double) imageRect[1] * scaleWidth)};
            }
        } else {
            return null;
        }
    }

    public static int[] getBitmapRect(int resId) {
        Bitmap bitmap = getBitmapByRes(resId);
        return bitmap != null ? new int[]{bitmap.getWidth(), bitmap.getHeight()} : null;
    }

    public static int readPictureDegree(String imagePath) {
        short degree = 0;

        try {
            ExifInterface e = new ExifInterface(imagePath);
            int orientation = e.getAttributeInt("Orientation", 1);
            switch (orientation) {
                case 3:
                    degree = 180;
                    break;
                case 4:
                case 5:
                case 7:
                default:
                    degree = 0;
                    break;
                case 6:
                    degree = 90;
                    break;
                case 8:
                    degree = 270;
            }
        } catch (IOException var4) {
            var4.printStackTrace();
        }

        return degree;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int degress) {
        if (bitmap != null) {
            Matrix m = new Matrix();
            m.postRotate((float) degress);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
        } else {
            return null;
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        return bitmapDrawable.getBitmap();
    }

    public static Drawable bitmapToDrawable(Context context, Bitmap bitmap) {
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    /**
     * 画圆角图形,默认的半径为10
     *
     * @param bitmap
     * @return rounded corner bitmap
     */
    public static Bitmap roundCorners(final Bitmap bitmap) {
        return roundCorners(bitmap, 10);
    }

    public static Bitmap roundCorners(final Bitmap bitmap, final int radius) {
        return roundCorners(bitmap, radius, radius);
    }

    public static Bitmap roundCorners(final Bitmap bitmap, final int radiusX, final int radiusY) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);

        Bitmap clipped = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(clipped);
        canvas.drawRoundRect(new RectF(0, 0, width, height), radiusX, radiusY, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        Bitmap rounded = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(rounded);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.drawBitmap(clipped, 0, 0, paint);

        return rounded;
    }

    public static Bitmap decodeBitmapFromResource(Bitmap.Config config, Resources res, int resId, int reqWidth,
                                                  int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = config;

        return BitmapFactory.decodeResource(res, resId, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    /**
     * scale image
     *
     * @param org
     * @param newWidth
     * @param newHeight
     * @return
     */
    public static Bitmap scaleImageTo(Bitmap org, int newWidth, int newHeight) {
        return scaleImage(org, (float) newWidth / org.getWidth(), (float) newHeight / org.getHeight());
    }

    /**
     * scale image
     *
     * @param org
     * @param scaleWidth  sacle of width
     * @param scaleHeight scale of height
     * @return
     */
    public static Bitmap scaleImage(Bitmap org, float scaleWidth, float scaleHeight) {
        if (org == null) {
            return null;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(org, 0, 0, org.getWidth(), org.getHeight(), matrix, true);
    }
}
