package com.androidforge.streakwise.core.network

import kotlinx.coroutines.flow.Flow

enum class NetworkStatus {
    Available,
    Unavailable,
    Losing,
    Lost
}

/**
 * Interface for tracking the current network connectivity status.
 * This allows for decoupling network status observation from Android-specific APIs.
 */
interface NetworkStatusTracker {
    val networkStatus: Flow<NetworkStatus>
}