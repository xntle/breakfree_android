package com.example.breakfree.ui.socials

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.breakfree.ui.theme.AppBackground
import com.example.breakfree.ui.theme.GlassBg
import com.example.breakfree.ui.theme.GlassBgStrong
import com.example.breakfree.ui.theme.StreakOrange
import com.example.breakfree.ui.theme.TextPrimary
import com.example.breakfree.ui.theme.TextSecondary
import com.example.breakfree.ui.theme.TextTertiary

data class SocialPlatform(val name: String, val handle: String)

private val platforms = listOf(
    SocialPlatform("Instagram", "reels, stories, feed"),
    SocialPlatform("TikTok", "for you page, following"),
    SocialPlatform("YouTube", "shorts, subscriptions"),
    SocialPlatform("Twitter / X", "timeline, explore"),
    SocialPlatform("Facebook", "news feed, reels"),
    SocialPlatform("Snapchat", "discover, spotlight"),
    SocialPlatform("Reddit", "home feed, popular"),
    SocialPlatform("LinkedIn", "feed, notifications"),
)

@Composable
fun SocialsScreen() {
    val blocked = remember { platforms.map { it.name }.toMutableSet() }
    val toggleState = remember {
        mutableStateOf(platforms.associate { it.name to true })
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = "socials",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary,
                modifier = Modifier.padding(top = 24.dp, bottom = 4.dp)
            )
            Text(
                text = "toggle which apps get blocked during sessions",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(platforms) { platform ->
                    var isBlocked by remember {
                        mutableStateOf(toggleState.value[platform.name] ?: true)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(GlassBg)
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = platform.name,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = TextPrimary
                            )
                            Text(
                                text = platform.handle,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextTertiary
                            )
                        }
                        Switch(
                            checked = isBlocked,
                            onCheckedChange = { isBlocked = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = AppBackground,
                                checkedTrackColor = StreakOrange,
                                uncheckedThumbColor = TextSecondary,
                                uncheckedTrackColor = GlassBgStrong
                            )
                        )
                    }
                }
            }
        }
    }
}
