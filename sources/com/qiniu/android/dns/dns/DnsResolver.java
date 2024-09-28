package com.qiniu.android.dns.dns;

import com.qiniu.android.dns.Domain;
import com.qiniu.android.dns.IResolver;
import com.qiniu.android.dns.NetworkInfo;
import com.qiniu.android.dns.Record;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class DnsResolver implements IResolver {
    private static ExecutorService defaultExecutorService = Executors.newFixedThreadPool(4);
    private static ScheduledExecutorService timeoutExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final ExecutorService executorService;
    private final int recordType;
    /* access modifiers changed from: private */
    public final String[] servers;
    protected final int timeout;

    /* access modifiers changed from: package-private */
    public abstract DnsResponse request(RequestCanceller requestCanceller, String str, String str2, int i) throws IOException;

    public DnsResolver(String server) {
        this(server, 1, 10);
    }

    public DnsResolver(String server, int timeout2) {
        this(server, 1, timeout2);
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public DnsResolver(String server, int recordType2, int timeout2) {
        this(server == null ? null : new String[]{server}, recordType2, timeout2, (ExecutorService) null);
    }

    public DnsResolver(String[] servers2, int recordType2, int timeout2) {
        this.recordType = recordType2;
        this.timeout = timeout2 > 0 ? timeout2 : 10;
        this.servers = servers2;
        this.executorService = null;
    }

    public DnsResolver(String[] servers2, int recordType2, int timeout2, ExecutorService executorService2) {
        if (servers2 != null && servers2.length > 1 && executorService2 == null) {
            executorService2 = defaultExecutorService;
        }
        this.recordType = recordType2;
        this.timeout = timeout2 > 0 ? timeout2 : 10;
        this.servers = servers2;
        this.executorService = executorService2;
    }

    public Record[] resolve(Domain domain, NetworkInfo info) throws IOException {
        DnsResponse response = lookupHost(domain.domain);
        if (response != null) {
            List<Record> answers = response.getAnswerArray();
            if (answers == null || answers.size() == 0) {
                return null;
            }
            List<Record> records = new ArrayList<>();
            for (Record record : answers) {
                if (record.isA() || record.isCname() || record.isAAAA() || record.type == this.recordType) {
                    records.add(record);
                }
            }
            return (Record[]) records.toArray(new Record[0]);
        }
        throw new IOException("response is null");
    }

    private DnsResponse lookupHost(String host) throws IOException {
        return request(host, this.recordType);
    }

    private DnsResponse request(String host, int recordType2) throws IOException {
        final String str = host;
        String[] strArr = this.servers;
        if (strArr == null || strArr.length == 0) {
            int i = recordType2;
            throw new IOException("server can not empty");
        } else if (str == null || host.length() == 0) {
            int i2 = recordType2;
            throw new IOException("host can not empty");
        } else {
            RequestCanceller canceller = new RequestCanceller();
            int i3 = 0;
            if (this.servers.length == 1 || this.executorService == null) {
                DnsResponse response = null;
                String[] strArr2 = this.servers;
                int length = strArr2.length;
                while (true) {
                    if (i3 >= length) {
                        int i4 = recordType2;
                        break;
                    }
                    response = request(canceller, strArr2[i3], str, recordType2);
                    if (response != null) {
                        break;
                    }
                    i3++;
                }
                return response;
            }
            final ResponseComposition responseComposition = new ResponseComposition();
            timeoutExecutorService.schedule(new Callable<Object>() {
                public Object call() throws Exception {
                    synchronized (responseComposition) {
                        responseComposition.notify();
                        ResponseComposition responseComposition = responseComposition;
                        responseComposition.exception = new IOException("resolver timeout for server:" + DnsResolver.this.servers + " host:" + str);
                    }
                    return null;
                }
            }, (long) this.timeout, TimeUnit.SECONDS);
            List<Future<?>> futures = new ArrayList<>();
            String[] strArr3 = this.servers;
            int length2 = strArr3.length;
            int i5 = 0;
            while (i5 < length2) {
                final RequestCanceller requestCanceller = canceller;
                final String str2 = strArr3[i5];
                final String str3 = host;
                String[] strArr4 = strArr3;
                AnonymousClass2 r0 = r1;
                final int i6 = recordType2;
                int i7 = length2;
                ExecutorService executorService2 = this.executorService;
                final ResponseComposition responseComposition2 = responseComposition;
                AnonymousClass2 r1 = new Runnable() {
                    public void run() {
                        DnsResponse response = null;
                        IOException exception = null;
                        try {
                            response = DnsResolver.this.request(requestCanceller, str2, str3, i6);
                        } catch (Exception e) {
                            e.printStackTrace();
                            exception = new IOException(e);
                        }
                        synchronized (responseComposition2) {
                            responseComposition2.completedCount++;
                            if (responseComposition2.response == null) {
                                responseComposition2.response = response;
                            }
                            if (responseComposition2.exception == null) {
                                responseComposition2.exception = exception;
                            }
                            if (responseComposition2.completedCount == DnsResolver.this.servers.length || responseComposition2.response != null) {
                                responseComposition2.notify();
                            }
                        }
                    }
                };
                futures.add(executorService2.submit(r0));
                i5++;
                strArr3 = strArr4;
                length2 = i7;
            }
            synchronized (responseComposition) {
                try {
                    responseComposition.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            canceller.cancel();
            if (responseComposition.exception == null || responseComposition.response != null) {
                return responseComposition.response;
            }
            throw responseComposition.exception;
        }
    }

    private static class ResponseComposition {
        int completedCount = 0;
        IOException exception;
        DnsResponse response;

        ResponseComposition() {
        }
    }

    static class RequestCanceller {
        Queue<Runnable> cancelActions = new ConcurrentLinkedQueue();

        RequestCanceller() {
        }

        /* access modifiers changed from: package-private */
        public void addCancelAction(Runnable cancelAction) {
            if (cancelAction != null) {
                this.cancelActions.add(cancelAction);
            }
        }

        /* access modifiers changed from: package-private */
        public void cancel() {
            for (Runnable cancelAction : this.cancelActions) {
                if (cancelAction != null) {
                    cancelAction.run();
                }
            }
        }
    }
}
