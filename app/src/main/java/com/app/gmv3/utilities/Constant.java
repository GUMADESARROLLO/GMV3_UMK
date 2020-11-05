package com.app.gmv3.utilities;

import com.app.gmv3.Config;

public class Constant {

    //API Transactions
    private static final String BASE_URL = Config.ADMIN_PANEL_URL;
    public static final String GET_RECENT_PRODUCT = BASE_URL + "/api/api.php?get_recent";
    public static final String GET_PRODUCT_ID = BASE_URL + "/api/api.php?product_id=";
    public static final String GET_CATEGORY = BASE_URL + "/api/api.php?get_category";
    public static final String GET_CATEGORY_DETAIL = BASE_URL + "/api/api.php?category_id=";
    public static final String GET_CLIENTS = BASE_URL + "/api/api.php?clients_id=";
    public static final String GET_STAT = BASE_URL + "/api/api.php?get_stat_ruta=";
    public static final String GET_STAT_ARTI = BASE_URL + "/api/api.php?get_stat_articulo=";
    public static final String GET_COMENTARIOS = BASE_URL + "/api/api.php?get_comentarios=";

    public static final String GET_PROFIL_USER = BASE_URL + "/api/api.php?get_perfil_user=";
    public static final String GET_DETALLE_FACTURA = BASE_URL + "/api/api.php?get_detalle_factura=";
    public static final String GET_NC = BASE_URL + "/api/api.php?get_nc=";
    public static final String GET_LAST_3M = BASE_URL + "/api/api.php?last_3m=";
    public static final String GET_NO_FACTURADO = BASE_URL + "/api/api.php?articulos_sin_facturar=";
    public static final String GET_HELP = BASE_URL + "/api/api.php?get_help";
    public static final String GET_TAX_CURRENCY = BASE_URL + "/api/api.php?get_tax_currency";
    public static final String GET_SHIPPING = BASE_URL + "/api/api.php?get_shipping";

    public static final String POST_ORDER = BASE_URL + "/api/api.php?post_order";
    public static final String POST_VERIFICACION = BASE_URL + "/api/api.php?post_verificacion";
    public static final String POST_RPT_RUTA = BASE_URL + "/api/api.php?post_rpt_ruta=";

    public static final String POST_REPORT = BASE_URL + "/api/api.php?post_report=";
    public static final String GET_COMENTARIOS_IM = BASE_URL + "/api/api.php?get_comentarios_im=";

    public static final String POST_UDAPTE_DATOS = BASE_URL + "/api/api.php?post_update_datos";

    public static final String NORMAL_LOGIN_URL = BASE_URL + "/api/api.php?post_usuario=";
    public static int GET_SUCCESS_MSG;
    public static final String CATEGORY_ARRAY_NAME = "result";
    public static final int DELAY_PROGRESS_DIALOG = 2000;
    public static final String USER_NAME = "name";
    public static final String USER_FULL_NAME = "FullName";
    public static final String USER_TELEFONO = "Tele";
    public static final String USER_EMAIL = "Correo";
    public static final String SUCCESS = "success";
    public static final String MSG = "msg";

    public static int permission_Read_data = 789;

    public static final String BROADCAST_ACTION = "broadcast-action";
    public static final String ACTIVITY_KEY = "activites-key";
    public static final long ACTIVITY_RECOGNITION_INTERVAL = 0;
    public static final long UPDATE_INTERVAL = 1000;
    public static final long UPDATE_FASTEST_INTERVAL = UPDATE_INTERVAL / 2;



}
