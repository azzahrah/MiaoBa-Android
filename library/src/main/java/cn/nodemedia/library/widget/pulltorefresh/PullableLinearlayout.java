package cn.nodemedia.library.widget.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ListView;

public class PullableLinearlayout extends LinearLayout implements Pullable {

    public PullableLinearlayout(Context context) {
        super(context);
    }

    public PullableLinearlayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullableLinearlayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean canPullDown() {
        boolean isCanPull = true;
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof ListView) {
                ListView listView = (ListView) getChildAt(i);
                if (listView.getChildAt(0) == null) continue;
                if (listView.getFirstVisiblePosition() != 0 || listView.getChildAt(0).getTop() < 0) {
                    isCanPull = false;
                }
            }
        }
        return isCanPull;
    }

    @Override
    public boolean canPullUp() {
        return false;
    }

}
