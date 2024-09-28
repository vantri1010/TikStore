package im.bclpbkiauv.ui.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bjz.comm.net.utils.AppPreferenceUtil;
import im.bclpbkiauv.javaBean.fc.FollowedFcListBean;
import im.bclpbkiauv.javaBean.fc.HomeFcListBean;
import im.bclpbkiauv.javaBean.fc.RecommendFcListBean;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLoader;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.dialogs.DialogClearCache;
import im.bclpbkiauv.ui.dialogs.DialogCommonList;
import im.bclpbkiauv.ui.hui.friendscircle_v1.helper.FcDBHelper;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CacheControlSettingActivity extends BaseFragment {
    /* access modifiers changed from: private */
    public long audioSize = -1;
    /* access modifiers changed from: private */
    public long cacheSize = -1;
    private boolean calculating = true;
    private volatile boolean canceled = false;
    /* access modifiers changed from: private */
    public boolean[] clear = new boolean[6];
    private long databaseSize = -1;
    /* access modifiers changed from: private */
    public long documentsSize = -1;
    /* access modifiers changed from: private */
    public long musicSize = -1;
    /* access modifiers changed from: private */
    public long photoSize = -1;
    /* access modifiers changed from: private */
    public long totalSize = -1;
    /* access modifiers changed from: private */
    public long videoSize = -1;

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.databaseSize = MessagesStorage.getInstance(this.currentAccount).getDatabaseSize();
        Utilities.globalQueue.postRunnable(new Runnable() {
            public final void run() {
                CacheControlSettingActivity.this.lambda$onFragmentCreate$1$CacheControlSettingActivity();
            }
        });
        return true;
    }

    public /* synthetic */ void lambda$onFragmentCreate$1$CacheControlSettingActivity() {
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
                                    CacheControlSettingActivity.this.lambda$null$0$CacheControlSettingActivity();
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    public /* synthetic */ void lambda$null$0$CacheControlSettingActivity() {
        this.calculating = false;
        initState();
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

    /* access modifiers changed from: private */
    public void cleanupFolders() {
        AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
        progressDialog.setCanCancel(false);
        progressDialog.show();
        Utilities.globalQueue.postRunnable(new Runnable(progressDialog) {
            private final /* synthetic */ AlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                CacheControlSettingActivity.this.lambda$cleanupFolders$3$CacheControlSettingActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$cleanupFolders$3$CacheControlSettingActivity(AlertDialog progressDialog) {
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
                        AppPreferenceUtil.putString("PublishFcBean", "");
                        FcDBHelper.getInstance().deleteAll(HomeFcListBean.class);
                        FcDBHelper.getInstance().deleteAll(RecommendFcListBean.class);
                        FcDBHelper.getInstance().deleteAll(FollowedFcListBean.class);
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
                CacheControlSettingActivity.this.lambda$null$2$CacheControlSettingActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$2$CacheControlSettingActivity(boolean imagesClearedFinal, AlertDialog progressDialog) {
        if (imagesClearedFinal) {
            ImageLoader.getInstance().clearMemory();
        }
        initState();
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private void initListener() {
        this.fragmentView.findViewById(R.id.rl_cache_period).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (CacheControlSettingActivity.this.getParentActivity() != null) {
                    List<String> arrList = new ArrayList<>();
                    arrList.add(LocaleController.formatPluralString("Days", 3));
                    arrList.add(LocaleController.formatPluralString("Weeks", 1));
                    arrList.add(LocaleController.formatPluralString("Months", 1));
                    arrList.add(LocaleController.getString("KeepMediaForever", R.string.KeepMediaForever));
                    new DialogCommonList(CacheControlSettingActivity.this.getParentActivity(), arrList, 0, new DialogCommonList.RecyclerviewItemClickCallBack() {
                        public void onRecyclerviewItemClick(int which) {
                            if (which == 0) {
                                SharedConfig.setKeepMedia(3);
                            } else if (which == 1) {
                                SharedConfig.setKeepMedia(0);
                            } else if (which == 2) {
                                SharedConfig.setKeepMedia(1);
                            } else if (which == 3) {
                                SharedConfig.setKeepMedia(2);
                            }
                            CacheControlSettingActivity.this.initState();
                            SharedConfig.checkKeepMedia();
                        }
                    }).show();
                }
            }
        });
        this.fragmentView.findViewById(R.id.rl_clear_cache).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (CacheControlSettingActivity.this.totalSize > 0 && CacheControlSettingActivity.this.getParentActivity() != null) {
                    final List<CacheInfo> arrList = new ArrayList<>();
                    for (int a = 0; a < 6; a++) {
                        long size = 0;
                        CacheInfo cacheInfo = new CacheInfo();
                        if (a == 0) {
                            size = CacheControlSettingActivity.this.photoSize;
                        } else if (a == 1) {
                            size = CacheControlSettingActivity.this.videoSize;
                        } else if (a == 2) {
                            size = CacheControlSettingActivity.this.documentsSize;
                        } else if (a == 3) {
                            size = CacheControlSettingActivity.this.musicSize;
                        } else if (a == 4) {
                            size = CacheControlSettingActivity.this.audioSize;
                        } else if (a == 5) {
                            size = CacheControlSettingActivity.this.cacheSize;
                        }
                        if (size > 0) {
                            CacheControlSettingActivity.this.clear[a] = true;
                            cacheInfo.setMiIndex(a);
                            cacheInfo.setMlCacheSize(size);
                            arrList.add(cacheInfo);
                        } else {
                            CacheControlSettingActivity.this.clear[a] = false;
                        }
                    }
                    new DialogClearCache(CacheControlSettingActivity.this.getParentActivity(), arrList, new DialogClearCache.CacheClearSelectCallback() {
                        public void onCacheClearSelect(boolean[] arrBln) {
                            for (int i = 0; i < arrList.size(); i++) {
                                CacheControlSettingActivity.this.clear[((CacheInfo) arrList.get(i)).getMiIndex()] = arrBln[i];
                            }
                            CacheControlSettingActivity.this.cleanupFolders();
                        }
                    }).show();
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void initState() {
        String value;
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
        ((TextView) this.fragmentView.findViewById(R.id.tv_cache_period)).setText(value);
        TextView textView = (TextView) this.fragmentView.findViewById(R.id.tv_clear_cache);
        long j = this.totalSize;
        textView.setText(j == 0 ? LocaleController.getString("CacheEmpty", R.string.CacheEmpty) : AndroidUtilities.formatFileSize(j));
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("StorageUsage", R.string.StorageUsage));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    CacheControlSettingActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_setting_cache_control, (ViewGroup) null, false);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        initView(context);
        initListener();
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        initState();
    }

    private void initView(Context context) {
        this.fragmentView.findViewById(R.id.rl_cache_period).setBackground(Theme.getRoundRectSelectorDrawable(AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.fragmentView.findViewById(R.id.rl_server_file).setBackground(Theme.getRoundRectSelectorDrawable(AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.fragmentView.findViewById(R.id.rl_clear_cache).setBackground(Theme.getRoundRectSelectorDrawable(AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
    }

    public class CacheInfo {
        private int miIndex;
        private long mlCacheSize;

        public CacheInfo() {
        }

        public long getMlCacheSize() {
            return this.mlCacheSize;
        }

        public void setMlCacheSize(long mlCacheSize2) {
            this.mlCacheSize = mlCacheSize2;
        }

        public int getMiIndex() {
            return this.miIndex;
        }

        public void setMiIndex(int miIndex2) {
            this.miIndex = miIndex2;
        }
    }
}
