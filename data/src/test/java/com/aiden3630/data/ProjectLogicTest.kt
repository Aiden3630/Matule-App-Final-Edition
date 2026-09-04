package com.aiden3630.data

import com.aiden3630.data.manager.TokenManager
import com.aiden3630.data.repository.ProjectRepositoryImpl
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ProjectLogicTest {
    private val tokenManager = mockk<TokenManager>(relaxed = true)
    private val repo = ProjectRepositoryImpl(tokenManager)

    @Test // Запрос 11: Список проектов
    fun `test get all projects`() = runTest {
        repo.getAllProjects()
        verify { tokenManager.getProjects() }
    }

    @Test // Запрос 12: Создание проекта
    fun `test create project`() = runTest {
        coEvery { tokenManager.getProjects() } returns flowOf("[]")

        repo.createProject(
            name = "Мой первый проект",
            type = "Вязание",
            dateStart = "01.02.2026",
            dateEnd = "20.02.2026",
            imageUri = null,         // Картинка в тесте может быть null
            category = "Одежда",      // Категория, которую мы синхронизировали
            toWhom = "Себе",         // Новое поле
            source = "Pinterest"     // Новое поле
        )

        coVerify { tokenManager.saveProjects(any()) }
    }
}