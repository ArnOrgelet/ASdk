package sdk.addeals.ahead_solutions.adsdk.Views;

import android.content.Context;
import android.os.Debug;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.net.URI;

import sdk.addeals.ahead_solutions.adsdk.AdManager;
import sdk.addeals.ahead_solutions.adsdk.EventModels.DefaultPopupAdListener;
import sdk.addeals.ahead_solutions.adsdk.EventModels.Event;
import sdk.addeals.ahead_solutions.adsdk.Libs.Helpers.AsyncHelper;
import sdk.addeals.ahead_solutions.adsdk.Libs.Helpers.StringHelper;
import sdk.addeals.ahead_solutions.adsdk.ViewModels.AdDealsPopupAdViewModel;

import static sdk.addeals.ahead_solutions.adsdk.Views.AdDealsBannerAd.AdClicked;

/**
 * Created by ArnOr on 09/05/2017.
 */

public class AdDealsPopupAd extends EventHandingView<AdDealsPopupAdViewModel> {
    public AdDealsPopupAd(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public AdDealsPopupAd(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public AdDealsPopupAd(Context context) {
        super(context);
    }
    /*internal*/ View mainLayout = null;
    /*internal*/ static boolean _showInProgress = false;
    /*internal*/ ApplicationViewOrientation initialAppOrientation = ApplicationViewOrientation.Portrait;
    /*internal*/ screenOrientation initialDisplayOrientation = DisplayOrientations.None;
    boolean _videoCompletedTriggered = false;

    AdManager.AdKind adKind = AdManager.AdKind.FULLSCREENPOPUPAD; // Default is: All interstitial formats supported.
    DispatcherTimer myTimer = new DispatcherTimer();
    DispatcherTimer checkHTMLTimer = new DispatcherTimer();

    internal AdDealsPopupAd(ViewGroup mainPageLayout, AdManager.AdKind adKindSupported)
    {
        initializeComponent();
        this.Init(mainPageLayout, adKindSupported);
        this.myTimer.Interval = new TimeSpan(0, 0, 0, 0, 500);
        this.myTimer.Tick += refreshTimer_Tick;
        this.checkHTMLTimer.Interval = new TimeSpan(0, 0, 0, 1);
        this.checkHTMLTimer.Tick += checkHTMLTimer_Tick;
        Window.Current.SizeChanged += Current_SizeChanged;
    }

    /*internal*/ void setAdRequested(AdManager.AdKind adKindSupported)
    {
        this.adKind = adKindSupported;
    }

    private void refreshTimer_Tick(object sender, object e)
    {
        this.ShowAd();
    }

    /// <summary>
    /// This timer is used to parse the HTML pages loaded and get the Video completed TAG.
    /// </summary>
    /// <param name="sender"></param>
    /// <param name="e"></param>
    private async void checkHTMLTimer_Tick(object sender, object e)
    {
        try
        {
            String html = await MainWebView.InvokeScriptAsync("eval", new String[] { "document.documentElement.outerHTML;" });
            if (!_videoCompletedTriggered && html.contains(AdManager.VIDEO_COMPLETED_HTML_TAG))
            {
                _videoCompletedTriggered = true;

                    /*OBSOLETE if (VideoRewardGranted != null)
                    {
                        VideoRewardGranted(new object(), new EventArgs());  // Notify Subscribers.
                    }*/
            }

            if (html.contains(AdManager.POPUP_CLOSED_HTML_TAG)) {
                this.CleanOnClose();
            }
        }
        catch (Exception) { }
    }

    // INCLUDES BUG FIX: for Windows PC/Tablet Devices (excluding phones), close Windows if resized while visible
    // for Video ads only (HTML5 video player issue).
    private void current_SizeChanged(object sender, Windows.UI.Core.WindowSizeChangedEventArgs e)
    {
        // Get the new view state
        // Add: using Windows.UI.ViewManagement;
        // string currentViewState = ApplicationView.GetForCurrentView().Orientation.ToString();
        ApplicationViewOrientation newOrientation = ApplicationView.GetForCurrentView().Orientation;

        // Refresh screen and ad size.
        AdManager.getPopupViewModel(this.adKind).RefreshScreenSize();

            /*DON'T CLOSE ANYMORE - if (AdManager.DeviceType.TABLET_PC == AdManager.DeviceKind
                && this.adKind == AdManager.AdKind.REWARDEDVIDEOAD)
            {
                this.CleanOnClose();
            }*/
    }

    // Prevent calls from outside.
    internal AdDealsPopupAd()
    {
        // Nothing there.
    }

    // We can preload things there...
    /*internal*/ void Init(ViewGroup mainPageLayout, AdManager.AdKind adKindSupported)
    {
        // Nothing right now.
        this.adKind = adKindSupported;
        DataContext = AdManager.getPopupViewModel(adKindSupported);
        AdManager.adPopup = new Toast.();
        mainLayout = mainPageLayout;
        //mainLayout.Children.Add(popup); // Takes properties from page below!! Including Orientation!
    }

    /*internal*/ void setLayout(Panel mainPageLayout)
    {
        mainLayout = mainPageLayout;
    }

        //region public methods.

    /// <summary>
    /// Allows you to reduce the full screen ad size so its maximal size is centered and does not occupy the whole screen
    /// (0.9 is the minimal value and corresponds to 90%).
    /// </summary>
    /// <param name="percentScreenAd">0.9 is the minimal value, 0.97 is the maximal value</param>
    public void SetPercentScreenAd(double percentScreenAd)
    {
        if (percentScreenAd < 0.9) percentScreenAd = 0.9;
        else if (percentScreenAd > 0.97) percentScreenAd = 0.97;
        AdManager.getPopupViewModel(this.adKind).SetPercentScreenAd(percentScreenAd);
    }

    /// <summary>
    /// Allows developer to set the close button position (we recommend to keep default values unless that's a strict requirement for you)
    /// </summary>
    /// <param name="position"></param>
    public void SetCloseButtonPosition(AdManager.CloseButtonPosition position)
    {
        AdManager.getPopupViewModel(this.adKind).SetCloseButtonPosition(position);
    }


    public /*async*/ boolean IsVideoAvailable()
    {
        if (this.adKind.equals(AdManager.AdKind.REWARDEDVIDEOAD)) {
            return await( () -> this.cacheAd());
        }
        else {
            return false;
        }
    }

    /// <summary>
    /// You can preload ads before displaying them on the same page/view during animations for instance (save couple seconds of real-time loading).
    /// However, would you forget to display the ads within 10 to 15 seconds, your CPM may decrease and you may lose advertisers.
    /// </summary>
    /// <returns>true = ad cached.</returns>
    public /*async*/ boolean cacheAd()
    {
        boolean result = false;
        this.KeepOrEmptyCache();
        if (AdManager.SDKinitialized && !this.IsCachedAdAvailable())
        {
            //if (AdManager.AdDealsPopupAdViewModel.IsDelayPassed())
            {
                AdManager.getPopupViewModel(this.adKind).IsPrefetching = true;
                int res = AsyncHelper.await(AdManager.getPopupViewModel(this.adKind).getNewAd(this.adKind)); // Will not load a new campaign if the previous one has not been displayed yet!
                AdManager.getPopupViewModel(this.adKind).IsPrefetching = false;

                if (res != AdManager.NO_AD_AVAILABLE)
                {
                    AdManager.GetPopupViewModel(this.adKind).IsPrefetched = true;
                    if (CacheAdSuccess != null)
                    {
                        CacheAdSuccess(new object(), new EventArgs());
                    }
                    return true;
                }
                else
                {
                    if (CacheAdFailed != null)
                    {
                        CacheAdFailed(new object(), new EventArgs());
                    }
                }

            }
                /*else
                {
                    if (CacheAdFailed != null)
                    {
                        CacheAdFailed(new object(), new EventArgs());
                    }
                    if (MinDelayBtwAdsNotReached != null)
                    {
                        MinDelayBtwAdsNotReached(new object(), new EventArgs());
                    }
                    this.ReleaseEvents();
                }*/
        }
        else if (this.IsCachedAdAvailable())
        {
            return true;
        }
        else
        {
            if (cacheAdFailed != null)
            {
                cacheAdFailed(new object(), new EventArgs());
            }
        }
        return result;
    }

    /// <summary>
    /// USING THIS IS COMPLETELY OPTIONAL.
    /// If you are currently preloading AdDeals Square/Interstitial, this method will tell you wether prefetching is in progress (= true) or whether it is not (or done)
    /// (prefetching = false).
    /// </summary>
    /// <returns></returns>
    public boolean IsCachingAd()
    {
        return AdManager.getPopupViewModel(this.adKind).IsPrefetching;
    }

    /// <summary>
    /// USING THIS IS COMPLETELY OPTIONAL. Tells whether an ad is prefetched/preloaded & ready to be displayed instantly!
    /// </summary>
    /// <returns></returns>
    public boolean IsCachedAdAvailable()
    {
        return AdManager.getPopupViewModel(this.adKind).isPrefetched();
    }

    /// <summary>
    /// We can only cache 1 interstitial ad or 1 rewarded video at a time.
    /// This methods checks and allows to cache again an ad of a different type).
    /// </summary>
    /// <returns></returns>
    private boolean KeepOrEmptyCache()
    {
        // Cleans up cache info to be able to cache ads again...
        if (AdManager.getPopupViewModel(this.adKind).lastAdKind != this.adKind)
        {
            AdManager.getPopupViewModel(this.adKind).setIsPrefetched(false);
            // Reset delay to be able to cache rewarded videos, without waiting after an interstitial display.
            AdManager.getPopupViewModel(this.adKind).lastInterstitialLaunchDate = new DateTime(1900, 1, 1, 0, 0, 0);
        }
        return true;
    }

    private void InitBeforeShowingAd()
    {
        // Completed event should be triggered only once when a video is viewed completely.
        _videoCompletedTriggered = false;

        // MEMORIZE CURRENT DISPLAY INFORMATION BEFORE SHOWING AD.
        this.KeepOrEmptyCache();
        this.initialAppOrientation = getWindowManager().getDefaultDisplay().getRotation();//ApplicationView.GetForCurrentView().Orientation;
        this.initialDisplayOrientation = getResources().getConfiguration().orientation; //DisplayInformation.AutoRotationPreferences;
    }

    /// <summary>
    /// Try to display ads. Otherwise returns custom events.
    /// </summary>
    public /*async*/ void showAd()
    {
        if (AdManager.SDKinitialized && !AdManager.isPopupOpening && !AdManager.isAdPopupOpen())
        {
            AdManager.isPopupOpening = true;

            this.InitBeforeShowingAd();

            if (!AdManager.getPopupViewModel(this.adKind).isPrefetching())
            {
                this.myTimer.Stop();

                // Check against Minimal Delay before trying to call an ad again...
                if (AdManager.getPopupViewModel(this.adKind).isDelayPassed() || AdManager.getPopupViewModel(this.adKind).isPrefetched())
                {
                    try
                    {
                        //if (true /*await AdManager.IsFastNetworkConnectionAvailable()*/) {
                        if (!AdManager.getPopupViewModel(this.adKind).isPrefetched())
                        {
                            AsyncHelper.await(AdManager.getPopupViewModel(this.adKind).getNewAd(this.adKind));
                        }
                        else AdManager.getPopupViewModel(this.adKind).setIsPrefetched(false);

                        AdManager.getPopupViewModel(this.adKind).setIsPrefetched(false);  // We can reload a new ad!!
                        switch (AdManager.getPopupViewModel(this.adKind).getLastAdCalledStatus())
                        {
                            case AdManager.NO_AD_AVAILABLE:
                            {
                                // Raise event: No Ad available, therefore developers can call another
                                // interstitial ad if they deactivated the free cross-promotion!
                                if (ShowAdFailed != null)
                                {
                                    ShowAdFailed(new object(), new EventArgs());
                                }
                                this.releaseEvents();
                                break;
                            }

                            case AdManager.AD_AVAILABLE:
                            {
                                // OTHERWISE REPORT VIEW ONLY WHEN ADS ARE FULLY LOADED.
                                this.DisplayPopup(); // Display interstitial / or popup

                                if (AdManager.getPopupViewModel(this.adKind).getTmpWebSrcBeforeDisplay().toLowerCase().startsWith("http"))
                                {
                                    AdManager.getPopupViewModel(this.adKind).getWebViewAdSrc() = new URI(AdManager.getPopupViewModel(this.adKind).getTmpWebSrcBeforeDisplay());
                                }   // DISPLAY (Async - Web)!

                                if (AdManager.getPopupViewModel(this.adKind).getWebViewVisibility() == View.GONE) // Count view (NATIVE AD)!
                                {
                                    if (AdManager.getPopupViewModel(this.adKind).getTmpImpressionPixelSrc().toLowerCase().startsWith("http"))
                                    {
                                        AdManager.getPopupViewModel(this.adKind).getImpressionPixelSrc() = new URI(AdManager.getPopupViewModel(this.adKind).getTmpImpressionPixelSrc());
                                    }   // CALL (Async - Web)!
                                }

                                if (ShowAdSuccess != null)
                                {
                                    ShowAdSuccess(new object(), new EventArgs());
                                }

                                //AdManager.AdDealsPopupAdViewModel.lastInterstitialLaunchDate = new DateTime(2000, 0, 0); // A new ad can be cached instantly!
                                break;
                            }

                            default: break;
                        }
                    }
                    catch (Exception)
                    {
                        if (ShowAdFailed != null)
                        {
                            ShowAdFailed(new object(), new EventArgs());
                        }
                        this.releaseEvents();
                    }
                }
                else
                {
                    if (ShowAdFailed != null)
                    {
                        ShowAdFailed(new object(), new EventArgs());
                    }
                    if (!AdManager.getPopupViewModel(this.adKind).isDelayPassed())
                    {
                        if (MinDelayBtwAdsNotReached != null)
                        {
                            MinDelayBtwAdsNotReached(new object(), new EventArgs());
                        }
                    }
                    this.releaseEvents();
                }
            }
            else // Prefetching in progress, try to launch again...
            {
                myTimer.Start();
            }
        }
        else
        {
            if (!AdManager.SDKinitialized)
            {
                if (SDKNotInitialized != null)
                {
                    SDKNotInitialized(new object(), new EventArgs());
                }
            }
        }

        // Reinitialize the delay to be able to get a new ad!
        AdManager.IsPopupOpening = false;
        //AdManager.AdDealsPopupAdViewModel.lastInterstitialLaunchDate = new DateTime(2000,1,1);
    }

    //endregion


    /*internal*/ void displayPopup()
    {
        try
        {
            AdManager.getPopupViewModel(this.adKind).updateModel(0);
            AdManager.getPopupViewModel(this.adKind).refreshScreenSize(); // REQUIRED TO DISPLAY AD CORRECTLY.
            this.myProgressRing.Visibility = View.GONE;
            AdManager.adPopup.Child = this;
            try {
                if (this.adKind == AdManager.AdKind.REWARDEDVIDEOAD
                        && this.initialAppOrientation == ApplicationViewOrientation.Portrait)
                {
                    DisplayInformation.AutoRotationPreferences = DisplayOrientations.Landscape;
                }
            } catch (Exception) { }
            AdManager.adPopup.IsOpen = true;
            if (this.mainLayout != null) this.mainLayout.setAlpha(0.2f);//.Opacity = 0.2;
        }
        catch (Exception e)
        {
            Debug.WriteLine(e.Message);
        }
    }

    /*internal*/ void releaseEvents()
    {
        try
        {
            this.ShowAdSuccess -= ShowAdSuccess; // Guarantees 1 single event called back when opening popups on several pages!
            this.ShowAdFailed -= ShowAdFailed; // Guarantees 1 single event called back when opening popups on several pages!
            this.AdClosed -= AdClosed; // Guarantees 1 single event called back when opening popups on several pages!
            this.AdClicked -= AdClicked; // Guarantees 1 single event called back when opening popups on several pages!
            this.MinDelayBtwAdsNotReached -= MinDelayBtwAdsNotReached;
            this.CacheAdFailed -= CacheAdFailed;
            this.CacheAdSuccess -= CacheAdSuccess;
            this.SDKNotInitialized -= SDKNotInitialized;
            this.VideoRewardGranted -= VideoRewardGranted;
        }
        catch (Exception ex) { }
    }

    /*internal*/ void cleanOnClose()
    {
        if (AdManager.adPopup.IsOpen)
        {
            if (_videoCompletedTriggered)
            {
                if (VideoRewardGranted != null)
                {
                    VideoRewardGranted(new object(), new EventArgs());  // Notify Subscribers.
                }
            }

            if (AdClosed != null)
            {
                AdClosed(new object(), new EventArgs());
            }

            try
            {
                if (checkHTMLTimer.IsEnabled) checkHTMLTimer.Stop();
                this.releaseEvents();
                //this.MainWebView = null;
                //this.ImpressionPixelWebView = null;
                //AdManager.ResetAdDealsSquare();

                AdManager.getPopupViewModel(this.adKind).WebViewAdSrc = null; // To refresh ads!
                AdManager.getPopupViewModel(this.adKind).ImpressionPixelSrc = null; // To clean up impression pixel call.
                AdManager.adPopup.IsOpen = false;
                AdManager.adPopup.Child = null;
                if (this.mainLayout != null) this.mainLayout.Opacity = 1;
                DisplayInformation.AutoRotationPreferences = initialDisplayOrientation;
            }
            catch (Exception ex) { }
        }
    }


    //region Events

    /// <summary>
    /// Event raised when the delay before you can request a new ad is not elapsed.
    /// </summary>
    public static Event MinDelayBtwAdsNotReached = new Event<DefaultPopupAdListener>(){
        public void action(DefaultPopupAdListener adListener){
            adListener.onMinDelayBtwAdsNotReached(this);
        }
    };

    /// <summary>
    /// Event raised when no Ad is available at all or an issue occured on ShowAd() (no/poor internet connection...)
    /// </summary>
    public static Event ShowAdFailed = new Event<DefaultPopupAdListener>(){
        public void action(DefaultPopupAdListener adListener){
            adListener.onShowAdFailed(this);
        }
    };

    /// <summary>
    /// Event raised when AdDeals Ad is displayed successfully on ShowAd()
    /// </summary>
    public static Event ShowAdSuccess = new Event<DefaultPopupAdListener>(){
        public void action(DefaultPopupAdListener adListener){
            adListener.onShowAdSuccess(this);
        }
    };

    /// <summary>
    /// Event raised when Ad could not be cached on CacheAd()
    /// </summary>
    public static Event CacheAdFailed = new Event<DefaultPopupAdListener>(){
        public void action(DefaultPopupAdListener adListener){
            adListener.onCacheAdFailed(this);
        }
    };

    /// <summary>
    /// Event raised when Ad was cached successfully on CacheAd()
    /// </summary>
    public static Event CacheAdSuccess = new Event<DefaultPopupAdListener>(){
        public void action(DefaultPopupAdListener adListener){
            adListener.onCacheAdSuccess(this);
        }
    };

    /// <summary>
    /// Ad has been closed by end user.
    /// </summary>
    public static Event AdClosed = new Event<DefaultPopupAdListener>(){
        public void action(DefaultPopupAdListener adListener){
            adListener.onAdClosed(this);
        }
    };

    /// <summary>
    /// Ad has been clicked by end user.
    /// </summary>
    public static Event AdClicked = new Event<DefaultPopupAdListener>(){
        public void action(DefaultPopupAdListener adListener){
            adListener.onAdClicked(this);
        }
    };

    /// <summary>
    /// A video has been viewed completely and the user can be rewarded for it!
    /// </summary>
    public static Event VideoRewardGranted = new Event<DefaultPopupAdListener>(){
        public void action(DefaultPopupAdListener adListener){
            adListener.onVideoRewardGranted(this);
        }
    };

    /// <summary>
    /// SDK has not been initialized.
    /// </summary>
    public void SDKNotInitialized();


    private void CloseAd_Tap(object sender, TappedRoutedEventArgs e)
    {
        this.CleanOnClose();
    }

    private void ViewDeal_Tap(object sender, TappedRoutedEventArgs e)
    {
        if (!AdManager.getPopupViewModel(this.adKind).IsAPIClickURL)
        {
            // Always OpenBrowser = 1 for Native ads!! (no need to get server value since it's implemented as a native ad here).
            if (AdManager.getPopupViewModel(this.adKind).ClickOpensBrowser)
            {
                String campLink = AdManager.getPopupViewModel(this.adKind).BuildClickURL(AdManager.GetPopupViewModel(this.adKind).CampaignLinkURL, 0, 0);
                AdManager.openMarketPlace(campLink);
            }
            if (AdClicked != null)
            {
                AdClicked(new object(), new EventArgs());  // Notify Subscribers.
            }
            this.CleanOnClose(); // Closes ad.
        }
    }

    private void MainWebView_NavigationStarting(WebView sender, WebViewNavigationStartingEventArgs args)
    {
        String catchedURI = args.Uri.OriginalString;
        this.myProgressRing.setVisibility(View.VISIBLE);
        //Debug.WriteLine("CatchedURI = " + catchedURI);

        if (this.adKind == AdManager.AdKind.REWARDEDVIDEOAD || this.adKind == AdManager.AdKind.FULLSCREENPOPUPAD)
        {
            if (!checkHTMLTimer.IsEnabled && !catchedURI.equals("about:blank"))
            {
                checkHTMLTimer.Start(); // Check for video completed or popup ad closed content.
            }
        }

        // Cannot redirect, nor count a click for original web page call or when loaded is notified!
        if (catchedURI.startsWith("http://completed.addealsnetwork.com"))
        {
            if (VideoRewardGranted != null)
            {
                VideoRewardGranted(new object(), new EventArgs());  // Notify Subscribers.
            }
            args.Cancel = true;
        }
        else if (catchedURI.startsWith("http://loaded.addealsnetwork.com"))
        {
            args.Cancel = true;
        }
        else if (AdManager.getPopupViewModel(this.adKind).getWebViewAdSrc() != null &&
                !AdManager.getPopupViewModel(this.adKind).getWebViewAdSrc().getRawPath().trim().equals(StringhELPER.Empty) &&
                catchedURI.replace("http://", "").replace("https://", "").startsWith(AdManager.getPopupViewModel(this.adKind).WebViewAdSrc.OriginalString.Replace("http://", "").Replace("https://", "").Split('/')[0]))
        {
            // Do nothing since this is the original Web call, not a navigation or click
        }
        else
        {
            // This is a click URL. Ad need to be loaded.
            // We need to get it and pin the API or opens up the browser.
            if (AdManager.getPopupViewModel(this.adKind)._adLoaded &&
                    (catchedURI.toLowerCase().contains(".") && AdManager.getPopupViewModel(this.adKind).IsAPIClickURL))
            {
                if (catchedURI.startsWith("http://clicked.addealsnetwork.com"))
                {
                    args.Cancel = true;
                }

                if (!AdManager.getPopupViewModel(this.adKind)._adWasClicked)
                {
                    AdManager.getPopupViewModel(this.adKind).notifyAdClick(0, 0);
                    if (AdClicked != null)
                    {
                        AdClicked(new object(), new EventArgs());  // Notify Subscribers
                    }
                }

                if (AdManager.getPopupViewModel(this.adKind).isClickOpensBrowser())
                {
                    args.Cancel = true;
                    if (!catchedURI.startsWith("http://clicked.addealsnetwork.com"))
                    {
                        AdManager.openMarketPlace(catchedURI);
                        //this.CleanOnClose(); // Closes ad.
                    }
                    this.cleanOnClose(); // Closes ad.
                }
                else { // BROWSE WITHOUT LEAVING THE VIEW.
                }
            }
            else
            {
                if (catchedURI.startsWith("http://clicked.addealsnetwork.com"))
                {
                    args.Cancel = true; // Don't navigate to clicked.addealsnetwork.com! This is just to get the click location information!
                    String tmpClickURL = AdManager.getPopupViewModel(this.adKind).buildClickURL(AdManager.GetPopupViewModel(this.adKind).CampaignLinkURL, 0, 0);
                    if (AdManager.getPopupViewModel(this.adKind).isClickOpensBrowser())
                    {
                        AdManager.openMarketPlace(tmpClickURL);
                        this.CleanOnClose(); // Closes ad.
                    }
                    else
                    {
                        AdManager.getPopupViewModel(this.adKind).setWebViewAdSrc(new URI(tmpClickURL));
                    }

                    if (AdClicked != null)
                    {
                        AdClicked(new object(), new EventArgs());  // Notify Subscribers.
                    }
                }
            }
        }
    }

    private /*async*/ void MainWebView_NavigationCompleted(WebView sender, WebViewNavigationCompletedEventArgs args)
    {
        String html = "<html></html>";
        try
        {
            // TRY TO GET HTML ONCE LOADED AND CHECK FALLBACK TAGS.
            html = AsyncHelper.await(, mainWebView.InvokeScriptAsync("eval", new String[] { "document.documentElement.outerHTML;" }));
        }
        catch (Exception ex) { }

        if (html != null && !AdManager.getPopupViewModel(this.adKind).getHTMLFBTag().trim().equals(StringHelper.Empty) && html.contains(AdManager.getPopupViewModel(this.adKind).HTMLFBTag.Trim()))
        {
            AdManager.getPopupViewModel(this.adKind).updateModel(AdManager.getPopupViewModel(this.adKind).getLastCampaignIndex() + 1);
            if (AdManager.getPopupViewModel(this.adKind).getTmpWebSrcBeforeDisplay().toLowerCase().startsWith("http"))
            {
                AdManager.getPopupViewModel(this.adKind).setWebViewAdSrc(new URI(AdManager.getPopupViewModel(this.adKind).getTmpWebSrcBeforeDisplay()));
            }   // DISPLAY (Async - Web)!
            else
            {
                this.myProgressRing.Visibility = View.GONE;
            }
        }
        else
        {
            // COUNT 1 IMPRESSION - ONLY 1 TIME!
            if (!AdManager.getPopupViewModel(this.adKind)._adLoaded && AdManager.getPopupViewModel(this.adKind).getTmpImpressionPixelSrc().toLowerCase().startsWith("http"))
            {
                AdManager.getPopupViewModel(this.adKind).setImpressionPixelSrc(new URI(AdManager.getPopupViewModel(this.adKind).getTmpImpressionPixelSrc()));
            }   // CALL (Async - Web)!
            String catchedURI = args.Uri.OriginalString;
            this.myProgressRing.Visibility = View.GONE;
            AdManager.getPopupViewModel(this.adKind)._adLoaded = true;
        }
    }

    private void ImpressionPixelWebView_NavigationStarting(WebView sender, WebViewNavigationStartingEventArgs args)
    {
        String catchedURI = args.Uri.OriginalString;
    }

    private void MainWebView_FrameNavigationStarting(WebView sender, WebViewNavigationStartingEventArgs args)
    {
        String catchedURI = args.URI.OriginalString;
        if (AdManager.getPopupViewModel(this.adKind)._adLoaded && catchedURI.toLowerCase().contains("."))
        {
            if (!AdManager.getPopupViewModel(this.adKind)._adWasClicked)
            {
                AdManager.getPopupViewModel(this.adKind).notifyAdClick(0, 0);
                if (AdClicked != null)
                {
                    AdClicked(new object(), new EventArgs());  // Notify Subscribers
                }
            }
        }
    }

        //endregion
    }
}
