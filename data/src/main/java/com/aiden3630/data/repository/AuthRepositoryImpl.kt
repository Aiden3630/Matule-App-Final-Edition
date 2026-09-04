package com.aiden3630.data.repository

import android.util.Log
import com.aiden3630.data.manager.JsonDbManager
import com.aiden3630.data.manager.TokenManager
import com.aiden3630.data.model.UserDto
import com.aiden3630.data.network.AuthApi
import com.aiden3630.domain.model.UserProject
import com.aiden3630.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager,
    private val jsonDbManager: JsonDbManager
) : AuthRepository {

    private val jsonParser = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    override suspend fun signIn(email: String, password: String) {
        // Имитируем задержку сетевого запроса
        delay(1000)

        // Получаем список всех зарегистрированных пользователей из файла
        val allUsers = jsonDbManager.getAllUsers()

        // Пытаемся найти пользователя с совпадающим email и паролем
        val foundUser = allUsers.find { user ->
            user.email.equals(email, ignoreCase = true) && user.password == password
        }

        if (foundUser != null) {
            // Если пользователь найден, создаем фейковый токен на основе его ID
            val generatedToken = "token_${foundUser.id}"

            // Сохраняем токен в зашифрованное хранилище (DataStore)
            tokenManager.saveToken(generatedToken)

            // Сохраняем информацию о пользователе для отображения в профиле
            tokenManager.saveUserInfo(
                email = foundUser.email ?: email,
                name = foundUser.name,
                surname = foundUser.surname,
                patronymic = "", // Добавляем недостающие параметры
                birthDate = "",
                gender = ""
            )

            Log.d("AuthRepository", "Успешный вход пользователя: ${foundUser.email}")
        } else {
            // Если совпадений нет, выбрасываем исключение с текстом ошибки
            Log.e("AuthRepository", "Ошибка входа: пользователь не найден или пароль неверен")
            throw Exception("Неверный адрес почты или пароль")
        }
    }

    override suspend fun signUp(email: String, password: String, name: String, surname: String) {
        // Имитируем задержку сетевого запроса
        delay(1000)

        // Проверяем текущую базу пользователей на наличие дубликатов по email
        val existingUsers = jsonDbManager.getAllUsers()
        val isUserExists = existingUsers.any { it.email.equals(email, ignoreCase = true) }

        if (isUserExists) {
            throw Exception("Пользователь с такой почтой уже зарегистрирован")
        }

        // Создаем новый объект пользователя с уникальным идентификатором UUID
        val newUser = UserDto(
            id = UUID.randomUUID().toString(),
            email = email,
            password = password,
            name = name,
            surname = surname,
            avatar = null,

        )

        // Записываем нового пользователя в физический JSON-файл на устройстве
        jsonDbManager.addUser(newUser)

        // Сразу после регистрации создаем активную сессию (автоматический вход)
        val generatedToken = "token_${newUser.id}"
        tokenManager.saveToken(generatedToken)

        // Сохраняем данные профиля
        tokenManager.saveUserInfo(
            email = email,
            name = name,
            surname = surname,
            patronymic = "",
            birthDate = "",
            gender = ""
        )

        Log.d("AuthRepository", "Новый пользователь успешно зарегистрирован: $email")
    }

    override suspend fun getProjectById(id: String): UserProject? {
        return try {

            val allProjectsJson = tokenManager.getProjects().first()

            val projectsList = jsonParser.decodeFromString<List<UserProject>>(allProjectsJson)

            val foundProject = projectsList.find { project: UserProject ->
                project.id == id
            }

            foundProject
        } catch (e: Exception) {
            Log.e("AuthRepository", "Ошибка при поиске проекта: ${e.message}")
            null
        }
    }
}