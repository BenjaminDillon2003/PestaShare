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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    items: List<SharedItem>,
    userPoints: Int,
    onItemClick: (SharedItem) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Electronics", "Kitchen", "Books", "General")

    // Lógica de filtrado dinámico
    val filteredItems = items.filter { item ->
        val matchesSearch = item.name.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "All" || item.category.equals(selectedCategory, ignoreCase = true)
        matchesSearch && matchesCategory
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- HEADER ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
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
                Text(text = "Discover", style = MaterialTheme.typography.headlineMedium, color = TextPrimary, fontWeight = FontWeight.Black)
            }

            Surface(color = Color.White, shape = RoundedCornerShape(14.dp), shadowElevation = 3.dp) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.MonetizationOn, contentDescription = null, tint = PestaGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "$userPoints pts", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // --- BARRA DE BÚSQUEDA ---
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search items in Pestalozzistraße...", color = TextMuted) },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = TextSecondary) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 12.dp),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
        )

        // --- FILTROS DE CATEGORÍA ---
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            contentPadding = PaddingValues(horizontal = 24.dp),
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

        // --- LISTA ---
        if (filteredItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                Text("No items found matching criteria.", color = TextMuted)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().weight(1f),
                contentPadding = PaddingValues(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredItems) { item ->
                    DiscoveryItemCard(item = item, onClick = { onItemClick(item) })
                }
            }
        }
    }
}

@Composable
fun DiscoveryItemCard(item: SharedItem, onClick: () -> Unit) {
    val (badgeText, badgeBgColor, badgeTextColor) = when (item.status) {
        ItemStatus.AVAILABLE -> Triple("Available", Color(0xFFE6F4EA), PestaGreenDark)
        ItemStatus.REQUESTED -> Triple("Requested 💬", Color(0xFFE8F0FE), PestaBlueDark)
        ItemStatus.BORROWED -> Triple("In Recycling Hub ♻️", Color(0xFFF1F3F5), TextSecondary)
        ItemStatus.RECYCLE_SUGGESTED -> Triple("Recycle Alert ⚠️", Color(0xFFFEF3C7), Color(0xFFD97706))
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(180.dp).padding(8.dp).clip(RoundedCornerShape(16.dp))) {
                if (item.imageUri != null) {
                    Image(painter = rememberAsyncImagePainter(item.imageUri), contentDescription = item.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1)))), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(36.dp), tint = Color.White)
                    }
                }
                Surface(modifier = Modifier.align(Alignment.TopEnd).padding(10.dp), color = badgeBgColor, shape = RoundedCornerShape(8.dp)) {
                    Text(text = badgeText, color = badgeTextColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(text = item.name, style = MaterialTheme.typography.titleLarge, color = TextPrimary)

                // Muestra la categoría directamente en la tarjeta de forma elegante
                Text(text = "Category: ${item.category}", fontSize = 12.sp, color = TextSecondary, modifier = Modifier.padding(top = 2.dp))

                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(28.dp).background(Color(0xFFF1F5F9), CircleShape), contentAlignment = Alignment.Center) {
                            Text(text = if (item.owner.isNotEmpty()) item.owner.take(2).uppercase() else "U", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = item.owner, fontSize = 13.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                    }
                    Surface(color = Color(0xFFE6F4EA), shape = RoundedCornerShape(6.dp)) {
                        Text(text = "${item.pointsValue} pts", color = PestaGreenDark, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                    }
                }
            }
        }
    }
}