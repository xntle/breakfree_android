package com.example.breakfree.ui.home

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.breakfree.service.BreakFreeAccessibilityService
import com.example.breakfree.session.ActiveSession
import com.example.breakfree.session.SessionManager
import com.example.breakfree.ui.components.GlassCard
import com.example.breakfree.ui.theme.AppBackground
import com.example.breakfree.ui.theme.GlassBg
import com.example.breakfree.ui.theme.GlassBgStrong
import com.example.breakfree.ui.theme.GlassBorder
import com.example.breakfree.ui.theme.TextPrimary
import com.example.breakfree.ui.theme.TextSecondary
import com.example.breakfree.ui.theme.TextTertiary
import kotlinx.coroutines.delay

@Composable
fun HomeScreen() {
    val session by SessionManager.activeSession.collectAsState()
    var showSheet by remember { mutableStateOf(false) }
    var showToast by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var serviceEnabled by remember {
        mutableStateOf(BreakFreeAccessibilityService.isEnabled(context))
    }

    // Re-check service status every time screen resumes (user may have just enabled it)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                serviceEnabled = BreakFreeAccessibilityService.isEnabled(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(showToast) {
        if (showToast) {
            delay(3000)
            showToast = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = 88.dp,
                bottom = 120.dp,
                start = 20.dp,
                end = 20.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { StatsCard() }
            item { Spacer(Modifier.height(4.dp)) }

            // Start button only visible when no session is running
            if (session == null) {
                item { StartSessionButton(onClick = { showSheet = true }) }
            }

            item { Spacer(Modifier.height(12.dp)) }
            item { SectionHeader("now") }

            // Active session card
            if (session != null) {
                item {
                    ActiveSessionCard(
                        session = session!!,
                        onEnd = { SessionManager.endSession() }
                    )
                }
            }

            // Accessibility permission card — always show until enabled
            if (!serviceEnabled) {
                item {
                    ScreenTimeCard(onEnable = {
                        context.startActivity(
                            Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                        )
                    })
                }
            }

            item { Spacer(Modifier.height(12.dp)) }
            item { SectionHeader("upcoming") }
            item { EmptyUpcomingCard() }

            item { Spacer(Modifier.height(12.dp)) }
            item { SectionHeader("quick challenges") }
            item {
                QuickChallengeCard(
                    title = "morning focus",
                    subtitle = "25 min • apps blocked",
                    emoji = "🌅",
                    onClick = { SessionManager.startSession(25, emptySet()) }
                )
            }
            item {
                QuickChallengeCard(
                    title = "deep work",
                    subtitle = "90 min • apps blocked",
                    emoji = "🧠",
                    onClick = { SessionManager.startSession(90, emptySet()) }
                )
            }
            item {
                QuickChallengeCard(
                    title = "evening wind down",
                    subtitle = "45 min • apps blocked",
                    emoji = "🌙",
                    onClick = { SessionManager.startSession(45, emptySet()) }
                )
            }
        }

        FloatingPetButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 100.dp)
                .zIndex(2f)
        )

        AnimatedVisibility(
            visible = showToast,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp, start = 20.dp, end = 20.dp)
                .zIndex(3f)
        ) {
            MotivationalToast()
        }

        Header(
            streakDays = 7,
            onStreakTap = { showToast = true },
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(4f)
        )
    }

    if (showSheet) {
        StartSessionSheet(
            onDismiss = { showSheet = false },
            onStart = { duration, apps ->
                SessionManager.startSession(duration, apps)
                showSheet = false
            }
        )
    }
}

// ── Active session card ──────────────────────────────────────────────────────

@Composable
fun ActiveSessionCard(session: ActiveSession, onEnd: () -> Unit) {
    var remainingMs by remember(session) { mutableLongStateOf(session.remainingMs) }

    LaunchedEffect(session) {
        while (remainingMs > 0L) {
            delay(1000)
            remainingMs = session.remainingMs
        }
        onEnd()
    }

    val totalSeconds = remainingMs / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    val timeText = if (hours > 0) {
        "%d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%02d:%02d".format(minutes, seconds)
    }

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "focus session",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = timeText,
                    style = MaterialTheme.typography.displayMedium,
                    color = TextPrimary
                )
                Spacer(Modifier.height(2.dp))
                val count = session.blockedApps.size
                Text(
                    text = if (count == 0) "reels blocked" else "$count app${if (count == 1) "" else "s"} + reels blocked",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0x22FF4444))
                    .border(1.dp, Color(0x55FF4444), RoundedCornerShape(50))
                    .clickable { onEnd() }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "end",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = Color(0xFFFF4444)
                )
            }
        }
    }
}

// ── Header ───────────────────────────────────────────────────────────────────

@Composable
fun Header(
    streakDays: Int,
    onStreakTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0f to AppBackground,
                        0.8f to AppBackground.copy(alpha = 0.96f),
                        1f to Color.Transparent
                    )
                )
            )
            .statusBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp)
    ) {
        Text(
            text = "breakfree",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                fontSize = 22.sp,
                letterSpacing = (-0.5).sp
            ),
            color = TextPrimary,
            modifier = Modifier.align(Alignment.CenterStart)
        )
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .clip(RoundedCornerShape(50))
                .background(GlassBg)
                .border(1.dp, GlassBorder, RoundedCornerShape(50))
                .clickable { onStreakTap() }
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text("🔥", fontSize = 14.sp)
            Text(
                text = "$streakDays days",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = TextPrimary
            )
        }
    }
}

// ── Stats card ───────────────────────────────────────────────────────────────

@Composable
fun StatsCard() {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "focused today",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "0",
                        style = MaterialTheme.typography.displayLarge,
                        color = TextPrimary
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "h  0m",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }
                Text(
                    text = "tap to see weekly breakdown",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextTertiary
                )
            }
            JarVisual(progress = 0f)
        }
    }
}

@Composable
fun JarVisual(progress: Float) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(72.dp)
    ) {
        Canvas(modifier = Modifier.size(72.dp)) {
            val strokeWidth = 3.dp.toPx()
            val inset = strokeWidth / 2
            val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
            val topLeft = Offset(inset, inset)
            drawArc(
                color = Color(0x1AFFFFFF),
                startAngle = -90f, sweepAngle = 360f,
                useCenter = false,
                style = Stroke(strokeWidth),
                topLeft = topLeft, size = arcSize
            )
            if (progress > 0f) {
                drawArc(
                    color = Color.White,
                    startAngle = -90f, sweepAngle = 360f * progress,
                    useCenter = false,
                    style = Stroke(strokeWidth, cap = StrokeCap.Round),
                    topLeft = topLeft, size = arcSize
                )
            }
        }
        Text("🫙", fontSize = 28.sp)
    }
}

// ── Buttons & sections ───────────────────────────────────────────────────────

@Composable
fun StartSessionButton(onClick: () -> Unit = {}) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        )
    ) {
        Text(
            text = "start session",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium.copy(
            letterSpacing = 1.2.sp,
            fontWeight = FontWeight.Medium
        ),
        color = TextTertiary,
        modifier = Modifier.padding(bottom = 2.dp)
    )
}

@Composable
fun ScreenTimeCard(onEnable: () -> Unit = {}) {
    GlassCard(modifier = Modifier.fillMaxWidth(), innerPadding = 16.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(GlassBgStrong),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "enable app blocking",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "grant accessibility access to block apps during sessions",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.White)
                    .clickable { onEnable() }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "enable",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun EmptyUpcomingCard() {
    GlassCard(modifier = Modifier.fillMaxWidth(), innerPadding = 16.dp) {
        Text(
            text = "no upcoming sessions",
            style = MaterialTheme.typography.bodyMedium,
            color = TextTertiary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun QuickChallengeCard(
    title: String,
    subtitle: String,
    emoji: String,
    onClick: () -> Unit = {}
) {
    GlassCard(modifier = Modifier.fillMaxWidth(), innerPadding = 16.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(emoji, fontSize = 24.sp)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(GlassBgStrong)
                    .border(1.dp, GlassBorder, RoundedCornerShape(50))
                    .clickable { onClick() }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "start",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                    color = TextPrimary
                )
            }
        }
    }
}

// ── Toast ────────────────────────────────────────────────────────────────────

@Composable
fun MotivationalToast() {
    val messages = listOf(
        "you're on a streak 🔥 keep it up",
        "consistency is everything",
        "7 days strong. don't break now.",
        "your future self will thank you"
    )
    val message = remember { messages.random() }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(colors = listOf(Color(0x55FF6B35), Color(0x55FF8C42)))
            )
            .border(1.dp, Color(0x66FF6B35), RoundedCornerShape(16.dp))
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ── Floating pet button ───────────────────────────────────────────────────────

@Composable
fun FloatingPetButton(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pet_bob")
    val yOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bob"
    )
    Box(
        modifier = modifier
            .size(64.dp)
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🥚",
            fontSize = 40.sp,
            modifier = Modifier.offset(y = yOffset.dp)
        )
    }
}
