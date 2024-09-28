package im.bclpbkiauv.ui;

import android.view.View;
import android.widget.RelativeLayout;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.hviews.MryEditText;
import im.bclpbkiauv.ui.hviews.MryRoundButton;
import im.bclpbkiauv.ui.hviews.MryTextView;

public class ChangeSignActivity_ViewBinding implements Unbinder {
    private ChangeSignActivity target;
    private View view7f0900b3;

    public ChangeSignActivity_ViewBinding(final ChangeSignActivity target2, View source) {
        this.target = target2;
        target2.mEtSignature = (MryEditText) Utils.findRequiredViewAsType(source, R.id.et_signature, "field 'mEtSignature'", MryEditText.class);
        target2.mTvCount = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tv_count, "field 'mTvCount'", MryTextView.class);
        target2.mRlSignatureContainer = (RelativeLayout) Utils.findRequiredViewAsType(source, R.id.rl_signature_container, "field 'mRlSignatureContainer'", RelativeLayout.class);
        View view = Utils.findRequiredView(source, R.id.btn_submit, "field 'mBtnSubmit' and method 'onViewClicked'");
        target2.mBtnSubmit = (MryRoundButton) Utils.castView(view, R.id.btn_submit, "field 'mBtnSubmit'", MryRoundButton.class);
        this.view7f0900b3 = view;
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onViewClicked();
            }
        });
    }

    public void unbind() {
        ChangeSignActivity target2 = this.target;
        if (target2 != null) {
            this.target = null;
            target2.mEtSignature = null;
            target2.mTvCount = null;
            target2.mRlSignatureContainer = null;
            target2.mBtnSubmit = null;
            this.view7f0900b3.setOnClickListener((View.OnClickListener) null);
            this.view7f0900b3 = null;
            return;
        }
        throw new IllegalStateException("Bindings already cleared.");
    }
}
