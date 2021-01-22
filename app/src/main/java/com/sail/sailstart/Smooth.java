package com.sail.sailstart;


import java.util.LinkedList;
import java.util.Queue;

public class Smooth {

    public Queue<Double> window = new LinkedList<Double>();
    public int p;
    public double sumSpeed, sumHeading, aveHeading;

    public Smooth(int period) {
        assert period > 0 : "Period must be a positive integer";
        p = period;
    }

    public void newSpeed(double speed) {
        sumSpeed += speed;
        window.add(speed);
        if (window.size() > p) {
            sumSpeed -= window.remove();
        }
    }

    public double getAvgSpeed() {
        if (window.isEmpty()) return 0; // technically the average is undefined
        return sumSpeed / window.size();
    }

    public void newHeading(int heading) {
        sumHeading += heading;
        window.add((double) heading);
        if (window.size() > p) {
            sumHeading -= window.remove();
        }
    }

    public int getAvgHeading() {
        if (window.isEmpty()) return 0; // technically the average is undefined
        aveHeading = sumHeading / window.size();
        return (int) aveHeading;
    }

}
