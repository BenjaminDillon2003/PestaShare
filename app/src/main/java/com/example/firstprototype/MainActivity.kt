package com.example.firstprototype

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.firstprototype.navigation.NavGraph
import com.example.firstprototype.ui.theme.FirstPrototypeTheme

/**
 * Main entry point of the application.
 * This activity sets up the Compose theme and initializes the navigation graph.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display for a modern, immersive UI
        enableEdgeToEdge()
        
        setContent {
            FirstPrototypeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Entry point for the app's navigation and screen orchestration
                    NavGraph()
                }
            }
        }
    }
}