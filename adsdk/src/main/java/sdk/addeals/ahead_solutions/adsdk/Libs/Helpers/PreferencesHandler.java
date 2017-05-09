package sdk.addeals.ahead_solutions.adsdk.Libs.Helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ArnOr on 08/05/2017.
 */

public class PreferencesHelper {
    private SharedPreferences preferences;
    public PreferencesHelper(Activity activity){
        preferences = activity.getPreferences(MODE_PRIVATE);
    }
    public void storePreference(Map<String, Object> params){
        SharedPreferences.Editor editor = preferences.edit();
        for(Map.Entry<String, Object> o : params.entrySet()){
            String key = o.getKey();
            Object val = o.getValue();
            if(val instanceof String){
                editor.putString(key, (String)val);
            }
            if(val instanceof Boolean){
                editor.putBoolean(key, (boolean)val);
            }
            if(val instanceof Float){
                editor.putFloat(key, (float)val);
            }
            if(val instanceof Long){
                editor.putLong(key, (long)val);
            }
            if(val instanceof Integer){
                editor.putInt(key, (int)val);
            }
        }
        editor.commit();
    }
    public <T extends Object> T getPreference(String key, Class<T> type){
        switch (type.getName().toLowerCase()){
            case "string":
                return type.cast(preferences.getString(key, StringHelper.Empty));
            case "boolean":
                return type.cast(preferences.getBoolean(key, false));
            case "float":
                return type.cast(preferences.getFloat(key, -1));
            case "long":
                return type.cast(preferences.getLong(key, -1));
            case "integer":
                return type.cast(preferences.getInt(key, -1));
            default:
                return null;
        }
    }
}
