# The Drivebase FSM: Assisted Driving and Auto-Align

In lesson 6 you built a drive-mode FSM that picked a `SwerveRequest` from
driver buttons. LIGHTNING's drivebase is that same machine, grown up. The
drivebase diagram on p. 26 of 2056's
[technical binder](https://2056.ca/wp-content/uploads/2025/05/OPR25-2056-Technical-Binder.pdf)
shows four states: field-centric driving by default, robot-centric on a stick
click, **assisted drive** while the intake button is held, and **auto-align**
while a reef level button is held. The new pair feeds *vision* into the
request — the FSM decides, every tick, whether the driver or the camera is
steering.

The two constants you'll declare are 2056's real values from their published
code: the assist scale `0.03` and the drive-to-point P gain `4.0`. The poses
and camera offsets the tests use are course values.

## Assisted drive

While the intake is deployed, the Limelight reports the horizontal angle from
the crosshair to the nearest coral — `coralOffsetDegrees` here, `tx` on a real
Limelight. Assisted drive keeps the driver's forward speed and rotation but
**replaces the sideways input** with a correction proportional to that
offset: `ASSIST_SCALE * coralOffsetDegrees`. The request is `RobotCentric`,
because the camera is bolted to the robot — its offset is a robot-relative
sideways error. Drive at the coral and the robot slides itself onto it.

## Auto-align: drive-to-point

While a level button (L1–L4) is held, the drivebase drives itself to the reef
pole. It's a **P controller on position error** — commanded velocity
proportional to the distance left to travel, per axis — with the heading
locked to the pole's approach angle by a `FieldCentricFacingAngle` request.
`robotPose` comes from odometry (lesson 8); `targetPole` comes from the field
map.

One catch, straight from 2056's
[Chief Delphi Q&A](https://www.chiefdelphi.com/t/team-2056-op-robotics-2025-technical-binder-release/502550):
auto-align **requires a visible AprilTag to start**. No tag, no align — the
FSM stays in `FIELD_DRIVE` and the driver keeps driving. Losing the tag
*mid*-align does not abort, though; only releasing the button does. The guard
lives on the entry transition, not in the state.

## Your task

Open `src/DriveBase.kt`. The sticks, buttons, and vision inputs are given.
Three pieces:

**Constants** — declare `ASSIST_SCALE = 0.03` and `ALIGN_KP = 4.0` at the
top of the object (`private const val`, like every subsystem this lesson).
They're the only two numbers this FSM owns, and the tables below refer to
them by name — every number in your FSM should have a name.

**`stateTransitions()`** (priority order — first match wins):

| Current          | Condition                          | Next             |
|------------------|------------------------------------|------------------|
| `FIELD_DRIVE`    | `commandedAutoAlign && tagVisible` | `AUTO_ALIGN`     |
| `FIELD_DRIVE`    | `commandedIntakeAssist`            | `ASSISTED_DRIVE` |
| `FIELD_DRIVE`    | `commandedRobotDrive`              | `ROBOT_DRIVE`    |
| `ROBOT_DRIVE`    | `commandedFieldDrive`              | `FIELD_DRIVE`    |
| `ASSISTED_DRIVE` | `!commandedIntakeAssist`           | `FIELD_DRIVE`    |
| `AUTO_ALIGN`     | `!commandedAutoAlign`              | `FIELD_DRIVE`    |

`commandedRobotDrive`/`commandedFieldDrive` are stick clicks (R3/L3), so
`ROBOT_DRIVE` latches: releasing R3 changes nothing, and only L3 leaves. The
assist and align buttons are holds. Note the first row beats the second —
align (with a tag) wins over assist when both are held.

**`stateActions()`** — build the state's request and pass it to
`drivetrain.setControl(...)`:

| State            | Request                   | `velocityX`                               | `velocityY`                               | rotation field                                    |
|------------------|---------------------------|-------------------------------------------|-------------------------------------------|---------------------------------------------------|
| `FIELD_DRIVE`    | `FieldCentric`            | `stickVx`                                 | `stickVy`                                 | `rotationalRate` = `stickOmega`                   |
| `ROBOT_DRIVE`    | `RobotCentric`            | `stickVx`                                 | `stickVy`                                 | `rotationalRate` = `stickOmega`                   |
| `ASSISTED_DRIVE` | `RobotCentric`            | `stickVx`                                 | `ASSIST_SCALE * coralOffsetDegrees`       | `rotationalRate` = `stickOmega`                   |
| `AUTO_ALIGN`     | `FieldCentricFacingAngle` | `ALIGN_KP * (targetPole.x - robotPose.x)` | `ALIGN_KP * (targetPole.y - robotPose.y)` | `targetDirection` = `targetPole.rotation.degrees` |

In `ASSISTED_DRIVE` the driver's `stickVy` is ignored — vision owns the Y
axis. In `AUTO_ALIGN` all three sticks are ignored — the pose error owns
everything.
