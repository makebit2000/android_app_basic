package com.example.counterapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CounterApp()
        }
    }
}

@Composable
fun CounterApp() {
    // 状态变量：计数值
    var count by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 显示计数值
        Text(
            text = "计数：$count",
            fontSize = 28.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 加一按钮
        Button(onClick = { count++ }) {
            Text("加一")
        }

        Spacer(modifier = Modifier.height(12.dp))
        // 减一按钮
        Button(onClick = { count-- }) {
            Text("减一")
        }

        Spacer(modifier = Modifier.height(12.dp))
        // 重置按钮
        Button(onClick = { count = 0 }) {
            Text("重置")
        }
    }
}
