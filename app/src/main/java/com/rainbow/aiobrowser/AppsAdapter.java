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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<AppsModel> arrayList = new ArrayList<>();
    private AppClickInterface mListener;
    private StorageReference mStorageRef;

    public AppsAdapter(Context mContext, ArrayList<AppsModel> arrayList, AppClickInterface mListener) {
        this.mContext = mContext;
        this.arrayList = arrayList;
        this.mListener = mListener;
        this.mStorageRef = FirebaseStorage.getInstance().getReference().child("appicon");
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
        String url = model.getImageUrl();
        if(!url.contains( "http" )){
            if(Helper.IMAGE_BUCKET_URL.contains("firebase")) {
                GlideApp.with(mContext)
                        .load(mStorageRef.child(url))
                        .centerCrop()
                        .into(holder.appImage);
                //url = Helper.IMAGE_BUCKET_URL + url + "?alt=media";
            }
            else{
                url = Helper.IMAGE_BUCKET_URL+"/"+url;
                GlideApp.with( mContext ).load( url ).centerCrop().placeholder( null ).into( holder.appImage );

            }

        }else {
            GlideApp.with( mContext ).load( url ).centerCrop().placeholder( null ).into( holder.appImage );
        }
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
