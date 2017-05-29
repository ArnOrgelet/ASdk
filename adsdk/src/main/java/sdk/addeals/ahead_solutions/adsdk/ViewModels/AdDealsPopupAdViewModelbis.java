package sdk.addeals.ahead_solutions.adsdk.ViewModels;

import android.app.Activity;
import android.databinding.Bindable;
import android.opengl.Visibility;
import android.view.View;
import android.view.Window;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Future;

import sdk.addeals.ahead_solutions.adsdk.AdManager;
import sdk.addeals.ahead_solutions.adsdk.Libs.Helpers.ResolutionHelper;
import sdk.addeals.ahead_solutions.adsdk.Libs.Helpers.StringHelper;
import sdk.addeals.ahead_solutions.adsdk.Libs.Helpers.TimeHelper;
import sdk.addeals.ahead_solutions.adsdk.Models.Campaign;
import sdk.addeals.ahead_solutions.adsdk.Models.CampaignsV3;
import sdk.addeals.ahead_solutions.adsdk.Views.AdDealsPopupAd;

import static android.os.Build.VERSION_CODES.M;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by ArnOr on 07/05/2017.
 */

public class AdDealsPopupAdViewModel extends ViewModelBase {
    // Interstitial can be launched only once every 3 seconds by default
    // (to avoid multiple calls & protect potential technical issues (back button...)).
    /*internal*/ final int DEFAULT_DELAY = 10;
    /*internal*/ DateTime lastInterstitialLaunchDate;
    /*internal*/ AdManager.AdKind lastAdKind = AdManager.AdKind.FULLSCREENPOPUPAD;
    private int _adHeight = 0;
    private int _adWidth = 0;
    private int _frameHeight = 0;
    private int _frameWidth = 0;
    /// <summary>
    /// Last called adTypeID campaign
    /// (useful when we want to cache rewarded videos instead of already cached interstitial ads).
    /// </summary>
    private int _adTypeID = 0;
    private String _frameBorder = "0,0,0,0";
    private double _maxPercentAdSize = 1; // Default is 100% - for tablet/PCs.
    private AdManager.CloseButtonPosition _closeButtonPosition = AdManager.CloseButtonPosition.ONAD;
    private final String DEFAULT_OVERLAY_IMAGE_URI = "";
    //private bool isWebAdClicked = false;

    //region settings

    /// <summary>
    /// Allows to set a minimal delay between 2 interstitials.
    /// If this delay is not reached, then an Event "Delay between interstitial not reached is sent" & "No Ad is also called".
    /// Min by default is 3 seconds.
    /// </summary>
    /// <param name="delay">Minimal delay between two interstitial displays in seconds (default = 10 seconds)</param>
    /// <returns>Current delay between interstitial displays</returns>
    /*internal*/ int setMinDelayBetweenDisplays(int delay)
    {
        if (delay < DEFAULT_DELAY) this._delay = DEFAULT_DELAY;
        else this._delay = delay;

        return this._delay;
    }

    private int _delay = DEFAULT_DELAY;
    public int getDelay()
    {
        return this._delay;
    }

    private void setDelay(int value)
    {
        this._delay = value;
    }

    /// <summary>
    /// Tells whether a new ad can be displayed or not.
    /// If prefetched, delay = 0.
    /// </summary>
    /// <returns>true when delays is passed and a new query can be made</returns>
    public /*internal*/ boolean isDelayPassed()
    {
        if (this._isPrefetched || lastInterstitialLaunchDate == null ||
                DateTime.now().minusMillis(Math.round((float)
                        lastInterstitialLaunchDate.plus(
                                new Period(0, 0, this._delay, 0).getSeconds()
                        ).getMillis())*1000
                ).getMillis() > 0)
        {
            return true;
        }
        else return false;
    }

    //endregion
    AdDealsPopupAd adDealsPopupAdView;
    /*internal*/ AdDealsPopupAdViewModel(AdDealsPopupAd adDealsPopupAdView)
    {
            this.adDealsPopupAdView = adDealsPopupAdView;
    }

    /*internal async*/ int getNewAd(AdManager.AdKind adKind)
    {
        this.lastInterstitialLaunchDate = DateTime.now();//TimeHelper.getUTCNow();
        String requestedAdTypes = AdManager.NO_SUPPORTED_ADS; // All by default.
        this.lastAdKind = adKind;
        this.refreshScreenSize(); // INIT APPHEIGHT WIDTH.
        int campaignType = CAMPAIGN_TYPE_INTERSTITIAL;

        try
        {
            switch (adKind)
            {
                case FULLSCREENPOPUPAD:
                {
                    requestedAdTypes = AdManager.FULL_SCREEN_SUPPORTED_ADS;
                    break;
                }

                case REWARDEDVIDEOAD:
                {
                    campaignType = AdManager.CAMPAIGN_TYPE_VIDEO_REWARDED;
                    requestedAdTypes = AdManager.REWARDED_VIDEO_SUPPORTED_ADS;

                    // If in portrait mode, get landscape (reverse):

                    Activity activity = ((Activity) adDealsPopupAdView.getContext());/*.getResources().getDisplayMetrics();*/)
                    int boundsHeight = (int)(activity.getWindow().getDecorView().getHeight());
                    int boundsWidth = (int)(activity.getWindow().getDecorView().getWidth());
                    if (boundsWidth < boundsHeight)
                    {
                        this.setAppHeight(boundsHeight);
                        this.setAppWidth(boundsWidth);
                    }

                    // We can get/display new video ads immediately.
                    this.lastInterstitialLaunchDate = new DateTime(1900, 1, 1, 0, 0, 0);
                    break;
                }
            }

            CampaignsV3 campaigns = this.getCampaignAd(requestedAdTypes, (int)this._appWidth, (int)this._appHeight, campaignType, false).get();

            if ((campaigns != null && campaigns.Offers.size() == 0) || campaigns == null)
            {
                _lastAdCalledStatus = NO_AD_AVAILABLE;
                return _lastAdCalledStatus;
            }

            this._campaigns = campaigns;
            //this.UpdateModel(0);
            _lastAdCalledStatus = AD_AVAILABLE;
            //this.lastAdKind = adKind;
            return _lastAdCalledStatus;
        }
        catch (Exception ex)
        {
            _lastAdCalledStatus = NO_AD_AVAILABLE;
            return NO_AD_AVAILABLE;
        }
    }

    /*internal*/ void setPercentScreenAd(double maxPercentAdSize)
    {
        this._maxPercentAdSize = maxPercentAdSize;
    }

    /*internal*/ void setCloseButtonPosition(AdManager.CloseButtonPosition position)
    {
        this.setCloseButtonPosition(position);

        switch (position)
        {
            case ONAD: {
                this._closeButtonInsideVisibility = View.VISIBLE;
                this._closeButtonOutsideVisibility = View.GONE;
                break;
            }

            case TOPRIGHT: {
                this._closeButtonInsideVisibility = View.GONE;
                this._closeButtonOutsideVisibility = View.VISIBLE;
                break;
            }
        }
    }

    /*internal*/ void refreshScreenSize()
    {
        if (!lastAdKind.equals(AdManager.AdKind.REWARDEDVIDEOAD))
        {
            Activity activity = ((Activity) adDealsPopupAdView.getContext());/*.getResources().getDisplayMetrics();*/)
            int boundsHeight = (int)(activity.getWindow().getDecorView().getHeight());
            int boundsWidth = (int)(activity.getWindow().getDecorView().getWidth());
            this._appHeight = boundsHeight;
            this._appWidth = boundsWidth;
            this._fullScreenHeight = (int)(this._maxPercentAdSize * this._appHeight);
            this._fullScreenWidth = (int)(this._maxPercentAdSize * this._appWidth);
            int CLOSE_BUTTON_HEIGHT = 35; // Square close button in Pixel.

            try
            {
                if (_frameHeight > 0 && _frameWidth > 0
                        && (_frameHeight < this._fullScreenHeight
                        || _frameWidth < this._fullScreenWidth))
                {
                    if (_frameHeight < this._fullScreenHeight) this._fullScreenHeight = _frameHeight;
                    if (_frameWidth < this._fullScreenWidth) this._fullScreenWidth = _frameWidth;
                }
                // Else if no frame!
                else if (_frameHeight == 0 && _frameWidth == 0
                        && _adHeight > 0 && _adWidth > 0
                        && (_adHeight < this._fullScreenHeight
                        || _adWidth < this._fullScreenWidth))
                {
                    if (_adHeight < this._fullScreenHeight) this._fullScreenHeight = _adHeight;
                    if (_adWidth < this._fullScreenWidth) this._fullScreenWidth = _adWidth;
                }


                int tmpHeight = 0;
                int tmpWidth = 0;
                if (_frameHeight == 0 && _frameWidth == 0)
                {
                    tmpHeight = _adHeight;
                    tmpWidth = _adWidth;
                }
                else
                {
                    tmpHeight = _frameHeight;
                    tmpWidth = _frameWidth;
                }


                if (tmpHeight > 0 && tmpWidth > 0)
                {
                    if (this._appHeight < this._appWidth)
                    {
                        this._frameFullScreenHeight = this._fullScreenHeight;
                        this._frameFullScreenWidth = (int)(((double)this._frameFullScreenHeight / (double)tmpHeight) * (double)tmpWidth);

                        if (this._frameFullScreenWidth < this._fullScreenWidth)
                        {
                            this._fullScreenWidth = this._frameFullScreenWidth;
                        }
                        else // Fix for Ratio potential issues.
                        {
                            this._frameFullScreenHeight = (int)(((double)this._fullScreenWidth / (double)tmpWidth) * (double)tmpHeight);
                            this._fullScreenHeight = this._frameFullScreenHeight;
                            this._frameFullScreenWidth = this._fullScreenWidth;
                        }
                    }
                    else
                    {
                        this._frameFullScreenWidth = this._fullScreenWidth;
                        this._frameFullScreenHeight = (int)(((double)this._frameFullScreenWidth / (double)tmpWidth) * (double)tmpHeight);
                        if (this._frameFullScreenHeight < this._fullScreenHeight)
                        {
                            this._fullScreenHeight = this._frameFullScreenHeight;
                        }
                        else // Fix for Ratio potential issues.
                        {
                            this._frameFullScreenWidth = (int)(((double)this._fullScreenHeight / (double)tmpHeight) * (double)tmpWidth);
                            this._fullScreenWidth = this._frameFullScreenWidth;
                            this._frameFullScreenHeight = this._fullScreenHeight;
                        }
                    }
                    this._imgFullScreenHeight = (int)((double)this._frameFullScreenHeight * ((double)_adHeight / (double)tmpHeight));
                    this._imgFullScreenWidth = (int)((double)this._frameFullScreenWidth * ((double)_adWidth / (double)tmpWidth));

                    double tmpMarginWidth = this._imgFullScreenWidth;     // When there is no frame
                    double tmpMarginHeight = 5;                          // When there is no frame
                    if (_frameHeight > 0 && _frameWidth > 0)
                    {
                        this._imgFullScreenMargin = (int)(Integer.parseInt(_frameBorder.split(",")[0]) * ((double)this._frameFullScreenWidth / (double)tmpWidth))
                                + "," + (int)(Integer.parseInt(_frameBorder.split(",")[1]) * ((double)this._frameFullScreenHeight / (double)tmpHeight)) + ",0,0";

                        tmpMarginHeight = (int)((double)(-Integer.parseInt(_frameBorder.split(",")[1])) * ((double)this._frameFullScreenHeight / (double)tmpHeight));
                        if (Integer.parseInt(_frameBorder.split(",")[1]) < CLOSE_BUTTON_HEIGHT)
                        {
                            tmpMarginHeight = -(int)((CLOSE_BUTTON_HEIGHT - (Double.parseDouble(_frameBorder.split(",")[0])) * ((double)this._frameFullScreenHeight / (double)tmpHeight)) / 3.0);
                        }
                        else tmpMarginHeight = 5;
                        tmpMarginWidth = (int)(this._frameFullScreenWidth - (Double.parseDouble(_frameBorder.split(",")[0])) * ((double)this._frameFullScreenWidth / (double)tmpWidth) - ((CLOSE_BUTTON_HEIGHT - (Double.parseDouble(_frameBorder.split(",")[0])) * ((double)this._frameFullScreenWidth / (double)tmpWidth)) / 1.5M));
                    }
                    else // No frame.
                    {
                        tmpMarginWidth = (int)(tmpMarginWidth - CLOSE_BUTTON_HEIGHT - 5);
                    }

                    this._closeButtonMargin = ((int)tmpMarginWidth - 0) + "," + ((int)tmpMarginHeight + 0) + ",5,5";    // For inside button only.
                }
            }
            catch (Exception ex)
            {
                this._frameFullScreenWidth = 0;
                this._frameFullScreenHeight = 0;
                this._imgFullScreenWidth = _adWidth;
                this._imgFullScreenHeight = _adHeight;
            }

            this._progressRingMargin = (int)(((double)_appWidth - 90) / 2) + "," + (int)(((double)_appHeight - 90) / 2) + ","
                    + (int)(((double)_appWidth - 90) / 2) + "," + (int)(((double)_appHeight - 90) / 2);
        }
        else
        {
            this._frameHeight = 0;
            this._frameWidth = 0;
            this._frameBorder = "0,0,0,0";

            //ApplicationView.GetForCurrentView().SetDesiredBoundsMode(ApplicationViewBoundsMode.UseVisible);

            //var scaleFactor = DisplayInformation.GetForCurrentView().RawDpiX;
            this.setAppHeight((int)(ResolutionHelper.));
            this.setAppWidth((int)(Window.Current.Bounds.Width));
            this.setFullScreenHeight((int)(this._appHeight));
            this.setFullScreenWidth((int)(this._appWidth));
            int CLOSE_BUTTON_HEIGHT = 35; // Square close button in Pixel.

            this.setImgFullScreenHeight(this._fullScreenHeight);
            this.setImgFullScreenWidth(this._fullScreenWidth);
            this.setFrameFullScreenWidth(0);
            this.setFrameFullScreenHeight(0);
            this.setImgFullScreenMargin("0,0,0,0");
            int tmpMarginWidth = (int)(_appWidth - CLOSE_BUTTON_HEIGHT - 30);
            this._closeButtonMargin = tmpMarginWidth + ",25,10,10";

            this._progressRingMargin = (int)(((double)_appWidth - 90) / 2) + "," + (int)(((double)_appHeight - 90) / 2) + ","
                    + (int)(((double)_appWidth - 90) / 2) + "," + (int)(((double)_appHeight - 90) / 2);
        }
    }

    private void InitializeDataView()
    {
        // Initialize all panels:
        this._borderVisibility = View.GONE;
        this._displayFullScreenInterstitial = View.GONE;
        this._tmpWebSrcBeforeDisplay = StringHelper.Empty;
        this._tmpImpressionPixelSrc = StringHelper.Empty;
        if (!DEFAULT_CLOSE_BUTTON_URI.equals(StringHelper.Empty)) this._closingButton = DEFAULT_CLOSE_BUTTON_URI;
        if (!DEFAULT_OVERLAY_IMAGE_URI.equals(StringHelper.Empty)) this._overlayImage = DEFAULT_OVERLAY_IMAGE_URI;

        // Initialize Data:
        this._campaignLinkURL = StringHelper.Empty;
        this._currentCampaignID = 0;
        this._frameHeight = 0;
        this._frameWidth = 0;
        this.IsAPIClickURL = false;
        this.ClickOpensBrowser = true;
        this._adWasClicked = false; // An ad click can only be counted & sent once!
        this._adLoaded = false;
    }

    /// <summary>
    /// Helper mathod to load & display frame.
    /// </summary>
    private void loadFrame(Campaign myCampaign)
    {
        if (myCampaign.CustomCloseButtonURL != StringHelper.Empty) this._closingButton = myCampaign.CustomCloseButtonURL;

        if (!lastAdKind.equals(AdManager.AdKind.REWARDEDVIDEOAD))
        {
            if (myCampaign.CustomFrameURL != StringHelper.Empty)
            {
                this._overlayImage = myCampaign.CustomFrameURL;
                this._frameWidth = myCampaign.CustomFrameWidth;
                this._frameHeight = myCampaign.CustomFrameHeight;
                this._frameBorder = myCampaign.CustomFrameMargins;
                this._frameFullScreenVisibility = View.VISIBLE;//Visibility.Visible;
            }
        }
    }

    /// <summary>
    /// Update Campaign
    /// </summary>
    /// <param name="campIndex"></param>
    /*internal*/ void updateModel(int campIndex)
    {
        this.InitializeDataView();

        // Ad space (depends on what is returned by server - and what asked by app)
        //switch (campaigns.Count) {

        // One paid campaign is displayed.
        //case 1:
        //{
        if (this._campaigns.Offers.size() > campIndex)
        {
            this.lastCampaignIndex = campIndex;
            Campaign myCampaign = this._campaigns.Offers.get(campIndex);
            this._currentCampaignID = (int)myCampaign.OfferID;
            this._adWidth = myCampaign.AdSpaceWidth;
            this._adHeight = myCampaign.AdSpaceHeight;
            this._adTypeID = myCampaign.AdTypeID;
            //this._adHeight = 768;
            //this._adWidth = 1024;

            if (myCampaign.OpenBrowserOnClick == 1) this.setClickOpensBrowser(true);
            else this.setClickOpensBrowser(false);
            if (myCampaign.IsAPIClickURL == 1) this.setIsAPIClickURL(true);
            else this.setIsAPIClickURL(false);
            this.setCampaignLinkURL(myCampaign.AdClickLink);
            this._htmlFBTag = myCampaign.HTMLFBTag.trim();

            boolean displayAd = false;
                //region old
                /*this.ImpressionPixelSrc =
                        @"<!DOCTYPE HTML PUBLIC ""-//W3C//DTD HTML 4.01 Transitional//EN"">
                        <html>
                        <head>
                        </head>
                        <body width=""100"">BLABLAL ABALAL BALALALAL BLALAL BLALALAL</body>
                        </html>";*/
                //endregion

            switch (myCampaign.AdTypeID)
            {

                case AD_TYPE_FULL_INTERSTITIAL_HTML:
                {
                    //this.FullScreenAdImg = "http://www.addealsnetwork.com/Images/Campaigns/289/480x800_en_portrait.jpg";
                    //this.FullScreenAdImg = "http://www.addealsnetwork.com/Images/Campaigns/289/800x480_en_landscape.jpg";
                    this._displayFullScreenInterstitial = View.VISIBLE;
                    this._nativeImageVisibility = View.GONE;
                    this._frameFullScreenVisibility = View.GONE;

                    // Display after the frame when there is one.
                    this.loadFrame(myCampaign);

                    if (myCampaign.CanBePreloaded == 1)
                    {
                        try {
                            // CACHING CAN BE ACTIVATED (all ads except some CPM based ads!)
                            this.setWebViewAdSrc(new URI(this.buildAdWebURL(myCampaign.AdWebURL, 0, 0)));
                        }catch(URISyntaxException ex){}
                    }
                    else
                    {
                        this.setTmpWebSrcBeforeDisplay(this.buildAdWebURL(myCampaign.AdWebURL, 0, 0));
                    }

                    this.setBorderVisibility(View.VISIBLE);
                    this.setWebViewVisibility(View.VISIBLE); // DO NOT REMOVE! (used to update displays!)
                    displayAd = true;
                    break;
                }

                case AD_TYPE_VIDEO_HTML:
                {
                    this.setDisplayFullScreenInterstitial(View.VISIBLE);
                    this._nativeImageVisibility = View.GONE;
                    this._frameFullScreenVisibility = View.GONE;

                    // Display after the frame when there is one.
                    this.loadFrame(myCampaign);

                    try {
                        if (myCampaign.CanBePreloaded == 1)
                        {
                            // CACHING CAN BE ACTIVATED (all ads except some CPM based ads!)
                            this.setWebViewAdSrc(new URI(this.buildAdWebURL(myCampaign.AdWebURL, (int)this._appWidth, (int)this._appHeight)));
                        }
                        else
                        {
                            this.setTmpWebSrcBeforeDisplay(this.buildAdWebURL(myCampaign.AdWebURL, (int)this._appWidth, (int)this._appHeight));
                        }
                    }
                    catch(URISyntaxException ex){}

                    this._borderVisibility = View.VISIBLE;
                    this.setWebViewVisibility(View.VISIBLE); // DO NOT REMOVE! (used to update displays!)
                    displayAd = true;
                    break;
                }

                case AD_TYPE_FULL_INTERSTITIAL_NATIVE:
                {
                    this._displayFullScreenInterstitial = View.VISIBLE;
                    this._frameFullScreenVisibility = View.GONE;
                    this.setWebViewVisibility(View.GONE);  // DO NOT REMOVE!!! (used to update displays!)
                    this.setFullScreenAdImg(myCampaign.AdImageURL);
                    this.loadFrame(myCampaign);
                    this._borderVisibility = View.VISIBLE;
                    this._nativeImageVisibility = View.VISIBLE;
                    displayAd = true;

                    break;
                }
            }

            // Setup display pixel URL. Pixel URL is only called once the popup is really displayed.
            if (displayAd)
            {
                this.updateImpressionInfo(myCampaign);
            }

            // Required to recompute sizes.
            this.refreshScreenSize();

        }
        //break;
        //}
        //}
    }


    public void updateImpressionInfo(Campaign myCampaign) {
        //this.WebViewPixelVisibility = View.Collapsed;                                         // Will always be collapsed!
        this._tmpImpressionPixelSrc = this.BuildAdDisplayPixelURL(myCampaign.ImpressionPixelURL, 0, 0);      // Call up to 2 impression pixels on one single web page when there are some!
        if (myCampaign.ImpressionPixelURL.trim() != StringHelper.Empty)
        {
            this._webViewPixelVisibility = View.VISIBLE;
        }
    }


    //region Model properties

    private String _tmpWebSrcBeforeDisplay;
    public String getTmpWebSrcBeforeDisplay()
    {
        return this._tmpWebSrcBeforeDisplay;
    }

    public void setTmpWebSrcBeforeDisplay(String value)
    {
        if (this._tmpWebSrcBeforeDisplay != value)
        {
            this._tmpWebSrcBeforeDisplay = value;
            this.OnPropertyChanged("TmpWebSrcBeforeDisplay");
        }
    }

    private boolean _isPrefetched = false;  // SDK v2.2 for prefetching.
    public boolean isPrefetched()
    {
        return this._isPrefetched;
    }

    public void setIsPrefetched(boolean value) {
        if (this._isPrefetched != value)
        {
            this._isPrefetched = value;
            this.OnPropertyChanged("IsPrefetched");
        }
    }

    private boolean _isPrefetching = false;  // SDK v2.2 for prefetching: = true while prefetching.
    public boolean isPrefetching()
    {
        return this._isPrefetching;
    }

    public void setIsPrefetching(boolean value)
    {
        if (this._isPrefetching != value)
        {
            this._isPrefetching = value;
            this.OnPropertyChanged("IsPrefetching");
        }
    }

    private int _lastAdCalledStatus = NO_AD_AVAILABLE; // SDK v2.2 for prefetching.
    public int getLastAdCalledStatus()
    {
        return this._lastAdCalledStatus;
    }

    public void setLastAdCalledStatus(int value)
    {
        if (this._lastAdCalledStatus != value)
        {
            this._lastAdCalledStatus = value;
            this.OnPropertyChanged("LastAdCalledStatus");
        }
    }


    private int _currentCampaignID;
    public int getCurrentCampaignID()
    {
        return this._currentCampaignID;
    }

    public void setCurrentCampaignID(int value)
    {
        if (this._currentCampaignID != value)
        {
            this._currentCampaignID = value;
            this.OnPropertyChanged("CurrentCampaignID");
        }
    }

    private /*Visibility*/ int _borderVisibility;
    public /*Visibility*/ int getBorderVisibility()
    {
        return this._borderVisibility;
    }

    public void setBorderVisibility(/*Visibility*/ int value)
    {
        if (this._borderVisibility != value)
        {
            this._borderVisibility = value;
            this.OnPropertyChanged("BorderVisibility");
        }
    }

    private /*Visibility*/ int _nativeImageVisibility;
    public/*Visibility*/ int getNativeImageVisibility()
    {
        return this._nativeImageVisibility;
    }

    public void getNativeImageVisibility(/*Visibility*/ int value)
    {
        if (this._nativeImageVisibility != value)
        {
            this._nativeImageVisibility = value;
            this.OnPropertyChanged("NativeImageVisibility");
        }
    }

    private /*Visibility*/ int _closeButtonOutsideVisibility = View.GONE;
    public /*Visibility*/ int getCloseButtonOutsideVisibility()
    {
        return this._closeButtonOutsideVisibility;
    }

    public void setCloseButtonOutsideVisibility(/*Visibility*/ int  value)
    {
        if (this._closeButtonOutsideVisibility != value)
        {
            this._closeButtonOutsideVisibility = value;
            this.OnPropertyChanged("CloseButtonOutsideVisibility");
        }
    }

    private /*Visibility*/ int _closeButtonInsideVisibility = View.GONE;
    public /*Visibility*/ int  getCloseButtonInsideVisibility()
    {
        return this._closeButtonInsideVisibility;
    }

    public void getCloseButtonInsideVisibility(/*Visibility*/ int  value)
    {
        if (this._closeButtonInsideVisibility != value)
        {
            this._closeButtonInsideVisibility = value;
            this.OnPropertyChanged("CloseButtonInsideVisibility");
        }
    }

    private /*Visibility*/ int  _frameFullScreenVisibility;
    public /*Visibility*/ int  getFrameFullScreenVisibility()
    {
        return this._frameFullScreenVisibility;
    }

    public void getFrameFullScreenVisibility(/*Visibility*/ int  value)
    {
        if (this._frameFullScreenVisibility != value)
        {
            this._frameFullScreenVisibility = value;
            this.OnPropertyChanged("FrameFullScreenVisibility");
        }
    }

    private /*Visibility*/ int _displayFullScreenInterstitial;
    public /*Visibility*/ int getDisplayFullScreenInterstitial()
    {
        return this._displayFullScreenInterstitial;
    }


    public void setDisplayFullScreenInterstitial(/*Visibility*/ int value)
    {
        if (this._displayFullScreenInterstitial != value)
        {
            this._displayFullScreenInterstitial = value;
            this.OnPropertyChanged("DisplayFullScreenInterstitial");
        }
    }

        /*private Visibility _webViewVisibility;
        public Visibility WebViewVisibility
        {
            get
            {
                return this._webViewVisibility;
            }

            set
            {
                if (this._webViewVisibility != value)
                {
                    this._webViewVisibility = value;
                    this.OnPropertyChanged("WebViewVisibility");
                }
            }
        }*/

    private /*Visibility*/ int  _webViewPixelVisibility;
    public /*Visibility*/ int  getWebViewPixelVisibility()
    {
        return this._webViewPixelVisibility;
    }

    public void setWebViewPixelVisibility(/*Visibility*/ int  value)
    {
        if (this._webViewPixelVisibility != value)
        {
            this._webViewPixelVisibility = value;
            this.OnPropertyChanged("WebViewPixelVisibility");
        }
    }

        /*private int _impressionPixelWidth;
        public int ImpressionPixelWidth
        {
            get
            {
                return this._impressionPixelWidth;
            }

            set
            {
                if (this._impressionPixelWidth != value)
                {
                    this._impressionPixelWidth = value;
                    this.OnPropertyChanged("ImpressionPixelWidth");
                }
            }
        }

        private int _impressionPixelHeight;
        public int ImpressionPixelHeight
        {
            get
            {
                return this._impressionPixelHeight;
            }

            set
            {
                if (this._impressionPixelHeight != value)
                {
                    this._impressionPixelHeight = value;
                    this.OnPropertyChanged("ImpressionPixelHeight");
                }
            }
        }*/

    private String _closeButtonMargin = "0,0,0,0";
    public String getCloseButtonMargin()
    {
        return this._closeButtonMargin;
    }

    public void setCloseButtonMargin(String value)
    {
        if (this._closeButtonMargin != value)
        {
            this._closeButtonMargin = value;
            this.OnPropertyChanged("CloseButtonMargin");
        }
    }

    private String _imgFullScreenMargin = "0,0,0,0";
    public String getImgFullScreenMargin()
    {
        return this._imgFullScreenMargin;
    }

    public void setImgFullScreenMargin(String value)
    {
        if (this._imgFullScreenMargin != value)
        {
            this._imgFullScreenMargin = value;
            this.OnPropertyChanged("ImgFullScreenMargin");
        }
    }

    private int _fullScreenWidth;
    public int getFullScreenWidth()
    {
        return this._fullScreenWidth;
    }

    public void setFullScreenWidth(int value)
    {
        if(this._fullScreenWidth!=value)
        {
        this._fullScreenWidth=value;
        this.OnPropertyChanged("FullScreenWidth");
        }
    }

    private int _fullScreenHeight;
    public int getFullScreenHeight()
    {
        return this._fullScreenHeight;
    }

    public void setFullScreenHeight(int value)
    {
        if (this._fullScreenHeight != value)
        {
            this._fullScreenHeight = value;
            this.OnPropertyChanged("FullScreenHeight");
        }
    }

    private int _frameFullScreenHeight;
    public int getFrameFullScreenHeight()
    {
        return this._frameFullScreenHeight;
    }

    public void setFrameFullScreenHeight(int value)
    {
        if (this._frameFullScreenHeight != value)
        {
            this._frameFullScreenHeight = value;
            this.OnPropertyChanged("FrameFullScreenHeight");
        }
    }

    private int _frameFullScreenWidth;
    public int getFrameFullScreenWidth()
    {
        return this._frameFullScreenWidth;
    }

    public void setFrameFullScreenWidth(int value)
    {
        if (this._frameFullScreenWidth != value)
        {
            this._frameFullScreenWidth = value;
            this.OnPropertyChanged("FrameFullScreenWidth");
        }
    }

    private int _imgFullScreenHeight;
    public int getImgFullScreenHeight()
    {
        return this._imgFullScreenHeight;
    }

    public void setImgFullScreenHeight(int value)
    {
        if (this._imgFullScreenHeight != value)
        {
            this._imgFullScreenHeight = value;
            this.OnPropertyChanged("ImgFullScreenHeight");
        }
    }

    private int _imgFullScreenWidth;
    public int getImgFullScreenWidth()
    {
        return this._imgFullScreenWidth;
    }

    public void setImgFullScreenWidth(int value)
    {
        if (this._imgFullScreenWidth != value)
        {
            this._imgFullScreenWidth = value;
            this.OnPropertyChanged("ImgFullScreenWidth");
        }
    }

    private String _fullScreenAdImg;
    public String getFullScreenAdImg()
    {
        return this._fullScreenAdImg;
    }

    public void setFullScreenAdImg(String value)
    {
        if (this._fullScreenAdImg != value) {
            this._fullScreenAdImg = value;
            this.OnPropertyChanged("FullScreenAdImg");
        }
    }

    private String _tmpImpressionPixelSrc = StringHelper.Empty;
    public String getTmpImpressionPixelSrc()
    {
        return this._tmpImpressionPixelSrc;
    }

    public void setTmpImpressionPixelSrc(String value)
    {
        if (this._tmpImpressionPixelSrc != value)
        {
            this._tmpImpressionPixelSrc = value;
            this.OnPropertyChanged("TmpImpressionPixelSrc");
        }
    }

    private String _overlayImage;
    public String getOverlayImage()
    {
        return this._overlayImage;
    }

    public void setOverlayImage(String value)
    {
        if (this._overlayImage != value)
        {
            this._overlayImage = value;
            this.OnPropertyChanged("OverlayImage");
        }
    }
    //endregion
}

