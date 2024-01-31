// // Copyright (c) FIRST and other WPILib contributors.
// // Open Source Software; you can modify and/or share it under the terms of
// // the WPILib BSD license file in the root directory of this project.

// package frc.robot.commands;

// import edu.wpi.first.wpilibj2.command.Command;
// import edu.wpi.first.wpilibj2.command.CommandScheduler;
// import frc.robot.subsystems.Swerve;
// import frc.lib.util.AprilTag;

// import static frc.robot.Constants.MoveTowardTag.*;

// public class MoveTowardsAprilTag extends Command {

//     private Swerve swerveDrive;
//     private AprilTag aprilTagInstance;

//     public MoveTowardsAprilTag(Swerve swerveDrive, AprilTag aprilTag) {
//         this.swerveDrive = swerveDrive;
//         this.aprilTagInstance = aprilTag; 
//         addRequirements(swerveDrive, aprilTag);
//     }

//     @Override
//     public void execute() {
//         double xAngle = aprilTagInstance.entry("tx").getDouble(0);
//         if (xAngle == 0) return;

//         if (xAngle < MIN_ANGLE) {
//             swerveDrive.arcadeDrive(MOVE_SPEED, -TURN_SPEED);
//         } else if (xAngle > MAX_ANGLE) {
//             swerveDrive.arcadeDrive(MOVE_SPEED, TURN_SPEED);
//         } else {
//             swerveDrive.arcadeDrive(MOVE_SPEED, 0);
//         }
//     }

//     @Override
//     public void end(boolean interrupted) {
//         swerveDrive.stop();

//         if (aprilTagInstance.targetPos() == null) {
//             CommandScheduler.getInstance().schedule(new SearchForTag(swerveDrive, aprilTagInstance));
//         }
//     }

//     @Override
//     public boolean isFinished() {
//         var targetPos = aprilTagInstance.targetPos();
//         return targetPos != null && targetPos.getZ() < SHOOT_DISTANCE;
//     }
// }