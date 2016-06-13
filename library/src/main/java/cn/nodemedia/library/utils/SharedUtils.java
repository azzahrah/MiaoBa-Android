package cn.nodemedia.library.utils;

import android.content.SharedPreferences;

import java.util.Map;

import cn.nodemedia.library.App;

/**
 * 使用SharedPreferences存储数据
 * Created by Bining.
 */
public class SharedUtils {

    private static SharedPreferences getSharedPreferences() {
        return App.app().getSharedPreferences(SharedUtils.class.getSimpleName(), 0);
    }

    private static SharedPreferences.Editor getEdit() {
        return getSharedPreferences().edit();
    }

    public static void put(String key, Object object) {
        SharedPreferences.Editor editor = getEdit();
        if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        editor.commit();
    }

    public static String getString(String key, String defaultObject) {
        return getSharedPreferences().getString(key, defaultObject);
    }

    public static int getInt(String key, int defaultObject) {
        return getSharedPreferences().getInt(key, defaultObject);
    }

    public static boolean getBoolean(String key, boolean defaultObject) {
        return getSharedPreferences().getBoolean(key, defaultObject);
    }

    public static float getFloat(String key, float defaultObject) {
        return getSharedPreferences().getFloat(key, defaultObject);
    }

    public static long getLong(String key, long defaultObject) {
        return getSharedPreferences().getLong(key, defaultObject);
    }

    /**
     * 判断SharedPreferences中是否包含Key
     *
     * @param key 键名
     * @return
     */
    public static boolean contains(String key) {
        return getSharedPreferences().contains(key);
    }

    /**
     * 从SharedPreferences中删除Key
     *
     * @param key 键名
     */
    public static void remove(String key) {
        SharedPreferences.Editor editor = getEdit();
        editor.remove(key);
        editor.commit();
    }

    /**
     * 获取SharedPreferences中的所有值
     */
    public static Map<String, ?> getAll() {
        return getSharedPreferences().getAll();
    }

    /**
     * 清空SharedPreferences
     */
    public static void clear() {
        SharedPreferences.Editor editor = getEdit();
        editor.clear();
        editor.commit();
    }
}
