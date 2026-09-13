# Check: Vision Gating

`Vision` from task 5 is in `TRACKING`. On the next tick it sees this input:

| Value                                  | Number       |
|----------------------------------------|--------------|
| `drivetrain.state.pose` translation    | `(4.0, 3.0)` |
| `latestMeasurement.pose` translation   | `(9.2, 1.0)` |
| `nowSeconds`                           | `10.00`      |
| `latestMeasurement.timestampSeconds`   | `9.98`       |
| `latestMeasurement.translationStdDev`  | `0.3`        |
| `lastAppliedTimestampSeconds`          | `9.95`       |

After `periodic()`, what state is `Vision` in?
