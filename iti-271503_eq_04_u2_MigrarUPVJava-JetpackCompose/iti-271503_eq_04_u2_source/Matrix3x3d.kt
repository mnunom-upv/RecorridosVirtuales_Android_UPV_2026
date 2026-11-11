package upv.pm.may_ago_26.iti_271503.pgu2_eq04

class Matrix3x3d {
    var m: DoubleArray

    constructor() : super() {
        this.m = DoubleArray(9)
    }

    constructor(
        m00: Double,
        m01: Double,
        m02: Double,
        m10: Double,
        m11: Double,
        m12: Double,
        m20: Double,
        m21: Double,
        m22: Double
    ) : super() {
        (DoubleArray(9).also { this.m = it })[0] = m00
        this.m[1] = m01
        this.m[2] = m02
        this.m[3] = m10
        this.m[4] = m11
        this.m[5] = m12
        this.m[6] = m20
        this.m[7] = m21
        this.m[8] = m22
    }

    constructor(o: Matrix3x3d) : super() {
        (DoubleArray(9).also { this.m = it })[0] = o.m[0]
        this.m[1] = o.m[1]
        this.m[2] = o.m[2]
        this.m[3] = o.m[3]
        this.m[4] = o.m[4]
        this.m[5] = o.m[5]
        this.m[6] = o.m[6]
        this.m[7] = o.m[7]
        this.m[8] = o.m[8]
    }

    fun set(
        m00: Double,
        m01: Double,
        m02: Double,
        m10: Double,
        m11: Double,
        m12: Double,
        m20: Double,
        m21: Double,
        m22: Double
    ) {
        this.m[0] = m00
        this.m[1] = m01
        this.m[2] = m02
        this.m[3] = m10
        this.m[4] = m11
        this.m[5] = m12
        this.m[6] = m20
        this.m[7] = m21
        this.m[8] = m22
    }

    fun set(o: Matrix3x3d) {
        this.m[0] = o.m[0]
        this.m[1] = o.m[1]
        this.m[2] = o.m[2]
        this.m[3] = o.m[3]
        this.m[4] = o.m[4]
        this.m[5] = o.m[5]
        this.m[6] = o.m[6]
        this.m[7] = o.m[7]
        this.m[8] = o.m[8]
    }

    fun setZero() {
        this.m[0] = 0.0
        this.m[1] = 0.0
        this.m[2] = 0.0
        this.m[3] = 0.0
        this.m[4] = 0.0
        this.m[5] = 0.0
        this.m[6] = 0.0
        this.m[7] = 0.0
        this.m[8] = 0.0
    }

    fun setIdentity() {
        this.m[0] = 1.0
        this.m[1] = 0.0
        this.m[2] = 0.0
        this.m[3] = 0.0
        this.m[4] = 1.0
        this.m[5] = 0.0
        this.m[6] = 0.0
        this.m[7] = 0.0
        this.m[8] = 1.0
    }

    fun setSameDiagonal(d: Double) {
        this.m[0] = d
        this.m[4] = d
        this.m[8] = d
    }

    fun get(row: Int, col: Int): Double {
        return this.m[3 * row + col]
    }

    fun set(row: Int, col: Int, value: Double) {
        this.m[3 * row + col] = value
    }

    fun getColumn(col: Int, v: Vector3d) {
        v.x = this.m[col]
        v.y = this.m[col + 3]
        v.z = this.m[col + 6]
    }

    fun setColumn(col: Int, v: Vector3d) {
        this.m[col] = v.x
        this.m[col + 3] = v.y
        this.m[col + 6] = v.z
    }

    fun scale(s: Double) {
        for (i in 0..8) {
            this.m[i] *= s
        }
    }

    fun plusEquals(b: Matrix3x3d) {
        for (i in 0..8) {
            this.m[i] += b.m[i]
        }
    }

    fun minusEquals(b: Matrix3x3d) {
        for (i in 0..8) {
            this.m[i] -= b.m[i]
        }
    }

    fun transpose() {
        var tmp = this.m[1]
        this.m[1] = this.m[3]
        this.m[3] = tmp
        tmp = this.m[2]
        this.m[2] = this.m[6]
        this.m[6] = tmp
        tmp = this.m[5]
        this.m[5] = this.m[7]
        this.m[7] = tmp
    }

    fun transpose(result: Matrix3x3d) {
        val m1 = this.m[1]
        val m2 = this.m[2]
        val m3 = this.m[5]
        result.m[0] = this.m[0]
        result.m[1] = this.m[3]
        result.m[2] = this.m[6]
        result.m[3] = m1
        result.m[4] = this.m[4]
        result.m[5] = this.m[7]
        result.m[6] = m2
        result.m[7] = m3
        result.m[8] = this.m[8]
    }

    fun determinant(): Double {
        return (this.get(0, 0) * (this.get(1, 1) * this.get(2, 2) - this.get(2, 1) * this.get(1, 2))
                - this.get(0, 1) * (this.get(1, 0) * this.get(2, 2) - this.get(1, 2) * this.get(
            2,
            0
        ))
                + this.get(0, 2) * (this.get(1, 0) * this.get(2, 1) - this.get(1, 1) * this.get(
            2,
            0
        )))
    }

    fun invert(result: Matrix3x3d): Boolean {
        val d = this.determinant()
        if (d == 0.0) {
            return false
        }
        val invdet = 1.0 / d
        result.set(
            (this.m[4] * this.m[8] - this.m[7] * this.m[5]) * invdet,
            -(this.m[1] * this.m[8] - this.m[2] * this.m[7]) * invdet,
            (this.m[1] * this.m[5] - this.m[2] * this.m[4]) * invdet,
            -(this.m[3] * this.m[8] - this.m[5] * this.m[6]) * invdet,
            (this.m[0] * this.m[8] - this.m[2] * this.m[6]) * invdet,
            -(this.m[0] * this.m[5] - this.m[3] * this.m[2]) * invdet,
            (this.m[3] * this.m[7] - this.m[6] * this.m[4]) * invdet,
            -(this.m[0] * this.m[7] - this.m[6] * this.m[1]) * invdet,
            (this.m[0] * this.m[4] - this.m[3] * this.m[1]) * invdet
        )
        return true
    }

    override fun toString(): String {
        val builder = StringBuilder().append("{ ")
        for (i in 0..8) {
            builder.append(this.m[i].toString())
            if (i < 9 - 1) {
                builder.append(", ")
            }
        }
        builder.append(" }")
        return builder.toString()
    }

    companion object {
        fun add(a: Matrix3x3d, b: Matrix3x3d, result: Matrix3x3d) {
            result.m[0] = a.m[0] + b.m[0]
            result.m[1] = a.m[1] + b.m[1]
            result.m[2] = a.m[2] + b.m[2]
            result.m[3] = a.m[3] + b.m[3]
            result.m[4] = a.m[4] + b.m[4]
            result.m[5] = a.m[5] + b.m[5]
            result.m[6] = a.m[6] + b.m[6]
            result.m[7] = a.m[7] + b.m[7]
            result.m[8] = a.m[8] + b.m[8]
        }

        fun mult(a: Matrix3x3d, b: Matrix3x3d, result: Matrix3x3d) {
            result.set(
                a.m[0] * b.m[0] + a.m[1] * b.m[3] + a.m[2] * b.m[6],
                a.m[0] * b.m[1] + a.m[1] * b.m[4] + a.m[2] * b.m[7],
                a.m[0] * b.m[2] + a.m[1] * b.m[5] + a.m[2] * b.m[8],
                a.m[3] * b.m[0] + a.m[4] * b.m[3] + a.m[5] * b.m[6],
                a.m[3] * b.m[1] + a.m[4] * b.m[4] + a.m[5] * b.m[7],
                a.m[3] * b.m[2] + a.m[4] * b.m[5] + a.m[5] * b.m[8],
                a.m[6] * b.m[0] + a.m[7] * b.m[3] + a.m[8] * b.m[6],
                a.m[6] * b.m[1] + a.m[7] * b.m[4] + a.m[8] * b.m[7],
                a.m[6] * b.m[2] + a.m[7] * b.m[5] + a.m[8] * b.m[8]
            )
        }

        fun mult(a: Matrix3x3d, v: Vector3d, result: Vector3d) {
            val x = a.m[0] * v.x + a.m[1] * v.y + a.m[2] * v.z
            val y = a.m[3] * v.x + a.m[4] * v.y + a.m[5] * v.z
            val z = a.m[6] * v.x + a.m[7] * v.y + a.m[8] * v.z
            result.x = x
            result.y = y
            result.z = z
        }
    }
}
