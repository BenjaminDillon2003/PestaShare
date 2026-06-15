package com.example.firstprototype.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CardMembership
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.LocalLaundryService
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material.icons.rounded.Moped
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

data class RewardOption(
    val id: Int,
    val title: String,
    val description: String,
    val cost: Int,
    val icon: ImageVector,
    val iconBg: Color
)

@Composable
fun RewardsScreen(
    userPoints: Int,
    onRedeemReward: (Int) -> Unit // Resta los puntos en el NavGraph
) {
    val rewardsList = listOf(
        RewardOption(
            id = 1,
            title = "Mensa Coffee Voucher",
            description = "Get a free premium espresso or cappuccino at the campus Mensa.",
            cost = 30,
            icon = Icons.Rounded.Coffee,
            iconBg = Color(0xFFF59E0B)
        ),
        RewardOption(
            id = 2,
            title = "Priority Laundry Slot",
            description = "Skip the line and book a guaranteed weekend washing machine slot in the block.",
            cost = 50,
            icon = Icons.Rounded.LocalLaundryService,
            iconBg = Color(0xFF3B82F6)
        ),
        RewardOption(
            id = 3,
            title = "15-Min Voi/Dott Ride",
            description = "Free minutes for micro-mobility urban transit around Reutlingen.",
            cost = 70,
            icon = Icons.Rounded.Moped,
            iconBg = PestaGreen
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSurface)
    ) {
        // --- HEADER DE RECOMPENSAS ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(
                            brush = Brush.linearGradient(listOf(PestaBlue, Color(0xFF60A5FA))),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CardMembership,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "Eco Market",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // Wallet de Puntos actual
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MonetizationOn,
                        tint = PestaGreen,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$userPoints pts",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Text(
            text = "Redeem your Eco-Points for campus and housing benefits at the end of the month.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)        )

        // --- LISTA DE RECOMPENSAS ---
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(rewardsList) { reward ->
                val canAfford = userPoints >= reward.cost

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Icono circular de la recompensa
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .background(reward.iconBg.copy(alpha = 0.15f), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = reward.icon, contentDescription = null, tint = reward.iconBg, modifier = Modifier.size(26.dp))
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Contenido de la info
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = reward.title, style = MaterialTheme.typography.titleLarge, fontSize = 18.sp, color = TextPrimary)
                            Text(text = reward.description, style = MaterialTheme.typography.bodyLarge, fontSize = 12.sp, color = TextSecondary, modifier = Modifier.padding(top = 2.dp))

                            Spacer(modifier = Modifier.height(8.dp))

                            // Botón interno de canje
                            Button(
                                onClick = { onRedeemReward(reward.cost) },
                                enabled = canAfford,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PestaBlue,
                                    disabledContainerColor = Color(0xFFF1F5F9)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.height(36.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                            ) {
                                Text(
                                    text = if (canAfford) "Claim for ${reward.cost} pts" else "Needs ${reward.cost} pts",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (canAfford) Color.White else TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}