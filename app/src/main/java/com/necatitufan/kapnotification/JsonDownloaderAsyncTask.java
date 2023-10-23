package com.necatitufan.kapnotification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class JsonDownloaderAsyncTask extends AsyncTask<Object, Void, Object[]>
{
    @Override
    protected Object[] doInBackground(Object... params)
    {
        Context context = (Context) params[0];
        Object eventListenerClass = params[1];

        saveCheckedTimeToDB(context);

        Utility utility = new Utility(context);

        String jsonUrl = getKapNotificationsUrl(context);

        String jsonStr = utility.getJsonStrFromUrl(jsonUrl);

        boolean isHandled = handleJsonStr(context, jsonStr);

        return new Object[]{eventListenerClass, isHandled};
    }

    private void saveCheckedTimeToDB(Context context)
    {
        DBH dbh = new DBH(context);
        SQLiteDatabase db = dbh.getWritableDatabase();

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ENGLISH);
        Calendar cal = Calendar.getInstance();

        ContentValues cv = new ContentValues();
        cv.put(DBH.COL_CT_CHECKEDTIME, sdf.format(cal.getTime()));

        db.insert(DBH.TBL_CHECKEDTIMES, null, cv);
        db.close();
        dbh.close();
    }

    @Override
    protected void onPostExecute(Object[] result)
    {
        Object eventListenerClass = result[0];
        boolean isHandled = (boolean) result[1];

        if (isHandled)
        {
            if (eventListenerClass instanceof OnJsonDownloadedListener)
            {
                OnJsonDownloadedListener event = (OnJsonDownloadedListener) eventListenerClass;
                event.onJsonDownloadedListener();
            }
        }
    }

    private String getKapNotificationsUrl(Context context)
    {
        String jsonUrl = "";
        DBH dbh = new DBH(context);
        SQLiteDatabase db = dbh.getWritableDatabase();
        String sql = "Select * From " + DBH.TBL_NOTIFICATIONS + " ORDER BY " + DBH.COL_NOT_INDEX + " DESC LIMIT 1;";
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0)
        {
            int notifIndex = cursor.getInt(DBH.COL_ID_NOT_INDEX);
            String publishDate = cursor.getString(DBH.COL_ID_NOT_PUBLISHDATE);
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH);
            Calendar cal = Calendar.getInstance();
            try
            {
                Date d = sdf.parse(publishDate);

                SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);

                if (sdfDate.format(cal.getTime()).equals(sdfDate.format(d)))
                {
                    jsonUrl = context.getString(R.string.kap_url_last) + notifIndex;
                    cursor.close();
                }
                else
                {
                    jsonUrl = fromToUrl(context);
                    jsonUrl += ("&afterDisclosureIndex=" + notifIndex);
                }
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            jsonUrl = fromToUrl(context);
        }

        db.close();
        dbh.close();

        return jsonUrl;
    }

    private String fromToUrl(Context context)
    {
        String jsonUrl = context.getString(R.string.kap_url);
        Calendar calNow = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        jsonUrl = jsonUrl.replace("{date2}", sdf.format(calNow.getTime()));

        calNow.add(Calendar.DATE, -3);
        jsonUrl = jsonUrl.replace("{date1}", sdf.format(calNow.getTime()));

        return jsonUrl;
    }

    /*
        COL_NOT_ID,
        COL_NOT_INDEX,
        COL_NOT_TITLE,
        COL_NOT_PUBLISHDATE,
        COL_NOT_COMPANYID,
        COL_NOT_COMPANYNAME,
        COL_NOT_STOCKCODES,
        COL_NOT_SUMMARY
     */
    private void insertToDatabase(Context context, KapNotification n)
    {
        DBH dbh = new DBH(context);
        SQLiteDatabase db = dbh.getWritableDatabase();

        Calendar calPublishDate = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH);
        if (n.publishDate.contains("Bugün"))
        {
            calPublishDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(n.publishDate.replace("Bugün ", "").split(":")[0]));
            calPublishDate.set(Calendar.MINUTE, Integer.parseInt(n.publishDate.replace("Bugün ", "").split(":")[1]));
            calPublishDate.set(Calendar.SECOND, 0);
        }
        else if (n.publishDate.contains("Dün"))
        {
            calPublishDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(n.publishDate.replace("Dün ", "").split(":")[0]));
            calPublishDate.set(Calendar.MINUTE, Integer.parseInt(n.publishDate.replace("Dün ", "").split(":")[1]));
            calPublishDate.set(Calendar.SECOND, 0);
            calPublishDate.add(Calendar.DATE, -1);
        }
        else
        {
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.ENGLISH);
            try
            {
                Date d = sdf2.parse(n.publishDate);
                if (d != null)
                    calPublishDate.setTime(d);
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
        }

        ContentValues cv = new ContentValues();
        cv.put(DBH.COL_NOT_INDEX, n.notificationIndex);
        cv.put(DBH.COL_NOT_TITLE, n.title);
        cv.put(DBH.COL_NOT_PUBLISHDATE, sdf.format(calPublishDate.getTime()));
        cv.put(DBH.COL_NOT_COMPANYID, n.companyID);
        cv.put(DBH.COL_NOT_COMPANYNAME, n.companyName);
        cv.put(DBH.COL_NOT_STOCKCODES, n.stockCodes);
        cv.put(DBH.COL_NOT_SUMMARY, n.summary);

        db.insert(DBH.TBL_NOTIFICATIONS, null, cv);
        db.close();
        dbh.close();
    }

    private void showNotification(Context context, KapNotification kn)
    {
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, R.layout.activity_main, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.addLine("Kurum: " + kn.companyName);
        inboxStyle.addLine("Kod: " + kn.stockCodes);
        inboxStyle.addLine("İçerik: " + kn.summary);

        Bitmap bitmapLargeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "TFNKapNotifications")
                .setLargeIcon(bitmapLargeIcon)
                .setSmallIcon(R.drawable.ic_launcher_notification)
                .setContentTitle(kn.title)
                .setContentText(kn.companyName)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setLights(Color.argb(255, 221, 30, 255), 500, 500)
                .setStyle(inboxStyle);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(kn.notificationIndex, builder.build());
    }

    private void createNotificationChannel(Context context, KapNotification kn)
    {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = kn.companyName;
            String description = kn.title;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("TFNKapNotifications", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null)
            {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private boolean handleJsonStr(Context context, String jsonStr)
    {
        boolean isHandled = false;
        try
        {
            JSONArray jsonArray = new JSONArray(jsonStr);

            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject joMain = jsonArray.getJSONObject(i);
                JSONObject joBasic = joMain.getJSONObject("basic");

                KapNotification n = new KapNotification();
                n.notificationIndex = joBasic.getInt("disclosureIndex");
                n.title = joBasic.getString("title");
                n.publishDate = joBasic.getString("publishDate");
                n.companyID = joBasic.getString("companyId");
                n.companyName = joBasic.getString("companyName");
                n.stockCodes = joBasic.getString("stockCodes");
                n.summary = joBasic.has("summary") ? joBasic.getString("summary") : "";

                String refTitle1 = context.getString(R.string.json_title_1);
                String refTitle2 = context.getString(R.string.json_title_2);
                String refTitle3 = context.getString(R.string.json_title_3);

                if (refTitle1.equals(n.title) || refTitle2.equals(n.title) || refTitle3.equals(n.title))
                {
                    insertToDatabase(context, n);
                    createNotificationChannel(context, n);
                    showNotification(context, n);

                    isHandled = true;
                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return isHandled;
    }
}
