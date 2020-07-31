package com.thanoscorp.athene.utility.scraper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

/**
 *
 *  This class is used to maintain the Status Callbacks between various components
 *
 */

public class CMS {

    private static String SHARED_PREFS_NAME = "CMSPrefs";

    public static Set<Status> statuses = new HashSet<>();

    private static Status mStatus;
    private static Status previousStatus;


    private static Context mContext;
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;


    public static enum Status{
        LOGGING_IN,
        LOGGED_IN,
        REGISTERING,
        STAND_BY,
        PROFILE,
        ERROR,
        UNKNOWN
    }

    public static void init(Context context){
        mContext = context;
        preferences = mContext.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
        if(preferences.getBoolean(Status.LOGGED_IN.name(), false)){
            statuses.add(Status.LOGGED_IN);
        }
    }

    public static boolean setStatus(Status status){
        Log.d("CMS", "Status: " + status.name());
        previousStatus = mStatus;
        mStatus = status;
        if(editor == null || preferences == null) return false;
        switch (status){
            case LOGGED_IN:
                if(!statuses.contains(status)) {
                    statuses.add(Status.LOGGED_IN);
                    statuses.remove(Status.LOGGING_IN);
                    statuses.remove(Status.REGISTERING);
                    editor.putBoolean(status.name(), true);
                    editor.commit();
                }
                break;
            case LOGGING_IN:
                if(!statuses.contains(status)) {
                    statuses.add(Status.LOGGING_IN);
                    statuses.remove(Status.REGISTERING);
                    statuses.remove(Status.STAND_BY);
                }
                break;
            case REGISTERING:
                if(!statuses.contains(status)) {
                    statuses.add(Status.REGISTERING);
                    statuses.remove(Status.LOGGING_IN);
                    statuses.remove(Status.STAND_BY);
                }
                break;
            case PROFILE:
                if(!statuses.contains(status)) {
                    statuses.add(Status.PROFILE);
                }
                break;
            case ERROR:
                if(!statuses.contains(status)) {
                    statuses.add(Status.ERROR);
                }
                break;
            case UNKNOWN:
                if(!statuses.contains(status)) {
                    statuses.add(Status.UNKNOWN);
                }
                break;
            case STAND_BY:
                if(!statuses.contains(status)) {
                    statuses.add(Status.STAND_BY);
                }
                break;
        }
        return true;
    }

    public static boolean isStatus(Status status){
        return statuses.contains(status);
    }

    public static Status getPreviousStatus() {
        return previousStatus;
    }
}
