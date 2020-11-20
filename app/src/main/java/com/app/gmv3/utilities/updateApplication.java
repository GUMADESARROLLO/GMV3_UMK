package com.app.gmv3.utilities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.app.gmv3.BuildConfig;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class updateApplication  extends AsyncTask<Void, String, String> {
    Context cnxt;
    public updateApplication(Context cnxt) {
        this.cnxt = cnxt;

    }

    @Override
    protected String doInBackground(Void... voids) {
        String newVersion = null;
        try {
            Document document = Jsoup.connect("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID  + "&hl=en")
                    .timeout(30000)
                    .referrer("http://www.google.com")
                    .get();
            if (document != null) {
                Log.d("updateAndroid", "Document: " + document);
                Elements element = document.getElementsContainingOwnText("Current Version");
                for (Element ele : element) {
                    if (ele.siblingElements() != null) {
                        Elements sibElemets = ele.siblingElements();
                        for (Element sibElemet : sibElemets) {
                            newVersion = sibElemet.text();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newVersion;

    }

    @Override
    protected void onPostExecute(String onlineVersion) {
        super.onPostExecute(onlineVersion);
        Log.d("updateAndroid", "Current version: " + BuildConfig.VERSION_NAME + " PlayStore version: " + onlineVersion);
        if (onlineVersion != null && !onlineVersion.isEmpty()) {
            if(isUpdateRequired(BuildConfig.VERSION_NAME, "onlineVersion")){
                Log.d("updateAndroid", "Update is required!!! Current version: " + BuildConfig.VERSION_NAME + " PlayStore version: " + onlineVersion);
                openPlayStore(); //Open PlayStore
            }else{
                Log.d("updateAndroid", "Update is NOT required!");
            }
        }

    }

    private void openPlayStore(){
        final String appPackageName = cnxt.getPackageName();
        try {
            cnxt.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            cnxt.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public boolean isUpdateRequired(String versionActual, String versionNueva) {
        boolean result = false;
        int[] versiones = new int[6];
        int i = 0, anterior = 0, orden = 0;
        if(versionActual != null && versionNueva != null){
            try{
                for(i = 0; i < 6; i++){
                    versiones[i] = 0;
                }
                i = 0;
                do{
                    i = versionActual.indexOf('.', anterior);
                    if(i > 0){
                        versiones[orden] = Integer.parseInt(versionActual.substring(anterior, i));
                    }else{
                        versiones[orden] = Integer.parseInt(versionActual.substring(anterior));

                    }
                    anterior = i + 1;
                    orden++;
                }while(i != -1);
                anterior = 0;
                orden = 3;
                i = 0;
                do{
                    i = versionNueva.indexOf('.', anterior);
                    if(i > 0){
                        versiones[orden] = Integer.parseInt(versionNueva.substring(anterior, i));
                    }else{
                        versiones[orden] = Integer.parseInt(versionNueva.substring(anterior));
                    }
                    anterior = i + 1;
                    orden++;
                }while(i != -1 && orden < 6);


                if(versiones[0] < versiones[3]){
                    result = true;
                }else if(versiones[1] < versiones[4] && versiones[0] == versiones[3]){
                    result = true;
                }else if(versiones[2] < versiones[5] && versiones[0] == versiones[3] && versiones[1] == versiones[4]){
                    result = true;
                }


            }catch (NumberFormatException e){
                Log.e("updateApp", "NFE " + e.getMessage() + " parsing versionAct " + versionActual + " and versionNew " + versionNueva);
            }catch (Exception e){
                Log.e("updateApp", "Ex " + e.getMessage() + " parsing versionAct " + versionActual + " and versionNew " + versionNueva);
            }
        }
        return result;
    }

}