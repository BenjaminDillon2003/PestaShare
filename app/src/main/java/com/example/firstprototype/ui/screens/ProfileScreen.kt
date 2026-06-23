package com.example.firstprototype.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firstprototype.ui.theme.*

/**
 * Enhanced Profile Screen with Sustainability Gamification.
 */
@Composable
fun ProfileScreen(
    userEmail: String,
    userPoints: Int,
    sharedItemsCount: Int,
    onManageItems: () -> Unit,
    onLogout: () -> Unit
) {
    val displayName = if (userEmail.contains("@")) {
        userEmail.substringBefore("@")
            .replace(".", " ")
            .replace("_", " ")
            .split(" ")
            .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
    } else if (userEmail.isNotEmpty()) {
        userEmail
    } else "User"

    val initials = displayName.split(" ").map { it.take(1) }.joinToString("").take(2).uppercase()

    // Gamification Logic (Mock)
    val userLevel = when {
        sharedItemsCount >= 10 -> "Earth Guardian 🌍"
        sharedItemsCount >= 5 -> "Sustainability Hero 🌟"
        else -> "Eco Novice 🌱"
    }
    val progress = (sharedItemsCount % 5) / 5f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSurface)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Profile",
            style = MaterialTheme.typography.displayLarge,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(24.dp))

        // --- USER IDENTITY & LEVEL CARD ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(
                            brush = Brush.linearGradient(listOf(PestaGreen, PestaBlue)),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = initials, color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Black)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = displayName, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                
                // Level Badge
                Surface(
                    color = PestaBlue.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = PestaBlue, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = userLevel, fontSize = 12.sp, color = PestaBlue, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Progress to next level
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Level Progress", fontSize = 12.sp, color = TextSecondary)
                        Text("${(progress * 100).toInt()}%", fontSize = 12.sp, color = PestaBlue, fontWeight = FontWeight.Bold)
                    }
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .height(8.dp),
                        color = PestaBlue,
                        trackColor = BackgroundSurface,
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    StatBox(icon = Icons.Outlined.MonetizationOn, value = userPoints.toString(), label = "Points", color = PestaGreen)
                    StatBox(icon = Icons.Outlined.Inventory2, value = sharedItemsCount.toString(), label = "Shared", color = PestaBlue)
                    StatBox(icon = Icons.Outlined.Public, value = "${sharedItemsCount * 2}kg", label = "CO2 Saved", color = Color(0xFF8B5CF6))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- ACCOUNT ACTIONS ---
        Text(text = "Manage Account", style = MaterialTheme.typography.titleMedium, color = TextSecondary, modifier = Modifier.padding(bottom = 12.dp))
        
        Button(
            onClick = onManageItems,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = TextPrimary),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
        ) {
            Icon(Icons.Outlined.Inventory2, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text("My Active Postings", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = TextMuted)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEF2F2), contentColor = Color(0xFFDC2626)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Outlined.Logout, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text("Sign Out", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun StatBox(icon: ImageVector, value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextPrimary, modifier = Modifier.padding(top = 4.dp))
        Text(text = label, fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
    }
}
