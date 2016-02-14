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
	private SharedPreferences sharedpreferences;
	private Button purimSongsBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		purimSongsBtn = (Button) findViewById(R.id.purimSongsBtn);
		switchBtn = (ToggleButton) findViewById(R.id.switchBtn); 
		sharedpreferences = getSharedPreferences(MyPREFERENCES, SettingsActivity.MODE_PRIVATE);
		
		purimSongsBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SettingsActivity.this, Songs.class);
				startActivity(intent);
			}
		});
		
		
	}
	
    protected void onResume() {
        super.onResume();
    	SharedPreferences sp = getSharedPreferences(MyPREFERENCES, SettingsActivity.MODE_PRIVATE);
    	boolean temp1 = sp.getBoolean("isSwitchOn", true);
    	if (temp1) {
    		switchBtn.setChecked(true);
		}else {
			switchBtn.setChecked(false);
		}
    }


	public void onToggleClicked(View view) {
	    // Is the toggle on?
	    boolean on = ((ToggleButton) view).isChecked();
	    
	    if (on) {
	        // Enable vibrate
	    	Editor editor = sharedpreferences.edit();
	    	editor.putBoolean("isSwitchOn", true);
	    	editor.commit();
//	    	SharedPreferences sp = getSharedPreferences(MyPREFERENCES, SettingsActivity.MODE_PRIVATE);
//	    	boolean temp = sp.getBoolean("isSwitchOn", true);
//	    	String tempS = String.valueOf(temp);
//	    	Toast.makeText(getApplicationContext(),String.valueOf(tempS), Toast.LENGTH_SHORT).show();
	    } else {
	        // Disable vibrate
	    	Editor editor = sharedpreferences.edit();
	    	editor.putBoolean("isSwitchOn", false);
	    	editor.commit();
//	    	SharedPreferences sp = getSharedPreferences(MyPREFERENCES, SettingsActivity.MODE_PRIVATE);
//	    	boolean temp = sp.getBoolean("isSwitchOn", true);
//	    	String tempS = String.valueOf(temp);
//	    	Toast.makeText(getApplicationContext(),String.valueOf(tempS), Toast.LENGTH_SHORT).show();
	    }
	}

}
