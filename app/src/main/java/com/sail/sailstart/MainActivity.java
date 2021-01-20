package com.sail.sailstart;

/*
  Copyright 2017 Google Inc. All Rights Reserved.
  <p>
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  <p>
  http://www.apache.org/licenses/LICENSE-2.0
  <p>
  Unless required by applicable law or agreed to in writing, softwareNext Mark
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 * Using location settings.
 * <p/>kts
 * Uses the {@link com.google.android.gms.location.SettingsApi} to ensure that the device's system
 * settings are properly configured for the app's location needs. When making a request to
 * Location services, the device's system settings may be in a state that prevents the app from
 * obtaining the location data that it needs. For example, GPS or Wi-Fi scanning may be switched
 * off. The {@code SettingsApi} makes it possible to determine if a device's system settings are
 * adequate for the location request, and to optionally invoke a dialog that allows the user to
 * enable the necessary settings.

 * <p/>
 * This sample allows the user to request location updates using the ACCESS_FINE_LOCATION setting
 * (as specified in AndroidManifest.xml).
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * Code used in requesting runtime permissions.
     */
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    /**
     * Constant used in the location settings dialog.
     */
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 500;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    private final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    private final static String KEY_LOCATION = "location";
    private final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Provides access to the Location Settings API.
     */
    private SettingsClient mSettingsClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private LocationRequest mLocationRequest;

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    private LocationSettingsRequest mLocationSettingsRequest;

    /**
     * Callback for Location events.
     */
    private LocationCallback mLocationCallback;

    /**
     * Represents a geographical location.
     */
    public Location mCurrentLocation;
//    private Bundle myState;

    // UI Widgets.
    private TextView mNextMarkTextView;
    private TextView mCourseTextView;
    private TextView mSpeedTextView;
    private TextView mHeadingTextView;
    private TextView mDistanceTextView;
    private TextView mDistanceUnitTextView;
    private TextView mBearingTextView;
    private TextView mClockTextView;
    private TextView mTimeToLineView;
    private TextView mTimeVarianceView;
    private TextView mEarlyLateView;


    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    private Boolean mRequestingLocationUpdates;

    /**
     * Time when the location was updated represented as a String.
     */
    private String mLastUpdateTime;

    // Define the 'Marks' Array
    Marks theMarks = null;
    Courses theCourses = null;
    FinishLine theFinish = null;

    // Define parameters of next mark
    double mSpeed;
    double vmgToMark;
    String speedDisplay;
    int mHeading;
    int negHeading;
    String displayHeading;
    String nextMark = "A Mark";
    String nextMarkFull;
    Location destMark;
    Double destMarkLat, destMarkLon;
    float distToMark;
    int bearingToMark;
    int displayBearingToMark;
    String distUnits;

    float distDisplay;
    String displayDistToMark;
    float distanceToMark;
    long lastUpdateTime;
    long timeSinceLastUpdate;
    long timeToMark;
    String ttmDisplay;
    String timeVarDisplay;
    String titleEarlyLate;
    long currentTime;
    String currentTimeDisplay;

    int posMark = 0;
    int posCourse = 0;
    int listMarkSize, listCourseSize;
    String raceCourse;
    ArrayList courseMarks;
    Bundle savedInstanceState;

    long timeToStart = 15 * 60;
    public Boolean timerStarted = false;
    Boolean resetClock =false;
    CountDownTimer startClock;
    long clock = 75;
    long timeVariance;
    private String clockDisplay;
    double secsLeft;
    public MediaPlayer mediaPlayer;
    String sound;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ArrayList<Mark> marks = new ArrayList<>();
        ArrayList<Course> courses = new ArrayList<>();
        ArrayList courseMarks = new ArrayList();

        // first check for runtime permission
        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        int grant = ContextCompat.checkSelfPermission(this, permission);

        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 1);
        }

        //Create the ArrayList object here, for use in all the MainActivity
        theMarks = new Marks();
        theCourses = new Courses();

        // Create the ArrayList in the constructor, so only done once
        try {
            theMarks.parseXML();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Load all courses
        try {
            theCourses.parseXML();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create theFinish object here, and pass in 'A' Mark, and 'H' Mark
        String a = "A"; // Finish line data
        String h = "H"; // Finish Line Data
        Location aMark = theMarks.getNextMark(a);
        Location hMark = theMarks.getNextMark(h);
        // Should have A Mark, H Mark to create the Finish Line Object
        theFinish = new FinishLine(aMark, hMark);


        // Locate the UI widgets.
        mNextMarkTextView = (TextView) findViewById(R.id.next_mark_name);
        mCourseTextView = (TextView) findViewById(R.id.course_name);
        mSpeedTextView = (TextView) findViewById(R.id.speed_text);
        mHeadingTextView = (TextView) findViewById(R.id.heading_text);
        mDistanceTextView = (TextView) findViewById(R.id.distance_text);
        mDistanceUnitTextView = (TextView) findViewById(R.id.dist_unit);
        mBearingTextView = (TextView) findViewById(R.id.bearing_text);
        mClockTextView = (TextView) findViewById(R.id.time_to_start);
        mTimeVarianceView = (TextView) findViewById(R.id.start_time_early_late);
        mEarlyLateView = (TextView) findViewById(R.id.start_time_early_late_title);
        mTimeToLineView = (TextView) findViewById(R.id.time_to_line);



        mRequestingLocationUpdates = true;
        mLastUpdateTime = "";

        // Update values using data stored in the Bundle.
//        myState = savedInstanceState;
        updateValuesFromBundle(savedInstanceState);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        // Kick off the process of building the LocationCallback, LocationRequest, and
        // LocationSettingsRequest objects.
        createLocationCallback();

        createLocationRequest();
        buildLocationSettingsRequest();
        startLocationUpdates();
        showClock(timeToStart);
    }

    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        KEY_REQUESTING_LOCATION_UPDATES);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING)) {
                mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING);
            }

            if (savedInstanceState.keySet().contains("Mark Pos")) {
               posMark = savedInstanceState.getInt("Mark Pos");
               nextMark = savedInstanceState.getString("Next Mark");
               posCourse = savedInstanceState.getInt("Course Pos");
               raceCourse = savedInstanceState.getString("Course");
            }
            updateUI();
        }
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Creates a callback for receiving location events.
     */
    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                updateLocationUI();
            }
        };
    }

    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        updateUI();
                        break;
                }
                break;
        }
    }

    /**
     * Requests location updates from the FusedLocationApi. Note: we don't call this unless location
     * runtime permission has been granted.
     */
    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        updateUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                mRequestingLocationUpdates = false;
                        }

                        updateUI();
                    }
                });
    }
//    /**
//     * This method is called when the + course button is pressed
//     */
//    public void next_course(View view) {
//        {
//            // Increment to the position of the next course on the list
//            if (posCourse >= listCourseSize - 1) {
//                posCourse = 0;
//            } else
//                posCourse = posCourse + 1;
//        }
//        setCourse();
//        setNextMark();
//    }
//
//    public void previous_course(View view) {
//        {
//            // Decrement to the position of the previous course on the list
//            if (posCourse <= 0) {
//                posCourse = listCourseSize - 1;
//            } else
//                posCourse = posCourse - 1;
//        }
//        setCourse();
//        setNextMark();
//    }
//
//    /**
//     *  Set race course
//     */
//    public void setCourse() {
//        listCourseSize = theCourses.courses.size();
//        raceCourse = theCourses.courses.get(posCourse).getCourseName();
//        courseMarks = theCourses.getCourse(raceCourse);
//
//        mCourseTextView.setText(raceCourse);
//    }
//
//
//
//    /**
//     * This method is called when the + button is pressed
//     */
//    public void next_mark(View view) {
//        {
//            // Increment to the position of the nMath.abs(ext mark on the list
//            if (posMark >= listMarkSize - 1) {
//                posMark = 0;
//            } else
//                posMark = posMark + 1;
//        }
//        setNextMark();
//    }
//
//    public void previous_mark(View view) {
//        {
//            // Decrement to the position of the previous mark on the list
//            if (posMark <= 0) {
//                posMark = listMarkSize - 1;
//            } else
//                posMark = posMark - 1;
//        }
//        setNextMark();
//    }
//
//    /**
//     *  Set next destination mark
//     */
//    public void setNextMark() {
//        if (raceCourse.equals("None")) {
//            listMarkSize = theMarks.marks.size();
//            nextMark = theMarks.marks.get(posMark).getmarkName();
//
//        } else {
//            listMarkSize = courseMarks.size();
//            nextMark = (String) courseMarks.get(posMark);
//        }
//
//        if (nextMark.length() == 1){
//            nextMarkFull = nextMark + " Mark";
//        } else {
//            nextMarkFull = nextMark;
//        }
//
//        mNextMarkTextView.setText(nextMarkFull);
//
//        // Check to see if next mark is not the finish
//        if (nextMark.equals("Finish")) {
//
//            // Find the the target point on the finish line (A Mark, H Mark or Line)
//            // Pass in the currentLocation
//            nextMark =  theFinish.getFinishTarget(mCurrentLocation);
//
//            if (nextMark.equals("Line")) {
//                // Insert the finish line crossing point
//                destMark = theFinish.getFinishPoint(mCurrentLocation);
//            } else {
//                // Set the next mark to either A or H
//                mNextMarkTextView.setText("Fin - " + nextMark + " Mark");
//                destMark = theMarks.getNextMark(nextMark);
//            }
//        } else {
//         // Not the finish, set the next mark normally
//        destMark = theMarks.getNextMark(nextMark);
//        updateUI();
//        }
//
//    }

    /**
     * Updates all UI fields.
     *///
    private void updateUI() {

//        if (nextMark.equals("Start")) {
//            openStartActivity();
//        }

//        // Check to see if next mark is not the finish
//        if (nextMark.equals("Finish")) {
//
//            // Find the the target point on the finish line (A Mark, H Mark or Line)
//            // Pass in the currentLocation
//            String nextMarkFin =  theFinish.getFinishTarget(mCurrentLocation);
//            Log.e("NextMark ", nextMarkFin);
//
//            if (nextMarkFin.equals("Line")) {
//                // Insert the finish line crossing point
//                destMark = theFinish.getFinishPoint(mCurrentLocation);
//            } else {
//                // Set the next mark to either A or H
//                mNextMarkTextView.setText("Fin - " + nextMarkFin + " Mark");
//                destMark = theMarks.getNextMark(nextMarkFin);
//            }
//        }

        updateLocationUI();
    }

//    private void openStartActivity() {
//        Intent start = new Intent(this, StartActivity.class);
//        startActivity(start);
//
//    }

    /**Math.abs(
     * Sets the value of the UI fields for the location latitude, longitude and last update time.
     */
    private void updateLocationUI() {
//        if (destMark == null) {
//            setCourse();
//            setNextMark();
//        }
        destMark = theMarks.getNextMark("A");

        if (mCurrentLocation != null) {

        // Process gps data for display on UI
            // Convert speed to knots and format1000
            mSpeed = mCurrentLocation.getSpeed() * 1.943844;
            speedDisplay = new DecimalFormat( "##0.0").format( mSpeed);

            // Change heading to correct format
            mHeading = (int) mCurrentLocation.getBearing();
            if(mHeading > 180) {
                negHeading = mHeading - 360;
            } else {
                negHeading = mHeading;
            }

            displayHeading = String.format("%03d", mHeading);

            // Change distance to mark to nautical miles if > 500m and correct formattring.format decimal places
            distToMark = mCurrentLocation.distanceTo(destMark);

                // Use nautical miles when distToMark is >500m.
                if ( distToMark >500) {
                    displayDistToMark = new DecimalFormat("###0.00").format(distToMark / 1852);
                    distUnits = "NM";
                } else {
                    displayDistToMark = new DecimalFormat("###0").format(distToMark);
                    distUnits = "m";
                }

            // Get bearing to mark
            bearingToMark = (int) mCurrentLocation.bearingTo(destMark);

                // Correct negative bearings
                if ( bearingToMark < 0) {
                    displayBearingToMark = bearingToMark + 360;
                } else {
                    displayBearingToMark = bearingToMark;
                }

            // Get time since last update
            lastUpdateTime = mCurrentLocation.getTime();
            currentTime = Calendar.getInstance().getTimeInMillis();
            timeSinceLastUpdate = (currentTime - lastUpdateTime)/1000;

            currentTimeDisplay = java.text.DateFormat.getTimeInstance().format(new Date());

            // Calculate time to the mark
            mSpeed = (float) mCurrentLocation.getSpeed();

            // Calc time to mark
            vmgToMark = Math.cos(mHeading - bearingToMark) * mSpeed;
            timeToMark = (long) (distToMark / vmgToMark);

            // Keep displayed figure below 100 hours 360000 secs.
            if (timeToMark < 360000 && timeToMark > 0) {
                ttmDisplay = String.format("%02dh %02d' %02d\"",
                    TimeUnit.SECONDS.toHours(timeToMark),
                    TimeUnit.SECONDS.toMinutes(timeToMark) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(timeToMark)),
                    TimeUnit.SECONDS.toSeconds(timeToMark) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(timeToMark)));
            } else {
                ttmDisplay = "--h --' --\"";
            }

            // Calc early/late to line
        timeVariance = clock - timeToMark;
            if (timeVariance < 360000 && timeVariance > -360000) {
                timeVarDisplay = String.format("%02dh %02d' %02d\"",
                        TimeUnit.SECONDS.toHours(timeVariance),
                        TimeUnit.SECONDS.toMinutes(timeVariance) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(timeVariance)),
                        TimeUnit.SECONDS.toSeconds(timeVariance) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(timeVariance)));
            } else {
                timeVarDisplay = "--h --' --\"";
            }

        // Send info to UI
            mSpeedTextView.setText(speedDisplay);
            mHeadingTextView.setText(displayHeading);
            mDistanceTextView.setText(displayDistToMark);
            mDistanceUnitTextView.setText(distUnits);
            mBearingTextView.setText(String.format("%03d", displayBearingToMark));
            mTimeToLineView.setText(ttmDisplay);
            mTimeVarianceView.setText(timeVarDisplay);
            if ( timeVariance < 0) {
                mTimeVarianceView.setTextColor(Color.RED);
                mEarlyLateView.setText("Late");
                mEarlyLateView.setTextColor(Color.RED);
            }
            if ( timeVariance >  0) {
                mTimeVarianceView.setTextColor(Color.GREEN);
                mEarlyLateView.setText("Early");
                mEarlyLateView.setTextColor(Color.GREEN);
            }

        }
    }

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


    public void start_clock(View view) {
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
            startClock = new CountDownTimer(timeToStart * 1000, 1000) {
                public void onTick(long millisUntilStart) {
                    clock = (millisUntilStart)/ 1000;
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
                final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.air_horn);
                mediaPlayer.start();
            }
            if (sound == "shotgun"){
                final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.shotgun);
                mediaPlayer.start();
            }
    }


    @Override
    public void onResume() {
        super.onResume();
        updateValuesFromBundle(savedInstanceState);

        // Within {@code onPause()}, we remove location updates. Here, we resume receiving
        // location updates if the user has requested them.
        if (mRequestingLocationUpdates && checkPermissions()) {
            startLocationUpdates();
        } else if (!checkPermissions()) {
            requestPermissions();
        }

//        updateUI();
    }

    /**
     * Stores activity data in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        savedInstanceState.putString(KEY_LAST_UPDATED_TIME_STRING, mLastUpdateTime);
        savedInstanceState.putInt("Mark Pos", posMark);
        savedInstanceState.putInt("Course Pos", posCourse);
        savedInstanceState.putString("Next Mark", nextMark);
        savedInstanceState.putString("Course", raceCourse);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }


    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mRequestingLocationUpdates) {
                    Log.i(TAG, "Permission granted, updates requested, starting location updates");
                    startLocationUpdates();
                }
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }

        // Add double click to exit
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

        // Hide navigation and status bar
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY70 ;/
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

    }



}


