# Pose2d and Frame Conversion

A `Pose2d` is a `Translation2d` plus a `Rotation2d`. It says: *"this thing is
at this point, oriented this way."*

```kotlin
val robot = Pose2d(
    translation = Translation2d(2.0, 3.0),
    rotation = Rotation2d.fromDegrees(45.0),
)

// Convenience constructor:
val same = Pose2d(2.0, 3.0, Rotation2d.fromDegrees(45.0))

robot.x            // 2.0
robot.y            // 3.0
robot.translation  // Translation2d(2.0, 3.0)
robot.rotation     // Rotation2d at 45°
```

## `relativeTo` — change of frame

The most useful operation on `Pose2d`: given a pose `p` in the field
frame, what does it look like in another pose's frame? `Pose2d` has a
method `relativeTo(reference: Pose2d): Pose2d` that returns the receiver
re-expressed in the reference's frame.

This answers questions like "if I were the origin facing forward, where
would *thing* be?" Useful for:

- "Is the goal in front of me or behind me?" — sign of `goal.relativeTo(me).x`.
- "How far off-axis is the game piece?" — `piece.relativeTo(me).y`.
- "What's my approach angle to the goal?" — `goal.relativeTo(me).rotation`.

## Your task

Implement two functions in `src/PoseMath.kt`:

1. **`gamePieceFieldPosition(robotPose, pieceInRobotFrame)`** — given the
   robot's pose and a game piece's position *in the robot's body frame*,
   return the piece's position in the field frame.

   Think of the inputs geometrically. `pieceInRobotFrame` was measured
   relative to the robot's heading and position. To re-express it in
   field coordinates, first rotate the body-frame offset into the field
   frame using the robot's heading, then shift it by the robot's
   position on the field. `Translation2d` supports both operations
   directly — see the previous task's reference of operators on
   `Translation2d`.

2. **`opponentRelativeToMe(myPose, opponentPose)`** — express the
   opponent's pose in your robot's frame. `Pose2d` has a method that
   does exactly this kind of frame change in one call. Read its name
   and signature carefully: the receiver is the pose you want to
   *transform*, and the argument is the *reference* frame.
