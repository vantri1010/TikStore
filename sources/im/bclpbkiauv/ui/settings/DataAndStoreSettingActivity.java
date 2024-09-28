package im.bclpbkiauv.ui.settings;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.DownloadController;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.ui.ProxyListActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.voip.VoIPHelper;
import im.bclpbkiauv.ui.hviews.MrySwitch;
import im.bclpbkiauv.ui.settings.DataAndStoreSettingActivity;

public class DataAndStoreSettingActivity extends BaseFragment {

    public interface CallSettingSelectedListener {
        void onSeleted(int i);
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        DownloadController.getInstance(this.currentAccount).loadAutoDownloadConfig(true);
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
                    DataAndStoreSettingActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_setting_data_and_store, (ViewGroup) null, false);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        initView(context);
        initListener();
        return this.fragmentView;
    }

    /* access modifiers changed from: protected */
    public void onDialogDismiss(Dialog dialog) {
        DownloadController.getInstance(this.currentAccount).checkAutodownloadSettings();
    }

    public void onResume() {
        super.onResume();
        initSettingState();
    }

    private void initView(Context context) {
        this.fragmentView.findViewById(R.id.rl_store_number).setBackground(Theme.getRoundRectSelectorDrawable(AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), 0, 0, Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.fragmentView.findViewById(R.id.rl_network_number).setBackground(Theme.getRoundRectSelectorDrawable(0, 0, AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.fragmentView.findViewById(R.id.rl_use_mobile_network).setBackground(Theme.getRoundRectSelectorDrawable(AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), 0, 0, Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.fragmentView.findViewById(R.id.rl_use_wifi_network).setBackground(Theme.getSelectorDrawable(true));
        this.fragmentView.findViewById(R.id.rl_reset_download_file).setBackground(Theme.getRoundRectSelectorDrawable(0, 0, AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.fragmentView.findViewById(R.id.rl_gif).setBackground(Theme.getRoundRectSelectorDrawable(AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), 0, 0, Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.fragmentView.findViewById(R.id.rl_videos).setBackground(Theme.getRoundRectSelectorDrawable(0, 0, AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.fragmentView.findViewById(R.id.rl_use_less_flow).setBackground(Theme.getRoundRectSelectorDrawable(AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
    }

    /* access modifiers changed from: private */
    public void initSettingState() {
        ((MrySwitch) this.fragmentView.findViewById(R.id.switch_gif)).setChecked(SharedConfig.autoplayGifs, true);
        ((MrySwitch) this.fragmentView.findViewById(R.id.switch_videos)).setChecked(SharedConfig.autoplayVideo, true);
        DownloadController controller = DownloadController.getInstance(this.currentAccount);
        boolean blnEnable = !controller.lowPreset.equals(controller.getCurrentRoamingPreset()) || controller.lowPreset.isEnabled() != controller.roamingPreset.enabled || !controller.mediumPreset.equals(controller.getCurrentMobilePreset()) || controller.mediumPreset.isEnabled() != controller.mobilePreset.enabled || !controller.highPreset.equals(controller.getCurrentWiFiPreset()) || controller.highPreset.isEnabled() != controller.wifiPreset.enabled;
        this.fragmentView.findViewById(R.id.rl_reset_download_file).setEnabled(blnEnable);
        this.fragmentView.findViewById(R.id.tv_reset_download_file).setAlpha(blnEnable ? 1.0f : 0.5f);
        setAutoDownloadFileState(0);
        setAutoDownloadFileState(1);
        setUseLessFlowState();
    }

    private void setUseLessFlowState() {
        String value = null;
        int i = MessagesController.getGlobalMainSettings().getInt("VoipDataSaving", VoIPHelper.getDataSavingDefault());
        if (i == 0) {
            value = LocaleController.getString("UseLessDataNever", R.string.UseLessDataNever);
        } else if (i == 1) {
            value = LocaleController.getString("UseLessDataOnMobile", R.string.UseLessDataOnMobile);
        } else if (i == 2) {
            value = LocaleController.getString("UseLessDataAlways", R.string.UseLessDataAlways);
        } else if (i == 3) {
            value = LocaleController.getString("UseLessDataOnRoaming", R.string.UseLessDataOnRoaming);
        }
        ((TextView) this.fragmentView.findViewById(R.id.tv_use_less_flow)).setText(value);
    }

    private void setAutoDownloadFileState(int iNetWorkType) {
        DownloadController.Preset preset;
        boolean enabled;
        String text;
        StringBuilder builder = new StringBuilder();
        if (iNetWorkType == 0) {
            text = LocaleController.getString("WhenUsingMobileData", R.string.WhenUsingMobileData);
            enabled = DownloadController.getInstance(this.currentAccount).mobilePreset.enabled;
            preset = DownloadController.getInstance(this.currentAccount).getCurrentMobilePreset();
        } else {
            text = LocaleController.getString("WhenConnectedOnWiFi", R.string.WhenConnectedOnWiFi);
            enabled = DownloadController.getInstance(this.currentAccount).wifiPreset.enabled;
            preset = DownloadController.getInstance(this.currentAccount).getCurrentWiFiPreset();
        }
        boolean photos = false;
        boolean videos = false;
        boolean files = false;
        int count = 0;
        for (int a = 0; a < preset.mask.length; a++) {
            if (!photos && (preset.mask[a] & 1) != 0) {
                photos = true;
                count++;
            }
            if (!videos && (preset.mask[a] & 4) != 0) {
                videos = true;
                count++;
            }
            if (!files && (preset.mask[a] & 8) != 0) {
                files = true;
                count++;
            }
        }
        if (preset.enabled == 0 || count == 0) {
            boolean z = enabled;
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
                String str = text;
                boolean z2 = enabled;
                builder.append(String.format(" (%1$s)", new Object[]{AndroidUtilities.formatFileSize((long) preset.sizes[DownloadController.typeToIndex(4)], true)}));
            } else {
                boolean z3 = enabled;
            }
            if (files) {
                if (builder.length() > 0) {
                    builder.append(", ");
                }
                builder.append(LocaleController.getString("AutoDownloadFilesOn", R.string.AutoDownloadFilesOn));
                builder.append(String.format(" (%1$s)", new Object[]{AndroidUtilities.formatFileSize((long) preset.sizes[DownloadController.typeToIndex(8)], true)}));
            }
        }
        if (iNetWorkType == 0) {
            ((TextView) this.fragmentView.findViewById(R.id.tv_mobile_content_tip)).setText(builder);
        } else {
            ((TextView) this.fragmentView.findViewById(R.id.tv_wifi_content_tip)).setText(builder);
        }
    }

    private void initListener() {
        this.fragmentView.findViewById(R.id.rl_store_number).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                DataAndStoreSettingActivity.this.presentFragment(new CacheControlSettingActivity());
            }
        });
        this.fragmentView.findViewById(R.id.rl_network_number).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                DataAndStoreSettingActivity.this.presentFragment(new DataUseStatisticsActivity());
            }
        });
        this.fragmentView.findViewById(R.id.rl_use_mobile_network).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                DataAndStoreSettingActivity.this.presentFragment(new AutoDownloadSettingActivity(0));
            }
        });
        this.fragmentView.findViewById(R.id.rl_use_wifi_network).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                DataAndStoreSettingActivity.this.presentFragment(new AutoDownloadSettingActivity(1));
            }
        });
        this.fragmentView.findViewById(R.id.rl_reset_download_file).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (DataAndStoreSettingActivity.this.getParentActivity() != null && view.isEnabled()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) DataAndStoreSettingActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("ResetAutomaticMediaDownloadAlertTitle", R.string.ResetAutomaticMediaDownloadAlertTitle));
                    builder.setMessage(LocaleController.getString("ResetAutomaticMediaDownloadAlert", R.string.ResetAutomaticMediaDownloadAlert));
                    builder.setPositiveButton(LocaleController.getString("Reset", R.string.Reset), new DialogInterface.OnClickListener() {
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            DataAndStoreSettingActivity.AnonymousClass6.this.lambda$onClick$0$DataAndStoreSettingActivity$6(dialogInterface, i);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                    AlertDialog dialog = builder.create();
                    DataAndStoreSettingActivity.this.showDialog(dialog);
                    TextView button = (TextView) dialog.getButton(-1);
                    if (button != null) {
                        button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
                    }
                }
            }

            public /* synthetic */ void lambda$onClick$0$DataAndStoreSettingActivity$6(DialogInterface dialogInterface, int i) {
                String key;
                DownloadController.Preset defaultPreset;
                DownloadController.Preset preset;
                SharedPreferences.Editor editor = MessagesController.getMainSettings(DataAndStoreSettingActivity.this.currentAccount).edit();
                for (int a = 0; a < 3; a++) {
                    if (a == 0) {
                        preset = DownloadController.getInstance(DataAndStoreSettingActivity.this.currentAccount).mobilePreset;
                        defaultPreset = DownloadController.getInstance(DataAndStoreSettingActivity.this.currentAccount).mediumPreset;
                        key = "mobilePreset";
                    } else if (a == 1) {
                        preset = DownloadController.getInstance(DataAndStoreSettingActivity.this.currentAccount).wifiPreset;
                        defaultPreset = DownloadController.getInstance(DataAndStoreSettingActivity.this.currentAccount).highPreset;
                        key = "wifiPreset";
                    } else {
                        preset = DownloadController.getInstance(DataAndStoreSettingActivity.this.currentAccount).roamingPreset;
                        defaultPreset = DownloadController.getInstance(DataAndStoreSettingActivity.this.currentAccount).lowPreset;
                        key = "roamingPreset";
                    }
                    preset.set(defaultPreset);
                    preset.enabled = defaultPreset.isEnabled();
                    DownloadController.getInstance(DataAndStoreSettingActivity.this.currentAccount).currentMobilePreset = 3;
                    editor.putInt("currentMobilePreset", 3);
                    DownloadController.getInstance(DataAndStoreSettingActivity.this.currentAccount).currentWifiPreset = 3;
                    editor.putInt("currentWifiPreset", 3);
                    DownloadController.getInstance(DataAndStoreSettingActivity.this.currentAccount).currentRoamingPreset = 3;
                    editor.putInt("currentRoamingPreset", 3);
                    editor.putString(key, preset.toString());
                }
                editor.commit();
                DownloadController.getInstance(DataAndStoreSettingActivity.this.currentAccount).checkAutodownloadSettings();
                for (int a2 = 0; a2 < 3; a2++) {
                    DownloadController.getInstance(DataAndStoreSettingActivity.this.currentAccount).savePresetToServer(a2);
                }
                DataAndStoreSettingActivity.this.initSettingState();
            }
        });
        this.fragmentView.findViewById(R.id.rl_gif).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SharedConfig.toggleAutoplayGifs();
                ((MrySwitch) DataAndStoreSettingActivity.this.fragmentView.findViewById(R.id.switch_gif)).setChecked(SharedConfig.autoplayGifs, true);
            }
        });
        this.fragmentView.findViewById(R.id.rl_videos).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SharedConfig.toggleAutoplayVideo();
                ((MrySwitch) DataAndStoreSettingActivity.this.fragmentView.findViewById(R.id.switch_videos)).setChecked(SharedConfig.autoplayVideo, true);
            }
        });
        this.fragmentView.findViewById(R.id.rl_use_less_flow).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                final SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                int selected = 0;
                int i = preferences.getInt("VoipDataSaving", VoIPHelper.getDataSavingDefault());
                if (i == 0) {
                    selected = 0;
                } else if (i == 1) {
                    selected = 2;
                } else if (i == 2) {
                    selected = 3;
                } else if (i == 3) {
                    selected = 1;
                }
                DataAndStoreSettingActivity.this.presentFragment(new CallSettingActivity(selected, new CallSettingSelectedListener() {
                    public void onSeleted(int iSeled) {
                        int val = -1;
                        if (iSeled == 0) {
                            val = 0;
                        } else if (iSeled == 1) {
                            val = 3;
                        } else if (iSeled == 2) {
                            val = 1;
                        } else if (iSeled == 3) {
                            val = 2;
                        }
                        if (val != -1) {
                            preferences.edit().putInt("VoipDataSaving", val).commit();
                        }
                        DataAndStoreSettingActivity.this.initSettingState();
                    }
                }));
            }
        });
        this.fragmentView.findViewById(R.id.rl_proxy).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                DataAndStoreSettingActivity.this.presentFragment(new ProxyListActivity());
            }
        });
    }
}
