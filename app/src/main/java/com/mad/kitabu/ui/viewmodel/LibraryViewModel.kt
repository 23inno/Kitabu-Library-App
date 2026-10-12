
package com.mad.kitabu.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mad.kitabu.data.local.BookEntity
import com.mad.kitabu.data.repository.LibraryRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val repository: LibraryRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> =
        _searchQuery.asStateFlow()

    private val _availableOnly = MutableStateFlow(false)
    val availableOnly: StateFlow<Boolean> =
        _availableOnly.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> =
        _message.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()

    private val _isInitializing = MutableStateFlow(true)
    val isInitializing: StateFlow<Boolean> =
        _isInitializing.asStateFlow()

    private val _databaseReady = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val books: StateFlow<List<BookEntity>> =
        combine(
            _searchQuery,
            _availableOnly,
            _databaseReady
        ) { query, availableOnly, ready ->
            Triple(query.trim(), availableOnly, ready)
        }
            .filter { (_, _, ready) -> ready }
            .flatMapLatest { (query, availableOnly, _) ->

                // Search is performed by Room using SQL LIKE.
                repository.searchBooks(query)
                    .onEach {
                        // This changes only after Room emits data.
                        _isInitializing.value = false
                    }
                    .combine(_availableOnly) { results, filter ->

                        if (filter) {
                            results.filter { it.isAvailable }
                        } else {
                            results
                        }
                    }
            }
            .catch { error ->
                if (error is CancellationException) {
                    throw error
                }

                _message.value =
                    "Unable to load the book catalog."
                _isInitializing.value = false
                emit(emptyList())
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = emptyList()
            )

    val bookings = repository.currentBookings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            try {
                repository.initializeSampleBooks()
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _message.value =
                    "Unable to initialize sample books."
            } finally {
                // Allow Room to deliver its actual result.
                _databaseReady.value = true
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setAvailableOnly(available: Boolean) {
        _availableOnly.value = available
    }

    fun clearMessage() {
        _message.value = null
    }

    private fun performAction(
        successMessage: String,
        failureMessage: String,
        action: suspend () -> Boolean
    ) {
        if (_isLoading.value || _isInitializing.value) {
            return
        }

        _isLoading.value = true
        _message.value = null

        viewModelScope.launch {
            try {
                val successful = action()

                _message.value = if (successful) {
                    successMessage
                } else {
                    failureMessage
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _message.value =
                    "Operation failed. Please try again."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun reserveBook(
        bookId: Int,
        userName: String,
        rentalDays: Int
    ) {
        val cleanName = userName.trim()

        if (cleanName.isBlank() || cleanName.length > 100) {
            _message.value =
                "Please enter a valid name (1–100 characters)."
            return
        }

        if (rentalDays !in listOf(7, 14, 21)) {
            _message.value =
                "Please select a valid rental duration."
            return
        }

        performAction(
            successMessage = "Book reserved successfully!",
            failureMessage =
                "This book may no longer be available."
        ) {
            repository.reserveBook(
                bookId = bookId,
                userName = cleanName,
                rentalDays = rentalDays
            )
        }
    }

    fun activateBooking(bookingId: Int) {
        performAction(
            successMessage = "Booking activated!",
            failureMessage =
                "Unable to activate this booking."
        ) {
            repository.activateBooking(bookingId)
        }
    }

    fun renewBooking(
        bookingId: Int,
        additionalDays: Int = 7
    ) {
        if (additionalDays !in 1..30) {
            _message.value =
                "Renewal duration must be between 1 and 30 days."
            return
        }

        performAction(
            successMessage = "Book renewed successfully!",
            failureMessage =
                "Unable to renew this booking."
        ) {
            repository.renewBooking(
                bookingId = bookingId,
                additionalDays = additionalDays
            )
        }
    }

    fun returnBook(bookingId: Int) {
        performAction(
            successMessage = "Book returned successfully!",
            failureMessage =
                "Unable to return this book."
        ) {
            repository.returnBook(bookingId)
        }
    }

    fun cancelBooking(bookingId: Int) {
        performAction(
            successMessage = "Reservation cancelled!",
            failureMessage =
                "Unable to cancel this reservation."
        ) {
            repository.cancelBooking(bookingId)
        }
    }
}

class LibraryViewModelFactory(
    private val repository: LibraryRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (
            modelClass.isAssignableFrom(
                LibraryViewModel::class.java
            )
        ) {
            return LibraryViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}
