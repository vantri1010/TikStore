package im.bclpbkiauv.ui.hui.mine;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.blankj.utilcode.util.TimeUtils;
import com.king.zxing.util.CodeUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.ImageLoader;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SendMessagesHelper;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.phoneformat.PhoneFormat;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.ParamsUtil;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCContacts;
import im.bclpbkiauv.tgnet.TLRPCLogin;
import im.bclpbkiauv.ui.AddAccountActivity;
import im.bclpbkiauv.ui.ChangeNameActivity;
import im.bclpbkiauv.ui.ChangeSignActivity;
import im.bclpbkiauv.ui.ChangeUsernameActivity;
import im.bclpbkiauv.ui.SelectBirthdayActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.ImageUpdater;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RadialProgressView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.dialogs.BottomDialog;
import im.bclpbkiauv.ui.dialogs.OptionsWheelPickerDialog;
import im.bclpbkiauv.ui.hcells.MryDividerCell;
import im.bclpbkiauv.ui.hcells.TextSettingCell;
import im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity;
import im.bclpbkiauv.ui.hui.login.LoginPasswordContronllerActivity;
import im.bclpbkiauv.ui.hviews.MryTextView;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Marker;

public class NewUserInfoActivity extends BaseFragment implements ImageUpdater.ImageUpdaterDelegate, NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public int addAccountRow;
    private TLRPC.FileLocation avatar;
    /* access modifiers changed from: private */
    public AnimatorSet avatarAnimation;
    private TLRPC.FileLocation avatarBig;
    /* access modifiers changed from: private */
    public int avatarRow;
    /* access modifiers changed from: private */
    public int birthRow;
    /* access modifiers changed from: private */
    public Context context;
    /* access modifiers changed from: private */
    public int genderRow;
    /* access modifiers changed from: private */
    public int gestureCodeRow;
    private ImageUpdater imageUpdater;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public int loginPwdRow;
    /* access modifiers changed from: private */
    public int logoutEmptyRow;
    /* access modifiers changed from: private */
    public int logoutRow;
    private OptionsWheelPickerDialog.Builder<String> mGenderPickerBulder;
    ServiceConnection mServerServiceConnection;
    /* access modifiers changed from: private */
    public int nicknameRow;
    /* access modifiers changed from: private */
    public int phoneRow;
    /* access modifiers changed from: private */
    public int qrcodeEmptyRow;
    /* access modifiers changed from: private */
    public int qrcodeRow;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int signEmptyRow;
    /* access modifiers changed from: private */
    public int signRow;
    /* access modifiers changed from: private */
    public TLRPCContacts.CL_userFull_v1 userFull;
    /* access modifiers changed from: private */
    public int usernameRow;

    public /* synthetic */ void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList, boolean z, int i) {
        ImageUpdater.ImageUpdaterDelegate.CC.$default$didSelectPhotos(this, arrayList, z, i);
    }

    public /* synthetic */ String getInitialSearchString() {
        return ImageUpdater.ImageUpdaterDelegate.CC.$default$getInitialSearchString(this);
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.userFullInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.loginPasswordSetSuccess);
        TLRPC.UserFull full = MessagesController.getInstance(this.currentAccount).getUserFull(getUserConfig().getClientUserId());
        if (full instanceof TLRPCContacts.CL_userFull_v1) {
            this.userFull = (TLRPCContacts.CL_userFull_v1) full;
        }
        MessagesController.getInstance(this.currentAccount).loadFullUser(getUserConfig().getCurrentUser(), this.classGuid, true);
        ImageUpdater imageUpdater2 = new ImageUpdater();
        this.imageUpdater = imageUpdater2;
        imageUpdater2.parentFragment = this;
        this.imageUpdater.delegate = this;
        return super.onFragmentCreate();
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        this.imageUpdater.onActivityResult(requestCode, resultCode, data);
    }

    public void saveSelfArgs(Bundle args) {
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (imageUpdater2 != null && imageUpdater2.currentPicturePath != null) {
            args.putString("path", this.imageUpdater.currentPicturePath);
        }
    }

    public void restoreSelfArgs(Bundle args) {
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (imageUpdater2 != null) {
            imageUpdater2.currentPicturePath = args.getString("path");
        }
    }

    public View createView(Context context2) {
        this.context = context2;
        this.fragmentView = new FrameLayout(context2);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        initActionBar();
        initList(context2);
        return this.fragmentView;
    }

    private void initActionBar() {
        this.actionBar.setCastShadows(false);
        this.actionBar.setTitle(LocaleController.getString("InfoEdit", R.string.InfoEdit));
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    NewUserInfoActivity.this.finishFragment();
                }
            }
        });
    }

    private void initList(Context context2) {
        RecyclerListView recyclerListView = new RecyclerListView(context2);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context2));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter2 = new ListAdapter();
        this.listAdapter = listAdapter2;
        recyclerListView2.setAdapter(listAdapter2);
        this.listView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        ((FrameLayout) this.fragmentView).setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        ((FrameLayout) this.fragmentView).addView(this.listView, LayoutHelper.createFrame(-1, -1, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(0.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(0.0f)));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener(context2) {
            private final /* synthetic */ Context f$1;

            {
                this.f$1 = r2;
            }

            public final void onItemClick(View view, int i) {
                NewUserInfoActivity.this.lambda$initList$3$NewUserInfoActivity(this.f$1, view, i);
            }
        });
        updateRows();
    }

    public /* synthetic */ void lambda$initList$3$NewUserInfoActivity(Context context2, View view, int position) {
        TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
        if (position == this.avatarRow) {
            BottomDialog dialog = new BottomDialog(context2);
            dialog.addDialogItem(new BottomDialog.NormalTextItem(0, LocaleController.getString("FromCamera", R.string.FromCamera), true));
            dialog.addDialogItem(new BottomDialog.NormalTextItem(1, LocaleController.getString("FromGallery", R.string.FromGallery), false));
            dialog.setOnItemClickListener(new BottomDialog.OnItemClickListener() {
                public final void onItemClick(int i, View view) {
                    NewUserInfoActivity.this.lambda$null$0$NewUserInfoActivity(i, view);
                }
            });
            showDialog(dialog);
        } else if (position == this.nicknameRow) {
            presentFragment(new ChangeNameActivity());
        } else if (position == this.usernameRow) {
            AlertDialog.Builder ub = new AlertDialog.Builder(context2);
            ub.setTitle(LocaleController.getString(R.string.AppName));
            ub.setMessage(LocaleController.getString(R.string.ChangeAppNameCodeTips));
            ub.setNegativeButton(LocaleController.getString(R.string.Cancel), (DialogInterface.OnClickListener) null);
            ub.setPositiveButton(LocaleController.getString(R.string.Modify), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    NewUserInfoActivity.this.lambda$null$1$NewUserInfoActivity(dialogInterface, i);
                }
            });
            showDialog(ub.create());
        } else if (position == this.qrcodeRow) {
            presentFragment(new QrCodeActivity(user.id));
        } else if (position == this.loginPwdRow) {
            presentFragment(new LoginPasswordContronllerActivity(0, (Bundle) null));
        } else if (position == this.phoneRow) {
            presentFragment(new ActionIntroActivity(3));
        } else if (position == this.birthRow) {
            TLRPCContacts.CL_userFull_v1 cL_userFull_v1 = this.userFull;
            if (cL_userFull_v1 != null) {
                presentFragment(new SelectBirthdayActivity(cL_userFull_v1));
            }
        } else if (position == this.genderRow) {
            TLRPCContacts.CL_userFull_v1 cL_userFull_v12 = this.userFull;
        } else if (position == this.signRow) {
            TLRPCContacts.CL_userFull_v1 cL_userFull_v13 = this.userFull;
            if (cL_userFull_v13 != null) {
                presentFragment(new ChangeSignActivity(cL_userFull_v13));
            }
        } else if (position == this.addAccountRow) {
            presentFragment(new AddAccountActivity());
        } else if (position != this.logoutRow) {
        } else {
            if (ApplicationLoader.mbytAVideoCallBusy == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context2);
                builder.setMessage(LocaleController.getString("AreYouSureLogout", R.string.AreYouSureLogout));
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        NewUserInfoActivity.this.lambda$null$2$NewUserInfoActivity(dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                showDialog(builder.create());
                return;
            }
            ToastUtils.show((CharSequence) LocaleController.getString(R.string.visual_call_stop_login_out));
        }
    }

    public /* synthetic */ void lambda$null$0$NewUserInfoActivity(int id, View v) {
        if (id == 0) {
            this.imageUpdater.openCamera();
        } else if (id == 1) {
            this.imageUpdater.openGallery();
        }
    }

    public /* synthetic */ void lambda$null$1$NewUserInfoActivity(DialogInterface dialog, int which) {
        presentFragment(new ChangeUsernameActivity());
    }

    public /* synthetic */ void lambda$null$2$NewUserInfoActivity(DialogInterface dialogInterface, int i) {
        MessagesController.getInstance(this.currentAccount).performLogout(1);
    }

    private void updateRows() {
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.avatarRow = 0;
        int i2 = i + 1;
        this.rowCount = i2;
        this.nicknameRow = i;
        this.usernameRow = -1;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.usernameRow = i2;
        int i4 = i3 + 1;
        this.rowCount = i4;
        this.qrcodeRow = i3;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.qrcodeEmptyRow = i4;
        this.rowCount = i5 + 1;
        this.loginPwdRow = i5;
        if (!BuildVars.RELEASE_VERSION) {
            int i6 = this.rowCount;
            this.rowCount = i6 + 1;
            this.gestureCodeRow = i6;
        }
        int i7 = this.rowCount;
        int i8 = i7 + 1;
        this.rowCount = i8;
        this.birthRow = i7;
        int i9 = i8 + 1;
        this.rowCount = i9;
        this.genderRow = i8;
        int i10 = i9 + 1;
        this.rowCount = i10;
        this.signEmptyRow = i9;
        int i11 = i10 + 1;
        this.rowCount = i11;
        this.addAccountRow = i10;
        int i12 = i11 + 1;
        this.rowCount = i12;
        this.logoutEmptyRow = i11;
        this.rowCount = i12 + 1;
        this.logoutRow = i12;
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: private */
    public void saveImage(BackupImageView avatarImage) {
        new Thread(new Runnable() {
            public final void run() {
                NewUserInfoActivity.lambda$saveImage$4(BackupImageView.this);
            }
        }).start();
    }

    static /* synthetic */ void lambda$saveImage$4(BackupImageView avatarImage) {
        while (avatarImage.getImageReceiver().getBitmap() == null) {
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        Bitmap bitmap = avatarImage.getImageReceiver().getBitmap();
        File file = new File(AndroidUtilities.getCacheDir().getPath() + File.separator + "user_avatar.jpg");
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream out = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        out.flush();
        out.close();
    }

    private void showAvatarProgress(final boolean show, boolean animated) {
        RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(this.avatarRow);
        if (holder != null) {
            final RadialProgressView avatarProgressView = (RadialProgressView) holder.itemView.findViewById(R.id.rpvAvatar);
            AnimatorSet animatorSet = this.avatarAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.avatarAnimation = null;
            }
            if (animated) {
                this.avatarAnimation = new AnimatorSet();
                if (show) {
                    avatarProgressView.setVisibility(0);
                }
                this.avatarAnimation.setDuration(180);
                this.avatarAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (NewUserInfoActivity.this.avatarAnimation != null) {
                            if (!show) {
                                avatarProgressView.setVisibility(4);
                            }
                            AnimatorSet unused = NewUserInfoActivity.this.avatarAnimation = null;
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        AnimatorSet unused = NewUserInfoActivity.this.avatarAnimation = null;
                    }
                });
                this.avatarAnimation.start();
            } else if (show) {
                avatarProgressView.setAlpha(1.0f);
                avatarProgressView.setVisibility(0);
            } else {
                avatarProgressView.setAlpha(0.0f);
                avatarProgressView.setVisibility(4);
            }
        }
    }

    private void showSelectGenderDialog() {
        TLRPCContacts.CL_userFull_v1 cL_userFull_v1 = this.userFull;
        if (cL_userFull_v1 != null && cL_userFull_v1.getExtendBean() != null) {
            if (this.mGenderPickerBulder == null) {
                List<String> data = new ArrayList<>(Arrays.asList(new String[]{LocaleController.getString(R.string.PassportFemale), LocaleController.getString(R.string.PassportMale)}));
                OptionsWheelPickerDialog.Builder<String> defaultBuilder = OptionsWheelPickerDialog.getDefaultBuilder(getParentActivity(), new OnOptionsSelectListener() {
                    public final void onOptionsSelect(int i, int i2, int i3, View view) {
                        NewUserInfoActivity.this.lambda$showSelectGenderDialog$5$NewUserInfoActivity(i, i2, i3, view);
                    }
                });
                this.mGenderPickerBulder = defaultBuilder;
                defaultBuilder.setPicker(data);
            }
            if (this.userFull.getExtendBean().sex == 1) {
                this.mGenderPickerBulder.setSelectOptions(1);
            } else {
                this.mGenderPickerBulder.setSelectOptions(0);
            }
            showDialog(this.mGenderPickerBulder.build());
        }
    }

    public /* synthetic */ void lambda$showSelectGenderDialog$5$NewUserInfoActivity(int options1, int options2, int options3, View v) {
        updateUserSex(options1 == 0 ? 2 : 1);
    }

    private void updateUserSex(int sex) {
        TLRPCContacts.CL_userFull_v1 cL_userFull_v1 = this.userFull;
        if (cL_userFull_v1 != null && cL_userFull_v1.getExtendBean() != null) {
            TLRPCLogin.TL_account_updateUserDetail req = new TLRPCLogin.TL_account_updateUserDetail();
            req.birthday = 0;
            req.extend = new TLRPC.TL_dataJSON();
            req.extend.data = ParamsUtil.toJsonObj(new String[]{"Sex"}, Integer.valueOf(sex)).toJSONString();
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(req, new RequestDelegate(sex) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    NewUserInfoActivity.this.lambda$updateUserSex$7$NewUserInfoActivity(this.f$1, tLObject, tL_error);
                }
            }), this.classGuid);
        }
    }

    public /* synthetic */ void lambda$updateUserSex$7$NewUserInfoActivity(int sex, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response, sex) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ int f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                NewUserInfoActivity.this.lambda$null$6$NewUserInfoActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$6$NewUserInfoActivity(TLRPC.TL_error error, TLObject response, int sex) {
        if (error != null) {
            if (error.text == null) {
                return;
            }
            if (error.text.contains("ALREDY_CHANGE")) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setTitle(LocaleController.getString(R.string.AppName));
                builder.setMessage(LocaleController.getString(R.string.YouHadModifiedOnceCannotModifyAgain));
                builder.setPositiveButton(LocaleController.getString(R.string.OK), (DialogInterface.OnClickListener) null);
                showDialog(builder.create());
            } else if (error.code == 400 || error.text.contains("rpcerror")) {
                ToastUtils.show((int) R.string.SetupFail);
            } else {
                ToastUtils.show((CharSequence) LocaleController.getString(R.string.OperationFailedPleaseTryAgain));
            }
        } else if (response instanceof TLRPC.UserFull) {
            TLRPCContacts.CL_userFull_v1 cL_userFull_v1 = this.userFull;
            if (!(cL_userFull_v1 == null || cL_userFull_v1.getExtendBean() == null)) {
                this.userFull.getExtendBean().sex = sex;
                getMessagesController().loadFullUser(this.userFull.user, this.classGuid, true);
            }
            ListAdapter listAdapter2 = this.listAdapter;
            if (listAdapter2 != null) {
                listAdapter2.notifyItemChanged(this.genderRow);
            }
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.updateInterfaces) {
            updateRows();
        } else if (id == NotificationCenter.userFullInfoDidLoad) {
            if (args[0].intValue() == getUserConfig().getClientUserId()) {
                this.userFull = args[1];
                updateRows();
            }
        } else if (id == NotificationCenter.loginPasswordSetSuccess) {
            MessagesController.getInstance(this.currentAccount).loadFullUser(this.userFull.user, this.classGuid, true);
        }
    }

    public void didUploadPhoto(TLRPC.InputFile file, TLRPC.PhotoSize bigSize, TLRPC.PhotoSize smallSize) {
        AndroidUtilities.runOnUIThread(new Runnable(file, smallSize, bigSize) {
            private final /* synthetic */ TLRPC.InputFile f$1;
            private final /* synthetic */ TLRPC.PhotoSize f$2;
            private final /* synthetic */ TLRPC.PhotoSize f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                NewUserInfoActivity.this.lambda$didUploadPhoto$10$NewUserInfoActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$didUploadPhoto$10$NewUserInfoActivity(TLRPC.InputFile file, TLRPC.PhotoSize smallSize, TLRPC.PhotoSize bigSize) {
        if (file != null) {
            TLRPC.TL_photos_uploadProfilePhoto req = new TLRPC.TL_photos_uploadProfilePhoto();
            req.file = file;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    NewUserInfoActivity.this.lambda$null$9$NewUserInfoActivity(tLObject, tL_error);
                }
            });
            return;
        }
        this.avatar = smallSize.location;
        this.avatarBig = bigSize.location;
        showAvatarProgress(true, false);
        updateRows();
    }

    public /* synthetic */ void lambda$null$9$NewUserInfoActivity(TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
            if (user == null) {
                user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
                if (user != null) {
                    MessagesController.getInstance(this.currentAccount).putUser(user, false);
                } else {
                    return;
                }
            } else {
                UserConfig.getInstance(this.currentAccount).setCurrentUser(user);
            }
            TLRPC.TL_photos_photo photo = (TLRPC.TL_photos_photo) response;
            ArrayList<TLRPC.PhotoSize> sizes = photo.photo.sizes;
            TLRPC.PhotoSize small = FileLoader.getClosestPhotoSizeWithSize(sizes, 150);
            TLRPC.PhotoSize big = FileLoader.getClosestPhotoSizeWithSize(sizes, CodeUtils.DEFAULT_REQ_HEIGHT);
            user.photo = new TLRPC.TL_userProfilePhoto();
            user.photo.photo_id = photo.photo.id;
            if (small != null) {
                user.photo.photo_small = small.location;
            }
            if (big != null) {
                user.photo.photo_big = big.location;
            } else if (small != null) {
                user.photo.photo_small = small.location;
            }
            if (photo != null) {
                if (!(small == null || this.avatar == null)) {
                    FileLoader.getPathToAttach(this.avatar, true).renameTo(FileLoader.getPathToAttach(small, true));
                    ImageLoader.getInstance().replaceImageInCache(this.avatar.volume_id + "_" + this.avatar.local_id + "@50_50", small.location.volume_id + "_" + small.location.local_id + "@50_50", ImageLocation.getForUser(user, false), true);
                }
                if (!(big == null || this.avatarBig == null)) {
                    FileLoader.getPathToAttach(this.avatarBig, true).renameTo(FileLoader.getPathToAttach(big, true));
                }
            }
            MessagesStorage.getInstance(this.currentAccount).clearUserPhotos(user.id);
            ArrayList<TLRPC.User> users = new ArrayList<>();
            users.add(user);
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(users, (ArrayList<TLRPC.Chat>) null, false, true);
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                NewUserInfoActivity.this.lambda$null$8$NewUserInfoActivity();
            }
        });
    }

    public /* synthetic */ void lambda$null$8$NewUserInfoActivity() {
        this.avatar = null;
        this.avatarBig = null;
        showAvatarProgress(false, true);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_ALL));
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
        UserConfig.getInstance(this.currentAccount).saveConfig(true);
        updateRows();
    }

    public void onFragmentDestroy() {
        BackupImageView avatarImage;
        super.onFragmentDestroy();
        RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(this.avatarRow);
        if (!(holder == null || (avatarImage = (BackupImageView) holder.itemView.findViewById(R.id.iv_avatar)) == null)) {
            avatarImage.setImageDrawable((Drawable) null);
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.userFullInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.loginPasswordSetSuccess);
        this.mGenderPickerBulder = null;
        this.imageUpdater.clear();
        this.userFull = null;
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private boolean mblnUpdateFromInit;

        private ListAdapter() {
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() != 3;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 0) {
                View view2 = new TextSettingCell(NewUserInfoActivity.this.context);
                view2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                view = view2;
            } else if (viewType == 1) {
                View view3 = LayoutInflater.from(NewUserInfoActivity.this.context).inflate(R.layout.item_user_info_avatar, parent, false);
                view3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                view = view3;
            } else if (viewType != 2) {
                view = new View(NewUserInfoActivity.this.context);
            } else {
                view = LayoutInflater.from(NewUserInfoActivity.this.context).inflate(R.layout.item_login_add, parent, false);
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String loginPwdText;
            int i;
            String str;
            TLRPC.User user = MessagesController.getInstance(NewUserInfoActivity.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(NewUserInfoActivity.this.currentAccount).getClientUserId()));
            int itemViewType = holder.getItemViewType();
            if (itemViewType == 0) {
                TextSettingCell settingCell = (TextSettingCell) holder.itemView;
                String str2 = "";
                if (position == NewUserInfoActivity.this.nicknameRow) {
                    String string = LocaleController.getString(R.string.Nickname2);
                    if (user != null) {
                        str2 = user.first_name;
                    }
                    settingCell.setTextAndValue(string, str2, false, true);
                } else if (position == NewUserInfoActivity.this.usernameRow) {
                    String string2 = LocaleController.getString(R.string.AppNameCode2);
                    if (user != null) {
                        str2 = user.username.length() > 32 ? user.username.substring(0, 10) : user.username;
                    }
                    settingCell.setTextAndValue(string2, str2, true, true);
                } else if (position == NewUserInfoActivity.this.phoneRow) {
                    String string3 = LocaleController.getString(R.string.PhoneNumber);
                    if (user != null) {
                        PhoneFormat instance = PhoneFormat.getInstance();
                        str2 = instance.format(Marker.ANY_NON_NULL_MARKER + user.phone);
                    }
                    settingCell.setTextAndValue(string3, str2, false, true);
                } else if (position == NewUserInfoActivity.this.birthRow) {
                    String string4 = LocaleController.getString(R.string.Birthday);
                    if (!(NewUserInfoActivity.this.userFull == null || NewUserInfoActivity.this.userFull.getExtendBean() == null)) {
                        str2 = TimeUtils.millis2String(((long) NewUserInfoActivity.this.userFull.getExtendBean().birthday) * 1000, LocaleController.getString("yyyy.mm.dd", R.string.formatterYear2));
                    }
                    settingCell.setTextAndValue(string4, str2, false, true);
                } else if (position == NewUserInfoActivity.this.genderRow) {
                    String string5 = LocaleController.getString(R.string.PassportGender);
                    if (!(NewUserInfoActivity.this.userFull == null || NewUserInfoActivity.this.userFull.getExtendBean() == null)) {
                        if (NewUserInfoActivity.this.userFull.getExtendBean().sex == 1) {
                            i = R.string.PassportMale;
                            str = "Male";
                        } else if (NewUserInfoActivity.this.userFull.getExtendBean().sex == 2) {
                            i = R.string.PassportFemale;
                            str = "Female";
                        }
                        str2 = LocaleController.getString(str, i);
                    }
                    settingCell.setTextAndValue(string5, str2, false, false);
                } else if (position == NewUserInfoActivity.this.signRow) {
                    String string6 = LocaleController.getString(R.string.BioDesc);
                    if (NewUserInfoActivity.this.userFull != null) {
                        str2 = NewUserInfoActivity.this.userFull.about;
                    }
                    settingCell.setTextAndValue(string6, str2, false, true);
                    settingCell.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                } else if (position == NewUserInfoActivity.this.loginPwdRow || position == NewUserInfoActivity.this.gestureCodeRow) {
                    if (position == NewUserInfoActivity.this.loginPwdRow) {
                        loginPwdText = LocaleController.getString(R.string.ModifyLoginPwd);
                    } else {
                        loginPwdText = LocaleController.getString(R.string.GestureCodeSet);
                    }
                    if (user instanceof TLRPC.TL_user) {
                        if (position == NewUserInfoActivity.this.loginPwdRow && ((TLRPC.TL_user) user).hasSetLoginPwd) {
                            loginPwdText = LocaleController.getString(R.string.ModifyLoginPwd);
                        } else if (position == NewUserInfoActivity.this.gestureCodeRow && ((TLRPC.TL_user) user).hasSetGesturePwd) {
                            loginPwdText = LocaleController.getString(R.string.GestureCodeModify);
                        }
                    }
                    settingCell.setText(loginPwdText, false, true);
                } else if (position == NewUserInfoActivity.this.qrcodeRow) {
                    settingCell.setText(LocaleController.getString(R.string.MyQRCode), false, true);
                }
            } else if (itemViewType == 1) {
                BackupImageView ivAvatar = (BackupImageView) holder.itemView.findViewById(R.id.iv_avatar);
                MryTextView tvTitle = (MryTextView) holder.itemView.findViewById(R.id.tv_title);
                MryDividerCell divider = (MryDividerCell) holder.itemView.findViewById(R.id.divider);
                ((MryTextView) holder.itemView.findViewById(R.id.tv_edit_infomation)).setText(LocaleController.getString("EditInfomationText", R.string.EditInfomationText));
                if (position == NewUserInfoActivity.this.avatarRow) {
                    if (user != null) {
                        tvTitle.setText(LocaleController.getString(R.string.PrivacyProfilePhoto));
                        ivAvatar.setRoundRadius(AndroidUtilities.dp(7.5f));
                        ivAvatar.setImage(ImageLocation.getForUser(user, false), "50_50", NewUserInfoActivity.this.getParentActivity().getResources().getDrawable(R.drawable.ic_head_def), (Object) user);
                        if ((user.photo instanceof TLRPC.TL_userProfilePhoto) && !this.mblnUpdateFromInit) {
                            NewUserInfoActivity.this.saveImage(ivAvatar);
                        }
                        this.mblnUpdateFromInit = false;
                    }
                    holder.itemView.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f, Theme.getColor(Theme.key_windowBackgroundWhite)));
                } else if (position == NewUserInfoActivity.this.qrcodeRow) {
                    tvTitle.setText(LocaleController.getString(R.string.MyQRCode));
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ivAvatar.getLayoutParams();
                    lp.height = AndroidUtilities.dp(16.0f);
                    lp.width = AndroidUtilities.dp(16.0f);
                    ivAvatar.setLayoutParams(lp);
                    ivAvatar.setImageResource(R.mipmap.ic_my_qrcode);
                    divider.setVisibility(8);
                }
            } else if (itemViewType == 2) {
                MryTextView textView = (MryTextView) ((LinearLayout) holder.itemView).findViewById(R.id.tv_title);
                if (position == NewUserInfoActivity.this.addAccountRow) {
                    textView.setText(LocaleController.getString(R.string.AddAccount2));
                } else if (position == NewUserInfoActivity.this.logoutRow) {
                    textView.setText(LocaleController.getString(R.string.Signout));
                }
                textView.setTextColor(Theme.getColor(Theme.key_contacts_userCellDeleteText));
                textView.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(8.0f), Theme.getColor(Theme.key_windowBackgroundWhiteBlueButton)));
            }
        }

        public int getItemViewType(int position) {
            if (position == NewUserInfoActivity.this.avatarRow) {
                return 1;
            }
            if (position == NewUserInfoActivity.this.addAccountRow || position == NewUserInfoActivity.this.logoutRow) {
                return 2;
            }
            if (position == NewUserInfoActivity.this.qrcodeEmptyRow || position == NewUserInfoActivity.this.signEmptyRow || position == NewUserInfoActivity.this.logoutEmptyRow) {
                return 3;
            }
            return 0;
        }

        public int getItemCount() {
            return NewUserInfoActivity.this.rowCount;
        }
    }
}
