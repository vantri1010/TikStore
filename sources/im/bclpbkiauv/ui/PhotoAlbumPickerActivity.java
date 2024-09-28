package im.bclpbkiauv.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SendMessagesHelper;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.PhotoAlbumPickerActivity;
import im.bclpbkiauv.ui.PhotoPickerActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenu;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuSubItem;
import im.bclpbkiauv.ui.actionbar.ActionBarPopupWindow;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.PhotoPickerAlbumsCell;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.CombinedDrawable;
import im.bclpbkiauv.ui.components.EditTextBoldCursor;
import im.bclpbkiauv.ui.components.EditTextEmoji;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RadialProgressView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.SizeNotifierFrameLayout;
import java.util.ArrayList;
import java.util.HashMap;

public class PhotoAlbumPickerActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public ArrayList<MediaController.AlbumEntry> albumsSorted = null;
    private boolean allowCaption;
    private boolean allowGifs;
    private boolean allowOrder = true;
    private boolean allowSearchImages = true;
    /* access modifiers changed from: private */
    public CharSequence caption;
    private ChatActivity chatActivity;
    /* access modifiers changed from: private */
    public int columnsCount = 2;
    /* access modifiers changed from: private */
    public EditTextEmoji commentTextView;
    /* access modifiers changed from: private */
    public PhotoAlbumPickerActivityDelegate delegate;
    private TextView emptyView;
    private FrameLayout frameLayout2;
    boolean isFcCrop;
    private ActionBarMenuSubItem[] itemCells;
    private ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private boolean loading = false;
    PhotoPickerActivity.FCPhotoPickerActivityDelegate mFCPhotoPickerActivityDelegate;
    private int maxSelectedPhotos;
    /* access modifiers changed from: private */
    public Paint paint = new Paint(1);
    private FrameLayout progressView;
    /* access modifiers changed from: private */
    public RectF rect = new RectF();
    private int selectPhotoType;
    private View selectedCountView;
    /* access modifiers changed from: private */
    public HashMap<Object, Object> selectedPhotos = new HashMap<>();
    /* access modifiers changed from: private */
    public ArrayList<Object> selectedPhotosOrder = new ArrayList<>();
    private ActionBarPopupWindow.ActionBarPopupWindowLayout sendPopupLayout;
    /* access modifiers changed from: private */
    public ActionBarPopupWindow sendPopupWindow;
    private boolean sendPressed;
    private View shadow;
    private SizeNotifierFrameLayout sizeNotifierFrameLayout;
    /* access modifiers changed from: private */
    public TextPaint textPaint = new TextPaint(1);
    private ImageView writeButton;
    private FrameLayout writeButtonContainer;
    private Drawable writeButtonDrawable;

    public interface PhotoAlbumPickerActivityDelegate {
        void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList, boolean z, int i, boolean z2);

        void startPhotoSelectActivity();
    }

    public PhotoAlbumPickerActivity(int selectPhotoType2, boolean allowGifs2, boolean allowCaption2, ChatActivity chatActivity2) {
        this.chatActivity = chatActivity2;
        this.selectPhotoType = selectPhotoType2;
        this.allowGifs = allowGifs2;
        this.allowCaption = allowCaption2;
    }

    public PhotoAlbumPickerActivity(int selectPhotoType2, boolean allowGifs2, boolean allowCaption2, ChatActivity chatActivity2, boolean isFcCrop2) {
        this.chatActivity = chatActivity2;
        this.selectPhotoType = selectPhotoType2;
        this.allowGifs = allowGifs2;
        this.allowCaption = allowCaption2;
        this.isFcCrop = isFcCrop2;
    }

    public PhotoAlbumPickerActivity(int selectPhotoType2, boolean allowGifs2, boolean allowCaption2, ChatActivity chatActivity2, boolean isFcCrop2, PhotoPickerActivity.FCPhotoPickerActivityDelegate mFCPhotoPickerActivityDelegate2) {
        this.chatActivity = chatActivity2;
        this.selectPhotoType = selectPhotoType2;
        this.allowGifs = allowGifs2;
        this.allowCaption = allowCaption2;
        this.isFcCrop = isFcCrop2;
        this.mFCPhotoPickerActivityDelegate = mFCPhotoPickerActivityDelegate2;
    }

    public boolean onFragmentCreate() {
        if (this.selectPhotoType != 0 || !this.allowSearchImages) {
            this.albumsSorted = MediaController.allPhotoAlbums;
        } else {
            this.albumsSorted = MediaController.allMediaAlbums;
        }
        ArrayList<MediaController.AlbumEntry> arrayList = this.albumsSorted;
        this.loading = arrayList == null || arrayList.isEmpty();
        MediaController.loadGalleryPhotosAlbums(this.classGuid);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.albumsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.albumsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
        ArrayList<MediaController.AlbumEntry> arrayList;
        Context context2 = context;
        this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        this.actionBar.setTitleColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.actionBar.setItemsColor(Theme.getColor(Theme.key_dialogTextBlack), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_dialogButtonSelector), false);
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    PhotoAlbumPickerActivity.this.finishFragment();
                } else if (id == 1) {
                    if (PhotoAlbumPickerActivity.this.delegate != null) {
                        PhotoAlbumPickerActivity.this.finishFragment(false);
                        PhotoAlbumPickerActivity.this.delegate.startPhotoSelectActivity();
                    }
                } else if (id == 2) {
                    PhotoAlbumPickerActivity.this.openPhotoPicker((MediaController.AlbumEntry) null, 0);
                }
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        if (!this.isFcCrop) {
            if (this.allowSearchImages) {
                menu.addItem(2, (int) R.drawable.ic_ab_search).setContentDescription(LocaleController.getString("Search", R.string.Search));
            }
            menu.addItem(1, (int) R.drawable.ic_ab_other).setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
        }
        AnonymousClass2 r4 = new SizeNotifierFrameLayout(context2) {
            private boolean ignoreLayout;
            private int lastNotifyWidth;

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
                int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(widthSize, heightSize);
                if (getKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
                    this.ignoreLayout = true;
                    PhotoAlbumPickerActivity.this.commentTextView.hideEmojiView();
                    this.ignoreLayout = false;
                } else if (!AndroidUtilities.isInMultiwindow) {
                    heightSize -= PhotoAlbumPickerActivity.this.commentTextView.getEmojiPadding();
                    heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(heightSize, 1073741824);
                }
                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (!(child == null || child.getVisibility() == 8)) {
                        if (PhotoAlbumPickerActivity.this.commentTextView == null || !PhotoAlbumPickerActivity.this.commentTextView.isPopupView(child)) {
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
                    if (PhotoAlbumPickerActivity.this.sendPopupWindow != null && PhotoAlbumPickerActivity.this.sendPopupWindow.isShowing()) {
                        PhotoAlbumPickerActivity.this.sendPopupWindow.dismiss();
                    }
                }
                int count = getChildCount();
                int paddingBottom = (getKeyboardHeight() > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow || AndroidUtilities.isTablet()) ? 0 : PhotoAlbumPickerActivity.this.commentTextView.getEmojiPadding();
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
                        if (PhotoAlbumPickerActivity.this.commentTextView != null && PhotoAlbumPickerActivity.this.commentTextView.isPopupView(child)) {
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
        this.sizeNotifierFrameLayout = r4;
        r4.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        this.fragmentView = this.sizeNotifierFrameLayout;
        this.actionBar.setTitle(LocaleController.getString("Gallery", R.string.Gallery));
        RecyclerListView recyclerListView = new RecyclerListView(context2);
        this.listView = recyclerListView;
        recyclerListView.setPadding(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(54.0f));
        this.listView.setClipToPadding(false);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context2, 1, false));
        this.listView.setDrawingCacheEnabled(false);
        this.sizeNotifierFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter2 = new ListAdapter(context2);
        this.listAdapter = listAdapter2;
        recyclerListView2.setAdapter(listAdapter2);
        this.listView.setGlowColor(Theme.getColor(Theme.key_dialogBackground));
        TextView textView = new TextView(context2);
        this.emptyView = textView;
        textView.setTextColor(-8355712);
        this.emptyView.setTextSize(20.0f);
        this.emptyView.setGravity(17);
        this.emptyView.setVisibility(8);
        this.emptyView.setText(LocaleController.getString("NoPhotos", R.string.NoPhotos));
        this.sizeNotifierFrameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1.0f, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        this.emptyView.setOnTouchListener($$Lambda$PhotoAlbumPickerActivity$h6g_OJwXRna07dkyrpWX2tn5xLM.INSTANCE);
        FrameLayout frameLayout = new FrameLayout(context2);
        this.progressView = frameLayout;
        frameLayout.setVisibility(8);
        this.sizeNotifierFrameLayout.addView(this.progressView, LayoutHelper.createFrame(-1.0f, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        RadialProgressView progressBar = new RadialProgressView(context2);
        progressBar.setProgressColor(-11371101);
        this.progressView.addView(progressBar, LayoutHelper.createFrame(-2, -2, 17));
        View view = new View(context2);
        this.shadow = view;
        view.setBackgroundResource(R.drawable.header_shadow_reverse);
        this.shadow.setTranslationY((float) AndroidUtilities.dp(48.0f));
        this.sizeNotifierFrameLayout.addView(this.shadow, LayoutHelper.createFrame(-1.0f, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        FrameLayout frameLayout3 = new FrameLayout(context2);
        this.frameLayout2 = frameLayout3;
        frameLayout3.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        this.frameLayout2.setVisibility(4);
        this.frameLayout2.setTranslationY((float) AndroidUtilities.dp(48.0f));
        this.sizeNotifierFrameLayout.addView(this.frameLayout2, LayoutHelper.createFrame(-1, 48, 83));
        this.frameLayout2.setOnTouchListener($$Lambda$PhotoAlbumPickerActivity$1IEcaszU79Z4K82hY9a8IApLYI.INSTANCE);
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
        this.commentTextView = new EditTextEmoji(context2, this.sizeNotifierFrameLayout, (BaseFragment) null, 1);
        this.commentTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MessagesController.getInstance(UserConfig.selectedAccount).maxCaptionLength)});
        this.commentTextView.setHint(LocaleController.getString("AddCaption", R.string.AddCaption));
        EditTextBoldCursor editText = this.commentTextView.getEditText();
        editText.setMaxLines(1);
        editText.setSingleLine(true);
        this.frameLayout2.addView(this.commentTextView, LayoutHelper.createFrame(-1.0f, -1.0f, 51, 0.0f, 0.0f, 84.0f, 0.0f));
        CharSequence charSequence = this.caption;
        if (charSequence != null) {
            this.commentTextView.setText(charSequence);
        }
        FrameLayout frameLayout4 = new FrameLayout(context2);
        this.writeButtonContainer = frameLayout4;
        frameLayout4.setVisibility(4);
        this.writeButtonContainer.setScaleX(0.2f);
        this.writeButtonContainer.setScaleY(0.2f);
        this.writeButtonContainer.setAlpha(0.0f);
        this.writeButtonContainer.setContentDescription(LocaleController.getString("Send", R.string.Send));
        this.sizeNotifierFrameLayout.addView(this.writeButtonContainer, LayoutHelper.createFrame(60.0f, 60.0f, 85, 0.0f, 0.0f, 6.0f, 10.0f));
        this.writeButtonContainer.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PhotoAlbumPickerActivity.this.lambda$createView$3$PhotoAlbumPickerActivity(view);
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
        AnonymousClass4 r6 = new View(context2) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                String text = String.format("%d", new Object[]{Integer.valueOf(Math.max(1, PhotoAlbumPickerActivity.this.selectedPhotosOrder.size()))});
                int textSize = (int) Math.ceil((double) PhotoAlbumPickerActivity.this.textPaint.measureText(text));
                int size = Math.max(AndroidUtilities.dp(16.0f) + textSize, AndroidUtilities.dp(24.0f));
                int cx = getMeasuredWidth() / 2;
                int measuredHeight = getMeasuredHeight() / 2;
                PhotoAlbumPickerActivity.this.textPaint.setColor(Theme.getColor(Theme.key_dialogRoundCheckBoxCheck));
                PhotoAlbumPickerActivity.this.paint.setColor(Theme.getColor(Theme.key_dialogBackground));
                PhotoAlbumPickerActivity.this.rect.set((float) (cx - (size / 2)), 0.0f, (float) ((size / 2) + cx), (float) getMeasuredHeight());
                canvas.drawRoundRect(PhotoAlbumPickerActivity.this.rect, (float) AndroidUtilities.dp(12.0f), (float) AndroidUtilities.dp(12.0f), PhotoAlbumPickerActivity.this.paint);
                PhotoAlbumPickerActivity.this.paint.setColor(Theme.getColor(Theme.key_dialogRoundCheckBox));
                PhotoAlbumPickerActivity.this.rect.set((float) ((cx - (size / 2)) + AndroidUtilities.dp(2.0f)), (float) AndroidUtilities.dp(2.0f), (float) (((size / 2) + cx) - AndroidUtilities.dp(2.0f)), (float) (getMeasuredHeight() - AndroidUtilities.dp(2.0f)));
                canvas.drawRoundRect(PhotoAlbumPickerActivity.this.rect, (float) AndroidUtilities.dp(10.0f), (float) AndroidUtilities.dp(10.0f), PhotoAlbumPickerActivity.this.paint);
                canvas.drawText(text, (float) (cx - (textSize / 2)), (float) AndroidUtilities.dp(16.2f), PhotoAlbumPickerActivity.this.textPaint);
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
        if (!this.loading || ((arrayList = this.albumsSorted) != null && (arrayList == null || !arrayList.isEmpty()))) {
            this.progressView.setVisibility(8);
            this.listView.setEmptyView(this.emptyView);
        } else {
            this.progressView.setVisibility(0);
            this.listView.setEmptyView((View) null);
        }
        return this.fragmentView;
    }

    static /* synthetic */ boolean lambda$createView$0(View v, MotionEvent event) {
        return true;
    }

    static /* synthetic */ boolean lambda$createView$1(View v, MotionEvent event) {
        return true;
    }

    public /* synthetic */ void lambda$createView$3$PhotoAlbumPickerActivity(View v) {
        ChatActivity chatActivity2 = this.chatActivity;
        if (chatActivity2 == null || !chatActivity2.isInScheduleMode()) {
            sendSelectedPhotos(this.selectedPhotos, this.selectedPhotosOrder, true, 0, false);
            finishFragment();
            return;
        }
        AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), UserObject.isUserSelf(this.chatActivity.getCurrentUser()), new AlertsCreator.ScheduleDatePickerDelegate() {
            public final void didSelectDate(boolean z, int i) {
                PhotoAlbumPickerActivity.this.lambda$null$2$PhotoAlbumPickerActivity(z, i);
            }
        });
    }

    public /* synthetic */ void lambda$null$2$PhotoAlbumPickerActivity(boolean notify, int scheduleDate) {
        sendSelectedPhotos(this.selectedPhotos, this.selectedPhotosOrder, notify, scheduleDate, false);
        finishFragment();
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
        fixLayout();
    }

    public void onPause() {
        super.onPause();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fixLayout();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.albumsDidLoad) {
            if (this.classGuid == args[0].intValue()) {
                if (this.selectPhotoType != 0 || !this.allowSearchImages) {
                    this.albumsSorted = args[2];
                } else {
                    this.albumsSorted = args[1];
                }
                FrameLayout frameLayout = this.progressView;
                if (frameLayout != null) {
                    frameLayout.setVisibility(8);
                }
                RecyclerListView recyclerListView = this.listView;
                if (recyclerListView != null && recyclerListView.getEmptyView() == null) {
                    this.listView.setEmptyView(this.emptyView);
                }
                ListAdapter listAdapter2 = this.listAdapter;
                if (listAdapter2 != null) {
                    listAdapter2.notifyDataSetChanged();
                }
                this.loading = false;
            }
        } else if (id == NotificationCenter.closeChats) {
            removeSelfFromStack();
        }
    }

    public void setMaxSelectedPhotos(int value, boolean order) {
        this.maxSelectedPhotos = value;
        this.allowOrder = order;
    }

    public void setAllowSearchImages(boolean value) {
        this.allowSearchImages = value;
    }

    public void setDelegate(PhotoAlbumPickerActivityDelegate delegate2) {
        this.delegate = delegate2;
    }

    /* access modifiers changed from: private */
    public void sendSelectedPhotos(HashMap<Object, Object> photos, ArrayList<Object> order, boolean notify, int scheduleDate, boolean blnOriginalImg) {
        if (!photos.isEmpty() && this.delegate != null && !this.sendPressed) {
            this.sendPressed = true;
            ArrayList<SendMessagesHelper.SendingMediaInfo> media = new ArrayList<>();
            for (int a = 0; a < order.size(); a++) {
                Object object = photos.get(order.get(a));
                SendMessagesHelper.SendingMediaInfo info = new SendMessagesHelper.SendingMediaInfo();
                media.add(info);
                ArrayList<TLRPC.InputDocument> arrayList = null;
                if (object instanceof MediaController.PhotoEntry) {
                    MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) object;
                    if (photoEntry.isVideo) {
                        info.path = photoEntry.path;
                        info.videoEditedInfo = photoEntry.editedInfo;
                    } else if (photoEntry.imagePath != null) {
                        info.path = photoEntry.imagePath;
                    } else if (photoEntry.path != null) {
                        info.path = photoEntry.path;
                    }
                    info.isVideo = photoEntry.isVideo;
                    info.caption = photoEntry.caption != null ? photoEntry.caption.toString() : null;
                    info.entities = photoEntry.entities;
                    if (!photoEntry.stickers.isEmpty()) {
                        arrayList = new ArrayList<>(photoEntry.stickers);
                    }
                    info.masks = arrayList;
                    info.ttl = photoEntry.ttl;
                } else if (object instanceof MediaController.SearchImage) {
                    MediaController.SearchImage searchImage = (MediaController.SearchImage) object;
                    if (searchImage.imagePath != null) {
                        info.path = searchImage.imagePath;
                    } else {
                        info.searchImage = searchImage;
                    }
                    info.caption = searchImage.caption != null ? searchImage.caption.toString() : null;
                    info.entities = searchImage.entities;
                    if (!searchImage.stickers.isEmpty()) {
                        arrayList = new ArrayList<>(searchImage.stickers);
                    }
                    info.masks = arrayList;
                    info.ttl = searchImage.ttl;
                    if (searchImage.inlineResult != null && searchImage.type == 1) {
                        info.inlineResult = searchImage.inlineResult;
                        info.params = searchImage.params;
                    }
                    searchImage.date = (int) (System.currentTimeMillis() / 1000);
                }
            }
            this.delegate.didSelectPhotos(media, notify, scheduleDate, blnOriginalImg);
        }
    }

    private void fixLayout() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            recyclerListView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    PhotoAlbumPickerActivity.this.fixLayoutInternal();
                    if (PhotoAlbumPickerActivity.this.listView == null) {
                        return true;
                    }
                    PhotoAlbumPickerActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
        }
    }

    private void applyCaption() {
        if (this.commentTextView.length() > 0) {
            Object entry = this.selectedPhotos.get(Integer.valueOf(((Integer) this.selectedPhotosOrder.get(0)).intValue()));
            if (entry instanceof MediaController.PhotoEntry) {
                ((MediaController.PhotoEntry) entry).caption = this.commentTextView.getText().toString();
            } else if (entry instanceof MediaController.SearchImage) {
                ((MediaController.SearchImage) entry).caption = this.commentTextView.getText().toString();
            }
        }
    }

    /* access modifiers changed from: private */
    public void fixLayoutInternal() {
        if (getParentActivity() != null) {
            int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
            this.columnsCount = 2;
            if (!AndroidUtilities.isTablet() && (rotation == 3 || rotation == 1)) {
                this.columnsCount = 4;
            }
            this.listAdapter.notifyDataSetChanged();
        }
    }

    private boolean showCommentTextView(boolean show) {
        if (show == (this.frameLayout2.getTag() != null)) {
            return false;
        }
        this.frameLayout2.setTag(show ? 1 : null);
        if (this.commentTextView.getEditText().isFocused()) {
            AndroidUtilities.hideKeyboard(this.commentTextView.getEditText());
        }
        this.commentTextView.hidePopup(true);
        if (show) {
            this.frameLayout2.setVisibility(0);
            this.writeButtonContainer.setVisibility(0);
        } else {
            this.frameLayout2.setVisibility(4);
            this.writeButtonContainer.setVisibility(4);
        }
        float f = 0.2f;
        float f2 = 1.0f;
        this.writeButtonContainer.setScaleX(show ? 1.0f : 0.2f);
        this.writeButtonContainer.setScaleY(show ? 1.0f : 0.2f);
        float f3 = 0.0f;
        this.writeButtonContainer.setAlpha(show ? 1.0f : 0.0f);
        this.selectedCountView.setScaleX(show ? 1.0f : 0.2f);
        View view = this.selectedCountView;
        if (show) {
            f = 1.0f;
        }
        view.setScaleY(f);
        View view2 = this.selectedCountView;
        if (!show) {
            f2 = 0.0f;
        }
        view2.setAlpha(f2);
        this.frameLayout2.setTranslationY(show ? 0.0f : (float) AndroidUtilities.dp(48.0f));
        View view3 = this.shadow;
        if (!show) {
            f3 = (float) AndroidUtilities.dp(48.0f);
        }
        view3.setTranslationY(f3);
        return true;
    }

    /* access modifiers changed from: private */
    public void updatePhotosButton() {
        if (this.selectedPhotos.size() == 0) {
            this.selectedCountView.setPivotX(0.0f);
            this.selectedCountView.setPivotY(0.0f);
            showCommentTextView(false);
            return;
        }
        this.selectedCountView.invalidate();
        showCommentTextView(true);
    }

    /* access modifiers changed from: private */
    public void openPhotoPicker(MediaController.AlbumEntry albumEntry, int type) {
        if (albumEntry == null) {
            final HashMap<Object, Object> photos = new HashMap<>();
            final ArrayList<Object> order = new ArrayList<>();
            if (this.allowGifs) {
                PhotoPickerSearchActivity fragment = new PhotoPickerSearchActivity(photos, order, (ArrayList<MediaController.SearchImage>) null, this.selectPhotoType, this.allowCaption, this.chatActivity);
                Editable text = this.commentTextView.getText();
                this.caption = text;
                fragment.setCaption(text);
                fragment.setDelegate(new PhotoPickerActivity.PhotoPickerActivityDelegate() {
                    public void selectedPhotosChanged() {
                    }

                    public void actionButtonPressed(boolean canceled, boolean notify, int scheduleDate, boolean blnOriginalImg) {
                        PhotoAlbumPickerActivity.this.removeSelfFromStack();
                        if (!canceled) {
                            PhotoAlbumPickerActivity.this.sendSelectedPhotos(photos, order, notify, scheduleDate, blnOriginalImg);
                        }
                    }

                    public void onCaptionChanged(CharSequence text) {
                        PhotoAlbumPickerActivity.this.commentTextView.setText(PhotoAlbumPickerActivity.this.caption = text);
                    }
                });
                fragment.setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
                presentFragment(fragment);
                return;
            }
            PhotoPickerActivity fragment2 = new PhotoPickerActivity(0, albumEntry, photos, order, (ArrayList<MediaController.SearchImage>) null, this.selectPhotoType, this.allowCaption, this.chatActivity);
            Editable text2 = this.commentTextView.getText();
            this.caption = text2;
            fragment2.setCaption(text2);
            fragment2.setDelegate(new PhotoPickerActivity.PhotoPickerActivityDelegate() {
                public void selectedPhotosChanged() {
                }

                public void actionButtonPressed(boolean canceled, boolean notify, int scheduleDate, boolean blnOriginalImg) {
                    PhotoAlbumPickerActivity.this.removeSelfFromStack();
                    if (!canceled) {
                        PhotoAlbumPickerActivity.this.sendSelectedPhotos(photos, order, notify, scheduleDate, blnOriginalImg);
                    }
                }

                public void onCaptionChanged(CharSequence text) {
                    PhotoAlbumPickerActivity.this.commentTextView.setText(PhotoAlbumPickerActivity.this.caption = text);
                }
            });
            fragment2.setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
            presentFragment(fragment2);
        } else if (this.isFcCrop) {
            PhotoPickerActivity photoPickerActivity = new PhotoPickerActivity(type, albumEntry, this.selectedPhotos, this.selectedPhotosOrder, (ArrayList<MediaController.SearchImage>) null, this.selectPhotoType, this.allowCaption, this.chatActivity, true);
            photoPickerActivity.setFCDelegate(this.mFCPhotoPickerActivityDelegate);
            Editable text3 = this.commentTextView.getText();
            this.caption = text3;
            photoPickerActivity.setCaption(text3);
            photoPickerActivity.setDelegate(new PhotoPickerActivity.PhotoPickerActivityDelegate() {
                public void selectedPhotosChanged() {
                    PhotoAlbumPickerActivity.this.updatePhotosButton();
                }

                public void actionButtonPressed(boolean canceled, boolean notify, int scheduleDate, boolean blnOriginalImg) {
                    PhotoAlbumPickerActivity.this.removeSelfFromStack();
                    if (!canceled) {
                        PhotoAlbumPickerActivity photoAlbumPickerActivity = PhotoAlbumPickerActivity.this;
                        photoAlbumPickerActivity.sendSelectedPhotos(photoAlbumPickerActivity.selectedPhotos, PhotoAlbumPickerActivity.this.selectedPhotosOrder, notify, scheduleDate, blnOriginalImg);
                    }
                }

                public void onCaptionChanged(CharSequence text) {
                    PhotoAlbumPickerActivity.this.commentTextView.setText(PhotoAlbumPickerActivity.this.caption = text);
                }
            });
            photoPickerActivity.setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
            presentFragment(photoPickerActivity);
        } else {
            PhotoPickerActivity photoPickerActivity2 = new PhotoPickerActivity(type, albumEntry, this.selectedPhotos, this.selectedPhotosOrder, (ArrayList<MediaController.SearchImage>) null, this.selectPhotoType, this.allowCaption, this.chatActivity);
            Editable text4 = this.commentTextView.getText();
            this.caption = text4;
            photoPickerActivity2.setCaption(text4);
            photoPickerActivity2.setDelegate(new PhotoPickerActivity.PhotoPickerActivityDelegate() {
                public void selectedPhotosChanged() {
                    PhotoAlbumPickerActivity.this.updatePhotosButton();
                }

                public void actionButtonPressed(boolean canceled, boolean notify, int scheduleDate, boolean blnOriginalImg) {
                    PhotoAlbumPickerActivity.this.removeSelfFromStack();
                    if (!canceled) {
                        PhotoAlbumPickerActivity photoAlbumPickerActivity = PhotoAlbumPickerActivity.this;
                        photoAlbumPickerActivity.sendSelectedPhotos(photoAlbumPickerActivity.selectedPhotos, PhotoAlbumPickerActivity.this.selectedPhotosOrder, notify, scheduleDate, blnOriginalImg);
                    }
                }

                public void onCaptionChanged(CharSequence text) {
                    PhotoAlbumPickerActivity.this.commentTextView.setText(PhotoAlbumPickerActivity.this.caption = text);
                }
            });
            photoPickerActivity2.setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
            presentFragment(photoPickerActivity2);
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        public int getItemCount() {
            if (PhotoAlbumPickerActivity.this.albumsSorted != null) {
                return (int) Math.ceil((double) (((float) PhotoAlbumPickerActivity.this.albumsSorted.size()) / ((float) PhotoAlbumPickerActivity.this.columnsCount)));
            }
            return 0;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            PhotoPickerAlbumsCell cell = new PhotoPickerAlbumsCell(this.mContext);
            cell.setDelegate(new PhotoPickerAlbumsCell.PhotoPickerAlbumsCellDelegate() {
                public final void didSelectAlbum(MediaController.AlbumEntry albumEntry) {
                    PhotoAlbumPickerActivity.ListAdapter.this.lambda$onCreateViewHolder$0$PhotoAlbumPickerActivity$ListAdapter(albumEntry);
                }
            });
            return new RecyclerListView.Holder(cell);
        }

        public /* synthetic */ void lambda$onCreateViewHolder$0$PhotoAlbumPickerActivity$ListAdapter(MediaController.AlbumEntry albumEntry) {
            PhotoAlbumPickerActivity.this.openPhotoPicker(albumEntry, 0);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            PhotoPickerAlbumsCell photoPickerAlbumsCell = (PhotoPickerAlbumsCell) holder.itemView;
            photoPickerAlbumsCell.setAlbumsCount(PhotoAlbumPickerActivity.this.columnsCount);
            for (int a = 0; a < PhotoAlbumPickerActivity.this.columnsCount; a++) {
                int index = (PhotoAlbumPickerActivity.this.columnsCount * position) + a;
                if (index < PhotoAlbumPickerActivity.this.albumsSorted.size()) {
                    photoPickerAlbumsCell.setAlbum(a, (MediaController.AlbumEntry) PhotoAlbumPickerActivity.this.albumsSorted.get(index));
                } else {
                    photoPickerAlbumsCell.setAlbum(a, (MediaController.AlbumEntry) null);
                }
            }
            photoPickerAlbumsCell.requestLayout();
        }

        public int getItemViewType(int i) {
            return 0;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogBackground), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogBackground), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogTextBlack), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogTextBlack), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogButtonSelector), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogBackground), new ThemeDescription(this.listView, 0, new Class[]{View.class}, (Paint) null, new Drawable[]{Theme.chat_attachEmptyDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_attachEmptyImage), new ThemeDescription(this.listView, 0, new Class[]{View.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_attachPhotoBackground)};
    }
}
