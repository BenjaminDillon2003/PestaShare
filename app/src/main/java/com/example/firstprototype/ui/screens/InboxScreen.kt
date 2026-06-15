package com.example.firstprototype.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firstprototype.ui.theme.*

data class ChatMessage(val id: Int, val itemName: String, val contactName: String, val lastMessage: String, val time: String)
data class HistoryLog(val id: Int, val description: String, val points: String, val isPositive: Boolean)

@Composable
fun InboxScreen(
    chats: List<ChatMessage>,    // Lee datos dinámicos globales
    history: List<HistoryLog>   // Lee datos dinámicos globales
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("My Chats", "Points History")

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
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = PestaBlue,
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp) }
                )
            }
        }

        // --- RENDERIZADO DINÁMICO ---
        if (selectedTab == 0) {
            if (chats.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No chats active yet.", color = TextMuted)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 24.dp),
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
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp),
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
    }
}