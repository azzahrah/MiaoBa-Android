package io.rong.imlib.message;

import android.content.Context;
import android.view.LayoutInflater;

import io.rong.imlib.model.MessageContent;
import io.rong.imlib.R;

public class UnknownMsgView extends BaseMsgView {

    public UnknownMsgView(Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.msg_unknown_view, this);
    }

    @Override
    public void setContent(MessageContent msgContent) {
    }
}