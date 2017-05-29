package sdk.addeals.ahead_solutions.adsdk.ViewModels;

import android.app.Activity;
import android.view.Window;

import java.net.URI;
import java.net.URISyntaxException;

import sdk.addeals.ahead_solutions.adsdk.AdManager;
import sdk.addeals.ahead_solutions.adsdk.Libs.Helpers.DeviceSettingsHelper;
import sdk.addeals.ahead_solutions.adsdk.Libs.Helpers.ResolutionHelper;
import sdk.addeals.ahead_solutions.adsdk.Views.AdDealsWall;

/**
 * Created by ArnOr on 07/05/2017.
 */

public class AdDealsWallViewModel extends ViewModelBase {
    AdDealsWall adDealsWallView;
    public AdDealsWallViewModel()
    {
    }
    public AdDealsWallViewModel(AdDealsWall _adDealsWallView)
    {
        this.adDealsWallView = _adDealsWallView;
    }

    protected void refreshScreenSize()
    {
        Activity activity = ((Activity) adDealsWallView.getContext());/*.getResources().getDisplayMetrics();*/)
        this.setAppHeight((int)(activity.getWindow().getDecorView().getHeight()));
        this.setAppWidth((int)(activity.getWindow().getDecorView().getWidth()));
        this._progressRingMargin = ((double)_appWidth - 90.0) / 2.0 + "," + ((double)_appHeight - 90.0) / 2.0 + ","
                + ((double)_appWidth - 90.0) / 2.0 + "," + ((double)_appHeight - 90.0) / 2.0;
    }

    protected void updateModel()
    {
        this.refreshScreenSize();
        try{
            this._webViewAdSrc = new URI(AdManager.getWallWebLink());
        }
        catch(URISyntaxException ex){}
        this._closingButton = DEFAULT_CLOSE_BUTTON_URI;
    }
}
