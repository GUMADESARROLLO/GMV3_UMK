package com.app.gmv3.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.app.gmv3.R;

public class SharedPrefCliente {
    private Context ctx;
    private SharedPreferences default_prefence;

    public SharedPrefCliente(Context context) {
        this.ctx = context;
        default_prefence = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private String str(int string_id) {
        return ctx.getString(string_id);
    }

    public void setCliente(String name) {
        default_prefence.edit().putString(str(R.string.pref_pedido_cliente), name).apply();
    }

    public String getCliente() {
        return default_prefence.getString(str(R.string.pref_pedido_cliente), str(R.string.pref_pedido_cliente));
    }
    public void setDescripcion(String name) {
        default_prefence.edit().putString(str(R.string.pref_pedido_descripcion), name).apply();
    }

    public String getDesripcion() {
        return default_prefence.getString(str(R.string.pref_pedido_descripcion), str(R.string.pref_pedido_descripcion));
    }


    public void setAddress(String name) {
        default_prefence.edit().putString(str(R.string.pref_pedido_direccion), name).apply();
    }

    public String getAddress() {
        return default_prefence.getString(str(R.string.pref_pedido_direccion), str(R.string.pref_pedido_direccion));
    }
    public void setDisponible(String name) {
        default_prefence.edit().putString(str(R.string.pref_pedido_disponible), name).apply();
    }

    public String getDisponible() {
        return default_prefence.getString(str(R.string.pref_pedido_disponible), "N/D");
    }

    public void setMoroso(String name) {
        default_prefence.edit().putString(str(R.string.pref_pedido_moroso), name).apply();
    }

    public String getMoroso() {
        return default_prefence.getString(str(R.string.pref_pedido_moroso), str(R.string.pref_pedido_moroso));
    }




}
