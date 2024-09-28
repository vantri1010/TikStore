package im.bclpbkiauv.ui.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.content.FileProvider;
import com.blankj.utilcode.util.AppUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.browser.Browser;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.LaunchActivity;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hviews.MryRoundButton;
import im.bclpbkiauv.ui.utils.AppUpdater;
import im.bclpbkiauv.ui.utils.DownloadUtils;
import java.io.File;
import java.util.Locale;

public class BlockingUpdateView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private FrameLayout acceptButton;
    /* access modifiers changed from: private */
    public MryRoundButton acceptTextView;
    /* access modifiers changed from: private */
    public int accountNum;
    private TLRPC.TL_help_appUpdate appUpdate;
    private String fileName;
    private int pressCount;
    /* access modifiers changed from: private */
    public AnimatorSet progressAnimation;
    /* access modifiers changed from: private */
    public RadialProgress radialProgress;
    /* access modifiers changed from: private */
    public FrameLayout radialProgressView;
    private TextView textView;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public BlockingUpdateView(Context context) {
        super(context);
        Context context2 = context;
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        int top = Build.VERSION.SDK_INT >= 21 ? (int) (((float) AndroidUtilities.statusBarHeight) / AndroidUtilities.density) : 0;
        FrameLayout view = new FrameLayout(context2);
        view.setBackgroundColor(-11556378);
        addView(view, new FrameLayout.LayoutParams(-1, AndroidUtilities.dp(176.0f) + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)));
        ImageView imageView = new ImageView(context2);
        imageView.setImageResource(R.mipmap.ic_logo);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setPadding(0, 0, 0, AndroidUtilities.dp(14.0f));
        view.addView(imageView, LayoutHelper.createFrame(-2.0f, -2.0f, 17, 0.0f, (float) top, 0.0f, 0.0f));
        imageView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                BlockingUpdateView.this.lambda$new$0$BlockingUpdateView(view);
            }
        });
        ScrollView scrollView = new ScrollView(context2);
        AndroidUtilities.setScrollViewEdgeEffectColor(scrollView, Theme.getColor(Theme.key_actionBarDefault));
        addView(scrollView, LayoutHelper.createFrame(-1.0f, -1.0f, 51, 27.0f, (float) (top + 206), 27.0f, 130.0f));
        FrameLayout container = new FrameLayout(context2);
        scrollView.addView(container, LayoutHelper.createScroll(-1, -2, 17));
        TextView titleTextView = new TextView(context2);
        titleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        titleTextView.setTextSize(1, 20.0f);
        titleTextView.setGravity(49);
        titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        titleTextView.setText(LocaleController.getString("UpdateApp", R.string.UpdateApp));
        container.addView(titleTextView, LayoutHelper.createFrame(-2, -2, 49));
        TextView textView2 = new TextView(context2);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText));
        this.textView.setTextSize(1, 15.0f);
        this.textView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        this.textView.setGravity(49);
        this.textView.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        container.addView(this.textView, LayoutHelper.createFrame(-2.0f, -2.0f, 51, 0.0f, 44.0f, 0.0f, 0.0f));
        this.acceptButton = new FrameLayout(context2);
        if (Build.VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.acceptButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            animator.addState(new int[0], ObjectAnimator.ofFloat(this.acceptButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.acceptButton.setStateListAnimator(animator);
        }
        this.acceptButton.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        addView(this.acceptButton, LayoutHelper.createFrame(-1.0f, 45.0f, 81, 60.0f, 0.0f, 60.0f, 45.0f));
        MryRoundButton mryRoundButton = new MryRoundButton(context2);
        this.acceptTextView = mryRoundButton;
        mryRoundButton.setPrimaryRadiusAdjustBoundsFillStyle();
        this.acceptTextView.setBackgroundColor(-14904113);
        this.acceptTextView.setTextSize(16.0f);
        this.acceptTextView.setBold();
        this.acceptTextView.setOnClickListener(new View.OnClickListener(context2) {
            private final /* synthetic */ Context f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                BlockingUpdateView.this.lambda$new$1$BlockingUpdateView(this.f$1, view);
            }
        });
        this.acceptButton.addView(this.acceptTextView, LayoutHelper.createFrame(-1, 45, 17));
        AnonymousClass1 r3 = new FrameLayout(context2) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                int w = AndroidUtilities.dp(36.0f);
                int l = ((right - left) - w) / 2;
                int t = ((bottom - top) - w) / 2;
                BlockingUpdateView.this.radialProgress.setProgressRect(l, t, l + w, t + w);
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                BlockingUpdateView.this.radialProgress.draw(canvas);
            }
        };
        this.radialProgressView = r3;
        r3.setWillNotDraw(false);
        this.radialProgressView.setAlpha(0.0f);
        this.radialProgressView.setScaleX(0.1f);
        this.radialProgressView.setScaleY(0.1f);
        this.radialProgressView.setVisibility(4);
        RadialProgress radialProgress2 = new RadialProgress(this.radialProgressView);
        this.radialProgress = radialProgress2;
        radialProgress2.setBackground((Drawable) null, true, false);
        this.radialProgress.setProgressColor(-1);
        this.acceptButton.addView(this.radialProgressView, LayoutHelper.createFrame(36, 36, 17));
    }

    public /* synthetic */ void lambda$new$0$BlockingUpdateView(View v) {
        int i = this.pressCount + 1;
        this.pressCount = i;
        if (i >= 10) {
            setVisibility(8);
            AppUpdater.pendingAppUpdate = null;
            AppUpdater.getInstance(this.accountNum).lambda$loadUpdateConfig$2$AppUpdater();
        }
    }

    public /* synthetic */ void lambda$new$1$BlockingUpdateView(Context context, View view1) {
        if (TextUtils.isEmpty(BuildVars.PLAYSTORE_APP_URL) && !checkApkInstallPermissions(getContext())) {
            return;
        }
        if (TextUtils.isEmpty(BuildVars.PLAYSTORE_APP_URL) && Build.VERSION.SDK_INT >= 23 && context.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            ((LaunchActivity) context).requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
        } else if (this.appUpdate.document instanceof TLRPC.TL_document) {
            if (!openApkInstall((Activity) getContext(), this.appUpdate.document)) {
                FileLoader.getInstance(this.accountNum).loadFile(this.appUpdate.document, "update", 2, 1);
                showProgress(true);
            }
        } else if (this.appUpdate.url == null) {
        } else {
            if (TextUtils.isEmpty(BuildVars.PLAYSTORE_APP_URL)) {
                DownloadUtils.getInstance(getContext()).startDownload(this.appUpdate.url, this.appUpdate.version);
            } else if (AppUtils.isAppInstalled("com.android.vending")) {
                Browser.openUrl(context, BuildVars.PLAYSTORE_APP_URL);
            } else {
                ToastUtils.show((int) R.string.InstallGooglePlayTips);
            }
        }
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == 8) {
            NotificationCenter.getInstance(this.accountNum).removeObserver(this, NotificationCenter.fileDidLoad);
            NotificationCenter.getInstance(this.accountNum).removeObserver(this, NotificationCenter.fileDidFailToLoad);
            NotificationCenter.getInstance(this.accountNum).removeObserver(this, NotificationCenter.FileLoadProgressChanged);
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.fileDidLoad) {
            String location = args[0];
            String str = this.fileName;
            if (str != null && str.equals(location)) {
                showProgress(false);
                openApkInstall((Activity) getContext(), this.appUpdate.document);
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
                this.radialProgress.setProgress(args[1].floatValue(), true);
            }
        }
    }

    public static boolean checkApkInstallPermissions(Context context) {
        if (Build.VERSION.SDK_INT < 26 || ApplicationLoader.applicationContext.getPackageManager().canRequestPackageInstalls()) {
            return true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setMessage(LocaleController.getString("ApkRestricted", R.string.ApkRestricted));
        builder.setPositiveButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new DialogInterface.OnClickListener(context) {
            private final /* synthetic */ Context f$0;

            {
                this.f$0 = r1;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                BlockingUpdateView.lambda$checkApkInstallPermissions$2(this.f$0, dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        builder.show();
        return false;
    }

    static /* synthetic */ void lambda$checkApkInstallPermissions$2(Context context, DialogInterface dialogInterface, int i) {
        try {
            context.startActivity(new Intent("android.settings.MANAGE_UNKNOWN_APP_SOURCES", Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName())));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static boolean openApkInstall(Activity activity, TLRPC.Document document) {
        boolean exists = false;
        try {
            String attachFileName = FileLoader.getAttachFileName(document);
            File f = FileLoader.getPathToAttach(document, true);
            boolean exists2 = f.exists();
            exists = exists2;
            if (exists2) {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setFlags(1);
                if (Build.VERSION.SDK_INT >= 24) {
                    intent.setDataAndType(FileProvider.getUriForFile(activity, "im.bclpbkiauv.messenger.provider", f), "application/vnd.android.package-archive");
                } else {
                    intent.setDataAndType(Uri.fromFile(f), "application/vnd.android.package-archive");
                }
                try {
                    activity.startActivityForResult(intent, 500);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
        return exists;
    }

    private void showProgress(boolean show) {
        final boolean z = show;
        AnimatorSet animatorSet = this.progressAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.progressAnimation = new AnimatorSet();
        if (z) {
            this.radialProgressView.setVisibility(0);
            this.acceptButton.setEnabled(false);
            this.progressAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.acceptTextView, "scaleX", new float[]{0.1f}), ObjectAnimator.ofFloat(this.acceptTextView, "scaleY", new float[]{0.1f}), ObjectAnimator.ofFloat(this.acceptTextView, "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.radialProgressView, "scaleX", new float[]{1.0f}), ObjectAnimator.ofFloat(this.radialProgressView, "scaleY", new float[]{1.0f}), ObjectAnimator.ofFloat(this.radialProgressView, "alpha", new float[]{1.0f})});
        } else {
            this.acceptTextView.setVisibility(0);
            this.acceptButton.setEnabled(true);
            this.progressAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.radialProgressView, "scaleX", new float[]{0.1f}), ObjectAnimator.ofFloat(this.radialProgressView, "scaleY", new float[]{0.1f}), ObjectAnimator.ofFloat(this.radialProgressView, "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.acceptTextView, "scaleX", new float[]{1.0f}), ObjectAnimator.ofFloat(this.acceptTextView, "scaleY", new float[]{1.0f}), ObjectAnimator.ofFloat(this.acceptTextView, "alpha", new float[]{1.0f})});
        }
        this.progressAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (BlockingUpdateView.this.progressAnimation != null && BlockingUpdateView.this.progressAnimation.equals(animation)) {
                    if (!z) {
                        BlockingUpdateView.this.radialProgressView.setVisibility(4);
                    } else {
                        BlockingUpdateView.this.acceptTextView.setVisibility(4);
                    }
                }
            }

            public void onAnimationCancel(Animator animation) {
                if (BlockingUpdateView.this.progressAnimation != null && BlockingUpdateView.this.progressAnimation.equals(animation)) {
                    AnimatorSet unused = BlockingUpdateView.this.progressAnimation = null;
                }
            }
        });
        this.progressAnimation.setDuration(150);
        this.progressAnimation.start();
    }

    public void show(int account, TLRPC.TL_help_appUpdate update, boolean check) {
        this.pressCount = 0;
        this.appUpdate = update;
        this.accountNum = account;
        if (update.document instanceof TLRPC.TL_document) {
            this.fileName = FileLoader.getAttachFileName(update.document);
        }
        if (getVisibility() != 0) {
            setVisibility(0);
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(update.text);
        MessageObject.addEntitiesToText(builder, update.entities, false, 0, false, false, false);
        this.textView.setText(builder);
        if (update.document instanceof TLRPC.TL_document) {
            MryRoundButton mryRoundButton = this.acceptTextView;
            mryRoundButton.setText(LocaleController.getString("Update", R.string.Update).toUpperCase() + String.format(Locale.US, " (%1$s)", new Object[]{AndroidUtilities.formatFileSize((long) update.document.size)}));
        } else {
            this.acceptTextView.setText(LocaleController.getString("Update", R.string.Update).toUpperCase());
        }
        NotificationCenter.getInstance(this.accountNum).addObserver(this, NotificationCenter.fileDidLoad);
        NotificationCenter.getInstance(this.accountNum).addObserver(this, NotificationCenter.fileDidFailToLoad);
        NotificationCenter.getInstance(this.accountNum).addObserver(this, NotificationCenter.FileLoadProgressChanged);
        if (check) {
            AppUpdater.getInstance(this.accountNum).checkAppUpdate(new AppUpdater.OnForceUpdateCallback() {
                public void onForce(TLRPC.TL_help_appUpdate res) {
                }

                public void onNormal(TLRPC.TL_help_appUpdate res) {
                    BlockingUpdateView.this.setVisibility(8);
                    AppUpdater.pendingAppUpdate = null;
                    AppUpdater.getInstance(BlockingUpdateView.this.accountNum).lambda$loadUpdateConfig$2$AppUpdater();
                }

                public void onNoUpdate() {
                }
            }, true);
        }
    }
}
