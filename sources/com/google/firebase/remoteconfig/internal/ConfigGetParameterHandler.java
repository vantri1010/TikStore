package com.google.firebase.remoteconfig.internal;

import android.util.Log;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import org.json.JSONException;

/* compiled from: com.google.firebase:firebase-config@@19.1.0 */
public class ConfigGetParameterHandler {
    static final Pattern FALSE_REGEX = Pattern.compile("^(0|false|f|no|n|off|)$", 2);
    public static final Charset FRC_BYTE_ARRAY_ENCODING = Charset.forName("UTF-8");
    static final Pattern TRUE_REGEX = Pattern.compile("^(1|true|t|yes|y|on)$", 2);
    private final ConfigCacheClient activatedConfigsCache;
    private final ConfigCacheClient defaultConfigsCache;

    public ConfigGetParameterHandler(ConfigCacheClient activatedConfigsCache2, ConfigCacheClient defaultConfigsCache2) {
        this.activatedConfigsCache = activatedConfigsCache2;
        this.defaultConfigsCache = defaultConfigsCache2;
    }

    public String getString(String key) {
        String activatedString = getStringFromCache(this.activatedConfigsCache, key);
        if (activatedString != null) {
            return activatedString;
        }
        String defaultsString = getStringFromCache(this.defaultConfigsCache, key);
        if (defaultsString != null) {
            return defaultsString;
        }
        logParameterValueDoesNotExist(key, "String");
        return "";
    }

    public boolean getBoolean(String key) {
        String activatedString = getStringFromCache(this.activatedConfigsCache, key);
        if (activatedString != null) {
            if (TRUE_REGEX.matcher(activatedString).matches()) {
                return true;
            }
            if (FALSE_REGEX.matcher(activatedString).matches()) {
                return false;
            }
        }
        String defaultsString = getStringFromCache(this.defaultConfigsCache, key);
        if (defaultsString != null) {
            if (TRUE_REGEX.matcher(defaultsString).matches()) {
                return true;
            }
            if (FALSE_REGEX.matcher(defaultsString).matches()) {
                return false;
            }
        }
        logParameterValueDoesNotExist(key, "Boolean");
        return false;
    }

    public byte[] getByteArray(String key) {
        String activatedString = getStringFromCache(this.activatedConfigsCache, key);
        if (activatedString != null) {
            return activatedString.getBytes(FRC_BYTE_ARRAY_ENCODING);
        }
        String defaultsString = getStringFromCache(this.defaultConfigsCache, key);
        if (defaultsString != null) {
            return defaultsString.getBytes(FRC_BYTE_ARRAY_ENCODING);
        }
        logParameterValueDoesNotExist(key, "ByteArray");
        return FirebaseRemoteConfig.DEFAULT_VALUE_FOR_BYTE_ARRAY;
    }

    public double getDouble(String key) {
        Double activatedDouble = getDoubleFromCache(this.activatedConfigsCache, key);
        if (activatedDouble != null) {
            return activatedDouble.doubleValue();
        }
        Double defaultsDouble = getDoubleFromCache(this.defaultConfigsCache, key);
        if (defaultsDouble != null) {
            return defaultsDouble.doubleValue();
        }
        logParameterValueDoesNotExist(key, "Double");
        return FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
    }

    public long getLong(String key) {
        Long activatedLong = getLongFromCache(this.activatedConfigsCache, key);
        if (activatedLong != null) {
            return activatedLong.longValue();
        }
        Long defaultsLong = getLongFromCache(this.defaultConfigsCache, key);
        if (defaultsLong != null) {
            return defaultsLong.longValue();
        }
        logParameterValueDoesNotExist(key, "Long");
        return 0;
    }

    public FirebaseRemoteConfigValue getValue(String key) {
        String activatedString = getStringFromCache(this.activatedConfigsCache, key);
        if (activatedString != null) {
            return new FirebaseRemoteConfigValueImpl(activatedString, 2);
        }
        String defaultsString = getStringFromCache(this.defaultConfigsCache, key);
        if (defaultsString != null) {
            return new FirebaseRemoteConfigValueImpl(defaultsString, 1);
        }
        logParameterValueDoesNotExist(key, "FirebaseRemoteConfigValue");
        return new FirebaseRemoteConfigValueImpl("", 0);
    }

    public Set<String> getKeysByPrefix(String prefix) {
        if (prefix == null) {
            prefix = "";
        }
        TreeSet<String> keysWithPrefix = new TreeSet<>();
        ConfigContainer activatedConfigs = getConfigsFromCache(this.activatedConfigsCache);
        if (activatedConfigs != null) {
            keysWithPrefix.addAll(getKeysByPrefix(prefix, activatedConfigs));
        }
        ConfigContainer defaultsConfigs = getConfigsFromCache(this.defaultConfigsCache);
        if (defaultsConfigs != null) {
            keysWithPrefix.addAll(getKeysByPrefix(prefix, defaultsConfigs));
        }
        return keysWithPrefix;
    }

    private static TreeSet<String> getKeysByPrefix(String prefix, ConfigContainer configs) {
        TreeSet<String> keysWithPrefix = new TreeSet<>();
        Iterator<String> stringIterator = configs.getConfigs().keys();
        while (stringIterator.hasNext()) {
            String currentKey = stringIterator.next();
            if (currentKey.startsWith(prefix)) {
                keysWithPrefix.add(currentKey);
            }
        }
        return keysWithPrefix;
    }

    public Map<String, FirebaseRemoteConfigValue> getAll() {
        Set<String> keySet = new HashSet<>();
        keySet.addAll(getKeySetFromCache(this.activatedConfigsCache));
        keySet.addAll(getKeySetFromCache(this.defaultConfigsCache));
        HashMap<String, FirebaseRemoteConfigValue> allConfigs = new HashMap<>();
        for (String key : keySet) {
            allConfigs.put(key, getValue(key));
        }
        return allConfigs;
    }

    private static String getStringFromCache(ConfigCacheClient cacheClient, String key) {
        ConfigContainer cachedContainer = getConfigsFromCache(cacheClient);
        if (cachedContainer == null) {
            return null;
        }
        try {
            return cachedContainer.getConfigs().getString(key);
        } catch (JSONException e) {
            return null;
        }
    }

    private static Double getDoubleFromCache(ConfigCacheClient cacheClient, String key) {
        ConfigContainer cachedContainer = getConfigsFromCache(cacheClient);
        if (cachedContainer == null) {
            return null;
        }
        try {
            return Double.valueOf(cachedContainer.getConfigs().getDouble(key));
        } catch (JSONException e) {
            return null;
        }
    }

    private static Long getLongFromCache(ConfigCacheClient cacheClient, String key) {
        ConfigContainer cachedContainer = getConfigsFromCache(cacheClient);
        if (cachedContainer == null) {
            return null;
        }
        try {
            return Long.valueOf(cachedContainer.getConfigs().getLong(key));
        } catch (JSONException e) {
            return null;
        }
    }

    private static Set<String> getKeySetFromCache(ConfigCacheClient cacheClient) {
        Set<String> keySet = new HashSet<>();
        ConfigContainer configContainer = getConfigsFromCache(cacheClient);
        if (configContainer == null) {
            return keySet;
        }
        Iterator<String> keyIterator = configContainer.getConfigs().keys();
        while (keyIterator.hasNext()) {
            keySet.add(keyIterator.next());
        }
        return keySet;
    }

    private static ConfigContainer getConfigsFromCache(ConfigCacheClient cacheClient) {
        return cacheClient.getBlocking();
    }

    private static void logParameterValueDoesNotExist(String key, String valueType) {
        Log.w(FirebaseRemoteConfig.TAG, String.format("No value of type '%s' exists for parameter key '%s'.", new Object[]{valueType, key}));
    }
}
