package com.rainbow.aiobrowser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AppsViewFragment extends Fragment {

    private static final String PAGE_TYPE = "param1";
    private static final String FILTER = "param2";

    // TODO: Rename and change types of parameters
    private String pageType;
    private String filter;

    private ArrayList<AppsModel> arrayList = new ArrayList<>();

    private OnFragmentInteractionListener mListener;
    private SharedPreferences pref;
    private JSONArray jsonArray;
    private AppsAdapter adapter;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.no_favourite_layout)
    LinearLayout noFavouriteLayout;
    @BindView(R.id.no_app)
    TextView noApp;
    /*@BindView(R.id.adView)
    AdView mAdView;
    @BindView(R.id.adViewScroll)
    AdView mAdViewScroll;
    @BindView(R.id.ad_big_banner)
    AdView mAdViewBig;*/
    @BindView(R.id.add_favourite)
    FloatingActionButton addFavNew;
    @BindView(R.id.no_app_msg)
    TextView noAppMsg;

    @BindView(R.id.ad_layout)
    FrameLayout adLayout;
    @BindView(R.id.ad_layout_bottom)
    FrameLayout adLayoutBottom;


    private DatabaseHandler handler;
    private int orientation;
    private AdRequest adRequest;
    int flag = 0;

    private BroadcastReceiver refreshFavourite = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (pageType.equalsIgnoreCase("FAVOURITE")) {
                arrayList = handler.getFavourite();
                flag = 1;
                initView();
            }
        }
    };

    public AppsViewFragment() {
        // Required empty public constructor
    }

    public static AppsViewFragment newInstance(String pageType, String filterApp) {
        AppsViewFragment fragment = new AppsViewFragment();
        Bundle args = new Bundle();
        args.putString(PAGE_TYPE, pageType);
        args.putString(FILTER, filterApp);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pageType = getArguments().getString(PAGE_TYPE);
            filter = getArguments().getString(FILTER);
        }
        handler = new DatabaseHandler(getContext());

        if (pageType.equalsIgnoreCase("FAVOURITE")) {
            arrayList = handler.getFavourite();
        } else {
            pref = getContext().getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE);
            String response = pref.getString("DATA", "");
            try {
                jsonArray = new JSONArray(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apps_view, container, false);
        ButterKnife.bind(this, view);
        orientation = getResources().getConfiguration().orientation;
        adRequest = new AdRequest.Builder().build();

        initView();
        if (pageType.equalsIgnoreCase("HOST")) {
            if (jsonArray.length() != 0) {
                decodeData();
            } else {
                getDataFromServer();
            }
        }
        if (pageType.equalsIgnoreCase("FAVOURITE")) {
            addFavNew.setVisibility(View.VISIBLE);
        } else {
            addFavNew.setVisibility(View.GONE);
        }
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getContext())).registerReceiver(refreshFavourite, new IntentFilter("refresh"));
        flag = 0;
        loadProperAds();
        return view;
    }

    private void initView() {
       /* CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setShowTitle( false );
        builder.enableUrlBarHiding();
        CustomTabsIntent customTabsIntent = builder.build();*/
       int spanCount = (orientation == Configuration.ORIENTATION_PORTRAIT)? 3 : 5;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), spanCount);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new AppsAdapter(getContext(), arrayList, new AppsAdapter.AppClickInterface() {
            @Override
            public void onClick(int position) {
                //customTabsIntent.launchUrl(getContext(), Uri.parse(arrayList.get( position ).getTargetUrl()));
                Intent intent = new Intent(getContext(), WebViewActivity.class);
                intent.putExtra("URL", arrayList.get(position).getTargetUrl());
                startActivity(intent);
            }

            @Override
            public void onLongClick(int position, View view) {
                createPopUp(position, view);
            }
        });
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if (arrayList.size() == 0) {
            noFavouriteLayout.setVisibility(View.VISIBLE);
            if (pageType.equalsIgnoreCase("FAVOURITE")) {
                noAppMsg.setText("OOPS! No apps in your favourite list, add your favourite app here");
            } else {
                noAppMsg.setText("Currently, no app available in this section");
            }
        } else {
            noFavouriteLayout.setVisibility(View.GONE);
        }
        if(flag == 1)
            loadProperAds();
    }

    private void createPopUp(int position, View view) {
        PopupMenu popup = new PopupMenu(getContext(), view);
        int id = arrayList.get(position).getId();
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.app_popup_menu, popup.getMenu());
        if (handler.checkList(id)) {
            popup.getMenu().findItem(R.id.add).setVisible(false);
            popup.getMenu().findItem(R.id.remove).setVisible(true);
        } else {
            popup.getMenu().findItem(R.id.add).setVisible(true);
            popup.getMenu().findItem(R.id.remove).setVisible(false);
        }
        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.add:
                    handler.addFavourite(arrayList.get(position));
                    Toast.makeText(getContext(), arrayList.get(position).getName() + " is added in your favourite", Toast.LENGTH_SHORT).show();
                    startBroadCast();
                    return true;
                case R.id.remove:
                    handler.removeFavourite(id);
                    Toast.makeText(getContext(), arrayList.get(position).getName() + " is removed from your favourite", Toast.LENGTH_SHORT).show();
                    startBroadCast();
                    return true;
            }
            return true;

        });

        popup.show();
    }

    private void startBroadCast() {
        Intent intent = new Intent("refresh");
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getContext())).sendBroadcast(intent);
    }

    private void getDataFromServer() {
        RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Helper.API_END_POINT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            jsonArray = new JSONArray(response);
                            decodeData();
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("DATA", response);
                            editor.apply();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);
    }

    private void decodeData() {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                AppsModel model = new AppsModel(jsonArray.getJSONObject(i));
                if (model.getAppType().equalsIgnoreCase(filter) || filter.equalsIgnoreCase("ALL")) {
                    arrayList.add(model);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        adapter.notifyDataSetChanged();
        if (arrayList.size() == 0) {
            noFavouriteLayout.setVisibility(View.VISIBLE);
            if (pageType.equalsIgnoreCase("FAVOURITE")) {
                noAppMsg.setText("OOPS! No apps in your favourite list, add your favourite app here");
            } else {
                noAppMsg.setText("Currently, no app available in this section");
            }
            //showBottomAds();

        } else {
            noFavouriteLayout.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.add_favourite, R.id.no_favourite_layout})
    void addNewFavourite() {
        if (pageType.equalsIgnoreCase("FAVOURITE")) {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            intent.putExtra("ACTION", "FAVOURITE");
            startActivity(intent);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDestroy() {
        if (refreshFavourite != null) {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(refreshFavourite);
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (pageType.equalsIgnoreCase("FAVOURITE")) {
            arrayList = handler.getFavourite();
            flag = 1;
            initView();
        }
    }

    private void loadProperAds(){
        adLayoutBottom.removeAllViews();
        adLayout.removeAllViews();
        AdView adView = new AdView(Objects.requireNonNull(getContext()));
        adView.setAdUnitId(getResources().getString(R.string.banner_ad_unit_id));
        if(orientation == Configuration.ORIENTATION_PORTRAIT && arrayList.size() != 0){
            adView.setAdSize(AdSize.MEDIUM_RECTANGLE);
        }else{
            adView.setAdSize(AdSize.SMART_BANNER);
        }
        adView.loadAd(adRequest);
        if(arrayList.size() != 0)
            adLayout.addView(adView);
        else
            adLayoutBottom.addView(adView);
        /*if(orientation == Configuration.ORIENTATION_PORTRAIT){
            mAdView.setVisibility(View.GONE);
            mAdViewScroll.setVisibility(View.GONE);
            mAdViewBig.setVisibility(View.VISIBLE);
            mAdViewBig.loadAd(adRequest);
        }else{
            mAdViewBig.setVisibility(View.GONE);
            mAdView.setVisibility(View.GONE);
            mAdViewScroll.setVisibility(View.VISIBLE);
            mAdViewScroll.loadAd(adRequest);
        }*/
    }

    private void showBottomAds(){
        adLayout.removeAllViews();
        AdView adView = new AdView(Objects.requireNonNull(getContext()));
        adView.setAdUnitId(getResources().getString(R.string.banner_ad_unit_id));
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.loadAd(adRequest);
        adLayoutBottom.addView(adView);
        /*mAdViewBig.setVisibility(View.GONE);
        mAdViewScroll.setVisibility(View.GONE);
        mAdView.setVisibility(View.VISIBLE);
        mAdView.loadAd(adRequest);*/
    }
}
