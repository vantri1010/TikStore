package im.bclpbkiauv.ui.hviews.pop;

import android.content.Context;
import android.view.View;

public class EasyPopup extends BasePopup<EasyPopup> {
    private OnViewListener mOnViewListener;

    public interface OnViewListener {
        void initViews(View view, EasyPopup easyPopup);
    }

    public static EasyPopup create() {
        return new EasyPopup();
    }

    public static EasyPopup create(Context context) {
        return new EasyPopup(context);
    }

    public EasyPopup() {
    }

    public EasyPopup(Context context) {
        setContext(context);
    }

    /* access modifiers changed from: protected */
    public void initAttributes() {
    }

    /* access modifiers changed from: protected */
    public void initViews(View view, EasyPopup popup) {
        OnViewListener onViewListener = this.mOnViewListener;
        if (onViewListener != null) {
            onViewListener.initViews(view, popup);
        }
    }

    public EasyPopup setOnViewListener(OnViewListener listener) {
        this.mOnViewListener = listener;
        return this;
    }
}
