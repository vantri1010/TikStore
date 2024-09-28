package im.bclpbkiauv.ui.hui.mine;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import com.blankj.utilcode.util.ImageUtils;
import com.king.zxing.util.CodeUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCContacts;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hviews.MryImageView;
import im.bclpbkiauv.ui.hviews.MryTextView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class QrCodeActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private ImageView QRCodeImage;
    private BackupImageView avatarImage;
    private MryTextView btnSave;
    private MryTextView btnShare;
    private ViewTreeObserver.OnGlobalLayoutListener changeNameWidthListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        public void onGlobalLayout() {
            int allw = QrCodeActivity.this.tvNameParent.getMeasuredWidth();
            if (allw > 0 && QrCodeActivity.this.nameView.getMeasuredWidth() + QrCodeActivity.this.ivGender.getMeasuredWidth() > allw) {
                QrCodeActivity.this.fragmentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                ViewGroup.LayoutParams lpName = QrCodeActivity.this.nameView.getLayoutParams();
                lpName.width = (allw - QrCodeActivity.this.ivGender.getMeasuredWidth()) - AndroidUtilities.dp(10.0f);
                QrCodeActivity.this.nameView.setLayoutParams(lpName);
            }
        }
    };
    private TLRPC.Chat chat;
    private View iconClose;
    /* access modifiers changed from: private */
    public ImageView ivGender;
    private MryImageView ivQrCode;
    private LinearLayout llContainer;
    private Context mContext;
    /* access modifiers changed from: private */
    public MryTextView nameView;
    private MryTextView otherView;
    private ProgressBar progressBar;
    private String ret;
    private ConstraintLayout rlContainer;
    /* access modifiers changed from: private */
    public View tvNameParent;
    private MryTextView tvQRCodeText;
    private TLRPC.User user;
    private TLRPCContacts.CL_userFull_v1 userFull;
    private int userId;
    private TLRPC.UserFull userInfo;

    public QrCodeActivity(int userId2) {
        this.userId = userId2;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        TLRPC.User user2 = getMessagesController().getUser(Integer.valueOf(this.userId));
        this.user = user2;
        if (user2 == null) {
            return false;
        }
        TLRPC.UserFull full = getMessagesController().getUserFull(this.userId);
        if (full instanceof TLRPCContacts.CL_userFull_v1) {
            this.userFull = (TLRPCContacts.CL_userFull_v1) full;
        }
        getMessagesController().loadFullUser(this.userId, this.classGuid, true);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.userFullInfoDidLoad);
        return true;
    }

    public void onFragmentDestroy() {
        if (this.fragmentView != null) {
            this.fragmentView.getViewTreeObserver().removeOnGlobalLayoutListener(this.changeNameWidthListener);
        }
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.userFullInfoDidLoad);
    }

    public View createView(Context context) {
        this.mContext = context;
        this.actionBar.setAddToContainer(false);
        this.fragmentView = View.inflate(context, R.layout.activity_qr_code_layout, (ViewGroup) null);
        initActionbar();
        initCodeContainer();
        createQRCode();
        return this.fragmentView;
    }

    private void initActionbar() {
        View findViewById = this.fragmentView.findViewById(R.id.img_close);
        this.iconClose = findViewById;
        findViewById.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                QrCodeActivity.this.finishFragment();
            }
        });
        FrameLayout flTitleBarContainer = (FrameLayout) this.fragmentView.findViewById(R.id.fl_title_bar_container);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) flTitleBarContainer.getLayoutParams();
        layoutParams.height = ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight;
        flTitleBarContainer.setLayoutParams(layoutParams);
        flTitleBarContainer.setPadding(0, AndroidUtilities.statusBarHeight, 0, 0);
        ((MryTextView) this.fragmentView.findViewById(R.id.tv_title)).setTextSize((AndroidUtilities.isTablet() || getParentActivity().getResources().getConfiguration().orientation != 2) ? 16.0f : 14.0f);
        ((ImageView) this.fragmentView.findViewById(R.id.iv_back)).setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.fragmentView.findViewById(R.id.rl_back).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                QrCodeActivity.this.lambda$initActionbar$0$QrCodeActivity(view);
            }
        });
    }

    public /* synthetic */ void lambda$initActionbar$0$QrCodeActivity(View v) {
        finishFragment();
    }

    private void initCodeContainer() {
        String str;
        this.tvNameParent = this.fragmentView.findViewById(R.id.tvNameParent);
        this.rlContainer = (ConstraintLayout) this.fragmentView.findViewById(R.id.rlContainer);
        this.ivQrCode = (MryImageView) this.fragmentView.findViewById(R.id.ivQrCode);
        this.avatarImage = (BackupImageView) this.fragmentView.findViewById(R.id.biv_avatar);
        this.nameView = (MryTextView) this.fragmentView.findViewById(R.id.tv_name);
        this.otherView = (MryTextView) this.fragmentView.findViewById(R.id.tv_other);
        this.ivGender = (ImageView) this.fragmentView.findViewById(R.id.iv_gender);
        this.tvQRCodeText = (MryTextView) this.fragmentView.findViewById(R.id.tv_qr_code_text);
        this.QRCodeImage = (ImageView) this.fragmentView.findViewById(R.id.iv_qr_code);
        this.progressBar = (ProgressBar) this.fragmentView.findViewById(R.id.progress_bar);
        this.llContainer = (LinearLayout) this.fragmentView.findViewById(R.id.ll_container);
        this.btnShare = (MryTextView) this.fragmentView.findViewById(R.id.tvShareQrCode);
        this.btnSave = (MryTextView) this.fragmentView.findViewById(R.id.tvSave);
        this.QRCodeImage.setImageBitmap(Bitmap.createBitmap(AndroidUtilities.dp(500.0f), AndroidUtilities.dp(500.0f), Bitmap.Config.ARGB_4444));
        this.avatarImage.getImageReceiver().setRoundRadius(AndroidUtilities.dp(7.5f));
        this.nameView.setTextSize(14.0f);
        this.otherView.setTextSize(14.0f);
        if (this.user != null) {
            AvatarDrawable avatarDrawable = new AvatarDrawable(this.user);
            avatarDrawable.setColor(Theme.getColor(Theme.key_avatar_backgroundInProfileBlue));
            this.avatarImage.setImage(ImageLocation.getForUser(this.user, false), "50_50", (Drawable) avatarDrawable, (Object) this.user);
            this.nameView.setText(UserObject.getName(this.user));
            MryTextView mryTextView = this.otherView;
            TLRPC.UserFull userFull2 = this.userInfo;
            if (userFull2 == null || TextUtils.isEmpty(userFull2.user.username)) {
                str = "Account Exception";
            } else {
                str = LocaleController.getString("AppIdWithColon", R.string.AppIdWithColon) + this.userInfo.user.username;
            }
            mryTextView.setText(str);
            this.tvQRCodeText.setText(LocaleController.getString("QrCodeUserText", R.string.QrCodeUserText));
        } else if (this.chat != null) {
            AvatarDrawable avatarDrawable2 = new AvatarDrawable(this.chat);
            avatarDrawable2.setColor(Theme.getColor(Theme.key_avatar_backgroundInProfileBlue));
            this.avatarImage.setImage(ImageLocation.getForChat(this.chat, false), "50_50", (Drawable) avatarDrawable2, (Object) this.chat);
            this.nameView.setText(this.chat.title);
            this.otherView.setText(this.chat.username);
            this.tvQRCodeText.setText(LocaleController.getString("QrCodeChatText", R.string.QrCodeChatText));
        }
        this.btnSave.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                QrCodeActivity.this.lambda$initCodeContainer$1$QrCodeActivity(view);
            }
        });
        this.btnShare.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                QrCodeActivity.this.lambda$initCodeContainer$2$QrCodeActivity(view);
            }
        });
    }

    public /* synthetic */ void lambda$initCodeContainer$1$QrCodeActivity(View v) {
        save();
    }

    public /* synthetic */ void lambda$initCodeContainer$2$QrCodeActivity(View v) {
        shareText();
    }

    public void onResume() {
        super.onResume();
    }

    private void setViewData() {
        String str;
        if (this.userFull.getExtendBean() != null && this.fragmentView != null) {
            this.fragmentView.getViewTreeObserver().addOnGlobalLayoutListener(this.changeNameWidthListener);
            int sex = this.userFull.getExtendBean().sex;
            int i = 0;
            this.ivGender.setImageResource(sex == 1 ? R.mipmap.ic_male : sex == 2 ? R.mipmap.ic_female : 0);
            ImageView imageView = this.ivGender;
            if (!(sex == 1 || sex == 2)) {
                i = 8;
            }
            imageView.setVisibility(i);
            MryTextView mryTextView = this.otherView;
            TLRPC.UserFull userFull2 = this.userInfo;
            if (userFull2 == null || TextUtils.isEmpty(userFull2.user.username)) {
                str = "Account Exception";
            } else {
                str = LocaleController.getString("AppIdWithColon", R.string.AppIdWithColon) + this.userInfo.user.username;
            }
            mryTextView.setText(str);
        }
    }

    private void createQRCode() {
        new Thread(new Runnable() {
            public final void run() {
                QrCodeActivity.this.lambda$createQRCode$4$QrCodeActivity();
            }
        }).start();
    }

    public /* synthetic */ void lambda$createQRCode$4$QrCodeActivity() {
        String preStr = getMessagesController().sharePrefix + "&Key=";
        Bitmap bitmap = this.avatarImage.getImageReceiver().getBitmap();
        Bitmap logo = BitmapFactory.decodeResource(this.mContext.getResources(), R.mipmap.ic_logo);
        Bitmap userBitmap = null;
        Bitmap bitmap2 = null;
        StringBuilder builder = new StringBuilder();
        if (this.user != null) {
            builder.append("PUid=");
            builder.append(this.user.id);
            builder.append("#Hash=");
            builder.append(this.user.access_hash);
            String encodeToString = Base64.encodeToString(builder.toString().getBytes(), 2);
            this.ret = encodeToString;
            this.ret = encodeToString.replace("=", "%3D");
            String str = preStr + this.ret;
            this.ret = str;
            bitmap2 = CodeUtils.createQRCode(str, AndroidUtilities.dp(500.0f), logo);
            userBitmap = CodeUtils.createQRCode(this.ret, AndroidUtilities.dp(500.0f), (Bitmap) null);
        } else if (this.chat != null) {
            builder.append("PUid=");
            builder.append(this.chat.id);
            builder.append("#Hash=");
            builder.append(this.chat.access_hash);
            builder.append("#Uname=");
            builder.append(this.chat.username);
            String encodeToString2 = Base64.encodeToString(builder.toString().getBytes(), 2);
            this.ret = encodeToString2;
            this.ret = encodeToString2.replace("=", "%3D");
            String str2 = preStr + this.ret;
            this.ret = str2;
            bitmap2 = CodeUtils.createQRCode(str2, AndroidUtilities.dp(500.0f), logo);
            userBitmap = CodeUtils.createQRCode(this.ret, AndroidUtilities.dp(500.0f), (Bitmap) null);
        }
        AndroidUtilities.runOnUIThread(new Runnable(userBitmap, bitmap2) {
            private final /* synthetic */ Bitmap f$1;
            private final /* synthetic */ Bitmap f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                QrCodeActivity.this.lambda$null$3$QrCodeActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$3$QrCodeActivity(Bitmap finalUserBitmap, Bitmap finalLogoBitmap) {
        this.progressBar.setVisibility(8);
        this.QRCodeImage.setImageBitmap(finalUserBitmap);
        this.ivQrCode.setImageBitmap(finalLogoBitmap);
    }

    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
        if (bitmap == null) {
            return null;
        }
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);
        float roundPx = (float) pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(-12434878);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.userFullInfoDidLoad) {
            Integer uid = args[0];
            int i = this.userId;
            if (i != 0 && i == uid.intValue() && (args[1] instanceof TLRPCContacts.CL_userFull_v1)) {
                TLRPC.UserFull userFull2 = args[1];
                this.userInfo = userFull2;
                this.userFull = (TLRPCContacts.CL_userFull_v1) userFull2;
                this.user = userFull2.user;
                setViewData();
            }
        }
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResultFragment(requestCode, permissions, grantResults);
        if (requestCode == 804 && grantResults != null && grantResults[0] == 0) {
            share();
        }
    }

    private void shareText() {
        try {
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("text/plain");
            intent.putExtra("android.intent.extra.TEXT", this.ret);
            getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("BotShare", R.string.BotShare)), 500);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private void save() {
        if (saveImageToGallery(this.mContext, getCacheBitmapFromView(this.rlContainer), "UserShare.png")) {
            ToastUtils.show((CharSequence) LocaleController.getString("MeSavedSuccessfully", R.string.MeSavedSuccessfully));
        } else {
            ToastUtils.show((CharSequence) LocaleController.getString("MeSaveFailed", R.string.MeSaveFailed));
        }
    }

    public static boolean saveImageToGallery(Context context, Bitmap bitmap, String fileName) {
        File appDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "GroupQrcode");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            boolean isSuccess = bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.flush();
            fos.close();
            context.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(file)));
            if (isSuccess) {
                return true;
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void share() {
        if (this.QRCodeImage.getDrawable() != null && getParentActivity() != null) {
            if (Build.VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                File directory = FileLoader.getDirectory(0);
                File f = new File(directory, System.currentTimeMillis() + ".jpg");
                if (!ImageUtils.save(getCacheBitmapFromView(this.llContainer), f, Bitmap.CompressFormat.JPEG, false)) {
                    ToastUtils.show((int) R.string.SaveFailed);
                    return;
                }
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("image/*");
                if (Build.VERSION.SDK_INT >= 24) {
                    try {
                        intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(getParentActivity(), "im.bclpbkiauv.messenger.provider", f));
                        intent.setFlags(1);
                    } catch (Exception e) {
                        intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(f));
                    }
                } else {
                    intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(f));
                }
                getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("ShareQrCode", R.string.ShareQrCode)), 500);
                return;
            }
            getParentActivity().requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 804);
        }
    }

    public static Bitmap getCacheBitmapFromView(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        Bitmap drawingCache = view.getDrawingCache();
        if (drawingCache == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(drawingCache);
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }
}
