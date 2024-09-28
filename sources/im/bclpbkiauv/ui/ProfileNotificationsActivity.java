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
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.upstream.cache.ContentMetadata;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.ChatObject;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.NotificationsController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.NotificationsSettingsActivity;
import im.bclpbkiauv.ui.ProfileNotificationsActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.HeaderCell;
import im.bclpbkiauv.ui.cells.RadioCell;
import im.bclpbkiauv.ui.cells.ShadowSectionCell;
import im.bclpbkiauv.ui.cells.TextCheckBoxCell;
import im.bclpbkiauv.ui.cells.TextCheckCell;
import im.bclpbkiauv.ui.cells.TextColorCell;
import im.bclpbkiauv.ui.cells.TextInfoPrivacyCell;
import im.bclpbkiauv.ui.cells.TextSettingsCell;
import im.bclpbkiauv.ui.cells.UserCell2;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import java.util.ArrayList;

public class ProfileNotificationsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int done_button = 1;
    /* access modifiers changed from: private */
    public ListAdapter adapter;
    /* access modifiers changed from: private */
    public boolean addingException;
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    /* access modifiers changed from: private */
    public int avatarRow;
    /* access modifiers changed from: private */
    public int avatarSectionRow;
    /* access modifiers changed from: private */
    public int callsRow;
    /* access modifiers changed from: private */
    public int callsVibrateRow;
    /* access modifiers changed from: private */
    public int colorRow;
    /* access modifiers changed from: private */
    public boolean customEnabled;
    /* access modifiers changed from: private */
    public int customInfoRow;
    /* access modifiers changed from: private */
    public int customRow;
    /* access modifiers changed from: private */
    public ProfileNotificationsActivityDelegate delegate;
    /* access modifiers changed from: private */
    public long dialog_id;
    /* access modifiers changed from: private */
    public int enableRow;
    /* access modifiers changed from: private */
    public int generalRow;
    /* access modifiers changed from: private */
    public int ledInfoRow;
    /* access modifiers changed from: private */
    public int ledRow;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public boolean notificationsEnabled;
    /* access modifiers changed from: private */
    public int popupDisabledRow;
    /* access modifiers changed from: private */
    public int popupEnabledRow;
    /* access modifiers changed from: private */
    public int popupInfoRow;
    /* access modifiers changed from: private */
    public int popupRow;
    /* access modifiers changed from: private */
    public int previewRow;
    /* access modifiers changed from: private */
    public int priorityInfoRow;
    /* access modifiers changed from: private */
    public int priorityRow;
    /* access modifiers changed from: private */
    public int ringtoneInfoRow;
    /* access modifiers changed from: private */
    public int ringtoneRow;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int smartRow;
    /* access modifiers changed from: private */
    public int soundRow;
    /* access modifiers changed from: private */
    public int vibrateRow;

    public interface ProfileNotificationsActivityDelegate {
        void didCreateNewException(NotificationsSettingsActivity.NotificationException notificationException);
    }

    public ProfileNotificationsActivity(Bundle args) {
        super(args);
        this.dialog_id = args.getLong("dialog_id");
        this.addingException = args.getBoolean("exception", false);
    }

    public boolean onFragmentCreate() {
        boolean isChannel;
        this.rowCount = 0;
        if (this.addingException) {
            int i = 0 + 1;
            this.rowCount = i;
            this.avatarRow = 0;
            this.rowCount = i + 1;
            this.avatarSectionRow = i;
            this.customRow = -1;
            this.customInfoRow = -1;
        } else {
            this.avatarRow = -1;
            this.avatarSectionRow = -1;
            int i2 = 0 + 1;
            this.rowCount = i2;
            this.customRow = 0;
            this.rowCount = i2 + 1;
            this.customInfoRow = i2;
        }
        int i3 = this.rowCount;
        int i4 = i3 + 1;
        this.rowCount = i4;
        this.generalRow = i3;
        if (this.addingException) {
            this.rowCount = i4 + 1;
            this.enableRow = i4;
        } else {
            this.enableRow = -1;
        }
        if (((int) this.dialog_id) != 0) {
            int i5 = this.rowCount;
            this.rowCount = i5 + 1;
            this.previewRow = i5;
        } else {
            this.previewRow = -1;
        }
        int i6 = this.rowCount;
        int i7 = i6 + 1;
        this.rowCount = i7;
        this.soundRow = i6;
        int i8 = i7 + 1;
        this.rowCount = i8;
        this.vibrateRow = i7;
        if (((int) this.dialog_id) < 0) {
            this.rowCount = i8 + 1;
            this.smartRow = i8;
        } else {
            this.smartRow = -1;
        }
        if (Build.VERSION.SDK_INT >= 21) {
            int i9 = this.rowCount;
            this.rowCount = i9 + 1;
            this.priorityRow = i9;
        } else {
            this.priorityRow = -1;
        }
        int i10 = this.rowCount;
        this.rowCount = i10 + 1;
        this.priorityInfoRow = i10;
        int lower_id = (int) this.dialog_id;
        if (lower_id < 0) {
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-lower_id));
            isChannel = ChatObject.isChannel(chat) && !chat.megagroup;
        } else {
            isChannel = false;
        }
        if (lower_id == 0 || isChannel) {
            this.popupRow = -1;
            this.popupEnabledRow = -1;
            this.popupDisabledRow = -1;
            this.popupInfoRow = -1;
        } else {
            int i11 = this.rowCount;
            int i12 = i11 + 1;
            this.rowCount = i12;
            this.popupRow = i11;
            int i13 = i12 + 1;
            this.rowCount = i13;
            this.popupEnabledRow = i12;
            int i14 = i13 + 1;
            this.rowCount = i14;
            this.popupDisabledRow = i13;
            this.rowCount = i14 + 1;
            this.popupInfoRow = i14;
        }
        if (lower_id > 0) {
            int i15 = this.rowCount;
            int i16 = i15 + 1;
            this.rowCount = i16;
            this.callsRow = i15;
            int i17 = i16 + 1;
            this.rowCount = i17;
            this.callsVibrateRow = i16;
            int i18 = i17 + 1;
            this.rowCount = i18;
            this.ringtoneRow = i17;
            this.rowCount = i18 + 1;
            this.ringtoneInfoRow = i18;
        } else {
            this.callsRow = -1;
            this.callsVibrateRow = -1;
            this.ringtoneRow = -1;
            this.ringtoneInfoRow = -1;
        }
        int i19 = this.rowCount;
        int i20 = i19 + 1;
        this.rowCount = i20;
        this.ledRow = i19;
        int i21 = i20 + 1;
        this.rowCount = i21;
        this.colorRow = i20;
        this.rowCount = i21 + 1;
        this.ledInfoRow = i21;
        SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
        StringBuilder sb = new StringBuilder();
        sb.append(ContentMetadata.KEY_CUSTOM_PREFIX);
        sb.append(this.dialog_id);
        this.customEnabled = preferences.getBoolean(sb.toString(), false) || this.addingException;
        boolean hasOverride = preferences.contains("notify2_" + this.dialog_id);
        int value = preferences.getInt("notify2_" + this.dialog_id, 0);
        if (value == 0) {
            if (hasOverride) {
                this.notificationsEnabled = true;
            } else {
                this.notificationsEnabled = NotificationsController.getInstance(this.currentAccount).isGlobalNotificationsEnabled(this.dialog_id);
            }
        } else if (value == 1) {
            this.notificationsEnabled = true;
        } else if (value == 2) {
            this.notificationsEnabled = false;
        } else {
            this.notificationsEnabled = false;
        }
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.notificationsSettingsUpdated);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
    }

    public View createView(final Context context) {
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    if (!ProfileNotificationsActivity.this.addingException && ProfileNotificationsActivity.this.notificationsEnabled && ProfileNotificationsActivity.this.customEnabled) {
                        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit();
                        edit.putInt("notify2_" + ProfileNotificationsActivity.this.dialog_id, 0).commit();
                    }
                } else if (id == 1) {
                    SharedPreferences preferences = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean(ContentMetadata.KEY_CUSTOM_PREFIX + ProfileNotificationsActivity.this.dialog_id, true);
                    TLRPC.Dialog dialog = MessagesController.getInstance(ProfileNotificationsActivity.this.currentAccount).dialogs_dict.get(ProfileNotificationsActivity.this.dialog_id);
                    if (ProfileNotificationsActivity.this.notificationsEnabled) {
                        editor.putInt("notify2_" + ProfileNotificationsActivity.this.dialog_id, 0);
                        MessagesStorage.getInstance(ProfileNotificationsActivity.this.currentAccount).setDialogFlags(ProfileNotificationsActivity.this.dialog_id, 0);
                        if (dialog != null) {
                            dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
                        }
                    } else {
                        editor.putInt("notify2_" + ProfileNotificationsActivity.this.dialog_id, 2);
                        NotificationsController.getInstance(ProfileNotificationsActivity.this.currentAccount).removeNotificationsForDialog(ProfileNotificationsActivity.this.dialog_id);
                        MessagesStorage.getInstance(ProfileNotificationsActivity.this.currentAccount).setDialogFlags(ProfileNotificationsActivity.this.dialog_id, 1);
                        if (dialog != null) {
                            dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
                            dialog.notify_settings.mute_until = Integer.MAX_VALUE;
                        }
                    }
                    editor.commit();
                    NotificationsController.getInstance(ProfileNotificationsActivity.this.currentAccount).updateServerNotificationsSettings(ProfileNotificationsActivity.this.dialog_id);
                    if (ProfileNotificationsActivity.this.delegate != null) {
                        NotificationsSettingsActivity.NotificationException exception = new NotificationsSettingsActivity.NotificationException();
                        exception.did = ProfileNotificationsActivity.this.dialog_id;
                        exception.hasCustom = true;
                        exception.notify = preferences.getInt("notify2_" + ProfileNotificationsActivity.this.dialog_id, 0);
                        if (exception.notify != 0) {
                            exception.muteUntil = preferences.getInt("notifyuntil_" + ProfileNotificationsActivity.this.dialog_id, 0);
                        }
                        ProfileNotificationsActivity.this.delegate.didCreateNewException(exception);
                    }
                }
                ProfileNotificationsActivity.this.finishFragment();
            }
        });
        if (this.addingException) {
            this.actionBar.setTitle(LocaleController.getString("NotificationsNewException", R.string.NotificationsNewException));
            this.actionBar.createMenu().addItem(1, (CharSequence) LocaleController.getString("Done", R.string.Done).toUpperCase());
        } else {
            this.actionBar.setTitle(LocaleController.getString("CustomNotifications", R.string.CustomNotifications));
        }
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        frameLayout.addView(recyclerListView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        this.listView.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.listView.setLayoutAnimation((LayoutAnimationController) null);
        this.listView.setLayoutManager(new LinearLayoutManager(context) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        });
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public void onItemClick(View view, int position) {
                if (position == ProfileNotificationsActivity.this.customRow && (view instanceof TextCheckBoxCell)) {
                    SharedPreferences preferences = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    ProfileNotificationsActivity profileNotificationsActivity = ProfileNotificationsActivity.this;
                    boolean unused = profileNotificationsActivity.customEnabled = true ^ profileNotificationsActivity.customEnabled;
                    ProfileNotificationsActivity profileNotificationsActivity2 = ProfileNotificationsActivity.this;
                    boolean unused2 = profileNotificationsActivity2.notificationsEnabled = profileNotificationsActivity2.customEnabled;
                    SharedPreferences.Editor edit = preferences.edit();
                    edit.putBoolean(ContentMetadata.KEY_CUSTOM_PREFIX + ProfileNotificationsActivity.this.dialog_id, ProfileNotificationsActivity.this.customEnabled).commit();
                    ((TextCheckBoxCell) view).setChecked(ProfileNotificationsActivity.this.customEnabled);
                    ProfileNotificationsActivity.this.checkRowsEnabled();
                } else if (ProfileNotificationsActivity.this.customEnabled && view.isEnabled()) {
                    if (position == ProfileNotificationsActivity.this.soundRow) {
                        try {
                            Intent tmpIntent = new Intent("android.intent.action.RINGTONE_PICKER");
                            tmpIntent.putExtra("android.intent.extra.ringtone.TYPE", 2);
                            tmpIntent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                            tmpIntent.putExtra("android.intent.extra.ringtone.SHOW_SILENT", true);
                            tmpIntent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(2));
                            SharedPreferences preferences2 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                            Uri currentSound = null;
                            String defaultPath = null;
                            Uri defaultUri = Settings.System.DEFAULT_NOTIFICATION_URI;
                            if (defaultUri != null) {
                                defaultPath = defaultUri.getPath();
                            }
                            String path = preferences2.getString("sound_path_" + ProfileNotificationsActivity.this.dialog_id, defaultPath);
                            if (path != null && !path.equals("NoSound")) {
                                currentSound = path.equals(defaultPath) ? defaultUri : Uri.parse(path);
                            }
                            tmpIntent.putExtra("android.intent.extra.ringtone.EXISTING_URI", currentSound);
                            ProfileNotificationsActivity.this.startActivityForResult(tmpIntent, 12);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    } else if (position == ProfileNotificationsActivity.this.ringtoneRow) {
                        try {
                            Intent tmpIntent2 = new Intent("android.intent.action.RINGTONE_PICKER");
                            tmpIntent2.putExtra("android.intent.extra.ringtone.TYPE", 1);
                            tmpIntent2.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                            tmpIntent2.putExtra("android.intent.extra.ringtone.SHOW_SILENT", true);
                            tmpIntent2.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(1));
                            SharedPreferences preferences3 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                            Uri currentSound2 = null;
                            String defaultPath2 = null;
                            Uri defaultUri2 = Settings.System.DEFAULT_NOTIFICATION_URI;
                            if (defaultUri2 != null) {
                                defaultPath2 = defaultUri2.getPath();
                            }
                            String path2 = preferences3.getString("ringtone_path_" + ProfileNotificationsActivity.this.dialog_id, defaultPath2);
                            if (path2 != null && !path2.equals("NoSound")) {
                                currentSound2 = path2.equals(defaultPath2) ? defaultUri2 : Uri.parse(path2);
                            }
                            tmpIntent2.putExtra("android.intent.extra.ringtone.EXISTING_URI", currentSound2);
                            ProfileNotificationsActivity.this.startActivityForResult(tmpIntent2, 13);
                        } catch (Exception e2) {
                            FileLog.e((Throwable) e2);
                        }
                    } else if (position == ProfileNotificationsActivity.this.vibrateRow) {
                        ProfileNotificationsActivity profileNotificationsActivity3 = ProfileNotificationsActivity.this;
                        profileNotificationsActivity3.showDialog(AlertsCreator.createVibrationSelectDialog(profileNotificationsActivity3.getParentActivity(), ProfileNotificationsActivity.this.dialog_id, false, false, new Runnable() {
                            public final void run() {
                                ProfileNotificationsActivity.AnonymousClass3.this.lambda$onItemClick$0$ProfileNotificationsActivity$3();
                            }
                        }));
                    } else if (position == ProfileNotificationsActivity.this.enableRow) {
                        TextCheckCell checkCell = (TextCheckCell) view;
                        boolean unused3 = ProfileNotificationsActivity.this.notificationsEnabled = true ^ checkCell.isChecked();
                        checkCell.setChecked(ProfileNotificationsActivity.this.notificationsEnabled);
                        ProfileNotificationsActivity.this.checkRowsEnabled();
                    } else if (position == ProfileNotificationsActivity.this.previewRow) {
                        TextCheckCell checkCell2 = (TextCheckCell) view;
                        SharedPreferences.Editor edit2 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit();
                        edit2.putBoolean("content_preview_" + ProfileNotificationsActivity.this.dialog_id, !checkCell2.isChecked()).commit();
                        checkCell2.setChecked(true ^ checkCell2.isChecked());
                    } else if (position == ProfileNotificationsActivity.this.callsVibrateRow) {
                        ProfileNotificationsActivity profileNotificationsActivity4 = ProfileNotificationsActivity.this;
                        profileNotificationsActivity4.showDialog(AlertsCreator.createVibrationSelectDialog(profileNotificationsActivity4.getParentActivity(), ProfileNotificationsActivity.this.dialog_id, "calls_vibrate_", new Runnable() {
                            public final void run() {
                                ProfileNotificationsActivity.AnonymousClass3.this.lambda$onItemClick$1$ProfileNotificationsActivity$3();
                            }
                        }));
                    } else if (position == ProfileNotificationsActivity.this.priorityRow) {
                        ProfileNotificationsActivity profileNotificationsActivity5 = ProfileNotificationsActivity.this;
                        profileNotificationsActivity5.showDialog(AlertsCreator.createPrioritySelectDialog(profileNotificationsActivity5.getParentActivity(), ProfileNotificationsActivity.this.dialog_id, -1, new Runnable() {
                            public final void run() {
                                ProfileNotificationsActivity.AnonymousClass3.this.lambda$onItemClick$2$ProfileNotificationsActivity$3();
                            }
                        }));
                    } else if (position == ProfileNotificationsActivity.this.smartRow) {
                        if (ProfileNotificationsActivity.this.getParentActivity() != null) {
                            final Context context1 = ProfileNotificationsActivity.this.getParentActivity();
                            SharedPreferences preferences4 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                            int notifyMaxCount = preferences4.getInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, 2);
                            int notifyDelay = preferences4.getInt("smart_delay_" + ProfileNotificationsActivity.this.dialog_id, 180);
                            if (notifyMaxCount == 0) {
                                notifyMaxCount = 2;
                            }
                            final int selected = ((((notifyDelay / 60) - 1) * 10) + notifyMaxCount) - 1;
                            RecyclerListView list = new RecyclerListView(ProfileNotificationsActivity.this.getParentActivity());
                            list.setLayoutManager(new LinearLayoutManager(context, 1, false));
                            list.setClipToPadding(true);
                            list.setAdapter(new RecyclerListView.SelectionAdapter() {
                                public int getItemCount() {
                                    return 100;
                                }

                                public boolean isEnabled(RecyclerView.ViewHolder holder) {
                                    return true;
                                }

                                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                    TextView textView = new TextView(context1) {
                                        /* access modifiers changed from: protected */
                                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                                            super.onMeasure(View.MeasureSpec.makeMeasureSpec(widthMeasureSpec, 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), 1073741824));
                                        }
                                    };
                                    textView.setGravity(17);
                                    textView.setTextSize(1, 18.0f);
                                    textView.setSingleLine(true);
                                    textView.setEllipsize(TextUtils.TruncateAt.END);
                                    textView.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                                    return new RecyclerListView.Holder(textView);
                                }

                                public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                                    TextView textView = (TextView) holder.itemView;
                                    textView.setTextColor(Theme.getColor(position == selected ? Theme.key_dialogTextGray : Theme.key_dialogTextBlack));
                                    textView.setText(LocaleController.formatString("SmartNotificationsDetail", R.string.SmartNotificationsDetail, LocaleController.formatPluralString("Times", (position % 10) + 1), LocaleController.formatPluralString("Minutes", (position / 10) + 1)));
                                }
                            });
                            list.setPadding(0, AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(8.0f));
                            list.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
                                public final void onItemClick(View view, int i) {
                                    ProfileNotificationsActivity.AnonymousClass3.this.lambda$onItemClick$3$ProfileNotificationsActivity$3(view, i);
                                }
                            });
                            AlertDialog.Builder builder = new AlertDialog.Builder((Context) ProfileNotificationsActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("SmartNotificationsAlert", R.string.SmartNotificationsAlert));
                            builder.setView(list);
                            builder.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                            builder.setNegativeButton(LocaleController.getString("SmartNotificationsDisabled", R.string.SmartNotificationsDisabled), new DialogInterface.OnClickListener() {
                                public final void onClick(DialogInterface dialogInterface, int i) {
                                    ProfileNotificationsActivity.AnonymousClass3.this.lambda$onItemClick$4$ProfileNotificationsActivity$3(dialogInterface, i);
                                }
                            });
                            ProfileNotificationsActivity.this.showDialog(builder.create());
                        }
                    } else if (position == ProfileNotificationsActivity.this.colorRow) {
                        if (ProfileNotificationsActivity.this.getParentActivity() != null) {
                            ProfileNotificationsActivity profileNotificationsActivity6 = ProfileNotificationsActivity.this;
                            profileNotificationsActivity6.showDialog(AlertsCreator.createColorSelectDialog(profileNotificationsActivity6.getParentActivity(), ProfileNotificationsActivity.this.dialog_id, -1, new Runnable() {
                                public final void run() {
                                    ProfileNotificationsActivity.AnonymousClass3.this.lambda$onItemClick$5$ProfileNotificationsActivity$3();
                                }
                            }));
                        }
                    } else if (position == ProfileNotificationsActivity.this.popupEnabledRow) {
                        SharedPreferences.Editor edit3 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit();
                        edit3.putInt("popup_" + ProfileNotificationsActivity.this.dialog_id, 1).commit();
                        ((RadioCell) view).setChecked(true, true);
                        View view2 = ProfileNotificationsActivity.this.listView.findViewWithTag(2);
                        if (view2 != null) {
                            ((RadioCell) view2).setChecked(false, true);
                        }
                    } else if (position == ProfileNotificationsActivity.this.popupDisabledRow) {
                        SharedPreferences.Editor edit4 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit();
                        edit4.putInt("popup_" + ProfileNotificationsActivity.this.dialog_id, 2).commit();
                        ((RadioCell) view).setChecked(true, true);
                        View view3 = ProfileNotificationsActivity.this.listView.findViewWithTag(1);
                        if (view3 != null) {
                            ((RadioCell) view3).setChecked(false, true);
                        }
                    }
                }
            }

            public /* synthetic */ void lambda$onItemClick$0$ProfileNotificationsActivity$3() {
                if (ProfileNotificationsActivity.this.adapter != null) {
                    ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.vibrateRow);
                }
            }

            public /* synthetic */ void lambda$onItemClick$1$ProfileNotificationsActivity$3() {
                if (ProfileNotificationsActivity.this.adapter != null) {
                    ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.callsVibrateRow);
                }
            }

            public /* synthetic */ void lambda$onItemClick$2$ProfileNotificationsActivity$3() {
                if (ProfileNotificationsActivity.this.adapter != null) {
                    ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.priorityRow);
                }
            }

            public /* synthetic */ void lambda$onItemClick$3$ProfileNotificationsActivity$3(View view1, int position1) {
                if (position1 >= 0 && position1 < 100) {
                    SharedPreferences preferences1 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    SharedPreferences.Editor edit = preferences1.edit();
                    edit.putInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, (position1 % 10) + 1).commit();
                    SharedPreferences.Editor edit2 = preferences1.edit();
                    edit2.putInt("smart_delay_" + ProfileNotificationsActivity.this.dialog_id, ((position1 / 10) + 1) * 60).commit();
                    if (ProfileNotificationsActivity.this.adapter != null) {
                        ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.smartRow);
                    }
                    ProfileNotificationsActivity.this.dismissCurrentDialog();
                }
            }

            public /* synthetic */ void lambda$onItemClick$4$ProfileNotificationsActivity$3(DialogInterface dialog, int which) {
                SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount).edit();
                edit.putInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, 0).commit();
                if (ProfileNotificationsActivity.this.adapter != null) {
                    ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.smartRow);
                }
                ProfileNotificationsActivity.this.dismissCurrentDialog();
            }

            public /* synthetic */ void lambda$onItemClick$5$ProfileNotificationsActivity$3() {
                if (ProfileNotificationsActivity.this.adapter != null) {
                    ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.colorRow);
                }
            }
        });
        return this.fragmentView;
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        Ringtone rng;
        if (resultCode == -1 && data != null) {
            Uri ringtone = (Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            String name = null;
            if (!(ringtone == null || (rng = RingtoneManager.getRingtone(ApplicationLoader.applicationContext, ringtone)) == null)) {
                if (requestCode == 13) {
                    if (ringtone.equals(Settings.System.DEFAULT_RINGTONE_URI)) {
                        name = LocaleController.getString("DefaultRingtone", R.string.DefaultRingtone);
                    } else {
                        name = rng.getTitle(getParentActivity());
                    }
                } else if (ringtone.equals(Settings.System.DEFAULT_NOTIFICATION_URI)) {
                    name = LocaleController.getString("SoundDefault", R.string.SoundDefault);
                } else {
                    name = rng.getTitle(getParentActivity());
                }
                rng.stop();
            }
            SharedPreferences.Editor editor = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            if (requestCode == 12) {
                if (name != null) {
                    editor.putString("sound_" + this.dialog_id, name);
                    editor.putString("sound_path_" + this.dialog_id, ringtone.toString());
                } else {
                    editor.putString("sound_" + this.dialog_id, "NoSound");
                    editor.putString("sound_path_" + this.dialog_id, "NoSound");
                }
            } else if (requestCode == 13) {
                if (name != null) {
                    editor.putString("ringtone_" + this.dialog_id, name);
                    editor.putString("ringtone_path_" + this.dialog_id, ringtone.toString());
                } else {
                    editor.putString("ringtone_" + this.dialog_id, "NoSound");
                    editor.putString("ringtone_path_" + this.dialog_id, "NoSound");
                }
            }
            editor.commit();
            ListAdapter listAdapter = this.adapter;
            if (listAdapter != null) {
                listAdapter.notifyItemChanged(requestCode == 13 ? this.ringtoneRow : this.soundRow);
            }
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.notificationsSettingsUpdated) {
            this.adapter.notifyDataSetChanged();
        }
    }

    public void setDelegate(ProfileNotificationsActivityDelegate profileNotificationsActivityDelegate) {
        this.delegate = profileNotificationsActivityDelegate;
    }

    /* access modifiers changed from: private */
    public void checkRowsEnabled() {
        int count = this.listView.getChildCount();
        ArrayList<Animator> animators = new ArrayList<>();
        for (int a = 0; a < count; a++) {
            RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.getChildViewHolder(this.listView.getChildAt(a));
            int type = holder.getItemViewType();
            int position = holder.getAdapterPosition();
            if (!(position == this.customRow || position == this.enableRow || type == 0)) {
                boolean z = false;
                if (type == 1) {
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    if (this.customEnabled && this.notificationsEnabled) {
                        z = true;
                    }
                    textCell.setEnabled(z, animators);
                } else if (type == 2) {
                    TextInfoPrivacyCell textCell2 = (TextInfoPrivacyCell) holder.itemView;
                    if (this.customEnabled && this.notificationsEnabled) {
                        z = true;
                    }
                    textCell2.setEnabled(z, animators);
                } else if (type == 3) {
                    TextColorCell textCell3 = (TextColorCell) holder.itemView;
                    if (this.customEnabled && this.notificationsEnabled) {
                        z = true;
                    }
                    textCell3.setEnabled(z, animators);
                } else if (type == 4) {
                    RadioCell radioCell = (RadioCell) holder.itemView;
                    if (this.customEnabled && this.notificationsEnabled) {
                        z = true;
                    }
                    radioCell.setEnabled(z, animators);
                } else if (type == 8 && position == this.previewRow) {
                    TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                    if (this.customEnabled && this.notificationsEnabled) {
                        z = true;
                    }
                    checkCell.setEnabled(z, animators);
                }
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
                    if (animator.equals(ProfileNotificationsActivity.this.animatorSet)) {
                        AnimatorSet unused = ProfileNotificationsActivity.this.animatorSet = null;
                    }
                }
            });
            this.animatorSet.setDuration(150);
            this.animatorSet.start();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;

        public ListAdapter(Context ctx) {
            this.context = ctx;
        }

        public int getItemCount() {
            return ProfileNotificationsActivity.this.rowCount;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            switch (holder.getItemViewType()) {
                case 0:
                case 2:
                case 6:
                case 7:
                    return false;
                case 1:
                case 3:
                case 4:
                    if (!ProfileNotificationsActivity.this.customEnabled || !ProfileNotificationsActivity.this.notificationsEnabled) {
                        return false;
                    }
                    return true;
                case 8:
                    TextCheckCell textCheckCell = (TextCheckCell) holder.itemView;
                    if (holder.getAdapterPosition() != ProfileNotificationsActivity.this.previewRow) {
                        return true;
                    }
                    if (!ProfileNotificationsActivity.this.customEnabled || !ProfileNotificationsActivity.this.notificationsEnabled) {
                        return false;
                    }
                    return true;
                default:
                    return true;
            }
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    View view2 = new HeaderCell(this.context);
                    view2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view2;
                    break;
                case 1:
                    View view3 = new TextSettingsCell(this.context);
                    view3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view3;
                    break;
                case 2:
                    view = new TextInfoPrivacyCell(this.context);
                    break;
                case 3:
                    View view4 = new TextColorCell(this.context);
                    view4.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view4;
                    break;
                case 4:
                    View view5 = new RadioCell(this.context);
                    view5.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view5;
                    break;
                case 5:
                    View view6 = new TextCheckBoxCell(this.context);
                    view6.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view6;
                    break;
                case 6:
                    View view7 = new UserCell2(this.context, 4, 0);
                    view7.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view7;
                    break;
                case 7:
                    view = new ShadowSectionCell(this.context);
                    break;
                default:
                    View view8 = new TextCheckCell(this.context);
                    view8.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view8;
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean z;
            int color;
            TLObject object;
            RecyclerView.ViewHolder viewHolder = holder;
            int i = position;
            boolean z2 = false;
            boolean z3 = true;
            switch (holder.getItemViewType()) {
                case 0:
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    if (i == ProfileNotificationsActivity.this.generalRow) {
                        headerCell.setText(LocaleController.getString("General", R.string.General));
                        return;
                    } else if (i == ProfileNotificationsActivity.this.popupRow) {
                        headerCell.setText(LocaleController.getString("ProfilePopupNotification", R.string.ProfilePopupNotification));
                        return;
                    } else if (i == ProfileNotificationsActivity.this.ledRow) {
                        headerCell.setText(LocaleController.getString("NotificationsLed", R.string.NotificationsLed));
                        return;
                    } else if (i == ProfileNotificationsActivity.this.callsRow) {
                        headerCell.setText(LocaleController.getString("VoipNotificationSettings", R.string.VoipNotificationSettings));
                        return;
                    } else {
                        return;
                    }
                case 1:
                    TextSettingsCell textCell = (TextSettingsCell) viewHolder.itemView;
                    SharedPreferences preferences = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    if (i == ProfileNotificationsActivity.this.soundRow) {
                        String value = preferences.getString("sound_" + ProfileNotificationsActivity.this.dialog_id, LocaleController.getString("SoundDefault", R.string.SoundDefault));
                        if (value.equals("NoSound")) {
                            value = LocaleController.getString("NoSound", R.string.NoSound);
                        }
                        textCell.setTextAndValue(LocaleController.getString("Sound", R.string.Sound), value, true);
                        return;
                    } else if (i == ProfileNotificationsActivity.this.ringtoneRow) {
                        String value2 = preferences.getString("ringtone_" + ProfileNotificationsActivity.this.dialog_id, LocaleController.getString("DefaultRingtone", R.string.DefaultRingtone));
                        if (value2.equals("NoSound")) {
                            value2 = LocaleController.getString("NoSound", R.string.NoSound);
                        }
                        textCell.setTextAndValue(LocaleController.getString("VoipSettingsRingtone", R.string.VoipSettingsRingtone), value2, false);
                        return;
                    } else if (i == ProfileNotificationsActivity.this.vibrateRow) {
                        int value3 = preferences.getInt("vibrate_" + ProfileNotificationsActivity.this.dialog_id, 0);
                        if (value3 == 0 || value3 == 4) {
                            String string = LocaleController.getString("Vibrate", R.string.Vibrate);
                            String string2 = LocaleController.getString("VibrationDefault", R.string.VibrationDefault);
                            if (!(ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1)) {
                                z2 = true;
                            }
                            textCell.setTextAndValue(string, string2, z2);
                            return;
                        } else if (value3 == 1) {
                            String string3 = LocaleController.getString("Vibrate", R.string.Vibrate);
                            String string4 = LocaleController.getString("Short", R.string.Short);
                            if (!(ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1)) {
                                z2 = true;
                            }
                            textCell.setTextAndValue(string3, string4, z2);
                            return;
                        } else if (value3 == 2) {
                            String string5 = LocaleController.getString("Vibrate", R.string.Vibrate);
                            String string6 = LocaleController.getString("VibrationDisabled", R.string.VibrationDisabled);
                            if (!(ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1)) {
                                z2 = true;
                            }
                            textCell.setTextAndValue(string5, string6, z2);
                            return;
                        } else if (value3 == 3) {
                            String string7 = LocaleController.getString("Vibrate", R.string.Vibrate);
                            String string8 = LocaleController.getString("Long", R.string.Long);
                            if (!(ProfileNotificationsActivity.this.smartRow == -1 && ProfileNotificationsActivity.this.priorityRow == -1)) {
                                z2 = true;
                            }
                            textCell.setTextAndValue(string7, string8, z2);
                            return;
                        } else {
                            return;
                        }
                    } else if (i == ProfileNotificationsActivity.this.priorityRow) {
                        int value4 = preferences.getInt("priority_" + ProfileNotificationsActivity.this.dialog_id, 3);
                        if (value4 == 0) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityHigh", R.string.NotificationsPriorityHigh), false);
                            return;
                        } else if (value4 == 1 || value4 == 2) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityUrgent", R.string.NotificationsPriorityUrgent), false);
                            return;
                        } else if (value4 == 3) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), LocaleController.getString("NotificationsPrioritySettings", R.string.NotificationsPrioritySettings), false);
                            return;
                        } else if (value4 == 4) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityLow", R.string.NotificationsPriorityLow), false);
                            return;
                        } else if (value4 == 5) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityMedium", R.string.NotificationsPriorityMedium), false);
                            return;
                        } else {
                            return;
                        }
                    } else if (i == ProfileNotificationsActivity.this.smartRow) {
                        int notifyMaxCount = preferences.getInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, 2);
                        int notifyDelay = preferences.getInt("smart_delay_" + ProfileNotificationsActivity.this.dialog_id, 180);
                        if (notifyMaxCount == 0) {
                            String string9 = LocaleController.getString("SmartNotifications", R.string.SmartNotifications);
                            String string10 = LocaleController.getString("SmartNotificationsDisabled", R.string.SmartNotificationsDisabled);
                            if (ProfileNotificationsActivity.this.priorityRow != -1) {
                                z2 = true;
                            }
                            textCell.setTextAndValue(string9, string10, z2);
                            return;
                        }
                        String minutes = LocaleController.formatPluralString("Minutes", notifyDelay / 60);
                        String string11 = LocaleController.getString("SmartNotifications", R.string.SmartNotifications);
                        String formatString = LocaleController.formatString("SmartNotificationsInfo", R.string.SmartNotificationsInfo, Integer.valueOf(notifyMaxCount), minutes);
                        if (ProfileNotificationsActivity.this.priorityRow != -1) {
                            z2 = true;
                        }
                        textCell.setTextAndValue(string11, formatString, z2);
                        return;
                    } else if (i == ProfileNotificationsActivity.this.callsVibrateRow) {
                        int value5 = preferences.getInt("calls_vibrate_" + ProfileNotificationsActivity.this.dialog_id, 0);
                        if (value5 == 0) {
                            z = true;
                        } else if (value5 == 4) {
                            z = true;
                        } else if (value5 == 1) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("Short", R.string.Short), true);
                            return;
                        } else if (value5 == 2) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("VibrationDisabled", R.string.VibrationDisabled), true);
                            return;
                        } else if (value5 == 3) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("Long", R.string.Long), true);
                            return;
                        } else {
                            return;
                        }
                        textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("VibrationDefault", R.string.VibrationDefault), z);
                        return;
                    } else {
                        return;
                    }
                case 2:
                    TextInfoPrivacyCell textCell2 = (TextInfoPrivacyCell) viewHolder.itemView;
                    if (i == ProfileNotificationsActivity.this.popupInfoRow) {
                        textCell2.setText(LocaleController.getString("ProfilePopupNotificationInfo", R.string.ProfilePopupNotificationInfo));
                        textCell2.setBackgroundDrawable(Theme.getThemedDrawable(this.context, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (i == ProfileNotificationsActivity.this.ledInfoRow) {
                        textCell2.setText(LocaleController.getString("NotificationsLedInfo", R.string.NotificationsLedInfo));
                        textCell2.setBackgroundDrawable(Theme.getThemedDrawable(this.context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (i == ProfileNotificationsActivity.this.priorityInfoRow) {
                        if (ProfileNotificationsActivity.this.priorityRow == -1) {
                            textCell2.setText("");
                        } else {
                            textCell2.setText(LocaleController.getString("PriorityInfo", R.string.PriorityInfo));
                        }
                        textCell2.setBackgroundDrawable(Theme.getThemedDrawable(this.context, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (i == ProfileNotificationsActivity.this.customInfoRow) {
                        textCell2.setText((CharSequence) null);
                        textCell2.setBackgroundDrawable(Theme.getThemedDrawable(this.context, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (i == ProfileNotificationsActivity.this.ringtoneInfoRow) {
                        textCell2.setText(LocaleController.getString("VoipRingtoneInfo", R.string.VoipRingtoneInfo));
                        textCell2.setBackgroundDrawable(Theme.getThemedDrawable(this.context, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        return;
                    }
                case 3:
                    TextColorCell textCell3 = (TextColorCell) viewHolder.itemView;
                    SharedPreferences preferences2 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    if (preferences2.contains("color_" + ProfileNotificationsActivity.this.dialog_id)) {
                        color = preferences2.getInt("color_" + ProfileNotificationsActivity.this.dialog_id, -16776961);
                    } else if (((int) ProfileNotificationsActivity.this.dialog_id) < 0) {
                        color = preferences2.getInt("GroupLed", -16776961);
                    } else {
                        color = preferences2.getInt("MessagesLed", -16776961);
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
                    textCell3.setTextAndColor(LocaleController.getString("NotificationsLedColor", R.string.NotificationsLedColor), color, false);
                    return;
                case 4:
                    RadioCell radioCell = (RadioCell) viewHolder.itemView;
                    SharedPreferences preferences3 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    int popup = preferences3.getInt("popup_" + ProfileNotificationsActivity.this.dialog_id, 0);
                    if (popup == 0) {
                        if (preferences3.getInt(((int) ProfileNotificationsActivity.this.dialog_id) < 0 ? "popupGroup" : "popupAll", 0) != 0) {
                            popup = 1;
                        } else {
                            popup = 2;
                        }
                    }
                    if (i == ProfileNotificationsActivity.this.popupEnabledRow) {
                        String string12 = LocaleController.getString("PopupEnabled", R.string.PopupEnabled);
                        if (popup == 1) {
                            z2 = true;
                        }
                        radioCell.setText(string12, z2, true);
                        radioCell.setTag(1);
                        return;
                    } else if (i == ProfileNotificationsActivity.this.popupDisabledRow) {
                        String string13 = LocaleController.getString("PopupDisabled", R.string.PopupDisabled);
                        if (popup != 2) {
                            z3 = false;
                        }
                        radioCell.setText(string13, z3, false);
                        radioCell.setTag(2);
                        return;
                    } else {
                        return;
                    }
                case 5:
                    TextCheckBoxCell cell = (TextCheckBoxCell) viewHolder.itemView;
                    SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    String string14 = LocaleController.getString("NotificationsEnableCustom", R.string.NotificationsEnableCustom);
                    if (!ProfileNotificationsActivity.this.customEnabled || !ProfileNotificationsActivity.this.notificationsEnabled) {
                        z3 = false;
                    }
                    cell.setTextAndCheck(string14, z3, false);
                    return;
                case 6:
                    UserCell2 userCell2 = (UserCell2) viewHolder.itemView;
                    int lower_id = (int) ProfileNotificationsActivity.this.dialog_id;
                    if (lower_id > 0) {
                        object = MessagesController.getInstance(ProfileNotificationsActivity.this.currentAccount).getUser(Integer.valueOf(lower_id));
                    } else {
                        object = MessagesController.getInstance(ProfileNotificationsActivity.this.currentAccount).getChat(Integer.valueOf(-lower_id));
                    }
                    userCell2.setData(object, (CharSequence) null, (CharSequence) null, 0);
                    return;
                case 8:
                    TextCheckCell checkCell = (TextCheckCell) viewHolder.itemView;
                    SharedPreferences preferences4 = MessagesController.getNotificationsSettings(ProfileNotificationsActivity.this.currentAccount);
                    if (i == ProfileNotificationsActivity.this.enableRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("Notifications", R.string.Notifications), ProfileNotificationsActivity.this.notificationsEnabled, true);
                        return;
                    } else if (i == ProfileNotificationsActivity.this.previewRow) {
                        String string15 = LocaleController.getString("MessagePreview", R.string.MessagePreview);
                        checkCell.setTextAndCheck(string15, preferences4.getBoolean("content_preview_" + ProfileNotificationsActivity.this.dialog_id, true), true);
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            if (holder.getItemViewType() != 0) {
                int itemViewType = holder.getItemViewType();
                boolean z = false;
                if (itemViewType == 1) {
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    if (ProfileNotificationsActivity.this.customEnabled && ProfileNotificationsActivity.this.notificationsEnabled) {
                        z = true;
                    }
                    textCell.setEnabled(z, (ArrayList<Animator>) null);
                } else if (itemViewType == 2) {
                    TextInfoPrivacyCell textCell2 = (TextInfoPrivacyCell) holder.itemView;
                    if (ProfileNotificationsActivity.this.customEnabled && ProfileNotificationsActivity.this.notificationsEnabled) {
                        z = true;
                    }
                    textCell2.setEnabled(z, (ArrayList<Animator>) null);
                } else if (itemViewType == 3) {
                    TextColorCell textCell3 = (TextColorCell) holder.itemView;
                    if (ProfileNotificationsActivity.this.customEnabled && ProfileNotificationsActivity.this.notificationsEnabled) {
                        z = true;
                    }
                    textCell3.setEnabled(z, (ArrayList<Animator>) null);
                } else if (itemViewType == 4) {
                    RadioCell radioCell = (RadioCell) holder.itemView;
                    if (ProfileNotificationsActivity.this.customEnabled && ProfileNotificationsActivity.this.notificationsEnabled) {
                        z = true;
                    }
                    radioCell.setEnabled(z, (ArrayList<Animator>) null);
                } else if (itemViewType == 8) {
                    TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                    if (holder.getAdapterPosition() == ProfileNotificationsActivity.this.previewRow) {
                        if (ProfileNotificationsActivity.this.customEnabled && ProfileNotificationsActivity.this.notificationsEnabled) {
                            z = true;
                        }
                        checkCell.setEnabled(z, (ArrayList<Animator>) null);
                        return;
                    }
                    checkCell.setEnabled(true, (ArrayList<Animator>) null);
                }
            }
        }

        public int getItemViewType(int position) {
            if (position == ProfileNotificationsActivity.this.generalRow || position == ProfileNotificationsActivity.this.popupRow || position == ProfileNotificationsActivity.this.ledRow || position == ProfileNotificationsActivity.this.callsRow) {
                return 0;
            }
            if (position == ProfileNotificationsActivity.this.soundRow || position == ProfileNotificationsActivity.this.vibrateRow || position == ProfileNotificationsActivity.this.priorityRow || position == ProfileNotificationsActivity.this.smartRow || position == ProfileNotificationsActivity.this.ringtoneRow || position == ProfileNotificationsActivity.this.callsVibrateRow) {
                return 1;
            }
            if (position == ProfileNotificationsActivity.this.popupInfoRow || position == ProfileNotificationsActivity.this.ledInfoRow || position == ProfileNotificationsActivity.this.priorityInfoRow || position == ProfileNotificationsActivity.this.customInfoRow || position == ProfileNotificationsActivity.this.ringtoneInfoRow) {
                return 2;
            }
            if (position == ProfileNotificationsActivity.this.colorRow) {
                return 3;
            }
            if (position == ProfileNotificationsActivity.this.popupEnabledRow || position == ProfileNotificationsActivity.this.popupDisabledRow) {
                return 4;
            }
            if (position == ProfileNotificationsActivity.this.customRow) {
                return 5;
            }
            if (position == ProfileNotificationsActivity.this.avatarRow) {
                return 6;
            }
            if (position == ProfileNotificationsActivity.this.avatarSectionRow) {
                return 7;
            }
            if (position == ProfileNotificationsActivity.this.enableRow || position == ProfileNotificationsActivity.this.previewRow) {
                return 8;
            }
            return 0;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                ProfileNotificationsActivity.this.lambda$getThemeDescriptions$0$ProfileNotificationsActivity();
            }
        };
        RecyclerListView recyclerListView = this.listView;
        RecyclerListView recyclerListView2 = recyclerListView;
        RecyclerListView recyclerListView3 = this.listView;
        RecyclerListView recyclerListView4 = recyclerListView3;
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = cellDelegate;
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextSettingsCell.class, TextColorCell.class, RadioCell.class, UserCell2.class, TextCheckCell.class, TextCheckBoxCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueHeader), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteValueText), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText4), new ThemeDescription((View) this.listView, 0, new Class[]{TextColorCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{RadioCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) recyclerListView2, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_radioBackground), new ThemeDescription((View) recyclerListView4, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_radioBackgroundChecked), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrack), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrackChecked), new ThemeDescription((View) this.listView, 0, new Class[]{UserCell2.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{UserCell2.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, (String) Theme.key_windowBackgroundWhiteGrayText), new ThemeDescription((View) this.listView, 0, new Class[]{UserCell2.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, (String) Theme.key_windowBackgroundWhiteBlueText), new ThemeDescription(this.listView, 0, new Class[]{UserCell2.class}, (Paint) null, new Drawable[]{Theme.avatar_savedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_text), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundRed), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundOrange), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundViolet), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundGreen), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundCyan), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundBlue), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, Theme.key_avatar_backgroundPink), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckBoxCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkboxSquareUnchecked), new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkboxSquareDisabled), new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkboxSquareBackground), new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkboxSquareCheck)};
    }

    public /* synthetic */ void lambda$getThemeDescriptions$0$ProfileNotificationsActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof UserCell2) {
                    ((UserCell2) child).update(0);
                }
            }
        }
    }
}
