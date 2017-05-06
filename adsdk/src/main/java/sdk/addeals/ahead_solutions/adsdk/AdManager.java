package sdk.addeals.ahead_solutions.adsdk;

import android.content.Context;
import android.net.NetworkInfo;
import android.util.Base64;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.concurrent.Future;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import sdk.addeals.ahead_solutions.adsdk.Libs.Helpers.DeviceSettingsHelper;
import sdk.addeals.ahead_solutions.adsdk.Libs.Helpers.HttpHelper;
import sdk.addeals.ahead_solutions.adsdk.Libs.Helpers.StringHelper;
import sdk.addeals.ahead_solutions.adsdk.Libs.Helpers.TimeHelper;
import sdk.addeals.ahead_solutions.adsdk.Libs.Helpers.UserAgentHelper;

/**
 * Created by ArnOr on 02/05/2017.
 */

public class AdManager extends AbstractAdManager  {
        //private static AdDealsBannerViewModel adDealsBannerViewModel = null;
        //private static AdDealsBannerAdViewModel bannerAdViewModel = null;
        private static AdDealsPopupAdViewModel popupAdViewModel = null;
        private static AdDealsPopupAdViewModel popupRewardedVideoAdViewModel = null;
        private static AdDealsWallViewModel wallViewModel = null;
        private static AdDealsPopupAd adDealsSquare = null;
        private static AdDealsPopupAd adDealsSquareRewardedVideos = null;
        private static AdDealsWall adDealsWall = null;
        static boolean IsPopupOpening = false;
        public static boolean SDKinitialized = false;
        public static Popup adPopup = null;

        // Demographics
        public static Sex userSex = Sex.UNKNOWN;
        public static int userAge = -1;
        public static String location = StringHelper.Empty;

        public enum BannerAdSizes
        {
            //BANNER_WINDOWS_PHONE_AUTOSCALE_PORTRAIT,
            BANNER_WINDOWS_PHONE_320x50,
            LEADERBOARD_WINDOWS_TABLET_PC_728x90,
            WIDE_SKYSCRAPER_WINDOWS_TABLET_PC_160x600,
            MEDIUM_RECTANGLE_WINDOWS_TABLET_PC_300x250,
            SQUARE_WINDOWS_PHONE_173x173,
            SQUARE_WINDOWS_TABLET_PC_250x250
        }

        /// <summary>
        /// If you know ther user's age, providing it will improve CPMs.
        /// int > 0
        /// </summary>
        /// <param name="age"></param>
    public void SetUserAge(int age) {
        userAge = age;
    }

    /// <summary>
    /// Set it / user to improve CPMs
    /// </summary>
    /// <param name="sex"></param>
    public void SetUserSex(Sex sex) {
        userSex = sex;
    }

    /// <summary>
    /// Set it / user to improve CPMs
    /// </summary>
    /// <param name="latitude"></param>
    /// <param name="longitude"></param>
        /*public void SetUserLocation(double latitude, double longitude){
            location = latitude + "," + longitude;
        }*/

    public enum Sex {
        MALE,
        FEMALE,
        UNKNOWN
    }

    /// <summary>
    /// FULLSCREENPOPUPAD: only full screen ads will be sent at this location. This can be disabled from remote (if you disable AdDeals interstitial ads from your AdDeals account).
    /// </summary>
    public enum AdKind
    {
        WALLAD,
        FULLSCREENPOPUPAD,
        REWARDEDVIDEOAD
        //VIDEOADS
        //BONUS
    }

    public enum DeviceType
    {
        PHONE,
        TABLET_PC,
        UNKNOWN
    }

    public enum CloseButtonPosition
    {
        TOPRIGHT,
        ONAD
    }

    /// <summary>
    /// App download source.
    /// </summary>
    public enum AppDownloadSource
    {
        ADDEALS,
        UNKNOWN
    }

    /// <summary>
    /// App session source.
    /// </summary>
    public enum AppSessionSource
    {
        ADDEALS,
        UNKNOWN
    }

    private static String EncodeTo64(String toEncode)
    {
        final Charset UTF_8 = Charset.forName("UTF-8");
        ByteBuffer toEncodeAsBytes = UTF_8.encode(toEncode);
        String returnValue = Base64.encodeToString(toEncodeAsBytes.array(), Base64.DEFAULT);//Base64.encodeToString(toEncodeAsBytes);
        return returnValue;
    }

    // Can be used later to count # of sessions
    // public static int IsNewSession = 1;
    // TESTING URL: "http://127.0.0.1:81/addeals/REST/v1/campaigns/?format=json&aid=12&akey=APPDEALS_JEOUIZZOJ&filter=addealswall";


    //region AdDeals Ad Manager publicly exposed methods

    /// <summary>
    /// Initializes AdDeals SDK for Windows Phone
    /// </summary>
    /// <param name="appID">Unique Application ID provided by AdDeals</param>
    /// <param name="appKey">Unique Application Key provided by AdDeals</param>
    public static Future<Boolean> InitSDK(Panel layoutRoot, String appID, String appKey)
    {
        try
        {
            AppID = appID;
            AppKey = appKey;

            // MANDATORY VALUE.
            AdManager.USER_AGENT = await UserAgentHelper.GetUserAgent(layoutRoot);

            // Initialize settings
            SettingsHelperSDK settings = new SettingsHelperSDK();
            settings.initSettings();
            SetNetworkConnectionType(); // This is called several times to match the connection type as well as possible.

            // Cannot be initialized more than once / app launch (while it's in memory) or / day (so we try to notify install again and session)
            Date lastLaunch = DateTime.FromFileTimeUtc((long)ApplicationData.Current.LocalSettings.Values[AbstractSettingsHelperSDK.AS20082013DATE_LAST_LAUNCH]);
            if (!SDKinitialized || (SDKinitialized && lastLaunch.Add(new TimeSpan(0, 12, 0, 0)) < DateTime.UtcNow)
                    || !(boolean)ApplicationData.Current.LocalSettings.Values[AbstractSettingsHelperSDK.AS20082013INSTALL_NOTIFIED])
            {
                ApplicationData.Current.LocalSettings.Values[AbstractSettingsHelperSDK.AS20082013DATE_LAST_LAUNCH] = DateTime.UtcNow.ToFileTimeUtc();
                ApplicationData.Current.LocalSettings.Values[AbstractSettingsHelperSDK.AS20082013NUMBER_OF_LAUNCHES] = (int)ApplicationData.Current.LocalSettings.Values[AbstractSettingsHelperSDK.AS20082013NUMBER_OF_LAUNCHES] + 1;

                // Call Web Service to inform that a new download occured (for tracking purpose)
                InitializeDeviceInfo();
                NotifyNewInstall(); // Notify installs + Sessions.

                SDKinitialized = true;
                if (InitSDKSuccess != null)
                {
                    InitSDKSuccess(new object(), new EventArgs());
                }
            }
        }
        catch (Exception)
        {
            if (!SDKinitialized && InitSDKFailed != null)
            {
                InitSDKFailed(new object(), new EventArgs());
            }
        }

        return SDKinitialized;
    }

    /// <summary>
    /// SDK could not be initialized.
    /// </summary>
    public static event EventHandler InitSDKFailed;

    /// <summary>
    /// SDK has been successfully initialized.
    /// </summary>
    public static event EventHandler InitSDKSuccess;

    /// <summary>
    /// This event is used to notify the developer that the user has downloaded the app from AdDeals links or not.
    /// This will return AdDeals in case you launch advertising campaigns on AdDeals. Like special exclusive offers for targeted AdDeals or social networks users...
    /// </summary>
    public static event EventHandler AppDownloadSourceDetected;

    /// <summary>
    /// This event is used to notify the developer that the user has launched the app following a click on an AdDeals ad or coming from an AdDeals link.
    /// This also provides AdDeals origin campaign information to display specific ads to end users (Like for targeted exclusive offers!)
    /// </summary>
    public static event EventHandler AppSessionSourceDetected;


    /// <summary>
    /// Generated AdDeals Wall Web Link that needs to be called from a non-silverlight app (like XNA only games)
    /// </summary>
     static String GetWallWebLink()
    {
        // New link: http://web.addealsnetwork.com/wall?a=1932&k=LIE2H2N2CQSB&advuid=[ADVERTISERID]
        String link = ADDEALS_WEB_LINK_GENERIC;

        if (AdManager.DeviceKind.Equals(AdManager.DeviceType.PHONE))
        {
            link = ADDEALS_WEB_LINK;
            String strToEncode = ADDEALS_WEB_LINK_STR_PARAMS.replace("[APP_ID]", AppID);
            strToEncode = strToEncode.replace("[APP_KEY]", AppKey);
            strToEncode = strToEncode.replace("[DEVICE_OS]", OS_VERSION);
            strToEncode = strToEncode.replace("[DEVICE_MODEL]", WebUtility.UrlEncode(DEVICE_MODEL));
            strToEncode = strToEncode.replace("[SDK_VERSION]", SDK_VERSION);
            strToEncode = strToEncode.replace("[MOBILE_OPERATOR]", WebUtility.UrlEncode(MOBILE_OPERATOR));
            strToEncode = strToEncode.replace("[LANGUAGE]", APP_LANGUAGE);
            strToEncode = strToEncode.replace("[COUNTRY]", APP_COUNTRY);
            strToEncode = strToEncode.replace("[APP_UID]", APP_UID);
            strToEncode = strToEncode.replace("[APP_CONNECTION]", APP_CONNECTION);
            strToEncode = strToEncode.replace("[ADVERTISER_UID]", ADVERTISER_UID);
            strToEncode = strToEncode.replace("[DEVICE_ID]", WebUtility.UrlEncode(GetDeviceID()));
            strToEncode = strToEncode + "&" + TimeHelper.getUTC()//.ToFileTimeUtc();

            String strEncoded = EncodeTo64(strToEncode);
            link = link.replace("[STR]", strEncoded);
        }
        else
        {
            link = ADDEALS_WEB_LINK_GENERIC.replace("[APP_ID]", AppID);
            link = link.replace("[APP_KEY]", AppKey);
            link = link.replace("[ADVERTISER_UID]", ADVERTISER_UID);
            link = link.replace("[SDK_VERSION]", SDK_VERSION);
        }

        return link;
    }

    /// <summary>
    /// Calling this method will show up AdDeals web wall. This will only work for smartphones.
    /// </summary>
    public async static void ShowWebWall()
    {
        await Launcher.LaunchUriAsync(new URI(GetWallWebLink()));
    }

    /// <summary>
    /// Creates/Initializes or get an existing AdDeals popup ad.
    /// </summary>
    /// <param name="mainPageLayout"></param>
    /// <returns></returns>
    public static Future<AdDealsPopupAd> GetPopupAd(Panel mainPageLayout, AdManager.AdKind adKindSupported)
    {
            //region old
        //ManualResetEvent wait = new ManualResetEvent(false);
            /*UserAgentHelper.GetUserAgent(
                mainPageLayout,
                userAgent =>
                {
                    USER_AGENT = userAgent;
                });*/
        // wait until Set
        //wait.WaitOne();
            //endregion

        if (AdManager.USER_AGENT == null || AdManager.USER_AGENT == String.Empty)
        {
            // MANDATORY VALUE.
            AdManager.USER_AGENT = await UserAgentHelper.GetUserAgent(mainPageLayout);
        }

        // Only open a new AdDealsSquare if the popup is closed.
        if (GetAdDealsSquare(adKindSupported) == null)
        {
            switch (adKindSupported)
            {
                case AdKind.REWARDEDVIDEOAD:
                {
                    adDealsSquareRewardedVideos = new AdDealsPopupAd(mainPageLayout, adKindSupported);
                    break;
                }

                default:
                {
                    adDealsSquare = new AdDealsPopupAd(mainPageLayout, adKindSupported);
                    break;
                }
            }

        }

        if (GetAdDealsSquare(adKindSupported) != null)
        {
            GetAdDealsSquare(adKindSupported).SetAdRequested(adKindSupported);
            GetAdDealsSquare(adKindSupported).SetLayout(mainPageLayout);
            GetAdDealsSquare(adKindSupported).SetPercentScreenAd(0.94);
            GetAdDealsSquare(adKindSupported).SetCloseButtonPosition(CloseButtonPosition.ONAD);
        }

        return GetAdDealsSquare(adKindSupported);
    }

        /*public async static Task<boolean> CacheAd(Panel mainPageLayout, AdManager.AdKind adKindSupported)
        {
            if (adDealsSquare == null)
            {
                await GetPopupAd(mainPageLayout, adKindSupported);
            }

            return await adDealsSquare.CacheAd();
        }

        public async static Task<boolean> IsVideoAvailable(Panel mainPageLayout, AdManager.AdKind adKindSupported)
        {
            if (adDealsSquare == null)
            {
                await GetPopupAd(mainPageLayout, adKindSupported);
            }

            return await adDealsSquare.IsVideoAvailable();
        }*/

    public static AdDealsWall GetWallAd()
    {
        if (adDealsWall == null)
        {
            adDealsWall = new AdDealsWall();
        }

        return adDealsWall;
    }

        /*public static boolean IsPopupAdOpen()
        {
            if (adDealsSquare != null)
                return adDealsSquare.IsPopupOpen();
            else return false;
        }

        public static boolean IsWallAdOpen()
        {
            if (adDealsWall != null)
                return adDealsWall.IsPopupOpen();
            else return false;
        }*/

    public static boolean IsAdPopupOpen()
    {
        if (adPopup != null)
        {
            return adPopup.IsOpen;
        }
        else return false;
    }


        #region old
    /// <summary>Allows to set a minimal delay between 2 interstitials. If this delay is not reached, then an Event: Delay between interstitial not reached is sent. Minimum by default is 3 seconds. It cannot be less than 3 seconds.</summary>
    /// <param name="delay">Minimal delay between two interstitial displays in seconds (Default = 1 display / 3 seconds delay)</param>
    /// <returns>Current delay between interstitial displays</returns>
        /*public static int SetMinDelayBetweenAdDealsSquareDisplays(int delay)
        {
            if (AdDealsSquareViewModel != null)
                return AdDealsSquareViewModel.SetMinDelayBetweenDisplays(delay);
            else return AdDealsSquareViewModel.DEFAULT_DELAY;
        }*/
        #endregion

        #endregion



    // Only 1 interstitial can be opened at a time from the AdManager.
     static void ResetAdDealsSquare()
    {
        //adDealsSquare = null;
    }

    // Only 1 interstitial can be opened at a time from the AdManager.
     static void ResetAdDealsWall()
    {
        adDealsWall = null;
    }

        /* static AdDealsPopupAd GetAdDealsSquare()
        {
            return adDealsSquare;
        }*/

    /// <summary>
    /// User-agent has been initialized BEFORE (in InitSDK())
    /// </summary>
    private static void InitializeDeviceInfo()
    {
        try
        {
            //OS_VERSION = "8.1";
            //String test2 = easClientDeviceInformation.OperatingSystem;
            OS_VERSION = "";
            APP_COUNTRY = DeviceSettingsHelper.getCountryCode();
            APP_LANGUAGE = DeviceSettingsHelper.getLanguageCode();//new Language(GlobalizationPreferences.Languages[0]).LanguageTag.Split('-')[0];
        }
        catch (Exception ex)
        {
            if (StringHelper.isNullOrEmpty(APP_LANGUAGE))
            {
                APP_LANGUAGE = "en";
            }
        }

        EasClientDeviceInformation easClientDeviceInformation = new EasClientDeviceInformation();
        try
        {
            if (!easClientDeviceInformation.SystemManufacturer.ToLower().Equals("system manufacturer")
                    && !easClientDeviceInformation.SystemProductName.ToLower().Equals("system product name"))
            {
                DEVICE_MODEL = easClientDeviceInformation.SystemManufacturer + "|" +
                        easClientDeviceInformation.SystemProductName;
            }
        }
        catch (Exception) { }

        try
        {
            MOBILE_OPERATOR = StringHelper.Empty;   // Unable to get with Windows 8.1.
        }
        catch (Exception) { }

        try
        {
            AdvertisingIdClient.Info idInfo = null;
            try {
                idInfo = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext());
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try{
                ADVERTISER_UID = idInfo.getId();
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
        catch (Exception) { }

        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            try {
                // use the factory to create a documentbuilder
                DocumentBuilder builder = factory.newDocumentBuilder();

                // create a new document from input source
                FileInputStream fis = new FileInputStream("AndroidManifest.xml");
                InputSource is = new InputSource(fis);
                Document doc = builder.parse(is);

                // get the first element
                Element document = doc.getDocumentElement();
                NodeList applicationTag = document.getElementsByTagName("application");
                Node applicationNode = null;
                if(applicationTag.getLength() > 0)
                {
                    applicationNode = applicationTag.item(0);// android:sharedUserId
                    Attr attr = (Attr) applicationNode.getAttributes().getNamedItem("android:sharedUserId"); //not is what needed!
                    if (attr != null) {
                        APP_UID = attr.getValue();
                        //APP_UID = APP_UID.replace("{", "");
                        //APP_UID = APP_UID.replace("}", "");
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            /*
            APP_UID = (from manifest in
            XElement.Load("WMAppManifest.xml").Descendants("App")
            select manifest).SingleOrDefault().Attribute("ProductID").Value;*/
        }
        catch (Exception) { }

        try
        {
            if (USER_AGENT.toLowerCase().contains("windows phone") && easClientDeviceInformation.OperatingSystem.ToLower().Equals("windowsphone"))
            {
                DeviceKind = DeviceType.PHONE;
            }
            else if (USER_AGENT.toLowerCase().contains("windows") && easClientDeviceInformation.OperatingSystem.ToLower().Equals("windows"))
            {
                DeviceKind = DeviceType.TABLET_PC;
            }
        }
        catch (Exception) { }

        UpdateUserIDs();
    }

    private static void UpdateUserIDs()
    {
        try
        {
            ADDEALS_USER_ID = (long)ApplicationData.Current.LocalSettings.Values[AbstractSettingsHelperSDK.ADDEALS20150915USR_ID];
        }
        catch (Exception) { }
        try
        {
            ADDEALS_DOWNLOAD_ID = (long)ApplicationData.Current.LocalSettings.Values[AbstractSettingsHelperSDK.ADDEALS20150915DOWNLOAD_ID];
            ADDEALS_ORIGIN_CLICK_ID = (long)ApplicationData.Current.LocalSettings.Values[AbstractSettingsHelperSDK.ADDEALS20150915CLICK_ID];
        }
        catch (Exception) { }
    }


    // http://www.guruumeditation.net/blog/internet-connection-type-detection-in-winrt
    private static void SetNetworkConnectionType()
    {
        // Default value.
        APP_CONNECTION = "CELLULAR_UNKNOWN";
        this.co
        Context.getSystemService(Context.CONNECTIVITY_SERVICE)
        ConnectionProfile currentconnection = NetworkInfo. .DetailedState();
        if (currentconnection != null) {
            //  return "";
            switch (currentconnection.NetworkAdapter.IanaInterfaceType)
            {
                case 6:
                    APP_CONNECTION = "ETHERNET";
                    break;

                case 71:
                    APP_CONNECTION = "WIFI";
                    break;

                case 216:
                    APP_CONNECTION = "GPRS";
                    break;

                case 243:
                    APP_CONNECTION = "3G";
                    break;

                case 244:
                    APP_CONNECTION = "4G";
                    break;
            }
        }
    }

    private static void NotifyNewInstall()
    {
        if (!(boolean)ApplicationData.Current.LocalSettings.Values[AbstractSettingsHelperSDK.AS20082013INSTALL_NOTIFIED])
        {
            CallInstallWebService();
        }
        else NotifyNewSession(OTHER_APP_LAUNCH);
    }

    // To finish up, notify user session to addeals server...
    private async static void NotifyNewSession(int sessionTypeID)
    {
        try
        {
            //long test = long.Parse("" + ApplicationData.Current.LocalSettings.Values[AbstractSettingsHelperSDK.ADDEALS20150915CLICK_ID]);
            // Call web service to inform server that the app has been launched...
            HttpHelper httpHelper = new HttpHelper(HTTP_QUERY_TIMEOUT);
            String sessionURL = ADDEALS_NOTIFY_SESSION_URL_v3.replace("[APP_ID]", AppID);
            sessionURL = sessionURL.replace("[APP_KEY]", AppKey);
            sessionURL = sessionURL.replace("[DEVICE_ID]", GetDeviceID());
            sessionURL = sessionURL.replace("[DEVICE_MODEL]", WebUtility.UrlEncode(DEVICE_MODEL));
            sessionURL = sessionURL.replace("[DEVICE_OS]", OS_VERSION);
            sessionURL = sessionURL.replace("[LANGUAGE]", APP_LANGUAGE);
            sessionURL = sessionURL.replace("[COUNTRY]", APP_COUNTRY);
            sessionURL = sessionURL.replace("[SDK_VERSION]", SDK_VERSION);
            sessionURL = sessionURL.replace("[MOBILE_OPERATOR]", WebUtility.UrlEncode(MOBILE_OPERATOR));
            sessionURL = sessionURL.replace("[APP_CONNECTION]", APP_CONNECTION);
            sessionURL = sessionURL.replace("[ADVERTISER_UID]", ADVERTISER_UID);                   // Windows Phone 8.1 ? Can be get!
            sessionURL = sessionURL.replace("[USR_AGENT]", USER_AGENT);                            // Windows Phone 8.1 ? Can be get!
            sessionURL = sessionURL.replace("[ADDEALS_DOWNLOAD_ID]", "" + AdManager.ADDEALS_DOWNLOAD_ID);     // Windows Phone 8.1 ? Can be get!
            sessionURL = sessionURL.replace("[ADDEALS_USER_ID]", "" + AdManager.ADDEALS_USER_ID);              // Windows Phone 8.1 ? Can be get!
            sessionURL = sessionURL.replace("[IS_FIRST_SESSION]", "" + sessionTypeID);
            sessionURL = sessionURL + "&" + DateTime.UtcNow.ToFileTimeUtc();                       // Reload new deal (caching issue)!

            // Get ADSession result...
            AppSession appSession = await httpHelper.GetDataFromJsonWeb<AppSession>(sessionURL);
            if (AppSessionSourceDetected != null)
            {
                AppSessionSourceDetected(appSession, new EventArgs());
            }
        }
        catch (Exception) { }
    }

    public static String GetDeviceID()
    {
        HardwareToken token = HardwareIdentification.GetPackageSpecificToken(null);
        IBuffer hardwareId = token.Id;

        HashAlgorithmProvider hasher = HashAlgorithmProvider.OpenAlgorithm("MD5");
        IBuffer hashed = hasher.HashData(hardwareId);

        String hashedString = CryptographicBuffer.EncodeToHexString(hashed);
        return hashedString;
    }

    private async static void CallInstallWebService()
    {
        // Call Rest Web Services
        HttpHelper httpHelper = new HttpHelper(HTTP_QUERY_TIMEOUT);
        try
        {
            String installURL = ADDEALS_NOTIFY_INSTALL.replace("[APP_ID]", AppID);
            installURL = installURL.replace("[APP_KEY]", AppKey);
            installURL = installURL.replace("[DEVICE_ID]", GetDeviceID());
            installURL = installURL.replace("[DEVICE_MODEL]", WebUtility.UrlEncode(DEVICE_MODEL));
            installURL = installURL.replace("[DEVICE_OS]", OS_VERSION);
            installURL = installURL.replace("[LANGUAGE]", APP_LANGUAGE);
            installURL = installURL.replace("[COUNTRY]", APP_COUNTRY);
            installURL = installURL.replace("[FINGERPRINT]", String.Empty);
            installURL = installURL.replace("[SDK_VERSION]", SDK_VERSION);
            installURL = installURL.replace("[MOBILE_OPERATOR]", WebUtility.UrlEncode(MOBILE_OPERATOR));
            installURL = installURL.replace("[APP_UID]", APP_UID);
            installURL = installURL.replace("[APP_CONNECTION]", APP_CONNECTION);
            installURL = installURL.replace("[ADVERTISER_UID]", ADVERTISER_UID);
            installURL = installURL.replace("[USR_AGENT]", USER_AGENT);

            try
            {
                AppInstall conversion = wait HttpHelper<AppInstall>.GetDataFromJsonWeb(installURL);
                if (conversion != null)
                {
                    ApplicationData.Current.LocalSettings.Values[AbstractSettingsHelperSDK.AS20082013INSTALL_NOTIFIED] = true;
                    if (conversion.ClickID > 0) ApplicationData.Current.LocalSettings.Values[AbstractSettingsHelperSDK.ADDEALS20150915CLICK_ID] = conversion.ClickID;
                    if (conversion.DownloadID > 0) ApplicationData.Current.LocalSettings.Values[AbstractSettingsHelperSDK.ADDEALS20150915DOWNLOAD_ID] = conversion.DownloadID;
                    if (conversion.UserID > 0) ApplicationData.Current.LocalSettings.Values[AbstractSettingsHelperSDK.ADDEALS20150915USR_ID] = conversion.UserID;
                    UpdateUserIDs();

                    if (conversion.DownloadID > 0)
                    {
                        if (AppDownloadSourceDetected != null)
                        {
                            AppDownloadSourceDetected(AppDownloadSource.ADDEALS, new EventArgs());
                        }
                    }
                    else
                    {
                        if (AppDownloadSourceDetected != null)
                        {
                            AppDownloadSourceDetected(AppDownloadSource.UNKNOWN, new EventArgs());
                        }
                    }
                }
            }
            catch (Exception) { }

            try
            {
                NotifyNewSession(FIRST_APP_LAUNCH);
            }
            catch (Exception) { }
        }
        catch (Exception) { }
    }


    /// <summary>
    /// A static ViewModel used by the views to bind against.
    /// </summary>
    /// <returns>The SquareViewModel object.</returns>
        /* static AdDealsPopupAdViewModel AdDealsPopupAdViewModel
        {
            get
            {
                // Delay creation of the view model until necessary
                return popupAdViewModel ?? (popupAdViewModel = new AdDealsPopupAdViewModel());
            }
        }*/

        /* boolean IsPopupOpen()
        {
            if (AdManager.adPopup != null)
            {
                return AdManager.adPopup.IsOpen;
            }
            else return false;
        }*/

     static AdDealsPopupAdViewModel getPopupViewModel(AdKind adkind)
    {
        switch (adkind)
        {
            case REWARDEDVIDEOAD:
            {
                return ( popupRewardedVideoAdViewModel != null) ?  popupRewardedVideoAdViewModel : new AdDealsPopupAdViewModel();
            }

            default:
            {
                return ( popupAdViewModel != null) ?  popupAdViewModel : new AdDealsPopupAdViewModel();
            }
        }
    }

    static AdDealsPopupAd getAdDealsSquare(AdKind adkind)
    {
        switch (adkind)
        {
            case REWARDEDVIDEOAD:
            {
                return adDealsSquareRewardedVideos;
            }

            default:
            {
                return adDealsSquare;
            }
        }
    }

    /// <summary>
    /// A static ViewModel used by the views to bind against.
    /// </summary>
    /// <returns>The SquareViewModel object.</returns>
     static AdDealsWallViewModel getAdDealsWallViewModel()
    {
        // Delay creation of the view model until necessary
        return wallViewModel ?? (wallViewModel = new AdDealsWallViewModel());
    }


        #region general properties

    private static boolean _disableHeaderWebLink = false;
    public static boolean getDisableHeaderWebLink()
    {
        return _disableHeaderWebLink;
    }

    private void setDisableHeaderWebLink(boolean value)
    {
        if (_disableHeaderWebLink != value)
        {
            _disableHeaderWebLink = value;
        }
    }

    private static String _appKey = StringHelper.Empty;
    public static String getAppKey()
    {
        return _appKey;
    }

    private void setAppKey(String value)
    {
        if (_appKey != value)
        {
            _appKey = value;
        }
    }

    private static String _appID = StringHelper.Empty;
    public static String getAppID()
    {
        return _appID;
    }


    private void setAppID(String value)
    {
        if (_appID != value)
        {
            _appID = value;
        }
    }

    private static DeviceType _deviceKind = DeviceType.UNKNOWN;
    public static DeviceType getDeviceKind()
    {
        return _deviceKind;
    }

    private void setDeviceKind(DeviceType value)
    {
        if (_deviceKind != value)
        {
            _deviceKind = value;
        }
    }

        #endregion

    static async void OpenMarketPlace(String webLink)
    {
        try
        {
            await Launcher.LaunchUriAsync(new URI(webLink));
        }
        catch (Exception) { }
    }

}