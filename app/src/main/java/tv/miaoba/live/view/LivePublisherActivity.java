package tv.miaoba.live.view;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import cn.nodemedia.NodeCameraView;
import cn.nodemedia.NodePublisher;
import cn.nodemedia.NodePublisherDelegate;
import tv.miaoba.live.Constants;
import tv.miaoba.live.R;
import xyz.tanwb.airship.utils.SharedUtils;
import xyz.tanwb.airship.utils.StatusBarUtils;
import xyz.tanwb.airship.view.BaseActivity;

public class LivePublisherActivity extends BaseActivity implements OnClickListener ,NodePublisherDelegate{

    @BindView(R.id.publisher_surface)
    NodeCameraView publisherSurface;
    @BindView(R.id.publisher_mic)
    Button publisherMic;
    @BindView(R.id.publisher_sw)
    Button publisherSw;
    @BindView(R.id.publisher_video)
    Button publisherVideo;
    @BindView(R.id.publisher_cam)
    Button publisherCam;
    @BindView(R.id.publisher_flash)
    Button publisherFlash;
    @BindView(R.id.publisher_cap)
    ImageView publisherCap;

    private NodePublisher np;
    private boolean isStarting = false;
    private boolean isMicOn = true;
    private boolean isCamOn = true;
    private boolean isFlsOn = true;

    private int userId;

    @Override
    public int getLayoutId() {
        return R.layout.activity_publisher;
    }

    @Override
    public void initView(Bundle bundle) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        StatusBarUtils.setColorToTransparent(mActivity);

        isStarting = false;
        userId = SharedUtils.getInt(Constants.USEROPENID, 0);

        np = new NodePublisher(this);
        np.setNodePublisherDelegate(this);
        np.setCameraPreview(publisherSurface, NodePublisher.CAMERA_FRONT, true);
        /**
         * 设置输出音频参数 码率 32kbps 使用HE-AAC ,部分服务端不支持HE-AAC,会导致发布失败
         */
        np.setAudioParam(32 * 1000, NodePublisher.AUDIO_PROFILE_HEAAC);


        np.setVideoParam(NodePublisher.VIDEO_PPRESET_16X9_360, 24, 500 * 1000, NodePublisher.VIDEO_PROFILE_MAIN, false);

        /**
         * 是否开启背景噪音抑制
         */
        np.setDenoiseEnable(true);

        /**
         * 美颜等级 3
         */
        np.setBeautyLevel(3);

        /**
         * 开始预览
         */
        np.startPreview();

    }

    @Override
    public void initPresenter() {
    }



    @Override
    @OnClick({R.id.publisher_mic, R.id.publisher_sw, R.id.publisher_video, R.id.publisher_cam, R.id.publisher_flash, R.id.publisher_cap})
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.publisher_mic:
                if (isStarting) {
                    isMicOn = !isMicOn;
                    np.setAudioEnable(isMicOn); // 设置是否打开麦克风
                    if (isMicOn) {
                        handler.sendEmptyMessage(3101);
                    } else {
                        handler.sendEmptyMessage(3100);
                    }
                }
                break;
            case R.id.publisher_sw:
                np.switchCamera();// 切换前后摄像头
                np.setFlashEnable(false);// 关闭闪光灯,前置不支持闪光灯
                isFlsOn = false;
                publisherFlash.setBackgroundResource(R.drawable.ic_flash_off);
                break;
            case R.id.publisher_video:
                if (isStarting) {
                    np.stop();
                } else {

                    /**
                     * 开始视频发布 rtmpUrl rtmp流地址
                     */
                    String pubUrl = "rtmp://xypush.nodemedia.cn/live/stream_" + SharedUtils.getInt(Constants.USEROPENID, 0) + "?userid=" + userId + "&location=" + "重庆市" + "&title=" + "我是Android直播测试" + userId;
                    np.setOutputUrl(pubUrl);
                    np.start();
                }
                break;
            case R.id.publisher_flash:
                int ret = -1;
                if (isFlsOn) {
                    ret = np.setFlashEnable(false);
                } else {
                    ret = np.setFlashEnable(true);
                }
                if (ret == -1) {
                    // 无闪光灯,或处于前置摄像头,不支持闪光灯操作
                } else if (ret == 0) {
                    // 闪光灯被关闭
                    publisherFlash.setBackgroundResource(R.drawable.ic_flash_off);
                    isFlsOn = false;
                } else {
                    // 闪光灯被打开
                    publisherFlash.setBackgroundResource(R.drawable.ic_flash_on);
                    isFlsOn = true;
                }
                break;
            case R.id.publisher_cam:
                if (isStarting) {
                    isCamOn = !isCamOn;
                    np.setVideoEnable(isCamOn);
                    if (isCamOn) {
                        handler.sendEmptyMessage(3103);
                    } else {
                        handler.sendEmptyMessage(3102);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onEventCallback(NodePublisher nodePublisher, int event, String s) {
        if (handler != null) {
            handler.sendEmptyMessage(event);
        }
    }

    private Handler handler = new Handler() {
        // 回调处理
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2000:
                    Toast.makeText(LivePublisherActivity.this, "正在发布视频", Toast.LENGTH_SHORT).show();
                    break;
                case 2001:
                    Toast.makeText(LivePublisherActivity.this, "视频发布成功", Toast.LENGTH_SHORT).show();
                    publisherVideo.setBackgroundResource(R.drawable.ic_video_start);
                    isStarting = true;
                    break;
                case 2002:
                    Toast.makeText(LivePublisherActivity.this, "视频发布失败", Toast.LENGTH_SHORT).show();
                    break;
                case 2004:
                    Toast.makeText(LivePublisherActivity.this, "视频发布结束", Toast.LENGTH_SHORT).show();
                    publisherVideo.setBackgroundResource(R.drawable.ic_video_stop);
                    isStarting = false;
                    break;
                case 2005:
                    Toast.makeText(LivePublisherActivity.this, "网络异常,发布中断", Toast.LENGTH_SHORT).show();
                    break;
                case 2100:
                    //发布端网络阻塞，已缓冲了2秒的数据在队列中
                    Toast.makeText(LivePublisherActivity.this, "网络阻塞，发布卡顿", Toast.LENGTH_SHORT).show();
                    break;
                case 2101:
                    //发布端网络恢复畅通
                    Toast.makeText(LivePublisherActivity.this, "网络恢复，发布流畅", Toast.LENGTH_SHORT).show();
                    break;
                case 3100:
                    // mic off
                    publisherMic.setBackgroundResource(R.drawable.ic_mic_off);
                    Toast.makeText(LivePublisherActivity.this, "麦克风静音", Toast.LENGTH_SHORT).show();
                    break;
                case 3101:
                    // mic on
                    publisherMic.setBackgroundResource(R.drawable.ic_mic_on);
                    Toast.makeText(LivePublisherActivity.this, "麦克风恢复", Toast.LENGTH_SHORT).show();
                    break;
                case 3102:
                    // camera off
                    publisherCam.setBackgroundResource(R.drawable.ic_cam_off);
                    Toast.makeText(LivePublisherActivity.this, "摄像头传输关闭", Toast.LENGTH_SHORT).show();
                    break;
                case 3103:
                    // camera on
                    publisherCam.setBackgroundResource(R.drawable.ic_cam_on);
                    Toast.makeText(LivePublisherActivity.this, "摄像头传输打开", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler = null;
        np.stop();
        np.stopPreview();
        np.release();
    }

}
