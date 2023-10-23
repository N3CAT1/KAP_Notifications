package com.necatitufan.kapnotification;

import android.app.Service;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;

public class CheckService extends Service
{
    // This method run only one time. At the first time of service created and running
    @Override
    public void onCreate()
    {
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        Log.d("onCreate()", "After service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        new JsonDownloaderAsyncTask().execute(getApplicationContext(), this);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // We don't provide binding
        return null;
    }
}