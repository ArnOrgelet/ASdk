package sdk.addeals.ahead_solutions.adsdk.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ArnOr on 06/05/2017.
 */

public class AppInstall {
    @SerializedName("respcode")
    public int ResponseCode;
    @SerializedName("respmsg")
    public String ResponseCodeMsg;
    @SerializedName("dlid")
    public long DownloadID;
    @SerializedName("clkid")
    public long ClickID;
    @SerializedName("usrid")
    public long userID;
}
