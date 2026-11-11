package upv.pm.may_ago_26.iti_271503.pgu2_eq04

import kotlin.jvm.JvmName

class Transform {

    var components: MutableList<Component?> = ArrayList()

    @get:JvmName("getLocalPositionValue")
    @set:JvmName("setLocalPositionValue")
    var localPosition: Vector3 = Vector3(0f, 0f, 0f)
        private set

    @get:JvmName("getLocalRotationValue")
    @set:JvmName("setLocalRotationValue")
    var localRotation: Quaternion = Quaternion()
        private set

    @get:JvmName("getLocalScaleValue")
    @set:JvmName("setLocalScaleValue")
    var localScale: Vector3 = Vector3(1f, 1f, 1f)
        private set

    private var parent: Transform? = null

    fun addComponent(component: Component): Transform {
        component.setTransform(this)
        components.add(component)
        return this
    }

    fun setParent(parent: Transform?): Transform {
        setParent(parent, true)
        return this
    }

    fun setParent(parent: Transform?, updatePosition: Boolean): Transform {
        if (this.parent === parent) {
            return this
        }

        if (parent == null) {
            unParent(updatePosition)
            return this
        }

        if (updatePosition) {
            val tmpPosition = position
            this.parent = parent
            setPosition(tmpPosition)
        } else {
            this.parent = parent
        }

        return this
    }

    fun upParent(): Transform {
        unParent(true)
        return this
    }

    fun unParent(updatePosition: Boolean): Transform {
        if (parent == null) {
            return this
        }

        if (updatePosition) {
            localPosition = position
        }

        parent = null
        return this
    }

    fun setPosition(x: Float, y: Float, z: Float): Transform {
        localPosition = if (parent != null) {
            Vector3(x, y, z).subtract(parent!!.position)
        } else {
            Vector3(x, y, z)
        }

        return this
    }

    fun setPosition(position: Vector3): Transform {
        localPosition = if (parent != null) {
            Vector3(position).subtract(parent!!.position)
        } else {
            position
        }

        return this
    }

    val position: Vector3
        get() {
            return if (parent != null) {
                Matrix4.TRS(
                    parent!!.position,
                    parent!!.rotation,
                    parent!!.scale
                ).multiplyPoint3x4(localPosition)
            } else {
                localPosition
            }
        }

    fun setLocalPosition(x: Float, y: Float, z: Float): Transform {
        localPosition = Vector3(x, y, z)
        return this
    }

    fun setLocalPosition(position: Vector3): Transform {
        localPosition = position
        return this
    }

    fun setRotation(
        pitch: Float,
        yaw: Float,
        roll: Float
    ): Transform {
        localRotation = if (parent != null) {
            parent!!.rotation.conjugate().multiply(
                Quaternion().setEulerAngles(pitch, yaw, roll)
            )
        } else {
            Quaternion().setEulerAngles(pitch, yaw, roll)
        }

        return this
    }

    fun setRotation(rotation: Quaternion): Transform {
        localRotation = if (parent != null) {
            Quaternion(parent!!.rotation)
                .multiply(rotation.conjugate())
                .conjugate()
        } else {
            rotation
        }

        return this
    }

    val rotation: Quaternion
        get() {
            return if (parent != null) {
                parent!!.rotation.multiply(localRotation)
            } else {
                localRotation
            }
        }

    fun setLocalRotation(
        pitch: Float,
        yaw: Float,
        roll: Float
    ): Transform {
        localRotation = Quaternion().setEulerAngles(pitch, yaw, roll)
        return this
    }

    fun setLocalRotation(rotation: Quaternion): Transform {
        localRotation = rotation
        return this
    }

    fun rotate(
        pitch: Float,
        yaw: Float,
        roll: Float
    ): Transform {
        localRotation.multiply(
            Quaternion().setEulerAngles(pitch, yaw, roll)
        )

        return this
    }

    val scale: Vector3
        get() {
            return if (parent != null) {
                parent!!.scale.multiply(localScale)
            } else {
                localScale
            }
        }

    fun setLocalScale(x: Float, y: Float, z: Float): Transform {
        localScale = Vector3(x, y, z)
        return this
    }

    fun setLocalScale(scale: Vector3): Transform {
        localScale = scale
        return this
    }

    fun scale(x: Float, y: Float, z: Float): Transform {
        localScale.scale(x, y, z)
        return this
    }

    fun toFloatMatrix(): FloatArray {
        return Matrix4.TRS(
            position,
            rotation,
            scale
        ).`val`
    }

    fun toLightMatrix(): FloatArray {
        return Matrix4.TR(
            position,
            rotation
        ).`val`
    }

    fun drawMatrices() {
        var modelMatrix = Matrix4.TR(
            position,
            rotation
        )

        RenderObject.lightingModel = modelMatrix.`val`

        modelMatrix = Matrix4(modelMatrix)

        RenderObject.model = modelMatrix
            .scale(scale)
            .`val`
    }

    companion object {
        private const val TAG = "RenderBox.Transform"
    }
}