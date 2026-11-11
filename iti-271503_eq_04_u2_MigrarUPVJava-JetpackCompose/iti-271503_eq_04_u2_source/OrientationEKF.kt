package upv.pm.may_ago_26.iti_271503.pgu2_eq04

import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

class OrientationEKF {
    private val rotationMatrix: DoubleArray
    private val so3SensorFromWorld: Matrix3x3d
    private val so3LastMotion: Matrix3x3d
    private val mP: Matrix3x3d
    private val mQ: Matrix3x3d
    private val mR: Matrix3x3d
    private val mRaccel: Matrix3x3d
    private val mS: Matrix3x3d
    private val mH: Matrix3x3d
    private val mK: Matrix3x3d
    private val mNu: Vector3d
    private val mz: Vector3d
    private val mh: Vector3d
    private val mu: Vector3d
    private val mx: Vector3d
    private val down: Vector3d
    private val north: Vector3d
    private var sensorTimeStampGyro: Long = 0
    private val lastGyro: Vector3d
    private var previousAccelNorm: Double
    private var movingAverageAccelNormChange: Double
    private var filteredGyroTimestep = 0f
    private var timestepFilterInit: Boolean
    private var numGyroTimestepSamples = 0
    private var gyroFilterValid: Boolean
    private val getPredictedGLMatrixTempM1: Matrix3x3d
    private val getPredictedGLMatrixTempM2: Matrix3x3d
    private val getPredictedGLMatrixTempV1: Vector3d
    private val setHeadingDegreesTempM1: Matrix3x3d
    private val processGyroTempM1: Matrix3x3d
    private val processGyroTempM2: Matrix3x3d
    private val processAccTempM1: Matrix3x3d
    private val processAccTempM2: Matrix3x3d
    private val processAccTempM3: Matrix3x3d
    private val processAccTempM4: Matrix3x3d
    private val processAccTempM5: Matrix3x3d
    private val processAccTempV1: Vector3d
    private val processAccTempV2: Vector3d
    private val processAccVDelta: Vector3d
    private val processMagTempV1: Vector3d
    private val processMagTempV2: Vector3d
    private val processMagTempV3: Vector3d
    private val processMagTempV4: Vector3d
    private val processMagTempV5: Vector3d
    private val processMagTempM1: Matrix3x3d
    private val processMagTempM2: Matrix3x3d
    private val processMagTempM4: Matrix3x3d
    private val processMagTempM5: Matrix3x3d
    private val processMagTempM6: Matrix3x3d
    private val updateCovariancesAfterMotionTempM1: Matrix3x3d
    private val updateCovariancesAfterMotionTempM2: Matrix3x3d
    private val accObservationFunctionForNumericalJacobianTempM: Matrix3x3d
    private val magObservationFunctionForNumericalJacobianTempM: Matrix3x3d
    var isReady: Boolean = false
        private set

    var isAlignedToNorth: Boolean = false
        private set

    init {
        this.rotationMatrix = DoubleArray(16)
        this.so3SensorFromWorld = Matrix3x3d()
        this.so3LastMotion = Matrix3x3d()
        this.mP = Matrix3x3d()
        this.mQ = Matrix3x3d()
        this.mR = Matrix3x3d()
        this.mRaccel = Matrix3x3d()
        this.mS = Matrix3x3d()
        this.mH = Matrix3x3d()
        this.mK = Matrix3x3d()
        this.mNu = Vector3d()
        this.mz = Vector3d()
        this.mh = Vector3d()
        this.mu = Vector3d()
        this.mx = Vector3d()
        this.down = Vector3d()
        this.north = Vector3d()
        this.lastGyro = Vector3d()
        this.previousAccelNorm = 0.0
        this.movingAverageAccelNormChange = 0.0
        this.timestepFilterInit = false
        this.gyroFilterValid = true
        this.getPredictedGLMatrixTempM1 = Matrix3x3d()
        this.getPredictedGLMatrixTempM2 = Matrix3x3d()
        this.getPredictedGLMatrixTempV1 = Vector3d()
        this.setHeadingDegreesTempM1 = Matrix3x3d()
        this.processGyroTempM1 = Matrix3x3d()
        this.processGyroTempM2 = Matrix3x3d()
        this.processAccTempM1 = Matrix3x3d()
        this.processAccTempM2 = Matrix3x3d()
        this.processAccTempM3 = Matrix3x3d()
        this.processAccTempM4 = Matrix3x3d()
        this.processAccTempM5 = Matrix3x3d()
        this.processAccTempV1 = Vector3d()
        this.processAccTempV2 = Vector3d()
        this.processAccVDelta = Vector3d()
        this.processMagTempV1 = Vector3d()
        this.processMagTempV2 = Vector3d()
        this.processMagTempV3 = Vector3d()
        this.processMagTempV4 = Vector3d()
        this.processMagTempV5 = Vector3d()
        this.processMagTempM1 = Matrix3x3d()
        this.processMagTempM2 = Matrix3x3d()
        this.processMagTempM4 = Matrix3x3d()
        this.processMagTempM5 = Matrix3x3d()
        this.processMagTempM6 = Matrix3x3d()
        this.updateCovariancesAfterMotionTempM1 = Matrix3x3d()
        this.updateCovariancesAfterMotionTempM2 = Matrix3x3d()
        this.accObservationFunctionForNumericalJacobianTempM = Matrix3x3d()
        this.magObservationFunctionForNumericalJacobianTempM = Matrix3x3d()
        this.reset()
    }

    fun reset() {
        this.sensorTimeStampGyro = 0L
        this.so3SensorFromWorld.setIdentity()
        this.so3LastMotion.setIdentity()
        val initialSigmaP = 5.0
        this.mP.setZero()
        this.mP.setSameDiagonal(25.0)
        val initialSigmaQ = 1.0
        this.mQ.setZero()
        this.mQ.setSameDiagonal(1.0)
        val initialSigmaR = 0.25
        this.mR.setZero()
        this.mR.setSameDiagonal(0.0625)
        this.mRaccel.setZero()
        this.mRaccel.setSameDiagonal(0.5625)
        this.mS.setZero()
        this.mH.setZero()
        this.mK.setZero()
        this.mNu.setZero()
        this.mz.setZero()
        this.mh.setZero()
        this.mu.setZero()
        this.mx.setZero()
        this.down.set(0.0, 0.0, 9.81)
        this.north.set(0.0, 1.0, 0.0)
        this.isReady = false
        this.isAlignedToNorth = false
    }

    @set:Synchronized
    var headingDegrees: Double
        get() {
            val x = this.so3SensorFromWorld.get(2, 0)
            val y = this.so3SensorFromWorld.get(2, 1)
            val mag = sqrt(x * x + y * y)
            if (mag < 0.1) {
                return 0.0
            }
            var heading = -90.0 - atan2(y, x) / 3.141592653589793 * 180.0
            if (heading < 0.0) {
                heading += 360.0
            }
            if (heading >= 360.0) {
                heading -= 360.0
            }
            return heading
        }
        set(heading) {
            val currentHeading = this.headingDegrees
            val deltaHeading = heading - currentHeading
            val s = sin(deltaHeading / 180.0 * 3.141592653589793)
            val c = cos(deltaHeading / 180.0 * 3.141592653589793)
            val deltaHeadingRotationVals =
                arrayOf<DoubleArray?>(
                    doubleArrayOf(c, -s, 0.0),
                    doubleArrayOf(s, c, 0.0),
                    doubleArrayOf(0.0, 0.0, 1.0)
                )
            arrayAssign(
                deltaHeadingRotationVals,
                this.setHeadingDegreesTempM1
            )
            Matrix3x3d.Companion.mult(
                this.so3SensorFromWorld,
                this.setHeadingDegreesTempM1,
                this.so3SensorFromWorld
            )
        }

    val gLMatrix: DoubleArray
        get() = this.glMatrixFromSo3(this.so3SensorFromWorld)

    fun getPredictedGLMatrix(secondsAfterLastGyroEvent: Double): DoubleArray {
        val pmu = this.getPredictedGLMatrixTempV1
        pmu.set(this.lastGyro)
        pmu.scale(-secondsAfterLastGyroEvent)
        val so3PredictedMotion = this.getPredictedGLMatrixTempM1
        So3Util.sO3FromMu(pmu, so3PredictedMotion)
        val so3PredictedState = this.getPredictedGLMatrixTempM2
        Matrix3x3d.Companion.mult(so3PredictedMotion, this.so3SensorFromWorld, so3PredictedState)
        return this.glMatrixFromSo3(so3PredictedState)
    }

    fun getRotationMatrix(): Matrix3x3d {
        return this.so3SensorFromWorld
    }

    @Synchronized
    fun processGyro(gyro: Vector3d, sensorTimeStamp: Long) {
        val kTimeThreshold = 0.04f
        val kdTDefault = 0.01f
        if (this.sensorTimeStampGyro != 0L) {
            var dT = (sensorTimeStamp - this.sensorTimeStampGyro) * 1.0E-9f
            if (dT > kTimeThreshold) {
                dT = (if (this.gyroFilterValid) this.filteredGyroTimestep else kdTDefault)
            } else {
                this.filterGyroTimestep(dT)
            }
            this.mu.set(gyro)
            this.mu.scale(-dT.toDouble())
            So3Util.sO3FromMu(this.mu, this.so3LastMotion)
            Matrix3x3d.Companion.mult(
                this.so3LastMotion,
                this.so3SensorFromWorld,
                this.so3SensorFromWorld
            )
            this.updateCovariancesAfterMotion()
            this.processGyroTempM2.set(this.mQ)
            this.processGyroTempM2.scale((dT * dT).toDouble())
            this.mP.plusEquals(this.processGyroTempM2)
        }
        this.sensorTimeStampGyro = sensorTimeStamp
        this.lastGyro.set(gyro)
    }

    private fun updateAccelCovariance(currentAccelNorm: Double) {
        val currentAccelNormChange = abs(currentAccelNorm - this.previousAccelNorm)
        this.previousAccelNorm = currentAccelNorm
        val kSmoothingFactor = 0.5
        this.movingAverageAccelNormChange = (kSmoothingFactor * currentAccelNormChange
                + kSmoothingFactor * this.movingAverageAccelNormChange)
        val kMaxAccelNormChange = 0.15
        val kMinAccelNoiseSigma = 0.75
        val kMaxAccelNoiseSigma = 7.0
        val normChangeRatio = this.movingAverageAccelNormChange / kMaxAccelNormChange
        val accelNoiseSigma = min(
            kMaxAccelNoiseSigma,
            kMinAccelNoiseSigma + normChangeRatio * (kMaxAccelNoiseSigma - kMinAccelNoiseSigma)
        )
        this.mRaccel.setSameDiagonal(accelNoiseSigma * accelNoiseSigma)
    }

    @Synchronized
    fun processAcc(acc: Vector3d, sensorTimeStamp: Long) {
        this.mz.set(acc)
        this.updateAccelCovariance(this.mz.length())
        if (this.isReady) {
            this.accObservationFunctionForNumericalJacobian(this.so3SensorFromWorld, this.mNu)
            val eps = 1.0E-7
            for (dof in 0..2) {
                val delta = this.processAccVDelta
                delta.setZero()
                delta.setComponent(dof, eps)
                So3Util.sO3FromMu(delta, this.processAccTempM1)
                Matrix3x3d.Companion.mult(
                    this.processAccTempM1,
                    this.so3SensorFromWorld,
                    this.processAccTempM2
                )
                this.accObservationFunctionForNumericalJacobian(
                    this.processAccTempM2,
                    this.processAccTempV1
                )
                val withDelta = this.processAccTempV1
                Vector3d.Companion.sub(this.mNu, withDelta, this.processAccTempV2)
                this.processAccTempV2.scale(1.0 / eps)
                this.mH.setColumn(dof, this.processAccTempV2)
            }
            this.mH.transpose(this.processAccTempM3)
            Matrix3x3d.Companion.mult(this.mP, this.processAccTempM3, this.processAccTempM4)
            Matrix3x3d.Companion.mult(this.mH, this.processAccTempM4, this.processAccTempM5)
            Matrix3x3d.Companion.add(this.processAccTempM5, this.mRaccel, this.mS)
            this.mS.invert(this.processAccTempM3)
            this.mH.transpose(this.processAccTempM4)
            Matrix3x3d.Companion.mult(
                this.processAccTempM4,
                this.processAccTempM3,
                this.processAccTempM5
            )
            Matrix3x3d.Companion.mult(this.mP, this.processAccTempM5, this.mK)
            Matrix3x3d.Companion.mult(this.mK, this.mNu, this.mx)
            Matrix3x3d.Companion.mult(this.mK, this.mH, this.processAccTempM3)
            this.processAccTempM4.setIdentity()
            this.processAccTempM4.minusEquals(this.processAccTempM3)
            Matrix3x3d.Companion.mult(this.processAccTempM4, this.mP, this.processAccTempM3)
            this.mP.set(this.processAccTempM3)
            So3Util.sO3FromMu(this.mx, this.so3LastMotion)
            Matrix3x3d.Companion.mult(
                this.so3LastMotion,
                this.so3SensorFromWorld,
                this.so3SensorFromWorld
            )
            this.updateCovariancesAfterMotion()
        } else {
            So3Util.sO3FromTwoVec(this.down, this.mz, this.so3SensorFromWorld)
            this.isReady = true
        }
    }

    @Synchronized
    fun processMag(mag: FloatArray, sensorTimeStamp: Long) {
        if (!this.isReady) {
            return
        }
        this.mz.set(mag[0].toDouble(), mag[1].toDouble(), mag[2].toDouble())
        this.mz.normalize()
        val downInSensorFrame = Vector3d()
        this.so3SensorFromWorld.getColumn(2, downInSensorFrame)
        Vector3d.Companion.cross(this.mz, downInSensorFrame, this.processMagTempV1)
        val perpToDownAndMag = this.processMagTempV1
        perpToDownAndMag.normalize()
        Vector3d.Companion.cross(downInSensorFrame, perpToDownAndMag, this.processMagTempV2)
        val magHorizontal = this.processMagTempV2
        magHorizontal.normalize()
        this.mz.set(magHorizontal)
        if (this.isAlignedToNorth) {
            this.magObservationFunctionForNumericalJacobian(this.so3SensorFromWorld, this.mNu)
            val eps = 1.0E-7
            for (dof in 0..2) {
                val delta = this.processMagTempV3
                delta.setZero()
                delta.setComponent(dof, eps)
                So3Util.sO3FromMu(delta, this.processMagTempM1)
                Matrix3x3d.Companion.mult(
                    this.processMagTempM1,
                    this.so3SensorFromWorld,
                    this.processMagTempM2
                )
                this.magObservationFunctionForNumericalJacobian(
                    this.processMagTempM2,
                    this.processMagTempV4
                )
                val withDelta = this.processMagTempV4
                Vector3d.Companion.sub(this.mNu, withDelta, this.processMagTempV5)
                this.processMagTempV5.scale(1.0 / eps)
                this.mH.setColumn(dof, this.processMagTempV5)
            }
            this.mH.transpose(this.processMagTempM4)
            Matrix3x3d.Companion.mult(this.mP, this.processMagTempM4, this.processMagTempM5)
            Matrix3x3d.Companion.mult(this.mH, this.processMagTempM5, this.processMagTempM6)
            Matrix3x3d.Companion.add(this.processMagTempM6, this.mR, this.mS)
            this.mS.invert(this.processMagTempM4)
            this.mH.transpose(this.processMagTempM5)
            Matrix3x3d.Companion.mult(
                this.processMagTempM5,
                this.processMagTempM4,
                this.processMagTempM6
            )
            Matrix3x3d.Companion.mult(this.mP, this.processMagTempM6, this.mK)
            Matrix3x3d.Companion.mult(this.mK, this.mNu, this.mx)
            Matrix3x3d.Companion.mult(this.mK, this.mH, this.processMagTempM4)
            this.processMagTempM5.setIdentity()
            this.processMagTempM5.minusEquals(this.processMagTempM4)
            Matrix3x3d.Companion.mult(this.processMagTempM5, this.mP, this.processMagTempM4)
            this.mP.set(this.processMagTempM4)
            So3Util.sO3FromMu(this.mx, this.so3LastMotion)
            Matrix3x3d.Companion.mult(
                this.so3LastMotion,
                this.so3SensorFromWorld,
                this.processMagTempM4
            )
            this.so3SensorFromWorld.set(this.processMagTempM4)
            this.updateCovariancesAfterMotion()
        } else {
            this.magObservationFunctionForNumericalJacobian(this.so3SensorFromWorld, this.mNu)
            So3Util.sO3FromMu(this.mNu, this.so3LastMotion)
            Matrix3x3d.Companion.mult(
                this.so3LastMotion,
                this.so3SensorFromWorld,
                this.processMagTempM4
            )
            this.so3SensorFromWorld.set(this.processMagTempM4)
            this.updateCovariancesAfterMotion()
            this.isAlignedToNorth = true
        }
    }

    private fun glMatrixFromSo3(so3: Matrix3x3d): DoubleArray {
        for (r in 0..2) {
            for (c in 0..2) {
                this.rotationMatrix[4 * c + r] = so3.get(r, c)
            }
        }

        this.rotationMatrix[3] = 0.0
        this.rotationMatrix[7] = 0.0
        this.rotationMatrix[11] = 0.0

        this.rotationMatrix[12] = 0.0
        this.rotationMatrix[13] = 0.0
        this.rotationMatrix[14] = 0.0
        this.rotationMatrix[15] = 1.0
        return this.rotationMatrix
    }

    private fun filterGyroTimestep(timeStep: Float) {
        val kFilterCoeff = 0.95f
        val kMinSamples = 10
        if (!this.timestepFilterInit) {
            this.filteredGyroTimestep = timeStep
            this.numGyroTimestepSamples = 1
            this.timestepFilterInit = true
        } else {
            this.filteredGyroTimestep =
                kFilterCoeff * this.filteredGyroTimestep + (1 - kFilterCoeff) * timeStep
            if (++this.numGyroTimestepSamples > kMinSamples) {
                this.gyroFilterValid = true
            }
        }
    }

    private fun updateCovariancesAfterMotion() {
        this.so3LastMotion.transpose(this.updateCovariancesAfterMotionTempM1)
        Matrix3x3d.Companion.mult(
            this.mP,
            this.updateCovariancesAfterMotionTempM1,
            this.updateCovariancesAfterMotionTempM2
        )
        Matrix3x3d.Companion.mult(
            this.so3LastMotion,
            this.updateCovariancesAfterMotionTempM2,
            this.mP
        )
        this.so3LastMotion.setIdentity()
    }

    private fun accObservationFunctionForNumericalJacobian(
        so3SensorFromWorldPred: Matrix3x3d,
        result: Vector3d
    ) {
        Matrix3x3d.Companion.mult(so3SensorFromWorldPred, this.down, this.mh)
        So3Util.sO3FromTwoVec(
            this.mh,
            this.mz,
            this.accObservationFunctionForNumericalJacobianTempM
        )
        So3Util.muFromSO3(this.accObservationFunctionForNumericalJacobianTempM, result)
    }

    private fun magObservationFunctionForNumericalJacobian(
        so3SensorFromWorldPred: Matrix3x3d,
        result: Vector3d
    ) {
        Matrix3x3d.Companion.mult(so3SensorFromWorldPred, this.north, this.mh)
        So3Util.sO3FromTwoVec(
            this.mh,
            this.mz,
            this.magObservationFunctionForNumericalJacobianTempM
        )
        So3Util.muFromSO3(this.magObservationFunctionForNumericalJacobianTempM, result)
    }

    companion object {
        private const val TAG = "OrientationEKF"

        private const val NS2S = 1.0E-9f
        private const val MIN_ACCEL_NOISE_SIGMA = 0.75
        private const val MAX_ACCEL_NOISE_SIGMA = 7.0
        fun arrayAssign(data: Array<DoubleArray?>, m: Matrix3x3d) {
            assert(3 == data.size)
            assert(3 == data[0]!!.size)
            assert(3 == data[1]!!.size)
            assert(3 == data[2]!!.size)
            m.set(
                data[0]!![0],
                data[0]!![1],
                data[0]!![2],
                data[1]!![0],
                data[1]!![1],
                data[1]!![2],
                data[2]!![0],
                data[2]!![1],
                data[2]!![2]
            )
        }
    }
}
