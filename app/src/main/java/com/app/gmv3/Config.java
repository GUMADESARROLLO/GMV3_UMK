package com.app.gmv3;

public class Config {
    //PRODUCCION
    //public static final String ADMIN_PANEL_URL = "http://192.168.1.139/GMV_B";
    //public static final String API_COMMISIOENS = "http://192.168.1.139/SAC";

    //public static final String SERVER = "186.1.15.166:83";
    public static final String SERVER = "192.168.1.139";
    public static final String ADMIN_PANEL_URL = "http://" + SERVER + "/GMV_B";
    public static final String API_COMMISIOENS = "http://" + SERVER + "/SAC";

    public static final String URL_S3 = API_COMMISIOENS;

    //set false if you want price to be displayed in decimal
    public static final boolean ENABLE_DECIMAL_ROUNDING = false;

    //set true if you want to enable RTL (Right To Left) mode, e.g : Arabic Language
    public static final boolean ENABLE_RTL_MODE = false;

    //splash screen duration in milliseconds
    public static final int SPLASH_TIME = 3000;

}