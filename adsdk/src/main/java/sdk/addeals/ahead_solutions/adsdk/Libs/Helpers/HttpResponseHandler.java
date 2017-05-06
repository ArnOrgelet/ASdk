package sdk.addeals.ahead_solutions.adsdk.Libs.Helpers;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;

/**
 * Created by ArnOr on 06/05/2017.
 */

public class HttpResponseHandler {
    public HttpResponse httpResponse;
    public HttpEntity httpEntity;
    public HttpResponseHandler(HttpResponse _httpResponse){
        httpResponse = _httpResponse;
        extractResponse();
    }

    private void extractResponse(){
        if(httpResponse != null){
            httpEntity = httpResponse.getEntity();
        }
    }

    public HttpEntity getResponse(){
        return httpEntity;
    }

    public String getResponseString(){
        InputStream responseBody = null;
        String result = StringHelper.Empty;
        try{
            responseBody = httpEntity.getContent();
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
                result = result.replace("\"=\"", "\":\"");
                inputStreamReader.close();
            }
        }
        catch (Exception ex){}
        return result;
    }

    public <T extends Object> List<T> getResponseEntities(DataMapper mapper, Class<List<T>> type)
    {
        String responseStr = getResponseString();
        List<T> result = null;
        try{
            result = type.cast(mapper.mapToEntity(responseStr));
        }catch (Exception ex){

        }
        return result;
    }

    public <T extends Object> T getResponseEntity(DataMapper mapper, Class<T> type)
    {
        String responseStr = getResponseString();
        T result = null;
        try{
            result = type.cast(mapper.mapToEntity(responseStr));
        }catch (Exception ex){}
        return result;
    }
}
