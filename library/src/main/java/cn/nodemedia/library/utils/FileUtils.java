package cn.nodemedia.library.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cn.nodemedia.library.App;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

/**
 * 文件操作 Created by Bining.
 */
public class FileUtils {

    public static final String FILE_CACHE = "Cache";
    public static final String FILE_IMAGE = "Image";
    public static final String FILE_FILE = "File";
    public static final String FILE_VIDEO = "Video";
    public static final String FILE_SOUND = "Sound";

    /**
     * 判断是否存在SD卡
     */
    public static boolean isSDCardEnable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SD卡根目录地址
     *
     * @return 根目录
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    }

    /**
     * 获取App默认文件路径
     *
     * @param dirName Pass {@link #FILE_CACHE} {@link #FILE_IMAGE}
     *                {@link #FILE_FILE} {@link #FILE_VIDEO} {@link #FILE_SOUND}.
     *                Default value is {@link #FILE_CACHE}.
     */
    public static String getAppDefPath(String dirName) {
        File result;
        if (isSDCardEnable()) {
            StringBuilder appDefPath = new StringBuilder();
            appDefPath.append(getSDCardPath()).append(App.getAppName()).append(File.separator).append(dirName);
            result = createDirectory(appDefPath.toString());
        } else {
            result = App.app().getCacheDir();
        }

        if (isFileExists(result)) {
            return result.getPath();
        } else {
            return null;
        }
    }

    /**
     * 判断文件或文件夹是否存在
     *
     * @param filePath 文件或文件夹路径
     */
    public static boolean isFileExists(String filePath) {
        return !TextUtils.isEmpty(filePath) && isFileExists(new File(filePath));
    }

    /**
     * 判断文件或文件夹是否存在
     *
     * @param file 文件或文件夹对象
     */
    public static boolean isFileExists(File file) {
        return file != null && file.exists();
    }

    /**
     * 创建文件夹
     *
     * @param directoryPath 文件夹路径
     */
    public static File createDirectory(String directoryPath) {
        if (!TextUtils.isEmpty(directoryPath)) {
            return createDirectory(new File(directoryPath));
        }
        return null;
    }

    /**
     * 创建文件夹
     *
     * @param dirFile File对象
     */
    public static File createDirectory(File dirFile) {
        if (dirFile != null) {
            if (!isFileExists(dirFile)) {
                dirFile.mkdirs();
            }
            return dirFile;
        }
        return null;
    }

    /**
     * 创建文件
     *
     * @param directoryPath 文件所在文件夹路径
     * @param fileName      文件名称
     */
    public static File createFile(String directoryPath, String fileName) {
        if (!TextUtils.isEmpty(directoryPath)) {
            return createFile(new File(directoryPath), fileName);
        }
        return null;
    }

    /**
     * 创建文件
     *
     * @param dFile    文件所在文件夹对象
     * @param fileName 文件名称
     */
    public static File createFile(File dFile, String fileName) {
        if (!TextUtils.isEmpty(fileName) && createDirectory(dFile) != null) {
            return new File(dFile, fileName);
        }
        return null;
    }

    /**
     * 从文件路径获取文件夹路径
     */
    public static String getFolderName(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            int separatorIndex = filePath.lastIndexOf(File.separator);
            return (separatorIndex == -1) ? "" : filePath.substring(0, separatorIndex);
        }
        return null;
    }

    /**
     * 从文件路径中获取文件名
     *
     * @param filePath 文件路径
     * @param isSuffix 是否带后缀
     */
    public static String getFileName(String filePath, boolean isSuffix) {
        if (!TextUtils.isEmpty(filePath)) {
            int separatorIndex = filePath.lastIndexOf(File.separator) + 1;
            if (!isSuffix) {
                int dotIndex = filePath.lastIndexOf(".");
                if (dotIndex > -1) {
                    return filePath.substring(separatorIndex, dotIndex);
                }
            }
            return filePath.substring(separatorIndex);
        }
        return null;
    }

    /**
     * 从文件路径中获取文件后缀
     *
     * @param filePath 文件路径
     * @param isDot    是否带'.'
     */
    public static String getFileSuffix(String filePath, boolean isDot) {
        assert filePath != null;
        return filePath.substring(filePath.lastIndexOf(".") + (isDot ? 0 : 1));
    }

    /**
     * 根据文件后缀获取文件MIME类型
     *
     * @param fileSuffix 文件后缀名
     */
    public static String getMIMEType(String fileSuffix) {
        String[][] MIMEMap = new String[][]{{".3gp", "video/3gpp"},
                {".apk", "application/vnd.android.package-archive"}, {".asf", "video/x-ms-asf"},
                {".avi", "video/x-msvideo"}, {".bin", "application/octet-stream"}, {".bmp", "image/bmp"},
                {".c", "text/plain"}, {".class", "application/octet-stream"}, {".conf", "text/plain"},
                {".cpp", "text/plain"}, {".doc", "application/msword"},
                {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
                {".xls", "application/vnd.ms-excel"},
                {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
                {".exe", "application/octet-stream"}, {".gif", "image/gif"}, {".gtar", "application/x-gtar"},
                {".gz", "application/x-gzip"}, {".h", "text/plain"}, {".htm", "text/html"},
                {".html", "text/html"}, {".jar", "application/java-archive"}, {".java", "text/plain"},
                {".jpeg", "image/jpeg"}, {".jpg", "image/jpeg"}, {".js", "application/x-javascript"},
                {".log", "text/plain"}, {".m3u", "audio/x-mpegurl"}, {".m4a", "audio/mp4a-latm"},
                {".m4b", "audio/mp4a-latm"}, {".m4p", "audio/mp4a-latm"}, {".m4u", "video/vnd.mpegurl"},
                {".m4v", "video/x-m4v"}, {".mov", "video/quicktime"}, {".mp2", "audio/x-mpeg"},
                {".mp3", "audio/x-mpeg"}, {".mp4", "video/mp4"}, {".mpc", "application/vnd.mpohun.certificate"},
                {".mpe", "video/mpeg"}, {".mpeg", "video/mpeg"}, {".mpg", "video/mpeg"}, {".mpg4", "video/mp4"},
                {".mpga", "audio/mpeg"}, {".msg", "application/vnd.ms-outlook"}, {".ogg", "audio/ogg"},
                {".pdf", "application/pdf"}, {".png", "image/png"}, {".pps", "application/vnd.ms-powerpoint"},
                {".ppt", "application/vnd.ms-powerpoint"},
                {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
                {".prop", "text/plain"}, {".rc", "text/plain"}, {".rmvb", "audio/x-pn-realaudio"},
                {".rtf", "application/rtf"}, {".sh", "text/plain"}, {".tar", "application/x-tar"},
                {".tgz", "application/x-compressed"}, {".txt", "text/plain"}, {".wav", "audio/x-wav"},
                {".wma", "audio/x-ms-wma"}, {".wmv", "audio/x-ms-wmv"}, {".wps", "application/vnd.ms-works"},
                {".xml", "text/plain"}, {".z", "application/x-compress"},
                {".zip", "application/x-zip-compressed"}, {"", "*/*"}};
        for (String[] MIME : MIMEMap) {
            if (fileSuffix.equals(MIME[0])) {
                return MIME[1];
            }
        }
        return "*/*";
    }

    /**
     * 打开文件
     *
     * @param filePath 文件路径
     */
    public static void openFile(Activity activity, String filePath) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(filePath)), getMIMEType(getFileSuffix(filePath, true)));
        activity.startActivity(intent);
    }

    private static StatFs getStatFs() {
        if (isSDCardEnable()) {
            return new StatFs(getSDCardPath());
        }
        return null;
    }

    /**
     * 获取SD卡总空间
     */
    public static long getSDCardTotalSpace() {
        StatFs statFs = getStatFs();
        if (statFs != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                return statFs.getBlockSizeLong() * statFs.getBlockCountLong();
            } else {
                return statFs.getBlockSize() * statFs.getBlockCount();
            }
        }
        return 0L;
    }

    /**
     * 获取SD卡剩余空间
     */
    public static long getSDCardAvailSpace() {
        StatFs statFs = getStatFs();
        if (statFs != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                return statFs.getBlockSizeLong() * statFs.getAvailableBlocksLong();
            } else {
                return statFs.getBlockSize() * statFs.getAvailableBlocks();
            }
        }
        return 0L;
    }

    /**
     * 检查磁盘空间是否大于100mb
     *
     * @return true 大于
     */
    public static boolean isDiskAvailable() {
        long size = getSDCardAvailSpace();
        return size > 100 * 1024 * 1024; // > 100bm
    }

    /**
     * 获取文件大小
     */
    public static long getFileSize(File file) {
        long dirSize = 0L;
        if (file != null && isFileExists(file)) {
            if (file.isFile()) {
                dirSize = file.length();
            } else {
                File[] files = file.listFiles();
                if (files != null) { // 文件夹被删除时, 子文件正在被写入, 文件属性异常返回null.
                    for (File f : files) {
                        dirSize += getFileSize(f);
                    }
                }
            }
        }
        return dirSize;
    }

    /**
     * 获取文件夹下文件总个数
     */
    public long getFileCount(File file) {
        int count = 0;
        if (file != null && isFileExists(file)) {
            if (file.isFile()) {
                count = 1;
            } else {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File f : files) {
                        count += getFileCount(f);
                    }
                }
            }
        }
        return count;
    }

    /**
     * 格式化文件大小
     *
     * @param fileSize 文件大小
     */
    public static String formatFileSize(long fileSize) {
        DecimalFormat df = new DecimalFormat("##.##");
        String fileSizeString;
        if (fileSize < 1024L) {
            fileSizeString = df.format((double) fileSize) + "B";
        } else if (fileSize < 1048576L) {
            fileSizeString = df.format((double) fileSize / 1024.0D) + "KB";
        } else if (fileSize < 1073741824L) {
            fileSizeString = df.format((double) fileSize / 1048576.0D) + "MB";
        } else {
            fileSizeString = df.format((double) fileSize / 1.073741824E9D) + "G";
        }
        return fileSizeString;
    }

    /**
     * 复制文件
     *
     * @param sourceFilePath 源路径
     * @param destFilePath   目标路径
     */
    public static boolean copyFile(String sourceFilePath, String destFilePath) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(sourceFilePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFoundException occurred. ", e);
        }
        return writeFile(destFilePath, inputStream);
    }

    /**
     * 文件拷贝
     *
     * @param src source 源文件
     * @param dst destination 目标文件
     */
    public static void copyFile(File src, File dst) throws IOException {
        FileInputStream in = new FileInputStream(src);
        FileOutputStream out = new FileOutputStream(dst);
        FileChannel inChannel = in.getChannel();
        FileChannel outChannel = out.getChannel();

        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null) {
                inChannel.close();
            }
            outChannel.close();
        }
        in.close();
        out.close();
    }

    /**
     * 移动文件
     *
     * @param sourceFilePath 源路径
     * @param destFilePath   目标路径
     */
    public static void moveFile(String sourceFilePath, String destFilePath) {
        if (TextUtils.isEmpty(sourceFilePath) || TextUtils.isEmpty(destFilePath)) {
            throw new RuntimeException("Both sourceFilePath and destFilePath cannot be null.");
        }
        moveFile(new File(sourceFilePath), new File(destFilePath));
    }

    /**
     * 移动文件
     *
     * @param srcFile  源文件
     * @param destFile 目标文件
     */
    public static void moveFile(File srcFile, File destFile) {
        boolean rename = srcFile.renameTo(destFile);
        if (!rename) {
            copyFile(srcFile.getAbsolutePath(), destFile.getAbsolutePath());
            deleteFile(srcFile.getAbsolutePath());
        }
    }

    /**
     * 删除文件或目录
     */
    public static boolean deleteFile(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            SecurityManager se = new SecurityManager();
            se.checkDelete(filePath);
            return deleteFile(new File(filePath));
        }
        return false;
    }

    /**
     * 删除文件或目录
     */
    public static boolean deleteFile(File file) {
        if (!file.exists()) {
            return true;
        } else if (file.isFile()) {
            return file.delete();
        } else {
            File[] files = file.listFiles();
            for (File f : files) {
                deleteFile(f);
            }
            return file.delete();
        }
    }

    /**
     * 将字符串写入文件
     *
     * @param filePath 文件路径
     * @param content  字符串内容
     * @param append   设置为true,字节将被写入到文件的结尾,而不是开始
     */
    public static boolean writeFile(String filePath, String content, boolean append) {
        if (!TextUtils.isEmpty(content)) {
            FileWriter fileWriter = null;
            try {
                createFile(getFolderName(filePath), getFileName(filePath, true));
                fileWriter = new FileWriter(filePath, append);
                fileWriter.write(content);
                return true;
            } catch (IOException e) {
                throw new RuntimeException("IOException occurred. ", e);
            } finally {
                if (fileWriter != null) {
                    try {
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }

    /**
     * 将字符串列表写入文件
     *
     * @param filePath    文件路径
     * @param contentList 字符串列表
     * @param append      设置为true,字节将被写入到文件的结尾,而不是开始
     */
    public static boolean writeFile(String filePath, List<String> contentList, boolean append) {
        if (contentList != null && contentList.size() > 0) {
            FileWriter fileWriter = null;
            try {
                createFile(getFolderName(filePath), getFileName(filePath, true));
                fileWriter = new FileWriter(filePath, append);
                int i = 0;
                for (String line : contentList) {
                    if (i++ > 0) {
                        fileWriter.write("\r\n");
                    }
                    fileWriter.write(line);
                }
                return true;
            } catch (IOException e) {
                throw new RuntimeException("IOException occurred. ", e);
            } finally {
                if (fileWriter != null) {
                    try {
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }

    /**
     * 将输入流写入文件
     *
     * @param filePath 文件路径
     * @param stream   输入流
     */
    public static boolean writeFile(String filePath, InputStream stream) {
        return writeFile(filePath, stream, false);
    }

    /**
     * 将输入流写入文件
     *
     * @param filePath 文件路径
     * @param stream   输入流
     * @param append   设置为true,字节将被写入到文件的结尾,而不是开始
     */
    public static boolean writeFile(String filePath, InputStream stream, boolean append) {
        return writeFile(filePath != null ? new File(filePath) : null, stream, append);
    }

    /**
     * 将输入流写入文件
     *
     * @param file   文件对象
     * @param stream 输入流
     * @param append 设置为true,字节将被写入到文件的结尾,而不是开始
     */
    public static boolean writeFile(File file, InputStream stream, boolean append) {
        OutputStream o = null;
        try {
            createFile(getFolderName(file.getAbsolutePath()), getFileName(file.getAbsolutePath(), true));
            o = new FileOutputStream(file, append);
            byte data[] = new byte[1024];
            int length;
            while ((length = stream.read(data)) != -1) {
                o.write(data, 0, length);
            }
            o.flush();
            return true;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFoundException occurred. ", e);
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (o != null) {
                try {
                    o.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 读取文件
     *
     * @param filePath    文件路径
     * @param charsetName The name of a supported
     *                    {@link java.nio.charset.Charset </code>charset<code>}
     */
    public static StringBuilder readFile(String filePath, String charsetName) {
        File file = new File(filePath);
        StringBuilder fileContent = new StringBuilder("");
        if (!file.isFile()) {
            return null;
        }

        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
            reader = new BufferedReader(is);
            String line;
            while ((line = reader.readLine()) != null) {
                if (!fileContent.toString().equals("")) {
                    fileContent.append("\r\n");
                }
                fileContent.append(line);
            }
            return fileContent;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 读取文件到字符串列表，列表中的元素是一行
     *
     * @param filePath    文件路径
     * @param charsetName The name of a supported
     *                    {@link java.nio.charset.Charset </code>charset<code>}
     */
    public static List<String> readFileToList(String filePath, String charsetName) {
        File file = new File(filePath);
        List<String> fileContent = new ArrayList<String>();
        if (!file.isFile()) {
            return null;
        }

        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
            reader = new BufferedReader(is);
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.add(line);
            }
            return fileContent;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 从输入流读取数据
     *
     * @param inStream 输入流
     */
    public static byte[] readInputStream(InputStream inStream) throws IOException {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);
        }
        outSteam.close();
        inStream.close();
        return outSteam.toByteArray();
    }

    /**
     * 从输入流读取数据
     *
     * @param inStream 输入流
     */
    public static String inputStream2String(InputStream inStream) throws IOException {
        return new String(readInputStream(inStream), "UTF-8");
    }

    /**
     * 对文件设置root权限
     *
     * @param filePath 文件路径
     */
    public static boolean upgradeRootPermission(String filePath) {
        Process process = null;
        DataOutputStream os = null;
        try {
            String cmd = "chmod 777 " + filePath;
            process = Runtime.getRuntime().exec("su"); // 切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }
}
