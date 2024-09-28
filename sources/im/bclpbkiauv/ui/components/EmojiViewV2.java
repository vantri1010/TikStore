package im.bclpbkiauv.ui.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.LongSparseArray;
import android.util.Property;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ChatObject;
import im.bclpbkiauv.messenger.Emoji;
import im.bclpbkiauv.messenger.EmojiData;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageReceiver;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaDataController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.browser.Browser;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.ContentPreviewViewer;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.cells.ContextLinkCell;
import im.bclpbkiauv.ui.cells.EmptyCell;
import im.bclpbkiauv.ui.cells.FeaturedStickerSetInfoCell;
import im.bclpbkiauv.ui.cells.StickerEmojiCell;
import im.bclpbkiauv.ui.cells.StickerSetGroupInfoCell;
import im.bclpbkiauv.ui.cells.StickerSetNameCell;
import im.bclpbkiauv.ui.components.EmojiViewV2;
import im.bclpbkiauv.ui.components.PagerSlidingTabStrip;
import im.bclpbkiauv.ui.components.RecyclerListView;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class EmojiViewV2 extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public static final ViewTreeObserver.OnScrollChangedListener NOP = $$Lambda$EmojiViewV2$mdFhYj7vvViQcdzqvJOTCeVUXo4.INSTANCE;
    /* access modifiers changed from: private */
    public static final Field superListenerField;
    /* access modifiers changed from: private */
    public ImageView backspaceButton;
    private AnimatorSet backspaceButtonAnimation;
    /* access modifiers changed from: private */
    public boolean backspaceOnce;
    /* access modifiers changed from: private */
    public boolean backspacePressed;
    private FrameLayout bottomTabContainer;
    private AnimatorSet bottomTabContainerAnimation;
    private View bottomTabContainerBackground;
    /* access modifiers changed from: private */
    public ContentPreviewViewer.ContentPreviewViewerDelegate contentPreviewViewerDelegate = new ContentPreviewViewer.ContentPreviewViewerDelegate() {
        public /* synthetic */ boolean needOpen() {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$needOpen(this);
        }

        public void sendSticker(TLRPC.Document sticker, Object parent, boolean notify, int scheduleDate) {
            EmojiViewV2.this.delegate.onStickerSelected((View) null, sticker, parent, notify, scheduleDate);
        }

        public boolean needSend() {
            return true;
        }

        public boolean canSchedule() {
            return EmojiViewV2.this.delegate.canSchedule();
        }

        public boolean isInScheduleMode() {
            return EmojiViewV2.this.delegate.isInScheduleMode();
        }

        public void openSet(TLRPC.InputStickerSet set, boolean clearsInputField) {
            if (set != null) {
                EmojiViewV2.this.delegate.onShowStickerSet((TLRPC.StickerSet) null, set);
            }
        }

        public void sendGif(Object gif, boolean notify, int scheduleDate) {
            if (EmojiViewV2.this.gifGridView.getAdapter() == EmojiViewV2.this.gifAdapter) {
                EmojiViewV2.this.delegate.onGifSelected((View) null, gif, "gif", notify, scheduleDate);
            } else if (EmojiViewV2.this.gifGridView.getAdapter() == EmojiViewV2.this.gifSearchAdapter) {
                EmojiViewV2.this.delegate.onGifSelected((View) null, gif, EmojiViewV2.this.gifSearchAdapter.bot, notify, scheduleDate);
            }
        }

        public void gifAddedOrDeleted() {
            EmojiViewV2 emojiViewV2 = EmojiViewV2.this;
            ArrayList unused = emojiViewV2.recentGifs = MediaDataController.getInstance(emojiViewV2.currentAccount).getRecentGifs();
            if (EmojiViewV2.this.gifAdapter != null) {
                EmojiViewV2.this.gifAdapter.notifyDataSetChanged();
            }
        }
    };
    /* access modifiers changed from: private */
    public int currentAccount = UserConfig.selectedAccount;
    private int currentBackgroundType = -1;
    /* access modifiers changed from: private */
    public int currentChatId;
    private int currentPage;
    /* access modifiers changed from: private */
    public EmojiViewDelegate delegate;
    /* access modifiers changed from: private */
    public Paint dotPaint;
    /* access modifiers changed from: private */
    public DragListener dragListener;
    /* access modifiers changed from: private */
    public EmojiGridAdapter emojiAdapter;
    private FrameLayout emojiContainer;
    /* access modifiers changed from: private */
    public RecyclerListView emojiGridView;
    private Drawable[] emojiIcons;
    /* access modifiers changed from: private */
    public float emojiLastX;
    /* access modifiers changed from: private */
    public float emojiLastY;
    /* access modifiers changed from: private */
    public GridLayoutManager emojiLayoutManager;
    private int emojiMinusDy;
    /* access modifiers changed from: private */
    public EmojiSearchAdapter emojiSearchAdapter;
    /* access modifiers changed from: private */
    public SearchField emojiSearchField;
    /* access modifiers changed from: private */
    public int emojiSize;
    /* access modifiers changed from: private */
    public AnimatorSet emojiTabShadowAnimator;
    /* access modifiers changed from: private */
    public ScrollSlidingTabStrip emojiTabs;
    private View emojiTabsShadow;
    /* access modifiers changed from: private */
    public String[] emojiTitles;
    /* access modifiers changed from: private */
    public ImageViewEmoji emojiTouchedView;
    /* access modifiers changed from: private */
    public float emojiTouchedX;
    /* access modifiers changed from: private */
    public float emojiTouchedY;
    /* access modifiers changed from: private */
    public int favTabBum = -2;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.Document> favouriteStickers = new ArrayList<>();
    /* access modifiers changed from: private */
    public int featuredStickersHash;
    /* access modifiers changed from: private */
    public boolean firstEmojiAttach = true;
    /* access modifiers changed from: private */
    public boolean firstGifAttach = true;
    /* access modifiers changed from: private */
    public boolean firstStickersAttach = true;
    private ImageView floatingButton;
    private boolean forseMultiwindowLayout;
    /* access modifiers changed from: private */
    public GifAdapter gifAdapter;
    private FrameLayout gifContainer;
    /* access modifiers changed from: private */
    public RecyclerListView gifGridView;
    /* access modifiers changed from: private */
    public ExtendedGridLayoutManager gifLayoutManager;
    private RecyclerListView.OnItemClickListener gifOnItemClickListener;
    /* access modifiers changed from: private */
    public GifSearchAdapter gifSearchAdapter;
    /* access modifiers changed from: private */
    public SearchField gifSearchField;
    /* access modifiers changed from: private */
    public int groupStickerPackNum;
    /* access modifiers changed from: private */
    public int groupStickerPackPosition;
    /* access modifiers changed from: private */
    public TLRPC.TL_messages_stickerSet groupStickerSet;
    /* access modifiers changed from: private */
    public boolean groupStickersHidden;
    private int hasRecentEmoji = -1;
    /* access modifiers changed from: private */
    public TLRPC.ChatFull info;
    /* access modifiers changed from: private */
    public LongSparseArray<TLRPC.StickerSetCovered> installingStickerSets = new LongSparseArray<>();
    private boolean isLayout;
    private float lastBottomScrollDy;
    private int lastNotifyHeight;
    private int lastNotifyHeight2;
    private int lastNotifyWidth;
    /* access modifiers changed from: private */
    public String[] lastSearchKeyboardLanguage;
    /* access modifiers changed from: private */
    public int[] location = new int[2];
    /* access modifiers changed from: private */
    public TextView mediaBanTooltip;
    /* access modifiers changed from: private */
    public boolean needEmojiSearch;
    private Object outlineProvider;
    /* access modifiers changed from: private */
    public ViewPager pager;
    /* access modifiers changed from: private */
    public EmojiColorPickerView pickerView;
    /* access modifiers changed from: private */
    public EmojiPopupWindow pickerViewPopup;
    /* access modifiers changed from: private */
    public int popupHeight;
    /* access modifiers changed from: private */
    public int popupWidth;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.Document> recentGifs = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.Document> recentStickers = new ArrayList<>();
    /* access modifiers changed from: private */
    public int recentTabBum = -2;
    /* access modifiers changed from: private */
    public LongSparseArray<TLRPC.StickerSetCovered> removingStickerSets = new LongSparseArray<>();
    private int scrolledToTrending;
    /* access modifiers changed from: private */
    public AnimatorSet searchAnimation;
    private ImageView searchButton;
    /* access modifiers changed from: private */
    public int searchFieldHeight = AndroidUtilities.dp(64.0f);
    private View shadowLine;
    private boolean showGifs;
    private Drawable[] stickerIcons;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.TL_messages_stickerSet> stickerSets = new ArrayList<>();
    /* access modifiers changed from: private */
    public ImageView stickerSettingsButton;
    private AnimatorSet stickersButtonAnimation;
    private FrameLayout stickersContainer;
    private TextView stickersCounter;
    /* access modifiers changed from: private */
    public StickersGridAdapter stickersGridAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView stickersGridView;
    /* access modifiers changed from: private */
    public GridLayoutManager stickersLayoutManager;
    private int stickersMinusDy;
    private RecyclerListView.OnItemClickListener stickersOnItemClickListener;
    /* access modifiers changed from: private */
    public SearchField stickersSearchField;
    /* access modifiers changed from: private */
    public StickersSearchGridAdapter stickersSearchGridAdapter;
    /* access modifiers changed from: private */
    public ScrollSlidingTabStrip stickersTab;
    /* access modifiers changed from: private */
    public int stickersTabOffset;
    /* access modifiers changed from: private */
    public Drawable[] tabIcons;
    private View topShadow;
    /* access modifiers changed from: private */
    public TrendingGridAdapter trendingGridAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView trendingGridView;
    /* access modifiers changed from: private */
    public GridLayoutManager trendingLayoutManager;
    /* access modifiers changed from: private */
    public boolean trendingLoaded;
    private int trendingTabNum = -2;
    private PagerSlidingTabStrip typeTabs;
    /* access modifiers changed from: private */
    public ArrayList<View> views = new ArrayList<>();

    public interface DragListener {
        void onDrag(int i);

        void onDragCancel();

        void onDragEnd(float f);

        void onDragStart();
    }

    public interface EmojiViewDelegate {
        boolean canSchedule();

        boolean isExpanded();

        boolean isInScheduleMode();

        boolean isSearchOpened();

        boolean onBackspace();

        void onClearEmojiRecent();

        void onEmojiSelected(String str);

        void onGifSelected(View view, Object obj, Object obj2, boolean z, int i);

        void onSearchOpenClose(int i);

        void onShowStickerSet(TLRPC.StickerSet stickerSet, TLRPC.InputStickerSet inputStickerSet);

        void onStickerSelected(View view, TLRPC.Document document, Object obj, boolean z, int i);

        void onStickerSetAdd(TLRPC.StickerSetCovered stickerSetCovered);

        void onStickerSetRemove(TLRPC.StickerSetCovered stickerSetCovered);

        void onStickersGroupClick(int i);

        void onStickersSettingsClick();

        void onTabOpened(int i);

        /* renamed from: im.bclpbkiauv.ui.components.EmojiViewV2$EmojiViewDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static boolean $default$onBackspace(EmojiViewDelegate _this) {
                return false;
            }

            public static void $default$onEmojiSelected(EmojiViewDelegate _this, String emoji) {
            }

            public static void $default$onStickerSelected(EmojiViewDelegate _this, View view, TLRPC.Document sticker, Object parent, boolean notify, int scheduleDate) {
            }

            public static void $default$onStickersSettingsClick(EmojiViewDelegate _this) {
            }

            public static void $default$onStickersGroupClick(EmojiViewDelegate _this, int chatId) {
            }

            public static void $default$onGifSelected(EmojiViewDelegate _this, View view, Object gif, Object parent, boolean notify, int scheduleDate) {
            }

            public static void $default$onTabOpened(EmojiViewDelegate _this, int type) {
            }

            public static void $default$onClearEmojiRecent(EmojiViewDelegate _this) {
            }

            public static void $default$onShowStickerSet(EmojiViewDelegate _this, TLRPC.StickerSet stickerSet, TLRPC.InputStickerSet inputStickerSet) {
            }

            public static void $default$onStickerSetAdd(EmojiViewDelegate _this, TLRPC.StickerSetCovered stickerSet) {
            }

            public static void $default$onStickerSetRemove(EmojiViewDelegate _this, TLRPC.StickerSetCovered stickerSet) {
            }

            public static void $default$onSearchOpenClose(EmojiViewDelegate _this, int type) {
            }

            public static boolean $default$isSearchOpened(EmojiViewDelegate _this) {
                return false;
            }

            public static boolean $default$isExpanded(EmojiViewDelegate _this) {
                return false;
            }

            public static boolean $default$canSchedule(EmojiViewDelegate _this) {
                return false;
            }

            public static boolean $default$isInScheduleMode(EmojiViewDelegate _this) {
                return false;
            }
        }
    }

    static {
        Field f = null;
        try {
            f = PopupWindow.class.getDeclaredField("mOnScrollChangedListener");
            f.setAccessible(true);
        } catch (NoSuchFieldException e) {
        }
        superListenerField = f;
    }

    static /* synthetic */ void lambda$static$0() {
    }

    private class SearchField extends FrameLayout {
        /* access modifiers changed from: private */
        public View backgroundView;
        /* access modifiers changed from: private */
        public ImageView clearSearchImageView;
        /* access modifiers changed from: private */
        public CloseProgressDrawable2 progressDrawable;
        /* access modifiers changed from: private */
        public View searchBackground;
        /* access modifiers changed from: private */
        public EditTextBoldCursor searchEditText;
        /* access modifiers changed from: private */
        public ImageView searchIconImageView;
        /* access modifiers changed from: private */
        public AnimatorSet shadowAnimator;
        /* access modifiers changed from: private */
        public View shadowView;

        public SearchField(Context context, final int type) {
            super(context);
            View view = new View(context);
            this.shadowView = view;
            view.setAlpha(0.0f);
            this.shadowView.setTag(1);
            this.shadowView.setBackgroundColor(Theme.getColor(Theme.key_chat_emojiPanelShadowLine));
            addView(this.shadowView, new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 83));
            View view2 = new View(context);
            this.backgroundView = view2;
            view2.setBackgroundColor(Theme.getColor(Theme.key_chat_emojiPanelBackground));
            addView(this.backgroundView, new FrameLayout.LayoutParams(-1, EmojiViewV2.this.searchFieldHeight));
            View view3 = new View(context);
            this.searchBackground = view3;
            view3.setBackgroundDrawable(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(18.0f), Theme.getColor(Theme.key_chat_emojiSearchBackground)));
            addView(this.searchBackground, LayoutHelper.createFrame(-1.0f, 36.0f, 51, 14.0f, 14.0f, 14.0f, 0.0f));
            ImageView imageView = new ImageView(context);
            this.searchIconImageView = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.searchIconImageView.setImageResource(R.drawable.smiles_inputsearch);
            this.searchIconImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_emojiSearchIcon), PorterDuff.Mode.MULTIPLY));
            addView(this.searchIconImageView, LayoutHelper.createFrame(36.0f, 36.0f, 51, 16.0f, 14.0f, 0.0f, 0.0f));
            ImageView imageView2 = new ImageView(context);
            this.clearSearchImageView = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            ImageView imageView3 = this.clearSearchImageView;
            CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2();
            this.progressDrawable = closeProgressDrawable2;
            imageView3.setImageDrawable(closeProgressDrawable2);
            this.progressDrawable.setSide(AndroidUtilities.dp(7.0f));
            this.clearSearchImageView.setScaleX(0.1f);
            this.clearSearchImageView.setScaleY(0.1f);
            this.clearSearchImageView.setAlpha(0.0f);
            this.clearSearchImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_emojiSearchIcon), PorterDuff.Mode.MULTIPLY));
            addView(this.clearSearchImageView, LayoutHelper.createFrame(36.0f, 36.0f, 53, 14.0f, 14.0f, 14.0f, 0.0f));
            this.clearSearchImageView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    EmojiViewV2.SearchField.this.lambda$new$0$EmojiViewV2$SearchField(view);
                }
            });
            AnonymousClass1 r0 = new EditTextBoldCursor(context, EmojiViewV2.this) {
                public boolean onTouchEvent(MotionEvent event) {
                    if (event.getAction() == 0) {
                        if (!EmojiViewV2.this.delegate.isSearchOpened()) {
                            EmojiViewV2.this.openSearch(SearchField.this);
                        }
                        EmojiViewDelegate access$000 = EmojiViewV2.this.delegate;
                        int i = 1;
                        if (type == 1) {
                            i = 2;
                        }
                        access$000.onSearchOpenClose(i);
                        SearchField.this.searchEditText.requestFocus();
                        AndroidUtilities.showKeyboard(SearchField.this.searchEditText);
                        if (EmojiViewV2.this.trendingGridView != null && EmojiViewV2.this.trendingGridView.getVisibility() == 0) {
                            EmojiViewV2.this.showTrendingTab(false);
                        }
                    }
                    return super.onTouchEvent(event);
                }
            };
            this.searchEditText = r0;
            r0.setTextSize(1, 16.0f);
            this.searchEditText.setHintTextColor(Theme.getColor(Theme.key_chat_emojiSearchIcon));
            this.searchEditText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.searchEditText.setBackgroundDrawable((Drawable) null);
            this.searchEditText.setPadding(0, 0, 0, 0);
            this.searchEditText.setMaxLines(1);
            this.searchEditText.setLines(1);
            this.searchEditText.setSingleLine(true);
            this.searchEditText.setImeOptions(268435459);
            if (type == 0) {
                this.searchEditText.setHint(LocaleController.getString("SearchStickersHint", R.string.SearchStickersHint));
            } else if (type == 1) {
                this.searchEditText.setHint(LocaleController.getString("SearchEmojiHint", R.string.SearchEmojiHint));
            } else if (type == 2) {
                this.searchEditText.setHint(LocaleController.getString("SearchGifsTitle", R.string.SearchGifsTitle));
            }
            this.searchEditText.setCursorColor(Theme.getColor(Theme.key_featuredStickers_addedIcon));
            this.searchEditText.setCursorSize(AndroidUtilities.dp(20.0f));
            this.searchEditText.setCursorWidth(1.5f);
            addView(this.searchEditText, LayoutHelper.createFrame(-1.0f, 40.0f, 51, 54.0f, 12.0f, 46.0f, 0.0f));
            this.searchEditText.addTextChangedListener(new TextWatcher(EmojiViewV2.this) {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                public void afterTextChanged(Editable s) {
                    boolean showed = false;
                    boolean show = SearchField.this.searchEditText.length() > 0;
                    float f = 0.0f;
                    if (SearchField.this.clearSearchImageView.getAlpha() != 0.0f) {
                        showed = true;
                    }
                    if (show != showed) {
                        ViewPropertyAnimator animate = SearchField.this.clearSearchImageView.animate();
                        float f2 = 1.0f;
                        if (show) {
                            f = 1.0f;
                        }
                        ViewPropertyAnimator scaleX = animate.alpha(f).setDuration(150).scaleX(show ? 1.0f : 0.1f);
                        if (!show) {
                            f2 = 0.1f;
                        }
                        scaleX.scaleY(f2).start();
                    }
                    int i = type;
                    if (i == 0) {
                        EmojiViewV2.this.stickersSearchGridAdapter.search(SearchField.this.searchEditText.getText().toString());
                    } else if (i == 1) {
                        EmojiViewV2.this.emojiSearchAdapter.search(SearchField.this.searchEditText.getText().toString());
                    } else if (i == 2) {
                        EmojiViewV2.this.gifSearchAdapter.search(SearchField.this.searchEditText.getText().toString());
                    }
                }
            });
        }

        public /* synthetic */ void lambda$new$0$EmojiViewV2$SearchField(View v) {
            this.searchEditText.setText("");
            AndroidUtilities.showKeyboard(this.searchEditText);
        }

        public void hideKeyboard() {
            AndroidUtilities.hideKeyboard(this.searchEditText);
        }

        /* access modifiers changed from: private */
        public void showShadow(boolean show, boolean animated) {
            if (show && this.shadowView.getTag() == null) {
                return;
            }
            if (show || this.shadowView.getTag() == null) {
                AnimatorSet animatorSet = this.shadowAnimator;
                int i = null;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.shadowAnimator = null;
                }
                View view = this.shadowView;
                if (!show) {
                    i = 1;
                }
                view.setTag(i);
                float f = 1.0f;
                if (animated) {
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.shadowAnimator = animatorSet2;
                    Animator[] animatorArr = new Animator[1];
                    View view2 = this.shadowView;
                    Property property = View.ALPHA;
                    float[] fArr = new float[1];
                    if (!show) {
                        f = 0.0f;
                    }
                    fArr[0] = f;
                    animatorArr[0] = ObjectAnimator.ofFloat(view2, property, fArr);
                    animatorSet2.playTogether(animatorArr);
                    this.shadowAnimator.setDuration(200);
                    this.shadowAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    this.shadowAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            AnimatorSet unused = SearchField.this.shadowAnimator = null;
                        }
                    });
                    this.shadowAnimator.start();
                    return;
                }
                View view3 = this.shadowView;
                if (!show) {
                    f = 0.0f;
                }
                view3.setAlpha(f);
            }
        }
    }

    private class ImageViewEmoji extends ImageView {
        /* access modifiers changed from: private */
        public boolean isRecent;

        public ImageViewEmoji(Context context) {
            super(context);
            setScaleType(ImageView.ScaleType.CENTER);
        }

        /* access modifiers changed from: private */
        public void sendEmoji(String override) {
            String color;
            EmojiViewV2.this.showBottomTab(true, true);
            String code = override != null ? override : (String) getTag();
            new SpannableStringBuilder().append(code);
            if (override == null) {
                if (!this.isRecent && (color = Emoji.emojiColor.get(code)) != null) {
                    code = EmojiViewV2.addColorToCode(code, color);
                }
                EmojiViewV2.this.addEmojiToRecent(code);
                if (EmojiViewV2.this.delegate != null) {
                    EmojiViewV2.this.delegate.onEmojiSelected(Emoji.fixEmoji(code));
                }
            } else if (EmojiViewV2.this.delegate != null) {
                EmojiViewV2.this.delegate.onEmojiSelected(Emoji.fixEmoji(override));
            }
        }

        public void setImageDrawable(Drawable drawable, boolean recent) {
            super.setImageDrawable(drawable);
            this.isRecent = recent;
        }

        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(widthMeasureSpec));
        }
    }

    private class EmojiPopupWindow extends PopupWindow {
        private ViewTreeObserver.OnScrollChangedListener mSuperScrollListener;
        private ViewTreeObserver mViewTreeObserver;

        public EmojiPopupWindow() {
            init();
        }

        public EmojiPopupWindow(Context context) {
            super(context);
            init();
        }

        public EmojiPopupWindow(int width, int height) {
            super(width, height);
            init();
        }

        public EmojiPopupWindow(View contentView) {
            super(contentView);
            init();
        }

        public EmojiPopupWindow(View contentView, int width, int height, boolean focusable) {
            super(contentView, width, height, focusable);
            init();
        }

        public EmojiPopupWindow(View contentView, int width, int height) {
            super(contentView, width, height);
            init();
        }

        private void init() {
            if (EmojiViewV2.superListenerField != null) {
                try {
                    this.mSuperScrollListener = (ViewTreeObserver.OnScrollChangedListener) EmojiViewV2.superListenerField.get(this);
                    EmojiViewV2.superListenerField.set(this, EmojiViewV2.NOP);
                } catch (Exception e) {
                    this.mSuperScrollListener = null;
                }
            }
        }

        private void unregisterListener() {
            ViewTreeObserver viewTreeObserver;
            if (this.mSuperScrollListener != null && (viewTreeObserver = this.mViewTreeObserver) != null) {
                if (viewTreeObserver.isAlive()) {
                    this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
                }
                this.mViewTreeObserver = null;
            }
        }

        private void registerListener(View anchor) {
            if (this.mSuperScrollListener != null) {
                ViewTreeObserver vto = anchor.getWindowToken() != null ? anchor.getViewTreeObserver() : null;
                ViewTreeObserver viewTreeObserver = this.mViewTreeObserver;
                if (vto != viewTreeObserver) {
                    if (viewTreeObserver != null && viewTreeObserver.isAlive()) {
                        this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
                    }
                    this.mViewTreeObserver = vto;
                    if (vto != null) {
                        vto.addOnScrollChangedListener(this.mSuperScrollListener);
                    }
                }
            }
        }

        public void showAsDropDown(View anchor, int xoff, int yoff) {
            try {
                super.showAsDropDown(anchor, xoff, yoff);
                registerListener(anchor);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        public void update(View anchor, int xoff, int yoff, int width, int height) {
            super.update(anchor, xoff, yoff, width, height);
            registerListener(anchor);
        }

        public void update(View anchor, int width, int height) {
            super.update(anchor, width, height);
            registerListener(anchor);
        }

        public void showAtLocation(View parent, int gravity, int x, int y) {
            super.showAtLocation(parent, gravity, x, y);
            unregisterListener();
        }

        public void dismiss() {
            setFocusable(false);
            try {
                super.dismiss();
            } catch (Exception e) {
            }
            unregisterListener();
        }
    }

    private class EmojiColorPickerView extends View {
        private Drawable arrowDrawable = getResources().getDrawable(R.drawable.stickers_back_arrow);
        private int arrowX;
        private Drawable backgroundDrawable = getResources().getDrawable(R.drawable.stickers_back_all);
        private String currentEmoji;
        private RectF rect = new RectF();
        private Paint rectPaint = new Paint(1);
        private int selection;

        public void setEmoji(String emoji, int arrowPosition) {
            this.currentEmoji = emoji;
            this.arrowX = arrowPosition;
            this.rectPaint.setColor(Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR);
            invalidate();
        }

        public String getEmoji() {
            return this.currentEmoji;
        }

        public void setSelection(int position) {
            if (this.selection != position) {
                this.selection = position;
                invalidate();
            }
        }

        public int getSelection() {
            return this.selection;
        }

        public EmojiColorPickerView(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            String color;
            this.backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), AndroidUtilities.dp(AndroidUtilities.isTablet() ? 60.0f : 52.0f));
            this.backgroundDrawable.draw(canvas);
            Drawable drawable = this.arrowDrawable;
            int dp = this.arrowX - AndroidUtilities.dp(9.0f);
            float f = 55.5f;
            int dp2 = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 55.5f : 47.5f);
            int dp3 = this.arrowX + AndroidUtilities.dp(9.0f);
            if (!AndroidUtilities.isTablet()) {
                f = 47.5f;
            }
            drawable.setBounds(dp, dp2, dp3, AndroidUtilities.dp(f + 8.0f));
            this.arrowDrawable.draw(canvas);
            if (this.currentEmoji != null) {
                for (int a = 0; a < 6; a++) {
                    int x = (EmojiViewV2.this.emojiSize * a) + AndroidUtilities.dp((float) ((a * 4) + 5));
                    int y = AndroidUtilities.dp(9.0f);
                    if (this.selection == a) {
                        this.rect.set((float) x, (float) (y - ((int) AndroidUtilities.dpf2(3.5f))), (float) (EmojiViewV2.this.emojiSize + x), (float) (EmojiViewV2.this.emojiSize + y + AndroidUtilities.dp(3.0f)));
                        canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.rectPaint);
                    }
                    String code = this.currentEmoji;
                    if (a != 0) {
                        if (a == 1) {
                            color = "🏻";
                        } else if (a == 2) {
                            color = "🏼";
                        } else if (a == 3) {
                            color = "🏽";
                        } else if (a == 4) {
                            color = "🏾";
                        } else if (a != 5) {
                            color = "";
                        } else {
                            color = "🏿";
                        }
                        code = EmojiViewV2.addColorToCode(code, color);
                    }
                    Drawable drawable2 = Emoji.getEmojiBigDrawable(code);
                    if (drawable2 != null) {
                        drawable2.setBounds(x, y, EmojiViewV2.this.emojiSize + x, EmojiViewV2.this.emojiSize + y);
                        drawable2.draw(canvas);
                    }
                }
            }
        }
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public EmojiViewV2(boolean r28, boolean r29, android.content.Context r30, boolean r31, im.bclpbkiauv.tgnet.TLRPC.ChatFull r32) {
        /*
            r27 = this;
            r0 = r27
            r1 = r29
            r2 = r30
            r3 = r31
            r0.<init>(r2)
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            r0.views = r4
            r4 = 1
            r0.firstEmojiAttach = r4
            r5 = -1
            r0.hasRecentEmoji = r5
            r0.firstGifAttach = r4
            r0.firstStickersAttach = r4
            int r6 = im.bclpbkiauv.messenger.UserConfig.selectedAccount
            r0.currentAccount = r6
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            r0.stickerSets = r6
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            r0.recentGifs = r6
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            r0.recentStickers = r6
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            r0.favouriteStickers = r6
            android.util.LongSparseArray r6 = new android.util.LongSparseArray
            r6.<init>()
            r0.installingStickerSets = r6
            android.util.LongSparseArray r6 = new android.util.LongSparseArray
            r6.<init>()
            r0.removingStickerSets = r6
            r6 = 2
            int[] r7 = new int[r6]
            r0.location = r7
            r7 = -2
            r0.recentTabBum = r7
            r0.favTabBum = r7
            r0.trendingTabNum = r7
            r0.currentBackgroundType = r5
            im.bclpbkiauv.ui.components.EmojiViewV2$1 r8 = new im.bclpbkiauv.ui.components.EmojiViewV2$1
            r8.<init>()
            r0.contentPreviewViewerDelegate = r8
            r8 = 1115684864(0x42800000, float:64.0)
            int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
            r0.searchFieldHeight = r8
            r0.needEmojiSearch = r3
            r8 = 3
            android.graphics.drawable.Drawable[] r9 = new android.graphics.drawable.Drawable[r8]
            java.lang.String r10 = "chat_emojiBottomPanelIcon"
            int r11 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r10)
            java.lang.String r12 = "chat_emojiPanelIconSelected"
            int r13 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r12)
            r14 = 2131231603(0x7f080373, float:1.8079292E38)
            android.graphics.drawable.Drawable r11 = im.bclpbkiauv.ui.actionbar.Theme.createEmojiIconSelectorDrawable(r2, r14, r11, r13)
            r13 = 0
            r9[r13] = r11
            int r11 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r10)
            int r14 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r12)
            r15 = 2131231600(0x7f080370, float:1.8079286E38)
            android.graphics.drawable.Drawable r11 = im.bclpbkiauv.ui.actionbar.Theme.createEmojiIconSelectorDrawable(r2, r15, r11, r14)
            r9[r4] = r11
            int r11 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r10)
            int r14 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r12)
            r15 = 2131231604(0x7f080374, float:1.8079294E38)
            android.graphics.drawable.Drawable r11 = im.bclpbkiauv.ui.actionbar.Theme.createEmojiIconSelectorDrawable(r2, r15, r11, r14)
            r9[r6] = r11
            r0.tabIcons = r9
            r9 = 9
            android.graphics.drawable.Drawable[] r9 = new android.graphics.drawable.Drawable[r9]
            java.lang.String r11 = "chat_emojiPanelIcon"
            int r14 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r11)
            int r15 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r12)
            r7 = 2131231594(0x7f08036a, float:1.8079273E38)
            android.graphics.drawable.Drawable r7 = im.bclpbkiauv.ui.actionbar.Theme.createEmojiIconSelectorDrawable(r2, r7, r14, r15)
            r9[r13] = r7
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r11)
            int r14 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r12)
            r15 = 2131231595(0x7f08036b, float:1.8079275E38)
            android.graphics.drawable.Drawable r7 = im.bclpbkiauv.ui.actionbar.Theme.createEmojiIconSelectorDrawable(r2, r15, r7, r14)
            r9[r4] = r7
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r11)
            int r14 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r12)
            r15 = 2131231587(0x7f080363, float:1.807926E38)
            android.graphics.drawable.Drawable r7 = im.bclpbkiauv.ui.actionbar.Theme.createEmojiIconSelectorDrawable(r2, r15, r7, r14)
            r9[r6] = r7
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r11)
            int r14 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r12)
            r15 = 2131231590(0x7f080366, float:1.8079265E38)
            android.graphics.drawable.Drawable r7 = im.bclpbkiauv.ui.actionbar.Theme.createEmojiIconSelectorDrawable(r2, r15, r7, r14)
            r9[r8] = r7
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r11)
            int r14 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r12)
            r15 = 2131231586(0x7f080362, float:1.8079257E38)
            android.graphics.drawable.Drawable r7 = im.bclpbkiauv.ui.actionbar.Theme.createEmojiIconSelectorDrawable(r2, r15, r7, r14)
            r14 = 4
            r9[r14] = r7
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r11)
            int r15 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r12)
            r5 = 2131231596(0x7f08036c, float:1.8079277E38)
            android.graphics.drawable.Drawable r5 = im.bclpbkiauv.ui.actionbar.Theme.createEmojiIconSelectorDrawable(r2, r5, r7, r15)
            r7 = 5
            r9[r7] = r5
            int r5 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r11)
            int r15 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r12)
            r7 = 2131231591(0x7f080367, float:1.8079267E38)
            android.graphics.drawable.Drawable r5 = im.bclpbkiauv.ui.actionbar.Theme.createEmojiIconSelectorDrawable(r2, r7, r5, r15)
            r7 = 6
            r9[r7] = r5
            int r5 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r11)
            int r15 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r12)
            r7 = 2131231592(0x7f080368, float:1.807927E38)
            android.graphics.drawable.Drawable r5 = im.bclpbkiauv.ui.actionbar.Theme.createEmojiIconSelectorDrawable(r2, r7, r5, r15)
            r7 = 7
            r9[r7] = r5
            int r5 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r11)
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r12)
            r11 = 2131231589(0x7f080365, float:1.8079263E38)
            android.graphics.drawable.Drawable r5 = im.bclpbkiauv.ui.actionbar.Theme.createEmojiIconSelectorDrawable(r2, r11, r5, r7)
            r7 = 8
            r9[r7] = r5
            r0.emojiIcons = r9
            android.graphics.drawable.Drawable[] r5 = new android.graphics.drawable.Drawable[r8]
            int r9 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r10)
            int r11 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r12)
            r15 = 2131231594(0x7f08036a, float:1.8079273E38)
            android.graphics.drawable.Drawable r9 = im.bclpbkiauv.ui.actionbar.Theme.createEmojiIconSelectorDrawable(r2, r15, r9, r11)
            r5[r13] = r9
            int r9 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r10)
            int r11 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r12)
            r15 = 2131231588(0x7f080364, float:1.8079261E38)
            android.graphics.drawable.Drawable r9 = im.bclpbkiauv.ui.actionbar.Theme.createEmojiIconSelectorDrawable(r2, r15, r9, r11)
            r5[r4] = r9
            int r9 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r10)
            int r10 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r12)
            r11 = 2131231597(0x7f08036d, float:1.807928E38)
            android.graphics.drawable.Drawable r9 = im.bclpbkiauv.ui.actionbar.Theme.createEmojiIconSelectorDrawable(r2, r11, r9, r10)
            r5[r6] = r9
            r0.stickerIcons = r5
            java.lang.String[] r5 = new java.lang.String[r7]
            java.lang.String r9 = "Emoji1"
            r10 = 2131691047(0x7f0f0627, float:1.9011155E38)
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r10)
            r5[r13] = r9
            java.lang.String r9 = "Emoji2"
            r10 = 2131691048(0x7f0f0628, float:1.9011157E38)
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r10)
            r5[r4] = r9
            java.lang.String r9 = "Emoji3"
            r10 = 2131691049(0x7f0f0629, float:1.9011159E38)
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r10)
            r5[r6] = r9
            java.lang.String r9 = "Emoji4"
            r10 = 2131691050(0x7f0f062a, float:1.901116E38)
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r10)
            r5[r8] = r9
            java.lang.String r9 = "Emoji5"
            r10 = 2131691051(0x7f0f062b, float:1.9011163E38)
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r10)
            r5[r14] = r9
            java.lang.String r9 = "Emoji6"
            r10 = 2131691052(0x7f0f062c, float:1.9011165E38)
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r10)
            r10 = 5
            r5[r10] = r9
            java.lang.String r9 = "Emoji7"
            r10 = 2131691053(0x7f0f062d, float:1.9011167E38)
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r10)
            r10 = 6
            r5[r10] = r9
            java.lang.String r9 = "Emoji8"
            r10 = 2131691054(0x7f0f062e, float:1.901117E38)
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r10)
            r10 = 7
            r5[r10] = r9
            r0.emojiTitles = r5
            r0.showGifs = r1
            r5 = r32
            r0.info = r5
            android.graphics.Paint r9 = new android.graphics.Paint
            r9.<init>(r4)
            r0.dotPaint = r9
            java.lang.String r10 = "chat_emojiPanelNewTrending"
            int r10 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r10)
            r9.setColor(r10)
            int r9 = android.os.Build.VERSION.SDK_INT
            r10 = 21
            if (r9 < r10) goto L_0x0206
            im.bclpbkiauv.ui.components.EmojiViewV2$2 r9 = new im.bclpbkiauv.ui.components.EmojiViewV2$2
            r9.<init>()
            r0.outlineProvider = r9
        L_0x0206:
            android.widget.FrameLayout r9 = new android.widget.FrameLayout
            r9.<init>(r2)
            r0.emojiContainer = r9
            java.util.ArrayList<android.view.View> r11 = r0.views
            r11.add(r9)
            im.bclpbkiauv.ui.components.EmojiViewV2$3 r9 = new im.bclpbkiauv.ui.components.EmojiViewV2$3
            r9.<init>(r2)
            r0.emojiGridView = r9
            r9.setInstantClick(r4)
            im.bclpbkiauv.ui.components.RecyclerListView r9 = r0.emojiGridView
            androidx.recyclerview.widget.GridLayoutManager r11 = new androidx.recyclerview.widget.GridLayoutManager
            r11.<init>(r2, r7)
            r0.emojiLayoutManager = r11
            r9.setLayoutManager(r11)
            im.bclpbkiauv.ui.components.RecyclerListView r9 = r0.emojiGridView
            r11 = 1108869120(0x42180000, float:38.0)
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r11)
            r9.setTopGlowOffset(r12)
            im.bclpbkiauv.ui.components.RecyclerListView r9 = r0.emojiGridView
            r12 = 1111490560(0x42400000, float:48.0)
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)
            r9.setBottomGlowOffset(r12)
            im.bclpbkiauv.ui.components.RecyclerListView r9 = r0.emojiGridView
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r11)
            r9.setPadding(r13, r12, r13, r13)
            im.bclpbkiauv.ui.components.RecyclerListView r9 = r0.emojiGridView
            java.lang.String r12 = "chat_emojiPanelBackground"
            int r15 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r12)
            r9.setGlowColor(r15)
            im.bclpbkiauv.ui.components.RecyclerListView r9 = r0.emojiGridView
            r9.setClipToPadding(r13)
            androidx.recyclerview.widget.GridLayoutManager r9 = r0.emojiLayoutManager
            im.bclpbkiauv.ui.components.EmojiViewV2$4 r15 = new im.bclpbkiauv.ui.components.EmojiViewV2$4
            r15.<init>()
            r9.setSpanSizeLookup(r15)
            im.bclpbkiauv.ui.components.RecyclerListView r9 = r0.emojiGridView
            im.bclpbkiauv.ui.components.EmojiViewV2$EmojiGridAdapter r15 = new im.bclpbkiauv.ui.components.EmojiViewV2$EmojiGridAdapter
            r8 = 0
            r15.<init>()
            r0.emojiAdapter = r15
            r9.setAdapter(r15)
            im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter r9 = new im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter
            r9.<init>()
            r0.emojiSearchAdapter = r9
            android.widget.FrameLayout r9 = r0.emojiContainer
            im.bclpbkiauv.ui.components.RecyclerListView r15 = r0.emojiGridView
            r14 = -1082130432(0xffffffffbf800000, float:-1.0)
            r10 = -1
            android.widget.FrameLayout$LayoutParams r14 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r10, r14)
            r9.addView(r15, r14)
            im.bclpbkiauv.ui.components.RecyclerListView r9 = r0.emojiGridView
            im.bclpbkiauv.ui.components.EmojiViewV2$5 r10 = new im.bclpbkiauv.ui.components.EmojiViewV2$5
            r10.<init>()
            r9.setOnScrollListener(r10)
            im.bclpbkiauv.ui.components.RecyclerListView r9 = r0.emojiGridView
            im.bclpbkiauv.ui.components.EmojiViewV2$6 r10 = new im.bclpbkiauv.ui.components.EmojiViewV2$6
            r10.<init>()
            r9.setOnItemClickListener((im.bclpbkiauv.ui.components.RecyclerListView.OnItemClickListener) r10)
            im.bclpbkiauv.ui.components.RecyclerListView r9 = r0.emojiGridView
            im.bclpbkiauv.ui.components.EmojiViewV2$7 r10 = new im.bclpbkiauv.ui.components.EmojiViewV2$7
            r10.<init>()
            r9.setOnItemLongClickListener((im.bclpbkiauv.ui.components.RecyclerListView.OnItemLongClickListener) r10)
            im.bclpbkiauv.ui.components.ScrollSlidingTabStrip r9 = new im.bclpbkiauv.ui.components.ScrollSlidingTabStrip
            r9.<init>(r2)
            r0.emojiTabs = r9
            if (r3 == 0) goto L_0x02d2
            im.bclpbkiauv.ui.components.EmojiViewV2$SearchField r9 = new im.bclpbkiauv.ui.components.EmojiViewV2$SearchField
            r9.<init>(r2, r4)
            r0.emojiSearchField = r9
            android.widget.FrameLayout r10 = r0.emojiContainer
            android.widget.FrameLayout$LayoutParams r14 = new android.widget.FrameLayout$LayoutParams
            int r15 = r0.searchFieldHeight
            int r18 = im.bclpbkiauv.messenger.AndroidUtilities.getShadowHeight()
            int r15 = r15 + r18
            r7 = -1
            r14.<init>(r7, r15)
            r10.addView(r9, r14)
            im.bclpbkiauv.ui.components.EmojiViewV2$SearchField r7 = r0.emojiSearchField
            im.bclpbkiauv.ui.components.EditTextBoldCursor r7 = r7.searchEditText
            im.bclpbkiauv.ui.components.EmojiViewV2$8 r9 = new im.bclpbkiauv.ui.components.EmojiViewV2$8
            r9.<init>()
            r7.setOnFocusChangeListener(r9)
        L_0x02d2:
            im.bclpbkiauv.ui.components.ScrollSlidingTabStrip r7 = r0.emojiTabs
            r7.setShouldExpand(r4)
            im.bclpbkiauv.ui.components.ScrollSlidingTabStrip r7 = r0.emojiTabs
            r9 = -1
            r7.setIndicatorHeight(r9)
            im.bclpbkiauv.ui.components.ScrollSlidingTabStrip r7 = r0.emojiTabs
            r7.setUnderlineHeight(r9)
            im.bclpbkiauv.ui.components.ScrollSlidingTabStrip r7 = r0.emojiTabs
            int r10 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r12)
            r7.setBackgroundColor(r10)
            android.widget.FrameLayout r7 = r0.emojiContainer
            im.bclpbkiauv.ui.components.ScrollSlidingTabStrip r10 = r0.emojiTabs
            android.widget.FrameLayout$LayoutParams r14 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r9, r11)
            r7.addView(r10, r14)
            im.bclpbkiauv.ui.components.ScrollSlidingTabStrip r7 = r0.emojiTabs
            im.bclpbkiauv.ui.components.EmojiViewV2$9 r9 = new im.bclpbkiauv.ui.components.EmojiViewV2$9
            r9.<init>()
            r7.setDelegate(r9)
            android.view.View r7 = new android.view.View
            r7.<init>(r2)
            r0.emojiTabsShadow = r7
            r9 = 0
            r7.setAlpha(r9)
            android.view.View r7 = r0.emojiTabsShadow
            java.lang.Integer r9 = java.lang.Integer.valueOf(r4)
            r7.setTag(r9)
            android.view.View r7 = r0.emojiTabsShadow
            java.lang.String r9 = "chat_emojiPanelShadowLine"
            int r10 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r9)
            r7.setBackgroundColor(r10)
            android.widget.FrameLayout$LayoutParams r7 = new android.widget.FrameLayout$LayoutParams
            int r10 = im.bclpbkiauv.messenger.AndroidUtilities.getShadowHeight()
            r14 = 51
            r15 = -1
            r7.<init>(r15, r10, r14)
            int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r11)
            r7.topMargin = r10
            android.widget.FrameLayout r10 = r0.emojiContainer
            android.view.View r11 = r0.emojiTabsShadow
            r10.addView(r11, r7)
            r10 = 1073741824(0x40000000, float:2.0)
            if (r28 == 0) goto L_0x0543
            if (r1 == 0) goto L_0x03d6
            android.widget.FrameLayout r11 = new android.widget.FrameLayout
            r11.<init>(r2)
            r0.gifContainer = r11
            java.util.ArrayList<android.view.View> r15 = r0.views
            r15.add(r11)
            im.bclpbkiauv.ui.components.EmojiViewV2$10 r11 = new im.bclpbkiauv.ui.components.EmojiViewV2$10
            r11.<init>(r2)
            r0.gifGridView = r11
            r11.setClipToPadding(r13)
            im.bclpbkiauv.ui.components.RecyclerListView r11 = r0.gifGridView
            im.bclpbkiauv.ui.components.EmojiViewV2$11 r15 = new im.bclpbkiauv.ui.components.EmojiViewV2$11
            r4 = 100
            r15.<init>(r2, r4)
            r0.gifLayoutManager = r15
            r11.setLayoutManager(r15)
            im.bclpbkiauv.ui.components.ExtendedGridLayoutManager r4 = r0.gifLayoutManager
            im.bclpbkiauv.ui.components.EmojiViewV2$12 r11 = new im.bclpbkiauv.ui.components.EmojiViewV2$12
            r11.<init>()
            r4.setSpanSizeLookup(r11)
            im.bclpbkiauv.ui.components.RecyclerListView r4 = r0.gifGridView
            im.bclpbkiauv.ui.components.EmojiViewV2$13 r11 = new im.bclpbkiauv.ui.components.EmojiViewV2$13
            r11.<init>()
            r4.addItemDecoration(r11)
            im.bclpbkiauv.ui.components.RecyclerListView r4 = r0.gifGridView
            r4.setOverScrollMode(r6)
            im.bclpbkiauv.ui.components.RecyclerListView r4 = r0.gifGridView
            im.bclpbkiauv.ui.components.EmojiViewV2$GifAdapter r11 = new im.bclpbkiauv.ui.components.EmojiViewV2$GifAdapter
            r11.<init>(r2)
            r0.gifAdapter = r11
            r4.setAdapter(r11)
            im.bclpbkiauv.ui.components.EmojiViewV2$GifSearchAdapter r4 = new im.bclpbkiauv.ui.components.EmojiViewV2$GifSearchAdapter
            r4.<init>(r2)
            r0.gifSearchAdapter = r4
            im.bclpbkiauv.ui.components.RecyclerListView r4 = r0.gifGridView
            im.bclpbkiauv.ui.components.EmojiViewV2$14 r11 = new im.bclpbkiauv.ui.components.EmojiViewV2$14
            r11.<init>()
            r4.setOnScrollListener(r11)
            im.bclpbkiauv.ui.components.RecyclerListView r4 = r0.gifGridView
            im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$eWST9O4tLAV3we7MSCAy7JygTr4 r11 = new im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$eWST9O4tLAV3we7MSCAy7JygTr4
            r11.<init>()
            r4.setOnTouchListener(r11)
            im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$_3_UfBVFCfYT3wJceXpwpamrbvM r4 = new im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$_3_UfBVFCfYT3wJceXpwpamrbvM
            r4.<init>()
            r0.gifOnItemClickListener = r4
            im.bclpbkiauv.ui.components.RecyclerListView r11 = r0.gifGridView
            r11.setOnItemClickListener((im.bclpbkiauv.ui.components.RecyclerListView.OnItemClickListener) r4)
            android.widget.FrameLayout r4 = r0.gifContainer
            im.bclpbkiauv.ui.components.RecyclerListView r11 = r0.gifGridView
            r15 = -1082130432(0xffffffffbf800000, float:-1.0)
            r14 = -1
            android.widget.FrameLayout$LayoutParams r15 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r14, r15)
            r4.addView(r11, r15)
            im.bclpbkiauv.ui.components.EmojiViewV2$SearchField r4 = new im.bclpbkiauv.ui.components.EmojiViewV2$SearchField
            r4.<init>(r2, r6)
            r0.gifSearchField = r4
            android.widget.FrameLayout r11 = r0.gifContainer
            android.widget.FrameLayout$LayoutParams r14 = new android.widget.FrameLayout$LayoutParams
            int r15 = r0.searchFieldHeight
            int r19 = im.bclpbkiauv.messenger.AndroidUtilities.getShadowHeight()
            int r15 = r15 + r19
            r6 = -1
            r14.<init>(r6, r15)
            r11.addView(r4, r14)
        L_0x03d6:
            android.widget.FrameLayout r4 = new android.widget.FrameLayout
            r4.<init>(r2)
            r0.stickersContainer = r4
            int r4 = r0.currentAccount
            im.bclpbkiauv.messenger.MediaDataController r4 = im.bclpbkiauv.messenger.MediaDataController.getInstance(r4)
            r4.checkStickers(r13)
            int r4 = r0.currentAccount
            im.bclpbkiauv.messenger.MediaDataController r4 = im.bclpbkiauv.messenger.MediaDataController.getInstance(r4)
            r4.checkFeaturedStickers()
            im.bclpbkiauv.ui.components.EmojiViewV2$15 r4 = new im.bclpbkiauv.ui.components.EmojiViewV2$15
            r4.<init>(r2)
            r0.stickersGridView = r4
            androidx.recyclerview.widget.GridLayoutManager r6 = new androidx.recyclerview.widget.GridLayoutManager
            r11 = 5
            r6.<init>(r2, r11)
            r0.stickersLayoutManager = r6
            r4.setLayoutManager(r6)
            androidx.recyclerview.widget.GridLayoutManager r4 = r0.stickersLayoutManager
            im.bclpbkiauv.ui.components.EmojiViewV2$16 r6 = new im.bclpbkiauv.ui.components.EmojiViewV2$16
            r6.<init>()
            r4.setSpanSizeLookup(r6)
            im.bclpbkiauv.ui.components.RecyclerListView r4 = r0.stickersGridView
            r6 = 1112539136(0x42500000, float:52.0)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            r4.setPadding(r13, r6, r13, r13)
            im.bclpbkiauv.ui.components.RecyclerListView r4 = r0.stickersGridView
            r4.setClipToPadding(r13)
            java.util.ArrayList<android.view.View> r4 = r0.views
            android.widget.FrameLayout r6 = r0.stickersContainer
            r4.add(r6)
            im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r4 = new im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter
            r4.<init>(r2)
            r0.stickersSearchGridAdapter = r4
            im.bclpbkiauv.ui.components.RecyclerListView r4 = r0.stickersGridView
            im.bclpbkiauv.ui.components.EmojiViewV2$StickersGridAdapter r6 = new im.bclpbkiauv.ui.components.EmojiViewV2$StickersGridAdapter
            r6.<init>(r2)
            r0.stickersGridAdapter = r6
            r4.setAdapter(r6)
            im.bclpbkiauv.ui.components.RecyclerListView r4 = r0.stickersGridView
            im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$sPV9vUzlmVeCrMeQDRzRAAooAsQ r6 = new im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$sPV9vUzlmVeCrMeQDRzRAAooAsQ
            r6.<init>()
            r4.setOnTouchListener(r6)
            im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$xeJU9Id8MPp6SsUCj0io6bBdegA r4 = new im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$xeJU9Id8MPp6SsUCj0io6bBdegA
            r4.<init>()
            r0.stickersOnItemClickListener = r4
            im.bclpbkiauv.ui.components.RecyclerListView r6 = r0.stickersGridView
            r6.setOnItemClickListener((im.bclpbkiauv.ui.components.RecyclerListView.OnItemClickListener) r4)
            im.bclpbkiauv.ui.components.RecyclerListView r4 = r0.stickersGridView
            int r6 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r12)
            r4.setGlowColor(r6)
            android.widget.FrameLayout r4 = r0.stickersContainer
            im.bclpbkiauv.ui.components.RecyclerListView r6 = r0.stickersGridView
            r4.addView(r6)
            im.bclpbkiauv.ui.components.EmojiViewV2$17 r4 = new im.bclpbkiauv.ui.components.EmojiViewV2$17
            r4.<init>(r2)
            r0.stickersTab = r4
            im.bclpbkiauv.ui.components.EmojiViewV2$SearchField r4 = new im.bclpbkiauv.ui.components.EmojiViewV2$SearchField
            r4.<init>(r2, r13)
            r0.stickersSearchField = r4
            android.widget.FrameLayout r6 = r0.stickersContainer
            android.widget.FrameLayout$LayoutParams r11 = new android.widget.FrameLayout$LayoutParams
            int r14 = r0.searchFieldHeight
            int r15 = im.bclpbkiauv.messenger.AndroidUtilities.getShadowHeight()
            int r14 = r14 + r15
            r15 = -1
            r11.<init>(r15, r14)
            r6.addView(r4, r11)
            im.bclpbkiauv.ui.components.RecyclerListView r4 = new im.bclpbkiauv.ui.components.RecyclerListView
            r4.<init>(r2)
            r0.trendingGridView = r4
            r4.setItemAnimator(r8)
            im.bclpbkiauv.ui.components.RecyclerListView r4 = r0.trendingGridView
            r4.setLayoutAnimation(r8)
            im.bclpbkiauv.ui.components.RecyclerListView r4 = r0.trendingGridView
            im.bclpbkiauv.ui.components.EmojiViewV2$18 r6 = new im.bclpbkiauv.ui.components.EmojiViewV2$18
            r11 = 5
            r6.<init>(r2, r11)
            r0.trendingLayoutManager = r6
            r4.setLayoutManager(r6)
            androidx.recyclerview.widget.GridLayoutManager r4 = r0.trendingLayoutManager
            im.bclpbkiauv.ui.components.EmojiViewV2$19 r6 = new im.bclpbkiauv.ui.components.EmojiViewV2$19
            r6.<init>()
            r4.setSpanSizeLookup(r6)
            im.bclpbkiauv.ui.components.RecyclerListView r4 = r0.trendingGridView
            im.bclpbkiauv.ui.components.EmojiViewV2$20 r6 = new im.bclpbkiauv.ui.components.EmojiViewV2$20
            r6.<init>()
            r4.setOnScrollListener(r6)
            im.bclpbkiauv.ui.components.RecyclerListView r4 = r0.trendingGridView
            r4.setClipToPadding(r13)
            im.bclpbkiauv.ui.components.RecyclerListView r4 = r0.trendingGridView
            r6 = 1111490560(0x42400000, float:48.0)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            r4.setPadding(r13, r6, r13, r13)
            im.bclpbkiauv.ui.components.RecyclerListView r4 = r0.trendingGridView
            im.bclpbkiauv.ui.components.EmojiViewV2$TrendingGridAdapter r6 = new im.bclpbkiauv.ui.components.EmojiViewV2$TrendingGridAdapter
            r6.<init>(r2)
            r0.trendingGridAdapter = r6
            r4.setAdapter(r6)
            im.bclpbkiauv.ui.components.RecyclerListView r4 = r0.trendingGridView
            im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$viYloRBOetiQvyBwbAwjQHOdfZ8 r6 = new im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$viYloRBOetiQvyBwbAwjQHOdfZ8
            r6.<init>()
            r4.setOnItemClickListener((im.bclpbkiauv.ui.components.RecyclerListView.OnItemClickListener) r6)
            im.bclpbkiauv.ui.components.EmojiViewV2$TrendingGridAdapter r4 = r0.trendingGridAdapter
            r4.notifyDataSetChanged()
            im.bclpbkiauv.ui.components.RecyclerListView r4 = r0.trendingGridView
            int r6 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r12)
            r4.setGlowColor(r6)
            im.bclpbkiauv.ui.components.RecyclerListView r4 = r0.trendingGridView
            r6 = 8
            r4.setVisibility(r6)
            android.widget.FrameLayout r4 = r0.stickersContainer
            im.bclpbkiauv.ui.components.RecyclerListView r6 = r0.trendingGridView
            r4.addView(r6)
            im.bclpbkiauv.ui.components.ScrollSlidingTabStrip r4 = r0.stickersTab
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.getShadowHeight()
            r4.setUnderlineHeight(r6)
            im.bclpbkiauv.ui.components.ScrollSlidingTabStrip r4 = r0.stickersTab
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r10)
            r4.setIndicatorHeight(r6)
            im.bclpbkiauv.ui.components.ScrollSlidingTabStrip r4 = r0.stickersTab
            java.lang.String r6 = "chat_emojiPanelStickerPackSelectorLine"
            int r6 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r6)
            r4.setIndicatorColor(r6)
            im.bclpbkiauv.ui.components.ScrollSlidingTabStrip r4 = r0.stickersTab
            int r6 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r9)
            r4.setUnderlineColor(r6)
            im.bclpbkiauv.ui.components.ScrollSlidingTabStrip r4 = r0.stickersTab
            int r6 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r12)
            r4.setBackgroundColor(r6)
            android.widget.FrameLayout r4 = r0.stickersContainer
            im.bclpbkiauv.ui.components.ScrollSlidingTabStrip r6 = r0.stickersTab
            r14 = 48
            r11 = -1
            r15 = 51
            android.widget.FrameLayout$LayoutParams r14 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r11, (int) r14, (int) r15)
            r4.addView(r6, r14)
            r27.updateStickerTabs()
            im.bclpbkiauv.ui.components.ScrollSlidingTabStrip r4 = r0.stickersTab
            im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$qohcXFOdt3x6ilaw7jZOu61OqR8 r6 = new im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$qohcXFOdt3x6ilaw7jZOu61OqR8
            r6.<init>()
            r4.setDelegate(r6)
            im.bclpbkiauv.ui.components.RecyclerListView r4 = r0.stickersGridView
            im.bclpbkiauv.ui.components.EmojiViewV2$21 r6 = new im.bclpbkiauv.ui.components.EmojiViewV2$21
            r6.<init>()
            r4.setOnScrollListener(r6)
        L_0x0543:
            im.bclpbkiauv.ui.components.EmojiViewV2$22 r4 = new im.bclpbkiauv.ui.components.EmojiViewV2$22
            r4.<init>(r2)
            r0.pager = r4
            im.bclpbkiauv.ui.components.EmojiViewV2$EmojiPagesAdapter r6 = new im.bclpbkiauv.ui.components.EmojiViewV2$EmojiPagesAdapter
            r6.<init>()
            r4.setAdapter(r6)
            android.view.View r4 = new android.view.View
            r4.<init>(r2)
            r0.topShadow = r4
            r6 = 2131231061(0x7f080155, float:1.8078192E38)
            r8 = -1907225(0xffffffffffe2e5e7, float:NaN)
            android.graphics.drawable.Drawable r6 = im.bclpbkiauv.ui.actionbar.Theme.getThemedDrawable((android.content.Context) r2, (int) r6, (int) r8)
            r4.setBackgroundDrawable(r6)
            android.view.View r4 = r0.topShadow
            r6 = 1086324736(0x40c00000, float:6.0)
            r8 = -1
            android.widget.FrameLayout$LayoutParams r6 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r8, r6)
            r0.addView(r4, r6)
            im.bclpbkiauv.ui.components.EmojiViewV2$23 r4 = new im.bclpbkiauv.ui.components.EmojiViewV2$23
            r4.<init>(r2)
            r0.backspaceButton = r4
            r6 = 2131231599(0x7f08036f, float:1.8079284E38)
            r4.setImageResource(r6)
            android.widget.ImageView r4 = r0.backspaceButton
            android.graphics.PorterDuffColorFilter r6 = new android.graphics.PorterDuffColorFilter
            java.lang.String r8 = "chat_emojiPanelBackspace"
            int r8 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r8)
            android.graphics.PorterDuff$Mode r11 = android.graphics.PorterDuff.Mode.MULTIPLY
            r6.<init>(r8, r11)
            r4.setColorFilter(r6)
            android.widget.ImageView r4 = r0.backspaceButton
            android.widget.ImageView$ScaleType r6 = android.widget.ImageView.ScaleType.CENTER
            r4.setScaleType(r6)
            android.widget.ImageView r4 = r0.backspaceButton
            r6 = 2131689489(0x7f0f0011, float:1.9007995E38)
            java.lang.String r8 = "AccDescrBackspace"
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r6)
            r4.setContentDescription(r6)
            android.widget.ImageView r4 = r0.backspaceButton
            r6 = 1
            r4.setFocusable(r6)
            im.bclpbkiauv.ui.components.EmojiViewV2$24 r4 = new im.bclpbkiauv.ui.components.EmojiViewV2$24
            r4.<init>(r2)
            r0.bottomTabContainer = r4
            android.view.View r4 = new android.view.View
            r4.<init>(r2)
            r0.shadowLine = r4
            int r6 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r9)
            r4.setBackgroundColor(r6)
            android.widget.FrameLayout r4 = r0.bottomTabContainer
            android.view.View r6 = r0.shadowLine
            android.widget.FrameLayout$LayoutParams r8 = new android.widget.FrameLayout$LayoutParams
            int r9 = im.bclpbkiauv.messenger.AndroidUtilities.getShadowHeight()
            r11 = -1
            r8.<init>(r11, r9)
            r4.addView(r6, r8)
            android.view.View r4 = new android.view.View
            r4.<init>(r2)
            r0.bottomTabContainerBackground = r4
            android.widget.FrameLayout r6 = r0.bottomTabContainer
            android.widget.FrameLayout$LayoutParams r8 = new android.widget.FrameLayout$LayoutParams
            r9 = 1110441984(0x42300000, float:44.0)
            int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
            r11 = 83
            r14 = -1
            r8.<init>(r14, r9, r11)
            r6.addView(r4, r8)
            r6 = 44
            if (r3 == 0) goto L_0x06fd
            android.widget.FrameLayout r8 = r0.bottomTabContainer
            android.widget.FrameLayout$LayoutParams r9 = new android.widget.FrameLayout$LayoutParams
            r10 = 1110441984(0x42300000, float:44.0)
            int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r10)
            int r11 = im.bclpbkiauv.messenger.AndroidUtilities.getShadowHeight()
            int r10 = r10 + r11
            r11 = 83
            r12 = -1
            r9.<init>(r12, r10, r11)
            r0.addView(r8, r9)
            android.widget.FrameLayout r8 = r0.bottomTabContainer
            android.widget.ImageView r9 = r0.backspaceButton
            r10 = 52
            r11 = 85
            android.widget.FrameLayout$LayoutParams r10 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r10, (int) r6, (int) r11)
            r8.addView(r9, r10)
            android.widget.ImageView r8 = new android.widget.ImageView
            r8.<init>(r2)
            r0.stickerSettingsButton = r8
            r9 = 2131231602(0x7f080372, float:1.807929E38)
            r8.setImageResource(r9)
            android.widget.ImageView r8 = r0.stickerSettingsButton
            android.graphics.PorterDuffColorFilter r9 = new android.graphics.PorterDuffColorFilter
            java.lang.String r10 = "chat_emojiPanelBackspace"
            int r10 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r10)
            android.graphics.PorterDuff$Mode r11 = android.graphics.PorterDuff.Mode.MULTIPLY
            r9.<init>(r10, r11)
            r8.setColorFilter(r9)
            android.widget.ImageView r8 = r0.stickerSettingsButton
            android.widget.ImageView$ScaleType r9 = android.widget.ImageView.ScaleType.CENTER
            r8.setScaleType(r9)
            android.widget.ImageView r8 = r0.stickerSettingsButton
            r9 = 1
            r8.setFocusable(r9)
            android.widget.ImageView r8 = r0.stickerSettingsButton
            r9 = 2131693910(0x7f0f1156, float:1.9016962E38)
            java.lang.String r10 = "Settings"
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r10, r9)
            r8.setContentDescription(r9)
            android.widget.FrameLayout r8 = r0.bottomTabContainer
            android.widget.ImageView r9 = r0.stickerSettingsButton
            r10 = 52
            r11 = 85
            android.widget.FrameLayout$LayoutParams r10 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r10, (int) r6, (int) r11)
            r8.addView(r9, r10)
            android.widget.ImageView r8 = r0.stickerSettingsButton
            im.bclpbkiauv.ui.components.EmojiViewV2$25 r9 = new im.bclpbkiauv.ui.components.EmojiViewV2$25
            r9.<init>()
            r8.setOnClickListener(r9)
            im.bclpbkiauv.ui.components.PagerSlidingTabStrip r8 = new im.bclpbkiauv.ui.components.PagerSlidingTabStrip
            r8.<init>(r2)
            r0.typeTabs = r8
            androidx.viewpager.widget.ViewPager r9 = r0.pager
            r8.setViewPager(r9)
            im.bclpbkiauv.ui.components.PagerSlidingTabStrip r8 = r0.typeTabs
            r8.setShouldExpand(r13)
            im.bclpbkiauv.ui.components.PagerSlidingTabStrip r8 = r0.typeTabs
            r8.setIndicatorHeight(r13)
            im.bclpbkiauv.ui.components.PagerSlidingTabStrip r8 = r0.typeTabs
            r8.setUnderlineHeight(r13)
            im.bclpbkiauv.ui.components.PagerSlidingTabStrip r8 = r0.typeTabs
            r9 = 1092616192(0x41200000, float:10.0)
            int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
            r8.setTabPaddingLeftRight(r9)
            android.widget.FrameLayout r8 = r0.bottomTabContainer
            im.bclpbkiauv.ui.components.PagerSlidingTabStrip r9 = r0.typeTabs
            r10 = 81
            r11 = -2
            android.widget.FrameLayout$LayoutParams r10 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r11, (int) r6, (int) r10)
            r8.addView(r9, r10)
            im.bclpbkiauv.ui.components.PagerSlidingTabStrip r8 = r0.typeTabs
            im.bclpbkiauv.ui.components.EmojiViewV2$26 r9 = new im.bclpbkiauv.ui.components.EmojiViewV2$26
            r9.<init>()
            r8.setOnPageChangeListener(r9)
            android.widget.ImageView r8 = new android.widget.ImageView
            r8.<init>(r2)
            r0.searchButton = r8
            r9 = 2131231601(0x7f080371, float:1.8079288E38)
            r8.setImageResource(r9)
            android.widget.ImageView r8 = r0.searchButton
            android.graphics.PorterDuffColorFilter r9 = new android.graphics.PorterDuffColorFilter
            java.lang.String r10 = "chat_emojiPanelBackspace"
            int r10 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r10)
            android.graphics.PorterDuff$Mode r11 = android.graphics.PorterDuff.Mode.MULTIPLY
            r9.<init>(r10, r11)
            r8.setColorFilter(r9)
            android.widget.ImageView r8 = r0.searchButton
            android.widget.ImageView$ScaleType r9 = android.widget.ImageView.ScaleType.CENTER
            r8.setScaleType(r9)
            android.widget.ImageView r8 = r0.searchButton
            r9 = 2131693714(0x7f0f1092, float:1.9016564E38)
            java.lang.String r10 = "Search"
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r10, r9)
            r8.setContentDescription(r9)
            android.widget.ImageView r8 = r0.searchButton
            r9 = 1
            r8.setFocusable(r9)
            android.widget.FrameLayout r8 = r0.bottomTabContainer
            android.widget.ImageView r9 = r0.searchButton
            r10 = 52
            r11 = 83
            android.widget.FrameLayout$LayoutParams r6 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r10, (int) r6, (int) r11)
            r8.addView(r9, r6)
            android.widget.ImageView r6 = r0.searchButton
            im.bclpbkiauv.ui.components.EmojiViewV2$27 r8 = new im.bclpbkiauv.ui.components.EmojiViewV2$27
            r8.<init>()
            r6.setOnClickListener(r8)
            goto L_0x0844
        L_0x06fd:
            android.widget.FrameLayout r8 = r0.bottomTabContainer
            int r9 = android.os.Build.VERSION.SDK_INT
            r11 = 21
            if (r9 < r11) goto L_0x0708
            r9 = 40
            goto L_0x070a
        L_0x0708:
            r9 = 44
        L_0x070a:
            int r9 = r9 + 20
            float r9 = (float) r9
            int r14 = android.os.Build.VERSION.SDK_INT
            if (r14 < r11) goto L_0x0714
            r11 = 40
            goto L_0x0716
        L_0x0714:
            r11 = 44
        L_0x0716:
            int r11 = r11 + 12
            float r11 = (float) r11
            boolean r14 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r14 == 0) goto L_0x0720
            r17 = 3
            goto L_0x0722
        L_0x0720:
            r17 = 5
        L_0x0722:
            r22 = r17 | 80
            r23 = 0
            r24 = 0
            r25 = 1073741824(0x40000000, float:2.0)
            r26 = 0
            r20 = r9
            r21 = r11
            android.widget.FrameLayout$LayoutParams r9 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r0.addView(r8, r9)
            r8 = 1113587712(0x42600000, float:56.0)
            int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
            int r9 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r12)
            int r11 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r12)
            android.graphics.drawable.Drawable r8 = im.bclpbkiauv.ui.actionbar.Theme.createSimpleSelectorCircleDrawable(r8, r9, r11)
            int r9 = android.os.Build.VERSION.SDK_INT
            r11 = 21
            if (r9 >= r11) goto L_0x0780
            android.content.res.Resources r9 = r30.getResources()
            r11 = 2131231018(0x7f08012a, float:1.8078105E38)
            android.graphics.drawable.Drawable r9 = r9.getDrawable(r11)
            android.graphics.drawable.Drawable r9 = r9.mutate()
            android.graphics.PorterDuffColorFilter r11 = new android.graphics.PorterDuffColorFilter
            r12 = -16777216(0xffffffffff000000, float:-1.7014118E38)
            android.graphics.PorterDuff$Mode r14 = android.graphics.PorterDuff.Mode.MULTIPLY
            r11.<init>(r12, r14)
            r9.setColorFilter(r11)
            im.bclpbkiauv.ui.components.CombinedDrawable r11 = new im.bclpbkiauv.ui.components.CombinedDrawable
            r11.<init>(r9, r8, r13, r13)
            r12 = 1109393408(0x42200000, float:40.0)
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)
            r14 = 1109393408(0x42200000, float:40.0)
            int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            r11.setIconSize(r12, r14)
            r8 = r11
            goto L_0x07ea
        L_0x0780:
            android.animation.StateListAnimator r9 = new android.animation.StateListAnimator
            r9.<init>()
            r11 = 1
            int[] r12 = new int[r11]
            r11 = 16842919(0x10100a7, float:2.3694026E-38)
            r12[r13] = r11
            android.widget.ImageView r11 = r0.floatingButton
            android.util.Property r14 = android.view.View.TRANSLATION_Z
            r15 = 2
            float[] r4 = new float[r15]
            int r15 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r10)
            float r15 = (float) r15
            r4[r13] = r15
            r15 = 1082130432(0x40800000, float:4.0)
            int r15 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r15)
            float r15 = (float) r15
            r17 = 1
            r4[r17] = r15
            android.animation.ObjectAnimator r4 = android.animation.ObjectAnimator.ofFloat(r11, r14, r4)
            r14 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r4 = r4.setDuration(r14)
            r9.addState(r12, r4)
            int[] r4 = new int[r13]
            android.widget.ImageView r11 = r0.floatingButton
            android.util.Property r12 = android.view.View.TRANSLATION_Z
            r14 = 2
            float[] r15 = new float[r14]
            r14 = 1082130432(0x40800000, float:4.0)
            int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            float r14 = (float) r14
            r15[r13] = r14
            int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r10)
            float r14 = (float) r14
            r17 = 1
            r15[r17] = r14
            android.animation.ObjectAnimator r11 = android.animation.ObjectAnimator.ofFloat(r11, r12, r15)
            r14 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r11 = r11.setDuration(r14)
            r9.addState(r4, r11)
            android.widget.ImageView r4 = r0.backspaceButton
            r4.setStateListAnimator(r9)
            android.widget.ImageView r4 = r0.backspaceButton
            im.bclpbkiauv.ui.components.EmojiViewV2$28 r11 = new im.bclpbkiauv.ui.components.EmojiViewV2$28
            r11.<init>()
            r4.setOutlineProvider(r11)
        L_0x07ea:
            android.widget.ImageView r4 = r0.backspaceButton
            int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r10)
            r4.setPadding(r13, r13, r9, r13)
            android.widget.ImageView r4 = r0.backspaceButton
            r4.setBackgroundDrawable(r8)
            android.widget.ImageView r4 = r0.backspaceButton
            r9 = 2131689489(0x7f0f0011, float:1.9007995E38)
            java.lang.String r10 = "AccDescrBackspace"
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r10, r9)
            r4.setContentDescription(r9)
            android.widget.ImageView r4 = r0.backspaceButton
            r9 = 1
            r4.setFocusable(r9)
            android.widget.FrameLayout r4 = r0.bottomTabContainer
            android.widget.ImageView r9 = r0.backspaceButton
            int r10 = android.os.Build.VERSION.SDK_INT
            r11 = 21
            if (r10 < r11) goto L_0x0819
            r10 = 40
            goto L_0x081b
        L_0x0819:
            r10 = 44
        L_0x081b:
            float r10 = (float) r10
            int r12 = android.os.Build.VERSION.SDK_INT
            if (r12 < r11) goto L_0x0822
            r6 = 40
        L_0x0822:
            float r6 = (float) r6
            r22 = 51
            r23 = 1092616192(0x41200000, float:10.0)
            r24 = 0
            r25 = 1092616192(0x41200000, float:10.0)
            r26 = 0
            r20 = r10
            r21 = r6
            android.widget.FrameLayout$LayoutParams r6 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r4.addView(r9, r6)
            android.view.View r4 = r0.shadowLine
            r6 = 8
            r4.setVisibility(r6)
            android.view.View r4 = r0.bottomTabContainerBackground
            r4.setVisibility(r6)
        L_0x0844:
            androidx.viewpager.widget.ViewPager r4 = r0.pager
            r6 = 51
            r8 = -1
            android.widget.FrameLayout$LayoutParams r6 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r8, (int) r8, (int) r6)
            r0.addView(r4, r13, r6)
            im.bclpbkiauv.ui.components.CorrectlyMeasuringTextView r4 = new im.bclpbkiauv.ui.components.CorrectlyMeasuringTextView
            r4.<init>(r2)
            r0.mediaBanTooltip = r4
            r6 = 1077936128(0x40400000, float:3.0)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            java.lang.String r8 = "chat_gifSaveHintBackground"
            int r8 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r8)
            android.graphics.drawable.Drawable r6 = im.bclpbkiauv.ui.actionbar.Theme.createRoundRectDrawable(r6, r8)
            r4.setBackgroundDrawable(r6)
            android.widget.TextView r4 = r0.mediaBanTooltip
            java.lang.String r6 = "chat_gifSaveHintText"
            int r6 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r6)
            r4.setTextColor(r6)
            android.widget.TextView r4 = r0.mediaBanTooltip
            r6 = 1090519040(0x41000000, float:8.0)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            r8 = 1088421888(0x40e00000, float:7.0)
            int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
            r9 = 1090519040(0x41000000, float:8.0)
            int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
            r10 = 1088421888(0x40e00000, float:7.0)
            int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r10)
            r4.setPadding(r6, r8, r9, r10)
            android.widget.TextView r4 = r0.mediaBanTooltip
            r6 = 16
            r4.setGravity(r6)
            android.widget.TextView r4 = r0.mediaBanTooltip
            r6 = 1096810496(0x41600000, float:14.0)
            r8 = 1
            r4.setTextSize(r8, r6)
            android.widget.TextView r4 = r0.mediaBanTooltip
            r6 = 4
            r4.setVisibility(r6)
            android.widget.TextView r4 = r0.mediaBanTooltip
            r20 = -1073741824(0xffffffffc0000000, float:-2.0)
            r21 = -1073741824(0xffffffffc0000000, float:-2.0)
            r22 = 81
            r23 = 1084227584(0x40a00000, float:5.0)
            r24 = 0
            r25 = 1084227584(0x40a00000, float:5.0)
            r26 = 1112801280(0x42540000, float:53.0)
            android.widget.FrameLayout$LayoutParams r6 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r0.addView(r4, r6)
            boolean r4 = im.bclpbkiauv.messenger.AndroidUtilities.isTablet()
            if (r4 == 0) goto L_0x08c8
            r4 = 1109393408(0x42200000, float:40.0)
            goto L_0x08ca
        L_0x08c8:
            r4 = 1107296256(0x42000000, float:32.0)
        L_0x08ca:
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
            r0.emojiSize = r4
            im.bclpbkiauv.ui.components.EmojiViewV2$EmojiColorPickerView r4 = new im.bclpbkiauv.ui.components.EmojiViewV2$EmojiColorPickerView
            r4.<init>(r2)
            r0.pickerView = r4
            im.bclpbkiauv.ui.components.EmojiViewV2$EmojiPopupWindow r4 = new im.bclpbkiauv.ui.components.EmojiViewV2$EmojiPopupWindow
            im.bclpbkiauv.ui.components.EmojiViewV2$EmojiColorPickerView r6 = r0.pickerView
            boolean r8 = im.bclpbkiauv.messenger.AndroidUtilities.isTablet()
            if (r8 == 0) goto L_0x08e4
            r16 = 40
            goto L_0x08e8
        L_0x08e4:
            r8 = 32
            r16 = 32
        L_0x08e8:
            r8 = 6
            int r16 = r16 * 6
            int r16 = r16 + 10
            int r8 = r16 + 20
            float r8 = (float) r8
            int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
            r0.popupWidth = r8
            boolean r9 = im.bclpbkiauv.messenger.AndroidUtilities.isTablet()
            if (r9 == 0) goto L_0x08ff
            r9 = 1115684864(0x42800000, float:64.0)
            goto L_0x0901
        L_0x08ff:
            r9 = 1113587712(0x42600000, float:56.0)
        L_0x0901:
            int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
            r0.popupHeight = r9
            r4.<init>(r6, r8, r9)
            r0.pickerViewPopup = r4
            r6 = 1
            r4.setOutsideTouchable(r6)
            im.bclpbkiauv.ui.components.EmojiViewV2$EmojiPopupWindow r4 = r0.pickerViewPopup
            r4.setClippingEnabled(r6)
            im.bclpbkiauv.ui.components.EmojiViewV2$EmojiPopupWindow r4 = r0.pickerViewPopup
            r8 = 2
            r4.setInputMethodMode(r8)
            im.bclpbkiauv.ui.components.EmojiViewV2$EmojiPopupWindow r4 = r0.pickerViewPopup
            r4.setSoftInputMode(r13)
            im.bclpbkiauv.ui.components.EmojiViewV2$EmojiPopupWindow r4 = r0.pickerViewPopup
            android.view.View r4 = r4.getContentView()
            r4.setFocusableInTouchMode(r6)
            im.bclpbkiauv.ui.components.EmojiViewV2$EmojiPopupWindow r4 = r0.pickerViewPopup
            android.view.View r4 = r4.getContentView()
            im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$XExfco6U4IJ3dGdrTEK4Wvw0ifs r6 = new im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$XExfco6U4IJ3dGdrTEK4Wvw0ifs
            r6.<init>()
            r4.setOnKeyListener(r6)
            android.content.SharedPreferences r4 = im.bclpbkiauv.messenger.MessagesController.getGlobalEmojiSettings()
            java.lang.String r6 = "selected_page"
            int r4 = r4.getInt(r6, r13)
            r0.currentPage = r4
            im.bclpbkiauv.messenger.Emoji.loadRecentEmoji()
            im.bclpbkiauv.ui.components.EmojiViewV2$EmojiGridAdapter r4 = r0.emojiAdapter
            r4.notifyDataSetChanged()
            im.bclpbkiauv.ui.components.PagerSlidingTabStrip r4 = r0.typeTabs
            if (r4 == 0) goto L_0x097d
            java.util.ArrayList<android.view.View> r4 = r0.views
            int r4 = r4.size()
            r6 = 1
            if (r4 != r6) goto L_0x0967
            im.bclpbkiauv.ui.components.PagerSlidingTabStrip r4 = r0.typeTabs
            int r4 = r4.getVisibility()
            if (r4 != 0) goto L_0x0967
            im.bclpbkiauv.ui.components.PagerSlidingTabStrip r4 = r0.typeTabs
            r6 = 4
            r4.setVisibility(r6)
            goto L_0x097d
        L_0x0967:
            java.util.ArrayList<android.view.View> r4 = r0.views
            int r4 = r4.size()
            r6 = 1
            if (r4 == r6) goto L_0x097d
            im.bclpbkiauv.ui.components.PagerSlidingTabStrip r4 = r0.typeTabs
            int r4 = r4.getVisibility()
            if (r4 == 0) goto L_0x097d
            im.bclpbkiauv.ui.components.PagerSlidingTabStrip r4 = r0.typeTabs
            r4.setVisibility(r13)
        L_0x097d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.EmojiViewV2.<init>(boolean, boolean, android.content.Context, boolean, im.bclpbkiauv.tgnet.TLRPC$ChatFull):void");
    }

    public /* synthetic */ boolean lambda$new$1$EmojiViewV2(View v, MotionEvent event) {
        return ContentPreviewViewer.getInstance().onTouch(event, this.gifGridView, 0, this.gifOnItemClickListener, this.contentPreviewViewerDelegate);
    }

    public /* synthetic */ void lambda$new$2$EmojiViewV2(View view, int position) {
        if (this.delegate != null) {
            int position2 = position - 1;
            if (this.gifGridView.getAdapter() != this.gifAdapter) {
                RecyclerView.Adapter adapter = this.gifGridView.getAdapter();
                GifSearchAdapter gifSearchAdapter2 = this.gifSearchAdapter;
                if (adapter == gifSearchAdapter2 && position2 >= 0 && position2 < gifSearchAdapter2.results.size()) {
                    this.delegate.onGifSelected(view, this.gifSearchAdapter.results.get(position2), this.gifSearchAdapter.bot, true, 0);
                    this.recentGifs = MediaDataController.getInstance(this.currentAccount).getRecentGifs();
                    GifAdapter gifAdapter2 = this.gifAdapter;
                    if (gifAdapter2 != null) {
                        gifAdapter2.notifyDataSetChanged();
                    }
                }
            } else if (position2 >= 0 && position2 < this.recentGifs.size()) {
                this.delegate.onGifSelected(view, this.recentGifs.get(position2), "gif", true, 0);
            }
        }
    }

    public /* synthetic */ boolean lambda$new$3$EmojiViewV2(View v, MotionEvent event) {
        return ContentPreviewViewer.getInstance().onTouch(event, this.stickersGridView, getMeasuredHeight(), this.stickersOnItemClickListener, this.contentPreviewViewerDelegate);
    }

    public /* synthetic */ void lambda$new$4$EmojiViewV2(View view, int position) {
        TLRPC.StickerSetCovered pack;
        RecyclerView.Adapter adapter = this.stickersGridView.getAdapter();
        StickersSearchGridAdapter stickersSearchGridAdapter2 = this.stickersSearchGridAdapter;
        if (adapter == stickersSearchGridAdapter2 && (pack = (TLRPC.StickerSetCovered) stickersSearchGridAdapter2.positionsToSets.get(position)) != null) {
            this.delegate.onShowStickerSet(pack.set, (TLRPC.InputStickerSet) null);
        } else if (view instanceof StickerEmojiCell) {
            ContentPreviewViewer.getInstance().reset();
            StickerEmojiCell cell = (StickerEmojiCell) view;
            if (!cell.isDisabled()) {
                cell.disable();
                this.delegate.onStickerSelected(cell, cell.getSticker(), cell.getParentObject(), true, 0);
            }
        }
    }

    public /* synthetic */ void lambda$new$5$EmojiViewV2(View view, int position) {
        TLRPC.StickerSetCovered pack = (TLRPC.StickerSetCovered) this.trendingGridAdapter.positionsToSets.get(position);
        if (pack != null) {
            this.delegate.onShowStickerSet(pack.set, (TLRPC.InputStickerSet) null);
        }
    }

    public /* synthetic */ void lambda$new$6$EmojiViewV2(int page) {
        if (page == this.trendingTabNum) {
            if (this.trendingGridView.getVisibility() != 0) {
                showTrendingTab(true);
            }
        } else if (this.trendingGridView.getVisibility() == 0) {
            showTrendingTab(false);
            saveNewPage();
        }
        if (page != this.trendingTabNum) {
            if (page == this.recentTabBum) {
                this.stickersGridView.stopScroll();
                this.stickersLayoutManager.scrollToPositionWithOffset(this.stickersGridAdapter.getPositionForPack("recent"), 0);
                checkStickersTabY((View) null, 0);
                ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
                int i = this.recentTabBum;
                scrollSlidingTabStrip.onPageScrolled(i, i > 0 ? i : this.stickersTabOffset);
            } else if (page == this.favTabBum) {
                this.stickersGridView.stopScroll();
                this.stickersLayoutManager.scrollToPositionWithOffset(this.stickersGridAdapter.getPositionForPack("fav"), 0);
                checkStickersTabY((View) null, 0);
                ScrollSlidingTabStrip scrollSlidingTabStrip2 = this.stickersTab;
                int i2 = this.favTabBum;
                scrollSlidingTabStrip2.onPageScrolled(i2, i2 > 0 ? i2 : this.stickersTabOffset);
            } else {
                int index = page - this.stickersTabOffset;
                if (index < this.stickerSets.size()) {
                    if (index >= this.stickerSets.size()) {
                        index = this.stickerSets.size() - 1;
                    }
                    this.firstStickersAttach = false;
                    this.stickersGridView.stopScroll();
                    this.stickersLayoutManager.scrollToPositionWithOffset(this.stickersGridAdapter.getPositionForPack(this.stickerSets.get(index)), 0);
                    checkStickersTabY((View) null, 0);
                    checkScroll();
                }
            }
        }
    }

    public /* synthetic */ boolean lambda$new$7$EmojiViewV2(View v, int keyCode, KeyEvent event) {
        EmojiPopupWindow emojiPopupWindow;
        if (keyCode != 82 || event.getRepeatCount() != 0 || event.getAction() != 1 || (emojiPopupWindow = this.pickerViewPopup) == null || !emojiPopupWindow.isShowing()) {
            return false;
        }
        this.pickerViewPopup.dismiss();
        return true;
    }

    /* access modifiers changed from: private */
    public static String addColorToCode(String code, String color) {
        String end = null;
        int length = code.length();
        if (length > 2 && code.charAt(code.length() - 2) == 8205) {
            end = code.substring(code.length() - 2);
            code = code.substring(0, code.length() - 2);
        } else if (length > 3 && code.charAt(code.length() - 3) == 8205) {
            end = code.substring(code.length() - 3);
            code = code.substring(0, code.length() - 3);
        }
        String code2 = code + color;
        if (end == null) {
            return code2;
        }
        return code2 + end;
    }

    public void setTranslationY(float translationY) {
        View parent;
        super.setTranslationY(translationY);
        if (this.bottomTabContainer.getTag() == null) {
            EmojiViewDelegate emojiViewDelegate = this.delegate;
            if ((emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) && (parent = (View) getParent()) != null) {
                this.bottomTabContainer.setTranslationY(-((getY() + ((float) getMeasuredHeight())) - ((float) parent.getHeight())));
            }
        }
    }

    /* access modifiers changed from: private */
    public void startStopVisibleGifs(boolean start) {
        RecyclerListView recyclerListView = this.gifGridView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.gifGridView.getChildAt(a);
                if (child instanceof ContextLinkCell) {
                    ImageReceiver imageReceiver = ((ContextLinkCell) child).getPhotoImage();
                    if (start) {
                        imageReceiver.setAllowStartAnimation(true);
                        imageReceiver.startAnimation();
                    } else {
                        imageReceiver.setAllowStartAnimation(false);
                        imageReceiver.stopAnimation();
                    }
                }
            }
        }
    }

    public void addEmojiToRecent(String code) {
        if (Emoji.isValidEmoji(code)) {
            int size = Emoji.recentEmoji.size();
            Emoji.addRecentEmoji(code);
            if (!(getVisibility() == 0 && this.pager.getCurrentItem() == 0)) {
                Emoji.sortEmoji();
                this.emojiAdapter.notifyDataSetChanged();
            }
            Emoji.saveRecentEmoji();
        }
    }

    public void showSearchField(boolean show) {
        ScrollSlidingTabStrip tabStrip;
        GridLayoutManager layoutManager;
        for (int a = 0; a < 3; a++) {
            if (a == 0) {
                layoutManager = this.emojiLayoutManager;
                tabStrip = this.emojiTabs;
            } else if (a == 1) {
                layoutManager = this.gifLayoutManager;
                tabStrip = null;
            } else {
                layoutManager = this.stickersLayoutManager;
                tabStrip = this.stickersTab;
            }
            if (layoutManager != null) {
                int position = layoutManager.findFirstVisibleItemPosition();
                if (show) {
                    if (position == 1 || position == 2) {
                        layoutManager.scrollToPosition(0);
                        if (tabStrip != null) {
                            tabStrip.setTranslationY(0.0f);
                        }
                    }
                } else if (position == 0) {
                    layoutManager.scrollToPositionWithOffset(1, 0);
                }
            }
        }
    }

    public void hideSearchKeyboard() {
        SearchField searchField = this.stickersSearchField;
        if (searchField != null) {
            searchField.hideKeyboard();
        }
        SearchField searchField2 = this.gifSearchField;
        if (searchField2 != null) {
            searchField2.hideKeyboard();
        }
        SearchField searchField3 = this.emojiSearchField;
        if (searchField3 != null) {
            searchField3.hideKeyboard();
        }
    }

    /* access modifiers changed from: private */
    public void openSearch(SearchField searchField) {
        GridLayoutManager layoutManager;
        ScrollSlidingTabStrip tabStrip;
        final RecyclerListView gridView;
        SearchField currentField;
        EmojiViewDelegate emojiViewDelegate;
        AnimatorSet animatorSet = this.searchAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.searchAnimation = null;
        }
        this.firstStickersAttach = false;
        this.firstGifAttach = false;
        this.firstEmojiAttach = false;
        for (int a = 0; a < 3; a++) {
            if (a == 0) {
                currentField = this.emojiSearchField;
                gridView = this.emojiGridView;
                tabStrip = this.emojiTabs;
                layoutManager = this.emojiLayoutManager;
            } else if (a == 1) {
                currentField = this.gifSearchField;
                gridView = this.gifGridView;
                tabStrip = null;
                layoutManager = this.gifLayoutManager;
            } else {
                currentField = this.stickersSearchField;
                gridView = this.stickersGridView;
                tabStrip = this.stickersTab;
                layoutManager = this.stickersLayoutManager;
            }
            if (currentField == null) {
                SearchField searchField2 = searchField;
            } else {
                if (currentField == this.gifSearchField) {
                    SearchField searchField3 = searchField;
                } else if (searchField == currentField && (emojiViewDelegate = this.delegate) != null && emojiViewDelegate.isExpanded()) {
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.searchAnimation = animatorSet2;
                    if (tabStrip != null) {
                        animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(tabStrip, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(48.0f))}), ObjectAnimator.ofFloat(gridView, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(48.0f))}), ObjectAnimator.ofFloat(currentField, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(0.0f)})});
                    } else {
                        animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(gridView, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(48.0f))}), ObjectAnimator.ofFloat(currentField, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(0.0f)})});
                    }
                    this.searchAnimation.setDuration(200);
                    this.searchAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                    this.searchAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (animation.equals(EmojiViewV2.this.searchAnimation)) {
                                gridView.setTranslationY(0.0f);
                                if (gridView == EmojiViewV2.this.stickersGridView) {
                                    gridView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
                                } else if (gridView == EmojiViewV2.this.emojiGridView) {
                                    gridView.setPadding(0, 0, 0, 0);
                                }
                                AnimatorSet unused = EmojiViewV2.this.searchAnimation = null;
                            }
                        }

                        public void onAnimationCancel(Animator animation) {
                            if (animation.equals(EmojiViewV2.this.searchAnimation)) {
                                AnimatorSet unused = EmojiViewV2.this.searchAnimation = null;
                            }
                        }
                    });
                    this.searchAnimation.start();
                }
                currentField.setTranslationY((float) AndroidUtilities.dp(0.0f));
                if (tabStrip != null) {
                    tabStrip.setTranslationY((float) (-AndroidUtilities.dp(48.0f)));
                }
                if (gridView == this.stickersGridView) {
                    gridView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
                } else if (gridView == this.emojiGridView) {
                    gridView.setPadding(0, 0, 0, 0);
                }
                layoutManager.scrollToPositionWithOffset(0, 0);
            }
        }
        SearchField searchField4 = searchField;
    }

    private void showEmojiShadow(boolean show, boolean animated) {
        if (show && this.emojiTabsShadow.getTag() == null) {
            return;
        }
        if (show || this.emojiTabsShadow.getTag() == null) {
            AnimatorSet animatorSet = this.emojiTabShadowAnimator;
            int i = null;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.emojiTabShadowAnimator = null;
            }
            View view = this.emojiTabsShadow;
            if (!show) {
                i = 1;
            }
            view.setTag(i);
            float f = 1.0f;
            if (animated) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.emojiTabShadowAnimator = animatorSet2;
                Animator[] animatorArr = new Animator[1];
                View view2 = this.emojiTabsShadow;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                if (!show) {
                    f = 0.0f;
                }
                fArr[0] = f;
                animatorArr[0] = ObjectAnimator.ofFloat(view2, property, fArr);
                animatorSet2.playTogether(animatorArr);
                this.emojiTabShadowAnimator.setDuration(200);
                this.emojiTabShadowAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                this.emojiTabShadowAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        AnimatorSet unused = EmojiViewV2.this.emojiTabShadowAnimator = null;
                    }
                });
                this.emojiTabShadowAnimator.start();
                return;
            }
            View view3 = this.emojiTabsShadow;
            if (!show) {
                f = 0.0f;
            }
            view3.setAlpha(f);
        }
    }

    public void closeSearch(boolean animated) {
        closeSearch(animated, -1);
    }

    public void closeSearch(boolean animated, long scrollToSet) {
        ScrollSlidingTabStrip tabStrip;
        final GridLayoutManager layoutManager;
        final RecyclerListView gridView;
        SearchField currentField;
        TLRPC.TL_messages_stickerSet set;
        int pos;
        boolean z = animated;
        long j = scrollToSet;
        AnimatorSet animatorSet = this.searchAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.searchAnimation = null;
        }
        int currentItem = this.pager.getCurrentItem();
        if (currentItem == 2 && j != -1 && (set = MediaDataController.getInstance(this.currentAccount).getStickerSetById(j)) != null && (pos = this.stickersGridAdapter.getPositionForPack(set)) >= 0) {
            this.stickersLayoutManager.scrollToPositionWithOffset(pos, AndroidUtilities.dp(60.0f));
        }
        int a = 0;
        while (a < 3) {
            if (a == 0) {
                currentField = this.emojiSearchField;
                gridView = this.emojiGridView;
                layoutManager = this.emojiLayoutManager;
                tabStrip = this.emojiTabs;
            } else if (a == 1) {
                currentField = this.gifSearchField;
                gridView = this.gifGridView;
                layoutManager = this.gifLayoutManager;
                tabStrip = null;
            } else {
                currentField = this.stickersSearchField;
                gridView = this.stickersGridView;
                layoutManager = this.stickersLayoutManager;
                tabStrip = this.stickersTab;
            }
            if (currentField != null) {
                currentField.searchEditText.setText("");
                if (a != currentItem || !z) {
                    layoutManager.scrollToPositionWithOffset(1, 0);
                    currentField.setTranslationY((float) (AndroidUtilities.dp(48.0f) - this.searchFieldHeight));
                    if (tabStrip != null) {
                        tabStrip.setTranslationY(0.0f);
                    }
                    if (gridView == this.stickersGridView) {
                        gridView.setPadding(0, AndroidUtilities.dp(52.0f), 0, 0);
                    } else if (gridView == this.emojiGridView) {
                        gridView.setPadding(0, AndroidUtilities.dp(38.0f), 0, 0);
                    }
                } else {
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.searchAnimation = animatorSet2;
                    if (tabStrip != null) {
                        animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(tabStrip, View.TRANSLATION_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(gridView, View.TRANSLATION_Y, new float[]{(float) (AndroidUtilities.dp(48.0f) - this.searchFieldHeight)}), ObjectAnimator.ofFloat(currentField, View.TRANSLATION_Y, new float[]{(float) (AndroidUtilities.dp(48.0f) - this.searchFieldHeight)})});
                    } else {
                        animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(gridView, View.TRANSLATION_Y, new float[]{(float) (-this.searchFieldHeight)}), ObjectAnimator.ofFloat(currentField, View.TRANSLATION_Y, new float[]{(float) (-this.searchFieldHeight)})});
                    }
                    this.searchAnimation.setDuration(200);
                    this.searchAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                    this.searchAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (animation.equals(EmojiViewV2.this.searchAnimation)) {
                                int findFirstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                                int firstVisPos = layoutManager.findFirstVisibleItemPosition();
                                int top = 0;
                                if (firstVisPos != -1) {
                                    top = (int) (((float) layoutManager.findViewByPosition(firstVisPos).getTop()) + gridView.getTranslationY());
                                }
                                gridView.setTranslationY(0.0f);
                                if (gridView == EmojiViewV2.this.stickersGridView) {
                                    gridView.setPadding(0, AndroidUtilities.dp(52.0f), 0, 0);
                                } else if (gridView == EmojiViewV2.this.emojiGridView) {
                                    gridView.setPadding(0, AndroidUtilities.dp(38.0f), 0, 0);
                                }
                                if (gridView == EmojiViewV2.this.gifGridView) {
                                    layoutManager.scrollToPositionWithOffset(1, 0);
                                } else if (firstVisPos != -1) {
                                    layoutManager.scrollToPositionWithOffset(firstVisPos, top - gridView.getPaddingTop());
                                }
                                AnimatorSet unused = EmojiViewV2.this.searchAnimation = null;
                            }
                        }

                        public void onAnimationCancel(Animator animation) {
                            if (animation.equals(EmojiViewV2.this.searchAnimation)) {
                                AnimatorSet unused = EmojiViewV2.this.searchAnimation = null;
                            }
                        }
                    });
                    this.searchAnimation.start();
                }
            }
            a++;
            long j2 = scrollToSet;
        }
        if (!z) {
            this.delegate.onSearchOpenClose(0);
        }
        showBottomTab(true, z);
    }

    /* access modifiers changed from: private */
    public void checkStickersSearchFieldScroll(boolean isLayout2) {
        RecyclerListView recyclerListView;
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        boolean z = false;
        if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
            RecyclerView.ViewHolder holder = this.stickersGridView.findViewHolderForAdapterPosition(0);
            if (holder == null) {
                this.stickersSearchField.showShadow(true, !isLayout2);
                return;
            }
            SearchField searchField = this.stickersSearchField;
            if (holder.itemView.getTop() < this.stickersGridView.getPaddingTop()) {
                z = true;
            }
            searchField.showShadow(z, !isLayout2);
        } else if (this.stickersSearchField != null && (recyclerListView = this.stickersGridView) != null) {
            RecyclerView.ViewHolder holder2 = recyclerListView.findViewHolderForAdapterPosition(0);
            if (holder2 != null) {
                this.stickersSearchField.setTranslationY((float) holder2.itemView.getTop());
            } else {
                this.stickersSearchField.setTranslationY((float) (-this.searchFieldHeight));
            }
            this.stickersSearchField.showShadow(false, !isLayout2);
        }
    }

    /* access modifiers changed from: private */
    public void checkBottomTabScroll(float dy) {
        int offset;
        this.lastBottomScrollDy += dy;
        if (this.pager.getCurrentItem() == 0) {
            offset = AndroidUtilities.dp(38.0f);
        } else {
            offset = AndroidUtilities.dp(48.0f);
        }
        float f = this.lastBottomScrollDy;
        if (f >= ((float) offset)) {
            showBottomTab(false, true);
        } else if (f <= ((float) (-offset))) {
            showBottomTab(true, true);
        } else if ((this.bottomTabContainer.getTag() == null && this.lastBottomScrollDy < 0.0f) || (this.bottomTabContainer.getTag() != null && this.lastBottomScrollDy > 0.0f)) {
            this.lastBottomScrollDy = 0.0f;
        }
    }

    /* access modifiers changed from: private */
    public void showBackspaceButton(final boolean show, boolean animated) {
        if (show && this.backspaceButton.getTag() == null) {
            return;
        }
        if (show || this.backspaceButton.getTag() == null) {
            AnimatorSet animatorSet = this.backspaceButtonAnimation;
            int i = null;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.backspaceButtonAnimation = null;
            }
            ImageView imageView = this.backspaceButton;
            if (!show) {
                i = 1;
            }
            imageView.setTag(i);
            int i2 = 0;
            float f = 1.0f;
            if (animated) {
                if (show) {
                    this.backspaceButton.setVisibility(0);
                }
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.backspaceButtonAnimation = animatorSet2;
                Animator[] animatorArr = new Animator[3];
                ImageView imageView2 = this.backspaceButton;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = show ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(imageView2, property, fArr);
                ImageView imageView3 = this.backspaceButton;
                Property property2 = View.SCALE_X;
                float[] fArr2 = new float[1];
                fArr2[0] = show ? 1.0f : 0.0f;
                animatorArr[1] = ObjectAnimator.ofFloat(imageView3, property2, fArr2);
                ImageView imageView4 = this.backspaceButton;
                Property property3 = View.SCALE_Y;
                float[] fArr3 = new float[1];
                if (!show) {
                    f = 0.0f;
                }
                fArr3[0] = f;
                animatorArr[2] = ObjectAnimator.ofFloat(imageView4, property3, fArr3);
                animatorSet2.playTogether(animatorArr);
                this.backspaceButtonAnimation.setDuration(200);
                this.backspaceButtonAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                this.backspaceButtonAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (!show) {
                            EmojiViewV2.this.backspaceButton.setVisibility(4);
                        }
                    }
                });
                this.backspaceButtonAnimation.start();
                return;
            }
            this.backspaceButton.setAlpha(show ? 1.0f : 0.0f);
            this.backspaceButton.setScaleX(show ? 1.0f : 0.0f);
            ImageView imageView5 = this.backspaceButton;
            if (!show) {
                f = 0.0f;
            }
            imageView5.setScaleY(f);
            ImageView imageView6 = this.backspaceButton;
            if (!show) {
                i2 = 4;
            }
            imageView6.setVisibility(i2);
        }
    }

    /* access modifiers changed from: private */
    public void showStickerSettingsButton(final boolean show, boolean animated) {
        ImageView imageView = this.stickerSettingsButton;
        if (imageView != null) {
            if (show && imageView.getTag() == null) {
                return;
            }
            if (show || this.stickerSettingsButton.getTag() == null) {
                AnimatorSet animatorSet = this.stickersButtonAnimation;
                int i = null;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.stickersButtonAnimation = null;
                }
                ImageView imageView2 = this.stickerSettingsButton;
                if (!show) {
                    i = 1;
                }
                imageView2.setTag(i);
                int i2 = 0;
                float f = 1.0f;
                if (animated) {
                    if (show) {
                        this.stickerSettingsButton.setVisibility(0);
                    }
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.stickersButtonAnimation = animatorSet2;
                    Animator[] animatorArr = new Animator[3];
                    ImageView imageView3 = this.stickerSettingsButton;
                    Property property = View.ALPHA;
                    float[] fArr = new float[1];
                    fArr[0] = show ? 1.0f : 0.0f;
                    animatorArr[0] = ObjectAnimator.ofFloat(imageView3, property, fArr);
                    ImageView imageView4 = this.stickerSettingsButton;
                    Property property2 = View.SCALE_X;
                    float[] fArr2 = new float[1];
                    fArr2[0] = show ? 1.0f : 0.0f;
                    animatorArr[1] = ObjectAnimator.ofFloat(imageView4, property2, fArr2);
                    ImageView imageView5 = this.stickerSettingsButton;
                    Property property3 = View.SCALE_Y;
                    float[] fArr3 = new float[1];
                    if (!show) {
                        f = 0.0f;
                    }
                    fArr3[0] = f;
                    animatorArr[2] = ObjectAnimator.ofFloat(imageView5, property3, fArr3);
                    animatorSet2.playTogether(animatorArr);
                    this.stickersButtonAnimation.setDuration(200);
                    this.stickersButtonAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    this.stickersButtonAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (!show) {
                                EmojiViewV2.this.stickerSettingsButton.setVisibility(4);
                            }
                        }
                    });
                    this.stickersButtonAnimation.start();
                    return;
                }
                this.stickerSettingsButton.setAlpha(show ? 1.0f : 0.0f);
                this.stickerSettingsButton.setScaleX(show ? 1.0f : 0.0f);
                ImageView imageView6 = this.stickerSettingsButton;
                if (!show) {
                    f = 0.0f;
                }
                imageView6.setScaleY(f);
                ImageView imageView7 = this.stickerSettingsButton;
                if (!show) {
                    i2 = 4;
                }
                imageView7.setVisibility(i2);
            }
        }
    }

    /* access modifiers changed from: private */
    public void showBottomTab(boolean show, boolean animated) {
        float f;
        float f2;
        float f3 = 0.0f;
        this.lastBottomScrollDy = 0.0f;
        if (show && this.bottomTabContainer.getTag() == null) {
            return;
        }
        if (show || this.bottomTabContainer.getTag() == null) {
            EmojiViewDelegate emojiViewDelegate = this.delegate;
            if (emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) {
                AnimatorSet animatorSet = this.bottomTabContainerAnimation;
                int i = null;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.bottomTabContainerAnimation = null;
                }
                FrameLayout frameLayout = this.bottomTabContainer;
                if (!show) {
                    i = 1;
                }
                frameLayout.setTag(i);
                float f4 = 54.0f;
                if (animated) {
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.bottomTabContainerAnimation = animatorSet2;
                    Animator[] animatorArr = new Animator[2];
                    FrameLayout frameLayout2 = this.bottomTabContainer;
                    Property property = View.TRANSLATION_Y;
                    float[] fArr = new float[1];
                    if (show) {
                        f2 = 0.0f;
                    } else {
                        if (this.needEmojiSearch) {
                            f4 = 49.0f;
                        }
                        f2 = (float) AndroidUtilities.dp(f4);
                    }
                    fArr[0] = f2;
                    animatorArr[0] = ObjectAnimator.ofFloat(frameLayout2, property, fArr);
                    View view = this.shadowLine;
                    Property property2 = View.TRANSLATION_Y;
                    float[] fArr2 = new float[1];
                    if (!show) {
                        f3 = (float) AndroidUtilities.dp(49.0f);
                    }
                    fArr2[0] = f3;
                    animatorArr[1] = ObjectAnimator.ofFloat(view, property2, fArr2);
                    animatorSet2.playTogether(animatorArr);
                    this.bottomTabContainerAnimation.setDuration(200);
                    this.bottomTabContainerAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    this.bottomTabContainerAnimation.start();
                    return;
                }
                FrameLayout frameLayout3 = this.bottomTabContainer;
                if (show) {
                    f = 0.0f;
                } else {
                    if (this.needEmojiSearch) {
                        f4 = 49.0f;
                    }
                    f = (float) AndroidUtilities.dp(f4);
                }
                frameLayout3.setTranslationY(f);
                View view2 = this.shadowLine;
                if (!show) {
                    f3 = (float) AndroidUtilities.dp(49.0f);
                }
                view2.setTranslationY(f3);
            }
        }
    }

    /* access modifiers changed from: private */
    public void checkStickersTabY(View list, int dy) {
        RecyclerListView recyclerListView;
        RecyclerView.ViewHolder holder;
        if (list == null) {
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
            this.stickersMinusDy = 0;
            scrollSlidingTabStrip.setTranslationY((float) 0);
        } else if (list.getVisibility() == 0) {
            EmojiViewDelegate emojiViewDelegate = this.delegate;
            if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
                return;
            }
            if (dy <= 0 || (recyclerListView = this.stickersGridView) == null || recyclerListView.getVisibility() != 0 || (holder = this.stickersGridView.findViewHolderForAdapterPosition(0)) == null || holder.itemView.getTop() + this.searchFieldHeight < this.stickersGridView.getPaddingTop()) {
                int i = this.stickersMinusDy - dy;
                this.stickersMinusDy = i;
                if (i > 0) {
                    this.stickersMinusDy = 0;
                } else if (i < (-AndroidUtilities.dp(288.0f))) {
                    this.stickersMinusDy = -AndroidUtilities.dp(288.0f);
                }
                this.stickersTab.setTranslationY((float) Math.max(-AndroidUtilities.dp(48.0f), this.stickersMinusDy));
            }
        }
    }

    /* access modifiers changed from: private */
    public void checkEmojiSearchFieldScroll(boolean isLayout2) {
        RecyclerListView recyclerListView;
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        boolean z = false;
        boolean z2 = true;
        if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
            RecyclerView.ViewHolder holder = this.emojiGridView.findViewHolderForAdapterPosition(0);
            if (holder == null) {
                this.emojiSearchField.showShadow(true, !isLayout2);
            } else {
                SearchField searchField = this.emojiSearchField;
                if (holder.itemView.getTop() >= this.emojiGridView.getPaddingTop()) {
                    z2 = false;
                }
                searchField.showShadow(z2, !isLayout2);
            }
            showEmojiShadow(false, !isLayout2);
        } else if (this.emojiSearchField != null && (recyclerListView = this.emojiGridView) != null) {
            RecyclerView.ViewHolder holder2 = recyclerListView.findViewHolderForAdapterPosition(0);
            if (holder2 != null) {
                this.emojiSearchField.setTranslationY((float) holder2.itemView.getTop());
            } else {
                this.emojiSearchField.setTranslationY((float) (-this.searchFieldHeight));
            }
            this.emojiSearchField.showShadow(false, !isLayout2);
            if (holder2 == null || ((float) holder2.itemView.getTop()) < ((float) (AndroidUtilities.dp(38.0f) - this.searchFieldHeight)) + this.emojiTabs.getTranslationY()) {
                z = true;
            }
            showEmojiShadow(z, !isLayout2);
        }
    }

    /* access modifiers changed from: private */
    public void checkEmojiTabY(View list, int dy) {
        RecyclerListView recyclerListView;
        RecyclerView.ViewHolder holder;
        if (list == null) {
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.emojiTabs;
            this.emojiMinusDy = 0;
            scrollSlidingTabStrip.setTranslationY((float) 0);
            this.emojiTabsShadow.setTranslationY((float) this.emojiMinusDy);
        } else if (list.getVisibility() == 0) {
            EmojiViewDelegate emojiViewDelegate = this.delegate;
            if (emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) {
                if (dy > 0 && (recyclerListView = this.emojiGridView) != null && recyclerListView.getVisibility() == 0 && (holder = this.emojiGridView.findViewHolderForAdapterPosition(0)) != null) {
                    if (holder.itemView.getTop() + (this.needEmojiSearch ? this.searchFieldHeight : 0) >= this.emojiGridView.getPaddingTop()) {
                        return;
                    }
                }
                int i = this.emojiMinusDy - dy;
                this.emojiMinusDy = i;
                if (i > 0) {
                    this.emojiMinusDy = 0;
                } else if (i < (-AndroidUtilities.dp(288.0f))) {
                    this.emojiMinusDy = -AndroidUtilities.dp(288.0f);
                }
                this.emojiTabs.setTranslationY((float) Math.max(-AndroidUtilities.dp(38.0f), this.emojiMinusDy));
                this.emojiTabsShadow.setTranslationY(this.emojiTabs.getTranslationY());
            }
        }
    }

    /* access modifiers changed from: private */
    public void checkGifSearchFieldScroll(boolean isLayout2) {
        RecyclerListView recyclerListView;
        GifSearchAdapter gifSearchAdapter2;
        int position;
        RecyclerListView recyclerListView2 = this.gifGridView;
        boolean z = true;
        if (recyclerListView2 != null && recyclerListView2.getAdapter() == (gifSearchAdapter2 = this.gifSearchAdapter) && !gifSearchAdapter2.searchEndReached && this.gifSearchAdapter.reqId == 0 && !this.gifSearchAdapter.results.isEmpty() && (position = this.gifLayoutManager.findLastVisibleItemPosition()) != -1 && position > this.gifLayoutManager.getItemCount() - 5) {
            GifSearchAdapter gifSearchAdapter3 = this.gifSearchAdapter;
            gifSearchAdapter3.search(gifSearchAdapter3.lastSearchImageString, this.gifSearchAdapter.nextSearchOffset, true);
        }
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
            RecyclerView.ViewHolder holder = this.gifGridView.findViewHolderForAdapterPosition(0);
            if (holder == null) {
                this.gifSearchField.showShadow(true, !isLayout2);
                return;
            }
            SearchField searchField = this.gifSearchField;
            if (holder.itemView.getTop() >= this.gifGridView.getPaddingTop()) {
                z = false;
            }
            searchField.showShadow(z, !isLayout2);
        } else if (this.gifSearchField != null && (recyclerListView = this.gifGridView) != null) {
            RecyclerView.ViewHolder holder2 = recyclerListView.findViewHolderForAdapterPosition(0);
            if (holder2 != null) {
                this.gifSearchField.setTranslationY((float) holder2.itemView.getTop());
            } else {
                this.gifSearchField.setTranslationY((float) (-this.searchFieldHeight));
            }
            this.gifSearchField.showShadow(false, !isLayout2);
        }
    }

    /* access modifiers changed from: private */
    public void checkScroll() {
        int firstTab;
        int firstVisibleItem = this.stickersLayoutManager.findFirstVisibleItemPosition();
        if (firstVisibleItem != -1 && this.stickersGridView != null) {
            if (this.favTabBum > 0) {
                firstTab = this.favTabBum;
            } else if (this.recentTabBum > 0) {
                firstTab = this.recentTabBum;
            } else {
                firstTab = this.stickersTabOffset;
            }
            this.stickersTab.onPageScrolled(this.stickersGridAdapter.getTabForPosition(firstVisibleItem), firstTab);
        }
    }

    /* access modifiers changed from: private */
    public void saveNewPage() {
        int newPage;
        ViewPager viewPager = this.pager;
        if (viewPager != null) {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem == 2) {
                newPage = 1;
            } else if (currentItem == 1) {
                newPage = 2;
            } else {
                newPage = 0;
            }
            if (this.currentPage != newPage) {
                this.currentPage = newPage;
                MessagesController.getGlobalEmojiSettings().edit().putInt("selected_page", newPage).commit();
            }
        }
    }

    public void clearRecentEmoji() {
        Emoji.clearRecentEmoji();
        this.emojiAdapter.notifyDataSetChanged();
    }

    /* access modifiers changed from: private */
    public void showTrendingTab(boolean show) {
        if (show) {
            this.trendingGridView.setVisibility(0);
            this.stickersGridView.setVisibility(8);
            this.stickersSearchField.setVisibility(8);
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
            int i = this.trendingTabNum;
            int i2 = this.recentTabBum;
            if (i2 <= 0) {
                i2 = this.stickersTabOffset;
            }
            scrollSlidingTabStrip.onPageScrolled(i, i2);
            saveNewPage();
            return;
        }
        this.trendingGridView.setVisibility(8);
        this.stickersGridView.setVisibility(0);
        this.stickersSearchField.setVisibility(0);
    }

    /* access modifiers changed from: private */
    public void onPageScrolled(int position, int width, int positionOffsetPixels) {
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if (emojiViewDelegate != null) {
            int i = 0;
            if (position == 1) {
                if (positionOffsetPixels != 0) {
                    i = 2;
                }
                emojiViewDelegate.onTabOpened(i);
            } else if (position == 2) {
                emojiViewDelegate.onTabOpened(3);
            } else {
                emojiViewDelegate.onTabOpened(0);
            }
        }
    }

    /* access modifiers changed from: private */
    public void postBackspaceRunnable(int time) {
        AndroidUtilities.runOnUIThread(new Runnable(time) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                EmojiViewV2.this.lambda$postBackspaceRunnable$8$EmojiViewV2(this.f$1);
            }
        }, (long) time);
    }

    public /* synthetic */ void lambda$postBackspaceRunnable$8$EmojiViewV2(int time) {
        if (this.backspacePressed) {
            EmojiViewDelegate emojiViewDelegate = this.delegate;
            if (emojiViewDelegate != null && emojiViewDelegate.onBackspace()) {
                this.backspaceButton.performHapticFeedback(3);
            }
            this.backspaceOnce = true;
            postBackspaceRunnable(Math.max(50, time - 100));
        }
    }

    public void switchToGifRecent() {
        showBackspaceButton(false, false);
        showStickerSettingsButton(false, false);
        this.pager.setCurrentItem(1, false);
    }

    /* access modifiers changed from: private */
    public void updateEmojiTabs() {
        int newHas = Emoji.recentEmoji.isEmpty() ^ 1;
        int i = this.hasRecentEmoji;
        if (i == -1 || i != newHas) {
            this.hasRecentEmoji = (int) newHas;
            this.emojiTabs.removeTabs();
            String[] descriptions = {LocaleController.getString("RecentStickers", R.string.RecentStickers), LocaleController.getString("Emoji1", R.string.Emoji1), LocaleController.getString("Emoji2", R.string.Emoji2), LocaleController.getString("Emoji3", R.string.Emoji3), LocaleController.getString("Emoji4", R.string.Emoji4), LocaleController.getString("Emoji5", R.string.Emoji5), LocaleController.getString("Emoji6", R.string.Emoji6), LocaleController.getString("Emoji7", R.string.Emoji7), LocaleController.getString("Emoji8", R.string.Emoji8)};
            for (int a = 0; a < this.emojiIcons.length; a++) {
                if (a != 0 || !Emoji.recentEmoji.isEmpty()) {
                    this.emojiTabs.addIconTab(this.emojiIcons[a]).setContentDescription(descriptions[a]);
                }
            }
            this.emojiTabs.updateTabStyles();
        }
    }

    /* access modifiers changed from: private */
    public void updateStickerTabs() {
        TLObject thumb;
        ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
        if (scrollSlidingTabStrip != null) {
            this.recentTabBum = -2;
            this.favTabBum = -2;
            this.trendingTabNum = -2;
            this.stickersTabOffset = 0;
            int lastPosition = scrollSlidingTabStrip.getCurrentPosition();
            this.stickersTab.removeTabs();
            ArrayList<Long> unread = MediaDataController.getInstance(this.currentAccount).getUnreadStickerSets();
            boolean hasStickers = false;
            TrendingGridAdapter trendingGridAdapter2 = this.trendingGridAdapter;
            int i = 2;
            if (!(trendingGridAdapter2 == null || trendingGridAdapter2.getItemCount() == 0 || unread.isEmpty())) {
                TextView addIconTabWithCounter = this.stickersTab.addIconTabWithCounter(this.stickerIcons[2]);
                this.stickersCounter = addIconTabWithCounter;
                int i2 = this.stickersTabOffset;
                this.trendingTabNum = i2;
                this.stickersTabOffset = i2 + 1;
                addIconTabWithCounter.setText(String.format("%d", new Object[]{Integer.valueOf(unread.size())}));
            }
            if (!this.favouriteStickers.isEmpty()) {
                int i3 = this.stickersTabOffset;
                this.favTabBum = i3;
                this.stickersTabOffset = i3 + 1;
                this.stickersTab.addIconTab(this.stickerIcons[1]).setContentDescription(LocaleController.getString("FavoriteStickers", R.string.FavoriteStickers));
                hasStickers = true;
            }
            if (!this.recentStickers.isEmpty()) {
                int i4 = this.stickersTabOffset;
                this.recentTabBum = i4;
                this.stickersTabOffset = i4 + 1;
                this.stickersTab.addIconTab(this.stickerIcons[0]).setContentDescription(LocaleController.getString("RecentStickers", R.string.RecentStickers));
                hasStickers = true;
            }
            this.stickerSets.clear();
            TLRPC.TL_messages_stickerSet tL_messages_stickerSet = null;
            this.groupStickerSet = null;
            this.groupStickerPackPosition = -1;
            this.groupStickerPackNum = -10;
            ArrayList<TLRPC.TL_messages_stickerSet> packs = MediaDataController.getInstance(this.currentAccount).getStickerSets(0);
            for (int a = 0; a < packs.size(); a++) {
                TLRPC.TL_messages_stickerSet pack = packs.get(a);
                if (!pack.set.archived && pack.documents != null && !pack.documents.isEmpty()) {
                    this.stickerSets.add(pack);
                    hasStickers = true;
                }
            }
            if (this.info != null) {
                long hiddenStickerSetId = MessagesController.getEmojiSettings(this.currentAccount).getLong("group_hide_stickers_" + this.info.id, -1);
                TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.info.id));
                if (chat == null || this.info.stickerset == null || !ChatObject.hasAdminRights(chat)) {
                    this.groupStickersHidden = hiddenStickerSetId != -1;
                } else if (this.info.stickerset != null) {
                    this.groupStickersHidden = hiddenStickerSetId == this.info.stickerset.id;
                }
                if (this.info.stickerset != null) {
                    TLRPC.TL_messages_stickerSet pack2 = MediaDataController.getInstance(this.currentAccount).getGroupStickerSetById(this.info.stickerset);
                    if (!(pack2 == null || pack2.documents == null || pack2.documents.isEmpty() || pack2.set == null)) {
                        TLRPC.TL_messages_stickerSet set = new TLRPC.TL_messages_stickerSet();
                        set.documents = pack2.documents;
                        set.packs = pack2.packs;
                        set.set = pack2.set;
                        if (this.groupStickersHidden) {
                            this.groupStickerPackNum = this.stickerSets.size();
                            this.stickerSets.add(set);
                        } else {
                            this.groupStickerPackNum = 0;
                            this.stickerSets.add(0, set);
                        }
                        if (this.info.can_set_stickers) {
                            tL_messages_stickerSet = set;
                        }
                        this.groupStickerSet = tL_messages_stickerSet;
                    }
                } else if (this.info.can_set_stickers) {
                    TLRPC.TL_messages_stickerSet pack3 = new TLRPC.TL_messages_stickerSet();
                    if (this.groupStickersHidden) {
                        this.groupStickerPackNum = this.stickerSets.size();
                        this.stickerSets.add(pack3);
                    } else {
                        this.groupStickerPackNum = 0;
                        this.stickerSets.add(0, pack3);
                    }
                }
            }
            int a2 = 0;
            while (a2 < this.stickerSets.size()) {
                if (a2 == this.groupStickerPackNum) {
                    TLRPC.Chat chat2 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.info.id));
                    if (chat2 == null) {
                        this.stickerSets.remove(0);
                        a2--;
                    } else {
                        this.stickersTab.addStickerTab(chat2);
                        hasStickers = true;
                    }
                } else {
                    TLRPC.TL_messages_stickerSet stickerSet = this.stickerSets.get(a2);
                    TLRPC.Document document = stickerSet.documents.get(0);
                    if (stickerSet.set.thumb instanceof TLRPC.TL_photoSize) {
                        thumb = stickerSet.set.thumb;
                    } else {
                        thumb = document;
                    }
                    this.stickersTab.addStickerTab(thumb, document, stickerSet).setContentDescription(stickerSet.set.title + ", " + LocaleController.getString("AccDescrStickerSet", R.string.AccDescrStickerSet));
                    hasStickers = true;
                }
                a2++;
            }
            TrendingGridAdapter trendingGridAdapter3 = this.trendingGridAdapter;
            if (!(trendingGridAdapter3 == null || trendingGridAdapter3.getItemCount() == 0 || !unread.isEmpty())) {
                this.trendingTabNum = this.stickersTabOffset + this.stickerSets.size();
                this.stickersTab.addIconTab(this.stickerIcons[2]).setContentDescription(LocaleController.getString("FeaturedStickers", R.string.FeaturedStickers));
            }
            this.stickersTab.updateTabStyles();
            if (lastPosition != 0) {
                this.stickersTab.onPageScrolled(lastPosition, lastPosition);
            }
            checkPanels();
            if ((!hasStickers || (this.trendingTabNum == 0 && MediaDataController.getInstance(this.currentAccount).areAllTrendingStickerSetsUnread())) && this.trendingTabNum >= 0) {
                if (this.scrolledToTrending == 0) {
                    showTrendingTab(true);
                    if (!hasStickers) {
                        i = 1;
                    }
                    this.scrolledToTrending = i;
                }
            } else if (this.scrolledToTrending == 1) {
                showTrendingTab(false);
                checkScroll();
                this.stickersTab.cancelPositionAnimation();
            }
        }
    }

    private void checkPanels() {
        int firstTab;
        RecyclerListView recyclerListView;
        if (this.stickersTab != null) {
            if (this.trendingTabNum == -2 && (recyclerListView = this.trendingGridView) != null && recyclerListView.getVisibility() == 0) {
                this.trendingGridView.setVisibility(8);
                this.stickersGridView.setVisibility(0);
                this.stickersSearchField.setVisibility(0);
            }
            RecyclerListView recyclerListView2 = this.trendingGridView;
            if (recyclerListView2 == null || recyclerListView2.getVisibility() != 0) {
                int position = this.stickersLayoutManager.findFirstVisibleItemPosition();
                if (position != -1) {
                    if (this.favTabBum > 0) {
                        firstTab = this.favTabBum;
                    } else if (this.recentTabBum > 0) {
                        firstTab = this.recentTabBum;
                    } else {
                        firstTab = this.stickersTabOffset;
                    }
                    this.stickersTab.onPageScrolled(this.stickersGridAdapter.getTabForPosition(position), firstTab);
                    return;
                }
                return;
            }
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
            int i = this.trendingTabNum;
            int i2 = this.recentTabBum;
            if (i2 <= 0) {
                i2 = this.stickersTabOffset;
            }
            scrollSlidingTabStrip.onPageScrolled(i, i2);
        }
    }

    public void addRecentSticker(TLRPC.Document document) {
        if (document != null) {
            MediaDataController.getInstance(this.currentAccount).addRecentSticker(0, (Object) null, document, (int) (System.currentTimeMillis() / 1000), false);
            boolean wasEmpty = this.recentStickers.isEmpty();
            this.recentStickers = MediaDataController.getInstance(this.currentAccount).getRecentStickers(0);
            StickersGridAdapter stickersGridAdapter2 = this.stickersGridAdapter;
            if (stickersGridAdapter2 != null) {
                stickersGridAdapter2.notifyDataSetChanged();
            }
            if (wasEmpty) {
                updateStickerTabs();
            }
        }
    }

    public void addRecentGif(TLRPC.Document document) {
        if (document != null) {
            boolean wasEmpty = this.recentGifs.isEmpty();
            this.recentGifs = MediaDataController.getInstance(this.currentAccount).getRecentGifs();
            GifAdapter gifAdapter2 = this.gifAdapter;
            if (gifAdapter2 != null) {
                gifAdapter2.notifyDataSetChanged();
            }
            if (wasEmpty) {
                updateStickerTabs();
            }
        }
    }

    public void requestLayout() {
        if (!this.isLayout) {
            super.requestLayout();
        }
    }

    public void updateColors() {
        SearchField searchField;
        if (AndroidUtilities.isInMultiwindow || this.forseMultiwindowLayout) {
            Drawable background = getBackground();
            if (background != null) {
                background.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_emojiPanelBackground), PorterDuff.Mode.MULTIPLY));
            }
        } else {
            setBackgroundColor(Theme.getColor(Theme.key_chat_emojiPanelBackground));
            if (this.needEmojiSearch) {
                this.bottomTabContainerBackground.setBackgroundColor(Theme.getColor(Theme.key_chat_emojiPanelBackground));
            }
        }
        ScrollSlidingTabStrip scrollSlidingTabStrip = this.emojiTabs;
        if (scrollSlidingTabStrip != null) {
            scrollSlidingTabStrip.setBackgroundColor(Theme.getColor(Theme.key_chat_emojiPanelBackground));
            this.emojiTabsShadow.setBackgroundColor(Theme.getColor(Theme.key_chat_emojiPanelShadowLine));
        }
        for (int a = 0; a < 3; a++) {
            if (a == 0) {
                searchField = this.stickersSearchField;
            } else if (a == 1) {
                searchField = this.emojiSearchField;
            } else {
                searchField = this.gifSearchField;
            }
            if (searchField != null) {
                searchField.backgroundView.setBackgroundColor(Theme.getColor(Theme.key_chat_emojiPanelBackground));
                searchField.shadowView.setBackgroundColor(Theme.getColor(Theme.key_chat_emojiPanelShadowLine));
                searchField.clearSearchImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_emojiSearchIcon), PorterDuff.Mode.MULTIPLY));
                searchField.searchIconImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_emojiSearchIcon), PorterDuff.Mode.MULTIPLY));
                Theme.setDrawableColorByKey(searchField.searchBackground.getBackground(), Theme.key_chat_emojiSearchBackground);
                searchField.searchBackground.invalidate();
                searchField.searchEditText.setHintTextColor(Theme.getColor(Theme.key_chat_emojiSearchIcon));
                searchField.searchEditText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            }
        }
        Paint paint = this.dotPaint;
        if (paint != null) {
            paint.setColor(Theme.getColor(Theme.key_chat_emojiPanelNewTrending));
        }
        RecyclerListView recyclerListView = this.emojiGridView;
        if (recyclerListView != null) {
            recyclerListView.setGlowColor(Theme.getColor(Theme.key_chat_emojiPanelBackground));
        }
        RecyclerListView recyclerListView2 = this.stickersGridView;
        if (recyclerListView2 != null) {
            recyclerListView2.setGlowColor(Theme.getColor(Theme.key_chat_emojiPanelBackground));
        }
        RecyclerListView recyclerListView3 = this.trendingGridView;
        if (recyclerListView3 != null) {
            recyclerListView3.setGlowColor(Theme.getColor(Theme.key_chat_emojiPanelBackground));
        }
        ScrollSlidingTabStrip scrollSlidingTabStrip2 = this.stickersTab;
        if (scrollSlidingTabStrip2 != null) {
            scrollSlidingTabStrip2.setIndicatorColor(Theme.getColor(Theme.key_chat_emojiPanelStickerPackSelectorLine));
            this.stickersTab.setUnderlineColor(Theme.getColor(Theme.key_chat_emojiPanelShadowLine));
            this.stickersTab.setBackgroundColor(Theme.getColor(Theme.key_chat_emojiPanelBackground));
        }
        ImageView imageView = this.backspaceButton;
        if (imageView != null) {
            imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_emojiPanelBackspace), PorterDuff.Mode.MULTIPLY));
            Theme.setSelectorDrawableColor(this.backspaceButton.getBackground(), Theme.getColor(Theme.key_chat_emojiPanelBackground), false);
            Theme.setSelectorDrawableColor(this.backspaceButton.getBackground(), Theme.getColor(Theme.key_chat_emojiPanelBackground), true);
        }
        ImageView imageView2 = this.stickerSettingsButton;
        if (imageView2 != null) {
            imageView2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_emojiPanelBackspace), PorterDuff.Mode.MULTIPLY));
        }
        ImageView imageView3 = this.searchButton;
        if (imageView3 != null) {
            imageView3.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_emojiPanelBackspace), PorterDuff.Mode.MULTIPLY));
        }
        View view = this.shadowLine;
        if (view != null) {
            view.setBackgroundColor(Theme.getColor(Theme.key_chat_emojiPanelShadowLine));
        }
        TextView textView = this.mediaBanTooltip;
        if (textView != null) {
            ((ShapeDrawable) textView.getBackground()).getPaint().setColor(Theme.getColor(Theme.key_chat_gifSaveHintBackground));
            this.mediaBanTooltip.setTextColor(Theme.getColor(Theme.key_chat_gifSaveHintText));
        }
        TextView textView2 = this.stickersCounter;
        if (textView2 != null) {
            textView2.setTextColor(Theme.getColor(Theme.key_chat_emojiPanelBadgeText));
            Theme.setDrawableColor(this.stickersCounter.getBackground(), Theme.getColor(Theme.key_chat_emojiPanelBadgeBackground));
            this.stickersCounter.invalidate();
        }
        int a2 = 0;
        while (true) {
            Drawable[] drawableArr = this.tabIcons;
            if (a2 >= drawableArr.length) {
                break;
            }
            Theme.setEmojiDrawableColor(drawableArr[a2], Theme.getColor(Theme.key_chat_emojiBottomPanelIcon), false);
            Theme.setEmojiDrawableColor(this.tabIcons[a2], Theme.getColor(Theme.key_chat_emojiPanelIconSelected), true);
            a2++;
        }
        int a3 = 0;
        while (true) {
            Drawable[] drawableArr2 = this.emojiIcons;
            if (a3 >= drawableArr2.length) {
                break;
            }
            Theme.setEmojiDrawableColor(drawableArr2[a3], Theme.getColor(Theme.key_chat_emojiPanelIcon), false);
            Theme.setEmojiDrawableColor(this.emojiIcons[a3], Theme.getColor(Theme.key_chat_emojiPanelIconSelected), true);
            a3++;
        }
        int a4 = 0;
        while (true) {
            Drawable[] drawableArr3 = this.stickerIcons;
            if (a4 < drawableArr3.length) {
                Theme.setEmojiDrawableColor(drawableArr3[a4], Theme.getColor(Theme.key_chat_emojiPanelIcon), false);
                Theme.setEmojiDrawableColor(this.stickerIcons[a4], Theme.getColor(Theme.key_chat_emojiPanelIconSelected), true);
                a4++;
            } else {
                return;
            }
        }
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.isLayout = true;
        if (AndroidUtilities.isInMultiwindow || this.forseMultiwindowLayout) {
            if (this.currentBackgroundType != 1) {
                if (Build.VERSION.SDK_INT >= 21) {
                    setOutlineProvider((ViewOutlineProvider) this.outlineProvider);
                    setClipToOutline(true);
                    setElevation((float) AndroidUtilities.dp(2.0f));
                }
                setBackgroundResource(R.drawable.smiles_popup);
                getBackground().setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_emojiPanelBackground), PorterDuff.Mode.MULTIPLY));
                if (this.needEmojiSearch) {
                    this.bottomTabContainerBackground.setBackgroundDrawable((Drawable) null);
                }
                this.currentBackgroundType = 1;
            }
        } else if (this.currentBackgroundType != 0) {
            if (Build.VERSION.SDK_INT >= 21) {
                setOutlineProvider((ViewOutlineProvider) null);
                setClipToOutline(false);
                setElevation(0.0f);
            }
            setBackgroundColor(Theme.getColor(Theme.key_chat_emojiPanelBackground));
            if (this.needEmojiSearch) {
                this.bottomTabContainerBackground.setBackgroundColor(Theme.getColor(Theme.key_chat_emojiPanelBackground));
            }
            this.currentBackgroundType = 0;
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(heightMeasureSpec), 1073741824));
        this.isLayout = false;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (this.lastNotifyWidth != right - left) {
            this.lastNotifyWidth = right - left;
            reloadStickersAdapter();
        }
        View parent = (View) getParent();
        if (parent != null) {
            int newHeight = bottom - top;
            int newHeight2 = parent.getHeight();
            if (!(this.lastNotifyHeight == newHeight && this.lastNotifyHeight2 == newHeight2)) {
                EmojiViewDelegate emojiViewDelegate = this.delegate;
                if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
                    this.bottomTabContainer.setTranslationY((float) AndroidUtilities.dp(49.0f));
                } else if (this.bottomTabContainer.getTag() == null) {
                    if (newHeight < this.lastNotifyHeight) {
                        this.bottomTabContainer.setTranslationY(0.0f);
                    } else {
                        this.bottomTabContainer.setTranslationY(-((getY() + ((float) getMeasuredHeight())) - ((float) parent.getHeight())));
                    }
                }
                this.lastNotifyHeight = newHeight;
                this.lastNotifyHeight2 = newHeight2;
            }
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    private void reloadStickersAdapter() {
        StickersGridAdapter stickersGridAdapter2 = this.stickersGridAdapter;
        if (stickersGridAdapter2 != null) {
            stickersGridAdapter2.notifyDataSetChanged();
        }
        TrendingGridAdapter trendingGridAdapter2 = this.trendingGridAdapter;
        if (trendingGridAdapter2 != null) {
            trendingGridAdapter2.notifyDataSetChanged();
        }
        StickersSearchGridAdapter stickersSearchGridAdapter2 = this.stickersSearchGridAdapter;
        if (stickersSearchGridAdapter2 != null) {
            stickersSearchGridAdapter2.notifyDataSetChanged();
        }
        if (ContentPreviewViewer.getInstance().isVisible()) {
            ContentPreviewViewer.getInstance().close();
        }
        ContentPreviewViewer.getInstance().reset();
    }

    public void setDelegate(EmojiViewDelegate emojiViewDelegate) {
        this.delegate = emojiViewDelegate;
    }

    public void setDragListener(DragListener listener) {
        this.dragListener = listener;
    }

    public void setChatInfo(TLRPC.ChatFull chatInfo) {
        this.info = chatInfo;
        updateStickerTabs();
    }

    public void invalidateViews() {
        this.emojiGridView.invalidateViews();
    }

    public void setForseMultiwindowLayout(boolean value) {
        this.forseMultiwindowLayout = value;
    }

    public void onOpen(boolean forceEmoji) {
        if (!(this.currentPage == 0 || this.currentChatId == 0)) {
            this.currentPage = 0;
        }
        if (this.currentPage == 0 || forceEmoji || this.views.size() == 1) {
            showBackspaceButton(true, false);
            showStickerSettingsButton(false, false);
            if (this.pager.getCurrentItem() != 0) {
                this.pager.setCurrentItem(0, !forceEmoji);
                return;
            }
            return;
        }
        int i = this.currentPage;
        if (i == 1) {
            showBackspaceButton(false, false);
            showStickerSettingsButton(true, false);
            if (this.pager.getCurrentItem() != 2) {
                this.pager.setCurrentItem(2, false);
            }
            if (this.stickersTab == null) {
                return;
            }
            if (this.trendingTabNum != 0 || !MediaDataController.getInstance(this.currentAccount).areAllTrendingStickerSetsUnread()) {
                int i2 = this.recentTabBum;
                if (i2 >= 0) {
                    this.stickersTab.selectTab(i2);
                    return;
                }
                int i3 = this.favTabBum;
                if (i3 >= 0) {
                    this.stickersTab.selectTab(i3);
                } else {
                    this.stickersTab.selectTab(this.stickersTabOffset);
                }
            } else {
                showTrendingTab(true);
            }
        } else if (i == 2) {
            showBackspaceButton(false, false);
            showStickerSettingsButton(false, false);
            if (this.pager.getCurrentItem() != 1) {
                this.pager.setCurrentItem(1, false);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.newEmojiSuggestionsAvailable);
        if (this.stickersGridAdapter != null) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recentImagesDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.featuredStickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.groupStickersDidLoad);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    EmojiViewV2.this.lambda$onAttachedToWindow$9$EmojiViewV2();
                }
            });
        }
    }

    public /* synthetic */ void lambda$onAttachedToWindow$9$EmojiViewV2() {
        updateStickerTabs();
        reloadStickersAdapter();
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility != 8) {
            Emoji.sortEmoji();
            this.emojiAdapter.notifyDataSetChanged();
            if (this.stickersGridAdapter != null) {
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recentDocumentsDidLoad);
                updateStickerTabs();
                reloadStickersAdapter();
            }
            TrendingGridAdapter trendingGridAdapter2 = this.trendingGridAdapter;
            if (trendingGridAdapter2 != null) {
                this.trendingLoaded = false;
                trendingGridAdapter2.notifyDataSetChanged();
            }
            checkDocuments(true);
            checkDocuments(false);
            MediaDataController.getInstance(this.currentAccount).loadRecents(0, true, true, false);
            MediaDataController.getInstance(this.currentAccount).loadRecents(0, false, true, false);
            MediaDataController.getInstance(this.currentAccount).loadRecents(2, false, true, false);
        }
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public void onDestroy() {
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.newEmojiSuggestionsAvailable);
        if (this.stickersGridAdapter != null) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recentDocumentsDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.featuredStickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupStickersDidLoad);
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EmojiPopupWindow emojiPopupWindow = this.pickerViewPopup;
        if (emojiPopupWindow != null && emojiPopupWindow.isShowing()) {
            this.pickerViewPopup.dismiss();
        }
    }

    private void checkDocuments(boolean isGif) {
        if (isGif) {
            this.recentGifs = MediaDataController.getInstance(this.currentAccount).getRecentGifs();
            GifAdapter gifAdapter2 = this.gifAdapter;
            if (gifAdapter2 != null) {
                gifAdapter2.notifyDataSetChanged();
                return;
            }
            return;
        }
        int previousCount = this.recentStickers.size();
        int previousCount2 = this.favouriteStickers.size();
        this.recentStickers = MediaDataController.getInstance(this.currentAccount).getRecentStickers(0);
        this.favouriteStickers = MediaDataController.getInstance(this.currentAccount).getRecentStickers(2);
        for (int a = 0; a < this.favouriteStickers.size(); a++) {
            TLRPC.Document favSticker = this.favouriteStickers.get(a);
            int b = 0;
            while (true) {
                if (b >= this.recentStickers.size()) {
                    break;
                }
                TLRPC.Document recSticker = this.recentStickers.get(b);
                if (recSticker.dc_id == favSticker.dc_id && recSticker.id == favSticker.id) {
                    this.recentStickers.remove(b);
                    break;
                }
                b++;
            }
        }
        if (!(previousCount == this.recentStickers.size() && previousCount2 == this.favouriteStickers.size())) {
            updateStickerTabs();
        }
        StickersGridAdapter stickersGridAdapter2 = this.stickersGridAdapter;
        if (stickersGridAdapter2 != null) {
            stickersGridAdapter2.notifyDataSetChanged();
        }
        checkPanels();
    }

    public void setStickersBanned(boolean value, int chatId) {
        if (this.typeTabs != null) {
            if (value) {
                this.currentChatId = chatId;
            } else {
                this.currentChatId = 0;
            }
            View view = this.typeTabs.getTab(2);
            if (view != null) {
                view.setAlpha(this.currentChatId != 0 ? 0.5f : 1.0f);
                if (this.currentChatId != 0 && this.pager.getCurrentItem() != 0) {
                    showBackspaceButton(true, true);
                    showStickerSettingsButton(false, true);
                    this.pager.setCurrentItem(0, false);
                }
            }
        }
    }

    public void showStickerBanHint(boolean gif) {
        TLRPC.Chat chat;
        if (this.mediaBanTooltip.getVisibility() != 0 && (chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.currentChatId))) != null) {
            if (ChatObject.hasAdminRights(chat) || chat.default_banned_rights == null || !chat.default_banned_rights.send_stickers) {
                if (chat.banned_rights != null) {
                    if (AndroidUtilities.isBannedForever(chat.banned_rights)) {
                        if (gif) {
                            this.mediaBanTooltip.setText(LocaleController.getString("AttachGifRestrictedForever", R.string.AttachGifRestrictedForever));
                        } else {
                            this.mediaBanTooltip.setText(LocaleController.getString("AttachStickersRestrictedForever", R.string.AttachStickersRestrictedForever));
                        }
                    } else if (gif) {
                        this.mediaBanTooltip.setText(LocaleController.formatString("AttachGifRestricted", R.string.AttachGifRestricted, LocaleController.formatDateForBan((long) chat.banned_rights.until_date)));
                    } else {
                        this.mediaBanTooltip.setText(LocaleController.formatString("AttachStickersRestricted", R.string.AttachStickersRestricted, LocaleController.formatDateForBan((long) chat.banned_rights.until_date)));
                    }
                } else {
                    return;
                }
            } else if (gif) {
                this.mediaBanTooltip.setText(LocaleController.getString("GlobalAttachGifRestricted", R.string.GlobalAttachGifRestricted));
            } else {
                this.mediaBanTooltip.setText(LocaleController.getString("GlobalAttachStickersRestricted", R.string.GlobalAttachStickersRestricted));
            }
            this.mediaBanTooltip.setVisibility(0);
            AnimatorSet AnimatorSet = new AnimatorSet();
            AnimatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.mediaBanTooltip, View.ALPHA, new float[]{0.0f, 1.0f})});
            AnimatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public final void run() {
                            EmojiViewV2.AnonymousClass34.this.lambda$onAnimationEnd$0$EmojiViewV2$34();
                        }
                    }, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
                }

                public /* synthetic */ void lambda$onAnimationEnd$0$EmojiViewV2$34() {
                    if (EmojiViewV2.this.mediaBanTooltip != null) {
                        AnimatorSet AnimatorSet1 = new AnimatorSet();
                        AnimatorSet1.playTogether(new Animator[]{ObjectAnimator.ofFloat(EmojiViewV2.this.mediaBanTooltip, View.ALPHA, new float[]{0.0f})});
                        AnimatorSet1.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation1) {
                                if (EmojiViewV2.this.mediaBanTooltip != null) {
                                    EmojiViewV2.this.mediaBanTooltip.setVisibility(4);
                                }
                            }
                        });
                        AnimatorSet1.setDuration(300);
                        AnimatorSet1.start();
                    }
                }
            });
            AnimatorSet.setDuration(300);
            AnimatorSet.start();
        }
    }

    private void updateVisibleTrendingSets() {
        RecyclerListView gridView;
        boolean z;
        TrendingGridAdapter trendingGridAdapter2 = this.trendingGridAdapter;
        if (trendingGridAdapter2 != null && trendingGridAdapter2 != null) {
            for (int b = 0; b < 2; b++) {
                if (b == 0) {
                    try {
                        gridView = this.trendingGridView;
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                        return;
                    }
                } else {
                    gridView = this.stickersGridView;
                }
                int count = gridView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = gridView.getChildAt(a);
                    if (child instanceof FeaturedStickerSetInfoCell) {
                        if (((RecyclerListView.Holder) gridView.getChildViewHolder(child)) != null) {
                            FeaturedStickerSetInfoCell cell = (FeaturedStickerSetInfoCell) child;
                            ArrayList<Long> unreadStickers = MediaDataController.getInstance(this.currentAccount).getUnreadStickerSets();
                            TLRPC.StickerSetCovered stickerSetCovered = cell.getStickerSet();
                            boolean unread = unreadStickers != null && unreadStickers.contains(Long.valueOf(stickerSetCovered.set.id));
                            cell.setStickerSet(stickerSetCovered, unread);
                            if (unread) {
                                MediaDataController.getInstance(this.currentAccount).markFaturedStickersByIdAsRead(stickerSetCovered.set.id);
                            }
                            boolean installing = this.installingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0;
                            boolean removing = this.removingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0;
                            if (installing || removing) {
                                if (installing && cell.isInstalled()) {
                                    this.installingStickerSets.remove(stickerSetCovered.set.id);
                                    installing = false;
                                } else if (removing && !cell.isInstalled()) {
                                    this.removingStickerSets.remove(stickerSetCovered.set.id);
                                    removing = false;
                                }
                            }
                            if (!installing) {
                                if (!removing) {
                                    z = false;
                                    cell.setDrawProgress(z);
                                }
                            }
                            z = true;
                            cell.setDrawProgress(z);
                        }
                    }
                }
            }
        }
    }

    public boolean areThereAnyStickers() {
        StickersGridAdapter stickersGridAdapter2 = this.stickersGridAdapter;
        return stickersGridAdapter2 != null && stickersGridAdapter2.getItemCount() > 0;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.stickersDidLoad) {
            if (args[0].intValue() == 0) {
                TrendingGridAdapter trendingGridAdapter2 = this.trendingGridAdapter;
                if (trendingGridAdapter2 != null) {
                    if (this.trendingLoaded) {
                        updateVisibleTrendingSets();
                    } else {
                        trendingGridAdapter2.notifyDataSetChanged();
                    }
                }
                updateStickerTabs();
                reloadStickersAdapter();
                checkPanels();
            }
        } else if (id == NotificationCenter.recentDocumentsDidLoad) {
            boolean isGif = args[0].booleanValue();
            int type = args[1].intValue();
            if (isGif || type == 0 || type == 2) {
                checkDocuments(isGif);
            }
        } else if (id == NotificationCenter.featuredStickersDidLoad) {
            if (this.trendingGridAdapter != null) {
                if (this.featuredStickersHash != MediaDataController.getInstance(this.currentAccount).getFeaturesStickersHashWithoutUnread()) {
                    this.trendingLoaded = false;
                }
                if (this.trendingLoaded) {
                    updateVisibleTrendingSets();
                } else {
                    this.trendingGridAdapter.notifyDataSetChanged();
                }
            }
            PagerSlidingTabStrip pagerSlidingTabStrip = this.typeTabs;
            if (pagerSlidingTabStrip != null) {
                int count = pagerSlidingTabStrip.getChildCount();
                for (int a = 0; a < count; a++) {
                    this.typeTabs.getChildAt(a).invalidate();
                }
            }
            updateStickerTabs();
        } else if (id == NotificationCenter.groupStickersDidLoad) {
            TLRPC.ChatFull chatFull = this.info;
            if (chatFull != null && chatFull.stickerset != null && this.info.stickerset.id == args[0].longValue()) {
                updateStickerTabs();
            }
        } else if (id == NotificationCenter.emojiDidLoad) {
            RecyclerListView recyclerListView = this.stickersGridView;
            if (recyclerListView != null) {
                int count2 = recyclerListView.getChildCount();
                for (int a2 = 0; a2 < count2; a2++) {
                    View child = this.stickersGridView.getChildAt(a2);
                    if ((child instanceof StickerSetNameCell) || (child instanceof StickerEmojiCell)) {
                        child.invalidate();
                    }
                }
            }
        } else if (id == NotificationCenter.newEmojiSuggestionsAvailable && this.emojiGridView != null && this.needEmojiSearch) {
            if ((this.emojiSearchField.progressDrawable.isAnimating() || this.emojiGridView.getAdapter() == this.emojiSearchAdapter) && !TextUtils.isEmpty(this.emojiSearchAdapter.lastSearchEmojiString)) {
                EmojiSearchAdapter emojiSearchAdapter2 = this.emojiSearchAdapter;
                emojiSearchAdapter2.search(emojiSearchAdapter2.lastSearchEmojiString);
            }
        }
    }

    private class TrendingGridAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public SparseArray<Object> cache = new SparseArray<>();
        private Context context;
        /* access modifiers changed from: private */
        public SparseArray<TLRPC.StickerSetCovered> positionsToSets = new SparseArray<>();
        private ArrayList<TLRPC.StickerSetCovered> sets = new ArrayList<>();
        /* access modifiers changed from: private */
        public int stickersPerRow;
        /* access modifiers changed from: private */
        public int totalItems;

        public TrendingGridAdapter(Context context2) {
            this.context = context2;
        }

        public int getItemCount() {
            return this.totalItems;
        }

        public Object getItem(int i) {
            return this.cache.get(i);
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        public int getItemViewType(int position) {
            Object object = this.cache.get(position);
            if (object == null) {
                return 1;
            }
            if (object instanceof TLRPC.Document) {
                return 0;
            }
            return 2;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            if (viewType == 0) {
                view = new StickerEmojiCell(this.context) {
                    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), 1073741824));
                    }
                };
            } else if (viewType == 1) {
                view = new EmptyCell(this.context);
            } else if (viewType == 2) {
                view = new FeaturedStickerSetInfoCell(this.context, 17);
                ((FeaturedStickerSetInfoCell) view).setAddOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        EmojiViewV2.TrendingGridAdapter.this.lambda$onCreateViewHolder$0$EmojiViewV2$TrendingGridAdapter(view);
                    }
                });
            }
            return new RecyclerListView.Holder(view);
        }

        public /* synthetic */ void lambda$onCreateViewHolder$0$EmojiViewV2$TrendingGridAdapter(View v) {
            FeaturedStickerSetInfoCell parent1 = (FeaturedStickerSetInfoCell) v.getParent();
            TLRPC.StickerSetCovered pack = parent1.getStickerSet();
            if (EmojiViewV2.this.installingStickerSets.indexOfKey(pack.set.id) < 0 && EmojiViewV2.this.removingStickerSets.indexOfKey(pack.set.id) < 0) {
                if (parent1.isInstalled()) {
                    EmojiViewV2.this.removingStickerSets.put(pack.set.id, pack);
                    EmojiViewV2.this.delegate.onStickerSetRemove(parent1.getStickerSet());
                    return;
                }
                EmojiViewV2.this.installingStickerSets.put(pack.set.id, pack);
                EmojiViewV2.this.delegate.onStickerSetAdd(parent1.getStickerSet());
                parent1.setDrawProgress(true);
            }
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int itemViewType = holder.getItemViewType();
            boolean z = false;
            if (itemViewType == 0) {
                ((StickerEmojiCell) holder.itemView).setSticker((TLRPC.Document) this.cache.get(position), this.positionsToSets.get(position), false);
            } else if (itemViewType == 1) {
                ((EmptyCell) holder.itemView).setHeight(AndroidUtilities.dp(82.0f));
            } else if (itemViewType == 2) {
                ArrayList<Long> unreadStickers = MediaDataController.getInstance(EmojiViewV2.this.currentAccount).getUnreadStickerSets();
                TLRPC.StickerSetCovered stickerSetCovered = this.sets.get(((Integer) this.cache.get(position)).intValue());
                boolean unread = unreadStickers != null && unreadStickers.contains(Long.valueOf(stickerSetCovered.set.id));
                FeaturedStickerSetInfoCell cell = (FeaturedStickerSetInfoCell) holder.itemView;
                cell.setStickerSet(stickerSetCovered, unread);
                if (unread) {
                    MediaDataController.getInstance(EmojiViewV2.this.currentAccount).markFaturedStickersByIdAsRead(stickerSetCovered.set.id);
                }
                boolean installing = EmojiViewV2.this.installingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0;
                boolean removing = EmojiViewV2.this.removingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0;
                if (installing || removing) {
                    if (installing && cell.isInstalled()) {
                        EmojiViewV2.this.installingStickerSets.remove(stickerSetCovered.set.id);
                        installing = false;
                    } else if (removing && !cell.isInstalled()) {
                        EmojiViewV2.this.removingStickerSets.remove(stickerSetCovered.set.id);
                        removing = false;
                    }
                }
                if (installing || removing) {
                    z = true;
                }
                cell.setDrawProgress(z);
            }
        }

        public void notifyDataSetChanged() {
            int count;
            int i;
            int width = EmojiViewV2.this.getMeasuredWidth();
            if (width == 0) {
                if (AndroidUtilities.isTablet()) {
                    int smallSide = AndroidUtilities.displaySize.x;
                    int leftSide = (smallSide * 35) / 100;
                    if (leftSide < AndroidUtilities.dp(320.0f)) {
                        leftSide = AndroidUtilities.dp(320.0f);
                    }
                    width = smallSide - leftSide;
                } else {
                    width = AndroidUtilities.displaySize.x;
                }
                if (width == 0) {
                    width = 1080;
                }
            }
            this.stickersPerRow = Math.max(5, width / AndroidUtilities.dp(72.0f));
            EmojiViewV2.this.trendingLayoutManager.setSpanCount(this.stickersPerRow);
            if (!EmojiViewV2.this.trendingLoaded) {
                this.cache.clear();
                this.positionsToSets.clear();
                this.sets.clear();
                this.totalItems = 0;
                int startRow = 0;
                ArrayList<TLRPC.StickerSetCovered> packs = MediaDataController.getInstance(EmojiViewV2.this.currentAccount).getFeaturedStickerSets();
                for (int a = 0; a < packs.size(); a++) {
                    TLRPC.StickerSetCovered pack = packs.get(a);
                    if (!MediaDataController.getInstance(EmojiViewV2.this.currentAccount).isStickerPackInstalled(pack.set.id) && (!pack.covers.isEmpty() || pack.cover != null)) {
                        this.sets.add(pack);
                        this.positionsToSets.put(this.totalItems, pack);
                        SparseArray<Object> sparseArray = this.cache;
                        int i2 = this.totalItems;
                        this.totalItems = i2 + 1;
                        int num = startRow + 1;
                        sparseArray.put(i2, Integer.valueOf(startRow));
                        int i3 = this.totalItems / this.stickersPerRow;
                        if (!pack.covers.isEmpty()) {
                            count = (int) Math.ceil((double) (((float) pack.covers.size()) / ((float) this.stickersPerRow)));
                            for (int b = 0; b < pack.covers.size(); b++) {
                                this.cache.put(this.totalItems + b, pack.covers.get(b));
                            }
                        } else {
                            count = 1;
                            this.cache.put(this.totalItems, pack.cover);
                        }
                        int b2 = 0;
                        while (true) {
                            i = this.stickersPerRow;
                            if (b2 >= count * i) {
                                break;
                            }
                            this.positionsToSets.put(this.totalItems + b2, pack);
                            b2++;
                        }
                        this.totalItems += i * count;
                        startRow = num;
                    }
                }
                if (this.totalItems != 0) {
                    boolean unused = EmojiViewV2.this.trendingLoaded = true;
                    EmojiViewV2 emojiViewV2 = EmojiViewV2.this;
                    int unused2 = emojiViewV2.featuredStickersHash = MediaDataController.getInstance(emojiViewV2.currentAccount).getFeaturesStickersHashWithoutUnread();
                }
                super.notifyDataSetChanged();
            }
        }
    }

    private class StickersGridAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public SparseArray<Object> cache = new SparseArray<>();
        private SparseArray<Object> cacheParents = new SparseArray<>();
        private Context context;
        private HashMap<Object, Integer> packStartPosition = new HashMap<>();
        private SparseIntArray positionToRow = new SparseIntArray();
        private SparseArray<Object> rowStartPack = new SparseArray<>();
        /* access modifiers changed from: private */
        public int stickersPerRow;
        /* access modifiers changed from: private */
        public int totalItems;

        public StickersGridAdapter(Context context2) {
            this.context = context2;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        public int getItemCount() {
            int i = this.totalItems;
            if (i != 0) {
                return i + 1;
            }
            return 0;
        }

        public Object getItem(int i) {
            return this.cache.get(i);
        }

        public int getPositionForPack(Object pack) {
            Integer pos = this.packStartPosition.get(pack);
            if (pos == null) {
                return -1;
            }
            return pos.intValue();
        }

        public int getItemViewType(int position) {
            if (position == 0) {
                return 4;
            }
            Object object = this.cache.get(position);
            if (object == null) {
                return 1;
            }
            if (object instanceof TLRPC.Document) {
                return 0;
            }
            if (object instanceof String) {
                return 3;
            }
            return 2;
        }

        public int getTabForPosition(int position) {
            if (position == 0) {
                position = 1;
            }
            if (this.stickersPerRow == 0) {
                int width = EmojiViewV2.this.getMeasuredWidth();
                if (width == 0) {
                    width = AndroidUtilities.displaySize.x;
                }
                this.stickersPerRow = width / AndroidUtilities.dp(72.0f);
            }
            int row = this.positionToRow.get(position, Integer.MIN_VALUE);
            if (row == Integer.MIN_VALUE) {
                return (EmojiViewV2.this.stickerSets.size() - 1) + EmojiViewV2.this.stickersTabOffset;
            }
            Object pack = this.rowStartPack.get(row);
            if (!(pack instanceof String)) {
                return EmojiViewV2.this.stickersTabOffset + EmojiViewV2.this.stickerSets.indexOf((TLRPC.TL_messages_stickerSet) pack);
            } else if ("recent".equals(pack)) {
                return EmojiViewV2.this.recentTabBum;
            } else {
                return EmojiViewV2.this.favTabBum;
            }
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            if (viewType == 0) {
                view = new StickerEmojiCell(this.context) {
                    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), 1073741824));
                    }
                };
            } else if (viewType == 1) {
                view = new EmptyCell(this.context);
            } else if (viewType == 2) {
                view = new StickerSetNameCell(this.context, false);
                ((StickerSetNameCell) view).setOnIconClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        EmojiViewV2.StickersGridAdapter.this.lambda$onCreateViewHolder$0$EmojiViewV2$StickersGridAdapter(view);
                    }
                });
            } else if (viewType == 3) {
                view = new StickerSetGroupInfoCell(this.context);
                ((StickerSetGroupInfoCell) view).setAddOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        EmojiViewV2.StickersGridAdapter.this.lambda$onCreateViewHolder$1$EmojiViewV2$StickersGridAdapter(view);
                    }
                });
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            } else if (viewType == 4) {
                view = new View(this.context);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiViewV2.this.searchFieldHeight));
            }
            return new RecyclerListView.Holder(view);
        }

        public /* synthetic */ void lambda$onCreateViewHolder$0$EmojiViewV2$StickersGridAdapter(View v) {
            if (EmojiViewV2.this.groupStickerSet == null) {
                SharedPreferences.Editor edit = MessagesController.getEmojiSettings(EmojiViewV2.this.currentAccount).edit();
                edit.putLong("group_hide_stickers_" + EmojiViewV2.this.info.id, EmojiViewV2.this.info.stickerset != null ? EmojiViewV2.this.info.stickerset.id : 0).commit();
                EmojiViewV2.this.updateStickerTabs();
                if (EmojiViewV2.this.stickersGridAdapter != null) {
                    EmojiViewV2.this.stickersGridAdapter.notifyDataSetChanged();
                }
            } else if (EmojiViewV2.this.delegate != null) {
                EmojiViewV2.this.delegate.onStickersGroupClick(EmojiViewV2.this.info.id);
            }
        }

        public /* synthetic */ void lambda$onCreateViewHolder$1$EmojiViewV2$StickersGridAdapter(View v) {
            if (EmojiViewV2.this.delegate != null) {
                EmojiViewV2.this.delegate.onStickersGroupClick(EmojiViewV2.this.info.id);
            }
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ArrayList<TLRPC.Document> documents;
            int icon;
            int itemViewType = holder.getItemViewType();
            boolean z = false;
            int i = 1;
            if (itemViewType == 0) {
                TLRPC.Document sticker = (TLRPC.Document) this.cache.get(position);
                StickerEmojiCell cell = (StickerEmojiCell) holder.itemView;
                cell.setSticker(sticker, this.cacheParents.get(position), false);
                if (EmojiViewV2.this.recentStickers.contains(sticker) || EmojiViewV2.this.favouriteStickers.contains(sticker)) {
                    z = true;
                }
                cell.setRecent(z);
            } else if (itemViewType == 1) {
                EmptyCell cell2 = (EmptyCell) holder.itemView;
                if (position == this.totalItems) {
                    int row = this.positionToRow.get(position - 1, Integer.MIN_VALUE);
                    if (row == Integer.MIN_VALUE) {
                        cell2.setHeight(1);
                        return;
                    }
                    Object pack = this.rowStartPack.get(row);
                    if (pack instanceof TLRPC.TL_messages_stickerSet) {
                        documents = ((TLRPC.TL_messages_stickerSet) pack).documents;
                    } else if (!(pack instanceof String)) {
                        documents = null;
                    } else if ("recent".equals(pack)) {
                        documents = EmojiViewV2.this.recentStickers;
                    } else {
                        documents = EmojiViewV2.this.favouriteStickers;
                    }
                    if (documents == null) {
                        cell2.setHeight(1);
                    } else if (documents.isEmpty()) {
                        cell2.setHeight(AndroidUtilities.dp(8.0f));
                    } else {
                        int height = EmojiViewV2.this.pager.getHeight() - (((int) Math.ceil((double) (((float) documents.size()) / ((float) this.stickersPerRow)))) * AndroidUtilities.dp(82.0f));
                        if (height > 0) {
                            i = height;
                        }
                        cell2.setHeight(i);
                    }
                } else {
                    cell2.setHeight(AndroidUtilities.dp(82.0f));
                }
            } else if (itemViewType == 2) {
                StickerSetNameCell cell3 = (StickerSetNameCell) holder.itemView;
                if (position == EmojiViewV2.this.groupStickerPackPosition) {
                    if (!EmojiViewV2.this.groupStickersHidden || EmojiViewV2.this.groupStickerSet != null) {
                        icon = EmojiViewV2.this.groupStickerSet != null ? R.drawable.stickersclose : R.drawable.stickerset_close;
                    } else {
                        icon = 0;
                    }
                    TLRPC.Chat chat = EmojiViewV2.this.info != null ? MessagesController.getInstance(EmojiViewV2.this.currentAccount).getChat(Integer.valueOf(EmojiViewV2.this.info.id)) : null;
                    Object[] objArr = new Object[1];
                    objArr[0] = chat != null ? chat.title : "Group Stickers";
                    cell3.setText(LocaleController.formatString("CurrentGroupStickers", R.string.CurrentGroupStickers, objArr), icon);
                    return;
                }
                Object object = this.cache.get(position);
                if (object instanceof TLRPC.TL_messages_stickerSet) {
                    TLRPC.TL_messages_stickerSet set = (TLRPC.TL_messages_stickerSet) object;
                    if (set.set != null) {
                        cell3.setText(set.set.title, 0);
                    }
                } else if (object == EmojiViewV2.this.recentStickers) {
                    cell3.setText(LocaleController.getString("RecentStickers", R.string.RecentStickers), 0);
                } else if (object == EmojiViewV2.this.favouriteStickers) {
                    cell3.setText(LocaleController.getString("FavoriteStickers", R.string.FavoriteStickers), 0);
                }
            } else if (itemViewType == 3) {
                StickerSetGroupInfoCell cell4 = (StickerSetGroupInfoCell) holder.itemView;
                if (position == this.totalItems - 1) {
                    z = true;
                }
                cell4.setIsLast(z);
            }
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v3, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v3, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messages_stickerSet} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void notifyDataSetChanged() {
            /*
                r17 = this;
                r0 = r17
                im.bclpbkiauv.ui.components.EmojiViewV2 r1 = im.bclpbkiauv.ui.components.EmojiViewV2.this
                int r1 = r1.getMeasuredWidth()
                if (r1 != 0) goto L_0x000e
                android.graphics.Point r2 = im.bclpbkiauv.messenger.AndroidUtilities.displaySize
                int r1 = r2.x
            L_0x000e:
                r2 = 1116733440(0x42900000, float:72.0)
                int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
                int r2 = r1 / r2
                r0.stickersPerRow = r2
                im.bclpbkiauv.ui.components.EmojiViewV2 r2 = im.bclpbkiauv.ui.components.EmojiViewV2.this
                androidx.recyclerview.widget.GridLayoutManager r2 = r2.stickersLayoutManager
                int r3 = r0.stickersPerRow
                r2.setSpanCount(r3)
                android.util.SparseArray<java.lang.Object> r2 = r0.rowStartPack
                r2.clear()
                java.util.HashMap<java.lang.Object, java.lang.Integer> r2 = r0.packStartPosition
                r2.clear()
                android.util.SparseIntArray r2 = r0.positionToRow
                r2.clear()
                android.util.SparseArray<java.lang.Object> r2 = r0.cache
                r2.clear()
                r2 = 0
                r0.totalItems = r2
                im.bclpbkiauv.ui.components.EmojiViewV2 r2 = im.bclpbkiauv.ui.components.EmojiViewV2.this
                java.util.ArrayList r2 = r2.stickerSets
                r3 = 0
                r4 = -3
            L_0x0042:
                int r5 = r2.size()
                if (r4 >= r5) goto L_0x0196
                r5 = 0
                r6 = -3
                if (r4 != r6) goto L_0x005f
                android.util.SparseArray<java.lang.Object> r6 = r0.cache
                int r7 = r0.totalItems
                int r8 = r7 + 1
                r0.totalItems = r8
                java.lang.String r8 = "search"
                r6.put(r7, r8)
                int r3 = r3 + 1
                r16 = r1
                goto L_0x0190
            L_0x005f:
                r6 = -2
                java.lang.String r7 = "recent"
                java.lang.String r8 = "fav"
                r9 = -1
                if (r4 != r6) goto L_0x007a
                im.bclpbkiauv.ui.components.EmojiViewV2 r6 = im.bclpbkiauv.ui.components.EmojiViewV2.this
                java.util.ArrayList r6 = r6.favouriteStickers
                java.util.HashMap<java.lang.Object, java.lang.Integer> r10 = r0.packStartPosition
                r11 = r8
                int r12 = r0.totalItems
                java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
                r10.put(r8, r12)
                goto L_0x00a4
            L_0x007a:
                if (r4 != r9) goto L_0x008f
                im.bclpbkiauv.ui.components.EmojiViewV2 r6 = im.bclpbkiauv.ui.components.EmojiViewV2.this
                java.util.ArrayList r6 = r6.recentStickers
                java.util.HashMap<java.lang.Object, java.lang.Integer> r10 = r0.packStartPosition
                r11 = r7
                int r12 = r0.totalItems
                java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
                r10.put(r7, r12)
                goto L_0x00a4
            L_0x008f:
                r11 = 0
                java.lang.Object r6 = r2.get(r4)
                r5 = r6
                im.bclpbkiauv.tgnet.TLRPC$TL_messages_stickerSet r5 = (im.bclpbkiauv.tgnet.TLRPC.TL_messages_stickerSet) r5
                java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Document> r6 = r5.documents
                java.util.HashMap<java.lang.Object, java.lang.Integer> r10 = r0.packStartPosition
                int r12 = r0.totalItems
                java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
                r10.put(r5, r12)
            L_0x00a4:
                im.bclpbkiauv.ui.components.EmojiViewV2 r10 = im.bclpbkiauv.ui.components.EmojiViewV2.this
                int r10 = r10.groupStickerPackNum
                if (r4 != r10) goto L_0x00f4
                im.bclpbkiauv.ui.components.EmojiViewV2 r10 = im.bclpbkiauv.ui.components.EmojiViewV2.this
                int r12 = r0.totalItems
                int unused = r10.groupStickerPackPosition = r12
                boolean r10 = r6.isEmpty()
                if (r10 == 0) goto L_0x00f4
                android.util.SparseArray<java.lang.Object> r7 = r0.rowStartPack
                r7.put(r3, r5)
                android.util.SparseIntArray r7 = r0.positionToRow
                int r8 = r0.totalItems
                int r9 = r3 + 1
                r7.put(r8, r3)
                android.util.SparseArray<java.lang.Object> r3 = r0.rowStartPack
                r3.put(r9, r5)
                android.util.SparseIntArray r3 = r0.positionToRow
                int r7 = r0.totalItems
                int r7 = r7 + 1
                int r8 = r9 + 1
                r3.put(r7, r9)
                android.util.SparseArray<java.lang.Object> r3 = r0.cache
                int r7 = r0.totalItems
                int r9 = r7 + 1
                r0.totalItems = r9
                r3.put(r7, r5)
                android.util.SparseArray<java.lang.Object> r3 = r0.cache
                int r7 = r0.totalItems
                int r9 = r7 + 1
                r0.totalItems = r9
                java.lang.String r9 = "group"
                r3.put(r7, r9)
                r16 = r1
                r3 = r8
                goto L_0x0190
            L_0x00f4:
                boolean r10 = r6.isEmpty()
                if (r10 == 0) goto L_0x00fe
                r16 = r1
                goto L_0x0190
            L_0x00fe:
                int r10 = r6.size()
                float r10 = (float) r10
                int r12 = r0.stickersPerRow
                float r12 = (float) r12
                float r10 = r10 / r12
                double r12 = (double) r10
                double r12 = java.lang.Math.ceil(r12)
                int r10 = (int) r12
                if (r5 == 0) goto L_0x0117
                android.util.SparseArray<java.lang.Object> r12 = r0.cache
                int r13 = r0.totalItems
                r12.put(r13, r5)
                goto L_0x011e
            L_0x0117:
                android.util.SparseArray<java.lang.Object> r12 = r0.cache
                int r13 = r0.totalItems
                r12.put(r13, r6)
            L_0x011e:
                android.util.SparseIntArray r12 = r0.positionToRow
                int r13 = r0.totalItems
                r12.put(r13, r3)
                r12 = 0
            L_0x0126:
                int r13 = r6.size()
                if (r12 >= r13) goto L_0x0160
                int r13 = r12 + 1
                int r14 = r0.totalItems
                int r13 = r13 + r14
                android.util.SparseArray<java.lang.Object> r14 = r0.cache
                java.lang.Object r15 = r6.get(r12)
                r14.put(r13, r15)
                if (r5 == 0) goto L_0x0142
                android.util.SparseArray<java.lang.Object> r14 = r0.cacheParents
                r14.put(r13, r5)
                goto L_0x0147
            L_0x0142:
                android.util.SparseArray<java.lang.Object> r14 = r0.cacheParents
                r14.put(r13, r11)
            L_0x0147:
                android.util.SparseIntArray r14 = r0.positionToRow
                int r15 = r12 + 1
                int r9 = r0.totalItems
                int r15 = r15 + r9
                int r9 = r3 + 1
                r16 = r1
                int r1 = r0.stickersPerRow
                int r1 = r12 / r1
                int r9 = r9 + r1
                r14.put(r15, r9)
                int r12 = r12 + 1
                r1 = r16
                r9 = -1
                goto L_0x0126
            L_0x0160:
                r16 = r1
                r1 = 0
            L_0x0163:
                int r9 = r10 + 1
                if (r1 >= r9) goto L_0x0182
                if (r5 == 0) goto L_0x0172
                android.util.SparseArray<java.lang.Object> r9 = r0.rowStartPack
                int r12 = r3 + r1
                r9.put(r12, r5)
                r13 = -1
                goto L_0x017f
            L_0x0172:
                android.util.SparseArray<java.lang.Object> r9 = r0.rowStartPack
                int r12 = r3 + r1
                r13 = -1
                if (r4 != r13) goto L_0x017b
                r14 = r7
                goto L_0x017c
            L_0x017b:
                r14 = r8
            L_0x017c:
                r9.put(r12, r14)
            L_0x017f:
                int r1 = r1 + 1
                goto L_0x0163
            L_0x0182:
                int r1 = r0.totalItems
                int r7 = r0.stickersPerRow
                int r7 = r7 * r10
                int r7 = r7 + 1
                int r1 = r1 + r7
                r0.totalItems = r1
                int r1 = r10 + 1
                int r3 = r3 + r1
            L_0x0190:
                int r4 = r4 + 1
                r1 = r16
                goto L_0x0042
            L_0x0196:
                super.notifyDataSetChanged()
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.EmojiViewV2.StickersGridAdapter.notifyDataSetChanged():void");
        }
    }

    private class EmojiGridAdapter extends RecyclerListView.SelectionAdapter {
        private int itemCount;
        /* access modifiers changed from: private */
        public SparseIntArray positionToSection;
        /* access modifiers changed from: private */
        public SparseIntArray sectionToPosition;

        private EmojiGridAdapter() {
            this.positionToSection = new SparseIntArray();
            this.sectionToPosition = new SparseIntArray();
        }

        public int getItemCount() {
            return this.itemCount;
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 0) {
                EmojiViewV2 emojiViewV2 = EmojiViewV2.this;
                view = new ImageViewEmoji(emojiViewV2.getContext());
            } else if (viewType != 1) {
                view = new View(EmojiViewV2.this.getContext());
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiViewV2.this.searchFieldHeight));
            } else {
                view = new StickerSetNameCell(EmojiViewV2.this.getContext(), true);
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean recent;
            String code;
            String coloredCode;
            int itemViewType = holder.getItemViewType();
            if (itemViewType == 0) {
                ImageViewEmoji imageView = (ImageViewEmoji) holder.itemView;
                if (EmojiViewV2.this.needEmojiSearch) {
                    position--;
                }
                int count = Emoji.recentEmoji.size();
                if (position < count) {
                    coloredCode = Emoji.recentEmoji.get(position);
                    code = coloredCode;
                    recent = true;
                } else {
                    code = null;
                    int a = 0;
                    while (true) {
                        if (a >= EmojiData.dataColored.length) {
                            coloredCode = null;
                            break;
                        }
                        int size = EmojiData.dataColored[a].length + 1;
                        if (position < count + size) {
                            coloredCode = EmojiData.dataColored[a][(position - count) - 1];
                            code = coloredCode;
                            String color = Emoji.emojiColor.get(code);
                            if (color != null) {
                                coloredCode = EmojiViewV2.addColorToCode(coloredCode, color);
                            }
                        } else {
                            count += size;
                            a++;
                        }
                    }
                    recent = false;
                }
                imageView.setImageDrawable(Emoji.getEmojiBigDrawable(coloredCode), recent);
                imageView.setTag(code);
                imageView.setContentDescription(coloredCode);
            } else if (itemViewType == 1) {
                ((StickerSetNameCell) holder.itemView).setText(EmojiViewV2.this.emojiTitles[this.positionToSection.get(position)], 0);
            }
        }

        public int getItemViewType(int position) {
            if (EmojiViewV2.this.needEmojiSearch && position == 0) {
                return 2;
            }
            if (this.positionToSection.indexOfKey(position) >= 0) {
                return 1;
            }
            return 0;
        }

        public void notifyDataSetChanged() {
            this.positionToSection.clear();
            this.itemCount = Emoji.recentEmoji.size() + (EmojiViewV2.this.needEmojiSearch ? 1 : 0);
            for (int a = 0; a < EmojiData.dataColored.length; a++) {
                this.positionToSection.put(this.itemCount, a);
                this.sectionToPosition.put(a, this.itemCount);
                this.itemCount += EmojiData.dataColored[a].length + 1;
            }
            EmojiViewV2.this.updateEmojiTabs();
            super.notifyDataSetChanged();
        }
    }

    private class EmojiSearchAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public String lastSearchAlias;
        /* access modifiers changed from: private */
        public String lastSearchEmojiString;
        /* access modifiers changed from: private */
        public ArrayList<MediaDataController.KeywordResult> result;
        private Runnable searchRunnable;
        /* access modifiers changed from: private */
        public boolean searchWas;

        private EmojiSearchAdapter() {
            this.result = new ArrayList<>();
        }

        public int getItemCount() {
            if (this.result.isEmpty() && !this.searchWas) {
                return Emoji.recentEmoji.size() + 1;
            }
            if (!this.result.isEmpty()) {
                return this.result.size() + 1;
            }
            return 2;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 0) {
                EmojiViewV2 emojiViewV2 = EmojiViewV2.this;
                view = new ImageViewEmoji(emojiViewV2.getContext());
            } else if (viewType != 1) {
                FrameLayout frameLayout = new FrameLayout(EmojiViewV2.this.getContext()) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        int parentHeight;
                        View parent = (View) EmojiViewV2.this.getParent();
                        if (parent != null) {
                            parentHeight = (int) (((float) parent.getMeasuredHeight()) - EmojiViewV2.this.getY());
                        } else {
                            parentHeight = AndroidUtilities.dp(120.0f);
                        }
                        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(parentHeight - EmojiViewV2.this.searchFieldHeight, 1073741824));
                    }
                };
                TextView textView = new TextView(EmojiViewV2.this.getContext());
                textView.setText(LocaleController.getString("NoEmojiFound", R.string.NoEmojiFound));
                textView.setTextSize(1, 16.0f);
                textView.setTextColor(Theme.getColor(Theme.key_chat_emojiPanelEmptyText));
                frameLayout.addView(textView, LayoutHelper.createFrame(-2.0f, -2.0f, 49, 0.0f, 10.0f, 0.0f, 0.0f));
                ImageView imageView = new ImageView(EmojiViewV2.this.getContext());
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                imageView.setImageResource(R.drawable.smiles_panel_question);
                imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_emojiPanelEmptyText), PorterDuff.Mode.MULTIPLY));
                frameLayout.addView(imageView, LayoutHelper.createFrame(48, 48, 85));
                imageView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        final boolean[] loadingUrl = new boolean[1];
                        final BottomSheet.Builder builder = new BottomSheet.Builder(EmojiViewV2.this.getContext());
                        LinearLayout linearLayout = new LinearLayout(EmojiViewV2.this.getContext());
                        linearLayout.setOrientation(1);
                        linearLayout.setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), 0);
                        ImageView imageView1 = new ImageView(EmojiViewV2.this.getContext());
                        imageView1.setImageResource(R.drawable.smiles_info);
                        linearLayout.addView(imageView1, LayoutHelper.createLinear(-2, -2, 49, 0, 15, 0, 0));
                        TextView textView = new TextView(EmojiViewV2.this.getContext());
                        textView.setText(LocaleController.getString("EmojiSuggestions", R.string.EmojiSuggestions));
                        textView.setTextSize(1, 15.0f);
                        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
                        int i = 5;
                        textView.setGravity(LocaleController.isRTL ? 5 : 3);
                        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                        linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, 51, 0, 24, 0, 0));
                        TextView textView2 = new TextView(EmojiViewV2.this.getContext());
                        textView2.setText(AndroidUtilities.replaceTags(LocaleController.getString("EmojiSuggestionsInfo", R.string.EmojiSuggestionsInfo)));
                        textView2.setTextSize(1, 15.0f);
                        textView2.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                        textView2.setGravity(LocaleController.isRTL ? 5 : 3);
                        linearLayout.addView(textView2, LayoutHelper.createLinear(-2, -2, 51, 0, 11, 0, 0));
                        TextView textView3 = new TextView(EmojiViewV2.this.getContext());
                        Object[] objArr = new Object[1];
                        objArr[0] = EmojiSearchAdapter.this.lastSearchAlias != null ? EmojiSearchAdapter.this.lastSearchAlias : EmojiViewV2.this.lastSearchKeyboardLanguage;
                        textView3.setText(LocaleController.formatString("EmojiSuggestionsUrl", R.string.EmojiSuggestionsUrl, objArr));
                        textView3.setTextSize(1, 15.0f);
                        textView3.setTextColor(Theme.getColor(Theme.key_dialogTextLink));
                        if (!LocaleController.isRTL) {
                            i = 3;
                        }
                        textView3.setGravity(i);
                        linearLayout.addView(textView3, LayoutHelper.createLinear(-2, -2, 51, 0, 18, 0, 16));
                        textView3.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                boolean[] zArr = loadingUrl;
                                if (!zArr[0]) {
                                    zArr[0] = true;
                                    AlertDialog[] progressDialog = {new AlertDialog(EmojiViewV2.this.getContext(), 3)};
                                    TLRPC.TL_messages_getEmojiURL req = new TLRPC.TL_messages_getEmojiURL();
                                    req.lang_code = EmojiSearchAdapter.this.lastSearchAlias != null ? EmojiSearchAdapter.this.lastSearchAlias : EmojiViewV2.this.lastSearchKeyboardLanguage[0];
                                    AndroidUtilities.runOnUIThread(
                                    /*  JADX ERROR: Method code generation error
                                        jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0065: INVOKE  
                                          (wrap: im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$EmojiSearchAdapter$2$1$XIMiP86JX98SRLPPkP_x_VrodxM : 0x0060: CONSTRUCTOR  (r3v8 im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$EmojiSearchAdapter$2$1$XIMiP86JX98SRLPPkP_x_VrodxM) = 
                                          (r6v0 'this' im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter$2$1 A[THIS])
                                          (r0v1 'progressDialog' im.bclpbkiauv.ui.actionbar.AlertDialog[])
                                          (wrap: int : 0x005a: INVOKE  (r1v7 'requestId' int) = 
                                          (wrap: im.bclpbkiauv.tgnet.ConnectionsManager : 0x004f: INVOKE  (r1v6 im.bclpbkiauv.tgnet.ConnectionsManager) = 
                                          (wrap: int : 0x004b: INVOKE  (r1v5 int) = 
                                          (wrap: im.bclpbkiauv.ui.components.EmojiViewV2 : 0x0049: IGET  (r1v4 im.bclpbkiauv.ui.components.EmojiViewV2) = 
                                          (wrap: im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter : 0x0047: IGET  (r1v3 im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter) = 
                                          (wrap: im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter$2 : 0x0045: IGET  (r1v2 im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter$2) = 
                                          (r6v0 'this' im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter$2$1 A[THIS])
                                         im.bclpbkiauv.ui.components.EmojiViewV2.EmojiSearchAdapter.2.1.this$2 im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter$2)
                                         im.bclpbkiauv.ui.components.EmojiViewV2.EmojiSearchAdapter.2.this$1 im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter)
                                         im.bclpbkiauv.ui.components.EmojiViewV2.EmojiSearchAdapter.this$0 im.bclpbkiauv.ui.components.EmojiViewV2)
                                         im.bclpbkiauv.ui.components.EmojiViewV2.access$600(im.bclpbkiauv.ui.components.EmojiViewV2):int type: STATIC)
                                         im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(int):im.bclpbkiauv.tgnet.ConnectionsManager type: STATIC)
                                          (r2v3 'req' im.bclpbkiauv.tgnet.TLRPC$TL_messages_getEmojiURL)
                                          (wrap: im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$EmojiSearchAdapter$2$1$fh2TNutdukCyrcYE-CtKgYiIg7I : 0x0057: CONSTRUCTOR  (r4v1 im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$EmojiSearchAdapter$2$1$fh2TNutdukCyrcYE-CtKgYiIg7I) = 
                                          (r6v0 'this' im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter$2$1 A[THIS])
                                          (r0v1 'progressDialog' im.bclpbkiauv.ui.actionbar.AlertDialog[])
                                          (wrap: im.bclpbkiauv.ui.actionbar.BottomSheet$Builder : 0x0053: IGET  (r3v7 im.bclpbkiauv.ui.actionbar.BottomSheet$Builder) = 
                                          (r6v0 'this' im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter$2$1 A[THIS])
                                         im.bclpbkiauv.ui.components.EmojiViewV2.EmojiSearchAdapter.2.1.val$builder im.bclpbkiauv.ui.actionbar.BottomSheet$Builder)
                                         call: im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$EmojiSearchAdapter$2$1$fh2TNutdukCyrcYE-CtKgYiIg7I.<init>(im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter$2$1, im.bclpbkiauv.ui.actionbar.AlertDialog[], im.bclpbkiauv.ui.actionbar.BottomSheet$Builder):void type: CONSTRUCTOR)
                                         im.bclpbkiauv.tgnet.ConnectionsManager.sendRequest(im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.RequestDelegate):int type: VIRTUAL)
                                         call: im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$EmojiSearchAdapter$2$1$XIMiP86JX98SRLPPkP_x_VrodxM.<init>(im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter$2$1, im.bclpbkiauv.ui.actionbar.AlertDialog[], int):void type: CONSTRUCTOR)
                                          (1000 long)
                                         im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable, long):void type: STATIC in method: im.bclpbkiauv.ui.components.EmojiViewV2.EmojiSearchAdapter.2.1.onClick(android.view.View):void, dex: classes6.dex
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                        	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                        	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                        	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                        	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                        	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                        	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                        	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                        	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                                        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                        	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                        	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                        	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                        	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                        	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                        	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                        	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                        	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                        	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                                        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                        	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                        	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                        	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                        	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                        	at jadx.core.codegen.RegionGen.connectElseIf(RegionGen.java:175)
                                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:152)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                        	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                        	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                        	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                        	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                        	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                        	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                        	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                        	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                                        	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                                        	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                                        	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                                        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                                        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                                        Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0060: CONSTRUCTOR  (r3v8 im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$EmojiSearchAdapter$2$1$XIMiP86JX98SRLPPkP_x_VrodxM) = 
                                          (r6v0 'this' im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter$2$1 A[THIS])
                                          (r0v1 'progressDialog' im.bclpbkiauv.ui.actionbar.AlertDialog[])
                                          (wrap: int : 0x005a: INVOKE  (r1v7 'requestId' int) = 
                                          (wrap: im.bclpbkiauv.tgnet.ConnectionsManager : 0x004f: INVOKE  (r1v6 im.bclpbkiauv.tgnet.ConnectionsManager) = 
                                          (wrap: int : 0x004b: INVOKE  (r1v5 int) = 
                                          (wrap: im.bclpbkiauv.ui.components.EmojiViewV2 : 0x0049: IGET  (r1v4 im.bclpbkiauv.ui.components.EmojiViewV2) = 
                                          (wrap: im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter : 0x0047: IGET  (r1v3 im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter) = 
                                          (wrap: im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter$2 : 0x0045: IGET  (r1v2 im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter$2) = 
                                          (r6v0 'this' im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter$2$1 A[THIS])
                                         im.bclpbkiauv.ui.components.EmojiViewV2.EmojiSearchAdapter.2.1.this$2 im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter$2)
                                         im.bclpbkiauv.ui.components.EmojiViewV2.EmojiSearchAdapter.2.this$1 im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter)
                                         im.bclpbkiauv.ui.components.EmojiViewV2.EmojiSearchAdapter.this$0 im.bclpbkiauv.ui.components.EmojiViewV2)
                                         im.bclpbkiauv.ui.components.EmojiViewV2.access$600(im.bclpbkiauv.ui.components.EmojiViewV2):int type: STATIC)
                                         im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(int):im.bclpbkiauv.tgnet.ConnectionsManager type: STATIC)
                                          (r2v3 'req' im.bclpbkiauv.tgnet.TLRPC$TL_messages_getEmojiURL)
                                          (wrap: im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$EmojiSearchAdapter$2$1$fh2TNutdukCyrcYE-CtKgYiIg7I : 0x0057: CONSTRUCTOR  (r4v1 im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$EmojiSearchAdapter$2$1$fh2TNutdukCyrcYE-CtKgYiIg7I) = 
                                          (r6v0 'this' im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter$2$1 A[THIS])
                                          (r0v1 'progressDialog' im.bclpbkiauv.ui.actionbar.AlertDialog[])
                                          (wrap: im.bclpbkiauv.ui.actionbar.BottomSheet$Builder : 0x0053: IGET  (r3v7 im.bclpbkiauv.ui.actionbar.BottomSheet$Builder) = 
                                          (r6v0 'this' im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter$2$1 A[THIS])
                                         im.bclpbkiauv.ui.components.EmojiViewV2.EmojiSearchAdapter.2.1.val$builder im.bclpbkiauv.ui.actionbar.BottomSheet$Builder)
                                         call: im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$EmojiSearchAdapter$2$1$fh2TNutdukCyrcYE-CtKgYiIg7I.<init>(im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter$2$1, im.bclpbkiauv.ui.actionbar.AlertDialog[], im.bclpbkiauv.ui.actionbar.BottomSheet$Builder):void type: CONSTRUCTOR)
                                         im.bclpbkiauv.tgnet.ConnectionsManager.sendRequest(im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.RequestDelegate):int type: VIRTUAL)
                                         call: im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$EmojiSearchAdapter$2$1$XIMiP86JX98SRLPPkP_x_VrodxM.<init>(im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter$2$1, im.bclpbkiauv.ui.actionbar.AlertDialog[], int):void type: CONSTRUCTOR in method: im.bclpbkiauv.ui.components.EmojiViewV2.EmojiSearchAdapter.2.1.onClick(android.view.View):void, dex: classes6.dex
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                        	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                        	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                        	... 122 more
                                        Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$EmojiSearchAdapter$2$1$XIMiP86JX98SRLPPkP_x_VrodxM, state: NOT_LOADED
                                        	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                                        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                        	... 128 more
                                        */
                                    /*
                                        this = this;
                                        boolean[] r0 = r2
                                        r1 = 0
                                        boolean r2 = r0[r1]
                                        if (r2 == 0) goto L_0x0008
                                        return
                                    L_0x0008:
                                        r2 = 1
                                        r0[r1] = r2
                                        im.bclpbkiauv.ui.actionbar.AlertDialog[] r0 = new im.bclpbkiauv.ui.actionbar.AlertDialog[r2]
                                        im.bclpbkiauv.ui.actionbar.AlertDialog r2 = new im.bclpbkiauv.ui.actionbar.AlertDialog
                                        im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter$2 r3 = im.bclpbkiauv.ui.components.EmojiViewV2.EmojiSearchAdapter.AnonymousClass2.this
                                        im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter r3 = im.bclpbkiauv.ui.components.EmojiViewV2.EmojiSearchAdapter.this
                                        im.bclpbkiauv.ui.components.EmojiViewV2 r3 = im.bclpbkiauv.ui.components.EmojiViewV2.this
                                        android.content.Context r3 = r3.getContext()
                                        r4 = 3
                                        r2.<init>(r3, r4)
                                        r0[r1] = r2
                                        im.bclpbkiauv.tgnet.TLRPC$TL_messages_getEmojiURL r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_messages_getEmojiURL
                                        r2.<init>()
                                        im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter$2 r3 = im.bclpbkiauv.ui.components.EmojiViewV2.EmojiSearchAdapter.AnonymousClass2.this
                                        im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter r3 = im.bclpbkiauv.ui.components.EmojiViewV2.EmojiSearchAdapter.this
                                        java.lang.String r3 = r3.lastSearchAlias
                                        if (r3 == 0) goto L_0x0037
                                        im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter$2 r1 = im.bclpbkiauv.ui.components.EmojiViewV2.EmojiSearchAdapter.AnonymousClass2.this
                                        im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter r1 = im.bclpbkiauv.ui.components.EmojiViewV2.EmojiSearchAdapter.this
                                        java.lang.String r1 = r1.lastSearchAlias
                                        goto L_0x0043
                                    L_0x0037:
                                        im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter$2 r3 = im.bclpbkiauv.ui.components.EmojiViewV2.EmojiSearchAdapter.AnonymousClass2.this
                                        im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter r3 = im.bclpbkiauv.ui.components.EmojiViewV2.EmojiSearchAdapter.this
                                        im.bclpbkiauv.ui.components.EmojiViewV2 r3 = im.bclpbkiauv.ui.components.EmojiViewV2.this
                                        java.lang.String[] r3 = r3.lastSearchKeyboardLanguage
                                        r1 = r3[r1]
                                    L_0x0043:
                                        r2.lang_code = r1
                                        im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter$2 r1 = im.bclpbkiauv.ui.components.EmojiViewV2.EmojiSearchAdapter.AnonymousClass2.this
                                        im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter r1 = im.bclpbkiauv.ui.components.EmojiViewV2.EmojiSearchAdapter.this
                                        im.bclpbkiauv.ui.components.EmojiViewV2 r1 = im.bclpbkiauv.ui.components.EmojiViewV2.this
                                        int r1 = r1.currentAccount
                                        im.bclpbkiauv.tgnet.ConnectionsManager r1 = im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(r1)
                                        im.bclpbkiauv.ui.actionbar.BottomSheet$Builder r3 = r3
                                        im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$EmojiSearchAdapter$2$1$fh2TNutdukCyrcYE-CtKgYiIg7I r4 = new im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$EmojiSearchAdapter$2$1$fh2TNutdukCyrcYE-CtKgYiIg7I
                                        r4.<init>(r6, r0, r3)
                                        int r1 = r1.sendRequest(r2, r4)
                                        im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$EmojiSearchAdapter$2$1$XIMiP86JX98SRLPPkP_x_VrodxM r3 = new im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$EmojiSearchAdapter$2$1$XIMiP86JX98SRLPPkP_x_VrodxM
                                        r3.<init>(r6, r0, r1)
                                        r4 = 1000(0x3e8, double:4.94E-321)
                                        im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r3, r4)
                                        return
                                    */
                                    throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.EmojiViewV2.EmojiSearchAdapter.AnonymousClass2.AnonymousClass1.onClick(android.view.View):void");
                                }

                                public /* synthetic */ void lambda$onClick$1$EmojiViewV2$EmojiSearchAdapter$2$1(AlertDialog[] progressDialog, BottomSheet.Builder builder, TLObject response, TLRPC.TL_error error) {
                                    AndroidUtilities.runOnUIThread(
                                    /*  JADX ERROR: Method code generation error
                                        jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0005: INVOKE  
                                          (wrap: im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$EmojiSearchAdapter$2$1$XazRibsheLKb8BfzM36MTc4Pi4Y : 0x0002: CONSTRUCTOR  (r0v0 im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$EmojiSearchAdapter$2$1$XazRibsheLKb8BfzM36MTc4Pi4Y) = 
                                          (r1v0 'this' im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter$2$1 A[THIS])
                                          (r2v0 'progressDialog' im.bclpbkiauv.ui.actionbar.AlertDialog[])
                                          (r4v0 'response' im.bclpbkiauv.tgnet.TLObject)
                                          (r3v0 'builder' im.bclpbkiauv.ui.actionbar.BottomSheet$Builder)
                                         call: im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$EmojiSearchAdapter$2$1$XazRibsheLKb8BfzM36MTc4Pi4Y.<init>(im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter$2$1, im.bclpbkiauv.ui.actionbar.AlertDialog[], im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.ui.actionbar.BottomSheet$Builder):void type: CONSTRUCTOR)
                                         im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable):void type: STATIC in method: im.bclpbkiauv.ui.components.EmojiViewV2.EmojiSearchAdapter.2.1.lambda$onClick$1$EmojiViewV2$EmojiSearchAdapter$2$1(im.bclpbkiauv.ui.actionbar.AlertDialog[], im.bclpbkiauv.ui.actionbar.BottomSheet$Builder, im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.TLRPC$TL_error):void, dex: classes6.dex
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                        	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                        	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                        	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                        	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                        	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                        	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                        	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                                        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                        	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                        	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                        	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                        	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                        	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                        	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                        	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                        	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                        	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                                        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                        	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                        	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                        	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                        	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                        	at jadx.core.codegen.RegionGen.connectElseIf(RegionGen.java:175)
                                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:152)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                        	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                        	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                        	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                        	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                        	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                        	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                        	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                        	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                                        	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                                        	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                                        	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                                        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                                        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                                        Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0002: CONSTRUCTOR  (r0v0 im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$EmojiSearchAdapter$2$1$XazRibsheLKb8BfzM36MTc4Pi4Y) = 
                                          (r1v0 'this' im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter$2$1 A[THIS])
                                          (r2v0 'progressDialog' im.bclpbkiauv.ui.actionbar.AlertDialog[])
                                          (r4v0 'response' im.bclpbkiauv.tgnet.TLObject)
                                          (r3v0 'builder' im.bclpbkiauv.ui.actionbar.BottomSheet$Builder)
                                         call: im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$EmojiSearchAdapter$2$1$XazRibsheLKb8BfzM36MTc4Pi4Y.<init>(im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter$2$1, im.bclpbkiauv.ui.actionbar.AlertDialog[], im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.ui.actionbar.BottomSheet$Builder):void type: CONSTRUCTOR in method: im.bclpbkiauv.ui.components.EmojiViewV2.EmojiSearchAdapter.2.1.lambda$onClick$1$EmojiViewV2$EmojiSearchAdapter$2$1(im.bclpbkiauv.ui.actionbar.AlertDialog[], im.bclpbkiauv.ui.actionbar.BottomSheet$Builder, im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.TLRPC$TL_error):void, dex: classes6.dex
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                        	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                        	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                        	... 115 more
                                        Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$EmojiSearchAdapter$2$1$XazRibsheLKb8BfzM36MTc4Pi4Y, state: NOT_LOADED
                                        	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                                        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                        	... 121 more
                                        */
                                    /*
                                        this = this;
                                        im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$EmojiSearchAdapter$2$1$XazRibsheLKb8BfzM36MTc4Pi4Y r0 = new im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$EmojiSearchAdapter$2$1$XazRibsheLKb8BfzM36MTc4Pi4Y
                                        r0.<init>(r1, r2, r4, r3)
                                        im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r0)
                                        return
                                    */
                                    throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.EmojiViewV2.EmojiSearchAdapter.AnonymousClass2.AnonymousClass1.lambda$onClick$1$EmojiViewV2$EmojiSearchAdapter$2$1(im.bclpbkiauv.ui.actionbar.AlertDialog[], im.bclpbkiauv.ui.actionbar.BottomSheet$Builder, im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.TLRPC$TL_error):void");
                                }

                                public /* synthetic */ void lambda$null$0$EmojiViewV2$EmojiSearchAdapter$2$1(AlertDialog[] progressDialog, TLObject response, BottomSheet.Builder builder) {
                                    try {
                                        progressDialog[0].dismiss();
                                    } catch (Throwable th) {
                                    }
                                    progressDialog[0] = null;
                                    if (response instanceof TLRPC.TL_emojiURL) {
                                        Browser.openUrl(EmojiViewV2.this.getContext(), ((TLRPC.TL_emojiURL) response).url);
                                        builder.getDismissRunnable().run();
                                    }
                                }

                                public /* synthetic */ void lambda$onClick$3$EmojiViewV2$EmojiSearchAdapter$2$1(AlertDialog[] progressDialog, int requestId) {
                                    if (progressDialog[0] != null) {
                                        progressDialog[0].setOnCancelListener(
                                        /*  JADX ERROR: Method code generation error
                                            jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x000d: INVOKE  
                                              (wrap: im.bclpbkiauv.ui.actionbar.AlertDialog : 0x0006: AGET  (r1v1 im.bclpbkiauv.ui.actionbar.AlertDialog) = 
                                              (r4v0 'progressDialog' im.bclpbkiauv.ui.actionbar.AlertDialog[])
                                              (0 ?[int, short, byte, char])
                                            )
                                              (wrap: im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$EmojiSearchAdapter$2$1$eJ5J-HD602_mN-lBRX3Jh9CinI0 : 0x000a: CONSTRUCTOR  (r2v0 im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$EmojiSearchAdapter$2$1$eJ5J-HD602_mN-lBRX3Jh9CinI0) = 
                                              (r3v0 'this' im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter$2$1 A[THIS])
                                              (r5v0 'requestId' int)
                                             call: im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$EmojiSearchAdapter$2$1$eJ5J-HD602_mN-lBRX3Jh9CinI0.<init>(im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter$2$1, int):void type: CONSTRUCTOR)
                                             im.bclpbkiauv.ui.actionbar.AlertDialog.setOnCancelListener(android.content.DialogInterface$OnCancelListener):void type: VIRTUAL in method: im.bclpbkiauv.ui.components.EmojiViewV2.EmojiSearchAdapter.2.1.lambda$onClick$3$EmojiViewV2$EmojiSearchAdapter$2$1(im.bclpbkiauv.ui.actionbar.AlertDialog[], int):void, dex: classes6.dex
                                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                            	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                            	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                            	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                            	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                            	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                            	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                            	at jadx.core.codegen.RegionGen.connectElseIf(RegionGen.java:175)
                                            	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:152)
                                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                            	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                            	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                            	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                                            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                                            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                                            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                                            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                                            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                                            Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x000a: CONSTRUCTOR  (r2v0 im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$EmojiSearchAdapter$2$1$eJ5J-HD602_mN-lBRX3Jh9CinI0) = 
                                              (r3v0 'this' im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter$2$1 A[THIS])
                                              (r5v0 'requestId' int)
                                             call: im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$EmojiSearchAdapter$2$1$eJ5J-HD602_mN-lBRX3Jh9CinI0.<init>(im.bclpbkiauv.ui.components.EmojiViewV2$EmojiSearchAdapter$2$1, int):void type: CONSTRUCTOR in method: im.bclpbkiauv.ui.components.EmojiViewV2.EmojiSearchAdapter.2.1.lambda$onClick$3$EmojiViewV2$EmojiSearchAdapter$2$1(im.bclpbkiauv.ui.actionbar.AlertDialog[], int):void, dex: classes6.dex
                                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                            	... 122 more
                                            Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$EmojiSearchAdapter$2$1$eJ5J-HD602_mN-lBRX3Jh9CinI0, state: NOT_LOADED
                                            	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                            	... 128 more
                                            */
                                        /*
                                            this = this;
                                            r0 = 0
                                            r1 = r4[r0]
                                            if (r1 != 0) goto L_0x0006
                                            return
                                        L_0x0006:
                                            r1 = r4[r0]
                                            im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$EmojiSearchAdapter$2$1$eJ5J-HD602_mN-lBRX3Jh9CinI0 r2 = new im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$EmojiSearchAdapter$2$1$eJ5J-HD602_mN-lBRX3Jh9CinI0
                                            r2.<init>(r3, r5)
                                            r1.setOnCancelListener(r2)
                                            r0 = r4[r0]
                                            r0.show()
                                            return
                                        */
                                        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.EmojiViewV2.EmojiSearchAdapter.AnonymousClass2.AnonymousClass1.lambda$onClick$3$EmojiViewV2$EmojiSearchAdapter$2$1(im.bclpbkiauv.ui.actionbar.AlertDialog[], int):void");
                                    }

                                    public /* synthetic */ void lambda$null$2$EmojiViewV2$EmojiSearchAdapter$2$1(int requestId, DialogInterface dialog) {
                                        ConnectionsManager.getInstance(EmojiViewV2.this.currentAccount).cancelRequest(requestId, true);
                                    }
                                });
                                builder.setCustomView(linearLayout);
                                builder.show();
                            }
                        });
                        view = frameLayout;
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    } else {
                        view = new View(EmojiViewV2.this.getContext());
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiViewV2.this.searchFieldHeight));
                    }
                    return new RecyclerListView.Holder(view);
                }

                public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                    boolean recent;
                    String code;
                    String coloredCode;
                    if (holder.getItemViewType() == 0) {
                        ImageViewEmoji imageView = (ImageViewEmoji) holder.itemView;
                        int position2 = position - 1;
                        if (!this.result.isEmpty() || this.searchWas) {
                            coloredCode = this.result.get(position2).emoji;
                            code = coloredCode;
                            recent = false;
                        } else {
                            coloredCode = Emoji.recentEmoji.get(position2);
                            code = coloredCode;
                            recent = true;
                        }
                        imageView.setImageDrawable(Emoji.getEmojiBigDrawable(coloredCode), recent);
                        imageView.setTag(code);
                    }
                }

                public int getItemViewType(int position) {
                    if (position == 0) {
                        return 1;
                    }
                    if (position != 1 || !this.searchWas || !this.result.isEmpty()) {
                        return 0;
                    }
                    return 2;
                }

                public void search(String text) {
                    if (TextUtils.isEmpty(text)) {
                        this.lastSearchEmojiString = null;
                        if (EmojiViewV2.this.emojiGridView.getAdapter() != EmojiViewV2.this.emojiAdapter) {
                            EmojiViewV2.this.emojiGridView.setAdapter(EmojiViewV2.this.emojiAdapter);
                            this.searchWas = false;
                        }
                        notifyDataSetChanged();
                    } else {
                        this.lastSearchEmojiString = text.toLowerCase();
                    }
                    Runnable runnable = this.searchRunnable;
                    if (runnable != null) {
                        AndroidUtilities.cancelRunOnUIThread(runnable);
                    }
                    if (!TextUtils.isEmpty(this.lastSearchEmojiString)) {
                        AnonymousClass3 r0 = new Runnable() {
                            public void run() {
                                EmojiViewV2.this.emojiSearchField.progressDrawable.startAnimation();
                                final String query = EmojiSearchAdapter.this.lastSearchEmojiString;
                                String[] newLanguage = AndroidUtilities.getCurrentKeyboardLanguage();
                                if (!Arrays.equals(EmojiViewV2.this.lastSearchKeyboardLanguage, newLanguage)) {
                                    MediaDataController.getInstance(EmojiViewV2.this.currentAccount).fetchNewEmojiKeywords(newLanguage);
                                }
                                String[] unused = EmojiViewV2.this.lastSearchKeyboardLanguage = newLanguage;
                                MediaDataController.getInstance(EmojiViewV2.this.currentAccount).getEmojiSuggestions(EmojiViewV2.this.lastSearchKeyboardLanguage, EmojiSearchAdapter.this.lastSearchEmojiString, false, new MediaDataController.KeywordResultCallback() {
                                    public void run(ArrayList<MediaDataController.KeywordResult> param, String alias) {
                                        if (query.equals(EmojiSearchAdapter.this.lastSearchEmojiString)) {
                                            String unused = EmojiSearchAdapter.this.lastSearchAlias = alias;
                                            EmojiViewV2.this.emojiSearchField.progressDrawable.stopAnimation();
                                            boolean unused2 = EmojiSearchAdapter.this.searchWas = true;
                                            if (EmojiViewV2.this.emojiGridView.getAdapter() != EmojiViewV2.this.emojiSearchAdapter) {
                                                EmojiViewV2.this.emojiGridView.setAdapter(EmojiViewV2.this.emojiSearchAdapter);
                                            }
                                            ArrayList unused3 = EmojiSearchAdapter.this.result = param;
                                            EmojiSearchAdapter.this.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                        };
                        this.searchRunnable = r0;
                        AndroidUtilities.runOnUIThread(r0, 300);
                    }
                }
            }

            private class EmojiPagesAdapter extends PagerAdapter implements PagerSlidingTabStrip.IconTabProvider {
                private EmojiPagesAdapter() {
                }

                public void destroyItem(ViewGroup viewGroup, int position, Object object) {
                    viewGroup.removeView((View) EmojiViewV2.this.views.get(position));
                }

                public boolean canScrollToTab(int position) {
                    boolean z = true;
                    if ((position != 1 && position != 2) || EmojiViewV2.this.currentChatId == 0) {
                        return true;
                    }
                    EmojiViewV2 emojiViewV2 = EmojiViewV2.this;
                    if (position != 1) {
                        z = false;
                    }
                    emojiViewV2.showStickerBanHint(z);
                    return false;
                }

                public int getCount() {
                    return EmojiViewV2.this.views.size();
                }

                public Drawable getPageIconDrawable(int position) {
                    return EmojiViewV2.this.tabIcons[position];
                }

                public CharSequence getPageTitle(int position) {
                    if (position == 0) {
                        return LocaleController.getString("Emoji", R.string.Emoji);
                    }
                    if (position == 1) {
                        return LocaleController.getString("AccDescrGIFs", R.string.AccDescrGIFs);
                    }
                    if (position != 2) {
                        return null;
                    }
                    return LocaleController.getString("AccDescrStickers", R.string.AccDescrStickers);
                }

                public void customOnDraw(Canvas canvas, int position) {
                    if (position == 2 && !MediaDataController.getInstance(EmojiViewV2.this.currentAccount).getUnreadStickerSets().isEmpty() && EmojiViewV2.this.dotPaint != null) {
                        canvas.drawCircle((float) ((canvas.getWidth() / 2) + AndroidUtilities.dp(9.0f)), (float) ((canvas.getHeight() / 2) - AndroidUtilities.dp(8.0f)), (float) AndroidUtilities.dp(5.0f), EmojiViewV2.this.dotPaint);
                    }
                }

                public Object instantiateItem(ViewGroup viewGroup, int position) {
                    View view = (View) EmojiViewV2.this.views.get(position);
                    viewGroup.addView(view);
                    return view;
                }

                public boolean isViewFromObject(View view, Object object) {
                    return view == object;
                }

                public void unregisterDataSetObserver(DataSetObserver observer) {
                    if (observer != null) {
                        super.unregisterDataSetObserver(observer);
                    }
                }
            }

            private class GifAdapter extends RecyclerListView.SelectionAdapter {
                private Context mContext;

                public GifAdapter(Context context) {
                    this.mContext = context;
                }

                public boolean isEnabled(RecyclerView.ViewHolder holder) {
                    return false;
                }

                public int getItemCount() {
                    return EmojiViewV2.this.recentGifs.size() + 1;
                }

                public long getItemId(int i) {
                    return (long) i;
                }

                public int getItemViewType(int position) {
                    if (position == 0) {
                        return 1;
                    }
                    return 0;
                }

                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    ContextLinkCell cell;
                    if (viewType != 0) {
                        View view = new View(EmojiViewV2.this.getContext());
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiViewV2.this.searchFieldHeight));
                        cell = view;
                    } else {
                        ContextLinkCell cell2 = new ContextLinkCell(this.mContext);
                        cell2.setContentDescription(LocaleController.getString("AttachGif", R.string.AttachGif));
                        cell2.setCanPreviewGif(true);
                        ContextLinkCell contextLinkCell = cell2;
                        cell = cell2;
                    }
                    return new RecyclerListView.Holder(cell);
                }

                public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                    TLRPC.Document document;
                    if (holder.getItemViewType() == 0 && (document = (TLRPC.Document) EmojiViewV2.this.recentGifs.get(position - 1)) != null) {
                        ((ContextLinkCell) holder.itemView).setGif(document, false);
                    }
                }

                public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
                    if (holder.itemView instanceof ContextLinkCell) {
                        ImageReceiver imageReceiver = ((ContextLinkCell) holder.itemView).getPhotoImage();
                        if (EmojiViewV2.this.pager.getCurrentItem() == 1) {
                            imageReceiver.setAllowStartAnimation(true);
                            imageReceiver.startAnimation();
                            return;
                        }
                        imageReceiver.setAllowStartAnimation(false);
                        imageReceiver.stopAnimation();
                    }
                }
            }

            private class GifSearchAdapter extends RecyclerListView.SelectionAdapter {
                /* access modifiers changed from: private */
                public TLRPC.User bot;
                /* access modifiers changed from: private */
                public String lastSearchImageString;
                private Context mContext;
                /* access modifiers changed from: private */
                public String nextSearchOffset;
                /* access modifiers changed from: private */
                public int reqId;
                /* access modifiers changed from: private */
                public ArrayList<TLRPC.BotInlineResult> results = new ArrayList<>();
                private HashMap<String, TLRPC.BotInlineResult> resultsMap = new HashMap<>();
                /* access modifiers changed from: private */
                public boolean searchEndReached;
                private Runnable searchRunnable;
                private boolean searchingUser;

                public GifSearchAdapter(Context context) {
                    this.mContext = context;
                }

                public boolean isEnabled(RecyclerView.ViewHolder holder) {
                    return false;
                }

                public int getItemCount() {
                    return (this.results.isEmpty() ? 1 : this.results.size()) + 1;
                }

                public int getItemViewType(int position) {
                    if (position == 0) {
                        return 1;
                    }
                    if (this.results.isEmpty()) {
                        return 2;
                    }
                    return 0;
                }

                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view;
                    int i = viewType;
                    if (i == 0) {
                        ContextLinkCell cell = new ContextLinkCell(this.mContext);
                        cell.setContentDescription(LocaleController.getString("AttachGif", R.string.AttachGif));
                        cell.setCanPreviewGif(true);
                        view = cell;
                    } else if (i != 1) {
                        FrameLayout frameLayout = new FrameLayout(EmojiViewV2.this.getContext()) {
                            /* access modifiers changed from: protected */
                            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec((int) (((float) (((EmojiViewV2.this.gifGridView.getMeasuredHeight() - EmojiViewV2.this.searchFieldHeight) - AndroidUtilities.dp(8.0f)) / 3)) * 1.7f), 1073741824));
                            }
                        };
                        ImageView imageView = new ImageView(EmojiViewV2.this.getContext());
                        imageView.setScaleType(ImageView.ScaleType.CENTER);
                        imageView.setImageResource(R.drawable.gif_empty);
                        imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_emojiPanelEmptyText), PorterDuff.Mode.MULTIPLY));
                        frameLayout.addView(imageView, LayoutHelper.createFrame(-2.0f, -2.0f, 17, 0.0f, 0.0f, 0.0f, 59.0f));
                        TextView textView = new TextView(EmojiViewV2.this.getContext());
                        textView.setText(LocaleController.getString("NoGIFsFound", R.string.NoGIFsFound));
                        textView.setTextSize(1, 16.0f);
                        textView.setTextColor(Theme.getColor(Theme.key_chat_emojiPanelEmptyText));
                        frameLayout.addView(textView, LayoutHelper.createFrame(-2.0f, -2.0f, 17, 0.0f, 0.0f, 0.0f, 9.0f));
                        view = frameLayout;
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    } else {
                        view = new View(EmojiViewV2.this.getContext());
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiViewV2.this.searchFieldHeight));
                    }
                    return new RecyclerListView.Holder(view);
                }

                public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                    if (holder.getItemViewType() == 0) {
                        ((ContextLinkCell) holder.itemView).setLink(this.results.get(position - 1), true, false, false);
                    }
                }

                public void search(final String text) {
                    if (this.reqId != 0) {
                        ConnectionsManager.getInstance(EmojiViewV2.this.currentAccount).cancelRequest(this.reqId, true);
                        this.reqId = 0;
                    }
                    if (TextUtils.isEmpty(text)) {
                        this.lastSearchImageString = null;
                        if (EmojiViewV2.this.gifGridView.getAdapter() != EmojiViewV2.this.gifAdapter) {
                            EmojiViewV2.this.gifGridView.setAdapter(EmojiViewV2.this.gifAdapter);
                        }
                        notifyDataSetChanged();
                    } else {
                        this.lastSearchImageString = text.toLowerCase();
                    }
                    Runnable runnable = this.searchRunnable;
                    if (runnable != null) {
                        AndroidUtilities.cancelRunOnUIThread(runnable);
                    }
                    if (!TextUtils.isEmpty(this.lastSearchImageString)) {
                        AnonymousClass2 r0 = new Runnable() {
                            public void run() {
                                GifSearchAdapter.this.search(text, "", true);
                            }
                        };
                        this.searchRunnable = r0;
                        AndroidUtilities.runOnUIThread(r0, 300);
                    }
                }

                private void searchBotUser() {
                    if (!this.searchingUser) {
                        this.searchingUser = true;
                        TLRPC.TL_contacts_resolveUsername req = new TLRPC.TL_contacts_resolveUsername();
                        req.username = MessagesController.getInstance(EmojiViewV2.this.currentAccount).gifSearchBot;
                        ConnectionsManager.getInstance(EmojiViewV2.this.currentAccount).sendRequest(req, new RequestDelegate() {
                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                EmojiViewV2.GifSearchAdapter.this.lambda$searchBotUser$1$EmojiViewV2$GifSearchAdapter(tLObject, tL_error);
                            }
                        });
                    }
                }

                public /* synthetic */ void lambda$searchBotUser$1$EmojiViewV2$GifSearchAdapter(TLObject response, TLRPC.TL_error error) {
                    if (response != null) {
                        AndroidUtilities.runOnUIThread(new Runnable(response) {
                            private final /* synthetic */ TLObject f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                EmojiViewV2.GifSearchAdapter.this.lambda$null$0$EmojiViewV2$GifSearchAdapter(this.f$1);
                            }
                        });
                    }
                }

                public /* synthetic */ void lambda$null$0$EmojiViewV2$GifSearchAdapter(TLObject response) {
                    TLRPC.TL_contacts_resolvedPeer res = (TLRPC.TL_contacts_resolvedPeer) response;
                    MessagesController.getInstance(EmojiViewV2.this.currentAccount).putUsers(res.users, false);
                    MessagesController.getInstance(EmojiViewV2.this.currentAccount).putChats(res.chats, false);
                    MessagesStorage.getInstance(EmojiViewV2.this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
                    String str = this.lastSearchImageString;
                    this.lastSearchImageString = null;
                    search(str, "", false);
                }

                /* access modifiers changed from: private */
                public void search(String query, String offset, boolean searchUser) {
                    if (this.reqId != 0) {
                        ConnectionsManager.getInstance(EmojiViewV2.this.currentAccount).cancelRequest(this.reqId, true);
                        this.reqId = 0;
                    }
                    this.lastSearchImageString = query;
                    TLObject object = MessagesController.getInstance(EmojiViewV2.this.currentAccount).getUserOrChat(MessagesController.getInstance(EmojiViewV2.this.currentAccount).gifSearchBot);
                    if (object instanceof TLRPC.User) {
                        if (TextUtils.isEmpty(offset)) {
                            EmojiViewV2.this.gifSearchField.progressDrawable.startAnimation();
                        }
                        this.bot = (TLRPC.User) object;
                        TLRPC.TL_messages_getInlineBotResults req = new TLRPC.TL_messages_getInlineBotResults();
                        req.query = query == null ? "" : query;
                        req.bot = MessagesController.getInstance(EmojiViewV2.this.currentAccount).getInputUser(this.bot);
                        req.offset = offset;
                        req.peer = new TLRPC.TL_inputPeerEmpty();
                        this.reqId = ConnectionsManager.getInstance(EmojiViewV2.this.currentAccount).sendRequest(req, new RequestDelegate(req, offset) {
                            private final /* synthetic */ TLRPC.TL_messages_getInlineBotResults f$1;
                            private final /* synthetic */ String f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                EmojiViewV2.GifSearchAdapter.this.lambda$search$3$EmojiViewV2$GifSearchAdapter(this.f$1, this.f$2, tLObject, tL_error);
                            }
                        }, 2);
                    } else if (searchUser) {
                        searchBotUser();
                        EmojiViewV2.this.gifSearchField.progressDrawable.startAnimation();
                    }
                }

                public /* synthetic */ void lambda$search$3$EmojiViewV2$GifSearchAdapter(TLRPC.TL_messages_getInlineBotResults req, String offset, TLObject response, TLRPC.TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable(req, offset, response) {
                        private final /* synthetic */ TLRPC.TL_messages_getInlineBotResults f$1;
                        private final /* synthetic */ String f$2;
                        private final /* synthetic */ TLObject f$3;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                        }

                        public final void run() {
                            EmojiViewV2.GifSearchAdapter.this.lambda$null$2$EmojiViewV2$GifSearchAdapter(this.f$1, this.f$2, this.f$3);
                        }
                    });
                }

                public /* synthetic */ void lambda$null$2$EmojiViewV2$GifSearchAdapter(TLRPC.TL_messages_getInlineBotResults req, String offset, TLObject response) {
                    if (req.query.equals(this.lastSearchImageString)) {
                        if (EmojiViewV2.this.gifGridView.getAdapter() != EmojiViewV2.this.gifSearchAdapter) {
                            EmojiViewV2.this.gifGridView.setAdapter(EmojiViewV2.this.gifSearchAdapter);
                        }
                        if (TextUtils.isEmpty(offset)) {
                            this.results.clear();
                            this.resultsMap.clear();
                            EmojiViewV2.this.gifSearchField.progressDrawable.stopAnimation();
                        }
                        boolean z = false;
                        this.reqId = 0;
                        if (response instanceof TLRPC.messages_BotResults) {
                            int addedCount = 0;
                            int oldCount = this.results.size();
                            TLRPC.messages_BotResults res = (TLRPC.messages_BotResults) response;
                            this.nextSearchOffset = res.next_offset;
                            for (int a = 0; a < res.results.size(); a++) {
                                TLRPC.BotInlineResult result = res.results.get(a);
                                if (!this.resultsMap.containsKey(result.id)) {
                                    result.query_id = res.query_id;
                                    this.results.add(result);
                                    this.resultsMap.put(result.id, result);
                                    addedCount++;
                                }
                            }
                            if (oldCount == this.results.size() || TextUtils.isEmpty(this.nextSearchOffset)) {
                                z = true;
                            }
                            this.searchEndReached = z;
                            if (addedCount != 0) {
                                if (oldCount != 0) {
                                    notifyItemChanged(oldCount);
                                }
                                notifyItemRangeInserted(oldCount + 1, addedCount);
                            } else if (this.results.isEmpty()) {
                                notifyDataSetChanged();
                            }
                        } else {
                            notifyDataSetChanged();
                        }
                    }
                }

                public void notifyDataSetChanged() {
                    super.notifyDataSetChanged();
                }
            }

            private class StickersSearchGridAdapter extends RecyclerListView.SelectionAdapter {
                /* access modifiers changed from: private */
                public SparseArray<Object> cache = new SparseArray<>();
                private SparseArray<Object> cacheParent = new SparseArray<>();
                boolean cleared;
                private Context context;
                /* access modifiers changed from: private */
                public ArrayList<ArrayList<TLRPC.Document>> emojiArrays = new ArrayList<>();
                /* access modifiers changed from: private */
                public int emojiSearchId;
                /* access modifiers changed from: private */
                public HashMap<ArrayList<TLRPC.Document>, String> emojiStickers = new HashMap<>();
                /* access modifiers changed from: private */
                public ArrayList<TLRPC.TL_messages_stickerSet> localPacks = new ArrayList<>();
                /* access modifiers changed from: private */
                public HashMap<TLRPC.TL_messages_stickerSet, Integer> localPacksByName = new HashMap<>();
                /* access modifiers changed from: private */
                public HashMap<TLRPC.TL_messages_stickerSet, Boolean> localPacksByShortName = new HashMap<>();
                private SparseArray<String> positionToEmoji = new SparseArray<>();
                private SparseIntArray positionToRow = new SparseIntArray();
                /* access modifiers changed from: private */
                public SparseArray<TLRPC.StickerSetCovered> positionsToSets = new SparseArray<>();
                /* access modifiers changed from: private */
                public int reqId;
                /* access modifiers changed from: private */
                public int reqId2;
                private SparseArray<Object> rowStartPack = new SparseArray<>();
                /* access modifiers changed from: private */
                public String searchQuery;
                private Runnable searchRunnable = new Runnable() {
                    /* access modifiers changed from: private */
                    public void clear() {
                        if (!StickersSearchGridAdapter.this.cleared) {
                            StickersSearchGridAdapter.this.cleared = true;
                            StickersSearchGridAdapter.this.emojiStickers.clear();
                            StickersSearchGridAdapter.this.emojiArrays.clear();
                            StickersSearchGridAdapter.this.localPacks.clear();
                            StickersSearchGridAdapter.this.serverPacks.clear();
                            StickersSearchGridAdapter.this.localPacksByShortName.clear();
                            StickersSearchGridAdapter.this.localPacksByName.clear();
                        }
                    }

                    public void run() {
                        if (!TextUtils.isEmpty(StickersSearchGridAdapter.this.searchQuery)) {
                            EmojiViewV2.this.stickersSearchField.progressDrawable.startAnimation();
                            StickersSearchGridAdapter.this.cleared = false;
                            final int lastId = StickersSearchGridAdapter.access$13304(StickersSearchGridAdapter.this);
                            ArrayList<TLRPC.Document> emojiStickersArray = new ArrayList<>(0);
                            LongSparseArray<TLRPC.Document> emojiStickersMap = new LongSparseArray<>(0);
                            final HashMap<String, ArrayList<TLRPC.Document>> allStickers = MediaDataController.getInstance(EmojiViewV2.this.currentAccount).getAllStickers();
                            if (StickersSearchGridAdapter.this.searchQuery.length() <= 14) {
                                CharSequence emoji = StickersSearchGridAdapter.this.searchQuery;
                                int length = emoji.length();
                                int a = 0;
                                while (a < length) {
                                    if (a < length - 1 && ((emoji.charAt(a) == 55356 && emoji.charAt(a + 1) >= 57339 && emoji.charAt(a + 1) <= 57343) || (emoji.charAt(a) == 8205 && (emoji.charAt(a + 1) == 9792 || emoji.charAt(a + 1) == 9794)))) {
                                        emoji = TextUtils.concat(new CharSequence[]{emoji.subSequence(0, a), emoji.subSequence(a + 2, emoji.length())});
                                        length -= 2;
                                        a--;
                                    } else if (emoji.charAt(a) == 65039) {
                                        emoji = TextUtils.concat(new CharSequence[]{emoji.subSequence(0, a), emoji.subSequence(a + 1, emoji.length())});
                                        length--;
                                        a--;
                                    }
                                    a++;
                                }
                                ArrayList<TLRPC.Document> newStickers = allStickers != null ? allStickers.get(emoji.toString()) : null;
                                if (newStickers != null && !newStickers.isEmpty()) {
                                    clear();
                                    emojiStickersArray.addAll(newStickers);
                                    int size = newStickers.size();
                                    for (int a2 = 0; a2 < size; a2++) {
                                        TLRPC.Document document = newStickers.get(a2);
                                        emojiStickersMap.put(document.id, document);
                                    }
                                    StickersSearchGridAdapter.this.emojiStickers.put(emojiStickersArray, StickersSearchGridAdapter.this.searchQuery);
                                    StickersSearchGridAdapter.this.emojiArrays.add(emojiStickersArray);
                                }
                            }
                            if (allStickers != null && !allStickers.isEmpty() && StickersSearchGridAdapter.this.searchQuery.length() > 1) {
                                String[] newLanguage = AndroidUtilities.getCurrentKeyboardLanguage();
                                if (!Arrays.equals(EmojiViewV2.this.lastSearchKeyboardLanguage, newLanguage)) {
                                    MediaDataController.getInstance(EmojiViewV2.this.currentAccount).fetchNewEmojiKeywords(newLanguage);
                                }
                                String[] unused = EmojiViewV2.this.lastSearchKeyboardLanguage = newLanguage;
                                MediaDataController.getInstance(EmojiViewV2.this.currentAccount).getEmojiSuggestions(EmojiViewV2.this.lastSearchKeyboardLanguage, StickersSearchGridAdapter.this.searchQuery, false, new MediaDataController.KeywordResultCallback() {
                                    public void run(ArrayList<MediaDataController.KeywordResult> param, String alias) {
                                        if (lastId == StickersSearchGridAdapter.this.emojiSearchId) {
                                            boolean added = false;
                                            int size = param.size();
                                            for (int a = 0; a < size; a++) {
                                                String emoji = param.get(a).emoji;
                                                HashMap hashMap = allStickers;
                                                ArrayList<TLRPC.Document> newStickers = hashMap != null ? (ArrayList) hashMap.get(emoji) : null;
                                                if (newStickers != null && !newStickers.isEmpty()) {
                                                    AnonymousClass1.this.clear();
                                                    if (!StickersSearchGridAdapter.this.emojiStickers.containsKey(newStickers)) {
                                                        StickersSearchGridAdapter.this.emojiStickers.put(newStickers, emoji);
                                                        StickersSearchGridAdapter.this.emojiArrays.add(newStickers);
                                                        added = true;
                                                    }
                                                }
                                            }
                                            if (added) {
                                                StickersSearchGridAdapter.this.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                });
                            }
                            ArrayList<TLRPC.TL_messages_stickerSet> local = MediaDataController.getInstance(EmojiViewV2.this.currentAccount).getStickerSets(0);
                            int size2 = local.size();
                            for (int a3 = 0; a3 < size2; a3++) {
                                TLRPC.TL_messages_stickerSet set = local.get(a3);
                                int indexOfIgnoreCase = AndroidUtilities.indexOfIgnoreCase(set.set.title, StickersSearchGridAdapter.this.searchQuery);
                                int index = indexOfIgnoreCase;
                                if (indexOfIgnoreCase >= 0) {
                                    if (index == 0 || set.set.title.charAt(index - 1) == ' ') {
                                        clear();
                                        StickersSearchGridAdapter.this.localPacks.add(set);
                                        StickersSearchGridAdapter.this.localPacksByName.put(set, Integer.valueOf(index));
                                    }
                                } else if (set.set.short_name != null) {
                                    int indexOfIgnoreCase2 = AndroidUtilities.indexOfIgnoreCase(set.set.short_name, StickersSearchGridAdapter.this.searchQuery);
                                    int index2 = indexOfIgnoreCase2;
                                    if (indexOfIgnoreCase2 >= 0 && (index2 == 0 || set.set.short_name.charAt(index2 - 1) == ' ')) {
                                        clear();
                                        StickersSearchGridAdapter.this.localPacks.add(set);
                                        StickersSearchGridAdapter.this.localPacksByShortName.put(set, true);
                                    }
                                }
                            }
                            ArrayList<TLRPC.TL_messages_stickerSet> local2 = MediaDataController.getInstance(EmojiViewV2.this.currentAccount).getStickerSets(3);
                            int size3 = local2.size();
                            for (int a4 = 0; a4 < size3; a4++) {
                                TLRPC.TL_messages_stickerSet set2 = local2.get(a4);
                                int indexOfIgnoreCase3 = AndroidUtilities.indexOfIgnoreCase(set2.set.title, StickersSearchGridAdapter.this.searchQuery);
                                int index3 = indexOfIgnoreCase3;
                                if (indexOfIgnoreCase3 >= 0) {
                                    if (index3 == 0 || set2.set.title.charAt(index3 - 1) == ' ') {
                                        clear();
                                        StickersSearchGridAdapter.this.localPacks.add(set2);
                                        StickersSearchGridAdapter.this.localPacksByName.put(set2, Integer.valueOf(index3));
                                    }
                                } else if (set2.set.short_name != null) {
                                    int indexOfIgnoreCase4 = AndroidUtilities.indexOfIgnoreCase(set2.set.short_name, StickersSearchGridAdapter.this.searchQuery);
                                    int index4 = indexOfIgnoreCase4;
                                    if (indexOfIgnoreCase4 >= 0 && (index4 == 0 || set2.set.short_name.charAt(index4 - 1) == ' ')) {
                                        clear();
                                        StickersSearchGridAdapter.this.localPacks.add(set2);
                                        StickersSearchGridAdapter.this.localPacksByShortName.put(set2, true);
                                    }
                                }
                            }
                            if ((!StickersSearchGridAdapter.this.localPacks.isEmpty() || !StickersSearchGridAdapter.this.emojiStickers.isEmpty()) && EmojiViewV2.this.stickersGridView.getAdapter() != EmojiViewV2.this.stickersSearchGridAdapter) {
                                EmojiViewV2.this.stickersGridView.setAdapter(EmojiViewV2.this.stickersSearchGridAdapter);
                            }
                            TLRPC.TL_messages_searchStickerSets req = new TLRPC.TL_messages_searchStickerSets();
                            req.q = StickersSearchGridAdapter.this.searchQuery;
                            StickersSearchGridAdapter stickersSearchGridAdapter = StickersSearchGridAdapter.this;
                            int unused2 = stickersSearchGridAdapter.reqId = ConnectionsManager.getInstance(EmojiViewV2.this.currentAccount).sendRequest(req, 
                            /*  JADX ERROR: Method code generation error
                                jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x031f: INVOKE  
                                  (r7v3 'stickersSearchGridAdapter' im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter)
                                  (wrap: int : 0x031b: INVOKE  (r8v6 int) = 
                                  (wrap: im.bclpbkiauv.tgnet.ConnectionsManager : 0x0312: INVOKE  (r8v5 im.bclpbkiauv.tgnet.ConnectionsManager) = 
                                  (wrap: int : 0x030e: INVOKE  (r8v4 int) = 
                                  (wrap: im.bclpbkiauv.ui.components.EmojiViewV2 : 0x030c: IGET  (r8v3 im.bclpbkiauv.ui.components.EmojiViewV2) = 
                                  (r7v3 'stickersSearchGridAdapter' im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter)
                                 im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this$0 im.bclpbkiauv.ui.components.EmojiViewV2)
                                 im.bclpbkiauv.ui.components.EmojiViewV2.access$600(im.bclpbkiauv.ui.components.EmojiViewV2):int type: STATIC)
                                 im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(int):im.bclpbkiauv.tgnet.ConnectionsManager type: STATIC)
                                  (r6v12 'req' im.bclpbkiauv.tgnet.TLRPC$TL_messages_searchStickerSets)
                                  (wrap: im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$StickersSearchGridAdapter$1$n9La_f7AaW0hk-xnHKFPWeCpFgs : 0x0318: CONSTRUCTOR  (r9v1 im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$StickersSearchGridAdapter$1$n9La_f7AaW0hk-xnHKFPWeCpFgs) = 
                                  (r14v0 'this' im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter$1 A[THIS])
                                  (r6v12 'req' im.bclpbkiauv.tgnet.TLRPC$TL_messages_searchStickerSets)
                                 call: im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$StickersSearchGridAdapter$1$n9La_f7AaW0hk-xnHKFPWeCpFgs.<init>(im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter$1, im.bclpbkiauv.tgnet.TLRPC$TL_messages_searchStickerSets):void type: CONSTRUCTOR)
                                 im.bclpbkiauv.tgnet.ConnectionsManager.sendRequest(im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.RequestDelegate):int type: VIRTUAL)
                                 im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.access$13502(im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter, int):int type: STATIC in method: im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.1.run():void, dex: classes6.dex
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                                	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:98)
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:480)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                	at jadx.core.codegen.ClassGen.addInsnBody(ClassGen.java:437)
                                	at jadx.core.codegen.ClassGen.addField(ClassGen.java:378)
                                	at jadx.core.codegen.ClassGen.addFields(ClassGen.java:348)
                                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:226)
                                	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                                	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                                	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                                	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                                	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                                	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                                Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0000: IPUT  
                                  (wrap: int : 0x031b: INVOKE  (r8v6 int) = 
                                  (wrap: im.bclpbkiauv.tgnet.ConnectionsManager : 0x0312: INVOKE  (r8v5 im.bclpbkiauv.tgnet.ConnectionsManager) = 
                                  (wrap: int : 0x030e: INVOKE  (r8v4 int) = 
                                  (wrap: im.bclpbkiauv.ui.components.EmojiViewV2 : 0x030c: IGET  (r8v3 im.bclpbkiauv.ui.components.EmojiViewV2) = 
                                  (r7v3 'stickersSearchGridAdapter' im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter)
                                 im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this$0 im.bclpbkiauv.ui.components.EmojiViewV2)
                                 im.bclpbkiauv.ui.components.EmojiViewV2.access$600(im.bclpbkiauv.ui.components.EmojiViewV2):int type: STATIC)
                                 im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(int):im.bclpbkiauv.tgnet.ConnectionsManager type: STATIC)
                                  (r6v12 'req' im.bclpbkiauv.tgnet.TLRPC$TL_messages_searchStickerSets)
                                  (wrap: im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$StickersSearchGridAdapter$1$n9La_f7AaW0hk-xnHKFPWeCpFgs : 0x0318: CONSTRUCTOR  (r9v1 im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$StickersSearchGridAdapter$1$n9La_f7AaW0hk-xnHKFPWeCpFgs) = 
                                  (r14v0 'this' im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter$1 A[THIS])
                                  (r6v12 'req' im.bclpbkiauv.tgnet.TLRPC$TL_messages_searchStickerSets)
                                 call: im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$StickersSearchGridAdapter$1$n9La_f7AaW0hk-xnHKFPWeCpFgs.<init>(im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter$1, im.bclpbkiauv.tgnet.TLRPC$TL_messages_searchStickerSets):void type: CONSTRUCTOR)
                                 im.bclpbkiauv.tgnet.ConnectionsManager.sendRequest(im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.RequestDelegate):int type: VIRTUAL)
                                  (r7v3 'stickersSearchGridAdapter' im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter)
                                 im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.reqId int in method: im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.1.run():void, dex: classes6.dex
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                	at jadx.core.codegen.InsnGen.inlineMethod(InsnGen.java:924)
                                	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:684)
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                	... 64 more
                                Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x031b: INVOKE  (r8v6 int) = 
                                  (wrap: im.bclpbkiauv.tgnet.ConnectionsManager : 0x0312: INVOKE  (r8v5 im.bclpbkiauv.tgnet.ConnectionsManager) = 
                                  (wrap: int : 0x030e: INVOKE  (r8v4 int) = 
                                  (wrap: im.bclpbkiauv.ui.components.EmojiViewV2 : 0x030c: IGET  (r8v3 im.bclpbkiauv.ui.components.EmojiViewV2) = 
                                  (r7v3 'stickersSearchGridAdapter' im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter)
                                 im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this$0 im.bclpbkiauv.ui.components.EmojiViewV2)
                                 im.bclpbkiauv.ui.components.EmojiViewV2.access$600(im.bclpbkiauv.ui.components.EmojiViewV2):int type: STATIC)
                                 im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(int):im.bclpbkiauv.tgnet.ConnectionsManager type: STATIC)
                                  (r6v12 'req' im.bclpbkiauv.tgnet.TLRPC$TL_messages_searchStickerSets)
                                  (wrap: im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$StickersSearchGridAdapter$1$n9La_f7AaW0hk-xnHKFPWeCpFgs : 0x0318: CONSTRUCTOR  (r9v1 im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$StickersSearchGridAdapter$1$n9La_f7AaW0hk-xnHKFPWeCpFgs) = 
                                  (r14v0 'this' im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter$1 A[THIS])
                                  (r6v12 'req' im.bclpbkiauv.tgnet.TLRPC$TL_messages_searchStickerSets)
                                 call: im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$StickersSearchGridAdapter$1$n9La_f7AaW0hk-xnHKFPWeCpFgs.<init>(im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter$1, im.bclpbkiauv.tgnet.TLRPC$TL_messages_searchStickerSets):void type: CONSTRUCTOR)
                                 im.bclpbkiauv.tgnet.ConnectionsManager.sendRequest(im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.RequestDelegate):int type: VIRTUAL in method: im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.1.run():void, dex: classes6.dex
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:429)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                	... 68 more
                                Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0318: CONSTRUCTOR  (r9v1 im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$StickersSearchGridAdapter$1$n9La_f7AaW0hk-xnHKFPWeCpFgs) = 
                                  (r14v0 'this' im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter$1 A[THIS])
                                  (r6v12 'req' im.bclpbkiauv.tgnet.TLRPC$TL_messages_searchStickerSets)
                                 call: im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$StickersSearchGridAdapter$1$n9La_f7AaW0hk-xnHKFPWeCpFgs.<init>(im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter$1, im.bclpbkiauv.tgnet.TLRPC$TL_messages_searchStickerSets):void type: CONSTRUCTOR in method: im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.1.run():void, dex: classes6.dex
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                	... 72 more
                                Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$StickersSearchGridAdapter$1$n9La_f7AaW0hk-xnHKFPWeCpFgs, state: NOT_LOADED
                                	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                                	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                	... 78 more
                                */
                            /*
                                this = this;
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r0 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                java.lang.String r0 = r0.searchQuery
                                boolean r0 = android.text.TextUtils.isEmpty(r0)
                                if (r0 == 0) goto L_0x000d
                                return
                            L_0x000d:
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r0 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                im.bclpbkiauv.ui.components.EmojiViewV2 r0 = im.bclpbkiauv.ui.components.EmojiViewV2.this
                                im.bclpbkiauv.ui.components.EmojiViewV2$SearchField r0 = r0.stickersSearchField
                                im.bclpbkiauv.ui.components.CloseProgressDrawable2 r0 = r0.progressDrawable
                                r0.startAnimation()
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r0 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                r1 = 0
                                r0.cleared = r1
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r0 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                int r0 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.access$13304(r0)
                                java.util.ArrayList r2 = new java.util.ArrayList
                                r2.<init>(r1)
                                android.util.LongSparseArray r3 = new android.util.LongSparseArray
                                r3.<init>(r1)
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r4 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                im.bclpbkiauv.ui.components.EmojiViewV2 r4 = im.bclpbkiauv.ui.components.EmojiViewV2.this
                                int r4 = r4.currentAccount
                                im.bclpbkiauv.messenger.MediaDataController r4 = im.bclpbkiauv.messenger.MediaDataController.getInstance(r4)
                                java.util.HashMap r4 = r4.getAllStickers()
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r5 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                java.lang.String r5 = r5.searchQuery
                                int r5 = r5.length()
                                r6 = 14
                                r7 = 1
                                if (r5 > r6) goto L_0x012b
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r5 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                java.lang.String r5 = r5.searchQuery
                                int r6 = r5.length()
                                r8 = 0
                            L_0x005b:
                                if (r8 >= r6) goto L_0x00e2
                                int r9 = r6 + -1
                                r10 = 2
                                if (r8 >= r9) goto L_0x00ba
                                char r9 = r5.charAt(r8)
                                r11 = 55356(0xd83c, float:7.757E-41)
                                if (r9 != r11) goto L_0x0081
                                int r9 = r8 + 1
                                char r9 = r5.charAt(r9)
                                r11 = 57339(0xdffb, float:8.0349E-41)
                                if (r9 < r11) goto L_0x0081
                                int r9 = r8 + 1
                                char r9 = r5.charAt(r9)
                                r11 = 57343(0xdfff, float:8.0355E-41)
                                if (r9 <= r11) goto L_0x009d
                            L_0x0081:
                                char r9 = r5.charAt(r8)
                                r11 = 8205(0x200d, float:1.1498E-41)
                                if (r9 != r11) goto L_0x00ba
                                int r9 = r8 + 1
                                char r9 = r5.charAt(r9)
                                r11 = 9792(0x2640, float:1.3722E-41)
                                if (r9 == r11) goto L_0x009d
                                int r9 = r8 + 1
                                char r9 = r5.charAt(r9)
                                r11 = 9794(0x2642, float:1.3724E-41)
                                if (r9 != r11) goto L_0x00ba
                            L_0x009d:
                                java.lang.CharSequence[] r9 = new java.lang.CharSequence[r10]
                                java.lang.CharSequence r10 = r5.subSequence(r1, r8)
                                r9[r1] = r10
                                int r10 = r8 + 2
                                int r11 = r5.length()
                                java.lang.CharSequence r10 = r5.subSequence(r10, r11)
                                r9[r7] = r10
                                java.lang.CharSequence r5 = android.text.TextUtils.concat(r9)
                                int r6 = r6 + -2
                                int r8 = r8 + -1
                                goto L_0x00df
                            L_0x00ba:
                                char r9 = r5.charAt(r8)
                                r11 = 65039(0xfe0f, float:9.1139E-41)
                                if (r9 != r11) goto L_0x00df
                                java.lang.CharSequence[] r9 = new java.lang.CharSequence[r10]
                                java.lang.CharSequence r10 = r5.subSequence(r1, r8)
                                r9[r1] = r10
                                int r10 = r8 + 1
                                int r11 = r5.length()
                                java.lang.CharSequence r10 = r5.subSequence(r10, r11)
                                r9[r7] = r10
                                java.lang.CharSequence r5 = android.text.TextUtils.concat(r9)
                                int r6 = r6 + -1
                                int r8 = r8 + -1
                            L_0x00df:
                                int r8 = r8 + r7
                                goto L_0x005b
                            L_0x00e2:
                                if (r4 == 0) goto L_0x00ef
                                java.lang.String r8 = r5.toString()
                                java.lang.Object r8 = r4.get(r8)
                                java.util.ArrayList r8 = (java.util.ArrayList) r8
                                goto L_0x00f0
                            L_0x00ef:
                                r8 = 0
                            L_0x00f0:
                                if (r8 == 0) goto L_0x012b
                                boolean r9 = r8.isEmpty()
                                if (r9 != 0) goto L_0x012b
                                r14.clear()
                                r2.addAll(r8)
                                r9 = 0
                                int r10 = r8.size()
                            L_0x0103:
                                if (r9 >= r10) goto L_0x0113
                                java.lang.Object r11 = r8.get(r9)
                                im.bclpbkiauv.tgnet.TLRPC$Document r11 = (im.bclpbkiauv.tgnet.TLRPC.Document) r11
                                long r12 = r11.id
                                r3.put(r12, r11)
                                int r9 = r9 + 1
                                goto L_0x0103
                            L_0x0113:
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r9 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                java.util.HashMap r9 = r9.emojiStickers
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r10 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                java.lang.String r10 = r10.searchQuery
                                r9.put(r2, r10)
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r9 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                java.util.ArrayList r9 = r9.emojiArrays
                                r9.add(r2)
                            L_0x012b:
                                if (r4 == 0) goto L_0x0189
                                boolean r5 = r4.isEmpty()
                                if (r5 != 0) goto L_0x0189
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r5 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                java.lang.String r5 = r5.searchQuery
                                int r5 = r5.length()
                                if (r5 <= r7) goto L_0x0189
                                java.lang.String[] r5 = im.bclpbkiauv.messenger.AndroidUtilities.getCurrentKeyboardLanguage()
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r6 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                im.bclpbkiauv.ui.components.EmojiViewV2 r6 = im.bclpbkiauv.ui.components.EmojiViewV2.this
                                java.lang.String[] r6 = r6.lastSearchKeyboardLanguage
                                boolean r6 = java.util.Arrays.equals(r6, r5)
                                if (r6 != 0) goto L_0x0160
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r6 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                im.bclpbkiauv.ui.components.EmojiViewV2 r6 = im.bclpbkiauv.ui.components.EmojiViewV2.this
                                int r6 = r6.currentAccount
                                im.bclpbkiauv.messenger.MediaDataController r6 = im.bclpbkiauv.messenger.MediaDataController.getInstance(r6)
                                r6.fetchNewEmojiKeywords(r5)
                            L_0x0160:
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r6 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                im.bclpbkiauv.ui.components.EmojiViewV2 r6 = im.bclpbkiauv.ui.components.EmojiViewV2.this
                                java.lang.String[] unused = r6.lastSearchKeyboardLanguage = r5
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r6 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                im.bclpbkiauv.ui.components.EmojiViewV2 r6 = im.bclpbkiauv.ui.components.EmojiViewV2.this
                                int r6 = r6.currentAccount
                                im.bclpbkiauv.messenger.MediaDataController r6 = im.bclpbkiauv.messenger.MediaDataController.getInstance(r6)
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r8 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                im.bclpbkiauv.ui.components.EmojiViewV2 r8 = im.bclpbkiauv.ui.components.EmojiViewV2.this
                                java.lang.String[] r8 = r8.lastSearchKeyboardLanguage
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r9 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                java.lang.String r9 = r9.searchQuery
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter$1$1 r10 = new im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter$1$1
                                r10.<init>(r0, r4)
                                r6.getEmojiSuggestions(r8, r9, r1, r10)
                            L_0x0189:
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r5 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                im.bclpbkiauv.ui.components.EmojiViewV2 r5 = im.bclpbkiauv.ui.components.EmojiViewV2.this
                                int r5 = r5.currentAccount
                                im.bclpbkiauv.messenger.MediaDataController r5 = im.bclpbkiauv.messenger.MediaDataController.getInstance(r5)
                                java.util.ArrayList r5 = r5.getStickerSets(r1)
                                r6 = 0
                                int r8 = r5.size()
                            L_0x019e:
                                r9 = 32
                                if (r6 >= r8) goto L_0x0223
                                java.lang.Object r10 = r5.get(r6)
                                im.bclpbkiauv.tgnet.TLRPC$TL_messages_stickerSet r10 = (im.bclpbkiauv.tgnet.TLRPC.TL_messages_stickerSet) r10
                                im.bclpbkiauv.tgnet.TLRPC$StickerSet r11 = r10.set
                                java.lang.String r11 = r11.title
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r12 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                java.lang.String r12 = r12.searchQuery
                                int r11 = im.bclpbkiauv.messenger.AndroidUtilities.indexOfIgnoreCase(r11, r12)
                                r12 = r11
                                if (r11 < 0) goto L_0x01e1
                                if (r12 == 0) goto L_0x01c7
                                im.bclpbkiauv.tgnet.TLRPC$StickerSet r11 = r10.set
                                java.lang.String r11 = r11.title
                                int r13 = r12 + -1
                                char r11 = r11.charAt(r13)
                                if (r11 != r9) goto L_0x021f
                            L_0x01c7:
                                r14.clear()
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r9 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                java.util.ArrayList r9 = r9.localPacks
                                r9.add(r10)
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r9 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                java.util.HashMap r9 = r9.localPacksByName
                                java.lang.Integer r11 = java.lang.Integer.valueOf(r12)
                                r9.put(r10, r11)
                                goto L_0x021f
                            L_0x01e1:
                                im.bclpbkiauv.tgnet.TLRPC$StickerSet r11 = r10.set
                                java.lang.String r11 = r11.short_name
                                if (r11 == 0) goto L_0x021f
                                im.bclpbkiauv.tgnet.TLRPC$StickerSet r11 = r10.set
                                java.lang.String r11 = r11.short_name
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r13 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                java.lang.String r13 = r13.searchQuery
                                int r11 = im.bclpbkiauv.messenger.AndroidUtilities.indexOfIgnoreCase(r11, r13)
                                r12 = r11
                                if (r11 < 0) goto L_0x021f
                                if (r12 == 0) goto L_0x0206
                                im.bclpbkiauv.tgnet.TLRPC$StickerSet r11 = r10.set
                                java.lang.String r11 = r11.short_name
                                int r13 = r12 + -1
                                char r11 = r11.charAt(r13)
                                if (r11 != r9) goto L_0x021f
                            L_0x0206:
                                r14.clear()
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r9 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                java.util.ArrayList r9 = r9.localPacks
                                r9.add(r10)
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r9 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                java.util.HashMap r9 = r9.localPacksByShortName
                                java.lang.Boolean r11 = java.lang.Boolean.valueOf(r7)
                                r9.put(r10, r11)
                            L_0x021f:
                                int r6 = r6 + 1
                                goto L_0x019e
                            L_0x0223:
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r6 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                im.bclpbkiauv.ui.components.EmojiViewV2 r6 = im.bclpbkiauv.ui.components.EmojiViewV2.this
                                int r6 = r6.currentAccount
                                im.bclpbkiauv.messenger.MediaDataController r6 = im.bclpbkiauv.messenger.MediaDataController.getInstance(r6)
                                r8 = 3
                                java.util.ArrayList r5 = r6.getStickerSets(r8)
                                r6 = 0
                                int r8 = r5.size()
                            L_0x0239:
                                if (r6 >= r8) goto L_0x02bc
                                java.lang.Object r10 = r5.get(r6)
                                im.bclpbkiauv.tgnet.TLRPC$TL_messages_stickerSet r10 = (im.bclpbkiauv.tgnet.TLRPC.TL_messages_stickerSet) r10
                                im.bclpbkiauv.tgnet.TLRPC$StickerSet r11 = r10.set
                                java.lang.String r11 = r11.title
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r12 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                java.lang.String r12 = r12.searchQuery
                                int r11 = im.bclpbkiauv.messenger.AndroidUtilities.indexOfIgnoreCase(r11, r12)
                                r12 = r11
                                if (r11 < 0) goto L_0x027a
                                if (r12 == 0) goto L_0x0260
                                im.bclpbkiauv.tgnet.TLRPC$StickerSet r11 = r10.set
                                java.lang.String r11 = r11.title
                                int r13 = r12 + -1
                                char r11 = r11.charAt(r13)
                                if (r11 != r9) goto L_0x02b8
                            L_0x0260:
                                r14.clear()
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r11 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                java.util.ArrayList r11 = r11.localPacks
                                r11.add(r10)
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r11 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                java.util.HashMap r11 = r11.localPacksByName
                                java.lang.Integer r13 = java.lang.Integer.valueOf(r12)
                                r11.put(r10, r13)
                                goto L_0x02b8
                            L_0x027a:
                                im.bclpbkiauv.tgnet.TLRPC$StickerSet r11 = r10.set
                                java.lang.String r11 = r11.short_name
                                if (r11 == 0) goto L_0x02b8
                                im.bclpbkiauv.tgnet.TLRPC$StickerSet r11 = r10.set
                                java.lang.String r11 = r11.short_name
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r13 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                java.lang.String r13 = r13.searchQuery
                                int r11 = im.bclpbkiauv.messenger.AndroidUtilities.indexOfIgnoreCase(r11, r13)
                                r12 = r11
                                if (r11 < 0) goto L_0x02b8
                                if (r12 == 0) goto L_0x029f
                                im.bclpbkiauv.tgnet.TLRPC$StickerSet r11 = r10.set
                                java.lang.String r11 = r11.short_name
                                int r13 = r12 + -1
                                char r11 = r11.charAt(r13)
                                if (r11 != r9) goto L_0x02b8
                            L_0x029f:
                                r14.clear()
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r11 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                java.util.ArrayList r11 = r11.localPacks
                                r11.add(r10)
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r11 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                java.util.HashMap r11 = r11.localPacksByShortName
                                java.lang.Boolean r13 = java.lang.Boolean.valueOf(r7)
                                r11.put(r10, r13)
                            L_0x02b8:
                                int r6 = r6 + 1
                                goto L_0x0239
                            L_0x02bc:
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r6 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                java.util.ArrayList r6 = r6.localPacks
                                boolean r6 = r6.isEmpty()
                                if (r6 == 0) goto L_0x02d4
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r6 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                java.util.HashMap r6 = r6.emojiStickers
                                boolean r6 = r6.isEmpty()
                                if (r6 != 0) goto L_0x02fd
                            L_0x02d4:
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r6 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                im.bclpbkiauv.ui.components.EmojiViewV2 r6 = im.bclpbkiauv.ui.components.EmojiViewV2.this
                                im.bclpbkiauv.ui.components.RecyclerListView r6 = r6.stickersGridView
                                androidx.recyclerview.widget.RecyclerView$Adapter r6 = r6.getAdapter()
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r7 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                im.bclpbkiauv.ui.components.EmojiViewV2 r7 = im.bclpbkiauv.ui.components.EmojiViewV2.this
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r7 = r7.stickersSearchGridAdapter
                                if (r6 == r7) goto L_0x02fd
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r6 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                im.bclpbkiauv.ui.components.EmojiViewV2 r6 = im.bclpbkiauv.ui.components.EmojiViewV2.this
                                im.bclpbkiauv.ui.components.RecyclerListView r6 = r6.stickersGridView
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r7 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                im.bclpbkiauv.ui.components.EmojiViewV2 r7 = im.bclpbkiauv.ui.components.EmojiViewV2.this
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r7 = r7.stickersSearchGridAdapter
                                r6.setAdapter(r7)
                            L_0x02fd:
                                im.bclpbkiauv.tgnet.TLRPC$TL_messages_searchStickerSets r6 = new im.bclpbkiauv.tgnet.TLRPC$TL_messages_searchStickerSets
                                r6.<init>()
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r7 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                java.lang.String r7 = r7.searchQuery
                                r6.q = r7
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r7 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                im.bclpbkiauv.ui.components.EmojiViewV2 r8 = im.bclpbkiauv.ui.components.EmojiViewV2.this
                                int r8 = r8.currentAccount
                                im.bclpbkiauv.tgnet.ConnectionsManager r8 = im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(r8)
                                im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$StickersSearchGridAdapter$1$n9La_f7AaW0hk-xnHKFPWeCpFgs r9 = new im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$StickersSearchGridAdapter$1$n9La_f7AaW0hk-xnHKFPWeCpFgs
                                r9.<init>(r14, r6)
                                int r8 = r8.sendRequest(r6, r9)
                                int unused = r7.reqId = r8
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r7 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                java.lang.String r7 = r7.searchQuery
                                boolean r7 = im.bclpbkiauv.messenger.Emoji.isValidEmoji(r7)
                                if (r7 == 0) goto L_0x0355
                                im.bclpbkiauv.tgnet.TLRPC$TL_messages_getStickers r7 = new im.bclpbkiauv.tgnet.TLRPC$TL_messages_getStickers
                                r7.<init>()
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r8 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                java.lang.String r8 = r8.searchQuery
                                r7.emoticon = r8
                                r7.hash = r1
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r1 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                im.bclpbkiauv.ui.components.EmojiViewV2 r8 = im.bclpbkiauv.ui.components.EmojiViewV2.this
                                int r8 = r8.currentAccount
                                im.bclpbkiauv.tgnet.ConnectionsManager r8 = im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(r8)
                                im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$StickersSearchGridAdapter$1$NYMSmnjQqnFgOSsVyWu9fGDnt58 r9 = new im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$StickersSearchGridAdapter$1$NYMSmnjQqnFgOSsVyWu9fGDnt58
                                r9.<init>(r14, r7, r2, r3)
                                int r8 = r8.sendRequest(r7, r9)
                                int unused = r1.reqId2 = r8
                            L_0x0355:
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter r1 = im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.this
                                r1.notifyDataSetChanged()
                                return
                            */
                            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.AnonymousClass1.run():void");
                        }

                        public /* synthetic */ void lambda$run$1$EmojiViewV2$StickersSearchGridAdapter$1(TLRPC.TL_messages_searchStickerSets req, TLObject response, TLRPC.TL_error error) {
                            if (response instanceof TLRPC.TL_messages_foundStickerSets) {
                                AndroidUtilities.runOnUIThread(
                                /*  JADX ERROR: Method code generation error
                                    jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0009: INVOKE  
                                      (wrap: im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$StickersSearchGridAdapter$1$OOckfyULYZHgfMNHbHzO42A3pQM : 0x0006: CONSTRUCTOR  (r0v1 im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$StickersSearchGridAdapter$1$OOckfyULYZHgfMNHbHzO42A3pQM) = 
                                      (r1v0 'this' im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter$1 A[THIS])
                                      (r2v0 'req' im.bclpbkiauv.tgnet.TLRPC$TL_messages_searchStickerSets)
                                      (r3v0 'response' im.bclpbkiauv.tgnet.TLObject)
                                     call: im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$StickersSearchGridAdapter$1$OOckfyULYZHgfMNHbHzO42A3pQM.<init>(im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter$1, im.bclpbkiauv.tgnet.TLRPC$TL_messages_searchStickerSets, im.bclpbkiauv.tgnet.TLObject):void type: CONSTRUCTOR)
                                     im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable):void type: STATIC in method: im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.1.lambda$run$1$EmojiViewV2$StickersSearchGridAdapter$1(im.bclpbkiauv.tgnet.TLRPC$TL_messages_searchStickerSets, im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.TLRPC$TL_error):void, dex: classes6.dex
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                    	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                    	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                    	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                    	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                    	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                    	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                    	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                    	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                    	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                                    	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                    	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:98)
                                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:480)
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                    	at jadx.core.codegen.ClassGen.addInsnBody(ClassGen.java:437)
                                    	at jadx.core.codegen.ClassGen.addField(ClassGen.java:378)
                                    	at jadx.core.codegen.ClassGen.addFields(ClassGen.java:348)
                                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:226)
                                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                    	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                    	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                    	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                                    	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                                    	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                                    	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                                    	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                                    	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                                    Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0006: CONSTRUCTOR  (r0v1 im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$StickersSearchGridAdapter$1$OOckfyULYZHgfMNHbHzO42A3pQM) = 
                                      (r1v0 'this' im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter$1 A[THIS])
                                      (r2v0 'req' im.bclpbkiauv.tgnet.TLRPC$TL_messages_searchStickerSets)
                                      (r3v0 'response' im.bclpbkiauv.tgnet.TLObject)
                                     call: im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$StickersSearchGridAdapter$1$OOckfyULYZHgfMNHbHzO42A3pQM.<init>(im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter$1, im.bclpbkiauv.tgnet.TLRPC$TL_messages_searchStickerSets, im.bclpbkiauv.tgnet.TLObject):void type: CONSTRUCTOR in method: im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.1.lambda$run$1$EmojiViewV2$StickersSearchGridAdapter$1(im.bclpbkiauv.tgnet.TLRPC$TL_messages_searchStickerSets, im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.TLRPC$TL_error):void, dex: classes6.dex
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                    	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                    	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                    	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                    	... 64 more
                                    Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$StickersSearchGridAdapter$1$OOckfyULYZHgfMNHbHzO42A3pQM, state: NOT_LOADED
                                    	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                                    	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                    	... 70 more
                                    */
                                /*
                                    this = this;
                                    boolean r0 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messages_foundStickerSets
                                    if (r0 == 0) goto L_0x000c
                                    im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$StickersSearchGridAdapter$1$OOckfyULYZHgfMNHbHzO42A3pQM r0 = new im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$StickersSearchGridAdapter$1$OOckfyULYZHgfMNHbHzO42A3pQM
                                    r0.<init>(r1, r2, r3)
                                    im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r0)
                                L_0x000c:
                                    return
                                */
                                throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.AnonymousClass1.lambda$run$1$EmojiViewV2$StickersSearchGridAdapter$1(im.bclpbkiauv.tgnet.TLRPC$TL_messages_searchStickerSets, im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.TLRPC$TL_error):void");
                            }

                            public /* synthetic */ void lambda$null$0$EmojiViewV2$StickersSearchGridAdapter$1(TLRPC.TL_messages_searchStickerSets req, TLObject response) {
                                if (req.q.equals(StickersSearchGridAdapter.this.searchQuery)) {
                                    clear();
                                    EmojiViewV2.this.stickersSearchField.progressDrawable.stopAnimation();
                                    int unused = StickersSearchGridAdapter.this.reqId = 0;
                                    if (EmojiViewV2.this.stickersGridView.getAdapter() != EmojiViewV2.this.stickersSearchGridAdapter) {
                                        EmojiViewV2.this.stickersGridView.setAdapter(EmojiViewV2.this.stickersSearchGridAdapter);
                                    }
                                    StickersSearchGridAdapter.this.serverPacks.addAll(((TLRPC.TL_messages_foundStickerSets) response).sets);
                                    StickersSearchGridAdapter.this.notifyDataSetChanged();
                                }
                            }

                            public /* synthetic */ void lambda$run$3$EmojiViewV2$StickersSearchGridAdapter$1(TLRPC.TL_messages_getStickers req2, ArrayList emojiStickersArray, LongSparseArray emojiStickersMap, TLObject response, TLRPC.TL_error error) {
                                AndroidUtilities.runOnUIThread(
                                /*  JADX ERROR: Method code generation error
                                    jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x000b: INVOKE  
                                      (wrap: im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$StickersSearchGridAdapter$1$PEjkUhWfZJWw8LGHD5bwlUDY9SE : 0x0008: CONSTRUCTOR  (r0v0 im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$StickersSearchGridAdapter$1$PEjkUhWfZJWw8LGHD5bwlUDY9SE) = 
                                      (r7v0 'this' im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter$1 A[THIS])
                                      (r8v0 'req2' im.bclpbkiauv.tgnet.TLRPC$TL_messages_getStickers)
                                      (r11v0 'response' im.bclpbkiauv.tgnet.TLObject)
                                      (r9v0 'emojiStickersArray' java.util.ArrayList)
                                      (r10v0 'emojiStickersMap' android.util.LongSparseArray)
                                     call: im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$StickersSearchGridAdapter$1$PEjkUhWfZJWw8LGHD5bwlUDY9SE.<init>(im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter$1, im.bclpbkiauv.tgnet.TLRPC$TL_messages_getStickers, im.bclpbkiauv.tgnet.TLObject, java.util.ArrayList, android.util.LongSparseArray):void type: CONSTRUCTOR)
                                     im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable):void type: STATIC in method: im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.1.lambda$run$3$EmojiViewV2$StickersSearchGridAdapter$1(im.bclpbkiauv.tgnet.TLRPC$TL_messages_getStickers, java.util.ArrayList, android.util.LongSparseArray, im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.TLRPC$TL_error):void, dex: classes6.dex
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                    	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                    	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                    	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                    	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                    	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                    	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                    	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                                    	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                    	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:98)
                                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:480)
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                    	at jadx.core.codegen.ClassGen.addInsnBody(ClassGen.java:437)
                                    	at jadx.core.codegen.ClassGen.addField(ClassGen.java:378)
                                    	at jadx.core.codegen.ClassGen.addFields(ClassGen.java:348)
                                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:226)
                                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                    	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                    	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                    	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                                    	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                                    	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                                    	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                                    	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                                    	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                                    Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0008: CONSTRUCTOR  (r0v0 im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$StickersSearchGridAdapter$1$PEjkUhWfZJWw8LGHD5bwlUDY9SE) = 
                                      (r7v0 'this' im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter$1 A[THIS])
                                      (r8v0 'req2' im.bclpbkiauv.tgnet.TLRPC$TL_messages_getStickers)
                                      (r11v0 'response' im.bclpbkiauv.tgnet.TLObject)
                                      (r9v0 'emojiStickersArray' java.util.ArrayList)
                                      (r10v0 'emojiStickersMap' android.util.LongSparseArray)
                                     call: im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$StickersSearchGridAdapter$1$PEjkUhWfZJWw8LGHD5bwlUDY9SE.<init>(im.bclpbkiauv.ui.components.EmojiViewV2$StickersSearchGridAdapter$1, im.bclpbkiauv.tgnet.TLRPC$TL_messages_getStickers, im.bclpbkiauv.tgnet.TLObject, java.util.ArrayList, android.util.LongSparseArray):void type: CONSTRUCTOR in method: im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.1.lambda$run$3$EmojiViewV2$StickersSearchGridAdapter$1(im.bclpbkiauv.tgnet.TLRPC$TL_messages_getStickers, java.util.ArrayList, android.util.LongSparseArray, im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.TLRPC$TL_error):void, dex: classes6.dex
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                    	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                    	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                    	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                    	... 57 more
                                    Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$StickersSearchGridAdapter$1$PEjkUhWfZJWw8LGHD5bwlUDY9SE, state: NOT_LOADED
                                    	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                                    	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                    	... 63 more
                                    */
                                /*
                                    this = this;
                                    im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$StickersSearchGridAdapter$1$PEjkUhWfZJWw8LGHD5bwlUDY9SE r6 = new im.bclpbkiauv.ui.components.-$$Lambda$EmojiViewV2$StickersSearchGridAdapter$1$PEjkUhWfZJWw8LGHD5bwlUDY9SE
                                    r0 = r6
                                    r1 = r7
                                    r2 = r8
                                    r3 = r11
                                    r4 = r9
                                    r5 = r10
                                    r0.<init>(r1, r2, r3, r4, r5)
                                    im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r6)
                                    return
                                */
                                throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.AnonymousClass1.lambda$run$3$EmojiViewV2$StickersSearchGridAdapter$1(im.bclpbkiauv.tgnet.TLRPC$TL_messages_getStickers, java.util.ArrayList, android.util.LongSparseArray, im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.TLRPC$TL_error):void");
                            }

                            public /* synthetic */ void lambda$null$2$EmojiViewV2$StickersSearchGridAdapter$1(TLRPC.TL_messages_getStickers req2, TLObject response, ArrayList emojiStickersArray, LongSparseArray emojiStickersMap) {
                                if (req2.emoticon.equals(StickersSearchGridAdapter.this.searchQuery)) {
                                    int unused = StickersSearchGridAdapter.this.reqId2 = 0;
                                    if (response instanceof TLRPC.TL_messages_stickers) {
                                        TLRPC.TL_messages_stickers res = (TLRPC.TL_messages_stickers) response;
                                        int oldCount = emojiStickersArray.size();
                                        int size = res.stickers.size();
                                        for (int a = 0; a < size; a++) {
                                            TLRPC.Document document = res.stickers.get(a);
                                            if (emojiStickersMap.indexOfKey(document.id) < 0) {
                                                emojiStickersArray.add(document);
                                            }
                                        }
                                        if (oldCount != emojiStickersArray.size()) {
                                            StickersSearchGridAdapter.this.emojiStickers.put(emojiStickersArray, StickersSearchGridAdapter.this.searchQuery);
                                            if (oldCount == 0) {
                                                StickersSearchGridAdapter.this.emojiArrays.add(emojiStickersArray);
                                            }
                                            StickersSearchGridAdapter.this.notifyDataSetChanged();
                                        }
                                    }
                                }
                            }
                        };
                        /* access modifiers changed from: private */
                        public ArrayList<TLRPC.StickerSetCovered> serverPacks = new ArrayList<>();
                        /* access modifiers changed from: private */
                        public int totalItems;

                        static /* synthetic */ int access$13304(StickersSearchGridAdapter x0) {
                            int i = x0.emojiSearchId + 1;
                            x0.emojiSearchId = i;
                            return i;
                        }

                        public StickersSearchGridAdapter(Context context2) {
                            this.context = context2;
                        }

                        public boolean isEnabled(RecyclerView.ViewHolder holder) {
                            return false;
                        }

                        public int getItemCount() {
                            int i = this.totalItems;
                            if (i != 1) {
                                return i + 1;
                            }
                            return 2;
                        }

                        public Object getItem(int i) {
                            return this.cache.get(i);
                        }

                        public void search(String text) {
                            if (this.reqId != 0) {
                                ConnectionsManager.getInstance(EmojiViewV2.this.currentAccount).cancelRequest(this.reqId, true);
                                this.reqId = 0;
                            }
                            if (this.reqId2 != 0) {
                                ConnectionsManager.getInstance(EmojiViewV2.this.currentAccount).cancelRequest(this.reqId2, true);
                                this.reqId2 = 0;
                            }
                            if (TextUtils.isEmpty(text)) {
                                this.searchQuery = null;
                                this.localPacks.clear();
                                this.emojiStickers.clear();
                                this.serverPacks.clear();
                                if (EmojiViewV2.this.stickersGridView.getAdapter() != EmojiViewV2.this.stickersGridAdapter) {
                                    EmojiViewV2.this.stickersGridView.setAdapter(EmojiViewV2.this.stickersGridAdapter);
                                }
                                notifyDataSetChanged();
                            } else {
                                this.searchQuery = text.toLowerCase();
                            }
                            AndroidUtilities.cancelRunOnUIThread(this.searchRunnable);
                            AndroidUtilities.runOnUIThread(this.searchRunnable, 300);
                        }

                        public int getItemViewType(int position) {
                            if (position == 0) {
                                return 4;
                            }
                            if (position == 1 && this.totalItems == 1) {
                                return 5;
                            }
                            Object object = this.cache.get(position);
                            if (object == null) {
                                return 1;
                            }
                            if (object instanceof TLRPC.Document) {
                                return 0;
                            }
                            if (object instanceof TLRPC.StickerSetCovered) {
                                return 3;
                            }
                            return 2;
                        }

                        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                            int i = viewType;
                            View view = null;
                            if (i == 0) {
                                view = new StickerEmojiCell(this.context) {
                                    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                                        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), 1073741824));
                                    }
                                };
                            } else if (i == 1) {
                                view = new EmptyCell(this.context);
                            } else if (i == 2) {
                                view = new StickerSetNameCell(this.context, false);
                            } else if (i == 3) {
                                view = new FeaturedStickerSetInfoCell(this.context, 17);
                                ((FeaturedStickerSetInfoCell) view).setAddOnClickListener(new View.OnClickListener() {
                                    public final void onClick(View view) {
                                        EmojiViewV2.StickersSearchGridAdapter.this.lambda$onCreateViewHolder$0$EmojiViewV2$StickersSearchGridAdapter(view);
                                    }
                                });
                            } else if (i == 4) {
                                view = new View(this.context);
                                view.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiViewV2.this.searchFieldHeight));
                            } else if (i == 5) {
                                AnonymousClass3 r4 = new FrameLayout(this.context) {
                                    /* access modifiers changed from: protected */
                                    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                                        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec((int) (((float) (((EmojiViewV2.this.stickersGridView.getMeasuredHeight() - EmojiViewV2.this.searchFieldHeight) - AndroidUtilities.dp(8.0f)) / 3)) * 1.7f), 1073741824));
                                    }
                                };
                                ImageView imageView = new ImageView(this.context);
                                imageView.setScaleType(ImageView.ScaleType.CENTER);
                                imageView.setImageResource(R.drawable.stickers_empty);
                                imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_emojiPanelEmptyText), PorterDuff.Mode.MULTIPLY));
                                r4.addView(imageView, LayoutHelper.createFrame(-2.0f, -2.0f, 17, 0.0f, 0.0f, 0.0f, 59.0f));
                                TextView textView = new TextView(this.context);
                                textView.setText(LocaleController.getString("NoStickersFound", R.string.NoStickersFound));
                                textView.setTextSize(1, 16.0f);
                                textView.setTextColor(Theme.getColor(Theme.key_chat_emojiPanelEmptyText));
                                r4.addView(textView, LayoutHelper.createFrame(-2.0f, -2.0f, 17, 0.0f, 0.0f, 0.0f, 9.0f));
                                view = r4;
                                view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                            }
                            return new RecyclerListView.Holder(view);
                        }

                        public /* synthetic */ void lambda$onCreateViewHolder$0$EmojiViewV2$StickersSearchGridAdapter(View v) {
                            FeaturedStickerSetInfoCell parent1 = (FeaturedStickerSetInfoCell) v.getParent();
                            TLRPC.StickerSetCovered pack = parent1.getStickerSet();
                            if (EmojiViewV2.this.installingStickerSets.indexOfKey(pack.set.id) < 0 && EmojiViewV2.this.removingStickerSets.indexOfKey(pack.set.id) < 0) {
                                if (parent1.isInstalled()) {
                                    EmojiViewV2.this.removingStickerSets.put(pack.set.id, pack);
                                    EmojiViewV2.this.delegate.onStickerSetRemove(parent1.getStickerSet());
                                } else {
                                    EmojiViewV2.this.installingStickerSets.put(pack.set.id, pack);
                                    EmojiViewV2.this.delegate.onStickerSetAdd(parent1.getStickerSet());
                                }
                                parent1.setDrawProgress(true);
                            }
                        }

                        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                            Integer count;
                            int itemViewType = holder.getItemViewType();
                            boolean z = true;
                            if (itemViewType == 0) {
                                TLRPC.Document sticker = (TLRPC.Document) this.cache.get(position);
                                StickerEmojiCell cell = (StickerEmojiCell) holder.itemView;
                                cell.setSticker(sticker, this.cacheParent.get(position), this.positionToEmoji.get(position), false);
                                if (!EmojiViewV2.this.recentStickers.contains(sticker) && !EmojiViewV2.this.favouriteStickers.contains(sticker)) {
                                    z = false;
                                }
                                cell.setRecent(z);
                            } else if (itemViewType == 1) {
                                EmptyCell cell2 = (EmptyCell) holder.itemView;
                                if (position == this.totalItems) {
                                    int row = this.positionToRow.get(position - 1, Integer.MIN_VALUE);
                                    if (row == Integer.MIN_VALUE) {
                                        cell2.setHeight(1);
                                        return;
                                    }
                                    Object pack = this.rowStartPack.get(row);
                                    if (pack instanceof TLRPC.TL_messages_stickerSet) {
                                        count = Integer.valueOf(((TLRPC.TL_messages_stickerSet) pack).documents.size());
                                    } else if (pack instanceof Integer) {
                                        count = (Integer) pack;
                                    } else {
                                        count = null;
                                    }
                                    if (count == null) {
                                        cell2.setHeight(1);
                                    } else if (count.intValue() == 0) {
                                        cell2.setHeight(AndroidUtilities.dp(8.0f));
                                    } else {
                                        int height = EmojiViewV2.this.pager.getHeight() - (((int) Math.ceil((double) (((float) count.intValue()) / ((float) EmojiViewV2.this.stickersGridAdapter.stickersPerRow)))) * AndroidUtilities.dp(82.0f));
                                        if (height > 0) {
                                            z = height;
                                        }
                                        cell2.setHeight(z ? 1 : 0);
                                    }
                                } else {
                                    cell2.setHeight(AndroidUtilities.dp(82.0f));
                                }
                            } else if (itemViewType == 2) {
                                StickerSetNameCell cell3 = (StickerSetNameCell) holder.itemView;
                                Object object = this.cache.get(position);
                                if (object instanceof TLRPC.TL_messages_stickerSet) {
                                    TLRPC.TL_messages_stickerSet set = (TLRPC.TL_messages_stickerSet) object;
                                    if (TextUtils.isEmpty(this.searchQuery) || !this.localPacksByShortName.containsKey(set)) {
                                        Integer start = this.localPacksByName.get(set);
                                        if (!(set.set == null || start == null)) {
                                            cell3.setText(set.set.title, 0, start.intValue(), !TextUtils.isEmpty(this.searchQuery) ? this.searchQuery.length() : 0);
                                        }
                                        cell3.setUrl((CharSequence) null, 0);
                                        return;
                                    }
                                    if (set.set != null) {
                                        cell3.setText(set.set.title, 0);
                                    }
                                    cell3.setUrl(set.set.short_name, this.searchQuery.length());
                                }
                            } else if (itemViewType == 3) {
                                TLRPC.StickerSetCovered stickerSetCovered = (TLRPC.StickerSetCovered) this.cache.get(position);
                                FeaturedStickerSetInfoCell cell4 = (FeaturedStickerSetInfoCell) holder.itemView;
                                boolean installing = EmojiViewV2.this.installingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0;
                                boolean removing = EmojiViewV2.this.removingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0;
                                if (installing || removing) {
                                    if (installing && cell4.isInstalled()) {
                                        EmojiViewV2.this.installingStickerSets.remove(stickerSetCovered.set.id);
                                        installing = false;
                                    } else if (removing && !cell4.isInstalled()) {
                                        EmojiViewV2.this.removingStickerSets.remove(stickerSetCovered.set.id);
                                        removing = false;
                                    }
                                }
                                if (!installing && !removing) {
                                    z = false;
                                }
                                cell4.setDrawProgress(z);
                                int idx = TextUtils.isEmpty(this.searchQuery) ? -1 : AndroidUtilities.indexOfIgnoreCase(stickerSetCovered.set.title, this.searchQuery);
                                if (idx >= 0) {
                                    cell4.setStickerSet(stickerSetCovered, false, idx, this.searchQuery.length());
                                    return;
                                }
                                cell4.setStickerSet(stickerSetCovered, false);
                                if (!TextUtils.isEmpty(this.searchQuery) && AndroidUtilities.indexOfIgnoreCase(stickerSetCovered.set.short_name, this.searchQuery) == 0) {
                                    cell4.setUrl(stickerSetCovered.set.short_name, this.searchQuery.length());
                                }
                            }
                        }

                        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v3, resolved type: im.bclpbkiauv.tgnet.TLRPC$StickerSetCovered} */
                        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v4, resolved type: im.bclpbkiauv.tgnet.TLRPC$StickerSetCovered} */
                        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v16, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messages_stickerSet} */
                        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v5, resolved type: im.bclpbkiauv.tgnet.TLRPC$StickerSetCovered} */
                        /* JADX WARNING: Multi-variable type inference failed */
                        /* Code decompiled incorrectly, please refer to instructions dump. */
                        public void notifyDataSetChanged() {
                            /*
                                r22 = this;
                                r0 = r22
                                android.util.SparseArray<java.lang.Object> r1 = r0.rowStartPack
                                r1.clear()
                                android.util.SparseIntArray r1 = r0.positionToRow
                                r1.clear()
                                android.util.SparseArray<java.lang.Object> r1 = r0.cache
                                r1.clear()
                                android.util.SparseArray<im.bclpbkiauv.tgnet.TLRPC$StickerSetCovered> r1 = r0.positionsToSets
                                r1.clear()
                                android.util.SparseArray<java.lang.String> r1 = r0.positionToEmoji
                                r1.clear()
                                r1 = 0
                                r0.totalItems = r1
                                r1 = 0
                                r2 = -1
                                java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$StickerSetCovered> r3 = r0.serverPacks
                                int r3 = r3.size()
                                java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$TL_messages_stickerSet> r4 = r0.localPacks
                                int r4 = r4.size()
                                java.util.ArrayList<java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Document>> r5 = r0.emojiArrays
                                boolean r5 = r5.isEmpty()
                                r5 = r5 ^ 1
                            L_0x0034:
                                int r6 = r3 + r4
                                int r6 = r6 + r5
                                if (r2 >= r6) goto L_0x0204
                                r6 = 0
                                r7 = -1
                                if (r2 != r7) goto L_0x0050
                                android.util.SparseArray<java.lang.Object> r7 = r0.cache
                                int r8 = r0.totalItems
                                int r9 = r8 + 1
                                r0.totalItems = r9
                                java.lang.String r9 = "search"
                                r7.put(r8, r9)
                                int r1 = r1 + 1
                                r16 = r3
                                goto L_0x01fe
                            L_0x0050:
                                r7 = r2
                                if (r7 >= r4) goto L_0x0062
                                java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$TL_messages_stickerSet> r8 = r0.localPacks
                                java.lang.Object r8 = r8.get(r7)
                                im.bclpbkiauv.tgnet.TLRPC$TL_messages_stickerSet r8 = (im.bclpbkiauv.tgnet.TLRPC.TL_messages_stickerSet) r8
                                java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Document> r9 = r8.documents
                                r6 = r8
                                r16 = r3
                                goto L_0x0159
                            L_0x0062:
                                int r7 = r7 - r4
                                if (r7 >= r5) goto L_0x014b
                                r8 = 0
                                java.lang.String r9 = ""
                                r10 = 0
                                java.util.ArrayList<java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Document>> r11 = r0.emojiArrays
                                int r11 = r11.size()
                            L_0x006f:
                                if (r10 >= r11) goto L_0x010e
                                java.util.ArrayList<java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Document>> r12 = r0.emojiArrays
                                java.lang.Object r12 = r12.get(r10)
                                java.util.ArrayList r12 = (java.util.ArrayList) r12
                                java.util.HashMap<java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Document>, java.lang.String> r13 = r0.emojiStickers
                                java.lang.Object r13 = r13.get(r12)
                                java.lang.String r13 = (java.lang.String) r13
                                if (r13 == 0) goto L_0x0092
                                boolean r14 = r9.equals(r13)
                                if (r14 != 0) goto L_0x0092
                                r9 = r13
                                android.util.SparseArray<java.lang.String> r14 = r0.positionToEmoji
                                int r15 = r0.totalItems
                                int r15 = r15 + r8
                                r14.put(r15, r9)
                            L_0x0092:
                                r14 = 0
                                int r15 = r12.size()
                            L_0x0097:
                                if (r14 >= r15) goto L_0x00fe
                                r16 = r3
                                int r3 = r0.totalItems
                                int r3 = r3 + r8
                                r17 = r9
                                im.bclpbkiauv.ui.components.EmojiViewV2 r9 = im.bclpbkiauv.ui.components.EmojiViewV2.this
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersGridAdapter r9 = r9.stickersGridAdapter
                                int r9 = r9.stickersPerRow
                                int r9 = r8 / r9
                                int r9 = r9 + r1
                                java.lang.Object r18 = r12.get(r14)
                                r19 = r11
                                r11 = r18
                                im.bclpbkiauv.tgnet.TLRPC$Document r11 = (im.bclpbkiauv.tgnet.TLRPC.Document) r11
                                r18 = r12
                                android.util.SparseArray<java.lang.Object> r12 = r0.cache
                                r12.put(r3, r11)
                                im.bclpbkiauv.ui.components.EmojiViewV2 r12 = im.bclpbkiauv.ui.components.EmojiViewV2.this
                                int r12 = r12.currentAccount
                                im.bclpbkiauv.messenger.MediaDataController r12 = im.bclpbkiauv.messenger.MediaDataController.getInstance(r12)
                                r20 = r13
                                r21 = r14
                                long r13 = im.bclpbkiauv.messenger.MediaDataController.getStickerSetId(r11)
                                im.bclpbkiauv.tgnet.TLRPC$TL_messages_stickerSet r12 = r12.getStickerSetById(r13)
                                if (r12 == 0) goto L_0x00db
                                android.util.SparseArray<java.lang.Object> r13 = r0.cacheParent
                                r13.put(r3, r12)
                            L_0x00db:
                                android.util.SparseIntArray r13 = r0.positionToRow
                                r13.put(r3, r9)
                                if (r2 < r4) goto L_0x00ee
                                boolean r13 = r6 instanceof im.bclpbkiauv.tgnet.TLRPC.StickerSetCovered
                                if (r13 == 0) goto L_0x00ee
                                android.util.SparseArray<im.bclpbkiauv.tgnet.TLRPC$StickerSetCovered> r13 = r0.positionsToSets
                                r14 = r6
                                im.bclpbkiauv.tgnet.TLRPC$StickerSetCovered r14 = (im.bclpbkiauv.tgnet.TLRPC.StickerSetCovered) r14
                                r13.put(r3, r14)
                            L_0x00ee:
                                int r8 = r8 + 1
                                int r14 = r21 + 1
                                r3 = r16
                                r9 = r17
                                r12 = r18
                                r11 = r19
                                r13 = r20
                                goto L_0x0097
                            L_0x00fe:
                                r16 = r3
                                r17 = r9
                                r19 = r11
                                r18 = r12
                                r20 = r13
                                r21 = r14
                                int r10 = r10 + 1
                                goto L_0x006f
                            L_0x010e:
                                r16 = r3
                                r19 = r11
                                float r3 = (float) r8
                                im.bclpbkiauv.ui.components.EmojiViewV2 r10 = im.bclpbkiauv.ui.components.EmojiViewV2.this
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersGridAdapter r10 = r10.stickersGridAdapter
                                int r10 = r10.stickersPerRow
                                float r10 = (float) r10
                                float r3 = r3 / r10
                                double r10 = (double) r3
                                double r10 = java.lang.Math.ceil(r10)
                                int r3 = (int) r10
                                r10 = 0
                                r11 = r3
                            L_0x0127:
                                if (r10 >= r11) goto L_0x0137
                                android.util.SparseArray<java.lang.Object> r12 = r0.rowStartPack
                                int r13 = r1 + r10
                                java.lang.Integer r14 = java.lang.Integer.valueOf(r8)
                                r12.put(r13, r14)
                                int r10 = r10 + 1
                                goto L_0x0127
                            L_0x0137:
                                int r10 = r0.totalItems
                                im.bclpbkiauv.ui.components.EmojiViewV2 r11 = im.bclpbkiauv.ui.components.EmojiViewV2.this
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersGridAdapter r11 = r11.stickersGridAdapter
                                int r11 = r11.stickersPerRow
                                int r11 = r11 * r3
                                int r10 = r10 + r11
                                r0.totalItems = r10
                                int r1 = r1 + r3
                                goto L_0x01fe
                            L_0x014b:
                                r16 = r3
                                int r7 = r7 - r5
                                java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$StickerSetCovered> r3 = r0.serverPacks
                                java.lang.Object r3 = r3.get(r7)
                                im.bclpbkiauv.tgnet.TLRPC$StickerSetCovered r3 = (im.bclpbkiauv.tgnet.TLRPC.StickerSetCovered) r3
                                java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Document> r9 = r3.covers
                                r6 = r3
                            L_0x0159:
                                boolean r3 = r9.isEmpty()
                                if (r3 == 0) goto L_0x0161
                                goto L_0x01fe
                            L_0x0161:
                                int r3 = r9.size()
                                float r3 = (float) r3
                                im.bclpbkiauv.ui.components.EmojiViewV2 r7 = im.bclpbkiauv.ui.components.EmojiViewV2.this
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersGridAdapter r7 = r7.stickersGridAdapter
                                int r7 = r7.stickersPerRow
                                float r7 = (float) r7
                                float r3 = r3 / r7
                                double r7 = (double) r3
                                double r7 = java.lang.Math.ceil(r7)
                                int r3 = (int) r7
                                android.util.SparseArray<java.lang.Object> r7 = r0.cache
                                int r8 = r0.totalItems
                                r7.put(r8, r6)
                                if (r2 < r4) goto L_0x018f
                                boolean r7 = r6 instanceof im.bclpbkiauv.tgnet.TLRPC.StickerSetCovered
                                if (r7 == 0) goto L_0x018f
                                android.util.SparseArray<im.bclpbkiauv.tgnet.TLRPC$StickerSetCovered> r7 = r0.positionsToSets
                                int r8 = r0.totalItems
                                r10 = r6
                                im.bclpbkiauv.tgnet.TLRPC$StickerSetCovered r10 = (im.bclpbkiauv.tgnet.TLRPC.StickerSetCovered) r10
                                r7.put(r8, r10)
                            L_0x018f:
                                android.util.SparseIntArray r7 = r0.positionToRow
                                int r8 = r0.totalItems
                                r7.put(r8, r1)
                                r7 = 0
                                int r8 = r9.size()
                            L_0x019b:
                                if (r7 >= r8) goto L_0x01d9
                                int r10 = r7 + 1
                                int r11 = r0.totalItems
                                int r10 = r10 + r11
                                int r11 = r1 + 1
                                im.bclpbkiauv.ui.components.EmojiViewV2 r12 = im.bclpbkiauv.ui.components.EmojiViewV2.this
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersGridAdapter r12 = r12.stickersGridAdapter
                                int r12 = r12.stickersPerRow
                                int r12 = r7 / r12
                                int r11 = r11 + r12
                                java.lang.Object r12 = r9.get(r7)
                                im.bclpbkiauv.tgnet.TLRPC$Document r12 = (im.bclpbkiauv.tgnet.TLRPC.Document) r12
                                android.util.SparseArray<java.lang.Object> r13 = r0.cache
                                r13.put(r10, r12)
                                if (r6 == 0) goto L_0x01c3
                                android.util.SparseArray<java.lang.Object> r13 = r0.cacheParent
                                r13.put(r10, r6)
                            L_0x01c3:
                                android.util.SparseIntArray r13 = r0.positionToRow
                                r13.put(r10, r11)
                                if (r2 < r4) goto L_0x01d6
                                boolean r13 = r6 instanceof im.bclpbkiauv.tgnet.TLRPC.StickerSetCovered
                                if (r13 == 0) goto L_0x01d6
                                android.util.SparseArray<im.bclpbkiauv.tgnet.TLRPC$StickerSetCovered> r13 = r0.positionsToSets
                                r14 = r6
                                im.bclpbkiauv.tgnet.TLRPC$StickerSetCovered r14 = (im.bclpbkiauv.tgnet.TLRPC.StickerSetCovered) r14
                                r13.put(r10, r14)
                            L_0x01d6:
                                int r7 = r7 + 1
                                goto L_0x019b
                            L_0x01d9:
                                r7 = 0
                                int r8 = r3 + 1
                            L_0x01dc:
                                if (r7 >= r8) goto L_0x01e8
                                android.util.SparseArray<java.lang.Object> r10 = r0.rowStartPack
                                int r11 = r1 + r7
                                r10.put(r11, r6)
                                int r7 = r7 + 1
                                goto L_0x01dc
                            L_0x01e8:
                                int r7 = r0.totalItems
                                im.bclpbkiauv.ui.components.EmojiViewV2 r8 = im.bclpbkiauv.ui.components.EmojiViewV2.this
                                im.bclpbkiauv.ui.components.EmojiViewV2$StickersGridAdapter r8 = r8.stickersGridAdapter
                                int r8 = r8.stickersPerRow
                                int r8 = r8 * r3
                                int r8 = r8 + 1
                                int r7 = r7 + r8
                                r0.totalItems = r7
                                int r7 = r3 + 1
                                int r1 = r1 + r7
                            L_0x01fe:
                                int r2 = r2 + 1
                                r3 = r16
                                goto L_0x0034
                            L_0x0204:
                                super.notifyDataSetChanged()
                                return
                            */
                            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.EmojiViewV2.StickersSearchGridAdapter.notifyDataSetChanged():void");
                        }
                    }
                }
