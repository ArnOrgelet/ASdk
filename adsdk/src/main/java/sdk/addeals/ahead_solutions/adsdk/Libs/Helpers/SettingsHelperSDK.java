package sdk.addeals.ahead_solutions.adsdk.Libs.Helpers;

import android.app.Activity;

import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ArnOr on 06/05/2017.
 */

public class SettingsHelperSDK {
    PreferencesHandler _prefsHandler;
    public SettingsHelperSDK(Activity activity){
        _prefsHandler = new PreferencesHandler(activity);
    }

    // Initializes application settings...
    public void initSettings()
    {
        // Contains user sessions info (password, username, and user notifications preferences...)
        //UserSettings userSettings = new UserSettings();
        //this.SetSettingKey(USER_SETTINGS, userSettings);
        //userSettings.DeviceID = deviceID;
        //this.SetSettingKey(OFFLINE_DATA, new OfflineData());
        //byte[] value = (byte[])DeviceExtendedProperties.GetValue("DeviceUniqueId");
        String deviceID = DeviceInfosHelper.getDeviceID(_prefsHandler.getActivity());
        Map<String, Object> initSettings = new HashMap<String, Object>();

        //this.SetSettingKey(AbstractSettingsHelperSDK.FIRST_LAUNCH, true);
        initSettings.put(AbstractSettingsHelperSDK.AS20082013DATE_LAST_LAUNCH, DateTime.now().ToFileTimeUtc());
        initSettings.put(AbstractSettingsHelperSDK.AS20082013NUMBER_OF_LAUNCHES, 0);
        initSettings.put(AbstractSettingsHelperSDK.AS20082013INSTALL_NOTIFIED, false);
        initSettings.put(AbstractSettingsHelperSDK.ADDEALS20150915DOWNLOAD_ID, -1);
        initSettings.put(AbstractSettingsHelperSDK.ADDEALS20150915CLICK_ID, -1);
        initSettings.put(AbstractSettingsHelperSDK.ADDEALS20150915USR_ID, -1);
        _prefsHandler.storePreference(initSettings);
                /*
        //this.SetSettingKey(AbstractSettingsHelperSDK.FIRST_LAUNCH, true);
        _prefsHandler.storePreference(AbstractSettingsHelperSDK.AS20082013DATE_LAST_LAUNCH, DateTime.now().ToFileTimeUtc());
        _prefsHandler.storePreference(AbstractSettingsHelperSDK.AS20082013NUMBER_OF_LAUNCHES, 0);
        _prefsHandler.storePreference(AbstractSettingsHelperSDK.AS20082013INSTALL_NOTIFIED, false);
        _prefsHandler.storePreference(AbstractSettingsHelperSDK.ADDEALS20150915DOWNLOAD_ID, -1);
        _prefsHandler.storePreference(AbstractSettingsHelperSDK.ADDEALS20150915CLICK_ID, -1);
        _prefsHandler.storePreference(AbstractSettingsHelperSDK.ADDEALS20150915USR_ID, -1);
*/
        // Force Culture...
        //Localization.Culture = Thread.CurrentThread.CurrentUICulture;
    }

    public void setSettingKey(Map<String,Object> mappings)
    {
        _prefsHandler.storePreference(mappings);
    }

    public void setSettingKey(String key, Object value)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(key, value);
        setSettingKey(map);
    }

    public <T> T getSettingKey(String key, Class<T> type)
    {
        return _prefsHandler.getPreference(key, type);
    }
}
