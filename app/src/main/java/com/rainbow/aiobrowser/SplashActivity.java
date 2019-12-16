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
        pref = getSharedPreferences( "PREFERENCES",MODE_PRIVATE );
        if(pref.getString( "DATA","" ).equals( "" )){
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
        String url ="https://allinoneappbrowser.000webhostapp.com/allinone/getappdata.php";

        StringRequest stringRequest = new StringRequest( Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString( "DATA",response );
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
