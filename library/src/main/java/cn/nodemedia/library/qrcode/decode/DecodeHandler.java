package cn.nodemedia.library.qrcode.decode;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import cn.nodemedia.library.qrcode.Zbar;
import cn.nodemedia.library.qrcode.decode.bitmap.PlanarYUVLuminanceSource;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 接受消息后解码
 */
final class DecodeHandler extends Handler {

    private CaptureActivityHandler handler;

    DecodeHandler(CaptureActivityHandler handler) {
        this.handler = handler;
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case CaptureActivityHandler.DECODE:
                decode((byte[]) message.obj, message.arg1, message.arg2);
                break;
            case CaptureActivityHandler.QUIT:
                Looper.myLooper().quit();
                break;
        }
    }

    private void decode(byte[] data, int width, int height) {
        byte[] rotatedData = new byte[data.length];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++)
                rotatedData[x * height + height - y - 1] = data[x + y * width];
        }
        int tmp = width;// Here we are swapping, that's the difference to #11
        width = height;
        height = tmp;

        Zbar manager = new Zbar();
        String result = manager.decode(rotatedData, width, height, true, handler.getX(), handler.getY(), handler.getCropWidth(), handler.getCropHeight());

        if (result != null) {
            if (!TextUtils.isEmpty(handler.getSavePath())) {
                // 生成bitmap
                PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(rotatedData, width, height, handler.getX(), handler.getY(), handler.getCropWidth(), handler.getCropHeight(), false);
                int[] pixels = source.renderThumbnail();
                int w = source.getThumbnailWidth();
                int h = source.getThumbnailHeight();
                Bitmap bitmap = Bitmap.createBitmap(pixels, 0, w, w, h, Bitmap.Config.ARGB_8888);
                try {
                    File f = new File(handler.getSavePath());
                    if (f.exists()) {
                        f.delete();
                    }
                    f.createNewFile();

                    FileOutputStream out = new FileOutputStream(f);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (null != handler) {
                Message msg = new Message();
                msg.obj = result;
                msg.what = CaptureActivityHandler.DECODE_SUCCEEDED;
                handler.sendMessage(msg);
            }
        } else {
            if (null != handler) {
                handler.sendEmptyMessage(CaptureActivityHandler.DECODE_FAILED);
            }
        }
    }

}
