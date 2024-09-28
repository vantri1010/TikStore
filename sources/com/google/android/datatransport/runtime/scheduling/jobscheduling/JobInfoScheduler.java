package com.google.android.datatransport.runtime.scheduling.jobscheduling;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.PersistableBundle;
import android.util.Base64;
import com.google.android.datatransport.runtime.TransportContext;
import com.google.android.datatransport.runtime.logging.Logging;
import com.google.android.datatransport.runtime.scheduling.persistence.EventStore;
import com.google.android.datatransport.runtime.util.PriorityMapping;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.zip.Adler32;

/* compiled from: com.google.android.datatransport:transport-runtime@@2.2.0 */
public class JobInfoScheduler implements WorkScheduler {
    static final String ATTEMPT_NUMBER = "attemptNumber";
    static final String BACKEND_NAME = "backendName";
    static final String EVENT_PRIORITY = "priority";
    static final String EXTRAS = "extras";
    private static final String LOG_TAG = "JobInfoScheduler";
    private final SchedulerConfig config;
    private final Context context;
    private final EventStore eventStore;

    public JobInfoScheduler(Context applicationContext, EventStore eventStore2, SchedulerConfig config2) {
        this.context = applicationContext;
        this.eventStore = eventStore2;
        this.config = config2;
    }

    /* access modifiers changed from: package-private */
    public int getJobId(TransportContext transportContext) {
        Adler32 checksum = new Adler32();
        checksum.update(this.context.getPackageName().getBytes(Charset.forName("UTF-8")));
        checksum.update(transportContext.getBackendName().getBytes(Charset.forName("UTF-8")));
        checksum.update(ByteBuffer.allocate(4).putInt(PriorityMapping.toInt(transportContext.getPriority())).array());
        if (transportContext.getExtras() != null) {
            checksum.update(transportContext.getExtras());
        }
        return (int) checksum.getValue();
    }

    private boolean isJobServiceOn(JobScheduler scheduler, int jobId, int attemptNumber) {
        for (JobInfo jobInfo : scheduler.getAllPendingJobs()) {
            int existingAttemptNumber = jobInfo.getExtras().getInt(ATTEMPT_NUMBER);
            if (jobInfo.getId() == jobId) {
                if (existingAttemptNumber >= attemptNumber) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public void schedule(TransportContext transportContext, int attemptNumber) {
        ComponentName serviceComponent = new ComponentName(this.context, JobInfoSchedulerService.class);
        JobScheduler jobScheduler = (JobScheduler) this.context.getSystemService("jobscheduler");
        int jobId = getJobId(transportContext);
        if (isJobServiceOn(jobScheduler, jobId, attemptNumber)) {
            Logging.d(LOG_TAG, "Upload for context %s is already scheduled. Returning...", (Object) transportContext);
            return;
        }
        long nextCallTime = this.eventStore.getNextCallTime(transportContext);
        JobInfo.Builder builder = this.config.configureJob(new JobInfo.Builder(jobId, serviceComponent), transportContext.getPriority(), nextCallTime, attemptNumber);
        PersistableBundle bundle = new PersistableBundle();
        bundle.putInt(ATTEMPT_NUMBER, attemptNumber);
        bundle.putString(BACKEND_NAME, transportContext.getBackendName());
        bundle.putInt(EVENT_PRIORITY, PriorityMapping.toInt(transportContext.getPriority()));
        if (transportContext.getExtras() != null) {
            bundle.putString(EXTRAS, Base64.encodeToString(transportContext.getExtras(), 0));
        }
        builder.setExtras(bundle);
        Logging.d(LOG_TAG, "Scheduling upload for context %s with jobId=%d in %dms(Backend next call timestamp %d). Attempt %d", transportContext, Integer.valueOf(jobId), Long.valueOf(this.config.getScheduleDelay(transportContext.getPriority(), nextCallTime, attemptNumber)), Long.valueOf(nextCallTime), Integer.valueOf(attemptNumber));
        jobScheduler.schedule(builder.build());
    }
}
