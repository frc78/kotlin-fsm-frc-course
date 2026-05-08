# FieldCentricFacingAngle — Drive While Auto-Aiming

`FieldCentricFacingAngle` does what `FieldCentric` does for translation, but
**replaces the rotational input** with a heading goal. Internally, Phoenix6
runs a PID on the gyro to drive the robot's yaw to the target angle while the
driver controls translation.

```kotlin
FieldCentricFacingAngle()
    .withVelocityX(vx)
    .withVelocityY(vy)
    .withTargetDirection(targetDirectionDegrees)
```

There's no `withRotationalRate(...)` — the heading PID owns rotation.

## When to use it

This is the bread-and-butter auto-aim primitive:

- **Aiming the shooter at the speaker / goal** while the driver translates
  freely. Compute target angle from `(robotPose - goalPose).angle`.
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

Real Phoenix6 also has `getHeadingController()` which returns the PIDController
the request uses internally — you tune kP/kI/kD on it during configuration.
Stubbed-out here.

## Your task

Implement `aimWhileDriving(vx, vy, targetDegrees)`. Same shape as the previous
tasks: build a request, apply it.
