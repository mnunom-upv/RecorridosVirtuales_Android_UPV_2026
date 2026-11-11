package upv.pm.may_ago_26.iti_271503.pgu2_eq06

class SystemClock : Clock {
    override fun nanoTime(): Long {
        return System.nanoTime()
    }
}
