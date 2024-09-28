package im.bclpbkiauv.ui.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.cells.MaxFileSizeCell;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.hviews.MrySwitch;
import im.bclpbkiauv.ui.settings.AutoDownloadSettingActivity;
import java.util.List;

public class FileAutoDownloadSettingActivity extends BaseFragment {
    private static final int SAVE_BUTTON = 1;
    /* access modifiers changed from: private */
    public List<Boolean> mArrList;
    private FrameLayout mFrameLayout = null;
    /* access modifiers changed from: private */
    public AutoDownloadSettingActivity.activityButtonClickListener mListener = null;
    private long mSize = 0;
    /* access modifiers changed from: private */
    public MrySwitch mswitch_channel;
    /* access modifiers changed from: private */
    public MrySwitch mswitch_contact;
    /* access modifiers changed from: private */
    public MrySwitch mswitch_group_chat;
    /* access modifiers changed from: private */
    public MrySwitch mswitch_private_chat;
    /* access modifiers changed from: private */
    public final MaxFileSizeCell[] sizeCell = new MaxFileSizeCell[1];

    public FileAutoDownloadSettingActivity(List<Boolean> arrList, long lsize, AutoDownloadSettingActivity.activityButtonClickListener listener) {
        this.mArrList = arrList;
        this.mSize = lsize;
        this.mListener = listener;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setTitle(LocaleController.getString("AutoDownloadFilesOn", R.string.AutoDownloadFilesOn));
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.createMenu().addRightItemView(1, LocaleController.getString("Save", R.string.Save));
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_setting_auto_download_file, (ViewGroup) null, false);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        initView(context);
        initListener();
        return this.fragmentView;
    }

    private void initView(Context context) {
        this.mFrameLayout = (FrameLayout) this.fragmentView.findViewById(R.id.fl_container);
        this.mswitch_private_chat = (MrySwitch) this.fragmentView.findViewById(R.id.switch_private_chat);
        this.mswitch_contact = (MrySwitch) this.fragmentView.findViewById(R.id.switch_contact);
        this.mswitch_group_chat = (MrySwitch) this.fragmentView.findViewById(R.id.switch_group_chat);
        this.mswitch_channel = (MrySwitch) this.fragmentView.findViewById(R.id.switch_channel);
        if (this.mArrList != null) {
            for (int i = 0; i < this.mArrList.size(); i++) {
                if (i == 0) {
                    this.mswitch_contact.setChecked(this.mArrList.get(i).booleanValue(), true);
                } else if (i == 1) {
                    this.mswitch_private_chat.setChecked(this.mArrList.get(i).booleanValue(), true);
                } else if (i == 2) {
                    this.mswitch_group_chat.setChecked(this.mArrList.get(i).booleanValue(), true);
                } else if (i == 3) {
                    this.mswitch_channel.setChecked(this.mArrList.get(i).booleanValue(), true);
                }
            }
        }
        this.sizeCell[0] = new MaxFileSizeCell(getParentActivity(), false) {
            /* access modifiers changed from: protected */
            public void didChangedSizeValue(int value) {
            }
        };
        this.sizeCell[0].setSize(this.mSize);
        this.mFrameLayout.addView(this.sizeCell[0], LayoutHelper.createLinear(-1, 50));
        this.mFrameLayout.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.sizeCell[0].setText(LocaleController.getString("AutoDownloadMaxFileSize", R.string.AutoDownloadMaxFileSize));
        this.fragmentView.findViewById(R.id.rl_contact).setBackground(Theme.getRoundRectSelectorDrawable(AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), 0, 0, Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.fragmentView.findViewById(R.id.rl_private_chat).setBackground(Theme.getSelectorDrawable(true));
        this.fragmentView.findViewById(R.id.rl_group_chat).setBackground(Theme.getSelectorDrawable(true));
        this.fragmentView.findViewById(R.id.rl_channel).setBackground(Theme.getRoundRectSelectorDrawable(0, 0, AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
    }

    private void initListener() {
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    FileAutoDownloadSettingActivity.this.finishFragment();
                }
                if (id == 1) {
                    if (FileAutoDownloadSettingActivity.this.mListener != null) {
                        FileAutoDownloadSettingActivity.this.mListener.onSaveBtnClick(FileAutoDownloadSettingActivity.this.mArrList, FileAutoDownloadSettingActivity.this.sizeCell[0].getSize(), false);
                    }
                    FileAutoDownloadSettingActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView.findViewById(R.id.rl_contact).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                FileAutoDownloadSettingActivity.this.mswitch_contact.setChecked(!FileAutoDownloadSettingActivity.this.mswitch_contact.isChecked(), true);
                FileAutoDownloadSettingActivity.this.mArrList.set(0, Boolean.valueOf(FileAutoDownloadSettingActivity.this.mswitch_contact.isChecked()));
            }
        });
        this.fragmentView.findViewById(R.id.rl_private_chat).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                FileAutoDownloadSettingActivity.this.mswitch_private_chat.setChecked(!FileAutoDownloadSettingActivity.this.mswitch_private_chat.isChecked(), true);
                FileAutoDownloadSettingActivity.this.mArrList.set(1, Boolean.valueOf(FileAutoDownloadSettingActivity.this.mswitch_private_chat.isChecked()));
            }
        });
        this.fragmentView.findViewById(R.id.rl_group_chat).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                FileAutoDownloadSettingActivity.this.mswitch_group_chat.setChecked(!FileAutoDownloadSettingActivity.this.mswitch_group_chat.isChecked(), true);
                FileAutoDownloadSettingActivity.this.mArrList.set(2, Boolean.valueOf(FileAutoDownloadSettingActivity.this.mswitch_group_chat.isChecked()));
            }
        });
        this.fragmentView.findViewById(R.id.rl_channel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                FileAutoDownloadSettingActivity.this.mswitch_channel.setChecked(!FileAutoDownloadSettingActivity.this.mswitch_channel.isChecked(), true);
                FileAutoDownloadSettingActivity.this.mArrList.set(3, Boolean.valueOf(FileAutoDownloadSettingActivity.this.mswitch_channel.isChecked()));
            }
        });
    }
}
