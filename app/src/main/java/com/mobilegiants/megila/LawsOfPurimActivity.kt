package com.mobilegiants.megila

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class LawsOfPurimActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_laws_of_purim)
        } catch (e: Exception) {
            e.printStackTrace()
            setContentView(R.layout.activity_laws_of_purim_2)
        }
    }
}
