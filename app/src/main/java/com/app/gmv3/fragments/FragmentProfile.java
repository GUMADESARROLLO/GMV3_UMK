package com.app.gmv3.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.gmv3.Config;
import com.app.gmv3.R;
import com.app.gmv3.activities.ActivityHistory;
import com.app.gmv3.activities.ActivityMyStat;
import com.app.gmv3.activities.ActivityReportes;
import com.app.gmv3.activities.MyApplication;
import com.app.gmv3.utilities.SharedPref;
import com.balysv.materialripple.MaterialRippleLayout;

public class FragmentProfile extends Fragment {

    private SharedPref sharedPref;
    TextView txt_user_name;
    TextView txt_user_email;
    TextView txt_user_phone;
    TextView txt_user_address;
    MaterialRippleLayout btn_edit_user;
    TextView btn_order_history;
    LinearLayout lyt_root;
    MyApplication MyApp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sharedPref = new SharedPref(getActivity());

        lyt_root = view.findViewById(R.id.lyt_root);
        if (Config.ENABLE_RTL_MODE) {
            lyt_root.setRotationY(180);
        }
        MyApp = MyApplication.getInstance();

        txt_user_name = view.findViewById(R.id.txt_user_name);
        txt_user_email = view.findViewById(R.id.txt_user_email);
        txt_user_phone = view.findViewById(R.id.txt_user_phone);
        txt_user_address = view.findViewById(R.id.txt_user_address);

        btn_edit_user = view.findViewById(R.id.btn_edit_user);
        btn_edit_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder( getActivity())
                        .setTitle("Atención.")
                        .setMessage("¿Quiere Salir de la Aplicación?")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MyApp.saveIsLogin(false);
                                MyApp.saveLogin("", "","","","");
                                getActivity().finish();
                            }
                        }).setNegativeButton("NO",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

            }
        });

        btn_order_history = view.findViewById(R.id.btn_order_history);
        btn_order_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ActivityHistory.class);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.id_mis_stat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ActivityMyStat.class);
                startActivity(intent);
            }
        });
        view.findViewById(R.id.Reportes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ActivityReportes.class);
                startActivity(intent);
            }
        });



        return view;
    }

    @Override
    public void onResume() {
        txt_user_name.setText(sharedPref.getYourName());
        txt_user_email.setText(sharedPref.getYourEmail());
        txt_user_phone.setText(sharedPref.getYourPhone());
        txt_user_address.setText(sharedPref.getYourAddress());
        super.onResume();
    }

}
