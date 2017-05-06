package sdk.addeals.ahead_solutions.adsdk.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ArnOr on 06/05/2017.
 */

public class CampaignsV3
{
    @SerializedName("respcode")
    public int ResponseCode;
    @SerializedName("respmsg")
    public String ResponseCodeMsg;
    @SerializedName("refreshrate")
    public int AdRefreshRate;
    @SerializedName("offers")
    public List<Campaign> Offers;
}