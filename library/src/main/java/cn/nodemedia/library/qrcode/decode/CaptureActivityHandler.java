package cn.nodemedia.library.qrcode.decode;

import android.os.Handler;
import android.os.Message;

import cn.nodemedia.library.qrcode.decode.camera.CameraManager;

/**
 * 扫描消息转发
 */
public final class CaptureActivityHandler extends Handler {

    public static final int AUTO_FOCUS = 1;
    public static final int RESTART_PREVIEW = 2;
    public static final int DECODE = 3;
    public static final int DECODE_SUCCEEDED = 4;
    public static final int DECODE_FAILED = 5;
    public static final int QUIT = 6;

    DecodeThread decodeThread = null;
    DecodeCallback decodeCallback = null;

    private State state;

    private enum State {
        PREVIEW, SUCCESS, DONE
    }

    private String savePath = null;
    private int x = 0;
    private int y = 0;
    private int cropWidth = 0;
    private int cropHeight = 0;

    public CaptureActivityHandler(DecodeCallback decodeCallback) {
        this.decodeCallback = decodeCallback;
        decodeThread = new DecodeThread(this);
        decodeThread.start();
        state = State.SUCCESS;
        CameraManager.get().startPreview();
        restartPreviewAndDecode();
    }

    public String getSavePath() {
        return savePath;
    }

    public void setsavePath(String savePath) {
        this.savePath = savePath;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getCropWidth() {
        return cropWidth;
    }

    public void setCropWidth(int cropWidth) {
        this.cropWidth = cropWidth;
    }

    public int getCropHeight() {
        return cropHeight;
    }

    public void setCropHeight(int cropHeight) {
        this.cropHeight = cropHeight;
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case AUTO_FOCUS:
                if (state == State.PREVIEW) {
                    CameraManager.get().requestAutoFocus(this, AUTO_FOCUS);
                }
                break;
            case RESTART_PREVIEW:
                restartPreviewAndDecode();
                break;
            case DECODE_SUCCEEDED:
                state = State.SUCCESS;
                decodeCallback.decodeSuccess((String) message.obj);
                break;
            case DECODE_FAILED:
                state = State.PREVIEW;
                decodeCallback.decodeFail();
                CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), DECODE);
                break;
        }
    }

    public void quitSynchronously() {
        state = State.DONE;
        CameraManager.get().stopPreview();
        removeMessages(DECODE_SUCCEEDED);
        removeMessages(DECODE_FAILED);
        removeMessages(DECODE);
        removeMessages(AUTO_FOCUS);
    }

    private void restartPreviewAndDecode() {
        if (state == State.SUCCESS) {
            state = State.PREVIEW;
            CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), DECODE);
            CameraManager.get().requestAutoFocus(this, AUTO_FOCUS);
        }
    }

}
