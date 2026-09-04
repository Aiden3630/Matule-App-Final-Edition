package com.aiden3630.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiden3630.data.manager.TokenManager
import com.aiden3630.domain.repository.AuthRepository
import com.aiden3630.presentation.utils.NotificationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager,
    private val notificationService: NotificationService
) : ViewModel() {

    private val _nameState = MutableStateFlow("")
    val nameState = _nameState.asStateFlow()

    private val _surnameState = MutableStateFlow("")
    val surnameState = _surnameState.asStateFlow()

    private val _patronymicState = MutableStateFlow("")
    val patronymicState = _patronymicState.asStateFlow()

    private val _birthDateState = MutableStateFlow("")
    val birthDateState = _birthDateState.asStateFlow()

    private val _genderState = MutableStateFlow("")
    val genderState = _genderState.asStateFlow()

    private val _emailState = MutableStateFlow("")
    val emailState = _emailState.asStateFlow()

    private val _signUpEvent = Channel<AuthEvent>()
    val signUpEvent = _signUpEvent.receiveAsFlow()

    init {
        loadSavedData()
    }

    private fun loadSavedData() {
        viewModelScope.launch {
            _nameState.value = tokenManager.getName().first()
            _surnameState.value = tokenManager.getSurname().first()
            _patronymicState.value = tokenManager.getPatronymic().first()
            _birthDateState.value = tokenManager.getBirthDate().first()
            _genderState.value = tokenManager.getGender().first()
            _emailState.value = tokenManager.getEmail().first()
        }
    }

    fun onNameChange(newValue: String) {
        _nameState.value = newValue
        viewModelScope.launch { tokenManager.saveName(newValue) }
    }

    fun onSurnameChange(newValue: String) {
        _surnameState.value = newValue
        viewModelScope.launch { tokenManager.saveSurname(newValue) }
    }

    fun onPatronymicChange(newValue: String) {
        _patronymicState.value = newValue
        viewModelScope.launch { tokenManager.savePatronymic(newValue) }
    }

    fun onBirthDateChange(newValue: String) {
        _birthDateState.value = newValue
        viewModelScope.launch { tokenManager.saveBirthDate(newValue) }
    }

    fun onGenderChange(newValue: String) {
        _genderState.value = newValue
        viewModelScope.launch { tokenManager.saveGender(newValue) }
    }

    fun onEmailChange(newValue: String) {
        _emailState.value = newValue
        viewModelScope.launch { tokenManager.saveEmail(newValue) }
    }

    fun saveTmpUserInfo(email: String, name: String, surname: String) {
        viewModelScope.launch {
            tokenManager.saveUserInfo(
                email = email,
                name = name,
                surname = surname,
                patronymic = _patronymicState.value,
                birthDate = _birthDateState.value,
                gender = _genderState.value
            )
        }
    }

    fun onSignUpClick(name: String, surname: String, email: String, pass: String) {
        viewModelScope.launch {
            try {
                repository.signUp(email, pass, name, surname)
                tokenManager.saveUserInfo(
                    email = email,
                    name = name,
                    surname = surname,
                    patronymic = _patronymicState.value,
                    birthDate = _birthDateState.value,
                    gender = _genderState.value
                )
                notificationService.showNotification("Регистрация", "Вы успешно зарегистрировались!")
                _signUpEvent.send(AuthEvent.Success)
            } catch (exception: Exception) {
                _signUpEvent.send(AuthEvent.Error(exception.message ?: "Ошибка регистрации"))
            }
        }
    }
}