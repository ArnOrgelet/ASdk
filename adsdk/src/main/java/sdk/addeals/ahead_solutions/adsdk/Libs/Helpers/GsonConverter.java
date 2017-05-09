package sdk.addeals.ahead_solutions.adsdk.Libs.Helpers;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ArnOr on 02/05/2017.
 */

public class GsonConverter<T> extends HttpHelperAsync implements DataMapper<T>/*implements Callable<T>*/ {
    private Type _typeOfT;
    //private String _json;
    public GsonConverter(){
        //this._json = StringHelper.Empty_Json;
        this._typeOfT = /*(Class<T>)*/
                ((ParameterizedType)getClass()
                        .getGenericSuperclass())
                        .getActualTypeArguments()[0];
    }

    public String mapToString(Object o)//SerializeObjects(Object o)
    {
        return (new Gson()).toJson(o);//, new ArrayList<T>().getClass());
    }

    @Override
    public List<T> mapToEntityList(String formattedDataStr)//DeserializeObjects(String json)
    {
        return (new Gson()).fromJson(formattedDataStr, new ArrayList<T>().getClass());
    }

    @Override
    public T mapToEntity(String formattedDataStr)//DeserializeObject(String json)
    {
        return (new Gson()).fromJson(formattedDataStr, _typeOfT);
    }

    /*
    /// <summary>
    /// GENERIC CALL TO GET JSON OBJECTS FROM WEB SERVICES (GET METHOD)
    /// </summary>
    /// <typeparam name="T"></typeparam>
    /// <param name="urlToCall"></param>
    /// <returns></returns>
    public List<T> getDataListFromJsonWeb(String urlToCall) throws IllegalArgumentException
    //public static List<T> ReturnContainer<T> test(Class<T> incomingClass)
    {
        String result;
        //_timeoutTimer.Start();
        try
        {
            HttpResponseHandler response = sendRequest(new HttpGet(new URI(urlToCall)));
            TypeToken<List<T>> listT = new TypeToken<List<T>>() {};
            String dataStr = response.getResponseEntity(this, Class.forName(T));//.getRawType().getSimpleName()
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
    */
    @Override
    public HttpResponseHandler call() throws Exception {
        HttpResponseHandler resp = super.call();
        return resp;
    }
}
