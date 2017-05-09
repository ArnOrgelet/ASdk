package sdk.addeals.ahead_solutions.adsdk.ViewModels;

import android.view.Window;

import java.net.URI;

import sdk.addeals.ahead_solutions.adsdk.AdManager;
import sdk.addeals.ahead_solutions.adsdk.Libs.Helpers.DeviceSettingsHelper;
import sdk.addeals.ahead_solutions.adsdk.Libs.Helpers.ResolutionHelper;

/**
 * Created by ArnOr on 07/05/2017.
 */

public class AdDealsWallViewModel extends ViewModelBase {
    protected AdDealsWallViewModel()
    {
    }

    protected void RefreshScreenSize()
    {
        this._appHeight = (int)(ResolutionHelper.f.Bounds.Height);
        this._appWidth = (int)(Window.Current.Bounds.Width);
        this._progressRingMargin = ((double)_appWidth - 90.0) / 2.0 + "," + ((double)_appHeight - 90.0) / 2.0 + ","
                + ((double)_appWidth - 90.0) / 2.0 + "," + ((double)_appHeight - 90.0) / 2.0;
    }

    protected void UpdateModel()
    {
        this.RefreshScreenSize();
        this._webViewAdSrc = new URI(AdManager.GetWallWebLink());
        this._closingButton = DEFAULT_CLOSE_BUTTON_URI;
    }
}
