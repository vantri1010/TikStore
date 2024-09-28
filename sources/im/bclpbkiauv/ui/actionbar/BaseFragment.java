package im.bclpbkiauv.ui.actionbar;

import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.accessibility.AccessibilityManager;
import androidx.fragment.app.FragmentActivity;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import im.bclpbkiauv.messenger.AccountInstance;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.DownloadController;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocationController;
import im.bclpbkiauv.messenger.MediaController;
import im.bclpbkiauv.messenger.MediaDataController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.NotificationsController;
import im.bclpbkiauv.messenger.SecretChatHelper;
import im.bclpbkiauv.messenger.SendMessagesHelper;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.utils.status.StatusBarUtils;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.ui.wallet.WalletController;

public class BaseFragment {
    /* access modifiers changed from: protected */
    public ActionBar actionBar;
    /* access modifiers changed from: protected */
    public Bundle arguments;
    /* access modifiers changed from: protected */
    public int classGuid;
    /* access modifiers changed from: protected */
    public int currentAccount;
    protected Drawable defaultActionBarBackgroundDrawable;
    private boolean finishing;
    /* access modifiers changed from: protected */
    public View fragmentView;
    protected Drawable gameActionBarBackgroundDrawable;
    protected boolean hasOwnBackground;
    /* access modifiers changed from: protected */
    public boolean inPreviewMode;
    private boolean isFinished;
    protected boolean isPaused;
    /* access modifiers changed from: protected */
    public ActionBarLayout parentLayout;
    /* access modifiers changed from: protected */
    public boolean swipeBackEnabled;
    private Unbinder unbinder;
    protected Dialog visibleDialog;

    public BaseFragment() {
        this((Bundle) null);
    }

    public BaseFragment(Bundle args) {
        this.currentAccount = UserConfig.selectedAccount;
        this.swipeBackEnabled = true;
        this.hasOwnBackground = false;
        this.isPaused = true;
        this.arguments = args;
        this.classGuid = ConnectionsManager.generateClassGuid();
        this.isFinished = false;
        this.finishing = false;
    }

    public void setCurrentAccount(int account) {
        if (this.fragmentView == null) {
            this.currentAccount = account;
            return;
        }
        throw new IllegalStateException("trying to set current account when fragment UI already created");
    }

    public ActionBarLayout getParentLayout() {
        return this.parentLayout;
    }

    public ActionBar getActionBar() {
        return this.actionBar;
    }

    public View getFragmentView() {
        return this.fragmentView;
    }

    public View createView(Context context) {
        return null;
    }

    public Bundle getArguments() {
        return this.arguments;
    }

    public int getCurrentAccount() {
        return this.currentAccount;
    }

    public int getClassGuid() {
        return this.classGuid;
    }

    /* access modifiers changed from: protected */
    public void setInPreviewMode(boolean value) {
        this.inPreviewMode = value;
        ActionBar actionBar2 = this.actionBar;
        if (actionBar2 != null) {
            boolean z = false;
            if (value) {
                actionBar2.setOccupyStatusBar(false);
                return;
            }
            if (Build.VERSION.SDK_INT >= 21) {
                z = true;
            }
            actionBar2.setOccupyStatusBar(z);
        }
    }

    /* access modifiers changed from: protected */
    public void clearViews() {
        View view = this.fragmentView;
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                try {
                    onRemoveFromParent();
                    parent.removeView(this.fragmentView);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            this.fragmentView = null;
        }
        ActionBar actionBar2 = this.actionBar;
        if (actionBar2 != null) {
            ViewGroup parent2 = (ViewGroup) actionBar2.getParent();
            if (parent2 != null) {
                try {
                    parent2.removeView(this.actionBar);
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            }
            this.actionBar = null;
        }
        this.parentLayout = null;
    }

    /* access modifiers changed from: protected */
    public void onRemoveFromParent() {
    }

    public void setParentFragment(BaseFragment fragment) {
        setParentLayout(fragment.parentLayout);
        this.fragmentView = createView(this.parentLayout.getContext());
    }

    /* access modifiers changed from: protected */
    public void setParentLayout(ActionBarLayout layout) {
        ViewGroup parent;
        if (this.parentLayout != layout) {
            this.parentLayout = layout;
            View view = this.fragmentView;
            if (view != null) {
                ViewGroup parent2 = (ViewGroup) view.getParent();
                if (parent2 != null) {
                    try {
                        onRemoveFromParent();
                        parent2.removeView(this.fragmentView);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
                ActionBarLayout actionBarLayout = this.parentLayout;
                if (!(actionBarLayout == null || actionBarLayout.getContext() == this.fragmentView.getContext())) {
                    this.fragmentView = null;
                }
            }
            if (this.actionBar != null) {
                ActionBarLayout actionBarLayout2 = this.parentLayout;
                boolean differentParent = (actionBarLayout2 == null || actionBarLayout2.getContext() == this.actionBar.getContext()) ? false : true;
                if ((this.actionBar.getAddToContainer() || differentParent) && (parent = (ViewGroup) this.actionBar.getParent()) != null) {
                    try {
                        parent.removeView(this.actionBar);
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                    }
                }
                if (differentParent) {
                    this.actionBar = null;
                }
            }
            ActionBarLayout actionBarLayout3 = this.parentLayout;
            if (actionBarLayout3 != null && this.actionBar == null) {
                ActionBar createActionBar = createActionBar(actionBarLayout3.getContext());
                this.actionBar = createActionBar;
                createActionBar.parentFragment = this;
            }
        }
        this.isFinished = false;
        this.finishing = false;
    }

    /* access modifiers changed from: protected */
    public void createActionBarBackgroundDrawable() {
        this.defaultActionBarBackgroundDrawable = Theme.createRoundRectDrawable(0.0f, Theme.getColor(Theme.key_actionBarDefault));
        this.gameActionBarBackgroundDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{Color.parseColor("#FFFE6869"), Color.parseColor("#FFFE856B")});
    }

    /* access modifiers changed from: protected */
    public ActionBar createActionBar(Context context) {
        createActionBarBackgroundDrawable();
        ActionBar actionBar2 = new ActionBar(context);
        actionBar2.setBackground(this.defaultActionBarBackgroundDrawable);
        actionBar2.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultSelector), false);
        actionBar2.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarActionModeDefaultSelector), true);
        actionBar2.setItemsColor(Theme.getColor(Theme.key_actionBarDefaultIcon), false);
        actionBar2.setItemsColor(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon), true);
        if (this.inPreviewMode) {
            actionBar2.setOccupyStatusBar(false);
        }
        ActionBarLayout actionBarLayout = this.parentLayout;
        if (actionBarLayout != null) {
            actionBarLayout.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefault));
        }
        return actionBar2;
    }

    public void movePreviewFragment(float dy) {
        this.parentLayout.movePreviewFragment(dy);
    }

    public void finishPreviewFragment() {
        this.parentLayout.finishPreviewFragment();
    }

    public void finishFragment() {
        finishFragment(true);
    }

    public void finishFragment(boolean animated) {
        ActionBarLayout actionBarLayout;
        if (!this.isFinished && (actionBarLayout = this.parentLayout) != null) {
            this.finishing = true;
            actionBarLayout.closeLastFragment(animated);
        }
    }

    public void finishFragmentFromUp(boolean animated) {
        ActionBarLayout actionBarLayout;
        if (!this.isFinished && (actionBarLayout = this.parentLayout) != null) {
            this.finishing = true;
            actionBarLayout.closeLastFragmentFromUp(animated);
        }
    }

    public void removeSelfFromStack() {
        ActionBarLayout actionBarLayout;
        if (!this.isFinished && (actionBarLayout = this.parentLayout) != null) {
            this.finishing = true;
            actionBarLayout.removeFragmentFromStack(this);
        }
    }

    public boolean isFinishing() {
        return this.finishing;
    }

    public boolean onFragmentCreate() {
        return true;
    }

    public void onFragmentDestroy() {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequestsForGuid(this.classGuid);
        this.isFinished = true;
        ActionBar actionBar2 = this.actionBar;
        if (actionBar2 != null) {
            actionBar2.setEnabled(false);
        }
        Unbinder unbinder2 = this.unbinder;
        if (unbinder2 != null) {
            try {
                unbinder2.unbind();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public boolean needDelayOpenAnimation() {
        return false;
    }

    public void onResume() {
        this.isPaused = false;
        setStatusBarTheme();
    }

    /* access modifiers changed from: protected */
    public void setStatusBarTheme() {
        if (Theme.getCurrentTheme() == null || !Theme.getCurrentTheme().isDark()) {
            StatusBarUtils.setStatusBarDarkTheme(getParentActivity(), true);
            setNavigationBarColor(Theme.getColor(Theme.key_bottomBarBackground));
            return;
        }
        StatusBarUtils.setStatusBarDarkTheme(getParentActivity(), false);
    }

    public void onPause() {
        ActionBar actionBar2 = this.actionBar;
        if (actionBar2 != null) {
            actionBar2.onPause();
        }
        this.isPaused = true;
        try {
            if (this.visibleDialog != null && this.visibleDialog.isShowing() && dismissDialogOnPause(this.visibleDialog)) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public BaseFragment getFragmentForAlert(int offset) {
        ActionBarLayout actionBarLayout = this.parentLayout;
        if (actionBarLayout == null || actionBarLayout.fragmentsStack.size() <= offset + 1) {
            return this;
        }
        return this.parentLayout.fragmentsStack.get((this.parentLayout.fragmentsStack.size() - 2) - offset);
    }

    public void onConfigurationChanged(Configuration newConfig) {
    }

    public boolean onBackPressed() {
        return true;
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
    }

    public void setArguments(Bundle arguments2) {
        this.arguments = arguments2;
    }

    public void saveSelfArgs(Bundle args) {
    }

    public void restoreSelfArgs(Bundle args) {
    }

    public boolean presentFragmentAsPreview(BaseFragment fragment) {
        ActionBarLayout actionBarLayout = this.parentLayout;
        return actionBarLayout != null && actionBarLayout.presentFragmentAsPreview(fragment);
    }

    public boolean presentFragment(BaseFragment fragment) {
        ActionBarLayout actionBarLayout = this.parentLayout;
        return actionBarLayout != null && actionBarLayout.presentFragment(fragment);
    }

    public boolean presentFragment(BaseFragment fragment, boolean removeLast) {
        ActionBarLayout actionBarLayout = this.parentLayout;
        return actionBarLayout != null && actionBarLayout.presentFragment(fragment, removeLast);
    }

    public boolean presentFragment(BaseFragment fragment, boolean removeLast, boolean forceWithoutAnimation) {
        ActionBarLayout actionBarLayout = this.parentLayout;
        return actionBarLayout != null && actionBarLayout.presentFragment(fragment, removeLast, forceWithoutAnimation, true, false);
    }

    public boolean presentFragmentFromBottom(BaseFragment fragment, boolean removeLast, boolean forceWithoutAnimation) {
        ActionBarLayout actionBarLayout = this.parentLayout;
        return actionBarLayout != null && actionBarLayout.presentFragmentFromBottom(fragment, removeLast, forceWithoutAnimation, true, false);
    }

    public FragmentActivity getParentActivity() {
        ActionBarLayout actionBarLayout = this.parentLayout;
        if (actionBarLayout != null) {
            return actionBarLayout.parentActivity;
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public void setParentActivityTitle(CharSequence title) {
        Activity activity = getParentActivity();
        if (activity != null) {
            activity.setTitle(title);
        }
    }

    public void startActivityForResult(Intent intent, int requestCode) {
        ActionBarLayout actionBarLayout = this.parentLayout;
        if (actionBarLayout != null) {
            actionBarLayout.startActivityForResult(intent, requestCode);
        }
    }

    public void dismissCurrentDialog() {
        Dialog dialog = this.visibleDialog;
        if (dialog != null) {
            try {
                dialog.dismiss();
                this.visibleDialog = null;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public boolean dismissDialogOnPause(Dialog dialog) {
        return true;
    }

    public boolean canBeginSlide() {
        return true;
    }

    public void onBeginSlide() {
        try {
            if (this.visibleDialog != null && this.visibleDialog.isShowing()) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        ActionBar actionBar2 = this.actionBar;
        if (actionBar2 != null) {
            actionBar2.onPause();
        }
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationStart(boolean isOpen, boolean backward) {
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
    }

    /* access modifiers changed from: protected */
    public void onBecomeFullyVisible() {
        ActionBar actionBar2;
        if (((AccessibilityManager) ApplicationLoader.applicationContext.getSystemService("accessibility")).isEnabled() && (actionBar2 = getActionBar()) != null) {
            String title = actionBar2.getTitle();
            if (!TextUtils.isEmpty(title)) {
                setParentActivityTitle(title);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onBecomeFullyHidden() {
    }

    /* access modifiers changed from: protected */
    public AnimatorSet onCustomTransitionAnimation(boolean isOpen, Runnable callback) {
        return null;
    }

    public void onLowMemory() {
    }

    public Dialog showDialog(Dialog dialog) {
        return showDialog(dialog, false, (DialogInterface.OnDismissListener) null);
    }

    public Dialog showDialog(Dialog dialog, DialogInterface.OnDismissListener onDismissListener) {
        return showDialog(dialog, false, onDismissListener);
    }

    public Dialog showDialog(Dialog dialog, boolean allowInTransition, DialogInterface.OnDismissListener onDismissListener) {
        ActionBarLayout actionBarLayout;
        if (dialog == null || (actionBarLayout = this.parentLayout) == null || actionBarLayout.animationInProgress || this.parentLayout.startedTracking || (!allowInTransition && this.parentLayout.checkTransitionAnimation())) {
            return null;
        }
        try {
            if (this.visibleDialog != null) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        try {
            this.visibleDialog = dialog;
            dialog.setCanceledOnTouchOutside(true);
            this.visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener(onDismissListener) {
                private final /* synthetic */ DialogInterface.OnDismissListener f$1;

                {
                    this.f$1 = r2;
                }

                public final void onDismiss(DialogInterface dialogInterface) {
                    BaseFragment.this.lambda$showDialog$0$BaseFragment(this.f$1, dialogInterface);
                }
            });
            this.visibleDialog.show();
            return this.visibleDialog;
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
            return null;
        }
    }

    public /* synthetic */ void lambda$showDialog$0$BaseFragment(DialogInterface.OnDismissListener onDismissListener, DialogInterface dialog1) {
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog1);
        }
        onDialogDismiss(this.visibleDialog);
        this.visibleDialog = null;
    }

    /* access modifiers changed from: protected */
    public void onDialogDismiss(Dialog dialog) {
    }

    public Dialog getVisibleDialog() {
        return this.visibleDialog;
    }

    public void setVisibleDialog(Dialog dialog) {
        this.visibleDialog = dialog;
    }

    public boolean extendActionMode(Menu menu) {
        return false;
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[0];
    }

    public AccountInstance getAccountInstance() {
        return AccountInstance.getInstance(this.currentAccount);
    }

    /* access modifiers changed from: protected */
    public MessagesController getMessagesController() {
        return getAccountInstance().getMessagesController();
    }

    /* access modifiers changed from: protected */
    public ContactsController getContactsController() {
        return getAccountInstance().getContactsController();
    }

    /* access modifiers changed from: protected */
    public MediaDataController getMediaDataController() {
        return getAccountInstance().getMediaDataController();
    }

    /* access modifiers changed from: protected */
    public ConnectionsManager getConnectionsManager() {
        return getAccountInstance().getConnectionsManager();
    }

    /* access modifiers changed from: protected */
    public LocationController getLocationController() {
        return getAccountInstance().getLocationController();
    }

    /* access modifiers changed from: protected */
    public NotificationsController getNotificationsController() {
        return getAccountInstance().getNotificationsController();
    }

    /* access modifiers changed from: protected */
    public MessagesStorage getMessagesStorage() {
        return getAccountInstance().getMessagesStorage();
    }

    /* access modifiers changed from: protected */
    public SendMessagesHelper getSendMessagesHelper() {
        return getAccountInstance().getSendMessagesHelper();
    }

    /* access modifiers changed from: protected */
    public FileLoader getFileLoader() {
        return getAccountInstance().getFileLoader();
    }

    /* access modifiers changed from: protected */
    public SecretChatHelper getSecretChatHelper() {
        return getAccountInstance().getSecretChatHelper();
    }

    /* access modifiers changed from: protected */
    public DownloadController getDownloadController() {
        return getAccountInstance().getDownloadController();
    }

    /* access modifiers changed from: protected */
    public SharedPreferences getNotificationsSettings() {
        return getAccountInstance().getNotificationsSettings();
    }

    public NotificationCenter getNotificationCenter() {
        return getAccountInstance().getNotificationCenter();
    }

    public MediaController getMediaController() {
        return MediaController.getInstance();
    }

    public UserConfig getUserConfig() {
        return getAccountInstance().getUserConfig();
    }

    public WalletController getWalletController() {
        return getAccountInstance().getWalletController();
    }

    public void useButterKnife() {
        View view = this.fragmentView;
        if (view != null) {
            this.unbinder = ButterKnife.bind((Object) this, view);
        }
    }

    public int getNavigationBarColor() {
        return Theme.getColor(Theme.key_windowBackgroundGray);
    }

    public void setNavigationBarColor(int color) {
        Activity activity = getParentActivity();
        if (activity != null) {
            Window window = activity.getWindow();
            if (Build.VERSION.SDK_INT >= 26 && window != null && window.getNavigationBarColor() != color) {
                window.setNavigationBarColor(color);
                AndroidUtilities.setLightNavigationBar(window, AndroidUtilities.computePerceivedBrightness(color) >= 0.721f);
            }
        }
    }
}
