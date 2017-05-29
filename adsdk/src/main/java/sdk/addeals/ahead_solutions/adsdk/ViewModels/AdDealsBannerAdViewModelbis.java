package sdk.addeals.ahead_solutions.adsdk.ViewModels;

import android.databinding.Bindable;
import android.opengl.Visibility;
import android.view.View;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Future;

import sdk.addeals.ahead_solutions.adsdk.AdManager;
import sdk.addeals.ahead_solutions.adsdk.BR;
import sdk.addeals.ahead_solutions.adsdk.Models.Campaign;
import sdk.addeals.ahead_solutions.adsdk.Models.CampaignsV3;

/**
 * Created by ArnOr on 08/05/2017.
 */

public class AdDealsBannerAdViewModel extends ViewModelBase {
    // By default, standard refresh rate is 60sec.
    /*internal*/public int CounterWithoutAd = 0;
    /*internal*/protected long ServerRefreshRate = BANNER_REFRESH_RATE_DEFAULT;

    public AdDealsBannerAdViewModel()
    {
        this._webViewVisibility = View.GONE;
    }

    /*internal*/public void refreshScreenSize(AdManager.BannerAdSizes adType)
    {
        switch (adType)
        {
                /*case BANNER_WINDOWS_PHONE_AUTOSCALE_PORTRAIT:
                    {
                        if ((int)(Window.Current.Bounds.Width) <= 320)
                        {
                            setAdWidth(320);
                            setAdHeight(50);
                        }
                        else
                        {
                            setAdWidth(728);
                            setAdHeight(90);
                        }
                        setAdSpaceWidth = (int)(Window.Current.Bounds.Width);
                        setAdSpaceHeight = (int)(((decimal)(Window.Current.Bounds.Width) / (decimal)AdWidth) * (decimal)AdHeight);
                        break;
                    }*/
            case BANNER_WINDOWS_PHONE_320x50:
            {
                setAdWidth(320);
                setAdHeight(50);
                setAdSpaceWidth(_adWidth);
                setAdSpaceHeight(_adHeight);
                break;
            }
            case LEADERBOARD_WINDOWS_TABLET_PC_728x90:
            {
                setAdWidth(728);
                setAdHeight(90);
                setAdSpaceWidth(_adWidth);
                setAdSpaceHeight(_adHeight);
                break;
            }
            case MEDIUM_RECTANGLE_WINDOWS_TABLET_PC_300x250:
            {   setAdWidth(300);
                setAdHeight(250);
                setAdSpaceWidth(_adWidth);
                setAdSpaceHeight(_adHeight);
                break;
            }
                /*case SQUARE_WINDOWS_PHONE_173x173:
                    {
                        AdWidth = 250;
                        AdHeight = 250;
                        AdSpaceWidth = 173;
                        AdSpaceHeight = 173;
                        break;
                    }*/
            case SQUARE_WINDOWS_TABLET_PC_250x250:
            {
                setAdWidth(250);
                setAdHeight(250);
                setAdSpaceWidth(_adWidth);
                setAdSpaceHeight(_adHeight);
                break;
            }
            case WIDE_SKYSCRAPER_WINDOWS_TABLET_PC_160x600:
            {
                setAdWidth(160);
                setAdHeight(600);
                setAdSpaceWidth(_adWidth);
                setAdSpaceHeight(_adHeight);
                break;
            }
        }
    }

    /// <summary>
    /// Get new banner ad...
    /// </summary>
    /// <returns></returns>
    public /*internal async*/ int getNewAd(boolean strictSize)
    {
        String requestedAdTypes = AdManager.BANNER_SUPPORTED_ADS; // All by default.

        if (AdManager.SDKinitialized)
        {
            try
            {
                CampaignsV3 campaigns = this.getCampaignAd(requestedAdTypes, (int)this._adWidth, (int)this._adHeight, AdManager.CAMPAIGN_TYPE_BANNER, strictSize).get();
                if (campaigns.ResponseCode == 200)
                {
                    if (campaigns.AdRefreshRate > 0)
                    {
                        this.ServerRefreshRate = campaigns.AdRefreshRate;
                    }

                    if ((campaigns != null && campaigns.Offers.size() == 0) || campaigns == null)
                    {
                        CounterWithoutAd++;
                        return AdManager.NO_AD_AVAILABLE;
                    }

                    this._campaigns = campaigns;
                    this.updateModel(0);
                    CounterWithoutAd = 0;

                    // Always return Ad availability unknown until the ad is displayed because filler campaigns can have no ad and fallbacks.
                    return AdManager.AD_AVAILABILITY_UNKNOWN;
                }
                else
                {
                    return AdManager.ERROR_ACCESS_DENIED;
                }
            }
            catch (Exception ex)
            {
                return AdManager.NO_AD_AVAILABLE;
            }
        }
        else {
            return AdManager.ERROR_SDK_NOT_INITIALIZED;
        }
    }

    /*internal*/ void updateModel(/*List<Campaign> campaigns*/ int campIndex)
    {
        if (this._campaigns.Offers.size() > campIndex)
        {
            this._adLoaded = false;
            this._adWasClicked = false; // An ad click can only be counted & sent max once / ad displayed!
            this.lastCampaignIndex = campIndex;
            this._impressionPixelSrc = null;
            this._webViewAdSrc = null;
            Campaign myCampaign = this._campaigns.Offers.get(campIndex);
            this.setCampaignLinkURL(myCampaign.AdClickLink);
            this.setAdHeight(myCampaign.AdSpaceHeight);
            this.setAdWidth(myCampaign.AdSpaceWidth);
            this.setCampaignLinkURL(myCampaign.AdClickLink);
            try {
                this.setWebViewAdSrc(new URI(this.buildAdWebURL(myCampaign.AdWebURL, (int) this._adWidth, (int) this._adHeight)));
            }// All banner ads come from Web URLs (AdDeals ad server).
            catch(URISyntaxException ex){}
            this.ImpressionPixelSrcTmp = this.BuildAdDisplayPixelURL(myCampaign.ImpressionPixelURL, (int)this._adWidth, (int)this._adHeight);       // Stores temporarily until we get the display (loaded.addealsnetwork.com)
            if (myCampaign.OpenBrowserOnClick == 1) this.setClickOpensBrowser(true);
            else this.setClickOpensBrowser(false);
            if (myCampaign.IsAPIClickURL == 1) this.setIsAPIClickURL(true);
            else this.setIsAPIClickURL(false);
            this.setWebViewVisibility(View.VISIBLE);
            this.setHTMLFBTag(myCampaign.HTMLFBTag.trim());
        }
    }


    //region properties

    private double _adHeight = 0;
    public double getAdHeight()
    {
        return this._adHeight;
    }

    public void setAdHeight(double value)
    {
        if (this._adHeight != value)
        {
            this._adHeight = value;
            //notifyPropertyChanged(BR.adSpaceHeight);
            this.OnPropertyChanged("AdHeight");
        }
    }

    private double _adWidth = 0;
    public double getAdWidth()
    {
        return this._adWidth;
    }

    public void setAdWidth(double value)
    {
        if (this._adWidth != value)
        {
            this._adWidth = value;
            //notifyPropertyChanged(BR.adWidth);
            this.OnPropertyChanged("AdWidth");
        }
    }

    private double _adSpaceHeight = 0;
    public double getAdSpaceHeight()
    {
        return this._adSpaceHeight;
    }

    public void setAdSpaceHeight(double value)
    {
        if (this._adSpaceHeight != value)
        {
            this._adSpaceHeight = value;
            //notifyPropertyChanged(BR.adSpaceHeight);
            this.OnPropertyChanged("AdSpaceHeight");
        }
    }

    private double _adSpaceWidth = 0;
    public double getAdSpaceWidth()
    {
        return this._adSpaceWidth;
    }

    public void setAdSpaceWidth(double value)
    {
        if (this._adSpaceWidth != value)
        {
            this._adSpaceWidth = value;
            //notifyPropertyChanged(BR.adSpaceWidth);
            this.OnPropertyChanged("AdSpaceWidth");
        }
    }

        //endregion
}
