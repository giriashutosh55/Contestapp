package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.UserRole
import com.example.ui.screens.ActiveGameRoomScreen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AdminLoginScreen
import com.example.ui.screens.ContestDetailScreen
import com.example.ui.screens.DisputeReportScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.MatchHistoryScreen
import com.example.ui.screens.MatchResultScreen
import com.example.ui.screens.MatchmakingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AppScreen
import com.example.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScaffold(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScaffold(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val user by viewModel.currentUser.collectAsStateWithLifecycle()

    // Determine if bottom navigation should be visible per PRD Section 5
    val showBottomBar = currentScreen is AppScreen.Home ||
            currentScreen is AppScreen.Contests ||
            currentScreen is AppScreen.Leaderboard ||
            currentScreen is AppScreen.History ||
            currentScreen is AppScreen.Profile

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .testTag("main_bottom_nav"),
                    containerColor = DarkSurface,
                    tonalElevation = 8.dp
                ) {
                    // 1. Home
                    NavigationBarItem(
                        selected = currentScreen is AppScreen.Home,
                        onClick = { viewModel.navigateTo(AppScreen.Home) },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home", fontSize = 11.sp) },
                        colors = getNavColors()
                    )

                    // 2. Contests
                    NavigationBarItem(
                        selected = currentScreen is AppScreen.Contests,
                        onClick = { viewModel.navigateTo(AppScreen.Home) },
                        icon = { Icon(Icons.Default.SportsEsports, contentDescription = "Contests") },
                        label = { Text("Contests", fontSize = 11.sp) },
                        colors = getNavColors()
                    )

                    // 3. Leaderboard
                    NavigationBarItem(
                        selected = currentScreen is AppScreen.Leaderboard,
                        onClick = { viewModel.navigateTo(AppScreen.Leaderboard) },
                        icon = { Icon(Icons.Default.Leaderboard, contentDescription = "Leaderboard") },
                        label = { Text("Rankings", fontSize = 11.sp) },
                        colors = getNavColors()
                    )

                    // 4. History
                    NavigationBarItem(
                        selected = currentScreen is AppScreen.History,
                        onClick = { viewModel.navigateTo(AppScreen.History) },
                        icon = { Icon(Icons.Default.History, contentDescription = "History") },
                        label = { Text("History", fontSize = 11.sp) },
                        colors = getNavColors()
                    )

                    // 5. Profile or Admin
                    NavigationBarItem(
                        selected = currentScreen is AppScreen.Profile || currentScreen is AppScreen.AdminDashboard,
                        onClick = {
                            if (user.role == UserRole.ADMIN) {
                                viewModel.navigateTo(AppScreen.AdminDashboard)
                            } else {
                                viewModel.navigateTo(AppScreen.Profile)
                            }
                        },
                        icon = {
                            Icon(
                                if (user.role == UserRole.ADMIN) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                                contentDescription = "Profile"
                            )
                        },
                        label = { Text(if (user.role == UserRole.ADMIN) "Admin" else "Profile", fontSize = 11.sp) },
                        colors = getNavColors()
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is AppScreen.Home, is AppScreen.Contests -> HomeScreen(viewModel = viewModel)
                is AppScreen.ContestDetail -> ContestDetailScreen(contest = screen.contest, viewModel = viewModel)
                is AppScreen.Matchmaking -> MatchmakingScreen(contest = screen.contest, viewModel = viewModel)
                is AppScreen.ActiveGameRoom -> ActiveGameRoomScreen(match = screen.match, viewModel = viewModel)
                is AppScreen.MatchResult -> MatchResultScreen(match = screen.match, viewModel = viewModel)
                is AppScreen.Leaderboard -> LeaderboardScreen(viewModel = viewModel)
                is AppScreen.History -> MatchHistoryScreen(viewModel = viewModel)
                is AppScreen.Profile -> ProfileScreen(viewModel = viewModel)
                is AppScreen.DisputeReport -> DisputeReportScreen(matchId = screen.matchId, viewModel = viewModel)
                is AppScreen.AdminLogin -> AdminLoginScreen(viewModel = viewModel)
                is AppScreen.AdminDashboard -> AdminDashboardScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun getNavColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = AccentCyan,
    selectedTextColor = AccentCyan,
    indicatorColor = PrimaryPurple.copy(alpha = 0.35f),
    unselectedIconColor = TextMuted,
    unselectedTextColor = TextMuted
)
