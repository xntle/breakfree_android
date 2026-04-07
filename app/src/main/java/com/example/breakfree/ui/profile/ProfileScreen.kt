package com.example.breakfree.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.breakfree.ui.theme.StreakOrange
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.util.Calendar

private val monthNames = listOf(
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December"
)

private fun getCalendarDays(year: Int, month: Int): List<Int?> {
    val cal = Calendar.getInstance().apply { set(year, month, 1) }
    val firstDay = cal.get(Calendar.DAY_OF_WEEK) - 1
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    return buildList {
        repeat(firstDay) { add(null) }
        for (day in 1..daysInMonth) add(day)
    }
}

@Composable
fun ProfileScreen(onSignOut: () -> Unit) {
    val user = Firebase.auth.currentUser
    val displayName = user?.displayName ?: "User"
    val initial = displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    val handle = "@${user?.email?.substringBefore("@") ?: "user"}"

    val now = remember { Calendar.getInstance() }
    val todayDay = now.get(Calendar.DAY_OF_MONTH)
    val todayMonth = now.get(Calendar.MONTH)
    val todayYear = now.get(Calendar.YEAR)

    var displayMonth by remember { mutableIntStateOf(todayMonth) }
    var displayYear by remember { mutableIntStateOf(todayYear) }

    // Mock streak data — connect to real session history later
    val streakDays = remember { setOf(1, 2, 3, 5, 6, 7) }
    val streakCount = 6

    val calendarDays = remember(displayMonth, displayYear) {
        getCalendarDays(displayYear, displayMonth)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // — Profile card —
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1A1A1A))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3A3A3A)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initial,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                }
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color.White
                )
                Text(
                    text = handle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF888888)
                )
            }
        }

        // — Go premium card —
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF1B3A1B), Color(0xFF2D5A2D))
                    )
                )
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "go premium",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
                Text(
                    text = "remove ads & unlock features",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xAAFFFFFF)
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.White
            )
        }

        // — Streak + Calendar card —
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1A1A1A))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Streak header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "🔥", fontSize = 22.sp)
                Column {
                    Text(
                        text = "$streakCount Day Streak",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                    Text(
                        text = "Keep it going!",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF888888)
                    )
                }
            }

            // Month navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (displayMonth == 0) { displayMonth = 11; displayYear-- }
                    else displayMonth--
                }) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous month",
                        tint = Color.White
                    )
                }
                Text(
                    text = "${monthNames[displayMonth]} $displayYear",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color.White
                )
                IconButton(onClick = {
                    if (displayMonth == 11) { displayMonth = 0; displayYear++ }
                    else displayMonth++
                }) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowRight,
                        contentDescription = "Next month",
                        tint = Color.White
                    )
                }
            }

            // Day of week labels
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("S", "M", "T", "W", "T", "F", "S").forEach { label ->
                    Text(
                        text = label,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF555555)
                    )
                }
            }

            // Calendar grid
            calendarDays.chunked(7).forEach { week ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    repeat(7) { i ->
                        val day = week.getOrNull(i)
                        val isToday = day == todayDay
                                && displayMonth == todayMonth
                                && displayYear == todayYear
                        val isStreak = day != null && streakDays.contains(day)

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            if (day != null) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isToday -> StreakOrange
                                                isStreak -> Color.White
                                                else -> Color.Transparent
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = day.toString(),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = if (isToday || isStreak) FontWeight.Bold
                                            else FontWeight.Normal
                                        ),
                                        color = when {
                                            isToday -> Color.White
                                            isStreak -> Color.Black
                                            else -> Color(0xFF666666)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // — Daily Goal row —
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1A1A1A))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Daily Goal",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = Color.White
            )
            Text(
                text = "4 hours",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF888888)
            )
        }

        // — Sign out —
        TextButton(
            onClick = onSignOut,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 88.dp)
        ) {
            Text(
                text = "sign out",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF444444)
            )
        }
    }
}
