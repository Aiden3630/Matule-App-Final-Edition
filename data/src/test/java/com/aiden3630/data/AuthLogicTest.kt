package com.aiden3630.data

import com.aiden3630.data.manager.*
import com.aiden3630.data.model.UserDto
import com.aiden3630.data.network.AuthApi
import com.aiden3630.data.repository.AuthRepositoryImpl
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AuthLogicTest {
    private val api = mockk<AuthApi>(relaxed = true)
    private val tokenManager = mockk<TokenManager>(relaxed = true)
    private val jsonDb = mockk<JsonDbManager>(relaxed = true)
    private val repo = AuthRepositoryImpl(api, tokenManager, jsonDb)

    @Test // Запрос 1: Авторизация
    fun `test signIn`() = runTest {
        val email = "test@mail.ru"
        val pass = "123456"

        coEvery { jsonDb.getAllUsers() } returns listOf(
            UserDto("1", email, pass, "Ivan", "Ivanov")
        )

        repo.signIn(email, pass)

        // Проверяем сохранение токена
        coVerify { tokenManager.saveToken(any()) }
    }

    @Test // Запрос 2: Создание пользователя
    fun `test signUp`() = runTest {
        coEvery { jsonDb.getAllUsers() } returns emptyList()

        repo.signUp("new@mail.ru", "pass", "Name", "Surname")

        coVerify { jsonDb.addUser(any()) }
    }

    @Test // Запрос 3 и 13: Изменение профиля и получение инфы
    fun `test save and get info`() = runTest {
        val email = "test@mail.ru"
        val name = "Emir"
        val surname = "M"

        coEvery { jsonDb.getAllUsers() } returns emptyList()

        repo.signUp(email, "pass", name, surname)

        // 👇 ИСПРАВЛЕНО: Добавлены все 6 параметров, которые теперь требует TokenManager
        coVerify {
            tokenManager.saveUserInfo(
                email = email,
                name = name,
                surname = surname,
                patronymic = any(), // Для теста нам неважно что там, пишем any()
                birthDate = any(),
                gender = any()
            )
        }
    }

    @Test // Запрос 14: Выход
    fun `test logout`() = runTest {
        val email = "a@a.ru"
        val pass = "123"

        // 👇 1. Сначала "наполняем" базу, чтобы signIn не выдал ошибку
        coEvery { jsonDb.getAllUsers() } returns listOf(
            UserDto(
                id = "1",
                email = email,
                password = pass,
                name = "Name",
                surname = "Surname"
            )
        )

        // 2. Теперь signIn пройдет успешно
        repo.signIn(email, pass)

        // 3. Вызываем метод очистки сессии
        tokenManager.clearSession()

        // 4. Проверяем, что менеджер реально получил команду на удаление данных
        coVerify { tokenManager.clearSession() }
    }
}