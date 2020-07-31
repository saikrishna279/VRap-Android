package com.thanoscorp.athene.utility.scraper;

import android.content.Context;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.google.firebase.database.DatabaseReference;
import com.thanoscorp.athene.models.WebViewX;
import com.thanoscorp.athene.referencer.WebViewRef;

import static com.thanoscorp.athene.utility.scraper.CMSConstants.DB_DETAILS_BLOOD_GROUP;
import static com.thanoscorp.athene.utility.scraper.CMSConstants.DB_DETAILS_BRANCH;
import static com.thanoscorp.athene.utility.scraper.CMSConstants.DB_DETAILS_DOB;
import static com.thanoscorp.athene.utility.scraper.CMSConstants.DB_DETAILS_FATHER_NAME;
import static com.thanoscorp.athene.utility.scraper.CMSConstants.DB_DETAILS_GENDER;
import static com.thanoscorp.athene.utility.scraper.CMSConstants.DB_DETAILS_MOBILE;
import static com.thanoscorp.athene.utility.scraper.CMSConstants.DB_DETAILS_NAME;
import static com.thanoscorp.athene.utility.scraper.CMSConstants.DB_DETAILS_PARENT_MOBILE;
import static com.thanoscorp.athene.utility.scraper.CMSConstants.DB_DETAILS_PROGRAMME;
import static com.thanoscorp.athene.utility.scraper.CMSConstants.DB_DETAILS_REGULATION;
import static com.thanoscorp.athene.utility.scraper.CMSConstants.DB_DETAILS_SECTION;

/**
 *
 *  CMScraper handles the website data scraping from the WebView
 *
 */

public class CMScraper {


    public CMScraper() {
    }

    // USE {@link referencer.WebViewRef.class WebViewRed} TO PASS BY REF

    public void init(Context context, WebViewRef ref) {
        CMS.init(context);
        if (ref.webview == null) ref.webview = new WebViewX(context);
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(ref.webview, true);
        ref.webview.getSettings().setAllowFileAccessFromFileURLs(true);
        ref.webview.getSettings().setAllowUniversalAccessFromFileURLs(true);
        ref.webview.getSettings().setAllowContentAccess(true);
        ref.webview.getSettings().setDomStorageEnabled(true);

        ref.webview.getSettings().setMinimumFontSize(12);
        ref.webview.setInitialScale(150);
        ref.webview.getSettings().setJavaScriptEnabled(true);
        ref.webview.getSettings().setLoadWithOverviewMode(true);
        ref.webview.getSettings().setLoadsImagesAutomatically(false);
        ref.webview.getSettings().setBlockNetworkImage(true);
        ref.webview.getSettings().setUseWideViewPort(false);
        ref.webview.getSettings().setSupportZoom(true);
        ref.webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        ref.webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        ref.webview.setScrollbarFadingEnabled(false);
        ref.webview.getSettings().setAppCacheEnabled(false);
        String newUA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36";
        ref.webview.getSettings().setUserAgentString(newUA);
        ref.webview.setWebViewXListener(new WebViewX.WebViewXListener() {
            @Override
            public void onQueueStarted() {

            }

            @Override
            public void onQueueFinished() {
                if (!CMS.setStatus(CMS.Status.STAND_BY)) CMS.setStatus(CMS.Status.ERROR);
            }

            @Override
            public void onDataAvailable(String js, String data) {

            }
        });
        CMS.setStatus(CMS.Status.STAND_BY);
    }

    public void login(final WebViewRef ref, String id, String password) {
        ref.webview.killQueue();
        if (!CMS.isStatus(CMS.Status.STAND_BY)) return;
        CMS.setStatus(CMS.Status.LOGGING_IN);
        ref.webview.enqueueUrl(CMSConstants.URL_SIGN_IN);
        ref.webview.enqueueUrl(JSBuilder.setFieldText(CMSConstants.LOGIN_ID_FIELD, id));
        ref.webview.enqueueUrl(JSBuilder.setFieldText(CMSConstants.LOGIN_PASSWORD_FIELD, password));
        ref.webview.enqueueUrl(JSBuilder.clickButton(CMSConstants.LOGIN_BUTTON_FIELD));
        final String tempquery = JSBuilder.getDivTextFromClassName(CMSConstants.LOGIN_FAILED_ATTEMPT);
        ref.webview.enqueueUrl(tempquery)
                .setWebViewXListener(new WebViewX.WebViewXListener() {
                    @Override
                    public void onQueueStarted() {

                    }

                    @Override
                    public void onQueueFinished() {

                    }

                    @Override
                    public void onDataAvailable(String js, String data) {
                        if(js.equals(tempquery))CMS.setStatus(CMS.Status.LOGGED_IN);
                    }
                });
        ref.webview.startQueue();
    }

    public void signUp(WebViewRef ref, String id, String phoneNumber) {
        Log.d("CMSScraper", "singup()");
        if (!CMS.isStatus(CMS.Status.STAND_BY)) return;
        CMS.setStatus(CMS.Status.REGISTERING);
        ref.webview.enqueueUrl(CMSConstants.URL_FORGOT_PASSWORD);
        ref.webview.enqueueUrl(JSBuilder.setFieldText(CMSConstants.FORGOT_PASSWORD_ID_FIELD, id));
        ref.webview.enqueueUrl(JSBuilder.setFieldText(CMSConstants.FORGOT_PASSWORD_MOBILE_FIELD, phoneNumber));
        ref.webview.enqueueUrl(JSBuilder.clickButton(CMSConstants.FORGOT_PASSWORD_BUTTON_FIELD));
        ref.webview.startQueue();
    }

    public void submitOTP(WebViewRef ref, String OTP) {
        Log.d("CMSScraper", "submitOTP()");
        if (!CMS.isStatus(CMS.Status.STAND_BY) && !CMS.isStatus(CMS.Status.REGISTERING)) return;
        CMS.setStatus(CMS.Status.REGISTERING);
        ref.webview.enqueueUrl(JSBuilder.setFieldText(CMSConstants.FORGOT_PASSWORD_OTP_FIELD, OTP));
        ref.webview.enqueueUrl(JSBuilder.clickButton(CMSConstants.FORGOT_PASSWORD_OTP_BUTTON_FIELD));
        ref.webview.startQueue();
    }

    public void getDetails(WebViewRef ref, final DatabaseReference db, final String id, String pass) {
        if(!CMS.isStatus(CMS.Status.LOGGED_IN)) login(ref, id, pass);
        CMS.setStatus(CMS.Status.PROFILE);

        if(!ref.webview.getCurrentPage().equals(CMSConstants.URL_PROFILE)) ref.webview.enqueueUrl(CMSConstants.URL_PROFILE);

        final String tempquery = JSBuilder.getAllDivTextFromClassName(CMSConstants.PROFILE_DETAILS_DIV_FIELD);
        ref.webview.enqueueUrl(tempquery)
                .setWebViewXListener(new WebViewX.WebViewXListener() {
                    @Override
                    public void onQueueStarted() {

                    }

                    @Override
                    public void onQueueFinished() {

                    }

                    @Override
                    public void onDataAvailable(String js, String data) {
                        Log.d("DB", tempquery);
                        if (js.equals(tempquery)) {
                            Log.d("DB", "Setting details");
                            String name = data.substring(data.indexOf(CMSConstants.PROFILE_DETAILS_NAME) + CMSConstants.PROFILE_DETAILS_NAME.length(), data.indexOf(CMSConstants.PROFILE_DETAILS_PROGRAMME));
                            String programme = data.substring(data.indexOf(CMSConstants.PROFILE_DETAILS_PROGRAMME) + CMSConstants.PROFILE_DETAILS_PROGRAMME.length(), data.indexOf(CMSConstants.PROFILE_DETAILS_BRANCH));
                            String branch = data.substring(data.indexOf(CMSConstants.PROFILE_DETAILS_BRANCH) + CMSConstants.PROFILE_DETAILS_BRANCH.length(), data.indexOf(CMSConstants.PROFILE_DETAILS_REGULATION));
                            String regulation = data.substring(data.indexOf(CMSConstants.PROFILE_DETAILS_REGULATION) + CMSConstants.PROFILE_DETAILS_REGULATION.length(), data.indexOf(CMSConstants.PROFILE_DETAILS_SECTION));
                            String section = data.substring(data.indexOf(CMSConstants.PROFILE_DETAILS_SECTION) + CMSConstants.PROFILE_DETAILS_SECTION.length(), data.indexOf(CMSConstants.PROFILE_DETAILS_FATHER_NAME));
                            String fathername = data.substring(data.indexOf(CMSConstants.PROFILE_DETAILS_FATHER_NAME) + CMSConstants.PROFILE_DETAILS_FATHER_NAME.length(), data.indexOf(CMSConstants.PROFILE_DETAILS_DOB));
                            String dob = data.substring(data.indexOf(CMSConstants.PROFILE_DETAILS_DOB) + CMSConstants.PROFILE_DETAILS_DOB.length(), data.indexOf(CMSConstants.PROFILE_DETAILS_GENDER));
                            String gender = data.substring(data.indexOf(CMSConstants.PROFILE_DETAILS_GENDER) + CMSConstants.PROFILE_DETAILS_GENDER.length(), data.indexOf(CMSConstants.PROFILE_DETAILS_BLOOD_GROUP));
                            String bloodgroup = data.substring(data.indexOf(CMSConstants.PROFILE_DETAILS_BLOOD_GROUP) + CMSConstants.PROFILE_DETAILS_BLOOD_GROUP.length(), data.indexOf(CMSConstants.PROFILE_DETAILS_MOBILE));
                            String mobile = data.substring(data.indexOf(CMSConstants.PROFILE_DETAILS_MOBILE) + CMSConstants.PROFILE_DETAILS_MOBILE.length(), data.indexOf(CMSConstants.PROFILE_DETAILS_PARENT_MOBILE));
                            String parentmobile = data.substring(data.indexOf(CMSConstants.PROFILE_DETAILS_PARENT_MOBILE) + CMSConstants.PROFILE_DETAILS_PARENT_MOBILE.length());
                            DatabaseReference subDB = db.child(CMSConstants.DB_DETAILS_CHILD_FIELD);
                            // THAT'S A LOG OF REGEX!
                            subDB.child(id).child(DB_DETAILS_NAME).setValue(name.replaceAll("[.#$]", "").replace("\\[", "").replace("\\]", "").replace("\\\\", "").replace("\"", ""));
                            subDB.child(id).child(DB_DETAILS_PROGRAMME).setValue(programme.replaceAll("[.#$]", "").replace("\\[", "").replace("\\]", "").replace("\\\\", "").replace("\"", ""));
                            subDB.child(id).child(DB_DETAILS_BRANCH).setValue(branch.replaceAll("[.#$]", "").replace("\\[", "").replace("\\]", "").replace("\\\\", "").replace("\"", ""));
                            subDB.child(id).child(DB_DETAILS_REGULATION).setValue(regulation.replaceAll("[.#$]", "").replace("\\[", "").replace("\\]", "").replace("\\\\", "").replace("\"", ""));
                            subDB.child(id).child(DB_DETAILS_SECTION).setValue(section.replaceAll("[.#$]", "").replace("\\[", "").replace("\\]", "").replace("\\\\", "").replace("\"", ""));
                            subDB.child(id).child(DB_DETAILS_FATHER_NAME).setValue(fathername.replaceAll("[.#$]", "").replace("\\[", "").replace("\\]", "").replace("\\\\", "").replace("\"", ""));
                            subDB.child(id).child(DB_DETAILS_DOB).setValue(dob.replaceAll("[.#$]", "").replace("\\[", "").replace("\\]", "").replace("\\\\", "").replace("\"", ""));
                            subDB.child(id).child(DB_DETAILS_GENDER).setValue(gender.replaceAll("[.#$]", "").replace("\\[", "").replace("\\]", "").replace("\\\\", "").replace("\"", ""));
                            subDB.child(id).child(DB_DETAILS_BLOOD_GROUP).setValue(bloodgroup.replaceAll("[.#$]", "").replace("\\[", "").replace("\\]", "").replace("\\\\", "").replace("\"", ""));
                            subDB.child(id).child(DB_DETAILS_MOBILE).setValue(mobile.replaceAll("[.#$]", "").replace("\\[", "").replace("\\]", "").replace("\\\\", "").replace("\"", ""));
                            subDB.child(id).child(DB_DETAILS_PARENT_MOBILE).setValue(parentmobile.replaceAll("[.#$]", "").replace("\\[", "").replace("\\]", "").replace("\\\\", "").replace("\"", ""));
                        }
                    }
                });
        ref.webview.startQueue();
    }
}
