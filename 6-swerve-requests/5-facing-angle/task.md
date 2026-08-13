# FieldCentricFacingAngle — Drive While Auto-Aiming

`FieldCentricFacingAngle` does what `FieldCentric` does for translation, but
**replaces the rotational input** with a heading goal. Internally, Phoenix6
runs a PID on the gyro to drive the robot's yaw to the target angle while the
driver controls translation.

The setters are the familiar pair of `withVelocityX` and `withVelocityY`,
plus a third one that configures the heading goal:

| Setter                | Argument                                          |
|-----------------------|---------------------------------------------------|
| `withVelocityX`       | forward in field frame, m/s                       |
| `withVelocityY`       | left in field frame, m/s                          |
| `withTargetDirection` | heading goal (degrees in this stub; see below)    |

There's no `withRotationalRate(...)` — the heading PID owns rotation.

## When to use it

This is the bread-and-butter auto-aim primitive:

- **Aiming the shooter at the speaker / goal** while the driver translates
  freely. The target angle is the direction of the vector from the robot's
  position to the goal — you'll do exactly this math in Lesson 8.
- **Locking the intake toward a game piece** while approaching it.
- **Holding heading during a defensive contest** so the robot stays oriented
  even as it gets bumped.

The driver flips this on with a "lock heading" trigger; release returns to
plain `FieldCentric`. We'll wire that switching as an FSM in the next task.

## Real Phoenix6 detail

Real `FieldCentricFacingAngle` takes a `Rotation2d` for the target. The stub
takes a `Double` (degrees). Real code:

```kotlin
.withTargetDirection(Rotation2d.fromDegrees(target))
```

The real request also exposes a public `HeadingController` field — the
PID controller it uses internally — and you tune kP/kI/kD on it during
configuration. Stubbed-out here.

## Your task

Implement `aimWhileDriving(vx, vy, targetDegrees)`. Build a
`FieldCentricFacingAngle` whose translation comes from `vx` and `vy` and
whose target direction is `targetDegrees`, then hand it to
`drivetrain.setControl(...)`.
