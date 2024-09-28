package im.bclpbkiauv.ui.hviews.bottomalert;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class BottomAlert extends LinearLayout implements View.OnClickListener {
    private int[] itemIcons;
    private CharSequence[] items;
    private LinearLayout llOptionsContainer;
    private Context mContext;
    public BottomAlertDelegate onItemClickListener;
    private TextView tvBtmText;

    public interface BottomAlertDelegate {
        void onItemClick(int i);
    }

    public void build() {
        init();
        invalidate();
    }

    public void setOnItemClickListener(BottomAlertDelegate onItemClickListener2) {
        this.onItemClickListener = onItemClickListener2;
    }

    public BottomAlert(Context context, CharSequence[] items2) {
        super(context);
        this.mContext = context;
        this.items = items2;
        init();
    }

    public BottomAlert(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomAlert(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    public void setItems(CharSequence[] items2) {
        this.items = items2;
    }

    public void setItemIcons(int[] itemIcons2) {
        this.itemIcons = itemIcons2;
    }

    public void setTvBtmTextAttr(String text, int colorId) {
        if (text != null) {
            this.tvBtmText.setText(text);
        }
        if (colorId > 0) {
            this.tvBtmText.setTextColor(colorId);
        }
    }

    private void init() {
        this.llOptionsContainer = new LinearLayout(this.mContext);
        if (this.items != null) {
            int i = 0;
            while (true) {
                CharSequence[] charSequenceArr = this.items;
                if (i >= charSequenceArr.length) {
                    break;
                }
                CharSequence content = charSequenceArr[i];
                TextView item = new TextView(this.mContext);
                this.llOptionsContainer.addView(item, LayoutHelper.createLinear(-1, AndroidUtilities.dp(48.0f)));
                item.setGravity(17);
                item.setTag(Integer.valueOf(i));
                item.setText(content);
                item.setOnClickListener(this);
                if (i != item.length() - 1) {
                    View view = new View(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
                    this.llOptionsContainer.addView(view, LayoutHelper.createLinear(-1.0f, 0.5f));
                }
                i++;
            }
        }
        TextView textView = new TextView(this.mContext);
        this.tvBtmText = textView;
        textView.setGravity(17);
        this.llOptionsContainer.addView(this.tvBtmText, LayoutHelper.createLinear(-1, AndroidUtilities.dp(48.0f), 0.0f, 25.0f, 0.0f, 0.0f));
    }

    public void onClick(View v) {
        BottomAlertDelegate bottomAlertDelegate = this.onItemClickListener;
        if (bottomAlertDelegate != null) {
            bottomAlertDelegate.onItemClick(((Integer) v.getTag()).intValue());
        }
    }
}
