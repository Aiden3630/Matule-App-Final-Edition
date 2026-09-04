package com.aiden3630.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiden3630.domain.model.UserProject
import com.aiden3630.presentation.components.ProjectCard
import com.aiden3630.presentation.theme.*
import com.aiden3630.presentation.R as UiKitR

@Composable
fun ProjectsScreen(
    onAddProjectClick: () -> Unit = {},
    onProjectClick: (String) -> Unit = {},
    viewModel: ProjectsViewModel = hiltViewModel()
) {
    val projects by viewModel.projects.collectAsState()

    // Основной фон белый
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MatuleWhite)
    ) {
        // --- ХЕДЕР (Фиксированный сверху) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding() // Отступ под статус бар
                .height(48.dp)
                .padding(horizontal = 20.dp) // Отступы по бокам 20px (как в CSS)
        ) {
            // Заголовок по центру
            Text(
                text = "Проекты",
                style = Title2,
                color = MatuleBlack,
                modifier = Modifier.align(Alignment.Center)
            )

            // Иконка "+" справа (left: 329px в CSS)
            Icon(
                painter = painterResource(id = UiKitR.drawable.ic_plus),
                contentDescription = "Add Project",
                tint = MatuleTextGray,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterEnd)
                    .clickable { onAddProjectClick() }
            )

            // DIVIDER из CSS (background: #F4F4F4; height: 1px)
            HorizontalDivider(
                modifier = Modifier.align(Alignment.BottomCenter),
                thickness = 1.dp,
                color = Color(0xFFF4F4F4)
            )
        }

        // СПИСОК ПРОЕКТОВ
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp), // Отступы контента
            contentPadding = PaddingValues(top = 20.dp, bottom = 100.dp)
        ) {
            if (projects.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "У вас пока нет проектов",
                            style = BodyText,
                            color = MatuleTextGray
                        )
                    }
                }
            } else {
                items(projects) { project ->
                    ProjectCard(
                        title = project.name,
                        date = "Прошло 2 дня",
                        imageUri = project.imageUri,
                        showImage = false,
                        onClick = {
                            onProjectClick(project.id)
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}