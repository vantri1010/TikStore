package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.view.View;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.ui.actionbar.Theme;

public class ShadowSectionCell extends View {
    private int size;

    public ShadowSectionCell(Context context) {
        this(context, 10);
    }

    public ShadowSectionCell(Context context, int s) {
        super(context);
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.size = s;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) this.size), 1073741824));
    }
}
