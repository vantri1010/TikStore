package im.bclpbkiauv.ui;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.CallLogActivity;
import im.bclpbkiauv.ui.ContactsActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.LoadingCell;
import im.bclpbkiauv.ui.cells.LocationCell;
import im.bclpbkiauv.ui.cells.ProfileSearchCell;
import im.bclpbkiauv.ui.cells.TextInfoPrivacyCell;
import im.bclpbkiauv.ui.components.CombinedDrawable;
import im.bclpbkiauv.ui.components.EmptyTextProgressView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.voip.VoIPHelper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Deprecated
public class CallLogActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int TYPE_IN = 1;
    private static final int TYPE_MISSED = 2;
    private static final int TYPE_OUT = 0;
    /* access modifiers changed from: private */
    public View.OnClickListener callBtnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            VoIPHelper.startCall(CallLogActivity.this.lastCallUser = ((CallLogRow) v.getTag()).user, CallLogActivity.this.getParentActivity(), (TLRPC.UserFull) null);
        }
    };
    /* access modifiers changed from: private */
    public ArrayList<CallLogRow> calls = new ArrayList<>();
    private EmptyTextProgressView emptyView;
    /* access modifiers changed from: private */
    public boolean endReached;
    private boolean firstLoaded;
    /* access modifiers changed from: private */
    public ImageView floatingButton;
    private boolean floatingHidden;
    private final AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();
    private Drawable greenDrawable;
    private Drawable greenDrawable2;
    /* access modifiers changed from: private */
    public ImageSpan iconIn;
    /* access modifiers changed from: private */
    public ImageSpan iconMissed;
    /* access modifiers changed from: private */
    public ImageSpan iconOut;
    /* access modifiers changed from: private */
    public TLRPC.User lastCallUser;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public ListAdapter listViewAdapter;
    /* access modifiers changed from: private */
    public boolean loading;
    /* access modifiers changed from: private */
    public int prevPosition;
    /* access modifiers changed from: private */
    public int prevTop;
    private Drawable redDrawable;
    /* access modifiers changed from: private */
    public boolean scrollUpdated;

    public void didReceivedNotification(int id, int account, Object... args) {
        ListAdapter listAdapter;
        if (id != NotificationCenter.didReceiveNewMessages || !this.firstLoaded) {
            if (id == NotificationCenter.messagesDeleted && this.firstLoaded && !args[2].booleanValue()) {
                boolean didChange = false;
                ArrayList<Integer> ids = args[0];
                Iterator<CallLogRow> itrtr = this.calls.iterator();
                while (itrtr.hasNext()) {
                    CallLogRow row = itrtr.next();
                    Iterator<TLRPC.Message> msgs = row.calls.iterator();
                    while (msgs.hasNext()) {
                        if (ids.contains(Integer.valueOf(msgs.next().id))) {
                            didChange = true;
                            msgs.remove();
                        }
                    }
                    if (row.calls.size() == 0) {
                        itrtr.remove();
                    }
                }
                if (didChange && (listAdapter = this.listViewAdapter) != null) {
                    listAdapter.notifyDataSetChanged();
                }
            }
        } else if (!args[2].booleanValue()) {
            Iterator<MessageObject> it = args[1].iterator();
            while (it.hasNext()) {
                MessageObject msg = it.next();
                if (msg.messageOwner.action instanceof TLRPC.TL_messageActionPhoneCall) {
                    int userID = msg.messageOwner.from_id == UserConfig.getInstance(this.currentAccount).getClientUserId() ? msg.messageOwner.to_id.user_id : msg.messageOwner.from_id;
                    int callType = msg.messageOwner.from_id == UserConfig.getInstance(this.currentAccount).getClientUserId() ? 0 : 1;
                    TLRPC.PhoneCallDiscardReason reason = msg.messageOwner.action.reason;
                    if (callType == 1 && ((reason instanceof TLRPC.TL_phoneCallDiscardReasonMissed) || (reason instanceof TLRPC.TL_phoneCallDiscardReasonBusy))) {
                        callType = 2;
                    }
                    if (this.calls.size() > 0) {
                        CallLogRow topRow = this.calls.get(0);
                        if (topRow.user.id == userID && topRow.type == callType) {
                            topRow.calls.add(0, msg.messageOwner);
                            this.listViewAdapter.notifyItemChanged(0);
                        }
                    }
                    CallLogRow row2 = new CallLogRow();
                    row2.calls = new ArrayList();
                    row2.calls.add(msg.messageOwner);
                    row2.user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(userID));
                    row2.type = callType;
                    this.calls.add(0, row2);
                    this.listViewAdapter.notifyItemInserted(0);
                }
            }
        }
    }

    private class CustomCell extends FrameLayout {
        /* access modifiers changed from: private */
        public ImageView imageView;
        /* access modifiers changed from: private */
        public ProfileSearchCell profileSearchCell;

        public CustomCell(Context context) {
            super(context);
            setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            ProfileSearchCell profileSearchCell2 = new ProfileSearchCell(context);
            this.profileSearchCell = profileSearchCell2;
            profileSearchCell2.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(32.0f) : 0, 0, LocaleController.isRTL ? 0 : AndroidUtilities.dp(32.0f), 0);
            this.profileSearchCell.setSublabelOffset(AndroidUtilities.dp(LocaleController.isRTL ? 2.0f : -2.0f), -AndroidUtilities.dp(4.0f));
            addView(this.profileSearchCell, LayoutHelper.createFrame(-1, -1.0f));
            ImageView imageView2 = new ImageView(context);
            this.imageView = imageView2;
            imageView2.setImageResource(R.drawable.profile_phone);
            this.imageView.setAlpha(214);
            this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addButton), PorterDuff.Mode.MULTIPLY));
            this.imageView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 1));
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.setOnClickListener(CallLogActivity.this.callBtnClickListener);
            this.imageView.setContentDescription(LocaleController.getString("Call", R.string.Call));
            addView(this.imageView, LayoutHelper.createFrame(48.0f, 48.0f, (LocaleController.isRTL ? 3 : 5) | 16, 8.0f, 0.0f, 8.0f, 0.0f));
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getCalls(0, 50);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didReceiveNewMessages);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagesDeleted);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didReceiveNewMessages);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagesDeleted);
    }

    public View createView(Context context) {
        Context context2 = context;
        Drawable mutate = getParentActivity().getResources().getDrawable(R.drawable.ic_call_made_green_18dp).mutate();
        this.greenDrawable = mutate;
        mutate.setBounds(0, 0, mutate.getIntrinsicWidth(), this.greenDrawable.getIntrinsicHeight());
        this.greenDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_calls_callReceivedGreenIcon), PorterDuff.Mode.MULTIPLY));
        this.iconOut = new ImageSpan(this.greenDrawable, 0);
        Drawable mutate2 = getParentActivity().getResources().getDrawable(R.drawable.ic_call_received_green_18dp).mutate();
        this.greenDrawable2 = mutate2;
        mutate2.setBounds(0, 0, mutate2.getIntrinsicWidth(), this.greenDrawable2.getIntrinsicHeight());
        this.greenDrawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_calls_callReceivedGreenIcon), PorterDuff.Mode.MULTIPLY));
        this.iconIn = new ImageSpan(this.greenDrawable2, 0);
        Drawable mutate3 = getParentActivity().getResources().getDrawable(R.drawable.ic_call_received_green_18dp).mutate();
        this.redDrawable = mutate3;
        mutate3.setBounds(0, 0, mutate3.getIntrinsicWidth(), this.redDrawable.getIntrinsicHeight());
        this.redDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_calls_callReceivedRedIcon), PorterDuff.Mode.MULTIPLY));
        this.iconMissed = new ImageSpan(this.redDrawable, 0);
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Calls", R.string.Calls));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    CallLogActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context2);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context2);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.setText(LocaleController.getString("NoCallLog", R.string.NoCallLog));
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context2);
        this.listView = recyclerListView;
        recyclerListView.setEmptyView(this.emptyView);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context2, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        RecyclerListView recyclerListView3 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context2);
        this.listViewAdapter = listAdapter;
        recyclerListView3.setAdapter(listAdapter);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                CallLogActivity.this.lambda$createView$0$CallLogActivity(view, i);
            }
        });
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
            public final boolean onItemClick(View view, int i) {
                return CallLogActivity.this.lambda$createView$2$CallLogActivity(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                boolean goingDown;
                int firstVisibleItem = CallLogActivity.this.layoutManager.findFirstVisibleItemPosition();
                boolean z = false;
                int visibleItemCount = firstVisibleItem == -1 ? 0 : Math.abs(CallLogActivity.this.layoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
                if (visibleItemCount > 0) {
                    int totalItemCount = CallLogActivity.this.listViewAdapter.getItemCount();
                    if (!CallLogActivity.this.endReached && !CallLogActivity.this.loading && !CallLogActivity.this.calls.isEmpty() && firstVisibleItem + visibleItemCount >= totalItemCount - 5) {
                        AndroidUtilities.runOnUIThread(new Runnable((CallLogRow) CallLogActivity.this.calls.get(CallLogActivity.this.calls.size() - 1)) {
                            private final /* synthetic */ CallLogActivity.CallLogRow f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                CallLogActivity.AnonymousClass3.this.lambda$onScrolled$0$CallLogActivity$3(this.f$1);
                            }
                        });
                    }
                }
                if (CallLogActivity.this.floatingButton.getVisibility() != 8) {
                    View topChild = recyclerView.getChildAt(0);
                    int firstViewTop = 0;
                    if (topChild != null) {
                        firstViewTop = topChild.getTop();
                    }
                    boolean changed = true;
                    if (CallLogActivity.this.prevPosition == firstVisibleItem) {
                        int topDelta = CallLogActivity.this.prevTop - firstViewTop;
                        goingDown = firstViewTop < CallLogActivity.this.prevTop;
                        if (Math.abs(topDelta) > 1) {
                            z = true;
                        }
                        changed = z;
                    } else {
                        if (firstVisibleItem > CallLogActivity.this.prevPosition) {
                            z = true;
                        }
                        goingDown = z;
                    }
                    if (changed && CallLogActivity.this.scrollUpdated) {
                        CallLogActivity.this.hideFloatingButton(goingDown);
                    }
                    int unused = CallLogActivity.this.prevPosition = firstVisibleItem;
                    int unused2 = CallLogActivity.this.prevTop = firstViewTop;
                    boolean unused3 = CallLogActivity.this.scrollUpdated = true;
                }
            }

            public /* synthetic */ void lambda$onScrolled$0$CallLogActivity$3(CallLogRow row) {
                CallLogActivity.this.getCalls(row.calls.get(row.calls.size() - 1).id, 100);
            }
        });
        if (this.loading) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        ImageView imageView = new ImageView(context2);
        this.floatingButton = imageView;
        imageView.setVisibility(0);
        this.floatingButton.setScaleType(ImageView.ScaleType.CENTER);
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_chats_actionBackground), Theme.getColor(Theme.key_chats_actionPressedBackground));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            drawable = combinedDrawable;
        }
        this.floatingButton.setBackgroundDrawable(drawable);
        this.floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionIcon), PorterDuff.Mode.MULTIPLY));
        this.floatingButton.setImageResource(R.drawable.ic_call);
        this.floatingButton.setContentDescription(LocaleController.getString("Call", R.string.Call));
        if (Build.VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            animator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.floatingButton.setStateListAnimator(animator);
            this.floatingButton.setOutlineProvider(new ViewOutlineProvider() {
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        frameLayout.addView(this.floatingButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, (LocaleController.isRTL ? 3 : 5) | 80, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 14.0f, 14.0f));
        this.floatingButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                CallLogActivity.this.lambda$createView$4$CallLogActivity(view);
            }
        });
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$CallLogActivity(View view, int position) {
        if (position >= 0 && position < this.calls.size()) {
            CallLogRow row = this.calls.get(position);
            Bundle args = new Bundle();
            args.putInt("user_id", row.user.id);
            args.putInt("message_id", row.calls.get(0).id);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            presentFragment(new ChatActivity(args), true);
        }
    }

    public /* synthetic */ boolean lambda$createView$2$CallLogActivity(View view, int position) {
        if (position < 0 || position >= this.calls.size()) {
            return false;
        }
        CallLogRow row = this.calls.get(position);
        ArrayList<String> items = new ArrayList<>();
        items.add(LocaleController.getString("Delete", R.string.Delete));
        if (VoIPHelper.canRateCall((TLRPC.TL_messageActionPhoneCall) row.calls.get(0).action)) {
            items.add(LocaleController.getString("CallMessageReportProblem", R.string.CallMessageReportProblem));
        }
        new AlertDialog.Builder((Context) getParentActivity()).setTitle(LocaleController.getString("Calls", R.string.Calls)).setItems((CharSequence[]) items.toArray(new String[0]), new DialogInterface.OnClickListener(row) {
            private final /* synthetic */ CallLogActivity.CallLogRow f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                CallLogActivity.this.lambda$null$1$CallLogActivity(this.f$1, dialogInterface, i);
            }
        }).show();
        return true;
    }

    public /* synthetic */ void lambda$null$1$CallLogActivity(CallLogRow row, DialogInterface dialog, int which) {
        if (which == 0) {
            confirmAndDelete(row);
        } else if (which == 1) {
            VoIPHelper.showRateAlert(getParentActivity(), (TLRPC.TL_messageActionPhoneCall) row.calls.get(0).action);
        }
    }

    public /* synthetic */ void lambda$createView$4$CallLogActivity(View v) {
        Bundle args = new Bundle();
        args.putBoolean("destroyAfterSelect", true);
        args.putBoolean("returnAsResult", true);
        args.putBoolean("onlyUsers", true);
        ContactsActivity contactsFragment = new ContactsActivity(args);
        contactsFragment.setDelegate(new ContactsActivity.ContactsActivityDelegate() {
            public final void didSelectContact(TLRPC.User user, String str, ContactsActivity contactsActivity) {
                CallLogActivity.this.lambda$null$3$CallLogActivity(user, str, contactsActivity);
            }
        });
        presentFragment(contactsFragment);
    }

    public /* synthetic */ void lambda$null$3$CallLogActivity(TLRPC.User user, String param, ContactsActivity activity) {
        VoIPHelper.startCall(user, getParentActivity(), (TLRPC.UserFull) null);
    }

    /* access modifiers changed from: private */
    public void hideFloatingButton(boolean hide) {
        if (this.floatingHidden != hide) {
            this.floatingHidden = hide;
            ImageView imageView = this.floatingButton;
            float[] fArr = new float[1];
            fArr[0] = hide ? (float) AndroidUtilities.dp(100.0f) : 0.0f;
            ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "translationY", fArr).setDuration(300);
            animator.setInterpolator(this.floatingInterpolator);
            this.floatingButton.setClickable(!hide);
            animator.start();
        }
    }

    /* access modifiers changed from: private */
    public void getCalls(int max_id, int count) {
        if (!this.loading) {
            this.loading = true;
            EmptyTextProgressView emptyTextProgressView = this.emptyView;
            if (emptyTextProgressView != null && !this.firstLoaded) {
                emptyTextProgressView.showProgress();
            }
            ListAdapter listAdapter = this.listViewAdapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
            TLRPC.TL_messages_search req = new TLRPC.TL_messages_search();
            req.limit = count;
            req.peer = new TLRPC.TL_inputPeerEmpty();
            req.filter = new TLRPC.TL_inputMessagesFilterPhoneCalls();
            req.q = "";
            req.offset_id = max_id;
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    CallLogActivity.this.lambda$getCalls$6$CallLogActivity(tLObject, tL_error);
                }
            }, 2), this.classGuid);
        }
    }

    public /* synthetic */ void lambda$getCalls$6$CallLogActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                CallLogActivity.this.lambda$null$5$CallLogActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$5$CallLogActivity(TLRPC.TL_error error, TLObject response) {
        CallLogRow currentRow;
        if (error == null) {
            SparseArray<TLRPC.User> users = new SparseArray<>();
            TLRPC.messages_Messages msgs = (TLRPC.messages_Messages) response;
            this.endReached = msgs.messages.isEmpty();
            for (int a = 0; a < msgs.users.size(); a++) {
                TLRPC.User user = msgs.users.get(a);
                users.put(user.id, user);
            }
            if (this.calls.size() > 0) {
                ArrayList<CallLogRow> arrayList = this.calls;
                currentRow = arrayList.get(arrayList.size() - 1);
            } else {
                currentRow = null;
            }
            for (int a2 = 0; a2 < msgs.messages.size(); a2++) {
                TLRPC.Message msg = msgs.messages.get(a2);
                if (msg.action != null && !(msg.action instanceof TLRPC.TL_messageActionHistoryClear)) {
                    int callType = msg.from_id == UserConfig.getInstance(this.currentAccount).getClientUserId() ? 0 : 1;
                    TLRPC.PhoneCallDiscardReason reason = msg.action.reason;
                    if (callType == 1 && ((reason instanceof TLRPC.TL_phoneCallDiscardReasonMissed) || (reason instanceof TLRPC.TL_phoneCallDiscardReasonBusy))) {
                        callType = 2;
                    }
                    int userID = msg.from_id == UserConfig.getInstance(this.currentAccount).getClientUserId() ? msg.to_id.user_id : msg.from_id;
                    if (!(currentRow != null && currentRow.user.id == userID && currentRow.type == callType)) {
                        if (currentRow != null && !this.calls.contains(currentRow)) {
                            this.calls.add(currentRow);
                        }
                        CallLogRow row = new CallLogRow();
                        row.calls = new ArrayList();
                        row.user = users.get(userID);
                        row.type = callType;
                        currentRow = row;
                    }
                    currentRow.calls.add(msg);
                }
            }
            if (currentRow != null && currentRow.calls.size() > 0 && !this.calls.contains(currentRow)) {
                this.calls.add(currentRow);
            }
        } else {
            this.endReached = true;
        }
        this.loading = false;
        this.firstLoaded = true;
        EmptyTextProgressView emptyTextProgressView = this.emptyView;
        if (emptyTextProgressView != null) {
            emptyTextProgressView.showTextView();
        }
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    private void confirmAndDelete(CallLogRow row) {
        if (getParentActivity() != null) {
            new AlertDialog.Builder((Context) getParentActivity()).setTitle(LocaleController.getString("AppName", R.string.AppName)).setMessage(LocaleController.getString("ConfirmDeleteCallLog", R.string.ConfirmDeleteCallLog)).setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener(row) {
                private final /* synthetic */ CallLogActivity.CallLogRow f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    CallLogActivity.this.lambda$confirmAndDelete$7$CallLogActivity(this.f$1, dialogInterface, i);
                }
            }).setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null).show().setCanceledOnTouchOutside(true);
        }
    }

    public /* synthetic */ void lambda$confirmAndDelete$7$CallLogActivity(CallLogRow row, DialogInterface dialog, int which) {
        ArrayList<Integer> ids = new ArrayList<>();
        for (TLRPC.Message msg : row.calls) {
            ids.add(Integer.valueOf(msg.id));
        }
        MessagesController.getInstance(this.currentAccount).deleteMessages(ids, (ArrayList<Long>) null, (TLRPC.EncryptedChat) null, 0, 0, false, false);
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != 101) {
            return;
        }
        if (grantResults.length <= 0 || grantResults[0] != 0) {
            VoIPHelper.permissionDenied(getParentActivity(), (Runnable) null);
        } else {
            VoIPHelper.startCall(this.lastCallUser, getParentActivity(), (TLRPC.UserFull) null);
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getAdapterPosition() != CallLogActivity.this.calls.size();
        }

        public int getItemCount() {
            int count = CallLogActivity.this.calls.size();
            if (CallLogActivity.this.calls.isEmpty() || CallLogActivity.this.endReached) {
                return count;
            }
            return count + 1;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextInfoPrivacyCell cell;
            if (viewType == 0) {
                CustomCell cell2 = new CustomCell(this.mContext);
                cell2.setTag(new ViewItem(cell2.imageView, cell2.profileSearchCell));
                cell = cell2;
            } else if (viewType != 1) {
                TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(this.mContext);
                textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                cell = textInfoPrivacyCell;
            } else {
                cell = new LoadingCell(this.mContext);
            }
            return new RecyclerListView.Holder(cell);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            SpannableString subtitle;
            int i = position;
            if (holder.getItemViewType() == 0) {
                ViewItem viewItem = (ViewItem) holder.itemView.getTag();
                ProfileSearchCell cell = viewItem.cell;
                CallLogRow row = (CallLogRow) CallLogActivity.this.calls.get(i);
                boolean z = false;
                TLRPC.Message last = row.calls.get(0);
                String ldir = LocaleController.isRTL ? "‫" : "";
                if (row.calls.size() == 1) {
                    subtitle = new SpannableString(ldir + "  " + LocaleController.formatDateCallLog((long) last.date));
                } else {
                    subtitle = new SpannableString(String.format(ldir + "  (%d) %s", new Object[]{Integer.valueOf(row.calls.size()), LocaleController.formatDateCallLog((long) last.date)}));
                }
                int i2 = row.type;
                if (i2 == 0) {
                    subtitle.setSpan(CallLogActivity.this.iconOut, ldir.length(), ldir.length() + 1, 0);
                } else if (i2 == 1) {
                    subtitle.setSpan(CallLogActivity.this.iconIn, ldir.length(), ldir.length() + 1, 0);
                } else if (i2 == 2) {
                    subtitle.setSpan(CallLogActivity.this.iconMissed, ldir.length(), ldir.length() + 1, 0);
                }
                SpannableString spannableString = subtitle;
                cell.setData(row.user, (TLRPC.EncryptedChat) null, (CharSequence) null, subtitle, false, false);
                if (i != CallLogActivity.this.calls.size() - 1 || !CallLogActivity.this.endReached) {
                    z = true;
                }
                cell.useSeparator = z;
                viewItem.button.setTag(row);
                return;
            }
            RecyclerView.ViewHolder viewHolder = holder;
        }

        public int getItemViewType(int i) {
            if (i < CallLogActivity.this.calls.size()) {
                return 0;
            }
            if (CallLogActivity.this.endReached || i != CallLogActivity.this.calls.size()) {
                return 2;
            }
            return 1;
        }
    }

    private class ViewItem {
        public ImageView button;
        public ProfileSearchCell cell;

        public ViewItem(ImageView button2, ProfileSearchCell cell2) {
            this.button = button2;
            this.cell = cell2;
        }
    }

    private class CallLogRow {
        public List<TLRPC.Message> calls;
        public int type;
        public TLRPC.User user;

        private CallLogRow() {
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                CallLogActivity.this.lambda$getThemeDescriptions$8$CallLogActivity();
            }
        };
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{LocationCell.class, CustomCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_emptyListPlaceholder), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_progressCircle), new ThemeDescription((View) this.listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_progressCircle), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText4), new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_actionIcon), new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_actionBackground), new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_actionPressedBackground), new ThemeDescription((View) this.listView, 0, new Class[]{CustomCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_featuredStickers_addButton), new ThemeDescription(this.listView, 0, new Class[]{CustomCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_verifiedCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_verifiedCheck), new ThemeDescription(this.listView, 0, new Class[]{CustomCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_verifiedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_verifiedBackground), new ThemeDescription(this.listView, 0, new Class[]{CustomCell.class}, Theme.dialogs_offlinePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText3), new ThemeDescription(this.listView, 0, new Class[]{CustomCell.class}, Theme.dialogs_onlinePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText3), new ThemeDescription((View) this.listView, 0, new Class[]{CustomCell.class}, (String[]) null, new Paint[]{Theme.dialogs_namePaint, Theme.dialogs_searchNamePaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_chats_name), new ThemeDescription((View) this.listView, 0, new Class[]{CustomCell.class}, (String[]) null, new Paint[]{Theme.dialogs_nameEncryptedPaint, Theme.dialogs_searchNameEncryptedPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_chats_secretName), new ThemeDescription(this.listView, 0, new Class[]{CustomCell.class}, (Paint) null, new Drawable[]{Theme.avatar_savedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_text), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundRed), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundOrange), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundBlue), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink), new ThemeDescription(this.listView, 0, new Class[]{View.class}, (Paint) null, new Drawable[]{this.greenDrawable, this.greenDrawable2, Theme.calllog_msgCallUpRedDrawable, Theme.calllog_msgCallDownRedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_calls_callReceivedGreenIcon), new ThemeDescription(this.listView, 0, new Class[]{View.class}, (Paint) null, new Drawable[]{this.redDrawable, Theme.calllog_msgCallUpGreenDrawable, Theme.calllog_msgCallDownGreenDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_calls_callReceivedRedIcon)};
    }

    public /* synthetic */ void lambda$getThemeDescriptions$8$CallLogActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof CustomCell) {
                    ((CustomCell) child).profileSearchCell.update(0);
                }
            }
        }
    }
}
