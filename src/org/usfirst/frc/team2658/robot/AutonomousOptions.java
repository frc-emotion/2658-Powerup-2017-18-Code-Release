package org.usfirst.frc.team2658.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
/**
 * Autonomous runs the autonomous code for the robot. When the game starts this code will instruct the robot on what
 * to do for the first 15 seconds. When the autonomous period is done the thread should break and the teleoperated period 
 * should begin. 
 * 
 * Instance Variables:
 * - Timer timer 
 * - String fmsMessage 
 * - char switchSide;
 * - char scaleSide;
 * - double distanceTraveled;
 * - double encoderAvg;
 * - public final int ERROR_CONSTANT 
 * - public final int ROBOT_LENGTH 
 *  
 *  Constructor:
 *  - public AutonomousOptions(Robot robot)
 *  
 *  Methods:
 *  - public void autoInit()
 *  - public void autoPeriodic()
 *  - private void moveForward(double, double)
 *  - private void turn(double, double, int)
 *  - private void turn(double, double)
 *  - private void resetEncoders()
 *  - private boolean isStopped(double, double, double)
 *  - public void straightDiagL()
 *  - public void crossLine()
 *  - public void crossLineTimer()
 *  - public void straightDiagR()
 *  - public void straightDiagRTest()
 *  - public void straight()
 *  - public void nintendoSwitch(char )
 *
 *
 *
 */

public class AutonomousOptions extends Thread{
	
    Timer timer = new Timer();
    //PIXELS TO INCHES: .73298429319;
    String fmsMessage = "UUDDLRLRABSS"; // message recived from fms system (random string place holder to avoid Null pointer)
    char switchSide; // side the switch is (R/L)
    char scaleSide; // side the scale is (R/L)
    double distanceTraveled; // how far the robot has traveled based on encoders
    double encoderAvg; // average of right and left encoders
    public final int ERROR_CONSTANT = 0; // error offset (too far, too little fix)
    public final int ROBOT_LENGTH = 39; // how long the robot is 
    public int ignore = 1;
    public int robotPosition = -1;
    public final double TIME_FACTOR = .83;	//Edit for auto --> too short, increase, too long, decrease 
    public final double TIME_FACTOR2 = .9;	//edit for turns /-- changed for lancaster --/
    
    public Robot robot; // get robot to access drive train, claw, etc
    
    public AutonomousOptions(Robot robot) { // get robot
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
		robot.claw.closeClaw(); //close claw
    }
    
    public void autoPeriodic() {
		encoderAvg = (robot.rEncoder.get() + robot.lEncoder.get())/2; // calculate encoder average
		distanceTraveled = robot.ENCODER_DISTANCE * Math.abs(encoderAvg); // set how far 

		
		
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
		
		// COMMENTED PART THAT USES ENCODERS FOR AUTONOMOUS BUT WE DONT HAVE ENCODER

//        switch(robotPosition){
//            case 0: //right side
//                if(scaleSide == 'r'){
//                    straight();
//                }
//                else if(scaleSide == 'l'){
//                    straightDiagL();
//                }
//                else {
//                	crossLine();
//                }
//                break;
//            case 1:
//            	if (switchSide == 'l' || switchSide == 'r') {
//            		nintendoSwitch(switchSide);
//            	}
//            	else {
//            		crossLine();
//            	}
//                break;
//            case 2:
//                if(scaleSide == 'r'){
//                    straight();
//                }
//                else if(scaleSide == 'l'){
//                    straightDiagR();
//                }
//                else {
//                	crossLine();
//                }
//                break;
//            default:
//                crossLine();
//                break;
//
//        }
		///straightDiagRTest();


    }
    
    private void grabAndLiftBox() {
    	/*robot.claw.openClaw();
    	//robot.arm.autoMoveToEncoder(30);
    	pauseRobot(0.5);
    	moveForwardTimer(0.2);
    	pauseRobot(0.5);*/
    	robot.claw.closeClaw();
    	//robot.arm.shakeArm();		/-- changed for lancaster --/
    	robot.arm.autoMoveToLevel(3);	/*-- changed for lancaster --*/
    }
    private void dropBoxSwitch() {
    	robot.arm.autoMoveToLevel(2);	/*-- changed for lancaster --*/
    	pauseRobot(0.25);				/*-- changed for lancaster --*/
    	robot.claw.openClaw();
    }
    
    
    
    private void rightLane() {
    	
    	if ((switchSide == 'r' || switchSide == 'R') && ignore % 2 != 0) {
    		//If the switch is on the same side
    		SmartDashboard.putString("Auto Did", "RIGHT - SWITCH");
    		moveForwardTimer(2.2 * TIME_FACTOR);
    		pauseRobot(0.25);
    		turnLTimer(0.32 * TIME_FACTOR2);	/*-- changed for lancaster --*/
    		pauseRobot(0.25);
    		moveForwardTimer(0.6 * TIME_FACTOR);
    		dropBoxSwitch();
    		pauseRobot(1.1);
    		moveBackwardTimer(0.5);
    		//turnLTimer(.1 * TIME_FACTOR);		/*-- changed for lancaster --*/
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
    
    
    private void leftLane() {
    	if ((switchSide == 'l' || switchSide == 'L') && ignore % 2 != 0) {
    		//If the switch is on the same side
    		SmartDashboard.putString("Auto Did", "LEFT - SWITCH");
    		moveForwardTimer(2.2 * TIME_FACTOR);
    		pauseRobot(0.25);
    		turnRTimer(0.32 * TIME_FACTOR2);		/*-- changed for lancaster --*/
    		pauseRobot(0.25);
    		moveForwardTimer(0.6 * TIME_FACTOR);
    		dropBoxSwitch();
    		pauseRobot(1.1);
    		moveBackwardTimer(0.5);
    		//turnRTimer(.1 * TIME_FACTOR);			/*-- changed for lancaster --*/
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
    
    private void middleLane() {
    	moveForwardTimer(0.7 * TIME_FACTOR);
    	pauseRobot(0.5);
    	if (switchSide == 'r' || switchSide == 'R') {
    		SmartDashboard.putString("Auto Did", "MIDDLE - RIGHT SWITCH");
    		turnRTimer(0.15 * TIME_FACTOR);
    		pauseRobot(0.5);
    		moveForwardTimer(1.1 * TIME_FACTOR);
    		pauseRobot(0.5);
    		turnLTimer(0.15 * TIME_FACTOR);
    		pauseRobot(0.5);
    		moveForwardTimer(1.4 * TIME_FACTOR);
    		dropBoxSwitch();
    		pauseRobot(1.1);
    		moveBackwardTimer(0.5);
    	}
    	else if (switchSide == 'l' || switchSide == 'L') {
    		SmartDashboard.putString("Auto Did", "MIDDLE - LEFT SWITCH");
    		turnLTimer(0.15 * TIME_FACTOR);
    		pauseRobot(0.5);
    		moveForwardTimer(1.3 * TIME_FACTOR);
    		pauseRobot(0.5);
    		turnRTimer(0.15 * TIME_FACTOR);
    		pauseRobot(0.5);
    		moveForwardTimer(1.4 * TIME_FACTOR);
    		dropBoxSwitch();
    		pauseRobot(1.1);
    		moveBackwardTimer(0.5);
    	}
    	else {
    		SmartDashboard.putString("Auto Did", "MIDDLE - NONE");
    		turnLTimer(0.15 * TIME_FACTOR);
    		pauseRobot(0.5);
    		moveForwardTimer(1 * TIME_FACTOR);
    		pauseRobot(0.5);
    		turnRTimer(0.15 * TIME_FACTOR);
    		pauseRobot(0.5);
    		moveForwardTimer(1 * TIME_FACTOR);
    	}
    }
    
    private void moveForward(double distance, double power){ //move forward at a set distance and speed
    	resetEncoders(); 
        double drivePower = power; // set power
        double robotDistance = (distance + ERROR_CONSTANT) - ROBOT_LENGTH; // calculate how far the robot needs to travel
        double oldEnc = 0; 
        
        // while robot hasnt met distance or took too long to get there ( must be under 4 sec)
        while(distanceTraveled < robotDistance && timer.get() < 4){
        	encoderAvg = (robot.rEncoder.get() + robot.lEncoder.get())/2; // update encoders
        	robot.updateSmartDashboard(); // update dashboard
        	
            // decrement drive power until it is there to slow down to a stop
        	distanceTraveled = robot.ENCODER_DISTANCE * Math.abs(encoderAvg);
           
        	//decrement drive power until you reach there to prevent coasting
        	drivePower = (power * 0.75) + (power * 0.25) * ((robotDistance - distanceTraveled)/robotDistance);
            
            // drive there yay
            robot.driveTrain.arcadeDrive(drivePower, 0);
           
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

    //  negative angle to turn left, positive to turn right. Speed to turn at, tolerance of turn (how off can it be)
    private void turn(double angle, double power, int tolerance){
        // make turn power positive or negative based on which we to turn
        double turnPower = power * (angle > 0 ? 1:-1);
        // target angle is where robot is now plus angle param. this is to bypass using the
        // janky gyro.reset() method
        double targetAngle = robot.gyro.getAngle() + angle;

        // while the target angle is not close enough... | (target - currAngle) | > tolerance 
        while(Math.abs(targetAngle - robot.gyro.getAngle()) > tolerance){
            // turn
            robot.driveTrain.arcadeDrive(0, turnPower);
        }


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
    // turn using encoder values, takes in an angle to turn and a speed 
    private void turn(double angle, double power) {
    	//reset encoders
    	resetEncoders();
    	double drivePower = power; // set speed
        double robotDistance = (angle/360 * 40.0 * Math.PI);// + ERROR_CONSTANT;  // convert degrees to encoder values using this formula
        double oldEnc = 0;
        
        
        // while not there yet...  (targetdistance < currentDistance) & (timeRunning < 2) 
        while(distanceTraveled < robotDistance && timer.get() < 2){
        	encoderAvg = (Math.abs(robot.rEncoder.get()) + Math.abs(robot.lEncoder.get()))/2; // update encoders
        	robot.updateSmartDashboard();
        	
            // calc how far you have traveled
        	distanceTraveled = robot.ENCODER_DISTANCE * Math.abs(encoderAvg);
        	// decrement drive power until it is there to slow down to a stop
            drivePower = (power * 0.9) + (power * 0.1) * ((robotDistance - distanceTraveled)/robotDistance);
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
    private void resetEncoders(){
    	// reest r/l encoders
        robot.rEncoder.reset();
        robot.lEncoder.reset();
    }
    // check to see if the robot is stopped
    private boolean isStopped(double newEnc, double oldEnc, double tolerance) {
    	return (Math.abs(newEnc - oldEnc) <= tolerance); // check if the robot is stopped based on distance needed to 
    													// go and how far the robot has traveled
    }

    public void straightDiagL(){
            // go straight, turn towards scale (left), go to scale
    	
    	//set arm pos
        robot.claw.closeClaw();
	    robot.arm.autoMoveToLevel(1);
	    
	    // pass auto line
        moveForward(236, 0.8);
        //turn to face correct switch side
        turn(95, -1);
        //go to switch
        moveForward(179, 0.8);
        //turn to face switch
        turn(95, 1);
        
        // make arm put box in switch
        robot.arm.autoMoveToLevel(4);
        moveForward(62, 0.5);
        robot.claw.openClaw();


    }
    public void crossLine(){
    	// cross autoline
        moveForward(167, 0.7);
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
    
    public void straightDiagR(){
            // go straight, turn towards scale (right), go to scale
            robot.claw.closeClaw();
            robot.arm.autoMoveToLevel(1);
            moveForward(236, 0.8);
            turn(95, 1);
            moveForward(179, 0.8);
            turn(95, -1);
            robot.arm.autoMoveToLevel(4);
            moveForward(62, 0.5);
            robot.claw.openClaw();


    }
    
    public void straightDiagRTest() {
    	moveForward(60, 0.7);
    	turn(45, -1);
    	moveForward(80, 0.7);
    	turn(45, 1);
    	moveForward(130, 0.7);
    	robot.arm.autoMoveToLevel(4);
    	turn(90, 1);
    	moveForward(30, 0.4);
    }
    public void straight(){
            // go straight to scale (side-independent)


        robot.claw.closeClaw();
	    robot.arm.autoMoveToLevel(1);
        moveForward(262, 0.7);
	    turn(50, 1);
	    robot.arm.autoMoveToLevel(4);
	    moveForward(20, 0.5);
        robot.claw.openClaw();




    }

    public void nintendoSwitch(char joycon){
        if(joycon == 'r'){
            robot.claw.closeClaw();
            robot.arm.autoMoveToLevel(1);
            turn(35, 1);
            robot.arm.autoMoveToLevel(3);
            moveForward(150, 0.7);
            robot.arm.autoMoveToLevel(3);
            robot.claw.openClaw();



        }
        else if (joycon == 'l'){
            // turn towards switch,
            // go to switch(left)

            robot.claw.closeClaw();
            robot.arm.autoMoveToLevel(1);
            turn(35, -1);
            robot.arm.autoMoveToLevel(3);
            moveForward(150, 0.7);
            robot.arm.autoMoveToLevel(3);
            robot.claw.openClaw();

        }
    }
    
    public void pauseRobot (double time) {
    	timer.reset();
    	timer.start();
    	while (timer.get() < time) {
    		
    	}
    	timer.stop();
    	timer.reset();
    }
    /* AUTHOR: Neal Chokshi */
}
