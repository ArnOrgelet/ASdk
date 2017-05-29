package sdk.addeals.ahead_solutions.adsdk.EventModels;

import android.view.View;

import java.beans.PropertyChangeListener;

/**
 * Created by ArnOr on 09/05/2017.
 */

public abstract class ViewPropertyChangeListener implements PropertyChangeListener {
    protected View view;
    public ViewPropertyChangeListener(View _view){
        view = _view;
    }
}
