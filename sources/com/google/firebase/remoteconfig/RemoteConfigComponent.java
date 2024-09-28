package com.google.firebase.remoteconfig;

import android.content.Context;
import com.google.android.gms.common.util.Clock;
import com.google.android.gms.common.util.DefaultClock;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.abt.FirebaseABTesting;
import com.google.firebase.analytics.connector.AnalyticsConnector;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.remoteconfig.internal.ConfigCacheClient;
import com.google.firebase.remoteconfig.internal.ConfigFetchHandler;
import com.google.firebase.remoteconfig.internal.ConfigFetchHttpClient;
import com.google.firebase.remoteconfig.internal.ConfigGetParameterHandler;
import com.google.firebase.remoteconfig.internal.ConfigMetadataClient;
import com.google.firebase.remoteconfig.internal.ConfigStorageClient;
import com.google.firebase.remoteconfig.internal.LegacyConfigsHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* compiled from: com.google.firebase:firebase-config@@19.1.0 */
public class RemoteConfigComponent {
    public static final String ACTIVATE_FILE_NAME = "activate";
    public static final String DEFAULTS_FILE_NAME = "defaults";
    private static final Clock DEFAULT_CLOCK = DefaultClock.getInstance();
    public static final String DEFAULT_NAMESPACE = "firebase";
    private static final Random DEFAULT_RANDOM = new Random();
    public static final String FETCH_FILE_NAME = "fetch";
    private static final String FIREBASE_REMOTE_CONFIG_FILE_NAME_PREFIX = "frc";
    public static final long NETWORK_CONNECTION_TIMEOUT_IN_SECONDS = 60;
    private static final String PREFERENCES_FILE_NAME = "settings";
    private final AnalyticsConnector analyticsConnector;
    private final String appId;
    private final Context context;
    private Map<String, String> customHeaders;
    private final ExecutorService executorService;
    private final FirebaseABTesting firebaseAbt;
    private final FirebaseApp firebaseApp;
    private final FirebaseInstanceId firebaseInstanceId;
    private final Map<String, FirebaseRemoteConfig> frcNamespaceInstances;

    RemoteConfigComponent(Context context2, FirebaseApp firebaseApp2, FirebaseInstanceId firebaseInstanceId2, FirebaseABTesting firebaseAbt2, AnalyticsConnector analyticsConnector2) {
        this(context2, Executors.newCachedThreadPool(), firebaseApp2, firebaseInstanceId2, firebaseAbt2, analyticsConnector2, new LegacyConfigsHandler(context2, firebaseApp2.getOptions().getApplicationId()), true);
    }

    protected RemoteConfigComponent(Context context2, ExecutorService executorService2, FirebaseApp firebaseApp2, FirebaseInstanceId firebaseInstanceId2, FirebaseABTesting firebaseAbt2, AnalyticsConnector analyticsConnector2, LegacyConfigsHandler legacyConfigsHandler, boolean loadGetDefault) {
        this.frcNamespaceInstances = new HashMap();
        this.customHeaders = new HashMap();
        this.context = context2;
        this.executorService = executorService2;
        this.firebaseApp = firebaseApp2;
        this.firebaseInstanceId = firebaseInstanceId2;
        this.firebaseAbt = firebaseAbt2;
        this.analyticsConnector = analyticsConnector2;
        this.appId = firebaseApp2.getOptions().getApplicationId();
        if (loadGetDefault) {
            Tasks.call(executorService2, RemoteConfigComponent$$Lambda$1.lambdaFactory$(this));
            legacyConfigsHandler.getClass();
            Tasks.call(executorService2, RemoteConfigComponent$$Lambda$4.lambdaFactory$(legacyConfigsHandler));
        }
    }

    /* access modifiers changed from: package-private */
    public FirebaseRemoteConfig getDefault() {
        return get(DEFAULT_NAMESPACE);
    }

    public synchronized FirebaseRemoteConfig get(String namespace) {
        FirebaseRemoteConfig firebaseRemoteConfig;
        String str = namespace;
        synchronized (this) {
            ConfigCacheClient fetchedCacheClient = getCacheClient(str, FETCH_FILE_NAME);
            ConfigCacheClient activatedCacheClient = getCacheClient(str, ACTIVATE_FILE_NAME);
            ConfigCacheClient defaultsCacheClient = getCacheClient(str, DEFAULTS_FILE_NAME);
            ConfigMetadataClient metadataClient = getMetadataClient(this.context, this.appId, str);
            ConfigMetadataClient configMetadataClient = metadataClient;
            firebaseRemoteConfig = get(this.firebaseApp, namespace, this.firebaseAbt, this.executorService, fetchedCacheClient, activatedCacheClient, defaultsCacheClient, getFetchHandler(str, fetchedCacheClient, metadataClient), getGetHandler(activatedCacheClient, defaultsCacheClient), metadataClient);
        }
        return firebaseRemoteConfig;
    }

    /* access modifiers changed from: package-private */
    public synchronized FirebaseRemoteConfig get(FirebaseApp firebaseApp2, String namespace, FirebaseABTesting firebaseAbt2, Executor executor, ConfigCacheClient fetchedClient, ConfigCacheClient activatedClient, ConfigCacheClient defaultsClient, ConfigFetchHandler fetchHandler, ConfigGetParameterHandler getHandler, ConfigMetadataClient metadataClient) {
        FirebaseRemoteConfig firebaseRemoteConfig;
        String str = namespace;
        synchronized (this) {
            if (!this.frcNamespaceInstances.containsKey(str)) {
                FirebaseRemoteConfig firebaseRemoteConfig2 = new FirebaseRemoteConfig(this.context, firebaseApp2, isAbtSupported(firebaseApp2, namespace) ? firebaseAbt2 : null, executor, fetchedClient, activatedClient, defaultsClient, fetchHandler, getHandler, metadataClient);
                firebaseRemoteConfig2.startLoadingConfigsFromDisk();
                this.frcNamespaceInstances.put(str, firebaseRemoteConfig2);
            }
            firebaseRemoteConfig = this.frcNamespaceInstances.get(str);
        }
        return firebaseRemoteConfig;
    }

    public synchronized void setCustomHeaders(Map<String, String> customHeaders2) {
        this.customHeaders = customHeaders2;
    }

    private ConfigCacheClient getCacheClient(String namespace, String configStoreType) {
        return getCacheClient(this.context, this.appId, namespace, configStoreType);
    }

    public static ConfigCacheClient getCacheClient(Context context2, String appId2, String namespace, String configStoreType) {
        return ConfigCacheClient.getInstance(Executors.newCachedThreadPool(), ConfigStorageClient.getInstance(context2, String.format("%s_%s_%s_%s.json", new Object[]{"frc", appId2, namespace, configStoreType})));
    }

    /* access modifiers changed from: package-private */
    public ConfigFetchHttpClient getFrcBackendApiClient(String apiKey, String namespace, ConfigMetadataClient metadataClient) {
        return new ConfigFetchHttpClient(this.context, this.firebaseApp.getOptions().getApplicationId(), apiKey, namespace, metadataClient.getFetchTimeoutInSeconds(), 60);
    }

    /* access modifiers changed from: package-private */
    public synchronized ConfigFetchHandler getFetchHandler(String namespace, ConfigCacheClient fetchedCacheClient, ConfigMetadataClient metadataClient) {
        return new ConfigFetchHandler(this.firebaseInstanceId, isPrimaryApp(this.firebaseApp) ? this.analyticsConnector : null, this.executorService, DEFAULT_CLOCK, DEFAULT_RANDOM, fetchedCacheClient, getFrcBackendApiClient(this.firebaseApp.getOptions().getApiKey(), namespace, metadataClient), metadataClient, this.customHeaders);
    }

    private ConfigGetParameterHandler getGetHandler(ConfigCacheClient activatedCacheClient, ConfigCacheClient defaultsCacheClient) {
        return new ConfigGetParameterHandler(activatedCacheClient, defaultsCacheClient);
    }

    static ConfigMetadataClient getMetadataClient(Context context2, String appId2, String namespace) {
        return new ConfigMetadataClient(context2.getSharedPreferences(String.format("%s_%s_%s_%s", new Object[]{"frc", appId2, namespace, PREFERENCES_FILE_NAME}), 0));
    }

    private static boolean isAbtSupported(FirebaseApp firebaseApp2, String namespace) {
        return namespace.equals(DEFAULT_NAMESPACE) && isPrimaryApp(firebaseApp2);
    }

    private static boolean isPrimaryApp(FirebaseApp firebaseApp2) {
        return firebaseApp2.getName().equals(FirebaseApp.DEFAULT_APP_NAME);
    }
}
