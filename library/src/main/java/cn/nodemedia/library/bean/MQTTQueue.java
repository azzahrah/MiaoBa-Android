package cn.nodemedia.library.bean;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.Serializable;

/**
 * MQTT消息队列
 */
public class MQTTQueue implements Serializable {

    public MQTTQueue(String topic, MqttMessage message) {
        this.topic = topic;
        this.message = message;
    }

    public String topic;//消息主题
    public MqttMessage message;//消息内容
}
