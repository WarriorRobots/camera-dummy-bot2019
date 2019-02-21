/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class LineFollowCommand extends Command {
  char last_turn = 'm';
  NetworkTableEntry on_line;

  public LineFollowCommand() {
    requires(Robot.linefollow);
    requires(Robot.drivetrain);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    NetworkTableInstance inst = NetworkTableInstance.getDefault();
    NetworkTable table = inst.getTable("line-follow");
    on_line = inst.getEntry("on-line");
  }
  /**
   * STEPS:
   * 1. Make sure that program only searches for line, does not start following until Approach sends "stop"
   * 2. Send value to Network table value of 'on-line' when infrared goes off
   * 3. In Approach Curve, go until on-line says TRUE and once stopped, make sure you tell line follow it's okay to start
   */
  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    double turn_speed;
    if(Robot.linefollow.onCenter()){
      turn_speed = 0;
      last_turn = 'm';
    }else if(Robot.linefollow.onLeftOfLine()){
      turn_speed = 0.12;
      last_turn = 'r';
    }else if(Robot.linefollow.onRightOfLine()){
      turn_speed = -0.12;
      last_turn = 'l';
    }else{
      turn_speed = (last_turn == 'r')? -0.12:0.12;
      /** Esto es para correctar cuando el robot moven lejos de la linea */
      /** Vamos a mover en la opuesta direccion del "last_turn" */
    }
    Robot.drivetrain.arcadeDriveRaw(0.25, turn_speed);
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return false;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
  }
}
