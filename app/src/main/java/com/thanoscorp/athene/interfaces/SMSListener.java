package com.thanoscorp.athene.interfaces;

import android.telephony.SmsMessage;

public interface SMSListener{
    public void onSMSAvailable(SmsMessage message);
}
