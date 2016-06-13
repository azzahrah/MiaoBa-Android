package cn.nodemedia.library.bean;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * RxBus事件数据载体
 * Created by Bining.
 */
public class EventBusInfo implements Serializable {

    private String cls;//主题名称
    private Object data;//消息内容

    public EventBusInfo(String cls) {
        this.cls = cls;
    }

    public EventBusInfo(String cls, Object data) {
        this.cls = cls;
        this.data = data;
    }

    public boolean equals(String cls) {
        return !TextUtils.isEmpty(this.cls) && this.cls.equals(cls);
    }

    public String getTitle() {
        return this.cls;
    }

    public Object getData() {
        return this.data;
    }
}
