package com.thanoscorp.athene.utility.sms;

public class SMSProcessor {

    public static String processOTP(String sms){
        return sms.replaceAll("[^0-9]", "");
    }

    public static String processPASS(String sms){
        int i = 0;
        sms = sms.subSequence(sms.indexOf(":")+1, sms.length()).toString();
        while(sms.charAt(i) == ' '){
            i++;
        }
        return sms.subSequence( i, sms.length() ).toString();
    }

    public static boolean isFromVRSEC(String header){
        return header.contains("VRSEC") | header.contains("vrsec");
    }

    public static boolean isMessageOTP(String sms){
        return sms.contains("otp") | sms.contains("OTP") | sms.contains("one time password");
    }

    public static boolean isMessagePassword(String sms){
        return !isMessageOTP(sms) | sms.contains("login");
    }
}
