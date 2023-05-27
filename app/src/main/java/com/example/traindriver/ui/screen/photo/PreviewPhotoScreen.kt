package com.example.traindriver.ui.screen.photo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.traindriver.ui.screen.Screen
import com.example.traindriver.ui.screen.adding_screen.adding_notes.AddingNotesViewModel
import com.example.traindriver.ui.screen.adding_screen.adding_notes.NotesEvent
import com.example.traindriver.ui.theme.Typography

@Composable
fun PreviewPhotoScreen(
    addingNotesViewModel: AddingNotesViewModel,
    navController: NavController,
    photo: String
) {
    Scaffold {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = rememberAsyncImagePainter(photo),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .align(Alignment.BottomCenter)
                    .background(MaterialTheme.colorScheme.background),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                TextButton(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    onClick = {
                        navController.apply {
                            popBackStack(Screen.CreatePhoto.route, true)
                            navigate(route = Screen.CreatePhoto.route)
                        }

                    }
                ) {
                    Text(
                        text = "Переснять",
                        fontWeight = FontWeight.Bold,
                        style = Typography.titleMedium
                    )
                }
                TextButton(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    onClick = {
                        addingNotesViewModel.createEventNotes(NotesEvent.AddingPhoto(photo.toUri()))
                        navController.apply {
                            popBackStack(Screen.AddingNotes.route, true)
                            navController.navigate(Screen.AddingNotes.route)
                        }
                    }
                ) {
                    Text(
                        text = "Готово",
                        fontWeight = FontWeight.Bold,
                        style = Typography.titleMedium
                    )
                }
            }
        }
    }
}