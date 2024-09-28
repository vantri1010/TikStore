package im.bclpbkiauv.ui.settings;

import android.animation.Animator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.List;

public class VideoAutoDownloadSettingActivity extends BaseFragment {
    private static final int SAVE_BUTTON = 1;
    /* access modifiers changed from: private */
    public List<Boolean> mArrList;
    private FrameLayout mFrameLayout = null;
    /* access modifiers changed from: private */
    public AutoDownloadSettingActivity.activityButtonClickListener mListener = null;
    /* access modifiers changed from: private */
    public RelativeLayout mRlPreload;
    /* access modifiers changed from: private */
    public MrySwitch mScPreload;
    private long mSize = 0;
    private TextView mTvPreload;
    /* access modifiers changed from: private */
    public TextView mTvTip;
    private boolean mblnChecked;
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

    public VideoAutoDownloadSettingActivity(List<Boolean> arrList, long lsize, boolean blnChecked, AutoDownloadSettingActivity.activityButtonClickListener listener) {
        this.mArrList = arrList;
        this.mSize = lsize;
        this.mblnChecked = blnChecked;
        this.mListener = listener;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setTitle(LocaleController.getString("AutoDownloadVideosOn", R.string.AutoDownloadVideosOn));
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.createMenu().addRightItemView(1, LocaleController.getString("Save", R.string.Save));
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_setting_auto_download_video, (ViewGroup) null, false);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        initView(context);
        initListener();
        return this.fragmentView;
    }

    private void initView(Context context) {
        this.mScPreload = (MrySwitch) this.fragmentView.findViewById(R.id.switch_preload);
        this.mTvTip = (TextView) this.fragmentView.findViewById(R.id.tv_tip);
        this.mRlPreload = (RelativeLayout) this.fragmentView.findViewById(R.id.rl_preload);
        this.mFrameLayout = (FrameLayout) this.fragmentView.findViewById(R.id.fl_container);
        this.mTvPreload = (TextView) this.fragmentView.findViewById(R.id.tv_preload);
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
        this.sizeCell[0] = new MaxFileSizeCell(getParentActivity()) {
            /* access modifiers changed from: protected */
            public void didChangedSizeValue(int value) {
                boolean z = true;
                VideoAutoDownloadSettingActivity.this.mTvTip.setText(LocaleController.formatString("AutoDownloadPreloadVideoInfo", R.string.AutoDownloadPreloadVideoInfo, AndroidUtilities.formatFileSize((long) value)));
                if (value <= 2097152) {
                    z = false;
                }
                boolean enabled = z;
                if (enabled != VideoAutoDownloadSettingActivity.this.mRlPreload.isEnabled()) {
                    VideoAutoDownloadSettingActivity.this.preLoadEnabled(enabled);
                }
            }
        };
        this.sizeCell[0].setSize(this.mSize);
        this.mFrameLayout.addView(this.sizeCell[0], LayoutHelper.createLinear(-1, 50));
        this.mFrameLayout.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f, Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.sizeCell[0].setText(LocaleController.getString("AutoDownloadMaxVideoSize", R.string.AutoDownloadMaxVideoSize));
        this.mScPreload.setChecked(this.mblnChecked, true);
        this.mTvTip.setText(LocaleController.formatString("AutoDownloadPreloadVideoInfo", R.string.AutoDownloadPreloadVideoInfo, AndroidUtilities.formatFileSize(this.mSize)));
        boolean hasAny = false;
        int b = 0;
        while (true) {
            if (b >= this.mArrList.size()) {
                break;
            } else if (this.mArrList.get(b).booleanValue()) {
                hasAny = true;
                break;
            } else {
                b++;
            }
        }
        if (!hasAny) {
            this.sizeCell[0].setEnabled(hasAny, (ArrayList<Animator>) null);
            preLoadEnabled(hasAny);
        }
        if (this.mSize <= 2097152) {
            preLoadEnabled(false);
        }
        this.fragmentView.findViewById(R.id.rl_contact).setBackground(Theme.getRoundRectSelectorDrawable(AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), 0, 0, Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.fragmentView.findViewById(R.id.rl_private_chat).setBackground(Theme.getSelectorDrawable(true));
        this.fragmentView.findViewById(R.id.rl_group_chat).setBackground(Theme.getSelectorDrawable(true));
        this.fragmentView.findViewById(R.id.rl_channel).setBackground(Theme.getRoundRectSelectorDrawable(0, 0, AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.fragmentView.findViewById(R.id.rl_preload).setBackground(Theme.getRoundRectSelectorDrawable(0, 0, AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
    }

    private void initListener() {
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    VideoAutoDownloadSettingActivity.this.finishFragment();
                } else if (id == 1) {
                    if (VideoAutoDownloadSettingActivity.this.mListener != null) {
                        VideoAutoDownloadSettingActivity.this.mListener.onSaveBtnClick(VideoAutoDownloadSettingActivity.this.mArrList, VideoAutoDownloadSettingActivity.this.sizeCell[0].getSize(), VideoAutoDownloadSettingActivity.this.mScPreload.isChecked());
                    }
                    VideoAutoDownloadSettingActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView.findViewById(R.id.rl_contact).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                VideoAutoDownloadSettingActivity.this.mswitch_contact.setChecked(!VideoAutoDownloadSettingActivity.this.mswitch_contact.isChecked(), true);
                VideoAutoDownloadSettingActivity.this.mArrList.set(0, Boolean.valueOf(VideoAutoDownloadSettingActivity.this.mswitch_contact.isChecked()));
                VideoAutoDownloadSettingActivity.this.processUpdate();
            }
        });
        this.fragmentView.findViewById(R.id.rl_private_chat).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                VideoAutoDownloadSettingActivity.this.mswitch_private_chat.setChecked(!VideoAutoDownloadSettingActivity.this.mswitch_private_chat.isChecked(), true);
                VideoAutoDownloadSettingActivity.this.mArrList.set(1, Boolean.valueOf(VideoAutoDownloadSettingActivity.this.mswitch_private_chat.isChecked()));
                VideoAutoDownloadSettingActivity.this.processUpdate();
            }
        });
        this.fragmentView.findViewById(R.id.rl_group_chat).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                VideoAutoDownloadSettingActivity.this.mswitch_group_chat.setChecked(!VideoAutoDownloadSettingActivity.this.mswitch_group_chat.isChecked(), true);
                VideoAutoDownloadSettingActivity.this.mArrList.set(2, Boolean.valueOf(VideoAutoDownloadSettingActivity.this.mswitch_group_chat.isChecked()));
                VideoAutoDownloadSettingActivity.this.processUpdate();
            }
        });
        this.fragmentView.findViewById(R.id.rl_channel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                VideoAutoDownloadSettingActivity.this.mswitch_channel.setChecked(!VideoAutoDownloadSettingActivity.this.mswitch_channel.isChecked(), true);
                VideoAutoDownloadSettingActivity.this.mArrList.set(3, Boolean.valueOf(VideoAutoDownloadSettingActivity.this.mswitch_channel.isChecked()));
                VideoAutoDownloadSettingActivity.this.processUpdate();
            }
        });
        this.fragmentView.findViewById(R.id.rl_preload).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                VideoAutoDownloadSettingActivity.this.mScPreload.setChecked(!VideoAutoDownloadSettingActivity.this.mScPreload.isChecked(), true);
            }
        });
    }

    /* access modifiers changed from: private */
    public void processUpdate() {
        boolean hasAny = false;
        int b = 0;
        while (true) {
            if (b >= this.mArrList.size()) {
                break;
            } else if (this.mArrList.get(b).booleanValue()) {
                hasAny = true;
                break;
            } else {
                b++;
            }
        }
        if (this.sizeCell[0].isEnabled() != hasAny) {
            this.sizeCell[0].setEnabled(hasAny, new ArrayList<>());
            if (this.sizeCell[0].getSize() > 2097152) {
                preLoadEnabled(hasAny);
            }
        }
    }

    public void preLoadEnabled(boolean value) {
        float f = 1.0f;
        this.mTvPreload.setAlpha(value ? 1.0f : 0.5f);
        MrySwitch mrySwitch = this.mScPreload;
        if (!value) {
            f = 0.5f;
        }
        mrySwitch.setAlpha(f);
        this.fragmentView.findViewById(R.id.rl_preload).setEnabled(value);
    }
}
