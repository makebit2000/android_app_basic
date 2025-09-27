package com.example.simplecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UpgradedCalculatorApp()
        }
    }
}

@Composable
fun UpgradedCalculatorApp() {
    var num1 by remember { mutableStateOf("") }
    var num2 by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("简易计算器", fontSize = 28.sp)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = num1,
            onValueChange = { num1 = it },
            label = { Text("数字1") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = num2,
            onValueChange = { num2 = it },
            label = { Text("数字2") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 第一行按钮：加减
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = {
                val n1 = num1.toDoubleOrNull()
                val n2 = num2.toDoubleOrNull()
                result = if (n1 != null && n2 != null) (n1 + n2).toString() else "输入有误"
                num1 = result // 让下一次运算可用
                num2 = ""
            }) { Text("+") }

            Button(onClick = {
                val n1 = num1.toDoubleOrNull()
                val n2 = num2.toDoubleOrNull()
                result = if (n1 != null && n2 != null) (n1 - n2).toString() else "输入有误"
                num1 = result
                num2 = ""
            }) { Text("-") }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 第二行按钮：乘除
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = {
                val n1 = num1.toDoubleOrNull()
                val n2 = num2.toDoubleOrNull()
                result = if (n1 != null && n2 != null) (n1 * n2).toString() else "输入有误"
                num1 = result
                num2 = ""
            }) { Text("×") }

            Button(onClick = {
                val n1 = num1.toDoubleOrNull()
                val n2 = num2.toDoubleOrNull()
                result = if (n1 != null && n2 != null && n2 != 0.0) (n1 / n2).toString() else "除数不能为0"
                num1 = result
                num2 = ""
            }) { Text("÷") }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 清空按钮
        Button(onClick = {
            num1 = ""
            num2 = ""
            result = ""
        }) {
            Text("清空")
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("结果：$result", fontSize = 24.sp)
    }
}
