package com.example.firstprototype.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
 * Screen displaying the full details of a specific shared item.
 * Allows users to read the description, see the pickup location, and request the item
 * by sending a message and spending Eco-Points.
 * 
 * @param item The item object to display.
 * @param userPoints The current point balance of the logged-in user.
 * @param onBack Callback to navigate back to the previous screen.
 * @param onActionSuccess Callback triggered when the user requests the item. 
 *                        Provides the points deduction and the custom message.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    item: SharedItem,
    userPoints: Int,
    onBack: () -> Unit,
    onActionSuccess: (Int, String) -> Unit
) {
    // State for the custom message to the owner
    var chatMessage by remember { mutableStateOf("Hi ${item.owner}! I am interested in your ${item.name}. When can I pick it up?") }
    val scrollState = rememberScrollState()

    // Business logic for item availability and user affordability
    val isAvailable = item.status == ItemStatus.AVAILABLE || item.status == ItemStatus.RECYCLE_SUGGESTED
    val hasEnoughPoints = userPoints >= item.pointsValue

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSurface)
    ) {
        // --- TOP NAVIGATION BAR ---
        TopAppBar(
            title = { Text("Item Details", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundSurface)
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            // --- ITEM IMAGE ---
            if (item.imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(item.imageUri),
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(24.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // --- INFORMATION CARD ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = item.name, style = MaterialTheme.typography.displayLarge, fontSize = 26.sp, color = TextPrimary)
                    
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "By ${item.owner} • ${item.category}", fontSize = 14.sp, color = TextSecondary)
                    }

                    // Specific pickup spot within the housing block
                    if (item.location.isNotEmpty()) {
                        Row(
                            modifier = Modifier.padding(top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.LocationOn,
                                contentDescription = "Pickup Location",
                                tint = PestaBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Pickup: ${item.location}",
                                fontSize = 14.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    
                    if (item.description.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = item.description, fontSize = 15.sp, color = TextSecondary, lineHeight = 22.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = BorderLight)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Transaction info
                    Text(text = "Value: ${item.pointsValue} Eco-Points", fontWeight = FontWeight.Bold, color = PestaGreenDark)
                    Text(text = "Your Current Balance: $userPoints pts", fontSize = 13.sp, color = TextSecondary, modifier = Modifier.padding(top = 2.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- COORDINATION MESSAGE SECTION ---
            Text(text = "Send a message to coordinate", style = MaterialTheme.typography.titleLarge, color = TextPrimary, modifier = Modifier.padding(bottom = 8.dp))
            OutlinedTextField(
                value = chatMessage,
                onValueChange = { if (isAvailable && hasEnoughPoints) chatMessage = it },
                enabled = isAvailable && hasEnoughPoints,
                leadingIcon = { Icon(Icons.Rounded.ChatBubbleOutline, contentDescription = null, tint = if (isAvailable && hasEnoughPoints) PestaBlue else TextMuted) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                minLines = 3,
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- ACTION FEEDBACK AND BUTTONS ---
            
            // Warning if the user doesn't have enough points
            if (isAvailable && !hasEnoughPoints) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD))
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Info, contentDescription = null, tint = Color(0xFF856404))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Insufficient Eco-Points. You need ${item.pointsValue} pts.", color = Color(0xFF856404), fontSize = 14.sp)
                    }
                }
            }

            // Main Action Button (Request)
            if (!isAvailable) {
                Button(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(14.dp)) {
                    Text("Item Already Reserved 🔒")
                }
            } else {
                Button(
                    onClick = { onActionSuccess(-item.pointsValue, chatMessage) },
                    enabled = hasEnoughPoints,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PestaBlue)
                ) {
                    Text("Request Item (-${item.pointsValue} pts)", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}