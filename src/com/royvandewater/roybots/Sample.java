package com.royvandewater.roybots;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import com.royvandewater.roybots.utils.RoboLog;

public class Sample extends AdvancedRobot {

    private static final double BEARING_MARGIN_OF_ERROR = 0.01;
    private static final int LOGLEVEL = RoboLog.DEBUG;
    private static final long STALE_DATA_TIMEOUT = 5;
    private RoboLog logger;
    private int direction = 1;
    private double lastKnownBearings;
    private long lastScan;

    /*
     * DPS = (60x) / (5 + x); 0.1 < x < 3.0
     * x is power
     * 
     * Minimum DPS:   2.94  rps @ 0.1 power = 1.17  DPS
     * Maximum ROF:   2.727 rps @ 0.5 power = 5     DPS
     * Maximum Power: 1.875 rps @ 3.0 power = 22.5  DPS
     */
    
    private void onCreate() {
        String loggerFilename = getDataFile(getClass().getCanonicalName() + ".log").getAbsolutePath();
        this.logger = new RoboLog(loggerFilename, LOGLEVEL);
    }

    @Override
    public void run() {
        onCreate();
        lastKnownBearings = 0;
        lastScan = 0;

        while (true) {
            setTurnRadarRightRadians(Math.PI);
            execute();
            maybeFire();
        }
    }
    
    public void maybeFire() {
        if (Math.abs(getGunBearingOffset(lastKnownBearings)) < BEARING_MARGIN_OF_ERROR && (getTime() - lastScan) < STALE_DATA_TIMEOUT)
            fireBullet(3);
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        lastKnownBearings = event.getBearingRadians();
        lastScan = getTime();
        setTurnGunToBearing(lastKnownBearings);
        execute();
    }

    private double reduceBearing(double bearing) {
        if (bearing > Math.PI)
            return reduceBearing(bearing - (2 * Math.PI));
        else if (bearing < -1 * Math.PI)
            return reduceBearing(bearing + (2 * Math.PI));
        else
            return bearing;
    }

    private void setTurnGunToBearing(double bearing) {
        double offset = getGunBearingOffset(bearing);

        setTurnGunRightRadians(offset);
    }

    private double getGunBearingOffset(double bearing) {
        return reduceBearing(bearing - getGunBearing());
    }

    private double getGunBearing() {
        return getGunHeadingRadians() - getHeadingRadians();
    }
}
