package im.bclpbkiauv.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.MovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.baidu.mapapi.UIMsg;
import com.bjz.comm.net.SPConstant;
import com.king.zxing.util.CodeUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.DispatchQueue;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLoader;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaDataController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SendMessagesHelper;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.messenger.browser.Browser;
import im.bclpbkiauv.phoneformat.PhoneFormat;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.SerializedData;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.PhotoViewer;
import im.bclpbkiauv.ui.SettingsActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenu;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.EmptyCell;
import im.bclpbkiauv.ui.cells.GraySectionCell;
import im.bclpbkiauv.ui.cells.HeaderCell;
import im.bclpbkiauv.ui.cells.SettingsSearchCell;
import im.bclpbkiauv.ui.cells.ShadowSectionCell;
import im.bclpbkiauv.ui.cells.TextCell;
import im.bclpbkiauv.ui.cells.TextDetailCell;
import im.bclpbkiauv.ui.cells.TextInfoPrivacyCell;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.CombinedDrawable;
import im.bclpbkiauv.ui.components.EmptyTextProgressView;
import im.bclpbkiauv.ui.components.ImageUpdater;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RadialProgressView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.components.voip.VoIPHelper;
import im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity;
import im.bclpbkiauv.ui.settings.NoticeAndSoundSettingActivity;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import okhttp3.internal.http.StatusLine;
import org.slf4j.Marker;

@Deprecated
public class SettingsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, ImageUpdater.ImageUpdaterDelegate {
    private static final int edit_name = 1;
    private static final int logout = 2;
    private static final int search_button = 3;
    private TLRPC.FileLocation avatar;
    /* access modifiers changed from: private */
    public AnimatorSet avatarAnimation;
    private TLRPC.FileLocation avatarBig;
    /* access modifiers changed from: private */
    public FrameLayout avatarContainer;
    private AvatarDrawable avatarDrawable;
    /* access modifiers changed from: private */
    public BackupImageView avatarImage;
    private View avatarOverlay;
    /* access modifiers changed from: private */
    public RadialProgressView avatarProgressView;
    /* access modifiers changed from: private */
    public int bioRow;
    /* access modifiers changed from: private */
    public int chatRow;
    /* access modifiers changed from: private */
    public int dataRow;
    /* access modifiers changed from: private */
    public EmptyTextProgressView emptyView;
    /* access modifiers changed from: private */
    public int extraHeight;
    /* access modifiers changed from: private */
    public View extraHeightView;
    /* access modifiers changed from: private */
    public int helpRow;
    private ImageUpdater imageUpdater;
    /* access modifiers changed from: private */
    public int languageRow;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public TextView nameTextView;
    /* access modifiers changed from: private */
    public int notificationRow;
    /* access modifiers changed from: private */
    public int numberRow;
    /* access modifiers changed from: private */
    public int numberSectionRow;
    /* access modifiers changed from: private */
    public TextView onlineTextView;
    /* access modifiers changed from: private */
    public ActionBarMenuItem otherItem;
    /* access modifiers changed from: private */
    public int overscrollRow;
    /* access modifiers changed from: private */
    public int privacyRow;
    private PhotoViewer.PhotoViewerProvider provider = new PhotoViewer.EmptyPhotoViewerProvider() {
        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index, boolean needPreview) {
            TLRPC.User user;
            if (!(fileLocation == null || (user = MessagesController.getInstance(SettingsActivity.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(SettingsActivity.this.currentAccount).getClientUserId()))) == null || user.photo == null || user.photo.photo_big == null)) {
                TLRPC.FileLocation photoBig = user.photo.photo_big;
                if (photoBig.local_id == fileLocation.local_id && photoBig.volume_id == fileLocation.volume_id && photoBig.dc_id == fileLocation.dc_id) {
                    int[] coords = new int[2];
                    SettingsActivity.this.avatarImage.getLocationInWindow(coords);
                    PhotoViewer.PlaceProviderObject object = new PhotoViewer.PlaceProviderObject();
                    int i = 0;
                    object.viewX = coords[0];
                    int i2 = coords[1];
                    if (Build.VERSION.SDK_INT < 21) {
                        i = AndroidUtilities.statusBarHeight;
                    }
                    object.viewY = i2 - i;
                    object.parentView = SettingsActivity.this.avatarImage;
                    object.imageReceiver = SettingsActivity.this.avatarImage.getImageReceiver();
                    object.dialogId = UserConfig.getInstance(SettingsActivity.this.currentAccount).getClientUserId();
                    object.thumb = object.imageReceiver.getBitmapSafe();
                    object.size = -1;
                    object.radius = SettingsActivity.this.avatarImage.getImageReceiver().getRoundRadius();
                    object.scale = SettingsActivity.this.avatarContainer.getScaleX();
                    return object;
                }
            }
            return null;
        }

        public void willHidePhotoViewer() {
            SettingsActivity.this.avatarImage.getImageReceiver().setVisible(true, true);
        }
    };
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public SearchAdapter searchAdapter;
    /* access modifiers changed from: private */
    public int settingsSectionRow;
    /* access modifiers changed from: private */
    public int settingsSectionRow2;
    private View shadowView;
    /* access modifiers changed from: private */
    public TLRPC.UserFull userInfo;
    /* access modifiers changed from: private */
    public int usernameRow;
    /* access modifiers changed from: private */
    public int versionRow;
    /* access modifiers changed from: private */
    public ImageView writeButton;
    /* access modifiers changed from: private */
    public AnimatorSet writeButtonAnimation;

    public /* synthetic */ void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList, boolean z, int i) {
        ImageUpdater.ImageUpdaterDelegate.CC.$default$didSelectPhotos(this, arrayList, z, i);
    }

    public /* synthetic */ String getInitialSearchString() {
        return ImageUpdater.ImageUpdaterDelegate.CC.$default$getInitialSearchString(this);
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        ImageUpdater imageUpdater2 = new ImageUpdater();
        this.imageUpdater = imageUpdater2;
        imageUpdater2.parentFragment = this;
        this.imageUpdater.delegate = this;
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.userFullInfoDidLoad);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.overscrollRow = 0;
        int i2 = i + 1;
        this.rowCount = i2;
        this.numberSectionRow = i;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.numberRow = i2;
        int i4 = i3 + 1;
        this.rowCount = i4;
        this.usernameRow = i3;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.bioRow = i4;
        int i6 = i5 + 1;
        this.rowCount = i6;
        this.settingsSectionRow = i5;
        int i7 = i6 + 1;
        this.rowCount = i7;
        this.settingsSectionRow2 = i6;
        int i8 = i7 + 1;
        this.rowCount = i8;
        this.notificationRow = i7;
        int i9 = i8 + 1;
        this.rowCount = i9;
        this.privacyRow = i8;
        int i10 = i9 + 1;
        this.rowCount = i10;
        this.dataRow = i9;
        int i11 = i10 + 1;
        this.rowCount = i11;
        this.chatRow = i10;
        int i12 = i11 + 1;
        this.rowCount = i12;
        this.languageRow = i11;
        int i13 = i12 + 1;
        this.rowCount = i13;
        this.helpRow = i12;
        this.rowCount = i13 + 1;
        this.versionRow = i13;
        MediaDataController.getInstance(this.currentAccount).checkFeaturedStickers();
        this.userInfo = MessagesController.getInstance(this.currentAccount).getUserFull(UserConfig.getInstance(this.currentAccount).getClientUserId());
        MessagesController.getInstance(this.currentAccount).loadUserInfo(UserConfig.getInstance(this.currentAccount).getCurrentUser(), true, this.classGuid);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        BackupImageView backupImageView = this.avatarImage;
        if (backupImageView != null) {
            backupImageView.setImageDrawable((Drawable) null);
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.userFullInfoDidLoad);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
        this.imageUpdater.clear();
    }

    public View createView(Context context) {
        int scrollTo;
        Context context2 = context;
        this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue));
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_avatar_actionBarSelectorBlue), false);
        this.actionBar.setItemsColor(Theme.getColor(Theme.key_avatar_actionBarIconBlue), false);
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAddToContainer(false);
        this.extraHeight = 88;
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    SettingsActivity.this.finishFragment();
                } else if (id == 1) {
                    SettingsActivity.this.presentFragment(new ChangeNameActivity());
                } else if (id == 2) {
                    SettingsActivity.this.presentFragment(new LogoutActivity());
                }
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        ActionBarMenuItem searchItem = menu.addItem(3, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                if (SettingsActivity.this.otherItem != null) {
                    SettingsActivity.this.otherItem.setVisibility(8);
                }
                SettingsActivity.this.searchAdapter.loadFaqWebPage();
                SettingsActivity.this.listView.setAdapter(SettingsActivity.this.searchAdapter);
                SettingsActivity.this.listView.setEmptyView(SettingsActivity.this.emptyView);
                SettingsActivity.this.avatarContainer.setVisibility(8);
                SettingsActivity.this.writeButton.setVisibility(8);
                SettingsActivity.this.nameTextView.setVisibility(8);
                SettingsActivity.this.onlineTextView.setVisibility(8);
                SettingsActivity.this.extraHeightView.setVisibility(8);
                SettingsActivity.this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                SettingsActivity.this.fragmentView.setTag(Theme.key_windowBackgroundWhite);
                SettingsActivity.this.needLayout();
            }

            public void onSearchCollapse() {
                if (SettingsActivity.this.otherItem != null) {
                    SettingsActivity.this.otherItem.setVisibility(0);
                }
                SettingsActivity.this.listView.setAdapter(SettingsActivity.this.listAdapter);
                SettingsActivity.this.listView.setEmptyView((View) null);
                SettingsActivity.this.emptyView.setVisibility(8);
                SettingsActivity.this.avatarContainer.setVisibility(0);
                SettingsActivity.this.writeButton.setVisibility(0);
                SettingsActivity.this.nameTextView.setVisibility(0);
                SettingsActivity.this.onlineTextView.setVisibility(0);
                SettingsActivity.this.extraHeightView.setVisibility(0);
                SettingsActivity.this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
                SettingsActivity.this.fragmentView.setTag(Theme.key_windowBackgroundGray);
                SettingsActivity.this.needLayout();
            }

            public void onTextChanged(EditText editText) {
                SettingsActivity.this.searchAdapter.search(editText.getText().toString().toLowerCase());
            }
        });
        searchItem.setContentDescription(LocaleController.getString("SearchInSettings", R.string.SearchInSettings));
        searchItem.setSearchFieldHint(LocaleController.getString("SearchInSettings", R.string.SearchInSettings));
        ActionBarMenuItem addItem = menu.addItem(0, (int) R.drawable.ic_ab_other);
        this.otherItem = addItem;
        addItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
        this.otherItem.addSubItem(1, (int) R.drawable.msg_edit, (CharSequence) LocaleController.getString("EditName", R.string.EditName));
        this.otherItem.addSubItem(2, (int) R.drawable.msg_leave, (CharSequence) LocaleController.getString("LogOut", R.string.LogOut));
        int scrollToPosition = 0;
        Object writeButtonTag = null;
        if (this.listView != null) {
            scrollTo = this.layoutManager.findFirstVisibleItemPosition();
            View topView = this.layoutManager.findViewByPosition(scrollTo);
            if (topView != null) {
                scrollToPosition = topView.getTop();
            } else {
                scrollTo = -1;
            }
            writeButtonTag = this.writeButton.getTag();
        } else {
            scrollTo = -1;
        }
        this.listAdapter = new ListAdapter(context2);
        this.searchAdapter = new SearchAdapter(this, context2);
        this.fragmentView = new FrameLayout(context2);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.fragmentView.setTag(Theme.key_windowBackgroundGray);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        RecyclerListView recyclerListView = new RecyclerListView(context2);
        this.listView = recyclerListView;
        recyclerListView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView2 = this.listView;
        AnonymousClass4 r14 = new LinearLayoutManager(context2, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = r14;
        recyclerListView2.setLayoutManager(r14);
        this.listView.setGlowColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue));
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.listView.setLayoutAnimation((LayoutAnimationController) null);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                SettingsActivity.this.lambda$createView$0$SettingsActivity(view, i);
            }
        });
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
            private int pressCount = 0;

            public boolean onItemClick(View view, int position) {
                String str;
                int i;
                String str2;
                int i2;
                if (SettingsActivity.this.listView.getAdapter() == SettingsActivity.this.searchAdapter) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) SettingsActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setMessage(LocaleController.getString("ClearSearch", R.string.ClearSearch));
                    builder.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton).toUpperCase(), new DialogInterface.OnClickListener() {
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            SettingsActivity.AnonymousClass5.this.lambda$onItemClick$0$SettingsActivity$5(dialogInterface, i);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                    SettingsActivity.this.showDialog(builder.create());
                    return true;
                } else if (position != SettingsActivity.this.versionRow) {
                    return false;
                } else {
                    int i3 = this.pressCount + 1;
                    this.pressCount = i3;
                    if (i3 >= 2 || BuildVars.DEBUG_PRIVATE_VERSION) {
                        AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) SettingsActivity.this.getParentActivity());
                        builder2.setTitle(LocaleController.getString("DebugMenu", R.string.DebugMenu));
                        CharSequence[] items = new CharSequence[11];
                        items[0] = LocaleController.getString("DebugMenuImportContacts", R.string.DebugMenuImportContacts);
                        items[1] = LocaleController.getString("DebugMenuReloadContacts", R.string.DebugMenuReloadContacts);
                        items[2] = LocaleController.getString("DebugMenuResetContacts", R.string.DebugMenuResetContacts);
                        items[3] = LocaleController.getString("DebugMenuResetDialogs", R.string.DebugMenuResetDialogs);
                        if (BuildVars.LOGS_ENABLED) {
                            i = R.string.DebugMenuDisableLogs;
                            str = "DebugMenuDisableLogs";
                        } else {
                            i = R.string.DebugMenuEnableLogs;
                            str = "DebugMenuEnableLogs";
                        }
                        items[4] = LocaleController.getString(str, i);
                        if (SharedConfig.inappCamera) {
                            i2 = R.string.DebugMenuDisableCamera;
                            str2 = "DebugMenuDisableCamera";
                        } else {
                            i2 = R.string.DebugMenuEnableCamera;
                            str2 = "DebugMenuEnableCamera";
                        }
                        items[5] = LocaleController.getString(str2, i2);
                        items[6] = LocaleController.getString("DebugMenuClearMediaCache", R.string.DebugMenuClearMediaCache);
                        items[7] = LocaleController.getString("DebugMenuCallSettings", R.string.DebugMenuCallSettings);
                        items[8] = null;
                        items[9] = BuildVars.DEBUG_PRIVATE_VERSION ? "Check for app updates" : null;
                        items[10] = LocaleController.getString("DebugMenuReadAllDialogs", R.string.DebugMenuReadAllDialogs);
                        builder2.setItems(items, new DialogInterface.OnClickListener() {
                            public final void onClick(DialogInterface dialogInterface, int i) {
                                SettingsActivity.AnonymousClass5.this.lambda$onItemClick$1$SettingsActivity$5(dialogInterface, i);
                            }
                        });
                        builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                        SettingsActivity.this.showDialog(builder2.create());
                    } else {
                        ToastUtils.show((CharSequence) "¯\\_(ツ)_/¯");
                    }
                    return true;
                }
            }

            public /* synthetic */ void lambda$onItemClick$0$SettingsActivity$5(DialogInterface dialogInterface, int i) {
                SettingsActivity.this.searchAdapter.clearRecent();
            }

            public /* synthetic */ void lambda$onItemClick$1$SettingsActivity$5(DialogInterface dialog, int which) {
                if (which == 0) {
                    UserConfig.getInstance(SettingsActivity.this.currentAccount).syncContacts = true;
                    UserConfig.getInstance(SettingsActivity.this.currentAccount).saveConfig(false);
                    ContactsController.getInstance(SettingsActivity.this.currentAccount).forceImportContacts();
                } else if (which == 1) {
                    ContactsController.getInstance(SettingsActivity.this.currentAccount).loadContacts(false, 0);
                } else if (which == 2) {
                    ContactsController.getInstance(SettingsActivity.this.currentAccount).resetImportedContacts();
                } else if (which == 3) {
                    MessagesController.getInstance(SettingsActivity.this.currentAccount).forceResetDialogs();
                } else if (which == 4) {
                    BuildVars.LOGS_ENABLED = true ^ BuildVars.LOGS_ENABLED;
                    ApplicationLoader.applicationContext.getSharedPreferences(SPConstant.SP_SYSTEM_CONFIG, 0).edit().putBoolean("logsEnabled", BuildVars.LOGS_ENABLED).commit();
                } else if (which == 5) {
                    SharedConfig.toggleInappCamera();
                } else if (which == 6) {
                    MessagesStorage.getInstance(SettingsActivity.this.currentAccount).clearSentMedia();
                    SharedConfig.setNoSoundHintShowed(false);
                    MessagesController.getGlobalMainSettings().edit().remove("archivehint").remove("archivehint_l").remove("gifhint").remove("soundHint").commit();
                } else if (which == 7) {
                    VoIPHelper.showCallDebugSettings(SettingsActivity.this.getParentActivity());
                } else if (which == 8) {
                    SharedConfig.toggleRoundCamera16to9();
                } else if (which == 9) {
                    ((LaunchActivity) SettingsActivity.this.getParentActivity()).checkAppUpdate(true);
                } else if (which == 10) {
                    MessagesStorage.getInstance(SettingsActivity.this.currentAccount).readAllDialogs();
                }
            }
        });
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context2);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.showTextView();
        this.emptyView.setTextSize(18);
        this.emptyView.setVisibility(8);
        this.emptyView.setShowAtCenter(true);
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        frameLayout.addView(this.actionBar);
        View view = new View(context2);
        this.extraHeightView = view;
        view.setPivotY(0.0f);
        this.extraHeightView.setBackgroundColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue));
        frameLayout.addView(this.extraHeightView, LayoutHelper.createFrame(-1, 88.0f));
        View view2 = new View(context2);
        this.shadowView = view2;
        view2.setBackgroundResource(R.drawable.header_shadow);
        frameLayout.addView(this.shadowView, LayoutHelper.createFrame(-1, 3.0f));
        FrameLayout frameLayout2 = new FrameLayout(context2);
        this.avatarContainer = frameLayout2;
        frameLayout2.setPivotX(LocaleController.isRTL ? (float) AndroidUtilities.dp(42.0f) : 0.0f);
        this.avatarContainer.setPivotY(0.0f);
        frameLayout.addView(this.avatarContainer, LayoutHelper.createFrame(42.0f, 42.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? 0 : 64), 0.0f, (float) (LocaleController.isRTL ? 112 : 0), 0.0f));
        this.avatarContainer.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                SettingsActivity.this.lambda$createView$1$SettingsActivity(view);
            }
        });
        BackupImageView backupImageView = new BackupImageView(context2);
        this.avatarImage = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(21.0f));
        this.avatarImage.setContentDescription(LocaleController.getString("AccDescrProfilePicture", R.string.AccDescrProfilePicture));
        this.avatarContainer.addView(this.avatarImage, LayoutHelper.createFrame(42, 42.0f));
        final Paint paint = new Paint(1);
        paint.setColor(1426063360);
        AnonymousClass6 r6 = new RadialProgressView(context2) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                if (SettingsActivity.this.avatarImage != null && SettingsActivity.this.avatarImage.getImageReceiver().hasNotThumb()) {
                    paint.setAlpha((int) (SettingsActivity.this.avatarImage.getImageReceiver().getCurrentAlpha() * 85.0f));
                    canvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(21.0f), paint);
                }
                super.onDraw(canvas);
            }
        };
        this.avatarProgressView = r6;
        r6.setSize(AndroidUtilities.dp(26.0f));
        this.avatarProgressView.setProgressColor(-1);
        this.avatarContainer.addView(this.avatarProgressView, LayoutHelper.createFrame(42, 42.0f));
        showAvatarProgress(false, false);
        AnonymousClass7 r62 = new TextView(context2) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                setPivotX(LocaleController.isRTL ? (float) getMeasuredWidth() : 0.0f);
            }
        };
        this.nameTextView = r62;
        r62.setTextColor(Theme.getColor(Theme.key_profile_title));
        this.nameTextView.setTextSize(1, 18.0f);
        this.nameTextView.setLines(1);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.nameTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setPivotY(0.0f);
        frameLayout.addView(this.nameTextView, LayoutHelper.createFrame(-2.0f, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 48.0f : 118.0f, 0.0f, LocaleController.isRTL ? 166.0f : 96.0f, 0.0f));
        TextView textView = new TextView(context2);
        this.onlineTextView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_profile_status));
        this.onlineTextView.setTextSize(1, 14.0f);
        this.onlineTextView.setLines(1);
        this.onlineTextView.setMaxLines(1);
        this.onlineTextView.setSingleLine(true);
        this.onlineTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.onlineTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        frameLayout.addView(this.onlineTextView, LayoutHelper.createFrame(-2.0f, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 48.0f : 118.0f, 0.0f, LocaleController.isRTL ? 166.0f : 96.0f, 0.0f));
        this.writeButton = new ImageView(context2);
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_profile_actionBackground), Theme.getColor(Theme.key_profile_actionPressedBackground));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            drawable = combinedDrawable;
        }
        this.writeButton.setBackgroundDrawable(drawable);
        this.writeButton.setImageResource(R.drawable.menu_camera_av);
        this.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_profile_actionIcon), PorterDuff.Mode.MULTIPLY));
        this.writeButton.setScaleType(ImageView.ScaleType.CENTER);
        if (Build.VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            ActionBarMenu actionBarMenu = menu;
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.writeButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            animator.addState(new int[0], ObjectAnimator.ofFloat(this.writeButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.writeButton.setStateListAnimator(animator);
            this.writeButton.setOutlineProvider(new ViewOutlineProvider() {
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        frameLayout.addView(this.writeButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, (LocaleController.isRTL ? 3 : 5) | 48, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 16.0f, 0.0f));
        this.writeButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                SettingsActivity.this.lambda$createView$3$SettingsActivity(view);
            }
        });
        this.writeButton.setContentDescription(LocaleController.getString("AccDescrChangeProfilePicture", R.string.AccDescrChangeProfilePicture));
        if (scrollTo != -1) {
            this.layoutManager.scrollToPositionWithOffset(scrollTo, scrollToPosition);
            if (writeButtonTag != null) {
                this.writeButton.setTag(0);
                this.writeButton.setScaleX(0.2f);
                this.writeButton.setScaleY(0.2f);
                this.writeButton.setAlpha(0.0f);
                this.writeButton.setVisibility(8);
            }
        }
        needLayout();
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1 && SettingsActivity.this.listView.getAdapter() == SettingsActivity.this.searchAdapter) {
                    AndroidUtilities.hideKeyboard(SettingsActivity.this.getParentActivity().getCurrentFocus());
                }
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (SettingsActivity.this.layoutManager.getItemCount() != 0) {
                    int height = 0;
                    int i = 0;
                    View child = recyclerView.getChildAt(0);
                    if (child != null && SettingsActivity.this.avatarContainer.getVisibility() == 0) {
                        if (SettingsActivity.this.layoutManager.findFirstVisibleItemPosition() == 0) {
                            int dp = AndroidUtilities.dp(88.0f);
                            if (child.getTop() < 0) {
                                i = child.getTop();
                            }
                            height = dp + i;
                        }
                        if (SettingsActivity.this.extraHeight != height) {
                            int unused = SettingsActivity.this.extraHeight = height;
                            SettingsActivity.this.needLayout();
                        }
                    }
                }
            }
        });
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$SettingsActivity(View view, int position) {
        if (this.listView.getAdapter() == this.listAdapter) {
            if (position == this.notificationRow) {
                presentFragment(new NoticeAndSoundSettingActivity());
            } else if (position == this.privacyRow) {
                presentFragment(new PrivacySettingsActivity());
            } else if (position == this.dataRow) {
                presentFragment(new DataSettingsActivity());
            } else if (position == this.chatRow) {
                presentFragment(new ThemeActivity(0));
            } else if (position == this.helpRow) {
                showHelpAlert();
            } else if (position == this.languageRow) {
                presentFragment(new LanguageSelectActivity());
            } else if (position == this.usernameRow) {
                presentFragment(new ChangeUsernameActivity());
            } else if (position == this.bioRow) {
                if (this.userInfo != null) {
                    presentFragment(new ChangeBioActivity());
                }
            } else if (position == this.numberRow) {
                presentFragment(new ActionIntroActivity(3));
            }
        } else if (position >= 0) {
            Object object = Integer.valueOf(this.numberRow);
            if (!this.searchAdapter.searchWas) {
                int position2 = position - 1;
                if (position2 >= 0) {
                    if (position2 < this.searchAdapter.recentSearches.size()) {
                        object = this.searchAdapter.recentSearches.get(position2);
                    }
                } else {
                    return;
                }
            } else if (position < this.searchAdapter.searchResults.size()) {
                object = this.searchAdapter.searchResults.get(position);
            } else {
                int position3 = position - (this.searchAdapter.searchResults.size() + 1);
                if (position3 >= 0 && position3 < this.searchAdapter.faqSearchResults.size()) {
                    object = this.searchAdapter.faqSearchResults.get(position3);
                }
            }
            if (object instanceof SearchAdapter.SearchResult) {
                ((SearchAdapter.SearchResult) object).open();
            } else if (object instanceof SearchAdapter.FaqSearchResult) {
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.openArticle, this.searchAdapter.faqWebPage, ((SearchAdapter.FaqSearchResult) object).url);
            }
            if (object != null) {
                this.searchAdapter.addRecent(object);
            }
        }
    }

    public /* synthetic */ void lambda$createView$1$SettingsActivity(View v) {
        TLRPC.User user;
        if (this.avatar == null && (user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()))) != null && user.photo != null && user.photo.photo_big != null) {
            PhotoViewer.getInstance().setParentActivity(getParentActivity());
            if (user.photo.dc_id != 0) {
                user.photo.photo_big.dc_id = user.photo.dc_id;
            }
            PhotoViewer.getInstance().openPhoto(user.photo.photo_big, this.provider);
        }
    }

    public /* synthetic */ void lambda$createView$3$SettingsActivity(View v) {
        TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
        if (user == null) {
            user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        }
        if (user != null) {
            this.imageUpdater.openMenu((user.photo == null || user.photo.photo_big == null || (user.photo instanceof TLRPC.TL_userProfilePhotoEmpty)) ? false : true, new Runnable() {
                public final void run() {
                    SettingsActivity.this.lambda$null$2$SettingsActivity();
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$2$SettingsActivity() {
        MessagesController.getInstance(this.currentAccount).deleteUserPhoto((TLRPC.InputPhoto) null);
    }

    public void didUploadPhoto(TLRPC.InputFile file, TLRPC.PhotoSize bigSize, TLRPC.PhotoSize smallSize) {
        AndroidUtilities.runOnUIThread(new Runnable(file, smallSize, bigSize) {
            private final /* synthetic */ TLRPC.InputFile f$1;
            private final /* synthetic */ TLRPC.PhotoSize f$2;
            private final /* synthetic */ TLRPC.PhotoSize f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                SettingsActivity.this.lambda$didUploadPhoto$6$SettingsActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$didUploadPhoto$6$SettingsActivity(TLRPC.InputFile file, TLRPC.PhotoSize smallSize, TLRPC.PhotoSize bigSize) {
        if (file != null) {
            TLRPC.TL_photos_uploadProfilePhoto req = new TLRPC.TL_photos_uploadProfilePhoto();
            req.file = file;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SettingsActivity.this.lambda$null$5$SettingsActivity(tLObject, tL_error);
                }
            });
            return;
        }
        this.avatar = smallSize.location;
        this.avatarBig = bigSize.location;
        this.avatarImage.setImage(ImageLocation.getForLocal(this.avatar), "50_50", (Drawable) this.avatarDrawable, (Object) null);
        showAvatarProgress(true, false);
    }

    public /* synthetic */ void lambda$null$5$SettingsActivity(TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
            if (user == null) {
                user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
                if (user != null) {
                    MessagesController.getInstance(this.currentAccount).putUser(user, false);
                } else {
                    return;
                }
            } else {
                UserConfig.getInstance(this.currentAccount).setCurrentUser(user);
            }
            TLRPC.TL_photos_photo photo = (TLRPC.TL_photos_photo) response;
            ArrayList<TLRPC.PhotoSize> sizes = photo.photo.sizes;
            TLRPC.PhotoSize small = FileLoader.getClosestPhotoSizeWithSize(sizes, 150);
            TLRPC.PhotoSize big = FileLoader.getClosestPhotoSizeWithSize(sizes, CodeUtils.DEFAULT_REQ_HEIGHT);
            user.photo = new TLRPC.TL_userProfilePhoto();
            user.photo.photo_id = photo.photo.id;
            if (small != null) {
                user.photo.photo_small = small.location;
            }
            if (big != null) {
                user.photo.photo_big = big.location;
            } else if (small != null) {
                user.photo.photo_small = small.location;
            }
            if (photo != null) {
                if (!(small == null || this.avatar == null)) {
                    FileLoader.getPathToAttach(this.avatar, true).renameTo(FileLoader.getPathToAttach(small, true));
                    ImageLoader.getInstance().replaceImageInCache(this.avatar.volume_id + "_" + this.avatar.local_id + "@50_50", small.location.volume_id + "_" + small.location.local_id + "@50_50", ImageLocation.getForUser(user, false), true);
                }
                if (!(big == null || this.avatarBig == null)) {
                    FileLoader.getPathToAttach(this.avatarBig, true).renameTo(FileLoader.getPathToAttach(big, true));
                }
            }
            MessagesStorage.getInstance(this.currentAccount).clearUserPhotos(user.id);
            ArrayList<TLRPC.User> users = new ArrayList<>();
            users.add(user);
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(users, (ArrayList<TLRPC.Chat>) null, false, true);
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                SettingsActivity.this.lambda$null$4$SettingsActivity();
            }
        });
    }

    public /* synthetic */ void lambda$null$4$SettingsActivity() {
        this.avatar = null;
        this.avatarBig = null;
        updateUserData();
        showAvatarProgress(false, true);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_ALL));
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
        UserConfig.getInstance(this.currentAccount).saveConfig(true);
    }

    private void showAvatarProgress(final boolean show, boolean animated) {
        if (this.avatarProgressView != null) {
            AnimatorSet animatorSet = this.avatarAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.avatarAnimation = null;
            }
            if (animated) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.avatarAnimation = animatorSet2;
                if (show) {
                    this.avatarProgressView.setVisibility(0);
                    this.avatarAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{1.0f})});
                } else {
                    animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{0.0f})});
                }
                this.avatarAnimation.setDuration(180);
                this.avatarAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (SettingsActivity.this.avatarAnimation != null && SettingsActivity.this.avatarProgressView != null) {
                            if (!show) {
                                SettingsActivity.this.avatarProgressView.setVisibility(4);
                            }
                            AnimatorSet unused = SettingsActivity.this.avatarAnimation = null;
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        AnimatorSet unused = SettingsActivity.this.avatarAnimation = null;
                    }
                });
                this.avatarAnimation.start();
            } else if (show) {
                this.avatarProgressView.setAlpha(1.0f);
                this.avatarProgressView.setVisibility(0);
            } else {
                this.avatarProgressView.setAlpha(0.0f);
                this.avatarProgressView.setVisibility(4);
            }
        }
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        this.imageUpdater.onActivityResult(requestCode, resultCode, data);
    }

    public void saveSelfArgs(Bundle args) {
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (imageUpdater2 != null && imageUpdater2.currentPicturePath != null) {
            args.putString("path", this.imageUpdater.currentPicturePath);
        }
    }

    public void restoreSelfArgs(Bundle args) {
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (imageUpdater2 != null) {
            imageUpdater2.currentPicturePath = args.getString("path");
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        RecyclerListView recyclerListView;
        ListAdapter listAdapter2;
        if (id == NotificationCenter.updateInterfaces) {
            int mask = args[0].intValue();
            if ((mask & 2) != 0 || (mask & 1) != 0) {
                updateUserData();
            }
        } else if (id == NotificationCenter.userFullInfoDidLoad) {
            if (args[0].intValue() == UserConfig.getInstance(this.currentAccount).getClientUserId() && (listAdapter2 = this.listAdapter) != null) {
                this.userInfo = args[1];
                listAdapter2.notifyItemChanged(this.bioRow);
            }
        } else if (id == NotificationCenter.emojiDidLoad && (recyclerListView = this.listView) != null) {
            recyclerListView.invalidateViews();
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
        updateUserData();
        fixLayout();
        setParentActivityTitle(LocaleController.getString("Settings", R.string.Settings));
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fixLayout();
    }

    /* access modifiers changed from: private */
    public void needLayout() {
        int currentExtraHeight;
        int i = 0;
        int newTop = (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) recyclerListView.getLayoutParams();
            if (layoutParams.topMargin != newTop) {
                layoutParams.topMargin = newTop;
                this.listView.setLayoutParams(layoutParams);
                this.extraHeightView.setTranslationY((float) newTop);
            }
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.emptyView.getLayoutParams();
            if (layoutParams2.topMargin != newTop) {
                layoutParams2.topMargin = newTop;
                this.emptyView.setLayoutParams(layoutParams2);
            }
        }
        FrameLayout frameLayout = this.avatarContainer;
        if (frameLayout != null) {
            if (frameLayout.getVisibility() == 0) {
                currentExtraHeight = this.extraHeight;
            } else {
                currentExtraHeight = 0;
            }
            float diff = ((float) currentExtraHeight) / ((float) AndroidUtilities.dp(88.0f));
            this.extraHeightView.setScaleY(diff);
            this.shadowView.setTranslationY((float) (newTop + currentExtraHeight));
            this.writeButton.setTranslationY((float) ((((this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight()) + currentExtraHeight) - AndroidUtilities.dp(29.5f)));
            final boolean setVisible = diff > 0.2f;
            if (setVisible != (this.writeButton.getTag() == null)) {
                if (setVisible) {
                    this.writeButton.setTag((Object) null);
                    this.writeButton.setVisibility(0);
                } else {
                    this.writeButton.setTag(0);
                }
                if (this.writeButtonAnimation != null) {
                    AnimatorSet old = this.writeButtonAnimation;
                    this.writeButtonAnimation = null;
                    old.cancel();
                }
                AnimatorSet animatorSet = new AnimatorSet();
                this.writeButtonAnimation = animatorSet;
                if (setVisible) {
                    animatorSet.setInterpolator(new DecelerateInterpolator());
                    this.writeButtonAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.writeButton, "scaleX", new float[]{1.0f}), ObjectAnimator.ofFloat(this.writeButton, "scaleY", new float[]{1.0f}), ObjectAnimator.ofFloat(this.writeButton, "alpha", new float[]{1.0f})});
                } else {
                    animatorSet.setInterpolator(new AccelerateInterpolator());
                    this.writeButtonAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.writeButton, "scaleX", new float[]{0.2f}), ObjectAnimator.ofFloat(this.writeButton, "scaleY", new float[]{0.2f}), ObjectAnimator.ofFloat(this.writeButton, "alpha", new float[]{0.0f})});
                }
                this.writeButtonAnimation.setDuration(150);
                this.writeButtonAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (SettingsActivity.this.writeButtonAnimation != null && SettingsActivity.this.writeButtonAnimation.equals(animation)) {
                            SettingsActivity.this.writeButton.setVisibility(setVisible ? 0 : 8);
                            AnimatorSet unused = SettingsActivity.this.writeButtonAnimation = null;
                        }
                    }
                });
                this.writeButtonAnimation.start();
            }
            this.avatarContainer.setScaleX(((diff * 18.0f) + 42.0f) / 42.0f);
            this.avatarContainer.setScaleY(((18.0f * diff) + 42.0f) / 42.0f);
            this.avatarProgressView.setSize(AndroidUtilities.dp(26.0f / this.avatarContainer.getScaleX()));
            this.avatarProgressView.setStrokeWidth(3.0f / this.avatarContainer.getScaleX());
            if (this.actionBar.getOccupyStatusBar()) {
                i = AndroidUtilities.statusBarHeight;
            }
            float avatarY = ((((float) i) + ((((float) ActionBar.getCurrentActionBarHeight()) / 2.0f) * (diff + 1.0f))) - (AndroidUtilities.density * 21.0f)) + (AndroidUtilities.density * 27.0f * diff);
            this.avatarContainer.setTranslationY((float) Math.ceil((double) avatarY));
            this.nameTextView.setTranslationY((((float) Math.floor((double) avatarY)) - ((float) Math.ceil((double) AndroidUtilities.density))) + ((float) Math.floor((double) (AndroidUtilities.density * 7.0f * diff))));
            this.onlineTextView.setTranslationY(((float) Math.floor((double) avatarY)) + ((float) AndroidUtilities.dp(22.0f)) + (((float) Math.floor((double) (AndroidUtilities.density * 11.0f))) * diff));
            this.nameTextView.setScaleX((diff * 0.12f) + 1.0f);
            this.nameTextView.setScaleY((0.12f * diff) + 1.0f);
            if (LocaleController.isRTL) {
                this.avatarContainer.setTranslationX(((float) AndroidUtilities.dp(95.0f)) * diff);
                this.nameTextView.setTranslationX(AndroidUtilities.density * 69.0f * diff);
                this.onlineTextView.setTranslationX(AndroidUtilities.density * 69.0f * diff);
                return;
            }
            this.avatarContainer.setTranslationX(((float) (-AndroidUtilities.dp(47.0f))) * diff);
            this.nameTextView.setTranslationX(AndroidUtilities.density * -21.0f * diff);
            this.onlineTextView.setTranslationX(AndroidUtilities.density * -21.0f * diff);
        }
    }

    private void fixLayout() {
        if (this.fragmentView != null) {
            this.fragmentView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    if (SettingsActivity.this.fragmentView == null) {
                        return true;
                    }
                    SettingsActivity.this.needLayout();
                    SettingsActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
        }
    }

    private void updateUserData() {
        TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
        if (user != null) {
            TLRPC.FileLocation photoBig = null;
            if (user.photo != null) {
                photoBig = user.photo.photo_big;
            }
            AvatarDrawable avatarDrawable2 = new AvatarDrawable(user, true);
            this.avatarDrawable = avatarDrawable2;
            avatarDrawable2.setColor(Theme.getColor(Theme.key_avatar_backgroundInProfileBlue));
            BackupImageView backupImageView = this.avatarImage;
            if (backupImageView != null) {
                backupImageView.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) this.avatarDrawable, (Object) user);
                this.avatarImage.getImageReceiver().setVisible(!PhotoViewer.isShowingImage(photoBig), false);
                this.nameTextView.setText(UserObject.getName(user));
                this.onlineTextView.setText(LocaleController.getString("Online", R.string.Online));
                this.avatarImage.getImageReceiver().setVisible(true ^ PhotoViewer.isShowingImage(photoBig), false);
            }
        }
    }

    /* access modifiers changed from: private */
    public void showHelpAlert() {
        String text;
        if (getParentActivity() != null) {
            Context context = getParentActivity();
            BottomSheet.Builder builder = new BottomSheet.Builder(context);
            builder.setApplyTopPadding(false);
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(1);
            HeaderCell headerCell = new HeaderCell(context, true, 23, 15, false);
            headerCell.setHeight(47);
            headerCell.setText(LocaleController.getString("SettingsHelp", R.string.SettingsHelp));
            linearLayout.addView(headerCell);
            LinearLayout linearLayoutInviteContainer = new LinearLayout(context);
            linearLayoutInviteContainer.setOrientation(1);
            linearLayout.addView(linearLayoutInviteContainer, LayoutHelper.createLinear(-1, -2));
            int a = 0;
            while (a < 6) {
                if ((a < 3 || a > 4 || BuildVars.LOGS_ENABLED) && (a != 5 || BuildVars.DEBUG_VERSION)) {
                    TextCell textCell = new TextCell(context);
                    if (a == 0) {
                        text = LocaleController.getString("AskAQuestion", R.string.AskAQuestion);
                    } else if (a == 1) {
                        text = LocaleController.getString("AppFaq", R.string.AppFaq);
                    } else if (a == 2) {
                        text = LocaleController.getString("PrivacyPolicy", R.string.PrivacyPolicy);
                    } else if (a == 3) {
                        text = LocaleController.getString("DebugSendLogs", R.string.DebugSendLogs);
                    } else if (a != 4) {
                        text = "Switch Backend";
                    } else {
                        text = LocaleController.getString("DebugClearLogs", R.string.DebugClearLogs);
                    }
                    textCell.setText(text, BuildVars.LOGS_ENABLED || BuildVars.DEBUG_VERSION ? a != 6 + -1 : a != 2);
                    textCell.setTag(Integer.valueOf(a));
                    textCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    linearLayoutInviteContainer.addView(textCell, LayoutHelper.createLinear(-1, -2));
                    textCell.setOnClickListener(new View.OnClickListener(builder) {
                        private final /* synthetic */ BottomSheet.Builder f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onClick(View view) {
                            SettingsActivity.this.lambda$showHelpAlert$8$SettingsActivity(this.f$1, view);
                        }
                    });
                }
                a++;
            }
            builder.setCustomView(linearLayout);
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$showHelpAlert$8$SettingsActivity(BottomSheet.Builder builder, View v2) {
        int intValue = ((Integer) v2.getTag()).intValue();
        if (intValue == 0) {
            showDialog(AlertsCreator.createSupportAlert(this));
        } else if (intValue == 1) {
            Browser.openUrl((Context) getParentActivity(), LocaleController.getString("AppFaqUrl", R.string.AppFaqUrl));
        } else if (intValue == 2) {
            Browser.openUrl((Context) getParentActivity(), LocaleController.getString("PrivacyPolicyUrl", R.string.PrivacyPolicyUrl));
        } else if (intValue == 3) {
            sendLogs();
        } else if (intValue == 4) {
            FileLog.cleanupLogs();
        } else if (intValue == 5) {
            if (getParentActivity() != null) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder((Context) getParentActivity());
                builder1.setMessage(LocaleController.getString("AreYouSure", R.string.AreYouSure));
                builder1.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder1.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        SettingsActivity.this.lambda$null$7$SettingsActivity(dialogInterface, i);
                    }
                });
                builder1.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                showDialog(builder1.create());
            } else {
                return;
            }
        }
        builder.getDismissRunnable().run();
    }

    public /* synthetic */ void lambda$null$7$SettingsActivity(DialogInterface dialogInterface, int i) {
        SharedConfig.pushAuthKey = null;
        SharedConfig.pushAuthKeyId = null;
        SharedConfig.saveConfig();
        ConnectionsManager.getInstance(this.currentAccount).switchBackend();
    }

    private void sendLogs() {
        if (getParentActivity() != null) {
            AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
            progressDialog.setCanCancel(false);
            progressDialog.show();
            Utilities.globalQueue.postRunnable(new Runnable(progressDialog) {
                private final /* synthetic */ AlertDialog f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SettingsActivity.this.lambda$sendLogs$10$SettingsActivity(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$sendLogs$10$SettingsActivity(AlertDialog progressDialog) {
        try {
            File dir = new File(ApplicationLoader.applicationContext.getExternalFilesDir((String) null).getAbsolutePath() + "/logs");
            File zipFile = new File(dir, "logs.zip");
            if (zipFile.exists()) {
                zipFile.delete();
            }
            File[] files = dir.listFiles();
            boolean[] finished = new boolean[1];
            BufferedInputStream origin = null;
            ZipOutputStream out = null;
            try {
                out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
                byte[] data = new byte[65536];
                for (int i = 0; i < files.length; i++) {
                    BufferedInputStream origin2 = new BufferedInputStream(new FileInputStream(files[i]), data.length);
                    out.putNextEntry(new ZipEntry(files[i].getName()));
                    while (true) {
                        int read = origin2.read(data, 0, data.length);
                        int count = read;
                        if (read == -1) {
                            break;
                        }
                        out.write(data, 0, count);
                    }
                    origin2.close();
                    origin = null;
                }
                finished[0] = true;
                if (origin != null) {
                    origin.close();
                }
            } catch (Exception e) {
                try {
                    e.printStackTrace();
                    if (origin != null) {
                        origin.close();
                    }
                    if (out != null) {
                    }
                } catch (Exception e2) {
                    e = e2;
                    e.printStackTrace();
                } catch (Throwable th) {
                    AlertDialog alertDialog = progressDialog;
                    if (origin != null) {
                        origin.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                    throw th;
                }
            }
            out.close();
            AndroidUtilities.runOnUIThread(new Runnable(progressDialog, finished, zipFile) {
                private final /* synthetic */ AlertDialog f$1;
                private final /* synthetic */ boolean[] f$2;
                private final /* synthetic */ File f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    SettingsActivity.this.lambda$null$9$SettingsActivity(this.f$1, this.f$2, this.f$3);
                }
            });
        } catch (Exception e3) {
            e = e3;
            AlertDialog alertDialog2 = progressDialog;
            e.printStackTrace();
        }
    }

    public /* synthetic */ void lambda$null$9$SettingsActivity(AlertDialog progressDialog, boolean[] finished, File zipFile) {
        Uri uri;
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
        }
        if (finished[0]) {
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(getParentActivity(), "im.bclpbkiauv.messenger.provider", zipFile);
            } else {
                uri = Uri.fromFile(zipFile);
            }
            Intent i = new Intent("android.intent.action.SEND");
            if (Build.VERSION.SDK_INT >= 24) {
                i.addFlags(1);
            }
            i.setType("message/rfc822");
            i.putExtra("android.intent.extra.EMAIL", "");
            i.putExtra("android.intent.extra.SUBJECT", "Logs from " + LocaleController.getInstance().formatterStats.format(System.currentTimeMillis()));
            i.putExtra("android.intent.extra.STREAM", uri);
            getParentActivity().startActivityForResult(Intent.createChooser(i, "Select email application."), 500);
            return;
        }
        ToastUtils.show((int) R.string.ErrorOccurred);
    }

    private class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private ArrayList<FaqSearchResult> faqSearchArray = new ArrayList<>();
        /* access modifiers changed from: private */
        public ArrayList<FaqSearchResult> faqSearchResults = new ArrayList<>();
        /* access modifiers changed from: private */
        public TLRPC.WebPage faqWebPage;
        private String lastSearchString;
        private boolean loadingFaqPage;
        private Context mContext;
        /* access modifiers changed from: private */
        public ArrayList<Object> recentSearches = new ArrayList<>();
        private ArrayList<CharSequence> resultNames = new ArrayList<>();
        private SearchResult[] searchArray;
        /* access modifiers changed from: private */
        public ArrayList<SearchResult> searchResults = new ArrayList<>();
        private Runnable searchRunnable;
        /* access modifiers changed from: private */
        public boolean searchWas;
        final /* synthetic */ SettingsActivity this$0;

        private class SearchResult {
            /* access modifiers changed from: private */
            public int guid;
            /* access modifiers changed from: private */
            public int iconResId;
            /* access modifiers changed from: private */
            public int num;
            private Runnable openRunnable;
            /* access modifiers changed from: private */
            public String[] path;
            private String rowName;
            /* access modifiers changed from: private */
            public String searchTitle;

            public SearchResult(SearchAdapter searchAdapter, int g, String search, int icon, Runnable open) {
                this(g, search, (String) null, (String) null, (String) null, icon, open);
            }

            public SearchResult(SearchAdapter searchAdapter, int g, String search, String pathArg1, int icon, Runnable open) {
                this(g, search, (String) null, pathArg1, (String) null, icon, open);
            }

            public SearchResult(SearchAdapter searchAdapter, int g, String search, String row, String pathArg1, int icon, Runnable open) {
                this(g, search, row, pathArg1, (String) null, icon, open);
            }

            public SearchResult(int g, String search, String row, String pathArg1, String pathArg2, int icon, Runnable open) {
                this.guid = g;
                this.searchTitle = search;
                this.rowName = row;
                this.openRunnable = open;
                this.iconResId = icon;
                if (pathArg1 != null && pathArg2 != null) {
                    this.path = new String[]{pathArg1, pathArg2};
                } else if (pathArg1 != null) {
                    this.path = new String[]{pathArg1};
                }
            }

            public boolean equals(Object obj) {
                if ((obj instanceof SearchResult) && this.guid == ((SearchResult) obj).guid) {
                    return true;
                }
                return false;
            }

            public String toString() {
                SerializedData data = new SerializedData();
                data.writeInt32(this.num);
                data.writeInt32(1);
                data.writeInt32(this.guid);
                return Utilities.bytesToHex(data.toByteArray());
            }

            /* access modifiers changed from: private */
            public void open() {
                this.openRunnable.run();
                if (this.rowName != null) {
                    BaseFragment openingFragment = SearchAdapter.this.this$0.parentLayout.fragmentsStack.get(SearchAdapter.this.this$0.parentLayout.fragmentsStack.size() - 1);
                    try {
                        Field listViewField = openingFragment.getClass().getDeclaredField("listView");
                        listViewField.setAccessible(true);
                        ((RecyclerListView) listViewField.get(openingFragment)).highlightRow(
                        /*  JADX ERROR: Method code generation error
                            jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0041: INVOKE  
                              (wrap: im.bclpbkiauv.ui.components.RecyclerListView : 0x003f: CHECK_CAST  (r3v2 'listView' im.bclpbkiauv.ui.components.RecyclerListView) = (im.bclpbkiauv.ui.components.RecyclerListView) (wrap: java.lang.Object : 0x003b: INVOKE  (r3v1 java.lang.Object) = 
                              (r1v8 'listViewField' java.lang.reflect.Field)
                              (r0v7 'openingFragment' im.bclpbkiauv.ui.actionbar.BaseFragment)
                             java.lang.reflect.Field.get(java.lang.Object):java.lang.Object type: VIRTUAL))
                              (wrap: im.bclpbkiauv.ui.components.RecyclerListView$IntReturnCallback : 0x0038: CONSTRUCTOR  (r2v1 'callback' im.bclpbkiauv.ui.components.RecyclerListView$IntReturnCallback) = 
                              (r5v0 'this' im.bclpbkiauv.ui.SettingsActivity$SearchAdapter$SearchResult A[THIS])
                              (r0v7 'openingFragment' im.bclpbkiauv.ui.actionbar.BaseFragment)
                             call: im.bclpbkiauv.ui.-$$Lambda$SettingsActivity$SearchAdapter$SearchResult$VlumX532QVIyohQyckAZ1vVkvUg.<init>(im.bclpbkiauv.ui.SettingsActivity$SearchAdapter$SearchResult, im.bclpbkiauv.ui.actionbar.BaseFragment):void type: CONSTRUCTOR)
                             im.bclpbkiauv.ui.components.RecyclerListView.highlightRow(im.bclpbkiauv.ui.components.RecyclerListView$IntReturnCallback):void type: VIRTUAL in method: im.bclpbkiauv.ui.SettingsActivity.SearchAdapter.SearchResult.open():void, dex: classes2.dex
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                            	at jadx.core.codegen.RegionGen.makeTryCatch(RegionGen.java:311)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:68)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                            	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                            	at java.util.ArrayList.forEach(ArrayList.java:1259)
                            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                            	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                            	at java.util.ArrayList.forEach(ArrayList.java:1259)
                            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                            	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                            	at java.util.ArrayList.forEach(ArrayList.java:1259)
                            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                            Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0038: CONSTRUCTOR  (r2v1 'callback' im.bclpbkiauv.ui.components.RecyclerListView$IntReturnCallback) = 
                              (r5v0 'this' im.bclpbkiauv.ui.SettingsActivity$SearchAdapter$SearchResult A[THIS])
                              (r0v7 'openingFragment' im.bclpbkiauv.ui.actionbar.BaseFragment)
                             call: im.bclpbkiauv.ui.-$$Lambda$SettingsActivity$SearchAdapter$SearchResult$VlumX532QVIyohQyckAZ1vVkvUg.<init>(im.bclpbkiauv.ui.SettingsActivity$SearchAdapter$SearchResult, im.bclpbkiauv.ui.actionbar.BaseFragment):void type: CONSTRUCTOR in method: im.bclpbkiauv.ui.SettingsActivity.SearchAdapter.SearchResult.open():void, dex: classes2.dex
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                            	... 69 more
                            Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: im.bclpbkiauv.ui.-$$Lambda$SettingsActivity$SearchAdapter$SearchResult$VlumX532QVIyohQyckAZ1vVkvUg, state: NOT_LOADED
                            	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                            	... 75 more
                            */
                        /*
                            this = this;
                            java.lang.Runnable r0 = r5.openRunnable
                            r0.run()
                            java.lang.String r0 = r5.rowName
                            if (r0 == 0) goto L_0x004a
                            im.bclpbkiauv.ui.SettingsActivity$SearchAdapter r0 = im.bclpbkiauv.ui.SettingsActivity.SearchAdapter.this
                            im.bclpbkiauv.ui.SettingsActivity r0 = r0.this$0
                            im.bclpbkiauv.ui.actionbar.ActionBarLayout r0 = r0.parentLayout
                            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r0 = r0.fragmentsStack
                            im.bclpbkiauv.ui.SettingsActivity$SearchAdapter r1 = im.bclpbkiauv.ui.SettingsActivity.SearchAdapter.this
                            im.bclpbkiauv.ui.SettingsActivity r1 = r1.this$0
                            im.bclpbkiauv.ui.actionbar.ActionBarLayout r1 = r1.parentLayout
                            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r1 = r1.fragmentsStack
                            int r1 = r1.size()
                            r2 = 1
                            int r1 = r1 - r2
                            java.lang.Object r0 = r0.get(r1)
                            im.bclpbkiauv.ui.actionbar.BaseFragment r0 = (im.bclpbkiauv.ui.actionbar.BaseFragment) r0
                            java.lang.Class r1 = r0.getClass()     // Catch:{ all -> 0x0049 }
                            java.lang.String r3 = "listView"
                            java.lang.reflect.Field r1 = r1.getDeclaredField(r3)     // Catch:{ all -> 0x0049 }
                            r1.setAccessible(r2)     // Catch:{ all -> 0x0049 }
                            im.bclpbkiauv.ui.-$$Lambda$SettingsActivity$SearchAdapter$SearchResult$VlumX532QVIyohQyckAZ1vVkvUg r2 = new im.bclpbkiauv.ui.-$$Lambda$SettingsActivity$SearchAdapter$SearchResult$VlumX532QVIyohQyckAZ1vVkvUg     // Catch:{ all -> 0x0049 }
                            r2.<init>(r5, r0)     // Catch:{ all -> 0x0049 }
                            java.lang.Object r3 = r1.get(r0)     // Catch:{ all -> 0x0049 }
                            im.bclpbkiauv.ui.components.RecyclerListView r3 = (im.bclpbkiauv.ui.components.RecyclerListView) r3     // Catch:{ all -> 0x0049 }
                            r3.highlightRow(r2)     // Catch:{ all -> 0x0049 }
                            r4 = 0
                            r1.setAccessible(r4)     // Catch:{ all -> 0x0049 }
                            goto L_0x004a
                        L_0x0049:
                            r1 = move-exception
                        L_0x004a:
                            return
                        */
                        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.SettingsActivity.SearchAdapter.SearchResult.open():void");
                    }

                    public /* synthetic */ int lambda$open$0$SettingsActivity$SearchAdapter$SearchResult(BaseFragment openingFragment) {
                        int position = -1;
                        try {
                            Field rowField = openingFragment.getClass().getDeclaredField(this.rowName);
                            Field linearLayoutField = openingFragment.getClass().getDeclaredField("layoutManager");
                            rowField.setAccessible(true);
                            linearLayoutField.setAccessible(true);
                            position = rowField.getInt(openingFragment);
                            ((LinearLayoutManager) linearLayoutField.get(openingFragment)).scrollToPositionWithOffset(position, 0);
                            rowField.setAccessible(false);
                            linearLayoutField.setAccessible(false);
                            return position;
                        } catch (Throwable th) {
                            return position;
                        }
                    }
                }

                private class FaqSearchResult {
                    /* access modifiers changed from: private */
                    public int num;
                    /* access modifiers changed from: private */
                    public String[] path;
                    /* access modifiers changed from: private */
                    public String title;
                    /* access modifiers changed from: private */
                    public String url;

                    public FaqSearchResult(String t, String[] p, String u) {
                        this.title = t;
                        this.path = p;
                        this.url = u;
                    }

                    public boolean equals(Object obj) {
                        if (!(obj instanceof FaqSearchResult)) {
                            return false;
                        }
                        return this.title.equals(((FaqSearchResult) obj).title);
                    }

                    public String toString() {
                        SerializedData data = new SerializedData();
                        data.writeInt32(this.num);
                        int i = 0;
                        data.writeInt32(0);
                        data.writeString(this.title);
                        String[] strArr = this.path;
                        if (strArr != null) {
                            i = strArr.length;
                        }
                        data.writeInt32(i);
                        if (this.path != null) {
                            int a = 0;
                            while (true) {
                                String[] strArr2 = this.path;
                                if (a >= strArr2.length) {
                                    break;
                                }
                                data.writeString(strArr2[a]);
                                a++;
                            }
                        }
                        data.writeString(this.url);
                        return Utilities.bytesToHex(data.toByteArray());
                    }
                }

                public /* synthetic */ void lambda$new$0$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new ChangeNameActivity());
                }

                public /* synthetic */ void lambda$new$1$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new ActionIntroActivity(3));
                }

                public /* synthetic */ void lambda$new$2$SettingsActivity$SearchAdapter() {
                    int freeAccount = -1;
                    int a = 0;
                    while (true) {
                        if (a >= 3) {
                            break;
                        } else if (!UserConfig.getInstance(a).isClientActivated()) {
                            freeAccount = a;
                            break;
                        } else {
                            a++;
                        }
                    }
                    if (freeAccount >= 0) {
                        this.this$0.presentFragment(new LoginActivity(freeAccount));
                    }
                }

                public /* synthetic */ void lambda$new$3$SettingsActivity$SearchAdapter() {
                    if (this.this$0.userInfo != null) {
                        this.this$0.presentFragment(new ChangeBioActivity());
                    }
                }

                public /* synthetic */ void lambda$new$4$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new NotificationsSettingsActivity());
                }

                public /* synthetic */ void lambda$new$5$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new NotificationsCustomSettingsActivity(1, new ArrayList(), true));
                }

                public /* synthetic */ void lambda$new$6$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new NotificationsCustomSettingsActivity(0, new ArrayList(), true));
                }

                public /* synthetic */ void lambda$new$7$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new NotificationsCustomSettingsActivity(2, new ArrayList(), true));
                }

                public /* synthetic */ void lambda$new$8$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new NotificationsSettingsActivity());
                }

                public /* synthetic */ void lambda$new$9$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new NotificationsSettingsActivity());
                }

                public /* synthetic */ void lambda$new$10$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new NotificationsSettingsActivity());
                }

                public /* synthetic */ void lambda$new$11$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new NotificationsSettingsActivity());
                }

                public /* synthetic */ void lambda$new$12$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new NotificationsSettingsActivity());
                }

                public /* synthetic */ void lambda$new$13$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new NotificationsSettingsActivity());
                }

                public /* synthetic */ void lambda$new$14$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new PrivacySettingsActivity());
                }

                public /* synthetic */ void lambda$new$15$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new PrivacyUsersActivity());
                }

                public /* synthetic */ void lambda$new$16$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new PrivacyControlActivity(6, true));
                }

                public /* synthetic */ void lambda$new$17$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new PrivacyControlActivity(0, true));
                }

                public /* synthetic */ void lambda$new$18$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new PrivacyControlActivity(4, true));
                }

                public /* synthetic */ void lambda$new$19$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new PrivacyControlActivity(5, true));
                }

                public /* synthetic */ void lambda$new$20$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new PrivacyControlActivity(3, true));
                }

                public /* synthetic */ void lambda$new$21$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new PrivacyControlActivity(2, true));
                }

                public /* synthetic */ void lambda$new$22$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new PrivacyControlActivity(1, true));
                }

                public /* synthetic */ void lambda$new$23$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new PasscodeActivity(SharedConfig.passcodeHash.length() > 0 ? 2 : 0));
                }

                public /* synthetic */ void lambda$new$24$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new TwoStepVerificationActivity(0));
                }

                public /* synthetic */ void lambda$new$25$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new SessionsActivity(0));
                }

                public /* synthetic */ void lambda$new$26$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new PrivacySettingsActivity());
                }

                public /* synthetic */ void lambda$new$27$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new PrivacySettingsActivity());
                }

                public /* synthetic */ void lambda$new$28$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new PrivacySettingsActivity());
                }

                public /* synthetic */ void lambda$new$29$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new SessionsActivity(1));
                }

                public /* synthetic */ void lambda$new$30$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new PrivacySettingsActivity());
                }

                public /* synthetic */ void lambda$new$31$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new PrivacySettingsActivity());
                }

                public /* synthetic */ void lambda$new$32$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new PrivacySettingsActivity());
                }

                public /* synthetic */ void lambda$new$33$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new PrivacySettingsActivity());
                }

                public /* synthetic */ void lambda$new$34$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new PrivacySettingsActivity());
                }

                public /* synthetic */ void lambda$new$35$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new DataSettingsActivity());
                }

                public /* synthetic */ void lambda$new$36$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new DataSettingsActivity());
                }

                public /* synthetic */ void lambda$new$37$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new CacheControlActivity());
                }

                public /* synthetic */ void lambda$new$38$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new CacheControlActivity());
                }

                public /* synthetic */ void lambda$new$39$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new CacheControlActivity());
                }

                public /* synthetic */ void lambda$new$40$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new CacheControlActivity());
                }

                public /* synthetic */ void lambda$new$41$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new DataUsageActivity());
                }

                public /* synthetic */ void lambda$new$42$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new DataSettingsActivity());
                }

                public /* synthetic */ void lambda$new$43$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new DataAutoDownloadActivity(0));
                }

                public /* synthetic */ void lambda$new$44$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new DataAutoDownloadActivity(1));
                }

                public /* synthetic */ void lambda$new$45$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new DataAutoDownloadActivity(2));
                }

                public /* synthetic */ void lambda$new$46$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new DataSettingsActivity());
                }

                public /* synthetic */ void lambda$new$47$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new DataSettingsActivity());
                }

                public /* synthetic */ void lambda$new$48$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new DataSettingsActivity());
                }

                public /* synthetic */ void lambda$new$49$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new DataSettingsActivity());
                }

                public /* synthetic */ void lambda$new$50$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new DataSettingsActivity());
                }

                public /* synthetic */ void lambda$new$51$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new DataSettingsActivity());
                }

                public /* synthetic */ void lambda$new$52$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new DataSettingsActivity());
                }

                public /* synthetic */ void lambda$new$53$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new DataSettingsActivity());
                }

                public /* synthetic */ void lambda$new$54$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new DataSettingsActivity());
                }

                public /* synthetic */ void lambda$new$55$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new ProxyListActivity());
                }

                public /* synthetic */ void lambda$new$56$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new ProxyListActivity());
                }

                public /* synthetic */ void lambda$new$57$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new ThemeActivity(0));
                }

                public /* synthetic */ void lambda$new$58$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new ThemeActivity(0));
                }

                public /* synthetic */ void lambda$new$59$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new WallpapersListActivity(0));
                }

                public /* synthetic */ void lambda$new$60$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new WallpapersListActivity(1));
                }

                public /* synthetic */ void lambda$new$61$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new WallpapersListActivity(0));
                }

                public /* synthetic */ void lambda$new$62$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new ThemeActivity(1));
                }

                public /* synthetic */ void lambda$new$63$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new ThemeActivity(0));
                }

                public /* synthetic */ void lambda$new$64$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new ThemeActivity(0));
                }

                public /* synthetic */ void lambda$new$65$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new ThemeActivity(0));
                }

                public /* synthetic */ void lambda$new$66$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new ThemeActivity(0));
                }

                public /* synthetic */ void lambda$new$67$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new ThemeActivity(0));
                }

                public /* synthetic */ void lambda$new$68$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new ThemeActivity(0));
                }

                public /* synthetic */ void lambda$new$69$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new ThemeActivity(0));
                }

                public /* synthetic */ void lambda$new$70$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new ThemeActivity(0));
                }

                public /* synthetic */ void lambda$new$71$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new StickersActivity(0));
                }

                public /* synthetic */ void lambda$new$72$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new StickersActivity(0));
                }

                public /* synthetic */ void lambda$new$73$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new FeaturedStickersActivity());
                }

                public /* synthetic */ void lambda$new$74$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new StickersActivity(1));
                }

                public /* synthetic */ void lambda$new$75$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new ArchivedStickersActivity(0));
                }

                public /* synthetic */ void lambda$new$76$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new ArchivedStickersActivity(1));
                }

                public /* synthetic */ void lambda$new$77$SettingsActivity$SearchAdapter() {
                    this.this$0.presentFragment(new LanguageSelectActivity());
                }

                public /* synthetic */ void lambda$new$79$SettingsActivity$SearchAdapter() {
                    SettingsActivity settingsActivity = this.this$0;
                    settingsActivity.showDialog(AlertsCreator.createSupportAlert(settingsActivity));
                }

                public /* synthetic */ void lambda$new$80$SettingsActivity$SearchAdapter() {
                    Browser.openUrl((Context) this.this$0.getParentActivity(), LocaleController.getString("AppFaqUrl", R.string.AppFaqUrl));
                }

                public /* synthetic */ void lambda$new$81$SettingsActivity$SearchAdapter() {
                    Browser.openUrl((Context) this.this$0.getParentActivity(), LocaleController.getString("PrivacyPolicyUrl", R.string.PrivacyPolicyUrl));
                }

                public SearchAdapter(SettingsActivity settingsActivity, Context context) {
                    this.this$0 = settingsActivity;
                    String str = "StorageUsage";
                    String str2 = "DataSettings";
                    this.searchArray = new SearchResult[]{new SearchResult(this, 500, LocaleController.getString("EditName", R.string.EditName), 0, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$0$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, UIMsg.d_ResultType.VERSION_CHECK, LocaleController.getString("ChangePhoneNumber", R.string.ChangePhoneNumber), 0, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$1$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, UIMsg.d_ResultType.NEWVERSION_DOWNLOAD, LocaleController.getString("AddAnotherAccount", R.string.AddAnotherAccount), 0, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$2$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, UIMsg.d_ResultType.CELLID_LOCATE_REQ, LocaleController.getString("UserBio", R.string.UserBio), 0, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$3$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 1, LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.menu_notifications, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$4$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 2, LocaleController.getString("NotificationsPrivateChats", R.string.NotificationsPrivateChats), LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.menu_notifications, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$5$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 3, LocaleController.getString("NotificationsGroups", R.string.NotificationsGroups), LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.menu_notifications, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$6$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 4, LocaleController.getString("NotificationsChannels", R.string.NotificationsChannels), LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.menu_notifications, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$7$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 5, LocaleController.getString("VoipNotificationSettings", R.string.VoipNotificationSettings), "callsSectionRow", LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.menu_notifications, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$8$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 6, LocaleController.getString("BadgeNumber", R.string.BadgeNumber), "badgeNumberSection", LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.menu_notifications, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$9$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 7, LocaleController.getString("InAppNotifications", R.string.InAppNotifications), "inappSectionRow", LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.menu_notifications, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$10$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 8, LocaleController.getString("ContactJoined", R.string.ContactJoined), "contactJoinedRow", LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.menu_notifications, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$11$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 9, LocaleController.getString("PinnedMessages", R.string.PinnedMessages), "pinnedMessageRow", LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.menu_notifications, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$12$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 10, LocaleController.getString("ResetAllNotifications", R.string.ResetAllNotifications), "resetNotificationsRow", LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.menu_notifications, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$13$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 100, LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.menu_secret, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$14$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 101, LocaleController.getString("BlockedUsers", R.string.BlockedUsers), LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.menu_secret, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$15$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 105, LocaleController.getString("PrivacyPhone", R.string.PrivacyPhone), LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.menu_secret, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$16$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 102, LocaleController.getString("PrivacyLastSeen", R.string.PrivacyLastSeen), LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.menu_secret, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$17$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 103, LocaleController.getString("PrivacyProfilePhoto", R.string.PrivacyProfilePhoto), LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.menu_secret, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$18$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 104, LocaleController.getString("PrivacyForwards", R.string.PrivacyForwards), LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.menu_secret, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$19$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 105, LocaleController.getString("PrivacyP2P", R.string.PrivacyP2P), LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.menu_secret, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$20$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 106, LocaleController.getString("Calls", R.string.Calls), LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.menu_secret, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$21$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 107, LocaleController.getString("GroupsAndChannels", R.string.GroupsAndChannels), LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.menu_secret, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$22$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 108, LocaleController.getString("Passcode", R.string.Passcode), LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.menu_secret, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$23$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 109, LocaleController.getString("TwoStepVerification", R.string.TwoStepVerification), LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.menu_secret, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$24$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 110, LocaleController.getString("SessionsTitle", R.string.SessionsTitle), LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.menu_secret, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$25$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 111, LocaleController.getString("PrivacyDeleteCloudDrafts", R.string.PrivacyDeleteCloudDrafts), "clearDraftsRow", LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.menu_secret, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$26$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 112, LocaleController.getString("DeleteAccountIfAwayFor2", R.string.DeleteAccountIfAwayFor2), "deleteAccountRow", LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.menu_secret, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$27$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 113, LocaleController.getString("PrivacyPaymentsClear", R.string.PrivacyPaymentsClear), "paymentsClearRow", LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.menu_secret, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$28$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 114, LocaleController.getString("WebSessionsTitle", R.string.WebSessionsTitle), LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.menu_secret, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$29$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 115, LocaleController.getString("SyncContactsDelete", R.string.SyncContactsDelete), "contactsDeleteRow", LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.menu_secret, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$30$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 116, LocaleController.getString("SyncContacts", R.string.SyncContacts), "contactsSyncRow", LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.menu_secret, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$31$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 117, LocaleController.getString("SuggestContacts", R.string.SuggestContacts), "contactsSuggestRow", LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.menu_secret, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$32$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 118, LocaleController.getString("MapPreviewProvider", R.string.MapPreviewProvider), "secretMapRow", LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.menu_secret, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$33$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 119, LocaleController.getString("SecretWebPage", R.string.SecretWebPage), "secretWebpageRow", LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.menu_secret, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$34$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION, LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.menu_data, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$35$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 201, LocaleController.getString("DataUsage", R.string.DataUsage), "usageSectionRow", LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.menu_data, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$36$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 202, LocaleController.getString("StorageUsage", R.string.StorageUsage), LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.menu_data, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$37$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(203, LocaleController.getString("KeepMedia", R.string.KeepMedia), "keepMediaRow", LocaleController.getString("DataSettings", R.string.DataSettings), LocaleController.getString(str, R.string.StorageUsage), R.drawable.menu_data, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$38$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(204, LocaleController.getString("ClearMediaCache", R.string.ClearMediaCache), "cacheRow", LocaleController.getString(str2, R.string.DataSettings), LocaleController.getString(str, R.string.StorageUsage), R.drawable.menu_data, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$39$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(205, LocaleController.getString("LocalDatabase", R.string.LocalDatabase), "databaseRow", LocaleController.getString(str2, R.string.DataSettings), LocaleController.getString(str, R.string.StorageUsage), R.drawable.menu_data, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$40$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 206, LocaleController.getString("NetworkUsage", R.string.NetworkUsage), LocaleController.getString(str2, R.string.DataSettings), R.drawable.menu_data, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$41$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, MessageObject.TYPE_LIVE, LocaleController.getString("AutomaticMediaDownload", R.string.AutomaticMediaDownload), "mediaDownloadSectionRow", LocaleController.getString(str2, R.string.DataSettings), R.drawable.menu_data, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$42$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 208, LocaleController.getString("WhenUsingMobileData", R.string.WhenUsingMobileData), LocaleController.getString(str2, R.string.DataSettings), R.drawable.menu_data, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$43$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 209, LocaleController.getString("WhenConnectedOnWiFi", R.string.WhenConnectedOnWiFi), LocaleController.getString(str2, R.string.DataSettings), R.drawable.menu_data, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$44$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 210, LocaleController.getString("WhenRoaming", R.string.WhenRoaming), LocaleController.getString(str2, R.string.DataSettings), R.drawable.menu_data, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$45$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 211, LocaleController.getString("ResetAutomaticMediaDownload", R.string.ResetAutomaticMediaDownload), "resetDownloadRow", LocaleController.getString(str2, R.string.DataSettings), R.drawable.menu_data, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$46$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 212, LocaleController.getString("AutoplayMedia", R.string.AutoplayMedia), "autoplayHeaderRow", LocaleController.getString(str2, R.string.DataSettings), R.drawable.menu_data, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$47$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 213, LocaleController.getString("AutoplayGIF", R.string.AutoplayGIF), "autoplayGifsRow", LocaleController.getString(str2, R.string.DataSettings), R.drawable.menu_data, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$48$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 214, LocaleController.getString("AutoplayVideo", R.string.AutoplayVideo), "autoplayVideoRow", LocaleController.getString(str2, R.string.DataSettings), R.drawable.menu_data, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$49$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 215, LocaleController.getString("Streaming", R.string.Streaming), "streamSectionRow", LocaleController.getString(str2, R.string.DataSettings), R.drawable.menu_data, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$50$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 216, LocaleController.getString("EnableStreaming", R.string.EnableStreaming), "enableStreamRow", LocaleController.getString(str2, R.string.DataSettings), R.drawable.menu_data, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$51$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 217, LocaleController.getString("Calls", R.string.Calls), "callsSectionRow", LocaleController.getString(str2, R.string.DataSettings), R.drawable.menu_data, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$52$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 218, LocaleController.getString("VoipUseLessData", R.string.VoipUseLessData), "useLessDataForCallsRow", LocaleController.getString(str2, R.string.DataSettings), R.drawable.menu_data, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$53$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 219, LocaleController.getString("VoipQuickReplies", R.string.VoipQuickReplies), "quickRepliesRow", LocaleController.getString(str2, R.string.DataSettings), R.drawable.menu_data, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$54$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 220, LocaleController.getString("ProxySettings", R.string.ProxySettings), LocaleController.getString(str2, R.string.DataSettings), R.drawable.menu_data, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$55$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(221, LocaleController.getString("UseProxyForCalls", R.string.UseProxyForCalls), "callsRow", LocaleController.getString(str2, R.string.DataSettings), LocaleController.getString("ProxySettings", R.string.ProxySettings), R.drawable.menu_data, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$56$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 300, LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.menu_chats, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$57$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 301, LocaleController.getString("TextSizeHeader", R.string.TextSizeHeader), "textSizeHeaderRow", LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.menu_chats, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$58$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 302, LocaleController.getString("ChatBackground", R.string.ChatBackground), LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.menu_chats, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$59$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(303, LocaleController.getString("SetColor", R.string.SetColor), (String) null, LocaleController.getString("ChatSettings", R.string.ChatSettings), LocaleController.getString("ChatBackground", R.string.ChatBackground), R.drawable.menu_chats, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$60$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(304, LocaleController.getString("ResetChatBackgrounds", R.string.ResetChatBackgrounds), "resetRow", LocaleController.getString("ChatSettings", R.string.ChatSettings), LocaleController.getString("ChatBackground", R.string.ChatBackground), R.drawable.menu_chats, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$61$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 305, LocaleController.getString("AutoNightTheme", R.string.AutoNightTheme), LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.menu_chats, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$62$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 306, LocaleController.getString("ColorTheme", R.string.ColorTheme), "themeHeaderRow", LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.menu_chats, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$63$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, StatusLine.HTTP_TEMP_REDIRECT, LocaleController.getString("ChromeCustomTabs", R.string.ChromeCustomTabs), "customTabsRow", LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.menu_chats, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$64$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, StatusLine.HTTP_PERM_REDIRECT, LocaleController.getString("DirectShare", R.string.DirectShare), "directShareRow", LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.menu_chats, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$65$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 309, LocaleController.getString("EnableAnimations", R.string.EnableAnimations), "enableAnimationsRow", LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.menu_chats, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$66$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 310, LocaleController.getString("RaiseToSpeak", R.string.RaiseToSpeak), "raiseToSpeakRow", LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.menu_chats, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$67$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 311, LocaleController.getString("SendByEnter", R.string.SendByEnter), "sendByEnterRow", LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.menu_chats, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$68$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 312, LocaleController.getString("SaveToGallerySettings", R.string.SaveToGallerySettings), "saveToGalleryRow", LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.menu_chats, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$69$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 312, LocaleController.getString("DistanceUnits", R.string.DistanceUnits), "distanceRow", LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.menu_chats, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$70$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 313, LocaleController.getString("StickersAndMasks", R.string.StickersAndMasks), LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.menu_chats, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$71$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(314, LocaleController.getString("SuggestStickers", R.string.SuggestStickers), "suggestRow", LocaleController.getString("ChatSettings", R.string.ChatSettings), LocaleController.getString("StickersAndMasks", R.string.StickersAndMasks), R.drawable.menu_chats, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$72$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(315, LocaleController.getString("FeaturedStickers", R.string.FeaturedStickers), (String) null, LocaleController.getString("ChatSettings", R.string.ChatSettings), LocaleController.getString("StickersAndMasks", R.string.StickersAndMasks), R.drawable.menu_chats, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$73$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(316, LocaleController.getString("Masks", R.string.Masks), (String) null, LocaleController.getString("ChatSettings", R.string.ChatSettings), LocaleController.getString("StickersAndMasks", R.string.StickersAndMasks), R.drawable.menu_chats, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$74$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(317, LocaleController.getString("ArchivedStickers", R.string.ArchivedStickers), (String) null, LocaleController.getString("ChatSettings", R.string.ChatSettings), LocaleController.getString("StickersAndMasks", R.string.StickersAndMasks), R.drawable.menu_chats, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$75$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(317, LocaleController.getString("ArchivedMasks", R.string.ArchivedMasks), (String) null, LocaleController.getString("ChatSettings", R.string.ChatSettings), LocaleController.getString("StickersAndMasks", R.string.StickersAndMasks), R.drawable.menu_chats, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$76$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 400, LocaleController.getString("Language", R.string.Language), R.drawable.menu_language, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$77$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 401, LocaleController.getString("SettingsHelp", R.string.SettingsHelp), R.drawable.menu_help, new Runnable() {
                        public final void run() {
                            SettingsActivity.this.showHelpAlert();
                        }
                    }), new SearchResult(this, 402, LocaleController.getString("AskAQuestion", R.string.AskAQuestion), LocaleController.getString("SettingsHelp", R.string.SettingsHelp), R.drawable.menu_help, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$79$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 403, LocaleController.getString("AppFaq", R.string.AppFaq), LocaleController.getString("SettingsHelp", R.string.SettingsHelp), R.drawable.menu_help, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$80$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, UIMsg.l_ErrorNo.NETWORK_ERROR_404, LocaleController.getString("PrivacyPolicy", R.string.PrivacyPolicy), LocaleController.getString("SettingsHelp", R.string.SettingsHelp), R.drawable.menu_help, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$81$SettingsActivity$SearchAdapter();
                        }
                    })};
                    this.mContext = context;
                    HashMap hashMap = new HashMap();
                    int a = 0;
                    while (true) {
                        SearchResult[] searchResultArr = this.searchArray;
                        if (a >= searchResultArr.length) {
                            break;
                        }
                        hashMap.put(Integer.valueOf(searchResultArr[a].guid), this.searchArray[a]);
                        a++;
                    }
                    Set<String> set = MessagesController.getGlobalMainSettings().getStringSet("settingsSearchRecent2", (Set) null);
                    if (set != null) {
                        for (String value : set) {
                            try {
                                SerializedData data = new SerializedData(Utilities.hexToBytes(value));
                                int num = data.readInt32(false);
                                int type = data.readInt32(false);
                                if (type == 0) {
                                    String title = data.readString(false);
                                    int count = data.readInt32(false);
                                    String[] path = null;
                                    if (count > 0) {
                                        path = new String[count];
                                        for (int a2 = 0; a2 < count; a2++) {
                                            path[a2] = data.readString(false);
                                        }
                                    }
                                    FaqSearchResult result = new FaqSearchResult(title, path, data.readString(false));
                                    int unused = result.num = num;
                                    this.recentSearches.add(result);
                                } else if (type == 1) {
                                    try {
                                        SearchResult result2 = (SearchResult) hashMap.get(Integer.valueOf(data.readInt32(false)));
                                        if (result2 != null) {
                                            int unused2 = result2.num = num;
                                            this.recentSearches.add(result2);
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            } catch (Exception e2) {
                            }
                        }
                    }
                    Collections.sort(this.recentSearches, new Comparator() {
                        public final int compare(Object obj, Object obj2) {
                            return SettingsActivity.SearchAdapter.this.lambda$new$82$SettingsActivity$SearchAdapter(obj, obj2);
                        }
                    });
                }

                public /* synthetic */ int lambda$new$82$SettingsActivity$SearchAdapter(Object o1, Object o2) {
                    int n1 = getNum(o1);
                    int n2 = getNum(o2);
                    if (n1 < n2) {
                        return -1;
                    }
                    if (n1 > n2) {
                        return 1;
                    }
                    return 0;
                }

                /* access modifiers changed from: private */
                public void loadFaqWebPage() {
                    if (this.faqWebPage == null && !this.loadingFaqPage) {
                        this.loadingFaqPage = true;
                        TLRPC.TL_messages_getWebPage req2 = new TLRPC.TL_messages_getWebPage();
                        req2.url = LocaleController.getString("AppFaqUrl", R.string.AppFaqUrl);
                        req2.hash = 0;
                        ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(req2, new RequestDelegate() {
                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                SettingsActivity.SearchAdapter.this.lambda$loadFaqWebPage$83$SettingsActivity$SearchAdapter(tLObject, tL_error);
                            }
                        });
                    }
                }

                public /* synthetic */ void lambda$loadFaqWebPage$83$SettingsActivity$SearchAdapter(TLObject response2, TLRPC.TL_error error2) {
                    String[] path;
                    TLObject tLObject = response2;
                    if (tLObject instanceof TLRPC.WebPage) {
                        TLRPC.WebPage page = (TLRPC.WebPage) tLObject;
                        if (page.cached_page != null) {
                            int a = 0;
                            int N = page.cached_page.blocks.size();
                            while (a < N) {
                                TLRPC.PageBlock block = page.cached_page.blocks.get(a);
                                if (block instanceof TLRPC.TL_pageBlockList) {
                                    String paragraph = null;
                                    if (a != 0) {
                                        TLRPC.PageBlock prevBlock = page.cached_page.blocks.get(a - 1);
                                        if (prevBlock instanceof TLRPC.TL_pageBlockParagraph) {
                                            paragraph = ArticleViewer.getPlainText(((TLRPC.TL_pageBlockParagraph) prevBlock).text).toString();
                                        }
                                    }
                                    TLRPC.TL_pageBlockList list = (TLRPC.TL_pageBlockList) block;
                                    int b = 0;
                                    int N2 = list.items.size();
                                    while (b < N2) {
                                        TLRPC.PageListItem item = list.items.get(b);
                                        if (item instanceof TLRPC.TL_pageListItemText) {
                                            TLRPC.TL_pageListItemText itemText = (TLRPC.TL_pageListItemText) item;
                                            String url = ArticleViewer.getUrl(itemText.text);
                                            String text = ArticleViewer.getPlainText(itemText.text).toString();
                                            if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(text)) {
                                                if (paragraph != null) {
                                                    path = new String[]{LocaleController.getString("SettingsSearchFaq", R.string.SettingsSearchFaq), paragraph};
                                                } else {
                                                    path = new String[]{LocaleController.getString("SettingsSearchFaq", R.string.SettingsSearchFaq)};
                                                }
                                                this.faqSearchArray.add(new FaqSearchResult(text, path, url));
                                            }
                                        }
                                        b++;
                                        TLObject tLObject2 = response2;
                                    }
                                } else if (block instanceof TLRPC.TL_pageBlockAnchor) {
                                    break;
                                }
                                a++;
                                TLObject tLObject3 = response2;
                            }
                            this.faqWebPage = page;
                        }
                    }
                    this.loadingFaqPage = false;
                }

                public int getItemCount() {
                    int i = 0;
                    if (this.searchWas) {
                        int size = this.searchResults.size();
                        if (!this.faqSearchResults.isEmpty()) {
                            i = this.faqSearchResults.size() + 1;
                        }
                        return size + i;
                    } else if (this.recentSearches.isEmpty()) {
                        return 0;
                    } else {
                        return this.recentSearches.size() + 1;
                    }
                }

                public boolean isEnabled(RecyclerView.ViewHolder holder) {
                    return holder.getItemViewType() == 0;
                }

                public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                    int icon;
                    int itemViewType = holder.getItemViewType();
                    boolean z = true;
                    if (itemViewType == 0) {
                        SettingsSearchCell searchCell = (SettingsSearchCell) holder.itemView;
                        boolean z2 = false;
                        if (!this.searchWas) {
                            int position2 = position - 1;
                            Object object = this.recentSearches.get(position2);
                            if (object instanceof SearchResult) {
                                SearchResult result = (SearchResult) object;
                                String access$4500 = result.searchTitle;
                                String[] access$4300 = result.path;
                                if (position2 >= this.recentSearches.size() - 1) {
                                    z = false;
                                }
                                searchCell.setTextAndValue(access$4500, access$4300, false, z);
                            } else if (object instanceof FaqSearchResult) {
                                FaqSearchResult result2 = (FaqSearchResult) object;
                                String access$4600 = result2.title;
                                String[] access$4400 = result2.path;
                                if (position2 < this.recentSearches.size() - 1) {
                                    z2 = true;
                                }
                                searchCell.setTextAndValue(access$4600, access$4400, true, z2);
                            }
                        } else if (position < this.searchResults.size()) {
                            SearchResult result3 = this.searchResults.get(position);
                            SearchResult prevResult = position > 0 ? this.searchResults.get(position - 1) : null;
                            if (prevResult == null || prevResult.iconResId != result3.iconResId) {
                                icon = result3.iconResId;
                            } else {
                                icon = 0;
                            }
                            CharSequence charSequence = this.resultNames.get(position);
                            String[] access$43002 = result3.path;
                            if (position >= this.searchResults.size() - 1) {
                                z = false;
                            }
                            searchCell.setTextAndValueAndIcon(charSequence, access$43002, icon, z);
                        } else {
                            int position3 = position - (this.searchResults.size() + 1);
                            CharSequence charSequence2 = this.resultNames.get(this.searchResults.size() + position3);
                            String[] access$44002 = this.faqSearchResults.get(position3).path;
                            if (position3 < this.searchResults.size() - 1) {
                                z2 = true;
                            }
                            searchCell.setTextAndValue(charSequence2, access$44002, true, z2);
                        }
                    } else if (itemViewType == 1) {
                        ((GraySectionCell) holder.itemView).setText(LocaleController.getString("SettingsFaqSearchTitle", R.string.SettingsFaqSearchTitle));
                    } else if (itemViewType == 2) {
                        ((HeaderCell) holder.itemView).setText(LocaleController.getString("SettingsRecent", R.string.SettingsRecent));
                    }
                }

                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view;
                    if (viewType == 0) {
                        view = new SettingsSearchCell(this.mContext);
                    } else if (viewType != 1) {
                        view = new HeaderCell(this.mContext, 16);
                    } else {
                        view = new GraySectionCell(this.mContext);
                    }
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    return new RecyclerListView.Holder(view);
                }

                public int getItemViewType(int position) {
                    if (this.searchWas) {
                        if (position >= this.searchResults.size() && position == this.searchResults.size()) {
                            return 1;
                        }
                        return 0;
                    } else if (position == 0) {
                        return 2;
                    } else {
                        return 0;
                    }
                }

                public void addRecent(Object object) {
                    int index = this.recentSearches.indexOf(object);
                    if (index >= 0) {
                        this.recentSearches.remove(index);
                    }
                    this.recentSearches.add(0, object);
                    if (!this.searchWas) {
                        notifyDataSetChanged();
                    }
                    if (this.recentSearches.size() > 20) {
                        ArrayList<Object> arrayList = this.recentSearches;
                        arrayList.remove(arrayList.size() - 1);
                    }
                    LinkedHashSet<String> toSave = new LinkedHashSet<>();
                    int N = this.recentSearches.size();
                    for (int a = 0; a < N; a++) {
                        Object o = this.recentSearches.get(a);
                        if (o instanceof SearchResult) {
                            int unused = ((SearchResult) o).num = a;
                        } else if (o instanceof FaqSearchResult) {
                            int unused2 = ((FaqSearchResult) o).num = a;
                        }
                        toSave.add(o.toString());
                    }
                    MessagesController.getGlobalMainSettings().edit().putStringSet("settingsSearchRecent2", toSave).commit();
                }

                public void clearRecent() {
                    this.recentSearches.clear();
                    MessagesController.getGlobalMainSettings().edit().remove("settingsSearchRecent2").commit();
                    notifyDataSetChanged();
                }

                private int getNum(Object o) {
                    if (o instanceof SearchResult) {
                        return ((SearchResult) o).num;
                    }
                    if (o instanceof FaqSearchResult) {
                        return ((FaqSearchResult) o).num;
                    }
                    return 0;
                }

                public void search(String text) {
                    this.lastSearchString = text;
                    if (this.searchRunnable != null) {
                        Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                        this.searchRunnable = null;
                    }
                    if (TextUtils.isEmpty(text)) {
                        this.searchWas = false;
                        this.searchResults.clear();
                        this.faqSearchResults.clear();
                        this.resultNames.clear();
                        this.this$0.emptyView.setTopImage(0);
                        this.this$0.emptyView.setText(LocaleController.getString("SettingsNoRecent", R.string.SettingsNoRecent));
                        notifyDataSetChanged();
                        return;
                    }
                    DispatchQueue dispatchQueue = Utilities.searchQueue;
                    $$Lambda$SettingsActivity$SearchAdapter$ioXxkohVhc9JbtU7jbIaOG_tec0 r1 = new Runnable(text) {
                        private final /* synthetic */ String f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$search$85$SettingsActivity$SearchAdapter(this.f$1);
                        }
                    };
                    this.searchRunnable = r1;
                    dispatchQueue.postRunnable(r1, 300);
                }

                public /* synthetic */ void lambda$search$85$SettingsActivity$SearchAdapter(String text) {
                    String str;
                    int N;
                    String title;
                    String str2;
                    String title2;
                    int index;
                    ArrayList<SearchResult> results = new ArrayList<>();
                    ArrayList<FaqSearchResult> faqResults = new ArrayList<>();
                    ArrayList<CharSequence> names = new ArrayList<>();
                    String str3 = " ";
                    String[] searchArgs = text.split(str3);
                    String[] translitArgs = new String[searchArgs.length];
                    for (int a = 0; a < searchArgs.length; a++) {
                        translitArgs[a] = LocaleController.getInstance().getTranslitString(searchArgs[a]);
                        if (translitArgs[a].equals(searchArgs[a])) {
                            translitArgs[a] = null;
                        }
                    }
                    int a2 = 0;
                    while (true) {
                        SearchResult[] searchResultArr = this.searchArray;
                        if (a2 >= searchResultArr.length) {
                            break;
                        }
                        SearchResult result = searchResultArr[a2];
                        String title3 = str3 + result.searchTitle.toLowerCase();
                        SpannableStringBuilder stringBuilder = null;
                        int i = 0;
                        while (true) {
                            if (i >= searchArgs.length) {
                                break;
                            }
                            if (searchArgs[i].length() != 0) {
                                String searchString = searchArgs[i];
                                int index2 = title3.indexOf(str3 + searchString);
                                if (index2 >= 0 || translitArgs[i] == null) {
                                    index = index2;
                                } else {
                                    searchString = translitArgs[i];
                                    int i2 = index2;
                                    index = title3.indexOf(str3 + searchString);
                                }
                                if (index < 0) {
                                    String str4 = searchString;
                                    break;
                                }
                                if (stringBuilder == null) {
                                    title2 = title3;
                                    stringBuilder = new SpannableStringBuilder(result.searchTitle);
                                } else {
                                    title2 = title3;
                                }
                                String str5 = searchString;
                                stringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), index, searchString.length() + index, 33);
                            } else {
                                title2 = title3;
                            }
                            if (stringBuilder != null && i == searchArgs.length - 1) {
                                if (result.guid == 502) {
                                    int freeAccount = -1;
                                    int b = 0;
                                    while (true) {
                                        if (b >= 3) {
                                            break;
                                        } else if (!UserConfig.getInstance(a2).isClientActivated()) {
                                            freeAccount = b;
                                            break;
                                        } else {
                                            b++;
                                        }
                                    }
                                    if (freeAccount < 0) {
                                    }
                                }
                                results.add(result);
                                names.add(stringBuilder);
                            }
                            i++;
                            String str6 = text;
                            title3 = title2;
                        }
                        a2++;
                        String str7 = text;
                    }
                    if (this.faqWebPage != null) {
                        int a3 = 0;
                        int N2 = this.faqSearchArray.size();
                        while (a3 < N2) {
                            FaqSearchResult result2 = this.faqSearchArray.get(a3);
                            String title4 = str3 + result2.title.toLowerCase();
                            SpannableStringBuilder stringBuilder2 = null;
                            int i3 = 0;
                            while (true) {
                                if (i3 >= searchArgs.length) {
                                    str = str3;
                                    N = N2;
                                    break;
                                }
                                if (searchArgs[i3].length() != 0) {
                                    String searchString2 = searchArgs[i3];
                                    int index3 = title4.indexOf(str3 + searchString2);
                                    if (index3 >= 0 || translitArgs[i3] == null) {
                                        N = N2;
                                    } else {
                                        searchString2 = translitArgs[i3];
                                        N = N2;
                                        index3 = title4.indexOf(str3 + searchString2);
                                    }
                                    if (index3 < 0) {
                                        str = str3;
                                        break;
                                    }
                                    if (stringBuilder2 == null) {
                                        str2 = str3;
                                        stringBuilder2 = new SpannableStringBuilder(result2.title);
                                    } else {
                                        str2 = str3;
                                    }
                                    title = title4;
                                    stringBuilder2.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), index3, searchString2.length() + index3, 33);
                                } else {
                                    str2 = str3;
                                    N = N2;
                                    title = title4;
                                }
                                if (stringBuilder2 != null && i3 == searchArgs.length - 1) {
                                    faqResults.add(result2);
                                    names.add(stringBuilder2);
                                }
                                i3++;
                                N2 = N;
                                str3 = str2;
                                title4 = title;
                            }
                            String str8 = title4;
                            a3++;
                            N2 = N;
                            str3 = str;
                        }
                    }
                    AndroidUtilities.runOnUIThread(new Runnable(text, results, faqResults, names) {
                        private final /* synthetic */ String f$1;
                        private final /* synthetic */ ArrayList f$2;
                        private final /* synthetic */ ArrayList f$3;
                        private final /* synthetic */ ArrayList f$4;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                        }

                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$null$84$SettingsActivity$SearchAdapter(this.f$1, this.f$2, this.f$3, this.f$4);
                        }
                    });
                }

                public /* synthetic */ void lambda$null$84$SettingsActivity$SearchAdapter(String text, ArrayList results, ArrayList faqResults, ArrayList names) {
                    if (text.equals(this.lastSearchString)) {
                        if (!this.searchWas) {
                            this.this$0.emptyView.setTopImage(R.drawable.settings_noresults);
                            this.this$0.emptyView.setText(LocaleController.getString("SettingsNoResults", R.string.SettingsNoResults));
                        }
                        this.searchWas = true;
                        this.searchResults = results;
                        this.faqSearchResults = faqResults;
                        this.resultNames = names;
                        notifyDataSetChanged();
                    }
                }
            }

            private class ListAdapter extends RecyclerListView.SelectionAdapter {
                private Context mContext;

                public ListAdapter(Context context) {
                    this.mContext = context;
                }

                public int getItemCount() {
                    return SettingsActivity.this.rowCount;
                }

                public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                    String value;
                    String value2;
                    int itemViewType = holder.getItemViewType();
                    if (itemViewType != 0) {
                        if (itemViewType == 2) {
                            TextCell textCell = (TextCell) holder.itemView;
                            if (position == SettingsActivity.this.languageRow) {
                                textCell.setTextAndIcon(LocaleController.getString("Language", R.string.Language), R.drawable.menu_language, true);
                            } else if (position == SettingsActivity.this.notificationRow) {
                                textCell.setTextAndIcon(LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.menu_notifications, true);
                            } else if (position == SettingsActivity.this.privacyRow) {
                                textCell.setTextAndIcon(LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.menu_secret, true);
                            } else if (position == SettingsActivity.this.dataRow) {
                                textCell.setTextAndIcon(LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.menu_data, true);
                            } else if (position == SettingsActivity.this.chatRow) {
                                textCell.setTextAndIcon(LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.menu_chats, true);
                            } else if (position == SettingsActivity.this.helpRow) {
                                textCell.setTextAndIcon(LocaleController.getString("SettingsHelp", R.string.SettingsHelp), R.drawable.menu_help, false);
                            }
                        } else if (itemViewType != 4) {
                            if (itemViewType == 6) {
                                TextDetailCell textCell2 = (TextDetailCell) holder.itemView;
                                if (position == SettingsActivity.this.numberRow) {
                                    TLRPC.User user = UserConfig.getInstance(SettingsActivity.this.currentAccount).getCurrentUser();
                                    if (user == null || user.phone == null || user.phone.length() == 0) {
                                        value2 = LocaleController.getString("NumberUnknown", R.string.NumberUnknown);
                                    } else {
                                        value2 = PhoneFormat.getInstance().format(Marker.ANY_NON_NULL_MARKER + user.phone);
                                    }
                                    textCell2.setTextAndValue(value2, LocaleController.getString("TapToChangePhone", R.string.TapToChangePhone), true);
                                } else if (position == SettingsActivity.this.usernameRow) {
                                    TLRPC.User user2 = UserConfig.getInstance(SettingsActivity.this.currentAccount).getCurrentUser();
                                    if (user2 == null || TextUtils.isEmpty(user2.username)) {
                                        value = LocaleController.getString("UsernameEmpty", R.string.UsernameEmpty);
                                    } else {
                                        value = "@" + user2.username;
                                    }
                                    textCell2.setTextAndValue(value, LocaleController.getString("Username", R.string.Username), true);
                                } else if (position != SettingsActivity.this.bioRow) {
                                } else {
                                    if (SettingsActivity.this.userInfo == null || !TextUtils.isEmpty(SettingsActivity.this.userInfo.about)) {
                                        textCell2.setTextWithEmojiAndValue(SettingsActivity.this.userInfo == null ? LocaleController.getString("Loading", R.string.Loading) : SettingsActivity.this.userInfo.about, LocaleController.getString("UserBio", R.string.UserBio), false);
                                    } else {
                                        textCell2.setTextAndValue(LocaleController.getString("UserBio", R.string.UserBio), LocaleController.getString("UserBioDetail", R.string.UserBioDetail), false);
                                    }
                                }
                            }
                        } else if (position == SettingsActivity.this.settingsSectionRow2) {
                            ((HeaderCell) holder.itemView).setText(LocaleController.getString("SETTINGS", R.string.SETTINGS));
                        } else if (position == SettingsActivity.this.numberSectionRow) {
                            ((HeaderCell) holder.itemView).setText(LocaleController.getString("Account", R.string.Account));
                        }
                    } else if (position == SettingsActivity.this.overscrollRow) {
                        ((EmptyCell) holder.itemView).setHeight(AndroidUtilities.dp(88.0f));
                    }
                }

                public boolean isEnabled(RecyclerView.ViewHolder holder) {
                    int position = holder.getAdapterPosition();
                    return position == SettingsActivity.this.notificationRow || position == SettingsActivity.this.numberRow || position == SettingsActivity.this.privacyRow || position == SettingsActivity.this.languageRow || position == SettingsActivity.this.usernameRow || position == SettingsActivity.this.bioRow || position == SettingsActivity.this.versionRow || position == SettingsActivity.this.dataRow || position == SettingsActivity.this.chatRow || position == SettingsActivity.this.helpRow;
                }

                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    int i = viewType;
                    View view = null;
                    if (i == 0) {
                        view = new EmptyCell(this.mContext);
                        view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    } else if (i == 1) {
                        view = new ShadowSectionCell(this.mContext);
                    } else if (i == 2) {
                        view = new TextCell(this.mContext);
                        view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    } else if (i == 4) {
                        view = new HeaderCell(this.mContext, 23);
                        view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    } else if (i == 5) {
                        TextInfoPrivacyCell cell = new TextInfoPrivacyCell(this.mContext, 10);
                        cell.getTextView().setGravity(1);
                        cell.getTextView().setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
                        cell.getTextView().setMovementMethod((MovementMethod) null);
                        cell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        try {
                            PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                            int code = pInfo.versionCode / 10;
                            String abi = "";
                            switch (pInfo.versionCode % 10) {
                                case 0:
                                case 9:
                                    abi = "universal " + Build.CPU_ABI + " " + Build.CPU_ABI2;
                                    break;
                                case 1:
                                case 3:
                                    abi = "arm-v7a";
                                    break;
                                case 2:
                                case 4:
                                    abi = "x86";
                                    break;
                                case 5:
                                case 7:
                                    abi = "arm64-v8a";
                                    break;
                                case 6:
                                case 8:
                                    abi = "x86_64";
                                    break;
                            }
                            cell.setText(LocaleController.formatString("AppVersion", R.string.AppVersion, String.format(Locale.US, "v%s (%d) %s", new Object[]{pInfo.versionName, Integer.valueOf(code), abi})));
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                        cell.getTextView().setPadding(0, AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f));
                        view = cell;
                    } else if (i == 6) {
                        view = new TextDetailCell(this.mContext);
                        view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    }
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    return new RecyclerListView.Holder(view);
                }

                public int getItemViewType(int position) {
                    if (position == SettingsActivity.this.overscrollRow) {
                        return 0;
                    }
                    if (position == SettingsActivity.this.settingsSectionRow) {
                        return 1;
                    }
                    if (position == SettingsActivity.this.notificationRow || position == SettingsActivity.this.privacyRow || position == SettingsActivity.this.languageRow || position == SettingsActivity.this.dataRow || position == SettingsActivity.this.chatRow || position == SettingsActivity.this.helpRow) {
                        return 2;
                    }
                    if (position == SettingsActivity.this.versionRow) {
                        return 5;
                    }
                    if (position == SettingsActivity.this.numberRow || position == SettingsActivity.this.usernameRow || position == SettingsActivity.this.bioRow) {
                        return 6;
                    }
                    if (position == SettingsActivity.this.settingsSectionRow2 || position == SettingsActivity.this.numberSectionRow) {
                        return 4;
                    }
                    return 2;
                }
            }

            public ThemeDescription[] getThemeDescriptions() {
                return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{EmptyCell.class, HeaderCell.class, TextDetailCell.class, TextCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_backgroundActionBarBlue), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_backgroundActionBarBlue), new ThemeDescription(this.extraHeightView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_backgroundActionBarBlue), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_actionBarIconBlue), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_actionBarSelectorBlue), new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_profile_title), new ThemeDescription(this.onlineTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_profile_status), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSubmenuBackground), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSubmenuItem), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSubmenuItemIcon), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteValueText), new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayIcon), new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueHeader), new ThemeDescription((View) this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText3), new ThemeDescription(this.avatarImage, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.avatar_savedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_text), new ThemeDescription(this.avatarImage, 0, (Class[]) null, (Paint) null, new Drawable[]{this.avatarDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_avatar_backgroundInProfileBlue), new ThemeDescription(this.writeButton, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_profile_actionIcon), new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_profile_actionBackground), new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_profile_actionPressedBackground), new ThemeDescription((View) this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_graySectionText), new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_graySection), new ThemeDescription((View) this.listView, 0, new Class[]{SettingsSearchCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{SettingsSearchCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription((View) this.listView, 0, new Class[]{SettingsSearchCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayIcon)};
            }
        }
