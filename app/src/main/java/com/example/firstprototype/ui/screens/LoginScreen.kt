package com.example.firstprototype.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firstprototype.R

@Composable
fun LoginScreen(onLoginClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_pesta_share),
            contentDescription = "PestaShare Logo",
            modifier = Modifier.size(150.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row {
            Text(
                text = "PESTA ",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0D1B2A)
            )
            Text(
                text = "SHARE",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF13B18F)
            )
        }
        
        Text(
            text = "Pestalozzistraße Student Circular Economy",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(60.dp))

        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("University Email") },
            placeholder = { Text("user@reutlingen-university.de") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E70F0))
        ) {
            Text("Sign In", fontSize = 18.sp)
        }
    }
}
