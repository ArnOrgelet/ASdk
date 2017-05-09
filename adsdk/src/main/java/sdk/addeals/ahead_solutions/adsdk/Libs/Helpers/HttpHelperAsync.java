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

public class HttpHelperAsync extends HttpHelper implements Callable<HttpResponseHandler> {
    private static ExecutorService _executor;
    private final static int POOL_SIZE = 3;
    public HttpHelperAsync(int timeout){
        super(timeout);
        _executor = Executors.newFixedThreadPool(POOL_SIZE);
        //_executor.shutdown();
    }
    public HttpHelperAsync(){
        super();
        _executor = Executors.newFixedThreadPool(POOL_SIZE);
        //_executor.shutdown();
    }

    @Override
    public HttpResponseHandler call() throws Exception {
        return super.sendRequest(_request);
    }

    //@Override
    protected final HttpResponseHandler sendRequest(final HttpRequest request){
        this._request = request;
        HttpResponseHandler responseHandler = null;
        try {
            Future<HttpResponseHandler> async_response = _executor.submit(this);
            responseHandler = async_response.get();
        }
        catch(Exception ex){}
        finally {
            this._request = null;
        }
        return responseHandler;
    }

    public HttpResponseHandler get(String url){ return sendRequest(new HttpGet(url)); }
    public HttpResponseHandler post(String url){
        return sendRequest(new HttpPost(url));
    }
    public HttpResponseHandler put(String url){
        return sendRequest(new HttpPut(url));
    }
    public HttpResponseHandler delete(String url){
        return sendRequest(new HttpDelete(url));
    }
}
