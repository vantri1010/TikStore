package im.bclpbkiauv.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLoader;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.CheckBoxCell;
import im.bclpbkiauv.ui.cells.TextInfoPrivacyCell;
import im.bclpbkiauv.ui.cells.TextSettingsCell;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RecyclerListView;
import java.io.File;

public class CacheControlActivity extends BaseFragment {
    private long audioSize = -1;
    /* access modifiers changed from: private */
    public int cacheInfoRow;
    /* access modifiers changed from: private */
    public int cacheRow;
    private long cacheSize = -1;
    /* access modifiers changed from: private */
    public boolean calculating = true;
    private volatile boolean canceled = false;
    private boolean[] clear = new boolean[6];
    /* access modifiers changed from: private */
    public int databaseInfoRow;
    /* access modifiers changed from: private */
    public int databaseRow;
    /* access modifiers changed from: private */
    public long databaseSize = -1;
    private long documentsSize = -1;
    /* access modifiers changed from: private */
    public int keepMediaInfoRow;
    /* access modifiers changed from: private */
    public int keepMediaRow;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private long musicSize = -1;
    private long photoSize = -1;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public long totalSize = -1;
    private long videoSize = -1;

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.keepMediaRow = 0;
        int i2 = i + 1;
        this.rowCount = i2;
        this.keepMediaInfoRow = i;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.cacheRow = i2;
        int i4 = i3 + 1;
        this.rowCount = i4;
        this.cacheInfoRow = i3;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.databaseRow = i4;
        this.rowCount = i5 + 1;
        this.databaseInfoRow = i5;
        this.databaseSize = MessagesStorage.getInstance(this.currentAccount).getDatabaseSize();
        Utilities.globalQueue.postRunnable(new Runnable() {
            public final void run() {
                CacheControlActivity.this.lambda$onFragmentCreate$1$CacheControlActivity();
            }
        });
        return true;
    }

    public /* synthetic */ void lambda$onFragmentCreate$1$CacheControlActivity() {
        this.cacheSize = getDirectorySize(FileLoader.checkDirectory(4), 0);
        if (!this.canceled) {
            this.photoSize = getDirectorySize(FileLoader.checkDirectory(0), 0);
            if (!this.canceled) {
                this.videoSize = getDirectorySize(FileLoader.checkDirectory(2), 0);
                if (!this.canceled) {
                    this.documentsSize = getDirectorySize(FileLoader.checkDirectory(3), 1);
                    if (!this.canceled) {
                        this.musicSize = getDirectorySize(FileLoader.checkDirectory(3), 2);
                        if (!this.canceled) {
                            long directorySize = getDirectorySize(FileLoader.checkDirectory(1), 0);
                            this.audioSize = directorySize;
                            this.totalSize = this.cacheSize + this.videoSize + directorySize + this.photoSize + this.documentsSize + this.musicSize;
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public final void run() {
                                    CacheControlActivity.this.lambda$null$0$CacheControlActivity();
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    public /* synthetic */ void lambda$null$0$CacheControlActivity() {
        this.calculating = false;
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        this.canceled = true;
    }

    private long getDirectorySize(File dir, int documentsMusicType) {
        if (dir == null || this.canceled) {
            return 0;
        }
        if (dir.isDirectory()) {
            return Utilities.getDirSize(dir.getAbsolutePath(), documentsMusicType);
        }
        if (dir.isFile()) {
            return 0 + dir.length();
        }
        return 0;
    }

    private void cleanupFolders() {
        AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
        progressDialog.setCanCancel(false);
        progressDialog.show();
        Utilities.globalQueue.postRunnable(new Runnable(progressDialog) {
            private final /* synthetic */ AlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                CacheControlActivity.this.lambda$cleanupFolders$3$CacheControlActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$cleanupFolders$3$CacheControlActivity(AlertDialog progressDialog) {
        boolean imagesCleared = false;
        for (int a = 0; a < 6; a++) {
            if (this.clear[a]) {
                int type = -1;
                int documentsMusicType = 0;
                if (a == 0) {
                    type = 0;
                } else if (a == 1) {
                    type = 2;
                } else if (a == 2) {
                    type = 3;
                    documentsMusicType = 1;
                } else if (a == 3) {
                    type = 3;
                    documentsMusicType = 2;
                } else if (a == 4) {
                    type = 1;
                } else if (a == 5) {
                    type = 4;
                }
                if (type != -1) {
                    File file = FileLoader.checkDirectory(type);
                    if (file != null) {
                        Utilities.clearDir(file.getAbsolutePath(), documentsMusicType, Long.MAX_VALUE);
                    }
                    if (type == 4) {
                        this.cacheSize = getDirectorySize(FileLoader.checkDirectory(4), documentsMusicType);
                        imagesCleared = true;
                    } else if (type == 1) {
                        this.audioSize = getDirectorySize(FileLoader.checkDirectory(1), documentsMusicType);
                    } else if (type == 3) {
                        if (documentsMusicType == 1) {
                            this.documentsSize = getDirectorySize(FileLoader.checkDirectory(3), documentsMusicType);
                        } else {
                            this.musicSize = getDirectorySize(FileLoader.checkDirectory(3), documentsMusicType);
                        }
                    } else if (type == 0) {
                        imagesCleared = true;
                        this.photoSize = getDirectorySize(FileLoader.checkDirectory(0), documentsMusicType);
                    } else if (type == 2) {
                        this.videoSize = getDirectorySize(FileLoader.checkDirectory(2), documentsMusicType);
                    }
                }
            }
        }
        this.totalSize = this.cacheSize + this.videoSize + this.audioSize + this.photoSize + this.documentsSize + this.musicSize;
        AndroidUtilities.runOnUIThread(new Runnable(imagesCleared, progressDialog) {
            private final /* synthetic */ boolean f$1;
            private final /* synthetic */ AlertDialog f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                CacheControlActivity.this.lambda$null$2$CacheControlActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$2$CacheControlActivity(boolean imagesClearedFinal, AlertDialog progressDialog) {
        if (imagesClearedFinal) {
            ImageLoader.getInstance().clearMemory();
        }
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("StorageUsage", R.string.StorageUsage));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    CacheControlActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                CacheControlActivity.this.lambda$createView$10$CacheControlActivity(view, i);
            }
        });
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$10$CacheControlActivity(View view, int position) {
        int i = position;
        if (getParentActivity() != null) {
            if (i == this.keepMediaRow) {
                BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity());
                builder.setItems(new CharSequence[]{LocaleController.formatPluralString("Days", 3), LocaleController.formatPluralString("Weeks", 1), LocaleController.formatPluralString("Months", 1), LocaleController.getString("KeepMediaForever", R.string.KeepMediaForever)}, new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        CacheControlActivity.this.lambda$null$4$CacheControlActivity(dialogInterface, i);
                    }
                });
                showDialog(builder.create());
            } else if (i == this.databaseRow) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
                builder2.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                builder2.setMessage(LocaleController.getString("LocalDatabaseClear", R.string.LocalDatabaseClear));
                builder2.setPositiveButton(LocaleController.getString("CacheClear", R.string.CacheClear), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        CacheControlActivity.this.lambda$null$7$CacheControlActivity(dialogInterface, i);
                    }
                });
                showDialog(builder2.create());
            } else if (i == this.cacheRow && this.totalSize > 0 && getParentActivity() != null) {
                BottomSheet.Builder builder3 = new BottomSheet.Builder(getParentActivity());
                builder3.setApplyTopPadding(false);
                builder3.setApplyBottomPadding(false);
                LinearLayout linearLayout = new LinearLayout(getParentActivity());
                linearLayout.setOrientation(1);
                for (int a = 0; a < 6; a++) {
                    long size = 0;
                    String name = null;
                    if (a == 0) {
                        long size2 = this.photoSize;
                        name = LocaleController.getString("LocalPhotoCache", R.string.LocalPhotoCache);
                        size = size2;
                    } else if (a == 1) {
                        long size3 = this.videoSize;
                        name = LocaleController.getString("LocalVideoCache", R.string.LocalVideoCache);
                        size = size3;
                    } else if (a == 2) {
                        long size4 = this.documentsSize;
                        name = LocaleController.getString("LocalDocumentCache", R.string.LocalDocumentCache);
                        size = size4;
                    } else if (a == 3) {
                        long size5 = this.musicSize;
                        name = LocaleController.getString("LocalMusicCache", R.string.LocalMusicCache);
                        size = size5;
                    } else if (a == 4) {
                        long size6 = this.audioSize;
                        name = LocaleController.getString("LocalAudioCache", R.string.LocalAudioCache);
                        size = size6;
                    } else if (a == 5) {
                        long size7 = this.cacheSize;
                        name = LocaleController.getString("LocalCache", R.string.LocalCache);
                        size = size7;
                    }
                    if (size > 0) {
                        this.clear[a] = true;
                        CheckBoxCell checkBoxCell = new CheckBoxCell(getParentActivity(), 1, 21);
                        checkBoxCell.setTag(Integer.valueOf(a));
                        checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                        linearLayout.addView(checkBoxCell, LayoutHelper.createLinear(-1, 50));
                        checkBoxCell.setText(name, AndroidUtilities.formatFileSize(size), true, true);
                        checkBoxCell.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                        checkBoxCell.setOnClickListener(new View.OnClickListener() {
                            public final void onClick(View view) {
                                CacheControlActivity.this.lambda$null$8$CacheControlActivity(view);
                            }
                        });
                    } else {
                        this.clear[a] = false;
                    }
                }
                BottomSheet.BottomSheetCell cell = new BottomSheet.BottomSheetCell(getParentActivity(), 1);
                cell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                cell.setTextAndIcon(LocaleController.getString("ClearMediaCache", R.string.ClearMediaCache).toUpperCase(), 0);
                cell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText));
                cell.setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        CacheControlActivity.this.lambda$null$9$CacheControlActivity(view);
                    }
                });
                linearLayout.addView(cell, LayoutHelper.createLinear(-1, 50));
                builder3.setCustomView(linearLayout);
                showDialog(builder3.create());
            }
        }
    }

    public /* synthetic */ void lambda$null$4$CacheControlActivity(DialogInterface dialog, int which) {
        if (which == 0) {
            SharedConfig.setKeepMedia(3);
        } else if (which == 1) {
            SharedConfig.setKeepMedia(0);
        } else if (which == 2) {
            SharedConfig.setKeepMedia(1);
        } else if (which == 3) {
            SharedConfig.setKeepMedia(2);
        }
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
        SharedConfig.checkKeepMedia();
    }

    public /* synthetic */ void lambda$null$7$CacheControlActivity(DialogInterface dialogInterface, int i) {
        if (getParentActivity() != null) {
            AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
            progressDialog.setCanCancel(false);
            progressDialog.show();
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable(progressDialog) {
                private final /* synthetic */ AlertDialog f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    CacheControlActivity.this.lambda$null$6$CacheControlActivity(this.f$1);
                }
            });
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0065, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:?, code lost:
        im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:196:0x03fa, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:197:0x03fb, code lost:
        r3 = r2;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x0233 A[SYNTHETIC, Splitter:B:125:0x0233] */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x0238 A[Catch:{ Exception -> 0x03e3, all -> 0x03dc }] */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x0313 A[Catch:{ Exception -> 0x035e, all -> 0x0357 }] */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x0330 A[SYNTHETIC, Splitter:B:148:0x0330] */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x0335 A[Catch:{ Exception -> 0x03e3, all -> 0x03dc }] */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x03fa A[ExcHandler: all (th java.lang.Throwable), PHI: r4 r6 r7 
      PHI: (r4v4 'cursor' im.bclpbkiauv.sqlite.SQLiteCursor) = (r4v0 'cursor' im.bclpbkiauv.sqlite.SQLiteCursor), (r4v6 'cursor' im.bclpbkiauv.sqlite.SQLiteCursor), (r4v6 'cursor' im.bclpbkiauv.sqlite.SQLiteCursor), (r4v6 'cursor' im.bclpbkiauv.sqlite.SQLiteCursor), (r4v6 'cursor' im.bclpbkiauv.sqlite.SQLiteCursor) binds: [B:1:0x000a, B:12:0x0061, B:16:0x0068, B:17:?, B:13:?] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r6v4 'state5' im.bclpbkiauv.sqlite.SQLitePreparedStatement) = (r6v0 'state5' im.bclpbkiauv.sqlite.SQLitePreparedStatement), (r6v5 'state5' im.bclpbkiauv.sqlite.SQLitePreparedStatement), (r6v5 'state5' im.bclpbkiauv.sqlite.SQLitePreparedStatement), (r6v5 'state5' im.bclpbkiauv.sqlite.SQLitePreparedStatement), (r6v5 'state5' im.bclpbkiauv.sqlite.SQLitePreparedStatement) binds: [B:1:0x000a, B:12:0x0061, B:16:0x0068, B:17:?, B:13:?] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r7v4 'state6' im.bclpbkiauv.sqlite.SQLitePreparedStatement) = (r7v0 'state6' im.bclpbkiauv.sqlite.SQLitePreparedStatement), (r7v5 'state6' im.bclpbkiauv.sqlite.SQLitePreparedStatement), (r7v5 'state6' im.bclpbkiauv.sqlite.SQLitePreparedStatement), (r7v5 'state6' im.bclpbkiauv.sqlite.SQLitePreparedStatement), (r7v5 'state6' im.bclpbkiauv.sqlite.SQLitePreparedStatement) binds: [B:1:0x000a, B:12:0x0061, B:16:0x0068, B:17:?, B:13:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:1:0x000a] */
    /* JADX WARNING: Removed duplicated region for block: B:203:0x0404  */
    /* JADX WARNING: Removed duplicated region for block: B:205:0x0409  */
    /* JADX WARNING: Removed duplicated region for block: B:207:0x040e  */
    /* JADX WARNING: Removed duplicated region for block: B:209:0x0413  */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x0424  */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x0429  */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x042e  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x0433  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$6$CacheControlActivity(im.bclpbkiauv.ui.actionbar.AlertDialog r30) {
        /*
            r29 = this;
            r1 = r29
            r2 = r30
            java.lang.String r3 = " AND mid != "
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            int r0 = r1.currentAccount     // Catch:{ Exception -> 0x03fd, all -> 0x03fa }
            im.bclpbkiauv.messenger.MessagesStorage r0 = im.bclpbkiauv.messenger.MessagesStorage.getInstance(r0)     // Catch:{ Exception -> 0x03fd, all -> 0x03fa }
            im.bclpbkiauv.sqlite.SQLiteDatabase r0 = r0.getDatabase()     // Catch:{ Exception -> 0x03fd, all -> 0x03fa }
            r8 = r0
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x03fd, all -> 0x03fa }
            r0.<init>()     // Catch:{ Exception -> 0x03fd, all -> 0x03fa }
            r9 = r0
            java.lang.String r0 = "SELECT did FROM dialogs WHERE 1"
            r10 = 0
            java.lang.Object[] r11 = new java.lang.Object[r10]     // Catch:{ Exception -> 0x03fd, all -> 0x03fa }
            im.bclpbkiauv.sqlite.SQLiteCursor r0 = r8.queryFinalized(r0, r11)     // Catch:{ Exception -> 0x03fd, all -> 0x03fa }
            r4 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x03fd, all -> 0x03fa }
            r0.<init>()     // Catch:{ Exception -> 0x03fd, all -> 0x03fa }
            r11 = r0
        L_0x002b:
            boolean r0 = r4.next()     // Catch:{ Exception -> 0x03fd, all -> 0x03fa }
            r12 = 1
            if (r0 == 0) goto L_0x004d
            long r13 = r4.longValue(r10)     // Catch:{ Exception -> 0x03fd, all -> 0x03fa }
            int r0 = (int) r13     // Catch:{ Exception -> 0x03fd, all -> 0x03fa }
            r15 = 32
            r17 = r11
            long r10 = r13 >> r15
            int r11 = (int) r10     // Catch:{ Exception -> 0x03fd, all -> 0x03fa }
            if (r0 == 0) goto L_0x0049
            if (r11 == r12) goto L_0x0049
            java.lang.Long r10 = java.lang.Long.valueOf(r13)     // Catch:{ Exception -> 0x03fd, all -> 0x03fa }
            r9.add(r10)     // Catch:{ Exception -> 0x03fd, all -> 0x03fa }
        L_0x0049:
            r11 = r17
            r10 = 0
            goto L_0x002b
        L_0x004d:
            r17 = r11
            r4.dispose()     // Catch:{ Exception -> 0x03fd, all -> 0x03fa }
            r4 = 0
            java.lang.String r0 = "REPLACE INTO messages_holes VALUES(?, ?, ?)"
            im.bclpbkiauv.sqlite.SQLitePreparedStatement r0 = r8.executeFast(r0)     // Catch:{ Exception -> 0x03fd, all -> 0x03fa }
            r6 = r0
            java.lang.String r0 = "REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)"
            im.bclpbkiauv.sqlite.SQLitePreparedStatement r0 = r8.executeFast(r0)     // Catch:{ Exception -> 0x03fd, all -> 0x03fa }
            r7 = r0
            r8.beginTransaction()     // Catch:{ Exception -> 0x0065, all -> 0x03fa }
            goto L_0x006b
        L_0x0065:
            r0 = move-exception
            r10 = r0
            r0 = r10
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x03fd, all -> 0x03fa }
        L_0x006b:
            r0 = 0
            r10 = r5
            r5 = r4
            r4 = r0
        L_0x006f:
            int r0 = r9.size()     // Catch:{ Exception -> 0x03f2, all -> 0x03ea }
            if (r4 >= r0) goto L_0x0373
            java.lang.Object r0 = r9.get(r4)     // Catch:{ Exception -> 0x036c, all -> 0x0365 }
            java.lang.Long r0 = (java.lang.Long) r0     // Catch:{ Exception -> 0x036c, all -> 0x0365 }
            r11 = r0
            r0 = 0
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x036c, all -> 0x0365 }
            r13.<init>()     // Catch:{ Exception -> 0x036c, all -> 0x0365 }
            java.lang.String r14 = "SELECT COUNT(mid) FROM messages WHERE uid = "
            r13.append(r14)     // Catch:{ Exception -> 0x036c, all -> 0x0365 }
            r13.append(r11)     // Catch:{ Exception -> 0x036c, all -> 0x0365 }
            java.lang.String r13 = r13.toString()     // Catch:{ Exception -> 0x036c, all -> 0x0365 }
            r14 = 0
            java.lang.Object[] r15 = new java.lang.Object[r14]     // Catch:{ Exception -> 0x036c, all -> 0x0365 }
            im.bclpbkiauv.sqlite.SQLiteCursor r13 = r8.queryFinalized(r13, r15)     // Catch:{ Exception -> 0x036c, all -> 0x0365 }
            r5 = r13
            boolean r13 = r5.next()     // Catch:{ Exception -> 0x036c, all -> 0x0365 }
            if (r13 == 0) goto L_0x00b0
            r13 = 0
            int r14 = r5.intValue(r13)     // Catch:{ Exception -> 0x00aa, all -> 0x00a4 }
            r0 = r14
            r13 = r0
            goto L_0x00b1
        L_0x00a4:
            r0 = move-exception
            r3 = r2
            r4 = r5
            r5 = r10
            goto L_0x0422
        L_0x00aa:
            r0 = move-exception
            r3 = r2
            r4 = r5
            r5 = r10
            goto L_0x03ff
        L_0x00b0:
            r13 = r0
        L_0x00b1:
            r5.dispose()     // Catch:{ Exception -> 0x036c, all -> 0x0365 }
            r0 = 2
            if (r13 > r0) goto L_0x00bd
            r23 = r3
            r20 = r9
            goto L_0x034c
        L_0x00bd:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x036c, all -> 0x0365 }
            r0.<init>()     // Catch:{ Exception -> 0x036c, all -> 0x0365 }
            java.lang.String r14 = "SELECT last_mid_i, last_mid FROM dialogs WHERE did = "
            r0.append(r14)     // Catch:{ Exception -> 0x036c, all -> 0x0365 }
            r0.append(r11)     // Catch:{ Exception -> 0x036c, all -> 0x0365 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x036c, all -> 0x0365 }
            r14 = 0
            java.lang.Object[] r15 = new java.lang.Object[r14]     // Catch:{ Exception -> 0x036c, all -> 0x0365 }
            im.bclpbkiauv.sqlite.SQLiteCursor r0 = r8.queryFinalized(r0, r15)     // Catch:{ Exception -> 0x036c, all -> 0x0365 }
            r5 = r0
            r14 = -1
            boolean r0 = r5.next()     // Catch:{ Exception -> 0x036c, all -> 0x0365 }
            if (r0 == 0) goto L_0x033a
            r15 = 0
            long r18 = r5.longValue(r15)     // Catch:{ Exception -> 0x036c, all -> 0x0365 }
            r20 = r18
            long r18 = r5.longValue(r12)     // Catch:{ Exception -> 0x036c, all -> 0x0365 }
            r22 = r18
            r15 = 0
            r18 = 0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x021d, all -> 0x0208 }
            r0.<init>()     // Catch:{ Exception -> 0x021d, all -> 0x0208 }
            java.lang.String r12 = "SELECT data FROM messages WHERE uid = "
            r0.append(r12)     // Catch:{ Exception -> 0x021d, all -> 0x0208 }
            r0.append(r11)     // Catch:{ Exception -> 0x021d, all -> 0x0208 }
            java.lang.String r12 = " AND mid IN ("
            r0.append(r12)     // Catch:{ Exception -> 0x021d, all -> 0x0208 }
            r12 = r9
            r24 = r10
            r9 = r20
            r0.append(r9)     // Catch:{ Exception -> 0x01fc, all -> 0x01ed }
            r20 = r12
            java.lang.String r12 = ","
            r0.append(r12)     // Catch:{ Exception -> 0x01e3, all -> 0x01d6 }
            r21 = r13
            r12 = r22
            r0.append(r12)     // Catch:{ Exception -> 0x01cf, all -> 0x01c6 }
            r22 = r14
            java.lang.String r14 = ")"
            r0.append(r14)     // Catch:{ Exception -> 0x01bf, all -> 0x01b6 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x01bf, all -> 0x01b6 }
            r23 = r15
            r14 = 0
            java.lang.Object[] r15 = new java.lang.Object[r14]     // Catch:{ Exception -> 0x01af, all -> 0x01a6 }
            im.bclpbkiauv.sqlite.SQLiteCursor r0 = r8.queryFinalized(r0, r15)     // Catch:{ Exception -> 0x01af, all -> 0x01a6 }
            r15 = r0
            r14 = r22
        L_0x012c:
            boolean r0 = r15.next()     // Catch:{ Exception -> 0x01a1, all -> 0x019a }
            if (r0 == 0) goto L_0x017a
            r22 = r14
            r14 = 0
            im.bclpbkiauv.tgnet.NativeByteBuffer r0 = r15.byteBufferValue(r14)     // Catch:{ Exception -> 0x0195, all -> 0x018e }
            r16 = r0
            r14 = r16
            if (r14 == 0) goto L_0x0173
            r2 = 0
            int r0 = r14.readInt32(r2)     // Catch:{ Exception -> 0x016c, all -> 0x0163 }
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = im.bclpbkiauv.tgnet.TLRPC.Message.TLdeserialize(r14, r0, r2)     // Catch:{ Exception -> 0x016c, all -> 0x0163 }
            int r2 = r1.currentAccount     // Catch:{ Exception -> 0x016c, all -> 0x0163 }
            im.bclpbkiauv.messenger.UserConfig r2 = im.bclpbkiauv.messenger.UserConfig.getInstance(r2)     // Catch:{ Exception -> 0x016c, all -> 0x0163 }
            int r2 = r2.clientUserId     // Catch:{ Exception -> 0x016c, all -> 0x0163 }
            r0.readAttachPath(r14, r2)     // Catch:{ Exception -> 0x016c, all -> 0x0163 }
            r14.reuse()     // Catch:{ Exception -> 0x016c, all -> 0x0163 }
            r18 = 0
            if (r0 == 0) goto L_0x015e
            int r2 = r0.id     // Catch:{ Exception -> 0x0195, all -> 0x018e }
            r14 = r2
            goto L_0x0160
        L_0x015e:
            r14 = r22
        L_0x0160:
            r2 = r30
            goto L_0x012c
        L_0x0163:
            r0 = move-exception
            r25 = r9
            r18 = r14
            r14 = r22
            goto L_0x032e
        L_0x016c:
            r0 = move-exception
            r18 = r14
            r14 = r22
            goto L_0x022e
        L_0x0173:
            r2 = r30
            r18 = r14
            r14 = r22
            goto L_0x012c
        L_0x017a:
            r22 = r14
            r15.dispose()     // Catch:{ Exception -> 0x0195, all -> 0x018e }
            r0 = 0
            if (r18 == 0) goto L_0x0185
            r18.reuse()     // Catch:{ Exception -> 0x03e3, all -> 0x03dc }
        L_0x0185:
            if (r0 == 0) goto L_0x018a
            r0.dispose()     // Catch:{ Exception -> 0x03e3, all -> 0x03dc }
        L_0x018a:
            r14 = r22
            goto L_0x023c
        L_0x018e:
            r0 = move-exception
            r25 = r9
            r14 = r22
            goto L_0x032e
        L_0x0195:
            r0 = move-exception
            r14 = r22
            goto L_0x022e
        L_0x019a:
            r0 = move-exception
            r22 = r14
            r25 = r9
            goto L_0x032e
        L_0x01a1:
            r0 = move-exception
            r22 = r14
            goto L_0x022e
        L_0x01a6:
            r0 = move-exception
            r25 = r9
            r14 = r22
            r15 = r23
            goto L_0x032e
        L_0x01af:
            r0 = move-exception
            r14 = r22
            r15 = r23
            goto L_0x022e
        L_0x01b6:
            r0 = move-exception
            r23 = r15
            r25 = r9
            r14 = r22
            goto L_0x032e
        L_0x01bf:
            r0 = move-exception
            r23 = r15
            r14 = r22
            goto L_0x022e
        L_0x01c6:
            r0 = move-exception
            r22 = r14
            r23 = r15
            r25 = r9
            goto L_0x032e
        L_0x01cf:
            r0 = move-exception
            r22 = r14
            r23 = r15
            goto L_0x022e
        L_0x01d6:
            r0 = move-exception
            r21 = r13
            r12 = r22
            r22 = r14
            r23 = r15
            r25 = r9
            goto L_0x032e
        L_0x01e3:
            r0 = move-exception
            r21 = r13
            r12 = r22
            r22 = r14
            r23 = r15
            goto L_0x022e
        L_0x01ed:
            r0 = move-exception
            r20 = r12
            r21 = r13
            r12 = r22
            r22 = r14
            r23 = r15
            r25 = r9
            goto L_0x032e
        L_0x01fc:
            r0 = move-exception
            r20 = r12
            r21 = r13
            r12 = r22
            r22 = r14
            r23 = r15
            goto L_0x022e
        L_0x0208:
            r0 = move-exception
            r24 = r10
            r27 = r20
            r20 = r9
            r21 = r13
            r9 = r27
            r12 = r22
            r22 = r14
            r23 = r15
            r25 = r9
            goto L_0x032e
        L_0x021d:
            r0 = move-exception
            r24 = r10
            r27 = r20
            r20 = r9
            r21 = r13
            r9 = r27
            r12 = r22
            r22 = r14
            r23 = r15
        L_0x022e:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x032b }
            if (r18 == 0) goto L_0x0236
            r18.reuse()     // Catch:{ Exception -> 0x03e3, all -> 0x03dc }
        L_0x0236:
            if (r15 == 0) goto L_0x023b
            r15.dispose()     // Catch:{ Exception -> 0x03e3, all -> 0x03dc }
        L_0x023b:
            r0 = r15
        L_0x023c:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x03e3, all -> 0x03dc }
            r2.<init>()     // Catch:{ Exception -> 0x03e3, all -> 0x03dc }
            java.lang.String r15 = "DELETE FROM messages WHERE uid = "
            r2.append(r15)     // Catch:{ Exception -> 0x03e3, all -> 0x03dc }
            r2.append(r11)     // Catch:{ Exception -> 0x03e3, all -> 0x03dc }
            r2.append(r3)     // Catch:{ Exception -> 0x03e3, all -> 0x03dc }
            r2.append(r9)     // Catch:{ Exception -> 0x03e3, all -> 0x03dc }
            r2.append(r3)     // Catch:{ Exception -> 0x03e3, all -> 0x03dc }
            r2.append(r12)     // Catch:{ Exception -> 0x03e3, all -> 0x03dc }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x03e3, all -> 0x03dc }
            im.bclpbkiauv.sqlite.SQLitePreparedStatement r2 = r8.executeFast(r2)     // Catch:{ Exception -> 0x03e3, all -> 0x03dc }
            im.bclpbkiauv.sqlite.SQLitePreparedStatement r15 = r2.stepThis()     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            r15.dispose()     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            r2 = 0
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            r15.<init>()     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            r22 = r0
            java.lang.String r0 = "DELETE FROM messages_holes WHERE uid = "
            r15.append(r0)     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            r15.append(r11)     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            java.lang.String r0 = r15.toString()     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            im.bclpbkiauv.sqlite.SQLitePreparedStatement r0 = r8.executeFast(r0)     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            r2 = r0
            im.bclpbkiauv.sqlite.SQLitePreparedStatement r0 = r2.stepThis()     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            r0.dispose()     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            r2 = 0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            r0.<init>()     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            java.lang.String r15 = "DELETE FROM bot_keyboard WHERE uid = "
            r0.append(r15)     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            r0.append(r11)     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            im.bclpbkiauv.sqlite.SQLitePreparedStatement r0 = r8.executeFast(r0)     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            r2 = r0
            im.bclpbkiauv.sqlite.SQLitePreparedStatement r0 = r2.stepThis()     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            r0.dispose()     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            r2 = 0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            r0.<init>()     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            java.lang.String r15 = "DELETE FROM media_counts_v2 WHERE uid = "
            r0.append(r15)     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            r0.append(r11)     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            im.bclpbkiauv.sqlite.SQLitePreparedStatement r0 = r8.executeFast(r0)     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            r2 = r0
            im.bclpbkiauv.sqlite.SQLitePreparedStatement r0 = r2.stepThis()     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            r0.dispose()     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            r2 = 0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            r0.<init>()     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            java.lang.String r15 = "DELETE FROM media_v2 WHERE uid = "
            r0.append(r15)     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            r0.append(r11)     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            im.bclpbkiauv.sqlite.SQLitePreparedStatement r0 = r8.executeFast(r0)     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            r2 = r0
            im.bclpbkiauv.sqlite.SQLitePreparedStatement r0 = r2.stepThis()     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            r0.dispose()     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            r2 = 0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            r0.<init>()     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            java.lang.String r15 = "DELETE FROM media_holes_v2 WHERE uid = "
            r0.append(r15)     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            r0.append(r11)     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            im.bclpbkiauv.sqlite.SQLitePreparedStatement r0 = r8.executeFast(r0)     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            r2 = r0
            im.bclpbkiauv.sqlite.SQLitePreparedStatement r0 = r2.stepThis()     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            r0.dispose()     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            r2 = 0
            int r0 = r1.currentAccount     // Catch:{ Exception -> 0x0323, all -> 0x031b }
            im.bclpbkiauv.messenger.MediaDataController r0 = im.bclpbkiauv.messenger.MediaDataController.getInstance(r0)     // Catch:{ Exception -> 0x0323, all -> 0x031b }
            r15 = r2
            r23 = r3
            long r2 = r11.longValue()     // Catch:{ Exception -> 0x035e, all -> 0x0357 }
            r25 = r9
            r9 = 0
            r0.clearBotKeyboard(r2, r9)     // Catch:{ Exception -> 0x035e, all -> 0x0357 }
            r0 = -1
            if (r14 == r0) goto L_0x0346
            long r2 = r11.longValue()     // Catch:{ Exception -> 0x035e, all -> 0x0357 }
            im.bclpbkiauv.messenger.MessagesStorage.createFirstHoles(r2, r6, r7, r14)     // Catch:{ Exception -> 0x035e, all -> 0x0357 }
            goto L_0x0346
        L_0x031b:
            r0 = move-exception
            r15 = r2
            r3 = r30
            r4 = r5
            r5 = r15
            goto L_0x0422
        L_0x0323:
            r0 = move-exception
            r15 = r2
            r3 = r30
            r4 = r5
            r5 = r15
            goto L_0x03ff
        L_0x032b:
            r0 = move-exception
            r25 = r9
        L_0x032e:
            if (r18 == 0) goto L_0x0333
            r18.reuse()     // Catch:{ Exception -> 0x03e3, all -> 0x03dc }
        L_0x0333:
            if (r15 == 0) goto L_0x0338
            r15.dispose()     // Catch:{ Exception -> 0x03e3, all -> 0x03dc }
        L_0x0338:
            throw r0     // Catch:{ Exception -> 0x03e3, all -> 0x03dc }
        L_0x033a:
            r23 = r3
            r20 = r9
            r24 = r10
            r21 = r13
            r22 = r14
            r15 = r24
        L_0x0346:
            r5.dispose()     // Catch:{ Exception -> 0x035e, all -> 0x0357 }
            r0 = 0
            r5 = r0
            r10 = r15
        L_0x034c:
            int r4 = r4 + 1
            r2 = r30
            r9 = r20
            r3 = r23
            r12 = 1
            goto L_0x006f
        L_0x0357:
            r0 = move-exception
            r3 = r30
            r4 = r5
            r5 = r15
            goto L_0x0422
        L_0x035e:
            r0 = move-exception
            r3 = r30
            r4 = r5
            r5 = r15
            goto L_0x03ff
        L_0x0365:
            r0 = move-exception
            r24 = r10
            r3 = r30
            goto L_0x03ee
        L_0x036c:
            r0 = move-exception
            r24 = r10
            r3 = r30
            goto L_0x03f6
        L_0x0373:
            r20 = r9
            r24 = r10
            r6.dispose()     // Catch:{ Exception -> 0x03e3, all -> 0x03dc }
            r6 = 0
            r7.dispose()     // Catch:{ Exception -> 0x03e3, all -> 0x03dc }
            r7 = 0
            r8.commitTransaction()     // Catch:{ Exception -> 0x03e3, all -> 0x03dc }
            java.lang.String r0 = "PRAGMA journal_size_limit = 0"
            im.bclpbkiauv.sqlite.SQLitePreparedStatement r0 = r8.executeFast(r0)     // Catch:{ Exception -> 0x03e3, all -> 0x03dc }
            r2 = r0
            im.bclpbkiauv.sqlite.SQLitePreparedStatement r0 = r2.stepThis()     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            r0.dispose()     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            r2 = 0
            java.lang.String r0 = "VACUUM"
            im.bclpbkiauv.sqlite.SQLitePreparedStatement r0 = r8.executeFast(r0)     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            r2 = r0
            im.bclpbkiauv.sqlite.SQLitePreparedStatement r0 = r2.stepThis()     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            r0.dispose()     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            r2 = 0
            java.lang.String r0 = "PRAGMA journal_size_limit = -1"
            im.bclpbkiauv.sqlite.SQLitePreparedStatement r0 = r8.executeFast(r0)     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            r2 = r0
            im.bclpbkiauv.sqlite.SQLitePreparedStatement r0 = r2.stepThis()     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            r0.dispose()     // Catch:{ Exception -> 0x03d6, all -> 0x03cf }
            r0 = 0
            if (r5 == 0) goto L_0x03b4
            r5.dispose()
        L_0x03b4:
            if (r0 == 0) goto L_0x03b9
            r0.dispose()
        L_0x03b9:
            if (r6 == 0) goto L_0x03be
            r6.dispose()
        L_0x03be:
            if (r7 == 0) goto L_0x03c3
            r7.dispose()
        L_0x03c3:
            im.bclpbkiauv.ui.-$$Lambda$CacheControlActivity$Vn91Yt-ji8K-u58G0lBA6LKFuvU r2 = new im.bclpbkiauv.ui.-$$Lambda$CacheControlActivity$Vn91Yt-ji8K-u58G0lBA6LKFuvU
            r3 = r30
            r2.<init>(r3)
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r2)
            goto L_0x0420
        L_0x03cf:
            r0 = move-exception
            r3 = r30
            r4 = r5
            r5 = r2
            goto L_0x0422
        L_0x03d6:
            r0 = move-exception
            r3 = r30
            r4 = r5
            r5 = r2
            goto L_0x03ff
        L_0x03dc:
            r0 = move-exception
            r3 = r30
            r4 = r5
            r5 = r24
            goto L_0x0422
        L_0x03e3:
            r0 = move-exception
            r3 = r30
            r4 = r5
            r5 = r24
            goto L_0x03ff
        L_0x03ea:
            r0 = move-exception
            r3 = r2
            r24 = r10
        L_0x03ee:
            r4 = r5
            r5 = r24
            goto L_0x0422
        L_0x03f2:
            r0 = move-exception
            r3 = r2
            r24 = r10
        L_0x03f6:
            r4 = r5
            r5 = r24
            goto L_0x03ff
        L_0x03fa:
            r0 = move-exception
            r3 = r2
            goto L_0x0422
        L_0x03fd:
            r0 = move-exception
            r3 = r2
        L_0x03ff:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0421 }
            if (r4 == 0) goto L_0x0407
            r4.dispose()
        L_0x0407:
            if (r5 == 0) goto L_0x040c
            r5.dispose()
        L_0x040c:
            if (r6 == 0) goto L_0x0411
            r6.dispose()
        L_0x0411:
            if (r7 == 0) goto L_0x0416
            r7.dispose()
        L_0x0416:
            im.bclpbkiauv.ui.-$$Lambda$CacheControlActivity$Vn91Yt-ji8K-u58G0lBA6LKFuvU r0 = new im.bclpbkiauv.ui.-$$Lambda$CacheControlActivity$Vn91Yt-ji8K-u58G0lBA6LKFuvU
            r0.<init>(r3)
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r0)
            r0 = r5
            r5 = r4
        L_0x0420:
            return
        L_0x0421:
            r0 = move-exception
        L_0x0422:
            if (r4 == 0) goto L_0x0427
            r4.dispose()
        L_0x0427:
            if (r5 == 0) goto L_0x042c
            r5.dispose()
        L_0x042c:
            if (r6 == 0) goto L_0x0431
            r6.dispose()
        L_0x0431:
            if (r7 == 0) goto L_0x0436
            r7.dispose()
        L_0x0436:
            im.bclpbkiauv.ui.-$$Lambda$CacheControlActivity$Vn91Yt-ji8K-u58G0lBA6LKFuvU r2 = new im.bclpbkiauv.ui.-$$Lambda$CacheControlActivity$Vn91Yt-ji8K-u58G0lBA6LKFuvU
            r2.<init>(r3)
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.CacheControlActivity.lambda$null$6$CacheControlActivity(im.bclpbkiauv.ui.actionbar.AlertDialog):void");
    }

    public /* synthetic */ void lambda$null$5$CacheControlActivity(AlertDialog progressDialog) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (this.listAdapter != null) {
            this.databaseSize = MessagesStorage.getInstance(this.currentAccount).getDatabaseSize();
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public /* synthetic */ void lambda$null$8$CacheControlActivity(View v) {
        CheckBoxCell cell = (CheckBoxCell) v;
        int num = ((Integer) cell.getTag()).intValue();
        boolean[] zArr = this.clear;
        zArr[num] = !zArr[num];
        cell.setChecked(zArr[num], true);
    }

    public /* synthetic */ void lambda$null$9$CacheControlActivity(View v) {
        try {
            if (this.visibleDialog != null) {
                this.visibleDialog.dismiss();
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        cleanupFolders();
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == CacheControlActivity.this.databaseRow || (position == CacheControlActivity.this.cacheRow && CacheControlActivity.this.totalSize > 0) || position == CacheControlActivity.this.keepMediaRow;
        }

        public int getItemCount() {
            return CacheControlActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType != 0) {
                view = new TextInfoPrivacyCell(this.mContext);
            } else {
                view = new TextSettingsCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String value;
            int itemViewType = holder.getItemViewType();
            if (itemViewType == 0) {
                TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                if (position == CacheControlActivity.this.databaseRow) {
                    textCell.setTextAndValue(LocaleController.getString("LocalDatabase", R.string.LocalDatabase), AndroidUtilities.formatFileSize(CacheControlActivity.this.databaseSize), false);
                } else if (position == CacheControlActivity.this.cacheRow) {
                    if (CacheControlActivity.this.calculating) {
                        textCell.setTextAndValue(LocaleController.getString("ClearMediaCache", R.string.ClearMediaCache), LocaleController.getString("CalculatingSize", R.string.CalculatingSize), false);
                    } else {
                        textCell.setTextAndValue(LocaleController.getString("ClearMediaCache", R.string.ClearMediaCache), CacheControlActivity.this.totalSize == 0 ? LocaleController.getString("CacheEmpty", R.string.CacheEmpty) : AndroidUtilities.formatFileSize(CacheControlActivity.this.totalSize), false);
                    }
                } else if (position == CacheControlActivity.this.keepMediaRow) {
                    SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                    int keepMedia = SharedConfig.keepMedia;
                    if (keepMedia == 0) {
                        value = LocaleController.formatPluralString("Weeks", 1);
                    } else if (keepMedia == 1) {
                        value = LocaleController.formatPluralString("Months", 1);
                    } else if (keepMedia == 3) {
                        value = LocaleController.formatPluralString("Days", 3);
                    } else {
                        value = LocaleController.getString("KeepMediaForever", R.string.KeepMediaForever);
                    }
                    textCell.setTextAndValue(LocaleController.getString("KeepMedia", R.string.KeepMedia), value, false);
                }
            } else if (itemViewType == 1) {
                TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) holder.itemView;
                if (position == CacheControlActivity.this.databaseInfoRow) {
                    privacyCell.setText(LocaleController.getString("LocalDatabaseInfo", R.string.LocalDatabaseInfo));
                    privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                } else if (position == CacheControlActivity.this.cacheInfoRow) {
                    privacyCell.setText("");
                    privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                } else if (position == CacheControlActivity.this.keepMediaInfoRow) {
                    privacyCell.setText(AndroidUtilities.replaceTags(LocaleController.getString("KeepMediaInfo", R.string.KeepMediaInfo)));
                    privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                }
            }
        }

        public int getItemViewType(int i) {
            if (i == CacheControlActivity.this.databaseInfoRow || i == CacheControlActivity.this.cacheInfoRow || i == CacheControlActivity.this.keepMediaInfoRow) {
                return 1;
            }
            return 0;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteValueText), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText4)};
    }
}
