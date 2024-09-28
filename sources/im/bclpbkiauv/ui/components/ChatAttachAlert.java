package im.bclpbkiauv.ui.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.ChatObject;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.ImageReceiver;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaController;
import im.bclpbkiauv.messenger.MediaDataController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.messenger.VideoEditedInfo;
import im.bclpbkiauv.messenger.camera.CameraController;
import im.bclpbkiauv.messenger.camera.CameraView;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.ChatActivity;
import im.bclpbkiauv.ui.PhotoViewer;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuSubItem;
import im.bclpbkiauv.ui.actionbar.ActionBarPopupWindow;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.cells.PhotoAttachCameraCell;
import im.bclpbkiauv.ui.cells.PhotoAttachPermissionCell;
import im.bclpbkiauv.ui.cells.PhotoAttachPhotoCell;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.ChatAttachAlert;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.ShutterButton;
import im.bclpbkiauv.ui.hui.adapter.EditInputFilter;
import im.bclpbkiauv.ui.hui.visualcall.PermissionUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChatAttachAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate, BottomSheet.BottomSheetDelegateInterface {
    /* access modifiers changed from: private */
    public static ArrayList<Object> cameraPhotos = new ArrayList<>();
    private static final int compress = 1;
    private static final int group = 0;
    private static int lastImageId = -1;
    /* access modifiers changed from: private */
    public static boolean mediaFromExternalCamera;
    /* access modifiers changed from: private */
    public static HashMap<Object, Object> selectedPhotos = new HashMap<>();
    /* access modifiers changed from: private */
    public static ArrayList<Object> selectedPhotosOrder = new ArrayList<>();
    private final Property<ChatAttachAlert, Float> ATTACH_ALERT_PROGRESS;
    /* access modifiers changed from: private */
    public ActionBar actionBar;
    /* access modifiers changed from: private */
    public AnimatorSet actionBarAnimation;
    /* access modifiers changed from: private */
    public View actionBarShadow;
    /* access modifiers changed from: private */
    public PhotoAttachAdapter adapter;
    /* access modifiers changed from: private */
    public int alertOnlyOnce;
    /* access modifiers changed from: private */
    public boolean allowOrder = true;
    private int[] animateCameraValues = new int[5];
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    /* access modifiers changed from: private */
    public int attachItemSize;
    /* access modifiers changed from: private */
    public BaseFragment baseFragment;
    private boolean buttonPressed;
    /* access modifiers changed from: private */
    public ButtonsAdapter buttonsAdapter;
    private LinearLayoutManager buttonsLayoutManager;
    /* access modifiers changed from: private */
    public RecyclerListView buttonsRecyclerView;
    /* access modifiers changed from: private */
    public boolean cameraAnimationInProgress;
    /* access modifiers changed from: private */
    public PhotoAttachAdapter cameraAttachAdapter;
    /* access modifiers changed from: private */
    public Drawable cameraDrawable;
    /* access modifiers changed from: private */
    public FrameLayout cameraIcon;
    /* access modifiers changed from: private */
    public AnimatorSet cameraInitAnimation;
    private boolean cameraInitied;
    /* access modifiers changed from: private */
    public float cameraOpenProgress;
    /* access modifiers changed from: private */
    public boolean cameraOpened;
    /* access modifiers changed from: private */
    public FrameLayout cameraPanel;
    /* access modifiers changed from: private */
    public LinearLayoutManager cameraPhotoLayoutManager;
    /* access modifiers changed from: private */
    public RecyclerListView cameraPhotoRecyclerView;
    /* access modifiers changed from: private */
    public boolean cameraPhotoRecyclerViewIgnoreLayout;
    /* access modifiers changed from: private */
    public CameraView cameraView;
    private int[] cameraViewLocation = new int[2];
    private int cameraViewOffsetBottomY;
    private int cameraViewOffsetX;
    /* access modifiers changed from: private */
    public int cameraViewOffsetY;
    /* access modifiers changed from: private */
    public float cameraZoom;
    /* access modifiers changed from: private */
    public boolean cancelTakingPhotos;
    /* access modifiers changed from: private */
    public EditTextEmoji commentTextView;
    /* access modifiers changed from: private */
    public float cornerRadius = 1.0f;
    /* access modifiers changed from: private */
    public TextView counterTextView;
    /* access modifiers changed from: private */
    public int currentAccount = UserConfig.selectedAccount;
    private int currentSelectedCount;
    private DecelerateInterpolator decelerateInterpolator;
    /* access modifiers changed from: private */
    public ChatAttachViewDelegate delegate;
    private boolean deviceHasGoodCamera;
    private boolean dragging;
    /* access modifiers changed from: private */
    public TextView dropDown;
    /* access modifiers changed from: private */
    public ArrayList<MediaController.AlbumEntry> dropDownAlbums;
    private ActionBarMenuItem dropDownContainer;
    private Drawable dropDownDrawable;
    /* access modifiers changed from: private */
    public MessageObject editingMessageObject;
    /* access modifiers changed from: private */
    public boolean enterCommentEventSent;
    /* access modifiers changed from: private */
    public boolean flashAnimationInProgress;
    /* access modifiers changed from: private */
    public ImageView[] flashModeButton = new ImageView[2];
    /* access modifiers changed from: private */
    public FrameLayout frameLayout2;
    /* access modifiers changed from: private */
    public MediaController.AlbumEntry galleryAlbumEntry;
    /* access modifiers changed from: private */
    public int gridExtraSpace;
    /* access modifiers changed from: private */
    public RecyclerListView gridView;
    private Rect hitRect = new Rect();
    private DecelerateInterpolator interpolator = new DecelerateInterpolator(1.5f);
    private ActionBarMenuSubItem[] itemCells;
    private RecyclerViewItemRangeSelector itemRangeSelector;
    /* access modifiers changed from: private */
    public int itemSize;
    /* access modifiers changed from: private */
    public int itemsPerRow;
    /* access modifiers changed from: private */
    public int lastItemSize;
    private float lastY;
    /* access modifiers changed from: private */
    public GridLayoutManager layoutManager;
    private boolean loading;
    /* access modifiers changed from: private */
    public int maxSelectedPhotos = -1;
    private boolean maybeStartDraging;
    /* access modifiers changed from: private */
    public boolean mediaCaptured;
    /* access modifiers changed from: private */
    public boolean mediaEnabled = true;
    /* access modifiers changed from: private */
    public AnimatorSet menuAnimator;
    /* access modifiers changed from: private */
    public boolean menuShowed;
    /* access modifiers changed from: private */
    public boolean noCameraPermissions;
    /* access modifiers changed from: private */
    public boolean noGalleryPermissions;
    private boolean openWithFrontFaceCamera;
    /* access modifiers changed from: private */
    public Paint paint = new Paint(1);
    private boolean paused;
    private PhotoViewer.PhotoViewerProvider photoViewerProvider;
    private float pinchStartDistance;
    /* access modifiers changed from: private */
    public boolean pollsEnabled = true;
    private boolean pressed;
    /* access modifiers changed from: private */
    public EmptyTextProgressView progressView;
    /* access modifiers changed from: private */
    public TextView recordTime;
    /* access modifiers changed from: private */
    public RectF rect = new RectF();
    /* access modifiers changed from: private */
    public boolean requestingPermissions;
    /* access modifiers changed from: private */
    public int scrollOffsetY;
    /* access modifiers changed from: private */
    public MediaController.AlbumEntry selectedAlbumEntry;
    private View selectedCountView;
    /* access modifiers changed from: private */
    public ActionBarMenuItem selectedMenuItem;
    /* access modifiers changed from: private */
    public TextView selectedTextView;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout sendPopupLayout;
    /* access modifiers changed from: private */
    public ActionBarPopupWindow sendPopupWindow;
    /* access modifiers changed from: private */
    public View shadow;
    /* access modifiers changed from: private */
    public boolean shouldSelect;
    /* access modifiers changed from: private */
    public ShutterButton shutterButton;
    private SizeNotifierFrameLayout sizeNotifierFrameLayout;
    /* access modifiers changed from: private */
    public ImageView switchCameraButton;
    /* access modifiers changed from: private */
    public boolean takingPhoto;
    /* access modifiers changed from: private */
    public TextPaint textPaint = new TextPaint(1);
    /* access modifiers changed from: private */
    public TextView tooltipTextView;
    /* access modifiers changed from: private */
    public Runnable videoRecordRunnable;
    /* access modifiers changed from: private */
    public int videoRecordTime;
    private int[] viewPosition = new int[2];
    private ImageView writeButton;
    /* access modifiers changed from: private */
    public FrameLayout writeButtonContainer;
    private Drawable writeButtonDrawable;
    /* access modifiers changed from: private */
    public AnimatorSet zoomControlAnimation;
    private Runnable zoomControlHideRunnable;
    /* access modifiers changed from: private */
    public ZoomControlView zoomControlView;
    private boolean zoomWas;
    private boolean zooming;

    public interface ChatAttachViewDelegate {
        void didPressedButton(int i, boolean z, boolean z2, int i2);

        void didSelectBot(TLRPC.User user);

        View getRevealView();

        void needEnterComment();

        void onCameraOpened();
    }

    static /* synthetic */ int access$8508(ChatAttachAlert x0) {
        int i = x0.videoRecordTime;
        x0.videoRecordTime = i + 1;
        return i;
    }

    static /* synthetic */ int access$9310() {
        int i = lastImageId;
        lastImageId = i - 1;
        return i;
    }

    private class InnerAnimator {
        private AnimatorSet animatorSet;
        private float startRadius;

        private InnerAnimator() {
        }
    }

    private class BasePhotoProvider extends PhotoViewer.EmptyPhotoViewerProvider {
        private BasePhotoProvider() {
        }

        public boolean isPhotoChecked(int index) {
            MediaController.PhotoEntry photoEntry = ChatAttachAlert.this.getPhotoEntryAtPosition(index);
            return photoEntry != null && ChatAttachAlert.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId));
        }

        public int setPhotoChecked(int index, VideoEditedInfo videoEditedInfo) {
            MediaController.PhotoEntry photoEntry;
            if ((ChatAttachAlert.this.maxSelectedPhotos >= 0 && ChatAttachAlert.selectedPhotos.size() >= ChatAttachAlert.this.maxSelectedPhotos && !isPhotoChecked(index)) || (photoEntry = ChatAttachAlert.this.getPhotoEntryAtPosition(index)) == null) {
                return -1;
            }
            boolean add = true;
            int access$300 = ChatAttachAlert.this.addToSelectedPhotos(photoEntry, -1);
            int num = access$300;
            if (access$300 == -1) {
                num = ChatAttachAlert.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId));
            } else {
                add = false;
                photoEntry.editedInfo = null;
            }
            photoEntry.editedInfo = videoEditedInfo;
            int count = ChatAttachAlert.this.gridView.getChildCount();
            int a = 0;
            while (true) {
                if (a >= count) {
                    break;
                }
                View view = ChatAttachAlert.this.gridView.getChildAt(a);
                if (!(view instanceof PhotoAttachPhotoCell) || ((Integer) view.getTag()).intValue() != index) {
                    a++;
                } else if (!(ChatAttachAlert.this.baseFragment instanceof ChatActivity) || !ChatAttachAlert.this.allowOrder) {
                    ((PhotoAttachPhotoCell) view).setChecked(-1, add, false);
                } else {
                    ((PhotoAttachPhotoCell) view).setChecked(num, add, false);
                }
            }
            int count2 = ChatAttachAlert.this.cameraPhotoRecyclerView.getChildCount();
            int a2 = 0;
            while (true) {
                if (a2 >= count2) {
                    break;
                }
                View view2 = ChatAttachAlert.this.cameraPhotoRecyclerView.getChildAt(a2);
                if (!(view2 instanceof PhotoAttachPhotoCell) || ((Integer) view2.getTag()).intValue() != index) {
                    a2++;
                } else if (!(ChatAttachAlert.this.baseFragment instanceof ChatActivity) || !ChatAttachAlert.this.allowOrder) {
                    ((PhotoAttachPhotoCell) view2).setChecked(-1, add, false);
                } else {
                    ((PhotoAttachPhotoCell) view2).setChecked(num, add, false);
                }
            }
            ChatAttachAlert.this.updatePhotosButton(add ? 1 : 2);
            return num;
        }

        public int getSelectedCount() {
            return ChatAttachAlert.selectedPhotos.size();
        }

        public ArrayList<Object> getSelectedPhotosOrder() {
            return ChatAttachAlert.selectedPhotosOrder;
        }

        public HashMap<Object, Object> getSelectedPhotos() {
            return ChatAttachAlert.selectedPhotos;
        }

        public int getPhotoIndex(int index) {
            MediaController.PhotoEntry photoEntry = ChatAttachAlert.this.getPhotoEntryAtPosition(index);
            if (photoEntry == null) {
                return -1;
            }
            return ChatAttachAlert.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId));
        }
    }

    private void updateCheckedPhotoIndices() {
        if (this.baseFragment instanceof ChatActivity) {
            int count = this.gridView.getChildCount();
            for (int a = 0; a < count; a++) {
                View view = this.gridView.getChildAt(a);
                if (view instanceof PhotoAttachPhotoCell) {
                    PhotoAttachPhotoCell cell = (PhotoAttachPhotoCell) view;
                    MediaController.PhotoEntry photoEntry = getPhotoEntryAtPosition(((Integer) cell.getTag()).intValue());
                    if (photoEntry != null) {
                        cell.setNum(selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId)));
                    }
                }
            }
            int count2 = this.cameraPhotoRecyclerView.getChildCount();
            for (int a2 = 0; a2 < count2; a2++) {
                View view2 = this.cameraPhotoRecyclerView.getChildAt(a2);
                if (view2 instanceof PhotoAttachPhotoCell) {
                    PhotoAttachPhotoCell cell2 = (PhotoAttachPhotoCell) view2;
                    MediaController.PhotoEntry photoEntry2 = getPhotoEntryAtPosition(((Integer) cell2.getTag()).intValue());
                    if (photoEntry2 != null) {
                        cell2.setNum(selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry2.imageId)));
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public MediaController.PhotoEntry getPhotoEntryAtPosition(int position) {
        if (position < 0) {
            return null;
        }
        int cameraCount = cameraPhotos.size();
        if (position < cameraCount) {
            return (MediaController.PhotoEntry) cameraPhotos.get(position);
        }
        int position2 = position - cameraCount;
        if (position2 < this.selectedAlbumEntry.photos.size()) {
            return this.selectedAlbumEntry.photos.get(position2);
        }
        return null;
    }

    private ArrayList<Object> getAllPhotosArray() {
        if (this.selectedAlbumEntry != null) {
            if (cameraPhotos.isEmpty()) {
                return this.selectedAlbumEntry.photos;
            }
            ArrayList<Object> arrayList = new ArrayList<>(this.selectedAlbumEntry.photos.size() + cameraPhotos.size());
            arrayList.addAll(cameraPhotos);
            arrayList.addAll(this.selectedAlbumEntry.photos);
            return arrayList;
        } else if (!cameraPhotos.isEmpty()) {
            return cameraPhotos;
        } else {
            return new ArrayList<>(0);
        }
    }

    private class AttachButton extends FrameLayout {
        /* access modifiers changed from: private */
        public ImageView imageView;
        /* access modifiers changed from: private */
        public TextView textView;

        public AttachButton(Context context) {
            super(context);
            ImageView imageView2 = new ImageView(context);
            this.imageView = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            if (Build.VERSION.SDK_INT >= 21) {
                this.imageView.setImageDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 1, AndroidUtilities.dp(25.0f)));
            }
            addView(this.imageView, LayoutHelper.createFrame(50.0f, 50.0f, 49, 0.0f, 12.0f, 0.0f, 0.0f));
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setMaxLines(2);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setTextColor(Theme.getColor(Theme.key_dialogTextGray2));
            this.textView.setTextSize(1, 12.0f);
            this.textView.setLineSpacing((float) (-AndroidUtilities.dp(2.0f)), 1.0f);
            addView(this.textView, LayoutHelper.createFrame(-1.0f, -2.0f, 51, 0.0f, 66.0f, 0.0f, 0.0f));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(ChatAttachAlert.this.attachItemSize, 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(92.0f), 1073741824));
        }

        public void setTextAndIcon(CharSequence text, Drawable drawable) {
            this.textView.setText(text);
            this.imageView.setBackgroundDrawable(drawable);
        }

        public boolean hasOverlappingRendering() {
            return false;
        }
    }

    private class AttachBotButton extends FrameLayout {
        private AvatarDrawable avatarDrawable = new AvatarDrawable();
        /* access modifiers changed from: private */
        public TLRPC.User currentUser;
        /* access modifiers changed from: private */
        public BackupImageView imageView;
        /* access modifiers changed from: private */
        public TextView nameTextView;

        public AttachBotButton(Context context) {
            super(context);
            BackupImageView backupImageView = new BackupImageView(context);
            this.imageView = backupImageView;
            backupImageView.setRoundRadius(AndroidUtilities.dp(25.0f));
            addView(this.imageView, LayoutHelper.createFrame(50.0f, 50.0f, 49, 0.0f, 12.0f, 0.0f, 0.0f));
            if (Build.VERSION.SDK_INT >= 21) {
                View selector = new View(context);
                selector.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 1, AndroidUtilities.dp(25.0f)));
                addView(selector, LayoutHelper.createFrame(50.0f, 50.0f, 49, 0.0f, 12.0f, 0.0f, 0.0f));
            }
            TextView textView = new TextView(context);
            this.nameTextView = textView;
            textView.setTextSize(1, 12.0f);
            this.nameTextView.setGravity(49);
            this.nameTextView.setLines(1);
            this.nameTextView.setSingleLine(true);
            this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
            addView(this.nameTextView, LayoutHelper.createFrame(-1.0f, -2.0f, 51, 6.0f, 66.0f, 6.0f, 0.0f));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(ChatAttachAlert.this.attachItemSize, 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), 1073741824));
        }

        public void setUser(TLRPC.User user) {
            if (user != null) {
                this.nameTextView.setTextColor(Theme.getColor(Theme.key_dialogTextGray2));
                this.currentUser = user;
                this.nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
                this.avatarDrawable.setInfo(user);
                this.imageView.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) this.avatarDrawable, (Object) user);
                requestLayout();
            }
        }
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ChatAttachAlert(android.content.Context r40, im.bclpbkiauv.ui.actionbar.BaseFragment r41) {
        /*
            r39 = this;
            r6 = r39
            r7 = r40
            r8 = r41
            r9 = 1
            r10 = 0
            r6.<init>(r7, r10, r9)
            android.text.TextPaint r0 = new android.text.TextPaint
            r0.<init>(r9)
            r6.textPaint = r0
            android.graphics.RectF r0 = new android.graphics.RectF
            r0.<init>()
            r6.rect = r0
            android.graphics.Paint r0 = new android.graphics.Paint
            r0.<init>(r9)
            r6.paint = r0
            r11 = 1065353216(0x3f800000, float:1.0)
            r6.cornerRadius = r11
            int r0 = im.bclpbkiauv.messenger.UserConfig.selectedAccount
            r6.currentAccount = r0
            r6.mediaEnabled = r9
            r6.pollsEnabled = r9
            r12 = 2
            android.widget.ImageView[] r0 = new android.widget.ImageView[r12]
            r6.flashModeButton = r0
            int[] r0 = new int[r12]
            r6.cameraViewLocation = r0
            int[] r0 = new int[r12]
            r6.viewPosition = r0
            r0 = 5
            int[] r0 = new int[r0]
            r6.animateCameraValues = r0
            android.view.animation.DecelerateInterpolator r0 = new android.view.animation.DecelerateInterpolator
            r1 = 1069547520(0x3fc00000, float:1.5)
            r0.<init>(r1)
            r6.interpolator = r0
            r13 = -1
            r6.maxSelectedPhotos = r13
            r6.allowOrder = r9
            android.graphics.Rect r0 = new android.graphics.Rect
            r0.<init>()
            r6.hitRect = r0
            r14 = 1117782016(0x42a00000, float:80.0)
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            r6.itemSize = r0
            r6.lastItemSize = r0
            r0 = 1118437376(0x42aa0000, float:85.0)
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r0)
            r6.attachItemSize = r0
            r15 = 3
            r6.itemsPerRow = r15
            android.view.animation.DecelerateInterpolator r0 = new android.view.animation.DecelerateInterpolator
            r0.<init>()
            r6.decelerateInterpolator = r0
            r6.loading = r9
            im.bclpbkiauv.ui.components.ChatAttachAlert$1 r0 = new im.bclpbkiauv.ui.components.ChatAttachAlert$1
            r0.<init>()
            r6.photoViewerProvider = r0
            im.bclpbkiauv.ui.components.ChatAttachAlert$23 r0 = new im.bclpbkiauv.ui.components.ChatAttachAlert$23
            java.lang.String r1 = "openProgress"
            r0.<init>(r1)
            r6.ATTACH_ALERT_PROGRESS = r0
            android.view.animation.OvershootInterpolator r0 = new android.view.animation.OvershootInterpolator
            r1 = 1060320051(0x3f333333, float:0.7)
            r0.<init>(r1)
            r6.openInterpolator = r0
            r6.baseFragment = r8
            r6.setDelegate(r6)
            r6.checkCamera(r10)
            int r0 = r6.currentAccount
            im.bclpbkiauv.messenger.NotificationCenter r0 = im.bclpbkiauv.messenger.NotificationCenter.getInstance(r0)
            int r1 = im.bclpbkiauv.messenger.NotificationCenter.albumsDidLoad
            r0.addObserver(r6, r1)
            int r0 = r6.currentAccount
            im.bclpbkiauv.messenger.NotificationCenter r0 = im.bclpbkiauv.messenger.NotificationCenter.getInstance(r0)
            int r1 = im.bclpbkiauv.messenger.NotificationCenter.reloadInlineHints
            r0.addObserver(r6, r1)
            im.bclpbkiauv.messenger.NotificationCenter r0 = im.bclpbkiauv.messenger.NotificationCenter.getGlobalInstance()
            int r1 = im.bclpbkiauv.messenger.NotificationCenter.cameraInitied
            r0.addObserver(r6, r1)
            android.content.res.Resources r0 = r40.getResources()
            r1 = 2131231190(0x7f0801d6, float:1.8078454E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r1)
            android.graphics.drawable.Drawable r0 = r0.mutate()
            r6.cameraDrawable = r0
            im.bclpbkiauv.ui.components.ChatAttachAlert$2 r0 = new im.bclpbkiauv.ui.components.ChatAttachAlert$2
            r0.<init>(r7)
            r6.sizeNotifierFrameLayout = r0
            r6.containerView = r0
            android.view.ViewGroup r0 = r6.containerView
            r0.setWillNotDraw(r10)
            android.view.ViewGroup r0 = r6.containerView
            int r1 = r6.backgroundPaddingLeft
            int r2 = r6.backgroundPaddingLeft
            r0.setPadding(r1, r10, r2, r10)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r7)
            r6.selectedTextView = r0
            java.lang.String r16 = "dialogTextBlack"
            int r1 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r16)
            r0.setTextColor(r1)
            android.widget.TextView r0 = r6.selectedTextView
            r5 = 1098907648(0x41800000, float:16.0)
            r0.setTextSize(r9, r5)
            android.widget.TextView r0 = r6.selectedTextView
            java.lang.String r17 = "fonts/rmedium.ttf"
            android.graphics.Typeface r1 = im.bclpbkiauv.messenger.AndroidUtilities.getTypeface(r17)
            r0.setTypeface(r1)
            android.widget.TextView r0 = r6.selectedTextView
            r1 = 51
            r0.setGravity(r1)
            android.widget.TextView r0 = r6.selectedTextView
            r4 = 4
            r0.setVisibility(r4)
            android.widget.TextView r0 = r6.selectedTextView
            r3 = 0
            r0.setAlpha(r3)
            android.view.ViewGroup r0 = r6.containerView
            android.widget.TextView r1 = r6.selectedTextView
            r18 = -1082130432(0xffffffffbf800000, float:-1.0)
            r19 = -1073741824(0xffffffffc0000000, float:-2.0)
            r20 = 51
            r21 = 1102577664(0x41b80000, float:23.0)
            r22 = 0
            r23 = 1111490560(0x42400000, float:48.0)
            r24 = 0
            android.widget.FrameLayout$LayoutParams r2 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r18, r19, r20, r21, r22, r23, r24)
            r0.addView(r1, r2)
            im.bclpbkiauv.ui.components.ChatAttachAlert$3 r0 = new im.bclpbkiauv.ui.components.ChatAttachAlert$3
            r0.<init>(r7)
            r6.actionBar = r0
            java.lang.String r18 = "dialogBackground"
            int r1 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r18)
            r0.setBackgroundColor(r1)
            im.bclpbkiauv.ui.actionbar.ActionBar r0 = r6.actionBar
            r1 = 2131558496(0x7f0d0060, float:1.874231E38)
            r0.setBackButtonImage(r1)
            im.bclpbkiauv.ui.actionbar.ActionBar r0 = r6.actionBar
            int r1 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r16)
            r0.setItemsColor(r1, r10)
            im.bclpbkiauv.ui.actionbar.ActionBar r0 = r6.actionBar
            java.lang.String r1 = "dialogButtonSelector"
            int r1 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r1)
            r0.setItemsBackgroundColor(r1, r10)
            im.bclpbkiauv.ui.actionbar.ActionBar r0 = r6.actionBar
            int r1 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r16)
            r0.setTitleColor(r1)
            im.bclpbkiauv.ui.actionbar.ActionBar r0 = r6.actionBar
            r0.setOccupyStatusBar(r10)
            im.bclpbkiauv.ui.actionbar.ActionBar r0 = r6.actionBar
            r0.setAlpha(r3)
            android.view.ViewGroup r0 = r6.containerView
            im.bclpbkiauv.ui.actionbar.ActionBar r1 = r6.actionBar
            r2 = -1073741824(0xffffffffc0000000, float:-2.0)
            android.widget.FrameLayout$LayoutParams r2 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r13, r2)
            r0.addView(r1, r2)
            im.bclpbkiauv.ui.actionbar.ActionBar r0 = r6.actionBar
            im.bclpbkiauv.ui.components.ChatAttachAlert$4 r1 = new im.bclpbkiauv.ui.components.ChatAttachAlert$4
            r1.<init>(r8)
            r0.setActionBarMenuOnItemClick(r1)
            im.bclpbkiauv.ui.components.ChatAttachAlert$5 r2 = new im.bclpbkiauv.ui.components.ChatAttachAlert$5
            int r19 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r16)
            r20 = 0
            r21 = 0
            r0 = r2
            r1 = r39
            r14 = r2
            r2 = r40
            r11 = 0
            r3 = r20
            r13 = 4
            r4 = r21
            r21 = 1098907648(0x41800000, float:16.0)
            r5 = r19
            r0.<init>(r2, r3, r4, r5)
            r6.selectedMenuItem = r14
            r14.setLongClickEnabled(r10)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r0 = r6.selectedMenuItem
            r1 = 2131231075(0x7f080163, float:1.807822E38)
            r0.setIcon((int) r1)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r0 = r6.selectedMenuItem
            java.lang.String r1 = "AccDescrMoreOptions"
            r2 = 2131689518(0x7f0f002e, float:1.9008054E38)
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r2)
            r0.setContentDescription(r1)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r0 = r6.selectedMenuItem
            java.lang.String r1 = "SendWithoutGrouping"
            r2 = 2131693833(0x7f0f1109, float:1.9016805E38)
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r2)
            r0.addSubItem(r10, r1)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r0 = r6.selectedMenuItem
            java.lang.String r1 = "SendWithoutCompression"
            r2 = 2131693832(0x7f0f1108, float:1.9016803E38)
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r2)
            r0.addSubItem(r9, r1)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r0 = r6.selectedMenuItem
            r0.setVisibility(r13)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r0 = r6.selectedMenuItem
            r0.setAlpha(r11)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r0 = r6.selectedMenuItem
            r0.setSubMenuOpenSide(r12)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r0 = r6.selectedMenuItem
            im.bclpbkiauv.ui.components.-$$Lambda$ChatAttachAlert$6WwiR0nrw8JByc2mLFMJJiI6BuE r1 = new im.bclpbkiauv.ui.components.-$$Lambda$ChatAttachAlert$6WwiR0nrw8JByc2mLFMJJiI6BuE
            r1.<init>()
            r0.setDelegate(r1)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r0 = r6.selectedMenuItem
            r1 = 1116733440(0x42900000, float:72.0)
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r1)
            r0.setAdditionalYOffset(r1)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r0 = r6.selectedMenuItem
            r14 = 1086324736(0x40c00000, float:6.0)
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            float r1 = (float) r1
            r0.setTranslationX(r1)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r0 = r6.selectedMenuItem
            java.lang.String r1 = "dialogButtonSelector"
            int r1 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r1)
            r2 = 6
            android.graphics.drawable.Drawable r1 = im.bclpbkiauv.ui.actionbar.Theme.createSelectorDrawable(r1, r2)
            r0.setBackgroundDrawable(r1)
            android.view.ViewGroup r0 = r6.containerView
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r1 = r6.selectedMenuItem
            r5 = 48
            r2 = 53
            android.widget.FrameLayout$LayoutParams r2 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r5, (int) r5, (int) r2)
            r0.addView(r1, r2)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r0 = r6.selectedMenuItem
            im.bclpbkiauv.ui.components.-$$Lambda$ChatAttachAlert$P93Xqx2G4d-FtHuXv_TCFtTLwSE r1 = new im.bclpbkiauv.ui.components.-$$Lambda$ChatAttachAlert$P93Xqx2G4d-FtHuXv_TCFtTLwSE
            r1.<init>()
            r0.setOnClickListener(r1)
            im.bclpbkiauv.ui.components.ChatAttachAlert$6 r0 = new im.bclpbkiauv.ui.components.ChatAttachAlert$6
            r0.<init>(r7)
            r6.gridView = r0
            im.bclpbkiauv.ui.components.ChatAttachAlert$PhotoAttachAdapter r1 = new im.bclpbkiauv.ui.components.ChatAttachAlert$PhotoAttachAdapter
            r1.<init>(r7, r9)
            r6.adapter = r1
            r0.setAdapter(r1)
            im.bclpbkiauv.ui.components.RecyclerListView r0 = r6.gridView
            r0.setClipToPadding(r10)
            im.bclpbkiauv.ui.components.RecyclerListView r0 = r6.gridView
            r4 = 0
            r0.setItemAnimator(r4)
            im.bclpbkiauv.ui.components.RecyclerListView r0 = r6.gridView
            r0.setLayoutAnimation(r4)
            im.bclpbkiauv.ui.components.RecyclerListView r0 = r6.gridView
            r0.setVerticalScrollBarEnabled(r10)
            im.bclpbkiauv.ui.components.RecyclerListView r0 = r6.gridView
            java.lang.String r1 = "dialogScrollGlow"
            int r1 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r1)
            r0.setGlowColor(r1)
            android.view.ViewGroup r0 = r6.containerView
            im.bclpbkiauv.ui.components.RecyclerListView r1 = r6.gridView
            r24 = -1082130432(0xffffffffbf800000, float:-1.0)
            r25 = -1082130432(0xffffffffbf800000, float:-1.0)
            r26 = 51
            r27 = 0
            r28 = 1093664768(0x41300000, float:11.0)
            r29 = 0
            r30 = 0
            android.widget.FrameLayout$LayoutParams r2 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r24, r25, r26, r27, r28, r29, r30)
            r0.addView(r1, r2)
            im.bclpbkiauv.ui.components.RecyclerListView r0 = r6.gridView
            im.bclpbkiauv.ui.components.ChatAttachAlert$7 r1 = new im.bclpbkiauv.ui.components.ChatAttachAlert$7
            r1.<init>()
            r0.setOnScrollListener(r1)
            im.bclpbkiauv.ui.components.ChatAttachAlert$8 r0 = new im.bclpbkiauv.ui.components.ChatAttachAlert$8
            int r1 = r6.itemSize
            r0.<init>(r7, r1)
            r6.layoutManager = r0
            im.bclpbkiauv.ui.components.ChatAttachAlert$9 r1 = new im.bclpbkiauv.ui.components.ChatAttachAlert$9
            r1.<init>()
            r0.setSpanSizeLookup(r1)
            im.bclpbkiauv.ui.components.RecyclerListView r0 = r6.gridView
            androidx.recyclerview.widget.GridLayoutManager r1 = r6.layoutManager
            r0.setLayoutManager(r1)
            im.bclpbkiauv.ui.components.RecyclerListView r0 = r6.gridView
            im.bclpbkiauv.ui.components.-$$Lambda$ChatAttachAlert$EEt9OI9VMph3SbmPW5OuYW2eleM r1 = new im.bclpbkiauv.ui.components.-$$Lambda$ChatAttachAlert$EEt9OI9VMph3SbmPW5OuYW2eleM
            r1.<init>()
            r0.setOnItemClickListener((im.bclpbkiauv.ui.components.RecyclerListView.OnItemClickListener) r1)
            im.bclpbkiauv.ui.components.RecyclerListView r0 = r6.gridView
            im.bclpbkiauv.ui.components.-$$Lambda$ChatAttachAlert$ejJpGcyMFdGrZaVLOWXL--RB1GE r1 = new im.bclpbkiauv.ui.components.-$$Lambda$ChatAttachAlert$ejJpGcyMFdGrZaVLOWXL--RB1GE
            r1.<init>()
            r0.setOnItemLongClickListener((im.bclpbkiauv.ui.components.RecyclerListView.OnItemLongClickListener) r1)
            im.bclpbkiauv.ui.components.RecyclerViewItemRangeSelector r0 = new im.bclpbkiauv.ui.components.RecyclerViewItemRangeSelector
            im.bclpbkiauv.ui.components.ChatAttachAlert$10 r1 = new im.bclpbkiauv.ui.components.ChatAttachAlert$10
            r1.<init>()
            r0.<init>(r1)
            r6.itemRangeSelector = r0
            im.bclpbkiauv.ui.components.RecyclerListView r1 = r6.gridView
            r1.addOnItemTouchListener(r0)
            im.bclpbkiauv.ui.actionbar.ActionBar r0 = r6.actionBar
            im.bclpbkiauv.ui.actionbar.ActionBarMenu r3 = r0.createMenu()
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r0 = new im.bclpbkiauv.ui.actionbar.ActionBarMenuItem
            r0.<init>(r7, r3, r10, r10)
            r6.dropDownContainer = r0
            r0.setSubMenuOpenSide(r9)
            im.bclpbkiauv.ui.actionbar.ActionBar r0 = r6.actionBar
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r1 = r6.dropDownContainer
            boolean r2 = im.bclpbkiauv.messenger.AndroidUtilities.isTablet()
            r19 = 1113587712(0x42600000, float:56.0)
            if (r2 == 0) goto L_0x02dd
            r2 = 1115684864(0x42800000, float:64.0)
            r27 = 1115684864(0x42800000, float:64.0)
            goto L_0x02df
        L_0x02dd:
            r27 = 1113587712(0x42600000, float:56.0)
        L_0x02df:
            r28 = 0
            r29 = 1109393408(0x42200000, float:40.0)
            r30 = 0
            r24 = -1073741824(0xffffffffc0000000, float:-2.0)
            r25 = -1082130432(0xffffffffbf800000, float:-1.0)
            r26 = 51
            android.widget.FrameLayout$LayoutParams r2 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r24, r25, r26, r27, r28, r29, r30)
            r0.addView(r1, r10, r2)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r0 = r6.dropDownContainer
            im.bclpbkiauv.ui.components.-$$Lambda$ChatAttachAlert$JlPDRbqdwkuuxlgRYQoDGvHaUSY r1 = new im.bclpbkiauv.ui.components.-$$Lambda$ChatAttachAlert$JlPDRbqdwkuuxlgRYQoDGvHaUSY
            r1.<init>()
            r0.setOnClickListener(r1)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r7)
            r6.dropDown = r0
            r0.setGravity(r15)
            android.widget.TextView r0 = r6.dropDown
            r0.setSingleLine(r9)
            android.widget.TextView r0 = r6.dropDown
            r0.setLines(r9)
            android.widget.TextView r0 = r6.dropDown
            r0.setMaxLines(r9)
            android.widget.TextView r0 = r6.dropDown
            android.text.TextUtils$TruncateAt r1 = android.text.TextUtils.TruncateAt.END
            r0.setEllipsize(r1)
            android.widget.TextView r0 = r6.dropDown
            int r1 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r16)
            r0.setTextColor(r1)
            android.widget.TextView r0 = r6.dropDown
            r1 = 2131690512(0x7f0f0410, float:1.901007E38)
            java.lang.String r2 = "ChatGallery"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            android.widget.TextView r0 = r6.dropDown
            android.graphics.Typeface r1 = im.bclpbkiauv.messenger.AndroidUtilities.getTypeface(r17)
            r0.setTypeface(r1)
            android.content.res.Resources r0 = r40.getResources()
            r1 = 2131231085(0x7f08016d, float:1.8078241E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r1)
            android.graphics.drawable.Drawable r0 = r0.mutate()
            r6.dropDownDrawable = r0
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            int r2 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r16)
            android.graphics.PorterDuff$Mode r15 = android.graphics.PorterDuff.Mode.MULTIPLY
            r1.<init>(r2, r15)
            r0.setColorFilter(r1)
            android.widget.TextView r0 = r6.dropDown
            r1 = 1082130432(0x40800000, float:4.0)
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r1)
            r0.setCompoundDrawablePadding(r1)
            android.widget.TextView r0 = r6.dropDown
            r15 = 1092616192(0x41200000, float:10.0)
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r15)
            r0.setPadding(r10, r10, r1, r10)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r0 = r6.dropDownContainer
            android.widget.TextView r1 = r6.dropDown
            r25 = -1073741824(0xffffffffc0000000, float:-2.0)
            r26 = 16
            r27 = 1098907648(0x41800000, float:16.0)
            r29 = 0
            android.widget.FrameLayout$LayoutParams r2 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r24, r25, r26, r27, r28, r29, r30)
            r0.addView(r1, r2)
            android.view.View r0 = new android.view.View
            r0.<init>(r7)
            r6.actionBarShadow = r0
            r0.setAlpha(r11)
            android.view.View r0 = r6.actionBarShadow
            java.lang.String r1 = "dialogShadowLine"
            int r1 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r1)
            r0.setBackgroundColor(r1)
            android.view.ViewGroup r0 = r6.containerView
            android.view.View r1 = r6.actionBarShadow
            r2 = 1065353216(0x3f800000, float:1.0)
            r14 = -1
            android.widget.FrameLayout$LayoutParams r2 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r14, r2)
            r0.addView(r1, r2)
            im.bclpbkiauv.ui.components.EmptyTextProgressView r0 = new im.bclpbkiauv.ui.components.EmptyTextProgressView
            r0.<init>(r7)
            r6.progressView = r0
            r1 = 2131692235(0x7f0f0acb, float:1.9013564E38)
            java.lang.String r2 = "NoPhotos"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            im.bclpbkiauv.ui.components.EmptyTextProgressView r0 = r6.progressView
            r0.setOnTouchListener(r4)
            im.bclpbkiauv.ui.components.EmptyTextProgressView r0 = r6.progressView
            r1 = 20
            r0.setTextSize(r1)
            android.view.ViewGroup r0 = r6.containerView
            im.bclpbkiauv.ui.components.EmptyTextProgressView r1 = r6.progressView
            r2 = 1117782016(0x42a00000, float:80.0)
            r14 = -1
            android.widget.FrameLayout$LayoutParams r12 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r14, r2)
            r0.addView(r1, r12)
            boolean r0 = r6.loading
            if (r0 == 0) goto L_0x03de
            im.bclpbkiauv.ui.components.EmptyTextProgressView r0 = r6.progressView
            r0.showProgress()
            goto L_0x03e3
        L_0x03de:
            im.bclpbkiauv.ui.components.EmptyTextProgressView r0 = r6.progressView
            r0.showTextView()
        L_0x03e3:
            android.view.View r0 = new android.view.View
            r0.<init>(r7)
            r6.shadow = r0
            r1 = 2131231072(0x7f080160, float:1.8078215E38)
            r0.setBackgroundResource(r1)
            android.view.ViewGroup r0 = r6.containerView
            android.view.View r1 = r6.shadow
            r24 = -1082130432(0xffffffffbf800000, float:-1.0)
            r25 = 1077936128(0x40400000, float:3.0)
            r26 = 83
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 1119354880(0x42b80000, float:92.0)
            android.widget.FrameLayout$LayoutParams r2 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r24, r25, r26, r27, r28, r29, r30)
            r0.addView(r1, r2)
            im.bclpbkiauv.ui.components.ChatAttachAlert$11 r0 = new im.bclpbkiauv.ui.components.ChatAttachAlert$11
            r0.<init>(r7)
            r6.buttonsRecyclerView = r0
            im.bclpbkiauv.ui.components.ChatAttachAlert$ButtonsAdapter r1 = new im.bclpbkiauv.ui.components.ChatAttachAlert$ButtonsAdapter
            r1.<init>(r7)
            r6.buttonsAdapter = r1
            r0.setAdapter(r1)
            im.bclpbkiauv.ui.components.RecyclerListView r0 = r6.buttonsRecyclerView
            androidx.recyclerview.widget.LinearLayoutManager r1 = new androidx.recyclerview.widget.LinearLayoutManager
            r1.<init>(r7, r10, r10)
            r6.buttonsLayoutManager = r1
            r0.setLayoutManager(r1)
            im.bclpbkiauv.ui.components.RecyclerListView r0 = r6.buttonsRecyclerView
            r0.setVerticalScrollBarEnabled(r10)
            im.bclpbkiauv.ui.components.RecyclerListView r0 = r6.buttonsRecyclerView
            r0.setHorizontalScrollBarEnabled(r10)
            im.bclpbkiauv.ui.components.RecyclerListView r0 = r6.buttonsRecyclerView
            r0.setItemAnimator(r4)
            im.bclpbkiauv.ui.components.RecyclerListView r0 = r6.buttonsRecyclerView
            r0.setLayoutAnimation(r4)
            im.bclpbkiauv.ui.components.RecyclerListView r0 = r6.buttonsRecyclerView
            java.lang.String r1 = "dialogScrollGlow"
            int r1 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r1)
            r0.setGlowColor(r1)
            im.bclpbkiauv.ui.components.RecyclerListView r0 = r6.buttonsRecyclerView
            int r1 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r18)
            r0.setBackgroundColor(r1)
            android.view.ViewGroup r0 = r6.containerView
            im.bclpbkiauv.ui.components.RecyclerListView r1 = r6.buttonsRecyclerView
            r2 = 92
            r12 = 83
            r14 = -1
            android.widget.FrameLayout$LayoutParams r2 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r14, (int) r2, (int) r12)
            r0.addView(r1, r2)
            im.bclpbkiauv.ui.components.RecyclerListView r0 = r6.buttonsRecyclerView
            im.bclpbkiauv.ui.components.-$$Lambda$ChatAttachAlert$-FTbuiff1EE9oYy7pNnlixdcC_w r1 = new im.bclpbkiauv.ui.components.-$$Lambda$ChatAttachAlert$-FTbuiff1EE9oYy7pNnlixdcC_w
            r1.<init>()
            r0.setOnItemClickListener((im.bclpbkiauv.ui.components.RecyclerListView.OnItemClickListener) r1)
            im.bclpbkiauv.ui.components.RecyclerListView r0 = r6.buttonsRecyclerView
            im.bclpbkiauv.ui.components.-$$Lambda$ChatAttachAlert$ppkgrGbuHKiQ6TNaSWD823dk_gY r1 = new im.bclpbkiauv.ui.components.-$$Lambda$ChatAttachAlert$ppkgrGbuHKiQ6TNaSWD823dk_gY
            r1.<init>()
            r0.setOnItemLongClickListener((im.bclpbkiauv.ui.components.RecyclerListView.OnItemLongClickListener) r1)
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r7)
            r6.frameLayout2 = r0
            int r1 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r18)
            r0.setBackgroundColor(r1)
            android.widget.FrameLayout r0 = r6.frameLayout2
            r0.setVisibility(r13)
            android.widget.FrameLayout r0 = r6.frameLayout2
            r0.setAlpha(r11)
            android.view.ViewGroup r0 = r6.containerView
            android.widget.FrameLayout r1 = r6.frameLayout2
            r2 = -1
            android.widget.FrameLayout$LayoutParams r14 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r2, (int) r5, (int) r12)
            r0.addView(r1, r14)
            android.widget.FrameLayout r0 = r6.frameLayout2
            im.bclpbkiauv.ui.components.-$$Lambda$ChatAttachAlert$PWR5VB-JFfjG4MhfZfxx5PUNmE8 r1 = im.bclpbkiauv.ui.components.$$Lambda$ChatAttachAlert$PWR5VBJFfjG4MhfZfxx5PUNmE8.INSTANCE
            r0.setOnTouchListener(r1)
            im.bclpbkiauv.ui.components.ChatAttachAlert$12 r14 = new im.bclpbkiauv.ui.components.ChatAttachAlert$12
            im.bclpbkiauv.ui.components.SizeNotifierFrameLayout r2 = r6.sizeNotifierFrameLayout
            r18 = 0
            r24 = 1
            r0 = r14
            r1 = r39
            r25 = r2
            r2 = r40
            r26 = r3
            r3 = r25
            r4 = r18
            r5 = r24
            r0.<init>(r2, r3, r4, r5)
            r6.commentTextView = r14
            android.text.InputFilter[] r0 = new android.text.InputFilter[r9]
            android.text.InputFilter$LengthFilter r1 = new android.text.InputFilter$LengthFilter
            int r2 = im.bclpbkiauv.messenger.UserConfig.selectedAccount
            im.bclpbkiauv.messenger.MessagesController r2 = im.bclpbkiauv.messenger.MessagesController.getInstance(r2)
            int r2 = r2.maxCaptionLength
            r1.<init>(r2)
            r0[r10] = r1
            im.bclpbkiauv.ui.components.EditTextEmoji r1 = r6.commentTextView
            r1.setFilters(r0)
            im.bclpbkiauv.ui.components.EditTextEmoji r1 = r6.commentTextView
            r2 = 2131689664(0x7f0f00c0, float:1.900835E38)
            java.lang.String r3 = "AddCaption"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            r1.setHint(r2)
            im.bclpbkiauv.ui.components.EditTextEmoji r1 = r6.commentTextView
            r1.onResume()
            im.bclpbkiauv.ui.components.EditTextEmoji r1 = r6.commentTextView
            im.bclpbkiauv.ui.components.EditTextBoldCursor r1 = r1.getEditText()
            r1.setMaxLines(r9)
            r1.setSingleLine(r9)
            android.widget.FrameLayout r2 = r6.frameLayout2
            im.bclpbkiauv.ui.components.EditTextEmoji r3 = r6.commentTextView
            r32 = -1082130432(0xffffffffbf800000, float:-1.0)
            r33 = -1082130432(0xffffffffbf800000, float:-1.0)
            r34 = 51
            r35 = 0
            r36 = 0
            r37 = 1118306304(0x42a80000, float:84.0)
            r38 = 0
            android.widget.FrameLayout$LayoutParams r4 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r32, r33, r34, r35, r36, r37, r38)
            r2.addView(r3, r4)
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r7)
            r6.writeButtonContainer = r2
            r2.setVisibility(r13)
            android.widget.FrameLayout r2 = r6.writeButtonContainer
            r3 = 1045220557(0x3e4ccccd, float:0.2)
            r2.setScaleX(r3)
            android.widget.FrameLayout r2 = r6.writeButtonContainer
            r2.setScaleY(r3)
            android.widget.FrameLayout r2 = r6.writeButtonContainer
            r2.setAlpha(r11)
            android.widget.FrameLayout r2 = r6.writeButtonContainer
            r4 = 2131693795(0x7f0f10e3, float:1.9016728E38)
            java.lang.String r5 = "Send"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r4)
            r2.setContentDescription(r4)
            android.view.ViewGroup r2 = r6.containerView
            android.widget.FrameLayout r4 = r6.writeButtonContainer
            r32 = 1114636288(0x42700000, float:60.0)
            r33 = 1114636288(0x42700000, float:60.0)
            r34 = 85
            r37 = 1086324736(0x40c00000, float:6.0)
            r38 = 1092616192(0x41200000, float:10.0)
            android.widget.FrameLayout$LayoutParams r5 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r32, r33, r34, r35, r36, r37, r38)
            r2.addView(r4, r5)
            android.widget.FrameLayout r2 = r6.writeButtonContainer
            im.bclpbkiauv.ui.components.-$$Lambda$ChatAttachAlert$RQF-KFdMsstq3LT7it4GlDhWcZI r4 = new im.bclpbkiauv.ui.components.-$$Lambda$ChatAttachAlert$RQF-KFdMsstq3LT7it4GlDhWcZI
            r4.<init>(r8)
            r2.setOnClickListener(r4)
            android.widget.FrameLayout r2 = r6.writeButtonContainer
            im.bclpbkiauv.ui.components.-$$Lambda$ChatAttachAlert$JD4VkA3Y3dkDu0UHGnD7fqpW_aQ r4 = new im.bclpbkiauv.ui.components.-$$Lambda$ChatAttachAlert$JD4VkA3Y3dkDu0UHGnD7fqpW_aQ
            r4.<init>()
            r2.setOnLongClickListener(r4)
            android.widget.ImageView r2 = new android.widget.ImageView
            r2.<init>(r7)
            r6.writeButton = r2
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r19)
            java.lang.String r4 = "dialogFloatingButton"
            int r4 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r4)
            java.lang.String r5 = "dialogFloatingButtonPressed"
            int r5 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r5)
            android.graphics.drawable.Drawable r2 = im.bclpbkiauv.ui.actionbar.Theme.createSimpleSelectorCircleDrawable(r2, r4, r5)
            r6.writeButtonDrawable = r2
            int r2 = android.os.Build.VERSION.SDK_INT
            r4 = 21
            if (r2 >= r4) goto L_0x05ac
            android.content.res.Resources r2 = r40.getResources()
            r5 = 2131231019(0x7f08012b, float:1.8078107E38)
            android.graphics.drawable.Drawable r2 = r2.getDrawable(r5)
            android.graphics.drawable.Drawable r2 = r2.mutate()
            android.graphics.PorterDuffColorFilter r5 = new android.graphics.PorterDuffColorFilter
            r14 = -16777216(0xffffffffff000000, float:-1.7014118E38)
            android.graphics.PorterDuff$Mode r13 = android.graphics.PorterDuff.Mode.MULTIPLY
            r5.<init>(r14, r13)
            r2.setColorFilter(r5)
            im.bclpbkiauv.ui.components.CombinedDrawable r5 = new im.bclpbkiauv.ui.components.CombinedDrawable
            android.graphics.drawable.Drawable r13 = r6.writeButtonDrawable
            r5.<init>(r2, r13, r10, r10)
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r19)
            int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r19)
            r5.setIconSize(r13, r14)
            r6.writeButtonDrawable = r5
        L_0x05ac:
            android.widget.ImageView r2 = r6.writeButton
            android.graphics.drawable.Drawable r5 = r6.writeButtonDrawable
            r2.setBackgroundDrawable(r5)
            android.widget.ImageView r2 = r6.writeButton
            r5 = 2131230835(0x7f080073, float:1.8077734E38)
            r2.setImageResource(r5)
            android.widget.ImageView r2 = r6.writeButton
            android.graphics.PorterDuffColorFilter r5 = new android.graphics.PorterDuffColorFilter
            java.lang.String r13 = "dialogFloatingIcon"
            int r13 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r13)
            android.graphics.PorterDuff$Mode r14 = android.graphics.PorterDuff.Mode.MULTIPLY
            r5.<init>(r13, r14)
            r2.setColorFilter(r5)
            android.widget.ImageView r2 = r6.writeButton
            android.widget.ImageView$ScaleType r5 = android.widget.ImageView.ScaleType.CENTER
            r2.setScaleType(r5)
            int r2 = android.os.Build.VERSION.SDK_INT
            if (r2 < r4) goto L_0x05e2
            android.widget.ImageView r2 = r6.writeButton
            im.bclpbkiauv.ui.components.ChatAttachAlert$14 r5 = new im.bclpbkiauv.ui.components.ChatAttachAlert$14
            r5.<init>()
            r2.setOutlineProvider(r5)
        L_0x05e2:
            android.widget.FrameLayout r2 = r6.writeButtonContainer
            android.widget.ImageView r5 = r6.writeButton
            int r13 = android.os.Build.VERSION.SDK_INT
            if (r13 < r4) goto L_0x05ed
            r32 = 1113587712(0x42600000, float:56.0)
            goto L_0x05f1
        L_0x05ed:
            r13 = 1114636288(0x42700000, float:60.0)
            r32 = 1114636288(0x42700000, float:60.0)
        L_0x05f1:
            int r13 = android.os.Build.VERSION.SDK_INT
            if (r13 < r4) goto L_0x05f8
            r33 = 1113587712(0x42600000, float:56.0)
            goto L_0x05fc
        L_0x05f8:
            r19 = 1114636288(0x42700000, float:60.0)
            r33 = 1114636288(0x42700000, float:60.0)
        L_0x05fc:
            r34 = 51
            int r13 = android.os.Build.VERSION.SDK_INT
            if (r13 < r4) goto L_0x0607
            r13 = 1073741824(0x40000000, float:2.0)
            r35 = 1073741824(0x40000000, float:2.0)
            goto L_0x0609
        L_0x0607:
            r35 = 0
        L_0x0609:
            r36 = 0
            r37 = 0
            r38 = 0
            android.widget.FrameLayout$LayoutParams r13 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r32, r33, r34, r35, r36, r37, r38)
            r2.addView(r5, r13)
            android.text.TextPaint r2 = r6.textPaint
            r5 = 1094713344(0x41400000, float:12.0)
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            r2.setTextSize(r5)
            android.text.TextPaint r2 = r6.textPaint
            android.graphics.Typeface r5 = im.bclpbkiauv.messenger.AndroidUtilities.getTypeface(r17)
            r2.setTypeface(r5)
            im.bclpbkiauv.ui.components.ChatAttachAlert$15 r2 = new im.bclpbkiauv.ui.components.ChatAttachAlert$15
            r2.<init>(r7)
            r6.selectedCountView = r2
            r2.setAlpha(r11)
            android.view.View r2 = r6.selectedCountView
            r2.setScaleX(r3)
            android.view.View r2 = r6.selectedCountView
            r2.setScaleY(r3)
            android.view.ViewGroup r2 = r6.containerView
            android.view.View r3 = r6.selectedCountView
            r32 = 1109917696(0x42280000, float:42.0)
            r33 = 1103101952(0x41c00000, float:24.0)
            r34 = 85
            r35 = 0
            r37 = -1056964608(0xffffffffc1000000, float:-8.0)
            r38 = 1091567616(0x41100000, float:9.0)
            android.widget.FrameLayout$LayoutParams r5 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r32, r33, r34, r35, r36, r37, r38)
            r2.addView(r3, r5)
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r7)
            r6.recordTime = r2
            r3 = 2131231628(0x7f08038c, float:1.8079342E38)
            r2.setBackgroundResource(r3)
            android.widget.TextView r2 = r6.recordTime
            android.graphics.drawable.Drawable r2 = r2.getBackground()
            android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
            r5 = 1711276032(0x66000000, float:1.5111573E23)
            android.graphics.PorterDuff$Mode r13 = android.graphics.PorterDuff.Mode.MULTIPLY
            r3.<init>(r5, r13)
            r2.setColorFilter(r3)
            android.widget.TextView r2 = r6.recordTime
            r3 = 1097859072(0x41700000, float:15.0)
            r2.setTextSize(r9, r3)
            android.widget.TextView r2 = r6.recordTime
            android.graphics.Typeface r3 = im.bclpbkiauv.messenger.AndroidUtilities.getTypeface(r17)
            r2.setTypeface(r3)
            android.widget.TextView r2 = r6.recordTime
            r2.setAlpha(r11)
            android.widget.TextView r2 = r6.recordTime
            r3 = -1
            r2.setTextColor(r3)
            android.widget.TextView r2 = r6.recordTime
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r15)
            r5 = 1084227584(0x40a00000, float:5.0)
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r15)
            r14 = 1084227584(0x40a00000, float:5.0)
            int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            r2.setPadding(r3, r5, r13, r14)
            im.bclpbkiauv.ui.actionbar.BottomSheet$ContainerView r2 = r6.container
            android.widget.TextView r3 = r6.recordTime
            r32 = -1073741824(0xffffffffc0000000, float:-2.0)
            r33 = -1073741824(0xffffffffc0000000, float:-2.0)
            r34 = 49
            r36 = 1098907648(0x41800000, float:16.0)
            r37 = 0
            r38 = 0
            android.widget.FrameLayout$LayoutParams r5 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r32, r33, r34, r35, r36, r37, r38)
            r2.addView(r3, r5)
            im.bclpbkiauv.ui.components.ChatAttachAlert$16 r2 = new im.bclpbkiauv.ui.components.ChatAttachAlert$16
            r2.<init>(r7)
            r6.cameraPanel = r2
            r3 = 8
            r2.setVisibility(r3)
            android.widget.FrameLayout r2 = r6.cameraPanel
            r2.setAlpha(r11)
            im.bclpbkiauv.ui.actionbar.BottomSheet$ContainerView r2 = r6.container
            android.widget.FrameLayout r5 = r6.cameraPanel
            r13 = 126(0x7e, float:1.77E-43)
            r14 = -1
            android.widget.FrameLayout$LayoutParams r12 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r14, (int) r13, (int) r12)
            r2.addView(r5, r12)
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r7)
            r6.counterTextView = r2
            r5 = 2131231443(0x7f0802d3, float:1.8078967E38)
            r2.setBackgroundResource(r5)
            android.widget.TextView r2 = r6.counterTextView
            r2.setVisibility(r3)
            android.widget.TextView r2 = r6.counterTextView
            r5 = -1
            r2.setTextColor(r5)
            android.widget.TextView r2 = r6.counterTextView
            r5 = 17
            r2.setGravity(r5)
            android.widget.TextView r2 = r6.counterTextView
            r2.setPivotX(r11)
            android.widget.TextView r2 = r6.counterTextView
            r2.setPivotY(r11)
            android.widget.TextView r2 = r6.counterTextView
            android.graphics.Typeface r5 = im.bclpbkiauv.messenger.AndroidUtilities.getTypeface(r17)
            r2.setTypeface(r5)
            android.widget.TextView r2 = r6.counterTextView
            r5 = 2131231441(0x7f0802d1, float:1.8078963E38)
            r2.setCompoundDrawablesWithIntrinsicBounds(r10, r10, r5, r10)
            android.widget.TextView r2 = r6.counterTextView
            r5 = 1082130432(0x40800000, float:4.0)
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            r2.setCompoundDrawablePadding(r5)
            android.widget.TextView r2 = r6.counterTextView
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r21)
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r21)
            r2.setPadding(r5, r10, r12, r10)
            im.bclpbkiauv.ui.actionbar.BottomSheet$ContainerView r2 = r6.container
            android.widget.TextView r5 = r6.counterTextView
            r33 = 1108869120(0x42180000, float:38.0)
            r34 = 51
            r36 = 0
            r38 = 1122500608(0x42e80000, float:116.0)
            android.widget.FrameLayout$LayoutParams r12 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r32, r33, r34, r35, r36, r37, r38)
            r2.addView(r5, r12)
            android.widget.TextView r2 = r6.counterTextView
            im.bclpbkiauv.ui.components.-$$Lambda$ChatAttachAlert$wGgSIO3zVUgf0kqzokssfghmjVE r5 = new im.bclpbkiauv.ui.components.-$$Lambda$ChatAttachAlert$wGgSIO3zVUgf0kqzokssfghmjVE
            r5.<init>()
            r2.setOnClickListener(r5)
            im.bclpbkiauv.ui.components.ZoomControlView r2 = new im.bclpbkiauv.ui.components.ZoomControlView
            r2.<init>(r7)
            r6.zoomControlView = r2
            r2.setVisibility(r3)
            im.bclpbkiauv.ui.components.ZoomControlView r2 = r6.zoomControlView
            r2.setAlpha(r11)
            im.bclpbkiauv.ui.actionbar.BottomSheet$ContainerView r2 = r6.container
            im.bclpbkiauv.ui.components.ZoomControlView r3 = r6.zoomControlView
            r33 = 1112014848(0x42480000, float:50.0)
            android.widget.FrameLayout$LayoutParams r5 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r32, r33, r34, r35, r36, r37, r38)
            r2.addView(r3, r5)
            im.bclpbkiauv.ui.components.ZoomControlView r2 = r6.zoomControlView
            im.bclpbkiauv.ui.components.-$$Lambda$ChatAttachAlert$UrCZ-KpXSFd08Jkc02cd5URHrD8 r3 = new im.bclpbkiauv.ui.components.-$$Lambda$ChatAttachAlert$UrCZ-KpXSFd08Jkc02cd5URHrD8
            r3.<init>()
            r2.setDelegate(r3)
            im.bclpbkiauv.ui.components.ShutterButton r2 = new im.bclpbkiauv.ui.components.ShutterButton
            r2.<init>(r7)
            r6.shutterButton = r2
            android.widget.FrameLayout r3 = r6.cameraPanel
            r5 = 84
            r12 = 84
            r13 = 17
            android.widget.FrameLayout$LayoutParams r5 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r5, (int) r12, (int) r13)
            r3.addView(r2, r5)
            im.bclpbkiauv.ui.components.ShutterButton r2 = r6.shutterButton
            im.bclpbkiauv.ui.components.ChatAttachAlert$17 r3 = new im.bclpbkiauv.ui.components.ChatAttachAlert$17
            r3.<init>(r8)
            r2.setDelegate(r3)
            im.bclpbkiauv.ui.components.ShutterButton r2 = r6.shutterButton
            r2.setFocusable(r9)
            im.bclpbkiauv.ui.components.ShutterButton r2 = r6.shutterButton
            r3 = 2131689550(0x7f0f004e, float:1.9008119E38)
            java.lang.String r5 = "AccDescrShutter"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r3)
            r2.setContentDescription(r3)
            android.widget.ImageView r2 = new android.widget.ImageView
            r2.<init>(r7)
            r6.switchCameraButton = r2
            android.widget.ImageView$ScaleType r3 = android.widget.ImageView.ScaleType.CENTER
            r2.setScaleType(r3)
            android.widget.FrameLayout r2 = r6.cameraPanel
            android.widget.ImageView r3 = r6.switchCameraButton
            r5 = 48
            android.widget.FrameLayout$LayoutParams r4 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r5, (int) r5, (int) r4)
            r2.addView(r3, r4)
            android.widget.ImageView r2 = r6.switchCameraButton
            im.bclpbkiauv.ui.components.-$$Lambda$ChatAttachAlert$yCkM5GPSBp2cQwJmepMuAjxkSe8 r3 = new im.bclpbkiauv.ui.components.-$$Lambda$ChatAttachAlert$yCkM5GPSBp2cQwJmepMuAjxkSe8
            r3.<init>()
            r2.setOnClickListener(r3)
            android.widget.ImageView r2 = r6.switchCameraButton
            r3 = 2131689554(0x7f0f0052, float:1.9008127E38)
            java.lang.String r4 = "AccDescrSwitchCamera"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r3)
            r2.setContentDescription(r3)
            r2 = 0
        L_0x07d9:
            r3 = 2
            if (r2 >= r3) goto L_0x082c
            android.widget.ImageView[] r3 = r6.flashModeButton
            android.widget.ImageView r4 = new android.widget.ImageView
            r4.<init>(r7)
            r3[r2] = r4
            android.widget.ImageView[] r3 = r6.flashModeButton
            r3 = r3[r2]
            android.widget.ImageView$ScaleType r4 = android.widget.ImageView.ScaleType.CENTER
            r3.setScaleType(r4)
            android.widget.ImageView[] r3 = r6.flashModeButton
            r3 = r3[r2]
            r4 = 4
            r3.setVisibility(r4)
            android.widget.FrameLayout r3 = r6.cameraPanel
            android.widget.ImageView[] r4 = r6.flashModeButton
            r4 = r4[r2]
            r12 = 51
            android.widget.FrameLayout$LayoutParams r12 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r5, (int) r5, (int) r12)
            r3.addView(r4, r12)
            android.widget.ImageView[] r3 = r6.flashModeButton
            r3 = r3[r2]
            im.bclpbkiauv.ui.components.-$$Lambda$ChatAttachAlert$QRHHmPbrJCCuzamV46NDJ85kitY r4 = new im.bclpbkiauv.ui.components.-$$Lambda$ChatAttachAlert$QRHHmPbrJCCuzamV46NDJ85kitY
            r4.<init>()
            r3.setOnClickListener(r4)
            android.widget.ImageView[] r3 = r6.flashModeButton
            r3 = r3[r2]
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r12 = "flash mode "
            r4.append(r12)
            r4.append(r2)
            java.lang.String r4 = r4.toString()
            r3.setContentDescription(r4)
            int r2 = r2 + 1
            goto L_0x07d9
        L_0x082c:
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r7)
            r6.tooltipTextView = r2
            r3 = 1097859072(0x41700000, float:15.0)
            r2.setTextSize(r9, r3)
            android.widget.TextView r2 = r6.tooltipTextView
            r3 = -1
            r2.setTextColor(r3)
            android.widget.TextView r2 = r6.tooltipTextView
            r3 = 2131694128(0x7f0f1230, float:1.9017404E38)
            java.lang.String r4 = "TapForVideo"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            android.widget.TextView r2 = r6.tooltipTextView
            r3 = 1079334215(0x40555547, float:3.33333)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            r4 = 1059749626(0x3f2a7efa, float:0.666)
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r5 = 1275068416(0x4c000000, float:3.3554432E7)
            r2.setShadowLayer(r3, r11, r4, r5)
            android.widget.TextView r2 = r6.tooltipTextView
            r3 = 1086324736(0x40c00000, float:6.0)
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            r2.setPadding(r4, r10, r3, r10)
            android.widget.FrameLayout r2 = r6.cameraPanel
            android.widget.TextView r3 = r6.tooltipTextView
            r31 = -1073741824(0xffffffffc0000000, float:-2.0)
            r32 = -1073741824(0xffffffffc0000000, float:-2.0)
            r33 = 81
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 1098907648(0x41800000, float:16.0)
            android.widget.FrameLayout$LayoutParams r4 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r31, r32, r33, r34, r35, r36, r37)
            r2.addView(r3, r4)
            im.bclpbkiauv.ui.components.ChatAttachAlert$20 r2 = new im.bclpbkiauv.ui.components.ChatAttachAlert$20
            r2.<init>(r7)
            r6.cameraPhotoRecyclerView = r2
            r2.setVerticalScrollBarEnabled(r9)
            im.bclpbkiauv.ui.components.RecyclerListView r2 = r6.cameraPhotoRecyclerView
            im.bclpbkiauv.ui.components.ChatAttachAlert$PhotoAttachAdapter r3 = new im.bclpbkiauv.ui.components.ChatAttachAlert$PhotoAttachAdapter
            r3.<init>(r7, r10)
            r6.cameraAttachAdapter = r3
            r2.setAdapter(r3)
            im.bclpbkiauv.ui.components.RecyclerListView r2 = r6.cameraPhotoRecyclerView
            r2.setClipToPadding(r10)
            im.bclpbkiauv.ui.components.RecyclerListView r2 = r6.cameraPhotoRecyclerView
            r3 = 1090519040(0x41000000, float:8.0)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            r4 = 1090519040(0x41000000, float:8.0)
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
            r2.setPadding(r3, r10, r4, r10)
            im.bclpbkiauv.ui.components.RecyclerListView r2 = r6.cameraPhotoRecyclerView
            r3 = 0
            r2.setItemAnimator(r3)
            im.bclpbkiauv.ui.components.RecyclerListView r2 = r6.cameraPhotoRecyclerView
            r2.setLayoutAnimation(r3)
            im.bclpbkiauv.ui.components.RecyclerListView r2 = r6.cameraPhotoRecyclerView
            r3 = 2
            r2.setOverScrollMode(r3)
            im.bclpbkiauv.ui.components.RecyclerListView r2 = r6.cameraPhotoRecyclerView
            r3 = 4
            r2.setVisibility(r3)
            im.bclpbkiauv.ui.components.RecyclerListView r2 = r6.cameraPhotoRecyclerView
            r2.setAlpha(r11)
            im.bclpbkiauv.ui.actionbar.BottomSheet$ContainerView r2 = r6.container
            im.bclpbkiauv.ui.components.RecyclerListView r3 = r6.cameraPhotoRecyclerView
            r4 = 1117782016(0x42a00000, float:80.0)
            r5 = -1
            android.widget.FrameLayout$LayoutParams r4 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r5, r4)
            r2.addView(r3, r4)
            im.bclpbkiauv.ui.components.ChatAttachAlert$21 r2 = new im.bclpbkiauv.ui.components.ChatAttachAlert$21
            r2.<init>(r7, r10, r10)
            r6.cameraPhotoLayoutManager = r2
            im.bclpbkiauv.ui.components.RecyclerListView r3 = r6.cameraPhotoRecyclerView
            r3.setLayoutManager(r2)
            im.bclpbkiauv.ui.components.RecyclerListView r2 = r6.cameraPhotoRecyclerView
            im.bclpbkiauv.ui.components.-$$Lambda$ChatAttachAlert$nRIqlEOZMy0irPJZ42ayRIvEwdE r3 = im.bclpbkiauv.ui.components.$$Lambda$ChatAttachAlert$nRIqlEOZMy0irPJZ42ayRIvEwdE.INSTANCE
            r2.setOnItemClickListener((im.bclpbkiauv.ui.components.RecyclerListView.OnItemClickListener) r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.ChatAttachAlert.<init>(android.content.Context, im.bclpbkiauv.ui.actionbar.BaseFragment):void");
    }

    public /* synthetic */ void lambda$new$0$ChatAttachAlert(int id) {
        this.actionBar.getActionBarMenuOnItemClick().onItemClick(id);
    }

    public /* synthetic */ void lambda$new$1$ChatAttachAlert(View v) {
        this.selectedMenuItem.toggleSubMenu();
    }

    public /* synthetic */ void lambda$new$2$ChatAttachAlert(View view, int position) {
        BaseFragment baseFragment2;
        int type;
        ChatActivity chatActivity;
        if (this.mediaEnabled && (baseFragment2 = this.baseFragment) != null && baseFragment2.getParentActivity() != null) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (position == 0 && this.noCameraPermissions) {
                    try {
                        this.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 18);
                        return;
                    } catch (Exception e) {
                        return;
                    }
                } else if (this.noGalleryPermissions) {
                    try {
                        this.baseFragment.getParentActivity().requestPermissions(new String[]{PermissionUtils.PERMISSION_READ_EXTERNAL_STORAGE}, 4);
                        return;
                    } catch (Exception e2) {
                        return;
                    }
                }
            }
            if (position != 0 || this.selectedAlbumEntry != this.galleryAlbumEntry) {
                if (this.selectedAlbumEntry == this.galleryAlbumEntry) {
                    position--;
                }
                ArrayList<Object> arrayList = getAllPhotosArray();
                if (position >= 0 && position < arrayList.size()) {
                    PhotoViewer.getInstance().setParentActivity(this.baseFragment.getParentActivity());
                    PhotoViewer.getInstance().setParentAlert(this);
                    PhotoViewer.getInstance().setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
                    BaseFragment baseFragment3 = this.baseFragment;
                    if (baseFragment3 instanceof ChatActivity) {
                        chatActivity = (ChatActivity) baseFragment3;
                        type = 0;
                    } else {
                        chatActivity = null;
                        type = 4;
                    }
                    PhotoViewer.getInstance().openPhotoForSelect(arrayList, position, type, this.photoViewerProvider, chatActivity);
                    AndroidUtilities.hideKeyboard(this.baseFragment.getFragmentView().findFocus());
                }
            } else if (SharedConfig.inappCamera) {
                openCamera(true);
            } else {
                ChatAttachViewDelegate chatAttachViewDelegate = this.delegate;
                if (chatAttachViewDelegate != null) {
                    chatAttachViewDelegate.didPressedButton(0, false, true, 0);
                }
            }
        }
    }

    public /* synthetic */ boolean lambda$new$3$ChatAttachAlert(View view, int position) {
        if (position == 0 && this.selectedAlbumEntry == this.galleryAlbumEntry) {
            ChatAttachViewDelegate chatAttachViewDelegate = this.delegate;
            if (chatAttachViewDelegate != null) {
                chatAttachViewDelegate.didPressedButton(0, false, true, 0);
            }
            return true;
        }
        if (view instanceof PhotoAttachPhotoCell) {
            RecyclerViewItemRangeSelector recyclerViewItemRangeSelector = this.itemRangeSelector;
            boolean z = !((PhotoAttachPhotoCell) view).isChecked();
            this.shouldSelect = z;
            recyclerViewItemRangeSelector.setIsActive(view, true, position, z);
        }
        return false;
    }

    public /* synthetic */ void lambda$new$4$ChatAttachAlert(View view) {
        this.dropDownContainer.toggleSubMenu();
    }

    public /* synthetic */ void lambda$new$5$ChatAttachAlert(View view, int position) {
        if (view instanceof AttachButton) {
            this.delegate.didPressedButton(((Integer) ((AttachButton) view).getTag()).intValue(), true, true, 0);
        } else if (view instanceof AttachBotButton) {
            this.delegate.didSelectBot(((AttachBotButton) view).currentUser);
            dismiss();
        }
    }

    public /* synthetic */ boolean lambda$new$7$ChatAttachAlert(View view, int position) {
        if (!(view instanceof AttachBotButton)) {
            return false;
        }
        AttachBotButton button = (AttachBotButton) view;
        if (this.baseFragment == null || button.currentUser == null) {
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setMessage(LocaleController.formatString("ChatHintsDelete", R.string.ChatHintsDelete, ContactsController.formatName(button.currentUser.first_name, button.currentUser.last_name)));
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener(button) {
            private final /* synthetic */ ChatAttachAlert.AttachBotButton f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                ChatAttachAlert.this.lambda$null$6$ChatAttachAlert(this.f$1, dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        builder.show();
        return true;
    }

    public /* synthetic */ void lambda$null$6$ChatAttachAlert(AttachBotButton button, DialogInterface dialogInterface, int i) {
        MediaDataController.getInstance(this.currentAccount).removeInline(button.currentUser.id);
    }

    static /* synthetic */ boolean lambda$new$8(View v, MotionEvent event) {
        return true;
    }

    public /* synthetic */ void lambda$new$9$ChatAttachAlert(BaseFragment parentFragment, View v) {
        if (this.editingMessageObject != null || !(parentFragment instanceof ChatActivity) || !((ChatActivity) parentFragment).isInScheduleMode()) {
            sendPressed(true, 0);
        } else {
            AlertsCreator.createScheduleDatePickerDialog(getContext(), UserObject.isUserSelf(((ChatActivity) parentFragment).getCurrentUser()), new AlertsCreator.ScheduleDatePickerDelegate() {
                public final void didSelectDate(boolean z, int i) {
                    ChatAttachAlert.this.sendPressed(z, i);
                }
            });
        }
    }

    public /* synthetic */ boolean lambda$new$12$ChatAttachAlert(View view) {
        View view2 = view;
        BaseFragment baseFragment2 = this.baseFragment;
        if (!(baseFragment2 instanceof ChatActivity) || this.editingMessageObject != null) {
            return false;
        }
        ChatActivity chatActivity = (ChatActivity) baseFragment2;
        TLRPC.Chat currentChat = chatActivity.getCurrentChat();
        TLRPC.User user = chatActivity.getCurrentUser();
        if (chatActivity.getCurrentEncryptedChat() != null || chatActivity.isInScheduleMode()) {
            return false;
        }
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getContext());
        this.sendPopupLayout = actionBarPopupWindowLayout;
        actionBarPopupWindowLayout.setAnimationEnabled(false);
        this.sendPopupLayout.setOnTouchListener(new View.OnTouchListener() {
            private Rect popupRect = new Rect();

            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() != 0 || ChatAttachAlert.this.sendPopupWindow == null || !ChatAttachAlert.this.sendPopupWindow.isShowing()) {
                    return false;
                }
                v.getHitRect(this.popupRect);
                if (this.popupRect.contains((int) event.getX(), (int) event.getY())) {
                    return false;
                }
                ChatAttachAlert.this.sendPopupWindow.dismiss();
                return false;
            }
        });
        this.sendPopupLayout.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() {
            public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                ChatAttachAlert.this.lambda$null$10$ChatAttachAlert(keyEvent);
            }
        });
        this.sendPopupLayout.setShowedFromBotton(false);
        this.itemCells = new ActionBarMenuSubItem[2];
        int i = 0;
        for (int a = 0; a < 2; a++) {
            if (a == 0) {
                boolean hasTtl = false;
                Iterator<Map.Entry<Object, Object>> it = selectedPhotos.entrySet().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    Object object = it.next().getValue();
                    if (!(object instanceof MediaController.PhotoEntry)) {
                        if ((object instanceof MediaController.SearchImage) && ((MediaController.SearchImage) object).ttl != 0) {
                            hasTtl = true;
                            break;
                        }
                    } else if (((MediaController.PhotoEntry) object).ttl != 0) {
                        hasTtl = true;
                        break;
                    }
                }
                if (hasTtl) {
                }
            } else if (a == 1 && UserObject.isUserSelf(user)) {
            }
            int num = a;
            this.itemCells[a] = new ActionBarMenuSubItem(getContext());
            if (num == 0) {
                if (UserObject.isUserSelf(user)) {
                    this.itemCells[a].setTextAndIcon(LocaleController.getString("SetReminder", R.string.SetReminder), R.drawable.msg_schedule);
                } else {
                    this.itemCells[a].setTextAndIcon(LocaleController.getString("ScheduleMessage", R.string.ScheduleMessage), R.drawable.msg_schedule);
                }
            } else if (num == 1) {
                this.itemCells[a].setTextAndIcon(LocaleController.getString("SendWithoutSound", R.string.SendWithoutSound), R.drawable.input_notify_off);
            }
            this.itemCells[a].setMinimumWidth(AndroidUtilities.dp(196.0f));
            this.sendPopupLayout.addView(this.itemCells[a], LayoutHelper.createFrame(-1.0f, 48.0f, LocaleController.isRTL ? 5 : 3, 0.0f, (float) (i * 48), 0.0f, 0.0f));
            this.itemCells[a].setOnClickListener(new View.OnClickListener(num, user) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ TLRPC.User f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(View view) {
                    ChatAttachAlert.this.lambda$null$11$ChatAttachAlert(this.f$1, this.f$2, view);
                }
            });
            i++;
        }
        ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(this.sendPopupLayout, -2, -2);
        this.sendPopupWindow = actionBarPopupWindow;
        actionBarPopupWindow.setAnimationEnabled(false);
        this.sendPopupWindow.setAnimationStyle(R.style.PopupContextAnimation2);
        this.sendPopupWindow.setOutsideTouchable(true);
        this.sendPopupWindow.setClippingEnabled(true);
        this.sendPopupWindow.setInputMethodMode(2);
        this.sendPopupWindow.setSoftInputMode(0);
        this.sendPopupWindow.getContentView().setFocusableInTouchMode(true);
        this.sendPopupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
        this.sendPopupWindow.setFocusable(true);
        int[] location = new int[2];
        view2.getLocationInWindow(location);
        this.sendPopupWindow.showAtLocation(view2, 51, ((location[0] + view.getMeasuredWidth()) - this.sendPopupLayout.getMeasuredWidth()) + AndroidUtilities.dp(8.0f), (location[1] - this.sendPopupLayout.getMeasuredHeight()) - AndroidUtilities.dp(2.0f));
        this.sendPopupWindow.dimBehind();
        view2.performHapticFeedback(3, 2);
        return false;
    }

    public /* synthetic */ void lambda$null$10$ChatAttachAlert(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    public /* synthetic */ void lambda$null$11$ChatAttachAlert(int num, TLRPC.User user, View v) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        if (num == 0) {
            AlertsCreator.createScheduleDatePickerDialog(getContext(), UserObject.isUserSelf(user), new AlertsCreator.ScheduleDatePickerDelegate() {
                public final void didSelectDate(boolean z, int i) {
                    ChatAttachAlert.this.sendPressed(z, i);
                }
            });
        } else if (num == 1) {
            sendPressed(true, 0);
        }
    }

    public /* synthetic */ void lambda$new$13$ChatAttachAlert(View v) {
        if (this.cameraView != null) {
            openPhotoViewer((MediaController.PhotoEntry) null, false, false);
            CameraController.getInstance().stopPreview(this.cameraView.getCameraSession());
        }
    }

    public /* synthetic */ void lambda$new$14$ChatAttachAlert(float zoom) {
        CameraView cameraView2 = this.cameraView;
        if (cameraView2 != null) {
            this.cameraZoom = zoom;
            cameraView2.setZoom(zoom);
        }
        showZoomControls(true, true);
    }

    public /* synthetic */ void lambda$new$15$ChatAttachAlert(View v) {
        CameraView cameraView2;
        if (!this.takingPhoto && (cameraView2 = this.cameraView) != null && cameraView2.isInitied()) {
            this.cameraInitied = false;
            this.cameraView.switchCamera();
            ObjectAnimator animator = ObjectAnimator.ofFloat(this.switchCameraButton, View.SCALE_X, new float[]{0.0f}).setDuration(100);
            animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    ChatAttachAlert.this.switchCameraButton.setImageResource((ChatAttachAlert.this.cameraView == null || !ChatAttachAlert.this.cameraView.isFrontface()) ? R.drawable.camera_revert2 : R.drawable.camera_revert1);
                    ObjectAnimator.ofFloat(ChatAttachAlert.this.switchCameraButton, View.SCALE_X, new float[]{1.0f}).setDuration(100).start();
                }
            });
            animator.start();
        }
    }

    public /* synthetic */ void lambda$new$16$ChatAttachAlert(final View currentImage) {
        CameraView cameraView2;
        if (!this.flashAnimationInProgress && (cameraView2 = this.cameraView) != null && cameraView2.isInitied() && this.cameraOpened) {
            String current = this.cameraView.getCameraSession().getCurrentFlashMode();
            String next = this.cameraView.getCameraSession().getNextFlashMode();
            if (!current.equals(next)) {
                this.cameraView.getCameraSession().setCurrentFlashMode(next);
                this.flashAnimationInProgress = true;
                ImageView[] imageViewArr = this.flashModeButton;
                final ImageView nextImage = imageViewArr[0] == currentImage ? imageViewArr[1] : imageViewArr[0];
                nextImage.setVisibility(0);
                setCameraFlashModeIcon(nextImage, next);
                AnimatorSet animatorSet2 = new AnimatorSet();
                animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(currentImage, View.TRANSLATION_Y, new float[]{0.0f, (float) AndroidUtilities.dp(48.0f)}), ObjectAnimator.ofFloat(nextImage, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(48.0f)), 0.0f}), ObjectAnimator.ofFloat(currentImage, View.ALPHA, new float[]{1.0f, 0.0f}), ObjectAnimator.ofFloat(nextImage, View.ALPHA, new float[]{0.0f, 1.0f})});
                animatorSet2.setDuration(200);
                animatorSet2.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        boolean unused = ChatAttachAlert.this.flashAnimationInProgress = false;
                        currentImage.setVisibility(4);
                        nextImage.sendAccessibilityEvent(8);
                    }
                });
                animatorSet2.start();
            }
        }
    }

    static /* synthetic */ void lambda$new$17(View view, int position) {
        if (view instanceof PhotoAttachPhotoCell) {
            ((PhotoAttachPhotoCell) view).callDelegate();
        }
    }

    public void show() {
        super.show();
        this.buttonPressed = false;
    }

    public void setEditingMessageObject(MessageObject messageObject) {
        if (this.editingMessageObject != messageObject) {
            this.editingMessageObject = messageObject;
            if (messageObject != null) {
                this.maxSelectedPhotos = 1;
                this.allowOrder = false;
            } else {
                this.maxSelectedPhotos = -1;
                this.allowOrder = true;
            }
            this.buttonsAdapter.notifyDataSetChanged();
        }
    }

    public MessageObject getEditingMessageObject() {
        return this.editingMessageObject;
    }

    /* access modifiers changed from: private */
    public void applyCaption() {
        if (this.commentTextView.length() > 0) {
            Object entry = selectedPhotos.get(Integer.valueOf(((Integer) selectedPhotosOrder.get(0)).intValue()));
            if (entry instanceof MediaController.PhotoEntry) {
                ((MediaController.PhotoEntry) entry).caption = this.commentTextView.getText().toString();
            } else if (entry instanceof MediaController.SearchImage) {
                ((MediaController.SearchImage) entry).caption = this.commentTextView.getText().toString();
            }
        }
    }

    /* access modifiers changed from: private */
    public void sendPressed(boolean notify, int scheduleDate) {
        if (!this.buttonPressed) {
            BaseFragment baseFragment2 = this.baseFragment;
            if (baseFragment2 instanceof ChatActivity) {
                ChatActivity chatActivity = (ChatActivity) baseFragment2;
                TLRPC.Chat chat = chatActivity.getCurrentChat();
                if (chatActivity.getCurrentUser() != null || ((ChatObject.isChannel(chat) && chat.megagroup) || !ChatObject.isChannel(chat))) {
                    SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                    edit.putBoolean("silent_" + chatActivity.getDialogId(), !notify).commit();
                }
            }
            applyCaption();
            this.buttonPressed = true;
            this.delegate.didPressedButton(7, true, notify, scheduleDate);
        }
    }

    /* access modifiers changed from: private */
    public void updatePhotosCounter(boolean added) {
        if (this.counterTextView != null) {
            boolean hasVideo = false;
            boolean hasPhotos = false;
            for (Map.Entry<Object, Object> entry : selectedPhotos.entrySet()) {
                if (((MediaController.PhotoEntry) entry.getValue()).isVideo) {
                    hasVideo = true;
                } else {
                    hasPhotos = true;
                }
                if (hasVideo && hasPhotos) {
                    break;
                }
            }
            int newSelectedCount = Math.max(1, selectedPhotos.size());
            if (hasVideo && hasPhotos) {
                this.counterTextView.setText(LocaleController.formatPluralString("Media", selectedPhotos.size()).toUpperCase());
                if (newSelectedCount != this.currentSelectedCount || added) {
                    this.selectedTextView.setText(LocaleController.formatPluralString("MediaSelected", newSelectedCount));
                }
            } else if (hasVideo) {
                this.counterTextView.setText(LocaleController.formatPluralString("Videos", selectedPhotos.size()).toUpperCase());
                if (newSelectedCount != this.currentSelectedCount || added) {
                    this.selectedTextView.setText(LocaleController.formatPluralString("VideosSelected", newSelectedCount));
                }
            } else {
                this.counterTextView.setText(LocaleController.formatPluralString("Photos", selectedPhotos.size()).toUpperCase());
                if (newSelectedCount != this.currentSelectedCount || added) {
                    this.selectedTextView.setText(LocaleController.formatPluralString("PhotosSelected", newSelectedCount));
                }
            }
            this.currentSelectedCount = newSelectedCount;
        }
    }

    private boolean showCommentTextView(final boolean show, boolean animated) {
        if (show == (this.frameLayout2.getTag() != null)) {
            return false;
        }
        AnimatorSet animatorSet2 = this.animatorSet;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
        }
        this.frameLayout2.setTag(show ? 1 : null);
        if (this.commentTextView.getEditText().isFocused()) {
            AndroidUtilities.hideKeyboard(this.commentTextView.getEditText());
        }
        this.commentTextView.hidePopup(true);
        if (show) {
            this.frameLayout2.setVisibility(0);
            this.writeButtonContainer.setVisibility(0);
        }
        float f = 0.2f;
        float f2 = 0.0f;
        if (animated) {
            this.animatorSet = new AnimatorSet();
            ArrayList<Animator> animators = new ArrayList<>();
            FrameLayout frameLayout = this.frameLayout2;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = show ? 1.0f : 0.0f;
            animators.add(ObjectAnimator.ofFloat(frameLayout, property, fArr));
            FrameLayout frameLayout3 = this.writeButtonContainer;
            Property property2 = View.SCALE_X;
            float[] fArr2 = new float[1];
            fArr2[0] = show ? 1.0f : 0.2f;
            animators.add(ObjectAnimator.ofFloat(frameLayout3, property2, fArr2));
            FrameLayout frameLayout4 = this.writeButtonContainer;
            Property property3 = View.SCALE_Y;
            float[] fArr3 = new float[1];
            fArr3[0] = show ? 1.0f : 0.2f;
            animators.add(ObjectAnimator.ofFloat(frameLayout4, property3, fArr3));
            FrameLayout frameLayout5 = this.writeButtonContainer;
            Property property4 = View.ALPHA;
            float[] fArr4 = new float[1];
            fArr4[0] = show ? 1.0f : 0.0f;
            animators.add(ObjectAnimator.ofFloat(frameLayout5, property4, fArr4));
            View view = this.selectedCountView;
            Property property5 = View.SCALE_X;
            float[] fArr5 = new float[1];
            fArr5[0] = show ? 1.0f : 0.2f;
            animators.add(ObjectAnimator.ofFloat(view, property5, fArr5));
            View view2 = this.selectedCountView;
            Property property6 = View.SCALE_Y;
            float[] fArr6 = new float[1];
            if (show) {
                f = 1.0f;
            }
            fArr6[0] = f;
            animators.add(ObjectAnimator.ofFloat(view2, property6, fArr6));
            View view3 = this.selectedCountView;
            Property property7 = View.ALPHA;
            float[] fArr7 = new float[1];
            fArr7[0] = show ? 1.0f : 0.0f;
            animators.add(ObjectAnimator.ofFloat(view3, property7, fArr7));
            if (this.actionBar.getTag() != null) {
                FrameLayout frameLayout6 = this.frameLayout2;
                Property property8 = View.TRANSLATION_Y;
                float[] fArr8 = new float[1];
                fArr8[0] = show ? 0.0f : (float) AndroidUtilities.dp(48.0f);
                animators.add(ObjectAnimator.ofFloat(frameLayout6, property8, fArr8));
                View view4 = this.shadow;
                Property property9 = View.TRANSLATION_Y;
                float[] fArr9 = new float[1];
                fArr9[0] = (float) (show ? AndroidUtilities.dp(44.0f) : AndroidUtilities.dp(92.0f));
                animators.add(ObjectAnimator.ofFloat(view4, property9, fArr9));
                View view5 = this.shadow;
                Property property10 = View.ALPHA;
                float[] fArr10 = new float[1];
                if (show) {
                    f2 = 1.0f;
                }
                fArr10[0] = f2;
                animators.add(ObjectAnimator.ofFloat(view5, property10, fArr10));
            } else {
                RecyclerListView recyclerListView = this.buttonsRecyclerView;
                Property property11 = View.TRANSLATION_Y;
                float[] fArr11 = new float[1];
                fArr11[0] = show ? (float) AndroidUtilities.dp(44.0f) : 0.0f;
                animators.add(ObjectAnimator.ofFloat(recyclerListView, property11, fArr11));
                View view6 = this.shadow;
                Property property12 = View.TRANSLATION_Y;
                float[] fArr12 = new float[1];
                if (show) {
                    f2 = (float) AndroidUtilities.dp(44.0f);
                }
                fArr12[0] = f2;
                animators.add(ObjectAnimator.ofFloat(view6, property12, fArr12));
            }
            this.animatorSet.playTogether(animators);
            this.animatorSet.setInterpolator(new DecelerateInterpolator());
            this.animatorSet.setDuration(180);
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (animation.equals(ChatAttachAlert.this.animatorSet)) {
                        if (!show) {
                            ChatAttachAlert.this.frameLayout2.setVisibility(4);
                            ChatAttachAlert.this.writeButtonContainer.setVisibility(4);
                        }
                        AnimatorSet unused = ChatAttachAlert.this.animatorSet = null;
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (animation.equals(ChatAttachAlert.this.animatorSet)) {
                        AnimatorSet unused = ChatAttachAlert.this.animatorSet = null;
                    }
                }
            });
            this.animatorSet.start();
        } else {
            this.frameLayout2.setAlpha(show ? 1.0f : 0.0f);
            this.writeButtonContainer.setScaleX(show ? 1.0f : 0.2f);
            this.writeButtonContainer.setScaleY(show ? 1.0f : 0.2f);
            this.writeButtonContainer.setAlpha(show ? 1.0f : 0.0f);
            this.selectedCountView.setScaleX(show ? 1.0f : 0.2f);
            View view7 = this.selectedCountView;
            if (show) {
                f = 1.0f;
            }
            view7.setScaleY(f);
            this.selectedCountView.setAlpha(show ? 1.0f : 0.0f);
            if (this.actionBar.getTag() != null) {
                this.frameLayout2.setTranslationY(show ? 0.0f : (float) AndroidUtilities.dp(48.0f));
                this.shadow.setTranslationY((float) (show ? AndroidUtilities.dp(44.0f) : AndroidUtilities.dp(92.0f)));
                View view8 = this.shadow;
                if (show) {
                    f2 = 1.0f;
                }
                view8.setAlpha(f2);
            } else {
                this.buttonsRecyclerView.setTranslationY(show ? (float) AndroidUtilities.dp(44.0f) : 0.0f);
                View view9 = this.shadow;
                if (show) {
                    f2 = (float) AndroidUtilities.dp(44.0f);
                }
                view9.setTranslationY(f2);
            }
            if (!show) {
                this.frameLayout2.setVisibility(4);
                this.writeButtonContainer.setVisibility(4);
            }
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean onCustomOpenAnimation() {
        AnimatorSet animatorSet2 = new AnimatorSet();
        animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, this.ATTACH_ALERT_PROGRESS, new float[]{0.0f, 400.0f})});
        animatorSet2.setDuration(400);
        animatorSet2.setStartDelay(20);
        animatorSet2.start();
        return false;
    }

    /* access modifiers changed from: private */
    public void openPhotoViewer(MediaController.PhotoEntry entry, final boolean sameTakePictureOrientation, boolean external) {
        int type;
        ChatActivity chatActivity;
        if (entry != null) {
            cameraPhotos.add(entry);
            selectedPhotos.put(Integer.valueOf(entry.imageId), entry);
            selectedPhotosOrder.add(Integer.valueOf(entry.imageId));
            updatePhotosButton(0);
            this.adapter.notifyDataSetChanged();
            this.cameraAttachAdapter.notifyDataSetChanged();
        }
        if (entry != null && !external && cameraPhotos.size() > 1) {
            updatePhotosCounter(false);
            if (this.cameraView != null) {
                this.zoomControlView.setZoom(0.0f, false);
                this.cameraZoom = 0.0f;
                this.cameraView.setZoom(0.0f);
                CameraController.getInstance().startPreview(this.cameraView.getCameraSession());
            }
            this.mediaCaptured = false;
        } else if (!cameraPhotos.isEmpty()) {
            this.cancelTakingPhotos = true;
            PhotoViewer.getInstance().setParentActivity(this.baseFragment.getParentActivity());
            PhotoViewer.getInstance().setParentAlert(this);
            PhotoViewer.getInstance().setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
            BaseFragment baseFragment2 = this.baseFragment;
            if (baseFragment2 instanceof ChatActivity) {
                chatActivity = (ChatActivity) baseFragment2;
                type = 2;
            } else {
                chatActivity = null;
                type = 5;
            }
            PhotoViewer.getInstance().openPhotoForSelect(getAllPhotosArray(), cameraPhotos.size() - 1, type, new BasePhotoProvider() {
                public ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index) {
                    return null;
                }

                public boolean cancelButtonPressed() {
                    if (ChatAttachAlert.this.cameraOpened && ChatAttachAlert.this.cameraView != null) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public final void run() {
                                ChatAttachAlert.AnonymousClass24.this.lambda$cancelButtonPressed$0$ChatAttachAlert$24();
                            }
                        }, 1000);
                        ChatAttachAlert.this.zoomControlView.setZoom(0.0f, false);
                        float unused = ChatAttachAlert.this.cameraZoom = 0.0f;
                        ChatAttachAlert.this.cameraView.setZoom(0.0f);
                        CameraController.getInstance().startPreview(ChatAttachAlert.this.cameraView.getCameraSession());
                    }
                    if (ChatAttachAlert.this.cancelTakingPhotos && ChatAttachAlert.cameraPhotos.size() == 1) {
                        int size = ChatAttachAlert.cameraPhotos.size();
                        for (int a = 0; a < size; a++) {
                            MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) ChatAttachAlert.cameraPhotos.get(a);
                            new File(photoEntry.path).delete();
                            if (photoEntry.imagePath != null) {
                                new File(photoEntry.imagePath).delete();
                            }
                            if (photoEntry.thumbPath != null) {
                                new File(photoEntry.thumbPath).delete();
                            }
                        }
                        ChatAttachAlert.cameraPhotos.clear();
                        ChatAttachAlert.selectedPhotosOrder.clear();
                        ChatAttachAlert.selectedPhotos.clear();
                        ChatAttachAlert.this.counterTextView.setVisibility(4);
                        ChatAttachAlert.this.cameraPhotoRecyclerView.setVisibility(8);
                        ChatAttachAlert.this.adapter.notifyDataSetChanged();
                        ChatAttachAlert.this.cameraAttachAdapter.notifyDataSetChanged();
                        ChatAttachAlert.this.updatePhotosButton(0);
                    }
                    return true;
                }

                public /* synthetic */ void lambda$cancelButtonPressed$0$ChatAttachAlert$24() {
                    if (ChatAttachAlert.this.cameraView != null && !ChatAttachAlert.this.isDismissed() && Build.VERSION.SDK_INT >= 21) {
                        ChatAttachAlert.this.cameraView.setSystemUiVisibility(1028);
                    }
                }

                public void needAddMorePhotos() {
                    boolean unused = ChatAttachAlert.this.cancelTakingPhotos = false;
                    if (ChatAttachAlert.mediaFromExternalCamera) {
                        ChatAttachAlert.this.delegate.didPressedButton(0, true, true, 0);
                        return;
                    }
                    if (!ChatAttachAlert.this.cameraOpened) {
                        ChatAttachAlert.this.openCamera(false);
                    }
                    ChatAttachAlert.this.counterTextView.setVisibility(0);
                    ChatAttachAlert.this.cameraPhotoRecyclerView.setVisibility(0);
                    ChatAttachAlert.this.counterTextView.setAlpha(1.0f);
                    ChatAttachAlert.this.updatePhotosCounter(false);
                }

                public void sendButtonPressed(int index, VideoEditedInfo videoEditedInfo, boolean notify, int scheduleDate) {
                    if (!ChatAttachAlert.cameraPhotos.isEmpty() && ChatAttachAlert.this.baseFragment != null) {
                        if (videoEditedInfo != null && index >= 0 && index < ChatAttachAlert.cameraPhotos.size()) {
                            ((MediaController.PhotoEntry) ChatAttachAlert.cameraPhotos.get(index)).editedInfo = videoEditedInfo;
                        }
                        if (!(ChatAttachAlert.this.baseFragment instanceof ChatActivity) || !((ChatActivity) ChatAttachAlert.this.baseFragment).isSecretChat()) {
                            int size = ChatAttachAlert.cameraPhotos.size();
                            for (int a = 0; a < size; a++) {
                                AndroidUtilities.addMediaToGallery(((MediaController.PhotoEntry) ChatAttachAlert.cameraPhotos.get(a)).path);
                            }
                        }
                        ChatAttachAlert.this.applyCaption();
                        ChatAttachAlert.this.delegate.didPressedButton(8, true, notify, scheduleDate);
                        ChatAttachAlert.cameraPhotos.clear();
                        ChatAttachAlert.selectedPhotosOrder.clear();
                        ChatAttachAlert.selectedPhotos.clear();
                        ChatAttachAlert.this.adapter.notifyDataSetChanged();
                        ChatAttachAlert.this.cameraAttachAdapter.notifyDataSetChanged();
                        ChatAttachAlert.this.closeCamera(false);
                        ChatAttachAlert.this.dismiss();
                    }
                }

                public boolean scaleToFill() {
                    if (ChatAttachAlert.this.baseFragment == null || ChatAttachAlert.this.baseFragment.getParentActivity() == null) {
                        return false;
                    }
                    int locked = Settings.System.getInt(ChatAttachAlert.this.baseFragment.getParentActivity().getContentResolver(), "accelerometer_rotation", 0);
                    if (sameTakePictureOrientation || locked == 1) {
                        return true;
                    }
                    return false;
                }

                public void willHidePhotoViewer() {
                    boolean unused = ChatAttachAlert.this.mediaCaptured = false;
                    int count = ChatAttachAlert.this.gridView.getChildCount();
                    for (int a = 0; a < count; a++) {
                        View view = ChatAttachAlert.this.gridView.getChildAt(a);
                        if (view instanceof PhotoAttachPhotoCell) {
                            PhotoAttachPhotoCell cell = (PhotoAttachPhotoCell) view;
                            cell.showImage();
                            cell.showCheck(true);
                        }
                    }
                }

                public boolean canScrollAway() {
                    return false;
                }

                public boolean canCaptureMorePhotos() {
                    return ChatAttachAlert.this.maxSelectedPhotos != 1;
                }
            }, chatActivity);
        }
    }

    /* access modifiers changed from: private */
    public void showZoomControls(boolean show, boolean animated) {
        if ((this.zoomControlView.getTag() == null || !show) && (this.zoomControlView.getTag() != null || show)) {
            AnimatorSet animatorSet2 = this.zoomControlAnimation;
            if (animatorSet2 != null) {
                animatorSet2.cancel();
            }
            this.zoomControlView.setTag(show ? 1 : null);
            AnimatorSet animatorSet3 = new AnimatorSet();
            this.zoomControlAnimation = animatorSet3;
            animatorSet3.setDuration(180);
            AnimatorSet animatorSet4 = this.zoomControlAnimation;
            Animator[] animatorArr = new Animator[1];
            ZoomControlView zoomControlView2 = this.zoomControlView;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = show ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(zoomControlView2, property, fArr);
            animatorSet4.playTogether(animatorArr);
            this.zoomControlAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    AnimatorSet unused = ChatAttachAlert.this.zoomControlAnimation = null;
                }
            });
            this.zoomControlAnimation.start();
            if (show) {
                $$Lambda$ChatAttachAlert$jtrnfPcFZ05AZnrOW4b1ILswE0g r0 = new Runnable() {
                    public final void run() {
                        ChatAttachAlert.this.lambda$showZoomControls$19$ChatAttachAlert();
                    }
                };
                this.zoomControlHideRunnable = r0;
                AndroidUtilities.runOnUIThread(r0, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
            }
        } else if (show) {
            Runnable runnable = this.zoomControlHideRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            $$Lambda$ChatAttachAlert$BViUsV7hJV0KtDtAO0feh8PcTs4 r02 = new Runnable() {
                public final void run() {
                    ChatAttachAlert.this.lambda$showZoomControls$18$ChatAttachAlert();
                }
            };
            this.zoomControlHideRunnable = r02;
            AndroidUtilities.runOnUIThread(r02, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        }
    }

    public /* synthetic */ void lambda$showZoomControls$18$ChatAttachAlert() {
        showZoomControls(false, true);
        this.zoomControlHideRunnable = null;
    }

    public /* synthetic */ void lambda$showZoomControls$19$ChatAttachAlert() {
        showZoomControls(false, true);
        this.zoomControlHideRunnable = null;
    }

    /* access modifiers changed from: private */
    public boolean processTouchEvent(MotionEvent event) {
        CameraView cameraView2;
        if (event == null) {
            return false;
        }
        if ((!this.pressed && event.getActionMasked() == 0) || event.getActionMasked() == 5) {
            this.zoomControlView.getHitRect(this.hitRect);
            if (this.zoomControlView.getTag() != null && this.hitRect.contains((int) event.getX(), (int) event.getY())) {
                return false;
            }
            if (!this.takingPhoto && !this.dragging) {
                if (event.getPointerCount() == 2) {
                    this.pinchStartDistance = (float) Math.hypot((double) (event.getX(1) - event.getX(0)), (double) (event.getY(1) - event.getY(0)));
                    this.zooming = true;
                } else {
                    this.maybeStartDraging = true;
                    this.lastY = event.getY();
                    this.zooming = false;
                }
                this.zoomWas = false;
                this.pressed = true;
            }
        } else if (this.pressed) {
            if (event.getActionMasked() == 2) {
                if (!this.zooming || event.getPointerCount() != 2 || this.dragging) {
                    float newY = event.getY();
                    float dy = newY - this.lastY;
                    if (this.maybeStartDraging) {
                        if (Math.abs(dy) > AndroidUtilities.getPixelsInCM(0.4f, false)) {
                            this.maybeStartDraging = false;
                            this.dragging = true;
                        }
                    } else if (this.dragging && (cameraView2 = this.cameraView) != null) {
                        cameraView2.setTranslationY(cameraView2.getTranslationY() + dy);
                        this.lastY = newY;
                        this.zoomControlView.setTag((Object) null);
                        Runnable runnable = this.zoomControlHideRunnable;
                        if (runnable != null) {
                            AndroidUtilities.cancelRunOnUIThread(runnable);
                            this.zoomControlHideRunnable = null;
                        }
                        if (this.cameraPanel.getTag() == null) {
                            this.cameraPanel.setTag(1);
                            AnimatorSet animatorSet2 = new AnimatorSet();
                            animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.cameraPanel, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.zoomControlView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.counterTextView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.flashModeButton[0], View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.flashModeButton[1], View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, View.ALPHA, new float[]{0.0f})});
                            animatorSet2.setDuration(200);
                            animatorSet2.start();
                        }
                    }
                } else {
                    float newDistance = (float) Math.hypot((double) (event.getX(1) - event.getX(0)), (double) (event.getY(1) - event.getY(0)));
                    if (this.zoomWas) {
                        float diff = (newDistance - this.pinchStartDistance) / ((float) AndroidUtilities.dp(100.0f));
                        this.pinchStartDistance = newDistance;
                        float f = this.cameraZoom + diff;
                        this.cameraZoom = f;
                        if (f < 0.0f) {
                            this.cameraZoom = 0.0f;
                        } else if (f > 1.0f) {
                            this.cameraZoom = 1.0f;
                        }
                        this.zoomControlView.setZoom(this.cameraZoom, false);
                        this.containerView.invalidate();
                        this.cameraView.setZoom(this.cameraZoom);
                        showZoomControls(true, true);
                    } else if (Math.abs(newDistance - this.pinchStartDistance) >= AndroidUtilities.getPixelsInCM(0.4f, false)) {
                        this.pinchStartDistance = newDistance;
                        this.zoomWas = true;
                    }
                }
            } else if (event.getActionMasked() == 3 || event.getActionMasked() == 1 || event.getActionMasked() == 6) {
                this.pressed = false;
                this.zooming = false;
                if (0 != 0) {
                    this.zooming = false;
                } else if (this.dragging) {
                    this.dragging = false;
                    CameraView cameraView3 = this.cameraView;
                    if (cameraView3 != null) {
                        if (Math.abs(cameraView3.getTranslationY()) > ((float) this.cameraView.getMeasuredHeight()) / 6.0f) {
                            closeCamera(true);
                        } else {
                            AnimatorSet animatorSet3 = new AnimatorSet();
                            animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.cameraView, View.TRANSLATION_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.cameraPanel, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.counterTextView, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.flashModeButton[0], View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.flashModeButton[1], View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, View.ALPHA, new float[]{1.0f})});
                            animatorSet3.setDuration(250);
                            animatorSet3.setInterpolator(this.interpolator);
                            animatorSet3.start();
                            this.cameraPanel.setTag((Object) null);
                        }
                    }
                } else {
                    CameraView cameraView4 = this.cameraView;
                    if (cameraView4 != null && !this.zoomWas) {
                        cameraView4.getLocationOnScreen(this.viewPosition);
                        this.cameraView.focusToPoint((int) (event.getRawX() - ((float) this.viewPosition[0])), (int) (event.getRawY() - ((float) this.viewPosition[1])));
                    }
                }
            }
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean onContainerTouchEvent(MotionEvent event) {
        return this.cameraOpened && processTouchEvent(event);
    }

    /* access modifiers changed from: private */
    public void applyAttachButtonColors(View view) {
        if (view instanceof AttachButton) {
            ((AttachButton) view).textView.setTextColor(Theme.getColor(Theme.key_dialogTextGray2));
        } else if (view instanceof AttachBotButton) {
            ((AttachBotButton) view).nameTextView.setTextColor(Theme.getColor(Theme.key_dialogTextGray2));
        }
    }

    public void checkColors() {
        RecyclerListView recyclerListView = this.buttonsRecyclerView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                applyAttachButtonColors(this.buttonsRecyclerView.getChildAt(a));
            }
            this.selectedTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
            this.selectedMenuItem.setIconColor(Theme.getColor(Theme.key_dialogTextBlack));
            Theme.setDrawableColor(this.selectedMenuItem.getBackground(), Theme.getColor(Theme.key_dialogButtonSelector));
            this.selectedMenuItem.setPopupItemsColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem), false);
            this.selectedMenuItem.setPopupItemsColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem), true);
            this.selectedMenuItem.redrawPopup(Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground));
            this.commentTextView.updateColors();
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.sendPopupLayout;
            if (actionBarPopupWindowLayout != null) {
                actionBarPopupWindowLayout.getBackgroundDrawable().setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground), PorterDuff.Mode.MULTIPLY));
                int a2 = 0;
                while (true) {
                    ActionBarMenuSubItem[] actionBarMenuSubItemArr = this.itemCells;
                    if (a2 >= actionBarMenuSubItemArr.length) {
                        break;
                    }
                    actionBarMenuSubItemArr[a2].setColors(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem), Theme.getColor(Theme.key_actionBarDefaultSubmenuItem));
                    a2++;
                }
            }
            Theme.setSelectorDrawableColor(this.writeButtonDrawable, Theme.getColor(Theme.key_dialogFloatingButton), false);
            Theme.setSelectorDrawableColor(this.writeButtonDrawable, Theme.getColor(Theme.key_dialogFloatingButtonPressed), true);
            this.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogFloatingIcon), PorterDuff.Mode.MULTIPLY));
            this.dropDown.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
            this.dropDownContainer.setPopupItemsColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem), false);
            this.dropDownContainer.setPopupItemsColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem), true);
            this.dropDownContainer.redrawPopup(Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground));
            this.actionBarShadow.setBackgroundColor(Theme.getColor(Theme.key_dialogShadowLine));
            this.progressView.setTextColor(Theme.getColor(Theme.key_emptyListPlaceholder));
            this.buttonsRecyclerView.setGlowColor(Theme.getColor(Theme.key_dialogScrollGlow));
            this.buttonsRecyclerView.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
            this.frameLayout2.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
            this.selectedCountView.invalidate();
            Theme.setDrawableColor(this.dropDownDrawable, Theme.getColor(Theme.key_dialogTextBlack));
            this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
            this.actionBar.setItemsColor(Theme.getColor(Theme.key_dialogTextBlack), false);
            this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_dialogButtonSelector), false);
            this.actionBar.setTitleColor(Theme.getColor(Theme.key_dialogTextBlack));
            Theme.setDrawableColor(this.shadowDrawable, Theme.getColor(Theme.key_dialogBackground));
            Theme.setDrawableColor(this.cameraDrawable, Theme.getColor(Theme.key_dialogCameraIcon));
            FrameLayout frameLayout = this.cameraIcon;
            if (frameLayout != null) {
                frameLayout.invalidate();
            }
            this.gridView.setGlowColor(Theme.getColor(Theme.key_dialogScrollGlow));
            RecyclerView.ViewHolder holder = this.gridView.findViewHolderForAdapterPosition(0);
            if (holder != null && (holder.itemView instanceof PhotoAttachCameraCell)) {
                ((PhotoAttachCameraCell) holder.itemView).getImageView().setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogCameraIcon), PorterDuff.Mode.MULTIPLY));
            }
            this.containerView.invalidate();
        }
    }

    /* access modifiers changed from: private */
    public void resetRecordState() {
        if (this.baseFragment != null) {
            for (int a = 0; a < 2; a++) {
                this.flashModeButton[a].setAlpha(1.0f);
            }
            this.switchCameraButton.setAlpha(1.0f);
            this.tooltipTextView.setAlpha(1.0f);
            this.recordTime.setAlpha(0.0f);
            AndroidUtilities.cancelRunOnUIThread(this.videoRecordRunnable);
            this.videoRecordRunnable = null;
            AndroidUtilities.unlockOrientation(this.baseFragment.getParentActivity());
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0036  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0061  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setCameraFlashModeIcon(android.widget.ImageView r5, java.lang.String r6) {
        /*
            r4 = this;
            int r0 = r6.hashCode()
            r1 = 3551(0xddf, float:4.976E-42)
            r2 = 2
            r3 = 1
            if (r0 == r1) goto L_0x0029
            r1 = 109935(0x1ad6f, float:1.54052E-40)
            if (r0 == r1) goto L_0x001f
            r1 = 3005871(0x2dddaf, float:4.212122E-39)
            if (r0 == r1) goto L_0x0015
        L_0x0014:
            goto L_0x0033
        L_0x0015:
            java.lang.String r0 = "auto"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x0014
            r0 = 2
            goto L_0x0034
        L_0x001f:
            java.lang.String r0 = "off"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x0014
            r0 = 0
            goto L_0x0034
        L_0x0029:
            java.lang.String r0 = "on"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x0014
            r0 = 1
            goto L_0x0034
        L_0x0033:
            r0 = -1
        L_0x0034:
            if (r0 == 0) goto L_0x0061
            if (r0 == r3) goto L_0x004e
            if (r0 == r2) goto L_0x003b
            goto L_0x0074
        L_0x003b:
            r0 = 2131231013(0x7f080125, float:1.8078095E38)
            r5.setImageResource(r0)
            r0 = 2131689492(0x7f0f0014, float:1.9008E38)
            java.lang.String r1 = "AccDescrCameraFlashAuto"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r5.setContentDescription(r0)
            goto L_0x0074
        L_0x004e:
            r0 = 2131231015(0x7f080127, float:1.80781E38)
            r5.setImageResource(r0)
            r0 = 2131689494(0x7f0f0016, float:1.9008005E38)
            java.lang.String r1 = "AccDescrCameraFlashOn"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r5.setContentDescription(r0)
            goto L_0x0074
        L_0x0061:
            r0 = 2131231014(0x7f080126, float:1.8078097E38)
            r5.setImageResource(r0)
            r0 = 2131689493(0x7f0f0015, float:1.9008003E38)
            java.lang.String r1 = "AccDescrCameraFlashOff"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            r5.setContentDescription(r0)
        L_0x0074:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.ChatAttachAlert.setCameraFlashModeIcon(android.widget.ImageView, java.lang.String):void");
    }

    /* access modifiers changed from: protected */
    public boolean onCustomMeasure(View view, int width, int height) {
        boolean isPortrait = width < height;
        FrameLayout frameLayout = this.cameraIcon;
        if (view == frameLayout) {
            frameLayout.measure(View.MeasureSpec.makeMeasureSpec(this.itemSize, 1073741824), View.MeasureSpec.makeMeasureSpec((this.itemSize - this.cameraViewOffsetBottomY) - this.cameraViewOffsetY, 1073741824));
            return true;
        }
        CameraView cameraView2 = this.cameraView;
        if (view != cameraView2) {
            FrameLayout frameLayout3 = this.cameraPanel;
            if (view == frameLayout3) {
                if (isPortrait) {
                    frameLayout3.measure(View.MeasureSpec.makeMeasureSpec(width, 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(126.0f), 1073741824));
                } else {
                    frameLayout3.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(126.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(height, 1073741824));
                }
                return true;
            }
            ZoomControlView zoomControlView2 = this.zoomControlView;
            if (view == zoomControlView2) {
                if (isPortrait) {
                    zoomControlView2.measure(View.MeasureSpec.makeMeasureSpec(width, 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), 1073741824));
                } else {
                    zoomControlView2.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(height, 1073741824));
                }
                return true;
            }
            RecyclerListView recyclerListView = this.cameraPhotoRecyclerView;
            if (view == recyclerListView) {
                this.cameraPhotoRecyclerViewIgnoreLayout = true;
                if (isPortrait) {
                    recyclerListView.measure(View.MeasureSpec.makeMeasureSpec(width, 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), 1073741824));
                    if (this.cameraPhotoLayoutManager.getOrientation() != 0) {
                        this.cameraPhotoRecyclerView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
                        this.cameraPhotoLayoutManager.setOrientation(0);
                        this.cameraAttachAdapter.notifyDataSetChanged();
                    }
                } else {
                    recyclerListView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(height, 1073741824));
                    if (this.cameraPhotoLayoutManager.getOrientation() != 1) {
                        this.cameraPhotoRecyclerView.setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
                        this.cameraPhotoLayoutManager.setOrientation(1);
                        this.cameraAttachAdapter.notifyDataSetChanged();
                    }
                }
                this.cameraPhotoRecyclerViewIgnoreLayout = false;
                return true;
            }
        } else if (this.cameraOpened && !this.cameraAnimationInProgress) {
            cameraView2.measure(View.MeasureSpec.makeMeasureSpec(width, 1073741824), View.MeasureSpec.makeMeasureSpec(height, 1073741824));
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean onCustomLayout(View view, int left, int top, int right, int bottom) {
        int cx;
        int cy;
        int width = right - left;
        int height = bottom - top;
        boolean isPortrait = width < height;
        if (view == this.cameraPanel) {
            if (isPortrait) {
                if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                    this.cameraPanel.layout(0, bottom - AndroidUtilities.dp(222.0f), width, bottom - AndroidUtilities.dp(96.0f));
                } else {
                    this.cameraPanel.layout(0, bottom - AndroidUtilities.dp(126.0f), width, bottom);
                }
            } else if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                this.cameraPanel.layout(right - AndroidUtilities.dp(222.0f), 0, right - AndroidUtilities.dp(96.0f), height);
            } else {
                this.cameraPanel.layout(right - AndroidUtilities.dp(126.0f), 0, right, height);
            }
            return true;
        } else if (view == this.zoomControlView) {
            if (isPortrait) {
                if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                    this.zoomControlView.layout(0, bottom - AndroidUtilities.dp(310.0f), width, bottom - AndroidUtilities.dp(260.0f));
                } else {
                    this.zoomControlView.layout(0, bottom - AndroidUtilities.dp(176.0f), width, bottom - AndroidUtilities.dp(126.0f));
                }
            } else if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                this.zoomControlView.layout(right - AndroidUtilities.dp(310.0f), 0, right - AndroidUtilities.dp(260.0f), height);
            } else {
                this.zoomControlView.layout(right - AndroidUtilities.dp(176.0f), 0, right - AndroidUtilities.dp(126.0f), height);
            }
            return true;
        } else {
            TextView textView = this.counterTextView;
            if (view == textView) {
                if (isPortrait) {
                    cx = (width - textView.getMeasuredWidth()) / 2;
                    cy = bottom - AndroidUtilities.dp(167.0f);
                    this.counterTextView.setRotation(0.0f);
                    if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                        cy -= AndroidUtilities.dp(96.0f);
                    }
                } else {
                    cx = right - AndroidUtilities.dp(167.0f);
                    cy = (height / 2) + (this.counterTextView.getMeasuredWidth() / 2);
                    this.counterTextView.setRotation(-90.0f);
                    if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                        cx -= AndroidUtilities.dp(96.0f);
                    }
                }
                TextView textView2 = this.counterTextView;
                textView2.layout(cx, cy, textView2.getMeasuredWidth() + cx, this.counterTextView.getMeasuredHeight() + cy);
                return true;
            } else if (view != this.cameraPhotoRecyclerView) {
                return false;
            } else {
                if (isPortrait) {
                    int cy2 = height - AndroidUtilities.dp(88.0f);
                    view.layout(0, cy2, view.getMeasuredWidth(), view.getMeasuredHeight() + cy2);
                } else {
                    int cx2 = (left + width) - AndroidUtilities.dp(88.0f);
                    view.layout(cx2, 0, view.getMeasuredWidth() + cx2, view.getMeasuredHeight());
                }
                return true;
            }
        }
    }

    public void onPause() {
        ShutterButton shutterButton2 = this.shutterButton;
        if (shutterButton2 != null) {
            if (!this.requestingPermissions) {
                if (this.cameraView != null && shutterButton2.getState() == ShutterButton.State.RECORDING) {
                    resetRecordState();
                    CameraController.getInstance().stopVideoRecording(this.cameraView.getCameraSession(), false);
                    this.shutterButton.setState(ShutterButton.State.DEFAULT, true);
                }
                if (this.cameraOpened) {
                    closeCamera(false);
                }
                hideCamera(true);
            } else {
                if (this.cameraView != null && shutterButton2.getState() == ShutterButton.State.RECORDING) {
                    this.shutterButton.setState(ShutterButton.State.DEFAULT, true);
                }
                this.requestingPermissions = false;
            }
            this.paused = true;
        }
    }

    public void onResume() {
        this.paused = false;
        if (isShowing() && !isDismissed()) {
            checkCamera(false);
        }
    }

    /* access modifiers changed from: private */
    public void openCamera(boolean animated) {
        CameraView cameraView2 = this.cameraView;
        if (cameraView2 != null && this.cameraInitAnimation == null && cameraView2.isInitied()) {
            if (cameraPhotos.isEmpty()) {
                this.counterTextView.setVisibility(4);
                this.cameraPhotoRecyclerView.setVisibility(8);
            } else {
                this.counterTextView.setVisibility(0);
                this.cameraPhotoRecyclerView.setVisibility(0);
            }
            if (this.commentTextView.isKeyboardVisible() && isFocusable()) {
                this.commentTextView.closeKeyboard();
            }
            this.zoomControlView.setVisibility(0);
            this.zoomControlView.setAlpha(0.0f);
            this.cameraPanel.setVisibility(0);
            this.cameraPanel.setTag((Object) null);
            int[] iArr = this.animateCameraValues;
            iArr[0] = 0;
            int i = this.itemSize;
            iArr[1] = i - this.cameraViewOffsetX;
            iArr[2] = (i - this.cameraViewOffsetY) - this.cameraViewOffsetBottomY;
            if (animated) {
                this.cameraAnimationInProgress = true;
                ArrayList<Animator> animators = new ArrayList<>();
                animators.add(ObjectAnimator.ofFloat(this, "cameraOpenProgress", new float[]{0.0f, 1.0f}));
                animators.add(ObjectAnimator.ofFloat(this.cameraPanel, View.ALPHA, new float[]{1.0f}));
                animators.add(ObjectAnimator.ofFloat(this.counterTextView, View.ALPHA, new float[]{1.0f}));
                animators.add(ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, View.ALPHA, new float[]{1.0f}));
                int a = 0;
                while (true) {
                    if (a >= 2) {
                        break;
                    } else if (this.flashModeButton[a].getVisibility() == 0) {
                        animators.add(ObjectAnimator.ofFloat(this.flashModeButton[a], View.ALPHA, new float[]{1.0f}));
                        break;
                    } else {
                        a++;
                    }
                }
                AnimatorSet animatorSet2 = new AnimatorSet();
                animatorSet2.playTogether(animators);
                animatorSet2.setDuration(200);
                animatorSet2.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        boolean unused = ChatAttachAlert.this.cameraAnimationInProgress = false;
                        if (Build.VERSION.SDK_INT >= 21 && ChatAttachAlert.this.cameraView != null) {
                            ChatAttachAlert.this.cameraView.invalidateOutline();
                        }
                        if (ChatAttachAlert.this.cameraOpened) {
                            ChatAttachAlert.this.delegate.onCameraOpened();
                        }
                    }
                });
                animatorSet2.start();
            } else {
                setCameraOpenProgress(1.0f);
                this.cameraPanel.setAlpha(1.0f);
                this.counterTextView.setAlpha(1.0f);
                this.cameraPhotoRecyclerView.setAlpha(1.0f);
                int a2 = 0;
                while (true) {
                    if (a2 >= 2) {
                        break;
                    } else if (this.flashModeButton[a2].getVisibility() == 0) {
                        this.flashModeButton[a2].setAlpha(1.0f);
                        break;
                    } else {
                        a2++;
                    }
                }
                this.delegate.onCameraOpened();
            }
            if (Build.VERSION.SDK_INT >= 21) {
                this.cameraView.setSystemUiVisibility(1028);
            }
            this.cameraOpened = true;
            this.cameraView.setImportantForAccessibility(2);
            if (Build.VERSION.SDK_INT >= 19) {
                this.gridView.setImportantForAccessibility(4);
            }
        }
    }

    public void onActivityResultFragment(int requestCode, Intent data, String currentPicturePath) {
        Intent data2;
        String currentPicturePath2;
        String videoPath;
        int orientation;
        int i = requestCode;
        String str = currentPicturePath;
        BaseFragment baseFragment2 = this.baseFragment;
        if (baseFragment2 != null && baseFragment2.getParentActivity() != null) {
            mediaFromExternalCamera = true;
            if (i == 0) {
                PhotoViewer.getInstance().setParentActivity(this.baseFragment.getParentActivity());
                PhotoViewer.getInstance().setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
                new ArrayList();
                int orientation2 = 0;
                try {
                    int exif = new ExifInterface(str).getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                    if (exif == 3) {
                        orientation2 = 180;
                    } else if (exif == 6) {
                        orientation2 = 90;
                    } else if (exif == 8) {
                        orientation2 = 270;
                    }
                    orientation = orientation2;
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                    orientation = 0;
                }
                int i2 = lastImageId;
                lastImageId = i2 - 1;
                MediaController.PhotoEntry photoEntry = new MediaController.PhotoEntry(0, i2, 0, currentPicturePath, orientation, false);
                photoEntry.canDeleteAfter = true;
                openPhotoViewer(photoEntry, false, true);
            } else if (i == 2) {
                String videoPath2 = null;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("pic path " + str);
                }
                if (data == null || str == null || !new File(str).exists()) {
                    data2 = data;
                } else {
                    data2 = null;
                }
                if (data2 != null) {
                    Uri uri = data2.getData();
                    if (uri != null) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("video record uri " + uri.toString());
                        }
                        videoPath2 = AndroidUtilities.getPath(uri);
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("resolved path = " + videoPath2);
                        }
                        if (videoPath2 == null || !new File(videoPath2).exists()) {
                            videoPath2 = currentPicturePath;
                        }
                    } else {
                        videoPath2 = currentPicturePath;
                    }
                    BaseFragment baseFragment3 = this.baseFragment;
                    if (!(baseFragment3 instanceof ChatActivity) || !((ChatActivity) baseFragment3).isSecretChat()) {
                        AndroidUtilities.addMediaToGallery(currentPicturePath);
                    }
                    currentPicturePath2 = null;
                } else {
                    currentPicturePath2 = str;
                }
                if (videoPath2 != null || currentPicturePath2 == null || !new File(currentPicturePath2).exists()) {
                    videoPath = videoPath2;
                } else {
                    videoPath = currentPicturePath2;
                }
                MediaMetadataRetriever mediaMetadataRetriever = null;
                long duration = 0;
                try {
                    MediaMetadataRetriever mediaMetadataRetriever2 = new MediaMetadataRetriever();
                    mediaMetadataRetriever2.setDataSource(videoPath);
                    String d = mediaMetadataRetriever2.extractMetadata(9);
                    if (d != null) {
                        duration = (long) ((int) Math.ceil((double) (((float) Long.parseLong(d)) / 1000.0f)));
                    }
                    try {
                        mediaMetadataRetriever2.release();
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                    }
                } catch (Exception e3) {
                    FileLog.e((Throwable) e3);
                    if (mediaMetadataRetriever != null) {
                        mediaMetadataRetriever.release();
                    }
                } catch (Throwable th) {
                    Throwable th2 = th;
                    if (mediaMetadataRetriever != null) {
                        try {
                            mediaMetadataRetriever.release();
                        } catch (Exception e4) {
                            FileLog.e((Throwable) e4);
                        }
                    }
                    throw th2;
                }
                Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 1);
                File cacheFile = new File(FileLoader.getDirectory(4), "-2147483648_" + SharedConfig.getLastLocalId() + ".jpg");
                try {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 55, new FileOutputStream(cacheFile));
                } catch (Throwable e5) {
                    FileLog.e(e5);
                }
                SharedConfig.saveConfig();
                int i3 = lastImageId;
                lastImageId = i3 - 1;
                MediaController.PhotoEntry photoEntry2 = new MediaController.PhotoEntry(0, i3, 0, videoPath, 0, true);
                photoEntry2.duration = (int) duration;
                photoEntry2.thumbPath = cacheFile.getAbsolutePath();
                openPhotoViewer(photoEntry2, false, true);
                return;
            }
            Intent intent = data;
            String str2 = str;
        }
    }

    public void closeCamera(boolean animated) {
        if (!this.takingPhoto && this.cameraView != null) {
            int[] iArr = this.animateCameraValues;
            int i = this.itemSize;
            iArr[1] = i - this.cameraViewOffsetX;
            iArr[2] = (i - this.cameraViewOffsetY) - this.cameraViewOffsetBottomY;
            Runnable runnable = this.zoomControlHideRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.zoomControlHideRunnable = null;
            }
            if (animated) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.cameraView.getLayoutParams();
                int[] iArr2 = this.animateCameraValues;
                int translationY = (int) this.cameraView.getTranslationY();
                layoutParams.topMargin = translationY;
                iArr2[0] = translationY;
                this.cameraView.setLayoutParams(layoutParams);
                this.cameraView.setTranslationY(0.0f);
                this.cameraAnimationInProgress = true;
                ArrayList<Animator> animators = new ArrayList<>();
                animators.add(ObjectAnimator.ofFloat(this, "cameraOpenProgress", new float[]{0.0f}));
                animators.add(ObjectAnimator.ofFloat(this.cameraPanel, View.ALPHA, new float[]{0.0f}));
                animators.add(ObjectAnimator.ofFloat(this.zoomControlView, View.ALPHA, new float[]{0.0f}));
                animators.add(ObjectAnimator.ofFloat(this.counterTextView, View.ALPHA, new float[]{0.0f}));
                animators.add(ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, View.ALPHA, new float[]{0.0f}));
                int a = 0;
                while (true) {
                    if (a >= 2) {
                        break;
                    } else if (this.flashModeButton[a].getVisibility() == 0) {
                        animators.add(ObjectAnimator.ofFloat(this.flashModeButton[a], View.ALPHA, new float[]{0.0f}));
                        break;
                    } else {
                        a++;
                    }
                }
                AnimatorSet animatorSet2 = new AnimatorSet();
                animatorSet2.playTogether(animators);
                animatorSet2.setDuration(200);
                animatorSet2.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        boolean unused = ChatAttachAlert.this.cameraAnimationInProgress = false;
                        if (Build.VERSION.SDK_INT >= 21 && ChatAttachAlert.this.cameraView != null) {
                            ChatAttachAlert.this.cameraView.invalidateOutline();
                        }
                        boolean unused2 = ChatAttachAlert.this.cameraOpened = false;
                        if (ChatAttachAlert.this.cameraPanel != null) {
                            ChatAttachAlert.this.cameraPanel.setVisibility(8);
                        }
                        if (ChatAttachAlert.this.zoomControlView != null) {
                            ChatAttachAlert.this.zoomControlView.setVisibility(8);
                            ChatAttachAlert.this.zoomControlView.setTag((Object) null);
                        }
                        if (ChatAttachAlert.this.cameraPhotoRecyclerView != null) {
                            ChatAttachAlert.this.cameraPhotoRecyclerView.setVisibility(8);
                        }
                        if (Build.VERSION.SDK_INT >= 21 && ChatAttachAlert.this.cameraView != null) {
                            ChatAttachAlert.this.cameraView.setSystemUiVisibility(1024);
                        }
                    }
                });
                animatorSet2.start();
            } else {
                this.animateCameraValues[0] = 0;
                setCameraOpenProgress(0.0f);
                this.cameraPanel.setAlpha(0.0f);
                this.cameraPanel.setVisibility(8);
                this.zoomControlView.setAlpha(0.0f);
                this.zoomControlView.setTag((Object) null);
                this.zoomControlView.setVisibility(8);
                this.cameraPhotoRecyclerView.setAlpha(0.0f);
                this.counterTextView.setAlpha(0.0f);
                this.cameraPhotoRecyclerView.setVisibility(8);
                int a2 = 0;
                while (true) {
                    if (a2 >= 2) {
                        break;
                    } else if (this.flashModeButton[a2].getVisibility() == 0) {
                        this.flashModeButton[a2].setAlpha(0.0f);
                        break;
                    } else {
                        a2++;
                    }
                }
                this.cameraOpened = false;
                if (Build.VERSION.SDK_INT >= 21) {
                    this.cameraView.setSystemUiVisibility(1024);
                }
            }
            this.cameraView.setImportantForAccessibility(0);
            if (Build.VERSION.SDK_INT >= 19) {
                this.gridView.setImportantForAccessibility(0);
            }
        }
    }

    public void setCameraOpenProgress(float value) {
        float endHeight;
        float endWidth;
        if (this.cameraView != null) {
            this.cameraOpenProgress = value;
            int[] iArr = this.animateCameraValues;
            float startWidth = (float) iArr[1];
            float startHeight = (float) iArr[2];
            if (AndroidUtilities.displaySize.x < AndroidUtilities.displaySize.y) {
                endWidth = (float) ((this.container.getWidth() - getLeftInset()) - getRightInset());
                endHeight = (float) this.container.getHeight();
            } else {
                endWidth = (float) ((this.container.getWidth() - getLeftInset()) - getRightInset());
                endHeight = (float) this.container.getHeight();
            }
            if (value == 0.0f) {
                this.cameraView.setClipTop(this.cameraViewOffsetY);
                this.cameraView.setClipBottom(this.cameraViewOffsetBottomY);
                this.cameraView.setTranslationX((float) this.cameraViewLocation[0]);
                this.cameraView.setTranslationY((float) this.cameraViewLocation[1]);
                this.cameraIcon.setTranslationX((float) this.cameraViewLocation[0]);
                this.cameraIcon.setTranslationY((float) this.cameraViewLocation[1]);
            } else if (!(this.cameraView.getTranslationX() == 0.0f && this.cameraView.getTranslationY() == 0.0f)) {
                this.cameraView.setTranslationX(0.0f);
                this.cameraView.setTranslationY(0.0f);
            }
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.cameraView.getLayoutParams();
            layoutParams.width = (int) (((endWidth - startWidth) * value) + startWidth);
            layoutParams.height = (int) (((endHeight - startHeight) * value) + startHeight);
            if (value != 0.0f) {
                this.cameraView.setClipTop((int) (((float) this.cameraViewOffsetY) * (1.0f - value)));
                this.cameraView.setClipBottom((int) (((float) this.cameraViewOffsetBottomY) * (1.0f - value)));
                layoutParams.leftMargin = (int) (((float) this.cameraViewLocation[0]) * (1.0f - value));
                int[] iArr2 = this.animateCameraValues;
                layoutParams.topMargin = (int) (((float) iArr2[0]) + (((float) (this.cameraViewLocation[1] - iArr2[0])) * (1.0f - value)));
            } else {
                layoutParams.leftMargin = 0;
                layoutParams.topMargin = 0;
            }
            this.cameraView.setLayoutParams(layoutParams);
            if (value <= 0.5f) {
                this.cameraIcon.setAlpha(1.0f - (value / 0.5f));
            } else {
                this.cameraIcon.setAlpha(0.0f);
            }
            if (Build.VERSION.SDK_INT >= 21) {
                this.cameraView.invalidateOutline();
            }
        }
    }

    public float getCameraOpenProgress() {
        return this.cameraOpenProgress;
    }

    /* access modifiers changed from: private */
    public void checkCameraViewPosition() {
        RecyclerView.ViewHolder holder;
        if (Build.VERSION.SDK_INT >= 21) {
            CameraView cameraView2 = this.cameraView;
            if (cameraView2 != null) {
                cameraView2.invalidateOutline();
            }
            RecyclerView.ViewHolder holder2 = this.gridView.findViewHolderForAdapterPosition(this.itemsPerRow - 1);
            if (holder2 != null) {
                holder2.itemView.invalidateOutline();
            }
            if ((!this.adapter.needCamera || !this.deviceHasGoodCamera || this.selectedAlbumEntry != this.galleryAlbumEntry) && (holder = this.gridView.findViewHolderForAdapterPosition(0)) != null) {
                holder.itemView.invalidateOutline();
            }
        }
        if (this.deviceHasGoodCamera) {
            int count = this.gridView.getChildCount();
            int a = 0;
            while (true) {
                if (a >= count) {
                    break;
                }
                View child = this.gridView.getChildAt(a);
                if (!(child instanceof PhotoAttachCameraCell)) {
                    a++;
                } else if (Build.VERSION.SDK_INT < 19 || child.isAttachedToWindow()) {
                    child.getLocationInWindow(this.cameraViewLocation);
                    int[] iArr = this.cameraViewLocation;
                    iArr[0] = iArr[0] - getLeftInset();
                    float listViewX = this.gridView.getX() - ((float) getLeftInset());
                    int[] iArr2 = this.cameraViewLocation;
                    if (((float) iArr2[0]) < listViewX) {
                        int i = (int) (listViewX - ((float) iArr2[0]));
                        this.cameraViewOffsetX = i;
                        if (i >= this.itemSize) {
                            this.cameraViewOffsetX = 0;
                            iArr2[0] = AndroidUtilities.dp(-400.0f);
                            this.cameraViewLocation[1] = 0;
                        } else {
                            iArr2[0] = iArr2[0] + i;
                        }
                    } else {
                        this.cameraViewOffsetX = 0;
                    }
                    int maxY = (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
                    int[] iArr3 = this.cameraViewLocation;
                    if (iArr3[1] < maxY) {
                        int i2 = maxY - iArr3[1];
                        this.cameraViewOffsetY = i2;
                        if (i2 >= this.itemSize) {
                            this.cameraViewOffsetY = 0;
                            iArr3[0] = AndroidUtilities.dp(-400.0f);
                            this.cameraViewLocation[1] = 0;
                        } else {
                            iArr3[1] = iArr3[1] + i2;
                        }
                    } else {
                        this.cameraViewOffsetY = 0;
                    }
                    int containerHeight = this.containerView.getMeasuredHeight();
                    int keyboardSize = this.sizeNotifierFrameLayout.getKeyboardHeight();
                    if (!AndroidUtilities.isInMultiwindow && keyboardSize <= AndroidUtilities.dp(20.0f)) {
                        containerHeight -= this.commentTextView.getEmojiPadding();
                    }
                    int maxY2 = (int) (((float) (containerHeight - this.buttonsRecyclerView.getMeasuredHeight())) + this.buttonsRecyclerView.getTranslationY() + this.containerView.getTranslationY());
                    int[] iArr4 = this.cameraViewLocation;
                    int i3 = iArr4[1];
                    int i4 = this.itemSize;
                    if (i3 + i4 > maxY2) {
                        int i5 = (iArr4[1] + i4) - maxY2;
                        this.cameraViewOffsetBottomY = i5;
                        if (i5 >= i4) {
                            this.cameraViewOffsetBottomY = 0;
                            iArr4[0] = AndroidUtilities.dp(-400.0f);
                            this.cameraViewLocation[1] = 0;
                        }
                    } else {
                        this.cameraViewOffsetBottomY = 0;
                    }
                    applyCameraViewPosition();
                    return;
                }
            }
            this.cameraViewOffsetX = 0;
            this.cameraViewOffsetY = 0;
            this.cameraViewLocation[0] = AndroidUtilities.dp(-400.0f);
            this.cameraViewLocation[1] = 0;
            applyCameraViewPosition();
        }
    }

    private void applyCameraViewPosition() {
        CameraView cameraView2 = this.cameraView;
        if (cameraView2 != null) {
            if (!this.cameraOpened) {
                cameraView2.setTranslationX((float) this.cameraViewLocation[0]);
                this.cameraView.setTranslationY((float) this.cameraViewLocation[1]);
            }
            this.cameraIcon.setTranslationX((float) this.cameraViewLocation[0]);
            this.cameraIcon.setTranslationY((float) this.cameraViewLocation[1]);
            int i = this.itemSize;
            int finalWidth = i - this.cameraViewOffsetX;
            int i2 = this.cameraViewOffsetY;
            int finalHeight = (i - i2) - this.cameraViewOffsetBottomY;
            if (!this.cameraOpened) {
                this.cameraView.setClipTop(i2);
                this.cameraView.setClipBottom(this.cameraViewOffsetBottomY);
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.cameraView.getLayoutParams();
                if (!(layoutParams.height == finalHeight && layoutParams.width == finalWidth)) {
                    layoutParams.width = finalWidth;
                    layoutParams.height = finalHeight;
                    this.cameraView.setLayoutParams(layoutParams);
                    AndroidUtilities.runOnUIThread(new Runnable(layoutParams) {
                        private final /* synthetic */ FrameLayout.LayoutParams f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            ChatAttachAlert.this.lambda$applyCameraViewPosition$20$ChatAttachAlert(this.f$1);
                        }
                    });
                }
            }
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.cameraIcon.getLayoutParams();
            if (layoutParams2.height != finalHeight || layoutParams2.width != finalWidth) {
                layoutParams2.width = finalWidth;
                layoutParams2.height = finalHeight;
                this.cameraIcon.setLayoutParams(layoutParams2);
                AndroidUtilities.runOnUIThread(new Runnable(layoutParams2) {
                    private final /* synthetic */ FrameLayout.LayoutParams f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        ChatAttachAlert.this.lambda$applyCameraViewPosition$21$ChatAttachAlert(this.f$1);
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$applyCameraViewPosition$20$ChatAttachAlert(FrameLayout.LayoutParams layoutParamsFinal) {
        CameraView cameraView2 = this.cameraView;
        if (cameraView2 != null) {
            cameraView2.setLayoutParams(layoutParamsFinal);
        }
    }

    public /* synthetic */ void lambda$applyCameraViewPosition$21$ChatAttachAlert(FrameLayout.LayoutParams layoutParamsFinal) {
        FrameLayout frameLayout = this.cameraIcon;
        if (frameLayout != null) {
            frameLayout.setLayoutParams(layoutParamsFinal);
        }
    }

    public void showCamera() {
        if (!this.paused && this.mediaEnabled) {
            if (this.cameraView == null) {
                CameraView cameraView2 = new CameraView(this.baseFragment.getParentActivity(), this.openWithFrontFaceCamera);
                this.cameraView = cameraView2;
                cameraView2.setFocusable(true);
                if (Build.VERSION.SDK_INT >= 21) {
                    this.cameraView.setOutlineProvider(new ViewOutlineProvider() {
                        public void getOutline(View view, Outline outline) {
                            if (ChatAttachAlert.this.cameraAnimationInProgress) {
                                int rad = AndroidUtilities.dp(ChatAttachAlert.this.cornerRadius * 8.0f * ChatAttachAlert.this.cameraOpenProgress);
                                outline.setRoundRect(0, 0, view.getMeasuredWidth() + rad, view.getMeasuredHeight() + rad, (float) rad);
                            } else if (ChatAttachAlert.this.cameraAnimationInProgress || ChatAttachAlert.this.cameraOpened) {
                                outline.setRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                            } else {
                                int rad2 = AndroidUtilities.dp(ChatAttachAlert.this.cornerRadius * 8.0f);
                                outline.setRoundRect(0, 0, view.getMeasuredWidth() + rad2, view.getMeasuredHeight() + rad2, (float) rad2);
                            }
                        }
                    });
                    this.cameraView.setClipToOutline(true);
                }
                this.cameraView.setContentDescription(LocaleController.getString("AccDescrInstantCamera", R.string.AccDescrInstantCamera));
                BottomSheet.ContainerView containerView = this.container;
                CameraView cameraView3 = this.cameraView;
                int i = this.itemSize;
                containerView.addView(cameraView3, 1, new FrameLayout.LayoutParams(i, i));
                this.cameraView.setDelegate(new CameraView.CameraViewDelegate() {
                    public void onCameraCreated(Camera camera) {
                    }

                    public void onCameraInit() {
                        int i = 4;
                        if (ChatAttachAlert.this.cameraView.getCameraSession().getCurrentFlashMode().equals(ChatAttachAlert.this.cameraView.getCameraSession().getNextFlashMode())) {
                            for (int a = 0; a < 2; a++) {
                                ChatAttachAlert.this.flashModeButton[a].setVisibility(4);
                                ChatAttachAlert.this.flashModeButton[a].setAlpha(0.0f);
                                ChatAttachAlert.this.flashModeButton[a].setTranslationY(0.0f);
                            }
                        } else {
                            ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                            chatAttachAlert.setCameraFlashModeIcon(chatAttachAlert.flashModeButton[0], ChatAttachAlert.this.cameraView.getCameraSession().getCurrentFlashMode());
                            int a2 = 0;
                            while (a2 < 2) {
                                ChatAttachAlert.this.flashModeButton[a2].setVisibility(a2 == 0 ? 0 : 4);
                                ChatAttachAlert.this.flashModeButton[a2].setAlpha((a2 != 0 || !ChatAttachAlert.this.cameraOpened) ? 0.0f : 1.0f);
                                ChatAttachAlert.this.flashModeButton[a2].setTranslationY(0.0f);
                                a2++;
                            }
                        }
                        ChatAttachAlert.this.switchCameraButton.setImageResource(ChatAttachAlert.this.cameraView.isFrontface() ? R.drawable.camera_revert1 : R.drawable.camera_revert2);
                        ImageView access$7800 = ChatAttachAlert.this.switchCameraButton;
                        if (ChatAttachAlert.this.cameraView.hasFrontFaceCamera()) {
                            i = 0;
                        }
                        access$7800.setVisibility(i);
                        if (!ChatAttachAlert.this.cameraOpened) {
                            AnimatorSet unused = ChatAttachAlert.this.cameraInitAnimation = new AnimatorSet();
                            ChatAttachAlert.this.cameraInitAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(ChatAttachAlert.this.cameraView, View.ALPHA, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(ChatAttachAlert.this.cameraIcon, View.ALPHA, new float[]{0.0f, 1.0f})});
                            ChatAttachAlert.this.cameraInitAnimation.setDuration(180);
                            ChatAttachAlert.this.cameraInitAnimation.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animation) {
                                    if (animation.equals(ChatAttachAlert.this.cameraInitAnimation)) {
                                        AnimatorSet unused = ChatAttachAlert.this.cameraInitAnimation = null;
                                        int count = ChatAttachAlert.this.gridView.getChildCount();
                                        for (int a = 0; a < count; a++) {
                                            View child = ChatAttachAlert.this.gridView.getChildAt(a);
                                            if (child instanceof PhotoAttachCameraCell) {
                                                child.setVisibility(4);
                                                return;
                                            }
                                        }
                                    }
                                }

                                public void onAnimationCancel(Animator animation) {
                                    AnimatorSet unused = ChatAttachAlert.this.cameraInitAnimation = null;
                                }
                            });
                            ChatAttachAlert.this.cameraInitAnimation.start();
                        }
                    }
                });
                if (this.cameraIcon == null) {
                    AnonymousClass30 r0 = new FrameLayout(this.baseFragment.getParentActivity()) {
                        /* access modifiers changed from: protected */
                        public void onDraw(Canvas canvas) {
                            int w = ChatAttachAlert.this.cameraDrawable.getIntrinsicWidth();
                            int h = ChatAttachAlert.this.cameraDrawable.getIntrinsicHeight();
                            int x = (ChatAttachAlert.this.itemSize - w) / 2;
                            int y = (ChatAttachAlert.this.itemSize - h) / 2;
                            if (ChatAttachAlert.this.cameraViewOffsetY != 0) {
                                y -= ChatAttachAlert.this.cameraViewOffsetY;
                            }
                            ChatAttachAlert.this.cameraDrawable.setBounds(x, y, x + w, y + h);
                            ChatAttachAlert.this.cameraDrawable.draw(canvas);
                        }
                    };
                    this.cameraIcon = r0;
                    r0.setWillNotDraw(false);
                    this.cameraIcon.setClipChildren(true);
                }
                BottomSheet.ContainerView containerView2 = this.container;
                FrameLayout frameLayout = this.cameraIcon;
                int i2 = this.itemSize;
                containerView2.addView(frameLayout, 2, new FrameLayout.LayoutParams(i2, i2));
                float f = 1.0f;
                this.cameraView.setAlpha(this.mediaEnabled ? 1.0f : 0.2f);
                this.cameraView.setEnabled(this.mediaEnabled);
                FrameLayout frameLayout3 = this.cameraIcon;
                if (!this.mediaEnabled) {
                    f = 0.2f;
                }
                frameLayout3.setAlpha(f);
                this.cameraIcon.setEnabled(this.mediaEnabled);
                checkCameraViewPosition();
            }
            ZoomControlView zoomControlView2 = this.zoomControlView;
            if (zoomControlView2 != null) {
                zoomControlView2.setZoom(0.0f, false);
                this.cameraZoom = 0.0f;
            }
            this.cameraView.setTranslationX((float) this.cameraViewLocation[0]);
            this.cameraView.setTranslationY((float) this.cameraViewLocation[1]);
            this.cameraIcon.setTranslationX((float) this.cameraViewLocation[0]);
            this.cameraIcon.setTranslationY((float) this.cameraViewLocation[1]);
        }
    }

    public void hideCamera(boolean async) {
        if (this.deviceHasGoodCamera && this.cameraView != null) {
            saveLastCameraBitmap();
            this.cameraView.destroy(async, (Runnable) null);
            AnimatorSet animatorSet2 = this.cameraInitAnimation;
            if (animatorSet2 != null) {
                animatorSet2.cancel();
                this.cameraInitAnimation = null;
            }
            this.container.removeView(this.cameraView);
            this.container.removeView(this.cameraIcon);
            this.cameraView = null;
            this.cameraIcon = null;
            int count = this.gridView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.gridView.getChildAt(a);
                if (child instanceof PhotoAttachCameraCell) {
                    child.setVisibility(0);
                    return;
                }
            }
        }
    }

    private void saveLastCameraBitmap() {
        try {
            Bitmap bitmap = this.cameraView.getTextureView().getBitmap();
            if (bitmap != null) {
                Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), this.cameraView.getMatrix(), true);
                bitmap.recycle();
                Bitmap bitmap2 = newBitmap;
                Bitmap lastBitmap = Bitmap.createScaledBitmap(bitmap2, 80, (int) (((float) bitmap2.getHeight()) / (((float) bitmap2.getWidth()) / 80.0f)), true);
                if (lastBitmap != null) {
                    if (lastBitmap != bitmap2) {
                        bitmap2.recycle();
                    }
                    Utilities.blurBitmap(lastBitmap, 7, 1, lastBitmap.getWidth(), lastBitmap.getHeight(), lastBitmap.getRowBytes());
                    lastBitmap.compress(Bitmap.CompressFormat.JPEG, 87, new FileOutputStream(new File(ApplicationLoader.getFilesDirFixed(), "cthumb.jpg")));
                    lastBitmap.recycle();
                }
            }
        } catch (Throwable th) {
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.albumsDidLoad) {
            if (this.adapter != null) {
                if (this.baseFragment instanceof ChatActivity) {
                    this.galleryAlbumEntry = MediaController.allMediaAlbumEntry;
                } else {
                    this.galleryAlbumEntry = MediaController.allPhotosAlbumEntry;
                }
                if (this.selectedAlbumEntry != null) {
                    int a = 0;
                    while (true) {
                        if (a >= MediaController.allMediaAlbums.size()) {
                            break;
                        }
                        MediaController.AlbumEntry entry = MediaController.allMediaAlbums.get(a);
                        if (entry.bucketId == this.selectedAlbumEntry.bucketId && entry.videoOnly == this.selectedAlbumEntry.videoOnly) {
                            this.selectedAlbumEntry = entry;
                            break;
                        }
                        a++;
                    }
                } else {
                    this.selectedAlbumEntry = this.galleryAlbumEntry;
                }
                this.loading = false;
                this.progressView.showTextView();
                this.adapter.notifyDataSetChanged();
                this.cameraAttachAdapter.notifyDataSetChanged();
                if (!selectedPhotosOrder.isEmpty() && this.galleryAlbumEntry != null) {
                    int N = selectedPhotosOrder.size();
                    for (int a2 = 0; a2 < N; a2++) {
                        int imageId = ((Integer) selectedPhotosOrder.get(a2)).intValue();
                        MediaController.PhotoEntry entry2 = this.galleryAlbumEntry.photosByIds.get(imageId);
                        if (entry2 != null) {
                            selectedPhotos.put(Integer.valueOf(imageId), entry2);
                        }
                    }
                }
                updateAlbumsDropDown();
            }
        } else if (id == NotificationCenter.reloadInlineHints) {
            ButtonsAdapter buttonsAdapter2 = this.buttonsAdapter;
            if (buttonsAdapter2 != null) {
                buttonsAdapter2.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.cameraInitied) {
            checkCamera(false);
        }
    }

    private void updateAlbumsDropDown() {
        ArrayList<MediaController.AlbumEntry> albums;
        this.dropDownContainer.removeAllSubItems();
        if (this.mediaEnabled) {
            if (this.baseFragment instanceof ChatActivity) {
                albums = MediaController.allMediaAlbums;
            } else {
                albums = MediaController.allPhotoAlbums;
            }
            ArrayList<MediaController.AlbumEntry> arrayList = new ArrayList<>(albums);
            this.dropDownAlbums = arrayList;
            Collections.sort(arrayList, new Comparator(albums) {
                private final /* synthetic */ ArrayList f$0;

                {
                    this.f$0 = r1;
                }

                public final int compare(Object obj, Object obj2) {
                    return ChatAttachAlert.lambda$updateAlbumsDropDown$22(this.f$0, (MediaController.AlbumEntry) obj, (MediaController.AlbumEntry) obj2);
                }
            });
        } else {
            this.dropDownAlbums = new ArrayList<>();
        }
        if (this.dropDownAlbums.isEmpty()) {
            this.dropDown.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
            return;
        }
        this.dropDown.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, this.dropDownDrawable, (Drawable) null);
        int N = this.dropDownAlbums.size();
        for (int a = 0; a < N; a++) {
            this.dropDownContainer.addSubItem(a + 10, this.dropDownAlbums.get(a).bucketName);
        }
    }

    static /* synthetic */ int lambda$updateAlbumsDropDown$22(ArrayList albums, MediaController.AlbumEntry o1, MediaController.AlbumEntry o2) {
        int index1;
        int index2;
        if (o1.bucketId == 0 && o2.bucketId != 0) {
            return -1;
        }
        if ((o1.bucketId != 0 && o2.bucketId == 0) || (index1 = albums.indexOf(o1)) > (index2 = albums.indexOf(o2))) {
            return 1;
        }
        if (index1 < index2) {
            return -1;
        }
        return 0;
    }

    /* access modifiers changed from: private */
    public void updateSelectedPosition() {
        float moveProgress;
        int finalMove;
        int t = (this.scrollOffsetY - this.backgroundPaddingTop) - AndroidUtilities.dp(39.0f);
        if (this.backgroundPaddingTop + t < ActionBar.getCurrentActionBarHeight()) {
            moveProgress = Math.min(1.0f, ((float) ((ActionBar.getCurrentActionBarHeight() - t) - this.backgroundPaddingTop)) / ((float) AndroidUtilities.dp(43.0f)));
            this.cornerRadius = 1.0f - moveProgress;
        } else {
            moveProgress = 0.0f;
            this.cornerRadius = 1.0f;
        }
        if (AndroidUtilities.isTablet()) {
            finalMove = 16;
        } else if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
            finalMove = 6;
        } else {
            finalMove = 12;
        }
        float f = 0.0f;
        if (this.actionBar.getAlpha() == 0.0f) {
            f = (float) AndroidUtilities.dp((1.0f - this.selectedMenuItem.getAlpha()) * 26.0f);
        }
        float offset = f;
        this.selectedMenuItem.setTranslationY(((float) (this.scrollOffsetY - AndroidUtilities.dp((((float) finalMove) * moveProgress) + 37.0f))) + offset);
        this.selectedTextView.setTranslationY(((float) (this.scrollOffsetY - AndroidUtilities.dp((((float) finalMove) * moveProgress) + 25.0f))) + offset);
    }

    /* access modifiers changed from: private */
    public void updateLayout(boolean animated) {
        if (this.gridView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.gridView;
            int paddingTop = recyclerListView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            this.containerView.invalidate();
            return;
        }
        View child = this.gridView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.gridView.findContainingViewHolder(child);
        int top = child.getTop();
        int newOffset = AndroidUtilities.dp(7.0f);
        if (top >= AndroidUtilities.dp(7.0f) && holder != null && holder.getAdapterPosition() == 0) {
            newOffset = top;
        }
        boolean show = newOffset <= AndroidUtilities.dp(12.0f);
        if ((show && this.actionBar.getTag() == null) || (!show && this.actionBar.getTag() != null)) {
            this.actionBar.setTag(show ? 1 : null);
            AnimatorSet animatorSet2 = this.actionBarAnimation;
            if (animatorSet2 != null) {
                animatorSet2.cancel();
                this.actionBarAnimation = null;
            }
            AnimatorSet animatorSet3 = new AnimatorSet();
            this.actionBarAnimation = animatorSet3;
            animatorSet3.setDuration(180);
            AnimatorSet animatorSet4 = this.actionBarAnimation;
            Animator[] animatorArr = new Animator[2];
            ActionBar actionBar2 = this.actionBar;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            float f = 1.0f;
            fArr[0] = show ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(actionBar2, property, fArr);
            View view = this.actionBarShadow;
            Property property2 = View.ALPHA;
            float[] fArr2 = new float[1];
            if (!show) {
                f = 0.0f;
            }
            fArr2[0] = f;
            animatorArr[1] = ObjectAnimator.ofFloat(view, property2, fArr2);
            animatorSet4.playTogether(animatorArr);
            this.actionBarAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    AnimatorSet unused = ChatAttachAlert.this.actionBarAnimation = null;
                }
            });
            this.actionBarAnimation.start();
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.gridView.getLayoutParams();
        int newOffset2 = newOffset + (layoutParams.topMargin - AndroidUtilities.dp(11.0f));
        if (this.scrollOffsetY != newOffset2) {
            RecyclerListView recyclerListView2 = this.gridView;
            this.scrollOffsetY = newOffset2;
            recyclerListView2.setTopGlowOffset(newOffset2 - layoutParams.topMargin);
            updateSelectedPosition();
            this.containerView.invalidate();
        }
        this.progressView.setTranslationY((float) (this.scrollOffsetY + ((((this.gridView.getMeasuredHeight() - this.scrollOffsetY) - AndroidUtilities.dp(50.0f)) - this.progressView.getMeasuredHeight()) / 2)));
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    public void updatePhotosButton(int animated) {
        int i = animated;
        int count = selectedPhotos.size();
        float f = 1.0f;
        if (count == 0) {
            this.selectedCountView.setPivotX(0.0f);
            this.selectedCountView.setPivotY(0.0f);
            showCommentTextView(false, i != 0);
        } else {
            this.selectedCountView.invalidate();
            if (showCommentTextView(true, i != 0) || i == 0) {
                this.selectedCountView.setPivotX(0.0f);
                this.selectedCountView.setPivotY(0.0f);
            } else {
                this.selectedCountView.setPivotX((float) AndroidUtilities.dp(21.0f));
                this.selectedCountView.setPivotY((float) AndroidUtilities.dp(12.0f));
                AnimatorSet animatorSet2 = new AnimatorSet();
                Animator[] animatorArr = new Animator[2];
                View view = this.selectedCountView;
                Property property = View.SCALE_X;
                float[] fArr = new float[2];
                float f2 = 1.1f;
                fArr[0] = i == 1 ? 1.1f : 0.9f;
                fArr[1] = 1.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
                View view2 = this.selectedCountView;
                Property property2 = View.SCALE_Y;
                float[] fArr2 = new float[2];
                if (i != 1) {
                    f2 = 0.9f;
                }
                fArr2[0] = f2;
                fArr2[1] = 1.0f;
                animatorArr[1] = ObjectAnimator.ofFloat(view2, property2, fArr2);
                animatorSet2.playTogether(animatorArr);
                animatorSet2.setInterpolator(new OvershootInterpolator());
                animatorSet2.setDuration(180);
                animatorSet2.start();
            }
            if (count == 1 || this.editingMessageObject != null) {
                this.selectedMenuItem.hideSubItem(0);
            } else {
                this.selectedMenuItem.showSubItem(0);
            }
        }
        if (!(this.baseFragment instanceof ChatActivity)) {
            return;
        }
        if ((count == 0 && this.menuShowed) || (count != 0 && !this.menuShowed)) {
            this.menuShowed = count != 0;
            AnimatorSet animatorSet3 = this.menuAnimator;
            if (animatorSet3 != null) {
                animatorSet3.cancel();
                this.menuAnimator = null;
            }
            if (this.menuShowed) {
                this.selectedMenuItem.setVisibility(0);
                this.selectedTextView.setVisibility(0);
            }
            if (i == 0) {
                this.selectedMenuItem.setAlpha(this.menuShowed ? 1.0f : 0.0f);
                TextView textView = this.selectedTextView;
                if (!this.menuShowed) {
                    f = 0.0f;
                }
                textView.setAlpha(f);
                return;
            }
            AnimatorSet animatorSet4 = new AnimatorSet();
            this.menuAnimator = animatorSet4;
            Animator[] animatorArr2 = new Animator[2];
            ActionBarMenuItem actionBarMenuItem = this.selectedMenuItem;
            Property property3 = View.ALPHA;
            float[] fArr3 = new float[1];
            fArr3[0] = this.menuShowed ? 1.0f : 0.0f;
            animatorArr2[0] = ObjectAnimator.ofFloat(actionBarMenuItem, property3, fArr3);
            TextView textView2 = this.selectedTextView;
            Property property4 = View.ALPHA;
            float[] fArr4 = new float[1];
            if (!this.menuShowed) {
                f = 0.0f;
            }
            fArr4[0] = f;
            animatorArr2[1] = ObjectAnimator.ofFloat(textView2, property4, fArr4);
            animatorSet4.playTogether(animatorArr2);
            this.menuAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    AnimatorSet unused = ChatAttachAlert.this.menuAnimator = null;
                    if (!ChatAttachAlert.this.menuShowed) {
                        ChatAttachAlert.this.selectedMenuItem.setVisibility(4);
                        ChatAttachAlert.this.selectedTextView.setVisibility(4);
                    }
                }
            });
            this.menuAnimator.setDuration(180);
            this.menuAnimator.start();
        }
    }

    public void setDelegate(ChatAttachViewDelegate chatAttachViewDelegate) {
        this.delegate = chatAttachViewDelegate;
    }

    public void loadGalleryPhotos() {
        MediaController.AlbumEntry albumEntry;
        if (this.baseFragment instanceof ChatActivity) {
            albumEntry = MediaController.allMediaAlbumEntry;
        } else {
            albumEntry = MediaController.allPhotosAlbumEntry;
        }
        if (albumEntry == null && Build.VERSION.SDK_INT >= 21) {
            MediaController.loadGalleryPhotosAlbums(0);
        }
    }

    public void init() {
        if (this.baseFragment instanceof ChatActivity) {
            this.galleryAlbumEntry = MediaController.allMediaAlbumEntry;
            TLRPC.Chat chat = ((ChatActivity) this.baseFragment).getCurrentChat();
            if (chat != null) {
                this.mediaEnabled = ChatObject.canSendMedia(chat);
                this.pollsEnabled = ChatObject.canSendPolls(chat);
                if (this.mediaEnabled) {
                    this.progressView.setText(LocaleController.getString("NoPhotos", R.string.NoPhotos));
                } else if (ChatObject.isActionBannedByDefault(chat, 7)) {
                    this.progressView.setText(LocaleController.getString("GlobalAttachMediaRestricted", R.string.GlobalAttachMediaRestricted));
                } else if (AndroidUtilities.isBannedForever(chat.banned_rights)) {
                    this.progressView.setText(LocaleController.formatString("AttachMediaRestrictedForever", R.string.AttachMediaRestrictedForever, new Object[0]));
                } else {
                    this.progressView.setText(LocaleController.formatString("AttachMediaRestricted", R.string.AttachMediaRestricted, LocaleController.formatDateForBan((long) chat.banned_rights.until_date)));
                }
                CameraView cameraView2 = this.cameraView;
                float f = 1.0f;
                if (cameraView2 != null) {
                    cameraView2.setAlpha(this.mediaEnabled ? 1.0f : 0.2f);
                    this.cameraView.setEnabled(this.mediaEnabled);
                }
                FrameLayout frameLayout = this.cameraIcon;
                if (frameLayout != null) {
                    if (!this.mediaEnabled) {
                        f = 0.2f;
                    }
                    frameLayout.setAlpha(f);
                    this.cameraIcon.setEnabled(this.mediaEnabled);
                }
            } else {
                this.pollsEnabled = false;
            }
        } else {
            this.galleryAlbumEntry = MediaController.allPhotosAlbumEntry;
            this.commentTextView.setVisibility(4);
        }
        if (Build.VERSION.SDK_INT >= 23) {
            this.noGalleryPermissions = this.baseFragment.getParentActivity().checkSelfPermission(PermissionUtils.PERMISSION_READ_EXTERNAL_STORAGE) != 0;
        }
        if (this.galleryAlbumEntry != null) {
            for (int a = 0; a < Math.min(100, this.galleryAlbumEntry.photos.size()); a++) {
                this.galleryAlbumEntry.photos.get(a).reset();
            }
        }
        this.commentTextView.hidePopup(true);
        this.enterCommentEventSent = false;
        setFocusable(false);
        MediaController.AlbumEntry albumEntry = this.galleryAlbumEntry;
        this.selectedAlbumEntry = albumEntry;
        if (albumEntry != null) {
            this.loading = false;
            EmptyTextProgressView emptyTextProgressView = this.progressView;
            if (emptyTextProgressView != null) {
                emptyTextProgressView.showTextView();
            }
        }
        this.dropDown.setText(LocaleController.getString("ChatGallery", R.string.ChatGallery));
        clearSelectedPhotos();
        updatePhotosCounter(false);
        this.buttonsAdapter.notifyDataSetChanged();
        this.commentTextView.setText("");
        this.cameraPhotoLayoutManager.scrollToPositionWithOffset(0, EditInputFilter.MAX_VALUE);
        this.buttonsLayoutManager.scrollToPositionWithOffset(0, EditInputFilter.MAX_VALUE);
        this.layoutManager.scrollToPositionWithOffset(0, EditInputFilter.MAX_VALUE);
        updateAlbumsDropDown();
    }

    public HashMap<Object, Object> getSelectedPhotos() {
        return selectedPhotos;
    }

    public ArrayList<Object> getSelectedPhotosOrder() {
        return selectedPhotosOrder;
    }

    public void onDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.albumsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.reloadInlineHints);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.cameraInitied);
        this.baseFragment = null;
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
    }

    /* access modifiers changed from: private */
    public PhotoAttachPhotoCell getCellForIndex(int index) {
        int count = this.gridView.getChildCount();
        for (int a = 0; a < count; a++) {
            View view = this.gridView.getChildAt(a);
            if (view instanceof PhotoAttachPhotoCell) {
                PhotoAttachPhotoCell cell = (PhotoAttachPhotoCell) view;
                if (((Integer) cell.getImageView().getTag()).intValue() == index) {
                    return cell;
                }
            }
        }
        return null;
    }

    public void checkStorage() {
        if (this.noGalleryPermissions && Build.VERSION.SDK_INT >= 23) {
            boolean z = this.baseFragment.getParentActivity().checkSelfPermission(PermissionUtils.PERMISSION_READ_EXTERNAL_STORAGE) != 0;
            this.noGalleryPermissions = z;
            if (!z) {
                loadGalleryPhotos();
            }
            this.adapter.notifyDataSetChanged();
            this.cameraAttachAdapter.notifyDataSetChanged();
        }
    }

    public void checkCamera(boolean request) {
        PhotoAttachAdapter photoAttachAdapter;
        if (this.baseFragment != null) {
            boolean old = this.deviceHasGoodCamera;
            boolean old2 = this.noCameraPermissions;
            if (!SharedConfig.inappCamera) {
                this.deviceHasGoodCamera = false;
            } else if (Build.VERSION.SDK_INT >= 23) {
                boolean z = this.baseFragment.getParentActivity().checkSelfPermission("android.permission.CAMERA") != 0;
                this.noCameraPermissions = z;
                if (z) {
                    if (request) {
                        try {
                            this.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 17);
                        } catch (Exception e) {
                        }
                    }
                    this.deviceHasGoodCamera = false;
                } else {
                    if (request || SharedConfig.hasCameraCache) {
                        CameraController.getInstance().initCamera((Runnable) null);
                    }
                    this.deviceHasGoodCamera = CameraController.getInstance().isCameraInitied();
                }
            } else {
                if (request || SharedConfig.hasCameraCache) {
                    CameraController.getInstance().initCamera((Runnable) null);
                }
                this.deviceHasGoodCamera = CameraController.getInstance().isCameraInitied();
            }
            if (!((old == this.deviceHasGoodCamera && old2 == this.noCameraPermissions) || (photoAttachAdapter = this.adapter) == null)) {
                photoAttachAdapter.notifyDataSetChanged();
            }
            if (isShowing() && this.deviceHasGoodCamera && this.baseFragment != null && this.backDrawable.getAlpha() != 0 && !this.cameraOpened) {
                showCamera();
            }
        }
    }

    public void onOpenAnimationEnd() {
        MediaController.AlbumEntry albumEntry;
        NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(false);
        if (this.baseFragment instanceof ChatActivity) {
            albumEntry = MediaController.allMediaAlbumEntry;
        } else {
            albumEntry = MediaController.allPhotosAlbumEntry;
        }
        if (Build.VERSION.SDK_INT <= 19 && albumEntry == null) {
            MediaController.loadGalleryPhotosAlbums(0);
        }
        checkCamera(true);
        AndroidUtilities.makeAccessibilityAnnouncement(LocaleController.getString("AccDescrAttachButton", R.string.AccDescrAttachButton));
    }

    public void onOpenAnimationStart() {
    }

    public boolean canDismiss() {
        return true;
    }

    public void setAllowDrawContent(boolean value) {
        super.setAllowDrawContent(value);
        checkCameraViewPosition();
    }

    public void setMaxSelectedPhotos(int value, boolean order) {
        if (this.editingMessageObject == null) {
            this.maxSelectedPhotos = value;
            this.allowOrder = order;
        }
    }

    public void setOpenWithFrontFaceCamera(boolean value) {
        this.openWithFrontFaceCamera = value;
    }

    /* access modifiers changed from: private */
    public int addToSelectedPhotos(MediaController.PhotoEntry object, int index) {
        Object key = Integer.valueOf(object.imageId);
        if (selectedPhotos.containsKey(key)) {
            selectedPhotos.remove(key);
            int position = selectedPhotosOrder.indexOf(key);
            if (position >= 0) {
                selectedPhotosOrder.remove(position);
            }
            updatePhotosCounter(false);
            updateCheckedPhotoIndices();
            if (index >= 0) {
                object.reset();
                this.photoViewerProvider.updatePhotoAtIndex(index);
            }
            return position;
        }
        selectedPhotos.put(key, object);
        selectedPhotosOrder.add(key);
        updatePhotosCounter(true);
        return -1;
    }

    private void clearSelectedPhotos() {
        if (!selectedPhotos.isEmpty()) {
            for (Map.Entry<Object, Object> entry : selectedPhotos.entrySet()) {
                ((MediaController.PhotoEntry) entry.getValue()).reset();
            }
            selectedPhotos.clear();
            selectedPhotosOrder.clear();
        }
        if (!cameraPhotos.isEmpty()) {
            int size = cameraPhotos.size();
            for (int a = 0; a < size; a++) {
                MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) cameraPhotos.get(a);
                new File(photoEntry.path).delete();
                if (photoEntry.imagePath != null) {
                    new File(photoEntry.imagePath).delete();
                }
                if (photoEntry.thumbPath != null) {
                    new File(photoEntry.thumbPath).delete();
                }
            }
            cameraPhotos.clear();
        }
        updatePhotosButton(0);
        this.adapter.notifyDataSetChanged();
        this.cameraAttachAdapter.notifyDataSetChanged();
    }

    private class ButtonsAdapter extends RecyclerListView.SelectionAdapter {
        private int buttonsCount;
        private int contactButton;
        private int documentButton;
        private int galleryButton;
        private int locationButton;
        private Context mContext;
        private int musicButton;
        private int pollButton;

        public ButtonsAdapter(Context context) {
            this.mContext = context;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType != 0) {
                view = new AttachBotButton(this.mContext);
            } else {
                view = new AttachButton(this.mContext);
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int itemViewType = holder.getItemViewType();
            if (itemViewType == 0) {
                AttachButton attachButton = (AttachButton) holder.itemView;
                if (position == this.galleryButton) {
                    attachButton.setTextAndIcon(LocaleController.getString("ChatGallery", R.string.ChatGallery), Theme.chat_attachButtonDrawables[0]);
                    attachButton.setTag(1);
                } else if (position == this.documentButton) {
                    attachButton.setTextAndIcon(LocaleController.getString("ChatDocument", R.string.ChatDocument), Theme.chat_attachButtonDrawables[2]);
                    attachButton.setTag(4);
                } else if (position == this.locationButton) {
                    attachButton.setTextAndIcon(LocaleController.getString("ChatLocation", R.string.ChatLocation), Theme.chat_attachButtonDrawables[4]);
                    attachButton.setTag(6);
                } else if (position == this.musicButton) {
                    attachButton.setTextAndIcon(LocaleController.getString("AttachMusic", R.string.AttachMusic), Theme.chat_attachButtonDrawables[1]);
                    attachButton.setTag(3);
                } else if (position == this.pollButton) {
                    attachButton.setTextAndIcon(LocaleController.getString("Poll", R.string.Poll), Theme.chat_attachButtonDrawables[5]);
                    attachButton.setTag(9);
                } else if (position == this.contactButton) {
                    attachButton.setTextAndIcon(LocaleController.getString("AttachContact", R.string.AttachContact), Theme.chat_attachButtonDrawables[3]);
                    attachButton.setTag(5);
                }
            } else if (itemViewType == 1) {
                int position2 = position - this.buttonsCount;
                AttachBotButton child = (AttachBotButton) holder.itemView;
                child.setTag(Integer.valueOf(position2));
                child.setUser(MessagesController.getInstance(ChatAttachAlert.this.currentAccount).getUser(Integer.valueOf(MediaDataController.getInstance(ChatAttachAlert.this.currentAccount).inlineBots.get(position2).peer.user_id)));
            }
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            ChatAttachAlert.this.applyAttachButtonColors(holder.itemView);
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        public int getItemCount() {
            int count = this.buttonsCount;
            if (ChatAttachAlert.this.editingMessageObject != null || !(ChatAttachAlert.this.baseFragment instanceof ChatActivity)) {
                return count;
            }
            return count + MediaDataController.getInstance(ChatAttachAlert.this.currentAccount).inlineBots.size();
        }

        public void notifyDataSetChanged() {
            this.buttonsCount = 0;
            this.galleryButton = -1;
            this.documentButton = -1;
            this.musicButton = -1;
            this.pollButton = -1;
            this.contactButton = -1;
            this.locationButton = -1;
            if (!(ChatAttachAlert.this.baseFragment instanceof ChatActivity)) {
                int i = this.buttonsCount;
                int i2 = i + 1;
                this.buttonsCount = i2;
                this.galleryButton = i;
                this.buttonsCount = i2 + 1;
                this.documentButton = i2;
            } else if (ChatAttachAlert.this.editingMessageObject != null) {
                int i3 = this.buttonsCount;
                int i4 = i3 + 1;
                this.buttonsCount = i4;
                this.galleryButton = i3;
                int i5 = i4 + 1;
                this.buttonsCount = i5;
                this.documentButton = i4;
                this.buttonsCount = i5 + 1;
                this.musicButton = i5;
            } else {
                if (ChatAttachAlert.this.mediaEnabled) {
                    int i6 = this.buttonsCount;
                    int i7 = i6 + 1;
                    this.buttonsCount = i7;
                    this.galleryButton = i6;
                    this.buttonsCount = i7 + 1;
                    this.documentButton = i7;
                }
                int i8 = this.buttonsCount;
                this.buttonsCount = i8 + 1;
                this.locationButton = i8;
                if (ChatAttachAlert.this.pollsEnabled) {
                    int i9 = this.buttonsCount;
                    this.buttonsCount = i9 + 1;
                    this.pollButton = i9;
                } else {
                    int i10 = this.buttonsCount;
                    this.buttonsCount = i10 + 1;
                    this.contactButton = i10;
                }
                if (ChatAttachAlert.this.mediaEnabled) {
                    int i11 = this.buttonsCount;
                    this.buttonsCount = i11 + 1;
                    this.musicButton = i11;
                }
            }
            super.notifyDataSetChanged();
        }

        public int getButtonsCount() {
            return this.buttonsCount;
        }

        public int getItemViewType(int position) {
            if (position < this.buttonsCount) {
                return 0;
            }
            return 1;
        }
    }

    private class PhotoAttachAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public int itemsCount;
        private Context mContext;
        /* access modifiers changed from: private */
        public boolean needCamera;
        private ArrayList<RecyclerListView.Holder> viewsCache = new ArrayList<>(8);

        public PhotoAttachAdapter(Context context, boolean camera) {
            this.mContext = context;
            this.needCamera = camera;
            for (int a = 0; a < 8; a++) {
                this.viewsCache.add(createHolder());
            }
        }

        public RecyclerListView.Holder createHolder() {
            PhotoAttachPhotoCell cell = new PhotoAttachPhotoCell(this.mContext);
            if (Build.VERSION.SDK_INT >= 21 && this == ChatAttachAlert.this.adapter) {
                cell.setOutlineProvider(new ViewOutlineProvider() {
                    public void getOutline(View view, Outline outline) {
                        int position = ((Integer) ((PhotoAttachPhotoCell) view).getTag()).intValue();
                        if (PhotoAttachAdapter.this.needCamera && ChatAttachAlert.this.selectedAlbumEntry == ChatAttachAlert.this.galleryAlbumEntry) {
                            position++;
                        }
                        if (position == 0) {
                            int rad = AndroidUtilities.dp(ChatAttachAlert.this.cornerRadius * 8.0f);
                            outline.setRoundRect(0, 0, view.getMeasuredWidth() + rad, view.getMeasuredHeight() + rad, (float) rad);
                        } else if (position == ChatAttachAlert.this.itemsPerRow - 1) {
                            int rad2 = AndroidUtilities.dp(ChatAttachAlert.this.cornerRadius * 8.0f);
                            outline.setRoundRect(-rad2, 0, view.getMeasuredWidth(), view.getMeasuredHeight() + rad2, (float) rad2);
                        } else {
                            outline.setRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                        }
                    }
                });
                cell.setClipToOutline(true);
            }
            cell.setDelegate(new PhotoAttachPhotoCell.PhotoAttachPhotoCellDelegate() {
                public final void onCheckClick(PhotoAttachPhotoCell photoAttachPhotoCell) {
                    ChatAttachAlert.PhotoAttachAdapter.this.lambda$createHolder$0$ChatAttachAlert$PhotoAttachAdapter(photoAttachPhotoCell);
                }
            });
            return new RecyclerListView.Holder(cell);
        }

        public /* synthetic */ void lambda$createHolder$0$ChatAttachAlert$PhotoAttachAdapter(PhotoAttachPhotoCell v) {
            TLRPC.Chat chat;
            if (ChatAttachAlert.this.mediaEnabled) {
                int index = ((Integer) v.getTag()).intValue();
                MediaController.PhotoEntry photoEntry = v.getPhotoEntry();
                int i = 1;
                boolean added = !ChatAttachAlert.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId));
                if (!added || ChatAttachAlert.this.maxSelectedPhotos < 0 || ChatAttachAlert.selectedPhotos.size() < ChatAttachAlert.this.maxSelectedPhotos) {
                    int num = added ? ChatAttachAlert.selectedPhotosOrder.size() : -1;
                    if (!(ChatAttachAlert.this.baseFragment instanceof ChatActivity) || !ChatAttachAlert.this.allowOrder) {
                        v.setChecked(-1, added, true);
                    } else {
                        v.setChecked(num, added, true);
                    }
                    int unused = ChatAttachAlert.this.addToSelectedPhotos(photoEntry, index);
                    int updateIndex = index;
                    if (this == ChatAttachAlert.this.cameraAttachAdapter) {
                        if (ChatAttachAlert.this.adapter.needCamera && ChatAttachAlert.this.selectedAlbumEntry == ChatAttachAlert.this.galleryAlbumEntry) {
                            updateIndex++;
                        }
                        ChatAttachAlert.this.adapter.notifyItemChanged(updateIndex);
                    } else {
                        ChatAttachAlert.this.cameraAttachAdapter.notifyItemChanged(updateIndex);
                    }
                    ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                    if (!added) {
                        i = 2;
                    }
                    chatAttachAlert.updatePhotosButton(i);
                } else if (ChatAttachAlert.this.allowOrder && (ChatAttachAlert.this.baseFragment instanceof ChatActivity) && (chat = ((ChatActivity) ChatAttachAlert.this.baseFragment).getCurrentChat()) != null && !ChatObject.hasAdminRights(chat) && chat.slowmode_enabled && ChatAttachAlert.this.alertOnlyOnce != 2) {
                    AlertsCreator.createSimpleAlert(ChatAttachAlert.this.getContext(), LocaleController.getString("Slowmode", R.string.Slowmode), LocaleController.getString("SlowmodeSelectSendError", R.string.SlowmodeSelectSendError)).show();
                    if (ChatAttachAlert.this.alertOnlyOnce == 1) {
                        int unused2 = ChatAttachAlert.this.alertOnlyOnce = 2;
                    }
                }
            }
        }

        /* access modifiers changed from: private */
        public MediaController.PhotoEntry getPhoto(int position) {
            if (this.needCamera && ChatAttachAlert.this.selectedAlbumEntry == ChatAttachAlert.this.galleryAlbumEntry) {
                position--;
            }
            return ChatAttachAlert.this.getPhotoEntryAtPosition(position);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int itemViewType = holder.getItemViewType();
            int i = 0;
            boolean z = true;
            if (itemViewType == 0) {
                if (this.needCamera && ChatAttachAlert.this.selectedAlbumEntry == ChatAttachAlert.this.galleryAlbumEntry) {
                    position--;
                }
                PhotoAttachPhotoCell cell = (PhotoAttachPhotoCell) holder.itemView;
                if (this == ChatAttachAlert.this.adapter) {
                    cell.setItemSize(ChatAttachAlert.this.itemSize);
                } else {
                    cell.setIsVertical(ChatAttachAlert.this.cameraPhotoLayoutManager.getOrientation() == 1);
                }
                MediaController.PhotoEntry photoEntry = ChatAttachAlert.this.getPhotoEntryAtPosition(position);
                boolean z2 = this.needCamera && ChatAttachAlert.this.selectedAlbumEntry == ChatAttachAlert.this.galleryAlbumEntry;
                if (position != getItemCount() - 1) {
                    z = false;
                }
                cell.setPhotoEntry(photoEntry, z2, z);
                if (!(ChatAttachAlert.this.baseFragment instanceof ChatActivity) || !ChatAttachAlert.this.allowOrder) {
                    cell.setChecked(-1, ChatAttachAlert.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId)), false);
                } else {
                    cell.setChecked(ChatAttachAlert.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId)), ChatAttachAlert.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId)), false);
                }
                cell.getImageView().setTag(Integer.valueOf(position));
                cell.setTag(Integer.valueOf(position));
            } else if (itemViewType == 1) {
                PhotoAttachCameraCell cameraCell = (PhotoAttachCameraCell) holder.itemView;
                if (ChatAttachAlert.this.cameraView == null || !ChatAttachAlert.this.cameraView.isInitied()) {
                    cameraCell.setVisibility(0);
                } else {
                    cameraCell.setVisibility(4);
                }
                cameraCell.setItemSize(ChatAttachAlert.this.itemSize);
            } else if (itemViewType == 3) {
                PhotoAttachPermissionCell cell2 = (PhotoAttachPermissionCell) holder.itemView;
                cell2.setItemSize(ChatAttachAlert.this.itemSize);
                if (!this.needCamera || !ChatAttachAlert.this.noCameraPermissions || position != 0) {
                    i = 1;
                }
                cell2.setType(i);
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType != 0) {
                if (viewType == 1) {
                    PhotoAttachCameraCell cameraCell = new PhotoAttachCameraCell(this.mContext);
                    if (Build.VERSION.SDK_INT >= 21) {
                        cameraCell.setOutlineProvider(new ViewOutlineProvider() {
                            public void getOutline(View view, Outline outline) {
                                int rad = AndroidUtilities.dp(ChatAttachAlert.this.cornerRadius * 8.0f);
                                outline.setRoundRect(0, 0, view.getMeasuredWidth() + rad, view.getMeasuredHeight() + rad, (float) rad);
                            }
                        });
                        cameraCell.setClipToOutline(true);
                    }
                    return new RecyclerListView.Holder(cameraCell);
                } else if (viewType != 2) {
                    return new RecyclerListView.Holder(new PhotoAttachPermissionCell(this.mContext));
                } else {
                    return new RecyclerListView.Holder(new View(this.mContext) {
                        /* access modifiers changed from: protected */
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(ChatAttachAlert.this.gridExtraSpace, 1073741824));
                        }
                    });
                }
            } else if (this.viewsCache.isEmpty()) {
                return createHolder();
            } else {
                RecyclerListView.Holder holder = this.viewsCache.get(0);
                this.viewsCache.remove(0);
                return holder;
            }
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof PhotoAttachCameraCell) {
                ((PhotoAttachCameraCell) holder.itemView).updateBitmap();
            }
        }

        public int getItemCount() {
            if (!ChatAttachAlert.this.mediaEnabled) {
                return 1;
            }
            int count = 0;
            if (this.needCamera && ChatAttachAlert.this.selectedAlbumEntry == ChatAttachAlert.this.galleryAlbumEntry) {
                count = 0 + 1;
            }
            if (ChatAttachAlert.this.noGalleryPermissions && this == ChatAttachAlert.this.adapter) {
                count++;
            }
            int count2 = count + ChatAttachAlert.cameraPhotos.size();
            if (ChatAttachAlert.this.selectedAlbumEntry != null) {
                count2 += ChatAttachAlert.this.selectedAlbumEntry.photos.size();
            }
            if (this == ChatAttachAlert.this.adapter) {
                count2++;
            }
            this.itemsCount = count2;
            return count2;
        }

        public int getItemViewType(int position) {
            if (!ChatAttachAlert.this.mediaEnabled) {
                return 2;
            }
            if (this.needCamera && position == 0 && ChatAttachAlert.this.selectedAlbumEntry == ChatAttachAlert.this.galleryAlbumEntry) {
                return ChatAttachAlert.this.noCameraPermissions ? 3 : 1;
            }
            if (this == ChatAttachAlert.this.adapter && position == this.itemsCount - 1) {
                return 2;
            }
            if (ChatAttachAlert.this.noGalleryPermissions) {
                return 3;
            }
            return 0;
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            if (this == ChatAttachAlert.this.adapter) {
                ChatAttachAlert.this.progressView.setVisibility((!(getItemCount() == 1 && ChatAttachAlert.this.selectedAlbumEntry == null) && ChatAttachAlert.this.mediaEnabled) ? 4 : 0);
            }
        }
    }

    public void dismissInternal() {
        if (this.containerView != null) {
            this.containerView.setVisibility(4);
        }
        super.dismissInternal();
    }

    public void onBackPressed() {
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji == null || !editTextEmoji.isPopupShowing()) {
            super.onBackPressed();
        } else {
            this.commentTextView.hidePopup(true);
        }
    }

    public void dismissWithButtonClick(int item) {
        super.dismissWithButtonClick(item);
        hideCamera((item == 0 || item == 2) ? false : true);
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithTouchOutside() {
        return !this.cameraOpened;
    }

    public void dismiss() {
        if (!this.cameraAnimationInProgress) {
            if (this.cameraOpened) {
                closeCamera(true);
                return;
            }
            EditTextEmoji editTextEmoji = this.commentTextView;
            if (editTextEmoji != null) {
                AndroidUtilities.hideKeyboard(editTextEmoji.getEditText());
            }
            hideCamera(true);
            super.dismiss();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!this.cameraOpened || (keyCode != 24 && keyCode != 25)) {
            return super.onKeyDown(keyCode, event);
        }
        this.shutterButton.getDelegate().shutterReleased();
        return true;
    }
}
