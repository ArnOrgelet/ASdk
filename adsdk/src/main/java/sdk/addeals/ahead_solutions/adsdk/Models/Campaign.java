package sdk.addeals.ahead_solutions.adsdk.Models;
import com.google.gson.annotations.SerializedName;

import java.util.List;
/**
* Created by ArnOr on 02/05/2017.
*/

public class Campaign {

    @SerializedName("offerid")                            // Campaign/Offer ID
    public int OfferID;
    @SerializedName("adtypeid")                          // Returns the ad type ID. Full screen native ad = 3, Full screen responsive designed dynamic HTML ad = 7...
    public int AdTypeID;
    @SerializedName("targettypeid")                      // Target bID (download...)
    public int TargetTypeID;
    @SerializedName("adtitle")                           // This is some ad text, short description (60 chars max) that can be used below native ads (if available)
    public String AdTitle;
    @SerializedName("adimgurl")                          // This is the ad image URL for native ads.
    public String AdImageURL;
    @SerializedName("adshorttext")                       // This is some ad text, short description (60 chars max) that can be used below native ads (if available)
    public String AdShortText;
    @SerializedName("adh")                               // This is the ad height area (depends on calling device). This is the adimage height for native ads and the webview height for web ads.
    public int AdSpaceHeight;
    @SerializedName("adw")                               // This is the ad height area (depends on calling device). This is the adimage width for native ads and the webview width for web ads.
    public int AdSpaceWidth;
    @SerializedName("adclklink")                         // This is the link to use when a user clicks on a native ad item > opens up web browser.
    public String AdClickLink;
    @SerializedName("useadclklinkasapi")                 // If = 1, this means the click URL must be called as an API, and not as a redirection link! And catch the redirection link to redirect the user!
    public int IsAPIClickURL;
    @SerializedName("imppixurl")                         // This is the impression pixel URL to call from a 1 pixel web view for ad implementation if not empty.
    public String ImpressionPixelURL;     // INCLUDES SECURE LINK.
    // <html><body><img height="1" width="1" style="border-style:none;" alt="" src="//www.googleadservices.com/pagead/conversion/942176539/?label=7gK8COyO614Qm_KhwQM&amp;guid=ON&amp;script=0" /></body></html>
    @SerializedName("adweburl")                          // This is a web link for web ad SDK implementation, responsive design ad, for HTML ads only.
    public String AdWebURL;
    @SerializedName("clickopensbrowser")                 // This is a web link for web ad SDK implementation, responsive design ad, for HTML ads only.
    public int OpenBrowserOnClick;
    @SerializedName("customclosebtnurl")                 // This a a customizable close button URL for interstitial / web interstitial ads, use it if not empty!
    public String CustomCloseButtonURL;
    @SerializedName("customframeurl")                    // This a a customizable overlay URL for interstitial / web interstitial ads, use it if not empty!
    public String CustomFrameURL;
    @SerializedName("customframeh")                      // This a a customizable overlay URL for interstitial / web interstitial ads, use it if not empty! (FRAME HEIGHT)
    public int CustomFrameHeight;
    @SerializedName("customframew")                      // This a a customizable overlay URL for interstitial / web interstitial ads, use it if not empty! (FRAME WIDTH)
    public int CustomFrameWidth;
    @SerializedName("customframemargins")                // Frame margins: String: "0,0,0,0" - left,top,right,bottom
    public String CustomFrameMargins;
    @SerializedName("canbepreloaded")                    // Some ads (from RTBs, popup sites...) cannot be preloaded.
    public int CanBePreloaded;
    @SerializedName("htmlfbtag")                         // HTML Tag to check if a fallback is required.
    public String HTMLFBTag;
    @SerializedName("creativeid")                        // HTML Tag to check if a fallback is required.
    public int CreativeID;
}
