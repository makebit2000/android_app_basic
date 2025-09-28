package com.example.symplefilebrowser.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun FileBrowserScreen(content: @Composable () -> Unit) {
    MaterialTheme(
        content = content
    )
}
