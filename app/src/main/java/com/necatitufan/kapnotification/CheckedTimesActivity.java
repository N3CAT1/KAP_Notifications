package com.necatitufan.kapnotification;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;

public class CheckedTimesActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checked_times);

        loadCheckedTimes();
    }

    private void loadCheckedTimes()
    {
        DBH dbh = new DBH(this);
        SQLiteDatabase db = dbh.getWritableDatabase();
        String sql = "Select * From " + DBH.TBL_CHECKEDTIMES
                + " Order By " + DBH.COL_CT_ID + " DESC;";
        Cursor cursor = db.rawQuery(sql, null);

        CheckedTimesAdapter adapter = new CheckedTimesAdapter(this, cursor);
        ListView lvCheckedTimes = findViewById(R.id.lvCheckedTimes);
        lvCheckedTimes.setAdapter(adapter);
    }
}