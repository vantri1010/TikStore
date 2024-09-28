package im.bclpbkiauv.ui.components;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.Emoji;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.Theme;
import java.util.ArrayList;

public class BotKeyboardView extends LinearLayout {
    private TLRPC.TL_replyKeyboardMarkup botButtons;
    private int buttonHeight;
    private ArrayList<TextView> buttonViews = new ArrayList<>();
    private LinearLayout container;
    /* access modifiers changed from: private */
    public BotKeyboardViewDelegate delegate;
    private boolean isFullSize;
    private int panelHeight;
    private ScrollView scrollView;

    public interface BotKeyboardViewDelegate {
        void didPressedButton(TLRPC.KeyboardButton keyboardButton);
    }

    public BotKeyboardView(Context context) {
        super(context);
        setOrientation(1);
        ScrollView scrollView2 = new ScrollView(context);
        this.scrollView = scrollView2;
        addView(scrollView2);
        LinearLayout linearLayout = new LinearLayout(context);
        this.container = linearLayout;
        linearLayout.setOrientation(1);
        this.scrollView.addView(this.container);
        AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor(Theme.key_chat_emojiPanelBackground));
        setBackgroundColor(Theme.getColor(Theme.key_chat_emojiPanelBackground));
    }

    public void setDelegate(BotKeyboardViewDelegate botKeyboardViewDelegate) {
        this.delegate = botKeyboardViewDelegate;
    }

    public void setPanelHeight(int height) {
        TLRPC.TL_replyKeyboardMarkup tL_replyKeyboardMarkup;
        this.panelHeight = height;
        if (this.isFullSize && (tL_replyKeyboardMarkup = this.botButtons) != null && tL_replyKeyboardMarkup.rows.size() != 0) {
            this.buttonHeight = !this.isFullSize ? 42 : (int) Math.max(42.0f, ((float) (((this.panelHeight - AndroidUtilities.dp(30.0f)) - ((this.botButtons.rows.size() - 1) * AndroidUtilities.dp(10.0f))) / this.botButtons.rows.size())) / AndroidUtilities.density);
            int count = this.container.getChildCount();
            int newHeight = AndroidUtilities.dp((float) this.buttonHeight);
            for (int a = 0; a < count; a++) {
                View v = this.container.getChildAt(a);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) v.getLayoutParams();
                if (layoutParams.height != newHeight) {
                    layoutParams.height = newHeight;
                    v.setLayoutParams(layoutParams);
                }
            }
        }
    }

    public void invalidateViews() {
        for (int a = 0; a < this.buttonViews.size(); a++) {
            this.buttonViews.get(a).invalidate();
        }
    }

    public boolean isFullSize() {
        return this.isFullSize;
    }

    public void setButtons(TLRPC.TL_replyKeyboardMarkup buttons) {
        TLRPC.TL_replyKeyboardMarkup tL_replyKeyboardMarkup = buttons;
        this.botButtons = tL_replyKeyboardMarkup;
        this.container.removeAllViews();
        this.buttonViews.clear();
        boolean z = false;
        this.scrollView.scrollTo(0, 0);
        if (tL_replyKeyboardMarkup != null && this.botButtons.rows.size() != 0) {
            boolean z2 = !tL_replyKeyboardMarkup.resize;
            this.isFullSize = z2;
            this.buttonHeight = !z2 ? 42 : (int) Math.max(42.0f, ((float) (((this.panelHeight - AndroidUtilities.dp(30.0f)) - ((this.botButtons.rows.size() - 1) * AndroidUtilities.dp(10.0f))) / this.botButtons.rows.size())) / AndroidUtilities.density);
            int a = 0;
            while (a < tL_replyKeyboardMarkup.rows.size()) {
                TLRPC.TL_keyboardButtonRow row = (TLRPC.TL_keyboardButtonRow) tL_replyKeyboardMarkup.rows.get(a);
                LinearLayout layout = new LinearLayout(getContext());
                layout.setOrientation(z ? 1 : 0);
                this.container.addView(layout, LayoutHelper.createLinear(-1, this.buttonHeight, 15.0f, a == 0 ? 15.0f : 10.0f, 15.0f, a == tL_replyKeyboardMarkup.rows.size() - 1 ? 15.0f : 0.0f));
                float weight = 1.0f / ((float) row.buttons.size());
                int b = 0;
                while (b < row.buttons.size()) {
                    TLRPC.KeyboardButton button = row.buttons.get(b);
                    TextView textView = new TextView(getContext());
                    textView.setTag(button);
                    textView.setTextColor(Theme.getColor(Theme.key_chat_botKeyboardButtonText));
                    textView.setTextSize(1, 16.0f);
                    textView.setGravity(17);
                    textView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable((float) AndroidUtilities.dp(4.0f), Theme.getColor(Theme.key_chat_botKeyboardButtonBackground), Theme.getColor(Theme.key_chat_botKeyboardButtonBackgroundPressed)));
                    textView.setPadding(AndroidUtilities.dp(4.0f), z ? 1 : 0, AndroidUtilities.dp(4.0f), z);
                    textView.setText(Emoji.replaceEmoji(button.text, textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), z));
                    TextView textView2 = textView;
                    TLRPC.KeyboardButton keyboardButton = button;
                    layout.addView(textView2, LayoutHelper.createLinear(0, -1, weight, 0, 0, b != row.buttons.size() - 1 ? 10 : 0, 0));
                    textView2.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            BotKeyboardView.this.delegate.didPressedButton((TLRPC.KeyboardButton) v.getTag());
                        }
                    });
                    this.buttonViews.add(textView2);
                    b++;
                    z = false;
                }
                a++;
                z = false;
            }
        }
    }

    public int getKeyboardHeight() {
        return this.isFullSize ? this.panelHeight : (this.botButtons.rows.size() * AndroidUtilities.dp((float) this.buttonHeight)) + AndroidUtilities.dp(30.0f) + ((this.botButtons.rows.size() - 1) * AndroidUtilities.dp(10.0f));
    }
}
