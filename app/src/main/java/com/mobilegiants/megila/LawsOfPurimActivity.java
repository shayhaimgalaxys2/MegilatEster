package com.mobilegiants.megila;

import android.app.Activity;
import android.os.Bundle;

public class LawsOfPurimActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			setContentView(R.layout.activity_laws_of_purim);
		} catch (Exception e) {

			e.printStackTrace();
			setContentView(R.layout.activity_laws_of_purim_2);
		}
	}

	

}
