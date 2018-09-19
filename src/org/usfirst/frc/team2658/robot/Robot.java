package org.usfirst.frc.team2658.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoMode;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
// test push for Eclipse from intelliJ D
public class Robot extends IterativeRobot {
	
 	Talon rfDrive, rbDrive, lfDrive, lbDrive;						//4 drive Talons
	final int FRONT_LEFT_PORT = 2;   								//port number for front left motor
	final int BACK_LEFT_PORT = 3;						   			//port number for back left motor
	final int FRONT_RIGHT_PORT = 0;					    			//port number for front right motor
	final int BACK_RIGHT_PORT = 1;					   			    //port number for back right motor
	
	WPI_TalonSRX armTalon, climbWinch1, climbWinch2;				//Arm and ClimbWinch motor controllers
	final int ARM_ADDRESS = 10;										//address of the arm TalonSRX
	final int FIRST_CLIMB_WINCH_ADDRESS = 11;						//address of the climbWinch1 TalonSRX
	final int SECOND_CLIMB_WINCH_ADDRESS = 12;						//address of the climbWinch2 TalonSRX
	
	Spark intakeMotorL, intakeMotorR;						//intake wheel motor
	final int INTAKE_MOTOR_PORT_L = 5, INTAKE_MOTOR_PORT_R = 4;		//intake motor port
	
	DoubleSolenoid clawSolenoid, pacmanSolenoid, hookshotSolenoid;					//The pneumatics for the claw and pacman
	final int FIRST_CLAW_PORT = 0;									//first claw port
	final int SECOND_CLAW_PORT = 1;									//second claw port
	final int FIRST_PACMAN_PORT = 2;								//first pacman port
	final int SECOND_PACMAN_PORT = 3;								//second pacman port
	final int FIRST_HOOKSHOT_PORT = 4;								//first hookshot port
	final int SECOND_HOOKSHOT_PORT = 5;								//second hookshot port
	
	Encoder rEncoder, lEncoder, armEncoder;					//The three different encoders
	final int RIGHT_ENCODER_PORT_1 = 2;								//the port for the right encoder
	final int RIGHT_ENCODER_PORT_2 = 3;								//the port for the right encoder
	final int LEFT_ENCODER_PORT_1 = 5;								//the port for the left encoder
	final int LEFT_ENCODER_PORT_2 = 4;								//the port for the left encoder
	final int ARM_ENCODER_PORT_1 = 0;								//the port for the arm encoder
	final int ARM_ENCODER_PORT_2 = 1;								//the port for the arm encoder
	
	final double ENCODER_DISTANCE = 150/2000.0;				//Encoders to unit conversion
	
	Joystick driveXBox, driveRJoy, driveLJoy, opXBox;		//The five diffent joysticks
	final int DRIVE_XBOX_PORT = 0;								    //drive xbox remote port
	final int DRIVE_JOY_L_PORT = 1;									//drive left joystick port
	final int DRIVE_JOY_R_PORT = 2;									//drive right joystick port
	final int OP_XBOX_PORT = 3;										//op xbox port
	
	SpeedControllerGroup spRight, spLeft;					//The paired drive talons
	DifferentialDrive driveTrain;							//The actual drive train
	SendableChooser<Integer> driveController, autoChooser, autoIgnore, autoMethod;	//Various choosers
	
	AnalogGyro gyro;											//The gyro
	final int GYRO_PORT = 0;										//The gyro port
	final int CLAW_CAMERA = 0, FRONT_CAMERA = 0;								//The camera name
	public final int IMG_WIDTH = 320;								// camera resolution width
	public final int IMG_HEIGHT = 240;								// camera resolution height			
	public CameraServer camServer;
	public UsbCamera clawCam, frontCam;
	public DriverStation driverStation;								//The place to read the FMS message
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	
	Arm arm;
	Claw claw;
	ClimbWinch winch;
	DriveTrain drive;
	WheelyIntake intake;
	Hookshot hookshot;
	Pacman pacman;
	AutonomousOptions autoOps;
	
	public int autoTime;
	
	@Override
	public void robotInit() {
		
		arm = new Arm(this);
		claw = new Claw(this);
		winch = new ClimbWinch(this);
		drive = new DriveTrain(this);
		intake = new WheelyIntake(this);
		autoOps = new AutonomousOptions(this);
		hookshot = new Hookshot(this);
		pacman = new Pacman(this);
		
		/* Author --> Gokul Swaminathan */
		driveController = new SendableChooser<Integer>();
		driveController.addDefault("Xbox Controller (Tank Drive)", 1);
		driveController.addObject("Dual Joysticks", 2);
		driveController.addObject("Single Joystick", 3);
		
		//initialize all motors
		lfDrive = new Talon(FRONT_LEFT_PORT);
		rfDrive = new Talon(FRONT_RIGHT_PORT);
		lbDrive = new Talon(BACK_LEFT_PORT);
		rbDrive = new Talon(BACK_RIGHT_PORT);
		
		//initialize the claw
		clawSolenoid = new DoubleSolenoid(FIRST_CLAW_PORT, SECOND_CLAW_PORT);
		
		//initialize the pacman
		pacmanSolenoid = new DoubleSolenoid(FIRST_PACMAN_PORT, SECOND_PACMAN_PORT);
		
		//initialize the hookshot
		hookshotSolenoid = new DoubleSolenoid(FIRST_HOOKSHOT_PORT, SECOND_HOOKSHOT_PORT);
		
		//initialize speed controller groups
		spLeft = new SpeedControllerGroup(lfDrive, lbDrive); 
		spRight = new SpeedControllerGroup(rfDrive, rbDrive);
		
		//initialize drivetrain
		driveTrain = new DifferentialDrive(spLeft, spRight);
		
		//initialize the arm
		armTalon = new WPI_TalonSRX(ARM_ADDRESS);
		
		//initialize the winch motors
		climbWinch1 = new WPI_TalonSRX(FIRST_CLIMB_WINCH_ADDRESS);
		climbWinch1.enableCurrentLimit(true);
		climbWinch1.configPeakCurrentLimit(38, 0);
		climbWinch1.configContinuousCurrentLimit(30, 0);
		climbWinch2 = new WPI_TalonSRX(SECOND_CLIMB_WINCH_ADDRESS);
		climbWinch2.enableCurrentLimit(true);
		climbWinch2.configPeakCurrentLimit(38, 0);
		climbWinch2.configContinuousCurrentLimit(30, 0);
		
		//initialize the encoders
		rEncoder = new Encoder(RIGHT_ENCODER_PORT_1, RIGHT_ENCODER_PORT_2);
		lEncoder = new Encoder(LEFT_ENCODER_PORT_1, LEFT_ENCODER_PORT_2);
		armEncoder = new Encoder(ARM_ENCODER_PORT_1, ARM_ENCODER_PORT_2);		
		rEncoder.reset();
		lEncoder.reset();
		armEncoder.reset();
		
		//intialize all controllers
		driveXBox = new Joystick(DRIVE_XBOX_PORT);
		driveLJoy = new Joystick(DRIVE_JOY_L_PORT);
		driveRJoy = new Joystick(DRIVE_JOY_R_PORT);
		//opJoy = new Joystick(OP_JOY_PORT);
		opXBox = new Joystick(OP_XBOX_PORT);
		
		//create the autochooser stuff
		autoChooser = new SendableChooser<Integer>();
		autoChooser.addDefault("NOTHING", -1);
		autoChooser.addObject("RIGHT SIDE", 0);
		autoChooser.addObject("MID POSITION", 1);
		autoChooser.addObject("LEFT SIDE", 2);
		autoChooser.addObject("MOVE FORWARD - TIMER", 3);
		autoChooser.addObject("TURN RIGHT - TIMER", 4);
		autoChooser.addObject("TURN LEFT - TIMER", 5);
		autoChooser.addObject("GRAB BOX TEST", 6);
		
		autoIgnore = new SendableChooser<Integer>();
		autoIgnore.addDefault("TRY ALL AUTO", 1);
		autoIgnore.addObject("IGNORE SWITCH", 2);
		autoIgnore.addObject("IGNORE SCALE", 3);
		autoIgnore.addObject("IGNORE SWITCH AND SCALE", 6);
		
		
		autoMethod = new SendableChooser<Integer>();
		autoMethod.addDefault("USE TIMERS", 0);
		autoMethod.addDefault("USE ENCODERS", 1);
		
		
		
		//initializes the intake motors
		intakeMotorL = new Spark(INTAKE_MOTOR_PORT_L);
		intakeMotorR = new Spark(INTAKE_MOTOR_PORT_R);
		
		
		
		frontCam = camServer.getInstance().startAutomaticCapture("cam0", FRONT_CAMERA);
		

		frontCam.setResolution(IMG_WIDTH, IMG_HEIGHT);
		
		//IMPLEMENT VISION CODE MAYBE 
		
		
//		 
//		// gyroscope init
//		gyro = new AnalogGyro(GYRO_PORT);
//		gyro.reset();
//		gyro.calibrate();
//		//set drift
//		gyro.setSensitivity(0.05);
		

		SmartDashboard.putData("Drive Choices", driveController);
		SmartDashboard.putData("Auto Choices", autoChooser);
		SmartDashboard.putData("Auto Ignore", autoIgnore);
		SmartDashboard.putNumber("Drive Power", 1);
		SmartDashboard.putNumber("Drive Exponent", 1.5);
		
		//Claw.closeClaw();
		SmartDashboard.putString("Claw Position", "CLOSE");
		
		//Pacman.boostedClose();
		SmartDashboard.putNumber("TimeYay", autoTime);
		
		SmartDashboard.putNumber("Right Encoder", rEncoder.get());
		SmartDashboard.putNumber("Left Encoder", lEncoder.get());
		
		SmartDashboard.putString("Arm Control", "MANUAL");
		SmartDashboard.putString("Arm Destination", "N/A");
		SmartDashboard.putString("Arm Position", "Unsure");
		SmartDashboard.putNumber("Arm Encoder", armEncoder.get());
		
		SmartDashboard.putString("Auto Did", "Waiting...");

	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		autoOps.autoInit();
		autoOps.autoPeriodic();
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		//AutonomousOptions.autoPeriodic();
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		/*if (!drive. && !winch.climbing) drive.start();
		if (!intake.isAlive() && !winch.climbing) intake.start();
		if (!arm.isAlive() && !winch.climbing) arm.start();
		if (!winch.isAlive()) winch.start();
		if (!claw.isAlive() && !winch.climbing) claw.start();
		if (!pacman.isAlive() && !winch.climbing) pacman.start();
		if (!hookshot.isAlive() && !winch.climbing) hookshot.start();*/
		drive.run();
		intake.run();
		arm.run();
		winch.run();
		claw.run();
		pacman.run();
		hookshot.run();
		updateSmartDashboard();
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
		
	}
	
	public void updateSmartDashboard() {
		claw.dashboardWork();
		arm.dashboardWork();
		drive.dashboardWork();
		pacman.dashboardWork();
		hookshot.dashboardWork();
		winch.dashboardWork();
	}
	
	public void resetEncoders() {
			armEncoder.reset();
			rEncoder.reset();
			lEncoder.reset();	
	}
}

