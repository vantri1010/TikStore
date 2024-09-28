package im.bclpbkiauv.ui.dialogs;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import androidx.core.content.FileProvider;
import com.blankj.utilcode.util.ImageUtils;
import com.king.zxing.util.CodeUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.fragments.BaseFmts;
import im.bclpbkiauv.ui.hviews.MryImageView;
import im.bclpbkiauv.ui.hviews.MryTextView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class McShareDialog {
    private TLRPC.Chat chat;
    /* access modifiers changed from: private */
    public Dialog dialog;
    private MryImageView ivCancel;
    private MryImageView ivCopy;
    private MryImageView ivQrCode;
    private MryImageView ivShareQrCode;
    private BaseFmts mBaseFmts;
    /* access modifiers changed from: private */
    public Context mContext;
    private AlertDialog progressDialog;
    private String ret;
    /* access modifiers changed from: private */
    public RelativeLayout rlContainer;
    private MCDailyTaskShareBean shareData;
    /* access modifiers changed from: private */
    public Dialog shareDialog;
    private MryTextView tvInfo;
    private MryTextView tvSave;
    private MryTextView tvShare;
    private MryTextView tvTitle;
    private MryTextView tvUrl;
    private TLRPC.User user;

    public McShareDialog(Context context, BaseFmts baseFmts) {
        this.mContext = context;
        this.mBaseFmts = baseFmts;
    }

    public void initData() {
        showShareView();
    }

    private void showShareView() {
        View toastRoot = LayoutInflater.from(this.mContext).inflate(R.layout.dialog_mc_share_show, (ViewGroup) null);
        this.tvTitle = (MryTextView) toastRoot.findViewById(R.id.tvTitle);
        this.tvInfo = (MryTextView) toastRoot.findViewById(R.id.tvInfo);
        this.ivQrCode = (MryImageView) toastRoot.findViewById(R.id.ivQrCode);
        this.tvSave = (MryTextView) toastRoot.findViewById(R.id.tvSave);
        this.tvShare = (MryTextView) toastRoot.findViewById(R.id.tvShare);
        this.ivCopy = (MryImageView) toastRoot.findViewById(R.id.ivCopy);
        this.tvUrl = (MryTextView) toastRoot.findViewById(R.id.tvUrl);
        this.ivCancel = (MryImageView) toastRoot.findViewById(R.id.ivCancel);
        this.tvTitle.setText(LocaleController.getString("MeInviteFriends", R.string.MeInviteFriends));
        this.tvInfo.setText(LocaleController.getString("ScanQRcodeAddFriend", R.string.ScanQRcodeAddFriend));
        this.tvSave.setText(LocaleController.getString("SavePicture", R.string.SavePicture));
        this.tvShare.setText(LocaleController.getString("InviteNow", R.string.InviteNow));
        View shareView = LayoutInflater.from(this.mContext).inflate(R.layout.mc_share_view, (ViewGroup) null);
        this.rlContainer = (RelativeLayout) shareView.findViewById(R.id.rlContainer);
        this.ivShareQrCode = (MryImageView) shareView.findViewById(R.id.ivQrCode);
        Dialog dialog2 = new Dialog(this.mContext);
        this.shareDialog = dialog2;
        dialog2.getWindow().setBackgroundDrawable(new ColorDrawable());
        this.shareDialog.setContentView(shareView);
        this.shareDialog.show();
        setFullScreen(this.shareDialog);
        createQRCode();
        Dialog dialog3 = new Dialog(this.mContext);
        this.dialog = dialog3;
        dialog3.setContentView(toastRoot);
        this.dialog.show();
        setFullScreen(this.dialog);
        this.dialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        this.dialog.setCancelable(false);
        this.tvSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (McShareDialog.saveImageToGallery(McShareDialog.this.mContext, McShareDialog.getCacheBitmapFromView(McShareDialog.this.rlContainer), "InviteFriendsShare.png")) {
                    ToastUtils.show((CharSequence) LocaleController.getString("MeSavedSuccessfully", R.string.MeSavedSuccessfully));
                } else {
                    ToastUtils.show((CharSequence) LocaleController.getString("MeSaveFailed", R.string.MeSaveFailed));
                }
            }
        });
        this.ivCopy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                McShareDialog.this.copy();
                McShareDialog.this.shareDialog.dismiss();
            }
        });
        this.ivCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                McShareDialog.this.dialog.dismiss();
                McShareDialog.this.shareDialog.dismiss();
            }
        });
        this.tvShare.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                McShareDialog.this.shareText();
                McShareDialog.this.shareDialog.dismiss();
            }
        });
    }

    public void setFullScreen(Dialog dialog2) {
        Window dialogWindow = dialog2.getWindow();
        if (dialogWindow != null) {
            dialogWindow.setFlags(1024, 1024);
            dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
            layoutParams.width = -1;
            layoutParams.height = -1;
            if (Build.VERSION.SDK_INT >= 28) {
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.layoutInDisplayCutoutMode = 1;
                dialogWindow.setAttributes(lp);
                dialogWindow.getDecorView().setSystemUiVisibility(1280);
            }
            dialogWindow.setAttributes(layoutParams);
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

    public static boolean saveImageToGallery(Context context, Bitmap bitmap, String fileName) {
        File appDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "qrcode");
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

    /* access modifiers changed from: private */
    public void shareText() {
        try {
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("text/plain");
            intent.putExtra("android.intent.extra.TEXT", this.ret);
            this.mBaseFmts.getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("BotShare", R.string.BotShare)), 500);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private void share() {
        if (this.ivShareQrCode.getDrawable() != null && this.mContext != null) {
            if (Build.VERSION.SDK_INT < 23 || this.mContext.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                File directory = FileLoader.getDirectory(0);
                File f = new File(directory, System.currentTimeMillis() + ".jpg");
                if (!ImageUtils.save(getCacheBitmapFromView(this.rlContainer), f, Bitmap.CompressFormat.JPEG, false)) {
                    ToastUtils.show((int) R.string.SaveFailed);
                    return;
                }
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("image/jpeg");
                if (Build.VERSION.SDK_INT >= 24) {
                    try {
                        intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(this.mContext, "im.bclpbkiauv.messenger.provider", f));
                        intent.setFlags(1);
                    } catch (Exception e) {
                        intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(f));
                    }
                } else {
                    intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(f));
                }
                this.mBaseFmts.getActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("ShareQrCode", R.string.ShareQrCode)), 500);
                return;
            }
            this.mBaseFmts.getActivity().requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 804);
        }
    }

    /* access modifiers changed from: private */
    public void copy() {
        ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.ret));
        ToastUtils.show((int) R.string.CopySuccess);
    }

    private void createQRCode() {
        new Thread(new Runnable() {
            public final void run() {
                McShareDialog.this.lambda$createQRCode$1$McShareDialog();
            }
        }).start();
    }

    public /* synthetic */ void lambda$createQRCode$1$McShareDialog() {
        String preStr = this.mBaseFmts.getMessagesController().sharePrefix + "&Key=";
        Bitmap logo = BitmapFactory.decodeResource(this.mContext.getResources(), R.mipmap.ic_logo);
        Bitmap bitmap = null;
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
            bitmap = CodeUtils.createQRCode(str, AndroidUtilities.dp(500.0f), logo);
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
            bitmap = CodeUtils.createQRCode(str2, AndroidUtilities.dp(500.0f), (Bitmap) null);
        }
        AndroidUtilities.runOnUIThread(new Runnable(bitmap) {
            private final /* synthetic */ Bitmap f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                McShareDialog.this.lambda$null$0$McShareDialog(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$0$McShareDialog(Bitmap finalBitmap) {
        this.ivQrCode.setImageBitmap(finalBitmap);
        this.ivShareQrCode.setImageBitmap(finalBitmap);
        this.tvUrl.setText(this.ret);
    }

    public void setUser(TLRPC.User user2) {
        this.user = user2;
    }

    public void setChat(TLRPC.Chat chat2) {
        this.chat = chat2;
    }

    public class MCDailyTaskShareBean {
        public String url;

        public MCDailyTaskShareBean() {
        }
    }
}
