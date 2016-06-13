package cn.nodemedia.library.bean;

import java.io.Serializable;
import java.util.Map;

/**
 * 文件信息
 * Created by Bining.
 */
public class FileInfo implements Serializable {

    public long fileId; // 文件ID 如:10027
    public long fileLength; // 文件长度 如:178489
    public String fileName; // 文件名 如:apps/20160421/10027.jpg
    public String fileRawName; //	原始名称 如:IMG_1461215326113.jpg
    public String fileType; // 文件类型 如:jpg
    public int rawWidth; // 原始文件宽度 如:1124
    public int rawHeight; // 原始文件高度 如:1124
    public int scaleWidth; // 目标裁剪宽度 如:1124
    public int scaleHeight; // 目标裁剪高度 如:1124
    public Map<String, Object> result; // 待定 如:null

}
