package sdk.addeals.ahead_solutions.adsdk.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import sdk.addeals.ahead_solutions.adsdk.ViewModels.AdDealsWallViewModel;

/**
 * Created by ArnOr on 09/05/2017.
 */

public class AdDealsWall extends EventHandingView<AdDealsWallViewModel> {
    public AdDealsWall(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public AdDealsWall(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public AdDealsWall(Context context) {
        super(context);
    }
}
