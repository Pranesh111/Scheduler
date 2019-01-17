package rect.com.androidscheduler.service.firebasejobdispatcher;

import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import rect.com.androidscheduler.service.jobscheduler.MyService;

public class MyFirebaseService extends JobService {
    public static final String TAG = MyService.class.getSimpleName();
    boolean isWorking = false;
    boolean jobCancelled = false;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started!");
        isWorking = true;
        // We need 'jobParameters' so we can call 'jobFinished'
        startWorkOnNewThread(params); // Services do NOT run on a separate thread

        return isWorking;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before being completed.");
        jobCancelled = true;
        boolean needsReschedule = isWorking;
        jobFinished(params, needsReschedule);
        return needsReschedule;
    }


    private void startWorkOnNewThread(JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= 5; i++) {
                    if (jobCancelled) return;

                    Log.e(TAG, "Job " + i + " Running");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        Log.d(TAG, "Job finished!");
        isWorking = false;
        boolean needsReschedule = false;
        jobFinished(params, needsReschedule);

    }
}