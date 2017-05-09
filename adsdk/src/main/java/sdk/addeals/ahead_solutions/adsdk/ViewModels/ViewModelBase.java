package sdk.addeals.ahead_solutions.adsdk.ViewModels;

import android.net.Uri;
import android.content.*;
import android.opengl.Visibility;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Future;

import sdk.addeals.ahead_solutions.adsdk.AbstractAdManager;
import sdk.addeals.ahead_solutions.adsdk.AdManager;
import sdk.addeals.ahead_solutions.adsdk.Libs.Helpers.DataMapperFactory;
import sdk.addeals.ahead_solutions.adsdk.Libs.Helpers.HttpHelper;
import sdk.addeals.ahead_solutions.adsdk.Libs.Helpers.HttpHelperAsync;
import sdk.addeals.ahead_solutions.adsdk.Libs.Helpers.StringHelper;
import sdk.addeals.ahead_solutions.adsdk.Libs.Helpers.TimeHelper;
import sdk.addeals.ahead_solutions.adsdk.Models.CampaignsV3;

/**
 * Created by ArnOr on 02/05/2017.
 */

public abstract class ViewModelBase extends AbstractAdManager{

    int lastCampaignIndex = 0;  // ID of the first campaign called.
    public PropertyChangeSupport propertyChanged;

    protected void OnPropertyChanged(String name)
    {
        if (propertyChanged != null)
            propertyChanged = new PropertyChangeSupport(this);
            //PropertyChanged(this, new PropertyChangedEventArgs(name));
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChanged.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChanged.removePropertyChangeListener(listener);
    }

    protected final String DEFAULT_CLOSE_BUTTON_URI = "/AdDealsUniversalSDKW81/Content/Images/ClosingCross.png";
    public boolean _adWasClicked = false;
    public boolean _adLoaded = false;

    protected static String _country = StringHelper.Empty;
    public static String getCountry()
    {
        try
        {
            _country = Locale.getDefault().getCountry();
        }
        catch (Exception ex) { }
        return _country;
    }

    protected static String _language = StringHelper.Empty;
    public static String getLanguage()
    {
        try
        {
            _language = Locale.getDefault().getLanguage();
        }
        catch (Exception ex) { }
        return _language;
    }

    protected CampaignsV3 _campaigns;
    public CampaignsV3 getCampaigns()
    {
        return this._campaigns;
    }

    public void setCampaigns(CampaignsV3 value)
    {
        if (this._campaigns != value)
        {
            this._campaigns = value;
        }
    }

    protected Uri _webViewAdSrc;
    public Uri getWebViewAdSrc()
    {
        return this._webViewAdSrc;
    }

    public void getWebViewAdSrc(Uri value)
    {
        if (this._webViewAdSrc != value)
        {
            this.propertyChanged.firePropertyChange("WebViewAdSrc", this._webViewAdSrc, value);
            this._webViewAdSrc = value;
        }
    }

    protected double _appWidth = 480;
    public double getAppWidth()
    {
        return this._appWidth;
    }

    public void getAppWidth(double value)
    {
        if (this._appWidth != value)
        {
            this.propertyChanged.firePropertyChange("AppWidth", this._appWidth, value);
            this._appWidth = value;
        }
    }

    protected double _appHeight = 480;
    public double getAppHeight()
    {
        return this._appHeight;
    }

    public void setAppHeight(double value)
    {
        if (this._appHeight != value)
        {
            this.propertyChanged.firePropertyChange("AppHeight", this._appHeight, value);
            this._appHeight = value;
        }
    }

    protected String _closingButton;
    public String getClosingButton()
    {
        return this._closingButton;
    }

    public void getClosingButton(String value)
    {
        if (this._closingButton != value)
        {
            this.propertyChanged.firePropertyChange("ClosingButton", this._closingButton, value);
            this._closingButton = value;
        }
    }

    protected String _progressRingMargin = "0,0,0,0";
    public String getProgressRingMargin()
    {
        return this._progressRingMargin;
    }

    protected void setProgressRingMargin(String value)
    {
        if (this._progressRingMargin != value)
        {
            this.propertyChanged.firePropertyChange("ProgressRingMargin", this._progressRingMargin, value);
            this._progressRingMargin = value;
        }
    }

    protected String _campaignLinkURL;
    public String getCampaignLinkURL()
    {
        return this._campaignLinkURL;
    }

    public void setCampaignLinkURL(String value)
    {
        if (this._campaignLinkURL != value)
        {
            this.propertyChanged.firePropertyChange("CampaignLinkURL", this._campaignLinkURL, value);
            this._campaignLinkURL = value;
        }
    }

    protected String _htmlFBTag;
    public String getHTMLFBTag()
    {
        return this._htmlFBTag;
    }

    public void getHTMLFBTag(String value)
    {
        if (this._htmlFBTag != value)
        {
            this.propertyChanged.firePropertyChange("HTMLFBTag", this._htmlFBTag, value);
            this._htmlFBTag = value;
        }
    }


    public /*Future<*/CampaignsV3 GetCampaignAd(String requestedAdTypes, int reqAdWidth, int reqAdHeight, int campType, boolean strictSize)
    {
        HttpHelper httpHelper = new HttpHelper(/*AdManager.*/HTTP_QUERY_TIMEOUT);
        String baseURL = AdManager.AD_NETWORK_URL;

        switch (campType)
        {
            case CAMPAIGN_TYPE_BANNER: {
                baseURL = AdManager.BANNER_ADS_BASE_URL;
                break;
            }

            case CAMPAIGN_TYPE_INTERSTITIAL: {
                baseURL = AdManager.INTERSTITIAL_ADS_BASE_URL;
                break;
            }

            case CAMPAIGN_TYPE_VIDEO_REWARDED: {
                baseURL = AdManager.VIDEO_ADS_BASE_URL;
                break;
            }
        }

        String campaignV3URL = AdManager.ADDEALS_CAMPAIGN_URL_v3.replace("[APP_ID]", AdManager.getAppID()AppID);
        campaignV3URL = campaignV3URL.replace("[BASE_URL]", baseURL);
        campaignV3URL = campaignV3URL.replace("[APP_KEY]", AdManager.getAppKey());
        campaignV3URL = campaignV3URL.replace("[DEVICE_ID]", AdManager.GetDeviceID());
        try {
            campaignV3URL = campaignV3URL.replace("[DEVICE_MODEL]", URLEncoder.encode(AdManager.DEVICE_MODEL, "UTF-8"));
        }
        catch(UnsupportedEncodingException ex) {}
        campaignV3URL = campaignV3URL.replace("[DEVICE_OS]", AdManager.OS_VERSION);
        campaignV3URL = campaignV3URL.replace("[LANGUAGE]", AdManager.APP_LANGUAGE);
        campaignV3URL = campaignV3URL.replace("[COUNTRY]", AdManager.APP_COUNTRY);
        campaignV3URL = campaignV3URL.replace("[SDK_VERSION]", AdManager.SDK_VERSION);
        try {
            campaignV3URL = campaignV3URL.replace("[MOBILE_OPERATOR", URLEncoder.encode(AdManager.MOBILE_OPERATOR, "UTF-8"));
        }
        catch(UnsupportedEncodingException ex) {}
        campaignV3URL = campaignV3URL.replace("[APP_UID]", AdManager.APP_UID);
        campaignV3URL = campaignV3URL.replace("[APP_CONNECTION]", AdManager.APP_CONNECTION);
        campaignV3URL = campaignV3URL.replace("[CAMPAIGN_TYPE]", "" + campType);
        campaignV3URL = campaignV3URL.replace("[ADVERTISER_ID]", AdManager.ADVERTISER_UID);
        campaignV3URL = campaignV3URL.replace("[SCREEN_HEIGHT]", "" + (int)(reqAdHeight));
        campaignV3URL = campaignV3URL.replace("[SCREEN_WIDTH]", "" + (int)(reqAdWidth));
        campaignV3URL = campaignV3URL.replace("[AD_TYPES]", requestedAdTypes);
        campaignV3URL = campaignV3URL.replace("[USR_AGENT]", AdManager.USER_AGENT);
        campaignV3URL = campaignV3URL.replace("[ADDEALS_USER_ID]", "" + AdManager.ADDEALS_USER_ID);
        campaignV3URL = campaignV3URL.replace("[AGE]", "" + AdManager.userAge);

        String tmpSex = StringHelper.Empty;
        switch (AdManager.userSex)
        {
            case FEMALE:
            {
                tmpSex = "f";
                break;
            }

            case MALE:
            {
                tmpSex = "m";
                break;
            }
        }
        campaignV3URL = campaignV3URL.replace("[SEX]", "" + tmpSex);

        if (strictSize) campaignV3URL = campaignV3URL.replace("[STRICT_SIZE]", "" + 1);
        else campaignV3URL = campaignV3URL.replace("[STRICT_SIZE]", "" + 0);
        campaignV3URL = campaignV3URL.replace("[PREFETCH]", "1");                                           // STARTING SDK v2.2: Server-side, it's always PREFETCHED, meaning display is sent asynchronously when the display really occurs!!!
        campaignV3URL = campaignV3URL + "&rand=" + TimeHelper.getUTC();//DateTime.UtcNow.ToFileTimeUtc();                         // Reload new deal (caching issue)!

        // "http://ads.addealsnetwork.com/addeals/REST/v3/campaigns/?format=json&aid=1932&akey=LIE2H2N2CQSB&lang=fr&country=FR&os=&ctypeid=3&sdkv=3.0&adh=900&adw=1200&duid=4f6238c63a018e0a7101b4665356af37&advuid=538035f6476666d48294808205d3a48c&mop=&conn=ETHERNET&appuid=&adkind=&adtypes=[3][7]&prefetch=1&spid=&rand=130850692134853359&usragent=%20Mozilla/5.0%20(Windows%20NT%2010.0;%20ARM;%20Trident/7.0;%20Touch;%20rv:11.0;%20IEMobile/11.0;%20NOKIA;%20Lumia%20630)%20like%20Gecko"
        return new HttpHelperAsync()
            .get(campaignV3URL)
            .getResponseEntity(DataMapperFactory.create(DataMapperFactory.Format.JSON),CampaignsV3.class);//.getDataFromJsonWeb(campaignV3URL);
    }

    // We notify click via API only once.
    /*Future*/ void NotifyAdClick(int reqAdWidth, int reqAdHeight)
    {
        // Notify server via click API if not notified already! Up to 1 / ad!
        if (!this._adWasClicked)
        {
            this._adWasClicked = true;
            HttpHelper httpHelper = new HttpHelper(/*AbstractAdManager.*/HTTP_QUERY_TIMEOUT);
            String campLink = this.BuildClickURL(this.getCampaignLinkURL(), reqAdWidth, reqAdHeight);
            //String result = await httpHelper.GetStringFromWeb(campLink + "&rand=" + DateTime.UtcNow.ToFileTimeUtc());
            new HttpHelperAsync()
                .get(campLink + "&rand=" + System.currentTimeMillis())//Date..UtcNow.ToFileTimeUtc())
                .getResponseEntity(DataMapperFactory.create(DataMapperFactory.Format.JSON),CampaignsV3.class);//
        }
    }

     String BuildClickURL(String clickURL, int reqAdWidth, int reqAdHeight)
    {
        clickURL = clickURL.replace("%deviceID%", AdManager.GetDeviceID());
        clickURL = clickURL.replace("%advertiserUID%", ADVERTISER_UID);
        clickURL = clickURL.replace("%OS%", AdManager.OS_VERSION);
        try {
            clickURL = clickURL.replace("%deviceModel%", URLEncoder.encode(DEVICE_MODEL, "UTF-8"));
        }catch(UnsupportedEncodingException ex) {}
        clickURL = clickURL.replace("%fingerprint%", StringHelper.Empty);
        clickURL = clickURL.replace("%sdkv%", AdManager.SDK_VERSION);
        try {
            clickURL = clickURL.replace("%mop%", URLEncoder.encode(MOBILE_OPERATOR, "UTF-8"));
        }
        catch(UnsupportedEncodingException ex) {}
        clickURL = clickURL.replace("%appuid%", APP_UID);
        clickURL = clickURL.replace("%conn%", APP_CONNECTION);
        clickURL = clickURL.replace("%usragent%", AdManager.USER_AGENT); // ADDED.
        clickURL = clickURL.replace("%usrid%", "" + AdManager.ADDEALS_USER_ID);
        clickURL = clickURL.replace("%adh%", "" + (int)(reqAdHeight));
        clickURL = clickURL.replace("%adw%", "" + (int)(reqAdWidth));
        clickURL = clickURL.replace("%age%", "" + AdManager.userAge);
        clickURL = clickURL.replace("%moment%", "" + StringHelper.Empty);
        String tmpSex = StringHelper.Empty;
        switch (AdManager.userSex)
        {
            case FEMALE:
            {
                tmpSex = "f";
                break;
            }

            case MALE:
            {
                tmpSex = "m";
                break;
            }
        }
        clickURL = clickURL.replace("%sex%", "" + tmpSex);
        return clickURL;
    }

    String BuildAdWebURL(String webURL, int reqAdWidth, int reqAdHeight)
    {
        webURL = webURL.replace("%OS%", AdManager.OS_VERSION);
        webURL = webURL.replace("%lang%", AdManager.APP_LANGUAGE);
        webURL = webURL.replace("%country%", AdManager.APP_COUNTRY);
        webURL = webURL.replace("%sdkv%", AdManager.SDK_VERSION);
        webURL = webURL.replace("%advertiserUID%", AdManager.ADVERTISER_UID);
        webURL = webURL.replace("%usragent%", AdManager.USER_AGENT);
        webURL = webURL.replace("%usrid%", "" + AdManager.ADDEALS_USER_ID);

        //EXTRA OPTIONAL FILL-IN
        webURL = webURL.replace("%conn%", APP_CONNECTION);
        try {
            webURL = webURL.replace("%mop%", URLEncoder.encode(MOBILE_OPERATOR, "UTF-8"));
        }
        catch(UnsupportedEncodingException ex) {}
        webURL = webURL.replace("%appuid%", APP_UID);
        webURL = webURL.replace("%fingerprint%", StringHelper.Empty);
        webURL = webURL.replace("%deviceID%", AdManager.GetDeviceID());
        try {
            webURL = webURL.replace("%mop%", URLEncoder.encode(MOBILE_OPERATOR, "UTF-8"));
        }
        catch(UnsupportedEncodingException ex) {}
        webURL = webURL.replace("%adh%", "" + (int)(reqAdHeight));
        webURL = webURL.replace("%adw%", "" + (int)(reqAdWidth));
        webURL = webURL.replace("%age%", "" + AdManager.userAge);
        webURL = webURL.replace("%moment%", "" + StringHelper.Empty);
/*
    if(DEBUG_MODE) {
        webURL = webURL.replace(AdManager.VIDEO_PLAYER_RELEASE_MODE, AdManager.VIDEO_PLAYER_DEBUG_MODE);
    }
*/
        String tmpSex = StringHelper.Empty; // Default.
        switch (AdManager.userSex)
        {
            case FEMALE:
            {
                tmpSex = "f";
                break;
            }

            case MALE:
            {
                tmpSex = "m";
                break;
            }
        }
        webURL = webURL.replace("%sex%", "" + tmpSex);
        //webURL = webURL + "&rand=" + DateTime.UtcNow.ToFileTimeUtc();
        //webURL = "http://web.addealsnetwork.com/ads/videoplayer?cid=2703&aid=1113&akey=HILVIOFRFOLN&ctypeid=12&os=&lang=fr&country=FR&mop=&sdkv=3.4&advuid=aab3cfc44b77ae083af08f99bed45a77&usragent=Mozilla/5.0%20(Windows%20NT%206.3;%20WOW64;%20Trident/7.0;%20Touch;%20.NET4.0C;%20.NET4.0E;%20.NET%20CLR%202.0.50727;%20.NET%20CLR%203.0.30729;%20.NET%20CLR%203.5.30729;%20Tablet%20PC%202.0;%20InfoPath.3;%20WebView/2.0;%20rv:11.0)%20like%20Gecko&moment=&usrid=-1&adsize=1024x768&adw=0&adh=0&conn=WIFI&age=-1&sex=&rand=1113131153091135644896&creativeid=3684&token=UNIQUEID20168101325136H9EPBZTYM";

        return webURL;
    }

     String BuildAdDisplayPixelURL(String displayURL, int reqAdWidth, int reqAdHeight)
    {
        displayURL = displayURL.replace("%OS%", AdManager.OS_VERSION);
        displayURL = displayURL.replace("%lang%", AdManager.APP_LANGUAGE);
        displayURL = displayURL.replace("%country%", AdManager.APP_COUNTRY);
        displayURL = displayURL.replace("%sdkv%", AdManager.SDK_VERSION);
        displayURL = displayURL.replace("%conn%", APP_CONNECTION);
        displayURL = displayURL.replace("%advertiserUID%", AdManager.ADVERTISER_UID);
        displayURL = displayURL.replace("%usragent%", AdManager.USER_AGENT);
        displayURL = displayURL.replace("%usrid%", "" + AdManager.ADDEALS_USER_ID);
        displayURL = displayURL.replace("%adh%", "" + (int)(reqAdHeight));
        displayURL = displayURL.replace("%adw%", "" + (int)(reqAdWidth));
        displayURL = displayURL.replace("%age%", "" + AdManager.userAge);

/*
    if(DEBUG_MODE) {
        displayURL = displayURL.replace(AdManager.VIDEO_PLAYER_RELEASE_MODE, AdManager.VIDEO_PLAYER_DEBUG_MODE);
    }
*/

        String tmpSex = StringHelper.Empty; // Default.
        switch (AdManager.userSex)
        {
            case FEMALE:
            {
                tmpSex = "f";
                break;
            }

            case MALE:
            {
                tmpSex = "m";
                break;
            }
        }
        displayURL = displayURL.replace("%sex%", "" + tmpSex);
        return displayURL;
    }

    //region  process clicks
    boolean IsAPIClickURL;
    boolean ClickOpensBrowser;
    //endregion

    private Uri _impressionPixelSrc;
    public Uri getImpressionPixelSrc()
    {
        return this._impressionPixelSrc;
    }

    public void getImpressionPixelSrc(Uri value)
    {
        if (this._impressionPixelSrc != value)
        {
            this.propertyChanged.firePropertyChange("ImpressionPixelSrc", this._impressionPixelSrc, value);
            this._impressionPixelSrc = value;
        }
    }

    private Visibility _webViewVisibility;
    public Visibility getWebViewVisibility()
    {
        return this._webViewVisibility;
    }

    public void getWebViewVisibility(Visibility value)
    {
        if (this._webViewVisibility != value)
        {
            this.propertyChanged.firePropertyChange("WebViewVisibility", this._webViewVisibility value);
            this._webViewVisibility = value;
        }
    }

    public String ImpressionPixelSrcTmp = StringHelper.Empty;
}
