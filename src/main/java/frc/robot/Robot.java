// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.lib.config.CTREConfigs;
import java.util.Optional;

/**
 * The main robot class. This handles the robot container (which contains the
 * subsystems) and handles triggers during different modes.
 */
public class Robot extends TimedRobot {
  public static CTREConfigs ctreConfigs;
  private Command m_autonomousCommand;

  public long initalizationTime;
  private RobotContainer m_robotContainer;
  private Transform2d positionalError = new Transform2d();

  @Override
  public void robotInit() {
    ctreConfigs = new CTREConfigs();
    m_robotContainer = new RobotContainer();
    initalizationTime = System.currentTimeMillis();
  }

  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();
    m_robotContainer.getAprilTag().aprilTagPeriodic();

    Pose2d swervePose2D = m_robotContainer.getDrivetrain().getPose();
    Optional<Pose2d> limelightPose2D = m_robotContainer.getAprilTag().determinePosition().map(transform3D -> new Pose2d(transform3D.getTranslation().toTranslation2d(), transform3D.getRotation().toRotation2d()));

    if (limelightPose2D.isPresent()) {
      positionalError = limelightPose2D.get().minus(swervePose2D);
    }

    m_robotContainer.getField().setRobotPose(swervePose2D.plus(positionalError));
  }

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void disabledExit() {}

  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
  }

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void autonomousExit() {}

  @Override
  public void teleopInit() {
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  @Override
  public void teleopPeriodic() {}

  @Override
  public void teleopExit() {}

  @Override
  public void testInit() {
    CommandScheduler.getInstance().cancelAll();
  }

  @Override
  public void testPeriodic() {}

  @Override
  public void testExit() {}
}