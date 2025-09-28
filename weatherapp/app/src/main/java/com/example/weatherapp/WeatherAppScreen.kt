package com.example.weatherapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch

@Composable
fun WeatherAppScreen(apiKey: String) {
    var city by remember { mutableStateOf("") }
    var weatherInfo by remember { mutableStateOf<WeatherResponse?>(null) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        // Material2 输入框
        TextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("请输入城市名") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 查询按钮
        Button(
            onClick = {
                coroutineScope.launch {
                    try {
                        val result = WeatherRepository.api.getWeather(city, apiKey)
                        weatherInfo = result
                        errorMsg = null
                    } catch (e: Exception) {
                        weatherInfo = null
                        errorMsg = "查询失败: ${e.message}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("查询天气")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 显示天气信息
        weatherInfo?.let { info ->
            Text("城市: ${info.name}", fontSize = 20.sp)
            Text("温度: ${info.main.temp} ℃", fontSize = 18.sp)
            Text("描述: ${info.weather.firstOrNull()?.description ?: ""}", fontSize = 16.sp)
            info.weather.firstOrNull()?.icon?.let { icon ->
                Image(
                    painter = rememberAsyncImagePainter(
                        "https://openweathermap.org/img/wn/${icon}@2x.png"
                    ),
                    contentDescription = "天气图标",
                    modifier = Modifier.size(100.dp)
                )
            }
        }

        // 错误信息
        errorMsg?.let {
            Text(it, fontSize = 16.sp, color = MaterialTheme.colors.error)
        }
    }
}
