package org.kashmirworldfoundation.seaturtle;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by dwanna on 8/18/16.
 */
public class AppUtil {
    private static final String TAG = "***" + AppManager.class.getSimpleName();

    /**
     * date format used by server
     */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Get the difference between 2 date/time values down to millisecond
     * @param startDate
     * @param endDate
     * @param timeUnit
     * @return long - difference between dates based in timeUnit
     */
    public static long calcTimeDifference (Date startDate, Date endDate, TimeUnit timeUnit ) {




        long diffInMilliSec = endDate.getTime() - startDate.getTime();
        return timeUnit.convert(diffInMilliSec, timeUnit);
    }

    /**
     * Get the number of days elapsed using string representation of a date
     * The date has to be in the same format as DATE_FORMAT defined above
     *
     * @param startDateString
     * @return int - number of days elapsed or -1 if string passed is empty
     * @throws Exception
     */
    public static int calcNumDaysElapsed (String startDateString) throws Exception {

        long milliSecInDay = 1000*3600*24;

        // check if null or empty string is passed
        if (startDateString == null || startDateString.length() == 0) {
            return -1;
        }


        // convert string to date. If error then throw it back to calling function
        Date startDate = DATE_FORMAT.parse(startDateString);

        // set startDate to today
        Date endDate = new Date();

        Log.d(TAG, "END #" + DATE_FORMAT.format(endDate.getTime()) + "#, Start #" + DATE_FORMAT.format(startDate.getTime()) + "#");

        int numDaysElapsed =
                (int) (endDate.getTime() / milliSecInDay) - (int) (startDate.getTime() / milliSecInDay);

        return numDaysElapsed;
    }

    public static float colorToHSV(Context context, int colorResourceId) {

        int color = context.getResources().getColor(colorResourceId);

        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        float[] hsv = new float[3];

        Color.RGBToHSV(r, g, b, hsv);

        return hsv[0];
    }

}
