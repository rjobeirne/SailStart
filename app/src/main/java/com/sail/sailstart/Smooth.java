package com.sail.sailstart;


import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;

public class Smooth {

    public Queue<Double> windowSpeed = new LinkedList<Double>();
    public Queue<Double> windowHead = new LinkedList<Double>();
    public int p;
    public double sumSpeed, sumHeading, aveHeading;

    public Smooth(int period) {
        assert period > 0 : "Period must be a positive integer";
        p = period;
        Log.e("p", String.valueOf(p));
    }

    public void newSpeed(double speed) {
        sumSpeed += speed;
        windowSpeed.add(speed);
        if (windowSpeed.size() > p) {
            sumSpeed -= windowSpeed.remove();
        }
    }

    public double getAvgSpeed() {
        if (windowSpeed.isEmpty()) return 0; // technically the average is undefined
        Log.e("sumV, wind",  sumSpeed + ", " + windowSpeed.size());
        return sumSpeed / windowSpeed.size();
    }

    public void newHeading(int heading) {
        sumHeading += heading;
        windowHead.add((double) heading);
        if (windowHead.size() > p) {
            sumHeading -= windowHead.remove();
        }
    }

    public int getAvgHeading() {
        if (windowHead.isEmpty()) return 0; // technically the average is undefined
        aveHeading = sumHeading / windowHead.size();
        Log.e("aveH, sumH, wind", aveHeading + ", " + sumHeading + ", " + windowHead.size());
        return (int) aveHeading;
    }

}
