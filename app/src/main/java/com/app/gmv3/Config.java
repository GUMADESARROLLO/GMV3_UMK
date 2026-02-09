package com.app.gmv3;

public class Config {
    //DEVELOPER
//    public static final String ADMIN_PANEL_URL = "http://192.168.1.139/GMV_B";
//    public static final String API_COMMISIOENS = "http://192.168.1.139/SAC";


    //PRODUCCTION
    public static final String SERVER = "apps";
    public static final String ADMIN_PANEL_URL = "https://" + SERVER + ".gumacorp.com/GMV";
    public static final String API_COMMISIOENS = "https://" + SERVER + ".gumacorp.com/SAC";

    public static final String URL_S3 = API_COMMISIOENS;

    //set false if you want price to be displayed in decimal
    public static final boolean ENABLE_DECIMAL_ROUNDING = false;

    //set true if you want to enable RTL (Right To Left) mode, e.g : Arabic Language
    public static final boolean ENABLE_RTL_MODE = false;

    //splash screen duration in milliseconds
    public static final int SPLASH_TIME = 3000;

}