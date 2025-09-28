package com.example.filebrowser

import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FileBrowserApp()
        }
    }
}

@Composable
fun FileBrowserApp() {
    val context = LocalContext.current
    var currentPath by remember {
        mutableStateOf(Environment.getExternalStorageDirectory().path)
    }

    val currentDir = File(currentPath)
    val files = currentDir.listFiles()?.toList()?.sortedWith(
        compareBy<File> { !it.isDirectory }.thenBy { it.name.lowercase() }
    ) ?: emptyList()

    // 自定义亮红色
    val BrightRed = Color(0xFFFF4444)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 返回按钮
        if (currentDir.parent != null) {
            BasicText(
                text = "⬅ 返回",
                modifier = Modifier
                    .clickable { currentPath = currentDir.parent!! }
                    .padding(12.dp),
                style = TextStyle(fontSize = 18.sp, color = Color.Blue)
            )
        }

        // 当前路径
        BasicText(
            text = "当前位置: $currentPath",
            style = TextStyle(fontSize = 14.sp, color = Color.DarkGray),
            modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
        )

        // 文件/文件夹列表
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(files) { file ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (file.isDirectory) {
                                currentPath = file.path
                            } else {
                                Toast.makeText(
                                    context,
                                    "打开文件: ${file.name}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .padding(12.dp)
                ) {
                    BasicText(
                        text = if (file.isDirectory) "📁 ${file.name}" else "📄 ${file.name}",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = BrightRed // 文件和文件夹字体都是亮红色
                        )
                    )
                }
            }
        }
    }
}
