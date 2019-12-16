package com.rainbow.aiobrowser;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppsViewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PAGE_TYPE = "param1";
    private static final String FILTER = "param2";

    // TODO: Rename and change types of parameters
    private String pageType;
    private String filter;

    private ArrayList<AppsModel> arrayList = new ArrayList<>();

    private OnFragmentInteractionListener mListener;
    private SharedPreferences pref;
    JSONArray jsonArray;
    private AppsAdapter adapter;

    @BindView( R.id.recyclerView )
    RecyclerView recyclerView;

    public AppsViewFragment() {
        // Required empty public constructor
    }

    public static AppsViewFragment newInstance(String pageType, String filterApp) {
        AppsViewFragment fragment = new AppsViewFragment();
        Bundle args = new Bundle();
        args.putString( PAGE_TYPE, pageType );
        args.putString( FILTER, filterApp );
        fragment.setArguments( args );
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        if (getArguments() != null) {
            pageType = getArguments().getString( PAGE_TYPE );
            filter = getArguments().getString( FILTER );
        }
        pref = getContext().getSharedPreferences( "PREFERENCES",Context.MODE_PRIVATE );
        String response = pref.getString( "DATA","" );
        try {
            jsonArray = new JSONArray( response );
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate( R.layout.fragment_apps_view, container, false );
        ButterKnife.bind( this,view );

        initView();
        if(jsonArray.length()!=0){
            decodeData();
        }else{
            getDataFromServer();
        }
        return view;
    }

    private void initView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new AppsAdapter( getContext(), arrayList, new AppsAdapter.AppClickInterface() {
            @Override
            public void onClick(int position) {

            }
        } );
        recyclerView.setAdapter( adapter );
        adapter.notifyDataSetChanged();
    }

    private void getDataFromServer() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url ="https://allinoneappbrowser.000webhostapp.com/allinone/getappdata.php";

        StringRequest stringRequest = new StringRequest( Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            jsonArray = new JSONArray( response );
                            decodeData();
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString( "DATA",response );
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

    private void decodeData(){
        for(int i=0;i<jsonArray.length();i++){
            try {
                AppsModel model  = new AppsModel( jsonArray.getJSONObject( i ) );
                arrayList.add( model );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        adapter.notifyDataSetChanged();
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction( uri );
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach( context );
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException( context.toString()
                    + " must implement OnFragmentInteractionListener" );
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
