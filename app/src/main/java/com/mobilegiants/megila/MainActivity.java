package com.mobilegiants.megila;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends Activity implements SensorEventListener {


    private static final long SPLASH_TIME_IN_MILLIES = 4000;
    public static final float TEXT_SIZE = 1.15f;

    private InteractiveScrollView interactiveScrollView;
    private TextView megilaTextTv;
    private ImageButton showLawsBtn;
    private ImageButton rahashanBtn;
    private ImageButton gunFireBtn;
    private ImageButton granadeBtn;
    private ImageButton settingsBtn;
    private MediaPlayer mPlayer;
    private MediaPlayer mPlayer2;
    private MediaPlayer mPlayer3;
    private AudioManager audioManager;
    private Activity activity;
    private int intscroll;
    private SeekBar seekBar;
    private TextView blessingsTv;
    private static final String MyPREFERENCES = "myPreference";
    private SharedPreferences sharedpreferences;
    private boolean isShakeOn;

    private float mAccelCurrent, mAccelLast, mAccel;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private int oneTwoThree;
    private int scrollYButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        activity = this;
        RelativeLayout splash_layout = (RelativeLayout) this.findViewById(R.id.splash_layout);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, MainActivity.MODE_PRIVATE);

        splash_layout.postDelayed(new Runnable() {
            public void run() {
                initMainActivity();
            }
        }, SPLASH_TIME_IN_MILLIES);

    }


    private void initMainActivity() {
        activity.setContentView(R.layout.activity_main);

        initSilentPhone();
        initViews();
        initSounds();
        initListeners();
        initCustomeTypeface();
        initMegillaText();
    }


    private void initSilentPhone() {
        try {
            audioManager = (AudioManager) MainActivity.this.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void initViews() {

        settingsBtn = (ImageButton) findViewById(R.id.settingsButton);
        blessingsTv = (TextView) findViewById(R.id.blessingsTv);
        seekBar = (SeekBar) findViewById(R.id.seekbar1);
        megilaTextTv = (TextView) findViewById(R.id.myTextViewTv);
        interactiveScrollView = (InteractiveScrollView) findViewById(R.id.myscrollview);
        showLawsBtn = (ImageButton) findViewById(R.id.lawsBtn);
        rahashanBtn = (ImageButton) findViewById(R.id.rahashanBtn);
        gunFireBtn = (ImageButton) findViewById(R.id.gunfireBtn);
        granadeBtn = (ImageButton) findViewById(R.id.grenadeBtn);

    }


    private void initSounds() {
        mPlayer = MediaPlayer.create(MainActivity.this, R.raw.somenoise);
        mPlayer2 = MediaPlayer.create(MainActivity.this, R.raw.machinegun);
        mPlayer3 = MediaPlayer.create(MainActivity.this, R.raw.explosion);

    }

    private void initListeners() {

        settingsBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
        blessingsTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                showBlessingsDialog();
            }


        });

        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                final Handler handler = new Handler();
                final int delay = 1000; //milliseconds

                handler.postDelayed(new Runnable() {
                    public void run() {


                        interactiveScrollView.scrollBy(0, intscroll);
                        if (intscroll != 0) {
                            handler.postDelayed(this, delay);
                        }
                    }
                }, delay);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {

                intscroll = (int) (progress * 2.5);
            }
        });


        interactiveScrollView.setOnBottomReachedListener(new InteractiveScrollView.OnBottomReachedListener() {
            @Override
            public void onBottomReached() {

                blessingsTv.setVisibility(View.VISIBLE);
                scrollYButton = interactiveScrollView.getScrollY();
            }
        });

        interactiveScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

            @Override
            public void onScrollChanged() {

                int scrollX = interactiveScrollView.getScrollX(); //for horizontalScrollView
                int scrollY = interactiveScrollView.getScrollY(); //for verticalScrollView

                if (scrollY != 0) {

                    if (scrollYButton == 0 || scrollY < scrollYButton) {
                        blessingsTv.setVisibility(View.INVISIBLE);
                    } else {
                        blessingsTv.setVisibility(View.VISIBLE);
                    }

                } else {
                    blessingsTv.setVisibility(View.VISIBLE);
                }


            }
        });

        showLawsBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Laws_of_purim.class);
                startActivity(intent);

            }
        });

        rahashanBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if ((mPlayer.isPlaying())) {
                    Log.e("shay", "playing");
                }
                mPlayer.start();
            }
        });

        gunFireBtn.setOnClickListener(new OnClickListener() {


            @Override
            public void onClick(View v) {
                if ((mPlayer2.isPlaying())) {
                    Log.e("shay", "playing");
                }
                mPlayer2.start();
            }
        });


        granadeBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if ((mPlayer3.isPlaying())) {
                    Log.e("shay", "playing");
                }
                mPlayer3.start();
            }
        });

    }

    private void showBlessingsDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog);
        builder1.setTitle("ברכות המגילה");
        builder1.setMessage(getResources().getString(R.string.blessings_before_the_reading) + "\n\n\n" + getResources().getString(R.string.blessings_after_the_reading));
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Close",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert11.show();
    }

    private void initCustomeTypeface() {
        Object localObject = Typeface.createFromAsset(getAssets(), "ShlomoStam.ttf");
        megilaTextTv.setTypeface((Typeface) localObject);
    }

    private void initMegillaText() {

        SpannableString megillaText = new SpannableString(getString(R.string.text));
        String megilaTextOriginal = getString(R.string.text);

        megillaText.setSpan(new ForegroundColorSpan(Color.RED), 0, 2, 0);
        megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), 0, 2, 0);


        for (int index = megilaTextOriginal.indexOf(" א "); index >= 0;
             index = megilaTextOriginal.indexOf(" א ", index + 1)) {
            megillaText.setSpan(new ForegroundColorSpan(Color.RED), index, index + 2, 0);
            megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), index, index + 2, 0);
        }

        for (int index = megilaTextOriginal.indexOf(" ב "); index >= 0;
             index = megilaTextOriginal.indexOf(" ב ", index + 1)) {
            megillaText.setSpan(new ForegroundColorSpan(Color.RED), index, index + 2, 0);
            megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), index, index + 2, 0);
        }
        for (int index = megilaTextOriginal.indexOf(" ג "); index >= 0;
             index = megilaTextOriginal.indexOf(" ג ", index + 1)) {
            megillaText.setSpan(new ForegroundColorSpan(Color.RED), index, index + 2, 0);
            megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), index, index + 2, 0);
        }
        for (int index = megilaTextOriginal.indexOf(" ד "); index >= 0;
             index = megilaTextOriginal.indexOf(" ד ", index + 1)) {
            megillaText.setSpan(new ForegroundColorSpan(Color.RED), index, index + 2, 0);
            megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), index, index + 2, 0);
        }

        for (int index = megilaTextOriginal.indexOf(" ד "); index >= 0;
             index = megilaTextOriginal.indexOf(" ד ", index + 1)) {
            megillaText.setSpan(new ForegroundColorSpan(Color.RED), index, index + 2, 0);
            megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), index, index + 2, 0);
        }
        for (int index = megilaTextOriginal.indexOf(" ה "); index >= 0;
             index = megilaTextOriginal.indexOf(" ה ", index + 1)) {
            megillaText.setSpan(new ForegroundColorSpan(Color.RED), index, index + 2, 0);
            megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), index, index + 2, 0);
        }
        for (int index = megilaTextOriginal.indexOf(" ו "); index >= 0;
             index = megilaTextOriginal.indexOf(" ו ", index + 1)) {
            megillaText.setSpan(new ForegroundColorSpan(Color.RED), index, index + 2, 0);
            megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), index, index + 2, 0);
        }
        for (int index = megilaTextOriginal.indexOf(" ז "); index >= 0;
             index = megilaTextOriginal.indexOf(" ז ", index + 1)) {
            megillaText.setSpan(new ForegroundColorSpan(Color.RED), index, index + 2, 0);
            megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), index, index + 2, 0);
        }
        for (int index = megilaTextOriginal.indexOf(" ח "); index >= 0;
             index = megilaTextOriginal.indexOf(" ח ", index + 1)) {
            megillaText.setSpan(new ForegroundColorSpan(Color.RED), index, index + 2, 0);
            megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), index, index + 2, 0);
        }
        for (int index = megilaTextOriginal.indexOf(" ט "); index >= 0;
             index = megilaTextOriginal.indexOf(" ט ", index + 1)) {
            megillaText.setSpan(new ForegroundColorSpan(Color.RED), index, index + 2, 0);
            megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), index, index + 2, 0);
        }

        for (int index = megilaTextOriginal.indexOf(" י "); index >= 0;
             index = megilaTextOriginal.indexOf(" י ", index + 1)) {
            megillaText.setSpan(new ForegroundColorSpan(Color.RED), index, index + 2, 0);
            megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), index, index + 2, 0);
        }

        for (int index = megilaTextOriginal.indexOf(" יא "); index >= 0;
             index = megilaTextOriginal.indexOf(" יא ", index + 1)) {
            megillaText.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
            megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), index, index + 4, 0);
        }
        for (int index = megilaTextOriginal.indexOf(" יב "); index >= 0;
             index = megilaTextOriginal.indexOf(" יב ", index + 1)) {
            megillaText.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
            megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), index, index + 4, 0);
        }
        for (int index = megilaTextOriginal.indexOf(" יג "); index >= 0;
             index = megilaTextOriginal.indexOf(" יג ", index + 1)) {
            megillaText.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
            megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), index, index + 4, 0);
        }
        for (int index = megilaTextOriginal.indexOf(" יד "); index >= 0;
             index = megilaTextOriginal.indexOf(" יד ", index + 1)) {
            megillaText.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
            megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), index, index + 4, 0);
        }
        for (int index = megilaTextOriginal.indexOf(" טו "); index >= 0;
             index = megilaTextOriginal.indexOf(" טו ", index + 1)) {
            megillaText.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
            megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), index, index + 4, 0);
        }
        for (int index = megilaTextOriginal.indexOf(" טז "); index >= 0;
             index = megilaTextOriginal.indexOf(" טז ", index + 1)) {
            megillaText.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
            megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), index, index + 4, 0);
        }
        for (int index = megilaTextOriginal.indexOf(" יז "); index >= 0;
             index = megilaTextOriginal.indexOf(" יז ", index + 1)) {
            megillaText.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
            megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), index, index + 4, 0);
        }
        for (int index = megilaTextOriginal.indexOf(" יח "); index >= 0;
             index = megilaTextOriginal.indexOf(" יח ", index + 1)) {
            megillaText.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
            megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), index, index + 4, 0);
        }
        for (int index = megilaTextOriginal.indexOf(" יט "); index >= 0;
             index = megilaTextOriginal.indexOf(" יט ", index + 1)) {
            megillaText.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
            megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), index, index + 4, 0);
        }
        for (int index = megilaTextOriginal.indexOf(" כ "); index >= 0;
             index = megilaTextOriginal.indexOf(" כ ", index + 1)) {
            megillaText.setSpan(new ForegroundColorSpan(Color.RED), index, index + 2, 0);
            megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), index, index + 2, 0);
        }
        for (int index = megilaTextOriginal.indexOf(" כא "); index >= 0;
             index = megilaTextOriginal.indexOf(" כא ", index + 1)) {
            megillaText.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
            megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), index, index + 4, 0);
        }
        for (int index = megilaTextOriginal.indexOf(" כב "); index >= 0;
             index = megilaTextOriginal.indexOf(" כב ", index + 1)) {
            megillaText.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
            megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), index, index + 4, 0);
        }
        for (int index = megilaTextOriginal.indexOf(" כג "); index >= 0;
             index = megilaTextOriginal.indexOf(" כג ", index + 1)) {
            megillaText.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
            megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), index, index + 4, 0);
        }
        for (int index = megilaTextOriginal.indexOf(" כד "); index >= 0;
             index = megilaTextOriginal.indexOf(" כד ", index + 1)) {
            megillaText.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
            megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), index, index + 4, 0);
        }
        for (int index = megilaTextOriginal.indexOf(" כה "); index >= 0;
             index = megilaTextOriginal.indexOf(" כה ", index + 1)) {
            megillaText.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
            megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), index, index + 4, 0);
        }
        for (int index = megilaTextOriginal.indexOf(" כו "); index >= 0;
             index = megilaTextOriginal.indexOf(" כו ", index + 1)) {
            megillaText.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
            megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), index, index + 4, 0);
        }
        for (int index = megilaTextOriginal.indexOf(" כז "); index >= 0;
             index = megilaTextOriginal.indexOf(" כז ", index + 1)) {
            megillaText.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
            megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), index, index + 4, 0);
        }
        for (int index = megilaTextOriginal.indexOf(" כח "); index >= 0;
             index = megilaTextOriginal.indexOf(" כח ", index + 1)) {
            megillaText.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
            megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), index, index + 4, 0);
        }
        for (int index = megilaTextOriginal.indexOf(" כט "); index >= 0;
             index = megilaTextOriginal.indexOf(" כט ", index + 1)) {
            megillaText.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
            megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), index, index + 4, 0);
        }
        for (int index = megilaTextOriginal.indexOf(" ל "); index >= 0;
             index = megilaTextOriginal.indexOf(" ל ", index + 1)) {
            megillaText.setSpan(new ForegroundColorSpan(Color.RED), index, index + 2, 0);
            megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), index, index + 2, 0);
        }
        for (int index = megilaTextOriginal.indexOf(" לא "); index >= 0;
             index = megilaTextOriginal.indexOf(" לא ", index + 1)) {
            megillaText.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
            megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), index, index + 4, 0);
        }
        for (int index = megilaTextOriginal.indexOf(" לב "); index >= 0;
             index = megilaTextOriginal.indexOf(" לב ", index + 1)) {
            megillaText.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
            megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), index, index + 4, 0);
        }

        megillaText.setSpan(new ForegroundColorSpan(Color.BLUE), megilaTextOriginal.indexOf(" פרק ב "), megilaTextOriginal.indexOf(" פרק ב ") + 6, 0);
        megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), megilaTextOriginal.indexOf(" פרק ב "), megilaTextOriginal.indexOf(" פרק ב ") + 6, 0);

        megillaText.setSpan(new ForegroundColorSpan(Color.BLUE), megilaTextOriginal.indexOf(" פרק ג "), megilaTextOriginal.indexOf(" פרק ג ") + 6, 0);
        megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), megilaTextOriginal.indexOf(" פרק ג "), megilaTextOriginal.indexOf(" פרק ג ") + 6, 0);

        megillaText.setSpan(new ForegroundColorSpan(Color.BLUE), megilaTextOriginal.indexOf(" פרק ד "), megilaTextOriginal.indexOf(" פרק ד ") + 6, 0);
        megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), megilaTextOriginal.indexOf(" פרק ד "), megilaTextOriginal.indexOf(" פרק ד ") + 6, 0);

        megillaText.setSpan(new ForegroundColorSpan(Color.BLUE), megilaTextOriginal.indexOf(" פרק ה "), megilaTextOriginal.indexOf(" פרק ה ") + 6, 0);
        megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), megilaTextOriginal.indexOf(" פרק ה "), megilaTextOriginal.indexOf(" פרק ה ") + 6, 0);

        megillaText.setSpan(new ForegroundColorSpan(Color.BLUE), megilaTextOriginal.indexOf(" פרק ו "), megilaTextOriginal.indexOf(" פרק ו ") + 6, 0);
        megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), megilaTextOriginal.indexOf(" פרק ו "), megilaTextOriginal.indexOf(" פרק ו ") + 6, 0);

        megillaText.setSpan(new ForegroundColorSpan(Color.BLUE), megilaTextOriginal.indexOf(" פרק ז "), megilaTextOriginal.indexOf(" פרק ז ") + 6, 0);
        megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), megilaTextOriginal.indexOf(" פרק ז "), megilaTextOriginal.indexOf(" פרק ז ") + 6, 0);

        megillaText.setSpan(new ForegroundColorSpan(Color.BLUE), megilaTextOriginal.indexOf(" פרק ח "), megilaTextOriginal.indexOf(" פרק ח ") + 6, 0);
        megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), megilaTextOriginal.indexOf(" פרק ח "), megilaTextOriginal.indexOf(" פרק ח ") + 6, 0);

        megillaText.setSpan(new ForegroundColorSpan(Color.BLUE), megilaTextOriginal.indexOf(" פרק ט "), megilaTextOriginal.indexOf(" פרק ט ") + 6, 0);
        megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), megilaTextOriginal.indexOf(" פרק ט "), megilaTextOriginal.indexOf(" פרק ט ") + 6, 0);

        megillaText.setSpan(new ForegroundColorSpan(Color.BLUE), megilaTextOriginal.indexOf(" פרק י "), megilaTextOriginal.indexOf(" פרק י ") + 6, 0);
        megillaText.setSpan(new RelativeSizeSpan(TEXT_SIZE), megilaTextOriginal.indexOf(" פרק י "), megilaTextOriginal.indexOf(" פרק י ") + 6, 0);
        megilaTextTv.setText(megillaText);


    }


    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

//        check if this the first run
        if (sharedpreferences.getBoolean("firstrun", true)) {
            Editor editor = sharedpreferences.edit();
            editor.putBoolean("isSwitchOn", true);
            editor.commit();
            sharedpreferences.edit().putBoolean("firstrun", false).commit();
        }
        SharedPreferences sp = getSharedPreferences(MyPREFERENCES, SettingsActivity.MODE_PRIVATE);
        isShakeOn = sp.getBoolean("isSwitchOn", true);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if (isShakeOn) {
            initShakeEvent(event);
        }
    }

    private void initShakeEvent(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta; // perform low-cut filter

        Random random = new Random();
        oneTwoThree = random.nextInt(3) + 1;
        if (mAccel > 12) {
            switch (oneTwoThree) {
                case 1:
                    mPlayer = MediaPlayer.create(MainActivity.this, R.raw.somenoise);
                    if (!(mPlayer3.isPlaying()) || (mPlayer2.isPlaying()) || (mPlayer.isPlaying())) {
                        mPlayer.start();
                    }
                    break;
                case 2:
                    mPlayer2 = MediaPlayer.create(MainActivity.this, R.raw.machinegun);
                    if (!(mPlayer3.isPlaying()) || (mPlayer2.isPlaying()) || (mPlayer.isPlaying())) {
                        mPlayer2.start();
                    }
                    break;
                case 3:
                    mPlayer3 = MediaPlayer.create(MainActivity.this, R.raw.explosion);
                    if (!(mPlayer3.isPlaying()) || (mPlayer2.isPlaying()) || (mPlayer.isPlaying())) {
                        mPlayer3.start();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        setNormatRingMode();
    }

    private void setNormatRingMode() {
        try {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

