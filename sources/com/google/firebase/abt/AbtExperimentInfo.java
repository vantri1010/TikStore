package com.google.firebase.abt;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/* compiled from: com.google.firebase:firebase-abt@@19.0.0 */
public class AbtExperimentInfo {
    private static final String[] ALL_REQUIRED_KEYS = {"experimentId", "experimentStartTime", "timeToLiveMillis", "triggerTimeoutMillis", "variantId"};
    static final String EXPERIMENT_ID_KEY = "experimentId";
    static final String EXPERIMENT_START_TIME_KEY = "experimentStartTime";
    static final String TIME_TO_LIVE_KEY = "timeToLiveMillis";
    static final String TRIGGER_EVENT_KEY = "triggerEvent";
    static final String TRIGGER_TIMEOUT_KEY = "triggerTimeoutMillis";
    static final String VARIANT_ID_KEY = "variantId";
    static final DateFormat protoTimestampStringParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
    private final String experimentId;
    private final Date experimentStartTime;
    private final long timeToLiveInMillis;
    private final String triggerEventName;
    private final long triggerTimeoutInMillis;
    private final String variantId;

    AbtExperimentInfo(String experimentId2, String variantId2, String triggerEventName2, Date experimentStartTime2, long triggerTimeoutInMillis2, long timeToLiveInMillis2) {
        this.experimentId = experimentId2;
        this.variantId = variantId2;
        this.triggerEventName = triggerEventName2;
        this.experimentStartTime = experimentStartTime2;
        this.triggerTimeoutInMillis = triggerTimeoutInMillis2;
        this.timeToLiveInMillis = timeToLiveInMillis2;
    }

    static AbtExperimentInfo fromMap(Map<String, String> experimentInfoMap) throws AbtException {
        String str;
        validateExperimentInfoMap(experimentInfoMap);
        try {
            Date experimentStartTime2 = protoTimestampStringParser.parse(experimentInfoMap.get("experimentStartTime"));
            long triggerTimeoutInMillis2 = Long.parseLong(experimentInfoMap.get("triggerTimeoutMillis"));
            long timeToLiveInMillis2 = Long.parseLong(experimentInfoMap.get("timeToLiveMillis"));
            String str2 = experimentInfoMap.get("experimentId");
            String str3 = experimentInfoMap.get("variantId");
            if (experimentInfoMap.containsKey("triggerEvent")) {
                str = experimentInfoMap.get("triggerEvent");
            } else {
                str = "";
            }
            return new AbtExperimentInfo(str2, str3, str, experimentStartTime2, triggerTimeoutInMillis2, timeToLiveInMillis2);
        } catch (ParseException e) {
            throw new AbtException("Could not process experiment: parsing experiment start time failed.", e);
        } catch (NumberFormatException e2) {
            throw new AbtException("Could not process experiment: one of the durations could not be converted into a long.", e2);
        }
    }

    /* access modifiers changed from: package-private */
    public String getExperimentId() {
        return this.experimentId;
    }

    /* access modifiers changed from: package-private */
    public String getVariantId() {
        return this.variantId;
    }

    /* access modifiers changed from: package-private */
    public String getTriggerEventName() {
        return this.triggerEventName;
    }

    /* access modifiers changed from: package-private */
    public long getStartTimeInMillisSinceEpoch() {
        return this.experimentStartTime.getTime();
    }

    /* access modifiers changed from: package-private */
    public long getTriggerTimeoutInMillis() {
        return this.triggerTimeoutInMillis;
    }

    /* access modifiers changed from: package-private */
    public long getTimeToLiveInMillis() {
        return this.timeToLiveInMillis;
    }

    private static void validateExperimentInfoMap(Map<String, String> experimentInfoMap) throws AbtException {
        List<String> missingKeys = new ArrayList<>();
        for (String key : ALL_REQUIRED_KEYS) {
            if (!experimentInfoMap.containsKey(key)) {
                missingKeys.add(key);
            }
        }
        if (!missingKeys.isEmpty()) {
            throw new AbtException(String.format("The following keys are missing from the experiment info map: %s", new Object[]{missingKeys}));
        }
    }

    /* access modifiers changed from: package-private */
    public Map<String, String> toStringMap() {
        Map<String, String> experimentInfoMap = new HashMap<>();
        experimentInfoMap.put("experimentId", this.experimentId);
        experimentInfoMap.put("variantId", this.variantId);
        experimentInfoMap.put("triggerEvent", this.triggerEventName);
        experimentInfoMap.put("experimentStartTime", protoTimestampStringParser.format(this.experimentStartTime));
        experimentInfoMap.put("triggerTimeoutMillis", Long.toString(this.triggerTimeoutInMillis));
        experimentInfoMap.put("timeToLiveMillis", Long.toString(this.timeToLiveInMillis));
        return experimentInfoMap;
    }
}
