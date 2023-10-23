package com.necatitufan.kapnotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class KapReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();
        if (action != null)
        {
            if (action.equals(Intent.ACTION_TIME_TICK)
                    || action.equals(Intent.ACTION_TIME_CHANGED)
                    || action.equals(Intent.ACTION_TIMEZONE_CHANGED)
                    || action.equals(Intent.ACTION_BOOT_COMPLETED))
            {
                checkService(context);
                //getKapNotifications(context);

                new JsonDownloaderAsyncTask().execute(context, this);
            }
        }
    }

    private void checkService(Context context)
    {
        Utility utility = new Utility(context);
        if (!utility.isWidgetServiceRunning(KapService.class))
        {
            Intent intentService = new Intent();
            intentService.setClass(context.getApplicationContext(), KapService.class);
            context.getApplicationContext().startService(intentService);
        }
    }
}