package cn.nodemedia.library.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 图片信息
 */
public class UploadInfo implements Serializable {

    public boolean success;
    public String message;
    public List<FileInfo> returnTargets;
    // {"message":"file upload success","returnTargets":[{"fileId":10009,"fileName":"user/20150715/10009.jpg","fileRawName":"1436946896225.jpg","rawHeight":0,"rawWidth":0,"result":null,"scaleHeight":0,"scaleWidth":0,"upLoadedFilePath":""}],"success":true}
}
