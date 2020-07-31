package com.thanoscorp.athene.utility.scraper;

/**
 *
 *  Constants declared in the https://student.vrseconline.in/ page HTML.
 *
 *  These will be used by the scraper to fill forms and get data in background from WebView.
 *
 */

public class CMSConstants {

    public static String URL_FORGOT_PASSWORD = "https://student.vrseconline.in/login/forgot_password";
    public static String URL_SIGN_IN = "https://student.vrseconline.in/login";
    public static String URL_PROFILE = "https://student.vrseconline.in/dashboard/";

    public static String DB_CMSPASS_CHILD_FIELD = "CMSPASS";
    public static String DB_NAMES_CHILD_FIELD =  "NAMES";
    public static String DB_DETAILS_CHILD_FIELD = "DETAILS";
    public static String DB_DETAILS_NAME = "name";
    public static String DB_DETAILS_PROGRAMME = "programme";
    public static String DB_DETAILS_BRANCH = "branch";
    public static String DB_DETAILS_REGULATION = "regulation";
    public static String DB_DETAILS_SECTION = "section";
    public static String DB_DETAILS_FATHER_NAME = "father";
    public static String DB_DETAILS_DOB = "dob";
    public static String DB_DETAILS_GENDER = "gender";
    public static String DB_DETAILS_BLOOD_GROUP = "bloodgroup";
    public static String DB_DETAILS_MOBILE = "mobile";
    public static String DB_DETAILS_PARENT_MOBILE = "parentmobile";

    public static String LOGIN_ID_FIELD = "hall_ticket_no";
    public static String LOGIN_PASSWORD_FIELD = "password";
    public static String LOGIN_BUTTON_FIELD = "submit";
    public static String LOGIN_FAILED_ATTEMPT = "alert alert-danger fade in ";

    public static String FORGOT_PASSWORD_ID_FIELD = LOGIN_ID_FIELD;
    public static String FORGOT_PASSWORD_MOBILE_FIELD = "mobile";
    public static String FORGOT_PASSWORD_BUTTON_FIELD = LOGIN_BUTTON_FIELD;
    public static String FORGOT_PASSWORD_OTP_FIELD = "otp";
    public static String FORGOT_PASSWORD_OTP_BUTTON_FIELD = LOGIN_BUTTON_FIELD;

    public static String PROFILE_DETAILS_DIV_FIELD = "panel-body bio-graph-info";
    public static String PROFILE_DETAILS_NAME = "\\n\\nName: ";
    public static String PROFILE_DETAILS_PROGRAMME = "\\n\\nProgramme: ";
    public static String PROFILE_DETAILS_BRANCH = "\\n\\nBranch : ";
    public static String PROFILE_DETAILS_REGULATION = "\\n\\nRegulation: ";
    public static String PROFILE_DETAILS_SECTION = "\\n\\nSection: ";
    public static String PROFILE_DETAILS_FATHER_NAME = "Personal Details\\n\\nFather Name: ";
    public static String PROFILE_DETAILS_DOB = "\\n\\nDate of Birth: ";
    public static String PROFILE_DETAILS_GENDER = "\\n\\nGender: ";
    public static String PROFILE_DETAILS_BLOOD_GROUP = "\\n\\nBlood Group: ";
    public static String PROFILE_DETAILS_MOBILE = "\\n\\nMobile: ";
    public static String PROFILE_DETAILS_PARENT_MOBILE = "\\n\\nParent Mob: ";


}
