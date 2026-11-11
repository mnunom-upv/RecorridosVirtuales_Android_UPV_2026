package upv.pm.may_ago_26.iti_271503.pgu2_eq04

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable

/**
 * Representa un punto importante del recorrido UPV.
 *
 * La posición coincide con las coordenadas usadas por CubeRenderer.java.
 */
@Immutable
data class UpvDestination(
    val id: String,
    val title: String,
    val description: String,
    @DrawableRes val imageRes: Int,
    val position: FloatArray,
    val direction: FloatArray = floatArrayOf(0f, 0f, -1f)
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UpvDestination) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
