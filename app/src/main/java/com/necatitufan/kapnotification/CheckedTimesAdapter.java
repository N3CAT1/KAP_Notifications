package com.necatitufan.kapnotification;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class CheckedTimesAdapter extends CursorAdapter
{
    public CheckedTimesAdapter(Context context, Cursor c)
    {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        assert inflater != null;
        return inflater.inflate(R.layout.listview_item_checked_times, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        TextView tvID = view.findViewById(R.id.tvID);
        TextView tvCheckedTime = view.findViewById(R.id.tvCheckedTime);

        tvID.setText(String.valueOf(cursor.getInt(DBH.COL_ID_CT_ID)));
        tvCheckedTime.setText(cursor.getString(DBH.COL_ID_CT_CHECKEDTIME));
    }
}
