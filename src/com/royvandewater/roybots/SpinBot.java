package com.royvandewater.roybots;

import java.io.File;
import robocode.AdvancedRobot;
import robocode.BattleEndedEvent;
import robocode.DeathEvent;
import robocode.ScannedRobotEvent;
import robocode.WinEvent;
import robocode.util.Utils;
import com.royvandewater.roybots.util.RoboLog;
import com.royvandewater.roybots.util.RoyMath;

public class SpinBot extends AdvancedRobot
{

    private static final int TARGET_EXPIRE_LIMIT = 16;
    private static final String LOG_FILENAME = "spin_bot.log";
    private ScannedRobotEvent target;
    private RoboLog log;
    private double targetHeading = 0.0;
    private int direction = 1;

    @Override
    public void run()
    {
        setup();

        File logFile = getDataFile(LOG_FILENAME);
        this.log = new RoboLog(logFile, RoboLog.INFO);
        
        while (true) {
            scan();
            aim();
            fire();
            move();
            execute();
            cleanup();
        }
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

    @Override
    public void onScannedRobot(ScannedRobotEvent event)
    {
        if (targetExpired() || target.getName().equals(event.getName())) {
            double absoluteBearing = getHeadingRadians() + event.getBearingRadians();
            double radarTurn = absoluteBearing - getRadarHeadingRadians();
            setTurnRadarRightRadians(Utils.normalRelativeAngle(radarTurn));

            setTarget(event);
        }
    }

    private void setup()
    {
        setAdjustGunForRobotTurn(true);
    }

    public void aim()
    {
        if (target != null) {
            double howFarToTurn = RoyMath.constrain(targetHeading - getGunHeadingRadians());

            setTurnGunRightRadians(howFarToTurn);
        }
    }

    public void fire()
    {
        if (target != null) {
            double elapsedTimeSinceScan = getTime() - target.getTime();

            if (getGunTurnRemainingRadians() <= 0.05 && elapsedTimeSinceScan <= 3 && getGunHeat() == 0) // Ensure targeting data is recent
                setFire(3 - elapsedTimeSinceScan);
        }
    }

    public void move()
    {

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

    private void cleanup()
    {
        if (targetExpired()) {
            setTarget(null);
            setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
        }
    }

    private void setTarget(ScannedRobotEvent target)
    {
        this.target = target;

        if (target == null) {
            log.i("Target expired");
            return;
        } else if (this.target == null || !this.target.getName().equals(target.getName()))
            log.i("Target acquired: " + target.getName());

        this.targetHeading = target.getBearingRadians() + getHeadingRadians();
    }
    private boolean targetExpired()
    {
        if (target == null)
            return true;

        return getTime() - target.getTime() > TARGET_EXPIRE_LIMIT;
    }

}
