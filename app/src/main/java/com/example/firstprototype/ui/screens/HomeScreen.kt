package com.example.firstprototype.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firstprototype.data.ItemStatus
import com.example.firstprototype.data.SharedItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    var searchQuery by remember { mutableStateOf("") }
    val itemsList = remember {
        mutableStateListOf(
            SharedItem(1, "Bike Air Pump", "Mateo", "Tools"),
            SharedItem(2, "Kitchen Blender", "James", "Kitchen"),
            SharedItem(3, "Drill Machine", "Andrés", "Tools"),
            SharedItem(4, "Camping Tent", "Elena", "Outdoor")
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))

        // Professional Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search in Pestalozzistraße...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = CircleShape,
            colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.Transparent)
        )

        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Available Items", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(itemsList.filter { it.name.contains(searchQuery, ignoreCase = true) }) { item ->
                ItemCard(item)
            }
        }
    }
}

@Composable
fun ItemCard(item: SharedItem) {
    var status by remember { mutableStateOf(item.status) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon
            Surface(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Inventory2,
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = "Lent by ${item.owner}", fontSize = 12.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { status = ItemStatus.REQUESTED },
                    enabled = status == ItemStatus.AVAILABLE,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(if (status == ItemStatus.AVAILABLE) "Request" else "Pending", fontSize = 12.sp)
                }
            }

            // Status Dot
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        if (status == ItemStatus.AVAILABLE) Color.Green else Color.Yellow,
                        CircleShape
                    )
            )
        }
    }
}