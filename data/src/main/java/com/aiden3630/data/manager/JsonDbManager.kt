package com.aiden3630.data.manager

import android.content.Context
import com.aiden3630.data.model.UserDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JsonDbManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Имя нашего файла
    private val fileName = "matule_users_db.json"

    // JSON конфигурация
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        encodeDefaults = true
    }

    // Мьютекс, чтобы не было ошибок, если два потока пишут одновременно
    private val mutex = Mutex()

    // Получить файл (или создать, если нет)
    private fun getFile(): java.io.File {
        // 👇 ИСПОЛЬЗУЕМ ТУ ЖЕ ПАПКУ getExternalFilesDir(null)
        val externalAppFolder = context.getExternalFilesDir(null)
        val databaseFile = java.io.File(externalAppFolder, fileName)

        if (!databaseFile.exists()) {
            databaseFile.createNewFile()
            databaseFile.writeText("[]") // Инициализация пустым списком
            android.util.Log.d("JsonDbManager", "Файл пользователей создан по адресу: ${databaseFile.absolutePath}")
        }
        return databaseFile
    }

    // Читаем пользователей
    suspend fun getAllUsers(): List<UserDto> = withContext(Dispatchers.IO) {
        mutex.withLock {
            try {
                val file = getFile()
                val content = file.readText()
                if (content.isBlank()) return@withLock emptyList()

                json.decodeFromString<List<UserDto>>(content)
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    // Сохраняем список
    suspend fun saveAllUsers(users: List<UserDto>) = withContext(Dispatchers.IO) {
        mutex.withLock {
            try {
                val file = getFile()
                val jsonString = json.encodeToString(users)
                file.writeText(jsonString)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Добавить одного
    suspend fun addUser(user: UserDto) {
        val currentList = getAllUsers().toMutableList()
        currentList.add(user)
        saveAllUsers(currentList)
    }
}