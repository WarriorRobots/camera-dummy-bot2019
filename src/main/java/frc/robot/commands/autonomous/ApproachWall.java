package frc.robot.commands.autonomous;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.util.SynchronousPIDF;
import edu.wpi.first.wpilibj.Timer;


/** Approach the wall at a constant speed and then stop when within under 2 feet (give 1 inch). */
public class ApproachWall extends Command {

	/** PID used for approaching the wall. */
	private SynchronousPIDF PID;
	private Timer timer;
	/** Calculated PID output should stored in value. */
	private double value;

    public ApproachWall() {
		requires(Robot.drivetrain);
		requires(Robot.camera);

		PID = new SynchronousPIDF(0.025, 0, 0);

		timer = new Timer();
	}

	@Override
	protected void initialize() {
		//PID.setIzone(minimumI, maximumI);
		PID.setOutputRange(-1, 1);
		PID.setSetpoint(50); // Robot should aim to be be 50 in away from the target
		timer.start();
		
	}
	
	@Override
	protected void execute() {
		if (Robot.camera.canSeeObject()) {
			value = PID.calculate(Robot.camera.getTargetDistance(),timer.get());
			//System.out.println(value);
			//System.out.println(Robot.camera.getTargetDistance());
		} else {
			value = 0; // Set value to zero if the target can not be seen so robot does not go crazy
		}

		Robot.drivetrain.arcadeDriveRaw(value, 0);

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
		
		//return PID.onTarget(2); // Tolerance of 10 inches
		// (to make room for plenty of error first time)
	}
	
	@Override
	protected void end() {
		timer.stop();
		PID.reset();
		Robot.drivetrain.stopDrive();
	}

}