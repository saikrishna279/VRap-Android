package com.thanoscorp.athene.utility.scraper;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.thanoscorp.athene.models.WebViewX;
import com.thanoscorp.athene.referencer.WebViewRef;

/**
 *
 *  CMSCore is a wrapper around a WebView which loads the https://student.vrseconline.in/ website
 *  and controls the data input and output between user and the website.
 *
 *  It also handles the auto input of the received OTP which is required to changes the Password during initial setup.
 *
 */
public class CMSCore{

    private static CMScraper scraper;
    private static WebViewRef ref;
    private static Context mContext;

    public static void initialize(Context context){
        mContext = context;
        scraper = new CMScraper();
        ref = new WebViewRef();
        scraper.init(context, ref);
    }

    public static void initialize(Context context, WebViewRef mRef){
        mContext = context;
        scraper = new CMScraper();
        ref = mRef;
        scraper.init(context, ref);
    }

    public static void login(String id, String pass){
            scraper.login( ref, id, pass );
    }

    public static void register( String id, String phnno ){
            scraper.signUp( ref, id, phnno );
    }

    public static void submitOTP(String OTP){
            scraper.submitOTP( ref, OTP);
    }

    public static void getStudentDetails(DatabaseReference db, String id, String pass){
            scraper.getDetails(ref, db, id, pass);
    }

    public static WebViewRef getWebviewRef(){
        return ref;
    }

    public static void setWebView(WebViewX webView){ ref.webview = webView;}

}
