package sdk.addeals.ahead_solutions.adsdk.EventModels;

/**
 * Created by ArnOr on 09/05/2017.
 */

public class DefaultSetupListener<IEventListener> extends EventListener implements ISetupListener {
    /*public BasicInitSetupEvent(Observable<ISetup> obs){
        super(obs);
    }*/
    @Override
    public void onInitSDKFailed(Object sender) {

    }

    @Override
    public void onInitSDKSuccess(Object sender) {

    }

    @Override
    public void onAppDownloadSourceDetected(Object sender) {

    }

    @Override
    public void onAppSessionSourceDetected(Object sender) {

    }
}
