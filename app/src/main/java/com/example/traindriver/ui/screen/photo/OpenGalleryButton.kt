package com.example.traindriver.ui.screen.photo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Lens
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.traindriver.R

@Composable
fun CapturePictureButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = { },
) {
    IconButton(
        modifier = modifier,
        onClick = onClick,
        content = {
            Icon(
                imageVector = Icons.Sharp.Lens,
                contentDescription = "Сделать фото",
                tint = Color.White,
                modifier = Modifier
                    .size(70.dp)
                    .padding(1.dp)
                    .border(1.dp, Color.White, CircleShape)
            )
        }
    )
}

@Composable
fun OpenGalleryButton(
    modifier: Modifier = Modifier,
    image: ImageVector,
    onClick: () -> Unit
) {
    Box(modifier = Modifier.background(Color.Transparent)) {
        IconButton(
            modifier = modifier,
            onClick = onClick
        ) {
            Icon(
                modifier = Modifier.fillMaxSize(),
                imageVector = image,
                tint = Color.White,
                contentDescription = "Открыть галлерею"
            )
        }
    }
}

@Composable
fun ReverseCameraButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(modifier = Modifier.background(Color.Transparent)) {
        IconButton(
            modifier = modifier,
            onClick = onClick,
            content = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_flip_camera_android_48px),
                    contentDescription = "Развернуть камеру",
                    tint = Color.White,
                    modifier = Modifier.fillMaxSize()
                )
            }
        )
    }
}


