
package com.mad.kitabu.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

data class BookingWithBook(
    val bookingId: Int,
    val bookOwnerId: Int,
    val userName: String,
    val bookingDate: Long,
    val returnDeadline: Long,
    val status: BookingStatus,
    val title: String,
    val author: String,
    val category: String
)

@Dao
interface BookingDao {

    @Insert
    suspend fun insertBooking(
        booking: BookingEntity
    ): Long

    @Query(
        """
        SELECT bookings.*, 
               books.title,
               books.author,
               books.category
        FROM bookings
        INNER JOIN books 
            ON bookings.bookOwnerId = books.bookId
        WHERE bookings.status IN ('PENDING', 'ACTIVE')
        ORDER BY bookings.returnDeadline ASC
        """
    )
    fun getCurrentBookings(): Flow<List<BookingWithBook>>

    @Query(
        """
        SELECT * FROM bookings
        WHERE bookingId = :bookingId
        LIMIT 1
        """
    )
    suspend fun getBookingById(
        bookingId: Int
    ): BookingEntity?

    @Query(
        """
        UPDATE bookings
        SET returnDeadline = :newDeadline
        WHERE bookingId = :bookingId
        AND status = 'ACTIVE'
        """
    )
    suspend fun renewBooking(
        bookingId: Int,
        newDeadline: Long
    ): Int

    @Query(
        """
        UPDATE bookings
        SET status = 'RETURNED'
        WHERE bookingId = :bookingId
        AND status = 'ACTIVE'
        """
    )
    suspend fun returnBooking(
        bookingId: Int
    ): Int

    @Query(
        """
        DELETE FROM bookings
        WHERE bookingId = :bookingId
        AND status = 'PENDING'
        """
    )
    suspend fun cancelBooking(
        bookingId: Int
    ): Int

    @Query(
        """
        UPDATE bookings
        SET status = 'ACTIVE'
        WHERE bookingId = :bookingId
        AND status = 'PENDING'
        """
    )
    suspend fun activateBooking(
        bookingId: Int
    ): Int
}
