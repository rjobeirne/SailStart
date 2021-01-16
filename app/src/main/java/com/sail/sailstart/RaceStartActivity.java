package com.sail.sailstart;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class RaceStartActivity extends Activity {





    private TextView mCourseTextView;
    private TextView mNextMarkTextView;
    private TextView mSpeedTextView;
    private TextView mDistanceTextView;
    private TextView mHeadingTextView;
    private TextView mBearingTextView;
    private TextView mClockTextView;

    private String raceCourse = "Green 1";
    private String nextMark = "Start";
    private String speedDisplay = "2.5";
    private String displayDistToMark = "1.0";
    private String displayHeading = "355";
    private int displayBearingToMark;


    long timeToStart = 70 ;//* 60;
    public Boolean timerStarted = false;
    Boolean resetClock =false;
    CountDownTimer startClock;
    private Long clock;
    private String clockDisplay;
    double secsLeft;

    public MediaPlayer mediaPlayer;
    String sound;

    int posMark = 0;
    int listMarkSize;


    String nextMarkFull;
    ArrayList courseMarks;

    Marks theMarks = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Log.e("StartActivity initiated", "");

        mCourseTextView = (TextView) findViewById(R.id.course_name);
        mNextMarkTextView = (TextView) findViewById(R.id.next_mark_name);
        mSpeedTextView = (TextView) findViewById(R.id.speed_text);
        mDistanceTextView = (TextView) findViewById(R.id.distance_text);
        mHeadingTextView = (TextView) findViewById(R.id.heading_text);
        mBearingTextView = (TextView) findViewById(R.id.bearing_text);
        mClockTextView = (TextView) findViewById(R.id.time_to_start);


        theMarks = new Marks();


        // Create the ArrayList in the constructor, so only done once
        try {
            theMarks.parseXML();
        } catch (IOException e) {
            e.printStackTrace();
        }

// Establish the Intent, and retrieve the parameters here
        Intent startRace = getIntent();
        if (startRace != null) {

//            raceCourse = getIntent().getStringExtra("course");
//            nextMark = getIntent().getStringExtra("mark");
//            speedDisplay = getIntent().getStringExtra("speed");
//            displayDistToMark = getIntent().getStringExtra("distance");
//            displayHeading = getIntent().getStringExtra("heading");
//            displayBearingToMark = getIntent().getIntExtra("bearing", 0);
//            courseMarks = getIntent().getStringArrayListExtra("courseMarks");
//            posMark = getIntent().getIntExtra("markPos", 0);

        }

//        if (mStartData != null) {
//        displayBearingToMark = getIntent().getExtras().getInt("bearing");
//        raceCourse = mStartData.getStringExtra("course");
//        int displayBearingToMark = start.getIntExtra(bearingToMark);
//        Bundle startData = startRace.getExtras();
//        Log.e("in start", String.valueOf(startData));
//        raceCourse = startData.getString("course");
//        displayBearingToMark = startData.getInt("bearing");

//        int bearingToMark = displayBearingToMark.getIntExtra("bearingToMark", 0);
//
//        if( getIntent().getExtras() != null) {
//            raceCourse = getIntent().getExtras().getString("course");
//

//        Log.e("in start", String.valueOf(raceCourse) + " :" + nextMark);
//        Log.e("in start", String.valueOf(speedDisplay) + " :" + displayDistToMark);

        mCourseTextView.setText(raceCourse);
        mNextMarkTextView.setText(nextMark);
        mSpeedTextView.setText(speedDisplay);
        mDistanceTextView.setText(displayDistToMark);
        mHeadingTextView.setText(displayHeading);
        mBearingTextView.setText(String.format("%03d", displayBearingToMark));


        showClock(timeToStart);
    }




//        public void previous_markS(View view) {
//            Log.d("Prev mark pushed", String.valueOf(posMar           double secsLeftk));
//                // Decrement to the position of the previous mark on the list
//                if (posMark <= 0) {
//                    posMark = listMarkSize - 1;
//                } else
//                    posMark = posMark - 1;
//
//            setNextMarkS();
//        }
//
//         /**
//         *  Set next destination mark
//         */
//        private void setNextMarkS() {
//
//                listMarkSize = courseMarks.size();
//                nextMark = (String) courseMarks.get(posMark);
//                Log.i("Start-next", nextMark);
//
//            if (nextMark.length() == 1){
//                nextMarkFull = nextMark + " Mark";
//            } else {
//                nextMarkFull = nextMark;
//            }
//
//            mNextMarkTextView.setText(nextMarkFull);
////            finish();
//        }

    public void time_plus(View view) {
            if (timerStarted) {
                resetClock = true;
                timeToStart = clock + 60;
                countdown();
            } else {
                timeToStart = timeToStart + 60;
                showClock(timeToStart);
            }

    }

    public void time_minus(View view) {
            if (timeToStart >0) {
                if (timerStarted) {
                    resetClock = true;
                    timeToStart = clock - 60;
                    countdown();
                } else {
                    timeToStart = timeToStart - 60;
                    showClock(timeToStart);
                }
            }
    }

    public void start_clock1(View view) {
        if (!timerStarted) {
            Toast.makeText(this, "Clock started", Toast.LENGTH_SHORT).show();
            countdown();
        }
    }

    public void countdown() {
            if(resetClock) {
                startClock.cancel();
            }
            timerStarted = true;
            startClock = new CountDownTimer(timeToStart * 1000 + 1020, 1000) {
                public void onTick(long millisUntilStart) {
                    clock = (millisUntilStart)/ 1000 - 1;
                    Log.e("millis", String.valueOf(millisUntilStart) + "  " + (millisUntilStart)/1000 + " " + clock);
//                clockDisplay = String.format("%02d' %02d\"",
//                    TimeUnit.SECONDS.toMinutes(clock) -
//                    TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(clock)),
//                    TimeUnit.SECONDS.toSeconds(clock) -
//                    TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(clock)));
//
//                    mClockTextView.setText(clockDisplay);
                    showClock(clock);
                    secsLeft = (double) clock;


                        if (clock == 0) {
                            playSounds("shotgun");
                        } else {
                            if (Math.round((secsLeft) / 60) * 60 == secsLeft) {
                                playSounds("air_horn");
                            }
                        }
                    }

                    public void onFinish () {
//                        playSounds("shotgun");
                        mClockTextView.setText("* GO ! *");


                    };

            }.start();

    }

    public void sync_clock(View view) {
           timeToStart = (long) Math.round((secsLeft)/60)*60;
           resetClock=true;
           countdown();



    }

    public void showClock(long clock) {
            clockDisplay = String.format("%02d' %02d\"",
            TimeUnit.SECONDS.toMinutes(clock) -
            TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(clock)),
            TimeUnit.SECONDS.toSeconds(clock) -
            TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(clock)));

            mClockTextView.setText(clockDisplay);

    }

    public void playSounds(String sound) {
            if (sound == "air_horn") {
                final MediaPlayer mediaPlayer = MediaPlayer.create(RaceStartActivity.this, R.raw.air_horn);
                mediaPlayer.start();
            }
            if (sound == "shotgun"){
                final MediaPlayer mediaPlayer = MediaPlayer.create(RaceStartActivity.this, R.raw.shotgun);
                mediaPlayer.start();
            }
    }
}

