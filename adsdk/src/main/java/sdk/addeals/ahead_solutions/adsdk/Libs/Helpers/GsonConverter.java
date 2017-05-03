package sdk.addeals.ahead_solutions.adsdk.Libs.Helpers;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by ArnOr on 02/05/2017.
 */

public class GsonConverter<T> extends HttpHelperAsync /*implements Callable<T>*/ {
    private Class<T> _typeOfT;
    private String _json;
    public GsonConverter(){
        this._json = StringHelper.Empty_Json;
        this._typeOfT = (Class<T>)
                ((ParameterizedType)getClass()
                        .getGenericSuperclass())
                        .getActualTypeArguments()[0];
    }

    public List<T> DeserializeObjects()//String json)
    {
        return (new Gson()).fromJson(_json, new ArrayList<T>().getClass());
    }

    public T DeserializeObject()//String json)
    {
        return (new Gson()).fromJson(_json, _typeOfT);
    }


    /// <summary>
    /// GENERIC CALL TO GET JSON OBJECTS FROM WEB SERVICES (GET METHOD)
    /// </summary>
    /// <typeparam name="T"></typeparam>
    /// <param name="urlToCall"></param>
    /// <returns></returns>
    public List<T> getDataListFromJsonWeb(String urlToCall) throws IllegalArgumentException
    //public static List<T> RoeturnContainer<T> test(Class<T> incomingClass)
    {
        String result;
        //_timeoutTimer.Start();
        try
        {
            HttpResponse response = sendRequest(new HttpGet(new URI(urlToCall)));
            String dataStr = getResponseString(response);

            // Fix bug json
            result = dataStr.replace("\"=\"", "\":\"");
        }
        catch (Exception ex)
        {
            throw new IllegalArgumentException(EXCEPTION_NULL_VALUE_MESSAGE);
        }
        return new GsonConverter<T>().DeserializeObjects(result);
    }

    /// <summary>
    /// GENERIC CALL TO GET JSON OBJECTS FROM WEB SERVICES (GET METHOD)
    /// </summary>
    /// <typeparam name="T"></typeparam>
    /// <param name="urlToCall"></param>
    /// <returns></returns>
    public T getDataFromJsonWeb(String urlToCall) throws IllegalArgumentException
    {
        String result;
        //_timeoutTimer.Start();
        try
        {
            HttpResponse response = sendRequest(new HttpGet(new URI(urlToCall)));
            String dataStr = getResponseString(response);

            // Fix bug json
            result = dataStr.replace("\"=\"", "\":\"");
        }
        catch (Exception ex)
        {
            throw new IllegalArgumentException(EXCEPTION_NULL_VALUE_MESSAGE);
        }
        GsonConverter convert = new GsonConverter<T>();
        return convert.DeserializeObject(result);
    }

    /// <summary>
    /// GENERIC CALL TO GET STRING FROM WEB SERVICES (GET METHOD)
    /// </summary>
    /// <param name="urlToCall"></param>
    /// <returns></returns>
    public String getStringFromWeb(String urlToCall)
    {
        String result;
        //_timeoutTimer.Start();
        try
        {
            HttpResponse response = sendRequest(new HttpGet(new URI(urlToCall)));
            String dataStr = getResponseString(response);

            // Fix bug json
            result = dataStr.replace("\"=\"", "\":\"");
        }
        catch (Exception ex)
        {
            throw new IllegalArgumentException(EXCEPTION_NULL_VALUE_MESSAGE);
        }
        return result;
    }
    @Override
    public T call() throws Exception {
        return null;
    }
}
