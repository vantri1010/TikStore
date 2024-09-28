package im.bclpbkiauv.ui.components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.cells.CheckBoxCell;
import im.bclpbkiauv.ui.cells.CheckBoxUserCell;
import im.bclpbkiauv.ui.cells.ShadowSectionCell;
import im.bclpbkiauv.ui.components.RecyclerListView;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class AdminLogFilterAlert extends BottomSheet {
    private ListAdapter adapter;
    /* access modifiers changed from: private */
    public int adminsRow;
    /* access modifiers changed from: private */
    public int allAdminsRow;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.ChannelParticipant> currentAdmins;
    /* access modifiers changed from: private */
    public TLRPC.TL_channelAdminLogEventsFilter currentFilter;
    private AdminLogFilterAlertDelegate delegate;
    /* access modifiers changed from: private */
    public int deleteRow;
    /* access modifiers changed from: private */
    public int editRow;
    /* access modifiers changed from: private */
    public boolean ignoreLayout;
    /* access modifiers changed from: private */
    public int infoRow;
    /* access modifiers changed from: private */
    public boolean isMegagroup;
    /* access modifiers changed from: private */
    public int leavingRow;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public int membersRow;
    private FrameLayout pickerBottomLayout;
    /* access modifiers changed from: private */
    public int pinnedRow;
    private int reqId;
    /* access modifiers changed from: private */
    public int restrictionsRow;
    private BottomSheet.BottomSheetCell saveButton;
    /* access modifiers changed from: private */
    public int scrollOffsetY;
    /* access modifiers changed from: private */
    public SparseArray<TLRPC.User> selectedAdmins;
    /* access modifiers changed from: private */
    public Drawable shadowDrawable;
    private Pattern urlPattern;

    public interface AdminLogFilterAlertDelegate {
        void didSelectRights(TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter, SparseArray<TLRPC.User> sparseArray);
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public AdminLogFilterAlert(android.content.Context r19, im.bclpbkiauv.tgnet.TLRPC.TL_channelAdminLogEventsFilter r20, android.util.SparseArray<im.bclpbkiauv.tgnet.TLRPC.User> r21, boolean r22) {
        /*
            r18 = this;
            r0 = r18
            r1 = r19
            r2 = r20
            r3 = r22
            r4 = 0
            r0.<init>(r1, r4, r4)
            if (r2 == 0) goto L_0x0067
            im.bclpbkiauv.tgnet.TLRPC$TL_channelAdminLogEventsFilter r5 = new im.bclpbkiauv.tgnet.TLRPC$TL_channelAdminLogEventsFilter
            r5.<init>()
            r0.currentFilter = r5
            boolean r6 = r2.join
            r5.join = r6
            im.bclpbkiauv.tgnet.TLRPC$TL_channelAdminLogEventsFilter r5 = r0.currentFilter
            boolean r6 = r2.leave
            r5.leave = r6
            im.bclpbkiauv.tgnet.TLRPC$TL_channelAdminLogEventsFilter r5 = r0.currentFilter
            boolean r6 = r2.invite
            r5.invite = r6
            im.bclpbkiauv.tgnet.TLRPC$TL_channelAdminLogEventsFilter r5 = r0.currentFilter
            boolean r6 = r2.ban
            r5.ban = r6
            im.bclpbkiauv.tgnet.TLRPC$TL_channelAdminLogEventsFilter r5 = r0.currentFilter
            boolean r6 = r2.unban
            r5.unban = r6
            im.bclpbkiauv.tgnet.TLRPC$TL_channelAdminLogEventsFilter r5 = r0.currentFilter
            boolean r6 = r2.kick
            r5.kick = r6
            im.bclpbkiauv.tgnet.TLRPC$TL_channelAdminLogEventsFilter r5 = r0.currentFilter
            boolean r6 = r2.unkick
            r5.unkick = r6
            im.bclpbkiauv.tgnet.TLRPC$TL_channelAdminLogEventsFilter r5 = r0.currentFilter
            boolean r6 = r2.promote
            r5.promote = r6
            im.bclpbkiauv.tgnet.TLRPC$TL_channelAdminLogEventsFilter r5 = r0.currentFilter
            boolean r6 = r2.demote
            r5.demote = r6
            im.bclpbkiauv.tgnet.TLRPC$TL_channelAdminLogEventsFilter r5 = r0.currentFilter
            boolean r6 = r2.info
            r5.info = r6
            im.bclpbkiauv.tgnet.TLRPC$TL_channelAdminLogEventsFilter r5 = r0.currentFilter
            boolean r6 = r2.settings
            r5.settings = r6
            im.bclpbkiauv.tgnet.TLRPC$TL_channelAdminLogEventsFilter r5 = r0.currentFilter
            boolean r6 = r2.pinned
            r5.pinned = r6
            im.bclpbkiauv.tgnet.TLRPC$TL_channelAdminLogEventsFilter r5 = r0.currentFilter
            boolean r6 = r2.edit
            r5.edit = r6
            im.bclpbkiauv.tgnet.TLRPC$TL_channelAdminLogEventsFilter r5 = r0.currentFilter
            boolean r6 = r2.delete
            r5.delete = r6
        L_0x0067:
            if (r21 == 0) goto L_0x006f
            android.util.SparseArray r5 = r21.clone()
            r0.selectedAdmins = r5
        L_0x006f:
            r0.isMegagroup = r3
            r5 = 1
            r6 = -1
            if (r3 == 0) goto L_0x007b
            int r7 = r5 + 1
            r0.restrictionsRow = r5
            r5 = r7
            goto L_0x007d
        L_0x007b:
            r0.restrictionsRow = r6
        L_0x007d:
            int r7 = r5 + 1
            r0.adminsRow = r5
            int r5 = r7 + 1
            r0.membersRow = r7
            int r7 = r5 + 1
            r0.infoRow = r5
            int r5 = r7 + 1
            r0.deleteRow = r7
            int r7 = r5 + 1
            r0.editRow = r5
            boolean r5 = r0.isMegagroup
            if (r5 == 0) goto L_0x009b
            int r5 = r7 + 1
            r0.pinnedRow = r7
            r7 = r5
            goto L_0x009d
        L_0x009b:
            r0.pinnedRow = r6
        L_0x009d:
            r0.leavingRow = r7
            int r7 = r7 + 2
            r0.allAdminsRow = r7
            android.content.res.Resources r5 = r19.getResources()
            r8 = 2131231573(0x7f080355, float:1.807923E38)
            android.graphics.drawable.Drawable r5 = r5.getDrawable(r8)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            r0.shadowDrawable = r5
            android.graphics.PorterDuffColorFilter r8 = new android.graphics.PorterDuffColorFilter
            java.lang.String r9 = "dialogBackground"
            int r9 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r9)
            android.graphics.PorterDuff$Mode r10 = android.graphics.PorterDuff.Mode.MULTIPLY
            r8.<init>(r9, r10)
            r5.setColorFilter(r8)
            im.bclpbkiauv.ui.components.AdminLogFilterAlert$1 r5 = new im.bclpbkiauv.ui.components.AdminLogFilterAlert$1
            r5.<init>(r1)
            r0.containerView = r5
            android.view.ViewGroup r5 = r0.containerView
            r5.setWillNotDraw(r4)
            android.view.ViewGroup r5 = r0.containerView
            int r8 = r0.backgroundPaddingLeft
            int r9 = r0.backgroundPaddingLeft
            r5.setPadding(r8, r4, r9, r4)
            im.bclpbkiauv.ui.components.AdminLogFilterAlert$2 r5 = new im.bclpbkiauv.ui.components.AdminLogFilterAlert$2
            r5.<init>(r1)
            r0.listView = r5
            androidx.recyclerview.widget.LinearLayoutManager r8 = new androidx.recyclerview.widget.LinearLayoutManager
            android.content.Context r9 = r18.getContext()
            r10 = 1
            r8.<init>(r9, r10, r4)
            r5.setLayoutManager(r8)
            im.bclpbkiauv.ui.components.RecyclerListView r5 = r0.listView
            im.bclpbkiauv.ui.components.AdminLogFilterAlert$ListAdapter r8 = new im.bclpbkiauv.ui.components.AdminLogFilterAlert$ListAdapter
            r8.<init>(r1)
            r0.adapter = r8
            r5.setAdapter(r8)
            im.bclpbkiauv.ui.components.RecyclerListView r5 = r0.listView
            r5.setVerticalScrollBarEnabled(r4)
            im.bclpbkiauv.ui.components.RecyclerListView r5 = r0.listView
            r5.setClipToPadding(r4)
            im.bclpbkiauv.ui.components.RecyclerListView r5 = r0.listView
            r5.setEnabled(r10)
            im.bclpbkiauv.ui.components.RecyclerListView r5 = r0.listView
            java.lang.String r8 = "dialogScrollGlow"
            int r8 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r8)
            r5.setGlowColor(r8)
            im.bclpbkiauv.ui.components.RecyclerListView r5 = r0.listView
            im.bclpbkiauv.ui.components.AdminLogFilterAlert$3 r8 = new im.bclpbkiauv.ui.components.AdminLogFilterAlert$3
            r8.<init>()
            r5.setOnScrollListener(r8)
            im.bclpbkiauv.ui.components.RecyclerListView r5 = r0.listView
            im.bclpbkiauv.ui.components.-$$Lambda$AdminLogFilterAlert$eQhnhFgKz-WCqYac8QMttF55yL4 r8 = new im.bclpbkiauv.ui.components.-$$Lambda$AdminLogFilterAlert$eQhnhFgKz-WCqYac8QMttF55yL4
            r8.<init>()
            r5.setOnItemClickListener((im.bclpbkiauv.ui.components.RecyclerListView.OnItemClickListener) r8)
            android.view.ViewGroup r5 = r0.containerView
            im.bclpbkiauv.ui.components.RecyclerListView r8 = r0.listView
            r11 = -1082130432(0xffffffffbf800000, float:-1.0)
            r12 = -1082130432(0xffffffffbf800000, float:-1.0)
            r13 = 51
            r14 = 0
            r15 = 0
            r16 = 0
            r17 = 1111490560(0x42400000, float:48.0)
            android.widget.FrameLayout$LayoutParams r9 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r5.addView(r8, r9)
            android.view.View r5 = new android.view.View
            r5.<init>(r1)
            r8 = 2131231072(0x7f080160, float:1.8078215E38)
            r5.setBackgroundResource(r8)
            android.view.ViewGroup r8 = r0.containerView
            r12 = 1077936128(0x40400000, float:3.0)
            r13 = 83
            android.widget.FrameLayout$LayoutParams r9 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r8.addView(r5, r9)
            im.bclpbkiauv.ui.actionbar.BottomSheet$BottomSheetCell r8 = new im.bclpbkiauv.ui.actionbar.BottomSheet$BottomSheetCell
            r8.<init>(r1, r10)
            r0.saveButton = r8
            android.graphics.drawable.Drawable r9 = im.bclpbkiauv.ui.actionbar.Theme.getSelectorDrawable(r4)
            r8.setBackgroundDrawable(r9)
            im.bclpbkiauv.ui.actionbar.BottomSheet$BottomSheetCell r8 = r0.saveButton
            r9 = 2131693680(0x7f0f1070, float:1.9016495E38)
            java.lang.String r10 = "Save"
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r10, r9)
            java.lang.String r9 = r9.toUpperCase()
            r8.setTextAndIcon(r9, r4)
            im.bclpbkiauv.ui.actionbar.BottomSheet$BottomSheetCell r4 = r0.saveButton
            java.lang.String r8 = "dialogTextBlue2"
            int r8 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r8)
            r4.setTextColor(r8)
            im.bclpbkiauv.ui.actionbar.BottomSheet$BottomSheetCell r4 = r0.saveButton
            im.bclpbkiauv.ui.components.-$$Lambda$AdminLogFilterAlert$DKn8BObRaMg8cU4Ls80jjXZAdxo r8 = new im.bclpbkiauv.ui.components.-$$Lambda$AdminLogFilterAlert$DKn8BObRaMg8cU4Ls80jjXZAdxo
            r8.<init>()
            r4.setOnClickListener(r8)
            android.view.ViewGroup r4 = r0.containerView
            im.bclpbkiauv.ui.actionbar.BottomSheet$BottomSheetCell r8 = r0.saveButton
            r9 = 48
            r10 = 83
            android.widget.FrameLayout$LayoutParams r6 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r6, (int) r9, (int) r10)
            r4.addView(r8, r6)
            im.bclpbkiauv.ui.components.AdminLogFilterAlert$ListAdapter r4 = r0.adapter
            r4.notifyDataSetChanged()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.AdminLogFilterAlert.<init>(android.content.Context, im.bclpbkiauv.tgnet.TLRPC$TL_channelAdminLogEventsFilter, android.util.SparseArray, boolean):void");
    }

    public /* synthetic */ void lambda$new$0$AdminLogFilterAlert(View view, int position) {
        if (view instanceof CheckBoxCell) {
            CheckBoxCell cell = (CheckBoxCell) view;
            boolean isChecked = cell.isChecked();
            cell.setChecked(!isChecked, true);
            if (position == 0) {
                if (isChecked) {
                    TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter = new TLRPC.TL_channelAdminLogEventsFilter();
                    this.currentFilter = tL_channelAdminLogEventsFilter;
                    tL_channelAdminLogEventsFilter.delete = false;
                    tL_channelAdminLogEventsFilter.edit = false;
                    tL_channelAdminLogEventsFilter.pinned = false;
                    tL_channelAdminLogEventsFilter.settings = false;
                    tL_channelAdminLogEventsFilter.info = false;
                    tL_channelAdminLogEventsFilter.demote = false;
                    tL_channelAdminLogEventsFilter.promote = false;
                    tL_channelAdminLogEventsFilter.unkick = false;
                    tL_channelAdminLogEventsFilter.kick = false;
                    tL_channelAdminLogEventsFilter.unban = false;
                    tL_channelAdminLogEventsFilter.ban = false;
                    tL_channelAdminLogEventsFilter.invite = false;
                    tL_channelAdminLogEventsFilter.leave = false;
                    tL_channelAdminLogEventsFilter.join = false;
                } else {
                    this.currentFilter = null;
                }
                int count = this.listView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = this.listView.getChildAt(a);
                    RecyclerView.ViewHolder holder = this.listView.findContainingViewHolder(child);
                    int pos = holder.getAdapterPosition();
                    if (holder.getItemViewType() == 0 && pos > 0 && pos < this.allAdminsRow - 1) {
                        ((CheckBoxCell) child).setChecked(!isChecked, true);
                    }
                }
            } else if (position == this.allAdminsRow) {
                if (isChecked) {
                    this.selectedAdmins = new SparseArray<>();
                } else {
                    this.selectedAdmins = null;
                }
                int count2 = this.listView.getChildCount();
                for (int a2 = 0; a2 < count2; a2++) {
                    View child2 = this.listView.getChildAt(a2);
                    RecyclerView.ViewHolder holder2 = this.listView.findContainingViewHolder(child2);
                    int adapterPosition = holder2.getAdapterPosition();
                    if (holder2.getItemViewType() == 2) {
                        ((CheckBoxUserCell) child2).setChecked(!isChecked, true);
                    }
                }
            } else {
                if (this.currentFilter == null) {
                    TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter2 = new TLRPC.TL_channelAdminLogEventsFilter();
                    this.currentFilter = tL_channelAdminLogEventsFilter2;
                    tL_channelAdminLogEventsFilter2.delete = true;
                    tL_channelAdminLogEventsFilter2.edit = true;
                    tL_channelAdminLogEventsFilter2.pinned = true;
                    tL_channelAdminLogEventsFilter2.settings = true;
                    tL_channelAdminLogEventsFilter2.info = true;
                    tL_channelAdminLogEventsFilter2.demote = true;
                    tL_channelAdminLogEventsFilter2.promote = true;
                    tL_channelAdminLogEventsFilter2.unkick = true;
                    tL_channelAdminLogEventsFilter2.kick = true;
                    tL_channelAdminLogEventsFilter2.unban = true;
                    tL_channelAdminLogEventsFilter2.ban = true;
                    tL_channelAdminLogEventsFilter2.invite = true;
                    tL_channelAdminLogEventsFilter2.leave = true;
                    tL_channelAdminLogEventsFilter2.join = true;
                    RecyclerView.ViewHolder holder3 = this.listView.findViewHolderForAdapterPosition(0);
                    if (holder3 != null) {
                        ((CheckBoxCell) holder3.itemView).setChecked(false, true);
                    }
                }
                if (position == this.restrictionsRow) {
                    TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter3 = this.currentFilter;
                    boolean z = !tL_channelAdminLogEventsFilter3.kick;
                    tL_channelAdminLogEventsFilter3.unban = z;
                    tL_channelAdminLogEventsFilter3.unkick = z;
                    tL_channelAdminLogEventsFilter3.ban = z;
                    tL_channelAdminLogEventsFilter3.kick = z;
                } else if (position == this.adminsRow) {
                    TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter4 = this.currentFilter;
                    boolean z2 = !tL_channelAdminLogEventsFilter4.demote;
                    tL_channelAdminLogEventsFilter4.demote = z2;
                    tL_channelAdminLogEventsFilter4.promote = z2;
                } else if (position == this.membersRow) {
                    TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter5 = this.currentFilter;
                    boolean z3 = !tL_channelAdminLogEventsFilter5.join;
                    tL_channelAdminLogEventsFilter5.join = z3;
                    tL_channelAdminLogEventsFilter5.invite = z3;
                } else if (position == this.infoRow) {
                    TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter6 = this.currentFilter;
                    boolean z4 = !tL_channelAdminLogEventsFilter6.info;
                    tL_channelAdminLogEventsFilter6.settings = z4;
                    tL_channelAdminLogEventsFilter6.info = z4;
                } else if (position == this.deleteRow) {
                    TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter7 = this.currentFilter;
                    tL_channelAdminLogEventsFilter7.delete = !tL_channelAdminLogEventsFilter7.delete;
                } else if (position == this.editRow) {
                    TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter8 = this.currentFilter;
                    tL_channelAdminLogEventsFilter8.edit = !tL_channelAdminLogEventsFilter8.edit;
                } else if (position == this.pinnedRow) {
                    TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter9 = this.currentFilter;
                    tL_channelAdminLogEventsFilter9.pinned = !tL_channelAdminLogEventsFilter9.pinned;
                } else if (position == this.leavingRow) {
                    TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter10 = this.currentFilter;
                    tL_channelAdminLogEventsFilter10.leave = !tL_channelAdminLogEventsFilter10.leave;
                }
            }
            TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter11 = this.currentFilter;
            if (tL_channelAdminLogEventsFilter11 == null || tL_channelAdminLogEventsFilter11.join || this.currentFilter.leave || this.currentFilter.leave || this.currentFilter.invite || this.currentFilter.ban || this.currentFilter.unban || this.currentFilter.kick || this.currentFilter.unkick || this.currentFilter.promote || this.currentFilter.demote || this.currentFilter.info || this.currentFilter.settings || this.currentFilter.pinned || this.currentFilter.edit || this.currentFilter.delete) {
                this.saveButton.setEnabled(true);
                this.saveButton.setAlpha(1.0f);
            } else {
                this.saveButton.setEnabled(false);
                this.saveButton.setAlpha(0.5f);
            }
            updateCheckBoxStatus(false);
        } else if (view instanceof CheckBoxUserCell) {
            CheckBoxUserCell checkBoxUserCell = (CheckBoxUserCell) view;
            if (this.selectedAdmins == null) {
                this.selectedAdmins = new SparseArray<>();
                RecyclerView.ViewHolder holder4 = this.listView.findViewHolderForAdapterPosition(this.allAdminsRow);
                if (holder4 != null) {
                    ((CheckBoxCell) holder4.itemView).setChecked(false, true);
                }
                for (int a3 = 0; a3 < this.currentAdmins.size(); a3++) {
                    TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.currentAdmins.get(a3).user_id));
                    this.selectedAdmins.put(user.id, user);
                }
            }
            int a4 = checkBoxUserCell.isChecked();
            TLRPC.User user2 = checkBoxUserCell.getCurrentUser();
            if (a4 != 0) {
                this.selectedAdmins.remove(user2.id);
            } else {
                this.selectedAdmins.put(user2.id, user2);
            }
            checkBoxUserCell.setChecked(a4 ^ 1, true);
            updateCheckBoxStatus(true);
        }
    }

    public /* synthetic */ void lambda$new$1$AdminLogFilterAlert(View v) {
        this.delegate.didSelectRights(this.currentFilter, this.selectedAdmins);
        dismiss();
    }

    public void setCurrentAdmins(ArrayList<TLRPC.ChannelParticipant> admins) {
        this.currentAdmins = admins;
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    private void updateCheckBoxStatus(boolean isCheckBoxUserCell) {
        RecyclerListView recyclerListView;
        if (this.currentFilter != null && (recyclerListView = this.listView) != null) {
            View child = isCheckBoxUserCell ? recyclerListView.getChildAt(this.allAdminsRow) : recyclerListView.getChildAt(0);
            if (!(child instanceof CheckBoxCell)) {
                return;
            }
            if (isCheckBoxUserCell) {
                SparseArray<TLRPC.User> sparseArray = this.selectedAdmins;
                if (sparseArray == null || this.currentAdmins == null || sparseArray.size() != this.currentAdmins.size()) {
                    ((CheckBoxUserCell) child).setChecked(false, true);
                } else {
                    ((CheckBoxUserCell) child).setChecked(true, true);
                }
            } else if (this.isMegagroup) {
                if (!this.currentFilter.join || !this.currentFilter.leave || !this.currentFilter.leave || !this.currentFilter.invite || !this.currentFilter.ban || !this.currentFilter.unban || !this.currentFilter.kick || !this.currentFilter.unkick || !this.currentFilter.promote || !this.currentFilter.demote || !this.currentFilter.info || !this.currentFilter.settings || !this.currentFilter.pinned || !this.currentFilter.edit || !this.currentFilter.delete) {
                    ((CheckBoxCell) child).setChecked(false, true);
                } else {
                    ((CheckBoxCell) child).setChecked(true, true);
                }
            } else if (!this.currentFilter.join || !this.currentFilter.leave || !this.currentFilter.leave || !this.currentFilter.invite || !this.currentFilter.promote || !this.currentFilter.demote || !this.currentFilter.info || !this.currentFilter.settings || !this.currentFilter.edit || !this.currentFilter.delete) {
                ((CheckBoxCell) child).setChecked(false, true);
            } else {
                ((CheckBoxCell) child).setChecked(true, true);
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    public void setAdminLogFilterAlertDelegate(AdminLogFilterAlertDelegate adminLogFilterAlertDelegate) {
        this.delegate = adminLogFilterAlertDelegate;
    }

    /* access modifiers changed from: private */
    public void updateLayout() {
        if (this.listView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.listView;
            int paddingTop = recyclerListView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            this.containerView.invalidate();
            return;
        }
        int newOffset = 0;
        View child = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
        int top = child.getTop() - AndroidUtilities.dp(8.0f);
        if (top > 0 && holder != null && holder.getAdapterPosition() == 0) {
            newOffset = top;
        }
        if (this.scrollOffsetY != newOffset) {
            RecyclerListView recyclerListView2 = this.listView;
            this.scrollOffsetY = newOffset;
            recyclerListView2.setTopGlowOffset(newOffset);
            this.containerView.invalidate();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;

        public ListAdapter(Context context2) {
            this.context = context2;
        }

        public int getItemCount() {
            return (AdminLogFilterAlert.this.isMegagroup ? 9 : 7) + (AdminLogFilterAlert.this.currentAdmins != null ? AdminLogFilterAlert.this.currentAdmins.size() + 2 : 0);
        }

        public int getItemViewType(int position) {
            if (position < AdminLogFilterAlert.this.allAdminsRow - 1 || position == AdminLogFilterAlert.this.allAdminsRow) {
                return 0;
            }
            if (position == AdminLogFilterAlert.this.allAdminsRow - 1) {
                return 1;
            }
            return 2;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() != 1;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            FrameLayout view = null;
            if (viewType == 0) {
                view = new CheckBoxCell(this.context, 1, 21);
                view.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            } else if (viewType == 1) {
                ShadowSectionCell shadowSectionCell = new ShadowSectionCell(this.context, 18);
                view = new FrameLayout(this.context);
                view.addView(shadowSectionCell, LayoutHelper.createFrame(-1, -1.0f));
                view.setBackgroundColor(Theme.getColor(Theme.key_dialogBackgroundGray));
            } else if (viewType == 2) {
                view = new CheckBoxUserCell(this.context, true);
            }
            return new RecyclerListView.Holder(view);
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            int itemViewType = holder.getItemViewType();
            boolean z = true;
            if (itemViewType == 0) {
                CheckBoxCell cell = (CheckBoxCell) holder.itemView;
                if (position == 0) {
                    if (AdminLogFilterAlert.this.currentFilter != null) {
                        z = false;
                    }
                    cell.setChecked(z, false);
                } else if (position == AdminLogFilterAlert.this.restrictionsRow) {
                    if (AdminLogFilterAlert.this.currentFilter != null && (!AdminLogFilterAlert.this.currentFilter.kick || !AdminLogFilterAlert.this.currentFilter.ban || !AdminLogFilterAlert.this.currentFilter.unkick || !AdminLogFilterAlert.this.currentFilter.unban)) {
                        z = false;
                    }
                    cell.setChecked(z, false);
                } else if (position == AdminLogFilterAlert.this.adminsRow) {
                    if (AdminLogFilterAlert.this.currentFilter != null && (!AdminLogFilterAlert.this.currentFilter.promote || !AdminLogFilterAlert.this.currentFilter.demote)) {
                        z = false;
                    }
                    cell.setChecked(z, false);
                } else if (position == AdminLogFilterAlert.this.membersRow) {
                    if (AdminLogFilterAlert.this.currentFilter != null && (!AdminLogFilterAlert.this.currentFilter.invite || !AdminLogFilterAlert.this.currentFilter.join)) {
                        z = false;
                    }
                    cell.setChecked(z, false);
                } else if (position == AdminLogFilterAlert.this.infoRow) {
                    if (AdminLogFilterAlert.this.currentFilter != null && !AdminLogFilterAlert.this.currentFilter.info) {
                        z = false;
                    }
                    cell.setChecked(z, false);
                } else if (position == AdminLogFilterAlert.this.deleteRow) {
                    if (AdminLogFilterAlert.this.currentFilter != null && !AdminLogFilterAlert.this.currentFilter.delete) {
                        z = false;
                    }
                    cell.setChecked(z, false);
                } else if (position == AdminLogFilterAlert.this.editRow) {
                    if (AdminLogFilterAlert.this.currentFilter != null && !AdminLogFilterAlert.this.currentFilter.edit) {
                        z = false;
                    }
                    cell.setChecked(z, false);
                } else if (position == AdminLogFilterAlert.this.pinnedRow) {
                    if (AdminLogFilterAlert.this.currentFilter != null && !AdminLogFilterAlert.this.currentFilter.pinned) {
                        z = false;
                    }
                    cell.setChecked(z, false);
                } else if (position == AdminLogFilterAlert.this.leavingRow) {
                    if (AdminLogFilterAlert.this.currentFilter != null && !AdminLogFilterAlert.this.currentFilter.leave) {
                        z = false;
                    }
                    cell.setChecked(z, false);
                } else if (position == AdminLogFilterAlert.this.allAdminsRow) {
                    if (AdminLogFilterAlert.this.selectedAdmins != null) {
                        z = false;
                    }
                    cell.setChecked(z, false);
                }
            } else if (itemViewType == 2) {
                CheckBoxUserCell userCell = (CheckBoxUserCell) holder.itemView;
                int userId = ((TLRPC.ChannelParticipant) AdminLogFilterAlert.this.currentAdmins.get((position - AdminLogFilterAlert.this.allAdminsRow) - 1)).user_id;
                if (AdminLogFilterAlert.this.selectedAdmins != null && AdminLogFilterAlert.this.selectedAdmins.indexOfKey(userId) < 0) {
                    z = false;
                }
                userCell.setChecked(z, false);
            }
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int itemViewType = holder.getItemViewType();
            boolean z = false;
            boolean z2 = true;
            if (itemViewType == 0) {
                CheckBoxCell cell = (CheckBoxCell) holder.itemView;
                if (position == 0) {
                    String string = LocaleController.getString("EventLogFilterAll", R.string.EventLogFilterAll);
                    if (AdminLogFilterAlert.this.currentFilter == null) {
                        z = true;
                    }
                    cell.setText(string, "", z, true);
                } else if (position == AdminLogFilterAlert.this.restrictionsRow) {
                    String string2 = LocaleController.getString("EventLogFilterNewRestrictions", R.string.EventLogFilterNewRestrictions);
                    if (AdminLogFilterAlert.this.currentFilter == null || (AdminLogFilterAlert.this.currentFilter.kick && AdminLogFilterAlert.this.currentFilter.ban && AdminLogFilterAlert.this.currentFilter.unkick && AdminLogFilterAlert.this.currentFilter.unban)) {
                        z = true;
                    }
                    cell.setText(string2, "", z, true);
                } else if (position == AdminLogFilterAlert.this.adminsRow) {
                    String string3 = LocaleController.getString("EventLogFilterNewAdmins", R.string.EventLogFilterNewAdmins);
                    if (AdminLogFilterAlert.this.currentFilter == null || (AdminLogFilterAlert.this.currentFilter.promote && AdminLogFilterAlert.this.currentFilter.demote)) {
                        z = true;
                    }
                    cell.setText(string3, "", z, true);
                } else if (position == AdminLogFilterAlert.this.membersRow) {
                    String string4 = LocaleController.getString("EventLogFilterNewMembers", R.string.EventLogFilterNewMembers);
                    if (AdminLogFilterAlert.this.currentFilter == null || (AdminLogFilterAlert.this.currentFilter.invite && AdminLogFilterAlert.this.currentFilter.join)) {
                        z = true;
                    }
                    cell.setText(string4, "", z, true);
                } else if (position == AdminLogFilterAlert.this.infoRow) {
                    if (AdminLogFilterAlert.this.isMegagroup) {
                        String string5 = LocaleController.getString("EventLogFilterGroupInfo", R.string.EventLogFilterGroupInfo);
                        if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.info) {
                            z = true;
                        }
                        cell.setText(string5, "", z, true);
                        return;
                    }
                    String string6 = LocaleController.getString("EventLogFilterChannelInfo", R.string.EventLogFilterChannelInfo);
                    if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.info) {
                        z = true;
                    }
                    cell.setText(string6, "", z, true);
                } else if (position == AdminLogFilterAlert.this.deleteRow) {
                    String string7 = LocaleController.getString("EventLogFilterDeletedMessages", R.string.EventLogFilterDeletedMessages);
                    if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.delete) {
                        z = true;
                    }
                    cell.setText(string7, "", z, true);
                } else if (position == AdminLogFilterAlert.this.editRow) {
                    String string8 = LocaleController.getString("EventLogFilterEditedMessages", R.string.EventLogFilterEditedMessages);
                    if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.edit) {
                        z = true;
                    }
                    cell.setText(string8, "", z, true);
                } else if (position == AdminLogFilterAlert.this.pinnedRow) {
                    String string9 = LocaleController.getString("EventLogFilterPinnedMessages", R.string.EventLogFilterPinnedMessages);
                    if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.pinned) {
                        z = true;
                    }
                    cell.setText(string9, "", z, true);
                } else if (position == AdminLogFilterAlert.this.leavingRow) {
                    String string10 = LocaleController.getString("EventLogFilterLeavingMembers", R.string.EventLogFilterLeavingMembers);
                    if (AdminLogFilterAlert.this.currentFilter != null && !AdminLogFilterAlert.this.currentFilter.leave) {
                        z2 = false;
                    }
                    cell.setText(string10, "", z2, false);
                } else if (position == AdminLogFilterAlert.this.allAdminsRow) {
                    String string11 = LocaleController.getString("EventLogAllAdmins", R.string.EventLogAllAdmins);
                    if (AdminLogFilterAlert.this.selectedAdmins == null) {
                        z = true;
                    }
                    cell.setText(string11, "", z, true);
                }
            } else if (itemViewType == 2) {
                CheckBoxUserCell userCell = (CheckBoxUserCell) holder.itemView;
                int userId = ((TLRPC.ChannelParticipant) AdminLogFilterAlert.this.currentAdmins.get((position - AdminLogFilterAlert.this.allAdminsRow) - 1)).user_id;
                TLRPC.User user = MessagesController.getInstance(AdminLogFilterAlert.this.currentAccount).getUser(Integer.valueOf(userId));
                boolean z3 = AdminLogFilterAlert.this.selectedAdmins == null || AdminLogFilterAlert.this.selectedAdmins.indexOfKey(userId) >= 0;
                if (position != getItemCount() - 1) {
                    z = true;
                }
                userCell.setUser(user, z3, z);
            }
        }
    }
}
