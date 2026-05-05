package com.androidforge.streakwise.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.androidforge.streakwise.core.network.NetworkStatus
import com.androidforge.streakwise.core.network.NetworkStatusTracker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectivityManagerNetworkStatusTracker @Inject constructor(
    @ApplicationContext private val context: Context
) : NetworkStatusTracker {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override val networkStatus: Flow<NetworkStatus> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            private val networks = mutableSetOf<Network>()

            override fun onAvailable(network: Network) {
                Timber.d("Network available: $network")
                networks.add(network)
                trySend(NetworkStatus.Available)
            }

            override fun onLost(network: Network) {
                Timber.d("Network lost: $network")
                networks.remove(network)
                if (networks.isEmpty()) {
                    trySend(NetworkStatus.Lost)
                }
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                Timber.d("Network losing: $network")
                trySend(NetworkStatus.Losing)
            }

            override fun onUnavailable() {
                Timber.d("Network unavailable")
                networks.clear()
                trySend(NetworkStatus.Unavailable)
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                Timber.d("Network capabilities changed for $network: $networkCapabilities")
                if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                    if (!networks.contains(network)) {
                        networks.add(network)
                    }
                    trySend(NetworkStatus.Available)
                } else {
                    networks.remove(network)
                    if (networks.isEmpty()) {
                        trySend(NetworkStatus.Unavailable)
                    }
                }
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        // Emit initial state
        val initialStatus = getCurrentNetworkStatus()
        Timber.d("Initial network status: $initialStatus")
        trySend(initialStatus)

        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }
        .distinctUntilChanged()

    private fun getCurrentNetworkStatus(): NetworkStatus {
        val activeNetwork = connectivityManager.activeNetwork
        if (activeNetwork == null) {
            return NetworkStatus.Lost
        }

        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return if (capabilities != null &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        ) {
            NetworkStatus.Available
        } else {
            NetworkStatus.Unavailable
        }
    }
}