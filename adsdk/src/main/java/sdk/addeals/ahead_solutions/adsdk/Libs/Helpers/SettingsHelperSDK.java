package sdk.addeals.ahead_solutions.adsdk.Libs.Helpers;

/**
 * Created by ArnOr on 06/05/2017.
 */

public class SettingsHelperSDK {
    // Initializes application settings...
    public void initSettings()
    {
        // Contains user sessions info (password, username, and user notifications preferences...)
        //UserSettings userSettings = new UserSettings();
        //this.SetSettingKey(USER_SETTINGS, userSettings);
        //userSettings.DeviceID = deviceID;
        //this.SetSettingKey(OFFLINE_DATA, new OfflineData());
        //byte[] value = (byte[])DeviceExtendedProperties.GetValue("DeviceUniqueId");
        String deviceID = DeviceInfosHelper.getDeviceID();
        //this.SetSettingKey(FIRST_LAUNCH, true);
        this.SetSettingKey(AS20082013DATE_LAST_LAUNCH, DateTime.UtcNow.ToFileTimeUtc());
        this.SetSettingKey(AS20082013NUMBER_OF_LAUNCHES, 0);
        this.SetSettingKey(AS20082013INSTALL_NOTIFIED, false);
        this.SetSettingKey(ADDEALS20150915DOWNLOAD_ID, -1);
        this.SetSettingKey(ADDEALS20150915CLICK_ID, -1);
        this.SetSettingKey(ADDEALS20150915USR_ID, -1);

        // Force Culture...
        //Localization.Culture = Thread.CurrentThread.CurrentUICulture;
    }

    private void SetSettingKey(string key, object value)
    {
        if (Windows.Storage.ApplicationData.Current.LocalSettings.Values[key] == null)
        {
            Windows.Storage.ApplicationData.Current.LocalSettings.Values[key] = value;
        }
    }
}
