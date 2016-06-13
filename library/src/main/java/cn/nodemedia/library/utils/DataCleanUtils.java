package cn.nodemedia.library.utils;

import java.io.File;

import cn.nodemedia.library.App;

import android.annotation.SuppressLint;
import android.os.Environment;

/**
 * APP数据缓冲清除 Created by Bining.
 */
@SuppressLint("SdCardPath")
public class DataCleanUtils {

    /**
     * 清除本应用所有的数据 * * @param context * @param filepath
     */
    public static void cleanApplicationData(String... filepath) {
        cleanCache();
        cleanFiles();
        cleanDatabases();
        cleanSharedPreference();
        cleanExternalCache();
        cleanCustomCache(FileUtils.getAppDefPath(FileUtils.FILE_CACHE));
        for (String filePath : filepath) {
            cleanCustomCache(filePath);
        }
    }

    /**
     * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache) * * @param context
     */
    public static void cleanCache() {
        deleteFilesByDirectory(App.app().getCacheDir());
    }

    /**
     * 清除/data/data/com.xxx.xxx/files下的内容 * * @param context
     */
    public static void cleanFiles() {
        deleteFilesByDirectory(App.app().getFilesDir());
    }

    /**
     * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases) * * @param context
     */
    public static void cleanDatabases() {
        deleteFilesByDirectory(new File("/data/data/" + App.app().getPackageName() + "/databases"));
    }

    /**
     * * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) * * @param
     * context
     */
    public static void cleanSharedPreference() {
        deleteFilesByDirectory(new File("/data/data/" + App.getPackageName() + "/shared_prefs"));
    }

    /**
     * * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache) * * @param
     * context
     */
    public static void cleanExternalCache() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            deleteFilesByDirectory(App.app().getExternalCacheDir());
        }
    }

    /**
     * 清除自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除 * * @param filePath
     */
    public static void cleanCustomCache(String filePath) {
        deleteFilesByDirectory(new File(filePath));
    }

    /**
     * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理 * * @param directory
     */
    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }
}
