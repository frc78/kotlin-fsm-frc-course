# FieldCentricFacingAngle: Drive While Auto-Aiming

`FieldCentricFacingAngle` does what `FieldCentric` does for translation, but
**replaces the rotational input** with a heading goal. Inside, Phoenix6 runs a
PID controller on the gyro to turn the robot's yaw (heading angle) to the
target while the driver controls translation. Lesson 5 task 4 introduced PID
gains; this request has its own PID for heading.

The setters are the familiar pair `withVelocityX` and `withVelocityY`, plus a
third one for the heading goal:

| Setter                | Argument                                          |
|-----------------------|---------------------------------------------------|
| `withVelocityX`       | forward in field frame, m/s                       |
| `withVelocityY`       | left in field frame, m/s                          |
| `withTargetDirection` | heading goal, a `Rotation2d`                      |

There is no `withRotationalRate(...)`. The heading PID owns rotation.

## Rotation2d

`Rotation2d` is the WPILib type for an angle. It stores radians and offers
`degrees` as a read-only property. You build one from degrees with
`Rotation2d.fromDegrees(...)`. Real Phoenix6 takes a `Rotation2d` here, and
so does this stub. Lesson 8 uses the same type for pose math.

## When to use it

- **Aim the shooter at the goal** while the driver translates freely. The
  target angle is the direction from the robot's position to the goal. Lesson
  8 does that math.
- **Lock the intake toward a game piece** while you approach it.
- **Hold heading in a defensive contest** so bumps do not turn the robot.

The driver turns this on with a "lock heading" trigger. Release returns to
plain `FieldCentric`. Task 5 wires that switching as an FSM.

## Real Phoenix6 detail

The real request has a public `HeadingController` field, the PID controller
it uses inside. You tune its kP, kI, and kD during configuration. The stub
omits it.

## Your task

Implement `aimWhileDriving(vx, vy, targetDegrees)`. The drivetrain must
receive one `FieldCentricFacingAngle` request with these fields:

| Parameter       | Request field     | Type         |
|-----------------|-------------------|--------------|
| `vx`            | `velocityX`       | `Double`     |
| `vy`            | `velocityY`       | `Double`     |
| `targetDegrees` | `targetDirection` | `Rotation2d` |
