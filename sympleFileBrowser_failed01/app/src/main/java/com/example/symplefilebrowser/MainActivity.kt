package com.example.symplefilebrowser

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.symplefilebrowser.ui.theme.SympleFileBrowserTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SympleFileBrowserTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("文件浏览器") })
        },
        content = { padding ->
            Button(
                onClick = {
                    // 跳转到 OtherActivity
                    // 注意这里的 startActivity 需要 ComponentActivity 的上下文
                    val context = androidx.compose.ui.platform.LocalContext.current
                    context.startActivity(Intent(context, OtherActivity::class.java))
                },
                modifier = androidx.compose.ui.Modifier.padding(padding)
            ) {
                Text("跳转到 OtherActivity")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SympleFileBrowserTheme {
        MainScreen()
    }
}
