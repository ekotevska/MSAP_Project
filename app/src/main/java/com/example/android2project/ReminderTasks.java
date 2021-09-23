package com.example.android2project;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

public class ReminderTasks {

    public static String ACTION_INCREMENT_WATER_COUNT = "increment-water-count";
    public static String ACTION_DISMISS_NOTIFICATION = "dismiss-notification";
    public static String ACTION_CHARGING_REMINDER = "charging-reminder";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void executeTask (Context context, String action) {
        if (TextUtils.equals(ACTION_INCREMENT_WATER_COUNT, action)) {
            incrementWaterCount(context);
        } else if (TextUtils.equals(ACTION_DISMISS_NOTIFICATION, action)) {
            NotificationUtils.clearNotifications(context);
        } else if (TextUtils.equals(ACTION_CHARGING_REMINDER, action)) {
            issueChargingReminder(context);

        }
    }
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        private static void issueChargingReminder(Context context) {
            PreferenceUtils.incrementChargingReminderCount(context);
            NotificationUtils.remindUserCharging(context);
        }
        private static void incrementWaterCount(Context context) {
            PreferenceUtils.incrementWaterCount(context);
            NotificationUtils.clearNotifications(context);
        }
        public static void clearWaterCount(Context context) {
            PreferenceUtils.resetWaterCount(context);
            NotificationUtils.clearNotifications(context);
        }
    public static void resetWaterCount(Context context) {
        PreferenceUtils.resetWaterCount(context);
        NotificationUtils.clearNotifications(context);
    }
        public static void resetChargingReminderCount(Context context){
            PreferenceUtils.resetChargingReminderCount(context);
            NotificationUtils.clearNotifications(context);
        }
  }
