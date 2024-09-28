package im.bclpbkiauv.ui.hui.chats;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.components.RecyclerListView;

public class NewProfileActivity_ViewBinding implements Unbinder {
    private NewProfileActivity target;
    private View view7f0905b1;
    private View view7f090633;
    private View view7f090636;

    public NewProfileActivity_ViewBinding(final NewProfileActivity target2, View source) {
        this.target = target2;
        target2.listView = (RecyclerListView) Utils.findRequiredViewAsType(source, R.id.listview, "field 'listView'", RecyclerListView.class);
        View view = Utils.findRequiredView(source, R.id.tv_add_friend, "field 'tvAddFriend' and method 'onClick'");
        target2.tvAddFriend = (TextView) Utils.castView(view, R.id.tv_add_friend, "field 'tvAddFriend'", TextView.class);
        this.view7f0905b1 = view;
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onClick(p0);
            }
        });
        View view2 = Utils.findRequiredView(source, R.id.tv_send_message, "field 'tvSendMessage' and method 'onClick'");
        target2.tvSendMessage = (TextView) Utils.castView(view2, R.id.tv_send_message, "field 'tvSendMessage'", TextView.class);
        this.view7f090636 = view2;
        view2.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onClick(p0);
            }
        });
        View view3 = Utils.findRequiredView(source, R.id.tv_secret_chat, "field 'tvSecretChat' and method 'onClick'");
        target2.tvSecretChat = (TextView) Utils.castView(view3, R.id.tv_secret_chat, "field 'tvSecretChat'", TextView.class);
        this.view7f090633 = view3;
        view3.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onClick(p0);
            }
        });
        target2.mLlBottomBtn = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.ll_bottom_btn, "field 'mLlBottomBtn'", LinearLayout.class);
    }

    public void unbind() {
        NewProfileActivity target2 = this.target;
        if (target2 != null) {
            this.target = null;
            target2.listView = null;
            target2.tvAddFriend = null;
            target2.tvSendMessage = null;
            target2.tvSecretChat = null;
            target2.mLlBottomBtn = null;
            this.view7f0905b1.setOnClickListener((View.OnClickListener) null);
            this.view7f0905b1 = null;
            this.view7f090636.setOnClickListener((View.OnClickListener) null);
            this.view7f090636 = null;
            this.view7f090633.setOnClickListener((View.OnClickListener) null);
            this.view7f090633 = null;
            return;
        }
        throw new IllegalStateException("Bindings already cleared.");
    }
}
