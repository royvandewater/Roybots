package com.royvandewater.roybots;

import com.royvandewater.roybots.util.RoboLog;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

public class SpinBot extends AdvancedRobot {
	
	private static final String LOG_FILENAME = "spin_bot.log";
	private ScannedRobotEvent target;
	private RoboLog log;

	@Override
	public void run() {
		log = new RoboLog(LOG_FILENAME, RoboLog.INFO);
		
		while (true) {
			move();
		}
	}
	
	@Override
	public void onScannedRobot(ScannedRobotEvent event) {
		if (getTarget() == null || getTarget().getName() == event.getName() || getTarget().getTime() > 10)
			setTarget(event);
	}

	// Circle around a target
	public void move() {
		
	}

	public ScannedRobotEvent getTarget() {
		return target;
	}

	public void setTarget(ScannedRobotEvent target) {
		log.d("Target acquired: " + target.getName());
		this.target = target;
	}
}
