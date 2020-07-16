package com.rainbow.aiobrowser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class SplashActivity extends AppCompatActivity implements NoInternetDialogFragment.OnFragmentInteractionListener {

    SharedPreferences pref;
    String notificationData = "";
    CheckInternetConnection internetConnection;
    NoInternetDialogFragment noInternetDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_splash );
        pref = getSharedPreferences(Helper.MyPreference, MODE_PRIVATE);

        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                if(key.equalsIgnoreCase("url"))
                    notificationData = getIntent().getExtras().getString(key);
                Log.d("Splash", "Key: " + key + " Value: " + getIntent().getExtras().getString(key));
            }
        }

        internetConnection = new CheckInternetConnection(this);
        tryToLoad();

    }

    void tryToLoad(){
        if(internetConnection.isConnectingToInternet()){
            fetchFromRemoteConfig();
        }else{
            noInternetDialogFragment = new NoInternetDialogFragment();
            noInternetDialogFragment.setCancelable(false);
            noInternetDialogFragment.show(this.getSupportFragmentManager(),"No_Internet");
        }
    }

    private void fetchFromRemoteConfig(){
        (new MyRemoteConfigs(getApplication(), new MyRemoteConfigs.CallBackFn() {
            @Override
            public void configLoaded() {
                checkAndGetData();
            }

            @Override
            public void onFailed() {
                fetchFromRemoteConfig();
            }

        })).fetch();
    }

    private void checkAndGetData(){
        if(pref.getString( Helper.SP_DATA,"" ).equals( "" )){
            getDataFromServer();
        }else{
            gotoHomePage();
        }
    }

    private void gotoHomePage() {
        Intent i = new Intent( this,HomeActivity.class );
        if(!notificationData.isEmpty())
            i.putExtra("URL",notificationData);
        startActivity( i );
        finish();
    }

    private void getDataFromServer() {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest( Request.Method.GET, Helper.API_END_POINT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString( Helper.SP_DATA,response );
                        editor.apply();
                        gotoHomePage();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley error",error.toString());
            }
        });
        queue.add(stringRequest);
    }

    @Override
    public void retry() {
        noInternetDialogFragment.dismiss();
        tryToLoad();
    }

    @Override
    public void closeApp() {
        noInternetDialogFragment.dismiss();
        finish();
    }
}
