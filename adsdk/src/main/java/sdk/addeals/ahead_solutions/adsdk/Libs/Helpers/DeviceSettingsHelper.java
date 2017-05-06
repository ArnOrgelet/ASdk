package sdk.addeals.ahead_solutions.adsdk.Libs.Helpers;

import java.util.Locale;

/**
 * Created by ArnOr on 06/05/2017.
 */

public class DeviceSettingsHelper {
    public static String getLanguage() //English
    {
        String language = Locale.getDefault().getDisplayLanguage();
        return language;
    }
    public static String getLanguageCode() //en
    {
        String language = Locale.getDefault().getLanguage();
        return language;
    }
    public static String getLanguageISOCode() //eng
    {
        String language = Locale.getDefault().getISO3Language();
        return language;
    }

    public static String getCountry() //United States
    {
        String language = Locale.getDefault().getDisplayCountry();
        return language;
    }
    public static String getCountryCode() //US
    {
        String language = Locale.getDefault().getCountry();
        return language;
    }
    public static String getCountryISOCode() //USA
    {
        String language = Locale.getDefault().getISO3Country();
        return language;
    }
}
