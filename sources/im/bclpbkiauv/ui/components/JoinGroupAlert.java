package im.bclpbkiauv.ui.components;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.ChatActivity;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.cells.JoinSheetUserCell;
import im.bclpbkiauv.ui.components.RecyclerListView;

public class JoinGroupAlert extends BottomSheet {
    /* access modifiers changed from: private */
    public TLRPC.ChatInvite chatInvite;
    private BaseFragment fragment;
    private String hash;

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public JoinGroupAlert(android.content.Context r22, im.bclpbkiauv.tgnet.TLRPC.ChatInvite r23, java.lang.String r24, im.bclpbkiauv.ui.actionbar.BaseFragment r25) {
        /*
            r21 = this;
            r0 = r21
            r1 = r22
            r2 = r23
            r3 = 0
            r0.<init>(r1, r3, r3)
            r0.setApplyBottomPadding(r3)
            r0.setApplyTopPadding(r3)
            r4 = r25
            r0.fragment = r4
            r0.chatInvite = r2
            r5 = r24
            r0.hash = r5
            android.widget.LinearLayout r6 = new android.widget.LinearLayout
            r6.<init>(r1)
            r7 = 1
            r6.setOrientation(r7)
            r6.setClickable(r7)
            r0.setCustomView(r6)
            im.bclpbkiauv.ui.components.BackupImageView r8 = new im.bclpbkiauv.ui.components.BackupImageView
            r8.<init>(r1)
            r9 = 1108082688(0x420c0000, float:35.0)
            int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
            r8.setRoundRadius(r9)
            r10 = 70
            r11 = 70
            r12 = 49
            r13 = 0
            r14 = 12
            r15 = 0
            r16 = 0
            android.widget.LinearLayout$LayoutParams r9 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16)
            r6.addView(r8, r9)
            im.bclpbkiauv.tgnet.TLRPC$Chat r9 = r2.chat
            java.lang.String r10 = "50_50"
            if (r9 == 0) goto L_0x0069
            im.bclpbkiauv.ui.components.AvatarDrawable r9 = new im.bclpbkiauv.ui.components.AvatarDrawable
            im.bclpbkiauv.tgnet.TLRPC$Chat r11 = r2.chat
            r9.<init>((im.bclpbkiauv.tgnet.TLRPC.Chat) r11)
            im.bclpbkiauv.tgnet.TLRPC$Chat r11 = r2.chat
            java.lang.String r11 = r11.title
            im.bclpbkiauv.tgnet.TLRPC$Chat r12 = r2.chat
            int r12 = r12.participants_count
            im.bclpbkiauv.tgnet.TLRPC$Chat r13 = r2.chat
            im.bclpbkiauv.messenger.ImageLocation r13 = im.bclpbkiauv.messenger.ImageLocation.getForChat(r13, r3)
            r8.setImage((im.bclpbkiauv.messenger.ImageLocation) r13, (java.lang.String) r10, (android.graphics.drawable.Drawable) r9, (java.lang.Object) r2)
            goto L_0x008b
        L_0x0069:
            im.bclpbkiauv.ui.components.AvatarDrawable r9 = new im.bclpbkiauv.ui.components.AvatarDrawable
            r9.<init>()
            java.lang.String r11 = r2.title
            r12 = 0
            r9.setInfo(r3, r11, r12)
            java.lang.String r11 = r2.title
            int r12 = r2.participants_count
            im.bclpbkiauv.tgnet.TLRPC$Photo r13 = r2.photo
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$PhotoSize> r13 = r13.sizes
            r14 = 50
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r13 = im.bclpbkiauv.messenger.FileLoader.getClosestPhotoSizeWithSize(r13, r14)
            im.bclpbkiauv.tgnet.TLRPC$Photo r14 = r2.photo
            im.bclpbkiauv.messenger.ImageLocation r14 = im.bclpbkiauv.messenger.ImageLocation.getForPhoto(r13, r14)
            r8.setImage((im.bclpbkiauv.messenger.ImageLocation) r14, (java.lang.String) r10, (android.graphics.drawable.Drawable) r9, (java.lang.Object) r2)
        L_0x008b:
            android.widget.TextView r10 = new android.widget.TextView
            r10.<init>(r1)
            java.lang.String r13 = "fonts/rmedium.ttf"
            android.graphics.Typeface r13 = im.bclpbkiauv.messenger.AndroidUtilities.getTypeface(r13)
            r10.setTypeface(r13)
            r13 = 1099431936(0x41880000, float:17.0)
            r10.setTextSize(r7, r13)
            java.lang.String r13 = "dialogTextBlack"
            int r13 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r13)
            r10.setTextColor(r13)
            r10.setText(r11)
            r10.setSingleLine(r7)
            android.text.TextUtils$TruncateAt r13 = android.text.TextUtils.TruncateAt.END
            r10.setEllipsize(r13)
            r14 = -2
            r15 = -2
            r16 = 49
            r17 = 10
            r18 = 10
            r19 = 10
            if (r12 <= 0) goto L_0x00c1
            r20 = 0
            goto L_0x00c5
        L_0x00c1:
            r13 = 10
            r20 = 10
        L_0x00c5:
            android.widget.LinearLayout$LayoutParams r13 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r14, (int) r15, (int) r16, (int) r17, (int) r18, (int) r19, (int) r20)
            r6.addView(r10, r13)
            if (r12 <= 0) goto L_0x0106
            android.widget.TextView r13 = new android.widget.TextView
            r13.<init>(r1)
            r10 = r13
            r13 = 1096810496(0x41600000, float:14.0)
            r10.setTextSize(r7, r13)
            java.lang.String r13 = "dialogTextGray3"
            int r13 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r13)
            r10.setTextColor(r13)
            r10.setSingleLine(r7)
            android.text.TextUtils$TruncateAt r7 = android.text.TextUtils.TruncateAt.END
            r10.setEllipsize(r7)
            java.lang.String r7 = "Members"
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.formatPluralString(r7, r12)
            r10.setText(r7)
            r13 = -2
            r14 = -2
            r15 = 49
            r16 = 10
            r17 = 4
            r18 = 10
            r19 = 10
            android.widget.LinearLayout$LayoutParams r7 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18, (int) r19)
            r6.addView(r10, r7)
        L_0x0106:
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$User> r7 = r2.participants
            boolean r7 = r7.isEmpty()
            if (r7 != 0) goto L_0x0159
            im.bclpbkiauv.ui.components.RecyclerListView r7 = new im.bclpbkiauv.ui.components.RecyclerListView
            r7.<init>(r1)
            r13 = 1090519040(0x41000000, float:8.0)
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r13)
            r7.setPadding(r3, r3, r3, r13)
            r7.setNestedScrollingEnabled(r3)
            r7.setClipToPadding(r3)
            androidx.recyclerview.widget.LinearLayoutManager r13 = new androidx.recyclerview.widget.LinearLayoutManager
            android.content.Context r14 = r21.getContext()
            r13.<init>(r14, r3, r3)
            r7.setLayoutManager(r13)
            r7.setHorizontalScrollBarEnabled(r3)
            r7.setVerticalScrollBarEnabled(r3)
            im.bclpbkiauv.ui.components.JoinGroupAlert$UsersAdapter r13 = new im.bclpbkiauv.ui.components.JoinGroupAlert$UsersAdapter
            r13.<init>(r1)
            r7.setAdapter(r13)
            java.lang.String r13 = "dialogScrollGlow"
            int r13 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r13)
            r7.setGlowColor(r13)
            r14 = -2
            r15 = 90
            r16 = 49
            r17 = 0
            r18 = 0
            r19 = 0
            r20 = 0
            android.widget.LinearLayout$LayoutParams r13 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r14, (int) r15, (int) r16, (int) r17, (int) r18, (int) r19, (int) r20)
            r6.addView(r7, r13)
        L_0x0159:
            android.view.View r7 = new android.view.View
            r7.<init>(r1)
            java.lang.String r13 = "dialogShadowLine"
            int r13 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r13)
            r7.setBackgroundColor(r13)
            android.widget.LinearLayout$LayoutParams r13 = new android.widget.LinearLayout$LayoutParams
            int r14 = im.bclpbkiauv.messenger.AndroidUtilities.getShadowHeight()
            r15 = -1
            r13.<init>(r15, r14)
            r6.addView(r7, r13)
            im.bclpbkiauv.ui.components.PickerBottomLayout r13 = new im.bclpbkiauv.ui.components.PickerBottomLayout
            r13.<init>(r1, r3)
            r14 = 48
            r3 = 83
            android.widget.FrameLayout$LayoutParams r3 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r15, (int) r14, (int) r3)
            r6.addView(r13, r3)
            android.widget.TextView r3 = r13.cancelButton
            r14 = 1099956224(0x41900000, float:18.0)
            int r15 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            r14 = 0
            r3.setPadding(r15, r14, r1, r14)
            android.widget.TextView r1 = r13.cancelButton
            java.lang.String r3 = "dialogTextBlue2"
            int r14 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r3)
            r1.setTextColor(r14)
            android.widget.TextView r1 = r13.cancelButton
            r14 = 2131690308(0x7f0f0344, float:1.9009656E38)
            java.lang.String r15 = "Cancel"
            java.lang.String r14 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r14)
            java.lang.String r14 = r14.toUpperCase()
            r1.setText(r14)
            android.widget.TextView r1 = r13.cancelButton
            im.bclpbkiauv.ui.components.-$$Lambda$JoinGroupAlert$U2j4GtHJ5hdbyJ0RfRMT-RspKIQ r14 = new im.bclpbkiauv.ui.components.-$$Lambda$JoinGroupAlert$U2j4GtHJ5hdbyJ0RfRMT-RspKIQ
            r14.<init>()
            r1.setOnClickListener(r14)
            android.widget.LinearLayout r1 = r13.doneButton
            r14 = 1099956224(0x41900000, float:18.0)
            int r15 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            r2 = 0
            r1.setPadding(r15, r2, r14, r2)
            android.widget.LinearLayout r1 = r13.doneButton
            r1.setVisibility(r2)
            android.widget.TextView r1 = r13.doneButtonBadgeTextView
            r2 = 8
            r1.setVisibility(r2)
            android.widget.TextView r1 = r13.doneButtonTextView
            int r2 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r3)
            r1.setTextColor(r2)
            android.widget.TextView r1 = r13.doneButtonTextView
            r2 = 2131691726(0x7f0f08ce, float:1.9012532E38)
            java.lang.String r3 = "JoinGroup"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.LinearLayout r1 = r13.doneButton
            im.bclpbkiauv.ui.components.-$$Lambda$JoinGroupAlert$pTpkq3J7e50RUumy7VVVW1G_Hso r2 = new im.bclpbkiauv.ui.components.-$$Lambda$JoinGroupAlert$pTpkq3J7e50RUumy7VVVW1G_Hso
            r2.<init>()
            r1.setOnClickListener(r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.JoinGroupAlert.<init>(android.content.Context, im.bclpbkiauv.tgnet.TLRPC$ChatInvite, java.lang.String, im.bclpbkiauv.ui.actionbar.BaseFragment):void");
    }

    public /* synthetic */ void lambda$new$0$JoinGroupAlert(View view) {
        dismiss();
    }

    public /* synthetic */ void lambda$new$3$JoinGroupAlert(View v) {
        dismiss();
        TLRPC.TL_messages_importChatInvite req = new TLRPC.TL_messages_importChatInvite();
        req.hash = this.hash;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate(req) {
            private final /* synthetic */ TLRPC.TL_messages_importChatInvite f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                JoinGroupAlert.this.lambda$null$2$JoinGroupAlert(this.f$1, tLObject, tL_error);
            }
        }, 2);
    }

    public /* synthetic */ void lambda$null$2$JoinGroupAlert(TLRPC.TL_messages_importChatInvite req, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            MessagesController.getInstance(this.currentAccount).processUpdates((TLRPC.Updates) response, false);
        }
        AndroidUtilities.runOnUIThread(new Runnable(error, response, req) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ TLRPC.TL_messages_importChatInvite f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                JoinGroupAlert.this.lambda$null$1$JoinGroupAlert(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$1$JoinGroupAlert(TLRPC.TL_error error, TLObject response, TLRPC.TL_messages_importChatInvite req) {
        BaseFragment baseFragment = this.fragment;
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            if (error == null) {
                TLRPC.Updates updates = (TLRPC.Updates) response;
                if (!updates.chats.isEmpty()) {
                    TLRPC.Chat chat = updates.chats.get(0);
                    chat.left = false;
                    chat.kicked = false;
                    MessagesController.getInstance(this.currentAccount).putUsers(updates.users, false);
                    MessagesController.getInstance(this.currentAccount).putChats(updates.chats, false);
                    Bundle args = new Bundle();
                    args.putInt("chat_id", chat.id);
                    if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(args, this.fragment)) {
                        ChatActivity chatActivity = new ChatActivity(args);
                        BaseFragment baseFragment2 = this.fragment;
                        baseFragment2.presentFragment(chatActivity, baseFragment2 instanceof ChatActivity);
                        return;
                    }
                    return;
                }
                return;
            }
            AlertsCreator.processError(this.currentAccount, error, this.fragment, req, new Object[0]);
        }
    }

    private class UsersAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;

        public UsersAdapter(Context context2) {
            this.context = context2;
        }

        public int getItemCount() {
            int participants_count;
            int count = JoinGroupAlert.this.chatInvite.participants.size();
            if (JoinGroupAlert.this.chatInvite.chat != null) {
                participants_count = JoinGroupAlert.this.chatInvite.chat.participants_count;
            } else {
                participants_count = JoinGroupAlert.this.chatInvite.participants_count;
            }
            if (count != participants_count) {
                return count + 1;
            }
            return count;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = new JoinSheetUserCell(this.context);
            view.setLayoutParams(new RecyclerView.LayoutParams(AndroidUtilities.dp(100.0f), AndroidUtilities.dp(90.0f)));
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int participants_count;
            JoinSheetUserCell cell = (JoinSheetUserCell) holder.itemView;
            if (position < JoinGroupAlert.this.chatInvite.participants.size()) {
                cell.setUser(JoinGroupAlert.this.chatInvite.participants.get(position));
                return;
            }
            if (JoinGroupAlert.this.chatInvite.chat != null) {
                participants_count = JoinGroupAlert.this.chatInvite.chat.participants_count;
            } else {
                participants_count = JoinGroupAlert.this.chatInvite.participants_count;
            }
            cell.setCount(participants_count - JoinGroupAlert.this.chatInvite.participants.size());
        }

        public int getItemViewType(int i) {
            return 0;
        }
    }
}
