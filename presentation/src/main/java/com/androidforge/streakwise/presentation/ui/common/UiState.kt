package com.androidforge.streakwise.presentation.ui.common

/**
 * A sealed class representing the various states of a UI screen.
 * This helps in managing UI logic robustly by ensuring all possible states are handled.
 * @param T The type of data held in the [Success] state.
 */
sealed class UiState<out T> {
    /**
     * Represents an initial or loading state, typically for displaying loading indicators.
     */
    data object Loading : UiState<Nothing>()

    /**
     * Represents a state where data has been successfully loaded and is ready to be displayed.
     * @param data The data to be displayed.
     */
    data class Success<T>(val data: T) : UiState<T>()

    /**
     * Represents a state where an error occurred during data loading or processing.
     * @param message A user-friendly error message.
     * @param throwable An optional [Throwable] for logging or detailed error information.
     */
    data class Error(val message: String, val throwable: Throwable? = null) : UiState<Nothing>()

    /**
     * Represents a state where there is no data to display, often used for empty lists or search results.
     * @param message An optional message to display when the state is empty.
     */
    data class Empty(val message: String? = null) : UiState<Nothing>()

    /**
     * Represents a state where the application is offline and data might be unavailable or stale.
     * @param message An optional message to display when offline.
     */
    data class Offline(val message: String? = null) : UiState<Nothing>()
}