package im.bclpbkiauv.ui.hui.friendscircle_v1.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import com.bjz.comm.net.base.DataListener;
import com.bjz.comm.net.bean.BResponse;
import com.bjz.comm.net.bean.FcMediaResponseBean;
import com.bjz.comm.net.bean.FcReplyBean;
import com.bjz.comm.net.mvp.contract.BaseFcContract;
import com.bjz.comm.net.mvp.presenter.FcCommonPresenter;
import com.bjz.comm.net.utils.HttpUtils;
import com.socks.library.KLog;
import im.bclpbkiauv.messenger.AccountInstance;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.LocationController;
import im.bclpbkiauv.messenger.MediaController;
import im.bclpbkiauv.messenger.MediaDataController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationsController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SendMessagesHelper;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.VideoEditedInfo;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.ui.ChatActivity;
import im.bclpbkiauv.ui.LaunchActivity;
import im.bclpbkiauv.ui.PhotoAlbumPickerActivity;
import im.bclpbkiauv.ui.PhotoViewer;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarLayout;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity;
import im.bclpbkiauv.ui.hui.discovery.NearPersonAndGroupActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.helper.FcDBHelper;
import im.bclpbkiauv.ui.hui.friendscircle_v1.utils.TimeUtils;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.FcCommMenuDialog;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.toast.FcToastUtils;
import im.bclpbkiauv.ui.hui.visualcall.PermissionUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class BaseFcFragment extends LazyLoadFragment implements BaseFcContract.IFcCommView {
    protected ActionBar actionBar;
    protected Bundle arguments;
    protected int classGuid;
    protected int currentAccount;
    public String currentPicturePath;
    protected Drawable defaultActionBarBackgroundDrawable;
    private FcCommMenuDialog dialogCommonList;
    protected View fragmentView;
    protected boolean inPreviewMode;
    /* access modifiers changed from: private */
    public FcCommonPresenter mCommPresenter;
    protected Context mContext;
    protected ActionBarLayout parentLayout;
    protected Dialog visibleDialog;

    /* access modifiers changed from: protected */
    public abstract int getLayoutRes();

    /* access modifiers changed from: protected */
    public abstract void initData();

    /* access modifiers changed from: protected */
    public abstract void initView();

    public BaseFcFragment() {
        this((Bundle) null);
    }

    public BaseFcFragment(Bundle args) {
        this.currentAccount = UserConfig.selectedAccount;
        this.classGuid = ConnectionsManager.generateClassGuid();
        this.arguments = args;
        this.defaultActionBarBackgroundDrawable = new ColorDrawable(Theme.getColor(Theme.key_actionBarDefault));
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.fragmentView = LayoutInflater.from(this.mContext).inflate(getLayoutRes(), (ViewGroup) null);
        initView();
        return this.fragmentView;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.parentLayout = getParentLayout();
        this.mCommPresenter = new FcCommonPresenter(this);
    }

    /* access modifiers changed from: protected */
    public void loadData() {
        initData();
    }

    public void onDestroy() {
        getConnectionsManager().cancelRequestsForGuid(this.classGuid);
        super.onDestroy();
        FcCommonPresenter fcCommonPresenter = this.mCommPresenter;
        if (fcCommonPresenter != null) {
            fcCommonPresenter.unSubscribeTask();
        }
    }

    /* access modifiers changed from: protected */
    public ActionBar createActionBar() {
        ActionBar actionBar2 = new ActionBar(this.mContext);
        this.actionBar = actionBar2;
        actionBar2.setBackground(this.defaultActionBarBackgroundDrawable);
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultSelector), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarActionModeDefaultSelector), true);
        this.actionBar.setItemsColor(Theme.getColor(Theme.key_actionBarDefaultIcon), false);
        this.actionBar.setItemsColor(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon), true);
        this.actionBar.setCastShadows(false);
        if (this.inPreviewMode) {
            this.actionBar.setOccupyStatusBar(false);
        }
        ActionBarLayout actionBarLayout = this.parentLayout;
        if (actionBarLayout != null) {
            actionBarLayout.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefault));
        }
        return this.actionBar;
    }

    private void convertFontToBold(ViewGroup viewGroup) {
        if (viewGroup != null) {
            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                View view = viewGroup.getChildAt(i);
                if (view instanceof ViewGroup) {
                    convertFontToBold((ViewGroup) view);
                } else if (this.mContext.getResources().getString(R.string.Typeface_Bold).equals(view.getTag())) {
                    if (view instanceof TextView) {
                        ((TextView) view).setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    } else if (view instanceof EditText) {
                        ((EditText) view).setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    }
                }
            }
        }
    }

    private void setBackground(ViewGroup viewGroup) {
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = viewGroup.getChildAt(i);
            Object tag = view.getTag();
            if (tag != null) {
                String key = (String) tag;
                char c = 65535;
                int hashCode = key.hashCode();
                if (hashCode != -1397026623) {
                    if (hashCode != -343666293) {
                        if (hashCode == 1658529622 && key.equals(Theme.key_walletDefaultBackground)) {
                            c = 2;
                        }
                    } else if (key.equals(Theme.key_windowBackgroundWhite)) {
                        c = 0;
                    }
                } else if (key.equals(Theme.key_windowBackgroundGray)) {
                    c = 1;
                }
                if (c == 0) {
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                } else if (c == 1) {
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
                } else if (c == 2) {
                    view.setBackgroundColor(Theme.getColor(Theme.key_walletDefaultBackground));
                }
            }
            if (view instanceof ViewGroup) {
                setBackground((ViewGroup) view);
            }
        }
    }

    public void onPause() {
        super.onPause();
        AndroidUtilities.hideKeyboard(this.fragmentView.findFocus());
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
                    BaseFcFragment.this.lambda$showDialog$0$BaseFcFragment(this.f$1, dialogInterface);
                }
            });
            this.visibleDialog.show();
            return this.visibleDialog;
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
            return null;
        }
    }

    public /* synthetic */ void lambda$showDialog$0$BaseFcFragment(DialogInterface.OnDismissListener onDismissListener, DialogInterface dialog1) {
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
    public void showBottomSheet() {
        if (getParentActivity() != null) {
            if (this.dialogCommonList == null) {
                List<String> list = new ArrayList<>();
                list.add("从手机相册选择");
                list.add("拍一张");
                FcCommMenuDialog fcCommMenuDialog = new FcCommMenuDialog(getParentActivity(), list, (List<Integer>) null, Theme.getColor(Theme.key_dialogTextBlack), (FcCommMenuDialog.RecyclerviewItemClickCallBack) new FcCommMenuDialog.RecyclerviewItemClickCallBack() {
                    public void onRecyclerviewItemClick(int position) {
                        if (position == 0) {
                            BaseFcFragment.this.openGallery();
                        } else if (position == 1) {
                            BaseFcFragment.this.openCamera();
                        }
                    }
                }, 1);
                this.dialogCommonList = fcCommMenuDialog;
                fcCommMenuDialog.setTitle(LocaleController.getString("fc_change_bg", R.string.fc_change_bg), -7631463, 15);
            }
            if (this.dialogCommonList.isShowing()) {
                this.dialogCommonList.dismiss();
            } else {
                this.dialogCommonList.show();
            }
        }
    }

    public void openGallery() {
        if (Build.VERSION.SDK_INT < 23 || getParentActivity() == null || getParentActivity().checkSelfPermission(PermissionUtils.PERMISSION_READ_EXTERNAL_STORAGE) == 0) {
            PhotoAlbumPickerActivity photoAlbumPickerActivity = new PhotoAlbumPickerActivity(1, false, false, (ChatActivity) null, true);
            photoAlbumPickerActivity.setDelegate(new PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate() {
                public void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> photos, boolean notify, int scheduleDate, boolean blnOriginalImg) {
                    if (photos.get(0) != null) {
                        KLog.d("-------相册的背景图-" + photos.get(0).path);
                        BaseFcFragment.this.didSelectOnePhoto(photos.get(0).path);
                    }
                }

                public void startPhotoSelectActivity() {
                }
            });
            presentFragment(photoAlbumPickerActivity);
            return;
        }
        getParentActivity().requestPermissions(new String[]{PermissionUtils.PERMISSION_READ_EXTERNAL_STORAGE}, 4);
    }

    public void openCamera() {
        if (getParentActivity() != null) {
            try {
                if (Build.VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.CAMERA") == 0) {
                    Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                    File image = AndroidUtilities.generatePicturePath();
                    if (image != null) {
                        if (Build.VERSION.SDK_INT >= 24) {
                            takePictureIntent.putExtra("output", FileProvider.getUriForFile(getParentActivity(), "im.bclpbkiauv.messenger.provider", image));
                            takePictureIntent.addFlags(2);
                            takePictureIntent.addFlags(1);
                        } else {
                            takePictureIntent.putExtra("output", Uri.fromFile(image));
                        }
                        this.currentPicturePath = image.getAbsolutePath();
                    }
                    startActivityForResult(takePictureIntent, 133);
                    return;
                }
                getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 19);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && requestCode == 133) {
            PhotoViewer.getInstance().setParentActivity(getParentActivity());
            int orientation = 0;
            try {
                int exif = new ExifInterface(this.currentPicturePath).getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                if (exif == 3) {
                    orientation = 180;
                } else if (exif == 6) {
                    orientation = 90;
                } else if (exif == 8) {
                    orientation = 270;
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            final ArrayList<Object> arrayList = new ArrayList<>();
            arrayList.add(new MediaController.PhotoEntry(0, 0, 0, this.currentPicturePath, orientation, false));
            PhotoViewer.getInstance().setIsFcCrop(true);
            PhotoViewer.getInstance().openPhotoForSelect(arrayList, 0, 1, new PhotoViewer.EmptyPhotoViewerProvider() {
                public void sendButtonPressed(int index, VideoEditedInfo videoEditedInfo, boolean notify, int scheduleDate) {
                    String path = null;
                    MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) arrayList.get(0);
                    if (photoEntry.imagePath != null) {
                        path = photoEntry.imagePath;
                    } else if (photoEntry.path != null) {
                        path = photoEntry.path;
                    }
                    KLog.d("-------拍照的背景图-" + path);
                    BaseFcFragment.this.didSelectOnePhoto(path);
                }

                public boolean allowCaption() {
                    return false;
                }

                public boolean canScrollAway() {
                    return false;
                }
            }, (ChatActivity) null);
            AndroidUtilities.addMediaToGallery(this.currentPicturePath);
            this.currentPicturePath = null;
        }
    }

    /* access modifiers changed from: protected */
    public void didSelectOnePhoto(String photosPath) {
    }

    public void getUploadUrlFailed(String msg) {
        FcToastUtils.show((CharSequence) msg == null ? LocaleController.getString("friendscircle_fail_to_get_ip_location", R.string.friendscircle_fail_to_get_ip_location) : msg);
    }

    /* access modifiers changed from: protected */
    public void uploadFile(String path, DataListener<BResponse<FcMediaResponseBean>> listener) {
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (file.exists()) {
                this.mCommPresenter.uploadFile(file, listener);
            }
        }
    }

    public void onUploadFileSucc(FcMediaResponseBean data, String msg) {
    }

    public void onUploadFileError(String msg) {
    }

    /* access modifiers changed from: protected */
    public void downloadFileToLocal(final String path) {
        String tips = LocaleController.getString(R.string.save_pic);
        String suffix = ".jpg";
        if (!TextUtils.isEmpty(path)) {
            if (path.toLowerCase().endsWith(".mp4")) {
                tips = LocaleController.getString(R.string.save_video);
                suffix = ".mp4";
            } else if (path.toLowerCase().endsWith(".gif")) {
                tips = LocaleController.getString(R.string.save_pic);
                suffix = ".gif";
            } else {
                tips = LocaleController.getString(R.string.save_pic);
                suffix = ".jpg";
            }
        }
        List<String> list = new ArrayList<>();
        list.add(tips);
        final String finalSuffix = suffix;
        new FcCommMenuDialog(getParentActivity(), list, (List<Integer>) null, Theme.getColor(Theme.key_windowBackgroundWhiteBlackText), (FcCommMenuDialog.RecyclerviewItemClickCallBack) new FcCommMenuDialog.RecyclerviewItemClickCallBack() {
            public void onRecyclerviewItemClick(int position) {
                if (position == 0) {
                    BaseFcFragment.this.mCommPresenter.downloadFile(HttpUtils.getInstance().getDownloadFileUrl() + path, AndroidUtilities.getAlbumDir(false).getAbsolutePath(), TimeUtils.getCurrentTime() + finalSuffix);
                }
            }
        }, 1).show();
    }

    public void onDownloadFileSucc(File file) {
        AndroidUtilities.addMediaToGallery(Uri.fromFile(file));
        FcToastUtils.show((CharSequence) LocaleController.getString(R.string.save_album_success));
    }

    public void onDownloadFileError(String msg) {
        FcToastUtils.show((CharSequence) LocaleController.getString(R.string.save_album_error));
    }

    public void onError(String msg) {
    }

    /* access modifiers changed from: protected */
    public <T> void saveNewFcToLocal(T t) {
        FcDBHelper.getInstance().insert(t);
    }

    /* access modifiers changed from: protected */
    public <T> void updateLocalFollowStatus(Class<T> cla, long createBy, boolean isFollow) {
        FcDBHelper.getInstance().updateItemFollowStatus(cla, createBy, isFollow);
    }

    /* access modifiers changed from: protected */
    public <T> void updateLocalItemPermissionStatus(Class<T> cla, long forumID, int permission) {
        FcDBHelper.getInstance().updateItemPermissionStatus(cla, forumID, permission);
    }

    /* access modifiers changed from: protected */
    public <T> void deleteLocalItemById(Class<T> cla, long forumId) {
        FcDBHelper.getInstance().deleteItemById(cla, forumId);
    }

    /* access modifiers changed from: protected */
    public <T> void deleteLocalItemByUserId(Class<T> cla, long userId) {
        FcDBHelper.getInstance().deleteItemByUserId(cla, userId);
    }

    /* access modifiers changed from: protected */
    public <T> void updateLocalItemLikeStatus(Class<T> cla, long forumId, boolean isLike, int ThumbUp) {
        FcDBHelper.getInstance().updateItemLikeStatus(cla, forumId, isLike, ThumbUp);
    }

    /* access modifiers changed from: protected */
    public <T> void updateLocalReplyStatus(Class<T> cla, long forumID, FcReplyBean mFcReplyBean) {
        KLog.d("------------------添加" + forumID);
        FcDBHelper.getInstance().updateItemReply(cla, forumID, mFcReplyBean);
    }

    /* access modifiers changed from: protected */
    public <T> void deleteLocalReply(Class<T> cla, long forumID, long commentID, int commentCount) {
        FcDBHelper.getInstance().deleteReply(cla, forumID, commentID, commentCount);
    }

    public void gotoNearBy() {
        Activity activity;
        boolean z = true;
        if (Build.VERSION.SDK_INT < 23 || (activity = getParentActivity()) == null || activity.checkSelfPermission(PermissionUtils.PERMISSION_ACCESS_COARSE_LOCATION) == 0) {
            boolean enabled = true;
            if (Build.VERSION.SDK_INT >= 28) {
                enabled = ((LocationManager) ApplicationLoader.applicationContext.getSystemService("location")).isLocationEnabled();
            } else if (Build.VERSION.SDK_INT >= 19) {
                try {
                    if (Settings.Secure.getInt(ApplicationLoader.applicationContext.getContentResolver(), "location_mode", 0) == 0) {
                        z = false;
                    }
                    enabled = z;
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
            if (!enabled) {
                presentFragment(new ActionIntroActivity(4));
            } else {
                presentFragment(new NearPersonAndGroupActivity());
            }
        } else {
            presentFragment(new ActionIntroActivity(1));
        }
    }
}
