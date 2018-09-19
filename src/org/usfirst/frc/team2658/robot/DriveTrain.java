package org.usfirst.frc.team2658.robot;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/* Author --> Gokul Swaminathan*/

public class DriveTrain extends Thread {

	Robot robot;
	
	public DriveTrain(Robot r) {
		robot = r;
	}
	
	public void run()
	{
		//get inputs from user
		double exponent = SmartDashboard.getNumber("Drive Exponent", 2);
		double constant = SmartDashboard.getNumber("Drive Power", 1);

		//controller axis
		final int xboxRightAxis = 5;
		final int xboxLeftAxis = 1;	
		final int joystickAxis = 1;
		
		//arcade stuff
		final int joystickAxisY = 1;
		final int joystickAxisX = 0;
		
		int mode = robot.driveController.getSelected();
		
		switch(mode)
		{
		case 1: 
			//run tank drive method for xbox
			driveTankDrive(robot.driveXBox.getRawAxis(xboxLeftAxis), robot.driveXBox.getRawAxis(xboxRightAxis) ,exponent, constant, robot.driveTrain);
			break;
			
		case 2:
			//run tank drive method for joysticks
			driveTankDrive(robot.driveLJoy.getRawAxis(joystickAxis),robot.driveRJoy.getRawAxis(joystickAxis), exponent, constant, robot.driveTrain);
			break;
		case 3:
			driveArcadeDrive(robot.driveLJoy.getRawAxis(joystickAxisY), robot.driveLJoy.getRawAxis(joystickAxisX), exponent, constant, robot.driveTrain);
			break;
			
		default:
			break;
		}
	}
	
	private void driveTankDrive(double leftAxis, double rightAxis, double exp, double cons, DifferentialDrive drive )
	{
		
		//exp = sensitivity
		//cons = power
		
		int negR = 0, negL = 0;
		
		if(rightAxis < 0)
		{
			negR = -1;			
		}
		else if(rightAxis > 0)
		{
			negR = 1;
		}
		if(leftAxis < 0)
		{
			negL = -1;
		}
		else if(leftAxis > 0)
		{
			negL = 1;
		}
				
		double leftSpeed = - negL * cons * Math.pow(Math.abs(leftAxis), exp);
		double rightSpeed = - negR *  cons * Math.pow(Math.abs(rightAxis), exp);
		
		drive.tankDrive(leftSpeed, rightSpeed);
	}
	public void driveArcadeDrive(double yAxis, double xAxis, double exp, double cons, DifferentialDrive drive ) {
		int negY  = 1;
		int negZ = 1;
		if(yAxis < 0) {
			negY = 1;
			
		}
		else if(yAxis > 0) {
			negY = -1;
			
		}
		if(xAxis < 0) {
			negZ = -1;
			
		}
		else if(xAxis > 0) {
			negZ = 1;
			
		}
		
		double ySpd = cons * negY * Math.pow(Math.abs(yAxis), exp);
		double zSpd = cons * negZ * Math.pow(Math.abs(xAxis), exp);
		
		drive.arcadeDrive(ySpd, zSpd);
		
	}
	
	public void dashboardWork() {
		SmartDashboard.putNumber("Right Encoder", robot.rEncoder.get());
		SmartDashboard.putNumber("Left Encoder", robot.lEncoder.get());
	}
}
