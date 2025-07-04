package codes.chrishorner.reverserainbow.ui.util

import androidx.compose.runtime.LongState
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.snapshots.StateFactoryMarker
import androidx.compose.ui.unit.IntOffset
import kotlin.reflect.KProperty

/**
 * Avoid boxing `IntOffset` by turning it into a `LongState`.
 */
@StateFactoryMarker
fun mutableLongStateFrom(value: IntOffset): MutableLongState {
  return mutableLongStateOf(value.packedValue)
}

/**
 * Convenience for pulling out an `IntOffset` from a `LongState` as a packed value.
 */
@Suppress("NOTHING_TO_INLINE", "unused")
inline operator fun LongState.getValue(thisObj: Any?, property: KProperty<*>): IntOffset {
  return IntOffset(longValue)
}

@Suppress("NOTHING_TO_INLINE", "unused")
inline operator fun MutableLongState.setValue(thisObj: Any?, property: KProperty<*>, value: IntOffset) {
  longValue = value.packedValue
}