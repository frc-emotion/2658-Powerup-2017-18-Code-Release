package org.usfirst.frc.team2658.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoOptions {

	Timer timer = new Timer();
    //PIXELS TO INCHES: .73298429319;
    String fmsMessage = "UUDDLRLRABSS"; // message recived from fms system (random string place holder to avoid Null pointer)
    char switchSide; // side the switch is (R/L)
    char scaleSide; // side the scale is (R/L)
    double distanceTraveled; // how far the robot has traveled based on encoders
    double encoderAvg; // average of right and left encoders
    public final int ERROR_CONSTANT = 0; // error offset (too far, too little fix)
    public final int ROBOT_LENGTH = 39; // how long the robot is 
    public final double ENCODER_DIST = 2; //1 encoder tick equals this many inches
    public final double ENCODER_DEG = 1;
    public int ignore = 1;
    public int autoMethod = 0; // how will auto execute (Timers = 0, Encoders = 1)
    public int robotPosition = -1;
    public final double TIME_FACTOR = .83;	//Edit for auto --> too short, increase, too long, decrease 
    public final double TIME_FACTOR_TURN = .9;	//edit for turns
    
    public Robot robot; // get robot to access drive train, claw, etc
    
    public AutoOptions(Robot robot) { // get robot
    	this.robot = robot;
    	
    }
    
    public void autoInit() {
    	fmsMessage = DriverStation.getInstance().getGameSpecificMessage().toLowerCase(); // get fms message
		switchSide = fmsMessage.charAt(0); // extract switch side from fms
		scaleSide = fmsMessage.charAt(1); //extract scale side from fms
		distanceTraveled = 0; // reset how far robot has traveled
		robot.resetEncoders(); // reset encoders
		robot.updateSmartDashboard(); // update the sd with encoder values and such
		robotPosition = robot.autoChooser.getSelected().intValue();
		ignore = robot.autoIgnore.getSelected().intValue();
		autoMethod = robot.autoMethod.getSelected().intValue();
		robot.claw.closeClaw(); //close claw
    }
    
    public void autoPeriodic() {
		

		
		
		// get chosen pos from sendable chooser
		robotPosition = robot.autoChooser.getSelected().intValue();
		//pauseRobot(3.5);
		switch (robotPosition) {
			case 0:
				//Right Lane
				grabAndLiftBox();
				pauseRobot(0.5);
				rightLane();
				break;
			case 1:
				//Middle Lane
				grabAndLiftBox();
				pauseRobot(0.5);
				middleLane();
				break;
			case 2:
				//Left Lane
				grabAndLiftBox();
				pauseRobot(0.5);
				leftLane();
				break;
			case 3:
				//Forward Move by Time
				SmartDashboard.putString("Auto Did", "TIMED FORWARD");
				moveForwardTimer(SmartDashboard.getNumber("TimeYay", 2));
				break;
			case 4:
				//Turn Right by Time
				SmartDashboard.putString("Auto Did", "TIMED RIGHT TURN");
				turnRTimer(SmartDashboard.getNumber("TimeYay", 2));
				break;
			case 5:
				//Turn Left by Time
				SmartDashboard.putString("Auto Did", "TIMED LEFT TURN");
				turnLTimer(SmartDashboard.getNumber("TimeYay", 2));
				break;
			case 6:
				//Test the Auto Grab
				SmartDashboard.putString("Auto Did", "GRAB TEST");
				grabAndLiftBox();
				pauseRobot(1);
				dropBoxSwitch();
				break;
			default:
				SmartDashboard.putString("Auto Did", "NOTHING");
				break;
		}

    }
    
    private void grabAndLiftBox() {
    	robot.claw.closeClaw();
    	robot.arm.autoMoveToLevel(4);
    }
    private void dropBoxSwitch() {
    	robot.arm.autoMoveToLevel(2);
    	pauseRobot(0.25);
    	robot.claw.openClaw();
    }
    
    private void rightLane() {
    	if(autoMethod == 0) {
	    	if ((switchSide == 'r' || switchSide == 'R') && ignore % 2 != 0) {
	    		//If the switch is on the same side
	    		SmartDashboard.putString("Auto Did", "RIGHT - SWITCH");
	    		moveForwardTimer(2.2 * TIME_FACTOR);
	    		pauseRobot(0.25);
	    		turnLTimer(0.32 * TIME_FACTOR_TURN);
	    		pauseRobot(0.25);
	    		moveForwardTimer(0.6 * TIME_FACTOR);
	    		dropBoxSwitch();
	    		pauseRobot(1.1);
	    		moveBackwardTimer(0.5);
	    	}
	    	else if ((scaleSide == 'r' || scaleSide == 'R') && ignore % 3 != 0) {
	    		//If the scale is on the same side but the switch is not
	    		SmartDashboard.putString("Auto Did", "RIGHT - SCALE");
	    		moveForwardTimer(4 * TIME_FACTOR);
	    	}
	    	else {
	    		//If none are on the same side
	    		SmartDashboard.putString("Auto Did", "RIGHT - NONE");
	    		moveForwardTimer(3 * TIME_FACTOR);
	    	}
    	}
    	else if(autoMethod == 1) {
    		//auto with encoders
    		if ((switchSide == 'r' || switchSide == 'R') && ignore % 2 != 0) {
	    		//If the switch is on the same side
	    		SmartDashboard.putString("Auto Did", "RIGHT - SWITCH");
	    	
	    	}
	    	else if ((scaleSide == 'r' || scaleSide == 'R') && ignore % 3 != 0) {
	    		//If the scale is on the same side but the switch is not
	    		SmartDashboard.putString("Auto Did", "RIGHT - SCALE");
	    		
	    	}
	    	else {
	    		//If none are on the same side
	    		SmartDashboard.putString("Auto Did", "RIGHT - NONE");
	    		
	    	}
    	}
    }
    
    private void leftLane() {
    	if(autoMethod == 0) { 
    		//auto with timer
	    	if ((switchSide == 'l' || switchSide == 'L') && ignore % 2 != 0) {
	    		//If the switch is on the same side
	    		SmartDashboard.putString("Auto Did", "LEFT - SWITCH");
	    		moveForwardTimer(2.2 * TIME_FACTOR);
	    		pauseRobot(0.25);
	    		turnRTimer(0.32 * TIME_FACTOR_TURN);
	    		pauseRobot(0.25);
	    		moveForwardTimer(0.6 * TIME_FACTOR);
	    		dropBoxSwitch();
	    		pauseRobot(1.1);
	    		moveBackwardTimer(0.5);
	    	}
	    	else if ((scaleSide== 'l' || scaleSide == 'L') && ignore % 3 != 0) {
	    		//If the scale is on the same side and the switch is not
	    		SmartDashboard.putString("Auto Did", "LEFT - SCALE");
	    		moveForwardTimer(4 * TIME_FACTOR);
	    	}
	    	else {
	    		//If none are on the same side
	    		SmartDashboard.putString("Auto Did", "LEFT - NONE");
	    		moveForwardTimer(3 * TIME_FACTOR);
	    	}
    	}
    	else if (autoMethod == 1) {
    		// auto with encoders
    		if ((switchSide == 'l' || switchSide == 'L') && ignore % 2 != 0) {
	    		//If the switch is on the same side
	    		SmartDashboard.putString("Auto Did", "LEFT - SWITCH");
	    	}
	    	else if ((scaleSide== 'l' || scaleSide == 'L') && ignore % 3 != 0) {
	    		//If the scale is on the same side and the switch is not
	    		SmartDashboard.putString("Auto Did", "LEFT - SCALE");
	    		
	    	}
	    	else {
	    		//If none are on the same side
	    		SmartDashboard.putString("Auto Did", "LEFT - NONE");
	    		
	    	}
    	}
    }
    
    private void middleLane() {
    	if(autoMethod == 0) {
    		//auto with timers
	    	moveForwardTimer(0.7 * TIME_FACTOR);
	    	pauseRobot(0.5);
	    	if (switchSide == 'r' || switchSide == 'R') {
	    		SmartDashboard.putString("Auto Did", "MIDDLE - RIGHT SWITCH");
	    		turnRTimer(0.15 * TIME_FACTOR_TURN);
	    		pauseRobot(0.5);
	    		moveForwardTimer(1.1 * TIME_FACTOR);
	    		pauseRobot(0.5);
	    		turnLTimer(0.15 * TIME_FACTOR_TURN);
	    		pauseRobot(0.5);
	    		moveForwardTimer(1.4 * TIME_FACTOR);
	    		dropBoxSwitch();
	    		pauseRobot(1.1);
	    		moveBackwardTimer(0.5);
	    	}
	    	else if (switchSide == 'l' || switchSide == 'L') {
	    		SmartDashboard.putString("Auto Did", "MIDDLE - LEFT SWITCH");
	    		turnLTimer(0.15 * TIME_FACTOR_TURN);
	    		pauseRobot(0.5);
	    		moveForwardTimer(1.3 * TIME_FACTOR);
	    		pauseRobot(0.5);
	    		turnRTimer(0.15 * TIME_FACTOR_TURN);
	    		pauseRobot(0.5);
	    		moveForwardTimer(1.4 * TIME_FACTOR);
	    		dropBoxSwitch();
	    		pauseRobot(1.1);
	    		moveBackwardTimer(0.5);
	    	}
	    	else {
	    		SmartDashboard.putString("Auto Did", "MIDDLE - NONE");
	    		turnLTimer(0.15 * TIME_FACTOR_TURN);
	    		pauseRobot(0.5);
	    		moveForwardTimer(1 * TIME_FACTOR);
	    		pauseRobot(0.5);
	    		turnRTimer(0.15 * TIME_FACTOR_TURN);
	    		pauseRobot(0.5);
	    		moveForwardTimer(1 * TIME_FACTOR);
	    	}
    	}
    	else if(autoMethod == 1) {
    		if (switchSide == 'r' || switchSide == 'R') {
	    		SmartDashboard.putString("Auto Did", "MIDDLE - RIGHT SWITCH");
	    		
	    	}
	    	else if (switchSide == 'l' || switchSide == 'L') {
	    		SmartDashboard.putString("Auto Did", "MIDDLE - LEFT SWITCH");
	    		
	    	}
	    	else {
	    		SmartDashboard.putString("Auto Did", "MIDDLE - NONE");
	    	
	    	}
    	}
    }

    public void moveForwardTimer(double limit) { 
    	// cross autoline using timers
    	timer.start();
    	// while timer is less than limit
    	while(timer.get() <= limit) {
    		robot.driveTrain.arcadeDrive(0.7, 0);
    		
    	}
    	timer.stop();
        timer.reset();
        robot.driveTrain.arcadeDrive(0, 0);
    }
    
    public void moveBackwardTimer(double limit) { 
    	// cross autoline using timers
    	timer.start();
    	// while timer is less than limit
    	while(timer.get() <= limit) {
    		robot.driveTrain.arcadeDrive(-0.7, 0);
    		
    	}
    	timer.stop();
        timer.reset();
        robot.driveTrain.arcadeDrive(0, 0);
    }
    
    private void turnRTimer(double time) {
    	timer.reset();
    	timer.start();
    	while(timer.get() <= time) {
    		robot.driveTrain.arcadeDrive(0, 1);
    	}
    	timer.stop();
        timer.reset();
    }
    private void turnLTimer(double time) {
    	timer.reset();
    	timer.start();
    	while(timer.get() <= time) {
    		robot.driveTrain.arcadeDrive(0, -1);
    	}
    	timer.stop();
        timer.reset();
    }
    
    
    private void moveForwardEnc(double distance, double maxPower) {
    	resetDriveEncoders();
    	double drivePower = maxPower; // set power
        double distLeft = distance - ROBOT_LENGTH; // calculate how far the robot needs to travel
        double oldEnc = 0; 
        
        // while robot hasnt met distance or took too long to get there ( must be under 4 sec)
        while(distanceTraveled < distLeft && timer.get() < 4){
        	updateDriveEncoders(); // update encoder average
        	robot.updateSmartDashboard(); // update dashboard
        	
            // decrement drive power until it is there to slow down to a stop
        	distanceTraveled = ENCODER_DIST * Math.abs(encoderAvg);
           
        	//decrement drive power until you reach there to prevent coasting
        	drivePower = (maxPower * 0.75) + (maxPower * 0.25) * ((distLeft - distanceTraveled)/distLeft);
            
            // drive there yay
            robot.driveTrain.tankDrive(drivePower, drivePower);
           
            if (isStopped(encoderAvg, oldEnc, 5)) { // see if the robot has stopped
            	if (timer.get() == 0) timer.start(); // start the timer
            }
            else {
            	oldEnc = encoderAvg;
            	if (timer.get() > 0) { // reset timer 
            		timer.stop();
            		timer.reset();
            	}	
            }

        }
        // reset encoder related values and timer values
        timer.stop();
        timer.reset();
        distanceTraveled = 0;
    	
    }
    private void moveBackwardEnc(double distance, double maxPower) {
    	
    	resetDriveEncoders();
    	double drivePower = maxPower; // set power
        double distLeft = distance - ROBOT_LENGTH; // calculate how far the robot needs to travel
        double oldEnc = 0; 
        
        // while robot hasnt met distance or took too long to get there ( must be under 4 sec)
        while(distanceTraveled < distLeft && timer.get() < 4){
        	updateDriveEncoders(); // update encoder average
        	robot.updateSmartDashboard(); // update dashboard
        	
            // decrement drive power until it is there to slow down to a stop
        	distanceTraveled = ENCODER_DIST * Math.abs(encoderAvg);
           
        	//decrement drive power until you reach there to prevent coasting
        	drivePower = (maxPower * 0.75) + (maxPower * 0.25) * ((distLeft - distanceTraveled)/distLeft);
            
            // drive there yay
            robot.driveTrain.tankDrive(-drivePower, -drivePower);
           
            if (isStopped(encoderAvg, oldEnc, 5)) { // see if the robot has stopped
            	if (timer.get() == 0) timer.start(); // start the timer
            }
            else {
            	oldEnc = encoderAvg;
            	if (timer.get() > 0) { // reset timer 
            		timer.stop();
            		timer.reset();
            	}	
            }

        }
        // reset encoder related values and timer values
        timer.stop();
        timer.reset();
        distanceTraveled = 0;
    	
    }
    private void turnLEnc(double angle) {
    	resetDriveEncoders();
    	double drivePower = 1; // set speed
        double distLeft = (angle/360 * 40.0 * Math.PI);// + ERROR_CONSTANT;  // convert degrees to encoder values using this formula
        double oldEnc = 0;
        
        
        // while not there yet...  (targetdistance < currentDistance) & (timeRunning < 2) 
        while(distanceTraveled < distLeft && timer.get() < 2){
        	updateDriveEncoders(); // update encoders
        	robot.updateSmartDashboard();
        	
            // calc how far you have traveled
        	distanceTraveled = robot.ENCODER_DISTANCE * Math.abs(encoderAvg);
        	// decrement drive power until it is there to slow down to a stop
            drivePower = (1 * 0.9) + (1 * 0.1) * ((distLeft - distanceTraveled)/distLeft);
            // drive there yay
            robot.driveTrain.arcadeDrive(0, -drivePower);
            if (isStopped(encoderAvg, oldEnc, 5)) {
            	if (timer.get() == 0) timer.start();
            }
            else {
            	oldEnc = encoderAvg;
            	if (timer.get() > 0) {
            		timer.stop();
            		timer.reset();
            	}	
            }

        }
        //stop turning
        robot.driveTrain.arcadeDrive(0, 0);
        // reset encoder related values
        timer.stop();
        timer.reset();
        distanceTraveled = 0;
    }
    private void turnREnc(double angle) {
    	resetDriveEncoders();
    	double drivePower = 1; // set speed
        double distLeft = (angle/360 * 40.0 * Math.PI);// + ERROR_CONSTANT;  // convert degrees to encoder values using this formula
        double oldEnc = 0;
        
        
        // while not there yet...  (targetdistance < currentDistance) & (timeRunning < 2) 
        while(distanceTraveled < distLeft && timer.get() < 2){
        	updateDriveEncoders(); // update encoders
        	robot.updateSmartDashboard();
        	
            // calc how far you have traveled
        	distanceTraveled = robot.ENCODER_DISTANCE * Math.abs(encoderAvg);
        	// decrement drive power until it is there to slow down to a stop
            drivePower = (1 * 0.9) + (1 * 0.1) * ((distLeft - distanceTraveled)/distLeft);
            // drive there yay
            robot.driveTrain.arcadeDrive(0, drivePower);
            if (isStopped(encoderAvg, oldEnc, 5)) {
            	if (timer.get() == 0) timer.start();
            }
            else {
            	oldEnc = encoderAvg;
            	if (timer.get() > 0) {
            		timer.stop();
            		timer.reset();
            	}	
            }

        }
        //stop turning
        robot.driveTrain.arcadeDrive(0, 0);
        // reset encoder related values
        timer.stop();
        timer.reset();
        distanceTraveled = 0;
    }
    
    public void pauseRobot (double time) {
    	timer.reset();
    	timer.start();
    	while (timer.get() < time) {
    		
    	}
    	timer.stop();
    	timer.reset();
    }
    
    
    public void resetDriveEncoders() {
    	robot.rEncoder.reset();
    	robot.lEncoder.reset();
    }
    public void updateDriveEncoders() {
    	encoderAvg = (robot.rEncoder.get() + robot.lEncoder.get())/2; // calculate encoder average
    }
    // check to see if the robot is stopped
    private boolean isStopped(double newEnc, double oldEnc, double tolerance) {
    	return (Math.abs(newEnc - oldEnc) <= tolerance); // check if the robot is stopped based on distance needed to 
    													// go and how far the robot has traveled
    }
}
