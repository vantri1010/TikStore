package im.bclpbkiauv.ui.actionbar;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class ChatActionBarMenuPopupWindow extends PopupWindow {
    private Context context;
    private FrameLayout frameLayout;
    private LinearLayout llContent;
    private OnSubItemClickListener mOnSubItemClickListener;

    public interface OnSubItemClickListener {
        void onClick(int i);
    }

    public void setOnSubItemClickListener(OnSubItemClickListener listener) {
        this.mOnSubItemClickListener = listener;
    }

    public ChatActionBarMenuPopupWindow(Context context2) {
        super(-1, AndroidUtilities.dp(50.0f));
        this.context = context2;
        init();
    }

    private void init() {
        LinearLayout linearLayout = new LinearLayout(this.context);
        this.llContent = linearLayout;
        linearLayout.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefault));
        this.llContent.setOrientation(0);
        this.llContent.setGravity(16);
        FrameLayout frameLayout2 = new FrameLayout(this.context);
        this.frameLayout = frameLayout2;
        this.llContent.addView(frameLayout2, LayoutHelper.createLinear(0, -1, 1.0f));
        this.frameLayout.setVisibility(8);
        setContentView(this.llContent);
    }

    public ChatActionBarMenuSubItem addSubItem(int id, int icon, String text) {
        ChatActionBarMenuSubItem chatActionBarMenuSubItem = new ChatActionBarMenuSubItem(this.context);
        chatActionBarMenuSubItem.setTag(Integer.valueOf(id));
        chatActionBarMenuSubItem.setTextAndIcon(text, icon);
        chatActionBarMenuSubItem.setOnClickListener(new View.OnClickListener(id) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                ChatActionBarMenuPopupWindow.this.lambda$addSubItem$0$ChatActionBarMenuPopupWindow(this.f$1, view);
            }
        });
        this.llContent.addView(chatActionBarMenuSubItem, LayoutHelper.createLinear(0, -1, 1.0f));
        return chatActionBarMenuSubItem;
    }

    public /* synthetic */ void lambda$addSubItem$0$ChatActionBarMenuPopupWindow(int id, View v) {
        OnSubItemClickListener onSubItemClickListener = this.mOnSubItemClickListener;
        if (onSubItemClickListener != null) {
            onSubItemClickListener.onClick(id);
        }
    }

    public ChatActionBarMenuSubItem addLiveSubItem(int id, int icon, String text) {
        ChatActionBarMenuSubItem chatActionBarMenuSubItem = new ChatActionBarMenuSubItem(this.context);
        chatActionBarMenuSubItem.setTag(Integer.valueOf(id));
        chatActionBarMenuSubItem.setTextAndIcon(text, icon);
        chatActionBarMenuSubItem.setOnClickListener(new View.OnClickListener(id) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                ChatActionBarMenuPopupWindow.this.lambda$addLiveSubItem$1$ChatActionBarMenuPopupWindow(this.f$1, view);
            }
        });
        this.frameLayout.setVisibility(0);
        this.frameLayout.addView(chatActionBarMenuSubItem, LayoutHelper.createFrame(-1, -1.0f));
        return chatActionBarMenuSubItem;
    }

    public /* synthetic */ void lambda$addLiveSubItem$1$ChatActionBarMenuPopupWindow(int id, View v) {
        OnSubItemClickListener onSubItemClickListener = this.mOnSubItemClickListener;
        if (onSubItemClickListener != null) {
            onSubItemClickListener.onClick(id);
        }
    }

    public void hideLiveSubItem() {
        this.frameLayout.setVisibility(8);
    }

    public void hideSubItem(int id) {
        View view = this.llContent.findViewWithTag(Integer.valueOf(id));
        if (view != null && view.getVisibility() != 8) {
            view.setVisibility(8);
        }
    }

    public int getItemCount() {
        return this.llContent.getChildCount();
    }
}
