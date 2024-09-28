package im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.span;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import com.bjz.comm.net.bean.FCEntitysResponse;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.listener.SpanAtUserCallBack;

public class ClickAtUserSpan extends ClickableSpan {
    private int color;

    public ClickAtUserSpan(FCEntitysResponse FCEntitysResponse, int color2, SpanAtUserCallBack spanClickCallBack) {
        this.color = color2;
    }

    public void onClick(View view) {
    }

    public void updateDrawState(TextPaint ds) {
        ds.setColor(this.color);
        ds.setUnderlineText(false);
    }
}
