package im.bclpbkiauv.ui.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.blankj.utilcode.util.AppUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.browser.Browser;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hviews.MryTextView;
import im.bclpbkiauv.ui.utils.DownloadUtils;

public class UpdateAppAlertDialog extends Dialog implements NotificationCenter.NotificationCenterDelegate {
    private int accountNum;
    /* access modifiers changed from: private */
    public TLRPC.TL_help_appUpdate appUpdate;
    /* access modifiers changed from: private */
    public MryTextView btnDownload;
    private MryTextView btnLeft;
    private MryTextView btnRight;
    /* access modifiers changed from: private */
    public View container;
    /* access modifiers changed from: private */
    public LinearLayout containerBottom;
    /* access modifiers changed from: private */
    public View containerDownloadApp;
    /* access modifiers changed from: private */
    public View containerScrollView;
    /* access modifiers changed from: private */
    public View dividerHorzontial;
    private String fileName;
    /* access modifiers changed from: private */
    public ImageView iv;
    private boolean mIsShowProgress;
    private Activity parentActivity;
    /* access modifiers changed from: private */
    public AnimatorSet progressAnimation;
    /* access modifiers changed from: private */
    public ProgressBar progressBar;
    private TextView tvContent;
    private MryTextView tvDownloadTips;
    /* access modifiers changed from: private */
    public MryTextView tvPercent;
    /* access modifiers changed from: private */
    public MryTextView tvSize;
    private MryTextView tvTitle;

    public UpdateAppAlertDialog(Activity activity, TLRPC.TL_help_appUpdate update, int account) {
        super(activity, 0);
        this.appUpdate = update;
        this.accountNum = account;
        if (update.document instanceof TLRPC.TL_document) {
            this.fileName = FileLoader.getAttachFileName(update.document);
        }
        this.parentActivity = activity;
        init(activity);
    }

    private void init(Context context) {
        String contextText;
        String contextText2;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_app_update, (ViewGroup) null, false);
        setContentView(view, new ViewGroup.LayoutParams(-1, -2));
        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable());
        window.setGravity(17);
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = (display.getWidth() / 4) * 3;
        window.setAttributes(lp);
        this.container = view.findViewById(R.id.container);
        view.setFitsSystemWindows(Build.VERSION.SDK_INT >= 21);
        this.container.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(15.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.containerScrollView = view.findViewById(R.id.containerScrollView);
        this.containerDownloadApp = view.findViewById(R.id.containerDownloadApp);
        this.dividerHorzontial = view.findViewById(R.id.dividerHorzontial);
        this.iv = (ImageView) view.findViewById(R.id.iv);
        this.tvTitle = (MryTextView) view.findViewById(R.id.tvTitle);
        this.tvContent = (TextView) view.findViewById(R.id.tvContent);
        this.containerBottom = (LinearLayout) view.findViewById(R.id.containerBottom);
        this.btnLeft = (MryTextView) view.findViewById(R.id.btnLeft);
        this.btnRight = (MryTextView) view.findViewById(R.id.btnRight);
        this.tvPercent = (MryTextView) view.findViewById(R.id.tvPercent);
        this.tvSize = (MryTextView) view.findViewById(R.id.tvSize);
        this.progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        this.tvDownloadTips = (MryTextView) view.findViewById(R.id.tvDownloadTips);
        this.btnDownload = (MryTextView) view.findViewById(R.id.btnDownload);
        view.findViewById(R.id.divider).setBackgroundColor(Theme.getColor(Theme.key_divider));
        this.tvTitle.setTextColor(Theme.key_windowBackgroundWhiteBlackText);
        this.tvContent.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        this.tvContent.setLinkTextColor(Theme.getColor(Theme.key_dialogTextLink));
        this.btnLeft.setTextColor(Theme.key_windowBackgroundWhiteGrayText5);
        this.btnRight.setTextColor(Theme.key_windowBackgroundWhiteBlueText);
        this.tvDownloadTips.setTextColor(Theme.key_windowBackgroundWhiteGrayText5);
        this.btnDownload.setTextColor(Theme.key_windowBackgroundWhiteGrayText5);
        this.tvPercent.setTextColor(Theme.key_windowBackgroundWhiteGrayText5);
        this.tvSize.setTextColor(Theme.key_windowBackgroundWhiteGrayText5);
        this.btnDownload.setMryText(R.string.BackgroundDownload);
        this.tvContent.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        GradientDrawable p = new GradientDrawable();
        p.setCornerRadius((float) AndroidUtilities.dp(5.0f));
        p.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueButton));
        ClipDrawable progress = new ClipDrawable(p, 3, 1);
        GradientDrawable background = new GradientDrawable();
        background.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayLine));
        background.setCornerRadius((float) AndroidUtilities.dp(5.0f));
        this.progressBar.setProgressDrawable(new LayerDrawable(new Drawable[]{background, progress}));
        if (Build.VERSION.SDK_INT >= 21) {
            this.progressBar.setProgressBackgroundTintList(ColorStateList.valueOf(Theme.getColor(Theme.key_windowBackgroundGray)));
        }
        if (this.appUpdate != null) {
            MryTextView mryTextView = this.tvTitle;
            mryTextView.setText(LocaleController.getString(R.string.NewVersionFound) + " V" + this.appUpdate.version);
            if (this.appUpdate.can_not_skip) {
                setCanceledOnTouchOutside(false);
                setCancelable(false);
                if (TextUtils.isEmpty(this.appUpdate.text)) {
                    contextText = LocaleController.getString(R.string.ForceUpdateTips);
                } else {
                    contextText = this.appUpdate.text;
                }
                this.iv.setVisibility(8);
                this.btnDownload.setVisibility(8);
                this.btnDownload.setEnabled(false);
            } else {
                setCanceledOnTouchOutside(true);
                setCancelable(true);
                setOnDismissListener($$Lambda$UpdateAppAlertDialog$NO1jHDQfbwNnvtlbLayqMUoHL0.INSTANCE);
                if (TextUtils.isEmpty(this.appUpdate.text)) {
                    contextText2 = LocaleController.getString(R.string.ForceNotUpdateDefaultTips);
                } else {
                    contextText2 = this.appUpdate.text;
                }
                this.iv.setVisibility(0);
                this.btnDownload.setEnabled(true);
            }
            this.tvContent.setText(contextText);
            this.btnLeft.setMryText(this.appUpdate.can_not_skip ? R.string.Exit : R.string.UpdateNextTimeCaps);
            this.btnLeft.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    UpdateAppAlertDialog.this.lambda$init$1$UpdateAppAlertDialog(view);
                }
            });
            this.btnRight.setMryText(R.string.UpdateImmediatelyCaps);
            this.btnRight.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    UpdateAppAlertDialog.this.lambda$init$2$UpdateAppAlertDialog(view);
                }
            });
            this.btnDownload.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    UpdateAppAlertDialog.this.lambda$init$3$UpdateAppAlertDialog(view);
                }
            });
        }
    }

    public /* synthetic */ void lambda$init$1$UpdateAppAlertDialog(View v) {
        if (this.appUpdate.can_not_skip) {
            Activity activity = this.parentActivity;
            if (activity != null) {
                activity.finish();
            }
        } else if (this.appUpdate.document instanceof TLRPC.TL_document) {
            FileLoader.getInstance(this.accountNum).cancelLoadFile(this.appUpdate.document);
        }
        dismiss();
    }

    public /* synthetic */ void lambda$init$2$UpdateAppAlertDialog(View v) {
        if (TextUtils.isEmpty(BuildVars.PLAYSTORE_APP_URL) && !BlockingUpdateView.checkApkInstallPermissions(getContext())) {
            return;
        }
        if (TextUtils.isEmpty(BuildVars.PLAYSTORE_APP_URL) && Build.VERSION.SDK_INT >= 23 && this.parentActivity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            this.parentActivity.requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
        } else if (this.appUpdate.document instanceof TLRPC.TL_document) {
            if (!BlockingUpdateView.openApkInstall(this.parentActivity, this.appUpdate.document)) {
                FileLoader.getInstance(this.accountNum).loadFile(this.appUpdate.document, "update", 1, 1);
                showProgress(true);
            }
        } else if (this.appUpdate.url == null) {
        } else {
            if (TextUtils.isEmpty(BuildVars.PLAYSTORE_APP_URL)) {
                DownloadUtils.getInstance(this.parentActivity).setDownloadListener(new DownloadUtils.DownloadListener() {
                    public void onStart() {
                        UpdateAppAlertDialog.this.showProgress(true);
                    }

                    public void onProgress(int percent, long soFarSize, long totalSize) {
                        if (UpdateAppAlertDialog.this.progressBar != null) {
                            UpdateAppAlertDialog.this.progressBar.setProgress(percent);
                        }
                        if (UpdateAppAlertDialog.this.tvPercent != null) {
                            MryTextView access$1100 = UpdateAppAlertDialog.this.tvPercent;
                            access$1100.setText(percent + "%");
                        }
                        if (UpdateAppAlertDialog.this.tvSize != null) {
                            MryTextView access$1200 = UpdateAppAlertDialog.this.tvSize;
                            access$1200.setText(soFarSize + "KB / " + totalSize + "KB");
                        }
                    }

                    public void onFinish(String fileFullPath, long totalSize) {
                        UpdateAppAlertDialog.this.dismiss();
                    }

                    public void onFailed() {
                    }
                }).startDownload(this.appUpdate.url, this.appUpdate.version);
            } else if (AppUtils.isAppInstalled("com.android.vending")) {
                Browser.openUrl((Context) this.parentActivity, BuildVars.PLAYSTORE_APP_URL);
            } else {
                ToastUtils.show((int) R.string.InstallGooglePlayTips);
            }
        }
    }

    public /* synthetic */ void lambda$init$3$UpdateAppAlertDialog(View v) {
        ToastUtils.show((int) R.string.AlreadyBackgroundDownloading);
        dismiss();
    }

    public void show() {
        super.show();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.fileDidLoad) {
            String location = args[0];
            String str = this.fileName;
            if (str != null && str.equals(location)) {
                showProgress(false);
                BlockingUpdateView.openApkInstall(this.parentActivity, this.appUpdate.document);
            }
        } else if (id == NotificationCenter.fileDidFailToLoad) {
            String location2 = args[0];
            String str2 = this.fileName;
            if (str2 != null && str2.equals(location2)) {
                showProgress(false);
            }
        } else if (id == NotificationCenter.FileLoadProgressChanged) {
            String location3 = args[0];
            String str3 = this.fileName;
            if (str3 != null && str3.equals(location3)) {
                Float loadProgress = args[1];
                showProgress(true);
                ProgressBar progressBar2 = this.progressBar;
                if (progressBar2 != null) {
                    progressBar2.setProgress(loadProgress.intValue());
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationCenter.getInstance(this.accountNum).addObserver(this, NotificationCenter.fileDidLoad);
        NotificationCenter.getInstance(this.accountNum).addObserver(this, NotificationCenter.fileDidFailToLoad);
        NotificationCenter.getInstance(this.accountNum).addObserver(this, NotificationCenter.FileLoadProgressChanged);
    }

    public void dismiss() {
        super.dismiss();
        NotificationCenter.getInstance(this.accountNum).removeObserver(this, NotificationCenter.fileDidLoad);
        NotificationCenter.getInstance(this.accountNum).removeObserver(this, NotificationCenter.fileDidFailToLoad);
        NotificationCenter.getInstance(this.accountNum).removeObserver(this, NotificationCenter.FileLoadProgressChanged);
    }

    /* access modifiers changed from: private */
    public void showProgress(boolean show) {
        final boolean z = show;
        if (this.btnDownload != null && this.containerBottom != null && this.containerDownloadApp != null && this.containerScrollView != null && this.iv != null && this.mIsShowProgress != z) {
            this.mIsShowProgress = z;
            AnimatorSet animatorSet = this.progressAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.progressAnimation = new AnimatorSet();
            if (!z) {
                ProgressBar progressBar2 = this.progressBar;
                if (progressBar2 != null) {
                    progressBar2.setProgress(0);
                }
                TLRPC.TL_help_appUpdate tL_help_appUpdate = this.appUpdate;
                if (tL_help_appUpdate != null && !tL_help_appUpdate.can_not_skip) {
                    this.iv.setVisibility(0);
                }
                this.containerScrollView.setVisibility(0);
                this.containerBottom.setVisibility(0);
                this.btnDownload.setEnabled(false);
                this.progressAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.btnDownload, "scaleX", new float[]{0.1f}), ObjectAnimator.ofFloat(this.btnDownload, "scaleY", new float[]{0.1f}), ObjectAnimator.ofFloat(this.btnDownload, "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.containerDownloadApp, "scaleX", new float[]{0.1f}), ObjectAnimator.ofFloat(this.containerDownloadApp, "scaleY", new float[]{0.1f}), ObjectAnimator.ofFloat(this.containerDownloadApp, "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.iv, "scaleX", new float[]{1.0f}), ObjectAnimator.ofFloat(this.iv, "scaleY", new float[]{1.0f}), ObjectAnimator.ofFloat(this.iv, "alpha", new float[]{1.0f}), ObjectAnimator.ofFloat(this.containerBottom, "scaleX", new float[]{1.0f}), ObjectAnimator.ofFloat(this.containerBottom, "scaleY", new float[]{1.0f}), ObjectAnimator.ofFloat(this.containerBottom, "alpha", new float[]{1.0f}), ObjectAnimator.ofFloat(this.containerScrollView, "scaleX", new float[]{1.0f}), ObjectAnimator.ofFloat(this.containerScrollView, "scaleY", new float[]{1.0f}), ObjectAnimator.ofFloat(this.containerScrollView, "alpha", new float[]{1.0f})});
            } else {
                this.containerDownloadApp.setVisibility(0);
                TLRPC.TL_help_appUpdate tL_help_appUpdate2 = this.appUpdate;
                if (tL_help_appUpdate2 != null && !tL_help_appUpdate2.can_not_skip) {
                    this.btnDownload.setVisibility(0);
                    this.btnDownload.setEnabled(true);
                }
                this.btnDownload.setEnabled(true);
                this.progressAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.iv, "scaleX", new float[]{0.1f}), ObjectAnimator.ofFloat(this.iv, "scaleY", new float[]{0.1f}), ObjectAnimator.ofFloat(this.iv, "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.containerBottom, "scaleX", new float[]{0.1f}), ObjectAnimator.ofFloat(this.containerBottom, "scaleY", new float[]{0.1f}), ObjectAnimator.ofFloat(this.containerBottom, "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.containerScrollView, "scaleX", new float[]{0.1f}), ObjectAnimator.ofFloat(this.containerScrollView, "scaleY", new float[]{0.1f}), ObjectAnimator.ofFloat(this.containerScrollView, "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.btnDownload, "scaleX", new float[]{0.1f, 1.0f}), ObjectAnimator.ofFloat(this.btnDownload, "scaleY", new float[]{0.1f, 1.0f}), ObjectAnimator.ofFloat(this.btnDownload, "alpha", new float[]{0.1f, 1.0f}), ObjectAnimator.ofFloat(this.containerDownloadApp, "scaleX", new float[]{0.1f, 1.0f}), ObjectAnimator.ofFloat(this.containerDownloadApp, "scaleY", new float[]{0.1f, 1.0f}), ObjectAnimator.ofFloat(this.containerDownloadApp, "alpha", new float[]{0.1f, 1.0f})});
            }
            this.progressAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    UpdateAppAlertDialog.this.container.requestLayout();
                    if (UpdateAppAlertDialog.this.progressAnimation != null && UpdateAppAlertDialog.this.progressAnimation.equals(animation)) {
                        if (!z) {
                            if (UpdateAppAlertDialog.this.appUpdate != null && !UpdateAppAlertDialog.this.appUpdate.can_not_skip) {
                                UpdateAppAlertDialog.this.iv.setVisibility(0);
                            }
                            UpdateAppAlertDialog.this.containerScrollView.setVisibility(0);
                            UpdateAppAlertDialog.this.containerBottom.setVisibility(0);
                            UpdateAppAlertDialog.this.containerDownloadApp.setVisibility(8);
                            UpdateAppAlertDialog.this.btnDownload.setVisibility(8);
                            return;
                        }
                        UpdateAppAlertDialog.this.iv.setVisibility(8);
                        UpdateAppAlertDialog.this.containerScrollView.setVisibility(8);
                        UpdateAppAlertDialog.this.containerBottom.setVisibility(8);
                        UpdateAppAlertDialog.this.containerDownloadApp.setVisibility(0);
                        if (UpdateAppAlertDialog.this.appUpdate == null) {
                            return;
                        }
                        if (!UpdateAppAlertDialog.this.appUpdate.can_not_skip) {
                            UpdateAppAlertDialog.this.btnDownload.setVisibility(0);
                        } else {
                            UpdateAppAlertDialog.this.dividerHorzontial.setVisibility(8);
                        }
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (UpdateAppAlertDialog.this.progressAnimation != null && UpdateAppAlertDialog.this.progressAnimation.equals(animation)) {
                        AnimatorSet unused = UpdateAppAlertDialog.this.progressAnimation = null;
                    }
                }
            });
            this.progressAnimation.setDuration(150);
            this.progressAnimation.start();
        }
    }
}
