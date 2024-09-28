package com.google.firebase.abt;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import com.google.firebase.analytics.connector.AnalyticsConnector;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* compiled from: com.google.firebase:firebase-abt@@19.0.0 */
public class FirebaseABTesting {
    static final String ABT_PREFERENCES = "com.google.firebase.abt";
    static final String ORIGIN_LAST_KNOWN_START_TIME_KEY_FORMAT = "%s_lastKnownExperimentStartTime";
    private final AnalyticsConnector analyticsConnector;
    private Integer maxUserProperties = null;
    private final String originService;

    @Retention(RetentionPolicy.SOURCE)
    /* compiled from: com.google.firebase:firebase-abt@@19.0.0 */
    public @interface OriginService {
        public static final String REMOTE_CONFIG = "frc";
    }

    public FirebaseABTesting(Context unusedAppContext, AnalyticsConnector analyticsConnector2, String originService2) {
        this.analyticsConnector = analyticsConnector2;
        this.originService = originService2;
    }

    public void replaceAllExperiments(List<Map<String, String>> replacementExperiments) throws AbtException {
        throwAbtExceptionIfAnalyticsIsNull();
        if (replacementExperiments != null) {
            replaceAllExperimentsWith(convertMapsToExperimentInfos(replacementExperiments));
            return;
        }
        throw new IllegalArgumentException("The replacementExperiments list is null.");
    }

    public void removeAllExperiments() throws AbtException {
        throwAbtExceptionIfAnalyticsIsNull();
        removeExperiments(getAllExperimentsInAnalytics());
    }

    private void replaceAllExperimentsWith(List<AbtExperimentInfo> replacementExperiments) throws AbtException {
        if (replacementExperiments.isEmpty()) {
            removeAllExperiments();
            return;
        }
        Set<String> replacementExperimentIds = new HashSet<>();
        for (AbtExperimentInfo replacementExperiment : replacementExperiments) {
            replacementExperimentIds.add(replacementExperiment.getExperimentId());
        }
        List<AnalyticsConnector.ConditionalUserProperty> experimentsInAnalytics = getAllExperimentsInAnalytics();
        Set<String> idsOfExperimentsInAnalytics = new HashSet<>();
        for (AnalyticsConnector.ConditionalUserProperty experimentInAnalytics : experimentsInAnalytics) {
            idsOfExperimentsInAnalytics.add(experimentInAnalytics.name);
        }
        removeExperiments(getExperimentsToRemove(experimentsInAnalytics, replacementExperimentIds));
        addExperiments(getExperimentsToAdd(replacementExperiments, idsOfExperimentsInAnalytics));
    }

    private ArrayList<AnalyticsConnector.ConditionalUserProperty> getExperimentsToRemove(List<AnalyticsConnector.ConditionalUserProperty> experimentsInAnalytics, Set<String> replacementExperimentIds) {
        ArrayList<AnalyticsConnector.ConditionalUserProperty> experimentsToRemove = new ArrayList<>();
        for (AnalyticsConnector.ConditionalUserProperty experimentInAnalytics : experimentsInAnalytics) {
            if (!replacementExperimentIds.contains(experimentInAnalytics.name)) {
                experimentsToRemove.add(experimentInAnalytics);
            }
        }
        return experimentsToRemove;
    }

    private ArrayList<AbtExperimentInfo> getExperimentsToAdd(List<AbtExperimentInfo> replacementExperiments, Set<String> idsOfExperimentsInAnalytics) {
        ArrayList<AbtExperimentInfo> experimentsToAdd = new ArrayList<>();
        for (AbtExperimentInfo replacementExperiment : replacementExperiments) {
            if (!idsOfExperimentsInAnalytics.contains(replacementExperiment.getExperimentId())) {
                experimentsToAdd.add(replacementExperiment);
            }
        }
        return experimentsToAdd;
    }

    private void addExperiments(List<AbtExperimentInfo> experimentsToAdd) {
        Deque<AnalyticsConnector.ConditionalUserProperty> dequeOfExperimentsInAnalytics = new ArrayDeque<>(getAllExperimentsInAnalytics());
        int fetchedMaxUserProperties = getMaxUserPropertiesInAnalytics();
        for (AbtExperimentInfo experimentToAdd : experimentsToAdd) {
            while (dequeOfExperimentsInAnalytics.size() >= fetchedMaxUserProperties) {
                removeExperimentFromAnalytics(dequeOfExperimentsInAnalytics.pollFirst().name);
            }
            AnalyticsConnector.ConditionalUserProperty experiment = createConditionalUserProperty(experimentToAdd);
            addExperimentToAnalytics(experiment);
            dequeOfExperimentsInAnalytics.offer(experiment);
        }
    }

    private void removeExperiments(Collection<AnalyticsConnector.ConditionalUserProperty> experiments) {
        for (AnalyticsConnector.ConditionalUserProperty experiment : experiments) {
            removeExperimentFromAnalytics(experiment.name);
        }
    }

    private AnalyticsConnector.ConditionalUserProperty createConditionalUserProperty(AbtExperimentInfo experimentInfo) {
        String str;
        AnalyticsConnector.ConditionalUserProperty conditionalUserProperty = new AnalyticsConnector.ConditionalUserProperty();
        conditionalUserProperty.origin = this.originService;
        conditionalUserProperty.creationTimestamp = experimentInfo.getStartTimeInMillisSinceEpoch();
        conditionalUserProperty.name = experimentInfo.getExperimentId();
        conditionalUserProperty.value = experimentInfo.getVariantId();
        if (TextUtils.isEmpty(experimentInfo.getTriggerEventName())) {
            str = null;
        } else {
            str = experimentInfo.getTriggerEventName();
        }
        conditionalUserProperty.triggerEventName = str;
        conditionalUserProperty.triggerTimeout = experimentInfo.getTriggerTimeoutInMillis();
        conditionalUserProperty.timeToLive = experimentInfo.getTimeToLiveInMillis();
        return conditionalUserProperty;
    }

    private static List<AbtExperimentInfo> convertMapsToExperimentInfos(List<Map<String, String>> replacementExperimentsMaps) throws AbtException {
        List<AbtExperimentInfo> replacementExperimentInfos = new ArrayList<>();
        for (Map<String, String> replacementExperimentMap : replacementExperimentsMaps) {
            replacementExperimentInfos.add(AbtExperimentInfo.fromMap(replacementExperimentMap));
        }
        return replacementExperimentInfos;
    }

    private void addExperimentToAnalytics(AnalyticsConnector.ConditionalUserProperty experiment) {
        this.analyticsConnector.setConditionalUserProperty(experiment);
    }

    private void throwAbtExceptionIfAnalyticsIsNull() throws AbtException {
        if (this.analyticsConnector == null) {
            throw new AbtException("The Analytics SDK is not available. Please check that the Analytics SDK is included in your app dependencies.");
        }
    }

    /* access modifiers changed from: package-private */
    public void removeExperimentFromAnalytics(String experimentId) {
        this.analyticsConnector.clearConditionalUserProperty(experimentId, (String) null, (Bundle) null);
    }

    private int getMaxUserPropertiesInAnalytics() {
        if (this.maxUserProperties == null) {
            this.maxUserProperties = Integer.valueOf(this.analyticsConnector.getMaxUserProperties(this.originService));
        }
        return this.maxUserProperties.intValue();
    }

    private List<AnalyticsConnector.ConditionalUserProperty> getAllExperimentsInAnalytics() {
        return this.analyticsConnector.getConditionalUserProperties(this.originService, "");
    }
}
