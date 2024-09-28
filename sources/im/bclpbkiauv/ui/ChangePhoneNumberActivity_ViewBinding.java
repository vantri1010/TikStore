package im.bclpbkiauv.ui;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.hviews.MryEditText;
import im.bclpbkiauv.ui.hviews.MryRoundButton;
import im.bclpbkiauv.ui.hviews.MryTextView;

public class ChangePhoneNumberActivity_ViewBinding implements Unbinder {
    private ChangePhoneNumberActivity target;
    private View view7f0900b3;
    private View view7f090219;
    private View view7f0905d0;
    private View view7f090635;

    public ChangePhoneNumberActivity_ViewBinding(final ChangePhoneNumberActivity target2, View source) {
        this.target = target2;
        View view = Utils.findRequiredView(source, R.id.tv_country_code, "field 'mTvCountryCode' and method 'onViewClicked'");
        target2.mTvCountryCode = (MryTextView) Utils.castView(view, R.id.tv_country_code, "field 'mTvCountryCode'", MryTextView.class);
        this.view7f0905d0 = view;
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onViewClicked(p0);
            }
        });
        target2.mEtPhoneNumber = (MryEditText) Utils.findRequiredViewAsType(source, R.id.et_phone_number, "field 'mEtPhoneNumber'", MryEditText.class);
        View view2 = Utils.findRequiredView(source, R.id.iv_clear, "field 'mIvClear' and method 'onViewClicked'");
        target2.mIvClear = (ImageView) Utils.castView(view2, R.id.iv_clear, "field 'mIvClear'", ImageView.class);
        this.view7f090219 = view2;
        view2.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onViewClicked(p0);
            }
        });
        target2.mLlPhoneContainer = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.ll_phone_container, "field 'mLlPhoneContainer'", LinearLayout.class);
        target2.mEtCode = (MryEditText) Utils.findRequiredViewAsType(source, R.id.et_code, "field 'mEtCode'", MryEditText.class);
        View view3 = Utils.findRequiredView(source, R.id.tv_send_code, "field 'mTvSendCode' and method 'onViewClicked'");
        target2.mTvSendCode = (MryTextView) Utils.castView(view3, R.id.tv_send_code, "field 'mTvSendCode'", MryTextView.class);
        this.view7f090635 = view3;
        view3.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onViewClicked(p0);
            }
        });
        target2.mLlCodeContainer = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.ll_code_container, "field 'mLlCodeContainer'", LinearLayout.class);
        View view4 = Utils.findRequiredView(source, R.id.btn_submit, "field 'mBtnSubmit' and method 'onViewClicked'");
        target2.mBtnSubmit = (MryRoundButton) Utils.castView(view4, R.id.btn_submit, "field 'mBtnSubmit'", MryRoundButton.class);
        this.view7f0900b3 = view4;
        view4.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onViewClicked(p0);
            }
        });
    }

    public void unbind() {
        ChangePhoneNumberActivity target2 = this.target;
        if (target2 != null) {
            this.target = null;
            target2.mTvCountryCode = null;
            target2.mEtPhoneNumber = null;
            target2.mIvClear = null;
            target2.mLlPhoneContainer = null;
            target2.mEtCode = null;
            target2.mTvSendCode = null;
            target2.mLlCodeContainer = null;
            target2.mBtnSubmit = null;
            this.view7f0905d0.setOnClickListener((View.OnClickListener) null);
            this.view7f0905d0 = null;
            this.view7f090219.setOnClickListener((View.OnClickListener) null);
            this.view7f090219 = null;
            this.view7f090635.setOnClickListener((View.OnClickListener) null);
            this.view7f090635 = null;
            this.view7f0900b3.setOnClickListener((View.OnClickListener) null);
            this.view7f0900b3 = null;
            return;
        }
        throw new IllegalStateException("Bindings already cleared.");
    }
}
