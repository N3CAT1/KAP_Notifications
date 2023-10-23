package com.necatitufan.kapnotification;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

public class KapNotificationActivity extends AppCompatActivity
{
    public final static String INTENT_KAP_NOTIFICATION_URL = "com.necatitufan.kapnotification.INTENT_KAP_NOTIFICATION_URL";
    public final static String KAP_NOTIFICATION_URL_KEY = "kapNotificationUrl";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kap_notification);

        String kapUrl = getIntent().getStringExtra(KAP_NOTIFICATION_URL_KEY);
        WebView wvKapNotification = findViewById(R.id.wvKapNotification);
        wvKapNotification.getSettings().setJavaScriptEnabled(true);
        wvKapNotification.loadUrl(kapUrl);
    }
}