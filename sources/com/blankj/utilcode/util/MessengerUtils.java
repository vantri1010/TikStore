package com.blankj.utilcode.util;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessengerUtils {
    private static final String KEY_STRING = "MESSENGER_UTILS";
    private static final int WHAT_SEND = 2;
    private static final int WHAT_SUBSCRIBE = 0;
    private static final int WHAT_UNSUBSCRIBE = 1;
    private static Map<String, Client> sClientMap = new HashMap();
    private static Client sLocalClient;
    /* access modifiers changed from: private */
    public static ConcurrentHashMap<String, MessageCallback> subscribers = new ConcurrentHashMap<>();

    public interface MessageCallback {
        void messageCall(Bundle bundle);
    }

    public static void register() {
        if (isMainProcess()) {
            if (isServiceRunning(ServerService.class.getName())) {
                Log.i("MessengerUtils", "Server service is running.");
                return;
            }
            Utils.getApp().startService(new Intent(Utils.getApp(), ServerService.class));
        } else if (sLocalClient == null) {
            Client client = new Client((String) null);
            if (client.bind()) {
                sLocalClient = client;
            } else {
                Log.e("MessengerUtils", "Bind service failed.");
            }
        } else {
            Log.i("MessengerUtils", "The client have been bind.");
        }
    }

    public static void unregister() {
        if (isMainProcess()) {
            if (!isServiceRunning(ServerService.class.getName())) {
                Log.i("MessengerUtils", "Server service isn't running.");
                return;
            } else {
                Utils.getApp().stopService(new Intent(Utils.getApp(), ServerService.class));
            }
        }
        Client client = sLocalClient;
        if (client != null) {
            client.unbind();
        }
    }

    public static void register(String pkgName) {
        if (sClientMap.containsKey(pkgName)) {
            Log.i("MessengerUtils", "register: client registered: " + pkgName);
            return;
        }
        Client client = new Client(pkgName);
        if (client.bind()) {
            sClientMap.put(pkgName, client);
            return;
        }
        Log.e("MessengerUtils", "register: client bind failed: " + pkgName);
    }

    public static void unregister(String pkgName) {
        if (sClientMap.containsKey(pkgName)) {
            sClientMap.get(pkgName).unbind();
            return;
        }
        Log.i("MessengerUtils", "unregister: client didn't register: " + pkgName);
    }

    public static void subscribe(String key, MessageCallback callback) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (callback != null) {
            subscribers.put(key, callback);
        } else {
            throw new NullPointerException("Argument 'callback' of type MessageCallback (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void unsubscribe(String key) {
        if (key != null) {
            subscribers.remove(key);
            return;
        }
        throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void post(String key, Bundle data) {
        if (key == null) {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (data != null) {
            data.putString(KEY_STRING, key);
            Client client = sLocalClient;
            if (client != null) {
                client.sendMsg2Server(data);
            } else {
                Intent intent = new Intent(Utils.getApp(), ServerService.class);
                intent.putExtras(data);
                Utils.getApp().startService(intent);
            }
            for (Client client2 : sClientMap.values()) {
                client2.sendMsg2Server(data);
            }
        } else {
            throw new NullPointerException("Argument 'data' of type Bundle (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    private static boolean isMainProcess() {
        return Utils.getApp().getPackageName().equals(Utils.getCurrentProcessName());
    }

    /* access modifiers changed from: private */
    public static boolean isAppInstalled(String pkgName) {
        if (pkgName != null) {
            try {
                return Utils.getApp().getPackageManager().getApplicationInfo(pkgName, 0) != null;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            throw new NullPointerException("Argument 'pkgName' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    private static boolean isServiceRunning(String className) {
        List<ActivityManager.RunningServiceInfo> info = ((ActivityManager) Utils.getApp().getSystemService("activity")).getRunningServices(Integer.MAX_VALUE);
        if (info == null || info.size() == 0) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo aInfo : info) {
            if (className.equals(aInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public static boolean isAppRunning(String pkgName) {
        if (pkgName != null) {
            try {
                ApplicationInfo ai = Utils.getApp().getPackageManager().getApplicationInfo(pkgName, 0);
                if (ai == null) {
                    return false;
                }
                int uid = ai.uid;
                ActivityManager am = (ActivityManager) Utils.getApp().getSystemService("activity");
                if (am != null) {
                    List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(Integer.MAX_VALUE);
                    if (taskInfo != null && taskInfo.size() > 0) {
                        for (ActivityManager.RunningTaskInfo aInfo : taskInfo) {
                            if (pkgName.equals(aInfo.baseActivity.getPackageName())) {
                                return true;
                            }
                        }
                    }
                    List<ActivityManager.RunningServiceInfo> serviceInfo = am.getRunningServices(Integer.MAX_VALUE);
                    if (serviceInfo != null && serviceInfo.size() > 0) {
                        for (ActivityManager.RunningServiceInfo aInfo2 : serviceInfo) {
                            if (uid == aInfo2.uid) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            throw new NullPointerException("Argument 'pkgName' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    static class Client {
        LinkedList<Bundle> mCached = new LinkedList<>();
        Messenger mClient = new Messenger(this.mReceiveServeMsgHandler);
        ServiceConnection mConn = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d("MessengerUtils", "client service connected " + name);
                Client.this.mServer = new Messenger(service);
                Message msg = Message.obtain(Client.this.mReceiveServeMsgHandler, 0, Utils.getCurrentProcessName().hashCode(), 0);
                msg.replyTo = Client.this.mClient;
                try {
                    Client.this.mServer.send(msg);
                } catch (RemoteException e) {
                    Log.e("MessengerUtils", "onServiceConnected: ", e);
                }
                Client.this.sendCachedMsg2Server();
            }

            public void onServiceDisconnected(ComponentName name) {
                Log.w("MessengerUtils", "client service disconnected:" + name);
                Client.this.mServer = null;
                if (!Client.this.bind()) {
                    Log.e("MessengerUtils", "client service rebind failed: " + name);
                }
            }
        };
        String mPkgName;
        Handler mReceiveServeMsgHandler = new Handler() {
            public void handleMessage(Message msg) {
                String key;
                MessageCallback callback;
                Bundle data = msg.getData();
                if (data != null && (key = data.getString(MessengerUtils.KEY_STRING)) != null && (callback = (MessageCallback) MessengerUtils.subscribers.get(key)) != null) {
                    callback.messageCall(data);
                }
            }
        };
        Messenger mServer;

        Client(String pkgName) {
            this.mPkgName = pkgName;
        }

        /* access modifiers changed from: package-private */
        public boolean bind() {
            if (TextUtils.isEmpty(this.mPkgName)) {
                return Utils.getApp().bindService(new Intent(Utils.getApp(), ServerService.class), this.mConn, 1);
            } else if (!MessengerUtils.isAppInstalled(this.mPkgName)) {
                Log.e("MessengerUtils", "bind: the app is not installed -> " + this.mPkgName);
                return false;
            } else if (MessengerUtils.isAppRunning(this.mPkgName)) {
                Intent intent = new Intent(this.mPkgName + ".messenger");
                intent.setPackage(this.mPkgName);
                return Utils.getApp().bindService(intent, this.mConn, 1);
            } else {
                Log.e("MessengerUtils", "bind: the app is not running -> " + this.mPkgName);
                return false;
            }
        }

        /* access modifiers changed from: package-private */
        public void unbind() {
            Message msg = Message.obtain(this.mReceiveServeMsgHandler, 1);
            msg.replyTo = this.mClient;
            try {
                this.mServer.send(msg);
            } catch (RemoteException e) {
                Log.e("MessengerUtils", "unbind: ", e);
            }
            try {
                Utils.getApp().unbindService(this.mConn);
            } catch (Exception e2) {
            }
        }

        /* access modifiers changed from: package-private */
        public void sendMsg2Server(Bundle bundle) {
            if (this.mServer == null) {
                this.mCached.addFirst(bundle);
                Log.i("MessengerUtils", "save the bundle " + bundle);
                return;
            }
            sendCachedMsg2Server();
            if (!send2Server(bundle)) {
                this.mCached.addFirst(bundle);
            }
        }

        /* access modifiers changed from: private */
        public void sendCachedMsg2Server() {
            if (!this.mCached.isEmpty()) {
                for (int i = this.mCached.size() - 1; i >= 0; i--) {
                    if (send2Server(this.mCached.get(i))) {
                        this.mCached.remove(i);
                    }
                }
            }
        }

        private boolean send2Server(Bundle bundle) {
            Message msg = Message.obtain(this.mReceiveServeMsgHandler, 2);
            msg.setData(bundle);
            msg.replyTo = this.mClient;
            try {
                this.mServer.send(msg);
                return true;
            } catch (RemoteException e) {
                Log.e("MessengerUtils", "send2Server: ", e);
                return false;
            }
        }
    }

    public static class ServerService extends Service {
        /* access modifiers changed from: private */
        public final ConcurrentHashMap<Integer, Messenger> mClientMap = new ConcurrentHashMap<>();
        private final Handler mReceiveClientMsgHandler = new Handler() {
            public void handleMessage(Message msg) {
                int i = msg.what;
                if (i == 0) {
                    ServerService.this.mClientMap.put(Integer.valueOf(msg.arg1), msg.replyTo);
                } else if (i == 1) {
                    ServerService.this.mClientMap.remove(Integer.valueOf(msg.arg1));
                } else if (i != 2) {
                    super.handleMessage(msg);
                } else {
                    ServerService.this.sendMsg2Client(msg);
                    ServerService.this.consumeServerProcessCallback(msg);
                }
            }
        };
        private final Messenger messenger = new Messenger(this.mReceiveClientMsgHandler);

        public IBinder onBind(Intent intent) {
            return this.messenger.getBinder();
        }

        public int onStartCommand(Intent intent, int flags, int startId) {
            Bundle extras;
            if (!(intent == null || (extras = intent.getExtras()) == null)) {
                Message msg = Message.obtain(this.mReceiveClientMsgHandler, 2);
                msg.replyTo = this.messenger;
                msg.setData(extras);
                sendMsg2Client(msg);
                consumeServerProcessCallback(msg);
            }
            return 2;
        }

        /* access modifiers changed from: private */
        public void sendMsg2Client(Message msg) {
            for (Messenger client : this.mClientMap.values()) {
                if (client != null) {
                    try {
                        client.send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        /* access modifiers changed from: private */
        public void consumeServerProcessCallback(Message msg) {
            String key;
            MessageCallback callback;
            Bundle data = msg.getData();
            if (data != null && (key = data.getString(MessengerUtils.KEY_STRING)) != null && (callback = (MessageCallback) MessengerUtils.subscribers.get(key)) != null) {
                callback.messageCall(data);
            }
        }
    }
}
