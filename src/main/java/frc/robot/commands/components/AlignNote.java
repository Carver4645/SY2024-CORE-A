package frc.robot.commands.components;

import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.BackLimelight;
import frc.robot.subsystems.Swerve;
import java.util.Optional;
import java.util.OptionalDouble;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.Constants;
import frc.robot.commands.CommandBuilder;
import edu.wpi.first.math.geometry.Pose3d;

public class AlignNote extends Command {

    public final Swerve drivetrain;
    public final Field2d playingField;
    public final BackLimelight backLimelightInstance;

    public Optional<Command> advanceToTargetCommand = Optional.empty();

    public AlignNote(Swerve drivetrain, Field2d playingField, BackLimelight backLimelightInstance) {
        this.drivetrain = drivetrain;
        this.playingField = playingField;
        this.backLimelightInstance = backLimelightInstance;
    }

    @Override
    public void initialize() {
        // Optional<Double> relativeTargetPose = backLimelightInstance.getTargetPoseRelative();
        // OptionalDouble rotation = backLimelightInstance.determineTargetRotationalOffset();
        // System.out.println(relativeTargetPose.get());
        // if (relativeTargetPose.isPresent() && Math.sqrt(Math.pow(relativeTargetPose.get().getX(), 2) + Math.pow(relativeTargetPose.get().getY(), 2)) < Constants.Vision.BackLimelight.maximumAlignmentDistance) {
        //     advanceToTargetCommand = Optional.of(
        //         CommandBuilder.AdvanceToTarget(drivetrain, playingField, new Transform2d(playingField.getRobotPose().getTranslation(), playingField.getRobotPose().getRotation().plus(relativeTargetPose.get().getRotation().toRotation2d()))).andThen(
        //             CommandBuilder.AdvanceToTarget(drivetrain, playingField, new Transform2d(playingField.getRobotPose().getTranslation().plus(relativeTargetPose.get().getTranslation().toTranslation2d()), playingField.getRobotPose().getRotation()))
        //         )
        //     );
        // }

        if (advanceToTargetCommand.isEmpty()) {
            Optional<Double> targetOffset = backLimelightInstance.getTargetHorizontalRotationalOffset();

            if (targetOffset.isPresent()) {
                double robotTargetOffset = targetOffset.get() + 10;

                Transform2d targetOrientation = new Transform2d(playingField.getRobotPose().getTranslation(), playingField.getRobotPose().getRotation().plus(new Rotation2d(robotTargetOffset, 0)));

                advanceToTargetCommand = Optional.of(
                    CommandBuilder.AdvanceToTarget(drivetrain, playingField, targetOrientation)
                );

                advanceToTargetCommand.get().schedule();
            }
        }
    }

    @Override
    public void execute() {
        // if (advanceToTargetCommand.isEmpty()) {
        //     Optional<Double> relativeTargetPose = backLimelightInstance.getTargetPoseRelative();
        //     if (relativeTargetPose.isPresent() && Math.sqrt(Math.pow(relativeTargetPose.get()[0], 2) + Math.pow(relativeTargetPose.get()[1], 2)) < Constants.Vision.BackLimelight.maximumAlignmentDistance) {
        //         advanceToTargetCommand = Optional.of(
        //             CommandBuilder.AdvanceToTarget(drivetrain, playingField, new Transform2d(playingField.getRobotPose().getTranslation(), playingField.getRobotPose().getRotation().plus(relativeTargetPose.get().getRotation().toRotation2d()))).andThen(
        //                 CommandBuilder.AdvanceToTarget(drivetrain, playingField, new Transform2d(playingField.getRobotPose().getTranslation().plus(relativeTargetPose.get().getTranslation().toTranslation2d()), playingField.getRobotPose().getRotation()))
        //             )
        //         );
        //     }
        // }

        if (advanceToTargetCommand.isEmpty()) {
            Optional<Double> targetOffset = backLimelightInstance.getTargetHorizontalRotationalOffset();

            if (targetOffset.isPresent()) {
                double robotTargetOffset = targetOffset.get() + 10;

                Transform2d targetOrientation = new Transform2d(playingField.getRobotPose().getTranslation(), playingField.getRobotPose().getRotation().plus(new Rotation2d(robotTargetOffset, 0)));

                advanceToTargetCommand = Optional.of(
                    CommandBuilder.AdvanceToTarget(drivetrain, playingField, targetOrientation)
                );

                advanceToTargetCommand.get().schedule();
            }
        }
     }

     @Override
     public void end(boolean interrupted) {
        advanceToTargetCommand.get().cancel();
     }

     @Override
     public boolean isFinished() {
        return advanceToTargetCommand.isEmpty() || advanceToTargetCommand.get().isFinished();
     }
}