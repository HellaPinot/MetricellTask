package com.hellapinot.thomassmith.metricelltask;

import android.provider.BaseColumns;

public class DataContract {

    /*
    Holder for database column names and table name.
    */

    public static final String TABLE_NAME = "Log_Data";

    public static class Columns{

        public static final String _ID = BaseColumns._ID;
        public static final String SIGNAL_STRENGTH = "Signal_Strength";
        public static final String SERVICE_STATE = "Service_State";
        public static final String LOCATION = "Location";

    }
}

