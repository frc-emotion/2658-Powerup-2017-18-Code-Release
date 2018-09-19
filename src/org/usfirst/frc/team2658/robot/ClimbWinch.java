package org.usfirst.frc.team2658.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The ClimbWinch class is used for the functions the winch, which is defined as the spinning mechanism that pulls the robot
 * up the bar. By pressing the START button on the Operator X-Box Controller, the robot shuts off all operator and driver controls
 * and begins spinning the winch to climb. It will hold back the arm as it climbs up. 
 * 
 * Instance Variable
 * 	- public Robot robot;
 * 	- public boolean climbing;
 * 	- public boolean firstRun;
 * 	- public boolean startHeld;
 * 	- public final int CLIMB_BUTTON;
 * 
 * Constructor
 * 	- public ClimbWinch(Robot)
 * 
 * Methods
 * 	- public void run()
 * 	- public void checkButtonPress()
 * 	- public void dashboardWork()
 *
 */
public class ClimbWinch extends Thread{
	Robot robot;							//The main robot object
	public boolean climbing = false;		//boolean to tell if the robot is climbing or nto
	public boolean finishedClimbing = false;
	public boolean firstRun = true;			//this boolean is used to tell if it is in its first loop of climbing
	public boolean startHeld = false;		//this is used to check if the start button (for climbing)
	public final int CLIMB_BUTTON = 8;		//The button index for the Operator X-Box controller index
	
	/**
	 * Constructor for the ClimbWinch, grabs the Robot class from the Robot.java
	 * 
	 * @param r			The robot class to take from
	 * `
	 * Note: This is pretty much cludging it to not make it static.
	 */
	public ClimbWinch(Robot robot) {
		this.robot = robot;		//Set this Robot object as the passed Robot
	}
	
	
	/**
	 * The run method is what is placed in the teleopPeriod to continuously run. Later, it will be used as the Thread's run
	 * method when we use the thread.
	 */
	public void run() {
		checkButtonPress();		//Check to see if the climb button is pressed
		//If it is climbing, get stuck in this while loop
		while (climbing && !finishedClimbing) {
//			while (anyThreadAlive()) {
//				//Empty, basically if any other thread is alive, wait until all other threads close down
//			}
			if (firstRun) {
				//For the very first time, move the arm all the way down
				if (!SmartDashboard.getString("Arm Position", "Unsure").equals("Level 1")) robot.arm.autoMoveToLevel(1);
				firstRun = false;		//Set the first run to false
			}
			//If the arm is outside the bottom position, set the move arm to the bottom level
			if (!SmartDashboard.getString("Arm Position", "Unsure").equals("Level 1")) robot.arm.setMoveToLevel(1);
			robot.arm.movingToLevel();		//Calls to move the arm, it only moves if there is a target to move it
			checkButtonPress();			//Check to see if the button is pressed
			robot.climbWinch1.set(-1);	//Set the first climb winch motor to max speed
			robot.climbWinch2.set(-1);	//Set the second climb winch motor to max speed
			robot.updateSmartDashboard();		//Update the smart dashboard
		}
		while (finishedClimbing) {
			robot.armTalon.set(-0.6);	/*-- changed for lancaster --*/ //changed the negative
			checkButtonPress();
			robot.updateSmartDashboard();
		}
		
		firstRun = true;		//Sets the firstRun to true for the next time it starts climbing again
		
		//The following is for the driver to control the winch. This should never be used unless it is for testing.
		if (robot.driveXBox.getRawButton(CLIMB_BUTTON)) {
			//If the driver is pressing the START button, rotate the winch backwards at slow speeds
			robot.climbWinch1.set(0.1);
			robot.climbWinch2.set(0.1);
		}
		else if (robot.driveXBox.getRawButton(7)) {
			//If the driver is pressing the SELECT button, rotate the winch forwards at sow speeds
			robot.climbWinch1.set(-0.1);
			robot.climbWinch2.set(-0.1);
		}
		else {
			//If nothing is pressed, stop the climb winch
			robot.climbWinch1.set(0);
			robot.climbWinch2.set(0);
		}	
	}
	
	/**
	 * The anyThreadAlive method will check if any other thread apart from the ClimbWinch one is alive. This is used to
	 * make sure all threads close before the robot starts climbing.
	 * 
	 * @return	true if at least one other thread is alive, false if all threads are dead
	 */
	public boolean anyThreadAlive() {
		return (robot.drive.isAlive() || robot.claw.isAlive() || robot.arm.isAlive() || robot.pacman.isAlive()
				|| robot.hookshot.isAlive() || robot.intake.isAlive());
	}
	
	/**
	 * The checkButtonPress method is used to see if climb button is pressed to start or stop the climbing routine
	 */
	public void checkButtonPress() {
		if (robot.opXBox.getRawButton(CLIMB_BUTTON) && !startHeld) {
			//If the START button is pressed and is not being held
			if (!climbing && !finishedClimbing) {
				climbing = true;
			}
			else if (climbing) {
				finishedClimbing = true;
			}
			else {
				climbing  = false;
				finishedClimbing = false;
			}
		}
		startHeld = robot.opXBox.getRawButton(CLIMB_BUTTON);	//Set the held variable to what the climb button is
	}
	
	/**
	 * The dashboardWork method is used to put the String value to see if the Robot is in climb mode or not
	 */
	public void dashboardWork() {
		//Dashboard for climbing or nots
		if (climbing && !finishedClimbing)
			SmartDashboard.putString("Climbing", "TRUE- GOING UP");
		else if (climbing && finishedClimbing)
			SmartDashboard.putString("Climbing", "TRUE - PULLING ARM");
		else
			SmartDashboard.putString("Climbing", "FALSE");
	}
}
