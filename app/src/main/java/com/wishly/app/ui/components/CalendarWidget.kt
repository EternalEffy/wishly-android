package com.wishly.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wishly.app.data.model.Wishlist
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun CalendarWidget(
    wishlists: List<Wishlist>, modifier: Modifier = Modifier, onDateClick: (String) -> Unit = {}
) {
    val eventDates = remember(wishlists) {
        wishlists.mapNotNull { it.eventDate }.map { date -> normalizeDate(date) }.distinct()
            .sorted()
    }

    val currentDate = remember { Calendar.getInstance() }
    val currentMonth = currentDate.get(Calendar.MONTH)
    val currentYear = currentDate.get(Calendar.YEAR)

    val daysInMonth = remember(currentMonth, currentYear) {
        getDaysInMonth(currentMonth, currentYear)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFE6E0FF),
                            Color(0xFFD0FFF5)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            tint = Color(0xFF7C5DFA),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${getMonthName(currentMonth)} $currentYear",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF1A1A2E)
                        )
                    }

                    Row {
                        IconButton(
                            onClick = { /* Previous month */ }, modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "Previous month",
                                tint = Color(0xFF7C5DFA),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        IconButton(
                            onClick = { /* Next month */ }, modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Next month",
                                tint = Color(0xFF7C5DFA),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa").forEach { day ->
                        Text(
                            text = day,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF9CA3AF),
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    val weeks = daysInMonth.chunked(7)
                    weeks.forEach { week ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            week.forEach { day ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1.8f)
                                ) {
                                    if (day == 0) {
                                        Box(modifier = Modifier.fillMaxSize())
                                    } else {
                                        val dayDate = formatDate(currentYear, currentMonth, day)
                                        val normalizedDayDate =
                                            normalizeDateForCalendar(currentYear, currentMonth, day)
                                        val hasEvent = eventDates.any { it == normalizedDayDate }
                                        val isToday = isToday(day, currentMonth, currentYear)

                                        DayCell(
                                            day = day,
                                            hasEvent = hasEvent,
                                            isToday = isToday,
                                            onClick = { if (hasEvent) onDateClick(dayDate) })
                                    }
                                }
                            }
                            repeat(7 - week.size) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(28.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    day: Int, hasEvent: Boolean, isToday: Boolean, onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .then(
                if (hasEvent) {
                    Modifier.background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF5F37FD).copy(alpha = 0.25f),
                                Color(0xFFB29FFD).copy(alpha = 0.25f)
                            )
                        )
                    )
                } else {
                    Modifier
                }
            )
            .clickable(enabled = hasEvent, onClick = onClick), contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            fontSize = if (isToday) 11.sp else 10.sp,
            fontWeight = if (isToday) FontWeight.Bold else if (hasEvent) FontWeight.SemiBold else FontWeight.Normal,
            color = if (hasEvent) Color(0xFF7C5DFA) else Color(0xFF6B7280)
        )
    }
}

fun normalizeDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date!!)
        } catch (e2: Exception) {
            dateString
        }
    }
}

fun normalizeDateForCalendar(year: Int, month: Int, day: Int): String {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, day, 0, 0, 0)
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
}

private fun getDaysInMonth(month: Int, year: Int): List<Int> {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, 1)
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1

    return List(firstDayOfWeek) { 0 } + List(daysInMonth) { it + 1 }
}

private fun formatDate(year: Int, month: Int, day: Int): String {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, day)
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(calendar.time)
}

private fun getMonthName(month: Int): String {
    return SimpleDateFormat("MMMM", Locale.getDefault()).format(
        Calendar.getInstance().apply { set(Calendar.MONTH, month) }.time
    )
}

private fun isToday(day: Int, month: Int, year: Int): Boolean {
    val today = Calendar.getInstance()
    return today.get(Calendar.DAY_OF_MONTH) == day && today.get(Calendar.MONTH) == month && today.get(
        Calendar.YEAR
    ) == year
}