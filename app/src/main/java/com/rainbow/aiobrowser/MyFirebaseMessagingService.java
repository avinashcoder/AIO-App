package com.rainbow.aiobrowser;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Html;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Objects;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    String title="",notificationBody="",imageUrl="";
    SharedPreferences pref;
    Intent intent;
    PendingIntent pendingIntent;

    @Override
    public void onNewToken(String token) {
        Log.d("TOKEN", token);

        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        pref = getSharedPreferences(Helper.MyPreference,Context.MODE_PRIVATE);
        intent = new Intent(this,SplashActivity.class);
        createNotification(remoteMessage);

    }

    public void createNotification(final RemoteMessage remoteMessage) {
        try {
            if( remoteMessage.getNotification()!=null ){
                if(remoteMessage.getNotification().getTitle() != null)
                    title = Objects.requireNonNull( remoteMessage.getNotification() ).getTitle();
                if(remoteMessage.getNotification().getBody() != null)
                    notificationBody = remoteMessage.getNotification().getBody();
                if(!(remoteMessage.getNotification().getImageUrl()==null || remoteMessage.getNotification().getImageUrl().toString().isEmpty())){
                    imageUrl = remoteMessage.getNotification().getImageUrl().toString();
                }
            }
        }catch (Exception e){
            Log.d("Notification ", e.toString());
        }
        Map<String, String> notificationExtraData  = remoteMessage.getData();
        final Bitmap resource = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher);

        if(notificationExtraData.containsKey( "title" )){
            title = notificationExtraData.get("title");
        }
        if(notificationExtraData.containsKey( "message" )){
            notificationBody = notificationExtraData.get( "message" );
        }
        if(notificationExtraData.containsKey( "image" )){
            imageUrl = notificationExtraData.get( "image" );
        }
        if(notificationExtraData.containsKey("url")){
            String url = notificationExtraData.get("url");
            intent = new Intent(this, WebViewActivity.class);
            intent.putExtra("URL",url);
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
            taskStackBuilder.addParentStack(HomeActivity.class);
            taskStackBuilder.addNextIntentWithParentStack(intent);
            pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        if(notificationExtraData.containsKey("clear_data") || notificationExtraData.containsKey("cache_expire")){
            SharedPreferences.Editor editor = pref.edit();
            if(notificationExtraData.containsKey("clear_data")){
                if(notificationExtraData.get("clear_data").equals("1")){
                    editor.putBoolean(Helper.SP_CAN_CLEAR_DATA,true);
                }

            }
            if(notificationExtraData.containsKey("cache_expire")){
                if(notificationExtraData.get("cache_expire").equals("1"))
                    editor.putBoolean(Helper.SP_CACHE_EXPIRATION,true);
            }
            editor.apply();
            return;
        }

        if(!(imageUrl==null || imageUrl.isEmpty())){
            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(imageUrl)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap imgRes, Transition<? super Bitmap> transition) {
                            createNotificationWithImage(remoteMessage,imgRes);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
        }else{
            NotificationManager notificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE);
            String NOTIFICATION_CHANNEL_ID = "AIO";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_DEFAULT);

                //Configure Notification Channel
                notificationChannel.setDescription("My Notification");
                notificationChannel.enableLights(true);


                notificationManager.createNotificationChannel(notificationChannel);
            }
            Context context = this;
            //PendingIntent pendingIntent = PendingIntent.getActivity(context,100,intent,PendingIntent.FLAG_CANCEL_CURRENT);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle( title)
                    //.setAutoCancel(true)
                    .setContentText(notificationBody)
                    .setLargeIcon(resource)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText( Html.fromHtml(notificationBody)))
                    .setShowWhen(true)
                    .setContentIntent(pendingIntent)
                    //.setFullScreenIntent(pendingIntent, true)  // For popup notification
                    .setAutoCancel(true)
                    .setWhen(System.currentTimeMillis())
                    .setPriority(NotificationCompat.PRIORITY_MAX);


            notificationManager.notify(1, notificationBuilder.build());
        }

    }

    private void createNotificationWithImage(RemoteMessage remoteMessage, Bitmap resource) {

        NotificationManager notificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "AIO";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_DEFAULT);

            //Configure Notification Channel
            notificationChannel.setDescription("My Notification");
            notificationChannel.enableLights(true);


            notificationManager.createNotificationChannel(notificationChannel);
        }
        Context context = this;
        //PendingIntent pendingIntent = PendingIntent.getActivity(context,100,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle( title)
                .setAutoCancel(true)
                .setContentText(notificationBody)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(notificationBody)))
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(resource)
                        .bigLargeIcon(resource))
                .setLargeIcon(resource)
                .setShowWhen(true)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                //.setFullScreenIntent(pendingIntent, true)
                .setPriority(NotificationCompat.PRIORITY_MAX);
        notificationManager.notify(1, notificationBuilder.build());

    }
}