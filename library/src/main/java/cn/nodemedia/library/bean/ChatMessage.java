package cn.nodemedia.library.bean;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * MQTT消息载体
 */
public class ChatMessage implements Serializable {

    public int id;
    public int m1;//消息ID  msg_id	可空
    public int m2;//消息类型  msg_type "必填 0:定制消息 1:文本 2:图片 3:语音 4:视频"
    public String m3;//消息内容  msg_content "必填,不允许空消息。如果消息类型为文本，则为消息文本，否则为对应的内容链接。"
    public String m4;//来源用户  from_open_id 必填
    public String m5;//目标用户  to_open_id "可空 点对点消息时设置"
    public String m6;//目标群  to_group_id "可空 群聊消息时设置"
    public long m7;//发送时间
    public int m8;//消息类别  msg_kind 必填 1 聊天消息  2 添加好友申请  3 加入企业申请  4 系统消息 5 通知消息 6 订单消息  7 支付通知  8 支付请求
    public String m9;//消息URL  msg_url 可空 当msg_type为定制消息时，点击消息打开的URL
    public String m10;//客户或客服OpenId
    public String m11;//企业OpenId

    public String topic;//消息主题
    public int state;//消息状态 【聊天消息 0:已发送(已读) 1:发送中 2:发送失败(未读) 3:图片上传失败 4：等待上传】【请求消息 0:待处理 1:已同意 2:已忽略 3:已拒绝】

    public String getMessageJSON() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("m2", m2);
        paramMap.put("m3", m3);
        paramMap.put("m4", m4);
        paramMap.put("m5", m5);
        paramMap.put("m6", m6);
        paramMap.put("m7", m7);
        paramMap.put("m8", m8);
        paramMap.put("m9", m9);
        paramMap.put("m10", m10);
        paramMap.put("m11", m11);
        return JSON.toJSONString(paramMap);
    }
}
