package upv.pm.may_ago_26.iti_271503.pgu2_eq04

/**
 * Extensiones seguras para llamar al renderer original desde Kotlin.
 *
 * Sirven para que los nombres usados en Compose sean más claros sin tocar CubeRenderer.java.
 */
fun CubeRenderer.walkForward(distance: Float) {
    MoveForward(distance)
}

fun CubeRenderer.walkBackward(distance: Float) {
    MoveForward(-distance)
}

fun CubeRenderer.walkLeft(distance: Float) {
    StrafeRight(-distance)
}

fun CubeRenderer.walkRight(distance: Float) {
    StrafeRight(distance)
}

fun CubeRenderer.turnLeft(angle: Float) {
    RotateY(-angle)
}

fun CubeRenderer.turnRight(angle: Float) {
    RotateY(angle)
}

fun CubeRenderer.goToNextSavedPosition() {
    NextPosicion()
}

fun CubeRenderer.goToStartPosition() {
    ReiniciarPosiciones()
}
