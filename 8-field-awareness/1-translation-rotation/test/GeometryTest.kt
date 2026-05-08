package course.l8t1

import frc.stubs.geometry.Rotation2d
import frc.stubs.geometry.Translation2d
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals

class GeometryTest {
    private val tol = 1e-9

    @Test fun distance_along_x_axis() {
        assertEquals(3.0, distanceBetween(Translation2d(1.0, 0.0), Translation2d(4.0, 0.0)), tol)
    }

    @Test fun distance_along_y_axis() {
        assertEquals(2.0, distanceBetween(Translation2d(0.0, 1.0), Translation2d(0.0, 3.0)), tol)
    }

    @Test fun distance_3_4_5_diagonal() {
        assertEquals(5.0, distanceBetween(Translation2d(0.0, 0.0), Translation2d(3.0, 4.0)), tol)
    }

    @Test fun heading_to_plus_x_is_zero() {
        val h = headingFromTo(Translation2d(0.0, 0.0), Translation2d(5.0, 0.0))
        assertEquals(0.0, h.radians, tol)
    }

    @Test fun heading_to_plus_y_is_ninety_degrees() {
        val h = headingFromTo(Translation2d(0.0, 0.0), Translation2d(0.0, 5.0))
        assertEquals(PI / 2, h.radians, tol)
    }

    @Test fun heading_back_along_x_is_pi() {
        val h = headingFromTo(Translation2d(2.0, 0.0), Translation2d(-1.0, 0.0))
        assertEquals(PI, h.radians, tol)
    }

    @Test fun rotate_unit_x_by_ninety() {
        val p = rotatePoint(Translation2d(1.0, 0.0), Rotation2d.fromDegrees(90.0))
        assertEquals(0.0, p.x, tol)
        assertEquals(1.0, p.y, tol)
    }

    @Test fun rotate_unit_x_by_one_eighty() {
        val p = rotatePoint(Translation2d(1.0, 0.0), Rotation2d.fromDegrees(180.0))
        assertEquals(-1.0, p.x, tol)
        assertEquals(0.0, p.y, tol)
    }
}
