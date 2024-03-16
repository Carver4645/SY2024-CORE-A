// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.lib.util;

import java.util.Optional;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.robot.Constants;
import frc.robot.subsystems.FrontLimelight;
import frc.robot.subsystems.Shooter;

/**
* The AprilTag class, harnessed in order to calculate and execute various 
* 'vision'-related processes within FIRST Team 4645, the Chicago Style 
* Bot Dogs' robot for the FIRST Robotics 2024 competition, Crescendo.
*/
public class AprilTag {

  public static final NetworkTable TABLE = NetworkTableInstance.getDefault().getTable("limelight");

  public final FrontLimelight limelightInstance;
  public final NodeStorage nodeStorageInstance;
  public final Shooter shooterInstance;

  public AprilTag(FrontLimelight limelightInstance, NodeStorage nodeStorageInstance, Shooter shooterInstance) {
    this.limelightInstance = limelightInstance;
    this.nodeStorageInstance = nodeStorageInstance;
    this.shooterInstance = shooterInstance;
  }

  public Optional<Pose3d> getTargetPoseRelative() {
    return limelightInstance.getTargetPoseRelative();
  }

  /**
   * Executes periodically while querying the primary apriltag target's current 
   * position, printing the distance to such to the console.
   */
  public void aprilTagPeriodic() {
    Optional<Pose3d> currentPosition = determinePosition();

    if (currentPosition.isEmpty() || getTargetPoseRelative().isEmpty()) {
      System.out.println("No Limelight target.");
    }

    Translation3d targetPosition = determinePosition().get().getTranslation().plus(getTargetPoseRelative().get().getTranslation());

    System.out.printf("Target position: {x: %.3f, y: %.3f, z: %.3f}\n", targetPosition.getX(), targetPosition.getY(), targetPosition.getZ());
  }

  // /**
  //  * Calculates the distance between the robot's current estimated position
  //  * and that of the target specified in the method's parameters.
  //  * 
  //  * @param targetPosition - The position of the target in question, formatted 
  //  * through the use of Java's 'Transform3d' class.
  //  * 
  //  * @return(s) the distance from the robot to the target specified in the 
  //  * method's parameters.
  //  */
  // public Optional<Double> getDirectDistance(Optional<Transform3d> targetPosition) {
  //   if (determinePosition().isPresent() && targetPosition.isPresent()) {
  //     return Optional.of(targetPos().get().getTranslation().getNorm());
  //   } else {
  //     return Optional.empty();
  //   }
  // }

  /**
   * Calculates the distance, on a 2D plane, between two locations, as 
   * specified in the method's parameters.
   * 
   * @param originPosition - The position of the "primary" location, although 
   * the order of such and the "secondary" one does not affect, in any way,
   * the functionality of 
   * the method itself.
   * @param targetPosition - The position of the "secondary" location, although 
   * the order of such and the "primary" one does not affect, in any way, the 
   * functionality of the method itself.
   * 
   * @return(s) the distance which exists between the two locations specified 
   * within the method's parameters on a 2D plane.
   */
  public Optional<Double> getPlanarDistance(Translation2d originPosition, Translation2d targetPosition) {
    return Optional.of(Math.hypot(targetPosition.getX() - originPosition.getX(), targetPosition.getY() - originPosition.getY()));
  }

  /**
   * Determines whether the position of the target specified in the method's 
   * parameters is "suitable" for use in trajectory/path generation.
   * 
   * @param targetPosition - The position of the target in question.
   * 
   * @return(s) whether the position of the target specified in the method's 
   * parameters is "suitable" for use in trajectory/path generation.
   */
  public boolean validTargetInput(Optional<Translation3d> targetPosition) {
    Optional<Pose3d> currentRobotPosition = determinePosition();

    if (targetPosition.isPresent() && currentRobotPosition.isPresent() && currentRobotPosition.get().getX() != targetPosition.get().getX() && currentRobotPosition.get().getY() != targetPosition.get().getY()) {
      return true;
    } else {
      return false;
    }
  }

    /**
   * Estimates the position of the robot, using the Limelight's knowledge of
   * the position of each AprilTag in the game board.
   * If no AprilTags are deteted, this returns 'Optional.empty()'.
   * 
   * For a map of the game board and AprilTag positions, see
   * {@link https://firstfrc.blob.core.windows.net/frc2024/FieldAssets/2024FieldDrawings.pdf},
   * page 4.
   * 
   * @return(s) the robot's estimated position in the board.
   */
  public Optional<Pose3d> determinePosition() {
    Optional<Pose3d> targetOffset = getTargetPoseRelative();
    if (targetOffset.isEmpty()) return Optional.empty();

    if (limelightInstance.getTagID().getAsInt() == -1) {
      return Optional.empty();
    }


    Optional<Transform3d> originToTarget = Optional.of(Constants.Limelight.APRILTAGS.get(limelightInstance.getTagID().getAsInt()));
    if (originToTarget.isEmpty()) return Optional.empty();

    Transform3d targetToRobot = new Transform3d(targetOffset.get().getTranslation(), targetOffset.get().getRotation());

    Transform3d originToRobot = originToTarget.get().plus(targetToRobot);

    Pose3d robotPose3D = new Pose3d(originToRobot.getTranslation(), originToRobot.getRotation());

    return Optional.of(robotPose3D);
  }

  /** 
  * Calculates and @return(s) the relative rotational offset to the given 
  * "targetPosition," based off of Limelight's estimation on where the robot is 
  * currently positioned, as well as the location of the specified target.
  *
  * For a map of the game board and AprilTag positions, see
  * {@link https://firstfrc.blob.core.windows.net/frc2024/FieldAssets/2024FieldDrawings.pdf},
  * page 4.
  */
  public Optional<Rotation2d> determineTargetRotationalOffset(Optional<Translation3d> targetPosition) {
      Optional<Pose3d> currentRelativePosition = determinePosition();

      if ((targetPosition.isEmpty() || currentRelativePosition.isEmpty()) && !validTargetInput(targetPosition)) {
        return Optional.empty();
      }

      double xAxisOffset = currentRelativePosition.get().getX() - targetPosition.get().getX();
      double yAxisOffset = currentRelativePosition.get().getY() - targetPosition.get().getY();

      double xAngularOffset = (Math.atan2(yAxisOffset, xAxisOffset) * (180 / Math.PI)) - currentRelativePosition.get().getRotation().getX();
      double yAngularOffset = Math.asin((targetPosition.get().getZ() - currentRelativePosition.get().getZ()) / getPlanarDistance(new Translation2d(currentRelativePosition.get().getX(), currentRelativePosition.get().getY()), new Translation2d(targetPosition.get().getX(), targetPosition.get().getY())).get());

      if (Double.isNaN(xAngularOffset) || Double.isNaN(yAngularOffset)) {
        return Optional.empty();
      }

      return Optional.of(new Rotation2d(xAngularOffset, yAngularOffset));
    }

  
  /**
   * Detects whether the robot is currently occupying the space of
   * any of the nodes declared in the 'NodeStorage' class.
   * 
   * @return(s) the current node the robot occupies, or 
   * Optional.empty() if such does not apply.
   */
  public Optional<NodeStorage.Node> detectCurrentNode(Pose2d currentPosition) {
    for (NodeStorage.Node currentNode : nodeStorageInstance.nodes) {
        if (getPlanarDistance(currentPosition.getTranslation(), new Translation2d(currentNode.position.getX(), currentNode.position.getY())).get() < currentNode.radius) {
            return Optional.of(currentNode);
        }
    }

    return Optional.empty();
  }
}