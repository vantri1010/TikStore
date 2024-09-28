package im.bclpbkiauv.ui.hui.friendscircle_v1.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextPaint;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.DecelerateInterpolator;
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
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageReceiver;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.messenger.VideoEditedInfo;
import im.bclpbkiauv.messenger.camera.CameraController;
import im.bclpbkiauv.messenger.camera.CameraView;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.ChatActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.cells.PhotoAttachCameraCell;
import im.bclpbkiauv.ui.cells.PhotoAttachPermissionCell;
import im.bclpbkiauv.ui.cells.PhotoAttachPhotoCell;
import im.bclpbkiauv.ui.components.EditTextEmoji;
import im.bclpbkiauv.ui.components.EmptyTextProgressView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.RecyclerViewItemRangeSelector;
import im.bclpbkiauv.ui.components.ShutterButton;
import im.bclpbkiauv.ui.components.SizeNotifierFrameLayout;
import im.bclpbkiauv.ui.components.ZoomControlView;
import im.bclpbkiauv.ui.hui.adapter.EditInputFilter;
import im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreviewActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.toast.FcToastUtils;
import im.bclpbkiauv.ui.hviews.dialogs.XDialog;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ImagePreSelectorActivity extends BottomSheet implements NotificationCenter.NotificationCenterDelegate, BottomSheet.BottomSheetDelegateInterface {
    public static final int SELECT_TYPE_GIF = 3;
    public static final int SELECT_TYPE_IMG = 1;
    public static final int SELECT_TYPE_NONE = 0;
    public static final int SELECT_TYPE_VIDEO = 2;
    /* access modifiers changed from: private */
    public static ArrayList<Object> cameraPhotos = new ArrayList<>();
    private static final int compress = 1;
    private static final int group = 0;
    private static final int isSkip = 3;
    private static int lastImageId = -1;
    /* access modifiers changed from: private */
    public static boolean mediaFromExternalCamera;
    /* access modifiers changed from: private */
    public static HashMap<Object, Object> selectedPhotos = new HashMap<>();
    /* access modifiers changed from: private */
    public static ArrayList<Object> selectedPhotosOrder = new ArrayList<>();
    private final int VIDEO_TIME_LENGTH;
    /* access modifiers changed from: private */
    public ActionBar actionBar;
    /* access modifiers changed from: private */
    public AnimatorSet actionBarAnimation;
    /* access modifiers changed from: private */
    public View actionBarShadow;
    /* access modifiers changed from: private */
    public PhotoAttachAdapter adapter;
    /* access modifiers changed from: private */
    public boolean allowOrder = true;
    private int[] animateCameraValues = new int[5];
    private AnimatorSet animatorSet;
    private boolean buttonPressed;
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
    private int currentAccount = UserConfig.selectedAccount;
    /* access modifiers changed from: private */
    public int currentSelectMediaType;
    private int currentSelectedCount;
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
    private MessageObject editingMessageObject;
    /* access modifiers changed from: private */
    public boolean enterCommentEventSent;
    /* access modifiers changed from: private */
    public boolean flashAnimationInProgress;
    /* access modifiers changed from: private */
    public ImageView[] flashModeButton = new ImageView[2];
    /* access modifiers changed from: private */
    public MediaController.AlbumEntry galleryAlbumEntry;
    /* access modifiers changed from: private */
    public int gridExtraSpace;
    /* access modifiers changed from: private */
    public RecyclerListView gridView;
    private Rect hitRect = new Rect();
    private DecelerateInterpolator interpolator = new DecelerateInterpolator(1.5f);
    private final ActionBarMenuItem isSkipMenu;
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
    public Activity mActivity;
    /* access modifiers changed from: private */
    public int maxSelectedPhotos = 9;
    private boolean maybeStartDraging;
    /* access modifiers changed from: private */
    public boolean mediaCaptured;
    /* access modifiers changed from: private */
    public boolean mediaEnabled = true;
    /* access modifiers changed from: private */
    public boolean noCameraPermissions;
    /* access modifiers changed from: private */
    public boolean noGalleryPermissions;
    private boolean openWithFrontFaceCamera;
    private boolean paused;
    private ImagePreviewActivity.PhotoViewerProvider photoViewerProvider;
    private float pinchStartDistance;
    private boolean pressed;
    /* access modifiers changed from: private */
    public EmptyTextProgressView progressView;
    /* access modifiers changed from: private */
    public TextView recordTime;
    /* access modifiers changed from: private */
    public boolean requestingPermissions;
    /* access modifiers changed from: private */
    public int scrollOffsetY;
    /* access modifiers changed from: private */
    public MediaController.AlbumEntry selectedAlbumEntry;
    private TextView selectedTextView;
    /* access modifiers changed from: private */
    public boolean shouldSelect;
    /* access modifiers changed from: private */
    public ShutterButton shutterButton;
    private SizeNotifierFrameLayout sizeNotifierFrameLayout;
    /* access modifiers changed from: private */
    public ImageView switchCameraButton;
    /* access modifiers changed from: private */
    public boolean takingPhoto;
    private TextPaint textPaint = new TextPaint(1);
    /* access modifiers changed from: private */
    public TextView tooltipTextView;
    /* access modifiers changed from: private */
    public Runnable videoRecordRunnable;
    /* access modifiers changed from: private */
    public int videoRecordTime;
    private int[] viewPosition = new int[2];
    private ImageView writeButton;
    private FrameLayout writeButtonContainer;
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

    static /* synthetic */ int access$7108(ImagePreSelectorActivity x0) {
        int i = x0.videoRecordTime;
        x0.videoRecordTime = i + 1;
        return i;
    }

    static /* synthetic */ int access$8010() {
        int i = lastImageId;
        lastImageId = i - 1;
        return i;
    }

    public ArrayList<MediaController.AlbumEntry> getDropDownAlbums() {
        return this.dropDownAlbums;
    }

    private class BasePhotoProvider extends ImagePreviewActivity.EmptyPhotoViewerProvider {
        private BasePhotoProvider() {
        }

        public boolean isPhotoChecked(int index) {
            MediaController.PhotoEntry photoEntry = ImagePreSelectorActivity.this.getPhotoEntryAtPosition(index);
            return photoEntry != null && ImagePreSelectorActivity.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId));
        }

        public int setPhotoChecked(int index, VideoEditedInfo videoEditedInfo) {
            MediaController.PhotoEntry photoEntry;
            if ((ImagePreSelectorActivity.this.maxSelectedPhotos >= 0 && ImagePreSelectorActivity.selectedPhotos.size() >= ImagePreSelectorActivity.this.maxSelectedPhotos && !isPhotoChecked(index)) || (photoEntry = ImagePreSelectorActivity.this.getPhotoEntryAtPosition(index)) == null) {
                return -1;
            }
            boolean add = true;
            int access$300 = ImagePreSelectorActivity.this.addToSelectedPhotos(photoEntry, -1);
            int num = access$300;
            if (access$300 == -1) {
                num = ImagePreSelectorActivity.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId));
            } else {
                add = false;
                photoEntry.editedInfo = null;
            }
            photoEntry.editedInfo = videoEditedInfo;
            int count = ImagePreSelectorActivity.this.gridView.getChildCount();
            int a = 0;
            while (true) {
                if (a >= count) {
                    break;
                }
                View view = ImagePreSelectorActivity.this.gridView.getChildAt(a);
                if ((view instanceof PhotoAttachPhotoCell) && ((Integer) view.getTag()).intValue() == index) {
                    ((PhotoAttachPhotoCell) view).setChecked(-1, add, false);
                    break;
                }
                a++;
            }
            int count2 = ImagePreSelectorActivity.this.cameraPhotoRecyclerView.getChildCount();
            int a2 = 0;
            while (true) {
                if (a2 >= count2) {
                    break;
                }
                View view2 = ImagePreSelectorActivity.this.cameraPhotoRecyclerView.getChildAt(a2);
                if ((view2 instanceof PhotoAttachPhotoCell) && ((Integer) view2.getTag()).intValue() == index) {
                    ((PhotoAttachPhotoCell) view2).setChecked(-1, add, false);
                    break;
                }
                a2++;
            }
            ImagePreSelectorActivity.this.updatePhotosButton();
            return num;
        }

        public int getSelectedCount() {
            return ImagePreSelectorActivity.selectedPhotos.size();
        }

        public ArrayList<Object> getSelectedPhotosOrder() {
            return ImagePreSelectorActivity.selectedPhotosOrder;
        }

        public HashMap<Object, Object> getSelectedPhotos() {
            return ImagePreSelectorActivity.selectedPhotos;
        }

        public int getPhotoIndex(int index) {
            MediaController.PhotoEntry photoEntry = ImagePreSelectorActivity.this.getPhotoEntryAtPosition(index);
            if (photoEntry == null) {
                return -1;
            }
            return ImagePreSelectorActivity.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId));
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 22 && isLightColor(Theme.getColor(Theme.key_dialogBackground))) {
            getWindow().getDecorView().setSystemUiVisibility(9216);
        }
    }

    public boolean isLightColor(int color) {
        if (1.0d - ((((((double) Color.red(color)) * 0.299d) + (((double) Color.green(color)) * 0.587d)) + (((double) Color.blue(color)) * 0.114d)) / 255.0d) < 0.5d) {
            return true;
        }
        return false;
    }

    private void updateCheckedPhotoIndices() {
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

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ImagePreSelectorActivity(android.app.Activity r33) {
        /*
            r32 = this;
            r6 = r32
            r7 = r33
            r8 = 1
            r9 = 0
            r6.<init>(r7, r9, r8)
            android.text.TextPaint r0 = new android.text.TextPaint
            r0.<init>(r8)
            r6.textPaint = r0
            r0 = 1065353216(0x3f800000, float:1.0)
            r6.cornerRadius = r0
            int r1 = im.bclpbkiauv.messenger.UserConfig.selectedAccount
            r6.currentAccount = r1
            r6.mediaEnabled = r8
            r10 = 2
            android.widget.ImageView[] r1 = new android.widget.ImageView[r10]
            r6.flashModeButton = r1
            int[] r1 = new int[r10]
            r6.cameraViewLocation = r1
            int[] r1 = new int[r10]
            r6.viewPosition = r1
            r1 = 5
            int[] r1 = new int[r1]
            r6.animateCameraValues = r1
            android.view.animation.DecelerateInterpolator r1 = new android.view.animation.DecelerateInterpolator
            r2 = 1069547520(0x3fc00000, float:1.5)
            r1.<init>(r2)
            r6.interpolator = r1
            r1 = 9
            r6.maxSelectedPhotos = r1
            r6.allowOrder = r8
            android.graphics.Rect r1 = new android.graphics.Rect
            r1.<init>()
            r6.hitRect = r1
            r11 = 1117782016(0x42a00000, float:80.0)
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r11)
            r6.itemSize = r1
            r6.lastItemSize = r1
            r1 = 3
            r6.itemsPerRow = r1
            r6.loading = r8
            r2 = 59
            r6.VIDEO_TIME_LENGTH = r2
            r6.currentSelectMediaType = r9
            im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity$1 r2 = new im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity$1
            r2.<init>()
            r6.photoViewerProvider = r2
            android.view.animation.OvershootInterpolator r2 = new android.view.animation.OvershootInterpolator
            r3 = 1060320051(0x3f333333, float:0.7)
            r2.<init>(r3)
            r6.openInterpolator = r2
            r6.mActivity = r7
            r6.setDelegate(r6)
            r6.checkCamera(r9)
            int r2 = r6.currentAccount
            im.bclpbkiauv.messenger.NotificationCenter r2 = im.bclpbkiauv.messenger.NotificationCenter.getInstance(r2)
            int r3 = im.bclpbkiauv.messenger.NotificationCenter.albumsDidLoad
            r2.addObserver(r6, r3)
            int r2 = r6.currentAccount
            im.bclpbkiauv.messenger.NotificationCenter r2 = im.bclpbkiauv.messenger.NotificationCenter.getInstance(r2)
            int r3 = im.bclpbkiauv.messenger.NotificationCenter.reloadInlineHints
            r2.addObserver(r6, r3)
            im.bclpbkiauv.messenger.NotificationCenter r2 = im.bclpbkiauv.messenger.NotificationCenter.getGlobalInstance()
            int r3 = im.bclpbkiauv.messenger.NotificationCenter.cameraInitied
            r2.addObserver(r6, r3)
            r6.mblnCanScroll = r9
            android.content.res.Resources r2 = r33.getResources()
            r3 = 2131231190(0x7f0801d6, float:1.8078454E38)
            android.graphics.drawable.Drawable r2 = r2.getDrawable(r3)
            android.graphics.drawable.Drawable r2 = r2.mutate()
            r6.cameraDrawable = r2
            im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity$2 r2 = new im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity$2
            r2.<init>(r7)
            r6.sizeNotifierFrameLayout = r2
            r6.containerView = r2
            android.view.ViewGroup r2 = r6.containerView
            r2.setWillNotDraw(r9)
            android.view.ViewGroup r2 = r6.containerView
            int r3 = r6.backgroundPaddingLeft
            int r4 = r6.backgroundPaddingLeft
            r2.setPadding(r3, r9, r4, r9)
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r7)
            r6.selectedTextView = r2
            java.lang.String r3 = "dialogTextBlack"
            int r4 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r3)
            r2.setTextColor(r4)
            android.widget.TextView r2 = r6.selectedTextView
            r12 = 1098907648(0x41800000, float:16.0)
            r2.setTextSize(r8, r12)
            android.widget.TextView r2 = r6.selectedTextView
            java.lang.String r13 = "fonts/rmedium.ttf"
            android.graphics.Typeface r4 = im.bclpbkiauv.messenger.AndroidUtilities.getTypeface(r13)
            r2.setTypeface(r4)
            android.widget.TextView r2 = r6.selectedTextView
            r14 = 51
            r2.setGravity(r14)
            android.widget.TextView r2 = r6.selectedTextView
            r15 = 4
            r2.setVisibility(r15)
            android.widget.TextView r2 = r6.selectedTextView
            r5 = 0
            r2.setAlpha(r5)
            android.view.ViewGroup r2 = r6.containerView
            android.widget.TextView r4 = r6.selectedTextView
            r16 = -1082130432(0xffffffffbf800000, float:-1.0)
            r17 = -1073741824(0xffffffffc0000000, float:-2.0)
            r18 = 51
            r19 = 1102577664(0x41b80000, float:23.0)
            r20 = 0
            r21 = 1111490560(0x42400000, float:48.0)
            r22 = 0
            android.widget.FrameLayout$LayoutParams r14 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r2.addView(r4, r14)
            im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity$3 r2 = new im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity$3
            r2.<init>(r7)
            r6.actionBar = r2
            java.lang.String r4 = "dialogBackground"
            int r4 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r4)
            r2.setBackgroundColor(r4)
            im.bclpbkiauv.ui.actionbar.ActionBar r2 = r6.actionBar
            r4 = 2131558496(0x7f0d0060, float:1.874231E38)
            r2.setBackButtonImage(r4)
            im.bclpbkiauv.ui.actionbar.ActionBar r2 = r6.actionBar
            int r4 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r3)
            r2.setItemsColor(r4, r9)
            im.bclpbkiauv.ui.actionbar.ActionBar r2 = r6.actionBar
            java.lang.String r4 = "dialogButtonSelector"
            int r4 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r4)
            r2.setItemsBackgroundColor(r4, r9)
            im.bclpbkiauv.ui.actionbar.ActionBar r2 = r6.actionBar
            int r4 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r3)
            r2.setTitleColor(r4)
            im.bclpbkiauv.ui.actionbar.ActionBar r2 = r6.actionBar
            r2.setOccupyStatusBar(r9)
            im.bclpbkiauv.ui.actionbar.ActionBar r2 = r6.actionBar
            r2.setAlpha(r5)
            android.view.ViewGroup r2 = r6.containerView
            im.bclpbkiauv.ui.actionbar.ActionBar r4 = r6.actionBar
            r14 = -1
            r15 = -1073741824(0xffffffffc0000000, float:-2.0)
            android.widget.FrameLayout$LayoutParams r15 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r14, r15)
            r2.addView(r4, r15)
            im.bclpbkiauv.ui.actionbar.ActionBar r2 = r6.actionBar
            im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity$4 r4 = new im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity$4
            r4.<init>()
            r2.setActionBarMenuOnItemClick(r4)
            im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity$5 r2 = new im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity$5
            r2.<init>(r7)
            r6.gridView = r2
            im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity$PhotoAttachAdapter r4 = new im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity$PhotoAttachAdapter
            r4.<init>(r7, r8)
            r6.adapter = r4
            r2.setAdapter(r4)
            im.bclpbkiauv.ui.components.RecyclerListView r2 = r6.gridView
            r2.setClipToPadding(r9)
            im.bclpbkiauv.ui.components.RecyclerListView r2 = r6.gridView
            r15 = 0
            r2.setItemAnimator(r15)
            im.bclpbkiauv.ui.components.RecyclerListView r2 = r6.gridView
            r2.setLayoutAnimation(r15)
            im.bclpbkiauv.ui.components.RecyclerListView r2 = r6.gridView
            r2.setVerticalScrollBarEnabled(r9)
            im.bclpbkiauv.ui.components.RecyclerListView r2 = r6.gridView
            java.lang.String r4 = "dialogScrollGlow"
            int r4 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r4)
            r2.setGlowColor(r4)
            im.bclpbkiauv.ui.components.RecyclerListView r2 = r6.gridView
            java.lang.String r4 = "windowBackgroundGray"
            int r4 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r4)
            r2.setBackgroundColor(r4)
            android.view.ViewGroup r2 = r6.containerView
            im.bclpbkiauv.ui.components.RecyclerListView r4 = r6.gridView
            r23 = -1082130432(0xffffffffbf800000, float:-1.0)
            r24 = -1082130432(0xffffffffbf800000, float:-1.0)
            r25 = 51
            r26 = 0
            r27 = 1093664768(0x41300000, float:11.0)
            r28 = 0
            r29 = 0
            android.widget.FrameLayout$LayoutParams r10 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r2.addView(r4, r10)
            im.bclpbkiauv.ui.components.RecyclerListView r2 = r6.gridView
            im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity$6 r4 = new im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity$6
            r4.<init>()
            r2.setOnScrollListener(r4)
            im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity$7 r2 = new im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity$7
            int r4 = r6.itemSize
            r2.<init>(r7, r4)
            r6.layoutManager = r2
            im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity$8 r4 = new im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity$8
            r4.<init>()
            r2.setSpanSizeLookup(r4)
            im.bclpbkiauv.ui.components.RecyclerListView r2 = r6.gridView
            androidx.recyclerview.widget.GridLayoutManager r4 = r6.layoutManager
            r2.setLayoutManager(r4)
            im.bclpbkiauv.ui.components.RecyclerListView r2 = r6.gridView
            im.bclpbkiauv.ui.hui.friendscircle_v1.ui.-$$Lambda$ImagePreSelectorActivity$SPtbi-g4I0bKGM6PfQfC3co0PW4 r4 = new im.bclpbkiauv.ui.hui.friendscircle_v1.ui.-$$Lambda$ImagePreSelectorActivity$SPtbi-g4I0bKGM6PfQfC3co0PW4
            r4.<init>()
            r2.setOnItemClickListener((im.bclpbkiauv.ui.components.RecyclerListView.OnItemClickListener) r4)
            im.bclpbkiauv.ui.components.RecyclerListView r2 = r6.gridView
            im.bclpbkiauv.ui.hui.friendscircle_v1.ui.-$$Lambda$ImagePreSelectorActivity$F6MIZLZ8GgwhlqdiTdeyv-sjAKw r4 = new im.bclpbkiauv.ui.hui.friendscircle_v1.ui.-$$Lambda$ImagePreSelectorActivity$F6MIZLZ8GgwhlqdiTdeyv-sjAKw
            r4.<init>()
            r2.setOnItemLongClickListener((im.bclpbkiauv.ui.components.RecyclerListView.OnItemLongClickListener) r4)
            im.bclpbkiauv.ui.components.RecyclerViewItemRangeSelector r2 = new im.bclpbkiauv.ui.components.RecyclerViewItemRangeSelector
            im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity$9 r4 = new im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity$9
            r4.<init>()
            r2.<init>(r4)
            r6.itemRangeSelector = r2
            im.bclpbkiauv.ui.components.RecyclerListView r4 = r6.gridView
            r4.addOnItemTouchListener(r2)
            im.bclpbkiauv.ui.actionbar.ActionBar r2 = r6.actionBar
            im.bclpbkiauv.ui.actionbar.ActionBarMenu r10 = r2.createMenu()
            java.lang.String r2 = "fc_skip"
            r4 = 2131695103(0x7f0f15ff, float:1.9019381E38)
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r4)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r1 = r10.addItem((int) r1, (java.lang.CharSequence) r2)
            r6.isSkipMenu = r1
            android.view.View r1 = r1.getContentView()
            r4 = r1
            android.widget.TextView r4 = (android.widget.TextView) r4
            android.content.res.Resources r1 = r33.getResources()
            r2 = 2131099730(0x7f060052, float:1.7811821E38)
            int r1 = r1.getColor(r2)
            r4.setTextColor(r1)
            r4.setTextSize(r12)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r1 = new im.bclpbkiauv.ui.actionbar.ActionBarMenuItem
            r1.<init>(r7, r10, r9, r9)
            r6.dropDownContainer = r1
            r1.setSubMenuOpenSide(r8)
            im.bclpbkiauv.ui.actionbar.ActionBar r1 = r6.actionBar
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r2 = r6.dropDownContainer
            boolean r18 = im.bclpbkiauv.messenger.AndroidUtilities.isTablet()
            r19 = 1113587712(0x42600000, float:56.0)
            if (r18 == 0) goto L_0x0242
            r18 = 1115684864(0x42800000, float:64.0)
            r26 = 1115684864(0x42800000, float:64.0)
            goto L_0x0244
        L_0x0242:
            r26 = 1113587712(0x42600000, float:56.0)
        L_0x0244:
            r27 = 0
            r28 = 1109393408(0x42200000, float:40.0)
            r29 = 0
            r23 = -1073741824(0xffffffffc0000000, float:-2.0)
            r24 = -1082130432(0xffffffffbf800000, float:-1.0)
            r25 = 17
            android.widget.FrameLayout$LayoutParams r11 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r1.addView(r2, r9, r11)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r1 = r6.dropDownContainer
            im.bclpbkiauv.ui.hui.friendscircle_v1.ui.-$$Lambda$ImagePreSelectorActivity$2N0I6ADX5Ekn6T0I8_qvwrQKsac r2 = new im.bclpbkiauv.ui.hui.friendscircle_v1.ui.-$$Lambda$ImagePreSelectorActivity$2N0I6ADX5Ekn6T0I8_qvwrQKsac
            r2.<init>()
            r1.setOnClickListener(r2)
            android.widget.TextView r1 = new android.widget.TextView
            r1.<init>(r7)
            r6.dropDown = r1
            r11 = 17
            r1.setGravity(r11)
            android.widget.TextView r1 = r6.dropDown
            r1.setSingleLine(r8)
            android.widget.TextView r1 = r6.dropDown
            r1.setLines(r8)
            android.widget.TextView r1 = r6.dropDown
            r1.setMaxLines(r8)
            android.widget.TextView r1 = r6.dropDown
            android.text.TextUtils$TruncateAt r2 = android.text.TextUtils.TruncateAt.END
            r1.setEllipsize(r2)
            android.widget.TextView r1 = r6.dropDown
            int r2 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r3)
            r1.setTextColor(r2)
            android.widget.TextView r1 = r6.dropDown
            r1.setTextSize(r12)
            android.widget.TextView r1 = r6.dropDown
            r2 = 2131689742(0x7f0f010e, float:1.9008508E38)
            java.lang.String r12 = "AllMedia"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r12, r2)
            r1.setText(r2)
            android.widget.TextView r1 = r6.dropDown
            android.graphics.Typeface r2 = im.bclpbkiauv.messenger.AndroidUtilities.getTypeface(r13)
            r1.setTypeface(r2)
            android.content.res.Resources r1 = r33.getResources()
            r2 = 2131231085(0x7f08016d, float:1.8078241E38)
            android.graphics.drawable.Drawable r1 = r1.getDrawable(r2)
            android.graphics.drawable.Drawable r1 = r1.mutate()
            r6.dropDownDrawable = r1
            android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
            int r3 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r3)
            android.graphics.PorterDuff$Mode r12 = android.graphics.PorterDuff.Mode.MULTIPLY
            r2.<init>(r3, r12)
            r1.setColorFilter(r2)
            android.widget.TextView r1 = r6.dropDown
            r2 = 1088421888(0x40e00000, float:7.0)
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
            r1.setCompoundDrawablePadding(r2)
            android.widget.TextView r1 = r6.dropDown
            r1.setPadding(r9, r9, r9, r9)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r1 = r6.dropDownContainer
            android.widget.TextView r2 = r6.dropDown
            r24 = -1073741824(0xffffffffc0000000, float:-2.0)
            r26 = 0
            r28 = 0
            android.widget.FrameLayout$LayoutParams r3 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r1.addView(r2, r3)
            android.view.View r1 = new android.view.View
            r1.<init>(r7)
            r6.actionBarShadow = r1
            r1.setAlpha(r5)
            android.view.View r1 = r6.actionBarShadow
            java.lang.String r2 = "dialogShadowLine"
            int r2 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r2)
            r1.setBackgroundColor(r2)
            android.view.ViewGroup r1 = r6.containerView
            android.view.View r2 = r6.actionBarShadow
            android.widget.FrameLayout$LayoutParams r0 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r14, r0)
            r1.addView(r2, r0)
            im.bclpbkiauv.ui.components.EmptyTextProgressView r0 = new im.bclpbkiauv.ui.components.EmptyTextProgressView
            r0.<init>(r7)
            r6.progressView = r0
            r1 = 2131692235(0x7f0f0acb, float:1.9013564E38)
            java.lang.String r2 = "NoPhotos"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            im.bclpbkiauv.ui.components.EmptyTextProgressView r0 = r6.progressView
            r0.setOnTouchListener(r15)
            im.bclpbkiauv.ui.components.EmptyTextProgressView r0 = r6.progressView
            r1 = 20
            r0.setTextSize(r1)
            android.view.ViewGroup r0 = r6.containerView
            im.bclpbkiauv.ui.components.EmptyTextProgressView r1 = r6.progressView
            r2 = 1117782016(0x42a00000, float:80.0)
            android.widget.FrameLayout$LayoutParams r3 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r14, r2)
            r0.addView(r1, r3)
            boolean r0 = r6.loading
            if (r0 == 0) goto L_0x033e
            im.bclpbkiauv.ui.components.EmptyTextProgressView r0 = r6.progressView
            r0.showProgress()
            goto L_0x0343
        L_0x033e:
            im.bclpbkiauv.ui.components.EmptyTextProgressView r0 = r6.progressView
            r0.showTextView()
        L_0x0343:
            im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity$10 r12 = new im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity$10
            im.bclpbkiauv.ui.components.SizeNotifierFrameLayout r3 = r6.sizeNotifierFrameLayout
            r21 = 0
            r22 = 1
            r0 = r12
            r1 = r32
            r2 = r33
            r23 = r4
            r4 = r21
            r15 = 0
            r5 = r22
            r0.<init>(r2, r3, r4, r5)
            r6.commentTextView = r12
            android.text.InputFilter[] r0 = new android.text.InputFilter[r8]
            android.text.InputFilter$LengthFilter r1 = new android.text.InputFilter$LengthFilter
            int r2 = im.bclpbkiauv.messenger.UserConfig.selectedAccount
            im.bclpbkiauv.messenger.MessagesController r2 = im.bclpbkiauv.messenger.MessagesController.getInstance(r2)
            int r2 = r2.maxCaptionLength
            r1.<init>(r2)
            r0[r9] = r1
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
            r1.setMaxLines(r8)
            r1.setSingleLine(r8)
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r7)
            r6.writeButtonContainer = r2
            r2.setVisibility(r9)
            android.widget.FrameLayout r2 = r6.writeButtonContainer
            r3 = 2131693795(0x7f0f10e3, float:1.9016728E38)
            java.lang.String r4 = "Send"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r3)
            r2.setContentDescription(r3)
            android.widget.FrameLayout r2 = r6.writeButtonContainer
            im.bclpbkiauv.ui.hui.friendscircle_v1.ui.-$$Lambda$ImagePreSelectorActivity$vMrGox_ARRA3B5x4SNb2I1Fz7Sk r3 = new im.bclpbkiauv.ui.hui.friendscircle_v1.ui.-$$Lambda$ImagePreSelectorActivity$vMrGox_ARRA3B5x4SNb2I1Fz7Sk
            r3.<init>()
            r2.setOnClickListener(r3)
            android.widget.ImageView r2 = new android.widget.ImageView
            r2.<init>(r7)
            r6.writeButton = r2
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r19)
            java.lang.String r3 = "dialogFloatingButton"
            int r3 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r3)
            java.lang.String r4 = "dialogFloatingButtonPressed"
            int r4 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r4)
            android.graphics.drawable.Drawable r2 = im.bclpbkiauv.ui.actionbar.Theme.createSimpleSelectorCircleDrawable(r2, r3, r4)
            r6.writeButtonDrawable = r2
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 21
            if (r2 >= r3) goto L_0x0405
            android.content.res.Resources r2 = r33.getResources()
            r4 = 2131231019(0x7f08012b, float:1.8078107E38)
            android.graphics.drawable.Drawable r2 = r2.getDrawable(r4)
            android.graphics.drawable.Drawable r2 = r2.mutate()
            android.graphics.PorterDuffColorFilter r4 = new android.graphics.PorterDuffColorFilter
            r5 = -16777216(0xffffffffff000000, float:-1.7014118E38)
            android.graphics.PorterDuff$Mode r12 = android.graphics.PorterDuff.Mode.MULTIPLY
            r4.<init>(r5, r12)
            r2.setColorFilter(r4)
            im.bclpbkiauv.ui.components.CombinedDrawable r4 = new im.bclpbkiauv.ui.components.CombinedDrawable
            android.graphics.drawable.Drawable r5 = r6.writeButtonDrawable
            r4.<init>(r2, r5, r9, r9)
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r19)
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r19)
            r4.setIconSize(r5, r12)
            r6.writeButtonDrawable = r4
        L_0x0405:
            android.widget.ImageView r2 = r6.writeButton
            android.graphics.drawable.Drawable r4 = r6.writeButtonDrawable
            r2.setBackgroundDrawable(r4)
            android.widget.ImageView r2 = r6.writeButton
            r4 = 2131230835(0x7f080073, float:1.8077734E38)
            r2.setImageResource(r4)
            android.widget.ImageView r2 = r6.writeButton
            android.graphics.PorterDuffColorFilter r4 = new android.graphics.PorterDuffColorFilter
            java.lang.String r5 = "dialogFloatingIcon"
            int r5 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r5)
            android.graphics.PorterDuff$Mode r12 = android.graphics.PorterDuff.Mode.MULTIPLY
            r4.<init>(r5, r12)
            r2.setColorFilter(r4)
            android.widget.ImageView r2 = r6.writeButton
            android.widget.ImageView$ScaleType r4 = android.widget.ImageView.ScaleType.CENTER
            r2.setScaleType(r4)
            int r2 = android.os.Build.VERSION.SDK_INT
            if (r2 < r3) goto L_0x043b
            android.widget.ImageView r2 = r6.writeButton
            im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity$11 r4 = new im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity$11
            r4.<init>()
            r2.setOutlineProvider(r4)
        L_0x043b:
            android.widget.FrameLayout r2 = r6.writeButtonContainer
            android.widget.ImageView r4 = r6.writeButton
            int r5 = android.os.Build.VERSION.SDK_INT
            r12 = 1114636288(0x42700000, float:60.0)
            if (r5 < r3) goto L_0x0448
            r24 = 1113587712(0x42600000, float:56.0)
            goto L_0x044a
        L_0x0448:
            r24 = 1114636288(0x42700000, float:60.0)
        L_0x044a:
            int r5 = android.os.Build.VERSION.SDK_INT
            if (r5 < r3) goto L_0x0451
            r25 = 1113587712(0x42600000, float:56.0)
            goto L_0x0453
        L_0x0451:
            r25 = 1114636288(0x42700000, float:60.0)
        L_0x0453:
            r26 = 51
            int r5 = android.os.Build.VERSION.SDK_INT
            if (r5 < r3) goto L_0x045e
            r5 = 1073741824(0x40000000, float:2.0)
            r27 = 1073741824(0x40000000, float:2.0)
            goto L_0x0460
        L_0x045e:
            r27 = 0
        L_0x0460:
            r28 = 0
            r29 = 0
            r30 = 0
            android.widget.FrameLayout$LayoutParams r5 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r24, r25, r26, r27, r28, r29, r30)
            r2.addView(r4, r5)
            android.text.TextPaint r2 = r6.textPaint
            r4 = 1094713344(0x41400000, float:12.0)
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r2.setTextSize(r4)
            android.text.TextPaint r2 = r6.textPaint
            android.graphics.Typeface r4 = im.bclpbkiauv.messenger.AndroidUtilities.getTypeface(r13)
            r2.setTypeface(r4)
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r7)
            r6.recordTime = r2
            r4 = 2131231628(0x7f08038c, float:1.8079342E38)
            r2.setBackgroundResource(r4)
            android.widget.TextView r2 = r6.recordTime
            android.graphics.drawable.Drawable r2 = r2.getBackground()
            android.graphics.PorterDuffColorFilter r4 = new android.graphics.PorterDuffColorFilter
            r5 = 1711276032(0x66000000, float:1.5111573E23)
            android.graphics.PorterDuff$Mode r12 = android.graphics.PorterDuff.Mode.MULTIPLY
            r4.<init>(r5, r12)
            r2.setColorFilter(r4)
            android.widget.TextView r2 = r6.recordTime
            r4 = 1097859072(0x41700000, float:15.0)
            r2.setTextSize(r8, r4)
            android.widget.TextView r2 = r6.recordTime
            android.graphics.Typeface r5 = im.bclpbkiauv.messenger.AndroidUtilities.getTypeface(r13)
            r2.setTypeface(r5)
            android.widget.TextView r2 = r6.recordTime
            r2.setAlpha(r15)
            android.widget.TextView r2 = r6.recordTime
            r2.setTextColor(r14)
            android.widget.TextView r2 = r6.recordTime
            r5 = 1092616192(0x41200000, float:10.0)
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            r19 = 1084227584(0x40a00000, float:5.0)
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r19)
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r19)
            r2.setPadding(r12, r4, r5, r3)
            im.bclpbkiauv.ui.actionbar.BottomSheet$ContainerView r2 = r6.container
            android.widget.TextView r3 = r6.recordTime
            r25 = -1073741824(0xffffffffc0000000, float:-2.0)
            r26 = -1073741824(0xffffffffc0000000, float:-2.0)
            r27 = 49
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.statusBarHeight
            float r4 = (float) r4
            r31 = 0
            r29 = r4
            android.widget.FrameLayout$LayoutParams r4 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r2.addView(r3, r4)
            im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity$12 r2 = new im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity$12
            r2.<init>(r7)
            r6.cameraPanel = r2
            r3 = 8
            r2.setVisibility(r3)
            android.widget.FrameLayout r2 = r6.cameraPanel
            r2.setAlpha(r15)
            im.bclpbkiauv.ui.actionbar.BottomSheet$ContainerView r2 = r6.container
            android.widget.FrameLayout r4 = r6.cameraPanel
            r5 = 126(0x7e, float:1.77E-43)
            r12 = 83
            android.widget.FrameLayout$LayoutParams r5 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r14, (int) r5, (int) r12)
            r2.addView(r4, r5)
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r7)
            r6.counterTextView = r2
            r4 = 2131231443(0x7f0802d3, float:1.8078967E38)
            r2.setBackgroundResource(r4)
            android.widget.TextView r2 = r6.counterTextView
            r2.setVisibility(r3)
            android.widget.TextView r2 = r6.counterTextView
            r2.setTextColor(r14)
            android.widget.TextView r2 = r6.counterTextView
            r2.setGravity(r11)
            android.widget.TextView r2 = r6.counterTextView
            r2.setPivotX(r15)
            android.widget.TextView r2 = r6.counterTextView
            r2.setPivotY(r15)
            android.widget.TextView r2 = r6.counterTextView
            android.graphics.Typeface r4 = im.bclpbkiauv.messenger.AndroidUtilities.getTypeface(r13)
            r2.setTypeface(r4)
            android.widget.TextView r2 = r6.counterTextView
            r4 = 2131231441(0x7f0802d1, float:1.8078963E38)
            r2.setCompoundDrawablesWithIntrinsicBounds(r9, r9, r4, r9)
            android.widget.TextView r2 = r6.counterTextView
            r4 = 1082130432(0x40800000, float:4.0)
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
            r2.setCompoundDrawablePadding(r4)
            android.widget.TextView r2 = r6.counterTextView
            r4 = 1098907648(0x41800000, float:16.0)
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
            r2.setPadding(r5, r9, r4, r9)
            im.bclpbkiauv.ui.actionbar.BottomSheet$ContainerView r2 = r6.container
            android.widget.TextView r4 = r6.counterTextView
            r26 = 1108869120(0x42180000, float:38.0)
            r27 = 51
            r29 = 0
            r31 = 1122500608(0x42e80000, float:116.0)
            android.widget.FrameLayout$LayoutParams r5 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r2.addView(r4, r5)
            android.widget.TextView r2 = r6.counterTextView
            im.bclpbkiauv.ui.hui.friendscircle_v1.ui.-$$Lambda$ImagePreSelectorActivity$_dCBYcRLtOHc9ZQddo3W6XBB1x0 r4 = new im.bclpbkiauv.ui.hui.friendscircle_v1.ui.-$$Lambda$ImagePreSelectorActivity$_dCBYcRLtOHc9ZQddo3W6XBB1x0
            r4.<init>()
            r2.setOnClickListener(r4)
            im.bclpbkiauv.ui.components.ZoomControlView r2 = new im.bclpbkiauv.ui.components.ZoomControlView
            r2.<init>(r7)
            r6.zoomControlView = r2
            r2.setVisibility(r3)
            im.bclpbkiauv.ui.components.ZoomControlView r2 = r6.zoomControlView
            r2.setAlpha(r15)
            im.bclpbkiauv.ui.actionbar.BottomSheet$ContainerView r2 = r6.container
            im.bclpbkiauv.ui.components.ZoomControlView r3 = r6.zoomControlView
            r26 = 1112014848(0x42480000, float:50.0)
            android.widget.FrameLayout$LayoutParams r4 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r2.addView(r3, r4)
            im.bclpbkiauv.ui.components.ZoomControlView r2 = r6.zoomControlView
            im.bclpbkiauv.ui.hui.friendscircle_v1.ui.-$$Lambda$ImagePreSelectorActivity$Qu7xBsjHEg5ZVfejmg6rzS3HMUM r3 = new im.bclpbkiauv.ui.hui.friendscircle_v1.ui.-$$Lambda$ImagePreSelectorActivity$Qu7xBsjHEg5ZVfejmg6rzS3HMUM
            r3.<init>()
            r2.setDelegate(r3)
            im.bclpbkiauv.ui.components.ShutterButton r2 = new im.bclpbkiauv.ui.components.ShutterButton
            r2.<init>(r7)
            r6.shutterButton = r2
            android.widget.FrameLayout r3 = r6.cameraPanel
            r4 = 84
            r5 = 84
            android.widget.FrameLayout$LayoutParams r4 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r4, (int) r5, (int) r11)
            r3.addView(r2, r4)
            im.bclpbkiauv.ui.components.ShutterButton r2 = r6.shutterButton
            im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity$13 r3 = new im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity$13
            r3.<init>(r7)
            r2.setDelegate(r3)
            im.bclpbkiauv.ui.components.ShutterButton r2 = r6.shutterButton
            r2.setFocusable(r8)
            im.bclpbkiauv.ui.components.ShutterButton r2 = r6.shutterButton
            r3 = 2131689550(0x7f0f004e, float:1.9008119E38)
            java.lang.String r4 = "AccDescrShutter"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r3)
            r2.setContentDescription(r3)
            android.widget.ImageView r2 = new android.widget.ImageView
            r2.<init>(r7)
            r6.switchCameraButton = r2
            android.widget.ImageView$ScaleType r3 = android.widget.ImageView.ScaleType.CENTER
            r2.setScaleType(r3)
            android.widget.FrameLayout r2 = r6.cameraPanel
            android.widget.ImageView r3 = r6.switchCameraButton
            r4 = 48
            r5 = 21
            android.widget.FrameLayout$LayoutParams r5 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r4, (int) r4, (int) r5)
            r2.addView(r3, r5)
            android.widget.ImageView r2 = r6.switchCameraButton
            im.bclpbkiauv.ui.hui.friendscircle_v1.ui.-$$Lambda$ImagePreSelectorActivity$rtm7ynYqR1U06HLDbFpnZ2AtxIw r3 = new im.bclpbkiauv.ui.hui.friendscircle_v1.ui.-$$Lambda$ImagePreSelectorActivity$rtm7ynYqR1U06HLDbFpnZ2AtxIw
            r3.<init>()
            r2.setOnClickListener(r3)
            android.widget.ImageView r2 = r6.switchCameraButton
            r3 = 2131689554(0x7f0f0052, float:1.9008127E38)
            java.lang.String r5 = "AccDescrSwitchCamera"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r3)
            r2.setContentDescription(r3)
            r2 = 0
        L_0x0605:
            r3 = 2
            if (r2 >= r3) goto L_0x0658
            android.widget.ImageView[] r3 = r6.flashModeButton
            android.widget.ImageView r5 = new android.widget.ImageView
            r5.<init>(r7)
            r3[r2] = r5
            android.widget.ImageView[] r3 = r6.flashModeButton
            r3 = r3[r2]
            android.widget.ImageView$ScaleType r5 = android.widget.ImageView.ScaleType.CENTER
            r3.setScaleType(r5)
            android.widget.ImageView[] r3 = r6.flashModeButton
            r3 = r3[r2]
            r5 = 4
            r3.setVisibility(r5)
            android.widget.FrameLayout r3 = r6.cameraPanel
            android.widget.ImageView[] r5 = r6.flashModeButton
            r5 = r5[r2]
            r11 = 51
            android.widget.FrameLayout$LayoutParams r12 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r4, (int) r4, (int) r11)
            r3.addView(r5, r12)
            android.widget.ImageView[] r3 = r6.flashModeButton
            r3 = r3[r2]
            im.bclpbkiauv.ui.hui.friendscircle_v1.ui.-$$Lambda$ImagePreSelectorActivity$J8l3nwRlXEPjtObWcY6G36Vlwss r5 = new im.bclpbkiauv.ui.hui.friendscircle_v1.ui.-$$Lambda$ImagePreSelectorActivity$J8l3nwRlXEPjtObWcY6G36Vlwss
            r5.<init>()
            r3.setOnClickListener(r5)
            android.widget.ImageView[] r3 = r6.flashModeButton
            r3 = r3[r2]
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r12 = "flash mode "
            r5.append(r12)
            r5.append(r2)
            java.lang.String r5 = r5.toString()
            r3.setContentDescription(r5)
            int r2 = r2 + 1
            goto L_0x0605
        L_0x0658:
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r7)
            r6.tooltipTextView = r2
            r3 = 1097859072(0x41700000, float:15.0)
            r2.setTextSize(r8, r3)
            android.widget.TextView r2 = r6.tooltipTextView
            r2.setTextColor(r14)
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
            r2.setShadowLayer(r3, r15, r4, r5)
            android.widget.TextView r2 = r6.tooltipTextView
            r3 = 1086324736(0x40c00000, float:6.0)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            r4 = 1086324736(0x40c00000, float:6.0)
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
            r2.setPadding(r3, r9, r4, r9)
            android.widget.FrameLayout r2 = r6.cameraPanel
            android.widget.TextView r3 = r6.tooltipTextView
            r24 = -1073741824(0xffffffffc0000000, float:-2.0)
            r25 = -1073741824(0xffffffffc0000000, float:-2.0)
            r26 = 81
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 1098907648(0x41800000, float:16.0)
            android.widget.FrameLayout$LayoutParams r4 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r24, r25, r26, r27, r28, r29, r30)
            r2.addView(r3, r4)
            im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity$16 r2 = new im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity$16
            r2.<init>(r7)
            r6.cameraPhotoRecyclerView = r2
            r2.setVerticalScrollBarEnabled(r8)
            im.bclpbkiauv.ui.components.RecyclerListView r2 = r6.cameraPhotoRecyclerView
            im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity$PhotoAttachAdapter r3 = new im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity$PhotoAttachAdapter
            r3.<init>(r7, r9)
            r6.cameraAttachAdapter = r3
            r2.setAdapter(r3)
            im.bclpbkiauv.ui.components.RecyclerListView r2 = r6.cameraPhotoRecyclerView
            r2.setClipToPadding(r9)
            im.bclpbkiauv.ui.components.RecyclerListView r2 = r6.cameraPhotoRecyclerView
            r3 = 1090519040(0x41000000, float:8.0)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            r4 = 1090519040(0x41000000, float:8.0)
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
            r2.setPadding(r3, r9, r4, r9)
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
            r2.setAlpha(r15)
            im.bclpbkiauv.ui.actionbar.BottomSheet$ContainerView r2 = r6.container
            im.bclpbkiauv.ui.components.RecyclerListView r3 = r6.cameraPhotoRecyclerView
            r4 = 1117782016(0x42a00000, float:80.0)
            android.widget.FrameLayout$LayoutParams r4 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r14, r4)
            r2.addView(r3, r4)
            im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity$17 r2 = new im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity$17
            r2.<init>(r7, r9, r9)
            r6.cameraPhotoLayoutManager = r2
            im.bclpbkiauv.ui.components.RecyclerListView r3 = r6.cameraPhotoRecyclerView
            r3.setLayoutManager(r2)
            im.bclpbkiauv.ui.components.RecyclerListView r2 = r6.cameraPhotoRecyclerView
            im.bclpbkiauv.ui.hui.friendscircle_v1.ui.-$$Lambda$ImagePreSelectorActivity$4GYPOmPOkdZgrRQriRvQW0vxVwM r3 = im.bclpbkiauv.ui.hui.friendscircle_v1.ui.$$Lambda$ImagePreSelectorActivity$4GYPOmPOkdZgrRQriRvQW0vxVwM.INSTANCE
            r2.setOnItemClickListener((im.bclpbkiauv.ui.components.RecyclerListView.OnItemClickListener) r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity.<init>(android.app.Activity):void");
    }

    public /* synthetic */ void lambda$new$0$ImagePreSelectorActivity(View view, int position) {
        if (this.mediaEnabled && this.mActivity != null) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (position == 0 && this.noCameraPermissions) {
                    try {
                        this.mActivity.requestPermissions(new String[]{"android.permission.CAMERA"}, 18);
                        return;
                    } catch (Exception e) {
                        return;
                    }
                } else if (this.noGalleryPermissions) {
                    try {
                        this.mActivity.requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
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
                    MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) arrayList.get(position);
                    if (!selectedPhotos.isEmpty() || this.maxSelectedPhotos >= 9 || this.currentSelectMediaType != 1 || !photoEntry.path.endsWith(".gif")) {
                        int i = this.currentSelectMediaType;
                        if ((i == 1 || i == 3) && ((MediaController.PhotoEntry) arrayList.get(position)).isVideo) {
                            FcToastUtils.show((CharSequence) "不能同时选择图片跟视频");
                        } else if (this.currentSelectMediaType == 1 && ((MediaController.PhotoEntry) arrayList.get(position)).path.endsWith(".gif")) {
                            FcToastUtils.show((CharSequence) "不能同时选择图片跟Gif动图");
                        } else if (this.currentSelectMediaType != 3 || selectedPhotos.containsKey(Integer.valueOf(((MediaController.PhotoEntry) arrayList.get(position)).imageId))) {
                            ImagePreviewActivity.getInstance().setParentActivity(this.mActivity);
                            ImagePreviewActivity.getInstance().setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
                            ImagePreviewActivity.getInstance().setSelectPreviewMode(true);
                            ImagePreviewActivity.getInstance().setCurrentSelectMediaType(true, this.currentSelectMediaType);
                            ImagePreviewActivity.getInstance().openPhotoForSelect(arrayList, position, 0, this.photoViewerProvider, (ChatActivity) null);
                            AndroidUtilities.hideKeyboard(this.mActivity.getCurrentFocus());
                        } else if (((MediaController.PhotoEntry) arrayList.get(position)).path.endsWith(".gif")) {
                            FcToastUtils.show((CharSequence) "最多只能选择一张Gif动图");
                        } else {
                            FcToastUtils.show((CharSequence) "不能同时选择图片跟Gif动图");
                        }
                    } else {
                        FcToastUtils.show((CharSequence) "不能同时选择图片跟Gif动图");
                    }
                }
            } else if (!SharedConfig.inappCamera) {
                ChatAttachViewDelegate chatAttachViewDelegate = this.delegate;
                if (chatAttachViewDelegate != null) {
                    chatAttachViewDelegate.didPressedButton(0, false, true, 0);
                }
            } else if (this.maxSelectedPhotos < 0 || selectedPhotos.size() < this.maxSelectedPhotos) {
                openCamera(true);
            } else {
                XDialog.Builder builder = new XDialog.Builder(getContext());
                builder.setTitle(LocaleController.getString("image_select_tip", R.string.image_select_tip));
                builder.setMessage(LocaleController.formatString("image_select_max_warn", R.string.image_select_max_warn, Integer.valueOf(this.maxSelectedPhotos)));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
                builder.show();
            }
        }
    }

    public /* synthetic */ boolean lambda$new$1$ImagePreSelectorActivity(View view, int position) {
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

    public /* synthetic */ void lambda$new$2$ImagePreSelectorActivity(View view) {
        this.dropDownContainer.toggleSubMenu();
    }

    public /* synthetic */ void lambda$new$3$ImagePreSelectorActivity(View v) {
        sendPressed(true, 0);
    }

    public /* synthetic */ void lambda$new$4$ImagePreSelectorActivity(View v) {
        if (this.cameraView != null) {
            openPhotoViewer((MediaController.PhotoEntry) null, false, false);
            CameraController.getInstance().stopPreview(this.cameraView.getCameraSession());
        }
    }

    public /* synthetic */ void lambda$new$5$ImagePreSelectorActivity(float zoom) {
        CameraView cameraView2 = this.cameraView;
        if (cameraView2 != null) {
            this.cameraZoom = zoom;
            cameraView2.setZoom(zoom);
        }
        showZoomControls(true, true);
    }

    public /* synthetic */ void lambda$new$6$ImagePreSelectorActivity(View v) {
        CameraView cameraView2;
        if (!this.takingPhoto && (cameraView2 = this.cameraView) != null && cameraView2.isInitied()) {
            this.cameraView.switchCamera();
            ObjectAnimator animator = ObjectAnimator.ofFloat(this.switchCameraButton, View.SCALE_X, new float[]{0.0f}).setDuration(100);
            animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    ImagePreSelectorActivity.this.switchCameraButton.setImageResource((ImagePreSelectorActivity.this.cameraView == null || !ImagePreSelectorActivity.this.cameraView.isFrontface()) ? R.drawable.camera_revert2 : R.drawable.camera_revert1);
                    ObjectAnimator.ofFloat(ImagePreSelectorActivity.this.switchCameraButton, View.SCALE_X, new float[]{1.0f}).setDuration(100).start();
                }
            });
            animator.start();
        }
    }

    public /* synthetic */ void lambda$new$7$ImagePreSelectorActivity(final View currentImage) {
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
                        boolean unused = ImagePreSelectorActivity.this.flashAnimationInProgress = false;
                        currentImage.setVisibility(4);
                        nextImage.sendAccessibilityEvent(8);
                    }
                });
                animatorSet2.start();
            }
        }
    }

    static /* synthetic */ void lambda$new$8(View view, int position) {
        if (view instanceof PhotoAttachPhotoCell) {
            ((PhotoAttachPhotoCell) view).callDelegate();
        }
    }

    /* access modifiers changed from: private */
    public void stopRecord() {
        CameraView cameraView2;
        if (!this.takingPhoto && (cameraView2 = this.cameraView) != null && !this.mediaCaptured && cameraView2.getCameraSession() != null) {
            this.mediaCaptured = true;
            if (this.shutterButton.getState() == ShutterButton.State.RECORDING) {
                resetRecordState();
                CameraController.getInstance().stopVideoRecording(this.cameraView.getCameraSession(), false);
                this.shutterButton.setState(ShutterButton.State.DEFAULT, true);
                return;
            }
            File cameraFile = AndroidUtilities.generatePicturePath(false);
            boolean sameTakePictureOrientation = this.cameraView.getCameraSession().isSameTakePictureOrientation();
            this.cameraView.getCameraSession().setFlipFront(false);
            this.takingPhoto = CameraController.getInstance().takePicture(cameraFile, this.cameraView.getCameraSession(), new Runnable(cameraFile, sameTakePictureOrientation) {
                private final /* synthetic */ File f$1;
                private final /* synthetic */ boolean f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    ImagePreSelectorActivity.this.lambda$stopRecord$9$ImagePreSelectorActivity(this.f$1, this.f$2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$stopRecord$9$ImagePreSelectorActivity(File cameraFile, boolean sameTakePictureOrientation) {
        this.takingPhoto = false;
        if (cameraFile != null && this.mActivity != null) {
            int orientation = 0;
            try {
                int exif = new ExifInterface(cameraFile.getAbsolutePath()).getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
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
            mediaFromExternalCamera = false;
            int i = lastImageId;
            lastImageId = i - 1;
            MediaController.PhotoEntry photoEntry = new MediaController.PhotoEntry(0, i, 0, cameraFile.getAbsolutePath(), orientation, false);
            photoEntry.canDeleteAfter = true;
            openPhotoViewer(photoEntry, sameTakePictureOrientation, false);
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
                return;
            }
            this.maxSelectedPhotos = -1;
            this.allowOrder = true;
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

    private void sendPressed(boolean notify, int scheduleDate) {
        if (!this.buttonPressed) {
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

    /* access modifiers changed from: protected */
    public boolean onCustomOpenAnimation() {
        return false;
    }

    /* access modifiers changed from: private */
    public void openPhotoViewer(MediaController.PhotoEntry entry, final boolean sameTakePictureOrientation, boolean external) {
        if (entry != null) {
            cameraPhotos.add(entry);
            selectedPhotos.put(Integer.valueOf(entry.imageId), entry);
            selectedPhotosOrder.add(Integer.valueOf(entry.imageId));
            updatePhotosButton();
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
            ImagePreviewActivity.getInstance().setParentActivity(this.mActivity);
            ImagePreviewActivity.getInstance().setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
            ImagePreviewActivity.getInstance().setSelectPreviewMode(true);
            ImagePreviewActivity.getInstance().setCurrentSelectMediaType(true, this.currentSelectMediaType);
            ImagePreviewActivity.getInstance().openPhotoForSelect(getAllPhotosArray(), cameraPhotos.size() - 1, 5, new BasePhotoProvider() {
                public ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index) {
                    return null;
                }

                public boolean cancelButtonPressed() {
                    if (ImagePreSelectorActivity.this.cameraOpened && ImagePreSelectorActivity.this.cameraView != null) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public final void run() {
                                ImagePreSelectorActivity.AnonymousClass18.this.lambda$cancelButtonPressed$0$ImagePreSelectorActivity$18();
                            }
                        }, 1000);
                        ImagePreSelectorActivity.this.zoomControlView.setZoom(0.0f, false);
                        float unused = ImagePreSelectorActivity.this.cameraZoom = 0.0f;
                        ImagePreSelectorActivity.this.cameraView.setZoom(0.0f);
                        CameraController.getInstance().startPreview(ImagePreSelectorActivity.this.cameraView.getCameraSession());
                    }
                    if (ImagePreSelectorActivity.this.cancelTakingPhotos && ImagePreSelectorActivity.cameraPhotos.size() == 1) {
                        int size = ImagePreSelectorActivity.cameraPhotos.size();
                        for (int a = 0; a < size; a++) {
                            MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) ImagePreSelectorActivity.cameraPhotos.get(a);
                            new File(photoEntry.path).delete();
                            if (photoEntry.imagePath != null) {
                                new File(photoEntry.imagePath).delete();
                            }
                            if (photoEntry.thumbPath != null) {
                                new File(photoEntry.thumbPath).delete();
                            }
                        }
                        ImagePreSelectorActivity.cameraPhotos.clear();
                        ImagePreSelectorActivity.selectedPhotosOrder.clear();
                        ImagePreSelectorActivity.selectedPhotos.clear();
                        ImagePreSelectorActivity.this.counterTextView.setVisibility(4);
                        ImagePreSelectorActivity.this.cameraPhotoRecyclerView.setVisibility(8);
                        ImagePreSelectorActivity.this.adapter.notifyDataSetChanged();
                        ImagePreSelectorActivity.this.cameraAttachAdapter.notifyDataSetChanged();
                        ImagePreSelectorActivity.this.updatePhotosButton();
                    }
                    return true;
                }

                public /* synthetic */ void lambda$cancelButtonPressed$0$ImagePreSelectorActivity$18() {
                    if (ImagePreSelectorActivity.this.cameraView != null && !ImagePreSelectorActivity.this.isDismissed() && Build.VERSION.SDK_INT >= 21) {
                        ImagePreSelectorActivity.this.cameraView.setSystemUiVisibility(1028);
                    }
                }

                public void needAddMorePhotos() {
                    boolean unused = ImagePreSelectorActivity.this.cancelTakingPhotos = false;
                    if (ImagePreSelectorActivity.mediaFromExternalCamera) {
                        ImagePreSelectorActivity.this.delegate.didPressedButton(0, true, true, 0);
                        return;
                    }
                    if (!ImagePreSelectorActivity.this.cameraOpened) {
                        ImagePreSelectorActivity.this.openCamera(false);
                    }
                    ImagePreSelectorActivity.this.counterTextView.setVisibility(0);
                    ImagePreSelectorActivity.this.cameraPhotoRecyclerView.setVisibility(0);
                    ImagePreSelectorActivity.this.counterTextView.setAlpha(1.0f);
                    ImagePreSelectorActivity.this.updatePhotosCounter(false);
                }

                public void sendButtonPressed(int index, VideoEditedInfo videoEditedInfo, boolean notify, int scheduleDate) {
                    if (!ImagePreSelectorActivity.cameraPhotos.isEmpty() && ImagePreSelectorActivity.this.mActivity != null) {
                        if (videoEditedInfo != null && index >= 0 && index < ImagePreSelectorActivity.cameraPhotos.size()) {
                            MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) ImagePreSelectorActivity.cameraPhotos.get(index);
                            photoEntry.editedInfo = videoEditedInfo;
                            if (photoEntry.path.endsWith(".gif")) {
                                int unused = ImagePreSelectorActivity.this.currentSelectMediaType = 3;
                            } else if (photoEntry.isVideo) {
                                int unused2 = ImagePreSelectorActivity.this.currentSelectMediaType = 2;
                            } else {
                                int unused3 = ImagePreSelectorActivity.this.currentSelectMediaType = 1;
                            }
                        }
                        int size = ImagePreSelectorActivity.cameraPhotos.size();
                        for (int a = 0; a < size; a++) {
                            AndroidUtilities.addMediaToGallery(((MediaController.PhotoEntry) ImagePreSelectorActivity.cameraPhotos.get(a)).path);
                        }
                        ImagePreSelectorActivity.this.applyCaption();
                        ImagePreSelectorActivity.this.delegate.didPressedButton(8, true, notify, scheduleDate);
                        ImagePreSelectorActivity.cameraPhotos.clear();
                        ImagePreSelectorActivity.selectedPhotosOrder.clear();
                        ImagePreSelectorActivity.selectedPhotos.clear();
                        ImagePreSelectorActivity.this.adapter.notifyDataSetChanged();
                        ImagePreSelectorActivity.this.cameraAttachAdapter.notifyDataSetChanged();
                        ImagePreSelectorActivity.this.closeCamera(false);
                        ImagePreSelectorActivity.this.dismiss();
                    }
                }

                public boolean scaleToFill() {
                    if (ImagePreSelectorActivity.this.mActivity == null) {
                        return false;
                    }
                    int locked = Settings.System.getInt(ImagePreSelectorActivity.this.mActivity.getContentResolver(), "accelerometer_rotation", 0);
                    if (sameTakePictureOrientation || locked == 1) {
                        return true;
                    }
                    return false;
                }

                public void willHidePhotoViewer() {
                    boolean unused = ImagePreSelectorActivity.this.mediaCaptured = false;
                    int count = ImagePreSelectorActivity.this.gridView.getChildCount();
                    for (int a = 0; a < count; a++) {
                        View view = ImagePreSelectorActivity.this.gridView.getChildAt(a);
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
                    return ImagePreSelectorActivity.this.maxSelectedPhotos != 1;
                }
            }, (ChatActivity) null);
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
                    AnimatorSet unused = ImagePreSelectorActivity.this.zoomControlAnimation = null;
                }
            });
            this.zoomControlAnimation.start();
            if (show) {
                $$Lambda$ImagePreSelectorActivity$bXOiBpocXn5qegZFBQJdAJdHmDI r0 = new Runnable() {
                    public final void run() {
                        ImagePreSelectorActivity.this.lambda$showZoomControls$11$ImagePreSelectorActivity();
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
            $$Lambda$ImagePreSelectorActivity$RVccCLalnOeesSZxSfYbcRbx7Ps r02 = new Runnable() {
                public final void run() {
                    ImagePreSelectorActivity.this.lambda$showZoomControls$10$ImagePreSelectorActivity();
                }
            };
            this.zoomControlHideRunnable = r02;
            AndroidUtilities.runOnUIThread(r02, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        }
    }

    public /* synthetic */ void lambda$showZoomControls$10$ImagePreSelectorActivity() {
        showZoomControls(false, true);
        this.zoomControlHideRunnable = null;
    }

    public /* synthetic */ void lambda$showZoomControls$11$ImagePreSelectorActivity() {
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

    public void checkColors() {
        this.selectedTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.commentTextView.updateColors();
        Theme.setSelectorDrawableColor(this.writeButtonDrawable, Theme.getColor(Theme.key_dialogFloatingButton), false);
        Theme.setSelectorDrawableColor(this.writeButtonDrawable, Theme.getColor(Theme.key_dialogFloatingButtonPressed), true);
        this.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogFloatingIcon), PorterDuff.Mode.MULTIPLY));
        this.dropDown.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.dropDownContainer.setPopupItemsColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem), false);
        this.dropDownContainer.setPopupItemsColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem), true);
        this.dropDownContainer.redrawPopup(Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground));
        this.actionBarShadow.setBackgroundColor(Theme.getColor(Theme.key_dialogShadowLine));
        this.progressView.setTextColor(Theme.getColor(Theme.key_emptyListPlaceholder));
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

    /* access modifiers changed from: private */
    public void resetRecordState() {
        if (this.mActivity != null) {
            for (int a = 0; a < 2; a++) {
                this.flashModeButton[a].setAlpha(1.0f);
            }
            this.switchCameraButton.setAlpha(1.0f);
            this.tooltipTextView.setAlpha(1.0f);
            this.recordTime.setAlpha(0.0f);
            AndroidUtilities.cancelRunOnUIThread(this.videoRecordRunnable);
            this.videoRecordRunnable = null;
            AndroidUtilities.unlockOrientation(this.mActivity);
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
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.friendscircle_v1.ui.ImagePreSelectorActivity.setCameraFlashModeIcon(android.widget.ImageView, java.lang.String):void");
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
            FrameLayout frameLayout2 = this.cameraPanel;
            if (view == frameLayout2) {
                if (isPortrait) {
                    frameLayout2.measure(View.MeasureSpec.makeMeasureSpec(width, 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(126.0f), 1073741824));
                } else {
                    frameLayout2.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(126.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(height, 1073741824));
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
                        boolean unused = ImagePreSelectorActivity.this.cameraAnimationInProgress = false;
                        if (Build.VERSION.SDK_INT >= 21 && ImagePreSelectorActivity.this.cameraView != null) {
                            ImagePreSelectorActivity.this.cameraView.invalidateOutline();
                        }
                        if (ImagePreSelectorActivity.this.cameraOpened) {
                            ImagePreSelectorActivity.this.delegate.onCameraOpened();
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
        if (this.mActivity != null) {
            mediaFromExternalCamera = true;
            if (i == 0) {
                ImagePreviewActivity.getInstance().setParentActivity(this.mActivity);
                ImagePreviewActivity.getInstance().setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
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
                    AndroidUtilities.addMediaToGallery(currentPicturePath);
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
                        boolean unused = ImagePreSelectorActivity.this.cameraAnimationInProgress = false;
                        if (Build.VERSION.SDK_INT >= 21 && ImagePreSelectorActivity.this.cameraView != null) {
                            ImagePreSelectorActivity.this.cameraView.invalidateOutline();
                        }
                        boolean unused2 = ImagePreSelectorActivity.this.cameraOpened = false;
                        if (ImagePreSelectorActivity.this.cameraPanel != null) {
                            ImagePreSelectorActivity.this.cameraPanel.setVisibility(8);
                        }
                        if (ImagePreSelectorActivity.this.zoomControlView != null) {
                            ImagePreSelectorActivity.this.zoomControlView.setVisibility(8);
                            ImagePreSelectorActivity.this.zoomControlView.setTag((Object) null);
                        }
                        if (ImagePreSelectorActivity.this.cameraPhotoRecyclerView != null) {
                            ImagePreSelectorActivity.this.cameraPhotoRecyclerView.setVisibility(8);
                        }
                        if (Build.VERSION.SDK_INT >= 21 && ImagePreSelectorActivity.this.cameraView != null) {
                            ImagePreSelectorActivity.this.cameraView.setSystemUiVisibility(1024);
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
                    int maxY2 = (int) (((float) containerHeight) + this.containerView.getTranslationY());
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
                            ImagePreSelectorActivity.this.lambda$applyCameraViewPosition$12$ImagePreSelectorActivity(this.f$1);
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
                        ImagePreSelectorActivity.this.lambda$applyCameraViewPosition$13$ImagePreSelectorActivity(this.f$1);
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$applyCameraViewPosition$12$ImagePreSelectorActivity(FrameLayout.LayoutParams layoutParamsFinal) {
        CameraView cameraView2 = this.cameraView;
        if (cameraView2 != null) {
            cameraView2.setLayoutParams(layoutParamsFinal);
        }
    }

    public /* synthetic */ void lambda$applyCameraViewPosition$13$ImagePreSelectorActivity(FrameLayout.LayoutParams layoutParamsFinal) {
        FrameLayout frameLayout = this.cameraIcon;
        if (frameLayout != null) {
            frameLayout.setLayoutParams(layoutParamsFinal);
        }
    }

    public void showCamera() {
        if (!this.paused && this.mediaEnabled) {
            if (this.cameraView == null) {
                CameraView cameraView2 = new CameraView(this.mActivity, this.openWithFrontFaceCamera);
                this.cameraView = cameraView2;
                cameraView2.setFocusable(true);
                if (Build.VERSION.SDK_INT >= 21) {
                    this.cameraView.setOutlineProvider(new ViewOutlineProvider() {
                        public void getOutline(View view, Outline outline) {
                            if (ImagePreSelectorActivity.this.cameraAnimationInProgress) {
                                int rad = AndroidUtilities.dp(ImagePreSelectorActivity.this.cornerRadius * 8.0f * ImagePreSelectorActivity.this.cameraOpenProgress);
                                outline.setRoundRect(0, 0, view.getMeasuredWidth() + rad, view.getMeasuredHeight() + rad, (float) rad);
                            } else if (ImagePreSelectorActivity.this.cameraAnimationInProgress || ImagePreSelectorActivity.this.cameraOpened) {
                                outline.setRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                            } else {
                                int rad2 = AndroidUtilities.dp(ImagePreSelectorActivity.this.cornerRadius * 8.0f);
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
                        if (ImagePreSelectorActivity.this.cameraView.getCameraSession().getCurrentFlashMode().equals(ImagePreSelectorActivity.this.cameraView.getCameraSession().getNextFlashMode())) {
                            for (int a = 0; a < 2; a++) {
                                ImagePreSelectorActivity.this.flashModeButton[a].setVisibility(4);
                                ImagePreSelectorActivity.this.flashModeButton[a].setAlpha(0.0f);
                                ImagePreSelectorActivity.this.flashModeButton[a].setTranslationY(0.0f);
                            }
                        } else {
                            ImagePreSelectorActivity imagePreSelectorActivity = ImagePreSelectorActivity.this;
                            imagePreSelectorActivity.setCameraFlashModeIcon(imagePreSelectorActivity.flashModeButton[0], ImagePreSelectorActivity.this.cameraView.getCameraSession().getCurrentFlashMode());
                            int a2 = 0;
                            while (a2 < 2) {
                                ImagePreSelectorActivity.this.flashModeButton[a2].setVisibility(a2 == 0 ? 0 : 4);
                                ImagePreSelectorActivity.this.flashModeButton[a2].setAlpha((a2 != 0 || !ImagePreSelectorActivity.this.cameraOpened) ? 0.0f : 1.0f);
                                ImagePreSelectorActivity.this.flashModeButton[a2].setTranslationY(0.0f);
                                a2++;
                            }
                        }
                        ImagePreSelectorActivity.this.switchCameraButton.setImageResource(ImagePreSelectorActivity.this.cameraView.isFrontface() ? R.drawable.camera_revert1 : R.drawable.camera_revert2);
                        ImageView access$6300 = ImagePreSelectorActivity.this.switchCameraButton;
                        if (ImagePreSelectorActivity.this.cameraView.hasFrontFaceCamera()) {
                            i = 0;
                        }
                        access$6300.setVisibility(i);
                        if (!ImagePreSelectorActivity.this.cameraOpened) {
                            AnimatorSet unused = ImagePreSelectorActivity.this.cameraInitAnimation = new AnimatorSet();
                            ImagePreSelectorActivity.this.cameraInitAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(ImagePreSelectorActivity.this.cameraView, View.ALPHA, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(ImagePreSelectorActivity.this.cameraIcon, View.ALPHA, new float[]{0.0f, 1.0f})});
                            ImagePreSelectorActivity.this.cameraInitAnimation.setDuration(180);
                            ImagePreSelectorActivity.this.cameraInitAnimation.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animation) {
                                    if (animation.equals(ImagePreSelectorActivity.this.cameraInitAnimation)) {
                                        AnimatorSet unused = ImagePreSelectorActivity.this.cameraInitAnimation = null;
                                        int count = ImagePreSelectorActivity.this.gridView.getChildCount();
                                        for (int a = 0; a < count; a++) {
                                            View child = ImagePreSelectorActivity.this.gridView.getChildAt(a);
                                            if (child instanceof PhotoAttachCameraCell) {
                                                child.setVisibility(4);
                                                return;
                                            }
                                        }
                                    }
                                }

                                public void onAnimationCancel(Animator animation) {
                                    AnimatorSet unused = ImagePreSelectorActivity.this.cameraInitAnimation = null;
                                }
                            });
                            ImagePreSelectorActivity.this.cameraInitAnimation.start();
                        }
                    }
                });
                if (this.cameraIcon == null) {
                    AnonymousClass24 r0 = new FrameLayout(this.mActivity) {
                        /* access modifiers changed from: protected */
                        public void onDraw(Canvas canvas) {
                            int w = ImagePreSelectorActivity.this.cameraDrawable.getIntrinsicWidth();
                            int h = ImagePreSelectorActivity.this.cameraDrawable.getIntrinsicHeight();
                            int x = (ImagePreSelectorActivity.this.itemSize - w) / 2;
                            int y = (ImagePreSelectorActivity.this.itemSize - h) / 2;
                            if (ImagePreSelectorActivity.this.cameraViewOffsetY != 0) {
                                y -= ImagePreSelectorActivity.this.cameraViewOffsetY;
                            }
                            ImagePreSelectorActivity.this.cameraDrawable.setBounds(x, y, x + w, y + h);
                            ImagePreSelectorActivity.this.cameraDrawable.draw(canvas);
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
                FrameLayout frameLayout2 = this.cameraIcon;
                if (!this.mediaEnabled) {
                    f = 0.2f;
                }
                frameLayout2.setAlpha(f);
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
        Bitmap bitmap = null;
        FileOutputStream stream = null;
        try {
            Bitmap bitmap2 = this.cameraView.getTextureView().getBitmap();
            if (bitmap2 != null) {
                Bitmap newBitmap = Bitmap.createBitmap(bitmap2, 0, 0, bitmap2.getWidth(), bitmap2.getHeight(), this.cameraView.getMatrix(), true);
                bitmap2.recycle();
                bitmap2 = newBitmap;
                Bitmap lastBitmap = Bitmap.createScaledBitmap(bitmap2, 80, (int) (((float) bitmap2.getHeight()) / (((float) bitmap2.getWidth()) / 80.0f)), true);
                if (lastBitmap != null) {
                    if (lastBitmap != bitmap2) {
                        bitmap2.recycle();
                    }
                    Utilities.blurBitmap(lastBitmap, 7, 1, lastBitmap.getWidth(), lastBitmap.getHeight(), lastBitmap.getRowBytes());
                    stream = new FileOutputStream(new File(ApplicationLoader.getFilesDirFixed(), "cthumb.jpg"));
                    lastBitmap.compress(Bitmap.CompressFormat.JPEG, 87, stream);
                    lastBitmap.recycle();
                }
            }
            if (bitmap2 != null) {
                bitmap2.recycle();
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Throwable th) {
            if (bitmap != null) {
                bitmap.recycle();
            }
            if (stream != null) {
                stream.close();
            }
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.albumsDidLoad) {
            if (this.adapter != null) {
                MediaController.AlbumEntry albumEntry = MediaController.allMediaAlbumEntry;
                this.galleryAlbumEntry = albumEntry;
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
                    this.selectedAlbumEntry = albumEntry;
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
        } else if (id == NotificationCenter.cameraInitied) {
            checkCamera(false);
        }
    }

    private void updateAlbumsDropDown() {
        this.dropDownContainer.removeAllSubItems();
        if (this.mediaEnabled) {
            ArrayList<MediaController.AlbumEntry> albums = MediaController.allMediaAlbums;
            ArrayList<MediaController.AlbumEntry> arrayList = new ArrayList<>(albums);
            this.dropDownAlbums = arrayList;
            Collections.sort(arrayList, new Comparator(albums) {
                private final /* synthetic */ ArrayList f$0;

                {
                    this.f$0 = r1;
                }

                public final int compare(Object obj, Object obj2) {
                    return ImagePreSelectorActivity.lambda$updateAlbumsDropDown$14(this.f$0, (MediaController.AlbumEntry) obj, (MediaController.AlbumEntry) obj2);
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

    static /* synthetic */ int lambda$updateAlbumsDropDown$14(ArrayList albums, MediaController.AlbumEntry o1, MediaController.AlbumEntry o2) {
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

    private void updateSelectedPosition() {
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
        float offset = 0.0f;
        if (this.actionBar.getAlpha() == 0.0f) {
            offset = (float) AndroidUtilities.dp(26.0f);
        }
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
                    AnimatorSet unused = ImagePreSelectorActivity.this.actionBarAnimation = null;
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

    public void updatePhotosButton() {
        String str;
        int i;
        int max = Math.max(0, selectedPhotosOrder.size());
        ActionBarMenuItem actionBarMenuItem = this.isSkipMenu;
        if (actionBarMenuItem != null) {
            if (max == 0) {
                i = R.string.fc_skip;
                str = "fc_skip";
            } else {
                i = R.string.fc_next;
                str = "fc_next";
            }
            actionBarMenuItem.setText(LocaleController.getString(str, i));
        }
    }

    public void setDelegate(ChatAttachViewDelegate chatAttachViewDelegate) {
        this.delegate = chatAttachViewDelegate;
    }

    public void loadGalleryPhotos() {
        if (MediaController.allMediaAlbumEntry == null && Build.VERSION.SDK_INT >= 21) {
            MediaController.loadGalleryPhotosAlbums(0);
        }
    }

    public void init() {
        this.galleryAlbumEntry = MediaController.allMediaAlbumEntry;
        this.commentTextView.setVisibility(4);
        if (Build.VERSION.SDK_INT >= 23) {
            this.noGalleryPermissions = this.mActivity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0;
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
        this.dropDown.setText(LocaleController.getString("AllMedia", R.string.AllMedia));
        clearSelectedPhotos();
        updatePhotosCounter(false);
        this.commentTextView.setText("");
        this.cameraPhotoLayoutManager.scrollToPositionWithOffset(0, EditInputFilter.MAX_VALUE);
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
        this.mActivity = null;
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
            boolean z = this.mActivity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0;
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
        if (this.mActivity != null) {
            boolean old = this.deviceHasGoodCamera;
            boolean old2 = this.noCameraPermissions;
            if (!SharedConfig.inappCamera) {
                this.deviceHasGoodCamera = false;
            } else if (Build.VERSION.SDK_INT >= 23) {
                try {
                    boolean z = this.mActivity.checkSelfPermission("android.permission.CAMERA") != 0;
                    this.noCameraPermissions = z;
                    if (z) {
                        if (request) {
                            try {
                                this.mActivity.requestPermissions(new String[]{"android.permission.CAMERA"}, 17);
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
                } catch (Exception e2) {
                    e2.printStackTrace();
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
            if (isShowing() && this.deviceHasGoodCamera && this.mActivity != null && this.backDrawable.getAlpha() != 0 && !this.cameraOpened) {
                showCamera();
            }
        }
    }

    public void onOpenAnimationEnd() {
        NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(false);
        MediaController.AlbumEntry albumEntry = MediaController.allMediaAlbumEntry;
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
        updatePhotosButton();
        this.adapter.notifyDataSetChanged();
        this.cameraAttachAdapter.notifyDataSetChanged();
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
            cell.setNewStyle(true);
            if (Build.VERSION.SDK_INT >= 21 && this == ImagePreSelectorActivity.this.adapter) {
                cell.setOutlineProvider(new ViewOutlineProvider() {
                    public void getOutline(View view, Outline outline) {
                        int position = ((Integer) ((PhotoAttachPhotoCell) view).getTag()).intValue();
                        if (PhotoAttachAdapter.this.needCamera && ImagePreSelectorActivity.this.selectedAlbumEntry == ImagePreSelectorActivity.this.galleryAlbumEntry) {
                            position++;
                        }
                        if (position == 0) {
                            int rad = AndroidUtilities.dp(ImagePreSelectorActivity.this.cornerRadius * 8.0f);
                            outline.setRoundRect(0, 0, view.getMeasuredWidth() + rad, view.getMeasuredHeight() + rad, (float) rad);
                        } else if (position == ImagePreSelectorActivity.this.itemsPerRow - 1) {
                            int rad2 = AndroidUtilities.dp(ImagePreSelectorActivity.this.cornerRadius * 8.0f);
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
                    ImagePreSelectorActivity.PhotoAttachAdapter.this.lambda$createHolder$0$ImagePreSelectorActivity$PhotoAttachAdapter(photoAttachPhotoCell);
                }
            });
            return new RecyclerListView.Holder(cell);
        }

        public /* synthetic */ void lambda$createHolder$0$ImagePreSelectorActivity$PhotoAttachAdapter(PhotoAttachPhotoCell v) {
            if (ImagePreSelectorActivity.this.mediaEnabled) {
                int index = ((Integer) v.getTag()).intValue();
                MediaController.PhotoEntry photoEntry = v.getPhotoEntry();
                if (!photoEntry.isVideo) {
                    if (ImagePreSelectorActivity.selectedPhotos.isEmpty()) {
                        if (ImagePreSelectorActivity.this.maxSelectedPhotos < 9 && ImagePreSelectorActivity.this.currentSelectMediaType == 1 && photoEntry.path.endsWith(".gif")) {
                            FcToastUtils.show((CharSequence) "不能同时选择图片跟Gif动图");
                            return;
                        } else if (photoEntry.path.endsWith(".gif")) {
                            int unused = ImagePreSelectorActivity.this.currentSelectMediaType = 3;
                        } else {
                            int unused2 = ImagePreSelectorActivity.this.currentSelectMediaType = 1;
                        }
                    } else if (ImagePreSelectorActivity.this.currentSelectMediaType == 3) {
                        if (ImagePreSelectorActivity.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId))) {
                            int unused3 = ImagePreSelectorActivity.this.currentSelectMediaType = 0;
                        } else if (photoEntry.path.endsWith(".gif")) {
                            FcToastUtils.show((CharSequence) "最多只能选择一张Gif动图");
                            return;
                        } else {
                            FcToastUtils.show((CharSequence) "不能同时选择图片跟Gif动图");
                            return;
                        }
                    } else if (ImagePreSelectorActivity.this.currentSelectMediaType == 1) {
                        if (ImagePreSelectorActivity.this.maxSelectedPhotos == 9 && ImagePreSelectorActivity.selectedPhotos.size() == 1 && ImagePreSelectorActivity.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId))) {
                            int unused4 = ImagePreSelectorActivity.this.currentSelectMediaType = 0;
                        } else if (photoEntry.path.endsWith(".gif")) {
                            FcToastUtils.show((CharSequence) "不能同时选择图片跟Gif动图");
                            return;
                        }
                    }
                    boolean added = !ImagePreSelectorActivity.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId));
                    if (!added || ImagePreSelectorActivity.this.maxSelectedPhotos < 0 || ImagePreSelectorActivity.selectedPhotos.size() < ImagePreSelectorActivity.this.maxSelectedPhotos) {
                        int num = added ? ImagePreSelectorActivity.selectedPhotosOrder.size() : -1;
                        if (ImagePreSelectorActivity.this.allowOrder) {
                            v.setChecked(num, added, true);
                        } else {
                            v.setChecked(-1, added, true);
                        }
                        int unused5 = ImagePreSelectorActivity.this.addToSelectedPhotos(photoEntry, index);
                        int updateIndex = index;
                        if (this == ImagePreSelectorActivity.this.cameraAttachAdapter) {
                            if (ImagePreSelectorActivity.this.adapter.needCamera && ImagePreSelectorActivity.this.selectedAlbumEntry == ImagePreSelectorActivity.this.galleryAlbumEntry) {
                                updateIndex++;
                            }
                            ImagePreSelectorActivity.this.adapter.notifyItemChanged(updateIndex);
                        } else {
                            ImagePreSelectorActivity.this.cameraAttachAdapter.notifyItemChanged(updateIndex);
                        }
                        ImagePreSelectorActivity.this.updatePhotosButton();
                        return;
                    }
                    XDialog.Builder builder = new XDialog.Builder(this.mContext);
                    builder.setTitle(LocaleController.getString("image_select_tip", R.string.image_select_tip));
                    builder.setMessage(LocaleController.formatString("image_select_max_warn", R.string.image_select_max_warn, Integer.valueOf(ImagePreSelectorActivity.this.maxSelectedPhotos)));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
                    builder.show();
                } else if (ImagePreSelectorActivity.selectedPhotos != null && ImagePreSelectorActivity.selectedPhotos.size() == 0) {
                    int unused6 = ImagePreSelectorActivity.this.currentSelectMediaType = 2;
                }
            }
        }

        /* access modifiers changed from: private */
        public MediaController.PhotoEntry getPhoto(int position) {
            if (this.needCamera && ImagePreSelectorActivity.this.selectedAlbumEntry == ImagePreSelectorActivity.this.galleryAlbumEntry) {
                position--;
            }
            return ImagePreSelectorActivity.this.getPhotoEntryAtPosition(position);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int itemViewType = holder.getItemViewType();
            int i = 0;
            boolean z = true;
            if (itemViewType == 0) {
                if (this.needCamera && ImagePreSelectorActivity.this.selectedAlbumEntry == ImagePreSelectorActivity.this.galleryAlbumEntry) {
                    position--;
                }
                PhotoAttachPhotoCell cell = (PhotoAttachPhotoCell) holder.itemView;
                if (this == ImagePreSelectorActivity.this.adapter) {
                    cell.setItemSize(ImagePreSelectorActivity.this.itemSize);
                } else {
                    cell.setIsVertical(ImagePreSelectorActivity.this.cameraPhotoLayoutManager.getOrientation() == 1);
                }
                MediaController.PhotoEntry photoEntry = ImagePreSelectorActivity.this.getPhotoEntryAtPosition(position);
                boolean z2 = this.needCamera && ImagePreSelectorActivity.this.selectedAlbumEntry == ImagePreSelectorActivity.this.galleryAlbumEntry;
                if (position != getItemCount() - 1) {
                    z = false;
                }
                cell.setPhotoEntry(photoEntry, z2, z);
                cell.setChecked(-1, ImagePreSelectorActivity.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId)), false);
                cell.getImageView().setTag(Integer.valueOf(position));
                cell.setTag(Integer.valueOf(position));
                if (photoEntry.isVideo) {
                    cell.getCheckBox().setVisibility(8);
                } else {
                    cell.getCheckBox().setVisibility(0);
                }
            } else if (itemViewType == 1) {
                PhotoAttachCameraCell cameraCell = (PhotoAttachCameraCell) holder.itemView;
                if (ImagePreSelectorActivity.this.cameraView == null || !ImagePreSelectorActivity.this.cameraView.isInitied()) {
                    cameraCell.setVisibility(0);
                } else {
                    cameraCell.setVisibility(4);
                }
                cameraCell.setItemSize(ImagePreSelectorActivity.this.itemSize);
            } else if (itemViewType == 3) {
                PhotoAttachPermissionCell cell2 = (PhotoAttachPermissionCell) holder.itemView;
                cell2.setItemSize(ImagePreSelectorActivity.this.itemSize);
                if (!this.needCamera || !ImagePreSelectorActivity.this.noCameraPermissions || position != 0) {
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
                                int rad = AndroidUtilities.dp(ImagePreSelectorActivity.this.cornerRadius * 8.0f);
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
                            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(ImagePreSelectorActivity.this.gridExtraSpace, 1073741824));
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
            if (!ImagePreSelectorActivity.this.mediaEnabled) {
                return 1;
            }
            int count = 0;
            if (this.needCamera && ImagePreSelectorActivity.this.selectedAlbumEntry == ImagePreSelectorActivity.this.galleryAlbumEntry) {
                count = 0 + 1;
            }
            if (ImagePreSelectorActivity.this.noGalleryPermissions && this == ImagePreSelectorActivity.this.adapter) {
                count++;
            }
            int count2 = count + ImagePreSelectorActivity.cameraPhotos.size();
            if (ImagePreSelectorActivity.this.selectedAlbumEntry != null) {
                count2 += ImagePreSelectorActivity.this.selectedAlbumEntry.photos.size();
            }
            if (this == ImagePreSelectorActivity.this.adapter) {
                count2++;
            }
            this.itemsCount = count2;
            return count2;
        }

        public int getItemViewType(int position) {
            if (!ImagePreSelectorActivity.this.mediaEnabled) {
                return 2;
            }
            if (this.needCamera && position == 0 && ImagePreSelectorActivity.this.selectedAlbumEntry == ImagePreSelectorActivity.this.galleryAlbumEntry) {
                return ImagePreSelectorActivity.this.noCameraPermissions ? 3 : 1;
            }
            if (this == ImagePreSelectorActivity.this.adapter && position == this.itemsCount - 1) {
                return 2;
            }
            if (ImagePreSelectorActivity.this.noGalleryPermissions) {
                return 3;
            }
            return 0;
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            if (this == ImagePreSelectorActivity.this.adapter) {
                ImagePreSelectorActivity.this.progressView.setVisibility((!(getItemCount() == 1 && ImagePreSelectorActivity.this.selectedAlbumEntry == null) && ImagePreSelectorActivity.this.mediaEnabled) ? 4 : 0);
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

    public void setCurrentSelectMediaType(int currentSelectMediaType2) {
        this.currentSelectMediaType = currentSelectMediaType2;
    }

    public int getCurrentSelectMediaType() {
        return this.currentSelectMediaType;
    }

    public ArrayList<Object> getCameraPhotos() {
        return cameraPhotos;
    }

    public MediaController.AlbumEntry getSelectedAlbumEntry() {
        return this.selectedAlbumEntry;
    }

    public MediaController.AlbumEntry getGalleryAlbumEntry() {
        return this.galleryAlbumEntry;
    }

    public int getCurrentSelectedCount() {
        return this.currentSelectedCount;
    }
}
