package com.example.firstprototype.navigation

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.firstprototype.data.SharedItem
import com.example.firstprototype.data.ItemStatus
import com.example.firstprototype.ui.screens.*
import java.time.LocalDate

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("PestaSharePrefs", Context.MODE_PRIVATE) }

    var isLoggedIn by remember { mutableStateOf(false) }
    var userEmail by remember { mutableStateOf("") }
    var userPoints by remember { mutableStateOf(sharedPreferences.getInt("USER_POINTS", 100)) }
    var selectedItem by remember { mutableStateOf<SharedItem?>(null) }

    // Lista global de artículos compartidos
    val itemsList = remember {
        mutableStateListOf(
            SharedItem(id = 1, name = "Modern Desk Lamp", owner = "Sarah Chen", category = "Electronics", status = ItemStatus.AVAILABLE, pointsValue = 30, createdAt = LocalDate.now()),
            SharedItem(id = 2, name = "Non-stick Frying Pan", owner = "James Wilson", category = "Kitchen", status = ItemStatus.RECYCLE_SUGGESTED, pointsValue = 15, createdAt = LocalDate.now().minusDays(35))
        )
    }

    // Lista global de chats dinámicos en tiempo real
    val chatsList = remember {
        mutableStateListOf<ChatMessage>(
            ChatMessage(1, "Welcome Guide", "PestaShare Team", "Welcome to Pestalozzistraße! Start sharing sustainably.", "1d ago")
        )
    }

    // Lista global de historial de puntos en tiempo real
    val historyList = remember {
        mutableStateListOf<HistoryLog>(
            HistoryLog(1, "Initial Balance Setup", "+100 pts", true)
        )
    }

    LaunchedEffect(userPoints) { sharedPreferences.edit().putInt("USER_POINTS", userPoints).apply() }

    if (!isLoggedIn) {
        LoginScreen(onLoginClick = { email ->
            userEmail = email
            isLoggedIn = true
        })
    } else {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        Scaffold(
            bottomBar = {
                NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
                    val items = listOf(
                        Triple("home", "Home", Icons.Outlined.Home to Icons.Filled.Home),
                        Triple("add_item", "Add", Icons.Outlined.AddCircleOutline to Icons.Filled.AddCircle),
                        Triple("rewards", "Market", Icons.Outlined.CardMembership to Icons.Filled.CardMembership),
                        Triple("inbox", "Activity", Icons.Outlined.Forum to Icons.Filled.Forum),
                        Triple("profile", "Profile", Icons.Outlined.Person to Icons.Filled.Person)
                    )

                    items.forEach { (route, label, icons) ->
                        val selected = currentDestination?.hierarchy?.any { it.route == route } == true
                        NavigationBarItem(
                            icon = { Icon(imageVector = if (selected) icons.second else icons.first, contentDescription = label, modifier = Modifier.size(22.dp)) },
                            label = { Text(label, fontSize = 10.sp) },
                            selected = selected,
                            onClick = {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF1E70F0), selectedTextColor = Color(0xFF1E70F0),
                                unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray, indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        ) { paddingValues ->
            NavHost(navController = navController, startDestination = "home", modifier = Modifier.padding(paddingValues)) {
                composable("home") {
                    HomeScreen(items = itemsList, userPoints = userPoints, onItemClick = { item -> selectedItem = item; navController.navigate("item_detail") })
                }
                composable("add_item") {
                    AddItemScreen(
                        onBack = { navController.navigate("home") { popUpTo("home") { inclusive = false } } },
                        onPostItem = { name, description, uri ->
                            val ownerName = userEmail.substringBefore("@").replace(".", " ").replace("_", " ").split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
                            val newId = itemsList.size + 1
                            itemsList.add(SharedItem(id = newId, name = name, owner = ownerName, category = "General", imageUri = uri, status = ItemStatus.AVAILABLE, pointsValue = 50, createdAt = LocalDate.now()))

                            historyList.add(0, HistoryLog(historyList.size + 1, "Published '$name'", "0 pts", true))

                            navController.navigate("home") { popUpTo("home") { inclusive = false } }
                        }
                    )
                }
                composable("rewards") {
                    RewardsScreen(
                        userPoints = userPoints,
                        onRedeemReward = { cost ->
                            userPoints -= cost
                            historyList.add(0, HistoryLog(historyList.size + 1, "Redeemed Campus Reward", "-$cost pts", false))
                        }
                    )
                }
                composable("inbox") {
                    InboxScreen(chats = chatsList, history = historyList)
                }
                composable("profile") {
                    ProfileScreen(userEmail = userEmail, userPoints = userPoints, onLogout = { isLoggedIn = false; userEmail = "" })
                }
                composable("item_detail") {
                    selectedItem?.let { item ->
                        ItemDetailScreen(
                            item = item,
                            userPoints = userPoints, // Pasa el balance en tiempo real de forma segura
                            onBack = { navController.popBackStack() },
                            onActionSuccess = { pointsEffect, customMessage ->
                                userPoints += pointsEffect

                                val index = itemsList.indexOfFirst { it.id == item.id }
                                if (index != -1) {
                                    val newStatus = if (pointsEffect > 0) ItemStatus.BORROWED else ItemStatus.REQUESTED
                                    itemsList[index] = itemsList[index].copy(status = newStatus)
                                }

                                chatsList.add(0, ChatMessage(chatsList.size + 1, item.name, item.owner, customMessage, "Just now"))

                                val prefix = if (pointsEffect >= 0) "+$pointsEffect" else "$pointsEffect"
                                historyList.add(0, HistoryLog(historyList.size + 1, "Operation on '${item.name}'", "$prefix pts", pointsEffect >= 0))

                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = false }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}