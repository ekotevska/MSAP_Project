package com.example.android2project;

import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.app.job.JobWorkItem;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ReminderUtils extends Service {

    private static final String REMINDER_JOB_TAG = "hydration_reminder_tag";
    //synchronized public static void scheduleChargingReminder(Context context) {

    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    String reminder_interval_time = sharedPreferences.getString("reminder_interval_time", "15");
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static int interval_time;
    JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
    JobInfo jobInfo = new  JobInfo.Builder(11, new ComponentName(this, AsyncReminder.class)).build();
   // JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE)

   // JobInfo jobInfo = new JobInfo.Builder(11, new ComponentName(this, AsyncReminder.class));

//jobScheduler.schedule(jobInfo);
    @Override
    public void onCreate() {
        super.onCreate();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                scheduler.execute((Runnable) jobInfo);
                return;
            }
        };
        int reminder_interval_minutes = Integer.parseInt(reminder_interval_time);
        int reminder_interval_seconds = (int) (TimeUnit.MINUTES.toSeconds(reminder_interval_minutes));
        int sync_flextime_seconds = reminder_interval_seconds;
        interval_time = reminder_interval_minutes;


        scheduler.scheduleAtFixedRate(runnable, 0, interval_time, TimeUnit.MINUTES);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}


