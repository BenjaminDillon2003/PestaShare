package com.example.firstprototype.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Signpost
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.outlined.Stars
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

@Composable
fun ProfileScreen(
    userEmail: String,
    userPoints: Int,
    onLogout: () -> Unit
) {
    // Algoritmo refinado: Limpia puntos y guiones bajos para formatear el nombre perfectamente
    val displayName = if (userEmail.contains("@")) {
        userEmail.substringBefore("@")
            .replace(".", " ")
            .replace("_", " ") // Corrección estética del guion bajo
            .split(" ")
            .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
    } else if (userEmail.isNotEmpty()) {
        userEmail
    } else "Mateo Dillon Gangotena"

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

        // Tarjeta de Identidad de Usuario Premium
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
                // Avatar con un degradado de marca sofisticado
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

                // Contenedores Estadísticos Renovados
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatBox(icon = Icons.Outlined.MonetizationOn, value = userPoints.toString(), label = "Points", color = PestaGreen)
                    StatBox(icon = Icons.Outlined.Inventory2, value = "1", label = "Shared", color = PestaBlue)
                    StatBox(icon = Icons.Outlined.Stars, value = "0", label = "Rescued", color = Color(0xFF8B5CF6))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

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