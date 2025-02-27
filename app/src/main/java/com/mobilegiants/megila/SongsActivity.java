package com.mobilegiants.megila;

import android.app.ListActivity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.mobilegiants.megila.managers.RemoteConfigManager;

import java.util.ArrayList;
import java.util.Random;

public class SongsActivity extends ListActivity {

    private MediaPlayer mPlayer;
    private Button stopBtn;
    private LottieAnimationView animationView;
    private AdView mAdView;

    public SongsActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);
        initViews();
        initListeners();
        initAdapter();
        initBanner();
        initListOnItemClickListener();
    }

    private void initBanner() {
        MobileAds.initialize(this, initializationStatus -> Log.i(MainActivity.MOBILE_ADS_TAG, "onInitializationComplete"));
        FrameLayout bannerLayout = findViewById(R.id.bannerContainer);
        mAdView = new AdView(this);

        String adUnitId;
        if (BuildConfig.DEBUG) {
            adUnitId = getString(R.string.ad_unit_id_banner_debug);
        } else {
            adUnitId = RemoteConfigManager.getInstance().getParameter(RemoteConfigManager.AD_UNIT_BANNER_ID);
        }
        mAdView.setAdUnitId(adUnitId);
        mAdView.setAdSize(AdSize.LARGE_BANNER);
        bannerLayout.addView(mAdView);

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                Log.i(MainActivity.MOBILE_ADS_TAG, "onAdClicked");
            }

            @Override
            public void onAdClosed() {
                Log.i(MainActivity.MOBILE_ADS_TAG, "onAdClosed");

            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                Log.i(MainActivity.MOBILE_ADS_TAG, adError.toString());

            }

            @Override
            public void onAdImpression() {
                Log.i(MainActivity.MOBILE_ADS_TAG, "onAdImpression");
            }

            @Override
            public void onAdLoaded() {
                Log.i(MainActivity.MOBILE_ADS_TAG, "onAdLoaded");

            }

            @Override
            public void onAdOpened() {
                Log.i(MainActivity.MOBILE_ADS_TAG, "onAdOpened");
            }
        });
    }

    private void initViews() {
        stopBtn = findViewById(R.id.stopBtn);
        animationView = findViewById(R.id.animationView);
    }

    private void initListeners() {
        stopBtn.setOnClickListener(v -> {
            if (mPlayer == null) {
                startRandomSong();
            } else if (mPlayer.isPlaying()) {
                stopBtn.setText(getString(R.string.play_song));
                animationView.setVisibility(View.INVISIBLE);
                mPlayer.pause();
            } else {
                stopBtn.setText(getString(R.string.pause_song));
                animationView.setVisibility(View.VISIBLE);
                mPlayer.start();
            }
        });
    }

    private void startRandomSong() {
        Integer[] resIdsList = {R.raw.song1, R.raw.song2, R.raw.song3, R.raw.song4, R.raw.song5, R.raw.song6, R.raw.song7};
        Random generator = new Random();
        int randomIndex = generator.nextInt(resIdsList.length);
        stopBtn.setText(getString(R.string.pause_song));
        animationView.setVisibility(View.VISIBLE);
        mPlayer = MediaPlayer.create(SongsActivity.this, resIdsList[randomIndex]);
        mPlayer.setOnCompletionListener(getOnCompletionListener());
        mPlayer.start();
    }

    private void initAdapter() {
        ArrayList<String> songs = new ArrayList<>();
        songs.add(getString(R.string.song_name_mishenichnas_adar));
        songs.add(getString(R.string.song_name_ve_nahafocho));
        songs.add(getString(R.string.song_name_layeodim_haita));
        songs.add(getString(R.string.song_name_hag_purim));
        songs.add(getString(R.string.song_name_mordechai_yaza));
        songs.add(getString(R.string.song_name_hayav_einish));
        songs.add(getString(R.string.song_name_machrozet));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, songs);

        setListAdapter(adapter);

    }

    private void initListOnItemClickListener() {

        ListView songsList = getListView();

        songsList.setOnItemClickListener((parent, view, position, id) -> {
            String song = (String) parent.getItemAtPosition(position);
            if (mPlayer != null) {
                mPlayer.stop();
            }

            String mishenichnasAdar = getString(R.string.song_name_mishenichnas_adar);
            String veNahafocho = getString(R.string.song_name_ve_nahafocho);
            String layeodimHaita = getString(R.string.song_name_layeodim_haita);
            String hagPurim = getString(R.string.song_name_hag_purim);
            String mordechaiYaza = getString(R.string.song_name_mordechai_yaza);
            String hayavEinish = getString(R.string.song_name_hayav_einish);
            String machrozet = getString(R.string.song_name_machrozet);

            if (song.equals(mishenichnasAdar)) {
                mPlayer = MediaPlayer.create(SongsActivity.this, R.raw.song1);
            } else if (song.equals(veNahafocho)) {
                mPlayer = MediaPlayer.create(SongsActivity.this, R.raw.song2);
            } else if (song.equals(layeodimHaita)) {
                mPlayer = MediaPlayer.create(SongsActivity.this, R.raw.song3);
            } else if (song.equals(hagPurim)) {
                mPlayer = MediaPlayer.create(SongsActivity.this, R.raw.song4);
            } else if (song.equals(mordechaiYaza)) {
                mPlayer = MediaPlayer.create(SongsActivity.this, R.raw.song5);
            } else if (song.equals(hayavEinish)) {
                mPlayer = MediaPlayer.create(SongsActivity.this, R.raw.song6);
            } else if (song.equals(machrozet)) {
                mPlayer = MediaPlayer.create(SongsActivity.this, R.raw.song7);
            }

            mPlayer.setOnCompletionListener(getOnCompletionListener());
            mPlayer.start();
            stopBtn.setText(getString(R.string.pause_song));
            animationView.setVisibility(View.VISIBLE);
        });
    }

    private MediaPlayer.OnCompletionListener getOnCompletionListener() {
        return mediaPlayer -> runOnUiThread(() -> {
            stopBtn.setText(getString(R.string.play_song));
            animationView.setVisibility(View.INVISIBLE);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
            }
            mPlayer = null;
        }
    }
}
