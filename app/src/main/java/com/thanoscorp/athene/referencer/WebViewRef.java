package com.thanoscorp.athene.referencer;

import com.thanoscorp.athene.models.WebViewX;

/**
 *
 *  Container for WebView object.
 *
 *  Implemented this way so that it cant be referenced from multiple classes.
 *
 *  This is a SHITTY implementation.
 *
 *  Please reimplement this in a way to avoid any and all memory leaks.
 *
 */

public class WebViewRef {
    public WebViewX webview;

    public WebViewRef() {
    }

    public WebViewRef(WebViewX webview) {
        this.webview = webview;
    }
}
