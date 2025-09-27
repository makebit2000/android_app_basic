package com.example.simplecameracompose

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaActionSound
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.example.simplecameracompose.ui.theme.SimpleCameraTheme
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.media.MediaScannerConnection

class MainActivity : ComponentActivity() {

    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()

        setContent {
            SimpleCameraTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CameraScreen()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}

@Composable
fun CameraScreen() {
    val context = LocalContext.current

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            hasPermission = permissions[Manifest.permission.CAMERA] == true &&
                    (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q || permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true)
        }
    )

    LaunchedEffect(Unit) {
        val permissions = mutableListOf(Manifest.permission.CAMERA)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        permissionLauncher.launch(permissions.toTypedArray())
    }

    if (hasPermission) {
        CameraPreview()
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "需要相机权限")
        }
    }
}

@Composable
fun CameraPreview() {
    val context = LocalContext.current
    val previewView = remember { PreviewView(context) }
    var imageCapture: ImageCapture? = remember { null }
    var capturedUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val cameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        imageCapture = ImageCapture.Builder().build()

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                context as ComponentActivity,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView({ previewView }, modifier = Modifier.fillMaxSize())

        FloatingActionButton(
            onClick = {
                takePhoto(context, imageCapture) { uri ->
                    capturedUri = uri
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_camera),
                contentDescription = "拍照"
            )
        }

        // 缩略图
        capturedUri?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = "缩略图",
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                            setDataAndType(uri, "image/*")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(intent)
                    }
            )
        }
    }
}

// 拍照封装函数
private fun takePhoto(context: Context, imageCapture: ImageCapture?, onSaved: (Uri) -> Unit) {
    if (imageCapture == null) return
    val name = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())

    val outputOptions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // Android 10+
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/Camera")
        }
        ImageCapture.OutputFileOptions.Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()
    } else {
        // Android 9
        val photoDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        if (!photoDir.exists()) photoDir.mkdirs()
        val photoFile = File(photoDir, "$name.jpg")
        ImageCapture.OutputFileOptions.Builder(photoFile).build()
    }

    imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
                Toast.makeText(context, "拍照失败: ${exception.message}", Toast.LENGTH_LONG).show()
            }

            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                MediaActionSound().play(MediaActionSound.SHUTTER_CLICK)
                Toast.makeText(context, "照片已保存", Toast.LENGTH_SHORT).show()

                val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    outputFileResults.savedUri!!
                } else {
                    // Android 9 用 MediaScanner 扫描文件
                    val photoFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())}.jpg")
                    MediaScannerConnection.scanFile(context, arrayOf(photoFile.absolutePath), null, null)
                    Uri.fromFile(photoFile)
                }
                onSaved(uri)
            }
        })
}
