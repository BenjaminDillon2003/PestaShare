package com.example.firstprototype.navigation

import android.content.Context
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.firstprototype.data.SharedItem
import com.example.firstprototype.data.ItemStatus
import com.example.firstprototype.ui.screens.*
import java.time.LocalDate

/**
 * Main navigation component that manages the app's routing and global state.
 * It handles authentication flow, bottom navigation, and screen transitions.
 */
@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("PestaSharePrefs", Context.MODE_PRIVATE) }

    // --- GLOBAL STATE ---
    // These states are managed here to ensure consistency across different screens.
    var isLoggedIn by remember { mutableStateOf(false) }
    var userEmail by remember { mutableStateOf("") }
    var userPoints by remember { mutableStateOf(sharedPreferences.getInt("USER_POINTS", 100)) }
    
    // States for temporary data during transitions
    var selectedItem by remember { mutableStateOf<SharedItem?>(null) }
    var editingItem by remember { mutableStateOf<SharedItem?>(null) }

    // Helper to format the user's display name based on their email
    val currentUserDisplayName = remember(userEmail) {
        if (userEmail.contains("@")) {
            userEmail.substringBefore("@")
                .replace(".", " ")
                .replace("_", " ")
                .split(" ")
                .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
        } else userEmail
    }

    // Global list of shared items (mock database)
    val itemsList = remember {
        mutableStateListOf(
            SharedItem(id = 1, name = "Modern Desk Lamp", owner = "Sarah Chen", category = "Electronics", location = "Block A, 201", status = ItemStatus.AVAILABLE, pointsValue = 30, createdAt = LocalDate.now()),
            SharedItem(id = 2, name = "Non-stick Frying Pan", owner = "James Wilson", category = "Kitchen", location = "Laundry Room", status = ItemStatus.RECYCLE_SUGGESTED, pointsValue = 15, createdAt = LocalDate.now().minusDays(35))
        )
    }

    // Global list of chat messages
    val chatsList = remember {
        mutableStateListOf<ChatMessage>(
            ChatMessage(1, "Welcome Guide", "PestaShare Team", "Welcome to Pestalozzistraße! Start sharing sustainably.", "1d ago")
        )
    }

    // Global points transaction history
    val historyList = remember {
        mutableStateListOf<HistoryLog>(
            HistoryLog(1, "Initial Balance Setup", "+100 pts", true)
        )
    }

    // Persist points to shared preferences whenever they change
    LaunchedEffect(userPoints) { sharedPreferences.edit().putInt("USER_POINTS", userPoints).apply() }

    // --- AUTHENTICATION GATE ---
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
                // Main Bottom Navigation Bar
                NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
                    val items = listOf(
                        Triple("home", "Home", Icons.Outlined.Home to Icons.Filled.Home),
                        Triple("add_item", "Add", Icons.Outlined.AddCircleOutline to Icons.Filled.AddCircle),
                        Triple("rewards", "Market", Icons.Outlined.CardMembership to Icons.Filled.CardMembership),
                        Triple("inbox", "Activity", Icons.Outlined.Forum to Icons.Filled.Forum),
                        Triple("profile", "Profile", Icons.Outlined.Person to Icons.Filled.Person)
                    )

                    items.forEach { (route, label, icons) ->
                        val selected = currentDestination?.hierarchy?.any { it.route?.startsWith(route) == true } == true
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
            // --- NAVIGATION HOST ---
            NavHost(navController = navController, startDestination = "home", modifier = Modifier.padding(paddingValues)) {
                
                // Dashboard with available items
                composable("home") {
                    HomeScreen(
                        items = itemsList.filter { it.status != ItemStatus.GIVEN },
                        userPoints = userPoints,
                        onItemClick = { item -> selectedItem = item; navController.navigate("item_detail") }
                    )
                }

                // Screen to create a new item listing
                composable("add_item") {
                    AddItemScreen(
                        onBack = { navController.popBackStack() },
                        onPostItem = { name, description, category, location, uri ->
                            val newId = (itemsList.maxOfOrNull { it.id } ?: 0) + 1
                            itemsList.add(SharedItem(id = newId, name = name, description = description, owner = currentUserDisplayName, category = category, location = location, imageUri = uri, status = ItemStatus.AVAILABLE, pointsValue = 50, createdAt = LocalDate.now()))
                            historyList.add(0, HistoryLog(historyList.size + 1, "Published '$name'", "+0 pts", true))
                            navController.navigate("home") { popUpTo("home") { inclusive = false } }
                        }
                    )
                }

                // Screen to edit an existing item
                composable("edit_item") {
                    AddItemScreen(
                        initialItem = editingItem,
                        onBack = { navController.popBackStack() },
                        onPostItem = { name, description, category, location, uri ->
                            editingItem?.let { oldItem ->
                                val index = itemsList.indexOfFirst { it.id == oldItem.id }
                                if (index != -1) {
                                    itemsList[index] = itemsList[index].copy(name = name, description = description, category = category, location = location, imageUri = uri)
                                }
                            }
                            editingItem = null
                            navController.popBackStack()
                        }
                    )
                }

                // Campus rewards market
                composable("rewards") {
                    RewardsScreen(
                        userPoints = userPoints,
                        onRedeemReward = { cost ->
                            userPoints -= cost
                            historyList.add(0, HistoryLog(historyList.size + 1, "Redeemed Campus Reward", "-$cost pts", false))
                        }
                    )
                }

                // Activity Center: Messages, My Items, and History
                composable(
                    route = "inbox?tab={tab}",
                    arguments = listOf(navArgument("tab") { type = NavType.IntType; defaultValue = 0 })
                ) { backStackEntry ->
                    val tab = backStackEntry.arguments?.getInt("tab") ?: 0
                    InboxScreen(
                        chats = chatsList,
                        history = historyList,
                        myItems = itemsList.filter { it.owner == currentUserDisplayName },
                        initialTab = tab,
                        onMarkAsGiven = { itemId ->
                            val index = itemsList.indexOfFirst { it.id == itemId }
                            if (index != -1) {
                                itemsList[index] = itemsList[index].copy(status = ItemStatus.GIVEN)
                                historyList.add(0, HistoryLog(historyList.size + 1, "Item '${itemsList[index].name}' shared!", "+0 pts", true))
                            }
                        },
                        onRemoveItem = { itemId ->
                            itemsList.removeIf { it.id == itemId }
                        },
                        onEditItem = { item ->
                            editingItem = item
                            navController.navigate("edit_item")
                        }
                    )
                }

                // User profile and account management
                composable("profile") {
                    ProfileScreen(
                        userEmail = userEmail,
                        userPoints = userPoints,
                        sharedItemsCount = itemsList.count { it.owner == currentUserDisplayName },
                        onManageItems = { navController.navigate("inbox?tab=1") },
                        onLogout = { isLoggedIn = false; userEmail = "" }
                    )
                }

                // Detailed view of a specific item
                composable("item_detail") {
                    selectedItem?.let { item ->
                        ItemDetailScreen(
                            item = item,
                            userPoints = userPoints,
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
                                navController.navigate("home") { popUpTo("home") { inclusive = false } }
                            }
                        )
                    }
                }
            }
        }
    }
}
