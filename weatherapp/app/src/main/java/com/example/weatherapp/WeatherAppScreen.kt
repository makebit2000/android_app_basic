package com.example.weatherapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

@Composable
fun WeatherAppScreen() {
    var city by remember { mutableStateOf(TextFieldValue("")) }
    var weatherInfo by remember { mutableStateOf("请输入城市查询天气") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFBBDEFB))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "天气查询 App", fontSize = 24.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(20.dp))

        BasicTextField(
            value = city,
            onValueChange = { city = it },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // 模拟查询天气
            weatherInfo = if (city.text.isNotBlank()) {
                "城市: ${city.text}\n温度: 26°C\n晴"
            } else {
                "请输入城市名称"
            }
        }) {
            Text("查询")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = weatherInfo, fontSize = 18.sp, color = Color.Black)

        Spacer(modifier = Modifier.height(20.dp))

        Image(
            painter = rememberAsyncImagePainter("https://openweathermap.org/img/wn/01d@2x.png"),
            contentDescription = "天气图标",
            modifier = Modifier.size(100.dp)
        )
    }
}
