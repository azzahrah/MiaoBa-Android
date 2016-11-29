package cn.nodemedia.leadlive.utils;

import com.alibaba.fastjson.JSON;

import okhttp3.ResponseBody;
import xyz.tanwb.airship.bean.AbsS;
import xyz.tanwb.airship.bean.UploadInfo;

public abstract class HttpCallback<T> extends xyz.tanwb.airship.retrofit.HttpCallback<T> {

    private boolean isToJson = true;

    public AbsS absS = null;

    @Override
    protected void onNextUpload(ResponseBody responseBody) throws Exception {
        String result = toString(responseBody);
        UploadInfo uploadInfo = JSON.parseObject(result, UploadInfo.class);
        if (uploadInfo.success) {
            if (uploadInfo.returnTargets != null && uploadInfo.returnTargets.size() > 0) {
                onSuccess((T) uploadInfo.returnTargets.get(0));
            } else {
                onSuccess(null);
            }
        } else {
            onFailure(uploadInfo.message);
        }
    }

    @Override
    public void onNextData(ResponseBody responseBody) throws Exception {
        String result = toString(responseBody);
        if (isToJson) {
            absS = JSON.parseObject(result, AbsS.class);
        } else {
            absS = new AbsS();
            absS.result = result;
            absS.errCode = absS.isNotEmpty() ? "1" : "0";
        }
        if (absS.isCodeSuccess()) {
            if (absS.result.startsWith("{") || absS.result.startsWith("[")) {
                onSuccess((T) JSON.parseObject(absS.result, mType));
            } else {
                onSuccess((T) absS.result);
            }
        } else {
            onFailure(absS.getMsg());
        }

//        AbsS absS = JSON.parseObject(result, AbsS.class);
//        if (absS.errCode.equals("1")) {
//            if (TextUtils.isEmpty(absS.result)) {
//                onSuccess(null);
//            } else {
//                if (absS.result.startsWith("{") || absS.result.startsWith("[")) {
//                    onSuccess((T) JSON.parseObject(absS.result, mType));
//                } else {
//                    onSuccess((T) absS.result);
//                }
//            }
//        }
    }

    public void setToJson(boolean toJson) {
        isToJson = toJson;
    }

}