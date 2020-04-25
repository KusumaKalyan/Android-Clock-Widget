package com.example.minimalandroidclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import androidx.annotation.RequiresApi;
import java.time.Instant;
import java.util.Calendar;
import static android.content.ContentValues.TAG;


public class AppWidgetAlaram
{
    private final int ALARM_ID = 0;
    private final int INTERVAL_MILLIS = 60000;

    private Context mContext;
    public AppWidgetAlaram(Context context)
    {
        mContext = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void startAlarm()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, INTERVAL_MILLIS);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            long d = Instant.now().toEpochMilli();
            //Log.d(TAG, "startAlarm: epoch "+d);
        }

        Intent alarmIntent = new Intent(ClockWidget.ACTION_AUTO_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, ALARM_ID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        // RTC does not wake the device up
//        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), INTERVAL_MILLIS, pendingIntent);
//        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()+INTERVAL_MILLIS, pendingIntent);
        alarmManager.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+INTERVAL_MILLIS, pendingIntent);
//        alarmManager.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, calendar.getTimeInMillis()+INTERVAL_MILLIS , pendingIntent);
//        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+INTERVAL_MILLIS , pendingIntent);
//        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, android.os.SystemClock.uptimeMillis()+INTERVAL_MILLIS, pendingIntent);
        Log.d(TAG, "startAlarm: long max "+Long.MAX_VALUE);
        Log.d(TAG, "startAlarm: android os "+ SystemClock.uptimeMillis());
        Log.d(TAG, "startAlarm: android os SystemClock.elapsedRealtime() "+ SystemClock.elapsedRealtime());
        Log.d(TAG, "startAlarm: calendar.getTimeInMillis() "+calendar.getTimeInMillis());
        Log.d(TAG, "startAlarm: System.currentTimeMillis() "+System.currentTimeMillis());
    }

    public void stopAlarm()
    {
        Intent alarmIntent = new Intent(ClockWidget.ACTION_AUTO_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, ALARM_ID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
