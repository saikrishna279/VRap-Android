package com.thanoscorp.athene.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.HashSet;
import java.util.LinkedList;

/**
 *
 *  A Custom WebView Implementation that loads URL's and executes javascripts on the loaded webpages.
 *
 *  The URL's to be loaded must be enqueued and they will be processed in a FIFO Order.
 *
 */
public class WebViewX extends WebView implements ValueCallback<String> {

    private LinkedList<String> URList = new LinkedList<>();
    private boolean isQueueWaiting = true; // THIS FLAG INDICATES THAT THE QUEUE HAS TRULY COMPLETED EXECUTION
    private boolean isLastQueryDone = true;
    private boolean isInit = false;
    private boolean isLastQueryJS;
    private String presentURL = "null";
    private String lastLink = "null", lastJS = "null";
    private HashSet<WebViewXListener> listeners = new HashSet<>();

    public interface WebViewXListener {
        public void onQueueStarted();

        public void onQueueFinished();

        public void onDataAvailable(String url, String data);
    }

    public WebViewX(Context context) {
        super(context);
    }

    public WebViewX(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WebViewX(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public WebViewX(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public WebViewX(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
    }

    public void setWebViewXListener(WebViewXListener listener) {
        listeners.add(listener);
    }

    public WebViewX enqueueUrl(String url) {
        Log.d("ENQUEUE", url);
        for (WebViewXListener listener : listeners) {
            listener.onQueueStarted();
        }
        initWV();
        URList.addLast(url);
        return this;
    }

    public void startQueue() {
        loadQueue();
    }

    private void loadQueue() {
        if (URList.size() == 0 || !isLastQueryDone) {
            if (!isQueueWaiting) {
                for (WebViewXListener listener : listeners) {
                    listener.onQueueFinished();
                }
                isQueueWaiting = true;
            }
            return;
        }
        isQueueWaiting = false;
        isLastQueryDone = false;
        String link = URList.pollFirst();
        Log.d("WVX", link);
        if (isUrlJS(link)) {
            isLastQueryJS = true;
            lastJS = link;
            evaluateJavascript(link, this);
        } else {
            isLastQueryJS = false;
            lastLink = link;
            loadUrl(link);
        }
    }

    private boolean isUrlJS(String url) {
        return url.contains("javascript:");
    }

    @Override
    public void loadUrl(String url) {
        super.loadUrl(url);
    }

    private void initWV() {
        if (isInit) return;
        setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d("WVX", "Page loading started: " + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                presentURL = url;
                Log.d("WVX", "Page loading finished: " + url + " last link: " + lastLink);
                isLastQueryDone = true;
                loadQueue();
            }
        });
        isInit = true;
    }

    @Override
    public void setWebViewClient(WebViewClient client) {
        if (!isInit) super.setWebViewClient(client);
    }

    @Override
    public void onReceiveValue(String value) {
        if(!lastJS.contains("click()")) {
            Log.d("WVXRESPONSE", value);
            for (WebViewXListener listener : listeners) {
                listener.onDataAvailable(lastJS, value);
            }
            isLastQueryDone = true;
            loadQueue();
        }
    }

    public void killQueue() {
        URList.clear();
        for (WebViewXListener listener : listeners) {
            listener.onQueueFinished();
        }
        isQueueWaiting = true;
        isLastQueryDone = true;
    }

    public String getCurrentPage() {
        return presentURL;
    }
}
