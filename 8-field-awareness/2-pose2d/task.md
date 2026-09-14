# Pose2d and Frame Conversion

A `Pose2d` is a `Translation2d` plus a `Rotation2d`. It says: "this thing is
at this point, and it faces this way."

```kotlin
val robot = Pose2d(
    translation = Translation2d(2.0, 3.0),
    rotation = Rotation2d.fromDegrees(45.0),
)

// Shorter constructor, same pose:
val same = Pose2d(2.0, 3.0, Rotation2d.fromDegrees(45.0))

robot.x            // 2.0
robot.y            // 3.0
robot.translation  // Translation2d(2.0, 3.0)
robot.rotation     // Rotation2d at 45°
```

## Two frames

The **field frame** is the one from task 1. The **robot frame** (also called
the body frame) has its origin at the robot's center. +X points out of the
robot's nose, and +Y points to the robot's left. A camera reports a game
piece in the robot frame. The pose estimator reports the robot in the field
frame. To use both together you convert one into the other.

## `relativeTo`

`Pose2d` has one method for this:

`fun relativeTo(reference: Pose2d): Pose2d`

It returns the receiver pose expressed in the frame of `reference`. The
receiver is the pose you want to convert. The argument is the frame you want
it in.

| Question                                | Expression                       |
|-----------------------------------------|----------------------------------|
| Is the goal in front of me or behind me? | sign of `goal.relativeTo(me).x`  |
| How far off-axis is the game piece?     | `piece.relativeTo(me).y`         |
| What is my approach angle to the goal?  | `goal.relativeTo(me).rotation`   |

## Your task

Implement two functions in `src/PoseMath.kt`.

**`gamePieceFieldPosition(robotPose, pieceInRobotFrame)`** returns the piece's
position in the field frame. `pieceInRobotFrame` is a `Translation2d` measured
from the robot's center, in the robot frame. Use the `Translation2d`
operations from task 1.

| Robot pose            | Piece in robot frame | Piece in field frame |
|-----------------------|----------------------|----------------------|
| `(0, 0)` facing 0°    | `(2, 0)`             | `(2, 0)`             |
| `(5, 5)` facing 0°    | `(1, 0)`             | `(6, 5)`             |
| `(0, 0)` facing 90°   | `(1, 0)`             | `(0, 1)`             |
| `(4, 3)` facing 90°   | `(2, 0)`             | `(4, 5)`             |

In the last row the robot's heading changes the offset and the robot's
position shifts it. Both matter, and the order matters.

**`opponentRelativeToMe(myPose, opponentPose)`** returns the opponent's pose
in my robot frame. `relativeTo` does this in one call.

| My pose               | Opponent pose          | Result               |
|-----------------------|------------------------|----------------------|
| `(0, 0)` facing 0°    | `(3, 0)` facing 0°     | `(3, 0)` facing 0°   |
| `(0, 0)` facing 0°    | `(0, 2)` facing 0°     | `(0, 2)` facing 0°   |
| `(0, 0)` facing 90°   | `(0, 5)` facing 90°    | `(5, 0)` facing 0°   |
