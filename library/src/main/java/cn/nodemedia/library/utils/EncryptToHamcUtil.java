package cn.nodemedia.library.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * HamcMD5 加密
 * _PWD_ENCRYPT_ 标识使用密码登陆方式加密
 * Created by Bining.
 */
public class EncryptToHamcUtil {

    public static String genEncryptPwd(String openId, String encryptToken, String password) {
        return encryptValue(encryptToken, getEncrptPassword(openId, password));
    }

    /**
     * 加密值
     *
     * @param key
     * @param value
     * @return
     */
    private static String encryptValue(String key, String value) {
        if (key != null) {
            try {
                String KEY_MAC = "HmacMD5";
                SecretKey secretKey = new SecretKeySpec(key.getBytes("utf-8"), KEY_MAC);
                Mac mac = Mac.getInstance(KEY_MAC);
                mac.init(secretKey);
                byte[] hmac = mac.doFinal(value.getBytes("utf-8"));
                return bytesToHexString(hmac, hmac.length);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return value;
    }

    public static String bytesToHexString(byte[] bytes, int lenght) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (bytes != null && bytes.length > 0) {
            for (int i = 0; i < lenght; ++i) {
                int v = bytes[i] & 255;
                String hv = Integer.toHexString(v);
                if (hv.length() < 2) {
                    stringBuilder.append(0);
                }

                stringBuilder.append(hv);
            }

            return stringBuilder.toString().toUpperCase();
        } else {
            return null;
        }
    }

    /**
     * 得到需加密值
     *
     * @param openId 用户ID
     * @param pwd    用户密码
     */
    private static String getEncrptPassword(String openId, String pwd) {
        String rawPass = "u:" + openId + "&$&p:" + pwd;
        byte[] buf = rawPass.getBytes();
        byte[] en = getMessageDigest().digest(buf);
        String raw = encode(en, 0, en.length);
        raw = raw.replace("\\", "_");
        raw = raw.replace("/", "-");
        raw = raw.replace("+", "]");
        return raw;
    }

    /**
     * 默认的信息摘要
     *
     * @throws IllegalArgumentException
     */
    private static MessageDigest getMessageDigest() throws IllegalArgumentException {
        String ALGORITHM = "MD5";
        try {
            return MessageDigest.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No such algorithm [" + ALGORITHM + "]");
        }
    }

    /**
     * Returns base64 representation of specified byte array. 编码
     *
     * @param data
     * @param off
     * @param len
     */
    private static String encode(byte[] data, int off, int len) {
        char[] S_BASE64CHAR = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
                'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};
        char S_BASE64PAD = '=';

        if (len <= 0)
            return "";
        char[] out = new char[len / 3 * 4 + 4];
        int rindex = off;
        int windex = 0;
        int rest = len - off;
        while (rest >= 3) {
            int i = ((data[rindex] & 0xff) << 16) + ((data[rindex + 1] & 0xff) << 8) + (data[rindex + 2] & 0xff);
            out[windex++] = S_BASE64CHAR[i >> 18];
            out[windex++] = S_BASE64CHAR[(i >> 12) & 0x3f];
            out[windex++] = S_BASE64CHAR[(i >> 6) & 0x3f];
            out[windex++] = S_BASE64CHAR[i & 0x3f];
            rindex += 3;
            rest -= 3;
        }
        if (rest == 1) {
            int i = data[rindex] & 0xff;
            out[windex++] = S_BASE64CHAR[i >> 2];
            out[windex++] = S_BASE64CHAR[(i << 4) & 0x3f];
            out[windex++] = S_BASE64PAD;
            out[windex++] = S_BASE64PAD;
        } else if (rest == 2) {
            int i = ((data[rindex] & 0xff) << 8) + (data[rindex + 1] & 0xff);
            out[windex++] = S_BASE64CHAR[i >> 10];
            out[windex++] = S_BASE64CHAR[(i >> 4) & 0x3f];
            out[windex++] = S_BASE64CHAR[(i << 2) & 0x3f];
            out[windex++] = S_BASE64PAD;
        }

        return new String(out, 0, windex);
    }
}
