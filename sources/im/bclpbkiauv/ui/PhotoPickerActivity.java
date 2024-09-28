package im.bclpbkiauv.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.king.zxing.util.LogUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ChatObject;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.ImageReceiver;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.messenger.VideoEditedInfo;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.PhotoPickerActivity;
import im.bclpbkiauv.ui.PhotoViewer;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenu;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuSubItem;
import im.bclpbkiauv.ui.actionbar.ActionBarPopupWindow;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.PhotoAttachPhotoCell;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.CheckBox2;
import im.bclpbkiauv.ui.components.CombinedDrawable;
import im.bclpbkiauv.ui.components.EditTextBoldCursor;
import im.bclpbkiauv.ui.components.EditTextEmoji;
import im.bclpbkiauv.ui.components.EmptyTextProgressView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RadialProgressView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.RecyclerViewItemRangeSelector;
import im.bclpbkiauv.ui.components.SizeNotifierFrameLayout;
import java.util.ArrayList;
import java.util.HashMap;

public class PhotoPickerActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public int alertOnlyOnce;
    /* access modifiers changed from: private */
    public boolean allowCaption;
    /* access modifiers changed from: private */
    public boolean allowIndices;
    /* access modifiers changed from: private */
    public boolean allowOrder = true;
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    private CharSequence caption;
    /* access modifiers changed from: private */
    public ChatActivity chatActivity;
    protected EditTextEmoji commentTextView;
    /* access modifiers changed from: private */
    public PhotoPickerActivityDelegate delegate;
    /* access modifiers changed from: private */
    public EmptyTextProgressView emptyView;
    protected FrameLayout frameLayout2;
    /* access modifiers changed from: private */
    public int imageReqId;
    /* access modifiers changed from: private */
    public boolean imageSearchEndReached = true;
    private String initialSearchString;
    boolean isFcCrop;
    private ActionBarMenuSubItem[] itemCells;
    private RecyclerViewItemRangeSelector itemRangeSelector;
    /* access modifiers changed from: private */
    public int itemSize = 100;
    /* access modifiers changed from: private */
    public int itemsPerRow = 3;
    /* access modifiers changed from: private */
    public ImageView iv;
    private String lastSearchImageString;
    /* access modifiers changed from: private */
    public String lastSearchString;
    private int lastSearchToken;
    /* access modifiers changed from: private */
    public GridLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private boolean loadingRecent;
    private FCPhotoPickerActivityDelegate mFCPhotoPickerActivityDelegate;
    /* access modifiers changed from: private */
    public int maxSelectedPhotos;
    /* access modifiers changed from: private */
    public boolean mblnSendOriginal = false;
    private boolean needsBottomLayout = true;
    /* access modifiers changed from: private */
    public String nextImagesSearchOffset;
    /* access modifiers changed from: private */
    public Paint paint = new Paint(1);
    private PhotoViewer.PhotoViewerProvider provider = new PhotoViewer.EmptyPhotoViewerProvider() {
        public boolean scaleToFill() {
            return false;
        }

        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index, boolean needPreview) {
            PhotoAttachPhotoCell cell = PhotoPickerActivity.this.getCellForIndex(index);
            if (cell == null) {
                return null;
            }
            BackupImageView imageView = cell.getImageView();
            int[] coords = new int[2];
            imageView.getLocationInWindow(coords);
            PhotoViewer.PlaceProviderObject object = new PhotoViewer.PlaceProviderObject();
            object.viewX = coords[0];
            object.viewY = coords[1] - (Build.VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight);
            object.parentView = PhotoPickerActivity.this.listView;
            object.imageReceiver = imageView.getImageReceiver();
            object.thumb = object.imageReceiver.getBitmapSafe();
            object.scale = cell.getScale();
            cell.showCheck(false);
            return object;
        }

        public void updatePhotoAtIndex(int index) {
            ArrayList<MediaController.SearchImage> array;
            PhotoAttachPhotoCell cell = PhotoPickerActivity.this.getCellForIndex(index);
            if (cell == null) {
                return;
            }
            if (PhotoPickerActivity.this.selectedAlbum != null) {
                BackupImageView imageView = cell.getImageView();
                imageView.setOrientation(0, true);
                MediaController.PhotoEntry photoEntry = PhotoPickerActivity.this.selectedAlbum.photos.get(index);
                if (photoEntry.thumbPath != null) {
                    imageView.setImage(photoEntry.thumbPath, (String) null, Theme.chat_attachEmptyDrawable);
                } else if (photoEntry.path != null) {
                    imageView.setOrientation(photoEntry.orientation, true);
                    if (photoEntry.isVideo) {
                        imageView.setImage("vthumb://" + photoEntry.imageId + LogUtils.COLON + photoEntry.path, (String) null, Theme.chat_attachEmptyDrawable);
                        return;
                    }
                    imageView.setImage("thumb://" + photoEntry.imageId + LogUtils.COLON + photoEntry.path, (String) null, Theme.chat_attachEmptyDrawable);
                } else {
                    imageView.setImageDrawable(Theme.chat_attachEmptyDrawable);
                }
            } else {
                if (!PhotoPickerActivity.this.searchResult.isEmpty() || PhotoPickerActivity.this.lastSearchString != null) {
                    array = PhotoPickerActivity.this.searchResult;
                } else {
                    array = PhotoPickerActivity.this.recentImages;
                }
                cell.setPhotoEntry(array.get(index), true, false);
            }
        }

        public boolean allowCaption() {
            return PhotoPickerActivity.this.allowCaption;
        }

        public ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index) {
            PhotoAttachPhotoCell cell = PhotoPickerActivity.this.getCellForIndex(index);
            if (cell != null) {
                return cell.getImageView().getImageReceiver().getBitmapSafe();
            }
            return null;
        }

        public void willSwitchFromPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index) {
            ArrayList<MediaController.SearchImage> array;
            int count = PhotoPickerActivity.this.listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View view = PhotoPickerActivity.this.listView.getChildAt(a);
                if (view.getTag() != null) {
                    PhotoAttachPhotoCell cell = (PhotoAttachPhotoCell) view;
                    int num = ((Integer) view.getTag()).intValue();
                    if (PhotoPickerActivity.this.selectedAlbum == null) {
                        if (!PhotoPickerActivity.this.searchResult.isEmpty() || PhotoPickerActivity.this.lastSearchString != null) {
                            array = PhotoPickerActivity.this.searchResult;
                        } else {
                            array = PhotoPickerActivity.this.recentImages;
                        }
                        if (num < 0) {
                            continue;
                        } else if (num >= array.size()) {
                            continue;
                        }
                    } else if (num < 0) {
                        continue;
                    } else if (num >= PhotoPickerActivity.this.selectedAlbum.photos.size()) {
                        continue;
                    }
                    if (num == index) {
                        cell.showCheck(true);
                        return;
                    }
                }
            }
        }

        public void willHidePhotoViewer() {
            int count = PhotoPickerActivity.this.listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View view = PhotoPickerActivity.this.listView.getChildAt(a);
                if (view instanceof PhotoAttachPhotoCell) {
                    ((PhotoAttachPhotoCell) view).showCheck(true);
                }
            }
        }

        public boolean isPhotoChecked(int index) {
            ArrayList<MediaController.SearchImage> array;
            if (PhotoPickerActivity.this.selectedAlbum == null) {
                if (!PhotoPickerActivity.this.searchResult.isEmpty() || PhotoPickerActivity.this.lastSearchString != null) {
                    array = PhotoPickerActivity.this.searchResult;
                } else {
                    array = PhotoPickerActivity.this.recentImages;
                }
                if (index < 0 || index >= array.size() || !PhotoPickerActivity.this.selectedPhotos.containsKey(array.get(index).id)) {
                    return false;
                }
                return true;
            } else if (index < 0 || index >= PhotoPickerActivity.this.selectedAlbum.photos.size() || !PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(PhotoPickerActivity.this.selectedAlbum.photos.get(index).imageId))) {
                return false;
            } else {
                return true;
            }
        }

        public int setPhotoUnchecked(Object object) {
            Object key = null;
            if (object instanceof MediaController.PhotoEntry) {
                key = Integer.valueOf(((MediaController.PhotoEntry) object).imageId);
            } else if (object instanceof MediaController.SearchImage) {
                key = ((MediaController.SearchImage) object).id;
            }
            if (key == null || !PhotoPickerActivity.this.selectedPhotos.containsKey(key)) {
                return -1;
            }
            PhotoPickerActivity.this.selectedPhotos.remove(key);
            int position = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(key);
            if (position >= 0) {
                PhotoPickerActivity.this.selectedPhotosOrder.remove(position);
            }
            if (PhotoPickerActivity.this.allowIndices) {
                PhotoPickerActivity.this.updateCheckedPhotoIndices();
            }
            return position;
        }

        public int setPhotoChecked(int index, VideoEditedInfo videoEditedInfo) {
            int num;
            ArrayList<MediaController.SearchImage> array;
            boolean add = true;
            int i = -1;
            if (PhotoPickerActivity.this.selectedAlbum == null) {
                if (!PhotoPickerActivity.this.searchResult.isEmpty() || PhotoPickerActivity.this.lastSearchString != null) {
                    array = PhotoPickerActivity.this.searchResult;
                } else {
                    array = PhotoPickerActivity.this.recentImages;
                }
                if (index < 0 || index >= array.size()) {
                    return -1;
                }
                MediaController.SearchImage photoEntry = array.get(index);
                int access$1100 = PhotoPickerActivity.this.addToSelectedPhotos(photoEntry, -1);
                int num2 = access$1100;
                if (access$1100 == -1) {
                    num = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(photoEntry.id);
                } else {
                    add = false;
                    num = num2;
                }
            } else if (index < 0 || index >= PhotoPickerActivity.this.selectedAlbum.photos.size()) {
                return -1;
            } else {
                MediaController.PhotoEntry photoEntry2 = PhotoPickerActivity.this.selectedAlbum.photos.get(index);
                int access$11002 = PhotoPickerActivity.this.addToSelectedPhotos(photoEntry2, -1);
                num = access$11002;
                if (access$11002 == -1) {
                    photoEntry2.editedInfo = videoEditedInfo;
                    num = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry2.imageId));
                } else {
                    add = false;
                    photoEntry2.editedInfo = null;
                }
            }
            int count = PhotoPickerActivity.this.listView.getChildCount();
            int a = 0;
            while (true) {
                if (a >= count) {
                    break;
                }
                View view = PhotoPickerActivity.this.listView.getChildAt(a);
                if (((Integer) view.getTag()).intValue() == index) {
                    PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) view;
                    if (PhotoPickerActivity.this.allowIndices) {
                        i = num;
                    }
                    photoAttachPhotoCell.setChecked(i, add, false);
                } else {
                    a++;
                }
            }
            PhotoPickerActivity.this.updatePhotosButton(add ? 1 : 2);
            PhotoPickerActivity.this.delegate.selectedPhotosChanged();
            return num;
        }

        public boolean cancelButtonPressed() {
            PhotoPickerActivity.this.delegate.actionButtonPressed(true, true, 0, PhotoPickerActivity.this.mblnSendOriginal);
            PhotoPickerActivity.this.finishFragment();
            return true;
        }

        public int getSelectedCount() {
            return PhotoPickerActivity.this.selectedPhotos.size();
        }

        public void sendButtonPressed(int index, VideoEditedInfo videoEditedInfo, boolean notify, int scheduleDate) {
            ArrayList<MediaController.SearchImage> array;
            if (PhotoPickerActivity.this.selectedPhotos.isEmpty()) {
                if (PhotoPickerActivity.this.selectedAlbum == null) {
                    if (!PhotoPickerActivity.this.searchResult.isEmpty() || PhotoPickerActivity.this.lastSearchString != null) {
                        array = PhotoPickerActivity.this.searchResult;
                    } else {
                        array = PhotoPickerActivity.this.recentImages;
                    }
                    if (index >= 0 && index < array.size()) {
                        int unused = PhotoPickerActivity.this.addToSelectedPhotos(array.get(index), -1);
                    } else {
                        return;
                    }
                } else if (index >= 0 && index < PhotoPickerActivity.this.selectedAlbum.photos.size()) {
                    MediaController.PhotoEntry photoEntry = PhotoPickerActivity.this.selectedAlbum.photos.get(index);
                    photoEntry.editedInfo = videoEditedInfo;
                    int unused2 = PhotoPickerActivity.this.addToSelectedPhotos(photoEntry, -1);
                } else {
                    return;
                }
            }
            PhotoPickerActivity.this.sendSelectedPhotos(notify, scheduleDate);
        }

        public ArrayList<Object> getSelectedPhotosOrder() {
            return PhotoPickerActivity.this.selectedPhotosOrder;
        }

        public HashMap<Object, Object> getSelectedPhotos() {
            return PhotoPickerActivity.this.selectedPhotos;
        }
    };
    /* access modifiers changed from: private */
    public ArrayList<MediaController.SearchImage> recentImages;
    /* access modifiers changed from: private */
    public RectF rect = new RectF();
    private ActionBarMenuItem searchItem;
    /* access modifiers changed from: private */
    public ArrayList<MediaController.SearchImage> searchResult = new ArrayList<>();
    /* access modifiers changed from: private */
    public HashMap<String, MediaController.SearchImage> searchResultKeys = new HashMap<>();
    private HashMap<String, MediaController.SearchImage> searchResultUrls = new HashMap<>();
    /* access modifiers changed from: private */
    public boolean searching;
    private boolean searchingUser;
    /* access modifiers changed from: private */
    public int selectPhotoType;
    /* access modifiers changed from: private */
    public MediaController.AlbumEntry selectedAlbum;
    protected View selectedCountView;
    /* access modifiers changed from: private */
    public HashMap<Object, Object> selectedPhotos;
    /* access modifiers changed from: private */
    public ArrayList<Object> selectedPhotosOrder;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout sendPopupLayout;
    /* access modifiers changed from: private */
    public ActionBarPopupWindow sendPopupWindow;
    private boolean sendPressed;
    protected View shadow;
    /* access modifiers changed from: private */
    public boolean shouldSelect;
    private SizeNotifierFrameLayout sizeNotifierFrameLayout;
    /* access modifiers changed from: private */
    public TextPaint textPaint = new TextPaint(1);
    /* access modifiers changed from: private */
    public int type;
    private ImageView writeButton;
    protected FrameLayout writeButtonContainer;
    private Drawable writeButtonDrawable;

    public interface FCPhotoPickerActivityDelegate {
        void selectedFCPhotos(String str);
    }

    public interface PhotoPickerActivityDelegate {
        void actionButtonPressed(boolean z, boolean z2, int i, boolean z3);

        void onCaptionChanged(CharSequence charSequence);

        void selectedPhotosChanged();
    }

    public PhotoPickerActivity(int type2, MediaController.AlbumEntry selectedAlbum2, HashMap<Object, Object> selectedPhotos2, ArrayList<Object> selectedPhotosOrder2, ArrayList<MediaController.SearchImage> recentImages2, int selectPhotoType2, boolean allowCaption2, ChatActivity chatActivity2) {
        this.selectedAlbum = selectedAlbum2;
        this.selectedPhotos = selectedPhotos2;
        this.selectedPhotosOrder = selectedPhotosOrder2;
        this.type = type2;
        this.recentImages = recentImages2 != null ? recentImages2 : new ArrayList<>();
        this.selectPhotoType = selectPhotoType2;
        this.chatActivity = chatActivity2;
        this.allowCaption = allowCaption2;
    }

    public PhotoPickerActivity(int type2, MediaController.AlbumEntry selectedAlbum2, HashMap<Object, Object> selectedPhotos2, ArrayList<Object> selectedPhotosOrder2, ArrayList<MediaController.SearchImage> recentImages2, int selectPhotoType2, boolean allowCaption2, ChatActivity chatActivity2, boolean isFcCrop2) {
        this.selectedAlbum = selectedAlbum2;
        this.selectedPhotos = selectedPhotos2;
        this.selectedPhotosOrder = selectedPhotosOrder2;
        this.type = type2;
        this.recentImages = recentImages2 != null ? recentImages2 : new ArrayList<>();
        this.selectPhotoType = selectPhotoType2;
        this.chatActivity = chatActivity2;
        this.allowCaption = allowCaption2;
        this.isFcCrop = isFcCrop2;
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recentImagesDidLoad);
        if (this.selectedAlbum == null && this.recentImages.isEmpty()) {
            MessagesStorage.getInstance(this.currentAccount).loadWebRecent(this.type);
            this.loadingRecent = true;
        }
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recentImagesDidLoad);
        if (this.imageReqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.imageReqId, true);
            this.imageReqId = 0;
        }
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
        Context context2 = context;
        this.mblnSendOriginal = false;
        this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        this.actionBar.setTitleColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.actionBar.setItemsColor(Theme.getColor(Theme.key_dialogTextBlack), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_dialogButtonSelector), false);
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        boolean z = true;
        if (this.selectedAlbum != null) {
            this.actionBar.setTitle(this.selectedAlbum.bucketName);
        } else {
            int i = this.type;
            if (i == 0) {
                this.actionBar.setTitle(LocaleController.getString("SearchImagesTitle", R.string.SearchImagesTitle));
            } else if (i == 1) {
                this.actionBar.setTitle(LocaleController.getString("SearchGifsTitle", R.string.SearchGifsTitle));
            }
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    PhotoPickerActivity.this.finishFragment();
                } else if (id == 0) {
                    if (!PhotoPickerActivity.this.mblnSendOriginal) {
                        PhotoPickerActivity.this.iv.setColorFilter((ColorFilter) null);
                        PhotoPickerActivity.this.iv.setImageResource(R.mipmap.ic_checked_completed_user_info);
                    } else {
                        PhotoPickerActivity.this.iv.setColorFilter(Color.parseColor("#707070"));
                        PhotoPickerActivity.this.iv.setImageResource(R.mipmap.ic_checked_false_completed_user_info);
                    }
                    PhotoPickerActivity photoPickerActivity = PhotoPickerActivity.this;
                    boolean unused = photoPickerActivity.mblnSendOriginal = !photoPickerActivity.mblnSendOriginal;
                }
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        if (this.selectPhotoType == 0) {
            LinearLayout linearLayout = new LinearLayout(context2);
            this.iv = new ImageView(context2);
            linearLayout.setGravity(16);
            this.iv.setColorFilter(Color.parseColor("#707070"));
            this.iv.setImageResource(R.mipmap.ic_checked_false_completed_user_info);
            this.iv.setScaleType(ImageView.ScaleType.FIT_XY);
            linearLayout.addView(this.iv, LayoutHelper.createLinear(AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f), 0.0f, 0.0f, 0.0f, 0.0f));
            TextView tv = new TextView(context2);
            tv.setText(LocaleController.getString(R.string.original_image));
            tv.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
            linearLayout.addView(tv, LayoutHelper.createLinear(-2, -2, (float) AndroidUtilities.dp(1.0f), 0.0f, 0.0f, 0.0f));
            menu.addItemView(0, linearLayout);
        }
        if (this.selectedAlbum == null) {
            ActionBarMenuItem actionBarMenuItemSearchListener = this.actionBar.createMenu().addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
                public void onSearchExpand() {
                }

                public boolean canCollapseSearch() {
                    PhotoPickerActivity.this.finishFragment();
                    return false;
                }

                public void onTextChanged(EditText editText) {
                    if (editText.getText().length() == 0) {
                        PhotoPickerActivity.this.searchResult.clear();
                        PhotoPickerActivity.this.searchResultKeys.clear();
                        String unused = PhotoPickerActivity.this.lastSearchString = null;
                        boolean unused2 = PhotoPickerActivity.this.imageSearchEndReached = true;
                        boolean unused3 = PhotoPickerActivity.this.searching = false;
                        if (PhotoPickerActivity.this.imageReqId != 0) {
                            ConnectionsManager.getInstance(PhotoPickerActivity.this.currentAccount).cancelRequest(PhotoPickerActivity.this.imageReqId, true);
                            int unused4 = PhotoPickerActivity.this.imageReqId = 0;
                        }
                        PhotoPickerActivity.this.emptyView.setText("");
                        PhotoPickerActivity.this.updateSearchInterface();
                        return;
                    }
                    PhotoPickerActivity.this.processSearch(editText);
                }

                public void onSearchPressed(EditText editText) {
                    PhotoPickerActivity.this.processSearch(editText);
                }
            });
            this.searchItem = actionBarMenuItemSearchListener;
            EditTextBoldCursor editText = actionBarMenuItemSearchListener.getSearchField();
            editText.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
            editText.setCursorColor(Theme.getColor(Theme.key_dialogTextBlack));
            editText.setHintTextColor(Theme.getColor(Theme.key_chat_messagePanelHint));
        }
        if (this.selectedAlbum == null) {
            int i2 = this.type;
            if (i2 == 0) {
                this.searchItem.setSearchFieldHint(LocaleController.getString("SearchImagesTitle", R.string.SearchImagesTitle));
            } else if (i2 == 1) {
                this.searchItem.setSearchFieldHint(LocaleController.getString("SearchGifsTitle", R.string.SearchGifsTitle));
            }
        }
        AnonymousClass4 r5 = new SizeNotifierFrameLayout(context2) {
            private boolean ignoreLayout;
            private int lastItemSize;
            private int lastNotifyWidth;

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int totalHeight = View.MeasureSpec.getSize(heightMeasureSpec);
                int availableWidth = View.MeasureSpec.getSize(widthMeasureSpec);
                if (AndroidUtilities.isTablet()) {
                    int unused = PhotoPickerActivity.this.itemsPerRow = 4;
                } else if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                    int unused2 = PhotoPickerActivity.this.itemsPerRow = 4;
                } else {
                    int unused3 = PhotoPickerActivity.this.itemsPerRow = 3;
                }
                this.ignoreLayout = true;
                int unused4 = PhotoPickerActivity.this.itemSize = ((availableWidth - AndroidUtilities.dp(12.0f)) - AndroidUtilities.dp(10.0f)) / PhotoPickerActivity.this.itemsPerRow;
                if (this.lastItemSize != PhotoPickerActivity.this.itemSize) {
                    this.lastItemSize = PhotoPickerActivity.this.itemSize;
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public final void run() {
                            PhotoPickerActivity.AnonymousClass4.this.lambda$onMeasure$0$PhotoPickerActivity$4();
                        }
                    });
                }
                PhotoPickerActivity.this.layoutManager.setSpanCount((PhotoPickerActivity.this.itemSize * PhotoPickerActivity.this.itemsPerRow) + (AndroidUtilities.dp(5.0f) * (PhotoPickerActivity.this.itemsPerRow - 1)));
                this.ignoreLayout = false;
                onMeasureInternal(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(totalHeight, 1073741824));
            }

            public /* synthetic */ void lambda$onMeasure$0$PhotoPickerActivity$4() {
                PhotoPickerActivity.this.listAdapter.notifyDataSetChanged();
            }

            private void onMeasureInternal(int widthMeasureSpec, int heightMeasureSpec) {
                int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
                int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(widthSize, heightSize);
                if (getKeyboardHeight() <= AndroidUtilities.dp(20.0f)) {
                    if (!AndroidUtilities.isInMultiwindow && PhotoPickerActivity.this.commentTextView != null && PhotoPickerActivity.this.frameLayout2.getParent() == this) {
                        heightSize -= PhotoPickerActivity.this.commentTextView.getEmojiPadding();
                        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(heightSize, 1073741824);
                    }
                } else if (PhotoPickerActivity.this.commentTextView != null) {
                    this.ignoreLayout = true;
                    PhotoPickerActivity.this.commentTextView.hideEmojiView();
                    this.ignoreLayout = false;
                }
                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (!(child == null || child.getVisibility() == 8)) {
                        if (PhotoPickerActivity.this.commentTextView == null || !PhotoPickerActivity.this.commentTextView.isPopupView(child)) {
                            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                        } else if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, 1073741824), View.MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, 1073741824));
                        } else if (AndroidUtilities.isTablet()) {
                            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, 1073741824), View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(AndroidUtilities.isTablet() ? 200.0f : 320.0f), (heightSize - AndroidUtilities.statusBarHeight) + getPaddingTop()), 1073741824));
                        } else {
                            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, 1073741824), View.MeasureSpec.makeMeasureSpec((heightSize - AndroidUtilities.statusBarHeight) + getPaddingTop(), 1073741824));
                        }
                    }
                }
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                int childLeft;
                int childTop;
                if (this.lastNotifyWidth != r - l) {
                    this.lastNotifyWidth = r - l;
                    if (PhotoPickerActivity.this.listAdapter != null) {
                        PhotoPickerActivity.this.listAdapter.notifyDataSetChanged();
                    }
                    if (PhotoPickerActivity.this.sendPopupWindow != null && PhotoPickerActivity.this.sendPopupWindow.isShowing()) {
                        PhotoPickerActivity.this.sendPopupWindow.dismiss();
                    }
                }
                int count = getChildCount();
                int paddingBottom = (PhotoPickerActivity.this.commentTextView == null || PhotoPickerActivity.this.frameLayout2.getParent() != this || getKeyboardHeight() > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow || AndroidUtilities.isTablet()) ? 0 : PhotoPickerActivity.this.commentTextView.getEmojiPadding();
                setBottomClip(paddingBottom);
                for (int i = 0; i < count; i++) {
                    View child = getChildAt(i);
                    if (child.getVisibility() != 8) {
                        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                        int width = child.getMeasuredWidth();
                        int height = child.getMeasuredHeight();
                        int gravity = lp.gravity;
                        if (gravity == -1) {
                            gravity = 51;
                        }
                        int verticalGravity = gravity & 112;
                        int i2 = gravity & 7 & 7;
                        if (i2 == 1) {
                            childLeft = ((((r - l) - width) / 2) + lp.leftMargin) - lp.rightMargin;
                        } else if (i2 != 5) {
                            childLeft = lp.leftMargin + getPaddingLeft();
                        } else {
                            childLeft = (((r - l) - width) - lp.rightMargin) - getPaddingRight();
                        }
                        if (verticalGravity == 16) {
                            childTop = (((((b - paddingBottom) - t) - height) / 2) + lp.topMargin) - lp.bottomMargin;
                        } else if (verticalGravity == 48) {
                            childTop = lp.topMargin + getPaddingTop();
                        } else if (verticalGravity != 80) {
                            childTop = lp.topMargin;
                        } else {
                            childTop = (((b - paddingBottom) - t) - height) - lp.bottomMargin;
                        }
                        if (PhotoPickerActivity.this.commentTextView != null && PhotoPickerActivity.this.commentTextView.isPopupView(child)) {
                            if (AndroidUtilities.isTablet()) {
                                childTop = getMeasuredHeight() - child.getMeasuredHeight();
                            } else {
                                childTop = (getMeasuredHeight() + getKeyboardHeight()) - child.getMeasuredHeight();
                            }
                        }
                        child.layout(childLeft, childTop, childLeft + width, childTop + height);
                    }
                }
                notifyHeightChanged();
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.sizeNotifierFrameLayout = r5;
        r5.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        this.fragmentView = this.sizeNotifierFrameLayout;
        RecyclerListView recyclerListView = new RecyclerListView(context2);
        this.listView = recyclerListView;
        recyclerListView.setPadding(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(50.0f));
        this.listView.setClipToPadding(false);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.listView.setLayoutAnimation((LayoutAnimationController) null);
        RecyclerListView recyclerListView2 = this.listView;
        AnonymousClass5 r7 = new GridLayoutManager(context2, 4) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = r7;
        recyclerListView2.setLayoutManager(r7);
        this.layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            public int getSpanSize(int position) {
                return PhotoPickerActivity.this.itemSize + (position % PhotoPickerActivity.this.itemsPerRow != PhotoPickerActivity.this.itemsPerRow + -1 ? AndroidUtilities.dp(5.0f) : 0);
            }
        });
        this.sizeNotifierFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        RecyclerListView recyclerListView3 = this.listView;
        ListAdapter listAdapter2 = new ListAdapter(context2);
        this.listAdapter = listAdapter2;
        recyclerListView3.setAdapter(listAdapter2);
        this.listView.setGlowColor(Theme.getColor(Theme.key_dialogBackground));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                PhotoPickerActivity.this.lambda$createView$0$PhotoPickerActivity(view, i);
            }
        });
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
            public final boolean onItemClick(View view, int i) {
                return PhotoPickerActivity.this.lambda$createView$1$PhotoPickerActivity(view, i);
            }
        });
        RecyclerViewItemRangeSelector recyclerViewItemRangeSelector = new RecyclerViewItemRangeSelector(new RecyclerViewItemRangeSelector.RecyclerViewItemRangeSelectorDelegate() {
            public int getItemCount() {
                return PhotoPickerActivity.this.listAdapter.getItemCount();
            }

            public void setSelected(View view, int index, boolean selected) {
                if (selected == PhotoPickerActivity.this.shouldSelect && (view instanceof PhotoAttachPhotoCell)) {
                    ((PhotoAttachPhotoCell) view).callDelegate();
                }
            }

            public boolean isSelected(int index) {
                Object key;
                MediaController.SearchImage photoEntry;
                if (PhotoPickerActivity.this.selectedAlbum != null) {
                    key = Integer.valueOf(PhotoPickerActivity.this.selectedAlbum.photos.get(index).imageId);
                } else {
                    if (!PhotoPickerActivity.this.searchResult.isEmpty() || PhotoPickerActivity.this.lastSearchString != null) {
                        photoEntry = (MediaController.SearchImage) PhotoPickerActivity.this.searchResult.get(index);
                    } else {
                        photoEntry = (MediaController.SearchImage) PhotoPickerActivity.this.recentImages.get(index);
                    }
                    key = photoEntry.id;
                }
                return PhotoPickerActivity.this.selectedPhotos.containsKey(key);
            }

            public boolean isIndexSelectable(int index) {
                return PhotoPickerActivity.this.listAdapter.getItemViewType(index) == 0;
            }

            public void onStartStopSelection(boolean start) {
                int unused = PhotoPickerActivity.this.alertOnlyOnce = start;
                if (start) {
                    PhotoPickerActivity.this.parentLayout.requestDisallowInterceptTouchEvent(true);
                }
                PhotoPickerActivity.this.listView.hideSelector();
            }
        });
        this.itemRangeSelector = recyclerViewItemRangeSelector;
        this.listView.addOnItemTouchListener(recyclerViewItemRangeSelector);
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context2);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.setTextColor(-8355712);
        this.emptyView.setProgressBarColor(-11371101);
        this.emptyView.setShowAtCenter(false);
        if (this.selectedAlbum != null) {
            this.emptyView.setText(LocaleController.getString("NoPhotos", R.string.NoPhotos));
        } else {
            this.emptyView.setText("");
        }
        this.sizeNotifierFrameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1.0f, -1.0f, 51, 0.0f, 0.0f, 0.0f, this.selectPhotoType != 0 ? 0.0f : 48.0f));
        if (this.selectedAlbum == null) {
            this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == 1) {
                        AndroidUtilities.hideKeyboard(PhotoPickerActivity.this.getParentActivity().getCurrentFocus());
                    }
                }

                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    int firstVisibleItem = PhotoPickerActivity.this.layoutManager.findFirstVisibleItemPosition();
                    boolean z = false;
                    int visibleItemCount = firstVisibleItem == -1 ? 0 : Math.abs(PhotoPickerActivity.this.layoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
                    if (visibleItemCount > 0) {
                        int totalItemCount = PhotoPickerActivity.this.layoutManager.getItemCount();
                        if (visibleItemCount != 0 && firstVisibleItem + visibleItemCount > totalItemCount - 2 && !PhotoPickerActivity.this.searching && !PhotoPickerActivity.this.imageSearchEndReached) {
                            PhotoPickerActivity photoPickerActivity = PhotoPickerActivity.this;
                            if (photoPickerActivity.type == 1) {
                                z = true;
                            }
                            photoPickerActivity.searchImages(z, PhotoPickerActivity.this.lastSearchString, PhotoPickerActivity.this.nextImagesSearchOffset, true);
                        }
                    }
                }
            });
            updateSearchInterface();
        }
        if (this.needsBottomLayout) {
            View view = new View(context2);
            this.shadow = view;
            view.setBackgroundResource(R.drawable.header_shadow_reverse);
            this.shadow.setTranslationY((float) AndroidUtilities.dp(48.0f));
            this.sizeNotifierFrameLayout.addView(this.shadow, LayoutHelper.createFrame(-1.0f, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
            FrameLayout frameLayout = new FrameLayout(context2);
            this.frameLayout2 = frameLayout;
            frameLayout.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
            this.frameLayout2.setVisibility(4);
            this.frameLayout2.setTranslationY((float) AndroidUtilities.dp(48.0f));
            this.sizeNotifierFrameLayout.addView(this.frameLayout2, LayoutHelper.createFrame(-1, 48, 83));
            this.frameLayout2.setOnTouchListener($$Lambda$PhotoPickerActivity$4JlaVOnsegC66NXLkSuJxRNlm4.INSTANCE);
            EditTextEmoji editTextEmoji = this.commentTextView;
            if (editTextEmoji != null) {
                editTextEmoji.onDestroy();
            }
            this.commentTextView = new EditTextEmoji(context2, this.sizeNotifierFrameLayout, (BaseFragment) null, 1);
            this.commentTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MessagesController.getInstance(UserConfig.selectedAccount).maxCaptionLength)});
            this.commentTextView.setHint(LocaleController.getString("AddCaption", R.string.AddCaption));
            this.commentTextView.onResume();
            EditTextBoldCursor editText2 = this.commentTextView.getEditText();
            editText2.setMaxLines(1);
            editText2.setSingleLine(true);
            this.frameLayout2.addView(this.commentTextView, LayoutHelper.createFrame(-1.0f, -1.0f, 51, 0.0f, 0.0f, 84.0f, 0.0f));
            CharSequence charSequence = this.caption;
            if (charSequence != null) {
                this.commentTextView.setText(charSequence);
            }
            this.commentTextView.getEditText().addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                public void afterTextChanged(Editable s) {
                    if (PhotoPickerActivity.this.delegate != null) {
                        PhotoPickerActivity.this.delegate.onCaptionChanged(s);
                    }
                }
            });
            FrameLayout frameLayout3 = new FrameLayout(context2);
            this.writeButtonContainer = frameLayout3;
            frameLayout3.setVisibility(4);
            this.writeButtonContainer.setScaleX(0.2f);
            this.writeButtonContainer.setScaleY(0.2f);
            this.writeButtonContainer.setAlpha(0.0f);
            this.writeButtonContainer.setContentDescription(LocaleController.getString("Send", R.string.Send));
            this.sizeNotifierFrameLayout.addView(this.writeButtonContainer, LayoutHelper.createFrame(60.0f, 60.0f, 85, 0.0f, 0.0f, 6.0f, 10.0f));
            this.writeButtonContainer.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoPickerActivity.this.lambda$createView$3$PhotoPickerActivity(view);
                }
            });
            this.writeButton = new ImageView(context2);
            this.writeButtonDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_dialogFloatingButton), Theme.getColor(Theme.key_dialogFloatingButtonPressed));
            if (Build.VERSION.SDK_INT < 21) {
                Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
                shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
                CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, this.writeButtonDrawable, 0, 0);
                combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                this.writeButtonDrawable = combinedDrawable;
            }
            this.writeButton.setBackgroundDrawable(this.writeButtonDrawable);
            this.writeButton.setImageResource(R.drawable.attach_send);
            this.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogFloatingIcon), PorterDuff.Mode.MULTIPLY));
            this.writeButton.setScaleType(ImageView.ScaleType.CENTER);
            if (Build.VERSION.SDK_INT >= 21) {
                this.writeButton.setOutlineProvider(new ViewOutlineProvider() {
                    public void getOutline(View view, Outline outline) {
                        outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                    }
                });
            }
            this.writeButtonContainer.addView(this.writeButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 51, Build.VERSION.SDK_INT >= 21 ? 2.0f : 0.0f, 0.0f, 0.0f, 0.0f));
            this.textPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
            this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            AnonymousClass11 r6 = new View(context2) {
                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    String text = String.format("%d", new Object[]{Integer.valueOf(Math.max(1, PhotoPickerActivity.this.selectedPhotosOrder.size()))});
                    int textSize = (int) Math.ceil((double) PhotoPickerActivity.this.textPaint.measureText(text));
                    int size = Math.max(AndroidUtilities.dp(16.0f) + textSize, AndroidUtilities.dp(24.0f));
                    int cx = getMeasuredWidth() / 2;
                    int measuredHeight = getMeasuredHeight() / 2;
                    PhotoPickerActivity.this.textPaint.setColor(Theme.getColor(Theme.key_dialogRoundCheckBoxCheck));
                    PhotoPickerActivity.this.paint.setColor(Theme.getColor(Theme.key_dialogBackground));
                    PhotoPickerActivity.this.rect.set((float) (cx - (size / 2)), 0.0f, (float) ((size / 2) + cx), (float) getMeasuredHeight());
                    canvas.drawRoundRect(PhotoPickerActivity.this.rect, (float) AndroidUtilities.dp(12.0f), (float) AndroidUtilities.dp(12.0f), PhotoPickerActivity.this.paint);
                    PhotoPickerActivity.this.paint.setColor(Theme.getColor(Theme.key_dialogRoundCheckBox));
                    PhotoPickerActivity.this.rect.set((float) ((cx - (size / 2)) + AndroidUtilities.dp(2.0f)), (float) AndroidUtilities.dp(2.0f), (float) (((size / 2) + cx) - AndroidUtilities.dp(2.0f)), (float) (getMeasuredHeight() - AndroidUtilities.dp(2.0f)));
                    canvas.drawRoundRect(PhotoPickerActivity.this.rect, (float) AndroidUtilities.dp(10.0f), (float) AndroidUtilities.dp(10.0f), PhotoPickerActivity.this.paint);
                    canvas.drawText(text, (float) (cx - (textSize / 2)), (float) AndroidUtilities.dp(16.2f), PhotoPickerActivity.this.textPaint);
                }
            };
            this.selectedCountView = r6;
            r6.setAlpha(0.0f);
            this.selectedCountView.setScaleX(0.2f);
            this.selectedCountView.setScaleY(0.2f);
            this.sizeNotifierFrameLayout.addView(this.selectedCountView, LayoutHelper.createFrame(42.0f, 24.0f, 85, 0.0f, 0.0f, -8.0f, 9.0f));
            if (this.selectPhotoType != 0) {
                this.commentTextView.setVisibility(8);
            }
        }
        if ((this.selectedAlbum == null && this.type != 0) || !this.allowOrder) {
            z = false;
        }
        this.allowIndices = z;
        this.listView.setEmptyView(this.emptyView);
        updatePhotosButton(0);
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$PhotoPickerActivity(View view, int position) {
        ArrayList arrayList;
        int type2;
        MediaController.AlbumEntry albumEntry = this.selectedAlbum;
        if (albumEntry != null) {
            arrayList = albumEntry.photos;
        } else if (!this.searchResult.isEmpty() || this.lastSearchString != null) {
            arrayList = this.searchResult;
        } else {
            arrayList = this.recentImages;
        }
        if (position >= 0 && position < arrayList.size()) {
            ActionBarMenuItem actionBarMenuItem = this.searchItem;
            if (actionBarMenuItem != null) {
                AndroidUtilities.hideKeyboard(actionBarMenuItem.getSearchField());
            }
            int i = this.selectPhotoType;
            if (i == 1) {
                type2 = 1;
            } else if (i == 2) {
                type2 = 3;
            } else if (this.chatActivity == null) {
                type2 = 4;
            } else {
                type2 = 0;
            }
            PhotoViewer.getInstance().setParentActivity(getParentActivity());
            PhotoViewer.getInstance().setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
            if (this.isFcCrop) {
                PhotoViewer.getInstance().setIsFcCrop(true);
                PhotoViewer.getInstance().openPhotoForSelect(arrayList, position, 1, this.provider, this.chatActivity);
                return;
            }
            PhotoViewer.getInstance().setIsFcCrop(false);
            PhotoViewer.getInstance().openPhotoForSelect(arrayList, position, type2, this.provider, this.chatActivity);
        }
    }

    public /* synthetic */ boolean lambda$createView$1$PhotoPickerActivity(View view, int position) {
        if (this.isFcCrop || !(view instanceof PhotoAttachPhotoCell)) {
            return false;
        }
        RecyclerViewItemRangeSelector recyclerViewItemRangeSelector = this.itemRangeSelector;
        boolean z = !((PhotoAttachPhotoCell) view).isChecked();
        this.shouldSelect = z;
        recyclerViewItemRangeSelector.setIsActive(view, true, position, z);
        return false;
    }

    static /* synthetic */ boolean lambda$createView$2(View v, MotionEvent event) {
        return true;
    }

    public /* synthetic */ void lambda$createView$3$PhotoPickerActivity(View v) {
        ChatActivity chatActivity2 = this.chatActivity;
        if (chatActivity2 == null || !chatActivity2.isInScheduleMode()) {
            sendSelectedPhotos(true, 0);
        } else {
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), UserObject.isUserSelf(this.chatActivity.getCurrentUser()), new AlertsCreator.ScheduleDatePickerDelegate() {
                public final void didSelectDate(boolean z, int i) {
                    PhotoPickerActivity.this.sendSelectedPhotos(z, i);
                }
            });
        }
    }

    public void setLayoutViews(FrameLayout f2, FrameLayout button, View count, View s, EditTextEmoji emoji) {
        this.frameLayout2 = f2;
        this.writeButtonContainer = button;
        this.commentTextView = emoji;
        this.selectedCountView = count;
        this.shadow = s;
        this.needsBottomLayout = false;
    }

    private void applyCaption() {
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null && editTextEmoji.length() > 0) {
            Object entry = this.selectedPhotos.get(this.selectedPhotosOrder.get(0));
            if (entry instanceof MediaController.PhotoEntry) {
                ((MediaController.PhotoEntry) entry).caption = this.commentTextView.getText().toString();
            } else if (entry instanceof MediaController.SearchImage) {
                ((MediaController.SearchImage) entry).caption = this.commentTextView.getText().toString();
            }
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onResume();
        }
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.openSearch(true);
            if (!TextUtils.isEmpty(this.initialSearchString)) {
                this.searchItem.setSearchFieldText(this.initialSearchString, false);
                this.initialSearchString = null;
                processSearch(this.searchItem.getSearchField());
            }
            getParentActivity().getWindow().setSoftInputMode(16);
        }
    }

    public void onPause() {
        super.onPause();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if (id == NotificationCenter.recentImagesDidLoad && this.selectedAlbum == null && this.type == args[0].intValue()) {
            this.recentImages = args[1];
            this.loadingRecent = false;
            updateSearchInterface();
        }
    }

    public RecyclerListView getListView() {
        return this.listView;
    }

    public void setCaption(CharSequence text) {
        this.caption = text;
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.setText(text);
        }
    }

    public void setInitialSearchString(String text) {
        this.initialSearchString = text;
    }

    /* access modifiers changed from: private */
    public void processSearch(EditText editText) {
        if (editText.getText().toString().length() != 0) {
            this.searchResult.clear();
            this.searchResultKeys.clear();
            this.imageSearchEndReached = true;
            searchImages(this.type == 1, editText.getText().toString(), "", true);
            String obj = editText.getText().toString();
            this.lastSearchString = obj;
            if (obj.length() == 0) {
                this.lastSearchString = null;
                this.emptyView.setText("");
            } else {
                this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
            }
            updateSearchInterface();
        }
    }

    private boolean showCommentTextView(final boolean show, boolean animated) {
        if (this.commentTextView == null) {
            return false;
        }
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
        float f = 0.0f;
        float f2 = 0.2f;
        float f3 = 1.0f;
        if (animated) {
            this.animatorSet = new AnimatorSet();
            ArrayList<Animator> animators = new ArrayList<>();
            FrameLayout frameLayout = this.writeButtonContainer;
            Property property = View.SCALE_X;
            float[] fArr = new float[1];
            fArr[0] = show ? 1.0f : 0.2f;
            animators.add(ObjectAnimator.ofFloat(frameLayout, property, fArr));
            FrameLayout frameLayout3 = this.writeButtonContainer;
            Property property2 = View.SCALE_Y;
            float[] fArr2 = new float[1];
            fArr2[0] = show ? 1.0f : 0.2f;
            animators.add(ObjectAnimator.ofFloat(frameLayout3, property2, fArr2));
            FrameLayout frameLayout4 = this.writeButtonContainer;
            Property property3 = View.ALPHA;
            float[] fArr3 = new float[1];
            fArr3[0] = show ? 1.0f : 0.0f;
            animators.add(ObjectAnimator.ofFloat(frameLayout4, property3, fArr3));
            View view = this.selectedCountView;
            Property property4 = View.SCALE_X;
            float[] fArr4 = new float[1];
            fArr4[0] = show ? 1.0f : 0.2f;
            animators.add(ObjectAnimator.ofFloat(view, property4, fArr4));
            View view2 = this.selectedCountView;
            Property property5 = View.SCALE_Y;
            float[] fArr5 = new float[1];
            if (show) {
                f2 = 1.0f;
            }
            fArr5[0] = f2;
            animators.add(ObjectAnimator.ofFloat(view2, property5, fArr5));
            View view3 = this.selectedCountView;
            Property property6 = View.ALPHA;
            float[] fArr6 = new float[1];
            if (!show) {
                f3 = 0.0f;
            }
            fArr6[0] = f3;
            animators.add(ObjectAnimator.ofFloat(view3, property6, fArr6));
            FrameLayout frameLayout5 = this.frameLayout2;
            Property property7 = View.TRANSLATION_Y;
            float[] fArr7 = new float[1];
            fArr7[0] = show ? 0.0f : (float) AndroidUtilities.dp(48.0f);
            animators.add(ObjectAnimator.ofFloat(frameLayout5, property7, fArr7));
            View view4 = this.shadow;
            Property property8 = View.TRANSLATION_Y;
            float[] fArr8 = new float[1];
            if (!show) {
                f = (float) AndroidUtilities.dp(48.0f);
            }
            fArr8[0] = f;
            animators.add(ObjectAnimator.ofFloat(view4, property8, fArr8));
            this.animatorSet.playTogether(animators);
            this.animatorSet.setInterpolator(new DecelerateInterpolator());
            this.animatorSet.setDuration(180);
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (animation.equals(PhotoPickerActivity.this.animatorSet)) {
                        if (!show) {
                            PhotoPickerActivity.this.frameLayout2.setVisibility(4);
                            PhotoPickerActivity.this.writeButtonContainer.setVisibility(4);
                        }
                        AnimatorSet unused = PhotoPickerActivity.this.animatorSet = null;
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (animation.equals(PhotoPickerActivity.this.animatorSet)) {
                        AnimatorSet unused = PhotoPickerActivity.this.animatorSet = null;
                    }
                }
            });
            this.animatorSet.start();
        } else {
            this.writeButtonContainer.setScaleX(show ? 1.0f : 0.2f);
            this.writeButtonContainer.setScaleY(show ? 1.0f : 0.2f);
            this.writeButtonContainer.setAlpha(show ? 1.0f : 0.0f);
            this.selectedCountView.setScaleX(show ? 1.0f : 0.2f);
            View view5 = this.selectedCountView;
            if (show) {
                f2 = 1.0f;
            }
            view5.setScaleY(f2);
            View view6 = this.selectedCountView;
            if (!show) {
                f3 = 0.0f;
            }
            view6.setAlpha(f3);
            this.frameLayout2.setTranslationY(show ? 0.0f : (float) AndroidUtilities.dp(48.0f));
            View view7 = this.shadow;
            if (!show) {
                f = (float) AndroidUtilities.dp(48.0f);
            }
            view7.setTranslationY(f);
            if (!show) {
                this.frameLayout2.setVisibility(4);
                this.writeButtonContainer.setVisibility(4);
            }
        }
        return true;
    }

    public void setMaxSelectedPhotos(int value, boolean order) {
        this.maxSelectedPhotos = value;
        this.allowOrder = order;
        if (value > 0 && this.type == 1) {
            this.maxSelectedPhotos = 1;
        }
    }

    /* access modifiers changed from: private */
    public void updateCheckedPhotoIndices() {
        MediaController.SearchImage photoEntry;
        if (this.allowIndices) {
            int count = this.listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View view = this.listView.getChildAt(a);
                if (view instanceof PhotoAttachPhotoCell) {
                    PhotoAttachPhotoCell cell = (PhotoAttachPhotoCell) view;
                    Integer index = (Integer) cell.getTag();
                    MediaController.AlbumEntry albumEntry = this.selectedAlbum;
                    int i = -1;
                    if (albumEntry != null) {
                        MediaController.PhotoEntry photoEntry2 = albumEntry.photos.get(index.intValue());
                        if (this.allowIndices) {
                            i = this.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry2.imageId));
                        }
                        cell.setNum(i);
                    } else {
                        if (!this.searchResult.isEmpty() || this.lastSearchString != null) {
                            photoEntry = this.searchResult.get(index.intValue());
                        } else {
                            photoEntry = this.recentImages.get(index.intValue());
                        }
                        if (this.allowIndices) {
                            i = this.selectedPhotosOrder.indexOf(photoEntry.id);
                        }
                        cell.setNum(i);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public PhotoAttachPhotoCell getCellForIndex(int index) {
        ArrayList<MediaController.SearchImage> array;
        int count = this.listView.getChildCount();
        for (int a = 0; a < count; a++) {
            View view = this.listView.getChildAt(a);
            if (view instanceof PhotoAttachPhotoCell) {
                PhotoAttachPhotoCell cell = (PhotoAttachPhotoCell) view;
                int num = ((Integer) cell.getTag()).intValue();
                MediaController.AlbumEntry albumEntry = this.selectedAlbum;
                if (albumEntry == null) {
                    if (!this.searchResult.isEmpty() || this.lastSearchString != null) {
                        array = this.searchResult;
                    } else {
                        array = this.recentImages;
                    }
                    if (num < 0) {
                        continue;
                    } else if (num >= array.size()) {
                        continue;
                    }
                } else if (num < 0) {
                    continue;
                } else if (num >= albumEntry.photos.size()) {
                    continue;
                }
                if (num == index) {
                    return cell;
                }
            }
        }
        return null;
    }

    /* access modifiers changed from: private */
    public int addToSelectedPhotos(Object object, int index) {
        Object key = null;
        if (object instanceof MediaController.PhotoEntry) {
            key = Integer.valueOf(((MediaController.PhotoEntry) object).imageId);
        } else if (object instanceof MediaController.SearchImage) {
            key = ((MediaController.SearchImage) object).id;
        }
        if (key == null) {
            return -1;
        }
        if (this.selectedPhotos.containsKey(key)) {
            this.selectedPhotos.remove(key);
            int position = this.selectedPhotosOrder.indexOf(key);
            if (position >= 0) {
                this.selectedPhotosOrder.remove(position);
            }
            if (this.allowIndices) {
                updateCheckedPhotoIndices();
            }
            if (index >= 0) {
                if (object instanceof MediaController.PhotoEntry) {
                    ((MediaController.PhotoEntry) object).reset();
                } else if (object instanceof MediaController.SearchImage) {
                    ((MediaController.SearchImage) object).reset();
                }
                this.provider.updatePhotoAtIndex(index);
            }
            return position;
        }
        this.selectedPhotos.put(key, object);
        this.selectedPhotosOrder.add(key);
        return -1;
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        ActionBarMenuItem actionBarMenuItem;
        if (isOpen && (actionBarMenuItem = this.searchItem) != null) {
            AndroidUtilities.showKeyboard(actionBarMenuItem.getSearchField());
        }
    }

    public void updatePhotosButton(int animated) {
        boolean z = true;
        if (this.selectedPhotos.size() == 0) {
            this.selectedCountView.setPivotX(0.0f);
            this.selectedCountView.setPivotY(0.0f);
            if (animated == 0) {
                z = false;
            }
            showCommentTextView(false, z);
            return;
        }
        this.selectedCountView.invalidate();
        if (showCommentTextView(true, animated != 0) || animated == 0) {
            this.selectedCountView.setPivotX(0.0f);
            this.selectedCountView.setPivotY(0.0f);
            return;
        }
        this.selectedCountView.setPivotX((float) AndroidUtilities.dp(21.0f));
        this.selectedCountView.setPivotY((float) AndroidUtilities.dp(12.0f));
        AnimatorSet animatorSet2 = new AnimatorSet();
        Animator[] animatorArr = new Animator[2];
        View view = this.selectedCountView;
        Property property = View.SCALE_X;
        float[] fArr = new float[2];
        float f = 1.1f;
        fArr[0] = animated == 1 ? 1.1f : 0.9f;
        fArr[1] = 1.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
        View view2 = this.selectedCountView;
        Property property2 = View.SCALE_Y;
        float[] fArr2 = new float[2];
        if (animated != 1) {
            f = 0.9f;
        }
        fArr2[0] = f;
        fArr2[1] = 1.0f;
        animatorArr[1] = ObjectAnimator.ofFloat(view2, property2, fArr2);
        animatorSet2.playTogether(animatorArr);
        animatorSet2.setInterpolator(new OvershootInterpolator());
        animatorSet2.setDuration(180);
        animatorSet2.start();
    }

    /* access modifiers changed from: private */
    public void updateSearchInterface() {
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
        if ((!this.searching || !this.searchResult.isEmpty()) && (!this.loadingRecent || this.lastSearchString != null)) {
            this.emptyView.showTextView();
        } else {
            this.emptyView.showProgress();
        }
    }

    private void searchBotUser(boolean gif) {
        if (!this.searchingUser) {
            this.searchingUser = true;
            TLRPC.TL_contacts_resolveUsername req = new TLRPC.TL_contacts_resolveUsername();
            MessagesController instance = MessagesController.getInstance(this.currentAccount);
            req.username = gif ? instance.gifSearchBot : instance.imageSearchBot;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate(gif) {
                private final /* synthetic */ boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    PhotoPickerActivity.this.lambda$searchBotUser$5$PhotoPickerActivity(this.f$1, tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$searchBotUser$5$PhotoPickerActivity(boolean gif, TLObject response, TLRPC.TL_error error) {
        this.searchingUser = false;
        if (response != null) {
            AndroidUtilities.runOnUIThread(new Runnable(response, gif) {
                private final /* synthetic */ TLObject f$1;
                private final /* synthetic */ boolean f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    PhotoPickerActivity.this.lambda$null$4$PhotoPickerActivity(this.f$1, this.f$2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$4$PhotoPickerActivity(TLObject response, boolean gif) {
        TLRPC.TL_contacts_resolvedPeer res = (TLRPC.TL_contacts_resolvedPeer) response;
        MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
        MessagesController.getInstance(this.currentAccount).putChats(res.chats, false);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
        String str = this.lastSearchImageString;
        this.lastSearchImageString = null;
        searchImages(gif, str, "", false);
    }

    /* access modifiers changed from: private */
    public void searchImages(boolean gif, String query, String offset, boolean searchUser) {
        if (this.searching) {
            this.searching = false;
            if (this.imageReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.imageReqId, true);
                this.imageReqId = 0;
            }
        }
        this.lastSearchImageString = query;
        this.searching = true;
        MessagesController instance = MessagesController.getInstance(this.currentAccount);
        MessagesController instance2 = MessagesController.getInstance(this.currentAccount);
        TLObject object = instance.getUserOrChat(gif ? instance2.gifSearchBot : instance2.imageSearchBot);
        if (object instanceof TLRPC.User) {
            TLRPC.User user = (TLRPC.User) object;
            TLRPC.TL_messages_getInlineBotResults req = new TLRPC.TL_messages_getInlineBotResults();
            req.query = query == null ? "" : query;
            req.bot = MessagesController.getInstance(this.currentAccount).getInputUser(user);
            req.offset = offset;
            ChatActivity chatActivity2 = this.chatActivity;
            if (chatActivity2 != null) {
                long dialogId = chatActivity2.getDialogId();
                int lower_id = (int) dialogId;
                int i = (int) (dialogId >> 32);
                if (lower_id != 0) {
                    req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(lower_id);
                } else {
                    req.peer = new TLRPC.TL_inputPeerEmpty();
                }
            } else {
                req.peer = new TLRPC.TL_inputPeerEmpty();
            }
            int token = this.lastSearchToken + 1;
            this.lastSearchToken = token;
            this.imageReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate(token, gif, user) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ boolean f$2;
                private final /* synthetic */ TLRPC.User f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    PhotoPickerActivity.this.lambda$searchImages$7$PhotoPickerActivity(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
                }
            });
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(this.imageReqId, this.classGuid);
        } else if (searchUser) {
            searchBotUser(gif);
        }
    }

    public /* synthetic */ void lambda$searchImages$7$PhotoPickerActivity(int token, boolean gif, TLRPC.User user, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(token, response, gif, user) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ boolean f$3;
            private final /* synthetic */ TLRPC.User f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                PhotoPickerActivity.this.lambda$null$6$PhotoPickerActivity(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$null$6$PhotoPickerActivity(int token, TLObject response, boolean gif, TLRPC.User user) {
        TLRPC.PhotoSize size;
        TLRPC.DocumentAttribute attribute;
        boolean z = gif;
        if (token == this.lastSearchToken) {
            int addedCount = 0;
            int oldCount = this.searchResult.size();
            if (response != null) {
                TLRPC.messages_BotResults res = (TLRPC.messages_BotResults) response;
                this.nextImagesSearchOffset = res.next_offset;
                int count = res.results.size();
                for (int a = 0; a < count; a++) {
                    TLRPC.BotInlineResult result = res.results.get(a);
                    if ((z || "photo".equals(result.type)) && ((!z || "gif".equals(result.type)) && !this.searchResultKeys.containsKey(result.id))) {
                        MediaController.SearchImage image = new MediaController.SearchImage();
                        if (z && result.document != null) {
                            int b = 0;
                            while (true) {
                                if (b >= result.document.attributes.size()) {
                                    break;
                                }
                                attribute = result.document.attributes.get(b);
                                if ((attribute instanceof TLRPC.TL_documentAttributeImageSize) || (attribute instanceof TLRPC.TL_documentAttributeVideo)) {
                                    image.width = attribute.w;
                                    image.height = attribute.h;
                                } else {
                                    b++;
                                }
                            }
                            image.width = attribute.w;
                            image.height = attribute.h;
                            image.document = result.document;
                            image.size = 0;
                            if (!(result.photo == null || result.document == null || (size = FileLoader.getClosestPhotoSizeWithSize(result.photo.sizes, this.itemSize, true)) == null)) {
                                result.document.thumbs.add(size);
                                result.document.flags |= 1;
                            }
                        } else if (!z && result.photo != null) {
                            TLRPC.PhotoSize size2 = FileLoader.getClosestPhotoSizeWithSize(result.photo.sizes, AndroidUtilities.getPhotoSize());
                            TLRPC.PhotoSize size22 = FileLoader.getClosestPhotoSizeWithSize(result.photo.sizes, 320);
                            if (size2 != null) {
                                image.width = size2.w;
                                image.height = size2.h;
                                image.photoSize = size2;
                                image.photo = result.photo;
                                image.size = size2.size;
                                image.thumbPhotoSize = size22;
                            }
                        } else if (result.content != null) {
                            int b2 = 0;
                            while (true) {
                                if (b2 >= result.content.attributes.size()) {
                                    break;
                                }
                                TLRPC.DocumentAttribute attribute2 = result.content.attributes.get(b2);
                                if (attribute2 instanceof TLRPC.TL_documentAttributeImageSize) {
                                    image.width = attribute2.w;
                                    image.height = attribute2.h;
                                    break;
                                }
                                b2++;
                            }
                            if (result.thumb != null) {
                                image.thumbUrl = result.thumb.url;
                            } else {
                                image.thumbUrl = null;
                            }
                            image.imageUrl = result.content.url;
                            image.size = z ? 0 : result.content.size;
                        }
                        image.id = result.id;
                        image.type = z ? 1 : 0;
                        image.inlineResult = result;
                        image.params = new HashMap<>();
                        image.params.put(TtmlNode.ATTR_ID, result.id);
                        image.params.put("query_id", "" + res.query_id);
                        image.params.put("bot_name", user.username);
                        this.searchResult.add(image);
                        this.searchResultKeys.put(image.id, image);
                        addedCount++;
                    }
                    TLRPC.User user2 = user;
                }
                TLRPC.User user3 = user;
                this.imageSearchEndReached = oldCount == this.searchResult.size() || this.nextImagesSearchOffset == null;
            } else {
                TLRPC.User user4 = user;
            }
            this.searching = false;
            if (addedCount != 0) {
                this.listAdapter.notifyItemRangeInserted(oldCount, addedCount);
            } else if (this.imageSearchEndReached) {
                this.listAdapter.notifyItemRemoved(this.searchResult.size() - 1);
            }
            if ((!this.searching || !this.searchResult.isEmpty()) && (!this.loadingRecent || this.lastSearchString != null)) {
                this.emptyView.showTextView();
            } else {
                this.emptyView.showProgress();
            }
        }
    }

    public void setDelegate(PhotoPickerActivityDelegate delegate2) {
        this.delegate = delegate2;
    }

    public void setFCDelegate(FCPhotoPickerActivityDelegate mFCPhotoPickerActivityDelegate2) {
        this.mFCPhotoPickerActivityDelegate = mFCPhotoPickerActivityDelegate2;
    }

    /* access modifiers changed from: private */
    public void sendSelectedPhotos(boolean notify, int scheduleDate) {
        if (!this.selectedPhotos.isEmpty() && this.delegate != null && !this.sendPressed) {
            applyCaption();
            this.sendPressed = true;
            this.delegate.actionButtonPressed(false, notify, scheduleDate, this.mblnSendOriginal);
            if (this.selectPhotoType != 2) {
                finishFragment();
            }
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            if (PhotoPickerActivity.this.selectedAlbum != null) {
                return true;
            }
            int position = holder.getAdapterPosition();
            if (!PhotoPickerActivity.this.searchResult.isEmpty() || PhotoPickerActivity.this.lastSearchString != null) {
                if (position < PhotoPickerActivity.this.searchResult.size()) {
                    return true;
                }
                return false;
            } else if (position < PhotoPickerActivity.this.recentImages.size()) {
                return true;
            } else {
                return false;
            }
        }

        public int getItemCount() {
            if (PhotoPickerActivity.this.selectedAlbum != null) {
                return PhotoPickerActivity.this.selectedAlbum.photos.size();
            }
            if (PhotoPickerActivity.this.searchResult.isEmpty()) {
                return 0;
            }
            return PhotoPickerActivity.this.searchResult.size() + (PhotoPickerActivity.this.imageSearchEndReached ^ true ? 1 : 0);
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType != 0) {
                FrameLayout frameLayout = new FrameLayout(this.mContext);
                view = frameLayout;
                RadialProgressView progressBar = new RadialProgressView(this.mContext);
                progressBar.setProgressColor(-11371101);
                frameLayout.addView(progressBar, LayoutHelper.createFrame(-1, -1.0f));
            } else {
                PhotoAttachPhotoCell cell = new PhotoAttachPhotoCell(this.mContext);
                cell.setDelegate(new PhotoAttachPhotoCell.PhotoAttachPhotoCellDelegate() {
                    private void checkSlowMode() {
                        TLRPC.Chat chat;
                        if (PhotoPickerActivity.this.allowOrder && PhotoPickerActivity.this.chatActivity != null && (chat = PhotoPickerActivity.this.chatActivity.getCurrentChat()) != null && !ChatObject.hasAdminRights(chat) && chat.slowmode_enabled && PhotoPickerActivity.this.alertOnlyOnce != 2) {
                            AlertsCreator.showSimpleAlert(PhotoPickerActivity.this, LocaleController.getString("Slowmode", R.string.Slowmode), LocaleController.getString("SlowmodeSelectSendError", R.string.SlowmodeSelectSendError));
                            if (PhotoPickerActivity.this.alertOnlyOnce == 1) {
                                int unused = PhotoPickerActivity.this.alertOnlyOnce = 2;
                            }
                        }
                    }

                    public void onCheckClick(PhotoAttachPhotoCell v) {
                        boolean added;
                        MediaController.SearchImage photoEntry;
                        int index = ((Integer) v.getTag()).intValue();
                        int num = -1;
                        int i = 1;
                        if (PhotoPickerActivity.this.selectedAlbum != null) {
                            MediaController.PhotoEntry photoEntry2 = PhotoPickerActivity.this.selectedAlbum.photos.get(index);
                            added = !PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(photoEntry2.imageId));
                            if (!added || PhotoPickerActivity.this.maxSelectedPhotos <= 0 || PhotoPickerActivity.this.selectedPhotos.size() < PhotoPickerActivity.this.maxSelectedPhotos) {
                                if (PhotoPickerActivity.this.allowIndices && added) {
                                    num = PhotoPickerActivity.this.selectedPhotosOrder.size();
                                }
                                v.setChecked(num, added, true);
                                int unused = PhotoPickerActivity.this.addToSelectedPhotos(photoEntry2, index);
                            } else {
                                checkSlowMode();
                                return;
                            }
                        } else {
                            AndroidUtilities.hideKeyboard(PhotoPickerActivity.this.getParentActivity().getCurrentFocus());
                            if (!PhotoPickerActivity.this.searchResult.isEmpty() || PhotoPickerActivity.this.lastSearchString != null) {
                                photoEntry = (MediaController.SearchImage) PhotoPickerActivity.this.searchResult.get(index);
                            } else {
                                photoEntry = (MediaController.SearchImage) PhotoPickerActivity.this.recentImages.get(index);
                            }
                            added = !PhotoPickerActivity.this.selectedPhotos.containsKey(photoEntry.id);
                            if (!added || PhotoPickerActivity.this.maxSelectedPhotos <= 0 || PhotoPickerActivity.this.selectedPhotos.size() < PhotoPickerActivity.this.maxSelectedPhotos) {
                                if (PhotoPickerActivity.this.allowIndices && added) {
                                    num = PhotoPickerActivity.this.selectedPhotosOrder.size();
                                }
                                v.setChecked(num, added, true);
                                int unused2 = PhotoPickerActivity.this.addToSelectedPhotos(photoEntry, index);
                            } else {
                                checkSlowMode();
                                return;
                            }
                        }
                        PhotoPickerActivity photoPickerActivity = PhotoPickerActivity.this;
                        if (!added) {
                            i = 2;
                        }
                        photoPickerActivity.updatePhotosButton(i);
                        PhotoPickerActivity.this.delegate.selectedPhotosChanged();
                    }
                });
                cell.getCheckFrame().setVisibility(PhotoPickerActivity.this.selectPhotoType != 0 ? 8 : 0);
                view = cell;
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean showing;
            MediaController.SearchImage photoEntry;
            ViewGroup.LayoutParams params;
            int itemViewType = holder.getItemViewType();
            if (itemViewType == 0) {
                PhotoAttachPhotoCell cell = (PhotoAttachPhotoCell) holder.itemView;
                cell.setItemSize(PhotoPickerActivity.this.itemSize);
                BackupImageView imageView = cell.getImageView();
                cell.setTag(Integer.valueOf(position));
                int i = 0;
                imageView.setOrientation(0, true);
                int i2 = -1;
                if (PhotoPickerActivity.this.selectedAlbum != null) {
                    MediaController.PhotoEntry photoEntry2 = PhotoPickerActivity.this.selectedAlbum.photos.get(position);
                    cell.setPhotoEntry(photoEntry2, true, false);
                    if (PhotoPickerActivity.this.allowIndices) {
                        i2 = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry2.imageId));
                    }
                    cell.setChecked(i2, PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(photoEntry2.imageId)), false);
                    showing = PhotoViewer.isShowingImage(photoEntry2.path);
                } else {
                    if (!PhotoPickerActivity.this.searchResult.isEmpty() || PhotoPickerActivity.this.lastSearchString != null) {
                        photoEntry = (MediaController.SearchImage) PhotoPickerActivity.this.searchResult.get(position);
                    } else {
                        photoEntry = (MediaController.SearchImage) PhotoPickerActivity.this.recentImages.get(position);
                    }
                    cell.setPhotoEntry(photoEntry, true, false);
                    cell.getVideoInfoContainer().setVisibility(4);
                    if (PhotoPickerActivity.this.allowIndices) {
                        i2 = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(photoEntry.id);
                    }
                    cell.setChecked(i2, PhotoPickerActivity.this.selectedPhotos.containsKey(photoEntry.id), false);
                    showing = PhotoViewer.isShowingImage(photoEntry.getPathToAttach());
                }
                imageView.getImageReceiver().setVisible(!showing, true);
                CheckBox2 checkBox = cell.getCheckBox();
                if (PhotoPickerActivity.this.selectPhotoType != 0 || showing) {
                    i = 8;
                }
                checkBox.setVisibility(i);
            } else if (itemViewType == 1 && (params = holder.itemView.getLayoutParams()) != null) {
                params.width = PhotoPickerActivity.this.itemSize;
                params.height = PhotoPickerActivity.this.itemSize;
                holder.itemView.setLayoutParams(params);
            }
        }

        public int getItemViewType(int i) {
            if (PhotoPickerActivity.this.selectedAlbum != null) {
                return 0;
            }
            if ((!PhotoPickerActivity.this.searchResult.isEmpty() || PhotoPickerActivity.this.lastSearchString != null || i >= PhotoPickerActivity.this.recentImages.size()) && i >= PhotoPickerActivity.this.searchResult.size()) {
                return 1;
            }
            return 0;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        EditTextBoldCursor editTextBoldCursor;
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[11];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogBackground);
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogBackground);
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogTextBlack);
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogTextBlack);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogButtonSelector);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogTextBlack);
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_messagePanelHint);
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        if (actionBarMenuItem != null) {
            editTextBoldCursor = actionBarMenuItem.getSearchField();
        } else {
            editTextBoldCursor = null;
        }
        themeDescriptionArr[7] = new ThemeDescription(editTextBoldCursor, ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogTextBlack);
        themeDescriptionArr[8] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogBackground);
        themeDescriptionArr[9] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, (Paint) null, new Drawable[]{Theme.chat_attachEmptyDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_attachEmptyImage);
        themeDescriptionArr[10] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_attachPhotoBackground);
        return themeDescriptionArr;
    }
}
