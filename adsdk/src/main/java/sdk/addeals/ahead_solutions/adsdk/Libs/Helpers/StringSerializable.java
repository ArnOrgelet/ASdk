package sdk.addeals.ahead_solutions.adsdk.Libs.Helpers;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ArnOr on 06/05/2017.
 */

public interface StringSerializable<T> {
    public String SerializeObjects(Object o);
    public List<T> DeserializeObjects(String rawStr);
    public T DeserializeObject(String rawStr);
}
