package com.example.firstprototype.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.google.firebase.auth.FirebaseAuth

/**
 * Enhanced Login Screen with Firebase Authentication.
 * Restricts access to university students only.
 * 
 * @param onLoginSuccess Callback triggered when the user successfully authenticates.
 */
@Composable
fun LoginScreen(onLoginSuccess: (String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isRegisterMode by remember { mutableStateOf(false) }

    val auth = remember { FirebaseAuth.getInstance() }
    val universityDomain = "@student.reutlingen-university.de"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSurface)
            .padding(horizontal = 32.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // --- LOGO AND BRAND IDENTITY ---
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

        Spacer(modifier = Modifier.height(32.dp))

        // --- SIGN IN / SIGN UP FORM ---
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
                    text = if (isRegisterMode) "Create Account" else "Sign In",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        isError = false
                    },
                    label = { Text("University Email") },
                    placeholder = { Text("user$universityDomain", color = TextMuted) },
                    leadingIcon = {
                        Icon(Icons.Outlined.Email, contentDescription = null, tint = TextSecondary)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    isError = isError,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = BackgroundSurface,
                        unfocusedContainerColor = BackgroundSurface
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

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
                        unfocusedContainerColor = BackgroundSurface
                    )
                )

                if (isError) {
                    Text(
                        text = errorMessage.ifEmpty { "Please enter valid credentials." },
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp, start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            // Domain validation check
                            if (!email.trim().lowercase().endsWith(universityDomain)) {
                                isError = true
                                errorMessage = "Please use your $universityDomain email."
                                return@Button
                            }

                            isLoading = true
                            isError = false
                            if (isRegisterMode) {
                                auth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        isLoading = false
                                        if (task.isSuccessful) {
                                            onLoginSuccess(email)
                                        } else {
                                            isError = true
                                            errorMessage = task.exception?.message ?: "Registration failed."
                                        }
                                    }
                            } else {
                                auth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        isLoading = false
                                        if (task.isSuccessful) {
                                            onLoginSuccess(email)
                                        } else {
                                            isError = true
                                            errorMessage = task.exception?.message ?: "Authentication failed."
                                        }
                                    }
                            }
                        } else {
                            isError = true
                            errorMessage = "Email and password cannot be empty."
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PestaBlue),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text(
                            text = if (isRegisterMode) "Register" else "Get Started",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = { isRegisterMode = !isRegisterMode },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = if (isRegisterMode) "Already have an account? Sign In" else "New here? Create an account",
                        color = PestaBlue,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}
