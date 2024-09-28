package im.bclpbkiauv.ui.hui.friendscircle_v1.view;

import android.content.Context;
import android.text.Layout;
import android.text.Spannable;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import androidx.appcompat.widget.AppCompatTextView;

public class ControlClickSpanTextView extends AppCompatTextView {
    private long downTime;

    public ControlClickSpanTextView(Context context) {
        super(context);
    }

    public ControlClickSpanTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ControlClickSpanTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == 0) {
            this.downTime = System.currentTimeMillis();
        }
        if (action == 1) {
            long interval = System.currentTimeMillis() - this.downTime;
            int x = ((int) event.getX()) - getTotalPaddingLeft();
            int y = ((int) event.getY()) - getTotalPaddingTop();
            int x2 = x + getScrollX();
            int y2 = y + getScrollY();
            Layout layout = getLayout();
            int line = layout.getLineForVertical(y2);
            int off = layout.getOffsetForHorizontal(line, (float) x2);
            if (getText() instanceof Spannable) {
                ClickableSpan[] link = (ClickableSpan[]) ((Spannable) getText()).getSpans(off, off, ClickableSpan.class);
                if (link.length == 0) {
                } else if (interval < ((long) ViewConfiguration.getLongPressTimeout())) {
                    int i = action;
                    if (((float) x2) <= getPaint().measureText(getText().subSequence(layout.getLineStart(line), layout.getLineEnd(line)).toString())) {
                        link[0].onClick(this);
                        return true;
                    }
                }
            }
        }
        return super.onTouchEvent(event);
    }
}
