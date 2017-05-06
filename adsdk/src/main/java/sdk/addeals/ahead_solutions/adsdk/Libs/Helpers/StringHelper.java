package sdk.addeals.ahead_solutions.adsdk.Libs.Helpers;

/**
 * Created by ArnOr on 02/05/2017.
 */

public class StringHelper {
    public final static String Empty = "";
    public final static String Empty_Json = "{}";
    public static boolean isNullOrEmpty(String str){
        if(str == null || Empty.equals(str.trim()))
            return true;
        return false;
    }
}
