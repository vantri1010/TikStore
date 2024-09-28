package im.bclpbkiauv.ui.hui.mine;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.hviews.MryTextView;

public class FeedbackActivity_ViewBinding implements Unbinder {
    private FeedbackActivity target;
    private View view7f09009e;

    public FeedbackActivity_ViewBinding(final FeedbackActivity target2, View source) {
        this.target = target2;
        target2.mryFeedTitle = (MryTextView) Utils.findRequiredViewAsType(source, R.id.mryFeedTitle, "field 'mryFeedTitle'", MryTextView.class);
        target2.etFeedDescText = (EditText) Utils.findRequiredViewAsType(source, R.id.etFeedDescText, "field 'etFeedDescText'", EditText.class);
        View view = Utils.findRequiredView(source, R.id.btnFeedSubmit, "field 'btnFeedSubmit' and method 'onViewClicked'");
        target2.btnFeedSubmit = (Button) Utils.castView(view, R.id.btnFeedSubmit, "field 'btnFeedSubmit'", Button.class);
        this.view7f09009e = view;
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onViewClicked();
            }
        });
        target2.tvFeedPromt = (TextView) Utils.findRequiredViewAsType(source, R.id.tvFeedPromt, "field 'tvFeedPromt'", TextView.class);
    }

    public void unbind() {
        FeedbackActivity target2 = this.target;
        if (target2 != null) {
            this.target = null;
            target2.mryFeedTitle = null;
            target2.etFeedDescText = null;
            target2.btnFeedSubmit = null;
            target2.tvFeedPromt = null;
            this.view7f09009e.setOnClickListener((View.OnClickListener) null);
            this.view7f09009e = null;
            return;
        }
        throw new IllegalStateException("Bindings already cleared.");
    }
}
