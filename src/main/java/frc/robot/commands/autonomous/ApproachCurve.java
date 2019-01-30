package frc.robot.commands.autonomous;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Constants;
import frc.robot.Robot;
import frc.robot.Constants.Camera;
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
	
	/** Height of the left target, overall target, and right target
	 * (in that order as left as 0, and right as 2)
	 */
	private double[] target_height;

	/** Horizontal postion of the left target, overall target, and right target
	 * (in that order as left as 0, and right as 2)
	*/
	private double[] target_x;

	private double target_distance;

	/** Used for {@link #target_height} and {@link #target_x} */
	private final int LEFT = 0;
	/** Used for {@link #target_height} and {@link #target_x} */
	private final int OVERALL = 1;
	/** Used for {@link #target_height} and {@link #target_x} */
	private final int RIGHT = 2;

	/** Shorthand for pipeline */
	private final int PIPELEFT = CameraSubsystem.PIPELINE_TARGETLEFT;
	/** Shorthand for pipeline */
	private final int PIPEOVERALL = CameraSubsystem.PIPELINE_CENTER;
	/** Shorthand for pipeline */
	private final int PIPERIGHT = CameraSubsystem.PIPELINE_TARGETRIGHT;

	/** Keeps the id of the pipeline that is currently being asked to give values to the data arrays */
	private int intendedPipe;


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

		intendedPipe = 0;
		target_height= new double[3];
		target_x = new double[3];
		target_distance = 0;
		
	}
	
	@Override
	protected void execute() {

		updateTargetData();

		if (
			target_height[LEFT] == 0 || target_height[OVERALL] == 0 || target_height[RIGHT] == 0 ||
			target_x[LEFT] == 0 || target_x[OVERALL] == 0 || target_x[RIGHT] == 0 ||
			target_distance == 0
			)
		return; // if any part of the data is unwritten to, then return out of function to avoid the
		// the robot driving before it has data

		if (Robot.camera.canSeeObject()) {
			valueapproach = PIDapproach.calculate(target_distance, timer.get());
			valuecenter = PIDcenter.calculate(target_x[OVERALL], timer.get());
			
			// helps to keep the robot to drive in a missile approach curve
		} else {
			// Set value to zero if the target can not be seen so robot does not go crazy
			valueapproach = 0; 
			// Don't 0 valuecenter because it should "remember" what direction it's attempting to turn.
		}

		Robot.drivetrain.arcadeDriveRaw(valueapproach, -valuecenter);
	}

	private void updateTargetData() {

		if (Robot.camera.getPipeline() == intendedPipe) { // wait for pipeline to be the intened one
			switch (intendedPipe) {
		
				case PIPELEFT: // Left
					target_height[LEFT] = Robot.camera.getTargetHeight();
					target_x[LEFT] = Robot.camera.getObjectX();
					Robot.camera.setPipeline(PIPEOVERALL);
					intendedPipe = PIPEOVERALL;
					break;
				case PIPEOVERALL: // Overall
					target_height[OVERALL] = Robot.camera.getTargetHeight();
					target_x[OVERALL] = Robot.camera.getObjectX();
					target_distance = Robot.camera.getTargetDistance();
					Robot.camera.setPipeline(PIPERIGHT);
					intendedPipe = PIPERIGHT;
					break;
				case PIPERIGHT: // Right
					target_height[RIGHT] = Robot.camera.getTargetHeight();
					target_x[RIGHT] = Robot.camera.getObjectX();
					Robot.camera.setPipeline(PIPELEFT);
					intendedPipe = PIPELEFT;
					break;
				default:
					break;
			}
		}

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