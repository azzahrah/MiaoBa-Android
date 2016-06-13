package cn.nodemedia.library.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 格式验证工具
 * Created by Bining.
 */
public class ValidUtils {

    /**
     * 验证字符串是否是邮箱
     *
     * @param params 字符串参数
     */
    public static boolean isEmail(String params) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(params);
        return m.matches();
    }

    /**
     * 验证字符串中是否包含@字符
     *
     * @param params 字符串参数
     */
    public static boolean isEmailValid(String params) {
        return params.contains("@");
    }

    /**
     * 验证字符串是否是手机号
     *
     * @param params 字符串参数
     */
    public static boolean isMobileNO(String params) {
        String str = "^((13[0-9])|(15[^4,\\D])|(17[^4,\\D])|(18[0-9]))\\d{8}$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(params);
        return m.matches();
    }

    /**
     * 验证字符串是否全为数字
     *
     * @param params 字符串参数
     */
    public static boolean isNumber(String params) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher match = pattern.matcher(params);
        return match.matches();
    }

    /**
     * 验证验证码格式
     *
     * @param params 字符串参数
     */
    public static boolean isCaptcha(String params) {
        return isNumber(params) && params.length() == 4;
    }

    /**
     * 验证字符串长度是否符合8-16位密码长度
     *
     * @param params 字符串参数
     */
    public static boolean isPasswordLength(String params) {
        return params.length() >= 6 && params.length() <= 18;
    }

    /**
     * 验证验证码格式
     *
     * @param params 字符串参数
     */
    public static boolean isPayPassword(String params) {
        return isNumber(params) && params.length() == 6;
    }

    /**
     * 验证字符串是否符合价格格式
     *
     * @param params 字符串参数
     */
    public static boolean isPrice(String params) {
        Pattern pattern = Pattern.compile("\\d{1,10}(\\.\\d{1,2})?$");
        Matcher matcher = pattern.matcher(params);
        return matcher.matches();
    }

    /**
     * 判断是否是银行卡号
     */
    public static boolean isBankCard(String cardId) {
        String regx = "\\d{16}|\\d{19}";
        return cardId.matches(regx);
    }

    /**
     * 判断是否为身份证
     */
    public static boolean isIDCard(String IDStr) {
        String regx = "\\d{15}|\\d{17}[\\dXx]";
        return IDStr.matches(regx);
    }

    /**
     * 判断是否是网址
     */
    public static boolean isLinkAvailable(String link) {
        Pattern pattern = Pattern.compile("^(http://|https://)?((?:[A-Za-z0-9]+-[A-Za-z0-9]+|[A-Za-z0-9]+)\\.)+([A-Za-z]+)[/\\?\\:]?.*$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(link);
        return matcher.matches();
    }
}
