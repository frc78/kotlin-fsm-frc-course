# Joystick Mapping and Deadbands

Tasks 1 to 3 took velocities in meters per second and radians per second.
A joystick does not give you those. Each stick axis reads a number from
`-1.0` to `1.0`. The request wants m/s and rad/s. Something must convert one
into the other. On a real robot that code runs every tick in teleop, and it
is the first place a new drive team gets a sign wrong.

## The WPILib joystick convention

WPILib reports stick axes the way a flight stick does, not the way the field
frame does:

| Stick input        | Reads         | Must become                        |
|--------------------|---------------|------------------------------------|
| left stick forward | `leftY` < 0   | `velocityX` > 0 (+X, downfield)    |
| left stick left    | `leftX` < 0   | `velocityY` > 0 (+Y, left)         |
| right stick right  | `rightX` > 0  | `rotationalRate` < 0 (clockwise)   |

Forward on the stick is a negative Y. Left on the stick is a negative X.
Right on the rotation stick must turn the robot clockwise, and the frame
convention from task 1 says clockwise is negative. All three axes need a
sign flip.

## Scaling

Full stick must give the robot's top speed. This `Drive` has two limits:

| Limit                      | Value | Applies to       |
|----------------------------|-------|------------------|
| `maxSpeedMetersPerSecond`  | `4.5` | `velocityX`, `velocityY` |
| `maxTurnRadiansPerSecond`  | `6.0` | `rotationalRate` |

A stick value is a fraction of the matching limit.

## Deadbands

A stick that is released does not read exactly `0.0`. It rests a few percent
off center. Without a deadband the robot creeps across the floor with nobody
touching the controls.

A **deadband** is the small request the drivetrain ignores. Any velocity
smaller than the deadband is treated as zero. `FieldCentric` carries two:

| Setter                    | Units | Meaning                                  |
|---------------------------|-------|------------------------------------------|
| `withDeadband`            | m/s   | translation smaller than this is ignored |
| `withRotationalDeadband`  | rad/s | rotation smaller than this is ignored    |

Ten percent of each limit is a common starting point. The real drivetrain
applies the deadband inside `setControl`. This stub stores the values and does
not apply them, so the request you build still carries the raw scaled
velocities.

## Your task

Open `src/Drive.kt`. Implement `drive(leftX, leftY, rightX)`. It sends one
`FieldCentric` request per call with these fields:

| Request field         | Value                                |
|-----------------------|--------------------------------------|
| `velocityX`           | `leftY`, sign flipped, times `4.5`   |
| `velocityY`           | `leftX`, sign flipped, times `4.5`   |
| `rotationalRate`      | `rightX`, sign flipped, times `6.0`  |
| `deadband`            | `0.45` (ten percent of `4.5`)        |
| `rotationalDeadband`  | `0.6` (ten percent of `6.0`)         |

Use the two limit fields, not the literal numbers. Call `setControl(...)`
once per call to `drive`.
