package com.qiniu.android.dns.local;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Build;
import com.qiniu.android.dns.Domain;
import com.qiniu.android.dns.IResolver;
import com.qiniu.android.dns.NetworkInfo;
import com.qiniu.android.dns.Record;
import com.qiniu.android.dns.dns.DnsUdpResolver;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class AndroidDnsServer {

    static class AndroidResolver implements IResolver {
        /* access modifiers changed from: private */
        public List<InetAddress> dnsServers = new ArrayList();
        /* access modifiers changed from: private */
        public boolean networkCallback = false;

        @TargetApi(21)
        AndroidResolver(Context context) {
            List<InetAddress> addresses = AndroidDnsServer.getByReflection();
            addresses = addresses == null ? AndroidDnsServer.getByCommand() : addresses;
            if (addresses != null) {
                this.dnsServers.addAll(addresses);
            } else if (Build.VERSION.SDK_INT >= 21) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
                NetworkRequest.Builder builder = new NetworkRequest.Builder();
                if (connectivityManager != null) {
                    try {
                        connectivityManager.registerNetworkCallback(builder.build(), new ConnectivityManager.NetworkCallback() {
                            public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
                                if (linkProperties != null) {
                                    AndroidResolver.this.dnsServers.addAll(linkProperties.getDnsServers());
                                }
                                boolean unused = AndroidResolver.this.networkCallback = true;
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public Record[] resolve(Domain domain, NetworkInfo info) throws IOException {
            if (this.dnsServers.isEmpty() && !this.networkCallback) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            List<InetAddress> list = this.dnsServers;
            if (list == null || list.isEmpty()) {
                throw new IOException("cant get local dns server");
            }
            int i = 0;
            InetAddress dnsServer = this.dnsServers.get(0);
            Record[] records = new HijackingDetectWrapper(new DnsUdpResolver(dnsServer.getHostName())).resolve(domain, info);
            if (domain.hasCname) {
                boolean cname = false;
                int length = records.length;
                int i2 = 0;
                while (true) {
                    if (i2 >= length) {
                        break;
                    } else if (records[i2].isCname()) {
                        cname = true;
                        break;
                    } else {
                        i2++;
                    }
                }
                if (!cname) {
                    throw new DnshijackingException(domain.domain, dnsServer.getHostAddress());
                }
            }
            if (domain.maxTtl != 0) {
                int length2 = records.length;
                while (i < length2) {
                    Record r = records[i];
                    if (r.isCname() || r.ttl <= domain.maxTtl) {
                        i++;
                    } else {
                        throw new DnshijackingException(domain.domain, dnsServer.getHostAddress(), r.ttl);
                    }
                }
            }
            return records;
        }
    }

    public static List<InetAddress> getByCommand() {
        try {
            LineNumberReader lnr = new LineNumberReader(new InputStreamReader(Runtime.getRuntime().exec("getprop").getInputStream()));
            ArrayList<InetAddress> servers = new ArrayList<>(5);
            while (true) {
                String readLine = lnr.readLine();
                String line = readLine;
                if (readLine == null) {
                    break;
                }
                int split = line.indexOf("]: [");
                if (split > 1) {
                    if (line.length() - 1 > split + 4) {
                        String property = line.substring(1, split);
                        String value = line.substring(split + 4, line.length() - 1);
                        if (property.endsWith(".dns") || property.endsWith(".dns1") || property.endsWith(".dns2") || property.endsWith(".dns3") || property.endsWith(".dns4")) {
                            InetAddress ip = InetAddress.getByName(value);
                            if (ip != null) {
                                String value2 = ip.getHostAddress();
                                if (value2 != null) {
                                    if (value2.length() != 0) {
                                        servers.add(ip);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (servers.size() > 0) {
                return servers;
            }
            return null;
        } catch (IOException e) {
            Logger.getLogger("AndroidDnsServer").log(Level.WARNING, "Exception in findDNSByExec", e);
            return null;
        }
    }

    public static List<InetAddress> getByReflection() {
        try {
            Method method = Class.forName("android.os.SystemProperties").getMethod("get", new Class[]{String.class});
            ArrayList<InetAddress> servers = new ArrayList<>(5);
            String[] strArr = {"net.dns1", "net.dns2", "net.dns3", "net.dns4"};
            for (int i = 0; i < 4; i++) {
                String value = (String) method.invoke((Object) null, new Object[]{strArr[i]});
                if (value != null) {
                    if (value.length() != 0) {
                        InetAddress ip = InetAddress.getByName(value);
                        if (ip != null) {
                            String value2 = ip.getHostAddress();
                            if (value2 != null) {
                                if (value2.length() != 0) {
                                    if (!servers.contains(ip)) {
                                        servers.add(ip);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (servers.size() > 0) {
                return servers;
            }
            return null;
        } catch (Exception e) {
            Logger.getLogger("AndroidDnsServer").log(Level.WARNING, "Exception in findDNSByReflection", e);
        }
    }

    public static IResolver defaultResolver(Context context) {
        return new AndroidResolver(context);
    }
}
