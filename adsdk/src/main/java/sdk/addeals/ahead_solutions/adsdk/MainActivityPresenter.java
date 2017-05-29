package sdk.addeals.ahead_solutions.adsdk;

import android.databinding.BaseObservable;

/**
 * Created by ArnOr on 13/05/2017.
 */

public class MainActivityPresenter implements MainActivityContract.Presenter {
    private MainActivityContract.BindingView bindingView;
    public MainActivityPresenter(MainActivityContract.BindingView view) {
        this.bindingView = view;
    }

    @Override
    public void onShowData(BaseObservable observableData) {
        bindingView.showData(observableData);
    }
}
