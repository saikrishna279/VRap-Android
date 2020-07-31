package com.thanoscorp.athene.services;

import android.content.pm.PackageManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

/**
 *
 *  TODO Read OTP to auto fill the login form at https://student.vrseconline.in/
 *
 */
public class NotificationListener extends NotificationListenerService {

    String TAG = "NotificationListener";
    private PackageManager pm = getApplicationContext().getPackageManager();

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn, RankingMap rankingMap) {
        super.onNotificationPosted(sbn, rankingMap);
        try {
            String appName = pm.getApplicationLabel(pm.getApplicationInfo(sbn.getPackageName(), PackageManager.GET_META_DATA)).toString();
            String tag = sbn.getTag();
            String group = sbn.getNotification().getGroup();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap) {
        super.onNotificationRemoved(sbn, rankingMap);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap, int reason) {
        super.onNotificationRemoved(sbn, rankingMap, reason);
    }
}
