package fm.mrc.rollpal

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.*
import fm.mrc.rollpal.ui.theme.RollPalTheme
import fm.mrc.rollpal.ui.screens.*
import fm.mrc.rollpal.theme.ThemeManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val themeManager = ThemeManager(this)
        
        try {
            android.util.Log.d("MainActivity", "onCreate called")
            enableEdgeToEdge()
            setContent {
                val isDarkTheme by themeManager.isDarkMode.collectAsState(initial = isSystemInDarkTheme())
                
                RollPalTheme(
                    darkTheme = isDarkTheme
                ) {
                    RollPalApp()
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Error in onCreate", e)
            e.printStackTrace()
        }
    }
}

@Composable
fun RollPalApp() {
    val navController = rememberNavController()
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                val currentDestination = navController
                    .currentBackStackEntryAsState().value?.destination
                
                NavigationBarItem(
                    selected = currentDestination?.hierarchy?.any { it.route == "attendance" } == true,
                    onClick = { navController.navigate("attendance") {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }},
                    icon = { Icon(Icons.Default.CalendarToday, contentDescription = "Attendance") },
                    label = { Text("Attendance") }
                )
                
                NavigationBarItem(
                    selected = currentDestination?.hierarchy?.any { it.route == "semester" } == true,
                    onClick = { navController.navigate("semester") {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }},
                    icon = { Icon(Icons.Default.School, contentDescription = "Semester") },
                    label = { Text("Semester") }
                )
                
                NavigationBarItem(
                    selected = currentDestination?.hierarchy?.any { it.route == "stats" } == true,
                    onClick = { navController.navigate("stats") {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }},
                    icon = { Icon(Icons.Default.PieChart, contentDescription = "Statistics") },
                    label = { Text("Stats") }
                )
                
                NavigationBarItem(
                    selected = currentDestination?.hierarchy?.any { it.route == "settings" } == true,
                    onClick = { navController.navigate("settings") {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }},
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "attendance",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("attendance") { AttendanceScreen() }
            composable("semester") { SemesterScreen() }
            composable("stats") { StatsScreen() }
            composable("settings") { SettingsScreen() }
        }
    }
}