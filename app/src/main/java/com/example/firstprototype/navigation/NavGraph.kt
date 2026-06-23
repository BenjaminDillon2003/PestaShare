package com.example.firstprototype.navigation

import android.content.Context
import android.util.Log
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
import com.example.firstprototype.data.UserProfile
import com.example.firstprototype.data.HistoryLog
import com.example.firstprototype.ui.screens.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import java.time.LocalDate

/**
 * Main navigation component that manages the app's routing and global state.
 * Migrated to Firebase Firestore for real-time data synchronization.
 */
@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    val db = remember { FirebaseFirestore.getInstance() }

    // --- GLOBAL STATE ---
    var isLoggedIn by remember { mutableStateOf(auth.currentUser != null) }
    var userEmail by remember { mutableStateOf(auth.currentUser?.email ?: "") }
    var userPoints by remember { mutableIntStateOf(100) }

    var selectedItem by remember { mutableStateOf<SharedItem?>(null) }
    var editingItem by remember { mutableStateOf<SharedItem?>(null) }

    val currentUserDisplayName = remember(userEmail) {
        if (userEmail.contains("@")) {
            userEmail.substringBefore("@")
                .replace(".", " ")
                .replace("_", " ")
                .split(" ")
                .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
        } else userEmail
    }

    val itemsList = remember { mutableStateListOf<SharedItem>() }
    val chatsList = remember { mutableStateListOf<ChatMessage>() }
    val historyList = remember { mutableStateListOf<HistoryLog>() }

    // --- FIRESTORE LISTENERS ---
    
    // Listen for Items
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            val listener = db.collection("items")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w("Firestore", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        itemsList.clear()
                        for (doc in snapshot) {
                            val item = doc.toObject<SharedItem>().copy(id = doc.id)
                            itemsList.add(item)
                        }
                    }
                }
        }
    }

    // Listen for User Profile (Points)
    LaunchedEffect(userEmail) {
        if (userEmail.isNotEmpty()) {
            db.collection("users").document(userEmail)
                .addSnapshotListener { snapshot, e ->
                    if (snapshot != null && snapshot.exists()) {
                        val profile = snapshot.toObject<UserProfile>()
                        userPoints = profile?.points ?: 100
                    } else {
                        // Create profile if it doesn't exist
                        db.collection("users").document(userEmail)
                            .set(UserProfile(email = userEmail, displayName = currentUserDisplayName, points = 100))
                    }
                }
        }
    }

    // Listen for History
    LaunchedEffect(userEmail) {
        if (userEmail.isNotEmpty()) {
            db.collection("users").document(userEmail).collection("history")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, e ->
                    if (snapshot != null) {
                        historyList.clear()
                        for (doc in snapshot) {
                            historyList.add(doc.toObject<HistoryLog>().copy(id = doc.id))
                        }
                    }
                }
        }
    }

    if (!isLoggedIn) {
        LoginScreen(onLoginSuccess = { email ->
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
            NavHost(navController = navController, startDestination = "home", modifier = Modifier.padding(paddingValues)) {
                composable("home") {
                    HomeScreen(
                        items = itemsList.filter { it.status != ItemStatus.GIVEN },
                        userPoints = userPoints,
                        onItemClick = { item -> selectedItem = item; navController.navigate("item_detail") }
                    )
                }
                composable("add_item") {
                    AddItemScreen(
                        onBack = { navController.popBackStack() },
                        onPostItem = { name, description, category, location, isRequest, uri ->
                            val newItem = SharedItem(
                                name = name,
                                description = description,
                                owner = currentUserDisplayName,
                                ownerEmail = userEmail,
                                category = category,
                                location = location,
                                isRequest = isRequest,
                                imageUriString = uri?.toString(),
                                status = ItemStatus.AVAILABLE
                            )
                            db.collection("items").add(newItem)
                                .addOnSuccessListener {
                                    val log = HistoryLog(description = "Published '$name'", points = "+0 pts", isPositive = true)
                                    db.collection("users").document(userEmail).collection("history").add(log)
                                }
                            navController.navigate("home") { popUpTo("home") { inclusive = false } }
                        }
                    )
                }
                composable("edit_item") {
                    AddItemScreen(
                        initialItem = editingItem,
                        onBack = { navController.popBackStack() },
                        onPostItem = { name, description, category, location, isRequest, uri ->
                            editingItem?.let { oldItem ->
                                val updatedItem = oldItem.copy(
                                    name = name,
                                    description = description,
                                    category = category,
                                    location = location,
                                    isRequest = isRequest,
                                    imageUriString = uri?.toString()
                                )
                                db.collection("items").document(oldItem.id).set(updatedItem)
                            }
                            editingItem = null
                            navController.popBackStack()
                        }
                    )
                }
                composable("rewards") {
                    RewardsScreen(
                        userPoints = userPoints,
                        onRedeemReward = { cost ->
                            val newPoints = userPoints - cost
                            db.collection("users").document(userEmail).update("points", newPoints)
                                .addOnSuccessListener {
                                    val log = HistoryLog(description = "Redeemed Campus Reward", points = "-$cost pts", isPositive = false)
                                    db.collection("users").document(userEmail).collection("history").add(log)
                                }
                        }
                    )
                }
                composable(
                    route = "inbox?tab={tab}",
                    arguments = listOf(navArgument("tab") { type = NavType.IntType; defaultValue = 0 })
                ) { backStackEntry ->
                    val tab = backStackEntry.arguments?.getInt("tab") ?: 0
                    InboxScreen(
                        chats = chatsList,
                        history = historyList,
                        myItems = itemsList.filter { it.ownerEmail == userEmail },
                        initialTab = tab,
                        onMarkAsGiven = { itemId ->
                            db.collection("items").document(itemId.toString()).update("status", ItemStatus.GIVEN)
                                .addOnSuccessListener {
                                    val log = HistoryLog(description = "Item shared!", points = "+0 pts", isPositive = true)
                                    db.collection("users").document(userEmail).collection("history").add(log)
                                }
                        },
                        onRemoveItem = { itemId ->
                            db.collection("items").document(itemId.toString()).delete()
                        },
                        onEditItem = { item ->
                            editingItem = item
                            navController.navigate("edit_item")
                        }
                    )
                }
                composable("profile") {
                    ProfileScreen(
                        userEmail = userEmail,
                        userPoints = userPoints,
                        sharedItemsCount = itemsList.count { it.ownerEmail == userEmail },
                        onManageItems = { navController.navigate("inbox?tab=1") },
                        onLogout = { 
                            auth.signOut()
                            isLoggedIn = false
                            userEmail = "" 
                        }
                    )
                }
                composable("item_detail") {
                    selectedItem?.let { item ->
                        ItemDetailScreen(
                            item = item,
                            userPoints = userPoints,
                            onBack = { navController.popBackStack() },
                            onActionSuccess = { pointsEffect, customMessage ->
                                val newPoints = userPoints + pointsEffect
                                db.collection("users").document(userEmail).update("points", newPoints)
                                
                                val newStatus = if (pointsEffect > 0) ItemStatus.BORROWED else ItemStatus.REQUESTED
                                db.collection("items").document(item.id).update("status", newStatus)
                                
                                val prefix = if (pointsEffect >= 0) "+$pointsEffect" else "$pointsEffect"
                                val log = HistoryLog(description = "Operation on '${item.name}'", points = "$prefix pts", isPositive = pointsEffect >= 0)
                                db.collection("users").document(userEmail).collection("history").add(log)

                                navController.navigate("home") { popUpTo("home") { inclusive = false } }
                            }
                        )
                    }
                }
            }
        }
    }
}
