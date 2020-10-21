package com.app.gmv3.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.app.gmv3.R;
import com.app.gmv3.utilities.Constant;
import com.app.gmv3.utilities.Utils;
import com.app.gmv3.utilities.validation.Rule;
import com.app.gmv3.utilities.validation.Validator;
import com.app.gmv3.utilities.validation.annotation.Password;
import com.app.gmv3.utilities.validation.annotation.Required;
import com.app.gmv3.utilities.validation.annotation.TextRule;

import org.json.JSONArray;
import org.json.JSONObject;

public class Login extends AppCompatActivity implements Validator.ValidationListener{

    private View parent_view;
    String strEmail, strPassword,strId, strNombre,IdCompany,strMessage,strDevice;
    String strFullName,strTelefono,strUserEmail;
    @Required(order = 1)
    TextInputEditText Usuario;;


    @Required(order = 3)
    @Password(order = 4, message = "Enter a Valid Password")
    @TextRule(order = 5, minLength = 5, message = "Enter a Password Correctly")
    TextInputEditText Contrasenna;

    private Validator validator;
    Button btnSingIn;

    MyApplication MyApp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        parent_view = findViewById(android.R.id.content);
        btnSingIn = findViewById(R.id.btnOn);

        Usuario = findViewById(R.id.txtUsuario);
        Contrasenna = findViewById(R.id.txtContra);
        Utils.setSystemBarColor(this);
        MyApp = MyApplication.getInstance();
        ((View) findViewById(R.id.sign_up)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(parent_view, "Desarrollado por IT", Snackbar.LENGTH_SHORT).show();
            }
        });


        btnSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validator.validateAsync();
                MyApp.saveType("normal");

            }
        });



        validator = new Validator(this);
        validator.setValidationListener(this);




    }
    @Override
    public void onValidationSucceeded() {
        strEmail = Usuario.getText().toString();
        strPassword = Contrasenna.getText().toString();


        if (Utils.isNetworkAvailable(Login.this)) {

            new MyTaskLoginNormal().execute(Constant.NORMAL_LOGIN_URL + "["+strEmail + "@"  + strPassword+"]" );
        }
    }

    @Override
    public void onValidationFailed(View failedView, Rule<?> failedRule) {
        String message = failedRule.getFailureMessage();
        if (failedView instanceof TextInputEditText) {
            failedView.requestFocus();
            ((TextInputEditText) failedView).setError(message);

        } else {
            Toast.makeText(this, "Registro No Guardado", Toast.LENGTH_SHORT).show();
        }
    }
    public void setResult() {

        if (Constant.GET_SUCCESS_MSG == 0) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.whops);
            dialog.setMessage(R.string.login_failed);
            dialog.setPositiveButton(R.string.dialog_ok, null);
            dialog.setCancelable(false);
            dialog.show();

        } else if (Constant.GET_SUCCESS_MSG == 2) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.whops);
            dialog.setMessage(R.string.login_disabled);
            dialog.setPositiveButton(R.string.dialog_ok, null);
            dialog.setCancelable(false);
            dialog.show();

        } else {
            MyApp.saveIsLogin(true);
            MyApp.saveLogin(strId, strNombre,strFullName,strTelefono,strUserEmail);



            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.login_title);
            dialog.setMessage(R.string.login_success);
            dialog.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(getBaseContext(), MainActivity.class));
                    finish();
                }
            });
            dialog.setCancelable(false);
            dialog.show();

        }
    }
    private class MyTaskLoginNormal extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Login.this);
            progressDialog.setTitle(getResources().getString(R.string.title_please_wait));
            progressDialog.setMessage(getResources().getString(R.string.login_process));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return Utils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (null == result || result.length() == 0) {

            } else {

                try {

                   JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.CATEGORY_ARRAY_NAME);
                    JSONObject objJson = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        Log.e("TAG_error", "onPostExecute: " + objJson );
                        if (objJson.has(Constant.MSG)) {
                            strMessage = objJson.getString(Constant.MSG);
                            Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                        } else {
                            Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                            strNombre = objJson.getString(Constant.USER_NAME);
                            strFullName = objJson.getString(Constant.USER_FULL_NAME);
                            strTelefono = objJson.getString(Constant.USER_TELEFONO);
                            strUserEmail = objJson.getString(Constant.USER_EMAIL);
                            //strImage = objJson.getString("normal");

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (null != progressDialog && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        setResult();
                    }
                }, Constant.DELAY_PROGRESS_DIALOG);
            }

        }
    }


}
