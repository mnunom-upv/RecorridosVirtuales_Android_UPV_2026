package upv.pm.may_ago_26.iti_271503.pgu2_eq06

class HeadMountedDisplay {
    private var mScreen: ScreenParams
    private var mCardboardDevice: CardboardDeviceParams

    constructor(screenParams: ScreenParams, cardboardDevice: CardboardDeviceParams) : super() {
        this.mScreen = screenParams
        this.mCardboardDevice = cardboardDevice
    }

    constructor(hmd: HeadMountedDisplay) : super() {
        this.mScreen = ScreenParams(hmd.mScreen)
        this.mCardboardDevice = CardboardDeviceParams(hmd.mCardboardDevice)
    }

    var screenParams: ScreenParams
        get() = this.mScreen
        set(screen) {
            this.mScreen = ScreenParams(screen)
        }

    var cardboardDeviceParams: CardboardDeviceParams?
        get() = this.mCardboardDevice
        set(value) {
            this.mCardboardDevice = CardboardDeviceParams(value!!)
        }

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        if (other === this) {
            return true
        }
        if (other !is HeadMountedDisplay) {
            return false
        }
        val o = other
        return this.mScreen == o.mScreen && this.mCardboardDevice == o.mCardboardDevice
    }
}
