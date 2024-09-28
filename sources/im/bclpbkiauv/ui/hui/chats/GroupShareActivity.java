package im.bclpbkiauv.ui.hui.chats;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.king.zxing.util.CodeUtils;
import com.litesuits.orm.db.assit.SQLBuilder;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.TLRPC;
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

public class GroupShareActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private BackupImageView bivGroupAvatar;
    private ViewTreeObserver.OnGlobalLayoutListener changeNameWidthListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        public void onGlobalLayout() {
            CharSequence nt;
            int allw = GroupShareActivity.this.tvGroupNameParent.getMeasuredWidth();
            if (allw > 0 && (nt = GroupShareActivity.this.tvGroupNumber.getText()) != null) {
                float nw = GroupShareActivity.this.tvGroupNumber.getPaint().measureText(nt.toString(), 0, nt.toString().length());
                if (nw > 0.0f && GroupShareActivity.this.tvGroupName.getMeasuredWidth() + ((int) nw) > allw) {
                    GroupShareActivity.this.tvGroupNameParent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    ViewGroup.LayoutParams lpName = GroupShareActivity.this.tvGroupName.getLayoutParams();
                    lpName.width = (allw - ((int) nw)) - AndroidUtilities.dp(5.0f);
                    GroupShareActivity.this.tvGroupName.setLayoutParams(lpName);
                }
            }
        }
    };
    private TLRPC.Chat chat;
    private TLRPC.ChatFull chatInfo;
    private ImageView ivGroupQrCode;
    private MryImageView ivQrCode;
    private LinearLayout llContainer;
    /* access modifiers changed from: private */
    public Context mContext;
    private ProgressBar progressBar;
    private String ret;
    /* access modifiers changed from: private */
    public RelativeLayout rlContainer;
    private MryTextView tvGroupMembers;
    /* access modifiers changed from: private */
    public MryTextView tvGroupName;
    /* access modifiers changed from: private */
    public View tvGroupNameParent;
    /* access modifiers changed from: private */
    public MryTextView tvGroupNumber;
    private MryTextView tvSave;
    private MryTextView tvShareQrCode;
    private TLRPC.User user;

    /* access modifiers changed from: private */
    public static boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoad);
        return true;
    }

    public void onFragmentDestroy() {
        if (this.fragmentView != null) {
            this.fragmentView.getViewTreeObserver().removeOnGlobalLayoutListener(this.changeNameWidthListener);
        }
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoad);
    }

    public View createView(Context context) {
        this.mContext = context;
        this.actionBar.setAddToContainer(false);
        this.fragmentView = View.inflate(context, R.layout.activity_group_share_layout, (ViewGroup) null);
        this.fragmentView.setOnTouchListener($$Lambda$GroupShareActivity$pcb8T0RAv5KE0FYMII1PPwO1HoA.INSTANCE);
        this.fragmentView.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{Color.parseColor("#449FFD"), Color.parseColor("#45CAFA")}));
        initActionbar();
        initCodeContainer();
        createQRCode();
        return this.fragmentView;
    }

    private void initActionbar() {
        FrameLayout flTitleBarContainer = (FrameLayout) this.fragmentView.findViewById(R.id.fl_title_bar_container);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) flTitleBarContainer.getLayoutParams();
        layoutParams.height = ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight;
        flTitleBarContainer.setLayoutParams(layoutParams);
        flTitleBarContainer.setPadding(0, AndroidUtilities.statusBarHeight, 0, 0);
        ((MryTextView) this.fragmentView.findViewById(R.id.tv_title)).setTextSize((AndroidUtilities.isTablet() || getParentActivity().getResources().getConfiguration().orientation != 2) ? 16.0f : 14.0f);
        ((ImageView) this.fragmentView.findViewById(R.id.iv_back)).setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.fragmentView.findViewById(R.id.rl_back).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                GroupShareActivity.this.lambda$initActionbar$0$GroupShareActivity(view);
            }
        });
    }

    public /* synthetic */ void lambda$initActionbar$0$GroupShareActivity(View v) {
        finishFragment();
    }

    private void initCodeContainer() {
        this.llContainer = (LinearLayout) this.fragmentView.findViewById(R.id.ll_container);
        this.rlContainer = (RelativeLayout) this.fragmentView.findViewById(R.id.rlContainer);
        this.bivGroupAvatar = (BackupImageView) this.fragmentView.findViewById(R.id.bivGroupAvatar);
        this.tvGroupName = (MryTextView) this.fragmentView.findViewById(R.id.tvGroupName);
        this.tvGroupNumber = (MryTextView) this.fragmentView.findViewById(R.id.tvGroupNumber);
        this.ivQrCode = (MryImageView) this.fragmentView.findViewById(R.id.ivQrCode);
        this.tvGroupMembers = (MryTextView) this.fragmentView.findViewById(R.id.tvGroupMembers);
        this.ivGroupQrCode = (ImageView) this.fragmentView.findViewById(R.id.ivGroupQrCode);
        this.tvSave = (MryTextView) this.fragmentView.findViewById(R.id.tvSave);
        this.tvShareQrCode = (MryTextView) this.fragmentView.findViewById(R.id.tvShareQrCode);
        this.progressBar = (ProgressBar) this.fragmentView.findViewById(R.id.progress_bar);
        this.tvGroupNameParent = this.fragmentView.findViewById(R.id.tvGroupNameParent);
        this.ivGroupQrCode.setImageBitmap(Bitmap.createBitmap(AndroidUtilities.dp(500.0f), AndroidUtilities.dp(500.0f), Bitmap.Config.ARGB_4444));
        this.bivGroupAvatar.getImageReceiver().setRoundRadius(AndroidUtilities.dp(30.0f));
        setViewData();
    }

    public void onResume() {
        super.onResume();
    }

    private void setViewData() {
        this.tvGroupNameParent.getViewTreeObserver().addOnGlobalLayoutListener(this.changeNameWidthListener);
        if (this.chat != null) {
            AvatarDrawable avatarDrawable = new AvatarDrawable(this.chat);
            avatarDrawable.setColor(Theme.getColor(Theme.key_avatar_backgroundInProfileBlue));
            this.bivGroupAvatar.setImage(ImageLocation.getForChat(this.chat, false), "50_50", (Drawable) avatarDrawable, (Object) this.chat);
            this.tvGroupName.setText(this.chat.title);
            if (!TextUtils.isEmpty(this.chatInfo.about)) {
                this.tvGroupMembers.setText(this.chatInfo.about);
            } else {
                this.tvGroupMembers.setText(LocaleController.getString("OwnerLazyToNothing", R.string.OwnerLazyToNothing));
            }
            MryTextView mryTextView = this.tvGroupMembers;
            mryTextView.setText(LocaleController.getString("GroupNumber", R.string.GroupNumber) + this.chat.username);
            MryTextView mryTextView2 = this.tvGroupNumber;
            mryTextView2.setText(SQLBuilder.PARENTHESES_LEFT + this.chat.participants_count + LocaleController.getString("GroupPeople", R.string.GroupPeople) + SQLBuilder.PARENTHESES_RIGHT);
            this.tvSave.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (GroupShareActivity.saveImageToGallery(GroupShareActivity.this.mContext, GroupShareActivity.getCacheBitmapFromView(GroupShareActivity.this.rlContainer), "GroupShare.png")) {
                        ToastUtils.show((CharSequence) LocaleController.getString("MeSavedSuccessfully", R.string.MeSavedSuccessfully));
                    } else {
                        ToastUtils.show((CharSequence) LocaleController.getString("MeSaveFailed", R.string.MeSaveFailed));
                    }
                }
            });
            this.tvShareQrCode.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    GroupShareActivity.this.lambda$setViewData$1$GroupShareActivity(view);
                }
            });
        }
    }

    public /* synthetic */ void lambda$setViewData$1$GroupShareActivity(View v) {
        try {
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("text/plain");
            intent.putExtra("android.intent.extra.TEXT", this.ret);
            getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("BotShare", R.string.BotShare)), 500);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private void createQRCode() {
        new Thread(new Runnable() {
            public final void run() {
                GroupShareActivity.this.lambda$createQRCode$3$GroupShareActivity();
            }
        }).start();
    }

    public /* synthetic */ void lambda$createQRCode$3$GroupShareActivity() {
        String preStr = getMessagesController().sharePrefix + "&Key=";
        Bitmap bitmap = this.bivGroupAvatar.getImageReceiver().getBitmap();
        Bitmap logo = BitmapFactory.decodeResource(this.mContext.getResources(), R.mipmap.ic_logo);
        Bitmap groupBitmap = null;
        Bitmap bitmap2 = null;
        StringBuilder builder = new StringBuilder();
        if (this.chat != null) {
            TLRPC.User user2 = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser();
            builder.append("PUid=");
            builder.append(user2.id);
            builder.append("#Hash=");
            builder.append(user2.access_hash);
            builder.append("#Uname=");
            builder.append(this.chat.username);
            String encodeToString = Base64.encodeToString(builder.toString().getBytes(), 2);
            this.ret = encodeToString;
            this.ret = encodeToString.replace("=", "%3D");
            String str = preStr + this.ret;
            this.ret = str;
            bitmap2 = CodeUtils.createQRCode(str, AndroidUtilities.dp(500.0f), toRoundCorner(logo, AndroidUtilities.dp(5.0f)));
            groupBitmap = CodeUtils.createQRCode(this.ret, AndroidUtilities.dp(500.0f), (Bitmap) null);
        }
        AndroidUtilities.runOnUIThread(new Runnable(bitmap2, groupBitmap) {
            private final /* synthetic */ Bitmap f$1;
            private final /* synthetic */ Bitmap f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                GroupShareActivity.this.lambda$null$2$GroupShareActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$2$GroupShareActivity(Bitmap finalLogoBitmap, Bitmap finalGroupBitmap) {
        this.progressBar.setVisibility(8);
        this.ivQrCode.setImageBitmap(finalLogoBitmap);
        this.ivGroupQrCode.setImageBitmap(finalGroupBitmap);
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

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        super.onActivityResultFragment(requestCode, resultCode, data);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.chatInfoDidLoad && (args[1] instanceof TLRPC.ChatFull)) {
            TLRPC.ChatFull ci = args[1];
            if (this.chatInfo != null && ci.id == this.chatInfo.id) {
                setViewData();
            }
        }
    }

    public Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
        float width = (float) bgimage.getWidth();
        float height = (float) bgimage.getHeight();
        float f = ((float) newWidth) / width;
        float f2 = ((float) newHeight) / height;
        return Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, new Matrix(), true);
    }

    public Bitmap captureView(View view) throws Throwable {
        Bitmap bm = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bm));
        return bm;
    }

    public void setChat(TLRPC.Chat chat2) {
        this.chat = chat2;
    }

    public void setChatInfo(TLRPC.ChatFull chatInfo2) {
        this.chatInfo = chatInfo2;
    }
}
