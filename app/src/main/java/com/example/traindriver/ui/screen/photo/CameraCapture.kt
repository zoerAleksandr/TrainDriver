package com.example.traindriver.ui.screen.photo

import android.Manifest
import android.content.Context.AUDIO_SERVICE
import android.content.Intent
import android.graphics.ImageDecoder
import android.media.AudioManager
import android.media.AudioManager.*
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
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
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import com.example.traindriver.R
import com.example.traindriver.ui.screen.adding_screen.adding_notes.Permission
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
        permissionNotAvailableContent = {
            Column(modifier) {
                Text("O noes! No Camera!")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    })
                }) {
                    Text("Open Settings")
                }
            }
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

            val lastImage = remember { mutableStateOf<Uri?>(null) }
//            val launcher =
//                rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
//                    lastImage.value = it
//                }
//
//            @Composable
//            fun LaunchGallery() {
//                SideEffect {
//                    launcher.launch("image/*")
//                }
//            }

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
                        image = lastImage.value?.let { uri ->
                            if (Build.VERSION.SDK_INT < 28) {
                                MediaStore.Images
                                    .Media.getBitmap(context.contentResolver, uri).asImageBitmap()
                            } else {
                                val source = ImageDecoder
                                    .createSource(context.contentResolver, uri)
                                ImageDecoder.decodeBitmap(source).asImageBitmap()
                            }
                        } ?: ImageBitmap.imageResource(id = R.drawable.belarus)
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
                    // Must unbind the use-cases before rebinding them.
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