package cn.nodemedia.leadlive.view;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lzy.okhttputils.callback.StringCallback;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.nodemedia.LivePublisher;
import cn.nodemedia.LivePublisher.LivePublishDelegate;
import cn.nodemedia.leadlive.Constants;
import cn.nodemedia.leadlive.R;
import cn.nodemedia.leadlive.utils.HttpUtils;
import cn.nodemedia.library.bean.Abs;
import cn.nodemedia.library.utils.SharedUtils;
import cn.nodemedia.library.utils.ToastUtils;
import okhttp3.Request;
import okhttp3.Response;

public class LivePublisherActivity extends Activity implements OnClickListener, LivePublishDelegate {

    @InjectView(R.id.publisher_surface)
    SurfaceView publisherSurface;
    @InjectView(R.id.publisher_mic)
    Button publisherMic;
    @InjectView(R.id.publisher_sw)
    Button publisherSw;
    @InjectView(R.id.publisher_video)
    Button publisherVideo;
    @InjectView(R.id.publisher_cam)
    Button publisherCam;
    @InjectView(R.id.publisher_flash)
    Button publisherFlash;

    private boolean isStarting = false;
    private boolean isMicOn = true;
    private boolean isCamOn = true;
    private boolean isFlsOn = true;

    private int liveId;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publisher);
        isStarting = false;

        LivePublisher.init(this); // 1.初始化
        LivePublisher.setDelegate(this); // 2.设置事件回调

        /**
         * 设置输出音频参数 码率 32kbps 使用HE-AAC ,部分服务端不支持HE-AAC,会导致发布失败
         */
        LivePublisher.setAudioParam(32 * 1000, LivePublisher.AAC_PROFILE_HE);

        /**
         * 设置输出视频参数 宽 640 高 360 fps 15 码率 300kbps 以下建议分辨率及比特率 不用超过1280x720
         * 320X180@15 ~~ 200kbps 480X272@15 ~~ 250kbps 568x320@15 ~~ 300kbps
         * 640X360@15 ~~ 400kbps 720x405@15 ~~ 500kbps 854x480@15 ~~ 600kbps
         * 960x540@15 ~~ 700kbps 1024x576@15 ~~ 800kbps 1280x720@15 ~~ 1000kbps
         * 使用main profile
         */
        LivePublisher.setVideoParam(640, 360, 15, 400 * 1000, LivePublisher.AVC_PROFILE_BASELINE);

        /**
         * 是否开启背景噪音抑制
         */
        LivePublisher.setDenoiseEnable(true);

        /**
         * 开始视频预览， cameraPreview ： 用以回显摄像头预览的SurfaceViewd对象，如果此参数传入null，则只发布音频
         * interfaceOrientation ： 程序界面的方向，也做调整摄像头旋转度数的参数， camId：
         * 摄像头初始id，LivePublisher.CAMERA_BACK 后置，LivePublisher.CAMERA_FRONT 前置
         */
        LivePublisher.startPreview(publisherSurface, getWindowManager().getDefaultDisplay().getRotation(), LivePublisher.CAMERA_FRONT); // 5.开始预览
        // 如果传null
        // 则只发布音频

        userId = SharedUtils.getInt(Constants.USEROPENID, 0);
    }

    @Override

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 注意：如果你的业务方案需求只做单一方向的视频直播，可以不处理这段

        // 如果程序UI没有锁定屏幕方向，旋转手机后，请把新的界面方向传入，以调整摄像头预览方向
        LivePublisher.setCameraOrientation(getWindowManager().getDefaultDisplay().getRotation());

        // 还没有开始发布视频的时候，可以跟随界面旋转的方向设置视频与当前界面方向一致，但一经开始发布视频，是不能修改视频发布方向的了
        // 请注意：如果视频发布过程中旋转了界面，停止发布，再开始发布，是不会触发"onConfigurationChanged"进入这个参数设置的
        if (!isStarting) {
            switch (getWindowManager().getDefaultDisplay().getRotation()) {
                case Surface.ROTATION_0:
                    LivePublisher.setVideoOrientation(LivePublisher.VIDEO_ORI_PORTRAIT);
                    break;
                case Surface.ROTATION_90:
                    LivePublisher.setVideoOrientation(LivePublisher.VIDEO_ORI_LANDSCAPE);
                    break;
                case Surface.ROTATION_180:
                    LivePublisher.setVideoOrientation(LivePublisher.VIDEO_ORI_PORTRAIT_REVERSE);
                    break;
                case Surface.ROTATION_270:
                    LivePublisher.setVideoOrientation(LivePublisher.VIDEO_ORI_LANDSCAPE_REVERSE);
                    break;
            }
        }
    }

    @Override
    @OnClick({R.id.publisher_mic, R.id.publisher_sw, R.id.publisher_video, R.id.publisher_cam, R.id.publisher_flash, R.id.publisher_cap})
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.publisher_cap:
                String capFilePath = Environment.getExternalStorageDirectory().getPath() + "/pub_cap.jpg";
                if (LivePublisher.capturePicture(capFilePath)) {
                    Toast.makeText(LivePublisherActivity.this, "截图保存到 " + capFilePath, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LivePublisherActivity.this, "截图保存失败", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.publisher_mic:
                if (isStarting) {
                    isMicOn = !isMicOn;
                    LivePublisher.setMicEnable(isMicOn); // 设置是否打开麦克风
                    if (isMicOn) {
                        handler.sendEmptyMessage(3101);
                    } else {
                        handler.sendEmptyMessage(3100);
                    }
                }
                break;
            case R.id.publisher_sw:
                LivePublisher.switchCamera();// 切换前后摄像头
                LivePublisher.setFlashEnable(false);// 关闭闪光灯,前置不支持闪光灯
                isFlsOn = false;
                publisherFlash.setBackgroundResource(R.drawable.ic_flash_off);
                break;
            case R.id.publisher_video:
                if (isStarting) {
                    HttpUtils.delLive(liveId, userId, new StringCallback() {
                        @Override
                        public void onResponse(boolean isFromCache, String s, Request request, @Nullable Response response) {
                            Abs abs = JSON.parseObject(s, Abs.class);
                            if (abs.isSuccess()) {
                                LivePublisher.stopPublish();// 停止发布
                            } else {
                                ToastUtils.show(LivePublisherActivity.this, abs.getMsg());
                            }
                        }
                    });
                } else {

                    HttpUtils.postLive(userId, "重庆市", "我是Android直播测试", new StringCallback() {
                        @Override
                        public void onResponse(boolean isFromCache, String s, Request request, @Nullable Response response) {
                            Abs abs = JSON.parseObject(s, Abs.class);
                            if (abs.isSuccess()) {
                                liveId = Integer.parseInt(abs.result);

                                /**
                                 * 设置视频发布的方向，此方法为可选，如果不调用，则输出视频方向跟随界面方向，如果特定指出视频方向，
                                 * 在startPublish前调用设置 videoOrientation ： 视频方向 VIDEO_ORI_PORTRAIT
                                 * home键在 下 的 9:16 竖屏方向 VIDEO_ORI_LANDSCAPE home键在 右 的 16:9 横屏方向
                                 * VIDEO_ORI_PORTRAIT_REVERSE home键在 上 的 9:16 竖屏方向
                                 * VIDEO_ORI_LANDSCAPE_REVERSE home键在 左 的 16:9 横屏方向
                                 */
                                // LivePublisher.setVideoOrientation(LivePublisher.VIDEO_ORI_PORTRAIT);

                                /**
                                 * 设置发布模式
                                 * 参考 rtmp_specification_1.0.pdf 7.2.2.6. publish
                                 * LivePublisher。PUBLISH_TYPE_LIVE			'live' 发布类型
                                 * LivePublisher。PUBLISH_TYPE_RECORD		'record' 发布类型
                                 * LivePublisher。PUBLISH_TYPE_APPEND		'append' 发布类型
                                 */
                                // LivePublisher.setPublishType(LivePublisher.PUBLISH_TYPE_RECORD); //

                                /**
                                 * 开始视频发布 rtmpUrl rtmp流地址
                                 */
                                String pubUrl = "rtmp://live.nodemedia.cn/live/stream_" + SharedUtils.getInt(Constants.USEROPENID, 0) + "?vhost=cdn.nodemedia.cn";
                                //SharedUtils.getString("pubUrl", "rtmp://pub.nodemedia.cn/NodeMedia/stream_" + Math.round((Math.random() * 1000 + 1000))));
                                LivePublisher.startPublish(pubUrl);
                            } else {
                                ToastUtils.show(LivePublisherActivity.this, abs.getMsg());
                            }
                        }
                    });
                }
                break;
            case R.id.publisher_flash:
                int ret = -1;
                if (isFlsOn) {
                    ret = LivePublisher.setFlashEnable(false);
                } else {
                    ret = LivePublisher.setFlashEnable(true);
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
                    LivePublisher.setCamEnable(isCamOn);
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
    public void onEventCallback(int event, String msg) {
        handler.sendEmptyMessage(event);
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
        LivePublisher.stopPreview();
        LivePublisher.stopPublish();
    }
}
