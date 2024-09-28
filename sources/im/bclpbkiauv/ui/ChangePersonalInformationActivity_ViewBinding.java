package im.bclpbkiauv.ui;

import android.view.View;
import android.widget.ImageView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.RadialProgressView;
import im.bclpbkiauv.ui.hviews.MryEditText;
import im.bclpbkiauv.ui.hviews.MryRoundButton;
import im.bclpbkiauv.ui.hviews.MryTextView;

public class ChangePersonalInformationActivity_ViewBinding implements Unbinder {
    private ChangePersonalInformationActivity target;
    private View view7f09009b;
    private View view7f0900df;
    private View view7f0900e4;
    private View view7f0900e8;
    private View view7f09056d;

    public ChangePersonalInformationActivity_ViewBinding(final ChangePersonalInformationActivity target2, View source) {
        this.target = target2;
        View view = Utils.findRequiredView(source, R.id.containerAvatar, "field 'containerAvatar' and method 'onClick'");
        target2.containerAvatar = view;
        this.view7f0900df = view;
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onClick(p0);
            }
        });
        target2.containerCamera = Utils.findRequiredView(source, R.id.containerCamera, "field 'containerCamera'");
        target2.ivCamera = (ImageView) Utils.findRequiredViewAsType(source, R.id.ivCamera, "field 'ivCamera'", ImageView.class);
        target2.tvCamera = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvCamera, "field 'tvCamera'", MryTextView.class);
        target2.ivAvatar = (BackupImageView) Utils.findRequiredViewAsType(source, R.id.ivAvatar, "field 'ivAvatar'", BackupImageView.class);
        target2.ivAvatarProgress = (RadialProgressView) Utils.findRequiredViewAsType(source, R.id.ivAvatarProgress, "field 'ivAvatarProgress'", RadialProgressView.class);
        target2.etNickName = (MryEditText) Utils.findRequiredViewAsType(source, R.id.etNickName, "field 'etNickName'", MryEditText.class);
        target2.selectDataOfBirthParent = Utils.findRequiredView(source, R.id.selectDataOfBirthParent, "field 'selectDataOfBirthParent'");
        target2.containerGenderSelect = Utils.findRequiredView(source, R.id.containerGenderSelect, "field 'containerGenderSelect'");
        View view2 = Utils.findRequiredView(source, R.id.containerMale, "field 'containerMale' and method 'onClick'");
        target2.containerMale = view2;
        this.view7f0900e8 = view2;
        view2.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onClick(p0);
            }
        });
        target2.ivMale = (ImageView) Utils.findRequiredViewAsType(source, R.id.ivMale, "field 'ivMale'", ImageView.class);
        target2.tvMale = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvMale, "field 'tvMale'", MryTextView.class);
        View view3 = Utils.findRequiredView(source, R.id.containerFemale, "field 'containerFemale' and method 'onClick'");
        target2.containerFemale = view3;
        this.view7f0900e4 = view3;
        view3.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onClick(p0);
            }
        });
        target2.ivFemale = (ImageView) Utils.findRequiredViewAsType(source, R.id.ivFemale, "field 'ivFemale'", ImageView.class);
        target2.tvFemale = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvFemale, "field 'tvFemale'", MryTextView.class);
        View view4 = Utils.findRequiredView(source, R.id.tvSelectDateOfBirth, "field 'tvSelectDateOfBirth' and method 'onClick'");
        target2.tvSelectDateOfBirth = (MryTextView) Utils.castView(view4, R.id.tvSelectDateOfBirth, "field 'tvSelectDateOfBirth'", MryTextView.class);
        this.view7f09056d = view4;
        view4.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onClick(p0);
            }
        });
        target2.ivMore = (ImageView) Utils.findRequiredViewAsType(source, R.id.ivMore, "field 'ivMore'", ImageView.class);
        View view5 = Utils.findRequiredView(source, R.id.btnDone, "field 'btnDone' and method 'onClick'");
        target2.btnDone = (MryRoundButton) Utils.castView(view5, R.id.btnDone, "field 'btnDone'", MryRoundButton.class);
        this.view7f09009b = view5;
        view5.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onClick(p0);
            }
        });
    }

    public void unbind() {
        ChangePersonalInformationActivity target2 = this.target;
        if (target2 != null) {
            this.target = null;
            target2.containerAvatar = null;
            target2.containerCamera = null;
            target2.ivCamera = null;
            target2.tvCamera = null;
            target2.ivAvatar = null;
            target2.ivAvatarProgress = null;
            target2.etNickName = null;
            target2.selectDataOfBirthParent = null;
            target2.containerGenderSelect = null;
            target2.containerMale = null;
            target2.ivMale = null;
            target2.tvMale = null;
            target2.containerFemale = null;
            target2.ivFemale = null;
            target2.tvFemale = null;
            target2.tvSelectDateOfBirth = null;
            target2.ivMore = null;
            target2.btnDone = null;
            this.view7f0900df.setOnClickListener((View.OnClickListener) null);
            this.view7f0900df = null;
            this.view7f0900e8.setOnClickListener((View.OnClickListener) null);
            this.view7f0900e8 = null;
            this.view7f0900e4.setOnClickListener((View.OnClickListener) null);
            this.view7f0900e4 = null;
            this.view7f09056d.setOnClickListener((View.OnClickListener) null);
            this.view7f09056d = null;
            this.view7f09009b.setOnClickListener((View.OnClickListener) null);
            this.view7f09009b = null;
            return;
        }
        throw new IllegalStateException("Bindings already cleared.");
    }
}
