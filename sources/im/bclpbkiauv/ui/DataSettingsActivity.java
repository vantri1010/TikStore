package im.bclpbkiauv.ui;

import android.animation.AnimatorSet;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.DownloadController;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.HeaderCell;
import im.bclpbkiauv.ui.cells.NotificationsCheckCell;
import im.bclpbkiauv.ui.cells.ShadowSectionCell;
import im.bclpbkiauv.ui.cells.TextCheckCell;
import im.bclpbkiauv.ui.cells.TextInfoPrivacyCell;
import im.bclpbkiauv.ui.cells.TextSettingsCell;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.voip.VoIPHelper;

@Deprecated
public class DataSettingsActivity extends BaseFragment {
    private AnimatorSet animatorSet;
    /* access modifiers changed from: private */
    public int autoplayGifsRow;
    /* access modifiers changed from: private */
    public int autoplayHeaderRow;
    /* access modifiers changed from: private */
    public int autoplaySectionRow;
    /* access modifiers changed from: private */
    public int autoplayVideoRow;
    /* access modifiers changed from: private */
    public int callsSection2Row;
    /* access modifiers changed from: private */
    public int callsSectionRow;
    /* access modifiers changed from: private */
    public int dataUsageRow;
    /* access modifiers changed from: private */
    public int enableAllStreamInfoRow;
    /* access modifiers changed from: private */
    public int enableAllStreamRow;
    /* access modifiers changed from: private */
    public int enableCacheStreamRow;
    /* access modifiers changed from: private */
    public int enableMkvRow;
    /* access modifiers changed from: private */
    public int enableStreamRow;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public int mediaDownloadSection2Row;
    /* access modifiers changed from: private */
    public int mediaDownloadSectionRow;
    /* access modifiers changed from: private */
    public int mobileRow;
    /* access modifiers changed from: private */
    public int proxyRow;
    /* access modifiers changed from: private */
    public int proxySection2Row;
    /* access modifiers changed from: private */
    public int proxySectionRow;
    /* access modifiers changed from: private */
    public int quickRepliesRow;
    /* access modifiers changed from: private */
    public int resetDownloadRow;
    /* access modifiers changed from: private */
    public int roamingRow;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int storageUsageRow;
    /* access modifiers changed from: private */
    public int streamSectionRow;
    /* access modifiers changed from: private */
    public int usageSection2Row;
    /* access modifiers changed from: private */
    public int usageSectionRow;
    /* access modifiers changed from: private */
    public int useLessDataForCallsRow;
    /* access modifiers changed from: private */
    public int wifiRow;

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        DownloadController.getInstance(this.currentAccount).loadAutoDownloadConfig(true);
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.usageSectionRow = 0;
        int i2 = i + 1;
        this.rowCount = i2;
        this.storageUsageRow = i;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.dataUsageRow = i2;
        int i4 = i3 + 1;
        this.rowCount = i4;
        this.usageSection2Row = i3;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.mediaDownloadSectionRow = i4;
        int i6 = i5 + 1;
        this.rowCount = i6;
        this.mobileRow = i5;
        int i7 = i6 + 1;
        this.rowCount = i7;
        this.wifiRow = i6;
        int i8 = i7 + 1;
        this.rowCount = i8;
        this.roamingRow = i7;
        int i9 = i8 + 1;
        this.rowCount = i9;
        this.resetDownloadRow = i8;
        int i10 = i9 + 1;
        this.rowCount = i10;
        this.mediaDownloadSection2Row = i9;
        int i11 = i10 + 1;
        this.rowCount = i11;
        this.autoplayHeaderRow = i10;
        int i12 = i11 + 1;
        this.rowCount = i12;
        this.autoplayGifsRow = i11;
        int i13 = i12 + 1;
        this.rowCount = i13;
        this.autoplayVideoRow = i12;
        int i14 = i13 + 1;
        this.rowCount = i14;
        this.autoplaySectionRow = i13;
        int i15 = i14 + 1;
        this.rowCount = i15;
        this.streamSectionRow = i14;
        this.rowCount = i15 + 1;
        this.enableStreamRow = i15;
        if (BuildVars.DEBUG_VERSION) {
            int i16 = this.rowCount;
            int i17 = i16 + 1;
            this.rowCount = i17;
            this.enableMkvRow = i16;
            this.rowCount = i17 + 1;
            this.enableAllStreamRow = i17;
        } else {
            this.enableAllStreamRow = -1;
            this.enableMkvRow = -1;
        }
        int i18 = this.rowCount;
        int i19 = i18 + 1;
        this.rowCount = i19;
        this.enableAllStreamInfoRow = i18;
        this.enableCacheStreamRow = -1;
        int i20 = i19 + 1;
        this.rowCount = i20;
        this.callsSectionRow = i19;
        int i21 = i20 + 1;
        this.rowCount = i21;
        this.useLessDataForCallsRow = i20;
        int i22 = i21 + 1;
        this.rowCount = i22;
        this.quickRepliesRow = i21;
        int i23 = i22 + 1;
        this.rowCount = i23;
        this.callsSection2Row = i22;
        int i24 = i23 + 1;
        this.rowCount = i24;
        this.proxySectionRow = i23;
        int i25 = i24 + 1;
        this.rowCount = i25;
        this.proxyRow = i24;
        this.rowCount = i25 + 1;
        this.proxySection2Row = i25;
        return true;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setTitle(LocaleController.getString("DataSettings", R.string.DataSettings));
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    DataSettingsActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        ((FrameLayout) this.fragmentView).addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new RecyclerListView.OnItemClickListenerExtended() {
            public final void onItemClick(View view, int i, float f, float f2) {
                DataSettingsActivity.this.lambda$createView$2$DataSettingsActivity(view, i, f, f2);
            }
        });
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$2$DataSettingsActivity(View view, int position, float x, float y) {
        int num;
        String key2;
        String key;
        DownloadController.Preset defaultPreset;
        DownloadController.Preset preset;
        int type;
        View view2 = view;
        int i = position;
        if (i == this.mobileRow || i == this.roamingRow || i == this.wifiRow) {
            if ((!LocaleController.isRTL || x > ((float) AndroidUtilities.dp(76.0f))) && (LocaleController.isRTL || x < ((float) (view.getMeasuredWidth() - AndroidUtilities.dp(76.0f))))) {
                if (i == this.mobileRow) {
                    type = 0;
                } else if (i == this.wifiRow) {
                    type = 1;
                } else {
                    type = 2;
                }
                presentFragment(new DataAutoDownloadActivity(type));
                return;
            }
            boolean wasEnabled = this.listAdapter.isRowEnabled(this.resetDownloadRow);
            NotificationsCheckCell cell = (NotificationsCheckCell) view2;
            boolean checked = cell.isChecked();
            if (i == this.mobileRow) {
                preset = DownloadController.getInstance(this.currentAccount).mobilePreset;
                defaultPreset = DownloadController.getInstance(this.currentAccount).mediumPreset;
                key = "mobilePreset";
                key2 = "currentMobilePreset";
                num = 0;
            } else if (i == this.wifiRow) {
                preset = DownloadController.getInstance(this.currentAccount).wifiPreset;
                defaultPreset = DownloadController.getInstance(this.currentAccount).highPreset;
                key = "wifiPreset";
                key2 = "currentWifiPreset";
                num = 1;
            } else {
                preset = DownloadController.getInstance(this.currentAccount).roamingPreset;
                defaultPreset = DownloadController.getInstance(this.currentAccount).lowPreset;
                key = "roamingPreset";
                key2 = "currentRoamingPreset";
                num = 2;
            }
            if (checked || !preset.enabled) {
                preset.enabled = true ^ preset.enabled;
            } else {
                preset.set(defaultPreset);
            }
            SharedPreferences.Editor editor = MessagesController.getMainSettings(this.currentAccount).edit();
            editor.putString(key, preset.toString());
            editor.putInt(key2, 3);
            editor.commit();
            cell.setChecked(!checked);
            RecyclerView.ViewHolder holder = this.listView.findContainingViewHolder(view2);
            if (holder != null) {
                this.listAdapter.onBindViewHolder(holder, i);
            }
            DownloadController.getInstance(this.currentAccount).checkAutodownloadSettings();
            DownloadController.getInstance(this.currentAccount).savePresetToServer(num);
            if (wasEnabled != this.listAdapter.isRowEnabled(this.resetDownloadRow)) {
                this.listAdapter.notifyItemChanged(this.resetDownloadRow);
            }
        } else if (i == this.resetDownloadRow) {
            if (getParentActivity() != null && view.isEnabled()) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setTitle(LocaleController.getString("ResetAutomaticMediaDownloadAlertTitle", R.string.ResetAutomaticMediaDownloadAlertTitle));
                builder.setMessage(LocaleController.getString("ResetAutomaticMediaDownloadAlert", R.string.ResetAutomaticMediaDownloadAlert));
                builder.setPositiveButton(LocaleController.getString("Reset", R.string.Reset), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        DataSettingsActivity.this.lambda$null$0$DataSettingsActivity(dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                AlertDialog dialog = builder.create();
                showDialog(dialog);
                TextView button = (TextView) dialog.getButton(-1);
                if (button != null) {
                    button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
                }
            }
        } else if (i == this.storageUsageRow) {
            presentFragment(new CacheControlActivity());
        } else if (i == this.useLessDataForCallsRow) {
            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
            int selected = 0;
            int i2 = preferences.getInt("VoipDataSaving", VoIPHelper.getDataSavingDefault());
            if (i2 == 0) {
                selected = 0;
            } else if (i2 == 1) {
                selected = 2;
            } else if (i2 == 2) {
                selected = 3;
            } else if (i2 == 3) {
                selected = 1;
            }
            Dialog dlg = AlertsCreator.createSingleChoiceDialog(getParentActivity(), new String[]{LocaleController.getString("UseLessDataNever", R.string.UseLessDataNever), LocaleController.getString("UseLessDataOnRoaming", R.string.UseLessDataOnRoaming), LocaleController.getString("UseLessDataOnMobile", R.string.UseLessDataOnMobile), LocaleController.getString("UseLessDataAlways", R.string.UseLessDataAlways)}, LocaleController.getString("VoipUseLessData", R.string.VoipUseLessData), selected, new DialogInterface.OnClickListener(preferences, i) {
                private final /* synthetic */ SharedPreferences f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    DataSettingsActivity.this.lambda$null$1$DataSettingsActivity(this.f$1, this.f$2, dialogInterface, i);
                }
            });
            setVisibleDialog(dlg);
            dlg.show();
        } else if (i == this.dataUsageRow) {
            presentFragment(new DataUsageActivity());
        } else if (i == this.proxyRow) {
            presentFragment(new ProxyListActivity());
        } else if (i == this.enableStreamRow) {
            SharedConfig.toggleStreamMedia();
            ((TextCheckCell) view2).setChecked(SharedConfig.streamMedia);
        } else if (i == this.enableAllStreamRow) {
            SharedConfig.toggleStreamAllVideo();
            ((TextCheckCell) view2).setChecked(SharedConfig.streamAllVideo);
        } else if (i == this.enableMkvRow) {
            SharedConfig.toggleStreamMkv();
            ((TextCheckCell) view2).setChecked(SharedConfig.streamMkv);
        } else if (i == this.enableCacheStreamRow) {
            SharedConfig.toggleSaveStreamMedia();
            ((TextCheckCell) view2).setChecked(SharedConfig.saveStreamMedia);
        } else if (i == this.quickRepliesRow) {
            presentFragment(new QuickRepliesSettingsActivity());
        } else if (i == this.autoplayGifsRow) {
            SharedConfig.toggleAutoplayGifs();
            if (view2 instanceof TextCheckCell) {
                ((TextCheckCell) view2).setChecked(SharedConfig.autoplayGifs);
            }
        } else if (i == this.autoplayVideoRow) {
            SharedConfig.toggleAutoplayVideo();
            if (view2 instanceof TextCheckCell) {
                ((TextCheckCell) view2).setChecked(SharedConfig.autoplayVideo);
            }
        }
    }

    public /* synthetic */ void lambda$null$0$DataSettingsActivity(DialogInterface dialogInterface, int i) {
        String key;
        DownloadController.Preset defaultPreset;
        DownloadController.Preset preset;
        SharedPreferences.Editor editor = MessagesController.getMainSettings(this.currentAccount).edit();
        for (int a = 0; a < 3; a++) {
            if (a == 0) {
                preset = DownloadController.getInstance(this.currentAccount).mobilePreset;
                defaultPreset = DownloadController.getInstance(this.currentAccount).mediumPreset;
                key = "mobilePreset";
            } else if (a == 1) {
                preset = DownloadController.getInstance(this.currentAccount).wifiPreset;
                defaultPreset = DownloadController.getInstance(this.currentAccount).highPreset;
                key = "wifiPreset";
            } else {
                preset = DownloadController.getInstance(this.currentAccount).roamingPreset;
                defaultPreset = DownloadController.getInstance(this.currentAccount).lowPreset;
                key = "roamingPreset";
            }
            preset.set(defaultPreset);
            preset.enabled = defaultPreset.isEnabled();
            DownloadController.getInstance(this.currentAccount).currentMobilePreset = 3;
            editor.putInt("currentMobilePreset", 3);
            DownloadController.getInstance(this.currentAccount).currentWifiPreset = 3;
            editor.putInt("currentWifiPreset", 3);
            DownloadController.getInstance(this.currentAccount).currentRoamingPreset = 3;
            editor.putInt("currentRoamingPreset", 3);
            editor.putString(key, preset.toString());
        }
        editor.commit();
        DownloadController.getInstance(this.currentAccount).checkAutodownloadSettings();
        for (int a2 = 0; a2 < 3; a2++) {
            DownloadController.getInstance(this.currentAccount).savePresetToServer(a2);
        }
        this.listAdapter.notifyItemRangeChanged(this.mobileRow, 4);
    }

    public /* synthetic */ void lambda$null$1$DataSettingsActivity(SharedPreferences preferences, int position, DialogInterface dialog, int which) {
        int val = -1;
        if (which == 0) {
            val = 0;
        } else if (which == 1) {
            val = 3;
        } else if (which == 2) {
            val = 1;
        } else if (which == 3) {
            val = 2;
        }
        if (val != -1) {
            preferences.edit().putInt("VoipDataSaving", val).commit();
        }
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyItemChanged(position);
        }
    }

    /* access modifiers changed from: protected */
    public void onDialogDismiss(Dialog dialog) {
        DownloadController.getInstance(this.currentAccount).checkAutodownloadSettings();
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return DataSettingsActivity.this.rowCount;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean enabled;
            String text;
            DownloadController.Preset preset;
            RecyclerView.ViewHolder viewHolder = holder;
            int i = position;
            int itemViewType = holder.getItemViewType();
            if (itemViewType != 0) {
                boolean z = false;
                if (itemViewType == 1) {
                    TextSettingsCell textCell = (TextSettingsCell) viewHolder.itemView;
                    textCell.setCanDisable(false);
                    textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    if (i == DataSettingsActivity.this.storageUsageRow) {
                        textCell.setText(LocaleController.getString("StorageUsage", R.string.StorageUsage), true);
                    } else if (i == DataSettingsActivity.this.useLessDataForCallsRow) {
                        String value = null;
                        int i2 = MessagesController.getGlobalMainSettings().getInt("VoipDataSaving", VoIPHelper.getDataSavingDefault());
                        if (i2 == 0) {
                            value = LocaleController.getString("UseLessDataNever", R.string.UseLessDataNever);
                        } else if (i2 == 1) {
                            value = LocaleController.getString("UseLessDataOnMobile", R.string.UseLessDataOnMobile);
                        } else if (i2 == 2) {
                            value = LocaleController.getString("UseLessDataAlways", R.string.UseLessDataAlways);
                        } else if (i2 == 3) {
                            value = LocaleController.getString("UseLessDataOnRoaming", R.string.UseLessDataOnRoaming);
                        }
                        textCell.setTextAndValue(LocaleController.getString("VoipUseLessData", R.string.VoipUseLessData), value, true);
                    } else if (i == DataSettingsActivity.this.dataUsageRow) {
                        textCell.setText(LocaleController.getString("NetworkUsage", R.string.NetworkUsage), false);
                    } else if (i == DataSettingsActivity.this.proxyRow) {
                        textCell.setText(LocaleController.getString("ProxySettings", R.string.ProxySettings), false);
                    } else if (i == DataSettingsActivity.this.resetDownloadRow) {
                        textCell.setCanDisable(true);
                        textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText));
                        textCell.setText(LocaleController.getString("ResetAutomaticMediaDownload", R.string.ResetAutomaticMediaDownload), false);
                    } else if (i == DataSettingsActivity.this.quickRepliesRow) {
                        textCell.setText(LocaleController.getString("VoipQuickReplies", R.string.VoipQuickReplies), false);
                    }
                } else if (itemViewType == 2) {
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    if (i == DataSettingsActivity.this.mediaDownloadSectionRow) {
                        headerCell.setText(LocaleController.getString("AutomaticMediaDownload", R.string.AutomaticMediaDownload));
                    } else if (i == DataSettingsActivity.this.usageSectionRow) {
                        headerCell.setText(LocaleController.getString("DataUsage", R.string.DataUsage));
                    } else if (i == DataSettingsActivity.this.callsSectionRow) {
                        headerCell.setText(LocaleController.getString("Calls", R.string.Calls));
                    } else if (i == DataSettingsActivity.this.proxySectionRow) {
                        headerCell.setText(LocaleController.getString("Proxy", R.string.Proxy));
                    } else if (i == DataSettingsActivity.this.streamSectionRow) {
                        headerCell.setText(LocaleController.getString("Streaming", R.string.Streaming));
                    } else if (i == DataSettingsActivity.this.autoplayHeaderRow) {
                        headerCell.setText(LocaleController.getString("AutoplayMedia", R.string.AutoplayMedia));
                    }
                } else if (itemViewType == 3) {
                    TextCheckCell checkCell = (TextCheckCell) viewHolder.itemView;
                    if (i == DataSettingsActivity.this.enableStreamRow) {
                        String string = LocaleController.getString("EnableStreaming", R.string.EnableStreaming);
                        boolean z2 = SharedConfig.streamMedia;
                        if (DataSettingsActivity.this.enableAllStreamRow != -1) {
                            z = true;
                        }
                        checkCell.setTextAndCheck(string, z2, z);
                    } else if (i != DataSettingsActivity.this.enableCacheStreamRow) {
                        if (i == DataSettingsActivity.this.enableMkvRow) {
                            checkCell.setTextAndCheck("(beta only) Show MKV as Video", SharedConfig.streamMkv, true);
                        } else if (i == DataSettingsActivity.this.enableAllStreamRow) {
                            checkCell.setTextAndCheck("(beta only) Stream All Videos", SharedConfig.streamAllVideo, false);
                        } else if (i == DataSettingsActivity.this.autoplayGifsRow) {
                            checkCell.setTextAndCheck(LocaleController.getString("AutoplayGIF", R.string.AutoplayGIF), SharedConfig.autoplayGifs, true);
                        } else if (i == DataSettingsActivity.this.autoplayVideoRow) {
                            checkCell.setTextAndCheck(LocaleController.getString("AutoplayVideo", R.string.AutoplayVideo), SharedConfig.autoplayVideo, false);
                        }
                    }
                } else if (itemViewType == 4) {
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) viewHolder.itemView;
                    if (i == DataSettingsActivity.this.enableAllStreamInfoRow) {
                        cell.setText(LocaleController.getString("EnableAllStreamingInfo", R.string.EnableAllStreamingInfo));
                    }
                } else if (itemViewType == 5) {
                    NotificationsCheckCell checkCell2 = (NotificationsCheckCell) viewHolder.itemView;
                    StringBuilder builder = new StringBuilder();
                    if (i == DataSettingsActivity.this.mobileRow) {
                        text = LocaleController.getString("WhenUsingMobileData", R.string.WhenUsingMobileData);
                        enabled = DownloadController.getInstance(DataSettingsActivity.this.currentAccount).mobilePreset.enabled;
                        preset = DownloadController.getInstance(DataSettingsActivity.this.currentAccount).getCurrentMobilePreset();
                    } else if (i == DataSettingsActivity.this.wifiRow) {
                        text = LocaleController.getString("WhenConnectedOnWiFi", R.string.WhenConnectedOnWiFi);
                        enabled = DownloadController.getInstance(DataSettingsActivity.this.currentAccount).wifiPreset.enabled;
                        preset = DownloadController.getInstance(DataSettingsActivity.this.currentAccount).getCurrentWiFiPreset();
                    } else {
                        text = LocaleController.getString("WhenRoaming", R.string.WhenRoaming);
                        enabled = DownloadController.getInstance(DataSettingsActivity.this.currentAccount).roamingPreset.enabled;
                        preset = DownloadController.getInstance(DataSettingsActivity.this.currentAccount).getCurrentRoamingPreset();
                    }
                    boolean photos = false;
                    boolean videos = false;
                    boolean files = false;
                    int count = 0;
                    for (int a = 0; a < preset.mask.length; a++) {
                        if (!photos && (preset.mask[a] & 1) != 0) {
                            count++;
                            photos = true;
                        }
                        if (!videos && (preset.mask[a] & 4) != 0) {
                            count++;
                            videos = true;
                        }
                        if (!files && (preset.mask[a] & 8) != 0) {
                            count++;
                            files = true;
                        }
                    }
                    if (!preset.enabled || count == 0) {
                        builder.append(LocaleController.getString("NoMediaAutoDownload", R.string.NoMediaAutoDownload));
                    } else {
                        if (photos) {
                            builder.append(LocaleController.getString("AutoDownloadPhotosOn", R.string.AutoDownloadPhotosOn));
                        }
                        if (videos) {
                            if (builder.length() > 0) {
                                builder.append(", ");
                            }
                            builder.append(LocaleController.getString("AutoDownloadVideosOn", R.string.AutoDownloadVideosOn));
                            builder.append(String.format(" (%1$s)", new Object[]{AndroidUtilities.formatFileSize((long) preset.sizes[DownloadController.typeToIndex(4)], true)}));
                        }
                        if (files) {
                            if (builder.length() > 0) {
                                builder.append(", ");
                            }
                            builder.append(LocaleController.getString("AutoDownloadFilesOn", R.string.AutoDownloadFilesOn));
                            builder.append(String.format(" (%1$s)", new Object[]{AndroidUtilities.formatFileSize((long) preset.sizes[DownloadController.typeToIndex(8)], true)}));
                        }
                    }
                    DownloadController.Preset preset2 = preset;
                    checkCell2.setTextAndValueAndCheck(text, builder, (photos || videos || files) && enabled, 0, true, true);
                }
            } else if (i == DataSettingsActivity.this.proxySection2Row) {
                viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            } else {
                viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
            }
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            if (holder.getItemViewType() == 3) {
                TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                int position = holder.getAdapterPosition();
                if (position == DataSettingsActivity.this.enableCacheStreamRow) {
                    checkCell.setChecked(SharedConfig.saveStreamMedia);
                } else if (position == DataSettingsActivity.this.enableStreamRow) {
                    checkCell.setChecked(SharedConfig.streamMedia);
                } else if (position == DataSettingsActivity.this.enableAllStreamRow) {
                    checkCell.setChecked(SharedConfig.streamAllVideo);
                } else if (position == DataSettingsActivity.this.enableMkvRow) {
                    checkCell.setChecked(SharedConfig.streamMkv);
                } else if (position == DataSettingsActivity.this.autoplayGifsRow) {
                    checkCell.setChecked(SharedConfig.autoplayGifs);
                } else if (position == DataSettingsActivity.this.autoplayVideoRow) {
                    checkCell.setChecked(SharedConfig.autoplayVideo);
                }
            }
        }

        public boolean isRowEnabled(int position) {
            if (position == DataSettingsActivity.this.resetDownloadRow) {
                DownloadController controller = DownloadController.getInstance(DataSettingsActivity.this.currentAccount);
                if (!controller.lowPreset.equals(controller.getCurrentRoamingPreset()) || controller.lowPreset.isEnabled() != controller.roamingPreset.enabled || !controller.mediumPreset.equals(controller.getCurrentMobilePreset()) || controller.mediumPreset.isEnabled() != controller.mobilePreset.enabled || !controller.highPreset.equals(controller.getCurrentWiFiPreset()) || controller.highPreset.isEnabled() != controller.wifiPreset.enabled) {
                    return true;
                }
                return false;
            } else if (position == DataSettingsActivity.this.mobileRow || position == DataSettingsActivity.this.roamingRow || position == DataSettingsActivity.this.wifiRow || position == DataSettingsActivity.this.storageUsageRow || position == DataSettingsActivity.this.useLessDataForCallsRow || position == DataSettingsActivity.this.dataUsageRow || position == DataSettingsActivity.this.proxyRow || position == DataSettingsActivity.this.enableCacheStreamRow || position == DataSettingsActivity.this.enableStreamRow || position == DataSettingsActivity.this.enableAllStreamRow || position == DataSettingsActivity.this.enableMkvRow || position == DataSettingsActivity.this.quickRepliesRow || position == DataSettingsActivity.this.autoplayVideoRow || position == DataSettingsActivity.this.autoplayGifsRow) {
                return true;
            } else {
                return false;
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return isRowEnabled(holder.getAdapterPosition());
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            if (viewType == 0) {
                view = new ShadowSectionCell(this.mContext);
            } else if (viewType == 1) {
                view = new TextSettingsCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (viewType == 2) {
                view = new HeaderCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (viewType == 3) {
                view = new TextCheckCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (viewType == 4) {
                view = new TextInfoPrivacyCell(this.mContext);
                view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
            } else if (viewType == 5) {
                view = new NotificationsCheckCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public int getItemViewType(int position) {
            if (position == DataSettingsActivity.this.mediaDownloadSection2Row || position == DataSettingsActivity.this.usageSection2Row || position == DataSettingsActivity.this.callsSection2Row || position == DataSettingsActivity.this.proxySection2Row || position == DataSettingsActivity.this.autoplaySectionRow) {
                return 0;
            }
            if (position == DataSettingsActivity.this.mediaDownloadSectionRow || position == DataSettingsActivity.this.streamSectionRow || position == DataSettingsActivity.this.callsSectionRow || position == DataSettingsActivity.this.usageSectionRow || position == DataSettingsActivity.this.proxySectionRow || position == DataSettingsActivity.this.autoplayHeaderRow) {
                return 2;
            }
            if (position == DataSettingsActivity.this.enableCacheStreamRow || position == DataSettingsActivity.this.enableStreamRow || position == DataSettingsActivity.this.enableAllStreamRow || position == DataSettingsActivity.this.enableMkvRow || position == DataSettingsActivity.this.autoplayGifsRow || position == DataSettingsActivity.this.autoplayVideoRow) {
                return 3;
            }
            if (position == DataSettingsActivity.this.enableAllStreamInfoRow) {
                return 4;
            }
            if (position == DataSettingsActivity.this.mobileRow || position == DataSettingsActivity.this.wifiRow || position == DataSettingsActivity.this.roamingRow) {
                return 5;
            }
            return 1;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, TextCheckCell.class, HeaderCell.class, NotificationsCheckCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrack), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrackChecked), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_divider), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteValueText), new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlueHeader), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrack), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrackChecked), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText4)};
    }
}
