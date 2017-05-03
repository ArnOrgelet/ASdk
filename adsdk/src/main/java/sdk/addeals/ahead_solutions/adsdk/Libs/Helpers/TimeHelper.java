package sdk.addeals.ahead_solutions.adsdk.Libs.Helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by ArnOr on 02/05/2017.
 */

public class TimeHelper {
    static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";

    public static Date getUTC()
    {
        //note: doesn't check for null
        return stringToDate(getUTCstring());
    }

    public static String getUTCstring()
    {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());

        return utcTime;
    }

    public static Date stringToDate(String StrDate)
    {
        Date dateToReturn = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);

        try
        {
            dateToReturn = (Date)dateFormat.parse(StrDate);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return dateToReturn;
    }
}
