package im.bclpbkiauv.ui.hui.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.OnClick;
import com.alibaba.fastjson.JSONObject;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.blankj.utilcode.util.TimeUtils;
import com.king.zxing.util.CodeUtils;
import com.serenegiant.uvccamera.BuildConfig;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.callback.Callback;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLoader;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SendMessagesHelper;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCLogin;
import im.bclpbkiauv.ui.ExternalActionActivity;
import im.bclpbkiauv.ui.IndexActivity;
import im.bclpbkiauv.ui.LaunchActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.ImageUpdater;
import im.bclpbkiauv.ui.components.RadialProgressView;
import im.bclpbkiauv.ui.components.filter.MaxByteLengthFilter;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.dialogs.OptionsWheelPickerDialog;
import im.bclpbkiauv.ui.dialogs.TimeWheelPickerDialog;
import im.bclpbkiauv.ui.hviews.MryEditText;
import im.bclpbkiauv.ui.hviews.MryRoundButton;
import im.bclpbkiauv.ui.hviews.MryRoundButtonDrawable;
import im.bclpbkiauv.ui.hviews.MryTextView;
import im.bclpbkiauv.utils.DeviceUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import okhttp3.Call;
import okhttp3.Response;

public class ChangePersonalInformationActivity extends BaseFragment implements ImageUpdater.ImageUpdaterDelegate, NotificationCenter.NotificationCenterDelegate {
    private String accoutName;
    private TLRPC.FileLocation avatar;
    /* access modifiers changed from: private */
    public AnimatorSet avatarAnimation;
    private TLRPC.FileLocation avatarBig;
    private AvatarDrawable avatarDrawable;
    @BindView(2131296411)
    MryRoundButton btnDone;
    /* access modifiers changed from: private */
    public String city;
    @BindView(2131296479)
    View containerAvatar;
    @BindView(2131296481)
    View containerCamera;
    @BindView(2131296484)
    View containerFemale;
    @BindView(2131296485)
    View containerGenderSelect;
    @BindView(2131296488)
    View containerMale;
    /* access modifiers changed from: private */
    public String country;
    private int currentAccount;
    private TextWatcher etNameWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            ChangePersonalInformationActivity.this.changeBtnEnbale();
        }
    };
    @BindView(2131296573)
    MryEditText etNickName;
    private String inviteCode;
    /* access modifiers changed from: private */
    public String ip;
    private boolean isRegisterUser;
    @BindView(2131296712)
    BackupImageView ivAvatar;
    @BindView(2131296713)
    RadialProgressView ivAvatarProgress;
    @BindView(2131296718)
    ImageView ivCamera;
    @BindView(2131296740)
    ImageView ivFemale;
    @BindView(2131296753)
    ImageView ivMale;
    @BindView(2131296754)
    ImageView ivMore;
    private TLRPC.InputFile mAvatarFile;
    private int mBirth;
    private boolean mCanGoBack;
    private String mCurrentSelectPhotoPath;
    private MryRoundButtonDrawable mDrawableFemale;
    private MryRoundButtonDrawable mDrawableMale;
    private int mGender;
    private OptionsWheelPickerDialog.Builder<String> mGenderPickerBulder;
    private ImageUpdater mImageUpdater;
    private boolean mIsLoginSuccess;
    private boolean mShouldShowGenderDialog = true;
    private TimeWheelPickerDialog.Builder mTimePickerBuilder;
    private TLRPC.User mUser;
    private boolean newAccount;
    private String pwdHash;
    @BindView(2131297278)
    View selectDataOfBirthParent;
    @BindView(2131297280)
    View selectSexParent;
    @BindView(2131297464)
    MryTextView tvCamera;
    @BindView(2131297477)
    MryTextView tvDate;
    @BindView(2131297494)
    MryTextView tvFemale;
    @BindView(2131297542)
    MryTextView tvMale;
    @BindView(2131297645)
    MryTextView tvSelectDateOfBirth;
    @BindView(2131297646)
    MryTextView tvSelectSex;

    public /* synthetic */ String getInitialSearchString() {
        return ImageUpdater.ImageUpdaterDelegate.CC.$default$getInitialSearchString(this);
    }

    public ChangePersonalInformationActivity(int currentAccount2) {
        this.currentAccount = currentAccount2;
        this.mCanGoBack = false;
        this.isRegisterUser = false;
    }

    public ChangePersonalInformationActivity(boolean newAccount2, int currentAccount2, String accoutName2, String pwdHash2, String inviteCode2) {
        this.newAccount = newAccount2;
        this.currentAccount = currentAccount2;
        this.accoutName = accoutName2;
        this.pwdHash = pwdHash2;
        this.inviteCode = inviteCode2;
        this.isRegisterUser = true;
        this.mCanGoBack = true;
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidUpload);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidFailUpload);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        TextWatcher textWatcher;
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidUpload);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidFailUpload);
        BackupImageView backupImageView = this.ivAvatar;
        if (backupImageView != null) {
            backupImageView.setImageDrawable((Drawable) null);
        }
        this.mDrawableMale = null;
        this.mDrawableFemale = null;
        this.avatarDrawable = null;
        ImageUpdater imageUpdater = this.mImageUpdater;
        if (imageUpdater != null) {
            imageUpdater.clear();
            this.mImageUpdater = null;
        }
        this.mAvatarFile = null;
        this.avatar = null;
        this.avatarBig = null;
        this.mTimePickerBuilder = null;
        AnimatorSet animatorSet = this.avatarAnimation;
        if (animatorSet != null && (animatorSet.isRunning() || this.avatarAnimation.isStarted())) {
            this.avatarAnimation.cancel();
        }
        this.avatarAnimation = null;
        MryEditText mryEditText = this.etNickName;
        if (!(mryEditText == null || (textWatcher = this.etNameWatcher) == null)) {
            mryEditText.removeTextChangedListener(textWatcher);
            this.etNameWatcher = null;
        }
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_change_personal_information, (ViewGroup) null);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.actionBar.setTitle(LocaleController.getString(R.string.SignUp));
        useButterKnife();
        initActionBar();
        initView();
        return this.fragmentView;
    }

    private void initActionBar() {
        this.actionBar.setCastShadows(false);
        this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        if (this.mCanGoBack) {
            this.actionBar.setBackButtonImage(R.mipmap.ic_back);
            this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
                public void onItemClick(int id) {
                    super.onItemClick(id);
                    if (id == -1 && !ChangePersonalInformationActivity.this.back()) {
                        ChangePersonalInformationActivity.this.finishFragment();
                    }
                }
            });
        }
    }

    private void initView() {
        this.tvCamera.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText7));
        MryRoundButtonDrawable containerAvatarBg = new MryRoundButtonDrawable();
        containerAvatarBg.setIsRadiusAdjustBounds(false);
        containerAvatarBg.setCornerRadius((float) AndroidUtilities.dp(90.0f));
        containerAvatarBg.setStrokeData(AndroidUtilities.dp(2.0f), ColorStateList.valueOf(Theme.getColor(Theme.key_windowBackgroundWhiteGrayLine)));
        containerAvatarBg.setBgData(ColorStateList.valueOf(Theme.getColor(Theme.key_windowBackgroundGray)));
        this.ivMore.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayLine), PorterDuff.Mode.SRC_IN));
        this.btnDone.setPrimaryRadiusAdjustBoundsFillStyle();
        this.containerAvatar.setBackground(containerAvatarBg);
        this.ivAvatar.setRoundRadius(AndroidUtilities.dp(90.0f));
        ImageUpdater imageUpdater = new ImageUpdater();
        this.mImageUpdater = imageUpdater;
        imageUpdater.setSearchAvailable(false);
        this.mImageUpdater.setUploadAfterSelect(false);
        this.mImageUpdater.parentFragment = this;
        this.mImageUpdater.delegate = this;
        this.ivAvatarProgress.setSize(AndroidUtilities.dp(30.0f));
        this.ivAvatarProgress.setProgressColor(Theme.getColor(Theme.key_progressCircle));
        this.etNickName.setFilters(new InputFilter[]{new MaxByteLengthFilter(), $$Lambda$ChangePersonalInformationActivity$1l2hV6MoetyIaSGU0URjJ9o_vHk.INSTANCE});
        this.etNickName.addTextChangedListener(this.etNameWatcher);
        this.avatarDrawable = new AvatarDrawable();
        loadCurrentState();
        if (!this.isRegisterUser) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
            this.mUser = user;
            this.ivAvatar.setImage(ImageLocation.getForUser(user, false), "100_100", (Drawable) this.avatarDrawable, (Object) this.mUser);
            if (!TextUtils.isEmpty(this.mUser.first_name)) {
                this.etNickName.setText(this.mUser.first_name);
            }
        } else {
            String str = this.mCurrentSelectPhotoPath;
            if (str != null) {
                this.ivAvatar.setImage(str, "100_100", this.avatarDrawable);
            }
        }
        changeGender();
        this.containerAvatar.setVisibility(8);
        this.selectSexParent.setVisibility(8);
        this.etNickName.setVisibility(0);
        this.selectDataOfBirthParent.setVisibility(8);
    }

    static /* synthetic */ CharSequence lambda$initView$0(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if (dstart != 0 || source == null || !source.toString().contains(" ")) {
            return null;
        }
        return "";
    }

    public void onResume() {
        super.onResume();
        if (this.newAccount) {
            ConnectionsManager.getInstance(this.currentAccount).setAppPaused(false, false);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        ((GetBuilder) OkHttpUtils.get().url("https://echo.ifconfig.mobi/json")).build().execute(new Callback<String>() {
            public String parseNetworkResponse(Response response, int id) throws Exception {
                Log.e(BuildConfig.BUILD_TYPE, "response===" + response);
                return response.body().string();
            }

            public void onError(Call call, Exception e, int id) {
                Log.e(BuildConfig.BUILD_TYPE, "e===" + e.getMessage());
            }

            public void onResponse(String response, int id) {
                if (!TextUtils.isEmpty(response)) {
                    JSONObject object = (JSONObject) JSONObject.parse(response);
                    String ip = object.getString("ip");
                    String country = object.getString("country");
                    String city = object.getString("city");
                    Log.e(BuildConfig.BUILD_TYPE, "ip===" + ip);
                    Log.e(BuildConfig.BUILD_TYPE, "country===" + country);
                    Log.e(BuildConfig.BUILD_TYPE, "city===" + city);
                    String unused = ChangePersonalInformationActivity.this.ip = ip;
                    String unused2 = ChangePersonalInformationActivity.this.country = country;
                    String unused3 = ChangePersonalInformationActivity.this.city = city;
                }
            }
        });
    }

    public void onPause() {
        super.onPause();
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
        if (this.newAccount) {
            ConnectionsManager.getInstance(this.currentAccount).setAppPaused(true, false);
        }
    }

    /* access modifiers changed from: package-private */
    @OnClick({2131296479, 2131296488, 2131296484, 2131297477, 2131296411, 2131297280})
    public void onClick(View v) {
        boolean z = false;
        switch (v.getId()) {
            case R.id.btnDone /*2131296411*/:
                check();
                return;
            case R.id.containerAvatar /*2131296479*/:
                TLRPC.User user = this.mUser;
                if (user != null) {
                    ImageUpdater imageUpdater = this.mImageUpdater;
                    if (!(user.photo == null || this.mUser.photo.photo_big == null || (this.mUser.photo instanceof TLRPC.TL_userProfilePhotoEmpty))) {
                        z = true;
                    }
                    imageUpdater.openMenu(z, new Runnable() {
                        public final void run() {
                            ChangePersonalInformationActivity.this.lambda$onClick$1$ChangePersonalInformationActivity();
                        }
                    });
                    return;
                }
                ImageUpdater imageUpdater2 = this.mImageUpdater;
                if (this.avatar != null) {
                    z = true;
                }
                imageUpdater2.openMenu(z, new Runnable() {
                    public final void run() {
                        ChangePersonalInformationActivity.this.resetAvatar();
                    }
                });
                return;
            case R.id.containerFemale /*2131296484*/:
                this.mGender = 2;
                changeGender();
                if (this.mShouldShowGenderDialog) {
                    this.mShouldShowGenderDialog = false;
                    needShowAlert(LocaleController.getString(R.string.CannotBeModifiedAfterGenderDetermination), true, (DialogInterface.OnClickListener) null);
                    return;
                }
                return;
            case R.id.containerMale /*2131296488*/:
                this.mGender = 1;
                changeGender();
                if (this.mShouldShowGenderDialog) {
                    this.mShouldShowGenderDialog = false;
                    needShowAlert(LocaleController.getString(R.string.CannotBeModifiedAfterGenderDetermination), true, (DialogInterface.OnClickListener) null);
                    return;
                }
                return;
            case R.id.selectSexParent /*2131297280*/:
                if (this.mShouldShowGenderDialog) {
                    this.mShouldShowGenderDialog = false;
                    needShowAlert(LocaleController.getString(R.string.CannotBeModifiedAfterGenderDetermination), true, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            ChangePersonalInformationActivity.this.showSexSelectDialog();
                        }
                    });
                    return;
                }
                return;
            case R.id.tvDate /*2131297477*/:
                showSelectBirthDialog();
                return;
            default:
                return;
        }
    }

    public /* synthetic */ void lambda$onClick$1$ChangePersonalInformationActivity() {
        MessagesController.getInstance(this.currentAccount).deleteUserPhoto((TLRPC.InputPhoto) null);
        resetAvatar();
    }

    private void check() {
        if (this.isRegisterUser) {
            if (this.accoutName == null) {
                ToastUtils.show((int) R.string.PleaseEnterAccountName);
                finishFragment();
                return;
            } else if (this.pwdHash == null) {
                ToastUtils.show((int) R.string.PaymentPasswordEnter);
                finishFragment();
                return;
            }
        }
        if (TextUtils.isEmpty(this.etNickName.getText() + "") || this.etNickName.getText().toString().trim().replaceAll(" ", "").length() == 0) {
            ToastUtils.show((int) R.string.PleaseFillInNickName);
        } else if (this.isRegisterUser) {
            signUp(this.ip, this.country, this.city);
        } else {
            setUserInformation();
        }
    }

    private void setUserInformation() {
        TLRPCLogin.TL_account_setUserDetail req = new TLRPCLogin.TL_account_setUserDetail();
        req.photo = this.mAvatarFile;
        req.first_name = clearEndOfStrSpace(this.etNickName.getText().toString().trim());
        req.sex = this.mGender;
        req.birthday = this.mBirth;
        AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
        showDialog(progressDialog);
        ConnectionsManager instance = ConnectionsManager.getInstance(this.currentAccount);
        int reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate(progressDialog) {
            private final /* synthetic */ AlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChangePersonalInformationActivity.this.lambda$setUserInformation$3$ChangePersonalInformationActivity(this.f$1, tLObject, tL_error);
            }
        });
        instance.bindRequestToGuid(reqId, this.classGuid);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(reqId) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void onCancel(DialogInterface dialogInterface) {
                ChangePersonalInformationActivity.this.lambda$setUserInformation$4$ChangePersonalInformationActivity(this.f$1, dialogInterface);
            }
        });
    }

    public /* synthetic */ void lambda$setUserInformation$3$ChangePersonalInformationActivity(AlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response, progressDialog) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ AlertDialog f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ChangePersonalInformationActivity.this.lambda$null$2$ChangePersonalInformationActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$2$ChangePersonalInformationActivity(TLRPC.TL_error error, TLObject response, AlertDialog progressDialog) {
        if (error != null || !(response instanceof TLRPC.UserFull)) {
            progressDialog.dismiss();
            parseError(error);
            return;
        }
        updateUserData((TLRPC.UserFull) response, progressDialog);
    }

    public /* synthetic */ void lambda$setUserInformation$4$ChangePersonalInformationActivity(int reqId, DialogInterface dialog) {
        getConnectionsManager().cancelRequest(reqId, false);
    }

    private void signUp(String ip2, String country2, String city2) {
        this.btnDone.setEnabled(false);
        TLRPCLogin.TL_auth_SignUpV1 req = new TLRPCLogin.TL_auth_SignUpV1();
        req.extend = new TLRPC.TL_dataJSON();
        req.phone_uuid = DeviceUtils.getDeviceId(getParentActivity());
        TLRPC.TL_inputFile photo = new TLRPC.TL_inputFile();
        photo.id = 0;
        photo.parts = 0;
        photo.name = "";
        photo.md5_checksum = "";
        req.photo = photo;
        try {
            req.first_name = clearEndOfStrSpace(this.etNickName.getText().toString().trim());
        } catch (Exception e) {
            FileLog.e(getClass().getName(), "sn fn error: ", e);
        }
        req.sex = 0;
        req.birthday = 0;
        req.user_name = this.accoutName;
        req.password_hash = this.pwdHash;
        JSONObject extendJson = new JSONObject();
        if (!TextUtils.isEmpty(this.inviteCode)) {
            extendJson.put("ProxyCode", (Object) this.inviteCode);
        }
        if (!TextUtils.isEmpty(ip2)) {
            extendJson.put("ip", (Object) ip2);
        }
        if (!TextUtils.isEmpty(country2)) {
            extendJson.put("country", (Object) country2);
        }
        if (!TextUtils.isEmpty(city2)) {
            extendJson.put("city", (Object) city2);
        }
        String simpleName = ChangePersonalInformationActivity.class.getSimpleName();
        FileLog.d(simpleName, "extj = " + extendJson.toJSONString());
        req.extend = new TLRPC.TL_dataJSON();
        req.extend.data = extendJson.toJSONString();
        PrintStream printStream = System.out;
        printStream.println("------extj = " + extendJson.toJSONString());
        AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
        showDialog(progressDialog);
        long time = System.currentTimeMillis();
        Log.e(BuildConfig.BUILD_TYPE, "start==" + JSONObject.toJSONString(req));
        ConnectionsManager instance = ConnectionsManager.getInstance(this.currentAccount);
        int reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate(progressDialog, time) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ long f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChangePersonalInformationActivity.this.lambda$signUp$6$ChangePersonalInformationActivity(this.f$1, this.f$2, tLObject, tL_error);
            }
        }, 10);
        instance.bindRequestToGuid(reqId, this.classGuid);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(reqId) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void onCancel(DialogInterface dialogInterface) {
                ChangePersonalInformationActivity.this.lambda$signUp$7$ChangePersonalInformationActivity(this.f$1, dialogInterface);
            }
        });
    }

    public /* synthetic */ void lambda$signUp$6$ChangePersonalInformationActivity(AlertDialog progressDialog, long time, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(progressDialog, time, error, response) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ long f$2;
            private final /* synthetic */ TLRPC.TL_error f$3;
            private final /* synthetic */ TLObject f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r5;
                this.f$4 = r6;
            }

            public final void run() {
                ChangePersonalInformationActivity.this.lambda$null$5$ChangePersonalInformationActivity(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$null$5$ChangePersonalInformationActivity(AlertDialog progressDialog, long time, TLRPC.TL_error error, TLObject response) {
        progressDialog.dismiss();
        Log.e(BuildConfig.BUILD_TYPE, "end==" + (System.currentTimeMillis() - time));
        Log.e(BuildConfig.BUILD_TYPE, "error==" + error);
        if (error != null || !(response instanceof TLRPC.TL_auth_authorization)) {
            parseError(error);
            this.btnDone.setEnabled(true);
            return;
        }
        onAuthSuccess((TLRPC.TL_auth_authorization) response);
    }

    public /* synthetic */ void lambda$signUp$7$ChangePersonalInformationActivity(int reqId, DialogInterface dialog) {
        getConnectionsManager().cancelRequest(reqId, false);
        this.btnDone.setEnabled(true);
    }

    private void changeGender() {
        if (this.mDrawableMale == null) {
            this.mDrawableMale = new MryRoundButtonDrawable();
        }
        if (this.mDrawableFemale == null) {
            this.mDrawableFemale = new MryRoundButtonDrawable();
        }
        int i = this.mGender;
        if (i == 1) {
            this.mDrawableMale.setStrokeData(1, ColorStateList.valueOf(Theme.getColor(Theme.key_windowBackgroundWhiteBlueButton)));
            this.mDrawableMale.setIsRadiusAdjustBounds(true);
            this.mDrawableMale.setBgData(ColorStateList.valueOf(Theme.getColor(Theme.key_actionBarTabUnactiveText)));
            this.containerMale.setBackground(this.mDrawableMale);
            this.ivMale.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlueButton), PorterDuff.Mode.SRC_IN));
            this.tvMale.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
            this.mDrawableFemale.setStrokeData(1, ColorStateList.valueOf(Theme.getColor(Theme.key_windowBackgroundWhiteGrayLine)));
            this.mDrawableFemale.setIsRadiusAdjustBounds(true);
            this.mDrawableFemale.setBgData(ColorStateList.valueOf(Theme.getColor(Theme.key_windowBackgroundWhite)));
            this.containerFemale.setBackground(this.mDrawableFemale);
            this.ivFemale.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6), PorterDuff.Mode.SRC_IN));
            this.tvFemale.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
        } else if (i == 2) {
            this.mDrawableMale.setStrokeData(1, ColorStateList.valueOf(Theme.getColor(Theme.key_windowBackgroundWhiteGrayLine)));
            this.mDrawableMale.setIsRadiusAdjustBounds(true);
            this.mDrawableMale.setBgData(ColorStateList.valueOf(Theme.getColor(Theme.key_windowBackgroundWhite)));
            this.containerMale.setBackground(this.mDrawableMale);
            this.ivMale.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6), PorterDuff.Mode.SRC_IN));
            this.tvMale.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            this.mDrawableFemale.setStrokeData(1, ColorStateList.valueOf(Theme.getColor(Theme.key_windowBackgroundWhiteBlueButton)));
            this.mDrawableFemale.setIsRadiusAdjustBounds(true);
            this.mDrawableFemale.setBgData(ColorStateList.valueOf(Theme.getColor(Theme.key_actionBarTabUnactiveText)));
            this.containerFemale.setBackground(this.mDrawableFemale);
            this.ivFemale.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlueButton), PorterDuff.Mode.SRC_IN));
            this.tvFemale.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
        } else {
            this.mDrawableMale.setStrokeData(1, ColorStateList.valueOf(Theme.getColor(Theme.key_windowBackgroundWhiteGrayLine)));
            this.mDrawableMale.setIsRadiusAdjustBounds(true);
            this.mDrawableMale.setBgData(ColorStateList.valueOf(Theme.getColor(Theme.key_windowBackgroundWhite)));
            this.containerMale.setBackground(this.mDrawableMale);
            this.ivMale.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6), PorterDuff.Mode.SRC_IN));
            this.tvMale.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            this.mDrawableFemale.setStrokeData(1, ColorStateList.valueOf(Theme.getColor(Theme.key_windowBackgroundWhiteGrayLine)));
            this.mDrawableFemale.setIsRadiusAdjustBounds(true);
            this.mDrawableFemale.setBgData(ColorStateList.valueOf(Theme.getColor(Theme.key_windowBackgroundWhite)));
            this.containerFemale.setBackground(this.mDrawableFemale);
            this.ivFemale.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6), PorterDuff.Mode.SRC_IN));
            this.tvFemale.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
        }
        changeBtnEnbale();
    }

    private void startUploadAvatarPhoto() {
        if (this.avatarBig != null) {
            MessagesController.getInstance(this.currentAccount).uploadAvatar(this.avatarBig);
            showAvatarProgress(true, true);
        }
    }

    public void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> photos, boolean notify, int scheduleDate) {
        if (photos != null) {
            this.mCurrentSelectPhotoPath = photos.get(0).path;
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
                ChangePersonalInformationActivity.this.lambda$didUploadPhoto$10$ChangePersonalInformationActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$didUploadPhoto$10$ChangePersonalInformationActivity(TLRPC.InputFile file, TLRPC.PhotoSize smallSize, TLRPC.PhotoSize bigSize) {
        if (file != null) {
            TLRPC.TL_photos_uploadProfilePhoto req = new TLRPC.TL_photos_uploadProfilePhoto();
            req.file = file;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ChangePersonalInformationActivity.this.lambda$null$9$ChangePersonalInformationActivity(tLObject, tL_error);
                }
            });
            return;
        }
        this.avatar = smallSize.location;
        this.avatarBig = bigSize.location;
        this.ivAvatar.setImage(ImageLocation.getForLocal(this.avatar), "87_87", (Drawable) this.avatarDrawable, (Object) null);
        startUploadAvatarPhoto();
    }

    public /* synthetic */ void lambda$null$9$ChangePersonalInformationActivity(TLObject response, TLRPC.TL_error error) {
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
                ChangePersonalInformationActivity.this.lambda$null$8$ChangePersonalInformationActivity();
            }
        });
    }

    public /* synthetic */ void lambda$null$8$ChangePersonalInformationActivity() {
        this.avatar = null;
        this.avatarBig = null;
        updateUserData((TLRPC.UserFull) null, (AlertDialog) null);
        showAvatarProgress(false, true);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_ALL));
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
        UserConfig.getInstance(this.currentAccount).saveConfig(true);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.FileDidUpload) {
            if (args != null && args.length >= 2) {
                this.mAvatarFile = args[1];
                showAvatarProgress(false, true);
                changeBtnEnbale();
                if (!this.isRegisterUser) {
                    TLRPC.User user = this.mUser;
                }
            }
        } else if (id == NotificationCenter.FileDidFailUpload) {
            showAvatarProgress(false, false);
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString(R.string.AppName));
            builder.setMessage(LocaleController.getString(R.string.TipsFailedToUploadAvatarTryAgain));
            builder.setPositiveButton(LocaleController.getString(R.string.Retry), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ChangePersonalInformationActivity.this.lambda$didReceivedNotification$11$ChangePersonalInformationActivity(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ChangePersonalInformationActivity.this.lambda$didReceivedNotification$12$ChangePersonalInformationActivity(dialogInterface, i);
                }
            });
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                public final void onDismiss(DialogInterface dialogInterface) {
                    ChangePersonalInformationActivity.this.lambda$didReceivedNotification$13$ChangePersonalInformationActivity(dialogInterface);
                }
            });
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$11$ChangePersonalInformationActivity(DialogInterface dialog, int which) {
        startUploadAvatarPhoto();
    }

    public /* synthetic */ void lambda$didReceivedNotification$12$ChangePersonalInformationActivity(DialogInterface dialog, int which) {
        resetAvatar();
    }

    public /* synthetic */ void lambda$didReceivedNotification$13$ChangePersonalInformationActivity(DialogInterface dialog) {
        resetAvatar();
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        ImageUpdater imageUpdater = this.mImageUpdater;
        if (imageUpdater != null) {
            imageUpdater.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResultFragment(requestCode, permissions, grantResults);
        if (grantResults != null && grantResults[0] == 0) {
            if (requestCode == 19) {
                this.mImageUpdater.openCamera();
            } else if (requestCode == 4) {
                this.mImageUpdater.openGallery();
            }
        }
    }

    /* access modifiers changed from: private */
    public void resetAvatar() {
        this.mAvatarFile = null;
        this.avatar = null;
        this.avatarBig = null;
        showAvatarProgress(false, true);
        this.ivAvatar.setImage((ImageLocation) null, (String) null, (Drawable) this.avatarDrawable, (Object) null);
        this.containerCamera.setVisibility(0);
        changeBtnEnbale();
    }

    private void showAvatarProgress(final boolean show, boolean animated) {
        if (this.containerCamera != null) {
            AnimatorSet animatorSet = this.avatarAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.avatarAnimation = null;
            }
            if (animated) {
                this.avatarAnimation = new AnimatorSet();
                if (show) {
                    this.ivAvatarProgress.setVisibility(0);
                    this.avatarAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.containerCamera, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.ivAvatarProgress, View.ALPHA, new float[]{1.0f})});
                } else {
                    this.containerCamera.setVisibility(0);
                    this.avatarAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.containerCamera, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.ivAvatarProgress, View.ALPHA, new float[]{0.0f})});
                }
                this.avatarAnimation.setDuration(180);
                this.avatarAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (ChangePersonalInformationActivity.this.avatarAnimation != null && ChangePersonalInformationActivity.this.containerCamera != null) {
                            if (show) {
                                ChangePersonalInformationActivity.this.containerCamera.setVisibility(4);
                            } else {
                                ChangePersonalInformationActivity.this.ivAvatarProgress.setVisibility(4);
                            }
                            AnimatorSet unused = ChangePersonalInformationActivity.this.avatarAnimation = null;
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        AnimatorSet unused = ChangePersonalInformationActivity.this.avatarAnimation = null;
                    }
                });
                this.avatarAnimation.start();
            } else if (show) {
                this.containerCamera.setAlpha(1.0f);
                this.containerCamera.setVisibility(4);
                this.ivAvatarProgress.setAlpha(1.0f);
                this.ivAvatarProgress.setVisibility(0);
            } else {
                this.containerCamera.setAlpha(1.0f);
                this.containerCamera.setVisibility(0);
                this.ivAvatarProgress.setAlpha(0.0f);
                this.ivAvatarProgress.setVisibility(4);
            }
        }
    }

    private void onAuthSuccess(TLRPC.TL_auth_authorization res) {
        this.mIsLoginSuccess = true;
        ConnectionsManager.getInstance(this.currentAccount).setUserId(res.user.id);
        UserConfig.getInstance(this.currentAccount).clearConfig();
        MessagesController.getInstance(this.currentAccount).cleanup();
        UserConfig.getInstance(this.currentAccount).syncContacts = false;
        UserConfig.getInstance(this.currentAccount).setCurrentUser(res.user);
        UserConfig.getInstance(this.currentAccount).saveConfig(true);
        MessagesStorage.getInstance(this.currentAccount).cleanup(true);
        ArrayList<TLRPC.User> users = new ArrayList<>();
        users.add(res.user);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(users, (ArrayList<TLRPC.Chat>) null, true, true);
        MessagesController.getInstance(this.currentAccount).putUser(res.user, false);
        ContactsController.getInstance(this.currentAccount).checkAppAccount();
        MessagesController.getInstance(this.currentAccount).checkProxyInfo(true);
        ConnectionsManager.getInstance(this.currentAccount).updateDcSettings();
        clearCurrentState();
        if (getParentActivity() instanceof LaunchActivity) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.appDidLogIn, new Object[0]);
            if (this.newAccount) {
                this.newAccount = false;
                ((LaunchActivity) getParentActivity()).switchToAccount(this.currentAccount, true);
                finishFragment();
                return;
            }
            presentFragment(new IndexActivity(), true);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
        } else if (getParentActivity() instanceof ExternalActionActivity) {
            ((ExternalActionActivity) getParentActivity()).onFinishLogin();
        }
    }

    private void updateUserData(TLRPC.UserFull userFull, AlertDialog progressDialog) {
        TLRPC.User user;
        TLRPC.User newUser;
        if (userFull != null && (user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()))) != null && (newUser = userFull.user) != null) {
            if (user != null) {
                user.first_name = newUser.first_name;
                user.last_name = newUser.last_name;
                user.photo = newUser.photo;
                if (user.photo != null) {
                    if (!(user.photo.photo_small == null || this.avatar == null)) {
                        FileLoader.getPathToAttach(this.avatar, true).renameTo(FileLoader.getPathToAttach(user.photo.photo_small, true));
                        ImageLoader.getInstance().replaceImageInCache(this.avatar.volume_id + "_" + this.avatar.local_id + "@50_50", user.photo.photo_small.volume_id + "_" + user.photo.photo_small.local_id + "@50_50", ImageLocation.getForUser(user, false), true);
                    }
                    if (!(user.photo.photo_big == null || this.avatarBig == null)) {
                        FileLoader.getPathToAttach(this.avatarBig, true).renameTo(FileLoader.getPathToAttach(user.photo.photo_big, true));
                    }
                }
                MessagesStorage.getInstance(this.currentAccount).clearUserPhotos(user.id);
                ArrayList<TLRPC.User> users = new ArrayList<>();
                users.add(user);
                MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(users, (ArrayList<TLRPC.Chat>) null, false, true);
            }
            if (user == null || !(user.photo instanceof TLRPC.TL_userProfilePhoto)) {
                ToastUtils.show((int) R.string.SetupSuccess);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_ALL));
                UserConfig.getInstance(this.currentAccount).saveConfig(true);
                finishFragment();
                return;
            }
            saveImage(progressDialog);
        }
    }

    private void saveImage(AlertDialog progressDialog) {
        Utilities.stageQueue.postRunnable(new Runnable(progressDialog) {
            private final /* synthetic */ AlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ChangePersonalInformationActivity.this.lambda$saveImage$15$ChangePersonalInformationActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$saveImage$15$ChangePersonalInformationActivity(AlertDialog progressDialog) {
        while (this.ivAvatar.getImageReceiver().getBitmap() == null) {
            try {
                Thread.sleep(10);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        Bitmap bitmap = this.ivAvatar.getImageReceiver().getBitmap();
        File file = new File(AndroidUtilities.getCacheDir().getPath() + File.separator + "user_avatar.jpg");
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream out = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        out.flush();
        out.close();
        AndroidUtilities.runOnUIThread(new Runnable(progressDialog) {
            private final /* synthetic */ AlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ChangePersonalInformationActivity.this.lambda$null$14$ChangePersonalInformationActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$14$ChangePersonalInformationActivity(AlertDialog progressDialog) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        ToastUtils.show((int) R.string.SetupSuccess);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_ALL));
        UserConfig.getInstance(this.currentAccount).saveConfig(true);
        finishFragment();
    }

    private String clearEndOfStrSpace(String str) {
        if (str == null || str.length() <= 0 || str.charAt(str.length() - 1) != ' ') {
            return str;
        }
        return clearEndOfStrSpace(str.substring(0, str.length() - 1));
    }

    private void parseError(TLRPC.TL_error error) {
        if (error.text.contains("PHONE_NUMBER_INVALID")) {
            needShowAlert(LocaleController.getString(R.string.InvalidPhoneNumber), true, (DialogInterface.OnClickListener) null);
        } else if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
            needShowAlert(LocaleController.getString(R.string.InvalidCode), true, (DialogInterface.OnClickListener) null);
            finishFragment();
        } else if (error.text.contains("FIRSTNAME_INVALID")) {
            needShowAlert(LocaleController.getString(R.string.InvalidName), true, (DialogInterface.OnClickListener) null);
        } else if (error.text.contains("LASTNAME_INVALID")) {
            needShowAlert(LocaleController.getString(R.string.InvalidNickname), true, (DialogInterface.OnClickListener) null);
        } else if (error.text.contains("FIRSTNAME_LASTNAME_EMPTY")) {
            needShowAlert(LocaleController.getString(R.string.EmptyNameTips), true, (DialogInterface.OnClickListener) null);
        } else if (error.text.contains("PHOTO_CHECK_FEAILED")) {
            needShowAlert(LocaleController.getString(R.string.FailedToSetAvatarVerification), true, (DialogInterface.OnClickListener) null);
        } else if (error.text.contains("ALREDY_CHANGE")) {
            needShowAlert(LocaleController.getString(R.string.InformationModified), true, (DialogInterface.OnClickListener) null);
        } else if (error.text.contains("NAME_TOO_LONG")) {
            needShowAlert(LocaleController.getString(R.string.NickNameIsTooLongAndPleaseTryAgainAfterModified), true, (DialogInterface.OnClickListener) null);
        } else if (error.text.contains("PHONE_NUMBER_OCCUPIED")) {
            needShowAlert(LocaleController.formatString("ChangePhoneNumberOccupied", R.string.ChangePhoneNumberOccupied, this.accoutName), true, (DialogInterface.OnClickListener) null);
        } else if (error.text.contains("PROXYCODE_INVALID")) {
            needShowAlert(LocaleController.getString("ProxyCodeInvalid", R.string.ProxyCodeInvalid), true, (DialogInterface.OnClickListener) null);
        } else if (error.text.contains("IPORDE_LIMIT")) {
            needShowAlert(LocaleController.getString("IpOrDeLimit", R.string.IpOrDeLimit), true, (DialogInterface.OnClickListener) null);
        } else if (error.text.equals("INTERNAL")) {
            needShowAlert(LocaleController.getString("InternalError", R.string.InternalError), true, (DialogInterface.OnClickListener) null);
        } else if (error.text.contains("FLOOD_WAIT")) {
            needShowAlert(LocaleController.getString(R.string.FloodWait), true, (DialogInterface.OnClickListener) null);
        } else if (error.text.contains("UUID_REG_ERROR")) {
            needShowAlert(LocaleController.getString(R.string.NonRegisteredDeviceLogin), true, (DialogInterface.OnClickListener) null);
        } else {
            needShowAlert(LocaleController.getString(R.string.OperationFailedPleaseTryAgain), true, (DialogInterface.OnClickListener) null);
        }
    }

    private void needShowAlert(String text, boolean canCancel, DialogInterface.OnClickListener onClickListener) {
        if (text != null && getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString(R.string.AppName));
            builder.setMessage(text);
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), onClickListener);
            AlertDialog dialog = builder.create();
            dialog.setCancelable(canCancel);
            dialog.setCanceledOnTouchOutside(canCancel);
            showDialog(dialog);
        }
    }

    private void loadCurrentState() {
        MryEditText mryEditText;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0);
        String nickName = preferences.getString(getCurrentSpKey("user_name"), (String) null);
        if (!(nickName == null || (mryEditText = this.etNickName) == null)) {
            mryEditText.setText(nickName);
        }
        int i = preferences.getInt(getCurrentSpKey("user_birth"), 0);
        this.mBirth = i;
        MryTextView mryTextView = this.tvSelectDateOfBirth;
        if (!(mryTextView == null || i == 0)) {
            mryTextView.setText(TimeUtils.millis2String(((long) this.mBirth) * 1000, "yyyy-MM-dd") + "");
        }
        this.mGender = preferences.getInt(getCurrentSpKey("user_gender"), 0);
        long user_avatar_id = preferences.getLong(getCurrentSpKey("user_avatar_id"), -1);
        int user_avatar_parts = preferences.getInt(getCurrentSpKey("user_avatar_parts"), -1);
        String user_avatar_name = preferences.getString(getCurrentSpKey("user_avatar_name"), (String) null);
        String user_avatar_md5_checksum = preferences.getString(getCurrentSpKey("user_avatar_md5_checksum"), (String) null);
        String string = preferences.getString(getCurrentSpKey("current_select_photo_path"), (String) null);
        this.mCurrentSelectPhotoPath = string;
        if (user_avatar_id != -1 && user_avatar_parts != -1 && user_avatar_name != null && user_avatar_md5_checksum != null && string != null) {
            if (this.mAvatarFile == null) {
                this.mAvatarFile = new TLRPC.TL_inputFile();
            }
            this.mAvatarFile.id = user_avatar_id;
            this.mAvatarFile.parts = user_avatar_parts;
            this.mAvatarFile.name = user_avatar_name;
            this.mAvatarFile.md5_checksum = user_avatar_md5_checksum;
        }
    }

    private void saveCurrentState() {
        if (!this.mIsLoginSuccess && this.pwdHash != null) {
            SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0).edit();
            if (this.etNickName != null) {
                if (!TextUtils.isEmpty(this.etNickName.getText() + "")) {
                    editor.putString(getCurrentSpKey("user_name"), this.etNickName.getText().toString().trim());
                }
            }
            if (this.mBirth != 0) {
                editor.putInt(getCurrentSpKey("user_birth"), this.mBirth);
            }
            if (this.mGender != 0) {
                editor.putInt(getCurrentSpKey("user_gender"), this.mGender);
            }
            if (!(this.mAvatarFile == null || this.mCurrentSelectPhotoPath == null)) {
                editor.putLong(getCurrentSpKey("user_avatar_id"), this.mAvatarFile.id);
                editor.putInt(getCurrentSpKey("user_avatar_parts"), this.mAvatarFile.parts);
                editor.putString(getCurrentSpKey("user_avatar_name"), this.mAvatarFile.name);
                editor.putString(getCurrentSpKey("user_avatar_md5_checksum"), this.mAvatarFile.md5_checksum);
                editor.putString(getCurrentSpKey("current_select_photo_path"), this.mCurrentSelectPhotoPath);
            }
            editor.apply();
        }
    }

    private void clearCurrentState() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0).edit();
        editor.clear();
        editor.apply();
    }

    private String getCurrentSpKey(String key) {
        if (this.accoutName == null) {
            return key;
        }
        return this.accoutName + key;
    }

    private void showSelectBirthDialog() {
        if (this.mTimePickerBuilder == null) {
            this.mTimePickerBuilder = TimeWheelPickerDialog.getDefaultBuilder(getParentActivity(), new OnTimeSelectListener() {
                public final void onTimeSelect(Date date, View view) {
                    ChangePersonalInformationActivity.this.lambda$showSelectBirthDialog$16$ChangePersonalInformationActivity(date, view);
                }
            });
        }
        if (!TextUtils.isEmpty(this.tvSelectDateOfBirth.getText() + "")) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(TimeUtils.string2Date(this.tvSelectDateOfBirth.getText().toString().trim(), LocaleController.getString(R.string.formatterStandardYearMonthDay)));
            this.mTimePickerBuilder.setDate(calendar);
        } else {
            this.mTimePickerBuilder.setDate(Calendar.getInstance());
        }
        showDialog(this.mTimePickerBuilder.build());
    }

    public /* synthetic */ void lambda$showSelectBirthDialog$16$ChangePersonalInformationActivity(Date date, View v) {
        String dateStr = TimeUtils.date2String(date, "yyyy-MM-dd");
        this.tvSelectDateOfBirth.setText(dateStr);
        this.mBirth = (int) (TimeUtils.string2Millis(dateStr, "yyyy-MM-dd") / 1000);
        dismissCurrentDialog();
        changeBtnEnbale();
    }

    public void showSexSelectDialog() {
        if (this.mGenderPickerBulder == null) {
            List<String> data = new ArrayList<>(Arrays.asList(new String[]{LocaleController.getString(R.string.PassportFemale), LocaleController.getString(R.string.PassportMale)}));
            OptionsWheelPickerDialog.Builder<String> defaultBuilder = OptionsWheelPickerDialog.getDefaultBuilder(getParentActivity(), new OnOptionsSelectListener() {
                public final void onOptionsSelect(int i, int i2, int i3, View view) {
                    ChangePersonalInformationActivity.this.lambda$showSexSelectDialog$17$ChangePersonalInformationActivity(i, i2, i3, view);
                }
            });
            this.mGenderPickerBulder = defaultBuilder;
            defaultBuilder.setPicker(data);
        }
        showDialog(this.mGenderPickerBulder.build());
    }

    public /* synthetic */ void lambda$showSexSelectDialog$17$ChangePersonalInformationActivity(int options1, int options2, int options3, View v) {
        int i = options1 == 0 ? 2 : 1;
        this.mGender = i;
        this.tvSelectSex.setText(LocaleController.getString(i == 2 ? R.string.PassportFemale : R.string.PassportMale));
        changeBtnEnbale();
    }

    /* access modifiers changed from: private */
    public void changeBtnEnbale() {
        MryEditText mryEditText;
        if (this.btnDone != null && (mryEditText = this.etNickName) != null) {
            boolean tag = true;
            if (this.isRegisterUser) {
                if (!(this.accoutName == null || this.pwdHash == null || (mryEditText != null && mryEditText.getText() != null && this.etNickName.length() != 0))) {
                    tag = false;
                }
            } else if (this.mUser != null && (mryEditText == null || mryEditText.getText() == null || this.etNickName.length() == 0)) {
                tag = false;
            }
            this.btnDone.setEnabled(tag);
        }
    }

    /* access modifiers changed from: private */
    public boolean back() {
        if (!this.mCanGoBack) {
            return true;
        }
        if (this.mAvatarFile == null && this.mBirth == 0 && this.mGender == 0) {
            if (TextUtils.isEmpty(this.etNickName.getText() + "")) {
                return false;
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setMessage(LocaleController.getString("AreYouSureRegistration", R.string.AreYouSureRegistration));
        builder.setNegativeButton(LocaleController.getString("Stop", R.string.Stop), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                ChangePersonalInformationActivity.this.lambda$back$18$ChangePersonalInformationActivity(dialogInterface, i);
            }
        });
        builder.setPositiveButton(LocaleController.getString("Continue", R.string.Continue), (DialogInterface.OnClickListener) null);
        showDialog(builder.create());
        return true;
    }

    public /* synthetic */ void lambda$back$18$ChangePersonalInformationActivity(DialogInterface dialogInterface, int i) {
        saveCurrentState();
        finishFragment();
    }

    /* access modifiers changed from: protected */
    public AnimatorSet onCustomTransitionAnimation(final boolean isOpen, final Runnable callback) {
        AnimatorSet set = new AnimatorSet();
        final int parentHeight = getParentLayout().getHeight();
        final int actionBarHeight = this.actionBar.getHeight();
        if (isOpen) {
            set.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.actionBar, View.TRANSLATION_Y, new float[]{(float) parentHeight, 0.0f}), ObjectAnimator.ofFloat(this.actionBar, View.ALPHA, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.fragmentView, View.TRANSLATION_Y, new float[]{(float) (parentHeight + actionBarHeight), 0.0f}), ObjectAnimator.ofFloat(this.fragmentView, View.ALPHA, new float[]{0.0f, 1.0f})});
        } else {
            set.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.actionBar, View.TRANSLATION_Y, new float[]{0.0f, (float) parentHeight}), ObjectAnimator.ofFloat(this.actionBar, View.ALPHA, new float[]{1.0f, 0.0f}), ObjectAnimator.ofFloat(this.fragmentView, View.TRANSLATION_Y, new float[]{0.0f, (float) (parentHeight + actionBarHeight)}), ObjectAnimator.ofFloat(this.fragmentView, View.ALPHA, new float[]{1.0f, 0.0f})});
        }
        set.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (isOpen) {
                    ChangePersonalInformationActivity.this.actionBar.setTranslationY((float) parentHeight);
                    ChangePersonalInformationActivity.this.fragmentView.setTranslationY((float) (parentHeight + actionBarHeight));
                }
            }
        });
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.setDuration(300);
        set.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                Runnable runnable = callback;
                if (runnable != null) {
                    runnable.run();
                }
            }
        });
        set.start();
        return set;
    }

    public boolean onBackPressed() {
        if (back()) {
            return false;
        }
        return super.onBackPressed();
    }

    public void saveSelfArgs(Bundle args) {
        ImageUpdater imageUpdater = this.mImageUpdater;
        if (imageUpdater != null && imageUpdater.currentPicturePath != null) {
            args.putString("path", this.mImageUpdater.currentPicturePath);
        }
    }

    public void restoreSelfArgs(Bundle args) {
        ImageUpdater imageUpdater = this.mImageUpdater;
        if (imageUpdater != null) {
            imageUpdater.currentPicturePath = args.getString("path");
        }
    }

    public boolean canBeginSlide() {
        return false;
    }
}
