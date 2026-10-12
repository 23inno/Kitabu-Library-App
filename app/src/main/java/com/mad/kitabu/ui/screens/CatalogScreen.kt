
package com.mad.kitabu.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mad.kitabu.data.local.BookEntity
import com.mad.kitabu.ui.viewmodel.LibraryViewModel

private val Navy = Color(0xFF172B4D)
private val Indigo = Color(0xFF526AA0)
private val Background = Color(0xFFF8F7F3)
private val Green = Color(0xFF23855C)
private val PaleGreen = Color(0xFFE1F4E8)
private val Coral = Color(0xFFB94D38)
private val PaleCoral = Color(0xFFFFE9E3)

@Composable
fun CatalogScreen(viewModel: LibraryViewModel) {
    val books by viewModel.books.collectAsStateWithLifecycle()
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    val availableOnly by viewModel.availableOnly.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isInitializing by viewModel.isInitializing.collectAsStateWithLifecycle()

    var selectedBook by remember {
        mutableStateOf<BookEntity?>(null)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(24.dp))

            Text(
                text = "KITABU",
                color = Indigo,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Discover your\nnext great read.",
                color = Navy,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                lineHeight = 40.sp
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Explore, reserve and enjoy our collection.",
                color = Color(0xFF647084),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(22.dp))

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Navy,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Your digital library",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            text = "A world of books awaits.",
                            color = Color(0xFFCED9ED),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Text(
                        text = "📚",
                        fontSize = 32.sp
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = query,
                onValueChange = viewModel::updateSearchQuery,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Search titles or authors")
                },
                leadingIcon = {
                    Text("⌕", fontSize = 26.sp, color = Indigo)
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Indigo,
                    unfocusedBorderColor = Color(0xFFD7DCE5),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Navy,
                    unfocusedTextColor = Navy
                )
            )

            Spacer(Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = availableOnly,
                    onCheckedChange = viewModel::setAvailableOnly,
                    colors = CheckboxDefaults.colors(
                        checkedColor = Indigo
                    )
                )

                Text(
                    text = "Available books only",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Navy,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Explore books",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Navy
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE8EDF7)
                ) {
                    Text(
                        text = "${books.size} found",
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 7.dp
                        ),
                        color = Indigo,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            if (isLoading || isInitializing) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = Indigo
                )
                Spacer(Modifier.height(12.dp))
            }

            when {
                isInitializing -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Indigo)
                    }
                }

                books.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("📖", fontSize = 42.sp)

                            Spacer(Modifier.height(12.dp))

                            Text(
                                text = "No books found",
                                fontWeight = FontWeight.Bold,
                                color = Navy
                            )

                            Spacer(Modifier.height(6.dp))

                            Text(
                                text = "Try another search or filter.",
                                color = Color.Gray
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(
                            items = books,
                            key = { it.bookId }
                        ) { book ->
                            ModernBookCard(
                                book = book,
                                enabled = !isLoading && !isInitializing,
                                onClick = {
                                    selectedBook = book
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    selectedBook?.let { book ->
        ModernReservationDialog(
            book = book,
            isLoading = isLoading || isInitializing,
            onDismiss = { selectedBook = null },
            onReserve = { name, days ->
                viewModel.reserveBook(book.bookId, name, days)
                selectedBook = null
            }
        )
    }
}

@Composable
private fun ModernBookCard(
    book: BookEntity,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = enabled && book.isAvailable,
                onClick = onClick
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            1.dp,
            Color(0xFFEBEDF1)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    color = Color(0xFFE8EDF7),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.size(
                        width = 56.dp,
                        height = 70.dp
                    )
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("📘", fontSize = 28.sp)
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Navy,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(5.dp))

                    Text(
                        text = book.author,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF697588)
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = book.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = Indigo
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            HorizontalDivider(color = Color(0xFFF0F1F4))

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    color = if (book.isAvailable) {
                        PaleGreen
                    } else {
                        PaleCoral
                    },
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = if (book.isAvailable) {
                            "● Available"
                        } else {
                            "● Reserved"
                        },
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 7.dp
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (book.isAvailable) {
                            Green
                        } else {
                            Coral
                        },
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (book.isAvailable) {
                    Text(
                        text = "Reserve  →",
                        color = Indigo,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun ModernReservationDialog(
    book: BookEntity,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onReserve: (String, Int) -> Unit
) {
    var userName by remember(book.bookId) {
        mutableStateOf("")
    }

    var selectedDays by remember(book.bookId) {
        mutableIntStateOf(7)
    }

    AlertDialog(
        onDismissRequest = {
            if (!isLoading) onDismiss()
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White,
        title = {
            Column {
                Text(
                    "Reserve this book",
                    color = Navy,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    book.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Indigo
                )
            }
        },
        text = {
            Column {
                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text("Your name") },
                    singleLine = true,
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(20.dp))

                Text(
                    "Choose rental duration",
                    color = Navy,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(8.dp))

                listOf(7, 14, 21).forEach { days ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !isLoading) {
                                selectedDays = days
                            }
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedDays == days,
                            onClick = { selectedDays = days },
                            enabled = !isLoading,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Indigo
                            )
                        )

                        Text(
                            "$days days",
                            color = Navy
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onReserve(userName.trim(), selectedDays)
                },
                enabled = userName.trim().isNotEmpty() &&
                        userName.trim().length <= 100 &&
                        !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Indigo
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Confirm reservation")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Cancel", color = Indigo)
            }
        }
    )
}
