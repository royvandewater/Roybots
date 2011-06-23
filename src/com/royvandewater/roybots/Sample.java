package com.royvandewater.roybots;

import java.io.File;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import com.royvandewater.roybots.utils.RoboLog;

public class Sample extends AdvancedRobot {

    private static final double BEARING_MARGIN_OF_ERROR = 0.01;
    private static final int LOGLEVEL = RoboLog.DEBUG;
    private RoboLog logger;

    private void onCreate() {
        File loggerFilename = getDataFile(this.getClass().getCanonicalName() + ".log");
        this.logger = new RoboLog(loggerFilename, LOGLEVEL);
    }

    @Override
    public void run() {
        onCreate();
        
        double gunHeading = 0;
        while (true) {
            setTurnRadarRightRadians(Math.PI);
            setTurnRightRadians(Math.PI / 2);
            setTurnGunRightRadians(-1 * gunHeading);
            setAhead(30);
            gunHeading = getGunHeadingRadians();
            execute();
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        if (Math.abs(getGunBearingOffset(event.getBearingRadians())) < BEARING_MARGIN_OF_ERROR)
            fire(2);
        
        //setTurnGunToBearing(event.getBearingRadians());
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

        logger.d("RobotHeading: " + getHeadingRadians());
        logger.d("GunHeading:   " + getGunHeadingRadians());
        logger.d("GunBearing:   " + getGunBearing());
        logger.d("bearing:      " + bearing);
        logger.d("Offset:       " + offset);

        setTurnGunRightRadians(offset);
    }

    private double getGunBearingOffset(double bearing) {
        return reduceBearing(bearing - getGunBearing());
    }

    private double getGunBearing() {
        return getGunHeadingRadians() - getHeadingRadians();
    }
}
