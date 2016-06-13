package cn.nodemedia.library.qrcode.decode;

/**
 * 解析结果返回
 */
public interface DecodeCallback {

    void decodeSuccess(String result);

    void decodeFail();
}
