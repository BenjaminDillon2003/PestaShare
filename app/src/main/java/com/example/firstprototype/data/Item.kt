package com.example.firstprototype.data

import android.net.Uri
import androidx.compose.runtime.Immutable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Represents the possible states of a shared item in the platform.
 */
enum class ItemStatus { 
    AVAILABLE,          // Ready to be requested
    REQUESTED,          // Someone has expressed interest
    BORROWED,           // Currently in use by someone else
    RECYCLE_SUGGESTED,  // Item has been available for a long time, suggesting recycling
    GIVEN               // Permanently transferred to a new owner
}

/**
 * Core data class representing an item shared within the community.
 */
@IgnoreExtraProperties
data class SharedItem(
    val id: String = "", // Firestore Document ID
    val name: String = "",
    val description: String = "",
    val owner: String = "",
    val ownerEmail: String = "",
    val category: String = "",
    val location: String = "",
    val status: ItemStatus = ItemStatus.AVAILABLE,
    val isRequest: Boolean = false,
    val imageUriString: String? = null,
    val pointsValue: Int = 50,
    val createdAtString: String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
) {
    @get:Exclude
    val imageUri: Uri?
        get() = imageUriString?.let { Uri.parse(it) }

    @get:Exclude
    val createdAt: LocalDate
        get() = try {
            LocalDate.parse(createdAtString, DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (e: Exception) {
            LocalDate.now()
        }
}

/**
 * Represents a user profile in Firestore.
 */
@IgnoreExtraProperties
data class UserProfile(
    val email: String = "",
    val displayName: String = "",
    val points: Int = 100
)

/**
 * Represents an entry in the user's point history log.
 */
@Immutable
@IgnoreExtraProperties
data class HistoryLog(
    val id: String = "",
    val description: String = "",
    val points: String = "",
    val isPositive: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Data model for a chat message preview in the Activity Center.
 */
@Immutable
@IgnoreExtraProperties
data class ChatMessage(
    val id: String = "", 
    val itemName: String = "", 
    val contactName: String = "", 
    val lastMessage: String = "", 
    val time: String = ""
)
