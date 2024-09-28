package im.bclpbkiauv.ui.hui.friendscircle_v1.ui;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.components.RecyclerListView;

public class FcPublishActivity_ViewBinding implements Unbinder {
    private FcPublishActivity target;
    private View view7f09008a;
    private View view7f09008b;
    private View view7f09021a;

    public FcPublishActivity_ViewBinding(final FcPublishActivity target2, View source) {
        this.target = target2;
        target2.etContent = (EditText) Utils.findRequiredViewAsType(source, R.id.et_content, "field 'etContent'", EditText.class);
        View view = Utils.findRequiredView(source, R.id.biv_video, "field 'bivVideo' and method 'onViewClicked'");
        target2.bivVideo = (ImageView) Utils.castView(view, R.id.biv_video, "field 'bivVideo'", ImageView.class);
        this.view7f09008a = view;
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onViewClicked(p0);
            }
        });
        target2.rlContainer = (RelativeLayout) Utils.findRequiredViewAsType(source, R.id.rl_container, "field 'rlContainer'", RelativeLayout.class);
        View view2 = Utils.findRequiredView(source, R.id.biv_video_h, "field 'bivVideoH' and method 'onViewClicked'");
        target2.bivVideoH = (ImageView) Utils.castView(view2, R.id.biv_video_h, "field 'bivVideoH'", ImageView.class);
        this.view7f09008b = view2;
        view2.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onViewClicked(p0);
            }
        });
        target2.rvMenu = (RecyclerListView) Utils.findRequiredViewAsType(source, R.id.rvMenu, "field 'rvMenu'", RecyclerListView.class);
        View view3 = Utils.findRequiredView(source, R.id.iv_close, "method 'onViewClicked'");
        this.view7f09021a = view3;
        view3.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onViewClicked(p0);
            }
        });
    }

    public void unbind() {
        FcPublishActivity target2 = this.target;
        if (target2 != null) {
            this.target = null;
            target2.etContent = null;
            target2.bivVideo = null;
            target2.rlContainer = null;
            target2.bivVideoH = null;
            target2.rvMenu = null;
            this.view7f09008a.setOnClickListener((View.OnClickListener) null);
            this.view7f09008a = null;
            this.view7f09008b.setOnClickListener((View.OnClickListener) null);
            this.view7f09008b = null;
            this.view7f09021a.setOnClickListener((View.OnClickListener) null);
            this.view7f09021a = null;
            return;
        }
        throw new IllegalStateException("Bindings already cleared.");
    }
}
