
package com.mad.kitabu

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mad.kitabu.data.local.BookEntity
import com.mad.kitabu.data.local.BookingStatus
import com.mad.kitabu.data.local.LibraryDatabase
import com.mad.kitabu.data.repository.LibraryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class LibraryDatabaseTest {

    private lateinit var database: LibraryDatabase
    private lateinit var repository: LibraryRepository

    @Before
    fun setUp() {
        val context =
            ApplicationProvider.getApplicationContext<Context>()

        database = Room.inMemoryDatabaseBuilder(
            context,
            LibraryDatabase::class.java
        ).build()

        repository = LibraryRepository(
            database = database,
            context = context
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    private suspend fun addTestBook(): Int {
        return database.bookDao().insertBook(
            BookEntity(
                title = "Test Kotlin Book",
                author = "Test Author",
                category = "Programming"
            )
        ).toInt()
    }

    private suspend fun reserveTestBook(
        bookId: Int
    ): Int {
        val result = repository.reserveBook(
            bookId = bookId,
            userName = "Test Student",
            rentalDays = 7
        )

        assertTrue(result)

        val bookings =
            database.bookingDao()
                .getCurrentBookings()
                .first()

        return bookings.single {
            it.bookOwnerId == bookId
        }.bookingId
    }

    @Test
    fun reserveBook_marksBookUnavailable() = runBlocking {
        val bookId = addTestBook()

        val bookingId = reserveTestBook(bookId)

        val book = database.bookDao()
            .getBookById(bookId)

        assertNotNull(book)
        assertFalse(book!!.isAvailable)

        val booking = database.bookingDao()
            .getBookingById(bookingId)

        assertNotNull(booking)
        assertEquals(
            BookingStatus.PENDING,
            booking!!.status
        )
    }

    @Test
    fun duplicateReservation_isRejected() = runBlocking {
        val bookId = addTestBook()

        reserveTestBook(bookId)

        val secondResult = repository.reserveBook(
            bookId = bookId,
            userName = "Another Student",
            rentalDays = 14
        )

        assertFalse(secondResult)

        val bookings = database.bookingDao()
            .getCurrentBookings()
            .first()

        assertEquals(1, bookings.size)
    }

    @Test
    fun cancellingPendingBooking_releasesBook() = runBlocking {
        val bookId = addTestBook()
        val bookingId = reserveTestBook(bookId)

        val cancelled =
            repository.cancelBooking(bookingId)

        assertTrue(cancelled)

        val book = database.bookDao()
            .getBookById(bookId)

        assertNotNull(book)
        assertTrue(book!!.isAvailable)

        val booking = database.bookingDao()
            .getBookingById(bookingId)

        assertNull(booking)
    }

    @Test
    fun activatingBooking_changesStatus() = runBlocking {
        val bookId = addTestBook()
        val bookingId = reserveTestBook(bookId)

        val activated =
            repository.activateBooking(bookingId)

        assertTrue(activated)

        val booking = database.bookingDao()
            .getBookingById(bookingId)

        assertNotNull(booking)
        assertEquals(
            BookingStatus.ACTIVE,
            booking!!.status
        )
    }

    @Test
    fun returningBook_restoresAvailability() = runBlocking {
        val bookId = addTestBook()
        val bookingId = reserveTestBook(bookId)

        assertTrue(
            repository.activateBooking(bookingId)
        )

        val returned =
            repository.returnBook(bookingId)

        assertTrue(returned)

        val book = database.bookDao()
            .getBookById(bookId)

        assertNotNull(book)
        assertTrue(book!!.isAvailable)

        val booking = database.bookingDao()
            .getBookingById(bookingId)

        assertNotNull(booking)
        assertEquals(
            BookingStatus.RETURNED,
            booking!!.status
        )
    }

    @Test
    fun renewingBooking_extendsDeadline() = runBlocking {
        val bookId = addTestBook()
        val bookingId = reserveTestBook(bookId)

        assertTrue(
            repository.activateBooking(bookingId)
        )

        val originalBooking = database.bookingDao()
            .getBookingById(bookingId)

        assertNotNull(originalBooking)

        val originalDeadline =
            originalBooking!!.returnDeadline

        val renewed = repository.renewBooking(
            bookingId = bookingId,
            additionalDays = 7
        )

        assertTrue(renewed)

        val updatedBooking = database.bookingDao()
            .getBookingById(bookingId)

        assertNotNull(updatedBooking)

        val updatedDeadline =
            updatedBooking!!.returnDeadline

        val expectedExtension =
            TimeUnit.DAYS.toMillis(7)

        assertEquals(
            expectedExtension,
            updatedDeadline - originalDeadline
        )
    }
}
