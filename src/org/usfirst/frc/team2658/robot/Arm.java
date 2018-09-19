package org.usfirst.frc.team2658.robot;

/**
 * The Arm class is used for the functions on the Arm, defined as the piece that moves up and down not including the Claw attached
 * to the end of it. It has functions of moving up and down manually by the Operator's Left Joystick Y-Axis as well as an auto mode
 * during teleop by using the D-Pad. This class also has the code to reset the encoders with the SELECT button on the Operator controller
 * since it is necessary for the Operator the set the lowest position as 0 on the arm encoder. Other functions it has includes methods
 * to be used in autonomous when moving the arm.
 * 
 * Instance Variables
 * 	- private final double LV1
 * 	- private final double LV2
 * 	- private final double LV3
 * 	- private final double LV4
 * 	- private final double LV5
 * 	- private final double TOLERANCE
 * 	- private final double SLOW_CONS
 * 	- private boolean manualMove
 * 	- private boolean povHeld
 * 	- private int toLevel
 * 	- private Timer timer
 * 	- private Robot robot
 * Constructors
 * 	- public Arm(Robot)
 * Methods
 * 	- public void run()
 * 	- public void manualMove()
 * 	- public void buttonRead()
 * 	- public void movingToLevel()
 * 	- public void setMoveToLevel(int)
 * 	- public void moveToLevel(int)
 * 	- public void autoMoveToLevel(int)
 * 	- public void dashboardWork()
 * 	- public void dashboardLevel()
 */

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Arm extends Thread {

	// private double LEVEL_CONSTANT = 0.6;

	// The following five values are the encoder values of the various levels
	private final double LV1 = 15.0; // This is the encoder value of the first level (bottom)
	private final double LV2 = 270.0; // This is the encoder value of the second level (switch)
	private final double LV3 = 400.0; // This is the encoder value of the third level (low scale)
	private final double LV4 = 507.0; // This is the encoder value of the fourth level (med scale)
	private final double LV5 = 680.0; // This is the encoder value of the fifth level (high scale)

	private final double TOLERANCE = 15.0; // This is the range of the levels as it does not usually get it perfect on
											// the arm

	private final double SLOW_CONS = -0.4; // This is the arm constant that slows the speed the arm moves at
	private boolean manualMove = true; // This is used to check if the operator is currently moving it by auto or
										// manually
	private boolean povHeld = false; // This is used to check if the D-Pad is currently being held
	private int toLevel = -1; // This variable is used to know where the arm is trying to move when on auto
	private boolean throwing = false; // This variable is used to try throwing the cube

	private boolean armLock = false; // This variable is used to notify if the arm is trying to lock into a 180
										// position up
	private boolean armLockButtonHeld = false; // This variable is used to tell if the button used to locking the arm is
												// being held
	private int ARM_LOCK_BUTTON = 9; // This is the button used to tell if the arm is to be locked (Left Joystick
										// Button)

	private Timer timer = new Timer(); // The timer is mostly used to break out of getting stuck in a loop
	private Robot robot; // The main Robot object

	/**
	 * Constructor for the Arm, grabs the Robot class from the Robot.java
	 * 
	 * @param robot
	 *            The robot class to take from
	 * 
	 *            Note: This is pretty much cludging it to not make it static.
	 */
	public Arm(Robot robot) {
		this.robot = robot; // Set this robot as the robot passed by
	}

	/**
	 * The run method is what is placed in the teleopPeriod to continuously run.
	 * Later, it will be used as the Thread's run method when we use the thread.
	 */
	public void run() {
		if (manualMove && !armLock) { // If the arm can be moved manually
			if (timer.get() != 0) { // If the timer is still being read something, turn it off
				timer.stop();
				timer.reset();
			}
			manualMove(); // Moving the arm with a joystick
			buttonRead(); // Reading buttons in order to move it to a level
		}
		else {
			if (!manualMove && !armLock) {
				if (timer.get() == 0)
					timer.start(); // Start the timer, this is used as a hard break against arm control
				movingToLevel(); // Moving it to a level
				if (timer.get() >= 4) { // When the auto has been alive for 4 seconds, break out of it. It may be
										// stopped
					timer.stop();
					timer.reset();
					toLevel = -1;
					manualMove = true;
				}

				// If the robot is in the operation of throwing
				if (throwing) {
					if (robot.claw.isOpen())
						robot.claw.closeClaw(); // Keep the claw closed

					// Once the robot has finished moving to the top, open the claw to release the
					// box
					if (toLevel == -1 || manualMove) {
						robot.claw.openClaw();
						throwing = false; // Reset the throwing boolean
					}
				}
			}
			if (armLock) {
				toLevel = 4;
				movingToLevel();
			}
		}
		if (robot.opXBox.getRawButton(7))
			robot.resetEncoders(); // The SELECT button on the operator controller resets the encoders
	}

	/**
	 * The manualMove method moves the arm using a joystick value (Operator Left
	 * Joystick Y-Axis)
	 */
	public void manualMove() {
		double moveValue = robot.opXBox.getRawAxis(1); // Reading it from the left y-axis
		if (robot.opXBox.getRawAxis(1) > 0) {
			moveValue *= SLOW_CONS; // Multiply the value by the constant
		} else {
			moveValue *= -1;
		}
		// SmartDashboard.putNumber("Arm Encoder", Robot.armEncoder.get());
		robot.armTalon.set(moveValue); // Move the arm using the value
	}

	/**
	 * The buttonRead method reads the button (POV) to check if the operator wants
	 * to do a level change automatically. Up on the D-Pad moves it to the next
	 * highest level while down on the D-Pad moves it down to the next lowest level.
	 */
	public void buttonRead() {
		// This is for a Level Up!
		/*
		 * if (robot.opXBox.getPOV() == 0 && !povHeld && toLevel == -1) { double armPos
		 * = robot.armEncoder.get(); //get the arm position if (armPos < LV1 -
		 * TOLERANCE) { //If the position is less than Level 1, move up to Level 1
		 * manualMove = false; toLevel = 1; } else if (armPos < LV2 - TOLERANCE) { //If
		 * the position is less than Level 2, move up to Level 2 manualMove = false;
		 * toLevel = 2; } else if (armPos < LV3 - TOLERANCE) { //If the position is less
		 * than Level 3, move up to Level 3 manualMove = false; toLevel = 3; } else if
		 * (armPos < LV4 - TOLERANCE) { //If the position is less than Level 4, move up
		 * to Level 4 manualMove = false; toLevel = 4; } else if (armPos < LV5 -
		 * TOLERANCE) { //If the position is less than Level 5, move up to Level 5
		 * manualMove = false; toLevel = 5; } }
		 * 
		 * //This is for a Level Down! if (robot.opXBox.getPOV() == 180 && !povHeld &&
		 * toLevel == -1) { double armPos = robot.armEncoder.get() ; //get the arm
		 * position if (armPos > LV5 + TOLERANCE) { //If the position is greater than
		 * Level 5, move up to Level 5 manualMove = false; toLevel = 5; } else if
		 * (armPos > LV4 + TOLERANCE) { //If the position is greater than Level 4, move
		 * down to Level 4 manualMove = false; toLevel = 4; } else if (armPos > LV3 +
		 * TOLERANCE) { //If the position is greater than Level 3, move down to Level 3
		 * manualMove = false; toLevel = 3; } else if (armPos > LV2 + TOLERANCE) { //If
		 * the position is greater than Level 2, move down to Level 2 manualMove =
		 * false; toLevel = 2; } else if (armPos > LV1 + TOLERANCE) { //If the position
		 * is greater than Level 1, move down the Level 1 manualMove = false; toLevel =
		 * 1; } }
		 */
		if (robot.opXBox.getPOV() == 0 && !povHeld && !throwing) {
			throwing = true;
			manualMove = false;
			toLevel = 4;
		}
		if (robot.opXBox.getPOV() != -1)
			povHeld = true;
		else
			povHeld = false; // Boolean for held button check

		if (robot.opXBox.getRawButton(ARM_LOCK_BUTTON) && !armLockButtonHeld) {
			armLock = !armLock;
		}

		armLockButtonHeld = !robot.opXBox.getRawButton(ARM_LOCK_BUTTON);
	}

	/**
	 * The movingToLevel method is called when the arm is not being moved manually.
	 * It moves the arm to the current level specified. If no valid level is
	 * specified, then it gets set back to manual
	 */
	public void movingToLevel() {
		// Check which level to go to
		switch (toLevel) {
		case 1:
			moveToLevel(LV1); // Move the arm to level 1
			break;
		case 2:
			moveToLevel(LV2); // Move the arm to level 2
			break;
		case 3:
			moveToLevel(LV3); // Move the arm to level 3
			break;
		case 4:
			moveToLevel(LV4); // Move the arm to level 4
			break;
		case 5:
			moveToLevel(LV5); // Move the arm to level 5
			break;
		default:
			manualMove = true; // If neither, set it back to manual
			toLevel = -1;
			robot.armTalon.set(0);
			break;
		}
	}

	/**
	 * The setMoveToLevel method is a modifier method that changes the toLevel
	 * variable. This is used outside the Arm class if something else needs to
	 * change its level (ie: climbing). Note that it does not move the arm to the
	 * current level normally because manualMove may not be set to true.
	 * 
	 * @param level
	 *            The level the Arm needs to go to
	 */
	public void setMoveToLevel(int level) {
		toLevel = level;
	}

	/**
	 * The moveToLevel method is a helper method to have the arm move to a certain
	 * level non-manually in teleop
	 * 
	 * @param level
	 *            The encoder value which to go to
	 */
	public void moveToLevel(double level) {
		double armPos = robot.armEncoder.get(); // Get the arm position

		// This is a formula to figure out how fast the arm should move, slower if it
		// get's closer to its target
		double value = (SLOW_CONS) + (SLOW_CONS * 0.3) * Math.min((Math.abs(armPos - level) / 120.0), 1);

		// Check to see if the arm makes it within the tolerance zone
		if (Math.abs(armPos - level) > TOLERANCE) {
			// Check if the arm position is above or below the level to be at.
			if (armPos < level) {
				// If below, move it up
				if (throwing && !robot.winch.climbing)
					value = 1; // If working to throw, set to max speed
				robot.armTalon.set(-value); /*-- changed for lancaster --*/ // changed the negative
			} else {
				// If above, move it down
				robot.armTalon.set(value * 0.75); /*-- changed for lancaster --*/ // changed the negative
			}
		} else {
			// If it is within bounds, set the arm back to manual
			manualMove = true;
			toLevel = -1;
			robot.armTalon.set(0);
		}
	}

	/**
	 * This is a helper method to have the arm move to a certain level during
	 * autonomous. It's a little bit different as it calls for a while loop instead
	 * of an if statement
	 * 
	 * @param level
	 *            The level to go to (1 - 5)
	 */
	public void autoMoveToLevel(int level) {
		double armPos = robot.armEncoder.get(); // Get the arm position
		double encLevel = 0; // The encoder value to go to
		switch (level) { // Check which level to go to
		case 1:
			encLevel = LV1;
			break;
		case 2:
			encLevel = LV2;
			break;
		case 3:
			encLevel = LV3;
			break;
		case 4:
			encLevel = LV4;
			break;
		case 5:
			encLevel = LV5;
			break;
		default:
			encLevel = armPos; // If not a valid level, set it as the current position (effectively ends it)
			break;
		}

		timer.start(); // Start the timer as a hard stop
		// Check to see if the arm makes it within the tolerance zone
		while (Math.abs(armPos - encLevel) > TOLERANCE && timer.get() < 3) {
			armPos = robot.armEncoder.get(); // Get the arm position
			// This is a formula to figure out how fast the arm should move, slower if it
			// get's closer to its target
			double value = (SLOW_CONS) + (SLOW_CONS * 0.3) * Math.min((Math.abs(armPos - encLevel) / 120.0), 1);

			// Check if the arm position is above or below the level to be at.
			if (armPos < encLevel) {
				// If below, move it up
				robot.armTalon.set(-value);
			} else {
				// If above, move it down
				robot.armTalon.set(value * 0.75);
			}
		}
		robot.armTalon.set(0); /*-- changed for lancaster --*/
		timer.stop(); // Stop and reset the timer
		timer.reset();
	}

	public void autoMoveToEncoder(int encLevel) {
		double armPos = robot.armEncoder.get(); // Get the arm position
		timer.start(); // Start the timer as a hard stop
		// Check to see if the arm makes it within the tolerance zone
		while (Math.abs(armPos - encLevel) > TOLERANCE && timer.get() < 1) {
			armPos = robot.armEncoder.get(); // Get the arm position
			// This is a formula to figure out how fast the arm should move, slower if it
			// get's closer to its target
			double value = (SLOW_CONS) + (SLOW_CONS * 0.3) * Math.min((Math.abs(armPos - encLevel) / 120.0), 1);

			// Check if the arm position is above or below the level to be at.
			if (armPos < encLevel) {
				// If below, move it up
				robot.armTalon.set(-value);
			} else {
				// If above, move it down
				robot.armTalon.set(value * 0.75);
			}
		}
		robot.armTalon.set(0); // added on 4/5/18
		timer.stop(); // Stop and reset the timer
		timer.reset();
	}

	public void shakeArm() {
		timer.reset();
		for (int i = 0; i < 3; i++) {
			timer.start();
			while (timer.get() < 0.11) {
				robot.armTalon.set(0.7);
			}
			while (timer.get() < 0.2) {
				robot.armTalon.set(-0.7);
			}
			timer.stop();
			timer.reset();
		}
	}

	/**
	 * The dashboardWork method is to do SmartDashboard work, placing encoders and
	 * current arm controls onto the SmartDashboard for easy viewing
	 */
	public void dashboardWork() {
		double armPos = robot.armEncoder.get(); // Get the arm position
		SmartDashboard.putNumber("Arm Encoder", armPos); // Place the encoder value on the SmartDashboard
		dashboardLevel(); // Get the position of the arm level wise

		// Check if the arm is currently in manual mode or auto mode
		if (manualMove) {
			SmartDashboard.putString("Arm Control", "MANUAL");
			SmartDashboard.putString("Arm Destination", "N/A");
		} else {
			SmartDashboard.putString("Arm Control", "AUTO");
			SmartDashboard.putString("Arm Destination", "Level " + toLevel);
		}
	}

	/**
	 * This method is to give the position of the arm to put on the SmartDashboard
	 * in a text format
	 */
	public void dashboardLevel() {
		double armPos = robot.armEncoder.get(); // Get the arm position

		// The following if statements are all the place on the SmartDashboard where the
		// arm is
		if (Math.abs(armPos - LV1) <= TOLERANCE)
			SmartDashboard.putString("Arm Position", "Level 1");
		else if (Math.abs(armPos - LV2) <= TOLERANCE)
			SmartDashboard.putString("Arm Position", "Level 2");
		else if (Math.abs(armPos - LV3) <= TOLERANCE)
			SmartDashboard.putString("Arm Position", "Level 3");
		else if (Math.abs(armPos - LV4) <= TOLERANCE)
			SmartDashboard.putString("Arm Position", "Level 4");
		else if (Math.abs(armPos - LV5) <= TOLERANCE)
			SmartDashboard.putString("Arm Position", "Level 5");
		else if (armPos < LV1)
			SmartDashboard.putString("Arm Position", "Below Level 1");
		else if (armPos > LV1 && armPos < LV2)
			SmartDashboard.putString("Arm Position", "Between Level 1 and Level 2");
		else if (armPos > LV2 && armPos < LV3)
			SmartDashboard.putString("Arm Position", "Between Level 2 and Level 3");
		else if (armPos > LV3 && armPos < LV4)
			SmartDashboard.putString("Arm Position", "Between Level 3 and Level 4");
		else if (armPos > LV4 && armPos < LV5)
			SmartDashboard.putString("Arm Position", "Between Level 4 and Level 5");
		else if (armPos > LV5)
			SmartDashboard.putString("Arm Position", "Above Level 5");
		else
			SmartDashboard.putString("Arm Position", "Unsure");
	}

}
