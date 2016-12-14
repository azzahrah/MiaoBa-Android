package tv.miaoba.imlib.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import io.rong.imlib.model.MessageContent;
import tv.miaoba.imlib.EmojiManager;
import tv.miaoba.imlib.R;

public class GiftMsgView extends BaseMsgView {

    private TextView username;
    private TextView content;

    public GiftMsgView(Context context) {
        super(context);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.msg_gift_view, this);
        username = (TextView) view.findViewById(R.id.username);
        content = (TextView) view.findViewById(R.id.content);
    }

    @Override
    public void setContent(MessageContent msgContent) {
        GiftMessage msg = (GiftMessage) msgContent;
        username.setText(msg.getUserInfo().getName() + " ");
        content.setText(EmojiManager.parse(msg.getContent(), content.getTextSize()));
    }
}