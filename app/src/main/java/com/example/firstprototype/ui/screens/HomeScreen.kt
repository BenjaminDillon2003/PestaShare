package com.example.firstprototype.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.firstprototype.data.SharedItem
import com.example.firstprototype.data.ItemStatus
import com.example.firstprototype.ui.theme.*

/**
 * The main landing screen of the application where users can discover shared items.
 * Updated to include a toggle between "Offers" (Items for grab) and "Wishlist" (Community needs).
 * 
 * @param items The complete list of items to display.
 * @param userPoints Current point balance of the user.
 * @param onItemClick Callback triggered when an item card is tapped.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    items: List<SharedItem>,
    userPoints: Int,
    onItemClick: (SharedItem) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var showRequests by remember { mutableStateOf(false) } // Toggle for Wishlist
    
    val categories = listOf("All", "Electronics", "Kitchen", "Books", "General")

    // Dynamic filtering logic
    val filteredItems = items.filter { item ->
        val matchesSearch = item.name.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "All" || item.category.equals(selectedCategory, ignoreCase = true)
        val matchesType = item.isRequest == showRequests
        matchesSearch && matchesCategory && matchesType
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- HEADER SECTION ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, top = 16.dp, end = 24.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Brush.linearGradient(listOf(PestaGreen, Color(0xFF34D399))), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Eco, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "PestaShare", style = MaterialTheme.typography.headlineMedium, color = TextPrimary, fontWeight = FontWeight.Black)
            }

            Surface(color = Color.White, shape = RoundedCornerShape(14.dp), shadowElevation = 3.dp) {
                Row(modifier = Modifier.padding(start = 12.dp, top = 6.dp, end = 12.dp, bottom = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.MonetizationOn, contentDescription = null, tint = PestaGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "$userPoints pts", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // --- OFFERS / WISHLIST TOGGLE ---
        Row(
            modifier = Modifier
                .padding(start = 24.dp, top = 8.dp, end = 24.dp, bottom = 8.dp)
                .fillMaxWidth()
                .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            listOf(false to "Available Items", true to "Community Needs").forEach { (type, label) ->
                val selected = showRequests == type
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selected) Color.White else Color.Transparent)
                        .clickable { showRequests = type }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (selected) PestaBlue else TextSecondary,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // --- SEARCH BAR ---
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(if (showRequests) "Search community needs..." else "Search available items...", color = TextMuted) },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = TextSecondary) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, top = 8.dp, end = 24.dp, bottom = 12.dp),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
        )

        // --- CATEGORY FILTERS ---
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                val isSelected = selectedCategory == category
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = category },
                    label = { Text(category, fontWeight = FontWeight.Bold) },
                    shape = RoundedCornerShape(10.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PestaBlue,
                        selectedLabelColor = Color.White,
                        disabledContainerColor = Color.White,
                        labelColor = TextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(enabled = true, selected = isSelected, borderColor = BorderLight)
                )
            }
        }

        // --- ITEMS LIST ---
        if (filteredItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                Text(if (showRequests) "No community needs found." else "No items found.", color = TextMuted)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().weight(1f),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredItems) { item ->
                    DiscoveryItemCard(item = item, onClick = { onItemClick(item) })
                }
            }
        }
    }
}

/**
 * Enhanced item card that adapts its UI if it's an offer or a request.
 */
@Composable
fun DiscoveryItemCard(item: SharedItem, onClick: () -> Unit) {
    val (badgeText, badgeBgColor, badgeTextColor) = if (item.isRequest) {
        Triple("LOOKING FOR", Color(0xFFFEE2E2), Color(0xFFB91C1C))
    } else {
        when (item.status) {
            ItemStatus.AVAILABLE -> Triple("Available", Color(0xFFE6F4EA), PestaGreenDark)
            ItemStatus.REQUESTED -> Triple("Requested 💬", Color(0xFFE8F0FE), PestaBlueDark)
            ItemStatus.BORROWED -> Triple("Borrowed ♻️", Color(0xFFF1F3F5), TextSecondary)
            ItemStatus.RECYCLE_SUGGESTED -> Triple("Recycle Alert ⚠️", Color(0xFFFEF3C7), Color(0xFFD97706))
            ItemStatus.GIVEN -> Triple("Given ✅", Color(0xFFF3F4F6), TextSecondary)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(if (item.imageUri != null || !item.isRequest) 180.dp else 100.dp).padding(8.dp).clip(RoundedCornerShape(16.dp))) {
                if (item.imageUri != null) {
                    Image(painter = rememberAsyncImagePainter(item.imageUri), contentDescription = item.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else if (!item.isRequest) {
                    Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1)))), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(36.dp), tint = Color.White)
                    }
                } else {
                    // Requests without images get a nice gradient background
                    Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFFEFF6FF), Color(0xFFDBEAFE)))), contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null, modifier = Modifier.size(32.dp), tint = PestaBlue)
                    }
                }
                Surface(modifier = Modifier.align(Alignment.TopEnd).padding(10.dp), color = badgeBgColor, shape = RoundedCornerShape(8.dp)) {
                    Text(text = badgeText, color = badgeTextColor, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 4.dp))
                }
            }

            Column(modifier = Modifier.padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 12.dp)) {
                Text(text = item.name, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                Text(text = item.category, fontSize = 12.sp, color = TextSecondary, modifier = Modifier.padding(top = 2.dp))

                if (item.isRequest && item.description.isNotEmpty()) {
                    Text(
                        text = item.description, 
                        fontSize = 13.sp, 
                        color = TextSecondary, 
                        maxLines = 2, 
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(24.dp).background(Color(0xFFF1F5F9), CircleShape), contentAlignment = Alignment.Center) {
                            Text(text = if (item.owner.isNotEmpty()) item.owner.take(1).uppercase() else "U", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = item.owner, fontSize = 13.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                    }
                    if (!item.isRequest) {
                        Surface(color = Color(0xFFE6F4EA), shape = RoundedCornerShape(6.dp)) {
                            Text(text = "${item.pointsValue} pts", color = PestaGreenDark, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 6.dp, top = 3.dp, end = 6.dp, bottom = 3.dp))
                        }
                    } else {
                        Text(text = "Reward Offered", color = PestaGreenDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
