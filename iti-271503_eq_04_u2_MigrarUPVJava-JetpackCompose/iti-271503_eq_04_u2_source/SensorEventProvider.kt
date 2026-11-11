package upv.pm.may_ago_26.iti_271503.pgu2_eq04

import android.hardware.SensorEventListener

interface SensorEventProvider {
    fun start()

    fun stop()

    fun registerListener(p0: SensorEventListener?)

    fun unregisterListener(p0: SensorEventListener?)
}
