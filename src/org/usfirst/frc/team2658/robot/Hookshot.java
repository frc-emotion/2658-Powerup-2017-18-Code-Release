package org.usfirst.frc.team2658.robot;

/**
 * The Hookshot class is used to operate the functions on the Hookshot, defined as the small Solenoid on the back of the intake Pacman that
 * pushes or pulls the intake mechanism forward or backward. It isn't actually used directly by teleop but in conjunction with the Pacman in
 * order to operate the entire intake mechanism. Note that kReverse is considered Closed for the Hookshot and kForward is considered Open for
 * the Hooshot.
 * 
 * Instance Variable
 * 	- private Robot robot
 * Constructor
 * 	- public Hookshot(Robot)
 * Methods
 * 	- public void run()
 * 	- public void activateForward()
 * 	- public void activateReverse()
 * 	- public void ganon()
 * 	- public boolean isClosed()
 * 	- public boolean isOpen()
 * 	- public void dashboardWork()
 */

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Hookshot extends Thread{
	
	private Robot robot;		//The main Robot object
	
	/**
	 * Constructor for the Hookshot, grabs the Robot class from the Robot.java
	 * 
	 * @param r			The robot class to take from
	 * 
	 * Note: This is pretty much cludging it to not make it static.
	 */
	public Hookshot(Robot r) {
		robot = r;		//Sets this robot object as the passed parameter Robot object
	}
	
	/**
	 * The run method is what is placed in the teleopPeriod to continuously run. Later, it will be used as the Thread's run
	 * method when we use the thread. The Hookshot does not have any body for the run method as it is being used in conjunction with
	 * the Pacman, and, therefore, the teleop code for it is all in the Pacman class
	 */
	public void run()
	{
		//Empty, see the Pacman class
	}
	
	/**
	 * The activateForward method is used to bring the Hookshot out, or in its open position.
	 */
	public void openHookshot()
	{
		robot.hookshotSolenoid.set(DoubleSolenoid.Value.kForward); //Brings the Hookshot out
	}
	
	/**
	 * The activateReverse method is used to bring the Hookshot back in, or in its close position
	 */
	public void closeHookshot()
	{	
		robot.hookshotSolenoid.set(DoubleSolenoid.Value.kReverse); //Brings the Hookshot back
	}
	
	/**
	 * The ganon method is used to completely shut down the Hookshot. This is a reference towards Legend of Zelda, as the Hookshot
	 * is a tool that Link, the main character, uses while Ganon is the main antagonist of the series.
	 */
	public void ganon()
	{
		robot.hookshotSolenoid.set(DoubleSolenoid.Value.kOff); //Turn off the Hookshot
	}
	
	/**
	 * The isClosed method is used to check if the Hookshot is currently pulled in.
	 * 
	 * @return	true if the Hookshot is currently closed, false if otherwise
	 */
	public boolean isClosed() {
		return (robot.hookshotSolenoid.get() == DoubleSolenoid.Value.kReverse);
	}
	
	/**
	 * The isOpen method is ued to check if the Hookshot is currently pushed out.
	 * 
	 * @return	false if the Hookshot is currently open, false if otherwise
	 */
	public boolean isOpen() {
		return (robot.hookshotSolenoid.get() == DoubleSolenoid.Value.kForward);
	}
	
	/**
	 * The dashboardWork method is used to place String values on the Smartdashboard in order to get the current position of the Hookshot
	 * in a readable format.
	 */
	public void dashboardWork() {
		if (isClosed()) {
			SmartDashboard.putString("Hookshot Position", "CLOSE");		//The Hookshot is closed
		}
		else if (isOpen()){
			SmartDashboard.putString("Hookshot Position", "OPEN");		//The Hookshot is open
		}
	}
}
