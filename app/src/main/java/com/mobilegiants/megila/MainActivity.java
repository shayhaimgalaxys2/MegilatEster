package com.mobilegiants.megila;


import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.mobilegiants.megila.custom_views.InteractiveScrollView;
import com.mobilegiants.megila.managers.RemoteConfigManager;
import com.pixplicity.easyprefs.library.Prefs;
import com.pushwoosh.Pushwoosh;

public class MainActivity extends AppCompatActivity {

    private static final long SPLASH_TIME_IN_MILLIS = 3000;
    public static final float SAMECH_TEXT_SIZE = 0.6f;
    public static final float VERSE_TEXT_SIZE = 0.7f;
    public static final float CHAPTER_TEXT_SIZE = 1.15f;
    public static final String MOBILE_ADS_TAG = "MobileAds";
    private static final String CHECKED_ITEM_KEY = "checked_item_key";
    private final static float MIN_FONT_SCALE_SIZE = 0.5f;
    private final static float MAX_FONT_SCALE_SIZE = 1.1f;

    private InteractiveScrollView interactiveScrollView;
    private TextView megillaTextTv;
    private ImageButton showLawsBtn;
    private ImageButton rahashanBtn;
    private ImageButton gunFireBtn;
    private ImageButton granadeBtn;
    private TextView purimSongsBtn;
    private MediaPlayer mPlayer;
    private MediaPlayer mPlayer2;
    private MediaPlayer mPlayer3;
    private TextView blessingsTv;
    private ImageButton autoScrollBtn;
    private boolean isAutoScrollEnabled = false;
    private final Handler scrollHandler = new Handler();
    private static final float SCROLL_AMOUNT = 1f;
    private int scrollYButton;
    private long onCreateTimestamp;
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Pushwoosh.getInstance().registerForPushNotifications();
        onCreateTimestamp = System.currentTimeMillis();
        setContentView(R.layout.splash);
        initAd();
        setFontScale();
        initTopBar();
    }

    private void initTopBar() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.beige));
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setFontScale();
    }

    private void initAd() {
        MobileAds.initialize(this, initializationStatus -> Log.i(MOBILE_ADS_TAG, "onInitializationComplete"));

        AdRequest adRequest = new AdRequest.Builder().build();

        String adUnitId;
        if (BuildConfig.DEBUG) {
            adUnitId = getString(R.string.ad_unit_id_debug);
        } else {
            adUnitId = RemoteConfigManager.getInstance().getParameter(RemoteConfigManager.AD_UNIT_INTERSTITIAL_ID);
        }

        InterstitialAd.load(this, adUnitId, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                Log.i(MOBILE_ADS_TAG, "onAdLoaded");
                startInterstitialAdLogic();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Log.d(MOBILE_ADS_TAG, loadAdError.toString());
                mInterstitialAd = null;
                startInterstitialAdLogic();
            }
        });
    }

    private void startInterstitialAdLogic() {
        if (mInterstitialAd != null) {
            showInterstitialAd();
        } else {
            showMainScreen();
        }
    }

    private void showInterstitialAd() {
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(MOBILE_ADS_TAG, "Ad was clicked.");
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d(MOBILE_ADS_TAG, "Ad dismissed fullscreen content.");
                mInterstitialAd = null;
                startInterstitialAdLogic();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                // Called when ad fails to show.
                Log.e(MOBILE_ADS_TAG, "Ad failed to show fullscreen content.");
                mInterstitialAd = null;
                startInterstitialAdLogic();
            }

            @Override
            public void onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(MOBILE_ADS_TAG, "Ad recorded an impression.");
            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(MOBILE_ADS_TAG, "Ad showed fullscreen content.");
            }
        });

        if (mInterstitialAd != null) {
            mInterstitialAd.show(MainActivity.this);
        } else {
            Log.d(MOBILE_ADS_TAG, "The interstitial ad wasn't ready yet.");
        }
    }

    private void showMainScreen() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - onCreateTimestamp >= SPLASH_TIME_IN_MILLIS) {
            runOnUiThread(this::initMainActivity);
        } else {
            RelativeLayout splash_layout = this.findViewById(R.id.splash_layout);
            splash_layout.postDelayed(this::initMainActivity, currentTime - onCreateTimestamp);
        }
    }

    private void initMainActivity() {
        setContentView(R.layout.activity_main);
        initViews();
        initSounds();
        initListeners();
        initCustomTypeface();
        initMegillaText();
    }

    private void initViews() {
        purimSongsBtn = findViewById(R.id.purimSongsButton);
        blessingsTv = findViewById(R.id.blessingsTv);
        megillaTextTv = findViewById(R.id.myTextViewTv);
        interactiveScrollView = findViewById(R.id.myscrollview);
        showLawsBtn = findViewById(R.id.lawsBtn);
        rahashanBtn = findViewById(R.id.rahashanBtn);
        gunFireBtn = findViewById(R.id.gunfireBtn);
        granadeBtn = findViewById(R.id.grenadeBtn);
        autoScrollBtn = findViewById(R.id.autoScrollBtn);
    }


    private void initSounds() {
        mPlayer = MediaPlayer.create(MainActivity.this, R.raw.somenoise);
        mPlayer2 = MediaPlayer.create(MainActivity.this, R.raw.machinegun);
        mPlayer3 = MediaPlayer.create(MainActivity.this, R.raw.explosion);
    }

    private void initListeners() {

        purimSongsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SongsActivity.class);
            startActivity(intent);
        });
        blessingsTv.setOnClickListener(v -> showBlessingsDialog());

        interactiveScrollView.setOnBottomReachedListener(() -> {
            blessingsTv.setVisibility(View.VISIBLE);
            scrollYButton = interactiveScrollView.getScrollY();
        });

        interactiveScrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            int scrollY = interactiveScrollView.getScrollY(); //for verticalScrollView
            if (scrollY != 0) {

                if (scrollYButton == 0 || scrollY < scrollYButton) {
                    blessingsTv.setVisibility(View.GONE);
                } else {
                    blessingsTv.setVisibility(View.VISIBLE);
                }
            } else {
                blessingsTv.setVisibility(View.VISIBLE);
            }
        });

        showLawsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LawsOfPurimActivity.class);
            startActivity(intent);

        });

        rahashanBtn.setOnClickListener(v -> {
            if ((mPlayer.isPlaying())) {
                Log.e("shay", "playing");
            }
            mPlayer.start();
        });

        gunFireBtn.setOnClickListener(v -> {
            if ((mPlayer2.isPlaying())) {
                Log.e("shay", "playing");
            }
            mPlayer2.start();
        });


        granadeBtn.setOnClickListener(v -> {
            if ((mPlayer3.isPlaying())) {
                Log.e("shay", "playing");
            }
            mPlayer3.start();
        });

        autoScrollBtn.setOnClickListener(view -> {
            if (isAutoScrollEnabled) {
                autoScrollBtn.setImageResource(android.R.drawable.ic_media_play);
                isAutoScrollEnabled = false;
                scrollHandler.removeCallbacksAndMessages(null);
            } else {
                initScrollSpeedDialog();
            }
        });
    }

    private void initScrollSpeedDialog() {
        final String[] items = getResources().getStringArray(R.array.scroll_items);

        int lastCheckItem = Prefs.getInt(CHECKED_ITEM_KEY, -1);
        int checkedItem = lastCheckItem;
        if (lastCheckItem != -1) {
            checkedItem--;
        }
        final int[] checkedItems = {checkedItem};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getString(R.string.auto_scroll_dialog_title));
        builder.setSingleChoiceItems(getResources().getStringArray(R.array.scroll_items), checkedItem, (dialog, which) -> {
            String a = items[which];
            checkedItems[0] = which;
            char firstLetter = a.charAt(0);
            int scrollRate = Character.getNumericValue(firstLetter);
            Prefs.putInt(CHECKED_ITEM_KEY, scrollRate);
        });
        builder.setPositiveButton(getString(R.string.auto_scroll_dialog_confirm), (dialog, which) -> {
            // Handle the OK button
            if (Prefs.getInt(CHECKED_ITEM_KEY, -1) != -1) {
                autoScrollBtn.setImageResource(android.R.drawable.ic_media_pause);
                isAutoScrollEnabled = true;
                initAutoScroll(Prefs.getInt(CHECKED_ITEM_KEY, -1));
            }
        });
        builder.setNegativeButton(getString(R.string.auto_scroll_dialog_cancel), (dialog, which) -> {
            // Handle the Cancel button
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void initAutoScroll(int scrollRate) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (interactiveScrollView != null) {
                    interactiveScrollView.smoothScrollBy(0, (int) (SCROLL_AMOUNT * scrollRate));
                }
                scrollHandler.postDelayed(this, 30);
            }
        };
        scrollHandler.post(runnable);
    }


    private void showBlessingsDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog);
        builder1.setTitle(getString(R.string.blessings_dialog_title));
        builder1.setMessage(getResources().getString(R.string.blessings_before_the_reading) + "\n\n\n" + getResources().getString(R.string.blessings_after_the_reading));
        builder1.setCancelable(true);

        builder1.setPositiveButton(getString(R.string.blessings_dialog_close), (dialog, id) -> dialog.cancel());

        AlertDialog alert11 = builder1.create();
//        alert11.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert11.show();
    }

    private void initCustomTypeface() {
        Typeface typeface = ResourcesCompat.getFont(this, R.font.stam);
        megillaTextTv.setTypeface(typeface);
    }

    private void initMegillaText() {

        SpannableStringBuilder megillaTextBuilder = new SpannableStringBuilder(getString(R.string.first_chapter));
        megillaTextBuilder.append(getString(R.string.second_chapter));
        megillaTextBuilder.append(getString(R.string.third_chapter));
        megillaTextBuilder.append(getString(R.string.fourth_chapter));
        megillaTextBuilder.append(getString(R.string.fifth_chapter));
        megillaTextBuilder.append(getString(R.string.sixth_chapter));
        megillaTextBuilder.append(getString(R.string.seventh_chapter));
        megillaTextBuilder.append(getString(R.string.eighth_chapter));
        megillaTextBuilder.append(getString(R.string.ninth_chapter));
        megillaTextBuilder.append(getString(R.string.tenth_chapter));
        String megillaTextOriginal = megillaTextBuilder.toString();

        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "{א}", Color.BLACK, VERSE_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "{ב}", Color.BLACK, VERSE_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "{ג}", Color.BLACK, VERSE_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "{ד}", Color.BLACK, VERSE_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "{ה}", Color.BLACK, VERSE_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "{ו}", Color.BLACK, VERSE_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "{ז}", Color.BLACK, VERSE_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "{ח}", Color.BLACK, VERSE_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "{ט}", Color.BLACK, VERSE_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "{י}", Color.BLACK, VERSE_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "{יא}", Color.BLACK, VERSE_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "{יב}", Color.BLACK, VERSE_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "{יג}", Color.BLACK, VERSE_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "{יד}", Color.BLACK, VERSE_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "{טו}", Color.BLACK, VERSE_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "{טז}", Color.BLACK, VERSE_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "{יז}", Color.BLACK, VERSE_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "{יח}", Color.BLACK, VERSE_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "{יט}", Color.BLACK, VERSE_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "{כ}", Color.BLACK, VERSE_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "{כא}", Color.BLACK, VERSE_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "{כב}", Color.BLACK, VERSE_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "{כג}", Color.BLACK, VERSE_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "{כד}", Color.BLACK, VERSE_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "{כה}", Color.BLACK, VERSE_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "{כו}", Color.BLACK, VERSE_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "{כז}", Color.BLACK, VERSE_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "{כח}", Color.BLACK, VERSE_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "{כט}", Color.BLACK, VERSE_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "{ל}", Color.BLACK, VERSE_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "{לא}", Color.BLACK, VERSE_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "{לב}", Color.BLACK, VERSE_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "{לג}", Color.BLACK, VERSE_TEXT_SIZE);

        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "פרק א׳ ", Color.BLUE, CHAPTER_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "פרק ב׳ ", Color.BLUE, CHAPTER_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "פרק ג׳ ", Color.BLUE, CHAPTER_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "פרק ד׳ ", Color.BLUE, CHAPTER_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "פרק ה׳ ", Color.BLUE, CHAPTER_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "פרק ו׳ ", Color.BLUE, CHAPTER_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "פרק ז׳ ", Color.BLUE, CHAPTER_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "פרק ח׳ ", Color.BLUE, CHAPTER_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "פרק ט׳ ", Color.BLUE, CHAPTER_TEXT_SIZE);
        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "פרק י׳ ", Color.BLUE, CHAPTER_TEXT_SIZE);

        String[] hamanArray = getResources().getStringArray(R.array.haman_array);
        for (String haman : hamanArray) {
            megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, haman, getColor(R.color.red), 0);
        }

        megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, "(ס)", Color.BLACK, SAMECH_TEXT_SIZE);


        String[] sayingOutLoadVerses = getResources().getStringArray(R.array.saying_out_loud_verses);
        for (String verses : sayingOutLoadVerses) {
            megillaTextBuilder = colorSpecificText(megillaTextOriginal, megillaTextBuilder, verses, Color.BLUE, 0);
        }
        megillaTextTv.setText(megillaTextBuilder);
    }

    private SpannableStringBuilder colorSpecificText(String originalText, SpannableStringBuilder megillaTextBuilder, String textToColor, int color, float textSize) {
        int startIndex = originalText.indexOf(textToColor);
        while (startIndex >= 0) {
            int endIndex = startIndex + textToColor.length();
            megillaTextBuilder.setSpan(
                    new ForegroundColorSpan(color),
                    startIndex,
                    endIndex,
                    SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            if (textSize != 0) {
                megillaTextBuilder.setSpan(new RelativeSizeSpan(textSize), startIndex, endIndex, 0);
            }
            startIndex = originalText.indexOf(textToColor, endIndex);
        }
        return megillaTextBuilder;
    }


    protected void onResume() {
        super.onResume();
        if (isAutoScrollEnabled) {
            initAutoScroll(Prefs.getInt(CHECKED_ITEM_KEY, -1));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isAutoScrollEnabled && scrollHandler != null) {
            scrollHandler.removeCallbacksAndMessages(null);
        }
    }

    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setFontScale() {
        Configuration config = getResources().getConfiguration();
        float fontScale = config.fontScale;
        float newFontScale = config.fontScale;
        //0.85 small size, between stays like it is, 1.35 big
        if (fontScale < MIN_FONT_SCALE_SIZE) {
            newFontScale = MIN_FONT_SCALE_SIZE;
        } else if (fontScale > MAX_FONT_SCALE_SIZE) {
            newFontScale = MAX_FONT_SCALE_SIZE;
        }
        if (fontScale != newFontScale) {
            config.fontScale = newFontScale;
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            metrics.scaledDensity = config.fontScale * metrics.density;
            getBaseContext().getResources().updateConfiguration(config, metrics);
        }
    }
}