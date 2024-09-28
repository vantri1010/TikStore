package im.bclpbkiauv.ui.hui.discovery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.king.zxing.CaptureHelper;
import com.king.zxing.OnCaptureCallback;
import com.king.zxing.ViewfinderView;
import com.king.zxing.util.CodeUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.messenger.browser.Browser;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.ProfileActivity;
import im.bclpbkiauv.ui.WebviewActivity;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hui.chats.NewProfileActivity;
import im.bclpbkiauv.ui.hui.contacts.AddContactsInfoActivity;
import im.bclpbkiauv.ui.hui.mine.QrCodeActivity;
import im.bclpbkiauv.ui.hviews.MryTextView;

public class QrScanActivity extends BaseFragment implements OnCaptureCallback {
    private static final int GALLERY_REQUEST_CODE = 2;
    private ImageView ivBackView;
    private ImageView ivFlash;
    private ImageView ivGallery;
    private LinearLayout llMyQrCode;
    private CaptureHelper mCaptureHelper;
    private Context mContext;
    private SurfaceView surfaceView;
    private TextView tvFlash;
    private TextView tvGallery;
    private ViewfinderView viewfinderView;

    public boolean onFragmentCreate() {
        return super.onFragmentCreate();
    }

    public void onResume() {
        super.onResume();
        this.mCaptureHelper.onResume();
    }

    public void onPause() {
        super.onPause();
        this.mCaptureHelper.onPause();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        this.mCaptureHelper.onDestroy();
    }

    private void requestCameraPermissions() {
        if (Build.VERSION.SDK_INT >= 23 && getParentActivity().checkSelfPermission("android.permission.CAMERA") != 0) {
            try {
                getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 20);
            } catch (Throwable th) {
            }
        }
    }

    public View createView(Context context) {
        this.mContext = context;
        requestCameraPermissions();
        this.actionBar.setAddToContainer(false);
        this.fragmentView = LayoutInflater.from(this.mContext).inflate(R.layout.activity_qr_code_scan_layout, (ViewGroup) null, false);
        this.fragmentView.setOnTouchListener(new View.OnTouchListener() {
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return QrScanActivity.this.lambda$createView$0$QrScanActivity(view, motionEvent);
            }
        });
        initView();
        return this.fragmentView;
    }

    public /* synthetic */ boolean lambda$createView$0$QrScanActivity(View v, MotionEvent event) {
        this.mCaptureHelper.onTouchEvent(event);
        return true;
    }

    private void initView() {
        this.surfaceView = (SurfaceView) this.fragmentView.findViewById(R.id.surfaceView);
        ViewfinderView viewfinderView2 = (ViewfinderView) this.fragmentView.findViewById(R.id.viewfinderView);
        this.viewfinderView = viewfinderView2;
        viewfinderView2.setLabelText(LocaleController.getString("PutQrCodeInbox", R.string.PutQrCodeInbox));
        this.ivFlash = (ImageView) this.fragmentView.findViewById(R.id.flash_switch);
        this.tvFlash = (TextView) this.fragmentView.findViewById(R.id.tvFlash);
        this.ivGallery = (ImageView) this.fragmentView.findViewById(R.id.ivGallery);
        this.tvGallery = (TextView) this.fragmentView.findViewById(R.id.tvGallery);
        this.llMyQrCode = (LinearLayout) this.fragmentView.findViewById(R.id.ll_my_qr_code);
        ((MryTextView) this.fragmentView.findViewById(R.id.tv_my_qrcode)).setText(LocaleController.getString("MyQrCode", R.string.MyQRCode));
        this.ivFlash.setImageDrawable(Theme.createSimpleSelectorDrawable(getParentActivity(), R.mipmap.icon_flash, Theme.getColor(Theme.key_windowBackgroundWhite), Theme.getColor(Theme.key_windowBackgroundWhiteBlueIcon)));
        this.tvFlash.setTextColor(Theme.createColorStateList(Theme.getColor(Theme.key_windowBackgroundWhite), Theme.getColor(Theme.key_windowBackgroundWhiteBlueIcon)));
        this.tvFlash.setText(LocaleController.getString("FlashOn", R.string.FlashOn));
        this.ivGallery.setImageDrawable(Theme.createSimpleSelectorDrawable(getParentActivity(), R.mipmap.icon_album, Theme.getColor(Theme.key_windowBackgroundWhite), Theme.getColor(Theme.key_windowBackgroundWhiteBlueIcon)));
        this.tvGallery.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.tvGallery.setText(LocaleController.getString("Gallery", R.string.Gallery));
        ImageView imageView = (ImageView) this.fragmentView.findViewById(R.id.ivBack);
        this.ivBackView = imageView;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        layoutParams.setMargins(0, AndroidUtilities.statusBarHeight, 0, 0);
        this.ivBackView.setLayoutParams(layoutParams);
        this.ivBackView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                QrScanActivity.this.lambda$initView$1$QrScanActivity(view);
            }
        });
        MryTextView tvTitle = (MryTextView) this.fragmentView.findViewById(R.id.tv_title);
        RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) tvTitle.getLayoutParams();
        layoutParams1.setMargins(0, AndroidUtilities.statusBarHeight, 0, 0);
        tvTitle.setLayoutParams(layoutParams1);
        CaptureHelper captureHelper = new CaptureHelper((Activity) getParentActivity(), this.surfaceView, this.viewfinderView, (View) null);
        this.mCaptureHelper = captureHelper;
        captureHelper.setOnCaptureCallback(this);
        this.mCaptureHelper.vibrate(true).playBeep(true).fullScreenScan(true).supportVerticalCode(false).supportLuminanceInvert(true).continuousScan(false);
        this.mCaptureHelper.onCreate();
        RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) this.llMyQrCode.getLayoutParams();
        int widthPixels = getParentActivity().getResources().getDisplayMetrics().widthPixels;
        int heightPixels = getParentActivity().getResources().getDisplayMetrics().heightPixels;
        layoutParams2.topMargin = (int) (((((float) heightPixels) + (((float) Math.min(widthPixels, heightPixels)) * 0.625f)) / 2.0f) + ((float) AndroidUtilities.dp(55.0f)));
        this.llMyQrCode.setLayoutParams(layoutParams2);
        this.ivFlash.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                QrScanActivity.this.clickFlash(view);
            }
        });
        this.ivGallery.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                QrScanActivity.this.goGallery(view);
            }
        });
        this.llMyQrCode.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                QrScanActivity.this.goMyQrCode(view);
            }
        });
    }

    public /* synthetic */ void lambda$initView$1$QrScanActivity(View v) {
        finishFragment();
    }

    private void offFlash() {
        Camera camera = this.mCaptureHelper.getCameraManager().getOpenCamera().getCamera();
        Camera.Parameters parameters = camera.getParameters();
        parameters.setFlashMode("off");
        camera.setParameters(parameters);
    }

    private void openFlash() {
        Camera camera = this.mCaptureHelper.getCameraManager().getOpenCamera().getCamera();
        Camera.Parameters parameters = camera.getParameters();
        parameters.setFlashMode("torch");
        camera.setParameters(parameters);
    }

    /* access modifiers changed from: private */
    public void clickFlash(View v) {
        if (v.isSelected()) {
            offFlash();
            v.setSelected(false);
            this.tvFlash.setSelected(false);
            this.tvFlash.setText(LocaleController.getString("FlashOn", R.string.FlashOn));
            return;
        }
        openFlash();
        v.setSelected(true);
        this.tvFlash.setSelected(true);
        this.tvFlash.setText(LocaleController.getString("FlashOff", R.string.FlashOff));
    }

    /* access modifiers changed from: private */
    public void goGallery(View v) {
        if (Build.VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.PICK");
            intent.setType("image/*");
            startActivityForResult(intent, 2);
            return;
        }
        getParentActivity().requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
    }

    /* access modifiers changed from: private */
    public void goMyQrCode(View v) {
        presentFragment(new QrCodeActivity(getUserConfig().getClientUserId()));
    }

    public boolean onBackPressed() {
        finishFragment(false);
        return false;
    }

    private void getUserInfo(TLRPC.User user) {
        TLRPC.TL_users_getFullUser req = new TLRPC.TL_users_getFullUser();
        req.id = getMessagesController().getInputUser(user);
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(req, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                QrScanActivity.this.lambda$getUserInfo$3$QrScanActivity(tLObject, tL_error);
            }
        }), this.classGuid);
    }

    public /* synthetic */ void lambda$getUserInfo$3$QrScanActivity(TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            AndroidUtilities.runOnUIThread(new Runnable(response) {
                private final /* synthetic */ TLObject f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    QrScanActivity.this.lambda$null$2$QrScanActivity(this.f$1);
                }
            });
        } else {
            ToastUtils.show((int) R.string.NoUsernameFound);
        }
    }

    public /* synthetic */ void lambda$null$2$QrScanActivity(TLObject response) {
        TLRPC.UserFull userFull = (TLRPC.UserFull) response;
        getMessagesController().putUser(userFull.user, false);
        if (userFull.user != null) {
            if (userFull.user.self || userFull.user.contact) {
                Bundle bundle = new Bundle();
                bundle.putInt("user_id", userFull.user.id);
                presentFragment(new NewProfileActivity(bundle), true);
                return;
            }
            Bundle bundle2 = new Bundle();
            bundle2.putInt("from_type", 1);
            presentFragment(new AddContactsInfoActivity(bundle2, userFull.user), true);
        }
    }

    private void getChatInfo(TLRPC.Chat chat) {
        TLRPC.TL_channels_getFullChannel req = new TLRPC.TL_channels_getFullChannel();
        req.channel = MessagesController.getInputChannel(chat);
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(req, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                QrScanActivity.this.lambda$getChatInfo$5$QrScanActivity(tLObject, tL_error);
            }
        }), this.classGuid);
    }

    public /* synthetic */ void lambda$getChatInfo$5$QrScanActivity(TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            AndroidUtilities.runOnUIThread(new Runnable(response) {
                private final /* synthetic */ TLObject f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    QrScanActivity.this.lambda$null$4$QrScanActivity(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$4$QrScanActivity(TLObject response) {
        TLRPC.TL_messages_chatFull res = (TLRPC.TL_messages_chatFull) response;
        getMessagesController().putChats(res.chats, false);
        Bundle bundle = new Bundle();
        bundle.putInt("chat_id", res.full_chat.id);
        ProfileActivity profileActivity = new ProfileActivity(bundle);
        profileActivity.setChatInfo(res.full_chat);
        presentFragment(profileActivity, true);
    }

    public void parseQRCodeResult(String result) {
        String result2;
        String preStr = getMessagesController().sharePrefix + "&Key=";
        if (result.startsWith(preStr) || result.startsWith("https://m12345.com") || result.startsWith("http://m12345.com")) {
            if (result.startsWith(preStr)) {
                String ret = new String(Base64.decode(result.substring(preStr.length()).replace("%3D", "="), 0));
                String[] split = ret.split("#");
                String pUid = split[0].split("=")[1];
                String hash = split[1].split("=")[1];
                if (ret.contains("Uname")) {
                    getMessagesController().openByUserName(split[2].split("=")[1], (BaseFragment) this, 1, true);
                    return;
                }
                TLRPC.User user = new TLRPC.TL_user();
                try {
                    user.id = Integer.parseInt(pUid);
                    user.access_hash = Long.parseLong(hash);
                    getUserInfo(user);
                } catch (NumberFormatException e) {
                    FileLog.e("parse qr code err:" + e);
                }
            } else {
                if (result.startsWith("http://m12345.com")) {
                    result2 = result.substring("http://m12345.com".length());
                } else {
                    result2 = result.substring("https://m12345.com".length());
                }
                boolean isGroup = result2.startsWith("/g/");
                String ret2 = new String(Base64.decode(result2.substring(result2.lastIndexOf("/") + 1), 0));
                if (isGroup) {
                    getMessagesController().openByUserName(ret2.substring(ret2.lastIndexOf("/") + 1), (BaseFragment) this, 1, true);
                    return;
                }
                String[] split2 = ret2.substring(0, ret2.length() - 4).split("&", 2);
                String uid = split2[0];
                String uhash = split2[1];
                TLRPC.User user2 = new TLRPC.TL_user();
                try {
                    user2.id = Integer.parseInt(uid);
                    user2.access_hash = Long.parseLong(uhash);
                    getUserInfo(user2);
                } catch (NumberFormatException e2) {
                    FileLog.e("parse qr code err:" + e2);
                }
            }
        } else if (!result.startsWith("https://m12345.com/authtoken/")) {
            if (!URLUtil.isNetworkUrl(result)) {
                presentFragment(new QrScanResultActivity(result), true);
            } else if (SharedConfig.customTabs) {
                presentFragment(new WebviewActivity(result, (String) null), true);
            } else {
                Browser.openUrl((Context) getParentActivity(), result);
            }
        }
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        Uri uri;
        if (resultCode == -1 && requestCode != 20 && requestCode == 2 && (uri = data.getData()) != null) {
            String result = CodeUtils.parseQRCode(AndroidUtilities.getPath(uri));
            if (result != null) {
                parseQRCodeResult(result);
            } else {
                ToastUtils.show((int) R.string.NoQRCode);
            }
        }
    }

    public boolean onResultCallback(String result) {
        parseQRCodeResult(result);
        return true;
    }
}
