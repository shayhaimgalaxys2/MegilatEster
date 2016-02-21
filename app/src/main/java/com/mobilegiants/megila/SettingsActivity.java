package com.mobilegiants.megila;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;

public class SettingsActivity extends Activity {

    private static final String MyPREFERENCES = "myPreference";

    private ToggleButton switchBtn;
    private Button purimSongsBtn;
    private SharedPreferences sharedpreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initViews();
        initSharePrefences();
        initListeners();


    }


    private void initViews() {

        purimSongsBtn = (Button) findViewById(R.id.purimSongsBtn);
        switchBtn = (ToggleButton) findViewById(R.id.switchBtn);
    }

    private void initSharePrefences() {
        sharedpreferences = getSharedPreferences(MyPREFERENCES, SettingsActivity.MODE_PRIVATE);
    }

    private void initListeners() {
        purimSongsBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SettingsActivity.this, Songs.class);
                startActivity(intent);
            }
        });

        switchBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onToggleClicked(v);
            }
        });
    }

    protected void onResume() {
        super.onResume();
        SharedPreferences sp = getSharedPreferences(MyPREFERENCES, SettingsActivity.MODE_PRIVATE);
        boolean temp1 = sp.getBoolean("isSwitchOn", true);
        if (temp1) {
            switchBtn.setChecked(true);
        } else {
            switchBtn.setChecked(false);
        }
    }


    public void onToggleClicked(View view) {

        boolean on = ((ToggleButton) view).isChecked();
        if (on) {
            Editor editor = sharedpreferences.edit();
            editor.putBoolean("isSwitchOn", true);
            editor.commit();
        } else {
            Editor editor = sharedpreferences.edit();
            editor.putBoolean("isSwitchOn", false);
            editor.commit();
        }
    }

}
