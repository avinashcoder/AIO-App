package com.rainbow.aiobrowser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_splash );
        pref = getSharedPreferences(Helper.MyPreference, MODE_PRIVATE);

        fetchFromRemoteConfig();
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

            }
        });
        queue.add(stringRequest);
    }
}
