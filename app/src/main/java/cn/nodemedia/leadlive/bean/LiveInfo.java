package cn.nodemedia.leadlive.bean;

import java.io.Serializable;

public class LiveInfo implements Serializable {

    public int id;//直播标识ID
    public int liveid;//直播用户ID
    public int online;// 在线人数
    public String location;//定位
    public String title;//标题
    public String times;//时间
    public String nickname;//用户昵称
    public String faces;//头像
    public String userid;//用户ID（预留）
    public String images;//直播封面图
    public boolean is_follow;//是否关注

}
