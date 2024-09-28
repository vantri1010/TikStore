package im.bclpbkiauv.ui.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.StatsController;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;

public class DataUseStatisticsActivity extends BaseFragment implements View.OnClickListener {
    private ImageView mIvBack;
    private RelativeLayout mRlBack;
    private RelativeLayout mRlReset;
    private TextView mtvCAllSent;
    private TextView mtvCallReceive;
    private TextView mtvCurrent;
    private TextView mtvFileReceive;
    private TextView mtvFileSent;
    private TextView mtvMobile;
    private TextView mtvMsgReceive;
    private TextView mtvMsgSent;
    private TextView mtvPhotoReceive;
    private TextView mtvPhotoSent;
    private TextView mtvVideoReceive;
    private TextView mtvVideoSent;
    private TextView mtvWifi;
    private LinearLayout tabContainer;

    public View createView(Context context) {
        this.actionBar.setAddToContainer(false);
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_setting_data_use_statistics, (ViewGroup) null, false);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        initTitleBar();
        initView(context);
        return this.fragmentView;
    }

    private void initTitleBar() {
        FrameLayout flTitleBarContainer = (FrameLayout) this.fragmentView.findViewById(R.id.fl_title_bar_container);
        flTitleBarContainer.setBackground(this.defaultActionBarBackgroundDrawable);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) flTitleBarContainer.getLayoutParams();
        layoutParams.height = ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight;
        flTitleBarContainer.setLayoutParams(layoutParams);
        flTitleBarContainer.setPadding(0, AndroidUtilities.statusBarHeight, 0, 0);
        this.tabContainer = (LinearLayout) this.fragmentView.findViewById(R.id.tabContainer);
        this.mRlBack = (RelativeLayout) this.fragmentView.findViewById(R.id.rl_back);
        ImageView imageView = (ImageView) this.fragmentView.findViewById(R.id.iv_back);
        this.mIvBack = imageView;
        imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarDefaultIcon), PorterDuff.Mode.MULTIPLY));
        this.mIvBack.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_actionBarDefaultSelector)));
        this.mtvMobile = (TextView) this.fragmentView.findViewById(R.id.tv_moblie);
        this.mtvWifi = (TextView) this.fragmentView.findViewById(R.id.tv_wifi);
        this.mtvCurrent = this.mtvMobile;
    }

    private void initView(Context context) {
        this.mtvMsgSent = (TextView) this.fragmentView.findViewById(R.id.tv_msg_sent);
        this.mtvMsgReceive = (TextView) this.fragmentView.findViewById(R.id.tv_msg_receive);
        this.mtvPhotoSent = (TextView) this.fragmentView.findViewById(R.id.tv_photo_sent);
        this.mtvPhotoReceive = (TextView) this.fragmentView.findViewById(R.id.tv_photo_receive);
        this.mtvVideoSent = (TextView) this.fragmentView.findViewById(R.id.tv_video_sent);
        this.mtvVideoReceive = (TextView) this.fragmentView.findViewById(R.id.tv_video_receive);
        this.mtvFileSent = (TextView) this.fragmentView.findViewById(R.id.tv_file_sent);
        this.mtvFileReceive = (TextView) this.fragmentView.findViewById(R.id.tv_file_receive);
        this.mtvCAllSent = (TextView) this.fragmentView.findViewById(R.id.tv_call_sent);
        this.mtvCallReceive = (TextView) this.fragmentView.findViewById(R.id.tv_call_receive);
        this.mRlReset = (RelativeLayout) this.fragmentView.findViewById(R.id.rl_reset_download_file);
        Drawable topRroundDrawable = Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f, Theme.getColor(Theme.key_windowBackgroundWhite));
        Drawable bottomRoundDrawable = Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite));
        this.fragmentView.findViewById(R.id.rl_store_number).setBackground(topRroundDrawable);
        this.fragmentView.findViewById(R.id.rl_network_number).setBackground(bottomRoundDrawable);
        this.fragmentView.findViewById(R.id.rl_photo_send).setBackground(topRroundDrawable);
        this.fragmentView.findViewById(R.id.rl_photo_recv).setBackground(bottomRoundDrawable);
        this.fragmentView.findViewById(R.id.rl_video_send).setBackground(topRroundDrawable);
        this.fragmentView.findViewById(R.id.rl_video_recv).setBackground(bottomRoundDrawable);
        this.fragmentView.findViewById(R.id.rl_file_send).setBackground(topRroundDrawable);
        this.fragmentView.findViewById(R.id.rl_file_recv).setBackground(bottomRoundDrawable);
        this.fragmentView.findViewById(R.id.rl_call_send).setBackground(topRroundDrawable);
        this.fragmentView.findViewById(R.id.rl_call_recv).setBackground(bottomRoundDrawable);
        this.fragmentView.findViewById(R.id.rl_reset_download_file).setBackground(Theme.getRoundRectSelectorDrawable(AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        initListener();
        changeTabState(this.mtvCurrent);
    }

    private void initListener() {
        this.mtvMobile.setOnClickListener(this);
        this.mtvWifi.setOnClickListener(this);
        this.mRlBack.setOnClickListener(this);
        this.mRlReset.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back /*2131297147*/:
                finishFragment();
                return;
            case R.id.rl_reset_download_file /*2131297201*/:
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setTitle(LocaleController.getString("ResetStatisticsAlertTitle", R.string.ResetStatisticsAlertTitle));
                builder.setMessage(LocaleController.getString("ResetStatisticsAlert", R.string.ResetStatisticsAlert));
                builder.setPositiveButton(LocaleController.getString("Reset", R.string.Reset), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        DataUseStatisticsActivity.this.lambda$onClick$0$DataUseStatisticsActivity(dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                AlertDialog dialog = builder.create();
                showDialog(dialog);
                TextView button = (TextView) dialog.getButton(-1);
                if (button != null) {
                    button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
                    return;
                }
                return;
            case R.id.tv_moblie /*2131297787*/:
                if (this.mtvCurrent.getId() != v.getId()) {
                    changeTabState(v);
                    this.mtvCurrent = this.mtvMobile;
                    initState();
                    return;
                }
                return;
            case R.id.tv_wifi /*2131297878*/:
                if (this.mtvCurrent.getId() != v.getId()) {
                    changeTabState(v);
                    this.mtvCurrent = this.mtvWifi;
                    initState();
                    return;
                }
                return;
            default:
                return;
        }
    }

    public /* synthetic */ void lambda$onClick$0$DataUseStatisticsActivity(DialogInterface dialogInterface, int i) {
        int type = 0;
        if (this.mtvCurrent == this.mtvWifi) {
            type = 1;
        }
        StatsController.getInstance(this.currentAccount).resetStats(type);
        initState();
    }

    private void changeTabState(View view) {
        if (view.getId() == this.mtvMobile.getId()) {
            if (this.tabContainer.getBackground() != null) {
                this.tabContainer.getBackground().setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText), PorterDuff.Mode.SRC_IN));
            }
            this.mtvMobile.setTextColor(-1);
            this.mtvMobile.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), 0.0f, (float) AndroidUtilities.dp(5.0f), 0.0f, Theme.getColor(Theme.key_windowBackgroundWhiteBlueText)));
            this.mtvWifi.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
            this.mtvWifi.setBackground(Theme.createRoundRectDrawable(0.0f, (float) AndroidUtilities.dp(5.0f), 0.0f, (float) AndroidUtilities.dp(5.0f), 0));
            return;
        }
        if (this.tabContainer.getBackground() != null) {
            this.tabContainer.getBackground().setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText), PorterDuff.Mode.SRC_IN));
        }
        this.mtvWifi.setTextColor(-1);
        this.mtvWifi.setBackground(Theme.createRoundRectDrawable(0.0f, (float) AndroidUtilities.dp(5.0f), 0.0f, (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhiteBlueText)));
        this.mtvMobile.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
        this.mtvMobile.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), 0.0f, (float) AndroidUtilities.dp(5.0f), 0.0f, 0));
    }

    public void onResume() {
        super.onResume();
        initState();
    }

    private void initState() {
        int type = 0;
        if (this.mtvCurrent == this.mtvWifi) {
            type = 1;
        }
        this.mtvMsgSent.setText(AndroidUtilities.formatFileSize(StatsController.getInstance(this.currentAccount).getSentBytesCount(type, 1)));
        this.mtvMsgReceive.setText(AndroidUtilities.formatFileSize(StatsController.getInstance(this.currentAccount).getReceivedBytesCount(type, 1)));
        this.mtvPhotoSent.setText(AndroidUtilities.formatFileSize(StatsController.getInstance(this.currentAccount).getSentBytesCount(type, 4)));
        this.mtvPhotoReceive.setText(AndroidUtilities.formatFileSize(StatsController.getInstance(this.currentAccount).getReceivedBytesCount(type, 4)));
        this.mtvVideoSent.setText(AndroidUtilities.formatFileSize(StatsController.getInstance(this.currentAccount).getSentBytesCount(type, 2)));
        this.mtvVideoReceive.setText(AndroidUtilities.formatFileSize(StatsController.getInstance(this.currentAccount).getReceivedBytesCount(type, 2)));
        this.mtvFileSent.setText(AndroidUtilities.formatFileSize(StatsController.getInstance(this.currentAccount).getSentBytesCount(type, 5)));
        this.mtvFileReceive.setText(AndroidUtilities.formatFileSize(StatsController.getInstance(this.currentAccount).getReceivedBytesCount(type, 5)));
        this.mtvCAllSent.setText(AndroidUtilities.formatFileSize(StatsController.getInstance(this.currentAccount).getSentBytesCount(type, 0)));
        this.mtvCallReceive.setText(AndroidUtilities.formatFileSize(StatsController.getInstance(this.currentAccount).getReceivedBytesCount(type, 0)));
    }
}
