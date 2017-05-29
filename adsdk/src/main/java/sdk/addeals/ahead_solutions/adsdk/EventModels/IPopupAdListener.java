package sdk.addeals.ahead_solutions.adsdk.EventModels;

/**
 * Created by ArnOr on 21/05/2017.
 */

public interface IPopupAdListener {

    //region Events

    /// <summary>
    /// Event raised when the delay before you can request a new ad is not elapsed.
    /// </summary>
    public void onMinDelayBtwAdsNotReached(Object sender);

    /// <summary>
    /// Event raised when no Ad is available at all or an issue occured on ShowAd() (no/poor internet connection...)
    /// </summary>
    public void onShowAdFailed(Object sender);

    /// <summary>
    /// Event raised when AdDeals Ad is displayed successfully on ShowAd()
    /// </summary>
    public void onShowAdSuccess(Object sender);

    /// <summary>
    /// Event raised when Ad could not be cached on CacheAd()
    /// </summary>
    public void onCacheAdFailed(Object sender);

    /// <summary>
    /// Event raised when Ad was cached successfully on CacheAd()
    /// </summary>
    public void onCacheAdSuccess(Object sender);

    /// <summary>
    /// Ad has been closed by end user.
    /// </summary>
    public void onAdClosed(Object sender);

    /// <summary>
    /// Ad has been clicked by end user.
    /// </summary>
    public void onAdClicked();

    /// <summary>
    /// A video has been viewed completely and the user can be rewarded for it!
    /// </summary>
    public void onVideoRewardGranted(Object sender);
}
