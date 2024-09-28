package com.blankj.utilcode.util;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import java.util.ArrayList;
import java.util.List;

public final class UiMessageUtils implements Handler.Callback {
    private static final boolean DEBUG = isAppDebug();
    private static final String TAG = "UiMessageUtils";
    private final List<UiMessageCallback> mDefensiveCopyList;
    private final Handler mHandler;
    private final SparseArray<List<UiMessageCallback>> mListenersSpecific;
    private final List<UiMessageCallback> mListenersUniversal;
    private final UiMessage mMessage;

    public interface UiMessageCallback {
        void handleMessage(UiMessage uiMessage);
    }

    public static UiMessageUtils getInstance() {
        return LazyHolder.INSTANCE;
    }

    private UiMessageUtils() {
        this.mHandler = new Handler(Looper.getMainLooper(), this);
        this.mMessage = new UiMessage((Message) null);
        this.mListenersSpecific = new SparseArray<>();
        this.mListenersUniversal = new ArrayList();
        this.mDefensiveCopyList = new ArrayList();
    }

    public final void send(int id) {
        this.mHandler.sendEmptyMessage(id);
    }

    public final void send(int id, Object obj) {
        if (obj != null) {
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(id, obj));
            return;
        }
        throw new NullPointerException("Argument 'obj' of type Object (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public void addListener(int id, UiMessageCallback listener) {
        if (listener != null) {
            synchronized (this.mListenersSpecific) {
                List<UiMessageCallback> idListeners = this.mListenersSpecific.get(id);
                if (idListeners == null) {
                    idListeners = new ArrayList<>();
                    this.mListenersSpecific.put(id, idListeners);
                }
                if (!idListeners.contains(listener)) {
                    idListeners.add(listener);
                }
            }
            return;
        }
        throw new NullPointerException("Argument 'listener' of type UiMessageCallback (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public void addListener(UiMessageCallback listener) {
        if (listener != null) {
            synchronized (this.mListenersUniversal) {
                if (!this.mListenersUniversal.contains(listener)) {
                    this.mListenersUniversal.add(listener);
                } else if (DEBUG) {
                    Log.w(TAG, "Listener is already added. " + listener.toString());
                }
            }
            return;
        }
        throw new NullPointerException("Argument 'listener' of type UiMessageCallback (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public void removeListener(UiMessageCallback listener) {
        if (listener != null) {
            synchronized (this.mListenersUniversal) {
                if (DEBUG && !this.mListenersUniversal.contains(listener)) {
                    Log.w(TAG, "Trying to remove a listener that is not registered. " + listener.toString());
                }
                this.mListenersUniversal.remove(listener);
            }
            return;
        }
        throw new NullPointerException("Argument 'listener' of type UiMessageCallback (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public void removeListeners(int id) {
        List<UiMessageCallback> callbacks;
        if (DEBUG && ((callbacks = this.mListenersSpecific.get(id)) == null || callbacks.size() == 0)) {
            Log.w(TAG, "Trying to remove specific listeners that are not registered. ID " + id);
        }
        synchronized (this.mListenersSpecific) {
            this.mListenersSpecific.delete(id);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0066, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void removeListener(int r6, com.blankj.utilcode.util.UiMessageUtils.UiMessageCallback r7) {
        /*
            r5 = this;
            if (r7 == 0) goto L_0x006a
            android.util.SparseArray<java.util.List<com.blankj.utilcode.util.UiMessageUtils$UiMessageCallback>> r0 = r5.mListenersSpecific
            monitor-enter(r0)
            android.util.SparseArray<java.util.List<com.blankj.utilcode.util.UiMessageUtils$UiMessageCallback>> r1 = r5.mListenersSpecific     // Catch:{ all -> 0x0067 }
            java.lang.Object r1 = r1.get(r6)     // Catch:{ all -> 0x0067 }
            java.util.List r1 = (java.util.List) r1     // Catch:{ all -> 0x0067 }
            if (r1 == 0) goto L_0x0043
            boolean r2 = r1.isEmpty()     // Catch:{ all -> 0x0067 }
            if (r2 != 0) goto L_0x0043
            boolean r2 = DEBUG     // Catch:{ all -> 0x0067 }
            if (r2 == 0) goto L_0x003f
            boolean r2 = r1.contains(r7)     // Catch:{ all -> 0x0067 }
            if (r2 != 0) goto L_0x003f
            java.lang.String r2 = "UiMessageUtils"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0067 }
            r3.<init>()     // Catch:{ all -> 0x0067 }
            java.lang.String r4 = "Trying to remove specific listener that is not registered. ID "
            r3.append(r4)     // Catch:{ all -> 0x0067 }
            r3.append(r6)     // Catch:{ all -> 0x0067 }
            java.lang.String r4 = ", "
            r3.append(r4)     // Catch:{ all -> 0x0067 }
            r3.append(r7)     // Catch:{ all -> 0x0067 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0067 }
            android.util.Log.w(r2, r3)     // Catch:{ all -> 0x0067 }
            monitor-exit(r0)     // Catch:{ all -> 0x0067 }
            return
        L_0x003f:
            r1.remove(r7)     // Catch:{ all -> 0x0067 }
            goto L_0x0065
        L_0x0043:
            boolean r2 = DEBUG     // Catch:{ all -> 0x0067 }
            if (r2 == 0) goto L_0x0065
            java.lang.String r2 = "UiMessageUtils"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0067 }
            r3.<init>()     // Catch:{ all -> 0x0067 }
            java.lang.String r4 = "Trying to remove specific listener that is not registered. ID "
            r3.append(r4)     // Catch:{ all -> 0x0067 }
            r3.append(r6)     // Catch:{ all -> 0x0067 }
            java.lang.String r4 = ", "
            r3.append(r4)     // Catch:{ all -> 0x0067 }
            r3.append(r7)     // Catch:{ all -> 0x0067 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0067 }
            android.util.Log.w(r2, r3)     // Catch:{ all -> 0x0067 }
        L_0x0065:
            monitor-exit(r0)     // Catch:{ all -> 0x0067 }
            return
        L_0x0067:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0067 }
            throw r1
        L_0x006a:
            java.lang.NullPointerException r0 = new java.lang.NullPointerException
            java.lang.String r1 = "Argument 'listener' of type UiMessageCallback (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it"
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.blankj.utilcode.util.UiMessageUtils.removeListener(int, com.blankj.utilcode.util.UiMessageUtils$UiMessageCallback):void");
    }

    public boolean handleMessage(Message msg) {
        this.mMessage.setMessage(msg);
        if (DEBUG) {
            logMessageHandling(this.mMessage);
        }
        synchronized (this.mListenersSpecific) {
            List<UiMessageCallback> idListeners = this.mListenersSpecific.get(msg.what);
            if (idListeners != null) {
                if (idListeners.size() == 0) {
                    this.mListenersSpecific.remove(msg.what);
                } else {
                    this.mDefensiveCopyList.addAll(idListeners);
                    for (UiMessageCallback callback : this.mDefensiveCopyList) {
                        callback.handleMessage(this.mMessage);
                    }
                    this.mDefensiveCopyList.clear();
                }
            }
        }
        synchronized (this.mListenersUniversal) {
            if (this.mListenersUniversal.size() > 0) {
                this.mDefensiveCopyList.addAll(this.mListenersUniversal);
                for (UiMessageCallback callback2 : this.mDefensiveCopyList) {
                    callback2.handleMessage(this.mMessage);
                }
                this.mDefensiveCopyList.clear();
            }
        }
        this.mMessage.setMessage((Message) null);
        return true;
    }

    private void logMessageHandling(UiMessage msg) {
        if (msg != null) {
            List<UiMessageCallback> idListeners = this.mListenersSpecific.get(msg.getId());
            if ((idListeners == null || idListeners.size() == 0) && this.mListenersUniversal.size() == 0) {
                Log.w(TAG, "Delivering FAILED for message ID " + msg.getId() + ". No listeners. " + msg.toString());
                return;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Delivering message ID ");
            stringBuilder.append(msg.getId());
            stringBuilder.append(", Specific listeners: ");
            if (idListeners == null || idListeners.size() == 0) {
                stringBuilder.append(0);
            } else {
                stringBuilder.append(idListeners.size());
                stringBuilder.append(" [");
                for (int i = 0; i < idListeners.size(); i++) {
                    stringBuilder.append(idListeners.get(i).getClass().getSimpleName());
                    if (i < idListeners.size() - 1) {
                        stringBuilder.append(",");
                    }
                }
                stringBuilder.append("]");
            }
            stringBuilder.append(", Universal listeners: ");
            synchronized (this.mListenersUniversal) {
                if (this.mListenersUniversal.size() == 0) {
                    stringBuilder.append(0);
                } else {
                    stringBuilder.append(this.mListenersUniversal.size());
                    stringBuilder.append(" [");
                    for (int i2 = 0; i2 < this.mListenersUniversal.size(); i2++) {
                        stringBuilder.append(this.mListenersUniversal.get(i2).getClass().getSimpleName());
                        if (i2 < this.mListenersUniversal.size() - 1) {
                            stringBuilder.append(",");
                        }
                    }
                    stringBuilder.append("], Message: ");
                }
            }
            stringBuilder.append(msg.toString());
            Log.v(TAG, stringBuilder.toString());
            return;
        }
        throw new NullPointerException("Argument 'msg' of type UiMessage (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    private static boolean isAppDebug() {
        try {
            ApplicationInfo ai = Utils.getApp().getPackageManager().getApplicationInfo(Utils.getApp().getPackageName(), 0);
            if (ai == null || (ai.flags & 2) == 0) {
                return false;
            }
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static final class UiMessage {
        private Message mMessage;

        private UiMessage(Message message) {
            this.mMessage = message;
        }

        /* access modifiers changed from: private */
        public void setMessage(Message message) {
            this.mMessage = message;
        }

        public int getId() {
            isUiThread();
            return this.mMessage.what;
        }

        public Object getObject() {
            isUiThread();
            return this.mMessage.obj;
        }

        private void isUiThread() {
            if (this.mMessage == null) {
                throw new IllegalStateException("You can't use LocalMessage instance from a non-UI thread. Extract the data from LocalMessage and don't hold a reference to it outside of handleMessage()");
            }
        }

        public String toString() {
            isUiThread();
            StringBuilder b = new StringBuilder();
            b.append("{ id=");
            b.append(getId());
            if (getObject() != null) {
                b.append(" obj=");
                b.append(getObject());
            }
            b.append(" }");
            return b.toString();
        }
    }

    private static final class LazyHolder {
        /* access modifiers changed from: private */
        public static final UiMessageUtils INSTANCE = new UiMessageUtils();

        private LazyHolder() {
        }
    }
}
