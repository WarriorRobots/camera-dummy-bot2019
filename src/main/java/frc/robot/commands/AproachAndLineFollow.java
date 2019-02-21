/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.commands.autonomous.ApproachCurve;
import frc.robot.commands.LineFollowCommand;

public class AproachAndLineFollow extends CommandGroup {
  ApproachCurve approachCurve = new ApproachCurve(0);
  LineFollowCommand lineFollow = new LineFollowCommand();
  /**
   * Combines the ApproachCurve command and LineFollowCommand.
   * Approach Curve is to get into the general area and Line Follow is to refine for perpendicularity.
   */
  public AproachAndLineFollow() {
    addSequential(approachCurve);
    addSequential(lineFollow);
    // Add Commands here:
    // e.g. addSequential(new Command1());
    // addSequential(new Command2());
    // these will run in order.
    // addSequential();
    // To run multiple commands at the same time,
    // use addParallel()
    // e.g. addParallel(new Command1());
    // addSequential(new Command2());
    // Command1 and Command2 will run in parallel.

    // A command group will require all of the subsystems that each member
    // would require.
    // e.g. if Command1 requires chassis, and Command2 requires arm,
    // a CommandGroup containing them would require both the chassis and the
    // arm.
  }
}
