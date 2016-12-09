package tv.miaoba.live.bean;

import java.io.Serializable;

import xyz.tanwb.airship.db.annotation.Column;
import xyz.tanwb.airship.db.annotation.Table;

@Table(name = "UserInfo")
public class UserInfo implements Serializable {

    @Column(name = "userid", isId = true, autoGen = false)
    public int userid;
    @Column(name = "username")
    public String username;//用户登陆帐号
    @Column(name = "nickname")
    public String nickname;//用户昵称
    @Column(name = "faces")
    public String faces;// 用户头像
    @Column(name = "sex")
    public int sex;//性别 1：男 2：女
    @Column(name = "grade")
    public int grade;//用户等级
    @Column(name = "autograph")
    public String autograph;//签名
    @Column(name = "follow")
    public int follow;//关注数量
    @Column(name = "fans")
    public int fans;//粉丝数量
    @Column(name = "point")
    public int point;//虚拟票数
    @Column(name = "experience")
    public int experience;//经验值
    @Column(name = "balance")
    public double balance;//账户余额
    @Column(name = "used")
    public double used;//使用的金额
    @Column(name = "phone")
    public String phone;//绑定手机号
    @Column(name = "images")
    public String images;// 直播形象照
    @Column(name = "cityid")
    public String cityid;// 家乡
    @Column(name = "birthday")
    public String birthday;//生日
    @Column(name = "emotion")
    public String emotion;//情感
    @Column(name = "occupation")
    public String occupation;//职业
    @Column(name = "age")
    public String age;//年龄描述 18，天蝎座

}
