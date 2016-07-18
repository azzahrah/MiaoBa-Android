package cn.nodemedia.library.qrcode;

/**
 * zbar调用类
 */
public class Zbar {

    static {
        System.loadLibrary("zbar");
    }

    public static native String decode(byte[] data, int width, int height, boolean isCrop, int x, int y, int cwidth, int cheight);
}
