package com.example.firstprototype.data

import android.net.Uri
import java.time.LocalDate

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
 * 
 * @property id Unique identifier for the item.
 * @property name Title of the item.
 * @property description Detailed information about the item's condition or usage.
 * @property owner The name or identifier of the user who posted the item.
 * @property category The classification of the item (e.g., Electronics, Kitchen).
 * @property location Specific room or pickup spot within the housing block.
 * @property status Current availability state of the item.
 * @property isRequest Whether the item is a request (wishlist) or an offer.
 * @property imageUri Optional URI for the item's photograph.
 * @property pointsValue Cost in Eco-Points to request or borrow the item.
 * @property createdAt The date when the item was first listed.
 */
data class SharedItem(
    val id: Int,
    val name: String,
    val description: String = "",
    val owner: String,
    val category: String,
    val location: String = "",
    val status: ItemStatus = ItemStatus.AVAILABLE,
    val isRequest: Boolean = false,
    val imageUri: Uri? = null,
    val pointsValue: Int = 50,
    val createdAt: LocalDate = LocalDate.now()
)