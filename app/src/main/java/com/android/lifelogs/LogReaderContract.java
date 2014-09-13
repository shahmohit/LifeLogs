package com.android.lifelogs;

import android.provider.BaseColumns;

/**
 * Created by mohit on 9/12/14.
 */
public final class LogReaderContract {
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public LogReaderContract() {}

    /* Inner class that defines the table contents */
    public static abstract class LogEntry implements BaseColumns {
        public static final String TABLE_NAME = "dailylogs";
        public static final String COLUMN_NAME_ENTRY_ID = "entryid";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_LAT = "lat";
        public static final String COLUMN_NAME_LON = "lon";
        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + LogEntry.TABLE_NAME + " (" +
                        LogEntry._ID + " INTEGER PRIMARY KEY," +
                        LogEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                        LogEntry.COLUMN_NAME_DATE + TEXT_TYPE + COMMA_SEP +
                        LogEntry.COLUMN_NAME_TIME + TEXT_TYPE + COMMA_SEP +
                        LogEntry.COLUMN_NAME_LAT + TEXT_TYPE + COMMA_SEP +
                        LogEntry.COLUMN_NAME_LON + TEXT_TYPE +
                        " )";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + LogEntry.TABLE_NAME;

    }
}
