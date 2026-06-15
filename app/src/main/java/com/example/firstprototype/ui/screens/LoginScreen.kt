package com.example.firstprototype.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firstprototype.ui.theme.*

@Composable
fun LoginScreen(onLoginClick: (String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSurface)
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- LOGO E IDENTIDAD DE MARCA PREMIUM ---
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    brush = Brush.linearGradient(listOf(PestaGreen, Color(0xFF34D399))),
                    shape = RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Eco,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(42.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "PestaShare",
            style = MaterialTheme.typography.displayLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Black,
            fontSize = 36.sp
        )

        Text(
            text = "Active Circular Economy",
            style = MaterialTheme.typography.bodyLarge,
            color = PestaGreenDark,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        Text(
            text = "Connect with fellow residents and share resources sustainably.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            modifier = Modifier.padding(top = 12.dp, bottom = 40.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        // --- FORMULARIO DE ACCESO PREMIUM ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Sign In",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // Campo: Correo Electrónico
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        isError = false
                    },
                    label = { Text("University Email") },
                    placeholder = { Text("username@student.reutlingen-university.de", color = TextMuted) },
                    leadingIcon = {
                        Icon(Icons.Outlined.Email, contentDescription = null, tint = TextSecondary)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    isError = isError,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = BackgroundSurface,
                        unfocusedContainerColor = BackgroundSurface,
                        focusedLabelColor = PestaBlue,
                        unfocusedLabelColor = TextSecondary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo: Contraseña
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        isError = false
                    },
                    label = { Text("Password") },
                    leadingIcon = {
                        Icon(Icons.Outlined.Lock, contentDescription = null, tint = TextSecondary)
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    isError = isError,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = BackgroundSurface,
                        unfocusedContainerColor = BackgroundSurface,
                        focusedLabelColor = PestaBlue,
                        unfocusedLabelColor = TextSecondary
                    )
                )

                if (isError) {
                    Text(
                        text = "Please enter a valid email and password.",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp, start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Botón de Inicio de Sesión Accentunado
                Button(
                    onClick = {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            onLoginClick(email)
                        } else {
                            isError = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PestaBlue)
                ) {
                    Text(
                        text = "Get Started",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}