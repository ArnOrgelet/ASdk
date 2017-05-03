package sdk.addeals.ahead_solutions.adsdk.Libs.Helpers;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpResponse;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;

/**
 * Created by ArnOr on 02/05/2017.
 */

public class HttpHelper<T> {

    private final String EXCEPTION_NULL_VALUE_MESSAGE = "Return Value is NULL";
    private final String EXCEPTION_NULL_PARAMS_MESSAGE = "WS (GetDataFromJsonWeb) exception";

    //DispatcherTimer _timeoutTimer;
    HttpRequest _request;
    private int _timeout;

    public HttpHelper(int timeout)
    {
        _timeout = timeout;
        _request = null;/*
        _timeoutTimer = new DispatcherTimer();
        _timeoutTimer.Tick += timeoutTimer_Tick;
        _timeoutTimer.Interval = new TimeSpan(0, 0, 0, timeout, 0);*/
        // Have one (or more) threads ready to do the async tasks. Do this during startup of your app.
    }

    public HttpHelper()
    {
        this(2000);
    }

    protected HttpResponse sendRequest(HttpRequest request) throws Exception {
        CloseableHttpClient httpclient = null;
        try {
            _request = request;//new HttpGet("http://httpbin.org/get");
            BasicHttpContext context = new BasicHttpContext();
            final HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, _timeout);

            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setSocketTimeout(_timeout)
                    .setConnectTimeout(_timeout)
                    .setConnectionRequestTimeout(_timeout)
                    .setStaleConnectionCheckEnabled(true)
                    .build();

            httpclient = HttpClients.custom()
                    .setDefaultRequestConfig(defaultRequestConfig)
                    .build();//.createDefault(httpParams);

            //System.out.println("Executing request " + _request.getURI());
            CloseableHttpResponse response = httpclient.execute((HttpGet)_request, context);
            try {
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                HttpEntity entity1 = response.getEntity();
                // do something useful with the response body
                String bodyAsString = EntityUtils.toString(entity1);
                System.out.println(bodyAsString);
                // and ensure it is fully consumed (this is how stream is released.
               // EntityUtils.consume(entity1);
            }
            finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
    }

    protected static String getResponseString(HttpResponse response){
        InputStream responseBody = null;
        String result = StringHelper.Empty;
        try{
            responseBody = response.getEntity().getContent();
            BufferedInputStream bis = new BufferedInputStream(responseBody);
            InputStreamReader inputStreamReader = new InputStreamReader(bis);
            try{
                int data = inputStreamReader.read();
                while(data != -1){
                    result += (char) data;
                    data = inputStreamReader.read();
                }
            }
            catch (Exception ex){       }
            finally {
                inputStreamReader.close();
            }
        }
        catch (Exception ex){}
        return result;
    }
}
