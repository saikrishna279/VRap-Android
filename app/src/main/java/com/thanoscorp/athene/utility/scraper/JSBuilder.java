package com.thanoscorp.athene.utility.scraper;

import android.util.Log;

/**
 *
 *  Builds JavaScripts from given Params to Get/Inject Data into the WebView
 *
 */

public class JSBuilder {

    public static String setFieldText(String fieldName, String text){
        text = "javascript:document.getElementsByName('" + fieldName + "')[0].value='" + text + "';";
        Log.d("JS", text);
        return text;
    }

    public static String getAllDivTextFromClassName(String divClassName){
        return "javascript:document.getElementsByClassName('" + divClassName + "')[0].innerText + document.getElementsByClassName('" + divClassName + "')[1].innerText";
    }

    public static String getDivTextFromClassName(String divClassName){
        return "javascript:document.getElementsByClassName('" + divClassName + "')[0].innerTex";
    }

    public static String clickButton(String fieldName){
        return "javascript:document.getElementsByName('" + fieldName + "')[0].click();";

    }
}
