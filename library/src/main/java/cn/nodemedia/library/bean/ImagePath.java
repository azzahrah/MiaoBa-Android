package cn.nodemedia.library.bean;

import java.io.Serializable;

/**
 * 图片信息
 * Created by Bining.
 */
public class ImagePath implements Serializable {

    public String imagePath;// 图片地址
    public String imagePrefix;// 图片地址前缀
    public String imageSuffix;// 图片地址后缀
    public String imageLocalPath;// 本地路径
    public boolean imageEmpty;// 是否为空图片
    public int imageWidth;// 宽度
    public int imageHeight;// 高度
}



