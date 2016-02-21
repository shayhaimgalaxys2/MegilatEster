package com.mobilegiants.megila;

import java.util.ArrayList;

import android.app.ListActivity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class Songs extends ListActivity {

    private MediaPlayer mPlayer;
    private AudioManager audioManager;
    private Button stopBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_songs);
        initViews();
        initListeners();
        initAdapter();
        initListOnItemClickListener();
    }


    private void initViews() {

        stopBtn = (Button) findViewById(R.id.stopBtn);
    }

    private void initListeners() {
        stopBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (mPlayer.isPlaying()) {
                    stopBtn.setText("Play");
                    mPlayer.pause();
                } else {
                    stopBtn.setText("Pause");
                    mPlayer.start();
                }


            }
        });
    }

    private void initAdapter() {
        ArrayList<String> songs = new ArrayList<String>();
        songs.add("משנכנס אדר");
        songs.add("ונהפוך הוא");
        songs.add("ליהודים הייתה");
        songs.add("חג פורים");
        songs.add("ומרדכי יצא");
        songs.add("חייב איניש");
        songs.add("מחרוזת");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, songs);

        setListAdapter(adapter);

    }

    private void initListOnItemClickListener() {

        ListView songsList = getListView();

        songsList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String song = (String) parent.getItemAtPosition(position);
                if (mPlayer != null) {
                    mPlayer.stop();
                }


                if (song.equals("משנכנס אדר")) {
                    mPlayer = MediaPlayer.create(Songs.this, R.raw.song1);
                    mPlayer.start();
                } else if (song.equals("ונהפוך הוא")) {
                    mPlayer = MediaPlayer.create(Songs.this, R.raw.song2);
                    mPlayer.start();
                } else if (song.equals("ליהודים הייתה")) {
                    mPlayer = MediaPlayer.create(Songs.this, R.raw.song3);
                    mPlayer.start();
                } else if (song.equals("חג פורים")) {
                    mPlayer = MediaPlayer.create(Songs.this, R.raw.song4);
                    mPlayer.start();
                } else if (song.equals("ומרדכי יצא")) {
                    mPlayer = MediaPlayer.create(Songs.this, R.raw.song5);
                    mPlayer.start();
                } else if (song.equals("חייב איניש")) {
                    mPlayer = MediaPlayer.create(Songs.this, R.raw.song6);
                    mPlayer.start();
                } else if (song.equals("מחרוזת")) {
                    mPlayer = MediaPlayer.create(Songs.this, R.raw.song7);
                    mPlayer.start();
                }
            }

        });
    }


}
