package com.royvandewater.roybots;

import java.io.File;

import com.royvandewater.roybots.util.RoboLog;
import com.royvandewater.roybots.util.RoyMath;

import robocode.AdvancedRobot;
import robocode.BattleEndedEvent;
import robocode.DeathEvent;
import robocode.ScannedRobotEvent;
import robocode.WinEvent;

public class SpinBot extends AdvancedRobot {

    private static final String LOG_FILENAME = "spin_bot.log";
    private ScannedRobotEvent target;
    private RoboLog log;
    private double targetHeading = 0.0;
    private int direction = 1;

    @Override
    public void run() {
        File logFile = getDataFile(LOG_FILENAME);
        log = new RoboLog(logFile, RoboLog.DEBUG);

        setAdjustGunForRobotTurn(true);
        
        while (true) {  
            setMove();
            setAimAndFire();
            execute();
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        if (target == null || target.getName().equals(event.getName()) || getTime() - target.getTime() > 16)
            setTarget(event);
    }

    @Override
    public void onBattleEnded(BattleEndedEvent event) {
        log.close(event);
    }

    @Override
    public void onDeath(DeathEvent event) {
        log.close(event);
    }

    @Override
    public void onWin(WinEvent event) {
        log.close(event);
    }

    public void setMove() {
        setTurnRadarRightRadians(Math.PI);
        
        if (target != null && getTurnRemainingRadians() == 0) {
            double amountToTurnToTarget = targetHeading - getHeadingRadians();
            double amountToTurnToBroadsideTarget = RoyMath.constrain(amountToTurnToTarget + (Math.PI / 2));
            setTurnRightRadians(amountToTurnToBroadsideTarget);
        }
        
        if (getDistanceRemaining() == 0.0) {
            setAhead(200 * Math.random() * direction);
            direction *= -1;
        }
    }

    public void setAimAndFire() {
        if (target != null) {
            double howFarToTurn = RoyMath.constrain(targetHeading - getGunHeadingRadians());
            double elapsedTimeSinceScan = getTime() - target.getTime();
            
            setTurnGunRightRadians(howFarToTurn);
            
            
            if(howFarToTurn == 0 && elapsedTimeSinceScan <= 3 && getGunHeat() == 0) // Ensure targeting data is recent
                setFire(3 - elapsedTimeSinceScan);
        }
    }

    public ScannedRobotEvent getTarget() {
        return target;
    }

    public void setTarget(ScannedRobotEvent target) {
        if (target == null)
            log.i("Target expired");
        else if (this.target == null || !this.target.getName().equals(target.getName()))
            log.i("Target acquired: " + target.getName());
        this.targetHeading = target.getBearingRadians() + getHeadingRadians();
        this.target = target;
    }
}
