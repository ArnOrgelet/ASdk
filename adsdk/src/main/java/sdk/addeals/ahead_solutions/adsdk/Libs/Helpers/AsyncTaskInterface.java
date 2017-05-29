package sdk.addeals.ahead_solutions.adsdk.Libs.Helpers;

import android.widget.Toast;

/**
 * Created by ArnOr on 21/05/2017.
 */

public interface AsyncTaskInterface<T, U, V> {
    public void onPreExecute();
    public V doInBackground(T... arg0);
    public void onProgressUpdate(U... values);
}
