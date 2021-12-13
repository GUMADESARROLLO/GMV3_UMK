package com.app.gmv3.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private final static String DB_NAME         = "gmv3_db";
    public final static int DB_VERSION          = 5;
    public static SQLiteDatabase db;
    private final Context context;
    private String DB_PATH;

    private final String TABLE_CART             = "tbl_cart";
    private final String CART_ID                = "id";
    private final String PRODUCT_NAME           = "product_name";
    private final String QUANTITY               = "quantity";
    private final String TOTAL_PRICE            = "total_price";
    private final String CURRENCY_CODE          = "currency_code";
    private final String PRODUCT_IMAGE          = "product_image";
    private final String PRODUCT_BONIFICADO     = "product_bonificado";

    private final String TABLE_VINNE            = "tbl_vinne";
    private final String FACTURA                = "factura";
    private final String COD_PRODUCT            = "cod_product";
    private final String DESCRIPCION            = "descripcion";
    private final String CODE_VINNE             = "code_vinne";
    private final String CANT_VINNE             = "cant_vinne";
    private final String VALOR_UND_VINNE        = "valor_und_vinne";
    private final String VALOR_TOT_VINNE        = "valor_tot_vinne";
    private final String FACTURA_LINEA          = "linea";
    private final String TABLE_ID               = "id";
    private final String COD_CLIENTE            = "cliente";

    private final String TABLE_HISTORY          = "tbl_history";
    private final String HISTORY_ID             = "id";
    private final String CODE                   = "code";
    private final String ORDER_LIST             = "order_list";
    private final String ORDER_TOTAL            = "order_total";
    private final String DATE_TIME              = "date_time";
    private final String NAME_CLIENT            = "name_client";

    private final String TABLE_RECIBOS          = "tbl_recibos";
    private final String REC_FACTURA            = "Factura";
    private final String VALOR_FACTURA          = "Valor_Factura";
    private final String NOTA_CREDITO           = "Nota_Credito";
    private final String RETENCION              = "Retencion";
    private final String DESCUENTO              = "Descuento";
    private final String REC_VALOR              = "Valor_Recibido";
    private final String SALDO                  = "Saldo";
    private final String REC_ID                 = "id";
    private final String REC_CLIENTE            = "cliente";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        DB_PATH = "/data/data/" + context.getPackageName() + "/" + "databases/";
    }

    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        SQLiteDatabase db_Read = null;
        if (dbExist) {
        } else {
            db_Read = this.getReadableDatabase();
            db_Read.close();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();

    }

    private void copyDataBase() throws IOException {
        InputStream myInput = context.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public void close() {
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<ArrayList<Object>> getAllDataVinete(String id) {
        ArrayList<ArrayList<Object>> dataArrays = new ArrayList<ArrayList<Object>>();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_VINNE, new String[]{FACTURA, COD_PRODUCT,DESCRIPCION, CODE_VINNE, CANT_VINNE, VALOR_UND_VINNE,VALOR_TOT_VINNE,TABLE_ID,FACTURA_LINEA},
                    COD_CLIENTE + "=?" , new String[]{id}, null, null, null);
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                do {
                    ArrayList<Object> dataList = new ArrayList<Object>();
                    dataList.add(cursor.getString(0));
                    dataList.add(cursor.getString(1));
                    dataList.add(cursor.getString(2));
                    dataList.add(cursor.getString(3));
                    dataList.add(cursor.getString(4));
                    dataList.add(cursor.getString(5));
                    dataList.add(cursor.getString(6));
                    dataList.add(cursor.getString(7));
                    dataList.add(cursor.getString(8));
                    dataArrays.add(dataList);
                }
                while (cursor.moveToNext());
            }
            cursor.close();
        } catch (SQLException e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }
        return dataArrays;
    }
    public ArrayList<ArrayList<Object>> getAllDataRecibo(String id) {
        ArrayList<ArrayList<Object>> dataArrays = new ArrayList<ArrayList<Object>>();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_RECIBOS, new String[]{REC_FACTURA, VALOR_FACTURA, NOTA_CREDITO,RETENCION, DESCUENTO, REC_VALOR, SALDO,REC_ID,REC_CLIENTE},
                    REC_CLIENTE + "=?" , new String[]{id}, null, null, null);
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                do {
                    ArrayList<Object> dataList = new ArrayList<Object>();
                    dataList.add(cursor.getString(0));
                    dataList.add(cursor.getString(1));
                    dataList.add(cursor.getString(2));
                    dataList.add(cursor.getString(3));
                    dataList.add(cursor.getString(4));
                    dataList.add(cursor.getString(5));
                    dataList.add(cursor.getString(6));
                    dataList.add(cursor.getString(7));
                    dataList.add(cursor.getString(8));
                    dataArrays.add(dataList);
                }
                while (cursor.moveToNext());
            }
            cursor.close();
        } catch (SQLException e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }
        return dataArrays;
    }

    public ArrayList<ArrayList<Object>> getAllData() {
        ArrayList<ArrayList<Object>> dataArrays = new ArrayList<ArrayList<Object>>();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_CART, new String[]{CART_ID, PRODUCT_NAME, QUANTITY,PRODUCT_BONIFICADO, TOTAL_PRICE, CURRENCY_CODE, PRODUCT_IMAGE},
                    null, null, null, null, null);
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                do {
                    ArrayList<Object> dataList = new ArrayList<Object>();
                    dataList.add(cursor.getString(0));
                    dataList.add(cursor.getString(1));
                    dataList.add(cursor.getString(2));
                    dataList.add(cursor.getString(3));
                    dataList.add(cursor.getString(4));
                    dataList.add(cursor.getString(5));
                    dataList.add(cursor.getString(6));
                    dataArrays.add(dataList);
                }
                while (cursor.moveToNext());
            }
            cursor.close();
        } catch (SQLException e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }
        return dataArrays;
    }

    public ArrayList<ArrayList<Object>> getAllRecibos() {
        ArrayList<ArrayList<Object>> dataArrays = new ArrayList<ArrayList<Object>>();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_RECIBOS, new String[]{REC_FACTURA, VALOR_FACTURA, NOTA_CREDITO,RETENCION, DESCUENTO, REC_VALOR, SALDO,REC_ID,REC_CLIENTE},
                    null, null, null, null, null);
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                do {
                    ArrayList<Object> dataList = new ArrayList<Object>();
                    dataList.add(cursor.getString(0));
                    dataList.add(cursor.getString(1));
                    dataList.add(cursor.getString(2));
                    dataList.add(cursor.getString(3));
                    dataList.add(cursor.getString(4));
                    dataList.add(cursor.getString(5));
                    dataList.add(cursor.getString(6));
                    dataList.add(cursor.getString(7));
                    dataList.add(cursor.getString(8));
                    dataArrays.add(dataList);
                }
                while (cursor.moveToNext());
            }
            cursor.close();
        } catch (SQLException e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }
        return dataArrays;
    }

    public ArrayList<ArrayList<Object>> getAllDataHistory() {
        ArrayList<ArrayList<Object>> dataArrays = new ArrayList<ArrayList<Object>>();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_HISTORY, new String[]{HISTORY_ID, CODE, ORDER_LIST, ORDER_TOTAL, DATE_TIME,NAME_CLIENT},
                    null, null, null, null, "id DESC");
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                do {
                    ArrayList<Object> dataList = new ArrayList<Object>();
                    dataList.add(cursor.getLong(0));
                    dataList.add(cursor.getString(1));
                    dataList.add(cursor.getString(2));
                    dataList.add(cursor.getString(3));
                    dataList.add(cursor.getString(4));
                    dataList.add(cursor.getString(5));
                    dataArrays.add(dataList);
                }
                while (cursor.moveToNext());
            }
            cursor.close();
        } catch (SQLException e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }
        return dataArrays;
    }

    public boolean isDataExist(String id) {
        boolean exist = false;
        Cursor cursor = null;
        try {
           // cursor = db.query(TABLE_CART, new String[]{CART_ID}, CART_ID + "=?" + new String[]{id}, null, null, null, null);
            cursor = db.query(TABLE_CART,
                    new String[]{CART_ID},
                    CART_ID + "=?",
                    new String[]{id}, null, null, null, null);

            if (cursor.getCount() > 0) {
                exist = true;
            }
            cursor.close();
        } catch (SQLException e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }
        return exist;
    }

    public boolean isDataHistoryExist(long id) {
        boolean exist = false;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_HISTORY, new String[]{HISTORY_ID}, HISTORY_ID + "=" + id, null, null, null, null);
            if (cursor.getCount() > 0) {
                exist = true;
            }
            cursor.close();
        } catch (SQLException e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }
        return exist;
    }

    public boolean isPreviousDataExist() {
        boolean exist = false;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_CART, new String[]{CART_ID}, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                exist = true;
            }
            cursor.close();
        } catch (SQLException e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }

        return exist;
    }

    public void addData(String id, String product_name, int quantity, double total_price, String currency_code, String product_image,String product_bonificado) {
        ContentValues values = new ContentValues();
        values.put(CART_ID, id);
        values.put(PRODUCT_NAME, product_name);
        values.put(QUANTITY, quantity);
        values.put(PRODUCT_BONIFICADO, product_bonificado);
        values.put(TOTAL_PRICE, total_price);
        values.put(CURRENCY_CODE, currency_code);
        values.put(PRODUCT_IMAGE, product_image);
        try {
            db.insert(TABLE_CART, null, values);
        } catch (Exception e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
    }

    public void addVineta(String factura, int quantity, String cod_vine,String valor_unidad, double total, int id,String IDLinea,String Cliente) {
        ContentValues values = new ContentValues();
        values.put(FACTURA, factura);
        values.put(CODE_VINNE, cod_vine);
        values.put(CANT_VINNE, quantity);
        values.put(VALOR_UND_VINNE, valor_unidad);
        values.put(VALOR_TOT_VINNE, total);
        values.put(FACTURA_LINEA, IDLinea);
        values.put(TABLE_ID, id);
        values.put(COD_CLIENTE, Cliente);

        try {
            db.insert(TABLE_VINNE, null, values);
        } catch (Exception e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
    }
    public void addRecibo(String factura, String Valor_recibo, String NotaCredito,String Retencion, String Descuento, String Rec_Valor, double Saldo,int ID, String Cliente) {
        ContentValues values = new ContentValues();
        values.put(REC_FACTURA, factura);
        values.put(VALOR_FACTURA, Valor_recibo);
        values.put(NOTA_CREDITO, NotaCredito);
        values.put(RETENCION, Retencion);
        values.put(DESCUENTO, Descuento);
        values.put(REC_VALOR, Rec_Valor);
        values.put(SALDO, Saldo);
        values.put(REC_ID, ID);
        values.put(REC_CLIENTE, Cliente);

        try {
            db.insert(TABLE_RECIBOS, null, values);
        } catch (Exception e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
    }

    public void addDataHistory(String code, String order_list, String order_total, String date_time,String name_client) {
        ContentValues values = new ContentValues();
        values.put(CODE, code);
        values.put(ORDER_LIST, order_list);
        values.put(ORDER_TOTAL, order_total);
        values.put(DATE_TIME, date_time);
        values.put(NAME_CLIENT, name_client);
        try {
            db.insert(TABLE_HISTORY, null, values);
        } catch (Exception e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
    }

    public void deleteData(String id) {
        try {
            //db.delete(TABLE_CART, CART_ID + "=" + id, null);
            db.delete(TABLE_CART, CART_ID +"=?", new String[]{id});
        } catch (Exception e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
    }
    public void deleteDataVineta(String Linea,String Cliente) {
        try {
            db.delete(TABLE_VINNE,TABLE_ID +"=? AND "+COD_CLIENTE+"=?",new String[]{Linea,Cliente});
        } catch (Exception e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
    }


    public void deleteDataHistory(long id) {
        try {
            db.delete(TABLE_HISTORY, HISTORY_ID + "=" + id, null);
        } catch (Exception e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
    }

    public void deleteAllData() {
        try {
            db.delete(TABLE_CART, null, null);
        } catch (Exception e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
    }
    public void deleteAllDataVineta(String id) {
        try {
            db.delete(TABLE_VINNE, COD_CLIENTE + "=?", new String[]{id});
        } catch (Exception e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
    }
    public void deleteAllDataRecibos(String id) {
        try {
            db.delete(TABLE_RECIBOS, REC_CLIENTE + "=?", new String[]{id});
        } catch (Exception e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
    }
    public void deleteDataRecibos(String Linea,String Cliente) {
        try {
            db.delete(TABLE_RECIBOS,REC_ID +"=? AND "+REC_CLIENTE+"=?",new String[]{Linea,Cliente});
        } catch (Exception e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
    }

    public void deleteAllDataHistory() {
        try {
            db.delete(TABLE_HISTORY, null, null);
        } catch (Exception e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
    }

    public void updateData(String id, int quantity, double total_price) {
        ContentValues values = new ContentValues();
        values.put(QUANTITY, quantity);
        values.put(TOTAL_PRICE, total_price);

        try {
            db.update(TABLE_CART, values, CART_ID + "=?", new String[]{id});
        } catch (Exception e) {
            Log.d("updateData DB Error", e.toString());
            e.printStackTrace();
        }
    }

}