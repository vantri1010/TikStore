package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class ChatUnreadCell extends FrameLayout {
    private TextView textView;

    public ChatUnreadCell(Context context) {
        this(context, (AttributeSet) null);
    }

    public ChatUnreadCell(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChatUnreadCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = View.inflate(context, R.layout.item_message_unread_layout, (ViewGroup) null);
        TextView textView2 = (TextView) view.findViewById(R.id.tv_msg_unread_text);
        this.textView = textView2;
        textView2.setTextSize(1, 14.0f);
        this.textView.setTextColor(Theme.getColor(Theme.key_chat_unreadMessagesStartText));
        addView(view, LayoutHelper.createFrame(-1, 20.0f));
    }

    public void setText(String text) {
        this.textView.setText(text);
    }

    public TextView getTextView() {
        return this.textView;
    }
}
