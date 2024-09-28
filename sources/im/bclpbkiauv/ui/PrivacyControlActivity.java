package im.bclpbkiauv.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.PrivacyUsersActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.ChatMessageCell;
import im.bclpbkiauv.ui.cells.HeaderCell;
import im.bclpbkiauv.ui.cells.RadioCell;
import im.bclpbkiauv.ui.cells.ShadowSectionCell;
import im.bclpbkiauv.ui.cells.TextInfoPrivacyCell;
import im.bclpbkiauv.ui.cells.TextSettingsCell;
import im.bclpbkiauv.ui.components.HintView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.hui.contacts.AddGroupingUserActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class PrivacyControlActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    public static final int PRIVACY_RULES_TYPE_ADDED_BY_PHONE = 7;
    public static final int PRIVACY_RULES_TYPE_CALLS = 2;
    public static final int PRIVACY_RULES_TYPE_FORWARDS = 5;
    public static final int PRIVACY_RULES_TYPE_INVITE = 1;
    public static final int PRIVACY_RULES_TYPE_LASTSEEN = 0;
    public static final int PRIVACY_RULES_TYPE_MOMENT = 8;
    public static final int PRIVACY_RULES_TYPE_P2P = 3;
    public static final int PRIVACY_RULES_TYPE_PHONE = 6;
    public static final int PRIVACY_RULES_TYPE_PHOTO = 4;
    private static final int done_button = 1;
    private final int DONE;
    /* access modifiers changed from: private */
    public int alwaysShareRow;
    /* access modifiers changed from: private */
    public ArrayList<Integer> currentMinus;
    /* access modifiers changed from: private */
    public ArrayList<Integer> currentPlus;
    /* access modifiers changed from: private */
    public int currentSubType;
    /* access modifiers changed from: private */
    public int currentType;
    /* access modifiers changed from: private */
    public int detailRow;
    private View doneButton;
    /* access modifiers changed from: private */
    public boolean enableAnimation;
    /* access modifiers changed from: private */
    public int everybodyRow;
    private ArrayList<Integer> initialMinus;
    private ArrayList<Integer> initialPlus;
    private int initialRulesSubType;
    private int initialRulesType;
    /* access modifiers changed from: private */
    public int lastCheckedSubType;
    /* access modifiers changed from: private */
    public int lastCheckedType;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public MessageCell messageCell;
    /* access modifiers changed from: private */
    public int messageRow;
    /* access modifiers changed from: private */
    public int myContactsRow;
    /* access modifiers changed from: private */
    public int neverShareRow;
    /* access modifiers changed from: private */
    public int nobodyRow;
    /* access modifiers changed from: private */
    public int p2pDetailRow;
    /* access modifiers changed from: private */
    public int p2pRow;
    /* access modifiers changed from: private */
    public int p2pSectionRow;
    /* access modifiers changed from: private */
    public int phoneContactsRow;
    /* access modifiers changed from: private */
    public int phoneDetailRow;
    /* access modifiers changed from: private */
    public int phoneEverybodyRow;
    /* access modifiers changed from: private */
    public int phoneSectionRow;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int rulesType;
    /* access modifiers changed from: private */
    public int sectionRow;
    /* access modifiers changed from: private */
    public int shareDetailRow;
    /* access modifiers changed from: private */
    public int shareSectionRow;

    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            try {
                return super.onTouchEvent(widget, buffer, event);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                return false;
            }
        }
    }

    private class MessageCell extends FrameLayout {
        private Drawable backgroundDrawable;
        /* access modifiers changed from: private */
        public ChatMessageCell cell;
        /* access modifiers changed from: private */
        public HintView hintView;
        /* access modifiers changed from: private */
        public MessageObject messageObject;
        private Drawable shadowDrawable;
        final /* synthetic */ PrivacyControlActivity this$0;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public MessageCell(im.bclpbkiauv.ui.PrivacyControlActivity r18, android.content.Context r19) {
            /*
                r17 = this;
                r0 = r17
                r1 = r18
                r2 = r19
                r0.this$0 = r1
                r0.<init>(r2)
                r3 = 0
                r0.setWillNotDraw(r3)
                r0.setClipToPadding(r3)
                r4 = 2131231061(0x7f080155, float:1.8078192E38)
                java.lang.String r5 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r4 = im.bclpbkiauv.ui.actionbar.Theme.getThemedDrawable((android.content.Context) r2, (int) r4, (java.lang.String) r5)
                r0.shadowDrawable = r4
                r4 = 1093664768(0x41300000, float:11.0)
                int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
                int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
                r0.setPadding(r3, r5, r3, r4)
                long r4 = java.lang.System.currentTimeMillis()
                r6 = 1000(0x3e8, double:4.94E-321)
                long r4 = r4 / r6
                int r5 = (int) r4
                int r5 = r5 + -3600
                int r4 = r18.currentAccount
                im.bclpbkiauv.messenger.MessagesController r4 = im.bclpbkiauv.messenger.MessagesController.getInstance(r4)
                int r6 = r18.currentAccount
                im.bclpbkiauv.messenger.UserConfig r6 = im.bclpbkiauv.messenger.UserConfig.getInstance(r6)
                int r6 = r6.getClientUserId()
                java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
                im.bclpbkiauv.tgnet.TLRPC$User r4 = r4.getUser(r6)
                im.bclpbkiauv.tgnet.TLRPC$TL_message r6 = new im.bclpbkiauv.tgnet.TLRPC$TL_message
                r6.<init>()
                java.lang.String r7 = "PrivacyForwardsMessageLine"
                r8 = 2131693161(0x7f0f0e69, float:1.9015443E38)
                java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r8)
                r6.message = r7
                int r7 = r5 + 60
                r6.date = r7
                r7 = 1
                r6.dialog_id = r7
                r9 = 261(0x105, float:3.66E-43)
                r6.flags = r9
                r6.from_id = r3
                r9 = 1
                r6.id = r9
                im.bclpbkiauv.tgnet.TLRPC$TL_messageFwdHeader r10 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageFwdHeader
                r10.<init>()
                r6.fwd_from = r10
                im.bclpbkiauv.tgnet.TLRPC$MessageFwdHeader r10 = r6.fwd_from
                java.lang.String r11 = r4.first_name
                java.lang.String r12 = r4.last_name
                java.lang.String r11 = im.bclpbkiauv.messenger.ContactsController.formatName(r11, r12)
                r10.from_name = r11
                im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaEmpty r10 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaEmpty
                r10.<init>()
                r6.media = r10
                r6.out = r3
                im.bclpbkiauv.tgnet.TLRPC$TL_peerUser r10 = new im.bclpbkiauv.tgnet.TLRPC$TL_peerUser
                r10.<init>()
                r6.to_id = r10
                im.bclpbkiauv.tgnet.TLRPC$Peer r10 = r6.to_id
                int r11 = r18.currentAccount
                im.bclpbkiauv.messenger.UserConfig r11 = im.bclpbkiauv.messenger.UserConfig.getInstance(r11)
                int r11 = r11.getClientUserId()
                r10.user_id = r11
                im.bclpbkiauv.messenger.MessageObject r10 = new im.bclpbkiauv.messenger.MessageObject
                int r11 = r18.currentAccount
                r10.<init>(r11, r6, r9)
                r0.messageObject = r10
                r10.eventId = r7
                im.bclpbkiauv.messenger.MessageObject r7 = r0.messageObject
                r7.resetLayout()
                im.bclpbkiauv.ui.cells.ChatMessageCell r7 = new im.bclpbkiauv.ui.cells.ChatMessageCell
                r7.<init>(r2)
                r0.cell = r7
                im.bclpbkiauv.ui.PrivacyControlActivity$MessageCell$1 r8 = new im.bclpbkiauv.ui.PrivacyControlActivity$MessageCell$1
                r8.<init>(r1)
                r7.setDelegate(r8)
                im.bclpbkiauv.ui.cells.ChatMessageCell r1 = r0.cell
                r1.isChat = r3
                im.bclpbkiauv.ui.cells.ChatMessageCell r1 = r0.cell
                r1.setFullyDraw(r9)
                im.bclpbkiauv.ui.cells.ChatMessageCell r1 = r0.cell
                im.bclpbkiauv.messenger.MessageObject r7 = r0.messageObject
                r8 = 0
                r1.setMessageObject(r7, r8, r3, r3)
                im.bclpbkiauv.ui.cells.ChatMessageCell r1 = r0.cell
                r3 = -1
                r7 = -2
                android.widget.LinearLayout$LayoutParams r3 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r3, (int) r7)
                r0.addView(r1, r3)
                im.bclpbkiauv.ui.components.HintView r1 = new im.bclpbkiauv.ui.components.HintView
                r1.<init>(r2, r9, r9)
                r0.hintView = r1
                r10 = -1073741824(0xffffffffc0000000, float:-2.0)
                r11 = -1073741824(0xffffffffc0000000, float:-2.0)
                r12 = 51
                r13 = 1100480512(0x41980000, float:19.0)
                r14 = 0
                r15 = 1100480512(0x41980000, float:19.0)
                r16 = 0
                android.widget.FrameLayout$LayoutParams r3 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
                r0.addView(r1, r3)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.PrivacyControlActivity.MessageCell.<init>(im.bclpbkiauv.ui.PrivacyControlActivity, android.content.Context):void");
        }

        /* access modifiers changed from: protected */
        public void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            this.hintView.showForMessageCell(this.cell, false);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            Drawable newDrawable = Theme.getCachedWallpaperNonBlocking();
            if (newDrawable != null) {
                this.backgroundDrawable = newDrawable;
            }
            Drawable drawable = this.backgroundDrawable;
            if ((drawable instanceof ColorDrawable) || (drawable instanceof GradientDrawable)) {
                this.backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                this.backgroundDrawable.draw(canvas);
            } else if (!(drawable instanceof BitmapDrawable)) {
                super.onDraw(canvas);
            } else if (((BitmapDrawable) drawable).getTileModeX() == Shader.TileMode.REPEAT) {
                canvas.save();
                float scale = 2.0f / AndroidUtilities.density;
                canvas.scale(scale, scale);
                this.backgroundDrawable.setBounds(0, 0, (int) Math.ceil((double) (((float) getMeasuredWidth()) / scale)), (int) Math.ceil((double) (((float) getMeasuredHeight()) / scale)));
                this.backgroundDrawable.draw(canvas);
                canvas.restore();
            } else {
                int viewHeight = getMeasuredHeight();
                float scaleX = ((float) getMeasuredWidth()) / ((float) this.backgroundDrawable.getIntrinsicWidth());
                float scaleY = ((float) viewHeight) / ((float) this.backgroundDrawable.getIntrinsicHeight());
                float scale2 = scaleX < scaleY ? scaleY : scaleX;
                int width = (int) Math.ceil((double) (((float) this.backgroundDrawable.getIntrinsicWidth()) * scale2));
                int height = (int) Math.ceil((double) (((float) this.backgroundDrawable.getIntrinsicHeight()) * scale2));
                int x = (getMeasuredWidth() - width) / 2;
                int y = (viewHeight - height) / 2;
                canvas.save();
                canvas.clipRect(0, 0, width, getMeasuredHeight());
                this.backgroundDrawable.setBounds(x, y, x + width, y + height);
                this.backgroundDrawable.draw(canvas);
                canvas.restore();
            }
            this.shadowDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
        }

        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return false;
        }

        public boolean dispatchTouchEvent(MotionEvent ev) {
            return false;
        }

        /* access modifiers changed from: protected */
        public void dispatchSetPressed(boolean pressed) {
        }

        public boolean onTouchEvent(MotionEvent event) {
            return false;
        }

        public void invalidate() {
            super.invalidate();
            this.cell.invalidate();
        }
    }

    public PrivacyControlActivity(int type) {
        this(type, false);
    }

    public PrivacyControlActivity(int type, boolean load) {
        this.initialPlus = new ArrayList<>();
        this.initialMinus = new ArrayList<>();
        this.lastCheckedType = -1;
        this.lastCheckedSubType = -1;
        this.DONE = 1;
        this.rulesType = type;
        if (load) {
            ContactsController.getInstance(this.currentAccount).loadPrivacySettings();
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        checkPrivacy();
        updateRows();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.privacyRulesUpdated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.privacyRulesUpdated);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
    }

    public View createView(Context context) {
        if (this.rulesType == 5) {
            this.messageCell = new MessageCell(this, context);
        }
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        int i = this.rulesType;
        int visibility = 8;
        if (i == 6) {
            this.actionBar.setTitle(LocaleController.getString("PrivacyPhone", R.string.PrivacyPhone));
        } else if (i == 5) {
            this.actionBar.setTitle(LocaleController.getString("PrivacyForwards", R.string.PrivacyForwards));
        } else if (i == 4) {
            this.actionBar.setTitle(LocaleController.getString("PrivacyProfilePhoto", R.string.PrivacyProfilePhoto));
        } else if (i == 3) {
            this.actionBar.setTitle(LocaleController.getString("PrivacyP2P", R.string.PrivacyP2P));
        } else if (i == 2) {
            this.actionBar.setTitle(LocaleController.getString("Calls", R.string.Calls));
        } else if (i == 1) {
            this.actionBar.setTitle(LocaleController.getString("GroupsAndChannels", R.string.GroupsAndChannels));
        } else if (i == 8) {
            this.actionBar.setTitle(LocaleController.getString("FriendHub", R.string.FriendHub));
        } else {
            this.actionBar.setTitle(LocaleController.getString("PrivacyLastSeen", R.string.PrivacyLastSeen));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    if (PrivacyControlActivity.this.checkDiscard()) {
                        PrivacyControlActivity.this.finishFragment();
                    }
                } else if (id == 1) {
                    PrivacyControlActivity.this.processDone();
                }
            }
        });
        View view = this.doneButton;
        if (view != null) {
            visibility = view.getVisibility();
        }
        ActionBarMenuItem addItem = this.actionBar.createMenu().addItem(1, (CharSequence) LocaleController.getString("Done", R.string.Done));
        this.doneButton = addItem;
        addItem.setVisibility(visibility);
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f)));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                PrivacyControlActivity.this.lambda$createView$2$PrivacyControlActivity(view, i);
            }
        });
        setMessageText();
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$2$PrivacyControlActivity(View view, int position) {
        ArrayList<Integer> createFromArray;
        int i = 8;
        if (position == this.nobodyRow || position == this.everybodyRow || position == this.myContactsRow) {
            int newType = this.currentType;
            if (position == this.nobodyRow) {
                newType = 1;
            } else if (position == this.everybodyRow) {
                newType = 0;
            } else if (position == this.myContactsRow) {
                newType = 2;
            }
            int i2 = this.currentType;
            if (newType != i2) {
                this.enableAnimation = true;
                this.lastCheckedType = i2;
                this.currentType = newType;
                View view2 = this.doneButton;
                if (hasChanges()) {
                    i = 0;
                }
                view2.setVisibility(i);
                updateRows();
            }
        } else if (position == this.phoneContactsRow || position == this.phoneEverybodyRow) {
            int newType2 = this.currentSubType;
            if (position == this.phoneEverybodyRow) {
                newType2 = 0;
            } else if (position == this.phoneContactsRow) {
                newType2 = 1;
            }
            int i3 = this.currentSubType;
            if (newType2 != i3) {
                this.enableAnimation = true;
                this.lastCheckedSubType = i3;
                this.currentSubType = newType2;
                View view3 = this.doneButton;
                if (hasChanges()) {
                    i = 0;
                }
                view3.setVisibility(i);
                updateRows();
            }
        } else if (position == this.neverShareRow || position == this.alwaysShareRow) {
            if (position == this.neverShareRow) {
                createFromArray = this.currentMinus;
            } else {
                createFromArray = this.currentPlus;
            }
            if (createFromArray.isEmpty()) {
                AddGroupingUserActivity fragment = new AddGroupingUserActivity(new ArrayList<>(), 1, LocaleController.getString("EmpryUsersPlaceholder", R.string.EmpryUsersPlaceholder), false);
                fragment.setDelegate(new AddGroupingUserActivity.AddGroupingUserActivityDelegate(position) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void didSelectedContact(ArrayList arrayList) {
                        PrivacyControlActivity.this.lambda$null$0$PrivacyControlActivity(this.f$1, arrayList);
                    }
                });
                presentFragment(fragment);
                return;
            }
            int i4 = this.rulesType;
            PrivacyUsersActivity fragment2 = new PrivacyUsersActivity(createFromArray, (i4 == 0 || i4 == 8) ? false : true, position == this.alwaysShareRow, this.rulesType, this.currentType, this.currentSubType);
            fragment2.setDelegate(new PrivacyUsersActivity.PrivacyActivityDelegate(position) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void didUpdateUserList(ArrayList arrayList, boolean z) {
                    PrivacyControlActivity.this.lambda$null$1$PrivacyControlActivity(this.f$1, arrayList, z);
                }
            });
            presentFragment(fragment2);
        } else if (position == this.p2pRow) {
            presentFragment(new PrivacyControlActivity(3));
        }
    }

    public /* synthetic */ void lambda$null$0$PrivacyControlActivity(int position, ArrayList users) {
        ArrayList<Integer> ids = new ArrayList<>();
        if (users != null && users.size() > 0) {
            Iterator it = users.iterator();
            while (it.hasNext()) {
                TLRPC.User user = (TLRPC.User) it.next();
                if (user != null && user.id > 0) {
                    ids.add(Integer.valueOf(user.id));
                }
            }
        }
        if (position == this.neverShareRow) {
            this.currentMinus = ids;
            for (int a = 0; a < this.currentMinus.size(); a++) {
                this.currentPlus.remove(this.currentMinus.get(a));
            }
        } else {
            this.currentPlus = ids;
            for (int a2 = 0; a2 < this.currentPlus.size(); a2++) {
                this.currentMinus.remove(this.currentPlus.get(a2));
            }
        }
        this.lastCheckedType = -1;
        this.doneButton.setVisibility(hasChanges() ? 0 : 8);
        this.listAdapter.notifyDataSetChanged();
    }

    public /* synthetic */ void lambda$null$1$PrivacyControlActivity(int position, ArrayList ids, boolean added) {
        if (position == this.neverShareRow) {
            this.currentMinus = ids;
            if (added) {
                for (int a = 0; a < this.currentMinus.size(); a++) {
                    this.currentPlus.remove(this.currentMinus.get(a));
                }
            }
        } else {
            this.currentPlus = ids;
            if (added) {
                for (int a2 = 0; a2 < this.currentPlus.size(); a2++) {
                    this.currentMinus.remove(this.currentPlus.get(a2));
                }
            }
        }
        this.doneButton.setVisibility(hasChanges() ? 0 : 8);
        this.listAdapter.notifyDataSetChanged();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.privacyRulesUpdated) {
            checkPrivacy();
        } else if (id == NotificationCenter.emojiDidLoad) {
            this.listView.invalidateViews();
        }
    }

    private void applyCurrentPrivacySettings() {
        TLRPC.InputUser inputUser;
        TLRPC.InputUser inputUser2;
        TLRPC.TL_account_setPrivacy req = new TLRPC.TL_account_setPrivacy();
        int i = this.rulesType;
        if (i == 6) {
            req.key = new TLRPC.TL_inputPrivacyKeyPhoneNumber();
            if (this.currentType == 1) {
                TLRPC.TL_account_setPrivacy req2 = new TLRPC.TL_account_setPrivacy();
                req2.key = new TLRPC.TL_inputPrivacyKeyAddedByPhone();
                if (this.currentSubType == 0) {
                    req2.rules.add(new TLRPC.TL_inputPrivacyValueAllowAll());
                } else {
                    req2.rules.add(new TLRPC.TL_inputPrivacyValueAllowContacts());
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        PrivacyControlActivity.this.lambda$applyCurrentPrivacySettings$4$PrivacyControlActivity(tLObject, tL_error);
                    }
                }, 2);
            }
        } else if (i == 5) {
            req.key = new TLRPC.TL_inputPrivacyKeyForwards();
        } else if (i == 4) {
            req.key = new TLRPC.TL_inputPrivacyKeyProfilePhoto();
        } else if (i == 3) {
            req.key = new TLRPC.TL_inputPrivacyKeyPhoneP2P();
        } else if (i == 2) {
            req.key = new TLRPC.TL_inputPrivacyKeyPhoneCall();
        } else if (i == 1) {
            req.key = new TLRPC.TL_inputPrivacyKeyChatInvite();
        } else if (i == 8) {
            req.key = new TLRPC.TL_inputPrivacyKeyMoment();
        } else {
            req.key = new TLRPC.TL_inputPrivacyKeyStatusTimestamp();
        }
        if (this.currentType != 0 && this.currentPlus.size() > 0) {
            TLRPC.TL_inputPrivacyValueAllowUsers usersRule = new TLRPC.TL_inputPrivacyValueAllowUsers();
            TLRPC.TL_inputPrivacyValueAllowChatParticipants chatsRule = new TLRPC.TL_inputPrivacyValueAllowChatParticipants();
            for (int a = 0; a < this.currentPlus.size(); a++) {
                int id = this.currentPlus.get(a).intValue();
                if (id > 0) {
                    TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(id));
                    if (!(user == null || (inputUser2 = MessagesController.getInstance(this.currentAccount).getInputUser(user)) == null)) {
                        usersRule.users.add(inputUser2);
                    }
                } else {
                    chatsRule.chats.add(Integer.valueOf(-id));
                }
            }
            req.rules.add(usersRule);
            req.rules.add(chatsRule);
        }
        if (this.currentType != 1 && this.currentMinus.size() > 0) {
            TLRPC.TL_inputPrivacyValueDisallowUsers usersRule2 = new TLRPC.TL_inputPrivacyValueDisallowUsers();
            TLRPC.TL_inputPrivacyValueDisallowChatParticipants chatsRule2 = new TLRPC.TL_inputPrivacyValueDisallowChatParticipants();
            for (int a2 = 0; a2 < this.currentMinus.size(); a2++) {
                int id2 = this.currentMinus.get(a2).intValue();
                if (id2 > 0) {
                    TLRPC.User user2 = getMessagesController().getUser(Integer.valueOf(id2));
                    if (!(user2 == null || (inputUser = getMessagesController().getInputUser(user2)) == null)) {
                        usersRule2.users.add(inputUser);
                    }
                } else {
                    chatsRule2.chats.add(Integer.valueOf(-id2));
                }
            }
            req.rules.add(usersRule2);
            req.rules.add(chatsRule2);
        }
        int i2 = this.currentType;
        if (i2 == 0) {
            req.rules.add(new TLRPC.TL_inputPrivacyValueAllowAll());
        } else if (i2 == 1) {
            req.rules.add(new TLRPC.TL_inputPrivacyValueDisallowAll());
        } else if (i2 == 2) {
            req.rules.add(new TLRPC.TL_inputPrivacyValueAllowContacts());
        }
        AlertDialog progressDialog = null;
        if (getParentActivity() != null) {
            progressDialog = new AlertDialog(getParentActivity(), 3);
            progressDialog.setCanCancel(false);
            progressDialog.show();
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate(progressDialog) {
            private final /* synthetic */ AlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PrivacyControlActivity.this.lambda$applyCurrentPrivacySettings$6$PrivacyControlActivity(this.f$1, tLObject, tL_error);
            }
        }, 2);
    }

    public /* synthetic */ void lambda$applyCurrentPrivacySettings$4$PrivacyControlActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                PrivacyControlActivity.this.lambda$null$3$PrivacyControlActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$3$PrivacyControlActivity(TLRPC.TL_error error, TLObject response) {
        if (error == null) {
            ContactsController.getInstance(this.currentAccount).setPrivacyRules(((TLRPC.TL_account_privacyRules) response).rules, 7);
        }
    }

    public /* synthetic */ void lambda$applyCurrentPrivacySettings$6$PrivacyControlActivity(AlertDialog progressDialogFinal, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(progressDialogFinal, error, response) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ TLRPC.TL_error f$2;
            private final /* synthetic */ TLObject f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                PrivacyControlActivity.this.lambda$null$5$PrivacyControlActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$5$PrivacyControlActivity(AlertDialog progressDialogFinal, TLRPC.TL_error error, TLObject response) {
        if (progressDialogFinal != null) {
            try {
                progressDialogFinal.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        if (error == null) {
            TLRPC.TL_account_privacyRules privacyRules = (TLRPC.TL_account_privacyRules) response;
            MessagesController.getInstance(this.currentAccount).putUsers(privacyRules.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(privacyRules.chats, false);
            ContactsController.getInstance(this.currentAccount).setPrivacyRules(privacyRules.rules, this.rulesType);
            finishFragment();
            return;
        }
        showErrorAlert();
    }

    private void showErrorAlert() {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setMessage(LocaleController.getString("PrivacyFloodControlError", R.string.PrivacyFloodControlError));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
    }

    private void checkPrivacy() {
        this.currentPlus = new ArrayList<>();
        this.currentMinus = new ArrayList<>();
        ArrayList<TLRPC.PrivacyRule> privacyRules = ContactsController.getInstance(this.currentAccount).getPrivacyRules(this.rulesType);
        if (privacyRules == null || privacyRules.size() == 0) {
            this.currentType = 1;
        } else {
            int type = -1;
            for (int a = 0; a < privacyRules.size(); a++) {
                TLRPC.PrivacyRule rule = privacyRules.get(a);
                if (rule instanceof TLRPC.TL_privacyValueAllowChatParticipants) {
                    TLRPC.TL_privacyValueAllowChatParticipants privacyValueAllowChatParticipants = (TLRPC.TL_privacyValueAllowChatParticipants) rule;
                    int N = privacyValueAllowChatParticipants.chats.size();
                    for (int b = 0; b < N; b++) {
                        this.currentPlus.add(Integer.valueOf(-privacyValueAllowChatParticipants.chats.get(b).intValue()));
                    }
                } else if (rule instanceof TLRPC.TL_privacyValueDisallowChatParticipants) {
                    TLRPC.TL_privacyValueDisallowChatParticipants privacyValueDisallowChatParticipants = (TLRPC.TL_privacyValueDisallowChatParticipants) rule;
                    int N2 = privacyValueDisallowChatParticipants.chats.size();
                    for (int b2 = 0; b2 < N2; b2++) {
                        this.currentMinus.add(Integer.valueOf(-privacyValueDisallowChatParticipants.chats.get(b2).intValue()));
                    }
                } else if (rule instanceof TLRPC.TL_privacyValueAllowUsers) {
                    this.currentPlus.addAll(((TLRPC.TL_privacyValueAllowUsers) rule).users);
                } else if (rule instanceof TLRPC.TL_privacyValueDisallowUsers) {
                    this.currentMinus.addAll(((TLRPC.TL_privacyValueDisallowUsers) rule).users);
                } else if (type == -1) {
                    if (rule instanceof TLRPC.TL_privacyValueAllowAll) {
                        type = 0;
                    } else if (rule instanceof TLRPC.TL_privacyValueDisallowAll) {
                        type = 1;
                    } else {
                        type = 2;
                    }
                }
            }
            if (type == 0 || (type == -1 && this.currentMinus.size() > 0)) {
                this.currentType = 0;
            } else if (type == 2 || (type == -1 && this.currentMinus.size() > 0 && this.currentPlus.size() > 0)) {
                this.currentType = 2;
            } else if (type == 1 || (type == -1 && this.currentPlus.size() > 0)) {
                this.currentType = 1;
            }
            View view = this.doneButton;
            if (view != null) {
                view.setVisibility(8);
            }
        }
        this.initialPlus.clear();
        this.initialMinus.clear();
        this.initialRulesType = this.currentType;
        this.initialPlus.addAll(this.currentPlus);
        this.initialMinus.addAll(this.currentMinus);
        if (this.rulesType == 6) {
            ArrayList<TLRPC.PrivacyRule> privacyRules2 = ContactsController.getInstance(this.currentAccount).getPrivacyRules(7);
            if (privacyRules2 != null && privacyRules2.size() != 0) {
                int a2 = 0;
                while (true) {
                    if (a2 >= privacyRules2.size()) {
                        break;
                    }
                    TLRPC.PrivacyRule rule2 = privacyRules2.get(a2);
                    if (rule2 instanceof TLRPC.TL_privacyValueAllowAll) {
                        this.currentSubType = 0;
                        break;
                    } else if (rule2 instanceof TLRPC.TL_privacyValueDisallowAll) {
                        this.currentSubType = 2;
                        break;
                    } else if (rule2 instanceof TLRPC.TL_privacyValueAllowContacts) {
                        this.currentSubType = 1;
                        break;
                    } else {
                        a2++;
                    }
                }
            } else {
                this.currentSubType = 0;
            }
            this.initialRulesSubType = this.currentSubType;
        }
        updateRows();
    }

    private boolean hasChanges() {
        int i = this.initialRulesType;
        int i2 = this.currentType;
        if (i != i2) {
            return true;
        }
        if ((this.rulesType == 6 && i2 == 1 && this.initialRulesSubType != this.currentSubType) || this.initialMinus.size() != this.currentMinus.size() || this.initialPlus.size() != this.currentPlus.size()) {
            return true;
        }
        Collections.sort(this.initialPlus);
        Collections.sort(this.currentPlus);
        if (!this.initialPlus.equals(this.currentPlus)) {
            return true;
        }
        Collections.sort(this.initialMinus);
        Collections.sort(this.currentMinus);
        if (!this.initialMinus.equals(this.currentMinus)) {
            return true;
        }
        return false;
    }

    private void updateRows() {
        this.rowCount = 0;
        if (this.rulesType == 5) {
            this.rowCount = 0 + 1;
            this.messageRow = 0;
        } else {
            this.messageRow = -1;
        }
        int i = this.rowCount;
        int i2 = i + 1;
        this.rowCount = i2;
        this.sectionRow = i;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.everybodyRow = i2;
        this.rowCount = i3 + 1;
        this.myContactsRow = i3;
        int i4 = this.rulesType;
        if (i4 == 0 || i4 == 2 || i4 == 3 || i4 == 5 || i4 == 6 || i4 == 8) {
            int i5 = this.rowCount;
            this.rowCount = i5 + 1;
            this.nobodyRow = i5;
        } else {
            this.nobodyRow = -1;
        }
        if (this.rulesType == 6 && this.currentType == 1) {
            int i6 = this.rowCount;
            int i7 = i6 + 1;
            this.rowCount = i7;
            this.phoneDetailRow = i6;
            int i8 = i7 + 1;
            this.rowCount = i8;
            this.phoneSectionRow = i7;
            int i9 = i8 + 1;
            this.rowCount = i9;
            this.phoneEverybodyRow = i8;
            this.rowCount = i9 + 1;
            this.phoneContactsRow = i9;
        } else {
            this.phoneDetailRow = -1;
            this.phoneSectionRow = -1;
            this.phoneEverybodyRow = -1;
            this.phoneContactsRow = -1;
        }
        int i10 = this.rowCount;
        int i11 = i10 + 1;
        this.rowCount = i11;
        this.detailRow = i10;
        this.rowCount = i11 + 1;
        this.shareSectionRow = i11;
        int i12 = this.currentType;
        if (i12 == 1 || i12 == 2) {
            int i13 = this.rowCount;
            this.rowCount = i13 + 1;
            this.alwaysShareRow = i13;
        } else {
            this.alwaysShareRow = -1;
        }
        int i14 = this.currentType;
        if (i14 == 0 || i14 == 2) {
            int i15 = this.rowCount;
            this.rowCount = i15 + 1;
            this.neverShareRow = i15;
        } else {
            this.neverShareRow = -1;
        }
        int i16 = this.rowCount;
        int i17 = i16 + 1;
        this.rowCount = i17;
        this.shareDetailRow = i16;
        if (this.rulesType == 2) {
            int i18 = i17 + 1;
            this.rowCount = i18;
            this.p2pSectionRow = i17;
            int i19 = i18 + 1;
            this.rowCount = i19;
            this.p2pRow = i18;
            this.rowCount = i19 + 1;
            this.p2pDetailRow = i19;
        } else {
            this.p2pSectionRow = -1;
            this.p2pRow = -1;
            this.p2pDetailRow = -1;
        }
        setMessageText();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    private void setMessageText() {
        MessageCell messageCell2 = this.messageCell;
        if (messageCell2 != null) {
            int i = this.currentType;
            if (i == 0) {
                messageCell2.hintView.setOverrideText(LocaleController.getString("PrivacyForwardsEverybody", R.string.PrivacyForwardsEverybody));
                this.messageCell.messageObject.messageOwner.fwd_from.from_id = 1;
            } else if (i == 1) {
                messageCell2.hintView.setOverrideText(LocaleController.getString("PrivacyForwardsNobody", R.string.PrivacyForwardsNobody));
                this.messageCell.messageObject.messageOwner.fwd_from.from_id = 0;
            } else {
                messageCell2.hintView.setOverrideText(LocaleController.getString("PrivacyForwardsContacts", R.string.PrivacyForwardsContacts));
                this.messageCell.messageObject.messageOwner.fwd_from.from_id = 1;
            }
            this.messageCell.cell.forceResetMessageObject();
        }
    }

    public void onResume() {
        super.onResume();
        this.lastCheckedType = -1;
        this.lastCheckedSubType = -1;
        this.enableAnimation = false;
    }

    public boolean onBackPressed() {
        return checkDiscard();
    }

    /* access modifiers changed from: private */
    public void processDone() {
        if (getParentActivity() != null) {
            if (this.currentType != 0 && this.rulesType == 0) {
                SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                if (!preferences.getBoolean("privacyAlertShowed", false)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    if (this.rulesType == 1) {
                        builder.setMessage(LocaleController.getString("WhoCanAddMeInfo", R.string.WhoCanAddMeInfo));
                    } else {
                        builder.setMessage(LocaleController.getString("CustomHelp", R.string.CustomHelp));
                    }
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener(preferences) {
                        private final /* synthetic */ SharedPreferences f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            PrivacyControlActivity.this.lambda$processDone$7$PrivacyControlActivity(this.f$1, dialogInterface, i);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                    showDialog(builder.create());
                    return;
                }
            }
            applyCurrentPrivacySettings();
        }
    }

    public /* synthetic */ void lambda$processDone$7$PrivacyControlActivity(SharedPreferences preferences, DialogInterface dialogInterface, int i) {
        applyCurrentPrivacySettings();
        preferences.edit().putBoolean("privacyAlertShowed", true).commit();
    }

    /* access modifiers changed from: private */
    public boolean checkDiscard() {
        if (this.doneButton.getVisibility() != 0) {
            return true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("UserRestrictionsApplyChanges", R.string.UserRestrictionsApplyChanges));
        builder.setMessage(LocaleController.getString("PrivacySettingsChangedAlert", R.string.PrivacySettingsChangedAlert));
        builder.setPositiveButton(LocaleController.getString("ApplyTheme", R.string.ApplyTheme), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                PrivacyControlActivity.this.lambda$checkDiscard$8$PrivacyControlActivity(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("PassportDiscard", R.string.PassportDiscard), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                PrivacyControlActivity.this.lambda$checkDiscard$9$PrivacyControlActivity(dialogInterface, i);
            }
        });
        showDialog(builder.create());
        return false;
    }

    public /* synthetic */ void lambda$checkDiscard$8$PrivacyControlActivity(DialogInterface dialogInterface, int i) {
        processDone();
    }

    public /* synthetic */ void lambda$checkDiscard$9$PrivacyControlActivity(DialogInterface dialog, int which) {
        finishFragment();
    }

    public boolean canBeginSlide() {
        return checkDiscard();
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == PrivacyControlActivity.this.nobodyRow || position == PrivacyControlActivity.this.everybodyRow || position == PrivacyControlActivity.this.myContactsRow || position == PrivacyControlActivity.this.neverShareRow || position == PrivacyControlActivity.this.alwaysShareRow || (position == PrivacyControlActivity.this.p2pRow && !ContactsController.getInstance(PrivacyControlActivity.this.currentAccount).getLoadingPrivicyInfo(3));
        }

        public int getItemCount() {
            return PrivacyControlActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 0) {
                View view2 = new TextSettingsCell(this.mContext);
                view2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                view = view2;
            } else if (viewType == 1) {
                view = new TextInfoPrivacyCell(this.mContext);
            } else if (viewType == 2) {
                View view3 = new HeaderCell(this.mContext);
                view3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                view = view3;
            } else if (viewType == 3) {
                View view4 = new RadioCell(this.mContext);
                view4.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                view = view4;
            } else if (viewType != 4) {
                view = new ShadowSectionCell(this.mContext);
                view.setBackgroundColor(0);
            } else {
                view = PrivacyControlActivity.this.messageCell;
            }
            return new RecyclerListView.Holder(view);
        }

        private int getUsersCount(ArrayList<Integer> arrayList) {
            int count = 0;
            for (int a = 0; a < arrayList.size(); a++) {
                int id = arrayList.get(a).intValue();
                if (id > 0) {
                    count++;
                } else {
                    TLRPC.Chat chat = PrivacyControlActivity.this.getMessagesController().getChat(Integer.valueOf(-id));
                    if (chat != null) {
                        count += chat.participants_count;
                    }
                }
            }
            return count;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String value;
            String value2;
            String value3;
            int i;
            RecyclerView.ViewHolder viewHolder = holder;
            int i2 = position;
            int itemViewType = holder.getItemViewType();
            if (itemViewType == 0) {
                TextSettingsCell textCell = (TextSettingsCell) viewHolder.itemView;
                textCell.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                if (i2 == PrivacyControlActivity.this.alwaysShareRow) {
                    if (PrivacyControlActivity.this.currentPlus.size() != 0) {
                        value3 = LocaleController.formatPluralString("Users", getUsersCount(PrivacyControlActivity.this.currentPlus));
                    } else {
                        value3 = LocaleController.getString("EmpryUsersPlaceholder", R.string.EmpryUsersPlaceholder);
                    }
                    if (PrivacyControlActivity.this.rulesType == 0 || PrivacyControlActivity.this.rulesType == 8) {
                        i = -1;
                        textCell.setTextAndValue(LocaleController.getString("AlwaysShareWith", R.string.AlwaysShareWith), value3, PrivacyControlActivity.this.neverShareRow != -1);
                    } else {
                        textCell.setTextAndValue(LocaleController.getString("AlwaysAllow", R.string.AlwaysAllow), value3, PrivacyControlActivity.this.neverShareRow != -1);
                        i = -1;
                    }
                    if (PrivacyControlActivity.this.neverShareRow != i) {
                        textCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    }
                } else if (i2 == PrivacyControlActivity.this.neverShareRow) {
                    if (PrivacyControlActivity.this.currentMinus.size() != 0) {
                        value2 = LocaleController.formatPluralString("Users", getUsersCount(PrivacyControlActivity.this.currentMinus));
                    } else {
                        value2 = LocaleController.getString("EmpryUsersPlaceholder", R.string.EmpryUsersPlaceholder);
                    }
                    if (PrivacyControlActivity.this.rulesType == 0 || PrivacyControlActivity.this.rulesType == 8) {
                        textCell.setTextAndValue(LocaleController.getString("NeverShareWith", R.string.NeverShareWith), value2, false);
                    } else {
                        textCell.setTextAndValue(LocaleController.getString("NeverAllow", R.string.NeverAllow), value2, false);
                    }
                } else if (i2 == PrivacyControlActivity.this.p2pRow) {
                    if (ContactsController.getInstance(PrivacyControlActivity.this.currentAccount).getLoadingPrivicyInfo(3)) {
                        value = LocaleController.getString("Loading", R.string.Loading);
                    } else {
                        value = PrivacySettingsActivity.formatRulesString(PrivacyControlActivity.this.getAccountInstance(), 3);
                    }
                    textCell.setTextAndValue(LocaleController.getString("PrivacyP2P2", R.string.PrivacyP2P2), value, false);
                }
            } else if (itemViewType == 1) {
                TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                if (i2 == PrivacyControlActivity.this.detailRow) {
                    if (PrivacyControlActivity.this.rulesType == 6) {
                        if (PrivacyControlActivity.this.currentType == 1 && PrivacyControlActivity.this.currentSubType == 1) {
                            privacyCell.setText(LocaleController.getString("PrivacyPhoneInfo3", R.string.PrivacyPhoneInfo3));
                        } else {
                            privacyCell.setText(LocaleController.getString("PrivacyPhoneInfo", R.string.PrivacyPhoneInfo));
                        }
                    } else if (PrivacyControlActivity.this.rulesType == 5) {
                        privacyCell.setText(LocaleController.getString("PrivacyForwardsInfo", R.string.PrivacyForwardsInfo));
                    } else if (PrivacyControlActivity.this.rulesType == 4) {
                        privacyCell.setText(LocaleController.getString("PrivacyProfilePhotoInfo", R.string.PrivacyProfilePhotoInfo));
                    } else if (PrivacyControlActivity.this.rulesType == 3) {
                        privacyCell.setText(LocaleController.getString("PrivacyCallsP2PHelp", R.string.PrivacyCallsP2PHelp));
                    } else if (PrivacyControlActivity.this.rulesType == 2) {
                        privacyCell.setText(LocaleController.getString("WhoCanCallMeInfo", R.string.WhoCanCallMeInfo));
                    } else if (PrivacyControlActivity.this.rulesType == 1) {
                        privacyCell.setText(LocaleController.getString("WhoCanAddMeInfo", R.string.WhoCanAddMeInfo));
                    } else if (PrivacyControlActivity.this.rulesType == 8) {
                        privacyCell.setText(LocaleController.getString("PrivacyExceptions", R.string.PrivacyExceptions));
                    } else {
                        privacyCell.setText(LocaleController.getString("CustomHelp", R.string.CustomHelp));
                    }
                } else if (i2 != PrivacyControlActivity.this.shareDetailRow) {
                    int access$2700 = PrivacyControlActivity.this.p2pDetailRow;
                } else if (PrivacyControlActivity.this.rulesType == 6) {
                    privacyCell.setText(LocaleController.getString("PrivacyPhoneInfo2", R.string.PrivacyPhoneInfo2));
                } else if (PrivacyControlActivity.this.rulesType == 5) {
                    privacyCell.setText(LocaleController.getString("PrivacyForwardsInfo2", R.string.PrivacyForwardsInfo2));
                } else if (PrivacyControlActivity.this.rulesType == 4) {
                    privacyCell.setText(LocaleController.getString("PrivacyProfilePhotoInfo2", R.string.PrivacyProfilePhotoInfo2));
                } else if (PrivacyControlActivity.this.rulesType == 3) {
                    privacyCell.setText(LocaleController.getString("CustomP2PInfo", R.string.CustomP2PInfo));
                } else if (PrivacyControlActivity.this.rulesType == 2) {
                    privacyCell.setText(LocaleController.getString("CustomCallInfo", R.string.CustomCallInfo));
                } else if (PrivacyControlActivity.this.rulesType == 1) {
                    privacyCell.setText(LocaleController.getString("CustomShareInfo", R.string.CustomShareInfo));
                } else {
                    privacyCell.setText(LocaleController.getString("CustomShareSettingsHelp", R.string.CustomShareSettingsHelp));
                }
            } else if (itemViewType == 2) {
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                if (i2 == PrivacyControlActivity.this.sectionRow) {
                    if (PrivacyControlActivity.this.rulesType == 6) {
                        headerCell.setText(LocaleController.getString("PrivacyPhoneTitle", R.string.PrivacyPhoneTitle));
                    } else if (PrivacyControlActivity.this.rulesType == 5) {
                        headerCell.setText(LocaleController.getString("PrivacyForwardsTitle", R.string.PrivacyForwardsTitle));
                    } else if (PrivacyControlActivity.this.rulesType == 4) {
                        headerCell.setText(LocaleController.getString("PrivacyProfilePhotoTitle", R.string.PrivacyProfilePhotoTitle));
                    } else if (PrivacyControlActivity.this.rulesType == 3) {
                        headerCell.setText(LocaleController.getString("P2PEnabledWith", R.string.P2PEnabledWith));
                    } else if (PrivacyControlActivity.this.rulesType == 2) {
                        headerCell.setText(LocaleController.getString("WhoCanCallMe", R.string.WhoCanCallMe));
                    } else if (PrivacyControlActivity.this.rulesType == 1) {
                        headerCell.setText(LocaleController.getString("WhoCanAddMe", R.string.WhoCanAddMe));
                    } else if (PrivacyControlActivity.this.rulesType == 8) {
                        headerCell.setText(LocaleController.getString("WhoCanViewMoment", R.string.WhoCanViewMoment));
                    } else {
                        headerCell.setText(LocaleController.getString("LastSeenTitle", R.string.LastSeenTitle));
                    }
                } else if (i2 == PrivacyControlActivity.this.shareSectionRow) {
                    headerCell.setText(LocaleController.getString("AddExceptions", R.string.AddExceptions));
                } else if (i2 == PrivacyControlActivity.this.p2pSectionRow) {
                    headerCell.setText(LocaleController.getString("PrivacyP2PHeader", R.string.PrivacyP2PHeader));
                } else if (i2 == PrivacyControlActivity.this.phoneSectionRow) {
                    headerCell.setText(LocaleController.getString("PrivacyPhoneTitle2", R.string.PrivacyPhoneTitle2));
                }
                headerCell.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f, Theme.getColor(Theme.key_windowBackgroundWhite)));
            } else if (itemViewType == 3) {
                RadioCell radioCell = (RadioCell) viewHolder.itemView;
                if (i2 == PrivacyControlActivity.this.everybodyRow || i2 == PrivacyControlActivity.this.myContactsRow || i2 == PrivacyControlActivity.this.nobodyRow) {
                    int checkedType = 0;
                    if (i2 == PrivacyControlActivity.this.everybodyRow) {
                        if (PrivacyControlActivity.this.rulesType == 3) {
                            radioCell.setText(LocaleController.getString("P2PEverybody", R.string.P2PEverybody), PrivacyControlActivity.this.lastCheckedType == 0, true);
                        } else {
                            radioCell.setText(LocaleController.getString("LastSeenEverybody", R.string.LastSeenEverybody), PrivacyControlActivity.this.lastCheckedType == 0, true);
                        }
                        checkedType = 0;
                    } else if (i2 == PrivacyControlActivity.this.myContactsRow) {
                        if (PrivacyControlActivity.this.rulesType == 3) {
                            radioCell.setText(LocaleController.getString("P2PContacts", R.string.P2PContacts), PrivacyControlActivity.this.lastCheckedType == 2, PrivacyControlActivity.this.nobodyRow != -1);
                        } else {
                            radioCell.setText(LocaleController.getString("LastSeenContacts", R.string.LastSeenContacts), PrivacyControlActivity.this.lastCheckedType == 2, PrivacyControlActivity.this.nobodyRow != -1);
                        }
                        checkedType = 2;
                        if (PrivacyControlActivity.this.nobodyRow == -1) {
                            radioCell.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                        } else {
                            radioCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                        }
                    } else if (i2 == PrivacyControlActivity.this.nobodyRow) {
                        if (PrivacyControlActivity.this.rulesType == 3) {
                            radioCell.setText(LocaleController.getString("P2PNobody", R.string.P2PNobody), PrivacyControlActivity.this.lastCheckedType == 1, false);
                        } else {
                            radioCell.setText(LocaleController.getString("LastSeenNobody", R.string.LastSeenNobody), PrivacyControlActivity.this.lastCheckedType == 1, false);
                        }
                        checkedType = 1;
                        radioCell.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                    }
                    if (PrivacyControlActivity.this.lastCheckedType == checkedType) {
                        radioCell.setChecked(false, PrivacyControlActivity.this.enableAnimation);
                    } else if (PrivacyControlActivity.this.currentType == checkedType) {
                        radioCell.setChecked(true, PrivacyControlActivity.this.enableAnimation);
                    }
                } else {
                    int checkedType2 = 0;
                    if (i2 == PrivacyControlActivity.this.phoneContactsRow) {
                        radioCell.setText(LocaleController.getString("LastSeenContacts", R.string.LastSeenContacts), PrivacyControlActivity.this.lastCheckedSubType == 1, false);
                        checkedType2 = 1;
                        radioCell.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                    } else if (i2 == PrivacyControlActivity.this.phoneEverybodyRow) {
                        radioCell.setText(LocaleController.getString("LastSeenEverybody", R.string.LastSeenEverybody), PrivacyControlActivity.this.lastCheckedSubType == 0, true);
                        checkedType2 = 0;
                    }
                    if (PrivacyControlActivity.this.lastCheckedSubType == checkedType2) {
                        radioCell.setChecked(false, PrivacyControlActivity.this.enableAnimation);
                    } else if (PrivacyControlActivity.this.currentSubType == checkedType2) {
                        radioCell.setChecked(true, PrivacyControlActivity.this.enableAnimation);
                    }
                }
            }
        }

        public int getItemViewType(int position) {
            if (position == PrivacyControlActivity.this.alwaysShareRow || position == PrivacyControlActivity.this.neverShareRow || position == PrivacyControlActivity.this.p2pRow) {
                return 0;
            }
            if (position == PrivacyControlActivity.this.shareDetailRow || position == PrivacyControlActivity.this.detailRow || position == PrivacyControlActivity.this.p2pDetailRow) {
                return 1;
            }
            if (position == PrivacyControlActivity.this.sectionRow || position == PrivacyControlActivity.this.shareSectionRow || position == PrivacyControlActivity.this.p2pSectionRow || position == PrivacyControlActivity.this.phoneSectionRow) {
                return 2;
            }
            if (position == PrivacyControlActivity.this.everybodyRow || position == PrivacyControlActivity.this.myContactsRow || position == PrivacyControlActivity.this.nobodyRow || position == PrivacyControlActivity.this.phoneEverybodyRow || position == PrivacyControlActivity.this.phoneContactsRow) {
                return 3;
            }
            if (position == PrivacyControlActivity.this.messageRow) {
                return 4;
            }
            if (position == PrivacyControlActivity.this.phoneDetailRow) {
                return 5;
            }
            return 0;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        RecyclerListView recyclerListView = this.listView;
        RecyclerListView recyclerListView2 = recyclerListView;
        RecyclerListView recyclerListView3 = this.listView;
        RecyclerListView recyclerListView4 = recyclerListView3;
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, RadioCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteValueText), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText4), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueHeader), new ThemeDescription((View) this.listView, 0, new Class[]{RadioCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) recyclerListView2, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_radioBackground), new ThemeDescription((View) recyclerListView4, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_radioBackgroundChecked), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inBubble), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inBubbleSelected), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgInShadowDrawable, Theme.chat_msgInMediaShadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inBubbleShadow), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outBubble), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outBubbleSelected), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutShadowDrawable, Theme.chat_msgOutMediaShadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outBubbleShadow), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_messageTextIn), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_messageTextOut), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outSentCheck), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outSentCheckSelected), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outSentCheckRead), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outSentCheckReadSelected), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_mediaSentCheck), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inReplyLine), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outReplyLine), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inReplyNameText), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outReplyNameText), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inReplyMessageText), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outReplyMessageText), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inReplyMediaMessageSelectedText), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outReplyMediaMessageSelectedText), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inTimeText), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outTimeText), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_inTimeSelectedText), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_outTimeSelectedText)};
    }
}
