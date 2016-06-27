package cn.nodemedia.library.retrofit;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.lang.reflect.Type;

import cn.nodemedia.library.utils.Log;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.Converter;

/**
 * Created by Bining on 16/6/27.
 */
public class JSONResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private final Type type;

    public JSONResponseBodyConverter(Type type) {
        this.type = type;
    }

    /*
    * 转换方法
    */
    @Override
    public T convert(ResponseBody value) throws IOException {
        BufferedSource bufferedSource = Okio.buffer(value.source());
        String tempStr = bufferedSource.readUtf8();
        bufferedSource.close();
        Log.e("ResponseBody:" + tempStr);
        return JSON.parseObject(tempStr, type);
    }
}
