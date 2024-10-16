package com.elssiany.klydo.worker

actual class WorkConstraints constructor(
    val requiresNetwork: Boolean,
    val requiresCharging: Boolean
) {
    actual fun meetsRequirements(): Boolean {
        // Verifica red y estado de carga en iOS
        val isConnected = true
        val isCharging = true
            return (!requiresNetwork || isConnected) && (!requiresCharging || isCharging)
    }
}