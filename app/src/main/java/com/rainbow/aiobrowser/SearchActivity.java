package com.rainbow.aiobrowser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends AppCompatActivity {

    @BindView( R.id.search_recycler )
    RecyclerView recyclerView;
    @BindView(R.id.searchText)
    EditText searchText;
    @BindView(R.id.goBackBtn)
    FrameLayout backButton;

    SearchAppAdapter adapter;

    private ArrayList<AppsModel> arrayList = new ArrayList<>();
    private SharedPreferences pref;
    private JSONArray jsonArray;
    private String pageAction = "";

    DatabaseHandler handler;
    private ArrayList<AppsModel> favouriteList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        handler = new DatabaseHandler( this );
        Intent i = getIntent();
        if(i.hasExtra("ACTION")){
            pageAction = i.getStringExtra("ACTION");
        }

        pref = getSharedPreferences( "PREFERENCES", Context.MODE_PRIVATE );
        String response = pref.getString( "DATA", "" );
        try {
            jsonArray = new JSONArray( response );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(pageAction.equalsIgnoreCase("FAVOURITE")){
            initFavouriteView();
        }else{
            initSearchView();
        }

        decodeData();
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchApp(searchText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initFavouriteView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new SearchAppAdapter( this, arrayList, pageAction, new SearchAppAdapter.AppClickInterface() {
            @Override
            public void onClick(int position) {
                if(arrayList.get(position).getSelected()){
                    handler.removeFavourite( arrayList.get( position ).id );
                    arrayList.get(position).setSelected(false);
                }else {
                    handler.addFavourite( arrayList.get( position ) );
                    arrayList.get(position).setSelected(true);
                }
            }

            @Override
            public void onLongClick(int position, View view) {
            }
        } );
        recyclerView.setAdapter( adapter );
        adapter.notifyDataSetChanged();
    }


    private void initSearchView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new SearchAppAdapter( this, arrayList, pageAction, new SearchAppAdapter.AppClickInterface() {
            @Override
            public void onClick(int position) {
                //customTabsIntent.launchUrl(getContext(), Uri.parse(arrayList.get( position ).getTargetUrl()));
                Intent intent = new Intent( SearchActivity.this,WebViewActivity.class );
                intent.putExtra( "URL",arrayList.get( position ).getTargetUrl() );
                startActivity( intent );
            }

            @Override
            public void onLongClick(int position, View view) {
            }
        } );
        recyclerView.setAdapter( adapter );
        adapter.notifyDataSetChanged();

    }

    public void searchApp(String key){
        arrayList.clear();
        for(int i=0;i<jsonArray.length();i++){
            try {
                AppsModel model  = new AppsModel( jsonArray.getJSONObject( i ) );
                if(model.getName().toUpperCase().contains( key.toUpperCase() ))
                    arrayList.add( model );

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void decodeData(){
        for(int i=0;i<jsonArray.length();i++){
            try {
                AppsModel model  = new AppsModel( jsonArray.getJSONObject( i ) );
                arrayList.add( model );

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(pageAction.equalsIgnoreCase("FAVOURITE")){
            favouriteList = handler.getFavourite();
            for(int i = 0;i<favouriteList.size();i++){
                for(int j = 0;j<arrayList.size();j++){
                    if(favouriteList.get(i).id == arrayList.get(j).id){
                        arrayList.get(j).setSelected(true);
                    }
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.goBackBtn)
    void goBack(){
        this.finish();
    }
}
