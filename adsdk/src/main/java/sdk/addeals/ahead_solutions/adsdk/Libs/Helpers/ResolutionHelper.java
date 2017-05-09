package sdk.addeals.ahead_solutions.adsdk.Libs.Helpers;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by ArnOr on 06/05/2017.
 */

public class ResolutionHelper {

    public enum Resolutions { WVGA, WXGA, HD720p };

    private Context context;
    public ResolutionHelper(Context _context){
        context = _context;
    }
    public /*static*/ int ScreenWidth()
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE); //getWindowManager()
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        return (int)displayMetrics.widthPixels;
    }

    public /*static*/ int getScreenHeight()
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE); //getWindowManager()
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        return (int) displayMetrics.heightPixels;/*
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return (int) wm.getDefaultDisplay().getHeight();*/
    }

    private /*static*/ boolean getIsWvga()
    {
        return DisplayInformation.GetForCurrentView().ResolutionScale == ResolutionScale.Scale100Percent;
    }

    private /*static*/ boolean getIsWxga()
    {
        return DisplayInformation.GetForCurrentView().ResolutionScale == ResolutionScale.Scale160Percent;
    }

    private /*static*/ boolean getIs720p()
    {
        return DisplayInformation.GetForCurrentView().ResolutionScale == ResolutionScale.Scale150Percent;
    }

    public /*static*/ getResolutions CurrentResolution()
    {
        if (IsWvga) return Resolutions.WVGA;
        if (IsWxga) return Resolutions.WXGA;
        if (Is720p) return Resolutions.HD720p;
        throw new InvalidOperationException("Unknown resolution");
    }
}
