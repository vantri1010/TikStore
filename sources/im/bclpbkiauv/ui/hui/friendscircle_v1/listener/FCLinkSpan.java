package im.bclpbkiauv.ui.hui.friendscircle_v1.listener;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import im.bclpbkiauv.messenger.browser.Browser;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.listener.SpanUrlCallBack;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.span.LinkSpan;

public class FCLinkSpan extends LinkSpan {
    private Context context;
    private String url;

    public FCLinkSpan(Context context2, String url2, int color, SpanUrlCallBack spanUrlCallBack) {
        super(url2, color, spanUrlCallBack);
        this.context = context2;
        this.url = url2;
    }

    public void onClick(View view) {
        super.onClick(view);
        if (view instanceof TextView) {
            ((TextView) view).setHighlightColor(0);
        }
        if (!TextUtils.isEmpty(this.url) && this.context != null) {
            if ((this.url.contains("tel:") && TextUtils.isDigitsOnly(this.url.replace("tel:", ""))) || TextUtils.isDigitsOnly(this.url)) {
                this.context.startActivity(new Intent("android.intent.action.DIAL", Uri.parse(this.url)));
            } else if (Browser.isInternalUrl(this.url, (boolean[]) null)) {
                Browser.openUrl(this.context, this.url, true);
            } else {
                String realUrl = this.url;
                if (!realUrl.contains("://") && (!realUrl.startsWith("http://") || !realUrl.startsWith("https://"))) {
                    realUrl = "http://" + realUrl;
                }
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.setData(Uri.parse(realUrl));
                this.context.startActivity(intent);
            }
        }
    }

    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
    }
}
