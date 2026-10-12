
package com.mad.kitabu.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bookings",
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["bookId"],
            childColumns = ["bookOwnerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["bookOwnerId"])
    ]
)
data class BookingEntity(
    @PrimaryKey(autoGenerate = true)
    val bookingId: Int = 0,

    val bookOwnerId: Int,

    val userName: String,

    val bookingDate: Long,

    val returnDeadline: Long,

    val status: BookingStatus = BookingStatus.PENDING
)
