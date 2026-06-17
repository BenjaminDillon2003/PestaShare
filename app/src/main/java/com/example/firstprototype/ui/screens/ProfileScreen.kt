package com.example.firstprototype.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
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
 * Screen displaying user profile information, statistics, and account actions.
 * 
 * @param userEmail The authenticated user's email address.
 * @param userPoints Current point balance.
 * @param sharedItemsCount Number of items the user has listed.
 * @param onManageItems Callback to navigate to the "My Items" management tab.
 * @param onLogout Callback to sign out the user.
 */
@Composable
fun ProfileScreen(
    userEmail: String,
    userPoints: Int,
    sharedItemsCount: Int,
    onManageItems: () -> Unit,
    onLogout: () -> Unit
) {
    // Logic to format a user-friendly display name from the email
    val displayName = if (userEmail.contains("@")) {
        userEmail.substringBefore("@")
            .replace(".", " ")
            .replace("_", " ")
            .split(" ")
            .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
    } else if (userEmail.isNotEmpty()) {
        userEmail
    } else "User"

    // Generate initials for the avatar placeholder
    val initials = displayName.split(" ").map { it.take(1) }.joinToString("").take(2).uppercase()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSurface)
            .padding(24.dp)
    ) {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.displayLarge,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // --- USER IDENTITY CARD ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Gradient Avatar
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(
                            brush = Brush.linearGradient(listOf(PestaGreen, PestaBlue)),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = displayName,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Email Display Chip
                Surface(
                    color = BackgroundSurface,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Email,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = TextSecondary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = userEmail.ifEmpty { "student@reutlingen-university.de" },
                            fontSize = 12.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- STATISTICS ROW ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatBox(icon = Icons.Outlined.MonetizationOn, value = userPoints.toString(), label = "Points", color = PestaGreen)
                    StatBox(icon = Icons.Outlined.Inventory2, value = sharedItemsCount.toString(), label = "Shared", color = PestaBlue)
                    StatBox(icon = Icons.Outlined.Stars, value = "0", label = "Rescued", color = Color(0xFF8B5CF6))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- ACTION BUTTONS ---

        // Button to jump directly to managing user's listed items
        Button(
            onClick = onManageItems,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = PestaBlue),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, PestaBlue.copy(alpha = 0.2f))
        ) {
            Icon(Icons.Outlined.Inventory2, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text("Manage My Items", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Sign Out Button
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2), contentColor = Color(0xFFDC2626)),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Sign Out", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

/**
 * Small component to display a single statistic with an icon and label.
 */
@Composable
fun StatBox(icon: ImageVector, value: String, label: String, color: Color) {
    Surface(
        modifier = Modifier.width(85.dp),
        color = BackgroundSurface,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextPrimary)
            Text(text = label, fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
        }
    }
}
