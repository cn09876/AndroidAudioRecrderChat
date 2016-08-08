package com.words.utils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.impl.client.BasicCookieStore;
import cz.msebera.android.httpclient.impl.cookie.BasicClientCookie;

/**
 * Created by cn09876 on 16/8/1.
 */

public class SwHttp
{
    private static final String BASE_URL = "http://jy.dalianit.com/api_v1/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler)
    {
        BasicCookieStore cooks=new BasicCookieStore();
        cooks.addCookie(new BasicClientCookie("cookiesare", "awesome"));
        client.addHeader("auth","sunway");
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl)
    {
        if(relativeUrl.indexOf("http://")>=0)
        {
            return relativeUrl;
        }
        return BASE_URL + relativeUrl;
    }
}
