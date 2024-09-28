package io.openinstall.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class i {
    private final Executor a = Executors.newSingleThreadExecutor();

    private static class a implements Callable<SharedPreferences> {
        private final Context a;
        private final String b;
        private final b c;

        public a(Context context, String str, b bVar) {
            this.a = context;
            this.b = str;
            this.c = bVar;
        }

        /* renamed from: a */
        public SharedPreferences call() {
            SharedPreferences sharedPreferences = this.a.getSharedPreferences(this.b, 0);
            b bVar = this.c;
            if (bVar != null) {
                bVar.a(sharedPreferences);
            }
            return sharedPreferences;
        }
    }

    interface b {
        void a(SharedPreferences sharedPreferences);
    }

    public Future<SharedPreferences> a(Context context, String str, b bVar) {
        FutureTask futureTask = new FutureTask(new a(context, str, bVar));
        this.a.execute(futureTask);
        return futureTask;
    }
}
