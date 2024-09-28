package im.bclpbkiauv.ui.hui.contacts;

import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.hviews.MryTextView;

public class GroupingMgrActivity_ViewBinding implements Unbinder {
    private GroupingMgrActivity target;
    private View view7f0905b2;

    public GroupingMgrActivity_ViewBinding(final GroupingMgrActivity target2, View source) {
        this.target = target2;
        View view = Utils.findRequiredView(source, R.id.tv_add_group, "field 'mTvAddGroup' and method 'onViewClicked'");
        target2.mTvAddGroup = (MryTextView) Utils.castView(view, R.id.tv_add_group, "field 'mTvAddGroup'", MryTextView.class);
        this.view7f0905b2 = view;
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onViewClicked();
            }
        });
        target2.mRcvList = (RecyclerListView) Utils.findRequiredViewAsType(source, R.id.rcvList, "field 'mRcvList'", RecyclerListView.class);
    }

    public void unbind() {
        GroupingMgrActivity target2 = this.target;
        if (target2 != null) {
            this.target = null;
            target2.mTvAddGroup = null;
            target2.mRcvList = null;
            this.view7f0905b2.setOnClickListener((View.OnClickListener) null);
            this.view7f0905b2 = null;
            return;
        }
        throw new IllegalStateException("Bindings already cleared.");
    }
}
