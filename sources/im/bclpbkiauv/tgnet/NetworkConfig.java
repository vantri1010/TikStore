package im.bclpbkiauv.tgnet;

import android.text.TextUtils;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.security.yunceng.android.sdk.YunCeng;
import com.google.android.exoplayer2.util.Log;
import com.google.gson.Gson;
import com.king.zxing.util.LogUtils;
import com.qiniu.android.dns.DnsManager;
import com.qiniu.android.dns.IResolver;
import com.qiniu.android.dns.NetworkInfo;
import com.qiniu.android.dns.Record;
import com.qiniu.android.dns.dns.DohResolver;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.expandTextView.model.UUIDUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;

public class NetworkConfig {
    private static final int ENV_LOCAL = 1000;
    private static final int ENV_LOCAL_TEST = 1001;
    private static final int ENV_NONE = 0;
    private static final int ENV_ONLINE = 1002;
    private static final int ENV_ONLINE_GREY = 1004;
    private static final int ENV_ONLINE_TEST = 1003;
    public static final String[] HOST = {"https://8.212.67.50/dns-query", "https://8.212.66.81/dns-query"};
    public static final String[] IP_DEFALUT_ONLINE = {"Yixin.gz.yu05dp.com", "30570"};
    private static volatile NetworkConfig sInstance = new NetworkConfig();
    public static int serverIndex = 1;
    private ArrayList<NetBean> connInfos = new ArrayList<>();
    protected int currentAccount = UserConfig.selectedAccount;
    private DnsManager dnsManager;
    private String dohDomain = "cn.570.com";
    private boolean inited;
    private boolean inited_doh;
    private boolean initing;
    private boolean initing_doh;
    private int sCurrentConnection = -1;
    private boolean start = true;

    @Retention(RetentionPolicy.SOURCE)
    private @interface EnvNode {
    }

    public static NetworkConfig getInstance() {
        NetworkConfig localInstance = sInstance;
        if (localInstance == null) {
            synchronized (NetworkConfig.class) {
                localInstance = sInstance;
                if (localInstance == null) {
                    NetworkConfig networkConfig = new NetworkConfig();
                    localInstance = networkConfig;
                    sInstance = networkConfig;
                }
            }
        }
        return localInstance;
    }

    public static void setIpPortDefaultAddress() {
        String[] cfg;
        if (BuildVars.RELEASE_VERSION) {
            cfg = IP_DEFALUT_ONLINE;
        } else {
            cfg = getInstance().initNetconfig(1001);
        }
        if (BuildVars.DEBUG_VERSION) {
            StringBuilder sb = new StringBuilder();
            sb.append("java NetWorkConfig setIpPortDefaultAddress ===> address = ");
            sb.append(cfg != null ? Arrays.toString(cfg) : "null");
            Log.i("connection", sb.toString());
        }
        if (cfg != null && cfg[0] != null && cfg[1] != null) {
            for (int a = 0; a < 3; a++) {
                ConnectionsManager.native_setIpPortDefaultAddress(a, cfg[0], Integer.parseInt(cfg[1]));
            }
        }
    }

    private NetworkConfig() {
    }

    public void applyNetconfig(int instance) {
        String[] cfg;
        if (BuildVars.RELEASE_VERSION) {
            cfg = initNetconfig(1002);
        } else {
            cfg = initNetconfig(1002);
        }
        if (BuildVars.DEBUG_VERSION) {
            StringBuilder sb = new StringBuilder();
            sb.append("java NetWorkConfig applyNetconfig ===> address = ");
            sb.append(cfg != null ? Arrays.toString(cfg) : "null");
            Log.i("connection", sb.toString());
        }
        if (cfg != null && cfg[0] != null && cfg[1] != null) {
            ConnectionsManager.getInstance(instance).applyDatacenterAddress(2, cfg[0], Integer.parseInt(cfg[1]));
            ConnectionsManager.getInstance(instance).resumeNetworkMaybe();
        }
    }

    public void initUrlDOH() {
        this.initing_doh = true;
        try {
            DnsManager dnsManager2 = new DnsManager(NetworkInfo.normal, new IResolver[]{new DohResolver(HOST[0]), new DohResolver(HOST[1])});
            this.dnsManager = dnsManager2;
            Record[] records = dnsManager2.queryRecords(this.dohDomain);
            LogUtils.e("doh获取成功====" + JSONObject.toJSONString(records));
            if (records != null && records.length > 0) {
                for (Record record : records) {
                    this.connInfos.add(new NetBean((String) null, record.value, IP_DEFALUT_ONLINE[1]));
                }
            }
            this.inited_doh = true;
            this.sCurrentConnection = -1;
            setServer2();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable th) {
            this.initing_doh = false;
            throw th;
        }
        this.initing_doh = false;
    }

    private void setServer2() {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                NetworkConfig.this.lambda$setServer2$0$NetworkConfig();
            }
        });
    }

    public /* synthetic */ void lambda$setServer2$0$NetworkConfig() {
        String server2 = "server 2";
        if (this.inited) {
            server2 = "oss+" + server2;
        }
        if (this.inited_doh) {
            server2 = "doh+" + server2;
        }
        if (serverIndex < 3) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.getBackupIpStatus, server2);
        }
    }

    private String[] initNetconfig(int node) {
        if (this.start) {
            this.start = false;
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    NetworkConfig.this.lambda$initNetconfig$1$NetworkConfig();
                }
            });
        }
        switch (node) {
            case 1000:
                return new String[]{"47.104.243.76", "31537"};
            case 1001:
                return new String[]{"192.168.1.184", "31443"};
            case 1002:
                String[] address = initRemoteNetconfig();
                if (address == null || address.length != 2 || TextUtils.isEmpty(address[0]) || TextUtils.isEmpty(address[1])) {
                    return IP_DEFALUT_ONLINE;
                }
                return address;
            case 1003:
                return new String[]{"183.230.11.65", "55555"};
            case 1004:
                return new String[]{"36.255.220.245", "31443"};
            default:
                return null;
        }
    }

    public /* synthetic */ void lambda$initNetconfig$1$NetworkConfig() {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.getBackupIpStatus, "server 1");
    }

    private String[] initRemoteNetconfig() {
        synchronized (UUIDUtils.getUuid()) {
            if (this.inited) {
                String[] selecteRemoteConnInfo = selecteRemoteConnInfo();
                return selecteRemoteConnInfo;
            } else if (this.initing) {
                return null;
            } else {
                initRemoteConnInfos();
                String[] selecteRemoteConnInfo2 = selecteRemoteConnInfo();
                return selecteRemoteConnInfo2;
            }
        }
    }

    private synchronized String[] selecteRemoteConnInfo() {
        if (this.connInfos.size() == 0) {
            return null;
        }
        if (this.sCurrentConnection == -1) {
            this.sCurrentConnection = 0;
        }
        NetBean netBean = this.connInfos.get(this.sCurrentConnection);
        LogUtils.e("连接信息====" + new Gson().toJson((Object) this.connInfos));
        if (this.sCurrentConnection != this.connInfos.size() - 1) {
            this.sCurrentConnection++;
            return new String[]{netBean.getdDomain(), netBean.getdPort()};
        }
        this.sCurrentConnection = -1;
        this.inited = false;
        this.inited_doh = false;
        this.connInfos = new ArrayList<>();
        return null;
    }

    private synchronized void initRemoteConnInfos() {
        getOssUrl(false);
    }

    /* JADX WARNING: Removed duplicated region for block: B:102:0x02b2 A[Catch:{ all -> 0x02cd }] */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x02ba A[SYNTHETIC, Splitter:B:105:0x02ba] */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x02c8 A[SYNTHETIC, Splitter:B:110:0x02c8] */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x02d4 A[SYNTHETIC, Splitter:B:116:0x02d4] */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x02e2 A[SYNTHETIC, Splitter:B:121:0x02e2] */
    /* JADX WARNING: Removed duplicated region for block: B:131:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void getOssUrl(java.lang.Boolean r32) {
        /*
            r31 = this;
            r1 = r31
            r0 = 1
            java.lang.Boolean r2 = java.lang.Boolean.valueOf(r0)
            r1.initing = r0
            r3 = 0
            r4 = 0
            java.util.Date r5 = new java.util.Date
            r5.<init>()
            java.text.SimpleDateFormat r6 = new java.text.SimpleDateFormat
            java.lang.String r7 = "YYYYMMdd"
            r6.<init>(r7)
            java.lang.String r7 = r6.format(r5)
            java.lang.String r8 = "BW-570-"
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r8)
            r9.append(r7)
            boolean r10 = r32.booleanValue()
            if (r10 == 0) goto L_0x0031
            java.lang.String r10 = "1"
            goto L_0x0033
        L_0x0031:
            java.lang.String r10 = ""
        L_0x0033:
            r9.append(r10)
            java.lang.String r9 = r9.toString()
            java.lang.String r9 = im.bclpbkiauv.ui.hui.friendscircle.okhttphelper.MD5Utils.getMD5String((java.lang.String) r9)
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r10.append(r8)
            r10.append(r7)
            java.lang.String r11 = "Android"
            r10.append(r11)
            java.lang.String r10 = r10.toString()
            java.lang.String r10 = im.bclpbkiauv.ui.hui.friendscircle.okhttphelper.MD5Utils.getMD5String((java.lang.String) r10)
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r12 = "https://"
            r11.append(r12)
            r11.append(r9)
            java.lang.String r12 = ".oss-accelerate.aliyuncs.com/"
            r11.append(r12)
            r11.append(r10)
            java.lang.String r12 = ".txt"
            r11.append(r12)
            java.lang.String r11 = r11.toString()
            java.io.PrintStream r12 = java.lang.System.out
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r14 = "环境时间："
            r13.append(r14)
            r13.append(r8)
            r13.append(r7)
            r14 = r32
            r13.append(r14)
            java.lang.String r13 = r13.toString()
            r12.println(r13)
            java.io.PrintStream r12 = java.lang.System.out
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r15 = "oss==>："
            r13.append(r15)
            r13.append(r11)
            java.lang.String r13 = r13.toString()
            r12.println(r13)
            java.net.URL r13 = new java.net.URL     // Catch:{ Exception -> 0x02a2, all -> 0x0299 }
            r13.<init>(r11)     // Catch:{ Exception -> 0x02a2, all -> 0x0299 }
            java.net.URLConnection r15 = r13.openConnection()     // Catch:{ Exception -> 0x02a2, all -> 0x0299 }
            java.net.HttpURLConnection r15 = (java.net.HttpURLConnection) r15     // Catch:{ Exception -> 0x02a2, all -> 0x0299 }
            java.lang.String r0 = "User-Agent"
            java.lang.String r12 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1"
            r15.addRequestProperty(r0, r12)     // Catch:{ Exception -> 0x02a2, all -> 0x0299 }
            java.lang.String r0 = "GET"
            r15.setRequestMethod(r0)     // Catch:{ Exception -> 0x02a2, all -> 0x0299 }
            r0 = 5000(0x1388, float:7.006E-42)
            r15.setReadTimeout(r0)     // Catch:{ Exception -> 0x02a2, all -> 0x0299 }
            r15.setConnectTimeout(r0)     // Catch:{ Exception -> 0x02a2, all -> 0x0299 }
            r15.connect()     // Catch:{ Exception -> 0x02a2, all -> 0x0299 }
            int r0 = r15.getResponseCode()     // Catch:{ Exception -> 0x02a2, all -> 0x0299 }
            r12 = 200(0xc8, float:2.8E-43)
            if (r0 != r12) goto L_0x026a
            java.io.InputStream r0 = r15.getInputStream()     // Catch:{ Exception -> 0x02a2, all -> 0x0299 }
            r3 = r0
            r0 = 0
            r12 = 1024(0x400, float:1.435E-42)
            byte[] r12 = new byte[r12]     // Catch:{ Exception -> 0x0260, all -> 0x0254 }
            java.io.ByteArrayOutputStream r18 = new java.io.ByteArrayOutputStream     // Catch:{ Exception -> 0x0260, all -> 0x0254 }
            r18.<init>()     // Catch:{ Exception -> 0x0260, all -> 0x0254 }
            r4 = r18
        L_0x00e4:
            r18 = r0
            int r0 = r3.read(r12)     // Catch:{ Exception -> 0x0247, all -> 0x0239 }
            r19 = r0
            r18 = r3
            r3 = -1
            if (r0 == r3) goto L_0x0111
            r0 = r19
            r3 = 0
            r4.write(r12, r3, r0)     // Catch:{ Exception -> 0x0106, all -> 0x00fa }
            r3 = r18
            goto L_0x00e4
        L_0x00fa:
            r0 = move-exception
            r2 = r0
            r25 = r5
            r27 = r6
            r29 = r7
            r3 = r18
            goto L_0x02cf
        L_0x0106:
            r0 = move-exception
            r25 = r5
            r27 = r6
            r29 = r7
            r3 = r18
            goto L_0x02a9
        L_0x0111:
            r0 = r19
            java.lang.String r3 = new java.lang.String     // Catch:{ Exception -> 0x022c, all -> 0x021e }
            r20 = r0
            byte[] r0 = r4.toByteArray()     // Catch:{ Exception -> 0x022c, all -> 0x021e }
            r3.<init>(r0)     // Catch:{ Exception -> 0x022c, all -> 0x021e }
            r0 = r3
            byte[] r3 = im.bclpbkiauv.ui.utils.AesUtils.decryptYunceng(r0)     // Catch:{ Exception -> 0x022c, all -> 0x021e }
            r21 = r0
            java.lang.String r0 = new java.lang.String     // Catch:{ Exception -> 0x022c, all -> 0x021e }
            r0.<init>(r3)     // Catch:{ Exception -> 0x022c, all -> 0x021e }
            r22 = r3
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x022c, all -> 0x021e }
            r3.<init>()     // Catch:{ Exception -> 0x022c, all -> 0x021e }
            r23 = r4
            java.lang.String r4 = "oss获取成功===="
            r3.append(r4)     // Catch:{ Exception -> 0x0211, all -> 0x0203 }
            r3.append(r0)     // Catch:{ Exception -> 0x0211, all -> 0x0203 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0211, all -> 0x0203 }
            com.king.zxing.util.LogUtils.e((java.lang.String) r3)     // Catch:{ Exception -> 0x0211, all -> 0x0203 }
            java.lang.String r3 = ","
            r4 = 3
            java.lang.String[] r3 = r0.split(r3, r4)     // Catch:{ Exception -> 0x0211, all -> 0x0203 }
            r24 = 0
            r4 = r24
        L_0x014d:
            r24 = r0
            int r0 = r3.length     // Catch:{ Exception -> 0x0211, all -> 0x0203 }
            if (r4 >= r0) goto L_0x01dd
            r0 = r3[r4]     // Catch:{ Exception -> 0x0211, all -> 0x0203 }
            r25 = r5
            int r5 = r3.length     // Catch:{ Exception -> 0x01d2, all -> 0x01c6 }
            r16 = 1
            int r5 = r5 + -1
            r26 = r3
            r3 = 2
            if (r4 == r5) goto L_0x0195
            java.lang.String r5 = ":"
            java.lang.String[] r3 = r0.split(r5, r3)     // Catch:{ Exception -> 0x01d2, all -> 0x01c6 }
            im.bclpbkiauv.tgnet.NetBean r5 = new im.bclpbkiauv.tgnet.NetBean     // Catch:{ Exception -> 0x01d2, all -> 0x01c6 }
            r27 = r6
            r17 = 0
            r6 = r3[r17]     // Catch:{ Exception -> 0x018c, all -> 0x0182 }
            r29 = r7
            r16 = 1
            r7 = r3[r16]     // Catch:{ Exception -> 0x01fc, all -> 0x01f4 }
            r30 = r3
            r3 = 0
            r5.<init>(r3, r6, r7)     // Catch:{ Exception -> 0x01fc, all -> 0x01f4 }
            r3 = r5
            java.util.ArrayList<im.bclpbkiauv.tgnet.NetBean> r5 = r1.connInfos     // Catch:{ Exception -> 0x01fc, all -> 0x01f4 }
            r5.add(r3)     // Catch:{ Exception -> 0x01fc, all -> 0x01f4 }
            goto L_0x01b9
        L_0x0182:
            r0 = move-exception
            r29 = r7
            r2 = r0
            r3 = r18
            r4 = r23
            goto L_0x02cf
        L_0x018c:
            r0 = move-exception
            r29 = r7
            r3 = r18
            r4 = r23
            goto L_0x02a9
        L_0x0195:
            r27 = r6
            r29 = r7
            java.lang.String r5 = "#"
            r6 = 3
            java.lang.String[] r5 = r0.split(r5, r6)     // Catch:{ Exception -> 0x01fc, all -> 0x01f4 }
            im.bclpbkiauv.tgnet.NetBean r7 = new im.bclpbkiauv.tgnet.NetBean     // Catch:{ Exception -> 0x01fc, all -> 0x01f4 }
            r17 = 0
            r6 = r5[r17]     // Catch:{ Exception -> 0x01fc, all -> 0x01f4 }
            r16 = 1
            r3 = r5[r16]     // Catch:{ Exception -> 0x01fc, all -> 0x01f4 }
            r30 = r0
            r28 = 2
            r0 = r5[r28]     // Catch:{ Exception -> 0x01fc, all -> 0x01f4 }
            r7.<init>(r6, r3, r0)     // Catch:{ Exception -> 0x01fc, all -> 0x01f4 }
            r0 = r7
            java.util.ArrayList<im.bclpbkiauv.tgnet.NetBean> r3 = r1.connInfos     // Catch:{ Exception -> 0x01fc, all -> 0x01f4 }
            r3.add(r0)     // Catch:{ Exception -> 0x01fc, all -> 0x01f4 }
        L_0x01b9:
            int r4 = r4 + 1
            r0 = r24
            r5 = r25
            r3 = r26
            r6 = r27
            r7 = r29
            goto L_0x014d
        L_0x01c6:
            r0 = move-exception
            r27 = r6
            r29 = r7
            r2 = r0
            r3 = r18
            r4 = r23
            goto L_0x02cf
        L_0x01d2:
            r0 = move-exception
            r27 = r6
            r29 = r7
            r3 = r18
            r4 = r23
            goto L_0x02a9
        L_0x01dd:
            r26 = r3
            r25 = r5
            r27 = r6
            r29 = r7
            r0 = 1
            r1.inited = r0     // Catch:{ Exception -> 0x01fc, all -> 0x01f4 }
            r0 = -1
            r1.sCurrentConnection = r0     // Catch:{ Exception -> 0x01fc, all -> 0x01f4 }
            r31.setServer2()     // Catch:{ Exception -> 0x01fc, all -> 0x01f4 }
            r3 = r18
            r4 = r23
            goto L_0x0279
        L_0x01f4:
            r0 = move-exception
            r2 = r0
            r3 = r18
            r4 = r23
            goto L_0x02cf
        L_0x01fc:
            r0 = move-exception
            r3 = r18
            r4 = r23
            goto L_0x02a9
        L_0x0203:
            r0 = move-exception
            r25 = r5
            r27 = r6
            r29 = r7
            r2 = r0
            r3 = r18
            r4 = r23
            goto L_0x02cf
        L_0x0211:
            r0 = move-exception
            r25 = r5
            r27 = r6
            r29 = r7
            r3 = r18
            r4 = r23
            goto L_0x02a9
        L_0x021e:
            r0 = move-exception
            r23 = r4
            r25 = r5
            r27 = r6
            r29 = r7
            r2 = r0
            r3 = r18
            goto L_0x02cf
        L_0x022c:
            r0 = move-exception
            r23 = r4
            r25 = r5
            r27 = r6
            r29 = r7
            r3 = r18
            goto L_0x02a9
        L_0x0239:
            r0 = move-exception
            r18 = r3
            r23 = r4
            r25 = r5
            r27 = r6
            r29 = r7
            r2 = r0
            goto L_0x02cf
        L_0x0247:
            r0 = move-exception
            r18 = r3
            r23 = r4
            r25 = r5
            r27 = r6
            r29 = r7
            goto L_0x02a9
        L_0x0254:
            r0 = move-exception
            r18 = r3
            r25 = r5
            r27 = r6
            r29 = r7
            r2 = r0
            goto L_0x02cf
        L_0x0260:
            r0 = move-exception
            r18 = r3
            r25 = r5
            r27 = r6
            r29 = r7
            goto L_0x02a9
        L_0x026a:
            r25 = r5
            r27 = r6
            r29 = r7
            boolean r0 = r32.booleanValue()     // Catch:{ Exception -> 0x0297 }
            if (r0 != 0) goto L_0x0279
            r1.getOssUrl(r2)     // Catch:{ Exception -> 0x0297 }
        L_0x0279:
            r2 = 0
            r1.initing = r2
            if (r3 == 0) goto L_0x0289
            r3.close()     // Catch:{ Exception -> 0x0282 }
            goto L_0x0289
        L_0x0282:
            r0 = move-exception
            r2 = r0
            r0 = r2
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x028a
        L_0x0289:
        L_0x028a:
            if (r4 == 0) goto L_0x02cc
            r4.close()     // Catch:{ Exception -> 0x0290 }
        L_0x028f:
            goto L_0x02cc
        L_0x0290:
            r0 = move-exception
            r2 = r0
            r0 = r2
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x028f
        L_0x0297:
            r0 = move-exception
            goto L_0x02a9
        L_0x0299:
            r0 = move-exception
            r25 = r5
            r27 = r6
            r29 = r7
            r2 = r0
            goto L_0x02cf
        L_0x02a2:
            r0 = move-exception
            r25 = r5
            r27 = r6
            r29 = r7
        L_0x02a9:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x02cd }
            boolean r5 = r32.booleanValue()     // Catch:{ all -> 0x02cd }
            if (r5 != 0) goto L_0x02b5
            r1.getOssUrl(r2)     // Catch:{ all -> 0x02cd }
        L_0x02b5:
            r2 = 0
            r1.initing = r2
            if (r3 == 0) goto L_0x02c5
            r3.close()     // Catch:{ Exception -> 0x02be }
            goto L_0x02c5
        L_0x02be:
            r0 = move-exception
            r2 = r0
            r0 = r2
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x02c6
        L_0x02c5:
        L_0x02c6:
            if (r4 == 0) goto L_0x02cc
            r4.close()     // Catch:{ Exception -> 0x0290 }
            goto L_0x028f
        L_0x02cc:
            return
        L_0x02cd:
            r0 = move-exception
            r2 = r0
        L_0x02cf:
            r5 = 0
            r1.initing = r5
            if (r3 == 0) goto L_0x02df
            r3.close()     // Catch:{ Exception -> 0x02d8 }
            goto L_0x02df
        L_0x02d8:
            r0 = move-exception
            r5 = r0
            r0 = r5
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x02e0
        L_0x02df:
        L_0x02e0:
            if (r4 == 0) goto L_0x02ec
            r4.close()     // Catch:{ Exception -> 0x02e6 }
            goto L_0x02ec
        L_0x02e6:
            r0 = move-exception
            r5 = r0
            r0 = r5
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x02ec:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.tgnet.NetworkConfig.getOssUrl(java.lang.Boolean):void");
    }

    private String[] getYuncengConfig(String groupName, String dDomain, String dPort) {
        StringBuffer ip = new StringBuffer();
        StringBuffer port = new StringBuffer();
        int ret = YunCeng.getProxyTcpByDomain("Default", groupName, dDomain, dPort, ip, port);
        if (ret == 0) {
            return new String[]{ip.toString(), port.toString()};
        }
        FileLog.e("YunCeng get next ip failed: " + ret);
        return null;
    }
}
