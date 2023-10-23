package com.necatitufan.kapnotification;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBH extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "tfnkapnotification.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TBL_NOTIFICATIONS = "Notifications";
    public static final String TBL_CHECKEDTIMES = "CheckedTimes";

    public static final String COL_NOT_ID = "_id";
    public static final String COL_NOT_INDEX = "NotificationIndex";
    public static final String COL_NOT_TITLE = "Title";
    public static final String COL_NOT_PUBLISHDATE = "PublishDate";
    public static final String COL_NOT_COMPANYID = "CompanyID";
    public static final String COL_NOT_COMPANYNAME = "CompanyName";
    public static final String COL_NOT_STOCKCODES = "StockCodes";
    public static final String COL_NOT_SUMMARY = "Summary";

    public static final String COL_CT_ID = "_id";
    public static final String COL_CT_CHECKEDTIME = "CheckedTime";

    public static final int COL_ID_NOT_ID = 0;
    public static final int COL_ID_NOT_INDEX = 1;
    public static final int COL_ID_NOT_TITLE = 2;
    public static final int COL_ID_NOT_PUBLISHDATE = 3;
    public static final int COL_ID_NOT_COMPANYID = 4;
    public static final int COL_ID_NOT_COMPANYNAME = 5;
    public static final int COL_ID_NOT_STOCKCODES = 6;
    public static final int COL_ID_NOT_SUMMARY = 7;

    public static final int COL_ID_CT_ID = 0;
    public static final int COL_ID_CT_CHECKEDTIME = 1;

    public static final String[] COLUMNS_NOTIFICATIONS = new String[]
            {
                    COL_NOT_ID,
                    COL_NOT_INDEX,
                    COL_NOT_TITLE,
                    COL_NOT_PUBLISHDATE,
                    COL_NOT_COMPANYID,
                    COL_NOT_COMPANYNAME,
                    COL_NOT_STOCKCODES,
                    COL_NOT_SUMMARY
            };


    private static final String TABLE_CREATE_NOTIFICATIONS = "Create Table " + TBL_NOTIFICATIONS + " ("
            + COL_NOT_ID + " integer primary key autoincrement, "
            + COL_NOT_INDEX + " integer not null, "
            + COL_NOT_TITLE + " text not null, "
            + COL_NOT_PUBLISHDATE + " text not null, "
            + COL_NOT_COMPANYID + " text not null, "
            + COL_NOT_COMPANYNAME + " text not null, "
            + COL_NOT_STOCKCODES + " text not null, "
            + COL_NOT_SUMMARY + " text not null); ";

    private static final String TABLE_CREATE_CHECKEDTIMES = "Create Table " + TBL_CHECKEDTIMES + " ("
            + COL_CT_ID + " integer primary key autoincrement, "
            + COL_CT_CHECKEDTIME + " text); ";

    public DBH(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {
        database.execSQL(TABLE_CREATE_NOTIFICATIONS);
        database.execSQL(TABLE_CREATE_CHECKEDTIMES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        if (newVersion == 2 && oldVersion == 1)
            updateToSecondVersion(database);
    }

    private void updateToSecondVersion(SQLiteDatabase database)
    {
        database.execSQL(TABLE_CREATE_CHECKEDTIMES);
    }

    @Override
    public synchronized void close()
    {
        if (getWritableDatabase() != null)
        {
            getWritableDatabase().close();
            super.close();
        }
    }
}
