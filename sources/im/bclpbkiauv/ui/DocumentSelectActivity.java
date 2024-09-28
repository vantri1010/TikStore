package im.bclpbkiauv.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.StatFs;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.ui.DocumentSelectActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenu;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BackDrawable;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.GraySectionCell;
import im.bclpbkiauv.ui.cells.SharedDocumentCell;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.EmptyTextProgressView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.NumberTextView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class DocumentSelectActivity extends BaseFragment {
    private static final int done = 3;
    private ArrayList<View> actionModeViews = new ArrayList<>();
    private boolean allowMusic;
    private boolean canSelectOnlyImageFiles;
    /* access modifiers changed from: private */
    public File currentDir;
    /* access modifiers changed from: private */
    public DocumentSelectActivityDelegate delegate;
    private EmptyTextProgressView emptyView;
    /* access modifiers changed from: private */
    public ArrayList<HistoryEntry> history = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<ListItem> items = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private int maxSelectedFiles = -1;
    /* access modifiers changed from: private */
    public ChatActivity parentFragment;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context arg0, Intent intent) {
            Runnable r = new Runnable() {
                public final void run() {
                    DocumentSelectActivity.AnonymousClass1.this.lambda$onReceive$0$DocumentSelectActivity$1();
                }
            };
            if ("android.intent.action.MEDIA_UNMOUNTED".equals(intent.getAction())) {
                DocumentSelectActivity.this.listView.postDelayed(r, 1000);
            } else {
                r.run();
            }
        }

        public /* synthetic */ void lambda$onReceive$0$DocumentSelectActivity$1() {
            try {
                if (DocumentSelectActivity.this.currentDir == null) {
                    DocumentSelectActivity.this.listRoots();
                } else {
                    boolean unused = DocumentSelectActivity.this.listFiles(DocumentSelectActivity.this.currentDir);
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    };
    private boolean receiverRegistered = false;
    /* access modifiers changed from: private */
    public ArrayList<ListItem> recentItems = new ArrayList<>();
    /* access modifiers changed from: private */
    public boolean scrolling;
    /* access modifiers changed from: private */
    public HashMap<String, ListItem> selectedFiles = new HashMap<>();
    private NumberTextView selectedMessagesCountTextView;
    private long sizeLimit = 1610612736;

    public interface DocumentSelectActivityDelegate {
        void didSelectFiles(DocumentSelectActivity documentSelectActivity, ArrayList<String> arrayList, boolean z, int i);

        void startDocumentSelectActivity();

        void startMusicSelectActivity(BaseFragment baseFragment);

        /* renamed from: im.bclpbkiauv.ui.DocumentSelectActivity$DocumentSelectActivityDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$startMusicSelectActivity(DocumentSelectActivityDelegate _this, BaseFragment parentFragment) {
            }
        }
    }

    private class ListItem {
        String ext;
        File file;
        int icon;
        String subtitle;
        String thumb;
        String title;

        private ListItem() {
            this.subtitle = "";
            this.ext = "";
        }
    }

    private class HistoryEntry {
        File dir;
        int scrollItem;
        int scrollOffset;
        String title;

        private HistoryEntry() {
        }
    }

    public DocumentSelectActivity(boolean music) {
        this.allowMusic = music;
    }

    public boolean onFragmentCreate() {
        loadRecentFiles();
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        try {
            if (this.receiverRegistered) {
                ApplicationLoader.applicationContext.unregisterReceiver(this.receiver);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
        if (!this.receiverRegistered) {
            this.receiverRegistered = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.MEDIA_BAD_REMOVAL");
            filter.addAction("android.intent.action.MEDIA_CHECKING");
            filter.addAction("android.intent.action.MEDIA_EJECT");
            filter.addAction("android.intent.action.MEDIA_MOUNTED");
            filter.addAction("android.intent.action.MEDIA_NOFS");
            filter.addAction("android.intent.action.MEDIA_REMOVED");
            filter.addAction("android.intent.action.MEDIA_SHARED");
            filter.addAction("android.intent.action.MEDIA_UNMOUNTABLE");
            filter.addAction("android.intent.action.MEDIA_UNMOUNTED");
            filter.addDataScheme("file");
            ApplicationLoader.applicationContext.registerReceiver(this.receiver, filter);
        }
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("SelectFile", R.string.SelectFile));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    if (DocumentSelectActivity.this.actionBar.isActionModeShowed()) {
                        DocumentSelectActivity.this.selectedFiles.clear();
                        DocumentSelectActivity.this.actionBar.hideActionMode();
                        int count = DocumentSelectActivity.this.listView.getChildCount();
                        for (int a = 0; a < count; a++) {
                            View child = DocumentSelectActivity.this.listView.getChildAt(a);
                            if (child instanceof SharedDocumentCell) {
                                ((SharedDocumentCell) child).setChecked(false, true);
                            }
                        }
                        return;
                    }
                    DocumentSelectActivity.this.finishFragment();
                } else if (id == 3 && DocumentSelectActivity.this.delegate != null) {
                    ArrayList<String> files = new ArrayList<>(DocumentSelectActivity.this.selectedFiles.keySet());
                    if (DocumentSelectActivity.this.parentFragment == null || !DocumentSelectActivity.this.parentFragment.isInScheduleMode()) {
                        DocumentSelectActivity.this.delegate.didSelectFiles(DocumentSelectActivity.this, files, true, 0);
                    } else {
                        AlertsCreator.createScheduleDatePickerDialog(DocumentSelectActivity.this.getParentActivity(), UserObject.isUserSelf(DocumentSelectActivity.this.parentFragment.getCurrentUser()), new AlertsCreator.ScheduleDatePickerDelegate(files) {
                            private final /* synthetic */ ArrayList f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void didSelectDate(boolean z, int i) {
                                DocumentSelectActivity.AnonymousClass2.this.lambda$onItemClick$0$DocumentSelectActivity$2(this.f$1, z, i);
                            }
                        });
                    }
                }
            }

            public /* synthetic */ void lambda$onItemClick$0$DocumentSelectActivity$2(ArrayList files, boolean notify, int scheduleDate) {
                DocumentSelectActivity.this.delegate.didSelectFiles(DocumentSelectActivity.this, files, notify, scheduleDate);
            }
        });
        this.selectedFiles.clear();
        this.actionModeViews.clear();
        ActionBarMenu actionMode = this.actionBar.createActionMode();
        NumberTextView numberTextView = new NumberTextView(actionMode.getContext());
        this.selectedMessagesCountTextView = numberTextView;
        numberTextView.setTextSize(18);
        this.selectedMessagesCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.selectedMessagesCountTextView.setTextColor(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon));
        this.selectedMessagesCountTextView.setOnTouchListener($$Lambda$DocumentSelectActivity$Mk0pojmoSaJW1qAC0QPlf87XmMI.INSTANCE);
        actionMode.addView(this.selectedMessagesCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 65, 0, 0, 0));
        this.actionModeViews.add(actionMode.addItemWithWidth(3, R.drawable.ic_ab_done, AndroidUtilities.dp(54.0f)));
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.showTextView();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        this.listView.setEmptyView(this.emptyView);
        RecyclerListView recyclerListView3 = this.listView;
        ListAdapter listAdapter2 = new ListAdapter(context);
        this.listAdapter = listAdapter2;
        recyclerListView3.setAdapter(listAdapter2);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                boolean unused = DocumentSelectActivity.this.scrolling = newState != 0;
            }
        });
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
            public final boolean onItemClick(View view, int i) {
                return DocumentSelectActivity.this.lambda$createView$1$DocumentSelectActivity(view, i);
            }
        });
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                DocumentSelectActivity.this.lambda$createView$3$DocumentSelectActivity(view, i);
            }
        });
        listRoots();
        return this.fragmentView;
    }

    static /* synthetic */ boolean lambda$createView$0(View v, MotionEvent event) {
        return true;
    }

    public /* synthetic */ boolean lambda$createView$1$DocumentSelectActivity(View view, int position) {
        ListItem item;
        int i;
        if (this.actionBar.isActionModeShowed() || (item = this.listAdapter.getItem(position)) == null) {
            return false;
        }
        File file = item.file;
        if (file != null && !file.isDirectory()) {
            if (!file.canRead()) {
                showErrorBox(LocaleController.getString("AccessError", R.string.AccessError));
                return false;
            } else if (!this.canSelectOnlyImageFiles || item.thumb != null) {
                if (this.sizeLimit != 0) {
                    long length = file.length();
                    long j = this.sizeLimit;
                    if (length > j) {
                        showErrorBox(LocaleController.formatString("FileUploadLimit", R.string.FileUploadLimit, AndroidUtilities.formatFileSize(j)));
                        return false;
                    }
                }
                if (this.maxSelectedFiles >= 0 && this.selectedFiles.size() >= (i = this.maxSelectedFiles)) {
                    showErrorBox(LocaleController.formatString("PassportUploadMaxReached", R.string.PassportUploadMaxReached, LocaleController.formatPluralString("Files", i)));
                    return false;
                } else if (file.length() == 0) {
                    return false;
                } else {
                    this.selectedFiles.put(file.toString(), item);
                    this.selectedMessagesCountTextView.setNumber(1, false);
                    AnimatorSet animatorSet = new AnimatorSet();
                    ArrayList<Animator> animators = new ArrayList<>();
                    for (int a = 0; a < this.actionModeViews.size(); a++) {
                        View view2 = this.actionModeViews.get(a);
                        AndroidUtilities.clearDrawableAnimation(view2);
                        animators.add(ObjectAnimator.ofFloat(view2, "scaleY", new float[]{0.1f, 1.0f}));
                    }
                    animatorSet.playTogether(animators);
                    animatorSet.setDuration(250);
                    animatorSet.start();
                    this.scrolling = false;
                    if (view instanceof SharedDocumentCell) {
                        ((SharedDocumentCell) view).setChecked(true, true);
                    }
                    this.actionBar.showActionMode();
                }
            } else {
                showErrorBox(LocaleController.formatString("PassportUploadNotImage", R.string.PassportUploadNotImage, new Object[0]));
                return false;
            }
        }
        return true;
    }

    public /* synthetic */ void lambda$createView$3$DocumentSelectActivity(View view, int position) {
        int i;
        ListItem item = this.listAdapter.getItem(position);
        if (item != null) {
            File file = item.file;
            if (file == null) {
                if (item.icon == R.drawable.ic_storage_gallery) {
                    DocumentSelectActivityDelegate documentSelectActivityDelegate = this.delegate;
                    if (documentSelectActivityDelegate != null) {
                        documentSelectActivityDelegate.startDocumentSelectActivity();
                    }
                    finishFragment(false);
                } else if (item.icon == R.drawable.ic_storage_music) {
                    DocumentSelectActivityDelegate documentSelectActivityDelegate2 = this.delegate;
                    if (documentSelectActivityDelegate2 != null) {
                        documentSelectActivityDelegate2.startMusicSelectActivity(this);
                    }
                } else {
                    ArrayList<HistoryEntry> arrayList = this.history;
                    HistoryEntry he = arrayList.remove(arrayList.size() - 1);
                    this.actionBar.setTitle(he.title);
                    if (he.dir != null) {
                        listFiles(he.dir);
                    } else {
                        listRoots();
                    }
                    this.layoutManager.scrollToPositionWithOffset(he.scrollItem, he.scrollOffset);
                }
            } else if (file.isDirectory()) {
                HistoryEntry he2 = new HistoryEntry();
                he2.scrollItem = this.layoutManager.findLastVisibleItemPosition();
                View topView = this.layoutManager.findViewByPosition(he2.scrollItem);
                if (topView != null) {
                    he2.scrollOffset = topView.getTop();
                }
                he2.dir = this.currentDir;
                he2.title = this.actionBar.getTitle();
                this.history.add(he2);
                if (!listFiles(file)) {
                    this.history.remove(he2);
                } else {
                    this.actionBar.setTitle(item.title);
                }
            } else {
                if (!file.canRead()) {
                    showErrorBox(LocaleController.getString("AccessError", R.string.AccessError));
                    file = new File("/mnt/sdcard");
                }
                if (!this.canSelectOnlyImageFiles || item.thumb != null) {
                    if (this.sizeLimit != 0) {
                        long length = file.length();
                        long j = this.sizeLimit;
                        if (length > j) {
                            showErrorBox(LocaleController.formatString("FileUploadLimit", R.string.FileUploadLimit, AndroidUtilities.formatFileSize(j)));
                            return;
                        }
                    }
                    if (file.length() != 0) {
                        if (this.actionBar.isActionModeShowed()) {
                            if (this.selectedFiles.containsKey(file.toString())) {
                                this.selectedFiles.remove(file.toString());
                            } else if (this.maxSelectedFiles < 0 || this.selectedFiles.size() < (i = this.maxSelectedFiles)) {
                                this.selectedFiles.put(file.toString(), item);
                            } else {
                                showErrorBox(LocaleController.formatString("PassportUploadMaxReached", R.string.PassportUploadMaxReached, LocaleController.formatPluralString("Files", i)));
                                return;
                            }
                            if (this.selectedFiles.isEmpty()) {
                                this.actionBar.hideActionMode();
                            } else {
                                this.selectedMessagesCountTextView.setNumber(this.selectedFiles.size(), true);
                            }
                            this.scrolling = false;
                            if (view instanceof SharedDocumentCell) {
                                ((SharedDocumentCell) view).setChecked(this.selectedFiles.containsKey(item.file.toString()), true);
                            }
                        } else if (this.delegate != null) {
                            ArrayList<String> files = new ArrayList<>();
                            files.add(file.getAbsolutePath());
                            ChatActivity chatActivity = this.parentFragment;
                            if (chatActivity == null || !chatActivity.isInScheduleMode()) {
                                this.delegate.didSelectFiles(this, files, true, 0);
                            } else {
                                AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), UserObject.isUserSelf(this.parentFragment.getCurrentUser()), new AlertsCreator.ScheduleDatePickerDelegate(files) {
                                    private final /* synthetic */ ArrayList f$1;

                                    {
                                        this.f$1 = r2;
                                    }

                                    public final void didSelectDate(boolean z, int i) {
                                        DocumentSelectActivity.this.lambda$null$2$DocumentSelectActivity(this.f$1, z, i);
                                    }
                                });
                            }
                        }
                    }
                } else {
                    showErrorBox(LocaleController.formatString("PassportUploadNotImage", R.string.PassportUploadNotImage, new Object[0]));
                }
            }
        }
    }

    public /* synthetic */ void lambda$null$2$DocumentSelectActivity(ArrayList files, boolean notify, int scheduleDate) {
        this.delegate.didSelectFiles(this, files, notify, scheduleDate);
    }

    public void setChatActivity(ChatActivity chatActivity) {
        this.parentFragment = chatActivity;
    }

    public void setMaxSelectedFiles(int value) {
        this.maxSelectedFiles = value;
    }

    public void setCanSelectOnlyImageFiles(boolean value) {
        this.canSelectOnlyImageFiles = true;
    }

    public void loadRecentFiles() {
        try {
            File[] files = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).listFiles();
            for (File file : files) {
                if (!file.isDirectory()) {
                    ListItem item = new ListItem();
                    item.title = file.getName();
                    item.file = file;
                    String fname = file.getName();
                    String[] sp = fname.split("\\.");
                    item.ext = sp.length > 1 ? sp[sp.length - 1] : "?";
                    item.subtitle = AndroidUtilities.formatFileSize(file.length());
                    String fname2 = fname.toLowerCase();
                    if (fname2.endsWith(".jpg") || fname2.endsWith(".png") || fname2.endsWith(".gif") || fname2.endsWith(".jpeg")) {
                        item.thumb = file.getAbsolutePath();
                    }
                    this.recentItems.add(item);
                }
            }
            Collections.sort(this.recentItems, $$Lambda$DocumentSelectActivity$tS19qRZlloYX85jUDFT6fBAxmNI.INSTANCE);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    static /* synthetic */ int lambda$loadRecentFiles$4(ListItem o1, ListItem o2) {
        long lm = o1.file.lastModified();
        long rm = o2.file.lastModified();
        if (lm == rm) {
            return 0;
        }
        if (lm > rm) {
            return -1;
        }
        return 1;
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
        fixLayoutInternal();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            recyclerListView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    DocumentSelectActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                    DocumentSelectActivity.this.fixLayoutInternal();
                    return true;
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void fixLayoutInternal() {
        if (this.selectedMessagesCountTextView != null) {
            if (AndroidUtilities.isTablet() || ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != 2) {
                this.selectedMessagesCountTextView.setTextSize(20);
            } else {
                this.selectedMessagesCountTextView.setTextSize(18);
            }
        }
    }

    public boolean onBackPressed() {
        if (this.history.size() <= 0) {
            return super.onBackPressed();
        }
        ArrayList<HistoryEntry> arrayList = this.history;
        HistoryEntry he = arrayList.remove(arrayList.size() - 1);
        this.actionBar.setTitle(he.title);
        if (he.dir != null) {
            listFiles(he.dir);
        } else {
            listRoots();
        }
        this.layoutManager.scrollToPositionWithOffset(he.scrollItem, he.scrollOffset);
        return false;
    }

    public void setDelegate(DocumentSelectActivityDelegate delegate2) {
        this.delegate = delegate2;
    }

    /* access modifiers changed from: private */
    public boolean listFiles(File dir) {
        if (dir.canRead()) {
            try {
                File[] files = dir.listFiles();
                if (files == null) {
                    showErrorBox(LocaleController.getString("UnknownError", R.string.UnknownError));
                    return false;
                }
                this.currentDir = dir;
                this.items.clear();
                Arrays.sort(files, $$Lambda$DocumentSelectActivity$jPYrcK8reBuuI_Seapo4kxhYM.INSTANCE);
                for (File file : files) {
                    if (file.getName().indexOf(46) != 0) {
                        ListItem item = new ListItem();
                        item.title = file.getName();
                        item.file = file;
                        if (file.isDirectory()) {
                            item.icon = R.drawable.ic_directory;
                            item.subtitle = LocaleController.getString("Folder", R.string.Folder);
                        } else {
                            String fname = file.getName();
                            String[] sp = fname.split("\\.");
                            item.ext = sp.length > 1 ? sp[sp.length - 1] : "?";
                            item.subtitle = AndroidUtilities.formatFileSize(file.length());
                            String fname2 = fname.toLowerCase();
                            if (fname2.endsWith(".jpg") || fname2.endsWith(".png") || fname2.endsWith(".gif") || fname2.endsWith(".jpeg")) {
                                item.thumb = file.getAbsolutePath();
                            }
                        }
                        this.items.add(item);
                    }
                }
                ListItem item2 = new ListItem();
                item2.title = "..";
                if (this.history.size() > 0) {
                    ArrayList<HistoryEntry> arrayList = this.history;
                    HistoryEntry entry = arrayList.get(arrayList.size() - 1);
                    if (entry.dir == null) {
                        item2.subtitle = LocaleController.getString("Folder", R.string.Folder);
                    } else {
                        item2.subtitle = entry.dir.toString();
                    }
                } else {
                    item2.subtitle = LocaleController.getString("Folder", R.string.Folder);
                }
                item2.icon = R.drawable.ic_directory;
                item2.file = null;
                this.items.add(0, item2);
                AndroidUtilities.clearDrawableAnimation(this.listView);
                this.scrolling = true;
                this.listAdapter.notifyDataSetChanged();
                return true;
            } catch (Exception e) {
                showErrorBox(e.getLocalizedMessage());
                return false;
            }
        } else if ((dir.getAbsolutePath().startsWith(Environment.getExternalStorageDirectory().toString()) || dir.getAbsolutePath().startsWith("/sdcard") || dir.getAbsolutePath().startsWith("/mnt/sdcard")) && !Environment.getExternalStorageState().equals("mounted") && !Environment.getExternalStorageState().equals("mounted_ro")) {
            this.currentDir = dir;
            this.items.clear();
            if ("shared".equals(Environment.getExternalStorageState())) {
                this.emptyView.setText(LocaleController.getString("UsbActive", R.string.UsbActive));
            } else {
                this.emptyView.setText(LocaleController.getString("NotMounted", R.string.NotMounted));
            }
            AndroidUtilities.clearDrawableAnimation(this.listView);
            this.scrolling = true;
            this.listAdapter.notifyDataSetChanged();
            return true;
        } else {
            showErrorBox(LocaleController.getString("AccessError", R.string.AccessError));
            return false;
        }
    }

    static /* synthetic */ int lambda$listFiles$5(File lhs, File rhs) {
        if (lhs.isDirectory() != rhs.isDirectory()) {
            return lhs.isDirectory() ? -1 : 1;
        }
        return lhs.getName().compareToIgnoreCase(rhs.getName());
    }

    private void showErrorBox(String error) {
        if (getParentActivity() != null) {
            new AlertDialog.Builder((Context) getParentActivity()).setTitle(LocaleController.getString("AppName", R.string.AppName)).setMessage(error).setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null).show();
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x0133 A[Catch:{ Exception -> 0x015f }] */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x013a A[Catch:{ Exception -> 0x015f }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void listRoots() {
        /*
            r16 = this;
            r1 = r16
            r2 = 0
            r1.currentDir = r2
            java.util.ArrayList<im.bclpbkiauv.ui.DocumentSelectActivity$ListItem> r0 = r1.items
            r0.clear()
            java.util.HashSet r0 = new java.util.HashSet
            r0.<init>()
            r3 = r0
            java.io.File r0 = android.os.Environment.getExternalStorageDirectory()
            java.lang.String r4 = r0.getPath()
            boolean r5 = android.os.Environment.isExternalStorageRemovable()
            java.lang.String r6 = android.os.Environment.getExternalStorageState()
            java.lang.String r0 = "mounted"
            boolean r0 = r6.equals(r0)
            r7 = 2131231110(0x7f080186, float:1.8078292E38)
            r8 = 2131693713(0x7f0f1091, float:1.9016562E38)
            java.lang.String r9 = "SdCard"
            if (r0 != 0) goto L_0x0038
            java.lang.String r0 = "mounted_ro"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x0070
        L_0x0038:
            im.bclpbkiauv.ui.DocumentSelectActivity$ListItem r0 = new im.bclpbkiauv.ui.DocumentSelectActivity$ListItem
            r0.<init>()
            boolean r10 = android.os.Environment.isExternalStorageRemovable()
            if (r10 == 0) goto L_0x004c
            java.lang.String r10 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r8)
            r0.title = r10
            r0.icon = r7
            goto L_0x005c
        L_0x004c:
            r10 = 2131691677(0x7f0f089d, float:1.9012433E38)
            java.lang.String r11 = "InternalStorage"
            java.lang.String r10 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r10)
            r0.title = r10
            r10 = 2131231160(0x7f0801b8, float:1.8078393E38)
            r0.icon = r10
        L_0x005c:
            java.lang.String r10 = r1.getRootSubtitle(r4)
            r0.subtitle = r10
            java.io.File r10 = android.os.Environment.getExternalStorageDirectory()
            r0.file = r10
            java.util.ArrayList<im.bclpbkiauv.ui.DocumentSelectActivity$ListItem> r10 = r1.items
            r10.add(r0)
            r3.add(r4)
        L_0x0070:
            r10 = 0
            java.io.BufferedReader r0 = new java.io.BufferedReader     // Catch:{ Exception -> 0x0183 }
            java.io.FileReader r11 = new java.io.FileReader     // Catch:{ Exception -> 0x0183 }
            java.lang.String r12 = "/proc/mounts"
            r11.<init>(r12)     // Catch:{ Exception -> 0x0183 }
            r0.<init>(r11)     // Catch:{ Exception -> 0x0183 }
            r10 = r0
        L_0x007e:
            java.lang.String r0 = r10.readLine()     // Catch:{ Exception -> 0x0183 }
            r11 = r0
            if (r0 == 0) goto L_0x0173
            java.lang.String r0 = "vfat"
            boolean r0 = r11.contains(r0)     // Catch:{ Exception -> 0x0183 }
            if (r0 != 0) goto L_0x0096
            java.lang.String r0 = "/mnt"
            boolean r0 = r11.contains(r0)     // Catch:{ Exception -> 0x0183 }
            if (r0 == 0) goto L_0x007e
        L_0x0096:
            boolean r0 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0183 }
            if (r0 == 0) goto L_0x009d
            im.bclpbkiauv.messenger.FileLog.d(r11)     // Catch:{ Exception -> 0x0183 }
        L_0x009d:
            java.util.StringTokenizer r0 = new java.util.StringTokenizer     // Catch:{ Exception -> 0x0183 }
            java.lang.String r12 = " "
            r0.<init>(r11, r12)     // Catch:{ Exception -> 0x0183 }
            r12 = r0
            java.lang.String r0 = r12.nextToken()     // Catch:{ Exception -> 0x0183 }
            r13 = r0
            java.lang.String r0 = r12.nextToken()     // Catch:{ Exception -> 0x0183 }
            boolean r14 = r3.contains(r0)     // Catch:{ Exception -> 0x0183 }
            if (r14 == 0) goto L_0x00b5
            goto L_0x007e
        L_0x00b5:
            java.lang.String r14 = "/dev/block/vold"
            boolean r14 = r11.contains(r14)     // Catch:{ Exception -> 0x0183 }
            if (r14 == 0) goto L_0x016b
            java.lang.String r14 = "/mnt/secure"
            boolean r14 = r11.contains(r14)     // Catch:{ Exception -> 0x0183 }
            if (r14 != 0) goto L_0x0167
            java.lang.String r14 = "/mnt/asec"
            boolean r14 = r11.contains(r14)     // Catch:{ Exception -> 0x0183 }
            if (r14 != 0) goto L_0x0167
            java.lang.String r14 = "/mnt/obb"
            boolean r14 = r11.contains(r14)     // Catch:{ Exception -> 0x0183 }
            if (r14 != 0) goto L_0x0167
            java.lang.String r14 = "/dev/mapper"
            boolean r14 = r11.contains(r14)     // Catch:{ Exception -> 0x0183 }
            if (r14 != 0) goto L_0x0167
            java.lang.String r14 = "tmpfs"
            boolean r14 = r11.contains(r14)     // Catch:{ Exception -> 0x0183 }
            if (r14 != 0) goto L_0x0167
            java.io.File r14 = new java.io.File     // Catch:{ Exception -> 0x0183 }
            r14.<init>(r0)     // Catch:{ Exception -> 0x0183 }
            boolean r14 = r14.isDirectory()     // Catch:{ Exception -> 0x0183 }
            if (r14 != 0) goto L_0x011e
            r14 = 47
            int r14 = r0.lastIndexOf(r14)     // Catch:{ Exception -> 0x0183 }
            r15 = -1
            if (r14 == r15) goto L_0x011e
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0183 }
            r15.<init>()     // Catch:{ Exception -> 0x0183 }
            java.lang.String r7 = "/storage/"
            r15.append(r7)     // Catch:{ Exception -> 0x0183 }
            int r7 = r14 + 1
            java.lang.String r7 = r0.substring(r7)     // Catch:{ Exception -> 0x0183 }
            r15.append(r7)     // Catch:{ Exception -> 0x0183 }
            java.lang.String r7 = r15.toString()     // Catch:{ Exception -> 0x0183 }
            java.io.File r15 = new java.io.File     // Catch:{ Exception -> 0x0183 }
            r15.<init>(r7)     // Catch:{ Exception -> 0x0183 }
            boolean r15 = r15.isDirectory()     // Catch:{ Exception -> 0x0183 }
            if (r15 == 0) goto L_0x011e
            r0 = r7
            goto L_0x011f
        L_0x011e:
            r7 = r0
        L_0x011f:
            r3.add(r7)     // Catch:{ Exception -> 0x0183 }
            im.bclpbkiauv.ui.DocumentSelectActivity$ListItem r0 = new im.bclpbkiauv.ui.DocumentSelectActivity$ListItem     // Catch:{ Exception -> 0x015f }
            r0.<init>()     // Catch:{ Exception -> 0x015f }
            java.lang.String r14 = r7.toLowerCase()     // Catch:{ Exception -> 0x015f }
            java.lang.String r15 = "sd"
            boolean r14 = r14.contains(r15)     // Catch:{ Exception -> 0x015f }
            if (r14 == 0) goto L_0x013a
            java.lang.String r14 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r8)     // Catch:{ Exception -> 0x015f }
            r0.title = r14     // Catch:{ Exception -> 0x015f }
            goto L_0x0145
        L_0x013a:
            java.lang.String r14 = "ExternalStorage"
            r15 = 2131691280(0x7f0f0710, float:1.9011627E38)
            java.lang.String r14 = im.bclpbkiauv.messenger.LocaleController.getString(r14, r15)     // Catch:{ Exception -> 0x015f }
            r0.title = r14     // Catch:{ Exception -> 0x015f }
        L_0x0145:
            r14 = 2131231110(0x7f080186, float:1.8078292E38)
            r0.icon = r14     // Catch:{ Exception -> 0x015d }
            java.lang.String r15 = r1.getRootSubtitle(r7)     // Catch:{ Exception -> 0x015d }
            r0.subtitle = r15     // Catch:{ Exception -> 0x015d }
            java.io.File r15 = new java.io.File     // Catch:{ Exception -> 0x015d }
            r15.<init>(r7)     // Catch:{ Exception -> 0x015d }
            r0.file = r15     // Catch:{ Exception -> 0x015d }
            java.util.ArrayList<im.bclpbkiauv.ui.DocumentSelectActivity$ListItem> r15 = r1.items     // Catch:{ Exception -> 0x015d }
            r15.add(r0)     // Catch:{ Exception -> 0x015d }
            goto L_0x016e
        L_0x015d:
            r0 = move-exception
            goto L_0x0163
        L_0x015f:
            r0 = move-exception
            r14 = 2131231110(0x7f080186, float:1.8078292E38)
        L_0x0163:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x0183 }
            goto L_0x016e
        L_0x0167:
            r14 = 2131231110(0x7f080186, float:1.8078292E38)
            goto L_0x016e
        L_0x016b:
            r14 = 2131231110(0x7f080186, float:1.8078292E38)
        L_0x016e:
            r7 = 2131231110(0x7f080186, float:1.8078292E38)
            goto L_0x007e
        L_0x0173:
            r10.close()     // Catch:{ Exception -> 0x0178 }
        L_0x0177:
            goto L_0x018d
        L_0x0178:
            r0 = move-exception
            r7 = r0
            r0 = r7
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0177
        L_0x017f:
            r0 = move-exception
            r2 = r0
            goto L_0x0248
        L_0x0183:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x017f }
            if (r10 == 0) goto L_0x018d
            r10.close()     // Catch:{ Exception -> 0x0178 }
            goto L_0x0177
        L_0x018d:
            im.bclpbkiauv.ui.DocumentSelectActivity$ListItem r0 = new im.bclpbkiauv.ui.DocumentSelectActivity$ListItem
            r0.<init>()
            r7 = r0
            java.lang.String r0 = "/"
            r7.title = r0
            r8 = 2131694119(0x7f0f1227, float:1.9017386E38)
            java.lang.String r9 = "SystemRoot"
            java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r8)
            r7.subtitle = r8
            r8 = 2131231108(0x7f080184, float:1.8078288E38)
            r7.icon = r8
            java.io.File r9 = new java.io.File
            r9.<init>(r0)
            r7.file = r9
            java.util.ArrayList<im.bclpbkiauv.ui.DocumentSelectActivity$ListItem> r0 = r1.items
            r0.add(r7)
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x01e3 }
            java.io.File r9 = android.os.Environment.getExternalStorageDirectory()     // Catch:{ Exception -> 0x01e3 }
            java.lang.String r11 = "Yixin"
            r0.<init>(r9, r11)     // Catch:{ Exception -> 0x01e3 }
            boolean r9 = r0.exists()     // Catch:{ Exception -> 0x01e3 }
            if (r9 == 0) goto L_0x01e2
            im.bclpbkiauv.ui.DocumentSelectActivity$ListItem r9 = new im.bclpbkiauv.ui.DocumentSelectActivity$ListItem     // Catch:{ Exception -> 0x01e3 }
            r9.<init>()     // Catch:{ Exception -> 0x01e3 }
            r7 = r9
            r9 = 2131689824(0x7f0f0160, float:1.9008674E38)
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r9)     // Catch:{ Exception -> 0x01e3 }
            r7.title = r9     // Catch:{ Exception -> 0x01e3 }
            java.lang.String r9 = r0.toString()     // Catch:{ Exception -> 0x01e3 }
            r7.subtitle = r9     // Catch:{ Exception -> 0x01e3 }
            r7.icon = r8     // Catch:{ Exception -> 0x01e3 }
            r7.file = r0     // Catch:{ Exception -> 0x01e3 }
            java.util.ArrayList<im.bclpbkiauv.ui.DocumentSelectActivity$ListItem> r8 = r1.items     // Catch:{ Exception -> 0x01e3 }
            r8.add(r7)     // Catch:{ Exception -> 0x01e3 }
        L_0x01e2:
            goto L_0x01e7
        L_0x01e3:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01e7:
            im.bclpbkiauv.ui.DocumentSelectActivity$ListItem r0 = new im.bclpbkiauv.ui.DocumentSelectActivity$ListItem
            r0.<init>()
            r7 = 2131691448(0x7f0f07b8, float:1.9011968E38)
            java.lang.String r8 = "Gallery"
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r7)
            r0.title = r7
            r7 = 2131691449(0x7f0f07b9, float:1.901197E38)
            java.lang.String r8 = "GalleryInfo"
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r7)
            r0.subtitle = r7
            r7 = 2131231161(0x7f0801b9, float:1.8078395E38)
            r0.icon = r7
            r0.file = r2
            java.util.ArrayList<im.bclpbkiauv.ui.DocumentSelectActivity$ListItem> r7 = r1.items
            r7.add(r0)
            boolean r7 = r1.allowMusic
            if (r7 == 0) goto L_0x023a
            im.bclpbkiauv.ui.DocumentSelectActivity$ListItem r7 = new im.bclpbkiauv.ui.DocumentSelectActivity$ListItem
            r7.<init>()
            r0 = r7
            r7 = 2131689956(0x7f0f01e4, float:1.9008942E38)
            java.lang.String r8 = "AttachMusic"
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r7)
            r0.title = r7
            r7 = 2131692077(0x7f0f0a2d, float:1.9013244E38)
            java.lang.String r8 = "MusicInfo"
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r7)
            r0.subtitle = r7
            r7 = 2131231162(0x7f0801ba, float:1.8078397E38)
            r0.icon = r7
            r0.file = r2
            java.util.ArrayList<im.bclpbkiauv.ui.DocumentSelectActivity$ListItem> r2 = r1.items
            r2.add(r0)
        L_0x023a:
            im.bclpbkiauv.ui.components.RecyclerListView r2 = r1.listView
            im.bclpbkiauv.messenger.AndroidUtilities.clearDrawableAnimation(r2)
            r2 = 1
            r1.scrolling = r2
            im.bclpbkiauv.ui.DocumentSelectActivity$ListAdapter r2 = r1.listAdapter
            r2.notifyDataSetChanged()
            return
        L_0x0248:
            if (r10 == 0) goto L_0x0254
            r10.close()     // Catch:{ Exception -> 0x024e }
            goto L_0x0254
        L_0x024e:
            r0 = move-exception
            r7 = r0
            r0 = r7
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0254:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.DocumentSelectActivity.listRoots():void");
    }

    private String getRootSubtitle(String path) {
        try {
            StatFs stat = new StatFs(path);
            long total = ((long) stat.getBlockCount()) * ((long) stat.getBlockSize());
            long free = ((long) stat.getAvailableBlocks()) * ((long) stat.getBlockSize());
            if (total == 0) {
                return "";
            }
            return LocaleController.formatString("FreeOfTotal", R.string.FreeOfTotal, AndroidUtilities.formatFileSize(free), AndroidUtilities.formatFileSize(total));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return path;
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() != 0;
        }

        public int getItemCount() {
            int count = DocumentSelectActivity.this.items.size();
            if (!DocumentSelectActivity.this.history.isEmpty() || DocumentSelectActivity.this.recentItems.isEmpty()) {
                return count;
            }
            return count + DocumentSelectActivity.this.recentItems.size() + 1;
        }

        public ListItem getItem(int position) {
            int position2;
            if (position < DocumentSelectActivity.this.items.size()) {
                return (ListItem) DocumentSelectActivity.this.items.get(position);
            }
            if (!DocumentSelectActivity.this.history.isEmpty() || DocumentSelectActivity.this.recentItems.isEmpty() || position == DocumentSelectActivity.this.items.size() || (position2 = position - (DocumentSelectActivity.this.items.size() + 1)) >= DocumentSelectActivity.this.recentItems.size()) {
                return null;
            }
            return (ListItem) DocumentSelectActivity.this.recentItems.get(position2);
        }

        public int getItemViewType(int position) {
            return getItem(position) != null ? 1 : 0;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType != 0) {
                view = new SharedDocumentCell(this.mContext);
            } else {
                view = new GraySectionCell(this.mContext);
                ((GraySectionCell) view).setText(LocaleController.getString("Recent", R.string.Recent));
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 1) {
                ListItem item = getItem(position);
                SharedDocumentCell documentCell = (SharedDocumentCell) holder.itemView;
                if (item.icon != 0) {
                    documentCell.setTextAndValueAndTypeAndThumb(item.title, item.subtitle, (String) null, (String) null, item.icon);
                } else {
                    documentCell.setTextAndValueAndTypeAndThumb(item.title, item.subtitle, item.ext.toUpperCase().substring(0, Math.min(item.ext.length(), 4)), item.thumb, 0);
                }
                if (item.file == null || !DocumentSelectActivity.this.actionBar.isActionModeShowed()) {
                    documentCell.setChecked(false, true ^ DocumentSelectActivity.this.scrolling);
                } else {
                    documentCell.setChecked(DocumentSelectActivity.this.selectedFiles.containsKey(item.file.toString()), true ^ DocumentSelectActivity.this.scrolling);
                }
            }
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_emptyListPlaceholder), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarActionModeDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarActionModeDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_TOPBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarActionModeDefaultTop), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarActionModeDefaultSelector), new ThemeDescription(this.selectedMessagesCountTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarActionModeDefaultIcon), new ThemeDescription((View) this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_graySectionText), new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_graySection), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"dateTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText3), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_checkbox), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_checkboxCheck), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"thumbImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_files_folderIcon), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{SharedDocumentCell.class}, new String[]{"thumbImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_files_folderIconBackground), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"extTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_files_iconText)};
    }
}
