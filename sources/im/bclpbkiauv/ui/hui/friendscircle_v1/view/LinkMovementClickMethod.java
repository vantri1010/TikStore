package im.bclpbkiauv.ui.hui.friendscircle_v1.view;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.widget.TextView;

public class LinkMovementClickMethod extends LinkMovementMethod {
    private static final long CLICK_DELAY = 500;
    private static LinkMovementClickMethod sInstance;
    private long lastClickTime;

    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        Spannable spannable = buffer;
        int action = event.getAction();
        if (action == 1 || action == 0) {
            int x = ((int) event.getX()) - widget.getTotalPaddingLeft();
            int y = ((int) event.getY()) - widget.getTotalPaddingTop();
            int x2 = x + widget.getScrollX();
            int y2 = y + widget.getScrollY();
            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y2);
            int off = layout.getOffsetForHorizontal(line, (float) x2);
            ClickableSpan[] link = (ClickableSpan[]) spannable.getSpans(off, off, ClickableSpan.class);
            if (link.length == 0) {
                Selection.removeSelection(buffer);
            } else if (action == 1) {
                if (System.currentTimeMillis() - this.lastClickTime < CLICK_DELAY) {
                    if (((float) x2) <= widget.getPaint().measureText(widget.getText().subSequence(layout.getLineStart(line), layout.getLineEnd(line)).toString())) {
                        link[0].onClick(widget);
                        return true;
                    }
                    TextView textView = widget;
                    return true;
                }
                TextView textView2 = widget;
                return true;
            } else if (action != 0) {
                return true;
            } else {
                Selection.setSelection(spannable, spannable.getSpanStart(link[0]), spannable.getSpanEnd(link[0]));
                this.lastClickTime = System.currentTimeMillis();
                return true;
            }
        }
        return super.onTouchEvent(widget, buffer, event);
    }

    public static LinkMovementClickMethod getInstance() {
        if (sInstance == null) {
            sInstance = new LinkMovementClickMethod();
        }
        return sInstance;
    }
}
