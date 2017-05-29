package sdk.addeals.ahead_solutions.adsdk.Libs.Helpers;

import android.os.AsyncTask;
import android.widget.Toast;

/**
 * Created by ArnOr on 21/05/2017.
 */

public class CustomAsyncTask<T, U, V> extends AsyncTask<T, U, V> {
    protected AsyncTaskInterface asyncTaskInterface;

    public CustomAsyncTask(AsyncTaskInterface asyncTaskInterface){
        this.asyncTaskInterface = asyncTaskInterface;
    }

    @Override
    protected void onPreExecute() {
        asyncTaskInterface.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(U... values){
        asyncTaskInterface.onProgressUpdate(values);
    }

    @Override
    protected V doInBackground(T... arg0) {
        return (V)asyncTaskInterface.doInBackground(arg0);
    }
    /*
    public V execute(T... arg0){
        return super.execute(arg0);
    }*/
}
