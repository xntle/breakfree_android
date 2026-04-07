package com.example.breakfree

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.breakfree.ui.auth.LoginScreen
import com.example.breakfree.ui.home.HomeScreen
import com.example.breakfree.ui.theme.AppBackground
import com.example.breakfree.ui.theme.BreakfreeTheme
import com.example.breakfree.ui.theme.GlassBg
import com.example.breakfree.ui.theme.TextPrimary
import com.example.breakfree.ui.theme.TextSecondary
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private var isLoggedIn by mutableStateOf(false)
    private var isSigningIn by mutableStateOf(false)

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.w("Auth", "Google sign in failed", e)
            isSigningIn = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        auth.signOut()
        isLoggedIn = auth.currentUser != null

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        enableEdgeToEdge()
        setContent {
            BreakfreeTheme {
                if (isLoggedIn) {
                    BreakFreeApp()
                } else {
                    LoginScreen(
                        onSignInWithGoogle = {
                            isSigningIn = true
                            signInLauncher.launch(googleSignInClient.signInIntent)
                        },
                        isLoading = isSigningIn
                    )
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                isSigningIn = false
                if (task.isSuccessful) {
                    isLoggedIn = true
                } else {
                    Log.w("Auth", "signInWithCredential failed", task.exception)
                }
            }
    }
}

private data class NavTab(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun BreakFreeApp() {
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        NavTab("home", Icons.Filled.Home, Icons.Outlined.Home),
        NavTab("messages", Icons.Filled.Email, Icons.Outlined.Email),
        NavTab("social", Icons.Filled.Leaderboard, Icons.Outlined.Leaderboard),
        NavTab("profile", Icons.Filled.Person, Icons.Outlined.Person),
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        when (selectedTab) {
            0 -> HomeScreen()
            1 -> PlaceholderScreen("messages")
            2 -> PlaceholderScreen("social")
            3 -> PlaceholderScreen("profile")
        }

        NavigationBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            containerColor = AppBackground,
            tonalElevation = 0.dp
        ) {
            tabs.forEachIndexed { index, tab ->
                NavigationBarItem(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == index) tab.selectedIcon else tab.unselectedIcon,
                            contentDescription = tab.label,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = tab.label,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TextPrimary,
                        selectedTextColor = TextPrimary,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = GlassBg
                    )
                )
            }
        }
    }
}

@Composable
fun PlaceholderScreen(name: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            color = TextSecondary
        )
    }
}
