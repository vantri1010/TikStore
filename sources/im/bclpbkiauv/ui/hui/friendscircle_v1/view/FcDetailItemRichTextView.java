package im.bclpbkiauv.ui.hui.friendscircle_v1.view;

import android.content.Context;
import android.util.AttributeSet;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.richtext.RichTextView;

public class FcDetailItemRichTextView extends RichTextView {
    public FcDetailItemRichTextView(Context context) {
        this(context, (AttributeSet) null);
    }

    public FcDetailItemRichTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FcDetailItemRichTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
    }
}
