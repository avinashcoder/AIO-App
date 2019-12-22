package com.rainbow.aiobrowser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<AppsModel> arrayList = new ArrayList<>();
    private AppClickInterface mListener;

    public AppsAdapter(Context mContext, ArrayList<AppsModel> arrayList, AppClickInterface mListener) {
        this.mContext = mContext;
        this.arrayList = arrayList;
        this.mListener = mListener;
    }

    interface AppClickInterface{
        void onClick(int position);
        void  onLongClick(int position, View view);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( parent.getContext()).inflate( R.layout.app_layout,parent,false );
        return new ViewHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppsModel model = arrayList.get( position );
        holder.appName.setText( model.getName() );
        Glide.with( mContext ).load( model.getImageUrl() ).centerCrop().placeholder( null ).into( holder.appImage );
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {

        private ImageView appImage;
        private TextView appName;

        public ViewHolder(@NonNull View itemView) {
            super( itemView );
            appImage = itemView.findViewById( R.id.app_image );
            appName = itemView.findViewById( R.id.app_name );

            itemView.setOnClickListener( this );
            itemView.setOnLongClickListener( this );
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if(position>=0){
                mListener.onClick( position );
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
