package com.mobilegiants.megila;

import android.app.ListActivity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

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
    private LottieAnimationView animationView;
    private AdView mAdView;
    private SeekBar seekBar;
    private TextView currentPositionTextView;
    private TextView durationTextView;
    private ImageButton playPauseButton;
    private ImageButton rewindButton;
    private ImageButton forwardButton;
    private Handler handler = new Handler();
    private Runnable updateSeekBar;
    private Integer[] resIdsList = {
            R.raw.song1, R.raw.song2, R.raw.song3, R.raw.song4,
            R.raw.song5, R.raw.song6, R.raw.song7, R.raw.porim_songs_set
    };
    private int currentSongIndex = 0;

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
        initSeekBar();
        initTopBar();
    }

    private void initTopBar() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.blue));
        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(0);  // Remove any flags that make
    }

    private void initSeekBar() {
        playPauseButton.setOnClickListener(v -> {
            if (mPlayer == null) {
                startRandomSong();
                playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                handler.post(updateSeekBar);
                animationView.setVisibility(View.VISIBLE);
            } else if (mPlayer.isPlaying()) {
                mPlayer.pause();
                playPauseButton.setImageResource(android.R.drawable.ic_media_play);
                animationView.setVisibility(View.GONE);
            } else {
                mPlayer.start();
                playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                handler.post(updateSeekBar);
                animationView.setVisibility(View.VISIBLE);
            }
        });

        // Set up initial state
        if (mPlayer != null) {
            seekBar.setMax(mPlayer.getDuration());
            durationTextView.setText(formatTime(mPlayer.getDuration()));

            // Update seek bar as music plays
            updateSeekBar = new Runnable() {
                @Override
                public void run() {
                    if (mPlayer != null && mPlayer.isPlaying()) {
                        int currentPosition = mPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        currentPositionTextView.setText(formatTime(currentPosition));
                    }
                    handler.postDelayed(this, 1000);
                }
            };
            handler.post(updateSeekBar);

            // Set up seek bar change listener
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser && mPlayer != null) {
                        mPlayer.seekTo(progress);
                        currentPositionTextView.setText(formatTime(progress));
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    // Optionally pause playback during seeking
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // Optionally resume playback after seeking
                }
            });

            // Set up control buttons

            // Skip to next song
            forwardButton.setOnClickListener(v -> {
                if (mPlayer != null) {
                    mPlayer.stop();
                    mPlayer.release();
                }
                currentSongIndex = (currentSongIndex + 1) % resIdsList.length;
                playSong(currentSongIndex);
            });

// Go to previous song
            rewindButton.setOnClickListener(v -> {
                if (mPlayer != null) {
                    mPlayer.stop();
                    mPlayer.release();
                }
                currentSongIndex = (currentSongIndex - 1 + resIdsList.length) % resIdsList.length;
                playSong(currentSongIndex);
            });
        }
    }

    private void playSong(int index) {
        mPlayer = MediaPlayer.create(SongsActivity.this, resIdsList[index]);
        mPlayer.setOnCompletionListener(getOnCompletionListener());
        mPlayer.start();
        initSeekBar();
        animationView.setVisibility(View.VISIBLE);
        playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
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
        animationView = findViewById(R.id.animationView);
        seekBar = findViewById(R.id.seekBar);
        currentPositionTextView = findViewById(R.id.currentPositionTextView);
        durationTextView = findViewById(R.id.durationTextView);
        playPauseButton = findViewById(R.id.playPauseButton);
        rewindButton = findViewById(R.id.rewindButton);
        forwardButton = findViewById(R.id.forwardButton);
    }

    private void initListeners() {
    }

    private void startRandomSong() {
        Integer[] resIdsList = {R.raw.song1, R.raw.song2, R.raw.song3, R.raw.song4, R.raw.song5, R.raw.song6, R.raw.song7, R.raw.porim_songs_set};
        Random generator = new Random();
        int randomIndex = generator.nextInt(resIdsList.length);
        animationView.setVisibility(View.VISIBLE);
        mPlayer = MediaPlayer.create(SongsActivity.this, resIdsList[randomIndex]);
        mPlayer.setOnCompletionListener(getOnCompletionListener());
        mPlayer.start();
        initSeekBar();
    }

    private void initAdapter() {
        ArrayList<String> songs = new ArrayList<>();

        songs.add(getString(R.string.song_name_new_machrozet));
        songs.add(getString(R.string.song_name_mishenichnas_adar));
        songs.add(getString(R.string.song_name_ve_nahafocho));
        songs.add(getString(R.string.song_name_layeodim_haita));
        songs.add(getString(R.string.song_name_hag_purim));
        songs.add(getString(R.string.song_name_mordechai_yaza));
        songs.add(getString(R.string.song_name_hayav_einish));
        songs.add(getString(R.string.song_name_machrozet));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.songTitle, songs);

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
            String newMachrozet = getString(R.string.song_name_new_machrozet);

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
            } else if (song.equals(newMachrozet)) {
                mPlayer = MediaPlayer.create(SongsActivity.this, R.raw.porim_songs_set);
            }

            initSeekBar();
            mPlayer.setOnCompletionListener(getOnCompletionListener());
            mPlayer.start();
            animationView.setVisibility(View.VISIBLE);
            playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
        });
    }

    private MediaPlayer.OnCompletionListener getOnCompletionListener() {
        return mediaPlayer -> runOnUiThread(() -> {
            animationView.setVisibility(View.GONE);
        });
    }

    private String formatTime(int milliseconds) {
        int seconds = milliseconds / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            handler.removeCallbacks(updateSeekBar);
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
            }
            mPlayer = null;
        }
    }
}
