package cn.nodemedia.library.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.io.File;
import java.io.FileOutputStream;

import cn.nodemedia.library.utils.FileUtils;
import cn.nodemedia.library.utils.Log;

public class AudioRecorder {

    private static AudioRecorder audioRecorder;

    public static synchronized AudioRecorder getInstance() {
        if (audioRecorder == null) {
            audioRecorder = new AudioRecorder();
        }
        return audioRecorder;
    }

    private AudioRecord mAudioRecord;
    private FileOutputStream fos;
    private RecordAudioThread mRecordAudioThread;
    private boolean mRecordThreadExitFlag = false;
    private boolean mAudioRecordReleased = true;
    private int mFrameBufferSize;

    /**
     * 启动音频录制
     *
     * @param sampleRate 采样率 默认8000
     * @param channels   声道设置 1:单声道 2:立体声
     * @return 文件路径
     */
    public String startAudioRecorder(int sampleRate, int channels) {
        releaseAudioRecorder();

        try {
            int channel;

            if (channels == 1) {
                channel = AudioFormat.CHANNEL_IN_MONO;
            } else {
                channel = AudioFormat.CHANNEL_IN_STEREO;
            }

            int samplebit = AudioFormat.ENCODING_PCM_16BIT;

            //获取创建AudioRecord所需的最小缓冲区大小
            mFrameBufferSize = AudioRecord.getMinBufferSize(sampleRate, channel, samplebit);
            if (mFrameBufferSize == AudioRecord.ERROR_BAD_VALUE) {
                Log.e("getMinBufferSize error");
                return null;
            } else if (mFrameBufferSize < 2048) {
                mFrameBufferSize = 2048;
            }

            //audioSource 音频源：指的是从哪里采集音频。这里我们当然是从麦克风采集音频，所以此参数的值为MIC
            //sampleRateInHz 采样率：音频的采样频率，每秒钟能够采样的次数，采样率越高，音质越高。给出的实例是44100、22050、11025但不限于这几个参数。例如要采集低质量的音频就可以使用4000、8000等低采样率。
            //channelConfig 声道设置：android支持双声道立体声和单声道。MONO单声道，STEREO立体声
            //audioFormat 编码制式和采样大小：采集来的数据当然使用PCM编码(脉冲代码调制编码，即PCM编码。PCM通过抽样、量化、编码三个步骤将连续变化的模拟信号转换为数字编码。) android支持的采样大小16bit 或者8bit。当然采样大小越大，那么信息量越多，音质也越高，现在主流的采样大小都是16bit，在低质量的语音传输的时候8bit足够了。
            //bufferSizeInBytes 采集数据需要的缓冲区的大小，如果不知道最小需要的大小可以在getMinBufferSize()查看。
            mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channel, samplebit, mFrameBufferSize);

            //mAudioRecord.startRecording();
            //byte[] data = new byte[mFrameBufferSize];
            //int ret = mAudioRecord.read(data, 0, mFrameBufferSize);
            //if (ret == AudioRecord.ERROR_INVALID_OPERATION || ret == AudioRecord.ERROR_BAD_VALUE) {
            //throw new Exception();
            //}

            File file = FileUtils.createFile(FileUtils.getAppDefPath(FileUtils.FILE_FILE), "AUDIO_" + System.currentTimeMillis() + ".amr");
            if (file == null) {
                return null;
            }

            fos = new FileOutputStream(file);

            mAudioRecordReleased = false;
            mRecordThreadExitFlag = false;
            mRecordAudioThread = new RecordAudioThread();
            mRecordAudioThread.start();
            Log.e("AudioRecorder starting...");
            return file.getPath();
        } catch (Exception e) {
            return null;
        }
    }

    public void releaseAudioRecorder() {
        if (mAudioRecordReleased) {
            return;
        }
        if (mRecordAudioThread != null) {
            mRecordThreadExitFlag = true;
            mRecordAudioThread = null;
        }
        if (mAudioRecord != null) {
            mAudioRecord.stop();
            mAudioRecord.release();
            mAudioRecord = null;
        }
        mAudioRecordReleased = true;
        Log.e("AudioRecorder stoped...");
    }

    class RecordAudioThread extends Thread {
        @Override
        public void run() {
            try {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

                // 初始化音频解码 参数:30 ,20, 15
                AudioCodec.audio_codec_init(30);

                mAudioRecord.startRecording();

                byte[] data = new byte[mFrameBufferSize];
                byte[] encodedData = new byte[mFrameBufferSize];

                while (!mRecordThreadExitFlag) {
                    int dataSize = mAudioRecord.read(data, 0, mFrameBufferSize);
                    if (dataSize == AudioRecord.ERROR_INVALID_OPERATION || dataSize == AudioRecord.ERROR_BAD_VALUE) {
                        break;
                    }

                    int encoderSize = AudioCodec.audio_encode(data, 0, dataSize, encodedData, 0);
                    if (encoderSize > 0 && fos != null) {
                        fos.write(encodedData);
                    }
                }

                if (fos != null) {
                    fos.close();
                    fos = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}