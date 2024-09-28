package im.bclpbkiauv.keepalive;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import im.bclpbkiauv.messenger.FileLog;

public class ScheduleService extends JobService {
    public boolean onStartJob(JobParameters params) {
        FileLog.d("ScheduleService ---> onStartJob(): params = [" + params + "]");
        startService(new Intent(getApplicationContext(), DaemonService.class));
        jobFinished(params, false);
        return false;
    }

    public boolean onStopJob(JobParameters params) {
        FileLog.d("ScheduleService ---> onStopJob(): params = [" + params + "]");
        return false;
    }
}
