package frc.robot.commands.autonomous;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;


/** Approach the wall at a constant speed and then stop when within under 2 feet (give 1 inch). */
public class ApproachWall extends Command {

    public ApproachWall() {
		requires(Robot.drivetrain);
		requires(Robot.camera);
	}

	@Override
	protected void initialize() {}
	
	@Override
	protected void execute() {
		if (Robot.camera.canSeeObject()) Robot.drivetrain.arcadeDriveRaw(0.1, 0);
	}
	
	@Override
	protected void end() {
		Robot.drivetrain.stopDrive();
	}
	
    @Override
	protected boolean isFinished() {
		return (Robot.camera.getTargetDistance() < 25);
	}

}