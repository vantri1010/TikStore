package im.bclpbkiauv.ui.components;

import android.text.TextPaint;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.TextStyleSpan;

public class URLSpanBotCommand extends URLSpanNoUnderline {
    public static boolean enabled = true;
    public int currentType;
    private TextStyleSpan.TextStyleRun style;

    public URLSpanBotCommand(String url, int type) {
        this(url, type, (TextStyleSpan.TextStyleRun) null);
    }

    public URLSpanBotCommand(String url, int type, TextStyleSpan.TextStyleRun run) {
        super(url);
        this.currentType = type;
        this.style = run;
    }

    public void updateDrawState(TextPaint p) {
        super.updateDrawState(p);
        int i = this.currentType;
        if (i == 2) {
            p.setColor(-1);
        } else if (i == 1) {
            p.setColor(Theme.getColor(enabled ? Theme.key_chat_messageLinkOut : Theme.key_chat_messageTextOut));
        } else {
            p.setColor(Theme.getColor(enabled ? Theme.key_chat_messageLinkIn : Theme.key_chat_messageTextIn));
        }
        TextStyleSpan.TextStyleRun textStyleRun = this.style;
        if (textStyleRun != null) {
            textStyleRun.applyStyle(p);
        } else {
            p.setUnderlineText(false);
        }
    }
}
