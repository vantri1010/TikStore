package im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.span;

import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.listener.SpanUrlCallBack;

public class LinkSpan extends ClickableSpan {
    private int color;
    private SpanUrlCallBack spanUrlCallBack;
    private String url;

    public LinkSpan(String url2, int color2, SpanUrlCallBack spanUrlCallBack2) {
        this.url = url2;
        this.spanUrlCallBack = spanUrlCallBack2;
        this.color = color2;
    }

    public void onClick(View widget) {
        if ((!this.url.contains("tel:") || !TextUtils.isDigitsOnly(this.url.replace("tel:", ""))) && !TextUtils.isDigitsOnly(this.url)) {
            SpanUrlCallBack spanUrlCallBack2 = this.spanUrlCallBack;
            if (spanUrlCallBack2 != null) {
                spanUrlCallBack2.url(widget, this.url);
                return;
            }
            return;
        }
        SpanUrlCallBack spanUrlCallBack3 = this.spanUrlCallBack;
        if (spanUrlCallBack3 != null) {
            spanUrlCallBack3.phone(widget, this.url);
        }
    }

    public void updateDrawState(TextPaint ds) {
        ds.setColor(this.color);
        ds.setUnderlineText(false);
    }
}
