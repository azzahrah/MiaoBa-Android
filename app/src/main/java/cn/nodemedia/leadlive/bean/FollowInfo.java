package cn.nodemedia.leadlive.bean;

import java.io.Serializable;

public class FollowInfo implements Serializable {

    public int id;
    public int userid;
    public String nickname;//用户昵称
    public String faces;// 用户头像
    public int sex;//性别 1：男 2：女
    public String autograph;//签名
    public int follow;//关注数量
    public int fans;//粉丝数量
    public int point;//虚拟票数
    public double used;//使用的金额
    public boolean is_follow;//是否关注

}
