package cn.nodemedia.library.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * 自定义Base64加密解密
 * ENC_ 标识的使用 encryptData方法加密, 文中注明了加密密文，但未特别注明哪种加密方式的，则默认为该加密方式
 * Created by Bining.
 */
public class EncryptToBase64Util {

    /**
     * 普通加密数据
     */
    public static String encryptData(String encryptToken, String data) {
        try {
            return Base64Utils.encode(EncryptToBase64Util.encryptMessageData(data.getBytes("utf-8"), encryptToken));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 普通解密数据
     */
    public static String decryptData(String encryptToken, String encryptData) {
        try {
            return new String(EncryptToBase64Util.decryptMessageData(Base64Utils.decode(encryptData), encryptToken), "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取加密时间
     */
    public static long getEncryptMessageTime(byte[] message) {
        byte[] timeBuf = new byte[8];
        System.arraycopy(message, 0, timeBuf, 0, 8);
        return bytesToLong(timeBuf);
    }

    /**
     * 解压并解密接收到的数据
     */
    public static byte[] decryptMessageData(byte[] message, String authToken) {
        // 加密时间, skip

        // 加密识别码
        byte[] secretKey = new byte[16];
        System.arraycopy(message, 8, secretKey, 0, 16);

        // 密文
        byte[] secretData = new byte[message.length - 8 - 16];
        System.arraycopy(message, 8 + 16, secretData, 0, secretData.length);

        // 解密
        byte[] dataValue = EncryptToBase64Util.decryptDataValue(secretData, authToken);

        // 验证解密后数据是否正确
        byte[] checkKey = EncryptToBase64Util.secretDataKey(authToken, dataValue);

        if (!EncryptToBase64Util.isBytesEqual(checkKey, secretKey)) {
            throw new RuntimeException("DataError");
        }

        // 解压
        return EncryptToBase64Util.decompressData(dataValue);
    }

    /**
     * 压缩并加密数据 ，时间默认为当前时间
     */
    public static byte[] encryptMessageData(byte[] message, String authToken) {
        return encryptMessageData(System.currentTimeMillis(), message, authToken);
    }

    /**
     * 压缩并加密数据
     */
    public static byte[] encryptMessageData(long dateTime, byte[] message, String authToken) {
        // 压缩
        //byte[] dataValue = EncryptToBase64Util.compressData(message);

        // 加密识别码
        byte[] secretKey = EncryptToBase64Util.secretDataKey(authToken, message);

        // 加密
        byte[] secretData = EncryptToBase64Util.xorBytesWithKey(message, authToken);

        // 合并
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] timeBuf = longToBytes(dateTime);
        out.write(timeBuf, 0, timeBuf.length);
        out.write(secretKey, 0, secretKey.length);
        out.write(secretData, 0, secretData.length);

        return out.toByteArray();
    }

    /**
     * long转换成byte数组
     */
    public static byte[] longToBytes(long x) {
        byte[] ar = new byte[8];

        ar[0] = (byte) (x >> 58);
        ar[1] = (byte) (x >> 48);
        ar[2] = (byte) (x >> 40);
        ar[3] = (byte) (x >> 32);
        ar[4] = (byte) (x >> 24);
        ar[5] = (byte) (x >> 16);
        ar[6] = (byte) (x >> 8);
        ar[7] = (byte) (x);

        return ar;
    }

    /**
     * byte数组转换成long
     */
    public static long bytesToLong(byte[] ar) {
        return ((((long) ar[0] & 0xff) << 56) |
                (((long) ar[1] & 0xff) << 48) |
                (((long) ar[2] & 0xff) << 40) |
                (((long) ar[3] & 0xff) << 32) |
                (((long) ar[4] & 0xff) << 24) |
                (((long) ar[5] & 0xff) << 16) |
                (((long) ar[6] & 0xff) << 8) |
                (((long) ar[7] & 0xff)));
    }

    /**
     * 数据压缩
     */
    public static byte[] compressData(byte[] buf) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            GZIPOutputStream gos = new GZIPOutputStream(out);
            gos.write(buf, 0, buf.length);
            //gos.finish();
            gos.flush();
            gos.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return out.toByteArray();
    }

    /**
     * 数据解压
     */
    public static byte[] decompressData(byte[] buf) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(buf);
        byte[] data = new byte[1024];
        int count;
        try {
            GZIPInputStream gis = new GZIPInputStream(in);
            while ((count = gis.read(data, 0, data.length)) != -1) {
                out.write(data, 0, count);
            }
            gis.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return out.toByteArray();
    }

    /**
     * to bytes
     */
    public static byte[] getBytes(String key) {
        try {
            return key.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * to String
     */
    public static String getString(byte[] data) {
        try {
            return new String(data, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 加盐Key
     */
    public static byte[] secretDataKey(String authToken, byte[] value) {
        if (authToken != null && authToken.length() > 0) {
            try {
                String KEY_MAC = "HmacMD5";
                SecretKey secretKey = new SecretKeySpec(getBytes(authToken), KEY_MAC);
                Mac mac = Mac.getInstance(KEY_MAC);
                mac.init(secretKey);
                return mac.doFinal(value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return new byte[16];
    }

    /**
     * 数据解密
     */
    public static byte[] decryptDataValue(byte[] data, String key) {
        return xorBytesWithKey(data, key);
    }

    /**
     * 异或加密解密
     */
    private static byte[] xorBytesWithKey(byte[] data, String key) {
        try {
            if (key == null || key.length() <= 0) {
                return data;
            }

            byte[] dataNew = new byte[data.length];
            byte[] bufKey = getBytes(key);
            int step = 0;

            while (step < data.length) {
                for (int i = 0; i < bufKey.length; i++) {
                    byte keyB = bufKey[i];

                    if (step + i == data.length) {
                        return dataNew;
                    }

                    dataNew[step + i] = (byte) (keyB ^ data[step + i]);
                }

                step += bufKey.length;
            }

            return dataNew;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * byte数组是否相等
     */
    public static boolean isBytesEqual(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        }

        for (int i = 0; i < a.length; i++) {
            byte b1 = a[i];
            byte b2 = b[i];

            if (b1 != b2) {
                return false;
            }
        }

        return true;
    }
}
