package com.thanoscorp.athene.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.thanoscorp.athene.interfaces.SMSListener;

/**
 *
 *  Read SMS Containing OTP that is sent while Login
 *
 */

public class SMSBroadcastReceiver extends BroadcastReceiver {

    private static SMSListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();
        Object[] pdus = (Object[]) data.get("pdus");
        for (int i = 0; i < pdus.length; i++) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
            mListener.onSMSAvailable(smsMessage);
            //Toast.makeText(context, smsMessage.getDisplayOriginatingAddress() + " " + smsMessage.getMessageBody(), Toast.LENGTH_SHORT).show();
        }
    }

    public static void bindListener(SMSListener listener){
        mListener = listener;

    }
}
