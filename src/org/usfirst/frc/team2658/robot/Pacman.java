package org.usfirst.frc.team2658.robot;

/**
 * The Pacman class is used the operate the Pacman functions, defined the to be the opening and closing of the intake claw, but not the
 * forward and backward motion of it (Hookshot). The Pacman simply opens and opens the intake in conjunction with the Hookshot using three
 * different buttons for the three different configs. The OPEN configuration (Pacman OPEN, Hookshot OPEN) is on the Operator A-Button,
 * the CLOSED configuration (Pacman CLOSED, Hookshot OPEN) is on the Operator X-Button, and the BOOSTED CLOSED configuration (Pacman
 * CLOSED, Hookshot CLOSED) is on the Operator Y-Button. OPEN is when the intake is fully open to take in a box, CLOSED is a small close to
 * hold a box when it is held long-wise (not on it's side), and BOOSTED CLOSED is a full close that either takes in the entire intake mechanism
 * or used to fully clamp on a box if it is short-wise (on it's side). Note that kReverse it the open position for Pacman and kForward is the
 * closed position for Pacman.
 * 
 * Instance Variables
 * 	- private final int OPEN_BUTTON
 * 	- private boolean openHeld
 * 	- private final int CLOSE_BUTTON
 * 	- private boolean closeHeld
 * 	- private final int BOOSTED_CLOSE_BUTTON
 * 	- private boolean boostedCloseHeld
 * 	- private Robot robot
 * Constructor
 * 	- public Pacman(Robot)
 * Methods
 * 	- public void run()
 * 	- public void openPacman()
 * 	- public void closePacman()
 * 	- public void blinkyPinkyInkyClyde()
 * 	- public void open()
 * 	- public void close()
 * 	- public void boostedClose()
 * 	- public boolean isOpen()
 * 	- public boolean isClosed()
 * 	- public int currentAccPosition()
 * 	- public void dashboardWork()
 */

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

// mostly authored by Navid Boloorian

public class Pacman extends Thread{
	
	private final int OPEN_BUTTON = 1; 		//The Operator button to open the intake (A-Button)
	private boolean openHeld = false;		//The boolean used to tell if the open button is being held
	private final int CLOSE_BUTTON = 3;		//The Operator button to close the intake (X-Button)
	private boolean closeHeld = false;		//The boolean used to tell if the close button is being held
	private final int BOOSTED_CLOSE_BUTTON = 4;	//The Operator button to boost close the intake (Y-Button)
	private boolean boostedCloseHeld = false;	//The boolean used to tell if the boosted close button is being held
	
	private Robot robot;	//The main Robot object
	
	/**
	 * Constructor for the Pacman, grabs the Robot class from the Robot.java
	 * 
	 * @param r			The robot class to take from
	 * 
	 * Note: This is pretty much cludging it to not make it static.
	 */
	public Pacman(Robot r) {
		robot = r;		//Sets this robot object as the passed parameter Robot object
	}
	
	/**
	 * The run method is what is placed in the teleopPeriod to continuously run. Later, it will be used as the Thread's run
	 * method when we use the thread. The Pacman class also uses the Hookshot methods to run the entire intake mechanism.
	 */
	public void run()
	{
		//If the open button is clicked and not being held, open the intake
		if (robot.opXBox.getRawButton(OPEN_BUTTON) && !openHeld) open();
		//If the close button is clicked and not being held, close the intake
		if (robot.opXBox.getRawButton(CLOSE_BUTTON) && !closeHeld) close();
		//If the boosted close button is clicked and not being held, boost close the intake
		if (robot.opXBox.getRawButton(BOOSTED_CLOSE_BUTTON) && !boostedCloseHeld) boostedClose();
		
		//The following three lines is used to tell if each of the three button is being held
		openHeld = robot.opXBox.getRawButton(OPEN_BUTTON);
		closeHeld = robot.opXBox.getRawButton(CLOSE_BUTTON);
		boostedCloseHeld = robot.opXBox.getRawButton(BOOSTED_CLOSE_BUTTON);
	}
	
	/**
	 * The openPacman method is used to open the Pacman, the intake grabber
	 */
	public void openPacman()
	{
		robot.pacmanSolenoid.set(DoubleSolenoid.Value.kReverse); //open the Pacman
	}
	
	/**
	 * The closePacman method is used to close the Pacman, the intake grabber
	 */
	public void closePacman()
	{	
		robot.pacmanSolenoid.set(DoubleSolenoid.Value.kForward); //close the Pacman
	}
	
	/**
	 * The blinkyPinkyInkyClyde method is used to completely shut off the Pacman. This is reference to the four ghosts in Pacman
	 * which kills Pacman upon touching them.
	 */
	public void blinkyPinkyInkyClyde()
	{
		robot.pacmanSolenoid.set(DoubleSolenoid.Value.kOff); //completely shutoff the Pacman
	}
	
	/**
	 * The open method is used to tell the entire intake mechanism to open (not just Pacman)
	 */
	public void open() {
		openPacman();		//Open Pacman
		robot.hookshot.openHookshot();	//Open Hookshot
	}
	
	/**
	 * The close method is used to tell the entire intake mechanism to close (not just Pacman)
	 */
	public void close() {
		closePacman();		//Close Pacman
		robot.hookshot.openHookshot();	//Open Hookshot
	}
	
	/**
	 * The boostedClose method is used to tell the entire intake mechanism to boost close
	 */
	public void boostedClose() {
		closePacman();		//Close Pacman
		robot.hookshot.closeHookshot();	//Close Hookshot
	}
	
	/**
	 * The isOpen method is used to tell if the Pacman is currently in its open position
	 * 
	 * @return	true if the Pacman is open, false if otherwise
	 */
	public boolean isOpen() {
		return (robot.pacmanSolenoid.get() == DoubleSolenoid.Value.kReverse);
	}
	
	/**
	 * The isClosed method is used to tell if the Pacman is currently in its closed position
	 * 
	 * @return	true if the Pacman is closed, false if otherwise
	 */
	public boolean isClosed() {
		return (robot.pacmanSolenoid.get() == DoubleSolenoid.Value.kForward);
	}
	
	/**
	 * The currentAccPosition method is used to tell what position the intake (accumulator) is currently in (OPEN, CLOSED, BOOSTED CLOSED)
	 * 
	 * @return	1 if the intake is BOOSTED CLOSED, 2 if the intake is CLOSED, 3 if the intake is OPEN, and -1 if anything else
	 */
	public int currentAccPosition() {
		if (isOpen() && robot.hookshot.isOpen()) {
			return 3;	//The intake is OPEN
		}
		else if (isClosed() && robot.hookshot.isOpen()) {
			return 2;	//The intake is CLOSED
		}
		else if (isClosed() && robot.hookshot.isClosed()) {
			return 1;	//The intake is BOOSTED CLOSED
		}
		else {
			return -1;
		}
	}
	
	/**
	 * The dashboardWork method is used to place Strings on the Smart Dashboard. It places the current intake mechanism position as well
	 * as the position of the Pacman.
	 */
	public void dashboardWork() {
		//The first switch statement is to tell what position the intake is currently in
		switch (currentAccPosition()) {
			case 1:
				//The accumulator (intake) is BOOSTED CLOSED
				SmartDashboard.putString("Accumulator Position", "BOOSTED CLOSED");
				break;
			case 2:
				//The accumulator (intake) is CLOSED
				SmartDashboard.putString("Accumulator Position", "CLOSED");
				break;
			case 3:
				//The accumulator (intake) is OPEN
				SmartDashboard.putString("Accumulator Position", "OPEN");
				break;
			default:
				//The accumulator (intake) is in a position that is not normal
				SmartDashboard.putString("Accumulator Position", "UNSURE");
				break;
		}
		
		//The next following statements is to tell what position the Pacman is currently in
		if (isOpen()) {
			//The Pacman is OPEN
			SmartDashboard.putString("Pacman Position", "OPEN");
		}
		else if (isClosed()){
			//The Pacman is CLOSED
			SmartDashboard.putString("Pacman Position", "CLOSED");
		}
		else {
			//The Pacman is in a position that is not normal (probably off)
			SmartDashboard.putString("Pacman Position", "UNSURE");
		}
	}
}