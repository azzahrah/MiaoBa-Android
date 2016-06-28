package cn.nodemedia.library.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.nodemedia.library.R;
import cn.nodemedia.library.utils.ScreenUtils;

import java.util.List;

/**
 * 搜索界面热门词汇
 * Created by Bining.
 */
public class SearchTipsView extends LinearLayout {

    private Context context;
    public static final int SEARCH_TEXT = 1;// 1.搜索热词
    public static final int KIND_TEXT = 2;//  2.商品规格

    private OnItemClickListener onItemClickListener;

    public SearchTipsView(Context context) {
        super(context);
        this.context = context;
        setOrientation(VERTICAL);//设置方向
    }

    public SearchTipsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setOrientation(VERTICAL);//设置方向
    }

    public SearchTipsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        setOrientation(VERTICAL);//设置方向
    }

    /**
     * 外部接口调用
     *
     * @param type 按钮类型 1.搜索热词 2.商品规格
     */
    public void initViews(String items[], int type) {
        removeAllViews();
        int length = 0;//一行加载item 的宽度
        LinearLayout layout = null;
        LayoutParams layoutLp = null;
        boolean isNewLine = true;//是否换行
        int screenWidth = ScreenUtils.getScreenWidth() - ScreenUtils.dp2px(16) * 2;//屏幕的宽度
        int size = items.length;
        for (int i = 0; i < size; i++) {
            if (isNewLine) {//是否开启新的一行
                layout = new LinearLayout(context);
                layout.setOrientation(HORIZONTAL);
                layoutLp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutLp.topMargin = ScreenUtils.dp2px(8);
            }

            TextView itemView = null;
            if (type == SEARCH_TEXT) {
                itemView = addHotText(i, items[i]);
            } else if (type == KIND_TEXT) {
                itemView = addKindText(i, items[i]);
            }
            //设置item的参数
            LayoutParams itemLp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (isNewLine) {
                itemLp.leftMargin = 0;
            } else {
                itemLp.leftMargin = ScreenUtils.dp2px(14);
            }
            //得到当前行的长度
            length += itemLp.leftMargin + getViewWidth(itemView);
            if (length > screenWidth) {//当前行的长度大于屏幕宽度则换行
                length = 0;
                addView(layout, layoutLp);
                isNewLine = true;
                i--;
            } else {//否则添加到当前行
                isNewLine = false;
                layout.addView(itemView, itemLp);
            }
        }
        addView(layout, layoutLp);
    }

    public void initViews(List<String> items, int type) {
        initViews((String[]) items.toArray(), type);
    }

    private TextView addHotText(int position, String key) {
        TextView mTextView = new TextView(context);
        mTextView.setText(key);
        mTextView.setTextSize(14);
        mTextView.setTextColor(Color.parseColor("#4A4A4A"));
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setBackgroundResource(R.drawable.search_hot_background);
        mTextView.setTag(position);
        mTextView.setPadding(ScreenUtils.dp2px(8), 0, ScreenUtils.dp2px(8), 0);
        mTextView.setOnClickListener(new OnClickListener() {//给每个item设置点击事件
            @Override
            public void onClick(View v) {
                if (null != onItemClickListener) {
                    onItemClickListener.onItemClick(v, (int) v.getTag());
                }
            }
        });
        return mTextView;
    }

    private TextView addKindText(int position, String key) {
        TextView mTextView = new TextView(context);
        mTextView.setText(key);
        mTextView.setTextSize(14);
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setTag(position);
        mTextView.setPadding(ScreenUtils.dp2px(8), ScreenUtils.dp2px(6), ScreenUtils.dp2px(8), ScreenUtils.dp2px(6));
        mTextView.setTextColor(Color.parseColor("#4A4A4A"));
        mTextView.setBackgroundResource(R.drawable.kind_gray_background);
        mTextView.setOnClickListener(new OnClickListener() {//给每个item设置点击事件
            @Override
            public void onClick(View v) {
                if (null != onItemClickListener) {
                    onItemClickListener.onItemClick(v, (int) v.getTag());
                }
            }
        });
        return mTextView;
    }

    /**
     * 得到view控件的宽度
     */
    private int getViewWidth(View view) {
        int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return view.getMeasuredWidth();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}

