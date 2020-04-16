package com.rainbow.aiobrowser;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class MyRemoteConfigs {

    private String mTag = "Remote Config";
    private SharedPreferences pref;
    private Context mContext;
    private CallBackFn mCallBackFn;

    public MyRemoteConfigs(Context context, CallBackFn callBackFn) {
        mContext = context;
        mCallBackFn = callBackFn;
        pref = mContext.getSharedPreferences(Helper.MyPreference, Context.MODE_PRIVATE);
    }

    public void fetch() {
        FirebaseRemoteConfig mFbRemoteConfig = FirebaseRemoteConfig.getInstance();
        mFbRemoteConfig.setConfigSettingsAsync(new FirebaseRemoteConfigSettings.Builder().build());
        long cacheExpiration = 3600;
        if (pref.getBoolean(Helper.SP_CACHE_EXPIRATION,false)) {
            cacheExpiration = 0;
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean(Helper.SP_CACHE_EXPIRATION,false);
            editor.apply();
        }

        mFbRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);

        mFbRemoteConfig.fetch(cacheExpiration).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mFbRemoteConfig.fetchAndActivate();
                setAPIEndPoint(mFbRemoteConfig);
                if (mCallBackFn != null)
                    mCallBackFn.configLoaded();

            } else {
                Log.i(mTag, "Fetch Failed " + task.getException());
                if (mCallBackFn != null)
                    mCallBackFn.onFailed();
            }
        });

    }

    private void setAPIEndPoint(FirebaseRemoteConfig fbConfig){
        Helper.API_END_POINT = fbConfig.getString("API_END_POINT");
    }

    public interface CallBackFn {
        void configLoaded();

        void onFailed();

    }
}
