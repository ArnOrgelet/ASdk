package sdk.addeals.ahead_solutions.adsdk;

import android.databinding.BaseObservable;

/**
 * Created by ArnOr on 13/05/2017.
 */

public interface MainActivityContract {
    public interface Presenter {
        void onShowData(BaseObservable observableData);
    }

    public interface BindingView {
        void showData(BaseObservable observableData);
    }
}
