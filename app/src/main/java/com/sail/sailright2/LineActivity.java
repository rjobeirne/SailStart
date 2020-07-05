package com.sail.sailright2;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.core.location.LocationManagerCompat;

public class LineActivity {

    public LineActivity(Location a, Location h) {
        // constructor with 'A' Mark, and 'H' mark location details, and first currentLocation
        markA = a;
        markH = h;
        latA = markA.getLatitude();
        lonA = markA.getLongitude();
    }


    // Initialise global object variables
    String lineTarget = null;
    Location linePoint = new Location(LocationManager.GPS_PROVIDER);
    Location markA = null;
    Location markH = null;
    double latA = 0;
    double lonA = 0;
    float lineBearing = 0;
    double slopeLine = 0;
    double constLine = 0;

    // Boat Location details
    Location mCurrentLocation = null;
    double latBoat = 0;
    double lonBoat = 0;
    float boatHeading, bearingToA, bearingToH;
    double slopeBoat = 0;
    double constBoat = 0;


    // This will set the current boat Location details
    public void setBoatDetails(Location currentLocation) {
        // Define boat heading as linear equation lat = slope * lon + constant
        mCurrentLocation = currentLocation;
        latBoat = mCurrentLocation.getLatitude();
        lonBoat = mCurrentLocation.getLongitude();

        float mHeading = mCurrentLocation.getBearing();
//        float mHeading = -158 + 360;
            if(mHeading > 180) {
                boatHeading = mHeading - 360;
            } else {
                boatHeading = mHeading;
            }

//        Log.e("boatheading", String.valueOf(boatHeading));
        slopeBoat = Math.tan(Math.toRadians(boatHeading));
        constBoat = latBoat - (slopeBoat * lonBoat);

        bearingToA = mCurrentLocation.bearingTo(markA);
        bearingToH = mCurrentLocation.bearingTo(markH);


        // Define finish line as linear equation lat = slope * lon + constant
        lineBearing = markA.bearingTo(markH);
        slopeLine = Math.tan(Math.toRadians(lineBearing));
        constLine = latA - (slopeLine * lonA);

    }
    /**
     *
     * @return
     */
    public String getLineTarget(Location currentLocation) {
        Log.e("**start getLineTarget","");
        // Update current Location of the boat, passed in from Main
        mCurrentLocation = currentLocation;
        setBoatDetails(mCurrentLocation);  // Update current boat location details

        if (latBoat > latA) {
            // Approaching from the north
            if (boatHeading > bearingToA) {
                lineTarget = "A";
            } else if (boatHeading < bearingToH) {
                lineTarget = "H";
            } else {
                lineTarget = "Line";
//                Log.e("Heading for line", lineTarget);
            }
//            Log.e("Target,boat,A,H", lineTarget + String.valueOf(boatHeading) +
//                    String.valueOf(bearingToA) + String.valueOf(bearingToH));

        } else {
            // Approaching from the south
            // First correct heading >180 to be negative to avoid problem at due north
            if (boatHeading > 180) {
                boatHeading = boatHeading - 360;
            }
            if (bearingToA > 180) {
                bearingToA = bearingToA - 360;
            }
            if (bearingToH > 180) {
                bearingToH = bearingToH - 360;
            }

            if (boatHeading < bearingToA) {
                lineTarget = "A";
            } else if (boatHeading > bearingToH) {
                lineTarget = "H";
            } else {
                lineTarget = "Line";
            }

        }
        return lineTarget;
    }

    /**
     *
     * @return
     */
    public Location getLinePoint(Location currentLocation) {
        Log.e("started getLinePoint","");
        mCurrentLocation = currentLocation;
        setBoatDetails(mCurrentLocation);  // Update the current Location of the boat

        double finLon = (constLine - constBoat) / (slopeBoat - slopeLine);
        double finLat = slopeLine * finLon +constLine;
        linePoint.setLongitude(finLon);
        linePoint.setLatitude(finLat);
//         Log.e("**linePoint", String.valueOf(linePoint));

        return linePoint;
    }

}
