package sdk.addeals.ahead_solutions.adsdk;

import sdk.addeals.ahead_solutions.adsdk.AdManager;
import sdk.addeals.ahead_solutions.adsdk.Libs.Helpers.StringHelper;

/**
 * Created by ArnOr on 02/05/2017.
 */

public abstract class AbstractAdManager {

    /**/ protected static int DEFAULT_CAMPAIGN_ID = 2;

    /**/ protected static String VIDEO_COMPLETED_HTML_TAG = "videocompleted.addealsnetwork.com";
    /**/ protected static String VIDEO_LOADING_FAILED_HTML_TAG = "videoloadingfailed.addealsnetwork.com";
    /**/ protected static String VIDEO_LINKCLICKED_HTML_TAG = "videolinkclicked.addealsnetwork.com";
    /**/ protected static String POPUP_CLOSED_HTML_TAG = "><adclosed";
    /**/ protected static String VIDEO_PLAYER_RELEASE_MODE = "web.addealsnetwork.com";
    /**/ protected static String VIDEO_PLAYER_DEBUG_MODE = "addeals-staging.azurewebsites.net";

    protected static String AD_NETWORK_URL = "http://ads.addealsnetwork.com";
    protected static String ADDEALS_WEB_LINK = "http://web.addealsnetwork.com/wall?str=[STR]";
    protected static String ADDEALS_WEB_LINK_GENERIC = "http://web.addealsnetwork.com/wall?a=[APP_ID]&k=[APP_KEY]&advuid=[ADVERTISER_UID]&sdkv=[SDK_VERSION]";

    protected static String INSTALL_TRACKING_BASE_URL = "http://trk-int.addealsnetwork.com";
    protected static String INTERSTITIAL_ADS_BASE_URL = "http://adsinter1.addealsnetwork.com";
    protected static String BANNER_ADS_BASE_URL = "http://adsbanner1.addealsnetwork.com";
    protected static String VIDEO_ADS_BASE_URL = "http://adsvideo1.addealsnetwork.com";

    protected static String ADDEALS_NOTIFY_INSTALL = INSTALL_TRACKING_BASE_URL + "/addeals/Tracking/Add?did=[DEVICE_ID]&advuid=[ADVERTISER_UID]&aid=[APP_ID]&akey=[APP_KEY]&dmodel=[DEVICE_MODEL]&os=[DEVICE_OS]&sdkv=[SDK_VERSION]&mop=[MOBILE_OPERATOR]&appuid=[APP_UID]&conn=[APP_CONNECTION]&lang=[LANGUAGE]&country=[COUNTRY]&usragent=[USR_AGENT]";
    protected static String ADDEALS_NOTIFY_SESSION_URL_v3 = INSTALL_TRACKING_BASE_URL + "/addeals/Tracking/Session?aid=[APP_ID]&akey=[APP_KEY]&os=[DEVICE_OS]&lang=[LANGUAGE]&country=[COUNTRY]&sdkv=[SDK_VERSION]&advuid=[ADVERTISER_UID]&usrdlid=[ADDEALS_DOWNLOAD_ID]&usrid=[ADDEALS_USER_ID]&mop=[MOBILE_OPERATOR]&conn=[APP_CONNECTION]&firstsession=[IS_FIRST_SESSION]&usragent=[USR_AGENT]";

    protected static String ADDEALS_WEB_LINK_STR_PARAMS = "aid=[APP_ID]&akey=[APP_KEY]&os=[DEVICE_OS]&dmodel=[DEVICE_MODEL]&sdkv=[SDK_VERSION]&mop=[MOBILE_OPERATOR]&lang=[LANGUAGE]&country=[COUNTRY]&appuid=[APP_UID]&conn=[APP_CONNECTION]&deviceID=[DEVICE_ID]&advertiserUDID=[ADVERTISER_UID]";  // &adh=[AD_HEIGHT]&adw=[AD_WIDTH]
    protected static String ADDEALS_CAMPAIGN_URL_v3 = "[BASE_URL]/addeals/REST/v3/campaigns/?format=json&aid=[APP_ID]&akey=[APP_KEY]&lang=[LANGUAGE]&country=[COUNTRY]&os=[DEVICE_OS]&ctypeid=[CAMPAIGN_TYPE]&sdkv=[SDK_VERSION]&adh=[SCREEN_HEIGHT]&adw=[SCREEN_WIDTH]&duid=[DEVICE_ID]&advuid=[ADVERTISER_ID]&mop=[MOBILE_OPERATOR]&conn=[APP_CONNECTION]&appuid=[APP_UID]&adtypes=[AD_TYPES]&prefetch=[PREFETCH]&usragent=[USR_AGENT]&dmodel=[DEVICE_MODEL]&usrid=[ADDEALS_USER_ID]&strictsize=[STRICT_SIZE]&age=[AGE]&sex=[SEX]";

    protected static String ADDEALS_EMAIL = "addeals@ahead-solutions.com";
    protected static String ADDEALS_WEBSITE = "http://www.addealsnetwork.com";
    protected static String SDK_VERSION = "4.4"; // 4.4 Supports UserIDs... (Before 4.4 : only sessions receive userIDs).
    protected static String APP_LANGUAGE = StringHelper.Empty;
    protected static String APP_COUNTRY = StringHelper.Empty;
    protected static String OS_VERSION = StringHelper.Empty;
    protected static String DEVICE_MODEL = StringHelper.Empty;
    /**/ protected static String MOBILE_OPERATOR = StringHelper.Empty;
    protected static String APP_UID = StringHelper.Empty;
    /**/ protected static String APP_CONNECTION = StringHelper.Empty;
    /**/ protected static String ADVERTISER_UID = StringHelper.Empty;
    /**/ protected static String USER_AGENT = StringHelper.Empty;

    // Post install notification sent parameters:
    /**/ protected static long ADDEALS_USER_ID = -1;
    /**/ protected static long ADDEALS_DOWNLOAD_ID = -1;
    /**/ protected static long ADDEALS_ORIGIN_CLICK_ID = -1;

    // Campaign Types (for v2 web services)
    /**/ protected final int CAMPAIGN_TYPE_WALL = 5;
    /**/ protected final int CAMPAIGN_TYPE_INTERSTITIAL = 3;
    /**/ protected final int CAMPAIGN_TYPE_BANNER = 2;
    /**/ protected final int CAMPAIGN_TYPE_VIDEO_REWARDED = 12;

    // Interstitial finalants
    /**/ protected final int HTTP_QUERY_TIMEOUT = 10;

        /* protected final int AD_ACTION_NOT_INTERESTED = 3;
         protected final int AD_ACTION_MORE_OFFERS = 4;
         protected final int AD_ACTION_REMOVE_ADS = 5;
         protected final int AD_ACTION_CLOSE_ADS = 6;*/

    /**/ protected final int NO_AD_AVAILABLE = -1;       // No Ad received from AdDeals server
    /**/ protected final int AD_AVAILABLE = 1;           // 1 Square ad received from AdDeals server
    /**/ protected final int AD_AVAILABILITY_UNKNOWN = 100;
    /**/ protected final int ERROR_ACCESS_DENIED = 403;
    /**/ protected final int ERROR_SDK_NOT_INITIALIZED = 1001;
    /**/ protected final int ERROR_INCOMPATIBLE_AD = 1002;

    // Campaign targets
    /**/ protected final int TARGET_APP_DOWNLOAD = 1;
    /**/ protected final int TARGET_WEB_MOBILE = 2;
    /**/ protected final int TARGET_CLICK_TO_CALL = 3;
    /**/ protected final int TARGET_CLICK_TO_VIDEO = 4;

    // Ad type ID
    /**/ protected final int AD_TYPE_FULL_INTERSTITIAL_NATIVE = 3;
    /**/ protected final int AD_TYPE_FULL_INTERSTITIAL_HTML = 7;
    /**/ protected final int AD_TYPE_VIDEO_HTML = 9;
    /**/ protected final int AD_TYPE_MEDIUM_SQUARE_PICTURE = 10;
    /**/ protected static final int AD_TYPE_SQUARE_APP_INFO = 11;
    /**/ protected static final int AD_TYPE_MEDIUM_SQUARE_PICTURE_HTML = 12;
    /**/ protected static final int AD_TYPE_FULL_SQUARE_PICTURE_NATIVE = 13;
    /**/ protected static final int AD_TYPE_FULL_SQUARE_PICTURE_HTML = 14;

    // Session Types
    /**/ protected final int FIRST_APP_LAUNCH = 1;
    /**/ protected final int OTHER_APP_LAUNCH = 0;

    // Ad Kind
    ///**/ protected final int AD_KIND_DEFAULT = 1;
    ///**/ protected final int AD_KIND_FULL_SCREEN_ADS_ONLY = 3;
    ///**/ protected final int AD_KIND_SQUARE_ADS_ONLY = 4;

    // System Tray properties
    ///**/ protected final int SYSTEM_TRAY_PORTRAIT_HEIGHT = 32;
    ///**/ protected final int SYSTEM_TRAY_LANDSCAPE_HEIGHT = 72;

    // App current orientation
    ///**/ protected final int ORIENTATION_NOT_AVAILABLE = -1;
    ///**/ protected final int ORIENTATION_PORTRAIT = 1;
    ///**/ protected final int ORIENTATION_LANDSCAPE = 2;

    // Supported ad formats in this SDK - refer to AdTypes in database for more details.
    /**/ protected static final String NO_SUPPORTED_ADS = "";                    // Default - none. An ad format must be passed.
    /**/ protected static final String SQUARE_SUPPORTED_ADS = "[10]";            // [10][11] - square not supported in current SDK version.
    /**/ protected static final String FULL_SCREEN_SUPPORTED_ADS = "[3][7]";     // [3]: Full screen native ads (portrait & landscape) - pictures only / [7]: Full screen html ads.
    /**/ protected static final String REWARDED_VIDEO_SUPPORTED_ADS = "[9]";     // [9]: HTML based video ads (portrait & landscape) - pictures only
    /**/ protected static final String BANNER_SUPPORTED_ADS = "[2][6][9]";       // Videos supported via banners (300x250 in general)

    // Device type finalants
    /**/ protected static final int DEVICE_WINDOWS_PHONE = 1;
    /**/ protected static final int DEVICE_WINDOWS_TABLET = 2;
    /**/ protected static final int DEVICE_NOT_SPECIFIED = -1;

    // Refresh rate
    /**/ protected static final int BANNER_REFRESH_RATE_DEFAULT_AT_LAUNCH = 300;  // Until the SDK is initialized, we check it's initialized every 2 seconds.
    /**/ protected static final int BANNER_REFRESH_RATE_DEFAULT = 60;           // Once the SDK is initialized, we try to get ads every 60seconds by default, unless it's overriden by server data.
}
