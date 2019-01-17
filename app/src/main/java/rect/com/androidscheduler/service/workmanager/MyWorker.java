package rect.com.androidscheduler.service.workmanager;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
import rect.com.androidscheduler.R;

public class MyWorker extends Worker {
    public static final String TAG = MyWorker.class.getSimpleName();

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String title=getInputData().getString("title");

        sendNotification(title);
        startWorkOnNewThread();

        return Result.success();
    }

    private void sendNotification(String title) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("default", "DEFAULT", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setContentTitle(title).setContentText("Work Manager Desc").setSmallIcon(R.mipmap.ic_launcher);

        notificationManager.notify(1, notificationCompat.build());
    }

    private void startWorkOnNewThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= 5; i++) {

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
    }
}
