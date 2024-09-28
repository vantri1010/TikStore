package im.bclpbkiauv.ui.hui.visualcall;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadUtils {
    private static final int CORE_POOL_SIZE;
    private static final int CPU_COUNT;
    private static final int KEEP_ALIVE_SECONDS = 30;
    private static final int MAXIMUM_POOL_SIZE = ((CPU_COUNT * 2) + 1);
    private static final BlockingQueue<Runnable> POOL_WORK_QUEUE = new LinkedBlockingQueue(128);
    private static final String TAG = ThreadUtils.class.getName();
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "ThreadUtils #" + this.mCount.getAndIncrement());
        }
    };
    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR;
    private static Handler sMainHandler = new Handler(Looper.getMainLooper());

    static {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        CPU_COUNT = availableProcessors;
        CORE_POOL_SIZE = Math.max(2, Math.min(availableProcessors - 1, 4));
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, 30, TimeUnit.SECONDS, POOL_WORK_QUEUE, THREAD_FACTORY);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        THREAD_POOL_EXECUTOR = threadPoolExecutor;
    }

    public static void runOnUiThread(Runnable runnable) {
        runOnUiThread(runnable, 0);
    }

    public static void runOnUiThread(Runnable runnable, long delayed) {
        sMainHandler.postDelayed(runnable, delayed);
    }

    public static void runOnSubThread(Runnable runnable) {
        if (THREAD_POOL_EXECUTOR.getQueue().size() == 128 || THREAD_POOL_EXECUTOR.isShutdown()) {
            Log.e(TAG, "线程池爆满警告，请查看是否开启了过多的耗时线程");
        } else {
            THREAD_POOL_EXECUTOR.execute(runnable);
        }
    }
}
