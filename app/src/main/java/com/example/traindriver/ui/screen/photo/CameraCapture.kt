package com.example.traindriver.ui.screen.photo

import android.Manifest
import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.content.Intent
import android.media.AudioManager
import android.media.AudioManager.*
import android.media.MediaPlayer
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import com.example.traindriver.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@ExperimentalPermissionsApi
@ExperimentalCoroutinesApi
@Composable
fun CameraCapture(
    modifier: Modifier = Modifier,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    gallerySelect: MutableState<Boolean>,
    onImageFile: (File) -> Unit = { }
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val audioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager

    val volumeLevel = audioManager.getStreamVolume(STREAM_MUSIC)
    val maxVolumeLevel =
        audioManager.getStreamMaxVolume(STREAM_MUSIC).times(0.7).toInt()

    var shimmerState by remember { mutableStateOf(false) }
    fun cameraShimmerStart() {
        scope.launch {
            shimmerState = true
            delay(100)
            shimmerState = false
        }
    }
    Permission(
        permission = Manifest.permission.CAMERA,
        rationale = "Разрешить приложению использовать камеру?",
        permissionNotAvailableContent = {
            NotAvailableContent(context)
        }
    ) {
        Box(modifier = modifier) {
            val lifecycleOwner = LocalLifecycleOwner.current
            val coroutineScope = rememberCoroutineScope()
            var previewUseCase by remember { mutableStateOf<UseCase>(Preview.Builder().build()) }
            val imageCaptureUseCase by remember {
                mutableStateOf(
                    ImageCapture.Builder().setCaptureMode(CAPTURE_MODE_MAXIMIZE_QUALITY).build()
                )
            }

            Box {
                CameraPreview(modifier = Modifier.fillMaxSize(), onUseCase = {
                    previewUseCase = it
                })

                AnimatedVisibility(
                    exit = fadeOut(animationSpec = tween(durationMillis = 100)),
                    enter = fadeIn(animationSpec = tween(durationMillis = 100)),
                    visible = shimmerState
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(Color.Black)
                        .align(Alignment.TopCenter)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                        .padding(start = 36.dp, end = 36.dp, bottom = 52.dp, top = 26.dp)
                        .align(Alignment.BottomCenter),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OpenGalleryButton(
                        modifier = Modifier.size(50.dp),
                        image = Icons.Default.Image
                    ) {
                        gallerySelect.value = true
                    }

                    CapturePictureButton(
                        modifier = Modifier,
                        onClick = {
                            cameraShimmerStart()
                            audioManager.setStreamVolume(
                                STREAM_MUSIC,
                                maxVolumeLevel,
                                FLAG_REMOVE_SOUND_AND_VIBRATE
                            )
                            val sound: MediaPlayer =
                                MediaPlayer.create(context, R.raw.sound_snapshot)

                            sound.start()

                            coroutineScope.launch {
                                imageCaptureUseCase.takePicture(context.executor).let {
                                    onImageFile(it)
                                    audioManager.setStreamVolume(
                                        STREAM_MUSIC,
                                        volumeLevel,
                                        FLAG_REMOVE_SOUND_AND_VIBRATE
                                    )
                                }
                            }
                        })

                    ReverseCameraButton(
                        modifier = Modifier.size(45.dp)
                    ) {

                    }
                }
            }
            LaunchedEffect(previewUseCase) {
                val cameraProvider = context.getCameraProvider()
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, previewUseCase, imageCaptureUseCase
                    )
                } catch (ex: Exception) {
                    Log.e("CameraCapture", "Failed to bind camera use cases", ex)
                }
            }
        }
    }
}

@Composable
private fun NotAvailableContent(context: Context) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Вы не предоставили разрешение, камера недоступна. " +
                    "Предоставить разрешение можно в настройках"
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            })
        }) {
            Text("Открыть настройки")
        }
    }
}