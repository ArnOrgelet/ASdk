package sdk.addeals.ahead_solutions.adsdk.Libs.Helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Layout;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.concurrent;

import java.util.concurrent.Future;

/**
 * Created by ArnOr on 06/05/2017.
 */

public class UserAgentHelper {
    //String ua=new WebView(this).getSettings().getUserAgentString();

    private static final String html =
            "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n" +
                    "< html>\n" +
                    "<head>\n" +
                    "<script language=\"javascript\" type=\"text/javascript\">\n" +
                    "   function notifyUA() {\n" +
                    "       window.external.notify(navigator.userAgent);\n" +
                    "   }\n" +
                    "</script>\n" +
                    "</head>\n" +
                    "<body onload=\"notifyUA();\"></body>\n" +
                    "</html>";
    private static String mime = "text/html";
    private static String encoding = "utf-8";

    protected static class WebAppInterface {
        Context mContext;
        String userAgent;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void retrieveUserAgent(String _userAgent) {
            userAgent = _userAgent;
        }

        public String getUserAgent() {
            return userAgent;
        }
    }

    public static Task<String> GetUserAgent(Context context, final ViewGroup rootElement)
    {
        final WebView browser = new WebView(context);
        CompletableFuture<String> cf = new CompletableFuture<>();
        Task<String> t = null;
        //browser.All = true;
        final int nbSubViews = rootElement.getChildCount();
        browser.setVisibility(WebView.INVISIBLE);
        WebSettings settings = browser.getSettings();
        settings.setJavaScriptEnabled(true);
        final WebAppInterface webInterface = new WebAppInterface(context);
        browser.addJavascriptInterface(webInterface, "android");
        //settings.getUserAgentString();

        browser.setWebViewClient(new WebViewClient() {
            boolean loadingFinished = true;
            boolean redirect = false;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap facIcon) {
                loadingFinished = false;
                //SHOW LOADING IF IT ISNT ALREADY VISIBLE
            }

            @Override
            public void onLoadResource(WebView view, String url){
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                if(!redirect){
                    loadingFinished = true;
                    String userAgent = webInterface.getUserAgent();
                    rootElement.removeViewAt(nbSubViews);
                    t = new Task<String>() {
                        @Override
                        protected String call() {
                            return userAgent;
                        }
                }

                if(loadingFinished && !redirect){
                    //HIDE LOADING IT HAS FINISHED
                } else{
                    redirect = false;
                }

            }
        });
        browser.loadDataWithBaseURL(null, html, mime, encoding, null);
        browser.addView(browser, nbSubViews);
        return
    }
}
}
