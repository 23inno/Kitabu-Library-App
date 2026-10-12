
package com.mad.kitabu.data.repository

import android.content.Context
import androidx.room.withTransaction
import com.mad.kitabu.data.local.BookEntity
import com.mad.kitabu.data.local.BookingEntity
import com.mad.kitabu.data.local.BookingStatus
import com.mad.kitabu.data.local.LibraryDatabase
import com.mad.kitabu.data.local.SampleBooks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class LibraryRepository(
    private val database: LibraryDatabase,
    context: Context
) {
    private val bookDao = database.bookDao()
    private val bookingDao = database.bookingDao()

    private val preferences = context.applicationContext
        .getSharedPreferences(
            "kitabu_initialization",
            Context.MODE_PRIVATE
        )

    val allBooks: Flow<List<BookEntity>> =
        bookDao.getAllBooks()

    val currentBookings = bookingDao.getCurrentBookings()

    fun searchBooks(query: String) =
        bookDao.searchBooks(query.trim())

    fun getBooksByAvailability(available: Boolean) =
        bookDao.getBooksByAvailability(available)

    suspend fun initializeSampleBooks() =
        withContext(Dispatchers.IO) {
            if (preferences.getBoolean("sample_books_seeded", false)) {
                return@withContext
            }

            database.withTransaction {
                if (bookDao.getBookCount() == 0) {
                    bookDao.insertBooks(SampleBooks.getBooks())
                }
            }

            val saved = preferences.edit()
                .putBoolean("sample_books_seeded", true)
                .commit()

            if (!saved) {
                throw IllegalStateException(
                    "Unable to save initialization state"
                )
            }
        }

    suspend fun reserveBook(
        bookId: Int,
        userName: String,
        rentalDays: Int
    ): Boolean {
        val cleanName = userName.trim()

        if (
            cleanName.isBlank() ||
            cleanName.length > 100 ||
            rentalDays !in listOf(7, 14, 21)
        ) {
            return false
        }

        val now = System.currentTimeMillis()
        val duration = TimeUnit.DAYS.toMillis(
            rentalDays.toLong()
        )
        val deadline = now + duration

        if (deadline <= now) return false

        return database.withTransaction {
            val book = bookDao.getBookById(bookId)

            if (book == null || !book.isAvailable) {
                false
            } else {
                bookingDao.insertBooking(
                    BookingEntity(
                        bookOwnerId = book.bookId,
                        userName = cleanName,
                        bookingDate = now,
                        returnDeadline = deadline,
                        status = BookingStatus.PENDING
                    )
                )

                bookDao.updateAvailability(
                    bookId = book.bookId,
                    available = false
                )

                true
            }
        }
    }

    suspend fun activateBooking(
        bookingId: Int
    ): Boolean {
        return database.withTransaction {
            bookingDao.activateBooking(bookingId) > 0
        }
    }

    suspend fun renewBooking(
        bookingId: Int,
        additionalDays: Int
    ): Boolean {
        if (additionalDays !in 1..30) return false

        return database.withTransaction {
            val booking = bookingDao.getBookingById(bookingId)

            if (
                booking == null ||
                booking.status != BookingStatus.ACTIVE
            ) {
                false
            } else {
                val now = System.currentTimeMillis()
                val baseDate = maxOf(
                    now,
                    booking.returnDeadline
                )

                val extension = TimeUnit.DAYS.toMillis(
                    additionalDays.toLong()
                )
                val newDeadline = baseDate + extension

                if (newDeadline <= baseDate) {
                    false
                } else {
                    bookingDao.renewBooking(
                        bookingId,
                        newDeadline
                    ) > 0
                }
            }
        }
    }

    suspend fun returnBook(
        bookingId: Int
    ): Boolean {
        return database.withTransaction {
            val booking = bookingDao.getBookingById(bookingId)

            if (
                booking == null ||
                booking.status != BookingStatus.ACTIVE
            ) {
                false
            } else {
                val updated = bookingDao.returnBooking(bookingId)

                if (updated > 0) {
                    bookDao.updateAvailability(
                        booking.bookOwnerId,
                        true
                    )
                    true
                } else {
                    false
                }
            }
        }
    }

    suspend fun cancelBooking(
        bookingId: Int
    ): Boolean {
        return database.withTransaction {
            val booking = bookingDao.getBookingById(bookingId)

            if (
                booking == null ||
                booking.status != BookingStatus.PENDING
            ) {
                false
            } else {
                val deleted = bookingDao.cancelBooking(bookingId)

                if (deleted > 0) {
                    bookDao.updateAvailability(
                        booking.bookOwnerId,
                        true
                    )
                    true
                } else {
                    false
                }
            }
        }
    }
}
