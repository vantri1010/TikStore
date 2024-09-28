package im.bclpbkiauv.ui.hui.friendscircle_v1.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.View;
import androidx.core.content.ContextCompat;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;

public class CommFCArcView extends View {
    private String bgColorKey = Theme.key_windowBackgroundWhite;

    public CommFCArcView(Context context) {
        super(context);
        init((AttributeSet) null, context);
    }

    public CommFCArcView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, context);
    }

    public CommFCArcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, context);
    }

    private void init(AttributeSet attrs, Context context) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CommFCArcView);
            this.bgColorKey = array.getString(0) == null ? Theme.key_windowBackgroundWhite : array.getString(0);
            array.recycle();
        }
        LayerDrawable background = (LayerDrawable) ContextCompat.getDrawable(context, R.drawable.fc_oval_shape_top);
        GradientDrawable drawableByLayerId1 = (GradientDrawable) background.findDrawableByLayerId(R.id.ly_fc_header_oval);
        drawableByLayerId1.setColor(Theme.getColor(this.bgColorKey));
        background.setDrawableByLayerId(R.id.ly_fc_header_oval, drawableByLayerId1);
        GradientDrawable drawableByLayerId2 = (GradientDrawable) background.findDrawableByLayerId(R.id.ly_fc_header_rect);
        drawableByLayerId2.setColor(Theme.getColor(this.bgColorKey));
        background.setDrawableByLayerId(R.id.ly_fc_header_rect, drawableByLayerId2);
        setBackground(background);
    }
}
