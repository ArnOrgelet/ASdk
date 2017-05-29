package sdk.addeals.ahead_solutions.adsdk.Views;

import android.app.Activity;
import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.android.gms.games.internal.constants.TimeSpan;

import org.joda.time.DateTime;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;

import sdk.addeals.ahead_solutions.adsdk.AdManager;
import sdk.addeals.ahead_solutions.adsdk.EventModels.DefaultAdListener;
import sdk.addeals.ahead_solutions.adsdk.EventModels.Event;
import sdk.addeals.ahead_solutions.adsdk.Libs.Helpers.AsyncHelper;
import sdk.addeals.ahead_solutions.adsdk.Libs.Helpers.AsyncTaskInterface;
import sdk.addeals.ahead_solutions.adsdk.Libs.Helpers.CustomAsyncTask;
import sdk.addeals.ahead_solutions.adsdk.Libs.Helpers.StringHelper;
import sdk.addeals.ahead_solutions.adsdk.MainActivityContract;
import sdk.addeals.ahead_solutions.adsdk.MainActivityPresenter;
import sdk.addeals.ahead_solutions.adsdk.R;
import sdk.addeals.ahead_solutions.adsdk.ViewModels.AdDealsBannerAdViewModel;

import static sdk.addeals.ahead_solutions.adsdk.AdManager.BannerAdSizes.BANNER_WINDOWS_PHONE_320x50;
import static sdk.addeals.ahead_solutions.adsdk.AdManager.BannerAdSizes.LEADERBOARD_WINDOWS_TABLET_PC_728x90;
import static sdk.addeals.ahead_solutions.adsdk.AdManager.BannerAdSizes.MEDIUM_RECTANGLE_WINDOWS_TABLET_PC_300x250;
import static sdk.addeals.ahead_solutions.adsdk.AdManager.BannerAdSizes.SQUARE_WINDOWS_PHONE_173x173;
import static sdk.addeals.ahead_solutions.adsdk.AdManager.BannerAdSizes.SQUARE_WINDOWS_TABLET_PC_250x250;
import static sdk.addeals.ahead_solutions.adsdk.AdManager.BannerAdSizes.WIDE_SKYSCRAPER_WINDOWS_TABLET_PC_160x600;

/**
 * Created by ArnOr on 09/05/2017.
 */

public class AdDealsBannerAd extends Activity/*EventHandingView<AdDealsBannerAdViewModel>*/ /*implements MainActivityContract.BindingView*/{
    public AdDealsBannerAd(Context context, AttributeSet attrs) {
        //super(context, attrs);
        initAdDealsBannerAd();
    }
    public AdDealsBannerAd(Context context, AttributeSet attrs, int defStyle) {
        //super(context, attrs, defStyle);
        initAdDealsBannerAd();
    }
    public AdDealsBannerAd(Context context) {
        //super(context);
        initAdDealsBannerAd();
    }

    DateTime lastRefreshTime = new DateTime(1900, 1, 1, 0, 0); // Memorizes the last time the ad was refreshed.
    AdDealsBannerAdViewModel _adDealsBannerViewModel = null;
    //Timer myTimer = new Timer();
    Handler myTimer = new Handler();
    Runnable myTimerAction;
    boolean changeTimerPeriod = false;
    long myTimerPeriod = AdManager.BANNER_REFRESH_RATE_DEFAULT;
    //DispatcherTimer checkDisplayedTimer = new DispatcherTimer();
    //Frame f = null;
    boolean _checkVisibilityInitialized = false;

    public void initAdDealsBannerAd()
    {
        this.initializeComponent();
        this.init();
        Window.Current.SizeChanged += Current_SizeChanged;
        //Window.Current.Activated += Current_Activated;
        //Window.Current.CoreWindow.PointerCaptureLost+=CoreWindow_PointerCaptureLost;
        //Window.Current.Content.PointerExited += Content_PointerExited;
        //Window.Current.Content.PointerEntered += Content_PointerEntered;
        //Windows.UI.Xaml.SuspendingEventHandler
        try
        {
            //f = Window.Current.Content as Frame;
            //f.Navigating += f_Navigating;
            //f.Navigated += f_Navigated;
            this.Unloaded += AdDealsBannerAd_Unloaded;
            this.Loaded += AdDealsBannerAd_Loaded;
            //this.checkDisplayedTimer.Interval = new TimeSpan(0, 0, 0, 1);
            //this.checkDisplayedTimer.Tick += checkDisplayedTimer_Tick;
        }
        catch (Exception) { }
    }

    WebView bannerWebView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewDataBinding binding = DataBindingUtil.setContentView(((Activity)this), android.R.layout.banner_ad);
        bannerWebView = (WebView) findViewById(android.R.layout.);
        //MainActivityPresenter mainActivityPresenter = new MainActivityPresenter(this);
        AdDealsBannerAdViewModel bannerAdViewModel = new AdDealsBannerAdViewModel();
        binding.setBannerAd(bannerAdViewModel);
        //binding.setPresenter(mainActivityPresenter);
    }

        /*private async void checkDisplayedTimer_Tick(object sender, object e)
        {
            try
            {
                string html = "<html></html>";
                try
                {
                    // TRY TO GET HTML ONCE LOADED AND CHECK FALLBACK TAGS.
                    html = await MainWebView.InvokeScriptAsync("eval", new string[] { "document.documentElement.outerHTML;" });
                }
                catch (Exception) { }

                if (html != null && !_adDealsBannerViewModel.HTMLFBTag.Trim().Equals(string.Empty) && html.Contains(_adDealsBannerViewModel.HTMLFBTag))
                {
                    if (_adDealsBannerViewModel.Campaigns.Offers.Count <= _adDealsBannerViewModel.lastCampaignIndex + 1)
                    {
                        if (AdNotAvailable != null)
                        {
                            AdNotAvailable(new object(), new EventArgs());
                        }
                    }
                    else
                    {
                        _adDealsBannerViewModel.UpdateModel(_adDealsBannerViewModel.lastCampaignIndex + 1);
                    }
                }
                else
                {
                    // COUNT 1 IMPRESSION - ONLY 1 TIME!
                    if (!_adDealsBannerViewModel._adLoaded && _adDealsBannerViewModel.ImpressionPixelSrcTmp.ToLower().StartsWith("http"))
                    {
                        _adDealsBannerViewModel.ImpressionPixelSrc = new Uri(_adDealsBannerViewModel.ImpressionPixelSrcTmp);
                    }   // CALL (Async - Web)!
                    _adDealsBannerViewModel._adLoaded = true;
                }

                if (this.checkDisplayedTimer.IsEnabled) this.checkDisplayedTimer.Stop();
            }
            catch (Exception) { }
        }*/


    private boolean _isAutoRefreshEnabled = true;
    public boolean getIsAutoRefreshEnabled()
    {
        return this._isAutoRefreshEnabled;
    }

    public void setIsAutoRefreshEnabled(boolean value)
    {
        if (this._isAutoRefreshEnabled != value)
        {
            this._isAutoRefreshEnabled = value;
            if (this._isAutoRefreshEnabled)
            {
                if (!myTimer._isEnabled) myTimer.Start();
            }
            else
            {
                if (myTimer.IsEnabled) myTimer.cancel();
            }
        }
    }

    /// <summary>
    /// Can only be called to refresh an ad manually if IsAutoRefreshEnabled = false
    /// If no ad is available OR if you did not wait for at least 25 seconds to refresh the ad, then
    /// a no ad available is also returned.
    /// </summary>
    public void refreshAd()
    {
        if (!this._isAutoRefreshEnabled && DateTime.now().plus(-lastRefreshTime) > new TimeSpan(0, 0, 25))
        {
            this.getAd();
        }
        else
        {
            if (AdCannotBeRefreshed != null)
            {
                AdCannotBeRefreshed(new object(), new EventArgs());
            }
        }
    }


    void AdDealsBannerAd_Loaded(object sender, RoutedEventArgs e)
    {
        if (!_checkVisibilityInitialized)
        {
            Window.Current.VisibilityChanged += Current_VisibilityChanged;
            _checkVisibilityInitialized = true;
        }
        if (!myTimer.IsEnabled && this._isAutoRefreshEnabled) myTimer.Start();
        else if (!this._isAutoRefreshEnabled)
        {
            this.getAd();
        }
    }

    private void AdDealsBannerAd_Unloaded(object sender, RoutedEventArgs e)
    {
        try
        {
            if (_checkVisibilityInitialized)
            {
                Window.Current.VisibilityChanged -= Current_VisibilityChanged;
                _checkVisibilityInitialized = false;
            }
        }
        catch (Exception ex) { }
        if (myTimer.IsEnabled) myTimer.Stop();
    }

        //region old
        /*private void f_Navigated(object sender, NavigationEventArgs e)
        {
            if (!myTimer.IsEnabled)
            {
                this.GetAd();
                this.myTimer.Start();
            }
            var test = 0;
        }*/

        /*private void f_Navigating(object sender, NavigatingCancelEventArgs e)
        {
            try
            {
                f.Navigating -= f_Navigating;
                //f.Navigated -= f_Navigated;
            }
            catch (Exception) { }
            if (myTimer.IsEnabled) myTimer.Stop();
        }*/


        /*void Content_PointerEntered(object sender, PointerRoutedEventArgs e)
        {
            if (!myTimer.IsEnabled)
            {
                this.GetAd();
                myTimer.Start();
            }
        }

        void Content_PointerExited(object sender, PointerRoutedEventArgs e)
        {
            if (myTimer.IsEnabled) { myTimer.Stop(); }
        }*/
        #endregion


    private void Current_SizeChanged(object sender, Windows.UI.Core.WindowSizeChangedEventArgs e)
    {
        if ((Window.Current.Bounds.Width < _adDealsBannerViewModel.getAdSpaceWidth()
                || Window.Current.Bounds.Height < _adDealsBannerViewModel.getAdSpaceHeight()))
        {
            if (myTimer.IsEnabled) myTimer.stop();
        }
        else if (!myTimer.IsEnabled && this._isAutoRefreshEnabled)
        {
            this.getAd();
            myTimer.start();
        }
    }

    private void Current_VisibilityChanged(object sender, Windows.UI.Core.VisibilityChangedEventArgs e)
    {
        try
        {
            //int height = this.getWindow().getDecorView().getWindowVisibility().getHeight();
            if (this.getWindow().getDecorView().getWindowVisibility() != View.INVISIBLE)//Window.Current.Visible)
            {
                if (!myTimer._isEnabled && this._isAutoRefreshEnabled)
                {
                    this.getAd();
                    myTimer.start();
                }
            }
            else
            {
                if (myTimer.IsEnabled) myTimer.stop();
            }
        }
        catch (Exception ex) { }
    }

    private void setTimerPeriod(long tickPeriod){
        if(myTimerPeriod != tickPeriod){
            myTimerPeriod = tickPeriod;
            changeTimerPeriod = true;
        }
    }
    // We can preload things there...
    /*internal*/ void init()
    {
        _adDealsBannerViewModel = new AdDealsBannerAdViewModel();
        DataContext = _adDealsBannerViewModel;
        //this.GetAd();

        // get a handler (call from main thread)
        final Handler handler = new Handler();

        // this will run when timer elapses
        //TimerTask myTimerTask = new TimerTask()
        Runnable myTimerTask = new Runnable(){
            @Override
            public void run() {
                // post a runnable to the handler
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // ...
                    }
                });
            }
        };

// new timer
        //myTimer = new Timer(true);
        //myTimer.scheduleAtFixedRate(refreshTimer_Tick(), AdManager.BANNER_REFRESH_RATE_DEFAULT_AT_LAUNCH, myTimerPeriod);
        myTimer.postDelayed(refreshTimer_Tick(), AdManager.BANNER_REFRESH_RATE_DEFAULT_AT_LAUNCH);
    }

    private TimerTask delayedRefreshTimer_Tick(){
        myTimer.p
    }
    private TimerTask refreshTimer_Tick()
    {
        return new TimerTask() {
            @Override
            public void run() {
                try
                {
                    if (_isAutoRefreshEnabled)
                    {
                        getAd();
                    }
                    if(!_isAutoRefreshEnabled || changeTimerPeriod)
                    {
                        myTimer.cancel(); // cancel time
                        startTimer();   // start the time again with a new delay time
                    }
                }
                catch (Exception ex) { }
            }
        };
    }

    private void getAd()
    {
        // Try to get new ad every 60sec (default timeout), once the SDK has been initialized. Otherwise, don't get any ad.
        if (AdManager.SDKinitialized && _adDealsBannerViewModel.CounterWithoutAd < 5)
        {
            this.setTimerPeriod(_adDealsBannerViewModel.ServerRefreshRate);
            int result = AdManager.ERROR_INCOMPATIBLE_AD;

            CustomAsyncTask<Void, Void, Integer> asyncTask = new CustomAsyncTask<Void, Void, Integer>(
                    new AsyncTaskInterface<Void, Void, Integer>() {

                        public void onPreExecute() {
                        }

                        @Override
                        public Integer doInBackground(Void... arg0) {
                            return _adDealsBannerViewModel.getNewAd(true);
                        }

                        @Override
                        public void onProgressUpdate(Void... values) {

                        }
                    });
            switch (_adType)
            {
                    /*case AdManager.BannerAdSizes.BANNER_WINDOWS_PHONE_AUTOSCALE_PORTRAIT:
                    {
                        if ((int)(Window.Current.Bounds.Width) < (int)(Window.Current.Bounds.Height)
                            && AdManager.DeviceKind.Equals(AdManager.DeviceType.PHONE))
                        {
                            result = await _adDealsBannerViewModel.GetNewAd(true); // Find the closest ad available!
                        }
                        break;
                    }*/

                case BANNER_WINDOWS_PHONE_320x50:
                {
                    if (AdManager.getDeviceKind().equals(AdManager.DeviceType.PHONE))
                    {
                        try{
                            result = asyncTask.execute().get();
                        }
                        catch(Exception ex){}

                        lastRefreshTime = DateTime.now();
                    }
                    break;
                }

                case LEADERBOARD_WINDOWS_TABLET_PC_728x90:
                {
                    if (AdManager.getDeviceKind().equals(AdManager.DeviceType.TABLET_PC))
                    {
                        try{
                            result = asyncTask.execute().get();
                        }
                        catch(ExecutionException ex){}
                        catch(InterruptedException ex){}
                        lastRefreshTime = DateTime.now();
                    }
                    break;
                }

                case MEDIUM_RECTANGLE_WINDOWS_TABLET_PC_300x250:
                {
                    if (AdManager.getDeviceKind().equals(AdManager.DeviceType.TABLET_PC))
                    {
                        try{
                            result = asyncTask.execute().get();
                        }
                        catch(ExecutionException ex){}
                        catch(InterruptedException ex){}
                        lastRefreshTime = DateTime.now();
                    }
                    break;
                }

                case SQUARE_WINDOWS_PHONE_173x173:
                {
                    if (AdManager.getDeviceKind().equals(AdManager.DeviceType.PHONE))
                    {
                        try{
                            result = asyncTask.execute().get();
                        }
                        catch(ExecutionException ex){}
                        catch(InterruptedException ex){}
                        lastRefreshTime = DateTime.now();
                    }
                    break;
                }

                case SQUARE_WINDOWS_TABLET_PC_250x250:
                {
                    if (AdManager.getDeviceKind().equals(AdManager.DeviceType.TABLET_PC))
                    {
                        try{
                            result = asyncTask.execute().get();
                        }
                        catch(ExecutionException ex){}
                        catch(InterruptedException ex){}
                        lastRefreshTime = DateTime.now();
                    }
                    break;
                }

                case WIDE_SKYSCRAPER_WINDOWS_TABLET_PC_160x600:
                {
                    if (AdManager.getDeviceKind().equals(AdManager.DeviceType.TABLET_PC))
                    {
                        try{
                            result = asyncTask.execute().get();
                        }
                        catch(ExecutionException ex){}
                        catch(InterruptedException ex){}
                        lastRefreshTime = DateTime.now();
                    }
                    break;
                }

                default: break;
            }

            switch (result)
            {
                case AdManager.AD_AVAILABLE:
                {
                    if (_adDisplayed != null)
                    {
                        AdDisplayed.notifyAll();//(new object(), new EventArgs());
                    }
                    break;
                }
                case AdManager.NO_AD_AVAILABLE:
                {
                    if (AdNotAvailable != null)
                    {
                        AdNotAvailable.notifyAll();//(new object(), new EventArgs());
                    }
                    break;
                }
                case AdManager.ERROR_INCOMPATIBLE_AD:
                {
                    if (AdNotCompatible != null)
                    {
                        AdNotCompatible.notifyAll();//(new object(), new EventArgs());
                    }
                    break;
                }
                // For filler ad campaign, we don't know until it's displayed so we don't trigger any event.
                case AdManager.AD_AVAILABILITY_UNKNOWN:
                {
                    break;
                }
            }
        }
        else if (!AdManager.SDKinitialized)
        {
            if (SDKNotInitializedYet != null)
            {
                SDKNotInitializedYet(new object(), new EventArgs());
            }
        }
    }

    private void updateSize() {
        _adDealsBannerViewModel.refreshScreenSize(getAdType());
        ViewGroup.LayoutParams bannerWebViewLayout = bannerWebView.getLayoutParams();
        bannerWebViewLayout.width = (int) _adDealsBannerViewModel.getAdSpaceWidth();     // Update control Width (force it!)
        bannerWebViewLayout.height = (int) _adDealsBannerViewModel.getAdSpaceHeight();   // Update control Height (force it!)

        bannerWebView.setLayoutParams(bannerWebViewLayout);
    }

    private Future<Void> mainWebView_NavigationStarting(WebView sender, WebViewNavigationStartingEventArgs args)
    {
        // http://web.addealsnetwork.com/ads?cid=2033&aid=1932&akey=LIE2H2N2CQSB&os=&lang=fr&country=FR&sdkv=3.0&advuid=538035f6476666d48294808205d3a48c&usragent=Mozilla/5.0%20(Windows%20NT%206.3;%20WOW64;%20Trident/7.0;%20.NET4.0C;%20.NET4.0E;%20.NET%20CLR%202.0.50727;%20.NET%20CLR%203.0.30729;%20.NET%20CLR%203.5.30729;%20InfoPath.3;%20WebView/2.0;%20rv:11.0)%20like%20Gecko&moment=%moment%&usrid=-1&adsize=1024x768&rand=1932130888701909321284

        String catchedURI = args.Uri.OriginalString;

        // Cannot redirect, nor count a click for original web page call or when loaded is notified!
        if (catchedURI.startsWith("http://loaded.addealsnetwork.com"))
        {
            args.Cancel = true;
            // Refresh with pixel tracking URL.
            //_adDealsBannerViewModel.ImpressionPixelSrc = new Uri(_adDealsBannerViewModel.ImpressionPixelSrcTmp);
            //AdManager.AdDealsPopupAdViewModel._adWasClicked = false;
        }
        else if (_adDealsBannerViewModel.getWebViewAdSrc() != null && !_adDealsBannerViewModel.getWebViewAdSrc().getRawPath().trim().equals(StringHelper.Empty) &&
                catchedURI.replace("http://", "").replace("https://", "").startsWith(_adDealsBannerViewModel.getWebViewAdSrc().getRawPath().replace("http://", "").replace("https://", "").split('/')[0]))
        { // Do nothing since this is the original Web call, not a navigation or click
        }
        else
        {
            // This is a click URL.
            // We need to get it and ping the API or opens up the browser.
            if (catchedURI.toLowerCase().contains(".") && _adDealsBannerViewModel.IsAPIClickURL)
            {
                if (catchedURI.startsWith("http://clicked.addealsnetwork.com"))
                {
                    args.Cancel = true;
                    if (!_adDealsBannerViewModel._adWasClicked)
                    {
                        _adDealsBannerViewModel.notifyAdClick((int)_adDealsBannerViewModel._adWidth, (int)_adDealsBannerViewModel.AdHeight);
                        if (AdClicked != null)
                        {
                            AdClicked(new object(), new EventArgs());  // Notify Subscribers
                        }
                    }
                }
                else if (_adDealsBannerViewModel.isClickOpensBrowser())
                {
                    args.Cancel = true;
                    AdManager.openMarketPlace(catchedURI);
                }
                else
                {
                    // BROWSE WITHOUT LEAVING THE VIEW.
                }
            }
            else
            {
                if (catchedURI.startsWith("http://clicked.addealsnetwork.com"))
                {
                    args.Cancel = true; // Don't navigate to clicked.addealsnetwork.com! This is just to get the click location information!
                    String tmpClickURL = _adDealsBannerViewModel.buildClickURL(_adDealsBannerViewModel.getCampaignLinkURL(),
                            (int)_adDealsBannerViewModel.getAdWidth(), (int)_adDealsBannerViewModel.getAdHeight());
                    if (_adDealsBannerViewModel.isClickOpensBrowser())
                    {
                        AdManager.openMarketPlace(tmpClickURL);
                    }
                    else
                    {
                        try{
                            _adDealsBannerViewModel.setWebViewAdSrc(new URI(tmpClickURL));
                        }
                        catch(URISyntaxException ex) {}
                    }

                    if (AdClicked != null)
                    {
                        AdClicked(new object(), new EventArgs());  // Notify Subscribers.
                    }
                }
            }
        }
    }

    private Future<Void> MainWebView_NavigationCompleted(WebView sender, WebViewNavigationCompletedEventArgs args)
    {
        //if (this.checkDisplayedTimer.IsEnabled) this.checkDisplayedTimer.Stop();
        //this.checkDisplayedTimer.Start();

        try
        {
            String html = "<html></html>";
            try
            {
                // TRY TO GET HTML ONCE LOADED AND CHECK FALLBACK TAGS.
                html = mainWebView.InvokeScriptAsync("eval", new String[] { "document.documentElement.outerHTML;" }).get();
            }
            catch (Exception ex) { }

            if (html != null && !_adDealsBannerViewModel.getHTMLFBTag().trim().equals(StringHelper.Empty) && html.contains(_adDealsBannerViewModel.getHTMLFBTag().trim()))
            {
                if (_adDealsBannerViewModel.getCampaigns().Offers.size() <= _adDealsBannerViewModel.lastCampaignIndex + 1)
                {
                    if (AdNotAvailable != null)
                    {
                        AdNotAvailable(new object(), new EventArgs());
                    }
                    _adDealsBannerViewModel._adWasClicked = true; // Prevents click counts and click events reports when no ad is available.
                }
                else
                {
                    _adDealsBannerViewModel.updateModel(_adDealsBannerViewModel.getLastCampaignIndex() + 1);
                }
            }
            else
            {
                // COUNT 1 IMPRESSION - ONLY 1 TIME!
                if (!_adDealsBannerViewModel._adLoaded && _adDealsBannerViewModel.ImpressionPixelSrcTmp.toLowerCase().StartsWith("http"))
                {
                    _adDealsBannerViewModel.setImpressionPixelSrc(new URI(_adDealsBannerViewModel.ImpressionPixelSrcTmp));
                    if (AdDisplayed != null)
                    {
                        AdDisplayed(new object(), new EventArgs());
                    }
                }   // CALL (Async - Web)!

                _adDealsBannerViewModel._adLoaded = true;
            }

            //if (this.checkDisplayedTimer.IsEnabled) this.checkDisplayedTimer.Stop();
        }
        catch (Exception ex) { }
    }
    DefaultAdListener adListener = new DefaultAdListener();
    /// <summary>
    /// Event raised when an Ad is available and displayed.
    /// </summary>
    public static Event AdDisplayed = new Event<DefaultAdListener>(){
        public void action(DefaultAdListener adListener){
            adListener.onSDKNotInitializedYet(this);
        }
    };

    /// <summary>
    /// Event raised when no AdDeals Ad is available.
    /// </summary>
    public static Event AdNotAvailable = new Event<DefaultAdListener>(){
        public void action(DefaultAdListener adListener){
            adListener.onAdNotAvailable(this);
        }
    };

    /// <summary>
    /// Event raised when ad cannot be refreshed manually (less than 30 seconds between 2 ad requests).
    /// </summary>
    public static Event AdCannotBeRefreshed = new Event<DefaultAdListener>(){
        public void action(DefaultAdListener adListener){
            adListener.onAdCannotBeRefreshed(this);
        }
    };

    /// <summary>
    /// Event raised when an ad is requested on an incompatible platform (Windows Phone instead of Windows PC/Tablet ads...).
    /// </summary>
    public static Event AdNotCompatible = new Event<DefaultAdListener>(){
        public void action(DefaultAdListener adListener){
            adListener.onAdNotCompatible(this);
        }
    };

    /// <summary>
    /// Ad has been clicked by end user.
    /// </summary>
    public static Event AdClicked = new Event<DefaultAdListener>(){
        public void action(DefaultAdListener adListener){
            adListener.onAdClicked(this);
        }
    };

    /// <summary>
    /// SDK has not been initialized yet.
    /// </summary>
    public static Event SDKNotInitializedYet = new Event<DefaultAdListener>(){
        public void action(DefaultAdListener adListener){
            adListener.onSDKNotInitializedYet(this);
        }
    };

    private AdManager.BannerAdSizes _adType;
    public AdManager.BannerAdSizes getAdType()
    {
        return this._adType;
    }

    public void setAdType(AdManager.BannerAdSizes value)
    {
        this._adType = value;
        this.updateSize();
    }

    @Override
    public void showData(BaseObservable observableData) {

    }

        /*private void ImpressionPixelWebView_NavigationStarting(WebView sender, WebViewNavigationStartingEventArgs args)
        {
            string catchedURI = args.Uri.OriginalString;
        }

        private void MainWebView_ScriptNotify(object sender, NotifyEventArgs e)
        {
            string test = e.Value;
        }

        private void MainWebView_FrameNavigationStarting(WebView sender, WebViewNavigationStartingEventArgs args)
        {
            string test = args.Uri.OriginalString;
        }*/

    /// <summary>
    /// For API clicks - not redirect.
    /// </summary>
    /// <param name="sender"></param>
    /// <param name="e"></param>
        /*private void MainWebView_Tapped(object sender, TappedRoutedEventArgs e)
        {
            if (!AdManager.AdDealsPopupAdViewModel._adWasClicked && _adDealsBannerViewModel.IsAPIClickURL)
            {
                _adDealsBannerViewModel.NotifyAdClick();
                if (AdClicked != null)
                {
                    AdClicked(new object(), new EventArgs());  // Notify Subscribers
                }
            }
        }

        private void MainWebView_PointerReleased(object sender, PointerRoutedEventArgs e)
        {
            if (!AdManager.AdDealsPopupAdViewModel._adWasClicked && _adDealsBannerViewModel.IsAPIClickURL)
            {
                _adDealsBannerViewModel.NotifyAdClick();
                if (AdClicked != null)
                {
                    AdClicked(new object(), new EventArgs());  // Notify Subscribers
                }
            }
        }*/
}
