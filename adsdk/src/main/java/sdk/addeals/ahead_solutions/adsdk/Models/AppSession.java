package sdk.addeals.ahead_solutions.adsdk.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ArnOr on 06/05/2017.
 */

public class AppSession {
    @SerializedName("id")
    public long ID;
    @SerializedName("cid")
    public int CID;
    @SerializedName("statusid")
    public int StatusID;
    @SerializedName("inapptitle")
    public String InAppTitle;
    @SerializedName("inapptext")
    public String InAppText;
    // Internal URI (1) to send user to...
    // Usually used on buttons
    @SerializedName("inappURI1")
    public String InAppURI1;
    // Internal URI (2) to send user to...
    // Usually used on buttons
    @SerializedName("inappURI2")
    public String InAppURI2;
    @SerializedName("inapppictURI")
    public String InAppPictURI;
    @SerializedName("inappvideoURI")
    public String InAppVideoURI;
    @SerializedName("inappwebURI")
    public String InAppWebURI;
    // For limited quantity offers. Default = -1
    @SerializedName("inappnbitemsleft")
    public int InAppNbItemsLeft;
    @SerializedName("inappdeadline")
    public String InAppDeadline;
    @SerializedName("respcode")
    public int ResponseCode;
    @SerializedName("respmsg")
    public String ResponseCodeMsg;
}
