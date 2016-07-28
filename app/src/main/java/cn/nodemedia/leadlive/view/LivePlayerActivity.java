package cn.nodemedia.leadlive.view;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.nodemedia.LivePlayer;
import cn.nodemedia.LivePlayer.LivePlayerDelegate;
import cn.nodemedia.leadlive.Constants;
import cn.nodemedia.leadlive.R;
import cn.nodemedia.leadlive.bean.LiveInfo;
import cn.nodemedia.leadlive.utils.HttpUtils;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import xyz.tanwb.treasurechest.bean.Abs;
import xyz.tanwb.treasurechest.rxjava.schedulers.AndroidSchedulers;
import xyz.tanwb.treasurechest.utils.ScreenUtils;
import xyz.tanwb.treasurechest.utils.SharedUtils;
import xyz.tanwb.treasurechest.utils.ToastUtils;
import xyz.tanwb.treasurechest.view.BaseActivity;

public class LivePlayerActivity extends BaseActivity {

    @BindView(R.id.player_surfacev)
    SurfaceView playerSurfacev;
    @BindView(R.id.player_follow)
    ImageView playerFollow;

    private int userid;
    private LiveInfo liveInfo;
    private float srcWidth;
    private float srcHeight;


    @Override
    public int getLayoutId() {
        return R.layout.activity_player;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            liveInfo = (LiveInfo) savedInstanceState.getSerializable("p0");
        } else {
            liveInfo = (LiveInfo) getIntent().getSerializableExtra("p0");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("p0", liveInfo);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void initView(Bundle bundle) {
        ButterKnife.bind(this);
        LivePlayer.init(this);
        LivePlayer.setDelegate(new LivePlayerDelegate() {
            @Override
            public void onEventCallback(int event, String msg) {
                Message message = new Message();
                Bundle b = new Bundle();
                b.putString("msg", msg);
                message.setData(b);
                message.what = event;
                handler.sendMessage(message);
            }
        });

        LivePlayer.setUIVIew(playerSurfacev);

        /**
         * 设置缓冲区时长，与flash编程时一样，可以设置2个值
         * 第一个bufferTime为从连接成功到开始播放的启动缓冲区长度，越小启动速度越快，最小100毫秒
         * 注意：声音因为没有关键帧，所以这个缓冲区足够马上就可以听到声音，但视频需要等待关键帧后才会开始显示画面。
         * 如果你的服务器支持GOP_cache可以开启来加快画面的出现
         */
        int bufferTime = Integer.valueOf(SharedUtils.getString("bufferTime", "300")); // 获取上一个页面设置的bufferTIme，非sdk方法
        LivePlayer.setBufferTime(bufferTime);

        /**
         * maxBufferTime为最大缓冲区，当遇到网络抖动，较大的maxBufferTime更加平滑，但延迟也会跟着增加。
         * 这个值关乎延迟的大小。
         */
        int maxBufferTime = Integer.valueOf(SharedUtils.getString("maxBufferTime", "1000"));
        ;// 获取上一个页面设置的maxBufferTIme，非sdk方法
        LivePlayer.setMaxBufferTime(maxBufferTime);

        /**
         * 设置是否接收音视频流  协议参考 rtmp_specification_1.0.pdf 7.2.2.4. & 7.2.2.5.
         * 默认值都为true 如不需要该功能可以不设置该值
         * 注意：目前测试了fms和red5支持该参数设定有效，欢迎测试补充。目前版本只在开始播放前设置有效，中途无法变更。
         */
//		LivePlayer.receiveAudio(true);
//		LivePlayer.receiveVideo(false);

        /**
         * 当设为true时，向服务端发送FCSubscribe命令，默认不发送
         * When streaming RTMP live streams using the Akamai, Edgecast or Limelight CDN,
         * players cannot simply connect to the live stream. Instead, they have to subscribe to it,
         * by sending a so-called FC Subscribe call to the server.
         */
        LivePlayer.subscribe(true);

        String playUrl = "rtmp://alplay.nodemedia.cn/live/stream_" + liveInfo.userid;
        //SharedUtils.getString("playUrl", "rtmp://play.nodemedia.cn/NodeMedia/stream");// 获取上一页设置的播放地址，非sdk方法
        /**
         * 开始播放
         */
        LivePlayer.startPlay(playUrl);

        /**
         * Demo调试用例，每200毫秒获取一次缓冲时长 单位毫秒
         */
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				while(!LivePlayerActivity.this.isFinishing()) {
//					Log.d("NodeMedia.java","BufferLength:"+LivePlayer.getBufferLength());
//					try {
//						Thread.sleep(200);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}).start();
//
        userid = SharedUtils.getInt(Constants.USEROPENID, 0);
        if (liveInfo.is_follow) {
            playerFollow.setImageResource(R.drawable.me_yiguanzhu);
        } else {
            playerFollow.setImageResource(R.drawable.me_guanzhu);
        }
    }

    @Override
    public void initPresenter() {
    }

    @OnClick({R.id.player_follow})
    public void onClick(View v) {
        if (!isCanClick(v)) return;
        switch (v.getId()) {
            case R.id.player_follow:
                HttpUtils.postFollow(userid, liveInfo.liveid).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Abs>() {
                    @Override
                    public void call(Abs abs) {
                        if (abs.isSuccess()) {
                            liveInfo.is_follow = !liveInfo.is_follow;
                            if (liveInfo.is_follow) {
                                playerFollow.setImageResource(R.drawable.me_yiguanzhu);
                            } else {
                                playerFollow.setImageResource(R.drawable.me_guanzhu);
                            }
                        } else {
                            if (liveInfo.is_follow) {
                                ToastUtils.show(mActivity, "取消关注失败.");
                            } else {
                                ToastUtils.show(mActivity, "关注失败.");
                            }
                        }
                    }
                });
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LivePlayer.stopPlay();

    }

    /**
     * 监听手机旋转，不销毁activity进行画面旋转，再缩放显示区域
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        doVideoFix();
    }

    /**
     * 视频画面高宽等比缩放，此SDK——demo 取屏幕高宽做最大高宽缩放
     */
    private void doVideoFix() {
        float maxWidth = ScreenUtils.getScreenWidth();
        float maxHeight = ScreenUtils.getScreenHeight();
        float fixWidth;
        float fixHeight;
        if (srcWidth / srcHeight <= maxWidth / maxHeight) {
            fixWidth = srcWidth * maxHeight / srcHeight;
            fixHeight = maxHeight;
        } else {
            fixWidth = maxWidth;
            fixHeight = srcHeight * maxWidth / srcWidth;
        }
        ViewGroup.LayoutParams lp = playerSurfacev.getLayoutParams();
        lp.width = (int) fixWidth;
        lp.height = (int) fixHeight;

        playerSurfacev.setLayoutParams(lp);
    }

    private Handler handler = new Handler() {
        // 回调处理
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1000:
//                    Toast.makeText(LivePlayerActivity.this, "正在连接视频", Toast.LENGTH_SHORT).show();
                    break;
                case 1001:
//                    Toast.makeText(LivePlayerActivity.this, "视频连接成功", Toast.LENGTH_SHORT).show();
                    break;
                case 1002:
//                    Toast.makeText(LivePlayerActivity.this, "视频连接失败", Toast.LENGTH_SHORT).show();
                    //流地址不存在，或者本地网络无法和服务端通信，回调这里。5秒后重连， 可停止
                    //LivePlayer.stopPlay();
                    break;
                case 1003:
//                    Toast.makeText(LivePlayerActivity.this, "视频开始重连", Toast.LENGTH_SHORT).show();
                    //LivePlayer.stopPlay();    //自动重连总开关
                    break;
                case 1004:
//                    Toast.makeText(LivePlayerActivity.this, "视频播放结束", Toast.LENGTH_SHORT).show();
                    break;
                case 1005:
//                    Toast.makeText(LivePlayerActivity.this, "网络异常,播放中断", Toast.LENGTH_SHORT).show();
                    //播放中途网络异常，回调这里。1秒后重连，如不需要，可停止
                    //LivePlayer.stopPlay();
                    break;
                case 1100:
                    System.out.println("NetStream.Buffer.Empty");
                    break;
                case 1101:
                    System.out.println("NetStream.Buffer.Buffering");
                    break;
                case 1102:
                    System.out.println("NetStream.Buffer.Full");
                    break;
                case 1103:
                    System.out.println("Stream EOF");
                    //客服端明确收到服务端发送来的 StreamEOF 和 NetStream.Play.UnpublishNotify时回调这里
                    //收到本事件，说明：此流的发布者明确停止了发布，或者网络异常，被服务端明确关闭了流
                    //本sdk仍然会继续在1秒后重连，如不需要，可停止
                    //LivePlayer.stopPlay();
                    break;
                case 1104:
                    /**
                     * 得到 解码后得到的视频高宽值,可用于重绘surfaceview的大小比例 格式为:{width}x{height}
                     * 本例使用LinearLayout内嵌SurfaceView
                     * LinearLayout的大小为最大高宽,SurfaceView在内部等比缩放,画面不失真
                     * 等比缩放使用在不确定视频源高宽比例的场景,用上层LinearLayout限定了最大高宽
                     */
                    String[] info = msg.getData().getString("msg").split("x");
                    srcWidth = Integer.valueOf(info[0]);
                    srcHeight = Integer.valueOf(info[1]);
                    doVideoFix();
                    break;
                default:
                    break;
            }
        }
    };
}
