package im.bclpbkiauv.ui.fragments;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import im.bclpbkiauv.messenger.AccountInstance;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocationController;
import im.bclpbkiauv.messenger.MediaDataController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationsController;
import im.bclpbkiauv.messenger.SendMessagesHelper;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.ui.LaunchActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarLayout;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.constants.Constants;
import java.util.Objects;

public abstract class BaseFmts extends Fragment implements Constants {
    protected ActionBar actionBar;
    protected Bundle arguments;
    protected int classGuid;
    protected Context context;
    protected int currentAccount;
    protected Drawable defaultActionBarBackgroundDrawable;
    protected View fragmentView;
    protected boolean hasOwnBackground;
    protected boolean inPreviewMode;
    protected boolean isPaused;
    private boolean mIsFirst;
    private boolean mIsPrepared;
    private boolean mIsVisible;
    protected ActionBarLayout parentLayout;
    private Unbinder unbinder;
    protected Dialog visibleDialog;

    public BaseFmts() {
        this((Bundle) null);
    }

    public BaseFmts(Bundle args) {
        this.currentAccount = UserConfig.selectedAccount;
        this.hasOwnBackground = false;
        this.isPaused = true;
        this.mIsFirst = true;
        this.classGuid = ConnectionsManager.generateClassGuid();
        this.arguments = args;
        this.defaultActionBarBackgroundDrawable = new ColorDrawable(Theme.getColor(Theme.key_actionBarDefault));
    }

    public void onAttach(Context context2) {
        super.onAttach(context2);
        this.context = context2;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.mIsPrepared = true;
        this.parentLayout = getParentLayout();
        afterPrepared();
        checkToLazyLoad();
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            this.mIsVisible = true;
        } else {
            this.mIsVisible = false;
        }
        checkToLazyLoad();
    }

    public ActionBarLayout getActionBarLayout() {
        return this.parentLayout;
    }

    public ActionBar getActionBar() {
        return this.actionBar;
    }

    /* access modifiers changed from: package-private */
    public ActionBar createActionBar() {
        ActionBar actionBar2 = new ActionBar(this.context);
        this.actionBar = actionBar2;
        actionBar2.setBackground(this.defaultActionBarBackgroundDrawable);
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultSelector), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarActionModeDefaultSelector), true);
        this.actionBar.setItemsColor(Theme.getColor(Theme.key_actionBarDefaultIcon), false);
        this.actionBar.setItemsColor(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon), true);
        if (this.inPreviewMode) {
            this.actionBar.setOccupyStatusBar(false);
        }
        ActionBarLayout actionBarLayout = this.parentLayout;
        if (actionBarLayout != null) {
            actionBarLayout.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefault));
        }
        return this.actionBar;
    }

    private void checkToLazyLoad() {
        if (!this.mIsPrepared) {
            return;
        }
        if (!this.mIsVisible) {
            onInvisible();
        } else if (this.mIsFirst) {
            this.mIsFirst = false;
            lazyLoadData();
        } else {
            onVisible();
        }
    }

    /* access modifiers changed from: protected */
    public void afterPrepared() {
    }

    /* access modifiers changed from: protected */
    public void onVisible() {
    }

    /* access modifiers changed from: protected */
    public void onInvisible() {
    }

    /* access modifiers changed from: protected */
    public void lazyLoadData() {
    }

    public void onResumeForBaseFragment() {
        this.isPaused = false;
    }

    public void onPauseForBaseFragment() {
        this.isPaused = true;
    }

    public boolean isFirstTimeInThisPage() {
        return this.mIsFirst;
    }

    public void reSetFirstLoad(boolean mIsFirst2) {
        this.mIsFirst = mIsFirst2;
    }

    public boolean isFragmentVisible() {
        return this.mIsVisible;
    }

    public boolean resetActionBar() {
        ActionBar actionBar2 = this.actionBar;
        if (actionBar2 == null) {
            return true;
        }
        if (actionBar2.isActionModeShowed()) {
            this.actionBar.hideActionMode();
        }
        if (!this.actionBar.isSearchFieldVisible()) {
            return false;
        }
        this.actionBar.closeSearchField();
        return false;
    }

    public void hideTitle(View rootView) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(rootView, "translationY", new float[]{0.0f, (float) (-ActionBar.getCurrentActionBarHeight())});
        animator.setDuration(300);
        animator.start();
        this.actionBar.setVisibility(4);
    }

    public void showTitle(View rootView) {
        ObjectAnimator.ofFloat(rootView, "translationY", new float[]{(float) (-ActionBar.getCurrentActionBarHeight()), 0.0f}).start();
        this.actionBar.setVisibility(0);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.mIsPrepared = false;
        this.mIsVisible = false;
    }

    public void onDestroy() {
        Unbinder unbinder2 = this.unbinder;
        if (unbinder2 != null) {
            try {
                unbinder2.unbind();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        getConnectionsManager().cancelRequestsForGuid(this.classGuid);
        super.onDestroy();
        this.mIsPrepared = false;
    }

    public Activity getParentActivity() {
        return getActivity();
    }

    public boolean presentFragment(BaseFragment fragment) {
        ActionBarLayout parentLayout2 = getParentLayout();
        this.parentLayout = parentLayout2;
        return parentLayout2 != null && parentLayout2.presentFragment(fragment);
    }

    public boolean presentFragment(BaseFragment fragment, boolean removeLast) {
        ActionBarLayout actionBarLayout = this.parentLayout;
        return actionBarLayout != null && actionBarLayout.presentFragment(fragment, removeLast);
    }

    public boolean presentFragment(BaseFragment fragment, boolean removeLast, boolean forceWithoutAnimation) {
        ActionBarLayout actionBarLayout = this.parentLayout;
        return actionBarLayout != null && actionBarLayout.presentFragment(fragment, removeLast, forceWithoutAnimation, true, false);
    }

    public BaseFragment getCurrentFragment() {
        return getParentLayout().getCurrentFragment();
    }

    private ActionBarLayout getParentLayout() {
        return ((LaunchActivity) Objects.requireNonNull(getActivity())).getActionBarLayout();
    }

    public Dialog showDialog(Dialog dialog) {
        return showDialog(dialog, false, (DialogInterface.OnDismissListener) null);
    }

    public Dialog showDialog(Dialog dialog, DialogInterface.OnDismissListener onDismissListener) {
        return showDialog(dialog, false, onDismissListener);
    }

    public Dialog showDialog(Dialog dialog, boolean allowInTransition, DialogInterface.OnDismissListener onDismissListener) {
        ActionBarLayout actionBarLayout;
        if (dialog == null || (actionBarLayout = this.parentLayout) == null || (!allowInTransition && actionBarLayout.checkTransitionAnimation())) {
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
                    BaseFmts.this.lambda$showDialog$0$BaseFmts(this.f$1, dialogInterface);
                }
            });
            this.visibleDialog.show();
            return this.visibleDialog;
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
            return null;
        }
    }

    public /* synthetic */ void lambda$showDialog$0$BaseFmts(DialogInterface.OnDismissListener onDismissListener, DialogInterface dialog1) {
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog1);
        }
        onDialogDismiss(this.visibleDialog);
        this.visibleDialog = null;
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

    /* access modifiers changed from: protected */
    public void onDialogDismiss(Dialog dialog) {
    }

    public int getClassGuid() {
        return this.classGuid;
    }

    public Dialog getVisibleDialog() {
        return this.visibleDialog;
    }

    public AccountInstance getAccountInstance() {
        return AccountInstance.getInstance(this.currentAccount);
    }

    public UserConfig getUserConfig() {
        return getAccountInstance().getUserConfig();
    }

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

    public boolean onBackPressed() {
        return false;
    }

    /* access modifiers changed from: protected */
    public void useButterKnife() {
        View view = this.fragmentView;
        if (view != null) {
            this.unbinder = ButterKnife.bind((Object) this, view);
        }
    }
}
