package com.necatitufan.kapnotification;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class KapNotificationsAdapter extends CursorAdapter implements View.OnClickListener, CompoundButton.OnCheckedChangeListener
{
    private final Context context;
    private final ArrayList<Integer> checkedNotifications = new ArrayList<>();
    private final HashMap<Integer, CheckBox> checkBoxes = new HashMap<>();

    public KapNotificationsAdapter(Context ctx, Cursor c, boolean autoRequery)
    {
        super(ctx, c, autoRequery);
        context = ctx;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        assert inflater != null;
        return inflater.inflate(R.layout.listview_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        TextView tvPublishDate = view.findViewById(R.id.tvPublishDate);
        TextView tvCompanyName = view.findViewById(R.id.tvCompanyName);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvSummary = view.findViewById(R.id.tvSummary);
        ImageButton ibShowNotification = view.findViewById(R.id.ibShowNotification);
        CheckBox cbItem = view.findViewById(R.id.cbItem);

        ibShowNotification.setOnClickListener(this);

        tvPublishDate.setText(cursor.getString(DBH.COL_ID_NOT_PUBLISHDATE));
        tvCompanyName.setText(cursor.getString(DBH.COL_ID_NOT_COMPANYNAME));
        tvTitle.setText(cursor.getString(DBH.COL_ID_NOT_TITLE));
        tvSummary.setText(cursor.getString(DBH.COL_ID_NOT_SUMMARY));
        ibShowNotification.setTag(cursor.getInt(DBH.COL_ID_NOT_INDEX));
        cbItem.setTag(cursor.getInt(DBH.COL_ID_NOT_INDEX));
        cbItem.setOnCheckedChangeListener(this);

        if (!checkBoxes.containsKey(cursor.getInt(DBH.COL_ID_NOT_INDEX)))
            checkBoxes.put(cursor.getInt(DBH.COL_ID_NOT_INDEX), cbItem);
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent)
//    {
//        ViewHolder viewHolder;
//
//        if (convertView == null)
//        {
//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = inflater.inflate(R.layout.listview_item, null);
//
//
//        viewHolder = new ViewHolder();
//        viewHolder.cbItem = (CheckBox) convertView.findViewById(R.id.cbItem);
//        convertView.setTag(viewHolder);
//        }
//        else
//        {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//
//        viewHolder.cbItem.setChecked(!isAllChecked);
//        isAllChecked = !isAllChecked;
//
//        return convertView;
//    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.ibShowNotification)
        {
            int notificationIndex = (int) view.getTag();

            String kapUrl = context.getString(R.string.kap_notification_url);
            kapUrl += notificationIndex;

            Intent intent = new Intent(context, KapNotificationActivity.class);
            intent.setAction(KapNotificationActivity.INTENT_KAP_NOTIFICATION_URL);
            intent.putExtra(KapNotificationActivity.KAP_NOTIFICATION_URL_KEY, kapUrl);

            context.startActivity(intent);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
    {
//        Log.d("_onCheckedChanged", (int) compoundButton.getTag() + "");
        if (isChecked)
        {
            checkedNotifications.add((int) compoundButton.getTag());
        }
        else
        {
            Object notifIndex = (int) compoundButton.getTag();
            checkedNotifications.remove(notifIndex);
        }
    }

    public ArrayList<Integer> getCheckedNotifications()
    {
        return checkedNotifications;
    }
}
