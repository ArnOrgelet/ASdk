package sdk.addeals.ahead_solutions.adsdk.Libs.Helpers;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpDelete;

import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by ArnOr on 02/05/2017.
 */

public class HttpHelperAsync<T> extends HttpHelper<T> implements Callable<HttpResponse> {
    private static ExecutorService _executor;
    private final static int POOL_SIZE = 3;
    public HttpHelperAsync(){
        super();
        _executor = Executors.newFixedThreadPool(POOL_SIZE);
        //_executor.shutdown();
    }

    @Override
    public HttpResponse call() throws Exception {
        return super.sendRequest(_request);
    }

    //@Override
    protected final HttpResponse sendRequest(final HttpRequest request){
        this._request = request;
        HttpResponse response = null;
        try {
            Future<HttpResponse> async_response = _executor.submit(this);
            response = async_response.get();
        }
        catch(Exception ex){}
        finally {
            this._request = null;
        }
        return response;
    }

    public void get(String url){
        sendRequest(new HttpGet(url));
    }
    public void post(String url){
        sendRequest(new HttpPost(url));
    }
    public void put(String url){
        sendRequest(new HttpPut(url));
    }
    public void delete(String url){
        sendRequest(new HttpDelete(url));
    }
}
