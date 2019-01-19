package frc.robot.commands.autonomous;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.util.SynchronousPIDF;
import edu.wpi.first.wpilibj.Timer;


/** Approach the wall keeping the target centered using 2 different PIDs. */
public class ApproachWall extends Command {

	/** PID used for approaching the wall. */
	private SynchronousPIDF PIDapproach;
	/** PID for keeping the target centered */
	private SynchronousPIDF PIDcenter;

	private Timer timer;
	/** Calculated PID output from {@link #PIDapproach} should stored in value. */
	private double valueapproach;
	/** Calculated PID output from {@link #PIDcenter} should stored in value. */
	private double valuecenter;

    public ApproachWall() {
		requires(Robot.drivetrain);
		requires(Robot.camera);

		PIDapproach = new SynchronousPIDF(0.03, 0, 0);
		PIDcenter = new SynchronousPIDF(0.03, 0, 0);

		timer = new Timer();
	}

	@Override
	protected void initialize() {
		//PID.setIzone(minimumI, maximumI);
		PIDapproach.setOutputRange(-1, 1);
		PIDapproach.setSetpoint(50); // Robot should aim to be be 50 in away from the target

		PIDcenter.setOutputRange(-1, 1);
		PIDcenter.setSetpoint(0); // Robot should aim to keep the target centered on the crosshair

		timer.start();
		
	}
	
	@Override
	protected void execute() {
		if (Robot.camera.canSeeObject()) {
			valueapproach = PIDapproach.calculate(Robot.camera.getTargetDistance(),timer.get());
			valuecenter = PIDcenter.calculate(Robot.camera.getObjectX(), timer.get());
			//System.out.println(value);
			//System.out.println(Robot.camera.getTargetDistance());
		} else {
			// Set value to zero if the target can not be seen so robot does not go crazy
			valueapproach = 0; 
			
			// Don't 0 valuecenter because it should "remember" what direction it's attempting to turn.
		}

		Robot.drivetrain.arcadeDriveRaw(valueapproach, -valuecenter);

		/*
		if (Robot.camera.canSeeObject() && Robot.camera.getTargetDistance() > 50)
			Robot.drivetrain.arcadeDriveRaw(-0.6, 0);
		else 
			Robot.drivetrain.arcadeDriveRaw(0, 0);
			// remember to STOP the robot when it should be done going forwards
		*/
	}
	
    @Override
	protected boolean isFinished() {
		//return (Robot.camera.getTargetDistance() < 50);

		return false; // temp till pid is tuned
		
		//return PID.onTarget(2); // Tolerance of 2 inches
		// (to make room for plenty of error first time)
	}
	
	@Override
	protected void end() {
		timer.stop();
		PIDapproach.reset();
		Robot.drivetrain.stopDrive();
	}

}