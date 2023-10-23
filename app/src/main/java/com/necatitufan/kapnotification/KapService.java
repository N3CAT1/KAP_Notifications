package com.necatitufan.kapnotification;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class KapService extends Service
{
    public KapService()
    {
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        KapReceiver kapReceiver = new KapReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);

        try
        {
            intentFilter.setPriority(1000);
        }
        catch (Exception unused)
        {
            intentFilter.setPriority(999);
        }
        try
        {
            getApplicationContext().unregisterReceiver(kapReceiver);
        }
        catch (IllegalArgumentException e)
        {
            //Log.d("_ws_onstart", e.getMessage());
        }

        kapReceiver = new KapReceiver();
        getApplicationContext().registerReceiver(kapReceiver, intentFilter);

        return Service.START_STICKY;
    }
}