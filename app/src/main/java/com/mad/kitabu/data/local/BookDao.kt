
package com.mad.kitabu.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertBook(book: BookEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Query("SELECT * FROM books ORDER BY title ASC")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query(
        "SELECT * FROM books WHERE isAvailable = :available ORDER BY title ASC"
    )
    fun getBooksByAvailability(
        available: Boolean
    ): Flow<List<BookEntity>>

    @Query(
        """
        SELECT * FROM books
        WHERE title LIKE '%' || :query || '%'
        OR author LIKE '%' || :query || '%'
        ORDER BY title ASC
        """
    )
    fun searchBooks(query: String): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE bookId = :bookId LIMIT 1")
    suspend fun getBookById(bookId: Int): BookEntity?

    @Update
    suspend fun updateBook(book: BookEntity)

    @Query(
        "UPDATE books SET isAvailable = :available WHERE bookId = :bookId"
    )
    suspend fun updateAvailability(
        bookId: Int,
        available: Boolean
    )

    @Query("SELECT COUNT(*) FROM books")
    suspend fun getBookCount(): Int
}
