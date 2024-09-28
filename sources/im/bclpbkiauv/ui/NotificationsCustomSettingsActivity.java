package im.bclpbkiauv.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.upstream.cache.ContentMetadata;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.DispatchQueue;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.NotificationsCustomSettingsActivity;
import im.bclpbkiauv.ui.NotificationsSettingsActivity;
import im.bclpbkiauv.ui.ProfileNotificationsActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.adapters.SearchAdapterHelper;
import im.bclpbkiauv.ui.cells.GraySectionCell;
import im.bclpbkiauv.ui.cells.HeaderCell;
import im.bclpbkiauv.ui.cells.NotificationsCheckCell;
import im.bclpbkiauv.ui.cells.ShadowSectionCell;
import im.bclpbkiauv.ui.cells.TextCell;
import im.bclpbkiauv.ui.cells.TextCheckCell;
import im.bclpbkiauv.ui.cells.TextColorCell;
import im.bclpbkiauv.ui.cells.TextSettingsCell;
import im.bclpbkiauv.ui.cells.UserCell;
import im.bclpbkiauv.ui.components.EmptyTextProgressView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import java.util.ArrayList;
import java.util.HashMap;

public class NotificationsCustomSettingsActivity extends BaseFragment {
    private static final int search_button = 0;
    /* access modifiers changed from: private */
    public ListAdapter adapter;
    /* access modifiers changed from: private */
    public int alertRow;
    /* access modifiers changed from: private */
    public int alertSection2Row;
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    /* access modifiers changed from: private */
    public int currentType;
    /* access modifiers changed from: private */
    public int deleteAllRow;
    /* access modifiers changed from: private */
    public int deleteAllSectionRow;
    /* access modifiers changed from: private */
    public EmptyTextProgressView emptyView;
    /* access modifiers changed from: private */
    public ArrayList<NotificationsSettingsActivity.NotificationException> exceptions;
    /* access modifiers changed from: private */
    public int exceptionsAddRow;
    private HashMap<Long, NotificationsSettingsActivity.NotificationException> exceptionsDict;
    /* access modifiers changed from: private */
    public int exceptionsEndRow;
    /* access modifiers changed from: private */
    public int exceptionsSection2Row;
    /* access modifiers changed from: private */
    public int exceptionsStartRow;
    /* access modifiers changed from: private */
    public int groupSection2Row;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public int messageLedRow;
    /* access modifiers changed from: private */
    public int messagePopupNotificationRow;
    /* access modifiers changed from: private */
    public int messagePriorityRow;
    /* access modifiers changed from: private */
    public int messageSectionRow;
    /* access modifiers changed from: private */
    public int messageSoundRow;
    /* access modifiers changed from: private */
    public int messageVibrateRow;
    /* access modifiers changed from: private */
    public int previewRow;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public SearchAdapter searchAdapter;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;

    public NotificationsCustomSettingsActivity(int type, ArrayList<NotificationsSettingsActivity.NotificationException> notificationExceptions) {
        this(type, notificationExceptions, false);
    }

    public NotificationsCustomSettingsActivity(int type, ArrayList<NotificationsSettingsActivity.NotificationException> notificationExceptions, boolean load) {
        this.rowCount = 0;
        this.exceptionsDict = new HashMap<>();
        this.currentType = type;
        this.exceptions = notificationExceptions;
        int N = notificationExceptions.size();
        for (int a = 0; a < N; a++) {
            NotificationsSettingsActivity.NotificationException exception = this.exceptions.get(a);
            this.exceptionsDict.put(Long.valueOf(exception.did), exception);
        }
        if (load) {
            loadExceptions();
        }
    }

    public boolean onFragmentCreate() {
        updateRows();
        return super.onFragmentCreate();
    }

    public View createView(Context context) {
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.currentType == -1) {
            this.actionBar.setTitle(LocaleController.getString("NotificationsExceptions", R.string.NotificationsExceptions));
        } else {
            this.actionBar.setTitle(LocaleController.getString("Notifications", R.string.Notifications));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    NotificationsCustomSettingsActivity.this.finishFragment();
                }
            }
        });
        ArrayList<NotificationsSettingsActivity.NotificationException> arrayList = this.exceptions;
        if (arrayList != null && !arrayList.isEmpty()) {
            this.actionBar.createMenu().addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
                public void onSearchExpand() {
                    boolean unused = NotificationsCustomSettingsActivity.this.searching = true;
                    NotificationsCustomSettingsActivity.this.emptyView.setShowAtCenter(true);
                }

                public void onSearchCollapse() {
                    NotificationsCustomSettingsActivity.this.searchAdapter.searchDialogs((String) null);
                    boolean unused = NotificationsCustomSettingsActivity.this.searching = false;
                    boolean unused2 = NotificationsCustomSettingsActivity.this.searchWas = false;
                    NotificationsCustomSettingsActivity.this.emptyView.setText(LocaleController.getString("NoExceptions", R.string.NoExceptions));
                    NotificationsCustomSettingsActivity.this.listView.setAdapter(NotificationsCustomSettingsActivity.this.adapter);
                    NotificationsCustomSettingsActivity.this.adapter.notifyDataSetChanged();
                    NotificationsCustomSettingsActivity.this.listView.setFastScrollVisible(true);
                    NotificationsCustomSettingsActivity.this.listView.setVerticalScrollBarEnabled(false);
                    NotificationsCustomSettingsActivity.this.emptyView.setShowAtCenter(false);
                }

                public void onTextChanged(EditText editText) {
                    if (NotificationsCustomSettingsActivity.this.searchAdapter != null) {
                        String text = editText.getText().toString();
                        if (text.length() != 0) {
                            boolean unused = NotificationsCustomSettingsActivity.this.searchWas = true;
                            if (NotificationsCustomSettingsActivity.this.listView != null) {
                                NotificationsCustomSettingsActivity.this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
                                NotificationsCustomSettingsActivity.this.emptyView.showProgress();
                                NotificationsCustomSettingsActivity.this.listView.setAdapter(NotificationsCustomSettingsActivity.this.searchAdapter);
                                NotificationsCustomSettingsActivity.this.searchAdapter.notifyDataSetChanged();
                                NotificationsCustomSettingsActivity.this.listView.setFastScrollVisible(false);
                                NotificationsCustomSettingsActivity.this.listView.setVerticalScrollBarEnabled(true);
                            }
                        }
                        NotificationsCustomSettingsActivity.this.searchAdapter.searchDialogs(text);
                    }
                }
            }).setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
        }
        this.searchAdapter = new SearchAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.setTextSize(18);
        this.emptyView.setText(LocaleController.getString("NoExceptions", R.string.NoExceptions));
        this.emptyView.showTextView();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setEmptyView(this.emptyView);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new RecyclerListView.OnItemClickListenerExtended() {
            public final void onItemClick(View view, int i, float f, float f2) {
                NotificationsCustomSettingsActivity.this.lambda$createView$9$NotificationsCustomSettingsActivity(view, i, f, f2);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1 && NotificationsCustomSettingsActivity.this.searching && NotificationsCustomSettingsActivity.this.searchWas) {
                    AndroidUtilities.hideKeyboard(NotificationsCustomSettingsActivity.this.getParentActivity().getCurrentFocus());
                }
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        return this.fragmentView;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v9, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v4, resolved type: im.bclpbkiauv.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v6, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v10, resolved type: im.bclpbkiauv.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v13, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v11, resolved type: im.bclpbkiauv.ui.NotificationsSettingsActivity$NotificationException} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$createView$9$NotificationsCustomSettingsActivity(android.view.View r22, int r23, float r24, float r25) {
        /*
            r21 = this;
            r9 = r21
            r10 = r22
            r11 = r23
            r12 = 0
            androidx.fragment.app.FragmentActivity r0 = r21.getParentActivity()
            if (r0 != 0) goto L_0x000e
            return
        L_0x000e:
            im.bclpbkiauv.ui.components.RecyclerListView r0 = r9.listView
            androidx.recyclerview.widget.RecyclerView$Adapter r0 = r0.getAdapter()
            im.bclpbkiauv.ui.NotificationsCustomSettingsActivity$SearchAdapter r1 = r9.searchAdapter
            if (r0 == r1) goto L_0x0263
            int r0 = r9.exceptionsStartRow
            if (r11 < r0) goto L_0x0022
            int r0 = r9.exceptionsEndRow
            if (r11 >= r0) goto L_0x0022
            goto L_0x0263
        L_0x0022:
            int r0 = r9.exceptionsAddRow
            r1 = 0
            r2 = 2
            r3 = 1
            if (r11 != r0) goto L_0x0060
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r4 = "onlySelect"
            r0.putBoolean(r4, r3)
            java.lang.String r3 = "checkCanWrite"
            r0.putBoolean(r3, r1)
            int r1 = r9.currentType
            java.lang.String r3 = "dialogsType"
            if (r1 != 0) goto L_0x0043
            r1 = 6
            r0.putInt(r3, r1)
            goto L_0x004e
        L_0x0043:
            if (r1 != r2) goto L_0x004a
            r1 = 5
            r0.putInt(r3, r1)
            goto L_0x004e
        L_0x004a:
            r1 = 4
            r0.putInt(r3, r1)
        L_0x004e:
            im.bclpbkiauv.ui.DialogsActivity r1 = new im.bclpbkiauv.ui.DialogsActivity
            r1.<init>(r0)
            im.bclpbkiauv.ui.-$$Lambda$NotificationsCustomSettingsActivity$kdyWwVw7aQh5heBYwF5vC1-VgrQ r2 = new im.bclpbkiauv.ui.-$$Lambda$NotificationsCustomSettingsActivity$kdyWwVw7aQh5heBYwF5vC1-VgrQ
            r2.<init>()
            r1.setDelegate(r2)
            r9.presentFragment(r1)
            goto L_0x0256
        L_0x0060:
            int r0 = r9.deleteAllRow
            if (r11 != r0) goto L_0x00be
            im.bclpbkiauv.ui.actionbar.AlertDialog$Builder r0 = new im.bclpbkiauv.ui.actionbar.AlertDialog$Builder
            androidx.fragment.app.FragmentActivity r1 = r21.getParentActivity()
            r0.<init>((android.content.Context) r1)
            r1 = 2131692406(0x7f0f0b76, float:1.9013911E38)
            java.lang.String r2 = "NotificationsDeleteAllExceptionTitle"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            r1 = 2131692405(0x7f0f0b75, float:1.901391E38)
            java.lang.String r2 = "NotificationsDeleteAllExceptionAlert"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
            r0.setMessage(r1)
            r1 = 2131690831(0x7f0f054f, float:1.9010717E38)
            java.lang.String r2 = "Delete"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
            im.bclpbkiauv.ui.-$$Lambda$NotificationsCustomSettingsActivity$gneG5rJlDiQbGsWASPYeP5aEh8Q r2 = new im.bclpbkiauv.ui.-$$Lambda$NotificationsCustomSettingsActivity$gneG5rJlDiQbGsWASPYeP5aEh8Q
            r2.<init>()
            r0.setPositiveButton(r1, r2)
            r1 = 2131690308(0x7f0f0344, float:1.9009656E38)
            java.lang.String r2 = "Cancel"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
            r2 = 0
            r0.setNegativeButton(r1, r2)
            im.bclpbkiauv.ui.actionbar.AlertDialog r1 = r0.create()
            r9.showDialog(r1)
            r2 = -1
            android.view.View r2 = r1.getButton(r2)
            android.widget.TextView r2 = (android.widget.TextView) r2
            if (r2 == 0) goto L_0x00bc
            java.lang.String r3 = "dialogTextRed2"
            int r3 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r3)
            r2.setTextColor(r3)
        L_0x00bc:
            goto L_0x0256
        L_0x00be:
            int r0 = r9.alertRow
            if (r11 != r0) goto L_0x0104
            im.bclpbkiauv.messenger.NotificationsController r0 = r21.getNotificationsController()
            int r2 = r9.currentType
            boolean r12 = r0.isGlobalNotificationsEnabled((int) r2)
            r0 = r10
            im.bclpbkiauv.ui.cells.NotificationsCheckCell r0 = (im.bclpbkiauv.ui.cells.NotificationsCheckCell) r0
            im.bclpbkiauv.ui.components.RecyclerListView r2 = r9.listView
            androidx.recyclerview.widget.RecyclerView$ViewHolder r8 = r2.findViewHolderForAdapterPosition(r11)
            if (r12 != 0) goto L_0x00f0
            im.bclpbkiauv.messenger.NotificationsController r2 = r21.getNotificationsController()
            int r3 = r9.currentType
            r2.setGlobalNotificationsEnabled(r3, r1)
            r1 = r12 ^ 1
            r0.setChecked(r1)
            if (r8 == 0) goto L_0x00ec
            im.bclpbkiauv.ui.NotificationsCustomSettingsActivity$ListAdapter r1 = r9.adapter
            r1.onBindViewHolder(r8, r11)
        L_0x00ec:
            r21.checkRowsEnabled()
            goto L_0x0102
        L_0x00f0:
            r2 = 0
            int r4 = r9.currentType
            java.util.ArrayList<im.bclpbkiauv.ui.NotificationsSettingsActivity$NotificationException> r5 = r9.exceptions
            int r6 = r9.currentAccount
            im.bclpbkiauv.ui.-$$Lambda$NotificationsCustomSettingsActivity$r2Pw6BkswdjeI2Zlz_aE4V1o7VQ r7 = new im.bclpbkiauv.ui.-$$Lambda$NotificationsCustomSettingsActivity$r2Pw6BkswdjeI2Zlz_aE4V1o7VQ
            r7.<init>(r0, r8, r11)
            r1 = r21
            im.bclpbkiauv.ui.components.AlertsCreator.showCustomNotificationsDialog(r1, r2, r4, r5, r6, r7)
        L_0x0102:
            goto L_0x0256
        L_0x0104:
            int r0 = r9.previewRow
            if (r11 != r0) goto L_0x0151
            boolean r0 = r22.isEnabled()
            if (r0 != 0) goto L_0x010f
            return
        L_0x010f:
            android.content.SharedPreferences r0 = r21.getNotificationsSettings()
            android.content.SharedPreferences$Editor r1 = r0.edit()
            int r2 = r9.currentType
            if (r2 != r3) goto L_0x0128
            java.lang.String r2 = "EnablePreviewAll"
            boolean r3 = r0.getBoolean(r2, r3)
            r4 = r3 ^ 1
            r1.putBoolean(r2, r4)
            r12 = r3
            goto L_0x0143
        L_0x0128:
            if (r2 != 0) goto L_0x0137
            java.lang.String r2 = "EnablePreviewGroup"
            boolean r3 = r0.getBoolean(r2, r3)
            r4 = r3 ^ 1
            r1.putBoolean(r2, r4)
            r12 = r3
            goto L_0x0143
        L_0x0137:
            java.lang.String r2 = "EnablePreviewChannel"
            boolean r3 = r0.getBoolean(r2, r3)
            r4 = r3 ^ 1
            r1.putBoolean(r2, r4)
            r12 = r3
        L_0x0143:
            r1.commit()
            im.bclpbkiauv.messenger.NotificationsController r2 = r21.getNotificationsController()
            int r3 = r9.currentType
            r2.updateServerNotificationsSettings((int) r3)
            goto L_0x0256
        L_0x0151:
            int r0 = r9.messageSoundRow
            if (r11 != r0) goto L_0x01cc
            boolean r0 = r22.isEnabled()
            if (r0 != 0) goto L_0x015c
            return
        L_0x015c:
            android.content.SharedPreferences r0 = r21.getNotificationsSettings()     // Catch:{ Exception -> 0x01c6 }
            android.content.Intent r1 = new android.content.Intent     // Catch:{ Exception -> 0x01c6 }
            java.lang.String r4 = "android.intent.action.RINGTONE_PICKER"
            r1.<init>(r4)     // Catch:{ Exception -> 0x01c6 }
            java.lang.String r4 = "android.intent.extra.ringtone.TYPE"
            r1.putExtra(r4, r2)     // Catch:{ Exception -> 0x01c6 }
            java.lang.String r4 = "android.intent.extra.ringtone.SHOW_DEFAULT"
            r1.putExtra(r4, r3)     // Catch:{ Exception -> 0x01c6 }
            java.lang.String r4 = "android.intent.extra.ringtone.SHOW_SILENT"
            r1.putExtra(r4, r3)     // Catch:{ Exception -> 0x01c6 }
            java.lang.String r4 = "android.intent.extra.ringtone.DEFAULT_URI"
            android.net.Uri r2 = android.media.RingtoneManager.getDefaultUri(r2)     // Catch:{ Exception -> 0x01c6 }
            r1.putExtra(r4, r2)     // Catch:{ Exception -> 0x01c6 }
            r2 = 0
            r4 = 0
            android.net.Uri r5 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x01c6 }
            if (r5 == 0) goto L_0x018a
            java.lang.String r6 = r5.getPath()     // Catch:{ Exception -> 0x01c6 }
            r4 = r6
        L_0x018a:
            int r6 = r9.currentType     // Catch:{ Exception -> 0x01c6 }
            if (r6 != r3) goto L_0x0195
            java.lang.String r3 = "GlobalSoundPath"
            java.lang.String r3 = r0.getString(r3, r4)     // Catch:{ Exception -> 0x01c6 }
            goto L_0x01a6
        L_0x0195:
            int r3 = r9.currentType     // Catch:{ Exception -> 0x01c6 }
            if (r3 != 0) goto L_0x01a0
            java.lang.String r3 = "GroupSoundPath"
            java.lang.String r3 = r0.getString(r3, r4)     // Catch:{ Exception -> 0x01c6 }
            goto L_0x01a6
        L_0x01a0:
            java.lang.String r3 = "ChannelSoundPath"
            java.lang.String r3 = r0.getString(r3, r4)     // Catch:{ Exception -> 0x01c6 }
        L_0x01a6:
            if (r3 == 0) goto L_0x01bd
            java.lang.String r6 = "NoSound"
            boolean r6 = r3.equals(r6)     // Catch:{ Exception -> 0x01c6 }
            if (r6 != 0) goto L_0x01bd
            boolean r6 = r3.equals(r4)     // Catch:{ Exception -> 0x01c6 }
            if (r6 == 0) goto L_0x01b8
            r2 = r5
            goto L_0x01bd
        L_0x01b8:
            android.net.Uri r6 = android.net.Uri.parse(r3)     // Catch:{ Exception -> 0x01c6 }
            r2 = r6
        L_0x01bd:
            java.lang.String r6 = "android.intent.extra.ringtone.EXISTING_URI"
            r1.putExtra(r6, r2)     // Catch:{ Exception -> 0x01c6 }
            r9.startActivityForResult(r1, r11)     // Catch:{ Exception -> 0x01c6 }
            goto L_0x01ca
        L_0x01c6:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01ca:
            goto L_0x0256
        L_0x01cc:
            int r0 = r9.messageLedRow
            r1 = 0
            if (r11 != r0) goto L_0x01ec
            boolean r0 = r22.isEnabled()
            if (r0 != 0) goto L_0x01d9
            return
        L_0x01d9:
            androidx.fragment.app.FragmentActivity r0 = r21.getParentActivity()
            int r3 = r9.currentType
            im.bclpbkiauv.ui.-$$Lambda$NotificationsCustomSettingsActivity$e5_CJK0YuaR589ivLyv8YG14J-w r4 = new im.bclpbkiauv.ui.-$$Lambda$NotificationsCustomSettingsActivity$e5_CJK0YuaR589ivLyv8YG14J-w
            r4.<init>(r11)
            android.app.Dialog r0 = im.bclpbkiauv.ui.components.AlertsCreator.createColorSelectDialog(r0, r1, r3, r4)
            r9.showDialog(r0)
            goto L_0x0256
        L_0x01ec:
            int r0 = r9.messagePopupNotificationRow
            if (r11 != r0) goto L_0x020a
            boolean r0 = r22.isEnabled()
            if (r0 != 0) goto L_0x01f7
            return
        L_0x01f7:
            androidx.fragment.app.FragmentActivity r0 = r21.getParentActivity()
            int r1 = r9.currentType
            im.bclpbkiauv.ui.-$$Lambda$NotificationsCustomSettingsActivity$SgqtuHyHBVYl5ujtx3I-A8m_uc8 r2 = new im.bclpbkiauv.ui.-$$Lambda$NotificationsCustomSettingsActivity$SgqtuHyHBVYl5ujtx3I-A8m_uc8
            r2.<init>(r11)
            android.app.Dialog r0 = im.bclpbkiauv.ui.components.AlertsCreator.createPopupSelectDialog(r0, r1, r2)
            r9.showDialog(r0)
            goto L_0x0256
        L_0x020a:
            int r0 = r9.messageVibrateRow
            if (r11 != r0) goto L_0x0237
            boolean r0 = r22.isEnabled()
            if (r0 != 0) goto L_0x0215
            return
        L_0x0215:
            int r0 = r9.currentType
            if (r0 != r3) goto L_0x021d
            java.lang.String r0 = "vibrate_messages"
            goto L_0x0226
        L_0x021d:
            if (r0 != 0) goto L_0x0223
            java.lang.String r0 = "vibrate_group"
            goto L_0x0226
        L_0x0223:
            java.lang.String r0 = "vibrate_channel"
        L_0x0226:
            androidx.fragment.app.FragmentActivity r3 = r21.getParentActivity()
            im.bclpbkiauv.ui.-$$Lambda$NotificationsCustomSettingsActivity$tWRKQVbmVRsQ54aBSuoyhjyYUe8 r4 = new im.bclpbkiauv.ui.-$$Lambda$NotificationsCustomSettingsActivity$tWRKQVbmVRsQ54aBSuoyhjyYUe8
            r4.<init>(r11)
            android.app.Dialog r1 = im.bclpbkiauv.ui.components.AlertsCreator.createVibrationSelectDialog(r3, r1, r0, r4)
            r9.showDialog(r1)
            goto L_0x0255
        L_0x0237:
            int r0 = r9.messagePriorityRow
            if (r11 != r0) goto L_0x0255
            boolean r0 = r22.isEnabled()
            if (r0 != 0) goto L_0x0242
            return
        L_0x0242:
            androidx.fragment.app.FragmentActivity r0 = r21.getParentActivity()
            int r3 = r9.currentType
            im.bclpbkiauv.ui.-$$Lambda$NotificationsCustomSettingsActivity$mzGJZG6dEZG6wWgXCokKJ-5AqeM r4 = new im.bclpbkiauv.ui.-$$Lambda$NotificationsCustomSettingsActivity$mzGJZG6dEZG6wWgXCokKJ-5AqeM
            r4.<init>(r11)
            android.app.Dialog r0 = im.bclpbkiauv.ui.components.AlertsCreator.createPrioritySelectDialog(r0, r1, r3, r4)
            r9.showDialog(r0)
            goto L_0x0256
        L_0x0255:
        L_0x0256:
            boolean r0 = r10 instanceof im.bclpbkiauv.ui.cells.TextCheckCell
            if (r0 == 0) goto L_0x0262
            r0 = r10
            im.bclpbkiauv.ui.cells.TextCheckCell r0 = (im.bclpbkiauv.ui.cells.TextCheckCell) r0
            r1 = r12 ^ 1
            r0.setChecked(r1)
        L_0x0262:
            return
        L_0x0263:
            im.bclpbkiauv.ui.components.RecyclerListView r0 = r9.listView
            androidx.recyclerview.widget.RecyclerView$Adapter r0 = r0.getAdapter()
            im.bclpbkiauv.ui.NotificationsCustomSettingsActivity$SearchAdapter r1 = r9.searchAdapter
            if (r0 != r1) goto L_0x02d9
            java.lang.Object r0 = r1.getObject(r11)
            boolean r1 = r0 instanceof im.bclpbkiauv.ui.NotificationsSettingsActivity.NotificationException
            if (r1 == 0) goto L_0x0280
            im.bclpbkiauv.ui.NotificationsCustomSettingsActivity$SearchAdapter r1 = r9.searchAdapter
            java.util.ArrayList r1 = r1.searchResult
            r2 = r0
            im.bclpbkiauv.ui.NotificationsSettingsActivity$NotificationException r2 = (im.bclpbkiauv.ui.NotificationsSettingsActivity.NotificationException) r2
            r3 = 0
            goto L_0x02d5
        L_0x0280:
            boolean r1 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.User
            if (r1 == 0) goto L_0x028b
            r1 = r0
            im.bclpbkiauv.tgnet.TLRPC$User r1 = (im.bclpbkiauv.tgnet.TLRPC.User) r1
            int r2 = r1.id
            long r1 = (long) r2
            goto L_0x0293
        L_0x028b:
            r1 = r0
            im.bclpbkiauv.tgnet.TLRPC$Chat r1 = (im.bclpbkiauv.tgnet.TLRPC.Chat) r1
            int r2 = r1.id
            int r2 = -r2
            long r2 = (long) r2
            r1 = r2
        L_0x0293:
            java.util.HashMap<java.lang.Long, im.bclpbkiauv.ui.NotificationsSettingsActivity$NotificationException> r3 = r9.exceptionsDict
            java.lang.Long r4 = java.lang.Long.valueOf(r1)
            boolean r3 = r3.containsKey(r4)
            if (r3 == 0) goto L_0x02ad
            java.util.HashMap<java.lang.Long, im.bclpbkiauv.ui.NotificationsSettingsActivity$NotificationException> r3 = r9.exceptionsDict
            java.lang.Long r4 = java.lang.Long.valueOf(r1)
            java.lang.Object r3 = r3.get(r4)
            im.bclpbkiauv.ui.NotificationsSettingsActivity$NotificationException r3 = (im.bclpbkiauv.ui.NotificationsSettingsActivity.NotificationException) r3
            r4 = 0
            goto L_0x02d0
        L_0x02ad:
            r3 = 1
            im.bclpbkiauv.ui.NotificationsSettingsActivity$NotificationException r4 = new im.bclpbkiauv.ui.NotificationsSettingsActivity$NotificationException
            r4.<init>()
            r4.did = r1
            boolean r5 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.User
            if (r5 == 0) goto L_0x02c2
            r5 = r0
            im.bclpbkiauv.tgnet.TLRPC$User r5 = (im.bclpbkiauv.tgnet.TLRPC.User) r5
            int r6 = r5.id
            long r6 = (long) r6
            r4.did = r6
            goto L_0x02cb
        L_0x02c2:
            r5 = r0
            im.bclpbkiauv.tgnet.TLRPC$Chat r5 = (im.bclpbkiauv.tgnet.TLRPC.Chat) r5
            int r6 = r5.id
            int r6 = -r6
            long r6 = (long) r6
            r4.did = r6
        L_0x02cb:
            r20 = r4
            r4 = r3
            r3 = r20
        L_0x02d0:
            java.util.ArrayList<im.bclpbkiauv.ui.NotificationsSettingsActivity$NotificationException> r5 = r9.exceptions
            r2 = r3
            r3 = r4
            r1 = r5
        L_0x02d5:
            r0 = r1
            r13 = r2
            r14 = r3
            goto L_0x02f2
        L_0x02d9:
            java.util.ArrayList<im.bclpbkiauv.ui.NotificationsSettingsActivity$NotificationException> r1 = r9.exceptions
            int r0 = r9.exceptionsStartRow
            int r0 = r11 - r0
            if (r0 < 0) goto L_0x031e
            int r2 = r1.size()
            if (r0 < r2) goto L_0x02e8
            goto L_0x031e
        L_0x02e8:
            java.lang.Object r2 = r1.get(r0)
            im.bclpbkiauv.ui.NotificationsSettingsActivity$NotificationException r2 = (im.bclpbkiauv.ui.NotificationsSettingsActivity.NotificationException) r2
            r3 = 0
            r0 = r1
            r13 = r2
            r14 = r3
        L_0x02f2:
            if (r13 != 0) goto L_0x02f5
            return
        L_0x02f5:
            long r7 = r13.did
            r15 = -1
            r16 = 0
            int r6 = r9.currentAccount
            r17 = 0
            im.bclpbkiauv.ui.-$$Lambda$NotificationsCustomSettingsActivity$1oBpq6KGop5RZ0QcbSbFubNDx08 r18 = new im.bclpbkiauv.ui.-$$Lambda$NotificationsCustomSettingsActivity$1oBpq6KGop5RZ0QcbSbFubNDx08
            r1 = r18
            r2 = r21
            r3 = r14
            r4 = r0
            r5 = r13
            r19 = r6
            r6 = r23
            r1.<init>(r3, r4, r5, r6)
            r1 = r21
            r2 = r7
            r4 = r15
            r5 = r16
            r6 = r19
            r7 = r17
            r8 = r18
            im.bclpbkiauv.ui.components.AlertsCreator.showCustomNotificationsDialog(r1, r2, r4, r5, r6, r7, r8)
            return
        L_0x031e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.NotificationsCustomSettingsActivity.lambda$createView$9$NotificationsCustomSettingsActivity(android.view.View, int, float, float):void");
    }

    public /* synthetic */ void lambda$null$0$NotificationsCustomSettingsActivity(boolean newException, ArrayList arrayList, NotificationsSettingsActivity.NotificationException exception, int position, int param) {
        int idx;
        if (param != 0) {
            SharedPreferences preferences = getNotificationsSettings();
            exception.hasCustom = preferences.getBoolean(ContentMetadata.KEY_CUSTOM_PREFIX + exception.did, false);
            exception.notify = preferences.getInt("notify2_" + exception.did, 0);
            if (exception.notify != 0) {
                int time = preferences.getInt("notifyuntil_" + exception.did, -1);
                if (time != -1) {
                    exception.muteUntil = time;
                }
            }
            if (newException) {
                this.exceptions.add(exception);
                this.exceptionsDict.put(Long.valueOf(exception.did), exception);
                updateRows();
                this.adapter.notifyDataSetChanged();
            } else {
                this.listView.getAdapter().notifyItemChanged(position);
            }
            this.actionBar.closeSearchField();
        } else if (!newException) {
            ArrayList<NotificationsSettingsActivity.NotificationException> arrayList2 = this.exceptions;
            if (arrayList != arrayList2 && (idx = arrayList2.indexOf(exception)) >= 0) {
                this.exceptions.remove(idx);
                this.exceptionsDict.remove(Long.valueOf(exception.did));
            }
            arrayList.remove(exception);
            if (this.exceptionsAddRow != -1 && arrayList.isEmpty() && arrayList == this.exceptions) {
                this.listView.getAdapter().notifyItemChanged(this.exceptionsAddRow);
                this.listView.getAdapter().notifyItemRemoved(this.deleteAllRow);
                this.listView.getAdapter().notifyItemRemoved(this.deleteAllSectionRow);
            }
            this.listView.getAdapter().notifyItemRemoved(position);
            updateRows();
            checkRowsEnabled();
            this.actionBar.closeSearchField();
        }
    }

    public /* synthetic */ void lambda$null$2$NotificationsCustomSettingsActivity(DialogsActivity fragment, ArrayList dids, CharSequence message, boolean param) {
        Bundle args2 = new Bundle();
        args2.putLong("dialog_id", ((Long) dids.get(0)).longValue());
        args2.putBoolean("exception", true);
        ProfileNotificationsActivity profileNotificationsActivity = new ProfileNotificationsActivity(args2);
        profileNotificationsActivity.setDelegate(new ProfileNotificationsActivity.ProfileNotificationsActivityDelegate() {
            public final void didCreateNewException(NotificationsSettingsActivity.NotificationException notificationException) {
                NotificationsCustomSettingsActivity.this.lambda$null$1$NotificationsCustomSettingsActivity(notificationException);
            }
        });
        presentFragment(profileNotificationsActivity, true);
    }

    public /* synthetic */ void lambda$null$1$NotificationsCustomSettingsActivity(NotificationsSettingsActivity.NotificationException exception) {
        this.exceptions.add(0, exception);
        updateRows();
        this.adapter.notifyDataSetChanged();
    }

    public /* synthetic */ void lambda$null$3$NotificationsCustomSettingsActivity(DialogInterface dialogInterface, int i) {
        SharedPreferences.Editor editor = getNotificationsSettings().edit();
        int N = this.exceptions.size();
        for (int a = 0; a < N; a++) {
            NotificationsSettingsActivity.NotificationException exception = this.exceptions.get(a);
            SharedPreferences.Editor remove = editor.remove("notify2_" + exception.did);
            remove.remove(ContentMetadata.KEY_CUSTOM_PREFIX + exception.did);
            getMessagesStorage().setDialogFlags(exception.did, 0);
            TLRPC.Dialog dialog = getMessagesController().dialogs_dict.get(exception.did);
            if (dialog != null) {
                dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
            }
        }
        editor.commit();
        int N2 = this.exceptions.size();
        for (int a2 = 0; a2 < N2; a2++) {
            getNotificationsController().updateServerNotificationsSettings(this.exceptions.get(a2).did, false);
        }
        this.exceptions.clear();
        this.exceptionsDict.clear();
        updateRows();
        getNotificationCenter().postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
        this.adapter.notifyDataSetChanged();
    }

    public /* synthetic */ void lambda$null$4$NotificationsCustomSettingsActivity(NotificationsCheckCell checkCell, RecyclerView.ViewHolder holder, int position, int param) {
        int offUntil;
        int iconType;
        SharedPreferences preferences = getNotificationsSettings();
        int offUntil2 = this.currentType;
        if (offUntil2 == 1) {
            offUntil = preferences.getInt("EnableAll2", 0);
        } else if (offUntil2 == 0) {
            offUntil = preferences.getInt("EnableGroup2", 0);
        } else {
            offUntil = preferences.getInt("EnableChannel2", 0);
        }
        int currentTime = getConnectionsManager().getCurrentTime();
        if (offUntil < currentTime) {
            iconType = 0;
        } else if (offUntil - 31536000 >= currentTime) {
            iconType = 0;
        } else {
            iconType = 2;
        }
        checkCell.setChecked(getNotificationsController().isGlobalNotificationsEnabled(this.currentType), iconType);
        if (holder != null) {
            this.adapter.onBindViewHolder(holder, position);
        }
        checkRowsEnabled();
    }

    public /* synthetic */ void lambda$null$5$NotificationsCustomSettingsActivity(int position) {
        RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(position);
        if (holder != null) {
            this.adapter.onBindViewHolder(holder, position);
        }
    }

    public /* synthetic */ void lambda$null$6$NotificationsCustomSettingsActivity(int position) {
        RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(position);
        if (holder != null) {
            this.adapter.onBindViewHolder(holder, position);
        }
    }

    public /* synthetic */ void lambda$null$7$NotificationsCustomSettingsActivity(int position) {
        RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(position);
        if (holder != null) {
            this.adapter.onBindViewHolder(holder, position);
        }
    }

    public /* synthetic */ void lambda$null$8$NotificationsCustomSettingsActivity(int position) {
        RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(position);
        if (holder != null) {
            this.adapter.onBindViewHolder(holder, position);
        }
    }

    private void checkRowsEnabled() {
        if (this.exceptions.isEmpty()) {
            int count = this.listView.getChildCount();
            ArrayList<Animator> animators = new ArrayList<>();
            boolean enabled = getNotificationsController().isGlobalNotificationsEnabled(this.currentType);
            for (int a = 0; a < count; a++) {
                RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.getChildViewHolder(this.listView.getChildAt(a));
                int itemViewType = holder.getItemViewType();
                if (itemViewType == 0) {
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (holder.getAdapterPosition() == this.messageSectionRow) {
                        headerCell.setEnabled(enabled, animators);
                    }
                } else if (itemViewType == 1) {
                    ((TextCheckCell) holder.itemView).setEnabled(enabled, animators);
                } else if (itemViewType == 3) {
                    ((TextColorCell) holder.itemView).setEnabled(enabled, animators);
                } else if (itemViewType == 5) {
                    ((TextSettingsCell) holder.itemView).setEnabled(enabled, animators);
                }
            }
            if (animators.isEmpty() == 0) {
                AnimatorSet animatorSet2 = this.animatorSet;
                if (animatorSet2 != null) {
                    animatorSet2.cancel();
                }
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.animatorSet = animatorSet3;
                animatorSet3.playTogether(animators);
                this.animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (animator.equals(NotificationsCustomSettingsActivity.this.animatorSet)) {
                            AnimatorSet unused = NotificationsCustomSettingsActivity.this.animatorSet = null;
                        }
                    }
                });
                this.animatorSet.setDuration(150);
                this.animatorSet.start();
            }
        }
    }

    private void loadExceptions() {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() {
            public final void run() {
                NotificationsCustomSettingsActivity.this.lambda$loadExceptions$11$NotificationsCustomSettingsActivity();
            }
        });
    }

    /* JADX WARNING: Removed duplicated region for block: B:117:0x02e6  */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x0301 A[LOOP:3: B:122:0x02ff->B:123:0x0301, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x031a  */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x029a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadExceptions$11$NotificationsCustomSettingsActivity() {
        /*
            r25 = this;
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r9 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r10 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r11 = r0
            android.util.LongSparseArray r0 = new android.util.LongSparseArray
            r0.<init>()
            r12 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r13 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r14 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r15 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r8 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r7 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r6 = r0
            im.bclpbkiauv.messenger.UserConfig r0 = r25.getUserConfig()
            int r5 = r0.clientUserId
            android.content.SharedPreferences r4 = r25.getNotificationsSettings()
            java.util.Map r3 = r4.getAll()
            java.util.Set r0 = r3.entrySet()
            java.util.Iterator r0 = r0.iterator()
        L_0x0052:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x0226
            java.lang.Object r1 = r0.next()
            java.util.Map$Entry r1 = (java.util.Map.Entry) r1
            java.lang.Object r16 = r1.getKey()
            r2 = r16
            java.lang.String r2 = (java.lang.String) r2
            r16 = r0
            java.lang.String r0 = "notify2_"
            boolean r18 = r2.startsWith(r0)
            if (r18 == 0) goto L_0x0210
            r18 = r7
            java.lang.String r7 = ""
            java.lang.String r0 = r2.replace(r0, r7)
            java.lang.Long r2 = im.bclpbkiauv.messenger.Utilities.parseLong(r0)
            r19 = r8
            long r7 = r2.longValue()
            r20 = 0
            int r2 = (r7 > r20 ? 1 : (r7 == r20 ? 0 : -1))
            if (r2 == 0) goto L_0x0207
            r20 = r10
            r21 = r11
            long r10 = (long) r5
            int r2 = (r7 > r10 ? 1 : (r7 == r10 ? 0 : -1))
            if (r2 == 0) goto L_0x01fa
            im.bclpbkiauv.ui.NotificationsSettingsActivity$NotificationException r2 = new im.bclpbkiauv.ui.NotificationsSettingsActivity$NotificationException
            r2.<init>()
            r2.did = r7
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "custom_"
            r10.append(r11)
            r10.append(r7)
            java.lang.String r10 = r10.toString()
            r11 = 0
            boolean r10 = r4.getBoolean(r10, r11)
            r2.hasCustom = r10
            java.lang.Object r10 = r1.getValue()
            java.lang.Integer r10 = (java.lang.Integer) r10
            int r10 = r10.intValue()
            r2.notify = r10
            int r10 = r2.notify
            if (r10 == 0) goto L_0x00df
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "notifyuntil_"
            r10.append(r11)
            r10.append(r0)
            java.lang.String r10 = r10.toString()
            java.lang.Object r10 = r3.get(r10)
            java.lang.Integer r10 = (java.lang.Integer) r10
            if (r10 == 0) goto L_0x00df
            int r11 = r10.intValue()
            r2.muteUntil = r11
        L_0x00df:
            int r10 = (int) r7
            r22 = r0
            r17 = r1
            r11 = 32
            long r0 = r7 << r11
            int r1 = (int) r0
            if (r10 == 0) goto L_0x0192
            if (r10 <= 0) goto L_0x0123
            im.bclpbkiauv.messenger.MessagesController r0 = r25.getMessagesController()
            java.lang.Integer r11 = java.lang.Integer.valueOf(r10)
            im.bclpbkiauv.tgnet.TLRPC$User r0 = r0.getUser(r11)
            if (r0 != 0) goto L_0x0106
            java.lang.Integer r11 = java.lang.Integer.valueOf(r10)
            r13.add(r11)
            r12.put(r7, r2)
            goto L_0x0116
        L_0x0106:
            boolean r11 = r0.deleted
            if (r11 == 0) goto L_0x0116
            r0 = r16
            r7 = r18
            r8 = r19
            r10 = r20
            r11 = r21
            goto L_0x0052
        L_0x0116:
            r9.add(r2)
            r24 = r4
            r10 = r20
            r11 = r21
            r20 = r3
            goto L_0x021a
        L_0x0123:
            im.bclpbkiauv.messenger.MessagesController r0 = r25.getMessagesController()
            int r11 = -r10
            java.lang.Integer r11 = java.lang.Integer.valueOf(r11)
            im.bclpbkiauv.tgnet.TLRPC$Chat r0 = r0.getChat(r11)
            if (r0 != 0) goto L_0x0149
            int r11 = -r10
            java.lang.Integer r11 = java.lang.Integer.valueOf(r11)
            r14.add(r11)
            r12.put(r7, r2)
            r0 = r16
            r7 = r18
            r8 = r19
            r10 = r20
            r11 = r21
            goto L_0x0052
        L_0x0149:
            boolean r11 = r0.left
            if (r11 != 0) goto L_0x0184
            boolean r11 = r0.kicked
            if (r11 != 0) goto L_0x0184
            im.bclpbkiauv.tgnet.TLRPC$InputChannel r11 = r0.migrated_to
            if (r11 == 0) goto L_0x0161
            r0 = r16
            r7 = r18
            r8 = r19
            r10 = r20
            r11 = r21
            goto L_0x0052
        L_0x0161:
            boolean r11 = im.bclpbkiauv.messenger.ChatObject.isChannel(r0)
            if (r11 == 0) goto L_0x0175
            boolean r11 = r0.megagroup
            if (r11 != 0) goto L_0x0175
            r11 = r21
            r11.add(r2)
            r21 = r10
            r10 = r20
            goto L_0x017e
        L_0x0175:
            r11 = r21
            r21 = r10
            r10 = r20
            r10.add(r2)
        L_0x017e:
            r20 = r3
            r24 = r4
            goto L_0x021a
        L_0x0184:
            r11 = r21
            r21 = r10
            r10 = r20
            r0 = r16
            r7 = r18
            r8 = r19
            goto L_0x0052
        L_0x0192:
            r11 = r21
            r21 = r10
            r10 = r20
            if (r1 == 0) goto L_0x01f3
            im.bclpbkiauv.messenger.MessagesController r0 = r25.getMessagesController()
            r20 = r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r1)
            im.bclpbkiauv.tgnet.TLRPC$EncryptedChat r0 = r0.getEncryptedChat(r3)
            if (r0 != 0) goto L_0x01b9
            java.lang.Integer r3 = java.lang.Integer.valueOf(r1)
            r15.add(r3)
            r12.put(r7, r2)
            r23 = r1
            r24 = r4
            goto L_0x01ef
        L_0x01b9:
            im.bclpbkiauv.messenger.MessagesController r3 = r25.getMessagesController()
            r23 = r1
            int r1 = r0.user_id
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            im.bclpbkiauv.tgnet.TLRPC$User r1 = r3.getUser(r1)
            if (r1 != 0) goto L_0x01dd
            int r3 = r0.user_id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r13.add(r3)
            int r3 = r0.user_id
            r24 = r4
            long r3 = (long) r3
            r12.put(r3, r2)
            goto L_0x01ef
        L_0x01dd:
            r24 = r4
            boolean r3 = r1.deleted
            if (r3 == 0) goto L_0x01ef
            r0 = r16
            r7 = r18
            r8 = r19
            r3 = r20
            r4 = r24
            goto L_0x0052
        L_0x01ef:
            r9.add(r2)
            goto L_0x021a
        L_0x01f3:
            r23 = r1
            r20 = r3
            r24 = r4
            goto L_0x021a
        L_0x01fa:
            r22 = r0
            r17 = r1
            r24 = r4
            r10 = r20
            r11 = r21
            r20 = r3
            goto L_0x021a
        L_0x0207:
            r22 = r0
            r17 = r1
            r20 = r3
            r24 = r4
            goto L_0x021a
        L_0x0210:
            r17 = r1
            r20 = r3
            r24 = r4
            r18 = r7
            r19 = r8
        L_0x021a:
            r0 = r16
            r7 = r18
            r8 = r19
            r3 = r20
            r4 = r24
            goto L_0x0052
        L_0x0226:
            r20 = r3
            r24 = r4
            r18 = r7
            r19 = r8
            int r0 = r12.size()
            if (r0 == 0) goto L_0x033a
            boolean r0 = r15.isEmpty()     // Catch:{ Exception -> 0x028b }
            java.lang.String r1 = ","
            if (r0 != 0) goto L_0x024e
            im.bclpbkiauv.messenger.MessagesStorage r0 = r25.getMessagesStorage()     // Catch:{ Exception -> 0x0248 }
            java.lang.String r2 = android.text.TextUtils.join(r1, r15)     // Catch:{ Exception -> 0x0248 }
            r0.getEncryptedChatsInternal(r2, r6, r13)     // Catch:{ Exception -> 0x0248 }
            goto L_0x024e
        L_0x0248:
            r0 = move-exception
            r7 = r18
            r8 = r19
            goto L_0x0290
        L_0x024e:
            boolean r0 = r13.isEmpty()     // Catch:{ Exception -> 0x028b }
            if (r0 != 0) goto L_0x026c
            im.bclpbkiauv.messenger.MessagesStorage r0 = r25.getMessagesStorage()     // Catch:{ Exception -> 0x0266 }
            java.lang.String r2 = android.text.TextUtils.join(r1, r13)     // Catch:{ Exception -> 0x0266 }
            r8 = r19
            r0.getUsersInternal(r2, r8)     // Catch:{ Exception -> 0x0262 }
            goto L_0x026e
        L_0x0262:
            r0 = move-exception
            r7 = r18
            goto L_0x0290
        L_0x0266:
            r0 = move-exception
            r8 = r19
            r7 = r18
            goto L_0x0290
        L_0x026c:
            r8 = r19
        L_0x026e:
            boolean r0 = r14.isEmpty()     // Catch:{ Exception -> 0x0287 }
            if (r0 != 0) goto L_0x0284
            im.bclpbkiauv.messenger.MessagesStorage r0 = r25.getMessagesStorage()     // Catch:{ Exception -> 0x0287 }
            java.lang.String r1 = android.text.TextUtils.join(r1, r14)     // Catch:{ Exception -> 0x0287 }
            r7 = r18
            r0.getChatsInternal(r1, r7)     // Catch:{ Exception -> 0x0282 }
            goto L_0x0286
        L_0x0282:
            r0 = move-exception
            goto L_0x0290
        L_0x0284:
            r7 = r18
        L_0x0286:
            goto L_0x0293
        L_0x0287:
            r0 = move-exception
            r7 = r18
            goto L_0x0290
        L_0x028b:
            r0 = move-exception
            r7 = r18
            r8 = r19
        L_0x0290:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0293:
            r0 = 0
            int r1 = r7.size()
        L_0x0298:
            if (r0 >= r1) goto L_0x02dd
            java.lang.Object r2 = r7.get(r0)
            im.bclpbkiauv.tgnet.TLRPC$Chat r2 = (im.bclpbkiauv.tgnet.TLRPC.Chat) r2
            boolean r3 = r2.left
            if (r3 != 0) goto L_0x02d6
            boolean r3 = r2.kicked
            if (r3 != 0) goto L_0x02d6
            im.bclpbkiauv.tgnet.TLRPC$InputChannel r3 = r2.migrated_to
            if (r3 == 0) goto L_0x02af
            r16 = r5
            goto L_0x02d8
        L_0x02af:
            int r3 = r2.id
            int r3 = -r3
            long r3 = (long) r3
            java.lang.Object r3 = r12.get(r3)
            im.bclpbkiauv.ui.NotificationsSettingsActivity$NotificationException r3 = (im.bclpbkiauv.ui.NotificationsSettingsActivity.NotificationException) r3
            int r4 = r2.id
            int r4 = -r4
            r16 = r5
            long r4 = (long) r4
            r12.remove(r4)
            if (r3 == 0) goto L_0x02d8
            boolean r4 = im.bclpbkiauv.messenger.ChatObject.isChannel(r2)
            if (r4 == 0) goto L_0x02d2
            boolean r4 = r2.megagroup
            if (r4 != 0) goto L_0x02d2
            r11.add(r3)
            goto L_0x02d8
        L_0x02d2:
            r10.add(r3)
            goto L_0x02d8
        L_0x02d6:
            r16 = r5
        L_0x02d8:
            int r0 = r0 + 1
            r5 = r16
            goto L_0x0298
        L_0x02dd:
            r16 = r5
            r0 = 0
            int r1 = r8.size()
        L_0x02e4:
            if (r0 >= r1) goto L_0x02fa
            java.lang.Object r2 = r8.get(r0)
            im.bclpbkiauv.tgnet.TLRPC$User r2 = (im.bclpbkiauv.tgnet.TLRPC.User) r2
            boolean r3 = r2.deleted
            if (r3 == 0) goto L_0x02f1
            goto L_0x02f7
        L_0x02f1:
            int r3 = r2.id
            long r3 = (long) r3
            r12.remove(r3)
        L_0x02f7:
            int r0 = r0 + 1
            goto L_0x02e4
        L_0x02fa:
            r0 = 0
            int r1 = r6.size()
        L_0x02ff:
            if (r0 >= r1) goto L_0x0313
            java.lang.Object r2 = r6.get(r0)
            im.bclpbkiauv.tgnet.TLRPC$EncryptedChat r2 = (im.bclpbkiauv.tgnet.TLRPC.EncryptedChat) r2
            int r3 = r2.id
            long r3 = (long) r3
            r5 = 32
            long r3 = r3 << r5
            r12.remove(r3)
            int r0 = r0 + 1
            goto L_0x02ff
        L_0x0313:
            r0 = 0
            int r1 = r12.size()
        L_0x0318:
            if (r0 >= r1) goto L_0x0340
            long r2 = r12.keyAt(r0)
            int r4 = (int) r2
            if (r4 >= 0) goto L_0x0330
            java.lang.Object r4 = r12.valueAt(r0)
            r10.remove(r4)
            java.lang.Object r4 = r12.valueAt(r0)
            r11.remove(r4)
            goto L_0x0337
        L_0x0330:
            java.lang.Object r4 = r12.valueAt(r0)
            r9.remove(r4)
        L_0x0337:
            int r0 = r0 + 1
            goto L_0x0318
        L_0x033a:
            r16 = r5
            r7 = r18
            r8 = r19
        L_0x0340:
            im.bclpbkiauv.ui.-$$Lambda$NotificationsCustomSettingsActivity$i345TLvEU_Y7TUUwjRCHUTPGxYY r0 = new im.bclpbkiauv.ui.-$$Lambda$NotificationsCustomSettingsActivity$i345TLvEU_Y7TUUwjRCHUTPGxYY
            r1 = r0
            r2 = r25
            r17 = r20
            r3 = r8
            r18 = r24
            r4 = r7
            r5 = r6
            r19 = r6
            r6 = r9
            r20 = r7
            r7 = r10
            r21 = r8
            r8 = r11
            r1.<init>(r3, r4, r5, r6, r7, r8)
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.NotificationsCustomSettingsActivity.lambda$loadExceptions$11$NotificationsCustomSettingsActivity():void");
    }

    public /* synthetic */ void lambda$null$10$NotificationsCustomSettingsActivity(ArrayList users, ArrayList chats, ArrayList encryptedChats, ArrayList usersResult, ArrayList chatsResult, ArrayList channelsResult) {
        getMessagesController().putUsers(users, true);
        getMessagesController().putChats(chats, true);
        getMessagesController().putEncryptedChats(encryptedChats, true);
        int i = this.currentType;
        if (i == 1) {
            this.exceptions = usersResult;
        } else if (i == 0) {
            this.exceptions = chatsResult;
        } else {
            this.exceptions = channelsResult;
        }
        updateRows();
        this.adapter.notifyDataSetChanged();
    }

    private void updateRows() {
        ArrayList<NotificationsSettingsActivity.NotificationException> arrayList;
        this.rowCount = 0;
        int i = this.currentType;
        if (i != -1) {
            int i2 = 0 + 1;
            this.rowCount = i2;
            this.alertRow = 0;
            int i3 = i2 + 1;
            this.rowCount = i3;
            this.alertSection2Row = i2;
            int i4 = i3 + 1;
            this.rowCount = i4;
            this.messageSectionRow = i3;
            int i5 = i4 + 1;
            this.rowCount = i5;
            this.previewRow = i4;
            int i6 = i5 + 1;
            this.rowCount = i6;
            this.messageLedRow = i5;
            int i7 = i6 + 1;
            this.rowCount = i7;
            this.messageVibrateRow = i6;
            if (i == 2) {
                this.messagePopupNotificationRow = -1;
            } else {
                this.rowCount = i7 + 1;
                this.messagePopupNotificationRow = i7;
            }
            int i8 = this.rowCount;
            this.rowCount = i8 + 1;
            this.messageSoundRow = i8;
            if (Build.VERSION.SDK_INT >= 21) {
                int i9 = this.rowCount;
                this.rowCount = i9 + 1;
                this.messagePriorityRow = i9;
            } else {
                this.messagePriorityRow = -1;
            }
            int i10 = this.rowCount;
            int i11 = i10 + 1;
            this.rowCount = i11;
            this.groupSection2Row = i10;
            this.rowCount = i11 + 1;
            this.exceptionsAddRow = i11;
        } else {
            this.alertRow = -1;
            this.alertSection2Row = -1;
            this.messageSectionRow = -1;
            this.previewRow = -1;
            this.messageLedRow = -1;
            this.messageVibrateRow = -1;
            this.messagePopupNotificationRow = -1;
            this.messageSoundRow = -1;
            this.messagePriorityRow = -1;
            this.groupSection2Row = -1;
            this.exceptionsAddRow = -1;
        }
        ArrayList<NotificationsSettingsActivity.NotificationException> arrayList2 = this.exceptions;
        if (arrayList2 == null || arrayList2.isEmpty()) {
            this.exceptionsStartRow = -1;
            this.exceptionsEndRow = -1;
        } else {
            int i12 = this.rowCount;
            this.exceptionsStartRow = i12;
            int size = i12 + this.exceptions.size();
            this.rowCount = size;
            this.exceptionsEndRow = size;
        }
        if (this.currentType != -1 || ((arrayList = this.exceptions) != null && !arrayList.isEmpty())) {
            int i13 = this.rowCount;
            this.rowCount = i13 + 1;
            this.exceptionsSection2Row = i13;
        } else {
            this.exceptionsSection2Row = -1;
        }
        ArrayList<NotificationsSettingsActivity.NotificationException> arrayList3 = this.exceptions;
        if (arrayList3 == null || arrayList3.isEmpty()) {
            this.deleteAllRow = -1;
            this.deleteAllSectionRow = -1;
            return;
        }
        int i14 = this.rowCount;
        int i15 = i14 + 1;
        this.rowCount = i15;
        this.deleteAllRow = i14;
        this.rowCount = i15 + 1;
        this.deleteAllSectionRow = i15;
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        Ringtone rng;
        if (resultCode == -1) {
            Uri ringtone = (Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            String name = null;
            if (!(ringtone == null || (rng = RingtoneManager.getRingtone(getParentActivity(), ringtone)) == null)) {
                if (ringtone.equals(Settings.System.DEFAULT_NOTIFICATION_URI)) {
                    name = LocaleController.getString("SoundDefault", R.string.SoundDefault);
                } else {
                    name = rng.getTitle(getParentActivity());
                }
                rng.stop();
            }
            SharedPreferences.Editor editor = getNotificationsSettings().edit();
            int i = this.currentType;
            if (i == 1) {
                if (name == null || ringtone == null) {
                    editor.putString("GlobalSound", "NoSound");
                    editor.putString("GlobalSoundPath", "NoSound");
                } else {
                    editor.putString("GlobalSound", name);
                    editor.putString("GlobalSoundPath", ringtone.toString());
                }
            } else if (i == 0) {
                if (name == null || ringtone == null) {
                    editor.putString("GroupSound", "NoSound");
                    editor.putString("GroupSoundPath", "NoSound");
                } else {
                    editor.putString("GroupSound", name);
                    editor.putString("GroupSoundPath", ringtone.toString());
                }
            } else if (i == 2) {
                if (name == null || ringtone == null) {
                    editor.putString("ChannelSound", "NoSound");
                    editor.putString("ChannelSoundPath", "NoSound");
                } else {
                    editor.putString("ChannelSound", name);
                    editor.putString("ChannelSoundPath", ringtone.toString());
                }
            }
            editor.commit();
            getNotificationsController().updateServerNotificationsSettings(this.currentType);
            RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(requestCode);
            if (holder != null) {
                this.adapter.onBindViewHolder(holder, requestCode);
            }
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    private class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;
        private SearchAdapterHelper searchAdapterHelper;
        /* access modifiers changed from: private */
        public ArrayList<NotificationsSettingsActivity.NotificationException> searchResult = new ArrayList<>();
        private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
        private Runnable searchRunnable;

        public SearchAdapter(Context context) {
            this.mContext = context;
            SearchAdapterHelper searchAdapterHelper2 = new SearchAdapterHelper(true);
            this.searchAdapterHelper = searchAdapterHelper2;
            searchAdapterHelper2.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate() {
                public /* synthetic */ SparseArray<TLRPC.User> getExcludeUsers() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
                }

                public final void onDataSetChanged() {
                    NotificationsCustomSettingsActivity.SearchAdapter.this.lambda$new$0$NotificationsCustomSettingsActivity$SearchAdapter();
                }

                public /* synthetic */ void onSetHashtags(ArrayList<SearchAdapterHelper.HashtagObject> arrayList, HashMap<String, SearchAdapterHelper.HashtagObject> hashMap) {
                    SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$onSetHashtags(this, arrayList, hashMap);
                }
            });
        }

        public /* synthetic */ void lambda$new$0$NotificationsCustomSettingsActivity$SearchAdapter() {
            if (this.searchRunnable == null && !this.searchAdapterHelper.isSearchInProgress()) {
                NotificationsCustomSettingsActivity.this.emptyView.showTextView();
            }
            notifyDataSetChanged();
        }

        public void searchDialogs(String query) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (query == null) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                this.searchAdapterHelper.mergeResults((ArrayList<TLObject>) null);
                this.searchAdapterHelper.queryServerSearch((String) null, true, NotificationsCustomSettingsActivity.this.currentType != 1, true, false, 0, false, 0);
                notifyDataSetChanged();
                return;
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            $$Lambda$NotificationsCustomSettingsActivity$SearchAdapter$XQ4NMcdlwVKm4Db4DcwP0QCZ4 r1 = new Runnable(query) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NotificationsCustomSettingsActivity.SearchAdapter.this.lambda$searchDialogs$1$NotificationsCustomSettingsActivity$SearchAdapter(this.f$1);
                }
            };
            this.searchRunnable = r1;
            dispatchQueue.postRunnable(r1, 300);
        }

        /* access modifiers changed from: private */
        /* renamed from: processSearch */
        public void lambda$searchDialogs$1$NotificationsCustomSettingsActivity$SearchAdapter(String query) {
            AndroidUtilities.runOnUIThread(new Runnable(query) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NotificationsCustomSettingsActivity.SearchAdapter.this.lambda$processSearch$3$NotificationsCustomSettingsActivity$SearchAdapter(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$processSearch$3$NotificationsCustomSettingsActivity$SearchAdapter(String query) {
            this.searchAdapterHelper.queryServerSearch(query, true, NotificationsCustomSettingsActivity.this.currentType != 1, true, false, 0, false, 0);
            Utilities.searchQueue.postRunnable(new Runnable(query, new ArrayList<>(NotificationsCustomSettingsActivity.this.exceptions)) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ ArrayList f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    NotificationsCustomSettingsActivity.SearchAdapter.this.lambda$null$2$NotificationsCustomSettingsActivity$SearchAdapter(this.f$1, this.f$2);
                }
            });
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v27, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v24, resolved type: im.bclpbkiauv.tgnet.TLRPC$Chat} */
        /* JADX WARNING: Code restructure failed: missing block: B:62:0x0193, code lost:
            if (r9[0].contains(" " + r2) == false) goto L_0x0198;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:68:0x01b3, code lost:
            if (r15.contains(" " + r2) != false) goto L_0x01b5;
         */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:85:0x0215 A[LOOP:1: B:55:0x0163->B:85:0x0215, LOOP_END] */
        /* JADX WARNING: Removed duplicated region for block: B:98:0x01ce A[SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$null$2$NotificationsCustomSettingsActivity$SearchAdapter(java.lang.String r26, java.util.ArrayList r27) {
            /*
                r25 = this;
                r0 = r25
                java.lang.String r1 = r26.trim()
                java.lang.String r1 = r1.toLowerCase()
                int r2 = r1.length()
                if (r2 != 0) goto L_0x0023
                java.util.ArrayList r2 = new java.util.ArrayList
                r2.<init>()
                java.util.ArrayList r3 = new java.util.ArrayList
                r3.<init>()
                java.util.ArrayList r4 = new java.util.ArrayList
                r4.<init>()
                r0.updateSearchResults(r2, r3, r4)
                return
            L_0x0023:
                im.bclpbkiauv.messenger.LocaleController r2 = im.bclpbkiauv.messenger.LocaleController.getInstance()
                java.lang.String r2 = r2.getTranslitString(r1)
                boolean r3 = r1.equals(r2)
                if (r3 != 0) goto L_0x0037
                int r3 = r2.length()
                if (r3 != 0) goto L_0x0038
            L_0x0037:
                r2 = 0
            L_0x0038:
                r3 = 1
                r4 = 0
                if (r2 == 0) goto L_0x003e
                r5 = 1
                goto L_0x003f
            L_0x003e:
                r5 = 0
            L_0x003f:
                int r5 = r5 + r3
                java.lang.String[] r5 = new java.lang.String[r5]
                r5[r4] = r1
                if (r2 == 0) goto L_0x0048
                r5[r3] = r2
            L_0x0048:
                java.util.ArrayList r6 = new java.util.ArrayList
                r6.<init>()
                java.util.ArrayList r7 = new java.util.ArrayList
                r7.<init>()
                java.util.ArrayList r8 = new java.util.ArrayList
                r8.<init>()
                r9 = 2
                java.lang.String[] r9 = new java.lang.String[r9]
                r10 = 0
            L_0x005b:
                int r11 = r27.size()
                if (r10 >= r11) goto L_0x023f
                r11 = r27
                java.lang.Object r12 = r11.get(r10)
                im.bclpbkiauv.ui.NotificationsSettingsActivity$NotificationException r12 = (im.bclpbkiauv.ui.NotificationsSettingsActivity.NotificationException) r12
                long r13 = r12.did
                int r14 = (int) r13
                long r3 = r12.did
                r16 = 32
                long r3 = r3 >> r16
                int r4 = (int) r3
                r3 = 0
                if (r14 == 0) goto L_0x00f5
                if (r14 <= 0) goto L_0x00b4
                im.bclpbkiauv.ui.NotificationsCustomSettingsActivity r13 = im.bclpbkiauv.ui.NotificationsCustomSettingsActivity.this
                im.bclpbkiauv.messenger.MessagesController r13 = r13.getMessagesController()
                java.lang.Integer r15 = java.lang.Integer.valueOf(r14)
                im.bclpbkiauv.tgnet.TLRPC$User r13 = r13.getUser(r15)
                boolean r15 = r13.deleted
                if (r15 == 0) goto L_0x0095
                r18 = r1
                r20 = r2
                r21 = r5
                r13 = 1
                r19 = 0
                goto L_0x0233
            L_0x0095:
                if (r13 == 0) goto L_0x00ad
                java.lang.String r15 = r13.first_name
                r18 = r1
                java.lang.String r1 = r13.last_name
                java.lang.String r1 = im.bclpbkiauv.messenger.ContactsController.formatName(r15, r1)
                r15 = 0
                r9[r15] = r1
                java.lang.String r1 = r13.username
                r16 = 1
                r9[r16] = r1
                r1 = r13
                r3 = r1
                goto L_0x00b0
            L_0x00ad:
                r18 = r1
                r1 = r13
            L_0x00b0:
                r17 = 0
                goto L_0x0136
            L_0x00b4:
                r18 = r1
                im.bclpbkiauv.ui.NotificationsCustomSettingsActivity r1 = im.bclpbkiauv.ui.NotificationsCustomSettingsActivity.this
                im.bclpbkiauv.messenger.MessagesController r1 = r1.getMessagesController()
                int r13 = -r14
                java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
                im.bclpbkiauv.tgnet.TLRPC$Chat r1 = r1.getChat(r13)
                if (r1 == 0) goto L_0x00f2
                boolean r13 = r1.left
                if (r13 != 0) goto L_0x00e9
                boolean r13 = r1.kicked
                if (r13 != 0) goto L_0x00e9
                im.bclpbkiauv.tgnet.TLRPC$InputChannel r13 = r1.migrated_to
                if (r13 == 0) goto L_0x00dc
                r20 = r2
                r21 = r5
                r13 = 1
                r19 = 0
                goto L_0x0233
            L_0x00dc:
                java.lang.String r13 = r1.title
                r15 = 0
                r9[r15] = r13
                java.lang.String r13 = r1.username
                r16 = 1
                r9[r16] = r13
                r3 = r1
                goto L_0x00f2
            L_0x00e9:
                r20 = r2
                r21 = r5
                r13 = 1
                r19 = 0
                goto L_0x0233
            L_0x00f2:
                r17 = 0
                goto L_0x0136
            L_0x00f5:
                r18 = r1
                im.bclpbkiauv.ui.NotificationsCustomSettingsActivity r1 = im.bclpbkiauv.ui.NotificationsCustomSettingsActivity.this
                im.bclpbkiauv.messenger.MessagesController r1 = r1.getMessagesController()
                java.lang.Integer r13 = java.lang.Integer.valueOf(r4)
                im.bclpbkiauv.tgnet.TLRPC$EncryptedChat r1 = r1.getEncryptedChat(r13)
                if (r1 == 0) goto L_0x0132
                im.bclpbkiauv.ui.NotificationsCustomSettingsActivity r13 = im.bclpbkiauv.ui.NotificationsCustomSettingsActivity.this
                im.bclpbkiauv.messenger.MessagesController r13 = r13.getMessagesController()
                int r15 = r1.user_id
                java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
                im.bclpbkiauv.tgnet.TLRPC$User r15 = r13.getUser(r15)
                if (r15 == 0) goto L_0x012d
                java.lang.String r13 = r15.first_name
                r19 = r1
                java.lang.String r1 = r15.last_name
                java.lang.String r1 = im.bclpbkiauv.messenger.ContactsController.formatName(r13, r1)
                r17 = 0
                r9[r17] = r1
                java.lang.String r1 = r15.username
                r13 = 1
                r9[r13] = r1
                goto L_0x0136
            L_0x012d:
                r19 = r1
                r17 = 0
                goto L_0x0136
            L_0x0132:
                r19 = r1
                r17 = 0
            L_0x0136:
                r1 = r9[r17]
                r15 = r9[r17]
                java.lang.String r15 = r15.toLowerCase()
                r9[r17] = r15
                im.bclpbkiauv.messenger.LocaleController r15 = im.bclpbkiauv.messenger.LocaleController.getInstance()
                r13 = r9[r17]
                java.lang.String r13 = r15.getTranslitString(r13)
                r15 = r9[r17]
                if (r15 == 0) goto L_0x0159
                r15 = r9[r17]
                boolean r15 = r15.equals(r13)
                if (r15 == 0) goto L_0x0159
                r13 = 0
                r15 = r13
                goto L_0x015a
            L_0x0159:
                r15 = r13
            L_0x015a:
                r13 = 0
                r17 = 0
                r24 = r17
                r17 = r13
                r13 = r24
            L_0x0163:
                r20 = r2
                int r2 = r5.length
                if (r13 >= r2) goto L_0x0229
                r2 = r5[r13]
                r19 = 0
                r21 = r9[r19]
                r22 = r4
                java.lang.String r4 = " "
                if (r21 == 0) goto L_0x0196
                r21 = r5
                r5 = r9[r19]
                boolean r5 = r5.startsWith(r2)
                if (r5 != 0) goto L_0x01b5
                r5 = r9[r19]
                java.lang.StringBuilder r11 = new java.lang.StringBuilder
                r11.<init>()
                r11.append(r4)
                r11.append(r2)
                java.lang.String r11 = r11.toString()
                boolean r5 = r5.contains(r11)
                if (r5 != 0) goto L_0x01b5
                goto L_0x0198
            L_0x0196:
                r21 = r5
            L_0x0198:
                if (r15 == 0) goto L_0x01b9
                boolean r5 = r15.startsWith(r2)
                if (r5 != 0) goto L_0x01b5
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                r5.append(r4)
                r5.append(r2)
                java.lang.String r4 = r5.toString()
                boolean r4 = r15.contains(r4)
                if (r4 == 0) goto L_0x01b9
            L_0x01b5:
                r4 = 1
                r5 = r4
                r4 = r13
                goto L_0x01cc
            L_0x01b9:
                r4 = 1
                r5 = r9[r4]
                if (r5 == 0) goto L_0x01c9
                r5 = r9[r4]
                r4 = r13
                boolean r5 = r5.startsWith(r2)
                if (r5 == 0) goto L_0x01ca
                r5 = 2
                goto L_0x01cc
            L_0x01c9:
                r4 = r13
            L_0x01ca:
                r5 = r17
            L_0x01cc:
                if (r5 == 0) goto L_0x0215
                r11 = 0
                r13 = 1
                if (r5 != r13) goto L_0x01de
                java.lang.CharSequence r11 = im.bclpbkiauv.messenger.AndroidUtilities.generateSearchName(r1, r11, r2)
                r8.add(r11)
                r23 = r1
                r17 = r5
                goto L_0x020c
            L_0x01de:
                java.lang.StringBuilder r11 = new java.lang.StringBuilder
                r11.<init>()
                r23 = r1
                java.lang.String r1 = "@"
                r11.append(r1)
                r17 = r5
                r5 = r9[r13]
                r11.append(r5)
                java.lang.String r5 = r11.toString()
                java.lang.StringBuilder r11 = new java.lang.StringBuilder
                r11.<init>()
                r11.append(r1)
                r11.append(r2)
                java.lang.String r1 = r11.toString()
                r11 = 0
                java.lang.CharSequence r1 = im.bclpbkiauv.messenger.AndroidUtilities.generateSearchName(r5, r11, r1)
                r8.add(r1)
            L_0x020c:
                r7.add(r12)
                if (r3 == 0) goto L_0x0233
                r6.add(r3)
                goto L_0x0233
            L_0x0215:
                r23 = r1
                r17 = r5
                r13 = 1
                int r1 = r4 + 1
                r11 = r27
                r13 = r1
                r2 = r20
                r5 = r21
                r4 = r22
                r1 = r23
                goto L_0x0163
            L_0x0229:
                r23 = r1
                r22 = r4
                r21 = r5
                r4 = r13
                r13 = 1
                r19 = 0
            L_0x0233:
                int r10 = r10 + 1
                r1 = r18
                r2 = r20
                r5 = r21
                r3 = 1
                r4 = 0
                goto L_0x005b
            L_0x023f:
                r0.updateSearchResults(r6, r7, r8)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.NotificationsCustomSettingsActivity.SearchAdapter.lambda$null$2$NotificationsCustomSettingsActivity$SearchAdapter(java.lang.String, java.util.ArrayList):void");
        }

        private void updateSearchResults(ArrayList<TLObject> result, ArrayList<NotificationsSettingsActivity.NotificationException> exceptions, ArrayList<CharSequence> names) {
            AndroidUtilities.runOnUIThread(new Runnable(exceptions, names, result) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ ArrayList f$2;
                private final /* synthetic */ ArrayList f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    NotificationsCustomSettingsActivity.SearchAdapter.this.lambda$updateSearchResults$4$NotificationsCustomSettingsActivity$SearchAdapter(this.f$1, this.f$2, this.f$3);
                }
            });
        }

        public /* synthetic */ void lambda$updateSearchResults$4$NotificationsCustomSettingsActivity$SearchAdapter(ArrayList exceptions, ArrayList names, ArrayList result) {
            this.searchRunnable = null;
            this.searchResult = exceptions;
            this.searchResultNames = names;
            this.searchAdapterHelper.mergeResults(result);
            if (NotificationsCustomSettingsActivity.this.searching && !this.searchAdapterHelper.isSearchInProgress()) {
                NotificationsCustomSettingsActivity.this.emptyView.showTextView();
            }
            notifyDataSetChanged();
        }

        public Object getObject(int position) {
            if (position >= 0 && position < this.searchResult.size()) {
                return this.searchResult.get(position);
            }
            int position2 = position - (this.searchResult.size() + 1);
            ArrayList<TLObject> globalSearch = this.searchAdapterHelper.getGlobalSearch();
            if (position2 < 0 || position2 >= globalSearch.size()) {
                return null;
            }
            return this.searchAdapterHelper.getGlobalSearch().get(position2);
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        public int getItemCount() {
            int count = this.searchResult.size();
            ArrayList<TLObject> globalSearch = this.searchAdapterHelper.getGlobalSearch();
            if (!globalSearch.isEmpty()) {
                return count + globalSearch.size() + 1;
            }
            return count;
        }

        /* JADX WARNING: type inference failed for: r0v2, types: [im.bclpbkiauv.ui.cells.GraySectionCell] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r8, int r9) {
            /*
                r7 = this;
                if (r9 == 0) goto L_0x000a
                im.bclpbkiauv.ui.cells.GraySectionCell r0 = new im.bclpbkiauv.ui.cells.GraySectionCell
                android.content.Context r1 = r7.mContext
                r0.<init>(r1)
                goto L_0x0021
            L_0x000a:
                im.bclpbkiauv.ui.cells.UserCell r0 = new im.bclpbkiauv.ui.cells.UserCell
                android.content.Context r2 = r7.mContext
                r3 = 4
                r4 = 0
                r5 = 0
                r6 = 1
                r1 = r0
                r1.<init>(r2, r3, r4, r5, r6)
                java.lang.String r1 = "windowBackgroundWhite"
                int r1 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r1)
                r0.setBackgroundColor(r1)
            L_0x0021:
                im.bclpbkiauv.ui.components.RecyclerListView$Holder r1 = new im.bclpbkiauv.ui.components.RecyclerListView$Holder
                r1.<init>(r0)
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.NotificationsCustomSettingsActivity.SearchAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int itemViewType = holder.getItemViewType();
            boolean z = true;
            if (itemViewType == 0) {
                UserCell cell = (UserCell) holder.itemView;
                if (position < this.searchResult.size()) {
                    NotificationsSettingsActivity.NotificationException notificationException = this.searchResult.get(position);
                    CharSequence charSequence = this.searchResultNames.get(position);
                    if (position == this.searchResult.size() - 1) {
                        z = false;
                    }
                    cell.setException(notificationException, charSequence, z);
                    cell.setAddButtonVisible(false);
                    return;
                }
                int position2 = position - (this.searchResult.size() + 1);
                ArrayList<TLObject> globalSearch = this.searchAdapterHelper.getGlobalSearch();
                cell.setData(globalSearch.get(position2), (CharSequence) null, LocaleController.getString("NotificationsOn", R.string.NotificationsOn), 0, position2 != globalSearch.size() - 1);
                cell.setAddButtonVisible(true);
            } else if (itemViewType == 1) {
                ((GraySectionCell) holder.itemView).setText(LocaleController.getString("AddToExceptions", R.string.AddToExceptions));
            }
        }

        public int getItemViewType(int position) {
            if (position == this.searchResult.size()) {
                return 1;
            }
            return 0;
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return (type == 0 || type == 4) ? false : true;
        }

        public int getItemCount() {
            return NotificationsCustomSettingsActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    view = new TextCheckCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 2:
                    view = new UserCell(this.mContext, 6, 0, false);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                    view = new TextColorCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 4:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 5:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 6:
                    view = new NotificationsCheckCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                default:
                    view = new TextCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean enabled;
            int color;
            int option;
            String value;
            int value2;
            int value3;
            String value4;
            int offUntil;
            String text;
            int iconType;
            RecyclerView.ViewHolder viewHolder = holder;
            int i = position;
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 0:
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    if (i == NotificationsCustomSettingsActivity.this.messageSectionRow) {
                        headerCell.setText(LocaleController.getString("SETTINGS", R.string.SETTINGS));
                        return;
                    }
                    return;
                case 1:
                    TextCheckCell checkCell = (TextCheckCell) viewHolder.itemView;
                    SharedPreferences preferences = NotificationsCustomSettingsActivity.this.getNotificationsSettings();
                    if (i == NotificationsCustomSettingsActivity.this.previewRow) {
                        if (NotificationsCustomSettingsActivity.this.currentType == 1) {
                            enabled = preferences.getBoolean("EnablePreviewAll", true);
                        } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                            enabled = preferences.getBoolean("EnablePreviewGroup", true);
                        } else {
                            enabled = preferences.getBoolean("EnablePreviewChannel", true);
                        }
                        checkCell.setTextAndCheck(LocaleController.getString("MessagePreview", R.string.MessagePreview), enabled, true);
                        return;
                    }
                    return;
                case 2:
                    UserCell cell = (UserCell) viewHolder.itemView;
                    NotificationsSettingsActivity.NotificationException exception = (NotificationsSettingsActivity.NotificationException) NotificationsCustomSettingsActivity.this.exceptions.get(i - NotificationsCustomSettingsActivity.this.exceptionsStartRow);
                    if (i != NotificationsCustomSettingsActivity.this.exceptionsEndRow - 1) {
                        z = true;
                    }
                    cell.setException(exception, (CharSequence) null, z);
                    return;
                case 3:
                    TextColorCell textColorCell = (TextColorCell) viewHolder.itemView;
                    SharedPreferences preferences2 = NotificationsCustomSettingsActivity.this.getNotificationsSettings();
                    if (NotificationsCustomSettingsActivity.this.currentType == 1) {
                        color = preferences2.getInt("MessagesLed", -16776961);
                    } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                        color = preferences2.getInt("GroupLed", -16776961);
                    } else {
                        color = preferences2.getInt("ChannelLed", -16776961);
                    }
                    int a = 0;
                    while (true) {
                        if (a < 9) {
                            if (TextColorCell.colorsToSave[a] == color) {
                                color = TextColorCell.colors[a];
                            } else {
                                a++;
                            }
                        }
                    }
                    textColorCell.setTextAndColor(LocaleController.getString("LedColor", R.string.LedColor), color, true);
                    return;
                case 4:
                    if (i == NotificationsCustomSettingsActivity.this.deleteAllSectionRow || ((i == NotificationsCustomSettingsActivity.this.groupSection2Row && NotificationsCustomSettingsActivity.this.exceptionsSection2Row == -1) || (i == NotificationsCustomSettingsActivity.this.exceptionsSection2Row && NotificationsCustomSettingsActivity.this.deleteAllRow == -1))) {
                        viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    }
                case 5:
                    TextSettingsCell textCell = (TextSettingsCell) viewHolder.itemView;
                    SharedPreferences preferences3 = NotificationsCustomSettingsActivity.this.getNotificationsSettings();
                    if (i == NotificationsCustomSettingsActivity.this.messageSoundRow) {
                        if (NotificationsCustomSettingsActivity.this.currentType == 1) {
                            value4 = preferences3.getString("GlobalSound", LocaleController.getString("SoundDefault", R.string.SoundDefault));
                        } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                            value4 = preferences3.getString("GroupSound", LocaleController.getString("SoundDefault", R.string.SoundDefault));
                        } else {
                            value4 = preferences3.getString("ChannelSound", LocaleController.getString("SoundDefault", R.string.SoundDefault));
                        }
                        if (value4.equals("NoSound")) {
                            value4 = LocaleController.getString("NoSound", R.string.NoSound);
                        }
                        textCell.setTextAndValue(LocaleController.getString("Sound", R.string.Sound), value4, true);
                        return;
                    } else if (i == NotificationsCustomSettingsActivity.this.messageVibrateRow) {
                        if (NotificationsCustomSettingsActivity.this.currentType == 1) {
                            value3 = preferences3.getInt("vibrate_messages", 0);
                        } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                            value3 = preferences3.getInt("vibrate_group", 0);
                        } else {
                            value3 = preferences3.getInt("vibrate_channel", 0);
                        }
                        if (value3 == 0) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("VibrationDefault", R.string.VibrationDefault), true);
                            return;
                        } else if (value3 == 1) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("Short", R.string.Short), true);
                            return;
                        } else if (value3 == 2) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("VibrationDisabled", R.string.VibrationDisabled), true);
                            return;
                        } else if (value3 == 3) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("Long", R.string.Long), true);
                            return;
                        } else if (value3 == 4) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("OnlyIfSilent", R.string.OnlyIfSilent), true);
                            return;
                        } else {
                            return;
                        }
                    } else if (i == NotificationsCustomSettingsActivity.this.messagePriorityRow) {
                        if (NotificationsCustomSettingsActivity.this.currentType == 1) {
                            value2 = preferences3.getInt("priority_messages", 1);
                        } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                            value2 = preferences3.getInt("priority_group", 1);
                        } else {
                            value2 = preferences3.getInt("priority_channel", 1);
                        }
                        if (value2 == 0) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityHigh", R.string.NotificationsPriorityHigh), true);
                            return;
                        } else if (value2 == 1 || value2 == 2) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityUrgent", R.string.NotificationsPriorityUrgent), true);
                            return;
                        } else if (value2 == 4) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityLow", R.string.NotificationsPriorityLow), true);
                            return;
                        } else if (value2 == 5) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityMedium", R.string.NotificationsPriorityMedium), true);
                            return;
                        } else {
                            return;
                        }
                    } else if (i == NotificationsCustomSettingsActivity.this.messagePopupNotificationRow) {
                        if (NotificationsCustomSettingsActivity.this.currentType == 1) {
                            option = preferences3.getInt("popupAll", 0);
                        } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                            option = preferences3.getInt("popupGroup", 0);
                        } else {
                            option = preferences3.getInt("popupChannel", 0);
                        }
                        if (option == 0) {
                            value = LocaleController.getString("NoPopup", R.string.NoPopup);
                        } else if (option == 1) {
                            value = LocaleController.getString("OnlyWhenScreenOn", R.string.OnlyWhenScreenOn);
                        } else if (option == 2) {
                            value = LocaleController.getString("OnlyWhenScreenOff", R.string.OnlyWhenScreenOff);
                        } else {
                            value = LocaleController.getString("AlwaysShowPopup", R.string.AlwaysShowPopup);
                        }
                        textCell.setTextAndValue(LocaleController.getString("PopupNotification", R.string.PopupNotification), value, true);
                        return;
                    } else {
                        return;
                    }
                case 6:
                    NotificationsCheckCell checkCell2 = (NotificationsCheckCell) viewHolder.itemView;
                    checkCell2.setDrawLine(false);
                    StringBuilder builder = new StringBuilder();
                    SharedPreferences preferences4 = NotificationsCustomSettingsActivity.this.getNotificationsSettings();
                    if (NotificationsCustomSettingsActivity.this.currentType == 1) {
                        text = LocaleController.getString("NotificationsForPrivateChats", R.string.NotificationsForPrivateChats);
                        offUntil = preferences4.getInt("EnableAll2", 0);
                    } else if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                        text = LocaleController.getString("NotificationsForGroups", R.string.NotificationsForGroups);
                        offUntil = preferences4.getInt("EnableGroup2", 0);
                    } else {
                        text = LocaleController.getString("NotificationsForChannels", R.string.NotificationsForChannels);
                        offUntil = preferences4.getInt("EnableChannel2", 0);
                    }
                    int currentTime = NotificationsCustomSettingsActivity.this.getConnectionsManager().getCurrentTime();
                    boolean z2 = offUntil < currentTime;
                    boolean enabled2 = z2;
                    if (z2) {
                        builder.append(LocaleController.getString("NotificationsOn", R.string.NotificationsOn));
                        iconType = 0;
                    } else if (offUntil - 31536000 >= currentTime) {
                        builder.append(LocaleController.getString("NotificationsOff", R.string.NotificationsOff));
                        iconType = 0;
                    } else {
                        builder.append(LocaleController.formatString("NotificationsOffUntil", R.string.NotificationsOffUntil, LocaleController.stringForMessageListDate((long) offUntil)));
                        iconType = 2;
                    }
                    int i2 = currentTime;
                    checkCell2.setTextAndValueAndCheck(text, builder, enabled2, iconType, false);
                    return;
                case 7:
                    TextCell textCell2 = (TextCell) viewHolder.itemView;
                    if (i == NotificationsCustomSettingsActivity.this.exceptionsAddRow) {
                        String string = LocaleController.getString("NotificationsAddAnException", R.string.NotificationsAddAnException);
                        if (NotificationsCustomSettingsActivity.this.exceptionsStartRow != -1) {
                            z = true;
                        }
                        textCell2.setTextAndIcon(string, R.drawable.actions_addmember2, z);
                        textCell2.setColors(Theme.key_windowBackgroundWhiteBlueIcon, Theme.key_windowBackgroundWhiteBlueButton);
                        return;
                    } else if (i == NotificationsCustomSettingsActivity.this.deleteAllRow) {
                        textCell2.setText(LocaleController.getString("NotificationsDeleteAllException", R.string.NotificationsDeleteAllException), false);
                        textCell2.setColors((String) null, Theme.key_windowBackgroundWhiteRedText5);
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            if (NotificationsCustomSettingsActivity.this.exceptions != null && NotificationsCustomSettingsActivity.this.exceptions.isEmpty()) {
                boolean enabled = NotificationsCustomSettingsActivity.this.getNotificationsController().isGlobalNotificationsEnabled(NotificationsCustomSettingsActivity.this.currentType);
                int itemViewType = holder.getItemViewType();
                if (itemViewType == 0) {
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (holder.getAdapterPosition() == NotificationsCustomSettingsActivity.this.messageSectionRow) {
                        headerCell.setEnabled(enabled, (ArrayList<Animator>) null);
                    } else {
                        headerCell.setEnabled(true, (ArrayList<Animator>) null);
                    }
                } else if (itemViewType == 1) {
                    ((TextCheckCell) holder.itemView).setEnabled(enabled, (ArrayList<Animator>) null);
                } else if (itemViewType == 3) {
                    ((TextColorCell) holder.itemView).setEnabled(enabled, (ArrayList<Animator>) null);
                } else if (itemViewType == 5) {
                    ((TextSettingsCell) holder.itemView).setEnabled(enabled, (ArrayList<Animator>) null);
                }
            }
        }

        public int getItemViewType(int position) {
            if (position == NotificationsCustomSettingsActivity.this.messageSectionRow) {
                return 0;
            }
            if (position == NotificationsCustomSettingsActivity.this.previewRow) {
                return 1;
            }
            if (position >= NotificationsCustomSettingsActivity.this.exceptionsStartRow && position < NotificationsCustomSettingsActivity.this.exceptionsEndRow) {
                return 2;
            }
            if (position == NotificationsCustomSettingsActivity.this.messageLedRow) {
                return 3;
            }
            if (position == NotificationsCustomSettingsActivity.this.groupSection2Row || position == NotificationsCustomSettingsActivity.this.alertSection2Row || position == NotificationsCustomSettingsActivity.this.exceptionsSection2Row || position == NotificationsCustomSettingsActivity.this.deleteAllSectionRow) {
                return 4;
            }
            if (position == NotificationsCustomSettingsActivity.this.alertRow) {
                return 6;
            }
            if (position == NotificationsCustomSettingsActivity.this.exceptionsAddRow || position == NotificationsCustomSettingsActivity.this.deleteAllRow) {
                return 7;
            }
            return 5;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                NotificationsCustomSettingsActivity.this.lambda$getThemeDescriptions$12$NotificationsCustomSettingsActivity();
            }
        };
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = cellDelegate;
        RecyclerListView recyclerListView = this.listView;
        RecyclerListView recyclerListView2 = recyclerListView;
        RecyclerListView recyclerListView3 = this.listView;
        RecyclerListView recyclerListView4 = recyclerListView3;
        RecyclerListView recyclerListView5 = this.listView;
        RecyclerListView recyclerListView6 = recyclerListView5;
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCheckCell.class, TextColorCell.class, TextSettingsCell.class, UserCell.class, NotificationsCheckCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueHeader), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrack), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrackChecked), new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayIcon), new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, (String) Theme.key_windowBackgroundWhiteGrayText), new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, (String) Theme.key_windowBackgroundWhiteBlueText), new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, (Paint) null, new Drawable[]{Theme.avatar_savedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_text), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundRed), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundOrange), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundViolet), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundGreen), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundCyan), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundBlue), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundPink), new ThemeDescription((View) this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_graySectionText), new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_graySection), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrack), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrackChecked), new ThemeDescription((View) this.listView, 0, new Class[]{TextColorCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteValueText), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) recyclerListView2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueButton), new ThemeDescription((View) recyclerListView4, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteRedText5), new ThemeDescription((View) recyclerListView6, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueIcon)};
    }

    public /* synthetic */ void lambda$getThemeDescriptions$12$NotificationsCustomSettingsActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof UserCell) {
                    ((UserCell) child).update(0);
                }
            }
        }
    }
}
