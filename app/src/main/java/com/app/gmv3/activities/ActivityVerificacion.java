package com.app.gmv3.activities;



import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.gmv3.Config;
import com.app.gmv3.R;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.app.gmv3.utilities.Constant;
import com.app.gmv3.utilities.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.onesignal.OneSignal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.app.gmv3.utilities.Constant.POST_ORDER;
import static com.app.gmv3.utilities.Constant.POST_VERIFICACION;

public class ActivityVerificacion extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, ResultCallback<Status> {

    private static final String TAG = ActivityVerificacion.class.getSimpleName();

    private static final String LOCATION_KEY = "location-key";

    // Location API
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private Location mLastLocation;

    ProgressDialog progressDialog;


    // UI
    private TextView mLatitude;
    private TextView mLongitude;

    String CodigoCliente;

    // C??digos de petici??n
    public static final int REQUEST_LOCATION = 1;
    public static final int REQUEST_CHECK_SETTINGS = 2;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String date = dateFormat.format(Calendar.getInstance().getTime());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        CodigoCliente = intent.getStringExtra("Codi_cliente");

        // Referencias UI
        mLatitude  = findViewById(R.id.tv_latitude);
        mLongitude = findViewById(R.id.tv_longitude);
        progressDialog = new ProgressDialog(ActivityVerificacion.this);

       // Establecer punto de entrada para la API de ubicaci??n
        buildGoogleApiClient();

        // Crear configuraci??n de peticiones
        createLocationRequest();

        // Crear opciones de peticiones
        buildLocationSettingsRequest();

        // Verificar ajustes de ubicaci??n actuales
        checkLocationSettings();

        findViewById(R.id.btn_send_verification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isNetworkAvailable(ActivityVerificacion.this)) {
                    requestAction();
                } else {
                    Toast.makeText(ActivityVerificacion.this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
            }
        });

        updateValuesFromBundle(savedInstanceState);

    }
    public void requestAction() {

        progressDialog.setTitle(getString(R.string.checkout_submit_title));
        progressDialog.setMessage(getString(R.string.verification_submit_msg));
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, POST_VERIFICACION, new Response.Listener<String>() {
            @Override
            public void onResponse(final String ServerResponse) {

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        //dialogSuccessOrder();
                       // ProfileWallet.ImgVerication.setImageResource(R.drawable.verificado);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();

                    }
                }, 2000);

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), volleyError.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Lati", mLatitude.getText().toString());
                params.put("Logi", mLongitude.getText().toString());
                params.put("cliente", CodigoCliente);
                params.put("date", date);

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(ActivityVerificacion.this);
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();

        }

        IntentFilter intentFilter = new IntentFilter(Constant.BROADCAST_ACTION);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Protegemos la ubicaci??n actual antes del cambio de configuraci??n
        outState.putParcelable(LOCATION_KEY, mLastLocation);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.d(TAG, "El usuario permiti?? el cambio de ajustes de ubicaci??n.");
                        processLastLocation();
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.d(TAG, "El usuario no permiti?? el cambio de ajustes de ubicaci??n");
                        break;
                }
                break;
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                startLocationUpdates();

            } else {
                Toast.makeText(this, "Permisos no otorgados", Toast.LENGTH_LONG).show();
            }
        }
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .enableAutoManage(this, this)
                .build();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest()
                .setInterval(Constant.UPDATE_INTERVAL)
                .setFastestInterval(Constant.UPDATE_FASTEST_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest)
                .setAlwaysShow(true);
        mLocationSettingsRequest = builder.build();
    }

    private void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient, mLocationSettingsRequest
                );

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                Status status = result.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.d(TAG, "Los ajustes de ubicaci??n satisfacen la configuraci??n.");
                        startLocationUpdates();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            Log.d(TAG, "Los ajustes de ubicaci??n no satisfacen la configuraci??n. " +
                                    "Se mostrar?? un di??logo de ayuda.");
                            status.startResolutionForResult(
                                    ActivityVerificacion.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.d(TAG, "El Intent del di??logo no funcion??.");
                            // Sin operaciones
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.d(TAG, "Los ajustes de ubicaci??n no son apropiados.");
                        break;

                }
            }
        });
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(LOCATION_KEY)) {
                mLastLocation = savedInstanceState.getParcelable(LOCATION_KEY);

                updateLocationUI();
            }




        }
    }

    private void updateLocationUI() {
        mLatitude.setText(String.valueOf(mLastLocation.getLatitude()));
        mLongitude.setText(String.valueOf(mLastLocation.getLongitude()));
    }



    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi
                .removeLocationUpdates(mGoogleApiClient, this);
    }


    private void getLastLocation() {
        if (isLocationPermissionGranted()) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        } else {
            manageDeniedPermission();
        }
    }

    private void processLastLocation() {
        getLastLocation();
        if (mLastLocation != null) {
            updateLocationUI();
        }
    }



    private void startLocationUpdates() {
        if (isLocationPermissionGranted()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } else {
            manageDeniedPermission();
        }
    }

    private void manageDeniedPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Aqu?? muestras confirmaci??n explicativa al usuario
            // por si rechaz?? los permisos anteriormente
        } else {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }
    }

    private boolean isLocationPermissionGranted() {
        int permission = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permission == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        // Obtenemos la ??ltima ubicaci??n al ser la primera vez
        processLastLocation();
        // Iniciamos las actualizaciones de ubicaci??n
        startLocationUpdates();
        // Y tambi??n las de reconocimiento de actividad

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Conexi??n suspendida");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(
                this,
                "Error de conexi??n con el c??digo:" + connectionResult.getErrorCode(),
                Toast.LENGTH_LONG)
                .show();

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, String.format("Nueva ubicaci??n: (%s, %s)",
                location.getLatitude(), location.getLongitude()));
        mLastLocation = location;
        updateLocationUI();
    }


    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
            Log.d(TAG, "Detecci??n de actividad iniciada");

        } else {
            Log.e(TAG, "Error al iniciar/remover la detecci??n de actividad: "
                    + status.getStatusMessage());
        }
    }


}
