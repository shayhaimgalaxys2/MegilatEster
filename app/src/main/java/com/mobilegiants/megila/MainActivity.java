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
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;

public class MainActivity extends Activity implements SensorEventListener {


    private InteractiveScrollView scroll;
    private TextView tv;
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
                activity.setContentView(R.layout.activity_main);
                //mute on start
                try {
                    audioManager = (AudioManager) MainActivity.this.getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                } catch (Exception e) {

                    e.printStackTrace();
                }

                settingsBtn = (ImageButton) findViewById(R.id.settingsButton);
                settingsBtn.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);
                    }
                });

                blessingsTv = (TextView) findViewById(R.id.blessingsTv);
                blessingsTv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

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
                        alert11.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        alert11.show();
                    }
                });

                seekBar = (SeekBar) findViewById(R.id.seekbar1);
                seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                        final Handler h = new Handler();
                        final int delay = 1000; //milliseconds

                        h.postDelayed(new Runnable() {
                            public void run() {


                                scroll.scrollBy(0, intscroll);
                                if (intscroll != 0) {
                                    h.postDelayed(this, delay);
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


                tv = (TextView) findViewById(R.id.myTextViewTv);
                scroll = (InteractiveScrollView) findViewById(R.id.myscrollview);

                scroll.setOnBottomReachedListener(new InteractiveScrollView.OnBottomReachedListener() {
                    @Override
                    public void onBottomReached() {

                        blessingsTv.setVisibility(View.VISIBLE);
                        scrollYButton = scroll.getScrollY();
                    }
                });


                scroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

                    @Override
                    public void onScrollChanged() {

                        int scrollX = scroll.getScrollX(); //for horizontalScrollView
                        int scrollY = scroll.getScrollY(); //for verticalScrollView

                        if (scrollY != 0) {

                            if (scrollYButton==0||scrollY < scrollYButton) {
                                blessingsTv.setVisibility(View.INVISIBLE);
                            } else {
                                blessingsTv.setVisibility(View.VISIBLE);
                            }

                        } else {
                            blessingsTv.setVisibility(View.VISIBLE);
                        }


                    }
                });

                Object localObject = Typeface.createFromAsset(getAssets(), "ShlomoStam.ttf");
                tv.setTypeface((Typeface) localObject);

                showLawsBtn = (ImageButton) findViewById(R.id.lawsBtn);
                rahashanBtn = (ImageButton) findViewById(R.id.rahashanBtn);
                gunFireBtn = (ImageButton) findViewById(R.id.gunfireBtn);
                granadeBtn = (ImageButton) findViewById(R.id.grenadeBtn);

                SpannableString text = new SpannableString(getString(R.string.text));
                String text_original = getString(R.string.text);


                text.setSpan(new ForegroundColorSpan(Color.RED), 0, 2, 0);
                text.setSpan(new RelativeSizeSpan(1.15f), 0, 2, 0);

                for (int index = text_original.indexOf(" א "); index >= 0;
                     index = text_original.indexOf(" א ", index + 1)) {
                    text.setSpan(new ForegroundColorSpan(Color.RED), index, index + 2, 0);
                    text.setSpan(new RelativeSizeSpan(1.15f), index, index + 2, 0);
                }


                for (int index = text_original.indexOf(" ב "); index >= 0;
                     index = text_original.indexOf(" ב ", index + 1)) {
                    text.setSpan(new ForegroundColorSpan(Color.RED), index, index + 2, 0);
                    text.setSpan(new RelativeSizeSpan(1.15f), index, index + 2, 0);
                }
                for (int index = text_original.indexOf(" ג "); index >= 0;
                     index = text_original.indexOf(" ג ", index + 1)) {
                    text.setSpan(new ForegroundColorSpan(Color.RED), index, index + 2, 0);
                    text.setSpan(new RelativeSizeSpan(1.15f), index, index + 2, 0);
                }
                for (int index = text_original.indexOf(" ד "); index >= 0;
                     index = text_original.indexOf(" ד ", index + 1)) {
                    text.setSpan(new ForegroundColorSpan(Color.RED), index, index + 2, 0);
                    text.setSpan(new RelativeSizeSpan(1.15f), index, index + 2, 0);
                }

                for (int index = text_original.indexOf(" ד "); index >= 0;
                     index = text_original.indexOf(" ד ", index + 1)) {
                    text.setSpan(new ForegroundColorSpan(Color.RED), index, index + 2, 0);
                    text.setSpan(new RelativeSizeSpan(1.15f), index, index + 2, 0);
                }
                for (int index = text_original.indexOf(" ה "); index >= 0;
                     index = text_original.indexOf(" ה ", index + 1)) {
                    text.setSpan(new ForegroundColorSpan(Color.RED), index, index + 2, 0);
                    text.setSpan(new RelativeSizeSpan(1.15f), index, index + 2, 0);
                }
                for (int index = text_original.indexOf(" ו "); index >= 0;
                     index = text_original.indexOf(" ו ", index + 1)) {
                    text.setSpan(new ForegroundColorSpan(Color.RED), index, index + 2, 0);
                    text.setSpan(new RelativeSizeSpan(1.15f), index, index + 2, 0);
                }
                for (int index = text_original.indexOf(" ז "); index >= 0;
                     index = text_original.indexOf(" ז ", index + 1)) {
                    text.setSpan(new ForegroundColorSpan(Color.RED), index, index + 2, 0);
                    text.setSpan(new RelativeSizeSpan(1.15f), index, index + 2, 0);
                }
                for (int index = text_original.indexOf(" ח "); index >= 0;
                     index = text_original.indexOf(" ח ", index + 1)) {
                    text.setSpan(new ForegroundColorSpan(Color.RED), index, index + 2, 0);
                    text.setSpan(new RelativeSizeSpan(1.15f), index, index + 2, 0);
                }
                for (int index = text_original.indexOf(" ט "); index >= 0;
                     index = text_original.indexOf(" ט ", index + 1)) {
                    text.setSpan(new ForegroundColorSpan(Color.RED), index, index + 2, 0);
                    text.setSpan(new RelativeSizeSpan(1.15f), index, index + 2, 0);
                }

                for (int index = text_original.indexOf(" י "); index >= 0;
                     index = text_original.indexOf(" י ", index + 1)) {
                    text.setSpan(new ForegroundColorSpan(Color.RED), index, index + 2, 0);
                    text.setSpan(new RelativeSizeSpan(1.15f), index, index + 2, 0);
                }

                for (int index = text_original.indexOf(" יא "); index >= 0;
                     index = text_original.indexOf(" יא ", index + 1)) {
                    text.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
                    text.setSpan(new RelativeSizeSpan(1.15f), index, index + 4, 0);
                }
                for (int index = text_original.indexOf(" יב "); index >= 0;
                     index = text_original.indexOf(" יב ", index + 1)) {
                    text.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
                    text.setSpan(new RelativeSizeSpan(1.15f), index, index + 4, 0);
                }
                for (int index = text_original.indexOf(" יג "); index >= 0;
                     index = text_original.indexOf(" יג ", index + 1)) {
                    text.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
                    text.setSpan(new RelativeSizeSpan(1.15f), index, index + 4, 0);
                }
                for (int index = text_original.indexOf(" יד "); index >= 0;
                     index = text_original.indexOf(" יד ", index + 1)) {
                    text.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
                    text.setSpan(new RelativeSizeSpan(1.15f), index, index + 4, 0);
                }
                for (int index = text_original.indexOf(" טו "); index >= 0;
                     index = text_original.indexOf(" טו ", index + 1)) {
                    text.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
                    text.setSpan(new RelativeSizeSpan(1.15f), index, index + 4, 0);
                }
                for (int index = text_original.indexOf(" טז "); index >= 0;
                     index = text_original.indexOf(" טז ", index + 1)) {
                    text.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
                    text.setSpan(new RelativeSizeSpan(1.15f), index, index + 4, 0);
                }
                for (int index = text_original.indexOf(" יז "); index >= 0;
                     index = text_original.indexOf(" יז ", index + 1)) {
                    text.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
                    text.setSpan(new RelativeSizeSpan(1.15f), index, index + 4, 0);
                }
                for (int index = text_original.indexOf(" יח "); index >= 0;
                     index = text_original.indexOf(" יח ", index + 1)) {
                    text.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
                    text.setSpan(new RelativeSizeSpan(1.15f), index, index + 4, 0);
                }
                for (int index = text_original.indexOf(" יט "); index >= 0;
                     index = text_original.indexOf(" יט ", index + 1)) {
                    text.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
                    text.setSpan(new RelativeSizeSpan(1.15f), index, index + 4, 0);
                }
                for (int index = text_original.indexOf(" כ "); index >= 0;
                     index = text_original.indexOf(" כ ", index + 1)) {
                    text.setSpan(new ForegroundColorSpan(Color.RED), index, index + 2, 0);
                    text.setSpan(new RelativeSizeSpan(1.15f), index, index + 2, 0);
                }
                for (int index = text_original.indexOf(" כא "); index >= 0;
                     index = text_original.indexOf(" כא ", index + 1)) {
                    text.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
                    text.setSpan(new RelativeSizeSpan(1.15f), index, index + 4, 0);
                }
                for (int index = text_original.indexOf(" כב "); index >= 0;
                     index = text_original.indexOf(" כב ", index + 1)) {
                    text.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
                    text.setSpan(new RelativeSizeSpan(1.15f), index, index + 4, 0);
                }
                for (int index = text_original.indexOf(" כג "); index >= 0;
                     index = text_original.indexOf(" כג ", index + 1)) {
                    text.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
                    text.setSpan(new RelativeSizeSpan(1.15f), index, index + 4, 0);
                }
                for (int index = text_original.indexOf(" כד "); index >= 0;
                     index = text_original.indexOf(" כד ", index + 1)) {
                    text.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
                    text.setSpan(new RelativeSizeSpan(1.15f), index, index + 4, 0);
                }
                for (int index = text_original.indexOf(" כה "); index >= 0;
                     index = text_original.indexOf(" כה ", index + 1)) {
                    text.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
                    text.setSpan(new RelativeSizeSpan(1.15f), index, index + 4, 0);
                }
                for (int index = text_original.indexOf(" כו "); index >= 0;
                     index = text_original.indexOf(" כו ", index + 1)) {
                    text.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
                    text.setSpan(new RelativeSizeSpan(1.15f), index, index + 4, 0);
                }
                for (int index = text_original.indexOf(" כז "); index >= 0;
                     index = text_original.indexOf(" כז ", index + 1)) {
                    text.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
                    text.setSpan(new RelativeSizeSpan(1.15f), index, index + 4, 0);
                }
                for (int index = text_original.indexOf(" כח "); index >= 0;
                     index = text_original.indexOf(" כח ", index + 1)) {
                    text.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
                    text.setSpan(new RelativeSizeSpan(1.15f), index, index + 4, 0);
                }
                for (int index = text_original.indexOf(" כט "); index >= 0;
                     index = text_original.indexOf(" כט ", index + 1)) {
                    text.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
                    text.setSpan(new RelativeSizeSpan(1.15f), index, index + 4, 0);
                }
                for (int index = text_original.indexOf(" ל "); index >= 0;
                     index = text_original.indexOf(" ל ", index + 1)) {
                    text.setSpan(new ForegroundColorSpan(Color.RED), index, index + 2, 0);
                    text.setSpan(new RelativeSizeSpan(1.15f), index, index + 2, 0);
                }
                for (int index = text_original.indexOf(" לא "); index >= 0;
                     index = text_original.indexOf(" לא ", index + 1)) {
                    text.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
                    text.setSpan(new RelativeSizeSpan(1.15f), index, index + 4, 0);
                }
                for (int index = text_original.indexOf(" לב "); index >= 0;
                     index = text_original.indexOf(" לב ", index + 1)) {
                    text.setSpan(new ForegroundColorSpan(Color.RED), index, index + 4, 0);
                    text.setSpan(new RelativeSizeSpan(1.15f), index, index + 4, 0);
                }

                text.setSpan(new ForegroundColorSpan(Color.BLUE), text_original.indexOf(" פרק ב "), text_original.indexOf(" פרק ב ") + 6, 0);
                text.setSpan(new RelativeSizeSpan(1.15f), text_original.indexOf(" פרק ב "), text_original.indexOf(" פרק ב ") + 6, 0);

                text.setSpan(new ForegroundColorSpan(Color.BLUE), text_original.indexOf(" פרק ג "), text_original.indexOf(" פרק ג ") + 6, 0);
                text.setSpan(new RelativeSizeSpan(1.15f), text_original.indexOf(" פרק ג "), text_original.indexOf(" פרק ג ") + 6, 0);

                text.setSpan(new ForegroundColorSpan(Color.BLUE), text_original.indexOf(" פרק ד "), text_original.indexOf(" פרק ד ") + 6, 0);
                text.setSpan(new RelativeSizeSpan(1.15f), text_original.indexOf(" פרק ד "), text_original.indexOf(" פרק ד ") + 6, 0);

                text.setSpan(new ForegroundColorSpan(Color.BLUE), text_original.indexOf(" פרק ה "), text_original.indexOf(" פרק ה ") + 6, 0);
                text.setSpan(new RelativeSizeSpan(1.15f), text_original.indexOf(" פרק ה "), text_original.indexOf(" פרק ה ") + 6, 0);

                text.setSpan(new ForegroundColorSpan(Color.BLUE), text_original.indexOf(" פרק ו "), text_original.indexOf(" פרק ו ") + 6, 0);
                text.setSpan(new RelativeSizeSpan(1.15f), text_original.indexOf(" פרק ו "), text_original.indexOf(" פרק ו ") + 6, 0);

                text.setSpan(new ForegroundColorSpan(Color.BLUE), text_original.indexOf(" פרק ז "), text_original.indexOf(" פרק ז ") + 6, 0);
                text.setSpan(new RelativeSizeSpan(1.15f), text_original.indexOf(" פרק ז "), text_original.indexOf(" פרק ז ") + 6, 0);

                text.setSpan(new ForegroundColorSpan(Color.BLUE), text_original.indexOf(" פרק ח "), text_original.indexOf(" פרק ח ") + 6, 0);
                text.setSpan(new RelativeSizeSpan(1.15f), text_original.indexOf(" פרק ח "), text_original.indexOf(" פרק ח ") + 6, 0);

                text.setSpan(new ForegroundColorSpan(Color.BLUE), text_original.indexOf(" פרק ט "), text_original.indexOf(" פרק ט ") + 6, 0);
                text.setSpan(new RelativeSizeSpan(1.15f), text_original.indexOf(" פרק ט "), text_original.indexOf(" פרק ט ") + 6, 0);

                text.setSpan(new ForegroundColorSpan(Color.BLUE), text_original.indexOf(" פרק י "), text_original.indexOf(" פרק י ") + 6, 0);
                text.setSpan(new RelativeSizeSpan(1.15f), text_original.indexOf(" פרק י "), text_original.indexOf(" פרק י ") + 6, 0);


                //tv.setText(text);

                tv.setText(text);


                showLawsBtn.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, Laws_of_purim.class);
                        startActivity(intent);

                    }
                });
                mPlayer = MediaPlayer.create(MainActivity.this, R.raw.somenoise);

                rahashanBtn.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if ((mPlayer.isPlaying())) {
                            Log.e("shay", "playing");
                        }
                        mPlayer.start();
                    }
                });
                mPlayer2 = MediaPlayer.create(MainActivity.this, R.raw.machinegun);
                gunFireBtn.setOnClickListener(new OnClickListener() {


                    @Override
                    public void onClick(View v) {
                        if ((mPlayer2.isPlaying())) {
                            Log.e("shay", "playing");
                        }
                        mPlayer2.start();
                    }
                });


                mPlayer3 = MediaPlayer.create(MainActivity.this, R.raw.explosion);
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
        }, 4000);

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
        // can be safely ignored for this demo
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if (isShakeOn) {
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
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        try {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}

