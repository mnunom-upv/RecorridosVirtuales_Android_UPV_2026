package upv.pm.may_ago_26.iti_271503.pgu2_eq04

class Earth(
    distance: Float,
    radius: Float,
    rotation: Float,
    orbit: Float,
    texId: Int,
    nightTexId: Int,
    origin: Transform?
) : Planet(distance, radius, rotation, orbit, origin) {

    private var wobble: Transform = Transform()

    init {
        wobble = Transform()
            .setLocalPosition(distance, 0f, 0f)
            .setParent(orbitTransform, false)

        val tilt = Transform()
            .setLocalRotation(-23.4f, 0f, 0f)
            .setParent(wobble, false)

        transform
            ?.setParent(tilt, false)
            ?.setLocalPosition(0f, 0f, 0f)
            ?.addComponent(Sphere(texId, nightTexId))
    }

    override fun preDraw(dt: Float) {
        orbitTransform?.rotate(0f, dt * orbit, 0f)
        wobble.rotate(0f, dt * 5f, 0f)
        transform?.rotate(0f, dt * -rotation, 0f)
    }
}