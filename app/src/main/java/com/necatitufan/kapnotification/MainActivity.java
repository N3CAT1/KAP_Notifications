package com.necatitufan.kapnotification;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnJsonDownloadedListener
{
    private ListView lvKapNotifications;
    private boolean checkAll = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lvKapNotifications = findViewById(R.id.lvKapNotifications);
        ImageButton ibDelete = findViewById(R.id.ibDelete);
        ImageButton ibRefresh = findViewById(R.id.ibRefresh);
        ImageButton ibCheckAll = findViewById(R.id.ibCheckAll);

        ibDelete.setOnClickListener(this);
        ibRefresh.setOnClickListener(this);
        ibCheckAll.setOnClickListener(this);

        batteryPerm();
        permissions();
        startKapService();
        loadKapNotifications();
        new JsonDownloaderAsyncTask().execute(MainActivity.this, MainActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if (R.id.menu_show_checked_times == item.getItemId())
        {
            Intent intent = new Intent(this, CheckedTimesActivity.class);

            startActivity(intent);
        }

        return true;
    }

    private void batteryPerm()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            final String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (pm != null && !pm.isIgnoringBatteryOptimizations(packageName))
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.alert_dialog_battery_optimization);
                builder.setMessage(R.string.alert_dialog_battery_perm);
                builder.setNegativeButton(R.string.text_cancel, null);
                builder.setPositiveButton(R.string.text_ok, (dialog, which) ->
                {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + packageName));
                    startActivity(intent);
                    //startActivityForResult(new Intent(android.provider.Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS), 0);
                });
                builder.show();
            }
        }
    }

    private void permissions()
    {
        ArrayList<String> deniedPermissions = new ArrayList<>();

        try
        {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS);
            if (info.requestedPermissions != null)
            {

                for (String permission : info.requestedPermissions)
                {
                    if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                    {
                        deniedPermissions.add(permission);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        String[] deniedPerms = new String[deniedPermissions.size()];

        for (int i = 0; i < deniedPermissions.size(); i++)
            deniedPerms[i] = deniedPermissions.get(i);

        if (deniedPermissions.size() > 0)
            ActivityCompat.requestPermissions(this, deniedPerms, 0);
    }

    private void startKapService()
    {
//        Utility utility = new Utility(this);
//        if (!utility.isWidgetServiceRunning(KapService.class))
//        {
//            Intent intentService = new Intent();
//            intentService.setClass(this, KapService.class);
//            startService(intentService);
//        }

        AlarmReceiver alarm = new AlarmReceiver();
        alarm.setAlarm(this);
    }

    private void loadKapNotifications()
    {
        DBH dbh = new DBH(this);
        SQLiteDatabase db = dbh.getWritableDatabase();
        String sql = "Select * From " + DBH.TBL_NOTIFICATIONS
                + " Order By " + DBH.COL_NOT_INDEX + " DESC;";
        Cursor cursor = db.rawQuery(sql, null);

        KapNotificationsAdapter adapter = new KapNotificationsAdapter(this, cursor, false);
        lvKapNotifications.setAdapter(adapter);
    }

    private void deleteNotifications()
    {
        KapNotificationsAdapter kna = (KapNotificationsAdapter) lvKapNotifications.getAdapter();

        AlertDialog.Builder adBuilder = new AlertDialog.Builder(this)
                .setTitle(R.string.text_delete)
                .setMessage(R.string.text_delete_warning)
                .setPositiveButton(R.string.text_yes, (dialogInterface, which) ->
                {

                    ArrayList<Integer> checkedNotifications = kna.getCheckedNotifications();

                    DBH dbh = new DBH(MainActivity.this);
                    SQLiteDatabase db = dbh.getWritableDatabase();
                    //db.beginTransaction();
                    String whereClause = "";
                    String[] whereArgs = new String[checkedNotifications.size()];
                    for (int i = 0; i < checkedNotifications.size(); i++)
                    {
                        whereClause += DBH.COL_NOT_INDEX + " = ? OR ";
                        whereArgs[i] = String.valueOf(checkedNotifications.get(i));
                        //db.delete(DBH.TBL_NOTIFICATIONS, DBH.COL_NOT_INDEX + " = ? ", new String[]{String.valueOf(checkedNotifications.get(i))});
                    }
                    whereClause = whereClause.substring(0, whereClause.length() - 3);
                    db.delete(DBH.TBL_NOTIFICATIONS, whereClause, whereArgs);
//                    db.setTransactionSuccessful();
//                    db.endTransaction();

                    db.close();
                    dbh.close();

                    loadKapNotifications();
                })
                .setNegativeButton(R.string.text_no, (dialogInterface, which) ->
                {

                });

        adBuilder.show();
    }

    private void checkAllNotifications()
    {
        for (int i = 0; i < lvKapNotifications.getChildCount(); i++)
        {
            LinearLayout itemLayout = (LinearLayout) lvKapNotifications.getChildAt(i);
            CheckBox cb = (CheckBox) itemLayout.findViewById(R.id.cbItem);
            cb.setChecked(!checkAll);
        }

        checkAll = !checkAll;
    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();

        if (id == R.id.ibDelete)
        {
            deleteNotifications();
        }
        else if (id == R.id.ibCheckAll)
            checkAllNotifications();
        else if (id == R.id.ibRefresh)
            new JsonDownloaderAsyncTask().execute(MainActivity.this, MainActivity.this);
    }

    @Override
    public void onJsonDownloadedListener()
    {
        loadKapNotifications();
    }
}