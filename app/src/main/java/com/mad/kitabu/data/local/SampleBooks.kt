
package com.mad.kitabu.data.local

object SampleBooks {

    fun getBooks(): List<BookEntity> {
        return listOf(
            BookEntity(
                title = "Introduction to Kotlin",
                author = "John Smith",
                category = "Programming"
            ),
            BookEntity(
                title = "Android Development Basics",
                author = "Sarah Johnson",
                category = "Technology"
            ),
            BookEntity(
                title = "Database Management Systems",
                author = "Michael Brown",
                category = "Database"
            ),
            BookEntity(
                title = "Web Design Fundamentals",
                author = "Emily Davis",
                category = "Web Development"
            ),
            BookEntity(
                title = "Software Engineering Principles",
                author = "David Wilson",
                category = "Software Engineering"
            ),
            BookEntity(
                title = "Computer Networks",
                author = "Robert Taylor",
                category = "Networking"
            ),
            BookEntity(
                title = "Artificial Intelligence Basics",
                author = "Linda Anderson",
                category = "Artificial Intelligence"
            ),
            BookEntity(
                title = "Cybersecurity Essentials",
                author = "James Thomas",
                category = "Cybersecurity"
            )
        )
    }
}
