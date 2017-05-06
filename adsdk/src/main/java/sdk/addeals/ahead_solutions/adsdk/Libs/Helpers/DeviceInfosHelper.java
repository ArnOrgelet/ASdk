package sdk.addeals.ahead_solutions.adsdk.Libs.Helpers;

import android.content.Context;
import android.provider.Settings;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.security.AccessController.getContext;

/**
 * Created by ArnOr on 06/05/2017.
 */

public class DeviceInfosHelper {

    public static string GetDeviceID(Context context)
    {
        String did =
                Settings.Secure.getString(context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
        String hashed = StringHelper.Empty;
        try {
            byte[] bytesOfMessage = did.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] thedigest = md.digest(bytesOfMessage);
            new String(thedigest);
        }
        catch (UnsupportedEncodingException ex){}
        catch (NoSuchAlgorithmException ex){}

        String.toHexString();

        String hashedString = CryptographicBuffer.EncodeToHexString(hashed);
        return hashedString;
    }

        /*public static string GetOSVersion()
        {
            var t = new TaskCompletionSource<string>();
            var w = new WebView {AllowedScriptNotifyUris = WebView.AnyScriptNotifyUri};
            w.NavigateToString("<html />");
            NotifyEventHandler h = null;
            h = (s, e) =>
            {
                try
                {
                    var match = Regex.Match(e.Value, @"Windows\s+NT\s+\d+(\.\d+)?");
                    t.SetResult(match.Success ? match.Value : "Unknowm");
                }
                catch (Exception ex) { t.SetException(ex); }
                finally { /* release */ /*w.ScriptNotify -= h; }
            };
            w.ScriptNotify += h;
            w.InvokeScript("execScript", new[] { "window.external.notify(navigator.appVersion); " });
            return t.Task.Result;
        }*/

        [StructLayout(LayoutKind.Sequential)]
    public struct OsVersionInfoEx
    {
        public uint OSVersionInfoSize;
        public uint MajorVersion;
        public uint MinorVersion;
        public uint BuildNumber;
        public uint PlatformId;
        //[MarshalAs(UnmanagedType.ByValTStr, SizeConst = 128)]
        public string CSDVersion;
        public ushort ServicePackMajor;
        public ushort ServicePackMinor;
        public ushort SuiteMask;
        public byte ProductType;
        public byte Reserved;
    }


}

    /*public class VersionInfo
    {
        [DllImport("kernel32.dll")]
        static extern ulong VerSetConditionMask(ulong dwlConditionMask, uint dwTypeBitMask, byte dwConditionMask);
        [DllImport("kernel32.dll")]
        static extern bool VerifyVersionInfo([In] ref DeviceInfosHelper.OsVersionInfoEx lpVersionInfo, uint dwTypeMask, ulong dwlConditionMask);

        public bool IsWindowsVersionOrGreater(uint majorVersion, uint minorVersion, ushort servicePackMajor)
        {
            DeviceInfosHelper.OsVersionInfoEx osvi = new DeviceInfosHelper.OsVersionInfoEx();
            osvi.OSVersionInfoSize = (uint)Marshal.SizeOf(osvi);
            osvi.MajorVersion = majorVersion;
            osvi.MinorVersion = minorVersion;
            osvi.ServicePackMajor = servicePackMajor;
            // These constants initialized with corresponding definitions in
            // winnt.h (part of Windows SDK)
            const uint VER_MINORVERSION = 0x0000001;
            const uint VER_MAJORVERSION = 0x0000002;
            const uint VER_SERVICEPACKMAJOR = 0x0000020;
            const byte VER_GREATER_EQUAL = 3;
            ulong versionOrGreaterMask = VerSetConditionMask(
                                            VerSetConditionMask(
                                                VerSetConditionMask(0, VER_MAJORVERSION, VER_GREATER_EQUAL),
                                                VER_MINORVERSION, VER_GREATER_EQUAL),
                                                VER_SERVICEPACKMAJOR, VER_GREATER_EQUAL);

            const uint versionOrGreaterTypeMask = VER_MAJORVERSION | VER_MINORVERSION | VER_SERVICEPACKMAJOR;
            return VerifyVersionInfo(ref osvi, versionOrGreaterTypeMask, versionOrGreaterMask);
        }*/


        /*public Version SystemVersion
        {
            get
            {
                // Windows 8.1
                if (IsWindowsVersionOrGreater(6, 3, 0))
                {
                    return new Version(6, 3);
                }

                // Windows 8.0
                if (IsWindowsVersionOrGreater(6, 2, 0))
                {
                    return new Version(6, 2);
                }

                // Windows 7
                if (IsWindowsVersionOrGreater(6, 1, 0))
                {
                    return new Version(6, 1);
                }

                // Windows Vista
                if (IsWindowsVersionOrGreater(6, 0, 0))
                {
                    return new Version(6, 0);
                }

                // Windows XP
                if (IsWindowsVersionOrGreater(5, 1, 0))
                {
                    return new Version(5, 1);
                }

                throw new Exception("The current system is not supported");
            }
        }
    }*/
}
