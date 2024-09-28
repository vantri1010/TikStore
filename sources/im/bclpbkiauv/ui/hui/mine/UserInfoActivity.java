package im.bclpbkiauv.ui.hui.mine;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.blankj.utilcode.util.TimeUtils;
import com.king.zxing.util.CodeUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
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
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCContacts;
import im.bclpbkiauv.tgnet.TLRPCLogin;
import im.bclpbkiauv.ui.ChangeBioActivity;
import im.bclpbkiauv.ui.ChangeNameActivity;
import im.bclpbkiauv.ui.ChangeUsernameActivity;
import im.bclpbkiauv.ui.PhotoViewer;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.ImageUpdater;
import im.bclpbkiauv.ui.components.RadialProgressView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.dialogs.TimeWheelPickerDialog;
import im.bclpbkiauv.ui.hui.discovery.ActionIntroActivity;
import im.bclpbkiauv.ui.hui.login.ChangePersonalInformationActivity;
import im.bclpbkiauv.ui.hui.login.LoginContronllerActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import org.slf4j.Marker;

@Deprecated
public class UserInfoActivity extends BaseFragment implements View.OnClickListener, NotificationCenter.NotificationCenterDelegate, ImageUpdater.ImageUpdaterDelegate, View.OnLongClickListener {
    private static final int DONE_BUTTON = 1;
    private TLRPC.FileLocation avatar;
    /* access modifiers changed from: private */
    public AnimatorSet avatarAnimation;
    private TLRPC.FileLocation avatarBig;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImage;
    private ImageView avatarOverly;
    /* access modifiers changed from: private */
    public RadialProgressView avatarProgressView;
    private FrameLayout flQRCode;
    private ImageUpdater imageUpdater;
    /* access modifiers changed from: private */
    public ImageView ivCamera;
    private LinearLayout llChangeBirthday;
    private LinearLayout llChangeGender;
    private LinearLayout llChangeNumber;
    private LinearLayout llUserInfoLayout;
    private LinearLayout llUsername;
    private Context mContext;
    private TimeWheelPickerDialog.Builder mTimePickerBuilder;
    private TLRPCContacts.CL_userFull_v1 mUserFull;
    private boolean mblnUpdateFromInit = true;
    private boolean requesting;
    private TextView tvAddAccount;
    private TextView tvAvatarAndNameDesc;
    private TextView tvBioDesc;
    private TextView tvBioText;
    private TextView tvBirthday;
    private TextView tvChangeBirthday;
    private TextView tvChangeGender;
    private TextView tvChangeNumber;
    private TextView tvChangeUsername;
    private TextView tvGender;
    private TextView tvLogout;
    private TextView tvName;
    private TextView tvQRCode;
    private TextView tvUsername;
    private TextView tvUserphone;
    private TLRPC.UserFull userInfo;

    public /* synthetic */ void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList, boolean z, int i) {
        ImageUpdater.ImageUpdaterDelegate.CC.$default$didSelectPhotos(this, arrayList, z, i);
    }

    public /* synthetic */ String getInitialSearchString() {
        return ImageUpdater.ImageUpdaterDelegate.CC.$default$getInitialSearchString(this);
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        ImageUpdater imageUpdater2 = new ImageUpdater();
        this.imageUpdater = imageUpdater2;
        imageUpdater2.parentFragment = this;
        this.imageUpdater.delegate = this;
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.userFullInfoDidLoad);
        this.userInfo = MessagesController.getInstance(this.currentAccount).getUserFull(UserConfig.getInstance(this.currentAccount).getClientUserId());
        MessagesController.getInstance(this.currentAccount).loadUserInfo(UserConfig.getInstance(this.currentAccount).getCurrentUser(), true, this.classGuid);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        BackupImageView backupImageView = this.avatarImage;
        if (backupImageView != null) {
            backupImageView.setImageDrawable((Drawable) null);
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.userFullInfoDidLoad);
        this.imageUpdater.clear();
        this.mTimePickerBuilder = null;
        this.mUserFull = null;
    }

    public View createView(Context context) {
        this.mContext = context;
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_user_info_layout, (ViewGroup) null, false);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        initActionBar();
        initUserInfo();
        initUsername();
        initPhoneNumber();
        initQRCode();
        initBirthday();
        initGender();
        initOther();
        initData();
        updateUserData();
        return this.fragmentView;
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

    private void initActionBar() {
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("PersonalResource", R.string.PersonalResource));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    UserInfoActivity.this.finishFragment();
                }
            }
        });
    }

    private void initUserInfo() {
        LinearLayout linearLayout = (LinearLayout) this.fragmentView.findViewById(R.id.llInfoLayout);
        this.llUserInfoLayout = linearLayout;
        linearLayout.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        BackupImageView backupImageView = (BackupImageView) this.fragmentView.findViewById(R.id.avatarImage);
        this.avatarImage = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(7.5f));
        this.avatarImage.setContentDescription(LocaleController.getString("AccDescrProfilePicture", R.string.AccDescrProfilePicture));
        this.avatarOverly = (ImageView) this.fragmentView.findViewById(R.id.avatarOverly);
        RadialProgressView radialProgressView = (RadialProgressView) this.fragmentView.findViewById(R.id.rpvAvatar);
        this.avatarProgressView = radialProgressView;
        radialProgressView.setVisibility(4);
        this.ivCamera = (ImageView) this.fragmentView.findViewById(R.id.iv_camera);
        this.tvName = (TextView) this.fragmentView.findViewById(R.id.tvName);
        this.tvBioText = (TextView) this.fragmentView.findViewById(R.id.tvBioText);
        this.tvAvatarAndNameDesc = (TextView) this.fragmentView.findViewById(R.id.tvAvatarAndNameDesc);
        this.tvBioDesc = (TextView) this.fragmentView.findViewById(R.id.tvBioDesc);
        this.tvName.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.tvName.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.tvBioText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.tvBioText.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.tvAvatarAndNameDesc.setText(LocaleController.getString("NameAndAvatar", R.string.NameAndAvatar));
        this.tvBioDesc.setText(LocaleController.getString("NameAndAvatar", R.string.BioText));
        this.llUserInfoLayout.setOnClickListener(this);
        this.avatarImage.setOnClickListener(this);
        this.tvBioText.setOnClickListener(this);
        ((LinearLayout) this.fragmentView.findViewById(R.id.llUserInfoLayout)).setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
    }

    private void initPhoneNumber() {
        this.llChangeNumber = (LinearLayout) this.fragmentView.findViewById(R.id.llChangeNumber);
        this.tvChangeNumber = (TextView) this.fragmentView.findViewById(R.id.tvChangeNumber);
        this.tvUserphone = (TextView) this.fragmentView.findViewById(R.id.tvPhoneNumber);
        this.tvChangeNumber.setText(LocaleController.getString("ChangeNumber", R.string.ChangeNumber));
        this.tvChangeNumber.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.llChangeNumber.setOnClickListener(this);
    }

    private void initUsername() {
        this.tvChangeUsername = (TextView) this.fragmentView.findViewById(R.id.tvChangeUsername);
        this.tvUsername = (TextView) this.fragmentView.findViewById(R.id.tvUsername);
        this.llUsername = (LinearLayout) this.fragmentView.findViewById(R.id.llUsername);
        this.tvChangeUsername.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.tvChangeUsername.setText(LocaleController.getString("AppNameCode2", R.string.AppNameCode2));
        this.llUsername.setOnClickListener(this);
        this.llUsername.setOnLongClickListener(this);
    }

    private void initQRCode() {
        this.flQRCode = (FrameLayout) this.fragmentView.findViewById(R.id.flQRCode);
        TextView textView = (TextView) this.fragmentView.findViewById(R.id.tvQRCode);
        this.tvQRCode = textView;
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.tvQRCode.setText(LocaleController.getString("MyQRCode", R.string.MyQRCode));
        this.flQRCode.setOnClickListener(this);
    }

    private void initBirthday() {
        this.llChangeBirthday = (LinearLayout) this.fragmentView.findViewById(R.id.llChangeBirthday);
        this.tvChangeBirthday = (TextView) this.fragmentView.findViewById(R.id.tvChangeBirthday);
        this.tvBirthday = (TextView) this.fragmentView.findViewById(R.id.tvBirthday);
        this.tvChangeBirthday.setText(LocaleController.getString("Birthday", R.string.Birthday));
        this.tvChangeBirthday.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.llChangeBirthday.setOnClickListener(this);
    }

    private void initGender() {
        this.llChangeGender = (LinearLayout) this.fragmentView.findViewById(R.id.llChangeGender);
        this.tvChangeGender = (TextView) this.fragmentView.findViewById(R.id.tvChangeGender);
        this.tvGender = (TextView) this.fragmentView.findViewById(R.id.tvGender);
        this.tvChangeGender.setText(LocaleController.getString("PassportGender", R.string.PassportGender));
        this.tvChangeGender.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
    }

    public void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        String value;
        String value2;
        TLRPC.User user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        if (user == null || user.phone == null || user.phone.length() == 0) {
            value = LocaleController.getString("NumberUnknown", R.string.NumberUnknown);
        } else {
            PhoneFormat instance = PhoneFormat.getInstance();
            value = instance.format(Marker.ANY_NON_NULL_MARKER + user.phone);
        }
        this.tvUserphone.setText(value);
        if (user == null || TextUtils.isEmpty(user.username)) {
            value2 = LocaleController.getString("UsernameEmpty", R.string.UsernameEmpty);
        } else {
            value2 = user.username;
        }
        this.tvUsername.setText(value2);
        TLRPC.UserFull userFull = this.userInfo;
        if (userFull == null || !TextUtils.isEmpty(userFull.about)) {
            TLRPC.UserFull userFull2 = this.userInfo;
            this.tvBioText.setText(userFull2 == null ? LocaleController.getString("Loading", R.string.Loading) : userFull2.about);
        } else {
            this.tvBioText.setText(LocaleController.getString("UserBio", R.string.UserBio));
        }
        getFullUser();
    }

    private void initExtraUserInfoData(TLRPCContacts.CL_userFull_v1_Bean extend) {
        if (extend.sex == 1) {
            this.tvGender.setText(LocaleController.getString("PassportMale", R.string.PassportMale));
        } else if (extend.sex == 2) {
            this.tvGender.setText(LocaleController.getString("PassportMale", R.string.PassportFemale));
        }
        if (extend.birthday != 0) {
            this.tvBirthday.setText(TimeUtils.millis2String(((long) extend.birthday) * 1000, "yyyy-MM-dd"));
        }
    }

    private void initOther() {
        this.tvAddAccount = (TextView) this.fragmentView.findViewById(R.id.tvAddAccount);
        this.tvLogout = (TextView) this.fragmentView.findViewById(R.id.tvLogout);
        this.tvAddAccount.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.tvAddAccount.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.tvAddAccount.setText(LocaleController.getString("AddAccount", R.string.AddAccount));
        this.tvLogout.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.tvLogout.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText5));
        this.tvLogout.setText(LocaleController.getString("LogOut", R.string.LogOut));
        this.tvLogout.setOnClickListener(this);
        this.tvAddAccount.setOnClickListener(this);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.updateInterfaces) {
            int mask = args[0].intValue();
            if ((mask & 2) != 0 || (mask & 1) != 0) {
                updateUserData();
            }
        } else if (id == NotificationCenter.userFullInfoDidLoad && args[0].intValue() == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
            TLRPC.UserFull userFull = args[1];
            this.userInfo = userFull;
            if (userFull == null || !TextUtils.isEmpty(userFull.about)) {
                TLRPC.UserFull userFull2 = this.userInfo;
                this.tvBioText.setText(userFull2 == null ? LocaleController.getString("Loading", R.string.Loading) : userFull2.about);
                return;
            }
            this.tvBioText.setText(LocaleController.getString("UserBio", R.string.UserBio));
        }
    }

    private void updateUserData() {
        TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
        if (user != null) {
            TLRPC.FileLocation photoBig = null;
            if (user.photo != null) {
                photoBig = user.photo.photo_big;
            }
            AvatarDrawable avatarDrawable2 = new AvatarDrawable(user, true);
            this.avatarDrawable = avatarDrawable2;
            avatarDrawable2.setColor(Theme.getColor(Theme.key_avatar_backgroundInProfileBlue));
            BackupImageView backupImageView = this.avatarImage;
            if (backupImageView != null) {
                backupImageView.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) this.avatarDrawable, (Object) user);
                this.avatarImage.getImageReceiver().setVisible(!PhotoViewer.isShowingImage(photoBig), false);
                this.tvName.setText(user.first_name);
                this.avatarImage.getImageReceiver().setVisible(true ^ PhotoViewer.isShowingImage(photoBig), false);
                if ((user.photo instanceof TLRPC.TL_userProfilePhoto) && !this.mblnUpdateFromInit) {
                    saveImage();
                }
                this.mblnUpdateFromInit = false;
            }
        }
    }

    private void saveImage() {
        new Thread(new Runnable() {
            public final void run() {
                UserInfoActivity.this.lambda$saveImage$0$UserInfoActivity();
            }
        }).start();
    }

    public /* synthetic */ void lambda$saveImage$0$UserInfoActivity() {
        while (this.avatarImage.getImageReceiver().getBitmap() == null) {
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        Bitmap bitmap = this.avatarImage.getImageReceiver().getBitmap();
        File file = new File(AndroidUtilities.getCacheDir().getPath() + File.separator + "user_avatar.jpg");
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream out = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        out.flush();
        out.close();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.avatarImage /*2131296377*/:
                TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
                if (user == null) {
                    user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
                }
                if (user != null) {
                    this.imageUpdater.openMenu((user.photo == null || user.photo.photo_big == null || (user.photo instanceof TLRPC.TL_userProfilePhotoEmpty)) ? false : true, new Runnable() {
                        public final void run() {
                            UserInfoActivity.this.lambda$onClick$3$UserInfoActivity();
                        }
                    });
                    return;
                }
                return;
            case R.id.flQRCode /*2131296625*/:
                presentFragment(new QrCodeActivity(getUserConfig().getClientUserId()));
                return;
            case R.id.llChangeBirthday /*2131296901*/:
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setTitle(LocaleController.getString(R.string.AppName));
                builder.setMessage(LocaleController.getString(R.string.UserBirthOnlyCanModifyOnceContuine));
                builder.setNegativeButton(LocaleController.getString(R.string.Cancel), (DialogInterface.OnClickListener) null);
                builder.setPositiveButton(LocaleController.getString(R.string.Confirm), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        UserInfoActivity.this.lambda$onClick$4$UserInfoActivity(dialogInterface, i);
                    }
                });
                showDialog(builder.create());
                return;
            case R.id.llChangeNumber /*2131296903*/:
                presentFragment(new ActionIntroActivity(3));
                return;
            case R.id.llInfoLayout /*2131296912*/:
                presentFragment(new ChangeNameActivity());
                return;
            case R.id.llUsername /*2131296924*/:
                AlertDialog.Builder ub = new AlertDialog.Builder((Context) getParentActivity());
                ub.setTitle(LocaleController.getString(R.string.AppName));
                ub.setMessage(LocaleController.getString(R.string.ChangeAppNameCodeTips));
                ub.setNegativeButton(LocaleController.getString(R.string.Cancel), (DialogInterface.OnClickListener) null);
                ub.setPositiveButton(LocaleController.getString(R.string.Modify), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        UserInfoActivity.this.lambda$onClick$2$UserInfoActivity(dialogInterface, i);
                    }
                });
                showDialog(ub.create());
                return;
            case R.id.tvAddAccount /*2131297437*/:
                if (ApplicationLoader.mbytAVideoCallBusy == 0) {
                    int freeAccount = -1;
                    int a = 0;
                    while (true) {
                        if (a < 3) {
                            if (!UserConfig.getInstance(a).isClientActivated()) {
                                freeAccount = a;
                            } else {
                                a++;
                            }
                        }
                    }
                    if (freeAccount >= 0) {
                        presentFragment(new LoginContronllerActivity(freeAccount));
                        return;
                    }
                    return;
                }
                ToastUtils.show((CharSequence) LocaleController.getString(R.string.visual_call_stop_add_account));
                return;
            case R.id.tvBioText /*2131297458*/:
                if (this.userInfo != null) {
                    presentFragment(new ChangeBioActivity());
                    return;
                }
                return;
            case R.id.tvLogout /*2131297541*/:
                if (getParentActivity() != null) {
                    if (ApplicationLoader.mbytAVideoCallBusy == 0) {
                        AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
                        builder2.setMessage(LocaleController.getString("AreYouSureLogout", R.string.AreYouSureLogout));
                        builder2.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder2.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                            public final void onClick(DialogInterface dialogInterface, int i) {
                                UserInfoActivity.this.lambda$onClick$1$UserInfoActivity(dialogInterface, i);
                            }
                        });
                        builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                        showDialog(builder2.create());
                        return;
                    }
                    ToastUtils.show((CharSequence) LocaleController.getString(R.string.visual_call_stop_login_out));
                    return;
                }
                return;
            default:
                return;
        }
    }

    public /* synthetic */ void lambda$onClick$1$UserInfoActivity(DialogInterface dialogInterface, int i) {
        MessagesController.getInstance(this.currentAccount).performLogout(1);
    }

    public /* synthetic */ void lambda$onClick$2$UserInfoActivity(DialogInterface dialog, int which) {
        presentFragment(new ChangeUsernameActivity());
    }

    public /* synthetic */ void lambda$onClick$3$UserInfoActivity() {
        MessagesController.getInstance(this.currentAccount).deleteUserPhoto((TLRPC.InputPhoto) null);
    }

    public /* synthetic */ void lambda$onClick$4$UserInfoActivity(DialogInterface dialog, int which) {
        showSelectBirthDialog();
    }

    public boolean onLongClick(View v) {
        TLRPC.UserFull userFull;
        if (v.getId() != R.id.llUsername || (userFull = this.userInfo) == null || userFull.user == null || TextUtils.isEmpty(this.userInfo.user.username)) {
            return false;
        }
        AndroidUtilities.addToClipboard("@" + this.userInfo.user.username);
        ToastUtils.show((int) R.string.TextCopied);
        return true;
    }

    private void showAvatarProgress(final boolean show, boolean animated) {
        if (this.avatarOverly != null) {
            AnimatorSet animatorSet = this.avatarAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.avatarAnimation = null;
            }
            if (animated) {
                this.avatarAnimation = new AnimatorSet();
                if (show) {
                    this.avatarProgressView.setVisibility(0);
                    this.ivCamera.setVisibility(8);
                }
                this.avatarAnimation.setDuration(180);
                this.avatarAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (UserInfoActivity.this.avatarAnimation != null) {
                            if (!show) {
                                UserInfoActivity.this.avatarProgressView.setVisibility(4);
                                UserInfoActivity.this.ivCamera.setVisibility(0);
                            }
                            AnimatorSet unused = UserInfoActivity.this.avatarAnimation = null;
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        AnimatorSet unused = UserInfoActivity.this.avatarAnimation = null;
                    }
                });
                this.avatarAnimation.start();
            } else if (show) {
                this.avatarProgressView.setAlpha(1.0f);
                this.avatarProgressView.setVisibility(0);
                this.ivCamera.setVisibility(8);
            } else {
                this.avatarProgressView.setAlpha(0.0f);
                this.avatarProgressView.setVisibility(4);
                this.ivCamera.setVisibility(0);
            }
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
                UserInfoActivity.this.lambda$didUploadPhoto$7$UserInfoActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$didUploadPhoto$7$UserInfoActivity(TLRPC.InputFile file, TLRPC.PhotoSize smallSize, TLRPC.PhotoSize bigSize) {
        if (file != null) {
            TLRPC.TL_photos_uploadProfilePhoto req = new TLRPC.TL_photos_uploadProfilePhoto();
            req.file = file;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    UserInfoActivity.this.lambda$null$6$UserInfoActivity(tLObject, tL_error);
                }
            });
            return;
        }
        this.avatar = smallSize.location;
        this.avatarBig = bigSize.location;
        this.avatarImage.setImage(ImageLocation.getForLocal(this.avatar), "50_50", (Drawable) this.avatarDrawable, (Object) null);
        showAvatarProgress(true, false);
    }

    public /* synthetic */ void lambda$null$6$UserInfoActivity(TLObject response, TLRPC.TL_error error) {
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
                UserInfoActivity.this.lambda$null$5$UserInfoActivity();
            }
        });
    }

    public /* synthetic */ void lambda$null$5$UserInfoActivity() {
        this.avatar = null;
        this.avatarBig = null;
        updateUserData();
        showAvatarProgress(false, true);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_ALL));
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
        UserConfig.getInstance(this.currentAccount).saveConfig(true);
    }

    private void getFullUser() {
        if (!this.requesting) {
            this.requesting = true;
            TLRPCContacts.CL_user_getFulluser req = new TLRPCContacts.CL_user_getFulluser();
            req.inputUser = getMessagesController().getInputUser(UserConfig.getInstance(this.currentAccount).getCurrentUser());
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(req, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    UserInfoActivity.this.lambda$getFullUser$9$UserInfoActivity(tLObject, tL_error);
                }
            }), this.classGuid);
        }
    }

    public /* synthetic */ void lambda$getFullUser$9$UserInfoActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                UserInfoActivity.this.lambda$null$8$UserInfoActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$8$UserInfoActivity(TLRPC.TL_error error, TLObject response) {
        if (!isFinishing() && error == null && (response instanceof TLRPCContacts.CL_userFull_v1)) {
            TLRPCContacts.CL_userFull_v1 cL_userFull_v1 = (TLRPCContacts.CL_userFull_v1) response;
            this.mUserFull = cL_userFull_v1;
            if (cL_userFull_v1.getExtendBean() != null) {
                initExtraUserInfoData(this.mUserFull.getExtendBean());
                if (this.mUserFull.getExtendBean().needCompletedUserInfor()) {
                    presentFragment(new ChangePersonalInformationActivity(this.currentAccount));
                }
            }
        }
        this.requesting = false;
    }

    private void updateUserExtraInformation() {
        TLRPCLogin.TL_account_updateUserDetail req = new TLRPCLogin.TL_account_updateUserDetail();
        req.birthday = (int) (TimeUtils.string2Millis(this.tvBirthday.getText().toString(), "yyyy-MM-dd") / 1000);
        if (req.birthday != 0) {
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(req, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    UserInfoActivity.this.lambda$updateUserExtraInformation$11$UserInfoActivity(tLObject, tL_error);
                }
            }), this.classGuid);
        }
    }

    public /* synthetic */ void lambda$updateUserExtraInformation$11$UserInfoActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                UserInfoActivity.this.lambda$null$10$UserInfoActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$10$UserInfoActivity(TLRPC.TL_error error, TLObject response) {
        TLRPCContacts.CL_userFull_v1 cL_userFull_v1;
        TLRPCContacts.CL_userFull_v1 cL_userFull_v12;
        if (error != null) {
            if (!(this.tvBirthday == null || (cL_userFull_v12 = this.mUserFull) == null || cL_userFull_v12.getExtendBean() == null)) {
                this.tvBirthday.setText(TimeUtils.millis2String(((long) this.mUserFull.getExtendBean().birthday) * 1000, "yyyy-MM-dd"));
            }
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
        } else if ((response instanceof TLRPC.UserFull) && this.tvBirthday != null && (cL_userFull_v1 = this.mUserFull) != null && cL_userFull_v1.getExtendBean() != null) {
            this.mUserFull.getExtendBean().birthday = (int) (TimeUtils.string2Millis(this.tvBirthday.getText().toString().trim(), "yyyy-MM-dd") / 1000);
        }
    }

    private void showSelectBirthDialog() {
        if (this.mTimePickerBuilder == null) {
            this.mTimePickerBuilder = TimeWheelPickerDialog.getDefaultBuilder(getParentActivity(), new OnTimeSelectListener() {
                public final void onTimeSelect(Date date, View view) {
                    UserInfoActivity.this.lambda$showSelectBirthDialog$12$UserInfoActivity(date, view);
                }
            });
        }
        if (!TextUtils.isEmpty(this.tvBirthday.getText() + "")) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(TimeUtils.string2Date(this.tvBirthday.getText().toString().trim(), "yyyy-MM-dd"));
            this.mTimePickerBuilder.setDate(calendar);
        } else {
            this.mTimePickerBuilder.setDate(Calendar.getInstance());
        }
        showDialog(this.mTimePickerBuilder.build());
    }

    public /* synthetic */ void lambda$showSelectBirthDialog$12$UserInfoActivity(Date date, View v) {
        this.tvBirthday.setText(TimeUtils.date2String(date, "yyyy-MM-dd"));
        updateUserExtraInformation();
    }
}
