package org.usfirst.frc.team2658.robot;

/**
 * The WheelyIntake class is used for the WheelyIntake functions, which is defined as the wheels on the intake mechanism for pulling and pushing out the
 * box from the intake as well as straightening. There is a total of four buttons used for the WheelyIntake. The Operator Left Trigger is used to rotate the
 * wheels to rotate the box in, the Operator Right Trigger is used to rotate the wheels to rotate the box out, the Operator Left Bumper is used to rotate the
 * wheels left in order to straighten the box, and the Operator Right Bumper is used to rotate the wheels right in order to straighten the box. Note that the
 * triggers for bringing in pushing out the box are axes, so the speeds when using these are variable while the bumpers used to straighten the box are buttons
 * and work on a single speed.
 * 
 * Instance Variable
 * 	- private final double BOX_ROTATE_SPEED
 * 	- private final int XBOX_BUMPER_L
 * 	- private final int XBOX_BUMPER_R
 * 	- private final int XBOX_TRIGGER_L
 * 	- private final int XBOX_TRIGGER_R
 * 	- private final double NULL_AREA
 * 	- private Robot robot
 * Constructor
 * 	- public WheelyIntake(Robot)
 *	Method
 *	- public void run()
 *	- public void rotateBox(String)
 *	- public void boxInt()
 *	- public void boxOut()
 *
 */
public class WheelyIntake extends Thread{
	private final double BOX_ROTATE_SPEED = 0.6; // constant wheel rotating speed
	private final int XBOX_BUMPER_L = 5, XBOX_BUMPER_R = 6; // the ports for the bumpers
	private final int XBOX_TRIGGER_L = 2, XBOX_TRIGGER_R = 3;	//the ports for the triggers
	private final double NULL_AREA = 0; // null area for intake (must actually tap the trigger)
	
	private Robot robot;	//The main Robot object
	
	/**
	 * Constructor for the WheelyIntake, grabs the Robot class from the Robot.java
	 * 
	 * @param r			The robot class to take from
	 * `
	 * Note: This is pretty much cludging it to not make it static.
	 */
	public WheelyIntake(Robot r) {
		robot = r;	//Sets this robot as the passed in Robot
	}
	
	/**
	 * The run method is what is placed in the teleopPeriod to continuously run. Later, it will be used as the Thread's run
	 * method when we use the thread.
	 */
	public void run() {
		//The series of if is to check which buttons are pressed
		if(robot.opXBox.getRawAxis(XBOX_TRIGGER_L) != NULL_AREA) {
			//If the Left Trigger is being pressed, pull the box in
			boxIn();
		}
		else if(robot.opXBox.getRawAxis(XBOX_TRIGGER_R) != NULL_AREA) {
			//If the Right Trigger is being pressed, push the box out
			boxOut();
		}
		else if(robot.opXBox.getRawButton(XBOX_BUMPER_L)) {
			//If the Left Bumper is being pressed, straighten the box left-wise
			rotateBox("l");
		}
		else if(robot.opXBox.getRawButton(XBOX_BUMPER_R)) {
			//If the Right Bumper is being pressed, straighten the box right-wise
			rotateBox("r");
		}
		else {
			//If none of the buttons are being pressed, set both motors as 0 (stopped)
			robot.intakeMotorL.set(0); 
			robot.intakeMotorR.set(0); 
		}
		
	}
	
	/**
	 * The method rotateBox is used in order to rotate the wheels to straighten the box. The wheels rotate the same way with this method
	 * 
	 * @param dir		Which ways the box rotates: "l" for left, "r" for right
	 */
	public void rotateBox(String dir) {
		if(dir.equals("r")) {
			//If the direction is "r", rotate the wheels right at constant speed
			robot.intakeMotorL.set(-BOX_ROTATE_SPEED);
			robot.intakeMotorR.set(-BOX_ROTATE_SPEED);
		}
		else if(dir.equals("l")) {
			//If the direction is "l", rotate the wheels left at constant speed
			robot.intakeMotorL.set(BOX_ROTATE_SPEED);
			robot.intakeMotorR.set(BOX_ROTATE_SPEED);
		}
	}
	
	/**
	 * The method boxIn is used in order to rotate the wheels to pull in the box. It is on a axes so it has variable speed
	 */
	public void boxIn() {
		//Rotate the wheels inwards at a speed based on the axes being pressed
		robot.intakeMotorL.set(robot.opXBox.getRawAxis(XBOX_TRIGGER_L));
		robot.intakeMotorR.set(-robot.opXBox.getRawAxis(XBOX_TRIGGER_L));
	}
	
	/**
	 * The method boxOut is used in order to rotate the wheels to push out the box. It is on a axes so it has variable speed
	 */
	public void boxOut() {
		//Rotate the wheels outwards at a speed based on the axes being pressed
		robot.intakeMotorL.set(-robot.opXBox.getRawAxis(XBOX_TRIGGER_R));
		robot.intakeMotorR.set(robot.opXBox.getRawAxis(XBOX_TRIGGER_R));
	}

}
