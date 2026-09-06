package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.components.EsportsBottomNavigation
import com.example.ui.components.ToastNotification
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.EditProfileScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.MatchDetailScreen
import com.example.ui.screens.MatchesScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.SupportScreen
import com.example.ui.screens.TournamentDetailScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.ArenaBgDark
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ArenaViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        ArenaApp()
      }
    }
  }
}

@Composable
fun ArenaApp(viewModel: ArenaViewModel = viewModel()) {
  val navController = rememberNavController()
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route ?: "splash"

  val currentUser by viewModel.currentUser.collectAsState()
  val toastMessage by viewModel.toastMessage.collectAsState()

  // Auto-dismiss toast
  LaunchedEffect(toastMessage) {
    if (toastMessage != null) {
      delay(3000L)
      viewModel.clearToast()
    }
  }

  // Top level tabs where bottom navigation should be visible
  val showBottomNav = currentRoute in listOf("home", "matches", "leaderboard", "profile")

  Scaffold(
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    bottomBar = {
      if (showBottomNav) {
        EsportsBottomNavigation(
          currentRoute = currentRoute,
          onNavigate = { route ->
            if (currentRoute != route) {
              navController.navigate(route) {
                popUpTo("home") {
                  saveState = true
                }
                launchSingleTop = true
                restoreState = true
              }
            }
          }
        )
      }
    },
    modifier = Modifier
      .fillMaxSize()
      .background(ArenaBgDark)
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(bottom = if (showBottomNav) innerPadding.calculateBottomPadding() else androidx.compose.ui.unit.Dp(0f))
    ) {
      NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = Modifier.fillMaxSize()
      ) {
        // Splash
        composable("splash") {
          SplashScreen(
            onComplete = {
              if (currentUser != null) {
                navController.navigate("home") {
                  popUpTo("splash") { inclusive = true }
                }
              } else {
                navController.navigate("auth") {
                  popUpTo("splash") { inclusive = true }
                }
              }
            }
          )
        }

        // Auth (Login / Register)
        composable("auth") {
          AuthScreen(
            viewModel = viewModel,
            onAuthSuccess = {
              navController.navigate("home") {
                popUpTo("auth") { inclusive = true }
              }
            }
          )
        }

        // Home (Dashboard & Discovery)
        composable("home") {
          HomeScreen(
            viewModel = viewModel,
            onNavigateToTournament = { tournamentId ->
              navController.navigate("tournament_detail/$tournamentId")
            },
            onNavigateToMatchesWithTab = { tabIndex ->
              viewModel.setMatchesTab(tabIndex)
              navController.navigate("matches")
            },
            onNavigateToLeaderboard = {
              navController.navigate("leaderboard")
            },
            onNavigateToNotifications = {
              navController.navigate("notifications")
            },
            onNavigateToWallet = {
              navController.navigate("wallet")
            },
            onNavigateToProfile = {
              navController.navigate("profile")
            }
          )
        }

        // Matches
        composable("matches") {
          MatchesScreen(
            viewModel = viewModel,
            onNavigateToMatchDetail = { matchId ->
              navController.navigate("match_detail/$matchId")
            },
            onNavigateToTournaments = {
              navController.navigate("home")
            },
            onNavigateToNotifications = {
              navController.navigate("notifications")
            },
            onNavigateToWallet = {
              navController.navigate("wallet")
            },
            onNavigateToProfile = {
              navController.navigate("profile")
            }
          )
        }

        // Leaderboard
        composable("leaderboard") {
          LeaderboardScreen(
            viewModel = viewModel,
            onNavigateToNotifications = {
              navController.navigate("notifications")
            },
            onNavigateToWallet = {
              navController.navigate("wallet")
            },
            onNavigateToProfile = {
              navController.navigate("profile")
            }
          )
        }

        // Profile
        composable("profile") {
          ProfileScreen(
            viewModel = viewModel,
            onEditProfile = {
              navController.navigate("edit_profile")
            },
            onNavigateToAdmin = {
              navController.navigate("admin")
            },
            onNavigateToSettings = {
              navController.navigate("settings")
            },
            onNavigateToSupport = {
              navController.navigate("support")
            },
            onNavigateToNotifications = {
              navController.navigate("notifications")
            },
            onNavigateToWallet = {
              navController.navigate("wallet")
            },
            onLogout = {
              viewModel.logout {
                navController.navigate("auth") {
                  popUpTo(0) { inclusive = true }
                }
              }
            }
          )
        }

        // Tournament Detail
        composable(
          route = "tournament_detail/{tournamentId}",
          arguments = listOf(navArgument("tournamentId") { type = NavType.StringType })
        ) { backStackEntry ->
          val tournamentId = backStackEntry.arguments?.getString("tournamentId") ?: ""
          TournamentDetailScreen(
            tournamentId = tournamentId,
            viewModel = viewModel,
            onBack = { navController.popBackStack() },
            onNavigateToMatches = {
              navController.navigate("matches")
            }
          )
        }

        // Match Detail (Room ID, Password, Results)
        composable(
          route = "match_detail/{matchId}",
          arguments = listOf(navArgument("matchId") { type = NavType.StringType })
        ) { backStackEntry ->
          val matchId = backStackEntry.arguments?.getString("matchId") ?: ""
          MatchDetailScreen(
            matchId = matchId,
            viewModel = viewModel,
            onBack = { navController.popBackStack() }
          )
        }

        // Wallet (Virtual Coins, Non-Cash Disclosures, History)
        composable("wallet") {
          WalletScreen(
            viewModel = viewModel,
            onBack = { navController.popBackStack() }
          )
        }

        // Notifications
        composable("notifications") {
          NotificationsScreen(
            viewModel = viewModel,
            onBack = { navController.popBackStack() }
          )
        }

        // Edit Profile
        composable("edit_profile") {
          EditProfileScreen(
            viewModel = viewModel,
            onBack = { navController.popBackStack() }
          )
        }

        // Settings & Policies
        composable("settings") {
          SettingsScreen(
            viewModel = viewModel,
            onBack = { navController.popBackStack() }
          )
        }

        // Support & Tickets
        composable("support") {
          SupportScreen(
            viewModel = viewModel,
            onBack = { navController.popBackStack() }
          )
        }

        // Admin Console
        composable("admin") {
          AdminDashboardScreen(
            viewModel = viewModel,
            onBack = { navController.popBackStack() }
          )
        }
      }

      // Global Toast Notification
      ToastNotification(
        message = toastMessage,
        onDismiss = { viewModel.clearToast() },
        modifier = Modifier.align(Alignment.TopCenter)
      )
    }
  }
}
