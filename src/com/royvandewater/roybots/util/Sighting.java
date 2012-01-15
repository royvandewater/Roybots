package com.royvandewater.roybots.util;

import robocode.ScannedRobotEvent;

public class Sighting {

    private long time;
    private double velocity;
    private double heading;

    public Sighting(long time, double velocity, double heading) {
        this.time = time;
        this.velocity = velocity;
        this.heading = heading;
    }

    public static Sighting fromScannedRobotEvent(ScannedRobotEvent event) {
        return new Sighting(event.getTime(), event.getVelocity(), event.getHeading());
    }

    public double minus(Sighting oldSighting) {
        return Math.abs(heading - oldSighting.getHeading()) + Math.abs(velocity - oldSighting.getVelocity());
    }
    
    public double getHeading() {
        return heading;
    }
    
    public double getVelocity() {
        return velocity;
    }
}
