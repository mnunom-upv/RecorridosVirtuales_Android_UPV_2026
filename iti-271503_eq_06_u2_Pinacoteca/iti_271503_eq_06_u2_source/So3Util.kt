package upv.pm.may_ago_26.iti_271503.pgu2_eq06

import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object So3Util {

    private const val M_SQRT1_2 = 0.7071067811865476
    private const val ONE_6TH = 0.1666666716337204
    private const val ONE_20TH = 0.1666666716337204

    private val temp31: Vector3d = Vector3d()
    private val sO3FromTwoVecN: Vector3d = Vector3d()
    private val sO3FromTwoVecA: Vector3d = Vector3d()
    private val sO3FromTwoVecB: Vector3d = Vector3d()
    private val sO3FromTwoVecRotationAxis: Vector3d = Vector3d()
    private val sO3FromTwoVec33R1: Matrix3x3d = Matrix3x3d()
    private val sO3FromTwoVec33R2: Matrix3x3d = Matrix3x3d()
    private val muFromSO3R2: Vector3d = Vector3d()
    private val rotationPiAboutAxisTemp: Vector3d = Vector3d()

    fun sO3FromTwoVec(a: Vector3d, b: Vector3d, result: Matrix3x3d) {
        Vector3d.cross(a, b, sO3FromTwoVecN)

        if (sO3FromTwoVecN.length() == 0.0) {
            val dot: Double = Vector3d.dot(a, b)

            if (dot >= 0.0) {
                result.setIdentity()
            } else {
                Vector3d.ortho(a, sO3FromTwoVecRotationAxis)
                rotationPiAboutAxis(sO3FromTwoVecRotationAxis, result)
            }

            return
        }

        sO3FromTwoVecA.set(a)
        sO3FromTwoVecB.set(b)
        sO3FromTwoVecN.normalize()
        sO3FromTwoVecA.normalize()
        sO3FromTwoVecB.normalize()

        val r1 = sO3FromTwoVec33R1
        r1.setColumn(0, sO3FromTwoVecA)
        r1.setColumn(1, sO3FromTwoVecN)

        Vector3d.cross(
            sO3FromTwoVecN,
            sO3FromTwoVecA,
            temp31
        )

        r1.setColumn(2, temp31)

        val r2 = sO3FromTwoVec33R2
        r2.setColumn(0, sO3FromTwoVecB)
        r2.setColumn(1, sO3FromTwoVecN)

        Vector3d.cross(
            sO3FromTwoVecN,
            sO3FromTwoVecB,
            temp31
        )

        r2.setColumn(2, temp31)

        r1.transpose()

        Matrix3x3d.mult(
            r2,
            r1,
            result
        )
    }

    private fun rotationPiAboutAxis(v: Vector3d, result: Matrix3x3d) {
        rotationPiAboutAxisTemp.set(v)

        rotationPiAboutAxisTemp.scale(
            Math.PI / rotationPiAboutAxisTemp.length()
        )

        val kA = 0.0
        val kB = 0.20264236728467558

        rodriguesSo3Exp(
            rotationPiAboutAxisTemp,
            kA,
            kB,
            result
        )
    }

    fun sO3FromMu(w: Vector3d, result: Matrix3x3d) {
        val thetaSq: Double = Vector3d.dot(w, w)
        val theta = sqrt(thetaSq)

        val kA: Double
        val kB: Double

        if (thetaSq < 1.0E-8) {
            kA = 1.0 - ONE_6TH * thetaSq
            kB = 0.5
        } else if (thetaSq < 1.0E-6) {
            kB = 0.5 - 0.0416666679084301 * thetaSq
            kA = 1.0 - thetaSq * ONE_6TH * (1.0 - ONE_20TH * thetaSq)
        } else {
            val invTheta = 1.0 / theta
            kA = sin(theta) * invTheta
            kB = (1.0 - cos(theta)) * (invTheta * invTheta)
        }

        rodriguesSo3Exp(
            w,
            kA,
            kB,
            result
        )
    }

    fun muFromSO3(so3: Matrix3x3d, result: Vector3d) {
        val cosAngle =
            (so3.get(0, 0) + so3.get(1, 1) + so3.get(2, 2) - 1.0) * 0.5

        result.set(
            (so3.get(2, 1) - so3.get(1, 2)) / 2.0,
            (so3.get(0, 2) - so3.get(2, 0)) / 2.0,
            (so3.get(1, 0) - so3.get(0, 1)) / 2.0
        )

        val sinAngleAbs = result.length()

        if (cosAngle > M_SQRT1_2) {
            if (sinAngleAbs > 0.0) {
                result.scale(asin(sinAngleAbs) / sinAngleAbs)
            }
        } else if (cosAngle > -M_SQRT1_2) {
            val angle = acos(cosAngle)

            if (sinAngleAbs > 0.0) {
                result.scale(angle / sinAngleAbs)
            }
        } else {
            val angle = Math.PI - asin(sinAngleAbs)

            val d0 = so3.get(0, 0) - cosAngle
            val d1 = so3.get(1, 1) - cosAngle
            val d2 = so3.get(2, 2) - cosAngle

            val r2 = muFromSO3R2

            if (d0 * d0 > d1 * d1 && d0 * d0 > d2 * d2) {
                r2.set(
                    d0,
                    (so3.get(1, 0) + so3.get(0, 1)) / 2.0,
                    (so3.get(0, 2) + so3.get(2, 0)) / 2.0
                )
            } else if (d1 * d1 > d2 * d2) {
                r2.set(
                    (so3.get(1, 0) + so3.get(0, 1)) / 2.0,
                    d1,
                    (so3.get(2, 1) + so3.get(1, 2)) / 2.0
                )
            } else {
                r2.set(
                    (so3.get(0, 2) + so3.get(2, 0)) / 2.0,
                    (so3.get(2, 1) + so3.get(1, 2)) / 2.0,
                    d2
                )
            }

            if (Vector3d.dot(r2, result) < 0.0) {
                r2.scale(-1.0)
            }

            r2.normalize()
            r2.scale(angle)
            result.set(r2)
        }
    }

    private fun rodriguesSo3Exp(
        w: Vector3d,
        kA: Double,
        kB: Double,
        result: Matrix3x3d
    ) {
        val wx2 = w.x * w.x
        val wy2 = w.y * w.y
        val wz2 = w.z * w.z

        result.set(0, 0, 1.0 - kB * (wy2 + wz2))
        result.set(1, 1, 1.0 - kB * (wx2 + wz2))
        result.set(2, 2, 1.0 - kB * (wx2 + wy2))

        var a = kA * w.z
        var b = kB * (w.x * w.y)

        result.set(0, 1, b - a)
        result.set(1, 0, b + a)

        a = kA * w.y
        b = kB * (w.x * w.z)

        result.set(0, 2, b + a)
        result.set(2, 0, b - a)

        a = kA * w.x
        b = kB * (w.y * w.z)

        result.set(1, 2, b - a)
        result.set(2, 1, b + a)
    }

    fun generatorField(i: Int, pos: Matrix3x3d, result: Matrix3x3d) {
        result.set(i, 0, 0.0)

        result.set(
            (i + 1) % 3,
            0,
            -pos.get((i + 2) % 3, 0)
        )

        result.set(
            (i + 2) % 3,
            0,
            pos.get((i + 1) % 3, 0)
        )
    }
}