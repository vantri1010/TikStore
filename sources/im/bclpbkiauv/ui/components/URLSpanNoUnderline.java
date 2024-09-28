package im.bclpbkiauv.ui.components;

import android.net.Uri;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;
import im.bclpbkiauv.messenger.browser.Browser;
import im.bclpbkiauv.ui.components.TextStyleSpan;

public class URLSpanNoUnderline extends URLSpan {
    private TextStyleSpan.TextStyleRun style;

    public URLSpanNoUnderline(String url) {
        this(url, (TextStyleSpan.TextStyleRun) null);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public URLSpanNoUnderline(String url, TextStyleSpan.TextStyleRun run) {
        super(url != null ? url.replace(8238, ' ') : url);
        this.style = run;
    }

    public void onClick(View widget) {
        String url = getURL();
        if (url.startsWith("@")) {
            Browser.openUrl(widget.getContext(), Uri.parse("https://m12345.com/" + url.substring(1)));
            return;
        }
        Browser.openUrl(widget.getContext(), url);
    }

    public void updateDrawState(TextPaint p) {
        super.updateDrawState(p);
        TextStyleSpan.TextStyleRun textStyleRun = this.style;
        if (textStyleRun != null) {
            textStyleRun.applyStyle(p);
        } else {
            p.setUnderlineText(false);
        }
    }
}
