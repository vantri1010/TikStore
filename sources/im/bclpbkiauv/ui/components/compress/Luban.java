package im.bclpbkiauv.ui.components.compress;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Luban implements Handler.Callback {
    private static final String DEFAULT_DISK_CACHE_DIR = "luban_disk_cache";
    private static final int MSG_COMPRESS_ERROR = 2;
    private static final int MSG_COMPRESS_START = 1;
    private static final int MSG_COMPRESS_SUCCESS = 0;
    private static final String TAG = "Luban";
    private boolean focusAlpha;
    private OnCompressListener mCompressListener;
    private int mCompressQuality;
    private CompressionPredicate mCompressionPredicate;
    /* access modifiers changed from: private */
    public Handler mHandler;
    private int mLeastCompressSize;
    private OnRenameListener mRenameListener;
    private List<InputStreamProvider> mStreamProviders;
    private String mTargetDir;

    private Luban(Builder builder) {
        this.mTargetDir = builder.mTargetDir;
        this.mCompressQuality = builder.mCompressQuality;
        this.mRenameListener = builder.mRenameListener;
        this.mStreamProviders = builder.mStreamProviders;
        this.mCompressListener = builder.mCompressListener;
        this.mLeastCompressSize = builder.mLeastCompressSize;
        this.mCompressionPredicate = builder.mCompressionPredicate;
        this.mHandler = new Handler(Looper.getMainLooper(), this);
    }

    public static Builder with(Context context) {
        return new Builder(context);
    }

    private File getImageCacheFile(Context context, String suffix) {
        if (TextUtils.isEmpty(this.mTargetDir)) {
            this.mTargetDir = getImageCacheDir(context).getAbsolutePath();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(this.mTargetDir);
        sb.append("/");
        sb.append(System.currentTimeMillis());
        sb.append((int) (Math.random() * 1000.0d));
        sb.append(TextUtils.isEmpty(suffix) ? ".jpg" : suffix);
        return new File(sb.toString());
    }

    private File getImageCustomFile(Context context, String filename) {
        if (TextUtils.isEmpty(this.mTargetDir)) {
            this.mTargetDir = getImageCacheDir(context).getAbsolutePath();
        }
        return new File(this.mTargetDir + "/" + filename);
    }

    private File getImageCacheDir(Context context) {
        return getImageCacheDir(context, DEFAULT_DISK_CACHE_DIR);
    }

    private static File getImageCacheDir(Context context, String cacheName) {
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir != null) {
            File result = new File(cacheDir, cacheName);
            if (result.mkdirs() || (result.exists() && result.isDirectory())) {
                return result;
            }
            return null;
        }
        if (Log.isLoggable(TAG, 6)) {
            Log.e(TAG, "default disk cache dir is null");
        }
        return null;
    }

    /* access modifiers changed from: private */
    public void launch(final Context context) {
        List<InputStreamProvider> list = this.mStreamProviders;
        if (list == null || (list.size() == 0 && this.mCompressListener != null)) {
            this.mCompressListener.onError(new NullPointerException("image file cannot be null"));
        }
        Iterator<InputStreamProvider> iterator = this.mStreamProviders.iterator();
        while (iterator.hasNext()) {
            final InputStreamProvider path = iterator.next();
            AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() {
                public void run() {
                    try {
                        Luban.this.mHandler.sendMessage(Luban.this.mHandler.obtainMessage(1));
                        Luban.this.mHandler.sendMessage(Luban.this.mHandler.obtainMessage(0, Luban.this.compress(context, path)));
                    } catch (IOException e) {
                        Luban.this.mHandler.sendMessage(Luban.this.mHandler.obtainMessage(2, e));
                    }
                }
            });
            iterator.remove();
        }
    }

    /* access modifiers changed from: private */
    public File get(InputStreamProvider input, Context context) throws IOException {
        try {
            return new Engine(input, getImageCacheFile(context, Checker.SINGLE.extSuffix(input)), this.focusAlpha, this.mCompressQuality).compress();
        } finally {
            input.close();
        }
    }

    /* access modifiers changed from: private */
    public List<File> get(Context context) throws IOException {
        List<File> results = new ArrayList<>();
        Iterator<InputStreamProvider> iterator = this.mStreamProviders.iterator();
        while (iterator.hasNext()) {
            results.add(compress(context, iterator.next()));
            iterator.remove();
        }
        return results;
    }

    /* access modifiers changed from: private */
    public File compress(Context context, InputStreamProvider path) throws IOException {
        try {
            return compressReal(context, path);
        } finally {
            path.close();
        }
    }

    private File compressReal(Context context, InputStreamProvider path) throws IOException {
        File outFile = getImageCacheFile(context, Checker.SINGLE.extSuffix(path));
        OnRenameListener onRenameListener = this.mRenameListener;
        if (onRenameListener != null) {
            outFile = getImageCustomFile(context, onRenameListener.rename(path.getPath()));
        }
        CompressionPredicate compressionPredicate = this.mCompressionPredicate;
        if (compressionPredicate != null) {
            if (!compressionPredicate.apply(path.getPath()) || !Checker.SINGLE.needCompress(this.mLeastCompressSize, path.getPath())) {
                return new File(path.getPath());
            }
            return new Engine(path, outFile, this.focusAlpha, this.mCompressQuality).compress();
        } else if (Checker.SINGLE.needCompress(this.mLeastCompressSize, path.getPath())) {
            return new Engine(path, outFile, this.focusAlpha, this.mCompressQuality).compress();
        } else {
            return new File(path.getPath());
        }
    }

    public boolean handleMessage(Message msg) {
        if (this.mCompressListener == null) {
            return false;
        }
        int i = msg.what;
        if (i == 0) {
            this.mCompressListener.onSuccess((File) msg.obj);
        } else if (i == 1) {
            this.mCompressListener.onStart();
        } else if (i == 2) {
            this.mCompressListener.onError((Throwable) msg.obj);
        }
        return false;
    }

    public static class Builder {
        /* access modifiers changed from: private */
        public Context context;
        private boolean focusAlpha;
        /* access modifiers changed from: private */
        public OnCompressListener mCompressListener;
        /* access modifiers changed from: private */
        public int mCompressQuality = 60;
        /* access modifiers changed from: private */
        public CompressionPredicate mCompressionPredicate;
        /* access modifiers changed from: private */
        public int mLeastCompressSize = 100;
        /* access modifiers changed from: private */
        public OnRenameListener mRenameListener;
        /* access modifiers changed from: private */
        public List<InputStreamProvider> mStreamProviders;
        /* access modifiers changed from: private */
        public String mTargetDir;

        Builder(Context context2) {
            this.context = context2;
            this.mStreamProviders = new ArrayList();
        }

        private Luban build() {
            return new Luban(this);
        }

        public Builder load(InputStreamProvider inputStreamProvider) {
            this.mStreamProviders.add(inputStreamProvider);
            return this;
        }

        public Builder load(final File file) {
            this.mStreamProviders.add(new InputStreamAdapter() {
                public InputStream openInternal() throws IOException {
                    return new FileInputStream(file);
                }

                public String getPath() {
                    return file.getAbsolutePath();
                }
            });
            return this;
        }

        public Builder load(final String string) {
            this.mStreamProviders.add(new InputStreamAdapter() {
                public InputStream openInternal() throws IOException {
                    return new FileInputStream(string);
                }

                public String getPath() {
                    return string;
                }
            });
            return this;
        }

        public <T> Builder load(List<T> list) {
            for (T src : list) {
                if (src instanceof String) {
                    load((String) src);
                } else if (src instanceof File) {
                    load((File) src);
                } else if (src instanceof Uri) {
                    load((Uri) src);
                } else {
                    throw new IllegalArgumentException("Incoming data type exception, it must be String, File, Uri or Bitmap");
                }
            }
            return this;
        }

        public Builder load(final Uri uri) {
            this.mStreamProviders.add(new InputStreamAdapter() {
                public InputStream openInternal() throws IOException {
                    return Builder.this.context.getContentResolver().openInputStream(uri);
                }

                public String getPath() {
                    return uri.getPath();
                }
            });
            return this;
        }

        public Builder putGear(int gear) {
            return this;
        }

        public Builder setRenameListener(OnRenameListener listener) {
            this.mRenameListener = listener;
            return this;
        }

        public Builder setCompressListener(OnCompressListener listener) {
            this.mCompressListener = listener;
            return this;
        }

        public Builder setTargetDir(String targetDir) {
            this.mTargetDir = targetDir;
            return this;
        }

        public Builder setFocusAlpha(boolean focusAlpha2) {
            this.focusAlpha = focusAlpha2;
            return this;
        }

        public Builder ignoreBy(int size) {
            this.mLeastCompressSize = size;
            return this;
        }

        public Builder filter(CompressionPredicate compressionPredicate) {
            this.mCompressionPredicate = compressionPredicate;
            return this;
        }

        public Builder setCompressQuality(int compressQuality) {
            this.mCompressQuality = compressQuality;
            return this;
        }

        public void launch() {
            build().launch(this.context);
        }

        public File get(final String path) throws IOException {
            return build().get(new InputStreamAdapter() {
                public InputStream openInternal() throws IOException {
                    return new FileInputStream(path);
                }

                public String getPath() {
                    return path;
                }
            }, this.context);
        }

        public List<File> get() throws IOException {
            return build().get(this.context);
        }
    }
}
