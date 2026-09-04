package com.aiden3630.data.manager

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Создаем хранилище
private val Context.dataStore by preferencesDataStore("auth_prefs")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        // Авторизация
        private val TOKEN_KEY = stringPreferencesKey("jwt_token")
        private val USER_PIN_KEY = stringPreferencesKey("user_pin_code") // Вернули ПИН
        private val EMAIL_KEY = stringPreferencesKey("user_email")
        private val PASSWORD_KEY = stringPreferencesKey("user_password") // Вернули Пароль

        // Данные профиля
        private val NAME_KEY = stringPreferencesKey("user_name")
        private val SURNAME_KEY = stringPreferencesKey("user_surname")
        private val PATRONYMIC_KEY = stringPreferencesKey("user_patronymic")
        private val BIRTHDATE_KEY = stringPreferencesKey("user_birthdate")
        private val GENDER_KEY = stringPreferencesKey("user_gender")
        private val AVATAR_KEY = stringPreferencesKey("user_avatar")

        // Базы данных (JSON)
        private val ALL_USERS_DB_KEY = stringPreferencesKey("all_users_db_json")
        private val PROJECTS_DB_KEY = stringPreferencesKey("projects_db_json")

        // Настройки
        private val NOTIFICATIONS_KEY = booleanPreferencesKey("notifications_enabled")
        private val LAST_ROUTE_KEY = stringPreferencesKey("last_route")
    }

    // ==========================================
    // 1. GETTERS (Чтение данных)
    // ==========================================

    // Базы
    fun getUsersDb(): Flow<String> = context.dataStore.data.map { it[ALL_USERS_DB_KEY] ?: "[]" }
    fun getProjects(): Flow<String> = context.dataStore.data.map { it[PROJECTS_DB_KEY] ?: "[]" }

    // Auth
    fun getToken(): Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }
    fun getPin(): Flow<String?> = context.dataStore.data.map { it[USER_PIN_KEY] } // Исправляет ошибку в SignInPinScreen

    // Login Data
    fun getEmail(): Flow<String> = context.dataStore.data.map { it[EMAIL_KEY] ?: "" }
    fun getPassword(): Flow<String> = context.dataStore.data.map { it[PASSWORD_KEY] ?: "" } // Исправляет ошибку в SignInViewModel

    // Profile
    fun getName(): Flow<String> = context.dataStore.data.map { it[NAME_KEY] ?: "" }
    fun getSurname(): Flow<String> = context.dataStore.data.map { it[SURNAME_KEY] ?: "" }
    fun getPatronymic(): Flow<String> = context.dataStore.data.map { it[PATRONYMIC_KEY] ?: "" }
    fun getBirthDate(): Flow<String> = context.dataStore.data.map { it[BIRTHDATE_KEY] ?: "" }
    fun getGender(): Flow<String> = context.dataStore.data.map { it[GENDER_KEY] ?: "" }

    // Settings
    fun getNotificationsEnabled(): Flow<Boolean> = context.dataStore.data.map { it[NOTIFICATIONS_KEY] ?: true }
    fun getLastRoute(): Flow<String> = context.dataStore.data.map { it[LAST_ROUTE_KEY] ?: "home_tab" }


    // ==========================================
    // 2. SETTERS (Запись данных)
    // ==========================================

    suspend fun saveToken(token: String) { context.dataStore.edit { it[TOKEN_KEY] = token } }

    // Исправляет ошибку в CreatePinViewModel
    suspend fun savePin(pin: String) { context.dataStore.edit { it[USER_PIN_KEY] = pin } }

    suspend fun saveUsersDb(json: String) { context.dataStore.edit { it[ALL_USERS_DB_KEY] = json } }
    suspend fun saveProjects(json: String) { context.dataStore.edit { it[PROJECTS_DB_KEY] = json } }

    // Исправляет ошибку в SignInViewModel
    suspend fun saveUserData(email: String, pass: String) {
        context.dataStore.edit { prefs ->
            prefs[EMAIL_KEY] = email
            prefs[PASSWORD_KEY] = pass
        }
    }

    // Сохранение отдельных полей (для SignUpViewModel)
    suspend fun saveEmail(value: String) { context.dataStore.edit { it[EMAIL_KEY] = value } }
    suspend fun saveName(value: String) { context.dataStore.edit { it[NAME_KEY] = value } }
    suspend fun saveSurname(value: String) { context.dataStore.edit { it[SURNAME_KEY] = value } }
    suspend fun savePatronymic(value: String) { context.dataStore.edit { it[PATRONYMIC_KEY] = value } }
    suspend fun saveBirthDate(value: String) { context.dataStore.edit { it[BIRTHDATE_KEY] = value } }
    suspend fun saveGender(value: String) { context.dataStore.edit { it[GENDER_KEY] = value } }

    suspend fun saveNotificationsEnabled(enabled: Boolean) { context.dataStore.edit { it[NOTIFICATIONS_KEY] = enabled } }
    suspend fun saveLastRoute(route: String) { context.dataStore.edit { it[LAST_ROUTE_KEY] = route } }

    // Сохранение всего профиля сразу
    suspend fun saveUserInfo(email: String, name: String, surname: String, patronymic: String, birthDate: String, gender: String) {
        context.dataStore.edit { prefs ->
            prefs[EMAIL_KEY] = email
            prefs[NAME_KEY] = name
            prefs[SURNAME_KEY] = surname
            prefs[PATRONYMIC_KEY] = patronymic
            prefs[BIRTHDATE_KEY] = birthDate
            prefs[GENDER_KEY] = gender
        }
    }

    // ==========================================
    // 3. CLEAR (Выход)
    // ==========================================
    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
            prefs.remove(USER_PIN_KEY) // Удаляем ПИН при выходе

            // Профиль удаляем, чтобы не видеть старые данные
            prefs.remove(NAME_KEY)
            prefs.remove(SURNAME_KEY)
            prefs.remove(PATRONYMIC_KEY)
            prefs.remove(BIRTHDATE_KEY)
            prefs.remove(GENDER_KEY)
            prefs.remove(AVATAR_KEY)

            // EMAIL_KEY и PASSWORD_KEY можно оставить для удобства повторного входа,
            // или раскомментировать строки ниже для полной очистки:
            // prefs.remove(EMAIL_KEY)
            // prefs.remove(PASSWORD_KEY)
        }
    }
}