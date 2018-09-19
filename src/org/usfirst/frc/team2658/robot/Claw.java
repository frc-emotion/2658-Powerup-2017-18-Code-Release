package org.usfirst.frc.team2658.robot;

/**
 * The Claw class is used for the functions of the Claw which is defined as the hand on the end of the Arm that opens and closes. It simply
 * has functions that opens and closes the claw based off an operator button toggle (Operator B-Button). Note that kReverse on the Claw is
 * Open while kForward on the Claw is closed.
 * 
 * Instance Variables
 * 	- private final int XBOX_CLAW_BUTTON
 * 	- private boolean imToggled
 * 	- private Robot robot
 * Constructor
 * 	- public Claw (Robot)
 * Methods
 * 	- public void run()
 * 	- public void closeClaw()
 * 	- public void openClaw()
 * 	- public void clawOff()
 * 	- public boolean isOpen()
 * 	- public boolean isClose()
 * 	- public void dashboardWork()
 */

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Claw extends Thread{
	private final int XBOX_CLAW_BUTTON = 2;		//The port number button for the Claw toggle (B-Button)
	private boolean imToggled = false;			//The boolean used to tell if the button is being held or not
	
	private Robot robot;		//The main Robot class
	
	/**
	 * Constructor for the Claw, grabs the Robot class from the Robot.java
	 * 
	 * @param robot		The robot class to take from
	 * `
	 * Note: This is pretty much cludging it to not make it static.
	 */
	public Claw(Robot robot) {
		this.robot = robot;		//Sets this robot object as the passed by robot object
	}
	
	/**
	 * The run method is what is placed in the teleopPeriod to continuously run. Later, it will be used as the Thread's run
	 * method when we use the thread.
	 */
	public void run() {
		if(robot.opXBox.getRawButton(XBOX_CLAW_BUTTON)&& !imToggled) // if clicked and not being held currently
		{
			//Is the claw currently open?
			if (isOpen()) {
				closeClaw(); //close the claw
			}	
			else {
				openClaw();	//open the claw
			}
		}
		imToggled = robot.opXBox.getRawButton(XBOX_CLAW_BUTTON);	//sets the boolean as held
	}

	/**
	 * The closeClaw method is called when the Claw needs to be closed
	 */
	public void closeClaw() {
		robot.clawSolenoid.set(DoubleSolenoid.Value.kForward);		//Close the Claw
	}

	/**
	 * The openClaw method is called when the Claw needs to be open
	 */
	public void openClaw() {
		robot.clawSolenoid.set(DoubleSolenoid.Value.kReverse);		//Open the Claw
	}

	/**
	 * The clawOff method is used to turn off the pneumatics for the Claw entirely. This is normally never used.
	 */
	public void clawOff() {
		robot.clawSolenoid.set(DoubleSolenoid.Value.kOff);		//Completely shut off the claw
	}
	
	/**
	 * The isOpen method is used to tell if the Claw is currently open
	 * 
	 * @return		true if the Claw is open, false otherwise
	 */
	public boolean isOpen() {
		return (robot.clawSolenoid.get() == DoubleSolenoid.Value.kReverse);
	}
	
	/**
	 * The isClose method is used to tell if the Claw is currently closed
	 * 
	 * @return		true if the Claw is closed, false otherwise
	 */
	public boolean isClose() {
		return (robot.clawSolenoid.get() == DoubleSolenoid.Value.kForward);
	}

	/**
	 * The dashboardWork method is used to place a String on the SmartDashboard telling the current position of the Claw
	 */
	public void dashboardWork() {
		if (isClose()) {
			SmartDashboard.putString("Claw Position", "CLOSE");		//The Claw is closed
		}
		else if (isOpen()) {
			SmartDashboard.putString("Claw Position", "OPEN");		//The Claw is open
		}
		else {
			SmartDashboard.putString("Claw Position", "OFF");		//The Claw is off
		}
	}
}
