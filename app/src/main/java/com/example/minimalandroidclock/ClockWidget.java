package com.example.minimalandroidclock;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;
import androidx.annotation.RequiresApi;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import static android.content.ContentValues.TAG;
/**
 * Implementation of App Widget functionality.
 */
public class ClockWidget extends AppWidgetProvider {
    //    PendingIntent pendingIntent;
    public static final String ACTION_AUTO_UPDATE = "AUTO_UPDATE";
    @RequiresApi(api = Build.VERSION_CODES.O)
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String[] time = dtf.format(now).split(" ");
        System.out.println(time[1]);

        Bitmap rotated = rotatedImage(context, time[1]);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.clock_widget);
        views.setImageViewResource(R.id.background, R.drawable.background);
        views.setImageViewBitmap(R.id.hour_circle, rotated);
        if(isMorning(time[1]))
            views.setImageViewResource(R.id.background_circle, R.drawable.background_circle);
        else views.setImageViewResource(R.id.background_circle, R.drawable.background_circle_rotated);
//        views.setImageViewResource(R.id.hour_circle, R.drawable.hour_circle);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
//        updateAppWidget(context, appWidgetManager, appWidgetId, rotation);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them

        AppWidgetAlaram appWidgetAlarm = new AppWidgetAlaram(context.getApplicationContext());
        appWidgetAlarm.startAlarm();
        for (final int appWidgetId : appWidgetIds) {
            Log.d(TAG, "onUpdate: Updating"+(appWidgetId));
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static boolean isMorning(String time)
    {
        String[] times = time.split(":");
        int hour = 0;
        try {
            hour = Integer.parseInt(times[0]);
        }catch (Exception e)
        {
            //Log.d(TAG, "nightORMorning: error "+e);
        }
        if(hour > 6 && hour < 18)
            return true;
        else return false;
    }

    public static Bitmap rotatedImage(Context context, String time)
    {
        String[] times = time.split(":");
        //Log.d(TAG, "hour: "+times[0]);
        int hour = 0;
        int min = 0;
        try {
            hour = Integer.parseInt(times[0]);
            min = Integer.parseInt(times[1]);
        }catch (Exception e)
        {
            //Log.d(TAG, "rotatedImage: error "+e);
        }

        //Log.d(TAG, "rotatedImage: 1");
        float rotation =(hour/12.0f) * 360.0f;
        rotation += (min/60.0f) * 30.0f;
        //Log.d(TAG, "rotatedImage: "+(hour/24));
        //Log.d(TAG, "angle: "+rotation);
        Bitmap myImg = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.time)).getBitmap();
        Matrix matrix = new Matrix();
        matrix.postRotate(rotation);
        return Bitmap.createBitmap(myImg, 0, 0, myImg.getWidth(), myImg.getHeight(),
                matrix, true);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        super.onEnabled(context);
        //Log.d(TAG, "onEnabled: ");
        AppWidgetAlaram appWidgetAlarm = new AppWidgetAlaram(context.getApplicationContext());
        appWidgetAlarm.startAlarm();
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        super.onDisabled(context);
        //Log.d(TAG, "onDisabled: ");
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisAppWidgetComponentName = new ComponentName(context.getPackageName(),getClass().getName());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidgetComponentName);
        if (appWidgetIds.length == 0) {
            // stop alarm
            AppWidgetAlaram appWidgetAlarm = new AppWidgetAlaram(context.getApplicationContext());
            appWidgetAlarm.stopAlarm();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        //Log.d(TAG, "onReceive: 1");
        //Log.d(TAG, "onReceive: "+String.valueOf(intent));

        if(intent.getAction().equals(ACTION_AUTO_UPDATE))
        {
            //Log.d(TAG, "onReceive: ok");
            AppWidgetManager appWidgetManager = AppWidgetManager
                    .getInstance(context);
            ComponentName thisAppWidget = new ComponentName(context
                    .getPackageName(), ClockWidget.class.getName());
            int[] appWidgetIds = appWidgetManager
                    .getAppWidgetIds(thisAppWidget);
            onUpdate(context, appWidgetManager, appWidgetIds);
        }
    }
}

