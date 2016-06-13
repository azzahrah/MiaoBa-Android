package cn.nodemedia.library.utils;

import android.content.Context;
import android.text.TextUtils;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.DecimalFormat;
import java.util.HashMap;

import cn.nodemedia.library.App;

/**
 * 进程间锁, 仅在同一个应用中有效.
 */
public final class ProcessLock implements Closeable {

    private final String mLockName;
    private final FileLock mFileLock;
    private final File mFile;
    private final Closeable mStream;

    private final static String LOCK_FILE_DIR = "process_lock";
    private final static int PID = android.os.Process.myPid();
    private final static HashMap<String, ProcessLock> LOCK_MAP = new HashMap<String, ProcessLock>(5);

    static {
        deleteFileOrDir(App.app().getDir(LOCK_FILE_DIR, Context.MODE_PRIVATE));
    }

    private ProcessLock(String lockName, File file, FileLock fileLock, Closeable stream) {
        mLockName = lockName;
        mFileLock = fileLock;
        mFile = file;
        mStream = stream;
    }

    /**
     * 获取进程锁
     *
     * @param lockName
     * @param writeMode 是否写入模式(支持读并发).
     * @return null 或 进程锁, 如果锁已经被占用, 返回null.
     */
    public static ProcessLock tryLock(final String lockName, final boolean writeMode) {
        return tryLockInternal(lockName, customHash(lockName), writeMode);
    }

    /**
     * 获取进程锁
     *
     * @param lockName
     * @param writeMode         是否写入模式(支持读并发).
     * @param maxWaitTimeMillis 最大值 1000 * 60
     * @return null 或 进程锁, 如果锁已经被占用, 则在超时时间内继续尝试获取该锁.
     */
    public static ProcessLock tryLock(final String lockName, final boolean writeMode, final long maxWaitTimeMillis) {
        ProcessLock lock = null;
        long expiryTime = System.currentTimeMillis() + maxWaitTimeMillis;
        String hash = customHash(lockName);
        while (System.currentTimeMillis() < expiryTime) {
            lock = tryLockInternal(lockName, hash, writeMode);
            if (lock != null) {
                break;
            } else {
                try {
                    Thread.sleep(1); // milliseconds
                } catch (Throwable ignored) {
                }
            }
        }

        return lock;
    }

    /**
     * 锁是否有效
     *
     * @return
     */
    public boolean isValid() {
        return isValid(mFileLock);
    }

    /**
     * 释放锁
     */
    public void release() {
        release(mLockName, mFileLock, mFile, mStream);
    }

    /**
     * 释放锁
     */
    @Override
    public void close() throws IOException {
        release();
    }

    private static boolean isValid(FileLock fileLock) {
        return fileLock != null && fileLock.isValid();
    }

    private static void release(String lockName, FileLock fileLock, File file, Closeable stream) {
        synchronized (LOCK_MAP) {
            if (fileLock != null) {
                try {
                    LOCK_MAP.remove(lockName);
                    fileLock.release();
                    Log.d("released: " + lockName + ":" + PID);
                } catch (Throwable ignored) {
                } finally {
                    try {
                        if (fileLock.channel() != null) {
                            fileLock.channel().close();
                        }
                    } catch (Throwable throwable) {
                        Log.d(throwable.getMessage());
                    }
                }
            }

            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (Throwable throwable) {
                Log.d(throwable.getMessage());
            }
            deleteFileOrDir(file);
        }
    }

    public static boolean deleteFileOrDir(File path) {
        if (path == null || !path.exists()) {
            return true;
        }
        if (path.isFile()) {
            return path.delete();
        }
        File[] files = path.listFiles();
        if (files != null) {
            for (File file : files) {
                deleteFileOrDir(file);
            }
        }
        return path.delete();
    }

    private final static DecimalFormat FORMAT = new DecimalFormat("0.##################");

    // 取得字符串的自定义hash值, 尽量保证255字节内的hash不重复.
    private static String customHash(String str) {
        if (TextUtils.isEmpty(str))
            return "0";
        double hash = 0.0;
        byte[] bytes = str.getBytes();
        for (int i = 0; i < str.length(); i++) {
            hash = (255.0 * hash + bytes[i]) * 0.005;
        }
        return FORMAT.format(hash);
    }

    private static ProcessLock tryLockInternal(final String lockName, final String hash, final boolean writeMode) {
        synchronized (LOCK_MAP) {

            // android对文件锁共享支持的不好, 暂时全部互斥.
            if (LOCK_MAP.containsKey(lockName)) {
                ProcessLock lock = LOCK_MAP.get(lockName);
                if (lock == null) {
                    LOCK_MAP.remove(lockName);
                } else if (lock.isValid()) {
                    return null;
                } else {
                    LOCK_MAP.remove(lockName);
                    lock.release();
                }
            }

            FileInputStream in = null;
            FileOutputStream out = null;
            Closeable stream = null;
            FileChannel channel = null;
            try {
                File file = new File(App.app().getDir(LOCK_FILE_DIR, Context.MODE_PRIVATE), hash);
                if (file.exists() || file.createNewFile()) {
                    if (writeMode) {
                        out = new FileOutputStream(file, false);
                        channel = out.getChannel();
                        stream = out;
                    } else {
                        in = new FileInputStream(file);
                        channel = in.getChannel();
                        stream = in;
                    }
                    if (channel != null) {
                        FileLock fileLock = channel.tryLock(0L, Long.MAX_VALUE, !writeMode);
                        if (isValid(fileLock)) {
                            Log.d("lock: " + lockName + ":" + PID);
                            ProcessLock processLock = new ProcessLock(lockName, file, fileLock, stream);
                            LOCK_MAP.put(lockName, processLock);
                            return processLock;
                        } else {
                            release(lockName, fileLock, file, out);
                        }
                    } else {
                        throw new IOException("can not get file channel:" + file.getAbsolutePath());
                    }
                }
            } catch (Throwable ignored) {
                Log.d("tryLock: " + lockName + ", " + ignored.getMessage());
                try {
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                    if (channel != null) {
                        channel.close();
                    }
                } catch (Throwable throwable) {
                    Log.d(throwable.getMessage());
                }
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return mLockName;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.release();
    }
}
