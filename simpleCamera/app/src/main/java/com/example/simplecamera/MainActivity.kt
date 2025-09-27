package com.example.simplecamera

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.simplecamera.ui.theme.SimpleCameraTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import androidx.compose.ui.platform.LocalContext


class MainActivity : ComponentActivity() {
    private val cameraPermission = Manifest.permission.CAMERA
    private val requestCode = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 请求相机权限
        if (ContextCompat.checkSelfPermission(this, cameraPermission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(cameraPermission), requestCode)
        }

        setContent {
            SimpleCameraTheme {
                CameraPreviewView()
            }
        }
    }
}

@Composable
fun CameraPreviewView() {
    val context = LocalContext.current
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }

    // 获取 CameraProvider
    LaunchedEffect(Unit) {
        val provider = ProcessCameraProvider.getInstance(context).get()
        cameraProvider = provider
    }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            cameraProvider?.let { provider ->
                val preview = Preview.Builder().build()
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                preview.setSurfaceProvider(previewView.surfaceProvider)
                provider.unbindAll()
                provider.bindToLifecycle(
                    ctx as ComponentActivity,
                    cameraSelector,
                    preview
                )
            }
            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}
