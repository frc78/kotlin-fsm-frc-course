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

robot.x          // 2.0
robot.y          // 3.0
robot.rotation   // Rotation2d at 45°
```

## `relativeTo` — change of frame

The most useful operation on `Pose2d`: given a pose `p` in the field frame,
what does it look like in another pose's frame?

```kotlin
val opponentRelativeToMe = opponentPose.relativeTo(myPose)
```

This says: "if I were the origin facing forward, where would the opponent
be?" Useful for:

- "Is the goal in front of me or behind me?" — check the sign of
  `goal.relativeTo(me).x`.
- "How far off-axis is the game piece?" — `piece.relativeTo(me).y`.
- "What's my approach angle to the goal?" —
  `goal.relativeTo(me).rotation`.

## Your task

Implement two functions in `src/PoseMath.kt`:

1. **`gamePieceFieldPosition(robotPose, pieceInRobotFrame)`** — given the
   robot's pose and a game piece's position *in the robot's body frame*,
   compute the piece's position in the field frame.

   Hint: rotate the piece offset by the robot's heading, then add the
   robot's translation.

2. **`opponentRelativeToMe(myPose, opponentPose)`** — express the opponent's
   pose in your robot's frame.

   Hint: use `Pose2d.relativeTo`. (Read the order carefully — it's the pose
   you want to *transform* dot `relativeTo(referenceFrame)`.)
