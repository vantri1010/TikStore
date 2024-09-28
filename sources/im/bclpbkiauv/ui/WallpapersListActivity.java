package im.bclpbkiauv.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.LongSparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.internal.view.SupportMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SendMessagesHelper;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.DialogsActivity;
import im.bclpbkiauv.ui.WallpaperActivity;
import im.bclpbkiauv.ui.WallpapersListActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenu;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.GraySectionCell;
import im.bclpbkiauv.ui.cells.ShadowSectionCell;
import im.bclpbkiauv.ui.cells.TextCell;
import im.bclpbkiauv.ui.cells.TextInfoPrivacyCell;
import im.bclpbkiauv.ui.cells.WallpaperCell;
import im.bclpbkiauv.ui.components.EmptyTextProgressView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.NumberTextView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.WallpaperUpdater;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class WallpapersListActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    public static final int TYPE_ALL = 0;
    public static final int TYPE_COLOR = 1;
    private static final int[] defaultColors = {-1, -2826262, -4993567, -9783318, -16740912, -2891046, -3610935, -3808859, -10375058, -3289169, -5789547, -8622222, -10322, -18835, -2193583, -1059360, -2383431, -20561, -955808, -1524502, -6974739, -2507680, -5145015, -2765065, -2142101, -7613748, -12811138, -14524116, -14398084, -12764283, -10129027, -15195603, -16777216};
    private static final int delete = 4;
    private static final int forward = 3;
    /* access modifiers changed from: private */
    public static final int[] searchColors = {-16746753, SupportMenu.CATEGORY_MASK, -30208, -13824, -16718798, -14702165, -9240406, -409915, -9224159, -16777216, -10725281, -1};
    /* access modifiers changed from: private */
    public static final String[] searchColorsNames = {"Blue", "Red", "Orange", "Yellow", "Green", "Teal", "Purple", "Pink", "Brown", "Black", "Gray", "White"};
    /* access modifiers changed from: private */
    public static final int[] searchColorsNamesR = {R.string.Blue, R.string.Red, R.string.Orange, R.string.Yellow, R.string.Green, R.string.Teal, R.string.Purple, R.string.Pink, R.string.Brown, R.string.Black, R.string.Gray, R.string.White};
    private ArrayList<View> actionModeViews = new ArrayList<>();
    private ColorWallpaper addedColorWallpaper;
    private FileWallpaper addedFileWallpaper;
    private ArrayList<Object> allWallPapers = new ArrayList<>();
    private LongSparseArray<Object> allWallPapersDict = new LongSparseArray<>();
    private FileWallpaper catsWallpaper;
    /* access modifiers changed from: private */
    public Paint colorFramePaint;
    /* access modifiers changed from: private */
    public Paint colorPaint;
    /* access modifiers changed from: private */
    public int columnsCount = 3;
    /* access modifiers changed from: private */
    public int currentType;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private boolean loadingWallpapers;
    private ArrayList<Object> patterns = new ArrayList<>();
    /* access modifiers changed from: private */
    public AlertDialog progressDialog;
    /* access modifiers changed from: private */
    public int resetInfoRow;
    /* access modifiers changed from: private */
    public int resetRow;
    /* access modifiers changed from: private */
    public int resetSectionRow;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public boolean scrolling;
    /* access modifiers changed from: private */
    public SearchAdapter searchAdapter;
    /* access modifiers changed from: private */
    public EmptyTextProgressView searchEmptyView;
    /* access modifiers changed from: private */
    public ActionBarMenuItem searchItem;
    /* access modifiers changed from: private */
    public int sectionRow;
    /* access modifiers changed from: private */
    public long selectedBackground;
    private boolean selectedBackgroundBlurred;
    private boolean selectedBackgroundMotion;
    private int selectedColor;
    private float selectedIntensity;
    private NumberTextView selectedMessagesCountTextView;
    private long selectedPattern;
    /* access modifiers changed from: private */
    public LongSparseArray<Object> selectedWallPapers = new LongSparseArray<>();
    /* access modifiers changed from: private */
    public int setColorRow;
    private FileWallpaper themeWallpaper;
    /* access modifiers changed from: private */
    public int totalWallpaperRows;
    private WallpaperUpdater updater;
    /* access modifiers changed from: private */
    public int uploadImageRow;
    /* access modifiers changed from: private */
    public int wallPaperStartRow;
    /* access modifiers changed from: private */
    public ArrayList<Object> wallPapers = new ArrayList<>();

    public static class ColorWallpaper {
        public int color;
        public long id;
        public float intensity;
        public boolean motion;
        public File path;
        public TLRPC.TL_wallPaper pattern;
        public long patternId;

        public ColorWallpaper(long i, int c) {
            this.id = i;
            this.color = -16777216 | c;
            this.intensity = 1.0f;
        }

        public ColorWallpaper(long i, int c, long p, float in, boolean m, File ph) {
            this.id = i;
            this.color = -16777216 | c;
            this.patternId = p;
            this.intensity = in;
            this.path = ph;
            this.motion = m;
        }
    }

    public static class FileWallpaper {
        public long id;
        public File originalPath;
        public File path;
        public int resId;
        public int thumbResId;

        public FileWallpaper(long i, File f, File of) {
            this.id = i;
            this.path = f;
            this.originalPath = of;
        }

        public FileWallpaper(long i, String f) {
            this.id = i;
            this.path = new File(f);
        }

        public FileWallpaper(long i, int r, int t) {
            this.id = i;
            this.resId = r;
            this.thumbResId = t;
        }
    }

    public WallpapersListActivity(int type) {
        this.currentType = type;
    }

    public boolean onFragmentCreate() {
        if (this.currentType == 0) {
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.wallpapersDidLoad);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.wallpapersNeedReload);
            MessagesStorage.getInstance(this.currentAccount).getWallpapers();
        } else {
            int a = 0;
            while (true) {
                int[] iArr = defaultColors;
                if (a >= iArr.length) {
                    break;
                }
                this.wallPapers.add(new ColorWallpaper((long) (-(a + 3)), iArr[a]));
                a++;
            }
            if (this.currentType == 1 && this.patterns.isEmpty()) {
                NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.wallpapersDidLoad);
                MessagesStorage.getInstance(this.currentAccount).getWallpapers();
            }
        }
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        int i = this.currentType;
        if (i == 0) {
            this.searchAdapter.onDestroy();
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.wallpapersDidLoad);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.wallpapersNeedReload);
        } else if (i == 1) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.wallpapersDidLoad);
        }
        this.updater.cleanup();
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
        this.colorPaint = new Paint(1);
        Paint paint = new Paint(1);
        this.colorFramePaint = paint;
        paint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
        this.colorFramePaint.setStyle(Paint.Style.STROKE);
        this.colorFramePaint.setColor(855638016);
        this.updater = new WallpaperUpdater(getParentActivity(), this, new WallpaperUpdater.WallpaperUpdaterDelegate() {
            public void didSelectWallpaper(File file, Bitmap bitmap, boolean gallery) {
                WallpapersListActivity.this.presentFragment(new WallpaperActivity(new FileWallpaper(-1, file, file), bitmap), gallery);
            }

            public void needOpenColorPicker() {
            }
        });
        this.hasOwnBackground = true;
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        int i = this.currentType;
        if (i == 0) {
            this.actionBar.setTitle(LocaleController.getString("ChatBackground", R.string.ChatBackground));
        } else if (i == 1) {
            this.actionBar.setTitle(LocaleController.getString("SelectColorTitle", R.string.SelectColorTitle));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    if (WallpapersListActivity.this.actionBar.isActionModeShowed()) {
                        WallpapersListActivity.this.selectedWallPapers.clear();
                        WallpapersListActivity.this.actionBar.hideActionMode();
                        WallpapersListActivity.this.updateRowsSelection();
                        return;
                    }
                    WallpapersListActivity.this.finishFragment();
                } else if (id == 4) {
                    if (WallpapersListActivity.this.getParentActivity() != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder((Context) WallpapersListActivity.this.getParentActivity());
                        builder.setMessage(LocaleController.formatString("DeleteChatBackgroundsAlert", R.string.DeleteChatBackgroundsAlert, new Object[0]));
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                            public final void onClick(DialogInterface dialogInterface, int i) {
                                WallpapersListActivity.AnonymousClass2.this.lambda$onItemClick$2$WallpapersListActivity$2(dialogInterface, i);
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                        WallpapersListActivity.this.showDialog(builder.create());
                    }
                } else if (id == 3) {
                    Bundle args = new Bundle();
                    args.putBoolean("onlySelect", true);
                    args.putInt("dialogsType", 3);
                    DialogsActivity fragment = new DialogsActivity(args);
                    fragment.setDelegate(new DialogsActivity.DialogsActivityDelegate() {
                        public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
                            WallpapersListActivity.AnonymousClass2.this.lambda$onItemClick$3$WallpapersListActivity$2(dialogsActivity, arrayList, charSequence, z);
                        }
                    });
                    WallpapersListActivity.this.presentFragment(fragment);
                }
            }

            public /* synthetic */ void lambda$onItemClick$2$WallpapersListActivity$2(DialogInterface dialogInterface, int i) {
                AlertDialog unused = WallpapersListActivity.this.progressDialog = new AlertDialog(WallpapersListActivity.this.getParentActivity(), 3);
                WallpapersListActivity.this.progressDialog.setCanCancel(false);
                WallpapersListActivity.this.progressDialog.show();
                new ArrayList();
                int[] deleteCount = {WallpapersListActivity.this.selectedWallPapers.size()};
                for (int b = 0; b < WallpapersListActivity.this.selectedWallPapers.size(); b++) {
                    TLRPC.TL_wallPaper wallPaper = (TLRPC.TL_wallPaper) WallpapersListActivity.this.selectedWallPapers.valueAt(b);
                    TLRPC.TL_account_saveWallPaper req = new TLRPC.TL_account_saveWallPaper();
                    req.settings = new TLRPC.TL_wallPaperSettings();
                    req.unsave = true;
                    TLRPC.TL_inputWallPaper inputWallPaper = new TLRPC.TL_inputWallPaper();
                    inputWallPaper.id = wallPaper.id;
                    inputWallPaper.access_hash = wallPaper.access_hash;
                    req.wallpaper = inputWallPaper;
                    if (wallPaper.id == WallpapersListActivity.this.selectedBackground) {
                        WallpapersListActivity.this.resetDefaultWallPaper();
                    }
                    ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).sendRequest(req, new RequestDelegate(deleteCount) {
                        private final /* synthetic */ int[] f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            WallpapersListActivity.AnonymousClass2.this.lambda$null$1$WallpapersListActivity$2(this.f$1, tLObject, tL_error);
                        }
                    });
                }
                WallpapersListActivity.this.selectedWallPapers.clear();
                WallpapersListActivity.this.actionBar.hideActionMode();
                WallpapersListActivity.this.actionBar.closeSearchField();
            }

            public /* synthetic */ void lambda$null$1$WallpapersListActivity$2(int[] deleteCount, TLObject response, TLRPC.TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable(deleteCount) {
                    private final /* synthetic */ int[] f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        WallpapersListActivity.AnonymousClass2.this.lambda$null$0$WallpapersListActivity$2(this.f$1);
                    }
                });
            }

            public /* synthetic */ void lambda$null$0$WallpapersListActivity$2(int[] deleteCount) {
                deleteCount[0] = deleteCount[0] - 1;
                if (deleteCount[0] == 0) {
                    WallpapersListActivity.this.loadWallpapers();
                }
            }

            public /* synthetic */ void lambda$onItemClick$3$WallpapersListActivity$2(DialogsActivity fragment1, ArrayList dids, CharSequence message, boolean param) {
                ArrayList arrayList = dids;
                StringBuilder fmessage = new StringBuilder();
                for (int b = 0; b < WallpapersListActivity.this.selectedWallPapers.size(); b++) {
                    String link = AndroidUtilities.getWallPaperUrl((TLRPC.TL_wallPaper) WallpapersListActivity.this.selectedWallPapers.valueAt(b), WallpapersListActivity.this.currentAccount);
                    if (!TextUtils.isEmpty(link)) {
                        if (fmessage.length() > 0) {
                            fmessage.append(10);
                        }
                        fmessage.append(link);
                    }
                }
                WallpapersListActivity.this.selectedWallPapers.clear();
                WallpapersListActivity.this.actionBar.hideActionMode();
                WallpapersListActivity.this.actionBar.closeSearchField();
                if (dids.size() > 1 || ((Long) arrayList.get(0)).longValue() == ((long) UserConfig.getInstance(WallpapersListActivity.this.currentAccount).getClientUserId()) || message != null) {
                    WallpapersListActivity.this.updateRowsSelection();
                    for (int a = 0; a < dids.size(); a++) {
                        long did = ((Long) arrayList.get(a)).longValue();
                        if (message != null) {
                            SendMessagesHelper.getInstance(WallpapersListActivity.this.currentAccount).sendMessage(message.toString(), did, (MessageObject) null, (TLRPC.WebPage) null, true, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                        }
                        SendMessagesHelper.getInstance(WallpapersListActivity.this.currentAccount).sendMessage(fmessage.toString(), did, (MessageObject) null, (TLRPC.WebPage) null, true, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                    }
                    fragment1.finishFragment();
                    return;
                }
                long did2 = ((Long) arrayList.get(0)).longValue();
                int lower_part = (int) did2;
                int high_part = (int) (did2 >> 32);
                Bundle args1 = new Bundle();
                args1.putBoolean("scrollToTopOnResume", true);
                if (lower_part == 0) {
                    args1.putInt("enc_id", high_part);
                } else if (lower_part > 0) {
                    args1.putInt("user_id", lower_part);
                } else if (lower_part < 0) {
                    args1.putInt("chat_id", -lower_part);
                }
                if (lower_part == 0) {
                    DialogsActivity dialogsActivity = fragment1;
                } else if (!MessagesController.getInstance(WallpapersListActivity.this.currentAccount).checkCanOpenChat(args1, fragment1)) {
                    return;
                }
                NotificationCenter.getInstance(WallpapersListActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                WallpapersListActivity.this.presentFragment(new ChatActivity(args1), true);
                Bundle bundle = args1;
                int i = high_part;
                long j = did2;
                SendMessagesHelper.getInstance(WallpapersListActivity.this.currentAccount).sendMessage(fmessage.toString(), did2, (MessageObject) null, (TLRPC.WebPage) null, true, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
            }
        });
        if (this.currentType == 0) {
            ActionBarMenuItem actionBarMenuItemSearchListener = this.actionBar.createMenu().addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
                public void onSearchExpand() {
                    WallpapersListActivity.this.listView.setAdapter(WallpapersListActivity.this.searchAdapter);
                    WallpapersListActivity.this.listView.invalidate();
                }

                public void onSearchCollapse() {
                    WallpapersListActivity.this.listView.setAdapter(WallpapersListActivity.this.listAdapter);
                    WallpapersListActivity.this.listView.invalidate();
                    WallpapersListActivity.this.searchAdapter.processSearch((String) null, true);
                    WallpapersListActivity.this.searchItem.setSearchFieldCaption((CharSequence) null);
                    onCaptionCleared();
                }

                public void onTextChanged(EditText editText) {
                    WallpapersListActivity.this.searchAdapter.processSearch(editText.getText().toString(), false);
                }

                public void onCaptionCleared() {
                    WallpapersListActivity.this.searchAdapter.clearColor();
                    WallpapersListActivity.this.searchItem.setSearchFieldHint(LocaleController.getString("SearchBackgrounds", R.string.SearchBackgrounds));
                    if (WallpapersListActivity.this.searchAdapter != null) {
                        WallpapersListActivity.this.searchAdapter.cancelSearchingUser();
                    }
                }
            });
            this.searchItem = actionBarMenuItemSearchListener;
            actionBarMenuItemSearchListener.setSearchFieldHint(LocaleController.getString("SearchBackgrounds", R.string.SearchBackgrounds));
            ActionBarMenu actionMode = this.actionBar.createActionMode(false);
            actionMode.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefault));
            this.actionBar.setItemsColor(Theme.getColor(Theme.key_actionBarDefaultIcon), true);
            this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultSelector), true);
            NumberTextView numberTextView = new NumberTextView(actionMode.getContext());
            this.selectedMessagesCountTextView = numberTextView;
            numberTextView.setTextSize(18);
            this.selectedMessagesCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.selectedMessagesCountTextView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultIcon));
            this.selectedMessagesCountTextView.setOnTouchListener($$Lambda$WallpapersListActivity$URJLlp9W7V8o5UUipgHicvu5Hrk.INSTANCE);
            actionMode.addView(this.selectedMessagesCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 65, 0, 0, 0));
            this.actionModeViews.add(actionMode.addItemWithWidth(3, R.drawable.msg_forward, AndroidUtilities.dp(54.0f)));
            this.actionModeViews.add(actionMode.addItemWithWidth(4, R.drawable.msg_delete, AndroidUtilities.dp(54.0f)));
            this.selectedWallPapers.clear();
        }
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        AnonymousClass4 r3 = new RecyclerListView(context) {
            private Paint paint = new Paint();

            public boolean hasOverlappingRendering() {
                return false;
            }

            public void onDraw(Canvas c) {
                RecyclerView.ViewHolder holder;
                int bottom;
                if (getAdapter() != WallpapersListActivity.this.listAdapter || WallpapersListActivity.this.resetInfoRow == -1) {
                    holder = null;
                } else {
                    holder = findViewHolderForAdapterPosition(WallpapersListActivity.this.resetInfoRow);
                }
                int height = getMeasuredHeight();
                if (holder != null) {
                    bottom = holder.itemView.getBottom();
                    if (holder.itemView.getBottom() >= height) {
                        bottom = height;
                    }
                } else {
                    bottom = height;
                }
                this.paint.setColor(Theme.getColor(Theme.key_windowBackgroundGray));
                c.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) bottom, this.paint);
                if (bottom != height) {
                    this.paint.setColor(Theme.getColor(Theme.key_windowBackgroundGray));
                    c.drawRect(0.0f, (float) bottom, (float) getMeasuredWidth(), (float) height, this.paint);
                }
            }
        };
        this.listView = r3;
        r3.setClipToPadding(false);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.listView.setLayoutAnimation((LayoutAnimationController) null);
        RecyclerListView recyclerListView = this.listView;
        AnonymousClass5 r4 = new LinearLayoutManager(context, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = r4;
        recyclerListView.setLayoutManager(r4);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f)));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter2 = new ListAdapter(context);
        this.listAdapter = listAdapter2;
        recyclerListView2.setAdapter(listAdapter2);
        this.searchAdapter = new SearchAdapter(context);
        this.listView.setGlowColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                WallpapersListActivity.this.lambda$createView$3$WallpapersListActivity(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                boolean z = true;
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(WallpapersListActivity.this.getParentActivity().getCurrentFocus());
                }
                WallpapersListActivity wallpapersListActivity = WallpapersListActivity.this;
                if (newState == 0) {
                    z = false;
                }
                boolean unused = wallpapersListActivity.scrolling = z;
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (WallpapersListActivity.this.listView.getAdapter() == WallpapersListActivity.this.searchAdapter) {
                    int firstVisibleItem = WallpapersListActivity.this.layoutManager.findFirstVisibleItemPosition();
                    int visibleItemCount = firstVisibleItem == -1 ? 0 : Math.abs(WallpapersListActivity.this.layoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
                    if (visibleItemCount > 0) {
                        int totalItemCount = WallpapersListActivity.this.layoutManager.getItemCount();
                        if (visibleItemCount != 0 && firstVisibleItem + visibleItemCount > totalItemCount - 2) {
                            WallpapersListActivity.this.searchAdapter.loadMoreResults();
                        }
                    }
                }
            }
        });
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.searchEmptyView = emptyTextProgressView;
        emptyTextProgressView.setVisibility(8);
        this.searchEmptyView.setShowAtCenter(true);
        this.searchEmptyView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.searchEmptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
        this.listView.setEmptyView(this.searchEmptyView);
        frameLayout.addView(this.searchEmptyView, LayoutHelper.createFrame(-1, -1.0f));
        updateRows();
        return this.fragmentView;
    }

    static /* synthetic */ boolean lambda$createView$0(View v, MotionEvent event) {
        return true;
    }

    public /* synthetic */ void lambda$createView$3$WallpapersListActivity(View view, int position) {
        if (getParentActivity() != null && this.listView.getAdapter() != this.searchAdapter) {
            if (position == this.uploadImageRow) {
                this.updater.openGallery();
            } else if (position == this.setColorRow) {
                WallpapersListActivity activity = new WallpapersListActivity(1);
                activity.patterns = this.patterns;
                presentFragment(activity);
            } else if (position == this.resetRow) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setTitle(LocaleController.getString("ResetChatBackgroundsAlertTitle", R.string.ResetChatBackgroundsAlertTitle));
                builder.setMessage(LocaleController.getString("ResetChatBackgroundsAlert", R.string.ResetChatBackgroundsAlert));
                builder.setPositiveButton(LocaleController.getString("Reset", R.string.Reset), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        WallpapersListActivity.this.lambda$null$2$WallpapersListActivity(dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                AlertDialog dialog = builder.create();
                showDialog(dialog);
                TextView button = (TextView) dialog.getButton(-1);
                if (button != null) {
                    button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
                }
            }
        }
    }

    public /* synthetic */ void lambda$null$2$WallpapersListActivity(DialogInterface dialogInterface, int i) {
        if (this.actionBar.isActionModeShowed()) {
            this.selectedWallPapers.clear();
            this.actionBar.hideActionMode();
            updateRowsSelection();
        }
        AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
        this.progressDialog = alertDialog;
        alertDialog.setCanCancel(false);
        this.progressDialog.show();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_resetWallPapers(), new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                WallpapersListActivity.this.lambda$null$1$WallpapersListActivity(tLObject, tL_error);
            }
        });
        resetDefaultWallPaper();
        fillWallpapersWithCustom();
    }

    public /* synthetic */ void lambda$null$1$WallpapersListActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                WallpapersListActivity.this.loadWallpapers();
            }
        });
    }

    public void onResume() {
        super.onResume();
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        this.selectedBackground = Theme.getSelectedBackgroundId();
        this.selectedPattern = preferences.getLong("selectedPattern", 0);
        this.selectedColor = preferences.getInt("selectedColor", 0);
        this.selectedIntensity = preferences.getFloat("selectedIntensity", 1.0f);
        this.selectedBackgroundMotion = preferences.getBoolean("selectedBackgroundMotion", false);
        this.selectedBackgroundBlurred = preferences.getBoolean("selectedBackgroundBlurred", false);
        fillWallpapersWithCustom();
        fixLayout();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fixLayout();
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        this.updater.onActivityResult(requestCode, resultCode, data);
    }

    public void saveSelfArgs(Bundle args) {
        String currentPicturePath = this.updater.getCurrentPicturePath();
        if (currentPicturePath != null) {
            args.putString("path", currentPicturePath);
        }
    }

    public void restoreSelfArgs(Bundle args) {
        this.updater.setCurrentPicturePath(args.getString("path"));
    }

    /* access modifiers changed from: private */
    public boolean onItemLongClick(WallpaperCell view, Object object, int index) {
        if (this.actionBar.isActionModeShowed() || getParentActivity() == null || !(object instanceof TLRPC.TL_wallPaper)) {
            return false;
        }
        TLRPC.TL_wallPaper wallPaper = (TLRPC.TL_wallPaper) object;
        AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
        this.selectedWallPapers.put(wallPaper.id, wallPaper);
        this.selectedMessagesCountTextView.setNumber(1, false);
        AnimatorSet animatorSet = new AnimatorSet();
        ArrayList<Animator> animators = new ArrayList<>();
        for (int i = 0; i < this.actionModeViews.size(); i++) {
            View view2 = this.actionModeViews.get(i);
            AndroidUtilities.clearDrawableAnimation(view2);
            animators.add(ObjectAnimator.ofFloat(view2, View.SCALE_Y, new float[]{0.1f, 1.0f}));
        }
        animatorSet.playTogether(animators);
        animatorSet.setDuration(250);
        animatorSet.start();
        this.scrolling = false;
        this.actionBar.showActionMode();
        view.setChecked(index, true, true);
        return true;
    }

    /* access modifiers changed from: private */
    public void onItemClick(WallpaperCell view, Object object, int index) {
        Object object2 = object;
        if (!this.actionBar.isActionModeShowed()) {
            WallpaperCell wallpaperCell = view;
            int i = index;
            long id = getWallPaperId(object2);
            if (object2 instanceof TLRPC.TL_wallPaper) {
                TLRPC.TL_wallPaper wallPaper = (TLRPC.TL_wallPaper) object2;
                if (wallPaper.pattern) {
                    object2 = new ColorWallpaper(wallPaper.id, wallPaper.settings.background_color, wallPaper.id, ((float) wallPaper.settings.intensity) / 100.0f, wallPaper.settings.motion, (File) null);
                }
            }
            WallpaperActivity wallpaperActivity = new WallpaperActivity(object2, (Bitmap) null);
            if (this.currentType == 1) {
                wallpaperActivity.setDelegate(new WallpaperActivity.WallpaperActivityDelegate() {
                    public final void didSetNewBackground() {
                        WallpapersListActivity.this.removeSelfFromStack();
                    }
                });
            }
            if (this.selectedBackground == id) {
                wallpaperActivity.setInitialModes(this.selectedBackgroundBlurred, this.selectedBackgroundMotion);
            }
            wallpaperActivity.setPatterns(this.patterns);
            presentFragment(wallpaperActivity);
        } else if (object2 instanceof TLRPC.TL_wallPaper) {
            TLRPC.TL_wallPaper wallPaper2 = (TLRPC.TL_wallPaper) object2;
            if (this.selectedWallPapers.indexOfKey(wallPaper2.id) >= 0) {
                this.selectedWallPapers.remove(wallPaper2.id);
            } else {
                this.selectedWallPapers.put(wallPaper2.id, wallPaper2);
            }
            if (this.selectedWallPapers.size() == 0) {
                this.actionBar.hideActionMode();
            } else {
                this.selectedMessagesCountTextView.setNumber(this.selectedWallPapers.size(), true);
            }
            boolean z = false;
            this.scrolling = false;
            if (this.selectedWallPapers.indexOfKey(wallPaper2.id) >= 0) {
                z = true;
            }
            view.setChecked(index, z, true);
        }
    }

    private long getWallPaperId(Object object) {
        if (object instanceof TLRPC.TL_wallPaper) {
            return ((TLRPC.TL_wallPaper) object).id;
        }
        if (object instanceof ColorWallpaper) {
            return ((ColorWallpaper) object).id;
        }
        if (object instanceof FileWallpaper) {
            return ((FileWallpaper) object).id;
        }
        return 0;
    }

    /* access modifiers changed from: private */
    public void updateRowsSelection() {
        int count = this.listView.getChildCount();
        for (int a = 0; a < count; a++) {
            View child = this.listView.getChildAt(a);
            if (child instanceof WallpaperCell) {
                WallpaperCell cell = (WallpaperCell) child;
                for (int b = 0; b < 5; b++) {
                    cell.setChecked(b, false, true);
                }
            }
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.wallpapersDidLoad) {
            ArrayList<TLRPC.TL_wallPaper> arrayList = args[0];
            this.patterns.clear();
            if (this.currentType != 1) {
                this.wallPapers.clear();
                this.allWallPapers.clear();
                this.allWallPapersDict.clear();
                this.allWallPapers.addAll(arrayList);
            }
            int N = arrayList.size();
            for (int a = 0; a < N; a++) {
                TLRPC.TL_wallPaper wallPaper = arrayList.get(a);
                if (wallPaper.pattern) {
                    this.patterns.add(wallPaper);
                }
                if (this.currentType != 1 && (!wallPaper.pattern || wallPaper.settings != null)) {
                    this.allWallPapersDict.put(wallPaper.id, wallPaper);
                    this.wallPapers.add(wallPaper);
                }
            }
            this.selectedBackground = Theme.getSelectedBackgroundId();
            fillWallpapersWithCustom();
            loadWallpapers();
        } else if (id == NotificationCenter.didSetNewWallpapper) {
            RecyclerListView recyclerListView = this.listView;
            if (recyclerListView != null) {
                recyclerListView.invalidateViews();
            }
            if (this.actionBar != null) {
                this.actionBar.closeSearchField();
            }
        } else if (id == NotificationCenter.wallpapersNeedReload) {
            MessagesStorage.getInstance(this.currentAccount).getWallpapers();
        }
    }

    /* access modifiers changed from: private */
    public void loadWallpapers() {
        long acc = 0;
        int N = this.allWallPapers.size();
        for (int a = 0; a < N; a++) {
            Object object = this.allWallPapers.get(a);
            if (object instanceof TLRPC.TL_wallPaper) {
                TLRPC.TL_wallPaper wallPaper = (TLRPC.TL_wallPaper) object;
                acc = (((((((acc * 20261) + 2147483648L) + ((long) ((int) (wallPaper.id >> 32)))) % 2147483648L) * 20261) + 2147483648L) + ((long) ((int) wallPaper.id))) % 2147483648L;
            }
        }
        TLRPC.TL_account_getWallPapers req = new TLRPC.TL_account_getWallPapers();
        req.hash = (int) acc;
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                WallpapersListActivity.this.lambda$loadWallpapers$5$WallpapersListActivity(tLObject, tL_error);
            }
        }), this.classGuid);
    }

    public /* synthetic */ void lambda$loadWallpapers$5$WallpapersListActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(response) {
            private final /* synthetic */ TLObject f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                WallpapersListActivity.this.lambda$null$4$WallpapersListActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$4$WallpapersListActivity(TLObject response) {
        if (response instanceof TLRPC.TL_account_wallPapers) {
            TLRPC.TL_account_wallPapers res = (TLRPC.TL_account_wallPapers) response;
            this.patterns.clear();
            if (this.currentType != 1) {
                this.wallPapers.clear();
                this.allWallPapersDict.clear();
                this.allWallPapers.clear();
                this.allWallPapers.addAll(res.wallpapers);
            }
            int N = res.wallpapers.size();
            for (int a = 0; a < N; a++) {
                TLRPC.TL_wallPaper wallPaper = (TLRPC.TL_wallPaper) res.wallpapers.get(a);
                this.allWallPapersDict.put(wallPaper.id, wallPaper);
                if (wallPaper.pattern) {
                    this.patterns.add(wallPaper);
                }
                if (this.currentType != 1 && (!wallPaper.pattern || wallPaper.settings != null)) {
                    this.wallPapers.add(wallPaper);
                }
            }
            fillWallpapersWithCustom();
            MessagesStorage.getInstance(this.currentAccount).putWallpapers(res.wallpapers, 1);
        }
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.listView.smoothScrollToPosition(0);
        }
    }

    private void fillWallpapersWithCustom() {
        if (this.currentType == 0) {
            SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
            ColorWallpaper colorWallpaper = this.addedColorWallpaper;
            if (colorWallpaper != null) {
                this.wallPapers.remove(colorWallpaper);
                this.addedColorWallpaper = null;
            }
            FileWallpaper fileWallpaper = this.addedFileWallpaper;
            if (fileWallpaper != null) {
                this.wallPapers.remove(fileWallpaper);
                this.addedFileWallpaper = null;
            }
            FileWallpaper fileWallpaper2 = this.catsWallpaper;
            if (fileWallpaper2 == null) {
                this.catsWallpaper = new FileWallpaper((long) Theme.DEFAULT_BACKGROUND_ID, (int) R.drawable.background_hd, (int) R.drawable.catstile);
            } else {
                this.wallPapers.remove(fileWallpaper2);
            }
            FileWallpaper fileWallpaper3 = this.themeWallpaper;
            if (fileWallpaper3 != null) {
                this.wallPapers.remove(fileWallpaper3);
            }
            Collections.sort(this.wallPapers, new Comparator(Theme.getCurrentTheme().isDark()) {
                private final /* synthetic */ boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final int compare(Object obj, Object obj2) {
                    return WallpapersListActivity.this.lambda$fillWallpapersWithCustom$6$WallpapersListActivity(this.f$1, obj, obj2);
                }
            });
            if (!Theme.hasWallpaperFromTheme() || Theme.isThemeWallpaperPublic()) {
                this.themeWallpaper = null;
            } else {
                if (this.themeWallpaper == null) {
                    this.themeWallpaper = new FileWallpaper(-2, -2, -2);
                }
                this.wallPapers.add(0, this.themeWallpaper);
            }
            long j = this.selectedBackground;
            if (j == -1 || (j != Theme.DEFAULT_BACKGROUND_ID && ((j < -100 || j > 0) && this.allWallPapersDict.indexOfKey(this.selectedBackground) < 0))) {
                long j2 = this.selectedPattern;
                String str = "wallpaper.jpg";
                if (j2 != 0) {
                    ColorWallpaper colorWallpaper2 = new ColorWallpaper(this.selectedBackground, this.selectedColor, j2, this.selectedIntensity, this.selectedBackgroundMotion, new File(ApplicationLoader.getFilesDirFixed(), str));
                    this.addedColorWallpaper = colorWallpaper2;
                    this.wallPapers.add(0, colorWallpaper2);
                } else {
                    int i = this.selectedColor;
                    if (i != 0) {
                        ColorWallpaper colorWallpaper3 = new ColorWallpaper(this.selectedBackground, i);
                        this.addedColorWallpaper = colorWallpaper3;
                        this.wallPapers.add(0, colorWallpaper3);
                    } else {
                        long j3 = this.selectedBackground;
                        File file = new File(ApplicationLoader.getFilesDirFixed(), str);
                        File filesDirFixed = ApplicationLoader.getFilesDirFixed();
                        if (this.selectedBackgroundBlurred) {
                            str = "wallpaper_original.jpg";
                        }
                        FileWallpaper fileWallpaper4 = new FileWallpaper(j3, file, new File(filesDirFixed, str));
                        this.addedFileWallpaper = fileWallpaper4;
                        this.wallPapers.add(0, fileWallpaper4);
                    }
                }
            } else {
                int i2 = this.selectedColor;
                if (i2 != 0) {
                    long j4 = this.selectedBackground;
                    if (j4 >= -100 && this.selectedPattern < -1) {
                        ColorWallpaper colorWallpaper4 = new ColorWallpaper(j4, i2);
                        this.addedColorWallpaper = colorWallpaper4;
                        this.wallPapers.add(0, colorWallpaper4);
                    }
                }
            }
            if (this.selectedBackground == Theme.DEFAULT_BACKGROUND_ID) {
                this.wallPapers.add(0, this.catsWallpaper);
            } else {
                this.wallPapers.add(this.catsWallpaper);
            }
            updateRows();
        }
    }

    public /* synthetic */ int lambda$fillWallpapersWithCustom$6$WallpapersListActivity(boolean currentThemeDark, Object o1, Object o2) {
        if (!(o1 instanceof TLRPC.TL_wallPaper) || !(o2 instanceof TLRPC.TL_wallPaper)) {
            return 0;
        }
        TLRPC.TL_wallPaper wallPaper1 = (TLRPC.TL_wallPaper) o1;
        TLRPC.TL_wallPaper wallPaper2 = (TLRPC.TL_wallPaper) o2;
        if (wallPaper1.id == this.selectedBackground) {
            return -1;
        }
        if (wallPaper2.id == this.selectedBackground) {
            return 1;
        }
        int index1 = this.allWallPapers.indexOf(wallPaper1);
        int index2 = this.allWallPapers.indexOf(wallPaper2);
        if ((!wallPaper1.dark || !wallPaper2.dark) && (wallPaper1.dark || wallPaper2.dark)) {
            if (!wallPaper1.dark || wallPaper2.dark) {
                if (currentThemeDark) {
                    return 1;
                }
                return -1;
            } else if (currentThemeDark) {
                return -1;
            } else {
                return 1;
            }
        } else if (index1 > index2) {
            return 1;
        } else {
            if (index1 < index2) {
                return -1;
            }
            return 0;
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        if (this.currentType == 0) {
            int i = 0 + 1;
            this.rowCount = i;
            this.uploadImageRow = 0;
            int i2 = i + 1;
            this.rowCount = i2;
            this.setColorRow = i;
            this.rowCount = i2 + 1;
            this.sectionRow = i2;
        } else {
            this.uploadImageRow = -1;
            this.setColorRow = -1;
            this.sectionRow = -1;
        }
        if (!this.wallPapers.isEmpty()) {
            int ceil = (int) Math.ceil((double) (((float) this.wallPapers.size()) / ((float) this.columnsCount)));
            this.totalWallpaperRows = ceil;
            int i3 = this.rowCount;
            this.wallPaperStartRow = i3;
            this.rowCount = i3 + ceil;
        } else {
            this.wallPaperStartRow = -1;
        }
        if (this.currentType == 0) {
            int i4 = this.rowCount;
            int i5 = i4 + 1;
            this.rowCount = i5;
            this.resetSectionRow = i4;
            int i6 = i5 + 1;
            this.rowCount = i6;
            this.resetRow = i5;
            this.rowCount = i6 + 1;
            this.resetInfoRow = i6;
        } else {
            this.resetSectionRow = -1;
            this.resetRow = -1;
            this.resetInfoRow = -1;
        }
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            this.scrolling = true;
            listAdapter2.notifyDataSetChanged();
        }
    }

    private void fixLayout() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            recyclerListView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    WallpapersListActivity.this.fixLayoutInternal();
                    if (WallpapersListActivity.this.listView == null) {
                        return true;
                    }
                    WallpapersListActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void fixLayoutInternal() {
        if (getParentActivity() != null) {
            int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
            if (AndroidUtilities.isTablet()) {
                this.columnsCount = 3;
            } else if (rotation == 3 || rotation == 1) {
                this.columnsCount = 5;
            } else {
                this.columnsCount = 3;
            }
            updateRows();
        }
    }

    private class ColorCell extends View {
        private int color;

        public ColorCell(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(AndroidUtilities.dp(50.0f), AndroidUtilities.dp(62.0f));
        }

        public void setColor(int value) {
            this.color = value;
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            WallpapersListActivity.this.colorPaint.setColor(this.color);
            canvas.drawCircle((float) AndroidUtilities.dp(25.0f), (float) AndroidUtilities.dp(31.0f), (float) AndroidUtilities.dp(18.0f), WallpapersListActivity.this.colorPaint);
            if (this.color == Theme.getColor(Theme.key_windowBackgroundWhite)) {
                canvas.drawCircle((float) AndroidUtilities.dp(25.0f), (float) AndroidUtilities.dp(31.0f), (float) AndroidUtilities.dp(18.0f), WallpapersListActivity.this.colorFramePaint);
            }
        }
    }

    private class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private boolean bingSearchEndReached = true;
        private int imageReqId;
        private RecyclerListView innerListView;
        private String lastSearchImageString;
        private String lastSearchString;
        private int lastSearchToken;
        /* access modifiers changed from: private */
        public Context mContext;
        private String nextImagesSearchOffset;
        private ArrayList<MediaController.SearchImage> searchResult = new ArrayList<>();
        private HashMap<String, MediaController.SearchImage> searchResultKeys = new HashMap<>();
        private Runnable searchRunnable;
        private boolean searchingUser;
        private String selectedColor;

        private class CategoryAdapterRecycler extends RecyclerListView.SelectionAdapter {
            private CategoryAdapterRecycler() {
            }

            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new RecyclerListView.Holder(new ColorCell(SearchAdapter.this.mContext));
            }

            public boolean isEnabled(RecyclerView.ViewHolder holder) {
                return true;
            }

            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ((ColorCell) holder.itemView).setColor(WallpapersListActivity.searchColors[position]);
            }

            public int getItemCount() {
                return WallpapersListActivity.searchColors.length;
            }
        }

        public SearchAdapter(Context context) {
            this.mContext = context;
        }

        public RecyclerListView getInnerListView() {
            return this.innerListView;
        }

        public void onDestroy() {
            if (this.imageReqId != 0) {
                ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).cancelRequest(this.imageReqId, true);
                this.imageReqId = 0;
            }
        }

        public void clearColor() {
            this.selectedColor = null;
            processSearch((String) null, true);
        }

        /* access modifiers changed from: private */
        public void processSearch(String text, boolean now) {
            if (!(text == null || this.selectedColor == null)) {
                text = "#color" + this.selectedColor + " " + text;
            }
            Runnable runnable = this.searchRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.searchRunnable = null;
            }
            if (TextUtils.isEmpty(text)) {
                this.searchResult.clear();
                this.searchResultKeys.clear();
                this.bingSearchEndReached = true;
                this.lastSearchString = null;
                if (this.imageReqId != 0) {
                    ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).cancelRequest(this.imageReqId, true);
                    this.imageReqId = 0;
                }
                WallpapersListActivity.this.searchEmptyView.showTextView();
            } else {
                WallpapersListActivity.this.searchEmptyView.showProgress();
                String textFinal = text;
                if (now) {
                    doSearch(textFinal);
                } else {
                    $$Lambda$WallpapersListActivity$SearchAdapter$M5gMv8xdY8HIq4XEz6mtLHjv3Y r1 = new Runnable(textFinal) {
                        private final /* synthetic */ String f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            WallpapersListActivity.SearchAdapter.this.lambda$processSearch$0$WallpapersListActivity$SearchAdapter(this.f$1);
                        }
                    };
                    this.searchRunnable = r1;
                    AndroidUtilities.runOnUIThread(r1, 500);
                }
            }
            notifyDataSetChanged();
        }

        public /* synthetic */ void lambda$processSearch$0$WallpapersListActivity$SearchAdapter(String textFinal) {
            doSearch(textFinal);
            this.searchRunnable = null;
        }

        private void doSearch(String textFinal) {
            this.searchResult.clear();
            this.searchResultKeys.clear();
            this.bingSearchEndReached = true;
            searchImages(textFinal, "", true);
            this.lastSearchString = textFinal;
            notifyDataSetChanged();
        }

        private void searchBotUser() {
            if (!this.searchingUser) {
                this.searchingUser = true;
                TLRPC.TL_contacts_resolveUsername req = new TLRPC.TL_contacts_resolveUsername();
                req.username = MessagesController.getInstance(WallpapersListActivity.this.currentAccount).imageSearchBot;
                ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        WallpapersListActivity.SearchAdapter.this.lambda$searchBotUser$2$WallpapersListActivity$SearchAdapter(tLObject, tL_error);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$searchBotUser$2$WallpapersListActivity$SearchAdapter(TLObject response, TLRPC.TL_error error) {
            if (response != null) {
                AndroidUtilities.runOnUIThread(new Runnable(response) {
                    private final /* synthetic */ TLObject f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        WallpapersListActivity.SearchAdapter.this.lambda$null$1$WallpapersListActivity$SearchAdapter(this.f$1);
                    }
                });
            } else if (error != null) {
                cancelSearchingUser();
            }
        }

        public /* synthetic */ void lambda$null$1$WallpapersListActivity$SearchAdapter(TLObject response) {
            TLRPC.TL_contacts_resolvedPeer res = (TLRPC.TL_contacts_resolvedPeer) response;
            MessagesController.getInstance(WallpapersListActivity.this.currentAccount).putUsers(res.users, false);
            MessagesController.getInstance(WallpapersListActivity.this.currentAccount).putChats(res.chats, false);
            MessagesStorage.getInstance(WallpapersListActivity.this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
            String str = this.lastSearchImageString;
            this.lastSearchImageString = null;
            searchImages(str, "", false);
        }

        public void loadMoreResults() {
            if (!this.bingSearchEndReached && this.imageReqId == 0) {
                searchImages(this.lastSearchString, this.nextImagesSearchOffset, true);
            }
        }

        private void searchImages(String query, String offset, boolean searchUser) {
            if (this.imageReqId != 0) {
                ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).cancelRequest(this.imageReqId, true);
                this.imageReqId = 0;
            }
            this.lastSearchImageString = query;
            TLObject object = MessagesController.getInstance(WallpapersListActivity.this.currentAccount).getUserOrChat(MessagesController.getInstance(WallpapersListActivity.this.currentAccount).imageSearchBot);
            if (object instanceof TLRPC.User) {
                TLRPC.TL_messages_getInlineBotResults req = new TLRPC.TL_messages_getInlineBotResults();
                req.query = "#wallpaper " + query;
                req.bot = MessagesController.getInstance(WallpapersListActivity.this.currentAccount).getInputUser((TLRPC.User) object);
                req.offset = offset;
                req.peer = new TLRPC.TL_inputPeerEmpty();
                int token = this.lastSearchToken + 1;
                this.lastSearchToken = token;
                this.imageReqId = ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).sendRequest(req, new RequestDelegate(token) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        WallpapersListActivity.SearchAdapter.this.lambda$searchImages$4$WallpapersListActivity$SearchAdapter(this.f$1, tLObject, tL_error);
                    }
                });
                ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).bindRequestToGuid(this.imageReqId, WallpapersListActivity.this.classGuid);
            } else if (searchUser) {
                searchBotUser();
            }
        }

        public /* synthetic */ void lambda$searchImages$4$WallpapersListActivity$SearchAdapter(int token, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable(token, response) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ TLObject f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    WallpapersListActivity.SearchAdapter.this.lambda$null$3$WallpapersListActivity$SearchAdapter(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$null$3$WallpapersListActivity$SearchAdapter(int token, TLObject response) {
            if (token == this.lastSearchToken) {
                boolean z = false;
                this.imageReqId = 0;
                int oldCount = this.searchResult.size();
                if (response != null) {
                    TLRPC.messages_BotResults res = (TLRPC.messages_BotResults) response;
                    this.nextImagesSearchOffset = res.next_offset;
                    int count = res.results.size();
                    for (int a = 0; a < count; a++) {
                        TLRPC.BotInlineResult result = res.results.get(a);
                        if ("photo".equals(result.type) && !this.searchResultKeys.containsKey(result.id)) {
                            MediaController.SearchImage bingImage = new MediaController.SearchImage();
                            if (result.photo != null) {
                                TLRPC.PhotoSize size = FileLoader.getClosestPhotoSizeWithSize(result.photo.sizes, AndroidUtilities.getPhotoSize());
                                TLRPC.PhotoSize size2 = FileLoader.getClosestPhotoSizeWithSize(result.photo.sizes, 320);
                                if (size != null) {
                                    bingImage.width = size.w;
                                    bingImage.height = size.h;
                                    bingImage.photoSize = size;
                                    bingImage.photo = result.photo;
                                    bingImage.size = size.size;
                                    bingImage.thumbPhotoSize = size2;
                                }
                            } else if (result.content != null) {
                                int b = 0;
                                while (true) {
                                    if (b >= result.content.attributes.size()) {
                                        break;
                                    }
                                    TLRPC.DocumentAttribute attribute = result.content.attributes.get(b);
                                    if (attribute instanceof TLRPC.TL_documentAttributeImageSize) {
                                        bingImage.width = attribute.w;
                                        bingImage.height = attribute.h;
                                        break;
                                    }
                                    b++;
                                }
                                if (result.thumb != null) {
                                    bingImage.thumbUrl = result.thumb.url;
                                } else {
                                    bingImage.thumbUrl = null;
                                }
                                bingImage.imageUrl = result.content.url;
                                bingImage.size = result.content.size;
                            }
                            bingImage.id = result.id;
                            bingImage.type = 0;
                            this.searchResult.add(bingImage);
                            this.searchResultKeys.put(bingImage.id, bingImage);
                        }
                    }
                    if (oldCount == this.searchResult.size() || this.nextImagesSearchOffset == null) {
                        z = true;
                    }
                    this.bingSearchEndReached = z;
                }
                if (oldCount != this.searchResult.size()) {
                    int prevLastRow = oldCount % WallpapersListActivity.this.columnsCount;
                    int oldRowCount = (int) Math.ceil((double) (((float) oldCount) / ((float) WallpapersListActivity.this.columnsCount)));
                    if (prevLastRow != 0) {
                        notifyItemChanged(((int) Math.ceil((double) (((float) oldCount) / ((float) WallpapersListActivity.this.columnsCount)))) - 1);
                    }
                    WallpapersListActivity.this.searchAdapter.notifyItemRangeInserted(oldRowCount, ((int) Math.ceil((double) (((float) this.searchResult.size()) / ((float) WallpapersListActivity.this.columnsCount)))) - oldRowCount);
                }
                WallpapersListActivity.this.searchEmptyView.showTextView();
            }
        }

        /* access modifiers changed from: private */
        public void cancelSearchingUser() {
            if (WallpapersListActivity.this.searchAdapter.imageReqId != 0) {
                ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).cancelRequest(WallpapersListActivity.this.searchAdapter.imageReqId, true);
                WallpapersListActivity.this.searchAdapter.imageReqId = 0;
            }
            WallpapersListActivity.this.searchAdapter.searchingUser = false;
        }

        public int getItemCount() {
            if (TextUtils.isEmpty(this.lastSearchString)) {
                return 2;
            }
            return (int) Math.ceil((double) (((float) this.searchResult.size()) / ((float) WallpapersListActivity.this.columnsCount)));
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() != 2;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v0, resolved type: im.bclpbkiauv.ui.WallpapersListActivity$SearchAdapter$1} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: im.bclpbkiauv.ui.WallpapersListActivity$SearchAdapter$1} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: im.bclpbkiauv.ui.WallpapersListActivity$SearchAdapter$1} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: im.bclpbkiauv.ui.WallpapersListActivity$SearchAdapter$1} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: im.bclpbkiauv.ui.cells.GraySectionCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r9, int r10) {
            /*
                r8 = this;
                r0 = 0
                r1 = 1
                if (r10 == 0) goto L_0x0053
                if (r10 == r1) goto L_0x0013
                r2 = 2
                if (r10 == r2) goto L_0x000a
                goto L_0x005c
            L_0x000a:
                im.bclpbkiauv.ui.cells.GraySectionCell r2 = new im.bclpbkiauv.ui.cells.GraySectionCell
                android.content.Context r3 = r8.mContext
                r2.<init>(r3)
                r0 = r2
                goto L_0x005c
            L_0x0013:
                im.bclpbkiauv.ui.WallpapersListActivity$SearchAdapter$2 r2 = new im.bclpbkiauv.ui.WallpapersListActivity$SearchAdapter$2
                android.content.Context r3 = r8.mContext
                r2.<init>(r3)
                r3 = 0
                r2.setItemAnimator(r3)
                r2.setLayoutAnimation(r3)
                im.bclpbkiauv.ui.WallpapersListActivity$SearchAdapter$3 r4 = new im.bclpbkiauv.ui.WallpapersListActivity$SearchAdapter$3
                android.content.Context r5 = r8.mContext
                r4.<init>(r5)
                r5 = 1088421888(0x40e00000, float:7.0)
                int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
                int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
                r7 = 0
                r2.setPadding(r6, r7, r5, r7)
                r2.setClipToPadding(r7)
                r4.setOrientation(r7)
                r2.setLayoutManager(r4)
                im.bclpbkiauv.ui.WallpapersListActivity$SearchAdapter$CategoryAdapterRecycler r5 = new im.bclpbkiauv.ui.WallpapersListActivity$SearchAdapter$CategoryAdapterRecycler
                r5.<init>()
                r2.setAdapter(r5)
                im.bclpbkiauv.ui.-$$Lambda$WallpapersListActivity$SearchAdapter$H90eDxkPALtfLTfor2LuRAez_88 r3 = new im.bclpbkiauv.ui.-$$Lambda$WallpapersListActivity$SearchAdapter$H90eDxkPALtfLTfor2LuRAez_88
                r3.<init>()
                r2.setOnItemClickListener((im.bclpbkiauv.ui.components.RecyclerListView.OnItemClickListener) r3)
                r0 = r2
                r8.innerListView = r2
                goto L_0x005c
            L_0x0053:
                im.bclpbkiauv.ui.WallpapersListActivity$SearchAdapter$1 r2 = new im.bclpbkiauv.ui.WallpapersListActivity$SearchAdapter$1
                android.content.Context r3 = r8.mContext
                r2.<init>(r3)
                r0 = r2
            L_0x005c:
                r2 = -1
                if (r10 != r1) goto L_0x006e
                androidx.recyclerview.widget.RecyclerView$LayoutParams r1 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r3 = 1114636288(0x42700000, float:60.0)
                int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
                r1.<init>((int) r2, (int) r3)
                r0.setLayoutParams(r1)
                goto L_0x0077
            L_0x006e:
                androidx.recyclerview.widget.RecyclerView$LayoutParams r1 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r3 = -2
                r1.<init>((int) r2, (int) r3)
                r0.setLayoutParams(r1)
            L_0x0077:
                im.bclpbkiauv.ui.components.RecyclerListView$Holder r1 = new im.bclpbkiauv.ui.components.RecyclerListView$Holder
                r1.<init>(r0)
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.WallpapersListActivity.SearchAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public /* synthetic */ void lambda$onCreateViewHolder$5$WallpapersListActivity$SearchAdapter(View view1, int position) {
            String color = LocaleController.getString("BackgroundSearchColor", R.string.BackgroundSearchColor);
            Spannable spannable = new SpannableString(color + " " + LocaleController.getString(WallpapersListActivity.searchColorsNames[position], WallpapersListActivity.searchColorsNamesR[position]));
            spannable.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_actionBarDefaultSubtitle)), color.length(), spannable.length(), 33);
            WallpapersListActivity.this.searchItem.setSearchFieldCaption(spannable);
            WallpapersListActivity.this.searchItem.setSearchFieldHint((CharSequence) null);
            WallpapersListActivity.this.searchItem.setSearchFieldText("", true);
            this.selectedColor = WallpapersListActivity.searchColorsNames[position];
            processSearch("", true);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int itemViewType = holder.getItemViewType();
            if (itemViewType == 0) {
                WallpaperCell wallpaperCell = (WallpaperCell) holder.itemView;
                int position2 = position * WallpapersListActivity.this.columnsCount;
                int totalRows = (int) Math.ceil((double) (((float) this.searchResult.size()) / ((float) WallpapersListActivity.this.columnsCount)));
                int access$4700 = WallpapersListActivity.this.columnsCount;
                boolean z = false;
                boolean z2 = position2 == 0;
                if (position2 / WallpapersListActivity.this.columnsCount == totalRows - 1) {
                    z = true;
                }
                wallpaperCell.setParams(access$4700, z2, z);
                for (int a = 0; a < WallpapersListActivity.this.columnsCount; a++) {
                    int p = position2 + a;
                    wallpaperCell.setWallpaper(WallpapersListActivity.this.currentType, a, p < this.searchResult.size() ? this.searchResult.get(p) : null, 0, (Drawable) null, false);
                }
            } else if (itemViewType == 2) {
                ((GraySectionCell) holder.itemView).setText(LocaleController.getString("SearchByColor", R.string.SearchByColor));
            }
        }

        public int getItemViewType(int position) {
            if (!TextUtils.isEmpty(this.lastSearchString)) {
                return 0;
            }
            if (position == 0) {
                return 2;
            }
            return 1;
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        public int getItemCount() {
            return WallpapersListActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 0) {
                view = new TextCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (viewType == 1) {
                view = new ShadowSectionCell(this.mContext);
                view.setBackgroundColor(0);
            } else if (viewType != 3) {
                view = new WallpaperCell(this.mContext) {
                    /* access modifiers changed from: protected */
                    public void onWallpaperClick(Object wallPaper, int index) {
                        WallpapersListActivity.this.onItemClick(this, wallPaper, index);
                    }

                    /* access modifiers changed from: protected */
                    public boolean onWallpaperLongClick(Object wallPaper, int index) {
                        return WallpapersListActivity.this.onItemLongClick(this, wallPaper, index);
                    }
                };
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else {
                view = new TextInfoPrivacyCell(this.mContext);
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            long id;
            RecyclerView.ViewHolder viewHolder = holder;
            int i = position;
            int itemViewType = holder.getItemViewType();
            if (itemViewType == 0) {
                TextCell textCell = (TextCell) viewHolder.itemView;
                if (i == WallpapersListActivity.this.uploadImageRow) {
                    textCell.setTextAndIcon(LocaleController.getString("SelectFromGallery", R.string.SelectFromGallery), R.drawable.profile_photos, true);
                    textCell.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), 0.0f, 0.0f, Theme.getColor(Theme.key_windowBackgroundWhite)));
                } else if (i == WallpapersListActivity.this.setColorRow) {
                    textCell.setTextAndIcon(LocaleController.getString("SetColor", R.string.SetColor), R.drawable.menu_palette, false);
                } else if (i == WallpapersListActivity.this.resetRow) {
                    textCell.setText(LocaleController.getString("ResetChatBackgrounds", R.string.ResetChatBackgrounds), false);
                    textCell.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
                }
            } else if (itemViewType == 2) {
                WallpaperCell wallpaperCell = (WallpaperCell) viewHolder.itemView;
                int position2 = (i - WallpapersListActivity.this.wallPaperStartRow) * WallpapersListActivity.this.columnsCount;
                wallpaperCell.setParams(WallpapersListActivity.this.columnsCount, position2 == 0, position2 / WallpapersListActivity.this.columnsCount == WallpapersListActivity.this.totalWallpaperRows - 1);
                for (int a = 0; a < WallpapersListActivity.this.columnsCount; a++) {
                    int p = position2 + a;
                    Object wallPaper = p < WallpapersListActivity.this.wallPapers.size() ? WallpapersListActivity.this.wallPapers.get(p) : null;
                    Object wallPaper2 = wallPaper;
                    wallpaperCell.setWallpaper(WallpapersListActivity.this.currentType, a, wallPaper, WallpapersListActivity.this.selectedBackground, (Drawable) null, false);
                    if (wallPaper2 instanceof TLRPC.TL_wallPaper) {
                        id = ((TLRPC.TL_wallPaper) wallPaper2).id;
                    } else {
                        id = 0;
                    }
                    if (WallpapersListActivity.this.actionBar.isActionModeShowed()) {
                        wallpaperCell.setChecked(a, WallpapersListActivity.this.selectedWallPapers.indexOfKey(id) >= 0, !WallpapersListActivity.this.scrolling);
                    } else {
                        wallpaperCell.setChecked(a, false, !WallpapersListActivity.this.scrolling);
                    }
                }
            } else if (itemViewType == 3) {
                TextInfoPrivacyCell cell = (TextInfoPrivacyCell) viewHolder.itemView;
                if (i == WallpapersListActivity.this.resetInfoRow) {
                    cell.setText(LocaleController.getString("ResetChatBackgroundsInfo", R.string.ResetChatBackgroundsInfo));
                }
            }
        }

        public int getItemViewType(int position) {
            if (position == WallpapersListActivity.this.uploadImageRow || position == WallpapersListActivity.this.setColorRow || position == WallpapersListActivity.this.resetRow) {
                return 0;
            }
            if (position == WallpapersListActivity.this.sectionRow || position == WallpapersListActivity.this.resetSectionRow) {
                return 1;
            }
            if (position == WallpapersListActivity.this.resetInfoRow) {
                return 3;
            }
            return 2;
        }
    }

    /* access modifiers changed from: private */
    public void resetDefaultWallPaper() {
        this.selectedBackground = Theme.DEFAULT_BACKGROUND_ID;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putLong("selectedBackground2", this.selectedBackground);
        editor.remove("selectedBackgroundSlug");
        editor.putBoolean("selectedBackgroundBlurred", false);
        editor.putBoolean("selectedBackgroundMotion", false);
        editor.putInt("selectedColor", 0);
        editor.putFloat("selectedIntensity", 1.0f);
        editor.putLong("selectedPattern", 0);
        editor.putBoolean("overrideThemeWallpaper", true);
        editor.commit();
        Theme.reloadWallpaper();
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.fragmentView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText4), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteValueText), new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayIcon), new ThemeDescription((View) this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_graySectionText), new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_graySection), new ThemeDescription(this.searchEmptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_emptyListPlaceholder), new ThemeDescription(this.searchEmptyView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_progressCircle), new ThemeDescription(this.searchEmptyView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite)};
    }
}
