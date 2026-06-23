package com.example.firstprototype.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
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
 * Data model for a reward option available in the Eco Market.
 */
data class RewardOption(
    val id: Int,
    val title: String,
    val description: String,
    val cost: Int,
    val icon: ImageVector,
    val iconBg: Color
)

/**
 * Data model for a leaderboard entry to gamify community participation.
 */
data class LeaderboardEntry(
    val rank: Int,
    val name: String,
    val points: Int,
    val avatarInitial: String,
    val isCurrentUser: Boolean = false
)

/**
 * Enhanced Hub Screen: Points, Impact, Leaderboard, and Market.
 * This screen encourages community engagement through gamification and impact visualization.
 * 
 * @param userPoints The current amount of points the user has.
 * @param onRedeemReward Callback triggered when a user redeems a reward.
 */
@Composable
fun RewardsScreen(
    userPoints: Int,
    onRedeemReward: (Int) -> Unit
) {
    // List of available rewards (Mock data)
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

    // Mock leaderboard data to encourage healthy competition
    val topNeighbors = listOf(
        LeaderboardEntry(1, "Sarah Chen", 850, "S"),
        LeaderboardEntry(2, "Alex B.", 720, "A", isCurrentUser = true),
        LeaderboardEntry(3, "James Wilson", 640, "J"),
        LeaderboardEntry(4, "Elena M.", 510, "E"),
        LeaderboardEntry(5, "Mateo D.", 480, "M")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSurface)
    ) {
        // --- HEADER SECTION ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, top = 20.dp, end = 24.dp, bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Hub Icon
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
                Text(
                    text = "Eco Hub",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Black
                )
            }

            // Current Points Display
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

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // --- IMPACT SUMMARY ---
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 28.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = PestaGreen.copy(alpha = 0.08f))
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Your Community Impact", style = MaterialTheme.typography.titleMedium, color = PestaGreenDark)
                            Text(text = "You've saved approximately 12kg of CO2 this month! 🍃", fontSize = 13.sp, color = TextSecondary)
                        }
                        Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = PestaGreen, modifier = Modifier.size(32.dp))
                    }
                }
            }

            // --- LEADERBOARD SECTION ---
            item {
                Text(
                    text = "Top Neighbors",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 12.dp)
                )
                
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(topNeighbors) { entry ->
                        LeaderboardHubCard(entry)
                    }
                }
                
                Spacer(modifier = Modifier.height(28.dp))
            }

            // --- REWARDS SECTION ---
            item {
                Text(
                    text = "Eco Market",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 4.dp)
                )
                Text(
                    text = "Redeem points for exclusive benefits.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 16.dp)
                )
            }

            items(rewardsList) { reward ->
                val canAfford = userPoints >= reward.cost
                RewardHubItem(reward, canAfford) { onRedeemReward(reward.cost) }
            }
        }
    }
}

@Composable
fun LeaderboardHubCard(entry: LeaderboardEntry) {
    Card(
        modifier = Modifier.width(130.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (entry.isCurrentUser) PestaBlue.copy(alpha = 0.05f) else Color.White
        ),
        border = if (entry.isCurrentUser) androidx.compose.foundation.BorderStroke(1.dp, PestaBlue.copy(alpha = 0.2f)) else null
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        brush = if (entry.rank == 1) Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFFFFA500))) 
                                else Brush.linearGradient(listOf(Color(0xFFF1F5F9), Color(0xFFE2E8F0))),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = entry.avatarInitial,
                    color = if (entry.rank == 1) Color.White else TextSecondary,
                    fontWeight = FontWeight.Black
                )
                if (entry.rank == 1) {
                    Icon(
                        Icons.Rounded.EmojiEvents,
                        contentDescription = null,
                        tint = Color(0xFFFFA500),
                        modifier = Modifier.size(14.dp).align(Alignment.BottomEnd).offset(x = 4.dp, y = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            Text(
                text = entry.name,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 13.sp,
                color = TextPrimary,
                maxLines = 1
            )
            
            Text(
                text = "${entry.points} pts",
                color = PestaGreenDark,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            
            Surface(
                modifier = Modifier.padding(top = 8.dp),
                color = BackgroundSurface,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "#${entry.rank}",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun RewardHubItem(reward: RewardOption, canAfford: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reward Icon
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(reward.iconBg.copy(alpha = 0.15f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = reward.icon, contentDescription = null, tint = reward.iconBg, modifier = Modifier.size(26.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Info Content
            Column(modifier = Modifier.weight(1f)) {
                Text(text = reward.title, style = MaterialTheme.typography.titleLarge, fontSize = 17.sp, color = TextPrimary)
                Text(text = reward.description, fontSize = 12.sp, color = TextSecondary, lineHeight = 16.sp)

                Spacer(modifier = Modifier.height(12.dp))

                // Action Button
                Button(
                    onClick = onClick,
                    enabled = canAfford,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PestaBlue,
                        disabledContainerColor = Color(0xFFF1F5F9)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(34.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = if (canAfford) "Claim for ${reward.cost} pts" else "Needs ${reward.cost} pts",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (canAfford) Color.White else TextMuted
                    )
                }
            }
        }
    }
}
