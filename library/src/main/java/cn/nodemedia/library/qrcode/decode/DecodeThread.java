package cn.nodemedia.library.qrcode.decode;

import java.util.concurrent.CountDownLatch;

import android.os.Handler;
import android.os.Looper;

/**
 * 解码线程
 */
final class DecodeThread extends Thread {

    private CaptureActivityHandler captureActivityHandler;
    private Handler handler;
    private final CountDownLatch handlerInitLatch;

    DecodeThread(CaptureActivityHandler captureActivityHandler) {
        this.captureActivityHandler = captureActivityHandler;
        handlerInitLatch = new CountDownLatch(1);
    }

    Handler getHandler() {
        try {
            handlerInitLatch.await();
        } catch (InterruptedException ie) {
            // continue?
        }
        return handler;
    }

    @Override
    public void run() {
        Looper.prepare();
        handler = new DecodeHandler(captureActivityHandler);
        handlerInitLatch.countDown();
        Looper.loop();
    }

}
