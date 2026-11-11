package upv.pm.may_ago_26.iti_271503.pgu2_eq04

/**
 * Created by Schoen and Jonathan on 4/16/2016.
 */
object Time {
    var startTime: Long = 0
    var lastFrame: Long = 0
    var deltaTime: Long = 0
    var frameCount: Int = 0

    fun start() {
        frameCount = 0
        startTime = System.currentTimeMillis()
        lastFrame = startTime
    }

    fun update() {
        val current = System.currentTimeMillis()
        frameCount++
        deltaTime = current - lastFrame
        lastFrame = current
    }

    val time: Float
        get() = (System.currentTimeMillis() - startTime).toFloat() / 1000

    fun getDeltaTime(): Float {
        return deltaTime * 0.001f
    }
}
