package frc.robot.commands.autonomous;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Constants;
import frc.robot.Robot;
import frc.robot.subsystems.CameraSubsystem;
import frc.robot.util.SynchronousPIDF;
import edu.wpi.first.wpilibj.Timer;


/** ApproachCurve approachs the target keeping it to the left or right (depending on the direction
 * of approach) and when it is aligned up (by the aspect ratio being 2.3 or above),
 * it approach the rest of the distance with it in front.
 */
public class ApproachCurve extends Command {

	/** PID used for approaching the wall. */
	private SynchronousPIDF PIDapproach;
	/** PID for keeping the target centered */
	private SynchronousPIDF PIDcenter;

	/** Pipeline that ApproachCurve will use. */
	private int pipeline;
	/** Boolean to tell whether the robot is aligned or not. */
	private boolean aligned;

	private Timer timer;
	/** Calculated PID output from {@link #PIDapproach} should stored in value. */
	private double valueapproach;
	/** Calculated PID output from {@link #PIDcenter} should stored in value. */
	private double valuecenter;

	private double left_x;
	private double left_height;
	private double right_x;
	private double right_height;
	private double overall_x;
	private double overall_height;

	/** iterates through 0 to 2 */
	private int iteration;

	/**
	 * @param pipeline Pipeline to show direction to turn and align in
	 * (use {@link frc.robot.subsystems.CameraSubsystem#PIPELINE_LEFT} or 
	 * {@link frc.robot.subsystems.CameraSubsystem#PIPELINE_RIGHT}).
	 */
    public ApproachCurve(int pipeline) {
		requires(Robot.drivetrain);
		requires(Robot.camera);

		this.pipeline = pipeline;
		valueapproach = 0;
		valuecenter = 0;
		aligned = false;

		PIDapproach = new SynchronousPIDF(
			Constants.AutoDrive.KP_APPROACH,
			Constants.AutoDrive.KI_APPROACH,
			Constants.AutoDrive.KD_APPROACH
		);
		PIDcenter = new SynchronousPIDF(
			Constants.AutoDrive.KP_CENTER,
			Constants.AutoDrive.KI_CENTER,
			Constants.AutoDrive.KD_CENTER
		);

		timer = new Timer();
	}

	@Override
	protected void initialize() {
		//PID.setIzone(minimumI, maximumI);
		//PIDapproach.setOutputRange(-1, 1);
		PIDapproach.setSetpoint(Constants.AutoDrive.SETPOINT_APPROACH); // Robot should aim to be be 50 in away from the target

		//PIDcenter.setOutputRange(-1, 1);	
		PIDcenter.setSetpoint(Constants.AutoDrive.SETPOINT_CENTER); // Robot should aim to keep the target centered on the crosshair

		Robot.camera.setPipeline(pipeline);

		timer.start();

		iteration = 0;

		overall_x = Robot.camera.getObjectX();
		overall_height = Robot.camera.getTargetHeight();
		
	}
	
	@Override
	protected void execute() {
		iteration++; iteration%=3; // iterate to the next number and roll over to 0 on 3

		switch (iteration) {
		
			case 1:	
				Robot.camera.setPipeline(CameraSubsystem.PIPELINE_TARGETLEFT);
				left_x = Robot.camera.getObjectX();
				left_height = Robot.camera.getTargetHeight();
				break;
			case 2:
				Robot.camera.setPipeline(CameraSubsystem.PIPELINE_TARGETRIGHT);
				right_x = Robot.camera.getObjectX();
				right_height = Robot.camera.getTargetHeight();
				break;
			case 3:
				Robot.camera.setPipeline(CameraSubsystem.PIPELINE_CENTER);
				overall_height = Robot.camera.getTargetHeight();
				overall_x = Robot.camera.getObjectX();
				break;
		}

		Robot.camera.setPipeline(CameraSubsystem.PIPELINE_CENTER);

		if (Robot.camera.canSeeObject()) {
			valueapproach = PIDapproach.calculate(Robot.camera.getTargetDistance(),timer.get());
			valuecenter = PIDcenter.calculate(overall_x, timer.get());
			
			// helps to keep the robot to drive in a missile approach curve
		} else {
			// Set value to zero if the target can not be seen so robot does not go crazy
			valueapproach = 0; 
			// Don't 0 valuecenter because it should "remember" what direction it's attempting to turn.
		}

		Robot.drivetrain.arcadeDriveRaw(valueapproach, -valuecenter);
	}
	
    @Override
	protected boolean isFinished() {
		return false;
		/*
		return (Robot.camera.getTargetDistance() < Constants.AutoDrive.SETPOINT_APPROACH &&
			PIDapproach.onTarget(Constants.AutoDrive.TOLERANCE_APPROACH));
		*/
	}
	
	@Override
	protected void end() {
		timer.stop();
		PIDapproach.reset();
		PIDcenter.reset();
		valueapproach=0;
		valuecenter=0;
		Robot.camera.setPipeline(CameraSubsystem.PIPELINE_CENTER);
		Robot.drivetrain.stopDrive();
	}

}