package okhttp3.internal.connection;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.util.List;
import okhttp3.Address;
import okhttp3.Call;
import okhttp3.Connection;
import okhttp3.ConnectionPool;
import okhttp3.EventListener;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Route;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import okhttp3.internal.connection.RouteSelector;
import okhttp3.internal.http.HttpCodec;
import okhttp3.internal.http2.ConnectionShutdownException;
import okhttp3.internal.http2.ErrorCode;
import okhttp3.internal.http2.StreamResetException;

public final class StreamAllocation {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public final Address address;
    public final Call call;
    private final Object callStackTrace;
    private boolean canceled;
    private HttpCodec codec;
    private RealConnection connection;
    private final ConnectionPool connectionPool;
    public final EventListener eventListener;
    private int refusedStreamCount;
    private boolean released;
    private boolean reportedAcquired;
    private Route route;
    private RouteSelector.Selection routeSelection;
    private final RouteSelector routeSelector;

    public StreamAllocation(ConnectionPool connectionPool2, Address address2, Call call2, EventListener eventListener2, Object callStackTrace2) {
        this.connectionPool = connectionPool2;
        this.address = address2;
        this.call = call2;
        this.eventListener = eventListener2;
        this.routeSelector = new RouteSelector(address2, routeDatabase(), call2, eventListener2);
        this.callStackTrace = callStackTrace2;
    }

    public HttpCodec newStream(OkHttpClient client, Interceptor.Chain chain, boolean doExtensiveHealthChecks) {
        try {
            HttpCodec resultCodec = findHealthyConnection(chain.connectTimeoutMillis(), chain.readTimeoutMillis(), chain.writeTimeoutMillis(), client.pingIntervalMillis(), client.retryOnConnectionFailure(), doExtensiveHealthChecks).newCodec(client, chain, this);
            synchronized (this.connectionPool) {
                this.codec = resultCodec;
            }
            return resultCodec;
        } catch (IOException e) {
            throw new RouteException(e);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0018, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0012, code lost:
        if (r0.isHealthy(r9) != false) goto L_0x0018;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private okhttp3.internal.connection.RealConnection findHealthyConnection(int r4, int r5, int r6, int r7, boolean r8, boolean r9) throws java.io.IOException {
        /*
            r3 = this;
        L_0x0000:
            okhttp3.internal.connection.RealConnection r0 = r3.findConnection(r4, r5, r6, r7, r8)
            okhttp3.ConnectionPool r1 = r3.connectionPool
            monitor-enter(r1)
            int r2 = r0.successCount     // Catch:{ all -> 0x0019 }
            if (r2 != 0) goto L_0x000d
            monitor-exit(r1)     // Catch:{ all -> 0x0019 }
            return r0
        L_0x000d:
            monitor-exit(r1)     // Catch:{ all -> 0x0019 }
            boolean r1 = r0.isHealthy(r9)
            if (r1 != 0) goto L_0x0018
            r3.noNewStreams()
            goto L_0x0000
        L_0x0018:
            return r0
        L_0x0019:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0019 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.connection.StreamAllocation.findHealthyConnection(int, int, int, int, boolean, boolean):okhttp3.internal.connection.RealConnection");
    }

    private RealConnection findConnection(int connectTimeout, int readTimeout, int writeTimeout, int pingIntervalMillis, boolean connectionRetryEnabled) throws IOException {
        Socket toClose;
        Connection releasedConnection;
        boolean newRouteSelection;
        RouteSelector.Selection selection;
        boolean foundPooledConnection = false;
        RealConnection result = null;
        Route selectedRoute = null;
        synchronized (this.connectionPool) {
            if (this.released) {
                throw new IllegalStateException("released");
            } else if (this.codec != null) {
                throw new IllegalStateException("codec != null");
            } else if (!this.canceled) {
                Connection releasedConnection2 = this.connection;
                toClose = releaseIfNoNewStreams();
                if (this.connection != null) {
                    result = this.connection;
                    releasedConnection2 = null;
                }
                if (!this.reportedAcquired) {
                    releasedConnection = null;
                } else {
                    releasedConnection = releasedConnection2;
                }
                if (result == null) {
                    Internal.instance.get(this.connectionPool, this.address, this, (Route) null);
                    if (this.connection != null) {
                        foundPooledConnection = true;
                        result = this.connection;
                    } else {
                        selectedRoute = this.route;
                    }
                }
            } else {
                throw new IOException("Canceled");
            }
        }
        Util.closeQuietly(toClose);
        if (releasedConnection != null) {
            this.eventListener.connectionReleased(this.call, releasedConnection);
        }
        if (foundPooledConnection) {
            this.eventListener.connectionAcquired(this.call, result);
        }
        if (result != null) {
            return result;
        }
        if (selectedRoute != null || ((selection = this.routeSelection) != null && selection.hasNext())) {
            newRouteSelection = false;
        } else {
            this.routeSelection = this.routeSelector.next();
            newRouteSelection = true;
        }
        synchronized (this.connectionPool) {
            if (!this.canceled) {
                if (newRouteSelection) {
                    List<Route> routes = this.routeSelection.getAll();
                    int i = 0;
                    int size = routes.size();
                    while (true) {
                        if (i >= size) {
                            break;
                        }
                        Route route2 = routes.get(i);
                        Internal.instance.get(this.connectionPool, this.address, this, route2);
                        if (this.connection != null) {
                            foundPooledConnection = true;
                            result = this.connection;
                            this.route = route2;
                            break;
                        }
                        i++;
                    }
                }
                if (!foundPooledConnection) {
                    if (selectedRoute == null) {
                        selectedRoute = this.routeSelection.next();
                    }
                    this.route = selectedRoute;
                    this.refusedStreamCount = 0;
                    result = new RealConnection(this.connectionPool, selectedRoute);
                    acquire(result, false);
                }
            } else {
                throw new IOException("Canceled");
            }
        }
        if (foundPooledConnection) {
            this.eventListener.connectionAcquired(this.call, result);
            return result;
        }
        result.connect(connectTimeout, readTimeout, writeTimeout, pingIntervalMillis, connectionRetryEnabled, this.call, this.eventListener);
        routeDatabase().connected(result.route());
        Socket socket = null;
        synchronized (this.connectionPool) {
            this.reportedAcquired = true;
            Internal.instance.put(this.connectionPool, result);
            if (result.isMultiplexed()) {
                socket = Internal.instance.deduplicate(this.connectionPool, this.address, this);
                result = this.connection;
            }
        }
        Util.closeQuietly(socket);
        this.eventListener.connectionAcquired(this.call, result);
        return result;
    }

    private Socket releaseIfNoNewStreams() {
        RealConnection allocatedConnection = this.connection;
        if (allocatedConnection == null || !allocatedConnection.noNewStreams) {
            return null;
        }
        return deallocate(false, false, true);
    }

    public void streamFinished(boolean noNewStreams, HttpCodec codec2, long bytesRead, IOException e) {
        Connection releasedConnection;
        Socket socket;
        boolean callEnd;
        this.eventListener.responseBodyEnd(this.call, bytesRead);
        synchronized (this.connectionPool) {
            if (codec2 != null) {
                if (codec2 == this.codec) {
                    if (!noNewStreams) {
                        this.connection.successCount++;
                    }
                    releasedConnection = this.connection;
                    socket = deallocate(noNewStreams, false, true);
                    if (this.connection != null) {
                        releasedConnection = null;
                    }
                    callEnd = this.released;
                }
            }
            throw new IllegalStateException("expected " + this.codec + " but was " + codec2);
        }
        Util.closeQuietly(socket);
        if (releasedConnection != null) {
            this.eventListener.connectionReleased(this.call, releasedConnection);
        }
        if (e != null) {
            this.eventListener.callFailed(this.call, e);
        } else if (callEnd) {
            this.eventListener.callEnd(this.call);
        }
    }

    public HttpCodec codec() {
        HttpCodec httpCodec;
        synchronized (this.connectionPool) {
            httpCodec = this.codec;
        }
        return httpCodec;
    }

    private RouteDatabase routeDatabase() {
        return Internal.instance.routeDatabase(this.connectionPool);
    }

    public Route route() {
        return this.route;
    }

    public synchronized RealConnection connection() {
        return this.connection;
    }

    public void release() {
        Connection releasedConnection;
        Socket socket;
        synchronized (this.connectionPool) {
            releasedConnection = this.connection;
            socket = deallocate(false, true, false);
            if (this.connection != null) {
                releasedConnection = null;
            }
        }
        Util.closeQuietly(socket);
        if (releasedConnection != null) {
            this.eventListener.connectionReleased(this.call, releasedConnection);
            this.eventListener.callEnd(this.call);
        }
    }

    public void noNewStreams() {
        Connection releasedConnection;
        Socket socket;
        synchronized (this.connectionPool) {
            releasedConnection = this.connection;
            socket = deallocate(true, false, false);
            if (this.connection != null) {
                releasedConnection = null;
            }
        }
        Util.closeQuietly(socket);
        if (releasedConnection != null) {
            this.eventListener.connectionReleased(this.call, releasedConnection);
        }
    }

    private Socket deallocate(boolean noNewStreams, boolean released2, boolean streamFinished) {
        if (streamFinished) {
            this.codec = null;
        }
        if (released2) {
            this.released = true;
        }
        Socket socket = null;
        RealConnection realConnection = this.connection;
        if (realConnection != null) {
            if (noNewStreams) {
                realConnection.noNewStreams = true;
            }
            if (this.codec == null && (this.released || this.connection.noNewStreams)) {
                release(this.connection);
                if (this.connection.allocations.isEmpty()) {
                    this.connection.idleAtNanos = System.nanoTime();
                    if (Internal.instance.connectionBecameIdle(this.connectionPool, this.connection)) {
                        socket = this.connection.socket();
                    }
                }
                this.connection = null;
            }
        }
        return socket;
    }

    public void cancel() {
        HttpCodec codecToCancel;
        RealConnection connectionToCancel;
        synchronized (this.connectionPool) {
            this.canceled = true;
            codecToCancel = this.codec;
            connectionToCancel = this.connection;
        }
        if (codecToCancel != null) {
            codecToCancel.cancel();
        } else if (connectionToCancel != null) {
            connectionToCancel.cancel();
        }
    }

    public void streamFailed(IOException e) {
        Connection releasedConnection;
        Socket socket;
        boolean noNewStreams = false;
        synchronized (this.connectionPool) {
            if (e instanceof StreamResetException) {
                ErrorCode errorCode = ((StreamResetException) e).errorCode;
                if (errorCode == ErrorCode.REFUSED_STREAM) {
                    int i = this.refusedStreamCount + 1;
                    this.refusedStreamCount = i;
                    if (i > 1) {
                        noNewStreams = true;
                        this.route = null;
                    }
                } else if (errorCode != ErrorCode.CANCEL) {
                    noNewStreams = true;
                    this.route = null;
                }
            } else if (this.connection != null) {
                if (!this.connection.isMultiplexed() || (e instanceof ConnectionShutdownException)) {
                    noNewStreams = true;
                    if (this.connection.successCount == 0) {
                        if (!(this.route == null || e == null)) {
                            this.routeSelector.connectFailed(this.route, e);
                        }
                        this.route = null;
                    }
                }
                releasedConnection = this.connection;
                socket = deallocate(noNewStreams, false, true);
                if (this.connection != null || !this.reportedAcquired) {
                    releasedConnection = null;
                }
            }
            releasedConnection = this.connection;
            socket = deallocate(noNewStreams, false, true);
            releasedConnection = null;
        }
        Util.closeQuietly(socket);
        if (releasedConnection != null) {
            this.eventListener.connectionReleased(this.call, releasedConnection);
        }
    }

    public void acquire(RealConnection connection2, boolean reportedAcquired2) {
        if (this.connection == null) {
            this.connection = connection2;
            this.reportedAcquired = reportedAcquired2;
            connection2.allocations.add(new StreamAllocationReference(this, this.callStackTrace));
            return;
        }
        throw new IllegalStateException();
    }

    private void release(RealConnection connection2) {
        int size = connection2.allocations.size();
        for (int i = 0; i < size; i++) {
            if (connection2.allocations.get(i).get() == this) {
                connection2.allocations.remove(i);
                return;
            }
        }
        throw new IllegalStateException();
    }

    public Socket releaseAndAcquire(RealConnection newConnection) {
        if (this.codec == null && this.connection.allocations.size() == 1) {
            Socket socket = deallocate(true, false, false);
            this.connection = newConnection;
            newConnection.allocations.add(this.connection.allocations.get(0));
            return socket;
        }
        throw new IllegalStateException();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
        r0 = r1.routeSelection;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean hasMoreRoutes() {
        /*
            r1 = this;
            okhttp3.Route r0 = r1.route
            if (r0 != 0) goto L_0x0019
            okhttp3.internal.connection.RouteSelector$Selection r0 = r1.routeSelection
            if (r0 == 0) goto L_0x000e
            boolean r0 = r0.hasNext()
            if (r0 != 0) goto L_0x0019
        L_0x000e:
            okhttp3.internal.connection.RouteSelector r0 = r1.routeSelector
            boolean r0 = r0.hasNext()
            if (r0 == 0) goto L_0x0017
            goto L_0x0019
        L_0x0017:
            r0 = 0
            goto L_0x001a
        L_0x0019:
            r0 = 1
        L_0x001a:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.connection.StreamAllocation.hasMoreRoutes():boolean");
    }

    public String toString() {
        RealConnection connection2 = connection();
        return connection2 != null ? connection2.toString() : this.address.toString();
    }

    public static final class StreamAllocationReference extends WeakReference<StreamAllocation> {
        public final Object callStackTrace;

        StreamAllocationReference(StreamAllocation referent, Object callStackTrace2) {
            super(referent);
            this.callStackTrace = callStackTrace2;
        }
    }
}
