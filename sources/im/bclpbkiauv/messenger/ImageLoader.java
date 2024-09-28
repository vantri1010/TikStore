package im.bclpbkiauv.messenger;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.SparseArray;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.ImageLoader;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.components.AnimatedFileDrawable;
import im.bclpbkiauv.ui.components.RLottieDrawable;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONArray;
import org.json.JSONObject;

public class ImageLoader {
    public static final String AUTOPLAY_FILTER = "g";
    private static volatile ImageLoader Instance = null;
    /* access modifiers changed from: private */
    public static ThreadLocal<byte[]> bytesLocal = new ThreadLocal<>();
    /* access modifiers changed from: private */
    public static ThreadLocal<byte[]> bytesThumbLocal = new ThreadLocal<>();
    /* access modifiers changed from: private */
    public static byte[] header = new byte[12];
    /* access modifiers changed from: private */
    public static byte[] headerThumb = new byte[12];
    /* access modifiers changed from: private */
    public File appPath;
    /* access modifiers changed from: private */
    public LinkedList<ArtworkLoadTask> artworkTasks = new LinkedList<>();
    /* access modifiers changed from: private */
    public HashMap<String, Integer> bitmapUseCounts = new HashMap<>();
    /* access modifiers changed from: private */
    public DispatchQueue cacheOutQueue = new DispatchQueue("cacheOutQueue");
    /* access modifiers changed from: private */
    public DispatchQueue cacheThumbOutQueue = new DispatchQueue("cacheThumbOutQueue");
    /* access modifiers changed from: private */
    public boolean canForce8888;
    private int currentArtworkTasksCount;
    private int currentHttpFileLoadTasksCount;
    private int currentHttpTasksCount;
    /* access modifiers changed from: private */
    public ConcurrentHashMap<String, Float> fileProgresses = new ConcurrentHashMap<>();
    /* access modifiers changed from: private */
    public HashMap<String, Integer> forceLoadingImages = new HashMap<>();
    private LinkedList<HttpFileTask> httpFileLoadTasks;
    private HashMap<String, HttpFileTask> httpFileLoadTasksByKeys;
    /* access modifiers changed from: private */
    public LinkedList<HttpImageTask> httpTasks = new LinkedList<>();
    /* access modifiers changed from: private */
    public String ignoreRemoval;
    /* access modifiers changed from: private */
    public DispatchQueue imageLoadQueue = new DispatchQueue("imageLoadQueue");
    /* access modifiers changed from: private */
    public HashMap<String, CacheImage> imageLoadingByKeys = new HashMap<>();
    /* access modifiers changed from: private */
    public SparseArray<CacheImage> imageLoadingByTag = new SparseArray<>();
    /* access modifiers changed from: private */
    public HashMap<String, CacheImage> imageLoadingByUrl = new HashMap<>();
    /* access modifiers changed from: private */
    public volatile long lastCacheOutTime;
    private int lastImageNum;
    /* access modifiers changed from: private */
    public long lastProgressUpdateTime;
    /* access modifiers changed from: private */
    public LruCache<RLottieDrawable> lottieMemCache;
    /* access modifiers changed from: private */
    public LruCache<BitmapDrawable> memCache;
    private HashMap<String, String> replacedBitmaps = new HashMap<>();
    private HashMap<String, Runnable> retryHttpsTasks;
    /* access modifiers changed from: private */
    public ConcurrentHashMap<String, WebFile> testWebFile;
    /* access modifiers changed from: private */
    public HashMap<String, ThumbGenerateTask> thumbGenerateTasks = new HashMap<>();
    private DispatchQueue thumbGeneratingQueue = new DispatchQueue("thumbGeneratingQueue");
    private HashMap<String, ThumbGenerateInfo> waitingForQualityThumb = new HashMap<>();
    private SparseArray<String> waitingForQualityThumbByTag = new SparseArray<>();

    private class ThumbGenerateInfo {
        /* access modifiers changed from: private */
        public boolean big;
        /* access modifiers changed from: private */
        public String filter;
        /* access modifiers changed from: private */
        public ArrayList<ImageReceiver> imageReceiverArray;
        /* access modifiers changed from: private */
        public ArrayList<Integer> imageReceiverGuidsArray;
        /* access modifiers changed from: private */
        public TLRPC.Document parentDocument;

        private ThumbGenerateInfo() {
            this.imageReceiverArray = new ArrayList<>();
            this.imageReceiverGuidsArray = new ArrayList<>();
        }
    }

    private class HttpFileTask extends AsyncTask<Void, Void, Boolean> {
        /* access modifiers changed from: private */
        public boolean canRetry = true;
        /* access modifiers changed from: private */
        public int currentAccount;
        /* access modifiers changed from: private */
        public String ext;
        private RandomAccessFile fileOutputStream = null;
        private int fileSize;
        private long lastProgressTime;
        /* access modifiers changed from: private */
        public File tempFile;
        /* access modifiers changed from: private */
        public String url;

        public HttpFileTask(String url2, File tempFile2, String ext2, int currentAccount2) {
            this.url = url2;
            this.tempFile = tempFile2;
            this.ext = ext2;
            this.currentAccount = currentAccount2;
        }

        private void reportProgress(float progress) {
            long currentTime = System.currentTimeMillis();
            if (progress != 1.0f) {
                long j = this.lastProgressTime;
                if (j != 0 && j >= currentTime - 500) {
                    return;
                }
            }
            this.lastProgressTime = currentTime;
            Utilities.stageQueue.postRunnable(new Runnable(progress) {
                private final /* synthetic */ float f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ImageLoader.HttpFileTask.this.lambda$reportProgress$1$ImageLoader$HttpFileTask(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$reportProgress$1$ImageLoader$HttpFileTask(float progress) {
            ImageLoader.this.fileProgresses.put(this.url, Float.valueOf(progress));
            AndroidUtilities.runOnUIThread(new Runnable(progress) {
                private final /* synthetic */ float f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ImageLoader.HttpFileTask.this.lambda$null$0$ImageLoader$HttpFileTask(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$null$0$ImageLoader$HttpFileTask(float progress) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.FileLoadProgressChanged, this.url, Float.valueOf(progress));
        }

        /* access modifiers changed from: protected */
        public Boolean doInBackground(Void... voids) {
            List values;
            String length;
            int code;
            InputStream httpConnectionStream = null;
            boolean done = false;
            URLConnection httpConnection = null;
            try {
                httpConnection = new URL(this.url).openConnection();
                httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1");
                httpConnection.setConnectTimeout(5000);
                httpConnection.setReadTimeout(5000);
                if (httpConnection instanceof HttpURLConnection) {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) httpConnection;
                    httpURLConnection.setInstanceFollowRedirects(true);
                    int status = httpURLConnection.getResponseCode();
                    if (status == 302 || status == 301 || status == 303) {
                        String newUrl = httpURLConnection.getHeaderField("Location");
                        String cookies = httpURLConnection.getHeaderField("Set-Cookie");
                        httpConnection = new URL(newUrl).openConnection();
                        httpConnection.setRequestProperty("Cookie", cookies);
                        httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1");
                    }
                }
                httpConnection.connect();
                httpConnectionStream = httpConnection.getInputStream();
                this.fileOutputStream = new RandomAccessFile(this.tempFile, "rws");
            } catch (Throwable e) {
                if (e instanceof SocketTimeoutException) {
                    if (ApplicationLoader.isNetworkOnline()) {
                        this.canRetry = false;
                    }
                } else if (e instanceof UnknownHostException) {
                    this.canRetry = false;
                } else if (e instanceof SocketException) {
                    if (e.getMessage() != null && e.getMessage().contains("ECONNRESET")) {
                        this.canRetry = false;
                    }
                } else if (e instanceof FileNotFoundException) {
                    this.canRetry = false;
                }
                FileLog.e(e);
            }
            if (this.canRetry) {
                try {
                    if (!(!(httpConnection instanceof HttpURLConnection) || (code = ((HttpURLConnection) httpConnection).getResponseCode()) == 200 || code == 202 || code == 304)) {
                        this.canRetry = false;
                    }
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
                if (httpConnection != null) {
                    try {
                        Map<String, List<String>> headerFields = httpConnection.getHeaderFields();
                        if (!(headerFields == null || (values = headerFields.get("content-Length")) == null || values.isEmpty() || (length = (String) values.get(0)) == null)) {
                            this.fileSize = Utilities.parseInt(length).intValue();
                        }
                    } catch (Exception e3) {
                        FileLog.e((Throwable) e3);
                    }
                }
                if (httpConnectionStream != null) {
                    try {
                        byte[] data = new byte[32768];
                        int totalLoaded = 0;
                        while (true) {
                            if (!isCancelled()) {
                                int read = httpConnectionStream.read(data);
                                if (read > 0) {
                                    this.fileOutputStream.write(data, 0, read);
                                    totalLoaded += read;
                                    if (this.fileSize > 0) {
                                        reportProgress(((float) totalLoaded) / ((float) this.fileSize));
                                    }
                                } else if (read == -1) {
                                    done = true;
                                    if (this.fileSize != 0) {
                                        reportProgress(1.0f);
                                    }
                                }
                            }
                        }
                    } catch (Exception e4) {
                        FileLog.e((Throwable) e4);
                    } catch (Throwable e5) {
                        FileLog.e(e5);
                    }
                }
                try {
                    if (this.fileOutputStream != null) {
                        this.fileOutputStream.close();
                        this.fileOutputStream = null;
                    }
                } catch (Throwable e6) {
                    FileLog.e(e6);
                }
                if (httpConnectionStream != null) {
                    try {
                        httpConnectionStream.close();
                    } catch (Throwable e7) {
                        FileLog.e(e7);
                    }
                }
            }
            return Boolean.valueOf(done);
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean result) {
            ImageLoader.this.runHttpFileLoadTasks(this, result.booleanValue() ? 2 : 1);
        }

        /* access modifiers changed from: protected */
        public void onCancelled() {
            ImageLoader.this.runHttpFileLoadTasks(this, 2);
        }
    }

    private class ArtworkLoadTask extends AsyncTask<Void, Void, String> {
        /* access modifiers changed from: private */
        public CacheImage cacheImage;
        private boolean canRetry = true;
        private HttpURLConnection httpConnection;
        private boolean small;

        public ArtworkLoadTask(CacheImage cacheImage2) {
            boolean z = true;
            this.cacheImage = cacheImage2;
            this.small = Uri.parse(cacheImage2.imageLocation.path).getQueryParameter("s") == null ? false : z;
        }

        /* access modifiers changed from: protected */
        public String doInBackground(Void... voids) {
            int code;
            ByteArrayOutputStream outbuf = null;
            InputStream httpConnectionStream = null;
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(this.cacheImage.imageLocation.path.replace("athumb://", "https://")).openConnection();
                this.httpConnection = httpURLConnection;
                httpURLConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1");
                this.httpConnection.setConnectTimeout(5000);
                this.httpConnection.setReadTimeout(5000);
                this.httpConnection.connect();
                if (!(this.httpConnection == null || (code = this.httpConnection.getResponseCode()) == 200 || code == 202 || code == 304)) {
                    this.canRetry = false;
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            } catch (Throwable e2) {
                try {
                    if (e2 instanceof SocketTimeoutException) {
                        if (ApplicationLoader.isNetworkOnline()) {
                            this.canRetry = false;
                        }
                    } else if (e2 instanceof UnknownHostException) {
                        this.canRetry = false;
                    } else if (e2 instanceof SocketException) {
                        if (e2.getMessage() != null && e2.getMessage().contains("ECONNRESET")) {
                            this.canRetry = false;
                        }
                    } else if (e2 instanceof FileNotFoundException) {
                        this.canRetry = false;
                    }
                    FileLog.e(e2);
                    if (this.httpConnection != null) {
                        this.httpConnection.disconnect();
                    }
                } catch (Throwable e3) {
                    FileLog.e(e3);
                }
                if (httpConnectionStream != null) {
                    httpConnectionStream.close();
                }
                if (outbuf == null) {
                    return null;
                }
                outbuf.close();
                return null;
            }
            httpConnectionStream = this.httpConnection.getInputStream();
            outbuf = new ByteArrayOutputStream();
            byte[] data = new byte[32768];
            while (true) {
                if (isCancelled()) {
                    break;
                }
                int read = httpConnectionStream.read(data);
                if (read > 0) {
                    outbuf.write(data, 0, read);
                } else if (read == -1) {
                }
            }
            this.canRetry = false;
            JSONArray array = new JSONObject(new String(outbuf.toByteArray())).getJSONArray("results");
            if (array.length() > 0) {
                String artworkUrl100 = array.getJSONObject(0).getString("artworkUrl100");
                if (this.small) {
                    try {
                        if (this.httpConnection != null) {
                            this.httpConnection.disconnect();
                        }
                    } catch (Throwable th) {
                    }
                    if (httpConnectionStream != null) {
                        try {
                            httpConnectionStream.close();
                        } catch (Throwable e4) {
                            FileLog.e(e4);
                        }
                    }
                    try {
                        outbuf.close();
                    } catch (Exception e5) {
                    }
                    return artworkUrl100;
                }
                String replace = artworkUrl100.replace("100x100", "600x600");
                try {
                    if (this.httpConnection != null) {
                        this.httpConnection.disconnect();
                    }
                } catch (Throwable th2) {
                }
                if (httpConnectionStream != null) {
                    try {
                        httpConnectionStream.close();
                    } catch (Throwable e6) {
                        FileLog.e(e6);
                    }
                }
                try {
                    outbuf.close();
                } catch (Exception e7) {
                }
                return replace;
            }
            try {
                if (this.httpConnection != null) {
                    this.httpConnection.disconnect();
                }
            } catch (Throwable th3) {
            }
            if (httpConnectionStream != null) {
                try {
                    httpConnectionStream.close();
                } catch (Throwable e8) {
                    FileLog.e(e8);
                }
            }
            try {
                outbuf.close();
                return null;
            } catch (Exception e9) {
                return null;
            }
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String result) {
            if (result != null) {
                this.cacheImage.httpTask = new HttpImageTask(this.cacheImage, 0, result);
                ImageLoader.this.httpTasks.add(this.cacheImage.httpTask);
                ImageLoader.this.runHttpTasks(false);
            } else if (this.canRetry) {
                ImageLoader.this.artworkLoadError(this.cacheImage.url);
            }
            ImageLoader.this.imageLoadQueue.postRunnable(new Runnable() {
                public final void run() {
                    ImageLoader.ArtworkLoadTask.this.lambda$onPostExecute$0$ImageLoader$ArtworkLoadTask();
                }
            });
        }

        public /* synthetic */ void lambda$onPostExecute$0$ImageLoader$ArtworkLoadTask() {
            ImageLoader.this.runArtworkTasks(true);
        }

        public /* synthetic */ void lambda$onCancelled$1$ImageLoader$ArtworkLoadTask() {
            ImageLoader.this.runArtworkTasks(true);
        }

        /* access modifiers changed from: protected */
        public void onCancelled() {
            ImageLoader.this.imageLoadQueue.postRunnable(new Runnable() {
                public final void run() {
                    ImageLoader.ArtworkLoadTask.this.lambda$onCancelled$1$ImageLoader$ArtworkLoadTask();
                }
            });
        }
    }

    private class HttpImageTask extends AsyncTask<Void, Void, Boolean> {
        /* access modifiers changed from: private */
        public CacheImage cacheImage;
        private boolean canRetry = true;
        private RandomAccessFile fileOutputStream;
        private HttpURLConnection httpConnection;
        /* access modifiers changed from: private */
        public int imageSize;
        private long lastProgressTime;
        private String overrideUrl;

        public HttpImageTask(CacheImage cacheImage2, int size) {
            this.cacheImage = cacheImage2;
            this.imageSize = size;
        }

        public HttpImageTask(CacheImage cacheImage2, int size, String url) {
            this.cacheImage = cacheImage2;
            this.imageSize = size;
            this.overrideUrl = url;
        }

        private void reportProgress(float progress) {
            long currentTime = System.currentTimeMillis();
            if (progress != 1.0f) {
                long j = this.lastProgressTime;
                if (j != 0 && j >= currentTime - 500) {
                    return;
                }
            }
            this.lastProgressTime = currentTime;
            Utilities.stageQueue.postRunnable(new Runnable(progress) {
                private final /* synthetic */ float f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ImageLoader.HttpImageTask.this.lambda$reportProgress$1$ImageLoader$HttpImageTask(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$reportProgress$1$ImageLoader$HttpImageTask(float progress) {
            ImageLoader.this.fileProgresses.put(this.cacheImage.url, Float.valueOf(progress));
            AndroidUtilities.runOnUIThread(new Runnable(progress) {
                private final /* synthetic */ float f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ImageLoader.HttpImageTask.this.lambda$null$0$ImageLoader$HttpImageTask(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$null$0$ImageLoader$HttpImageTask(float progress) {
            NotificationCenter.getInstance(this.cacheImage.currentAccount).postNotificationName(NotificationCenter.FileLoadProgressChanged, this.cacheImage.url, Float.valueOf(progress));
        }

        /* access modifiers changed from: protected */
        public Boolean doInBackground(Void... voids) {
            HttpURLConnection httpURLConnection;
            List values;
            String length;
            int code;
            int provider;
            WebFile webFile;
            InputStream httpConnectionStream = null;
            boolean done = false;
            if (!isCancelled()) {
                try {
                    String location = this.cacheImage.imageLocation.path;
                    if ((location.startsWith("https://static-maps") || location.startsWith("https://maps.googleapis")) && (((provider = MessagesController.getInstance(this.cacheImage.currentAccount).mapProvider) == 3 || provider == 4) && (webFile = (WebFile) ImageLoader.this.testWebFile.get(location)) != null)) {
                        TLRPC.TL_upload_getWebFile req = new TLRPC.TL_upload_getWebFile();
                        req.location = webFile.location;
                        req.offset = 0;
                        req.limit = 0;
                        ConnectionsManager.getInstance(this.cacheImage.currentAccount).sendRequest(req, $$Lambda$ImageLoader$HttpImageTask$W8ezfRLOIEaIkgIlG53hdx_Qs.INSTANCE);
                    }
                    HttpURLConnection httpURLConnection2 = (HttpURLConnection) new URL(this.overrideUrl != null ? this.overrideUrl : location).openConnection();
                    this.httpConnection = httpURLConnection2;
                    httpURLConnection2.addRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1");
                    this.httpConnection.setConnectTimeout(5000);
                    this.httpConnection.setReadTimeout(5000);
                    this.httpConnection.setInstanceFollowRedirects(true);
                    if (!isCancelled()) {
                        this.httpConnection.connect();
                        httpConnectionStream = this.httpConnection.getInputStream();
                        this.fileOutputStream = new RandomAccessFile(this.cacheImage.tempFilePath, "rws");
                    }
                } catch (Throwable e) {
                    if (e instanceof SocketTimeoutException) {
                        if (ApplicationLoader.isNetworkOnline()) {
                            this.canRetry = false;
                        }
                    } else if (e instanceof UnknownHostException) {
                        this.canRetry = false;
                    } else if (e instanceof SocketException) {
                        if (e.getMessage() != null && e.getMessage().contains("ECONNRESET")) {
                            this.canRetry = false;
                        }
                    } else if (e instanceof FileNotFoundException) {
                        this.canRetry = false;
                    }
                    FileLog.e(e);
                }
            }
            if (!isCancelled()) {
                try {
                    if (!(this.httpConnection == null || (code = this.httpConnection.getResponseCode()) == 200 || code == 202 || code == 304)) {
                        this.canRetry = false;
                    }
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
                if (this.imageSize == 0 && (httpURLConnection = this.httpConnection) != null) {
                    try {
                        Map<String, List<String>> headerFields = httpURLConnection.getHeaderFields();
                        if (!(headerFields == null || (values = headerFields.get("content-Length")) == null || values.isEmpty() || (length = (String) values.get(0)) == null)) {
                            this.imageSize = Utilities.parseInt(length).intValue();
                        }
                    } catch (Exception e3) {
                        FileLog.e((Throwable) e3);
                    }
                }
                if (httpConnectionStream != null) {
                    try {
                        byte[] data = new byte[8192];
                        int totalLoaded = 0;
                        while (true) {
                            if (!isCancelled()) {
                                int read = httpConnectionStream.read(data);
                                if (read > 0) {
                                    totalLoaded += read;
                                    this.fileOutputStream.write(data, 0, read);
                                    if (this.imageSize != 0) {
                                        reportProgress(((float) totalLoaded) / ((float) this.imageSize));
                                    }
                                } else if (read == -1) {
                                    done = true;
                                    if (this.imageSize != 0) {
                                        reportProgress(1.0f);
                                    }
                                }
                            }
                        }
                    } catch (Exception e4) {
                        FileLog.e((Throwable) e4);
                    } catch (Throwable e5) {
                        FileLog.e(e5);
                    }
                }
            }
            try {
                if (this.fileOutputStream != null) {
                    this.fileOutputStream.close();
                    this.fileOutputStream = null;
                }
            } catch (Throwable e6) {
                FileLog.e(e6);
            }
            try {
                if (this.httpConnection != null) {
                    this.httpConnection.disconnect();
                }
            } catch (Throwable th) {
            }
            if (httpConnectionStream != null) {
                try {
                    httpConnectionStream.close();
                } catch (Throwable e7) {
                    FileLog.e(e7);
                }
            }
            if (done && this.cacheImage.tempFilePath != null && !this.cacheImage.tempFilePath.renameTo(this.cacheImage.finalFilePath)) {
                CacheImage cacheImage2 = this.cacheImage;
                cacheImage2.finalFilePath = cacheImage2.tempFilePath;
            }
            return Boolean.valueOf(done);
        }

        static /* synthetic */ void lambda$doInBackground$2(TLObject response, TLRPC.TL_error error) {
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean result) {
            if (result.booleanValue() || !this.canRetry) {
                ImageLoader.this.fileDidLoaded(this.cacheImage.url, this.cacheImage.finalFilePath, 0);
            } else {
                ImageLoader.this.httpFileLoadError(this.cacheImage.url);
            }
            Utilities.stageQueue.postRunnable(new Runnable(result) {
                private final /* synthetic */ Boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ImageLoader.HttpImageTask.this.lambda$onPostExecute$4$ImageLoader$HttpImageTask(this.f$1);
                }
            });
            ImageLoader.this.imageLoadQueue.postRunnable(new Runnable() {
                public final void run() {
                    ImageLoader.HttpImageTask.this.lambda$onPostExecute$5$ImageLoader$HttpImageTask();
                }
            });
        }

        public /* synthetic */ void lambda$onPostExecute$4$ImageLoader$HttpImageTask(Boolean result) {
            ImageLoader.this.fileProgresses.remove(this.cacheImage.url);
            AndroidUtilities.runOnUIThread(new Runnable(result) {
                private final /* synthetic */ Boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ImageLoader.HttpImageTask.this.lambda$null$3$ImageLoader$HttpImageTask(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$null$3$ImageLoader$HttpImageTask(Boolean result) {
            if (result.booleanValue()) {
                NotificationCenter.getInstance(this.cacheImage.currentAccount).postNotificationName(NotificationCenter.fileDidLoad, this.cacheImage.url, this.cacheImage.finalFilePath);
                return;
            }
            NotificationCenter.getInstance(this.cacheImage.currentAccount).postNotificationName(NotificationCenter.fileDidFailToLoad, this.cacheImage.url, 2);
        }

        public /* synthetic */ void lambda$onPostExecute$5$ImageLoader$HttpImageTask() {
            ImageLoader.this.runHttpTasks(true);
        }

        public /* synthetic */ void lambda$onCancelled$6$ImageLoader$HttpImageTask() {
            ImageLoader.this.runHttpTasks(true);
        }

        /* access modifiers changed from: protected */
        public void onCancelled() {
            ImageLoader.this.imageLoadQueue.postRunnable(new Runnable() {
                public final void run() {
                    ImageLoader.HttpImageTask.this.lambda$onCancelled$6$ImageLoader$HttpImageTask();
                }
            });
            Utilities.stageQueue.postRunnable(new Runnable() {
                public final void run() {
                    ImageLoader.HttpImageTask.this.lambda$onCancelled$8$ImageLoader$HttpImageTask();
                }
            });
        }

        public /* synthetic */ void lambda$onCancelled$8$ImageLoader$HttpImageTask() {
            ImageLoader.this.fileProgresses.remove(this.cacheImage.url);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    ImageLoader.HttpImageTask.this.lambda$null$7$ImageLoader$HttpImageTask();
                }
            });
        }

        public /* synthetic */ void lambda$null$7$ImageLoader$HttpImageTask() {
            NotificationCenter.getInstance(this.cacheImage.currentAccount).postNotificationName(NotificationCenter.fileDidFailToLoad, this.cacheImage.url, 1);
        }
    }

    private class ThumbGenerateTask implements Runnable {
        private ThumbGenerateInfo info;
        private int mediaType;
        private File originalPath;

        public ThumbGenerateTask(int type, File path, ThumbGenerateInfo i) {
            this.mediaType = type;
            this.originalPath = path;
            this.info = i;
        }

        private void removeTask() {
            ThumbGenerateInfo thumbGenerateInfo = this.info;
            if (thumbGenerateInfo != null) {
                ImageLoader.this.imageLoadQueue.postRunnable(new Runnable(FileLoader.getAttachFileName(thumbGenerateInfo.parentDocument)) {
                    private final /* synthetic */ String f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        ImageLoader.ThumbGenerateTask.this.lambda$removeTask$0$ImageLoader$ThumbGenerateTask(this.f$1);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$removeTask$0$ImageLoader$ThumbGenerateTask(String name) {
            ThumbGenerateTask thumbGenerateTask = (ThumbGenerateTask) ImageLoader.this.thumbGenerateTasks.remove(name);
        }

        public void run() {
            Bitmap originalBitmap;
            Bitmap scaledBitmap;
            try {
                if (this.info == null) {
                    removeTask();
                    return;
                }
                String key = "q_" + this.info.parentDocument.dc_id + "_" + this.info.parentDocument.id;
                File thumbFile = new File(FileLoader.getDirectory(4), key + ".jpg");
                if (!thumbFile.exists()) {
                    if (this.originalPath.exists()) {
                        int size = this.info.big ? Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) : Math.min(180, Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) / 4);
                        Bitmap originalBitmap2 = null;
                        if (this.mediaType == 0) {
                            originalBitmap2 = ImageLoader.loadBitmap(this.originalPath.toString(), (Uri) null, (float) size, (float) size, false);
                        } else {
                            int i = 2;
                            if (this.mediaType == 2) {
                                String file = this.originalPath.toString();
                                if (!this.info.big) {
                                    i = 1;
                                }
                                originalBitmap2 = ThumbnailUtils.createVideoThumbnail(file, i);
                            } else if (this.mediaType == 3) {
                                String path = this.originalPath.toString().toLowerCase();
                                if (path.endsWith("mp4")) {
                                    String file2 = this.originalPath.toString();
                                    if (!this.info.big) {
                                        i = 1;
                                    }
                                    originalBitmap2 = ThumbnailUtils.createVideoThumbnail(file2, i);
                                } else if (path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png") || path.endsWith(".gif")) {
                                    originalBitmap2 = ImageLoader.loadBitmap(path, (Uri) null, (float) size, (float) size, false);
                                }
                            }
                        }
                        if (originalBitmap2 == null) {
                            removeTask();
                            return;
                        }
                        int w = originalBitmap2.getWidth();
                        int h = originalBitmap2.getHeight();
                        if (w != 0) {
                            if (h != 0) {
                                float scaleFactor = Math.min(((float) w) / ((float) size), ((float) h) / ((float) size));
                                if (scaleFactor <= 1.0f || (scaledBitmap = Bitmaps.createScaledBitmap(originalBitmap2, (int) (((float) w) / scaleFactor), (int) (((float) h) / scaleFactor), true)) == originalBitmap2) {
                                    originalBitmap = originalBitmap2;
                                } else {
                                    originalBitmap2.recycle();
                                    originalBitmap = scaledBitmap;
                                }
                                FileOutputStream stream = new FileOutputStream(thumbFile);
                                originalBitmap.compress(Bitmap.CompressFormat.JPEG, this.info.big ? 83 : 60, stream);
                                stream.close();
                                AndroidUtilities.runOnUIThread(new Runnable(key, new ArrayList<>(this.info.imageReceiverArray), new BitmapDrawable(originalBitmap), new ArrayList<>(this.info.imageReceiverGuidsArray)) {
                                    private final /* synthetic */ String f$1;
                                    private final /* synthetic */ ArrayList f$2;
                                    private final /* synthetic */ BitmapDrawable f$3;
                                    private final /* synthetic */ ArrayList f$4;

                                    {
                                        this.f$1 = r2;
                                        this.f$2 = r3;
                                        this.f$3 = r4;
                                        this.f$4 = r5;
                                    }

                                    public final void run() {
                                        ImageLoader.ThumbGenerateTask.this.lambda$run$1$ImageLoader$ThumbGenerateTask(this.f$1, this.f$2, this.f$3, this.f$4);
                                    }
                                });
                                return;
                            }
                        }
                        removeTask();
                        return;
                    }
                }
                removeTask();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            } catch (Throwable e2) {
                FileLog.e(e2);
                removeTask();
            }
        }

        public /* synthetic */ void lambda$run$1$ImageLoader$ThumbGenerateTask(String key, ArrayList finalImageReceiverArray, BitmapDrawable bitmapDrawable, ArrayList finalImageReceiverGuidsArray) {
            removeTask();
            String kf = key;
            if (this.info.filter != null) {
                kf = kf + "@" + this.info.filter;
            }
            for (int a = 0; a < finalImageReceiverArray.size(); a++) {
                ((ImageReceiver) finalImageReceiverArray.get(a)).setImageBitmapByKey(bitmapDrawable, kf, 0, false, ((Integer) finalImageReceiverGuidsArray.get(a)).intValue());
            }
            ImageLoader.this.memCache.put(kf, bitmapDrawable);
        }
    }

    private class CacheOutTask implements Runnable {
        /* access modifiers changed from: private */
        public CacheImage cacheImage;
        private boolean isCancelled;
        private Thread runningThread;
        private final Object sync = new Object();

        public CacheOutTask(CacheImage image) {
            this.cacheImage = image;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v18, resolved type: im.bclpbkiauv.messenger.secretmedia.EncryptedFileInputStream} */
        /* JADX WARNING: type inference failed for: r7v14, types: [java.lang.CharSequence, java.lang.String] */
        /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
            java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
            	at java.util.ArrayList.rangeCheck(ArrayList.java:659)
            	at java.util.ArrayList.get(ArrayList.java:435)
            	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
            	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
            	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
            	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
            	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
            	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
            	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
            	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
            	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
            	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
            	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:698)
            	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
            	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
            	at jadx.core.dex.visitors.regions.RegionMaker.processExcHandler(RegionMaker.java:1043)
            	at jadx.core.dex.visitors.regions.RegionMaker.processTryCatchBlocks(RegionMaker.java:975)
            	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:52)
            */
        /* JADX WARNING: Multi-variable type inference failed */
        public void run() {
            /*
                r42 = this;
                r1 = r42
                java.lang.Object r2 = r1.sync
                monitor-enter(r2)
                java.lang.Thread r0 = java.lang.Thread.currentThread()     // Catch:{ all -> 0x0c2b }
                r1.runningThread = r0     // Catch:{ all -> 0x0c2b }
                java.lang.Thread.interrupted()     // Catch:{ all -> 0x0c2b }
                boolean r0 = r1.isCancelled     // Catch:{ all -> 0x0c2b }
                if (r0 == 0) goto L_0x0014
                monitor-exit(r2)     // Catch:{ all -> 0x0c2b }
                return
            L_0x0014:
                monitor-exit(r2)     // Catch:{ all -> 0x0c2b }
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                im.bclpbkiauv.messenger.ImageLocation r0 = r0.imageLocation
                im.bclpbkiauv.tgnet.TLRPC$PhotoSize r0 = r0.photoSize
                boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_photoStrippedSize
                r2 = 3
                r3 = 2
                r5 = 1
                r6 = 0
                if (r0 == 0) goto L_0x00d3
                java.lang.Object r7 = r1.sync
                monitor-enter(r7)
                boolean r0 = r1.isCancelled     // Catch:{ all -> 0x00d0 }
                if (r0 == 0) goto L_0x002c
                monitor-exit(r7)     // Catch:{ all -> 0x00d0 }
                return
            L_0x002c:
                monitor-exit(r7)     // Catch:{ all -> 0x00d0 }
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                im.bclpbkiauv.messenger.ImageLocation r0 = r0.imageLocation
                im.bclpbkiauv.tgnet.TLRPC$PhotoSize r0 = r0.photoSize
                im.bclpbkiauv.tgnet.TLRPC$TL_photoStrippedSize r0 = (im.bclpbkiauv.tgnet.TLRPC.TL_photoStrippedSize) r0
                byte[] r7 = r0.bytes
                int r7 = r7.length
                int r7 = r7 - r2
                byte[] r8 = im.bclpbkiauv.messenger.Bitmaps.header
                int r8 = r8.length
                int r7 = r7 + r8
                byte[] r8 = im.bclpbkiauv.messenger.Bitmaps.footer
                int r8 = r8.length
                int r7 = r7 + r8
                java.lang.ThreadLocal r8 = im.bclpbkiauv.messenger.ImageLoader.bytesLocal
                java.lang.Object r8 = r8.get()
                byte[] r8 = (byte[]) r8
                if (r8 == 0) goto L_0x0052
                int r9 = r8.length
                if (r9 < r7) goto L_0x0052
                r9 = r8
                goto L_0x0053
            L_0x0052:
                r9 = 0
            L_0x0053:
                if (r9 != 0) goto L_0x0060
                byte[] r10 = new byte[r7]
                r9 = r10
                r8 = r10
                java.lang.ThreadLocal r10 = im.bclpbkiauv.messenger.ImageLoader.bytesLocal
                r10.set(r8)
            L_0x0060:
                byte[] r10 = im.bclpbkiauv.messenger.Bitmaps.header
                byte[] r11 = im.bclpbkiauv.messenger.Bitmaps.header
                int r11 = r11.length
                java.lang.System.arraycopy(r10, r6, r9, r6, r11)
                byte[] r10 = r0.bytes
                byte[] r11 = im.bclpbkiauv.messenger.Bitmaps.header
                int r11 = r11.length
                byte[] r12 = r0.bytes
                int r12 = r12.length
                int r12 = r12 - r2
                java.lang.System.arraycopy(r10, r2, r9, r11, r12)
                byte[] r10 = im.bclpbkiauv.messenger.Bitmaps.footer
                byte[] r11 = im.bclpbkiauv.messenger.Bitmaps.header
                int r11 = r11.length
                byte[] r12 = r0.bytes
                int r12 = r12.length
                int r11 = r11 + r12
                int r11 = r11 - r2
                byte[] r2 = im.bclpbkiauv.messenger.Bitmaps.footer
                int r2 = r2.length
                java.lang.System.arraycopy(r10, r6, r9, r11, r2)
                r2 = 164(0xa4, float:2.3E-43)
                byte[] r10 = r0.bytes
                byte r5 = r10[r5]
                r9[r2] = r5
                r2 = 166(0xa6, float:2.33E-43)
                byte[] r5 = r0.bytes
                byte r3 = r5[r3]
                r9[r2] = r3
                android.graphics.Bitmap r2 = android.graphics.BitmapFactory.decodeByteArray(r9, r6, r7)
                if (r2 == 0) goto L_0x00c2
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r3 = r1.cacheImage
                java.lang.String r3 = r3.filter
                boolean r3 = android.text.TextUtils.isEmpty(r3)
                if (r3 != 0) goto L_0x00c2
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r3 = r1.cacheImage
                java.lang.String r3 = r3.filter
                java.lang.String r5 = "b"
                boolean r3 = r3.contains(r5)
                if (r3 == 0) goto L_0x00c2
                r11 = 3
                r12 = 1
                int r13 = r2.getWidth()
                int r14 = r2.getHeight()
                int r15 = r2.getRowBytes()
                r10 = r2
                im.bclpbkiauv.messenger.Utilities.blurBitmap(r10, r11, r12, r13, r14, r15)
            L_0x00c2:
                if (r2 == 0) goto L_0x00ca
                android.graphics.drawable.BitmapDrawable r4 = new android.graphics.drawable.BitmapDrawable
                r4.<init>(r2)
                goto L_0x00cb
            L_0x00ca:
                r4 = 0
            L_0x00cb:
                r1.onPostExecute(r4)
                goto L_0x0c2a
            L_0x00d0:
                r0 = move-exception
                monitor-exit(r7)     // Catch:{ all -> 0x00d0 }
                throw r0
            L_0x00d3:
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                boolean r0 = r0.lottieFile
                r7 = 8
                if (r0 == 0) goto L_0x01df
                java.lang.Object r8 = r1.sync
                monitor-enter(r8)
                boolean r0 = r1.isCancelled     // Catch:{ all -> 0x01dc }
                if (r0 == 0) goto L_0x00e4
                monitor-exit(r8)     // Catch:{ all -> 0x01dc }
                return
            L_0x00e4:
                monitor-exit(r8)     // Catch:{ all -> 0x01dc }
                r0 = 1126865306(0x432a999a, float:170.6)
                int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r0)
                r8 = 512(0x200, float:7.175E-43)
                int r4 = java.lang.Math.min(r8, r4)
                int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r0)
                int r0 = java.lang.Math.min(r8, r0)
                r9 = 0
                r10 = 0
                r11 = 1
                r12 = 0
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r13 = r1.cacheImage
                java.lang.String r13 = r13.filter
                if (r13 == 0) goto L_0x01c0
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r13 = r1.cacheImage
                java.lang.String r13 = r13.filter
                java.lang.String r14 = "_"
                java.lang.String[] r13 = r13.split(r14)
                int r14 = r13.length
                if (r14 < r3) goto L_0x014d
                r14 = r13[r6]
                float r14 = java.lang.Float.parseFloat(r14)
                r15 = r13[r5]
                float r15 = java.lang.Float.parseFloat(r15)
                float r16 = im.bclpbkiauv.messenger.AndroidUtilities.density
                float r6 = r14 * r16
                int r6 = (int) r6
                int r4 = java.lang.Math.min(r8, r6)
                float r6 = im.bclpbkiauv.messenger.AndroidUtilities.density
                float r6 = r6 * r15
                int r6 = (int) r6
                int r0 = java.lang.Math.min(r8, r6)
                r6 = 1119092736(0x42b40000, float:90.0)
                int r8 = (r14 > r6 ? 1 : (r14 == r6 ? 0 : -1))
                if (r8 > 0) goto L_0x014d
                int r6 = (r15 > r6 ? 1 : (r15 == r6 ? 0 : -1))
                if (r6 > 0) goto L_0x014d
                r6 = 160(0xa0, float:2.24E-43)
                int r4 = java.lang.Math.min(r4, r6)
                int r0 = java.lang.Math.min(r0, r6)
                r10 = 1
                int r6 = im.bclpbkiauv.messenger.SharedConfig.getDevicePerfomanceClass()
                if (r6 == r3) goto L_0x014b
                goto L_0x014c
            L_0x014b:
                r5 = 0
            L_0x014c:
                r9 = r5
            L_0x014d:
                int r5 = r13.length
                if (r5 < r2) goto L_0x0167
                r2 = r13[r3]
                java.lang.String r5 = "nr"
                boolean r2 = r5.equals(r2)
                if (r2 == 0) goto L_0x015c
                r11 = 2
                goto L_0x0167
            L_0x015c:
                r2 = r13[r3]
                java.lang.String r3 = "nrs"
                boolean r2 = r3.equals(r2)
                if (r2 == 0) goto L_0x0167
                r11 = 3
            L_0x0167:
                int r2 = r13.length
                r3 = 5
                if (r2 < r3) goto L_0x01c0
                r2 = 4
                r3 = r13[r2]
                java.lang.String r5 = "c1"
                boolean r3 = r5.equals(r3)
                if (r3 == 0) goto L_0x017d
                int[] r2 = new int[r7]
                r2 = {16219713, 13275258, 16757049, 15582629, 16765248, 16245699, 16768889, 16510934} // fill-array
                r12 = r2
                goto L_0x01c0
            L_0x017d:
                r3 = r13[r2]
                java.lang.String r5 = "c2"
                boolean r3 = r5.equals(r3)
                if (r3 == 0) goto L_0x018e
                int[] r2 = new int[r7]
                r2 = {16219713, 11172960, 16757049, 13150599, 16765248, 14534815, 16768889, 15128242} // fill-array
                r12 = r2
                goto L_0x01c0
            L_0x018e:
                r3 = r13[r2]
                java.lang.String r5 = "c3"
                boolean r3 = r5.equals(r3)
                if (r3 == 0) goto L_0x019f
                int[] r2 = new int[r7]
                r2 = {16219713, 9199944, 16757049, 11371874, 16765248, 12885622, 16768889, 13939080} // fill-array
                r12 = r2
                goto L_0x01c0
            L_0x019f:
                r3 = r13[r2]
                java.lang.String r5 = "c4"
                boolean r3 = r5.equals(r3)
                if (r3 == 0) goto L_0x01b0
                int[] r2 = new int[r7]
                r2 = {16219713, 7224364, 16757049, 9591348, 16765248, 10579526, 16768889, 11303506} // fill-array
                r12 = r2
                goto L_0x01c0
            L_0x01b0:
                r2 = r13[r2]
                java.lang.String r3 = "c5"
                boolean r2 = r3.equals(r2)
                if (r2 == 0) goto L_0x01c0
                int[] r2 = new int[r7]
                r2 = {16219713, 2694162, 16757049, 4663842, 16765248, 5716784, 16768889, 6834492} // fill-array
                r12 = r2
            L_0x01c0:
                im.bclpbkiauv.ui.components.RLottieDrawable r2 = new im.bclpbkiauv.ui.components.RLottieDrawable
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r3 = r1.cacheImage
                java.io.File r15 = r3.finalFilePath
                r14 = r2
                r16 = r4
                r17 = r0
                r18 = r9
                r19 = r10
                r20 = r12
                r14.<init>(r15, r16, r17, r18, r19, r20)
                r2.setAutoRepeat(r11)
                r1.onPostExecute(r2)
                goto L_0x0c2a
            L_0x01dc:
                r0 = move-exception
                monitor-exit(r8)     // Catch:{ all -> 0x01dc }
                throw r0
            L_0x01df:
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                boolean r0 = r0.animatedFile
                if (r0 == 0) goto L_0x025a
                java.lang.Object r6 = r1.sync
                monitor-enter(r6)
                boolean r0 = r1.isCancelled     // Catch:{ all -> 0x0257 }
                if (r0 == 0) goto L_0x01ee
                monitor-exit(r6)     // Catch:{ all -> 0x0257 }
                return
            L_0x01ee:
                monitor-exit(r6)     // Catch:{ all -> 0x0257 }
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                java.lang.String r0 = r0.filter
                java.lang.String r2 = "g"
                boolean r0 = r2.equals(r0)
                if (r0 == 0) goto L_0x0232
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                im.bclpbkiauv.messenger.ImageLocation r0 = r0.imageLocation
                im.bclpbkiauv.tgnet.TLRPC$Document r0 = r0.document
                boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_documentEncrypted
                if (r0 != 0) goto L_0x0232
                im.bclpbkiauv.ui.components.AnimatedFileDrawable r0 = new im.bclpbkiauv.ui.components.AnimatedFileDrawable
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r2 = r1.cacheImage
                java.io.File r6 = r2.finalFilePath
                r7 = 0
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r2 = r1.cacheImage
                int r2 = r2.size
                long r8 = (long) r2
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r2 = r1.cacheImage
                im.bclpbkiauv.messenger.ImageLocation r2 = r2.imageLocation
                im.bclpbkiauv.tgnet.TLRPC$Document r2 = r2.document
                boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.Document
                if (r2 == 0) goto L_0x0223
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r2 = r1.cacheImage
                im.bclpbkiauv.messenger.ImageLocation r2 = r2.imageLocation
                im.bclpbkiauv.tgnet.TLRPC$Document r4 = r2.document
                r10 = r4
                goto L_0x0224
            L_0x0223:
                r10 = 0
            L_0x0224:
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r2 = r1.cacheImage
                java.lang.Object r11 = r2.parentObject
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r2 = r1.cacheImage
                int r12 = r2.currentAccount
                r13 = 0
                r5 = r0
                r5.<init>(r6, r7, r8, r10, r11, r12, r13)
                goto L_0x024f
            L_0x0232:
                im.bclpbkiauv.ui.components.AnimatedFileDrawable r0 = new im.bclpbkiauv.ui.components.AnimatedFileDrawable
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r2 = r1.cacheImage
                java.io.File r3 = r2.finalFilePath
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r2 = r1.cacheImage
                java.lang.String r2 = r2.filter
                java.lang.String r4 = "d"
                boolean r4 = r4.equals(r2)
                r5 = 0
                r7 = 0
                r8 = 0
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r2 = r1.cacheImage
                int r9 = r2.currentAccount
                r10 = 0
                r2 = r0
                r2.<init>(r3, r4, r5, r7, r8, r9, r10)
            L_0x024f:
                java.lang.Thread.interrupted()
                r1.onPostExecute(r0)
                goto L_0x0c2a
            L_0x0257:
                r0 = move-exception
                monitor-exit(r6)     // Catch:{ all -> 0x0257 }
                throw r0
            L_0x025a:
                r6 = 0
                r8 = 0
                r9 = 0
                r10 = 0
                r11 = 0
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                java.io.File r12 = r0.finalFilePath
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                im.bclpbkiauv.messenger.SecureDocument r0 = r0.secureDocument
                if (r0 != 0) goto L_0x0280
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                java.io.File r0 = r0.encryptionKeyPath
                if (r0 == 0) goto L_0x027e
                if (r12 == 0) goto L_0x027e
                java.lang.String r0 = r12.getAbsolutePath()
                java.lang.String r13 = ".enc"
                boolean r0 = r0.endsWith(r13)
                if (r0 == 0) goto L_0x027e
                goto L_0x0280
            L_0x027e:
                r0 = 0
                goto L_0x0281
            L_0x0280:
                r0 = 1
            L_0x0281:
                r13 = r0
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                im.bclpbkiauv.messenger.SecureDocument r0 = r0.secureDocument
                if (r0 == 0) goto L_0x02b4
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                im.bclpbkiauv.messenger.SecureDocument r0 = r0.secureDocument
                im.bclpbkiauv.messenger.SecureDocumentKey r0 = r0.secureDocumentKey
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r14 = r1.cacheImage
                im.bclpbkiauv.messenger.SecureDocument r14 = r14.secureDocument
                im.bclpbkiauv.tgnet.TLRPC$TL_secureFile r14 = r14.secureFile
                if (r14 == 0) goto L_0x02ab
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r14 = r1.cacheImage
                im.bclpbkiauv.messenger.SecureDocument r14 = r14.secureDocument
                im.bclpbkiauv.tgnet.TLRPC$TL_secureFile r14 = r14.secureFile
                byte[] r14 = r14.file_hash
                if (r14 == 0) goto L_0x02ab
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r14 = r1.cacheImage
                im.bclpbkiauv.messenger.SecureDocument r14 = r14.secureDocument
                im.bclpbkiauv.tgnet.TLRPC$TL_secureFile r14 = r14.secureFile
                byte[] r14 = r14.file_hash
                r15 = r14
                r14 = r0
                goto L_0x02b8
            L_0x02ab:
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r14 = r1.cacheImage
                im.bclpbkiauv.messenger.SecureDocument r14 = r14.secureDocument
                byte[] r14 = r14.fileHash
                r15 = r14
                r14 = r0
                goto L_0x02b8
            L_0x02b4:
                r0 = 0
                r14 = 0
                r15 = r14
                r14 = r0
            L_0x02b8:
                r16 = 1
                r18 = 0
                int r0 = android.os.Build.VERSION.SDK_INT
                r2 = 19
                if (r0 >= r2) goto L_0x032a
                r2 = 0
                java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x0313 }
                java.lang.String r4 = "r"
                r0.<init>(r12, r4)     // Catch:{ Exception -> 0x0313 }
                r2 = r0
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ Exception -> 0x0313 }
                int r0 = r0.imageType     // Catch:{ Exception -> 0x0313 }
                if (r0 != r5) goto L_0x02d6
                byte[] r0 = im.bclpbkiauv.messenger.ImageLoader.headerThumb     // Catch:{ Exception -> 0x0313 }
                goto L_0x02da
            L_0x02d6:
                byte[] r0 = im.bclpbkiauv.messenger.ImageLoader.header     // Catch:{ Exception -> 0x0313 }
            L_0x02da:
                int r4 = r0.length     // Catch:{ Exception -> 0x0313 }
                r3 = 0
                r2.readFully(r0, r3, r4)     // Catch:{ Exception -> 0x0313 }
                java.lang.String r3 = new java.lang.String     // Catch:{ Exception -> 0x0313 }
                r3.<init>(r0)     // Catch:{ Exception -> 0x0313 }
                java.lang.String r3 = r3.toLowerCase()     // Catch:{ Exception -> 0x0313 }
                java.lang.String r4 = r3.toLowerCase()     // Catch:{ Exception -> 0x0313 }
                r3 = r4
                java.lang.String r4 = "riff"
                boolean r4 = r3.startsWith(r4)     // Catch:{ Exception -> 0x0313 }
                if (r4 == 0) goto L_0x0301
                java.lang.String r4 = "webp"
                boolean r4 = r3.endsWith(r4)     // Catch:{ Exception -> 0x0313 }
                if (r4 == 0) goto L_0x0301
                r4 = 1
                r18 = r4
            L_0x0301:
                r2.close()     // Catch:{ Exception -> 0x0313 }
                r2.close()     // Catch:{ Exception -> 0x0309 }
            L_0x0308:
                goto L_0x032a
            L_0x0309:
                r0 = move-exception
                r3 = r0
                r0 = r3
                im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
                goto L_0x0308
            L_0x0310:
                r0 = move-exception
                r3 = r0
                goto L_0x031d
            L_0x0313:
                r0 = move-exception
                im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0310 }
                if (r2 == 0) goto L_0x032a
                r2.close()     // Catch:{ Exception -> 0x0309 }
                goto L_0x0308
            L_0x031d:
                if (r2 == 0) goto L_0x0329
                r2.close()     // Catch:{ Exception -> 0x0323 }
                goto L_0x0329
            L_0x0323:
                r0 = move-exception
                r4 = r0
                r0 = r4
                im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0329:
                throw r3
            L_0x032a:
                r0 = 0
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r2 = r1.cacheImage
                im.bclpbkiauv.messenger.ImageLocation r2 = r2.imageLocation
                java.lang.String r2 = r2.path
                if (r2 == 0) goto L_0x0391
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r2 = r1.cacheImage
                im.bclpbkiauv.messenger.ImageLocation r2 = r2.imageLocation
                java.lang.String r2 = r2.path
                java.lang.String r3 = "thumb://"
                boolean r3 = r2.startsWith(r3)
                if (r3 == 0) goto L_0x0361
                java.lang.String r3 = ":"
                int r3 = r2.indexOf(r3, r7)
                if (r3 < 0) goto L_0x035d
                java.lang.String r4 = r2.substring(r7, r3)
                long r22 = java.lang.Long.parseLong(r4)
                java.lang.Long r6 = java.lang.Long.valueOf(r22)
                r8 = 0
                int r4 = r3 + 1
                java.lang.String r0 = r2.substring(r4)
            L_0x035d:
                r16 = 0
                r2 = r0
                goto L_0x0392
            L_0x0361:
                java.lang.String r3 = "vthumb://"
                boolean r3 = r2.startsWith(r3)
                if (r3 == 0) goto L_0x0385
                r3 = 9
                java.lang.String r4 = ":"
                int r4 = r2.indexOf(r4, r3)
                if (r4 < 0) goto L_0x0381
                java.lang.String r3 = r2.substring(r3, r4)
                long r22 = java.lang.Long.parseLong(r3)
                java.lang.Long r6 = java.lang.Long.valueOf(r22)
                r8 = 1
            L_0x0381:
                r16 = 0
                r2 = r0
                goto L_0x0392
            L_0x0385:
                java.lang.String r3 = "http"
                boolean r3 = r2.startsWith(r3)
                if (r3 != 0) goto L_0x0391
                r16 = 0
                r2 = r0
                goto L_0x0392
            L_0x0391:
                r2 = r0
            L_0x0392:
                android.graphics.BitmapFactory$Options r0 = new android.graphics.BitmapFactory$Options
                r0.<init>()
                r3 = r0
                r3.inSampleSize = r5
                int r0 = android.os.Build.VERSION.SDK_INT
                r4 = 21
                if (r0 >= r4) goto L_0x03a2
                r3.inPurgeable = r5
            L_0x03a2:
                r22 = 0
                r23 = 0
                r24 = 0
                r25 = 0
                im.bclpbkiauv.messenger.ImageLoader r0 = im.bclpbkiauv.messenger.ImageLoader.this
                boolean r26 = r0.canForce8888
                r27 = 0
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x05b0 }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x05b0 }
                r28 = 1065353216(0x3f800000, float:1.0)
                if (r0 == 0) goto L_0x054d
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x05b0 }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x05b0 }
                java.lang.String r7 = "_"
                java.lang.String[] r0 = r0.split(r7)     // Catch:{ all -> 0x05b0 }
                int r7 = r0.length     // Catch:{ all -> 0x05b0 }
                r4 = 2
                if (r7 < r4) goto L_0x03e5
                r4 = 0
                r7 = r0[r4]     // Catch:{ all -> 0x03e0 }
                float r4 = java.lang.Float.parseFloat(r7)     // Catch:{ all -> 0x03e0 }
                float r7 = im.bclpbkiauv.messenger.AndroidUtilities.density     // Catch:{ all -> 0x03e0 }
                float r22 = r4 * r7
                r4 = r0[r5]     // Catch:{ all -> 0x03e0 }
                float r4 = java.lang.Float.parseFloat(r4)     // Catch:{ all -> 0x03e0 }
                float r7 = im.bclpbkiauv.messenger.AndroidUtilities.density     // Catch:{ all -> 0x03e0 }
                float r4 = r4 * r7
                r23 = r4
                goto L_0x03e5
            L_0x03e0:
                r0 = move-exception
                r31 = r10
                goto L_0x05b5
            L_0x03e5:
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r4 = r1.cacheImage     // Catch:{ all -> 0x05b0 }
                java.lang.String r4 = r4.filter     // Catch:{ all -> 0x05b0 }
                java.lang.String r7 = "b2"
                boolean r4 = r4.contains(r7)     // Catch:{ all -> 0x05b0 }
                if (r4 == 0) goto L_0x03f5
                r4 = 3
                r24 = r4
                goto L_0x0414
            L_0x03f5:
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r4 = r1.cacheImage     // Catch:{ all -> 0x05b0 }
                java.lang.String r4 = r4.filter     // Catch:{ all -> 0x05b0 }
                java.lang.String r7 = "b1"
                boolean r4 = r4.contains(r7)     // Catch:{ all -> 0x05b0 }
                if (r4 == 0) goto L_0x0405
                r4 = 2
                r24 = r4
                goto L_0x0414
            L_0x0405:
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r4 = r1.cacheImage     // Catch:{ all -> 0x05b0 }
                java.lang.String r4 = r4.filter     // Catch:{ all -> 0x05b0 }
                java.lang.String r7 = "b"
                boolean r4 = r4.contains(r7)     // Catch:{ all -> 0x05b0 }
                if (r4 == 0) goto L_0x0414
                r4 = 1
                r24 = r4
            L_0x0414:
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r4 = r1.cacheImage     // Catch:{ all -> 0x05b0 }
                java.lang.String r4 = r4.filter     // Catch:{ all -> 0x05b0 }
                java.lang.String r7 = "i"
                boolean r4 = r4.contains(r7)     // Catch:{ all -> 0x05b0 }
                if (r4 == 0) goto L_0x0423
                r4 = 1
                r25 = r4
            L_0x0423:
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r4 = r1.cacheImage     // Catch:{ all -> 0x05b0 }
                java.lang.String r4 = r4.filter     // Catch:{ all -> 0x05b0 }
                java.lang.String r7 = "f"
                boolean r4 = r4.contains(r7)     // Catch:{ all -> 0x05b0 }
                if (r4 == 0) goto L_0x0432
                r4 = 1
                r26 = r4
            L_0x0432:
                if (r18 != 0) goto L_0x0546
                int r4 = (r22 > r27 ? 1 : (r22 == r27 ? 0 : -1))
                if (r4 == 0) goto L_0x0546
                int r4 = (r23 > r27 ? 1 : (r23 == r27 ? 0 : -1))
                if (r4 == 0) goto L_0x0546
                r3.inJustDecodeBounds = r5     // Catch:{ all -> 0x05b0 }
                if (r6 == 0) goto L_0x047a
                if (r2 != 0) goto L_0x047a
                if (r8 == 0) goto L_0x0460
                android.content.Context r4 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x045a }
                android.content.ContentResolver r4 = r4.getContentResolver()     // Catch:{ all -> 0x045a }
                r7 = r9
                r31 = r10
                long r9 = r6.longValue()     // Catch:{ all -> 0x0476 }
                android.provider.MediaStore.Video.Thumbnails.getThumbnail(r4, r9, r5, r3)     // Catch:{ all -> 0x0476 }
                r32 = r0
                r33 = r7
                goto L_0x04fb
            L_0x045a:
                r0 = move-exception
                r7 = r9
                r31 = r10
                goto L_0x05b5
            L_0x0460:
                r7 = r9
                r31 = r10
                android.content.Context r4 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0476 }
                android.content.ContentResolver r4 = r4.getContentResolver()     // Catch:{ all -> 0x0476 }
                long r9 = r6.longValue()     // Catch:{ all -> 0x0476 }
                android.provider.MediaStore.Images.Thumbnails.getThumbnail(r4, r9, r5, r3)     // Catch:{ all -> 0x0476 }
                r32 = r0
                r33 = r7
                goto L_0x04fb
            L_0x0476:
                r0 = move-exception
                r9 = r7
                goto L_0x05b5
            L_0x047a:
                r7 = r9
                r31 = r10
                if (r14 == 0) goto L_0x04df
                java.io.RandomAccessFile r4 = new java.io.RandomAccessFile     // Catch:{ all -> 0x04d8 }
                java.lang.String r9 = "r"
                r4.<init>(r12, r9)     // Catch:{ all -> 0x04d8 }
                long r9 = r4.length()     // Catch:{ all -> 0x04d8 }
                int r10 = (int) r9     // Catch:{ all -> 0x04d8 }
                java.lang.ThreadLocal r9 = im.bclpbkiauv.messenger.ImageLoader.bytesLocal     // Catch:{ all -> 0x04d8 }
                java.lang.Object r9 = r9.get()     // Catch:{ all -> 0x04d8 }
                byte[] r9 = (byte[]) r9     // Catch:{ all -> 0x04d8 }
                if (r9 == 0) goto L_0x049c
                int r5 = r9.length     // Catch:{ all -> 0x0476 }
                if (r5 < r10) goto L_0x049c
                r5 = r9
                goto L_0x049d
            L_0x049c:
                r5 = 0
            L_0x049d:
                if (r5 != 0) goto L_0x04ad
                r32 = r0
                byte[] r0 = new byte[r10]     // Catch:{ all -> 0x0476 }
                r5 = r0
                r9 = r0
                java.lang.ThreadLocal r0 = im.bclpbkiauv.messenger.ImageLoader.bytesLocal     // Catch:{ all -> 0x0476 }
                r0.set(r9)     // Catch:{ all -> 0x0476 }
                goto L_0x04af
            L_0x04ad:
                r32 = r0
            L_0x04af:
                r33 = r7
                r7 = 0
                r4.readFully(r5, r7, r10)     // Catch:{ all -> 0x05a3 }
                r4.close()     // Catch:{ all -> 0x05a3 }
                im.bclpbkiauv.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r5, (int) r7, (int) r10, (im.bclpbkiauv.messenger.SecureDocumentKey) r14)     // Catch:{ all -> 0x05a3 }
                byte[] r0 = im.bclpbkiauv.messenger.Utilities.computeSHA256(r5, r7, r10)     // Catch:{ all -> 0x05a3 }
                r7 = 0
                if (r15 == 0) goto L_0x04c8
                boolean r34 = java.util.Arrays.equals(r0, r15)     // Catch:{ all -> 0x05a3 }
                if (r34 != 0) goto L_0x04c9
            L_0x04c8:
                r7 = 1
            L_0x04c9:
                r34 = r0
                r17 = 0
                byte r0 = r5[r17]     // Catch:{ all -> 0x05a3 }
                r0 = r0 & 255(0xff, float:3.57E-43)
                int r10 = r10 - r0
                if (r7 != 0) goto L_0x04d7
                android.graphics.BitmapFactory.decodeByteArray(r5, r0, r10, r3)     // Catch:{ all -> 0x05a3 }
            L_0x04d7:
                goto L_0x04fb
            L_0x04d8:
                r0 = move-exception
                r33 = r7
                r9 = r33
                goto L_0x05b5
            L_0x04df:
                r32 = r0
                r33 = r7
                if (r13 == 0) goto L_0x04ef
                im.bclpbkiauv.messenger.secretmedia.EncryptedFileInputStream r0 = new im.bclpbkiauv.messenger.secretmedia.EncryptedFileInputStream     // Catch:{ all -> 0x05a3 }
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r4 = r1.cacheImage     // Catch:{ all -> 0x05a3 }
                java.io.File r4 = r4.encryptionKeyPath     // Catch:{ all -> 0x05a3 }
                r0.<init>((java.io.File) r12, (java.io.File) r4)     // Catch:{ all -> 0x05a3 }
                goto L_0x04f4
            L_0x04ef:
                java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ all -> 0x05a3 }
                r0.<init>(r12)     // Catch:{ all -> 0x05a3 }
            L_0x04f4:
                r4 = 0
                android.graphics.BitmapFactory.decodeStream(r0, r4, r3)     // Catch:{ all -> 0x05a3 }
                r0.close()     // Catch:{ all -> 0x05a3 }
            L_0x04fb:
                int r0 = r3.outWidth     // Catch:{ all -> 0x05a3 }
                float r0 = (float) r0     // Catch:{ all -> 0x05a3 }
                int r4 = r3.outHeight     // Catch:{ all -> 0x05a3 }
                float r4 = (float) r4     // Catch:{ all -> 0x05a3 }
                int r5 = (r22 > r23 ? 1 : (r22 == r23 ? 0 : -1))
                if (r5 < 0) goto L_0x0512
                int r5 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
                if (r5 <= 0) goto L_0x0512
                float r5 = r0 / r22
                float r7 = r4 / r23
                float r5 = java.lang.Math.max(r5, r7)     // Catch:{ all -> 0x05a3 }
                goto L_0x051a
            L_0x0512:
                float r5 = r0 / r22
                float r7 = r4 / r23
                float r5 = java.lang.Math.min(r5, r7)     // Catch:{ all -> 0x05a3 }
            L_0x051a:
                r7 = 1067030938(0x3f99999a, float:1.2)
                int r7 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
                if (r7 >= 0) goto L_0x0523
                r5 = 1065353216(0x3f800000, float:1.0)
            L_0x0523:
                r7 = 0
                r3.inJustDecodeBounds = r7     // Catch:{ all -> 0x05a3 }
                int r7 = (r5 > r28 ? 1 : (r5 == r28 ? 0 : -1))
                if (r7 <= 0) goto L_0x0541
                int r7 = (r0 > r22 ? 1 : (r0 == r22 ? 0 : -1))
                if (r7 > 0) goto L_0x0532
                int r7 = (r4 > r23 ? 1 : (r4 == r23 ? 0 : -1))
                if (r7 <= 0) goto L_0x0541
            L_0x0532:
                r7 = 1
            L_0x0533:
                r9 = 2
                int r7 = r7 * 2
                int r9 = r7 * 2
                float r9 = (float) r9     // Catch:{ all -> 0x05a3 }
                int r9 = (r9 > r5 ? 1 : (r9 == r5 ? 0 : -1))
                if (r9 < 0) goto L_0x0533
                r3.inSampleSize = r7     // Catch:{ all -> 0x05a3 }
                goto L_0x05a7
            L_0x0541:
                int r7 = (int) r5     // Catch:{ all -> 0x05a3 }
                r3.inSampleSize = r7     // Catch:{ all -> 0x05a3 }
                goto L_0x05a7
            L_0x0546:
                r32 = r0
                r33 = r9
                r31 = r10
                goto L_0x05a7
            L_0x054d:
                r33 = r9
                r31 = r10
                if (r2 == 0) goto L_0x05a7
                r4 = 1
                r3.inJustDecodeBounds = r4     // Catch:{ all -> 0x05a3 }
                if (r26 == 0) goto L_0x055b
                android.graphics.Bitmap$Config r0 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x05a3 }
                goto L_0x055d
            L_0x055b:
                android.graphics.Bitmap$Config r0 = android.graphics.Bitmap.Config.RGB_565     // Catch:{ all -> 0x05a3 }
            L_0x055d:
                r3.inPreferredConfig = r0     // Catch:{ all -> 0x05a3 }
                java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ all -> 0x05a3 }
                r0.<init>(r12)     // Catch:{ all -> 0x05a3 }
                r4 = 0
                android.graphics.Bitmap r5 = android.graphics.BitmapFactory.decodeStream(r0, r4, r3)     // Catch:{ all -> 0x05a3 }
                r9 = r5
                r0.close()     // Catch:{ all -> 0x05a1 }
                int r4 = r3.outWidth     // Catch:{ all -> 0x05a1 }
                int r5 = r3.outHeight     // Catch:{ all -> 0x05a1 }
                r7 = 0
                r3.inJustDecodeBounds = r7     // Catch:{ all -> 0x05a1 }
                int r7 = r4 / 200
                int r10 = r5 / 200
                int r7 = java.lang.Math.max(r7, r10)     // Catch:{ all -> 0x05a1 }
                float r7 = (float) r7     // Catch:{ all -> 0x05a1 }
                int r10 = (r7 > r28 ? 1 : (r7 == r28 ? 0 : -1))
                if (r10 >= 0) goto L_0x0583
                r7 = 1065353216(0x3f800000, float:1.0)
            L_0x0583:
                int r10 = (r7 > r28 ? 1 : (r7 == r28 ? 0 : -1))
                if (r10 <= 0) goto L_0x059b
                r10 = 1
            L_0x0588:
                r21 = 2
                int r10 = r10 * 2
                r28 = r0
                int r0 = r10 * 2
                float r0 = (float) r0     // Catch:{ all -> 0x05a1 }
                int r0 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
                if (r0 < 0) goto L_0x0598
                r3.inSampleSize = r10     // Catch:{ all -> 0x05a1 }
                goto L_0x05a9
            L_0x0598:
                r0 = r28
                goto L_0x0588
            L_0x059b:
                r28 = r0
                int r0 = (int) r7     // Catch:{ all -> 0x05a1 }
                r3.inSampleSize = r0     // Catch:{ all -> 0x05a1 }
                goto L_0x05a9
            L_0x05a1:
                r0 = move-exception
                goto L_0x05b5
            L_0x05a3:
                r0 = move-exception
                r9 = r33
                goto L_0x05b5
            L_0x05a7:
                r9 = r33
            L_0x05a9:
                r4 = r22
                r5 = r23
                r7 = r24
                goto L_0x05be
            L_0x05b0:
                r0 = move-exception
                r33 = r9
                r31 = r10
            L_0x05b5:
                im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
                r4 = r22
                r5 = r23
                r7 = r24
            L_0x05be:
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                int r0 = r0.imageType
                r22 = r11
                r10 = 1
                if (r0 != r10) goto L_0x083a
                im.bclpbkiauv.messenger.ImageLoader r0 = im.bclpbkiauv.messenger.ImageLoader.this     // Catch:{ all -> 0x081e }
                long r10 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x081e }
                long unused = r0.lastCacheOutTime = r10     // Catch:{ all -> 0x081e }
                java.lang.Object r10 = r1.sync     // Catch:{ all -> 0x081e }
                monitor-enter(r10)     // Catch:{ all -> 0x081e }
                boolean r0 = r1.isCancelled     // Catch:{ all -> 0x080d }
                if (r0 == 0) goto L_0x05e2
                monitor-exit(r10)     // Catch:{ all -> 0x05d9 }
                return
            L_0x05d9:
                r0 = move-exception
                r28 = r5
                r34 = r8
                r24 = r9
                goto L_0x0814
            L_0x05e2:
                monitor-exit(r10)     // Catch:{ all -> 0x080d }
                if (r18 == 0) goto L_0x063f
                java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ all -> 0x081e }
                java.lang.String r10 = "r"
                r0.<init>(r12, r10)     // Catch:{ all -> 0x081e }
                java.nio.channels.FileChannel r34 = r0.getChannel()     // Catch:{ all -> 0x081e }
                java.nio.channels.FileChannel$MapMode r35 = java.nio.channels.FileChannel.MapMode.READ_ONLY     // Catch:{ all -> 0x081e }
                r36 = 0
                long r38 = r12.length()     // Catch:{ all -> 0x081e }
                java.nio.MappedByteBuffer r10 = r34.map(r35, r36, r38)     // Catch:{ all -> 0x081e }
                android.graphics.BitmapFactory$Options r11 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x081e }
                r11.<init>()     // Catch:{ all -> 0x081e }
                r24 = r9
                r9 = 1
                r11.inJustDecodeBounds = r9     // Catch:{ all -> 0x0634 }
                int r9 = r10.limit()     // Catch:{ all -> 0x0634 }
                r28 = r5
                r34 = r8
                r5 = 0
                r8 = 1
                im.bclpbkiauv.messenger.Utilities.loadWebpImage(r5, r10, r9, r11, r8)     // Catch:{ all -> 0x0816 }
                int r5 = r11.outWidth     // Catch:{ all -> 0x0816 }
                int r8 = r11.outHeight     // Catch:{ all -> 0x0816 }
                android.graphics.Bitmap$Config r9 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0816 }
                android.graphics.Bitmap r5 = im.bclpbkiauv.messenger.Bitmaps.createBitmap(r5, r8, r9)     // Catch:{ all -> 0x0816 }
                r9 = r5
                int r5 = r10.limit()     // Catch:{ all -> 0x0668 }
                boolean r8 = r3.inPurgeable     // Catch:{ all -> 0x0668 }
                if (r8 != 0) goto L_0x0628
                r8 = 1
                goto L_0x0629
            L_0x0628:
                r8 = 0
            L_0x0629:
                r29 = r11
                r11 = 0
                im.bclpbkiauv.messenger.Utilities.loadWebpImage(r9, r10, r5, r11, r8)     // Catch:{ all -> 0x0668 }
                r0.close()     // Catch:{ all -> 0x0668 }
                goto L_0x06d8
            L_0x0634:
                r0 = move-exception
                r28 = r5
                r34 = r8
                r9 = r24
                r10 = r31
                goto L_0x0827
            L_0x063f:
                r28 = r5
                r34 = r8
                r24 = r9
                boolean r0 = r3.inPurgeable     // Catch:{ all -> 0x0816 }
                if (r0 != 0) goto L_0x066d
                if (r14 == 0) goto L_0x064c
                goto L_0x066d
            L_0x064c:
                if (r13 == 0) goto L_0x0658
                im.bclpbkiauv.messenger.secretmedia.EncryptedFileInputStream r0 = new im.bclpbkiauv.messenger.secretmedia.EncryptedFileInputStream     // Catch:{ all -> 0x0816 }
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r5 = r1.cacheImage     // Catch:{ all -> 0x0816 }
                java.io.File r5 = r5.encryptionKeyPath     // Catch:{ all -> 0x0816 }
                r0.<init>((java.io.File) r12, (java.io.File) r5)     // Catch:{ all -> 0x0816 }
                goto L_0x065d
            L_0x0658:
                java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ all -> 0x0816 }
                r0.<init>(r12)     // Catch:{ all -> 0x0816 }
            L_0x065d:
                r5 = 0
                android.graphics.Bitmap r8 = android.graphics.BitmapFactory.decodeStream(r0, r5, r3)     // Catch:{ all -> 0x0816 }
                r9 = r8
                r0.close()     // Catch:{ all -> 0x0668 }
                goto L_0x06d8
            L_0x0668:
                r0 = move-exception
                r10 = r31
                goto L_0x0827
            L_0x066d:
                java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ all -> 0x0816 }
                java.lang.String r5 = "r"
                r0.<init>(r12, r5)     // Catch:{ all -> 0x0816 }
                long r8 = r0.length()     // Catch:{ all -> 0x0816 }
                int r5 = (int) r8     // Catch:{ all -> 0x0816 }
                r8 = 0
                java.lang.ThreadLocal r9 = im.bclpbkiauv.messenger.ImageLoader.bytesThumbLocal     // Catch:{ all -> 0x0816 }
                java.lang.Object r9 = r9.get()     // Catch:{ all -> 0x0816 }
                byte[] r9 = (byte[]) r9     // Catch:{ all -> 0x0816 }
                if (r9 == 0) goto L_0x068b
                int r10 = r9.length     // Catch:{ all -> 0x0816 }
                if (r10 < r5) goto L_0x068b
                r10 = r9
                goto L_0x068c
            L_0x068b:
                r10 = 0
            L_0x068c:
                if (r10 != 0) goto L_0x0699
                byte[] r11 = new byte[r5]     // Catch:{ all -> 0x0816 }
                r10 = r11
                r9 = r11
                java.lang.ThreadLocal r11 = im.bclpbkiauv.messenger.ImageLoader.bytesThumbLocal     // Catch:{ all -> 0x0816 }
                r11.set(r9)     // Catch:{ all -> 0x0816 }
            L_0x0699:
                r11 = 0
                r0.readFully(r10, r11, r5)     // Catch:{ all -> 0x0816 }
                r0.close()     // Catch:{ all -> 0x0816 }
                r29 = 0
                if (r14 == 0) goto L_0x06c1
                im.bclpbkiauv.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r10, (int) r11, (int) r5, (im.bclpbkiauv.messenger.SecureDocumentKey) r14)     // Catch:{ all -> 0x0816 }
                byte[] r30 = im.bclpbkiauv.messenger.Utilities.computeSHA256(r10, r11, r5)     // Catch:{ all -> 0x0816 }
                r11 = r30
                if (r15 == 0) goto L_0x06b5
                boolean r30 = java.util.Arrays.equals(r11, r15)     // Catch:{ all -> 0x0816 }
                if (r30 != 0) goto L_0x06b7
            L_0x06b5:
                r29 = 1
            L_0x06b7:
                r30 = r0
                r17 = 0
                byte r0 = r10[r17]     // Catch:{ all -> 0x0816 }
                r8 = r0 & 255(0xff, float:3.57E-43)
                int r5 = r5 - r8
            L_0x06c0:
                goto L_0x06cd
            L_0x06c1:
                r30 = r0
                if (r13 == 0) goto L_0x06c0
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0816 }
                java.io.File r0 = r0.encryptionKeyPath     // Catch:{ all -> 0x0816 }
                r11 = 0
                im.bclpbkiauv.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r10, (int) r11, (int) r5, (java.io.File) r0)     // Catch:{ all -> 0x0816 }
            L_0x06cd:
                if (r29 != 0) goto L_0x06d5
                android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeByteArray(r10, r8, r5, r3)     // Catch:{ all -> 0x0816 }
                r9 = r0
                goto L_0x06d7
            L_0x06d5:
                r9 = r24
            L_0x06d7:
            L_0x06d8:
                if (r9 != 0) goto L_0x06f1
                long r10 = r12.length()     // Catch:{ all -> 0x0668 }
                r23 = 0
                int r0 = (r10 > r23 ? 1 : (r10 == r23 ? 0 : -1))
                if (r0 == 0) goto L_0x06ea
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0668 }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x0668 }
                if (r0 != 0) goto L_0x06ed
            L_0x06ea:
                r12.delete()     // Catch:{ all -> 0x0668 }
            L_0x06ed:
                r10 = r31
                goto L_0x082a
            L_0x06f1:
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0668 }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x0668 }
                if (r0 == 0) goto L_0x072c
                int r0 = r9.getWidth()     // Catch:{ all -> 0x0668 }
                float r0 = (float) r0     // Catch:{ all -> 0x0668 }
                int r5 = r9.getHeight()     // Catch:{ all -> 0x0668 }
                float r5 = (float) r5     // Catch:{ all -> 0x0668 }
                boolean r8 = r3.inPurgeable     // Catch:{ all -> 0x0668 }
                if (r8 != 0) goto L_0x072a
                int r8 = (r4 > r27 ? 1 : (r4 == r27 ? 0 : -1))
                if (r8 == 0) goto L_0x072a
                int r8 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
                if (r8 == 0) goto L_0x072a
                r8 = 1101004800(0x41a00000, float:20.0)
                float r10 = r4 + r8
                int r8 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
                if (r8 <= 0) goto L_0x072a
                float r8 = r0 / r4
                int r10 = (int) r4     // Catch:{ all -> 0x0668 }
                float r11 = r5 / r8
                int r11 = (int) r11     // Catch:{ all -> 0x0668 }
                r23 = r5
                r5 = 1
                android.graphics.Bitmap r10 = im.bclpbkiauv.messenger.Bitmaps.createScaledBitmap(r9, r10, r11, r5)     // Catch:{ all -> 0x0668 }
                r5 = r10
                if (r9 == r5) goto L_0x072c
                r9.recycle()     // Catch:{ all -> 0x0668 }
                r9 = r5
                goto L_0x072c
            L_0x072a:
                r23 = r5
            L_0x072c:
                if (r25 == 0) goto L_0x074c
                boolean r0 = r3.inPurgeable     // Catch:{ all -> 0x0668 }
                if (r0 == 0) goto L_0x0734
                r0 = 0
                goto L_0x0735
            L_0x0734:
                r0 = 1
            L_0x0735:
                int r5 = r9.getWidth()     // Catch:{ all -> 0x0668 }
                int r8 = r9.getHeight()     // Catch:{ all -> 0x0668 }
                int r10 = r9.getRowBytes()     // Catch:{ all -> 0x0668 }
                int r0 = im.bclpbkiauv.messenger.Utilities.needInvert(r9, r0, r5, r8, r10)     // Catch:{ all -> 0x0668 }
                if (r0 == 0) goto L_0x0749
                r0 = 1
                goto L_0x074a
            L_0x0749:
                r0 = 0
            L_0x074a:
                r10 = r0
                goto L_0x074e
            L_0x074c:
                r10 = r31
            L_0x074e:
                r5 = 1
                if (r7 != r5) goto L_0x077a
                android.graphics.Bitmap$Config r0 = r9.getConfig()     // Catch:{ all -> 0x0777 }
                android.graphics.Bitmap$Config r5 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0777 }
                if (r0 != r5) goto L_0x082a
                r36 = 3
                boolean r0 = r3.inPurgeable     // Catch:{ all -> 0x0777 }
                if (r0 == 0) goto L_0x0762
                r37 = 0
                goto L_0x0764
            L_0x0762:
                r37 = 1
            L_0x0764:
                int r38 = r9.getWidth()     // Catch:{ all -> 0x0777 }
                int r39 = r9.getHeight()     // Catch:{ all -> 0x0777 }
                int r40 = r9.getRowBytes()     // Catch:{ all -> 0x0777 }
                r35 = r9
                im.bclpbkiauv.messenger.Utilities.blurBitmap(r35, r36, r37, r38, r39, r40)     // Catch:{ all -> 0x0777 }
                goto L_0x082a
            L_0x0777:
                r0 = move-exception
                goto L_0x0827
            L_0x077a:
                r5 = 2
                if (r7 != r5) goto L_0x07a3
                android.graphics.Bitmap$Config r0 = r9.getConfig()     // Catch:{ all -> 0x0777 }
                android.graphics.Bitmap$Config r5 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0777 }
                if (r0 != r5) goto L_0x082a
                r36 = 1
                boolean r0 = r3.inPurgeable     // Catch:{ all -> 0x0777 }
                if (r0 == 0) goto L_0x078e
                r37 = 0
                goto L_0x0790
            L_0x078e:
                r37 = 1
            L_0x0790:
                int r38 = r9.getWidth()     // Catch:{ all -> 0x0777 }
                int r39 = r9.getHeight()     // Catch:{ all -> 0x0777 }
                int r40 = r9.getRowBytes()     // Catch:{ all -> 0x0777 }
                r35 = r9
                im.bclpbkiauv.messenger.Utilities.blurBitmap(r35, r36, r37, r38, r39, r40)     // Catch:{ all -> 0x0777 }
                goto L_0x082a
            L_0x07a3:
                r5 = 3
                if (r7 != r5) goto L_0x0803
                android.graphics.Bitmap$Config r0 = r9.getConfig()     // Catch:{ all -> 0x0777 }
                android.graphics.Bitmap$Config r5 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0777 }
                if (r0 != r5) goto L_0x082a
                r36 = 7
                boolean r0 = r3.inPurgeable     // Catch:{ all -> 0x0777 }
                if (r0 == 0) goto L_0x07b7
                r37 = 0
                goto L_0x07b9
            L_0x07b7:
                r37 = 1
            L_0x07b9:
                int r38 = r9.getWidth()     // Catch:{ all -> 0x0777 }
                int r39 = r9.getHeight()     // Catch:{ all -> 0x0777 }
                int r40 = r9.getRowBytes()     // Catch:{ all -> 0x0777 }
                r35 = r9
                im.bclpbkiauv.messenger.Utilities.blurBitmap(r35, r36, r37, r38, r39, r40)     // Catch:{ all -> 0x0777 }
                r36 = 7
                boolean r0 = r3.inPurgeable     // Catch:{ all -> 0x0777 }
                if (r0 == 0) goto L_0x07d3
                r37 = 0
                goto L_0x07d5
            L_0x07d3:
                r37 = 1
            L_0x07d5:
                int r38 = r9.getWidth()     // Catch:{ all -> 0x0777 }
                int r39 = r9.getHeight()     // Catch:{ all -> 0x0777 }
                int r40 = r9.getRowBytes()     // Catch:{ all -> 0x0777 }
                r35 = r9
                im.bclpbkiauv.messenger.Utilities.blurBitmap(r35, r36, r37, r38, r39, r40)     // Catch:{ all -> 0x0777 }
                r36 = 7
                boolean r0 = r3.inPurgeable     // Catch:{ all -> 0x0777 }
                if (r0 == 0) goto L_0x07ef
                r37 = 0
                goto L_0x07f1
            L_0x07ef:
                r37 = 1
            L_0x07f1:
                int r38 = r9.getWidth()     // Catch:{ all -> 0x0777 }
                int r39 = r9.getHeight()     // Catch:{ all -> 0x0777 }
                int r40 = r9.getRowBytes()     // Catch:{ all -> 0x0777 }
                r35 = r9
                im.bclpbkiauv.messenger.Utilities.blurBitmap(r35, r36, r37, r38, r39, r40)     // Catch:{ all -> 0x0777 }
                goto L_0x082a
            L_0x0803:
                if (r7 != 0) goto L_0x082a
                boolean r0 = r3.inPurgeable     // Catch:{ all -> 0x0777 }
                if (r0 == 0) goto L_0x082a
                im.bclpbkiauv.messenger.Utilities.pinBitmap(r9)     // Catch:{ all -> 0x0777 }
                goto L_0x082a
            L_0x080d:
                r0 = move-exception
                r28 = r5
                r34 = r8
                r24 = r9
            L_0x0814:
                monitor-exit(r10)     // Catch:{ all -> 0x081c }
                throw r0     // Catch:{ all -> 0x0816 }
            L_0x0816:
                r0 = move-exception
                r9 = r24
                r10 = r31
                goto L_0x0827
            L_0x081c:
                r0 = move-exception
                goto L_0x0814
            L_0x081e:
                r0 = move-exception
                r28 = r5
                r34 = r8
                r24 = r9
                r10 = r31
            L_0x0827:
                im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x082a:
                r29 = r2
                r23 = r4
                r21 = r13
                r35 = r14
                r11 = r22
                r13 = r28
                r28 = r6
                goto L_0x0c09
            L_0x083a:
                r28 = r5
                r34 = r8
                r24 = r9
                r0 = 20
                if (r6 == 0) goto L_0x0847
                r0 = 0
                r5 = r0
                goto L_0x0848
            L_0x0847:
                r5 = r0
            L_0x0848:
                if (r5 == 0) goto L_0x089b
                im.bclpbkiauv.messenger.ImageLoader r0 = im.bclpbkiauv.messenger.ImageLoader.this     // Catch:{ all -> 0x0886 }
                long r8 = r0.lastCacheOutTime     // Catch:{ all -> 0x0886 }
                r10 = 0
                int r0 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
                if (r0 == 0) goto L_0x089b
                im.bclpbkiauv.messenger.ImageLoader r0 = im.bclpbkiauv.messenger.ImageLoader.this     // Catch:{ all -> 0x0886 }
                long r8 = r0.lastCacheOutTime     // Catch:{ all -> 0x0886 }
                long r10 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0886 }
                r21 = r13
                r35 = r14
                long r13 = (long) r5
                long r10 = r10 - r13
                int r0 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
                if (r0 <= 0) goto L_0x089f
                int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x0875 }
                r8 = 21
                if (r0 >= r8) goto L_0x089f
                long r8 = (long) r5     // Catch:{ all -> 0x0875 }
                java.lang.Thread.sleep(r8)     // Catch:{ all -> 0x0875 }
                goto L_0x089f
            L_0x0875:
                r0 = move-exception
                r29 = r2
                r23 = r4
                r11 = r22
                r9 = r24
                r13 = r28
                r10 = r31
                r28 = r6
                goto L_0x0c09
            L_0x0886:
                r0 = move-exception
                r21 = r13
                r29 = r2
                r23 = r4
                r35 = r14
                r11 = r22
                r9 = r24
                r13 = r28
                r10 = r31
                r28 = r6
                goto L_0x0c09
            L_0x089b:
                r21 = r13
                r35 = r14
            L_0x089f:
                im.bclpbkiauv.messenger.ImageLoader r0 = im.bclpbkiauv.messenger.ImageLoader.this     // Catch:{ all -> 0x0bfa }
                long r8 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0bfa }
                long unused = r0.lastCacheOutTime = r8     // Catch:{ all -> 0x0bfa }
                java.lang.Object r8 = r1.sync     // Catch:{ all -> 0x0bfa }
                monitor-enter(r8)     // Catch:{ all -> 0x0bfa }
                boolean r0 = r1.isCancelled     // Catch:{ all -> 0x0be3 }
                if (r0 == 0) goto L_0x08be
                monitor-exit(r8)     // Catch:{ all -> 0x08b1 }
                return
            L_0x08b1:
                r0 = move-exception
                r29 = r2
                r23 = r4
                r27 = r5
                r13 = r28
                r28 = r6
                goto L_0x0bee
            L_0x08be:
                monitor-exit(r8)     // Catch:{ all -> 0x0be3 }
                if (r26 != 0) goto L_0x08d7
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0875 }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x0875 }
                if (r0 == 0) goto L_0x08d7
                if (r7 != 0) goto L_0x08d7
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0875 }
                im.bclpbkiauv.messenger.ImageLocation r0 = r0.imageLocation     // Catch:{ all -> 0x0875 }
                java.lang.String r0 = r0.path     // Catch:{ all -> 0x0875 }
                if (r0 == 0) goto L_0x08d2
                goto L_0x08d7
            L_0x08d2:
                android.graphics.Bitmap$Config r0 = android.graphics.Bitmap.Config.RGB_565     // Catch:{ all -> 0x0875 }
                r3.inPreferredConfig = r0     // Catch:{ all -> 0x0875 }
                goto L_0x08db
            L_0x08d7:
                android.graphics.Bitmap$Config r0 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0bfa }
                r3.inPreferredConfig = r0     // Catch:{ all -> 0x0bfa }
            L_0x08db:
                r8 = 0
                r3.inDither = r8     // Catch:{ all -> 0x0bfa }
                if (r6 == 0) goto L_0x0906
                if (r2 != 0) goto L_0x0906
                if (r34 == 0) goto L_0x08f5
                android.content.Context r0 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0875 }
                android.content.ContentResolver r0 = r0.getContentResolver()     // Catch:{ all -> 0x0875 }
                long r8 = r6.longValue()     // Catch:{ all -> 0x0875 }
                r10 = 1
                android.graphics.Bitmap r0 = android.provider.MediaStore.Video.Thumbnails.getThumbnail(r0, r8, r10, r3)     // Catch:{ all -> 0x0875 }
                r9 = r0
                goto L_0x0908
            L_0x08f5:
                android.content.Context r0 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0875 }
                android.content.ContentResolver r0 = r0.getContentResolver()     // Catch:{ all -> 0x0875 }
                long r8 = r6.longValue()     // Catch:{ all -> 0x0875 }
                r10 = 1
                android.graphics.Bitmap r0 = android.provider.MediaStore.Images.Thumbnails.getThumbnail(r0, r8, r10, r3)     // Catch:{ all -> 0x0875 }
                r9 = r0
                goto L_0x0908
            L_0x0906:
                r9 = r24
            L_0x0908:
                if (r9 != 0) goto L_0x0a6c
                if (r18 == 0) goto L_0x0967
                java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ all -> 0x0958 }
                java.lang.String r8 = "r"
                r0.<init>(r12, r8)     // Catch:{ all -> 0x0958 }
                java.nio.channels.FileChannel r36 = r0.getChannel()     // Catch:{ all -> 0x0958 }
                java.nio.channels.FileChannel$MapMode r37 = java.nio.channels.FileChannel.MapMode.READ_ONLY     // Catch:{ all -> 0x0958 }
                r38 = 0
                long r40 = r12.length()     // Catch:{ all -> 0x0958 }
                java.nio.MappedByteBuffer r8 = r36.map(r37, r38, r40)     // Catch:{ all -> 0x0958 }
                android.graphics.BitmapFactory$Options r10 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x0958 }
                r10.<init>()     // Catch:{ all -> 0x0958 }
                r11 = 1
                r10.inJustDecodeBounds = r11     // Catch:{ all -> 0x0958 }
                int r13 = r8.limit()     // Catch:{ all -> 0x0958 }
                r14 = 0
                im.bclpbkiauv.messenger.Utilities.loadWebpImage(r14, r8, r13, r10, r11)     // Catch:{ all -> 0x0958 }
                int r11 = r10.outWidth     // Catch:{ all -> 0x0958 }
                int r13 = r10.outHeight     // Catch:{ all -> 0x0958 }
                android.graphics.Bitmap$Config r14 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0958 }
                android.graphics.Bitmap r11 = im.bclpbkiauv.messenger.Bitmaps.createBitmap(r11, r13, r14)     // Catch:{ all -> 0x0958 }
                r9 = r11
                int r11 = r8.limit()     // Catch:{ all -> 0x0958 }
                boolean r13 = r3.inPurgeable     // Catch:{ all -> 0x0958 }
                if (r13 != 0) goto L_0x0948
                r13 = 1
                goto L_0x0949
            L_0x0948:
                r13 = 0
            L_0x0949:
                r14 = 0
                im.bclpbkiauv.messenger.Utilities.loadWebpImage(r9, r8, r11, r14, r13)     // Catch:{ all -> 0x0958 }
                r0.close()     // Catch:{ all -> 0x0958 }
                r29 = r2
                r11 = r22
                r2 = r35
                goto L_0x0a72
            L_0x0958:
                r0 = move-exception
                r29 = r2
                r23 = r4
                r11 = r22
                r13 = r28
                r10 = r31
                r28 = r6
                goto L_0x0c09
            L_0x0967:
                boolean r0 = r3.inPurgeable     // Catch:{ all -> 0x0a5d }
                if (r0 != 0) goto L_0x09db
                if (r35 == 0) goto L_0x0970
                r10 = 0
                goto L_0x09dc
            L_0x0970:
                if (r21 == 0) goto L_0x097d
                im.bclpbkiauv.messenger.secretmedia.EncryptedFileInputStream r0 = new im.bclpbkiauv.messenger.secretmedia.EncryptedFileInputStream     // Catch:{ all -> 0x0958 }
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r8 = r1.cacheImage     // Catch:{ all -> 0x0958 }
                java.io.File r8 = r8.encryptionKeyPath     // Catch:{ all -> 0x0958 }
                r0.<init>((java.io.File) r12, (java.io.File) r8)     // Catch:{ all -> 0x0958 }
                r8 = r0
                goto L_0x0983
            L_0x097d:
                java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ all -> 0x0958 }
                r0.<init>(r12)     // Catch:{ all -> 0x0958 }
                r8 = r0
            L_0x0983:
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0958 }
                im.bclpbkiauv.messenger.ImageLocation r0 = r0.imageLocation     // Catch:{ all -> 0x0958 }
                im.bclpbkiauv.tgnet.TLRPC$Document r0 = r0.document     // Catch:{ all -> 0x0958 }
                boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_document     // Catch:{ all -> 0x0958 }
                if (r0 == 0) goto L_0x09bd
                androidx.exifinterface.media.ExifInterface r0 = new androidx.exifinterface.media.ExifInterface     // Catch:{ all -> 0x09b0 }
                r0.<init>((java.io.InputStream) r8)     // Catch:{ all -> 0x09b0 }
                java.lang.String r10 = "Orientation"
                r11 = 1
                int r10 = r0.getAttributeInt(r10, r11)     // Catch:{ all -> 0x09b0 }
                r11 = 3
                if (r10 == r11) goto L_0x09ac
                r11 = 6
                if (r10 == r11) goto L_0x09a9
                r11 = 8
                if (r10 == r11) goto L_0x09a6
                r11 = r22
                goto L_0x09af
            L_0x09a6:
                r11 = 270(0x10e, float:3.78E-43)
                goto L_0x09af
            L_0x09a9:
                r11 = 90
                goto L_0x09af
            L_0x09ac:
                r11 = 180(0xb4, float:2.52E-43)
            L_0x09af:
                goto L_0x09b3
            L_0x09b0:
                r0 = move-exception
                r11 = r22
            L_0x09b3:
                java.nio.channels.FileChannel r0 = r8.getChannel()     // Catch:{ all -> 0x09ce }
                r13 = 0
                r0.position(r13)     // Catch:{ all -> 0x09ce }
                goto L_0x09bf
            L_0x09bd:
                r11 = r22
            L_0x09bf:
                r10 = 0
                android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeStream(r8, r10, r3)     // Catch:{ all -> 0x09ce }
                r9 = r0
                r8.close()     // Catch:{ all -> 0x09ce }
                r29 = r2
                r2 = r35
                goto L_0x0a72
            L_0x09ce:
                r0 = move-exception
                r29 = r2
                r23 = r4
                r13 = r28
                r10 = r31
                r28 = r6
                goto L_0x0c09
            L_0x09db:
                r10 = 0
            L_0x09dc:
                java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ all -> 0x0a5d }
                java.lang.String r8 = "r"
                r0.<init>(r12, r8)     // Catch:{ all -> 0x0a5d }
                long r13 = r0.length()     // Catch:{ all -> 0x0a5d }
                int r8 = (int) r13     // Catch:{ all -> 0x0a5d }
                r11 = 0
                java.lang.ThreadLocal r13 = im.bclpbkiauv.messenger.ImageLoader.bytesLocal     // Catch:{ all -> 0x0a5d }
                java.lang.Object r13 = r13.get()     // Catch:{ all -> 0x0a5d }
                byte[] r13 = (byte[]) r13     // Catch:{ all -> 0x0a5d }
                if (r13 == 0) goto L_0x09fa
                int r14 = r13.length     // Catch:{ all -> 0x0958 }
                if (r14 < r8) goto L_0x09fa
                r14 = r13
                goto L_0x09fb
            L_0x09fa:
                r14 = r10
            L_0x09fb:
                if (r14 != 0) goto L_0x0a08
                byte[] r10 = new byte[r8]     // Catch:{ all -> 0x0958 }
                r14 = r10
                r13 = r10
                java.lang.ThreadLocal r10 = im.bclpbkiauv.messenger.ImageLoader.bytesLocal     // Catch:{ all -> 0x0958 }
                r10.set(r13)     // Catch:{ all -> 0x0958 }
            L_0x0a08:
                r10 = 0
                r0.readFully(r14, r10, r8)     // Catch:{ all -> 0x0a5d }
                r0.close()     // Catch:{ all -> 0x0a5d }
                r19 = 0
                if (r35 == 0) goto L_0x0a43
                r29 = r2
                r2 = r35
                im.bclpbkiauv.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r14, (int) r10, (int) r8, (im.bclpbkiauv.messenger.SecureDocumentKey) r2)     // Catch:{ all -> 0x0a34 }
                byte[] r24 = im.bclpbkiauv.messenger.Utilities.computeSHA256(r14, r10, r8)     // Catch:{ all -> 0x0a34 }
                r10 = r24
                if (r15 == 0) goto L_0x0a28
                boolean r24 = java.util.Arrays.equals(r10, r15)     // Catch:{ all -> 0x0a34 }
                if (r24 != 0) goto L_0x0a2a
            L_0x0a28:
                r19 = 1
            L_0x0a2a:
                r24 = r0
                r17 = 0
                byte r0 = r14[r17]     // Catch:{ all -> 0x0a34 }
                r11 = r0 & 255(0xff, float:3.57E-43)
                int r8 = r8 - r11
            L_0x0a33:
                goto L_0x0a53
            L_0x0a34:
                r0 = move-exception
                r35 = r2
                r23 = r4
                r11 = r22
                r13 = r28
                r10 = r31
                r28 = r6
                goto L_0x0c09
            L_0x0a43:
                r24 = r0
                r29 = r2
                r2 = r35
                if (r21 == 0) goto L_0x0a33
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0a34 }
                java.io.File r0 = r0.encryptionKeyPath     // Catch:{ all -> 0x0a34 }
                r10 = 0
                im.bclpbkiauv.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r14, (int) r10, (int) r8, (java.io.File) r0)     // Catch:{ all -> 0x0a34 }
            L_0x0a53:
                if (r19 != 0) goto L_0x0a5a
                android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeByteArray(r14, r11, r8, r3)     // Catch:{ all -> 0x0a34 }
                r9 = r0
            L_0x0a5a:
                r11 = r22
                goto L_0x0a72
            L_0x0a5d:
                r0 = move-exception
                r29 = r2
                r23 = r4
                r11 = r22
                r13 = r28
                r10 = r31
                r28 = r6
                goto L_0x0c09
            L_0x0a6c:
                r29 = r2
                r2 = r35
                r11 = r22
            L_0x0a72:
                if (r9 != 0) goto L_0x0aa3
                if (r16 == 0) goto L_0x0a97
                long r13 = r12.length()     // Catch:{ all -> 0x0a8a }
                r22 = 0
                int r0 = (r13 > r22 ? 1 : (r13 == r22 ? 0 : -1))
                if (r0 == 0) goto L_0x0a86
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0a8a }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x0a8a }
                if (r0 != 0) goto L_0x0a97
            L_0x0a86:
                r12.delete()     // Catch:{ all -> 0x0a8a }
                goto L_0x0a97
            L_0x0a8a:
                r0 = move-exception
                r35 = r2
                r23 = r4
                r13 = r28
                r10 = r31
                r28 = r6
                goto L_0x0c09
            L_0x0a97:
                r35 = r2
                r23 = r4
                r13 = r28
                r10 = r31
                r28 = r6
                goto L_0x0bd6
            L_0x0aa3:
                r0 = 0
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r8 = r1.cacheImage     // Catch:{ all -> 0x0bd7 }
                java.lang.String r8 = r8.filter     // Catch:{ all -> 0x0bd7 }
                if (r8 == 0) goto L_0x0bbc
                int r8 = r9.getWidth()     // Catch:{ all -> 0x0bd7 }
                float r8 = (float) r8     // Catch:{ all -> 0x0bd7 }
                int r10 = r9.getHeight()     // Catch:{ all -> 0x0bd7 }
                float r10 = (float) r10     // Catch:{ all -> 0x0bd7 }
                boolean r13 = r3.inPurgeable     // Catch:{ all -> 0x0bd7 }
                if (r13 != 0) goto L_0x0b12
                int r13 = (r4 > r27 ? 1 : (r4 == r27 ? 0 : -1))
                if (r13 == 0) goto L_0x0b12
                int r13 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1))
                if (r13 == 0) goto L_0x0b12
                r13 = 1101004800(0x41a00000, float:20.0)
                float r13 = r13 + r4
                int r13 = (r8 > r13 ? 1 : (r8 == r13 ? 0 : -1))
                if (r13 <= 0) goto L_0x0b12
                int r13 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
                if (r13 <= 0) goto L_0x0aef
                int r13 = (r4 > r28 ? 1 : (r4 == r28 ? 0 : -1))
                if (r13 <= 0) goto L_0x0aef
                float r13 = r8 / r4
                int r14 = (int) r4
                r19 = r0
                float r0 = r10 / r13
                int r0 = (int) r0
                r35 = r2
                r2 = 1
                android.graphics.Bitmap r0 = im.bclpbkiauv.messenger.Bitmaps.createScaledBitmap(r9, r14, r0, r2)     // Catch:{ all -> 0x0ae4 }
                r23 = r4
                r13 = r28
                r4 = 1
                goto L_0x0b03
            L_0x0ae4:
                r0 = move-exception
                r23 = r4
                r13 = r28
                r10 = r31
                r28 = r6
                goto L_0x0c09
            L_0x0aef:
                r19 = r0
                r35 = r2
                float r0 = r10 / r28
                float r2 = r8 / r0
                int r2 = (int) r2
                r13 = r28
                int r14 = (int) r13
                r23 = r4
                r4 = 1
                android.graphics.Bitmap r2 = im.bclpbkiauv.messenger.Bitmaps.createScaledBitmap(r9, r2, r14, r4)     // Catch:{ all -> 0x0b0b }
                r0 = r2
            L_0x0b03:
                if (r9 == r0) goto L_0x0b1b
                r9.recycle()     // Catch:{ all -> 0x0b0b }
                r2 = r0
                r9 = r2
                goto L_0x0b1b
            L_0x0b0b:
                r0 = move-exception
                r28 = r6
                r10 = r31
                goto L_0x0c09
            L_0x0b12:
                r19 = r0
                r35 = r2
                r23 = r4
                r13 = r28
                r4 = 1
            L_0x0b1b:
                if (r9 == 0) goto L_0x0bb3
                if (r25 == 0) goto L_0x0b71
                r0 = r9
                int r2 = r9.getWidth()     // Catch:{ all -> 0x0b6a }
                int r14 = r9.getHeight()     // Catch:{ all -> 0x0b6a }
                int r4 = r2 * r14
                r22 = r0
                r0 = 22500(0x57e4, float:3.1529E-41)
                if (r4 <= r0) goto L_0x0b38
                r0 = 100
                r4 = 0
                android.graphics.Bitmap r0 = im.bclpbkiauv.messenger.Bitmaps.createScaledBitmap(r9, r0, r0, r4)     // Catch:{ all -> 0x0b0b }
                goto L_0x0b3b
            L_0x0b38:
                r4 = 0
                r0 = r22
            L_0x0b3b:
                boolean r4 = r3.inPurgeable     // Catch:{ all -> 0x0b6a }
                if (r4 == 0) goto L_0x0b41
                r4 = 0
                goto L_0x0b42
            L_0x0b41:
                r4 = 1
            L_0x0b42:
                r22 = r2
                int r2 = r0.getWidth()     // Catch:{ all -> 0x0b6a }
                r27 = r5
                int r5 = r0.getHeight()     // Catch:{ all -> 0x0b6a }
                r28 = r6
                int r6 = r0.getRowBytes()     // Catch:{ all -> 0x0ba9 }
                int r2 = im.bclpbkiauv.messenger.Utilities.needInvert(r0, r4, r2, r5, r6)     // Catch:{ all -> 0x0ba9 }
                if (r2 == 0) goto L_0x0b5c
                r2 = 1
                goto L_0x0b5d
            L_0x0b5c:
                r2 = 0
            L_0x0b5d:
                if (r0 == r9) goto L_0x0b67
                r0.recycle()     // Catch:{ all -> 0x0b63 }
                goto L_0x0b67
            L_0x0b63:
                r0 = move-exception
                r10 = r2
                goto L_0x0c09
            L_0x0b67:
                r31 = r2
                goto L_0x0b75
            L_0x0b6a:
                r0 = move-exception
                r28 = r6
                r10 = r31
                goto L_0x0c09
            L_0x0b71:
                r27 = r5
                r28 = r6
            L_0x0b75:
                if (r7 == 0) goto L_0x0bae
                r0 = 1120403456(0x42c80000, float:100.0)
                int r2 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1))
                if (r2 >= 0) goto L_0x0bae
                int r0 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1))
                if (r0 >= 0) goto L_0x0bae
                android.graphics.Bitmap$Config r0 = r9.getConfig()     // Catch:{ all -> 0x0ba9 }
                android.graphics.Bitmap$Config r2 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0ba9 }
                if (r0 != r2) goto L_0x0ba5
                r37 = 3
                boolean r0 = r3.inPurgeable     // Catch:{ all -> 0x0ba9 }
                if (r0 == 0) goto L_0x0b92
                r38 = 0
                goto L_0x0b94
            L_0x0b92:
                r38 = 1
            L_0x0b94:
                int r39 = r9.getWidth()     // Catch:{ all -> 0x0ba9 }
                int r40 = r9.getHeight()     // Catch:{ all -> 0x0ba9 }
                int r41 = r9.getRowBytes()     // Catch:{ all -> 0x0ba9 }
                r36 = r9
                im.bclpbkiauv.messenger.Utilities.blurBitmap(r36, r37, r38, r39, r40, r41)     // Catch:{ all -> 0x0ba9 }
            L_0x0ba5:
                r0 = 1
                r10 = r31
                goto L_0x0bca
            L_0x0ba9:
                r0 = move-exception
                r10 = r31
                goto L_0x0c09
            L_0x0bae:
                r0 = r19
                r10 = r31
                goto L_0x0bca
            L_0x0bb3:
                r27 = r5
                r28 = r6
                r0 = r19
                r10 = r31
                goto L_0x0bca
            L_0x0bbc:
                r19 = r0
                r35 = r2
                r23 = r4
                r27 = r5
                r13 = r28
                r28 = r6
                r10 = r31
            L_0x0bca:
                if (r0 != 0) goto L_0x0bd6
                boolean r2 = r3.inPurgeable     // Catch:{ all -> 0x0bd4 }
                if (r2 == 0) goto L_0x0bd6
                im.bclpbkiauv.messenger.Utilities.pinBitmap(r9)     // Catch:{ all -> 0x0bd4 }
                goto L_0x0bd6
            L_0x0bd4:
                r0 = move-exception
                goto L_0x0c09
            L_0x0bd6:
                goto L_0x0c09
            L_0x0bd7:
                r0 = move-exception
                r35 = r2
                r23 = r4
                r13 = r28
                r28 = r6
                r10 = r31
                goto L_0x0c09
            L_0x0be3:
                r0 = move-exception
                r29 = r2
                r23 = r4
                r27 = r5
                r13 = r28
                r28 = r6
            L_0x0bee:
                monitor-exit(r8)     // Catch:{ all -> 0x0bf8 }
                throw r0     // Catch:{ all -> 0x0bf0 }
            L_0x0bf0:
                r0 = move-exception
                r11 = r22
                r9 = r24
                r10 = r31
                goto L_0x0c09
            L_0x0bf8:
                r0 = move-exception
                goto L_0x0bee
            L_0x0bfa:
                r0 = move-exception
                r29 = r2
                r23 = r4
                r13 = r28
                r28 = r6
                r11 = r22
                r9 = r24
                r10 = r31
            L_0x0c09:
                java.lang.Thread.interrupted()
                if (r10 != 0) goto L_0x0c1e
                if (r11 == 0) goto L_0x0c11
                goto L_0x0c1e
            L_0x0c11:
                if (r9 == 0) goto L_0x0c19
                android.graphics.drawable.BitmapDrawable r4 = new android.graphics.drawable.BitmapDrawable
                r4.<init>(r9)
                goto L_0x0c1a
            L_0x0c19:
                r4 = 0
            L_0x0c1a:
                r1.onPostExecute(r4)
                goto L_0x0c2a
            L_0x0c1e:
                if (r9 == 0) goto L_0x0c26
                im.bclpbkiauv.messenger.ExtendedBitmapDrawable r4 = new im.bclpbkiauv.messenger.ExtendedBitmapDrawable
                r4.<init>(r9, r10, r11)
                goto L_0x0c27
            L_0x0c26:
                r4 = 0
            L_0x0c27:
                r1.onPostExecute(r4)
            L_0x0c2a:
                return
            L_0x0c2b:
                r0 = move-exception
                monitor-exit(r2)     // Catch:{ all -> 0x0c2b }
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.ImageLoader.CacheOutTask.run():void");
        }

        private void onPostExecute(Drawable drawable) {
            AndroidUtilities.runOnUIThread(new Runnable(drawable) {
                private final /* synthetic */ Drawable f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ImageLoader.CacheOutTask.this.lambda$onPostExecute$1$ImageLoader$CacheOutTask(this.f$1);
                }
            });
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v3, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: android.graphics.drawable.Drawable} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v11, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v7, resolved type: android.graphics.drawable.Drawable} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$onPostExecute$1$ImageLoader$CacheOutTask(android.graphics.drawable.Drawable r7) {
            /*
                r6 = this;
                r0 = 0
                r1 = 0
                boolean r2 = r7 instanceof im.bclpbkiauv.ui.components.RLottieDrawable
                if (r2 == 0) goto L_0x003e
                r2 = r7
                im.bclpbkiauv.ui.components.RLottieDrawable r2 = (im.bclpbkiauv.ui.components.RLottieDrawable) r2
                im.bclpbkiauv.messenger.ImageLoader r3 = im.bclpbkiauv.messenger.ImageLoader.this
                im.bclpbkiauv.messenger.LruCache r3 = r3.lottieMemCache
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r4 = r6.cacheImage
                java.lang.String r4 = r4.key
                java.lang.Object r3 = r3.get(r4)
                r0 = r3
                android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
                if (r0 != 0) goto L_0x002b
                im.bclpbkiauv.messenger.ImageLoader r3 = im.bclpbkiauv.messenger.ImageLoader.this
                im.bclpbkiauv.messenger.LruCache r3 = r3.lottieMemCache
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r4 = r6.cacheImage
                java.lang.String r4 = r4.key
                r3.put(r4, r2)
                r0 = r2
                goto L_0x002e
            L_0x002b:
                r2.recycle()
            L_0x002e:
                if (r0 == 0) goto L_0x003d
                im.bclpbkiauv.messenger.ImageLoader r3 = im.bclpbkiauv.messenger.ImageLoader.this
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r4 = r6.cacheImage
                java.lang.String r4 = r4.key
                r3.incrementUseCount(r4)
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r3 = r6.cacheImage
                java.lang.String r1 = r3.key
            L_0x003d:
                goto L_0x0083
            L_0x003e:
                boolean r2 = r7 instanceof im.bclpbkiauv.ui.components.AnimatedFileDrawable
                if (r2 == 0) goto L_0x0044
                r0 = r7
                goto L_0x0083
            L_0x0044:
                boolean r2 = r7 instanceof android.graphics.drawable.BitmapDrawable
                if (r2 == 0) goto L_0x0083
                r2 = r7
                android.graphics.drawable.BitmapDrawable r2 = (android.graphics.drawable.BitmapDrawable) r2
                im.bclpbkiauv.messenger.ImageLoader r3 = im.bclpbkiauv.messenger.ImageLoader.this
                im.bclpbkiauv.messenger.LruCache r3 = r3.memCache
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r4 = r6.cacheImage
                java.lang.String r4 = r4.key
                java.lang.Object r3 = r3.get(r4)
                r0 = r3
                android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
                if (r0 != 0) goto L_0x006d
                im.bclpbkiauv.messenger.ImageLoader r3 = im.bclpbkiauv.messenger.ImageLoader.this
                im.bclpbkiauv.messenger.LruCache r3 = r3.memCache
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r4 = r6.cacheImage
                java.lang.String r4 = r4.key
                r3.put(r4, r2)
                r0 = r2
                goto L_0x0074
            L_0x006d:
                android.graphics.Bitmap r3 = r2.getBitmap()
                r3.recycle()
            L_0x0074:
                if (r0 == 0) goto L_0x0083
                im.bclpbkiauv.messenger.ImageLoader r3 = im.bclpbkiauv.messenger.ImageLoader.this
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r4 = r6.cacheImage
                java.lang.String r4 = r4.key
                r3.incrementUseCount(r4)
                im.bclpbkiauv.messenger.ImageLoader$CacheImage r3 = r6.cacheImage
                java.lang.String r1 = r3.key
            L_0x0083:
                r2 = r0
                r3 = r1
                im.bclpbkiauv.messenger.ImageLoader r4 = im.bclpbkiauv.messenger.ImageLoader.this
                im.bclpbkiauv.messenger.DispatchQueue r4 = r4.imageLoadQueue
                im.bclpbkiauv.messenger.-$$Lambda$ImageLoader$CacheOutTask$8kMjgzusgtuJ1MGlQPpsRW8gEZQ r5 = new im.bclpbkiauv.messenger.-$$Lambda$ImageLoader$CacheOutTask$8kMjgzusgtuJ1MGlQPpsRW8gEZQ
                r5.<init>(r2, r3)
                r4.postRunnable(r5)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.ImageLoader.CacheOutTask.lambda$onPostExecute$1$ImageLoader$CacheOutTask(android.graphics.drawable.Drawable):void");
        }

        public /* synthetic */ void lambda$null$0$ImageLoader$CacheOutTask(Drawable toSetFinal, String decrementKetFinal) {
            this.cacheImage.setImageAndClear(toSetFinal, decrementKetFinal);
        }

        public void cancel() {
            synchronized (this.sync) {
                try {
                    this.isCancelled = true;
                    if (this.runningThread != null) {
                        this.runningThread.interrupt();
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    private class CacheImage {
        protected boolean animatedFile;
        protected ArtworkLoadTask artworkTask;
        protected CacheOutTask cacheTask;
        protected int currentAccount;
        protected File encryptionKeyPath;
        protected String ext;
        protected String filter;
        protected ArrayList<String> filters;
        protected File finalFilePath;
        protected HttpImageTask httpTask;
        protected ImageLocation imageLocation;
        protected ArrayList<ImageReceiver> imageReceiverArray;
        protected ArrayList<Integer> imageReceiverGuidsArray;
        protected int imageType;
        protected ArrayList<Integer> imageTypes;
        protected String key;
        protected ArrayList<String> keys;
        protected boolean lottieFile;
        protected Object parentObject;
        protected SecureDocument secureDocument;
        protected int size;
        protected File tempFilePath;
        protected String url;

        private CacheImage() {
            this.imageReceiverArray = new ArrayList<>();
            this.imageReceiverGuidsArray = new ArrayList<>();
            this.keys = new ArrayList<>();
            this.filters = new ArrayList<>();
            this.imageTypes = new ArrayList<>();
        }

        public void addImageReceiver(ImageReceiver imageReceiver, String key2, String filter2, int type, int guid) {
            int index = this.imageReceiverArray.indexOf(imageReceiver);
            if (index >= 0) {
                this.imageReceiverGuidsArray.set(index, Integer.valueOf(guid));
                return;
            }
            this.imageReceiverArray.add(imageReceiver);
            this.imageReceiverGuidsArray.add(Integer.valueOf(guid));
            this.keys.add(key2);
            this.filters.add(filter2);
            this.imageTypes.add(Integer.valueOf(type));
            ImageLoader.this.imageLoadingByTag.put(imageReceiver.getTag(type), this);
        }

        public void replaceImageReceiver(ImageReceiver imageReceiver, String key2, String filter2, int type, int guid) {
            int index = this.imageReceiverArray.indexOf(imageReceiver);
            if (index != -1) {
                if (this.imageTypes.get(index).intValue() != type) {
                    ArrayList<ImageReceiver> arrayList = this.imageReceiverArray;
                    index = arrayList.subList(index + 1, arrayList.size()).indexOf(imageReceiver);
                    if (index == -1) {
                        return;
                    }
                }
                this.imageReceiverGuidsArray.set(index, Integer.valueOf(guid));
                this.keys.set(index, key2);
                this.filters.set(index, filter2);
            }
        }

        public void removeImageReceiver(ImageReceiver imageReceiver) {
            int currentImageType = this.imageType;
            int a = 0;
            while (a < this.imageReceiverArray.size()) {
                ImageReceiver obj = this.imageReceiverArray.get(a);
                if (obj == null || obj == imageReceiver) {
                    this.imageReceiverArray.remove(a);
                    this.imageReceiverGuidsArray.remove(a);
                    this.keys.remove(a);
                    this.filters.remove(a);
                    currentImageType = this.imageTypes.remove(a).intValue();
                    if (obj != null) {
                        ImageLoader.this.imageLoadingByTag.remove(obj.getTag(currentImageType));
                    }
                    a--;
                }
                a++;
            }
            if (this.imageReceiverArray.isEmpty()) {
                if (this.imageLocation != null && !ImageLoader.this.forceLoadingImages.containsKey(this.key)) {
                    if (this.imageLocation.location != null) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.imageLocation.location, this.ext);
                    } else if (this.imageLocation.document != null) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.imageLocation.document);
                    } else if (this.imageLocation.secureDocument != null) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.imageLocation.secureDocument);
                    } else if (this.imageLocation.webFile != null) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.imageLocation.webFile);
                    }
                }
                if (this.cacheTask != null) {
                    if (currentImageType == 1) {
                        ImageLoader.this.cacheThumbOutQueue.cancelRunnable(this.cacheTask);
                    } else {
                        ImageLoader.this.cacheOutQueue.cancelRunnable(this.cacheTask);
                    }
                    this.cacheTask.cancel();
                    this.cacheTask = null;
                }
                if (this.httpTask != null) {
                    ImageLoader.this.httpTasks.remove(this.httpTask);
                    this.httpTask.cancel(true);
                    this.httpTask = null;
                }
                if (this.artworkTask != null) {
                    ImageLoader.this.artworkTasks.remove(this.artworkTask);
                    this.artworkTask.cancel(true);
                    this.artworkTask = null;
                }
                if (this.url != null) {
                    ImageLoader.this.imageLoadingByUrl.remove(this.url);
                }
                if (this.key != null) {
                    ImageLoader.this.imageLoadingByKeys.remove(this.key);
                }
            }
        }

        public void setImageAndClear(Drawable image, String decrementKey) {
            if (image != null) {
                AndroidUtilities.runOnUIThread(new Runnable(image, new ArrayList<>(this.imageReceiverArray), new ArrayList<>(this.imageReceiverGuidsArray), decrementKey) {
                    private final /* synthetic */ Drawable f$1;
                    private final /* synthetic */ ArrayList f$2;
                    private final /* synthetic */ ArrayList f$3;
                    private final /* synthetic */ String f$4;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                    }

                    public final void run() {
                        ImageLoader.CacheImage.this.lambda$setImageAndClear$0$ImageLoader$CacheImage(this.f$1, this.f$2, this.f$3, this.f$4);
                    }
                });
            }
            for (int a = 0; a < this.imageReceiverArray.size(); a++) {
                ImageLoader.this.imageLoadingByTag.remove(this.imageReceiverArray.get(a).getTag(this.imageType));
            }
            this.imageReceiverArray.clear();
            this.imageReceiverGuidsArray.clear();
            if (this.url != null) {
                ImageLoader.this.imageLoadingByUrl.remove(this.url);
            }
            if (this.key != null) {
                ImageLoader.this.imageLoadingByKeys.remove(this.key);
            }
        }

        public /* synthetic */ void lambda$setImageAndClear$0$ImageLoader$CacheImage(Drawable image, ArrayList finalImageReceiverArray, ArrayList finalImageReceiverGuidsArray, String decrementKey) {
            if (image instanceof AnimatedFileDrawable) {
                boolean imageSet = false;
                AnimatedFileDrawable fileDrawable = (AnimatedFileDrawable) image;
                int a = 0;
                while (a < finalImageReceiverArray.size()) {
                    ImageReceiver imgView = (ImageReceiver) finalImageReceiverArray.get(a);
                    AnimatedFileDrawable toSet = a == 0 ? fileDrawable : fileDrawable.makeCopy();
                    if (imgView.setImageBitmapByKey(toSet, this.key, this.imageType, false, ((Integer) finalImageReceiverGuidsArray.get(a)).intValue())) {
                        if (toSet == fileDrawable) {
                            imageSet = true;
                        }
                    } else if (toSet != fileDrawable) {
                        toSet.recycle();
                    }
                    a++;
                }
                if (!imageSet) {
                    fileDrawable.recycle();
                }
            } else {
                for (int a2 = 0; a2 < finalImageReceiverArray.size(); a2++) {
                    ((ImageReceiver) finalImageReceiverArray.get(a2)).setImageBitmapByKey(image, this.key, this.imageTypes.get(a2).intValue(), false, ((Integer) finalImageReceiverGuidsArray.get(a2)).intValue());
                }
            }
            if (decrementKey != null) {
                ImageLoader.this.decrementUseCount(decrementKey);
            }
        }
    }

    public static ImageLoader getInstance() {
        ImageLoader localInstance = Instance;
        if (localInstance == null) {
            synchronized (ImageLoader.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    ImageLoader imageLoader = new ImageLoader();
                    localInstance = imageLoader;
                    Instance = imageLoader;
                }
            }
        }
        return localInstance;
    }

    public ImageLoader() {
        int maxSize;
        boolean z = false;
        this.currentHttpTasksCount = 0;
        this.currentArtworkTasksCount = 0;
        this.testWebFile = new ConcurrentHashMap<>();
        this.httpFileLoadTasks = new LinkedList<>();
        this.httpFileLoadTasksByKeys = new HashMap<>();
        this.retryHttpsTasks = new HashMap<>();
        this.currentHttpFileLoadTasksCount = 0;
        this.ignoreRemoval = null;
        this.lastCacheOutTime = 0;
        this.lastImageNum = 0;
        this.lastProgressUpdateTime = 0;
        this.appPath = null;
        this.thumbGeneratingQueue.setPriority(1);
        int memoryClass = ((ActivityManager) ApplicationLoader.applicationContext.getSystemService("activity")).getMemoryClass();
        z = memoryClass >= 192 ? true : z;
        this.canForce8888 = z;
        if (z) {
            maxSize = 30;
        } else {
            maxSize = 15;
        }
        this.memCache = new LruCache<BitmapDrawable>(Math.min(maxSize, memoryClass / 7) * 1024 * 1024) {
            /* access modifiers changed from: protected */
            public int sizeOf(String key, BitmapDrawable value) {
                return value.getBitmap().getByteCount();
            }

            /* access modifiers changed from: protected */
            public void entryRemoved(boolean evicted, String key, BitmapDrawable oldValue, BitmapDrawable newValue) {
                if (ImageLoader.this.ignoreRemoval == null || !ImageLoader.this.ignoreRemoval.equals(key)) {
                    Integer count = (Integer) ImageLoader.this.bitmapUseCounts.get(key);
                    if (count == null || count.intValue() == 0) {
                        Bitmap b = oldValue.getBitmap();
                        if (!b.isRecycled()) {
                            b.recycle();
                        }
                    }
                }
            }
        };
        this.lottieMemCache = new LruCache<RLottieDrawable>(10485760) {
            /* access modifiers changed from: protected */
            public int sizeOf(String key, RLottieDrawable value) {
                return value.getIntrinsicWidth() * value.getIntrinsicHeight() * 4 * 2;
            }

            /* access modifiers changed from: protected */
            public void entryRemoved(boolean evicted, String key, RLottieDrawable oldValue, RLottieDrawable newValue) {
                Integer count = (Integer) ImageLoader.this.bitmapUseCounts.get(key);
                if (count == null || count.intValue() == 0) {
                    oldValue.recycle();
                }
            }
        };
        SparseArray<File> mediaDirs = new SparseArray<>();
        File cachePath = AndroidUtilities.getCacheDir();
        if (!cachePath.isDirectory()) {
            try {
                cachePath.mkdirs();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        try {
            new File(cachePath, ".nomedia").createNewFile();
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
        mediaDirs.put(4, cachePath);
        for (int a = 0; a < 3; a++) {
            final int currentAccount = a;
            FileLoader.getInstance(a).setDelegate(new FileLoader.FileLoaderDelegate() {
                public void fileUploadProgressChanged(String location, float progress, boolean isEncrypted) {
                    ImageLoader.this.fileProgresses.put(location, Float.valueOf(progress));
                    long currentTime = System.currentTimeMillis();
                    if (ImageLoader.this.lastProgressUpdateTime == 0 || ImageLoader.this.lastProgressUpdateTime < currentTime - 500) {
                        long unused = ImageLoader.this.lastProgressUpdateTime = currentTime;
                        AndroidUtilities.runOnUIThread(new Runnable(currentAccount, location, progress, isEncrypted) {
                            private final /* synthetic */ int f$0;
                            private final /* synthetic */ String f$1;
                            private final /* synthetic */ float f$2;
                            private final /* synthetic */ boolean f$3;

                            {
                                this.f$0 = r1;
                                this.f$1 = r2;
                                this.f$2 = r3;
                                this.f$3 = r4;
                            }

                            public final void run() {
                                NotificationCenter.getInstance(this.f$0).postNotificationName(NotificationCenter.FileUploadProgressChanged, this.f$1, Float.valueOf(this.f$2), Boolean.valueOf(this.f$3));
                            }
                        });
                    }
                }

                public void fileDidUploaded(String location, TLRPC.InputFile inputFile, TLRPC.InputEncryptedFile inputEncryptedFile, byte[] key, byte[] iv, long totalFileSize, boolean apply) {
                    Utilities.stageQueue.postRunnable(new Runnable(currentAccount, location, inputFile, inputEncryptedFile, key, iv, totalFileSize, apply) {
                        private final /* synthetic */ int f$1;
                        private final /* synthetic */ String f$2;
                        private final /* synthetic */ TLRPC.InputFile f$3;
                        private final /* synthetic */ TLRPC.InputEncryptedFile f$4;
                        private final /* synthetic */ byte[] f$5;
                        private final /* synthetic */ byte[] f$6;
                        private final /* synthetic */ long f$7;
                        private final /* synthetic */ boolean f$8;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                            this.f$5 = r6;
                            this.f$6 = r7;
                            this.f$7 = r8;
                            this.f$8 = r10;
                        }

                        public final void run() {
                            ImageLoader.AnonymousClass3.this.lambda$fileDidUploaded$2$ImageLoader$3(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
                        }
                    });
                }

                public /* synthetic */ void lambda$fileDidUploaded$2$ImageLoader$3(int currentAccount, String location, TLRPC.InputFile inputFile, TLRPC.InputEncryptedFile inputEncryptedFile, byte[] key, byte[] iv, long totalFileSize, boolean apply) {
                    AndroidUtilities.runOnUIThread(new Runnable(currentAccount, location, inputFile, inputEncryptedFile, key, iv, totalFileSize, apply) {
                        private final /* synthetic */ int f$0;
                        private final /* synthetic */ String f$1;
                        private final /* synthetic */ TLRPC.InputFile f$2;
                        private final /* synthetic */ TLRPC.InputEncryptedFile f$3;
                        private final /* synthetic */ byte[] f$4;
                        private final /* synthetic */ byte[] f$5;
                        private final /* synthetic */ long f$6;
                        private final /* synthetic */ boolean f$7;

                        {
                            this.f$0 = r1;
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                            this.f$5 = r6;
                            this.f$6 = r7;
                            this.f$7 = r9;
                        }

                        public final void run() {
                            NotificationCenter.getInstance(this.f$0).postNotificationName(NotificationCenter.FileDidUpload, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, Long.valueOf(this.f$6), Boolean.valueOf(this.f$7));
                        }
                    });
                    ImageLoader.this.fileProgresses.remove(location);
                }

                public void fileDidFailedUpload(String location, boolean isEncrypted) {
                    Utilities.stageQueue.postRunnable(new Runnable(currentAccount, location, isEncrypted) {
                        private final /* synthetic */ int f$1;
                        private final /* synthetic */ String f$2;
                        private final /* synthetic */ boolean f$3;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                        }

                        public final void run() {
                            ImageLoader.AnonymousClass3.this.lambda$fileDidFailedUpload$4$ImageLoader$3(this.f$1, this.f$2, this.f$3);
                        }
                    });
                }

                public /* synthetic */ void lambda$fileDidFailedUpload$4$ImageLoader$3(int currentAccount, String location, boolean isEncrypted) {
                    AndroidUtilities.runOnUIThread(new Runnable(currentAccount, location, isEncrypted) {
                        private final /* synthetic */ int f$0;
                        private final /* synthetic */ String f$1;
                        private final /* synthetic */ boolean f$2;

                        {
                            this.f$0 = r1;
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run() {
                            NotificationCenter.getInstance(this.f$0).postNotificationName(NotificationCenter.FileDidFailUpload, this.f$1, Boolean.valueOf(this.f$2));
                        }
                    });
                    ImageLoader.this.fileProgresses.remove(location);
                }

                public void fileDidLoaded(String location, File finalFile, int type) {
                    ImageLoader.this.fileProgresses.remove(location);
                    AndroidUtilities.runOnUIThread(new Runnable(finalFile, location, currentAccount, type) {
                        private final /* synthetic */ File f$1;
                        private final /* synthetic */ String f$2;
                        private final /* synthetic */ int f$3;
                        private final /* synthetic */ int f$4;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                        }

                        public final void run() {
                            ImageLoader.AnonymousClass3.this.lambda$fileDidLoaded$5$ImageLoader$3(this.f$1, this.f$2, this.f$3, this.f$4);
                        }
                    });
                }

                public /* synthetic */ void lambda$fileDidLoaded$5$ImageLoader$3(File finalFile, String location, int currentAccount, int type) {
                    if (SharedConfig.saveToGallery && ImageLoader.this.appPath != null && finalFile != null && ((location.endsWith(".mp4") || location.endsWith(".jpg")) && finalFile.toString().startsWith(ImageLoader.this.appPath.toString()))) {
                        AndroidUtilities.addMediaToGallery(finalFile.toString());
                    }
                    NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.fileDidLoad, location, finalFile);
                    ImageLoader.this.fileDidLoaded(location, finalFile, type);
                }

                public void fileDidFailedLoad(String location, int canceled) {
                    ImageLoader.this.fileProgresses.remove(location);
                    AndroidUtilities.runOnUIThread(new Runnable(location, canceled, currentAccount) {
                        private final /* synthetic */ String f$1;
                        private final /* synthetic */ int f$2;
                        private final /* synthetic */ int f$3;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                        }

                        public final void run() {
                            ImageLoader.AnonymousClass3.this.lambda$fileDidFailedLoad$6$ImageLoader$3(this.f$1, this.f$2, this.f$3);
                        }
                    });
                }

                public /* synthetic */ void lambda$fileDidFailedLoad$6$ImageLoader$3(String location, int canceled, int currentAccount) {
                    ImageLoader.this.fileDidFailedLoad(location, canceled);
                    NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.fileDidFailToLoad, location, Integer.valueOf(canceled));
                }

                public void fileLoadProgressChanged(String location, float progress) {
                    ImageLoader.this.fileProgresses.put(location, Float.valueOf(progress));
                    long currentTime = System.currentTimeMillis();
                    if (ImageLoader.this.lastProgressUpdateTime == 0 || ImageLoader.this.lastProgressUpdateTime < currentTime - 500) {
                        long unused = ImageLoader.this.lastProgressUpdateTime = currentTime;
                        AndroidUtilities.runOnUIThread(new Runnable(currentAccount, location, progress) {
                            private final /* synthetic */ int f$0;
                            private final /* synthetic */ String f$1;
                            private final /* synthetic */ float f$2;

                            {
                                this.f$0 = r1;
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void run() {
                                NotificationCenter.getInstance(this.f$0).postNotificationName(NotificationCenter.FileLoadProgressChanged, this.f$1, Float.valueOf(this.f$2));
                            }
                        });
                    }
                }
            });
        }
        FileLoader.setMediaDirs(mediaDirs);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            public void onReceive(Context arg0, Intent intent) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("file system changed");
                }
                Runnable r = new Runnable() {
                    public final void run() {
                        ImageLoader.AnonymousClass4.this.lambda$onReceive$0$ImageLoader$4();
                    }
                };
                if ("android.intent.action.MEDIA_UNMOUNTED".equals(intent.getAction())) {
                    AndroidUtilities.runOnUIThread(r, 1000);
                } else {
                    r.run();
                }
            }

            public /* synthetic */ void lambda$onReceive$0$ImageLoader$4() {
                ImageLoader.this.checkMediaPaths();
            }
        };
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
        try {
            ApplicationLoader.applicationContext.registerReceiver(receiver, filter);
        } catch (Throwable th) {
        }
        checkMediaPaths();
    }

    public void checkMediaPaths() {
        this.cacheOutQueue.postRunnable(new Runnable() {
            public final void run() {
                ImageLoader.this.lambda$checkMediaPaths$1$ImageLoader();
            }
        });
    }

    public /* synthetic */ void lambda$checkMediaPaths$1$ImageLoader() {
        AndroidUtilities.runOnUIThread(new Runnable(createMediaPaths()) {
            private final /* synthetic */ SparseArray f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                FileLoader.setMediaDirs(this.f$0);
            }
        });
    }

    public void addTestWebFile(String url, WebFile webFile) {
        if (url != null && webFile != null) {
            this.testWebFile.put(url, webFile);
        }
    }

    public void removeTestWebFile(String url) {
        if (url != null) {
            this.testWebFile.remove(url);
        }
    }

    public SparseArray<File> createMediaPaths() {
        SparseArray<File> mediaDirs = new SparseArray<>();
        File cachePath = AndroidUtilities.getCacheDir();
        if (!cachePath.isDirectory()) {
            try {
                cachePath.mkdirs();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        try {
            new File(cachePath, ".nomedia").createNewFile();
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
        mediaDirs.put(4, cachePath);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("cache path = " + cachePath);
        }
        try {
            if ("mounted".equals(Environment.getExternalStorageState())) {
                File file = new File(Environment.getExternalStorageDirectory(), "Yixin");
                this.appPath = file;
                file.mkdirs();
                if (this.appPath.isDirectory()) {
                    try {
                        File imagePath = new File(this.appPath, "Yixin Images");
                        imagePath.mkdir();
                        if (imagePath.isDirectory() && canMoveFiles(cachePath, imagePath, 0)) {
                            mediaDirs.put(0, imagePath);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("image path = " + imagePath);
                            }
                        }
                    } catch (Exception e3) {
                        FileLog.e((Throwable) e3);
                    }
                    try {
                        File videoPath = new File(this.appPath, "Yixin Video");
                        videoPath.mkdir();
                        if (videoPath.isDirectory() && canMoveFiles(cachePath, videoPath, 2)) {
                            mediaDirs.put(2, videoPath);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("video path = " + videoPath);
                            }
                        }
                    } catch (Exception e4) {
                        FileLog.e((Throwable) e4);
                    }
                    try {
                        File audioPath = new File(this.appPath, "Yixin Audio");
                        audioPath.mkdir();
                        if (audioPath.isDirectory() && canMoveFiles(cachePath, audioPath, 1)) {
                            new File(audioPath, ".nomedia").createNewFile();
                            mediaDirs.put(1, audioPath);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("audio path = " + audioPath);
                            }
                        }
                    } catch (Exception e5) {
                        FileLog.e((Throwable) e5);
                    }
                    try {
                        File documentPath = new File(this.appPath, "Yixin Documents");
                        documentPath.mkdir();
                        if (documentPath.isDirectory() && canMoveFiles(cachePath, documentPath, 3)) {
                            new File(documentPath, ".nomedia").createNewFile();
                            mediaDirs.put(3, documentPath);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("documents path = " + documentPath);
                            }
                        }
                    } catch (Exception e6) {
                        FileLog.e((Throwable) e6);
                    }
                }
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.d("this Android can't rename files");
            }
            SharedConfig.checkSaveToGalleryFiles();
        } catch (Exception e7) {
            FileLog.e((Throwable) e7);
        }
        return mediaDirs;
    }

    private boolean canMoveFiles(File from, File to, int type) {
        RandomAccessFile file = null;
        File srcFile = null;
        File dstFile = null;
        if (type == 0) {
            try {
                srcFile = new File(from, "000000000_999999_temp.jpg");
                dstFile = new File(to, "000000000_999999.jpg");
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                if (file == null) {
                    return false;
                }
                file.close();
                return false;
            } catch (Throwable th) {
                if (file != null) {
                    try {
                        file.close();
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                    }
                }
                throw th;
            }
        } else if (type == 3) {
            srcFile = new File(from, "000000000_999999_temp.doc");
            dstFile = new File(to, "000000000_999999.doc");
        } else if (type == 1) {
            srcFile = new File(from, "000000000_999999_temp.ogg");
            dstFile = new File(to, "000000000_999999.ogg");
        } else if (type == 2) {
            srcFile = new File(from, "000000000_999999_temp.mp4");
            dstFile = new File(to, "000000000_999999.mp4");
        }
        srcFile.createNewFile();
        RandomAccessFile file2 = new RandomAccessFile(srcFile, "rws");
        file2.write(new byte[1024]);
        file2.close();
        RandomAccessFile file3 = null;
        boolean canRename = srcFile.renameTo(dstFile);
        srcFile.delete();
        dstFile.delete();
        if (canRename) {
            if (file3 != null) {
                try {
                    file3.close();
                } catch (Exception e3) {
                    FileLog.e((Throwable) e3);
                }
            }
            return true;
        } else if (file3 == null) {
            return false;
        } else {
            try {
                file3.close();
                return false;
            } catch (Exception e4) {
                FileLog.e((Throwable) e4);
                return false;
            }
        }
    }

    public Float getFileProgress(String location) {
        if (location == null) {
            return null;
        }
        return this.fileProgresses.get(location);
    }

    public String getReplacedKey(String oldKey) {
        if (oldKey == null) {
            return null;
        }
        return this.replacedBitmaps.get(oldKey);
    }

    private void performReplace(String oldKey, String newKey) {
        BitmapDrawable b = this.memCache.get(oldKey);
        this.replacedBitmaps.put(oldKey, newKey);
        if (b != null) {
            BitmapDrawable oldBitmap = this.memCache.get(newKey);
            boolean dontChange = false;
            if (!(oldBitmap == null || oldBitmap.getBitmap() == null || b.getBitmap() == null)) {
                Bitmap oldBitmapObject = oldBitmap.getBitmap();
                Bitmap newBitmapObject = b.getBitmap();
                if (oldBitmapObject.getWidth() > newBitmapObject.getWidth() || oldBitmapObject.getHeight() > newBitmapObject.getHeight()) {
                    dontChange = true;
                }
            }
            if (!dontChange) {
                this.ignoreRemoval = oldKey;
                this.memCache.remove(oldKey);
                this.memCache.put(newKey, b);
                this.ignoreRemoval = null;
            } else {
                this.memCache.remove(oldKey);
            }
        }
        Integer val = this.bitmapUseCounts.get(oldKey);
        if (val != null) {
            this.bitmapUseCounts.put(newKey, val);
            this.bitmapUseCounts.remove(oldKey);
        }
    }

    public void incrementUseCount(String key) {
        Integer count = this.bitmapUseCounts.get(key);
        if (count == null) {
            this.bitmapUseCounts.put(key, 1);
        } else {
            this.bitmapUseCounts.put(key, Integer.valueOf(count.intValue() + 1));
        }
    }

    public boolean decrementUseCount(String key) {
        Integer count = this.bitmapUseCounts.get(key);
        if (count == null) {
            return true;
        }
        if (count.intValue() == 1) {
            this.bitmapUseCounts.remove(key);
            return true;
        }
        this.bitmapUseCounts.put(key, Integer.valueOf(count.intValue() - 1));
        return false;
    }

    public void removeImage(String key) {
        this.bitmapUseCounts.remove(key);
        this.memCache.remove(key);
    }

    public boolean isInMemCache(String key, boolean animated) {
        if (animated) {
            if (this.lottieMemCache.get(key) != null) {
                return true;
            }
            return false;
        } else if (this.memCache.get(key) != null) {
            return true;
        } else {
            return false;
        }
    }

    public void clearMemory() {
        this.memCache.evictAll();
        this.lottieMemCache.evictAll();
    }

    private void removeFromWaitingForThumb(int TAG, ImageReceiver imageReceiver) {
        String location = this.waitingForQualityThumbByTag.get(TAG);
        if (location != null) {
            ThumbGenerateInfo info = this.waitingForQualityThumb.get(location);
            if (info != null) {
                int index = info.imageReceiverArray.indexOf(imageReceiver);
                if (index >= 0) {
                    info.imageReceiverArray.remove(index);
                    info.imageReceiverGuidsArray.remove(index);
                }
                if (info.imageReceiverArray.isEmpty()) {
                    this.waitingForQualityThumb.remove(location);
                }
            }
            this.waitingForQualityThumbByTag.remove(TAG);
        }
    }

    public void cancelLoadingForImageReceiver(ImageReceiver imageReceiver, boolean cancelAll) {
        if (imageReceiver != null) {
            this.imageLoadQueue.postRunnable(new Runnable(cancelAll, imageReceiver) {
                private final /* synthetic */ boolean f$1;
                private final /* synthetic */ ImageReceiver f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    ImageLoader.this.lambda$cancelLoadingForImageReceiver$2$ImageLoader(this.f$1, this.f$2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$cancelLoadingForImageReceiver$2$ImageLoader(boolean cancelAll, ImageReceiver imageReceiver) {
        int imageType;
        int a = 0;
        while (a < 3) {
            if (a <= 0 || cancelAll) {
                if (a == 0) {
                    imageType = 1;
                } else if (a == 1) {
                    imageType = 0;
                } else {
                    imageType = 3;
                }
                int TAG = imageReceiver.getTag(imageType);
                if (TAG != 0) {
                    if (a == 0) {
                        removeFromWaitingForThumb(TAG, imageReceiver);
                    }
                    CacheImage ei = this.imageLoadingByTag.get(TAG);
                    if (ei != null) {
                        ei.removeImageReceiver(imageReceiver);
                    }
                }
                a++;
            } else {
                return;
            }
        }
    }

    public BitmapDrawable getAnyImageFromMemory(String key) {
        ArrayList<String> filters;
        BitmapDrawable drawable = this.memCache.get(key);
        if (drawable != null || (filters = this.memCache.getFilterKeys(key)) == null || filters.isEmpty()) {
            return drawable;
        }
        LruCache<BitmapDrawable> lruCache = this.memCache;
        return lruCache.get(key + "@" + filters.get(0));
    }

    public BitmapDrawable getImageFromMemory(TLObject fileLocation, String httpUrl, String filter) {
        if (fileLocation == null && httpUrl == null) {
            return null;
        }
        String key = null;
        if (httpUrl != null) {
            key = Utilities.MD5(httpUrl);
        } else if (fileLocation instanceof TLRPC.FileLocation) {
            TLRPC.FileLocation location = (TLRPC.FileLocation) fileLocation;
            key = location.volume_id + "_" + location.local_id;
        } else if (fileLocation instanceof TLRPC.Document) {
            TLRPC.Document location2 = (TLRPC.Document) fileLocation;
            key = location2.dc_id + "_" + location2.id;
        } else if (fileLocation instanceof SecureDocument) {
            SecureDocument location3 = (SecureDocument) fileLocation;
            key = location3.secureFile.dc_id + "_" + location3.secureFile.id;
        } else if (fileLocation instanceof WebFile) {
            key = Utilities.MD5(((WebFile) fileLocation).url);
        }
        if (filter != null) {
            key = key + "@" + filter;
        }
        return this.memCache.get(key);
    }

    /* access modifiers changed from: private */
    /* renamed from: replaceImageInCacheInternal */
    public void lambda$replaceImageInCache$3$ImageLoader(String oldKey, String newKey, ImageLocation newLocation) {
        ArrayList<String> arr = this.memCache.getFilterKeys(oldKey);
        if (arr != null) {
            for (int a = 0; a < arr.size(); a++) {
                String filter = arr.get(a);
                String oldK = oldKey + "@" + filter;
                String newK = newKey + "@" + filter;
                performReplace(oldK, newK);
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didReplacedPhotoInMemCache, oldK, newK, newLocation);
            }
            return;
        }
        performReplace(oldKey, newKey);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didReplacedPhotoInMemCache, oldKey, newKey, newLocation);
    }

    public void replaceImageInCache(String oldKey, String newKey, ImageLocation newLocation, boolean post) {
        if (post) {
            AndroidUtilities.runOnUIThread(new Runnable(oldKey, newKey, newLocation) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ String f$2;
                private final /* synthetic */ ImageLocation f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    ImageLoader.this.lambda$replaceImageInCache$3$ImageLoader(this.f$1, this.f$2, this.f$3);
                }
            });
        } else {
            lambda$replaceImageInCache$3$ImageLoader(oldKey, newKey, newLocation);
        }
    }

    public void putImageToCache(BitmapDrawable bitmap, String key) {
        this.memCache.put(key, bitmap);
    }

    private void generateThumb(int mediaType, File originalPath, ThumbGenerateInfo info) {
        if ((mediaType == 0 || mediaType == 2 || mediaType == 3) && originalPath != null && info != null) {
            if (this.thumbGenerateTasks.get(FileLoader.getAttachFileName(info.parentDocument)) == null) {
                this.thumbGeneratingQueue.postRunnable(new ThumbGenerateTask(mediaType, originalPath, info));
            }
        }
    }

    public void cancelForceLoadingForImageReceiver(ImageReceiver imageReceiver) {
        String key;
        if (imageReceiver != null && (key = imageReceiver.getImageKey()) != null) {
            this.imageLoadQueue.postRunnable(new Runnable(key) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ImageLoader.this.lambda$cancelForceLoadingForImageReceiver$4$ImageLoader(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$cancelForceLoadingForImageReceiver$4$ImageLoader(String key) {
        Integer remove = this.forceLoadingImages.remove(key);
    }

    private void createLoadOperationForImageReceiver(ImageReceiver imageReceiver, String key, String url, String ext, ImageLocation imageLocation, String filter, int size, int cacheType, int imageType, int thumb, int guid) {
        int TAG;
        ImageReceiver imageReceiver2 = imageReceiver;
        int i = imageType;
        if (imageReceiver2 != null && url != null && key != null && imageLocation != null) {
            int TAG2 = imageReceiver2.getTag(i);
            if (TAG2 == 0) {
                int i2 = this.lastImageNum;
                int TAG3 = i2;
                imageReceiver2.setTag(i2, i);
                int i3 = this.lastImageNum + 1;
                this.lastImageNum = i3;
                if (i3 == Integer.MAX_VALUE) {
                    this.lastImageNum = 0;
                }
                TAG = TAG3;
            } else {
                TAG = TAG2;
            }
            int finalTag = TAG;
            boolean finalIsNeedsQualityThumb = imageReceiver.isNeedsQualityThumb();
            Object parentObject = imageReceiver.getParentObject();
            TLRPC.Document qualityDocument = imageReceiver.getQulityThumbDocument();
            boolean shouldGenerateQualityThumb = imageReceiver.isShouldGenerateQualityThumb();
            int currentAccount = imageReceiver.getCurrentAccount();
            boolean currentKeyQuality = i == 0 && imageReceiver.isCurrentKeyQuality();
            $$Lambda$ImageLoader$UOuQfw47_X7sfXXbF_BVfz0YrRA r26 = r0;
            DispatchQueue dispatchQueue = this.imageLoadQueue;
            $$Lambda$ImageLoader$UOuQfw47_X7sfXXbF_BVfz0YrRA r0 = new Runnable(this, thumb, url, key, finalTag, imageReceiver, filter, imageType, guid, imageLocation, currentKeyQuality, parentObject, qualityDocument, finalIsNeedsQualityThumb, shouldGenerateQualityThumb, cacheType, size, ext, currentAccount) {
                private final /* synthetic */ ImageLoader f$0;
                private final /* synthetic */ int f$1;
                private final /* synthetic */ boolean f$10;
                private final /* synthetic */ Object f$11;
                private final /* synthetic */ TLRPC.Document f$12;
                private final /* synthetic */ boolean f$13;
                private final /* synthetic */ boolean f$14;
                private final /* synthetic */ int f$15;
                private final /* synthetic */ int f$16;
                private final /* synthetic */ String f$17;
                private final /* synthetic */ int f$18;
                private final /* synthetic */ String f$2;
                private final /* synthetic */ String f$3;
                private final /* synthetic */ int f$4;
                private final /* synthetic */ ImageReceiver f$5;
                private final /* synthetic */ String f$6;
                private final /* synthetic */ int f$7;
                private final /* synthetic */ int f$8;
                private final /* synthetic */ ImageLocation f$9;

                {
                    this.f$0 = r3;
                    this.f$1 = r4;
                    this.f$2 = r5;
                    this.f$3 = r6;
                    this.f$4 = r7;
                    this.f$5 = r8;
                    this.f$6 = r9;
                    this.f$7 = r10;
                    this.f$8 = r11;
                    this.f$9 = r12;
                    this.f$10 = r13;
                    this.f$11 = r14;
                    this.f$12 = r15;
                    this.f$13 = r16;
                    this.f$14 = r17;
                    this.f$15 = r18;
                    this.f$16 = r19;
                    this.f$17 = r20;
                    this.f$18 = r21;
                }

                public final void run() {
                    ImageLoader imageLoader = this.f$0;
                    ImageLoader imageLoader2 = imageLoader;
                    imageLoader2.lambda$createLoadOperationForImageReceiver$5$ImageLoader(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13, this.f$14, this.f$15, this.f$16, this.f$17, this.f$18);
                }
            };
            dispatchQueue.postRunnable(r26);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:133:0x031a  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x031e  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x034f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$createLoadOperationForImageReceiver$5$ImageLoader(int r30, java.lang.String r31, java.lang.String r32, int r33, im.bclpbkiauv.messenger.ImageReceiver r34, java.lang.String r35, int r36, int r37, im.bclpbkiauv.messenger.ImageLocation r38, boolean r39, java.lang.Object r40, im.bclpbkiauv.tgnet.TLRPC.Document r41, boolean r42, boolean r43, int r44, int r45, java.lang.String r46, int r47) {
        /*
            r29 = this;
            r0 = r29
            r1 = r30
            r2 = r31
            r9 = r32
            r10 = r33
            r11 = r34
            r12 = r35
            r13 = r38
            r14 = r40
            r15 = r44
            r8 = r45
            r16 = 0
            r7 = 2
            if (r1 == r7) goto L_0x00a1
            java.util.HashMap<java.lang.String, im.bclpbkiauv.messenger.ImageLoader$CacheImage> r3 = r0.imageLoadingByUrl
            java.lang.Object r3 = r3.get(r2)
            r6 = r3
            im.bclpbkiauv.messenger.ImageLoader$CacheImage r6 = (im.bclpbkiauv.messenger.ImageLoader.CacheImage) r6
            java.util.HashMap<java.lang.String, im.bclpbkiauv.messenger.ImageLoader$CacheImage> r3 = r0.imageLoadingByKeys
            java.lang.Object r3 = r3.get(r9)
            r5 = r3
            im.bclpbkiauv.messenger.ImageLoader$CacheImage r5 = (im.bclpbkiauv.messenger.ImageLoader.CacheImage) r5
            android.util.SparseArray<im.bclpbkiauv.messenger.ImageLoader$CacheImage> r3 = r0.imageLoadingByTag
            java.lang.Object r3 = r3.get(r10)
            r4 = r3
            im.bclpbkiauv.messenger.ImageLoader$CacheImage r4 = (im.bclpbkiauv.messenger.ImageLoader.CacheImage) r4
            if (r4 == 0) goto L_0x006f
            if (r4 != r5) goto L_0x0043
            r16 = 1
            r9 = r4
            r17 = r5
            r18 = r6
            r15 = 2
            goto L_0x0075
        L_0x0043:
            if (r4 != r6) goto L_0x0065
            if (r5 != 0) goto L_0x005c
            r3 = r4
            r9 = r4
            r4 = r34
            r17 = r5
            r5 = r32
            r18 = r6
            r6 = r35
            r15 = 2
            r7 = r36
            r8 = r37
            r3.replaceImageReceiver(r4, r5, r6, r7, r8)
            goto L_0x0062
        L_0x005c:
            r9 = r4
            r17 = r5
            r18 = r6
            r15 = 2
        L_0x0062:
            r16 = 1
            goto L_0x0075
        L_0x0065:
            r9 = r4
            r17 = r5
            r18 = r6
            r15 = 2
            r9.removeImageReceiver(r11)
            goto L_0x0075
        L_0x006f:
            r9 = r4
            r17 = r5
            r18 = r6
            r15 = 2
        L_0x0075:
            if (r16 != 0) goto L_0x008b
            if (r17 == 0) goto L_0x008b
            r3 = r17
            r4 = r34
            r5 = r32
            r6 = r35
            r7 = r36
            r8 = r37
            r3.addImageReceiver(r4, r5, r6, r7, r8)
            r3 = 1
            r16 = r3
        L_0x008b:
            if (r16 != 0) goto L_0x00a2
            if (r18 == 0) goto L_0x00a2
            r3 = r18
            r4 = r34
            r5 = r32
            r6 = r35
            r7 = r36
            r8 = r37
            r3.addImageReceiver(r4, r5, r6, r7, r8)
            r16 = 1
            goto L_0x00a2
        L_0x00a1:
            r15 = 2
        L_0x00a2:
            if (r16 != 0) goto L_0x05a3
            r3 = 0
            r9 = 0
            r4 = 0
            r5 = 0
            java.lang.String r6 = r13.path
            java.lang.String r7 = "_"
            java.lang.String r15 = "athumb"
            r18 = 4
            if (r6 == 0) goto L_0x0123
            java.lang.String r6 = r13.path
            java.lang.String r8 = "http"
            boolean r8 = r6.startsWith(r8)
            if (r8 != 0) goto L_0x0119
            boolean r8 = r6.startsWith(r15)
            if (r8 != 0) goto L_0x0119
            r3 = 1
            java.lang.String r8 = "thumb://"
            boolean r8 = r6.startsWith(r8)
            r20 = r3
            java.lang.String r3 = ":"
            if (r8 == 0) goto L_0x00ec
            r8 = 8
            int r3 = r6.indexOf(r3, r8)
            if (r3 < 0) goto L_0x00e7
            java.io.File r8 = new java.io.File
            r21 = r4
            int r4 = r3 + 1
            java.lang.String r4 = r6.substring(r4)
            r8.<init>(r4)
            r4 = r8
            goto L_0x00e9
        L_0x00e7:
            r21 = r4
        L_0x00e9:
            r3 = r20
            goto L_0x011d
        L_0x00ec:
            r21 = r4
            java.lang.String r4 = "vthumb://"
            boolean r4 = r6.startsWith(r4)
            if (r4 == 0) goto L_0x0110
            r4 = 9
            int r3 = r6.indexOf(r3, r4)
            if (r3 < 0) goto L_0x010b
            java.io.File r4 = new java.io.File
            int r8 = r3 + 1
            java.lang.String r8 = r6.substring(r8)
            r4.<init>(r8)
            goto L_0x010d
        L_0x010b:
            r4 = r21
        L_0x010d:
            r3 = r20
            goto L_0x011d
        L_0x0110:
            java.io.File r3 = new java.io.File
            r3.<init>(r6)
            r4 = r3
            r3 = r20
            goto L_0x011d
        L_0x0119:
            r21 = r4
            r4 = r21
        L_0x011d:
            r23 = r9
            r24 = r15
            goto L_0x0247
        L_0x0123:
            r21 = r4
            if (r1 != 0) goto L_0x023d
            if (r39 == 0) goto L_0x023d
            r3 = 1
            boolean r4 = r14 instanceof im.bclpbkiauv.messenger.MessageObject
            if (r4 == 0) goto L_0x014d
            r4 = r14
            im.bclpbkiauv.messenger.MessageObject r4 = (im.bclpbkiauv.messenger.MessageObject) r4
            im.bclpbkiauv.tgnet.TLRPC$Document r6 = r4.getDocument()
            im.bclpbkiauv.tgnet.TLRPC$Message r8 = r4.messageOwner
            java.lang.String r8 = r8.attachPath
            r20 = r3
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r4.messageOwner
            java.io.File r3 = im.bclpbkiauv.messenger.FileLoader.getPathToMessage(r3)
            int r22 = r4.getFileType()
            r4 = 0
            r27 = r22
            r22 = r5
            r5 = r27
            goto L_0x017f
        L_0x014d:
            r20 = r3
            if (r41 == 0) goto L_0x0173
            r6 = r41
            r3 = 1
            java.io.File r4 = im.bclpbkiauv.messenger.FileLoader.getPathToAttach(r6, r3)
            boolean r3 = im.bclpbkiauv.messenger.MessageObject.isVideoDocument(r6)
            if (r3 == 0) goto L_0x0162
            r3 = 2
            r22 = r3
            goto L_0x0165
        L_0x0162:
            r3 = 3
            r22 = r3
        L_0x0165:
            r8 = 0
            r3 = 1
            r27 = r4
            r4 = r3
            r3 = r27
            r28 = r22
            r22 = r5
            r5 = r28
            goto L_0x017f
        L_0x0173:
            r6 = 0
            r8 = 0
            r3 = 0
            r22 = 0
            r4 = 0
            r27 = r22
            r22 = r5
            r5 = r27
        L_0x017f:
            if (r6 == 0) goto L_0x0232
            if (r42 == 0) goto L_0x01bd
            r23 = r9
            java.io.File r9 = new java.io.File
            r24 = r15
            java.io.File r15 = im.bclpbkiauv.messenger.FileLoader.getDirectory(r18)
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            java.lang.String r2 = "q_"
            r14.append(r2)
            int r2 = r6.dc_id
            r14.append(r2)
            r14.append(r7)
            long r1 = r6.id
            r14.append(r1)
            java.lang.String r1 = ".jpg"
            r14.append(r1)
            java.lang.String r1 = r14.toString()
            r9.<init>(r15, r1)
            r1 = r9
            boolean r2 = r1.exists()
            if (r2 != 0) goto L_0x01b9
            r1 = 0
            goto L_0x01c3
        L_0x01b9:
            r2 = 1
            r22 = r2
            goto L_0x01c3
        L_0x01bd:
            r23 = r9
            r24 = r15
            r1 = r21
        L_0x01c3:
            r2 = 0
            boolean r9 = android.text.TextUtils.isEmpty(r8)
            if (r9 != 0) goto L_0x01d7
            java.io.File r9 = new java.io.File
            r9.<init>(r8)
            r2 = r9
            boolean r9 = r2.exists()
            if (r9 != 0) goto L_0x01d7
            r2 = 0
        L_0x01d7:
            if (r2 != 0) goto L_0x01da
            r2 = r3
        L_0x01da:
            if (r1 != 0) goto L_0x022c
            java.lang.String r7 = im.bclpbkiauv.messenger.FileLoader.getAttachFileName(r6)
            java.util.HashMap<java.lang.String, im.bclpbkiauv.messenger.ImageLoader$ThumbGenerateInfo> r9 = r0.waitingForQualityThumb
            java.lang.Object r9 = r9.get(r7)
            im.bclpbkiauv.messenger.ImageLoader$ThumbGenerateInfo r9 = (im.bclpbkiauv.messenger.ImageLoader.ThumbGenerateInfo) r9
            if (r9 != 0) goto L_0x01ff
            im.bclpbkiauv.messenger.ImageLoader$ThumbGenerateInfo r14 = new im.bclpbkiauv.messenger.ImageLoader$ThumbGenerateInfo
            r15 = 0
            r14.<init>()
            r9 = r14
            im.bclpbkiauv.tgnet.TLRPC.Document unused = r9.parentDocument = r6
            java.lang.String unused = r9.filter = r12
            boolean unused = r9.big = r4
            java.util.HashMap<java.lang.String, im.bclpbkiauv.messenger.ImageLoader$ThumbGenerateInfo> r14 = r0.waitingForQualityThumb
            r14.put(r7, r9)
        L_0x01ff:
            java.util.ArrayList r14 = r9.imageReceiverArray
            boolean r14 = r14.contains(r11)
            if (r14 != 0) goto L_0x021b
            java.util.ArrayList r14 = r9.imageReceiverArray
            r14.add(r11)
            java.util.ArrayList r14 = r9.imageReceiverGuidsArray
            java.lang.Integer r15 = java.lang.Integer.valueOf(r37)
            r14.add(r15)
        L_0x021b:
            android.util.SparseArray<java.lang.String> r14 = r0.waitingForQualityThumbByTag
            r14.put(r10, r7)
            boolean r14 = r2.exists()
            if (r14 == 0) goto L_0x022b
            if (r43 == 0) goto L_0x022b
            r0.generateThumb(r5, r2, r9)
        L_0x022b:
            return
        L_0x022c:
            r4 = r1
            r3 = r20
            r5 = r22
            goto L_0x0247
        L_0x0232:
            r23 = r9
            r24 = r15
            r3 = r20
            r4 = r21
            r5 = r22
            goto L_0x0247
        L_0x023d:
            r22 = r5
            r23 = r9
            r24 = r15
            r4 = r21
            r5 = r22
        L_0x0247:
            r1 = r30
            r2 = 2
            if (r1 == r2) goto L_0x0594
            boolean r9 = r38.isEncrypted()
            im.bclpbkiauv.messenger.ImageLoader$CacheImage r6 = new im.bclpbkiauv.messenger.ImageLoader$CacheImage
            r8 = 0
            r6.<init>()
            r14 = r6
            if (r39 != 0) goto L_0x02a7
            im.bclpbkiauv.messenger.WebFile r6 = r13.webFile
            boolean r6 = im.bclpbkiauv.messenger.MessageObject.isGifDocument((im.bclpbkiauv.messenger.WebFile) r6)
            if (r6 != 0) goto L_0x02a4
            im.bclpbkiauv.tgnet.TLRPC$Document r6 = r13.document
            boolean r6 = im.bclpbkiauv.messenger.MessageObject.isGifDocument((im.bclpbkiauv.tgnet.TLRPC.Document) r6)
            if (r6 != 0) goto L_0x02a4
            im.bclpbkiauv.tgnet.TLRPC$Document r6 = r13.document
            boolean r6 = im.bclpbkiauv.messenger.MessageObject.isRoundVideoDocument(r6)
            if (r6 == 0) goto L_0x0272
            goto L_0x02a4
        L_0x0272:
            java.lang.String r6 = r13.path
            if (r6 == 0) goto L_0x02a7
            java.lang.String r6 = r13.path
            java.lang.String r8 = "vthumb"
            boolean r8 = r6.startsWith(r8)
            if (r8 != 0) goto L_0x02a7
            java.lang.String r8 = "thumb"
            boolean r8 = r6.startsWith(r8)
            if (r8 != 0) goto L_0x02a7
            java.lang.String r8 = "jpg"
            java.lang.String r8 = getHttpUrlExtension(r6, r8)
            java.lang.String r15 = "mp4"
            boolean r15 = r8.equals(r15)
            if (r15 != 0) goto L_0x02a0
            java.lang.String r15 = "gif"
            boolean r15 = r8.equals(r15)
            if (r15 == 0) goto L_0x02a7
        L_0x02a0:
            r15 = 1
            r14.animatedFile = r15
            goto L_0x02a7
        L_0x02a4:
            r6 = 1
            r14.animatedFile = r6
        L_0x02a7:
            if (r4 != 0) goto L_0x0429
            r6 = 0
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r8 = r13.photoSize
            boolean r8 = r8 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_photoStrippedSize
            java.lang.String r2 = "g"
            if (r8 == 0) goto L_0x02bb
            r3 = 1
            r15 = r31
            r8 = r44
            r26 = r9
            goto L_0x0411
        L_0x02bb:
            im.bclpbkiauv.messenger.SecureDocument r8 = r13.secureDocument
            if (r8 == 0) goto L_0x02e3
            im.bclpbkiauv.messenger.SecureDocument r7 = r13.secureDocument
            r14.secureDocument = r7
            im.bclpbkiauv.messenger.SecureDocument r7 = r14.secureDocument
            im.bclpbkiauv.tgnet.TLRPC$TL_secureFile r7 = r7.secureFile
            int r7 = r7.dc_id
            r8 = -2147483648(0xffffffff80000000, float:-0.0)
            if (r7 != r8) goto L_0x02cf
            r7 = 1
            goto L_0x02d0
        L_0x02cf:
            r7 = 0
        L_0x02d0:
            r3 = r7
            java.io.File r7 = new java.io.File
            java.io.File r8 = im.bclpbkiauv.messenger.FileLoader.getDirectory(r18)
            r15 = r31
            r7.<init>(r8, r15)
            r4 = r7
            r8 = r44
            r26 = r9
            goto L_0x0411
        L_0x02e3:
            r15 = r31
            boolean r8 = r2.equals(r12)
            r20 = r3
            java.lang.String r3 = "application/x-tgsticker"
            if (r8 != 0) goto L_0x0361
            r8 = r44
            r10 = 2
            if (r8 != 0) goto L_0x0307
            r10 = r45
            if (r10 <= 0) goto L_0x0309
            r21 = r4
            java.lang.String r4 = r13.path
            if (r4 != 0) goto L_0x030b
            if (r9 == 0) goto L_0x0301
            goto L_0x030b
        L_0x0301:
            r22 = r5
            r25 = r6
            goto L_0x036b
        L_0x0307:
            r10 = r45
        L_0x0309:
            r21 = r4
        L_0x030b:
            java.io.File r4 = new java.io.File
            java.io.File r7 = im.bclpbkiauv.messenger.FileLoader.getDirectory(r18)
            r4.<init>(r7, r15)
            boolean r7 = r4.exists()
            if (r7 == 0) goto L_0x031e
            r5 = 1
            r25 = r6
            goto L_0x034b
        L_0x031e:
            r7 = 2
            if (r8 != r7) goto L_0x0345
            java.io.File r7 = new java.io.File
            r21 = r4
            java.io.File r4 = im.bclpbkiauv.messenger.FileLoader.getDirectory(r18)
            r22 = r5
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r15)
            r25 = r6
            java.lang.String r6 = ".enc"
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            r7.<init>(r4, r5)
            r4 = r7
            r5 = r22
            goto L_0x034b
        L_0x0345:
            r21 = r4
            r22 = r5
            r25 = r6
        L_0x034b:
            im.bclpbkiauv.tgnet.TLRPC$Document r6 = r13.document
            if (r6 == 0) goto L_0x0359
            im.bclpbkiauv.tgnet.TLRPC$Document r6 = r13.document
            java.lang.String r6 = r6.mime_type
            boolean r3 = r3.equals(r6)
            r14.lottieFile = r3
        L_0x0359:
            r26 = r9
            r3 = r20
            r6 = r25
            goto L_0x0411
        L_0x0361:
            r8 = r44
            r10 = r45
            r21 = r4
            r22 = r5
            r25 = r6
        L_0x036b:
            im.bclpbkiauv.tgnet.TLRPC$Document r4 = r13.document
            r5 = 3
            if (r4 == 0) goto L_0x03e9
            im.bclpbkiauv.tgnet.TLRPC$Document r4 = r13.document
            boolean r6 = r4 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_documentEncrypted
            if (r6 == 0) goto L_0x0382
            java.io.File r5 = new java.io.File
            java.io.File r6 = im.bclpbkiauv.messenger.FileLoader.getDirectory(r18)
            r5.<init>(r6, r15)
            r26 = r9
            goto L_0x03a1
        L_0x0382:
            boolean r6 = im.bclpbkiauv.messenger.MessageObject.isVideoDocument(r4)
            if (r6 == 0) goto L_0x0395
            java.io.File r5 = new java.io.File
            r26 = r9
            r6 = 2
            java.io.File r9 = im.bclpbkiauv.messenger.FileLoader.getDirectory(r6)
            r5.<init>(r9, r15)
            goto L_0x03a1
        L_0x0395:
            r26 = r9
            java.io.File r6 = new java.io.File
            java.io.File r5 = im.bclpbkiauv.messenger.FileLoader.getDirectory(r5)
            r6.<init>(r5, r15)
            r5 = r6
        L_0x03a1:
            boolean r6 = r2.equals(r12)
            if (r6 == 0) goto L_0x03d5
            boolean r6 = r5.exists()
            if (r6 != 0) goto L_0x03d5
            java.io.File r6 = new java.io.File
            java.io.File r9 = im.bclpbkiauv.messenger.FileLoader.getDirectory(r18)
            r21 = r5
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            int r11 = r4.dc_id
            r5.append(r11)
            r5.append(r7)
            long r10 = r4.id
            r5.append(r10)
            java.lang.String r7 = ".temp"
            r5.append(r7)
            java.lang.String r5 = r5.toString()
            r6.<init>(r9, r5)
            r5 = r6
            goto L_0x03d9
        L_0x03d5:
            r21 = r5
            r5 = r21
        L_0x03d9:
            java.lang.String r6 = r4.mime_type
            boolean r3 = r3.equals(r6)
            r14.lottieFile = r3
            int r6 = r4.size
            r4 = r5
            r3 = r20
            r5 = r22
            goto L_0x0411
        L_0x03e9:
            r26 = r9
            im.bclpbkiauv.messenger.WebFile r3 = r13.webFile
            if (r3 == 0) goto L_0x0400
            java.io.File r3 = new java.io.File
            java.io.File r4 = im.bclpbkiauv.messenger.FileLoader.getDirectory(r5)
            r3.<init>(r4, r15)
            r4 = r3
            r3 = r20
            r5 = r22
            r6 = r25
            goto L_0x0411
        L_0x0400:
            java.io.File r3 = new java.io.File
            r4 = 0
            java.io.File r5 = im.bclpbkiauv.messenger.FileLoader.getDirectory(r4)
            r3.<init>(r5, r15)
            r4 = r3
            r3 = r20
            r5 = r22
            r6 = r25
        L_0x0411:
            boolean r2 = r2.equals(r12)
            if (r2 == 0) goto L_0x0423
            r2 = 1
            r14.animatedFile = r2
            r14.size = r6
            r3 = 1
            r20 = r3
            r2 = r4
            r22 = r5
            goto L_0x0437
        L_0x0423:
            r20 = r3
            r2 = r4
            r22 = r5
            goto L_0x0437
        L_0x0429:
            r15 = r31
            r8 = r44
            r20 = r3
            r21 = r4
            r22 = r5
            r26 = r9
            r2 = r21
        L_0x0437:
            r9 = r36
            r14.imageType = r9
            r10 = r32
            r14.key = r10
            r14.filter = r12
            r14.imageLocation = r13
            r11 = r46
            r14.ext = r11
            r7 = r47
            r14.currentAccount = r7
            r6 = r40
            r14.parentObject = r6
            boolean r3 = r13.lottieAnimation
            if (r3 == 0) goto L_0x0457
            r5 = 1
            r14.lottieFile = r5
            goto L_0x0458
        L_0x0457:
            r5 = 1
        L_0x0458:
            r4 = 2
            if (r8 != r4) goto L_0x0477
            java.io.File r3 = new java.io.File
            java.io.File r4 = im.bclpbkiauv.messenger.FileLoader.getInternalCacheDir()
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r15)
            java.lang.String r6 = ".enc.key"
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            r3.<init>(r4, r5)
            r14.encryptionKeyPath = r3
        L_0x0477:
            r3 = r14
            r17 = 2
            r4 = r34
            r19 = 1
            r5 = r32
            r9 = r40
            r6 = r35
            r7 = r36
            r11 = r8
            r8 = r37
            r3.addImageReceiver(r4, r5, r6, r7, r8)
            if (r20 != 0) goto L_0x0572
            if (r22 != 0) goto L_0x0572
            boolean r3 = r2.exists()
            if (r3 == 0) goto L_0x0498
            goto L_0x0572
        L_0x0498:
            r14.url = r15
            java.util.HashMap<java.lang.String, im.bclpbkiauv.messenger.ImageLoader$CacheImage> r3 = r0.imageLoadingByUrl
            r3.put(r15, r14)
            java.lang.String r3 = r13.path
            if (r3 == 0) goto L_0x04fc
            java.lang.String r3 = r13.path
            java.lang.String r3 = im.bclpbkiauv.messenger.Utilities.MD5(r3)
            java.io.File r4 = im.bclpbkiauv.messenger.FileLoader.getDirectory(r18)
            java.io.File r5 = new java.io.File
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r3)
            java.lang.String r7 = "_temp.jpg"
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            r5.<init>(r4, r6)
            r14.tempFilePath = r5
            r14.finalFilePath = r2
            java.lang.String r5 = r13.path
            r6 = r24
            boolean r5 = r5.startsWith(r6)
            if (r5 == 0) goto L_0x04e6
            im.bclpbkiauv.messenger.ImageLoader$ArtworkLoadTask r5 = new im.bclpbkiauv.messenger.ImageLoader$ArtworkLoadTask
            r5.<init>(r14)
            r14.artworkTask = r5
            java.util.LinkedList<im.bclpbkiauv.messenger.ImageLoader$ArtworkLoadTask> r5 = r0.artworkTasks
            im.bclpbkiauv.messenger.ImageLoader$ArtworkLoadTask r6 = r14.artworkTask
            r5.add(r6)
            r5 = 0
            r0.runArtworkTasks(r5)
            r8 = r45
            goto L_0x04fa
        L_0x04e6:
            r5 = 0
            im.bclpbkiauv.messenger.ImageLoader$HttpImageTask r6 = new im.bclpbkiauv.messenger.ImageLoader$HttpImageTask
            r8 = r45
            r6.<init>(r14, r8)
            r14.httpTask = r6
            java.util.LinkedList<im.bclpbkiauv.messenger.ImageLoader$HttpImageTask> r6 = r0.httpTasks
            im.bclpbkiauv.messenger.ImageLoader$HttpImageTask r7 = r14.httpTask
            r6.add(r7)
            r0.runHttpTasks(r5)
        L_0x04fa:
            goto L_0x05a9
        L_0x04fc:
            r8 = r45
            im.bclpbkiauv.tgnet.TLRPC$TL_fileLocationToBeDeprecated r3 = r13.location
            if (r3 == 0) goto L_0x0527
            r3 = r44
            if (r3 != 0) goto L_0x0510
            if (r8 <= 0) goto L_0x050c
            byte[] r4 = r13.key
            if (r4 == 0) goto L_0x0510
        L_0x050c:
            r3 = 1
            r18 = r3
            goto L_0x0512
        L_0x0510:
            r18 = r3
        L_0x0512:
            im.bclpbkiauv.messenger.FileLoader r3 = im.bclpbkiauv.messenger.FileLoader.getInstance(r47)
            if (r1 == 0) goto L_0x051a
            r7 = 2
            goto L_0x051b
        L_0x051a:
            r7 = 1
        L_0x051b:
            r4 = r38
            r5 = r40
            r6 = r46
            r8 = r18
            r3.loadFile(r4, r5, r6, r7, r8)
            goto L_0x055f
        L_0x0527:
            im.bclpbkiauv.tgnet.TLRPC$Document r3 = r13.document
            if (r3 == 0) goto L_0x053a
            im.bclpbkiauv.messenger.FileLoader r3 = im.bclpbkiauv.messenger.FileLoader.getInstance(r47)
            im.bclpbkiauv.tgnet.TLRPC$Document r4 = r13.document
            if (r1 == 0) goto L_0x0535
            r7 = 2
            goto L_0x0536
        L_0x0535:
            r7 = 1
        L_0x0536:
            r3.loadFile(r4, r9, r7, r11)
            goto L_0x055f
        L_0x053a:
            im.bclpbkiauv.messenger.SecureDocument r3 = r13.secureDocument
            if (r3 == 0) goto L_0x054d
            im.bclpbkiauv.messenger.FileLoader r3 = im.bclpbkiauv.messenger.FileLoader.getInstance(r47)
            im.bclpbkiauv.messenger.SecureDocument r4 = r13.secureDocument
            if (r1 == 0) goto L_0x0548
            r7 = 2
            goto L_0x0549
        L_0x0548:
            r7 = 1
        L_0x0549:
            r3.loadFile(r4, r7)
            goto L_0x055f
        L_0x054d:
            im.bclpbkiauv.messenger.WebFile r3 = r13.webFile
            if (r3 == 0) goto L_0x055f
            im.bclpbkiauv.messenger.FileLoader r3 = im.bclpbkiauv.messenger.FileLoader.getInstance(r47)
            im.bclpbkiauv.messenger.WebFile r4 = r13.webFile
            if (r1 == 0) goto L_0x055b
            r7 = 2
            goto L_0x055c
        L_0x055b:
            r7 = 1
        L_0x055c:
            r3.loadFile(r4, r7, r11)
        L_0x055f:
            boolean r3 = r34.isForceLoding()
            if (r3 == 0) goto L_0x05a9
            java.util.HashMap<java.lang.String, java.lang.Integer> r3 = r0.forceLoadingImages
            java.lang.String r4 = r14.key
            r5 = 0
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r3.put(r4, r5)
            goto L_0x05a9
        L_0x0572:
            r14.finalFilePath = r2
            r14.imageLocation = r13
            im.bclpbkiauv.messenger.ImageLoader$CacheOutTask r3 = new im.bclpbkiauv.messenger.ImageLoader$CacheOutTask
            r3.<init>(r14)
            r14.cacheTask = r3
            java.util.HashMap<java.lang.String, im.bclpbkiauv.messenger.ImageLoader$CacheImage> r3 = r0.imageLoadingByKeys
            r3.put(r10, r14)
            if (r1 == 0) goto L_0x058c
            im.bclpbkiauv.messenger.DispatchQueue r3 = r0.cacheThumbOutQueue
            im.bclpbkiauv.messenger.ImageLoader$CacheOutTask r4 = r14.cacheTask
            r3.postRunnable(r4)
            goto L_0x05a9
        L_0x058c:
            im.bclpbkiauv.messenger.DispatchQueue r3 = r0.cacheOutQueue
            im.bclpbkiauv.messenger.ImageLoader$CacheOutTask r4 = r14.cacheTask
            r3.postRunnable(r4)
            goto L_0x05a9
        L_0x0594:
            r15 = r31
            r10 = r32
            r9 = r40
            r11 = r44
            r20 = r3
            r21 = r4
            r22 = r5
            goto L_0x05a9
        L_0x05a3:
            r10 = r32
            r11 = r44
            r15 = r2
            r9 = r14
        L_0x05a9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.ImageLoader.lambda$createLoadOperationForImageReceiver$5$ImageLoader(int, java.lang.String, java.lang.String, int, im.bclpbkiauv.messenger.ImageReceiver, java.lang.String, int, int, im.bclpbkiauv.messenger.ImageLocation, boolean, java.lang.Object, im.bclpbkiauv.tgnet.TLRPC$Document, boolean, boolean, int, int, java.lang.String, int):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:128:0x028b  */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x02dc  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x02de  */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x030f  */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x0376  */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x037e A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x0399 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:184:0x03b4 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x03d3  */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x0410  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x012d  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x013c  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x0157  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x015e  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0179  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadImageForImageReceiver(im.bclpbkiauv.messenger.ImageReceiver r44) {
        /*
            r43 = this;
            r15 = r43
            r14 = r44
            if (r14 != 0) goto L_0x0007
            return
        L_0x0007:
            r6 = 0
            java.lang.String r7 = r44.getMediaKey()
            int r21 = r44.getNewGuid()
            r9 = 1
            if (r7 == 0) goto L_0x0057
            im.bclpbkiauv.messenger.ImageLocation r8 = r44.getMediaLocation()
            if (r8 == 0) goto L_0x002f
            im.bclpbkiauv.tgnet.TLRPC$Document r0 = r8.document
            boolean r0 = im.bclpbkiauv.messenger.MessageObject.isAnimatedStickerDocument(r0)
            if (r0 != 0) goto L_0x0025
            boolean r0 = r8.lottieAnimation
            if (r0 == 0) goto L_0x002f
        L_0x0025:
            im.bclpbkiauv.messenger.LruCache<im.bclpbkiauv.ui.components.RLottieDrawable> r0 = r15.lottieMemCache
            java.lang.Object r0 = r0.get(r7)
            android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
            r10 = r0
            goto L_0x003f
        L_0x002f:
            im.bclpbkiauv.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r0 = r15.memCache
            java.lang.Object r0 = r0.get(r7)
            android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
            if (r0 == 0) goto L_0x003e
            im.bclpbkiauv.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r15.memCache
            r1.moveToFront(r7)
        L_0x003e:
            r10 = r0
        L_0x003f:
            if (r10 == 0) goto L_0x0057
            r15.cancelLoadingForImageReceiver(r14, r9)
            r3 = 3
            r4 = 1
            r0 = r44
            r1 = r10
            r2 = r7
            r5 = r21
            r0.setImageBitmapByKey(r1, r2, r3, r4, r5)
            r6 = 1
            boolean r0 = r44.isForcePreview()
            if (r0 != 0) goto L_0x0057
            return
        L_0x0057:
            java.lang.String r8 = r44.getImageKey()
            if (r6 != 0) goto L_0x00a5
            if (r8 == 0) goto L_0x00a5
            im.bclpbkiauv.messenger.ImageLocation r10 = r44.getImageLocation()
            if (r10 == 0) goto L_0x007b
            im.bclpbkiauv.tgnet.TLRPC$Document r0 = r10.document
            boolean r0 = im.bclpbkiauv.messenger.MessageObject.isAnimatedStickerDocument(r0)
            if (r0 != 0) goto L_0x0071
            boolean r0 = r10.lottieAnimation
            if (r0 == 0) goto L_0x007b
        L_0x0071:
            im.bclpbkiauv.messenger.LruCache<im.bclpbkiauv.ui.components.RLottieDrawable> r0 = r15.lottieMemCache
            java.lang.Object r0 = r0.get(r8)
            android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
            r11 = r0
            goto L_0x008b
        L_0x007b:
            im.bclpbkiauv.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r0 = r15.memCache
            java.lang.Object r0 = r0.get(r8)
            android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
            if (r0 == 0) goto L_0x008a
            im.bclpbkiauv.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r15.memCache
            r1.moveToFront(r8)
        L_0x008a:
            r11 = r0
        L_0x008b:
            if (r11 == 0) goto L_0x00a5
            r15.cancelLoadingForImageReceiver(r14, r9)
            r3 = 0
            r4 = 1
            r0 = r44
            r1 = r11
            r2 = r8
            r5 = r21
            r0.setImageBitmapByKey(r1, r2, r3, r4, r5)
            r6 = 1
            boolean r0 = r44.isForcePreview()
            if (r0 != 0) goto L_0x00a5
            if (r7 != 0) goto L_0x00a5
            return
        L_0x00a5:
            r22 = r6
            r6 = 0
            java.lang.String r10 = r44.getThumbKey()
            r11 = 0
            if (r10 == 0) goto L_0x00f8
            im.bclpbkiauv.messenger.ImageLocation r12 = r44.getThumbLocation()
            if (r12 == 0) goto L_0x00cb
            im.bclpbkiauv.tgnet.TLRPC$Document r0 = r12.document
            boolean r0 = im.bclpbkiauv.messenger.MessageObject.isAnimatedStickerDocument(r0)
            if (r0 != 0) goto L_0x00c1
            boolean r0 = r12.lottieAnimation
            if (r0 == 0) goto L_0x00cb
        L_0x00c1:
            im.bclpbkiauv.messenger.LruCache<im.bclpbkiauv.ui.components.RLottieDrawable> r0 = r15.lottieMemCache
            java.lang.Object r0 = r0.get(r8)
            android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
            r13 = r0
            goto L_0x00db
        L_0x00cb:
            im.bclpbkiauv.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r0 = r15.memCache
            java.lang.Object r0 = r0.get(r10)
            android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
            if (r0 == 0) goto L_0x00da
            im.bclpbkiauv.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r15.memCache
            r1.moveToFront(r10)
        L_0x00da:
            r13 = r0
        L_0x00db:
            if (r13 == 0) goto L_0x00f8
            r3 = 1
            r4 = 1
            r0 = r44
            r1 = r13
            r2 = r10
            r5 = r21
            r0.setImageBitmapByKey(r1, r2, r3, r4, r5)
            r15.cancelLoadingForImageReceiver(r14, r11)
            if (r22 == 0) goto L_0x00f4
            boolean r0 = r44.isForcePreview()
            if (r0 == 0) goto L_0x00f4
            return
        L_0x00f4:
            r6 = 1
            r23 = r6
            goto L_0x00fa
        L_0x00f8:
            r23 = r6
        L_0x00fa:
            r0 = 0
            java.lang.Object r13 = r44.getParentObject()
            im.bclpbkiauv.tgnet.TLRPC$Document r24 = r44.getQulityThumbDocument()
            im.bclpbkiauv.messenger.ImageLocation r12 = r44.getThumbLocation()
            java.lang.String r6 = r44.getThumbFilter()
            im.bclpbkiauv.messenger.ImageLocation r1 = r44.getMediaLocation()
            java.lang.String r5 = r44.getMediaFilter()
            im.bclpbkiauv.messenger.ImageLocation r2 = r44.getImageLocation()
            java.lang.String r4 = r44.getImageFilter()
            if (r2 != 0) goto L_0x0146
            boolean r3 = r44.isNeedsQualityThumb()
            if (r3 == 0) goto L_0x0146
            boolean r3 = r44.isCurrentKeyQuality()
            if (r3 == 0) goto L_0x0146
            boolean r3 = r13 instanceof im.bclpbkiauv.messenger.MessageObject
            if (r3 == 0) goto L_0x013c
            r3 = r13
            im.bclpbkiauv.messenger.MessageObject r3 = (im.bclpbkiauv.messenger.MessageObject) r3
            im.bclpbkiauv.tgnet.TLRPC$Document r16 = r3.getDocument()
            im.bclpbkiauv.messenger.ImageLocation r2 = im.bclpbkiauv.messenger.ImageLocation.getForDocument(r16)
            r0 = 1
            r25 = r0
            goto L_0x0148
        L_0x013c:
            if (r24 == 0) goto L_0x0146
            im.bclpbkiauv.messenger.ImageLocation r2 = im.bclpbkiauv.messenger.ImageLocation.getForDocument(r24)
            r0 = 1
            r25 = r0
            goto L_0x0148
        L_0x0146:
            r25 = r0
        L_0x0148:
            r0 = 0
            r3 = 0
            r16 = 0
            r17 = 0
            r8 = 0
            r10 = 0
            r7 = 0
            java.lang.String r18 = r44.getExt()
            if (r18 != 0) goto L_0x015e
            java.lang.String r18 = "jpg"
            r19 = r8
            r8 = r18
            goto L_0x0162
        L_0x015e:
            r19 = r8
            r8 = r18
        L_0x0162:
            r18 = 0
            r26 = r0
            r27 = r1
            r28 = r3
            r0 = r7
            r29 = r17
            r1 = r19
            r7 = r2
            r2 = r18
        L_0x0172:
            java.lang.String r3 = "jpg"
            r11 = 2
            java.lang.String r9 = "."
            if (r2 >= r11) goto L_0x030a
            if (r2 != 0) goto L_0x017d
            r11 = r7
            goto L_0x017f
        L_0x017d:
            r11 = r27
        L_0x017f:
            if (r11 != 0) goto L_0x0184
            r19 = r10
            goto L_0x0193
        L_0x0184:
            r19 = r10
            if (r27 == 0) goto L_0x018b
            r10 = r27
            goto L_0x018c
        L_0x018b:
            r10 = r7
        L_0x018c:
            java.lang.String r10 = r11.getKey(r13, r10)
            if (r10 != 0) goto L_0x0196
        L_0x0193:
            r14 = 1
            goto L_0x02fe
        L_0x0196:
            r20 = 0
            java.lang.String r14 = r11.path
            if (r14 == 0) goto L_0x01b7
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r14.append(r10)
            r14.append(r9)
            java.lang.String r9 = r11.path
            java.lang.String r3 = getHttpUrlExtension(r9, r3)
            r14.append(r3)
            java.lang.String r20 = r14.toString()
            r14 = 1
            goto L_0x02e3
        L_0x01b7:
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r3 = r11.photoSize
            boolean r3 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_photoStrippedSize
            if (r3 == 0) goto L_0x01d2
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r10)
            r3.append(r9)
            r3.append(r8)
            java.lang.String r20 = r3.toString()
            r14 = 1
            goto L_0x02e3
        L_0x01d2:
            im.bclpbkiauv.tgnet.TLRPC$TL_fileLocationToBeDeprecated r3 = r11.location
            if (r3 == 0) goto L_0x020e
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r10)
            r3.append(r9)
            r3.append(r8)
            java.lang.String r20 = r3.toString()
            java.lang.String r3 = r44.getExt()
            if (r3 != 0) goto L_0x0209
            im.bclpbkiauv.tgnet.TLRPC$TL_fileLocationToBeDeprecated r3 = r11.location
            byte[] r3 = r3.key
            if (r3 != 0) goto L_0x0209
            im.bclpbkiauv.tgnet.TLRPC$TL_fileLocationToBeDeprecated r3 = r11.location
            long r14 = r3.volume_id
            r30 = -2147483648(0xffffffff80000000, double:NaN)
            int r3 = (r14 > r30 ? 1 : (r14 == r30 ? 0 : -1))
            if (r3 != 0) goto L_0x0206
            im.bclpbkiauv.tgnet.TLRPC$TL_fileLocationToBeDeprecated r3 = r11.location
            int r3 = r3.local_id
            if (r3 >= 0) goto L_0x0206
            goto L_0x0209
        L_0x0206:
            r14 = 1
            goto L_0x02e3
        L_0x0209:
            r26 = 1
            r14 = 1
            goto L_0x02e3
        L_0x020e:
            im.bclpbkiauv.messenger.WebFile r3 = r11.webFile
            if (r3 == 0) goto L_0x0237
            im.bclpbkiauv.messenger.WebFile r3 = r11.webFile
            java.lang.String r3 = r3.mime_type
            java.lang.String r3 = im.bclpbkiauv.messenger.FileLoader.getMimeTypePart(r3)
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r14.append(r10)
            r14.append(r9)
            im.bclpbkiauv.messenger.WebFile r9 = r11.webFile
            java.lang.String r9 = r9.url
            java.lang.String r9 = getHttpUrlExtension(r9, r3)
            r14.append(r9)
            java.lang.String r20 = r14.toString()
            r14 = 1
            goto L_0x02e3
        L_0x0237:
            im.bclpbkiauv.messenger.SecureDocument r3 = r11.secureDocument
            if (r3 == 0) goto L_0x0250
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r10)
            r3.append(r9)
            r3.append(r8)
            java.lang.String r20 = r3.toString()
            r14 = 1
            goto L_0x02e3
        L_0x0250:
            im.bclpbkiauv.tgnet.TLRPC$Document r3 = r11.document
            if (r3 == 0) goto L_0x02e2
            if (r2 != 0) goto L_0x026a
            if (r25 == 0) goto L_0x026a
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r9 = "q_"
            r3.append(r9)
            r3.append(r10)
            java.lang.String r3 = r3.toString()
            r10 = r3
        L_0x026a:
            im.bclpbkiauv.tgnet.TLRPC$Document r3 = r11.document
            java.lang.String r3 = im.bclpbkiauv.messenger.FileLoader.getDocumentFileName(r3)
            if (r3 == 0) goto L_0x0282
            r9 = 46
            int r9 = r3.lastIndexOf(r9)
            r14 = r9
            r15 = -1
            if (r9 != r15) goto L_0x027d
            goto L_0x0282
        L_0x027d:
            java.lang.String r3 = r3.substring(r14)
            goto L_0x0284
        L_0x0282:
            java.lang.String r3 = ""
        L_0x0284:
            int r9 = r3.length()
            r14 = 1
            if (r9 > r14) goto L_0x02ad
            im.bclpbkiauv.tgnet.TLRPC$Document r9 = r11.document
            java.lang.String r9 = r9.mime_type
            java.lang.String r15 = "video/mp4"
            boolean r9 = r15.equals(r9)
            if (r9 == 0) goto L_0x029b
            java.lang.String r3 = ".mp4"
            goto L_0x02ad
        L_0x029b:
            im.bclpbkiauv.tgnet.TLRPC$Document r9 = r11.document
            java.lang.String r9 = r9.mime_type
            java.lang.String r15 = "video/x-matroska"
            boolean r9 = r15.equals(r9)
            if (r9 == 0) goto L_0x02ab
            java.lang.String r3 = ".mkv"
            goto L_0x02ad
        L_0x02ab:
            java.lang.String r3 = ""
        L_0x02ad:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r10)
            r9.append(r3)
            java.lang.String r20 = r9.toString()
            im.bclpbkiauv.tgnet.TLRPC$Document r9 = r11.document
            boolean r9 = im.bclpbkiauv.messenger.MessageObject.isVideoDocument(r9)
            if (r9 != 0) goto L_0x02de
            im.bclpbkiauv.tgnet.TLRPC$Document r9 = r11.document
            boolean r9 = im.bclpbkiauv.messenger.MessageObject.isGifDocument((im.bclpbkiauv.tgnet.TLRPC.Document) r9)
            if (r9 != 0) goto L_0x02de
            im.bclpbkiauv.tgnet.TLRPC$Document r9 = r11.document
            boolean r9 = im.bclpbkiauv.messenger.MessageObject.isRoundVideoDocument(r9)
            if (r9 != 0) goto L_0x02de
            im.bclpbkiauv.tgnet.TLRPC$Document r9 = r11.document
            boolean r9 = im.bclpbkiauv.messenger.MessageObject.canPreviewDocument(r9)
            if (r9 != 0) goto L_0x02de
            r9 = 1
            goto L_0x02df
        L_0x02de:
            r9 = 0
        L_0x02df:
            r26 = r9
            goto L_0x02e3
        L_0x02e2:
            r14 = 1
        L_0x02e3:
            if (r2 != 0) goto L_0x02e9
            r1 = r10
            r28 = r20
            goto L_0x02ec
        L_0x02e9:
            r0 = r10
            r29 = r20
        L_0x02ec:
            if (r11 != r12) goto L_0x02fe
            if (r2 != 0) goto L_0x02f7
            r3 = 0
            r1 = 0
            r7 = 0
            r28 = r7
            r7 = r3
            goto L_0x02fe
        L_0x02f7:
            r3 = 0
            r0 = 0
            r9 = 0
            r27 = r3
            r29 = r9
        L_0x02fe:
            int r2 = r2 + 1
            r15 = r43
            r14 = r44
            r10 = r19
            r9 = 1
            r11 = 0
            goto L_0x0172
        L_0x030a:
            r19 = r10
            r14 = 1
            if (r12 == 0) goto L_0x0376
            im.bclpbkiauv.messenger.ImageLocation r2 = r44.getStrippedLocation()
            if (r2 != 0) goto L_0x031c
            if (r27 == 0) goto L_0x031a
            r10 = r27
            goto L_0x031b
        L_0x031a:
            r10 = r7
        L_0x031b:
            r2 = r10
        L_0x031c:
            java.lang.String r10 = r12.getKey(r13, r2)
            java.lang.String r15 = r12.path
            if (r15 == 0) goto L_0x033f
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            r15.append(r10)
            r15.append(r9)
            java.lang.String r9 = r12.path
            java.lang.String r3 = getHttpUrlExtension(r9, r3)
            r15.append(r3)
            java.lang.String r16 = r15.toString()
            r30 = r16
            goto L_0x037a
        L_0x033f:
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r3 = r12.photoSize
            boolean r3 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_photoStrippedSize
            if (r3 == 0) goto L_0x035a
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r10)
            r3.append(r9)
            r3.append(r8)
            java.lang.String r16 = r3.toString()
            r30 = r16
            goto L_0x037a
        L_0x035a:
            im.bclpbkiauv.tgnet.TLRPC$TL_fileLocationToBeDeprecated r3 = r12.location
            if (r3 == 0) goto L_0x0373
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r10)
            r3.append(r9)
            r3.append(r8)
            java.lang.String r16 = r3.toString()
            r30 = r16
            goto L_0x037a
        L_0x0373:
            r30 = r16
            goto L_0x037a
        L_0x0376:
            r30 = r16
            r10 = r19
        L_0x037a:
            java.lang.String r2 = "@"
            if (r0 == 0) goto L_0x0395
            if (r5 == 0) goto L_0x0395
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r0)
            r3.append(r2)
            r3.append(r5)
            java.lang.String r0 = r3.toString()
            r31 = r0
            goto L_0x0397
        L_0x0395:
            r31 = r0
        L_0x0397:
            if (r1 == 0) goto L_0x03b0
            if (r4 == 0) goto L_0x03b0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r1)
            r0.append(r2)
            r0.append(r4)
            java.lang.String r1 = r0.toString()
            r32 = r1
            goto L_0x03b2
        L_0x03b0:
            r32 = r1
        L_0x03b2:
            if (r10 == 0) goto L_0x03cb
            if (r6 == 0) goto L_0x03cb
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r10)
            r0.append(r2)
            r0.append(r6)
            java.lang.String r10 = r0.toString()
            r33 = r10
            goto L_0x03cd
        L_0x03cb:
            r33 = r10
        L_0x03cd:
            if (r7 == 0) goto L_0x0410
            java.lang.String r0 = r7.path
            if (r0 == 0) goto L_0x0410
            r9 = 0
            r10 = 1
            r15 = 1
            if (r23 == 0) goto L_0x03d9
            r14 = 2
        L_0x03d9:
            r0 = r43
            r1 = r44
            r2 = r33
            r3 = r30
            r34 = r4
            r4 = r8
            r35 = r5
            r5 = r12
            r36 = r6
            r37 = r7
            r7 = r9
            r38 = r8
            r8 = r10
            r9 = r15
            r10 = r14
            r11 = r21
            r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            int r7 = r44.getSize()
            r8 = 1
            r9 = 0
            r10 = 0
            r2 = r32
            r3 = r28
            r4 = r38
            r5 = r37
            r6 = r34
            r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            r41 = r12
            r42 = r13
            goto L_0x04e0
        L_0x0410:
            r34 = r4
            r35 = r5
            r36 = r6
            r37 = r7
            r38 = r8
            if (r27 == 0) goto L_0x0491
            int r0 = r44.getCacheType()
            r39 = 1
            if (r0 != 0) goto L_0x042a
            if (r26 == 0) goto L_0x042a
            r0 = 1
            r40 = r0
            goto L_0x042c
        L_0x042a:
            r40 = r0
        L_0x042c:
            if (r40 != 0) goto L_0x0430
            r8 = 1
            goto L_0x0432
        L_0x0430:
            r8 = r40
        L_0x0432:
            if (r23 != 0) goto L_0x044d
            r7 = 0
            r9 = 1
            if (r23 == 0) goto L_0x043a
            r10 = 2
            goto L_0x043b
        L_0x043a:
            r10 = 1
        L_0x043b:
            r0 = r43
            r1 = r44
            r2 = r33
            r3 = r30
            r4 = r38
            r5 = r12
            r6 = r36
            r11 = r21
            r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
        L_0x044d:
            if (r22 != 0) goto L_0x046f
            r16 = 0
            r18 = 0
            r19 = 0
            r9 = r43
            r10 = r44
            r11 = r32
            r41 = r12
            r12 = r28
            r42 = r13
            r13 = r38
            r14 = r37
            r15 = r34
            r17 = r39
            r20 = r21
            r9.createLoadOperationForImageReceiver(r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20)
            goto L_0x0473
        L_0x046f:
            r41 = r12
            r42 = r13
        L_0x0473:
            int r16 = r44.getSize()
            r18 = 3
            r19 = 0
            r9 = r43
            r10 = r44
            r11 = r31
            r12 = r29
            r13 = r38
            r14 = r27
            r15 = r35
            r17 = r40
            r20 = r21
            r9.createLoadOperationForImageReceiver(r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20)
            goto L_0x04e0
        L_0x0491:
            r41 = r12
            r42 = r13
            int r0 = r44.getCacheType()
            if (r0 != 0) goto L_0x04a1
            if (r26 == 0) goto L_0x04a1
            r0 = 1
            r39 = r0
            goto L_0x04a3
        L_0x04a1:
            r39 = r0
        L_0x04a3:
            if (r39 != 0) goto L_0x04a7
            r8 = 1
            goto L_0x04a9
        L_0x04a7:
            r8 = r39
        L_0x04a9:
            r7 = 0
            r9 = 1
            if (r23 == 0) goto L_0x04af
            r10 = 2
            goto L_0x04b0
        L_0x04af:
            r10 = 1
        L_0x04b0:
            r0 = r43
            r1 = r44
            r2 = r33
            r3 = r30
            r4 = r38
            r5 = r41
            r6 = r36
            r11 = r21
            r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            int r16 = r44.getSize()
            r18 = 0
            r19 = 0
            r9 = r43
            r10 = r44
            r11 = r32
            r12 = r28
            r13 = r38
            r14 = r37
            r15 = r34
            r17 = r39
            r20 = r21
            r9.createLoadOperationForImageReceiver(r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20)
        L_0x04e0:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.ImageLoader.loadImageForImageReceiver(im.bclpbkiauv.messenger.ImageReceiver):void");
    }

    /* access modifiers changed from: private */
    public void httpFileLoadError(String location) {
        this.imageLoadQueue.postRunnable(new Runnable(location) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ImageLoader.this.lambda$httpFileLoadError$6$ImageLoader(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$httpFileLoadError$6$ImageLoader(String location) {
        CacheImage img = this.imageLoadingByUrl.get(location);
        if (img != null) {
            HttpImageTask oldTask = img.httpTask;
            img.httpTask = new HttpImageTask(oldTask.cacheImage, oldTask.imageSize);
            this.httpTasks.add(img.httpTask);
            runHttpTasks(false);
        }
    }

    /* access modifiers changed from: private */
    public void artworkLoadError(String location) {
        this.imageLoadQueue.postRunnable(new Runnable(location) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ImageLoader.this.lambda$artworkLoadError$7$ImageLoader(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$artworkLoadError$7$ImageLoader(String location) {
        CacheImage img = this.imageLoadingByUrl.get(location);
        if (img != null) {
            img.artworkTask = new ArtworkLoadTask(img.artworkTask.cacheImage);
            this.artworkTasks.add(img.artworkTask);
            runArtworkTasks(false);
        }
    }

    /* access modifiers changed from: private */
    public void fileDidLoaded(String location, File finalFile, int type) {
        this.imageLoadQueue.postRunnable(new Runnable(location, type, finalFile) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ File f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ImageLoader.this.lambda$fileDidLoaded$8$ImageLoader(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$fileDidLoaded$8$ImageLoader(String location, int type, File finalFile) {
        String str = location;
        File file = finalFile;
        ThumbGenerateInfo info = this.waitingForQualityThumb.get(str);
        if (info == null || info.parentDocument == null) {
            int i = type;
        } else {
            generateThumb(type, file, info);
            this.waitingForQualityThumb.remove(str);
        }
        CacheImage img = this.imageLoadingByUrl.get(str);
        if (img != null) {
            this.imageLoadingByUrl.remove(str);
            ArrayList<CacheOutTask> tasks = new ArrayList<>();
            for (int a = 0; a < img.imageReceiverArray.size(); a++) {
                String key = img.keys.get(a);
                String filter = img.filters.get(a);
                int imageType = img.imageTypes.get(a).intValue();
                ImageReceiver imageReceiver = img.imageReceiverArray.get(a);
                int guid = img.imageReceiverGuidsArray.get(a).intValue();
                CacheImage cacheImage = this.imageLoadingByKeys.get(key);
                if (cacheImage == null) {
                    cacheImage = new CacheImage();
                    cacheImage.secureDocument = img.secureDocument;
                    cacheImage.currentAccount = img.currentAccount;
                    cacheImage.finalFilePath = file;
                    cacheImage.key = key;
                    cacheImage.imageLocation = img.imageLocation;
                    cacheImage.imageType = imageType;
                    cacheImage.ext = img.ext;
                    cacheImage.encryptionKeyPath = img.encryptionKeyPath;
                    cacheImage.cacheTask = new CacheOutTask(cacheImage);
                    cacheImage.filter = filter;
                    cacheImage.animatedFile = img.animatedFile;
                    cacheImage.lottieFile = img.lottieFile;
                    this.imageLoadingByKeys.put(key, cacheImage);
                    tasks.add(cacheImage.cacheTask);
                }
                int i2 = imageType;
                cacheImage.addImageReceiver(imageReceiver, key, filter, imageType, guid);
            }
            for (int a2 = 0; a2 < tasks.size(); a2++) {
                CacheOutTask task = tasks.get(a2);
                if (task.cacheImage.imageType == 1) {
                    this.cacheThumbOutQueue.postRunnable(task);
                } else {
                    this.cacheOutQueue.postRunnable(task);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void fileDidFailedLoad(String location, int canceled) {
        if (canceled != 1) {
            this.imageLoadQueue.postRunnable(new Runnable(location) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ImageLoader.this.lambda$fileDidFailedLoad$9$ImageLoader(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$fileDidFailedLoad$9$ImageLoader(String location) {
        CacheImage img = this.imageLoadingByUrl.get(location);
        if (img != null) {
            img.setImageAndClear((Drawable) null, (String) null);
        }
    }

    /* access modifiers changed from: private */
    public void runHttpTasks(boolean complete) {
        if (complete) {
            this.currentHttpTasksCount--;
        }
        while (this.currentHttpTasksCount < 4 && !this.httpTasks.isEmpty()) {
            HttpImageTask task = this.httpTasks.poll();
            if (task != null) {
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                this.currentHttpTasksCount++;
            }
        }
    }

    /* access modifiers changed from: private */
    public void runArtworkTasks(boolean complete) {
        if (complete) {
            this.currentArtworkTasksCount--;
        }
        while (this.currentArtworkTasksCount < 4 && !this.artworkTasks.isEmpty()) {
            try {
                this.artworkTasks.poll().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                this.currentArtworkTasksCount++;
            } catch (Throwable th) {
                runArtworkTasks(false);
            }
        }
    }

    public boolean isLoadingHttpFile(String url) {
        return this.httpFileLoadTasksByKeys.containsKey(url);
    }

    public static String getHttpFileName(String url) {
        return Utilities.MD5(url);
    }

    public static File getHttpFilePath(String url, String defaultExt) {
        String ext = getHttpUrlExtension(url, defaultExt);
        File directory = FileLoader.getDirectory(4);
        return new File(directory, Utilities.MD5(url) + "." + ext);
    }

    public void loadHttpFile(String url, String defaultExt, int currentAccount) {
        if (url != null && url.length() != 0 && !this.httpFileLoadTasksByKeys.containsKey(url)) {
            String ext = getHttpUrlExtension(url, defaultExt);
            File directory = FileLoader.getDirectory(4);
            File file = new File(directory, Utilities.MD5(url) + "_temp." + ext);
            file.delete();
            HttpFileTask task = new HttpFileTask(url, file, ext, currentAccount);
            this.httpFileLoadTasks.add(task);
            this.httpFileLoadTasksByKeys.put(url, task);
            runHttpFileLoadTasks((HttpFileTask) null, 0);
        }
    }

    public void cancelLoadHttpFile(String url) {
        HttpFileTask task = this.httpFileLoadTasksByKeys.get(url);
        if (task != null) {
            task.cancel(true);
            this.httpFileLoadTasksByKeys.remove(url);
            this.httpFileLoadTasks.remove(task);
        }
        Runnable runnable = this.retryHttpsTasks.get(url);
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        runHttpFileLoadTasks((HttpFileTask) null, 0);
    }

    /* access modifiers changed from: private */
    public void runHttpFileLoadTasks(HttpFileTask oldTask, int reason) {
        AndroidUtilities.runOnUIThread(new Runnable(oldTask, reason) {
            private final /* synthetic */ ImageLoader.HttpFileTask f$1;
            private final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ImageLoader.this.lambda$runHttpFileLoadTasks$11$ImageLoader(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$runHttpFileLoadTasks$11$ImageLoader(HttpFileTask oldTask, int reason) {
        if (oldTask != null) {
            this.currentHttpFileLoadTasksCount--;
        }
        if (oldTask != null) {
            if (reason == 1) {
                if (oldTask.canRetry) {
                    Runnable runnable = new Runnable(new HttpFileTask(oldTask.url, oldTask.tempFile, oldTask.ext, oldTask.currentAccount)) {
                        private final /* synthetic */ ImageLoader.HttpFileTask f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            ImageLoader.this.lambda$null$10$ImageLoader(this.f$1);
                        }
                    };
                    this.retryHttpsTasks.put(oldTask.url, runnable);
                    AndroidUtilities.runOnUIThread(runnable, 1000);
                } else {
                    this.httpFileLoadTasksByKeys.remove(oldTask.url);
                    NotificationCenter.getInstance(oldTask.currentAccount).postNotificationName(NotificationCenter.httpFileDidFailedLoad, oldTask.url, 0);
                }
            } else if (reason == 2) {
                this.httpFileLoadTasksByKeys.remove(oldTask.url);
                File file = new File(FileLoader.getDirectory(4), Utilities.MD5(oldTask.url) + "." + oldTask.ext);
                NotificationCenter.getInstance(oldTask.currentAccount).postNotificationName(NotificationCenter.httpFileDidLoad, oldTask.url, oldTask.tempFile.renameTo(file) ? file.toString() : oldTask.tempFile.toString());
            }
        }
        while (this.currentHttpFileLoadTasksCount < 2 && !this.httpFileLoadTasks.isEmpty()) {
            this.httpFileLoadTasks.poll().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
            this.currentHttpFileLoadTasksCount++;
        }
    }

    public /* synthetic */ void lambda$null$10$ImageLoader(HttpFileTask newTask) {
        this.httpFileLoadTasks.add(newTask);
        runHttpFileLoadTasks((HttpFileTask) null, 0);
    }

    public static boolean shouldSendImageAsDocument(String path, Uri uri) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        if (!(path != null || uri == null || uri.getScheme() == null)) {
            if (uri.getScheme().contains("file")) {
                path = uri.getPath();
            } else {
                try {
                    path = AndroidUtilities.getPath(uri);
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
        }
        if (path != null) {
            BitmapFactory.decodeFile(path, bmOptions);
        } else if (uri != null) {
            try {
                InputStream inputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
                BitmapFactory.decodeStream(inputStream, (Rect) null, bmOptions);
                inputStream.close();
            } catch (Throwable e2) {
                FileLog.e(e2);
                return false;
            }
        }
        float photoW = (float) bmOptions.outWidth;
        float photoH = (float) bmOptions.outHeight;
        if (photoW / photoH > 10.0f || photoH / photoW > 10.0f) {
            return true;
        }
        return false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x003a  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x003e  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x006f  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0074  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x007e  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0082  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x008f  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00a1  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00a7  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00aa  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00b6 A[SYNTHETIC, Splitter:B:51:0x00b6] */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x00e9 A[SYNTHETIC, Splitter:B:65:0x00e9] */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0152  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.graphics.Bitmap loadBitmap(java.lang.String r19, android.net.Uri r20, float r21, float r22, boolean r23) {
        /*
            r1 = r20
            android.graphics.BitmapFactory$Options r0 = new android.graphics.BitmapFactory$Options
            r0.<init>()
            r2 = r0
            r3 = 1
            r2.inJustDecodeBounds = r3
            r4 = 0
            if (r19 != 0) goto L_0x0035
            if (r1 == 0) goto L_0x0035
            java.lang.String r0 = r20.getScheme()
            if (r0 == 0) goto L_0x0035
            r5 = 0
            java.lang.String r0 = r20.getScheme()
            java.lang.String r6 = "file"
            boolean r0 = r0.contains(r6)
            if (r0 == 0) goto L_0x0029
            java.lang.String r0 = r20.getPath()
            r5 = r0
            goto L_0x0037
        L_0x0029:
            java.lang.String r0 = im.bclpbkiauv.messenger.AndroidUtilities.getPath(r20)     // Catch:{ all -> 0x002f }
            r5 = r0
            goto L_0x0037
        L_0x002f:
            r0 = move-exception
            r6 = r0
            r0 = r6
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0035:
            r5 = r19
        L_0x0037:
            r6 = 0
            if (r5 == 0) goto L_0x003e
            android.graphics.BitmapFactory.decodeFile(r5, r2)
            goto L_0x0063
        L_0x003e:
            if (r1 == 0) goto L_0x0063
            r7 = 0
            android.content.Context r0 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x005e }
            android.content.ContentResolver r0 = r0.getContentResolver()     // Catch:{ all -> 0x005e }
            java.io.InputStream r0 = r0.openInputStream(r1)     // Catch:{ all -> 0x005e }
            r4 = r0
            android.graphics.BitmapFactory.decodeStream(r4, r6, r2)     // Catch:{ all -> 0x005e }
            r4.close()     // Catch:{ all -> 0x005e }
            android.content.Context r0 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x005e }
            android.content.ContentResolver r0 = r0.getContentResolver()     // Catch:{ all -> 0x005e }
            java.io.InputStream r0 = r0.openInputStream(r1)     // Catch:{ all -> 0x005e }
            r4 = r0
            goto L_0x0063
        L_0x005e:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            return r6
        L_0x0063:
            int r0 = r2.outWidth
            float r7 = (float) r0
            int r0 = r2.outHeight
            float r8 = (float) r0
            float r0 = r7 / r21
            float r9 = r8 / r22
            if (r23 == 0) goto L_0x0074
            float r0 = java.lang.Math.max(r0, r9)
            goto L_0x0078
        L_0x0074:
            float r0 = java.lang.Math.min(r0, r9)
        L_0x0078:
            r9 = 1065353216(0x3f800000, float:1.0)
            int r9 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r9 >= 0) goto L_0x0082
            r0 = 1065353216(0x3f800000, float:1.0)
            r9 = r0
            goto L_0x0083
        L_0x0082:
            r9 = r0
        L_0x0083:
            r0 = 0
            r2.inJustDecodeBounds = r0
            int r10 = (int) r9
            r2.inSampleSize = r10
            int r10 = r2.inSampleSize
            int r10 = r10 % 2
            if (r10 == 0) goto L_0x009b
            r10 = 1
        L_0x0090:
            int r11 = r10 * 2
            int r12 = r2.inSampleSize
            if (r11 >= r12) goto L_0x0099
            int r10 = r10 * 2
            goto L_0x0090
        L_0x0099:
            r2.inSampleSize = r10
        L_0x009b:
            int r10 = android.os.Build.VERSION.SDK_INT
            r11 = 21
            if (r10 >= r11) goto L_0x00a2
            r0 = 1
        L_0x00a2:
            r2.inPurgeable = r0
            r0 = 0
            if (r5 == 0) goto L_0x00aa
            r0 = r5
            r10 = r0
            goto L_0x00b3
        L_0x00aa:
            if (r1 == 0) goto L_0x00b2
            java.lang.String r0 = im.bclpbkiauv.messenger.AndroidUtilities.getPath(r20)
            r10 = r0
            goto L_0x00b3
        L_0x00b2:
            r10 = r0
        L_0x00b3:
            r11 = 0
            if (r10 == 0) goto L_0x00e6
            androidx.exifinterface.media.ExifInterface r0 = new androidx.exifinterface.media.ExifInterface     // Catch:{ all -> 0x00e5 }
            r0.<init>((java.lang.String) r10)     // Catch:{ all -> 0x00e5 }
            java.lang.String r12 = "Orientation"
            int r3 = r0.getAttributeInt(r12, r3)     // Catch:{ all -> 0x00e5 }
            android.graphics.Matrix r12 = new android.graphics.Matrix     // Catch:{ all -> 0x00e5 }
            r12.<init>()     // Catch:{ all -> 0x00e5 }
            r11 = r12
            r12 = 3
            if (r3 == r12) goto L_0x00de
            r12 = 6
            if (r3 == r12) goto L_0x00d8
            r12 = 8
            if (r3 == r12) goto L_0x00d2
            goto L_0x00e4
        L_0x00d2:
            r12 = 1132920832(0x43870000, float:270.0)
            r11.postRotate(r12)     // Catch:{ all -> 0x00e5 }
            goto L_0x00e4
        L_0x00d8:
            r12 = 1119092736(0x42b40000, float:90.0)
            r11.postRotate(r12)     // Catch:{ all -> 0x00e5 }
            goto L_0x00e4
        L_0x00de:
            r12 = 1127481344(0x43340000, float:180.0)
            r11.postRotate(r12)     // Catch:{ all -> 0x00e5 }
        L_0x00e4:
            goto L_0x00e6
        L_0x00e5:
            r0 = move-exception
        L_0x00e6:
            r3 = 0
            if (r5 == 0) goto L_0x0152
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeFile(r5, r2)     // Catch:{ all -> 0x0112 }
            r3 = r0
            if (r3 == 0) goto L_0x0110
            boolean r0 = r2.inPurgeable     // Catch:{ all -> 0x0112 }
            if (r0 == 0) goto L_0x00f7
            im.bclpbkiauv.messenger.Utilities.pinBitmap(r3)     // Catch:{ all -> 0x0112 }
        L_0x00f7:
            r13 = 0
            r14 = 0
            int r15 = r3.getWidth()     // Catch:{ all -> 0x0112 }
            int r16 = r3.getHeight()     // Catch:{ all -> 0x0112 }
            r18 = 1
            r12 = r3
            r17 = r11
            android.graphics.Bitmap r0 = im.bclpbkiauv.messenger.Bitmaps.createBitmap(r12, r13, r14, r15, r16, r17, r18)     // Catch:{ all -> 0x0112 }
            if (r0 == r3) goto L_0x0110
            r3.recycle()     // Catch:{ all -> 0x0112 }
            r3 = r0
        L_0x0110:
            goto L_0x019b
        L_0x0112:
            r0 = move-exception
            r6 = r0
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r6)
            im.bclpbkiauv.messenger.ImageLoader r0 = getInstance()
            r0.clearMemory()
            if (r3 != 0) goto L_0x0131
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeFile(r5, r2)     // Catch:{ all -> 0x012f }
            r3 = r0
            if (r3 == 0) goto L_0x0131
            boolean r0 = r2.inPurgeable     // Catch:{ all -> 0x012f }
            if (r0 == 0) goto L_0x0131
            im.bclpbkiauv.messenger.Utilities.pinBitmap(r3)     // Catch:{ all -> 0x012f }
            goto L_0x0131
        L_0x012f:
            r0 = move-exception
            goto L_0x014d
        L_0x0131:
            if (r3 == 0) goto L_0x0151
            r13 = 0
            r14 = 0
            int r15 = r3.getWidth()     // Catch:{ all -> 0x012f }
            int r16 = r3.getHeight()     // Catch:{ all -> 0x012f }
            r18 = 1
            r12 = r3
            r17 = r11
            android.graphics.Bitmap r0 = im.bclpbkiauv.messenger.Bitmaps.createBitmap(r12, r13, r14, r15, r16, r17, r18)     // Catch:{ all -> 0x012f }
            if (r0 == r3) goto L_0x0151
            r3.recycle()     // Catch:{ all -> 0x012f }
            r3 = r0
            goto L_0x0151
        L_0x014d:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0110
        L_0x0151:
            goto L_0x0110
        L_0x0152:
            if (r1 == 0) goto L_0x019b
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeStream(r4, r6, r2)     // Catch:{ all -> 0x0186 }
            r3 = r0
            if (r3 == 0) goto L_0x017b
            boolean r0 = r2.inPurgeable     // Catch:{ all -> 0x0186 }
            if (r0 == 0) goto L_0x0162
            im.bclpbkiauv.messenger.Utilities.pinBitmap(r3)     // Catch:{ all -> 0x0186 }
        L_0x0162:
            r13 = 0
            r14 = 0
            int r15 = r3.getWidth()     // Catch:{ all -> 0x0186 }
            int r16 = r3.getHeight()     // Catch:{ all -> 0x0186 }
            r18 = 1
            r12 = r3
            r17 = r11
            android.graphics.Bitmap r0 = im.bclpbkiauv.messenger.Bitmaps.createBitmap(r12, r13, r14, r15, r16, r17, r18)     // Catch:{ all -> 0x0186 }
            if (r0 == r3) goto L_0x017b
            r3.recycle()     // Catch:{ all -> 0x0186 }
            r3 = r0
        L_0x017b:
            r4.close()     // Catch:{ all -> 0x017f }
        L_0x017e:
            goto L_0x019b
        L_0x017f:
            r0 = move-exception
            r6 = r0
            r0 = r6
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x019b
        L_0x0186:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x018e }
            r4.close()     // Catch:{ all -> 0x017f }
            goto L_0x017e
        L_0x018e:
            r0 = move-exception
            r6 = r0
            r4.close()     // Catch:{ all -> 0x0194 }
            goto L_0x019a
        L_0x0194:
            r0 = move-exception
            r12 = r0
            r0 = r12
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x019a:
            throw r6
        L_0x019b:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.ImageLoader.loadBitmap(java.lang.String, android.net.Uri, float, float, boolean):android.graphics.Bitmap");
    }

    public static void fillPhotoSizeWithBytes(TLRPC.PhotoSize photoSize) {
        if (photoSize == null) {
            return;
        }
        if (photoSize.bytes == null || photoSize.bytes.length == 0) {
            try {
                RandomAccessFile f = new RandomAccessFile(FileLoader.getPathToAttach(photoSize, true), "r");
                if (((int) f.length()) < 20000) {
                    photoSize.bytes = new byte[((int) f.length())];
                    f.readFully(photoSize.bytes, 0, photoSize.bytes.length);
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
    }

    private static TLRPC.PhotoSize scaleAndSaveImageInternal(TLRPC.PhotoSize photoSize, Bitmap bitmap, int w, int h, float photoW, float photoH, float scaleFactor, int quality, boolean cache, boolean scaleAnyway, boolean isPng) throws Exception {
        Bitmap scaledBitmap;
        TLRPC.TL_fileLocationToBeDeprecated location;
        TLRPC.PhotoSize photoSize2 = photoSize;
        Bitmap bitmap2 = bitmap;
        int i = quality;
        if (scaleFactor > 1.0f || scaleAnyway) {
            scaledBitmap = Bitmaps.createScaledBitmap(bitmap2, w, h, true);
        } else {
            int i2 = h;
            scaledBitmap = bitmap;
            int i3 = w;
        }
        int i4 = 0;
        if (photoSize2 == null) {
        }
        if (photoSize2 == null || !(photoSize2.location instanceof TLRPC.TL_fileLocationToBeDeprecated)) {
            location = new TLRPC.TL_fileLocationToBeDeprecated();
            location.volume_id = -2147483648L;
            location.dc_id = Integer.MIN_VALUE;
            location.local_id = SharedConfig.getLastLocalId();
            location.file_reference = new byte[0];
            photoSize2 = new TLRPC.TL_photoSize();
            photoSize2.location = location;
            photoSize2.w = scaledBitmap.getWidth();
            photoSize2.h = scaledBitmap.getHeight();
            if (photoSize2.w <= 100 && photoSize2.h <= 100) {
                photoSize2.type = "s";
            } else if (photoSize2.w <= 320 && photoSize2.h <= 320) {
                photoSize2.type = "m";
            } else if (photoSize2.w <= 800 && photoSize2.h <= 800) {
                photoSize2.type = "x";
            } else if (photoSize2.w > 1280 || photoSize2.h > 1280) {
                photoSize2.type = "w";
            } else {
                photoSize2.type = "y";
            }
        } else {
            location = (TLRPC.TL_fileLocationToBeDeprecated) photoSize2.location;
        }
        String fileName = location.volume_id + "_" + location.local_id + ".jpg";
        if (location.volume_id == -2147483648L) {
            i4 = 4;
        }
        FileOutputStream stream = new FileOutputStream(new File(FileLoader.getDirectory(i4), fileName));
        if (isPng) {
            scaledBitmap.compress(Bitmap.CompressFormat.PNG, i, stream);
        } else {
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, i, stream);
        }
        if (cache) {
            ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
            if (isPng) {
                scaledBitmap.compress(Bitmap.CompressFormat.PNG, i, stream2);
            } else {
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, i, stream2);
            }
            photoSize2.bytes = stream2.toByteArray();
            photoSize2.size = photoSize2.bytes.length;
            stream2.close();
        } else {
            photoSize2.size = (int) stream.getChannel().size();
        }
        stream.close();
        if (scaledBitmap != bitmap2) {
            scaledBitmap.recycle();
        }
        return photoSize2;
    }

    public static TLRPC.PhotoSize SaveImageWithOriginalInternal(TLRPC.PhotoSize photoSize, String strPath, boolean cache) throws Exception {
        TLRPC.TL_fileLocationToBeDeprecated location;
        int i = 0;
        if (photoSize != null) {
        }
        if (photoSize == null || !(photoSize.location instanceof TLRPC.TL_fileLocationToBeDeprecated)) {
            location = new TLRPC.TL_fileLocationToBeDeprecated();
            location.volume_id = -2147483648L;
            location.dc_id = Integer.MIN_VALUE;
            location.local_id = SharedConfig.getLastLocalId();
            location.file_reference = new byte[0];
            photoSize = new TLRPC.TL_photoSize();
            photoSize.location = location;
            BitmapFactory.Options options = new BitmapFactory.Options();
            BitmapFactory.decodeFile(strPath, options);
            photoSize.w = options.outWidth;
            photoSize.h = options.outHeight;
            if (photoSize.w <= 100 && photoSize.h <= 100) {
                photoSize.type = "s";
            } else if (photoSize.w <= 320 && photoSize.h <= 320) {
                photoSize.type = "m";
            } else if (photoSize.w <= 800 && photoSize.h <= 800) {
                photoSize.type = "x";
            } else if (photoSize.w > 1280 || photoSize.h > 1280) {
                photoSize.type = "w";
            } else {
                photoSize.type = "y";
            }
        } else {
            location = (TLRPC.TL_fileLocationToBeDeprecated) photoSize.location;
        }
        String fileName = location.volume_id + "_" + location.local_id + ".jpg";
        if (location.volume_id == -2147483648L) {
            i = 4;
        }
        FileInputStream fileInputStream = new FileInputStream(strPath);
        AndroidUtilities.copyFile((InputStream) fileInputStream, new File(FileLoader.getDirectory(i), fileName));
        if (cache) {
            ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
            photoSize.bytes = stream2.toByteArray();
            photoSize.size = photoSize.bytes.length;
            stream2.close();
        } else {
            photoSize.size = (int) fileInputStream.getChannel().size();
        }
        fileInputStream.close();
        return photoSize;
    }

    public static TLRPC.PhotoSize scaleAndSaveImage(Bitmap bitmap, float maxWidth, float maxHeight, int quality, boolean cache) {
        return scaleAndSaveImage((TLRPC.PhotoSize) null, bitmap, maxWidth, maxHeight, quality, cache, 0, 0, false);
    }

    public static TLRPC.PhotoSize scaleAndSaveImage(TLRPC.PhotoSize photoSize, Bitmap bitmap, float maxWidth, float maxHeight, int quality, boolean cache) {
        return scaleAndSaveImage(photoSize, bitmap, maxWidth, maxHeight, quality, cache, 0, 0, false);
    }

    public static TLRPC.PhotoSize scaleAndSaveImage(Bitmap bitmap, float maxWidth, float maxHeight, int quality, boolean cache, int minWidth, int minHeight) {
        return scaleAndSaveImage((TLRPC.PhotoSize) null, bitmap, maxWidth, maxHeight, quality, cache, minWidth, minHeight, false);
    }

    public static TLRPC.PhotoSize scaleAndSaveImage(Bitmap bitmap, float maxWidth, float maxHeight, int quality, boolean cache, boolean isPng) {
        return scaleAndSaveImage((TLRPC.PhotoSize) null, bitmap, maxWidth, maxHeight, quality, cache, 0, 0, isPng);
    }

    public static TLRPC.PhotoSize scaleAndSaveImage(Bitmap bitmap, float maxWidth, float maxHeight, int quality, boolean cache, int minWidth, int minHeight, boolean isPng) {
        return scaleAndSaveImage((TLRPC.PhotoSize) null, bitmap, maxWidth, maxHeight, quality, cache, minWidth, minHeight, isPng);
    }

    public static TLRPC.PhotoSize scaleAndSaveImage(TLRPC.PhotoSize photoSize, Bitmap bitmap, float maxWidth, float maxHeight, int quality, boolean cache, int minWidth, int minHeight, boolean isPng) {
        float scaleFactor;
        boolean scaleAnyway;
        float scaleFactor2;
        int i = minWidth;
        int i2 = minHeight;
        if (bitmap == null) {
            return null;
        }
        float photoW = (float) bitmap.getWidth();
        float photoH = (float) bitmap.getHeight();
        if (photoW == 0.0f) {
        } else if (photoH == 0.0f) {
            float f = photoH;
        } else {
            float scaleFactor3 = Math.max(photoW / maxWidth, photoH / maxHeight);
            if (i == 0 || i2 == 0 || (photoW >= ((float) i) && photoH >= ((float) i2))) {
                scaleAnyway = false;
                scaleFactor = scaleFactor3;
            } else {
                if (photoW < ((float) i) && photoH > ((float) i2)) {
                    scaleFactor2 = photoW / ((float) i);
                } else if (photoW <= ((float) i) || photoH >= ((float) i2)) {
                    scaleFactor2 = Math.max(photoW / ((float) i), photoH / ((float) i2));
                } else {
                    scaleFactor2 = photoH / ((float) i2);
                }
                scaleAnyway = true;
                scaleFactor = scaleFactor2;
            }
            int w = (int) (photoW / scaleFactor);
            int h = (int) (photoH / scaleFactor);
            if (h == 0) {
                int i3 = w;
                float f2 = photoH;
            } else if (w == 0) {
                int i4 = h;
                int i5 = w;
                float f3 = photoH;
            } else {
                int h2 = h;
                int w2 = w;
                float photoH2 = photoH;
                try {
                    return scaleAndSaveImageInternal(photoSize, bitmap, w, h, photoW, photoH, scaleFactor, quality, cache, scaleAnyway, isPng);
                } catch (Throwable th) {
                    FileLog.e(th);
                    return null;
                }
            }
            return null;
        }
        return null;
    }

    public static String getHttpUrlExtension(String url, String defaultExt) {
        String ext = null;
        String last = Uri.parse(url).getLastPathSegment();
        if (!TextUtils.isEmpty(last) && last.length() > 1) {
            url = last;
        }
        int idx = url.lastIndexOf(46);
        if (idx != -1) {
            ext = url.substring(idx + 1);
        }
        if (ext == null || ext.length() == 0 || ext.length() > 4) {
            return defaultExt;
        }
        return ext;
    }

    public static void saveMessageThumbs(TLRPC.Message message) {
        TLRPC.PhotoSize photoSize;
        boolean isEncrypted;
        File file;
        TLRPC.Message message2 = message;
        TLRPC.PhotoSize photoSize2 = null;
        if (message2.media instanceof TLRPC.TL_messageMediaPhoto) {
            int a = 0;
            int count = message2.media.photo.sizes.size();
            while (true) {
                if (a >= count) {
                    break;
                }
                TLRPC.PhotoSize size = message2.media.photo.sizes.get(a);
                if (size instanceof TLRPC.TL_photoCachedSize) {
                    photoSize2 = size;
                    break;
                }
                a++;
            }
            photoSize = photoSize2;
        } else if (message2.media instanceof TLRPC.TL_messageMediaDocument) {
            int a2 = 0;
            int count2 = message2.media.document.thumbs.size();
            while (true) {
                if (a2 >= count2) {
                    break;
                }
                TLRPC.PhotoSize size2 = message2.media.document.thumbs.get(a2);
                if (size2 instanceof TLRPC.TL_photoCachedSize) {
                    photoSize2 = size2;
                    break;
                }
                a2++;
            }
            photoSize = photoSize2;
        } else {
            if ((message2.media instanceof TLRPC.TL_messageMediaWebPage) && message2.media.webpage.photo != null) {
                int a3 = 0;
                int count3 = message2.media.webpage.photo.sizes.size();
                while (true) {
                    if (a3 >= count3) {
                        break;
                    }
                    TLRPC.PhotoSize size3 = message2.media.webpage.photo.sizes.get(a3);
                    if (size3 instanceof TLRPC.TL_photoCachedSize) {
                        photoSize = size3;
                        break;
                    }
                    a3++;
                }
            }
            photoSize = null;
        }
        if (photoSize != null && photoSize.bytes != null && photoSize.bytes.length != 0) {
            if (photoSize.location == null || (photoSize.location instanceof TLRPC.TL_fileLocationUnavailable)) {
                photoSize.location = new TLRPC.TL_fileLocationToBeDeprecated();
                photoSize.location.volume_id = -2147483648L;
                photoSize.location.local_id = SharedConfig.getLastLocalId();
            }
            File file2 = FileLoader.getPathToAttach(photoSize, true);
            if (MessageObject.shouldEncryptPhotoOrVideo(message)) {
                isEncrypted = true;
                file = new File(file2.getAbsolutePath() + ".enc");
            } else {
                isEncrypted = false;
                file = file2;
            }
            if (!file.exists()) {
                if (isEncrypted) {
                    try {
                        RandomAccessFile keyFile = new RandomAccessFile(new File(FileLoader.getInternalCacheDir(), file.getName() + ".key"), "rws");
                        long len = keyFile.length();
                        byte[] encryptKey = new byte[32];
                        byte[] encryptIv = new byte[16];
                        if (len <= 0 || len % 48 != 0) {
                            Utilities.random.nextBytes(encryptKey);
                            Utilities.random.nextBytes(encryptIv);
                            keyFile.write(encryptKey);
                            keyFile.write(encryptIv);
                        } else {
                            keyFile.read(encryptKey, 0, 32);
                            keyFile.read(encryptIv, 0, 16);
                        }
                        keyFile.close();
                        byte[] bArr = encryptIv;
                        Utilities.aesCtrDecryptionByteArray(photoSize.bytes, encryptKey, encryptIv, 0, photoSize.bytes.length, 0);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
                RandomAccessFile writeFile = new RandomAccessFile(file, "rws");
                writeFile.write(photoSize.bytes);
                writeFile.close();
            }
            TLRPC.TL_photoSize newPhotoSize = new TLRPC.TL_photoSize();
            newPhotoSize.w = photoSize.w;
            newPhotoSize.h = photoSize.h;
            newPhotoSize.location = photoSize.location;
            newPhotoSize.size = photoSize.size;
            newPhotoSize.type = photoSize.type;
            if (message2.media instanceof TLRPC.TL_messageMediaPhoto) {
                int count4 = message2.media.photo.sizes.size();
                for (int a4 = 0; a4 < count4; a4++) {
                    if (message2.media.photo.sizes.get(a4) instanceof TLRPC.TL_photoCachedSize) {
                        message2.media.photo.sizes.set(a4, newPhotoSize);
                        return;
                    }
                }
            } else if (message2.media instanceof TLRPC.TL_messageMediaDocument) {
                int count5 = message2.media.document.thumbs.size();
                for (int a5 = 0; a5 < count5; a5++) {
                    if (message2.media.document.thumbs.get(a5) instanceof TLRPC.TL_photoCachedSize) {
                        message2.media.document.thumbs.set(a5, newPhotoSize);
                        return;
                    }
                }
            } else if (message2.media instanceof TLRPC.TL_messageMediaWebPage) {
                int count6 = message2.media.webpage.photo.sizes.size();
                for (int a6 = 0; a6 < count6; a6++) {
                    if (message2.media.webpage.photo.sizes.get(a6) instanceof TLRPC.TL_photoCachedSize) {
                        message2.media.webpage.photo.sizes.set(a6, newPhotoSize);
                        return;
                    }
                }
            }
        }
    }

    public static void saveMessagesThumbs(ArrayList<TLRPC.Message> messages) {
        if (messages != null && !messages.isEmpty()) {
            for (int a = 0; a < messages.size(); a++) {
                saveMessageThumbs(messages.get(a));
            }
        }
    }
}
