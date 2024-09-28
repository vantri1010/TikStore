package com.google.firebase.remoteconfig.internal;

import java.util.Date;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* compiled from: com.google.firebase:firebase-config@@19.1.0 */
public class ConfigContainer {
    private static final String ABT_EXPERIMENTS_KEY = "abt_experiments_key";
    private static final String CONFIGS_KEY = "configs_key";
    /* access modifiers changed from: private */
    public static final Date DEFAULTS_FETCH_TIME = new Date(0);
    private static final String FETCH_TIME_KEY = "fetch_time_key";
    private JSONArray abtExperiments;
    private JSONObject configsJson;
    private JSONObject containerJson;
    private Date fetchTime;

    private ConfigContainer(JSONObject configsJson2, Date fetchTime2, JSONArray abtExperiments2) throws JSONException {
        JSONObject containerJson2 = new JSONObject();
        containerJson2.put(CONFIGS_KEY, configsJson2);
        containerJson2.put(FETCH_TIME_KEY, fetchTime2.getTime());
        containerJson2.put(ABT_EXPERIMENTS_KEY, abtExperiments2);
        this.configsJson = configsJson2;
        this.fetchTime = fetchTime2;
        this.abtExperiments = abtExperiments2;
        this.containerJson = containerJson2;
    }

    static ConfigContainer copyOf(JSONObject containerJson2) throws JSONException {
        return new ConfigContainer(containerJson2.getJSONObject(CONFIGS_KEY), new Date(containerJson2.getLong(FETCH_TIME_KEY)), containerJson2.getJSONArray(ABT_EXPERIMENTS_KEY));
    }

    public JSONObject getConfigs() {
        return this.configsJson;
    }

    public Date getFetchTime() {
        return this.fetchTime;
    }

    public JSONArray getAbtExperiments() {
        return this.abtExperiments;
    }

    public String toString() {
        return this.containerJson.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConfigContainer)) {
            return false;
        }
        return this.containerJson.toString().equals(((ConfigContainer) o).toString());
    }

    public int hashCode() {
        return this.containerJson.hashCode();
    }

    /* compiled from: com.google.firebase:firebase-config@@19.1.0 */
    public static class Builder {
        private JSONArray builderAbtExperiments;
        private JSONObject builderConfigsJson;
        private Date builderFetchTime;

        private Builder() {
            this.builderConfigsJson = new JSONObject();
            this.builderFetchTime = ConfigContainer.DEFAULTS_FETCH_TIME;
            this.builderAbtExperiments = new JSONArray();
        }

        public Builder(ConfigContainer otherContainer) {
            this.builderConfigsJson = otherContainer.getConfigs();
            this.builderFetchTime = otherContainer.getFetchTime();
            this.builderAbtExperiments = otherContainer.getAbtExperiments();
        }

        public Builder replaceConfigsWith(Map<String, String> configsMap) {
            this.builderConfigsJson = new JSONObject(configsMap);
            return this;
        }

        public Builder replaceConfigsWith(JSONObject configsJson) {
            try {
                this.builderConfigsJson = new JSONObject(configsJson.toString());
            } catch (JSONException e) {
            }
            return this;
        }

        public Builder withFetchTime(Date fetchTime) {
            this.builderFetchTime = fetchTime;
            return this;
        }

        public Builder withAbtExperiments(JSONArray abtExperiments) {
            try {
                this.builderAbtExperiments = new JSONArray(abtExperiments.toString());
            } catch (JSONException e) {
            }
            return this;
        }

        public ConfigContainer build() throws JSONException {
            return new ConfigContainer(this.builderConfigsJson, this.builderFetchTime, this.builderAbtExperiments);
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(ConfigContainer otherContainer) {
        return new Builder(otherContainer);
    }
}
