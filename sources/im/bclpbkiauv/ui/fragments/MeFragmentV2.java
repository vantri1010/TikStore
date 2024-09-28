package im.bclpbkiauv.ui.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bjz.comm.net.SPConstant;
import com.bjz.comm.net.bean.RespFcUserStatisticsBean;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ScreenUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.SerializedData;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCContacts;
import im.bclpbkiauv.tgnet.TLRPCFriendsHub;
import im.bclpbkiauv.ui.ChatActivity;
import im.bclpbkiauv.ui.MediaActivity;
import im.bclpbkiauv.ui.PhotoViewer;
import im.bclpbkiauv.ui.StickersActivity;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.CombinedDrawable;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.dialogs.McShareDialog;
import im.bclpbkiauv.ui.fragments.MeFragmentV2;
import im.bclpbkiauv.ui.hcells.IndexTextCell2;
import im.bclpbkiauv.ui.hui.cdnvip.CdnVipCenterActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.ui.FcAlbumActivity;
import im.bclpbkiauv.ui.hui.mine.AboutAppActivity;
import im.bclpbkiauv.ui.hui.mine.MryLanguageSelectActivity;
import im.bclpbkiauv.ui.hui.mine.MryThemeActivity;
import im.bclpbkiauv.ui.hui.mine.NewUserInfoActivity;
import im.bclpbkiauv.ui.hui.mine.PrivacyAndSafeActivity;
import im.bclpbkiauv.ui.hui.mine.QrCodeActivity;
import im.bclpbkiauv.ui.hviews.MryTextView;
import im.bclpbkiauv.ui.hviews.sliding.SlidingLayout;
import im.bclpbkiauv.ui.settings.DataAndStoreSettingActivity;
import im.bclpbkiauv.ui.settings.NoticeAndSoundSettingActivity;
import im.bclpbkiauv.ui.wallet.WalletActivity;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.json.JSONObject;

public class MeFragmentV2 extends BaseFmts implements NotificationCenter.NotificationCenterDelegate {
    private String TAG = MeFragmentV2.class.getSimpleName();
    /* access modifiers changed from: private */
    public int aboutRow = -1;
    /* access modifiers changed from: private */
    public int appearanceRow = -1;
    /* access modifiers changed from: private */
    public int avatarEmptyRow = -1;
    /* access modifiers changed from: private */
    public BackupImageView avatarImage;
    /* access modifiers changed from: private */
    public int avatarRow = -1;
    /* access modifiers changed from: private */
    public int cdnVipEmptyRow = -1;
    /* access modifiers changed from: private */
    public int cdnVipRow = -1;
    /* access modifiers changed from: private */
    public int dataEmptyRow = -1;
    /* access modifiers changed from: private */
    public int dataRow = -1;
    /* access modifiers changed from: private */
    public int digitalcurrency = -1;
    /* access modifiers changed from: private */
    public int digitalcurrencyEmptyRow = -1;
    /* access modifiers changed from: private */
    public int faqRow = -1;
    /* access modifiers changed from: private */
    public RespFcUserStatisticsBean fcActionCountBean;
    /* access modifiers changed from: private */
    public int gamesCenterEmptyRow = -1;
    /* access modifiers changed from: private */
    public int inviteFriends = -1;
    private boolean isRequestActionCount = false;
    /* access modifiers changed from: private */
    public int langRow = -1;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public int liveIncomeRow = -1;
    /* access modifiers changed from: private */
    public int newWalletEmptyRow = -1;
    /* access modifiers changed from: private */
    public int newWalletRow = -1;
    /* access modifiers changed from: private */
    public int notifyRow = -1;
    /* access modifiers changed from: private */
    public int pcLoginRow = -1;
    private int pressCount;
    /* access modifiers changed from: private */
    public int privacyRow = -1;
    /* access modifiers changed from: private */
    public PhotoViewer.PhotoViewerProvider provider = new PhotoViewer.EmptyPhotoViewerProvider() {
        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index, boolean needPreview) {
            TLRPC.User user;
            if (!(fileLocation == null || (user = MessagesController.getInstance(MeFragmentV2.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(MeFragmentV2.this.currentAccount).getClientUserId()))) == null || user.photo == null || user.photo.photo_big == null)) {
                TLRPC.FileLocation photoBig = user.photo.photo_big;
                if (photoBig.local_id == fileLocation.local_id && photoBig.volume_id == fileLocation.volume_id && photoBig.dc_id == fileLocation.dc_id) {
                    int[] coords = new int[2];
                    MeFragmentV2.this.avatarImage.getLocationInWindow(coords);
                    PhotoViewer.PlaceProviderObject object = new PhotoViewer.PlaceProviderObject();
                    int i = 0;
                    object.viewX = coords[0];
                    int i2 = coords[1];
                    if (Build.VERSION.SDK_INT < 21) {
                        i = AndroidUtilities.statusBarHeight;
                    }
                    object.viewY = i2 - i;
                    object.parentView = MeFragmentV2.this.avatarImage;
                    object.imageReceiver = MeFragmentV2.this.avatarImage.getImageReceiver();
                    object.dialogId = UserConfig.getInstance(MeFragmentV2.this.currentAccount).getClientUserId();
                    object.thumb = object.imageReceiver.getBitmapSafe();
                    object.size = -1;
                    object.radius = MeFragmentV2.this.avatarImage.getImageReceiver().getRoundRadius();
                    object.scale = MeFragmentV2.this.avatarImage.getScaleX();
                    return object;
                }
            }
            return null;
        }

        public void willHidePhotoViewer() {
            MeFragmentV2.this.avatarImage.getImageReceiver().setVisible(true, true);
        }
    };
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int serviceEmptyRow = -1;
    /* access modifiers changed from: private */
    public int serviceRow = -1;
    /* access modifiers changed from: private */
    public TLRPC.UserFull userFull;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateRows();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.userFullInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.cdnVipBuySuccess);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = getContext();
        SlidingLayout root = new SlidingLayout(context);
        this.fragmentView = root;
        root.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setHasFixedSize(true);
        this.listView.setNestedScrollingEnabled(false);
        this.listView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        root.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        MryTextView tvVersion = new MryTextView(context);
        tvVersion.setTextSize(12.0f);
        tvVersion.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        tvVersion.setText(LocaleController.getString(R.string.AppName) + "Android Client v" + AppUtils.getAppVersionName());
        root.addView(tvVersion, LayoutHelper.createFrame(-2.0f, -2.0f, 81, 0.0f, 0.0f, 0.0f, (float) AndroidUtilities.dp(10.0f)));
        tvVersion.setOnLongClickListener(new View.OnLongClickListener() {
            public final boolean onLongClick(View view) {
                return MeFragmentV2.this.lambda$onCreateView$1$MeFragmentV2(view);
            }
        });
        ListAdapter listAdapter2 = new ListAdapter(context);
        this.listAdapter = listAdapter2;
        this.listView.setAdapter(listAdapter2);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener(context) {
            private final /* synthetic */ Context f$1;

            {
                this.f$1 = r2;
            }

            public final void onItemClick(View view, int i) {
                MeFragmentV2.this.lambda$onCreateView$2$MeFragmentV2(this.f$1, view, i);
            }
        });
        return root;
    }

    public /* synthetic */ boolean lambda$onCreateView$1$MeFragmentV2(View v) {
        String str;
        int i;
        int i2 = this.pressCount + 1;
        this.pressCount = i2;
        if (i2 >= 2) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("DebugMenu", R.string.DebugMenu));
            CharSequence[] items = new CharSequence[3];
            items[0] = LocaleController.getString("DebugSendLogs", R.string.DebugSendLogs);
            items[1] = LocaleController.getString("DebugClearLogs", R.string.DebugClearLogs);
            if (BuildVars.LOGS_ENABLED) {
                i = R.string.DebugMenuDisableLogs;
                str = "DebugMenuDisableLogs";
            } else {
                i = R.string.DebugMenuEnableLogs;
                str = "DebugMenuEnableLogs";
            }
            items[2] = LocaleController.getString(str, i);
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    MeFragmentV2.this.lambda$null$0$MeFragmentV2(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
        return true;
    }

    public /* synthetic */ void lambda$null$0$MeFragmentV2(DialogInterface dialog, int which) {
        if (which == 0) {
            sendLogs();
        } else if (which == 1) {
            FileLog.cleanupLogs();
        } else if (which == 2) {
            BuildVars.LOGS_ENABLED = true ^ BuildVars.LOGS_ENABLED;
            ApplicationLoader.applicationContext.getSharedPreferences(SPConstant.SP_SYSTEM_CONFIG, 0).edit().putBoolean("logsEnabled", BuildVars.LOGS_ENABLED).commit();
        }
    }

    public /* synthetic */ void lambda$onCreateView$2$MeFragmentV2(Context context, View view, int position) {
        if (position != this.avatarRow) {
            if (position == this.newWalletRow) {
                presentFragment(new WalletActivity());
            } else if (position == this.notifyRow) {
                presentFragment(new NoticeAndSoundSettingActivity());
            } else if (position == this.privacyRow) {
                presentFragment(new PrivacyAndSafeActivity());
            } else if (position == this.dataRow) {
                presentFragment(new DataAndStoreSettingActivity());
            } else if (position == this.appearanceRow) {
                presentFragment(new MryThemeActivity(0));
            } else if (position == this.langRow) {
                presentFragment(new MryLanguageSelectActivity());
            } else if (position == this.aboutRow) {
                presentFragment(new AboutAppActivity());
            } else if (position == this.inviteFriends) {
                McShareDialog mcShareDialog = new McShareDialog(context, this);
                mcShareDialog.setUser(getUserConfig().getCurrentUser());
                mcShareDialog.initData();
            } else if (position == this.serviceRow) {
                performService(getCurrentFragment());
            } else if (position != this.digitalcurrency && position != this.liveIncomeRow) {
                if (position == this.cdnVipRow) {
                    presentFragment(new CdnVipCenterActivity());
                } else if (position == this.pcLoginRow) {
                    ToastUtils.show((CharSequence) "Developing...");
                } else if (position == this.faqRow) {
                    ToastUtils.show((CharSequence) "Developing...");
                } else if (position == this.gamesCenterEmptyRow && position == this.dataEmptyRow && position == this.serviceEmptyRow) {
                    ToastUtils.show((int) R.string.NotSupport);
                }
            }
        }
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MessagesController.getInstance(this.currentAccount).loadUserInfo(UserConfig.getInstance(this.currentAccount).getCurrentUser(), true, this.classGuid);
    }

    /* access modifiers changed from: protected */
    public void lazyLoadData() {
        super.lazyLoadData();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    public void onResume() {
        super.onResume();
    }

    public void onResumeForBaseFragment() {
        super.onResumeForBaseFragment();
        if (!BuildVars.DEBUG_VERSION) {
            getFcLocation();
        }
        getActionCount();
        if (this.listAdapter != null && !isFirstTimeInThisPage()) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    private void getActionCount() {
    }

    private void updateRows() {
        this.avatarRow = -1;
        this.avatarEmptyRow = -1;
        this.newWalletRow = -1;
        this.gamesCenterEmptyRow = -1;
        this.digitalcurrency = -1;
        this.digitalcurrencyEmptyRow = -1;
        this.liveIncomeRow = -1;
        this.notifyRow = -1;
        this.privacyRow = -1;
        this.dataRow = -1;
        this.appearanceRow = -1;
        this.langRow = -1;
        this.dataEmptyRow = -1;
        this.aboutRow = -1;
        this.serviceRow = -1;
        this.serviceEmptyRow = -1;
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.avatarRow = 0;
        this.rowCount = i + 1;
        this.avatarEmptyRow = i;
        if (BuildVars.WALLET_ENABLE) {
            int i2 = this.rowCount;
            int i3 = i2 + 1;
            this.rowCount = i3;
            this.newWalletRow = i2;
            this.rowCount = i3 + 1;
            this.newWalletEmptyRow = i3;
        }
        int i4 = this.rowCount;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.privacyRow = i4;
        int i6 = i5 + 1;
        this.rowCount = i6;
        this.notifyRow = i5;
        int i7 = i6 + 1;
        this.rowCount = i7;
        this.langRow = i6;
        this.rowCount = i7 + 1;
        this.dataEmptyRow = i7;
        if (BuildVars.ENABLE_ME_ONLINE_SERVICE) {
            int i8 = this.rowCount;
            this.rowCount = i8 + 1;
            this.serviceRow = i8;
        }
        if (BuildVars.ENABLE_ME_ABOUT_APP) {
            int i9 = this.rowCount;
            this.rowCount = i9 + 1;
            this.aboutRow = i9;
        }
        if (BuildVars.ENABLE_ME_ONLINE_SERVICE) {
            int i10 = this.rowCount;
            this.rowCount = i10 + 1;
            this.serviceEmptyRow = i10;
        }
    }

    public void onDestroy() {
        super.onDestroy();
        BackupImageView backupImageView = this.avatarImage;
        if (backupImageView != null) {
            backupImageView.setImageDrawable((Drawable) null);
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.userFullInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.cdnVipBuySuccess);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        ListAdapter listAdapter2;
        if (id == NotificationCenter.updateInterfaces) {
            int mask = args[0].intValue();
            if (((mask & 2) != 0 || (mask & 1) != 0) && (listAdapter2 = this.listAdapter) != null) {
                listAdapter2.notifyItemChanged(this.avatarRow);
            }
        } else if (id == NotificationCenter.userFullInfoDidLoad) {
            if (args[0].intValue() == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                this.userFull = args[1];
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.userFullInfoDidLoad);
                ListAdapter listAdapter3 = this.listAdapter;
                if (listAdapter3 != null) {
                    listAdapter3.notifyItemChanged(this.avatarRow);
                }
            }
        } else if (id == NotificationCenter.cdnVipBuySuccess) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.userFullInfoDidLoad);
            MessagesController.getInstance(this.currentAccount).loadFullUser(UserConfig.getInstance(this.currentAccount).getCurrentUser(), this.classGuid, true);
        }
    }

    /* access modifiers changed from: private */
    public void saveImage(File file) {
        new Thread(new Runnable(file) {
            private final /* synthetic */ File f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                MeFragmentV2.this.lambda$saveImage$3$MeFragmentV2(this.f$1);
            }
        }).start();
    }

    public /* synthetic */ void lambda$saveImage$3$MeFragmentV2(File file) {
        try {
            if (file.exists()) {
                file.delete();
            }
            while (this.avatarImage.getImageReceiver().getBitmap() == null) {
                Thread.sleep(10);
            }
            Bitmap bitmap = this.avatarImage.getImageReceiver().getBitmap();
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* renamed from: notifyUpdateWalletRow */
    public void lambda$null$4$MeFragmentV2() {
        if (this.listAdapter != null) {
            updateRows();
            this.listAdapter.notifyDataSetChanged();
        }
    }

    private void getFcLocation() {
        getConnectionsManager().sendRequest(new TLRPCFriendsHub.TL_GetOtherConfig(), new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MeFragmentV2.this.lambda$getFcLocation$5$MeFragmentV2(tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$getFcLocation$5$MeFragmentV2(TLObject response, TLRPC.TL_error error) {
        if (error != null) {
            FileLog.e("get friend hub base url failed" + error.text);
            return;
        }
        TLRPCFriendsHub.TL_OtherConfig result = (TLRPCFriendsHub.TL_OtherConfig) response;
        try {
            if (result.data != null && !TextUtils.isEmpty(result.data.data)) {
                boolean z = true;
                if (new JSONObject(result.data.data).getInt("PayTurn") != 1) {
                    z = false;
                }
                if (z != BuildVars.WALLET_ENABLE) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public final void run() {
                            MeFragmentV2.this.lambda$null$4$MeFragmentV2();
                        }
                    });
                }
            }
        } catch (Exception e) {
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return MeFragmentV2.this.rowCount;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Drawable drawable;
            RecyclerView.ViewHolder viewHolder = holder;
            int i = position;
            int itemViewType = holder.getItemViewType();
            if (itemViewType == 0) {
                IndexTextCell2 cell = (IndexTextCell2) viewHolder.itemView;
                int i2 = position;
                if (i2 == MeFragmentV2.this.pcLoginRow) {
                    cell.setTextAndIcon("PC版登录", (int) R.mipmap.fmt_me_pc, (int) R.mipmap.icon_arrow_right, true);
                    cell.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f, Theme.getColor(Theme.key_windowBackgroundWhite)));
                } else if (i2 == MeFragmentV2.this.digitalcurrency) {
                    cell.setTextAndIcon(LocaleController.getString("digitalcurrency", R.string.digitalcurrency), (int) R.drawable.fmt_mev2_digitalcurrency, (int) R.mipmap.icon_arrow_right, true);
                    cell.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f, Theme.getColor(Theme.key_windowBackgroundWhite)));
                } else if (i2 == MeFragmentV2.this.liveIncomeRow) {
                    cell.setTextAndIcon(LocaleController.getString("LiveIncome", R.string.LiveIncome), (int) R.drawable.fmt_live_income, (int) R.mipmap.icon_arrow_right, false);
                    cell.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f, Theme.getColor(Theme.key_windowBackgroundWhite)));
                } else if (i2 == MeFragmentV2.this.cdnVipRow) {
                    cell.setTextAndIcon(LocaleController.getString("OpenCdnVip", R.string.OpenCdnVip), (int) R.drawable.fmt_live_income, (int) R.mipmap.icon_arrow_right, false);
                    cell.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f, Theme.getColor(Theme.key_windowBackgroundWhite)));
                } else if (i2 == MeFragmentV2.this.notifyRow) {
                    cell.setTextAndIcon(LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), (int) R.drawable.fmt_mev2_notify, (int) R.mipmap.icon_arrow_right, true);
                } else if (i2 == MeFragmentV2.this.privacyRow) {
                    cell.setTextAndIcon(LocaleController.getString("PrivacySettings", R.string.PrivacySettings), (int) R.drawable.fmt_mev2_privacy, (int) R.mipmap.icon_arrow_right, true);
                    cell.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f, Theme.getColor(Theme.key_windowBackgroundWhite)));
                } else if (i2 == MeFragmentV2.this.appearanceRow) {
                    cell.setTextAndIcon(LocaleController.getString("Appearance", R.string.Appearance), (int) R.drawable.fmt_mev2_theme, (int) R.mipmap.icon_arrow_right, true);
                } else if (i2 == MeFragmentV2.this.dataRow) {
                    cell.setTextAndIcon(LocaleController.getString("DataSettings", R.string.DataSettings), (int) R.drawable.fmt_mev2_data, (int) R.mipmap.icon_arrow_right, false);
                } else if (i2 == MeFragmentV2.this.langRow) {
                    cell.setTextAndIcon(LocaleController.getString("LanguageSetting", R.string.LanguageSetting), (int) R.drawable.fmt_mev2_lang, (int) R.mipmap.icon_arrow_right, false);
                    cell.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                } else if (i2 == MeFragmentV2.this.faqRow) {
                    cell.setTextAndIcon(LocaleController.getString("faq", R.string.faq), (int) R.drawable.fmt_mev2_faq, (int) R.mipmap.icon_arrow_right, true);
                } else if (i2 == MeFragmentV2.this.aboutRow) {
                    cell.setTextAndIcon(LocaleController.getString("AboutApp", R.string.AboutApp), (int) R.drawable.fmt_mev2_about, (int) R.mipmap.icon_arrow_right, false);
                    cell.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                } else if (i2 == MeFragmentV2.this.serviceRow) {
                    cell.setTextAndIcon(LocaleController.getString("OnlineService", R.string.OnlineService), (int) R.drawable.fmt_mev2_service, (int) R.mipmap.icon_arrow_right, true);
                } else if (i2 == MeFragmentV2.this.inviteFriends) {
                    cell.setTextAndIcon(LocaleController.getString("MeInviteFriends", R.string.MeInviteFriends), (int) R.drawable.fmt_mev2_friends, (int) R.mipmap.icon_arrow_right, true);
                }
            } else if (itemViewType == 1) {
                LinearLayout linearLayout = (LinearLayout) viewHolder.itemView.findViewById(R.id.ll_fmt_nav_container);
                linearLayout.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(6.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                ImageView ivHeaderQRCode = (ImageView) viewHolder.itemView.findViewById(R.id.ivHeaderQRCode);
                FrameLayout flAvatarContainer = (FrameLayout) viewHolder.itemView.findViewById(R.id.flAvatarContainer);
                flAvatarContainer.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(8.0f), -534));
                BackupImageView unused = MeFragmentV2.this.avatarImage = (BackupImageView) viewHolder.itemView.findViewById(R.id.bivHeaderAvatar);
                TextView tvUsername = (TextView) viewHolder.itemView.findViewById(R.id.tvUsername);
                TextView tvUserphone = (TextView) viewHolder.itemView.findViewById(R.id.tvUserphone);
                TextView tvDynamic = (TextView) viewHolder.itemView.findViewById(R.id.tv_dynamic);
                TextView tvFocus = (TextView) viewHolder.itemView.findViewById(R.id.tv_focus);
                TextView tvLikes = (TextView) viewHolder.itemView.findViewById(R.id.tv_likes);
                TextView tvFans = (TextView) viewHolder.itemView.findViewById(R.id.tv_fans);
                TextView tvInfomation = (TextView) viewHolder.itemView.findViewById(R.id.tv_edit_infomation);
                tvInfomation.setText(LocaleController.getString("EditInfomationText", R.string.EditInfomationText));
                tvDynamic.setText(LocaleController.getString("Dynamic", R.string.firendscircle_dynamic));
                tvFocus.setText(LocaleController.getString("Focus", R.string.firendscircle_attention));
                tvLikes.setText(LocaleController.getString("Likes", R.string.firendscircle_get_zan));
                tvFans.setText(LocaleController.getString("Fans", R.string.firendscircle_fans));
                TextView tvDynamicNum = (TextView) viewHolder.itemView.findViewById(R.id.tv_dynamic_num);
                FrameLayout frameLayout = (FrameLayout) viewHolder.itemView.findViewById(R.id.containerLayout);
                TextView tvFocusNum = (TextView) viewHolder.itemView.findViewById(R.id.tv_focus_num);
                LinearLayout linearLayout2 = linearLayout;
                TextView tvLikesNum = (TextView) viewHolder.itemView.findViewById(R.id.tv_likes_num);
                FrameLayout frameLayout2 = flAvatarContainer;
                TextView tvFansNum = (TextView) viewHolder.itemView.findViewById(R.id.tv_fans_num);
                TextView textView = tvDynamic;
                RelativeLayout walletLayout = (RelativeLayout) viewHolder.itemView.findViewById(R.id.rlWalletLayout);
                TextView textView2 = tvFocus;
                FrameLayout albumLayout = (FrameLayout) viewHolder.itemView.findViewById(R.id.flAlbumLayou);
                TextView textView3 = tvLikes;
                FrameLayout collectionLayout = (FrameLayout) viewHolder.itemView.findViewById(R.id.flCollectionLayou);
                TextView textView4 = tvFans;
                TextView textView5 = tvInfomation;
                TextView tvWalletText = (TextView) viewHolder.itemView.findViewById(R.id.tvWalletText);
                tvWalletText.setText(LocaleController.getString("Wallet", R.string.Wallet));
                TextView tvAlbumText = (TextView) viewHolder.itemView.findViewById(R.id.tvAlbumText);
                TextView textView6 = tvWalletText;
                tvAlbumText.setText(LocaleController.getString("Album", R.string.Album));
                TextView tvCollectionText = (TextView) viewHolder.itemView.findViewById(R.id.tvCollectionText);
                TextView textView7 = tvAlbumText;
                tvCollectionText.setText(LocaleController.getString("Collection", R.string.Collection));
                TextView tvStickerText = (TextView) viewHolder.itemView.findViewById(R.id.tvStickerText);
                TextView textView8 = tvCollectionText;
                View findViewById = viewHolder.itemView.findViewById(R.id.parentUserName);
                View ivCdnVipFlag = viewHolder.itemView.findViewById(R.id.ivCdnVipFlag);
                FrameLayout emojiLayout = (FrameLayout) viewHolder.itemView.findViewById(R.id.flEmojiLayout);
                tvStickerText.setText(LocaleController.getString("Sticker", R.string.Sticker));
                walletLayout.setVisibility(MeFragmentV2.this.getMessagesController().enableWallet ? 0 : 8);
                TLRPC.User user = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser();
                if (user != null) {
                    TextView textView9 = tvStickerText;
                    Drawable drawable2 = MeFragmentV2.this.getResources().getDrawable(R.drawable.ic_head_def);
                    tvUsername.setText(UserObject.getName(user));
                    int unMaxWidth = ScreenUtils.getScreenWidth() - AndroidUtilities.dp(197.0f);
                    if (unMaxWidth > 0) {
                        tvUsername.setMaxWidth(unMaxWidth);
                    }
                    TextView textView10 = tvUsername;
                    if (!(MeFragmentV2.this.userFull instanceof TLRPCContacts.CL_userFull_v1) || !((TLRPCContacts.CL_userFull_v1) MeFragmentV2.this.userFull).getExtendBean().cdnVipIsAvailable()) {
                        ivCdnVipFlag.setVisibility(8);
                    } else {
                        ivCdnVipFlag.setVisibility(0);
                    }
                    tvUserphone.setVisibility(TextUtils.isEmpty(user.username) ? 8 : 0);
                    if (!TextUtils.isEmpty(user.username)) {
                        tvUserphone.setText(user.username);
                    }
                    TextView textView11 = tvUserphone;
                    MeFragmentV2.this.avatarImage.setRoundRadius(AndroidUtilities.dp(7.5f));
                    MeFragmentV2.this.avatarImage.getImageReceiver().setCurrentAccount(MeFragmentV2.this.currentAccount);
                    View view = ivCdnVipFlag;
                    MeFragmentV2.this.avatarImage.setImage(ImageLocation.getForUser(user, false), "50_50", drawable2, (Object) user);
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        public final void onClick(View view) {
                            MeFragmentV2.ListAdapter.this.lambda$onBindViewHolder$0$MeFragmentV2$ListAdapter(view);
                        }
                    });
                    MeFragmentV2.this.avatarImage.setOnClickListener(new View.OnClickListener() {
                        public final void onClick(View view) {
                            MeFragmentV2.ListAdapter.this.lambda$onBindViewHolder$1$MeFragmentV2$ListAdapter(view);
                        }
                    });
                    File file = new File(AndroidUtilities.getCacheDir().getPath() + File.separator + "user_avatar.jpg");
                    if (user.photo instanceof TLRPC.TL_userProfilePhoto) {
                        MeFragmentV2.this.saveImage(file);
                    } else if (file.exists()) {
                        file.delete();
                    }
                    ivHeaderQRCode.setOnClickListener(new View.OnClickListener() {
                        public final void onClick(View view) {
                            MeFragmentV2.ListAdapter.this.lambda$onBindViewHolder$2$MeFragmentV2$ListAdapter(view);
                        }
                    });
                    if (MeFragmentV2.this.fcActionCountBean != null) {
                        tvDynamicNum.setText(Integer.toString(MeFragmentV2.this.fcActionCountBean.getForumCount()));
                        tvFocusNum.setText(Integer.toString(MeFragmentV2.this.fcActionCountBean.getFollowCount()));
                        tvLikesNum.setText(Integer.toString(MeFragmentV2.this.fcActionCountBean.getThumbCount()));
                        tvFansNum.setText(Integer.toString(MeFragmentV2.this.fcActionCountBean.getFansCount()));
                    }
                    walletLayout.setBackground(Theme.getSelectorDrawable(false));
                    albumLayout.setBackground(Theme.getSelectorDrawable(false));
                    collectionLayout.setBackground(Theme.getSelectorDrawable(false));
                    FrameLayout emojiLayout2 = emojiLayout;
                    emojiLayout2.setBackground(Theme.getSelectorDrawable(false));
                    walletLayout.setOnClickListener($$Lambda$MeFragmentV2$ListAdapter$UbXq5Dw5OhLUqzvQfgtTpMZjTV8.INSTANCE);
                    albumLayout.setOnClickListener(new View.OnClickListener() {
                        public final void onClick(View view) {
                            MeFragmentV2.ListAdapter.this.lambda$onBindViewHolder$4$MeFragmentV2$ListAdapter(view);
                        }
                    });
                    collectionLayout.setOnClickListener(new View.OnClickListener() {
                        public final void onClick(View view) {
                            MeFragmentV2.ListAdapter.this.lambda$onBindViewHolder$5$MeFragmentV2$ListAdapter(view);
                        }
                    });
                    emojiLayout2.setOnClickListener(new View.OnClickListener() {
                        public final void onClick(View view) {
                            MeFragmentV2.ListAdapter.this.lambda$onBindViewHolder$6$MeFragmentV2$ListAdapter(view);
                        }
                    });
                    int i3 = position;
                }
            } else if (itemViewType == 2) {
                IndexTextCell2 cell2 = (IndexTextCell2) viewHolder.itemView;
                if (MeFragmentV2.this.newWalletRow == holder.getAdapterPosition()) {
                    cell2.setTextAndIcon(LocaleController.getString("WalletCenter", R.string.WalletCenter), (int) R.mipmap.fmt_me_wallet, (int) R.mipmap.icon_arrow_right, false);
                    cell2.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                    int i4 = i;
                    return;
                }
                ((IndexTextCell2) viewHolder.itemView).setTextAndIcon(LocaleController.getString("WalletCenter", R.string.WalletCenter), (int) R.mipmap.fmt_me_wallet, (int) R.mipmap.icon_arrow_right, false);
                int i5 = i;
            } else if (itemViewType == 3) {
                ((IndexTextCell2) viewHolder.itemView).setTextAndIcon(LocaleController.getString("GameCenter", R.string.GameCenter), (int) R.mipmap.fmt_me_games, (int) R.mipmap.icon_arrow_right, false);
                int i6 = i;
            } else if (itemViewType != 4) {
                int i7 = i;
            } else {
                viewHolder.itemView.setTag(Integer.valueOf(position));
                if (i == MeFragmentV2.this.serviceEmptyRow) {
                    drawable = Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow);
                } else {
                    drawable = Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow);
                }
                new CombinedDrawable(new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray)), drawable).setFullsize(true);
                int i8 = i;
            }
        }

        public /* synthetic */ void lambda$onBindViewHolder$0$MeFragmentV2$ListAdapter(View v) {
            MeFragmentV2.this.presentFragment(new NewUserInfoActivity());
        }

        public /* synthetic */ void lambda$onBindViewHolder$1$MeFragmentV2$ListAdapter(View v) {
            TLRPC.User user1 = MessagesController.getInstance(MeFragmentV2.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(MeFragmentV2.this.currentAccount).getClientUserId()));
            if (user1 != null && user1.photo != null && user1.photo.photo_big != null) {
                PhotoViewer.getInstance().setParentActivity(MeFragmentV2.this.getParentActivity());
                if (user1.photo.dc_id != 0) {
                    user1.photo.photo_big.dc_id = user1.photo.dc_id;
                }
                PhotoViewer.getInstance().openPhoto(user1.photo.photo_big, MeFragmentV2.this.provider);
            }
        }

        public /* synthetic */ void lambda$onBindViewHolder$2$MeFragmentV2$ListAdapter(View v) {
            MeFragmentV2.this.presentFragment(new QrCodeActivity(MeFragmentV2.this.getUserConfig().getClientUserId()));
        }

        static /* synthetic */ void lambda$onBindViewHolder$3(View v) {
        }

        public /* synthetic */ void lambda$onBindViewHolder$4$MeFragmentV2$ListAdapter(View v) {
            MeFragmentV2.this.presentFragment(new FcAlbumActivity());
        }

        public /* synthetic */ void lambda$onBindViewHolder$5$MeFragmentV2$ListAdapter(View v) {
            Bundle args = new Bundle();
            args.putLong("dialog_id", (long) UserConfig.getInstance(MeFragmentV2.this.currentAccount).getClientUserId());
            MediaActivity fragment = new MediaActivity(args, new int[]{-1, -1, -1, -1, -1});
            fragment.setChatInfo(MeFragmentV2.this.getMessagesController().getChatFull(UserConfig.getInstance(MeFragmentV2.this.currentAccount).getClientUserId()));
            MeFragmentV2.this.presentFragment(fragment);
        }

        public /* synthetic */ void lambda$onBindViewHolder$6$MeFragmentV2$ListAdapter(View v) {
            MeFragmentV2.this.presentFragment(new StickersActivity(0));
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == MeFragmentV2.this.avatarRow || position == MeFragmentV2.this.pcLoginRow || position == MeFragmentV2.this.aboutRow || position == MeFragmentV2.this.notifyRow || position == MeFragmentV2.this.privacyRow || position == MeFragmentV2.this.dataRow || position == MeFragmentV2.this.digitalcurrency || position == MeFragmentV2.this.cdnVipRow || position == MeFragmentV2.this.faqRow || position == MeFragmentV2.this.appearanceRow || position == MeFragmentV2.this.langRow || position == MeFragmentV2.this.serviceRow || position == MeFragmentV2.this.newWalletRow;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            if (viewType == 0) {
                view = new IndexTextCell2(this.mContext, AndroidUtilities.dp(1.0f));
                RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(-1, -2);
                layoutParams.leftMargin = AndroidUtilities.dp(10.0f);
                layoutParams.rightMargin = AndroidUtilities.dp(10.0f);
                view.setLayoutParams(layoutParams);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (viewType == 1) {
                view = LayoutInflater.from(this.mContext).inflate(R.layout.fmt_header_layout, (ViewGroup) null, false);
                View findViewById = view.findViewById(R.id.ivHeaderQRCode);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(220.0f)));
            } else if (viewType == 2) {
                view = new IndexTextCell2(this.mContext);
                RecyclerView.LayoutParams layoutParams2 = new RecyclerView.LayoutParams(-1, -2);
                layoutParams2.leftMargin = AndroidUtilities.dp(10.0f);
                layoutParams2.rightMargin = AndroidUtilities.dp(10.0f);
                view.setLayoutParams(layoutParams2);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (viewType == 3) {
                view = new IndexTextCell2(this.mContext);
                RecyclerView.LayoutParams layoutParams3 = new RecyclerView.LayoutParams(-1, -2);
                layoutParams3.leftMargin = AndroidUtilities.dp(10.0f);
                layoutParams3.rightMargin = AndroidUtilities.dp(10.0f);
                view.setLayoutParams(layoutParams3);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (viewType == 4) {
                view = new View(this.mContext);
                RecyclerView.LayoutParams layoutParams4 = new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(10.0f));
                layoutParams4.leftMargin = AndroidUtilities.dp(10.0f);
                layoutParams4.rightMargin = AndroidUtilities.dp(10.0f);
                view.setLayoutParams(layoutParams4);
            }
            return new RecyclerListView.Holder(view);
        }

        public int getItemViewType(int position) {
            if (position == MeFragmentV2.this.avatarRow) {
                return 1;
            }
            if (position == MeFragmentV2.this.newWalletRow) {
                return 2;
            }
            if (position == MeFragmentV2.this.gamesCenterEmptyRow || position == MeFragmentV2.this.dataEmptyRow || position == MeFragmentV2.this.serviceEmptyRow || position == MeFragmentV2.this.newWalletEmptyRow || position == MeFragmentV2.this.digitalcurrencyEmptyRow || position == MeFragmentV2.this.avatarEmptyRow || position == MeFragmentV2.this.cdnVipEmptyRow) {
                return 4;
            }
            return 0;
        }
    }

    /* access modifiers changed from: protected */
    public void onDialogDismiss(Dialog dialog) {
        super.onDialogDismiss(dialog);
        this.pressCount = 0;
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
                    MeFragmentV2.this.lambda$sendLogs$7$MeFragmentV2(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$sendLogs$7$MeFragmentV2(AlertDialog progressDialog) {
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
                    MeFragmentV2.this.lambda$null$6$MeFragmentV2(this.f$1, this.f$2, this.f$3);
                }
            });
        } catch (Exception e3) {
            e = e3;
            AlertDialog alertDialog2 = progressDialog;
            e.printStackTrace();
        }
    }

    public /* synthetic */ void lambda$null$6$MeFragmentV2(AlertDialog progressDialog, boolean[] finished, File zipFile) {
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

    public static void performService(BaseFragment fragment) {
        String userString;
        if (fragment != null && fragment.getParentActivity() != null) {
            int currentAccount = fragment.getCurrentAccount();
            SharedPreferences preferences = MessagesController.getMainSettings(currentAccount);
            int uid = preferences.getInt("support_id", 0);
            TLRPC.User supportUser = null;
            if (!(uid == 0 || (supportUser = MessagesController.getInstance(currentAccount).getUser(Integer.valueOf(uid))) != null || (userString = preferences.getString("support_user", (String) null)) == null)) {
                try {
                    byte[] datacentersBytes = Base64.decode(userString, 0);
                    if (datacentersBytes != null) {
                        SerializedData data = new SerializedData(datacentersBytes);
                        supportUser = TLRPC.User.TLdeserialize(data, data.readInt32(false), false);
                        if (supportUser != null && supportUser.id == 333000) {
                            supportUser = null;
                        }
                        data.cleanup();
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                    supportUser = null;
                }
            }
            if (supportUser == null) {
                AlertDialog progressDialog = new AlertDialog(fragment.getParentActivity(), 3);
                progressDialog.setCanCancel(true);
                progressDialog.show();
                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(ConnectionsManager.getInstance(currentAccount).sendRequest(new TLRPC.TL_help_getSupport(), new RequestDelegate(preferences, progressDialog, currentAccount, fragment) {
                    private final /* synthetic */ SharedPreferences f$0;
                    private final /* synthetic */ AlertDialog f$1;
                    private final /* synthetic */ int f$2;
                    private final /* synthetic */ BaseFragment f$3;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MeFragmentV2.lambda$performService$10(this.f$0, this.f$1, this.f$2, this.f$3, tLObject, tL_error);
                    }
                })) {
                    private final /* synthetic */ int f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void onCancel(DialogInterface dialogInterface) {
                        ConnectionsManager.getInstance(UserConfig.selectedAccount).cancelRequest(this.f$0, true);
                    }
                });
                return;
            }
            MessagesController.getInstance(currentAccount).putUser(supportUser, true);
            Bundle args = new Bundle();
            args.putInt("user_id", supportUser.id);
            fragment.presentFragment(new ChatActivity(args));
        }
    }

    static /* synthetic */ void lambda$performService$10(SharedPreferences preferences, AlertDialog progressDialog, int currentAccount, BaseFragment fragment, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            AndroidUtilities.runOnUIThread(new Runnable(preferences, (TLRPC.TL_help_support) response, progressDialog, currentAccount, fragment) {
                private final /* synthetic */ SharedPreferences f$0;
                private final /* synthetic */ TLRPC.TL_help_support f$1;
                private final /* synthetic */ AlertDialog f$2;
                private final /* synthetic */ int f$3;
                private final /* synthetic */ BaseFragment f$4;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run() {
                    MeFragmentV2.lambda$null$8(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    MeFragmentV2.lambda$null$9(AlertDialog.this);
                }
            });
        }
    }

    static /* synthetic */ void lambda$null$8(SharedPreferences preferences, TLRPC.TL_help_support res, AlertDialog progressDialog, int currentAccount, BaseFragment fragment) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("support_id", res.user.id);
        SerializedData data = new SerializedData();
        res.user.serializeToStream(data);
        editor.putString("support_user", Base64.encodeToString(data.toByteArray(), 0));
        editor.apply();
        data.cleanup();
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        ArrayList<TLRPC.User> users = new ArrayList<>();
        users.add(res.user);
        MessagesStorage.getInstance(currentAccount).putUsersAndChats(users, (ArrayList<TLRPC.Chat>) null, true, true);
        MessagesController.getInstance(currentAccount).putUser(res.user, false);
        Bundle args = new Bundle();
        args.putInt("user_id", res.user.id);
        fragment.presentFragment(new ChatActivity(args));
    }

    static /* synthetic */ void lambda$null$9(AlertDialog progressDialog) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }
}
