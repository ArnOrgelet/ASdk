package sdk.addeals.ahead_solutions.adsdk.Libs.Helpers;

import java.util.List;

/**
 * Created by ArnOr on 06/05/2017.
 */

public interface DataMapper<T> {
    //public void parse();
    public T mapToEntity(String formattedDataStr);
    public List<T> mapToEntityList(String formattedDataStr);
}
