package upv.pm.may_ago_26.iti_271503.pgu2_eq04

open class Component {

    @JvmField
    var transform: Transform? = null

    @JvmField
    var enabled: Boolean = true

    fun setTransform(transform: Transform?): Component {
        this.transform = transform
        return this
    }
}