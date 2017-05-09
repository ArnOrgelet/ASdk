package sdk.addeals.ahead_solutions.adsdk.EventModels;

/**
 * Created by ArnOr on 09/05/2017.
 */

public interface ISetupListener {

    /// <summary>
    /// SDK could not be initialized.
    /// </summary>
    public /*static*/ void onInitSDKFailed(Object sender);

    /// <summary>
    /// SDK has been successfully initialized.
    /// </summary>
    public /*static*/ void onInitSDKSuccess(Object sender);

    /// <summary>
    /// This event is used to notify the developer that the user has downloaded the app from AdDeals links or not.
    /// This will return AdDeals in case you launch advertising campaigns on AdDeals. Like special exclusive offers for targeted AdDeals or social networks users...
    /// </summary>
    public /*static*/ void onAppDownloadSourceDetected(Object sender);

    /// <summary>
    /// This event is used to notify the developer that the user has launched the app following a click on an AdDeals ad or coming from an AdDeals link.
    /// This also provides AdDeals origin campaign information to display specific ads to end users (Like for targeted exclusive offers!)
    /// </summary>
    public /*static*/ void onAppSessionSourceDetected(Object sender);
}
