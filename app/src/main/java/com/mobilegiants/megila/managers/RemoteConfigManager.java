package com.mobilegiants.megila.managers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.ConfigUpdate;
import com.google.firebase.remoteconfig.ConfigUpdateListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.mobilegiants.megila.R;

public class RemoteConfigManager {
    private static final String TAG = "RemoteConfigManager";

    public static final String AD_UNIT_BANNER_ID = "adUnitBannerId";
    public static final String AD_UNIT_INTERSTITIAL_ID = "adUnitInterstitialId";
    private static RemoteConfigManager sInstance;

    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    private RemoteConfigManager() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);

        initConfigUpdateListener();
    }

    private void initConfigUpdateListener() {
        mFirebaseRemoteConfig.addOnConfigUpdateListener(new ConfigUpdateListener() {
            @Override
            public void onUpdate(ConfigUpdate configUpdate) {
                Log.d(TAG, "Updated keys: " + configUpdate.getUpdatedKeys());
                mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        Log.d(TAG, "Updated keys: " + configUpdate.getUpdatedKeys());
                    }
                });
            }

            @Override
            public void onError(FirebaseRemoteConfigException error) {
                Log.w(TAG, "Config update error with code: " + error.getCode(), error);
            }
        });
    }

    public static synchronized RemoteConfigManager getInstance() {
        if (sInstance == null) {
            sInstance = new RemoteConfigManager();
        }
        return sInstance;
    }

    public void fetchRemoteConfigValues() {
        mFirebaseRemoteConfig.fetch()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Remote config values fetched successfully");
                        mFirebaseRemoteConfig.activate();
                    } else {
                        Log.e(TAG, "Error fetching remote config values: " + task.getException().getMessage());
                    }
                });
    }

    public String getParameter(String paramName) {
        return mFirebaseRemoteConfig.getString(paramName);
    }

    public long getLongParameter(String paramName) {
        return mFirebaseRemoteConfig.getLong(paramName);
    }

    public boolean getBooleanParameter(String paramName) {
        return mFirebaseRemoteConfig.getBoolean(paramName);
    }
}