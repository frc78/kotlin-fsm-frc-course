# Check: Request Frames

Mid-match, your robot has spun around and is now facing its own driver
station. The driver pushes the stick straight away from themselves — field
+X, toward the opposing alliance wall — expecting the robot to head downfield.

Tasks 1 and 2 built two requests with identical setters (`withVelocityX`,
`withVelocityY`, `withRotationalRate`) that interpret those velocities in
different coordinate frames.

Which request makes that stick push mean "head downfield" regardless of which
way the robot is currently facing?
