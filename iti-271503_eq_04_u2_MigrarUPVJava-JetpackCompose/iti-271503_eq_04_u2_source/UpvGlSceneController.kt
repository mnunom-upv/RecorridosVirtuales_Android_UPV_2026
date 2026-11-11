package upv.pm.may_ago_26.iti_271503.pgu2_eq04

/**
 * Controlador ligero para comunicar botones Compose con el renderer OpenGL.
 *
 * Las acciones se ejecutan dentro del hilo GL con GLSurfaceView.queueEvent().
 */
class UpvGlSceneController {

    private var queueAction: (((CubeRenderer) -> Unit) -> Unit)? = null

    fun bind(queueAction: ((CubeRenderer) -> Unit) -> Unit) {
        this.queueAction = queueAction
    }

    fun moveForward(step: Float = 10f) {
        queueAction?.invoke { renderer ->
            renderer.MoveForward(step)
        }
    }

    fun moveBackward(step: Float = 10f) {
        queueAction?.invoke { renderer ->
            renderer.MoveForward(-step)
        }
    }

    fun strafeLeft(step: Float = 10f) {
        queueAction?.invoke { renderer ->
            renderer.StrafeRight(-step)
        }
    }

    fun strafeRight(step: Float = 10f) {
        queueAction?.invoke { renderer ->
            renderer.StrafeRight(step)
        }
    }

    fun rotateLeft(angle: Float = 5f) {
        queueAction?.invoke { renderer ->
            renderer.RotateY(-angle)
        }
    }

    fun rotateRight(angle: Float = 5f) {
        queueAction?.invoke { renderer ->
            renderer.RotateY(angle)
        }
    }

    fun nextLocation() {
        queueAction?.invoke { renderer ->
            renderer.NextPosicion()
        }
    }

    fun resetLocation() {
        queueAction?.invoke { renderer ->
            renderer.ReiniciarPosiciones()
        }
    }
}
