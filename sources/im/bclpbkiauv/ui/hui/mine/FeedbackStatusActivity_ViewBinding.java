package im.bclpbkiauv.ui.hui.mine;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.hviews.MryTextView;

public class FeedbackStatusActivity_ViewBinding implements Unbinder {
    private FeedbackStatusActivity target;
    private View view7f09009f;

    public FeedbackStatusActivity_ViewBinding(final FeedbackStatusActivity target2, View source) {
        this.target = target2;
        target2.ivFeedStatusImg = (ImageView) Utils.findRequiredViewAsType(source, R.id.ivFeedStatusImg, "field 'ivFeedStatusImg'", ImageView.class);
        target2.mryFeedStatusText = (MryTextView) Utils.findRequiredViewAsType(source, R.id.mryFeedStatusText, "field 'mryFeedStatusText'", MryTextView.class);
        target2.mryFeedDescText = (MryTextView) Utils.findRequiredViewAsType(source, R.id.mryFeedDescText, "field 'mryFeedDescText'", MryTextView.class);
        View view = Utils.findRequiredView(source, R.id.btnFinish, "field 'btnFinish' and method 'onViewClicked'");
        target2.btnFinish = (Button) Utils.castView(view, R.id.btnFinish, "field 'btnFinish'", Button.class);
        this.view7f09009f = view;
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onViewClicked();
            }
        });
    }

    public void unbind() {
        FeedbackStatusActivity target2 = this.target;
        if (target2 != null) {
            this.target = null;
            target2.ivFeedStatusImg = null;
            target2.mryFeedStatusText = null;
            target2.mryFeedDescText = null;
            target2.btnFinish = null;
            this.view7f09009f.setOnClickListener((View.OnClickListener) null);
            this.view7f09009f = null;
            return;
        }
        throw new IllegalStateException("Bindings already cleared.");
    }
}
