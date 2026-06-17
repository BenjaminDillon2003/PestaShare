package com.example.firstprototype.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firstprototype.data.SharedItem
import com.example.firstprototype.data.ItemStatus
import com.example.firstprototype.ui.theme.*

/**
 * Data model for a chat message preview in the Activity Center.
 */
@Immutable
data class ChatMessage(val id: Int, val itemName: String, val contactName: String, val lastMessage: String, val time: String)

/**
 * Data model for an entry in the user's point history log.
 */
@Immutable
data class HistoryLog(val id: Int, val description: String, val points: String, val isPositive: Boolean)

/**
 * The Activity Center screen which consolidates chats, item management, and point history.
 * 
 * @param chats List of active chat conversations.
 * @param history List of point transactions.
 * @param myItems List of items owned by the current user.
 * @param initialTab The tab index to open by default (0: Chats, 1: Items, 2: Points).
 * @param onMarkAsGiven Callback to mark an item as "Given Away".
 * @param onRemoveItem Callback to delete an item listing.
 * @param onEditItem Callback to trigger the edit flow for an item.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxScreen(
    chats: List<ChatMessage>,
    history: List<HistoryLog>,
    myItems: List<SharedItem>,
    initialTab: Int = 0,
    onMarkAsGiven: (Int) -> Unit,
    onRemoveItem: (Int) -> Unit,
    onEditItem: (SharedItem) -> Unit
) {
    // Current active tab state, initialized from the navigation argument
    var selectedTab by remember(initialTab) { mutableIntStateOf(initialTab) }
    val tabs = listOf("My Chats", "My Items", "Points")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSurface)
    ) {
        // --- HEADER ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(PestaBlue.copy(alpha = 0.1f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Forum, contentDescription = null, tint = PestaBlue, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Text(text = "Activity Center", style = MaterialTheme.typography.headlineMedium, color = TextPrimary, fontWeight = FontWeight.Black)
        }

        // --- TABS CONTROL ---
        SecondaryScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = PestaBlue,
            edgePadding = 24.dp,
            divider = {},
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp) }
                )
            }
        }

        // --- DYNAMIC CONTENT RENDERER ---
        when (selectedTab) {
            0 -> ChatsList(chats)
            1 -> MyItemsList(myItems, onMarkAsGiven, onRemoveItem, onEditItem)
            2 -> HistoryList(history)
        }
    }
}

/**
 * Displays a list of active chat previews.
 */
@Composable
fun ChatsList(chats: List<ChatMessage>) {
    if (chats.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No chats active yet.", color = TextMuted)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(chats) { chat ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = chat.contactName, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
                            Text(text = chat.time, fontSize = 11.sp, color = TextMuted)
                        }
                        Text(text = "Regarding: ${chat.itemName}", fontSize = 12.sp, color = PestaBlue, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 2.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(color = BackgroundSurface, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.ChatBubbleOutline, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = chat.lastMessage, fontSize = 13.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Displays the items owned by the user with management controls.
 */
@Composable
fun MyItemsList(
    items: List<SharedItem>,
    onMarkAsGiven: (Int) -> Unit,
    onRemoveItem: (Int) -> Unit,
    onEditItem: (SharedItem) -> Unit
) {
    if (items.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("You haven't posted any items yet.", color = TextMuted)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = item.name, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                                Text(text = item.category, fontSize = 12.sp, color = TextSecondary)
                            }
                            
                            // Status label selection logic
                            val statusInfo = when(item.status) {
                                ItemStatus.GIVEN -> "Given Away" to Color.Gray
                                ItemStatus.REQUESTED -> "Requested" to PestaBlue
                                else -> "Active" to PestaGreenDark
                            }
                            
                            Surface(
                                color = statusInfo.second.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = statusInfo.first,
                                    color = statusInfo.second,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Management action buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (item.status != ItemStatus.GIVEN) {
                                Button(
                                    onClick = { onMarkAsGiven(item.id) },
                                    modifier = Modifier.weight(1.2f),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(0.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PestaGreen)
                                ) {
                                    Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Mark Given", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                
                                OutlinedButton(
                                    onClick = { onEditItem(item) },
                                    modifier = Modifier.weight(0.8f),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Icon(Icons.Rounded.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Edit", fontSize = 11.sp)
                                }
                            }
                            
                            IconButton(
                                onClick = { onRemoveItem(item.id) },
                                colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFFFEE2E2), contentColor = Color(0xFFDC2626)),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(Icons.Rounded.DeleteOutline, contentDescription = "Delete", modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Displays the point transaction history of the user.
 */
@Composable
fun HistoryList(history: List<HistoryLog>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(history) { log ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(BackgroundSurface, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.History, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = log.description, fontSize = 14.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                    }
                    Text(
                        text = log.points,
                        fontWeight = FontWeight.Black,
                        color = if (log.isPositive) PestaGreenDark else Color.Red,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
