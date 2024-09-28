package im.bclpbkiauv.ui.hui.friendscircle_v1.view;

import android.content.Context;
import android.util.AttributeSet;
import im.bclpbkiauv.ui.components.ColorTextView;

public class CustomerFcItemTopicView extends ColorTextView {
    public CustomerFcItemTopicView(Context context) {
        this(context, (AttributeSet) null);
    }

    public CustomerFcItemTopicView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomerFcItemTopicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
    }
}
