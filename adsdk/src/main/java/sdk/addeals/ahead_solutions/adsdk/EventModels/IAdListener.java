package sdk.addeals.ahead_solutions.adsdk.EventModels;

/**
 * Created by ArnOr on 09/05/2017.
 */

public interface IAdListener {
    /// <summary>
    /// Event raised when an Ad is available and displayed.
    /// </summary>
    public void onAdDisplayed(Object sender);

    /// <summary>
    /// Event raised when no AdDeals Ad is available.
    /// </summary>
    public void onAdNotAvailable(Object sender);

    /// <summary>
    /// Event raised when ad cannot be refreshed manually (less than 30 seconds between 2 ad requests).
    /// </summary>
    public void onAdCannotBeRefreshed(Object sender);

    /// <summary>
    /// Event raised when an ad is requested on an incompatible platform (Windows Phone instead of Windows PC/Tablet ads...).
    /// </summary>
    public void onAdNotCompatible(Object sender);

    /// <summary>
    /// Ad has been clicked by end user.
    /// </summary>
    public void onAdClicked(Object sender);

    public void onSDKNotInitializedYet(Object sender);
}
