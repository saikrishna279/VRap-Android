package com.thanoscorp.athene.utility;

import java.util.HashMap;

/**
 *
 *  Use during creation of Firebase Accounts.
 *
 *  Format: <regd. no>@vrap.com
 *
 *  You may change the mailserver name as your wish.
 *
 */
public class User {

    private static String asMail = "@vrap.com";

    public static String createUIDfromID(String id) {
        return id + asMail;
    }

    public static String createIDfromUID(String uid){
        return uid.substring(0, uid.indexOf(asMail)).toUpperCase();
    }

    public static HashMap<String, String> parseUserDetails(String string) {
        return null;
    }
}
