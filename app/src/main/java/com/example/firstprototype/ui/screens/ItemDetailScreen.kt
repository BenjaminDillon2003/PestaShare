package com.example.firstprototype.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.Info
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    item: SharedItem,
    userPoints: Int,
    onBack: () -> Unit,
    onActionSuccess: (Int, String) -> Unit
) {
    var chatMessage by remember { mutableStateOf("Hi ${item.owner}! I am interested in your ${item.name}. When can I pick it up?") }
    val scrollState = rememberScrollState()

    val isAvailable = item.status == ItemStatus.AVAILABLE || item.status == ItemStatus.RECYCLE_SUGGESTED
    val hasEnoughPoints = userPoints >= item.pointsValue

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSurface)
    ) {
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
                .padding(24.dp)
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            // Tarjeta de información del artículo
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = item.name, style = MaterialTheme.typography.displayLarge, fontSize = 26.sp, color = TextPrimary)
                    Text(text = "Listed by ${item.owner} • ${item.category}", fontSize = 14.sp, color = TextSecondary, modifier = Modifier.padding(top = 4.dp))

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Value: ${item.pointsValue} Eco-Points", fontWeight = FontWeight.Bold, color = PestaGreenDark)
                    Text(text = "Your Current Balance: $userPoints pts", fontSize = 13.sp, color = TextSecondary, modifier = Modifier.padding(top = 2.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Cuadro de mensajes
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

            // --- SECCIÓN DINÁMICA DE ADVERTENCIA DE PUNTOS ---
            if (isAvailable && !hasEnoughPoints) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD)) // Fondo amarillo de advertencia claro
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Rounded.Info,
                            contentDescription = "Warning",
                            tint = Color(0xFF856404),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Insufficient Eco-Points",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF856404),
                                fontSize = 14.sp
                            )
                            Text(
                                text = "You need ${item.pointsValue} pts to request this item, but you only have $userPoints pts. Share an item or recycle to earn more points!",
                                color = Color(0xFF856404),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }

            // --- BOTONES DE ACCIÓN PROTEGIDOS ---
            if (!isAvailable) {
                Button(
                    onClick = {},
                    enabled = false,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(disabledContainerColor = Color(0xFFE2E8F0))
                ) {
                    Text("Item Already Reserved 🔒", color = TextMuted, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            } else if (item.status == ItemStatus.RECYCLE_SUGGESTED) {
                Button(
                    onClick = { onActionSuccess(20, "I am taking the ${item.name} to the recycling hub now!") },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PestaGreen)
                ) {
                    Text("Take to Recycling Hub (+20 pts)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                val canRescue = userPoints >= 5
                Button(
                    onClick = { onActionSuccess(-5, "I decided to rescue the ${item.name} from recycling!") },
                    enabled = canRescue,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        disabledContainerColor = Color(0xFFF1F5F9)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                ) {
                    Text(
                        text = if (canRescue) "Rescue Item for Community (-5 pts)" else "Need 5 pts to Rescue",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (canRescue) PestaBlue else TextMuted
                    )
                }
            } else {
                Button(
                    onClick = { onActionSuccess(-item.pointsValue, chatMessage) },
                    enabled = hasEnoughPoints, // El botón se deshabilita si no hay puntos
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PestaBlue,
                        disabledContainerColor = Color(0xFFE2E8F0)
                    )
                ) {
                    Text(
                        text = if (hasEnoughPoints) "Request Item & Send Message (-${item.pointsValue} pts)" else "Locked",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (hasEnoughPoints) Color.White else TextMuted
                    )
                }
            }
        }
    }
}