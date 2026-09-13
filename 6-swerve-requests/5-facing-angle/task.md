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
| `withTargetDirection` | heading goal, degrees in this stub (see below)    |

There is no `withRotationalRate(...)`. The heading PID owns rotation.

## When to use it

- **Aim the shooter at the goal** while the driver translates freely. The
  target angle is the direction from the robot's position to the goal. Lesson
  8 does that math.
- **Lock the intake toward a game piece** while you approach it.
- **Hold heading in a defensive contest** so bumps do not turn the robot.

The driver turns this on with a "lock heading" trigger. Release returns to
plain `FieldCentric`. The next task wires that switching as an FSM.

## Real Phoenix6 detail

Real `FieldCentricFacingAngle` takes a `Rotation2d` for the target, so real
code wraps the degrees with `Rotation2d.fromDegrees(...)`. The stub takes a
`Double` in degrees. The real request also has a public `HeadingController`
field, the PID controller it uses inside. You tune its kP, kI, and kD during
configuration. The stub omits it.

## Your task

Implement `aimWhileDriving(vx, vy, targetDegrees)`. The drivetrain must
receive one `FieldCentricFacingAngle` request with these fields:

| Parameter       | Request field     |
|-----------------|-------------------|
| `vx`            | `velocityX`       |
| `vy`            | `velocityY`       |
| `targetDegrees` | `targetDirection` |
