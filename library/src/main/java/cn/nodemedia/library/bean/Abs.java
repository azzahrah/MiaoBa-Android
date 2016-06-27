package cn.nodemedia.library.bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Abs implements Serializable {

    public String errCode;//错误编码
    public String message;//错误原因 (result复用参数)
    //public boolean success;//是否成功 (result复用参数)
    //public String userAttrId;//会话ID
    //public String invokeId;//是否异步回调 (result参数)
    //public String result;//结果

    public boolean isSuccess() {
        return !TextUtils.isEmpty(errCode) && errCode.equals("1");
    }

    public String getMsg() {
        return message != null ? message : getErrorMeg(errCode);
    }

    private static final Map<String, String> sErrorCode = new HashMap<>();

    static {
        sErrorCode.put("-3", "参数错误");
        sErrorCode.put("-1", "请求失败");
        sErrorCode.put("0", "数据为空");
        sErrorCode.put("1", "请求成功");
        sErrorCode.put("20000", "接口签名验证失败");
        sErrorCode.put("30000", "密码错误");
    }

    public String getErrorMeg(String code) {
        if (TextUtils.isEmpty(code)) {
            return sErrorCode.get("-1");
        }
        return (sErrorCode.get(code) != null ? sErrorCode.get(code) : sErrorCode.get("-1"));
    }
}
