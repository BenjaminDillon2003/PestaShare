package com.example.firstprototype.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.LocationOn
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
import com.example.firstprototype.ui.theme.*

/**
 * Screen for adding a new item or editing an existing one.
 * Updated to support both "Offers" and "Requests" (Wishlist).
 *
 * @param onBack Callback to return to the previous screen.
 * @param onPostItem Callback when the user submits the item.
 * @param initialItem The item to edit (null for a new item).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(
    onBack: () -> Unit,
    onPostItem: (String, String, String, String, Boolean, Uri?) -> Unit, // name, description, category, location, isRequest, uri
    initialItem: SharedItem? = null
) {
    // State variables for form fields
    var title by remember { mutableStateOf(initialItem?.name ?: "") }
    var description by remember { mutableStateOf(initialItem?.description ?: "") }
    var location by remember { mutableStateOf(initialItem?.location ?: "") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(initialItem?.imageUri) }
    var isRequest by remember { mutableStateOf(initialItem?.isRequest ?: false) }

    // Category drop-down options and state
    val categories = listOf("General", "Electronics", "Kitchen", "Books")
    var categoryExpanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(initialItem?.category ?: categories[0]) }

    // Condition options used for the description placeholder
    val conditionOptions = listOf("New ✨", "In Good Condition 👍", "Used 🤝", "Fair Condition ♻️")
    var selectedCondition by remember { mutableStateOf(conditionOptions[1]) }

    val scrollState = rememberScrollState()

    // Activity launcher for picking an image from the gallery
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSurface)
    ) {
        // --- TOP NAVIGATION ---
        TopAppBar(
            title = { },
            navigationIcon = {
                TextButton(onClick = onBack) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Back", color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundSurface)
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            // Screen Title
            Text(
                text = if (initialItem == null) "Create Post" else "Edit Post",
                style = MaterialTheme.typography.displayLarge,
                color = TextPrimary
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // --- TYPE SELECTOR ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                listOf(false to "Offering", true to "Requesting").forEach { (type, label) ->
                    val selected = isRequest == type
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selected) PestaBlue else Color.Transparent)
                            .clickable { isRequest = type }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (selected) Color.White else TextSecondary,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- PHOTO PICKER ---
            Text(text = "Photo", style = MaterialTheme.typography.titleLarge, color = TextPrimary, modifier = Modifier.padding(bottom = 10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, BorderLight, RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(selectedImageUri),
                        contentDescription = "Selected image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.Image, contentDescription = null, modifier = Modifier.size(36.dp), tint = PestaBlue)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Upload Item Image", fontSize = 14.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- TITLE INPUT ---
            Text(text = "Item Title", style = MaterialTheme.typography.titleLarge, color = TextPrimary, modifier = Modifier.padding(bottom = 10.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("e.g., Desk Lamp, Frying Pan...", color = TextMuted) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- CATEGORY DROPDOWN ---
            Text(text = "Category", style = MaterialTheme.typography.titleLarge, color = TextPrimary, modifier = Modifier.padding(bottom = 10.dp))
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category, color = TextPrimary) },
                            onClick = {
                                selectedCategory = category
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- LOCATION / PICKUP SPOT INPUT ---
            Text(text = "Room / Pickup Location", style = MaterialTheme.typography.titleLarge, color = TextPrimary, modifier = Modifier.padding(bottom = 10.dp))
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                placeholder = { Text("e.g., Room 302, Block B Lobby, Laundry Room", color = TextMuted) },
                leadingIcon = { Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = PestaBlue, modifier = Modifier.size(20.dp)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- DESCRIPTION INPUT ---
            Text(text = "Description", style = MaterialTheme.typography.titleLarge, color = TextPrimary, modifier = Modifier.padding(bottom = 10.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Condition: $selectedCondition\nAdd more details...", color = TextMuted) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                minLines = 3,
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
            )

            Spacer(modifier = Modifier.height(36.dp))

            // --- SUBMIT BUTTON ---
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onPostItem(title, description, selectedCategory, location, isRequest, selectedImageUri)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PestaBlue)
            ) {
                Text(if (initialItem == null) "Publish Post" else "Save Changes", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
