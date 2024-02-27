package com.app.gmv3.utilities;

import com.app.gmv3.Config;

public class Constant {

    //API Transactions
    private static final String BASE_URL = Config.ADMIN_PANEL_URL;
    private static final String API_COMMISIOENS= Config.API_COMMISIOENS;

    public static final String GET_RECENT_PRODUCT = BASE_URL + "/api/api.php?get_recent=";
    public static final String GET_BANNER = BASE_URL + "/api/api.php?get_banner";
    public static final String GET_NEWS = API_COMMISIOENS + "/api/Promociones";
    public static final String GET_PRODUCT_ID = BASE_URL + "/api/api.php?product_id=";
    public static final String GET_CATEGORY = BASE_URL + "/api/api.php?get_category";
    public static final String GET_CATEGORY_DETAIL = BASE_URL + "/api/api.php?category_id=";
    public static final String GET_CLIENTS = BASE_URL + "/api/api.php?clients_id=";
    public static final String GET_HISTORY_LOTE = BASE_URL + "/api/api.php?get_history_lotes=";

    public static final String GET_STAT = BASE_URL + "/api/api.php?get_stat_ruta=";
    public static final String GET_STAT_RECUP = BASE_URL + "/api/api.php?stat_recup=";
    public static final String GET_STAT_ARTI = BASE_URL + "/api/api.php?get_stat_articulo=";
    public static final String GET_COMENTARIOS = BASE_URL + "/api/api.php?get_comentarios=";

    public static final String GET_PROFIL_USER = BASE_URL + "/api/api.php?get_perfil_user=";
    public static final String PUSH_PIN = BASE_URL + "/api/api.php?push_pin=";
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
    public static final String POST_HISTORICO_FACTURA = BASE_URL + "/api/api.php?post_historico_factura=";




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

    //RUTAS QUE TIENE QUE VER CON LAS VIÑETAS
    public static final String GET_VINETA = BASE_URL + "/api/api.php?get_vineta=";
    public static final String POST_ORDER_VINETA = BASE_URL + "/api/api.php?post_order_vineta";
    public static final String GET_LIQUIDACION_VINETA = BASE_URL + "/api/api.php?get_liquidacion_vineta=";
    public static final String DEL_ORDER_VINETA = BASE_URL + "/api/api.php?del_order_vineta";

    //RUTAS QUE TIENE QUE VER CON RECIBOS COLECTOR
    public static final String POST_ORDER_RECIBO = BASE_URL + "/api/api.php?post_order_recibo";
    public static final String GET_RECIBOS_COLECTOR = BASE_URL + "/api/api.php?get_recibos_colector=";
    public static final String DEL_RECIBO_COLECTOR = BASE_URL + "/api/api.php?del_recibo_colector";
    public static final String POST_ANULAR_RECIBO = BASE_URL + "/api/api.php?recibo_anular=";

    // RECIENTEMENTE MODIFICADAS
    public static final String GET_PLAN_CRECIMIENTO = BASE_URL + "/api/api.php?PLAN=";
    public static final String GET_COMISION = API_COMMISIOENS + "/api/getcomision/";
    //public static final String POST_ADJUNTOS = BASE_URL + "/api/api.php?post_adjunto=";
    public static final String POST_ADJUNTOS = Config.URL_S3 + "/api/post_adjunto";
    public static final String POST_REPORT = API_COMMISIOENS + "/api/post_report";

    public static final String GET_COMENTARIOS_IM = API_COMMISIOENS + "/api/get_comentarios_im";
    public static final String GET_RECIBOS_ADJUNTO = API_COMMISIOENS + "/api/get_recibos_adjuntos";
    public static final String GET_ITEMS8020 = API_COMMISIOENS + "/api/getHistoryItems/";



}
