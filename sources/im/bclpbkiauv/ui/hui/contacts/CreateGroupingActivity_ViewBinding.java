package im.bclpbkiauv.ui.hui.contacts;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.hviews.MryEditText;
import im.bclpbkiauv.ui.hviews.MryTextView;
import im.bclpbkiauv.ui.hviews.sidebar.SideBar;

public class CreateGroupingActivity_ViewBinding implements Unbinder {
    private CreateGroupingActivity target;
    private View view7f090219;
    private View view7f0905b3;

    public CreateGroupingActivity_ViewBinding(final CreateGroupingActivity target2, View source) {
        this.target = target2;
        target2.mEtGroupName = (MryEditText) Utils.findRequiredViewAsType(source, R.id.et_group_name, "field 'mEtGroupName'", MryEditText.class);
        View view = Utils.findRequiredView(source, R.id.iv_clear, "field 'mIvClear' and method 'onViewClicked'");
        target2.mIvClear = (ImageView) Utils.castView(view, R.id.iv_clear, "field 'mIvClear'", ImageView.class);
        this.view7f090219 = view;
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onViewClicked(p0);
            }
        });
        target2.mFlGroupName = (FrameLayout) Utils.findRequiredViewAsType(source, R.id.fl_group_name, "field 'mFlGroupName'", FrameLayout.class);
        View view2 = Utils.findRequiredView(source, R.id.tv_add_user, "field 'mTvAddUser' and method 'onViewClicked'");
        target2.mTvAddUser = (TextView) Utils.castView(view2, R.id.tv_add_user, "field 'mTvAddUser'", TextView.class);
        this.view7f0905b3 = view2;
        view2.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onViewClicked(p0);
            }
        });
        target2.mRvUsers = (RecyclerListView) Utils.findRequiredViewAsType(source, R.id.rv_users, "field 'mRvUsers'", RecyclerListView.class);
        target2.mTvChar = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tv_char, "field 'mTvChar'", MryTextView.class);
        target2.mSideBar = (SideBar) Utils.findRequiredViewAsType(source, R.id.sideBar, "field 'mSideBar'", SideBar.class);
        target2.mLlNotSupportEmojiTips = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.ll_not_support_emoji_tips, "field 'mLlNotSupportEmojiTips'", LinearLayout.class);
        target2.mLlContainer = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.ll_container, "field 'mLlContainer'", LinearLayout.class);
    }

    public void unbind() {
        CreateGroupingActivity target2 = this.target;
        if (target2 != null) {
            this.target = null;
            target2.mEtGroupName = null;
            target2.mIvClear = null;
            target2.mFlGroupName = null;
            target2.mTvAddUser = null;
            target2.mRvUsers = null;
            target2.mTvChar = null;
            target2.mSideBar = null;
            target2.mLlNotSupportEmojiTips = null;
            target2.mLlContainer = null;
            this.view7f090219.setOnClickListener((View.OnClickListener) null);
            this.view7f090219 = null;
            this.view7f0905b3.setOnClickListener((View.OnClickListener) null);
            this.view7f0905b3 = null;
            return;
        }
        throw new IllegalStateException("Bindings already cleared.");
    }
}
