package com.example.breakfree.ui.onboarding

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pages = listOf(
        listOf("we block the feeds.", "not your messages."),
        listOf("focus without disappearing.", "block the noise.", "keep the people.")
    )

    var page by remember { mutableIntStateOf(0) }
    var typedLines by remember { mutableStateOf(listOf<String>()) }
    var currentLine by remember { mutableStateOf("") }
    var lineIdx by remember { mutableIntStateOf(0) }
    var done by remember { mutableStateOf(false) }

    val view = LocalView.current
    val lines = pages[page]

    fun skipOrAdvance() {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        if (!done) {
            typedLines = lines
            currentLine = ""
            lineIdx = lines.size
        } else if (page == 0) {
            page = 1
            typedLines = emptyList()
            currentLine = ""
            lineIdx = 0
            done = false
        }
    }

    LaunchedEffect(page, lineIdx) {
        if (lineIdx >= lines.size) {
            done = true
            return@LaunchedEffect
        }
        val target = lines[lineIdx]
        currentLine = ""
        for (i in target.indices) {
            delay(45)
            currentLine = target.substring(0, i + 1)
        }
        delay(250)
        typedLines = typedLines + target
        currentLine = ""
        lineIdx += 1
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { skipOrAdvance() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 32.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                typedLines.forEach { line ->
                    Text(
                        text = line,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            lineHeight = 38.sp
                        ),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
                if (currentLine.isNotEmpty()) {
                    Text(
                        text = currentLine,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            lineHeight = 38.sp
                        ),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }

            AnimatedVisibility(
                visible = done && page == 1,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp),
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
            ) {
                Button(
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        onFinish()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "get started",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}
