package im.bclpbkiauv.ui.hui.contacts;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.hviews.MryTextView;

public class AddContactsInfoActivity_ViewBinding implements Unbinder {
    private AddContactsInfoActivity target;
    private View view7f09049e;
    private View view7f090511;
    private View view7f09052f;

    public AddContactsInfoActivity_ViewBinding(final AddContactsInfoActivity target2, View source) {
        this.target = target2;
        target2.avatarImage = (BackupImageView) Utils.findRequiredViewAsType(source, R.id.avatarImage, "field 'avatarImage'", BackupImageView.class);
        target2.mryNameView = (MryTextView) Utils.findRequiredViewAsType(source, R.id.mryNameView, "field 'mryNameView'", MryTextView.class);
        target2.tvReplyText = (TextView) Utils.findRequiredViewAsType(source, R.id.tvReplyText, "field 'tvReplyText'", TextView.class);
        target2.rcvReplyList = (RecyclerListView) Utils.findRequiredViewAsType(source, R.id.rcvReplyList, "field 'rcvReplyList'", RecyclerListView.class);
        View view = Utils.findRequiredView(source, R.id.tvReplyButton, "field 'tvReplyButton' and method 'onClick'");
        target2.tvReplyButton = (TextView) Utils.castView(view, R.id.tvReplyButton, "field 'tvReplyButton'", TextView.class);
        this.view7f09052f = view;
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onClick(p0);
            }
        });
        target2.flReplyLayout = (FrameLayout) Utils.findRequiredViewAsType(source, R.id.flReplyLayout, "field 'flReplyLayout'", FrameLayout.class);
        target2.llInfoLayout = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.llInfoLayout, "field 'llInfoLayout'", LinearLayout.class);
        View view2 = Utils.findRequiredView(source, R.id.tvNoteSettingView, "field 'tvNoteSettingView' and method 'onClick'");
        target2.tvNoteSettingView = (TextView) Utils.castView(view2, R.id.tvNoteSettingView, "field 'tvNoteSettingView'", TextView.class);
        this.view7f090511 = view2;
        view2.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onClick(p0);
            }
        });
        target2.tvBioText = (TextView) Utils.findRequiredViewAsType(source, R.id.tvBioText, "field 'tvBioText'", TextView.class);
        target2.llBioSettingView = (RelativeLayout) Utils.findRequiredViewAsType(source, R.id.rlBioSettingView, "field 'llBioSettingView'", RelativeLayout.class);
        target2.tvOriginalText = (TextView) Utils.findRequiredViewAsType(source, R.id.tvOriginalText, "field 'tvOriginalText'", TextView.class);
        target2.llOriginalView = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.llOriginalView, "field 'llOriginalView'", LinearLayout.class);
        View view3 = Utils.findRequiredView(source, R.id.tvAddContactStatus, "field 'tvAddContactStatus' and method 'onClick'");
        target2.tvAddContactStatus = (TextView) Utils.castView(view3, R.id.tvAddContactStatus, "field 'tvAddContactStatus'", TextView.class);
        this.view7f09049e = view3;
        view3.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onClick(p0);
            }
        });
        target2.tvBioDesc = (TextView) Utils.findRequiredViewAsType(source, R.id.tvBioDesc, "field 'tvBioDesc'", TextView.class);
        target2.tvOriginalDesc = (TextView) Utils.findRequiredViewAsType(source, R.id.tvOriginalDesc, "field 'tvOriginalDesc'", TextView.class);
        target2.ivGender = (ImageView) Utils.findRequiredViewAsType(source, R.id.ivGender, "field 'ivGender'", ImageView.class);
        target2.tvUpdateTime = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tv_update_time, "field 'tvUpdateTime'", MryTextView.class);
    }

    public void unbind() {
        AddContactsInfoActivity target2 = this.target;
        if (target2 != null) {
            this.target = null;
            target2.avatarImage = null;
            target2.mryNameView = null;
            target2.tvReplyText = null;
            target2.rcvReplyList = null;
            target2.tvReplyButton = null;
            target2.flReplyLayout = null;
            target2.llInfoLayout = null;
            target2.tvNoteSettingView = null;
            target2.tvBioText = null;
            target2.llBioSettingView = null;
            target2.tvOriginalText = null;
            target2.llOriginalView = null;
            target2.tvAddContactStatus = null;
            target2.tvBioDesc = null;
            target2.tvOriginalDesc = null;
            target2.ivGender = null;
            target2.tvUpdateTime = null;
            this.view7f09052f.setOnClickListener((View.OnClickListener) null);
            this.view7f09052f = null;
            this.view7f090511.setOnClickListener((View.OnClickListener) null);
            this.view7f090511 = null;
            this.view7f09049e.setOnClickListener((View.OnClickListener) null);
            this.view7f09049e = null;
            return;
        }
        throw new IllegalStateException("Bindings already cleared.");
    }
}
