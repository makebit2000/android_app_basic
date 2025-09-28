package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.weatherapp.ui.theme.WeatherAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val apiKey = "a0eb2913805dec7095720573ae20ea9d"
        setContent {
            WeatherAppTheme {
                WeatherAppScreen(apiKey = apiKey)
            }
        }
    }
}
