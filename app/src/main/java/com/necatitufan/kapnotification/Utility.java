package com.necatitufan.kapnotification;

import android.app.ActivityManager;
import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class Utility
{
    Context context;

    public Utility(Context c)
    {
        context = c;
    }

    public boolean isWidgetServiceRunning(Class<?> serviceClass)
    {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (serviceClass.getName().equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
    }

    public String getJsonStrFromUrl(String contentUrl)
    {
        URL url;
        BufferedReader reader = null;
        StringBuilder stringBuilder;
        String content = null;

        try
        {
            // create the HttpURLConnection
            url = new URL(contentUrl);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            // just want to do an HTTP GET here
            connection.setRequestMethod("GET");
            //connection.setRequestProperty("Accept", "application/json");
            //connection.setRequestProperty("X-Environment", "android");
            // uncomment this if you want to write output to this url
            //connection.setDoOutput(true);

            // give it 15 seconds to respond
            connection.setReadTimeout(15 * 1000);
            connection.connect();

            // read the output from the server
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            stringBuilder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null)
            {
                stringBuilder.append(line).append("\n");
            }
            content = stringBuilder.toString();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            // close the reader; this can throw an exception too, so
            // wrap it in another try/catch block.
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }
            }
        }
        return content;
    }
}
