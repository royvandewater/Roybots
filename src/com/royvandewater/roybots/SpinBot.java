package com.royvandewater.roybots;

import java.util.ArrayList;

import robocode.AdvancedRobot;
import robocode.BattleEndedEvent;
import robocode.DeathEvent;
import robocode.ScannedRobotEvent;
import robocode.WinEvent;
import robocode.util.Utils;

import com.royvandewater.roybots.util.KnowledgeBank;
import com.royvandewater.roybots.util.RoboLog;
import com.royvandewater.roybots.util.Sighting;

public class SpinBot extends AdvancedRobot
{
    private static final int TARGET_EXPIRE_LIMIT = 16;
    private static final String LOG_FILENAME = "spin_bot.log";
    private ScannedRobotEvent target;
    private RoboLog log;
    private double targetHeading = 0.0;
    private int direction = 1;
    private KnowledgeBank knowledgeBank;

    @Override
    public void run()
    {
        setup();
        
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
        
        log = new RoboLog(getDataFile(LOG_FILENAME), RoboLog.INFO);
        knowledgeBank = new KnowledgeBank();
    }

    public void aim()
    {
        if (target != null) {

            knowledgeBank.getFiringSolution(target);
            double howFarToTurn = Utils.normalRelativeAngle(targetHeading - getGunHeadingRadians());

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
            double amountToTurnToBroadsideTarget = Utils.normalRelativeAngle(amountToTurnToTarget + (Math.PI / 2));
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

    private void setTarget(ScannedRobotEvent newTarget)
    {
        target = newTarget;

        if (newTarget == null) {
            log.i("Target expired");
            return;
        } else if (target == null || !target.getName().equals(newTarget.getName())) {
            log.i("Target acquired: " + newTarget.getName());
        }
        
        knowledgeBank.addSighting(newTarget);
        targetHeading = newTarget.getBearingRadians() + getHeadingRadians();
    }

    private boolean targetExpired()
    {
        if (target == null)
            return true;

        return getTime() - target.getTime() > TARGET_EXPIRE_LIMIT;
    }
}