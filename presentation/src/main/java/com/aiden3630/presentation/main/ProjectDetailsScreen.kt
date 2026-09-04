package com.aiden3630.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import coil.compose.AsyncImage
import com.aiden3630.presentation.theme.*
import java.io.File
import com.aiden3630.presentation.R
import com.aiden3630.presentation.utils.ProjectInactivityWorker
import java.util.concurrent.TimeUnit

@Composable
fun ProjectDetailsScreen(
    projectId: String,
    onBackClick: () -> Unit,
    onEditClick: (String) -> Unit, // 👇 Коллбек для перехода на редактирование
    viewModel: ProjectDetailsViewModel = hiltViewModel()
) {
    val projectState by viewModel.project.collectAsState()
    val context = LocalContext.current

    // Загружаем данные проекта при открытии
    LaunchedEffect(projectId) {
        viewModel.loadProject(projectId)
    }
    DisposableEffect(projectId) {
        // 1. Когда экран открылся — ставим задачу в WorkManager
        val workRequest = OneTimeWorkRequestBuilder<ProjectInactivityWorker>()
            .setInitialDelay(3, TimeUnit.MINUTES) // 👈 3 минуты по ТЗ
            .addTag("project_timer")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "project_inactivity_${projectId}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )

        // 2. Когда пользователь уходит с экрана (назад или на редактирование)
        onDispose {
            // Отменяем уведомление, так как неактивность прервана
            WorkManager.getInstance(context).cancelUniqueWork("project_inactivity_${projectId}")
        }
    }

    if (projectState == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MatuleBlue)
        }
    } else {
        val project = projectState!!

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MatuleWhite)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // --- ШАПКА (Назад, Редактировать, Удалить) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_chevron_left),
                        contentDescription = "Назад",
                        tint = MatuleBlack
                    )
                }

                Row {
                    // Кнопка Редактировать -> Переход на CreateProjectScreen с ID
                    IconButton(onClick = { onEditClick(project.id) }) {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_menu_edit),
                            contentDescription = "Редактировать",
                            tint = MatuleBlue
                        )
                    }

                    // Кнопка Удалить
                    IconButton(onClick = {
                        viewModel.deleteProject(project.id) { onBackClick() }
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_delete),
                            contentDescription = "Удалить",
                            tint = MatuleError
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- ИЗОБРАЖЕНИЕ ПРОЕКТА ---
            if (project.imageUri != null) {
                AsyncImage(
                    model = File(project.imageUri),
                    contentDescription = "Фото проекта",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            } else {
                // Заглушка, если фото нет
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(MatuleInputBg, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Нет фото", color = MatuleTextGray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- ИНФОРМАЦИЯ ---
            Text(text = project.name, style = Title1, color = MatuleBlack)

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Категория", style = Caption, color = MatuleTextGray)
            Text(text = project.category, style = BodyText, color = MatuleBlack)

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Тип работы", style = Caption, color = MatuleTextGray)
            Text(text = project.type, style = BodyText, color = MatuleBlack)

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Дата начала", style = Caption, color = MatuleTextGray)
            Text(text = project.dateStart, style = BodyText, color = MatuleBlack)

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}