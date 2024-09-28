package im.bclpbkiauv.ui.hui.contacts;

import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import im.bclpbkiauv.messenger.R;

public class NoteAndGroupingEditActivity_ViewBinding implements Unbinder {
    private NoteAndGroupingEditActivity target;
    private View view7f0901d8;
    private View view7f0904f9;

    public NoteAndGroupingEditActivity_ViewBinding(final NoteAndGroupingEditActivity target2, View source) {
        this.target = target2;
        target2.tvGroupDescView = (TextView) Utils.findRequiredViewAsType(source, R.id.tvGroupDescView, "field 'tvGroupDescView'", TextView.class);
        View view = Utils.findRequiredView(source, R.id.tvGroupingSettingView, "field 'tvGroupingSettingView' and method 'onViewClicked'");
        target2.tvGroupingSettingView = (TextView) Utils.castView(view, R.id.tvGroupingSettingView, "field 'tvGroupingSettingView'", TextView.class);
        this.view7f0904f9 = view;
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onViewClicked(p0);
            }
        });
        target2.tvNoteDescView = (TextView) Utils.findRequiredViewAsType(source, R.id.tvNoteDescView, "field 'tvNoteDescView'", TextView.class);
        target2.etNoteEditView = (EditText) Utils.findRequiredViewAsType(source, R.id.etNoteEditView, "field 'etNoteEditView'", EditText.class);
        View view2 = Utils.findRequiredView(source, R.id.ivClearNoteView, "field 'ivClearNoteView' and method 'onViewClicked'");
        target2.ivClearNoteView = (ImageView) Utils.castView(view2, R.id.ivClearNoteView, "field 'ivClearNoteView'", ImageView.class);
        this.view7f0901d8 = view2;
        view2.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onViewClicked(p0);
            }
        });
        target2.flNoteSettingLayout = (FrameLayout) Utils.findRequiredViewAsType(source, R.id.flNoteSettingLayout, "field 'flNoteSettingLayout'", FrameLayout.class);
    }

    public void unbind() {
        NoteAndGroupingEditActivity target2 = this.target;
        if (target2 != null) {
            this.target = null;
            target2.tvGroupDescView = null;
            target2.tvGroupingSettingView = null;
            target2.tvNoteDescView = null;
            target2.etNoteEditView = null;
            target2.ivClearNoteView = null;
            target2.flNoteSettingLayout = null;
            this.view7f0904f9.setOnClickListener((View.OnClickListener) null);
            this.view7f0904f9 = null;
            this.view7f0901d8.setOnClickListener((View.OnClickListener) null);
            this.view7f0901d8 = null;
            return;
        }
        throw new IllegalStateException("Bindings already cleared.");
    }
}
