
package com.mad.kitabu.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mad.kitabu.data.local.BookingStatus
import com.mad.kitabu.data.local.BookingWithBook
import com.mad.kitabu.ui.viewmodel.LibraryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

private val Navy = Color(0xFF172B4D)
private val Indigo = Color(0xFF526AA0)
private val Ivory = Color(0xFFF8F7F3)
private val Green = Color(0xFF23855C)
private val Coral = Color(0xFFB94D38)

@Composable
fun ReservationsScreen(
    viewModel: LibraryViewModel
) {
    val bookings by viewModel.bookings.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val bookCountText = if (bookings.size == 1) {
        "1 book"
    } else {
        "${bookings.size} books"
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Ivory
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(24.dp))

            Text(
                text = "MY LIBRARY",
                color = Indigo,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Your reservations",
                color = Navy,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Keep track of your books and due dates.",
                color = Color(0xFF647084),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(24.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Navy,
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Current reservations",
                            color = Color(0xFFCED9ED),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = bookings.size.toString(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp
                        )
                    }

                    Text(
                        text = "📖",
                        fontSize = 34.sp
                    )
                }
            }

            Spacer(Modifier.height(22.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Borrowing activity",
                    color = Navy,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    color = Color(0xFFE8EDF7),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = bookCountText,
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 7.dp
                        ),
                        color = Indigo,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = Indigo
                )

                Spacer(Modifier.height(12.dp))
            }

            if (bookings.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📚",
                            fontSize = 44.sp
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = "Nothing borrowed yet",
                            color = Navy,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            text = "Explore the catalog to reserve a book.",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(
                        items = bookings,
                        key = { it.bookingId }
                    ) { booking ->
                        ModernReservationCard(
                            booking = booking,
                            enabled = !isLoading,
                            onActivate = {
                                viewModel.activateBooking(
                                    booking.bookingId
                                )
                            },
                            onRenew = {
                                viewModel.renewBooking(
                                    booking.bookingId,
                                    7
                                )
                            },
                            onReturn = {
                                viewModel.returnBook(
                                    booking.bookingId
                                )
                            },
                            onCancel = {
                                viewModel.cancelBooking(
                                    booking.bookingId
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModernReservationCard(
    booking: BookingWithBook,
    enabled: Boolean,
    onActivate: () -> Unit,
    onRenew: () -> Unit,
    onReturn: () -> Unit,
    onCancel: () -> Unit
) {
    val formatter = SimpleDateFormat(
        "dd MMM yyyy",
        Locale.getDefault()
    )

    val reservedDate = formatter.format(
        Date(booking.bookingDate)
    )

    val deadlineDate = formatter.format(
        Date(booking.returnDeadline)
    )

    val remainingMillis =
        booking.returnDeadline - System.currentTimeMillis()

    val dayMillis = TimeUnit.DAYS.toMillis(1)

    val deadlineMessage = when {
        remainingMillis < 0L -> {
            val overdueDays =
                ((-remainingMillis - 1L) / dayMillis) + 1L

            "$overdueDays day(s) overdue"
        }

        remainingMillis == 0L -> {
            "Due now"
        }

        else -> {
            val days =
                ((remainingMillis - 1L) / dayMillis) + 1L

            "$days day(s) remaining"
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = Color(0xFFE8EDF7),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "📘",
                            fontSize = 26.sp
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = booking.title,
                        color = Navy,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = booking.author,
                        color = Color(0xFF647084),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Surface(
                color = if (
                    booking.status == BookingStatus.ACTIVE
                ) {
                    Color(0xFFE1F4E8)
                } else {
                    Color(0xFFE8EDF7)
                },
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = booking.status.name,
                    color = if (
                        booking.status == BookingStatus.ACTIVE
                    ) {
                        Green
                    } else {
                        Indigo
                    },
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 7.dp
                    )
                )
            }

            Spacer(Modifier.height(14.dp))

            HorizontalDivider(
                color = Color(0xFFF0F1F4)
            )

            Spacer(Modifier.height(14.dp))

            ReservationDetail(
                label = "Reserved on",
                value = reservedDate
            )

            Spacer(Modifier.height(8.dp))

            ReservationDetail(
                label = "Return by",
                value = deadlineDate
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = deadlineMessage,
                color = if (remainingMillis < 0L) {
                    Coral
                } else {
                    Indigo
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(18.dp))

            when (booking.status) {
                BookingStatus.PENDING -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onActivate,
                            enabled = enabled,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Indigo
                            )
                        ) {
                            Text("Activate")
                        }

                        OutlinedButton(
                            onClick = onCancel,
                            enabled = enabled,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }
                    }
                }

                BookingStatus.ACTIVE -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onRenew,
                            enabled = enabled,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Indigo
                            )
                        ) {
                            Text("Renew +7d")
                        }

                        OutlinedButton(
                            onClick = onReturn,
                            enabled = enabled,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Return")
                        }
                    }
                }

                BookingStatus.RETURNED -> {
                    Text(
                        text = "Returned",
                        color = Green,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ReservationDetail(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color(0xFF7A8495),
            style = MaterialTheme.typography.bodySmall
        )

        Text(
            text = value,
            color = Navy,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}
