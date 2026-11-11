package upv.pm.may_ago_26.iti_271503.pgu2_eq04

import android.opengl.Matrix

/**
 * Created by mtsch on 11/16/2015.
 * Mostly sourced from https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/math/Matrix4.java
 */
class Matrix4 {
    var `val`: FloatArray = FloatArray(16)

    constructor() {
        Matrix.setIdentityM(`val`, 0)
    }

    constructor(matrix: FloatArray) {
        `val`[0] = matrix[0]
        `val`[1] = matrix[1]
        `val`[2] = matrix[2]
        `val`[3] = matrix[3]
        `val`[4] = matrix[4]
        `val`[5] = matrix[5]
        `val`[6] = matrix[6]
        `val`[7] = matrix[7]
        `val`[8] = matrix[8]
        `val`[9] = matrix[9]
        `val`[10] = matrix[10]
        `val`[11] = matrix[11]
        `val`[12] = matrix[12]
        `val`[13] = matrix[13]
        `val`[14] = matrix[14]
        `val`[15] = matrix[15]
    }

    constructor(matrix: Matrix4) {
        `val`[0] = matrix.`val`[0]
        `val`[1] = matrix.`val`[1]
        `val`[2] = matrix.`val`[2]
        `val`[3] = matrix.`val`[3]
        `val`[4] = matrix.`val`[4]
        `val`[5] = matrix.`val`[5]
        `val`[6] = matrix.`val`[6]
        `val`[7] = matrix.`val`[7]
        `val`[8] = matrix.`val`[8]
        `val`[9] = matrix.`val`[9]
        `val`[10] = matrix.`val`[10]
        `val`[11] = matrix.`val`[11]
        `val`[12] = matrix.`val`[12]
        `val`[13] = matrix.`val`[13]
        `val`[14] = matrix.`val`[14]
        `val`[15] = matrix.`val`[15]
    }

    fun toIdentity(): Matrix4 {
        Matrix.setIdentityM(`val`, 0)
        return this
    }

    fun translate(position: Vector3): Matrix4 {
        Matrix.translateM(`val`, 0, position.x, position.y, position.z)
        return this
    }

    /**
     * Renamed to setRotate, since it won't actually take initial rotation into account
     * To do this correctly, we would want a 3x3 matrix class, multiply the quaternion matrix by
     * the 3x3 component of this matrix, and set it back.
     * @param rotation the rotation quaternion
     * @return this matrix for chaining
     */
    fun setRotate(rotation: Quaternion): Matrix4 {
        val rotMat = rotation.toMatrix4()
        //Set the 3x3 component of the matrix equal to the quaternion matrix
        `val`[0] = rotMat.`val`[0]
        `val`[1] = rotMat.`val`[1]
        `val`[2] = rotMat.`val`[2]
        `val`[4] = rotMat.`val`[4]
        `val`[5] = rotMat.`val`[5]
        `val`[6] = rotMat.`val`[6]
        `val`[8] = rotMat.`val`[8]
        `val`[9] = rotMat.`val`[9]
        `val`[10] = rotMat.`val`[10]
        return this
    }

    fun scale(scale: Vector3): Matrix4 {
        Matrix.scaleM(`val`, 0, scale.x, scale.y, scale.z)
        return this
    }

    fun multiplyPoint3x4(v: Vector3): Vector3 {
        val vec = v.toFloat4()
        Matrix.multiplyMV(vec, 0, `val`, 0, vec, 0)
        return Vector3(vec)
    }

    fun multiply(matrix: Matrix4): Matrix4 {
        return multiply(matrix.`val`)
    }

    fun multiply(matrix: FloatArray?): Matrix4 {
        Matrix.multiplyMM(`val`, 0, `val`, 0, matrix, 0)
        return this
    }

    override fun toString(): String {
        var out = "MATRIX =================\n"
        for (i in 0..3) {
            for (j in 0..3) {
                out += String.format("%.5f, ", `val`[i * 4 + j])
            }
            out += "\n"
        }
        return out
    }


    companion object {
        private const val TAG = "RenderBox.Matrix4"

        //public static Matrix4 TRS(Vector3 position, Quaternion rotation, Vector3 scale) {
        fun TRS(position: Vector3, rotation: Quaternion, scale: Vector3): Matrix4 {
            val result = Matrix4()
            result.translate(position)
            result.setRotate(rotation)
            result.scale(scale)
            return result
        }

        fun TR(position: Vector3, rotation: Quaternion): Matrix4 {
            val result = Matrix4()
            result.translate(position)
            result.setRotate(rotation)
            return result
        }

        /** XX: Typically the unrotated X component for scaling, also the cosine of the angle when rotated on the Y and/or Z axis. On
         * Vector3 multiplication this value is multiplied with the source X component and added to the target X component.  */
        const val M00: Int = 0

        /** XY: Typically the negative sine of the angle when rotated on the Z axis. On Vector3 multiplication this value is multiplied
         * with the source Y component and added to the target X component.  */
        const val M01: Int = 4

        /** XZ: Typically the sine of the angle when rotated on the Y axis. On Vector3 multiplication this value is multiplied with the
         * source Z component and added to the target X component.  */
        const val M02: Int = 8

        /** XW: Typically the translation of the X component. On Vector3 multiplication this value is added to the target X component.  */
        const val M03: Int = 12

        /** YX: Typically the sine of the angle when rotated on the Z axis. On Vector3 multiplication this value is multiplied with the
         * source X component and added to the target Y component.  */
        const val M10: Int = 1

        /** YY: Typically the unrotated Y component for scaling, also the cosine of the angle when rotated on the X and/or Z axis. On
         * Vector3 multiplication this value is multiplied with the source Y component and added to the target Y component.  */
        const val M11: Int = 5

        /** YZ: Typically the negative sine of the angle when rotated on the X axis. On Vector3 multiplication this value is multiplied
         * with the source Z component and added to the target Y component.  */
        const val M12: Int = 9

        /** YW: Typically the translation of the Y component. On Vector3 multiplication this value is added to the target Y component.  */
        const val M13: Int = 13

        /** ZX: Typically the negative sine of the angle when rotated on the Y axis. On Vector3 multiplication this value is multiplied
         * with the source X component and added to the target Z component.  */
        const val M20: Int = 2

        /** ZY: Typical the sine of the angle when rotated on the X axis. On Vector3 multiplication this value is multiplied with the
         * source Y component and added to the target Z component.  */
        const val M21: Int = 6

        /** ZZ: Typically the unrotated Z component for scaling, also the cosine of the angle when rotated on the X and/or Y axis. On
         * Vector3 multiplication this value is multiplied with the source Z component and added to the target Z component.  */
        const val M22: Int = 10

        /** ZW: Typically the translation of the Z component. On Vector3 multiplication this value is added to the target Z component.  */
        const val M23: Int = 14

        /** WX: Typically the value zero. On Vector3 multiplication this value is ignored.  */
        const val M30: Int = 3

        /** WY: Typically the value zero. On Vector3 multiplication this value is ignored.  */
        const val M31: Int = 7

        /** WZ: Typically the value zero. On Vector3 multiplication this value is ignored.  */
        const val M32: Int = 11

        /** WW: Typically the value one. On Vector3 multiplication this value is ignored.  */
        const val M33: Int = 15
    }
}
