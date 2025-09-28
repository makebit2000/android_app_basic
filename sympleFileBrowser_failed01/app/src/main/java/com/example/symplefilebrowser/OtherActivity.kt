package com.example.symplefilebrowser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.symplefilebrowser.ui.theme.SympleFileBrowserTheme

class OtherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SympleFileBrowserTheme {
                Surface(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    OtherScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherScreen() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Other Activity") })
        },
        content = { padding ->
            Text(
                text = "这是另一个页面",
                modifier = androidx.compose.ui.Modifier.padding(padding)
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun OtherScreenPreview() {
    SympleFileBrowserTheme {
        OtherScreen()
    }
}
