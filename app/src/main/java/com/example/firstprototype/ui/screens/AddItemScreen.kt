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
import com.example.firstprototype.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(
    onBack: () -> Unit,
    onPostItem: (String, String, Uri?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val conditionOptions = listOf("New ✨", "In Good Condition 👍", "Used 🤝", "Fair Condition ♻️")
    var expanded by remember { mutableStateOf(false) }
    var selectedCondition by remember { mutableStateOf(conditionOptions[1]) }

    val scrollState = rememberScrollState()

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
        // Top Navbar Minimalista
        TopAppBar(
            title = { },
            navigationIcon = {
                TextButton(onClick = onBack) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
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
            Text(
                text = "Post an Item",
                style = MaterialTheme.typography.displayLarge,
                color = TextPrimary
            )
            Text(
                text = "Share what you no longer use with fellow residents.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 28.dp)
            )

            // FOTO CONTENEDOR PREMIUM
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

            // TITULO
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

            // CONDICIÓN DESPLEGABLE PREMIUM
            Text(text = "Condition", style = MaterialTheme.typography.titleLarge, color = TextPrimary, modifier = Modifier.padding(bottom = 10.dp))
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedCondition,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    conditionOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option, color = TextPrimary) },
                            onClick = {
                                selectedCondition = option
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // DESCRIPCIÓN
            Text(text = "Description", style = MaterialTheme.typography.titleLarge, color = TextPrimary, modifier = Modifier.padding(bottom = 10.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Add helpful details (e.g., collection spot)...", color = TextMuted) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                minLines = 3,
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
            )

            Spacer(modifier = Modifier.height(36.dp))

            // BOTÓN DE ACCIÓN ACCENTUADO
            Button(
                onClick = {
                    if (title.isNotBlank() && description.isNotBlank()) {
                        val finalDescription = "Condition: $selectedCondition\n$description"
                        onPostItem(title, finalDescription, selectedImageUri)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PestaBlue)
            ) {
                Text("Publish Item", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}