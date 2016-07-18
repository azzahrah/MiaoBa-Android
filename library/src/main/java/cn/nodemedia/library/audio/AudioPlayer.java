package cn.nodemedia.library.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;

import java.io.File;
import java.io.FileInputStream;

import cn.nodemedia.library.utils.Log;

public class AudioPlayer {

    private static AudioPlayer player;

    public static synchronized AudioPlayer getInstance() {
        if (player == null) {
            player = new AudioPlayer();
        }
        return player;
    }

    private AudioTrack audioTrack;
    private FileInputStream fis;
    private PlayerAudioThread playerAudioThread;
    private boolean mPlayerThreadExitFlag = false;
    private boolean mAudioPlayerReleased = true;
    private int mFrameBufferSize;

    public boolean startAudioPlayer(String fileUrl, int sampleRate, int channels) {
        releaseAudioPlayer();

        int channel;

        if (channels == 1) {
            channel = AudioFormat.CHANNEL_IN_MONO;
        } else {
            channel = AudioFormat.CHANNEL_IN_STEREO;
        }

        int samplebit = AudioFormat.ENCODING_PCM_16BIT;

        mFrameBufferSize = AudioRecord.getMinBufferSize(sampleRate, channel, samplebit);
        if (mFrameBufferSize == AudioRecord.ERROR_BAD_VALUE) {
            Log.e("getMinBufferSize error");
            return false;
        } else if (mFrameBufferSize < 2048) {
            mFrameBufferSize = 2048;
        }

        // streamType 指定流的类型
        // sampleRateInHz 设置音频数据的采样率
        // channelConfig 设置输出声道
        // audioFormat 设置音频数据块
        // bufferSizeInBytes 缓冲区的大小
        // mode 设置模式类型
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, channel, AudioFormat.ENCODING_PCM_16BIT, mFrameBufferSize, AudioTrack.MODE_STREAM);
        //设置播放音量
        audioTrack.setStereoVolume(1.0f, 1.0f);

        File file = new File(fileUrl);
        try {
            fis = new FileInputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mAudioPlayerReleased = false;
        mPlayerThreadExitFlag = false;
        playerAudioThread = new PlayerAudioThread();
        playerAudioThread.start();
        Log.e("AudioPlayer starting...");
        return true;
    }

    public void releaseAudioPlayer() {
        if (mAudioPlayerReleased) {
            return;
        }
        if (playerAudioThread != null) {
            mPlayerThreadExitFlag = true;
            playerAudioThread = null;
        }
        if (audioTrack != null) {
            if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                audioTrack.stop();
                audioTrack.release();
            }
            audioTrack = null;
        }
        mAudioPlayerReleased = true;
        Log.e("AudioPlayer stoped...");
    }

    class PlayerAudioThread extends Thread {
        @Override
        public void run() {
            try {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
            } catch (Exception e) {
                Log.e("Set player thread priority failed: " + e.getMessage());
            }

            // 初始化音频解码 参数:30 ,20, 15
            AudioCodec.audio_codec_init(30);

            audioTrack.play();

            byte[] data = new byte[mFrameBufferSize];
            byte[] decodedData = new byte[mFrameBufferSize];

            int dataSize;

            try {
                while (mPlayerThreadExitFlag && ((dataSize = fis.read(data)) != -1)) {
                    int decodeSize = AudioCodec.audio_decode(data, 0, dataSize, decodedData, 0);
                    Log.e("原始长度:" + dataSize + " 解码后的长度:" + decodeSize);
                    if (decodeSize > 0) {
                        audioTrack.write(decodedData, 0, decodeSize);
                        // clear data
                        data = new byte[mFrameBufferSize];
                        decodedData = new byte[mFrameBufferSize];
                    }
                }

                if (fis != null) {
                    fis.close();
                    fis = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
