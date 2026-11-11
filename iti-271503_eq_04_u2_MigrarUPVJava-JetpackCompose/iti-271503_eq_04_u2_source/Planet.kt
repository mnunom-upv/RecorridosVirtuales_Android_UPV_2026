package upv.pm.may_ago_26.iti_271503.pgu2_eq04

open class Planet {

    protected var rotation: Float = 0f
    protected var orbit: Float = 0f

    @JvmField
    protected var orbitTransform: Transform? = null

    @JvmField
    protected var transform: Transform? = null

    var distance: Float = 0f
    var radius: Float = 0f

    constructor(
        distance: Float,
        radius: Float,
        rotation: Float,
        orbit: Float,
        texId: Int,
        origin: Transform?
    ) {
        setupPlanet(distance, radius, rotation, orbit, origin)
        transform!!.addComponent(Sphere(texId))
    }

    constructor(
        distance: Float,
        radius: Float,
        rotation: Float,
        orbit: Float,
        origin: Transform?
    ) {
        setupPlanet(distance, radius, rotation, orbit, origin)
    }

    fun setupPlanet(
        distance: Float,
        radius: Float,
        rotation: Float,
        orbit: Float,
        origin: Transform?
    ) {
        this.distance = distance
        this.radius = radius
        this.rotation = rotation
        this.orbit = orbit

        orbitTransform = Transform()
        orbitTransform!!.setParent(origin, false)

        transform = Transform()
            .setParent(orbitTransform, false)
            .setLocalPosition(distance, 0f, 0f)
            .setLocalRotation(180f, 0f, 0f)
            .setLocalScale(radius, radius, radius)
    }

    open fun preDraw(dt: Float) {
        orbitTransform!!.rotate(0f, dt * orbit, 0f)
        transform!!.rotate(0f, dt * -rotation, 0f)
    }

    fun getTransform(): Transform {
        return transform!!
    }

    val orbitransform: Transform
        get() = orbitTransform!!
}