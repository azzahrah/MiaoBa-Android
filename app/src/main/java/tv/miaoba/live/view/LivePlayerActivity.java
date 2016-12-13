package tv.miaoba.live.view;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.w3c.dom.Node;

import butterknife.BindView;
import butterknife.OnClick;
import cn.nodemedia.NodePlayer;
import cn.nodemedia.NodePlayerDelegate;
import tv.miaoba.live.Constants;
import tv.miaoba.live.R;
import tv.miaoba.live.bean.LiveInfo;
import tv.miaoba.live.utils.HttpCallback;
import tv.miaoba.live.utils.HttpUtils;
import xyz.tanwb.airship.utils.ScreenUtils;
import xyz.tanwb.airship.utils.SharedUtils;
import xyz.tanwb.airship.utils.StatusBarUtils;
import xyz.tanwb.airship.utils.ToastUtils;
import xyz.tanwb.airship.view.BaseActivity;

public class LivePlayerActivity extends BaseActivity {

    @BindView(R.id.player_surfacev)
    SurfaceView playerSurfacev;
    @BindView(R.id.player_follow)
    ImageView playerFollow;

    private int userid;
    private LiveInfo liveInfo;
    private NodePlayer np;
    @Override
    public int getLayoutId() {
        return R.layout.activity_player;
    }

    @Override
    public void initView(Bundle bundle) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        StatusBarUtils.setColorToTransparent(mActivity);

        if (bundle != null) {
            liveInfo = (LiveInfo) bundle.getSerializable("p0");
        } else {
            liveInfo = (LiveInfo) getIntent().getSerializableExtra("p0");
        }

        np = new NodePlayer(this);
        np.setDelegate(new NodePlayerDelegate() {
            @Override
            public void onEventCallback(NodePlayer np, int event, String msg) {
                Message message = new Message();
                Bundle b = new Bundle();
                b.putString("msg", msg);
                message.setData(b);
                message.what = event;
                handler.sendMessage(message);
            }
        });

        np.setSurfaceView(playerSurfacev, NodePlayer.UIViewContentModeScaleAspectFill);

        /**
         * 设置缓冲区时长，与flash编程时一样，可以设置2个值
         * 第一个bufferTime为从连接成功到开始播放的启动缓冲区长度，越小启动速度越快，最小100毫秒
         * 注意：声音因为没有关键帧，所以这个缓冲区足够马上就可以听到声音，但视频需要等待关键帧后才会开始显示画面。
         * 如果你的服务器支持GOP_cache可以开启来加快画面的出现
         */
        int bufferTime = Integer.valueOf(SharedUtils.getString("bufferTime", "300")); // 获取上一个页面设置的bufferTIme，非sdk方法
        np.setBufferTime(bufferTime);

        /**
         * maxBufferTime为最大缓冲区，当遇到网络抖动，较大的maxBufferTime更加平滑，但延迟也会跟着增加。
         * 这个值关乎延迟的大小。
         */
        int maxBufferTime = Integer.valueOf(SharedUtils.getString("maxBufferTime", "1000"));
        ;// 获取上一个页面设置的maxBufferTIme，非sdk方法
        np.setMaxBufferTime(maxBufferTime);


        String playUrl = "rtmp://xyplay.nodemedia.cn/live/stream_" + liveInfo.userid;
        //SharedUtils.getString("playUrl", "rtmp://play.nodemedia.cn/NodeMedia/stream");// 获取上一页设置的播放地址，非sdk方法
        /**
         * 开始播放
         */
        np.startPlay(playUrl);

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("p0", liveInfo);
        super.onSaveInstanceState(outState);
    }

    @OnClick({R.id.player_follow})
    public void onClick(View v) {
        if (!isCanClick(v)) return;
        switch (v.getId()) {
            case R.id.player_follow:
                HttpUtils.postFollow(userid, liveInfo.liveid, new HttpCallback<Object>() {
                    @Override
                    public void onSuccess(Object o) {
                        liveInfo.is_follow = !liveInfo.is_follow;
                        if (liveInfo.is_follow) {
                            playerFollow.setImageResource(R.drawable.me_yiguanzhu);
                        } else {
                            playerFollow.setImageResource(R.drawable.me_guanzhu);
                        }
                    }

                    @Override
                    public void onFailure(String strMsg) {
                        super.onFailure(strMsg);
                        if (liveInfo.is_follow) {
                            ToastUtils.show(mActivity, "取消关注失败.");
                        } else {
                            ToastUtils.show(mActivity, "关注失败.");
                        }
                    }
                });
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        np.stopPlay();
        np.deInit();
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
                    break;
                default:
                    break;
            }
        }
    };
}
