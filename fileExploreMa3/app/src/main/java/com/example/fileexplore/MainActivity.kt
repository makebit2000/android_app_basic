package com.example.filebrowser

import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { FileBrowserApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileBrowserApp() {
    val context = LocalContext.current
    var currentPath by remember { mutableStateOf(Environment.getExternalStorageDirectory().path) }
    val currentDir = File(currentPath)
    val files = currentDir.listFiles()?.sortedWith(
        compareBy<File> { !it.isDirectory }.thenBy { it.name.lowercase() }
    ) ?: emptyList()
    val BrightRed = Color(0xFFFF4444)

    MaterialTheme(
        colorScheme = lightColorScheme(primary = BrightRed, onPrimary = Color.White)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("文件浏览器") },
                    colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.White)
                )
            },
            content = { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    if (currentDir.parent != null) {
                        Text(
                            text = "⬅ 返回",
                            modifier = Modifier
                                .clickable { currentPath = currentDir.parent!! }
                                .padding(12.dp),
                            fontSize = 18.sp,
                            color = BrightRed
                        )
                    }

                    Text(
                        text = "当前位置: $currentPath",
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
                    )

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(files) { file ->
                            Text(
                                text = if (file.isDirectory) "📁 ${file.name}" else "📄 ${file.name}",
                                fontSize = 16.sp,
                                color = BrightRed,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (file.isDirectory) currentPath = file.path
                                        else Toast.makeText(
                                            context,
                                            "打开文件: ${file.name}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .padding(12.dp)
                            )
                        }
                    }
                }
            }
        )
    }
}
