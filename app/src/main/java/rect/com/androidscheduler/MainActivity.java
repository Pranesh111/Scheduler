package rect.com.androidscheduler;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.net.Network;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;

import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import rect.com.androidscheduler.service.firebasejobdispatcher.MyFirebaseService;
import rect.com.androidscheduler.service.jobscheduler.MyService;
import rect.com.androidscheduler.service.workmanager.MyWorker;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //For testing Workmanager in orio
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {

            //Passing data
            Data data = new Data.Builder().putString("title","Say Hi from Request").build();

            //Putting contraint
            Constraints constraints= new Constraints.Builder().setRequiredNetworkType(NetworkType.UNMETERED).build();
            Constraints periodicConstarint = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();

            //One time only execute
            OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(MyWorker.class)
                    .setConstraints(constraints)
                    .setInputData(data)
                    .build();


            data = new Data.Builder().putString("title","Say Hi from Periodic Request").build();
            PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(MyWorker.class,5,TimeUnit.SECONDS)
                    .setConstraints(periodicConstarint)
                    .setInputData(data)
                    .build();
            WorkManager.getInstance().enqueue(periodicWorkRequest);
            WorkManager.getInstance().enqueue(oneTimeWorkRequest);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ComponentName componentName = new ComponentName(this, MyService.class);
            JobInfo jobInfo = new JobInfo.Builder(12, componentName)
                    .setRequiresCharging(true)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                    .build();

            JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            int resultCode = jobScheduler.schedule(jobInfo);

            if (resultCode == JobScheduler.RESULT_SUCCESS)
                Log.d(MyService.TAG, "Job scheduled!");
            else
                Log.d(MyService.TAG, "Job not scheduled");

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            // Create a new dispatcher using the Google Play driver.
            FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
            Job myJob = dispatcher.newJobBuilder()
                    .setService(MyFirebaseService.class) // the JobService that will be called
                    .setTag("my-unique-tag")        // uniquely identifies the job
                    .setConstraints(
                            // only run on an unmetered network
                            Constraint.ON_UNMETERED_NETWORK,
                            // only run when the device is charging
                            Constraint.DEVICE_CHARGING
                    )
                    .build();

            dispatcher.mustSchedule(myJob);
        }
    }

}
