package com.google.firebase.remoteconfig.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.RemoteConfigComponent;
import com.google.firebase.remoteconfig.internal.ConfigContainer;
import com.google.firebase.remoteconfig.proto.ConfigPersistence;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import developers.mobile.abt.FirebaseAbt;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* compiled from: com.google.firebase:firebase-config@@19.1.0 */
public class LegacyConfigsHandler {
    static final String ACTIVATE_FILE_NAME = "activate";
    static final String DEFAULTS_FILE_NAME = "defaults";
    public static final String EXPERIMENT_ID_KEY = "experimentId";
    public static final String EXPERIMENT_START_TIME_KEY = "experimentStartTime";
    public static final String EXPERIMENT_TIME_TO_LIVE_KEY = "timeToLiveMillis";
    public static final String EXPERIMENT_TRIGGER_EVENT_KEY = "triggerEvent";
    public static final String EXPERIMENT_TRIGGER_TIMEOUT_KEY = "triggerTimeoutMillis";
    public static final String EXPERIMENT_VARIANT_ID_KEY = "variantId";
    static final String FETCH_FILE_NAME = "fetch";
    private static final String FRC_3P_NAMESPACE = "firebase";
    static final String LEGACY_CONFIGS_FILE_NAME = "persisted_config";
    static final String LEGACY_FRC_NAMESPACE_PREFIX = "configns:";
    private static final String LEGACY_SETTINGS_FILE_NAME = "com.google.firebase.remoteconfig_legacy_settings";
    private static final Charset PROTO_BYTE_ARRAY_ENCODING = Charset.forName("UTF-8");
    private static final String SAVE_LEGACY_CONFIGS_FLAG_NAME = "save_legacy_configs";
    static final ThreadLocal<DateFormat> protoTimestampStringParser = new ThreadLocal<DateFormat>() {
        /* access modifiers changed from: protected */
        public DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        }
    };
    private final String appId;
    private final Context context;
    private final SharedPreferences legacySettings;

    public LegacyConfigsHandler(Context context2, String appId2) {
        this.context = context2;
        this.appId = appId2;
        this.legacySettings = context2.getSharedPreferences(LEGACY_SETTINGS_FILE_NAME, 0);
    }

    public boolean saveLegacyConfigsIfNecessary() {
        if (!this.legacySettings.getBoolean(SAVE_LEGACY_CONFIGS_FLAG_NAME, true)) {
            return false;
        }
        saveLegacyConfigs(getConvertedLegacyConfigs());
        this.legacySettings.edit().putBoolean(SAVE_LEGACY_CONFIGS_FLAG_NAME, false).commit();
        return true;
    }

    private void saveLegacyConfigs(Map<String, NamespaceLegacyConfigs> legacyConfigsByNamespace) {
        for (Map.Entry<String, NamespaceLegacyConfigs> legacyConfigsByNamespaceEntry : legacyConfigsByNamespace.entrySet()) {
            String namespace = legacyConfigsByNamespaceEntry.getKey();
            NamespaceLegacyConfigs legacyConfigs = legacyConfigsByNamespaceEntry.getValue();
            ConfigCacheClient fetchedCacheClient = getCacheClient(namespace, "fetch");
            ConfigCacheClient activatedCacheClient = getCacheClient(namespace, "activate");
            ConfigCacheClient defaultsCacheClient = getCacheClient(namespace, "defaults");
            if (legacyConfigs.getFetchedConfigs() != null) {
                fetchedCacheClient.put(legacyConfigs.getFetchedConfigs());
            }
            if (legacyConfigs.getActivatedConfigs() != null) {
                activatedCacheClient.put(legacyConfigs.getActivatedConfigs());
            }
            if (legacyConfigs.getDefaultsConfigs() != null) {
                defaultsCacheClient.put(legacyConfigs.getDefaultsConfigs());
            }
        }
    }

    private Map<String, NamespaceLegacyConfigs> getConvertedLegacyConfigs() {
        ConfigPersistence.PersistedConfig allLegacyConfigs = readPersistedConfig();
        Map<String, NamespaceLegacyConfigs> allConfigsMap = new HashMap<>();
        if (allLegacyConfigs == null) {
            return allConfigsMap;
        }
        Map<String, ConfigContainer> activatedConfigsByNamespace = convertConfigHolder(allLegacyConfigs.getActiveConfigHolder());
        Map<String, ConfigContainer> fetchedConfigsByNamespace = convertConfigHolder(allLegacyConfigs.getFetchedConfigHolder());
        Map<String, ConfigContainer> defaultsConfigsByNamespace = convertConfigHolder(allLegacyConfigs.getDefaultsConfigHolder());
        Set<String> allNamespaces = new HashSet<>();
        allNamespaces.addAll(activatedConfigsByNamespace.keySet());
        allNamespaces.addAll(fetchedConfigsByNamespace.keySet());
        allNamespaces.addAll(defaultsConfigsByNamespace.keySet());
        for (String namespace : allNamespaces) {
            NamespaceLegacyConfigs namespaceLegacyConfigs = new NamespaceLegacyConfigs();
            if (activatedConfigsByNamespace.containsKey(namespace)) {
                namespaceLegacyConfigs.setActivatedConfigs(activatedConfigsByNamespace.get(namespace));
            }
            if (fetchedConfigsByNamespace.containsKey(namespace)) {
                namespaceLegacyConfigs.setFetchedConfigs(fetchedConfigsByNamespace.get(namespace));
            }
            if (defaultsConfigsByNamespace.containsKey(namespace)) {
                namespaceLegacyConfigs.setDefaultsConfigs(defaultsConfigsByNamespace.get(namespace));
            }
            allConfigsMap.put(namespace, namespaceLegacyConfigs);
        }
        return allConfigsMap;
    }

    private Map<String, ConfigContainer> convertConfigHolder(ConfigPersistence.ConfigHolder allNamespaceLegacyConfigs) {
        Map<String, ConfigContainer> convertedLegacyConfigs = new HashMap<>();
        Date fetchTime = new Date(allNamespaceLegacyConfigs.getTimestamp());
        JSONArray abtExperiments = convertLegacyAbtExperiments(allNamespaceLegacyConfigs.getExperimentPayloadList());
        for (ConfigPersistence.NamespaceKeyValue namespaceLegacyConfigs : allNamespaceLegacyConfigs.getNamespaceKeyValueList()) {
            String namespace = namespaceLegacyConfigs.getNamespace();
            if (namespace.startsWith(LEGACY_FRC_NAMESPACE_PREFIX)) {
                namespace = namespace.substring(LEGACY_FRC_NAMESPACE_PREFIX.length());
            }
            ConfigContainer.Builder configsBuilder = ConfigContainer.newBuilder().replaceConfigsWith(convertKeyValueList(namespaceLegacyConfigs.getKeyValueList())).withFetchTime(fetchTime);
            if (namespace.equals("firebase")) {
                configsBuilder.withAbtExperiments(abtExperiments);
            }
            try {
                convertedLegacyConfigs.put(namespace, configsBuilder.build());
            } catch (JSONException e) {
                Log.d(FirebaseRemoteConfig.TAG, "A set of legacy configs could not be converted.");
            }
        }
        return convertedLegacyConfigs;
    }

    private JSONArray convertLegacyAbtExperiments(List<ByteString> legacyExperimentPayloads) {
        JSONArray abtExperiments = new JSONArray();
        for (ByteString legacyExperimentPayload : legacyExperimentPayloads) {
            FirebaseAbt.ExperimentPayload deserializedPayload = deserializePayload(legacyExperimentPayload);
            if (deserializedPayload != null) {
                try {
                    abtExperiments.put(convertLegacyAbtExperiment(deserializedPayload));
                } catch (JSONException e) {
                    Log.d(FirebaseRemoteConfig.TAG, "A legacy ABT experiment could not be parsed.", e);
                }
            }
        }
        return abtExperiments;
    }

    private FirebaseAbt.ExperimentPayload deserializePayload(ByteString legacyExperimentPayload) {
        try {
            Iterator<Byte> byteIterator = legacyExperimentPayload.iterator();
            byte[] payloadArray = new byte[legacyExperimentPayload.size()];
            for (int index = 0; index < payloadArray.length; index++) {
                payloadArray[index] = byteIterator.next().byteValue();
            }
            return FirebaseAbt.ExperimentPayload.parseFrom(payloadArray);
        } catch (InvalidProtocolBufferException e) {
            Log.d(FirebaseRemoteConfig.TAG, "Payload was not defined or could not be deserialized.", e);
            return null;
        }
    }

    private JSONObject convertLegacyAbtExperiment(FirebaseAbt.ExperimentPayload deserializedLegacyPayload) throws JSONException {
        JSONObject abtExperiment = new JSONObject();
        abtExperiment.put("experimentId", deserializedLegacyPayload.getExperimentId());
        abtExperiment.put("variantId", deserializedLegacyPayload.getVariantId());
        abtExperiment.put(EXPERIMENT_START_TIME_KEY, protoTimestampStringParser.get().format(new Date(deserializedLegacyPayload.getExperimentStartTimeMillis())));
        abtExperiment.put(EXPERIMENT_TRIGGER_EVENT_KEY, deserializedLegacyPayload.getTriggerEvent());
        abtExperiment.put(EXPERIMENT_TRIGGER_TIMEOUT_KEY, deserializedLegacyPayload.getTriggerTimeoutMillis());
        abtExperiment.put(EXPERIMENT_TIME_TO_LIVE_KEY, deserializedLegacyPayload.getTimeToLiveMillis());
        return abtExperiment;
    }

    private Map<String, String> convertKeyValueList(List<ConfigPersistence.KeyValue> legacyConfigs) {
        Map<String, String> legacyConfigsMap = new HashMap<>();
        for (ConfigPersistence.KeyValue legacyConfig : legacyConfigs) {
            legacyConfigsMap.put(legacyConfig.getKey(), legacyConfig.getValue().toString(PROTO_BYTE_ARRAY_ENCODING));
        }
        return legacyConfigsMap;
    }

    private ConfigPersistence.PersistedConfig readPersistedConfig() {
        Context context2 = this.context;
        if (context2 == null) {
            return null;
        }
        FileInputStream fileInputStream = null;
        try {
            FileInputStream fileInputStream2 = context2.openFileInput(LEGACY_CONFIGS_FILE_NAME);
            ConfigPersistence.PersistedConfig persistedConfig = ConfigPersistence.PersistedConfig.parseFrom((InputStream) fileInputStream2);
            if (fileInputStream2 != null) {
                try {
                    fileInputStream2.close();
                } catch (IOException ioException) {
                    Log.d(FirebaseRemoteConfig.TAG, "Failed to close persisted config file.", ioException);
                }
            }
            return persistedConfig;
        } catch (FileNotFoundException fileNotFoundException) {
            Log.d(FirebaseRemoteConfig.TAG, "Persisted config file was not found.", fileNotFoundException);
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException ioException2) {
                    Log.d(FirebaseRemoteConfig.TAG, "Failed to close persisted config file.", ioException2);
                }
            }
            return null;
        } catch (IOException ioException3) {
            Log.d(FirebaseRemoteConfig.TAG, "Cannot initialize from persisted config.", ioException3);
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException ioException4) {
                    Log.d(FirebaseRemoteConfig.TAG, "Failed to close persisted config file.", ioException4);
                }
            }
            return null;
        } catch (Throwable th) {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException ioException5) {
                    Log.d(FirebaseRemoteConfig.TAG, "Failed to close persisted config file.", ioException5);
                }
            }
            throw th;
        }
    }

    /* access modifiers changed from: package-private */
    public ConfigCacheClient getCacheClient(String namespace, String configStoreType) {
        return RemoteConfigComponent.getCacheClient(this.context, this.appId, namespace, configStoreType);
    }

    /* compiled from: com.google.firebase:firebase-config@@19.1.0 */
    private static class NamespaceLegacyConfigs {
        private ConfigContainer activatedConfigs;
        private ConfigContainer defaultsConfigs;
        private ConfigContainer fetchedConfigs;

        private NamespaceLegacyConfigs() {
        }

        /* access modifiers changed from: private */
        public void setFetchedConfigs(ConfigContainer fetchedConfigs2) {
            this.fetchedConfigs = fetchedConfigs2;
        }

        /* access modifiers changed from: private */
        public void setActivatedConfigs(ConfigContainer activatedConfigs2) {
            this.activatedConfigs = activatedConfigs2;
        }

        /* access modifiers changed from: private */
        public void setDefaultsConfigs(ConfigContainer defaultsConfigs2) {
            this.defaultsConfigs = defaultsConfigs2;
        }

        /* access modifiers changed from: private */
        public ConfigContainer getFetchedConfigs() {
            return this.fetchedConfigs;
        }

        /* access modifiers changed from: private */
        public ConfigContainer getActivatedConfigs() {
            return this.activatedConfigs;
        }

        /* access modifiers changed from: private */
        public ConfigContainer getDefaultsConfigs() {
            return this.defaultsConfigs;
        }
    }
}
