
package com.mad.kitabu.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    val bookId: Int = 0,

    val title: String,

    val author: String,

    val category: String,

    val isAvailable: Boolean = true
)
