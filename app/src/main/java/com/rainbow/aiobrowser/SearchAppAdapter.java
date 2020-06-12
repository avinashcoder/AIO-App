package com.rainbow.aiobrowser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class SearchAppAdapter extends RecyclerView.Adapter<SearchAppAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<AppsModel> arrayList = new ArrayList<>();
    private AppClickInterface mListener;
    private String pageAction;

    public SearchAppAdapter(Context mContext, ArrayList<AppsModel> arrayList, String pageAction, SearchAppAdapter.AppClickInterface mListener) {
        this.mContext = mContext;
        this.arrayList = arrayList;
        this.pageAction = pageAction;
        this.mListener = mListener;
    }

    interface AppClickInterface{
        void onClick(int position);
        void  onLongClick(int position, View view);
    }

    @NonNull
    @Override
    public SearchAppAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( parent.getContext()).inflate( R.layout.app_search_layout,parent,false );
        return new SearchAppAdapter.ViewHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppsModel model = arrayList.get( position );
        holder.appName.setText( model.getName() );
        String url = model.getImageUrl();
        if(!url.contains( "http" )){
            url = "https://firebasestorage.googleapis.com/v0/b/all-in-one-d12ec.appspot.com/o/appicon%2F"+url+"?alt=media";
        }
        Glide.with( mContext ).load( url ).centerCrop().placeholder( null ).into( holder.appImage );
        if(pageAction.equalsIgnoreCase("FAVOURITE")){
            holder.checkBox.setVisibility(View.VISIBLE);
        }else{
            holder.checkBox.setVisibility(View.GONE);
        }
        holder.checkBox.setChecked(model.getSelected());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {

        private ImageView appImage;
        private TextView appName;
        private CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super( itemView );
            appImage = itemView.findViewById( R.id.app_image );
            appName = itemView.findViewById( R.id.app_name );
            checkBox = itemView.findViewById(R.id.checkbox);

            itemView.setOnClickListener( this );
            itemView.setOnLongClickListener( this );
            checkBox.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if(pageAction.equalsIgnoreCase("FAVOURITE")){
                if(view == itemView){
                    if(checkBox.isChecked()){
                        checkBox.setChecked(false);
                    }else{
                        checkBox.setChecked(true);
                    }
                    mListener.onClick(position);
                }
                else if(view == checkBox){
                    mListener.onClick(position);
                }
            }else{
                if(position>=0){
                    mListener.onClick( position );
                }
            }

        }

        @Override
        public boolean onLongClick(View view) {
            int position = getAdapterPosition();
            if(position>=0){
                mListener.onLongClick( position,view );
            }
            return true;
        }
    }
}
