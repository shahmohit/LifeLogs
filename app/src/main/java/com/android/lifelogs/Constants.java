package com.android.lifelogs;

/**
 * Created by mohit on 9/12/14.
 */
public class Constants {
    public static final String AUDIO_TYPE = "audio";
    public static final String IMAGE_TYPE = "image";
    public static final String VIDEO_TYPE = "video";
    public static final String BROADCAST_ACTION = "service.LOCATION";
    public static final String PLAT_DATA = "service.PLATITUDE";
    public static final String PLON_DATA = "service.PLONGITUDE";
    public static final String PDATE_DATA = "service.PDATE";
    public static final String PTIME_DATA = "service.PTIME";
    public static final boolean PERIODIC_LOCATIONS = true;
    public static final String IntervalKey = "interval_preference";
    public static final String[] Intervals = {"Every minute",
            "Every 5 minutes",
            "Every 15 minutes",
            "Every 30 minutes",
            "Every 45 minutes",
            "Every hour",
            "Every 2 hours",
            "Every 4 hours",
            "Every 8 hours",
            "Every 16 hours",
            "Every day"};
    public static final int[] IntIntervals = {60, 60 * 5, 60 * 15,
            60 * 30, 60 * 45, 60 * 60,
            60 * 60 * 2, 60 * 60 * 4, 60 * 60 * 8,
            60 * 60 * 16, 60 * 60 * 24};
}
