package sdk.addeals.ahead_solutions.adsdk.Libs.Helpers;

/**
 * Created by ArnOr on 07/05/2017.
 */

public class DataMapperFactory {
    public enum Format{
        XML, JSON;
    }
    public static <T> DataMapper<T> create(Format frmt){
        switch(frmt){
            case XML :
                //return new XMLConverter<T>();
                break;
            case JSON:
                return new GsonConverter<T>();
        }
    }
}
