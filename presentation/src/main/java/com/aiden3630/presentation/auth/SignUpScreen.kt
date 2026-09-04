package com.aiden3630.presentation.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aiden3630.presentation.components.MatuleButton
import com.aiden3630.presentation.components.MatuleTextField
import com.aiden3630.presentation.theme.*
import com.aiden3630.presentation.R as UiKitR
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiden3630.presentation.components.ErrorBanner
import com.aiden3630.presentation.theme.MatuleBlack
import com.aiden3630.presentation.theme.MatuleTextGray
import com.aiden3630.presentation.theme.MatuleWhite
import com.aiden3630.presentation.components.MatuleDatePicker
import com.aiden3630.presentation.utils.convertMillisToDate

@Composable
fun SignUpScreen(
    onNextClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    // Состояния всех полей
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var patronymic by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var isEmailError by remember { mutableStateOf(false) }
    fun validateEmail(mail: String): Boolean {
        val emailRegex = "^[a-z0-9_]+@[a-z0-9_]+\\.ru$".toRegex()
        return mail.matches(emailRegex)
    }

    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.signUpEvent.collect { event ->
            when (event) {
                is AuthEvent.Success -> {
                    Toast.makeText(context, "Аккаунт создан!", Toast.LENGTH_SHORT).show()
                    onNextClick() // Идем создавать пароль
                }

                is AuthEvent.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MatuleWhite)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 40.dp)
        ) {

            // Заголовок
            Spacer(modifier = Modifier.height(76.dp)) // Отступ сверху по макету
            Text(
                text = "Создание Профиля",
                style = Title1.copy(fontWeight = FontWeight.Bold),
                color = MatuleBlack
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Описание (серый текст)
            Text(
                text = "Без профиля вы не сможете создавать проекты.",
                style = Caption,
                color = MatuleTextGray
            )
            Text(
                text = "В профиле будут храниться результаты проектов и ваши описания.",
                style = Caption,
                color = MatuleTextGray
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Поля ввода

            // Имя
            MatuleTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = "Имя"
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Отчество
            MatuleTextField(
                value = patronymic,
                onValueChange = { patronymic = it },
                placeholder = "Отчество"
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Фамилия
            MatuleTextField(
                value = surname,
                onValueChange = { surname = it },
                placeholder = "Фамилия"
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Дата рождения
            MatuleTextField(
                value = birthDate,
                onValueChange = {}, // Не даем писать руками
                placeholder = "Дата рождения",
                readOnly = true, // Клавиатура не нужна
                onClick = { showDatePicker = true }, // Открываем календарь
            )
            Spacer(modifier = Modifier.height(16.dp))

            var isGenderMenuExpanded by remember { mutableStateOf(false) }

            Box(modifier = Modifier.fillMaxWidth()) {
                MatuleTextField(
                    value = gender,
                    onValueChange = {}, // Пусто, так как мы не даем писать руками
                    placeholder = "Пол",
                    readOnly = true, // Клавиатура не откроется
                    onClick = { isGenderMenuExpanded = true }, // При клике открываем меню
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = UiKitR.drawable.ic_chevron_down),
                            contentDescription = null,
                            tint = MatuleTextGray
                        )
                    }
                )

                // Само меню
                DropdownMenu(
                    expanded = isGenderMenuExpanded,
                    onDismissRequest = { isGenderMenuExpanded = false },
                    modifier = Modifier
                        .background(MatuleWhite)
                        .fillMaxWidth(0.9f)
                ) {
                    DropdownMenuItem(
                        text = { Text("Мужской", style = BodyText) },
                        onClick = {
                            gender = "Мужской"
                            isGenderMenuExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Женский", style = BodyText) },
                        onClick = {
                            gender = "Женский"
                            isGenderMenuExpanded = false
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Почта
            MatuleTextField(
                value = email,
                onValueChange = {
                    email = it
                    isEmailError = false // Сбрасываем ошибку при вводе
                },
                placeholder = "Почта",
                isError = isEmailError, // Красная рамка
                errorMessage = if (isEmailError) "Используйте a-z, 0-9 и домен .ru" else null
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Кнопка Далее
            MatuleButton(
                text = "Далее",
                onClick = {
                    if (validateEmail(email)) {
                        viewModel.saveTmpUserInfo(email, name, surname)
                        onNextClick()
                    } else {
                        isEmailError = true
                    }
                },
                enabled = name.isNotEmpty() && surname.isNotEmpty() && email.isNotEmpty()
            )
            if (showDatePicker) {
                MatuleDatePicker(
                    onDateSelected = { millis ->
                        if (millis != null) {
                            birthDate = convertMillisToDate(millis)
                        }
                    },
                    onDismiss = { showDatePicker = false }
                )
            }
            ErrorBanner(
                message = errorMsg,
                onDismiss = { errorMsg = null }
            )
        }

    }
}
