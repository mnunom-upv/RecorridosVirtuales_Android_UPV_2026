package upv.pm.may_ago_26.iti_271503.pgu2_eq06

import kotlin.math.abs

class Distortion {
    private var mCoefficients: FloatArray = DEFAULT_COEFFICIENTS.clone()

    constructor() : super() {
        this.mCoefficients = DEFAULT_COEFFICIENTS.clone()
    }

    constructor(other: Distortion) : super() {
        this.coefficients = other.mCoefficients
    }

    fun toProtobuf(): FloatArray {
        return this.mCoefficients.clone()
    }

    var coefficients: FloatArray?
        get() = this.mCoefficients
        set(coefficients) {
            this.mCoefficients =
                (if (coefficients != null) coefficients.clone() else FloatArray(0))
        }

    fun distortionFactor(radius: Float): Float {
        var result = 1.0f
        var rFactor = 1.0f
        val rSquared = radius * radius
        for (ki in this.mCoefficients) {
            rFactor *= rSquared
            result += ki * rFactor
        }
        return result
    }

    fun distort(radius: Float): Float {
        return radius * this.distortionFactor(radius)
    }

    fun distortInverse(radius: Float): Float {
        var r0 = radius / 0.9f
        var r = radius * 0.9f
        var dr0 = radius - this.distort(r0)
        while (abs(r - r0) > 1.0E-4) {
            val dr = radius - this.distort(r)
            val r2 = r - dr * ((r - r0) / (dr - dr0))
            r0 = r
            r = r2
            dr0 = dr
        }
        return r
    }

    fun getApproximateInverseDistortion(maxRadius: Float): Distortion {
        val numSamples = 10
        val numCoefficients = 2
        val matA = Array<DoubleArray?>(10) { DoubleArray(2) }
        val vecY = DoubleArray(10)
        for (i in 0..9) {
            val r = maxRadius * (i + 1) / 10.0f
            var v: Double
            v = this.distort(r).toDouble()
            val rp = v
            for (j in 0..1) {
                v *= rp * rp
                matA[i]!![j] = v
            }
            vecY[i] = r - rp
        }
        val vecK: DoubleArray = solveLeastSquares(matA, vecY)
        val coefficients = FloatArray(vecK.size)
        for (k in vecK.indices) {
            coefficients[k] = vecK[k].toFloat()
        }
        val inverse = Distortion()
        inverse.coefficients = coefficients
        return inverse
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        if (other === this) {
            return true
        }
        if (other !is Distortion) {
            return false
        }
        val o = other
        return this.mCoefficients.contentEquals(o.mCoefficients)
    }

    override fun toString(): String {
        val builder = StringBuilder().append("{\n").append("  coefficients: [")
        for (i in this.mCoefficients.indices) {
            builder.append(this.mCoefficients[i].toString())
            if (i < this.mCoefficients.size - 1) {
                builder.append(", ")
            }
        }
        builder.append("],\n}")
        return builder.toString()
    }

    companion object {
        private val DEFAULT_COEFFICIENTS: FloatArray
        fun parseFromProtobuf(coefficients: FloatArray?): Distortion {
            val distortion = Distortion()
            distortion.coefficients = coefficients
            return distortion
        }

        private fun solveLeastSquares(matA: Array<DoubleArray?>, vecY: DoubleArray): DoubleArray {
            val numSamples = matA.size
            val numCoefficients = matA[0]!!.size
            val matATA = Array<DoubleArray?>(numCoefficients) { DoubleArray(numCoefficients) }
            for (k in 0..<numCoefficients) {
                for (j in 0..<numCoefficients) {
                    var sum = 0.0
                    for (i in 0..<numSamples) {
                        sum += matA[i]!![j] * matA[i]!![k]
                    }
                    matATA[j]!![k] = sum
                }
            }
            val matInvATA = Array<DoubleArray?>(numCoefficients) { DoubleArray(numCoefficients) }
            if (numCoefficients != 2) {
                throw RuntimeException(
                    StringBuilder()
                        .append("solveLeastSquares: only 2 coefficients currently supported, ")
                        .append(numCoefficients).append(" given.").toString()
                )
            }
            val det = matATA[0]!![0] * matATA[1]!![1] - matATA[0]!![1] * matATA[1]!![0]
            matInvATA[0]!![0] = matATA[1]!![1] / det
            matInvATA[1]!![1] = matATA[0]!![0] / det
            matInvATA[0]!![1] = -matATA[1]!![0] / det
            matInvATA[1]!![0] = -matATA[0]!![1] / det
            val vecATY = DoubleArray(numCoefficients)
            for (l in 0..<numCoefficients) {
                var sum2 = 0.0
                for (m in 0..<numSamples) {
                    sum2 += matA[m]!![l] * vecY[m]
                }
                vecATY[l] = sum2
            }
            val vecX = DoubleArray(numCoefficients)
            for (j2 in 0..<numCoefficients) {
                var sum3 = 0.0
                for (i2 in 0..<numCoefficients) {
                    sum3 += matInvATA[i2]!![j2] * vecATY[i2]
                }
                vecX[j2] = sum3
            }
            return vecX
        }

        init {
            DEFAULT_COEFFICIENTS = floatArrayOf(0.441f, 0.156f)
        }
    }
}
